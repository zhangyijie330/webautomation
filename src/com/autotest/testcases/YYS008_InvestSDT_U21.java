package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.text.DecimalFormat;
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
 * 1-1循环：u24投资u21用户发起的速兑通，投资结束后查看u24账户xhh-7206 ~ xhh-7224
 * 1-2循环：u25投资u21用户发起的速兑通，投资结束后查看u25账户xhh-7225 ~ xhh-7243
 * 
 * @author 002194
 * 
 */
public class YYS008_InvestSDT_U21 extends TestDriver {
	private static String	productNumber	= "SDT006";
	private static String	user_number		= "u21";

	private static String	productId		= "SDT002";

	@Test
	public void test() {
		boolean result = true;
		boolean bFinalResult = true;
		PubXhhService pubXhhService = new PubXhhService();
		KeyWords kw = new KeyWords(driver, log);
		InvestFinanceService investFinanceService = new InvestFinanceService(
				log);
		InvestSDTService investSDTService = new InvestSDTService(log);
		UserAccountService userAccountService = new UserAccountService(log);
		InvestSDTService2 investSDTService2 = new InvestSDTService2(log);
		Loan loan = new Loan(log);
		GeneratePaymentPlan generatePaymentPlan = new GeneratePaymentPlan(kw,
				log);
		boolean tmpResult = false;
		// 获取投资速兑通项目用户
		try {
			Map<String, String> proinfo = ProductDao
					.getProByCode(ProductTempDao.getProductCodeByNumber(
							productId).get("product_code"));
			String projectName = proinfo.get("prj_name");
			// 通过productNumber获得投资项目名称，通过项目名称获得项目信息
			// 获得产品名称
			String productCode = ProductTempDao.getProductCodeByNumber(
					productNumber).get("product_code");
			// 速兑通已被投资额
			double haveCashMoney = 0;

			// 获取变现项目者的uid
			String ori_uid = ProductDao.selectCashUser(user_number).get("uid");
			log.info("变现用户ID" + ori_uid);

			// 通过productNumber获取投资用户uid, 投资金额invest_amount
			String investAmount = "";
			String userName = "";
			String pwd = "";
			String payPwd = "";
			String uid = "";
			List<Map<String, String>> uidAndInvestAmountList = UserTempDao
					.getInvestUserByProNo(productNumber);
			for (Map<String, String> uidAndInvestAmountMap : uidAndInvestAmountList) {
				// 获取速兑通变现者账户信息
				Map<String, String> ori_exptectAcctInfo = AccountDao
						.getUserAcctByUid(ori_uid);

				log.info("速兑通变现者账户余额" + ori_exptectAcctInfo.get("balance"));

				// 获取投资用户投资额
				investAmount = uidAndInvestAmountMap.get("invest_amount");

				// 根据uid获取投资用户信息
				uid = uidAndInvestAmountMap.get("uid");
				String userNumber = UserTempDao.getUserNumber(uid).get(
						"user_number");
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
				investFinanceService.viewInvertListPage(kw);

				// 获取账户纵览数据
				Map<String, String> exptectAcctInfo = AccountDao
						.getUserAcctByUid(uid);

				// 点击速兑通页签
				investSDTService.clickSDTPage(kw);

				// 数据库获取速兑通数据
				Map<String, String> expectProdInfo = ProductDao
						.getCashProByCode(productCode);

				// 查找项目
				if (investFinanceService.findProductByPaging(kw, productCode)) {
					// // 1.用户点击[立即投资]按钮
					investFinanceService.selectInvestProduct(kw, productCode);

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
							expectProdInfo, exptectAcctInfo, checkInvestAmount,
							checkCollect, checkIncomePer10Thous,
							checkRemainAmount, checkEndTime, checkRate,
							checkMinInvestAmount, checkStepInvestAmount,
							checkMaxInvestAmount, checkAmount, checkGuardWay,
							checkProfitStart, checkDateUsed, checkRepayType,
							checkRepayDate, checkSupportCash, checkEarlyClose,
							checkExtend, checkEarlyRepay, checkAutoRatio,
							checkAcctBalance, checkProfit, RepayWay.E,
							discountType)) {
						log.info(userNumber + "【详情页面】字段校验成功");
					} else {
						log.error(userNumber + "【详情页面】字段校验失败");
						result = false;
					}

					// 输入投资金额和支付密码
					investFinanceService.setAmountAndPWd(kw, investAmount,
							payPwd);

					// 计算获得预计收益
					Map<String, String> expConfmPayMap = investFinanceService
							.getExpConfmPayMap(expectProdInfo, exptectAcctInfo,
									Double.parseDouble(investAmount),
									RepayWay.C, discountType);

					// 校验预估收益，投资金额，使用账户余额的值
					if (investFinanceService.checkMapInConfirmPay(kw,
							expConfmPayMap, discountType)) {
						log.info(userNumber + "【投资确认】页面数据校验成功");
					} else {
						log.error(userNumber + "【投资确认】页面数据校验失败");
						result = false;
					}

					// 点击【确认支付】按钮
					if (investFinanceService.payConfirmAndCheckSuccess(kw)) {
						log.info(userNumber + "用户投资成功");
						haveCashMoney = haveCashMoney
								+ Double.parseDouble(investAmount);
					} else {
						log.error(userNumber + "用户投资失败");
						result = false;
					}

					// 回写需要修改的数据
					userAccountService.dataWriteBack(expConfmPayMap,
							exptectAcctInfo, expectProdInfo, productCode,
							Double.parseDouble(investAmount), uid);

					// 原始变现者数据库回写：账户概括，资金记录（变现）
					userAccountService.sdtdataWriteBack(ori_exptectAcctInfo,
							productCode, Double.parseDouble(investAmount),
							ori_uid);

					// u21变现项目被u25用户投资满标，即截标
					if (StringUtils.isEquals(userNumber, "u25")) {
						// 计算投资速兑通用户收益并回写数据库
						log.info("u25  截标后回写需要修改的数据");
						investAmount = uidAndInvestAmountMap
								.get("invest_amount");
						String profit = investSDTService.setProfit2(kw,
								productCode, Double.parseDouble(investAmount));
						// 更新用户账户表资金记录
						UserAcctDao.updateProfit(uid, profit);
						// 更新投资记录收益值
						ProductDao.updateSdtProfit(productCode, uid, profit);
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
					if (userAccountService.checkValInPayRecord(kw, productCode,
							expectPayRecord, FinanceType.invest)) {
						log.info(userNumber + "【资金记录】校验数据成功");
					} else {
						log.error(userNumber + "【资金记录】校验数据失败");
						result = false;
					}
				} else {
					log.error(productCode + "项目不存在");
					bFinalResult = false;
					log.info(ScreenShotUtil.takeScreenshot(ThreadName,
							kw.driver));
					throw new Exception("项目不存在");
				}

				// 当前用户退出登录
				// 调用登出函数
				if (pubXhhService.logout(kw)) {
					log.info(userNumber + " 登出成功");
					result = true;
				} else {
					log.error(userNumber + " 登出失败");
					log.error(ScreenShotUtil.takeScreenshot(ThreadName,
							kw.driver));
					throw new Exception(userNumber + " 登出失败");
				}
				// 校验所有人投资人投资是否正确
				bFinalResult = bFinalResult && result;
			}

			// 速兑通项目截标后查看投资者账户信息
			for (Map<String, String> uidAndInvestAmountMap : uidAndInvestAmountList) {
				// 根据uid获取投资用户信息
				uid = uidAndInvestAmountMap.get("uid");
				String userNumber = UserTempDao.getUserNumber(uid).get(
						"user_number");
				Map<String, String> userMap = UserDao.getUserById(uid);
				userName = userMap.get("mobile");
				pwd = userMap.get("login_pwd");
				payPwd = userMap.get("pay_pwd");

				// u21变现项目被投资满标，即截标,更新u24用户收益
				if (StringUtils.isEquals(userNumber, "u24")) {
					// 计算投资速兑通用户收益并回写数据库
					log.info("u24  截标后回写需要修改的数据");
					investAmount = uidAndInvestAmountMap.get("invest_amount");
					String profit = investSDTService.setProfit2(kw,
							productCode, Double.parseDouble(investAmount));
					// 更新用户账户表资金记录
					UserAcctDao.updateProfit(uid, profit);
					// 更新投资记录收益值
					ProductDao.updateSdtProfit(productCode, uid, profit);
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
				userAccountService.viewFinaceManagePage(kw);

				// 理财记录中点击【所有】类型
				userAccountService.viewAllType(kw);

				// 获取理财记录信息
				Map<String, String> orderInfoMap = InvestRecordDao
						.getRecordByUserCode(uid, productCode);
				if (userAccountService.checkValInInvestRecord(kw, productCode,
						orderInfoMap)) {
					log.info(userNumber + " 截标后【理财记录】校验数据成功");
				} else {
					log.error(userNumber + " 截标后【理财记录】校验数据失败");
					result = false;
				}

				// 用户投资后查看【资金记录】数据
				log.info(uid + " 截标后查看【资金记录】数据");
				userAccountService.viewPayRecord(kw);
				Map<String, String> expectPayRecord = FinanceRecordDao
						.getRecordByUserCode(uid, productCode,
								FinanceType.invest);
				if (userAccountService.checkValInPayRecord(kw, productCode,
						expectPayRecord, FinanceType.invest)) {
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
					throw new Exception(userNumber + " 截标后登出失败");
				}
			}

			// 回写用户还款计划表
			log.info(productCode + " 回写用户还款计划表");
			// 写入用户还款计划
			tmpResult = generatePaymentPlan.updateSDTUserRepayPlan(productCode);
			if (!tmpResult) {
				log.error(productCode + " 写入用户还款计划失败!");
				result = false;
			}

			// 累计投资笔数数值修正为保留小数点后一位
			double investNum = uidAndInvestAmountList.size();
			DecimalFormat df = new DecimalFormat("######0.0");
			df.format(investNum);

			// 速兑通项目累计数据回写
			double count_plant_fee = haveCashMoney * 2 / 1000;
			ProductDao.insertSdtRecord(uid, investNum + "", productCode,
					MathUtil.round2(haveCashMoney),
					MathUtil.round2(count_plant_fee));

			// 原始变现者数据库回写：账户概括，资金记录（平台费）
			Map<String, String> ori_exptectAcctInfo = AccountDao
					.getUserAcctByUid(ori_uid);

			userAccountService.sdtFeedataWriteBack(ori_exptectAcctInfo,
					productCode, count_plant_fee, ori_uid);
			investSDTService2.countUserAcct(productCode, ori_uid, projectName,
					user_number, productId);
			// investSDTService.countProfit(kw, productCode, ori_uid,
			// projectName);
			// 校验所有投资人投资是否正确
			bFinalResult = bFinalResult & result;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			bFinalResult = false;
			log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}

		assertTrue(bFinalResult);
	}
}
