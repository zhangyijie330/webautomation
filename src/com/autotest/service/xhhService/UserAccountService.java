package com.autotest.service.xhhService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.autotest.dao.AccountDao;
import com.autotest.dao.FinanceRecordDao;
import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.driver.KeyWords;
import com.autotest.enums.FinanceType;
import com.autotest.enums.Key;
import com.autotest.or.ObjectLib;
import com.autotest.utility.OrUtil;
import com.autotest.utility.PatternUtil;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * 用户个人中心界面
 * 
 * @author sunlingyun
 * 
 */
public class UserAccountService {

	private Logger log = null;

	public UserAccountService(Logger log) {
		this.log = log;
	}

	/**
	 * 访问我的账户页面
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewUserAccountIndex(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("account_index_cssSelector",
				ObjectLib.XhhObjectLib));
	}

	/**
	 * 访问我的账户中中的投资管理页面
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewFinaceManagePage(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("account_financeManage_linkText",
				ObjectLib.XhhObjectLib));
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
		if (isExist) {
			return isExist;
		} else {
			if (kw.isElementExist2(OrUtil.getBy("account_pageCount_xpath",
					ObjectLib.XhhObjectLib))) {
				// 获得总页数
				int pageCount = Integer.parseInt(kw.getText(OrUtil.getBy(
						"account_pageCount_xpath", ObjectLib.XhhObjectLib)));
				// 翻找所有页数直到找到项目
				for (int i = 1; i < pageCount; i++) {
					kw.click(OrUtil.getBy("account_nextPage_xpath",
							ObjectLib.XhhObjectLib));
					isExist = kw.isElementExist(By.xpath(key));
					if (isExist) {
						break;
					}
				}

			}

		}

		return isExist;
	}

	/**
	 * 访问我的奖励并校验满减券、红包、加息券
	 * 
	 * @param kw
	 * @param expMap
	 * @return
	 * @throws Exception
	 */
	public boolean viewAndCheckCouponList(KeyWords kw,
			Map<String, String> expMap) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		// 点击用户中心
		viewUserCenter(kw);
		// 点击我的奖励
		kw.click(OrUtil.getBy("account_myCouponsList_linkText",
				ObjectLib.XhhObjectLib));
		// 点击满减券
		kw.click(OrUtil.getBy("account_centerCoupons_xpath",
				ObjectLib.XhhObjectLib));
		// 已使用
		String couponNotUse = PatternUtil
				.getMoneyMatch(kw.getText(OrUtil.getBy(
						"account_notUse_partialLinkText",
						ObjectLib.XhhObjectLib)));
		map.put("coupons_not_use", couponNotUse);
		// 已使用
		String couponUsed = PatternUtil.getMoneyMatch(kw.getText(OrUtil.getBy(
				"account_used_partialLinkText", ObjectLib.XhhObjectLib)));
		map.put("coupons_used", couponUsed);
		// 过期
		String couponExpire = PatternUtil
				.getMoneyMatch(kw.getText(OrUtil.getBy(
						"account_expire_partialLinkText",
						ObjectLib.XhhObjectLib)));
		map.put("coupons_expire", couponExpire);

		// 点击红包
		kw.click(OrUtil.getBy("account_centerBonus_xpath",
				ObjectLib.XhhObjectLib));
		// 可使用
		String bonusNotUse = PatternUtil.getMoneyMatch(kw.getText(OrUtil.getBy(
				"account_canUse_partialLinkText", ObjectLib.XhhObjectLib)));
		map.put("bonus_not_use", bonusNotUse);
		// 已使用
		String bonusUsed = PatternUtil.getMoneyMatch(kw.getText(OrUtil.getBy(
				"account_used_partialLinkText", ObjectLib.XhhObjectLib)));
		map.put("bonus_used", bonusUsed);
		// 过期
		String bonusExpire = PatternUtil.getMoneyMatch(kw.getText(OrUtil.getBy(
				"account_expire_partialLinkText", ObjectLib.XhhObjectLib)));
		map.put("bonus_expire", bonusExpire);

		// 点击加息券
		kw.click(OrUtil.getBy("account_centerRateCoupons_xpath",
				ObjectLib.XhhObjectLib));
		// 已使用
		String rateCouponsNotUse = PatternUtil
				.getDigestMatch(kw.getText(OrUtil.getBy(
						"account_notUse_partialLinkText",
						ObjectLib.XhhObjectLib)));
		map.put("rate_coupons_not_use", rateCouponsNotUse);
		// 已使用
		String rateCouponsUsed = PatternUtil
				.getDigestMatch(kw.getText(OrUtil
						.getBy("account_used2_partialLinkText",
								ObjectLib.XhhObjectLib)));
		map.put("rate_coupons_used", rateCouponsUsed);
		// 过期
		String rateCouponsExpire = PatternUtil
				.getDigestMatch(kw.getText(OrUtil.getBy(
						"account_expire_partialLinkText",
						ObjectLib.XhhObjectLib)));
		map.put("rate_coupons_expire", rateCouponsExpire);

		// 与期望值校验
		boolean flag = StringUtils.isEquals(expMap, map, log);
		return flag;
	}

	/**
	 * 访问我的账户下的用户中心
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewUserCenter(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("account_userCenter_linkText",
				ObjectLib.XhhObjectLib));
	}

	/**
	 * 投资理财页面点击所有
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewAllType(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("account_allInvertRecord_linkText",
				ObjectLib.XhhObjectLib));
	}

	/**
	 * 点击资金记录页面查看
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewPayRecord(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("account_payRecord_linkText",
				ObjectLib.XhhObjectLib));
		ThreadUtil.sleep(5);
	}

	/**
	 * 
	 * 检查理财记录列表数据,访问个人中心投资管理页面中的理财记录/资金记录/变现借款，在所有中校验是否有记录
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean checkNoRecordExist(KeyWords kw) throws Exception {
		return kw.isElementExist(OrUtil.getBy("account_noRecord_xpath",
				ObjectLib.XhhObjectLib));
	}

	/**
	 * 访问个人中心投资管理页面中的理财记录，在所有中校验是否有理财记录
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean checkNoInvertRecordExist(KeyWords kw) throws Exception {
		// 点击理财记录
		kw.click(OrUtil.getBy("account_financeRecord_linkText",
				ObjectLib.XhhObjectLib));
		// 点击【所有】入口
		kw.click(OrUtil.getBy("account_allInvertRecord_linkText",
				ObjectLib.XhhObjectLib));
		return checkNoRecordExist(kw);
	}

	/**
	 * 访问个人中心投资管理页面中的资金记录，在所有中校验是否有资金记录
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean checkNoPayRecordExist(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("account_payRecord_linkText",
				ObjectLib.XhhObjectLib));
		kw.click(OrUtil.getBy("account_payRecordDatail_linkText",
				ObjectLib.XhhObjectLib));
		kw.click(OrUtil.getBy("account_payRecordAll_linkText",
				ObjectLib.XhhObjectLib));
		return checkNoRecordExist(kw);
	}

	/**
	 * 
	 * 校验理财记录页面的表格字段值，校验字段包含： 1. 项目 2. 还款日期 3. 投资金额 4. 收益 5. 状态
	 * 
	 * @param kw
	 * @param productCode
	 * @param expectedMap
	 * @return
	 * @throws Exception
	 */
	public Boolean checkValInInvestRecord(KeyWords kw, String productCode,
			Map<String, String> expectedMap) throws Exception {
		boolean flag = false;
		Map<String, String> map = new HashMap<String, String>();

		// 项目
		String productXpath = "//a[contains(text(),'" + productCode + "')]";
		if (findKeyPaging(kw, productXpath)) {
			String productNameVal = PatternUtil.getMatchStr(".+?(\\w+)",
					kw.getText(By.xpath(productXpath)));
			// 还款日期xpath
			String repayXpath = "//a[contains(text(),'" + productCode
					+ "')]/ancestor::td/preceding-sibling::td/p";
			String repayVal = kw.getText(By.xpath(repayXpath));

			// 投资金额xpath
			String investAmountXpath = "//a[contains(text(),'"
					+ productCode
					+ "')]/ancestor::td/following-sibling::td/span[@class='orange']";
			String investAmountStr = kw.getText(By.xpath(investAmountXpath));
			String amountVal = PatternUtil.getMoneyMatch(investAmountStr);
			// 收益xpath
			String incomeXpath = "//a[contains(text(),'"
					+ productCode
					+ "')]/ancestor::td/following-sibling::td/div[@class='prel envelopes_parent']";
			String incomeVal = PatternUtil.getMoneyMatch(kw.getText(By
					.xpath(incomeXpath)));

			// 成交时间xpath
			/*
			 * String dealTimeXpath = "//a[contains(text(),'" + productCode +
			 * "')]/ancestor::td/following-sibling::td/p"; String dealTimeVal =
			 * kw.getText(By.xpath(dealTimeXpath));
			 */

			// 状态xpath
			String statusXpath = "//a[contains(text(),'" + productCode
					+ "')]/ancestor::td/following-sibling::td[4]";
			String statusVal = kw.getText(By.xpath(statusXpath));

			// 如果已还款则还款日期中会出现还款完成
			if (expectedMap.get("status").equals("已还款结束")) {
				repayVal = StringUtils.format(repayVal);
				expectedMap.put("repay_date_desc",
						expectedMap.get("repay_date") + "还款完成");
				map.put("repay_date_desc", repayVal);
			} else {
				map.put("repay_date", repayVal);
			}
			map.put("code", productNameVal);
			map.put("invest_amount", amountVal);
			map.put("profit", incomeVal);
			map.put("status", statusVal);

			// 与期望值校验
			flag = StringUtils.isEquals(expectedMap, map, log);
		} else {
			log.error("【理财记录】未找到项目" + productCode);
			flag = false;
		}
		return flag;

	}

	/**
	 * 校验资金记录表格字段值，校验信息包含： 1. 摘要 2. 时间 3. 类型 4. 交易金额 5. 账户余额
	 * 
	 * @param kw
	 * @param productCode
	 *            产品编号
	 * @param expectedMap
	 * @param type
	 *            投资类型
	 * @return
	 * @throws Exception
	 */
	public Boolean checkValInPayRecord(KeyWords kw, String productCode,
			Map<String, String> expectedMap, FinanceType type) throws Exception {
		boolean flag = false;

		Map<String, String> map = new HashMap<String, String>();
		String financeType = getFinanceType(type);
		// 摘要
		String summaryXpath = "//td[contains(text(),'" + productCode
				+ "')][(contains(text(),'" + financeType + "'))]";
		if (findKeyPaging(kw, summaryXpath)) {
			String summaryVal = kw.getText(By.xpath(summaryXpath));
			// 时间
			// String timeXpath =
			// "//td[contains(text(),'"+productCode+"')]/preceding-sibling::td/span[@class='time pl10']";
			// String timeVal = kw.getText(By.xpath(timeXpath));
			// 类型
			String typeXpath = "//td[contains(text(),'" + productCode
					+ "')][(contains(text(),'" + financeType
					+ "'))]/preceding-sibling::td[3]";
			String typeVal = kw.getText(By.xpath(typeXpath));
			// 交易金额
			String payAmountXpath = "//td[contains(text(),'" + productCode
					+ "')][(contains(text(),'" + financeType
					+ "'))]/preceding-sibling::td/em";
			if (type == FinanceType.invest || type == FinanceType.fee
					|| type == FinanceType.withdraw) {
				payAmountXpath = "//td[contains(text(),'" + productCode
						+ "')][(contains(text(),'" + financeType
						+ "'))]/preceding-sibling::td/div[@class='green']";
			}
			String payAmountVal = PatternUtil.getMoneyMatch(kw.getText(By
					.xpath(payAmountXpath)));

			// 账户余额
			String acctRemainXpath = "//td[contains(text(),'" + productCode
					+ "')][(contains(text(),'" + financeType
					+ "'))]/preceding-sibling::td[1]";
			String acctRemainVal = PatternUtil.getMoneyMatch(kw.getText(By
					.xpath(acctRemainXpath)));

			map.put("abstract", summaryVal);
			map.put("finance_type", typeVal);
			map.put("amount", payAmountVal);
			map.put("acct_balance", acctRemainVal);

			// 与期望值校验
			flag = StringUtils.isEquals(expectedMap, map, log);

		} else {
			log.error("【资金记录】未找到项目" + productCode);
			flag = false;
		}
		return flag;
	}

	/**
	 * xhh-211:case5:用户登陆成功后，查看【账户总览】页面数据,校验【账户总览】页面字段值 校验信息包含： 1. 账户净资产 2. 账户余额
	 * 3. 我的投资 4. 变现借款 5. 预期总收益 6. 待收收益 7. 已赚收益 8. 满减券 9. 红包 10. 加息券 11. 邀请好友
	 * 
	 * @param kw
	 * @param checkMap
	 * @param log
	 * @return
	 * @throws Exception
	 */
	public Boolean checkAccountInvestVal(KeyWords kw,
			Map<String, String> checkMap) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
		boolean flag = false;
		String xpathStr = "//a[text()='账户总览']";
		kw.click(By.xpath(xpathStr));
		ThreadUtil.sleep();
		kw.waitPageLoad();
		ThreadUtil.sleep();
		for (int i = 0; i < 10; i++) {
			try {
				if (kw.isElementExist(OrUtil.getBy("importantinfo_xpath",
						ObjectLib.XhhObjectLib))) {
					kw.click(OrUtil.getBy("importantinfo_xpath",
							ObjectLib.XhhObjectLib));
					break;
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
			ThreadUtil.sleep();
		}
		// 获取账户净资产
		String asset = kw.getText(OrUtil.getBy("account_asset_xpath",
				ObjectLib.XhhObjectLib));
		String assetVal = PatternUtil.getMoneyMatch(asset);

		// 获取账户余额
		String balance = kw.getText(OrUtil.getBy("account_balance_xpath",
				ObjectLib.XhhObjectLib));
		String balanceVal = PatternUtil.getMoneyMatch(balance);
		// 获取我的投资
		String invert = kw.getText(OrUtil.getBy("account_myInvest_xpath",
				ObjectLib.XhhObjectLib));
		String invertVal = PatternUtil.getMoneyMatch(invert);
		// 获取变现借款
		String cashLoan = kw.getText(OrUtil.getBy("account_cashLoan_xpath",
				ObjectLib.XhhObjectLib));
		String cashLoanVal = PatternUtil.getMoneyMatch(cashLoan);
		// 获取预期总收益
		String incomeTotal = kw.getText(OrUtil.getBy(
				"account_incomeTotal_className", ObjectLib.XhhObjectLib));
		String incomeTotalVal = PatternUtil.getMoneyMatch(incomeTotal);
		// 获取待收收益
		String incomeWait = kw.getText(OrUtil.getBy(
				"account_incomeWait_className", ObjectLib.XhhObjectLib));
		String incomeWaitVal = PatternUtil.getMoneyMatch(incomeWait);
		// 获取已赚收益
		String incomeAlready = kw.getText(OrUtil.getBy(
				"account_incomeAlready_className", ObjectLib.XhhObjectLib));
		String incomeAlreadyVal = PatternUtil.getMoneyMatch(incomeAlready);
		// 获取满减券
		String coupons = kw.getText(OrUtil.getBy("account_coupons_xpath",
				ObjectLib.XhhObjectLib));
		String couponsVal = PatternUtil.getMoneyMatch(coupons);

		// 获取红包
		String bonus = kw.getText(OrUtil.getBy("account_bonus_xpath",
				ObjectLib.XhhObjectLib));
		String bonusVal = PatternUtil.getMoneyMatch(bonus);
		// 获取加息券
		String rateCoupons = kw.getText(OrUtil.getBy(
				"account_rateCoupons_xpath", ObjectLib.XhhObjectLib));
		String rateCouponsVal = PatternUtil.getDigestMatch(rateCoupons);
		// 获取邀请好友
		String partner = kw.getText(OrUtil.getBy("account_partner_xpth",
				ObjectLib.XhhObjectLib));
		String partnerVal = PatternUtil.getDigestMatch(partner);

		map.put("asset", assetVal);
		map.put("balance", balanceVal);
		map.put("total_invest", invertVal);
		map.put("cash_loan", cashLoanVal);
		map.put("total_profit", incomeTotalVal);
		map.put("will_profit", incomeWaitVal);
		map.put("exist_profit", incomeAlreadyVal);
		map.put("coupons_not_use", couponsVal);
		map.put("bonus_not_use", bonusVal);
		map.put("rate_coupons_not_use", rateCouponsVal);
		map.put("invite_partner", partnerVal);
		// 与期望值校验
		flag = StringUtils.isEquals(checkMap, map, log);
		return flag;
	}

	/**
	 * 投资后回写用户数据、项目数据、投资数据
	 * 
	 * @param exptectAcctInfo
	 * @param expectProdInfo
	 * @param productCode
	 * @param investAmount
	 * @param uid
	 * @throws IOException
	 * @throws SQLException
	 */
	public void dataWriteBack(Map<String, String> expConfmPayMap,
			Map<String, String> exptectAcctInfo,
			Map<String, String> expectProdInfo, String productCode,
			double investAmount, String uid) throws IOException, SQLException {
		// 用户账号更新
		List<Double> list = accountWriteBack(exptectAcctInfo, investAmount, uid);
		// 产品信息更新
		productWriteBack(expectProdInfo, productCode, investAmount);
		// 插入理财记录
		investRecordWriteBack(expectProdInfo, productCode, investAmount, uid);
		// 插入资金记录
		financeRrdWriteBack(list.get(0), productCode, list.get(1), uid);
	}

	/**
	 * 投资速兑通项目后原用户账户信息回写用户数据、项目数据、投资数据
	 * 
	 * @param exptectAcctInfo
	 * @param expectProdInfo
	 * @param productCode
	 * @param investAmount
	 * @param uid
	 * @throws IOException
	 * @throws SQLException
	 */
	public void sdtdataWriteBack(Map<String, String> exptectAcctInfo,
			String productCode, double investAmount, String uid)
			throws IOException, SQLException {
		// 用户账号更新
		List<Double> list = sdtAccountWriteBack(exptectAcctInfo, investAmount,
				uid);
		// 插入变现记录
		sdtFinanceRrdWriteBack(list.get(0), productCode, investAmount, uid);
	}

	/**
	 * 投资速兑通项目后原用户账户信息回写用户数据、手续费数据
	 * 
	 * @param exptectAcctInfo
	 * @param expectProdInfo
	 * @param productCode
	 * @param investAmount
	 * @param uid
	 * @throws IOException
	 * @throws SQLException
	 */
	public void sdtFeedataWriteBack(Map<String, String> exptectAcctInfo,
			String productCode, double count_plant_fee, String uid)
			throws IOException, SQLException {

		// 用户账号更新
		List<Double> list = feeAccountWriteBack(exptectAcctInfo,
				count_plant_fee, uid);
		// 插入手续费
		plantFeeFinanceRrdWriteBack(list.get(0), productCode, count_plant_fee,
				uid);
		log.info("插入手续费成功!");
	}

	/**
	 * 更新用户账户表
	 * 
	 * @param exptectAcctInfo
	 * @param investAmount
	 *            投资金额
	 * @param uid
	 *            数据库user表id
	 * @return list 对应数据为 0：账户余额 1：实际使用的账户余额
	 * @throws SQLException
	 */
	public List<Double> accountWriteBack(Map<String, String> exptectAcctInfo,
			double investAmount, String uid) throws SQLException {
		List<Double> list = new ArrayList<Double>();
		/*
		 * 用户账号更新
		 */
		// 计算实际使用的账户余额
		double bonusNotUse = Double.parseDouble(exptectAcctInfo
				.get("bonus_not_use"));
		double bonusUsed = 0;
		double couponsNotUse = Double.parseDouble(exptectAcctInfo
				.get("coupons_not_use"));
		double couponsUsed = 0;
		// 如果存在红包或满减券，则会默认使用，计算实际投资使用的账户中金额
		if (bonusNotUse != 0) {
			bonusUsed = bonusNotUse;
			investAmount = investAmount - bonusNotUse;
			bonusNotUse = 0;
			AccountDao.updateAcctProfit(uid, StringUtils.double2Str(bonusUsed),
					StringUtils.double2Str(bonusUsed));
		} else if (couponsNotUse != 0) {
			couponsUsed = couponsNotUse;
			investAmount = investAmount - couponsNotUse;
			couponsNotUse = 0;
			AccountDao.updateAcctProfit(uid,
					StringUtils.double2Str(couponsUsed),
					StringUtils.double2Str(couponsUsed));
		}

		// 原余额
		String acctBalanceStr = exptectAcctInfo.get("balance");
		// 原总投资
		String totalInvstStr = exptectAcctInfo.get("total_invest");
		// 投资后剩余余额
		double acctBalance = StringUtils.str2Double(acctBalanceStr)
				- investAmount;

		// 投资后总投资金额
		double totalInvst = StringUtils.str2Double(totalInvstStr)
				+ investAmount;
		AccountDao.updateAccount(StringUtils.double2Str(acctBalance),
				StringUtils.double2Str(totalInvst),
				StringUtils.double2Str(couponsNotUse),
				StringUtils.double2Str(couponsUsed),
				StringUtils.double2Str(bonusNotUse),
				StringUtils.double2Str(bonusUsed), uid);
		list.add(acctBalance);
		list.add(investAmount);
		return list;
	}

	/**
	 * 项目变现被购买后账户信息修改
	 * 
	 * @param exptectAcctInfo
	 * @param cashAmount
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public List<Double> sdtAccountWriteBack(
			Map<String, String> exptectAcctInfo, double cashAmount, String uid)
			throws SQLException {
		List<Double> list = new ArrayList<Double>();
		/*
		 * 用户账号更新
		 */
		// 计算实际使用的账户余额
		// 原余额
		String acctBalanceStr = exptectAcctInfo.get("balance");
		// 原变现总额
		String totalcashloanStr = exptectAcctInfo.get("cash_loan");
		// 原总投资
		// String totalInvstStr = exptectAcctInfo.get("total_invest");
		// 变现后账户剩余余额
		// double acctBalance = StringUtils.str2Double(acctBalanceStr);

		double acctBalance = StringUtils.str2Double(acctBalanceStr)
				+ cashAmount;

		// 变现后总变现金额
		double totalcashloan = StringUtils.str2Double(totalcashloanStr)
				+ cashAmount;
		AccountDao.updateSDTAccount(StringUtils.double2Str(acctBalance),
				StringUtils.double2Str(totalcashloan), uid);
		list.add(acctBalance);
		list.add(totalcashloan);
		return list;
	}

	/**
	 * 手续费扣除后账户余额变化
	 * 
	 * @param exptectAcctInfo
	 * @param plantFee
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public List<Double> feeAccountWriteBack(
			Map<String, String> exptectAcctInfo, double plantFee, String uid)
			throws SQLException {
		List<Double> list = new ArrayList<Double>();
		/*
		 * 用户账号更新
		 */
		// 原余额
		String acctBalanceStr = exptectAcctInfo.get("balance");
		// 手续费后账户剩余余额
		double acctBalance = StringUtils.str2Double(acctBalanceStr) - plantFee;
		log.info("减去平台费后账户余额" + acctBalance);
		AccountDao.updateFeeAccount(StringUtils.double2Str(acctBalance), uid);
		list.add(acctBalance);
		return list;
	}

	/**
	 * 更新产品信息表
	 * 
	 * @param expectProdInfo
	 * @param productCode
	 * @param investAmount
	 * @throws SQLException
	 */
	public void productWriteBack(Map<String, String> expectProdInfo,
			String productCode, double investAmount) throws SQLException {
		// 融资规模
		String prodAmountStr = expectProdInfo.get("demand_amount");
		double prodAmount = StringUtils.str2Double(prodAmountStr);
		// 投资前产品余额
		String balance = expectProdInfo.get("balance");
		// 投资后产品余额
		double prodBalance = StringUtils.str2Double(balance) - investAmount;
		// 投资后已募集金额
		double collect = prodAmount - prodBalance;
		// 投资后产品进度（百分比）
		int currProgrs = (int) new BigDecimal(collect)
				.divide(new BigDecimal(prodAmount), 2,
						BigDecimal.ROUND_HALF_EVEN)
				.multiply(new BigDecimal(100)).doubleValue();
		ProductDao.updateAmount(StringUtils.double2Str(prodBalance), currProgrs
				+ "", StringUtils.double2Str(collect), productCode);
	}

	/**
	 * 插入新的理财记录
	 * 
	 * @param expectProdInfo
	 * @param productCode
	 * @param investAmount
	 * @param uid
	 * @throws IOException
	 * @throws SQLException
	 */
	public void investRecordWriteBack(Map<String, String> expectProdInfo,
			String productCode, double investAmount, String uid)
			throws IOException, SQLException {
		String profit = "--";
		String repayDate = expectProdInfo.get("repay_date_stage1");
		if (repayDate == null) {
			repayDate = expectProdInfo.get("repay_date");
		}
		String stauts = "投标结束";
		String investDate = SSHUtil.sshCurrentTime(log);
		InvestRecordDao.insertRecord(uid, productCode, profit, repayDate,
				StringUtils.double2Str(investAmount), stauts, investDate);
	}

	/**
	 * 插入新的资金记录表
	 * 
	 * @param acctBalance
	 * @param productCode
	 * @param investAmount
	 * @param uid
	 * @throws SQLException
	 */
	public void financeRrdWriteBack(double acctBalance, String productCode,
			double investAmount, String uid) throws SQLException {
		String financeType = "投资";
		String abstracts = "投资项目 “" + productCode + "”";
		String dealAmount = "-" + StringUtils.double2Str(investAmount);
		FinanceRecordDao.insertRecord(uid, productCode, financeType,
				dealAmount, StringUtils.double2Str(acctBalance), abstracts);
	}

	/**
	 * 速兑通变现类型资金记录
	 * 
	 * @param acctBalance
	 * @param productCode
	 * @param investAmount
	 * @param uid
	 * @throws SQLException
	 */
	public void sdtFinanceRrdWriteBack(double acctBalance, String productCode,
			double cashAmount, String uid) throws SQLException {
		String financeType = "变现";
		String abstracts = "速兑通“" + productCode + "”变现";
		String addAmount = "+" + StringUtils.double2Str(cashAmount);
		FinanceRecordDao.insertRecord(uid, productCode, financeType, addAmount,
				StringUtils.double2Str(acctBalance), abstracts);
	}

	/**
	 * 速兑通手续费类型资金记录
	 * 
	 * @param acctBalance
	 * @param productCode
	 * @param investAmount
	 * @param uid
	 * @throws SQLException
	 */
	public void plantFeeFinanceRrdWriteBack(double acctBalance,
			String productCode, double plantFee, String uid)
			throws SQLException {
		String financeType = "手续费";
		String abstracts = "速兑通“" + productCode + "”手续费";
		String dealAmount = "-" + StringUtils.double2Str(plantFee);
		FinanceRecordDao.insertRecord(uid, productCode, financeType,
				dealAmount, StringUtils.double2Str(acctBalance), abstracts);
	}

	/**
	 * 校验表格行数
	 * 
	 * @param kw
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public boolean checkRowCount(KeyWords kw, int count) throws Exception {
		boolean flag = false;
		WebElement table = kw.getWebElement(OrUtil.getBy(
				"account_financeManage_table_cssSelector",
				ObjectLib.XhhObjectLib));
		List<WebElement> rows = table.findElements(By.tagName("tr"));
		if (rows.size() == count)
			flag = true;
		else {
			log.error("期望值条数：" + count + " 实际值条数：" + rows.size());
		}
		return flag;
	}

	/**
	 * 校验表格行数
	 * 
	 * @param kw
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public boolean checkRowCount2(KeyWords kw, int count) throws Exception {
		boolean flag = false;
		List<WebElement> rows = null;
		WebElement table = null;
		boolean isExist = kw.isElementExist2(By
				.xpath("//div[@class='page pageWraper']/a[text()='>']"));
		if (isExist) {
			// 分页
			rows = new ArrayList<>();
			int maxPage = getRepayPlanTotalPageNum(kw);
			// 循环所有页数
			for (int i = 0; i < maxPage; i++) {
				// 获取当前页
				int nowPage = Integer
						.parseInt(kw
								.executeJsReturnString("return $('.page.pageWraper').attr('nowpage')"));
				// 取出当前页的数据
				table = kw.getWebElement(OrUtil.getBy(
						"account_financeManage_table_cssSelector",
						ObjectLib.XhhObjectLib));
				List<WebElement> nowRows = table.findElements(By.tagName("tr"));
				for (WebElement webElement : nowRows) {
					rows.add(webElement);
				}
				// 如果不是最后一页
				if (maxPage != nowPage) {
					kw.sendKeys(Key.PAGE_DOWN);
					ThreadUtil.sleep();
					// 点击下一页
					kw.click(By
							.xpath("//div[@class='page pageWraper']/a[text()='>']"));
				}
			}
			// 点击回第一页
			kw.click(By.xpath("//a[text()='1']"));
		} else {
			// 1页
			table = kw.getWebElement(OrUtil.getBy(
					"account_financeManage_table_cssSelector",
					ObjectLib.XhhObjectLib));
			rows = table.findElements(By.tagName("tr"));
		}
		if (rows.size() == count)
			flag = true;
		return flag;
	}

	/**
	 * 获取还款计划的总页数
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public int getRepayPlanTotalPageNum(KeyWords kw) throws Exception {
		kw.waitElementToBeClickable(OrUtil.getBy("repay_total_path_xpath",
				ObjectLib.XhhObjectLib));
		return Integer.parseInt(kw.getWebElement(
				OrUtil.getBy("repay_total_path_xpath", ObjectLib.XhhObjectLib))
				.getText());
	}

	/**
	 * 根据资金类型获得对应字符串类型
	 * 
	 * @param type
	 * @return
	 */
	public String getFinanceType(FinanceType type) {
		String typeStr = "";
		switch (type) {
		case invest:
			typeStr = "投资";
			break;
		case receive:
			typeStr = "回款";
			break;
		case fee:
			typeStr = "手续费";
			break;
		case reward:
			typeStr = "奖励";
			break;
		case realization:
			typeStr = "变现";
			break;
		case recharge:
			typeStr = "充值";
			break;
		case withdraw:
			typeStr = "提现";
			break;
		default:
			typeStr = "投资";
			break;
		}

		return typeStr;
	}

	/**
	 * 含有速兑通项目的回款后资金记录会写
	 * 
	 * @param balanceDb
	 * @param productCode
	 * @param will_profit
	 * @param uid
	 * @throws SQLException
	 */
	public void sdtFinanceRepaydWriteBack(String balanceDb, String productCode,
			String midMoneyDb, String uid) throws SQLException {
		String financeType = "回款";
		String abstracts = "项目“" + productCode + "”回款";
		String addAmount = "+" + midMoneyDb;
		FinanceRecordDao.insertRecord(uid, productCode, financeType, addAmount,
				balanceDb, abstracts);
	}

}
