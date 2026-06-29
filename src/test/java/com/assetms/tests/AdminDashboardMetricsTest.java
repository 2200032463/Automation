package com.assetms.tests;

import com.assetms.pages.AdminDashboardPage;
import com.assetms.pages.AssetTrackingPage;
import com.assetms.pages.EmployeeManagementPage;
import com.assetms.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


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

    @Test(priority = 1,groups = {"regression", "admin", "positive"})
    public void validateStatAssetsCount()
    {
        dashboardPage.navigateToDashboard();
        int totalAssets = Integer.parseInt(dashboardPage.getTotalAssets());
        int AssignedAssets = Integer.parseInt(dashboardPage.getAssignedAssets());
        int availableAssets = Integer.parseInt(dashboardPage.getAvailableAssets());
        boolean match = false;
        if(totalAssets == AssignedAssets+availableAssets)
        {
            match=true;
        }
        Assert.assertTrue(match,"Missmatch");
    }

    
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
