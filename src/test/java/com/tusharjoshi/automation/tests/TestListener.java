package com.tusharjoshi.automation.tests;

import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        System.err.println("❌ FAILED: " + result.getTestClass().getName()
                + "." + result.getMethod().getMethodName());
        System.err.println("   Reason: " + result.getThrowable().getMessage());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("⏭ SKIPPED: " + result.getMethod().getMethodName()
                + " | " + (result.getThrowable() != null ? result.getThrowable().getMessage() : ""));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
       // System.out.println("✅ PASSED: " + result.getMethod().getMethodName());
        System.out.println("[" + Thread.currentThread().getName() + "] PASSED: " + result.getMethod().getMethodName());
    }
}
