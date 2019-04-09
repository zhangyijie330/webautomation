package com.autotest.testcases;

import java.util.List;
import java.util.Map;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.FinanceRecordDao;
import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.ProductTempDao;
import com.autotest.dao.UserDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.DiscountType;
import com.autotest.enums.FinanceType;
import com.autotest.enums.RepayWay;
import com.autotest.service.xhhService.InvestFinanceService;
import com.autotest.service.xhhService.InvestSDTService;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.MathUtil;
import com.autotest.utility.ScreenShotUtil;

/**
 * 用例xhh-1333至用例xhh-1356
 * 
 * @author 000738
 * 
 */
public class YYS007_InvestSDT extends TestDriver {

	public static KeyWords kw;
	// public static boolean bResult;
	public static boolean bFinalResult = true;
	private String productNumber = "SDT001";
	private String investProduct_id = "YYS007";
	private String user_number = "u12";

	@Test
	public void test() {

		PubXhhService publicService = new PubXhhService();
		UserAccountService accountService = new UserAccountService(log);
		InvestFinanceService financeService = new InvestFinanceService(log);
		InvestSDTService investSDT = new InvestSDTService(log);
		kw = new KeyWords(driver, log);
		try {
			// 获取变现者投资项目名称
			Map<String, String> proinfo = ProductDao.getProByCode((ProductDao
					.productIdSelectProductInfo(investProduct_id)
					.get("product_name")));
			String prj_name = proinfo.get("prj_name");

			// 根据productNumber获得产品名称
			String productCode = ProductTempDao.getProductCodeByNumber(
					productNumber).get("product_code");
			log.info("产品名称" + productCode);
			double haveCashMoney = 0;

			// 获取变现项目者的uid
			String ori_uid = ProductDao.selectCashUser(user_number).get("uid");
			log.info("变现用户ID" + ori_uid);

			// 根据productNumber获得投资该产品的用户id与投资金额
			List<Map<String, String>> lst = UserTempDao
					.getInvestUserByProNo(productNumber);

			int successCount = 0;
			for (int i = 0; i < lst.size(); i++) {
				log.info("购买速兑通项目的用户个数" + lst.size());
				String uid = lst.get(i).get("uid");
				log.info("投资速兑通用户uid" + uid);
				int investAmount = Integer.parseInt(lst.get(i).get(
						"invest_amount"));
				log.info("investAmount[]=" + investAmount + "");

				// 获取速兑通变现者账户信息
				Map<String, String> ori_exptectAcctInfo = AccountDao
						.getUserAcctByUid(ori_uid);

				log.info("速兑通变现者账户余额" + ori_exptectAcctInfo.get("balance"));

				// 获得详细的用户信息
				Map<String, String> userInfo = UserDao.getUserById(uid);
				String user = userInfo.get("mobile");
				String pwd = userInfo.get("login_pwd");
				String paymentPwd = userInfo.get("pay_pwd");
				log.info("user=[" + user + "];pwd=[" + pwd + "];paymentPwd=["
						+ paymentPwd + "]");

				// 1.用户登录
				if (publicService.login(kw, user, pwd)) {

					log.info(user + "登录成功");

					// 2.校验【账户总览】页面字段值
					Map<String, String> exptectAcctInfo = AccountDao
							.getUserAcctByUid(uid);
					if (accountService.checkAccountInvestVal(kw,
							exptectAcctInfo)) {
						log.info("【账户总览】页面字段校验成功");
					} else {
						log.error("【账户总览】页面字段校验失败");
						bFinalResult = false;
					}

					// 1.点击顶部导航拦【投资理财】入口
					financeService.viewInvertListPage(kw);
					log.info("查看投资理财界面");

					// 3.点击速兑通页签
					investSDT.clickSDTPage(kw);

					// 5.数据库获取速兑通数据
					Map<String, String> expectProdInfo = ProductDao
							.getCashProByCode(productCode);

					log.info("产品名称productCode=[" + productCode + "]");

					// 6.用户点击立即投资按钮
					financeService.selectInvestProduct(kw, productCode);

					// 7.项目详情页面上的字段校验（非公式部分）
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
					DiscountType discountType = financeService
							.useBonusOrCoupons(exptectAcctInfo);
					if (financeService.checkMapInProDetail(kw, expectProdInfo,
							exptectAcctInfo, checkInvestAmount, checkCollect,
							checkIncomePer10Thous, checkRemainAmount,
							checkEndTime, checkRate, checkMinInvestAmount,
							checkStepInvestAmount, checkMaxInvestAmount,
							checkAmount, checkGuardWay, checkProfitStart,
							checkDateUsed, checkRepayType, checkRepayDate,
							checkSupportCash, checkEarlyClose, checkExtend,
							checkEarlyRepay, checkAutoRatio, checkAcctBalance,
							checkProfit, RepayWay.E, discountType)) {
						log.info("【详情页面】字段校验成功");
					} else {
						log.error("【详情页面】字段校验失败");
						bFinalResult = false;
					}

					// 8.输入投资金额和支付密码
					financeService.setAmountAndPWd(kw, investAmount + "",
							paymentPwd);

					// 9.计算获得预计收益
					Map<String, String> expConfmPayMap = financeService
							.getExpConfmPayMap(expectProdInfo, exptectAcctInfo,
									investAmount, RepayWay.C, discountType);

					// 10.校验预估收益，投资金额，使用账户余额的值
					if (financeService.checkMapInConfirmPay(kw, expConfmPayMap,
							discountType)) {
						log.info("【投资确认】页面数据校验成功");
					} else {
						log.error("【投资确认】页面数据校验失败");
						bFinalResult = false;
					}

					/*
					 * 用户确认投资
					 */
					// 1.点击【确认支付】按钮
					if (financeService.payConfirmAndCheckSuccess(kw)) {
						log.info("用户" + user + "投资成功");
						successCount++;
					} else {
						log.error("用户投资失败");
						bFinalResult = false;
						log.info(ScreenShotUtil.takeScreenshot(ThreadName,
								kw.driver));
						throw new Exception("投资失败");
					}

					/*
					 * 投资信息回写数据库 投资成功后账户总览，理财记录，资金记录信息验证
					 */

					// 1.用户数据，项目数据，投资记录数据会写
					log.info("用户信息会写uid" + uid);
					accountService.dataWriteBack(expConfmPayMap,
							exptectAcctInfo, expectProdInfo, productCode,
							investAmount, uid);
					log.info("投资者数据库回写成功");

					// 2.原始变现者数据库回写：账户概括，资金记录（变现）
					accountService.sdtdataWriteBack(ori_exptectAcctInfo,
							productCode, investAmount, ori_uid);
					log.info("变现者数据库回写成功");

					// 2.累计投资金额
					haveCashMoney = haveCashMoney + investAmount;
					log.info("haveCashMoney=[" + haveCashMoney + "]");

					// 3.点击【投资成功】页面的【查看账户】按钮
					financeService.viewAccountAndCheckPage(kw);

					// 4. 用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值
					Map<String, String> expextAccountMap = AccountDao
							.getUserAcctByUid(uid);
					if (accountService.checkAccountInvestVal(kw,
							expextAccountMap)) {
						log.info("【账户总览】页面字段校验成功");
					} else {
						log.error("【账户总览】页面字段校验失败");
						bFinalResult = false;

					}
					// 5.用户投资后访问【理财记录】页面
					accountService.viewFinaceManagePage(kw);

					// 6.理财记录中点击所有类型
					accountService.viewAllType(kw);

					// 7.获取理财记录信息
					Map<String, String> orderInfoMap = InvestRecordDao
							.getRecordByUserCode(uid, productCode);
					if (accountService.checkValInInvestRecord(kw, productCode,
							orderInfoMap)) {
						log.info("【理财记录】校验数据成功");
					} else {
						log.error("【理财记录】校验数据失败");
						bFinalResult = false;
					}

					// 8.用户投资后点击【资金记录】页签

					accountService.viewPayRecord(kw);

					// 9.用户投资后查看【资金记录】数据

					Map<String, String> expectPayRecord = FinanceRecordDao
							.getRecordByUserCode(uid, productCode,
									FinanceType.invest);
					if (accountService.checkValInPayRecord(kw, productCode,
							expectPayRecord, FinanceType.invest)) {
						log.info("【资金记录】校验数据成功");
					} else {
						log.error("【资金记录】校验数据失败");
						bFinalResult = false;
					}

					// 10.调用登出函数
					if (publicService.logout(kw)) {
						log.info("logout successfully");
						// bResult = true;
					} else {
						log.info("logout failed");
						log.info(ScreenShotUtil.takeScreenshot(ThreadName,
								kw.driver));
					}

				} else {
					log.error("登录失败");
					bFinalResult = false;
					throw new Exception("登录失败");
				}
			}

			/*
			 * 速兑通项目截标后自动生成还款计划 1.理财记录数据会写 2.理财记录数据验证
			 */
			for (int j = 0; j < lst.size(); j++) {
				log.info("购买速兑通项目的用户个数" + lst.size());
				String uid = lst.get(j).get("uid");
				log.info("投资速兑通用户uid" + uid);
				int investAmount = Integer.parseInt(lst.get(j).get(
						"invest_amount"));
				log.info("investAmount[]=" + investAmount + "");
				// 计算投资用户收益
				String profit = investSDT.setProfit(kw, productCode,
						investAmount);
				// 更新数据库里的理财记录的收益值
				ProductDao.updateSdtProfit(productCode, uid, profit);
				// 更新数据库里的账户收益的值
				investSDT.accoutProfit(uid);

				// 获得详细的用户信息
				Map<String, String> userInfo = UserDao.getUserById(uid);
				String user = userInfo.get("mobile");
				String pwd = userInfo.get("login_pwd");
				String paymentPwd = userInfo.get("pay_pwd");
				log.info("user=[" + user + "];pwd=[" + pwd + "];paymentPwd=["
						+ paymentPwd + "]");

				// 1.用户登录
				if (publicService.login(kw, user, pwd)) {

					log.info(user + "登录成功");

					// 2.校验【账户总览】页面字段值
					Map<String, String> exptectAcctInfo = AccountDao
							.getUserAcctByUid(uid);
					if (accountService.checkAccountInvestVal(kw,
							exptectAcctInfo)) {
						log.info("【账户总览】页面字段校验成功");
					} else {
						log.error("【账户总览】页面字段校验失败");
						bFinalResult = false;
					}
					// 5.用户投资后访问【理财记录】页面
					accountService.viewFinaceManagePage(kw);

					// 6.理财记录中点击所有类型
					accountService.viewAllType(kw);

					// 7.获取理财记录信息
					Map<String, String> orderInfoMap = InvestRecordDao
							.getRecordByUserCode(uid, productCode);
					if (accountService.checkValInInvestRecord(kw, productCode,
							orderInfoMap)) {
						log.info("【理财记录】校验数据成功");
					} else {
						log.error("【理财记录】校验数据失败");
						bFinalResult = false;
					}

					// 10.调用登出函数
					if (publicService.logout(kw)) {
						log.info("logout successfully");

					} else {
						log.info("logout failed");
						log.info(ScreenShotUtil.takeScreenshot(ThreadName,
								kw.driver));
					}

				} else {
					log.error("登录失败");
					bFinalResult = false;
					throw new Exception("登录失败");
				}
			}

			Thread.sleep(3000);
			// 11.速兑通项目累计数据会写
			log.info("successCount" + successCount);
			log.info("变现总金额" + haveCashMoney);
			double count_plant_fee = haveCashMoney * 2 / 1000;

			log.info("ori_uid=[" + ori_uid + "];lst.size()=[" + lst.size()
					+ "];productCode=[" + productCode + "];haveCashMoney=["
					+ haveCashMoney + "];count_plant_fee=[" + count_plant_fee
					+ "]");

			String count_plant_feeDb = MathUtil.round2(count_plant_fee);
			String haveCashMoneyDb = MathUtil.round2(haveCashMoney);
			String countDb = MathUtil.round2(lst.size());

			ProductDao.insertSdtRecord(ori_uid, countDb, productCode,
					haveCashMoneyDb, count_plant_feeDb);
			log.info("ori_uid=[" + ori_uid + "];count_plant_feeDb=["
					+ count_plant_feeDb + "];productCode=[" + productCode
					+ "];haveCashMoneyDb=[" + haveCashMoneyDb + "];countDb=["
					+ countDb + "]");

			// 12.原始变现者数据库回写：账户概括，资金记录（平台费）
			Map<String, String> ori_exptectAcctInfo = AccountDao
					.getUserAcctByUid(ori_uid);
			log.info("账户余额" + ori_exptectAcctInfo.get("balance"));

			accountService.sdtFeedataWriteBack(ori_exptectAcctInfo,
					productCode, count_plant_fee, ori_uid);
			investSDT.countProfit(kw, productCode, ori_uid, prj_name);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			bFinalResult = false;
			log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}
		AssertJUnit.assertTrue(bFinalResult);
	}
}
