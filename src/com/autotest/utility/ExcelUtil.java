/** 
 * @date 2016年9月8日 下午2:05:32
 * @version 1.0
 * 
 * @author xudashen
 */
package com.autotest.utility;

import java.io.IOException;
import java.nio.charset.Charset;

import com.csvreader.CsvWriter;

/**
 * @author xudashen
 * 
 * @date 2016年9月8日 下午2:05:32
 */
public class ExcelUtil {

	/**
	 * 功能：TODO
	 * 
	 * @author xudashen
	 * @param fileName
	 * @param map
	 * @return void
	 * @date 2016年9月8日 下午2:06:50
	 */
	public static void writerCsv(String fileName, String[] title,
			String[] content) {
		CsvWriter wr = new CsvWriter(fileName, ',', Charset.forName("UTF-8"));
		// String[] title = { "uid", "uname", "realName", "mobile" };
		// String[] content = { map.get("uid"), map.get("uname"),
		// map.get("real_name"), map.get("mobile") };
		try {
			wr.writeRecord(title);
			wr.writeRecord(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != wr) {
				wr.close();
			}
		}
	}

}
