/**
 * 
 */
package com.knowology.DbDAO;

import java.sql.Clob;
import java.sql.SQLException;

import com.knowology.GlobalValue;


/**
 *描述：数据库读取值相关的操作类
 *@author: qianlei
 *@date： 日期：2015-4-14 时间：下午02:32:27
 */
public class DBValueOper {

/**
 * 
 *描述：当sql查询结果的字段值为null时，将其转换成空串返回
 *@author: qianlei
 *@date： 日期：2015-4-14 时间：下午02:34:37
 *@return String
 */
	public static String GetValidateStringObj4Null(Object str){
	   	 if(str==null)return"";
	   	 else return str.toString();
	    }
	

	/**
	 * 
	 * 方法名称clobToString
	 * 内容摘要：将oracle.sql.Clob类型转换成String类型，采用字符流为媒介的方式进行
	 * 首先，获取clob对象的字符流；然后，通过BufferedReader将该字符流读出存入java的String字符串对象中
	 * @author zhanggang
	 * @param clob对象
	 * @return clob转化后的string对象
	 * @throws
	 */
//	public static String clobToString(Clob clob)
//	{
//	if(clob==null)return "";
//	String reString = "";
//	Reader is = null;
//	try {
//	is = clob.getCharacterStream();	
//	BufferedReader br = new BufferedReader(is);
//	String s = null;	
//	s = br.readLine();	
//	StringBuffer sb = new StringBuffer();
//	while (s != null) {
//
//	sb.append(s);	
//	s = br.readLine();
//	}
//	reString = sb.toString();
//	}
//	catch(Exception ex)
//	{
//		GlobalValue.myLog.error("clobToString中出现异常："+ ex.toString());
//		return "";
//	}
//	return reString;
//	} 
	
	/**
	 * 
	 * Description:将Clob对象转换为String对象,Blob处理方式与此相同
	 * 
	 * @param clob
	 */
	public static String clobToString(Clob clob)  {
		try {
			return (clob != null ? clob.getSubString((long) 1, (int) clob.length()): "");
		} catch (SQLException e) {
			GlobalValue.myLog.error("【clobToString】"+e.toString());
			return "";
		}
	}

	
}
