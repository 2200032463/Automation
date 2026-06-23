package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class EmployeeDashboardPage {

    private final WebDriver driver;

    // ── Navigation ────────────────────────────────────────────────────────────────
    private final By navMyDashboard =
            By.xpath("/html/body/app-root/div/aside/nav/a[2]");

    // ── Page Heading ─────────────────────────────────────────────────────────────
    private final By pageHeading = By.xpath("//h2[normalize-space(text())='Employee Dashboard']");

    // ── Profile Card ─────────────────────────────────────────────────────────────
    private final By profileSection   = By.xpath("//h3[normalize-space(text())='My Profile']/..");
    private final By profileName      = By.xpath("//p[strong[text()='Name:']]");
    private final By profileEmpId     = By.xpath("//p[strong[text()='Employee ID:']]");
    private final By profileDept      = By.xpath("//p[strong[text()='Department:']]");
    private final By profileEmail     = By.xpath("//p[strong[text()='Email:']]");

    // ── Section Tables ─────────────────────────────────────────────────────────────
    private final By assignedAssetsRows =
            By.xpath("//h3[text()='Assigned Assets']/following-sibling::div//table//tr[position()>1]");
    private final By returnHistoryRows  =
            By.xpath("//h3[text()='Return History']/following-sibling::div//table//tr[position()>1]");
    private final By overdueHistoryRows =
            By.xpath("//h3[text()='Overdue History']/following-sibling::div//table//tr[position()>1]");
    private final By ticketHistoryRows  =
            By.xpath("//h3[text()='Ticket History']/following-sibling::div//table//tr[position()>1]");

    // ── Constructor ───────────────────────────────────────────────────────────────
    public EmployeeDashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    // ── Navigation ─────────────────────────────────────────────────────────────────

    public void navigateToEmployeeDashboard() {
        WaitUtils.click(driver, navMyDashboard);
        WaitUtils.waitForVisible(driver, pageHeading);
    }

    // ── Verifications ───────────────────────────────────────────────────────────────

    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, pageHeading).getText();
    }

    public boolean isPageVisible() {
        return WaitUtils.isPresent(driver, pageHeading);
    }

    public boolean isProfileSectionVisible() {
        try {
            WaitUtils.waitForVisible(driver, profileSection);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Profile Fields ────────────────────────────────────────────────────────────

    /** Returns full text like "Name: John Doe" */
    public String getProfileName() {
        return WaitUtils.waitForVisible(driver, profileName).getText();
    }

    public String getProfileEmployeeId() {
        return WaitUtils.waitForVisible(driver, profileEmpId).getText();
    }

    public String getProfileDepartment() {
        return WaitUtils.waitForVisible(driver, profileDept).getText();
    }

    public String getProfileEmail() {
        return WaitUtils.waitForVisible(driver, profileEmail).getText();
    }

    // ── Table Row Counts ──────────────────────────────────────────────────────────

    public int getAssignedAssetsCount() {
        return driver.findElements(assignedAssetsRows).size();
    }

    public int getReturnHistoryCount() {
        return driver.findElements(returnHistoryRows).size();
    }

    public int getOverdueHistoryCount() {
        return driver.findElements(overdueHistoryRows).size();
    }

    public int getTicketHistoryCount() {
        return driver.findElements(ticketHistoryRows).size();
    }
}

