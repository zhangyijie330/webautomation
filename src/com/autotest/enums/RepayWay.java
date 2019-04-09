package com.autotest.enums;

/**
 * 还款方式
 * 
 * @author wb0002
 * 
 */
public enum RepayWay {

	/**
	 * 速兑通到期还本付息
	 */
	C,
	
	/**
	 * 到期还本付息
	 */
	E,

	/**
	 * 半年付息到期还本
	 */
	halfyear,

	/**
	 * 每月等额本息
	 */
	permonth,

	/**
	 * 按月等额本息
	 */
	PermonthFixN,

	/**
	 * 按月付息，到期还本
	 */
	D

}
