package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.FinanceRecordDao;
import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.ProductTempDao;
import com.autotest.dao.UserAcctDao;
import com.autotest.dao.UserDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.DiscountType;
import com.autotest.enums.FinanceType;
import com.autotest.enums.RepayWay;
import com.autotest.service.bmpService.GeneratePaymentPlan;
import com.autotest.service.bmpService.Loan;
import com.autotest.service.xhhService.Fastcash;
import com.autotest.service.xhhService.InvestFinanceService;
import com.autotest.service.xhhService.InvestSDTService;
import com.autotest.service.xhhService.InvestSDTService2;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.MathUtil;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * 
 * 1-1循环： u21投资u17发起的速兑通项目 xhh-2522 ~ xhh-2540，投资结束后查看u21账户xhh-6995 ~ xhh-7003
 * 1-2循环： u25投资u17发起的速兑通项目 xhh-6917 ~ xhh-6935，投资结束后查看u25账户xhh-7005 ~ xhh-7014
 * 1-3循环： u26投资u17发起的速兑通项目 xhh-6936 ~ xhh-6954，投资结束后查看u26账户xhh-7015 ~ xhh-7024
 * 2-1循环： 没有用户投资u18发起的速兑通项目 3-1循环： u23投资u20发起的速兑通项目，投资结束后查看u23账户 xhh-7114 ~
 * xhh-7132 4-1循环： u21投资u19发起的速兑通项目xhh-7059 ~ xhh-7077，投资结束后查看u21账户xhh-7168 ~
 * xhh-7177 4-2循环： u22投资u19发起的速兑通项目xhh-7078 ~ xhh-7096，投资结束后查看u22账户xhh-7178 ~
 * xhh-7187
 * 
 * @author 002194
 * 
 */
public class YYS008_InvestSDT_U17 extends TestDriver {
	private static String	productId	= "YYS008";

	@Test
	public void test() {
		boolean result = true;
		boolean bFinalResult = true;
		PubXhhService pubXhhService = new PubXhhService();
		KeyWords kw = new KeyWords(driver, log);
		InvestFinanceService investFinanceService = new InvestFinanceService(
				log);
		InvestSDTService investSDTService = new InvestSDTService(log);
		InvestSDTService2 investSDTService2 = new InvestSDTService2(log);
		UserAccountService userAccountService = new UserAccountService(log);
		GeneratePaymentPlan generatePaymentPlan = new GeneratePaymentPlan(kw,
				log);
		boolean tmpResult = false;
		Loan loan = new Loan(log);
		List<String> productIdList = new ArrayList<String>();
		productIdList.add("SDT002");
		productIdList.add("SDT003");
		productIdList.add("SDT005");
		productIdList.add("SDT004");

		List<String> userNumberList = new ArrayList<String>();
		userNumberList.add("u17");
		userNumberList.add("u18");
		userNumberList.add("u20");
		userNumberList.add("u19");

		// 获取投资速兑通项目用户
		try {
			// 获取变现者投资的项目
			Map<String, String> proinfo = ProductDao
					.getProByCode(ProductTempDao.getProductCodeByNumber(
							productId).get("product_code"));
			String projectName = proinfo.get("prj_name");

			// 通过productNumber获得投资项目名称，通过项目名称获得项目信息
			// 获得产品名称
			for (int i = 0; i < productIdList.size(); i++) {
				// 获取变现项目者的uid
				String ori_uid = ProductDao.selectCashUser(
						userNumberList.get(i)).get("uid");
				log.info("变现用户ID" + ori_uid);

				log.info(productIdList.get(i) + " 获取项目信息");
				String productCode = ProductTempDao.getProductCodeByNumber(
						productIdList.get(i)).get("product_code");
				// 速兑通已被投资额
				double haveCashMoney = 0.00;

				// 通过productNumber获取投资用户uid, 投资金额invest_amount
				String investAmount = "";
				String userName = "";
				String pwd = "";
				String payPwd = "";
				String uid = "";
				if ("SDT003" == productIdList.get(i)) {
					log.info("u18  变现项目数据库回写");
					Map<String, String> userMap = UserTempDao.getUid("u18");
					uid = userMap.get("uid");
					ProductDao.insertSdtRecord(uid, 0.0 + "", productCode,
							MathUtil.round2(haveCashMoney), 0.00 + "");
				} else {
					List<Map<String, String>> uidAndInvestAmountList = UserTempDao
							.getInvestUserByProNo(productIdList.get(i));
					for (Map<String, String> uidAndInvestAmountMap : uidAndInvestAmountList) {
						// 获取速兑通变现者账户信息
						Map<String, String> ori_exptectAcctInfo = AccountDao
								.getUserAcctByUid(ori_uid);

						log.info("速兑通变现者账户余额"
								+ ori_exptectAcctInfo.get("balance"));

						// 获取投资用户投资额
						investAmount = uidAndInvestAmountMap
								.get("invest_amount");

						// 根据uid获取投资用户信息
						uid = uidAndInvestAmountMap.get("uid");
						String userNumber = UserTempDao.getUserNumber(uid).get(
								"user_number");
						log.info(ori_uid + " 获取投资用户信息");
						Map<String, String> userMap = UserDao.getUserById(uid);
						userName = userMap.get("mobile");
						pwd = userMap.get("login_pwd");
						payPwd = userMap.get("pay_pwd");

						// 投资用户登录
						if (!pubXhhService.login(kw, userName, pwd)) {
							log.error(userNumber + " 登录失败！");
							throw new Exception();
						} else {
							log.info(userNumber + " 登录成功！");
						}

						// 点击【投资理财】菜单
						log.info(userNumber + " 投资理财");
						investFinanceService.viewInvertListPage(kw);

						// 获取账户纵览数据
						log.info(userNumber + " 获取账户纵览数据");
						Map<String, String> exptectAcctInfo = AccountDao
								.getUserAcctByUid(uid);

						// 点击速兑通页签
						log.info(userNumber + " 点击速兑通页签");
						investSDTService.clickSDTPage(kw);

						// 数据库获取速兑通数据
						log.info(userNumber + " 数据库获取速兑通数据");
						Map<String, String> expectProdInfo = ProductDao
								.getCashProByCode(productCode);

						// 查找项目
						if (investFinanceService.findProductByPaging(kw,
								productCode)) {
							// 用户点击[立即投资]按钮
							log.info(userNumber + " 立即投资速兑通");
							investFinanceService.selectInvestProduct(kw,
									productCode);

							// 项目详情页面上的字段校验（非公式部分）
							boolean checkInvestAmount = false;
							boolean checkCollect = true;
							boolean checkIncomePer10Thous = false;
							boolean checkRemainAmount = true;
							boolean checkEndTime = false;
							boolean checkRate = false;
							boolean checkMinInvestAmount = false;
							boolean checkStepInvestAmount = false;
							boolean checkMaxInvestAmount = false;
							boolean checkAmount = true;
							boolean checkGuardWay = false;
							boolean checkProfitStart = false;
							boolean checkDateUsed = false;
							boolean checkRepayType = true;
							boolean checkRepayDate = false;
							boolean checkSupportCash = false;
							boolean checkEarlyClose = false;
							boolean checkExtend = false;
							boolean checkEarlyRepay = false;
							boolean checkAutoRatio = false;
							boolean checkAcctBalance = true;
							boolean checkProfit = true;
							DiscountType discountType = investFinanceService
									.useBonusOrCoupons(exptectAcctInfo);
							if (investFinanceService.checkMapInProDetail(kw,
									expectProdInfo, exptectAcctInfo,
									checkInvestAmount, checkCollect,
									checkIncomePer10Thous, checkRemainAmount,
									checkEndTime, checkRate,
									checkMinInvestAmount,
									checkStepInvestAmount,
									checkMaxInvestAmount, checkAmount,
									checkGuardWay, checkProfitStart,
									checkDateUsed, checkRepayType,
									checkRepayDate, checkSupportCash,
									checkEarlyClose, checkExtend,
									checkEarlyRepay, checkAutoRatio,
									checkAcctBalance, checkProfit, RepayWay.E,
									discountType)) {
								log.info(userNumber + " 【详情页面】字段校验成功");
							} else {
								log.error(userNumber + " 【详情页面】字段校验失败");
								result = false;
							}

							// 输入投资金额和支付密码
							log.info(userNumber + " 输入投资金额和支付密码");
							investFinanceService.setAmountAndPWd(kw,
									investAmount, payPwd);

							// 计算获得预计收益
							Map<String, String> expConfmPayMap = investFinanceService
									.getExpConfmPayMap(expectProdInfo,
											exptectAcctInfo,
											Double.parseDouble(investAmount),
											RepayWay.C, discountType);

							// 校验预估收益，投资金额，使用账户余额的值
							if (investFinanceService.checkMapInConfirmPay(kw,
									expConfmPayMap, discountType)) {
								log.info(userNumber + " 【投资确认】页面数据校验成功");
							} else {
								log.error(userNumber + " 【投资确认】页面数据校验失败");
								result = false;
							}

							// 点击【确认支付】按钮
							if (investFinanceService
									.payConfirmAndCheckSuccess(kw)) {
								log.info(userNumber + " 用户投资成功");
								haveCashMoney = haveCashMoney
										+ Double.parseDouble(investAmount);
							} else {
								log.error(userNumber + " 用户投资失败");
								result = false;
							}

							// 回写需要修改的数据
							log.info(userNumber + " 回写需要修改的数据");
							userAccountService.dataWriteBack(expConfmPayMap,
									exptectAcctInfo, expectProdInfo,
									productCode,
									Double.parseDouble(investAmount), uid);

							// 原始变现者数据库回写：账户概括，资金记录（变现）
							userAccountService.sdtdataWriteBack(
									ori_exptectAcctInfo, productCode,
									Double.parseDouble(investAmount), ori_uid);

							// u20变现项目被u23用户投资满标，即截标
							if ((StringUtils.isEquals(productIdList.get(i),
									"SDT005"))
									|| (StringUtils.isEquals(userNumber, "u26"))) {
								// 计算投资速兑通用户收益并回写数据库
								log.info("u23  回写需要修改的数据");
								investAmount = uidAndInvestAmountMap
										.get("invest_amount");
								String profit = investSDTService.setProfit2(kw,
										productCode,
										Double.parseDouble(investAmount));
								// 更新用户账户表资金记录
								UserAcctDao.updateProfit(uid, profit);
								// 更新投资记录收益值
								ProductDao.updateSdtProfit(productCode, uid,
										profit);
								// 修改invest_record表的status字段为待还款
								loan.updateInvestrecoed(productCode);
								ThreadUtil.sleep(3);
							}

							// 用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值
							userAccountService.viewUserAccountIndex(kw);
							Map<String, String> expextAccountMap = AccountDao
									.getUserAcctByUid(uid);
							if (userAccountService.checkAccountInvestVal(kw,
									expextAccountMap)) {
								log.info(userNumber + " 【账户总览】页面字段校验成功");
							} else {
								log.error(userNumber + " 【账户总览】页面字段校验失败");
								result = false;
							}

							// 用户投资后访问【理财记录】页面
							log.info(userNumber + " 用户投资后访问【理财记录】页面");
							userAccountService.viewFinaceManagePage(kw);

							// 理财记录中点击【所有】类型
							userAccountService.viewAllType(kw);

							// 获取理财记录信息
							log.info(userNumber + " 获取理财记录信息");
							Map<String, String> orderInfoMap = InvestRecordDao
									.getRecordByUserCode(uid, productCode);
							if (userAccountService.checkValInInvestRecord(kw,
									productCode, orderInfoMap)) {
								log.info(userNumber + "【理财记录】校验数据成功");
							} else {
								log.error(userNumber + "【理财记录】校验数据失败");
								result = false;
							}

							// 用户投资后查看【资金记录】数据
							log.info(userNumber + " 查看【资金记录】数据");
							userAccountService.viewPayRecord(kw);
							Map<String, String> expectPayRecord = FinanceRecordDao
									.getRecordByUserCode(uid, productCode,
											FinanceType.invest);
							if (userAccountService.checkValInPayRecord(kw,
									productCode, expectPayRecord,
									FinanceType.invest)) {
								log.info(userNumber + "【资金记录】校验数据成功");
							} else {
								log.error(userNumber + "【资金记录】校验数据失败");
								result = false;
							}

						} else {
							log.error(productCode + "项目不存在");
							result = false;
							log.error(ScreenShotUtil.takeScreenshot(ThreadName,
									kw.driver));
							throw new Exception("项目不存在");
						}

						// 当前用户退出登录
						// 调用登出函数
						if (pubXhhService.logout(kw)) {
							log.info(userNumber + " 登出成功");
						} else {
							log.error(userNumber + " 登出失败");
							log.error(ScreenShotUtil.takeScreenshot(ThreadName,
									kw.driver));
							throw new Exception();
						}
					}

					// 速兑通项目截标后查看投资者账户信息
					for (Map<String, String> uidAndInvestAmountMap : uidAndInvestAmountList) {
						// 根据uid获取投资用户信息

						uid = uidAndInvestAmountMap.get("uid");
						String userNumber = UserTempDao.getUserNumber(uid).get(
								"user_number");
						log.info(ori_uid + " 截标后获取投资用户信息");
						Map<String, String> userMap = UserDao.getUserById(uid);
						userName = userMap.get("mobile");
						pwd = userMap.get("login_pwd");
						payPwd = userMap.get("pay_pwd");

						// 如果是u19 用户变现项目，则更改服务器时间来进行截标
						if (StringUtils
								.isEquals(productIdList.get(i), "SDT004")) {
							// 通过user_number获得投资项目名称，通过项目名称获得项目信息
							Map<String, String> YYS_proinfo = ProductDao
									.getProByCode(ProductTempDao
											.getProductCodeByNumber(productId)
											.get("product_code"));
							String end_bid_time = YYS_proinfo
									.get("end_bid_time");
							// 修改服务器时间为变现截标日第5天
							Fastcash fastcash = new Fastcash(log);
							fastcash.changTime2(end_bid_time, 67);
							ThreadUtil.sleep(10);
							// 计算投资速兑通用户收益并回写数据库
							investAmount = uidAndInvestAmountMap
									.get("invest_amount");
							String profit = investSDTService.setProfit2(kw,
									productCode,
									Double.parseDouble(investAmount));
							// 更新用户账户表资金记录
							UserAcctDao.updateProfit(uid, profit);
							// 更新投资记录收益值
							ProductDao
									.updateSdtProfit(productCode, uid, profit);
							// 修改invest_record表的status字段为待还款
							loan.updateInvestrecoed(productCode);
							ThreadUtil.sleep(3);
						}

						// u17、u19变现项目截标后更新投资用户收益；（u17被投资满标截标，u19募集期结束截标）
						if ((StringUtils.isEquals(productIdList.get(i),
								"SDT002"))
								&& (!StringUtils.isEquals(userNumber, "u26"))) {
							// 计算投资速兑通用户收益并回写数据库
							investAmount = uidAndInvestAmountMap
									.get("invest_amount");
							String profit = investSDTService.setProfit2(kw,
									productCode,
									Double.parseDouble(investAmount));
							// 更新用户账户表资金记录
							UserAcctDao.updateProfit(uid, profit);
							// 更新投资记录收益值
							ProductDao
									.updateSdtProfit(productCode, uid, profit);
							// 修改invest_record表的status字段为待还款
							loan.updateInvestrecoed(productCode);
							ThreadUtil.sleep(3);
						}

						// 投资用户登录
						if (!pubXhhService.login(kw, userName, pwd)) {
							log.error(userNumber + " 截标后登录失败！");
							throw new Exception();
						} else {
							log.info(userNumber + " 截标后登录成功！");
						}

						// 用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值
						Map<String, String> expextAccountMap = AccountDao
								.getUserAcctByUid(uid);
						if (userAccountService.checkAccountInvestVal(kw,
								expextAccountMap)) {
							log.info(userNumber + " 截标后【账户总览】页面字段校验成功");
						} else {
							log.error(userNumber + " 截标后【账户总览】页面字段校验失败");
							result = false;
						}

						// 用户投资后访问【理财记录】页面
						log.info(userNumber + " 截标后用户投资后访问【理财记录】页面");
						userAccountService.viewFinaceManagePage(kw);

						// 理财记录中点击【所有】类型
						userAccountService.viewAllType(kw);

						// 获取理财记录信息
						log.info(userNumber + " 截标后获取理财记录信息");
						Map<String, String> orderInfoMap = InvestRecordDao
								.getRecordByUserCode(uid, productCode);
						if (userAccountService.checkValInInvestRecord(kw,
								productCode, orderInfoMap)) {
							log.info(userNumber + " 截标后【理财记录】校验数据成功");
						} else {
							log.error(userNumber + " 截标后【理财记录】校验数据失败");
							result = false;
						}

						// 用户投资后查看【资金记录】数据
						log.info(userNumber + " 截标后查看【资金记录】数据");
						userAccountService.viewPayRecord(kw);
						Map<String, String> expectPayRecord = FinanceRecordDao
								.getRecordByUserCode(uid, productCode,
										FinanceType.invest);
						if (userAccountService.checkValInPayRecord(kw,
								productCode, expectPayRecord,
								FinanceType.invest)) {
							log.info(userNumber + " 截标后【资金记录】校验数据成功");
						} else {
							log.error(userNumber + " 截标后【资金记录】校验数据失败");
							result = false;
						}

						// 当前用户退出登录
						// 调用登出函数
						if (pubXhhService.logout(kw)) {
							log.info(userNumber + " 截标后登出成功");
						} else {
							log.error(userNumber + " 截标后登出失败");
							log.error(ScreenShotUtil.takeScreenshot(ThreadName,
									kw.driver));
							throw new Exception();
						}
					}

					// 回写用户还款计划表
					log.info(productCode + " 回写用户还款计划表");
					// 写入用户还款计划
					tmpResult = generatePaymentPlan
							.updateSDTUserRepayPlan(productCode);
					if (!tmpResult) {
						log.error(productCode + " 写入用户还款计划失败!");
						result = false;
					}

					// 累计投资笔数数值修正为保留小数点后一位
					double investNum = uidAndInvestAmountList.size();
					DecimalFormat df = new DecimalFormat("######0.0");
					df.format(investNum);
					// 回写速兑通项目累计数据
					double count_plant_fee = haveCashMoney * 2 / 1000;
					ProductDao.insertSdtRecord(uid, investNum + "",
							productCode, MathUtil.round2(haveCashMoney),
							MathUtil.round2(count_plant_fee));

					// 原始变现者数据库回写：账户概括，资金记录（平台费）
					Map<String, String> ori_exptectAcctInfo = AccountDao
							.getUserAcctByUid(ori_uid);
					log.info("账户余额" + ori_exptectAcctInfo.get("balance"));
					userAccountService.sdtFeedataWriteBack(ori_exptectAcctInfo,
							productCode, count_plant_fee, ori_uid);

					investSDTService2.countUserAcct(productCode, ori_uid,
							projectName, userNumberList.get(i),
							productIdList.get(i));

					// 校验所有投资人投资是否正确
					bFinalResult = bFinalResult && result;
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			bFinalResult = false;
			log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}

		assertTrue(bFinalResult);
	}
}
