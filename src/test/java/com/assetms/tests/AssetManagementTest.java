package com.assetms.tests;

import com.assetms.pages.LoginPage;
import com.assetms.pages.AssetManagementPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AssetManagementTest extends BaseTest {

    @Test(priority = 1, description = "TC-AS01-01: Verify admin can create a new asset with all required fields")
    public void testCreateNewAssetSuccess() {
        // Step 1: Login as Admin first to access the system
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");

        // Step 2: Initialize the Asset Management Page and navigate
        AssetManagementPage assetPage = new AssetManagementPage(driver);
        assetPage.navigateToAssetManagement();

        // Step 3: Trigger the creation form modal/page
        assetPage.clickAddAsset();

        // Step 4: Input asset fields as outlined in the test plan scope
        String uniqueAssetName = "MacBook Pro " + System.currentTimeMillis(); // Adding timestamp to prevent naming collisions
        assetPage.fillAssetDetails(
                uniqueAssetName,
                "Electronics",  // Dropdown option text for category
                "Apple",        // Brand text field
                "Available",    // Dropdown option text for assetStatus
                "New"           // Dropdown option text for assetCondition
        );

        // Step 5: Save the asset
        assetPage.clickSave();

        // Step 6: Verification (TC-AS01-02) - Check if the new asset is rendered at the top of the grid
        String actualCreatedName = assetPage.getNewestAssetName();
        Assert.assertEquals(actualCreatedName, uniqueAssetName, "The asset was not successfully created or does not appear in the grid list!");
    }
}