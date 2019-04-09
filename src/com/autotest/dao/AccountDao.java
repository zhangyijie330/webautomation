package com.autotest.dao;

import java.sql.SQLException;
import java.util.Map;

import com.autotest.enums.DbType;
import com.autotest.enums.DiscountType;
import com.autotest.utility.DbUtil;

/**
 * 账号信息的数据库操作
 * 
 * @author sunlingyun
 * 
 */
public class AccountDao {
	private AccountDao() {
	}

	/**
	 * 更新账号
	 * 
	 * @param acctBalance
	 * @param invest
	 * @param couponsNotUse
	 * @param couponsUsed
	 * @param bonusNotUse
	 * @param bonusUsed
	 * @param uid
	 * @throws SQLException
	 */
	public static void updateAccount(String acctBalance, String invest,
			String couponsNotUse, String couponsUsed, String bonusNotUse,
			String bonusUsed, String uid) throws SQLException {
		DbUtil.update("update user_acct set balance='" + acctBalance
				+ "' , total_invest='" + invest + "', coupons_not_use='"
				+ couponsNotUse + "',coupons_used='" + couponsUsed
				+ "',bonus_not_use='" + bonusNotUse + "',bonus_used='"
				+ bonusUsed + "' where uid =" + uid, DbType.Local);
	}

	/**
	 * 更新优惠券信息
	 * 
	 * @param rewardAmt
	 * @param type
	 * @throws Exception
	 */
	public static void updateReward(String rewardAmt, DiscountType type,
			String uid) throws Exception {
		switch (type) {
		case bonus:
			DbUtil.update("update user_acct set bonus_not_use='" + rewardAmt
					+ "' where uid =" + uid, DbType.Local);
			break;
		case coupons:
			DbUtil.update("update user_acct set coupons_not_use='" + rewardAmt
					+ "' where uid =" + uid, DbType.Local);
			break;
		case none:
			throw new Exception("不接受的优惠券类型");
		default:
			DbUtil.update("update user_acct set bonus_not_use='" + rewardAmt
					+ "' where uid =" + uid, DbType.Local);
			break;
		}

	}

	/**
	 * 投资后更新收益
	 * 
	 * @param uid
	 * @param totalProfit
	 * @param willProfit
	 * @throws SQLException
	 */
	public static void updateAcctProfit(String uid, String totalProfit,
			String willProfit) throws SQLException {
		DbUtil.update("update user_acct set total_profit='" + totalProfit
				+ "' , will_profit='" + willProfit + "' where uid =" + uid,
				DbType.Local);
	}

	/**
	 * 变现被购买后账户信息变化
	 * 
	 * @param acctBalance
	 * @param totalcashloan
	 * @param uid
	 * @throws SQLException
	 */
	public static void updateSDTAccount(String acctBalance,
			String totalcashloan, String uid) throws SQLException {
		DbUtil.update("update user_acct set balance='" + acctBalance
				+ "' , cash_loan='" + totalcashloan + "' where uid =" + uid,
				DbType.Local);
	}

	/**
	 * 扣除手续费账户信息变更
	 * 
	 * @param acctBalance
	 * @param uid
	 * @throws SQLException
	 */
	public static void updateFeeAccount(String acctBalance, String uid)
			throws SQLException {
		DbUtil.update("update user_acct set balance='" + acctBalance
				+ "' where uid =" + uid, DbType.Local);
	}

	/**
	 * 通过uid获得账号信息
	 * 
	 * @param uid
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> getUserAcctByUid(String uid)
			throws SQLException {
		return DbUtil.querySingleData("select * from user_acct where uid="
				+ uid, DbType.Local);
	}

	/**
	 * 插入新注册用户的账户初始值
	 * 
	 * @param uid
	 * @param asset
	 * @param total_invest
	 * @throws SQLException
	 */
	public static void insertAccount(String uid, String asset,
			String total_invest) throws SQLException {
		String sql = "INSERT INTO user_acct(uid,asset,balance,total_invest,cash_loan,total_profit,will_profit,exist_profit,coupons_not_use,coupons_used,coupons_expire,bonus_not_use,bonus_used,bonus_expire,rate_coupons_not_use,rate_coupons_used,rate_coupons_expire,invite_partner) VALUES ('"
				+ uid
				+ "','"
				+ asset
				+ "','"
				+ total_invest
				+ "','0.00','0.00','0.00','0.00','0.00','0.00','0.00','0.00','0.00','0.00','0.00','0','0','0','0')";
		DbUtil.insert(sql, DbType.Local);
	}

	/**
	 * 速兑通项目还款结束后 账户概括回写
	 * 
	 * @param uid
	 * @param balanceDb
	 * @param exist_profitDb
	 * @param total_profitDb
	 * @throws SQLException
	 */
	public static void cashAccount(String uid, String balanceDb,
			String exist_profitDb, String total_profitDb) throws SQLException {
		DbUtil.update(
				"update user_acct set total_invest='0.00',cash_loan='0.00',will_profit='0.00',balance='"
						+ balanceDb
						+ "',exist_profit='"
						+ exist_profitDb
						+ "',total_profit='"
						+ total_profitDb
						+ "',asset='"
						+ balanceDb + "' where uid =" + uid, DbType.Local);
	}

	/**
	 * 速兑通项目还款结束后理财记录的值回写
	 * 
	 * @param code
	 * @param uid
	 * @param profit
	 * @throws SQLException
	 */
	public static void updateSdtRepayInvest(String code, String uid)
			throws SQLException {
		DbUtil.update("update invest_record set status='已还款结束'where code = '"
				+ code + "'and uid = '" + uid + "'", DbType.Local);

	}

}
