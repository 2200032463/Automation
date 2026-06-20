package com.assetms.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * WaitUtils – Centralises all explicit wait helpers to keep Page Objects clean.
 */
public class WaitUtils {

    private static final int DEFAULT_TIMEOUT = 100; // seconds

    /** Returns a new WebDriverWait with the default timeout. */
    public static WebDriverWait wait(WebDriver driver) {
        return new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT));
    }

    /** Waits until the element is visible and returns it. */
    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Waits until the element is clickable and returns it. */
    public static WebElement waitForClickable(WebDriver driver, By locator) {
        return wait(driver).until(ExpectedConditions.elementToBeClickable(locator));
    }

    /** Waits until the URL contains the given fragment. */
    public static boolean waitForUrlContains(WebDriver driver, String urlFragment) {
        return wait(driver).until(ExpectedConditions.urlContains(urlFragment));
    }

    /** Waits until the element text is not empty. */
    public static String waitForText(WebDriver driver, By locator) {
        WebElement el = waitForVisible(driver, locator);
        new WebDriverWait(driver, Duration.ofSeconds(DEFAULT_TIMEOUT))
                .until(d -> !el.getText().isEmpty());
        return el.getText();
    }

    /**
     * Safe click: waits for clickability then clicks.
     */
    public static void click(WebDriver driver, By locator) {
        waitForClickable(driver, locator).click();
    }

    /**
     * Safe sendKeys: clears the field, waits for visibility, then types.
     */
    public static void type(WebDriver driver, By locator, String text) {
        WebElement el = waitForVisible(driver, locator);
        el.clear();
        el.sendKeys(text);
    }

    /**
     * Checks whether an element is present in the DOM at all (non-blocking).
     */
    public static boolean isPresent(WebDriver driver, By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * Short pause for UI settling. Use sparingly – prefer explicit waits.
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
