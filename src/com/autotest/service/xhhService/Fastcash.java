package com.autotest.service.xhhService;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.autotest.dao.ProductDao;
import com.autotest.driver.KeyWords;
import com.autotest.enums.Key;
import com.autotest.or.ObjectLib;
import com.autotest.utility.DateUtils;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.PatternUtil;
import com.autotest.utility.ProfitCal;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * 变现借款页面
 * 
 * @author 000738
 * 
 */
public class Fastcash {
	private Logger log = null;

	public Fastcash(Logger log) {
		this.log = log;
	}

	/**
	 * 修改服务器时间 项目的截标时间+60天
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */

	public void changTime(String investEndTime) throws ParseException,
			IOException {

		// 截标日+60天，输出日期格式
		Date cashdate = DateUtils.dayAddDays(
				DateUtils.stringToDate2(investEndTime), 68);
		// 日期后加上10分钟；转换成格式为“2016-08-05 19:58:13”;日期格式转换成字符串
		String changtimes = DateUtils.simpleDateFormat(DateUtils
				.getSomeDayAddMinute(cashdate, 10));
		// 修改服务器时间至changtimes
		SSHUtil.sshChangeTime(log, changtimes);

	}

	/**
	 * 修改服务器时间 项目的截标时间+n天
	 * 
	 * @throws ParseException
	 * @throws IOException
	 */

	public void changTime2(String investEndTime, int days)
			throws ParseException, IOException {

		// 截标日+60天，输出日期格式
		Date cashdate = DateUtils.dayAddDays(
				DateUtils.stringToDate2(investEndTime), days);
		// 日期后加上10分钟；转换成格式为“2016-08-05 19:58:13”;日期格式转换成字符串
		String changtimes = DateUtils.simpleDateFormat(DateUtils
				.getSomeDayAddMinute(cashdate, 10));
		// 修改服务器时间至changtimes
		SSHUtil.sshChangeTime(log, changtimes);
		ThreadUtil.sleep(10);
	}

	/**
	 * 点击我的账户
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void clickUserAccount(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("userAccountManage_link_xpath",
				ObjectLib.XhhObjectLib));
	}

	/**
	 * 点击投资管理
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void clickInvestManage(KeyWords kw) throws Exception {
		kw.click(OrUtil
				.getBy("investManage_link_xpath", ObjectLib.XhhObjectLib));
	}

	/**
	 * 点击变现借款
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void clickCashManage(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("cashManage_link_xpath", ObjectLib.XhhObjectLib));
	}

	/**
	 * 点击可变现项目列表里的立即变现按钮
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void clickFastCash(KeyWords kw, String prjName) throws Exception {

		// 项目剩余期限
		// String dateResidue = kw.getText(By.xpath("//td[text()='" + prjName +
		// "']/following-sibling::td[2]"));
		// log.info("项目剩余期限" + dateResidue );
		// 点击变现列表里面的数据
		kw.click(By.xpath("//td[text()='" + prjName
				+ "']/following-sibling::td[4]/label/a"));

	}

	/**
	 * 获取变现列表值
	 * 
	 * @param kw
	 * @param prjName
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getCashList(KeyWords kw, String prjName)
			throws Exception {
		log.info("项目名称--" + prjName);
		String xpathList = "//td[contains(text(),'" + prjName + "')]";
		List<WebElement> list = kw.getWebElements(By.xpath(xpathList));
		log.info("list.size()的值为" + list.size());
		if (list.size() == 1) {
			// 项目的还款日期
			String repayDate = kw.getText(By.xpath("//td[contains(text(),'"
					+ prjName + "')]/preceding-sibling::td[1]"));
			log.info("项目的还款日期" + repayDate);
			// 项目剩余期限
			String dateResidue = kw.getText(By.xpath("//td[contains(text(),'"
					+ prjName + "')]/following-sibling::td[2]"));
			log.info("项目剩余期限" + dateResidue);
			String term = StringUtils.getNum(dateResidue);
			// 项目的可变现额度
			String cashAssert = kw.getText(By.xpath("//td[contains(text(),'"
					+ prjName + "')]/following-sibling::td[3]"));
			log.info("项目的可变现额度" + cashAssert);
			Map<String, String> getlist = new HashMap<String, String>();
			getlist.put("repay_date", repayDate);
			getlist.put("term", dateResidue);
			kw.click(By.xpath("//td[contains(text(),'" + prjName
					+ "')]/following-sibling::td[4]/label/a"));
			return getlist;
		} else {
			// 项目的还款日期
			String repayDate = kw.getText(By
					.xpath("//tr[1]/td[contains(text(),'" + prjName
							+ "')]/preceding-sibling::td[1]"));
			log.info("项目的还款日期" + repayDate);
			// 项目剩余期限
			String dateResidue = PatternUtil.getMoneyMatch(kw.getText(By
					.xpath("//tr[1]/td[contains(text(),'" + prjName
							+ "')]/following-sibling::td[2]")));
			log.info("项目剩余期限" + dateResidue);
			// String repayDate =
			// kw.getText(By.xpath("//td[text()='月益升-经营贷AppP012016042602']/preceding-sibling::td[1]"));
			// 项目的可变现额度
			String cashAssert = PatternUtil.getMoneyMatch(kw.getText(By
					.xpath("//tr[1]/td[contains(text(),'" + prjName
							+ "')]/following-sibling::td[3]")));
			log.info("项目的可变现额度" + cashAssert);
			Map<String, String> getlist = new HashMap<String, String>();
			getlist.put("repay_date", repayDate);
			getlist.put("term", dateResidue);
			kw.click(By.xpath("//tr[1]/td[contains(text(),'" + prjName
					+ "')]/following-sibling::td[4]/label/a"));
			return getlist;
		}

	}

	/**
	 * 计算变现公式
	 * 
	 * @param kw
	 * @param 到期应付本金
	 *            ：principal
	 * @param cashRate
	 * @param dateResidue
	 * @param platformFee
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getValueFormula(KeyWords kw, String principal,
			String cashRate, String term) throws Exception {
		Map<String, String> cashValue = new HashMap<String, String>();
		ProfitCal pro = new ProfitCal(log);
		double money = Double.parseDouble(principal);
		double rate = Double.parseDouble(cashRate);
		// 项目剩余期限
		int residueTime = Integer.parseInt(StringUtils.getNum(term));
		log.info("项目剩余期限-公式：" + residueTime);
		// 计算到期应付利息
		double interests = pro.repayInterests(money, rate, residueTime);
		log.info("到期应付利息：" + interests);
		// 计算平台管理费
		double fee = pro.PlantFee(money);
		String feeDb = MathUtil.round2(fee);
		log.info("平台管理费" + fee);
		// 存入map
		// cashValue.put("residueTime", residueTime+"");
		cashValue.put("interests", interests + "");
		cashValue.put("platformFee", feeDb);

		return cashValue;
	}

	/**
	 * 计算变现公式2
	 * 
	 * @param kw
	 * @param 到期应付本金
	 *            ：principal
	 * @param cashRate
	 * @param dateResidue
	 * @param platformFee
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getValueFormula2(KeyWords kw, String principal,
			String cashRate, String term) throws Exception {
		Map<String, String> cashValue = new HashMap<String, String>();
		ProfitCal pro = new ProfitCal(log);
		double money = Double.parseDouble(principal);
		double rate = Double.parseDouble(cashRate);
		// 项目剩余期限
		int residueTime = Integer.parseInt(StringUtils.getNum(term));
		log.info("项目剩余期限-公式：" + residueTime);
		// 计算到期应付利息
		double interests = pro.repayInterests(money, rate, residueTime);
		log.info("到期应付利息：" + interests);
		// 计算平台管理费
		double fee = pro.PlantFee(money);
		log.info("平台管理费" + fee);
		// 存入map
		cashValue.put("interests", interests + "");
		cashValue.put("platformFee", MathUtil.format2(fee));

		return cashValue;
	}

	/**
	 * 变现页面上的值输入 .
	 * 
	 * @param kw
	 * @param cashRate
	 * @param cashMoney
	 * @param payPasswd
	 * @return
	 * @throws Exception
	 */
	public void setValueCashPage(KeyWords kw, int i, String cashRate,
			String cashMoney, String payPasswd) throws Exception {
		ThreadUtil.sleep();
		// 变现利率
		kw.setValue(
				OrUtil.getBy("cashRate_table_xpath", ObjectLib.XhhObjectLib),
				cashRate);
		// 变现金额=到期应付本金
		for (int j = 0; j < 2; j++) {
			if (i == 0) {
				// 触发js
				String jquery = "$('input[name=\"cashRate\"]').blur()";
				kw.executeJS(jquery);
				ThreadUtil.sleep();
				kw.setValue(OrUtil.getBy("cashMoney_table_xpath",
						ObjectLib.XhhObjectLib), cashMoney);
				// 触发js
				jquery = "$('input[name=\"cashAmount\"]').blur()";
				kw.executeJS(jquery);
				ThreadUtil.sleep();
			} else {
				kw.click(OrUtil.getBy("totalCash_table_xpath",
						ObjectLib.XhhObjectLib));
			}
			// kw.click(OrUtil.getBy("payPasswd_table_xpath",ObjectLib.XhhObjectLib));
			// ThreadUtil.sleep(1);
			System.out.println(kw.getAttribute(OrUtil.getBy(
					"cashMoney_table_xpath", ObjectLib.XhhObjectLib), "value"));
			if (kw.getAttribute(
					OrUtil.getBy("cashMoney_table_xpath",
							ObjectLib.XhhObjectLib), "value").equals(cashMoney)) {
				break;
			}
		}
		kw.sendKeys(Key.PAGE_DOWN);
		ThreadUtil.sleep();
		// 支付密码
		// kw.click(OrUtil.getBy("payPasswd_table_xpath",
		// ObjectLib.XhhObjectLib));
		kw.setValue(
				OrUtil.getBy("payPasswd_table_xpath", ObjectLib.XhhObjectLib),
				payPasswd);
		ThreadUtil.sleep(2);
		// 点击变现协议
		kw.check(OrUtil.getBy("protocol_click_xpath", ObjectLib.XhhObjectLib));
		ThreadUtil.sleep(2);

	}

	/**
	 * xhh-469至xhh-690 投资变现页面
	 * 
	 * @param kw
	 * @param cashRate
	 *            变现利率
	 * @param cashMoney
	 *            变现金额
	 * @param payPasswd
	 *            支付密码
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> checkFastCashPage(KeyWords kw) throws Exception {

		// 到期应付利息
		String interests = PatternUtil.getMoneyMatch(kw.getText(OrUtil.getBy(
				"interests_table_xpath", ObjectLib.XhhObjectLib)));
		log.info("到期应付利息页面上的值" + interests);
		// 平台服务费
		String platformFee = PatternUtil.getMoneyMatch(kw.getText(OrUtil.getBy(
				"platformFee_table_xpath", ObjectLib.XhhObjectLib)));
		String fee = StringUtils.double2Str(Double.parseDouble(platformFee));
		log.info("平台管理费页面上的值" + platformFee);
		// //实际到账金额
		// String realMoney=kw.getText(OrUtil.getBy("realMoney_table_xpath",
		// ObjectLib.XhhObjectLib));
		// log.info("实际到账金额" + realMoney);
		// //实际获得收益
		// String
		// realInterests=kw.getText(OrUtil.getBy("realInterests_table_xpath",
		// ObjectLib.XhhObjectLib));
		// log.info("实际获得收益" + realInterests);
		// //剩余资产到期价值
		// String
		// surplusAsset=kw.getText(OrUtil.getBy("surplusAsset_table_xpath",
		// ObjectLib.XhhObjectLib));
		// log.info("剩余资产到期价值" + surplusAsset);
		HashMap<String, String> destMap = new HashMap<String, String>();
		destMap.put("interests", interests);
		destMap.put("platformFee", fee);
		return destMap;
	}

	/**
	 * xhh-469至xhh-690 投资变现页面
	 * 
	 * @param kw
	 * @param cashRate
	 *            变现利率
	 * @param cashMoney
	 *            变现金额
	 * @param payPasswd
	 *            支付密码
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> CreatFastCashPage(KeyWords kw) throws Exception {

		// 借款產品名稱
		// String cashProName = kw.getText(OrUtil.getBy("cashProName_text",
		// ObjectLib.XhhObjectLib));
		int startIndex = kw.getText(
				By.xpath("//ul[@class='formList']/li[1]/span")).indexOf("S");
		String cashProName = kw.getText(
				By.xpath("//ul[@class='formList']/li[1]/span")).substring(
				startIndex);
		log.info("借款產品名稱" + cashProName);
		// 还款方式
		String repayType = kw.getText(By
				.xpath("//ul[@class='formList']/li[2]/span"));
		log.info("还款方式" + repayType);
		// 可变现资产价值
		String cashAsset = kw.getText(OrUtil.getBy("cashAsset_table_xpath",
				ObjectLib.XhhObjectLib));
		log.info("可变现资产价值" + cashAsset);
		// 剩余资产到期价值
		String surplusAsset = kw.getText(OrUtil.getBy(
				"surplusAsset_table_xpath", ObjectLib.XhhObjectLib));
		log.info("剩余资产到期价值" + surplusAsset);

		Map<String, String> getValue = new HashMap<String, String>();

		getValue.put("prj_name", cashProName);
		getValue.put("repay_way", repayType);
		// getValue.put("surplusAsset", surplusAsset);
		return getValue;
	}

	/**
	 * 提交变现申请按钮
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void cashSubmit(KeyWords kw) throws Exception {
		// 点击提交变现申请
		kw.click(OrUtil.getBy("loanApplication_form_xpath",
				ObjectLib.XhhObjectLib));
		ThreadUtil.sleep(3);
	}

	/**
	 * 点击查看更多可变现项目
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String clickManyCashPro(KeyWords kw) throws Exception {
		kw.clickByText("查看更多可变现项目");
		// kw.click(OrUtil.getBy("manyCashPro_click_xpath",
		// ObjectLib.XhhObjectLib));
		String title = kw.getTitle();
		log.info("title" + title);
		return title;
	}

	/**
	 * 点击已变现页签
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void clickCashFasted(KeyWords kw) throws Exception {
		kw.click(OrUtil.getBy("cashFasted_click_xpath", ObjectLib.XhhObjectLib));
		ThreadUtil.sleep(3);
	}

	/**
	 * 已变现列表信息验证
	 * 
	 * @param kw
	 * @param cashMoney
	 * @param sdt_name
	 * @return
	 * @throws Exception
	 */
	public boolean checkCashFasted(KeyWords kw, String cashMoney,
			String sdt_name) throws Exception {

		boolean flag = false;
		String money = StringUtils.strWithComma(cashMoney);
		log.info("变现金额应该为" + money);
		// 到期本息
		// double interest
		// =MathUtil.format(kw.getText(By.xpath("//td[text() = '速兑通-SDT2016032010069']/following-sibling::td[1]")));

		// 剩余期限
		// kw.getText(By.xpath("//td[text() = '速兑通-SDT2016032010069']/following-sibling::td[2]"));

		// 可变现额度:3450.00
		// double principal =
		// MathUtil.format(kw.getText(By.xpath("//td[text() = '速兑通-SDT2016032010069']/following-sibling::td[3]")));

		// 总募集金额
		// double totalPrincipal =
		// MathUtil.format(kw.getText(By.xpath("//em[text()='"+cashMoney+"']")));

		// 已募集金额

		String havePrincipal = StringUtils.double2Str(MathUtil.format(kw
				.getText(By.xpath("//em[text()='" + money
						+ "']/parent::node()/following-sibling::td[1]/em"))));

		// 平台费
		String plantfee = StringUtils.double2Str(MathUtil.format(kw.getText(By
				.xpath("//em[text()='" + money
						+ "']/parent::node()/following-sibling::td[2]"))));

		// 累计投资笔数

		// String investNum = String.valueOf(MathUtil.format(kw.getText(By
		// .xpath("//em[text()='" + money
		// + "']/parent::node()/following-sibling::td[3]"))));

		String investNum = kw.getText(
				By.xpath("//em[text()='" + money
						+ "']/parent::node()/following-sibling::td[3]"))
				.substring(0, 1);
		investNum = String.valueOf(MathUtil.format(investNum));

		// String investNum = String.valueOf(kw.getText(By
		// .xpath("//em[text()='" + money
		// + "']/parent::node()/following-sibling::td[3]")));

		String count_invest_money = ProductDao.selectSdtRecord(sdt_name).get(
				"count_invest_money");
		String count_plant_fee = ProductDao.selectSdtRecord(sdt_name).get(
				"count_plant_fee");
		String count = ProductDao.selectSdtRecord(sdt_name).get("count");

		// 页面上的值
		HashMap<String, String> destMap = new HashMap<String, String>();
		destMap.put("havePrincipal", havePrincipal);
		destMap.put("plantfee", plantfee);
		destMap.put("investNum", investNum);

		// 数据库里面的值
		HashMap<String, String> expectedMap = new HashMap<String, String>();
		expectedMap.put("havePrincipal", count_invest_money);
		expectedMap.put("plantfee", count_plant_fee);
		expectedMap.put("investNum", count);

		flag = StringUtils.isEquals(expectedMap, destMap, log);
		return flag;

	}

	/**
	 * 点击资金记录-变现交易页签
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void clickCashButton(KeyWords kw) throws Exception {
		kw.click(By.xpath("//a[text()  ='变现交易']"));
		// kw.clickByText("变现交易");

	}

	/**
	 * 速兑通已变现列表上的数据
	 * 
	 * @param kw
	 * @param sdt_name
	 * @return
	 * @throws Exception
	 */
	public boolean sdtFinanceRecord(KeyWords kw, String sdt_name)
			throws Exception {

		UserAccountService userAcc = new UserAccountService(log);
		boolean flag = true;
		// Map<String, String> destMap = new HashMap<String, String>();
		// HashMap<String, String> expectMap = new HashMap<String, String>();

		// 通过项目名称查找资金记录，可能存在分页
		String summaryXpath = "//td[contains(text(),'" + sdt_name + "')]";
		if (userAcc.findKeyPaging(kw, summaryXpath)) {

			// 获取页面上变现条数
			List<WebElement> cashlists = kw
					.getWebElements(By
							.xpath("//td[contains(text(),'"
									+ sdt_name
									+ "')]/preceding-sibling::td[text()='变现']/parent::tr"));
			log.info("变现记录条数=[" + cashlists.size() + "]");

			// 获取页面上的Map值
			// List<Map<String, String>> listsPage = new ArrayList<Map<String,
			// String>>();
			// 获取数据库里面的变现条数
			List<Map<String, String>> listsdb = ProductDao.selectFinanceRecord(
					sdt_name, "变现");
			log.info("变现条数" + listsdb);

			// 将页面上的变现的资金记录写入map

			for (int i = 0; i < cashlists.size(); i++) {

				WebElement tr = cashlists.get(i);
				List<WebElement> tds = tr.findElements(By.tagName("td"));
				// 类型
				String typeValPage = tds.get(1).getText();
				int size = listsdb.size();
				String typeValDb = listsdb.get(size - i - 1)
						.get("finance_type");
				if (!StringUtils.isEquals(typeValPage, typeValDb, log)) {
					flag = false;
				}
				// 交易金额
				String payAmountXapth = tds.get(2).getText();
				String payAmountValPage = PatternUtil
						.getMoneyMatch(payAmountXapth);
				String payAmountValDb = listsdb.get(size - i - 1).get("amount");
				if (!StringUtils
						.isEquals(payAmountValDb, payAmountValPage, log)) {
					flag = false;
				}
				// 账户余额
				String acctRemainXpath = tds.get(3).getText();
				String acctRemainPage = PatternUtil
						.getMoneyMatch(acctRemainXpath);
				String acctRemainDb = listsdb.get(size - i - 1).get(
						"acct_balance");
				if (!StringUtils.isEquals(acctRemainDb, acctRemainPage, log)) {
					flag = false;
				}
				// 摘要
				String summaryValXpath = tds.get(4).getText();
				String summaryValPage = StringUtils.format(summaryValXpath);
				String summaryValDb = listsdb.get(size - i - 1).get("abstract");
				if (!StringUtils.isEquals(summaryValDb, summaryValPage, log)) {
					flag = false;
				}
			}
		}
		return flag;

	}

	/**
	 * 验证速兑通项目的手续费
	 * 
	 * @param kw
	 * @param sdt_name
	 * @return
	 * @throws Exception
	 */
	public boolean checkCashFee(KeyWords kw, String sdt_name) throws Exception {

		UserAccountService userAcc = new UserAccountService(log);
		boolean flag = false;
		Map<String, String> destMap = new HashMap<String, String>();
		Map<String, String> expectMap = new HashMap<String, String>();

		// 通过项目名称查找资金记录，可能存在分页
		// String xpath = "//td[contains(text()," + sdt_name
		// + ")]/preceding-sibling::td[text()='手续费']/parent::tr";
		//
		// if (userAcc.findKeyPaging(kw, xpath)) {
		// // 摘要
		// String summaryXpath = "//td[contains(text()," + sdt_name
		// + ")]/preceding-sibling::td[text()='手续费']/parent::tr/td[5]";
		// String summaryValPage = kw.getText(By.xpath(summaryXpath));
		// // 类型
		// String typeXpath = "//td[contains(text()," + sdt_name
		// + ")]/preceding-sibling::td[text()='手续费']/parent::tr/td[2]";
		// String typeValPage = kw.getText(By.xpath(typeXpath));
		// // 账户余额
		// String acctRemainXpath = "//td[contains(text()," + sdt_name
		// + ")]/preceding-sibling::td[text()='手续费']/parent::tr/td[4]";
		// String acctRemainValPage = PatternUtil.getMoneyMatch(kw.getText(By
		// .xpath(acctRemainXpath)));
		// // 交易金额
		// String payAmountXpath = "//td[contains(text()," + sdt_name
		// + ")]/preceding-sibling::td[text()='手续费']/parent::tr/td[3]";
		// String payAmountValPage = PatternUtil.getMoneyMatch(kw.getText(By
		// .xpath(payAmountXpath)));
		//
		// destMap.put("abstract", summaryValPage);
		// destMap.put("finance_type", typeValPage);
		// destMap.put("amount", payAmountValPage);
		// destMap.put("acct_balance", acctRemainValPage);
		// // 获取数据库的值
		// expectMap = ProductDao.selectFeeRecord(sdt_name, "手续费");
		// if (StringUtils.isEquals(expectMap, destMap, log)) {
		// log.info("数据验证成功");
		// flag = true;
		// } else {
		// log.info("数据验证失败");
		// flag = false;
		// }
		//
		// } else {
		// log.info("该项目无手续费记录");
		// }
		//
		//
		// 摘要
		String summaryXpath = "//td[contains(text(),'" + sdt_name
				+ "')]/preceding-sibling::td[text()='手续费']/parent::tr/td[5]";
		String summaryValPage = StringUtils.format(kw.getText(By
				.xpath(summaryXpath)));
		// 类型
		String typeXpath = "//td[contains(text(),'" + sdt_name
				+ "')]/preceding-sibling::td[text()='手续费']/parent::tr/td[2]";
		String typeValPage = kw.getText(By.xpath(typeXpath));
		// 账户余额
		String acctRemainXpath = "//td[contains(text(),'" + sdt_name
				+ "')]/preceding-sibling::td[text()='手续费']/parent::tr/td[4]";
		String acctRemainValPage = PatternUtil.getMoneyMatch(kw.getText(By
				.xpath(acctRemainXpath)));
		// 交易金额
		String payAmountXpath = "//td[contains(text(),'" + sdt_name
				+ "')]/preceding-sibling::td[text()='手续费']/parent::tr/td[3]";
		String payAmountValPage = PatternUtil.getMoneyMatch(kw.getText(By
				.xpath(payAmountXpath)));

		destMap.put("abstract", summaryValPage);
		destMap.put("finance_type", typeValPage);
		destMap.put("amount", payAmountValPage);
		destMap.put("acct_balance", acctRemainValPage);
		// 获取数据库的值
		expectMap = ProductDao.selectFeeRecord(sdt_name, "手续费");
		if (StringUtils.isEquals(expectMap, destMap, log)) {
			log.info("数据验证成功");
			flag = true;
		} else {
			log.info("数据验证失败");
			flag = false;
		}
		return flag;

	}
}
