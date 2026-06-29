package com.assetms.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.*;

import java.time.Duration;

@Listeners(MyListener.class)
public class BaseTest {

    protected WebDriver driver;
    protected final String BASE_URL = "https://assets-management-sandy.vercel.app/";
    @BeforeClass(alwaysRun = true)
    @Parameters(value = {"browser"})
    public void setUp(@Optional("chrome") String browser) {
        if (browser == null) browser = "chrome";

        if (browser.equalsIgnoreCase("edge")) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080", "--disable-notifications");
            driver = new EdgeDriver(options);
        } else {
            
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");

            driver = new ChromeDriver(options);
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(180));
        driver.get(BASE_URL);
    }

    
    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }
}