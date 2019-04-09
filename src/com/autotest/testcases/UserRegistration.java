package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.BusinessDao;
import com.autotest.dao.UserDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.or.ObjectLib;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.MathUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

public class UserRegistration extends TestDriver {

	public static KeyWords kw;

	public static String realName = "孙晓明";
	public static String CerNo = "330721198012040257";

	// 需要注册的用户个数
	public static int userCount;
	// 注册用户在映射表的开始序号，值为1就是从u1开始映射，值为2从u2开始映射，以此类推
	public static int startUserNo;

	public static String login_pwd = "a111111";
	public static String pay_pwd = "123456";
	public static String amount = "50000.00";
	int retry = 0;
	int retryMax = 3;

	@Test
	@Parameters({ "userCountVal", "startUserNoVal" })
	public void test(String userCountVal, String startUserNoVal) {

		// 从testng.xml获取注册用户个数和起始用户的参数值,方便每个场景分别注册不同用户
		userCount = Integer.parseInt(userCountVal);
		startUserNo = Integer.parseInt(startUserNoVal);
		// 标识用例运行结果
		boolean result = true;
		PubXhhService pubService = new PubXhhService();
		try {
			// List<Map<String, String>> regUserList =
			// UserProductDao.getRegUserList();
			// int userCount = regUserList.size();
			List<String> mobileList = genRegMobileList(userCount);
			String mobile;
			String uNo;
			for (int u = 0; u < userCount;) {
				kw = new KeyWords(driver, log);
				mobile = mobileList.get(u);
				// String uNo = regUserList.get(u).get("user_number");
				uNo = "u" + (u + startUserNo);
				if (pubService.register(kw, mobile, login_pwd)) {
					log.info(uNo + " " + mobile + "注册成功");
					log.info(uNo + " " + mobile + "实名认证");
					if (!authenticate(kw)) {
						result = false;
						log.error("实名认证失败");
						throw new Exception(uNo + " " + mobile + "实名认证失败");
					}
					log.info(uNo + " " + mobile + "设置支付密码");
					if (!setPayPwd(kw, pay_pwd)) {
						result = false;
						throw new Exception(uNo + " " + mobile + "支付密码设置失败");
					}

					log.info(uNo + " " + mobile + "充值");
					BusinessDao.updateUserAmount(mobile, amount.replace(".", ""));
					ThreadUtil.sleep(2);

					// 新注册用户插入user表
					String busi_uid = BusinessDao.getUid(mobile);
					if (busi_uid != null && busi_uid != "") {
						UserDao.insertUser(busi_uid, mobile, login_pwd, pay_pwd);
					} else {
						log.error("业务数据库不存在注册用户");
						throw new Exception("业务数据库不存在注册用户");
					}

					// 新注册用户初始账户插入user_acct表
					String uid = UserDao.getIdByMobile(mobile);
					AccountDao.insertAccount(uid, amount, amount);

					// 更新用户映射表

					UserTempDao.updateUid(uid, uNo);

					// 注册成功后已自动登录，下次循环前退出登录
					if (pubService.logout(kw)) {
						log.info(uNo + " " + mobile + "退出成功");
					} else {
						log.error(uNo + " " + mobile + "退出失败");
						result = false;
					}
					u++;
					if (u != userCount) {
						ThreadUtil.sleep(110);
					}
				} else {
					log.error(uNo + " " + mobile + "注册失败");
					if (kw.isElementExist(OrUtil.getBy("register_errorTxt_xpath", ObjectLib.XhhObjectLib))) {
						switch (kw.getText(OrUtil.getBy("register_errorTxt_xpath", ObjectLib.XhhObjectLib))) {
						case "内部程序错误":
							log.error("页面提示内部程序错误");
							log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
							break;
						case "手机号码已被占用":
							log.error("页面提示手机号码被占用");
							log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
							// 手机号被占用时重新生成待注册的手机号列表
							mobileList = genRegMobileList(userCount);
							break;
						case "动态码不正确!":
							log.error("页面提示动态码输入错误");
							break;
						case "":
							if (kw.getAttribute(OrUtil.getBy("register_register_btn_xpath", ObjectLib.XhhObjectLib),
									"value").endsWith("..."))
								log.error("注册成功后未执行页面跳转");
							log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
							break;
						default:
							log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
							break;
						}
					}
					retry++;
					if (retry <= retryMax) {
						ThreadUtil.sleep(110);
						continue;
					} else {
						log.error("超过最大重试次数，退出用例");
						throw new Exception("超过最大重试次数，退出用例");
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			// 截图
			log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
			result = false;
		}
		assertTrue(result);
	}

	/**
	 * 调用接口进行实名认证
	 * 
	 * @author 001811
	 * @throws Exception
	 */
	public boolean authenticate(KeyWords kw) throws Exception {
		boolean result = false;
		realName = StringUtils.Utf8URLencode(realName);
		String authUrl = BaseConfigUtil.getHomePageURL() + "Account/IdAuth/verifyId?isAjax=1&name=" + realName
				+ "&personId=" + CerNo;
		for (int r = 0; r < retryMax;) {
			kw.open(authUrl);
			log.info("打开实名认证链接：" + authUrl);
			if (kw.getWebElement(By.xpath("/html/body")).getText().contains("\"boolen\":1")) {
				result = true;
				kw.goBack();
				break;
			} else {
				r++;
			}
		}
		return result;
	}

	/**
	 * 设置支付密码
	 * 
	 * @author 001811
	 * @param mobile
	 * @throws Exception
	 */
	public boolean setPayPwd(KeyWords kw, String payPwd) throws Exception {
		boolean result = false;
		try {
			// 点击“我的账户”
			kw.click(OrUtil.getBy("userAccount_link_id", ObjectLib.XhhObjectLib));
			// 等待页面加载
			kw.waitPageLoad();
			// 若存在新手指引浮层，则关闭
			if (kw.isElementVisible(OrUtil.getBy("account_newUserKnown_id", ObjectLib.XhhObjectLib))) {
				kw.click(OrUtil.getBy("account_newUserKnown_id", ObjectLib.XhhObjectLib));
			}
			// 点击顶部的用户中心，默认进入账户设置
			kw.click(OrUtil.getBy("account_userCenter_linkText", ObjectLib.XhhObjectLib));
			// 如果存在【客服】和【二维码】悬浮按钮，将其隐藏，避免遮盖支付密码的设置按钮
			if (kw.isElementExist(OrUtil.getBy("qqService_xpath", ObjectLib.XhhObjectLib))) {
				String jString_qs = "var ps = document.getElementById(\"qqService\");ps.parentNode.removeChild(ps);";
				String jString_ac = "var ps = document.getElementById(\"appCode\");ps.parentNode.removeChild(ps);";
				kw.executeJS(jString_qs);
				kw.executeJS(jString_ac);
			}
			kw.click(OrUtil.getBy("account_setPayPwd_id", ObjectLib.XhhObjectLib));
			kw.setValue(OrUtil.getBy("account_payPwdInput_name", ObjectLib.XhhObjectLib), payPwd);
			kw.setValue(OrUtil.getBy("account_payPwdRepeatInput_name", ObjectLib.XhhObjectLib), payPwd);
			kw.click(OrUtil.getBy("account_payPwdSubmit_xpath", ObjectLib.XhhObjectLib));
			// 如果存在【支付密码设置成功】的弹窗，先关闭
			if (kw.isElementVisible(OrUtil.getBy("success_closebtn_id", ObjectLib.XhhObjectLib))) {
				kw.click(OrUtil.getBy("success_closebtn_id", ObjectLib.XhhObjectLib));
			}
			kw.waitPageLoad();
			log.info("支付密码设置成功！");
			result = true;
		} catch (Exception e) {
			log.error("支付密码设置失败！", e);
			e.printStackTrace();
			// 截图
			log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}

		return result;
	}

	/**
	 * 生成注册手机号
	 * 
	 * @return
	 */
	public List<String> genRegMobileList(int userCount) {
		List<String> mobileList = new ArrayList<String>();
		String mobile;
		for (int i = 0; i < userCount;) {
			mobile = MathUtil.getMobile();
			if (mobile != null) {
				mobileList.add(i, mobile);
				i++;
			}
		}
		return mobileList;
	}
}
