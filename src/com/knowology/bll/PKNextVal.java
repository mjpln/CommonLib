package com.knowology.bll;


import javax.servlet.jsp.jstl.sql.Result;

import org.apache.log4j.Logger;

import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class PKNextVal {	
	public static Logger logger = Logger.getLogger("commonlib");
	// / <summary>
	// / 获取序列的下一个值
	// / </summary>
	// / <param name="seqName">序列名</param>
	// / <returns>下一个值</returns>
	public static boolean MySQL = 
		GetConfigValue.isMySQL ? true : false;
	
	/**
	 * @description 	获取表主键对应的下一个值
	 * @param seqName	Oracle数据库中表主键对应的序列名称
	 * @param tabName	MySQL数据库中的表名
	 * @return
	 */
	public static int getNextVal(String seqName, String tabName) {
		String sql = null;
		// 组成sql语句
		if(MySQL){
			sql = "SELECT nextval('"+tabName+"') as seq";
		}else{
			sql = "select " + seqName + ".nextval  seq from dual";
		}
		
		Result rs = null;
		try {
			rs = Database.executeQuery(sql);
		} catch (Exception e) {
			logger.error("SQL 错误 ==> \r\n Sql: "+sql, e);
		}
		if (rs == null || rs.getRows().length == 0) {
			return Integer.MAX_VALUE;
		}
		return Integer.parseInt(rs.getRows()[0].get("seq").toString());
	}
}
