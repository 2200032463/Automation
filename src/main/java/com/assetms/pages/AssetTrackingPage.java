package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;


public class AssetTrackingPage {

    private final WebDriver driver;

    private final By navAssetTracking =
            By.xpath("//span[normalize-space()='Asset Tracking']");

    
    private final By pageHeading = By.xpath("//h2[normalize-space(text())='Asset Tracking Page']");

    
    private final By filterAll       = By.xpath("//button[normalize-space(text())='All Assets']");
    private final By filterAvailable = By.xpath("//button[normalize-space(text())='Available Assets']");
    private final By filterAssigned  = By.xpath("//button[normalize-space(text())='Assigned Assets']");
    private final By filterOverdue   = By.xpath("//button[normalize-space(text())='Overdue Assets']");
    private final By filterLost      = By.xpath("//button[normalize-space(text())='Lost Assets']");
    private final By filterDamaged   = By.xpath("//button[normalize-space(text())='Damaged Assets']");

    
    private final By searchInput = By.cssSelector(
            "input.input[placeholder='Search Asset Name, Asset ID or Employee Name']");

    
    private final By tableRows = By.cssSelector(".table-wrap table tr:not(:first-child)");

    
    public AssetTrackingPage(WebDriver driver) {
        this.driver = driver;
    }

    

    public void navigateToAssetTracking() {
        WaitUtils.click(driver, navAssetTracking);
        WaitUtils.waitForVisible(driver, pageHeading);
    }

    

    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, pageHeading).getText();
    }

    public boolean isPageVisible() {
        return WaitUtils.isPresent(driver, pageHeading);
    }

    

    public void filterAll() {
        WaitUtils.click(driver, filterAll);
        WaitUtils.sleep(500);
    }

    public void filterAvailable() {
        WaitUtils.click(driver, filterAvailable);
        WaitUtils.sleep(500);
    }

    public void filterAssigned() {
        WaitUtils.click(driver, filterAssigned);
        WaitUtils.sleep(500);
    }

    public void filterOverdue() {
        WaitUtils.click(driver, filterOverdue);
        WaitUtils.sleep(500);
    }

    public void filterLost() {
        WaitUtils.click(driver, filterLost);
        WaitUtils.sleep(500);
    }

    public void filterDamaged() {
        WaitUtils.click(driver, filterDamaged);
        WaitUtils.sleep(500);
    }

    

    public void searchAsset(String text) {
        WaitUtils.type(driver, searchInput, text);
        WaitUtils.sleep(400);
    }

    public void clearSearch() {
        WaitUtils.waitForVisible(driver, searchInput).clear();
    }

    

    
    public int getTableRowCount() {
        return driver.findElements(tableRows).size();
    }

    
    public boolean isRowPresent(String text) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().toLowerCase().contains(text.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    
    public boolean allRowsHaveStatus(String expectedStatus) {
        List<WebElement> rows = driver.findElements(tableRows);
        if (rows.isEmpty()) return false;
        for (WebElement row : rows) {
            
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.isEmpty()) continue;
            String statusCell = cells.get(cells.size() - 1).getText();
            if (!statusCell.equalsIgnoreCase(expectedStatus)) return false;
        }
        return true;
    }
}

