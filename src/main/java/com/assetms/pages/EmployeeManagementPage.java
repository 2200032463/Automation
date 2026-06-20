package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
public class EmployeeManagementPage {

    private final WebDriver driver;

    // ── Navigation ────────────────────────────────────────────────────────────────
    private final By navEmployeeManagement =
            By.xpath("//html/body/app-root/div/aside/nav/a[2]");

    // ── Page Elements ─────────────────────────────────────────────────────────────
    private final By pageHeading  = By.xpath("//h2[normalize-space(text())='Employee Management']");
    private final By searchInput  = By.cssSelector("input.input[placeholder='Search by Employee Name, ID, Role']");
    private final By pageMessage  = By.cssSelector("p.info");

    // ── Table ─────────────────────────────────────────────────────────────────────
    // All data rows (skipping header)
    private final By tableRows    = By.cssSelector("table tr:not(:first-child)");

    // ── Constructor ───────────────────────────────────────────────────────────────
    public EmployeeManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    // ── Navigation ─────────────────────────────────────────────────────────────────

    public void navigateToEmployeeManagement() {
        WaitUtils.click(driver, navEmployeeManagement);
        WaitUtils.waitForVisible(driver, pageHeading);
    }

    // ── Verifications ───────────────────────────────────────────────────────────────

    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, pageHeading).getText();
    }

    public boolean isPageVisible() {
        return WaitUtils.isPresent(driver, pageHeading);
    }

    // ── Search ─────────────────────────────────────────────────────────────────────

    public void searchEmployee(String searchText) {
        WaitUtils.type(driver, searchInput, searchText);
    }

    public void clearSearch() {
        WaitUtils.waitForVisible(driver, searchInput).clear();
    }

    // ── Table Helpers ───────────────────────────────────────────────────────────────

    /** Total visible employee data rows (excludes the header row). */
    public int getEmployeeTableRowCount() {
        return driver.findElements(tableRows).size();
    }

    /** Checks if any row contains the given search term anywhere in its text. */
    public boolean isEmployeeVisible(String nameOrId) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().toLowerCase().contains(nameOrId.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /** Clicks "View Details" for the first row matching the given name. */
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

    /** Clicks "Delete" for the first row matching the given name. */
    public void clickDeleteEmployee(String employeeName) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(employeeName)) {
                row.findElement(By.cssSelector("button.btn-danger")).click();
                return;
            }
        }
        throw new RuntimeException("Employee not found: " + employeeName);
    }

    /**
     * Gets the column value (0-based) from the first row whose any column
     * text matches the given identifier.
     *
     * @param identifier  Employee name or ID to find the row.
     * @param colIndex    0-based column index (0=EmployeeID, 1=Name, etc.).
     */
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

    /** Returns the info/error message shown on the page. */
    public String getMessage() {
        if (WaitUtils.isPresent(driver, pageMessage)) {
            return driver.findElement(pageMessage).getText();
        }
        return "";
    }
}
