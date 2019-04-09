package com.autotest.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * 用户投资信息数据库操作
 * 
 * @author sunlingyun
 * 
 */
public class UserProductDao {
	private UserProductDao() {
	}

	/**
	 * 查询用户投资金额
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getInvestInfoByNumber(String userNumber,
			String productNumber) throws SQLException {
		return DbUtil.querySingleData(
				"select invest_amount from user_product where user_number="
						+ userNumber + " and product_number='" + productNumber
						+ "'", DbType.Local);
	}

	/**
	 * 查询所有用户列表
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getRegUserList()
			throws SQLException {
		return DbUtil.queryDataList(
				"select distinct user_number from user_product", DbType.Local);
	}

	/**
	 * 根据用户号查询关联关系
	 * 
	 * @param userNumber
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getUserInvestProductList(
			String userNumber) throws SQLException {
		return DbUtil.queryDataList(
				"select * from user_product where user_number='" + userNumber
						+ "'", DbType.Local);
	}

	/**
	 * 通过产品号查询用户列表
	 * 
	 * @param productNumber
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getUserListByProduct(
			String productNumber) throws SQLException {
		return DbUtil.queryDataList(
				"select distinct user_number from user_product where product_number='"
						+ productNumber + "'", DbType.Local);
	}
}
