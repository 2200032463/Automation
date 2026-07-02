package com.assetms.tests;

import com.assetms.pages.LoginPage;
import com.assetms.pages.TicketManagementPage;
import com.assetms.utils.WaitUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.time.Duration;
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

 }


    @Test(priority = 2,
            groups = {"regression", "employee", "positive"},
            description = "TC_TCK_002: Employee can access Ticket Management page after login")
    public void testRaiseTicketFormVisibilityForEmployee() {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);


        LoginPage loginPageEmp = new LoginPage(driver);
        loginPageEmp.login("john.carter@company.com", "john123");

        WaitUtils.waitForAngularBootstrapped(driver);

        Assert.assertTrue(loginPageEmp.isRedirectedTo("/employee-dashboard"), "Login redirection failed");
        TicketManagementPage empTicketPage = new TicketManagementPage(driver);

        empTicketPage.navigateToTicketManagement();

        Assert.assertTrue(empTicketPage.isRaiseTicketFormVisible(),
                "Raise Ticket form should be visible to Employee");

    }


    @Test(priority = 3,
            groups = {"regression", "employee", "positive"},
            description = "TC_TCK_003: Employee dashboard is accessible after login")
    public void testDescriptionLengthConstraint() throws InterruptedException {
        driver.manage().deleteAllCookies();
        driver.get(BASE_URL);

        LoginPage loginPageEmp = new LoginPage(driver);
        loginPageEmp.login("john.carter@company.com", "john123");
        WaitUtils.waitForAngularBootstrapped(driver);


        Assert.assertTrue(loginPageEmp.isRedirectedTo("/employee-dashboard"), "Login redirection failed");

        TicketManagementPage empTicketPage = new TicketManagementPage(driver);
        empTicketPage.navigateToTicketManagement();
        Assert.assertTrue(empTicketPage.isRaiseTicketFormVisible(),
                "Raise Ticket form should be visible to Employee");

       Thread.sleep(40000);

        By assetIdSelect = By.cssSelector("select[formcontrolname='assetId']");
        try {
            WaitUtils.waitForCondition(driver,
                    d -> new Select(d.findElement(assetIdSelect)).getOptions().size() > 1, 10);
        } catch (Exception e) {
            System.out.println("No assets available to test description validation. Skipping.");
            return;
        }

        int ticketCountBefore = empTicketPage.getTicketRowCount();

        // Select an asset and issue type so the description field is the only invalid input
        empTicketPage.selectAssetdynamic();
        empTicketPage.selectIssueType(new Select(driver.findElement(
                By.cssSelector("select[formcontrolname='issueType']")))
                .getOptions().get(1).getAttribute("value"));

        String shortDescription = "short"; // 5 characters, below the 10 character minimum
        empTicketPage.enterIssueDescription(shortDescription);
        empTicketPage.clickCreateTicket();

        boolean errorShown = empTicketPage.isDescriptionErrorVisible();
        //boolean buttonDisabled = !empTicketPage.isCreateTicketButtonEnabled();

        Assert.assertTrue(errorShown ,
                "A description under 10 characters should either show a validation message " +
                        "or keep the Create Ticket button disabled");
        if (errorShown) {
            String errorText = empTicketPage.getDescriptionErrorText();
            Assert.assertTrue(errorText.toLowerCase().contains("10")
                            || errorText.toLowerCase().contains("at least"),
                    "Validation message should indicate a minimum of 10 characters. Actual: " + errorText);
        }

        int ticketCountAfter = empTicketPage.getTicketRowCount();
        Assert.assertEquals(ticketCountAfter, ticketCountBefore,
                "No ticket should be created when description is less than 10 characters");



}
}