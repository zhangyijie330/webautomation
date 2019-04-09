package com.autotest.service.xhhService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.autotest.dao.RepayPlanDao;
import com.autotest.driver.KeyWords;
import com.autotest.enums.DateType;
import com.autotest.enums.DiscountType;
import com.autotest.enums.RepayWay;
import com.autotest.or.ObjectLib;
import com.autotest.utility.DateUtils;
import com.autotest.utility.OrUtil;
import com.autotest.utility.PatternUtil;
import com.autotest.utility.ProfitCal;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * 投资理财界面
 * 
 * @author sunlingyun
 * 
 */
public class InvestFinanceService {
	private Logger	log			= null;
	private int		retryTimes	= 3;

	public InvestFinanceService(Logger log) {
		this.log = log;
	}

	/**
	 * 点击顶部导航拦【投资理财】入口
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewInvertListPage(KeyWords kw) throws Exception {
		kw.click(OrUtil
				.getBy("finance_invest_linkText", ObjectLib.XhhObjectLib));
		ThreadUtil.sleep(2);
	}

	/**
	 * 投资成功后点击查看账户，并校验跳转页面的title
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean viewAccountAndCheckPage(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("finance_viewAccount_linkText",
				ObjectLib.XhhObjectLib));
		kw.waitPageLoad();
		boolean flag = false;
		String expectedTitle = "账户总览";
		for (int i = 0; i < retryTimes; i++) {
			String title = kw.getTitle();
			if (StringUtils.isEquals(expectedTitle, title, log)) {
				flag = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * xhh-179:case11:访问【投资理财-投资项目-日益升】页面
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public boolean viewRysList(KeyWords kw) throws Exception {

		boolean flag = false;
		for (int i = 0; i < retryTimes; i++) {
			kw.click(OrUtil.getBy("finance_riyi_linkText",
					ObjectLib.XhhObjectLib));
			if ("日益升-鑫合汇".equals(kw.getTitle().trim())) {
				flag = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * 访问还款计划标签页
	 * 
	 * @param kw
	 */
	public void viewRepayPlan(KeyWords kw) throws Exception {
		kw.click(OrUtil
				.getBy("finance_repayPlan_xpath", ObjectLib.XhhObjectLib));

	}

	/**
	 * 访问月益升页面
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public boolean viewYysList(KeyWords kw) throws Exception {
		boolean flag = false;
		for (int i = 0; i < retryTimes; i++) {
			kw.click(OrUtil.getBy("finance_yueyi_linkText",
					ObjectLib.XhhObjectLib));
			if ("月益升-鑫合汇".equals(kw.getTitle().trim())) {
				flag = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * 校验还款描述信息
	 * 
	 * @param kw
	 * @param containText
	 * @return
	 * @throws Exception
	 */
	public boolean checkRepayPlanDesc(KeyWords kw, String containText)
			throws Exception {
		String text = kw.getText(OrUtil.getBy(
				"finance_description_cssSelector", ObjectLib.XhhObjectLib));
		boolean flag = text.contains(containText);

		return flag;
	}

	/**
	 * 查找对应的项目
	 * 
	 * @param kw
	 * @param productCode
	 * @return
	 * @throws Exception
	 */
	public boolean findProductByPaging(KeyWords kw, String productCode)
			throws Exception {
		String key = "//a[contains(text(),'" + productCode + "')]";
		boolean isExist = findKeyPaging(kw, key);
		return isExist;
	}

	/**
	 * 校验还款计划表指定行
	 * 
	 * @param kw
	 * @param rowIndex
	 * @param expMap
	 * @return
	 * @throws Exception
	 */
	public boolean checkRepayPlanByRow(KeyWords kw, int rowIndex,
			Map<String, String> expMap) throws Exception {
		// 获取表格元素
		List<WebElement> rows = kw.getWebElements(OrUtil.getBy(
				"finance_table_cssSelector", ObjectLib.XhhObjectLib));
		// 获取指定行
		WebElement row = rows.get(rowIndex - 1);
		List<WebElement> columns = row.findElements(By.tagName("td"));
		Map<String, String> map = new HashMap<String, String>();
		boolean flag1 = false;
		boolean flag2 = true;
		int index = 0;
		if (columns.size() == 8) {
			index = 1;
			// 款项
			String funds = columns.get(0).getText();
			flag2 = StringUtils.isEquals(expMap.get("fund"),
					StringUtils.format(funds), log);
		}
		// 期次
		String stage = StringUtils.format(columns.get(index).getText());
		map.put("stage", stage);
		// 还款日
		String repayDate = StringUtils.format(columns.get(index + 2).getText());
		map.put("repay_date", repayDate);
		// 应收本息（元）
		String totalMoney = StringUtils
				.format(columns.get(index + 3).getText()).replaceAll(",", "");
		map.put("total_money", totalMoney);
		// 应收利息（元）
		String profit = StringUtils.format(columns.get(index + 4).getText())
				.replaceAll(",", "");
		map.put("profit", profit);
		// 应收本金（元）
		String capital = StringUtils.format(columns.get(index + 5).getText())
				.replaceAll(",", "");
		map.put("capital", capital);
		// 剩余本金（元）
		String remainCapital = columns.get(index + 6).getText()
				.replaceAll(",", "");
		map.put("remain_capital", remainCapital);

		flag1 = StringUtils.isEquals(expMap, map, log);

		return flag1 & flag2;
	}

	/**
	 * 校验产品详情页中还款计划的每天还款信息和合计信息
	 * 
	 * @param kw
	 * @param productCode
	 * @return
	 * @throws Exception
	 */
	public boolean checkRepayPlan(KeyWords kw, String productCode)
			throws Exception {
		boolean flag1 = true;
		boolean flag2 = false;
		boolean flag3 = false;
		BigDecimal totalMoney = new BigDecimal(0);
		BigDecimal totalProfit = new BigDecimal(0);
		BigDecimal totalCapital = new BigDecimal(0);
		// 数据库获取还款计划列表
		List<Map<String, String>> expPlanList = RepayPlanDao
				.getPlanByCode(productCode);
		for (int i = 0; i < expPlanList.size(); i++) {
			// 按顺序拿不同期的信息
			Map<String, String> expMap = expPlanList.get(i);
			totalMoney = totalMoney.add(new BigDecimal(expMap
					.get("total_money")));
			totalProfit = totalProfit.add(new BigDecimal(expMap.get("profit")));
			totalCapital = totalCapital.add(new BigDecimal(expMap
					.get("capital")));
			// 如果是第一期，页面表格中存在“正常还款日期”列
			if (i == 0) {
				expMap.put("fund", "正常还款日期");
			}
			flag2 = checkRepayPlanByRow(kw, i + 2, expMap);
			flag1 = flag1 & flag2;
		}
		// 合计结果
		Map<String, String> totalExpMap = getRepayPlanTotalRow(totalMoney,
				totalProfit, totalCapital);
		flag3 = checkRepayPlanByRow(kw, expPlanList.size() + 2, totalExpMap);
		if (flag3) {
			log.info("【还款计划】合计数据校验成功");
		} else {
			log.error("【还款计划】合计数据校验失败");
		}
		return flag1 & flag3;
	}

	/**
	 * 校验产品剩余可购买余额
	 * 
	 * @param kw
	 * @param productCode
	 *            产品号
	 * @param checkStr
	 *            期望值
	 * @throws Exception
	 */
	public Boolean checkProductBalance(KeyWords kw, String productCode,
			String checkStr) throws Exception {
		boolean flag = false;
		// 产品余额的xpath根据产品号
		String balanceXpath = "//a[contains(text(),'"
				+ productCode
				+ "')]/ancestor::div[@class='proListLeft']/following-sibling::*/descendant::div[@class='propress-balance']";
		By by = By.xpath(balanceXpath);
		// 获得剩余总额
		String balanceValue = kw.getText(by);
		balanceValue = PatternUtil.getMoneyMatch(balanceValue);

		flag = StringUtils.isEquals(checkStr, balanceValue, log);
		return flag;
	}

	/**
	 * 返回产品剩余可购买百分比
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public Boolean checkProgressRate(KeyWords kw, String productCode,
			String checkStr) throws Exception {
		boolean flag = false;
		// 产品余额的xpath根据产品号
		String rateXpath = "//a[contains(text(),'"
				+ productCode
				+ "')]/ancestor::div[@class='proListLeft']/following-sibling::*/descendant::em[@class='propress-data']";
		By by = By.xpath(rateXpath);
		String rateVal = kw.getText(by);
		rateVal = PatternUtil.getDigestMatch(rateVal);

		// 与期望值比较
		flag = StringUtils.isEquals(checkStr, rateVal, log);
		return flag;
	}

	/**
	 * 
	 * @param kw
	 * @param InvestEndTime
	 *            融资截止时间
	 * @return
	 * @throws Exception
	 */
	public Boolean checkInvestRemainTime(KeyWords kw, String investEndTime,
			int accpectDiffSec) throws Exception {
		boolean flag = false;
		// 获得服务器当前时间
		String currentDate = SSHUtil.sshCurrentTime(log);
		// 获得界面上融资剩余时间
		String investRemainTime = null;
		// 等待，直到时间出现
		int waitTime = 0;
		while (true) {
			investRemainTime = getProductRemainTime(kw);
			if (PatternUtil.isTimeDisplay(investRemainTime)) {
				break;
			} else {
				ThreadUtil.sleep();
				waitTime++;
			}
			if (waitTime > 60) {
				break;
			}
		}
		investRemainTime = StringUtils.format(investRemainTime);
		// 获得服务器时间与融资截止时间差
		String format = PatternUtil.getTimeFormat(investRemainTime);
		String dfferTime = DateUtils.differTime2String(currentDate,
				investEndTime, format);
		// 比较程序计算的时间与界面上获取的时间差，如果在accpectDiffSec时间内，为可接受误差
		flag = DateUtils.isDifferAccept(dfferTime, investRemainTime,
				accpectDiffSec, format);
		return flag;
	}

	/**
	 * 在产品详情页面返回目前已完成募集金额
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getProductCollect(KeyWords kw) throws Exception {
		String productCollect = kw.getText(OrUtil.getBy(
				"finance_productCollection_xpath", ObjectLib.XhhObjectLib));
		return productCollect;
	}

	/**
	 * xhh-223:详情页部分字段校验
	 * 
	 * @param kw
	 * @param expectedMap
	 * @param InvestAmount
	 *            默认的投资金额
	 * @param checkInvestAmount
	 *            是否校验默认投资金额
	 * @param checkCollect
	 *            是否校验已募集金额
	 * @param checkIncomePer10Thous
	 *            是否校验每万份收益
	 * @param checkRemainAmount
	 *            是否校验剩余金额
	 * @param checkEndTime
	 *            是否校验融资截止时间
	 * @param checkRate
	 *            是否校验年化率
	 * @param checkMinInvestAmount
	 *            是否校验起投金额
	 * @param checkStepInvestAmount
	 *            是否校验递增金额
	 * @param checkMaxInvestAmount
	 *            是否校验最高限额
	 * @param checkAmount
	 *            是否校验融资规模
	 * @param checkGuardWay
	 *            是否校验保障方式
	 * @param checkProfitStart
	 *            是否校验起息日
	 * @param checkDateUsed
	 *            是否校验项目期限
	 * @param checkRepayType
	 *            是否校验还款方式
	 * @param checkRepayDate
	 *            是否校验还款日期
	 * @param checkSupportCash
	 *            是否校验支持变现
	 * @param checkEarlyClose
	 *            是否校验提前技术募集
	 * @param checkExtend
	 *            是否校验支持展期
	 * @param checkEarlyRepay
	 *            是否校验提前还款
	 * @param checkAutoRatio
	 *            是否校验可自动投标比例
	 * @param checkAcctBalance
	 *            是否校验账户余额
	 * @param checkProfit
	 *            是否校验预期收益
	 * @return
	 * @throws Exception
	 */
	public Boolean checkMapInProDetail(KeyWords kw,
			Map<String, String> expProMap, Map<String, String> expAcctMap,
			boolean checkInvestAmount, boolean checkCollect,
			boolean checkIncomePer10Thous, boolean checkRemainAmount,
			boolean checkEndTime, boolean checkRate,
			boolean checkMinInvestAmount, boolean checkStepInvestAmount,
			boolean checkMaxInvestAmount, boolean checkAmount,
			boolean checkGuardWay, boolean checkProfitStart,
			boolean checkDateUsed, boolean checkRepayType,
			boolean checkRepayDate, boolean checkSupportCash,
			boolean checkEarlyClose, boolean checkExtend,
			boolean checkEarlyRepay, boolean checkAutoRatio,
			boolean checkAcctBalance, boolean checkProfit, RepayWay repayWay,
			DiscountType discountType) throws Exception {
		boolean flag = false;
		boolean flag2 = true;
		boolean flag3 = true;
		Map<String, String> map = new HashMap<String, String>();
		String expInvestAmount = expAcctMap.get("balance");
		String expRemainAmt = expProMap.get("balance");
		String expFinalRepayDate = expProMap.get("repay_date");
		String expProfitStartDate = expProMap.get("profit_start_date");
		String investEndTime = expProMap.get("end_bid_time");
		// 用款期限
		String dateUsedStr = expProMap.get("term");
		// 用款类型
		DateType dateType = StringUtils.getStrDateType(dateUsedStr);
		// 已完成募集
		if (checkCollect) {
			String productCollect = PatternUtil
					.getMoneyMatch(getProductCollect(kw));
			map.put("collect", productCollect);
		}
		// 项目名下第二行的收益起始日期
		if (checkProfitStart) {
			String incomeStartdate = getProductIncomeStartdate(kw);
			if (!StringUtils.isEquals(expProfitStartDate, incomeStartdate, log)) {
				log.error("【项目详情页面】项目名下第二行收益起始日期校验失败");
				flag3 = false;
			}
		}

		// 每万份收益
		if (checkIncomePer10Thous) {
			String incomePer10Thous = PatternUtil
					.getMoneyMatch(getProductIncomePerMill(kw));
			map.put("profit_10thous", incomePer10Thous);
		}

		// 剩余可投金额
		if (checkRemainAmount) {
			String remainAmount = PatternUtil
					.getMoneyMatch(getProductRemainAmount(kw));
			map.put("balance", remainAmount);
		}

		// 融资截止时间
		if (checkEndTime) {
			if (checkInvestRemainTime(kw, investEndTime, 10)) {
				log.info("【详情页面】融资截止时间校验成功");
			} else {
				flag2 = false;
				log.error("【详情页面】融资截止时间校验失败");
			}
		}

		// 年化收益率
		if (checkRate) {
			String rate = kw.getText(OrUtil.getBy("finance_rate_cssSelector",
					ObjectLib.XhhObjectLib));
			map.put("year_rate", rate);
		}
		// 起投金额
		if (checkMinInvestAmount) {
			String minAmount = PatternUtil.getMoneyMatch(kw.getText(OrUtil
					.getBy("finance_minAmount_xpath", ObjectLib.XhhObjectLib)));
			map.put("min_bid_amount", minAmount);
		}
		// 递增金额
		if (checkStepInvestAmount) {
			String stepBidAmount = PatternUtil.getMoneyMatch(kw.getText(OrUtil
					.getBy("finance_stepBidAmount_xpath",
							ObjectLib.XhhObjectLib)));
			map.put("step_bid_amount", stepBidAmount);
		}
		// 最高限额
		if (checkMaxInvestAmount) {
			String maxAmount = PatternUtil.getMoneyMatch(kw.getText(OrUtil
					.getBy("finance_maxAmount_xpath", ObjectLib.XhhObjectLib)));
			if (maxAmount.equals("不限")) {
				maxAmount = "";
			}
			map.put("max_bid_amount", maxAmount);
		}

		// 融资规模（万元）
		if (checkAmount) {
			String amtStr = kw.getText(OrUtil.getBy("finance_amount_xpath",
					ObjectLib.XhhObjectLib));
			int ratio = 1;
			if (amtStr.contains("万")) {
				ratio = 10000;
			}
			String amount = PatternUtil.getSpecifyContent(amtStr, "\\S+");

			amount = StringUtils.double2Str(Double.parseDouble(PatternUtil
					.getMoneyMatch(amount)) * ratio);
			map.put("demand_amount", amount);

		}
		// 保障方式
		if (checkGuardWay) {
			String safeguardWay = PatternUtil.getSpecifyContent(kw
					.getText(OrUtil.getBy("finance_safeguardWay_xpath",
							ObjectLib.XhhObjectLib)), "\\S+");
			map.put("safe_guard_way", safeguardWay);
		}
		// 起息日期
		if (checkProfitStart) {
			// 根据T+0或者T+1的起息日，投资日期加起息日计算
			String startProfitDays = expProMap.get("start_profit_date");
			int days = Integer.parseInt(startProfitDays);
			Date investDate = DateUtils.stringToDate2(SSHUtil
					.sshCurrentDate(log));
			String expProfStartDate = DateUtils.simpleDateFormat2(DateUtils
					.dayAddDays(investDate, days));
			String profitStart = PatternUtil.getSpecifyContent(kw
					.getText(OrUtil.getBy("finance_profitStart2_xpath",
							ObjectLib.XhhObjectLib)), "\\S+");
			map.put("current_profit_start_date", profitStart);
			expProMap.put("current_profit_start_date", expProfStartDate);
		}

		// 项目期限
		if (checkDateUsed) {
			String term = StringUtils.format(kw.getText(OrUtil.getBy(
					"finance_dateUsed_cssSelector", ObjectLib.XhhObjectLib)));
			if (dateType == DateType.month)
				term = StringUtils.format(term.replace("个", ""));
			map.put("term", term);
		}
		// 还款方式
		if (checkRepayType) {
			String repayType = kw.getText(OrUtil.getBy(
					"finance_repayType_cssSelector", ObjectLib.XhhObjectLib));
			map.put("repay_way", repayType);
		}
		// 还款日,获得还款计划第一期的还款日期
		String expRepayDate = RepayPlanDao.getRepayPlanByCodeStage(
				expProMap.get("prj_name"), 1).get("repay_date");
		expProMap.put("repay_date_stage1", expRepayDate);
		if (checkRepayDate) {
			String repayDate = PatternUtil.getSpecifyContent(kw.getText(OrUtil
					.getBy("finance_reayDate_xpath", ObjectLib.XhhObjectLib)),
					"\\S+");
			map.put("repay_date_stage1", repayDate);
		}
		// 是否支持变现
		if (checkSupportCash) {
			String supportCash = PatternUtil.getSpecifyContent(kw
					.getText(OrUtil.getBy("finance_suppertCash_xpath",
							ObjectLib.XhhObjectLib)), "\\S+");
			map.put("is_transfer", supportCash);
		}
		// 可提前结束募集
		if (checkEarlyClose) {
			String isEarlyClose = PatternUtil.getSpecifyContent(kw
					.getText(OrUtil.getBy("finance_isEarlyClose_xpath",
							ObjectLib.XhhObjectLib)), "\\S+");
			map.put("is_early_close", isEarlyClose);
		}
		// 是否允许展期
		if (checkExtend) {
			String isExtend = PatternUtil.getSpecifyContent(kw.getText(OrUtil
					.getBy("finanace_isExtend_xpath", ObjectLib.XhhObjectLib)),
					"\\S+");
			map.put("is_extend", isExtend);
		}
		// 是否允许提前还款
		if (checkEarlyRepay) {
			String isEarlyRepay = PatternUtil.getSpecifyContent(kw
					.getText(OrUtil.getBy("finance_isEarlyRepay_xpath",
							ObjectLib.XhhObjectLib)), "\\S+");
			map.put("is_early_repay", isEarlyRepay);
		}
		// 可自动投标比例
		if (checkAutoRatio) {
			String appointPrjRatio = PatternUtil.getDigestMatch(PatternUtil
					.getSpecifyContent(kw.getText(OrUtil.getBy(
							"finance_appointPrjRatio_xpath",
							ObjectLib.XhhObjectLib)), "\\S+"));
			map.put("appoint_prj_ratio", appointPrjRatio);
		}

		// 判断是否需要使用红包或满减券,在有红包的情况下使用红包，没有红包时在有满减券的情况下使用满减券
		double discountAmount = 0;
		double expInvAmtDouble = Double.parseDouble(expInvestAmount);
		double bonusNotUse = Double
				.parseDouble(expAcctMap.get("bonus_not_use"));
		double counponsNotUse = Double.parseDouble(expAcctMap
				.get("coupons_not_use"));
		// 用红包时账户余额与投资金额均加上红包的金额，用满减券时只有投资金额加上满减券的金额
		switch (discountType) {
			case bonus:
				discountAmount = bonusNotUse;
				expInvAmtDouble = Double.parseDouble(expInvestAmount)
						+ discountAmount;
				break;
			case coupons:
				discountAmount = counponsNotUse;
				expInvAmtDouble = Double.parseDouble(expInvestAmount)
						+ discountAmount;
				break;
			case none:
				break;
			default:
				break;
		}
		// 账户余额
		if (checkAcctBalance) {
			String acctBalance = PatternUtil
					.getMoneyMatch(kw.getText(OrUtil
							.getBy("finance_acctBalance_xpath",
									ObjectLib.XhhObjectLib)));
			expProMap.put("acct_balance", expInvestAmount);
			map.put("acct_balance", acctBalance);
		}

		// 投资金额
		if (checkInvestAmount) {
			// 界面上投资金额的值
			String investAmtVal = kw.getAttribute(OrUtil.getBy(
					"finance_investAmountInput_name", ObjectLib.XhhObjectLib),
					"value");
			// 去除逗号
			String investAmount = StringUtils.double2Str(StringUtils
					.str2Double(investAmtVal));

			double expRemAmtDouble = Double.parseDouble(expRemainAmt);

			// 如果用户剩余金额小于产品剩余金额,默认投资金额显示用户剩余金额，否则显示产品剩余金额
			if (expInvAmtDouble <= expRemAmtDouble) {
				expProMap.put("invest_amount",
						StringUtils.double2Str(expInvAmtDouble));
			} else {
				expProMap.put("invest_amount",
						StringUtils.double2Str(expRemAmtDouble));
				expInvAmtDouble = expRemAmtDouble;
			}
			map.put("invest_amount", investAmount);
			// 若需要校验预计收益
			if (checkProfit) {

				int dateUsed = Integer.parseInt(PatternUtil
						.getDigestMatch(dateUsedStr));
				String annualRate = PatternUtil.getMoneyMatch(expProMap
						.get("year_rate"));

				double expIncome = 0;
				// 根据还款方式计算预期收益
				switch (repayWay) {
					case E:
						expIncome = ProfitCal.calTotalProfit(investEndTime,
								expInvAmtDouble,
								Double.parseDouble(annualRate) / 100,
								expFinalRepayDate, log);
						break;
					case D:
						expIncome = ProfitCal.calTotalProfitForProPerMonth(
								investEndTime, expInvAmtDouble,
								Double.parseDouble(annualRate) / 100,
								expFinalRepayDate, dateUsed, dateType, log);

						break;
					case permonth:
						int stage = 0;
						switch (dateType) {
							case month:
								stage = dateUsed;
								break;
							case year:
								stage = 12 * dateUsed;
								break;
							default:
								log.error("每月等额本息不期望出现的期限类型");
								break;
						}
						expIncome = ProfitCal
								.calTotalProfitForEqualMonthly(investEndTime,
										Double.parseDouble(annualRate) / 100,
										expInvAmtDouble, stage, dateUsed,
										dateType, log);
						break;
					default:
						break;
				}
				expProMap.put("income", StringUtils.double2Str(expIncome));
				String income = PatternUtil.getMoneyMatch(kw.getText(OrUtil
						.getBy("finance_income_id", ObjectLib.XhhObjectLib)));
				map.put("income", income);

			}
		}

		flag = StringUtils.isEquals(expProMap, map, log);
		return flag & flag2 & flag3;
	}

	/**
	 * 在详情页面获得收益起始日期
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getProductIncomeStartdate(KeyWords kw) throws Exception {
		// 获取页面上“即刻投资收益自xxx起算，每万份收益xxx元”
		String string = kw.getText(OrUtil.getBy(
				"finance_incomeStartdate_xpath", ObjectLib.XhhObjectLib));
		// 使用正则表达式进行匹配，获得收益起始日期
		String regex = "即刻投资收益自 (.+?) 起算";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(string);
		String incomeStartdate = null;
		if (matcher.find()) {

			incomeStartdate = matcher.group(1);
		}
		return incomeStartdate;
	}

	/**
	 * 在详情页面获得每万份收益
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getProductIncomePerMill(KeyWords kw) throws Exception {
		String incomePerMill = kw.getText(OrUtil.getBy(
				"finance_perMillIncome_xpath", ObjectLib.XhhObjectLib));
		return incomePerMill;
	}

	/**
	 * 在详情页获得剩余可投金额
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getProductRemainAmount(KeyWords kw) throws Exception {
		String remainAmount = kw.getText(OrUtil.getBy(
				"finance_remainAmount_className", ObjectLib.XhhObjectLib));
		return remainAmount;
	}

	/**
	 * 在详情页获得剩余投资时间
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getProductRemainTime(KeyWords kw) throws Exception {
		String remainTime = kw.getText(OrUtil.getBy(
				"finance_remainTime_className", ObjectLib.XhhObjectLib));
		return remainTime;
	}

	/**
	 * 校验投资确认页面数据 校验信息包含： 1. 预估收益， 2. 投资金额， 3. 使用账户余额。
	 * 
	 * @param kw
	 * @param expectedMap
	 * @return
	 * @throws Exception
	 */
	public Boolean checkMapInConfirmPay(KeyWords kw,
			Map<String, String> expectedMap, DiscountType discountType)
			throws Exception {
		boolean flag = false;
		Map<String, String> map = new HashMap<String, String>();
		// 获得预估收益
		String income = kw.getText(OrUtil.getBy("finance_income_id",
				ObjectLib.XhhObjectLib));
		// 等待，直到收益发生变化,最多等待10s
		int waitTime = 0;
		while (true) {
			if (waitTime > 10)
				break;
			if (StringUtils.isEquals(expectedMap.get("expect_income"), income)) {
				break;
			} else {
				ThreadUtil.sleep();
				waitTime++;
				income = PatternUtil.getMoneyMatch(kw.getText(OrUtil.getBy(
						"finance_income_id", ObjectLib.XhhObjectLib)));
			}
		}
		String investAmount = null;
		// 获得投资金额，多次查找
		for (int i = 0; i < retryTimes; i++) {
			if (kw.isElementExist2(OrUtil.getBy("finance_investMoney_xpath",
					ObjectLib.XhhObjectLib))) {
				investAmount = StringUtils.double2Str(StringUtils.str2Double(kw
						.getText(OrUtil.getBy("finance_investMoney_xpath",
								ObjectLib.XhhObjectLib))));
				break;
			} else {
				ThreadUtil.sleep();
			}
		}

		// 获得使用账户余额
		String accountPay = StringUtils.double2Str(Double.parseDouble(kw
				.getText(OrUtil.getBy("finance_accountPay_xpath",
						ObjectLib.XhhObjectLib))));

		map.put("expect_income", income);
		map.put("invest_amount", investAmount);
		map.put("acct_pay", accountPay);

		// 校验红包或者满减券
		String discountAmt = "";
		switch (discountType) {
			case bonus:
				discountAmt = StringUtils.double2Str(StringUtils.str2Double(kw
						.getText(OrUtil.getBy("finance_use_bonus_xpath",
								ObjectLib.XhhObjectLib))));
				map.put("bonus_not_use", discountAmt);
				break;
			case coupons:
				discountAmt = StringUtils.double2Str(StringUtils.str2Double(kw
						.getText(OrUtil.getBy("finance_use_coupons_xpath",
								ObjectLib.XhhObjectLib))));
				map.put("coupons_not_use", discountAmt);

				break;
			case none:
				break;
			default:
				break;
		}
		// 与期望值校验
		flag = StringUtils.isEquals(expectedMap, map, log);
		return flag;
	}

	/**
	 * 获取预期收益、投资金额、账户使用金额、红包或满减券
	 * 
	 * @param dateUsedStr
	 *            用款期限
	 * @param annualRateStr
	 *            年化收益
	 * @param investAmount
	 *            投资金额
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getExpConfmPayMap(
			Map<String, String> expProductInfo,
			Map<String, String> expAcctInfo, double investAmount,
			RepayWay repayWay, DiscountType discountType) throws Exception {

		// 用款期限
		String dateUsedStr = expProductInfo.get("term");
		// 用款期限类型
		DateType dateType = StringUtils.getStrDateType(dateUsedStr);
		// 只含数字的用款期限
		int dateUsed = Integer
				.parseInt(PatternUtil.getDigestMatch(dateUsedStr));
		// 年利率
		String annualRateStr = PatternUtil.getMoneyMatch(expProductInfo
				.get("year_rate"));
		double annualRate = Double.parseDouble(annualRateStr);
		// 融资截止时间
		String investEnd = expProductInfo.get("end_bid_time");
		// 还款日
		String expFinalRepayDate = expProductInfo.get("repay_date");
		// // 起息日
		// String expProfitStartDate = expProductInfo.get("profit_start_date");
		// 确认投资页面期望校验的map申明
		Map<String, String> expConfmPayMap = new HashMap<String, String>();
		// 根据投资金额计算预期收益,不同还款方式选择不同的收益计算方法
		double income = 0;
		switch (repayWay) {
			case C:
				income = ProfitCal.calBaseProfit2(investAmount, dateUsed,
						dateType, annualRate / 100);
				break;
			case E:
				income = ProfitCal.calTotalProfit(investEnd, investAmount,
						annualRate / 100, expFinalRepayDate, log);
				break;
			case D:
				income = ProfitCal.calTotalProfitForProPerMonth(investEnd,
						investAmount, annualRate / 100, expFinalRepayDate,
						dateUsed, dateType, log);
				break;
			case permonth:
				int stage = 0;
				switch (dateType) {
					case month:
						stage = dateUsed;
						break;
					case year:
						stage = 12 * dateUsed;
						break;
					default:
						log.error("每月等额本息不期望出现的期限类型");
						break;
				}
				income = ProfitCal.calTotalProfitForEqualMonthly(investEnd,
						annualRate / 100, investAmount, stage, dateUsed,
						dateType, log);
				break;
			default:
				break;
		}

		// 判断是否需要使用红包或满减券,在有红包的情况下使用红包，没有红包时在有满减券的情况下使用满减券
		double discountAmount = 0;
		double expInvAmtDouble = investAmount;
		double expAcctDouble = 0;
		double bonusNotUse = Double.parseDouble(expAcctInfo
				.get("bonus_not_use"));
		double counponsNotUse = Double.parseDouble(expAcctInfo
				.get("coupons_not_use"));
		// 用红包时账户余额与投资金额均加上红包的金额，用满减券时只有投资金额加上满减券的金额

		switch (discountType) {
			case bonus:
				discountAmount = bonusNotUse;
				expConfmPayMap.put("bonus_not_use",
						StringUtils.double2Str(bonusNotUse));
				break;
			case coupons:
				discountAmount = counponsNotUse;
				expConfmPayMap.put("counpons_not_use",
						StringUtils.double2Str(counponsNotUse));
				break;
			default:
				break;
		}

		expAcctDouble = expInvAmtDouble - discountAmount;
		// 存入map

		expConfmPayMap.put("expect_income", StringUtils.double2Str(income));
		expConfmPayMap.put("invest_amount",
				StringUtils.double2Str(investAmount));
		expConfmPayMap.put("acct_pay", StringUtils.double2Str(expAcctDouble));
		return expConfmPayMap;
	}

	/**
	 * 在投资金额框中输入投资金额与密码
	 * 
	 * @param kw
	 * @param value
	 * @throws Exception
	 */
	public void setAmountAndPWd(KeyWords kw, String amount, String pwd)
			throws Exception {
		By amountBy = OrUtil.getBy("finance_investAmountInput_name",
				ObjectLib.XhhObjectLib);
		By pwdBy = OrUtil.getBy("finance_payPwd_name", ObjectLib.XhhObjectLib);
		kw.setValue(amountBy, amount);
		kw.click(pwdBy);
		kw.setValue(pwdBy, pwd);
	}

	/**
	 * xhh-221:用户访问投资详情页面 点击立即投资访问投资详情页
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void selectInvestProduct(KeyWords kw, String productCode)

	throws Exception {
		String productDetailXpath = "//a[contains(text(),'"
				+ productCode
				+ "')]/ancestor::div[@class='proListLeft']/following-sibling::*/descendant::a[contains(text(),'立即投资')]";
		System.out.println("productDetailXpath=[" + productDetailXpath + "]");
		By by = By.xpath(productDetailXpath);
		kw.click(by);
		ThreadUtil.sleep(3);

	}

	/**
	 * xhh-224:用户投资，确认支付并校验是否跳转至投资成功
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean payConfirmAndCheckSuccess(KeyWords kw) throws Exception {
		// 点击确认支付
		for (int i = 0; i < 10; i++) {
			if (kw.isElementExist(OrUtil.getBy("finance_confirmPay_className",
					ObjectLib.XhhObjectLib))) {
				kw.click(OrUtil.getBy("finance_confirmPay_className",
						ObjectLib.XhhObjectLib));
				break;
			}
		}

		Boolean flag1 = false;
		Boolean flag2 = false;
		kw.waitPageLoad();
		// 获得窗口title并与期望值比较

		ThreadUtil.sleep(3);
		// String title = kw.getTitle();
		String expectedTitle = "我要理财-投资成功";
		for (int i = 0; i < retryTimes; i++) {
			String title = kw.getTitle();
			if (StringUtils.isEquals(expectedTitle, title, log)) {
				flag1 = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}

		// 获得页面中的投资成功
		String investSucess = kw.getText(OrUtil.getBy(
				"finance_investSucess_cssSelector", ObjectLib.XhhObjectLib));
		String expectedSucessStr = "投资成功！";
		if (StringUtils.isEquals(expectedSucessStr, investSucess, log)) {
			flag2 = true;
		}
		log.info("flag1=[" + flag1 + "];flag2=[" + flag2 + "]");
		return flag1 & flag2;
	}

	/**
	 * 在投资成功后的页面校验起息日和预计还款时间
	 * 
	 * @param kw
	 * @param expProfStartDate
	 *            期望的起息日
	 * @param checkProfitStartDate
	 *            是否校验起息日
	 * @param expRepayTime
	 *            期望的预期还款时间
	 * @param checkRepayTime
	 *            是否校验预计还款时间
	 * @return
	 * @throws Exception
	 */
	public boolean checkInfoInIvstSuccPage(KeyWords kw, String investEndDate,
			String startProfitDays, boolean checkProfitStartDate,
			String expRepayTime, boolean checkRepayTime) throws Exception {
		boolean flag1 = true;
		boolean flag2 = true;
		// 起息日
		String profitStart = kw.getText(OrUtil.getBy(
				"finance_profitStart_xpath", ObjectLib.XhhObjectLib));
		// 预计还款时间
		String repayTime = kw.getText(OrUtil.getBy("finance_repay_xpath",
				ObjectLib.XhhObjectLib));
		// 转换日期格式为yyyy年MM月dd日
		int days = Integer.parseInt(startProfitDays);
		Date investEnd = DateUtils.stringToDate(investEndDate);
		String expProfStartDate = DateUtils.formatDate("yyyy年MM月dd日",
				DateUtils.dayAddDays(investEnd, days));
		if (checkProfitStartDate) {
			if (StringUtils.isEquals(expProfStartDate, profitStart, log)) {
				flag1 = true;
				log.info("【投资成功页面】起息日校验成功");
			} else {
				flag1 = false;
				log.error("【投资成功页面】起息日校验失败");
			}
		} else {
			log.warn("【投资成功页面】起息日未校验");
			flag1 = true;
		}
		// 转换预计还款时间日期格式
		expRepayTime = DateUtils.formatDate("yyyy年MM月dd日",
				DateUtils.stringToDate2(expRepayTime))
				+ " 16:00-24:00";
		if (checkRepayTime) {
			if (StringUtils.isEquals(expRepayTime, repayTime, log)) {
				flag2 = true;
				log.info("【投资成功页面】预计还款时间校验成功");
			} else {
				flag2 = false;
				log.error("【投资成功页面】预计还款时间校验失败");
			}
		} else {
			log.warn("【投资成功页面】预计还款时间未校验");
			flag2 = true;
		}

		return flag1 & flag2;
	}

	/**
	 * 获得还款计划中预期募集期信息
	 * 
	 * @param investEnd
	 * @param investAmount
	 * @return
	 * @throws SQLException
	 */
	public Map<String, String> getRepayPlanCollectRow(String productCode,
			String demandAmount) throws SQLException {
		Map<String, String> firstRepayPlan = RepayPlanDao
				.getRepayPlanByCodeStage(productCode, 1);
		String repayDate = firstRepayPlan.get("repay_date");
		Map<String, String> expMap = new HashMap<String, String>();
		expMap.put("fund", "募集期资金占用费");
		expMap.put("stage", "");
		expMap.put("repay_date", repayDate);
		expMap.put("total_money", "实际金额以融资截止日显示为准");
		expMap.put("profit", "实际金额以融资截止日显示为准");
		expMap.put("capital", "-");
		expMap.put("remain_capital", demandAmount);

		return expMap;
	}

	/**
	 * 获得还款计划表的预期合计信息
	 * 
	 * @param investAmount
	 * @return
	 */
	public Map<String, String> getRepayPlanTotalRow(BigDecimal totalMoney,
			BigDecimal profit, BigDecimal capital) {
		Map<String, String> expMap = new HashMap<String, String>();

		expMap.put("fund", "合计");
		expMap.put("stage", "");
		expMap.put("repay_date", "");
		expMap.put("total_money",
				StringUtils.double2Str(totalMoney.doubleValue()));
		expMap.put("profit", StringUtils.double2Str(profit.doubleValue()));
		expMap.put("capital", StringUtils.double2Str(capital.doubleValue()));
		expMap.put("remain_capital", "");
		return expMap;
	}

	/**
	 * 对使用优惠券情况进行判断
	 * 
	 * @param acctMap
	 * @return
	 */
	public DiscountType useBonusOrCoupons(Map<String, String> acctMap) {
		boolean useBonus = false;
		boolean useCoupons = false;
		DiscountType type = DiscountType.none;
		double bonusNotUse = Double.parseDouble(acctMap.get("bonus_not_use"));
		double counponsNotUse = Double.parseDouble(acctMap
				.get("coupons_not_use"));
		if (bonusNotUse != 0) {
			useBonus = true;
		} else if (counponsNotUse != 0) {
			useCoupons = true;
		}
		if (useBonus) {
			type = DiscountType.bonus;
		} else if (useCoupons) {
			type = DiscountType.coupons;
		}
		log.info("type=[" + type + "]");
		return type;

	}

	/**
	 * 翻页查找指定元素，直到翻完所有页面
	 * 
	 * @param kw
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public boolean findKeyPaging(KeyWords kw, String key) throws Exception {
		boolean isExist = false;
		isExist = kw.isElementExist(By.xpath(key));
		// 若不存在，则往下翻页，翻找所有页数直到找到项目
		if (!isExist) {
			// 获得总页数
			int pageCount = Integer.parseInt(kw.getText(OrUtil.getBy(
					"finance_pageCount_xpath", ObjectLib.XhhObjectLib)));
			for (int i = 1; i < pageCount; i++) {
				kw.click(OrUtil.getBy("finance_nextPage_xpath",
						ObjectLib.XhhObjectLib));
				isExist = kw.isElementExist(By.xpath(key));
				if (isExist) {
					break;
				}
			}

		}

		return isExist;
	}
}
