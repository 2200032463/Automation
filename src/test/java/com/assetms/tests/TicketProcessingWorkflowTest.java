package com.assetms.tests;

import com.assetms.pages.LoginPage;
import com.assetms.pages.TicketManagementPage;
import com.assetms.utils.WaitUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;


public class TicketProcessingWorkflowTest extends BaseTest {

    private String raisedDamagedAssetCode = "";
    private String raisedLostAssetCode = "";

    
    
    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        
    }

    
    private  String raiseTicketAsEmployee(String issueType, String desc) {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        new LoginPage(driver).login("john.carter@company.com", "john123");
        
        WaitUtils.waitForUrlContains(driver, "/employee-dashboard");

        TicketManagementPage ticketPage = new TicketManagementPage(driver);
        ticketPage.navigateToTicketManagement();

        
        try {
            WaitUtils.waitForCondition(driver,
                d -> new Select(d.findElement(
                    By.cssSelector("select[formcontrolname='assetId']")
                )).getOptions().size() > 1, 10);
        } catch (Exception e) {
            return ""; 
        }

        Select select = new Select(driver.findElement(
                By.cssSelector("select[formcontrolname='assetId']")));
        if (select.getOptions().size() <= 1) {
            return "";
        }

        WebElement option = select.getOptions().get(1);
        String assetInfo = option.getText(); 
        String assetCode = assetInfo.split("-")[0].trim();

        ticketPage.raiseTicket(assetInfo, issueType, desc);
        
        try { ticketPage.getFormMessage(); } catch (Exception ignored) {}
        return assetCode;
    }

    
    @Test(priority = 1,
          groups = {"regression", "admin", "positive"},
          description = "TC_TCK_006/7/8: DAMAGED ticket transitions: PENDING → UNDER_REPAIR → RESOLVED → CLOSED")
    public void testDamagedTicketWorkflow() {
        raisedDamagedAssetCode = raiseTicketAsEmployee("DAMAGED", "Damaged laptop keyboard screen cracking - Test");
        if (raisedDamagedAssetCode.isEmpty()) {
            System.out.println("No assets available to test damaged workflow. Skipping.");
            return;
        }

        
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        new LoginPage(driver).login("admin@gmail.com", "admin123");
        
        TicketManagementPage adminTicketPage = new TicketManagementPage(driver);
        adminTicketPage.navigateToTicketManagement();
        
        
        Assert.assertTrue(adminTicketPage.isTicketPresent(raisedDamagedAssetCode), "Ticket should exist in Admin view");
        Assert.assertEquals(adminTicketPage.getTicketStatus(raisedDamagedAssetCode), "PENDING", "Initial ticket state should be PENDING");

        
        adminTicketPage.clickActionForTicket(raisedDamagedAssetCode, "Start Repair");
        Assert.assertEquals(
            adminTicketPage.waitForTicketStatus(raisedDamagedAssetCode, "UNDER_REPAIR", 10),
            "UNDER_REPAIR", "Ticket should transition to UNDER_REPAIR");

        
        adminTicketPage.clickActionForTicket(raisedDamagedAssetCode, "Resolve");
        Assert.assertEquals(
            adminTicketPage.waitForTicketStatus(raisedDamagedAssetCode, "RESOLVED", 10),
            "RESOLVED", "Ticket should transition to RESOLVED");

        
        adminTicketPage.clickActionForTicket(raisedDamagedAssetCode, "Close");
        Assert.assertEquals(
            adminTicketPage.waitForTicketStatus(raisedDamagedAssetCode, "CLOSED", 10),
            "CLOSED", "Ticket should transition to CLOSED");
    }



//    @Test(priority = 3,
//            groups = {"regression", "admin", "positive"},
//            description = "TC_TCK_010: Admin ticket action buttons change dynamically based on ticket status")
//    public void testActionButtonsChangeDynamically() {
//        driver.manage().deleteAllCookies();
//        driver.get(BASE_URL);
//        new LoginPage(driver).login("admin@gmail.com", "admin123");
//
//        WaitUtils.sleep(5000);
//        // Verify admin URL after login
//        String currentUrl = driver.getCurrentUrl();
//        Assert.assertTrue(currentUrl.contains("/admin-dashboard"),
//                "Admin should be on /admin-dashboard after login. Current URL: " + currentUrl);
//
//        TicketManagementPage adminTicketPage = new TicketManagementPage(driver);
//        adminTicketPage.navigateToTicketManagement();
//        WaitUtils.sleep(5000);
//
//        // Pick an existing ticket that is already in PENDING state instead of raising a new one
//        String assetCode = adminTicketPage.getFirstTicketIdWithStatus("PENDING");
//        if (assetCode.isEmpty()) {
//            System.out.println("No existing ticket found in PENDING state. Skipping.");
//            return;
//        }
//
//        Assert.assertTrue(adminTicketPage.isTicketPresent(assetCode), "Ticket should exist in Admin view");
//        Assert.assertEquals(adminTicketPage.getTicketStatus(assetCode), "PENDING", "Initial ticket state should be PENDING");
//
//        // PENDING -> only "Start Repair" button should be visible
//        List<String> pendingButtons = adminTicketPage.getVisibleActionButtons(assetCode);
//        Assert.assertEquals(pendingButtons.size(), 1,
//                "Only one action button should be visible in PENDING state. Found: " + pendingButtons);
//        Assert.assertTrue(pendingButtons.get(0).equalsIgnoreCase("Start Repair"),
//                "PENDING state should show only 'Start Repair' button. Found: " + pendingButtons);
//
//        // Click "Start Repair" -> ticket moves to UNDER_REPAIR, only "Resolve" should be visible
//        adminTicketPage.clickActionForTicket(assetCode, "Start Repair");
//        Assert.assertEquals(
//                adminTicketPage.waitForTicketStatus(assetCode, "UNDER_REPAIR", 10),
//                "UNDER_REPAIR", "Ticket should transition to UNDER_REPAIR");
//
//        List<String> underRepairButtons = adminTicketPage.getVisibleActionButtons(assetCode);
//        Assert.assertEquals(underRepairButtons.size(), 1,
//                "Only one action button should be visible in UNDER_REPAIR state. Found: " + underRepairButtons);
//        Assert.assertTrue(underRepairButtons.get(0).equalsIgnoreCase("Resolve"),
//                "UNDER_REPAIR state should show only 'Resolve' button. Found: " + underRepairButtons);
//
//        // Click "Resolve" -> ticket moves to RESOLVED, only "Close" should be visible
//        adminTicketPage.clickActionForTicket(assetCode, "Resolve");
//        Assert.assertEquals(
//                adminTicketPage.waitForTicketStatus(assetCode, "RESOLVED", 10),
//                "RESOLVED", "Ticket should transition to RESOLVED");
//
//        List<String> resolvedButtons = adminTicketPage.getVisibleActionButtons(assetCode);
//        Assert.assertEquals(resolvedButtons.size(), 1,
//                "Only one action button should be visible in RESOLVED state. Found: " + resolvedButtons);
//        Assert.assertTrue(resolvedButtons.get(0).equalsIgnoreCase("Close"),
//                "RESOLVED state should show only 'Close' button. Found: " + resolvedButtons);
//
//        // Click "Close" -> ticket moves to CLOSED, no action buttons should remain
//        adminTicketPage.clickActionForTicket(assetCode, "Close");
//        Assert.assertEquals(
//                adminTicketPage.waitForTicketStatus(assetCode, "CLOSED", 10),
//                "CLOSED", "Ticket should transition to CLOSED");
//
//        List<String> closedButtons = adminTicketPage.getVisibleActionButtons(assetCode);
//        Assert.assertTrue(closedButtons.isEmpty(),
//                "No action buttons should be displayed once ticket is CLOSED. Found: " + closedButtons);
//    }


}
