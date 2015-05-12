package net.continuumsecurity.examples.ropeytasks;

import net.continuumsecurity.Config;
import net.continuumsecurity.Credentials;
import net.continuumsecurity.Restricted;
import net.continuumsecurity.UserPassCredentials;
import net.continuumsecurity.behaviour.ICaptcha;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.IRecoverPassword;
import net.continuumsecurity.web.CaptchaSolver;
import net.continuumsecurity.web.WebApplication;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class RopeyTasksApplication extends WebApplication implements ILogin,
        ILogout, IRecoverPassword {

    public RopeyTasksApplication() {
        super();
    }

    @Override
    public void openLoginPage() {
		System.out.println("Inside openLoginPage method");
		try{
			driver.get(Config.getInstance().getBaseUrl() + "user/login");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		System.out.println("Called selenium driver to get user/login page");
        verifyTextPresent("Login");
		System.out.println("verified text Login present");
    }

    @Override
    public void login(Credentials credentials) {
        UserPassCredentials creds = new UserPassCredentials(credentials);
        driver.findElement(By.id("username")).clear();
        driver.findElement(By.id("username")).sendKeys(creds.getUsername());
        driver.findElement(By.id("password")).clear();
        driver.findElement(By.id("password")).sendKeys(creds.getPassword());
        driver.findElement(By.name("_action_login")).click();
    }

    // Convenience method
    public void login(String username, String password) {
		System.out.println("Inside Login method");
        login(new UserPassCredentials(username, password));
		System.out.println("Exiting login method successfully");
    }

    @Override
    public boolean isLoggedIn() {
        if (driver.getPageSource().contains("Tasks")) {
            return true;
        } else {
            return false;
        }
    }

    public void viewProfile() {

    }

    @Restricted(users = {"bob", "admin"},
            sensitiveData = "Robert")
    public void viewProfileForBob() {
        driver.findElement(By.linkText("Profile")).click();
    }

    @Restricted(users = {"alice", "admin"},
            sensitiveData = "alice@continuumsecurity.net")
    public void viewProfileForAlice() {
        viewProfile();
    }

    @Restricted(users = {"admin"},
            sensitiveData = "User List")
    public void viewUserList() {
        driver.get(Config.getInstance().getBaseUrl() + "admin/list");
    }

    @Override
    public void logout() {
        driver.findElement(By.linkText("Logout")).click();
    }

    public void search(String query) {
        driver.findElement(By.linkText("Tasks")).click();
        driver.findElement(By.id("q")).clear();
        driver.findElement(By.id("q")).sendKeys(query);
        driver.findElement(By.id("search")).click();
    }

    public void navigate() {
        System.out.println("Entered navigate method");
		System.out.println("About to execute openLoginPage method");
		openLoginPage();
		System.out.println("Executed openLoginPage method");
		System.out.println("executing login method with the default credentials in config.xml");
        login(Config.getInstance().getUsers().getDefaultCredentials());
		System.out.println("Executed login method successfully");
		System.out.println("Verify if Welcome string is present");
        verifyTextPresent("Welcome");
		System.out.println("Welcome string is verified");
		System.out.println("Executing viewProfile method");
        viewProfile();
		System.out.println("view profile method executed successfully");
		System.out.println("Executing search method");
        search("test");
		System.out.println("Search method executed successfully");
    }

    /*
     * The details map will be created from the name and value attributes of the
     * <recoverpassword> tags defined for each user in the config.xml file.
     *
     * (non-Javadoc)
     *
     * @see
     * net.continuumsecurity.behaviour.IRecoverPassword#submitRecover(java.util.Map)
     */
    @Override
    public void submitRecover(Map<String, String> details) {
        driver.get(Config.getInstance().getBaseUrl() + "user/recover");
        driver.findElement(By.id("email")).sendKeys(details.get("email"));
        driver.findElement(By.xpath("//input[@value='Recover']")).click();
    }
    
    /*
        To enable CAPTCHA solving, there should be a deathbycaptcha.properties file in the project root with the format:
        type=DeathByCaptcha
        username=deathbycaptcha.com username
        password=my password

    @Override
	public WebElement getCaptchaImage() {
		return driver.findElement(By.id("recaptcha_challenge_image"));		
	}

	@Override
	public WebElement getCaptchaResponseField() {
		return driver.findElement(By.id("recaptcha_response_field"));
	}

	@Override
	public void setDefaultSolver() {
		Properties props = new Properties();
		try {
			props.load(new FileInputStream("deathbycaptcha.properties"));
			setCaptchaSolver(new CaptchaSolver(this, props));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}
    */
}

