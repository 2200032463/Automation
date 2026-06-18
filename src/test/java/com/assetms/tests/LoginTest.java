package com.assetms.tests;

import com.assetms.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.time.Duration;

public class LoginTest extends BaseTest {

    @Test(priority = 1, description = "TC-A01-01: Verify admin can log in with valid credentials")
    public void testAdminLoginSuccess() {
        LoginPage loginPage = new LoginPage(driver);

        // From Test Plan: Admin credentials are admin@gmail.com / admin123
        loginPage.login("admin@gmail.com", "admin123");

        // Verification: Check if URL redirects to the admin dashboard
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(180));
        boolean isDashboardUrl = wait.until(ExpectedConditions.urlContains("/admin-dashboard"));

        // Assert that the login redirection worked perfectly
        Assert.assertTrue(isDashboardUrl, "Redirection to admin dashboard failed after login!");
    }

    @Test(priority = 2, description = "TC-A03-01: Verify login with wrong password displays an error message")
    public void testLoginWithWrongPassword() {
        LoginPage loginPage = new LoginPage(driver);

        // Intentionally using an incorrect password
        loginPage.login("john.carter@company.com", "wrongpassword");

        // Locate the error message notification alert element on your Angular app
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        By errorToastLocator = By.xpath("//p[text()='Invalid credentials']");

        String actualErrorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(errorToastLocator)).getText();

        // Assert that the proper error message is rendered on screen
        Assert.assertEquals(actualErrorMessage, "Invalid credentials", "The error message text does not match!");
    }
}
