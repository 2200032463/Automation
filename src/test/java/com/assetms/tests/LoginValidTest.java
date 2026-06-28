package com.assetms.tests;

import com.assetms.pages.AdminDashboardPage;
import com.assetms.pages.LoginPage;
import com.assetms.utils.WaitUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;


public class LoginValidTest extends BaseTest {

    
    @Test(priority = 1,
            groups = {"sanity", "regression", "admin", "positive"},
            description = "TC_LGN_001: Admin login with valid credentials redirects to admin dashboard")
    public void testAdminLoginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");

        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"),
                "Admin was NOT redirected to /admin-dashboard.");
    }

    private void prepareLoginPage() {
        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        }
        driver.manage().deleteAllCookies();

        String currentUrl = driver.getCurrentUrl();
        if (currentUrl.contains("/login")) {
            driver.navigate().refresh();
        } else {
            driver.get(BASE_URL);
        }
        WaitUtils.waitForUrlContains(driver, "/login");
        WaitUtils.waitForAngularBootstrapped(driver); 
        WaitUtils.waitForClickable(driver, org.openqa.selenium.By.cssSelector("input[type='email']"));
    }

    
    @Test(priority = 2,
            groups = {"regression", "employee", "positive"},
            description = "TC_LGN_002: Employee login with valid credentials redirects to employee dashboard")
    public void testEmployeeLoginSuccess() {
        prepareLoginPage();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");

        Assert.assertTrue(loginPage.isRedirectedTo("/employee-dashboard"),
                "Employee was NOT redirected to /employee-dashboard.");
    }

    
    @Test(priority = 6,
            groups = {"regression", "admin", "positive"},
            description = "TC_LGN_003: Admin login shows dashboard with all 7 stat cards")
    public void testAdminDashboardStatCardsVisible() {
        prepareLoginPage();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        AdminDashboardPage dashboardPage = new AdminDashboardPage(driver);
        dashboardPage.navigateToDashboard();

        
        int cardCount = dashboardPage.getStatCardCount();
        Assert.assertEquals(cardCount, 7,
                "Expected 7 stat cards on Admin Dashboard, found: " + cardCount);

        
        Assert.assertTrue(dashboardPage.isStatValueNumeric(dashboardPage.getTotalEmployees()),
                "Total Employees should be a numeric value");
        Assert.assertTrue(dashboardPage.isStatValueNumeric(dashboardPage.getTotalAssets()),
                "Total Assets should be a numeric value");
    }

    
    @Test(priority = 4,
            groups = {"regression", "admin", "positive"},
            description = "TC_LGN_004: Logout button redirects user back to login page")
    public void testLogoutRedirectsToLogin() {
        prepareLoginPage();

        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        
        WaitUtils.click(driver, org.openqa.selenium.By.xpath("//button[contains(text(),'Logout')]"));

        
        Assert.assertTrue(loginPage.isRedirectedTo("/login"),
                "Should redirect to /login page after clicking Logout.");
    }

    
    @Test(priority = 5,
            groups = {"regression", "admin", "negative"},
            description = "TC_LGN_005: Direct URL access to admin dashboard when not logged in is blocked")
    public void testDirectAccessBlocked() {
        
        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        }
        driver.manage().deleteAllCookies();

        
        driver.get(BASE_URL + "admin-dashboard");
        WaitUtils.sleep(1500);

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(
                currentUrl.contains("/login") || currentUrl.endsWith("/"),
                "Should block direct access to admin-dashboard when logged out. Current URL: " + currentUrl);
    }
}
