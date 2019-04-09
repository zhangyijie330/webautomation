package com.autotest.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 正则表达式工具类
 * 
 * @author sunlingyun
 * 
 */
public class PatternUtil {
	/**
	 * 根据格式匹配字符串，并返回第一个匹配的
	 * 
	 * @param regex
	 * @param input
	 * @return
	 */
	public static String getMatchStr(String regex, String input) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		String matchStr = "";
		if (matcher.find()) {
			matchStr = matcher.group(1);
		}
		return matchStr;
	}

	/**
	 * 匹配出价格的部分，并去除逗号，使用格式为.+?([\\d,\\.]+).*
	 * 
	 * @param input
	 * @return
	 */
	public static String getMoneyMatch(String input) {
		String regex = ".*?([-\\+\\d,\\.]+).*";
		return getMatchStr(regex, input).replace(",", "");
	}

	/**
	 * 匹配其中唯一数字部分，使用格式为.*?([\\d]+).*
	 * 
	 * @param input
	 * @return
	 */
	public static String getDigestMatch(String input) {
		String regex = ".*?([\\d]+).*";
		return getMatchStr(regex, input);
	}

	/**
	 * 根据关键字的正则表达式匹配
	 * 
	 * @param input
	 *            需要匹配的字符串,如“是否支持变现：否 ”
	 * @param matchReg
	 *            匹配关键字的正则表达式，如"[是否]"
	 * @return
	 */
	public static String getSpecifyContent(String input, String matchReg) {
		String regex = ".*?：\\s*?(" + matchReg + ")\\s*";
		return getMatchStr(regex, input);
	}

	/**
	 * 判断时间是否已经显示出来，若显示显示出来必存在数字
	 * 
	 * @param time
	 * @return
	 */
	public static boolean isTimeDisplay(String time) {
		String regex = "\\d+";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(time);
		return matcher.find();
	}

	/**
	 * 获得界面上的时间格式
	 * 
	 * @param time
	 * @return
	 */
	public static String getTimeFormat(String time) {
		String reg1 = "\\d+秒\\d+分\\d+时\\d+天";
		String reg2 = "\\d+秒\\d+分\\d+时";
		String reg3 = "\\d+秒\\d+分";
		String reg4 = "\\d+秒";
		String format = null;
		Pattern pattern1 = Pattern.compile(reg1);
		Matcher matcher1 = pattern1.matcher(time);
		if (matcher1.find()) {
			format = "ss秒mm分HH时dd天";
		} else {
			Pattern pattern2 = Pattern.compile(reg2);
			Matcher matcher2 = pattern2.matcher(time);
			if (matcher2.find()) {
				format = "ss秒mm分HH时";
			} else {
				Pattern pattern3 = Pattern.compile(reg3);
				Matcher matcher3 = pattern3.matcher(time);
				if (matcher3.find()) {
					format = "ss秒mm分";
				} else {
					Pattern pattern4 = Pattern.compile(reg4);
					Matcher matcher4 = pattern4.matcher(time);
					if (matcher4.find())
						format = "ss秒";
				}
			}
		}
		return format;
	}

}
