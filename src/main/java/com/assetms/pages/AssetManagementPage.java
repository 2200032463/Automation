package com.assetms.pages;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class AssetManagementPage {

    private WebDriver driver;
    private WebDriverWait wait;
    // 1. Locators
    private By assetMenuLink = By.xpath("/html/body/app-root/div/aside/nav/a[3]");// or locator used in your navigation panel
    private By addAssetButton = By.cssSelector("button#add-asset-btn, .btn-primary");
    private By assetNameInput = By.xpath("/html/body/app-root/div/main/div/app-asset-management/section/div[1]/form/div[2]/input");
    private By categorySelect = By.xpath("//select[@formcontrolname='category']");
    private By brandInput = By.xpath("//input[@placeholder='Enter Brand Name (Dell, HP, Lenovo...)']");
    private By statusSelect = By.name("assetStatus");
    private By conditionSelect = By.name("assetCondition");
    private By saveButton = By.xpath("//button[text()='Save' or @type='submit']");

    // Grid item verification locator
    private By firstRowAssetName = By.cssSelector("table.assets-table tbody tr:first-child td.name-column");

    // Constructor
    public AssetManagementPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // 2. Page Actions
    public void navigateToAssetManagement() {
        wait.until(ExpectedConditions.elementToBeClickable(assetMenuLink)).click();
    }

    public void clickAddAsset() {
        wait.until(ExpectedConditions.elementToBeClickable(addAssetButton)).click();
    }

    public void fillAssetDetails(String name, String category, String brand, String status, String condition) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(assetNameInput)).sendKeys(name);

        new Select(driver.findElement(categorySelect)).selectByVisibleText(category);
        driver.findElement(brandInput).sendKeys(brand);
        new Select(driver.findElement(statusSelect)).selectByVisibleText(status);
        new Select(driver.findElement(conditionSelect)).selectByVisibleText(condition);
    }

    public void clickSave() {
        driver.findElement(saveButton).click();
    }

    public String getNewestAssetName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(firstRowAssetName)).getText();
    }
}