package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
public class EmployeeManagementPage {

    private final WebDriver driver;

    
    
    private final By navEmployeeManagement =
            By.xpath("//span[normalize-space()='Employee Management']");

    
    private final By pageHeading  = By.xpath("//h2[normalize-space(text())='Employee Management']");
    private final By searchInput  = By.cssSelector("input.input[placeholder='Search by Employee Name, ID, Role']");
    private final By pageMessage  = By.cssSelector("p.info");

    
    
    private final By tableRows = By.cssSelector("table tr:not(:first-child)");

    
    public EmployeeManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    

    public void navigateToEmployeeManagement() {
        WaitUtils.click(driver, navEmployeeManagement);
        WaitUtils.waitForVisible(driver, pageHeading);
    }

    

    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, pageHeading).getText();
    }

    public boolean isPageVisible() {
        return WaitUtils.isPresent(driver, pageHeading);
    }

    

    
    public void searchEmployee(String searchText) {
        
        WebElement field = WaitUtils.waitForClickable(driver, searchInput);
        field.click();
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        field.sendKeys(Keys.chord(Keys.COMMAND, "a"));
        field.sendKeys(Keys.DELETE);

        
        if (searchText != null && !searchText.isEmpty()) {
            field.clear();
            field.sendKeys(searchText);
        }

        
        WaitUtils.waitForCondition(driver, d -> {
            int before = d.findElements(tableRows).size();
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            int after = d.findElements(tableRows).size();
            return before == after;
        }, 5);
    }

    
    public void clearSearch() {
        WebElement field = WaitUtils.waitForClickable(driver, searchInput);
        field.click();
        field.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        field.sendKeys(Keys.chord(Keys.COMMAND, "a"));
        field.sendKeys(Keys.DELETE);
        
        WaitUtils.waitForCondition(driver, d -> {
            int before = d.findElements(tableRows).size();
            try { Thread.sleep(300); } catch (InterruptedException ignored) {}
            int after = d.findElements(tableRows).size();
            return before == after && after > 0;
        }, 5);
    }

    

    
    public int getEmployeeTableRowCount() {
        List<WebElement> rows = driver.findElements(tableRows);
        if(rows.isEmpty())
        {
            return 0;
        }
        return rows.size();

    }

    
    public boolean isEmployeeVisible(String nameOrId) {
        
        try {
            WaitUtils.waitForCondition(driver,
                    d -> !d.findElements(tableRows).isEmpty(), 5);
        } catch (Exception ignored) {
            
        }
        WebElement element = driver.findElement(searchInput);
        element.clear();
        element.sendKeys(nameOrId);

        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().toLowerCase().contains(nameOrId.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    
    public void clickViewDetails(String employeeName) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(employeeName)) {
                row.findElement(By.cssSelector("button.btn-secondary")).click();
                return;
            }
        }
        throw new RuntimeException("Employee not found: " + employeeName);
    }

    
    public void clickDeleteEmployee(String employeeName) throws InterruptedException {
        WebElement element = driver.findElement(searchInput);
        element.clear();
        element.sendKeys(employeeName);
        Thread.sleep(3000);
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(employeeName)) {
                System.out.println(row.getText());
                row.findElement(By.xpath("/html/body/app-root/div/main/div/app-employee-management/section/div/table/tr[2]/td[6]/button[2]")).click();
                return;
            }
        }
        throw new RuntimeException("Employee not found: " + employeeName);
    }

    
    public String getCellValue(String identifier, int colIndex) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(identifier)) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (colIndex < cells.size()) {
                    return cells.get(colIndex).getText();
                }
            }
        }
        return "";
    }

    
    public String getMessage() {
        if (WaitUtils.isPresent(driver, pageMessage)) {
            return driver.findElement(pageMessage).getText();
        }
        return "";
    }
}
