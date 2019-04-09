package com.autotest.service.xhhService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.log4j.Logger;

import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.RepayPlanDao;
import com.autotest.dao.UserAcctDao;
import com.autotest.dao.UserDao;
import com.autotest.driver.KeyWords;
import com.autotest.enums.DbType;
import com.autotest.or.ObjectLib;
import com.autotest.utility.DbUtil;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.ProfitCal;
import com.autotest.utility.StringUtils;

public class ExtendRepayService {

	private Logger log = null;

	public ExtendRepayService(Logger log) {
		this.log = log;
	}

	/**
	 * 返回初始日期+天数后的新日期
	 * 
	 * @param repayDate
	 * @param extendDay
	 * @return
	 * @throws Exception
	 */
	public String getExtendDate(String repayDate, String extendDay) throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date ExtendDay = format.parse(repayDate);
		Calendar calendar = Calendar.getInstance();
		int exDay = Integer.parseInt(extendDay);
		calendar.setTime(ExtendDay);
		calendar.add(Calendar.DATE, exDay);
		return format.format(calendar.getTime()).toString();
	}

	/**
	 * xhh-788:保理公司【展期还款】
	 * 
	 * @param kw
	 * @param proName
	 * @throws Exception
	 */
	public boolean doExtendRepay(KeyWords kw, String proName) throws Exception {
		// 点击还款明细页面下的“展期还款”按钮
		int retry = 5;
		for (int r = 0; r < retry;) {
			if (kw.isVisible(OrUtil.getBy("repay_extend_repaybtn_xpath", ObjectLib.XhhObjectLib))) {
				log.info("展期还款按钮可见！");
				kw.click(OrUtil.getBy("repay_extend_repaybtn_xpath", ObjectLib.XhhObjectLib));
				// 点击确认按钮
				log.info("点击确认展期还款按钮！");
				kw.click(OrUtil.getBy("repay_detail_pop_verify_xpath", ObjectLib.XhhObjectLib));
				if (kw.isElementVisible(OrUtil.getBy("repay_progressLoad_xpath", ObjectLib.XhhObjectLib))) {
					while (true) {
						if (!kw.isElementVisible(OrUtil.getBy("repay_progressLoad_xpath", ObjectLib.XhhObjectLib)))
							break;
					}
				}
				// 点击取消按钮
				// kw.click(OrUtil.getBy("repay_detail_pop_cancel_xpath",
				// ObjectLib.XhhObjectLib));
				return true;
			} else {
				log.error("展期还款按钮不可见！");
				r++;
			}
		}
		return false;
	}

	/**
	 * 展期后更新用户投资记录的利息和还款日期
	 * 
	 * @param proName
	 * @param uid
	 * @throws Exception
	 */
	public void updateInvestRecord(String proName, String uid, String extendDate) throws Exception {
		Map<String, String> investRec = InvestRecordDao.getRecordByUserCode(uid, proName);
		double amount = Double.parseDouble(investRec.get("invest_amount"));
		// 展期后的新利息=展期利息+原利息
		double newProfit = MathUtil.retain2Decimal2(
				ProfitCal.getExtendProfit(proName, amount) + Double.parseDouble(investRec.get("profit")));
		InvestRecordDao.updateProfit(uid, proName, String.valueOf(newProfit));
		InvestRecordDao.updateRepayDate(proName, extendDate);
	}

	/**
	 * 计算项目的展期利息：投资用户展期利息之和
	 * 
	 * @param proName
	 * @return
	 * @throws SQLException
	 */
	public BigDecimal calcProExtendFrofit(String proName) throws SQLException {
		double exProfit;
		String sql = "select sum(profit) as exprofit from user_repay_plan where name='" + proName
				+ "' and stage='展期利息'";
		Map<String, String> sumMap = DbUtil.querySingleData(sql, DbType.Local);
		System.out.println("exProfit=[" + sumMap.get("exprofit") + "]");
		exProfit = Double.parseDouble(sumMap.get("exprofit"));
		return MathUtil.roundD(exProfit);
	}

	/**
	 * 展期后项目还款计划中插入展期信息，并更新还款日期
	 * 
	 * @param proName
	 * @param stage
	 * @throws Exception
	 */
	public void insertExtendToProPepayPlan(String proName, String extendDate) throws Exception {
		Map<String, String> pro = ProductDao.getProByCode(proName);
		String code = pro.get("prj_no");
		String capital = "0.00";
		String exprofit = String.valueOf(calcProExtendFrofit(proName));
		String total_money = exprofit;
		String remain_capital = "0.00";
		String status = "0";
		String stage = "展期";
		RepayPlanDao.insertIntoProductPayPlan(proName, code, stage, capital, exprofit, total_money, remain_capital,
				extendDate, status);
		RepayPlanDao.updateProRepayDate(proName, extendDate);
	}

	/**
	 * 展期后用户还款计划中插入展期信息，并更新还款日期
	 * 
	 * @param proName
	 * @param uid
	 * @param stage
	 * @param extendDate
	 * @throws Exception
	 */
	public void insertExtendToUserPepayPlan(String proName, String uid, String extendDate) throws Exception {
		Map<String, String> pro = ProductDao.getProByCode(proName);
		String invest_amount = InvestRecordDao.getRecordByUserCode(uid, proName).get("invest_amount");
		String code = pro.get("prj_no");
		String bus_uid = "u" + UserDao.getUidById(uid);
		String capital = "0.00";
		String exprofit = StringUtils.double2Str(ProfitCal.getExtendProfit(proName, Double.valueOf(invest_amount)))
				.toString();
		String total_money = exprofit;
		String remain_capital = "0.00";
		String status = "0";
		String stage = "展期利息";

		RepayPlanDao.insertIntoUserPayPlan(uid, bus_uid, proName, code, stage, invest_amount, capital, exprofit,
				total_money, remain_capital, extendDate, status);
		RepayPlanDao.updateUserRepayDate(proName, extendDate);
	}

	/**
	 * 展期后更新用户账户的待收收益、总收益
	 * 
	 * @param uid
	 * @throws Exception
	 */
	public void updateUserAcct(String uid, String proName) throws Exception {
		String capital = InvestRecordDao.getRecordByUserCode(uid, proName).get("invest_amount");
		Double exprofit = ProfitCal.getExtendProfit(proName, Double.valueOf(capital));
		UserAcctDao.updateProfit(uid, exprofit.toString());
	}

	/**
	 * 展期后更新项目信息中的还款日期
	 * 
	 * @param proName
	 * @param extendDate
	 * @throws SQLException
	 */
	public void updateProInfo(String proName, String extendDate) throws SQLException {
		String sql = "update productinfo set repay_date='" + extendDate + "' where prj_name='" + proName + "'";
		DbUtil.update(sql, DbType.Local);
	}

}
