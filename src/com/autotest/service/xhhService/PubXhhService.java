package com.autotest.service.xhhService;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.autotest.dao.BusinessDao;
import com.autotest.dao.RepayPlanDao;
import com.autotest.dao.UserDao;
import com.autotest.driver.KeyWords;
import com.autotest.or.ObjectLib;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.DateUtils;
import com.autotest.utility.OrUtil;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.ThreadUtil;
import com.autotest.utility.UserAgentUtil;

/**
 * 公共功能点封装类
 * 
 * @author wb0002
 */
public class PubXhhService {

	/**
	 * 账号注册
	 * 
	 * @param kw
	 * @param userName
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public boolean register(KeyWords kw, String mobile, String pwd) throws Exception {
		boolean registerResult = false;
		// open鑫合汇首页
		kw.open(BaseConfigUtil.getHomePageURL());
		// 点击注册按钮
		kw.click(OrUtil.getBy("register_button_xpath", ObjectLib.XhhObjectLib));
		// 输入手机号码、登录密码、验证码
		kw.setValue(OrUtil.getBy("register_mobile_xpath", ObjectLib.XhhObjectLib), mobile);
		kw.setValue(OrUtil.getBy("register_password_xpath", ObjectLib.XhhObjectLib), pwd);
		// 暂停以便手动输入图片验证码
		// Thread.sleep(10000);

		long beginTime = DateUtils.getTime();
		/**
		 * while (true) { String js = "return $(\"input[name=" + "'authCode']" +
		 * "\")[0].value"; String code = kw.executeJsReturnString(js);
		 * System.out.println(code); if (code.length() == 4) { break; } long
		 * endTime = DateUtils.getTime(); if ((endTime - beginTime) > 1 * 60 *
		 * 1000) { throw new Exception("输入验证码超时!"); } ThreadUtil.sleep(1); }
		 */

		kw.setValue(OrUtil.getBy("register_authCode_xpath", ObjectLib.XhhObjectLib), "XHHA");

		// 点击获取手机动态码
		kw.click(OrUtil.getBy("register_send_btn_xpath", ObjectLib.XhhObjectLib));
		/*
		 * // 判断验证码是否输入正确，如错误，则重新输入验证码 String js =
		 * "return $(\".register-content\").text()"; String res =
		 * kw.executeJsReturnString(js); if (res.indexOf("验证码错误!") != -1) {
		 * throw new Exception("验证码错误!"); }
		 */
		// 如果存在【验证码发送成功】窗口，先关闭
		if (kw.isElementVisible(OrUtil.getBy("success_closebtn_id", ObjectLib.XhhObjectLib))) {
			kw.click(OrUtil.getBy("success_closebtn_id", ObjectLib.XhhObjectLib));
		}
		Thread.sleep(2000);

		// 业务库获取动态码
		Map<String, String> codeMap = BusinessDao.getMobileCode(mobile);
		String code = codeMap.get("code");
		// 输入手机动态码
		kw.setValue(OrUtil.getBy("register_code_xpath", ObjectLib.XhhObjectLib), code);
		// 点击注册按钮
		kw.isElementVisible(OrUtil.getBy("register_register_btn_xpath", ObjectLib.XhhObjectLib));
		kw.click(OrUtil.getBy("register_register_btn_xpath", ObjectLib.XhhObjectLib));
		ScreenShotUtil.takeScreenshot(UserAgentUtil.getUserAgent(kw), kw.driver);
		// 等待注册完成
		kw.waitPageLoad();
		ScreenShotUtil.takeScreenshot(UserAgentUtil.getUserAgent(kw), kw.driver);
		// TODO
		// 判断是否注册成功
		if (kw.isElementExist(OrUtil.getBy("account_logout_xpath", ObjectLib.XhhObjectLib))) {
			registerResult = true;
			// 等待倒计时结束
			beginTime = DateUtils.getTime();
			while (true) {
				if (!kw.isElementVisible(OrUtil.getBy("register_timer_id", ObjectLib.XhhObjectLib)))
					break;
				long endTime = DateUtils.getTime();
				if ((endTime - beginTime) > 30 * 1000) {
					break;
				}
				ThreadUtil.sleep();
			}
		}

		// 判断数据库中是否有登录的账号记录，如果没有，择该账号未注册成功

		String sql = "select * from fi_user t where t.mobile='" + mobile + "'";
		System.out.println("sql=[" + sql + "]");
		Map<String, String> map = UserDao.queryBusUser(sql);
		if (0 == map.size()) {
			// String errMsg = "用户[" + mobile + "]未注册成功,count=[" + map.size() +
			// "]";
			registerResult = false;
		}

		return registerResult;
	}

	/**
	 * case1.访问鑫合汇主站 case2:用户访问登陆页面 case3:用户填写登录信息 case4:用户提交登陆
	 * 
	 * @param kw
	 * @param userName
	 * @param pwd
	 * @return
	 * @throws Exception
	 */
	public boolean login(KeyWords kw, String userName, String pwd) throws Exception {
		System.out.println("login userName=[" + userName + "];pwd=[" + pwd + "]");
		boolean loginResult = false;
		// open鑫合汇首页
		String url = BaseConfigUtil.getHomePageURL();
		kw.open(url);
		// 点击登录按钮，打开登录界面
		kw.click(OrUtil.getBy("login_button_xpath", ObjectLib.XhhObjectLib));
		// 输入用户名和密码
		kw.setValue(OrUtil.getBy("login_username_xpath", ObjectLib.XhhObjectLib), userName);
		kw.setValue(OrUtil.getBy("login_pwd_xpath", ObjectLib.XhhObjectLib), pwd);
		// 判断是否需要输入验证码
		// if (kw.isElementVisible(OrUtil.getBy("login_autoCode_xpath",
		// ObjectLib.XhhObjectLib))) {
		// 获取验证码图片
		// TODO
		// 识别验证码图片
		// TODO
		// 输入验证码
		// TODO
		// }
		String user = kw.getAttribute(OrUtil.getBy("login_username_xpath", ObjectLib.XhhObjectLib), "value");
		System.out.println("user=[" + user + "]");
		String passWord = kw.getAttribute(OrUtil.getBy("login_pwd_xpath", ObjectLib.XhhObjectLib), "value");
		System.out.println("passWord=[" + passWord + "]");
		// 点击登录按钮
		kw.click(OrUtil.getBy("login_normalSub_xpath", ObjectLib.XhhObjectLib));
		// 等待登录完成
		kw.waitPageLoad();
		// 判断是否跳转到登录页面
		String title = kw.getTitle();
		System.out.println("title=[" + title + "]");
		if (title.equals("用户登录")) {
			System.out.println("回调登录函数");
			// 回调
			login(kw, userName, pwd);
		}
		// 判断是否登录成功
		if (kw.isElementExist(OrUtil.getBy("account_logout_xpath", ObjectLib.XhhObjectLib))) {
			loginResult = true;
		}
		for (int i = 0; i < 10; i++) {
			try {
				if (kw.isElementExist(OrUtil.getBy("importantinfo_xpath", ObjectLib.XhhObjectLib))) {
					kw.click(OrUtil.getBy("importantinfo_xpath", ObjectLib.XhhObjectLib));
					break;
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
			ThreadUtil.sleep();
		}
		return loginResult;
	}

	/**
	 * 登出
	 * 
	 * @param kw
	 * @return
	 * @throws Exception
	 */

	public boolean logout(KeyWords kw) throws Exception {

		boolean bResult = false;
		kw.click(OrUtil.getBy("account_logout_xpath", ObjectLib.XhhObjectLib));
		kw.waitPageLoad();
		// 判断是否有登录按钮，来判断是否退出成功
		if (kw.isElementExist(OrUtil.getBy("login_button_xpath", ObjectLib.XhhObjectLib))) {
			bResult = true;
		}
		return bResult;

	}

	/**
	 * 校验回款日历数据
	 * 
	 * @param kw
	 * @param date
	 * @param count
	 * @param amount
	 * @param status
	 * @return
	 * @throws Exception
	 */
	public boolean checkReturnMoneyCalendar(KeyWords kw, String date, String count, String amount, String status)
			throws Exception {
		boolean Result = false;
		// 调整日历使得与预期数据月份一致
		String[] date_temp = date.split("-");
		int year_data = Integer.parseInt(date_temp[0]);
		int month_data = Integer.parseInt(date_temp[1]);
		// 获取页面年份数据，四次获取不到则跳出
		int find_year_page_count = 0;
		while (kw.getWebElement2(OrUtil.getBy("account_calendarYear_xpath", ObjectLib.XhhObjectLib)) == null) {
			ThreadUtil.sleep();
			find_year_page_count++;
			if (find_year_page_count > 4)
				break;
		}
		int year_page = Integer.parseInt(kw.getAttribute2(
				kw.getWebElement2(OrUtil.getBy("account_calendarYear_xpath", ObjectLib.XhhObjectLib)), "value"));
		// 比较页面年份和预期年份，通过按钮调整使得他们一致
		if (year_data == year_page) {
		} else if (year_data < year_page) {
			do {
				kw.click(OrUtil.getBy("account_calendarBefore_xpath", ObjectLib.XhhObjectLib));
				ThreadUtil.sleep();
				year_page = Integer.parseInt(kw.getAttribute2(
						kw.getWebElement2(OrUtil.getBy("account_calendarYear_xpath", ObjectLib.XhhObjectLib)),
						"value"));
			} while (year_data < year_page);
		} else {
			do {
				kw.click(OrUtil.getBy("account_calendarNext_xpath", ObjectLib.XhhObjectLib));
				ThreadUtil.sleep();
				year_page = Integer.parseInt(kw.getAttribute2(
						kw.getWebElement2(OrUtil.getBy("account_calendarYear_xpath", ObjectLib.XhhObjectLib)),
						"value"));
			} while (year_data > year_page);
		}

		ThreadUtil.sleep(2);
		// 获取页面月份数据，四次获取不到则跳出
		int find_month_page_count = 0;
		while (kw.getWebElement2(OrUtil.getBy("account_calendarMonth_xpath", ObjectLib.XhhObjectLib)) == null) {
			ThreadUtil.sleep();
			find_month_page_count++;
			if (find_month_page_count > 4)
				break;
		}
		int month_page = Integer.parseInt(kw.getAttribute2(
				kw.getWebElement2(OrUtil.getBy("account_calendarMonth_xpath", ObjectLib.XhhObjectLib)), "value"));
		// 比较页面月份和预期月份，通过按钮调整使得他们一致
		if (month_data == month_page) {
		} else if (month_data < month_page) {
			do {
				kw.click(OrUtil.getBy("account_calendarBefore_xpath", ObjectLib.XhhObjectLib));
				ThreadUtil.sleep();
				month_page = Integer.parseInt(kw.getAttribute2(
						kw.getWebElement2(OrUtil.getBy("account_calendarMonth_xpath", ObjectLib.XhhObjectLib)),
						"value"));
			} while (month_data < month_page);
		} else {
			do {
				kw.click(OrUtil.getBy("account_calendarNext_xpath", ObjectLib.XhhObjectLib));
				ThreadUtil.sleep();
				month_page = Integer.parseInt(kw.getAttribute2(
						kw.getWebElement2(OrUtil.getBy("account_calendarMonth_xpath", ObjectLib.XhhObjectLib)),
						"value"));
			} while (month_data > month_page);
		}
		// 记录回款日历信息
		String info = kw.executeJsReturnString("return $(\".datepicker-text\").text()");
		String check_date = String.valueOf(month_page) + "月" + Integer.parseInt(date_temp[2]) + "日";
		count = count + "笔";
		if (status.equals("0")) {
			status = "待回";
		} else {
			status = "已回";
		}
		// 校验信息中包含的日、笔数、金额是否与预期一致
		if (info.contains(check_date) && info.contains(count) && info.contains(amount) && info.contains(status)) {
			/* && */
			Result = true;
		} else {
			Result = false;
			kw.log.error("实际值" + info);
			kw.log.error("预期日期" + check_date);
			kw.log.error("预期笔数" + count);
			kw.log.error("预期金额" + amount);
			kw.log.error("预期状态" + status);
			kw.log.error("预期值与实际值不匹配");
		}
		return Result;
	}

	/**
	 * 校验回款提醒-无消息
	 * 
	 * @param kw
	 * @param reminder
	 * @return
	 * @throws Exception
	 */
	public boolean checkReturnMoneyReminder(KeyWords kw, String reminder) throws Exception {
		boolean Result = false;
		// 点击回款提醒，记录回款提醒信息
		kw.click(OrUtil.getBy("account_returnMoneyReminder_id", ObjectLib.XhhObjectLib));
		ThreadUtil.sleep();
		String info = kw.executeJsReturnString("return $(\".msg\").text()");
		// 判断页面回款信息是否包含参数提供的消息
		if (info.contains(reminder)) {
			Result = true;
		} else {
			Result = false;
			kw.log.error("预期值" + reminder);
			kw.log.error("实际值" + info);
			kw.log.error("预期值与实际值不匹配");
		}
		return Result;
	}

	/**
	 * 校验回款提醒-有消息
	 * 
	 * @param kw
	 * @param reminder
	 * @return
	 * @throws Exception
	 */
	public boolean checkReturnMoneyReminder(KeyWords kw, String date, String reminder) throws Exception {
		boolean Result = false;
		// 点击回款提醒，记录回款提醒信息
		kw.click(OrUtil.getBy("account_returnMoneyReminder_id", ObjectLib.XhhObjectLib));
		ThreadUtil.sleep();
		// 判断页面回款提醒数量是一条还是两条并将信息内容和日期分别存储到字符串reminder_page和date_page
		if (kw.isElementExist(OrUtil.getBy("account_returnMoneyDate2#_xpath", ObjectLib.XhhObjectLib))) {
			String date_page = kw.getText(OrUtil.getBy("account_returnMoneyDate1#_xpath", ObjectLib.XhhObjectLib)) + " "
					+ kw.getText(OrUtil.getBy("account_returnMoneyDate2#_xpath", ObjectLib.XhhObjectLib));
			String reminder_page = kw
					.getText(OrUtil.getBy("account_returnMoneyContent1#_xpath", ObjectLib.XhhObjectLib)) + " "
					+ kw.getText(OrUtil.getBy("account_returnMoneyContent2#_xpath", ObjectLib.XhhObjectLib));
			// 判断字符串是否包含预期的日期和提醒信息
			if (date_page.contains(date) && reminder_page.contains(reminder)) {
				Result = true;
			} else {
				Result = false;
				kw.log.error("预期日期" + date);
				kw.log.error("实际日期" + date_page);
				kw.log.error("预期信息" + reminder);
				kw.log.error("实际信息" + reminder_page);
				kw.log.error("预期值与实际值不匹配");
			}
		} else {
			// 执行只有一条回款提醒的情况
			String date_page = kw.getText(OrUtil.getBy("account_returnMoneyDate1#_xpath", ObjectLib.XhhObjectLib));
			String reminder_page = kw
					.getText(OrUtil.getBy("account_returnMoneyContent1#_xpath", ObjectLib.XhhObjectLib));

			if (date_page.contains(date) && reminder_page.contains(reminder)) {
				Result = true;
			} else {
				Result = false;
				kw.log.error("预期日期" + date);
				kw.log.error("实际日期" + date_page);
				kw.log.error("预期信息" + reminder);
				kw.log.error("实际信息" + reminder_page);
				kw.log.error("预期值与实际值不匹配");
			}
		}

		return Result;

	}

	/**
	 * 查询同一用户还款日相同的投资，计算笔数、金额
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> mergeList(String uid) throws SQLException {
		// 获取指定用户所有还款计划
		List<Map<String, String>> list = RepayPlanDao.getAllRecordByUserCode(uid);
		// 目标list存储处理后的数据
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		int count = 0;
		double amount = 0.00;
		Map<String, String> Map = new HashMap<String, String>();
		DecimalFormat fm = new DecimalFormat("###,###.##");// 将数据为财务格式
		String format_amount = "";
		// 遍历list，合并日期相同且还款状态相同的用户还款计划数据，并将结果存储到retList
		for (int i = 0; i < list.size(); i++) {
			if (retList.size() != 0 && list.get(i).get("repay_date").equals(retList.get(retList.size() - 1).get("date"))
					&& list.get(i).get("status").equals(retList.get(retList.size() - 1).get("status"))) {
				// 募集期和展期的金额计入结果，但不单独作为一条还款笔数
				if (list.get(i).get("stage").equals("募集期") || list.get(i).get("stage").equals("展期利息")) {
					count = Integer.parseInt(retList.get(retList.size() - 1).get("count"));
				} else {
					count = Integer.parseInt(retList.get(retList.size() - 1).get("count")) + 1;
				}
				amount = Double.parseDouble(retList.get(retList.size() - 1).get("amount").replaceAll(",", ""))
						+ Double.parseDouble(list.get(i).get("total_money"));
				// 格式化总金额
				format_amount = fm.format(amount);
				retList.get(retList.size() - 1).put("count", String.valueOf(count));
				retList.get(retList.size() - 1).put("amount", format_amount);
			} else {
				// 没有相同日期的还款记录直接存储进retList
				Map.put("date", list.get(i).get("repay_date"));
				// 如果是募集期，定义笔数为0，之后必定会有第一期数据，其他情况直接定义笔数为1
				if (list.get(i).get("stage").equals("募集期")) {
					Map.put("count", "0");
				} else {
					Map.put("count", "1");
				}
				amount = Double.parseDouble(list.get(i).get("total_money"));
				// 格式化总金额
				format_amount = fm.format(amount);
				Map.put("amount", format_amount);
				// 存储还款状态
				Map.put("status", list.get(i).get("status"));
				retList.add(Map);
			}
		}
		return retList;
	}

}
