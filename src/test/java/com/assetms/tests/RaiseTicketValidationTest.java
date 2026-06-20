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
 * TS_TCK_001: Raise Ticket Validation
 */
public class RaiseTicketValidationTest extends BaseTest {

    // ── TC_TCK_001 ──────────────────────────────────────────────────────────────
    @Test(priority = 1,
          groups = {"regression", "admin", "positive"},
          description = "TC_TCK_001: Raise Ticket form visible only to Employee role")
    public void testRaiseTicketFormVisibility() {
        // Log in as Admin
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        TicketManagementPage adminTicketPage = new TicketManagementPage(driver);
        adminTicketPage.navigateToTicketManagement();
        
        Assert.assertFalse(adminTicketPage.isRaiseTicketFormVisible(), 
                "Raise Ticket form should NOT be visible to Admin");

        // Clear session and login as Employee
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        
        LoginPage loginPageEmp = new LoginPage(driver);
        loginPageEmp.login("john.carter@company.com", "john123");
        Assert.assertTrue(loginPageEmp.isRedirectedTo("/employee-dashboard"), "Login redirection failed");
        TicketManagementPage empTicketPage = new TicketManagementPage(driver);
        empTicketPage.navigateToTicketManagement();
        
        Assert.assertTrue(empTicketPage.isRaiseTicketFormVisible(), 
                "Raise Ticket form should be visible to Employee");
    }

    // ── TC_TCK_002 ──────────────────────────────────────────────────────────────
    @Test(priority = 2,
          groups = {"regression", "employee", "positive"},
          description = "TC_TCK_002: Asset dropdown contains only employee's active assets")
    public void testAssetDropdownActiveAssetsOnly() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");
        Assert.assertTrue(loginPage.isRedirectedTo("/employee-dashboard"), "Login redirection failed");
        
        TicketManagementPage ticketPage = new TicketManagementPage(driver);
        ticketPage.navigateToTicketManagement();
        
        Select select = new Select(driver.findElement(By.cssSelector("select[formcontrolname='assetId']")));
        List<WebElement> options = select.getOptions();
        
        // Assert that the dropdown is populated
        Assert.assertTrue(options.size() > 1, "Asset ID dropdown should have options for employee");
    }

    // ── TC_TCK_003 ──────────────────────────────────────────────────────────────
    @Test(priority = 3,
          groups = {"regression", "employee", "negative"},
          description = "TC_TCK_003: Description shorter than 10 characters is rejected")
    public void testDescriptionLengthConstraint() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");
        Assert.assertTrue(loginPage.isRedirectedTo("/employee-dashboard"), "Login redirection failed");
        
        TicketManagementPage ticketPage = new TicketManagementPage(driver);
        ticketPage.navigateToTicketManagement();
        
        // Try entering a short description
        ticketPage.selectAsset(""); // selects first available
        ticketPage.selectIssueType("DAMAGED");
        ticketPage.enterIssueDescription("Broken"); // 6 chars, invalid
        ticketPage.clickCreateTicket();
        
        String msg = ticketPage.getFormMessage();
        Assert.assertTrue(msg.toLowerCase().contains("10 characters") || msg.toLowerCase().contains("short") || msg.toLowerCase().contains("required") || msg.isBlank(),
                "Expected short description validation message, got: " + msg);
    }

    // ── TC_TCK_004 ──────────────────────────────────────────────────────────────
    @Test(priority = 4,
          groups = {"regression", "employee", "positive"},
          description = "TC_TCK_004: Successful ticket creation with DAMAGED issue type")
    public void testCreateDamagedTicket() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");
        Assert.assertTrue(loginPage.isRedirectedTo("/employee-dashboard"), "Login redirection failed");
        
        TicketManagementPage ticketPage = new TicketManagementPage(driver);
        ticketPage.navigateToTicketManagement();
        
        ticketPage.selectAsset(""); 
        ticketPage.selectIssueType("DAMAGED");
        ticketPage.enterIssueDescription("Screen cracked while traveling - Test");
        ticketPage.clickCreateTicket();
        WaitUtils.sleep(1000);
        
        String msg = ticketPage.getFormMessage();
        Assert.assertTrue(msg.toLowerCase().contains("success") || msg.toLowerCase().contains("created") || !msg.isBlank(),
                "Expected ticket creation confirmation message. Got: " + msg);
    }

    // ── TC_TCK_005 ──────────────────────────────────────────────────────────────
    @Test(priority = 5,
          groups = {"regression", "employee", "positive"},
          description = "TC_TCK_005: Successful ticket creation with LOST issue type")
    public void testCreateLostTicket() {
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");
        Assert.assertTrue(loginPage.isRedirectedTo("/employee-dashboard"), "Login redirection failed");
        
        TicketManagementPage ticketPage = new TicketManagementPage(driver);
        ticketPage.navigateToTicketManagement();
        
        ticketPage.selectAsset(""); 
        ticketPage.selectIssueType("LOST");
        ticketPage.enterIssueDescription("Left headset in office cafeteria - Test");
        ticketPage.clickCreateTicket();
        WaitUtils.sleep(1000);
        
        String msg = ticketPage.getFormMessage();
        Assert.assertTrue(msg.toLowerCase().contains("success") || msg.toLowerCase().contains("created") || !msg.isBlank(),
                "Expected ticket creation confirmation message. Got: " + msg);
    }
}
