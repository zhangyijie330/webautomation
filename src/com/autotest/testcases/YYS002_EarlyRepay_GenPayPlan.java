package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.FinanceRecordDao;
import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.ProductDao;
import com.autotest.dao.ProductTempDao;
import com.autotest.dao.UserDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.DbType;
import com.autotest.enums.FinanceType;
import com.autotest.or.ObjectLib;
import com.autotest.service.bmpService.Loan;
import com.autotest.service.bmpService.PubBmpService;
import com.autotest.service.xhhService.EarlyRepayService;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.RepayPlanService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.DbUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * 测试集 : 提前回款生成还款计划(保理项目投资流程/YYS20160718002)
 * 
 * @author 001811
 *
 */
public class YYS002_EarlyRepay_GenPayPlan extends TestDriver {
	boolean result = true;
	public static KeyWords kw;
	private String productNumber = "YYS002";

	@Test
	public void Test() {
		PubXhhService pubXhhService = new PubXhhService();
		PubBmpService pubBmpService = new PubBmpService();
		RepayPlanService repayService = new RepayPlanService(log);
		UserAccountService accountService = new UserAccountService(log);
		EarlyRepayService earlyRepayService = new EarlyRepayService(log);
		int earlyRepayDays = 3;
		kw = new KeyWords(driver, log);

		// 保理公司账号密码
		String baoli_userName = BaseConfigUtil.getFactorName();
		String baoli_pwd = BaseConfigUtil.getFactorPwd();
		try {

			String projectName = ProductTempDao.getProductCodeByNumber(productNumber).get("product_code");

			// 初始还款日
			String repayDate = ProductDao.getProByCode(projectName).get("repay_date");
			// 允许的最大提前还款天数
			String MaxEarlyDays = ProductDao.getProByCode(projectName).get("early_repay_days");
			String earlyRepayDate = earlyRepayService.getEarlyRepayDate(repayDate, earlyRepayDays);

			// 设置的提前还款天数变量值若大于允许的最大提前还款天数，用例失败
			if (earlyRepayDays > Integer.parseInt(MaxEarlyDays)) {
				log.error("设置的提前天数大于最大允许提前天数");
				throw new Exception("设置的提前天数大于最大允许提前天数");
			}
			// xhh-3735:融资人账户登录主站
			boolean loginResult = pubXhhService.login(kw, baoli_userName, baoli_pwd);
			if (loginResult) {
				log.info(baoli_userName + "登录成功");
			} else {
				log.error(baoli_userName + "登录失败");
				log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
				throw new Exception("login failed");
			}
			// 访问项目管理页面:xhh-3736:融资人在【项目管理-全部】页面查看项目
			earlyRepayService.viewProManageTab(kw);
			kw.waitPageLoad();
			while (true) {
				// 判断当前页是否存在要查找的项目
				if (earlyRepayService.isFindProject(kw, projectName)) {
					log.info("找到项目： " + projectName);
					// xhh-3737:校验项目管理列表的值
					if (earlyRepayService.checkProManageInfo(kw, projectName,
							earlyRepayService.getExpectRroInfo(projectName))) {
						log.info("【项目管理列表的字段及字段值】校验正确！");
					} else {
						log.error("【项目管理列表的字段及字段值】校验失败！");
						result = false;
					}
					break;
				} else {
					log.info("在当前页没找到项目");
					if (kw.isElementExist(By.xpath("//div[@class='page pageWraper']"))) {
						log.info("项目管理页存在分页");
						int toltalPageNum = repayService.getRepayPlanTotalPageNum(kw);
						log.info("项目管理总页数为:" + toltalPageNum);
						int curPageNum = repayService.getRepayPlanCurrentPageNum(kw);
						// 判断是否是最后一页
						if (curPageNum < toltalPageNum) {
							log.info("当前页是： " + curPageNum + "，没找到项目，跳转到下一页!");
							// 点击下一页按钮
							kw.click(OrUtil.getBy("repay_next_page_xpath", ObjectLib.XhhObjectLib));
							ThreadUtil.sleep(3);
						} else {
							log.error("找不到项目" + projectName);
							throw new Exception("找不到项目" + projectName);
						}
					} else {
						log.error("项目不存在分页，找不到项目" + projectName);
						throw new Exception("项目不存在分页，找不到项目" + projectName);
					}
				}
			}
			// 点击提前回购申请：xhh-3738:点击提前回购申请按钮
			earlyRepayService.clickEarlyRepayApply(kw, projectName);
			// 设置提前回购时间
			earlyRepayService.setEarlyRepayDate(kw, earlyRepayDate);
			// 操作成功后点击【确定】按钮：xhh-3834:选择提前回购日期
			kw.click(OrUtil.getBy("proManage_earlyRepay_submit_xpath", ObjectLib.XhhObjectLib));
			// 判断是否设置成功
			if (kw.isElementVisible(OrUtil.getBy("proManage_earlyRepay_success_xpath", ObjectLib.XhhObjectLib))) {
				String txt = kw.getText2(OrUtil.getBy("proManage_earlyRepay_success_xpath", ObjectLib.XhhObjectLib));
				if (txt.indexOf("申请成功") != -1) {
					log.info("提前还款申请成功");
				} else {
					log.error("提前还款申请失败");
					result = false;
					// throw new Exception("提前还款申请失败");

				}
			}

			// 保理账号退出登录
			ThreadUtil.sleep(3);
			kw.isElementVisible(OrUtil.getBy("account_logout_xpath", ObjectLib.XhhObjectLib));
			kw.waitElementToBeClickable(OrUtil.getBy("account_logout_xpath", ObjectLib.XhhObjectLib));
			if (pubXhhService.logout(kw)) {
				log.info(baoli_userName + "退出登录成功");
			} else {
				log.error(baoli_userName + "退出登录失败");
				result = false;
			}

			// 登录xhh-admin业务管理平台
			String adminUserName = BaseConfigUtil.getBMPName();
			String adminPwd = BaseConfigUtil.getBMPPwd();
			if (pubBmpService.login(kw, adminUserName, adminPwd)) {
				log.info(adminUserName + "登录成功");
			} else {
				log.error(adminUserName + "登录失败");
				throw new Exception("login failed");
			}
			// xhh-3835:访问【项目运营-提前还款审核】页面
			if (earlyRepayService.enterEarlyRepayExamine(kw)) {
				log.info("访问【项目运营-提前还款审核】页面成功");
			} else {
				log.error("访问【项目运营-提前还款审核】页面失败!");
				log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
				throw new Exception("访问【项目运营-提前还款审核】页面失败!");
			}
			// xhh-3836:在提前还款审核页面查找出保理项目
			while (true) {
				if (earlyRepayService.isFindExamine(kw, projectName)) {
					log.info("找到要审核的项目");
					// xhh-3840:校验提前还款申请页面结果列表展示的记录
					Map<String, String> expMap = earlyRepayService.getExpactedExamineData(projectName, earlyRepayDate);
					Map<String, String> pageMap = earlyRepayService.getPageExamineData(kw, projectName);
					if (earlyRepayService.checkExamineData(pageMap, expMap)) {
						log.info("待审核申请校验成功");
					} else {
						log.error("待审核申请校验失败");
						result = false;
					}
					// xhh-3866:提前还款审核操作
					if (earlyRepayService.doExamine(kw, projectName)) {
						log.info(projectName + "审核成功");
					} else {
						log.error(projectName + "审核失败");
						log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
						throw new Exception(projectName + "审核失败");
					}
					break;
				} else {
					log.info("在当前页没找到提前回款申请");
					String totalPage = kw
							.getText(OrUtil.getBy("earlyRepayExamine_totalPage_xpath", ObjectLib.XhhObjectLib))
							.substring(3, 4);
					int toltalPageNum = Integer.parseInt(totalPage);
					if (toltalPageNum > 1) {
						log.info("存在分页");
						log.info("总页数为:" + toltalPageNum);
						int curPageNum = earlyRepayService.getCurrentPage(kw);
						// 判断是否是最后一页
						if (curPageNum < toltalPageNum) {
							log.info("当前页是： " + curPageNum + "，没找到提前回款申请，跳转到下一页!");
							// 点击下一页按钮
							kw.click(OrUtil.getBy("earlyRepayExamine_nextPage_xpath", ObjectLib.XhhObjectLib));
							ThreadUtil.sleep(3);
						} else {
							log.error("找不到提前回款申请" + projectName);
							throw new Exception("找不到提前回款申请" + projectName);
						}
					} else {
						log.error("不存在分页，找不到提前回款申请" + projectName);
						throw new Exception("找不到提前回款申请" + projectName);
					}
				}
			}

			// xhh-3869:修改php服务器时间到提前还款日
			SSHUtil.sshChangeTime(log, earlyRepayDate + " 13:00:00");
			// xhh-3868:融资人账户再次登录主站
			if (pubXhhService.login(kw, baoli_userName, baoli_pwd)) {
				log.info(baoli_userName + "登录成功");
			} else {
				log.error(baoli_userName + "登录失败");
				log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
				throw new Exception("login failed");
			}
			// xhh-3870:融资人访问【还款计划-月益升】页面
			repayService.viewRepayPlanTab(kw);
			kw.waitPageLoad();
			// 点击"月益升"
			repayService.viewMonthGrouth(kw);
			kw.waitPageLoad();
			// 点击"还款日"，更改排列顺序
			repayService.clickRepayDate(kw);
			// 确定排序后页面刷新完成
			kw.isVisible(OrUtil.getBy("repay_table_repaydate_desc_xpath", ObjectLib.XhhObjectLib));
			while (true) {
				// 判断当前页是否存在要查找的项目
				if (repayService.isFindProject(kw, projectName)) {
					log.info("还款计划中找到项目： " + projectName);
					// xhh-3872:查看项目[YYS20160718002]列表数据
					if (repayService.checkRepayPlanInfo(kw, projectName,
							repayService.getExpectRepayPlanInfo(projectName, repayDate))) {
						log.info("【项目计划列表的字段及字段值】校验正确！");
					} else {
						log.error("【项目计划列表的字段及字段值】校验失败！");
						result = false;
					}
					// 点击【还款】按钮，跳转到还款明细页面
					if (repayService.viewRepayDetailPage(kw, projectName)) {
						log.info("跳转到【还款明细页面】成功");
					} else {
						log.error("跳转到【还款明细页面】失败");
						log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
						throw new Exception("跳转到【还款明细页面】失败");
					}
					// 校验还款明细页面的各字段值
					if (StringUtils.isEquals(repayService.getExpectedDetail(projectName),
							repayService.getRepayDetail(kw), log)) {
						log.info("【还款明细页面的字段及字段值】校验正确！");
					} else {
						log.error("【还款明细页面的字段及字段值】校验失败！");
						log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
						result = false;
					}
					// 校验当期还款明细
					if (earlyRepayService.checkRepayDetail(kw)) {
						log.info("【当期还款明细】校验正确");
					} else {
						log.error("【当期还款明细】校验失败！");
						log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
						result = false;
					}
					break;
				} else {
					log.info("在当前页没找到项目");
					if (kw.isElementExist(By.xpath("//div[@class='page pageWraper']"))) {
						log.info("还款计划页存在分页");
						int toltalPageNum = repayService.getRepayPlanTotalPageNum(kw);
						log.info("还款计划总页数为:" + toltalPageNum);
						int curPageNum = repayService.getRepayPlanCurrentPageNum(kw);
						// 判断是否是最后一页
						if (curPageNum < toltalPageNum) {
							log.info("当前页是： " + curPageNum + "，没找到项目，跳转到下一页!");
							// 点击下一页按钮
							kw.click(OrUtil.getBy("repay_next_page_xpath", ObjectLib.XhhObjectLib));
							ThreadUtil.sleep(3);
						} else {
							log.error("找不到项目" + projectName);
							throw new Exception("找不到项目" + projectName);
						}
					} else {
						log.error("还款计划不存在分页，找不到项目" + projectName);
						throw new Exception("还款计划不存在分页，找不到项目" + projectName);
					}
				}
			}

			// xhh-3875:融资人点击提前还款操作
			earlyRepayService.doEarlyRepay(kw, projectName);
			// 更新项目信息中的还款日期
			earlyRepayService.updateProInfo(projectName, earlyRepayDate);
			// 更新用户投资记录、还款计划、账户中的收益和还款日期
			List<Map<String, String>> list = UserTempDao.getInvestUserByProNo(productNumber);
			String uid;
			for (int i = 0; i < list.size(); i++) {
				uid = list.get(i).get("uid");
				// 用新的用户还款计划计算待收收益及预期总收益，因此先更新用户还款计划，再更新用户账户
				earlyRepayService.updateUserRepayPlan(projectName, uid, earlyRepayDays, earlyRepayDate);
				earlyRepayService.updateUserAcct(uid, projectName, earlyRepayDays);
				earlyRepayService.updateInvestRecord(projectName, uid, earlyRepayDays, earlyRepayDate);

			}
			// 更新项目还款计划的收益和还款日期
			// 项目的收益由用户还款计划中的收益累加，先更新用户还款计划，再更新项目收益
			earlyRepayService.updateProductRepayPlan(projectName, earlyRepayDate);

			// 保理账号退出登录
			kw.isElementVisible(OrUtil.getBy("account_logout_xpath", ObjectLib.XhhObjectLib));
			kw.waitElementToBeClickable(OrUtil.getBy("account_logout_xpath", ObjectLib.XhhObjectLib));

			if (pubXhhService.logout(kw)) {
				log.info("logout successfully");
			} else {
				log.error("logout failed");
				log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
				throw new Exception("logout failed");
			}

			// 用户账户校验
			for (int i = 0; i < list.size(); i++) {
				uid = list.get(i).get("uid");
				// 获得详细的用户信息

				Map<String, String> userInfo = UserDao.getUserById(uid);
				String user = userInfo.get("mobile");
				String pwd = userInfo.get("login_pwd");

				// 投资人账户登录
				if (pubXhhService.login(kw, user, pwd)) {
					log.info(user + "登录成功");
				} else {
					log.error(user + "登录失败");
					log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
					throw new Exception("login failed");
				}

				// xhh-3740:提前回款生成还款计划后，用户查看【账户总览-回款日历】区域数据

				Map<String, String> exptectAcctInfo = AccountDao.getUserAcctByUid(uid);
				if (accountService.checkAccountInvestVal(kw, exptectAcctInfo)) {
					log.info("【账户总览】页面字段校验成功");
				} else {
					log.error("【账户总览】页面字段校验失败");
					result = false;
				}

				// 校验回款日历

				String date = "";
				String amount = "";
				String count = "";
				String status = "";
				List<Map<String, String>> relstCalendar = pubXhhService.mergeList(uid);
				for (Map<String, String> mapCalendar : relstCalendar) {
					date = mapCalendar.get("date");
					amount = mapCalendar.get("amount");
					count = mapCalendar.get("count");
					status = mapCalendar.get("status");
					if (pubXhhService.checkReturnMoneyCalendar(kw, date, count, amount, status)) {
						log.info("回款日历校验成功！");
					} else {
						log.error("回款日历校验失败！");
						result = false;
					}
				}

				// 校验回款提醒

				List<Map<String, String>> userrepayplan = DbUtil
						.queryDataList("select * from user_repay_plan where uid='" + uid + "' and name='" + projectName
								+ "' and (stage='募集期' or stage='第1期' or stage='展期')", DbType.Local);
				String date2 = userrepayplan.get(0).get("repay_date");
				String capital = Loan.formatStr(userrepayplan.get(0).get("capital"));
				double sumprofit = 0.00;
				for (int j = 0; j < userrepayplan.size(); j++) {
					sumprofit = sumprofit + Double.parseDouble(userrepayplan.get(j).get("profit"));
				}
				DecimalFormat fm = new DecimalFormat("###,###.00");
				String profit = fm.format(sumprofit);
				String reminder = "投资" + projectName + "将收到" + capital + "元本金 和" + profit + "元收益 ，预计16:00-24:00到账。";
				if (pubXhhService.checkReturnMoneyReminder(kw, date2, reminder)) {
					log.info("校验回款提醒成功！");
				} else {
					log.error("未找到该回款提醒！");
					result = false;
				}

				// 点击头像左侧的【投资管理】入口
				accountService.viewFinaceManagePage(kw);
				// 点击【所有】入口并检查理财记录列表数据
				accountService.viewAllType(kw);

				// 校验理财记录条数
				int investRowCount = InvestRecordDao.getCountByUser(uid);
				if (accountService.checkRowCount(kw, investRowCount)) {
					log.info("【理财记录】校验记录条数成功");
				} else {
					log.error("【理财记录】校验记录条数失败");
					result = false;
				}

				// xhh-3742:提前回款生成还款计划后，用户查看【理财记录】数据
				Map<String, String> orderInfoMap = InvestRecordDao.getRecordByUserCode(uid, projectName);
				if (accountService.checkValInInvestRecord(kw, projectName, orderInfoMap)) {
					log.info("【理财记录】校验数据成功");
				} else {
					log.error("【理财记录】校验数据失败");
					result = false;
				}

				// 用户查看【资金记录】数据
				accountService.viewPayRecord(kw);

				// xhh-3744:提前回款生成还款计划后，用户查看【资金记录】数据
				Map<String, String> expectPayRecord = FinanceRecordDao.getRecordByUserCode(uid, projectName,
						FinanceType.invest);
				if (accountService.checkValInPayRecord(kw, projectName, expectPayRecord, FinanceType.invest)) {
					log.info("【资金记录】校验数据成功");
				} else {
					log.error("【资金记录】校验数据失败");
					result = false;
				}

				// 投资人账户登出

				if (pubXhhService.logout(kw)) {
					log.info("logout successfully");
				} else {
					log.error("logout failed");
					log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
					throw new Exception("logout failed");
				}
			}
			// 恢复系统时间
			SSHUtil.sshRecoverTime(log);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			// 截图
			log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
			result = false;
		}

		assertTrue(result);
	}
}