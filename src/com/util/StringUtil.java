package com.util;

/**  
 * @Project: SynchronizeInterface
 * @Title: StringUtil.java
 * @Package com.knowology.util
 * @author xsheng_knowology@163.com
 * @date 2015-12-22 15:00:00
 * @Copyright: 2015 www.knowology.cn Inc. All rights reserved.
 * @version V1.0   
 */

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Clob;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * 内容摘要： 字符串操作相关类 类修改者： 修改日期: 修改说明:
 * 
 * @ClassName StringUtil
 *            <p>
 *            Company: knowology
 *            </p>
 * @author xsheng_knowology@163.com
 * @date 2015-12-22 15:00:00
 * @version V1.0
 */
public class StringUtil {
	// 日志输出
	private static Logger log = Logger.getLogger(StringUtil.class);

	/***
	 * 方法名：changePunctuation 内容摘要：逗号换成破折号
	 * 
	 * @param
	 * @return string
	 * @throws Exception
	 */
	static public String changePunctuation(String str) {
		String str1 = str.replace(",", "->");
		str1 = str1.replaceAll("，", "->");
		return str1;
	}

	/***
	 * 方法名：StringToNumber 内容摘要：逗号换成破折号
	 * 
	 * @param
	 * @return string
	 * @throws Exception
	 */
	static public Number stringToNumber(String str) throws Exception {
		Number num = NumberFormat.getInstance().parse(str);
		return num;
	}

	/***
	 * 方法名：clobToString 内容摘要：处理oracle数据库大字段
	 * 
	 * @param clob
	 *            大字段类型数据
	 * @return string
	 * @throws Exception
	 */
	static public String oracleClob2Str(Clob clob) throws Exception {
		return (clob != null ? clob.getSubString(1, (int) clob.length()) : null);
	}

	static public String clobToString(Clob ret) throws Exception {
		String reString = "";
		Reader is = ret.getCharacterStream();// 得到流
		BufferedReader br = new BufferedReader(is);
		String s = br.readLine();
		StringBuffer sb = new StringBuffer();
		while (s != null) {// 执行循环将字符串全部取出,并赋值给StringBuffer由StringBuffer转成String
			sb.append(s);
			s = br.readLine();
		}
		br.close();
		is.close();
		reString = sb.toString();
		return reString;
	}

	/**
	 * 方法名称： transfermPunctuation 内容摘要：将中文标点符号转换成英文标点符号
	 * 
	 * @author lcen 2014-8-21
	 * @param str
	 *            要处理的字符串
	 * @return String
	 */
//	public static String transfermPunctuation(String str) {
//		str = str.replace("）", ")");
//		str = str.replace("（", "(");
//		str = str.replace("？", "?");
//		str = str.replace("，", ",");
//		str = str.replace("；", ";");
//		str = str.replace("“", "\"");
//		str = str.replace("”", "\"");
//		str = str.replace("‘", "''");
//		str = str.replace("’", "''");
//		str = str.replace("：", ":");
//		str = str.replace("。", ".");
//		return str;
//	}

	/**
	 * 方法名称： deleteSpecialCharacter 内容摘要： 删除空格、回车、换行符、制表符特殊字符
	 * 
	 * @author lcen 2014-8-21
	 * @param str
	 *            要处理的字符串
	 * @return String
	 */
	public static String deleteSpecialCharacter(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 方法名称： getCurrentTime 内容摘要： 获得当前的日期+时间
	 * 
	 * @author xsheng 2015-01-05
	 * @return String
	 */
	public static String getCurrentTime() {
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		return simpleDateFormat.format(date);
	}

	private static ResourceBundle rb;
	private static BufferedInputStream inputStream;
	static {
		// rb = ResourceBundle.getBundle("xcc.resourceBundle");
		// String proFilePath ="C:\\JGH_FAQ\\config.properties";
		// String proFilePath ="/app/FAQ/ALL/config.properties";
		String proFilePath = System.getProperty("user.dir") + "/src/"
				+ "jdbc_mysql.properties";

		try {
			inputStream = new BufferedInputStream(new FileInputStream(
					proFilePath));
			rb = new PropertyResourceBundle(inputStream);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 方法名称： getConfigInfo 内容摘要： 读取配置文件
	 * 
	 * @author xsheng 2015-01-05
	 * @return String
	 */
	public static String getConfigInfo(String key) {
		// ResourceBundle resourcesTable =
		// ResourceBundle.getBundle("C:/docTest/config",Locale.getDefault());
		String result = null;
		try {
			// result = new
			// String(resourcesTable.getString(key).getBytes("ISO-8859-1"),"UTF-8");
			result = new String(rb.getString(key).getBytes("ISO-8859-1"),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error("解析配置文件出错" + e);
		}
		return result;
	}

	/**
	 * 方法名称： Text_Length 内容摘要：计算字符串在内存中的占用长度
	 * 
	 * @author lcen 2014-8-21
	 * @param Text
	 * @return int
	 * @throws
	 * 
	 */
	public static int textLength(String Text) {
		int len = 0;
		for (int i = 0; i < Text.length(); i++) {
			byte[] byte_len = Text.substring(i, i + 1).getBytes();
			if (byte_len.length > 1)
				len += 2; // 如果长度大于1，是中文，占两个字节，+2
			else
				len += 1; // 如果长度等于1，是英文，占一个字节，+1
		}
		return len;
	}

	/**
	 * @function 将字符串中数字、英文字符、中午字符之外的其他字符去掉
	 * @author ghj
	 * @param str
	 * @return
	 */
	public static String docNameFormal2(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("([^a-zA-Z0-9\u4E00-\u9FA5])+");
			Matcher m = p.matcher(str);
			// boolean b= m.matches();
			// System.out.println(b);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 方法名称： getConfigInfo 内容摘要： 读取配置文件
	 * 
	 * @author xsheng 2015-01-05
	 * @return String
	 */
	public static String constructUUID() {
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		// 去除原生态uuid中的"-"
		str = str.substring(0, 8) + str.substring(9, 13)
				+ str.substring(14, 18) + str.substring(19, 23)
				+ str.substring(24);
		return str;
	}
}
