package com.assetms.tests;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.annotations.*;
import java.io.IOException;
import java.time.Duration;

@Listeners(MyListener.class)
public class BaseTest {
    protected WebDriver driver;
    // Vercel deployment URL
    protected final String BASE_URL = "https://assets-management-sandy.vercel.app/";

    @BeforeMethod
    @Parameters(value = {"browser"})
    public void setUp(@Optional("chrome") String browser) throws IOException {
        if (browser == null) browser = "chrome";

        if (browser.equalsIgnoreCase("chrome")) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            driver = new ChromeDriver(options);
        }
        else if (browser.equalsIgnoreCase("edge")) {
            EdgeOptions options = new EdgeOptions();
            options.addArguments("--start-maximized");
            options.addArguments("--disable-notifications");
            driver = new EdgeDriver(options);
        }

        // Apply environment settings uniformly to both browsers
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.get(BASE_URL);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}