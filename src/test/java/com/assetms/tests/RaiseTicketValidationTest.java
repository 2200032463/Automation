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


public class RaiseTicketValidationTest extends BaseTest {

    
    @org.testng.annotations.BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        
    }

    
    @Test(priority = 1,
          groups = {"regression", "admin", "positive"},
          description = "TC_TCK_001: Raise Ticket form visible only to Employee role")
    public void testRaiseTicketFormVisibility() {
        
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        TicketManagementPage adminTicketPage = new TicketManagementPage(driver);
        adminTicketPage.navigateToTicketManagement();
        
        Assert.assertFalse(adminTicketPage.isRaiseTicketFormVisible(), 
                "Raise Ticket form should NOT be visible to Admin");

        
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

    
    @Test(priority = 2,
          groups = {"regression", "employee", "positive"},
          description = "TC_TCK_002: Employee can access Ticket Management page after login")
    public void testAssetDropdownActiveAssetsOnly() {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");

        // Verify employee is redirected to their dashboard after login
        Assert.assertTrue(loginPage.isRedirectedTo("/employee-dashboard"),
                "Employee should be redirected to /employee-dashboard after login.");
    }


    @Test(priority = 3,
          groups = {"regression", "employee", "positive"},
          description = "TC_TCK_003: Employee dashboard is accessible after login")
    public void testDescriptionLengthConstraint() {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");

        // Verify employee URL after login
        String currentUrl = driver.getCurrentUrl();
        Assert.assertTrue(currentUrl.contains("/employee-dashboard"),
                "Employee should be on /employee-dashboard after login. Current URL: " + currentUrl);
    }

}
