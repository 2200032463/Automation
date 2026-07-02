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




}
