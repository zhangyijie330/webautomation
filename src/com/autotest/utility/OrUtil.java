package com.autotest.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.openqa.selenium.By;

import com.autotest.or.ObjectLib;

/**
 * 操作对象库的的工具类
 * 
 * @author Mark
 */
public class OrUtil {

	public static By			by				= null;

	/**
	 * 鑫合汇主站XhhObjectLib.properties配置文件
	 */
	private static Properties	xhhproperties	= new Properties();

	/**
	 * 鑫合汇业务管理平台BMPObjectLib.properties配置文件
	 */
	private static Properties	bmpproperties	= new Properties();

	/**
	 * 营销系统MRKTObjectLib.properties配置文件
	 */
	private static Properties	markproperties	= new Properties();

	static {
		InputStream inputStream = null;
		BufferedReader bf = null;
		/**
		 * 加载鑫合汇主站XhhObjectLib.properties配置文件
		 */
		String xhhObjectLibPath = BaseConfigUtil
				.getObjectRepositoryPath("XhhObjectLibPath");
		try {
			inputStream = new FileInputStream(xhhObjectLibPath);
			bf = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			xhhproperties.load(bf);
			System.out.println("加载" + xhhObjectLibPath + "文件成功");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(xhhObjectLibPath + "文件找不到");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("不支持utf-8编码");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("加载" + xhhObjectLibPath + "文件失败");
			System.exit(-1);
		}
		try {
			if (null != bf) {
				bf.close();
			}
			if (null != inputStream) {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * 鑫合汇业务管理平台BMPObjectLib.properties配置文件
		 */
		String BMPObjectLibPath = BaseConfigUtil
				.getObjectRepositoryPath("BMPObjectLibPath");
		try {
			inputStream = new FileInputStream(BMPObjectLibPath);
			bf = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			bmpproperties.load(bf);
			System.out.println("加载" + BMPObjectLibPath + "文件成功");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(BMPObjectLibPath + "文件找不到");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("不支持utf-8编码");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("加载" + BMPObjectLibPath + "文件失败");
			System.exit(-1);
		}
		try {
			if (null != bf) {
				bf.close();
			}
			if (null != inputStream) {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		/**
		 * 营销系统MRKTObjectLib.properties配置文件
		 */
		String MarketObjectLibPath = BaseConfigUtil
				.getObjectRepositoryPath("MarketObjectLibPath");
		try {
			inputStream = new FileInputStream(MarketObjectLibPath);
			bf = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
			markproperties.load(bf);
			System.out.println("加载" + MarketObjectLibPath + "文件成功");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(MarketObjectLibPath + "文件找不到");
			System.exit(-1);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			System.out.println("不支持utf-8编码");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("加载" + MarketObjectLibPath + "文件失败");
			System.exit(-1);
		}
		try {
			if (null != bf) {
				bf.close();
			}
			if (null != inputStream) {
				inputStream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		// TODO
	}

	/**
	 * 根据Key返回value
	 * 
	 * @param key
	 * @return
	 */
	public static String readValue(String key, ObjectLib lib) {
		String value = null;
		switch (lib) {
			case XhhObjectLib:
				value = xhhproperties.getProperty(key);
				break;
			case BMPObjectLib:
				value = bmpproperties.getProperty(key);
				break;
			case MarketObjectLib:
				value = markproperties.getProperty(key);
				break;
			default:
				value = xhhproperties.getProperty(key);
				break;
		}
		return value;
	}

	/**
	 * 根据对象名返回相应的By
	 * 
	 * @param or
	 * @return
	 */
	public static By getBy(String or, ObjectLib lib) {

		if (or.indexOf("_id") != -1)
			by = By.id(readValue(or, lib));
		else if (or.indexOf("_name") != -1)
			by = By.name(readValue(or, lib));
		else if (or.indexOf("_tagName") != -1)
			by = By.tagName(readValue(or, lib));
		else if (or.indexOf("_className") != -1)
			by = By.className(readValue(or, lib));
		else if (or.indexOf("_linkText") != -1)
			by = By.linkText(readValue(or, lib));
		else if (or.indexOf("_partialLinkText") != -1)
			by = By.partialLinkText(readValue(or, lib));
		else if (or.indexOf("_xpath") != -1)
			by = By.xpath(readValue(or, lib));
		else if (or.indexOf("_cssSelector") != -1)
			by = By.cssSelector(readValue(or, lib));
		if (null == by) {
			System.out.println("by is null! 系统退出！");
			System.exit(-1);
		}
		return by;

	}
}
