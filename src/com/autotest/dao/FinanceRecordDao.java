package com.autotest.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.enums.FinanceType;
import com.autotest.utility.DbUtil;

/**
 * 资金记录的数据库操作
 * 
 * @author sunlingyun
 * 
 */
public class FinanceRecordDao {
	private FinanceRecordDao() {
	}

	/**
	 * 插入资金记录
	 * 
	 * @param uid
	 * @param code
	 * @param financeType
	 * @param amount
	 * @param acctBalance
	 * @param abstracts
	 * @throws SQLException
	 */
	public static void insertRecord(String uid, String code,
			String financeType, String amount, String acctBalance,
			String abstracts) throws SQLException {
		DbUtil.insert(
				"insert into finance_record"
						+ "(uid,code,finance_type,amount,acct_balance,abstract) values "
						+ "(" + uid + ",'" + code + "','" + financeType + "','"
						+ amount + "','" + acctBalance + "','" + abstracts
						+ "')", DbType.Local);
	}

	/**
	 * 获得用户指定产品的投资记录
	 * 
	 * @param uid
	 * @param productCode
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getRecordByUserCode(String uid,
			String productCode, FinanceType type) throws SQLException {
		String typeStr = "";
		switch (type) {
		case invest:
			typeStr = "投资";
			break;
		case receive:
			typeStr = "回款";
			break;
		case fee:
			typeStr = "手续费";
			break;
		case reward:
			typeStr = "奖励";
			break;
		case realization:
			typeStr = "变现";
			break;
		case recharge:
			typeStr = "充值";
			break;
		case withdraw:
			typeStr = "提现";
			break;
		default:
			typeStr = "投资";
			break;
		}

		return DbUtil.querySingleData("select * from finance_record where uid="
				+ uid + " and code='" + productCode + "' and finance_type='"
				+ typeStr + "'", DbType.Local);
	}

	/**
	 * 获得数量
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static int getCountByUser(String uid) throws SQLException {
		return DbUtil.getCount("select count(*) from finance_record where uid="
				+ uid, DbType.Local);
	}

	/**
	 * 查询多条资金记录
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, String>> selectFinanceRecord(String pro_name,
			String finance_type) throws SQLException {
		return DbUtil.queryDataList(
				"select *from fund_record where pro_name = " + pro_name
						+ "' and finance_type='" + finance_type + "'",
				DbType.Local);
	}
}
