package com.autotest.dao;

import java.sql.SQLException;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * 数据库product_temp表操作
 * 
 * @author sunlingyun
 * 
 */
public class ProductTempDao {

	/**
	 * 根据指定的产品顺序号查询相应产品编号
	 * 
	 * @param productNo
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getProductCodeByNumber(String productNo)
			throws SQLException {
		return DbUtil.querySingleData(
				"select product_name product_code from product_temp where product_id='"
						+ productNo + "'", DbType.Local);
	}

	/**
	 * 根据指定的编号号查询相应顺序号
	 * 
	 * @param productNo
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getProductNumberByCode(String productCode)
			throws SQLException {
		return DbUtil.querySingleData(
				"select product_id product_name from product_temp where product_name='"
						+ productCode + "'", DbType.Local);
	}
}
