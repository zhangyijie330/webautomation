package com.autotest.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.utility.DbUtil;

/**
 * 产品表操作
 * 
 * @author sunlingyun
 * 
 */
public class ProductDao {
	private ProductDao() {
	}

	/**
	 * 功能：根据产品名称到业务数据库查询产品记录
	 * 
	 * @param projectName
	 * @return int
	 * @throws SQLException
	 * 
	 */
	public static int queryProjectByProject(String projectName)
			throws SQLException {
		String sql = "select * from fi_prj t where t.prj_name='" + projectName
				+ "'";
		System.out.println("sql=[" + sql + "]");
		Map<String, String> map = DbUtil.querySingleData(sql, DbType.Business);
		if (null != map && map.size() > 0) {
			return map.size();
		} else {
			return 0;
		}
	}

	/**
	 * 更新产品金额相关数据
	 * 
	 * @param prodBalance
	 * @param progress
	 * @param code
	 * @throws SQLException
	 */
	public static void updateAmount(String prodBalance, String progress,
			String collect, String code) throws SQLException {
		DbUtil.update("update productinfo set balance='" + prodBalance
				+ "' , progress='" + progress + "',collect='" + collect
				+ "' where prj_name='" + code + "'", DbType.Local);
	}

	/**
	 * 通过产品编号获得产品信息
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getProByCode(String code)
			throws SQLException {
		return DbUtil.querySingleData(
				"select * from productinfo where prj_name='" + code + "'",
				DbType.Local);
	}

	/**
	 * 通过速兑通产品名称获得项目信息
	 * 
	 * @param prj_name
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getCashProByCode(String prj_name)
			throws SQLException {
		return DbUtil.querySingleData(
				"select * from productinfo where prj_name='" + prj_name + "'",
				DbType.Local);
	}

	/**
	 * 根据项目ID查询产品类型模板里面是否有该类型
	 * 
	 * @param product_id
	 * @return
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> getCashFastId(String product_id)
			throws SQLException {
		return DbUtil.queryDataList(
				"select * from product_temp where product_id='" + product_id
						+ "'", DbType.Local);
	}

	/**
	 * 查询离当前的最近的一条产品名称
	 * 
	 * @param date
	 * @param productType
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> queryProductNameByDate(String date,
			String productType) throws SQLException {
		return DbUtil.querySingleData(
				"select productName from productname where productType='"
						+ productType + "' and date='" + date
						+ "' order by productName desc limit 1", DbType.Local);
	}

	/**
	 * 插入产品名称
	 * 
	 * @param date
	 * @param productName
	 * @param productType
	 * @throws SQLException
	 */
	public static void insertIntoProductName(String date, String productName,
			String productType) throws SQLException {
		DbUtil.insert(
				"insert into productname (date,productName,productType) values ('"
						+ date + "','" + productName + "','" + productType
						+ "')", DbType.Local);
	}

	/**
	 * 插入速兑通项目
	 * 
	 * @param cashProName
	 * @param repayType
	 * @param cashAsset
	 * @param cashMoney
	 * @param surplusAsset
	 * @throws SQLException
	 */
	public static void insertIntoCashProductInfo(String cashProName,
			String term, String repayType, String repay_date, String cashMoney,
			String prj_type, String year_rate, String progress, String balance,
			String collect) throws SQLException {

		DbUtil.insert(
				"insert into productinfo(repay_date,term,prj_name,repay_way,demand_amount,prj_type,year_rate,progress,balance,collect) values('"
						+ repay_date
						+ "','"
						+ term
						+ "','"
						+ cashProName
						+ "','"
						+ repayType
						+ "','"
						+ cashMoney
						+ "','"
						+ prj_type
						+ "','"
						+ year_rate
						+ "','"
						+ progress
						+ "','" + cashMoney + "','" + collect + "')",
				DbType.Local);
	}

	/**
	 * 更新产品募集金额
	 * 
	 * @param productcode
	 * @param collect
	 * @throws SQLException
	 */
	public static void updateCollect(String productcode, String collect)
			throws SQLException {
		DbUtil.update("update productinfo set collect='" + collect
				+ "' where prj_name ='" + productcode + "'", DbType.Local);
	}

	/**
	 * 更新产品利息相关数据
	 * 
	 * @param prodBalance
	 * @param progress
	 * @param code
	 * @throws SQLException
	 */
	public static void updateProfit(String profit, String total_money,
			String code) throws SQLException {
		DbUtil.update("update productinfo set profit='" + profit
				+ "' , total_money='" + total_money + "' where prj_name='"
				+ code + "'", DbType.Local);
	}

	/**
	 * 插入product_temp表
	 * 
	 * @param prodctid
	 * @param product_name
	 * @throws SQLException
	 */
	public static void insertCashFast(String product_id, String product_name)
			throws SQLException {
		DbUtil.insert(
				"insert into product_temp (product_id,product_name,product_no) values('"
						+ product_id + "','" + product_name + "','"
						+ product_name + "')", DbType.Local);

	}

	/**
	 * 更新product_temp表
	 * 
	 * @param product_id
	 * @param product_name
	 * @throws SQLException
	 */
	public static void updateCashFast(String product_id, String product_name)
			throws SQLException {
		DbUtil.update("update product_temp set product_name='" + product_name
				+ "', product_no='" + product_name + "'where product_id = '"
				+ product_id + "'", DbType.Local);
	}

	/**
	 * 根据user_number查找UID
	 * 
	 * @param user_number
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> selectCashUser(String user_number)
			throws SQLException {

		return DbUtil.querySingleData(
				"select *from user_temp where user_number = '"

				+ user_number + "'", DbType.Local);

	}

	/**
	 * 通过user_number查找变现信息
	 * 
	 * @param user_number
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> selectCashFastUser(String user_number)
			throws SQLException {

		return DbUtil.querySingleData(
				"select *from user_cash_fast where user_number = '"

				+ user_number + "'", DbType.Local);

	}

	/**
	 * 根据项目编号invest_product_id查询用户的变现信息
	 * 
	 * @param user_number
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> selectCashFastPrj(String invest_product_id)
			throws SQLException {

		return DbUtil.querySingleData(
				"select *from user_cash_fast where invest_product_id = '"

				+ invest_product_id + "'", DbType.Local);

	}

	/**
	 * 通过UID获取用户信息
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getCashFastUserInfo(String uid)
			throws SQLException {
		return DbUtil.querySingleData("select * from user where id = '" + uid
				+ "'", DbType.Local);

	}

	/**
	 * 通过user_number获取用户信息
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getUserInfo(String user_number)
			throws SQLException {
		return DbUtil.querySingleData(
				"select * from user_temp where user_number = '" + user_number
						+ "'", DbType.Local);

	}

	/**
	 * 插入速兑通购买记录，表名sdt_record
	 * 
	 * @param uid
	 * @param count
	 * @param sdt_name
	 * @param count_invest_money
	 * @param count_plant_fee
	 * @throws SQLException
	 */
	public static void insertSdtRecord(String uid, String count,
			String sdt_name, String count_invest_money, String count_plant_fee)
			throws SQLException {
		DbUtil.insert(
				"insert into sdt_record (uid,count,sdt_name,count_invest_money,count_plant_fee) values('"
						+ uid
						+ "','"
						+ count
						+ "','"
						+ sdt_name
						+ "','"
						+ count_invest_money + "'," + count_plant_fee + ")",
				DbType.Local);
	}

	/**
	 * 速兑通记录查询
	 * 
	 * @param sdt_name
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> selectSdtRecord(String sdt_name)
			throws SQLException {
		return DbUtil.querySingleData(
				"select *from sdt_record where sdt_name = '" + sdt_name + "'",
				DbType.Local);
	}

	/**
	 * 速兑通资金记录查询多条变现类型返回结果
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> selectFinanceRecord(String code,
			String finance_type) throws SQLException {
		return DbUtil
				.queryDataList(
						"select abstract,finance_type,amount,acct_balance from finance_record where code = '"
								+ code
								+ "' and finance_type='"
								+ finance_type
								+ "'", DbType.Local);
	}

	/**
	 * 速兑通资金记录里面手续费查询
	 * 
	 * @throws SQLException
	 */
	public static Map<String, String> selectFeeRecord(String code,
			String finance_type) throws SQLException {
		return DbUtil
				.querySingleData(
						"select abstract,finance_type,amount,acct_balance from finance_record where code = '"
								+ code
								+ "' and finance_type='"
								+ finance_type
								+ "'", DbType.Local);

	}

	/**
	 * 根据UID获取资金记录
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> uidSelectFinanceRecord(String uid)
			throws SQLException {
		return DbUtil.querySingleData(
				"select *from finance_record where uid = '" + uid + "'",
				DbType.Local);
	}

	/**
	 * 根据product_id查找项目名称
	 * 
	 * @param invest_product_id
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> productIdSelectProductInfo(
			String invest_product_id) throws SQLException {
		return DbUtil.querySingleData(
				"select *from product_temp where product_id = '"
						+ invest_product_id + "'", DbType.Local);

	}

	/**
	 * 根据productid从产品表、loan_temp、loan_apply中查询所需校验数据
	 * 
	 * @param productid
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> loanApply(String productid)
			throws SQLException {
		return DbUtil
				.querySingleData(
						"select DISTINCT pro_temp.product_name,pro.borrower_name,pro.demand_amount,pro.collect,pro.year_rate,pro.term,pro.platform_fee,loant.management_fee,loana.bonus_proportion,loana.bond_proportion,loana.bond,loana.confirmation_upload,loana.confirming_order,loana.pro_info,loana.raise_notice,loana.UBSP_notice,loana.completion_notice,pro.start_bid_time,pro.end_bid_time from product_temp as pro_temp join productinfo as pro on pro_temp.product_name=pro.prj_name join loan_temp as loant on pro_temp.product_id=loant.product_id join loan_apply as loana on pro_temp.product_id=loana.pro_id where pro_temp.product_id='"
								+ productid + "'", DbType.Local);
	}

	/**
	 * 更新invest_record里的收益值
	 * 
	 * @param code
	 * @param uid
	 * @param profit
	 * @throws SQLException
	 */
	public static void updateSdtProfit(String code, String uid, String profit)
			throws SQLException {
		DbUtil.update("update invest_record set profit='" + profit
				+ "',status='待还款'where code = '" + code + "'and uid = '" + uid
				+ "'", DbType.Local);

	}

	/**
	 * 获取理财记录里面的收益和
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> countProfit(String code)
			throws SQLException {
		return DbUtil.queryDataList(
				"select * from invest_record where code = '" + code + "'",
				DbType.Local);
	}

	/**
	 * 获取理财记录里面的收益和:根据uid
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> countProfitUid(String uid)
			throws SQLException {
		return DbUtil.queryDataList("select * from invest_record where uid = '"
				+ uid + "'", DbType.Local);
	}

	/**
	 * 获取理财记录里面的收益和:根据uid,与code
	 * 
	 * @param code
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> countProfitUidAndCode(String uid,
			String code) throws SQLException {
		return DbUtil.querySingleData(
				"select * from invest_record where uid = '" + uid + "'"
						+ " and code = '" + code + "'", DbType.Local);
	}

	/**
	 * 根据用户ID和项目名称获取理财记录
	 * 
	 * @param code
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getInvestMoney(String code, String uid)
			throws SQLException {
		return DbUtil.querySingleData(
				"select *from invest_record where code = '" + code
						+ "'and uid = '" + uid + "'", DbType.Local);

	}

	/**
	 * 更新账户概括的值
	 * 
	 * @param cashLoanDb
	 * @param will_profitDb
	 * @param exist_profitDb
	 * @param total_profitDb
	 * @throws SQLException
	 */

	public static void updateSdtUserAcct(String uid, String assetDb,
			String cashLoanDb, String will_profitDb, String exist_profitDb,
			String total_profitDb) throws SQLException {
		DbUtil.update("update user_acct set asset = '" + assetDb
				+ "',cash_loan = '" + cashLoanDb + "',total_profit = '"
				+ total_profitDb + "',will_profit = '" + will_profitDb
				+ "',exist_profit = '" + exist_profitDb + "'" + "where uid='"
				+ uid + "'", DbType.Local);

	}

	/**
	 * 更新账户概括的值
	 * 
	 * @param cashLoanDb
	 * @param will_profitDb
	 * @param exist_profitDb
	 * @param total_profitDb
	 * @throws SQLException
	 */
	public static void updateInvSdtAcct(String uid, String will_profitDb,
			String total_profitDb) throws SQLException {
		DbUtil.update("update user_acct set total_profit = '" + total_profitDb
				+ "',will_profit = '" + will_profitDb + "'" + "where uid='"
				+ uid + "'", DbType.Local);

	}

	/**
	 * 获取投资YYS001和SDT001的用户：u14,u15,u16
	 * 
	 * @param productNumber
	 *            SDT001
	 * @param investProduct_id
	 *            YYS001
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> oriSdtcUid(String productNumber,
			String investProduct_id) throws SQLException {
		return DbUtil
				.queryDataList(
						"select * from user_product where product_number = '"
								+ productNumber
								+ "' and user_number in (select user_number from user_product where product_number = '"
								+ productNumber + "')", DbType.Local);
	}
}
