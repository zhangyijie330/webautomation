package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.FinanceRecordDao;
import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductTempDao;
import com.autotest.dao.UserDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.FinanceType;
import com.autotest.service.bmpService.GeneratePaymentPlan;
import com.autotest.service.bmpService.PubBmpService;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.ScreenShotUtil;

/**
 * 测试集 : 生成还款计划，保理项目 YYS20160718004
 * 
 * @author wb0004
 * 
 */
public class YYS004_GenPayPlan extends TestDriver {
	public static KeyWords kw;
	public static boolean bResult = false;
	private String productNumber = "YYS004";

	@Test
	public void test() {
		kw = new KeyWords(driver, log);
		// 标识当前用例是否验证通过

		try { 
			String productCode = ProductTempDao.getProductCodeByNumber(productNumber).get("product_code");
			PubBmpService pubBmpService = new PubBmpService(); 
			KeyWords keyWords = new KeyWords(driver, log); 
			GeneratePaymentPlan generatePaymentPlan = new GeneratePaymentPlan(keyWords, log);
			boolean tmpResult = false; 
			// 登录鑫合汇业务管理平台 
			String userName = BaseConfigUtil.getBMPName(); 
			String pwd = BaseConfigUtil.getBMPPwd(); 
			boolean loginResult = pubBmpService.login(keyWords, userName, pwd);
			if (!loginResult) {
				log.error("登录失败!"); 
				throw new Exception("login failed");
				} 
			// 访问项目生成还款计划 
			tmpResult = generatePaymentPlan.enterGeneratePaymentPlan(); 
			if (!tmpResult) {
				log.error("访问【项目运营-项目生成还款计划】页面失败!");
				throw new Exception("enter failed"); 
				} 
			// 项目名称 
			tmpResult = generatePaymentPlan.checkGeneratePaymentPlan(productCode);
			if(!tmpResult) 
			{ 
				log.error("查询项目失败!"); 
				throw new Exception("search failed"); 
				} 
			// 校验结果列表展示记录
			// 从数据库中读取期望值，从页面读取实际值 
			Map<String, String> mapFromDB = generatePaymentPlan.getMapFromDB(productCode); 
			Map<String, String> mapFromPage = generatePaymentPlan.getMapFromPage(); 
			
			// 对比页面数据和期望数据
			boolean isEquals = generatePaymentPlan.checkGeneratePaymentPlanInfo(productCode, mapFromPage, mapFromDB);
			if (!isEquals) 
			{ 
				log.error("页面数据校验失败！");
				bResult = false;
				} 
			
			// 点击生成还款计划
			tmpResult = generatePaymentPlan.clickGeneratePaymentPlan(productCode); 
			if (!tmpResult) {
				log.error("点击生成还款计划失败!");
				throw new Exception("click gen_pay_plan_btn failed"); 
				}
			
			//写入用户还款计划 
			tmpResult =  generatePaymentPlan.updateUserRepayPlan(productCode); 
			if (!tmpResult)
			{ 
				log.error("写入用户还款计划失败!"); 
				throw new Exception("write user_pay_plan failed"); 
				}
			
			//写入项目还款计划 
			tmpResult = generatePaymentPlan.updateProductRepayPlan(productCode);
			if(!tmpResult) { 
				log.error("写入项目还款计划失败!");
				throw new Exception("write product_pay_plan failed"); 
				}
						
			PubXhhService publicService = new PubXhhService();
			UserAccountService accountService = new UserAccountService(log);

			String reminder = "暂无回款消息！";
			List<Map<String, String>> lst = UserTempDao.getInvestUserByProNo(productNumber);
			for (int i = 0; i < lst.size(); i++) {
				String uid = lst.get(i).get("uid");
				Map<String, String> userInfo = UserDao.getUserById(uid);
				String user = userInfo.get("mobile");
				String pwd2 = userInfo.get("login_pwd");
					if (publicService.login(kw, user, pwd2)) {
						log.info(user + "登录成功");
						// 校验【账户总览】页面字段值
						Map<String, String> exptectAcctInfo = AccountDao
								.getUserAcctByUid(uid);
						if (accountService.checkAccountInvestVal(kw,
								exptectAcctInfo)) {
							log.info("【账户总览】页面字段校验成功");
						} else {
							log.error("【账户总览】页面字段校验失败");
							bResult = false;
						}
						// 校验回款日历数据
						String date = "";
						String amount = "";
						String count = "";
						String status ="";
						List<Map<String, String>> relstCalendar = publicService.mergeList(uid);
						for (Map<String, String> mapCalendar : relstCalendar) {
							date = mapCalendar.get("date");
							amount = mapCalendar.get("amount");
							count = mapCalendar.get("count");
							status = mapCalendar.get("status");
							if (publicService.checkReturnMoneyCalendar(kw, date,
									count, amount, status)) {
								log.info("【回款日历】校验成功");
							} else {
								log.error("【回款日历】校验失败");
								bResult = false;
							}
						}

						// 校验回款提醒
						if (publicService.checkReturnMoneyReminder(kw, reminder)) {
							log.info("【回款提醒】校验成功");
						} else {
							log.error("【回款提醒】校验失败");
							bResult = false;
						}

						// 点击头像左侧的【投资管理】入口
						accountService.viewFinaceManagePage(kw);
						// 点击【所有】入口并检查理财记录列表数据
						accountService.viewAllType(kw);
						/*
						 * 
						 * 用户查看【理财记录】数据
						 */
						Map<String, String> investInfoMap = InvestRecordDao
								.getRecordByUserCode(uid, productCode);
						if (accountService.checkValInInvestRecord(kw, productCode,
								investInfoMap)) {
							log.info("【理财记录】校验数据成功");
						} else {
							log.error("【理财记录】校验数据失败");
							bResult = false;
						}

						/*
						 * 查看【资金记录】数据
						 */
						accountService.viewPayRecord(kw);
						Map<String, String> expectPayRecord = FinanceRecordDao
								.getRecordByUserCode(uid, productCode,
										FinanceType.invest);
						if (accountService.checkValInPayRecord(kw, productCode,
								expectPayRecord, FinanceType.invest)) {
							log.info("【资金记录】校验数据成功");
						} else {
							log.error("【资金记录】校验数据失败");
							bResult = false;
						}

						// 登出
						if (publicService.logout(kw)) {
							log.info("logout successfully");
							bResult = true;
						} else {
							log.error("logout failed");
							log.error(ScreenShotUtil.takeScreenshot(ThreadName,
									kw.driver));
							bResult = false;
						}

					} else {
						log.error("登录失败");
						log.error(ScreenShotUtil.takeScreenshot(ThreadName,
								kw.driver));
						bResult = false;
						}
					}
			}catch (Exception e) {
				log.error("Exception", e);
				log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
				bResult = false;
			}
		//验证整个测试用例是否成功
		assertTrue(bResult);
	}
}
