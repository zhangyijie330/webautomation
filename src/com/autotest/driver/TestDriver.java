package com.autotest.driver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.autotest.utility.DbUtil;
import com.autotest.utility.LogUtil;

/**
 * 创建Selenium Driver1.0
 * 
 * @author Mark
 */
public class TestDriver {

	public String ThreadName;
	public RemoteWebDriver driver = null;
	public DesiredCapabilities desiredcap = null;
	public String REMOTE_ADDRESS = "http://localhost:4444/wd/hub";
	public String REPORTNG_PATH = null;
	public LogUtil logUtil;
	public Logger log;
	public String baseUrl = System.getProperty("user.dir");

	/**
	 * 初始化Log
	 */
	@BeforeTest
	public void initialLog() {

		PropertyConfigurator.configure("log4j.properties");
	}

	/**
	 * 创建Driver
	 * 
	 * @param threadName
	 * @param browserName
	 */
	@BeforeClass
	@Parameters({ "threadName", "browserName", "runMode" })
	public void createDriver(String threadName, String browserName,
			String runMode) {

		this.ThreadName = threadName;

		if (runMode.equalsIgnoreCase("local")) {
			// 如果同名的logger已经存在，不会创建新的logger实例,所以这里用线程名区分不同的logger
			try {
				if (threadName.startsWith("Firefox")) {
					logUtil = new LogUtil(baseUrl + "/logs/firefox",
							Logger.getLogger(threadName));
					desiredcap = DesiredCapabilities.firefox();
					desiredcap.setBrowserName(browserName);
					System.setProperty("webdriver.firefox.bin",
							"C:\\Program Files\\Mozilla Firefox\\firefox.exe");
					driver = new FirefoxDriver(desiredcap);
				} else if (threadName.startsWith("Chrome")) {
					logUtil = new LogUtil(baseUrl + "/logs/chrome",
							Logger.getLogger(threadName));
					desiredcap = DesiredCapabilities.chrome();
					desiredcap.setBrowserName(browserName);
					driver = new ChromeDriver(desiredcap);
				} else {
					logUtil = new LogUtil(baseUrl + "/logs/ie",
							Logger.getLogger(threadName));
					desiredcap = DesiredCapabilities.internetExplorer();
					desiredcap.setBrowserName(browserName);
					driver = new InternetExplorerDriver(desiredcap);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				if (threadName.startsWith("Firefox")) {
					logUtil = new LogUtil(baseUrl + "/logs/firefox",
							Logger.getLogger(threadName));
					// System.setProperty("webdriver.firefox.bin",
					// "C:\\Program Files\\Mozilla Firefox\\firefox.exe");
					desiredcap = DesiredCapabilities.firefox();
				} else if (threadName.startsWith("Chrome")) {
					logUtil = new LogUtil(baseUrl + "/logs/chrome",
							Logger.getLogger(threadName));
					desiredcap = DesiredCapabilities.chrome();
				} else {
					logUtil = new LogUtil(baseUrl + "/logs/ie",
							Logger.getLogger(threadName));
					desiredcap = DesiredCapabilities.internetExplorer();
				}
				desiredcap.setBrowserName(browserName);
				driver = new RemoteWebDriver(new URL(REMOTE_ADDRESS),
						desiredcap);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}

		log = logUtil.setLogFileName();
		log = logUtil.setLogFileName2();
		log.info("******************************************************************************");
		log.info("Create a Driver, Browser:" + browserName);
		driver.manage().window().maximize();
		//
		new DbUtil(log);
	}

	/**
	 * 关闭Driver
	 */
	@AfterClass
	public void quitDriver() {

		if (null != driver) {
			log.info("Driver with " + desiredcap.getBrowserName()
					+ " is closed.");
			driver.quit();
		}
	}

	/**
	 * 打开报告
	 */
	@AfterSuite
	public void openReport() throws IOException {

		log.info("Open the ReportNG report.");
		Runtime ce = Runtime.getRuntime();
		REPORTNG_PATH = baseUrl + "\\test-output\\html\\index.html";
		ce.exec("cmd   /c   start  " + REPORTNG_PATH);

	}

}
