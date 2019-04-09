package com.autotest.service.xhhService;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.autotest.dao.CashProjectRelatedDao;
import com.autotest.dao.ProductTempDao;
import com.autotest.dao.RepayPlanDao;
import com.autotest.dao.UserProductDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.enums.DbType;
import com.autotest.or.ObjectLib;
import com.autotest.utility.DbUtil;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * 保理账户登录后还款计划页面及下层页面
 * 
 * @author luyiwen
 * 
 */

public class RepayPlanService {

	private Logger log = null;

	public RepayPlanService(Logger log) {
		this.log = log;
	}

	/**
	 * 根据RYS/YYS编号获得项目名称
	 * 
	 * @param kw
	 * @throws Exception
	 * @throws Exception
	 */
	public String getProName(String proid) throws Exception {
		String sql = "select product_name from product_temp where product_id='"
				+ proid + "'";
		Map<String, String> tmpMap = new HashMap<String, String>();
		tmpMap = DbUtil.querySingleData(sql, DbType.Local);
		return tmpMap.get("product_name");
	}

	/**
	 * 根据RYS/YYS编号获得产品名称
	 * 
	 * @param kw
	 * @throws Exception
	 * @throws Exception
	 */
	public String getProCode(String proid) throws Exception {
		String sql = "select product_no from product_temp where product_id='"
				+ proid + "'";
		Map<String, String> tmpMap = new HashMap<String, String>();
		tmpMap = DbUtil.querySingleData(sql, DbType.Local);
		return tmpMap.get("product_no");
	}

	/**
	 * 根据产品名称获得还款时间
	 * 
	 * @param kw
	 * @throws Exception
	 * @throws Exception
	 */
	public String getRepayDate(String proName) throws Exception {
		String sql = "select repay_date from product_repay_plan where name='"
				+ proName + "'";
		Map<String, String> tmpMap = new HashMap<String, String>();
		tmpMap = DbUtil.querySingleData(sql, DbType.Local);
		return tmpMap.get("repay_date");
	}

	/**
	 * xhh-3749:保理公司访问【还款计划】页面
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewRepayPlanTab(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("repayment_plan_xpath", ObjectLib.XhhObjectLib));
	}

	/**
	 * 切换到日益升选项
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewDayGrouth(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("repay_daygrow_xpath", ObjectLib.XhhObjectLib));
	}

	/**
	 * 切换到月益升选项
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewMonthGrouth(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("repay_monthgrow_xpath", ObjectLib.XhhObjectLib));
	}

	/**
	 * 点击表头的还款日一列，更改排序
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void clickRepayDate(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("repay_table_repaydate_xpath",
				ObjectLib.XhhObjectLib));
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
	 * 获取还款计划的当前页码
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public int getRepayPlanCurrentPageNum(KeyWords kw) throws Exception {
		// 获取当前页页数值
		kw.waitElementToBeClickable(By.xpath("//span[@class='current']"));
		String xpahtCurrentPage = "//span[@class='current']";
		return Integer.parseInt(kw.getWebElement(By.xpath(xpahtCurrentPage))
				.getText());

	}

	/**
	 * xhh-3750:保理公司在【还款计划-月益升】列表查找项目
	 * 
	 * @param kw
	 * @param projectName
	 * @return boolean
	 * @throws Exception
	 */
	public boolean isFindProject(KeyWords kw, String projectName) {
		log.info("isFindPoject:要查找的项目名字为---" + projectName);
		String xpathProjectName = "//p[@class='pro_name' and contains(text(),'"
				+ projectName + "')]/parent::node()/parent::node()";
		boolean res = kw.isElementVisible(By.xpath(xpathProjectName));
		return res;
	}

	/**
	 * xhh-3751:校验还款计划列表的字段及字段值
	 * 
	 * @param kw
	 * @param projectName
	 * @return boolean
	 * @throws Exception
	 */
	public boolean checkRepayPlanInfo(KeyWords keywords, String projectName,
			Map<String, String> expectedMap) throws Exception {

		// 要查找的项目xpath路径
		String xpathProjectName = "//p[@class='pro_name' and contains(text(),'"
				+ projectName + "')]/parent::node()/parent::node()";
		HashMap<String, String> showHashmap = new HashMap<String, String>();
		showHashmap.put("还款日期",
				keywords.getWebElement(By.xpath(xpathProjectName + "/td[1]"))
						.getText());
		showHashmap.put("项目名称",
				keywords.getWebElement(By.xpath(xpathProjectName + "/td[2]/p"))
						.getText());
		showHashmap.put(
				"融资规模",
				MathUtil.format2(MathUtil.format(keywords.getWebElement(
						By.xpath(xpathProjectName + "/td[3]/span")).getText()))
						+ "");
		showHashmap.put(
				"实际融资规模",
				MathUtil.format2(MathUtil.format(keywords.getWebElement(
						By.xpath(xpathProjectName + "/td[4]")).getText()))
						+ "");

		showHashmap.put("预期年化利率",
				keywords.getWebElement(By.xpath(xpathProjectName + "/td[6]/p"))
						.getText());
		showHashmap.put("还款方式",
				keywords.getWebElement(By.xpath(xpathProjectName + "/td[7]"))
						.getText());
		showHashmap.put(
				"需还款金额",
				MathUtil.format2(MathUtil.format(keywords.getWebElement(
						By.xpath(xpathProjectName + "/td[8]/a")).getText())));
		showHashmap.put(
				"发布时间",
				keywords.getWebElement(
						By.xpath(xpathProjectName + "/td[10]/span")).getText());

		// 由于数据库中存期限为“x月”，而界面显示获得为“x个月”，故单独 对此值用contain进行比较
		String time_exp = expectedMap.get("期限");
		expectedMap.remove("期限");
		boolean res1 = StringUtils.isEquals(expectedMap, showHashmap, log);
		String time_show = keywords.getWebElement(
				By.xpath(xpathProjectName + "/td[5]")).getText();
		boolean res2 = StringUtils.isContain(time_exp, time_show, log);
		if (res2) {
			log.info("期限期望值：" + time_exp + ";实际值：" + time_show + "校验成功");
		} else {
			log.info("期限期望值：" + time_exp + ";实际值：" + time_show + "校验失败");
		}

		return res1 & res2;
	}

	/**
	 * 转换字符，过滤掉结尾的"万"或者"元"和中间的逗号
	 * 
	 * @param str
	 * @return str
	 */
	public String filterString(String str) {
		String tail = str.split("\\.")[1].substring(0, 2);
		if (str.toCharArray()[str.length() - 1] == '万') {
			str = str.split("\\.")[0];
			str = str.replace(",", "");
			str = str + "0000";
		} else {
			str = str.split("\\.")[0];
			str = str.replace(",", "");
		}
		return str + "." + tail;
	}

	/**
	 * 从数据库获取还款计划期望值
	 * 
	 * @param ：
	 * @throws Exception
	 */
	public Map<String, String> getExpectRepayPlanInfo(String proName,
			String repayDate) throws Exception {

		Map<String, String> expectedHashmap = new HashMap<String, String>();
		List<Map<String, String>> dbMapList = DbUtil
				.queryDataList(
						"select name, product_repay_plan.repay_date,demand_amount,collect,term,year_rate,repay_way,total_money,time,stage,profit"
								+ " from productinfo,product_repay_plan"
								+ " where productinfo.prj_name=product_repay_plan.name and product_repay_plan.name='"
								+ proName
								+ "' and product_repay_plan.repay_date='"
								+ repayDate + "'", DbType.Local);
		// 计算需还款金额
		double repayMoney = RepayPlanDao.getTotalByCode(proName);
		System.out.println("repayMoney的值是" + repayMoney);
		for (Map<String, String> map : dbMapList) {
			String stage = map.get("stage");
			String profit = map.get("profit");
			// 调用RepayPlanDao里的getTotalByCode 计算出还款计划页面的总本息
			// double total = RepayPlanDao.getTotalByCode(proName);
			// if (stage.equals("募集期")) {
			// repayMoney = repayMoney + Double.parseDouble(profit);
			// } else {

			// }
		}
		expectedHashmap.put("需还款金额", MathUtil.format2(repayMoney));
		Map<String, String> dbMap = dbMapList.get(0);
		expectedHashmap.put("还款日期", dbMap.get("repay_date"));
		expectedHashmap.put("项目名称", dbMap.get("name"));
		expectedHashmap.put("融资规模",
				MathUtil.format2(MathUtil.format(dbMap.get("demand_amount"))));

		expectedHashmap.put("期限", dbMap.get("term"));
		expectedHashmap.put("预期年化利率", dbMap.get("year_rate"));
		expectedHashmap.put("还款方式", dbMap.get("repay_way"));
		expectedHashmap.put("实际融资规模", dbMap.get("collect"));
		expectedHashmap.put("发布时间", dbMap.get("time").substring(0, 10));
		return expectedHashmap;
	}

	/**
	 * xhh-3752:保理公司点击进入【还款】页面
	 * 
	 * @param kw
	 * @param projectName
	 * @return boolean
	 * @throws Exception
	 */
	public boolean viewRepayDetailPage(KeyWords kw, String projectName)
			throws Exception {
		// 项目对应的还款操作按钮xptah路径
		String xpathClickBtn = "//p[@class='pro_name' and contains(text(),'"
				+ projectName
				+ "')]/parent::node()/parent::node()/td/a[@class='button-small-info']";
		// 点击项目的还款按钮进入还款详情页
		kw.isVisible(By.xpath(xpathClickBtn));
		kw.click(By.xpath(xpathClickBtn));
		// log.info(ScreenShotUtil.takeScreenshot("mytest", kw.driver));
		// 判断跳转页面成功
		kw.waitPageLoad();
		boolean result = false;
		for (int i = 0; i < 5; i++) {
			result = kw.isElementVisible(OrUtil.getBy(
					"repay_detail_subtitle_xpath", ObjectLib.XhhObjectLib));
			if (result) {
				break;
			}
		}
		return result;
	}

	/**
	 * 获取还款明细页面的还款明细信息 xhh-3753:检查还款页面的字段及字段值
	 * 
	 * @param kw
	 * @return boolean
	 * @throws Exception
	 */
	public HashMap<String, String> getRepayDetail(KeyWords kw) throws Exception {
		HashMap<String, String> returnDetailHashmap = new HashMap<String, String>();
		returnDetailHashmap.put(
				"融资规模",
				filterString(kw.getWebElement(
						OrUtil.getBy("repay_detail_finance_scale_xpath",
								ObjectLib.XhhObjectLib)).getText()));
		returnDetailHashmap.put(
				"实际融资金额",
				filterString(kw.getWebElement(
						OrUtil.getBy("repay_detail_actual_amount_xpath",
								ObjectLib.XhhObjectLib)).getText()));
		returnDetailHashmap.put(
				"预期利率",
				kw.getWebElement(
						OrUtil.getBy("repay_detail_expected_rate_xpath",
								ObjectLib.XhhObjectLib)).getText());
		returnDetailHashmap.put(
				"期限",
				kw.getWebElement(
						OrUtil.getBy("repay_detail_time_limi_xpatht",
								ObjectLib.XhhObjectLib)).getText());
		returnDetailHashmap.put(
				"还款方式",
				kw.getWebElement(
						OrUtil.getBy("repay_detail_paytype_xpath",
								ObjectLib.XhhObjectLib)).getText()
						.replace(" ", ""));
		// returnDetailHashmap.put(
		// "融资开标时间",
		// kw.getWebElement(
		// OrUtil.getBy("repay_detail_bid_open_time_xpath",
		// ObjectLib.XhhObjectLib)).getText());
		// returnDetailHashmap.put(
		// "融资截止时间",
		// kw.getWebElement(
		// OrUtil.getBy("repay_detail_bid_deadline_xpath",
		// ObjectLib.XhhObjectLib)).getText());
		returnDetailHashmap.put(
				"还款进度",
				kw.getWebElement(
						OrUtil.getBy("repay_detail_progress_xpath",
								ObjectLib.XhhObjectLib)).getText()
						.replace("\n", ""));

		Set<String> srcSet = returnDetailHashmap.keySet();
		for (String dkey : srcSet) {
			System.out.println(dkey + "-->" + returnDetailHashmap.get(dkey));
		}
		return returnDetailHashmap;
	}

	/**
	 * 获得还款进度
	 * 
	 * @param proName
	 * @throws Exception
	 */
	public String getRepayPorgress(String proName) throws Exception {
		// 获得项目总的还款期数
		Map<String, String> dbMap = new HashMap<String, String>();
		dbMap = DbUtil.querySingleData(
				"select count(*) from product_repay_plan where name='"
						+ proName
						+ "' and stage<>'募集期' and stage not like '展期%'",
				DbType.Local);
		String totalStage = dbMap.get("count(*)");
		// 获得项目还款计划已还的期数
		dbMap = DbUtil
				.querySingleData(
						"select count(*) from product_repay_plan where name='"
								+ proName
								+ "' and stage<>'募集期' and stage<>'展期' and stage not like '展期%' and status='1'",
						DbType.Local);
		String payedStage = dbMap.get("count(*)");
		return payedStage + "/" + totalStage;
	}

	/**
	 * 从数据库获取还款明细页面的还款明细信息的期望值
	 * 
	 * @param kw
	 * @return HashMap
	 * @throws Exception
	 */
	public HashMap<String, String> getExpectedDetail(String proName)
			throws Exception {
		HashMap<String, String> returnDetailHashmap = new HashMap<String, String>();
		Map<String, String> dbMap = new HashMap<String, String>();
		dbMap = DbUtil
				.querySingleData(
						"select demand_amount,collect,term,year_rate,repay_way from productinfo where productinfo.prj_name='"
								+ proName + "'", DbType.Local);
		for (String key : dbMap.keySet()) {
			String value = dbMap.get(key);
			System.out.println("Key = " + key + ", Value = " + value);
		}
		returnDetailHashmap.put(
				"融资规模",
				"融资规模： "
						+ MathUtil.format2(MathUtil.format(dbMap
								.get("demand_amount"))));
		returnDetailHashmap
				.put("实际融资金额",
						"实际融资金额："
								+ MathUtil.format2(MathUtil.format(dbMap
										.get("collect"))));
		returnDetailHashmap
				.put("预期利率", "预期利率：" + dbMap.get("year_rate") + " 年");
		returnDetailHashmap.put("期限", "期限：" + dbMap.get("term"));
		returnDetailHashmap.put("还款方式", "还款方式：" + dbMap.get("repay_way"));
		returnDetailHashmap.put("还款进度", "还款进度：" + getRepayPorgress(proName));
		Set<String> srcSet = returnDetailHashmap.keySet();
		for (String dkey : srcSet) {
			System.out.println(dkey + "-->" + returnDetailHashmap.get(dkey));
		}
		return returnDetailHashmap;
	}

	/**
	 * xhh-3758:保理公司进行还款操作，点击还款明细页右下角的还款按钮以及弹出框的确认按钮
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void doRepay(KeyWords kw, String proName, String repayDate)
			throws Exception {
		// 点击还款明细页面下的“还款”按钮
		for (int i = 0; i < 10; i++) {

			boolean res = kw.isVisible(OrUtil.getBy(
					"repay_detail_repaybtn_xpath", ObjectLib.XhhObjectLib));
			if (res) {
				break;
			}
			ThreadUtil.sleep();
		}
		kw.click(OrUtil.getBy("repay_detail_repaybtn_xpath",
				ObjectLib.XhhObjectLib));
		// 点击确认按钮
		kw.click(OrUtil.getBy("repay_detail_pop_verify_xpath",
				ObjectLib.XhhObjectLib));
		// 点击取消按钮
		// kw.click(OrUtil.getBy("repay_detail_pop_cancel_xpath",
		// ObjectLib.XhhObjectLib));
		// 回写还款计划数据库表的还款状态
		updateRepayStatus(proName, repayDate);

	}

	/**
	 * 还款操作后回写还款表中的还款状态字段
	 * 
	 * @param proName
	 *            ,repayDate
	 * @throws Exception
	 * @throws Exception
	 */
	public void updateRepayStatus(String proName, String repayDate)
			throws Exception {
		// 更新产品还款计划表里的状态
		String sql = "update product_repay_plan set status='1' where name='"
				+ proName + "' and repay_date='" + repayDate + "'";
		DbUtil.update(sql, DbType.Local);
		// 更新用户还款计划表的状态
		sql = "update user_repay_plan set status='1' where name='" + proName
				+ "' and repay_date='" + repayDate + "'";
		DbUtil.update(sql, DbType.Local);
	}

	/**
	 * 还款操作后回写投资表状态
	 * 
	 * @param proName
	 * @throws Exception
	 * @throws Exception
	 */
	public void updateInvest(String proName, String repayDate, String status)
			throws Exception {
		String sql = "update invest_record set status='" + status
				+ "' where code='" + proName + "'and repay_date='" + repayDate
				+ "'";
		DbUtil.update(sql, DbType.Local);
	}

	/**
	 * 还款操作后回写投资表还款时间
	 * 
	 * @param proName
	 * @throws Exception
	 * @throws Exception
	 */
	public void updateInverstRD(String proName, String nextRepayDate,
			String repayDate) throws Exception {
		String sql = "update invest_record set repay_date ='" + nextRepayDate
				+ "'where code='" + proName + "' and repay_date='" + repayDate
				+ "'";
		DbUtil.update(sql, DbType.Local);
		System.out.println(sql);
	}

	/**
	 * 还款操作后回写用户账户表和用户资金记录表
	 * 
	 * @param proName
	 * @throws Exception
	 * @throws Exception
	 */
	public void updateAcct(String proName, String repayDate) throws Exception {
		String sql = "select distinct uid from user_repay_plan where name='"
				+ proName + "' and repay_date='" + repayDate + "'";
		List<Map<String, String>> uidList = DbUtil.queryDataList(sql,
				DbType.Local);
		for (int i = 0; i < uidList.size(); i++) {
			String uid = uidList.get(i).get("uid");
			// 从用户账户信息表获取当前用户信息
			sql = "select * from user_acct where uid=" + uid;
			Map<String, String> accMap = DbUtil.querySingleData(sql,
					DbType.Local);
			String asset = accMap.get("asset");// 账户净资产
			String balance = accMap.get("balance");// 账户余额
			String total_invest = accMap.get("total_invest");// 我的投资
			String will_profit = accMap.get("will_profit");// 待收收益
			String exist_profit = accMap.get("exist_profit");// 已赚收益
			// 从用户还款计划表获取当前用户在此项目的还款计划记录
			sql = "select * from user_repay_plan where name='" + proName
					+ "' and repay_date='" + repayDate + "' and uid='" + uid
					+ "'";
			List<Map<String, String>> userRepayList = DbUtil.queryDataList(sql,
					DbType.Local);
			BigDecimal totalMoney = new BigDecimal(0.00);// 应还本息总和
			BigDecimal totalCaptital = new BigDecimal(0.00);// 应还本金总和
			BigDecimal totalProfit = new BigDecimal(0.00);// 应还利息总和
			for (Map<String, String> map : userRepayList) {
				String total = map.get("total_money");// 应还本息
				String capital = map.get("capital");// 应还本金
				String profit = map.get("profit");// 应还利息
				String stage = map.get("stage");
				if (stage.equals("募集期")) {
					capital = "0.00";
				}
				totalMoney = totalMoney.add(new BigDecimal(total));
				totalCaptital = totalCaptital.add(new BigDecimal(capital));
				totalProfit = totalProfit.add(new BigDecimal(profit));
			}
			System.out.println("totalMoney=" + totalMoney + ";totalCaptital="
					+ totalCaptital + ";totalProfit=" + totalProfit);
			// 计算用户需要更新的字段数值
			asset = StringUtils.double2Str(new BigDecimal(asset).add(
					totalProfit).doubleValue());// 账户净资产
			balance = StringUtils.double2Str(new BigDecimal(balance).add(
					totalMoney).doubleValue());// 账户余额
			total_invest = StringUtils.double2Str(new BigDecimal(total_invest)
					.subtract(totalCaptital).doubleValue());// 我的投资
			will_profit = StringUtils.double2Str(new BigDecimal(will_profit)
					.subtract(totalProfit).doubleValue());// 待收收益
			exist_profit = StringUtils.double2Str(new BigDecimal(exist_profit)
					.add(totalProfit).doubleValue());// 已赚收益
			// TODO
			// 更新用户资产表
			sql = "update user_acct set asset='" + asset + "',balance='"
					+ balance + "',total_invest='" + total_invest
					+ "',will_profit='" + will_profit + "',exist_profit='"
					+ exist_profit + "' where uid='" + uid + "'";
			System.out.println(sql);
			DbUtil.update(sql, DbType.Local);
			// 用户资金记录表插入信息
			String abs = "项目“" + proName + "” 回款";
			String amount = StringUtils.double2Str(totalMoney.doubleValue());
			String date = SSHUtil.sshCurrentDate(log);
			sql = "insert into finance_record (uid,code,finance_type,amount,acct_balance,abstract,date) values("
					+ uid
					+ ",'"
					+ proName
					+ "','回款','+"
					+ amount
					+ "','"
					+ balance + "','" + abs + "','" + date + "')";
			System.out.println("updateAcct--sql string is :" + sql);
			DbUtil.insert(sql, DbType.Local);
		}
	}

	/**
	 * 还款操作后回写用户资金记录表，变现用的方法
	 * 
	 * @param proName
	 * @throws Exception
	 * @throws Exception
	 */
	public void updateAcct2(String proCode, String repayDate) throws Exception {
		String sql = "select distinct uid from user_repay_plan where name='"
				+ proCode + "' and repay_date='" + repayDate + "'";
		List<Map<String, String>> uidList = DbUtil.queryDataList(sql,
				DbType.Local);

		// 获得将原始项目变现的用户列表
		String proName = ProductTempDao.getProductNumberByCode(proCode).get(
				"product_name");
		List<Map<String, String>> userFastCashList = CashProjectRelatedDao
				.getUserFastCashList(proName, proName);
		List<String> uidFastCashList = new ArrayList<String>();
		for (Map<String, String> map : userFastCashList) {
			String fastCashUid = UserTempDao.getUid(
					map.get("fast_cash_user_number")).get("uid");
			uidFastCashList.add(fastCashUid);
		}

		for (int i = 0; i < uidList.size(); i++) {
			String uid = uidList.get(i).get("uid");
			// 从用户账户信息表获取当前用户信息
			sql = "select * from user_acct where uid=" + uid;
			Map<String, String> accMap = DbUtil.querySingleData(sql,
					DbType.Local);
			String balance = accMap.get("balance");// 账户余额
			// 从用户还款计划表获取当前用户在此项目的还款计划记录
			sql = "select * from user_repay_plan where name='" + proCode
					+ "' and repay_date='" + repayDate + "' and uid='" + uid
					+ "'";
			List<Map<String, String>> userRepayList = DbUtil.queryDataList(sql,
					DbType.Local);
			BigDecimal totalMoney = new BigDecimal(0.00);// 应还本息总和
			BigDecimal totalCaptital = new BigDecimal(0.00);// 应还本金总和
			BigDecimal totalProfit = new BigDecimal(0.00);// 应还利息总和
			for (Map<String, String> map : userRepayList) {
				String total = map.get("total_money");// 应还本息
				String capital = map.get("capital");// 应还本金
				String profit = map.get("profit");// 应还利息
				String stage = map.get("stage");
				if (stage.equals("募集期")) {
					capital = "0.00";
				}
				totalMoney = totalMoney.add(new BigDecimal(total));
				totalCaptital = totalCaptital.add(new BigDecimal(capital));
				totalProfit = totalProfit.add(new BigDecimal(profit));
			}
			System.out.println("totalMoney=" + totalMoney + ";totalCaptital="
					+ totalCaptital + ";totalProfit=" + totalProfit);
			// 如果该用户变现过原始项目，则回款金额需重新计算
			if (uidFastCashList.contains(uid)) {
				// 获得用户编号
				String userNumber = UserTempDao.getUserNumber(uid).get(
						"user_number");
				// 获得变现产品号
				String fastCashPrjNo = CashProjectRelatedDao.getFashCashByUser(
						userNumber, proName).get("fast_cash_project");
				// 获得关联的变现产品编号
				String fastCashPrjCode = ProductTempDao.getProductCodeByNumber(
						fastCashPrjNo).get("product_code");
				// 从用户还款计划获得该变现项目需还款的信息，并计算总还款金额
				double repaySum = RepayPlanDao.getTotalByCode(fastCashPrjCode);
				totalMoney = totalMoney.subtract(new BigDecimal(repaySum));
			}
			// 计算用户需要更新的字段数值
			balance = StringUtils.double2Str(new BigDecimal(balance).add(
					totalMoney).doubleValue());// 账户余额
			// 用户资金记录表插入信息
			String abs = "项目“" + proCode + "” 回款";

			String amount = StringUtils.double2Str(totalMoney.doubleValue());
			String date = SSHUtil.sshCurrentDate(log);
			sql = "insert into finance_record (uid,code,finance_type,amount,acct_balance,abstract,date) values("
					+ uid
					+ ",'"
					+ proCode
					+ "','回款','+"
					+ amount
					+ "','"
					+ balance + "','" + abs + "','" + date + "')";
			System.out.println("updateAcct--sql string is :" + sql);
			DbUtil.insert(sql, DbType.Local);
		}

		// 更新速兑通信息
		List<Map<String, String>> fastCashList = CashProjectRelatedDao
				.getFastCashList(proName);
		for (Map<String, String> map : fastCashList) {
			String fastCashPrj = map.get("fast_cash_project");
			String fastCashPrjCode = ProductTempDao.getProductCodeByNumber(
					fastCashPrj).get("product_code");
			addSDTFinanceRerd(fastCashPrjCode);
		}
		// 更新所有用户信息
		updateAcctForSDT(proName);

	}

	/**
	 * 速兑通项目更新用户账户信息与资金记录信息
	 * 
	 * @param projectCode
	 *            变现项目编号
	 * @param originUid
	 *            变现者
	 * @throws SQLException
	 * @throws IOException
	 */
	public void addSDTFinanceRerd(String projectCode) throws SQLException,
			IOException {
		DecimalFormat fm = new DecimalFormat("##0.00");

		// 取user_temp表中投资某个速兑通项目的投资人uid
		String sql2 = "select * from user_temp where user_number in(select user_number from user_product where product_number in (select product_id from product_temp where product_name='"
				+ projectCode + "'))";
		List<Map<String, String>> lst1 = DbUtil.queryDataList(sql2,
				DbType.Local);

		// 更新投资人的user_acct和速兑通出让人的user_acct
		for (int i = 0; i < lst1.size(); i++) {
			String sql1 = "select * from sdt_record where uid='"
					+ lst1.get(i).get("uid") + "'";
			Map<String, String> map1 = DbUtil.querySingleData(sql1,
					DbType.Local);

			// 取出user_repay_plan表的投资人为uid项目名称为sdt_name的还款计划
			String sql3 = "select * from user_repay_plan where uid='"
					+ lst1.get(i).get("uid") + "' and name='" + projectCode
					+ "'";

			// 取出user_acct表的uid的速兑通投资人用户的账户资金记录
			Map<String, String> map = DbUtil.querySingleData(
					"select * from user_acct where uid='"
							+ lst1.get(i).get("uid") + "'", DbType.Local);

			List<Map<String, String>> lst = DbUtil.queryDataList(sql3,
					DbType.Local);
			double totalCapital = 0.00; // 应还本金合计
			double totalProfit = 0.00; // 应还利息合计
			double totalmoney = 0.00; // 应还本息合计
			String balance;

			// 计算uid速兑通投资用户投资速兑通项目sdt_name的应还本金、利息、本息，然后更新该投资用户的user_acct和该速兑通出让人的user_acct
			for (int j = 0; j < lst.size(); j++) {
				totalCapital = totalCapital
						+ Double.parseDouble(lst.get(j).get("capital"));
				totalProfit = totalProfit
						+ Double.parseDouble(lst.get(j).get("profit"));
				totalmoney = totalmoney
						+ Double.parseDouble(lst.get(j).get("total_money"));

				// 原剩余总产+本息
				balance = fm.format(Double.parseDouble(map.get("balance"))
						+ totalmoney);

				// 用户资金记录表插入信息
				String abs = "项目“" + map1.get("sdt_name") + "” 回款";
				String amount = StringUtils.double2Str(totalmoney);
				String date = SSHUtil.sshCurrentDate(log);
				String sql = "insert into finance_record (uid,code,finance_type,amount,acct_balance,abstract,date) values("
						+ lst1.get(i).get("uid")
						+ ",'"
						+ map1.get("sdt_name")
						+ "','回款','+"
						+ amount
						+ "','"
						+ balance
						+ "','"
						+ abs
						+ "','" + date + "')";
				System.out.println("updateAcct--sql string is :" + sql);
				DbUtil.insert(sql, DbType.Local);
			}
		}

	}

	/**
	 * 速兑通更新用户信息
	 * 
	 * @param relatedProject
	 * @throws SQLException
	 */
	public void updateAcctForSDT(String relatedProject) throws SQLException {
		DecimalFormat fm = new DecimalFormat("##0.00");
		// 找到相关的项目
		List<Map<String, String>> list = CashProjectRelatedDao
				.getFastCashList(relatedProject);
		List<String> prjList = new ArrayList<String>();
		for (Map<String, String> map : list) {
			String originPrj = map.get("origin_project");
			String fastCashPrj = map.get("fast_cash_project");
			if (!prjList.contains(originPrj)) {
				prjList.add(originPrj);
			}
			if (!prjList.contains(fastCashPrj)) {
				prjList.add(fastCashPrj);
			}
		}
		// 找到相关的用户
		List<String> usrList = new ArrayList<String>();
		for (String prj : prjList) {
			List<Map<String, String>> userListByPrj = UserProductDao
					.getUserListByProduct(prj);
			for (Map<String, String> map : userListByPrj) {
				if (!usrList.contains(map.get("user_number"))) {
					usrList.add(map.get("user_number"));
				}
			}
		}

		for (String uNo : usrList) {
			String uid = UserTempDao.getUid(uNo).get("uid");
			// 取出速兑通出让人的账户资金记录
			Map<String, String> map2 = DbUtil.querySingleData(
					"select * from user_acct where uid='" + uid + "'",
					DbType.Local);
			// 更新速兑通项目sdt_name的出让人的user_acct

			// 原净资产+待收收益
			String Passet = fm.format(Double.parseDouble(map2.get("asset"))
					+ Double.parseDouble(map2.get("will_profit")));
			// 原剩余总产+（投资金额-变现借款+待收收益）
			String Pbalance = fm
					.format(Double.parseDouble(map2.get("balance"))
							+ (Double.parseDouble(map2.get("total_invest"))
									- Double.parseDouble(map2.get("cash_loan")) + Double
										.parseDouble(map2.get("will_profit"))));
			// 总收益不变
			String Ptotal_profit = fm.format(Double.parseDouble(map2
					.get("total_profit")));
			// 待收收益设置为0
			String Pwill_profit = "0.00";
			// 已赚收益=原已赚收益+待收收益
			String Pexist_profit = fm.format(Double.parseDouble(map2
					.get("exist_profit"))
					+ Double.parseDouble(map2.get("will_profit")));
			// 变现借款设置为0
			String Pcash_loan = "0.00";
			// 投资金额设置为0
			String Ptotal_invest = "0.00";

			String sql5 = "update user_acct set asset='" + Passet
					+ "',balance='" + Pbalance + "',total_invest='"
					+ Ptotal_invest + "',will_profit='" + Pwill_profit
					+ "',exist_profit='" + Pexist_profit + "' ,cash_loan='"
					+ Pcash_loan + "' where uid=" + uid;
			System.out.println(sql5);
			DbUtil.update(sql5, DbType.Local);
		}
	}

	/**
	 * 还款操作后回写保理资金流表
	 * 
	 * @param proName
	 * @throws Exception
	 * @throws Exception
	 */
	public void updateFund(String proName, String repayDate) throws Exception {
		String sql = "select * from user_repay_plan where name='"
				+ proName
				+ "' and repay_date='"
				+ repayDate
				+ "'and id not in(select id from user_repay_plan where stage='募集期' and total_money='0.00')";
		List<Map<String, String>> dbRecords = DbUtil.queryDataList(sql,
				DbType.Local);
		for (Map<String, String> map : dbRecords) {
			String total = "-" + map.get("total_money");// 应还本息
			sql = "insert fund_record (pro_name,type,amout)values('" + proName
					+ "','回款冻结','" + total + "')";
			System.out.println("updateFund() :excute sql is:" + sql);
			DbUtil.insert(sql, DbType.Local);
		}
	}

	/**
	 * 校验还款详细页面的还款进度 ：已还期次/总期次
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public boolean checkRepayProgress(KeyWords kw, String proName)
			throws Exception {
		// 确认提交完成，返回主窗口，确认进度条可见
		kw.isElementVisible(OrUtil.getBy("repay_detail_progress_icon_xpath",
				ObjectLib.XhhObjectLib));
		// ThreadUtil.sleep(10);
		// 等待进度浮层消失
		while (true) {
			ThreadUtil.sleep();
			if (!kw.isVisible(By.id("myprocess"))) {
				break;
			}
		}

		kw.waitPageLoad();
		String s = kw.getWebElement(
				OrUtil.getBy("repay_detail_progress_xpath",
						ObjectLib.XhhObjectLib)).getText();
		String[] ss = s.split("[\\t \\n]+");
		System.out.println("还款进度为实际值为：" + ss[1]);
		System.out.println("还款进度期望值为：" + getRepayPorgress(proName));
		return StringUtils.isEquals(getRepayPorgress(proName), ss[1]);
	}

	/**
	 * 获取表单内容
	 * 
	 * @param ：kw isPaging 是否存在分页 xpath_tbody xpath_nextpage xpath_total_page
	 *        xpath_curPage
	 * @throws Exception
	 */
	public List<String> getAllPageRecordsStrList(KeyWords kw, boolean isPaging,
			String xpath_tbody, String xpath_nextpage, String xpath_total_page,
			String xpath_curPage) throws Exception {
		List<WebElement> curPageRecordsList = new ArrayList<WebElement>();
		List<String> allRecordsStrList = new ArrayList<String>();

		int currentPageNum;
		if (isPaging) {
			log.info("存在分页！");
			int totalPageNum = Integer.parseInt(kw.getWebElement(
					OrUtil.getBy(xpath_total_page, ObjectLib.XhhObjectLib))
					.getText());

			while (true) {
				curPageRecordsList = kw.getWebElements(OrUtil.getBy(
						xpath_tbody, ObjectLib.XhhObjectLib));
				for (int i = 0; i < curPageRecordsList.size(); i++) {
					WebElement curWebEle = curPageRecordsList.get(i);
					allRecordsStrList.add(curWebEle.getText());
				}

				currentPageNum = Integer.parseInt(kw.getWebElement(
						OrUtil.getBy(xpath_curPage, ObjectLib.XhhObjectLib))
						.getText());
				if (currentPageNum < totalPageNum) {
					log.info("当前页是：" + currentPageNum + ",总页数是：" + totalPageNum);
					log.info("点击下一页");
					kw.click(OrUtil.getBy(xpath_nextpage,
							ObjectLib.XhhObjectLib));
					kw.waitPageLoad();
					ThreadUtil.sleep(2);
				} else {
					log.info("当前页是：" + currentPageNum + ",总页数是：" + totalPageNum
							+ "。当前页和总页数相同，break跳出！");
					break;
				}
			}
			log.info("还款详情页总条数为：" + allRecordsStrList.size());

		} else {
			log.info("不存在分页");
			for (int loop = 0; loop < 10; loop++) {
				curPageRecordsList = kw.getWebElements(OrUtil.getBy(
						xpath_tbody, ObjectLib.XhhObjectLib));
				if (curPageRecordsList.size() == 0) {
					ThreadUtil.sleep();
				} else {
					break;
				}

			}
			System.out.println(curPageRecordsList.size());
			for (WebElement curWebEle : curPageRecordsList) {
				allRecordsStrList.add(curWebEle.getText());
			}
		}
		return allRecordsStrList;
	}

	/**
	 * 从数据库获取当前还款计划详细信息表单的期值,并进行当期还款明细记录的比对
	 * 
	 * @param
	 * @throws Exception
	 * @throws Exception
	 */

	public boolean checkRepayTableRecords(String proName, String repayDate,
			List<String> showList) throws Exception {
		// 从用户还款计划表获取相关记录
		String sql = "select * from user_repay_plan where name='"
				+ proName
				+ "' and repay_date='"
				+ repayDate
				+ "'and id not in(select id from user_repay_plan where stage='募集期' and total_money='0.00')";
		List<Map<String, String>> expectList = DbUtil.queryDataList(sql,
				DbType.Local);
		String expStr;
		List<String> expStrList = new ArrayList<String>();
		for (Map<String, String> ele : expectList) {
			expStr = ele.get("name") + " " + ele.get("bus_uid") + " "
					+ ele.get("stage") + " " + ele.get("invest_amount") + " "
					+ ele.get("total_money") + " " + ele.get("repay_date");
			expStrList.add(expStr);
		}
		// 去除界面获得的资金额度显示中的逗号
		for (int i = 0; i < showList.size(); i++) {
			showList.set(i, showList.get(i).replace(",", ""));
		}
		// 打印一下两个list的内容
		log.info("期望的值如下：");
		for (String ele : expStrList) {
			log.info(ele);
		}
		log.info("实际的值如下：");
		for (String ele : showList) {
			log.info(ele);
		}
		return StringUtils.checkContentSame(expStrList, showList, log);
	}

	/**
	 * 设置资金记录页面下的时间区间
	 * 
	 * @param ：kw，startDate，endDate
	 * @throws Exception
	 */
	public void setDate(KeyWords kw, String startDate, String endDate)
			throws Exception {
		kw.setValue(OrUtil.getBy("cash_recorad_start_time_xpath",
				ObjectLib.XhhObjectLib), startDate);
		kw.setValue(OrUtil.getBy("cash_recorad_end_time_xpath",
				ObjectLib.XhhObjectLib), endDate);
		// 点击搜索按钮
		kw.click(OrUtil.getBy("cash_recorad_searchbtn_xpath",
				ObjectLib.XhhObjectLib));
		ThreadUtil.sleep(3);
	}

	/**
	 * 返回前count条记录
	 * 
	 * @param ：kw，xpath_tbody，count
	 * @throws Exception
	 */
	public List<String> getTopRecordsStrList(KeyWords kw, String xpath_tbody,
			int count) throws Exception {
		List<WebElement> curPageRecordsList = new ArrayList<WebElement>();
		List<String> allRecordsStrList = new ArrayList<String>();
		List<String> topRecordsStrList = new ArrayList<String>();
		int read = 0;
		while (true) {
			System.out.println("read=" + read + ";count=" + count);
			curPageRecordsList = kw.getWebElements(OrUtil.getBy(xpath_tbody,
					ObjectLib.XhhObjectLib));
			for (int i = 0; i < curPageRecordsList.size(); i++) {
				WebElement curWebEle = curPageRecordsList.get(i);
				allRecordsStrList.add(curWebEle.getText());
			}
			read = read + curPageRecordsList.size();
			if (read >= count) {
				break;
			}
			kw.click(OrUtil.getBy("repay_next_page_xpath",
					ObjectLib.XhhObjectLib));
			ThreadUtil.sleep(2);
		}
		for (int j = 0; j < count; j++) {
			topRecordsStrList.add(allRecordsStrList.get(j));
		}
		return topRecordsStrList;
	}

	/**
	 * 检验资金记录
	 * 
	 * @param ：showList 界面显示的资金记录
	 * @throws Exception
	 */
	public boolean checkCashRecord(List<String> showList) throws Exception {
		int compLine = showList.size();
		int i;
		Map<String, String> dbLine = new HashMap<String, String>();
		List<Map<String, String>> dbLines = new ArrayList<Map<String, String>>();
		List<Map<String, String>> showLines = new ArrayList<Map<String, String>>();
		List<Map<String, String>> queryDB = new ArrayList<Map<String, String>>();

		// 从数据库中读取最新需要比对的数据,从最新一条往前取compLine条
		String sql = "select * from fund_record order by id desc limit "
				+ compLine;
		queryDB = DbUtil.queryDataList(sql, DbType.Local);
		for (i = 0; i < queryDB.size(); i++) {
			Map<String, String> tmp = new HashMap<String, String>();
			dbLine = queryDB.get(i);
			tmp.put("类型", dbLine.get("type"));
			tmp.put("交易金额", dbLine.get("amout"));
			dbLines.add(tmp);
			log.info("类型=[" + dbLine.get("type") + "];交易金额=["
					+ dbLine.get("amout") + "]");
		}
		// 从showlist中提取需要比对“类型”和“交易金额”的内容转存到map中，便于比较
		for (i = 0; i < compLine; i++) {
			Map<String, String> tmp = new HashMap<String, String>();
			String[] ss = showList.get(i).split("[\\t \\n]+");
			tmp.put("类型", ss[2]);
			tmp.put("交易金额", ss[3].replace(",", ""));
			showLines.add(tmp);
		}
		boolean result = StringUtils.compareList(dbLines, showLines, log);
		return result;
	}

	/**
	 * 检验还款情况签页记录
	 * 
	 * @param ：showList 界面显示的资金记录
	 * @throws Exception
	 * @throws Exception
	 */
	public boolean checkRepayConRecords(String proName, List<String> showList)
			throws Exception {
		String sql = "select * from product_repay_plan where code='" + proName
				+ "'";
		List<Map<String, String>> expectList = DbUtil.queryDataList(sql,
				DbType.Local);
		boolean res = true;
		boolean finalres = true;
		String expStr;
		Map<String, String> ele = new HashMap<String, String>();
		if (!(expectList.size() == showList.size())) {
			finalres = false;
			log.info("记录条数不相等，期望值为：" + expectList.size() + ";实际值为:"
					+ showList.size() + "!");
			return finalres;
		}
		for (int i = 0; i < expectList.size(); i++) {
			ele = expectList.get(i);
			expStr = ele.get("stage") + " " + ele.get("repay_date") + " "
					+ ele.get("capital") + " " + ele.get("profit") + " "
					+ ele.get("remain_capital") + " " + "待还";
			res = StringUtils.isEquals(expStr, showList.get(i));
			if (res == false) {
				System.out.println("字符串不相等：期望值：" + expStr + "；实际值："
						+ MathUtil.format2(MathUtil.format(showList.get(i))));
				finalres = false;
			} else {
				System.out.println("字符串相等：期望值：" + expStr + "；实际值："
						+ MathUtil.format2(MathUtil.format(showList.get(i))));
			}
		}
		return finalres;

	}

	/**
	 * 检验用户资金记录界面最新一条数据
	 * 
	 * @param ：repayDate,uid,kw
	 * @throws Exception
	 */
	public boolean checkUserRepayFinanceRecord(String proName,
			String repayDate, String uid, KeyWords kw) throws Exception {
		String sql = "select * from finance_record where date='" + repayDate
				+ "' and uid='" + uid + "' and code='" + proName + "'";
		System.out.println("sql is :" + sql);
		Map<String, String> dbmap = DbUtil.querySingleData(sql, DbType.Local);
		Map<String, String> expectMap = DbUtil.querySingleData(sql,
				DbType.Local);
		expectMap.put("时间", dbmap.get("date"));
		expectMap.put("类型", dbmap.get("finance_type"));
		expectMap.put("交易金额", dbmap.get("amount"));
		expectMap.put("账户余额", dbmap.get("acct_balance"));
		expectMap.put("摘要", dbmap.get("abstract"));
		Map<String, String> showmap = new HashMap<String, String>();
		showmap.put(
				"时间",
				kw.getWebElement(
						By.xpath("//div[@id='ajaxContent_Payment_PayAccount_getMyMoneyList']/div/table/tbody/tr[1]/td[2]/preceding-sibling::*[1]/child::*"))
						.getText().trim().substring(0, 10));
		showmap.put(
				"类型",
				kw.getWebElement(
						By.xpath("//div[@id='ajaxContent_Payment_PayAccount_getMyMoneyList']/div/table/tbody/tr[1]/td[2]"))
						.getText().trim());
		showmap.put(
				"交易金额",
				kw.getWebElement(
						By.xpath("//div[@id='ajaxContent_Payment_PayAccount_getMyMoneyList']/div/table/tbody/tr[1]/td[2]/following-sibling::*[1]/child::*"))
						.getText().trim().replace(",", ""));
		showmap.put(
				"账户余额",
				kw.getWebElement(
						By.xpath("//div[@id='ajaxContent_Payment_PayAccount_getMyMoneyList']/div/table/tbody/tr[1]/td[4]"))
						.getText().trim().replace(",", ""));
		showmap.put(
				"摘要",
				kw.getWebElement(
						By.xpath("//div[@id='ajaxContent_Payment_PayAccount_getMyMoneyList']/div/table/tbody/tr[1]/td[5]"))
						.getText().trim());

		return StringUtils.isEquals(expectMap, showmap, log);

	}

	/**
	 * 校验还款情况签页
	 * 
	 * @param ：proName,showList
	 * @throws Exception
	 */

	public boolean checkConditionTable(String proName, List<String> showList)
			throws Exception {
		boolean result = true;
		String sql = "select  * from product_repay_plan where name='" + proName
				+ "' order by id";
		List<Map<String, String>> dbMapList = DbUtil.queryDataList(sql,
				DbType.Local);
		double mujiqi = 0.00;// 募集期利息
		double zhanqi = 0.00;// 展期利息
		for (int i = 0; i < dbMapList.size(); i++) {
			Map<String, String> map = dbMapList.get(i);
			String stage = map.get("stage");
			String profit = map.get("profit");
			if (stage.equals("募集期")) {
				mujiqi = Double.parseDouble(profit);
			} else if (stage.contains("展期")) {
				zhanqi = Double.parseDouble(profit);
			}
		}

		for (int i = 0; i < showList.size(); i++) {
			Map<String, String> map = dbMapList.get(i + 1);
			String stage = map.get("stage");
			String repayDate = map.get("repay_date");
			String capital = map.get("capital");
			String profit = map.get("profit");
			String remainCapital = map.get("remain_capital");
			String status = map.get("status");
			// double mujiqi = 0.00;// 募集期利息
			// double zhanqi = 0.00;// 展期利息
			String dbLine = "";
			if (status.equals("0")) {
				status = "待还";
			} else {
				status = "√ 查看明细";
			}
			if (!stage.equals("募集期") && !stage.equals("展期利息")) {
				// mujiqi = Double.parseDouble(profit);
				// } else if (stage.equals("展期利息")) {
				// zhanqi = Double.parseDouble(profit);
				// } else {
				String regEx = "[^0-9]";
				Pattern p = Pattern.compile(regEx);
				Matcher m = p.matcher(stage);
				stage = m.replaceAll("").trim();
				if (stage.equals("1")) {
					profit = MathUtil.format2(Double.parseDouble(profit)
							+ mujiqi + zhanqi);
				}
				dbLine = stage + " " + repayDate + " " + capital + " " + profit
						+ " " + remainCapital + " " + status;
				String showLine = showList.get(i).replace(",", "");
				if (StringUtils.isEquals(dbLine, showLine)) {
					log.info("校验内容相等：期望值为：" + dbLine + ";实际值为：" + showLine);
				} else {
					log.error("校验内容不相等：期望值为：" + dbLine + ";实际值为：" + showLine);
					result = false;
				}
			}
		}
		return result;

	}

	/**
	 * 展期还款
	 * 
	 * @param kw
	 * @param proName
	 * @throws Exception
	 */
	public void doExtendRepay(KeyWords kw, String proName) throws Exception {
		// 点击还款明细页面下的“展期还款”按钮
		kw.isVisible(OrUtil.getBy("repay_extend_repaybtn_xpath",
				ObjectLib.XhhObjectLib));
		kw.click(OrUtil.getBy("repay_extend_repaybtn_xpath",
				ObjectLib.XhhObjectLib));
		// 点击确认按钮
		kw.click(OrUtil.getBy("repay_detail_pop_verify_xpath",
				ObjectLib.XhhObjectLib));
		// 点击取消按钮
		// kw.click(OrUtil.getBy("repay_detail_pop_cancel_xpath",
		// ObjectLib.XhhObjectLib));
	}

	/**
	 * 展期后更新用户投资记录的利息和还款日期
	 * 
	 * @param proName
	 * @param uid
	 */
	public void updateInvestRecord(String proName, String uid) {

	}

	/**
	 * 展期后更新项目还款计划的需支付收益、总还款金额、还款日期
	 * 
	 * @param proName
	 * @param uid
	 * @param stage
	 */
	public void updateProRepayPlan(String proName, String uid, String stage) {

	}

	/**
	 * 展期后更新用户还款计划的收益、总收益、还款日期
	 * 
	 * @param proName
	 * @param uid
	 * @param stage
	 */
	public void updateUserPepayPlan(String proName, String uid, String stage) {

	}

	/**
	 * 展期后更新用户账户的待收收益、总收益
	 * 
	 * @param uid
	 */
	public void updateUserAcct(String uid) {

	}
}
