package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class LoginPage {

    private final WebDriver driver;

    
    private final By emailField    = By.cssSelector("input[type='email']");
    private final By passwordField = By.cssSelector("input[type='password']");
    private final By loginButton   = By.cssSelector("button[type='submit']");
    private final By messageText   = By.cssSelector("p.info");

    
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    

    public void enterEmail(String email) {
        WaitUtils.type(driver, emailField, email);
    }

    public void enterPassword(String password) {
        WaitUtils.type(driver, passwordField, password);
    }

    public void clickLogin() {
        WaitUtils.click(driver, loginButton);
    }

    
    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }

    
    public String getMessage() {
        return WaitUtils.waitForText(driver, messageText);
    }

    
    public boolean isRedirectedTo(String urlFragment) {
        return WaitUtils.waitForUrlContains(driver, urlFragment);
    }
}