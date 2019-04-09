package com.autotest.utility;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.autotest.enums.DateType;

/**
 * String工具类
 * 
 * @author XUjj
 * 
 */
public class StringUtils {

	/**
	 * 判断字符串是否为空
	 * 
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
		boolean isEmpty = true;// 默认为空
		if (null != string && string.length() > 0) {
			isEmpty = false;
		}
		return isEmpty;
	}

	/**
	 * 比较2个数字类型的字符串内容是否相同
	 * 
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static boolean numIsEquals(String num1, String num2) {
		boolean isEquals = false;
		double n1 = Double.parseDouble(num1);
		double n2 = Double.parseDouble(num2);
		isEquals = MathUtil.retain2Decimal(n1) == MathUtil.retain2Decimal(n2);
		return isEquals;
	}

	/**
	 * 比较2个字符串内容是否相同
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static boolean isEquals(String str1, String str2) {
		boolean isEquals = false;
		if (null != str1 && null != str2) {
			if (str1.equals(str2)) {
				isEquals = true;
			}
		}
		return isEquals;
	}

	/**
	 * 比较2个字符串内容是否相同
	 * 
	 * @param str1
	 * @param str2
	 * @param logger
	 * @return
	 */
	public static boolean isEquals(String str1, String str2, Logger logger) {
		boolean isEquals = false;
		if (null != str1 && null != str2) {
			if (str1.equals(str2)) {
				isEquals = true;
				logger.info("期望值:" + str1 + ";与实际值:" + str2 + "相等！");
			} else {
				logger.error("期望值:" + str1 + ";与实际值:" + str2 + "不等！");
			}
		}
		return isEquals;
	}

	/**
	 * 判断期望值与实际值不等
	 * 
	 * @param srcMap
	 *            期望的值
	 * @param destMap
	 *            界面上获取的值
	 * @param logger
	 * @return
	 */
	public static boolean isEquals(Map<String, String> srcMap,
			Map<String, String> destMap, Logger logger) {
		boolean flag = false;
		boolean finalFlag = true;
		Set<String> srcSet = srcMap.keySet();
		Set<String> destSet = destMap.keySet();
		for (String dkey : destSet) {
			if (srcMap.containsKey(dkey)) {
				for (String skey : srcSet) {
					if (skey.equalsIgnoreCase(dkey)) {
						String expectedStr = srcMap.get(skey);
						String str = destMap.get(dkey);
						if (isEquals(expectedStr, str)) {
							logger.info(skey + "期望值:" + expectedStr + ";与实际值:"
									+ str + "相等！");
							flag = true;
						} else {
							logger.error(skey + "期望值:" + expectedStr + ";与实际值:"
									+ str + "不等！");
							flag = false;
						}
					}
				}

			} else {
				logger.error("期望值中不存在" + dkey + "字段");
			}
			finalFlag = finalFlag & flag;
		}
		return finalFlag;
	}

	/**
	 * 判断期望值与实际值是否相等
	 * 
	 * @param srcMap
	 * @param destMap
	 * @param logger
	 * @return
	 */
	public static boolean isEquals2(Map<String, String> srcMap,
			Map<String, String> destMap, Logger logger) {
		boolean flag = false;
		boolean finalFlag = true;
		Set<String> srcSet = srcMap.keySet();
		Set<String> destSet = destMap.keySet();
		for (String dkey : destSet) {
			if (srcMap.containsKey(dkey)) {
				for (String skey : srcSet) {
					if (skey.equalsIgnoreCase(dkey)) {
						String expectedStr = srcMap.get(skey);
						String str = destMap.get(dkey);
						if (isEquals(expectedStr, str)) {

							flag = true;
						} else {

							flag = false;
						}
					}
				}

			} else {
				logger.error("期望值中不存在" + dkey + "字段");
			}
			finalFlag = finalFlag & flag;
		}
		return finalFlag;
	}

	/**
	 * 比较两个list
	 * 
	 * @param compLine
	 * @param queryDB
	 * @param dbLines
	 * @param showLines
	 * @param log
	 * @return
	 */
	public static boolean compareList(List<Map<String, String>> dbLines,
			List<Map<String, String>> showLines, Logger log) {
		int i = 0;
		int j = 0;
		boolean result = false;
		boolean finalres = true;
		for (i = 0; i < showLines.size(); i++) {
			for (j = 0; j < dbLines.size(); j++) {
				if (StringUtils
						.isEquals2(dbLines.get(j), showLines.get(i), log)) {
					log.info("期望值" + dbLines.get(j) + "；与实际值："
							+ showLines.get(i) + "相等！");
					result = true;
					dbLines.remove(j);
					break;
				} else {
					result = false;
				}
			}
			if (result == false) {
				log.error("期望值列表中不存在实际值：" + showLines.get(i));
				finalres = result;
			}

		}
		return finalres;
	}

	/**
	 * 格式化字符串(去除空格、换行符、制表符)
	 * 
	 * @param str
	 * @return
	 */
	public static String format(String str) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}

	/**
	 * 按照正则表达式来格式化字符串
	 * 
	 * @param str
	 *            需要格式化的字符串
	 * @param pattern
	 *            字符串形式的正则表达式
	 * @return
	 */
	public static String format(String str, String pattern) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		return m.replaceAll("");
	}

	/**
	 * 按照正则表达式来格式化字符串
	 * 
	 * @param str
	 *            需要格式化的字符串
	 * @param pattern
	 *            字符串形式的正则表达式
	 * @param replaceStr
	 *            替换的字符串
	 * @return
	 */
	public static String format(String str, String pattern, String replaceStr) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		return m.replaceAll(replaceStr);
	}

	/**
	 * 格式化HTML字符串
	 * 
	 * @return
	 */
	public static String formatHTML(String htmlStr) {
		// 定义script的正则表达式
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>";
		// 定义style的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>";
		// 定义HTML标签的正则表达式
		String regEx_html = "<[^>]+>";

		Pattern p_script = Pattern.compile(regEx_script,
				Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		// 过滤script标签
		htmlStr = m_script.replaceAll("");

		Pattern p_style = Pattern
				.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		// 过滤style标签
		htmlStr = m_style.replaceAll("");

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		// 过滤html标签
		htmlStr = m_html.replaceAll("");

		// 返回文本字符串
		return htmlStr.trim();
	}

	/**
	 * 将double类型的数据转换成字符串，并且保留两位有效数字，如5000变成5000.00
	 * 
	 * @param data
	 * @return
	 */
	public static String double2Str(double data) {
		String str = "";
		if (data == 0) {
			str = "0.00";
		} else {
			DecimalFormat df = new DecimalFormat("###0.00");
			str = df.format(data);
		}
		return str;
	}

	/**
	 * string类型的数据添加逗号
	 * 
	 * @param str
	 * @return
	 */
	public static String strWithComma(String str) {
		double data = Double.parseDouble(str);
		DecimalFormat df = new DecimalFormat("#,###.00");
		String result = df.format(data);
		return result;
	}

	/**
	 * 将int类型的数据转换成以每3位用逗号分隔
	 * 
	 * @param data
	 * @return
	 */
	public static String int2Str(int data) {
		DecimalFormat df = new DecimalFormat("#,###");
		String str = df.format(data);
		return str;
	}

	/**
	 * 将字符串中逗号去除，并转换成double类型 如5,000.00变成5000.0
	 * 
	 * @param str
	 * @return
	 */
	public static double str2Double(String str) {
		String str2 = StringUtils.format(str, ",");
		return Double.parseDouble(str2);
	}

	/**
	 * 获取日期型字符串的单位(如:180天，返回day)
	 * 
	 * @param dateStr
	 * @return
	 */
	public static DateType getStrDateType(String dateStr) {
		DateType dateType = null;
		if (dateStr.indexOf("天") > 0 && dateStr.indexOf("年") == -1
				&& dateStr.indexOf("月") == -1) {
			dateType = DateType.day;
		} else if (dateStr.indexOf("月") > 0 && dateStr.indexOf("年") == -1
				&& dateStr.indexOf("天") == -1) {
			dateType = DateType.month;
		} else if (dateStr.indexOf("年") > 0 && dateStr.indexOf("天") == -1
				&& dateStr.indexOf("月") == -1) {
			dateType = DateType.year;
		}
		return dateType;
	}

	/**
	 * 获取字符串中的数字
	 * 
	 * @param str
	 * @return
	 */
	public static String getNum(String str) {
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * Utf8URL编码
	 * 
	 * @param s
	 * @return
	 */
	public static String Utf8URLencode(String text) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c >= 0 && c <= 255) {
				result.append(c);
			} else {
				byte[] b = new byte[0];
				try {
					b = Character.toString(c).getBytes("UTF-8");
				} catch (Exception ex) {
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0)
						k += 256;
					result.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return result.toString();
	}

	/**
	 * 判断两个字符串，长的字符串中是否包含短的字符串的内容。比如判断“4个月”包含“4月”
	 * 
	 * @param str1
	 * @param str2
	 * @return boolean
	 */
	public static boolean isContain(String str1, String str2, Logger log) {
		boolean res = true;
		String str_shorter = str1.length() < str2.length() ? str1 : str2;
		String str_longer = str1.length() >= str2.length() ? str1 : str2;
		for (int i = 0; i < str_shorter.length(); i++) {
			char tmp = str_shorter.toCharArray()[i];
			CharSequence seq = tmp + "";
			if (str_longer.contains(seq)) {
			} else {
				res = false;
				break;
			}
		}
		if (res) {
			log.info("字符串校验成功：“" + str_longer + "”包含“" + str_shorter + "”");
		} else {
			log.info("字符串校验失败：“" + str_longer + "”不包含“" + str_shorter + "”");
		}
		return res;
	}

	/**
	 * 判断两个字符串List内容是否一致
	 * 
	 * @param list
	 * @param list2
	 * @return boolean
	 */
	public static boolean checkContentSame(List<String> list,
			List<String> list2, Logger log) {
		boolean finalres = true;
		if (list.size() == list2.size()) {
			log.info("两个List长度相同");
		} else {
			log.info("两个List长度不相同");
			finalres = false;
		}
		for (int i = 0; i < list.size(); i++) {
			System.out.println("要查找：" + list.get(i));
			boolean isfind = false;
			for (int j = 0; j < list2.size(); j++) {
				if (list.get(i).equals(list2.get(j))) {
					isfind = true;
					log.info("找到" + list.get(i));
					list2.remove(j);
					break;
				}
			}
			finalres = finalres & isfind;
			if (isfind) {
				continue;
			} else {
				log.error("没有找到" + list.get(i));
			}
		}
		return finalres;
	}

}
