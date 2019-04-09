package com.autotest.utility;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 加载baseConfig.properties配置文件信息
 * 
 * @author XUjj
 */
public class BaseConfigUtil {

	/**
	 * baseConfig.properties文件加载器
	 */
	static Properties baseProperties = new Properties();

	static {
		/**
		 * 加载baseConfig.properties文件
		 */
		String baseConfigPath = "config/baseConfig.properties";
		try {
			InputStream inputStream = new FileInputStream(baseConfigPath);
			BufferedReader bf = new BufferedReader(new InputStreamReader(
					inputStream, "utf-8"));
			baseProperties.load(bf);
			inputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println("baseConfig.properties文件不存在！系统退出！");
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("加载baseConfig.properties文件失败！系统退出！");
			System.exit(-1);
		}
	}

	/**
	 * 获取鑫合汇首页地址
	 * 
	 * @return
	 */
	public static String getHomePageURL() {
		String homePageURL = null;
		homePageURL = baseProperties.getProperty("homePageURL");
		if (StringUtils.isEmpty(homePageURL)) {
			System.out
					.println("获取鑫合汇首页地址失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return homePageURL;
	}

	/**
	 * 获取鑫合汇业务管理平台地址
	 * 
	 * @return
	 */
	public static String getBmpConfig(String key) {
		String bmpConfig = baseProperties.getProperty(key);
		if (StringUtils.isEmpty(bmpConfig)) {
			System.out.println("获取业务管理平台配置" + key
					+ "失败！请检查baseConfig.properties配置文件是否正确！");
			System.exit(-1);
		}
		return bmpConfig;
	}

	/**
	 * 获取对象库路径
	 * 
	 * @param key
	 * @return
	 */
	public static String getObjectRepositoryPath(String key) {
		String objectRepositoryPath = null;
		objectRepositoryPath = baseProperties.getProperty(key);
		if (StringUtils.isEmpty(objectRepositoryPath)) {
			System.out
					.println("获取对象库路径失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return objectRepositoryPath;
	}

	/**
	 * 获取数据库配置信息
	 * 
	 * @param key
	 * @return
	 */
	public static String getDBConfig(String key) {
		String DBConfig = null;
		DBConfig = baseProperties.getProperty(key);
		if (StringUtils.isEmpty(DBConfig)) {
			System.out.println("获取数据库配置信息 " + key
					+ " 失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return DBConfig;
	}

	/**
	 * 获取服务器配置信息
	 * 
	 * @param key
	 * @return
	 */
	public static String getServerConfig(String key) {
		String serverInfo = baseProperties.getProperty(key);
		if (StringUtils.isEmpty(serverInfo)) {
			System.out.println("获取服务器信息" + key
					+ "失败！请检查baseConfig.properties配置文件是否正确！");
			System.exit(-1);
		}
		return serverInfo;
	}

	/**
	 * 获取登录鑫合汇用户名
	 * 
	 * @return
	 */
	public static String getUserName() {
		String userName = null;
		userName = baseProperties.getProperty("userName");
		if (StringUtils.isEmpty(userName)) {
			System.out
					.println("获取鑫合汇用户名失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return userName;
	}

	/**
	 * 获取登录鑫合汇密码
	 * 
	 * @return
	 */
	public static String getPwd() {
		String userPWd = null;
		userPWd = baseProperties.getProperty("userPWd");
		if (StringUtils.isEmpty(userPWd)) {
			System.out
					.println("获取鑫合汇密码失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return userPWd;
	}

	/**
	 * 读取等待页面加载时间(秒)
	 * 
	 * @return
	 */
	public static String getWaitPageLoadTime() {
		String time = baseProperties.getProperty("waitPageLoadTime");
		if (!StringUtils.isEmpty(time)) {
			try {
				return time;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("读取等待页面加载时间(秒)失败，使用默认配置，30秒。");
			}
		}
		return "30";// 如果没有配置，默认为30秒
	}

	/**
	 * 读取等待加载元素时间(秒)
	 * 
	 * @return
	 */
	public static String getWaitElementLoadTime() {
		String time = baseProperties.getProperty("waitElementLoadTime");
		if (!StringUtils.isEmpty(time)) {
			try {
				return time;
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("读取等待加载元素时间(秒)失败，使用默认配置，10秒。");
			}
		}
		return "10";// 如果没有配置，默认为10秒
	}

	/**
	 * 获取保理账号用户名
	 * 
	 * @return
	 */
	public static String getFactorName() {
		String userName = null;
		userName = baseProperties.getProperty("factor_username");
		if (StringUtils.isEmpty(userName)) {
			System.out
					.println("获取保理账号用户名失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return userName;
	}

	/**
	 * 获取保理账号密码
	 * 
	 * @return
	 */
	public static String getFactorPwd() {
		String userPWd = null;
		userPWd = baseProperties.getProperty("factor_pwd");
		if (StringUtils.isEmpty(userPWd)) {
			System.out
					.println("获取保理账号密码失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return userPWd;
	}

	/**
	 * 获取管理系统账号用户名
	 * 
	 * @return
	 */
	public static String getBMPName() {
		String userName = null;
		userName = baseProperties.getProperty("bmp_username");
		if (StringUtils.isEmpty(userName)) {
			System.out
					.println("获取管理系统账号用户名失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return userName;
	}

	/**
	 * 获取营销系统账号用户名
	 * 
	 * @return
	 */
	public static String getMRKTName() {
		String userName = null;
		userName = baseProperties.getProperty("mrkt_username");
		if (StringUtils.isEmpty(userName)) {
			System.out
					.println("获取管理系统账号用户名失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return userName;
	}

	/**
	 * 获取营销系统URL
	 * 
	 * @return
	 */
	public static String getMRKTUrl() {
		String userPWd = null;
		userPWd = baseProperties.getProperty("mrkt_url");
		if (StringUtils.isEmpty(userPWd)) {
			System.out
					.println("获取管理系统URL失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return userPWd;
	}

	/**
	 * 获取营销系统账号密码
	 * 
	 * @return
	 */
	public static String getMRKTPwd() {
		String userPWd = null;
		userPWd = baseProperties.getProperty("mrkt_pwd");
		if (StringUtils.isEmpty(userPWd)) {
			System.out
					.println("获取管理系统账号密码失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return userPWd;
	}

	/**
	 * 获取管理系统账号密码
	 * 
	 * @return
	 */
	public static String getBMPPwd() {
		String userPWd = null;
		userPWd = baseProperties.getProperty("bmp_pwd");
		if (StringUtils.isEmpty(userPWd)) {
			System.out
					.println("获取管理系统账号密码失败！请检查baseConfig.properties配置文件是否正确！系统退出！");
			System.exit(-1);
		}
		return userPWd;
	}

}
