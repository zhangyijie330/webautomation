package com.autotest.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.List;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;
import org.uncommons.reportng.HTMLReporter;

/**
 * 生成Report并拷贝到另一目录
 * @author Mark
 */
public class Reporter extends HTMLReporter
{
 
	/**
	 * 重写HTMLReporter
	 * @param xmlSuites
	 * @param suites
	 * @param outputDirectoryName
	 */
	public void generateReport( List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectoryName )
	{
		super.generateReport( xmlSuites, suites, outputDirectoryName );
		generateAutoTestReport();
	}

	/**
	 * 拷贝报告
	 */
	private void generateAutoTestReport()
	{
		String reportPath = "reports/report " + getCurrentTimeStr() + "/";
		// 创建报告目录
		makeDir( reportPath );
		// 拷贝原来的报告到新建目录
		File srcPath = new File( "test-output/html/" );
		File[] srcFiles = srcPath.listFiles();
		for( File file : srcFiles )
		{
			copyFile( file, reportPath );
		}
	}

	/**
	 * 获取当前系统时间
	 * @return
	 */
	private String getCurrentTimeStr()
	{
		return Calendar.getInstance().getTime().toString().replace( ":", "_" );
	}
   
	/**
	 * 创建目录
	 * @param destPath
	 * @return
	 */
	public static boolean makeDir( String destPath )
	{
		File file = new File( destPath );
		return file.mkdirs();
	}
   
	/**
	 * 拷贝文件到一个目录
	 * @param srcFile
	 * @param destPath
	 */
	public static void copyFile( File srcFile, String destPath )
	{
		File destFile = new File( destPath + "/" + srcFile.getName() );
		try
		{
			BufferedReader bd = new BufferedReader( new InputStreamReader( new FileInputStream( srcFile ) ) );
			BufferedWriter bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( destFile ) ) );
			String s1 = "";
			s1 = bd.readLine();
			while( s1 != null )
			{
				bw.write( s1 );
				s1 = bd.readLine();
				if( s1 != null )
				{
					bw.newLine();
				}
			}
			bw.flush();
			bd.close();
			bw.close();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
	}
   
	/**
	 * 写文件
	 * @param file
	 * @param text
	 */
	public static void write( File file, String text )
	{
		try
		{
			PrintWriter out = new PrintWriter( file );
			try
			{
				out.print( text );
			}
			finally
			{
				out.close();
			}
		}
		catch( IOException e )
		{
			throw new RuntimeException( e );
		}
	}

}
