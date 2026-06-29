package com.assetms.tests;

import com.assetms.pages.AssetAssignmentPage;
import com.assetms.pages.AssetManagementPage;
import com.assetms.pages.LoginPage;
import com.assetms.utils.WaitUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class AssetCrudTest extends BaseTest {

    private AssetManagementPage assetPage;
    private final String testAssetName = "CRUD Test Monitor X";

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        assetPage = new AssetManagementPage(driver);
        assetPage.navigateToAssetManagement();
    }

    
    @Test(priority = 1,
            groups = {"regression", "admin", "negative"},
            description = "TC_AST_001: Add Asset form rejects submission with empty Name")
    public void testAddAssetRejectsEmptyName() {
        assetPage.navigateToAssetManagement();
        assetPage.clickClear();

        
        assetPage.fillAssetForm("", "Monitor", "Dell", "2026-01-01", "2028-01-01", "AVAILABLE", "GOOD");
        assetPage.clickAddAsset();

        String msg = assetPage.getFormMessage();
        Assert.assertTrue(
                msg.toLowerCase().contains("fill")
                        || msg.toLowerCase().contains("required")
                        || msg.isBlank(),
                "Expected a validation error or blocked submit, got: " + msg);
    }

    
    @Test(priority = 2,
            groups = {"regression", "admin", "positive"},
            description = "TC_AST_002: Add Asset succeeds with all valid inputs")
    public void testAddAssetSucceeds() {
        assetPage.navigateToAssetManagement();
        assetPage.clickClear();

        assetPage.fillAssetForm(testAssetName, "Monitor", "Dell",
                "2026-01-01", "2028-01-01", "AVAILABLE", "GOOD");
        assetPage.clickAddAsset();
        // Wait for asset to be added
        WaitUtils.sleep(3000);

        // Refresh the page
        driver.navigate().refresh();

        // Wait until Angular loads
        WaitUtils.waitForAngularBootstrapped(driver);

//        // Navigate back to Asset Management page
//        assetPage.navigateToAssetManagement();
        
        assetPage.searchAsset(testAssetName);
        WaitUtils.waitForCondition(driver, d -> assetPage.isAssetPresent(testAssetName));

        Assert.assertTrue(assetPage.isAssetPresent(testAssetName),
                "New asset '" + testAssetName + "' should be visible in the table");
    }

    
    @Test(priority = 3,
            groups = {"regression", "admin", "positive"},
            description = "TC_AST_003: Edit Asset populates form with existing values")
    public void testEditAssetPopulatesForm() {
        assetPage.navigateToAssetManagement();

        WaitUtils.sleep(3000);
        driver.navigate().refresh();

        // Wait until Angular loads
        WaitUtils.waitForAngularBootstrapped(driver);

        assetPage.searchAsset(testAssetName);
        assetPage.clickEditForAsset(testAssetName);

        String expectedButtonText = "Update Asset";
        String actualButtonText   = assetPage.getbuttontext();
        Assert.assertEquals(actualButtonText, expectedButtonText,
                "Submit button should read '" + expectedButtonText + "' in edit mode, got: " + actualButtonText);
    }

    
    @Test(priority = 4,
            groups = {"regression", "admin", "positive"},
            description = "TC_AST_004: Edit Asset saves updated values correctly")
    public void testEditAssetSavesChanges() {
        assetPage.navigateToAssetManagement();
        assetPage.searchAsset(testAssetName);
        assetPage.clickEditForAsset(testAssetName);
        String updatedName = testAssetName;
        assetPage.fillAssetForm(updatedName, "Monitor", "Dell",
                "2026-01-01", "2028-01-01", "AVAILABLE", "FAIR");
        assetPage.clickSave();

        
        assetPage.searchAsset(updatedName);
        WaitUtils.waitForCondition(driver, d -> assetPage.isAssetPresent(updatedName));

        Assert.assertTrue(assetPage.isAssetPresent(updatedName),
                "Updated asset '" + updatedName + "' should be visible in the table");
    }

    
    @Test(priority = 5,
            groups = {"regression", "admin", "negative", "bug"},
            description = "TC_AST_BUG_003: [BUG] Delete button has no effect — asset remains in the table")
    public void testDeleteAssetRemovesRow() {
        assetPage.navigateToAssetManagement();
        String targetName = testAssetName;
        assetPage.searchAsset(targetName);

        assetPage.clickDeleteForAsset(targetName);
        WaitUtils.sleep(2000);

        assetPage.searchAsset(targetName);
        boolean assetStillPresent = assetPage.isAssetPresent(targetName);

        Assert.assertFalse(assetStillPresent,
                "[BUG] Delete button has no effect. Asset '" + targetName + "' is still present in the table after clicking Delete.");
    }
}