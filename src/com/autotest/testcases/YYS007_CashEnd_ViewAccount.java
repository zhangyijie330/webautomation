package com.autotest.testcases;

import java.util.List;
import java.util.Map;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.ProductDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.service.xhhService.Fastcash;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.ScreenShotUtil;

public class YYS007_CashEnd_ViewAccount extends TestDriver {
	public static KeyWords kw;
	// public static boolean bResult;
	public static boolean bFinalResult = true;
	private String user_number = "u12";
	private String SdtProduct_id = "SDT001";
	private String Product_id = "YYS007";
	private Map<String, String> String;

	@Test()
	public void test() {
		PubXhhService pubXhhService = new PubXhhService();
		UserAccountService accountService = new UserAccountService(log);
		Fastcash fc = new Fastcash(log);
		kw = new KeyWords(driver, log);
		try {
			// 通过user_number获得投资项目名称，通过项目名称获得项目信息
			Map<String, String> proinfo = ProductDao
					.productIdSelectProductInfo(SdtProduct_id);
			String sdt_name = proinfo.get("product_name");
			log.info("速兑通项目名称" + sdt_name);

			// 获取用户uid
			String uid = ProductDao.getUserInfo(user_number).get("uid");
			log.info("用户uid" + uid);
			// 通过uid获取用户详细信息
			Map<String, String> user = ProductDao.getCashFastUserInfo(uid);
			String mobile = user.get("mobile");
			String password = user.get("login_pwd");

			// 获取用户变现信息
			String user_fastmoney = ProductDao.selectCashFastPrj(Product_id)
					.get("user_fastmoney");
			log.info("变现金额" + user_fastmoney);
			String invest_product = ProductDao.productIdSelectProductInfo(
					Product_id).get("product_name");
			log.info("被投资项目" + invest_product);

			if (pubXhhService.login(kw, mobile, password)) {
				log.info(mobile + "登录成功");

				// 1.用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值

				Map<String, String> expextAccountMap = AccountDao
						.getUserAcctByUid(uid);
				if (accountService.checkAccountInvestVal(kw, expextAccountMap)) {
					log.info("【账户总览】页面字段校验成功");
				} else {
					log.error("【账户总览】页面字段校验失败");
					bFinalResult = false;

				}
				// 2.校验回款日历
				String date = "";
				String amount = "";
				String count = "";
				String status = "";
				List<Map<String, String>> relstCalendar = pubXhhService
						.mergeList(uid);
				for (Map<String, String> mapCalendar : relstCalendar) {
					date = mapCalendar.get("date");
					amount = mapCalendar.get("amount");
					count = mapCalendar.get("count");
					status = mapCalendar.get("status");
					if (pubXhhService.checkReturnMoneyCalendar(kw, date, count,
							amount, status)) {
						log.info("回款日历校验成功！");
					} else {
						log.error("回款日历校验失败！");
						bFinalResult = false;

					}
				}

				// 3.用户点击【投资管理】页面
				accountService.viewFinaceManagePage(kw);
				log.info("投资管理");
				// // 4.理财记录中点击所有类型
				// accountService.viewAllType(kw);
				//
				// // 5.获取理财记录信息
				// Map<String, String> orderInfoMap = InvestRecordDao
				// .getRecordByUserCode(uid, sdt_name);
				// // 6.校验理财条数
				// if (accountService.checkRowCount(kw, 1)) {
				// log.info("【理财记录】校验记录条数成功");
				// } else {
				// log.error("【理财记录】校验记录条数失败");
				// }
				//
				// // 7.校验理财记录信息
				// if (accountService.checkValInInvestRecord(kw, sdt_name,
				// orderInfoMap)) {
				// log.info("【理财记录】校验数据成功");
				// } else {
				// log.error("【理财记录】校验数据失败");
				// }

				// 8.点击变现借款页签
				fc.clickCashManage(kw);
				log.info("点击变现管理");
				// 9.点击已变现页签
				fc.clickCashFasted(kw);

				// 10.已变现列表值的验证
				if (fc.checkCashFasted(kw, user_fastmoney, sdt_name)) {
					log.info("已变现列表值验证成功");
				} else {
					log.error("已变现列表值验证失败");
					bFinalResult = false;
				}
				/**
				 * 资金记录验证
				 */
				// 1.用户点击【资金记录】页签
				accountService.viewPayRecord(kw);
				// 2.点击【变现交易】页签
				fc.clickCashButton(kw);
				// 3.验证手续费
				bFinalResult = fc.checkCashFee(kw, sdt_name);
				// 4.验证资金记录
				bFinalResult = fc.sdtFinanceRecord(kw, sdt_name);

				// 调用登出函数
				if (pubXhhService.logout(kw)) {
					log.info("logout successfully");
				} else {
					log.error("logout failed");
					bFinalResult = false;
					log.info(ScreenShotUtil.takeScreenshot(ThreadName,
							kw.driver));
					throw new Exception("登出失败");
				}
			} else {
				log.error("登录失败");
				bFinalResult = false;
				log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
				throw new Exception("登录失败");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			bFinalResult = false;
			log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}
		AssertJUnit.assertTrue(bFinalResult);
	}

}
