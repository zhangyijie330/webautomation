package com.autotest.service.bmpService;

import java.util.HashMap;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.autotest.driver.KeyWords;
import com.autotest.enums.BusinessType;
import com.autotest.enums.DateType;
import com.autotest.enums.Key;
import com.autotest.enums.ProductType;
import com.autotest.enums.RepayWay;
import com.autotest.or.ObjectLib;
import com.autotest.utility.DateUtils;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;
import com.autotest.utility.UserAgentUtil;

/**
 * 项目运营-->保理发布
 * 
 * @author wb0002
 * 
 */
public class FactoringRelease {

	public KeyWords kw = null;

	/**
	 * 到期还本付息
	 */
	public String E = "到期还本付息";
	/**
	 * 半年付息到期还本
	 */
	public String HALFYEAR = "半年付息到期还本";
	/**
	 * 每月等额本息
	 */
	public String PERMONTH = " 每月等额本息 ";
	/**
	 * 按月等额本息
	 */
	public String PERMONTHFIXN = "按月等额本息";
	/**
	 * 按月付息，到期还本
	 */
	public String D = "按月付息，到期还本";

	/**
	 * 鑫股一号
	 */
	public String BUSI_F = "鑫股一号";
	/**
	 * 经营贷
	 */
	public String BUSI_E = "经营贷";
	/**
	 * 三农贷
	 */
	public String BUSI_D = "三农贷";
	/**
	 * 电商贷
	 */
	public String BUSI_C = "电商贷";
	/**
	 * 鑫银一号
	 */
	public String BUSI_B = "鑫银一号";
	/**
	 * 鑫保一号
	 */
	public String BUSI_A = "鑫保一号";
	/**
	 * 假日升
	 */
	public String BUSI_G = "假日升";

	/**
	 * 学历
	 */
	public static HashMap<Integer, String> educational_hashMap = null;

	/**
	 * 投资资金转入账户
	 */
	public static HashMap<Integer, String> fundAccount_hashMap = null;

	static {
		educational_hashMap = new HashMap<Integer, String>();
		educational_hashMap.put(1, "博士后");
		educational_hashMap.put(2, "博士");
		educational_hashMap.put(3, "硕士");
		educational_hashMap.put(5, "本科");
		educational_hashMap.put(6, "大专");
		educational_hashMap.put(7, "高中");
		educational_hashMap.put(8, "其他");
		// ////////////////////////
		fundAccount_hashMap = new HashMap<Integer, String>();
		fundAccount_hashMap.put(701, "上海鑫合汇网络科技有限公司(工商银行-6227003814170172872)");
		fundAccount_hashMap.put(734, "上海鑫合汇第二张卡(招商银行-789789782173487928134)");
		fundAccount_hashMap.put(739, "招商卡(招商银行-33001616127053005280)");
		// ////////////////////////
	}

	public FactoringRelease(KeyWords kw) {
		this.kw = kw;
	}

	/**
	 * xhh-235: 访问【项目运营-保理发布】页面 前提: xhh-234
	 * 
	 * @return 访问【项目运营-保理发布】页面实际结果。false:失败；true:成功
	 * @throws Exception
	 */
	public boolean enterFactoringRelease() throws Exception {
		boolean result = false;
		// 【项目运营】
		kw.click(OrUtil.getBy("index_projectOperation_xpath",
				ObjectLib.BMPObjectLib));
		// 【保理发布】
		kw.click(OrUtil.getBy("index_ProjectRelease_xpath",
				ObjectLib.BMPObjectLib));
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
					"body_dataTable_tr_xpath", ObjectLib.BMPObjectLib));
			if (webElements.size() > 0) {
				result = true;
				break;
			}
			ThreadUtil.sleep();
		}
		return result;
	}

	/**
	 * xhh-237 进入【保理发布-基本信息】页签 前提：xhh-235
	 * 
	 * @return 进入【保理发布-基本信息】页签实际结果。false:失败；true:成功
	 * @throws Exception
	 */
	public boolean enterNewReleaseProject() throws Exception {
		boolean result = false;
		kw.click(OrUtil.getBy("add_add_btn_xpath", ObjectLib.BMPObjectLib));
		kw.waitPageLoad();
		for (int i = 0; i < 3; i++) {
			List<WebElement> webElements = kw.getWebElements(OrUtil.getBy(
					"add_baseInfo_form_xpath", ObjectLib.BMPObjectLib));
			if (webElements.size() > 0) {
				result = true;
				break;
			}
			ThreadUtil.sleep();
		}
		return result;
	}

	/**
	 * xhh-238 填写保理项目基本信息 前提:xhh-237
	 * 
	 * @param ifJoinPlan
	 *            是否可加入鑫计划 boolean 说明：true-->是;false-->否
	 * @param borrowerType
	 *            借款人类型 int 说明：1-->企业;2-->个人
	 * @param borrower
	 *            借款人 int 说明：1-->表示选择当前页借款方列表第一个，以此类推;最大5
	 * @param financingScale
	 *            融资规模 long 说明：金额
	 * @param timeOut
	 *            用款期限 int 说明：数字
	 * @param dateType
	 *            用款期限单位 DateType 说明：dd-->天；mm-->月；yy-->年
	 * @param rate
	 *            预期年化利率 double 说明：double类型。如8.5。
	 * @param repayWay
	 *            还款方式 RepayWay 说明：还款方式下拉选择框选项中具体方式的value。
	 *            E-->到期还本付息;halfyear-->半年付息到期还本 ;permonth-->每月等额本息;
	 *            PermonthFixN-->按月等额本息;D-->按月付息，到期还本
	 * @param businessType
	 *            业务类型 BusinessType 说明：F-->鑫股一号;E-->经营贷;D-->三农贷;C-->电商贷;
	 *            B-->鑫银一号;A-->鑫保一号;G-->假日升
	 * @param createTime
	 *            融资发标时间 String 说明：String类型的时间格式，如：2016-08-04 15:49:24
	 * @param beginTime
	 *            融资开标时间 String 说明：同上
	 * @param endTime
	 *            融资截止时间 String 说明：同上
	 * 
	 * @return
	 * @throws Exception
	 */
	public String writeBasicInformationFactoringProjects(boolean ifJoinPlan,
			int borrowerType, int borrower, long financingScale, int timeOut,
			DateType dateType, double rate, RepayWay repayWay,
			BusinessType businessType, String createTime, String beginTime,
			String endTime) throws Exception {
		String borrowName = null;
		// 【是否可加入鑫计划】选择[否]
		if (ifJoinPlan) {// 是否可加入鑫计划->是
			kw.click(OrUtil.getBy("add_ifJoinPlan_radio_yes_xpath",
					ObjectLib.BMPObjectLib));
		} else {// 否
			kw.click(OrUtil.getBy("add_ifJoinPlan_radio_no_xpath",
					ObjectLib.BMPObjectLib));
		}
		// 【借款人类型】选择[个人]
		kw.click(OrUtil.getBy("add_borrowerType_btn_xpath",
				ObjectLib.BMPObjectLib));
		if (borrowerType == 1) {// 借款人类型1-->企业;
			kw.click(OrUtil.getBy("add_borrowerType_company_xpath",
					ObjectLib.BMPObjectLib));
		} else if (borrowerType == 2) {// 2-->个人
			kw.click(OrUtil.getBy("add_borrowerType_personal_xpath",
					ObjectLib.BMPObjectLib));
		} else {
			throw new Exception("参数borrowerType[" + borrowerType + "]错误!");
		}
		// 点击【借款人】选择框
		kw.click(OrUtil.getBy("add_borrowName_btn_xpath",
				ObjectLib.BMPObjectLib));
		boolean flag = kw.isElementVisible(OrUtil.getBy(
				"add_panel_window_xpath", ObjectLib.BMPObjectLib));
		if (flag) {
			if (kw.isElementVisible(OrUtil.getBy(
					"add_body_borrowerDataTable_xpath", ObjectLib.BMPObjectLib))) {
				List<WebElement> webElements = kw.getWebElements(OrUtil.getBy(
						"add_tr_borrowerDataTable_xpath",
						ObjectLib.BMPObjectLib));
				if (!(webElements.size() > 0)) {
					return null;
				}
			}
		} else {
			return null;
		}
		// 在借款人列表中勾选第一条数据，点击左上角【选择】按钮
		kw.click(OrUtil.getBy("add_borrowerData" + borrower + "_xpath",
				ObjectLib.BMPObjectLib));
		String id = kw.getAttribute(OrUtil.getBy("add_borrowerData" + borrower
				+ "_xpath", ObjectLib.BMPObjectLib), "id");
		String text = kw.getText(By.xpath("//tr[@id='" + id + "']/td[2]/div"));
		kw.executeJS("chooseSelect()");
		String val = kw.getAttribute(OrUtil.getBy("add_borrowName_btn_xpath",
				ObjectLib.BMPObjectLib), "value");
		if (!text.equals(val)) {
			throw new Exception("实际值[" + val + "];于期望值[" + text + "]不等!");
		} else {
			borrowName = text;// 将选中的借款人保存下来
		}
		ThreadUtil.sleep(2);
		// 【融资规模】填写[100000]
		kw.setValue(
				OrUtil.getBy("add_demandAmount_xpath", ObjectLib.BMPObjectLib),
				financingScale + "");
		// 【用款期限】填写[3]，后方的时间单位选择[天]
		kw.setValue(
				OrUtil.getBy("add_time_input_xpath", ObjectLib.BMPObjectLib),
				timeOut + "");
		ThreadUtil.sleep(1);
		// 用款期限单位
		kw.clickDiv(OrUtil.getBy("add_time_btn_xpath", ObjectLib.BMPObjectLib));
		switch (dateType) {
		case day:
			kw.clickDiv(OrUtil.getBy("add_time_dd_xpath",
					ObjectLib.BMPObjectLib));
			break;
		case month:
			kw.clickDiv(OrUtil.getBy("add_time_mm_xpath",
					ObjectLib.BMPObjectLib));
			break;
		case year:
			kw.clickDiv(OrUtil.getBy("add_time_yy_xpath",
					ObjectLib.BMPObjectLib));
			break;
		default:
			kw.clickDiv(OrUtil.getBy("add_time_dd_xpath",
					ObjectLib.BMPObjectLib));
			break;
		}
		// 【预期年化利率】填写[8.5]
		kw.setValue(
				OrUtil.getBy("add_rate_input_xpath", ObjectLib.BMPObjectLib),
				rate + "");
		// 【还款方式】
		switch (repayWay) {
		case E:
			break;
		case halfyear:
			break;
		case permonth:
			kw.clickDiv(OrUtil.getBy("add_repayWay_btn_xpath",
					ObjectLib.BMPObjectLib));
			kw.clickDivByText(this.PERMONTH);
			break;
		case PermonthFixN:
			break;
		case D:
			kw.clickDiv(OrUtil.getBy("add_repayWay_btn_xpath",
					ObjectLib.BMPObjectLib));
			kw.clickDivByText(this.D);
			break;
		default:
			break;
		}
		// 【业务类型】选择[鑫股一号]
		kw.clickDiv(OrUtil.getBy("add_busiType_btn_xpath",
				ObjectLib.BMPObjectLib));
		switch (businessType) {
		case F:
			kw.clickDivByText(this.BUSI_F);
			break;
		case E:
			kw.clickDivByText(this.BUSI_E);
			break;
		case D:
			kw.clickDivByText(this.BUSI_D);
			break;
		case C:
			kw.clickDivByText(this.BUSI_C);
			break;
		case B:
			kw.clickDivByText(this.BUSI_B);
			break;
		case A:
			kw.clickDivByText(this.BUSI_A);
			break;
		case G:
			kw.clickDivByText(this.BUSI_G);
			break;
		default:
			break;
		}
		// 设置【融资发标时间】、【融资开标时间】、【融资截止时间】
		// 张艾说发标时间的秒，必须是10的倍数，现修改为固定的秒数，10秒。
		createTime = createTime.substring(0, createTime.length() - 2) + "10";
		System.out.println("createTime=[" + createTime + "]");
		// //////////////////////////////////////////////////
		kw.setValue(OrUtil.getBy("add_createTime_input_xpath",
				ObjectLib.BMPObjectLib), createTime);
		kw.setValue(OrUtil.getBy("add_beginTime_input_xpath",
				ObjectLib.BMPObjectLib), beginTime);
		kw.setValue(
				OrUtil.getBy("add_endTime_input_xpath", ObjectLib.BMPObjectLib),
				endTime);
		return borrowName;
	}

	/**
	 * xhh-239 进入【保理发布-附加信息】页签 前提:xhh-238
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean enterAddInformation() throws Exception {
		boolean result = false;
		ThreadUtil.sleep();
		kw.click(OrUtil.getBy("add_addInformation_btn_xpath",
				ObjectLib.BMPObjectLib));
		// 判断附加信息页面是否已经显示
		if (kw.isElementVisible(OrUtil.getBy("add_addInfo_product_form_xpath",
				ObjectLib.BMPObjectLib))) {
			if (kw.isElementVisible(OrUtil.getBy("add_addInfo_bid_form_xpath",
					ObjectLib.BMPObjectLib))) {
				if (kw.isElementVisible(OrUtil.getBy(
						"add_addInfo_protocolparamform_xpath",
						ObjectLib.BMPObjectLib))) {
					result = true;
				}
			}
		}
		return result;
	}

	/**
	 * xhh-240 填写保理项目附加信息 前提:xhh-239
	 * 
	 * @param productName
	 *            产品名称 String
	 * @param moneyUsing
	 *            资金用途 String
	 * @param repayOrigin
	 *            还款来源 String
	 * @param education
	 *            学历 int 说明：必须为1-7之间的值，代表的是每个学历div中的value
	 * @param annualEarnings
	 *            个人年现金收入 long
	 * @param account
	 *            投资资金转入账户 int 说明：代表的是每个投资资金转入账户div中的value
	 * @param contract
	 *            原债权人与原债务人合同 String
	 * @param contractNo
	 *            原债权人与原债务人合同合同编号 String
	 * @param letterNo
	 *            同意担保函编号 String
	 * @param contractNo2
	 *            国内保理合同编号 String
	 * @param name
	 *            原债务人姓名名称 String
	 * @param idNumber
	 *            原债务人证件号码 String
	 * @param buyBackNum
	 *            提前回购操作通知发送提前量 int
	 * @param beforeSubmitNum
	 *            展期申请提交提前量 int
	 * @param paymentRatio
	 *            违约金支付比例 int
	 * @return
	 * @throws Exception
	 */
	public boolean writeAddInfo(String productName, String moneyUsing,
			String repayOrigin, int education, long annualEarnings,
			int account, String contract, String contractNo, String letterNo,
			String contractNo2, String name, String idNumber, int buyBackNum,
			int beforeSubmitNum, int paymentRatio) throws Exception {
		boolean result = false;
		// 获取浏览器类型
		String borrowType = UserAgentUtil.getUserAgent(kw);
		System.out.println("borrowType=[" + borrowType + "]");
		// 1.【产品名称】
		kw.setValue(OrUtil.getBy("add_addInfo_productName_xpath",
				ObjectLib.BMPObjectLib), productName);
		kw.blurById("productName");
		ThreadUtil.sleep(2);
		if (kw.isExistAlert()) {
			// 获取Alert内容
			String title = kw.getAlertText();
			// 关闭Alert
			// kw.acceptAlert();
			throw new Exception(title);
		}
		// 2.【资金用途】
		kw.setValue(OrUtil.getBy("add_addInfo_money_using_xpath",
				ObjectLib.BMPObjectLib), moneyUsing);
		// 3.【还款来源】
		kw.setValue(OrUtil.getBy("add_addInfo_repay_origin_xpath",
				ObjectLib.BMPObjectLib), repayOrigin);
		// 【性别】
		String xpath = "(//input[@id='sex1'])[2]";
		kw.click(By.xpath(xpath));
		ThreadUtil.sleep();
		// 4.【学历】
		kw.clickDiv(OrUtil.getBy("add_addInfo_eduBtn_xpath",
				ObjectLib.BMPObjectLib));
		String educational = educational_hashMap.get(education);
		kw.clickDivByText(educational);
		if (borrowType.equals("Chrome")) {
			kw.sendKeys(Key.PAGE_DOWN);
		}
		// 5.【个人年现金收入】
		kw.setValue(OrUtil.getBy("add_addInfo_annualEarnings_xpath",
				ObjectLib.BMPObjectLib), annualEarnings + "");
		// 6.【投资资金转入账户】
		kw.clickDiv(OrUtil.getBy("add_addInfo_fundAccountBtn_xpath",
				ObjectLib.BMPObjectLib));
		String fundAccount = fundAccount_hashMap.get(account);
		kw.clickDivByText(fundAccount);
		// 7.【原债权人与原债务人合同】
		kw.setValue(OrUtil.getBy("add_addInfo_yuanhetong_xpath",
				ObjectLib.BMPObjectLib), contract);
		if (borrowType.equals("Chrome")) {
			kw.sendKeys(Key.PAGE_DOWN);
		}
		// 8.【原债权人与原债务人合同合同编号】
		kw.setValue(OrUtil.getBy("add_addInfo_yuanhetong_no_xpath",
				ObjectLib.BMPObjectLib), contractNo);
		// 9.【同意担保函编号】
		kw.setValue(OrUtil.getBy("agree_letterNo_input_xpath",
				ObjectLib.BMPObjectLib), letterNo);
		// 10.【国内保理合同编号】
		kw.setValue(OrUtil.getBy("add_addInfo_guoneibaolihetong_no_xpath",
				ObjectLib.BMPObjectLib), contractNo2);
		if (borrowType.equals("Chrome")) {
			kw.sendKeys(Key.PAGE_DOWN);
		}
		// 11.【原债务人姓名名称】
		kw.setValue(OrUtil.getBy("add_addInfo_yuanzhaiwuName_xpath",
				ObjectLib.BMPObjectLib), name);
		// 12.【原债务人证件号码】
		kw.setValue(OrUtil.getBy("add_addInfo_yuanzhaiwu_zhengjianhao_xpath",
				ObjectLib.BMPObjectLib), idNumber);
		// 13.【提前回购操作通知发送提前量】
		kw.setValue(OrUtil.getBy(
				"add_addInfo_tiqianhuikuantongzhifasongtiqianliang_xpath",
				ObjectLib.BMPObjectLib), buyBackNum + "");
		// 14.【展期申请提交提前量】
		kw.setValue(OrUtil.getBy("beforeSubmitNum_input_xpath",
				ObjectLib.BMPObjectLib), beforeSubmitNum + "");
		ThreadUtil.sleep();
		if (borrowType.equals("Chrome")) {
			kw.sendKeys(Key.PAGE_DOWN);
		}
		// 15.【违约金支付比例】
		String js = "$(\"input[name='protocolList[24].paramValue']\").attr(\"value\",\""
				+ paymentRatio + "\")";
		System.out.println("js=[" + js + "]");
		kw.executeJS(js);
		// 最后判断一下【产品名称】是否填写成功
		String productName_val = kw.getAttribute(OrUtil.getBy(
				"add_addInfo_productName_xpath", ObjectLib.BMPObjectLib),
				"value");
		if (!StringUtils.isEquals(productName_val, productName)) {
			result = false;
			throw new Exception("产品名称实际输入值与期望值不同!实际值:[" + productName_val
					+ "];期望值:[" + productName + "]");
		}
		result = true;
		return result;
	}

	/**
	 * xhh-241 发布保理项目 前提:xhh-240
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean releaseProject() throws Exception {
		boolean flag = false;
		// 【发布】
		kw.click(OrUtil.getBy("add_addInfo_releaseProject_btn_xpath",
				ObjectLib.BMPObjectLib));
		ThreadUtil.sleep();
		try {
			WebElement webelement = kw.getWebElement2(By
					.xpath("//div[text()='生成项目编号失败，请重新点击发布按钮!']"));
			System.out.println("webelement == null => [" + (null == webelement)
					+ "]");
			if (webelement != null) {
				// 点击确定
				kw.click(By.xpath("//span[text()='确定']"));
				ThreadUtil.sleep();
				// 回调
				releaseProject();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		boolean isVis = false;
		for (int i = 0; i < 3; i++) {
			ThreadUtil.sleep();
			if (kw.isExistAlert()) {
				// 获取Alert内容
				String title = kw.getAlertText();
				System.out.println("title=[" + title + "]");
				// 关闭Alert
				// kw.acceptAlert();
				throw new Exception(title);
			}
			try {
				isVis = kw.isElementVisible(OrUtil.getBy(
						"add_addInfo_msg_xpath", ObjectLib.BMPObjectLib));
				break;
			} catch (Exception e) {

			}
		}
		isVis = kw.isElementVisible(OrUtil.getBy("add_addInfo_msg_xpath",
				ObjectLib.BMPObjectLib));
		if (isVis) {
			String msg = kw.getText2(OrUtil.getBy("add_addInfo_msg_xpath",
					ObjectLib.BMPObjectLib));
			if (msg.equals("保存成功！")) {
				flag = true;
			} else {
				if (msg != null && msg.indexOf("生成项目编号失败，请重新点击发布按钮!") >= 0) {
					// TODO 重新点击发布按钮
					// TODO 未重现出来，待补充
				} else {
					throw new Exception(msg);
				}
			}
		} else {
			String errMsg = "点击发布后，未检测到页面做出相关反应!";
			throw new Exception(errMsg);
		}
		return flag;
	}

	/**
	 * 生产产品名称 格式：产品类型(拼音缩写) + 日期(yyyy-MM-dd) + 3位数的随机数
	 * 
	 * @param productType
	 *            产品类型
	 * @return
	 */
	public String getProductName(ProductType productType) {
		StringBuffer productName = new StringBuffer();
		switch (productType) {
		case RYS:
			productName.append("RYS");
			break;
		case YYS:
			productName.append("YYS");
			break;
		default:
			productName.append("RYS");
			break;
		}
		String date = DateUtils.formatDate("yyyyMMdd");
		productName.append(date);
		int bit = MathUtil.getRandom();
		int ten = MathUtil.getRandomAll();
		int hundreds = MathUtil.getRandomAll();
		productName.append(hundreds).append(ten).append(bit);
		return productName.toString();
	}

	/**
	 * xhh-242 发布结果确认 前提:xhh-241
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean releaseResultConfirm() throws Exception {
		boolean flag = false;
		ThreadUtil.sleep();
		boolean isExist = kw.isElementExist2(OrUtil.getBy(
				"add_addInfo_msg_yesBtn_xpath", ObjectLib.BMPObjectLib));
		System.out.println("isExist=[" + isExist + "]");
		// 点击发布成功提示框的【确定】按钮
		kw.click(OrUtil.getBy("add_addInfo_msg_yesBtn_xpath",
				ObjectLib.BMPObjectLib));
		// 跳转回保理发布主页面
		/*
		 * for (int i = 0; i < 3; i++) { if
		 * (kw.isElementExist(OrUtil.getBy("iframe_work_frame_id",
		 * ObjectLib.BMPObjectLib))) { break; } ThreadUtil.sleep(); }
		 * kw.switchToFrame("work_frame");
		 */
		for (int i = 0; i < 3; i++) {
			List<WebElement> webElements = kw.getWebElements(OrUtil.getBy(
					"body_dataTable_tr_xpath", ObjectLib.BMPObjectLib));
			if (webElements.size() > 0) {
				flag = true;
				break;
			}
			ThreadUtil.sleep();
		}
		return flag;
	}

}
