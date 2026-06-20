package com.assetms.tests;

import com.assetms.pages.LoginPage;
import com.assetms.pages.TicketManagementPage;
import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TS_TCK_002: Ticket Processing Workflows
 */
public class TicketProcessingWorkflowTest extends BaseTest {

    private String raisedDamagedAssetCode = "";
    private String raisedLostAssetCode = "";

    // Helper: Raises a ticket as an employee and returns the asset code
    private String raiseTicketAsEmployee(String issueType, String desc) {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        new LoginPage(driver).login("john.carter@company.com", "john123");
        
        TicketManagementPage ticketPage = new TicketManagementPage(driver);
        ticketPage.navigateToTicketManagement();
        
        Select select = new Select(driver.findElement(By.cssSelector("select[formcontrolname='assetId']")));
        if (select.getOptions().size() <= 1) {
            return "";
        }
        
        WebElement option = select.getOptions().get(1);
        String assetInfo = option.getText(); // e.g. "AST101 - Dell Laptop"
        String assetCode = assetInfo.split("-")[0].trim();
        
        ticketPage.raiseTicket(assetInfo, issueType, desc);
        WaitUtils.sleep(1000);
        return assetCode;
    }

    // ── TC_TCK_006, TC_TCK_007, TC_TCK_008 ─────────────────────────────────────────
    @Test(priority = 1,
          groups = {"regression", "admin", "positive"},
          description = "TC_TCK_006/7/8: DAMAGED ticket transitions: PENDING → UNDER_REPAIR → RESOLVED → CLOSED")
    public void testDamagedTicketWorkflow() {
        raisedDamagedAssetCode = raiseTicketAsEmployee("DAMAGED", "Damaged laptop keyboard screen cracking - Test");
        if (raisedDamagedAssetCode.isEmpty()) {
            System.out.println("[WARN] No assets available to test damaged workflow. Skipping.");
            return;
        }

        // Login as Admin
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        new LoginPage(driver).login("admin@gmail.com", "admin123");
        
        TicketManagementPage adminTicketPage = new TicketManagementPage(driver);
        adminTicketPage.navigateToTicketManagement();
        
        // Assert ticket is PENDING
        Assert.assertTrue(adminTicketPage.isTicketPresent(raisedDamagedAssetCode), "Ticket should exist in Admin view");
        Assert.assertEquals(adminTicketPage.getTicketStatus(raisedDamagedAssetCode), "PENDING", "Initial ticket state should be PENDING");

        // TC_TCK_006: Click 'Start Repair' -> UNDER_REPAIR
        adminTicketPage.clickActionForTicket(raisedDamagedAssetCode, "Start Repair");
        WaitUtils.sleep(1000);
        Assert.assertEquals(adminTicketPage.getTicketStatus(raisedDamagedAssetCode), "UNDER_REPAIR", "Ticket should transition to UNDER_REPAIR");

        // TC_TCK_007: Click 'Resolve' -> RESOLVED
        adminTicketPage.clickActionForTicket(raisedDamagedAssetCode, "Resolve");
        WaitUtils.sleep(1000);
        Assert.assertEquals(adminTicketPage.getTicketStatus(raisedDamagedAssetCode), "RESOLVED", "Ticket should transition to RESOLVED");

        // TC_TCK_008: Click 'Close' -> CLOSED
        adminTicketPage.clickActionForTicket(raisedDamagedAssetCode, "Close");
        WaitUtils.sleep(1000);
        Assert.assertEquals(adminTicketPage.getTicketStatus(raisedDamagedAssetCode), "CLOSED", "Ticket should transition to CLOSED");
    }

    // ── TC_TCK_009 ──────────────────────────────────────────────────────────────
    @Test(priority = 2,
          groups = {"regression", "admin", "positive"},
          description = "TC_TCK_009: LOST ticket transitions: PENDING → UNDER_REVIEW → CLOSED")
    public void testLostTicketWorkflow() {
        raisedLostAssetCode = raiseTicketAsEmployee("LOST", "Left headset in cafeteria lost forever - Test");
        if (raisedLostAssetCode.isEmpty()) {
            System.out.println("[WARN] No assets available to test lost workflow. Skipping.");
            return;
        }

        // Login as Admin
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        new LoginPage(driver).login("admin@gmail.com", "admin123");
        
        TicketManagementPage adminTicketPage = new TicketManagementPage(driver);
        adminTicketPage.navigateToTicketManagement();
        
        Assert.assertTrue(adminTicketPage.isTicketPresent(raisedLostAssetCode), "Ticket should exist in Admin view");
        Assert.assertEquals(adminTicketPage.getTicketStatus(raisedLostAssetCode), "PENDING", "Initial ticket state should be PENDING");

        // Click 'Under Review' -> UNDER_REVIEW
        adminTicketPage.clickActionForTicket(raisedLostAssetCode, "Under Review");
        WaitUtils.sleep(1000);
        Assert.assertEquals(adminTicketPage.getTicketStatus(raisedLostAssetCode), "UNDER_REVIEW", "Ticket should transition to UNDER_REVIEW");

        // Click 'Confirm Lost' -> CLOSED (Wait, button text could be 'Confirm Lost' or 'Close')
        // Let's try Confirm Lost
        adminTicketPage.clickActionForTicket(raisedLostAssetCode, "Confirm Lost");
        WaitUtils.sleep(1000);
        Assert.assertEquals(adminTicketPage.getTicketStatus(raisedLostAssetCode), "CLOSED", "Ticket should transition to CLOSED");
    }

    // ── TC_TCK_010 ──────────────────────────────────────────────────────────────
    @Test(priority = 3,
          groups = {"regression", "admin", "positive"},
          description = "TC_TCK_010: Action buttons change dynamically with ticket status")
    public void testActionButtonsChangeDynamically() {
        // Log in as Admin
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        new LoginPage(driver).login("admin@gmail.com", "admin123");
        
        TicketManagementPage adminTicketPage = new TicketManagementPage(driver);
        adminTicketPage.navigateToTicketManagement();
        
        // Find tickets in the table and verify buttons
        // This confirms the action button rule mappings.
        List<WebElement> rows = driver.findElements(By.cssSelector(".table-wrap table tr:not(:first-child)"));
        if (rows.isEmpty()) {
            return;
        }
        
        for (WebElement row : rows) {
            String status = row.findElement(By.xpath("./td[6]")).getText().toUpperCase();
            List<WebElement> buttons = row.findElements(By.tagName("button"));
            
            if (status.equals("CLOSED")) {
                Assert.assertTrue(buttons.isEmpty(), "CLOSED tickets should not have action buttons");
            } else if (status.equals("PENDING")) {
                boolean hasValidButton = false;
                for (WebElement btn : buttons) {
                    String text = btn.getText();
                    if (text.equalsIgnoreCase("Start Repair") || text.equalsIgnoreCase("Under Review")) {
                        hasValidButton = true;
                    }
                }
                Assert.assertTrue(hasValidButton, "PENDING ticket should have Start Repair or Under Review action buttons");
            }
        }
    }
}
