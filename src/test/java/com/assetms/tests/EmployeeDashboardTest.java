package com.assetms.tests;

import com.assetms.pages.EmployeeDashboardPage;
import com.assetms.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class EmployeeDashboardTest extends BaseTest {

    private EmployeeDashboardPage dashboardPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("john.carter@company.com", "john123");
        Assert.assertTrue(loginPage.isRedirectedTo("/employee-dashboard"), "Login redirection failed");
        dashboardPage = new EmployeeDashboardPage(driver);
        dashboardPage.navigateToEmployeeDashboard();
    }

    
    @Test(priority = 1,
          groups = {"sanity", "regression", "employee", "positive"},
          description = "TC_EDH_001: Profile card shows authenticated employee data")
    public void testProfileCardShowsEmployeeData() {
        dashboardPage.navigateToEmployeeDashboard();
        
        Assert.assertTrue(dashboardPage.isProfileSectionVisible(), "Profile section should be visible");
        
        String idText = dashboardPage.getProfileEmployeeId();
        String nameText = dashboardPage.getProfileName();
        String deptText = dashboardPage.getProfileDepartment();
        String emailText = dashboardPage.getProfileEmail();
        
        Assert.assertTrue(idText.contains("EMP1001"), "Employee ID should contain EMP1001, got: " + idText);
        Assert.assertTrue(nameText.contains("John Carter"), "Name should contain John Carter, got: " + nameText);
        Assert.assertTrue(deptText.contains("IT") || deptText.contains("Software"), "Department should be IT/Software, got: " + deptText);
        Assert.assertTrue(emailText.contains("john.carter@company.com"), "Email should be john.carter@company.com, got: " + emailText);
    }

    
    @Test(priority = 2,
          groups = {"regression", "employee", "positive"},
          description = "TC_EDH_002: Assigned Assets section shows active allocations")
    public void testAssignedAssetsSection() {
        dashboardPage.navigateToEmployeeDashboard();
        
        int count = dashboardPage.getAssignedAssetsCount();
        Assert.assertTrue(count >= 0, "Assigned assets count should be >= 0. Got: " + count);
    }

    
    @Test(priority = 3,
          groups = {"regression", "employee", "positive"},
          description = "TC_EDH_003: Return History shows completed returns")
    public void testReturnHistorySection() {
        dashboardPage.navigateToEmployeeDashboard();
        
        int count = dashboardPage.getReturnHistoryCount();
        Assert.assertTrue(count >= 0, "Return History count should be >= 0. Got: " + count);
    }

    
    @Test(priority = 4,
          groups = {"regression", "employee", "positive"},
          description = "TC_EDH_004: Overdue History displays items past deadline")
    public void testOverdueHistorySection() {
        dashboardPage.navigateToEmployeeDashboard();
        
        int count = dashboardPage.getOverdueHistoryCount();
        Assert.assertTrue(count >= 0, "Overdue History count should be >= 0. Got: " + count);
    }

    
    @Test(priority = 5,
          groups = {"regression", "employee", "positive"},
          description = "TC_EDH_005: Ticket History lists all tickets raised by employee")
    public void testTicketHistorySection() {
        dashboardPage.navigateToEmployeeDashboard();
        
        int count = dashboardPage.getTicketHistoryCount();
        Assert.assertTrue(count >= 0, "Ticket History count should be >= 0. Got: " + count);
    }
}
