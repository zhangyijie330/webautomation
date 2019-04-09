package com.autotest.utility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.autotest.enums.DbType;

/**
 * 数据库工具类
 * 
 * @author Tom/Mark
 */
public class DbUtil {

	public static Connection conn;

	public static Logger logger = null;

	public DbUtil(Logger logger) {
		DbUtil.logger = logger;
	}

	/**
	 * 数据库连接
	 * 
	 * @return
	 */
	public static Connection getConn(DbType DbType) {

		String driver = null;
		String url = null;
		String user = null;
		String pwd = null;

		switch (DbType) {
		case Local:
			driver = BaseConfigUtil.getDBConfig("localSqlDriver");
			url = BaseConfigUtil.getDBConfig("localSqlUrl");
			user = BaseConfigUtil.getDBConfig("localSqlUser");
			pwd = BaseConfigUtil.getDBConfig("localSqlPassword");
			break;
		case Business:
			driver = BaseConfigUtil.getDBConfig("businessSqlDriver");
			url = BaseConfigUtil.getDBConfig("businessSqlUrl");
			user = BaseConfigUtil.getDBConfig("businessSqlUser");
			pwd = BaseConfigUtil.getDBConfig("businessSqlPassword");
			break;
		default:
			driver = BaseConfigUtil.getDBConfig("localSqlDriver");
			url = BaseConfigUtil.getDBConfig("localSqlUrl");
			user = BaseConfigUtil.getDBConfig("localSqlUser");
			pwd = BaseConfigUtil.getDBConfig("localSqlPassword");
			break;
		}

		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, user, pwd);
			System.out.println("MariaDB Connected Successfully!");
		} catch (SQLException e) {
			System.err.println("SQL Exception:" + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.err.println("Driver Class not found " + e.getMessage());
		}
		return conn;
	}

	/**
	 * 查询单条
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static Map<String, String> querySingleData(String sql, DbType DbType)
			throws SQLException {
		Map<String, String> map = new HashMap<String, String>();
		Connection conn = getConn(DbType);
		ResultSet rs = null;
		try {
			PreparedStatement pstmt = (PreparedStatement) conn
					.prepareStatement(sql);
			rs = pstmt.executeQuery();
			int col = rs.getMetaData().getColumnCount();
			if (rs.next()) {
				for (int i = 1; i <= col; i++) {
					map.put(rs.getMetaData().getColumnLabel(i).toLowerCase(),
							rs.getString(i));
				}
			}
			System.out.println("Query with SQL - [" + sql + "] Successfully!");
			logger.info("Query with SQL - [" + sql + "] Successfully!");
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
			if (conn != null) {
				conn.close();
			}
			System.out.println("MariaDB Closed Successfully!");
		}
		return map;
	}

	/**
	 * 查询多条
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static List<Map<String, String>> queryDataList(String sql,
			DbType DbType) throws SQLException {
		List<Map<String, String>> lst = new ArrayList<Map<String, String>>();
		Connection conn = getConn(DbType);
		PreparedStatement pstmt;
		ResultSet rs = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			int col = rs.getMetaData().getColumnCount();
			while (rs.next()) {
				HashMap<String, String> map = new HashMap<String, String>();
				for (int i = 1; i <= col; i++) {
					map.put(rs.getMetaData().getColumnLabel(i).toLowerCase(),
							rs.getString(i));
				}
				lst.add(map);
			}
			System.out.println("Query with SQL - [" + sql + "] Successfully!");
			logger.info("Query with SQL - [" + sql + "] Successfully!");
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (conn != null) {
				conn.close();
			}
			System.out.println("MariaDB Closed Successfully!");
		}
		return lst;
	}

	/**
	 * 插入
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static int insert(String sql, DbType DbType) throws SQLException {
		Connection conn = getConn(DbType);
		int i = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			i = pstmt.executeUpdate();
			System.out.println("Insert Result:" + i);
			logger.info("Insert Result:" + i);
			if (i != 0) {
				System.out.println("Insert with SQL - [" + sql
						+ "] Successfully!");
				logger.info("Insert with SQL - [" + sql + "] Successfully!");
			} else {
				System.out.println("Insert with SQL - [" + sql + "] Failed!");
				logger.error("Insert with SQL - [" + sql + "] Failed!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
			System.out.println("MariaDB Closed Successfully!");
		}
		return i;
	}

	/**
	 * 更新
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static int update(String sql, DbType DbType) throws SQLException {
		Connection conn = getConn(DbType);
		int i = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			i = pstmt.executeUpdate();
			System.out.println("Update Result:" + i);
			logger.info("Update Result:" + i);
			if (i != 0) {
				System.out.println("Update with SQL - [" + sql
						+ "] Successfully!");
				logger.info("Update with SQL - [" + sql + "] Successfully!");
			} else {
				System.out.println("Update with SQL - [" + sql + "] Failed!");
				logger.error("Update with SQL - [" + sql + "] Failed!");
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
			System.out.println("MariaDB Closed Successfully!");
		}
		return i;
	}

	/**
	 * 删除
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public static int delete(String sql, DbType DbType) throws SQLException {
		Connection conn = getConn(DbType);
		int i = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			i = pstmt.executeUpdate();
			System.out.println("Delete Result:" + i);
			logger.info("Delete Result:" + i);
			if (i != 0) {
				System.out.println("Delete with SQL - [" + sql
						+ "] Successfully!");
				logger.info("Delete with SQL - [" + sql + "] Successfully!");
			} else {
				System.out.println("Delete with SQL - [" + sql + "] Failed!");
				logger.error("Delete with SQL - [" + sql + "] Failed!");
			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
			System.out.println("MariaDB Closed Successfully!");
		}
		return i;
	}

	/**
	 * 获得数量
	 * 
	 * @param sql
	 * @param DbType
	 * @return
	 * @throws SQLException
	 */
	public static int getCount(String sql, DbType DbType) throws SQLException {
		Connection conn = getConn(DbType);
		int rowCount = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				rowCount = rs.getInt(1);
			}
			System.out.println("Get Result count:" + rowCount);
			logger.info("Get Result count:" + rowCount);
		} catch (SQLException e) {
			throw e;
		} finally {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
			System.out.println("MariaDB Closed Successfully!");
		}
		return rowCount;
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws SQLException {

		insert("insert into students values('3', 'lily', '女', '18')",
				DbType.Local);
		update("update students set cName='lucy' where Id='3'", DbType.Local);
		delete("delete from students where Id='3'", DbType.Local);
		Map<String, String> map1 = querySingleData(
				"select * from students where age = '20'", DbType.Local);
		List<Map<String, String>> map2 = queryDataList(
				"select * from students", DbType.Local);
		System.out.println("ok");

	}
}
