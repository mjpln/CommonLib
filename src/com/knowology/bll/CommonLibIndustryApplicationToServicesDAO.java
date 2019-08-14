package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.dal.Database;

public class CommonLibIndustryApplicationToServicesDAO {
	
	/**
	 *@description  获得四层结构信息
	 *@return 
	 *@returnType Result 
	 */
	public static Result getIndustryapplicationToServicesInfo() {
		String sql = "select * from m_industryapplication2services ";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *@description  通过商家组织应用获得四层结构信息
	 *@return 
	 *@returnType Result 
	 */
	public static Result getIndustryapplicationToServicesInfo(String industry ,String organization, String application ) {
		String sql = "select * from m_industryapplication2services where  industry =? and organization='"+organization+"' and  application ='"+application+"'";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql,industry);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *@description  通过商家组织应用获得四层结构信息
	 *@return 
	 *@returnType Result 
	 */
	public static Result getIndustryapplicationToServicesInfo(String industry ,String organization, String application ,String isshow) {
//		String sql = "select * from m_industryapplication2services where  industry = '"+industry+"' and organization='"+organization+"' and  application ='"+application+"' and isshow ='"+isshow+"'";
		String sql = "select * from m_industryapplication2services where  industry =? and organization=? and  application =?";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, industry, organization, application);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	
	
	/**
	 *@description  获得行业
	 *@return 
	 *@returnType Result 
	 */
	public static Result getIndustry() {
		String sql = "select  distinct industry  name from m_industryapplication2services  where isshow ='是' order by industry ";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *@description  获得全部行业
	 *@return 
	 *@returnType Result 
	 */
	public static Result getIndustryAll() {
		String sql = "select  distinct industry  name from m_industryapplication2services  order by industry ";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	
	/**
	 *@description  通过行业获得商家
	 *@return 
	 *@returnType Result 
	 */
	public static Result getOrganizationByIndustry(String industry) {
		String sql = "select  distinct organization  name from m_industryapplication2services where   isshow ='是' and industry =?  ";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql,industry);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *@description  通过行业获得全部商家
	 *@return 
	 *@returnType Result 
	 */
	public static Result getOrganizationByIndustryAll(String industry) {
		String sql = "select  distinct organization  name from m_industryapplication2services where industry =?  ";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql,industry);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *@description  通过行业,商家获得应用
	 *@return 
	 *@returnType Result 
	 */
	public static Result getApplicationByIndustryAndOrganization(String industry,String organization) { 
		String sql = "select  distinct application  name from m_industryapplication2services where   isshow ='是' and industry =? and organization =?  ";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql,industry,organization); 
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *@description  通过行业,商家获得全部应用
	 *@return 
	 *@returnType Result 
	 */
	public static Result getApplicationByIndustryAndOrganizationAll(String industry,String organization) { 
		String sql = "select  distinct application  name from m_industryapplication2services where industry =? and organization =?  ";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql,industry,organization); 
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
}
