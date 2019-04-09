package com.autotest.dao;

import java.sql.SQLException;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * 操作业务数据库
 * 
 * @author 001811
 * 
 */
public class BusinessDao {
	private BusinessDao() {
	}

	/**
	 * 取注册动态码
	 * 
	 * @author 001811
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getMobileCode(String mobile)
			throws SQLException {
		String sql = "select mc.code from fi_mobile_validate_code mc where mc.type = 'REGISTER' and mc.mobile = '"
				+ mobile + "' and mc.status=1 ORDER BY mc.sms_id DESC LIMIT 1";
		return DbUtil.querySingleData(sql, DbType.Business);
	}

	/**
	 * 充值-修改fi_user_account标的amount字段值
	 * 
	 * @author 001811
	 * @throws SQLException
	 */
	public static void updateUserAmount(String mobile, String amount)
			throws SQLException {
		String sql = "UPDATE fi_user_account ua SET ua.amount = '"
				+ amount
				+ "' WHERE ua.uid = (SELECT u.uid FROM fi_user u WHERE u.mobile = '"
				+ mobile + "')";
		DbUtil.update(sql, DbType.Business);
	}

	/**
	 * 注册后获取用户的uid
	 * 
	 * @param mobile
	 * @return
	 * @throws SQLException
	 */
	public static String getUid(String mobile) throws SQLException {
		String sql = "SELECT u.uid FROM fi_user u WHERE u.mobile = '" + mobile
				+ "'";
		Map<String, String> map = DbUtil.querySingleData(sql, DbType.Business);
		return map.get("uid");
	}

	/**
	 * 功能：根据U17的手机号码去业务数据库查找对应的Uid，uname，realName
	 * 
	 * @author xudashen
	 * @param mobile
	 * @return Map<String,String>
	 * @date 2016年9月8日 上午9:57:03
	 */
	public static Map<String, String> getUserByMobile(String mobile) {
		Map<String, String> map = null;
		String sql = "select uid,uname,real_name,mobile from fi_user where mobile='"
				+ mobile + "'";
		try {
			map = DbUtil.querySingleData(sql, DbType.Business);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
}
