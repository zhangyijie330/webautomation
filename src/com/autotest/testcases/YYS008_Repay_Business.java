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
import com.autotest.dao.UserDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.DbType;
import com.autotest.or.ObjectLib;
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

public class YYS008_Repay_Business extends TestDriver {

	@Test
	public void test() throws Exception {
		// 标识当前用例是否验证通过
		boolean result = true;

		PubXhhService pubXhhService = new PubXhhService();
		RepayPlanService repayService = new RepayPlanService(log);
		KeyWords keyWords = new KeyWords(driver, log);
		try {

			String productID = "YYS008";
			// 要查找的项目名称
			String projectName = repayService.getProName(productID);
			System.out.println("项目名称为：" + projectName);
			// 要查找的项目编号
			String projectCode = repayService.getProCode(productID);
			System.out.println("项目的编号为：" + projectCode);
			// 项目还款日期
			String repayDate = repayService.getRepayDate(projectName);
			System.out.println("项目还款日期为：" + repayDate);

			// 获取当前服务器时间
			String currTime = SSHUtil.sshCurrentTime(log);
			log.info("当前服务器时间为：" + currTime);
			// 修改服务器时间到还款日
			SSHUtil.sshChangeTime(log, repayDate + " 02:00:00");

			// 保理公司账号密码
			String userName = BaseConfigUtil.getFactorName();
			String pwd = BaseConfigUtil.getFactorPwd();
			// 保理公司账号登录鑫合汇主站
			boolean loginResult = pubXhhService.login(keyWords, userName, pwd);
			if (!loginResult) {
				log.error("登录失败!用例结束");
				throw new Exception("登录失败！");
			}
			log.info("登录成功！");

			// 点击"还款计划"
			repayService.viewRepayPlanTab(keyWords);
			// 点击"月益升"
			repayService.viewMonthGrouth(keyWords);
			// 点击"还款日"，更改排列顺序
			repayService.clickRepayDate(keyWords);
			// 确认排序后页面刷新完成
			keyWords.isVisible(OrUtil.getBy("repay_table_repaydate_desc_xpath",
					ObjectLib.XhhObjectLib));

			// 开始查找项目所在行
			while (true) {
				// 判断当前页是否存在要查找的项目
				if (repayService.isFindProject(keyWords, projectName)) {
					log.info("找到项目： " + projectName);
					// 校验项目的还款计划字段
					if (repayService.checkRepayPlanInfo(keyWords, projectName,
							repayService.getExpectRepayPlanInfo(projectName,
									repayDate))) {
						log.info("【还款计划列表的字段及字段值】校验正确！");
					} else {
						log.error("【还款计划列表的字段及字段值】校验失败！");
						result = false;
					}

					if (repayService.viewRepayDetailPage(keyWords, projectName)) {
						log.info("跳转到【还款明细页面】成功");
					} else {
						log.error("跳转到【还款明细页面】失败");
						result = false;
						log.info(ScreenShotUtil.takeScreenshot(ThreadName,
								keyWords.driver));
						throw new Exception("跳转到【还款明细页面】失败！");
					}

					if (StringUtils.isEquals(
							repayService.getExpectedDetail(projectName),
							repayService.getRepayDetail(keyWords), log)) {
						log.info("【还款明细页面的字段及字段值】校验正确！");
					} else {
						log.info("【还款明细页面的字段及字段值】校验失败！");
						log.info(ScreenShotUtil.takeScreenshot(ThreadName,
								keyWords.driver));
						result = false;
					}

					// 判断项目还款明细页面是否存在分页
					boolean res = keyWords.isElementExist(By
							.xpath("//div[@class='page pageWraper']/a"));

					// 获取当期还款列表内容
					List<String> repayTableList = repayService
							.getAllPageRecordsStrList(keyWords, res,
									"repay_detail_tbody_xpath",
									"repay_detail_tbody_nextpage_xpath",
									"repay_detail_tbody_totalpage_xpath",
									"repay_detail_tbody_curpage_xpath");
					for (int i = 0; i < repayTableList.size(); i++) {
						System.out.println(repayTableList.get(i));
					}
					// 校验当期还款列表内容的期望值
					if (repayService.checkRepayTableRecords(projectName,
							repayDate, repayTableList)) {
						log.info("【当期还款明细信息列表】校验成功");
					} else {
						log.error("【当期还款明细信息列表】校验失败");
						result = false;
					}

					// 切换到还款情况签页
					keyWords.click(OrUtil.getBy(
							"repay_detail_repay_situation_xpath",
							ObjectLib.XhhObjectLib));
					for (int loop = 0; loop < 10; loop++) {
						boolean check = keyWords
								.isVisible(By
										.xpath("//div[@id='ajaxContent_Application_ProjectRepayment_repaymentList']/descendant::th[text()='期次']"));
						if (check) {
							log.info("切换到还款情况签页成功");
							break;
						} else if (loop < 10) {
							ThreadUtil.sleep(2);
						} else {
							result = false;
							throw new Exception("跳转到【还款情况签页】失败！");
						}
					}
					// 获取还款情况列表内容
					List<String> repayConditionTableList = repayService
							.getAllPageRecordsStrList(keyWords, false,
									"repay_detail_tbody_xpath", "", "", "");
					for (int i = 0; i < repayConditionTableList.size(); i++) {
						System.out.println(repayConditionTableList.get(i));
					}
					if (repayService.checkConditionTable(projectName,
							repayConditionTableList)) {
						log.info("【还款情况签页校验】成功");
					} else {
						log.error("【还款情况签页校验】失败");
					}
					// 切换到当期还款明细签页
					keyWords.click(OrUtil.getBy(
							"repay_detail_curr_coupon_xpath",
							ObjectLib.XhhObjectLib));
					keyWords.waitPageLoad();

					// 进行还款操作并回写还款计划表状态
					repayService.doRepay(keyWords, projectName, repayDate);

					// 还款进度校验
					if (repayService.checkRepayProgress(keyWords, projectName)) {
						log.info("【还款进度校验】成功");
						// 回写用户投资表的投资状态
						repayService.updateInvest(projectName, repayDate,
								"已还款结束");
						// 回写用户账户信息表和用户资金记录表
						repayService.updateAcct2(projectName, repayDate);
						// 回写保理账户资金流表
						repayService.updateFund(projectName, repayDate);

					} else {
						log.error("【还款进度校验】失败");
						result = false;
					}
					// 恢复服务器时间
					SSHUtil.sshRecoverTime(log);
					// 点击“资金记录”
					keyWords.click(OrUtil.getBy("cash_record_xpath",
							ObjectLib.XhhObjectLib));

					// 设置筛选日期
					// repayService.setDate(keyWords, repayDate, repayDate);
					// 获得收支记录表格需校验的资金记录内容，需要获取的记录条数等于当前还款计划表种的记录条数
					List<String> incomeExpenseTableList = repayService
							.getTopRecordsStrList(keyWords,
									"cash_record_table_xpath",
									repayTableList.size());
					for (int i = 0; i < incomeExpenseTableList.size(); i++) {
						System.out.println("i="
								+ i
								+ "-->"
								+ StringUtils.format(incomeExpenseTableList
										.get(i)));
					}
					// 检验资金记录表
					if (repayService.checkCashRecord(incomeExpenseTableList)) {
						log.info("【资金记录表校验】成功");
					} else {
						log.error("【资金记录表校验】失败");
						result = false;
					}
					// 检验保理账户退出系统
					if (pubXhhService.logout(keyWords)) {
						log.info("【保理账户退出】成功！");
					} else {
						log.error("【保理账户退出】失败！");
						result = false;
					}

					// 用户登入鑫合汇主站进行账户数据检验
					List<Map<String, String>> userList = UserTempDao
							.getInvestUserByProNo(productID);
					for (Map<String, String> map : userList) {
						Map<String, String> userInfo = UserDao.getUserById(map
								.get("uid"));
						String uid = userInfo.get("id");
						String user = userInfo.get("mobile");
						String passwd = userInfo.get("login_pwd");

						log.info("【查看用户:" + user + "】相关信息：");
						boolean checkUserRes = checkUser(keyWords, projectName,
								uid, user, passwd, repayDate, pubXhhService,
								repayService);
						if (checkUserRes) {
							log.info("【检验用户:" + user + "】信息成功！");
						} else {
							log.info("【检验用户:" + user + "】信息失败！");
							result = false;
						}
					}
					break;
				} else {
					log.info("在当前页没找到项目");
					if (keyWords.isElementExist(By
							.xpath("//div[@class='page pageWraper']"))) {
						log.info("还款计划页存在分页");
						int toltalPageNum = repayService
								.getRepayPlanTotalPageNum(keyWords);
						log.info("还款计划总页数为:" + toltalPageNum);
						int curPageNum = repayService
								.getRepayPlanCurrentPageNum(keyWords);
						// 判断是否是最后一页
						if (curPageNum < toltalPageNum) {
							log.info("当前页是： " + curPageNum + "，没找到项目，跳转到下一页!");
							// 点击下一页按钮
							keyWords.click(OrUtil.getBy(
									"repay_next_page_xpath",
									ObjectLib.XhhObjectLib));
							ThreadUtil.sleep(3);
						} else {
							log.error("找不到项目" + projectName);
							result = false;
							break;
						}
					} else {
						log.error("项目不存在分页，找不到项目" + projectName);
						result = false;
						break;
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception", e);
			log.info(ScreenShotUtil.takeScreenshot(ThreadName, keyWords.driver));
		} finally {
			SSHUtil.sshRecoverTime(log);
		}
		// 验证整个测试用例是否成功
		System.out.println(result);
		assertTrue(result);
	}

	/**
	 * 功能： xhh-:case3鑫合汇主站登录以后查看账户信息
	 * 
	 * @param repayDate
	 * @param uid
	 * @param userName
	 * @param pwd
	 * @param repayDate
	 * @return boolean
	 * @throws Exception
	 */
	public boolean checkUser(KeyWords kw, String productCode, String uid,
			String userName, String pwd, String repayDate,
			PubXhhService pubXhhService, RepayPlanService repayService)
			throws Exception {
		boolean result = true;
		UserAccountService accountService = new UserAccountService(log);
		if (pubXhhService.login(kw, userName, pwd)) {
			log.info(userName + "登录鑫合汇主站成功！");

			// 校验【账户总览】页面字段值
			Map<String, String> exptectAcctInfo = AccountDao
					.getUserAcctByUid(uid);
			if (accountService.checkAccountInvestVal(kw, exptectAcctInfo)) {
				log.info("【账户总览页面字段校验】成功");
			} else {
				log.error("【账户总览页面字段校验】失败");
				result = false;
			}
			// xhh-:用户查看【账户总览-回款日历】区域数据
			List<Map<String, String>> exptectCalendar = DbUtil.queryDataList(
					"select * from finance_record where finance_type='回款' and uid='"
							+ uid + "' and date='" + repayDate + "'",
					DbType.Local);
			double totalAmount = 0.00;
			for (Map<String, String> map : exptectCalendar) {
				totalAmount = totalAmount
						+ Double.parseDouble(map.get("amount"));
			}
			DecimalFormat fm = new DecimalFormat("###,##0.00");
			if (pubXhhService.checkReturnMoneyCalendar(kw, repayDate,
					String.valueOf(exptectCalendar.size()),
					fm.format(totalAmount), "已回")) {
				log.info("【回款日历】校验成功");
			} else {
				log.error("【回款日历】校验失败");
				result = false;
			}

			// 1.点击头像左侧的【投资管理】入口
			accountService.viewFinaceManagePage(kw);
			// 点击【所有】入口并检查理财记录列表数据
			accountService.viewAllType(kw);
			// 校验条数
			int investRowCount = InvestRecordDao.getCountByUser(uid);
			if (accountService.checkRowCount(kw, investRowCount)) {
				log.info("【理财记录】校验记录条数成功");
			} else {
				log.error("【理财记录】校验记录条数失败");
				result = false;
			}
			// 获取理财记录信息
			Map<String, String> orderInfoMap = InvestRecordDao
					.getRecordByUserCode(uid, productCode);
			// 校验用户理财记录
			if (accountService.checkValInInvestRecord(kw, productCode,
					orderInfoMap)) {
				log.info("【理财记录】校验数据成功");
			} else {
				log.error("【理财记录】校验数据失败");
				result = false;
			}
			// 查看资金记录页面
			accountService.viewPayRecord(kw);
			// 校验【资金记录】数据条数
			int financeRowCount = FinanceRecordDao.getCountByUser(uid);
			if (accountService.checkRowCount(kw, financeRowCount)) {
				log.info("【资金记录】校验记录条数成功");
			} else {
				log.error("【资金记录】校验记录条数失败");
				log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
			}
			// 用户查看【资金记录】最新增加的一条数据

			if (repayService.checkUserRepayFinanceRecord(productCode,
					repayDate, uid, kw)) {
				log.info("【资金记录】校验数据成功");
			} else {
				log.error("【资金记录】校验数据失败");
				result = false;
				log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
			}

			// 调用登出函数
			if (pubXhhService.logout(kw)) {
				log.info("logout successfully");
			} else {
				log.info("logout failed");
				result = false;
				log.info(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
			}
		} else {
			log.error(userName + "登录鑫合汇主站失败！");
			result = false;
		}
		return result;
	}

}
