package com.example.report;

import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IConfigurationListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener that integrates ExtentReports.
 * Captures pass / fail / skip results and attaches screenshots on UI test failures.
 *
 * Registered in testng.xml:
 * {@code <listener class-name="com.example.report.ExtentTestListener"/>}
 */
public class ExtentTestListener implements ITestListener, ISuiteListener, IConfigurationListener {

    private static final Logger log = LoggerFactory.getLogger(ExtentTestListener.class);
    private static final String REPORT_DIR = "test-output/extent-reports";

    // ── Suite lifecycle ────────────────────────────────────────────────

    @Override
    public void onStart(ISuite suite) {
        ExtentReportManager.initReport(REPORT_DIR);
        log.info("ExtentReports initialised → {}", REPORT_DIR);
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentReportManager.flush();
        log.info("ExtentReports flushed to disk.");
    }

    // ── Test lifecycle ─────────────────────────────────────────────────

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription();

        ExtentReportManager.startTest(testName, description);

        // Auto-detect test type from class package (api.* → API, ui.* → UI)
        String className = result.getTestClass().getName().toLowerCase();
        String testType;
        if (className.contains(".api.")) {
            testType = "API";
        } else if (className.contains(".ui.")) {
            testType = "UI";
        } else {
            testType = "Other";
        }

        // Category view → filter by API / UI
        ExtentReportManager.assignCategory(testType);
        // Author → which test class (useful when many classes exist)
        ExtentReportManager.assignAuthor(result.getTestClass().getRealClass().getSimpleName());

        // Also assign any explicit TestNG groups
        String[] groups = result.getMethod().getGroups();
        if (groups.length > 0) {
            ExtentReportManager.assignCategory(groups);
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        ExtentReportManager.getTest()
                .log(Status.PASS, "Test passed");
        ExtentReportManager.endTest();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        var test = ExtentReportManager.getTest();

        // Log the exception
        Throwable throwable = result.getThrowable();
        if (throwable != null) {
            test.log(Status.FAIL, throwable);
        } else {
            test.log(Status.FAIL, "Test failed");
        }

        // Attach screenshot if a WebDriver is available (UI test)
        WebDriver driver = DriverManager.getDriver();
        if (driver instanceof TakesScreenshot ts) {
            try {
                String base64Screenshot = ts.getScreenshotAs(OutputType.BASE64);
                test.addScreenCaptureFromBase64String(base64Screenshot, "Screenshot on failure");
                log.info("Screenshot attached to report for failed test: {}", result.getMethod().getMethodName());
            } catch (Exception e) {
                log.warn("Could not take screenshot: {}", e.getMessage());
            }
        }

        ExtentReportManager.endTest();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        ExtentReportManager.getTest()
                .log(Status.SKIP, "Test skipped: " + (result.getThrowable() != null
                        ? result.getThrowable().getMessage()
                        : "No reason provided"));
        ExtentReportManager.endTest();
    }

    // ── Unused callbacks (no-op) ───────────────────────────────────────

    @Override public void onTestFailedButWithinSuccessPercentage(ITestResult result) {}

    @Override public void onStart(ITestContext context) {}

    @Override public void onFinish(ITestContext context) {}

    @Override public void onConfigurationSuccess(ITestResult result) {}

    @Override public void onConfigurationFailure(ITestResult result) {}

    @Override public void onConfigurationSkip(ITestResult result) {}
}
