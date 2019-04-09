package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.ProductDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.service.xhhService.Fastcash;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.ScreenShotUtil;

/**
 * 
 * 1-1循环：U17查看变现投资结束后的账户信息xhh-2579 ~ xhh-2589 1-2循环：U19查看变现投资结束后的账户信息xhh-7156 ~
 * xhh-7167 1-3循环：U20查看变现投资结束后的账户信息xhh-7133 ~ xhh-7143
 * 
 * @author 002194
 * 
 */
public class YYS008_CashEnd_ViewAccount_U17 extends TestDriver {
	public static KeyWords	kw;
	public static boolean	bResult;
	public static boolean	bFinalResult	= true;
	private String			Product_id		= "YYS008";

	@Test()
	public void test() throws SQLException {
		List<String> userNumberList = new ArrayList<String>();
		userNumberList.add("u17");
		userNumberList.add("u20");
		userNumberList.add("u19");

		List<String> sdtProductIdList = new ArrayList<String>();
		sdtProductIdList.add("SDT002");
		sdtProductIdList.add("SDT005");
		sdtProductIdList.add("SDT004");

		PubXhhService pubXhhService = new PubXhhService();
		UserAccountService accountService = new UserAccountService(log);
		Fastcash fc = new Fastcash(log);
		kw = new KeyWords(driver, log);
		try {
			for (int i = 0; i < userNumberList.size(); i++) {
				// 通过user_number获得投资项目名称，通过项目名称获得项目信息
				Map<String, String> proinfo = ProductDao
						.productIdSelectProductInfo(sdtProductIdList.get(i));
				String sdt_name = proinfo.get("product_name");
				log.info("速兑通项目名称" + sdt_name);

				// 获取用户uid
				String uid = ProductDao.getUserInfo(userNumberList.get(i)).get(
						"uid");

				// 通过uid获取用户详细信息
				Map<String, String> user = ProductDao.getCashFastUserInfo(uid);
				String mobile = user.get("mobile");
				String password = user.get("login_pwd");

				// 获取用户变现信息
				String user_fastmoney = ProductDao.selectCashFastUser(
						userNumberList.get(i)).get("user_fastmoney");
				log.info("变现金额" + user_fastmoney);
				String invest_product = ProductDao.productIdSelectProductInfo(
						Product_id).get("product_name");
				log.info("被投资项目" + invest_product);

				if (pubXhhService.login(kw, mobile, password)) {
					log.info(userNumberList.get(i) + "登录成功");

					// 1.用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值

					Map<String, String> expextAccountMap = AccountDao
							.getUserAcctByUid(uid);
					if (accountService.checkAccountInvestVal(kw,
							expextAccountMap)) {
						log.info(userNumberList.get(i) + "【账户总览】页面字段校验成功");
					} else {
						log.error(userNumberList.get(i) + "【账户总览】页面字段校验失败");
						bResult = false;
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
						if (pubXhhService.checkReturnMoneyCalendar(kw, date,
								count, amount, status)) {
							log.info(userNumberList.get(i) + "回款日历校验成功！");
						} else {
							log.error(userNumberList.get(i) + "回款日历校验失败！");
							bResult = false;
						}
					}

					// 3.用户点击【投资管理】页面
					accountService.viewFinaceManagePage(kw);

					// 4.理财记录中点击所有类型
					accountService.viewAllType(kw);

					// // 5.获取理财记录信息
					// Map<String, String> orderInfoMap = InvestRecordDao
					// .getRecordByUserCode(uid, sdt_name);
					// 6.校验理财条数
					// if (accountService.checkRowCount(kw, 1)) {
					// log.info("【理财记录】校验记录条数成功");
					// } else {
					// log.error("【理财记录】校验记录条数失败");
					// }

					// 7.校验理财记录信息
					// if (accountService.checkValInInvestRecord(kw, sdt_name,
					// orderInfoMap)) {
					// log.info("【理财记录】校验数据成功");
					// } else {
					// log.error("【理财记录】校验数据失败");
					// }

					// 8.点击变现借款页签
					fc.clickCashManage(kw);

					// 9.点击已变现页签
					fc.clickCashFasted(kw);

					// 10.已变现列表值的验证
					if (fc.checkCashFasted(kw, user_fastmoney, sdt_name)) {
						log.info(userNumberList.get(i) + "已变现列表值验证成功");
					} else {
						log.error(userNumberList.get(i) + "已变现列表值验证失败");
						bResult = false;
					}
					/**
					 * 资金记录验证
					 */
					// 1.用户点击【资金记录】页签
					accountService.viewPayRecord(kw);
					// 2.点击【变现交易】页签
					fc.clickCashButton(kw);
					// 3.验证手续费
					bResult = fc.checkCashFee(kw, sdt_name);
					// 4.验证资金积累
					bResult = fc.sdtFinanceRecord(kw, sdt_name);

					// 调用登出函数
					if (pubXhhService.logout(kw)) {
						log.info(userNumberList.get(i) + " 登出成功");
					} else {
						log.error(userNumberList.get(i) + " 登出失败");
						log.error(ScreenShotUtil.takeScreenshot(ThreadName,
								kw.driver));
					}

					bFinalResult = bFinalResult & bResult;

				} else {
					bFinalResult = false;
					log.error(ScreenShotUtil.takeScreenshot(ThreadName,
							kw.driver));
					throw new Exception(userNumberList.get(i) + "登录失败");
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
