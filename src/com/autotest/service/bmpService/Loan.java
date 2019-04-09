package com.autotest.service.bmpService;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.autotest.dao.AccountDao;
import com.autotest.dao.FundRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.RepayPlanDao;
import com.autotest.dao.UserAcctDao;
import com.autotest.driver.KeyWords;
import com.autotest.enums.DbType;
import com.autotest.or.ObjectLib;
import com.autotest.service.xhhService.RepayPlanService;
import com.autotest.utility.DateUtils;
import com.autotest.utility.DbUtil;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;
import com.autotest.utility.UserAgentUtil;

/**
 * 
 * @author zhangyijie
 * 
 */

public class Loan {

	private Logger log = null;

	public Loan(Logger log) {
		this.log = log;
	}

	/**
	 * 将字符串转换 为每3位逗号分隔的格式
	 * 
	 * @param str
	 * @return
	 */
	public static String formatStr(String str) {
		DecimalFormat fm = new DecimalFormat("###,##0.00");// 将数据为财务格式
		str = fm.format(Double.parseDouble(str));

		return str;
	}

	/**
	 * 根据code获取数据库保理公司资金记录中的类型、交易金额、摘要
	 * 
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getfinanceinfo(String productCode)
			throws SQLException {

		List<Map<String, String>> lst = DbUtil.queryDataList(
				"select type,amout,abstract from fund_record where pro_name='"
						+ productCode + "'", DbType.Local);

		for (int i = 0; i < lst.size(); i++) {
			String Type = lst.get(i).get("type");
			if (Type.equals("项目支付")) {
				lst.get(i)
						.put("amout", Loan.formatStr(lst.get(i).get("amout")));
			} else {
				lst.get(i).put("amout",
						"+" + Loan.formatStr(lst.get(i).get("amout")));
			}
		}
		return lst;
	}

	/**
	 * 根据product_id获取数据库product_temp表的信息
	 * 
	 * @throws SQLException
	 */
	public static Map<String, String> getprodtemp(String productid)
			throws SQLException {
		return DbUtil.querySingleData(
				"select * from product_temp where product_id='" + productid
						+ "'", DbType.Local);
	}

	/**
	 * 根据prj_name获取数据库productinfo表的数据并将以募集金额和融资规模的格式转换为逗号分隔
	 * 
	 * @throws SQLException
	 */
	public static Map<String, String> getprodinfo(String productCode)
			throws SQLException {

		Map<String, String> prodinfo = DbUtil.querySingleData(
				"select * from productinfo where prj_name='" + productCode
						+ "'", DbType.Local);
		prodinfo.put("collect", Loan.formatStr(prodinfo.get("collect")));
		prodinfo.put("demand_amount",
				Loan.formatStr(prodinfo.get("demand_amount")));

		return prodinfo;
	}

	/**
	 * 根据name获取数据库表中的利息
	 * 
	 * @param productCode
	 * @return
	 * @throws SQLException
	 */
	public static String getprofit(String productCode) throws SQLException {
		List<Map<String, String>> getProfit = DbUtil.queryDataList(
				"select profit from product_repay_plan where name='"
						+ productCode + "'", DbType.Local);
		double sumprofit = 0;
		for (int i = 0; i < getProfit.size(); i++) {
			sumprofit = sumprofit
					+ Double.parseDouble(getProfit.get(i).get("profit"));
		}
		DecimalFormat fm = new DecimalFormat("###,##0.00");
		String Sprofit = fm.format(sumprofit);
		return Sprofit;

	}

	/**
	 * 根据product_id获取loan_temp表中的信息
	 * 
	 * @param prodictid
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getloantemp(String productid)
			throws SQLException {
		return DbUtil.querySingleData(
				"select * from loan_temp where product_id='" + productid + "'",
				DbType.Local);
	}

	/**
	 * 访问【财务管理-项目放款】页面
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean EnterLoan(KeyWords kw) throws Exception {
		boolean result = false;
		// 点击左侧菜单栏的【财务管理】，在下拉菜单中点击【项目放款】菜单
		kw.click(OrUtil.getBy("index_financialManagement_xpath",
				ObjectLib.BMPObjectLib));
		kw.click(OrUtil
				.getBy("index_projectLoan_xpath", ObjectLib.BMPObjectLib));
		kw.waitPageLoad();
		kw.switchToFrame("work_frame");
		if (kw.isElementExist(By.id("work_frame"))) {
			result = true;
		}
		return result;
	}

	/**
	 * 在项目放款页面筛选出保理项目
	 * 
	 * @param kw
	 * @param productCode
	 *            项目名称
	 * @param productid
	 *            项目id
	 * @param prodinfo
	 *            productinfo表数据
	 * @return
	 * @throws Exception
	 */

	public boolean Checkprod(KeyWords kw, String productCode, String productid,
			Map<String, String> prodinfo, String getProfit) throws Exception {
		boolean result = false;

		Map<String, String> destMap = new HashMap<String, String>();
		Map<String, String> srcMap = new HashMap<String, String>();

		int investcount = DbUtil.getCount(
				"select count(*) from invest_record where code='" + productCode
						+ "'", DbType.Local);
		// 上方筛选栏，【项目名称】输入
		kw.setValue(OrUtil.getBy("project_name_id", ObjectLib.BMPObjectLib),
				productCode);
		// 点击筛选栏最末的【查询】按钮
		kw.click(OrUtil.getBy("Loan_search_xpath", ObjectLib.BMPObjectLib));
		// 判断是否查询到项目
		kw.isElementExist(OrUtil.getBy("search_result_xpath",
				ObjectLib.BMPObjectLib));
		// 校验筛选结果列表展示的字段及部分字段值
		// 项目名称
		destMap.put("prodname", kw.getText(OrUtil.getBy(
				"Loan_productname_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("prodname", prodinfo.get("prj_name") + "(订单数:0/"
				+ investcount + ")");

		// 查看子标
		destMap.put("substandard", kw.getText(OrUtil.getBy(
				"viewsubstandard_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("substandard",
				Loan.getloantemp(productid).get("sub_standard"));

		// 主合同签约进度
		destMap.put("maincontract", kw.getText(OrUtil.getBy(
				"maincontract_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("maincontract",
				Loan.getloantemp(productid).get("main_contract"));

		// 确认书签约进度
		destMap.put("confirmation", kw.getText(OrUtil.getBy(
				"Confirmation_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("confirmation",
				Loan.getloantemp(productid).get("confirmation"));

		// 转入方账户
		destMap.put("inaccount", kw.getText(OrUtil.getBy("inaccount_xpath",
				ObjectLib.BMPObjectLib)));

		srcMap.put("inaccount", prodinfo.get("account_name"));

		// 平台管理费
		destMap.put("managementfee", kw.getText(OrUtil.getBy(
				"managementfee_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("managementfee",
				Loan.getloantemp(productid).get("management_fee"));

		// 保证金/融资管理费
		destMap.put("bond",
				kw.getText(OrUtil.getBy("bond_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("bond", Loan.getloantemp(productid).get("bond"));

		// 需支付额度
		destMap.put("needpayamount", kw.getText(OrUtil.getBy(
				"needpayamount_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("needpayamount", prodinfo.get("collect"));

		// 投放站点
		destMap.put("site",
				kw.getText(OrUtil.getBy("site_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("site", prodinfo.get("mi_no"));

		// 融资规模
		destMap.put("financingscale", kw.getText(OrUtil.getBy(
				"financingscale_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("financingscale", prodinfo.get("demand_amount"));

		// 线上实际融资
		destMap.put("actualfinacamount", kw.getText(OrUtil.getBy(
				"Actualfinacamount_xpath", ObjectLib.BMPObjectLib)));

		srcMap.put("actualfinacamount", prodinfo.get("collect"));

		// 收益
		destMap.put("loanprofit", kw.getText(OrUtil.getBy("Loanprofit_xpath",
				ObjectLib.BMPObjectLib)));

		srcMap.put("loanprofit", getProfit);

		result = StringUtils.isEquals(srcMap, destMap, log);

		return result;

	}

	/**
	 * 点击支付保理项目
	 * 
	 * @param kw
	 * @param payment
	 *            支付金额
	 * @return
	 * @throws Exception
	 */
	public boolean Checkpayment(KeyWords kw, Map<String, String> prodinfo)
			throws Exception {
		boolean result = false;
		// 点击【支付】按钮
		kw.click(OrUtil.getBy("search_result_xpath", ObjectLib.BMPObjectLib));
		kw.isElementExist(OrUtil.getBy("payment_xpath", ObjectLib.BMPObjectLib));
		String payamount = kw.getText(OrUtil.getBy("payamount_id",
				ObjectLib.BMPObjectLib));
		String payment = prodinfo.get("collect");
		String exceptpayamount = payment.substring(0, payment.indexOf("."))
				+ "元";
		result = StringUtils.isEquals(exceptpayamount, payamount);

		return result;
	}

	/**
	 * 填写支付流水号 确认支付保理项目 判断是否项目支付完成
	 * 
	 * @param kw
	 * @param serialnumber
	 *            流水号
	 * @return
	 * @throws Exception
	 */
	public boolean Finishpay(KeyWords kw, String serialnumber) throws Exception {
		boolean result = false;
		boolean tflag = false;
		// 填写支付流水号
		kw.setValue(OrUtil.getBy("serialnumber_id", ObjectLib.BMPObjectLib),
				serialnumber);

		// 点击[支付]提示框右下角的【提交】按钮

		kw.click(OrUtil.getBy("confirm_xpath", ObjectLib.BMPObjectLib));

		// 判断等待框是否消失，返回为false则等待框给消失
		long beginTime = new Date().getTime();
		do {
			tflag = kw.isElementVisible(OrUtil.getBy("processbar_id",
					ObjectLib.BMPObjectLib));
			ThreadUtil.sleep();
			long endTime = new Date().getTime();
			if ((endTime - beginTime) / 1000 > 600) {
				break;
			}
		} while (tflag == true);

		// 判断是否项目支付完成

		String alerttext = kw.getText(OrUtil.getBy("completion_payment_xpath",
				ObjectLib.BMPObjectLib));
		result = StringUtils.isEquals("项目支付完成", alerttext);

		return result;
	}

	/**
	 * 确认保理项目支付结果
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean Confirmresult(KeyWords kw) throws Exception {
		boolean result = false;
		// 点击提示框【项目支付完成】的【确认】按钮
		kw.click(OrUtil.getBy("paidconfirm_xpath", ObjectLib.BMPObjectLib));
		// 检查【项目放款-待支付】页签下记录
		String tabletxt = kw.getText(OrUtil.getBy("nodata_xpath",
				ObjectLib.BMPObjectLib));
		result = StringUtils.isEquals("没有数据", tabletxt);
		return result;
	}

	/**
	 * 支付后，保理公司访问【资金记录】页面
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean Enterbaoli(KeyWords kw) throws Exception {
		boolean result = false;
		// 点击菜单栏的【资金记录】入口
		kw.click(OrUtil.getBy("fundrecord_xpath", ObjectLib.XhhObjectLib));
		kw.waitPageLoad();

		if (kw.isElementExist(OrUtil.getBy("in-out_records_id",
				ObjectLib.XhhObjectLib))) {
			result = true;
		}

		return result;
	}

	/**
	 * 获取服务器当前时间并在资金记录页面查询
	 * 
	 * @param kw
	 * @param repayService
	 * @throws Exception
	 */
	public void setdate(KeyWords kw, RepayPlanService repayService)
			throws Exception {
		String searchdate = DateUtils.simpleDateFormat2();
		repayService.setDate(kw, searchdate, searchdate);
		kw.click(OrUtil.getBy("fundsearch_xpath", ObjectLib.XhhObjectLib));

	}

	/**
	 * 支付后，保理公司账户校验资金流水
	 * 
	 * @param kw
	 * @param productCode
	 *            项目名称
	 * @param Expectlist
	 * @return
	 * @throws Exception
	 */

	public boolean getdata(KeyWords kw, List<String> showList,
			String productCode) throws Exception {

		int compLine = showList.size();
		int i;
		Map<String, String> dbLine = new HashMap<String, String>();
		List<Map<String, String>> dbLines = new ArrayList<Map<String, String>>();
		List<Map<String, String>> showLines = new ArrayList<Map<String, String>>();
		List<Map<String, String>> queryDB = new ArrayList<Map<String, String>>();

		// 从数据库中读取数据
		String sql = "select * from fund_record where pro_name='" + productCode
				+ "' ";
		queryDB = DbUtil.queryDataList(sql, DbType.Local);
		for (i = 0; i < queryDB.size(); i++) {
			Map<String, String> tmp = new HashMap<String, String>();
			dbLine = queryDB.get(i);
			tmp.put("类型", dbLine.get("type"));
			tmp.put("交易金额", dbLine.get("amout"));
			tmp.put("摘要", dbLine.get("abstract"));
			dbLines.add(tmp);
		}
		// 从showlist中提取需要比对“类型”、“摘要”和“交易金额”的内容转存到map中，便于比较
		for (i = 0; i < compLine; i++) {
			Map<String, String> tmp = new HashMap<String, String>();
			String[] ss = showList.get(i).split("[\\t \\n]+");
			tmp.put("类型", ss[2]);
			tmp.put("交易金额", ss[3].replace(",", ""));
			if (ss.length > 7) {
				tmp.put("摘要", ss[5] + " " + ss[6] + " " + ss[7]);

			} else {
				tmp.put("摘要", ss[5]);
			}
			showLines.add(tmp);
		}
		if (dbLines.size() == showLines.size()) {
			return StringUtils.compareList(dbLines, showLines, log);
		} else {
			log.error("页面获取数据和数据库获取数据条数不相等！");
			return false;
		}

	}

	/**
	 * 支付完成后回写数据库资金记录表
	 * 
	 * @param productCode
	 * @throws SQLException
	 */
	public void fundrecordwrite(String productCode,
			List<Map<String, String>> userlst) throws IOException, SQLException {
		DecimalFormat fm = new DecimalFormat("#0.00");
		Map<String, String> map = DbUtil.querySingleData(
				"select collect from productinfo where prj_name='"
						+ productCode + "'", DbType.Local);
		String financetype = "项目支付";
		String amount = "-" + map.get("collect");
		String abstracts = "项目 “" + productCode + "” 支付";
		FundRecordDao.insertRecord(productCode, financetype, amount, abstracts);

		List<Map<String, String>> couponlst = new ArrayList<>();
		for (int i = 0; i < userlst.size(); i++) {
			String uid = userlst.get(i).get("uid");
			Map<String, String> acctmap = AccountDao.getUserAcctByUid(uid);
			couponlst.add(acctmap);
		}
		for (int i = couponlst.size() - 1; i >= 0; i--) {
			if (!couponlst.get(i).get("coupons_used").equals("0.00")) {
				financetype = "投资者的奖励入账(满减券)";
				amount = "+" + couponlst.get(i).get("coupons_used");
				abstracts = "投资者的奖励入账(满减券)";
				FundRecordDao.insertRecord(productCode, financetype, amount,
						abstracts);
			} else if (!couponlst.get(i).get("bonus_used").equals("0.00")) {
				financetype = "投资者的奖励入账(红包)";
				amount = "+" + couponlst.get(i).get("bonus_used");
				abstracts = "投资者的奖励入账(红包)";
				FundRecordDao.insertRecord(productCode, financetype, amount,
						abstracts);
			}
		}

		List<Map<String, String>> lst = DbUtil.queryDataList(
				"select * from invest_record where code='" + productCode + "'",
				DbType.Local);
		Collections.reverse(lst);
		financetype = "项目融资";
		abstracts = "项目 “" + productCode + "” 融资";
		for (int i = 0; i < lst.size(); i++) {
			Map<String, String> cmap = DbUtil.querySingleData(
					"select * from user_acct where uid='"
							+ lst.get(i).get("uid") + "'", DbType.Local);
			amount = "+"
					+ fm.format(Double.parseDouble(lst.get(i).get(
							"invest_amount"))
							- Double.parseDouble(cmap.get("coupons_used"))
							- Double.parseDouble(cmap.get("bonus_used")));
			FundRecordDao.insertRecord(productCode, financetype, amount,
					abstracts);
		}

	}

	/**
	 * 支付后修改invset_record的status字段
	 * 
	 * @param proName
	 * @throws SQLException
	 */
	public void updateInvestrecoed(String productCode) throws IOException,
			SQLException {
		String sql = "update invest_record set status='待还款' where code='"
				+ productCode + "'";
		DbUtil.update(sql, DbType.Local);
	}

	/**
	 * 更新user_acct表的total_profit和will_profit
	 * 
	 * @param uid
	 * @param productCode
	 * @throws SQLException
	 */
	public void updateuseracct(String uid, String productCode)
			throws IOException, SQLException {
		String sql = "select profit from user_repay_plan where uid='" + uid
				+ "' and name='" + productCode + "'";
		List<Map<String, String>> lst = DbUtil.queryDataList(sql, DbType.Local);
		double pro = 0;
		String profit = null;
		for (int i = 0; i < lst.size(); i++) {
			String income = lst.get(i).get("profit");
			pro = pro + Double.parseDouble(income);
		}
		String sql2 = "UPDATE user_acct SET will_profit ='0.00' WHERE uid = '"
				+ uid + "'";
		DbUtil.update(sql2, DbType.Local);
		profit = String.valueOf(pro);
		UserAcctDao.updateProfit(uid, profit);
	}

	/**
	 * 更新user_acct表中的账户净资产、我的投资、已赚收益的值
	 * 
	 * @param uid
	 * @throws SQLException
	 */
	public void updateasset(String uid) throws IOException, SQLException {
		String sql = "select coupons_used,bonus_used from user_acct where uid='"
				+ uid + "'";
		Map<String, String> map = DbUtil.querySingleData(sql, DbType.Local);
		String coupons = String.valueOf(Double.parseDouble(map
				.get("coupons_used"))
				+ Double.parseDouble(map.get("bonus_used")));
		UserAcctDao.updateAsset(uid, coupons);
	}

	/**
	 * 放款申请操作
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean enterLoanapply(KeyWords kw) throws Exception {
		boolean result = false;
		// 进入鑫合汇业务管理平台，在项目运营列表里下点击【放款申请】
		kw.click(OrUtil.getBy("index_projectOperation_xpath",
				ObjectLib.BMPObjectLib)); // 点击项目运营
		kw.click(OrUtil.getBy("loanApply_xpath", ObjectLib.BMPObjectLib));// 点击放款申请
		kw.waitPageLoad();
		for (int i = 0; i < 3; i++) {
			if (kw.isElementExist(OrUtil.getBy("iframe_work_frame_id",
					ObjectLib.BMPObjectLib))) {
				break;
			}
			ThreadUtil.sleep();
		}
		kw.switchToFrame("work_frame");
		for (int i = 0; i < 3; i++) {
			List<WebElement> webElements = kw.getWebElements(OrUtil.getBy(
					"applybody_xpath", ObjectLib.BMPObjectLib));
			if (webElements.size() > 0) {
				result = true;
				break;
			}
			ThreadUtil.sleep();
		}
		return result;
	}

	/**
	 * 输入项目名称查询项目
	 * 
	 * @param kw
	 * @param productCode
	 * @return
	 * @throws Exception
	 */
	public boolean searchproj(KeyWords kw, String productCode) throws Exception {
		boolean tflag = true;
		boolean result = false;
		kw.setValue(OrUtil.getBy("productname_xpath", ObjectLib.BMPObjectLib),
				productCode);// 输入项目名称
		kw.click(OrUtil.getBy("searchbutton_xpath", ObjectLib.BMPObjectLib));// 点击查询按钮
		long beginTime = new Date().getTime();
		do {
			tflag = kw.isElementVisible(OrUtil.getBy("wait1_xpath",
					ObjectLib.BMPObjectLib));
			ThreadUtil.sleep();
			long endTime = new Date().getTime();
			if ((endTime - beginTime) / 1000 > 600) {
				break;
			}
		} while (tflag == true);// 判断等待框是否存在
		if (!kw.getText(
				OrUtil.getBy("firstProname_xpath", ObjectLib.BMPObjectLib))
				.equals(productCode)) {
			result = false;
		} else {
			result = true;
		}
		return result;
	}

	/**
	 * 放款操作
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean loanOperation(KeyWords kw) throws Exception {
		boolean result = false;
		kw.click(OrUtil.getBy("checkbox1_id", ObjectLib.BMPObjectLib));// 勾选项目
		kw.click(OrUtil.getBy("subbutton_xpath", ObjectLib.BMPObjectLib));// 点击提交按钮
		if (kw.isElementVisible(OrUtil.getBy("loaninfo_xpath",
				ObjectLib.BMPObjectLib))) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * 确认提交放款申请
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean subApply(KeyWords kw) throws Exception {
		boolean tflag = false;
		boolean result = true;
		kw.click(OrUtil.getBy("dropdown_xpath", ObjectLib.BMPObjectLib));
		kw.clickDivByText("支票");// 在弹出放款信息列表里输入付款方式【支票】
		kw.click(OrUtil.getBy("comfirmbutton_id", ObjectLib.BMPObjectLib));// 点击确定按钮
		long beginTime = new Date().getTime();
		do {
			tflag = kw.isElementVisible(OrUtil.getBy("wait2_xpath",
					ObjectLib.BMPObjectLib));
			ThreadUtil.sleep();
			long endTime = new Date().getTime();
			if ((endTime - beginTime) / 1000 > 600) {
				break;
			}
		} while (tflag == true);// 判断等待框是否存在

		String resText = kw.getText(OrUtil.getBy("nodata2_xpath",
				ObjectLib.BMPObjectLib));
		if (StringUtils.isEquals(resText, "没有数据")) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	/**
	 * 查询任务
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean searchApply(KeyWords kw, int taskName, String nowDate)
			throws Exception {
		boolean result = false;
		boolean tflag = false;
		// 输入客户名称
		kw.setValue(OrUtil.getBy("customname_xpath", ObjectLib.BMPObjectLib),
				"上海鑫合汇网络科技有限公司");
		// 输入查询日期起始日期和截止日期
		kw.setValue(OrUtil.getBy("stratdate_xpath", ObjectLib.BMPObjectLib),
				nowDate);
		kw.setValue(OrUtil.getBy("enddate_xpath", ObjectLib.BMPObjectLib),
				nowDate);
		if (taskName == 1) {// 财务费用确认岗
			kw.setValue(OrUtil.getBy("taskname_xpath", ObjectLib.BMPObjectLib),
					"财务费用确认岗");
		} else if (taskName == 2) {// 资金制单岗
			kw.setValue(OrUtil.getBy("taskname_xpath", ObjectLib.BMPObjectLib),
					"资金制单岗");
		} else if (taskName == 3) {// 资金复核岗
			kw.setValue(OrUtil.getBy("taskname_xpath", ObjectLib.BMPObjectLib),
					"资金复核岗");
		}
		// 点击查询按钮
		kw.click(OrUtil.getBy("searchbutton_xpath", ObjectLib.BMPObjectLib));
		long beginTime = new Date().getTime();
		do {
			tflag = kw.isElementVisible(OrUtil.getBy("wait3_xpath",
					ObjectLib.BMPObjectLib));
			ThreadUtil.sleep();
			long endTime = new Date().getTime();
			if ((endTime - beginTime) / 1000 > 600) {
				break;
			}
		} while (tflag == true);// 判断等待框是否存在
		for (int i = 0; i < 3; i++) {
			List<WebElement> webElements = kw.getWebElements(OrUtil.getBy(
					"task_xpath", ObjectLib.BMPObjectLib));
			if (webElements.size() > 1) {
				result = true;
				break;
			}
			ThreadUtil.sleep();
		}
		return result;
	}

	/**
	 * 进入处理页面校验字段
	 * 
	 * @param kw
	 * @param productCode
	 * @param productid
	 * @return
	 * @throws Exception
	 */
	public boolean checkInfo(KeyWords kw, String productCode, String productid)
			throws Exception {
		// 勾选查询结果第一条记录
		kw.click(OrUtil.getBy("checkbox2_id", ObjectLib.BMPObjectLib));
		// 点击进入处理按钮
		kw.click(OrUtil.getBy("handle_xpath", ObjectLib.BMPObjectLib));
		kw.waitPageLoad();
		ThreadUtil.sleep();
		// 获取页面数据
		Map<String, String> map = new HashMap<String, String>();
		String HH = kw.getText(OrUtil.getBy("firstline_id",
				ObjectLib.BMPObjectLib));
		String[] ss = HH.split("\n");
		map.put("项目名称", ss[1]);
		map.put("融资人", ss[2]);
		map.put("融资规模", ss[3]);
		map.put("实际融资", ss[4]);
		map.put("年化利率", ss[5]);
		map.put("到期利息", ss[6].replace(",", ""));
		map.put("募集利息", ss[7].replace(",", ""));
		map.put("募集时间", ss[8]);
		map.put("项目期限", ss[9]);
		map.put("红包比例", ss[10]);
		map.put("平台费率", ss[11]);
		map.put("平台费", ss[12].replace(",", ""));
		map.put("保证金/融资服务费率", ss[13]);
		map.put("保证金/融资服务费", ss[14].replace(",", ""));
		map.put("确认书上传", ss[15]);
		map.put("放款确认书", ss[16]);
		map.put("项目信息", ss[17]);
		map.put("募集完成通知", ss[18]);
		map.put("UBSP通知放款", ss[19]);
		map.put("放款完成通知", ss[20]);

		DecimalFormat fm = new DecimalFormat("###,###.##");
		Map<String, String> loanapply = ProductDao.loanApply(productid);
		// 募集期的利息
		String reprofit = RepayPlanDao.recruitment(productCode);
		// 除了募集期的利息和
		String sumprofit = RepayPlanDao.sumprofit(productCode);
		// 募集期天数
		String daydiff = String.valueOf(DateUtils.differDays(ProductDao
				.getProByCode(productCode).get("start_bid_time"), ProductDao
				.getProByCode(productCode).get("end_bid_time")) + 1);
		String platformfee = fm.format(Double.parseDouble(loanapply.get(
				"platform_fee").replace("%", "")));
		String tear_rate = fm.format(Double.parseDouble(loanapply.get(
				"year_rate").replace("%", "")));
		String managementfee = fm.format(Double.parseDouble(loanapply
				.get("management_fee")));
		String demand_amount = fm.format(Double.parseDouble(loanapply
				.get("demand_amount")));
		String collect = fm
				.format(Double.parseDouble(loanapply.get("collect")));
		// 数据库获取的数据
		Map<String, String> expmap = new HashMap<String, String>();
		expmap.put("项目名称", loanapply.get("product_name"));
		expmap.put("融资人", loanapply.get("borrower_name"));
		expmap.put("融资规模", demand_amount);
		expmap.put("实际融资", collect);
		expmap.put("年化利率", tear_rate);
		expmap.put("到期利息", sumprofit);
		expmap.put("募集利息", reprofit);
		expmap.put("募集时间", daydiff);
		expmap.put("项目期限", loanapply.get("term"));
		expmap.put("红包比例", loanapply.get("bonus_proportion"));
		expmap.put("平台费率", platformfee);
		expmap.put("平台费", managementfee);
		expmap.put("保证金/融资服务费率", loanapply.get("bond_proportion"));
		expmap.put("保证金/融资服务费", loanapply.get("bond"));
		expmap.put("确认书上传", loanapply.get("confirmation_upload"));
		expmap.put("放款确认书", loanapply.get("confirming_order"));
		expmap.put("项目信息", loanapply.get("pro_info"));
		expmap.put("募集完成通知", loanapply.get("raise_notice"));
		expmap.put("UBSP通知放款", loanapply.get("ubsp_notice"));
		expmap.put("放款完成通知", loanapply.get("completion_notice"));
		// 校验放款确认页面字段
		boolean result = StringUtils.isEquals(expmap, map, log);
		return result;
	}

	/**
	 * 上传附件
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean uploadFile(KeyWords kw, int i, String productid)
			throws Exception {
		boolean result = false;
		boolean tflag = false;
		int rowCount;
		// 获取数据库中存的附件文件路径
		String filpath = DbUtil.querySingleData(
				"select * from loan_apply where pro_id='" + productid + "'",
				DbType.Local).get("file_path");
		// 审批流向勾选同意
		kw.click(OrUtil.getBy("agree_xpath", ObjectLib.BMPObjectLib));

		List<WebElement> webElements = kw.getWebElements(OrUtil.getBy(
				"filelist_xpath", ObjectLib.BMPObjectLib));
		rowCount = webElements.size();
		// 调用脚本上传文件
		File file = new File(filpath);
		String filePath = file.getAbsolutePath();
		String brow = UserAgentUtil.getUserAgent(kw);
		if (brow.equals("Chrome")) {
			Runtime.getRuntime()
					.exec(".\\config\\upload.exe " + " " + "\"" + filePath
							+ "\"" + " " + "\"" + String.valueOf(i) + "\"")
					.waitFor();
		} else if (brow.equals("Firefox")) {
			Runtime.getRuntime()
					.exec(".\\config\\uploadFF.exe " + " " + "\"" + filePath
							+ "\"" + " " + "\"" + String.valueOf(i) + "\"")
					.waitFor();
		}
		ThreadUtil.sleep();
		// 点击开始上传
		kw.click(OrUtil.getBy("startupload_xpath", ObjectLib.BMPObjectLib));
		long beginTime = new Date().getTime();
		do {
			tflag = kw.isElementVisible(OrUtil.getBy("fileinput_id",
					ObjectLib.BMPObjectLib));
			ThreadUtil.sleep();
			long endTime = new Date().getTime();
			if ((endTime - beginTime) / 1000 > 600) {
				break;
			}
		} while (tflag == true);// 判断上传进度框是否存在
		int newrowCount = kw.getWebElements(
				OrUtil.getBy("filelist_xpath", ObjectLib.BMPObjectLib)).size();
		if (newrowCount == rowCount + 1) {
			result = true;
		}
		return result;
	}

	/**
	 * 提交处理
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean submitTask(KeyWords kw) throws Exception {
		boolean result = false;
		// 点击提交处理按钮
		kw.click(OrUtil.getBy("subhandle_xpath", ObjectLib.BMPObjectLib));
		ThreadUtil.sleep(3);
		if (kw.getText(OrUtil.getBy("success_xpath", ObjectLib.BMPObjectLib))
				.equals("任务处理成功")) {
			result = true;
		}
		kw.click(OrUtil.getBy("clickconfirm_xpath", ObjectLib.BMPObjectLib));// 点击确认
		ThreadUtil.sleep(3);
		return result;
	}

	/**
	 * 资金符合岗填流水号并最后提交
	 * 
	 * @param kw
	 * @return
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean finalSubmit(KeyWords kw) throws IOException, Exception {
		boolean result = false;
		String loanTime = DateUtils.getSomeDayAddMinute2(
				SSHUtil.sshCurrentTime(log), 5);
		String liushuihao = String.valueOf(MathUtil.getRandom(8));
		// 填流水号
		kw.setValue(OrUtil.getBy("liushuihao_xpath", ObjectLib.BMPObjectLib),
				liushuihao);
		// 填放款时间
		kw.setValue(OrUtil.getBy("loantime_xpath", ObjectLib.BMPObjectLib),
				loanTime);

		// 点击提交处理
		kw.click(OrUtil.getBy("subhandle_xpath", ObjectLib.BMPObjectLib));
		if (kw.getText(
				OrUtil.getBy("handleresult_xpath", ObjectLib.BMPObjectLib))
				.equals("任务处理成功,是否打印")) {
			kw.click(OrUtil.getBy("cancel_xpath", ObjectLib.BMPObjectLib));
			result = true;
		}
		return result;
	}
}