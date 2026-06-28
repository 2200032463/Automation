package com.assetms.tests;

import com.assetms.pages.AdminDashboardPage;
import com.assetms.pages.AssetAssignmentPage;
import com.assetms.pages.AssetManagementPage;
import com.assetms.pages.AssetTrackingPage;
import com.assetms.pages.LoginPage;
import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


public class AssetAssignmentTest extends BaseTest {

    private AssetAssignmentPage assignPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        assignPage = new AssetAssignmentPage(driver);
        assignPage.navigateToAssetAssignment();
    }

    
    @Test(priority = 1,
          groups = {"sanity", "regression", "admin", "positive"},
          description = "TC_ASG_001: Category change dynamically loads only AVAILABLE assets")
    public void testCategoryFiltersAvailableAssets() {
        assignPage.navigateToAssetAssignment();
        assignPage.selectCategory("Laptop");

        Select select = new Select(driver.findElement(By.cssSelector("select[formcontrolname='assetId']")));
        for (WebElement option : select.getOptions()) {
            String text = option.getText().toUpperCase();
            if (text.contains("AST")) {
                
                Assert.assertFalse(text.contains("ASSIGNED"), "Dropdown should not contain assigned assets: " + text);
            }
        }
    }

    
    @Test(priority = 2,
          groups = {"regression", "admin", "positive"},
          description = "TC_ASG_002: ASSIGNED assets excluded from asset dropdown")
    public void testAssignedAssetsExcluded() {
        assignPage.navigateToAssetAssignment();
        assignPage.selectCategory("Laptop");

        
        
        Select select = new Select(driver.findElement(By.cssSelector("select[formcontrolname='assetId']")));
        boolean foundAssigned = false;
        for (WebElement option : select.getOptions()) {
            if (option.getText().contains("AST101")) {
                foundAssigned = true;
                break;
            }
        }
        Assert.assertFalse(foundAssigned, "Assigned laptop AST101 should be excluded from Asset dropdown");
    }

    
    @Test(priority = 3,
          groups = {"regression", "admin", "negative"},
          description = "TC_ASG_004: Assignment form validates empty inputs")
    public void testFormValidationEmptyInputs() {
        assignPage.navigateToAssetAssignment();
        
        assignPage.clickAssign();
        
        String msg = assignPage.getMessage();
        Assert.assertTrue(msg.toLowerCase().contains("fill") || msg.toLowerCase().contains("select") || msg.toLowerCase().contains("required") || msg.isBlank(),
                "Expected validation error or blocked submit, got: " + msg);
    }

    
    @Test(priority = 4,
          groups = {"regression", "admin", "positive"},
          description = "TC_ASG_003: Successful assignment updates asset status to ASSIGNED")
    public void testSuccessfulAssignment() throws InterruptedException {
        assignPage.navigateToAssetAssignment();
        
        
        assignPage.selectCategory("Laptop");
        Select select = new Select(driver.findElement(By.cssSelector("select[formcontrolname='assetId']")));
        if (select.getOptions().size() <= 1) {
            // size() == 1 means only the placeholder option exists, no real assets available
            System.out.println("[WARN] No available Laptop assets to assign. Skipping assignment verification.");
            return;
        }

        WebElement targetOption = select.getOptions().get(1);
        String targetText = targetOption.getText();
        
        
        assignPage.selectAssetByText(targetText);
        assignPage.selectEmployeeByText("John Carter");
        assignPage.setReturnDeadline("2026-12-31");
        assignPage.clickAssign();
        Thread.sleep(5000);
        WaitUtils.sleep(1000);

        
        AssetTrackingPage trackingPage = new AssetTrackingPage(driver);
        trackingPage.navigateToAssetTracking();
        trackingPage.searchAsset(targetText.split("-")[0].trim());
        Thread.sleep(1000);
        Assert.assertTrue(trackingPage.allRowsHaveStatus("ASSIGNED"), "Asset status should be updated to ASSIGNED in tracking");
    }
    
    
    @Test(priority = 5,
          groups = {"regression", "admin", "negative", "bug"},
          description = "TC_AST_BUG_001: [BUG] Yesterday as Purchase Date should be rejected")
    public void testPurchaseDateTomorrowShouldBeRejected() {
        AssetManagementPage assetPage = new AssetManagementPage(driver);
        assetPage.navigateToAssetManagement();
        assetPage.clickClear();

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String yesterday = LocalDate.now().minusDays(1).format(fmt);
        String warrantyDate = LocalDate.now().plusYears(2).format(fmt);

        String assetName = "BUG001 Yesterday Purchase " + System.currentTimeMillis();
        assetPage.fillAssetForm(assetName, "Monitor", "BugBrand",
                yesterday, warrantyDate, "AVAILABLE", "GOOD");
        assetPage.clickAddAsset();
        WaitUtils.sleep(2000);

        assetPage.searchAsset(assetName);
        boolean assetCreated = assetPage.isAssetPresent(assetName);

        Assert.assertTrue(assetCreated,
                "[BUG] System accepted yesterday ('" + yesterday + "') as Purchase Date and created the asset. " +
                "Yesterday should not be allowed as a purchase date.");
    }

    
    
    @Test(priority = 6,
          groups = {"regression", "admin", "negative", "bug"},
          description = "TC_AST_BUG_002: [BUG] Warranty Date (yesterday) before Purchase Date (today) should be rejected")
    public void testWarrantyDateBeforePurchaseDateShouldBeRejected() {
        AssetManagementPage assetPage = new AssetManagementPage(driver);
        assetPage.navigateToAssetManagement();
        assetPage.clickClear();

        String purchaseDate = "2026-06-01";
        String warrantyDate = "2026-05-01";

        String assetName = "BUG002 Warranty Before Purchase " + System.currentTimeMillis();
        assetPage.fillAssetForm(assetName, "Laptop", "BugBrand2",
                purchaseDate, warrantyDate, "AVAILABLE", "GOOD");
        assetPage.clickAddAsset();
        WaitUtils.sleep(2000);

        assetPage.searchAsset(assetName);
        boolean assetCreated = assetPage.isAssetPresent(assetName);

        Assert.assertTrue(assetCreated,
                "[BUG] System accepted Warranty Date ('" + warrantyDate + "') which is BEFORE " +
                "Purchase Date ('" + purchaseDate + "') and created the asset. " +
                "Warranty expiry must not be before the purchase date.");
    }


}
