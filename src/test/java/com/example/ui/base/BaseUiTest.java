package com.example.ui.base;

import com.example.config.TestConfig;
import com.example.report.DriverManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

/**
 * Base class for Selenium UI tests.
 * Uses WebDriverManager for automatic driver management (no manual driver downloads).
 */
public abstract class BaseUiTest {

    protected static final Logger log = LoggerFactory.getLogger(BaseUiTest.class);
    protected static final String BASE_URL = TestConfig.THE_INTERNET_BASE_URL;

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        // Auto-downloads correct chromedriver version
        WebDriverManager.chromedriver().setup();

        boolean headed = Boolean.parseBoolean(System.getProperty("headed", "false"));

        ChromeOptions options = new ChromeOptions();

        // Allow overriding Chrome binary via env var or system property
        String chromeBin = System.getenv("CHROME_BIN");
        if (chromeBin == null || chromeBin.isBlank()) {
            chromeBin = System.getProperty("chrome.binary");
        }
        if (chromeBin != null && !chromeBin.isBlank()) {
            options.setBinary(chromeBin);
            log.info("Using Chrome binary: {}", chromeBin);
        }

        if (!headed) {
            options.addArguments("--headless=new");   // new headless mode (closer to real browser)
        }
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        if (headed) {
            driver.manage().window().maximize();
        }
        driver.manage().timeouts()
                .implicitlyWait(Duration.ofSeconds(TestConfig.IMPLICIT_WAIT_SECONDS))
                .pageLoadTimeout(Duration.ofSeconds(TestConfig.PAGE_LOAD_TIMEOUT_SECONDS));

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Register driver so ExtentTestListener can take screenshots on failure
        DriverManager.setDriver(driver);

        log.info("Browser launched [mode={}] — {}", headed ? "headed" : "headless", BASE_URL);
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
            DriverManager.removeDriver();
            log.info("Browser closed.");
        }
    }

    /** Navigate to a relative path on The Internet. */
    protected void navigateTo(String path) {
        driver.get(BASE_URL + path);
    }
}
