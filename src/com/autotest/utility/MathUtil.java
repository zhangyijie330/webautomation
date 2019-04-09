package com.autotest.utility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

/**
 * 数学函数封装类
 * 
 * @author wb0002
 * 
 */
public class MathUtil {

	/**
	 * 生产1-9的随机数
	 * 
	 * @return
	 */
	public static int getRandom() {
		int rand = 0;
		while (rand == 0) {
			rand = (int) (random() * 10);
		}
		return rand;
	}

	/**
	 * 生产0-9的随机数
	 * 
	 * @return
	 */
	public static int getRandomAll() {
		int rand = 0;
		rand = (int) (random() * 10);
		return rand;
	}

	/**
	 * 生产0-num之间的随机数
	 * 
	 * @return
	 */
	public static int getRandomAll(int num) {
		int rand = 0;
		Random random = new Random();
		rand = random.nextInt(num);
		return rand;
	}

	/**
	 * 生产beginNum-endNum之间的随机数
	 * 
	 * @param beginNum
	 * @param endNum
	 * @return
	 */
	public static int getRandomAll(int beginNum, int endNum) {
		int rand = 0;
		Random random = new Random();
		rand = random.nextInt(endNum) + beginNum;
		return rand;
	}

	/**
	 * 生产X位的随机数 如:size=2，就是生产10-99之间的随机数;size=3，就是生产100-999之间的随机数
	 * 
	 * @param size
	 * @return
	 */
	public static int getRandom(int size) {
		int rand = 0;
		switch (size) {
		case 1:
			rand = getRandomAll();
			break;
		default:
			for (int i = 1; i <= size; i++) {
				int temp_rand;
				if (i == 1) {
					temp_rand = getRandomAll();
				} else {
					temp_rand = getRandom() * (int) Math.pow(10, i - 1);
				}
				rand = rand + temp_rand;
			}
			break;
		}
		return rand;
	}

	private static double random() {
		return Math.random();
	}

	/**
	 * 保留0位小数(四舍五入)
	 * 
	 * @param d
	 * @return
	 */
	public static double retain0Decimal(double d) {
		BigDecimal bigDecimal = new BigDecimal(d);
		return bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 保留0位小数
	 * 
	 * @param d
	 * @return
	 */
	public static double retain0Decimal2(double d) {
		BigDecimal bigDecimal = new BigDecimal(d);
		return bigDecimal.setScale(0, BigDecimal.ROUND_DOWN).doubleValue();
	}

	/**
	 * 保留两位小数(四舍五入)
	 * 
	 * @param d
	 * @return
	 */
	public static double retain2Decimal(double d) {
		BigDecimal bigDecimal = new BigDecimal(d);
		return bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 使用银行家算法，保留2位小数，不进行四舍五入(推荐，精度极高)
	 * 
	 * @param number
	 * @return
	 */
	public static BigDecimal roundD(double number) {
		BigDecimal bigDecimal = new BigDecimal(number);
		return bigDecimal.setScale(2, BigDecimal.ROUND_DOWN);
	}

	/**
	 * 保留两位小数(五舍六入)
	 * 
	 * @param d
	 * @return
	 */
	public static double retain2Decimal2(double d) {
		DecimalFormat DecimalFormat = new DecimalFormat("#.00");
		return Double.parseDouble(DecimalFormat.format(d));
	}

	/**
	 * 保留两位小数(四舍五入)
	 * 
	 * @param d
	 * @return
	 */
	public static double retain2Decimal3(double d) {
		NumberFormat numberFormat = NumberFormat.getNumberInstance();
		numberFormat.setMaximumFractionDigits(2);
		return Double.parseDouble(numberFormat.format(d));
	}

	/**
	 * 手机号是由三部分组成的。前三个数代表者运行商，比如第一部分（1-3）150.151.159等是中国移动，153.133.189是中国电信。
	 * 131.132
	 * .155这是中国联通。第二部分（4-8）是代表着地区，比如0635山东是聊城，0997是新疆阿克苏，0531山东济南，0991新疆乌鲁木齐
	 * 。第三部份（9-12）才是用户的号码。
	 */
	/**
	 * 获取手机号
	 * 
	 * @return
	 */
	public static String getMobile() {
		String mobile = null;
		String[] operatorType = { "133", "142", "144", "146", "148", "149", "153", "180", "181", "189", "130", "131",
				"132", "141", "143", "145", "155", "156", "185", "186", "134", "135", "136", "137", "138", "139", "140",
				"147", "150", "151", "152", "157", "158", "159", "182", "183", "187", "188" };
		int index = getRandomAll(operatorType.length - 1);
		String begin = operatorType[index];// 手机号开头
		String code = getRandom(8) + "";
		mobile = begin + code;
		if (mobile.length() > 11) {
			return null;
		}
		return mobile;
	}

	/**
	 * 保留2位小数(四舍五入)
	 * 
	 * @param num
	 * @return
	 */
	public static String round(double num) {
		BigDecimal bigDecimal = new BigDecimal(num);
		String result = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString();
		return result;
	}

	/**
	 * 保留2位小数(直接舍弃)
	 * 
	 * @param num
	 * @return
	 */
	public static String round2(double num) {
		BigDecimal bigDecimal = new BigDecimal(num);
		String result = bigDecimal.setScale(2, BigDecimal.ROUND_DOWN).toPlainString();
		return result;
	}

	/**
	 * 格式化金额
	 * 
	 * @param str
	 * @return
	 */
	public static double format(String str) {
		str = str.replace(",", "").replaceAll("元", "");
		int a = 1;
		if (str.indexOf("万") >= 0) {
			str = str.replaceAll("万", "");
			a = 10000;
		}
		double num = Double.parseDouble(str);
		num = num * a;
		return num;
	}

	/**
	 * 
	 * 功能：将金额格式化成保留2位小数形式
	 * 
	 * @author xudashen
	 * @param d
	 * @return double
	 * @date 2016年9月1日 上午9:56:43
	 */
	public static String format2(double d) {
		String pattrn = "##0.00";
		DecimalFormat formater = new DecimalFormat(pattrn);
		formater.setRoundingMode(RoundingMode.DOWN);
		String dd = formater.format(d);
		return dd;
	}

	/**
	 * 保留X位小数，并指定舍入模式和分组模式
	 * 
	 * @param number
	 *            需要保留小数的数值
	 * @param scale
	 *            精度，保留多少位小数
	 * @param groupingSize
	 *            分组大小，比如=3时，表示按千分位分组;如：1,000.00;注意：=0时为不分组
	 * @param roundingMode
	 *            保留小数时的舍入模式
	 * @return String
	 */
	public static String roundUseDecimalFormat(double number, int scale, int groupingSize,
			java.math.RoundingMode roundingMode) {
		DecimalFormat formater = new DecimalFormat();
		formater.setMaximumFractionDigits(scale);
		formater.setGroupingSize(groupingSize);// 设置分组大小;0时为不分组
		// formater.setGroupingUsed(false);//禁用分组设置
		formater.setRoundingMode(roundingMode);// 设置舍入模式
		return formater.format(number);
	}

	/**
	 * 将金额格式化成千分位格式，并指定小数位数，以及舍入模式
	 * 
	 * @param number
	 * @param scale
	 *            精度，保留多少位小数
	 * @param roundingMode
	 *            保留小数时的舍入模式
	 * @return
	 */
	public static String formatToThousands(double number, int scale, java.math.RoundingMode roundingMode) {
		return roundUseDecimalFormat(number, scale, 3, roundingMode);
	}

	/**
	 * 将金额格式化成千分位格式(默认最多保留6位小数，四舍五入)
	 * 
	 * @param number
	 *            需要保留小数的数值
	 * @return String
	 */
	public static String formatToThousands(double number) {
		return formatToThousands(number, 6, java.math.RoundingMode.HALF_UP);
	}

	/**
	 * 功能：比较字符串类型的金额是否相等
	 * 
	 * @author xudashen
	 * @param num1
	 * @param num2
	 * @return boolean
	 * @date 2016年9月7日 上午9:10:30
	 */
	public static boolean equals(String num1, String num2) {
		return Double.parseDouble(num1) == Double.parseDouble(num2);
	}

}
