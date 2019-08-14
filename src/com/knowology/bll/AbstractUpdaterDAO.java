package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class AbstractUpdaterDAO {
	public static Result update(boolean flage, String table){
		Result rs = null;
		String sql = "select * from " +table;
		if(!flage)
			sql += " order by EditTime";
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;	
	}
	
	public static int MarkProcessedDataInDB(String[] a, String SelfIP){
		String sql = "";
		
		if(GetConfigValue.isMySQL){
			if(!a[0].equals("")){
				sql="update WordInc set IncStatus = concat(IncStatus, '#"
					+ SelfIP + "#)' where ";
				sql += "WordID = " + a[1];
				sql += " and DATE_FORMAT(EditTime,'%Y-%m-%d %H:%i:%s') = '" + a[2]+"'";
			}
			else{
				sql="update WordInc set IncStatus = '#"
					+ SelfIP + "#' where ";
				sql += "WordID = " + a[1];
				sql += " and EditTime = '" + a[2]+"'";
			}
		}
		else{
			sql = "update WordInc set IncStatus = IncStatus || '#"
				+ SelfIP + "#' where ";
			sql += "WordID = " + a[1];
			sql += " and to_char(EditTime, 'yyyy-MM-dd hh24:mi:ss') = '" + a[2]+"'";
		}
		
		try{ 
			return Database.executeNonQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	} 
	
	/**
	 * 方法名称： DeleteProcessedDataInDB
	 * 内容摘要：删除增量表中，已经更新到内存中、并且保存了的数据
	 * @description selectDeleteProcessedDataInDB和DeleteProcessedDataInDB在原理类中是同一方法
	 * @return
	 */
	public static Result selectDeleteProcessedDataInDB(){
		Result rs = null;
		String sql = "select * from KBDataInc";
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 方法名称： DeleteProcessedDataInDB
	 * 内容摘要：删除增量表中，已经更新到内存中、并且保存了的数据
	 * @param a 
	 * @return  >0 执行成功
				<0 没有执行相关sql或执行失败
	 */
	public static int DeleteProcessedDataInDB(String[] a){
		String sql = "delete KBDataInc where  KBDataID = " + a[0];
		if(GetConfigValue.isMySQL)
			sql += " and DATE_FORMAT(EditTime, '%Y-%m-%d %H:%i:%s')= '"+a[1]+"'";					
		else
			sql += " and to_char(EditTime, 'yyyy-MM-dd hh24:mi:ss') = '"+a[1]+"'";
		
		try{ 
			return Database.executeNonQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
}

