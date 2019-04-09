package com.autotest.testcases;

import static org.testng.AssertJUnit.assertTrue;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.testng.annotations.Test;

import com.autotest.dao.ProductDao;
import com.autotest.dao.ProjectReleaseDao;
import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.enums.BusinessType;
import com.autotest.enums.DateType;
import com.autotest.enums.ProductType;
import com.autotest.enums.RepayWay;
import com.autotest.service.bmpService.FactoringRelease;
import com.autotest.service.bmpService.Product;
import com.autotest.service.bmpService.ProjectAudit;
import com.autotest.service.bmpService.PubBmpService;
import com.autotest.service.xhhService.FactoringProject;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.DateUtils;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.ScreenShotUtil;

/**
 * old:测试集 : 保理项目（YYS20160718004/月益升/2天募集期/等额本息）--------------And--------------
 * new:测试集 : 保理项目（YYS20160718004/月益升/3天募集期/等额本息）
 * 
 * @author wb0002
 * 
 */
public class Project_Release_YYS004 extends TestDriver {

	public String			borrowerName	= "";				// 借款人，用于保存所选中的借款人的姓名

	HashMap<String, String>	projectInfo		= new HashMap<>();	// 存放项目信息的HashMap

	@Test
	public void test() {
		// 标识当前用例是否验证通过
		boolean result = false;
		String case_id = "YYS20160718004";
		Map<String, String> configMap = null;
		try {
			configMap = ProjectReleaseDao.readProjectRelease(case_id);
		} catch (SQLException e1) {
			e1.printStackTrace();
			log.error("读取项目配置错误!", e1);
		}
		if (null == configMap) {
			return;
		}
		PubBmpService pubBmpService = new PubBmpService();
		KeyWords keyWords = new KeyWords(driver, log);
		try {
			// 恢复服务器当前时间
			SSHUtil.sshRecoverTime(log);
			// 登录鑫合汇业务管理平台
			String userName = "wb002";
			String pwd = "upg2015";
			userName = configMap.get("username");
			pwd = configMap.get("pwd");
			boolean loginResult = pubBmpService.login(keyWords, userName, pwd);
			if (!loginResult) {
				log.error("登录失败!用例结束");
				return;
			}
			//
			FactoringRelease factoringRelease = new FactoringRelease(keyWords);
			boolean tmpResult = false;
			//
			tmpResult = factoringRelease.enterFactoringRelease();
			if (!tmpResult) {
				log.error("访问【项目运营-保理发布】页面失败!");
				return;
			}
			//
			tmpResult = factoringRelease.enterNewReleaseProject();
			if (!tmpResult) {
				log.error("进入【保理发布-基本信息】页签失败!");
				return;
			}
			//
			boolean ifJoinPlan = false;// 是否可加入鑫计划
			int borrowerType = 2;// 借款人类型
			int borrower = 1;// 借款人
			long financingScale = 30000;// 融资规模
			int timeOut = 12;// 用款期限
			DateType dateType = DateType.month;// 用款期限单位(月)
			double rate = 11;// 预期年化利率
			RepayWay repayWay = RepayWay.permonth;// 还款方式
			BusinessType businessType = BusinessType.C;// 业务类型
			// String createTime = DateUtils.getNowDateAddMinute(5);
			// String beginTime = DateUtils.getNowDateAddMinute(10);
			// String endTime = DateUtils.getSomeDayEndTime(2);// 3天募集期
			if (configMap.get("ifjoinplan").equals("true")) {
				ifJoinPlan = true;
			} else {
				ifJoinPlan = false;
			}
			borrowerType = Integer.parseInt(configMap.get("borrowertype"));
			borrower = Integer.parseInt(configMap.get("borrower"));
			financingScale = Long.parseLong(configMap.get("financingscale"));
			timeOut = Integer.parseInt(configMap.get("timeout"));
			if (configMap.get("datetype").equals("DateType.day")) {
				dateType = DateType.day;
			} else if (configMap.get("datetype").equals("DateType.month")) {
				dateType = DateType.month;
			} else if (configMap.get("datetype").equals("DateType.year")) {
				dateType = DateType.year;
			} else {
				throw new Exception();
			}
			rate = Double.parseDouble(configMap.get("rate"));
			if (configMap.get("repayway").equals("RepayWay.E")) {
				repayWay = RepayWay.E;
			} else if (configMap.get("repayway").equals("RepayWay.C")) {
				repayWay = RepayWay.C;
			} else if (configMap.get("repayway").equals("RepayWay.D")) {
				repayWay = RepayWay.D;
			} else if (configMap.get("repayway").equals("RepayWay.halfyear")) {
				repayWay = RepayWay.halfyear;
			} else if (configMap.get("repayway").equals("RepayWay.permonth")) {
				repayWay = RepayWay.permonth;
			} else if (configMap.get("repayway")
					.equals("RepayWay.PermonthFixN")) {
				repayWay = RepayWay.PermonthFixN;
			} else {
				throw new Exception();
			}
			if (configMap.get("businesstype").equals("BusinessType.A")) {
				businessType = BusinessType.A;
			} else if (configMap.get("businesstype").equals("BusinessType.B")) {
				businessType = BusinessType.B;
			} else if (configMap.get("businesstype").equals("BusinessType.C")) {
				businessType = BusinessType.C;
			} else if (configMap.get("businesstype").equals("BusinessType.D")) {
				businessType = BusinessType.D;
			} else if (configMap.get("businesstype").equals("BusinessType.E")) {
				businessType = BusinessType.E;
			} else if (configMap.get("businesstype").equals("BusinessType.F")) {
				businessType = BusinessType.F;
			} else if (configMap.get("businesstype").equals("BusinessType.G")) {
				businessType = BusinessType.G;
			} else {
				throw new Exception();
			}
			String createTime = DateUtils.getSomeDayAddMinute2(
					SSHUtil.sshCurrentTime(log),
					Integer.parseInt(configMap.get("createtime")));
			String beginTime = DateUtils.getSomeDayAddMinute2(
					SSHUtil.sshCurrentTime(log),
					Integer.parseInt(configMap.get("begintime")));
			String endTime = DateUtils.simpleDateFormat2(DateUtils.dayAddDays(
					DateUtils.stringToDate2(SSHUtil.sshCurrentDate(log)),
					(Integer.parseInt(configMap.get("mujiqi")) - 1)))
					+ " 23:59:59";
			System.out.println("createTime=[" + createTime + "];beginTime=["
					+ beginTime + "];endTime=[" + endTime + "]");
			borrowerName = factoringRelease
					.writeBasicInformationFactoringProjects(ifJoinPlan,
							borrowerType, borrower, financingScale, timeOut,
							dateType, rate, repayWay, businessType, createTime,
							beginTime, endTime);
			if (null == borrowerName) {
				log.error("填写保理项目基本信息失败!");
				return;
			}
			projectInfo.put("borrowerName", borrowerName);// 将借款人存入项目信息HashMap
			//
			tmpResult = factoringRelease.enterAddInformation();
			if (!tmpResult) {
				log.error("进入【保理发布-附加信息】页签失败!");
				return;
			}
			//
			ProductType productType = ProductType.YYS;// 产品类型
			String productName = null;// 产品名称
			int num = -1;
			while (0 != num) {
				productName = Product.getProductName(productType);// 产品名称
				num = ProductDao.queryProjectByProject(productName);
				log.info("num=[" + num + "]");
				if (num == 0) {
					break;
				}
			}
			log.info("productName=[" + productName + "]");
			String moneyUsing = "临时周转";// 资金用途
			String repayOrigin = "经营所得";// 还款来源
			int education = 1;// 学历
			long income = 1000000;// 个人年现金收入
			int account = 701;// 投资资金转入账户
			String contract = productName + "合同";// 原债权人与原债务人合同
			String contractNo = "xm004";// 原债权人与原债务人合同合同编号
			String letterNo = "dbbh004";// 同意担保函编号
			String contractNo2 = "blh004";// 国内保理合同编号
			String name = "孙群领";// 原债务人姓名名称
			String idNumber = "330722196501292110";// 原债务人证件号码
			int buyBackNum = 0;// 提前回购操作通知发送提前量
			int beforeSubmitNum = 1;// 展期申请提交提前量
			int paymentRatio = 0;// 违约金支付比例
			moneyUsing = configMap.get("moneyusing");
			repayOrigin = configMap.get("repayorigin");
			education = Integer.parseInt(configMap.get("education"));
			income = Long.parseLong(configMap.get("income"));
			account = Integer.parseInt(configMap.get("account"));
			contractNo = configMap.get("contractno");
			letterNo = configMap.get("letterno");
			contractNo2 = configMap.get("contractno2");
			name = configMap.get("name");
			idNumber = configMap.get("idnumber");
			buyBackNum = Integer.parseInt(configMap.get("buybacknum"));
			beforeSubmitNum = Integer
					.parseInt(configMap.get("beforesubmitnum"));
			paymentRatio = Integer.parseInt(configMap.get("paymentratio"));
			tmpResult = factoringRelease.writeAddInfo(productName, moneyUsing,
					repayOrigin, education, income, account, contract,
					contractNo, letterNo, contractNo2, name, idNumber,
					buyBackNum, beforeSubmitNum, paymentRatio);
			if (!tmpResult) {
				log.error("填写保理项目附加信息失败!");
				return;
			}
			//
			tmpResult = factoringRelease.releaseProject();
			if (!tmpResult) {
				log.error("保理项目发布失败!");
				return;
			}
			//
			tmpResult = factoringRelease.releaseResultConfirm();
			if (!tmpResult) {
				log.error("发布结果确认失败!");
				return;
			}
			// //////////////////////////////////////////////////////
			//
			ProjectAudit projectAudit = new ProjectAudit(keyWords);
			tmpResult = projectAudit.enterProjectAudit();
			if (!tmpResult) {
				log.error("访问【项目运营-项目审核】页面失败!");
				return;
			}
			//
			tmpResult = projectAudit.writeFilter(productName);
			if (!tmpResult) {
				log.error("填写筛选条件失败!");
				return;
			}
			//
			tmpResult = projectAudit.queryProduct();
			if (!tmpResult) {
				log.error("筛选出刚才发布的保理项目失败!");
				return;
			}
			// 获取项目的发布时间，并进行相关字段校验
			String time = projectAudit.checkProductInfo(productName,
					borrowerName, productType, financingScale, rate, repayWay,
					businessType);
			if (time.length() != 19) {
				log.error("校验列表展示的字段及字段值失败!");
				return;
			}
			projectInfo.put("time", time);// 将项目发布时间存入项目信息HashMap
			//
			tmpResult = projectAudit.selectProject();
			if (!tmpResult) {
				log.error("勾选刚才发布的保理项目失败!");
				return;
			}
			//
			tmpResult = projectAudit.enterFinancing();
			if (!tmpResult) {
				log.error("进入【项目审核-融资信息】页签失败!");
				return;
			}
			//
			tmpResult = projectAudit.enterProjectAuditPage();
			if (!tmpResult) {
				log.error("进入【项目审核-项目审核】页签失败!");
				return;
			}
			//
			int isXzdValue = -1;// 是否鑫整点
			int isExtendValue = -1;// 是否展期
			int isExtendTime = -1;// 展期天数
			int putInSite = 1234567890;// 投放站点
			int isEarlyRepayValue = -1;// 是否可以提前还款
			int earlyRepayDays = -1;// 可以提前还款天数
			int isTransferValue = -1;// 是否可以变现
			int putInTerminal = 0;// 投放终端
			String contract_no = "hth004";// 合同编号
			isXzdValue = Integer.parseInt(configMap.get("isxzdvalue"));
			isExtendValue = Integer.parseInt(configMap.get("isextendvalue"));
			isExtendTime = Integer.parseInt(configMap.get("isextendtime"));
			putInSite = Integer.parseInt(configMap.get("putinsite"));
			isEarlyRepayValue = Integer.parseInt(configMap
					.get("isearlyrepayvalue"));
			earlyRepayDays = Integer.parseInt(configMap.get("earlyrepaydays"));
			isTransferValue = Integer
					.parseInt(configMap.get("istransfervalue"));
			putInTerminal = Integer.parseInt(configMap.get("putinterminal"));
			contract_no = configMap.get("contract_no");
			tmpResult = projectAudit
					.writeProjectAuditInfo(isXzdValue, isExtendValue,
							isExtendTime, putInSite, isEarlyRepayValue,
							earlyRepayDays, isTransferValue, putInTerminal,
							contract_no);
			if (!tmpResult) {
				log.error("填写项目审核信息失败!");
				return;
			}
			// 读取项目信息
			HashMap<String, String> hashMap = projectAudit.getProjectInfo(
					timeOut, dateType, repayWay);
			if (null != hashMap) {
				// 遍历hashMap
				Iterator<Entry<String, String>> iter = hashMap.entrySet()
						.iterator();
				while (iter.hasNext()) {
					Map.Entry<String, String> entry = (Map.Entry<String, String>) iter
							.next();
					String key = entry.getKey();
					String value = entry.getValue();
					projectInfo.put(key, value);// 将获取到的项目信息都存入projectInfo中
				}
			}
			//
			tmpResult = projectAudit.submit();
			if (!tmpResult) {
				log.error("项目审核通过失败!");
				return;
			}
			// 将发布成功的项目信息写入到数据库中
			tmpResult = projectAudit.writeDB(projectInfo, repayWay, timeOut,
					dateType, log, "YYS004");
			if (!tmpResult) {
				log.error("将发布的项目信息写入数据库失败!");
				return;
			}
			// ////////////////////////////////////////////////////////////////
			//
			FactoringProject FactoringProject = new FactoringProject(keyWords);
			// 访问鑫合汇主站
			String url = BaseConfigUtil.getHomePageURL();
			tmpResult = FactoringProject.openIndex(url);
			if (!tmpResult) {
				log.error("访问鑫合汇主站失败!");
				throw new Exception("访问鑫合汇主站失败!");
			}
			// 访问【投资理财-投资项目-精品推荐】页面
			tmpResult = FactoringProject.accessPage();
			if (!tmpResult) {
				log.error("访问【投资理财-投资项目-精品推荐】页面失败!");
				throw new Exception("访问【投资理财-投资项目-精品推荐】页面失败!");
			}
			// 访问【投资理财-投资项目-日益升】页面 / 访问【投资理财-投资项目-月益升】页面
			int projectType = 3;
			projectType = Integer.parseInt(configMap.get("projecttype"));
			tmpResult = FactoringProject.accessProjectPage(projectType);
			if (!tmpResult) {
				log.error("访问【投资理财-投资项目-日益升】页面 / 访问【投资理财-投资项目-月益升】页面 失败!");
				throw new Exception(
						"访问【投资理财-投资项目-日益升】页面 / 访问【投资理财-投资项目-月益升】页面 失败!");
			}
			// 在【日益升】列表等待至保理项目的【融资发标时间】/ 在【月益升】列表等待至保理项目的【融资发标时间】
			Date date = DateUtils.stringToDate(createTime);
			Date nowDate = DateUtils.stringToDate(SSHUtil.sshCurrentTime(log));
			tmpResult = FactoringProject.waitProjectOpen(date, nowDate);
			if (!tmpResult) {
				log.error("在【日益升】列表等待至保理项目的【融资发标时间】/ 在【月益升】列表等待至保理项目的【融资发标时间】 失败!");
				throw new Exception(
						"在【日益升】列表等待至保理项目的【融资发标时间】/ 在【月益升】列表等待至保理项目的【融资发标时间】 失败!");
			}
			// 至【融资发标时间】后，刷新页面
			tmpResult = FactoringProject.refreshPage();
			if (!tmpResult) {
				log.error("至【融资发标时间】后，刷新页面失败!");
				throw new Exception("至【融资发标时间】后，刷新页面失败!");
			}
			// 校验【日益升】列表展示的保理项目字段及字段值 / 校验【月益升】列表展示的保理项目字段及字段值
			String projectName = productName;
			String minBidAmount = hashMap.get("minBidAmount");
			tmpResult = FactoringProject.searchProject(projectName, rate,
					timeOut, dateType, repayWay, minBidAmount);
			if (!tmpResult) {
				log.error("校验【日益升】列表展示的保理项目字段及字段值 / 校验【月益升】列表展示的保理项目字段及字段值失败!");
				throw new Exception(
						"校验【日益升】列表展示的保理项目字段及字段值 / 校验【月益升】列表展示的保理项目字段及字段值失败!");
			}
			// 校验【日益升】列表展示的保理项目的状态 / 校验【月益升】列表展示的保理项目的状态
			nowDate = DateUtils.stringToDate(SSHUtil.sshCurrentTime(log));
			tmpResult = FactoringProject.checkProjectStatus(projectName, date,
					nowDate);
			if (!tmpResult) {
				log.error("校验【日益升】列表展示的保理项目的状态 / 校验【月益升】列表展示的保理项目的状态失败!");
				throw new Exception(
						"校验【日益升】列表展示的保理项目的状态 / 校验【月益升】列表展示的保理项目的状态失败!");
			}
			result = true;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			log.error(ScreenShotUtil
					.takeScreenshot(ThreadName, keyWords.driver));
		}
		// 验证整个测试用例是否成功
		assertTrue(result);
	}

	public String getBorrowerName() {
		return borrowerName;
	}

	public void setBorrowerName(String borrowerName) {
		this.borrowerName = borrowerName;
	}

}
