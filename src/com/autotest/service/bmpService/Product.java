package com.autotest.service.bmpService;

import java.sql.SQLException;
import java.util.Map;

import com.autotest.dao.ProductDao;
import com.autotest.enums.ProductType;
import com.autotest.utility.DateUtils;
import com.autotest.utility.StringUtils;

/**
 * 
 * @author wb0002
 * 
 */
public class Product {

	/**
	 * 生产产品名称 格式：产品类型(拼音缩写) + 日期(yyyy-MM-dd) + 3位数
	 * 
	 * @return
	 */
	public static String getProductName(ProductType productType) {
		StringBuffer productName = new StringBuffer();
		String date = DateUtils.formatDate("yyyyMMdd");
		Map<String, String> map = null;
		String type = null;
		switch (productType) {
			case RYS:
				type = "RYS";
				break;
			case YYS:
				type = "YYS";
				break;
			default:
				break;
		}
		try {
			map = ProductDao.queryProductNameByDate(date, type);
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		if (null != map) {
			String product_Name = map.get("productname");
			if (StringUtils.isEmpty(product_Name)) {// 如果当天没有对应的产品名称，则直接新建
				productName.append(type);
				productName.append(date);
				productName.append("001");
			} else {// 产品名称+1
				String index = product_Name.substring(
						product_Name.length() - 3, product_Name.length());
				int ii = Integer.parseInt(index) + 1;
				if (ii > 999) {
					try {
						throw new Exception("项目名称数字已达上限!" + ii);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				String newIndex = null;
				if (ii < 10) {
					newIndex = "00" + ii;
				} else if (ii < 100) {
					newIndex = "0" + ii;
				}
				productName.append(type);
				productName.append(date);
				productName.append(newIndex);
			}
		} else {
			productName.append(type);
			productName.append(date);
			productName.append("001");
		}
		// insert into
		try {
			ProductDao
					.insertIntoProductName(date, productName.toString(), type);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return productName.toString();
	}

}
