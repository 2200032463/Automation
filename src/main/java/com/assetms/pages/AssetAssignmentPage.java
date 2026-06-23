
package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;


public class AssetAssignmentPage {

    private final WebDriver driver;

    private final By navAssetAssignment =
            By.xpath("//span[normalize-space()='Asset Assignment']");

    // ── Page Heading ─────────────────────────────────────────────────────────────
    private final By pageHeading = By.xpath("//h2[normalize-space(text())='Assign Asset']");

    // ── Form Elements ─────────────────────────────────────────────────────────────
    private final By categorySelect    = By.cssSelector("select[formcontrolname='category']");
    private final By assetIdSelect     = By.cssSelector("select[formcontrolname='assetId']");
    private final By employeeIdSelect  = By.cssSelector("select[formcontrolname='employeeId']");
    private final By returnDeadline    = By.cssSelector("input[formcontrolname='returnDeadline']");
    private final By assignButton      = By.cssSelector("button[type='submit']");
    private final By pageMessage       = By.cssSelector("p.info");

    // ── Constructor ───────────────────────────────────────────────────────────────
    public AssetAssignmentPage(WebDriver driver) {
        this.driver = driver;
    }

    // ── Navigation ─────────────────────────────────────────────────────────────────

    public void navigateToAssetAssignment() {
        WaitUtils.click(driver, navAssetAssignment);
        WaitUtils.waitForVisible(driver, pageHeading);
    }

    // ── Verifications ───────────────────────────────────────────────────────────────

    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, pageHeading).getText();
    }

    public boolean isPageVisible() {
        return WaitUtils.isPresent(driver, pageHeading);
    }

    // ── Form Actions ──────────────────────────────────────────────────────────────

    /**
     * Selects a category from the dropdown.
     * This triggers the Angular onCategoryChange() which loads assets for that category.
     *
     * @param category  e.g. "Laptop"
     */
    public void selectCategory(String category) {
        new Select(WaitUtils.waitForVisible(driver, categorySelect)).selectByVisibleText(category);
        // Brief pause to allow the Angular event to populate assets
        WaitUtils.sleep(800);
    }

    /**
     * Selects the asset from the Asset ID dropdown by visible text.
     * Visible text is in the form "AST101 - Dell Laptop".
     *
     * @param assetText  Partial or full text shown in the option.
     */
    public void selectAssetByText(String assetText) {
        Select select = new Select(WaitUtils.waitForVisible(driver, assetIdSelect));
        for (var option : select.getOptions()) {
            if (option.getText().contains(assetText)) {
                option.click();
                return;
            }
        }
        // Fallback: try by index (first non-placeholder)
        if (select.getOptions().size() > 1) {
            select.selectByIndex(1);
        }
    }

    /**
     * Selects the employee from the Employee dropdown by visible text.
     * Visible text is "EMP001 - John Doe".
     *
     * @param employeeText  Partial or full visible text of the option.
     */
    public void selectEmployeeByText(String employeeText) {
        Select select = new Select(WaitUtils.waitForVisible(driver, employeeIdSelect));
        for (var option : select.getOptions()) {
            if (option.getText().contains(employeeText)) {
                option.click();
                return;
            }
        }
        // Fallback: first non-placeholder option
        if (select.getOptions().size() > 1) {
            select.selectByIndex(1);
        }
    }

    /**
     * Sets the Return Deadline date field.
     *
     * @param date  In yyyy-MM-dd format.
     */
    public void setReturnDeadline(String date) {
        WaitUtils.type(driver, returnDeadline, date);
    }

    /** Clicks the "Assign Asset" submit button. */
    public void clickAssign() {
        WaitUtils.click(driver, assignButton);
    }

    /**
     * Full workflow: selects category → asset → employee → deadline → submits.
     */
    public void assignAsset(String category, String assetText, String employeeText, String deadline) {
        selectCategory(category);
        selectAssetByText(assetText);
        selectEmployeeByText(employeeText);
        setReturnDeadline(deadline);
        clickAssign();
    }

    /** Returns the result message after assigning. */
    public String getMessage() {
        return WaitUtils.waitForText(driver, pageMessage);
    }

    /** Checks if the message element is present and non-empty. */
    public boolean hasMessage() {
        if (WaitUtils.isPresent(driver, pageMessage)) {
            String txt = driver.findElement(pageMessage).getText();
            return txt != null && !txt.isBlank();
        }
        return false;
    }
}
