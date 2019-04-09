package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.ProductTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.service.xhhService.Fastcash;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.ScreenShotUtil;

/**
 * U18用户变现截标后查看账户信息 xhh-7144 ~ xhh-7153
 * 
 * @author 002194
 * 
 */
public class YYS008_CashEnd_ViewAccount_U18 extends TestDriver {
	public static KeyWords	kw;
	public static boolean	result;
	private static String	className		= "YYS008_CashEnd_ViewAccount_NoInvester ";
	private static String	userNumber		= "u18";
	private static String	productId		= "YYS008";
	private static String	sdtProductId	= "SDT003";

	@Test
	public void test() {
		result = true;
		try {

			kw = new KeyWords(driver, log);
			PubXhhService pubXhhService = new PubXhhService();
			// 通过user_number获得投资项目名称，通过项目名称获得项目信息
			Map<String, String> proinfo = ProductDao
					.getProByCode(ProductTempDao.getProductCodeByNumber(
							productId).get("product_code"));
			String prj_name = proinfo.get("prj_name");
			String end_bid_time = proinfo.get("end_bid_time");

			// 修改服务器时间为变现截标日第5天
			Fastcash fastcash = new Fastcash(log);
			fastcash.changTime2(end_bid_time, 67);

			// 通过user_number获得投资项目名称，通过项目名称获得项目信息
			Map<String, String> sdt_proinfo = ProductDao
					.productIdSelectProductInfo(sdtProductId);
			String sdt_name = sdt_proinfo.get("product_name");
			log.info("速兑通项目名称" + sdt_name);

			// 通过user_number获取用户uid,通过uid获取用户详细信息
			// 获取用户变现信息
			String uid = ProductDao.getUserInfo(userNumber).get("uid");
			Map<String, String> user = ProductDao.getCashFastUserInfo(uid);
			String userName = user.get("mobile");
			String pwd = user.get("login_pwd");

			// 获取用户变现信息
			String user_fastmoney = ProductDao.selectCashFastUser(userNumber)
					.get("user_fastmoney");
			log.info("变现金额" + user_fastmoney);
			String invest_product = ProductDao.productIdSelectProductInfo(
					productId).get("product_name");
			log.info("被投资项目" + invest_product);

			// 登录鑫合汇主站
			if (!pubXhhService.login(kw, userName, pwd)) {
				log.error(className + userName + " 登录失败！");
				throw new Exception(className + userName + " 登录失败！");
			} else {
				log.info(className + userName + " 登录成功！");
			}

			// 变现后，校验【账户总览】页面数据
			Map<String, String> exptectedAcctInfo = AccountDao
					.getUserAcctByUid(uid);
			UserAccountService userAccountService = new UserAccountService(log);
			if (userAccountService.checkAccountInvestVal(kw, exptectedAcctInfo)) {
				log.info(className + userName + " 账户总览校验成功！");
			} else {
				log.error(className + userName + " 账户总览校验失败！");
				result = false;
			}

			// 变现后，校验【账户总览-回款日历】区域数据
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
					log.info(className + userName + " 回款日历验证成功!");
				} else {
					log.error(className + userName + " 回款日历验证失败!");
					result = false;
				}
			}

			// 变现后，校验【理财记录】页面
			// 点击【理财记录】
			userAccountService.viewFinaceManagePage(kw);
			// 理财记录中点击所有类型
			userAccountService.viewAllType(kw);

			// 变现后，校验【理财记录】页面
			Map<String, String> orderInfoMap = InvestRecordDao
					.getRecordByUserCode(uid, prj_name);
			if (userAccountService.checkValInInvestRecord(kw, prj_name,
					orderInfoMap)) {
				log.info(className + userName + "【理财记录】校验数据成功");
			} else {
				log.error(className + userName + "【理财记录】校验数据失败");
				result = false;
			}

			// 点击变现借款页签
			fastcash.clickCashManage(kw);

			// 点击已变现页签
			fastcash.clickCashFasted(kw);

			// 已变现列表值的验证
			if (fastcash.checkCashFasted(kw, user_fastmoney, sdt_name)) {
				log.info("已变现列表值验证成功");
			} else {
				log.error("已变现列表值验证失败");
				result = false;
			}

			// 调用登出函数
			if (pubXhhService.logout(kw)) {
				log.info(className + userName + " 退出成功！");
			} else {
				log.error(className + userName + " 退出失败！");
				log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
				throw new Exception(className + userName + " 退出失败！");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
			log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}

		assertTrue(result);

	}
}
