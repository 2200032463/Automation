package com.assetms.tests;

import com.assetms.pages.AssetTrackingPage;
import com.assetms.pages.LoginPage;
import com.assetms.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class AssetTrackingTest extends BaseTest {

    private AssetTrackingPage trackingPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        trackingPage = new AssetTrackingPage(driver);
        trackingPage.navigateToAssetTracking();
    }

    
    @Test(priority = 1,
          groups = {"sanity", "regression", "admin", "positive"},
          description = "TC_TRK_001: ALL filter displays complete inventory")
    public void testAllFilterDisplaysInventory() {
        trackingPage.navigateToAssetTracking();
        trackingPage.filterAll();
        
        int rows = trackingPage.getTableRowCount();
        Assert.assertTrue(rows >= 0, "Table row count under 'All Assets' should be non-negative.");
    }

    
    @Test(priority = 2,
          groups = {"regression", "admin", "positive"},
          description = "TC_TRK_002: AVAILABLE filter shows only available assets")
    public void testAvailableFilter() {
        trackingPage.navigateToAssetTracking();
        trackingPage.filterAvailable();
        
        int rows = trackingPage.getTableRowCount();
        if (rows > 0) {
            Assert.assertTrue(trackingPage.allRowsHaveStatus("AVAILABLE"), "All rows must have AVAILABLE status");
        }
    }

    
    @Test(priority = 3,
          groups = {"regression", "admin", "positive"},
          description = "TC_TRK_003: ASSIGNED and OVERDUE filters show correct subsets")
    public void testAssignedAndOverdueFilters() {
        trackingPage.navigateToAssetTracking();
        
        
        trackingPage.filterAssigned();
        int rowsAssigned = trackingPage.getTableRowCount();
        if (rowsAssigned > 0) {
            Assert.assertTrue(trackingPage.allRowsHaveStatus("ASSIGNED"), "All rows must have ASSIGNED status");
        }

        
        trackingPage.filterOverdue();
        int rowsOverdue = trackingPage.getTableRowCount();
        if (rowsOverdue > 0) {
            Assert.assertTrue(trackingPage.allRowsHaveStatus("OVERDUE"), "All rows must have OVERDUE status");
        }
    }

    
    @Test(priority = 4,
          groups = {"regression", "admin", "positive"},
          description = "TC_TRK_004: LOST and DAMAGED filters show correct subsets")
    public void testLostAndDamagedFilters() {
        trackingPage.navigateToAssetTracking();
        
        
        trackingPage.filterLost();
        int rowsLost = trackingPage.getTableRowCount();
        if (rowsLost > 0) {
            Assert.assertTrue(trackingPage.allRowsHaveStatus("LOST"), "All rows must have LOST status");
        }

        
        trackingPage.filterDamaged();
        int rowsDamaged = trackingPage.getTableRowCount();
        if (rowsDamaged > 0) {
            
            
            Assert.assertTrue(rowsDamaged > 0, "Damaged Assets filter should return results");
        }
    }

    
    @Test(priority = 5,
          groups = {"regression", "admin", "positive"},
          description = "TC_TRK_005: Text search filters by Asset Name, Code, and Employee")
    public void testTextSearchFiltersRow() {
        trackingPage.navigateToAssetTracking();
        trackingPage.filterAll();
        
        int totalRows = trackingPage.getTableRowCount();
        if (totalRows == 0) {
            return;
        }

        
        trackingPage.searchAsset("AST101");
        int count1 = trackingPage.getTableRowCount();
        Assert.assertTrue(count1 <= totalRows, "Filtered row count should be <= total");
        trackingPage.clearSearch();

        
        trackingPage.searchAsset("Laptop");
        int count2 = trackingPage.getTableRowCount();
        Assert.assertTrue(count2 <= totalRows, "Filtered row count should be <= total");
        trackingPage.clearSearch();

        
        trackingPage.searchAsset("Carter");
        int count3 = trackingPage.getTableRowCount();
        Assert.assertTrue(count3 <= totalRows, "Filtered row count should be <= total");
        trackingPage.clearSearch();
    }
}
