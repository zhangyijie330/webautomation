package com.autotest.service.bmpService;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.RepayPlanDao;
import com.autotest.dao.UserDao;
import com.autotest.driver.KeyWords;
import com.autotest.enums.DateType;
import com.autotest.or.ObjectLib;
import com.autotest.service.xhhService.InvestSDTService2;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.ProfitCal;
import com.autotest.utility.ThreadUtil;

/**
 * 
 * 项目运营-->项目生成还款计划
 * 
 * @author wb0004
 * 
 */

public class GeneratePaymentPlan {

	public KeyWords	kw	= null;
	private Logger	log	= null;
	DecimalFormat	fm	= new DecimalFormat("###,##0.00");
	DecimalFormat	fm2	= new DecimalFormat("##0.00");

	public GeneratePaymentPlan(KeyWords kw, Logger log) {
		this.kw = kw;
		this.log = log;
	}

	/**
	 * xhh-383 访问【项目运营-保理发布】页面 前提：xhh-381 步骤：
	 * 1.点击左侧菜单栏的【项目运营】，在下拉菜单中点击【项目生成还款计划】菜单 期望的结果： 展示生成还款计划页面
	 * 
	 * @return 访问【项目运营-项目生成还款计划】页面实际结果。false:失败；true:成功
	 * @throws Exception
	 */
	public boolean enterGeneratePaymentPlan() throws Exception {
		boolean result = false;
		// 点击左侧菜单栏的【项目运营】
		kw.click(OrUtil.getBy("index_projectOperation_xpath",
				ObjectLib.BMPObjectLib));
		// 点击【项目生成还款计划】菜单
		kw.click(OrUtil.getBy("index_GeneratePaymentPlan_xpath",
				ObjectLib.BMPObjectLib));
		kw.waitPageLoad();
		ThreadUtil.sleep();
		kw.switchToFrame("work_frame");
		if (kw.isElementExist(OrUtil.getBy("search_btn_xpath",
				ObjectLib.BMPObjectLib))) {
			result = true;
		}
		return result;
	}

	/**
	 * xhh-384 筛选指定项目并校验值 前提：xhh-383 步骤： 1.在【项目名称】中输入指定项目，点击【查询】按钮 期望的结果：
	 * 1.展示筛选出的项目
	 * 
	 * @return false:失败；true:成功
	 * @throws Exception
	 */
	public boolean checkGeneratePaymentPlan(String projectName)
			throws Exception {
		boolean result = false;
		// 查询记录条数
		String projectNum = kw.getText(OrUtil.getBy("projectNum_xpath",
				ObjectLib.BMPObjectLib));
		String projectNum2 = "";
		// 输入指定项目名称并点击查询
		kw.setValue(OrUtil.getBy("search_projectName_input_id",
				ObjectLib.BMPObjectLib), projectName);
		// kw.click(OrUtil.getBy("search_btn_xpath", ObjectLib.BMPObjectLib));
		kw.executeJS("doSearchForPayWait()");
		// 比较页面右下角记录条数是否变化
		for (int i = 0; i < 10; i++) {
			ThreadUtil.sleep();
			projectNum2 = kw.getText(OrUtil.getBy("projectNum_xpath",
					ObjectLib.BMPObjectLib));
			if (!(projectNum2.equals(projectNum))) {
				break;
			}
		}
		// 校验生成还款计划按钮是否存在
		if (kw.isElementExist(OrUtil.getBy("generatePaymentPlan_btn_xpath",
				ObjectLib.BMPObjectLib))) {
			result = true;
		}
		return result;
	}

	/**
	 * 获得项目名称
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getProjectName(KeyWords kw) throws Exception {
		String projectName = kw.getText(OrUtil.getBy("projectName_xpath",
				ObjectLib.BMPObjectLib));
		return projectName;
	}

	/**
	 * 获得 业务类型
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getBusinessType(KeyWords kw) throws Exception {
		String businessType = kw.getText(OrUtil.getBy("businessType_xpath",
				ObjectLib.BMPObjectLib));
		return businessType;
	}

	/**
	 * 获得实际借款人
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getActualBorrower(KeyWords kw) throws Exception {
		String actualBorrower = kw.getText(OrUtil.getBy("actualBorrower_xpath",
				ObjectLib.BMPObjectLib));
		return actualBorrower;
	}

	/**
	 * 获得转入方账户
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getTransferInAccount(KeyWords kw) throws Exception {
		String transferInAccount = kw.getText(OrUtil.getBy(
				"transferInAccount_xpath", ObjectLib.BMPObjectLib));
		return transferInAccount;
	}

	/**
	 * 获得融资规模
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getFinancingScale(KeyWords kw) throws Exception {
		String financingScale = kw.getText(OrUtil.getBy("financingScale_xpath",
				ObjectLib.BMPObjectLib));
		return financingScale;
	}

	/**
	 * 获得线上实际融资
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getActualFinancingOnline(KeyWords kw) throws Exception {
		String actualFinancingOnline = kw.getText(OrUtil.getBy(
				"actualFinancingOnline_xpath", ObjectLib.BMPObjectLib));
		return actualFinancingOnline;
	}

	/**
	 * 获得是否提前结束融资
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getWhetherEndFinancingInTime(KeyWords kw) throws Exception {
		String whetherEndFinancingInTime = kw.getText(OrUtil.getBy(
				"whetherEndFinancingInTime_xpath", ObjectLib.BMPObjectLib));
		return whetherEndFinancingInTime;
	}

	/**
	 * 获得查看子标
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getCheckChildDoc(KeyWords kw) throws Exception {
		String checkChildDoc = kw.getText(OrUtil.getBy("checkChildDoc_xpath",
				ObjectLib.BMPObjectLib));
		return checkChildDoc;
	}

	/**
	 * 获得主合同签约进度
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getContractSchedule(KeyWords kw) throws Exception {
		String contractSchedule = kw.getText(OrUtil.getBy(
				"contractSchedule_xpath", ObjectLib.BMPObjectLib));
		return contractSchedule;
	}

	/**
	 * 获得确认书签约进度
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getConfirmSchedule(KeyWords kw) throws Exception {
		String confirmSchedule = kw.getText(OrUtil.getBy(
				"confirmSchedule_xpath", ObjectLib.BMPObjectLib));
		return confirmSchedule;
	}

	/**
	 * 获得平台管理费
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getPlatformfee(KeyWords kw) throws Exception {
		String platformfee = kw.getText(OrUtil.getBy("platformfee_xpath",
				ObjectLib.BMPObjectLib));
		return platformfee;
	}

	/**
	 * 获得保证金/融资管理费
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getGuaranteefee(KeyWords kw) throws Exception {
		String guaranteefee = kw.getText(OrUtil.getBy("guaranteefee_xpath",
				ObjectLib.BMPObjectLib));
		return guaranteefee;
	}

	/**
	 * 获得需付额度
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getLimitForPay(KeyWords kw) throws Exception {
		String limitForPay = kw.getText(OrUtil.getBy("limitForPay_xpath",
				ObjectLib.BMPObjectLib));
		return limitForPay;
	}

	/**
	 * 获得投放站点
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getLauchSite(KeyWords kw) throws Exception {
		String lauchSite = kw.getText(OrUtil.getBy("lauchSite_xpath",
				ObjectLib.BMPObjectLib));
		return lauchSite;
	}

	/**
	 * 获得收益
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */
	public String getEarning(KeyWords kw) throws Exception {
		String earning = kw.getText(OrUtil.getBy("earning_xpath",
				ObjectLib.BMPObjectLib));
		return earning;
	}

	/**
	 * 将页面数据存入Hashmap
	 * 
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getMapFromPage() throws Exception {
		Map<String, String> mapFromPage = new HashMap<String, String>();
		mapFromPage.put("项目名称", getProjectName(kw));
		mapFromPage.put("业务类型", getBusinessType(kw));
		mapFromPage.put("实际借款人", getActualBorrower(kw));
		mapFromPage.put("转入方账户", getTransferInAccount(kw));
		mapFromPage.put("融资规模", getFinancingScale(kw));
		mapFromPage.put("线上实际融资", getActualFinancingOnline(kw));
		mapFromPage.put("是否提前结束融资", getWhetherEndFinancingInTime(kw));
		mapFromPage.put("查看子标", getCheckChildDoc(kw));
		mapFromPage.put("主合同签约进度", getContractSchedule(kw));
		mapFromPage.put("确认书签约进度", getConfirmSchedule(kw));
		mapFromPage.put("平台管理费", getPlatformfee(kw));
		mapFromPage.put("保证金/融资管理费", getGuaranteefee(kw));
		mapFromPage.put("需付额度", getLimitForPay(kw));
		mapFromPage.put("投放站点", getLauchSite(kw));
		mapFromPage.put("收益", getEarning(kw));

		return mapFromPage;
	}

	/**
	 * 将数据库数据存入Hashmap
	 * 
	 * @param productcode
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getMapFromDB(String productcode)
			throws Exception {
		Map<String, String> mapFromDB = ProductDao.getProByCode(productcode);
		return mapFromDB;
	}

	/**
	 * xhh-385 针对指定项目校验各项数据 前提：xhh-384 步骤： 1.从数据库中读取指定项目的数据与页面数据进行对比 期望的结果：
	 * 1.数据库中数据与页面值相等
	 * 
	 * @return false:失败；true:成功
	 * @throws Exception
	 */
	public boolean checkGeneratePaymentPlanInfo(String productcode,
			Map<String, String> mapFromPage, Map<String, String> mapFromDB)
			throws Exception {
		boolean result = false;

		int count = InvestRecordDao.getCountByCode(productcode);
		String temp = mapFromDB.get("prj_name") + "(订单数:0/" + count + ")";
		if (mapFromPage.get("项目名称").equals(temp)) {
			result = true;
			log.info("【项目名称】校验成功");
		} else {
			result = false;
			log.error("【项目名称】校验失败");
		}

		if (mapFromPage.get("业务类型").equals(mapFromDB.get("busi_type"))) {
			result = result && true;
			log.info("【业务类型】校验成功");
		} else {
			result = false;
			log.error("【业务类型】校验失败");
		}

		if (mapFromPage.get("实际借款人").equals(mapFromDB.get("borrower_name"))) {
			result = result && true;
			log.info("【实际借款人】校验成功");
		} else {
			result = false;
			log.error("【实际借款人】校验失败");
		}

		if (mapFromPage.get("转入方账户").equals(mapFromDB.get("account_name"))) {
			result = result && true;
			log.info("【转入方账户】校验成功");
		} else {
			result = false;
			log.error("【转入方账户】校验失败");
		}
		if (mapFromPage.get("融资规模").replaceAll(",", "")
				.equals(mapFromDB.get("demand_amount"))) {
			result = result && true;
			log.info("【融资规模】校验成功");
		} else {
			result = false;
			log.error("【融资规模】校验失败");
		}

		// 用户投资之和与页面比较并写入productinfo
		double sum = InvestRecordDao.getAmountByCode(productcode);
		double sumPage = Double.parseDouble(mapFromPage.get("线上实际融资")
				.replaceAll(",", ""));
		if (sum == sumPage) {
			log.info("【线上实际融资】校验成功");
			result = result && true;
			ProductDao.updateCollect(productcode, fm2.format(sum));
			log.info("项目投资总额写入成功");
		} else {
			result = false;
			log.error("【线上实际融资】校验失败");
		}

		if (mapFromPage.get("是否提前结束融资").equals(mapFromDB.get("is_early_close"))) {
			result = result && true;
			log.info("【是否提前结束融资】校验成功");
		} else {
			result = false;
			log.error("【是否提前结束融资】校验失败");
		}

		// 数据库中没有子标相关字段
		if (mapFromPage.get("查看子标").equals("无子标")) {
			result = result && true;
			log.info("【查看子标】校验成功");
		} else {
			result = false;
			log.error("【查看子标】校验失败");
		}

		// 数据库中没有主合同签约进度相关字段
		if (mapFromPage.get("主合同签约进度").equals("-")
				|| mapFromPage.get("主合同签约进度").equals("主合同签章关闭")) {
			result = result && true;
			log.info("【主合同签约进度】校验成功");
		} else {
			result = false;
			log.error("【主合同签约进度】校验失败");
		}

		// 数据库中缺少确认书签约进度相关字段
		if (mapFromPage.get("确认书签约进度").equals("-")
				|| mapFromPage.get("确认书签约进度").equals("电子签章关闭")) {
			result = result && true;
			log.info("【确认书签约进度】校验成功");
		} else {
			result = false;
			log.error("【确认书签约进度】校验失败");
		}

		// 平台管理费设置为0.00
		if (mapFromPage.get("平台管理费").equals("0.00")) {
			result = result && true;
			log.info("【是否提前结束融资】校验成功");
		} else {
			result = false;
			log.error("【是否提前结束融资】校验失败");
		}

		// 保证金/融资管理费设置为0.00
		if (mapFromPage.get("保证金/融资管理费").equals("0.00")) {
			result = result && true;
			log.info("【保证金/融资管理费】校验成功");
		} else {
			result = false;
			log.error("【保证金/融资管理费】校验失败");
		}

		// 管理费暂时为0.00，故需付额度=融资额
		if (Double.parseDouble(mapFromPage.get("需付额度").replaceAll(",", "")) == Double
				.parseDouble((mapFromDB.get("collect")))) {
			result = result && true;
			log.info("【需付额度】校验成功");
		} else {
			result = false;
			log.error("【需付额度】校验失败");
		}

		if (mapFromPage.get("投放站点").equals(mapFromDB.get("mi_no"))) {
			result = result && true;
			log.info("【投放站点】校验成功");
		} else {
			result = false;
			log.error("【投放站点】校验失败");
		}

		// 收益设置为0.00，点击按钮后再校验回写
		if (mapFromPage.get("收益").equals("0.00")) {
			result = result && true;
			log.info("【收益】校验成功");
		} else {
			result = false;
			log.error("【收益】校验失败");
		}

		return result;
	}

	/**
	 * xhh-462 点击生成还款计划 xhh-463 确认生成还款计划 xhh-464 确认还款计划生成结果 前提：xhh-385 步骤：
	 * 1.点击【生成还款计划】按钮 期望的结果： 1.【生成还款计划】按钮变为 “-” 2.收益等于期望值
	 * 
	 * @return false:失败；true:成功
	 * @throws Exception
	 */
	public boolean clickGeneratePaymentPlan(String productcode)
			throws Exception {
		boolean result = false;
		// 点击【生成还款计划】按钮
		kw.click(OrUtil.getBy("generatePaymentPlan_btn_xpath",
				ObjectLib.BMPObjectLib));
		kw.waitPageLoad();
		// 等待
		ThreadUtil.sleep();

		/*
		 * for(int timer = 0; timer < 5; timer++){
		 * if(kw.isElementExist(OrUtil.getBy("wait_dialog_xpath",
		 * ObjectLib.BMPObjectLib))){ ThreadUtil.sleep(); //等待超过5s，返回失败 if(timer
		 * > 5 ){ return result; } } else{ return result; } }
		 */

		// 检查确认框是否存在
		if (kw.isElementExist(OrUtil.getBy("confirm_dialog_xpath",
				ObjectLib.BMPObjectLib))) {
			// 确认生成还款计划
			// 点击确认按钮
			kw.click(OrUtil.getBy("confirm_btn_xpath", ObjectLib.BMPObjectLib));
			// 校验【生成还款计划】按钮是否变化
			ThreadUtil.sleep(10);
			if (kw.isElementExist(OrUtil.getBy("success_confirm_btn_xpath",
					ObjectLib.BMPObjectLib))) {
				// 点击生成成功确认按钮
				kw.click(OrUtil.getBy("success_confirm_btn_xpath",
						ObjectLib.BMPObjectLib));
			}
			ThreadUtil.sleep(4);
			// 确认还款计划生成结果
			// 检查收益变化是否符合预期
			String mark_generatePaymentPlan = kw.getText2(OrUtil.getBy(
					"generatePaymentPlan_btn_afterclick_xpath",
					ObjectLib.BMPObjectLib));
			String mark_earning = kw.getText(OrUtil.getBy("earning_xpath",
					ObjectLib.BMPObjectLib));
			double earning = RepayPlanDao.getProfitByCode(productcode);
			double tmp = earning
					- Double.parseDouble(mark_earning.replaceAll(",", ""));
			if (mark_generatePaymentPlan.equals("-") && tmp < 0.01) {
				result = true;
			} else {
				log.info("预期标记：-");
				log.info("预期差值：0");
				log.error("实际标记：" + mark_generatePaymentPlan);
				log.error("实际差值：" + tmp);
			}
		}
		return result;
	}

	/**
	 * 
	 * 向项目还款计划表里插入项目还款计划数据
	 * 
	 * @param productcode
	 * @return false:失败；true:成功
	 * @throws Exception
	 */
	public boolean updateProductRepayPlan(String productcode) throws Exception {
		boolean result = false;
		// 查询productinfo表得到指定编号产品的信息
		Map<String, String> product = ProductDao.getProByCode(productcode);
		// 查询product_repay_plan表得到指定编号的产品的还款计划
		List<Map<String, String>> productPlan = RepayPlanDao
				.getPlanByCode(productcode);
		String capital = "";
		double pro = 0.00;
		String profit = "";
		String total_money = "";
		String remain_capital = "";
		String repay_date = productPlan.get(0).get("repay_date");

		// 更新product_repay_plan里的产品字段信息包含profit、capital、remain_capital、repay_date
		String stage = "募集期";
		// if条件用来筛选项目是否分期
		if (product.get("repay_way").equals("每月等额本息")
				|| product.get("repay_way").equals("按月付息，到期还本")) {
			// 更新募集期product_repay_plan里的产品字段信息，包含profit、capital、remain_capital、repay_date
			pro = RepayPlanDao.getProfitByCode(productcode, stage);
			profit = fm2.format(pro);
			capital = fm2.format(RepayPlanDao.getCapitalByCode(productcode,
					stage));
			total_money = fm2.format(pro);
			remain_capital = fm2.format(RepayPlanDao.getRemainByCode(
					productcode, stage));
			RepayPlanDao.updateInfo(capital, profit, total_money,
					remain_capital, productcode, stage, repay_date);
			// 更新各个分期product_repay_plan里的产品字段信息
			for (int j = 1; j <= 12; j++) {
				stage = "第" + j + "期";
				capital = fm2.format(RepayPlanDao.getCapitalByCode(productcode,
						stage));
				pro = RepayPlanDao.getProfitByCode(productcode, stage);
				profit = fm2.format(pro);
				total_money = fm2.format(Double.parseDouble(capital) + pro);
				remain_capital = fm2.format(RepayPlanDao.getRemainByCode(
						productcode, stage));
				stage = String.valueOf(j);
				RepayPlanDao.updateInfo(capital, profit, total_money,
						remain_capital, productcode, stage, repay_date);
			}
		} else {
			// 更新不分期项目的募集期和第1期product_repay_plan里的产品字段信息
			pro = RepayPlanDao.getProfitByCode(productcode, stage);
			profit = fm2.format(pro);
			capital = fm2.format(RepayPlanDao.getCapitalByCode(productcode,
					stage));
			remain_capital = fm2.format(RepayPlanDao.getRemainByCode(
					productcode, stage));
			total_money = fm2.format(Double.parseDouble(product.get("collect"))
					+ pro);
			// 更新项目还款计划募集期收益
			RepayPlanDao.updateInfo(capital, profit, total_money,
					remain_capital, productcode, stage, repay_date);
			stage = "第1期";
			pro = RepayPlanDao.getProfitByCode(productcode, stage);
			stage = "1";
			profit = fm2.format(pro);
			total_money = fm2.format(Double.parseDouble(product.get("collect"))
					+ pro);
			// 更新项目还款计划第一期收益
			RepayPlanDao.updateProfit(profit, total_money, productcode, stage);
		}
		result = true;
		return result;
	}

	/**
	 * 
	 * 向用户还款计划表里插入用户还款计划数据
	 * 
	 * @param productcode
	 * @return false:失败；true:成功
	 * @throws Exception
	 */
	public boolean updateUserRepayPlan(String productcode) throws Exception {
		boolean result = false;
		String uid = "";
		String bus_uid = "";
		// 查询productinfo表得到指定编号产品的信息
		Map<String, String> product = ProductDao.getProByCode(productcode);
		// 查询product_repay_plan表得到指定编号的产品的还款计划
		List<Map<String, String>> productPlan = RepayPlanDao
				.getPlanByCode(productcode);
		// 从product_repay_plan取得项目相关信息存到对应变量中
		String name = product.get("prj_name");
		String code = product.get("prj_no");
		String capital = "";
		double profit = 0.00;
		String remain_capital = "0.00";
		String status = "0";
		String total_money = "";
		String stage = "";
		String invest_amount = "";
		String end_bid_time = product.get("end_bid_time");
		String repay_date = productPlan.get(0).get("repay_date");
		// System.out.println(repay_date);
		DateType datetype;
		char type = product.get("term")
				.charAt(product.get("term").length() - 1);
		if (type == '天') {
			datetype = DateType.day;
		} else if (type == '月') {
			datetype = DateType.month;
		} else {
			datetype = DateType.year;
		}
		// 查询invest_record里指定项目的投资信息
		List<Map<String, String>> list = InvestRecordDao
				.getAllRecordByCode(productcode);
		// 遍历所有投资指定项目的用户，查询投资信息，写入user_repay_plan表
		for (int i = 0; i < list.size(); i++) {
			uid = list.get(i).get("uid");
			bus_uid = "u" + UserDao.getUidById(uid);
			// 根据用户uid和项目名称查询invest_record表某用户投资指定项目的信息
			Map<String, String> invest = InvestRecordDao.getRecordByUserCode(
					uid, productcode);
			capital = invest.get("invest_amount");
			invest_amount = invest.get("invest_amount");
			// 计算用户投资某项目的募集期利息，并写入user_repay_plan表
			profit = ProfitCal
					.calCollectProfit2(
							invest.get("invest_date"),
							product.get("end_bid_time"),
							Double.parseDouble(capital),
							Double.parseDouble(product.get("year_rate").split(
									"%")[0]) / 100, log);
			total_money = fm2.format(profit);
			stage = "募集期";
			repay_date = productPlan.get(0).get("repay_date");
			// System.out.println(repay_date);
			RepayPlanDao.insertIntoUserPayPlan(uid, bus_uid, name, code, stage,
					invest_amount, capital, fm2.format(profit), total_money,
					remain_capital, repay_date, status);
			// if用来判断每月等额本息，按月付息，到期支付本息三种不同情况，分别计算每期收益，写入user_repay_plan表
			if (product.get("repay_way").equals("每月等额本息")) {
				for (int j = 1; j <= 12; j++) {
					stage = "第" + j + "期";
					// if用来判断是否为第12期，利息计算方式和前11期不同
					if (j != 12) {
						total_money = fm2.format(ProfitCal.getRepayPerM(Double
								.parseDouble(product.get("year_rate")
										.replaceAll("%", "")) / 100, Double
								.parseDouble(list.get(i).get("invest_amount")),
								j, Integer.parseInt(product.get("term")
										.replaceAll("月", "")
										.replaceAll("天", "")), datetype));
						List<Double> tmplist = ProfitCal.calForEqualMonthly(
								Double.parseDouble(list.get(i).get(
										"invest_amount")),
								Double.parseDouble(product.get("year_rate")
										.replaceAll("%", "")) / 100 / 12, j,
								Double.parseDouble(total_money));
						profit = tmplist.get(0);
						capital = fm2.format(tmplist.get(1));
						remain_capital = fm2.format(tmplist.get(2));
						repay_date = productPlan.get(j - 1).get("repay_date");
						RepayPlanDao.insertIntoUserPayPlan(uid, bus_uid, name,
								code, stage, invest_amount, capital,
								fm2.format(profit), total_money,
								remain_capital, repay_date, status);
						// 将第12期数据插入user_repay_plan表
					} else {
						List<Double> tmplist2 = ProfitCal
								.getLastStageTotalMoney(
										Double.parseDouble(list.get(i).get(
												"invest_amount")),
										Double.parseDouble(product.get(
												"year_rate")
												.replaceAll("%", "")) / 100 / 12,
										Double.parseDouble(total_money), j);
						profit = tmplist2.get(0);
						capital = fm2.format(tmplist2.get(1));
						total_money = fm2.format(tmplist2.get(2));
						remain_capital = "0.00";
						repay_date = productPlan.get(j - 1).get("repay_date");
						RepayPlanDao.insertIntoUserPayPlan(uid, bus_uid, name,
								code, stage, invest_amount, capital,
								fm2.format(profit), total_money,
								remain_capital, repay_date, status);
					}
				}
			} else if (product.get("repay_way").equals("按月付息，到期还本")) {
				for (int j = 1; j <= 12; j++) {
					stage = "第" + j + "期";
					profit = ProfitCal.calBaseProfit(
							Double.parseDouble(invest.get("invest_amount")),
							Double.parseDouble(product.get("year_rate").split(
									"%")[0]) / 100, end_bid_time,
							product.get("repay_date"));
					profit = MathUtil.retain2Decimal(profit / 12);
					// if判断是否为第12期，第12期应比前11期多还项目本金
					if (j != 12) {
						total_money = fm2.format(profit);
						capital = "0.00";
						remain_capital = invest_amount;
						repay_date = productPlan.get(j - 1).get("repay_date");
						RepayPlanDao.insertIntoUserPayPlan(uid, bus_uid, name,
								code, stage, invest_amount, capital,
								fm2.format(profit), total_money,
								remain_capital, repay_date, status);
						// 将第12期数据插入user_repay_plan表
					} else {
						total_money = fm2.format(profit
								+ Double.parseDouble(invest_amount));
						capital = invest_amount;
						remain_capital = "0.00";
						repay_date = productPlan.get(j - 1).get("repay_date");
						RepayPlanDao.insertIntoUserPayPlan(uid, bus_uid, name,
								code, stage, invest_amount, capital,
								fm2.format(profit), total_money,
								remain_capital, repay_date, status);
					}
				}
			} else {
				// 不分期的情况，更新第1期的项目还款计划到user_repay_plan表
				profit = ProfitCal.calBaseProfit(Double.parseDouble(invest
						.get("invest_amount")), Double.parseDouble(product.get(
						"year_rate").split("%")[0]) / 100, end_bid_time,
						repay_date);
				total_money = fm2.format(Double.parseDouble(capital) + profit);
				stage = "第1期";
				RepayPlanDao.insertIntoUserPayPlan(uid, bus_uid, name, code,
						stage, invest_amount, capital, fm2.format(profit),
						total_money, remain_capital, repay_date, status);
			}
			// 更新invest_record表profit字段
			double sum = RepayPlanDao.getProfitByCodeandUid(productcode, uid);
			InvestRecordDao.updateProfit(uid, productcode, fm2.format(sum));
		}
		result = true;
		return result;
	}

	/**
	 * 
	 * 向用户还款计划表里插入用户速兑通还款计划数据
	 * 
	 * @param productcode
	 * @return false:失败；true:成功
	 * @throws Exception
	 */
	public boolean updateSDTUserRepayPlan(String productcode) throws Exception {
		boolean result = false;
		String uid = "";
		String bus_uid = "";
		// 查询productinfo表得到指定编号产品的信息
		Map<String, String> product = ProductDao.getProByCode(productcode);
		InvestSDTService2 investSDTService = new InvestSDTService2(log);
		// 从product_repay_plan取得项目相关信息存到对应变量中
		String name = product.get("prj_name");
		String code = product.get("prj_no");
		String capital = "";
		// double profit = 0.00;
		String remain_capital = "0.00";
		String status = "0";
		String total_money = "";
		String stage = "";
		String invest_amount = "";
		// String end_bid_time = product.get("end_bid_time");
		String repay_date = product.get("repay_date");

		// 查询invest_record里指定项目的投资信息
		List<Map<String, String>> list = InvestRecordDao
				.getAllRecordByCode(productcode);
		// 遍历所有投资指定项目的用户，查询投资信息，写入user_repay_plan表
		for (int i = 0; i < list.size(); i++) {
			uid = list.get(i).get("uid");
			bus_uid = "u" + UserDao.getUidById(uid);
			// 根据用户uid和项目名称查询invest_record表某用户投资指定项目的信息
			Map<String, String> invest = InvestRecordDao.getRecordByUserCode(
					uid, productcode);
			capital = invest.get("invest_amount");
			invest_amount = invest.get("invest_amount");

			// 不分期的情况，更新第1期的项目还款计划到user_repay_plan表
			String profit = investSDTService.setProfit(productcode,
					Double.parseDouble(invest_amount));

			total_money = fm2.format(Double.parseDouble(capital)
					+ Double.parseDouble(profit));
			stage = "第1期";
			RepayPlanDao.insertIntoUserPayPlan(uid, bus_uid, name, code, stage,
					invest_amount, capital, profit, total_money,
					remain_capital, repay_date, status);
		}
		result = true;
		return result;
	}
}
