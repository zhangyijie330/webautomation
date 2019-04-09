package com.autotest.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.DateUtils;
import com.autotest.utility.DbUtil;

/**
 * 用户信息的数据库操作
 * 
 * @author sunlingyun
 * 
 */
public class UserDao {
	private UserDao() {
	}

	/**
	 * 获得数据库所有用户
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getAllUsers() throws SQLException {
		return DbUtil.queryDataList(
				"select id,mobile,login_pwd,pay_pwd from user", DbType.Local);
	}

	/**
	 * 获取注册的输入数据：手机号、密码、支付密码
	 * 
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getRegistData() throws SQLException {
		return DbUtil.queryDataList(
				"select mobile,login_pwd,pay_pwd from user", DbType.Local);
	}

	/**
	 * 根据用户id查询用户信息
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getUserById(String uid)
			throws SQLException {
		return DbUtil.querySingleData("select * from user where id='" + uid
				+ "' and platform='" + BaseConfigUtil.getHomePageURL() + "'",
				DbType.Local);
	}

	/**
	 * 新注册用户的信息插入user表
	 * 
	 * @param busi_uid
	 * @param mobile
	 * @param login_pwd
	 * @param pay_pwd
	 * @throws SQLException
	 */
	public static void insertUser(String busi_uid, String mobile,
			String login_pwd, String pay_pwd) throws SQLException {
		String sql = "INSERT INTO user(busi_uid,mobile,login_pwd,pay_pwd,date,platform) VALUES ('"
				+ busi_uid
				+ "','"
				+ mobile
				+ "','"
				+ login_pwd
				+ "','"
				+ pay_pwd
				+ "','"
				+ DateUtils.getNowDateStr()
				+ "','"
				+ BaseConfigUtil.getHomePageURL() + "')";
		DbUtil.insert(sql, DbType.Local);
	}

	/**
	 * 根据用户id查询用户uid
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static String getUidById(String id) throws SQLException {
		Map<String, String> map = DbUtil.querySingleData(
				"select * from user where id='" + id + "'", DbType.Local);
		return map.get("busi_uid");
	}

	/**
	 * 根据手机号查询用户id
	 * 
	 * @param mobile
	 * @return
	 * @throws SQLException
	 */
	public static String getIdByMobile(String mobile) throws SQLException {
		Map<String, String> map = DbUtil.querySingleData(
				"select * from user where mobile=" + mobile, DbType.Local);
		return map.get("id");
	}

	/**
	 * 查询业务数据库的user表记录
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> queryBusUser(String sql)
			throws SQLException {
		return DbUtil.querySingleData(sql, DbType.Business);
	}
}
