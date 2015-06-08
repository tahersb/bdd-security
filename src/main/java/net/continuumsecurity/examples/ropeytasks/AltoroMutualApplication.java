package net.continuumsecurity.examples.ropeytasks;

import java.util.Map;

import org.openqa.selenium.By;

import net.continuumsecurity.Config;
import net.continuumsecurity.Credentials;
import net.continuumsecurity.Restricted;
import net.continuumsecurity.UserPassCredentials;
import net.continuumsecurity.behaviour.ILogin;
import net.continuumsecurity.behaviour.ILogout;
import net.continuumsecurity.behaviour.IRecoverPassword;
import net.continuumsecurity.web.WebApplication;

public class AltoroMutualApplication extends WebApplication implements ILogin,
		ILogout, IRecoverPassword {
	
	
	 public AltoroMutualApplication() {
	        super();
	    }
	
	 @Override
	    public void openLoginPage() {
			System.out.println("Inside openLoginPage method");
			try{
				driver.get(Config.getInstance().getBaseUrl() + "bank/login.aspx");
			} catch (Exception e) {
				e.printStackTrace();
			}
	        verifyTextPresent("Online Banking Login");
	    }

	    @Override
	    public void login(Credentials credentials) {
	        UserPassCredentials creds = new UserPassCredentials(credentials);
	        
	        driver.findElement(By.id("uid")).click();
	        driver.findElement(By.id("uid")).clear();
	        driver.findElement(By.id("uid")).sendKeys(creds.getUsername());
	        driver.findElement(By.id("passw")).click();
	        driver.findElement(By.id("passw")).clear();
	        driver.findElement(By.id("passw")).sendKeys(creds.getPassword());
	        driver.findElement(By.name("btnSubmit")).click();
	        
	    }

	    // Convenience method
	    public void login(String username, String password) {
			login(new UserPassCredentials(username, password));
			
	    }

	    @Override
	    public boolean isLoggedIn() {
	        if (driver.getPageSource().contains("MY ACCOUNT")) {
	            return true;
	        } else {
	            return false;
	        }
	    }
	    
	    public void browse()
	    {
	    	driver.findElement(By.id("_ctl0__ctl0_Content_AccountLink")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_LinkHeader2")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_LinkHeader3")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_LinkHeader4")).click();
	        driver.findElement(By.id("_ctl0__ctl0_HyperLink3")).click();
	        driver.findElement(By.id("_ctl0__ctl0_HyperLink4")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink1")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink2")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink3")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink4")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink5")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink6")).click();
	        driver.findElement(By.linkText("Altoro Private Bank")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink7")).click();
	        driver.findElement(By.linkText("High Yield Investments")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink7")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink8")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink9")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink10")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink11")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink12")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink13")).click();
	        driver.findElement(By.cssSelector("div.fl > ul > li > a")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink13")).click();
	        driver.findElement(By.linkText("Community Affairs")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink13")).click();
	        driver.findElement(By.linkText("Analyst Reviews")).click();
	        driver.navigate().back();
	        driver.findElement(By.linkText("Points of Interest")).click();
	        driver.navigate().back();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink14")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink15")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink16")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink17")).click();
	        driver.findElement(By.linkText("Altoro Mutual Declares Quarterly Dividend")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink16")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink17")).click();
	        driver.findElement(By.xpath("//div[@class='fl']/table/tbody/tr[2]/td[2]/a")).click();
	        driver.findElement(By.linkText("More Press Releases")).click();
	        driver.findElement(By.linkText("Altoro Mutual Appoints Jane Smith to Manager of Consumer Banking")).click();
	        driver.findElement(By.linkText("More Press Releases")).click();
	        driver.findElement(By.linkText("Altoro Mutual Announces Prime Rate Increase")).click();
	        driver.findElement(By.linkText("More Press Releases")).click();
	        driver.findElement(By.linkText("Bank Regulators Approve Altoro Mutual's Acquisition Plans")).click();
	        driver.findElement(By.linkText("More Press Releases")).click();
	        driver.findElement(By.xpath("//div[@class='fl']/table/tbody/tr[6]/td[2]/a")).click();
	        driver.findElement(By.linkText("More Press Releases")).click();
	        driver.findElement(By.linkText("John Doe Joins Altoro Mutual Board of Directors")).click();
	        driver.findElement(By.linkText("More Press Releases")).click();
	        driver.findElement(By.xpath("//div[@class='fl']/table/tbody/tr[8]/td[2]/a")).click();
	        driver.findElement(By.linkText("More Press Releases")).click();
	        driver.findElement(By.id("_ctl0__ctl0_Content_MenuHyperLink18")).click();
	        driver.findElement(By.linkText("Current Job Openings")).click();
	        driver.findElement(By.linkText("Executive Assistant")).click();
	        driver.navigate().back();
	        driver.findElement(By.linkText("Teller")).click();
	        driver.navigate().back();
	        driver.findElement(By.linkText("Customer Service Representative")).click();
	        driver.navigate().back();
	        driver.findElement(By.linkText("Loyalty Marketing Program Manager")).click();
	        driver.navigate().back();
	        driver.findElement(By.linkText("Operational Risk Manager")).click();
	        driver.navigate().back();
	        driver.findElement(By.linkText("Mortgage Lending Account Executive")).click();
	        driver.navigate().back();
	        driver.findElement(By.id("_ctl0__ctl0_HyperLink5")).click();
	        driver.findElement(By.id("_ctl0__ctl0_HyperLink6")).click();
	    }

	    /**public void viewProfile() {

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
	    }**/

	    @Override
	    public void logout() {
	    	driver.findElement(By.id("_ctl0__ctl0_LoginLink")).click();
	    }

	    public void search(String query) {
	    	
	    	
	    	driver.findElement(By.id("txtSearch")).click();
	        driver.findElement(By.id("txtSearch")).clear();
	        driver.findElement(By.id("txtSearch")).sendKeys(query);
	        driver.findElement(By.cssSelector("input[type=\"submit\"]")).click();
	     
	    }

	    public void navigate() {
	        
			openLoginPage();
			
	        login(Config.getInstance().getUsers().getDefaultCredentials());
			
	        verifyTextPresent("Welcome to Altoro Mutual Online.");
			
	        browse();
			
	        search("test");
			
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
