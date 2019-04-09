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
import com.autotest.service.xhhService.ExtendRepayService;
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
 * 测试集 : 展期还款生成还款计划(保理项目投资流程/YYS20160718003)
 * 
 * @author 001811
 *
 */
public class YYS003_ExtendRepay_GenPayPlan extends TestDriver {
	boolean result = true;
	public static KeyWords kw;
	private String productNumber = "YYS003";

	@Test
	public void Test() {
		PubXhhService pubXhhService = new PubXhhService();
		RepayPlanService repayService = new RepayPlanService(log);
		ExtendRepayService exRapayService = new ExtendRepayService(log);
		UserAccountService accountService = new UserAccountService(log);
		kw = new KeyWords(driver, log);

		// 保理公司账号密码
		String baoli_userName = BaseConfigUtil.getFactorName();
		String baoli_pwd = BaseConfigUtil.getFactorPwd();

		try {
			String projectName = ProductTempDao.getProductCodeByNumber(productNumber).get("product_code");

			// 初始还款日
			String repayDate = ProductDao.getProByCode(projectName).get("repay_date");
			// 展期还款天数
			String extendDays = ProductDao.getProByCode(projectName).get("extend_time");

			// 获取当前服务器时间
			String currTime = SSHUtil.sshCurrentTime(log);
			log.info("当前服务器时间为：" + currTime);
			// xhh-779:修改服务器时间至正常还款日
			SSHUtil.sshChangeTime(log, repayDate + " 13:00:00");

			// xhh-787:保理公司账户登录
			boolean loginResult = pubXhhService.login(kw, baoli_userName, baoli_pwd);
			if (loginResult) {
				log.info(baoli_userName + "登录成功");
			} else {
				log.error(baoli_userName + "登录失败");
				throw new Exception("登录失败");
			}
			// xhh-780:保理公司访问【还款计划】页面
			repayService.viewRepayPlanTab(kw);
			// 点击"月益升"
			repayService.viewMonthGrouth(kw);
			// 点击"还款日"，更改排列顺序
			repayService.clickRepayDate(kw);
			// 确定排序后页面刷新完成
			kw.isVisible(OrUtil.getBy("repay_table_repaydate_desc_xpath", ObjectLib.XhhObjectLib));
			// 开始查找项目
			while (true) {
				// xhh-781:保理公司在【还款计划-月益升】列表查找项目
				if (repayService.isFindProject(kw, projectName)) {
					log.info("找到项目： " + projectName);
					// xhh-782:校验还款计划列表的字段及字段值
					if (repayService.checkRepayPlanInfo(kw, projectName,
							repayService.getExpectRepayPlanInfo(projectName, repayDate))) {
						log.info("【还款计划列表的字段及字段值】校验正确！");
					} else {
						result = false;
						log.error("【还款计划列表的字段及字段值】校验失败！");
					}
					// xhh-783:保理公司点击进入【还款】页面
					if (repayService.viewRepayDetailPage(kw, projectName)) {
						log.info("跳转到【还款明细页面】成功");
					} else {
						log.error("跳转到【还款明细页面】失败");
						throw new Exception("跳转到【还款明细页面】失败");
					}

					// xhh-784:检查还款页面的字段及字段值
					if (StringUtils.isEquals(repayService.getExpectedDetail(projectName),
							repayService.getRepayDetail(kw), log)) {
						log.info("【还款明细页面的字段及字段值】校验正确！");
					} else {
						log.error("【还款明细页面的字段及字段值】校验失败！");
						result = false;
					}

					// 判断项目还款明细页面是否存在分页
					boolean res = kw.isElementExist(By.xpath("//div[@class='page pageWraper']/a"));

					// 获取当期还款列表内容
					List<String> repayTableList = repayService.getAllPageRecordsStrList(kw, res,
							"repay_detail_tbody_xpath", "repay_detail_tbody_nextpage_xpath",
							"repay_detail_tbody_totalpage_xpath", "repay_detail_tbody_curpage_xpath");
					for (int i = 0; i < repayTableList.size(); i++) {
						System.out.println(repayTableList.get(i));
					}

					// 校验当期还款列表内容的期望值
					if (repayService.checkRepayTableRecords(projectName, repayDate, repayTableList)) {
						log.info("【当期还款明细信息列表】校验成功");
					} else {
						log.error("【当期还款明细信息列表】校验失败");
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
						log.error("项目不存在分页，找不到项目" + projectName);
						throw new Exception("找不到项目" + projectName);
					}
				}
			}
			// 保理账号退出登录
			if (pubXhhService.logout(kw)) {
				log.info("保理账号登出成功");
			} else {
				log.error("保理账号登出失败");
				throw new Exception("保理账号登出失败");
			}

			// xhh-1092:修改服务器时间至展期还款日
			String extendDate = exRapayService.getExtendDate(repayDate, extendDays);
			SSHUtil.sshChangeTime(log, extendDate + " 13:00:00");

			// xhh-1094:再次保理公司账户登录
			if (pubXhhService.login(kw, baoli_userName, baoli_pwd)) {
				log.info(baoli_userName + "修改时间后登录成功");
			} else {
				log.error(baoli_userName + "修改时间后登录失败");
				throw new Exception(baoli_userName + "修改时间后登录失败");
			}

			// xhh-1095:再次保理公司访问【还款计划】页面
			repayService.viewRepayPlanTab(kw);
			// 点击"月益升"
			repayService.viewMonthGrouth(kw);
			// 点击"还款日"，更改排列顺序
			repayService.clickRepayDate(kw);
			// 确定排序后页面刷新完成
			kw.isVisible(OrUtil.getBy("repay_table_repaydate_desc_xpath", ObjectLib.XhhObjectLib));
			// 开始查找项目
			while (true) {
				// xhh-1096:再次保理公司在【还款计划-月益升】列表查找项目
				if (repayService.isFindProject(kw, projectName)) {
					log.info("修改时间后找到项目： " + projectName);
					if (repayService.viewRepayDetailPage(kw, projectName)) {
						log.info("跳转到【还款明细页面】成功");
					} else {
						log.error("跳转到【还款明细页面】失败");
						throw new Exception("跳转到【还款明细页面】失败");
					}
					break;
				} else {
					log.info("修改时间后在当前页没找到项目");
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
							log.error("修改时间后找不到项目" + projectName);
							throw new Exception("修改时间后找不到项目" + projectName);
						}
					} else {
						log.error("项目不存在分页，修改时间后找不到项目" + projectName);
						throw new Exception("项目不存在分页，修改时间后找不到项目" + projectName);
					}
				}
			}

			// xhh-788:保理公司【展期还款】
			if (!exRapayService.doExtendRepay(kw, projectName)) {
				log.error("执行展期还款失败" + projectName);
				throw new Exception("执行展期还款失败" + projectName);
			}

			// 更新项目信息中的还款日期
			exRapayService.updateProInfo(projectName, extendDate);

			// 更新自动化测试库的用户投资记录、还款计划、账户中的收益和还款日期
			List<Map<String, String>> list = UserTempDao.getInvestUserByProNo(productNumber);
			String uid;
			for (int i = 0; i < list.size(); i++) {
				uid = list.get(i).get("uid");
				exRapayService.insertExtendToUserPepayPlan(projectName, uid, extendDate);
				exRapayService.updateInvestRecord(projectName, uid, extendDate);
				exRapayService.updateUserAcct(uid, projectName);
			}

			// 更新自动化测试库的项目还款计划的收益和还款日期
			exRapayService.insertExtendToProPepayPlan(projectName, extendDate);

			// 保理账号退出登录
			kw.waitElementToBeClickable(OrUtil.getBy("account_logout_xpath", ObjectLib.XhhObjectLib));
			if (pubXhhService.logout(kw)) {
				log.info("保理账号登出成功");
			} else {
				log.error("保理账号登出失败");
				throw new Exception("保理账号登出失败");
			}

			// 投资人账户校验
			for (int i = 0; i < list.size(); i++) {
				uid = list.get(i).get("uid");
				// 获得详细的用户信息

				Map<String, String> userInfo = UserDao.getUserById(uid);
				String user = userInfo.get("mobile");
				String pwd = userInfo.get("login_pwd");

				// xhh-1077:case3.用户登录
				if (pubXhhService.login(kw, user, pwd)) {
					log.info(user + "登录成功");
				} else {
					log.error(user + "登录失败");
					throw new Exception("登录失败");
				}

				// xhh-1078:展期后，用户查看【账户总览】页面数据

				Map<String, String> exptectAcctInfo = AccountDao.getUserAcctByUid(uid);
				if (accountService.checkAccountInvestVal(kw, exptectAcctInfo)) {
					log.info("【账户总览】页面字段校验成功");
				} else {
					log.error("【账户总览】页面字段校验失败");
					result = false;
				}

				// xhh-1079:展期后，用户查看【账户总览-回款日历】区域数据

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
								+ "' and (stage='募集期' or stage='第1期' or stage='展期利息')", DbType.Local);
				String date2 = userrepayplan.get(0).get("repay_date");
				String capital = Loan.formatStr(userrepayplan.get(0).get("capital"));
				double sumprofit = 0.00;
				for (int j = 0; j < userrepayplan.size(); j++) {
					sumprofit = sumprofit + Double.parseDouble(userrepayplan.get(j).get("profit"));
				}
				DecimalFormat fm = new DecimalFormat("###,###.00");
				String profit = fm.format(sumprofit);
				String reminder = "投资" + projectName + "将收到" + capital + "元本金 和" + profit
						+ "元收益 ，预计16:00-24:00到账。本项目展期还款，最迟" + date2 + "到账。";
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

				// xhh-1081:展期后，用户查看【理财记录】数据
				Map<String, String> orderInfoMap = InvestRecordDao.getRecordByUserCode(uid, projectName);
				if (accountService.checkValInInvestRecord(kw, projectName, orderInfoMap)) {
					log.info("【理财记录】校验数据成功");
				} else {
					log.error("【理财记录】校验数据失败");
					result = false;
				}

				// 用户查看【资金记录】数据
				accountService.viewPayRecord(kw);

				// xhh-1083:展期后，用户查看【资金记录】数据
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
					log.info("登出成功");

				} else {
					log.error("登出失败");
					throw new Exception("登出失败");
				}
			}
			// 恢复系统时间
			SSHUtil.sshRecoverTime(log);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result = false;
			// 截图
			log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
		}
		assertTrue(result);
	}

}
