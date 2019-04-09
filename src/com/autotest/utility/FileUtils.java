package com.autotest.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

/**
 * 文件操作工具类
 * 
 * @author wb0002
 * 
 */
public class FileUtils {

	/**
	 * UTF-8
	 */
	static String ENCODING_UTF8 = "UTF-8";

	/**
	 * GBK
	 */
	static String ENCODING_GBK = "GBK";

	/**
	 * GB2312
	 */
	static String ENCODING_GB2312 = "GB2312";

	/**
	 * ISO-8859-1
	 */
	static String ENCODING_ISO_8859_1 = "ISO-8859-1";

	/**
	 * 读取文件内容，以行为单位读。会自动进行UTF-8编码。
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFile(String filePath) {
		StringBuffer sb = new StringBuffer();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(new File(filePath));
			isr = new InputStreamReader(fis, ENCODING_UTF8);
			br = new BufferedReader(isr);
			String line = null;
			while (null != (line = br.readLine())) {
				sb.append(line + "\r\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != br) {
					br.close();
				}
				if (null != isr) {
					isr.close();
				}
				if (null != fis) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 读取文件内容，以行为单位读。需指定编码格式。当编码不存在时，采用UTF-8编码。
	 * 
	 * @param filePath
	 *            文件路径
	 * @param encoding
	 *            编码格式
	 * @return
	 */
	public static String readFile(String filePath, Encoding encoding) {
		StringBuffer sb = new StringBuffer();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(new File(filePath));
			switch (encoding) {
			case encode_GB2312:
				isr = new InputStreamReader(fis, ENCODING_GB2312);
				break;
			case encode_ISO_8859_1:
				isr = new InputStreamReader(fis, ENCODING_ISO_8859_1);
				break;
			case encode_UTF8:
				isr = new InputStreamReader(fis, ENCODING_UTF8);
				break;
			case encode_GBK:
				isr = new InputStreamReader(fis, ENCODING_GBK);
				break;
			default:
				isr = new InputStreamReader(fis, ENCODING_UTF8);
				break;
			}
			br = new BufferedReader(isr);
			String line = null;
			while (null != (line = br.readLine())) {
				sb.append(line + "\r\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != br) {
					br.close();
				}
				if (null != isr) {
					isr.close();
				}
				if (null != fis) {
					fis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * 读取文件内容(此方式读取中文可能会乱码)
	 * 
	 * @param filePath
	 * @return
	 */
	public static String readFileByFileReader(String filePath) {
		StringBuffer sb = new StringBuffer();
		File file = new File(filePath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String tempString = null;
			while ((tempString = br.readLine()) != null) {
				sb.append(tempString + "\r\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 将文件的unicode编码转换成中文
	 * 
	 * @param ori
	 * @return
	 */
	public static String convertUnicode(String ori) {
		char aChar;
		int len = ori.length();
		StringBuffer outBuffer = new StringBuffer(len);
		for (int x = 0; x < len;) {
			aChar = ori.charAt(x++);
			if (aChar == '\\') {
				aChar = ori.charAt(x++);
				if (aChar == 'u') {
					int value = 0;
					for (int i = 0; i < 4; i++) {
						aChar = ori.charAt(x++);
						switch (aChar) {
						case '0':
						case '1':
						case '2':
						case '3':
						case '4':
						case '5':
						case '6':
						case '7':
						case '8':
						case '9':
							value = (value << 4) + aChar - '0';
							break;
						case 'a':
						case 'b':
						case 'c':
						case 'd':
						case 'e':
						case 'f':
							value = (value << 4) + 10 + aChar - 'a';
							break;
						case 'A':
						case 'B':
						case 'C':
						case 'D':
						case 'E':
						case 'F':
							value = (value << 4) + 10 + aChar - 'A';
							break;
						default:
							throw new IllegalArgumentException(
									"Malformed   \\uxxxx   encoding.");
						}
					}
					outBuffer.append((char) value);
				} else {
					if (aChar == 't')
						aChar = '\t';
					else if (aChar == 'r')
						aChar = '\r';
					else if (aChar == 'n')
						aChar = '\n';
					else if (aChar == 'f')
						aChar = '\f';
					outBuffer.append(aChar);
				}
			} else
				outBuffer.append(aChar);

		}
		return outBuffer.toString();
	}

	/**
	 * 写入txt文件内容(UTF-8编码写入)
	 * 
	 * @param text
	 * @param dstFilePath
	 */
	public static void writeToTxtFile(String text, String dstFilePath) {
		OutputStreamWriter write = null;
		BufferedWriter writer = null;
		try {
			File file = new File(dstFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			write = new OutputStreamWriter(new FileOutputStream(file, false),
					ENCODING_UTF8);
			writer = new BufferedWriter(write);
			writer.write(text);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != writer) {
					writer.close();
				}
				if (null != write) {
					write.close();
				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
	}

}
