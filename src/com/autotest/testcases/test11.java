package com.autotest.testcases;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import com.autotest.driver.KeyWords;
import com.autotest.driver.TestDriver;
import com.autotest.service.bmpService.Loan;
import com.autotest.service.xhhService.PubXhhService;
import com.autotest.service.xhhService.RepayPlanService;
import com.autotest.utility.ThreadUtil;

public class test11 extends TestDriver {

	public static KeyWords kw;
	public static boolean result = false;

	@Test
	public void test() {
		PubXhhService publicService = new PubXhhService();
		RepayPlanService repayService = new RepayPlanService(log);
		Loan loan = new Loan(log);
		List<Map<String, String>> Elementstext = new ArrayList<Map<String, String>>();
		List<WebElement> webElements1 = new ArrayList<WebElement>();
		List<WebElement> webElements2 = new ArrayList<WebElement>();
		List<WebElement> webElements3 = new ArrayList<WebElement>();
		List<String> Text1 = new ArrayList<String>();
		List<String> Text2 = new ArrayList<String>();
		List<String> Text3 = new ArrayList<String>();
		kw = new KeyWords(driver, log);
		String user = "maoyl1";
		String pwd = "m123456";
		String productCode = "ZYJ20160817001";
		List<String> expectlist = new ArrayList<String>();

		try {
			if (publicService.login(kw, user, pwd)) {
				log.info(user + "登录成功");
				// kw.click(By.xpath("//a[contains(text(),'审核未通过')]"));
				ThreadUtil.sleep(3);

				kw.click(By
						.xpath("//a[contains(@href,'/Application/ProjectManage/getMyMoneyList')]"));
				kw.waitPageLoad();
				ThreadUtil.sleep(3);

				if (kw.isElementExist(By.id("getMyRecordList_type"))) {
					result = true;
					log.info("【进入资金记录页面成功！】");
				} else {
					log.error("【进入资金记录页面失败！】");
				}

				loan.setdate(kw, repayService);

				// kw.click(By.xpath("//div[@id='search']/button"));
				// kw.click(By.xpath("//span[contains(text(),'最近一周')]"));
				// kw.click(By.xpath("//div[@id='search']/label/input"));
				// repayService.setDate(kw, searchdate, searchdate);
				// kw.click(By.xpath("//div[@id='search']/label/input"));
				// ThreadUtil.sleep(3);

				String maxpage = kw
						.getText(By
								.xpath("//a[contains(text(),'>>')]/preceding-sibling::a[2]"));
				// HashMap<String, String> destMap = new HashMap<String,
				// String>();

				while (true) {

					boolean flag = false;
					String currentpage = kw.getText(By
							.xpath("//span[contains(@class,'current')]"));

					flag = kw.isElementExist(By.xpath("//td[contains(text(),'"
							+ productCode + "')]"));

					if (flag == true) {
						/*
						 * for (int i = 0; i < webElements.size(); i++) {
						 * WebElement webelements = webElements.get(i);
						 * Elementstext.add(webelements.getText()); }
						 */
						webElements1 = kw
								.getWebElements(By
										.xpath("//td[contains(text(),'ZYJ20160817001')]"));
						webElements2 = kw
								.getWebElements(By
										.xpath("//td[contains(text(),'ZYJ20160817001')]/preceding-sibling::td[2]"));
						webElements3 = kw
								.getWebElements(By
										.xpath("//td[contains(text(),'ZYJ20160817001')]/preceding-sibling::td[3]"));
						/*
						 * for (int i = 0; i < webElements.size(); i++) {
						 * WebElement webelements = webElements.get(i);
						 * Text.add(webelements.getText()); }
						 */
						/*
						 * for (int i = 0; i < webElements.size(); i++) {
						 * Map<String, String> map = new HashMap<String,
						 * String>(); map.put("finance_type", kw.getText(By
						 * .xpath(
						 * "//td[contains(text(),'ZYJ20160817001')]/preceding-sibling::td[3]"
						 * ))); map.put("amount", kw.getText(By .xpath(
						 * "//td[contains(text(),'ZYJ20160817001')]/preceding-sibling::td[2]"
						 * ))); map.put("abstract", kw.getText(By
						 * .xpath("//td[contains(text(),'ZYJ20160817001')]")));
						 * Elementstext.add(map); }
						 */
						ThreadUtil.sleep(2);
					}

					/*
					 * if (kw.isElementExist(By .xpath(
					 * "//td[contains(text(),'ZYJ20160809004')]/preceding-sibling::td[contains(text(),'项目支付')]"
					 * ))) { destMap.put( "类型", kw.getText(By .xpath(
					 * "//td[contains(text(),'ZYJ20160809004')]/preceding-sibling::td[contains(text(),'项目支付')]"
					 * ))); destMap.put( "交易金额", kw.getText(By .xpath(
					 * "//td[contains(text(),'ZYJ20160809004')]/preceding-sibling::td[contains(text(),'项目支付')]/following-sibling::td[1]"
					 * ))); destMap.put( "摘要", kw.getText(By .xpath(
					 * "//td[contains(text(),'ZYJ20160809004')]/preceding-sibling::td[contains(text(),'项目支付')]/following-sibling::td[3]"
					 * ))); }
					 */

					if (!kw.isElementExist(By.xpath("//input[@value='跳转']"))) {
						break;
					} else {
						if (currentpage.equals(maxpage)) {
							break;
						} else {
							kw.click(By
									.xpath("//a[contains(text(),'>>')]/preceding-sibling::a[1]"));
						}
					}

				}
				for (int i = 0; i < webElements1.size(); i++) {
					WebElement webelements = webElements1.get(i);
					Text1.add(webelements.getText());
				}
				for (int i = 0; i < webElements2.size(); i++) {
					WebElement webelements = webElements2.get(i);
					Text2.add(webelements.getText());
				}
				for (int i = 0; i < webElements3.size(); i++) {
					WebElement webelements = webElements3.get(i);
					Text3.add(webelements.getText());
				}
				for (int i = 0; i < Text1.size(); i++) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("finance_type", Text3.get(i));
					map.put("amount", Text2.get(i));
					map.put("abstract", Text1.get(i));
					Elementstext.add(map);
				}
				/*
				 * expectlist.add("项目支付-10,000.00项目“ZYJ20160817001”支付");
				 * expectlist.add("项目融资+10,000.00项目“ZYJ20160817001”融资"); for
				 * (int i = 0; i < Elementstext.size(); i++) {
				 * System.out.println(Elementstext.get(i));
				 * System.out.println("===================================="); }
				 * for (int i = 0; i < expectlist.size(); i++) {
				 * System.out.println(StringUtils.format(expectlist.get(i)));
				 * System.out.println("===================================="); }
				 */

				System.out.println(Elementstext);
				// for (int i = 0; i < Elementstext.size(); i++) {
				// if (Elementstext.contains(expectlist)) {
				// result = true;
				// }
				// }

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}