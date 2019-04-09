/**
 * 
 */
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
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.FinanceType;
import com.autotest.service.xhhService.InvestSDTService;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.ScreenShotUtil;

/**
 * @author 000738
 * 
 */
public class YYS007_Repay_ViewAccount extends TestDriver {
	public static KeyWords kw;
	// public static boolean bResult;
	public static boolean bFinalResult = true;
	private String productNumber = "SDT001";
	private String investProduct_id = "YYS007";
	private String user_numberSdt = "u12";
	private String user_numberOri = "u13";

	@Test
	public void test() {
		PubXhhService publicService = new PubXhhService();
		UserAccountService accountService = new UserAccountService(log);

		InvestSDTService investSDT = new InvestSDTService(log);
		kw = new KeyWords(driver, log);

		try {
			// 获取YYS项目名称：code
			Map<String, String> proinfo = ProductDao.getProByCode((ProductDao
					.productIdSelectProductInfo(investProduct_id)
					.get("product_name")));
			String code = proinfo.get("prj_name");

			// 根据productNumber获得SDT产品名称
			String prj_name = ProductTempDao.getProductCodeByNumber(
					productNumber).get("product_code");
			log.info("产品名称" + prj_name);
			/**
			 * 用户U12的三处验证
			 */
			// 获取变现项目者的uid u12
			String sdt_uid = ProductDao.selectCashUser(user_numberSdt).get(
					"uid");
			log.info("变现用户ID" + sdt_uid);
			// 变现者账户概括，理财记录，资金记录回写
			investSDT.sdtRepayAccount(prj_name, sdt_uid, code);

			// 获取用户详细信息
			Map<String, String> userInfo = UserDao.getUserById(sdt_uid);
			String user = userInfo.get("mobile");
			String pwd = userInfo.get("login_pwd");
			String paymentPwd = userInfo.get("pay_pwd");
			log.info("user=[" + user + "];pwd=[" + pwd + "];paymentPwd=["
					+ paymentPwd + "]");
			if (publicService.login(kw, user, pwd)) {

				log.info(user + "登录成功");

				// 4. 用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值
				Map<String, String> expextAccountMap = AccountDao
						.getUserAcctByUid(sdt_uid);
				if (accountService.checkAccountInvestVal(kw, expextAccountMap)) {
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
						.getRecordByUserCode(sdt_uid, code);
				if (accountService.checkValInInvestRecord(kw, code,
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
						.getRecordByUserCode(sdt_uid, code, FinanceType.receive);
				if (accountService.checkValInPayRecord(kw, code,
						expectPayRecord, FinanceType.receive)) {
					log.info("【资金记录】校验数据成功");
				} else {
					log.error("【资金记录】校验数据失败");
					bFinalResult = false;
				}

				// 8.调用登出函数
				if (publicService.logout(kw)) {
					log.info("logout successfully");
				} else {
					log.error("logout failed");
					throw new Exception("登出失败");
				}

			} else {
				log.error("登录失败");
				bFinalResult = false;
				throw new Exception("登录失败");
			}
			/**
			 * U13三处验证
			 */
			// 获取普通投资项目者的uid u13
			String ori_uid = ProductDao.selectCashUser(user_numberOri).get(
					"uid");
			log.info("用户ID" + ori_uid);
			// 变现者账户概括，理财记录，资金记录回写
			investSDT.repayAccount(code, ori_uid);
			// 获取用户详细信息
			Map<String, String> userInfo1 = UserDao.getUserById(ori_uid);
			String user1 = userInfo1.get("mobile");
			String pwd1 = userInfo1.get("login_pwd");
			String paymentPwd1 = userInfo1.get("pay_pwd");
			log.info("user=[" + user1 + "];pwd=[" + pwd1 + "];paymentPwd=["
					+ paymentPwd1 + "]");
			if (publicService.login(kw, user1, pwd1)) {

				log.info(user + "登录成功");

				// 4. 用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值
				Map<String, String> expextAccountMap = AccountDao
						.getUserAcctByUid(ori_uid);
				if (accountService.checkAccountInvestVal(kw, expextAccountMap)) {
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
						.getRecordByUserCode(ori_uid, code);
				if (accountService.checkValInInvestRecord(kw, code,
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
						.getRecordByUserCode(ori_uid, code, FinanceType.receive);
				if (accountService.checkValInPayRecord(kw, code,
						expectPayRecord, FinanceType.receive)) {
					log.info("【资金记录】校验数据成功");
				} else {
					log.error("【资金记录】校验数据失败");
					bFinalResult = false;
				}

				// 8.调用登出函数
				if (publicService.logout(kw)) {
					log.info("logout successfully");
				} else {
					log.error("logout failed");
					throw new Exception("登出失败");
				}

			} else {
				log.error("登录失败");
				bFinalResult = false;
				throw new Exception("登录失败");
			}

			/**
			 * U14,15,16三处验证
			 */
			// 获取普通投资项目者的uid u14,15,16
			List<Map<String, String>> lstOriSdt = ProductDao.oriSdtcUid(
					productNumber, investProduct_id);
			for (int i = 0; i < lstOriSdt.size(); i++) {
				String userNumber = lstOriSdt.get(i).get("user_number");
				log.info("用户Number" + userNumber);

				String mid_uid = ProductDao.getUserInfo(userNumber).get("uid");
				log.info("变现用户ID" + mid_uid);
				// 获取普通投资项目者的回写
				investSDT.sdtRepayInvest(code, mid_uid, prj_name);

				// 获取用户详细信息
				Map<String, String> userInfo2 = UserDao.getUserById(mid_uid);
				String user2 = userInfo2.get("mobile");
				String pwd2 = userInfo2.get("login_pwd");
				String paymentPwd2 = userInfo2.get("pay_pwd");
				log.info("user=[" + user2 + "];pwd=[" + pwd2 + "];paymentPwd=["
						+ paymentPwd2 + "]");
				if (publicService.login(kw, user2, pwd2)) {

					log.info(user + "登录成功");

					// 4. 用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值
					Map<String, String> expextAccountMap = AccountDao
							.getUserAcctByUid(mid_uid);
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
							.getRecordByUserCode(mid_uid, code);
					if (accountService.checkValInInvestRecord(kw, code,
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
							.getRecordByUserCode(mid_uid, code,
									FinanceType.receive);
					if (accountService.checkValInPayRecord(kw, code,
							expectPayRecord, FinanceType.receive)) {
						log.info("【资金记录】校验数据成功");
					} else {
						log.error("【资金记录】校验数据失败");
						bFinalResult = false;
					}
					// 5.用户投资后访问【理财记录】页面
					accountService.viewFinaceManagePage(kw);

					// 6.理财记录中点击所有类型
					accountService.viewAllType(kw);

					// 7.获取理财记录信息

					Map<String, String> orderInfoMap1 = InvestRecordDao
							.getRecordByUserCode(mid_uid, prj_name);
					if (accountService.checkValInInvestRecord(kw, prj_name,
							orderInfoMap1)) {
						log.info("【理财记录】校验数据成功");
					} else {
						log.error("【理财记录】校验数据失败");
						bFinalResult = false;
					}
					// 8.用户投资后点击【资金记录】页签

					accountService.viewPayRecord(kw);

					// 9.用户投资后查看【资金记录】数据

					Map<String, String> expectPayRecord1 = FinanceRecordDao
							.getRecordByUserCode(mid_uid, prj_name,
									FinanceType.receive);
					if (accountService.checkValInPayRecord(kw, prj_name,
							expectPayRecord1, FinanceType.receive)) {
						log.info("【资金记录】校验数据成功");
					} else {
						log.error("【资金记录】校验数据失败");
						bFinalResult = false;
					}

					// 8.调用登出函数
					if (publicService.logout(kw)) {
						log.info("logout successfully");
					} else {
						log.error("logout failed");
						throw new Exception("登出失败");
					}

				} else {
					log.error("登录失败");
					bFinalResult = false;
					throw new Exception("登录失败");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			bFinalResult = false;
			log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}
		AssertJUnit.assertTrue(bFinalResult);
	}
}
