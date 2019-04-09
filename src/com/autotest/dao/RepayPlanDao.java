package com.autotest.dao;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * 还款计划数据库操作
 * 
 * @author sunlingyun
 * 
 */
public class RepayPlanDao {
	private RepayPlanDao() {
	}

	/**
	 * 根据项目编号获得产品计划列表,按期数升序排列
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getPlanByCode(String code)
			throws SQLException {
		return DbUtil.queryDataList(
				"select * from product_repay_plan where name='" + code
						+ "'  and stage!='募集期' order by id", DbType.Local);
	}

	/**
	 * 获得项目期数数量
	 * 
	 * @author Minhui
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static int getStageCountByCode(String code) throws SQLException {
		return DbUtil.getCount(
				"select count(*) from product_repay_plan where code='" + code
						+ "'", DbType.Local);
	}

	/**
	 * 根据产品编号和期数获取信息
	 * 
	 * @author Minhui
	 * @param code
	 * @param stage
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getRepayPlanByCodeStage(String code,
			int stage) throws SQLException {
		return DbUtil.querySingleData(
				"select * from product_repay_plan where name='" + code
						+ "' and stage='" + stage + "'", DbType.Local);
	}

	/**
	 * 插入项目还款计划
	 * 
	 * @author Minhui
	 * @throws SQLException
	 * 
	 */
	public static void insertIntoProductPayPlan(String name, String code,
			String stage, String capital, String profit, String total_money,
			String remain_capital, String repay_date, String status)
			throws SQLException {
		DbUtil.insert(
				"insert into product_repay_plan"
						+ "(name,code,stage,capital,profit,total_money,remain_capital,repay_date,status) values "
						+ "('" + name + "','" + code + "','" + stage + "','"
						+ capital + "','" + profit + "','" + total_money
						+ "','" + remain_capital + "','" + repay_date + "','"
						+ status + "')", DbType.Local);
	}

	/**
	 * 插入 用户还款计划
	 * 
	 * @author Minhui
	 * @throws SQLException
	 * 
	 */
	public static void insertIntoUserPayPlan(String uid, String bus_uid,
			String name, String code, String stage, String invest_amount,
			String capital, String profit, String total_money,
			String remain_capital, String repay_date, String status)
			throws SQLException {
		DbUtil.insert(
				"insert into user_repay_plan"
						+ "(uid,bus_uid,name,code,stage,invest_amount,capital,profit,total_money,remain_capital,repay_date,status) values "
						+ "("
						+ uid
						+ ",'"
						+ bus_uid
						+ "','"
						+ name
						+ "','"
						+ code
						+ "','"
						+ stage
						+ "','"
						+ invest_amount
						+ "','"
						+ capital
						+ "','"
						+ profit
						+ "','"
						+ total_money
						+ "','"
						+ remain_capital
						+ "','"
						+ repay_date
						+ "','"
						+ status + "')", DbType.Local);
	}

	/**
	 * 获得项目投资总收益
	 * 
	 * @author Minhui
	 * @param code
	 * @param stage
	 * @return
	 * @throws SQLException
	 */
	public static double getProfitByCode(String code) throws SQLException {
		List<Map<String, String>> tmp = DbUtil.queryDataList(
				"select * from user_repay_plan where name='" + code + "'",
				DbType.Local);
		double sum = 0.00;
		for (int i = 0; i < tmp.size(); i++) {
			sum = sum + Double.parseDouble(tmp.get(i).get("profit"));
		}
		return sum;
	}

	/**
	 * 获得项目投资总本息
	 * 
	 * @author Minhui
	 * @param code
	 * @param stage
	 * @return sum
	 * @throws SQLException
	 */
	public static double getTotalByCode(String code) throws SQLException {
		List<Map<String, String>> tmp = DbUtil.queryDataList(
				"select * from user_repay_plan where name='" + code + "'",
				DbType.Local);
		double sum = 0.00;
		for (int i = 0; i < tmp.size(); i++) {
			sum = sum + Double.parseDouble(tmp.get(i).get("total_money"));
		}
		return sum;
	}

	/**
	 * 获得项目投资某一期总本金
	 * 
	 * @author Minhui
	 * @param code
	 * @param stage
	 * @return
	 * @throws SQLException
	 */
	public static double getCapitalByCode(String code, String stage)
			throws SQLException {
		List<Map<String, String>> tmp = DbUtil.queryDataList(
				"select * from user_repay_plan where name='" + code
						+ "' and stage='" + stage + "'", DbType.Local);
		double sum = 0.00;
		for (int i = 0; i < tmp.size(); i++) {
			sum = sum + Double.parseDouble(tmp.get(i).get("capital"));
		}
		return sum;
	}

	/**
	 * 获得用户某一项目总收益
	 * 
	 * @author Minhui
	 * @param code
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static double getProfitByCodeandUid(String code, String uid)
			throws SQLException {
		List<Map<String, String>> tmp = DbUtil.queryDataList(
				"select * from user_repay_plan where name='" + code
						+ "' and uid='" + uid + "'", DbType.Local);
		double sum = 0.00;
		for (int i = 0; i < tmp.size(); i++) {
			sum = sum + Double.parseDouble(tmp.get(i).get("profit"));
		}
		return sum;
	}

	/**
	 * 获得项目投资某一期总收益
	 * 
	 * @author Minhui
	 * @param code
	 * @param stage
	 * @return
	 * @throws SQLException
	 */
	public static double getProfitByCode(String code, String stage)
			throws SQLException {
		List<Map<String, String>> tmp = DbUtil.queryDataList(
				"select * from user_repay_plan where name='" + code
						+ "' and stage='" + stage + "'", DbType.Local);
		double sum = 0.00;
		for (int i = 0; i < tmp.size(); i++) {
			sum = sum + Double.parseDouble(tmp.get(i).get("profit"));
		}
		return sum;
	}

	/**
	 * 获得项目投资某一期总剩余金额
	 * 
	 * @author Minhui
	 * @param code
	 * @param stage
	 * @return
	 * @throws SQLException
	 */
	public static double getRemainByCode(String code, String stage)
			throws SQLException {
		List<Map<String, String>> tmp = DbUtil.queryDataList(
				"select * from user_repay_plan where name='" + code
						+ "' and stage='" + stage + "'", DbType.Local);
		double sum = 0.00;
		for (int i = 0; i < tmp.size(); i++) {
			sum = sum + Double.parseDouble(tmp.get(i).get("remain_capital"));
		}
		return sum;
	}

	/**
	 * 更新产品利息相关数据
	 * 
	 * @author Minhui
	 * @param profit
	 * @param total_money
	 * @param code
	 * @throws SQLException
	 */
	public static void updateProfit(String profit, String total_money,
			String code, String stage) throws SQLException {
		DbUtil.update("update product_repay_plan set profit='" + profit
				+ "' , total_money='" + total_money + "', stage='第" + stage
				+ "期' where name='" + code + "' and stage='" + stage + "'",
				DbType.Local);
	}

	/**
	 * 更新产品利息相关数据
	 * 
	 * @author Minhui
	 * @param profit
	 * @param total_money
	 * @param code
	 * @throws SQLException
	 */
	public static void updateInfo(String capital, String profit,
			String total_money, String remain_capital, String code,
			String stage, String repay_date) throws SQLException {
		if (stage.equals("募集期")) {
			DbUtil.update("update product_repay_plan set capital='" + capital
					+ "', profit='" + profit + "' , total_money='"
					+ total_money + "', remain_capital='" + remain_capital
					+ "', repay_date='" + repay_date + "' where name='" + code
					+ "' and stage='" + stage + "'", DbType.Local);
		} else {
			DbUtil.update("update product_repay_plan set capital='" + capital
					+ "', profit='" + profit + "' , total_money='"
					+ total_money + "', remain_capital='" + remain_capital
					+ "',stage='第" + stage + "期' where name='" + code
					+ "' and stage='" + stage + "'", DbType.Local);
		}
	}

	/**
	 * 更新用户利息相关数据
	 * 
	 * @author Minhui
	 * @param uid
	 * @param profit
	 * @param total_money
	 * @param code
	 * @param stage
	 * @throws SQLException
	 */
	public static void updateUserProfit(String uid, String profit,
			String total_money, String code, String stage) throws SQLException {
		DbUtil.update("update user_repay_plan set profit='" + profit
				+ "' , total_money='" + total_money + "', stage='第" + stage
				+ "期' where name='" + code + "' and stage='" + stage
				+ "' and uid='" + uid + "'", DbType.Local);
	}

	/**
	 * 获得用户所有产品的还款计划
	 * 
	 * @author Minhui
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getAllRecordByUserCode(String uid)
			throws SQLException {
		return DbUtil.queryDataList("select * from user_repay_plan where uid='"
				+ uid + "'", DbType.Local);
	}

	/**
	 * 获得某产品的所有还款计划
	 * 
	 * @author Minhui
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getAllRecordByCode(String code)
			throws SQLException {
		return DbUtil.queryDataList(
				"select * from product_repay_plan where name='" + code + "'",
				DbType.Local);
	}

	/**
	 * 更新产品还款计划的还款日期
	 * 
	 * @author Minhui
	 * @param proName
	 * @param repayDate
	 * @throws SQLException
	 */
	public static void updateProRepayDate(String proName, String repayDate)
			throws SQLException {
		DbUtil.update("update product_repay_plan set repay_date='" + repayDate
				+ "' where name='" + proName + "'", DbType.Local);
	}

	/**
	 * 更新用户还款计划的还款日期
	 * 
	 * @author Minhui
	 * @param proName
	 * @param repayDate
	 * @throws SQLException
	 */
	public static void updateUserRepayDate(String proName, String repayDate)
			throws SQLException {
		DbUtil.update("update user_repay_plan set repay_date='" + repayDate
				+ "' where name='" + proName + "'", DbType.Local);
	}

	/**
	 * 获取product_repay_plan中指定项目非募集期的利息总和
	 * 
	 * @param productCode
	 * @param stage
	 * @return
	 * @throws SQLException
	 */
	public static String sumprofit(String productCode) throws SQLException {
		List<Map<String, String>> lst = DbUtil.queryDataList(
				"select profit from product_repay_plan where name='"
						+ productCode + "' and stage not like'募集期'",
				DbType.Local);
		double sum = 0.00;
		for (int i = 0; i < lst.size(); i++) {
			sum = sum + Double.parseDouble(lst.get(i).get("profit"));
		}
		DecimalFormat fm = new DecimalFormat("#.00");
		String fsum = fm.format(sum);
		return fsum;
	}

	/**
	 * 获取product_repay_plan指定项目的募集期利息
	 * 
	 * @param productCode
	 * @return
	 * @throws SQLException
	 */
	public static String recruitment(String productCode) throws SQLException {
		Map<String, String> mm = DbUtil.querySingleData(
				"select profit from product_repay_plan where name='"
						+ productCode + "' and stage='募集期'", DbType.Local);
		String ss = mm.get("profit");
		DecimalFormat fm = new DecimalFormat("#.##");
		String prof = fm.format(Double.parseDouble(ss));
		return prof;
	}

}
