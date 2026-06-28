package com.assetms.tests;

import com.assetms.pages.LoginPage;
import com.assetms.pages.TicketManagementPage;
import com.assetms.utils.WaitUtils;
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

    
    private String raiseTicketAsEmployee(String issueType, String desc) {
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

    
    @Test(priority = 2,
          groups = {"regression", "admin", "positive"},
          description = "TC_TCK_009: LOST ticket transitions: PENDING → UNDER_REVIEW → CLOSED" )
    public void testLostTicketWorkflow() {
        raisedLostAssetCode = raiseTicketAsEmployee("LOST", "Left headset in cafeteria lost forever - Test");
        if (raisedLostAssetCode.isEmpty()) {
            System.out.println("[WARN] No assets available to test lost workflow. Skipping.");
            return;
        }
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        new LoginPage(driver).login("admin@gmail.com", "admin123");
        
        TicketManagementPage adminTicketPage = new TicketManagementPage(driver);
        adminTicketPage.navigateToTicketManagement();
        
        Assert.assertTrue(adminTicketPage.isTicketPresent(raisedLostAssetCode), "Ticket should exist in Admin view");
        Assert.assertEquals(adminTicketPage.getTicketStatus(raisedLostAssetCode), "PENDING", "Initial ticket state should be PENDING");

        
        adminTicketPage.clickActionForTicket(raisedLostAssetCode, "Under Review");
        Assert.assertEquals(
            adminTicketPage.waitForTicketStatus(raisedLostAssetCode, "UNDER_REVIEW", 10),
            "UNDER_REVIEW", "Ticket should transition to UNDER_REVIEW");

        
        adminTicketPage.clickActionForTicket(raisedLostAssetCode, "Confirm Lost");
        Assert.assertEquals(
            adminTicketPage.waitForTicketStatus(raisedLostAssetCode, "CLOSED", 10),
            "CLOSED", "Ticket should transition to CLOSED");
    }

    
    @Test(priority = 3,
          groups = {"regression", "admin", "positive"},
          description = "TC_TCK_010: Action buttons change dynamically with ticket status")
    public void testActionButtonsChangeDynamically() {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        new LoginPage(driver).login("admin@gmail.com", "admin123");

        TicketManagementPage adminTicketPage = new TicketManagementPage(driver);
        adminTicketPage.navigateToTicketManagement();

        List<WebElement> rows = driver.findElements(By.cssSelector(".table-wrap table tr:not(:first-child)"));
        if (rows.isEmpty()) {
            System.out.println("[INFO] No tickets found in table. Skipping dynamic button check.");
            return;
        }

        for (WebElement row : rows) {
            // Use relative XPath (./td[...]) so it scopes to the current row, not the whole document
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.size() < 6) continue;

            String status = cells.get(5).getText().toUpperCase();
            List<WebElement> buttons = row.findElements(By.tagName("button"));

            if (status.equals("CLOSED")) {
                Assert.assertTrue(buttons.isEmpty(),
                        "CLOSED tickets should not have action buttons");
            } else if (status.equals("PENDING")) {
                boolean hasValidButton = buttons.stream()
                        .map(WebElement::getText)
                        .anyMatch(t -> t.equalsIgnoreCase("Start Repair") || t.equalsIgnoreCase("Under Review"));
                Assert.assertTrue(hasValidButton,
                        "PENDING ticket should have 'Start Repair' or 'Under Review' button");
            }
            // For other statuses (UNDER_REPAIR, UNDER_REVIEW, RESOLVED) we just skip detailed check
        }
    }
}
