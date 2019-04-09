package com.autotest.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * cash_project_related变现项目关联表数据库操作
 * 
 * @author wb0001
 * 
 */
public class CashProjectRelatedDao {
	private CashProjectRelatedDao() {

	}

	/**
	 * 获得相关项目关联的，指定原始项目的变现关联关系列表
	 * 
	 * @param relatedProject
	 * @param originProject
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getUserFastCashList(
			String relatedProject, String originProject) throws SQLException {
		return DbUtil.queryDataList(
				"select * from cash_project_related where related_project='"
						+ relatedProject + "' and origin_project='"
						+ originProject + "'", DbType.Local);
	}

	/**
	 * 获得指定项目关联的变现信息
	 * 
	 * @param relatedProject
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getFastCashList(
			String relatedProject) throws SQLException {
		return DbUtil.queryDataList(
				"select * from cash_project_related where related_project='"
						+ relatedProject + "'", DbType.Local);
	}

	/**
	 * 获得指定变现人的对应项目的变现关联信息
	 * 
	 * @param userNumber
	 * @param relatedProject
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getFashCashByUser(String userNumber,
			String relatedProject) throws SQLException {
		return DbUtil.querySingleData(
				"select * from cash_project_related where related_project='"
						+ relatedProject + "' and fast_cash_user_number='"
						+ userNumber + "'", DbType.Local);
	}
}
