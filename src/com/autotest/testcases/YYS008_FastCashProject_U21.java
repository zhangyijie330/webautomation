package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.FinanceRecordDao;
import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.ProductTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.FinanceType;
import com.autotest.service.xhhService.Fastcash;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.DateUtils;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.StringUtils;

/**
 * U21 用户投资的速兑通项目再次变现操作
 * 
 * u21 发起变现操作 xhh-7188 ~ xhh-7195，变现后账户查看xhh-7197 ~ xhh-7205
 * 
 * @author 002194
 * 
 */
public class YYS008_FastCashProject_U21 extends TestDriver {
	public static KeyWords	kw;
	public static boolean	result;
	private static String	className		= "YYS008_FastCashProject_U21 ";
	private static String	productId		= "SDT002";
	private static String	userNumber		= "u21";
	private static String	product_id		= "SDT006";
	private static String	YYSproductId	= "YYS008";

	/**
	 * 
	 */
	@Test
	public void test() {
		// 标识当前用例是否验证通过
		result = true;
		try {
			// 通过product_id获得投资项目名称，通过项目名称获得项目信息
			log.info(className + userNumber + " 获取项目信息");
			Map<String, String> proinfo = ProductDao
					.getProByCode(ProductTempDao.getProductCodeByNumber(
							productId).get("product_code"));
			String projectName = proinfo.get("prj_name");
			String repayTime = proinfo.get("repay_date");

			Map<String, String> yysProinfo = ProductDao
					.getProByCode(ProductTempDao.getProductCodeByNumber(
							YYSproductId).get("product_code"));
			String end_bid_time = yysProinfo.get("end_bid_time");

			// 修改服务器时间为项目用款期的第127天
			log.info(className + userNumber + " 修改服务器时间");
			Fastcash fastcash = new Fastcash(log);
			fastcash.changTime2(end_bid_time, 127);

			// 计算速兑通剩余天数
			String startTime = SSHUtil.sshCurrentDate(log);
			int residueTime = DateUtils.differDays2(startTime, repayTime);

			UserAccountService userAccountService = new UserAccountService(log);
			// U17登录鑫合汇主站（test9环境）
			kw = new KeyWords(driver, log);
			// 通过user_number获取用户uid,通过uid获取用户详细信息
			String uid = ProductDao.selectCashUser(userNumber).get("uid");
			Map<String, String> user = ProductDao.getCashFastUserInfo(uid);
			String userName = user.get("mobile");
			String pwd = user.get("login_pwd");
			String payPasswd = user.get("pay_pwd");

			PubXhhService pubXhhService = new PubXhhService();
			if (!pubXhhService.login(kw, userName, pwd)) {
				log.error(className + userNumber + " 登录失败！");
				throw new Exception();
			} else {
				log.info(className + userNumber + " 登录成功！");
			}

			// 点击【投资管理】
			log.info(className + userNumber + " 投资管理");
			fastcash.clickInvestManage(kw);

			// 点击【变现借款】
			log.info(className + userNumber + " 变现借款");
			fastcash.clickCashManage(kw);

			// 获取页面可变现项目
			log.info(className + userNumber + " 获取页面可变现项目");
			Map<String, String> fastCashMap = fastcash.getCashList(kw,
					projectName);

			// 根据用户userNumber获取变现需要输入的参数值
			Map<String, String> cashMap = ProductDao
					.selectCashFastUser(userNumber);
			// 变现利率
			String cashRate = cashMap.get("cash_rate");
			// 变现金额=到期应付本金
			String cashMoney = cashMap.get("user_fastmoney");
			// 设置变现信息（变现页面）
			log.info(className + userNumber + " 设置变现信息");
			fastcash.setValueCashPage(kw, 0, cashRate, cashMoney, payPasswd);

			// 期望变现值:到期应付利息,平台管理费
			Map<String, String> expectedMap = fastcash.getValueFormula(kw,
					cashMoney, cashRate, String.valueOf(residueTime + 1));

			// 页面上获取变现实际值：到期应付利息，平台服务费
			Map<String, String> actualMap = fastcash.checkFastCashPage(kw);

			// 校验变现值
			if (!StringUtils.isEquals(expectedMap, actualMap, log)) {
				log.error(className + userNumber + "变现页面校验失败");
				log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
				result = false;
			} else {
				log.info(className + userNumber + "变现页面校验成功");
			}

			// 获取变现页面上的相关值
			Map<String, String> castFastValue = fastcash.CreatFastCashPage(kw);

			// 提交变现数据
			fastcash.cashSubmit(kw);

			// 校验页面是否调转成功
			String title = "投资变现-变现成功";
			String title2 = "变现借款-变现产品";
			String exceptitle = fastcash.clickManyCashPro(kw);
			log.info("exceptitle" + exceptitle);
			log.info(StringUtils.isEquals(exceptitle, title));
			log.info(StringUtils.isEquals(exceptitle, title2));
			if (StringUtils.isEquals(exceptitle, title)
					|| StringUtils.isEquals(exceptitle, title2)) {
				log.info(className + "变现页面跳转成功");
				// 速兑通项目写入数据库
				ProductDao.insertIntoCashProductInfo(
						castFastValue.get("prj_name"), fastCashMap.get("term"),
						castFastValue.get("repay_way"),
						fastCashMap.get("repay_date"), cashMoney, "速兑通",
						cashRate, "0", cashMoney, "0.00");
				// 写入product_temp
				List<Map<String, String>> cashFastMap = ProductDao
						.getCashFastId(product_id);
				if (0 == cashFastMap.size()) {
					ProductDao.insertCashFast(product_id,
							castFastValue.get("prj_name"));
				} else {
					ProductDao.updateCashFast(product_id,
							castFastValue.get("prj_name"));
				}

			} else {
				log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
				result = false;
			}

			// 用户投资后查看【账户总览】数据,并校验【账户总览】页面字段值
			userAccountService.viewUserAccountIndex(kw);
			Map<String, String> expextAccountMap = AccountDao
					.getUserAcctByUid(uid);
			if (userAccountService.checkAccountInvestVal(kw, expextAccountMap)) {
				log.info(userNumber + " 【账户总览】页面字段校验成功");
			} else {
				log.error(userNumber + " 【账户总览】页面字段校验失败");
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
					log.info(userNumber + " 回款日历验证成功!");
				} else {
					log.error(userNumber + " 回款日历验证失败!");
					result = false;
				}
			}

			// 用户变现后查看【理财记录】数据
			userAccountService.viewFinaceManagePage(kw);
			// 理财记录中点击【所有】类型
			userAccountService.viewAllType(kw);
			Map<String, String> orderInfoMap = InvestRecordDao
					.getRecordByUserCode(uid, projectName);
			if (userAccountService.checkValInInvestRecord(kw, projectName,
					orderInfoMap)) {
				log.info(userNumber + "【理财记录】校验数据成功");
			} else {
				log.error(userNumber + "【理财记录】校验数据失败");
				result = false;
			}

			// 用户变现后查看【资金记录】数据
			log.info(userNumber + " 查看【资金记录】数据");
			userAccountService.viewPayRecord(kw);
			Map<String, String> expectPayRecord = FinanceRecordDao
					.getRecordByUserCode(uid, projectName, FinanceType.invest);
			if (userAccountService.checkValInPayRecord(kw, projectName,
					expectPayRecord, FinanceType.invest)) {
				log.info(userNumber + "【资金记录】校验数据成功");
			} else {
				log.error(userNumber + "【资金记录】校验数据失败");
				result = false;
			}

			// 退出当前登录
			if (!pubXhhService.logout(kw)) {
				log.error(className + userNumber + "退出失败！");
				throw new Exception();
			} else {
				log.info(className + userNumber + "退出成功！");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
			log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}

		// 验证用例是否通过
		assertTrue(result);
	}

}
