package com.autotest.dao;

import java.sql.SQLException;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * 保理公司资金记录的数据库操作
 * 
 * @author wb0005
 * 
 */
public class FundRecordDao {
	private FundRecordDao() {

	}

	/**
	 * 插入资 金记录
	 * 
	 * @param proname
	 * @param type
	 * @param amount
	 * @param abstracts
	 * @throws SQLException
	 */

	public static void insertRecord(String proname, String type, String amount,
			String abstracts) throws SQLException {
		DbUtil.insert("insert into fund_record"
				+ "(pro_name,type,amout,abstract) values " + "('" + proname
				+ "','" + type + "','" + amount + "','" + abstracts + "')",
				DbType.Local);
	}

}
