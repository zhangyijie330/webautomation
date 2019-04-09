
package com.autotest.service.xhhService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.autotest.driver.KeyWords;
import com.autotest.or.ObjectLib;
import com.autotest.utility.DateUtils;
import com.autotest.utility.SSHUtil;
import com.autotest.utility.OrUtil;
import com.autotest.utility.StringUtils;

public class ProjectInfo {
	public String PrjName;
	private Logger log = null;
	KeyWords kw=null;
	public ProjectInfo(Logger log, KeyWords kw){
		this.kw = kw;
		this.log = log;
	}
/**
 * 进入投资理财页面
 * @param kw
 * @throws Exception
 */
	public void FinanceInvest(KeyWords kw) throws Exception{
		
		kw.click(OrUtil.getBy("finance_invest_linkText", ObjectLib.XhhObjectLib));
	}
/**
 * 进入日益升页面
 * @param kw
 * @throws Exception
 */
	public void FinanceRiYiS(KeyWords kw) throws Exception{
		
		kw.click(OrUtil.getBy("finance_riyi_linkText", ObjectLib.XhhObjectLib));
	}
/**
 * 进入月益升页面
 * @param kw
 * @throws Exception
 */
    public void FinanceYueYiS(KeyWords kw) throws Exception{
		
		kw.click(OrUtil.getBy("finance_yueyi_linkText", ObjectLib.XhhObjectLib));
	}

/**
 * 校验项目信息
 * @param kw
 * @param PrjName  项目名称
 * @param driver  
 * @param rate  项目年化收益率
 * @param time  项目期限
 * @param repay 项目还款方式
 * @param mixMoney 项目起投金额
 * @return 
 * @throws Exception
 */
	public boolean ViewProjectInfo(KeyWords kw,String PrjName,Map<String,String> expectedMap) throws Exception{
		
		//获取项目年化收益率
		String rate = kw.getWebElement(By.xpath("//a[text()='" + PrjName + "']/parent::h4/following-sibling::div/span/div/em")).getText();
		log.info("获取项目年化收益率" +rate);	
		boolean flag = false;
		//获取项目还款方式
		String repay = kw.getWebElement(By.xpath("//a[text()='" + PrjName + "']/parent::h4/following-sibling::div/span[5]/p[2]/em")).getText();
		log.info("获取项目还款方式" + repay);
		//获取项目起投金额
		String mixMoney = kw.getWebElement(By.xpath("//a[text()='" + PrjName + "']/parent::h4/following-sibling::div/span[7]/p[2]/em")).getText();
		log.info("获取项目起投金额" + mixMoney);
		//获取项目状态
		String proStatus = kw.getText(By.xpath("//a[text()='" + PrjName + "']/parent::h4/parent::div/following-sibling::div/label/a"));
		log.info("获取项目状态" + proStatus);
		//获取无项目期限
		String time = this.getProTime(kw);
		
		//项目信息校验
		//HashMap<String, String> srcMap = new HashMap<String, String>();
		HashMap<String, String> destMap = new HashMap<String, String>();
		//读数据库项目信息值
		//srcMap = DbUtil.querySingleData("select *from prj where prjname = "+ PrjName +"", dbType.Local);
		//将页面的值保存
		destMap.put("rate", rate);
		destMap.put("limittime", time);
		destMap.put("repay", repay);
		destMap.put("mixMoney", mixMoney);
		destMap.put("status", proStatus);
		//页面值与数据库值进行比较
		flag = StringUtils.isEquals(expectedMap, destMap, log);
		return flag;
		
	}
	
	public String getProTime(KeyWords kw) throws Exception{
		String PrjName = "月益升-经营贷YYS20160718003";
		String time = null;
		List<WebElement> list = kw.getWebElements(By.xpath("//a[text()='" + PrjName + "']/parent::h4/following-sibling::div/span[3]/p/em"));
		log.info("项目是否展期" + list.size());
		if (list.size() == 1) {
			 time = kw.getAttribute(By.xpath("//a[text()='" + PrjName + "']/parent::h4/following-sibling::div/span[3]/p[3]"), "title");
			 log.info("项目期限" + time);
		}else {
			String limittime = kw.getAttribute(By.xpath("//a[text()='" + PrjName + "']/parent::h4/following-sibling::div/span[3]/p[3]"), "title");
			log.info("项目期限" + limittime);
			String Zqtime = kw.getWebElement(By.xpath("//a[text()='" + PrjName + "']/parent::h4/following-sibling::div/span[3]/p[3]/em[2]")).getText();
			log.info("项目展期期限" + Zqtime);
			time = limittime + "+" + Zqtime;
			log.info("项目期限 = 项目期限+展期期限" + time);
		}
        return time;
	}
	
/**
 * 获取页面上倒计时时间    
 * @param kw
 * @return
 * @throws Exception
 */
    public String getProductPageTime(KeyWords kw) throws Exception{
		String pageTime = kw.getText(OrUtil.getBy("finance_pageTime_xpath", ObjectLib.XhhObjectLib));
		return pageTime;
	}
    
/**
 * 验证倒计时是否正确
 * @param kw
 * @param investEndTime
 * @param accpectDiffSec
 * @return
 * @throws Exception
 */
    public Boolean checkInvestStartTime(KeyWords kw, String investEndTime, int accpectDiffSec) throws Exception{
		boolean flag = false;
		//获得服务器当前时间
		String currentDate = SSHUtil.sshCurrentTime(log);
		//获得界面上融资剩余时间
		String investRemainTime = getProductPageTime(kw);
		investRemainTime = StringUtils.format(investRemainTime);
		//获得服务器时间与融资截止时间差
		String format = "HH时mm分ss秒";
		String dfferTime = DateUtils.differTime2String(currentDate, investEndTime,format);
		//比较程序计算的时间与界面上获取的时间差，如果在accpectDiffSec时间内，为可接受误差
		flag = DateUtils.isDifferAccept(dfferTime, investRemainTime, accpectDiffSec,format);
		return flag;
	}
}

