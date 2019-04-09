package com.autotest.service.xhhService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.autotest.dao.AccountDao;
import com.autotest.dao.ProductDao;
import com.autotest.driver.KeyWords;
import com.autotest.or.ObjectLib;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.StringUtils;

public class InvestSDTService {

	private Logger	log	= null;

	public InvestSDTService(Logger log) {
		this.log = log;
	}

	/**
	 * 点击速兑通页签
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void clickSDTPage(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("finance_sudui_linkText", ObjectLib.XhhObjectLib));
		Thread.sleep(2);

	}

	/**
	 * 计算速兑通项目收益
	 * 
	 * @param kw
	 * @param code
	 * @param investAmount
	 * @return
	 * @throws Exception
	 */
	public String setProfit(KeyWords kw, String code, int investAmount)
			throws Exception {
		Map<String, String> map = ProductDao.getProByCode(code);
		double term = Double.parseDouble(StringUtils.getNum(map.get("term")));
		double rate = Double.parseDouble(map.get("year_rate"));
		double profit = investAmount * term * rate / 100 / 365;
		// 收益值保留两位小数并转化成字符串型（直接舍弃）
		return MathUtil.round2(profit);
	}

	/**
	 * 计算速兑通项目收益
	 * 
	 * @param kw
	 * @param code
	 * @param investAmount
	 * @return
	 * @throws Exception
	 */
	public String setProfit2(KeyWords kw, String code, Double investAmount)
			throws Exception {
		Map<String, String> map = ProductDao.getProByCode(code);
		double term = Double.parseDouble(StringUtils.getNum(map.get("term")));
		double rate = Double.parseDouble(map.get("year_rate"));
		double profit = investAmount * term * rate / 100 / 365;
		// 收益值保留两位小数并转化成字符串型（直接舍弃）
		return MathUtil.round2(profit);
	}

	/**
	 * 账户概括公式
	 * 
	 * @param kw
	 * @param code
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public void countProfit(KeyWords kw, String code, String uid,
			String prj_name) throws SQLException {
		List<Map<String, String>> lst = ProductDao.countProfit(code);
		Map<String, String> map = ProductDao.getInvestMoney(prj_name, uid);
		Map<String, String> sdtmap = ProductDao.selectSdtRecord(code);
		Map<String, String> account = AccountDao.getUserAcctByUid(uid);
		// 购买速兑通项目的用户收益
		double profit = 0.00;
		// 购买速兑通项目的用户收益总和
		double profitCount = 0.00;
		// 投资速兑通项目的金额
		double investMoney = 0.00;
		// 投资速兑通项目的金额总和
		double investMoneyCount = 0.00;
		// 变现者投资原始项目的收益
		double oriProfit = 0.00;
		// 变现者原始投资金额
		double oriInvestMoney = 0.00;
		log.info("lst的长度为" + lst.size());
		// 统计投资和收益总和
		for (int i = 0; i < lst.size(); i++) {
			profit = Double.parseDouble(lst.get(i).get("profit"));
			profitCount = profitCount + profit;
			investMoney = Double.parseDouble(lst.get(i).get("invest_amount"));
			investMoneyCount = investMoneyCount + investMoney;
		}
		log.info("是否执行");

		// 平台费计算
		double plantFee = Double.parseDouble(sdtmap.get("count_plant_fee"));
		oriProfit = Double.parseDouble(map.get("profit"));
		oriInvestMoney = Double.parseDouble(map.get("invest_amount"));

		// 变现金额
		double cashLoan = oriInvestMoney * (investMoneyCount + profitCount)
				/ (oriProfit + oriInvestMoney);
		log.info("变量名=[" + cashLoan + "]");
		String cashLoanDb = MathUtil.round2(cashLoan);
		double cashKey = Double.parseDouble(cashLoanDb);
		log.info("变量名=[" + cashKey + "]");

		// 已赚收益
		BigDecimal investMoneyCountKey = new BigDecimal(
				Double.toString(investMoneyCount));
		BigDecimal cashLoanKey = new BigDecimal(Double.toString(cashKey));
		BigDecimal plantFeeKey = new BigDecimal(Double.toString(plantFee));
		double exist_profit = ((investMoneyCountKey.subtract(cashLoanKey))
				.subtract(plantFeeKey)).doubleValue();
		String exist_profitDb = MathUtil.round2(exist_profit);
		double existKey = Double.parseDouble(exist_profitDb);

		// 待收收益
		BigDecimal oriProfitKey = new BigDecimal(Double.toString(oriProfit));
		BigDecimal profitCountKey = new BigDecimal(Double.toString(profitCount));
		double will_profit = ((oriProfitKey.subtract(profitCountKey))
				.subtract(investMoneyCountKey).add(cashLoanKey)).doubleValue();
		// double will_profit = oriProfit - profitCount - investMoneyCount +
		// cashKey;
		double will_profitMid = MathUtil.retain2Decimal2(will_profit);
		String will_profitDb = Double.toString(will_profitMid);
		double willKey = Double.parseDouble(will_profitDb);

		// 预期总收益
		double total_profit = existKey + willKey;
		String total_profitDb = Double.toString(total_profit);

		// 账户净资产
		double balance = Double.parseDouble(account.get("balance"));
		BigDecimal balanceKey = new BigDecimal(Double.toString(balance));
		BigDecimal oriInvestMoneyKey = new BigDecimal(
				Double.toString(oriInvestMoney));
		double asset = ((balanceKey.add(oriInvestMoneyKey))
				.subtract(cashLoanKey)).doubleValue();

		// asset = Double.parseDouble(account.get("balance")) + oriInvestMoney
		// - cashKey;
		// String assetDb = MathUtil.round2(asset);50093.42的值转换就成了50093.41
		String assetDb = Double.toString(asset);
		ProductDao.updateSdtUserAcct(uid, assetDb, cashLoanDb, will_profitDb,
				exist_profitDb, total_profitDb);
	}

	/**
	 * 变现者含速兑通原始项目回款后用户信息变更
	 * 
	 * @param prj_name
	 *            SDT001
	 * @param uid
	 *            U12
	 * @param code
	 *            YYS007
	 * @throws SQLException
	 */
	public void sdtRepayAccount(String prj_name, String uid, String code)
			throws SQLException {
		UserAccountService userAccount = new UserAccountService(log);
		// 获取投资记录表
		Map<String, String> map = ProductDao.getInvestMoney(code, uid);
		// 获取购买速兑通项目的投资用户信息
		List<Map<String, String>> lst = ProductDao.countProfit(prj_name);
		// 获取变现者的账户信息
		Map<String, String> account = AccountDao.getUserAcctByUid(uid);
		// 购买速兑通项目的用户收益
		double profit = 0.00;
		// 购买速兑通项目的用户收益总和
		double profitCount = 0.00;
		// 投资速兑通项目的金额
		double investMoney = 0.00;
		// 投资速兑通项目的金额总和
		double investMoneyCount = 0.00;
		// U12投资YYS007的投资金额
		double oriInvestMoney = Double.parseDouble(map.get("invest_amount"));
		// U12投资YYS007的投资收益
		double oriProfit = Double.parseDouble(map.get("profit"));
		// 变现者的原始项目投资本息之和
		double oriMidMoney = oriInvestMoney + oriProfit;
		log.info("变现者的原始项目投资本息之和" + oriMidMoney);
		log.info("lst的长度为" + lst.size());
		// 统计投资和收益总和（u14,u15,u16的本金之和与收益之和）
		for (int i = 0; i < lst.size(); i++) {
			profit = Double.parseDouble(lst.get(i).get("profit"));
			profitCount = profitCount + profit;
			investMoney = Double.parseDouble(lst.get(i).get("invest_amount"));
			investMoneyCount = investMoneyCount + investMoney;
		}
		double rateMoney = profitCount + investMoneyCount;
		log.info("购买速兑通项目的用户本息之和" + rateMoney);
		// U12回款后的资金记录值(U12原本息之和-（14+15+16的本息和）)
		BigDecimal oriMidMoneyKey = new BigDecimal(oriMidMoney);
		log.info("oriMidMoneyKey" + oriMidMoneyKey);
		BigDecimal rateMoneyKey = new BigDecimal(Double.toString(rateMoney));
		log.info("rateMoneyKey" + rateMoneyKey);
		double midMoney = ((oriMidMoneyKey.subtract(rateMoneyKey)))
				.doubleValue();

		// double midMoney = oriMidMoney - rateMoney;
		double midMoney1 = MathUtil.retain2Decimal2(midMoney);
		String midMoneyDb = Double.toString(midMoney1);
		// 获取还款之前账户余额
		double oriBalance = Double.parseDouble(account.get("balance"));
		// 账户余额的计算
		double balance = MathUtil.retain2Decimal2(oriBalance + midMoney1);
		String balanceDb = Double.toString(balance);
		// 获取还款之前账户已赚收益
		double oriExist_profit = Double
				.parseDouble(account.get("exist_profit"));
		// 获取还款之前账户待收收益
		double will_profit = Double.parseDouble(account.get("will_profit"));
		// String will_profitDb = Double.toString(will_profit);
		// 已赚收益,预期总收益
		double exist_profit = oriExist_profit + will_profit;
		String exist_profitDb = Double.toString(exist_profit);
		// 预期总收益
		double total_profit = exist_profit;
		String total_profitDb = Double.toString(total_profit);

		// 回款后更新U12的账户信息
		AccountDao.cashAccount(uid, balanceDb, exist_profitDb, total_profitDb);

		// 回款后更新U12的理财记录信息
		AccountDao.updateSdtRepayInvest(code, uid);

		// 回款后更新U12的资金记录
		userAccount.sdtFinanceRepaydWriteBack(balanceDb, code, midMoneyDb, uid);

	}

	/**
	 * 购买YYS和SDT项目的账户信息，理财记录，资金记录回写
	 * 
	 * @param code
	 *            YYS007
	 * @param uid
	 * @param prj_name
	 *            SDT001
	 * @throws SQLException
	 */
	public void sdtRepayInvest(String code, String uid, String prj_name)
			throws SQLException {
		UserAccountService userAccount = new UserAccountService(log);
		// 获取YYS投资记录表
		Map<String, String> oriMap = ProductDao.getInvestMoney(code, uid);
		// 获取SDT投资记录表
		Map<String, String> sdtMap = ProductDao.getInvestMoney(prj_name, uid);
		// 获取变现者的账户信息
		Map<String, String> account = AccountDao.getUserAcctByUid(uid);
		// 获取投资人的账户余额
		double oribalance = Double.parseDouble(account.get("balance"));
		// 原始项目投资本金
		double oriInvsetMoney = Double.parseDouble(oriMap.get("invest_amount"));
		// 原始项目利息
		double oriprofit = Double.parseDouble(oriMap.get("profit"));
		// 原始项目本息之和
		double oriPrincipalInterest = oriInvsetMoney + oriprofit;
		String oriPrincipalInterestDb = Double.toString(oriPrincipalInterest);

		// 速兑通项目投资本金
		double sdtInvsetMoney = Double.parseDouble(sdtMap.get("invest_amount"));
		// 速兑通项目利息
		double sdtprofit = Double.parseDouble(sdtMap.get("profit"));
		// 速兑通项目本息之和
		double sdtPrincipalInterest = sdtInvsetMoney + sdtprofit;
		String sdtPrincipalInterestDb = Double.toString(sdtPrincipalInterest);

		// 账户余额(全部回款)
		double balance = oribalance + oriPrincipalInterest
				+ sdtPrincipalInterest;
		String balanceDb = Double.toString(balance);

		// 账户余额(YYS007回款)
		double midBalance = oribalance + oriPrincipalInterest;
		String midBalanceDb = Double.toString(midBalance);
		// 获取还款之前账户待收收益
		double will_profit = Double.parseDouble(account.get("will_profit"));
		// String will_profitDb = Double.toString(will_profit);

		// 获取还款之前账户已赚收益
		double oriExist_profit = Double
				.parseDouble(account.get("exist_profit"));
		// 已赚收益
		double exist_profit = oriExist_profit + will_profit;
		String exist_profitDb = Double.toString(exist_profit);

		// 预期总收益
		double total_profit = exist_profit;
		String total_profitDb = Double.toString(total_profit);

		// 回款后更新U14，15，16的账户信息
		AccountDao.cashAccount(uid, balanceDb, exist_profitDb, total_profitDb);

		// 回款后更新U14，15，16的理财记录(YYS007)
		AccountDao.updateSdtRepayInvest(code, uid);
		// 回款后更新U14，15，16的理财记录(SDT001)
		AccountDao.updateSdtRepayInvest(prj_name, uid);

		// 回款后更新U14，15，16的资金记录(YYS007)
		userAccount.sdtFinanceRepaydWriteBack(midBalanceDb, code,
				oriPrincipalInterestDb, uid);
		// 回款后更新U14，15，16的资金记录(SDT001)
		userAccount.sdtFinanceRepaydWriteBack(balanceDb, prj_name,
				sdtPrincipalInterestDb, uid);

	}

	/**
	 * 普通项目回款信息
	 * 
	 * @param code
	 *            YYS007
	 * @param uid
	 * @throws SQLException
	 */
	public void repayAccount(String code, String uid) throws SQLException {
		UserAccountService userAccount = new UserAccountService(log);
		// 获取YYS投资记录表
		Map<String, String> oriMap = ProductDao.getInvestMoney(code, uid);
		// 获取变现者的账户信息
		Map<String, String> account = AccountDao.getUserAcctByUid(uid);
		// 获取投资人的账户余额
		double oribalance = Double.parseDouble(account.get("balance"));
		// 原始项目投资本金
		double oriInvsetMoney = Double.parseDouble(oriMap.get("invest_amount"));
		// 原始项目利息
		double oriprofit = Double.parseDouble(oriMap.get("profit"));
		// 原始项目本息之和
		double oriPrincipalInterest = oriInvsetMoney + oriprofit;
		String oriPrincipalInterestDb = Double.toString(oriPrincipalInterest);

		// 账户余额(全部回款)
		double balance = oribalance + oriPrincipalInterest;

		String balanceDb = Double.toString(balance);

		// 获取还款之前账户待收收益
		double will_profit = Double.parseDouble(account.get("will_profit"));

		// 获取还款之前账户已赚收益
		double oriExist_profit = Double
				.parseDouble(account.get("exist_profit"));
		// 已赚收益
		double exist_profit = oriExist_profit + will_profit;
		String exist_profitDb = Double.toString(exist_profit);

		// 预期总收益
		double total_profit = exist_profit;
		// double total_profit = will_profit + exist_profit;
		String total_profitDb = Double.toString(total_profit);

		// 回款后更新U13的账户信息
		AccountDao.cashAccount(uid, balanceDb, exist_profitDb, total_profitDb);

		// 回款后更新U13的理财记录(YYS007)
		AccountDao.updateSdtRepayInvest(code, uid);

		// 回款后更新U13的资金记录(YYS007)
		userAccount.sdtFinanceRepaydWriteBack(balanceDb, code,
				oriPrincipalInterestDb, uid);

	}

	/**
	 * 速兑通项目被购买后账户概况值回写
	 * 
	 * @param uid
	 * @throws SQLException
	 */
	public void accoutProfit(String uid) throws SQLException {
		Map<String, String> account = AccountDao.getUserAcctByUid(uid);
		List<Map<String, String>> lst = ProductDao.countProfitUid(uid);
		// 用户收益
		double profit = 0.00;
		// 待收收益 = 理财记录待还款列表的收益之和
		double willProfit = 0.00;
		// 理财记录页面上的收益之和
		for (int i = 0; i < lst.size(); i++) {
			profit = Double.parseDouble(lst.get(i).get("profit"));
			willProfit = willProfit + profit;
		}
		// 已赚收益
		double existProfit = Double.parseDouble(account.get("exist_profit"));
		log.info("willProfit" + willProfit);

		// 预期总收益 = 待收收益+已赚收益
		// String willProfitC = MathUtil.round2(willProfit);
		BigDecimal willProfitKey = new BigDecimal(willProfit);
		log.info("willProfitKey" + willProfitKey);
		BigDecimal existProfitKey = new BigDecimal(Double.toString(existProfit));
		log.info("existProfitKey" + existProfitKey);
		double totalCount = ((willProfitKey.add(existProfitKey))).doubleValue();
		// double totalCount = willProfit + existProfit;
		String willProfitDb = Double.toString(willProfit);
		String totalCountDb = Double.toString(totalCount);
		// String willProfitDb = MathUtil.retain2Decimal2(willProfit);
		// String totalCountDb = MathUtil.round2(totalCount);
		ProductDao.updateInvSdtAcct(uid, willProfitDb, totalCountDb);

	}

}
