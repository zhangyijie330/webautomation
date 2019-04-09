/** 
 * @date 2016年9月9日 下午2:12:54
 * @version 1.0
 * 
 * @author xudashen
 */
package com.autotest.dao;

import java.sql.SQLException;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * @author xudashen
 * 
 * @date 2016年9月9日 下午2:12:54
 */
public class ProjectReleaseDao {

	/**
	 * 功能：读取项目相关配置
	 * 
	 * @param case_id
	 * @return Map<String,String>
	 * @throws SQLException
	 * 
	 */
	public static Map<String, String> readProjectRelease(String case_id)
			throws SQLException {
		String sql = "select * from project_release where case_id='" + case_id
				+ "'";
		return DbUtil.querySingleData(sql, DbType.Local);
	}

}
