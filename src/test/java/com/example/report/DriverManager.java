package com.example.report;

import org.openqa.selenium.WebDriver;

/**
 * Thread-safe holder for the current WebDriver instance.
 * BaseUiTest sets it; ExtentTestListener reads it for screenshots on failure.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverManager() {}

    public static void setDriver(WebDriver driver) {
        driverThreadLocal.set(driver);
    }

    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public static void removeDriver() {
        driverThreadLocal.remove();
    }
}
