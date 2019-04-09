package com.autotest.utility;

/**
 * Thread工具类
 * @author XUjj
 *
 */
public class ThreadUtil {
	
	/**
	 * 暂停1秒
	 */
	public static void sleep(){
		try {
			Thread.sleep(1000);
			System.out.println( "Wait 1 second.");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 暂停s秒
	 * @param s
	 */
	public static void sleep(int s){
		try {
			System.out.println( "Wait " + s + " seconds.");
			Thread.sleep(s * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
		
}
