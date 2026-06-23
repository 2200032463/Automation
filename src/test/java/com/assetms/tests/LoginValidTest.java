package com.assetms.tests;

import com.assetms.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.annotations.Test;


 //TS_LGN_001: Valid Login & Session

public class LoginValidTest extends BaseTest {

    // ── TC_LGN_001 ──────────────────────────────────────────────────────────────
    @Test(priority = 1,
          groups = {"sanity", "regression", "admin", "positive"},
          description = "TC_LGN_001: Admin login with valid credentials")
    public void testAdminLoginSuccess() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"),
                "Admin was NOT redirected to /admin-dashboard.");

        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String role = (String) js.executeScript("return window.localStorage.getItem('role');");
            Assert.assertEquals(role, "ADMIN", "role in localStorage should be ADMIN");
        }
    }

    // ── TC_LGN_002 ──────────────────────────────────────────────────────────────
    @Test(priority = 2,
          groups = {"regression", "employee", "positive"},
          description = "TC_LGN_002: Employee login with valid credentials")
    public void testEmployeeLoginSuccess() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");
        
        Assert.assertTrue(loginPage.isRedirectedTo("/employee-dashboard"),
                "Employee was NOT redirected to /employee-dashboard.");

        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String role = (String) js.executeScript("return window.localStorage.getItem('role');");
            Assert.assertEquals(role, "EMPLOYEE", "role in localStorage should be EMPLOYEE");
        }
    }

    // ── TC_LGN_003 ──────────────────────────────────────────────────────────────
    @Test(priority = 3,
          groups = {"regression", "admin", "positive"},
          description = "TC_LGN_003: Session storage keys validated on login",enabled = false)
    public void testSessionStorageKeys() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"));

        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String token = (String) js.executeScript("return window.localStorage.getItem('token');");
            String role = (String) js.executeScript("return window.localStorage.getItem('role');");
            String userId = (String) js.executeScript("return window.localStorage.getItem('userId');");

            Assert.assertNotNull(token, "token should not be null");
            Assert.assertFalse(token.trim().isEmpty(), "token should not be empty");
            Assert.assertEquals(role, "ADMIN", "role should be ADMIN");
            Assert.assertNotNull(userId, "userId should not be null");
            Assert.assertFalse(userId.trim().isEmpty(), "userId should not be empty");
        }
    }

    // ── TC_LGN_004 ──────────────────────────────────────────────────────────────
    @Test(priority = 4,
          groups = {"regression", "admin", "positive"},
          description = "TC_LGN_004: Logout clears session and blocks re-entry")
    public void testLogoutClearsSession() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"));

        // Perform logout (click logout button in sidebar/header)
        // Locate logout button: By.xpath("//nav//a[contains(text(),'Logout')]") or similar
        // Let's use By.xpath("//aside//nav//a[contains(text(),'Logout')]") or similar.
        // Wait, where is logout? In Sidebar navigation. Let's find it.
        // In LogoutTest.java, the locator was: By.xpath("//nav//a[contains(text(),'Logout')]")
        // Let's click it.
        try {
            driver.findElement(org.openqa.selenium.By.xpath("//a[contains(text(),'Logout')]")).click();
        } catch (Exception e) {
            // fallback: direct click or JS click
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("document.querySelector(\"nav a[href='/login']\")?.click() || document.querySelectorAll(\"a\").forEach(a => { if(a.textContent.includes('Logout')) a.click(); });");
        }

        // Wait for redirect to login
        Assert.assertTrue(loginPage.isRedirectedTo("/login"), "Should redirect to /login after logout");

        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            String token = (String) js.executeScript("return window.localStorage.getItem('token');");
            String role = (String) js.executeScript("return window.localStorage.getItem('role');");
            String userId = (String) js.executeScript("return window.localStorage.getItem('userId');");

            Assert.assertTrue(token == null || token.isEmpty(), "token should be cleared");
            Assert.assertTrue(role == null || role.isEmpty(), "role should be cleared");
            Assert.assertTrue(userId == null || userId.isEmpty(), "userId should be cleared");
        }

        // Try to navigate back
        driver.navigate().back();
        com.assetms.utils.WaitUtils.sleep(1000);
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login") || currentUrl.endsWith("/"), 
                "Should not be able to navigate back to dashboard after logout. URL: " + currentUrl);
    }

    // ── TC_LGN_005 ──────────────────────────────────────────────────────────────
    @Test(priority = 5,
          groups = {"regression", "admin", "negative"},
          description = "TC_LGN_005: Direct URL access blocked when logged out")
    public void testDirectAccessBlocked() {
        // Clear storage
        if (driver instanceof JavascriptExecutor) {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        }
        driver.manage().deleteAllCookies();

        // Direct navigate
        driver.get(BASE_URL + "admin-dashboard");
        com.assetms.utils.WaitUtils.sleep(1000);

        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/login") || currentUrl.endsWith("/"), 
                "Should block direct access to dashboard when logged out. URL: " + currentUrl);
    }
}
