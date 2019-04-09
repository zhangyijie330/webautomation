/** 
 * @date 2016年9月14日 下午3:47:59
 * @version 1.0
 * 
 * @author wb0002
 */
package com.autotest.utility;

import com.autotest.driver.KeyWords;

/**
 * @author wb0002
 * 
 * @date 2016年9月14日 下午3:47:59
 */
public class UserAgentUtil {

	public static final String	UserAgent_JS	= "return navigator.userAgent;";

	/**
	 * 功能：获取浏览器类型
	 * 
	 * @param keyWord
	 * @return
	 * @throws Exception
	 *             String
	 * 
	 */
	public static String getUserAgent(KeyWords keyWord) throws Exception {
		String userAgent = keyWord.executeJsReturnString(UserAgent_JS);
		if (userAgent.indexOf("MSIE") >= 0) {
			return "ie";
		}
		// firefox
		else if (userAgent.indexOf("Firefox") >= 0) {
			return "Firefox";
		}
		// Chrome
		else if (userAgent.indexOf("Chrome") >= 0) {
			return "Chrome";
		}
		// Opera
		else if (userAgent.indexOf("Opera") >= 0) {
			return "Opera";
		}
		// Safari
		else if (userAgent.indexOf("Safari") >= 0) {
			return "Safari";
		}
		// Netscape
		else if (userAgent.indexOf("Netscape") >= 0) {
			return "Netscape";
		} else {
			return null;
		}
	}
}
