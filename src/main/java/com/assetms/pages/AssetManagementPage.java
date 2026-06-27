package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;


public class AssetManagementPage {

    private final WebDriver driver;

    // ── Navigation ──────────────────────────────────────────────────────────────
    // Sidebar link text: "Asset Management"
    private final By navAssetManagement = By.xpath("//span[normalize-space()='Asset Management']");

    // ── Add / Update Form ────────────────────────────────────────────────────────
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

    // ── Asset Table (All Assets section) ─────────────────────────────────────────
    private final By searchInput         = By.cssSelector("input[placeholder='Search by Asset Name or Asset ID']");
    private final By tableRows           = By.cssSelector(".table-wrap table tr:not(:first-child)");
    private final By firstRowNameCell    = By.cssSelector(".table-wrap table tr:nth-child(2) td:nth-child(2)");

    // ── Constructor ──────────────────────────────────────────────────────────────
    public AssetManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    // ── Navigation ────────────────────────────────────────────────────────────────

    public void navigateToAssetManagement() {
        WaitUtils.click(driver, navAssetManagement);
        // Wait for the page heading to appear
        WaitUtils.waitForVisible(driver, By.xpath("//h2[normalize-space(text())='Asset Management']"));
    }

    // ── Form Actions ──────────────────────────────────────────────────────────────

    /**
     * Fills all fields in the Add/Update Asset form.
     *
     * @param assetName    Asset name string.
     * @param category     One of: Laptop, Mouse, Keyboard, Monitor, Mobile, Charger, Headset
     * @param brand        Brand name (e.g. "Dell").
     * @param purchaseDate In yyyy-MM-dd format.
     * @param warrantyDate In yyyy-MM-dd format.
     * @param status       One of: AVAILABLE, ASSIGNED, OVERDUE, LOST, UNDER_REPAIR, UNASSIGNED
     * @param condition    One of: GOOD, FAIR, DAMAGED
     */
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

    /** Overloaded – legacy signature used by existing AssetManagementTest. */
    public void fillAssetDetails(String assetName, String category, String brand,
                                 String status, String condition) {
        WaitUtils.type(driver, assetNameInput, assetName);
        new Select(WaitUtils.waitForVisible(driver, categorySelect)).selectByVisibleText(category);
        WaitUtils.type(driver, brandInput, brand);
        new Select(WaitUtils.waitForVisible(driver, statusSelect)).selectByValue(status);
        new Select(WaitUtils.waitForVisible(driver, conditionSelect)).selectByValue(condition);
    }

    public void clickAddAsset() {
        // The submit button text changes to "Add Asset" when not editing
        WaitUtils.click(driver, submitButton);
    }

    public void clickSave() {
        WaitUtils.click(driver, submitButton);
    }

    public void clickClear() {
        WaitUtils.click(driver, clearButton);
    }

    /** Returns the success/error message shown below the form. */
    public String getFormMessage() {
        return WaitUtils.waitForText(driver, formMessage);
    }

    // ── Table / Search ─────────────────────────────────────────────────────────

    public void searchAsset(String text) {
        WaitUtils.type(driver, searchInput, text);
    }

    /** Returns the name from the first data row of the asset table. */
    public String getNewestAssetName() {
        return WaitUtils.waitForVisible(driver, firstRowNameCell).getText();
    }

    /** Returns the total number of visible rows in the asset table. */
    public int getAssetTableRowCount() {
        List<WebElement> rows = driver.findElements(tableRows);
        return rows.size();
    }

    /** Returns true when a row containing the given asset name is visible. */
    public boolean isAssetPresent(String assetName) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(assetName)) return true;
        }
        return false;
    }

    /** Clicks the Edit button on the row that matches the given asset name. */
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

    /** Clicks the Delete button on the row that matches the given asset name. */
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

    /** Returns the heading text to verify the page is loaded. */
    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, By.xpath("//h2[normalize-space(text())='Asset Management']")).getText();
    }
}
