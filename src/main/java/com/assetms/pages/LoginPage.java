package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class LoginPage {

    private final WebDriver driver;

    // ── Locators ────────────────────────────────────────────────────────────────
    private final By emailField    = By.cssSelector("input[type='email']");
    private final By passwordField = By.cssSelector("input[type='password']");
    private final By loginButton   = By.cssSelector("button[type='submit']");
    private final By messageText   = By.cssSelector("p.info");

    // ── Constructor ──────────────────────────────────────────────────────────────
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // ── Actions ──────────────────────────────────────────────────────────────────

    public void enterEmail(String email) {
        WaitUtils.type(driver, emailField, email);
    }

    public void enterPassword(String password) {
        WaitUtils.type(driver, passwordField, password);
    }

    public void clickLogin() {
        WaitUtils.click(driver, loginButton);
    }

    /** Combined login helper – enters credentials and clicks Login. */
    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }

    /**
     * Returns the message text shown below the form (success or error).
     * e.g. "Invalid credentials"
     */
    public String getMessage() {
        return WaitUtils.waitForText(driver, messageText);
    }

    /**
     * Waits for the URL to redirect to the expected path after login.
     * @param urlFragment  e.g. "/admin-dashboard"
     */
    public boolean isRedirectedTo(String urlFragment) {
        return WaitUtils.waitForUrlContains(driver, urlFragment);
    }
}