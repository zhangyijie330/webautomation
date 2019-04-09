package com.autotest.service.bmpService;

import com.autotest.driver.KeyWords;
import com.autotest.or.ObjectLib;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.ThreadUtil;

/**
 * 鑫合汇业务管理平台公共功能封装类
 * 
 * @author wb0002
 */
public class PubBmpService {

	/**
	 * xhh-233 访问鑫合汇业务管理平台 步骤： 1.打开test9鑫合汇业务管理平台 期望的结果： 页面正常加载
	 * 
	 * @param kw
	 * @throws Exception
	 */
	public void enterIndex(KeyWords kw) throws Exception {
		// 打开鑫合汇业务管理平台登录页面
		kw.open(BaseConfigUtil.getBmpConfig("bmp_url"));
	}

	/**
	 * xhh-234 登录鑫合汇业务管理平台 前提：xhh-233 步骤： 1.使用账户t0277（登录密码upg2015）登录 期望的结果： 登录成功
	 * 
	 * @param kw
	 * @param userName
	 * @param pwd
	 * @return 登录鑫合汇业务管理平台实际结果。false：失败；true：成功
	 * @throws Exception
	 */
	public boolean login(KeyWords kw, String userName, String pwd)
			throws Exception {
		boolean loginResult = false;
		// 打开鑫合汇业务管理平台登录页面
		enterIndex(kw);
		// 输入账号密码
		kw.setValue(OrUtil.getBy("login_userNo_xpath", ObjectLib.BMPObjectLib),
				userName);
		kw.setValue(OrUtil.getBy("login_pwd_xpath", ObjectLib.BMPObjectLib),
				pwd);
		ThreadUtil.sleep();
		// 判断 业务平台中心 文本是否显示
		if (!kw.isElementVisible(OrUtil.getBy("login_mi_xpath",
				ObjectLib.BMPObjectLib))) {
			System.out.println(kw.isElementVisible(OrUtil.getBy(
					"login_mi_xpath", ObjectLib.BMPObjectLib)));
			throw new Exception("业务平台中心文本未显示出来，说明账号输入有问题。");
		}
		// 点击登录
		kw.click(OrUtil.getBy("login_login_btn_xpath", ObjectLib.BMPObjectLib));
		// 等待登录成功
		kw.waitPageLoad();
		// 判断是否登录成功
		if (!kw.isElementExist(OrUtil.getBy("index_userName_xpath",
				ObjectLib.BMPObjectLib))) {
			throw new Exception("登录失败！");
		} else {
			loginResult = true;
		}
		return loginResult;
	}

}
