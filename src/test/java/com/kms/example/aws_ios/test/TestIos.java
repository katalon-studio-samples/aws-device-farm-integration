package com.kms.example.aws_ios.test;

import java.net.URL;
import java.util.HashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.example.aw_ios.utils.ConsoleLogger;
import com.kms.example.aw_ios.utils.SideloadUtils;

import io.appium.java_client.MobileElement;
import io.appium.java_client.ios.IOSDriver;

public class TestIos {

    /**
     * Katalon version which will be used to run the test
     */
    private static final String KATALON_VERSION = ""; // Leave it blank to always use the latest version

    /**
     * The package file under the "src/test/resources" folder
     */
    private static final String KATALON_PROJECT_PACKAGE_FILE = "KatalonDemoProject.zip";

    /**
     * Path to the katalon project inside the package file.
     * If not specified it will use the same name with the package file.
     * (In this case, it is: KatalonDemoProject)
     */
    private static final String KATALON_PROJECT_PATH = "";

    /**
     * Katalon arguments
     * @apiNote Remember to always set "browserType" to "Remote". This will prevent Katalon from inject inappropriate configurations in execution.
     * @apiNote Besides, you do not need to include project path in the argument list.
     */
    private static final String KATALON_EXECUTE_ARGS = String.format("-retry=0 -testSuitePath=\"Test Suites/Regression Tests\" -executionProfile=default -browserType=Remote");

    private IOSDriver driver;

    @Before
    public void setUp() throws Exception {
//        final String URL_STRING = "http://127.0.0.1:4723/wd/hub";
//        URL url = new URL(URL_STRING);
//        // driver = new AdroidDriver<MobileElement>(url, new DesiredCapabilities());
//        DesiredCapabilities capabilities = new DesiredCapabilities();
//        capabilities.setCapability("platformName", "iOS");
//        driver = new IOSDriver<MobileElement>(url, capabilities);
    }

    @After
    public void tearDown() throws Exception {
//        driver.quit();
    }

    @Test
    public void test() {
        String version = SideloadUtils.getProperty("KATALON_VERSION", KATALON_VERSION);
        String projectPackageFile = SideloadUtils.getProperty("KATALON_PROJECT_PACKAGE_FILE", KATALON_PROJECT_PACKAGE_FILE);
        String projectPath = SideloadUtils.getProperty("KATALON_PROJECT_PATH", KATALON_PROJECT_PATH);
        String executeArgs = SideloadUtils.getProperty("KATALON_EXECUTE_ARGS", KATALON_EXECUTE_ARGS);

        ConsoleLogger.logInfo("KATALON_VERSION: " + version);
        ConsoleLogger.logInfo("KATALON_PROJECT_PACKAGE_FILE: " + projectPackageFile);
        ConsoleLogger.logInfo("KATALON_PROJECT_PATH: " + projectPath);
        
        boolean result = SideloadUtils.executeKatalon(
                projectPackageFile,
                version,
                null, // ksLocation
                projectPath,
                executeArgs,
                null, // x11Display
                null, // xvfbConfiguration
                new HashMap<>(System.getenv()));
        if (!result) {
            Assert.fail("Failed to execute Katalon");
        }
    }

}
