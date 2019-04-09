package com.autotest.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class SSHUtil {
	/**
	 * 
	 * 作用：设置date命令字符串的
	 * 
	 */
	public static String getDateTime() {
		return "date '+%Y-%m-%d %H:%M:%S'";
	}

	/**
	 * 作用：ssh连接服务器，获取当前时间，格式为+%Y-%m-%d %H:%M:%S
	 * 
	 * @param log
	 * @return
	 * @throws IOException
	 */
	public static String sshCurrentTime(Logger log) throws IOException {
		// 需要把daysAtfer转换为Long型，否则天数多时，数值会越界
		String commandStr = getDateTime();
		String hostname = BaseConfigUtil.getServerConfig("hostname").trim();
		String username = BaseConfigUtil.getServerConfig("host_username")
				.trim();
		String password = BaseConfigUtil.getServerConfig("host_pwd").trim();
		String currentTime = null;
		// 指明连接主机的IP地址
		Connection conn = new Connection(hostname);
		Session ssh = null;
		// 连接到主机
		conn.connect();
		// 使用用户名和密码校验
		boolean isconn = conn.authenticateWithPassword(username, password);
		if (!isconn) {
			log.error("用户名称或者是密码不正确");
		} else {

			log.info("ssh已经连接OK");
			ssh = conn.openSession();
			// 执行命令
			ssh.execCommand(commandStr);
			// 获取控制台输出
			InputStream is = new StreamGobbler(ssh.getStdout());
			BufferedReader brs = new BufferedReader(new InputStreamReader(is));
			while (true) {
				String line = brs.readLine();
				if (line == null) {
					break;
				}
				currentTime = line;
			}
			brs.close();
		}
		// 连接的Session和Connection对象都需要关闭
		if (ssh != null) {
			ssh.close();
		}
		if (conn != null) {
			conn.close();
		}
		return currentTime;
	}

	/**
	 * 作用：ssh连接服务器，获取当前日期，格式为+%Y-%m-%d
	 * 
	 * @param log
	 * @return
	 * @throws IOException
	 */
	public static String sshCurrentDate(Logger log) throws IOException {
		// 需要把daysAtfer转换为Long型，否则天数多时，数值会越界
		String hostname = BaseConfigUtil.getServerConfig("hostname").trim();
		String username = BaseConfigUtil.getServerConfig("host_username")
				.trim();
		String password = BaseConfigUtil.getServerConfig("host_pwd").trim();
		String currentTime = null;
		// 指明连接主机的IP地址
		Connection conn = new Connection(hostname);
		Session ssh = null;
		// 连接到主机
		conn.connect();
		// 使用用户名和密码校验
		boolean isconn = conn.authenticateWithPassword(username, password);
		if (!isconn) {
			log.error("用户名称或者是密码不正确");
		} else {

			log.info("ssh已经连接OK");
			ssh = conn.openSession();
			// 执行命令
			ssh.execCommand("date '+%Y-%m-%d'");
			// 获取控制台输出
			InputStream is = new StreamGobbler(ssh.getStdout());
			BufferedReader brs = new BufferedReader(new InputStreamReader(is));
			while (true) {
				String line = brs.readLine();
				if (line == null) {
					break;
				}
				currentTime = line;
			}
			brs.close();
		}
		// 连接的Session和Connection对象都需要关闭
		if (ssh != null) {
			ssh.close();
		}
		if (conn != null) {
			conn.close();
		}
		return currentTime;
	}

	/**
	 * 作用：ssh连接服务器，修改时间
	 * 
	 * @param changeTime
	 *            传入的格式样例为'2016-08-05 19:58:13'
	 * @throws Exception
	 */
	public static void sshChangeTime(Logger log, String changeTime)
			throws IOException {
		String commandStr = null;
		String hostname = BaseConfigUtil.getServerConfig("hostname").trim();
		String username = BaseConfigUtil.getServerConfig("host_username")
				.trim();
		String password = BaseConfigUtil.getServerConfig("host_pwd").trim();
		// 指明连接主机的IP地址
		Connection conn = new Connection(hostname);
		Session ssh = null;
		try {
			// 连接到主机
			conn.connect();
			// 使用用户名和密码校验
			boolean isconn = conn.authenticateWithPassword(username, password);
			if (!isconn) {
				System.out.println("用户名称或者是密码不正确");
			} else {
				System.out.println("ssh已经连接OK");
				ssh = conn.openSession();
				// 使用多个命令用分号隔开
				commandStr = "date -s '" + changeTime + "' "
						+ "'+%Y-%m-%d %H:%M:%S'";
				ssh.execCommand(commandStr);
				// 将屏幕上的文字打印出来
				InputStream is = new StreamGobbler(ssh.getStdout());
				BufferedReader brs = new BufferedReader(new InputStreamReader(
						is));
				String line = brs.readLine();
				if (line == null) {
					log.info("修改服务器时间失败！");
				} else {
					log.info("服务器时间修改为:" + line);
				}
				brs.close();
			}
			// 连接的Session和Connection对象都需要关闭
			ssh.close();
			conn.close();
			ThreadUtil.sleep(3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * 作用：ssh连接服务器，恢复到当前时间
	 * 
	 * @throws IOException
	 */
	public static void sshRecoverTime(Logger log) throws IOException {
		String commandStr = "ntpdate time.nist.gov";
		String hostname = BaseConfigUtil.getServerConfig("hostname").trim();
		String username = BaseConfigUtil.getServerConfig("host_username")
				.trim();
		String password = BaseConfigUtil.getServerConfig("host_pwd").trim();
		// 指明连接主机的IP地址
		Connection conn = new Connection(hostname);
		Session ssh = null;
		try {
			// 连接到主机
			conn.connect();
			// 使用用户名和密码校验
			boolean isconn = conn.authenticateWithPassword(username, password);
			if (!isconn) {
				System.out.println("用户名称或者是密码不正确");
			} else {
				System.out.println("ssh已经连接OK");
				ssh = conn.openSession();
				// 执行命令
				ssh.execCommand(commandStr);
				// 将屏幕上的文字全部打印出来
				InputStream is = new StreamGobbler(ssh.getStdout());
				BufferedReader brs = new BufferedReader(new InputStreamReader(
						is));
				String line = brs.readLine();
				if (line == null) {
					log.error("恢复时间失败！");
				} else {
					log.info("恢复到当前时间: " + line);
				}
				brs.close();
			}
			// 连接的Session和Connection对象都需要关闭
			ssh.close();
			conn.close();
			ThreadUtil.sleep(3);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
	}

}
