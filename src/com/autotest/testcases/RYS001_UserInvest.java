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
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.DateUtils;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.ThreadUtil;

/**
 * 用户投资用例：日益升，1天募集期，到期还付本息
 * 
 * @author sunlingyun
 * 
 */
public class RYS001_UserInvest extends TestDriver {

	public static KeyWords kw;
	public static boolean bFinalResult = true;
	private String productNumber = "RYS001";
	// 投资起始用户，指定从顺序的第几个用户开始跑
	private int startUserNo = 1;
	// 等待开标,如为false则为修改服务器时间
	private boolean waitForInvest = true;

	@Test
	public void test() {
		PubXhhService pubXhhService = new PubXhhService();
		UserAccountService accountService = new UserAccountService(log);
		InvestFinanceService financeService = new InvestFinanceService(log);
		kw = new KeyWords(driver, log);

		try {
			// 获得产品编号
			String productCode = ProductTempDao.getProductCodeByNumber(
					productNumber).get("product_code");
			// 获得投资该产品的用户id与投资金额
			List<Map<String, String>> lst = UserTempDao
					.getInvestUserByProNo(productNumber);
			for (int i = startUserNo - 1; i < lst.size(); i++) {
				String uid = lst.get(i).get("uid");
				int investAmount = Integer.parseInt(lst.get(i).get(
						"invest_amount"));
				// 获得详细的用户信息
				Map<String, String> userInfo = UserDao.getUserById(uid);
				String user = userInfo.get("mobile");
				String pwd = userInfo.get("login_pwd");
				String paymentPwd = userInfo.get("pay_pwd");

				// 数据库获取产品数据
				Map<String, String> expectProdInfo = ProductDao
						.getProByCode(productCode);
				/*
				 * 若未到开标时间修改服务器时间至开标时间
				 */
				if ((!waitForInvest) && i == 0) {
					// 获取开标时间与服务器当前时间
					String investStartTime = expectProdInfo
							.get("start_bid_time");
					boolean isEarly = DateUtils.isEarlyThan(
							SSHUtil.sshCurrentTime(log), investStartTime);
					// 若未到开标时间修改服务器时间至开标时间
					if (isEarly) {
						String changeTime = DateUtils.getSomeDayAddMinute2(
								investStartTime, 1);
						SSHUtil.sshChangeTime(log, changeTime);
					}
				}

				if (pubXhhService.login(kw, user, pwd)) {
					log.info(user + "登录成功");
					/*
					 * 用户登陆成功后，查看【账户总览】页面数据
					 */
					// 1.校验【账户总览】页面字段值
					Map<String, String> exptectAcctInfo = AccountDao
							.getUserAcctByUid(uid);
					if (accountService.checkAccountInvestVal(kw,
							exptectAcctInfo)) {
						log.info("【账户总览】页面字段校验成功");
					} else {
						log.error("【账户总览】页面字段校验失败");
						bFinalResult = false;
					}
					/*
					 * 用户登陆成功后，访问【理财记录】页面
					 */
					// 1.点击头像左侧的【投资管理】入口
					accountService.viewFinaceManagePage(kw);
					/*
					 * 用户登陆成功后，查看【理财记录】页面数据
					 */
					// 1.点击【所有】入口并检查理财记录列表数据
					if (accountService.checkNoInvertRecordExist(kw)) {
						log.info("【投资管理】不存在理财记录");
					} else {
						log.error("【投资管理】存在理财记录");
						bFinalResult = false;
					}
					/*
					 * 用户登陆成功后，访问【资金记录】页面
					 */
					// 1.点击左侧菜单栏的【资金记录】入口，并检查【资金记录】列表字段及字段值
					if (accountService.checkNoPayRecordExist(kw)) {
						log.info("【投资管理】不存在资金记录");
					} else {
						log.error("【投资管理】存在资金记录");
						bFinalResult = false;
					}
					// 等待投标开始
					if (waitForInvest && i == 0) {
						String investStartTime = expectProdInfo
								.get("start_bid_time");
						boolean isEarly = DateUtils.isEarlyThan(
								SSHUtil.sshCurrentTime(log), investStartTime);
						if (isEarly) {
							int gap = (int) DateUtils.differSecondTime(
									SSHUtil.sshCurrentTime(log),
									investStartTime);
							ThreadUtil.sleep(gap + 5);

						}
					}
					/*
					 * 访问【投资理财-投资项目-精品推荐】页面
					 */
					// 1.点击顶部导航拦【投资理财】入口
					financeService.viewInvertListPage(kw);
					log.info("查看投资理财界面");
					/*
					 * 访问【投资理财-投资项目-日益升】页面
					 */
					if (financeService.viewRysList(kw)) {
						log.info("查看日益升列表");
					} else {
						log.error("查看日益升列表失败");
					}
					/*
					 * 校验【日益升】列表展示的保理项目字段以及值
					 */

					// 查找项目
					if (financeService.findProductByPaging(kw, productCode)) {

						String balance = expectProdInfo.get("balance");
						// 1.检查项目[RYS20160718001]右侧的项目剩余投资
						if (financeService.checkProductBalance(kw, productCode,
								balance)) {
							log.info("【日益升】产品余额校验成功");
						} else {
							log.error("【日益升】产品余额校验失败");
							bFinalResult = false;
						}
						// 2.检查投资进度
						String progress = expectProdInfo.get("progress");

						if (financeService.checkProgressRate(kw, productCode,
								progress)) {
							log.info("【日益升】产品投资比例校验成功");
						} else {
							log.error("【日益升】产品投资比例校验失败");
							bFinalResult = false;
						}

						/*
						 * 用户访问投资详情页面
						 */
						// 1.用户点击[立即投资]按钮
						financeService.selectInvestProduct(kw, productCode);
						/*
						 * 详情页部分字段校验
						 */
						// 1.校验项目名称[日益升-鑫股一号RYS20160718001]下字段值
						DiscountType discountType = financeService
								.useBonusOrCoupons(exptectAcctInfo);
						// 设置需要校验的信息
						boolean checkInvestAmount = false;
						boolean checkCollect = true;
						boolean checkIncomePer10Thous = true;
						boolean checkRemainAmount = true;
						boolean checkEndTime = true;
						boolean checkRate = false;
						boolean checkMinInvestAmount = false;
						boolean checkStepInvestAmount = false;
						boolean checkMaxInvestAmount = false;
						boolean checkAmount = false;
						boolean checkGuardWay = false;
						boolean checkProfitStart = false;
						boolean checkDateUsed = false;
						boolean checkRepayType = false;
						boolean checkRepayDate = false;
						boolean checkSupportCash = false;
						boolean checkEarlyClose = false;
						boolean checkExtend = false;
						boolean checkEarlyRepay = false;
						boolean checkAutoRatio = false;
						boolean checkAcctBalance = false;
						boolean checkProfit = false;
						if (financeService.checkMapInProDetail(kw,
								expectProdInfo, exptectAcctInfo,
								checkInvestAmount, checkCollect,
								checkIncomePer10Thous, checkRemainAmount,
								checkEndTime, checkRate, checkMinInvestAmount,
								checkStepInvestAmount, checkMaxInvestAmount,
								checkAmount, checkGuardWay, checkProfitStart,
								checkDateUsed, checkRepayType, checkRepayDate,
								checkSupportCash, checkEarlyClose, checkExtend,
								checkEarlyRepay, checkAutoRatio,
								checkAcctBalance, checkProfit, RepayWay.E,
								discountType)) {
							log.info("【详情页面】字段校验成功");
						} else {
							log.error("【详情页面】字段校验失败");
							bFinalResult = false;
						}
						/*
						 * 用户投资
						 */
						// 输入投资金额和支付密码
						financeService.setAmountAndPWd(kw, investAmount + "",
								paymentPwd);
						// 计算获得预计收益
						Map<String, String> expConfmPayMap = financeService
								.getExpConfmPayMap(expectProdInfo,
										exptectAcctInfo, investAmount,
										RepayWay.E, discountType);
						// 校验预估收益，投资金额，使用账户余额的值
						if (financeService.checkMapInConfirmPay(kw,
								expConfmPayMap, discountType)) {
							log.info("【投资确认】页面数据校验成功");
						} else {
							log.error("【投资确认】页面数据校验失败");
							bFinalResult = false;
						}

						/*
						 * 用户确认投资
						 */
						// 点击【确认支付】按钮
						if (financeService.payConfirmAndCheckSuccess(kw)) {
							log.info("用户" + user + "投资成功");
						} else {
							log.error("用户" + user + "投资失败");
							bFinalResult = false;
							throw new Exception("投资失败");
						}
						// 投资页面信息校验

						/*
						 * 回写需要修改的数据
						 */
						accountService.dataWriteBack(expConfmPayMap,
								exptectAcctInfo, expectProdInfo, productCode,
								investAmount, uid);
						/*
						 * 用户投资后访问【账户总览】页面
						 */
						// 点击【投资成功】页面的【查看账户】按钮
						financeService.viewAccountAndCheckPage(kw);
						/*
						 * 用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值
						 */
						Map<String, String> expextAccountMap = AccountDao
								.getUserAcctByUid(uid);
						if (accountService.checkAccountInvestVal(kw,
								expextAccountMap)) {
							log.info("【账户总览】页面字段校验成功");
						} else {
							log.error("【账户总览】页面字段校验失败");
							bFinalResult = false;

						}
						/*
						 * 用户投资后访问【理财记录】页面
						 */
						accountService.viewFinaceManagePage(kw);
						// 理财记录中点击所有类型
						accountService.viewAllType(kw);

						/*
						 * 
						 * 用户投资后查看【理财记录】数据
						 */
						// 获取理财记录信息
						Map<String, String> investInfoMap = InvestRecordDao
								.getRecordByUserCode(uid, productCode);
						if (accountService.checkValInInvestRecord(kw,
								productCode, investInfoMap)) {
							log.info("【理财记录】校验数据成功");
						} else {
							log.error("【理财记录】校验数据失败");
							bFinalResult = false;
						}

						/*
						 * 用户投资后查看【资金记录】数据
						 */
						accountService.viewPayRecord(kw);
						/*
						 * 用户投资后查看【资金记录】数据
						 */
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
					} else {
						log.error(productCode + "项目不存在");
						bFinalResult = false;
						throw new Exception("无法找到项目");
					}

					// 调用登出函数
					if (pubXhhService.logout(kw)) {
						log.info(user + "登出成功");
					} else {
						log.error(user + "登出失败");
						throw new Exception("登出失败");
					}
				} else {
					log.error(user + "登录失败");
					bFinalResult = false;
					throw new Exception("登录失败");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			bFinalResult = false;
			log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}
		AssertJUnit.assertTrue(bFinalResult);
	}
}
