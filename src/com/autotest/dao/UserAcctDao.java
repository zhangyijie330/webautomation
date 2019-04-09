package com.autotest.dao;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * 数据库user_acct表操作
 * 
 * @author wb004
 * 
 */
public class UserAcctDao {

	/**
	 * 更新用户预期总收益和代收收益
	 * 
	 * @throws SQLException
	 */
	public static void updateProfit(String uid, String profit)
			throws SQLException {
		Map<String, String> tmp = DbUtil
				.querySingleData("select * from user_acct where uid='" + uid
						+ "'", DbType.Local);
		DecimalFormat fm = new DecimalFormat("####0.00");
		String total_profit = tmp.get("total_profit");
		total_profit = fm.format(Double.parseDouble(total_profit)
				+ Double.parseDouble(profit));
		String will_profit = tmp.get("will_profit");
		will_profit = fm.format(Double.parseDouble(will_profit)
				+ Double.parseDouble(profit));
		String sql = "UPDATE user_acct SET total_profit = '" + total_profit
				+ "', will_profit ='" + will_profit + "' WHERE uid = '" + uid
				+ "'";
		DbUtil.update(sql, DbType.Local);
	}

	/**
	 * 更新用户的净资产、我的投资、已赚收益
	 * 
	 * @throws SQLException
	 */
	public static void updateAsset(String uid, String coupons)
			throws SQLException {
		Map<String, String> tmp = DbUtil
				.querySingleData("select * from user_acct where uid='" + uid
						+ "'", DbType.Local);
		DecimalFormat fm = new DecimalFormat("####0.00");
		String asset = tmp.get("asset");
		asset = fm.format(Double.parseDouble(asset)
				+ Double.parseDouble(coupons));
		String total_invest = tmp.get("total_invest");
		total_invest = fm.format(Double.parseDouble(total_invest)
				+ Double.parseDouble(coupons));
		String exist_profit = tmp.get("exist_profit");
		exist_profit = fm.format(Double.parseDouble(exist_profit)
				+ Double.parseDouble(coupons));
		String sql = "UPDATE user_acct SET asset = '" + asset
				+ "', total_invest ='" + total_invest + "', exist_profit ='"
				+ exist_profit + "' WHERE uid = '" + uid + "'";
		DbUtil.update(sql, DbType.Local);
	}
}
