package com.assetms.tests;

import com.assetms.pages.EmployeeManagementPage;
import com.assetms.pages.LoginPage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * TS_EMP_001: Employee Grid & Search
 */
public class EmployeeGridSearchTest extends BaseTest {

    private EmployeeManagementPage empPage;

    @BeforeClass(alwaysRun = true, dependsOnMethods = "setUp")
    public void loginAndNavigate() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login("admin@gmail.com", "admin123");
        Assert.assertTrue(loginPage.isRedirectedTo("/admin-dashboard"), "Login redirection failed");
        empPage = new EmployeeManagementPage(driver);
        empPage.navigateToEmployeeManagement();
    }

    // ── TC_EMP_001 ──────────────────────────────────────────────────────────────
    @Test(priority = 1,
          groups = {"sanity", "regression", "admin", "positive"},
          description = "TC_EMP_001: Employee table displays correct columns")
    public void testEmployeeTableColumns() {
        empPage.navigateToEmployeeManagement();
        
        List<WebElement> headers = driver.findElements(By.cssSelector("table th"));
        Assert.assertTrue(headers.size() >= 6, "Expected at least 6 column headers");

        String hText = "";
        for (WebElement h : headers) {
            hText += h.getText() + " | ";
        }

        Assert.assertTrue(hText.contains("Employee ID"), "Expected Employee ID column");
        Assert.assertTrue(hText.contains("Name"), "Expected Name column");
        Assert.assertTrue(hText.contains("Department"), "Expected Department column");
        Assert.assertTrue(hText.contains("Role"), "Expected Role column");
        Assert.assertTrue(hText.contains("Email"), "Expected Email column");
        Assert.assertTrue(hText.contains("Actions"), "Expected Actions column");
    }

    // ── TC_EMP_002 ──────────────────────────────────────────────────────────────
    @Test(priority = 2,
          groups = {"regression", "admin", "positive"},
          description = "TC_EMP_002: Search by employee Name filters correctly")
    public void testSearchByName() {
        empPage.navigateToEmployeeManagement();
        empPage.searchEmployee("john");
        
        Assert.assertTrue(empPage.isEmployeeVisible("John Carter"), "John Carter should be visible");
        Assert.assertFalse(empPage.isEmployeeVisible("Aisha Khan"), "Aisha Khan should not be visible");
        
        empPage.clearSearch();
    }

    // ── TC_EMP_003 ──────────────────────────────────────────────────────────────
    @Test(priority = 3,
          groups = {"regression", "admin", "positive"},
          description = "TC_EMP_003: Search by Employee ID filters correctly")
    public void testSearchById() {
        empPage.navigateToEmployeeManagement();
        empPage.searchEmployee("EMP1002");
        
        Assert.assertTrue(empPage.isEmployeeVisible("Aisha Khan"), "Aisha Khan (EMP1002) should be visible");
        Assert.assertFalse(empPage.isEmployeeVisible("John Carter"), "John Carter should not be visible");
        
        empPage.clearSearch();
    }

    // ── TC_EMP_004 ──────────────────────────────────────────────────────────────
    @Test(priority = 4,
          groups = {"regression", "admin", "positive"},
          description = "TC_EMP_004: Search by Role filters correctly")
    public void testSearchByRole() {
        empPage.navigateToEmployeeManagement();
        // Ravi Kumar is Support Analyst
        empPage.searchEmployee("Support");
        
        Assert.assertTrue(empPage.isEmployeeVisible("Ravi Kumar"), "Ravi Kumar (Support Analyst) should be visible");
        Assert.assertFalse(empPage.isEmployeeVisible("John Carter"), "John Carter should not be visible");
        
        empPage.clearSearch();
    }

    // ── TC_EMP_005 ──────────────────────────────────────────────────────────────
    @Test(priority = 5,
          groups = {"regression", "admin", "negative"},
          description = "TC_EMP_005: No-match search shows empty table without errors")
    public void testSearchNoMatch() {
        empPage.navigateToEmployeeManagement();
        empPage.searchEmployee("XYZ999");
        
        int rowCount = empPage.getEmployeeTableRowCount();
        Assert.assertEquals(rowCount, 0, "Table should be empty for non-matching search");
        
        empPage.clearSearch();
    }
}
