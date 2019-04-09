package com.autotest.testcases;

import java.util.Map;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.autotest.dao.UserDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.DiscountType;
import com.autotest.service.marketService.MarketingService;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.ScreenShotUtil;

/**
 * 
 * 注册后添加红包或者满减券
 * 
 * @author sunlingyun
 * 
 * @date 2016年9月8日 上午9:47:08
 */
public class YYS008_RewardAdd extends TestDriver {
	// 设置添加红包/满减券的用户
	String[] users = { "u17", "u18" };
	// 设置不同用户的优惠券类型，与users数组一一对应
	DiscountType[] rewards = { DiscountType.bonus, DiscountType.coupons };
	// 设置预期的用户优惠券金额,不能根据这里的设置改变实际用户获得的金额（满减券/红包金额是提前设置好的），仅为预期值
	String[] rewardAmts = { "22.00", "10.00" };
	// 文件路径
	String csvFilePath = "config/批量发放奖励模板.csv";
	boolean result = true;

	@Test
	public void test() {
		KeyWords keyWords = new KeyWords(driver, log);
		MarketingService marketingService = new MarketingService(keyWords, log);
		PubXhhService xhhService = new PubXhhService();

		try {
			// 红包添加
			for (int i = 0; i < users.length; i++) {
				DiscountType rewardType = rewards[i];
				String uid = UserTempDao.getUid(users[i]).get("uid");
				String rewardAmt = rewardAmts[i];
				Map<String, String> user = UserDao.getUserById(uid);
				String mobile = user.get("mobile");
				String loginPwd = user.get("login_pwd");
				// 数据准备
				marketingService.dataReady(mobile, csvFilePath);
				boolean flag = marketingService.openUrl(BaseConfigUtil
						.getMRKTUrl());
				if (!flag) {
					log.error("访问营销系统失败!");
					result = false;
					throw new Exception("访问营销系统失败!");
				}
				// 登陆营销系统
				flag = marketingService.loginMarket(
						BaseConfigUtil.getMRKTName(),
						BaseConfigUtil.getMRKTPwd());
				if (!flag) {
					log.error("登陆营销系统失败!");
					result = false;
					throw new Exception("登陆营销系统失败!");
				}
				// 进入【奖励发放管理-奖励发放】页面
				flag = marketingService.gotoRewardSendPage();
				if (!flag) {
					log.error("进入【奖励发放管理-奖励发放】页面失败!");
					result = false;
					throw new Exception("进入【奖励发放管理-奖励发放】页面失败!");
				}
				// 新增奖励
				flag = marketingService.addReward();
				if (!flag) {
					log.error("新增奖励失败!");
					result = false;
					throw new Exception("新增奖励失败!");
				}
				// 填写新增信息，选择对应的value
				// 消息通知
				String newsContent = "1";
				// 短信内容
				String messageContent = "1";
				// 备注
				String rewardNotes = "1";
				flag = marketingService.writeAddInfo(csvFilePath, newsContent,
						messageContent, rewardNotes, rewardType);
				if (!flag) {
					log.error("填写新增信息失败!");
					result = false;
					throw new Exception("填写新增信息失败!");
				}

				// 点击添加
				flag = marketingService.clickAdd();
				if (!flag) {
					log.error("点击添加失败!");
					result = false;
					throw new Exception("点击添加失败!");
				}
				// 数据回写
				marketingService.rewardWriteBack(rewardType, uid, rewardAmt);
				// 奖励发放后账户信息确认
				if (xhhService.login(keyWords, mobile, loginPwd)) {
					log.info("奖励用户登录成功");
					switch (rewardType) {
					case bonus:
						if (marketingService.infoConfirm(rewardType, uid)) {
							log.info("校验红包金额成功");
						} else {
							result = false;
							log.error("校验红包金额失败");
						}
						break;
					case coupons:
						if (marketingService.infoConfirm(rewardType, uid)) {
							log.info("校验满减券金额成功");
						} else {
							result = false;
							log.error("校验满减券金额失败");
						}
						break;

					default:
						result = false;
						throw new Exception("错误的优惠券类型");
					}
				} else {
					log.error("奖励用户登录失败");
					result = false;
					throw new Exception("奖励发放后账户信息确认失败!");
				}
				// 调用登出函数
				if (xhhService.logout(keyWords)) {
					log.info("logout successfully");
				} else {
					log.error("logout failed");
					log.info(ScreenShotUtil.takeScreenshot(ThreadName,
							keyWords.driver));
					throw new Exception("登出失败");
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
			log.info(ScreenShotUtil.takeScreenshot(ThreadName, keyWords.driver));
		}
		AssertJUnit.assertTrue(result);
	}
}
