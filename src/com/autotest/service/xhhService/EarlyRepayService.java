package com.autotest.service.xhhService;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;

import com.autotest.dao.AccountDao;
import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.RepayPlanDao;
import com.autotest.driver.KeyWords;
import com.autotest.enums.DateType;
import com.autotest.enums.DbType;
import com.autotest.or.ObjectLib;
import com.autotest.utility.DateUtils;
import com.autotest.utility.DbUtil;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.PatternUtil;
import com.autotest.utility.ProfitCal;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

public class EarlyRepayService {
	private Logger log = null;

	public EarlyRepayService(Logger log) {
		this.log = log;
	}

	/**
	 * 访问项目管理页面
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void viewProManageTab(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("project_manage_xpath", ObjectLib.XhhObjectLib));
	}

	/**
	 * 获取项目管理的总页数
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public int getRepayPlanTotalPageNum(KeyWords kw) throws Exception {
		kw.waitElementToBeClickable(OrUtil.getBy("proManage_total_page_xpath", ObjectLib.XhhObjectLib));
		return Integer.parseInt(
				kw.getWebElement(OrUtil.getBy("proManage_total_page_xpath", ObjectLib.XhhObjectLib)).getText());
	}

	/**
	 * 获取项目管理的当前页码
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public int getRepayPlanCurrentPageNum(KeyWords kw) throws Exception {
		// 获取当前页页数值
		kw.waitElementToBeClickable(By.xpath("//span[@class='current']"));
		String xpahtCurrentPage = "//span[@class='current']";
		return Integer.parseInt(kw.getWebElement(By.xpath(xpahtCurrentPage)).getText());

	}

	/**
	 * xhh-3736:融资人在【项目管理-全部】页面查看项目[yYS20160718001]
	 * 
	 * @param kw
	 * @param projectName
	 * @return boolean
	 * @throws Exception
	 */
	public boolean isFindProject(KeyWords kw, String projectName) {
		log.info("isFindPoject:要查找的项目名字为---" + projectName);
		String xpathProjectName = "//p[@class='pro_name']/a[contains(text(),'" + projectName
				+ "')]/parent::node()/parent::node()/parent::node()";
		boolean res = kw.isElementVisible(By.xpath(xpathProjectName));
		return res;
	}

	/**
	 * xhh-3737:校验项目管理列表的值
	 * 
	 * @param kw
	 * @param projectName
	 * @return boolean
	 * @throws Exception
	 */
	public boolean checkProManageInfo(KeyWords keywords, String projectName, Map<String, String> expectedMap)
			throws Exception {
		// 要查找的项目xpath路径
		String xpathProjectName = "//p[@class='pro_name']/a[contains(text(),'" + projectName
				+ "')]/parent::node()/parent::node()/parent::node()";
		HashMap<String, String> showHashmap = new HashMap<String, String>();
		showHashmap.put("项目名称", keywords.getWebElement(By.xpath(xpathProjectName + "/td[1]/p/a")).getText());
		showHashmap.put("投放站点", keywords.getWebElement(By.xpath(xpathProjectName + "/td[2]")).getText());

		showHashmap.put("融资规模", MathUtil.format2(
				MathUtil.format(keywords.getWebElement(By.xpath(xpathProjectName + "/td[3]")).getText())) + "");
		showHashmap.put("预期年化利率", keywords.getWebElement(By.xpath(xpathProjectName + "/td[4]/p")).getText());
		showHashmap.put("期限", keywords.getWebElement(By.xpath(xpathProjectName + "/td[5]")).getText());
		showHashmap.put("还款方式", keywords.getWebElement(By.xpath(xpathProjectName + "/td[6]")).getText());
		showHashmap.put("发布时间", keywords.getWebElement(By.xpath(xpathProjectName + "/td[7]/span")).getText());
		showHashmap.put("收益", MathUtil
				.format2(MathUtil.format(keywords.getWebElement(By.xpath(xpathProjectName + "/td[8]/a")).getText())));
		showHashmap.put("状态", keywords.getWebElement(By.xpath(xpathProjectName + "/td[9]")).getText());
		return StringUtils.isEquals(expectedMap, showHashmap, log);
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
	 * 从数据库获取项目管理数据的期望值
	 * 
	 * @param ：
	 * @throws Exception
	 */
	public Map<String, String> getExpectRroInfo(String proName) throws Exception {

		Map<String, String> expectedHashmap = new HashMap<String, String>();
		Map<String, String> dbMap = new HashMap<String, String>();
		dbMap = DbUtil.querySingleData("select name, mi_no,demand_amount,year_rate,term,repay_way,time,status"
				+ " from productinfo,product_repay_plan"
				+ " where productinfo.prj_name=product_repay_plan.name and product_repay_plan.name='" + proName + "'",
				DbType.Local);
		for (String key : dbMap.keySet()) {
			String value = dbMap.get(key);
			System.out.println("getExpectRepayPlanInfo():Key = " + key + ", Value = " + value);
		}
		expectedHashmap.put("项目名称", dbMap.get("name"));
		expectedHashmap.put("投放站点", dbMap.get("mi_no"));
		expectedHashmap.put("融资规模", MathUtil.format2(MathUtil.format(dbMap.get("demand_amount"))));
		expectedHashmap.put("期限", dbMap.get("term"));
		expectedHashmap.put("预期年化利率", dbMap.get("year_rate"));
		expectedHashmap.put("还款方式", dbMap.get("repay_way"));
		// 计算项目总收益
		double profit = Double.parseDouble(RepayPlanDao.sumprofit(proName))
				+ Double.parseDouble(RepayPlanDao.recruitment(proName));
		expectedHashmap.put("收益", MathUtil.round2(profit));
		expectedHashmap.put("发布时间", dbMap.get("time").substring(0, 16));
		switch (dbMap.get("status")) {
		case "0":
			expectedHashmap.put("状态", "待还款");
		case "1":
			expectedHashmap.put("状态", "已还款结束");
		}
		return expectedHashmap;
	}

	/**
	 * xhh-3738:点击提前回购申请按钮
	 * 
	 * @param kw
	 * @param projectName
	 * @return boolean
	 * @throws Exception
	 */
	public void clickEarlyRepayApply(KeyWords kw, String projectName) throws Exception {
		// 要查找的项目xpath路径
		String xpathProjectName = "//p[@class='pro_name']/a[contains(text(),'" + projectName
				+ "')]/parent::node()/parent::node()/parent::node()";
		// 项目对应的操作菜单xptah路径
		String menuXpath = xpathProjectName + "/td[10]";
		// 点击项目操作菜单上的【提前回购申请】链接
		kw.moveToElement(By.xpath(menuXpath));
		kw.Focus(By.xpath(menuXpath));
		if (kw.isVisible(By.xpath(menuXpath))) {
			kw.clickByText("提前回购申请");
		} else {
			log.error("操作菜单不可见");
			throw new Exception("操作菜单不可见");
			// log.info(ScreenShotUtil.takeScreenshot("mytest", kw.driver));
		}
	}

	/**
	 * 获取提前回购时间
	 * 
	 * @param repayDate
	 * @param earlyDay
	 * @return
	 * @throws Exception
	 */
	public String getEarlyRepayDate(String repayDate, int earlyDays) throws Exception {
		Date repay = DateUtils.stringToDate2(repayDate);
		Date earlyRepay = DateUtils.dayAddDays(repay, -earlyDays);
		return DateUtils.simpleDateFormat2(earlyRepay);
	}

	/**
	 * xhh-3834:选择提前回购日期
	 * 
	 * @param kw
	 * @param earlyRepayDate
	 * @throws Exception
	 */
	public void setEarlyRepayDate(KeyWords kw, String earlyRepayDate) throws Exception {
		kw.isElementVisible(OrUtil.getBy("proManage_repayDate_set_xpath", ObjectLib.XhhObjectLib));
		String jsString = "document.getElementById(\"repay_date\").setAttribute('value','" + earlyRepayDate + "')";
		kw.executeJS(jsString);
	}

	/**
	 * xhh-3835:访问【项目运营-提前还款审核】页面
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean enterEarlyRepayExamine(KeyWords kw) throws Exception {
		boolean result = false;
		// 点击【项目运营】
		kw.click(OrUtil.getBy("index_projectOperation_xpath", ObjectLib.BMPObjectLib));
		// 点击【提前还款审核】
		kw.click(OrUtil.getBy("index_earlyRepayExamine_xpath", ObjectLib.BMPObjectLib));
		// 等待页面加载
		kw.waitPageLoad();
		ThreadUtil.sleep();
		kw.switchToFrame("work_frame");
		if (kw.isElementExist(OrUtil.getBy("earlyRepayExamine_pass_xpath", ObjectLib.BMPObjectLib))) {
			result = true;
		}
		return result;
	}

	/**
	 * 根据数据条数计算当前页码
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public int getCurrentPage(KeyWords kw) throws Exception {
		// 每页显示多少条数据
		int dataPerPage = 15;
		// 当前页显示第N到第N+14条数据，从字符串文本中匹配到N，计算当前页码
		String dataNumTxt = kw.getText(OrUtil.getBy("earlyRepayExamine_currentNo_xpath", ObjectLib.BMPObjectLib));
		Pattern pattern = Pattern.compile("\\d+");
		Matcher matcher = pattern.matcher(dataNumTxt);
		while (matcher.find()) {
			break;
		}
		return Integer.parseInt(matcher.group(0)) % dataPerPage + 1;
	}

	/**
	 * 根据项目名称查找待审核的提前还款申请： xhh-3836:在提前还款审核页面查找出保理项目
	 * 
	 * @param kw
	 * @param projectName
	 * @return
	 */
	public boolean isFindExamine(KeyWords kw, String projectName) {
		log.info("isFindExamine:要查找的项目名字为---" + projectName);
		String xpathProjectName = "//div[@class='datagrid-cell' and contains(text(),'" + projectName
				+ "')]/parent::node()";
		boolean res = kw.isElementVisible(By.xpath(xpathProjectName));
		return res;
	}

	/**
	 * 业务平台审核通过： xhh-3866:提前还款审核操作
	 * 
	 * @param kw
	 * @param projectName
	 * @return
	 * @throws Exception
	 */
	public boolean doExamine(KeyWords kw, String projectName) throws Exception {
		boolean result = false;
		// 找到待审核申请的多选框
		String xpathCheckBox = "//div[@class='datagrid-cell' and contains(text(),'" + projectName
				+ "')]/parent::node()/parent::node()/td[1]/div/input";
		// 勾选该申请
		kw.check(By.xpath(xpathCheckBox));
		kw.click(OrUtil.getBy("earlyRepayExamine_pass_xpath", ObjectLib.BMPObjectLib));
		// 弹出是否审核通过的确认框
		kw.isElementExist(OrUtil.getBy("confirm_dialog_xpath", ObjectLib.BMPObjectLib));
		// 点击【确定】按钮
		kw.click(OrUtil.getBy("confirm_btn_xpath", ObjectLib.BMPObjectLib));
		// 审核成功弹框
		if (kw.isElementExist(OrUtil.getBy("earlyRepayExamine_successDialog_xpath", ObjectLib.BMPObjectLib))) {
			String txt = kw.getText2(OrUtil.getBy("earlyRepayExamine_successTxt_xpath", ObjectLib.BMPObjectLib));
			if (txt.equals("处理成功！")) {
				result = true;
				kw.click(OrUtil.getBy("earlyRepayExamine_successBtn_xpath", ObjectLib.BMPObjectLib));
				kw.waitPageLoad();
			}
		}
		return result;
	}

	/**
	 * 获取待审核数据的预期值
	 * 
	 * @param projectName
	 * @return
	 * @throws SQLException
	 */
	public Map<String, String> getExpactedExamineData(String projectName, String earlyRepayDate) throws SQLException {
		Map<String, String> expactedExamineMap = new HashMap<String, String>();
		// TODO 修改募集规模和实际融资金额的格式为50,000，募集期收益期望值:0.0;与实际值:0不等！
		Map<String, String> proInfo = ProductDao.getProByCode(projectName);
		expactedExamineMap.put("项目名称", proInfo.get("prj_name"));
		String fdemand_amount = MathUtil.formatToThousands(Double.parseDouble(proInfo.get("demand_amount")), 0,
				java.math.RoundingMode.HALF_UP);
		expactedExamineMap.put("融资规模", fdemand_amount);
		String fcollect = MathUtil.formatToThousands(Double.parseDouble(proInfo.get("collect")), 0,
				java.math.RoundingMode.HALF_UP);
		expactedExamineMap.put("实际融资金额", fcollect);
		expactedExamineMap.put("预期年化收益率", proInfo.get("year_rate"));
		expactedExamineMap.put("期限", proInfo.get("term"));
		expactedExamineMap.put("发布站点", proInfo.get("mi_no"));
		double mProfit = RepayPlanDao.getProfitByCode(projectName, "募集期");
		double bProfit = RepayPlanDao.getProfitByCode(projectName, "第1期");
		double totalProfit = mProfit + bProfit;
		expactedExamineMap.put("用款期收益", MathUtil.format2(bProfit));
		if (mProfit == 0) {
			expactedExamineMap.put("募集期收益", "0");
		} else {
			expactedExamineMap.put("募集期收益", MathUtil.format2(mProfit));
		}
		expactedExamineMap.put("总收益", MathUtil.format2(totalProfit));
		expactedExamineMap.put("还款日期", earlyRepayDate);
		return expactedExamineMap;
	}

	/**
	 * 获取待审核数据的页面值
	 * 
	 * @param kw
	 * @param projectName
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getPageExamineData(KeyWords kw, String projectName) throws Exception {
		Map<String, String> pageExamineMap = new HashMap<String, String>();
		String xpathRow = "//div[@class='datagrid-cell' and contains(text(),'" + projectName
				+ "')]/parent::node()/parent::node()";
		pageExamineMap.put("项目名称", kw.getText(By.xpath(xpathRow + "/td[2]/div")));
		pageExamineMap.put("还款日期", kw.getText(By.xpath(xpathRow + "/td[3]/div")));
		pageExamineMap.put("融资规模", kw.getText(By.xpath(xpathRow + "/td[4]/div")));
		pageExamineMap.put("实际融资金额", kw.getText(By.xpath(xpathRow + "/td[5]/div")));
		pageExamineMap.put("预期年化收益率", kw.getText(By.xpath(xpathRow + "/td[6]/div")));
		pageExamineMap.put("期限", kw.getText(By.xpath(xpathRow + "/td[7]/div")));
		pageExamineMap.put("用款期收益", kw.getText(By.xpath(xpathRow + "/td[8]/div")));
		pageExamineMap.put("募集期收益", kw.getText(By.xpath(xpathRow + "/td[9]/div")));
		pageExamineMap.put("总收益", kw.getText(By.xpath(xpathRow + "/td[10]/div")));
		pageExamineMap.put("发布站点", kw.getText(By.xpath(xpathRow + "/td[11]/div")));
		return pageExamineMap;
	}

	/**
	 * 校验待审核信息：xhh-3840:校验提前还款申请页面结果列表展示的记录
	 * 
	 * @param pageMap
	 * @param expectMap
	 * @return
	 */
	public boolean checkExamineData(Map<String, String> pageMap, Map<String, String> expectMap) {
		return StringUtils.isEquals(expectMap, pageMap, log);
	}

	/**
	 * 提前还款前校验当期还款计划
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public boolean checkRepayDetail(KeyWords kw) throws Exception {
		boolean result = false;
		String tbodyXpath = "//table[@class='ui-record-table']/tbody/tr";
		String txt = kw.getText(By.xpath(tbodyXpath + "/td"));
		if (txt.equals("没有相关数据"))
			result = true;
		return result;
	}

	/**
	 * 提前还款操作:xhh-3873:点击提前还款操作
	 * 
	 * @param kw
	 * @param proName
	 * @throws Exception
	 */
	public void doEarlyRepay(KeyWords kw, String proName) throws Exception {
		// 点击还款明细页面下的【提前还款】按钮
		boolean isVis = kw.isVisible(OrUtil.getBy("repay_early_repayBtn_xpath", ObjectLib.XhhObjectLib));
		System.out.println(isVis);
		if (isVis) {
			kw.click(OrUtil.getBy("repay_early_repayBtn_xpath", ObjectLib.XhhObjectLib));
			// 点击确认按钮
			kw.click(OrUtil.getBy("repay_detail_pop_verify_xpath", ObjectLib.XhhObjectLib));
			if (kw.isElementVisible(OrUtil.getBy("repay_progressLoad_xpath", ObjectLib.XhhObjectLib))) {
				long beginTime = DateUtils.getTime();
				while (true) {
					if (!kw.isElementVisible(OrUtil.getBy("repay_progressLoad_xpath", ObjectLib.XhhObjectLib)))
						break;
					ThreadUtil.sleep();
					long endTime = DateUtils.getTime();
					if ((endTime - beginTime) > 30000) {
						throw new Exception("找不到repay_progressLoad_xpath元素!超时!");
					}
				}
			}
		}
	}

	/**
	 * 计算提前还款利息：金额*(原期限-提前天数)*年化利率/365
	 * 
	 * @param proName
	 * @param earlyDays
	 * @param amount
	 * @return
	 * @throws Exception
	 */
	public String getEarlyRepayProfit(String proName, int earlyDays, double amount) throws Exception {
		Map<String, String> pro = ProductDao.getProByCode(proName);
		String term = pro.get("term");
		// 用款期限类型
		DateType dateType = StringUtils.getStrDateType(term);
		// 只含数字的用款期限
		int dateUsed = Integer.parseInt(PatternUtil.getDigestMatch(term));
		int earlyDateUsed = dateUsed - earlyDays;
		// 年利率
		double annualRate = Double.parseDouble(pro.get("year_rate").split("%")[0]) / 100;
		return Double.toString(ProfitCal.calBaseProfit2(amount, earlyDateUsed, dateType, annualRate));
	}

	/**
	 * 计算提前还款后项目用款期利息：投资用户用款期利息之和
	 * 
	 * @param proName
	 * @return
	 * @throws SQLException
	 */
	public BigDecimal calcProEarlyFrofit(String proName) throws SQLException {
		double earlyProfit;
		String sql = "select sum(profit) as earlyprofit from user_repay_plan where name='" + proName
				+ "' and stage='第1期'";
		Map<String, String> sumMap = DbUtil.querySingleData(sql, DbType.Local);
		System.out.println("earlyProfit=[" + sumMap.get("earlyprofit") + "]");
		earlyProfit = Double.parseDouble(sumMap.get("earlyprofit"));
		return MathUtil.roundD(earlyProfit);
	}

	/**
	 * 提前还款后更新项目信息中的还款日期
	 * 
	 * @param proName
	 * @param earlyDate
	 * @throws SQLException
	 */
	public void updateProInfo(String proName, String earlyRepayDate) throws SQLException {
		String sql = "update productinfo set repay_date='" + earlyRepayDate + "' where prj_name='" + proName + "'";
		DbUtil.update(sql, DbType.Local);
	}

	/**
	 * 提前还款后更新项目还款计划的用款期利息、还款本息、还款日期
	 * 
	 * @param proName
	 * @param earlyRepayDate
	 * @throws SQLException
	 */
	public void updateProductRepayPlan(String proName, String earlyRepayDate) throws SQLException {

		String stage = "第1期";
		BigDecimal newProfit = calcProEarlyFrofit(proName);
		double captial = RepayPlanDao.getCapitalByCode(proName, stage);
		double total_money = captial + newProfit.doubleValue();
		String sql = "update product_repay_plan set profit='" + newProfit + "' , total_money='" + total_money
				+ "' where name='" + proName + "' and stage='" + stage + "'";
		DbUtil.update(sql, DbType.Local);
		RepayPlanDao.updateProRepayDate(proName, earlyRepayDate);
	}

	/**
	 * 提前还款后更新用户还款计划的用款期收益、还款本息、还款日期
	 * 
	 * @param proName
	 * @param uid
	 * @param earlyDays
	 * @param earlyRepayDate
	 * @throws Exception
	 */
	public void updateUserRepayPlan(String proName, String uid, int earlyDays, String earlyRepayDate) throws Exception {
		String stage = "第1期";
		String capital = InvestRecordDao.getRecordByUserCode(uid, proName).get("invest_amount");
		String earlyProfit = getEarlyRepayProfit(proName, earlyDays, Double.parseDouble(capital));
		double total_money = Double.parseDouble(capital) + Double.parseDouble(earlyProfit);
		String sql = "update user_repay_plan set profit='" + earlyProfit + "' , total_money='" + total_money
				+ "' where name='" + proName + "' and stage='" + stage + "' and uid='" + uid + "'";
		DbUtil.update(sql, DbType.Local);
		RepayPlanDao.updateUserRepayDate(proName, earlyRepayDate);
	}

	/**
	 * 重新生成还款计划，用新的用户还款计划计算待收收益及预期总收益。因此要先更新用户还款计划 预期总收益=待收收益+已赚收益
	 * 
	 * @param uid
	 * @param proName
	 * @param earlyDays
	 * @throws Exception
	 */
	public void updateUserAcct(String uid, String proName, int earlyDays) throws Exception {
		double newWill = RepayPlanDao.getProfitByCodeandUid(proName, uid);
		String existProfit = AccountDao.getUserAcctByUid(uid).get("exist_profit");
		double newTotal = Double.parseDouble(existProfit) + newWill;
		String sql = "update user_acct set will_profit = '" + newWill + "' , total_profit='" + newTotal
				+ "' where uid='" + uid + "'";
		DbUtil.update(sql, DbType.Local);
	}

	/**
	 * 提前还款更新用户投资记录的利息和还款日期
	 * 
	 * @param proName
	 * @param uid
	 * @throws Exception
	 */
	public void updateInvestRecord(String proName, String uid, int earlyDays, String earlyRepayDate) throws Exception {
		Map<String, String> userInvest = InvestRecordDao.getRecordByUserCode(uid, proName);
		String amount = userInvest.get("invest_amount");
		double investAmount = Double.parseDouble(amount);
		String newProfit = getEarlyRepayProfit(proName, earlyDays, investAmount);
		InvestRecordDao.updateProfit(uid, proName, newProfit);
		InvestRecordDao.updateRepayDate(proName, earlyRepayDate);
	}

}
