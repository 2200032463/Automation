package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

/**
 * TicketManagementPage – Page Object for the Ticket Management screen.
 *
 * Locators derived from ticket-management.component.html.
 *
 * Note: The "Raise Ticket" form is only shown when role === 'EMPLOYEE'.
 *       Admin sees only the "Open Tickets" table with action buttons.
 *
 * Form elements (Employee only):
 *   <select formControlName="assetId" class="input">...</select>
 *   <select formControlName="issueType" class="input">DAMAGED | LOST</select>
 *   <textarea formControlName="issueDescription" ...></textarea>
 *   <button class="btn btn-primary" type="submit">Create Ticket</button>
 *
 * Admin action buttons per row:
 *   DAMAGED + PENDING     → "Start Repair"   → UNDER_REPAIR
 *   DAMAGED + UNDER_REPAIR → "Resolve"       → RESOLVED
 *   DAMAGED + RESOLVED     → "Close"         → CLOSED
 *   LOST + PENDING         → "Under Review"  → UNDER_REVIEW
 *   LOST + UNDER_REVIEW    → "Confirm Lost"  → CLOSED
 */
public class TicketManagementPage {

    private final WebDriver driver;

    private final By navTicketManagement =
            By.xpath("//span[normalize-space()='Ticket Management']");

    // ── Page Heading ─────────────────────────────────────────────────────────────
    private final By pageHeading = By.xpath("//h2[normalize-space(text())='Ticket Management']");

    // ── Raise Ticket Form (Employee) ──────────────────────────────────────────────
    private final By raiseTicketSection = By.xpath("//h3[normalize-space(text())='Raise Ticket']");
    private final By assetIdSelect      = By.cssSelector("select[formcontrolname='assetId']");
    private final By issueTypeSelect    = By.cssSelector("select[formcontrolname='issueType']");
    private final By issueDescription   = By.cssSelector("textarea[formcontrolname='issueDescription']");
    private final By createTicketButton = By.cssSelector("button[type='submit']");
    private final By formMessage        = By.cssSelector("p.info");

    // ── Tickets Table (Both Roles) ────────────────────────────────────────────────
    private final By tableRows = By.cssSelector(".table-wrap table tr:not(:first-child)");

    // ── Constructor ───────────────────────────────────────────────────────────────
    public TicketManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    // ── Navigation ─────────────────────────────────────────────────────────────────

    public void navigateToTicketManagement() {
        WaitUtils.click(driver, navTicketManagement);
        WaitUtils.waitForVisible(driver, pageHeading);
    }

    // ── Verifications ───────────────────────────────────────────────────────────────

    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, pageHeading).getText();
    }

    public boolean isPageVisible() {
        return WaitUtils.isPresent(driver, pageHeading);
    }

    public boolean isRaiseTicketFormVisible() {
        return WaitUtils.isPresent(driver, raiseTicketSection);
    }

    // ── Raise Ticket (Employee) ───────────────────────────────────────────────────

    /**
     * Selects the asset from the Asset ID dropdown by matching text.
     * Option text format: "AST101 - Dell Laptop"
     */
    public void selectAsset(String assetText) {
        Select select = new Select(WaitUtils.waitForVisible(driver, assetIdSelect));
        for (var option : select.getOptions()) {
            if (option.getText().contains(assetText)) {
                option.click();
                return;
            }
        }
        // Fall back to first non-placeholder
        if (select.getOptions().size() > 1) {
            select.selectByIndex(1);
        }
    }

    /**
     * Selects issue type: "DAMAGED" or "LOST".
     */
    public void selectIssueType(String issueType) {
        new Select(WaitUtils.waitForVisible(driver, issueTypeSelect)).selectByValue(issueType);
    }

    /**
     * Types the issue description in the textarea.
     * Minimum 10 characters required by the Angular validator.
     */
    public void enterIssueDescription(String description) {
        WaitUtils.type(driver, issueDescription, description);
    }

    /** Clicks the "Create Ticket" button. */
    public void clickCreateTicket() {
        WaitUtils.click(driver, createTicketButton);
    }

    /**
     * Full workflow to raise a ticket (Employee role only).
     */
    public void raiseTicket(String assetText, String issueType, String description) {
        selectAsset(assetText);
        selectIssueType(issueType);
        enterIssueDescription(description);
        clickCreateTicket();
    }

    /** Returns the success/error message shown on the form. */
    public String getFormMessage() {
        return WaitUtils.waitForText(driver, formMessage);
    }

    // ── Admin Actions ─────────────────────────────────────────────────────────────

    /**
     * Clicks the given action button in the row that contains the ticketId text.
     *
     * @param ticketId      Ticket ID (partial match on row text).
     * @param buttonText    "Start Repair" | "Resolve" | "Close" | "Under Review" | "Confirm Lost"
     */
    public void clickActionForTicket(String ticketId, String buttonText) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(ticketId)) {
                List<WebElement> buttons = row.findElements(By.tagName("button"));
                for (WebElement btn : buttons) {
                    if (btn.getText().equalsIgnoreCase(buttonText)) {
                        btn.click();
                        return;
                    }
                }
            }
        }
        throw new RuntimeException("Action button '" + buttonText + "' not found for ticket: " + ticketId);
    }

    // ── Table Helpers ───────────────────────────────────────────────────────────────

    /** Total number of visible ticket rows. */
    public int getTicketRowCount() {
        return driver.findElements(tableRows).size();
    }

    /** Returns true if any row contains the given text (e.g. ticket ID). */
    public boolean isTicketPresent(String text) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().toLowerCase().contains(text.toLowerCase())) return true;
        }
        return false;
    }

    /**
     * Gets the status cell text from the row matching the given ticket ID.
     * Status column is column index 5 (0-based).
     */
    public String getTicketStatus(String ticketId) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(ticketId)) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() > 5) {
                    return cells.get(5).getText();
                }
            }
        }
        return "";
    }
}