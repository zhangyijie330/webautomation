package com.autotest.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * 数据库user_temp表操作
 * 
 * @author sunlingyun
 * 
 */
public class UserTempDao {

	/**
	 * 根据产品的顺序号查询需要参与投资的用户uid与投资金额invest_amount
	 * 
	 * @param productNo
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getInvestUserByProNo(
			String productNo) throws SQLException {
		return DbUtil
				.queryDataList(
						"select user_temp.uid,user_product.invest_amount from user_temp left join user_product on user_product.user_number=user_temp.user_number where product_number='"
								+ productNo + "'", DbType.Local);
	}

	/**
	 * 获得映射的用户id
	 * 
	 * @param uNo
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getUid(String uNo) throws SQLException {
		return DbUtil.querySingleData(
				"select user_temp.uid from user_temp where user_number='" + uNo
						+ "'", DbType.Local);
	}

	/**
	 * 更新映射表对应关系
	 * 
	 * @throws SQLException
	 */
	public static void updateUid(String uid, String uNo) throws SQLException {
		String sql = "UPDATE user_temp ut SET ut.uid = '" + uid
				+ "' WHERE ut.user_number = '" + uNo + "'";
		DbUtil.update(sql, DbType.Local);
	}

	/**
	 * 获得映射的用户user_number
	 * 
	 * @param uNo
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getUserNumber(String uid)
			throws SQLException {
		return DbUtil.querySingleData(
				"select user_temp.user_number from user_temp where uid='" + uid
						+ "'", DbType.Local);
	}
}
