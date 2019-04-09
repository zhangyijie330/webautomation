package com.autotest.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 * 
 * @author wb0002
 * 
 */
public class DateUtils {

	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private static SimpleDateFormat dateFormat2 = new SimpleDateFormat(
			"yyyy-MM-dd");

	private static SimpleDateFormat priDateFormat = null;

	private static Calendar calendar = Calendar.getInstance();

	/**
	 * 将当前日期格式化成yyyy-MM-dd HH:mm:ss格式
	 * 
	 * @return
	 */
	public static String simpleDateFormat() {
		return dateFormat.format(new Date());
	}

	/**
	 * 将当前日期格式化成yyyy-MM-dd格式
	 * 
	 * @return
	 */
	public static String simpleDateFormat2() {
		return dateFormat2.format(new Date());
	}

	/**
	 * 将当前日期格式化成指定格式 如： "yyyy.MM.dd G 'at' HH:mm:ss z" --> 2001.07.04 AD at
	 * 12:08:56 PDT "EEE, MMM d, ''yy" --> Wed, Jul 4, '01 "h:mm a" --> 12:08 PM
	 * "hh 'o''clock' a, zzzz" --> 12 o'clock PM, Pacific Daylight Time
	 * "K:mm a, z" --> 0:08 PM, PDT "yyyyy.MMMMM.dd GGG hh:mm aaa" -->
	 * 02001.July.04 AD 12:08 PM "EEE, d MMM yyyy HH:mm:ss Z" --> Wed, 4 Jul
	 * 2001 12:08:56 -0700 "yyMMddHHmmssZ" --> 010704120856-0700
	 * "yyyy-MM-dd'T'HH:mm:ss.SSSZ" --> 2001-07-04T12:08:56.235-0700
	 * 
	 * @param pattern
	 * @return
	 */
	public static String formatDate(String pattern) {
		priDateFormat = new SimpleDateFormat(pattern);
		return priDateFormat.format(new Date());
	}

	/**
	 * 将Date格式化成yyyy-MM-dd HH:mm:ss格式
	 * 
	 * @param date
	 * @return
	 */
	public static String simpleDateFormat(Date date) {
		return dateFormat.format(date);
	}

	/**
	 * 将Date格式化成yyyy-MM-dd格式
	 * 
	 * @param date
	 * @return
	 */
	public static String simpleDateFormat2(Date date) {
		return dateFormat2.format(date);
	}

	/**
	 * 将Date按指定格式进行格式化 "yyyy.MM.dd G 'at' HH:mm:ss z" --> 2001.07.04 AD at
	 * 12:08:56 PDT "EEE, MMM d, ''yy" --> Wed, Jul 4, '01 "h:mm a" --> 12:08 PM
	 * "hh 'o''clock' a, zzzz" --> 12 o'clock PM, Pacific Daylight Time
	 * "K:mm a, z" --> 0:08 PM, PDT "yyyyy.MMMMM.dd GGG hh:mm aaa" -->
	 * 02001.July.04 AD 12:08 PM "EEE, d MMM yyyy HH:mm:ss Z" --> Wed, 4 Jul
	 * 2001 12:08:56 -0700 "yyMMddHHmmssZ" --> 010704120856-0700
	 * "yyyy-MM-dd'T'HH:mm:ss.SSSZ" --> 2001-07-04T12:08:56.235-0700
	 * 
	 * @param pattern
	 * @param date
	 * @return
	 */
	public static String formatDate(String pattern, Date date) {
		priDateFormat = new SimpleDateFormat(pattern);
		return priDateFormat.format(date);
	}

	/**
	 * 将string类型的日期按yyyy-MM-dd HH:mm:ss格式格式化成Date类型
	 * 
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date stringToDate(String strDate) throws ParseException {
		return dateFormat.parse(strDate);
	}

	/**
	 * 将string类型的日期按yyyy-MM-dd格式格式化成Date类型
	 * 
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Date stringToDate2(String strDate) throws ParseException {
		return dateFormat2.parse(strDate);
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static Date getNowDate() {
		return new Date();
	}

	/**
	 * 获取当前日期，String类型的日期(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @return
	 */
	public static String getNowDateStr() {
		return simpleDateFormat(new Date());
	}

	/**
	 * 获取当前日期，String类型的日期(yyyy-MM-dd)
	 * 
	 * @return
	 */
	public static String getNowDateStr2() {
		return simpleDateFormat2(new Date());
	}

	/**
	 * 获取当前时间的毫秒数
	 * 
	 * @return
	 */
	public static Long getTime() {
		Date date = new Date();
		return date.getTime();
	}

	/**
	 * 将当前时间+N天，并返回string类型的日期(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param days
	 * @return
	 */
	public static String getNowDateAddDays(int days) {
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)
				+ days);
		Date date = calendar.getTime();
		return simpleDateFormat(date);
	}

	/**
	 * 将当前时间+N天，并返回Date类型的日期
	 * 
	 * @param days
	 * @return
	 */
	public static Date getNowDateAddDays2(int days) {
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)
				+ days);
		return calendar.getTime();
	}

	/**
	 * 将Date加X天
	 * 
	 * @param date
	 * @param days
	 * @return
	 */
	public static Date dayAddDays(Date date, int days) {
		calendar.setTime(date);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)
				+ days);
		return calendar.getTime();
	}

	/**
	 * 功能：将Date加X月
	 * 
	 * @author xudashen
	 * @param date
	 * @param month
	 * @return Date
	 * @date 2016年9月1日 上午10:57:01
	 */
	public static Date dayAddMonth(Date date, int month) {
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, month);
		return calendar.getTime();
	}

	/**
	 * 将当前时间+N天，并返回string类型的日期(yyyy-MM-dd)
	 * 
	 * @param days
	 * @return
	 */
	public static String getNowDateAddDays3(int days) {
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)
				+ days);
		Date date = calendar.getTime();
		return simpleDateFormat2(date);
	}

	/**
	 * 将当前时间+N分钟，并返回string类型的日期(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param minute
	 * @return
	 */
	public static String getNowDateAddMinute(int minute) {
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, minute);
		Date date = calendar.getTime();
		return simpleDateFormat(date);
	}

	/**
	 * 将当前时间+N分钟，并返回Date类型的日期
	 * 
	 * @param minute
	 * @return
	 */
	public static Date getNowDateAddMinute2(int minute) {
		calendar.setTime(new Date());
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	/**
	 * 指定日期+N分钟
	 * 
	 * @param date
	 * @param minute
	 * @return
	 */
	public static Date getSomeDayAddMinute(Date date, int minute) {
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minute);
		return calendar.getTime();
	}

	/**
	 * 指定日期+N分钟,输入日期格式为yyyy-MM-dd HH:mm:ss,并返回string类型的日期(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param time
	 * @param minute
	 * @return
	 * @throws ParseException
	 */
	public static String getSomeDayAddMinute2(String time, int minute)
			throws ParseException {
		Date date = stringToDate(time);
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minute);
		Date date2 = calendar.getTime();
		return simpleDateFormat(date2);
	}

	/**
	 * 将当前时间+N小时，并返回string类型的日期(yyyy-MM-dd HH:mm:ss)
	 * 
	 * @param minute
	 * @return
	 */
	public static String getNowDateAddHour(int hour) {
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, hour);
		Date date = calendar.getTime();
		return simpleDateFormat(date);
	}

	/**
	 * 将当前时间+N小时，并返回Date类型的日期
	 * 
	 * @param minute
	 * @return
	 */
	public static Date getNowDateAddHour2(int hour) {
		calendar.setTime(new Date());
		calendar.add(Calendar.HOUR, hour);
		return calendar.getTime();
	}

	/**
	 * 获取当天的最后的时间，string类型的日期(yyyy-MM-dd HH:mm:ss) 如：2016-08-04 23:59:00
	 * 
	 * @return
	 */
	public static String getTodayEndTime() {
		String today = simpleDateFormat2();
		return today + " 23:59:59";
	}

	/**
	 * 获取当天+N天后的最后的时间，string类型的日期(yyyy-MM-dd HH:mm:ss) 如：2016-08-04 23:59:00
	 * 
	 * @param days
	 * @return
	 */
	public static String getSomeDayEndTime(int days) {
		String date = getNowDateAddDays3(days);
		return date + " 23:59:59";
	}

	/**
	 * 获取当天的最后的时间，Date类型的日期
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static Date getTodayEndTime2() throws ParseException {
		String today = simpleDateFormat2() + " 23:59:59";
		return stringToDate(today);
	}

	/**
	 * 计算相差X天，日期格式必须是yyyy-MM-dd HH:mm:ss
	 * 
	 * @param smallTime
	 * @param bigTime
	 * @return
	 * @throws ParseException
	 */
	public static int differDays(String smallTime, String bigTime)
			throws ParseException {
		Date sDate = stringToDate(smallTime);
		Date bDate = stringToDate(bigTime);
		calendar.setTime(sDate);
		int day1 = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.setTime(bDate);
		int day2 = calendar.get(Calendar.DAY_OF_YEAR);
		return (day2 - day1);
	}

	/**
	 * 计算相差X天，日期格式必须是yyyy-MM-dd
	 * 
	 * @param smallTime
	 * @param bigTime
	 * @return
	 * @throws ParseException
	 */
	public static int differDays2(String smallTime, String bigTime)
			throws ParseException {
		Date sDate = stringToDate2(smallTime);
		Date bDate = stringToDate2(bigTime);
		Calendar aCalendar = Calendar.getInstance();
		Calendar bCalendar = Calendar.getInstance();
		aCalendar.setTime(sDate);
		bCalendar.setTime(bDate);
		int days = 0;
		while (aCalendar.before(bCalendar)) {
			days++;
			aCalendar.add(Calendar.DAY_OF_YEAR, 1);
		}
		return days;
	}

	/**
	 * 计算相差X天
	 * 
	 * @param smallTime
	 * @param bigTime
	 * @return
	 * @throws ParseException
	 */
	public static int differDays3(Date smallDate, Date bigDate)
			throws ParseException {
		calendar.setTime(smallDate);
		int day1 = calendar.get(Calendar.DAY_OF_YEAR);
		calendar.setTime(bigDate);
		int day2 = calendar.get(Calendar.DAY_OF_YEAR);
		return (day2 - day1);
	}

	/**
	 * 比较是否是同一天，日期格式必须为yyyy-MM-dd
	 * 
	 * @param strDate1
	 * @param strDate2
	 * @return
	 * @throws ParseException
	 */
	public static boolean isSameDay(String strDate1, String strDate2)
			throws ParseException {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		Date date1 = stringToDate2(strDate1);
		Date date2 = stringToDate2(strDate2);
		calendar1.setTime(date1);
		calendar2.setTime(date2);
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.MONTH) == calendar2
						.get(Calendar.MONTH)
				&& calendar1.get(Calendar.DAY_OF_MONTH) == calendar2
						.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 比较是否是同一天
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 * @throws ParseException
	 */
	public static boolean isSameDay2(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(date1);
		calendar2.setTime(date2);
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.MONTH) == calendar2
						.get(Calendar.MONTH)
				&& calendar1.get(Calendar.DAY_OF_MONTH) == calendar2
						.get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 比较是否是同一天的同一时刻，日期格式必须为yyyy-MM-dd HH:mm:ss
	 * 
	 * @param strDate1
	 * @param strDate2
	 * @return
	 * @throws ParseException
	 */
	public static boolean isSameTime(String strDate1, String strDate2)
			throws ParseException {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		Date date1 = stringToDate(strDate1);
		Date date2 = stringToDate(strDate2);
		calendar1.setTime(date1);
		calendar2.setTime(date2);
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.MONTH) == calendar2
						.get(Calendar.MONTH)
				&& calendar1.get(Calendar.DAY_OF_MONTH) == calendar2
						.get(Calendar.DAY_OF_MONTH)
				&& calendar1.get(Calendar.HOUR_OF_DAY) == calendar2
						.get(Calendar.HOUR_OF_DAY)
				&& calendar1.get(Calendar.MINUTE) == calendar2
						.get(Calendar.MINUTE)
				&& calendar1.get(Calendar.SECOND) == calendar2
						.get(Calendar.SECOND);
	}

	/**
	 * 比较是否是同一天的同一时刻
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameTime2(Date date1, Date date2) {
		Calendar calendar1 = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		calendar1.setTime(date1);
		calendar2.setTime(date2);
		return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR)
				&& calendar1.get(Calendar.MONTH) == calendar2
						.get(Calendar.MONTH)
				&& calendar1.get(Calendar.DAY_OF_MONTH) == calendar2
						.get(Calendar.DAY_OF_MONTH)
				&& calendar1.get(Calendar.HOUR_OF_DAY) == calendar2
						.get(Calendar.HOUR_OF_DAY)
				&& calendar1.get(Calendar.MINUTE) == calendar2
						.get(Calendar.MINUTE)
				&& calendar1.get(Calendar.SECOND) == calendar2
						.get(Calendar.SECOND);
	}

	/**
	 * 用于计算当天时间差,最大单位时间为日 dd：日 HH: 时 mm: 分 ss： 秒
	 * 
	 * @param startTime
	 *            格式必须为yyyy-MM-dd HH:mm:ss
	 * @param endTime
	 *            格式必须为yyyy-MM-dd HH:mm:ss
	 * @param format
	 *            期望输出的格式，比如：dd日HH时mm分ss秒
	 * @return 输出格式为format
	 * @throws ParseException
	 */
	public static String differTime2String(String startTime, String endTime,
			String format) throws ParseException {
		long endTimeLong = dateFormat.parse(endTime).getTime();
		long satrtTimeLong = dateFormat.parse(startTime).getTime();
		// 计算时间差
		long diff = (endTimeLong - satrtTimeLong);
		long day = diff / (24 * 60 * 60 * 1000);
		long hour = (diff / (60 * 60 * 1000) - day * 24);
		long min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		String minStr = "" + min;
		String secStr = "" + sec;
		if (min < 10) {
			minStr = "0" + minStr;
		}
		if (sec < 10) {
			secStr = "0" + secStr;
		}
		String differTime = StringUtils.format(format, "d+", day + "");
		differTime = StringUtils.format(differTime, "[Hh]+", hour + "");
		differTime = StringUtils.format(differTime, "m+", minStr);
		differTime = StringUtils.format(differTime, "s+", secStr);
		return differTime;
	}

	/**
	 * 判断是否时间差是否在指定可接受的时间范围内,
	 * 
	 * @param time1
	 * @param time2
	 * @param sec
	 *            秒
	 * @format time1与time2的格式
	 * @return
	 * @throws ParseException
	 */
	public static boolean isDifferAccept(String time1, String time2, int sec,
			String format) throws ParseException {
		boolean flag = false;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		long time1Long = dateFormat.parse(time1).getTime();
		long time2Long = dateFormat.parse(time2).getTime();
		long differ = time2Long - time1Long;
		int differSec = (int) (differ / 1000);
		if (differSec < 0)
			differSec = 0 - differSec;
		if (differSec < sec)
			flag = true;
		return flag;
	}

	/**
	 * 比较time1是否比time2时间早
	 * 
	 * @param time1
	 *            格式必须为yyyy-MM-dd HH:mm:ss
	 * @param time2
	 *            格式必须为yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public static boolean isEarlyThan(String time1, String time2)
			throws ParseException {
		long timelong1 = stringToDate(time1).getTime();
		long timelong2 = stringToDate(time2).getTime();
		return timelong1 < timelong2;
	}

	/**
	 * 比较time1是否比time2时间早
	 * 
	 * @param time1
	 *            格式必须为yyyy-MM-dd HH:mm:ss
	 * @param time2
	 *            格式必须为yyyy-MM-dd HH:mm:ss
	 * @return
	 * @throws ParseException
	 */
	public static long differSecondTime(String smallTime, String bigTime)
			throws ParseException {
		long timelong1 = stringToDate(smallTime).getTime();
		long timelong2 = stringToDate(bigTime).getTime();
		return (timelong2 - timelong1) / 1000;
	}

}
