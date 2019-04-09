package com.autotest.utility;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

/**
 * 日志工具类
 * 
 * @author Mark
 */
public class LogUtil {

	public String path;
	public Logger logger;

	/**
	 * 构造函数
	 * 
	 * @param path
	 * @param logger
	 */
	public LogUtil(String path, Logger logger) {
		this.path = path;
		this.logger = logger;
	}

	public Logger setLogFileName() {
		DailyRollingFileAppender appender = (DailyRollingFileAppender) Logger
				.getRootLogger().getAppender("R");
		appender.setFile(path + "/all_log.log");
		appender.activateOptions();
		return logger;
	}

	public Logger setLogFileName2() {
		DailyRollingFileAppender appender = (DailyRollingFileAppender) Logger
				.getRootLogger().getAppender("E");
		appender.setFile(path + "/error.log");
		appender.activateOptions();
		return logger;
	}

	/**
	 * 日志模板设置
	 * 
	 * @param tName
	 * @return
	 */
	public Logger getLogger(String tName) {

		logger.removeAllAppenders();
		logger.setLevel(Level.INFO);
		logger.setAdditivity(true);

		FileAppender appender = new FileAppender();

		PatternLayout layout = new PatternLayout();
		String conversionPattern = "%d{[yyyy-MM-dd hh:mm:ss]}[%p][%c][%F][%L] - %m%n";
		layout.setConversionPattern(conversionPattern);
		appender.setLayout(layout);

		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		appender.setFile(path + File.separator + sf.format(new Date()) + "_"
				+ tName + ".log");
		appender.setEncoding("UTF-8");

		appender.setAppend(true);
		appender.activateOptions();
		logger.addAppender(appender);

		return logger;
	}

}