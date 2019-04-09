package com.autotest.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * 理财记录的数据库操作
 * 
 * @author sunlingyun
 * 
 */
public class InvestRecordDao {
	private InvestRecordDao() {
	}

	/**
	 * 插入理财记录
	 * 
	 * @param uid
	 * @param code
	 * @param profit
	 * @param repayDate
	 * @param investAmount
	 * @param stauts
	 * @param productName
	 * @throws SQLException
	 */
	public static void insertRecord(String uid, String code, String profit,
			String repayDate, String investAmount, String status,
			String investDate) throws SQLException {
		DbUtil.insert(
				"insert into invest_record"
						+ "(uid,code,profit,repay_date,invest_amount,status,invest_date) values "
						+ "(" + uid + ",'" + code + "','" + profit + "','"
						+ repayDate + "','" + investAmount + "','" + status
						+ "','" + investDate + "')", DbType.Local);
	}

	/**
	 * 获得用户指定产品的理财记录
	 * 
	 * @param uid
	 * @param productCode
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getRecordByUserCode(String uid,
			String productCode) throws SQLException {
		return DbUtil.querySingleData("select * from invest_record where uid="
				+ uid + " and code='" + productCode + "'", DbType.Local);
	}

	/**
	 * 获得用户所有产品的理财记录
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getAllRecordByUserCode(String uid)
			throws SQLException {
		return DbUtil.queryDataList("select * from invest_record where uid='"
				+ uid + "'", DbType.Local);
	}

	/**
	 * 获得特定产品的所有理财记录
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getAllRecordByCode(String code)
			throws SQLException {
		return DbUtil.queryDataList("select * from invest_record where code='"
				+ code + "'", DbType.Local);
	}

	/**
	 * 获得数量
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static int getCountByUser(String uid) throws SQLException {
		return DbUtil.getCount("select count(*) from invest_record where uid="
				+ uid, DbType.Local);
	}

	/**
	 * 获得项目投资数量
	 * 
	 * @param productCode
	 * @return
	 * @throws SQLException
	 */
	public static int getCountByCode(String code) throws SQLException {
		return DbUtil.getCount(
				"select count(*) from invest_record where code='" + code + "'",
				DbType.Local);
	}

	/**
	 * 获得项目投资总额
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static double getAmountByCode(String code) throws SQLException {
		List<Map<String, String>> tmpList = DbUtil.queryDataList(
				"select * from invest_record where code='" + code + "'",
				DbType.Local);
		double sum = 0.00;
		for (int i = 0; i < tmpList.size(); i++) {
			sum = sum + Double.parseDouble(tmpList.get(i).get("invest_amount"));
		}
		return sum;

	}

	/**
	 * 更新用户指定产品的利息
	 * @author Minhui
	 * @param uid
	 * @param productCode
	 * @return
	 * @throws SQLException
	 */
	public static void updateProfit(String uid, String productCode,
			String profit) throws SQLException {
		DbUtil.update("update invest_record set profit='" + profit
				+ "'  where uid=" + uid + " and code='" + productCode + "'",
				DbType.Local);
	}

	/**
	 * 更新所有用户指定产品的还款日期
	 * 
	 * @param uid
	 * @param productCode
	 * @param repayDate
	 * @throws SQLException
	 */
	public static void updateRepayDate(String productCode, String repayDate)
			throws SQLException {
		DbUtil.update("update invest_record set repay_date='" + repayDate
				+ "' where code='" + productCode + "'", DbType.Local);
	}
}
