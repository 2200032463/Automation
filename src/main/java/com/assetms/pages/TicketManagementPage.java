package com.assetms.pages;

import com.assetms.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;


public class TicketManagementPage {

    private final WebDriver driver;

    
    private final By navTicketManagement =
            By.xpath("//span[normalize-space()='Ticket Management']");

    
    private final By pageHeading = By.xpath("/html/body/app-root/div/main/div/app-ticket-management/section/h2");

    
    private final By raiseTicketSection = By.xpath("//h3[normalize-space(text())='Raise Ticket']");
    private final By assetIdSelect      = By.cssSelector("select[formcontrolname='assetId']");
    private final By issueTypeSelect    = By.cssSelector("select[formcontrolname='issueType']");
    private final By issueDescription   = By.cssSelector("textarea[formcontrolname='issueDescription']");
    private final By createTicketButton = By.cssSelector("button[type='submit']");
    private final By formMessage        = By.cssSelector("p.info");

    
    private final By tableRows = By.cssSelector(".table-wrap table tr:not(:first-child)");

    
    public TicketManagementPage(WebDriver driver) {
        this.driver = driver;
    }

    

    public void navigateToTicketManagement() {
        WaitUtils.click(driver, navTicketManagement);
        WaitUtils.waitForVisible(driver, pageHeading);
        
        
        try {
            WaitUtils.waitForCondition(driver,
                d -> !d.findElements(tableRows).isEmpty(), 10);
        } catch (Exception ignored) {
            
        }
    }

    

    public String getPageHeading() {
        return WaitUtils.waitForVisible(driver, pageHeading).getText();
    }

    public boolean isPageVisible() {
        return WaitUtils.isPresent(driver, pageHeading);
    }

    public boolean isRaiseTicketFormVisible() {
        return WaitUtils.isPresent(driver, raiseTicketSection);
    }

    

    
    public void selectAsset(String assetText) {
        driver.findElement(assetIdSelect).click();
        Select select;
        WaitUtils.waitForCondition(driver,
            d -> new Select(d.findElement(assetIdSelect)).getOptions().size() > 1, 10);
        select = new Select(driver.findElement(assetIdSelect));

        if (assetText != null && !assetText.isEmpty()) {
            for (var option : select.getOptions()) {
                if (option.getText().contains(assetText)) {
                    option.click();
                    return;
                }
            }
        }
        
        if (select.getOptions().size() > 1) {
            select.selectByIndex(1);
        }
    }
    public void selectAssetdynamic() {
        Select select = new Select(WaitUtils.waitForVisible(driver, assetIdSelect));
        select.selectByIndex(1);
    }

    
    public void selectIssueType(String issueType) {
        new Select(WaitUtils.waitForVisible(driver, issueTypeSelect)).selectByValue(issueType);
    }

    
    public void enterIssueDescription(String description) {
        WaitUtils.type(driver, issueDescription, description);
    }

    
    public void clickCreateTicket() {
        WaitUtils.click(driver, createTicketButton);
    }

    
    public void raiseTicket(String assetText, String issueType, String description) {
        selectAsset(assetText);
        selectIssueType(issueType);
        enterIssueDescription(description);
        clickCreateTicket();
    }

    
    public String getFormMessage() {
        return WaitUtils.waitForText(driver, formMessage);
    }

    

    
    public void clickActionForTicket(String ticketId, String buttonText) {
        
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(ticketId)) {
                List<WebElement> buttons = row.findElements(By.tagName("button"));
                for (WebElement btn : buttons) {
                    if (btn.getText().equalsIgnoreCase(buttonText)) {
                        
                        
                        ((org.openqa.selenium.JavascriptExecutor) driver)
                            .executeScript("arguments[0].scrollIntoView({block:'center'});", btn);
                        WaitUtils.waitForCondition(driver,
                            d -> btn.isDisplayed() && btn.isEnabled(), 5);
                        btn.click();
                        return;
                    }
                }
                throw new RuntimeException(
                    "Action button '" + buttonText + "' not found in row for ticket: " + ticketId +
                    ". Available buttons: " + buttons.stream()
                        .map(WebElement::getText).toList());
            }
        }
        throw new RuntimeException("Ticket '" + ticketId + "' not found in the table");
    }

    

    
    public int getTicketRowCount() {
        return driver.findElements(tableRows).size();
    }

    
    public boolean isTicketPresent(String text) {
        
        try {
            WaitUtils.waitForCondition(driver,
                d -> !d.findElements(tableRows).isEmpty(), 10);
        } catch (Exception ignored) {}
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().toLowerCase().contains(text.toLowerCase())) return true;
        }
        return false;
    }

    
    public String getTicketStatus(String ticketId) {
        List<WebElement> rows = driver.findElements(tableRows);
        for (WebElement row : rows) {
            if (row.getText().contains(ticketId)) {
                List<WebElement> cells = row.findElements(By.tagName("td"));
                if (cells.size() > 5) {
                    return cells.get(5).getText();
                }
            }
        }
        return "";
    }

    
    public String waitForTicketStatus(String ticketId, String expectedStatus, int timeoutSeconds) {
        WaitUtils.waitForCondition(driver, d -> {
            List<WebElement> rows = d.findElements(tableRows);
            for (WebElement row : rows) {
                if (row.getText().contains(ticketId)) {
                    List<WebElement> cells = row.findElements(By.tagName("td"));
                    if (cells.size() > 5) {
                        return expectedStatus.equalsIgnoreCase(cells.get(5).getText());
                    }
                }
            }
            return false;
        }, timeoutSeconds);
        return getTicketStatus(ticketId);
    }

}