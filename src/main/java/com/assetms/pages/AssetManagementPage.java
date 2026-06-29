package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;


public class AssetManagementPage {

    private final WebDriver driver;

    
    
    private final By navAssetManagement = By.xpath("//span[normalize-space()='Asset Management']");

    
    private final By assetNameInput    = By.cssSelector("input[formcontrolname='assetName']");
    private final By categorySelect    = By.cssSelector("select[formcontrolname='category']");
    private final By brandInput        = By.cssSelector("input[formcontrolname='brand']");
    private final By purchaseDateInput = By.cssSelector("input[formcontrolname='purchaseDate']");
    private final By warrantyDateInput = By.cssSelector("input[formcontrolname='warrantyDate']");
    private final By statusSelect      = By.cssSelector("select[formcontrolname='assetStatus']");
    private final By conditionSelect   = By.cssSelector("select[formcontrolname='assetCondition']");
    private final By submitButton      = By.cssSelector("button[type='submit']");
    private final By clearButton       = By.xpath("//button[normalize-space(text())='Clear']");
    private final By formMessage       = By.cssSelector("p.info");

    
    private final By searchInput         = By.cssSelector("input[placeholder='Search by Asset Name or Asset ID']");
    private final By tableRows           = By.cssSelector(".table-wrap table tr:not(:first-child)");
    private final By firstRowNameCell    = By.cssSelector(".table-wrap table tr:nth-child(2) td:nth-child(2)");

    
    public AssetManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    

    public void navigateToAssetManagement() {
        WaitUtils.click(driver, navAssetManagement);
        
        WaitUtils.waitForVisible(driver, By.xpath("//h2[normalize-space(text())='Asset Management']"));
        
        try {
            WaitUtils.waitForCondition(driver,
                d -> !d.findElements(tableRows).isEmpty(), 10);
        } catch (Exception ignored) {
            
        }
    }


    public void fillAssetForm(String assetName, String category, String brand,
                              String purchaseDate, String warrantyDate,
                              String status, String condition) {
        WaitUtils.type(driver, assetNameInput, assetName);
        new Select(WaitUtils.waitForVisible(driver, categorySelect)).selectByVisibleText(category);
        WaitUtils.type(driver, brandInput, brand);
        WaitUtils.type(driver, purchaseDateInput, purchaseDate);
        WaitUtils.type(driver, warrantyDateInput, warrantyDate);
        new Select(WaitUtils.waitForVisible(driver, statusSelect)).selectByValue(status);
        new Select(WaitUtils.waitForVisible(driver, conditionSelect)).selectByValue(condition);
    }


    public void clickAddAsset() {
        
        WaitUtils.click(driver, submitButton);
    }

    public void clickSave() {
        WaitUtils.click(driver, submitButton);
    }

    public void clickClear() {
        WaitUtils.click(driver, clearButton);
    }

    
    public String getFormMessage() {
        return WaitUtils.waitForText(driver, formMessage);
    }

    

    
    public void searchAsset(String text) {
        WaitUtils.sleep(3000);
        WebElement field = WaitUtils.waitForClickable(driver, searchInput);
        field.click();
        field.clear();
        field.sendKeys(Keys.DELETE);

        
        if (text != null && !text.isEmpty()) {
            field.sendKeys(text);
        }

        
        
        WaitUtils.waitForCondition(driver, d -> {
            int before = d.findElements(tableRows).size();
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            int after = d.findElements(tableRows).size();
            return before == after;
        }, 5);
    }

    
    public String getNewestAssetName() {
        return WaitUtils.waitForVisible(driver, firstRowNameCell).getText();
    }

    
    public int getAssetTableRowCount() {
        List<WebElement> rows = driver.findElements(tableRows);
        return rows.size();
    }

    
    public boolean isAssetPresent(String assetName) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(assetName)) return true;
        }
        return false;
    }

    
    public void clickEditForAsset(String assetName) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(assetName)) {
                row.findElement(By.cssSelector("button.btn-warning")).click();
                return;
            }
        }
        throw new RuntimeException("Asset not found in table: " + assetName);
    }

    
    public void clickDeleteForAsset(String assetName) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(assetName)) {
                row.findElement(By.cssSelector("button.btn-danger")).click();
                return;
            }
        }
        throw new RuntimeException("Asset not found in table: " + assetName);
    }

    
    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, By.xpath("//h2[normalize-space(text())='Asset Management']")).getText();
    }

    
    public String getbuttontext() {
        return WaitUtils.waitForNonBlankText(driver, submitButton);
    }
}
