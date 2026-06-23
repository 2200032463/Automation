package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class EmployeeDetailsPage {

    private final WebDriver driver;

    // ── Page Heading ─────────────────────────────────────────────────────────────
    private final By pageHeading = By.xpath("//h2[normalize-space(text())='Employee Details']");

    // ── Profile Info ─────────────────────────────────────────────────────────────
    // Locate <p> tags by text content of the <strong> sibling
    private final By employeeIdText    = By.xpath("//p[strong[text()='Employee ID:']]");
    private final By nameText          = By.xpath("//p[strong[text()='Name:']]");
    private final By departmentText    = By.xpath("//p[strong[text()='Department:']]");
    private final By roleText          = By.xpath("//p[strong[text()='Role:']]");
    private final By emailText         = By.xpath("//p[strong[text()='Email:']]");
    private final By phoneText         = By.xpath("//p[strong[text()='Phone:']]");
    private final By pageMessage       = By.cssSelector("p.info");

    // ── Section Tables ─────────────────────────────────────────────────────────────
    private final By assignedAssetsRows = By.xpath("//h3[text()='Assigned Assets']/following-sibling::div//table//tr[position()>1]");
    private final By assetHistoryRows   = By.xpath("//h3[text()='Asset History']/following-sibling::div//table//tr[position()>1]");
    private final By returnHistoryRows  = By.xpath("//h3[text()='Return History']/following-sibling::div//table//tr[position()>1]");
    private final By ticketHistoryRows  = By.xpath("//h3[text()='Ticket History']/following-sibling::div//table//tr[position()>1]");

    // ── Unassign Button ───────────────────────────────────────────────────────────
    // Unassign button in the Assigned Assets table
    private final By unassignButtons = By.xpath("//h3[text()='Assigned Assets']/following-sibling::div//button[contains(@class,'btn-warning')]");

    // ── Constructor ───────────────────────────────────────────────────────────────
    public EmployeeDetailsPage(WebDriver driver) {
        this.driver = driver;
    }

    // ── Verifications ─────────────────────────────────────────────────────────────

    public boolean isPageVisible() {
        return WaitUtils.isPresent(driver, pageHeading);
    }

    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, pageHeading).getText();
    }

    // ── Profile Fields ────────────────────────────────────────────────────────────

    /** Returns the full <p> text, e.g. "Employee ID: EMP001". Extract value after ": " if needed. */
    public String getEmployeeIdText() {
        return WaitUtils.waitForVisible(driver, employeeIdText).getText();
    }

    public String getNameText() {
        return WaitUtils.waitForVisible(driver, nameText).getText();
    }

    public String getDepartmentText() {
        return WaitUtils.waitForVisible(driver, departmentText).getText();
    }

    public String getRoleText() {
        return WaitUtils.waitForVisible(driver, roleText).getText();
    }

    public String getEmailText() {
        return WaitUtils.waitForVisible(driver, emailText).getText();
    }

    public String getPhoneText() {
        return WaitUtils.waitForVisible(driver, phoneText).getText();
    }

    // ── Table Row Counts ──────────────────────────────────────────────────────────

    public int getAssignedAssetsCount() {
        return driver.findElements(assignedAssetsRows).size();
    }

    public int getAssetHistoryCount() {
        return driver.findElements(assetHistoryRows).size();
    }

    public int getReturnHistoryCount() {
        return driver.findElements(returnHistoryRows).size();
    }

    public int getTicketHistoryCount() {
        return driver.findElements(ticketHistoryRows).size();
    }

    // ── Actions ───────────────────────────────────────────────────────────────────

    /** Clicks the first available Unassign button in the Assigned Assets table. */
    public void clickFirstUnassign() {
        List<WebElement> buttons = driver.findElements(unassignButtons);
        if (!buttons.isEmpty()) {
            buttons.get(0).click();
        } else {
            throw new RuntimeException("No Unassign buttons found on Employee Details page.");
        }
    }

    /** Returns the message text shown at the top of the page. */
    public String getMessage() {
        if (WaitUtils.isPresent(driver, pageMessage)) {
            return driver.findElement(pageMessage).getText();
        }
        return "";
    }

    /** Navigates back using the browser back button. */
    public void goBack() {
        driver.navigate().back();
    }
}

