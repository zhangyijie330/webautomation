package com.autotest.service.bmpService;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import com.autotest.driver.KeyWords;
import com.autotest.enums.BusinessType;
import com.autotest.enums.DateType;
import com.autotest.enums.DbType;
import com.autotest.enums.ProductType;
import com.autotest.enums.RepayWay;
import com.autotest.or.ObjectLib;
import com.autotest.utility.DateUtils;
import com.autotest.utility.DbUtil;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.ProfitCal;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * 【项目运营】-->【项目审核】
 * 
 * @author wb0002
 * 
 */
public class ProjectAudit {

	public KeyWords	kw	= null;

	public ProjectAudit(KeyWords kw) {
		this.kw = kw;
	}

	/**
	 * xhh-243 访问【项目运营-项目审核】页面 前提:xhh-242
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean enterProjectAudit() throws Exception {
		boolean flag = false;
		// 在【项目运营】的下拉菜单中点击【项目审核】菜单
		kw.switchToDefaultFrame();
		kw.click(OrUtil.getBy("audit_projectAudit_btn_xpath",
				ObjectLib.BMPObjectLib));
		for (int i = 0; i < 3; i++) {
			if (kw.isElementExist(OrUtil.getBy("audit_work_frame_xpath",
					ObjectLib.BMPObjectLib))) {
				break;
			}
			ThreadUtil.sleep();
		}
		kw.switchToFrame("work_frame");
		for (int i = 0; i < 3; i++) {
			List<WebElement> webElements = kw.getWebElements(OrUtil.getBy(
					"audit_data_tr_xpath", ObjectLib.BMPObjectLib));
			if (webElements.size() > 0) {
				flag = true;
				break;
			}
			ThreadUtil.sleep();
		}
		return flag;
	}

	/**
	 * xhh-244 填写筛选条件 前提:xhh-243
	 * 
	 * @param productName
	 *            产品名称
	 * @return
	 * @throws Exception
	 */
	public boolean writeFilter(String productName) throws Exception {
		boolean flag = false;
		kw.setValue(OrUtil.getBy("audit_productName_input_xpath",
				ObjectLib.BMPObjectLib), productName);
		String val = kw.getAttribute(OrUtil.getBy(
				"audit_productName_input_xpath", ObjectLib.BMPObjectLib),
				"value");
		if (StringUtils.isEquals(productName, val)) {
			flag = true;
		} else {
			throw new Exception("实际值[" + val + "]与期望值[" + productName + "]不同!");
		}
		return flag;
	}

	/**
	 * xhh-245 筛选出刚才发布的保理项目 前提:xhh-244
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean queryProduct() throws Exception {
		boolean flag = false;
		// 点击筛选栏最末的【查询】按钮
		String js = "doQuery()";
		kw.executeJS(js);
		//
		String text = "数据正在加载中";
		boolean f = false;
		boolean isExist;
		long beginTime = DateUtils.getTime();
		while (!f) {
			isExist = kw.isExistByText(text);
			if (isExist) {
				f = true;
			}
			long nowTime = DateUtils.getTime();
			if ((nowTime - beginTime) > 10000) {
				f = false;
				break;
			}
		}
		while (f) {
			isExist = kw.isExistByText(text);
			if (!isExist) {
				f = false;
			}
		}
		// 判断查询列表
		List<WebElement> webElements = kw.getWebElements(OrUtil.getBy(
				"audit_productList_tr_xpath", ObjectLib.BMPObjectLib));
		// System.out.println(webElements.size());
		if (webElements.size() == 1) {
			flag = true;
		}
		return flag;
	}

	/**
	 * xhh-246 校验列表展示的字段及字段值 前提：xhh-245
	 * 
	 * @param productName
	 *            项目名称 String
	 * @param borrowerName
	 *            借款人 String
	 * @param productType
	 *            项目类型 ProductType
	 * @param financingScale
	 *            融资规模（元） long
	 * @param rate
	 *            预期年化利率 double
	 * @param repayWay
	 *            还款方式 RepayWay
	 * @param businessType
	 *            业务类型 BusinessType
	 * @return
	 * @throws Exception
	 */
	public String checkProductInfo(String productName, String borrowerName,
			ProductType productType, long financingScale, double rate,
			RepayWay repayWay, BusinessType businessType) throws Exception {
		boolean flag = false;
		// 【序号】：[1]
		String no = kw.getText2(OrUtil.getBy("audit_numberID_td_xpath",
				ObjectLib.BMPObjectLib));
		if (!StringUtils.isEquals(no, "1")) {
			flag = false;
			throw new Exception("序号[" + no + "]不为1!");
		}
		// 勾选框
		String type = kw.getAttribute(OrUtil.getBy(
				"audit_gouxuankuan_td_xpath", ObjectLib.BMPObjectLib), "type");
		if (!StringUtils.isEquals(type, "checkbox")) {
			flag = false;
			throw new Exception("勾选框[" + type + "]类型非checkbox!");
		}
		// 项目名称
		String entryName = kw.getText(OrUtil.getBy(
				"audit_productName_td_xpath", ObjectLib.BMPObjectLib));
		if (!StringUtils.isEquals(productName, entryName)) {
			flag = false;
			throw new Exception("发布时项目名称[" + productName + "]与审核时项目名称["
					+ entryName + "]不同!");
		}
		// 借款人
		String borrower = kw.getText(OrUtil.getBy("audit_borrower_td_xpath",
				ObjectLib.BMPObjectLib));
		if (!StringUtils.isEquals(borrowerName, borrower)) {
			flag = false;
			throw new Exception("发布项目时借款人[" + borrowerName + "]与审核项目时借款人["
					+ borrower + "]不同!");
		}
		// 项目类型
		String itemType = kw.getText(OrUtil.getBy("audit_productType_td_xpath",
				ObjectLib.BMPObjectLib));
		switch (productType) {
			case RYS:
				if (!StringUtils.isEquals(itemType, "日益升")) {
					flag = false;
					throw new Exception("发布项目时项目类型[RYS]与审核项目时项目类型[" + itemType
							+ "]不同!");
				}
				break;
			case YYS:
				if (!StringUtils.isEquals(itemType, "月益升")) {
					flag = false;
					throw new Exception("发布项目时项目类型[YYS]与审核项目时项目类型[" + itemType
							+ "]不同!");
				}
				break;
			default:
				flag = false;
				break;
		}
		// 融资规模(元)
		String amt = kw.getText(OrUtil.getBy("audit_financingScale_td_xpath",
				ObjectLib.BMPObjectLib));
		if (!StringUtils.numIsEquals(financingScale + "",
				StringUtils.format(amt.replaceAll(",", "")))) {
			flag = false;
			throw new Exception("发布项目时融资规模[" + financingScale + "]与审核项目时融资规模["
					+ StringUtils.format(amt.replaceAll(",", "")) + "]不同!");
		}
		// 预期年化利率
		String annualRate = kw.getText(OrUtil.getBy("audit_rate_td_xpath",
				ObjectLib.BMPObjectLib));
		if (!StringUtils.numIsEquals(rate + "", annualRate.replaceAll("%", ""))) {
			flag = false;
			throw new Exception("发布项目时预期年化利率[" + rate + "%]与审核项目时预期年化利率["
					+ annualRate + "]不同!");
		}
		// 还款方式
		String repaymentMethod = kw.getText(OrUtil.getBy(
				"audit_repayWay_td_xpath", ObjectLib.BMPObjectLib));
		switch (repayWay) {
			case E:
				if (!StringUtils.isEquals(repaymentMethod, "到期还本付息")) {
					flag = false;
					throw new Exception("发布项目时还款方式[到期还本付息]与审核项目时还款方式["
							+ repaymentMethod + "]不同!");
				}
				break;
			case halfyear:
				if (!StringUtils.isEquals(repaymentMethod, "半年付息到期还本")) {
					flag = false;
					throw new Exception("发布项目时还款方式[半年付息到期还本]与审核项目时还款方式["
							+ repaymentMethod + "]不同!");
				}
				break;
			case permonth:
				if (!StringUtils.isEquals(repaymentMethod, "每月等额本息")) {
					flag = false;
					throw new Exception("发布项目时还款方式[每月等额本息]与审核项目时还款方式["
							+ repaymentMethod + "]不同!");
				}
				break;
			case PermonthFixN:
				if (!StringUtils.isEquals(repaymentMethod, "按月等额本息")) {
					flag = false;
					throw new Exception("发布项目时还款方式[按月等额本息]与审核项目时还款方式["
							+ repaymentMethod + "]不同!");
				}
				break;
			case D:
				if (!StringUtils.isEquals(repaymentMethod, "按月付息，到期还本")) {
					flag = false;
					throw new Exception("发布项目时还款方式[按月付息，到期还本]与审核项目时还款方式["
							+ repaymentMethod + "]不同!");
				}
				break;
			default:
				flag = false;
				break;
		}
		// 发布时间
		String time = kw.getText(OrUtil.getBy("audit_sendTime_td_xpath",
				ObjectLib.BMPObjectLib));
		// 状态
		String state = kw.getText(OrUtil.getBy("audit_state_td_xpath",
				ObjectLib.BMPObjectLib));
		if (!StringUtils.isEquals(state, "待审核")) {
			flag = false;
			throw new Exception("审核项目时项目状态[" + state + "]非待审核!");
		}
		// 业务类型
		String serviceType = kw.getText(OrUtil.getBy(
				"audit_businessType_td_xpath", ObjectLib.BMPObjectLib));
		switch (businessType) {
			case F:
				if (!StringUtils.isEquals(serviceType, "鑫股一号")) {
					flag = false;
					throw new Exception("发布项目时业务类型[鑫股一号]与审核项目时业务类型["
							+ serviceType + "]不同!");
				}
				break;
			case E:
				if (!StringUtils.isEquals(serviceType, "经营贷")) {
					flag = false;
					throw new Exception("发布项目时业务类型[经营贷]与审核项目时业务类型["
							+ serviceType + "]不同!");
				}
				break;
			case D:
				if (!StringUtils.isEquals(serviceType, "三农贷")) {
					flag = false;
					throw new Exception("发布项目时业务类型[三农贷]与审核项目时业务类型["
							+ serviceType + "]不同!");
				}
				break;
			case C:
				if (!StringUtils.isEquals(serviceType, "电商贷")) {
					flag = false;
					throw new Exception("发布项目时业务类型[电商贷]与审核项目时业务类型["
							+ serviceType + "]不同!");
				}
				break;
			case B:
				if (!StringUtils.isEquals(serviceType, "鑫银一号")) {
					flag = false;
					throw new Exception("发布项目时业务类型[鑫银一号]与审核项目时业务类型["
							+ serviceType + "]不同!");
				}
				break;
			case A:
				if (!StringUtils.isEquals(serviceType, "鑫保一号")) {
					flag = false;
					throw new Exception("发布项目时业务类型[鑫保一号]与审核项目时业务类型["
							+ serviceType + "]不同!");
				}
				break;
			case G:
				if (!StringUtils.isEquals(serviceType, "假日升")) {
					flag = false;
					throw new Exception("发布项目时业务类型[假日升]与审核项目时业务类型["
							+ serviceType + "]不同!");
				}
				break;
			default:
				flag = false;
				break;
		}
		// 业务类型
		String serviceType2 = kw.getText(OrUtil.getBy(
				"audit_businessType2_td_xpath", ObjectLib.BMPObjectLib));
		if (!StringUtils.isEquals(serviceType2, "保理")) {
			flag = false;
			throw new Exception("审核项目时业务类型[" + serviceType2 + "]非保理!");
		}
		flag = true;
		if (flag) {
			if (time.length() != 19) {
				throw new Exception("校验列表展示的字段及字段值time=[" + time + "]异常!");
			}
			return time;
		} else {
			throw new Exception("校验列表展示的字段及字段值结果flag=[" + flag + "]");
		}
	}

	/**
	 * xhh-247 勾选刚才发布的保理项目 前提:xhh-246
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean selectProject() throws Exception {
		boolean flag = false;
		String js = "dataTable.selectRow(0);";
		kw.executeJS(js);
		for (int i = 0; i < 3; i++) {
			ThreadUtil.sleep();
			String classStr = kw.getAttribute(OrUtil.getBy(
					"audit_productList_tr_xpath", ObjectLib.BMPObjectLib),
					"class");
			if (classStr.indexOf("datagrid-row-selected") >= 0) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	/**
	 * xhh-248 进入【项目审核-融资信息】页签 前提:xhh-247
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean enterFinancing() throws Exception {
		boolean flag = false;
		String js = "doAudit()";
		kw.executeJS(js);
		ThreadUtil.sleep(1);
		//
		boolean isExist = kw.isElementExist(OrUtil.getBy(
				"audit_financingInfo_form_xpath", ObjectLib.BMPObjectLib));
		if (isExist) {
			flag = true;
		}
		return flag;
	}

	/**
	 * xhh-249 进入【项目审核-项目审核】页签 前提:xhh-248
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean enterProjectAuditPage() throws Exception {
		boolean flag = false;
		kw.click(OrUtil.getBy("audit_ProjectAudit_btn_xpath",
				ObjectLib.BMPObjectLib));
		ThreadUtil.sleep();
		boolean isExist = kw.isElementExist(OrUtil.getBy(
				"audit_ProjectAudit_table_xpath", ObjectLib.BMPObjectLib));
		if (isExist) {
			flag = true;
		}
		return flag;
	}

	/**
	 * xhh-250 填写项目审核信息 前提:xhh-249
	 * 
	 * @param isXzdValue
	 *            是否鑫整点 int 说明：1-->是;0-->否
	 * @param isExtendValue
	 *            是否展期 int 说明：1-->展期;0-->不展期
	 * @param isExtendTime
	 *            展期天数 int
	 * @param putInSite
	 *            投放站点 int 说明：1234567889-->全部;1234567890-->鑫合汇;1234567891-->拔萃;
	 *            1234567892-->财富;1234567893-->融资服务中心;1234567991-->数米财富;
	 *            1234567992-->聚财富;1234567994-->118114理财;
	 * @param isEarlyRepayValue
	 *            是否可以提前还款 int 说明：1-->是;0-->否
	 * @param earlyRepayDays
	 *            可以提前还款天数 int
	 * @param isTransferValue
	 *            是否可以变现 int 说明：1-->是;0-->否
	 * @param putInTerminal
	 *            投放终端 int 说明：0-->全部;1-->网站端;2-->微信WAP端;3-->手机APP端;
	 * @param contractNo
	 *            合同编号 String
	 * @return
	 * @throws Exception
	 */
	public boolean writeProjectAuditInfo(int isXzdValue, int isExtendValue,
			int isExtendTime, int putInSite, int isEarlyRepayValue,
			int earlyRepayDays, int isTransferValue, int putInTerminal,
			String contractNo) throws Exception {
		boolean flag = true;
		// 是否鑫整点
		if (isXzdValue == 0) {// 否
			kw.click(OrUtil.getBy("audit_fiPrjExt_isXzd0_xpath",
					ObjectLib.BMPObjectLib));
		} else if (isXzdValue == 1) {// 是
			kw.click(OrUtil.getBy("audit_fiPrjExt_isXzd1_xpath",
					ObjectLib.BMPObjectLib));
		}
		// 是否展期
		if (isExtendValue == 0) {// 不展期
			kw.click(OrUtil.getBy("audit_fiPrjExt_isExtend0_xpath",
					ObjectLib.BMPObjectLib));
		} else if (isExtendValue == 1) {// 展期
			kw.click(OrUtil.getBy("audit_fiPrjExt_isExtend1_xpath",
					ObjectLib.BMPObjectLib));
			// 展期天数
			kw.setValue(OrUtil.getBy("audit_extendTime_xpath",
					ObjectLib.BMPObjectLib), isExtendTime + "");
		}
		// 投放站点
		kw.click(OrUtil.getBy("audit_memberId_noShare_btn_xpath",
				ObjectLib.BMPObjectLib));
		if (putInSite == 1234567889) {// 全部
			kw.clickDivByText("全部");
		} else if (putInSite == 1234567890) {// 鑫合汇
			kw.clickDivByText("鑫合汇");
		} else if (putInSite == 1234567891) {// 拔萃
			kw.clickDivByText("拔萃");
		} else if (putInSite == 1234567892) {// 财富
			kw.clickDivByText("财富");
		} else if (putInSite == 1234567893) {// 融资服务中心
			kw.clickDivByText("融资服务中心");
		} else if (putInSite == 1234567991) {// 数米财富
			kw.clickDivByText("数米财富");
		} else if (putInSite == 1234567992) {// 聚财富
			kw.clickDivByText("聚财富");
		} else if (putInSite == 1234567994) {// 118114理财
			kw.clickDivByText("118114理财");
		} else {
			flag = false;
			throw new Exception("投放站点参数[" + putInSite + "]错误!");
		}
		// 投放终端
		if (putInTerminal == 0) {// 全部
			kw.click(OrUtil.getBy("audit_ch1_xpath", ObjectLib.BMPObjectLib));
		} else if (putInTerminal == 1) {// 网站端
			kw.click(OrUtil.getBy("audit_filter.clientType-1_xpath",
					ObjectLib.BMPObjectLib));
		} else if (putInTerminal == 2) {// 微信WAP端
			kw.click(OrUtil.getBy("audit_filter.clientType-2_xpath",
					ObjectLib.BMPObjectLib));
		} else if (putInTerminal == 3) {// 手机APP端
			kw.click(OrUtil.getBy("audit_filter.clientType-3_xpath",
					ObjectLib.BMPObjectLib));
		} else {
			flag = false;
			throw new Exception("投放终端参数[" + putInTerminal + "]错误!");
		}
		// 是否可以提前还款
		if (isEarlyRepayValue == 0) {// 否
			kw.click(OrUtil.getBy("audit_fiPrjExt_isEarlyRepay0_xpath",
					ObjectLib.BMPObjectLib));
		} else if (isEarlyRepayValue == 1) {// 是
			kw.click(OrUtil.getBy("audit_fiPrjExt_isEarlyRepay1_xpath",
					ObjectLib.BMPObjectLib));
			// 可以提前还款天数
			kw.setValue(OrUtil.getBy("audit_earlyRepayDaysId_xpath",
					ObjectLib.BMPObjectLib), earlyRepayDays + "");
		}
		// 是否可以变现
		if (isTransferValue == 0) {// 否
			kw.click(OrUtil.getBy("audit_chooseTransfer0_xpath",
					ObjectLib.BMPObjectLib));
		} else if (isTransferValue == 1) {// 是
			kw.click(OrUtil.getBy("audit_chooseTransfer1_xpath",
					ObjectLib.BMPObjectLib));
		}
		// 合同编号
		kw.setValue(OrUtil.getBy("audit_protocolList[0].paramValue_xpath",
				ObjectLib.BMPObjectLib), contractNo);
		return flag;
	}

	/**
	 * xhh-251 点击通过该保理项目审核 前提:xhh-250 和 xhh-252 确认通过该保理项目审核 前提:xhh-251
	 * 
	 * @return
	 * @throws Exception
	 */
	public boolean submit() throws Exception {
		boolean flag = false;
		String js = "doAudit(2)";
		kw.executeJS(js);
		ThreadUtil.sleep();
		kw.acceptAlert();
		//
		String text = kw.getText(OrUtil.getBy("body_dataTable_xpath",
				ObjectLib.BMPObjectLib));
		System.out.println(text);
		if (!StringUtils.isEquals(text, "没有数据")) {
			flag = false;
			throw new Exception("确认通过该保理项目审核出错!");
		} else {
			flag = true;
		}
		return flag;
	}

	/**
	 * 读取项目审核时的项目信息
	 * 
	 * @param timeOut
	 *            用款期限 int
	 * @param dateType
	 *            用款期限单位 DateType
	 * @param repayway
	 *            还款方式 RepayWay
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, String> getProjectInfo(int timeOut,
			DateType dateType, RepayWay repayway) throws Exception {
		HashMap<String, String> hashMap = new HashMap<>();
		// 项目名称
		String js = "return $('#prjName').val()";
		String prjName = kw.executeJsReturnString(js);
		hashMap.put("prjName", prjName);
		// 项目编号
		js = "return $('#prjNo').text()";
		String prjNo = kw.executeJsReturnString(js);
		hashMap.put("prjNo", prjNo);
		// 产品类别
		js = "return $('#prjType').text()";
		String prjType = kw.executeJsReturnString(js);
		hashMap.put("prjType", prjType);
		// 业务类型
		String busiType = kw.getText(OrUtil.getBy("busi_xpath",
				ObjectLib.BMPObjectLib));
		busiType = StringUtils.format(busiType);
		hashMap.put("busiType", busiType);
		// 融资规模
		js = "return $('#demandAmount').val()";
		String demandAmount = kw.executeJsReturnString(js);
		hashMap.put("demandAmount", demandAmount);
		// 预期利率
		js = "return $('#yearRate').text()";
		String yearRate = kw.executeJsReturnString(js);
		yearRate = StringUtils.format(yearRate).split("年")[0];
		hashMap.put("yearRate", yearRate);
		// 期限
		String term = kw.getText(OrUtil.getBy("term_xpath",
				ObjectLib.BMPObjectLib));
		term = StringUtils.format(term).split("还款日期")[0];
		term = term.substring(0, term.length() - 2);
		hashMap.put("term", term);
		// 还款日期
		js = "return $('#repayDateSpan').text()";
		String repayDate = kw.executeJsReturnString(js);
		System.out.println(repayDate);
		repayDate = repayDate.split("还款日期：")[1];
		repayDate = repayDate.substring(0, repayDate.length() - 1);
		hashMap.put("repayDate", repayDate);
		// 还款方式
		String repayWay = kw.getText(OrUtil.getBy("RepayWay_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("repayWay", repayWay);
		// 是否新客项目
		boolean isNew = kw.isSelected(OrUtil.getBy("isNew1_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("isNew", isNew + "");
		// 是否鑫整点
		boolean isXzd = kw.isSelected(OrUtil.getBy("isXzd1_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("isXzd", isXzd + "");
		// 只有还款方式为：到期还本付息，才有是否展期
		if (repayway == RepayWay.E) {
			// 是否展期
			boolean isExtend = kw.isSelected(OrUtil.getBy("isExtend1_xpath",
					ObjectLib.BMPObjectLib));
			if (isExtend) {
				// 展期天数
				js = "return $('#extendTime').val()";
				String extendTime = kw.executeJsReturnString(js);
				hashMap.put("extendTime", extendTime);
			}
			hashMap.put("isExtend", isExtend + "");
		} else {
			System.out.println("还款方式非到期还本付息，不读取是否展期!");
		}
		// 融资发标时间
		js = "return $('#showBidTime').val()";
		String showBidTime = kw.executeJsReturnString(js);
		hashMap.put("showBidTime", showBidTime);
		// 融资开标时间
		js = "return $('#startBidTime').val()";
		String startBidTime = kw.executeJsReturnString(js);
		hashMap.put("startBidTime", startBidTime);
		// 融资截止时间
		js = "return $('#endBidTime').val()";
		String endBidTime = kw.executeJsReturnString(js);
		hashMap.put("endBidTime", endBidTime);
		// 起息日
		js = "return $(\"input[name='interestStartDate']\").val()";
		String interestStartDate = kw.executeJsReturnString(js);
		hashMap.put("interestStartDate", interestStartDate);
		// 投资起始金额
		js = "return $('#minBidAmountInput').val()";
		String minBidAmount = kw.executeJsReturnString(js);
		hashMap.put("minBidAmount", minBidAmount);
		// 最大投资金额
		js = "return $('#maxBidAmountInput').val()";
		String maxBidAmount = kw.executeJsReturnString(js);
		hashMap.put("maxBidAmount", maxBidAmount);
		// 投资递增金额
		js = "return $(\"input[name='fiPrj.stepBidAmount']\").val()";
		String stepBidAmount = kw.executeJsReturnString(js);
		hashMap.put("stepBidAmount", stepBidAmount);
		// 支付方式
		String payWay = kw.getAttribute(
				OrUtil.getBy("payWay_xpath", ObjectLib.BMPObjectLib), "value");
		hashMap.put("payWay", payWay);
		// 平台管理费率
		js = "return $('#platformFee').val()";
		String platformFee = kw.executeJsReturnString(js);
		hashMap.put("platformFee", platformFee + "%");
		// 保障方式
		String safeguardWay = kw.getText(OrUtil.getBy("safeWay_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("safeguardWay", safeguardWay);
		// 发布机构
		String releaseOrgan = kw.getText(OrUtil.getBy("releaseOrgan_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("releaseOrgan", releaseOrgan);
		// 投资资金转入账户-->户名
		String accountName = kw.getText(OrUtil.getBy("accountName_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("accountName", accountName);
		// 投资资金转入账户-->账号
		String account = kw.getText(OrUtil.getBy("account_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("account", account);
		// 投资资金转入账户-->开户行
		String accountAddress = kw.getText(OrUtil.getBy("accountAddress_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("accountAddress", accountAddress);
		// 是否共享标
		boolean isShare = kw.isSelected(OrUtil.getBy("isShare1_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("isShare", isShare + "");
		// 投放站点
		js = "return $(\"input[name='fiMember.miNo']\").val()";
		String miNo = kw.executeJsReturnString(js);
		if (StringUtils.isEquals(miNo, "1234567890")) {
			hashMap.put("miNo", "鑫合汇");
		} else {
			hashMap.put("miNo", miNo);
		}
		// 是否可以提前结束募集期
		boolean isEarlyClose = kw.isSelected(OrUtil.getBy(
				"isEarlyClose1_xpath", ObjectLib.BMPObjectLib));
		hashMap.put("isEarlyClose", isEarlyClose + "");
		// 募集期是否给利息
		boolean isHaveBidingIncoming = kw.isSelected(OrUtil.getBy(
				"isHaveBidingIncoming1_xpath", ObjectLib.BMPObjectLib));
		hashMap.put("isHaveBidingIncoming", isHaveBidingIncoming + "");
		// 只有<=90天的才可以提前还款
		// 只有>90天，且还款方式为到期还本付息的才可以变现
		if (dateType == DateType.day) {
			if (timeOut <= 90) {
				System.out.println("期限<=90天，读取是否可以提前还款!");
				// 是否可以提前还款
				boolean isEarlyRepay = kw.isSelected(OrUtil.getBy(
						"isEarlyRepay1_xpath", ObjectLib.BMPObjectLib));
				if (isEarlyRepay) {
					// 可以提前还款天数
					js = "return $('#earlyRepayDaysId').val()";
					String earlyRepayDays = kw.executeJsReturnString(js);
					hashMap.put("earlyRepayDays", earlyRepayDays);
				}
				hashMap.put("isEarlyRepay", isEarlyRepay + "");
			} else if (timeOut > 90 && repayway == RepayWay.E) {
				System.out.println("期限>90天，且还款方式为到期还本付息，读取是否可以变现!");
				// 是否可以变现
				boolean isTransfer = kw.isSelected(OrUtil.getBy(
						"isTransfer1_xpath", ObjectLib.BMPObjectLib));
				hashMap.put("isTransfer", isTransfer + "");
			} else {
				System.out.println("不读取是否可以提前还款及是否可以变现!");
			}
		} else if (dateType == DateType.month) {
			if (timeOut <= 3) {
				System.out.println("期限<=90天，读取是否可以提前还款!");
				// 是否可以提前还款
				boolean isEarlyRepay = kw.isSelected(OrUtil.getBy(
						"isEarlyRepay1_xpath", ObjectLib.BMPObjectLib));
				if (isEarlyRepay) {
					// 可以提前还款天数
					js = "return $('#earlyRepayDaysId').val()";
					String earlyRepayDays = kw.executeJsReturnString(js);
					hashMap.put("earlyRepayDays", earlyRepayDays);
				}
				hashMap.put("isEarlyRepay", isEarlyRepay + "");
			} else if (timeOut > 3 && repayway == RepayWay.E) {
				System.out.println("期限>90天，且还款方式为到期还本付息，读取是否可以变现!");
				// 是否可以变现
				boolean isTransfer = kw.isSelected(OrUtil.getBy(
						"isTransfer1_xpath", ObjectLib.BMPObjectLib));
				hashMap.put("isTransfer", isTransfer + "");
			} else {
				System.out.println("不读取是否可以提前还款及是否可以变现!");
			}
		} else if (dateType == DateType.year) {

		}
		// 是否允许多次投标
		boolean isMultiBuy = kw.isSelected(OrUtil.getBy("isMultiBuy1_xpath",
				ObjectLib.BMPObjectLib));
		hashMap.put("isMultiBuy", isMultiBuy + "");
		// 自动预约投标
		boolean isAppointPrj = kw.isSelected(OrUtil.getBy(
				"fiPrjExt.isAppointPrj1_xpath", ObjectLib.BMPObjectLib));
		if (isAppointPrj) {
			// 预约投标额占比
			js = "return $('#appointPrjRatioInput').val()";
			String appointPrjRatio = kw.executeJsReturnString(js);
			hashMap.put("appointPrjRatio", appointPrjRatio);
		}
		hashMap.put("isAppointPrj", isAppointPrj + "");
		// 合同编号
		js = "return $(\"input[name='protocolList[0].paramValue']\").val()";
		String protocolList0 = kw.executeJsReturnString(js);
		hashMap.put("protocolList0", protocolList0);
		// 原债权人与原债务人合同
		js = "return $(\"input[name='protocolList[1].paramValue']\").val()";
		String protocolList1 = kw.executeJsReturnString(js);
		hashMap.put("protocolList1", protocolList1);
		// 原债权人与原债务人合同合同编号
		js = "return $(\"input[name='protocolList[2].paramValue']\").val()";
		String protocolList2 = kw.executeJsReturnString(js);
		hashMap.put("protocolList2", protocolList2);
		// 应收账款总金额人民币大写
		js = "return $(\"input[name='protocolList[3].paramValue']\").val()";
		String protocolList3 = kw.executeJsReturnString(js);
		hashMap.put("protocolList3", protocolList3);
		// 应收账款总金额人民币小写
		js = "return $(\"input[name='protocolList[4].paramValue']\").val()";
		String protocolList4 = kw.executeJsReturnString(js);
		hashMap.put("protocolList4", protocolList4);
		// 国内保理合同编号
		js = "return $(\"input[name='protocolList[5].paramValue']\").val()";
		String protocolList5 = kw.executeJsReturnString(js);
		hashMap.put("protocolList5", protocolList5);
		// 原债权人姓名名称
		js = "return $(\"input[name='protocolList[6].paramValue']\").val()";
		String protocolList6 = kw.executeJsReturnString(js);
		hashMap.put("protocolList6", protocolList6);
		// 原债权人证件类型
		js = "return $(\"input[name='protocolList[7].paramValue']\").val()";
		String protocolList7 = kw.executeJsReturnString(js);
		hashMap.put("protocolList7", protocolList7);
		// 原债权人证件号码
		js = "return $(\"input[name='protocolList[8].paramValue']\").val()";
		String protocolList8 = kw.executeJsReturnString(js);
		hashMap.put("protocolList8", protocolList8);
		// 原债务人姓名名称
		js = "return $(\"input[name='protocolList[9].paramValue']\").val()";
		String protocolList9 = kw.executeJsReturnString(js);
		hashMap.put("protocolList9", protocolList9);
		// 原债务人证件类型
		js = "return $(\"input[name='protocolList[10].paramValue']\").val()";
		String protocolList10 = kw.executeJsReturnString(js);
		hashMap.put("protocolList10", protocolList10);
		// 原债务人证件号码
		js = "return $(\"input[name='protocolList[11].paramValue']\").val()";
		String protocolList11 = kw.executeJsReturnString(js);
		hashMap.put("protocolList11", protocolList11);
		// 还款日还款时间
		js = "return $(\"input[name='protocolList[12].paramValue']\").val()";
		String protocolList12 = kw.executeJsReturnString(js);
		hashMap.put("protocolList12", protocolList12);
		// 提前回购操作通知发送提前量
		js = "return $(\"input[name='protocolList[13].paramValue']\").val()";
		String protocolList13 = kw.executeJsReturnString(js);
		hashMap.put("protocolList13", protocolList13);
		// 募集期保证金比例
		js = "return $(\"input[name='protocolList[14].paramValue']\").val()";
		String protocolList14 = kw.executeJsReturnString(js);
		hashMap.put("protocolList14", protocolList14);
		// 风险保证金比例
		js = "return $(\"input[name='protocolList[15].paramValue']\").val()";
		String protocolList15 = kw.executeJsReturnString(js);
		hashMap.put("protocolList15", protocolList15);
		// 风险保证金第一次追加天数
		js = "return $(\"input[name='protocolList[16].paramValue']\").val()";
		String protocolList16 = kw.executeJsReturnString(js);
		hashMap.put("protocolList16", protocolList16);
		// 风险保证金第一次追加百分比
		js = "return $(\"input[name='protocolList[17].paramValue']\").val()";
		String protocolList17 = kw.executeJsReturnString(js);
		hashMap.put("protocolList17", protocolList17);
		// 风险保证金第二次追加天数
		js = "return $(\"input[name='protocolList[18].paramValue']\").val()";
		String protocolList18 = kw.executeJsReturnString(js);
		hashMap.put("protocolList18", protocolList18);
		// 风险保证金第二次追加百分比
		js = "return $(\"input[name='protocolList[19].paramValue']\").val()";
		String protocolList19 = kw.executeJsReturnString(js);
		hashMap.put("protocolList19", protocolList19);
		// 风险保证金第三次追加天数
		js = "return $(\"input[name='protocolList[20].paramValue']\").val()";
		String protocolList20 = kw.executeJsReturnString(js);
		hashMap.put("protocolList20", protocolList20);
		// 流标百分比
		js = "return $(\"input[name='protocolList[21].paramValue']\").val()";
		String protocolList21 = kw.executeJsReturnString(js);
		hashMap.put("protocolList21", protocolList21);
		return hashMap;
	}

	/**
	 * 将发布的项目信息写入数据库
	 * 
	 * @param hashMap
	 * @param repayWay
	 * @param timeOut
	 * @param dateType
	 * @param log
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public boolean writeDB(HashMap<String, String> hashMap, RepayWay repayWay,
			int timeOut, DateType dateType, Logger log, String id)
			throws Exception {
		boolean flag = false;
		// 遍历hashMap
		Iterator<Entry<String, String>> iter = hashMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
					.next();
			String key = entry.getKey();
			String value = entry.getValue();
			System.out.println(key + "-->" + value);
		}
		StringBuffer sb = new StringBuffer();
		// insert into sql
		sb.append("insert into productinfo (prj_name,prj_no,prj_type,busi_type,demand_amount,year_rate,term,repay_date,repay_way,is_new,is_xzd,is_extend,extend_time,show_bid_time,start_bid_time,min_bid_amount,end_bid_time,start_profit_date,max_bid_amount,step_bid_amount,pay_way,platform_fee,safe_guard_way,mi_no,is_early_close,is_haveBidingIncoming,is_early_repay,early_repay_days,is_transfer,is_multi_buy,is_appoint_prj,time,appoint_prj_ratio,protocol_list0,balance,progress,collect,profit_10thous,profit_start_date,account_name,borrower_name) values (");
		// 项目名称
		sb.append("'" + hashMap.get("prjName") + "',");
		// 项目编号
		sb.append("'" + hashMap.get("prjNo") + "',");
		// 产品类别
		sb.append("'" + hashMap.get("prjType") + "',");
		// 业务类型
		sb.append("'" + hashMap.get("busiType") + "',");
		// 融资规模
		sb.append("'"
				+ MathUtil.round(Double.parseDouble(hashMap.get("demandAmount")))
				+ "',");
		// 预期利率
		sb.append("'"
				+ MathUtil.round(Double.parseDouble(hashMap.get("yearRate")
						.replaceAll("%", ""))) + "%',");
		// 期限
		sb.append("'" + hashMap.get("term") + "',");
		// 还款日期
		sb.append("'" + hashMap.get("repayDate").split("，")[0] + "',");
		// 还款方式
		sb.append("'" + hashMap.get("repayWay") + "',");
		// 是否新客项目
		// sb.append("'" + hashMap.get("isNew") + "'");
		sb.append("'"
				+ (StringUtils.isEquals(hashMap.get("isNew"), "true") ? "是"
						: "否") + "',");
		// 是否鑫整点
		// sb.append("'" + hashMap.get("isXzd") + "'");
		sb.append("'"
				+ (StringUtils.isEquals(hashMap.get("isXzd"), "true") ? "是"
						: "否") + "',");
		// 是否展期
		// sb.append("'" + hashMap.get("isExtend") + "'");
		sb.append("'"
				+ (StringUtils.isEquals(hashMap.get("isExtend"), "true") ? "是"
						: "否") + "',");
		// 展期天数
		sb.append("'" + hashMap.get("extendTime") + "',");
		// 融资发标时间
		sb.append("'" + hashMap.get("showBidTime") + "',");
		// 融资开标时间
		sb.append("'" + hashMap.get("startBidTime") + "',");
		// 投资起始金额
		sb.append("'"
				+ MathUtil.round(Double.parseDouble(hashMap.get("minBidAmount")))
				+ "',");
		// 融资截止时间
		sb.append("'" + hashMap.get("endBidTime") + "',");
		// 起息日
		sb.append("'" + hashMap.get("interestStartDate") + "',");
		// 最大投资金额
		if (hashMap.get("maxBidAmount") != null
				&& hashMap.get("maxBidAmount").length() > 0) {
			sb.append("'"
					+ MathUtil.round(Double.parseDouble(hashMap
							.get("maxBidAmount"))) + "',");
		} else {
			sb.append("'" + "',");
		}
		// 投资递增金额
		if (hashMap.get("stepBidAmount") != null
				&& hashMap.get("stepBidAmount").length() > 0) {
			sb.append("'"
					+ MathUtil.round(Double.parseDouble(hashMap
							.get("stepBidAmount"))) + "',");
		} else {
			sb.append("'" + MathUtil.round(0.00) + "',");
		}
		// 支付方式
		sb.append("'" + hashMap.get("payWay") + "',");
		// 平台管理费率
		sb.append("'"
				+ MathUtil.round(Double.parseDouble(hashMap.get("platformFee")
						.replaceAll("%", ""))) + "%',");
		// 保障方式
		sb.append("'" + hashMap.get("safeguardWay") + "',");
		// 投放站点
		sb.append("'" + hashMap.get("miNo") + "',");
		// 是否可以提前结束募集期
		// sb.append("'" + hashMap.get("isEarlyClose") + "'");
		sb.append("'"
				+ (StringUtils.isEquals(hashMap.get("isEarlyClose"), "true") ? "是"
						: "否") + "',");
		// 募集期是否给利息
		// sb.append("'" + hashMap.get("isHaveBidingIncoming") + "'");
		sb.append("'"
				+ (StringUtils.isEquals(hashMap.get("isHaveBidingIncoming"),
						"true") ? "是" : "否") + "',");
		// 是否可以提前还款
		// sb.append("'" + hashMap.get("isEarlyRepay") + "'");
		sb.append("'"
				+ (StringUtils.isEquals(hashMap.get("isEarlyRepay"), "true") ? "是"
						: "否") + "',");
		// 可以提前还款天数
		sb.append("'" + hashMap.get("earlyRepayDays") + "',");
		// 是否可以变现
		// sb.append("'" + hashMap.get("isTransfer") + "'");
		sb.append("'"
				+ (StringUtils.isEquals(hashMap.get("isTransfer"), "true") ? "是"
						: "否") + "',");
		// 是否允许多次投标
		// sb.append("'" + hashMap.get("isMultiBuy") + "'");
		sb.append("'"
				+ (StringUtils.isEquals(hashMap.get("isMultiBuy"), "true") ? "是"
						: "否") + "',");
		// 自动预约投标(true:开启;false:关闭)
		// sb.append("'" + hashMap.get("isAppointPrj") + "'");
		sb.append("'"
				+ (StringUtils.isEquals(hashMap.get("isAppointPrj"), "true") ? "是"
						: "否") + "',");
		// 项目发布时间
		sb.append("'" + hashMap.get("time") + "',");
		// 预约投标额占比
		if (null != hashMap.get("appointPrjRatio")) {
			sb.append("'" + hashMap.get("appointPrjRatio") + "',");
		} else {
			sb.append("'" + 0 + "',");
		}
		// 合同编号
		sb.append("'" + hashMap.get("protocolList0") + "',");
		// 产品余额
		sb.append("'"
				+ MathUtil.round(Double.parseDouble(hashMap.get("demandAmount")))
				+ "',");
		// 产品进度
		sb.append("'" + 0 + "',");
		// 已募集金额
		sb.append("'" + MathUtil.round(0.00) + "',");
		// 万份收益
		/*
		 * sb.append("'" + MathUtil.round(ProfitCal.calProfit10Thous(
		 * Double.parseDouble(hashMap.get("yearRate").split("%")[0]) / 100,
		 * Integer.parseInt(StringUtils.getNum(hashMap.get("term"))),
		 * StringUtils.getStrDateType(hashMap.get("term")))) + "',");
		 */
		sb.append("'"
				+ MathUtil.round(ProfitCal.calProfit10Thous(
						Double.parseDouble(hashMap.get("yearRate").split("%")[0]) / 100,
						hashMap.get("endBidTime"), hashMap.get("repayDate")
								.split("，")[0])) + "',");
		// 收益起始日期 = 融资开标时间 + 起息日
		sb.append("'"
				+ DateUtils.simpleDateFormat2(DateUtils.dayAddDays(
						DateUtils.stringToDate(hashMap.get("startBidTime")),
						Integer.parseInt(hashMap.get("interestStartDate"))))
				+ "',");
		// 投资资金转入账户户名
		sb.append("'" + hashMap.get("accountName") + "',");
		// 借款人
		sb.append("'" + hashMap.get("borrowerName") + "'");
		// sql end
		sb.append(")");
		// execute sql
		System.out.println(sb);
		DbUtil.insert(sb.toString(), DbType.Local);
		// //////////////////////////////
		// //////////////////////////////
		// //////////////////////////////
		// 期数
		if (dateType == DateType.month && timeOut >= 12
				&& (repayWay == RepayWay.permonth || repayWay == RepayWay.D)) {
			for (int i = 1; i <= timeOut; i++) {
				System.out.println(i);
				if (i == 1) {
					String sql = "insert into product_repay_plan (name,code,stage,repay_date,status) values ('"
							+ hashMap.get("prjName")
							+ "','"
							+ hashMap.get("prjNo")
							+ "','募集期','"
							+ hashMap.get("repayDate").split("，")[0] + "','0')";
					System.out.println(sql);
					DbUtil.insert(sql, DbType.Local);
				}
				if (repayWay == RepayWay.permonth) {
					StringBuffer per_sql = new StringBuffer();
					per_sql.append("insert into product_repay_plan (name,code,stage,capital,profit,total_money,remain_capital,repay_date,status) values (");
					// 产品名称
					per_sql.append("'" + hashMap.get("prjName") + "',");
					// 产品编号
					per_sql.append("'" + hashMap.get("prjNo") + "',");
					// 期数
					per_sql.append(i + ",");
					// 应收本息 = 应收本金 + 应收利息
					double total;
					if (i == timeOut) {
						// 计算倒数第二期应收本息
						double total2 = ProfitCal.getRepayPerM(Double
								.parseDouble(hashMap.get("yearRate")
										.replaceAll("%", "")) / 100, Double
								.parseDouble(hashMap.get("demandAmount")),
								timeOut - 1, Integer.parseInt(hashMap
										.get("term").replaceAll("月", "")
										.replaceAll("天", "")), dateType);
						// 计算最后一期的应收本息
						List<Double> list = ProfitCal
								.getLastStageTotalMoney(
										Double.parseDouble(hashMap
												.get("demandAmount")),
										Double.parseDouble(hashMap.get(
												"yearRate").replaceAll("%", "")) / 100 / 12,
										total2, timeOut);
						double lixi = list.get(0);
						double benjin = list.get(1);
						double benxi = list.get(2);
						// 应收本金
						per_sql.append("'" + MathUtil.round(benjin) + "',");
						// 应收利息
						per_sql.append("'" + MathUtil.round(lixi) + "',");
						// 应收本息
						per_sql.append("'" + MathUtil.round(benxi) + "',");
						// 剩余本金
						per_sql.append("'0.00',");
						// 还款日
						per_sql.append("'"
								+ hashMap.get("repayDate").split("，")[0] + "',");
						// 还款状态
						per_sql.append("0");
						per_sql.append(")");
						System.out.println(per_sql);
						DbUtil.insert(per_sql.toString(), DbType.Local);
						continue;
					} else {
						// 计算本期应收本息（除最后一期外）
						total = ProfitCal.getRepayPerM(Double
								.parseDouble(hashMap.get("yearRate")
										.replaceAll("%", "")) / 100, Double
								.parseDouble(hashMap.get("demandAmount")), i,
								Integer.parseInt(hashMap.get("term")
										.replaceAll("月", "")
										.replaceAll("天", "")), dateType);

					}
					List<Double> list = ProfitCal.calForEqualMonthly(Double
							.parseDouble(hashMap.get("demandAmount")), Double
							.parseDouble(hashMap.get("yearRate").replaceAll(
									"%", "")) / 100 / 12, i, total);
					double lixi = list.get(0);
					double benjin = list.get(1);
					double shengyu = list.get(2);
					// 应收本金
					per_sql.append("'" + MathUtil.round(benjin) + "',");
					// 应收利息
					per_sql.append("'" + MathUtil.round(lixi) + "',");
					// 应收本息
					per_sql.append("'" + MathUtil.round(total) + "',");
					// 剩余本金
					per_sql.append("'" + MathUtil.round(shengyu) + "',");
					// 还款日
					String repayDate = hashMap.get("repayDate").split("，")[0];
					repayDate = DateUtils.simpleDateFormat2(DateUtils
							.dayAddMonth(DateUtils.stringToDate2(repayDate),
									-(timeOut - i)));
					per_sql.append("'" + repayDate + "',");
					// 还款状态
					per_sql.append("0");
					per_sql.append(")");
					System.out.println(per_sql);
					DbUtil.insert(per_sql.toString(), DbType.Local);
				} else if (repayWay == RepayWay.D) {
					StringBuffer d_sql = new StringBuffer();
					d_sql.append("insert into product_repay_plan (name,code,stage,capital,profit,total_money,remain_capital,repay_date,status) values (");
					// 产品名称
					d_sql.append("'" + hashMap.get("prjName") + "',");
					// 产品编号
					d_sql.append("'" + hashMap.get("prjNo") + "',");
					// 期数
					d_sql.append(i + ",");
					// 应收本金
					double benjin = 0.00;
					if (i == timeOut) {
						benjin = MathUtil
								.roundD(Double.parseDouble(hashMap
										.get("demandAmount"))).doubleValue();
					}
					d_sql.append("'" + MathUtil.round(benjin) + "',");
					// 应收利息
					/*
					 * double ddd = ProfitCal.calBaseProfit(
					 * Double.parseDouble(hashMap.get("demandAmount")), timeOut,
					 * dateType,
					 * Double.parseDouble(hashMap.get("yearRate").split(
					 * "%")[0]) / 100);
					 */
					double ddd = ProfitCal.calBaseProfit(
							Double.parseDouble(hashMap.get("demandAmount")),
							Double.parseDouble(hashMap.get("yearRate").split(
									"%")[0]) / 100, hashMap.get("endBidTime"),
							hashMap.get("repayDate").split("，")[0]);
					double lixi = MathUtil.retain2Decimal(ddd / 12);
					System.out.println(lixi);
					System.out.println(ddd / 12);
					d_sql.append("'" + lixi + "',");
					// 应收本息 = 应收本金 + 应收利息
					double benxi = benjin + lixi;
					d_sql.append("'" + MathUtil.round(benxi) + "',");
					// 剩余本金
					if (i == timeOut) {
						d_sql.append("'0.00',");
					} else {
						d_sql.append("'"
								+ MathUtil.round(Double.parseDouble(hashMap
										.get("demandAmount"))) + "',");
					}
					// 还款日
					String repayDate = hashMap.get("repayDate").split("，")[0];
					repayDate = DateUtils.simpleDateFormat2(DateUtils
							.dayAddMonth(DateUtils.stringToDate2(repayDate),
									-(timeOut - i)));
					d_sql.append("'" + repayDate + "',");
					// 还款状态
					d_sql.append("0");
					d_sql.append(")");
					System.out.println(d_sql);
					DbUtil.insert(d_sql.toString(), DbType.Local);
				}
			}
		} else {
			String tepSQL = "insert into product_repay_plan (name,code,stage,repay_date,status) values ('"
					+ hashMap.get("prjName")
					+ "','"
					+ hashMap.get("prjNo")
					+ "','募集期','"
					+ hashMap.get("repayDate").split("，")[0]
					+ "','0')";
			System.out.println(tepSQL);
			DbUtil.insert(tepSQL, DbType.Local);
			//
			StringBuffer sql = new StringBuffer();
			sql.append("insert into product_repay_plan (name,code,stage,capital,profit,total_money,remain_capital,repay_date,status) values (");
			// 产品名称
			sql.append("'" + hashMap.get("prjName") + "',");
			// 产品编号
			sql.append("'" + hashMap.get("prjNo") + "',");
			// 期数
			sql.append("'1',");
			// 应收本金
			sql.append("'"
					+ MathUtil.round(Double.parseDouble(hashMap
							.get("demandAmount"))) + "',");
			// 应收利息
			// double ddd = ProfitCal
			// .calBaseProfit(
			// Double.parseDouble(hashMap.get("demandAmount")),
			// timeOut,
			// dateType,
			// Double.parseDouble(hashMap.get("yearRate").split(
			// "%")[0]) / 100);
			double ddd = ProfitCal
					.calBaseProfit(
							Double.parseDouble(hashMap.get("demandAmount")),
							Double.parseDouble(hashMap.get("yearRate").split(
									"%")[0]) / 100, hashMap.get("endBidTime"),
							hashMap.get("repayDate").split("，")[0]);
			System.out.println(ddd);
			sql.append("'" + MathUtil.round(ddd) + "',");
			// 应收本息 = 应收本金 + 应收利息
			sql.append("'"
					+ MathUtil.round(Double.parseDouble(hashMap
							.get("demandAmount")) + ddd) + "',");
			// 剩余本金
			sql.append("'0.00',");
			// 还款日
			sql.append("'" + hashMap.get("repayDate").split("，")[0] + "',");
			// 还款状态
			sql.append("0");
			sql.append(")");
			System.out.println(sql);
			DbUtil.insert(sql.toString(), DbType.Local);
		}

		// ////////////////////////////////
		// ////////////////////////////////
		// ////////////////////////////////
		StringBuffer sql = new StringBuffer();
		sql.append("select * from product_temp where product_id = '");
		sql.append(id);
		sql.append("'");
		System.out.println(sql);
		Map<String, String> map = DbUtil.querySingleData(sql.toString(),
				DbType.Local);
		if (map == null || map.size() <= 0) {
			sql = new StringBuffer();
			sql.append("insert into product_temp (product_id,product_name,product_no,time) values (");
			sql.append("'" + id + "',");
			sql.append("'" + hashMap.get("prjName") + "',");
			sql.append("'" + hashMap.get("prjNo") + "',");
			sql.append("'" + hashMap.get("time") + "'");
			sql.append(")");
			System.out.println(sql);
			DbUtil.insert(sql.toString(), DbType.Local);
		} else {
			sql = new StringBuffer();
			// sql.append("update product_temp set product_name='',product_no='',time='' where product_id=''");
			sql.append("update product_temp set product_name='");
			sql.append(hashMap.get("prjName") + "',");
			sql.append("product_no='" + hashMap.get("prjNo") + "',");
			sql.append("time='" + hashMap.get("time") + "'");
			sql.append(" where product_id='");
			sql.append(id + "'");
			System.out.println(sql);
			DbUtil.update(sql.toString(), DbType.Local);
		}
		flag = true;
		return flag;
	}
}
