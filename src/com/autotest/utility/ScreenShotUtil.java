package com.autotest.utility;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * 截图工具类
 * 
 * @author Mark
 */
public class ScreenShotUtil {

	/**
	 * 截图主调函数
	 * 
	 * @param ThreadName
	 * @param driver
	 * @return
	 */
	public static String takeScreenshot(String ThreadName,
			RemoteWebDriver driver) {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		String screenName = ThreadName + "_" + sf.format(new Date()) + ".jpg";
		File dir = new File("snapshot");
		if (!dir.exists())
			dir.mkdirs();
		takeScreenshot(dir.getAbsolutePath(), screenName, driver);
		return "Screenshot path: " + dir.getAbsolutePath() + "\\" + screenName;
	}

	/**
	 * 截图被调函数
	 * 
	 * @param screenPath
	 * @param screenName
	 * @param driver
	 */
	public static void takeScreenshot(String screenPath, String screenName,
			RemoteWebDriver driver) {
		try {
			File scrFile = ((TakesScreenshot) driver)
					.getScreenshotAs(OutputType.FILE);
			FileUtils
					.copyFile(scrFile, new File(screenPath + "/" + screenName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}