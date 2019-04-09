/** 
 * @date 2016年9月8日 上午9:50:44
 * @version 1.0
 * 
 * @author xudashen
 */
package com.autotest.service.marketService;

import java.io.File;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;

import com.autotest.dao.AccountDao;
import com.autotest.dao.BusinessDao;
import com.autotest.driver.KeyWords;
import com.autotest.enums.DiscountType;
import com.autotest.or.ObjectLib;
import com.autotest.utility.ExcelUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.PatternUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * @author xudashen
 * 
 * @date 2016年9月8日 上午9:50:44
 */
public class MarketingService {

	KeyWords	keyWords	= null;
	Logger		log			= null;

	public MarketingService(KeyWords keyWords, Logger log) {
		this.keyWords = keyWords;
		this.log = log;
	}

	/**
	 * 功能：xhh-7609:数据准备
	 * 
	 * @author xudashen
	 * @param mobile
	 * @param fileName
	 * @return boolean
	 * @date 2016年9月8日 上午10:28:08
	 */
	public boolean dataReady(String mobile, String fileName) {
		boolean flag = false;
		Map<String, String> map = null;
		map = BusinessDao.getUserByMobile(mobile);
		String[] title = { "uid", "uname", "realName", "mobile" };
		String[] content = { map.get("uid"), map.get("uname"),
				map.get("real_name"), map.get("mobile") };
		try {
			ExcelUtil.writerCsv(fileName, title, content);
			flag = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * 功能：xhh-7611:访问营销系统
	 * 
	 * @author xudashen
	 * @param url
	 * @return boolean
	 * @throws Exception
	 * @date 2016年9月8日 上午11:16:20
	 */
	public boolean openUrl(String url) throws Exception {
		boolean flag = false;
		keyWords.open(url);
		flag = true;
		return flag;
	}

	/**
	 * 功能：xhh-7612:登陆营销系统
	 * 
	 * @author xudashen
	 * @param user
	 * @param pwd
	 * @return boolean
	 * @throws Exception
	 * @date 2016年9月8日 上午11:28:27
	 */
	public boolean loginMarket(String user, String pwd) throws Exception {
		boolean flag = false;
		keyWords.setValue(
				OrUtil.getBy("login_username_id", ObjectLib.MarketObjectLib),
				user);
		keyWords.setValue(
				OrUtil.getBy("login_pwd_id", ObjectLib.MarketObjectLib), pwd);
		String js = "submit_form()";
		keyWords.executeJS(js);
		for (int i = 0; i < 10; i++) {
			if (keyWords
					.isVisible(OrUtil.getBy("index_rewardManage_text_xpath",
							ObjectLib.MarketObjectLib))) {
				flag = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * 功能：xhh-7613:进入【奖励发放管理-奖励发放】页面
	 * 
	 * @author xudashen
	 * @return boolean
	 * @date 2016年9月8日 下午1:46:00
	 */
	public boolean gotoRewardSendPage() {
		boolean flag = false;
		keyWords.clickDiv(OrUtil.getBy("index_rewardManage_xpath",
				ObjectLib.MarketObjectLib));
		for (int i = 0; i < 3; i++) {
			if (keyWords.isVisible(OrUtil.getBy("index_rewardGive_text_xpath",
					ObjectLib.MarketObjectLib))) {
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		keyWords.clickDiv(OrUtil.getBy("index_rewardGive_text_xpath",
				ObjectLib.MarketObjectLib));
		for (int i = 0; i < 3; i++) {
			if (keyWords.isVisible(OrUtil.getBy("reward_btn_searchBatch_xpath",
					ObjectLib.MarketObjectLib))) {
				flag = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * 功能：xhh-7614:新增奖励
	 * 
	 * @author xudashen
	 * @return boolean
	 * @throws Exception
	 * @date 2016年9月8日 下午1:47:47
	 */
	public boolean addReward() throws Exception {
		boolean flag = false;
		keyWords.click(OrUtil.getBy("reward_addReward_xpath",
				ObjectLib.MarketObjectLib));
		for (int i = 0; i < 10; i++) {
			if (keyWords.isVisible(OrUtil.getBy("reward_addForm_xpath",
					ObjectLib.MarketObjectLib))) {
				flag = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * 功能：xhh-7615 : 填写新增信息
	 * 
	 * @author xudashen
	 * @param accType
	 *            账户类型 对应option的value，为-1时则表示选择全部
	 * @param activityType
	 *            营销活动 对应option的value
	 * @param type
	 *            奖励类别
	 * @param selectReward
	 *            选择奖励
	 * @param csvFilePath
	 *            发放用户名单
	 * @param newsContent
	 *            消息通知
	 * @param messageContent
	 *            短信内容
	 * @param rewardNotes
	 *            奖励备注
	 * @return boolean
	 * @throws Exception
	 * @date 2016年9月8日 下午1:53:46
	 */
	public boolean writeAddInfo(String csvFilePath, String newsContent,
			String messageContent, String rewardNotes, DiscountType discType)
			throws Exception {
		boolean flag = false;
		// 账户类型
		keyWords.selectByText(OrUtil.getBy("reward_accounTypeChan_xpath",
				ObjectLib.MarketObjectLib), "运营账户");
		switch (discType) {
			case bonus:
				// 营销活动
				keyWords.selectByText(OrUtil.getBy(
						"reward_marketingActivity_xpath",
						ObjectLib.MarketObjectLib), "22元红包");
				// 奖励类别
				keyWords.click(OrUtil.getBy("reward_bonusRadio_xpath",
						ObjectLib.MarketObjectLib));
				// 选择奖励 test8环境selectId为33

				// keyWords.selectByValue(OrUtil.getBy("reward_select_id",
				// ObjectLib.MarketObjectLib), "38");

				keyWords.selectByValue(OrUtil.getBy("reward_select_id",
						ObjectLib.MarketObjectLib), "33");
				break;
			case coupons:
				// 营销活动
				keyWords.selectByText(OrUtil.getBy(
						"reward_marketingActivity_xpath",
						ObjectLib.MarketObjectLib), "10元满减券");
				// 奖励类别
				keyWords.click(OrUtil.getBy("reward_couponsRadio_xpath",
						ObjectLib.MarketObjectLib));
				// 选择奖励
				keyWords.selectByValue(OrUtil.getBy("reward_select_id",
						ObjectLib.MarketObjectLib), "11");
				break;
			default:
				break;
		}

		// upload
		WebElement webElement = keyWords.getWebElement(OrUtil.getBy(
				"reward_upload_id", ObjectLib.MarketObjectLib));
		if (null == webElement) {
			flag = false;
			throw new Exception("发放用户名单按钮未获取到!");
		}
		File file = new File(csvFilePath);
		webElement.sendKeys(file.getAbsolutePath());
		// 消息发送
		if (newsContent.equals("1")) {
			keyWords.click(OrUtil.getBy("reward_msgSend_id",
					ObjectLib.MarketObjectLib));
		} else if (newsContent.equals("0")) {
			keyWords.click(OrUtil.getBy("reward_msgUnSend_id",
					ObjectLib.MarketObjectLib));
		}
		// 短信内容
		keyWords.setValue(
				OrUtil.getBy("reward_msg_id", ObjectLib.MarketObjectLib),
				messageContent);
		// 奖励备注
		keyWords.setValue(
				OrUtil.getBy("reward_remark_id", ObjectLib.MarketObjectLib),
				rewardNotes);

		flag = true;
		return flag;
	}

	/**
	 * 功能：xhh-7616:点击添加
	 * 
	 * @author xudashen
	 * @return boolean
	 * @throws Exception
	 * @date 2016年9月8日 下午2:31:08
	 */
	public boolean clickAdd() throws Exception {
		boolean flag = true;
		// 点击增加
		keyWords.click(OrUtil.getBy("reward_add_xpath",
				ObjectLib.MarketObjectLib));
		ThreadUtil.sleep();
		// 确认增加
		keyWords.click(OrUtil.getBy("reward_confirm_linkText",
				ObjectLib.MarketObjectLib));
		// 立即执行
		keyWords.click(OrUtil.getBy("reward_confirm_linkText",
				ObjectLib.MarketObjectLib));
		keyWords.waitPageLoad();

		// 点击奖励发放
		for (int i = 0; i < 3; i++) {
			if (keyWords.isVisible(OrUtil.getBy("index_rewardGive_text_xpath",
					ObjectLib.MarketObjectLib))) {
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		keyWords.clickDiv(OrUtil.getBy("index_rewardGive_text_xpath",
				ObjectLib.MarketObjectLib));

		// 最新一条数据为发送成功
		for (int j = 0; j < 3; j++) {
			String status = keyWords.getText(OrUtil.getBy(
					"reward_tableStatus_xpath", ObjectLib.MarketObjectLib));
			if (!StringUtils.isEquals("发放成功", status, log)) {
				flag = false;
			} else {
				flag = true;
				break;
			}

		}

		return flag;
	}

	/**
	 * 功能：xhh-7617:奖励发放后账户信息确认
	 * 
	 * @author xudashen
	 * @param url
	 * @param account
	 * @param password
	 * @param total
	 * @return boolean
	 * @throws Exception
	 * @date 2016年9月8日 下午2:34:21
	 */
	public boolean infoConfirm(DiscountType rewardType, String uid)
			throws Exception {
		boolean flag = false;
		String rewardAmt = null;
		switch (rewardType) {
			case bonus:
				String bonusVal = PatternUtil.getMoneyMatch(keyWords
						.getText(OrUtil.getBy("account_bonus_xpath",
								ObjectLib.XhhObjectLib)));
				rewardAmt = AccountDao.getUserAcctByUid(uid).get(
						"bonus_not_use");
				flag = StringUtils.isEquals(rewardAmt, bonusVal, log);
				break;
			case coupons:
				String couponsVal = PatternUtil.getMoneyMatch(keyWords
						.getText(OrUtil.getBy("account_coupons_xpath",
								ObjectLib.XhhObjectLib)));
				rewardAmt = AccountDao.getUserAcctByUid(uid).get(
						"coupons_not_use");
				flag = StringUtils.isEquals(rewardAmt, couponsVal, log);
				break;
			case none:
				throw new Exception("无需发送优惠券");
			default:
				throw new Exception("无需发送优惠券");
		}
		return flag;
	}

	/**
	 * 数据回写，更新账号信息
	 */
	public void rewardWriteBack(DiscountType type, String uid, String rewardAmt)
			throws Exception {
		switch (type) {
			case bonus:
				double bonusNotUse = Double.parseDouble(AccountDao
						.getUserAcctByUid(uid).get("bonus_not_use"));
				rewardAmt = StringUtils.double2Str(Double
						.parseDouble(rewardAmt) + bonusNotUse);
				break;
			case coupons:
				double couponsNotUse = Double.parseDouble(AccountDao
						.getUserAcctByUid(uid).get("coupons_not_use"));
				rewardAmt = StringUtils.double2Str(Double
						.parseDouble(rewardAmt) + couponsNotUse);
				break;
			default:
				break;
		}
		AccountDao.updateReward(rewardAmt, type, uid);
	}
}
