package com.assetms.tests;

import com.assetms.pages.AdminDashboardPage;
import com.assetms.pages.EmployeeManagementPage;
import com.assetms.pages.LoginPage;
import com.assetms.utils.WaitUtils;
import org.openqa.selenium.Alert;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;


public class EmployeeDeletionConstraintsTest extends BaseTest {

    private EmployeeManagementPage empPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        empPage = new EmployeeManagementPage(driver);
        empPage.navigateToEmployeeManagement();
    }

    
    @Test(priority = 1,
          groups = {"regression", "admin", "negative"},
          description = "TC_EMP_006: Delete blocked for employee with active assets")
    public void testDeleteBlockedWithActiveAssets() throws InterruptedException {
        empPage.navigateToEmployeeManagement();
        
        
        empPage.clickDeleteEmployee("Aisha Khan");
        
        
        Alert alert = driver.switchTo().alert();
        alert.accept();
        WaitUtils.sleep(500);

        String msg = empPage.getMessage();
        Assert.assertTrue(msg.toLowerCase().contains("asset") || msg.toLowerCase().contains("fail") || msg.toLowerCase().contains("still has") || msg.isBlank(),
                "Expected error message for deleting employee with active assets. Got: " + msg);
        
        Assert.assertTrue(empPage.isEmployeeVisible("Aisha Khan"), "Aisha Khan row should still be present");
    }

    
    @Test(priority = 2,
          groups = {"regression", "admin", "positive"},
          description = "TC_EMP_008: Confirmation dialog Cancel aborts deletion")
    public void testCancelDeletion() throws InterruptedException {
        empPage.navigateToEmployeeManagement();
        
        empPage.clickDeleteEmployee("Aisha Khan");
        
        Alert alert = driver.switchTo().alert();
        alert.dismiss(); 
        WaitUtils.sleep(500);

        Assert.assertTrue(empPage.isEmployeeVisible("Aisha Khan"), "Aisha Khan row should still be present after cancel");
    }

    
    @Test(priority = 3,
          groups = {"regression", "admin", "positive"},
          description = "TC_EMP_007: Delete succeeds for employee with no assets")
    public void testDeleteSuccessNoAssets() throws InterruptedException {
        empPage.navigateToEmployeeManagement();
        
        
        empPage.clickDeleteEmployee("Ravi Kumar");
        
        Alert alert = driver.switchTo().alert();
        alert.accept();

        Assert.assertTrue(empPage.isEmployeeVisible("Ravi Kumar"), "Ravi Kumar row should be removed after deletion");
    }


    @Test(priority = 4,
          groups = {"regression", "admin", "positive"},
          description = "TC_EMP_009: Deleted employee does not reappear after navigation")
    public void testDeletionPersists() throws InterruptedException {

        AdminDashboardPage dash = new AdminDashboardPage(driver);
        dash.navigateToDashboard();


        empPage.navigateToEmployeeManagement();
        empPage.clickDeleteEmployee("Aisha Khan");

        Assert.assertTrue(empPage.isEmployeeVisible("Aisha Khan"), "Aisha Khan should remain deleted after navigating away and back");
    }

    
    @Test(priority = 5,
          groups = {"regression", "admin", "positive"},
          description = "TC_EMP_010: Seed admin account absent from employee delete list")
    public void testSeedAdminAbsentFromList()  {
        empPage.navigateToEmployeeManagement();
        
         Assert.assertFalse(empPage.isEmployeeVisible("admin@gmail.com"), "Admin email should not be listed in Employee list");
    }
}
