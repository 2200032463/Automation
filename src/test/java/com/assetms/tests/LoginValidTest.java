package com.assetms.tests;

import com.assetms.pages.AdminDashboardPage;
import com.assetms.pages.LoginPage;
import com.assetms.utils.WaitUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * TS_LGN_001: Valid Login & Session
 */
public class LoginValidTest extends BaseTest {

    // ── TC_LGN_001 ──────────────────────────────────────────────────────────────
    @Test(priority = 1,
            groups = {"sanity", "regression", "admin", "positive"},
            description = "TC_LGN_001: Admin login with valid credentials redirects to admin dashboard")
    public void testAdminLoginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");

        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"),
                "Admin was NOT redirected to /admin-dashboard.");
    }

    // ── TC_LGN_002 ──────────────────────────────────────────────────────────────
    @Test(priority = 2,
            groups = {"regression", "employee", "positive"},
            description = "TC_LGN_002: Employee login with valid credentials redirects to employee dashboard")
    public void testEmployeeLoginSuccess() {
        // Clear localStorage and cookies before switching user
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript("window.localStorage.clear();");
        }
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");

        Assert.assertTrue(loginPage.isRedirectedTo("/employee-dashboard"),
                "Employee was NOT redirected to /employee-dashboard.");
    }

    // ── TC_LGN_003 ──────────────────────────────────────────────────────────────
    @Test(priority = 3,
            groups = {"regression", "admin", "positive"},
            description = "TC_LGN_003: Admin login shows dashboard with all 7 stat cards")
    public void testAdminDashboardStatCardsVisible() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"),
                "Admin was NOT redirected to /admin-dashboard.");
        AdminDashboardPage dashboardPage = new AdminDashboardPage(driver);
        dashboardPage.navigateToDashboard();

        // Verify all 7 stat cards are visible
        int cardCount = dashboardPage.getStatCardCount();
        Assert.assertEquals(cardCount, 7,
                "Expected 7 stat cards on Admin Dashboard, found: " + cardCount);

        // Verify each stat value is a valid non-negative number
        Assert.assertTrue(dashboardPage.isStatValueNumeric(dashboardPage.getTotalEmployees()),
                "Total Employees should be a numeric value");
        Assert.assertTrue(dashboardPage.isStatValueNumeric(dashboardPage.getTotalAssets()),
                "Total Assets should be a numeric value");
    }

    // ── TC_LGN_004 ──────────────────────────────────────────────────────────────
    @Test(priority = 4,
            groups = {"regression", "admin", "positive"},
            description = "TC_LGN_004: Logout button redirects user back to login page")
    public void testLogoutRedirectsToLogin() {
        // Clear session and log in as admin

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"),
                "Admin was NOT redirected to /admin-dashboard before logout test.");

        // Click the Logout button — it is a <button> element, NOT a nav <a> link
        WaitUtils.click(driver, org.openqa.selenium.By.xpath("//button[contains(text(),'Logout')]"));

        // Verify redirect to login page
        Assert.assertTrue(loginPage.isRedirectedTo("/login"),
                "Should redirect to /login page after clicking Logout.");
    }

    // ── TC_LGN_005 ──────────────────────────────────────────────────────────────
    @Test(priority = 5,
            groups = {"regression", "admin", "negative"},
            description = "TC_LGN_005: Direct URL access to admin dashboard when not logged in is blocked")
    public void testDirectAccessBlocked() {
        // Clear all auth state: both localStorage and cookies
        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        }
        driver.manage().deleteAllCookies();

        // Attempt direct navigation to the protected admin dashboard
        driver.get(BASE_URL + "admin-dashboard");
        WaitUtils.sleep(1500);

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
                currentUrl.contains("/login") || currentUrl.endsWith("/"),
                "Should block direct access to admin-dashboard when logged out. Current URL: " + currentUrl);
    }
}
