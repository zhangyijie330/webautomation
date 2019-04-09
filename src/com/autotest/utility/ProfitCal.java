package com.autotest.utility;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.autotest.dao.ProductDao;
import com.autotest.enums.DateType;

/**
 * 收益计算
 * 
 * @author sunlingyun
 * 
 */
public class ProfitCal {
	private Logger log = null;

	public ProfitCal(Logger log) {
		this.log = log;
	}

	/**
	 * 到期还付本息，基础利息与募集期利息之和
	 * 
	 * @param investEnd
	 *            融资截止时间
	 * @param amount
	 *            投资数量
	 * @param annualRate
	 *            年利率
	 * @param profitStartDate
	 *            起息日
	 * @param repayDate
	 *            还款日
	 * @param log
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static double calTotalProfit(String investEnd, double amount,
			double annualRate, String repayDate, Logger log)
			throws ParseException, IOException {
		// 募集期利息和基础利息
		double collectProfit = calCollectProfit(investEnd, amount, annualRate,
				log);
		double baseProfit = calBaseProfit(amount, annualRate, investEnd,
				repayDate);
		double income = collectProfit + baseProfit;

		return income;

	}

	/**
	 * 按月付息到期还款，基础利息与募集期利息之和
	 * 
	 * @param investEnd
	 *            融资截止时间
	 * @param amount
	 *            投资数量
	 * @param annualRate
	 *            年利率
	 * @param profitStartDate
	 *            起息日
	 * @param repayDate
	 *            还款日
	 * @param log
	 * @return
	 * @throws Exception
	 */
	public static double calTotalProfitForProPerMonth(String investEnd,
			double amount, double annualRate, String repayDate, int term,
			DateType dateType, Logger log) throws Exception {
		// 募集期利息和基础利息
		double collectProfit = calCollectProfit(investEnd, amount, annualRate,
				log);
		double baseProfit = calBaseProfitForProPerMon(amount, annualRate,
				investEnd, repayDate, term, dateType);
		double income = collectProfit + baseProfit;

		return income;

	}

	/**
	 * 速兑通逾期收益计算
	 * 
	 * @param investEnd
	 * @param amount
	 * @param annualRate
	 * @param profitStartDate
	 * @param repayDate
	 * @param log
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static double calTotalProfitCash(double amount, double annualRate,
			String investEndTime, String repayDate, Logger log)
			throws ParseException, IOException {
		// 募集期利息和基础利息
		// double collectProfit = calCollectProfit(investEnd, amount,
		// annualRate,
		// log);
		double income = calBaseProfit(amount, annualRate, investEndTime,
				repayDate);

		return income;

	}

	/**
	 * 到期还付本息，募集期利息
	 * 
	 * @param investEnd
	 *            融资截止时间
	 * @param amount
	 *            投资金额/融资规模
	 * @param annualRate
	 *            年利率
	 * @param log
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static double calCollectProfit(String investEnd, double amount,
			double annualRate, Logger log) throws IOException, ParseException {
		String investDate = SSHUtil.sshCurrentDate(log);
		// 募集期需要计算的利息天数
		int differDays = DateUtils.differDays2(investDate, investEnd);
		// 募集期利息
		double income = new BigDecimal(amount)
				.multiply(new BigDecimal(differDays))
				.multiply(new BigDecimal(annualRate))
				.divide(new BigDecimal(365), 2, BigDecimal.ROUND_DOWN)
				.doubleValue();
		return income;
	}

	/**
	 * 到期还付本息，募集期利息
	 * 
	 * @param investStart
	 *            开标时间
	 * @param investEnd
	 *            融资截止时间
	 * @param amount
	 *            投资金额/融资规模
	 * @param annualRate
	 *            年利率
	 * @param log
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static double calCollectProfit2(String investStart,
			String investEnd, double amount, double annualRate, Logger log)
			throws IOException, ParseException {
		// 募集期需要计算的利息天数
		int differDays = DateUtils.differDays2(investStart, investEnd);
		// 募集期利息
		double income = new BigDecimal(amount)
				.multiply(new BigDecimal(differDays))
				.multiply(new BigDecimal(annualRate))
				.divide(new BigDecimal(365), 2, BigDecimal.ROUND_DOWN)
				.doubleValue();
		return income;
	}

	/**
	 * 按月付息到期还款，总基本基本利息
	 * 
	 * @param amount
	 *            投资金额/融资规模
	 * @param annualRate
	 *            年利率
	 * @param investEndTime
	 *            融资截止时间
	 * @param repayDate
	 *            还款日
	 * @return
	 * @throws Exception
	 */
	public static double calBaseProfitForProPerMon(double amount,
			double annualRate, String investEndTime, String repayDate,
			int term, DateType dateType) throws Exception {

		// 基本利息计算
		String investEndDate = DateUtils.simpleDateFormat2(DateUtils
				.stringToDate(investEndTime));
		int dateUsed = DateUtils.differDays2(investEndDate, repayDate);
		BigDecimal income = new BigDecimal(amount)
				.multiply(new BigDecimal(dateUsed))
				.multiply(new BigDecimal(annualRate))
				.divide(new BigDecimal(365), 2, BigDecimal.ROUND_DOWN);
		double totalIncome = 0;
		int months = 0;
		switch (dateType) {
		case year:
			months = term * 12;
			totalIncome = income
					.divide(new BigDecimal(months), 2, BigDecimal.ROUND_HALF_UP)
					.multiply(new BigDecimal(months)).doubleValue();
			break;
		case month:
			months = term;
			totalIncome = income
					.divide(new BigDecimal(months), 2, BigDecimal.ROUND_HALF_UP)
					.multiply(new BigDecimal(months)).doubleValue();
			break;
		case day:
			throw new Exception("不接受日期类型为日");
		default:
			months = term;
			totalIncome = income
					.divide(new BigDecimal(months), 2, BigDecimal.ROUND_HALF_UP)
					.multiply(new BigDecimal(months)).doubleValue();
			break;
		}

		return totalIncome;
	}

	/**
	 * 到期还付本息，基本利息
	 * 
	 * @param amount
	 *            投资金额/融资规模
	 * @param annualRate
	 *            年利率
	 * @param investEndTime
	 *            融资截止时间
	 * @param repayDate
	 *            还款日
	 * @return
	 * @throws ParseException
	 */
	public static double calBaseProfit(double amount, double annualRate,
			String investEndTime, String repayDate) throws ParseException {
		// 基本利息计算
		String investEndDate = DateUtils.simpleDateFormat2(DateUtils
				.stringToDate(investEndTime));
		int dateUsed = DateUtils.differDays2(investEndDate, repayDate);
		double income = new BigDecimal(amount)
				.multiply(new BigDecimal(dateUsed))
				.multiply(new BigDecimal(annualRate))
				.divide(new BigDecimal(365), 2, BigDecimal.ROUND_DOWN)
				.doubleValue();
		return income;
	}

	/**
	 * 基本利息计算，日期类型必须为天
	 * 
	 * @param amount
	 * @param annualRate
	 * @param dateUsed
	 * @return
	 * @throws Exception
	 */
	public static double calBaseProfit2(double amount, int dateUsed,
			DateType dateType, double annualRate) throws Exception {
		double income = 0;
		switch (dateType) {
		case day:
			income = new BigDecimal(amount).multiply(new BigDecimal(dateUsed))
					.multiply(new BigDecimal(annualRate))
					.divide(new BigDecimal(365), 2, BigDecimal.ROUND_DOWN)
					.doubleValue();
			break;
		case month:
			throw new Exception("不接受日期类型为月");
		case year:
			throw new Exception("不接受日期类型为年");
		}
		return income;
	}

	/**
	 * 计算基础本息
	 * 
	 * @param amount
	 * @param dateUsed
	 * @param annualRate
	 * @return
	 * @throws ParseException
	 */
	public static double calCaptialAndProfit(double amount, double annualRate,
			String investEndTime, String repayDate) throws ParseException {
		// 基本利息
		double income = calBaseProfit(amount, annualRate, investEndTime,
				repayDate);
		return amount + income;

	}

	/**
	 * 计算展期利息
	 * 
	 * @param amount
	 * @param proName
	 * @return
	 * @throws Exception
	 */
	public static double getExtendProfit(String proName, double amount)
			throws Exception {
		Map<String, String> pro = ProductDao.getProByCode(proName);
		int extend = Integer.parseInt(pro.get("extend_time"));
		double rate = Double.parseDouble(pro.get("year_rate").split("%")[0]) / 100;
		return ProfitCal.calBaseProfit2(amount, extend, DateType.day, rate);
	}

	/**
	 * 万份收益计算
	 * 
	 * @param annualRate
	 *            年利率
	 * @param profitStartDate
	 *            起息日
	 * @param repayDate
	 *            还款日
	 * @return
	 * @throws ParseException
	 */
	public static double calProfit10Thous(double annualRate,
			String investEndTime, String repayDate) throws ParseException {
		double income = calBaseProfit(10000, annualRate, investEndTime,
				repayDate);
		return income;
	}

	/**
	 * 每月等额本息总利息计算
	 * 
	 * @param investEnd
	 *            融资截止时间
	 * @param annualRate
	 *            年利率
	 * @param capital
	 *            本金
	 * @param stage
	 *            总期数
	 * @throws Exception
	 */
	public static double calTotalProfitForEqualMonthly(String investEnd,
			double annualRate, double capital, int stage, int dateUsed,
			DateType dateType, Logger log) throws Exception {
		// 募集期收益
		double collectProfit = calCollectProfit(investEnd, capital, annualRate,
				log);
		// 前n-1期每期本息
		double repayMoney = getRepayPerM(annualRate, capital, stage, dateUsed,
				dateType);
		BigDecimal baseProfit = new BigDecimal(0);
		for (int i = 1; i <= stage - 1; i++) {
			List<Double> list = calForEqualMonthly(capital, annualRate / stage,
					i, repayMoney);
			baseProfit = baseProfit.add(new BigDecimal(list.get(0) + ""));
		}
		// 倒数第二期剩余本金
		double preRemainMoney = calForEqualMonthly(capital, annualRate / stage,
				stage - 1, repayMoney).get(2);
		// 最后一期利息
		double lastStageProfit = getRepayProfit(preRemainMoney, annualRate
				/ stage);

		baseProfit = baseProfit.add(new BigDecimal("" + lastStageProfit));
		double totalRepay = new BigDecimal(collectProfit).add(baseProfit)
				.doubleValue();

		return totalRepay;

	}

	/**
	 * 每月等额本息,获取前n-1期的应还本息金额
	 * 
	 * @param annualRate
	 * @param capital
	 * @param stage
	 * @param dateUsed
	 * @param dateType
	 * @return
	 * @throws Exception
	 */
	public static double getRepayPerM(double annualRate, double capital,
			int stage, int dateUsed, DateType dateType) throws Exception {
		double mRate = annualRate / 12;
		double mlimit = dateUsed;
		if (dateType == DateType.day) {
			throw new Exception("融资期必须至少是月份以上单位");
		}
		if (dateType == DateType.year) {
			mlimit = 12 * dateUsed;
		}
		// 每月还款本息
		double repayMoney = capital * mRate * Math.pow(mRate + 1, mlimit)
				/ (Math.pow(1 + mRate, mlimit) - 1);
		// 保留小数
		repayMoney = new BigDecimal(repayMoney).setScale(2,
				BigDecimal.ROUND_DOWN).doubleValue();

		return repayMoney;
	}

	/**
	 * 每月等额本息，计算最后一期利息、本金、本息
	 * 
	 * @param capital
	 *            总本金
	 * @param mRate
	 *            月利息
	 * @param preTotalMoney
	 *            上期的本息
	 * @param limitCount
	 *            总期数
	 * @return list 顺序为 0:利息 1：本金 2：本息
	 */
	public static List<Double> getLastStageTotalMoney(double capital,
			double mRate, double preTotalMoney, int limitCount) {
		List<Double> list = new ArrayList<Double>();
		// 倒数第二期的剩余总金额
		int preLimitNum = limitCount - 1;
		// double preRemianMoney = getRemainMoney(capital, mRate, preLimitNum,
		// preTotalMoney);
		double preRemainMoney = calForEqualMonthly(capital, mRate, preLimitNum,
				preTotalMoney).get(2);
		// 最后一期利息
		double currentProfit = getRepayProfit(preRemainMoney, mRate);
		// 最后一期的本金
		double currentCapital = preRemainMoney;

		// 最后一期本息
		double lastTotalMoney = currentProfit + currentCapital;
		list.add(currentProfit);
		list.add(currentCapital);
		list.add(lastTotalMoney);
		return list;
	}

	/**
	 * 每月等额本息某期利息
	 * 
	 * @param lastRemain
	 *            上期剩余
	 * @param mRate
	 *            月利率
	 * @return
	 */

	public static double getRepayProfit(double lastRemain, double mRate) {
		// 上期剩余金额
		// double remainMoney = calForEqualMonthly(capital, mRate, limitNum,
		// lastRepay);
		// 本期利息
		double currentProfit = new BigDecimal(lastRemain).multiply(
				new BigDecimal(mRate)).doubleValue();
		// 保留两位小数
		currentProfit = new BigDecimal(currentProfit).setScale(2,
				BigDecimal.ROUND_HALF_UP).doubleValue();
		return currentProfit;
	}

	/**
	 * 每月等额本息计算本期本金
	 * 
	 * @param totalMoney
	 * @param profit
	 * @return
	 */
	public static double getCurrentCapital(double totalMoney, double profit) {

		BigDecimal capitalDe = new BigDecimal(totalMoney)
				.subtract(new BigDecimal(profit));
		double capital = capitalDe.setScale(2, BigDecimal.ROUND_FLOOR)
				.doubleValue();
		return capital;
	}

	/**
	 * @author maoyinglan
	 * @param repayTime
	 * @return
	 * @throws ParseException
	 */
	public int residueTiem(String repayTime) throws ParseException {
		int residueTime = DateUtils.differDays2(DateUtils.getNowDateStr2(),
				repayTime);
		log.info("服务器获取当前时间" + DateUtils.getNowDateStr2());
		log.info("数据库还款时间" + repayTime);
		log.info("剩余时间" + residueTime);
		return residueTime;
	}

	/**
	 * 变现到期应付利息
	 * 
	 * @param money
	 * @param rate
	 * @param repayTime
	 * @return
	 * @throws Exception
	 */
	public double repayInterests(double money, double rate, int residueTime)
			throws Exception {

		// 计算到期应付利息
		double interests = money * rate * residueTime / 100 / 365;
		double interestsScale = new BigDecimal(interests).setScale(2,
				BigDecimal.ROUND_DOWN).doubleValue();
		return interestsScale;

	}

	/**
	 * 计算变现的平台管理费
	 * 
	 * @param repayTime
	 * @param residueTime
	 * @return
	 * @throws ParseException
	 */
	public double PlantFee(double money) throws ParseException {
		// 计算平台管理费
		double fee = money * 0.2 / 100;
		double feeScale = new BigDecimal(fee)
				.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
		return feeScale;
	}

	/**
	 * 每月等额本息，计算每期的利息、本金、剩余本金，除最后一期外的计算
	 * 
	 * @param capital
	 *            融资金额
	 * @param mRate
	 *            月利率
	 * @param limitNum
	 *            第x期
	 * @param repay
	 *            本期应还本息
	 * @return list 列表中第一个为本期利息，第二个为本期应还本金，第三个为剩余应还本金
	 */
	public static List<Double> calForEqualMonthly(double capital, double mRate,
			int limitNum, double repay) {
		List<Double> list = new ArrayList<Double>();
		BigDecimal remain = new BigDecimal(capital + "");
		BigDecimal profit = new BigDecimal(0);
		BigDecimal repayBD = new BigDecimal(repay + "");
		BigDecimal repayCapital = new BigDecimal(0);
		for (int i = 1; i <= limitNum; i++) {
			profit = remain.multiply(new BigDecimal(mRate)).setScale(2,
					BigDecimal.ROUND_HALF_UP);
			repayCapital = repayBD.subtract(profit).setScale(2,
					BigDecimal.ROUND_FLOOR);
			remain = remain.subtract(repayCapital).setScale(2,
					BigDecimal.ROUND_FLOOR);
		}
		list.add(profit.doubleValue());
		list.add(repayCapital.doubleValue());
		list.add(remain.doubleValue());
		return list;
	}

}
