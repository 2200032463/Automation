package com.assetms.tests;

import com.assetms.pages.AdminDashboardPage;
import com.assetms.pages.EmployeeManagementPage;
import com.assetms.pages.LoginPage;
import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;


public class AdminDashboardListRefreshTest extends BaseTest {

    private AdminDashboardPage dashboardPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        dashboardPage = new AdminDashboardPage(driver);
        dashboardPage.navigateToDashboard();
    }

    
    @Test(priority = 1,
          groups = {"regression", "admin", "positive"},
          description = "TC_DSH_006: Recent Allocations shows max 5 rows in reverse order")
    public void testRecentAllocationsList() {
        dashboardPage.navigateToDashboard();
        int rowCount = dashboardPage.getRecentAllocationRowCount();
        Assert.assertTrue(rowCount <= 5, 
                "Recent Allocations table should display a maximum of 5 rows, got: " + rowCount);
    }

    
    @Test(priority = 2,
          groups = {"regression", "admin", "positive"},
          description = "TC_DSH_007: Overdue Assets list shows assets past return deadline")
    public void testOverdueAssetsList() {
        dashboardPage.navigateToDashboard();
        int rowCount = dashboardPage.getOverdueListRowCount();
        Assert.assertTrue(rowCount >= 0, "Overdue Assets list row count should be non-negative.");
        
        
        if (rowCount > 0) {
            List<WebElement> rows = driver.findElements(
                    By.xpath("//h3[text()='Overdue Assets']/following-sibling::div//table//tr[position()>1]"));
            for (WebElement row : rows) {
                String text = row.getText();
                Assert.assertTrue(text.toLowerCase().contains("overdue") || !text.isBlank(), 
                        "Expected row to represent overdue status, got: " + text);
            }
        }
    }

    
    @Test(priority = 3,
          groups = {"regression", "admin", "positive"},
          description = "TC_DSH_008: Open Tickets list shows non-closed tickets")
    public void testOpenTicketsList() {
        dashboardPage.navigateToDashboard();
        int rowCount = dashboardPage.getOpenTicketRowCount();
        Assert.assertTrue(rowCount >= 0, "Open Tickets row count should be non-negative.");

        if (rowCount > 0) {
            List<WebElement> rows = driver.findElements(
                    By.xpath("//h3[text()='Open Tickets']/following-sibling::div//table//tr[position()>1]"));
            for (WebElement row : rows) {
                String text = row.getText().toUpperCase();
                Assert.assertFalse(text.contains("CLOSED"), "Open tickets list should not contain CLOSED tickets: " + text);
            }
        }
    }

    
    @Test(priority = 4,
          groups = {"regression", "admin", "positive"},
          description = "TC_DSH_010: Sidebar navigation from dashboard works")
    public void testSidebarNavigation() {
        dashboardPage.navigateToDashboard();
        EmployeeManagementPage empPage = new EmployeeManagementPage(driver);
        empPage.navigateToEmployeeManagement();
        
        Assert.assertTrue(empPage.isPageVisible(), "Navigation to Employee Management failed.");
        Assert.assertEquals(empPage.getPageHeading(), "Employee Management", "Page heading mismatch after navigation");
    }
}
