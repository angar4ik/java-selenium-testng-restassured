package com.example.report;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Thread-safe manager for ExtentReports instance.
 * One instance per suite run; test nodes are stored per-thread.
 */
public final class ExtentReportManager {

    private static final ExtentReports extentReports = new ExtentReports();
    private static final ThreadLocal<com.aventstack.extentreports.ExtentTest> testNode = new ThreadLocal<>();
    private static volatile boolean initialized = false;

    private ExtentReportManager() {}

    /** Initialise the report - call once before the suite runs. */
    public static synchronized void initReport(String outputDir) {
        if (!initialized) {
            File dir = new File(outputDir);
            if (!dir.exists()) dir.mkdirs();

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
            String path = outputDir + File.separator + "SparkReport_" + timestamp + ".html";

            ExtentSparkReporter spark = new ExtentSparkReporter(path);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle("Test Automation Report");
            spark.config().setReportName("Java Selenium TestNG REST Assured");
            spark.config().setTimeStampFormat("yyyy-MM-dd HH:mm:ss");

            extentReports.attachReporter(spark);
            initialized = true;
        }
    }

    /** Create a test node in the report for the current thread. */
    public static void startTest(String testName, String description) {
        testNode.set(extentReports.createTest(testName, description));
    }

    /** Assign categories / groups to the current test. */
    public static void assignCategory(String... categories) {
        getTest().assignCategory(categories);
    }

    /** Assign an author tag to the current test. */
    public static void assignAuthor(String... authors) {
        getTest().assignAuthor(authors);
    }

    public static com.aventstack.extentreports.ExtentTest getTest() {
        return testNode.get();
    }

    /** Remove the ThreadLocal test node after the test method finishes. */
    public static void endTest() {
        testNode.remove();
    }

    /** Flush all report data to disk. Call after the suite finishes. */
    public static void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
