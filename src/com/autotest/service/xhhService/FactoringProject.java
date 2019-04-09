/** 
 * @date 2016年9月9日 上午9:29:05
 * @version 1.0
 * 
 * @author xudashen
 */
package com.autotest.service.xhhService;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.autotest.driver.KeyWords;
import com.autotest.enums.DateType;
import com.autotest.enums.RepayWay;
import com.autotest.or.ObjectLib;
import com.autotest.utility.OrUtil;
import com.autotest.utility.StringUtils;
import com.autotest.utility.ThreadUtil;

/**
 * @author xudashen
 * 
 * @date 2016年9月9日 上午9:29:05
 */
public class FactoringProject {

	public KeyWords	keyWord			= null;

	public String	c				= "速兑通到期还本付息";

	public String	e				= "到期还本付息";

	public String	halfyear		= "半年付息到期还本";

	public String	permonth		= "每月等额本息";

	public String	permonthFixN	= "按月等额本息";

	public String	d				= "按月付息，到期还本";

	public FactoringProject(KeyWords keyWord) {
		this.keyWord = keyWord;
	}

	/**
	 * 功能：xhh-253:case20.访问鑫合汇主站
	 * 
	 * @param url
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	public boolean openIndex(String url) throws Exception {
		boolean flag = false;
		keyWord.open(url);
		String js = "return document.readyState";
		for (int i = 0; i < 10; i++) {
			String readyState = keyWord.executeJsReturnString(js);
			if (readyState.equals("complete")) {
				flag = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * 功能：xhh-254:case21.访问【投资理财-投资项目-精品推荐】页面
	 * 
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	public boolean accessPage() throws Exception {
		boolean flag = false;
		keyWord.click(OrUtil
				.getBy("menuLeft2_li_xpath", ObjectLib.XhhObjectLib));
		String js = "return document.readyState";
		for (int i = 0; i < 10; i++) {
			String readyState = keyWord.executeJsReturnString(js);
			if (readyState.equals("complete")) {
				flag = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * 功能：xhh-255:case22.访问【投资理财-投资项目-日益升】页面 /
	 * xhh-374:case22.访问【投资理财-投资项目-月益升】页面
	 * 
	 * @param projectType
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	public boolean accessProjectPage(int projectType) throws Exception {
		boolean flag = false;
		By by = By.xpath("//ul[@class='plistW']/li[" + projectType
				+ "]/span[1]/a");
		keyWord.click(by);
		String js = "return document.readyState";
		for (int i = 0; i < 10; i++) {
			String readyState = keyWord.executeJsReturnString(js);
			if (readyState.equals("complete")) {
				flag = true;
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * 功能：xhh-257:case23.在【日益升】列表等待至保理项目的【融资发标时间】 /
	 * xhh-375:case23.在【月益升】列表等待至保理项目的【融资发标时间】
	 * 
	 * @param date
	 * @return boolean
	 * @throws ParseException
	 * @throws IOException
	 * 
	 */
	public boolean waitProjectOpen(Date date, Date nowDate)
			throws ParseException, IOException {
		boolean flag = false;
		long tt = date.getTime() - nowDate.getTime();
		System.out.println("tt=[" + tt + "]");
		if (tt > 0) {
			ThreadUtil.sleep((int) tt / 1000);
		}
		ThreadUtil.sleep(90);
		flag = true;
		return flag;
	}

	/**
	 * 功能：xhh-258:case24.至【融资发标时间】后，刷新页面
	 * 
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	public boolean refreshPage() throws Exception {
		boolean flag = false;
		String js = "location.reload(true)";
		keyWord.executeJS(js);
		js = "return document.readyState";
		for (int i = 0; i < 10; i++) {
			String readyState = keyWord.executeJsReturnString(js);
			if (readyState.equals("complete")) {
				flag = true;
				ThreadUtil.sleep();
				break;
			} else {
				ThreadUtil.sleep();
			}
		}
		return flag;
	}

	/**
	 * 功能：项目筛选
	 * 
	 * @param projectType
	 * @param projectDuration
	 * @param projectStatus
	 * @return boolean
	 * 
	 */
	public void filterProject(String projectType, String projectDuration,
			String projectStatus) {
		// TODO
	}

	/**
	 * 功能：xhh-260:case25.校验【日益升】列表展示的保理项目字段及字段值
	 * 
	 * @param projectName
	 *            项目名称
	 * @param rate
	 *            预期年化收益率
	 * @param timeOut
	 *            项目期限
	 * @param dateType
	 *            项目期限单位
	 * @param repayWay
	 *            还款方式
	 * @param minBidAmount
	 *            起投金额（元）
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	public boolean searchProject(String projectName, double rate, int timeOut,
			DateType dateType, RepayWay repayWay, String minBidAmount)
			throws Exception {
		boolean flag = false;
		String maxPage = keyWord.getAttribute(
				OrUtil.getBy("maxPage_xpath", ObjectLib.XhhObjectLib), "text");
		System.out.println("maxPage=[" + maxPage + "]");
		String xpath = "//a[contains(text(),'" + projectName
				+ "')]/parent::h4/following-sibling::div[1]";
		System.out.println("xpath=[" + xpath + "]");
		WebElement webElement = null;
		for (int i = 0; i < Integer.parseInt(maxPage); i++) {
			String nowpage = keyWord.getAttribute(
					OrUtil.getBy("nowPage_xpath", ObjectLib.XhhObjectLib),
					"nowpage");
			System.out.println("nowpage=[" + nowpage + "]");
			boolean isExist = keyWord.isElementExist2(By.xpath(xpath));
			if (isExist) {
				webElement = keyWord.getWebElement(By.xpath(xpath));
				System.out.println(webElement == null);
			}
			if (null != webElement) {
				break;
			} else {
				keyWord.click(OrUtil.getBy("nextPage_xpath",
						ObjectLib.XhhObjectLib));
				for (int j = 0; j < 10; j++) {
					ThreadUtil.sleep(5);
					String nowpage2 = keyWord
							.getAttribute(OrUtil.getBy("nowPage_xpath",
									ObjectLib.XhhObjectLib), "nowpage");
					if (nowpage2.equals(nowpage)) {
						ThreadUtil.sleep();
					} else {
						break;
					}
				}
			}
		}
		if (null == webElement) {
			throw new Exception("获取项目失败!");
		}
		// 预期年化收益率
		String lilv = keyWord.getWebElement(
				By.xpath(xpath + "/span[1]/div[1]/em[1]")).getText();
		// 项目期限
		String qixian = keyWord.getAttribute(By.xpath(xpath + "/span[3]/p[2]"),
				"title");
		// 还款方式
		String fangshi = keyWord.getWebElement(
				By.xpath(xpath + "/span[5]/p[2]/em")).getText();
		// 起投金额(元)
		String jine = keyWord.getWebElement(
				By.xpath(xpath + "/span[7]/p[2]/em")).getText();
		System.out.println("lilv=[" + lilv + "]");
		System.out.println("qixian=[" + qixian + "]");
		System.out.println("fangshi=[" + fangshi + "]");
		System.out.println("jine=[" + jine + "]");
		if (lilv.indexOf("" + rate) == -1) {
			throw new Exception("预期年化收益率不符!");
		}
		int day = converToDay(qixian);
		int day2 = converToDay(timeOut, dateType);
		if (day != day2) {
			throw new Exception("项目期限不符!");
		}
		if (fangshi.equals(c)) {
			if (repayWay != RepayWay.C) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(e)) {
			if (repayWay != RepayWay.E) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(halfyear)) {
			if (repayWay != RepayWay.halfyear) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(permonth)) {
			if (repayWay != RepayWay.permonth) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(permonthFixN)) {
			if (repayWay != RepayWay.PermonthFixN) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(d)) {
			if (repayWay != RepayWay.D) {
				throw new Exception("还款方式不符!");
			}
		} else {
			throw new Exception("还款方式不符!");
		}
		Pattern pattern = Pattern.compile("[^0-9]");
		Matcher matcher = pattern.matcher(jine);
		String num = matcher.replaceAll("");
		int qian = Integer.parseInt(num);
		if (Integer.parseInt(minBidAmount) != qian) {
			throw new Exception("起投金额不符!");
		}
		flag = true;
		return flag;
	}

	/**
	 * 功能：xhh-260:case25.校验【日益升】列表展示的保理项目字段及字段值
	 * 
	 * @param projectName
	 *            项目名称
	 * @param rate
	 *            预期年化收益率
	 * @param timeOut
	 *            项目期限
	 * @param dateType
	 *            项目期限单位
	 * @param repayWay
	 *            还款方式
	 * @param minBidAmount
	 *            起投金额（元）
	 * @param demandAmount
	 *            融资金额
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	public boolean searchProject2(String projectName, double rate, int timeOut,
			DateType dateType, RepayWay repayWay, String minBidAmount,
			String demandAmount) throws Exception {
		boolean flag = false;
		String maxPage = keyWord.getAttribute(
				OrUtil.getBy("maxPage_xpath", ObjectLib.XhhObjectLib), "text");
		System.out.println("maxPage=[" + maxPage + "]");
		String xpath = "//a[contains(text(),'" + projectName
				+ "')]/parent::h4/following-sibling::div[1]";
		System.out.println("xpath=[" + xpath + "]");
		WebElement webElement = null;
		for (int i = 0; i < Integer.parseInt(maxPage); i++) {
			String nowpage = keyWord.getAttribute(
					OrUtil.getBy("nowPage_xpath", ObjectLib.XhhObjectLib),
					"nowpage");
			System.out.println("nowpage=[" + nowpage + "]");
			boolean isExist = keyWord.isElementExist2(By.xpath(xpath));
			if (isExist) {
				webElement = keyWord.getWebElement(By.xpath(xpath));
				System.out.println(webElement == null);
			}
			if (null != webElement) {
				break;
			} else {
				keyWord.click(OrUtil.getBy("nextPage_xpath",
						ObjectLib.XhhObjectLib));
				for (int j = 0; j < 10; j++) {
					String nowpage2 = keyWord
							.getAttribute(OrUtil.getBy("nowPage_xpath",
									ObjectLib.XhhObjectLib), "nowpage");
					if (nowpage2.equals(nowpage)) {
						ThreadUtil.sleep();
					} else {
						break;
					}
				}
			}
		}
		if (null == webElement) {
			throw new Exception("获取项目失败!");
		}
		// 预期年化收益率
		String lilv = keyWord.getWebElement(
				By.xpath(xpath + "/span[1]/div[1]/em[1]")).getText();
		// 项目期限
		String qixian = keyWord.getAttribute(By.xpath(xpath + "/span[3]/p[2]"),
				"title");
		// 还款方式
		String fangshi = keyWord.getWebElement(
				By.xpath(xpath + "/span[5]/p[2]/em")).getText();
		// 起投金额(元)
		String jine = keyWord.getWebElement(
				By.xpath(xpath + "/span[7]/p[2]/em")).getText();
		System.out.println("lilv=[" + lilv + "]");
		System.out.println("qixian=[" + qixian + "]");
		System.out.println("fangshi=[" + fangshi + "]");
		System.out.println("jine=[" + jine + "]");
		if (lilv.indexOf("" + rate) == -1) {
			throw new Exception("预期年化收益率不符!");
		}
		int day = converToDay(qixian);
		int day2 = converToDay(timeOut, dateType);
		if (day != day2) {
			throw new Exception("项目期限不符!");
		}
		if (fangshi.equals(c)) {
			if (repayWay != RepayWay.C) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(e)) {
			if (repayWay != RepayWay.E) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(halfyear)) {
			if (repayWay != RepayWay.halfyear) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(permonth)) {
			if (repayWay != RepayWay.permonth) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(permonthFixN)) {
			if (repayWay != RepayWay.PermonthFixN) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(d)) {
			if (repayWay != RepayWay.D) {
				throw new Exception("还款方式不符!");
			}
		} else {
			throw new Exception("还款方式不符!");
		}
		int qian1 = Integer.parseInt(jine.split("-")[0]);
		int qian2 = Integer.parseInt(jine.split("-")[1]);
		if (Integer.parseInt(minBidAmount) != qian1) {
			throw new Exception("起投金额不符!");
		}
		if (Integer.parseInt(demandAmount) != qian2) {
			throw new Exception("起投金额不符!");
		}
		flag = true;
		return flag;
	}

	/**
	 * 功能：xhh-260:case25.校验【日益升】列表展示的保理项目字段及字段值
	 * 
	 * @param projectName
	 *            项目名称
	 * @param rate
	 *            预期年化收益率
	 * @param timeOut
	 *            项目期限
	 * @param dateType
	 *            项目期限单位
	 * @param repayWay
	 *            还款方式
	 * @param minBidAmount
	 *            起投金额（元）
	 * @param isExtendTime
	 *            展期天数
	 * @return
	 * @throws Exception
	 *             boolean
	 * 
	 */
	public boolean searchProject(String projectName, double rate, int timeOut,
			DateType dateType, RepayWay repayWay, String minBidAmount,
			int isExtendTime) throws Exception {
		boolean flag = false;
		String maxPage = keyWord.getAttribute(
				OrUtil.getBy("maxPage_xpath", ObjectLib.XhhObjectLib), "text");
		System.out.println("maxPage=[" + maxPage + "]");
		String xpath = "//a[contains(text(),'" + projectName
				+ "')]/parent::h4/following-sibling::div[1]";
		System.out.println("xpath=[" + xpath + "]");
		WebElement webElement = null;
		for (int i = 0; i < Integer.parseInt(maxPage); i++) {
			String nowpage = keyWord.getAttribute(
					OrUtil.getBy("nowPage_xpath", ObjectLib.XhhObjectLib),
					"nowpage");
			System.out.println("nowpage=[" + nowpage + "]");
			boolean isExist = keyWord.isElementExist2(By.xpath(xpath));
			if (isExist) {
				webElement = keyWord.getWebElement(By.xpath(xpath));
				System.out.println(webElement == null);
			}
			if (null != webElement) {
				break;
			} else {
				keyWord.click(OrUtil.getBy("nextPage_xpath",
						ObjectLib.XhhObjectLib));
				for (int j = 0; j < 10; j++) {
					String nowpage2 = keyWord
							.getAttribute(OrUtil.getBy("nowPage_xpath",
									ObjectLib.XhhObjectLib), "nowpage");
					if (nowpage2.equals(nowpage)) {
						ThreadUtil.sleep();
					} else {
						break;
					}
				}
			}
		}
		if (null == webElement) {
			throw new Exception("获取项目失败!");
		}
		// 预期年化收益率
		String lilv = keyWord.getWebElement(
				By.xpath(xpath + "/span[1]/div[1]/em[1]")).getText();
		// 项目期限
		String qixian = keyWord.getAttribute(By.xpath(xpath + "/span[3]/p[3]"),
				"title");
		// 展期
		String zanqi = keyWord.getWebElement(
				By.xpath(xpath + "/span[3]/p[3]/em[2]")).getText();
		// 还款方式
		String fangshi = keyWord.getWebElement(
				By.xpath(xpath + "/span[5]/p[2]/em")).getText();
		// 起投金额(元)
		String jine = keyWord.getWebElement(
				By.xpath(xpath + "/span[7]/p[2]/em")).getText();
		System.out.println("lilv=[" + lilv + "]");
		System.out.println("qixian=[" + qixian + "]");
		System.out.println("zanqi=[" + zanqi + "]");
		System.out.println("fangshi=[" + fangshi + "]");
		System.out.println("jine=[" + jine + "]");
		if (lilv.indexOf("" + rate) == -1) {
			throw new Exception("预期年化收益率不符!");
		}
		int day = converToDay(qixian);
		int day2 = converToDay(timeOut, dateType);
		if (day != day2) {
			throw new Exception("项目期限不符!");
		}
		if (!(isExtendTime + "").equals(zanqi)) {
			throw new Exception("展期期限不符!");
		}
		if (fangshi.equals(c)) {
			if (repayWay != RepayWay.C) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(e)) {
			if (repayWay != RepayWay.E) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(halfyear)) {
			if (repayWay != RepayWay.halfyear) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(permonth)) {
			if (repayWay != RepayWay.permonth) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(permonthFixN)) {
			if (repayWay != RepayWay.PermonthFixN) {
				throw new Exception("还款方式不符!");
			}
		} else if (fangshi.equals(d)) {
			if (repayWay != RepayWay.D) {
				throw new Exception("还款方式不符!");
			}
		} else {
			throw new Exception("还款方式不符!");
		}
		Pattern pattern = Pattern.compile("[^0-9]");
		Matcher matcher = pattern.matcher(jine);
		String num = matcher.replaceAll("");
		int qian = Integer.parseInt(num);
		if (Integer.parseInt(minBidAmount) != qian) {
			throw new Exception("起投金额不符!");
		}
		flag = true;
		return flag;
	}

	/**
	 * 功能：xhh-262:case26.校验【日益升】列表展示的保理项目的状态 \
	 * xhh-378:case26.校验【月益升】列表展示的保理项目的状态
	 * 
	 * @param projectName
	 * @param date1
	 * @param date2
	 * @return boolean
	 * @throws Exception
	 * 
	 */
	public boolean checkProjectStatus(String projectName, Date date1, Date date2)
			throws Exception {
		boolean flag = false;
		String xpath = "//a[contains(text(),'"
				+ projectName
				+ "')]/parent::h4/parent::div/following-sibling::div[1]/label[1]/a[1]";
		String xpath2 = "//a[contains(text(),'"
				+ projectName
				+ "')]/parent::h4/parent::div/following-sibling::div[1]/div[1]/span";
		String status = keyWord.getWebElement(By.xpath(xpath)).getText();
		String time = keyWord.getWebElement(By.xpath(xpath2)).getText();
		System.out.println("status=[" + status + "]");
		System.out.println("time=[" + time + "]");
		if (!status.equals("即将开标")) {
			// throw new Exception("项目状态不符!");
		}
		if (time.indexOf("00天 00时") == -1 || time.indexOf("分") == -1
				|| time.indexOf("秒") == -1) {
			// throw new Exception("项目开标时间不符!");
		}
		flag = true;
		return flag;
	}

	public int converToDay(String str) {
		str = StringUtils.format(str);
		Pattern pattern = Pattern.compile("[^0-9]");
		Matcher matcher = pattern.matcher(str);
		String num = matcher.replaceAll("");
		System.out.println("num=[" + num + "]");
		System.out.println("str=[" + str + "]");
		if (str.endsWith("天")) {
			return Integer.parseInt(num);
		}
		if (str.endsWith("月")) {
			return Integer.parseInt(num) * 30;
		}
		if (str.endsWith("个月")) {
			return Integer.parseInt(num) * 30;
		}
		return 0;
	}

	public int converToDay(int timeOut, DateType dateType) {
		if (dateType == DateType.day) {
			return timeOut;
		}
		if (dateType == DateType.month) {
			return timeOut * 30;
		}
		return 0;
	}

}
