package com.assetms.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class WaitUtils {

    private static final int DEFAULT_TIMEOUT = 180; 

    

    
    public static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    
    public static WebDriverWait wait(WebDriver driver, int timeoutSeconds) {
        return new WebDriverWait(driver, Duration.ofSeconds(timeoutSeconds));
    }

    

    
    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    
    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    public static boolean isVisible(WebDriver driver, By locator) {
        try {
            return driver.findElement(locator).isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    
    public static boolean waitForUrlContains(WebDriver driver, String urlFragment) {
        return wait(driver).until(ExpectedConditions.urlContains(urlFragment));
    }

    

    
    public static String waitForText(WebDriver driver, By locator) {
        WebElement el = waitForVisible(driver, locator);
        wait(driver).until(d -> !el.getText().isEmpty());
        return el.getText();
    }



    
    public static String waitForNonBlankText(WebDriver driver, By locator) {
        wait(driver).until(d -> {
            try {
                String t = d.findElement(locator).getText();
                return t != null && !t.isBlank();
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
        return driver.findElement(locator).getText();
    }

    

    
    public static <T> T waitForCondition(WebDriver driver, ExpectedCondition<T> condition) {
        return wait(driver).until(condition);
    }

    
    public static <T> T waitForCondition(WebDriver driver, ExpectedCondition<T> condition, int timeoutSeconds) {
        return wait(driver, timeoutSeconds).until(condition);
    }

    

    
    public static void click(WebDriver driver, By locator) {
        waitForClickable(driver, locator).click();
    }

    
    public static void type(WebDriver driver, By locator, String text) {
        WebElement el = waitForClickable(driver, locator);
        el.click();
        el.clear();
        el.sendKeys(Keys.DELETE);
        if (text != null && !text.isEmpty()) {
            el.sendKeys(text);
        }
    }

    
    public static void waitForAngularBootstrapped(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
            try {
                Object res = ((JavascriptExecutor) d).executeScript(
                    "return document.querySelector('app-root') && document.querySelector('app-root').children.length > 0"
                );
                return res != null && (Boolean) res;
            } catch (Exception e) {
                return false;
            }
        });
    }

    

    
    public static boolean isPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}