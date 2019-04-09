package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;
import com.autotest.dao.AccountDao;
import com.autotest.dao.UserDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.ThreadUtil;

public class TestCase_Demo extends TestDriver {

	@Test
	public void Test() {

		KeyWords kw = new KeyWords(driver, log);
		Map<String, String> srcMap = new HashMap<String, String>();
		PubXhhService pubXhhService = new PubXhhService();
		UserAccountService accountService = new UserAccountService(log);
		boolean bFinalResult = true;
		String productNumber = "RYS001";
		// 获得投资该产品的用户id
		try {
			List<Map<String, String>> lst = UserTempDao
					.getInvestUserByProNo(productNumber);
			for (int i = 0; i < lst.size()-4; i++) {
				boolean bResult = true;
				String uid = lst.get(i).get("uid");
				// 获得详细的用户信息
				Map<String, String> userInfo = UserDao.getUserById(uid);
				String mobile = userInfo.get("mobile");
				String password = userInfo.get("login_pwd");
				// 调用登录函数
				if (pubXhhService.login(kw, mobile, password)) {
					log.info("login successfully");
				} else {
					log.error("login failed");
					throw new Exception("login failed");
				}

				// 验证账户总览
				srcMap = AccountDao.getUserAcctByUid(uid);
				if (accountService.checkAccountInvestVal(kw, srcMap)) {
					log.info("【账户总览】页面字段校验成功");
				} else {
					bResult = false;
					log.error("【账户总览】页面字段校验失败");
				}

				// 调用登出函数
				if (pubXhhService.logout(kw)) {
					log.info("logout successfully");
				} else {
					log.error("logout failed");
					throw new Exception("logout failed");
				}

				bFinalResult = bFinalResult & bResult;
			}
		} catch (Exception e) {
			log.error("Exception", e);
			log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
			bFinalResult = false;
		} finally {
			srcMap.clear();
		}

		// 验证整个测试用例是否成功
		assertTrue(bFinalResult);
		
		
		ThreadUtil.sleep(600);

	}

}
