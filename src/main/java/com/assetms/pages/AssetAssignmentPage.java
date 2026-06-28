
package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;


public class AssetAssignmentPage {

    private final WebDriver driver;

    private final By navAssetAssignment =
            By.xpath("//span[normalize-space()='Asset Assignment']");

    
    private final By pageHeading = By.xpath("//h2[normalize-space(text())='Assign Asset']");

    
    private final By categorySelect    = By.cssSelector("select[formcontrolname='category']");
    private final By assetIdSelect     = By.cssSelector("select[formcontrolname='assetId']");
    private final By employeeIdSelect  = By.cssSelector("select[formcontrolname='employeeId']");
    private final By returnDeadline    = By.cssSelector("input[formcontrolname='returnDeadline']");
    private final By assignButton      = By.cssSelector("button[type='submit']");
    private final By pageMessage       = By.cssSelector("p.info");

    
    public AssetAssignmentPage(WebDriver driver) {
        this.driver = driver;
    }

    

    public void navigateToAssetAssignment() {
        WaitUtils.click(driver, navAssetAssignment);
        WaitUtils.waitForVisible(driver, pageHeading);
    }

    

    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, pageHeading).getText();
    }

    public boolean isPageVisible() {
        return WaitUtils.isPresent(driver, pageHeading);
    }

    

    
    public void selectCategory(String category) {
        new Select(WaitUtils.waitForVisible(driver, categorySelect)).selectByVisibleText(category);
        
        WaitUtils.sleep(800);
    }

    
    public void selectAssetByText(String assetText) {
        Select select = new Select(WaitUtils.waitForVisible(driver, assetIdSelect));
        for (var option : select.getOptions()) {
            if (option.getText().contains(assetText)) {
                option.click();
                return;
            }
        }
        
        if (select.getOptions().size() > 1) {
            select.selectByIndex(1);
        }
    }

    
    public void selectEmployeeByText(String employeeText) {
        Select select = new Select(WaitUtils.waitForVisible(driver, employeeIdSelect));
        for (var option : select.getOptions()) {
            if (option.getText().contains(employeeText)) {
                option.click();
                return;
            }
        }
        
        if (select.getOptions().size() > 1) {
            select.selectByIndex(1);
        }
    }

    
    public void setReturnDeadline(String date) {
        WaitUtils.type(driver, returnDeadline, date);
    }

    
    public void clickAssign() {
        WaitUtils.click(driver, assignButton);
    }

    
    public void assignAsset(String category, String assetText, String employeeText, String deadline) {
        selectCategory(category);
        selectAssetByText(assetText);
        selectEmployeeByText(employeeText);
        setReturnDeadline(deadline);
        clickAssign();
    }

    
    public String getMessage() {
        return WaitUtils.waitForText(driver, pageMessage);
    }

    
    public boolean hasMessage() {
        if (WaitUtils.isPresent(driver, pageMessage)) {
            String txt = driver.findElement(pageMessage).getText();
            return txt != null && !txt.isBlank();
        }
        return false;
    }
}
