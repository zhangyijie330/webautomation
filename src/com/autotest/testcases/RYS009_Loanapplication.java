package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.util.List;
import java.util.Map;

import org.openqa.selenium.JavascriptExecutor;
import org.testng.annotations.Test;

import com.autotest.dao.UserTempDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.or.ObjectLib;
import com.autotest.service.bmpService.Loan;
import com.autotest.service.bmpService.PubBmpService;
import com.autotest.utility.OrUtil;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.ScreenShotUtil;

/**
 * RYS009放款申请：日益升，1天募集期，到期还本付息
 * 
 * @author wb0005
 * 
 */
public class RYS009_Loanapplication extends TestDriver {

	@Test
	public void Test() {
		Loan loan = new Loan(log);
		String productid = "RYS009";
		boolean bFinalResult = true;
		KeyWords kw = new KeyWords(driver, log);
		PubBmpService pubBmpService = new PubBmpService();
		String bmpuserName = "wb005";
		String bmppwd = "upg2015";
		try {
			boolean bResult = true;
			String productCode = Loan.getprodtemp(productid)
					.get("product_name");
			// String productCode = "ZYJ20160912001";
			boolean loginResult = pubBmpService.login(kw, bmpuserName, bmppwd);
			List<Map<String, String>> lst = UserTempDao
					.getInvestUserByProNo(productid);
			// 系统登录
			if (!loginResult) {
				log.error("登录失败!用例结束");

				throw new Exception("login failed");
			}

			// 进入放款申请页面
			if (loan.enterLoanapply(kw)) {
				log.info("【放款申请】进入放款申请页面成功！");
			} else {
				log.error("【放款申请】进入放款申请页面失败！");

				throw new Exception("enter failed");
			}

			// 查询放款申请项目
			if (loan.searchproj(kw, productCode)) {
				log.info("【放款申请】查询成功！");
			} else {
				log.error("【放款申请】查询失败！");

				throw new Exception("search failed");
			}

			// 选择项目点击提交
			if (loan.loanOperation(kw)) {
				log.info("【放款申请】显示放款信息！");
			} else {
				log.error("【放款申请】未显示放款信息！");

				throw new Exception("operat failed");
			}

			// 确认提交放款申请
			if (loan.subApply(kw)) {
				log.info("【放款申请】提交放款申请成功！");
			} else {
				log.error("【放款申请】提交放款申请失败！");

				throw new Exception("apply failed");
			}

			// 获取服务器当前日期
			String nowDate = SSHUtil.sshCurrentDate(log);

			kw.switchToDefaultFrame();// 切换回默认iframe
			kw.click(OrUtil.getBy("process_xpath", ObjectLib.BMPObjectLib));// 流程管理
			kw.click(OrUtil.getBy("todotask_xpath", ObjectLib.BMPObjectLib));// 待处理任务
			kw.switchToFrame("work_frame");// 切换iframe

			// 处理待处理任务：i=0-->放款申请确认; i=1-->财务费用确认岗; i=2-->资金制单岗; i=3-->资金复核岗
			for (int i = 0; i < 4; i++) {
				int taskName = i;
				// 查询待处理任务
				if (loan.searchApply(kw, taskName, nowDate)) {
					log.info("【放款申请】查询任务成功！");
				} else {
					log.error("【放款申请】查询任务失败！");

					throw new Exception("search failed");
				}
				// 校验任务处理页面字段
				if (loan.checkInfo(kw, productCode, productid)) {
					log.info("字段信息校验成功！");
				} else {
					log.error("字段信息校验失败！");
					bResult = false;
				}
				// 上传附件
				((JavascriptExecutor) driver)
						.executeScript("window.scrollTo(0, document.body.scrollHeight)");// 页面滚动最底部
				if (loan.uploadFile(kw, i, productid)) {
					log.info("【放款申请】上传附件成功！");
				} else {
					log.error("【放款申请】上传附件失败！");

					throw new Exception("upload failed");
				}

				if (i == 3) {
					// 提交任务处理
					if (loan.finalSubmit(kw)) {
						// 项目完成后向数据库资金记录表回写数据

						loan.fundrecordwrite(productCode, lst);

						// 修改invest_record表的status字段为待还款

						loan.updateInvestrecoed(productCode);

						// 修改user_acct表的total_invest和will_profit字段

						for (int j = 0; j < lst.size(); j++) {
							String uid = lst.get(j).get("uid");
							loan.updateuseracct(uid, productCode);
						}
						log.info("【放款申请】提交处理成功！");

					} else {
						log.error("【放款申请】提交处理失败！");

						throw new Exception("submit failed");
					}
				} else {
					// 提交任务处理
					if (loan.submitTask(kw)) {
						log.info("【放款申请】提交处理成功！");

					} else {
						log.error("【放款申请】提交处理失败！");

						throw new Exception("submit failed");
					}
				}

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
