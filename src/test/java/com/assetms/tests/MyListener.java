package com.assetms.tests;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class MyListener implements ITestListener {
    public void onStart(ITestContext context)
    {
        System.out.println("My Listner started");
    }

    public void onTestStart(ITestResult result)
    {
        System.out.println("Test started");
    }
}
