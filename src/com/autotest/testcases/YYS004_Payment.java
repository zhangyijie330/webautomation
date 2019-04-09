package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import com.autotest.dao.AccountDao;
import com.autotest.dao.FinanceRecordDao;
import com.autotest.dao.InvestRecordDao;
import com.autotest.dao.UserDao;
import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.DbType;
import com.autotest.enums.FinanceType;
import com.autotest.service.bmpService.Loan;
import com.autotest.service.bmpService.PubBmpService;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.RepayPlanService;
import com.autotest.service.xhhService.UserAccountService;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.DbUtil;
import com.autotest.utility.ScreenShotUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * YYS004项目支付：月益升，3天募集期，等额本息
 * 
 * @author wb0005
 * 
 */
public class YYS004_Payment extends TestDriver {

	@Test
	public void Test() {
		boolean bFinalResult = true;

		KeyWords kw = new KeyWords(driver, log);
		PubBmpService pubBmpService = new PubBmpService();
		PubXhhService publicService = new PubXhhService();
		RepayPlanService repayService = new RepayPlanService(log);
		Loan loan = new Loan(log);
		UserAccountService accountService = new UserAccountService(log);
		String productid = "YYS004";
		String bmpuserName = BaseConfigUtil.getBMPName();
		String bmppwd = BaseConfigUtil.getBMPPwd();
		String bluserName = BaseConfigUtil.getFactorName();
		String blpwd = BaseConfigUtil.getFactorPwd();
		try {
			boolean bResult = true;
			String productCode = Loan.getprodtemp(productid)
					.get("product_name");
			String serialnumber = Loan.getloantemp(productid).get(
					"serialnumber_id");
			List<Map<String, String>> lst = UserTempDao
					.getInvestUserByProNo(productid);
			// int rowcount = Integer.parseInt(Loan.getloantemp(productid).get(
			// "row_count"));
			int fundrecordcount = Integer.parseInt(Loan.getloantemp(productid)
					.get("fundrecord_count"));

			boolean loginResult = pubBmpService.login(kw, bmpuserName, bmppwd);
			if (!loginResult) {
				log.error("登录失败!用例结束");
				throw new Exception("login failed");
			}

			// 进入项目放款页面

			loan.EnterLoan(kw);

			// 输入项目名称查询项目 校 验查询结果

			Map<String, String> prodinfo = Loan.getprodinfo(productCode);
			String getProfit = Loan.getprofit(productCode);
			if (loan.Checkprod(kw, productCode, productid, prodinfo, getProfit)) {
				log.info("【项目放款】放款字段校验成功！");
			} else {
				log.error("【项目放款】放款字段校验失败！");
				bResult = false;
			}

			// 点击支付支付项目 验证支付金额

			if (loan.Checkpayment(kw, prodinfo)) {
				log.info("【项目放款】校验支付金额正确！");
			} else {
				log.error("【项目放款】校验支付金额错误！");

				throw new Exception("check payment failed");
			}

			// 填写支付流水号 确认支付保理项目 判断是否项目支付完成

			if (loan.Finishpay(kw, serialnumber)) {
				log.info("【项目放款】支付完成！");

				// 项目完成后向数据库资金记录表回写数据

				loan.fundrecordwrite(productCode, lst);

				// 修改invest_record表的status字段为待还款

				loan.updateInvestrecoed(productCode);

				// 修改user_acct表的total_invest和will_profit字段

				for (int i = 0; i < lst.size(); i++) {
					String uid = lst.get(i).get("uid");
					loan.updateuseracct(uid, productCode);
				}

			} else {
				log.error("【项目放款】支付失败！");

				throw new Exception("pay failed");
			}

			// 确认保理项目支付结果

			if (loan.Confirmresult(kw)) {
				log.info("【项目放款】放款成功！");

			} else {
				log.error("【项目放款】放款失败！");

				throw new Exception("Confirmresult failed");
			}

			// 用户登录鑫合汇主站

			for (int i = 0; i < lst.size(); i++) {
				String uid = lst.get(i).get("uid");

				// 获得详细的用户信息

				Map<String, String> userInfo = UserDao.getUserById(uid);
				String user = userInfo.get("mobile");
				String pwd = userInfo.get("login_pwd");

				if (!publicService.login(kw, user, pwd)) {
					log.info("登录失败！");
					throw new Exception("login failed");
				}

				// 校验【账户总览】页面

				Map<String, String> exptectAcctInfo = AccountDao
						.getUserAcctByUid(uid);
				if (accountService.checkAccountInvestVal(kw, exptectAcctInfo)) {
					log.info("【账户总览】页面字段校验成功");
				} else {
					log.error("【账户总览】页面字段校验失败");
					bResult = false;
				}

				// 校验回款日历

				String date = "";
				String amount = "";
				String count = "";
				String status = "";
				List<Map<String, String>> relstCalendar = publicService
						.mergeList(uid);
				for (Map<String, String> mapCalendar : relstCalendar) {
					date = mapCalendar.get("date");
					amount = mapCalendar.get("amount");
					count = mapCalendar.get("count");
					status = mapCalendar.get("status");
					if (publicService.checkReturnMoneyCalendar(kw, date, count,
							amount, status)) {
						log.info("回款日历校验成功！");
					} else {
						log.error("回款日历校验失败！");
						bResult = false;
					}
				}

				// 校验回款提醒

				List<Map<String, String>> userrepayplan = DbUtil.queryDataList(
						"select * from user_repay_plan where uid='" + uid
								+ "' and name='" + productCode
								+ "' and (stage='募集期' or stage='第1期')",
						DbType.Local);
				String date2 = userrepayplan.get(0).get("repay_date");
				String capital = Loan.formatStr(userrepayplan.get(1).get(
						"capital"));
				double sumprofit = 0;
				for (int j = 0; j < userrepayplan.size(); j++) {
					sumprofit = sumprofit
							+ Double.parseDouble(userrepayplan.get(j).get(
									"profit"));
				}
				DecimalFormat fm = new DecimalFormat("###,##0.00");
				String profit = fm.format(sumprofit);
				String reminder = "投资" + productCode + "将收到" + capital
						+ "元本金 和" + profit + "元收益 ，预计16:00-24:00到账。";
				if (publicService.checkReturnMoneyReminder(kw, date2, reminder)) {
					log.info("校验回款提醒成功！");
				} else {
					log.error("未找到该回款提醒！");
					bResult = false;
				}

				// 用户访问【理财记录】页面

				accountService.viewFinaceManagePage(kw); // 理财记录中点击所有类型
				accountService.viewAllType(kw);

				// 校验【理财记录】表格中的记录条数
				int investRowCount = InvestRecordDao.getCountByUser(uid);
				if (accountService.checkRowCount(kw, investRowCount)) {
					log.info("【理财记录】中的记录条数正确！");
				} else {
					log.error("【理财记录】中的记录条数错误！");
					bResult = false;
				}

				// 用户查看【理财记录】数据

				Map<String, String> orderInfoMap = InvestRecordDao
						.getRecordByUserCode(uid, productCode);
				if (accountService.checkValInInvestRecord(kw, productCode,
						orderInfoMap)) {
					log.info("【理财记录】校验数据成功");
				} else {
					log.error("【理财记录】校验数据失败");
					bResult = false;
				}

				// 用户查看【资金记录】数据

				accountService.viewPayRecord(kw);

				// 用户查看【资金记录】数据

				Map<String, String> expectPayRecord = FinanceRecordDao
						.getRecordByUserCode(uid, productCode,
								FinanceType.invest);
				if (accountService.checkValInPayRecord(kw, productCode,
						expectPayRecord, FinanceType.invest)) {
					log.info("【资金记录】校验数据成功");
				} else {
					log.error("【资金记录】校验数据失败");
					bResult = false;
				}

				// 调用登出函数

				if (publicService.logout(kw)) {
					log.info("logout successfully");

				} else {
					bResult = false;
					log.error("logout failed");
					throw new Exception("登出失败");
				}
			}

			// 保理账户登录

			if (publicService.login(kw, bluserName, blpwd)) {
				log.info("保理账户登录成功！");

				// 进入资金记录页面

				if (loan.Enterbaoli(kw)) {
					log.info("【进入资金记录页面成功！】");
				} else {
					log.error("【进入资金记录页面失败！】");

					throw new Exception("enter failed");
				}

				// 根据日期筛选数据

				loan.setdate(kw, repayService);
				ThreadUtil.sleep(3);

				// 获取资金记录校验中的前count条记录

				List<String> incomeExpenseTableList = repayService
						.getTopRecordsStrList(kw, "cash_record_table_xpath",
								fundrecordcount);
				for (int i = 0; i < incomeExpenseTableList.size(); i++) {
					System.out
							.println("i="
									+ i
									+ "-->"
									+ StringUtils.format(incomeExpenseTableList
											.get(i)));
				}

				// 校验资金记录数据

				if (loan.getdata(kw, incomeExpenseTableList, productCode)) {
					log.info("【资金记录数据校验正确！】");
				} else {
					log.error("【资金记录数据校验错误！】");
					bResult = false;
				}

				// 保理账号登出

				if (publicService.logout(kw)) {
					log.info("logout successfully");

				} else {
					bResult = false;
					log.error("logout failed");
					throw new Exception("登出失败");
				}

			} else {
				log.error("保理账户登录失败！");
				throw new Exception("login failed");
			}
			bFinalResult = bFinalResult & bResult;
		} catch (Exception e) {
			log.error("Exception", e);
			log.error(ScreenShotUtil.takeScreenshot(ThreadName, kw.driver));
			bFinalResult = false;
		}

		assertTrue(bFinalResult);
	}

}
