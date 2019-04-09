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
import com.autotest.enums.DateType;
import com.autotest.enums.DiscountType;
import com.autotest.enums.FinanceType;
import com.autotest.enums.RepayWay;
import com.autotest.service.xhhService.InvestFinanceService;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.DateUtils;
import com.autotest.utility.PatternUtil;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * 用户投资用例：月益升，1天募集期，到期还付本息
 * 
 * @author sunlingyun
 * 
 */
public class YYS007_UserInvest extends TestDriver {

	public static KeyWords kw;
	public static boolean bFinalResult = true;
	private String productNumber = "YYS007";
	// 投资起始用户，指定从顺序的第几个用户开始跑
	private int startUserNo = 1;
	// 等待开标,如为false则为修改服务器时间
	private boolean waitForInvest = true;

	@Test
	public void test() {
		PubXhhService publicService = new PubXhhService();
		UserAccountService accountService = new UserAccountService(log);
		InvestFinanceService financeService = new InvestFinanceService(log);

		kw = new KeyWords(driver, log);
		try {
			// 当前获得产品编号
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

				if (publicService.login(kw, user, pwd)) {
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

					/*
					 * 访问【投资理财-投资项目-精品推荐】页面
					 */
					// 1.点击顶部导航拦【投资理财】入口
					financeService.viewInvertListPage(kw);
					log.info("查看投资理财界面");
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
					 * 访问【投资理财-投资项目-月益升】页面
					 */
					if (financeService.viewYysList(kw)) {
						log.info("查看月益升列表");

					} else {
						log.error("查看月益升列表失败");
					}
					/*
					 * 校验【月益升】列表展示的保理项目字段以及值
					 */

					// 查找项目
					if (financeService.findProductByPaging(kw, productCode)) {
						String balance = expectProdInfo.get("balance");
						// 1.检查项目[RYS20160718001]右侧的项目剩余投资
						if (financeService.checkProductBalance(kw, productCode,
								balance)) {
							log.info("【月益升】产品余额校验成功");
						} else {
							log.error("【月益升】产品余额校验失败");
							bFinalResult = false;
						}
						// 2.检查投资进度
						String progress = expectProdInfo.get("progress");

						if (financeService.checkProgressRate(kw, productCode,
								progress)) {
							log.info("【月益升】产品投资比例校验成功");
						} else {
							log.error("【月益升】产品投资比例校验失败");
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
						// 1.校验项目名称[月益升-鑫股一号YY.....]下两行的字段及字段值
						boolean checkInvestAmount = true;
						boolean checkCollect = true;
						boolean checkIncomePer10Thous = true;
						boolean checkRemainAmount = true;
						boolean checkEndTime = false;
						boolean checkRate = true;
						boolean checkMinInvestAmount = true;
						boolean checkStepInvestAmount = true;
						boolean checkMaxInvestAmount = true;
						boolean checkAmount = true;
						boolean checkGuardWay = true;
						boolean checkProfitStart = true;
						boolean checkDateUsed = true;
						boolean checkRepayType = true;
						boolean checkRepayDate = true;
						boolean checkSupportCash = true;
						boolean checkEarlyClose = true;
						boolean checkExtend = true;
						boolean checkEarlyRepay = true;
						boolean checkAutoRatio = true;
						boolean checkAcctBalance = true;
						boolean checkProfit = true;
						DiscountType discountType = financeService
								.useBonusOrCoupons(exptectAcctInfo);
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

						// 2.还款计划
						financeService.viewRepayPlan(kw);
						String term = expectProdInfo.get("term");
						if (StringUtils.getStrDateType(term) == DateType.month) {
							term = PatternUtil.getDigestMatch(term) + "个月";
						}
						String amount = StringUtils.strWithComma(expectProdInfo
								.get("demand_amount"));
						String expDesc = "以下数据根据\"投资金额" + amount + "元,期限"
								+ term + "\"计算";
						if (financeService.checkRepayPlanDesc(kw, expDesc)) {
							log.info("【还款计划】描述校验成功");
						} else {
							log.error("期望描述：" + expDesc);
							log.error("【还款计划】描述校验失败");
							bFinalResult = false;
						}
						String demandAmount = expectProdInfo
								.get("demand_amount");
						Map<String, String> expMap = financeService
								.getRepayPlanCollectRow(productCode,
										demandAmount);
						if (financeService.checkRepayPlanByRow(kw, 1, expMap)) {
							log.info("【还款计划】募集期资金占用费校验成功");
						} else {
							log.error("【还款计划】募集期资金占用费校验失败");
							bFinalResult = false;
						}
						if (financeService.checkRepayPlan(kw, productCode)) {
							log.info("【还款计划】正常还款日期校验成功");
						} else {
							log.error("【还款计划】正常还款日期校验失败");
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
							log.info("用户投资成功");
						} else {
							log.error("用户投资失败");
							bFinalResult = false;
							throw new Exception("投资失败");
						}
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
						Map<String, String> orderInfoMap = InvestRecordDao
								.getRecordByUserCode(uid, productCode);
						// 校验信息
						if (accountService.checkValInInvestRecord(kw,
								productCode, orderInfoMap)) {
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
						// 校验信息
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
						throw new Exception("项目不存在");
					}
					// 调用登出函数
					if (publicService.logout(kw)) {
						log.info(user + "登出成功");
					} else {
						log.error(user + "登出失败");
						bFinalResult = false;
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
