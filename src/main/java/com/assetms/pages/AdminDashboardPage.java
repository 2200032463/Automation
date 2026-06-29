package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AdminDashboardPage {

    private final WebDriver driver;

    
    private final By navDashboard = By.xpath("/html/body/app-root/div/aside/nav/a[1]");
    
    private final By pageHeading = By.xpath("//h2[normalize-space(text())='Admin Dashboard']");

    
    
    private final By statCards = By.cssSelector(".card.stat");

    
    private final By totalEmployeesValue = By.xpath("//div[@class='card stat'][.//h3[text()='Total Employees']]//p");
    private final By totalAssetsValue    = By.xpath("//div[@class='card stat'][.//h3[text()='Total Assets']]//p");
    private final By assignedAssetsValue = By.xpath("//div[@class='card stat'][.//h3[text()='Assigned Assets']]//p");
    private final By availableAssetsValue= By.xpath("//div[@class='card stat'][.//h3[text()='Available Assets']]//p");
    private final By overdueAssetsValue  = By.xpath("//div[@class='card stat'][.//h3[text()='Overdue Assets']]//p");
    private final By lostAssetsValue     = By.xpath("//div[@class='card stat'][.//h3[text()='Lost Assets']]//p");
    private final By openTicketsValue    = By.xpath("//div[@class='card stat'][.//h3[text()='Open Tickets']]//p");

    
    private final By recentAllocationsRows = By.xpath("//h3[text()='Recent Allocations']/following-sibling::div//table//tr[position()>1]");
    private final By overdueListRows       = By.xpath("//h3[text()='Overdue Assets']/following-sibling::div//table//tr[position()>1]");
    private final By openTicketRows        = By.xpath("//h3[text()='Open Tickets']/following-sibling::div//table//tr[position()>1]");

    
    public AdminDashboardPage(WebDriver driver) {
        this.driver = driver;
    }

    

    public void navigateToDashboard() {
        WaitUtils.click(driver, navDashboard);
        WaitUtils.waitForVisible(driver, pageHeading);
    }


    public String getTotalEmployees() {
        return WaitUtils.waitForVisible(driver, totalEmployeesValue).getText();
    }

    public String getTotalAssets() {
        return WaitUtils.waitForVisible(driver, totalAssetsValue).getText();
    }

    public String getAssignedAssets() {
        return WaitUtils.waitForVisible(driver, assignedAssetsValue).getText();
    }

    public String getAvailableAssets() {
        return WaitUtils.waitForVisible(driver, availableAssetsValue).getText();
    }

    public String getOverdueAssets() {
        return WaitUtils.waitForVisible(driver, overdueAssetsValue).getText();
    }

    public String getLostAssets() {
        return WaitUtils.waitForVisible(driver, lostAssetsValue).getText();
    }

    public String getOpenTickets() {
        return WaitUtils.waitForVisible(driver, openTicketsValue).getText();
    }

    
    public int getStatCardCount() {
        return driver.findElements(statCards).size();
    }

    

    public int getRecentAllocationRowCount() {
        return driver.findElements(recentAllocationsRows).size();
    }

    public int getOverdueListRowCount() {
        return driver.findElements(overdueListRows).size();
    }

    public int getOpenTicketRowCount() {
        return driver.findElements(openTicketRows).size();
    }

    
    public boolean areAllStatCardsVisible() {
        return getStatCardCount() == 7;
    }

    
    public boolean isStatValueNumeric(String statValue) {
        try {
            return Integer.parseInt(statValue.trim()) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
