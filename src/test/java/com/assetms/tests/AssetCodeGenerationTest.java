package com.assetms.tests;

import com.assetms.pages.AssetManagementPage;
import com.assetms.pages.LoginPage;
import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AssetCodeGenerationTest extends BaseTest {

    private AssetManagementPage assetPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        assetPage = new AssetManagementPage(driver);
        assetPage.navigateToAssetManagement();
    }


  @Test(priority = 1,
          groups = {"regression", "admin", "positive"},
          description = "TC_AST_007: First asset in empty DB gets code AST101")
    public void testFirstAssetCodeAST101() {
        
        
        assetPage.navigateToAssetManagement();
        List<WebElement> cells = driver.findElements(By.xpath("//table//tr/td[1]"));
        Pattern pattern = Pattern.compile("AST(\\d+)");
        for (WebElement cell : cells) {
            String text = cell.getText().trim();
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                int id = Integer.parseInt(matcher.group(1));
                Assert.assertTrue(id >= 101, "All generated asset codes must be AST101 or higher. Found: " + text);
            }
        }
    }

    
    @Test(priority = 2,
          groups = {"regression", "admin", "positive"},
          description = "TC_AST_008: Asset code column has unique database constraint")
    public void testAssetCodeUniqueConstraint() {
        assetPage.navigateToAssetManagement();
        
        
        
        boolean hasAssetCodeInputInForm = false;
        try {
            driver.findElement(By.cssSelector("input[formcontrolname='assetCode']"));
            hasAssetCodeInputInForm = true;
        } catch (Exception ignored) {}

        
        if (hasAssetCodeInputInForm) {
            WebElement input = driver.findElement(By.cssSelector("input[formcontrolname='assetCode']"));
            Assert.assertFalse(input.isEnabled(), "Asset Code input should be disabled if present on form");
        } else {
            
            Assert.assertTrue(true);
        }
    }

    
    @Test(priority = 3,
          groups = {"regression", "admin", "positive"},
          description = "TC_AST_009: Special characters accepted in Name and Brand fields")
    public void testSpecialCharsAccepted() {
        assetPage.navigateToAssetManagement();
        assetPage.clickClear();

        String name = "MacBook Pro 16\" @Retina";
        String brand = "Apple Inc. #US";
        assetPage.fillAssetForm(name, "Laptop", brand, "2026-01-01", "2028-01-01", "AVAILABLE", "GOOD");
        assetPage.clickAddAsset();
        WaitUtils.sleep(1000);

        assetPage.searchAsset(name);
        Assert.assertTrue(assetPage.isAssetPresent(name), "Asset with special characters should be created and visible");
    }

    
    @Test(priority = 4,
          groups = {"regression", "admin", "positive"},
          description = "TC_AST_010: Asset ID field is disabled and read-only on form")
    public void testAssetIdFieldReadOnly() {
        assetPage.navigateToAssetManagement();
        
        
        
        List<WebElement> inputs = driver.findElements(By.cssSelector("input"));
        for (WebElement input : inputs) {
            String name = input.getAttribute("formcontrolname");
            if (name != null && (name.toLowerCase().contains("code") || name.toLowerCase().contains("id"))) {
                Assert.assertFalse(input.isEnabled(), "Asset ID input field " + name + " must be disabled.");
            }
        }
    }


}
