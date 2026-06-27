package com.assetms.tests;

import com.assetms.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TS_LGN_002: Invalid Login Handling
 */
public class LoginInvalidTest extends BaseTest {

    // ── TC_LGN_006 ──────────────────────────────────────────────────────────────
    @Test(priority = 1,
            groups = {"regression", "negative"},
            description = "TC_LGN_006: Login with wrong password")
    public void testWrongPassword() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "wrongpass");

        String msg = loginPage.getMessage();
        Assert.assertTrue(msg.toLowerCase().contains("invalid") || msg.toLowerCase().contains("credentials") || msg.toLowerCase().contains("error"),
                "Expected error message for wrong password, got: " + msg);
        Assert.assertTrue(driver.getCurrentUrl().contains("/login") || driver.getCurrentUrl().endsWith("/"),
                "Should stay on login page after wrong password.");
    }

    // ── TC_LGN_007 ──────────────────────────────────────────────────────────────
    @Test(priority = 2,
            groups = {"regression", "negative"},
            description = "TC_LGN_007: Login with non-existent email")
    public void testNonExistentEmail() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("nonexistent@company.com", "anypass123");

        String msg = loginPage.getMessage();
        Assert.assertTrue(msg.toLowerCase().contains("invalid") || msg.toLowerCase().contains("credentials") || msg.toLowerCase().contains("error") || !msg.isBlank(),
                "Expected error message for non-existent email.");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login") || driver.getCurrentUrl().endsWith("/"),
                "Should stay on login page.");
    }

    // ── TC_LGN_008 ──────────────────────────────────────────────────────────────
    @Test(priority = 3,
            groups = {"regression", "negative"},
            description = "TC_LGN_008: Login with empty email and password")
    public void testEmptyFields() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.clickLogin();

        // Form submission is blocked or staying on login page
        String msg = loginPage.getMessage();
        Assert.assertTrue(driver.getCurrentUrl().contains("/login") || driver.getCurrentUrl().endsWith("/"),
                "Should stay on login page.");
    }

    // ── TC_LGN_009 ──────────────────────────────────────────────────────────────
    @Test(priority = 4,
            groups = {"regression", "negative"},
            description = "TC_LGN_009: Login with invalid email format")
    public void testInvalidEmailFormat() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.company.com", "john123");

        // HTML5 validation or application validation should reject
        Assert.assertTrue(driver.getCurrentUrl().contains("/login") || driver.getCurrentUrl().endsWith("/"),
                "Should stay on login page with invalid email format.");
    }

    // ── TC_LGN_010 ──────────────────────────────────────────────────────────────
    @Test(priority = 5,
            groups = {"regression", "negative"},
            description = "TC_LGN_010: Login with case-sensitive password mismatch")
    public void testCaseSensitivePassword() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "ADMIN123"); // uppercase

        String msg = loginPage.getMessage();
        Assert.assertTrue(msg.toLowerCase().contains("invalid") || msg.toLowerCase().contains("credentials") || msg.toLowerCase().contains("error"),
                "Expected error message for case-sensitive mismatch.");
        Assert.assertTrue(driver.getCurrentUrl().contains("/login") || driver.getCurrentUrl().endsWith("/"),
                "Should stay on login page.");
    }
}
