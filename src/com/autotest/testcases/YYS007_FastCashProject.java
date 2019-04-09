package com.autotest.testcases;

import java.util.List;
import java.util.Map;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.autotest.dao.ProductDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.service.xhhService.Fastcash;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.StringUtils;

/**
 * 用例xhh-469至用例xhh-690
 * 
 * @author 000738
 * 
 */
public class YYS007_FastCashProject extends TestDriver {
	public static KeyWords kw;
	public static boolean bResult = true;
	// public static boolean bFinalResult = true;
	private String user_number = "u12";
	private String product_id = "SDT001";
	private String investProduct_id = "YYS007";

	@Test
	public void test() {

		// 标识当前用例是否验证通过
		// boolean bResult = false;
		Fastcash fc = new Fastcash(log);
		PubXhhService pubXhhService = new PubXhhService();
		kw = new KeyWords(driver, log);
		// UserAccountService accountService = new UserAccountService(log);
		try {
			// 通过user_number获得投资项目名称，通过项目名称获得项目信息
			Map<String, String> proinfo = ProductDao.getProByCode((ProductDao
					.productIdSelectProductInfo(investProduct_id)
					.get("product_name")));
			String prj_name = proinfo.get("prj_name");
			log.info("项目名称" + prj_name);
			String repay_date = proinfo.get("repay_date");
			log.info("项目还款日期" + repay_date);
			String end_bid_time = proinfo.get("end_bid_time");
			String term = proinfo.get("term");
			log.info("项目剩余期限" + term);

			// 通过user_number获取用户uid,通过uid获取用户详细信息
			Map<String, String> user = ProductDao
					.getCashFastUserInfo((ProductDao.getUserInfo(user_number)
							.get("uid")));

			String uid = ProductDao.getUserInfo(user_number).get("uid");
			log.info("uid--");
			String mobile = user.get("mobile");
			log.info("mobile--" + mobile);
			String login_pwd = user.get("login_pwd");
			log.info("password--" + login_pwd);
			String pay_pwd = user.get("pay_pwd");

			// 获取用户变现信息
			Map<String, String> cash = ProductDao
					.selectCashFastUser(user_number);

			// 用户变现金额
			String user_fastmoney = cash.get("user_fastmoney");

			// 用户变现利率
			String cash_rate = cash.get("cash_rate");

			// 修改服务器时间
			fc.changTime(end_bid_time);
			// 登录鑫合汇网站
			if (pubXhhService.login(kw, mobile, login_pwd)) {
				log.info(mobile + "登录成功");
				// bResult = true;
				// 3.点击我的账户
				// fc.clickUserAccount(kw);

				/**
				 * 变现操作
				 */

				// 1.点击投资管理
				fc.clickInvestManage(kw);

				// 2.点击变现管理
				fc.clickCashManage(kw);

				// 3.获取可变现页面上列表的值
				Map<String, String> getlist = fc.getCashList(kw, prj_name);
				String SdtTerm = getlist.get("term");

				// 4.点击可变现项目列表里的立即变现按钮
				// fc.clickFastCash(kw, prj_name);

				// 5.输入变现页面上的值
				fc.setValueCashPage(kw, 0, cash_rate, user_fastmoney, pay_pwd);

				// 6.获取变现页面上的公式的值:到期应付利息,平台管理费
				Map<String, String> exceptedMap = fc.getValueFormula(kw,
						user_fastmoney, cash_rate, SdtTerm);

				// 7.页面上可获取的值：到期应付利息，平台服务费
				Map<String, String> destMap = fc.checkFastCashPage(kw);

				// 8.校验变现页面上的值
				if (StringUtils.isEquals(exceptedMap, destMap, log)) {
					log.info("变现页面上值校验成功");
				} else {
					log.error("变现页面上的值校验失败");
					bResult = false;
				}

				// 9.获取变现页面上的相关值
				Map<String, String> cashValue = fc.CreatFastCashPage(kw);

				// 10.提交变现数据
				fc.cashSubmit(kw);

				// 11.校验页面是否调转成功
				String title = "投资变现-变现成功";
				String title2 = "变现借款-变现产品";
				String exceptitle = fc.clickManyCashPro(kw);
				log.info("exceptitle" + exceptitle);
				log.info(StringUtils.isEquals(exceptitle, title));
				log.info(StringUtils.isEquals(exceptitle, title2));
				if (StringUtils.isEquals(exceptitle, title)
						|| StringUtils.isEquals(exceptitle, title2)) {

					log.info("页面跳转成功");

					// 12.速兑通项目写入数据库productinfo表

					ProductDao.insertIntoCashProductInfo(
							cashValue.get("prj_name"), getlist.get("term"),
							cashValue.get("repay_way"),
							getlist.get("repay_date"), user_fastmoney, "速兑通",
							cash_rate, "0", user_fastmoney, "0.00");

					// 13.如果没有速兑通类型，新加入一条写入数据库product_temp表
					List<Map<String, String>> map = ProductDao
							.getCashFastId(product_id);
					log.info("map.size的值" + map.size());
					if (map.size() == 0) {

						ProductDao.insertCashFast(product_id,
								cashValue.get("prj_name"));

					} else {
						// 14.如果有速兑通类型，更新product_temp表速兑通数据
						ProductDao.updateCashFast(product_id,
								cashValue.get("prj_name"));

					}
				} else {
					log.info("页面跳转失败");
					bResult = false;
				}

				// 8.调用登出函数
				if (pubXhhService.logout(kw)) {
					log.info("logout successfully");
				} else {
					log.error("logout failed");
					throw new Exception("登出失败");
				}

			} else {
				log.error("登录失败");
				bResult = false;
				throw new Exception("登录失败");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			bResult = false;
			log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}
		AssertJUnit.assertTrue(bResult);
	}
}