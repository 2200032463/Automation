package com.assetms.tests;

import com.assetms.pages.AdminDashboardPage;
import com.assetms.pages.AssetTrackingPage;
import com.assetms.pages.EmployeeManagementPage;
import com.assetms.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * TS_DSH_001: Admin Dashboard Metrics
 */
public class AdminDashboardMetricsTest extends BaseTest {

    private AdminDashboardPage dashboardPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        dashboardPage = new AdminDashboardPage(driver);
        dashboardPage.navigateToDashboard();
    }

    // ── TC_DSH_001 ──────────────────────────────────────────────────────────────
    @Test(priority = 1,
          groups = {"regression", "admin", "positive"},
          description = "TC_DSH_001: Total Employees count matches DB")
    public void testTotalEmployeesCount() {
        dashboardPage.navigateToDashboard();
        String dashboardCountStr = dashboardPage.getTotalEmployees();
        int dashboardCount = Integer.parseInt(dashboardCountStr.trim());

        EmployeeManagementPage empPage = new EmployeeManagementPage(driver);
        empPage.navigateToEmployeeManagement();
        int actualCount = empPage.getEmployeeTableRowCount();

        Assert.assertEquals(dashboardCount, actualCount, 
                "Dashboard Total Employees count should match Employee Management table row count");
    }

    // ── TC_DSH_002 ──────────────────────────────────────────────────────────────
    @Test(priority = 2,
          groups = {"regression", "admin", "positive"},
          description = "TC_DSH_002: Total Assets count matches DB")
    public void testTotalAssetsCount() {
        dashboardPage.navigateToDashboard();
        String dashboardCountStr = dashboardPage.getTotalAssets();
        int dashboardCount = Integer.parseInt(dashboardCountStr.trim());

        AssetTrackingPage trackingPage = new AssetTrackingPage(driver);
        trackingPage.navigateToAssetTracking();
        trackingPage.filterAll();
        int actualCount = trackingPage.getTableRowCount();

        Assert.assertEquals(dashboardCount, actualCount,
                "Dashboard Total Assets count should match Asset Tracking All Assets count");
    }

    // ── TC_DSH_003 ──────────────────────────────────────────────────────────────
    @Test(priority = 3,
          groups = {"regression", "admin", "positive"},
          description = "TC_DSH_003: Available Assets count matches DB")
    public void testAvailableAssetsCount() {
        dashboardPage.navigateToDashboard();
        String dashboardCountStr = dashboardPage.getAvailableAssets();
        int dashboardCount = Integer.parseInt(dashboardCountStr.trim());

        AssetTrackingPage trackingPage = new AssetTrackingPage(driver);
        trackingPage.navigateToAssetTracking();
        trackingPage.filterAvailable();
        int actualCount = trackingPage.getTableRowCount();

        Assert.assertEquals(dashboardCount, actualCount,
                "Dashboard Available Assets count should match Asset Tracking Available count");
    }

    // ── TC_DSH_004 ──────────────────────────────────────────────────────────────
    @Test(priority = 4,
          groups = {"regression", "admin", "positive"},
          description = "TC_DSH_004: Overdue Assets count matches DB")
    public void testOverdueAssetsCount() {
        dashboardPage.navigateToDashboard();
        String dashboardCountStr = dashboardPage.getOverdueAssets();
        int dashboardCount = Integer.parseInt(dashboardCountStr.trim());

        AssetTrackingPage trackingPage = new AssetTrackingPage(driver);
        trackingPage.navigateToAssetTracking();
        trackingPage.filterOverdue();
        int actualCount = trackingPage.getTableRowCount();

        Assert.assertEquals(dashboardCount, actualCount,
                "Dashboard Overdue Assets count should match Asset Tracking Overdue count");
    }

    // ── TC_DSH_005 ──────────────────────────────────────────────────────────────
    @Test(priority = 5,
          groups = {"regression", "admin", "positive"},
          description = "TC_DSH_005: Lost Assets count matches DB")
    public void testLostAssetsCount() {
        dashboardPage.navigateToDashboard();
        String dashboardCountStr = dashboardPage.getLostAssets();
        int dashboardCount = Integer.parseInt(dashboardCountStr.trim());

        AssetTrackingPage trackingPage = new AssetTrackingPage(driver);
        trackingPage.navigateToAssetTracking();
        trackingPage.filterLost();
        int actualCount = trackingPage.getTableRowCount();

        Assert.assertEquals(dashboardCount, actualCount,
                "Dashboard Lost Assets count should match Asset Tracking Lost count");
    }
}
