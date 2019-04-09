/**
 * 
 */
package com.autotest.service.xhhService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.autotest.dao.AccountDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.UserProductDao;
import com.autotest.utility.MathUtil;
import com.autotest.utility.StringUtils;

/**
 * @author 000738
 * 
 */
public class InvestSDTService2 {
	private Logger	log	= null;

	public InvestSDTService2(Logger log) {
		this.log = log;
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
	public void countUserAcct(String code, String uid, String prj_name,
			String userNumber, String productId) throws SQLException {
		List<Map<String, String>> lst = ProductDao.countProfit(code);
		Map<String, String> map = ProductDao.getInvestMoney(prj_name, uid);
		Map<String, String> sdtmap = ProductDao.selectSdtRecord(code);
		Map<String, String> account = AccountDao.getUserAcctByUid(uid);

		// 购买速兑通项目的用户收益
		BigDecimal profit = new BigDecimal(0.00);
		// 购买速兑通项目的用户收益总和
		BigDecimal profitCount = new BigDecimal(0.00);
		// 投资速兑通项目的金额
		BigDecimal investMoney = new BigDecimal(0.00);
		// 投资速兑通项目的金额总和
		BigDecimal investMoneyCount = new BigDecimal(0.00);
		// 变现者投资原始项目的收益
		BigDecimal oriProfit = new BigDecimal(0.00);
		// 变现者原始投资金额
		BigDecimal oriInvestMoney = new BigDecimal(0.00);
		log.info("lst的长度为" + lst.size());
		// 统计投资和收益总和
		for (int i = 0; i < lst.size(); i++) {
			profit = new BigDecimal(
					Double.parseDouble(lst.get(i).get("profit")));
			profitCount = profitCount.add(profit);
			investMoney = new BigDecimal(Double.parseDouble(lst.get(i).get(
					"invest_amount")));
			investMoneyCount = investMoneyCount.add(investMoney);
		}
		log.info("是否执行");
		// 平台费计算
		BigDecimal plantFee = new BigDecimal(Double.parseDouble(sdtmap
				.get("count_plant_fee")));
		oriProfit = new BigDecimal(Double.parseDouble(map.get("profit")));
		oriInvestMoney = new BigDecimal(Double.parseDouble(map
				.get("invest_amount")));
		// 变现金额
		BigDecimal cashLoan = oriInvestMoney.multiply(
				investMoneyCount.add(profitCount)).divide(
				oriProfit.add(oriInvestMoney), 2, BigDecimal.ROUND_DOWN);
		log.info("变量名=[" + cashLoan + "]");
		String cashLoanDb = StringUtils.double2Str(cashLoan.doubleValue());
		// log.info("变量名=[" + cashLoanDb + "]");
		// 已赚收益
		BigDecimal exist_profit = investMoneyCount
				.subtract(cashLoan)
				.subtract(plantFee)
				.add(new BigDecimal(Double.parseDouble(account
						.get("exist_profit"))));
		String exist_profitDb = StringUtils.double2Str(exist_profit
				.doubleValue());
		BigDecimal will_profit = new BigDecimal(0.00);
		BigDecimal total_profit = new BigDecimal(0.00);
		BigDecimal investOther = new BigDecimal(0.00);

		if (StringUtils.isEquals("u21", userNumber)) {
			Map<String, String> investRecord = ProductDao
					.countProfitUidAndCode(uid, prj_name);
			// 待收收益
			will_profit = new BigDecimal(Double.parseDouble(account
					.get("will_profit"))).subtract(new BigDecimal(Double
					.parseDouble(investRecord.get("profit"))));
			total_profit = exist_profit.add(will_profit);
			List<Map<String, String>> userInvestProductList = UserProductDao
					.getUserInvestProductList(userNumber);
			for (int i = 0; i < userInvestProductList.size(); i++) {
				if (!StringUtils.isEquals(
						userInvestProductList.get(i).get("product_number"),
						productId)) {
					investOther = investOther.add(new BigDecimal(Double
							.parseDouble(userInvestProductList.get(i).get(
									"invest_amount"))));
				}
			}
		} else {
			// 待收收益
			will_profit = oriProfit.subtract(profitCount)
					.subtract(investMoneyCount).add(cashLoan);

			// 预期总收益
			total_profit = exist_profit.add(will_profit);

		}

		String will_profitDb = StringUtils
				.double2Str(will_profit.doubleValue());
		if (StringUtils.isEquals(will_profitDb, "-0.00")) {
			will_profitDb = "0.00";
		}
		String total_profitDb = StringUtils.double2Str(total_profit
				.doubleValue());
		BigDecimal asset = new BigDecimal(0.00);
		asset = new BigDecimal(Double.parseDouble(account.get("balance")))
				.add(oriInvestMoney).subtract(cashLoan).add(investOther);
		String assetDb = StringUtils.double2Str(asset.doubleValue());
		ProductDao.updateSdtUserAcct(uid, assetDb, cashLoanDb, will_profitDb,
				exist_profitDb, total_profitDb);
	}

	/**
	 * 计算速兑通项目收益
	 * 
	 * @param code
	 * @param investAmount
	 * @return
	 * @throws Exception
	 */
	public String setProfit(String code, Double investAmount) throws Exception {
		Map<String, String> map = ProductDao.getProByCode(code);
		double term = Double.parseDouble(StringUtils.getNum(map.get("term")));
		double rate = Double.parseDouble(map.get("year_rate"));
		double profit = investAmount * term * rate / 100 / 365;
		// 收益值保留两位小数并转化成字符串型（直接舍弃）
		return MathUtil.round2(profit);
	}
}
