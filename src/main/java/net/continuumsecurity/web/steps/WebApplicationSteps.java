/*******************************************************************************
 *    BDD-Security, application security testing framework
 *
 * Copyright (C) `2012 Stephen de Vries`
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see `<http://www.gnu.org/licenses/>`.
 ******************************************************************************/
package net.continuumsecurity.web.steps;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.continuumsecurity.Config;
import net.continuumsecurity.ConfigurationException;
import net.continuumsecurity.User;
import net.continuumsecurity.UserPassCredentials;
import net.continuumsecurity.Utils;
import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.IRecoverPassword;
import net.continuumsecurity.web.Application;
import net.continuumsecurity.web.FakeCaptchaHelper;
import net.continuumsecurity.web.StepException;
import net.continuumsecurity.web.WebApplication;
import net.continuumsecurity.web.drivers.DriverFactory;
import net.continuumsecurity.web.drivers.ProxyDriver;
import net.lightbody.bmp.core.har.HarCookie;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarRequest;
import net.lightbody.bmp.core.har.HarResponse;

import org.apache.log4j.Logger;
import org.jbehave.core.annotations.Alias;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.BeforeStory;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import com.rits.cloning.Cloner;

public class WebApplicationSteps {
	Logger log = Logger.getLogger(WebApplicationSteps.class);
	public Application app;
	UserPassCredentials credentials;
	HarEntry currentHttp;
	HarEntry savedMessage;
	List<Cookie> sessionIds;
	Map<String, List<HarEntry>> methodProxyMap = new HashMap<String, List<HarEntry>>();
	ProxyDriver driver;

	public WebApplicationSteps() {

	}

	@BeforeStory
	public void setup() {
		createApp();
	}

	/*
	 * This has to be called explicitly when using an examples table in order to
	 * start with a fresh browser instance, because @BeforeScenario is only
	 * called once for the whole scenario, not each example.
	 */
	@Given("a fresh application")
	public void createApp() {
		app = Config.createApp();
		((WebApplication)app).setProxyDriver((ProxyDriver)DriverFactory.getDriver(Config.getDefaultDriver()));
		assert app.getProxyDriver() != null;
		driver = app.getProxyDriver(); //Convenience
		driver.clear();
	}

	@BeforeScenario
	public void createAppAndCredentials() {
		createApp();
		credentials = new UserPassCredentials("", "");
		sessionIds = new ArrayList<Cookie>();
	}

	@Given("the login page")
	@When("the login page is displayed")
	public void openLoginPage() {
		((ILogin) app).openLoginPage();
	}

	@Given("the default username from: $credentialsTable")
	public void defaultUsername(ExamplesTable credentialsTable) {
		credentials.setUsername(tableToDefaultCredentials(credentialsTable)
				.getUsername());
		log.debug("username=" + credentials.getUsername());
	}

	@Given("the default password from: $credentialsTable")
	@When("the default password is used from: $credentialsTable")
	public void defaultPassword(ExamplesTable credentialsTable) {
		credentials.setPassword(tableToDefaultCredentials(credentialsTable)
				.getPassword());
		log.debug("password=" + credentials.getPassword());
	}

	@When("the user logs in")
	@Given("the user logs in")
	public void loginWithSetCredentials() {
		assert credentials != null;
		((ILogin) app).login(credentials);
	}

	@Given("the default user logs in with credentials from: $credentialsTable")
	@When("the default user logs in with credentials from: $credentialsTable")
	public void loginFromTable(ExamplesTable credentialsTable) {
		assert credentialsTable != null;
		openLoginPage();
		credentials = tableToDefaultCredentials(credentialsTable);
		loginWithSetCredentials();
	}

	@Given("the username <username>")
	public void setUsernameFromExamples(@Named("username") String username) {
		credentials.setUsername(username);
	}

	@Given("an invalid username")
	public void setInvalidUsername() {
		credentials.setUsername(Config.getIncorrectUsername());
	}

	@Given("the password <password>")
	public void setCredentialsFromExamples(@Named("password") String password) {
		credentials.setPassword(password);
	}

	@Given("an incorrect password")
	public void incorrectPassword() {
		credentials.setPassword(Config.getIncorrectPassword());
	}

	@When("the user logs in from a fresh login page")
	public void loginFromFreshPage() {
		createApp();
		openLoginPage();
		loginWithSetCredentials();
	}

	// Returns just the first row in the users' credentials table
	private UserPassCredentials tableToDefaultCredentials(
			ExamplesTable credentialsTable) {
		assert credentialsTable.getRowCount() > 0 : "user table must have at least 1 row";
		Parameters firstRow = credentialsTable.getRowAsParameters(0);
		String username = firstRow.valueAs("username", String.class);
		String password = firstRow.valueAs("password", String.class);

		return new UserPassCredentials(username, password);
	}

	private String findRoleByUsername(String username) {
		User user = Config.instance().getUsers()
				.findByCredential("username", username);
		if (user != null) {
			return user.getDefaultRole();
		}
		return null;
	}

	@Then("the user with the role <role> should be logged in")
	public void isLoggedIn(@Named("role") String role) {
		log.debug("checking whether logged in");
		assertThat(((ILogin) app).isLoggedIn(role), is(true));
	}

	@Then("the user is logged in")
	public void loginSucceedsVariant2() {
		assertThat(((ILogin) app).isLoggedIn(findRoleByUsername(credentials
				.getUsername())), is(true));
	}

	@Then("login fails")
	@Alias("the user is not logged in")
	public void loginFails() {
		assertThat(((ILogin) app).isLoggedIn(findRoleByUsername(credentials
				.getUsername())), is(false));
	}

	@When("the case of the password is changed")
	public void loginWithWrongCasedPassword() {
		String wrongCasePassword = credentials.getPassword().toUpperCase();

		if (wrongCasePassword.equals(credentials.getPassword())) {
			wrongCasePassword = credentials.getPassword().toLowerCase();
			if (wrongCasePassword.equals(credentials.getPassword())) {
				throw new RuntimeException(
						"Password doesn't have alphabetic characters, can't run this test.");
			} else {
				credentials.setPassword(wrongCasePassword);
			}
		} else {
			credentials.setPassword(wrongCasePassword);
		}
	}

	@Given("the user logs in from a fresh login page $limit times")
	public void whenTheUserLogsInFromAFreshLoginPageXTimes(int limit) {
		for (int i = 0; i < limit; i++) {
			createApp();
			openLoginPage();
			loginWithSetCredentials();
		}
	}

	@When("the password is changed to values from <value>")
	public void changePasswordTo(@Named("value") String value) {
		credentials.setPassword(value);
	}

	@When("the username is changed to values from <value>")
	public void changeUsernameTo(@Named("value") String value) {
		credentials.setUsername(value);
	}

	@When("an SQL injection <value> is appended to the username")
	public void appendValueToUsername(@Named("value") String value) {
		credentials.setUsername(credentials.getUsername() + value);
	}

	@When("the user logs out")
	@Given("the user logs out")
	public void logout() {
		((ILogout) app).logout();
	}

	@Given("clean HTTP logs")
	@When("the HTTP logs are cleared")
	public void resetProxy() {
		app.getProxyDriver().clear();
		//burp = BurpFactory.getBurp();
		//burp.reset();
	}

	@Given("the HTTP request-response containing the default credentials")
	public void findRequestWithPassword() throws UnsupportedEncodingException {
		String passwd = URLEncoder.encode(credentials.getPassword(), "UTF-8");
		String username = URLEncoder.encode(credentials.getUsername(), "UTF-8");
		 
		//HttpMessageList messageList = new HttpMessageList();
		//messageList.setMessages(burp.findInRequestHistory(passwd));
		//List<HttpMessage> requests = messageList.findInMessages(username,
		//		MessageType.REQUEST);
		List<HarEntry> requests = driver.findInRequestHistory(passwd);
		
		
		if (requests == null || requests.size() == 0)
			throw new StepException(
					"Could not find HTTP request with credentials: "
							+ credentials.getUsername() + " "
							+ credentials.getPassword());
		currentHttp = requests.get(0);
	}

	@Then("the protocol should be HTTPS")
	public void protocolHttps() {
		assertThat(currentHttp.getRequest().getUrl().startsWith("https"),is(true));
	}

	@Given("the HTTP request-response containing the login form")
	public void findResponseWithLoginform() throws UnsupportedEncodingException {
		String regex = "(?i)input[\\s\\w=:'\"]*type\\s*=\\s*['\"]password['\"]";
		//HttpMessageList messageList = new HttpMessageList();
		//messageList.setMessages(burp.findInResponseHistory(regex));
		List<HarEntry> responses = driver.findInResponseHistory(regex);
		
		if (responses == null || responses.size() == 0)
			throw new StepException(
					"Could not find HTTP response with password form using regex: "
							+ regex);
		currentHttp = responses.get(0);
	}

	@Given("the request-response is saved")
	public void saveCurrentHttp() {
		savedMessage = new Cloner().deepClone(currentHttp);
	}

	@Then("the protocol of the current URL should be HTTPS")
	public void protocolUrlHttps() {
		log.debug("URL of login page: "
				+ ((WebApplication) app).getProxyDriver().getCurrentUrl());
		assertThat(((WebApplication) app).getProxyDriver().getCurrentUrl()
				.substring(0, 4), equalToIgnoringCase("https"));
	}

	@Then("the response should be the same as the saved response from the invalid username")
	public void compareResponses() {
		assertThat(savedMessage.getResponse().getStatus(),
				equalTo(currentHttp.getResponse().getStatus()));

		String incorrectUsernameResponse = savedMessage.getResponse().getContent().getText().replaceAll(Config.getIncorrectUsername(), "");
		String correctUsernameResponse = currentHttp.getResponse().getContent().getText()
				.replaceAll(
						Config.instance().getUsers().getDefaultCredentials()
								.get("username"), "");
		assertThat(incorrectUsernameResponse, equalTo(correctUsernameResponse));
	}

	@Then("the response status code should start with 3")
	public void statusCode3xx() {
		assertThat(Integer.toString(currentHttp.getResponse().getStatus()).substring(0, 1),
				equalTo("3"));
	}

	@Given("the session cookies")
	public void getSessionIds() {
		Config.instance();
		for (String name : Config.getSessionIDs()) {
			Cookie cookie = app.getCookieByName(name);
			if (cookie != null)
				sessionIds.add(cookie);
		}
	}

	@Then("the session cookies after authentication should be different from those issued before")
	public void compareSessionIds() {
		Config.instance();
		for (String name : Config.getSessionIDs()) {
			Cookie initialSessionCookie = findCookieByName(sessionIds, name);
			if (initialSessionCookie != null) {
				String existingCookieValue = findCookieByName(sessionIds, name)
						.getValue();
				assertThat(app.getCookieByName(name).getValue(),
						not(initialSessionCookie.getValue()));
			} else if (app.getCookieByName(name).getValue() == null) {
				throw new RuntimeException(
						"No session IDs found after login with name: " + name);
			}
		}
	}

	@Then("the session cookies should have the secure flag set")
	public void sessionCookiesSecureFlag() {
		Config.instance();
		for (String name : Config.getSessionIDs()) {
			assertThat(app.getCookieByName(name).isSecure(), equalTo(true));
		}
	}

	@Then("the session cookies should have the httpOnly flag set")
	public void sessionCookiesHttpOnlyFlag() {
		Config.instance();
		int numCookies = Config.getSessionIDs().size();
		int cookieCount = 0;
		for (HarEntry entry : driver.getHistory()) {
			for (String name : Config.getSessionIDs()) {
				for (HarCookie cookie : entry.getResponse().getCookies()) {
					if (name.equalsIgnoreCase(cookie.getName())) {
						if (cookie.getHttpOnly()) cookieCount++;
					}
				}
				
			}
		}
		Assert.assertThat(cookieCount, greaterThanOrEqualTo(numCookies));
	}

	@Then("the password field should have the autocomplete directive set to 'off'")
	public void thenThePasswordFieldShouldHaveTheAutocompleteDirectiveSetTodisabled() {
		WebElement passwd = ((WebApplication) app).getProxyDriver().findElement(
				By.xpath("//input[@type='password']"));
		assertThat(passwd.getAttribute("autocomplete"),
				equalToIgnoringCase("off"));
	}

	@Then("no exceptions are thrown")
	public void doNothing() {

	}

	@Then("the CAPTCHA request should be present")
	public void checkCaptchaRequestPresent() {
		if (!(app instanceof ICaptcha))
			throw new RuntimeException(
					"Application doesn't implement ICaptcha, can't run captcha scenarios");
		ICaptcha captchaApp = (ICaptcha) app;
		try {
			assertThat(captchaApp.getCaptchaImage(), notNullValue());
		} catch (NoSuchElementException nse) {
			fail("Captcha image not found.");
		}
	}

	@Given("a CAPTCHA solver that always fails")
	public void setIncorrectCaptchaHelper() {
		if (!(app instanceof ICaptcha))
			throw new RuntimeException(
					"App does not implement ICaptcha, skipping.");
		((ICaptcha) app).setCaptchaHelper(new FakeCaptchaHelper(app));
	}

	@When("the password recovery feature is requested")
	public void submitPasswordRecovery() {
		((IRecoverPassword) app).submitRecover(Config.instance().getUsers()
				.getAll().get(0).getRecoverPasswordMap());
	}

	@Then("the CAPTCHA should be presented again")
	public void checkCaptchaPresent() {
		try {
			assertThat(((ICaptcha) app).getCaptchaImage(), notNullValue());
		} catch (NoSuchElementException nse) {
			fail("CAPTCHA not found");
		}
	}

	@Then("the resource name <method> and HTTP requests should be recorded and stored")
	public void recordFlowInAccessControlMap(@Named("method") String method) {
		if (methodProxyMap.get(method) != null) {
			log.info("The method: "
					+ method
					+ " has already been added to the map, using the existing HTTP logs");
			return;
		}
		methodProxyMap.put(method, driver.getHistory());
	}

	@Given("the access control map for authorised users has been populated")
	public void checkIfMapPopulated() {
		if (methodProxyMap.size() == 0)
			throw new RuntimeException(
					"Access control map has not been populated.");
	}

	@Then("they should see the word <verifyString> when accessing the restricted resource <method>")
	public void checkAccessToResource(
			@Named("verifyString") String verifyString,
			@Named("method") String method) {
		try {
			app.getClass().getMethod(method, null).invoke(app, null);
			// For web services, calling the method might throw an exception if
			// access is denied.
		} catch (Exception e) {
			fail("User with credentials: " + credentials.getUsername() + " "
					+ credentials.getPassword()
					+ " could not access the method: " + method + "()");
		}
		if (methodProxyMap.get(method) != null) {
			log.info("The method: "
					+ method
					+ " has already been added to the map, using the existing HTTP logs");
			return;
		}
		methodProxyMap.put(method, driver.getHistory());

		Assert.assertThat(driver.findInResponseHistory(verifyString).size(),
				greaterThan(0));
	}

	@Then("they should not see the word <verifyString> when accessing the restricted resource <method>")
	public void checkNoAccessToResource(
			@Named("verifyString") String verifyString,
			@Named("method") String method) throws Exception {
		if (methodProxyMap == null || methodProxyMap.get(method).size() == 0)
			throw new ConfigurationException(
					"No HTTP messages were recorded for the method: " + method);
		Pattern pattern = Pattern.compile(verifyString);
		boolean accessible = false;
		getSessionIds();
		for (HarEntry message : methodProxyMap.get(method)) {
			if (!"".equals(message.getResponse().getContent().getText())) {
				//log.debug("Original request:\n" + message.getRequestAsString());
				//log.debug("Original response:\n"
				//		+ message.getResponseAsString());
				Map<String, String> cookieMap = new HashMap<String, String>();
				for (Cookie cookie : sessionIds) {
					cookieMap.put(cookie.getName(), cookie.getValue());
				}
				
				HarRequest manual = new Cloner().deepClone(message.getRequest());
				Utils.replaceCookies(manual,cookieMap);
				//log.debug("Replaced request: " + manual.getRequestAsString());
				HarResponse response = driver.makeRequest(manual);
				//log.debug("Response: " + manual.getResponseAsString());

				if (pattern.matcher(response.getContent().getText()).find()) {
					log.debug("Found regex: " + verifyString);
					accessible = true;
					break;
				} else {
					log.debug("Did not find regex: " + verifyString);
				}
			}
		}
		Assert.assertThat("Resource: " + method + " can be accessed.",
				accessible, equalTo(false));
	}

	public Application getWebApplication() {
		return app;
	}

	private Cookie findCookieByName(List<Cookie> cookies, String name) {
		if (cookies.size() == 0)
			return null;
		for (Cookie cookie : cookies) {
			if (cookie == null)
				return null;
			if (cookie.getName().equalsIgnoreCase(name))
				return cookie;
		}
		return null;
	}
}
