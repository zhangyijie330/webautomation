package com.autotest.testcases;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.autotest.utility.BaseConfigUtil;
import com.autotest.utility.ThreadUtil;

public class TestCase2_zyj {

	public static String getDateTime() {
		return "date '+%Y-%m-%d %H:%M:%S'";
	}

	public static void main(String[] args) throws IOException {
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
					System.out.println("恢复时间失败！");
				} else {
					System.out.println("恢复到当前时间: " + line);
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
