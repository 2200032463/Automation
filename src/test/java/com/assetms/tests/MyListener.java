package com.assetms.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyListener implements ITestListener, IInvokedMethodListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testThreadLocal = new ThreadLocal<>();
    private static final String SCREENSHOT_DIR = "test-output/ExtentReports/screenshots";

    @Override
    public void onStart(ITestContext context) {
        System.out.println("My Listener started");
        if (extent == null) {
            extent = ExtentManager.getExtentReports();
        }
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("Test started: " + result.getName());
        String desc = result.getMethod().getDescription();
        ExtentTest extentTest = extent.createTest(result.getMethod().getMethodName(), desc);
        
        // Add groups as categories in ExtentReports
        String[] groups = result.getMethod().getGroups();
        if (groups != null) {
            for (String group : groups) {
                extentTest.assignCategory(group);
            }
        }
        testThreadLocal.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentTest t = testThreadLocal.get();
        if (t != null) {
            t.pass("Test passed successfully.");
        }
        testThreadLocal.remove();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest t = testThreadLocal.get();
        if (t != null) {
            t.fail(result.getThrowable());
            
            // Capture and attach a screenshot on failure
            Object testInstance = result.getInstance();
            if (testInstance instanceof BaseTest) {
                WebDriver driver = ((BaseTest) testInstance).driver;
                if (driver != null) {
                    try {
                        // Capture screenshot and save to file
                        String screenshotPath = captureScreenshot(driver, result.getMethod().getMethodName());
                        if (screenshotPath != null) {
                            t.addScreenCaptureFromPath(screenshotPath);
                            System.out.println("Screenshot captured on test failure: " + screenshotPath);
                        }
                    } catch (Exception e) {
                        System.err.println("Failed to capture screenshot: " + e.getMessage());
                        t.info("Failed to capture screenshot: " + e.getMessage());
                    }
                }
            }
        }
        testThreadLocal.remove();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentTest t = testThreadLocal.get();
        if (t != null) {
            if (result.getThrowable() != null) {
                t.skip(result.getThrowable());
            } else {
                t.skip("Test skipped.");
            }
        }
        testThreadLocal.remove();
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("My Listener finished");
        if (extent != null) {
            extent.flush();
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            Object testInstance = testResult.getInstance();
            if (testInstance instanceof BaseTest) {
                BaseTest baseTest = (BaseTest) testInstance;
                String className = testInstance.getClass().getSimpleName();
                
                // Only clear session for stateless test classes where each method performs its own login
                if (className.equals("LoginValidTest") || className.equals("LoginInvalidTest")) {
                    WebDriver driver = baseTest.driver;
                    if (driver != null) {
                        try {
                            System.out.println("Clearing session and navigating to BASE_URL after method: " + method.getTestMethod().getMethodName());
                            driver.manage().deleteAllCookies();
                            if (driver instanceof JavascriptExecutor) {
                                JavascriptExecutor js = (JavascriptExecutor) driver;
                                js.executeScript("window.localStorage.clear();");
                                js.executeScript("window.sessionStorage.clear();");
                            }
                            driver.get(baseTest.BASE_URL);
                        } catch (Exception e) {
                            System.err.println("Error clearing session in listener: " + e.getMessage());
                        }
                    }
                }
            }
        }
    }

    /**
     * Helper method to capture screenshot and save to file system
     * @param driver WebDriver instance
     * @param testMethodName The name of the test method
     * @return The relative path to the saved screenshot file
     */
    private String captureScreenshot(WebDriver driver, String testMethodName) {
        try {
            // Create screenshots directory if it doesn't exist
            File screenshotDir = new File(SCREENSHOT_DIR);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            // Generate unique filename with timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String fileName = testMethodName + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_DIR + File.separator + fileName;

            // Capture screenshot
            TakesScreenshot ts = (TakesScreenshot) driver;
            File screenshot = ts.getScreenshotAs(OutputType.FILE);
            
            // Copy to destination
            Files.copy(screenshot.toPath(), Paths.get(filePath));
            
            // Return relative path for report (works in HTML)
            return filePath;

        } catch (IOException e) {
            System.err.println("IOException while capturing screenshot: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Exception while capturing screenshot: " + e.getMessage());
            return null;
        }
    }
}
