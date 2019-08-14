package com.knowology.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLibPatternkeyDAO {
	
	public static Result QueryCount(String keys){
		StringBuilder sb = new StringBuilder();
		sb.append("select count(*) from patternkey where 1=1 ");
		List<String> lstpara = new ArrayList<String>();
		if (keys != null && keys != "" && keys.length() > 0) {
			if (GetConfigValue.isMySQL) {
				sb.append("and patternkey like ? ");
				lstpara.add("%" + keys + "%");
			} else {
				sb.append("and patternkey like '%'||?||'%' ");
				lstpara.add(keys);
			}
		}
		sb.append("order by keyid");
		Result rs = null;
		rs = Database.executeQuery(sb.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sb + "#" + lstpara );
		
		return rs;
	}
	
	public static Result GetData(String keys, int start, int limit) {
		StringBuilder sb = new StringBuilder();
		List<Object> lstpara = new ArrayList<Object>();
		if (GetConfigValue.isMySQL) {
			sb.append("select p.* from patternkey p where 1=1 ");
			if (keys != null && keys != "" && keys.length() > 0) {
				sb.append("and patternkey like ? ");
				lstpara.add("%" + keys + "%");
			}
			sb.append("limit ?,?");
			lstpara.add(start);
			lstpara.add(limit);
		} else {
			sb.append("select * from (select p.*,rownum r from patternkey p where 1=1 ");
			if (keys != null && keys != "" && keys.length() > 0) {
				sb.append("and patternkey like '%'||?||'%' ");
				lstpara.add(keys);
			}
			sb.append(")  where r >? and r <=?");
			lstpara.add(String.valueOf(start));
			lstpara.add(String.valueOf(start + limit));
			GlobalValue.myLog.info("GHJ start="+start+"  and  start + limit="+start + limit);

//			if("true".equalsIgnoreCase(ResourceBundle
//					.getBundle("commonLibGlobal").getString("isToMysql")) ? true:false){
//				// 绑定开始条数参数
//				lstpara.add(start);
//				// 绑定截止条数参数
//				lstpara.add(limit);
//			}
//			else{
//				// 绑定开始条数参数
//				lstpara.add(String.valueOf(start));
//				// 绑定截止条数参数
//				lstpara.add(String.valueOf(start + limit));
//			}
		}
		
		//文件日志
		GlobalValue.myLog.info( sb + "#" + lstpara );
		return Database.executeQuery(sb.toString(), lstpara.toArray());
	}
	
	/**
	 * 插入
	 * 
	 * @param keys
	 * @return
	 */
	public static Result InsertSelect(String keys){
		StringBuilder sb = new StringBuilder();
		sb.append("select * from patternkey where patternkey=?");
		List<String> lstpara = new ArrayList<String>();
		lstpara.add(keys);
		Result rs = null;
		rs = Database.executeQuery(sb.toString(), lstpara.toArray());
		//文件日志
		GlobalValue.myLog.info( sb + "#" + lstpara );
		
		return rs;
	}
	
	public static int Insert (String keys, User user){
		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String sql = "";
		if (GetConfigValue.isMySQL) {
			sql = "insert into patternkey (patternkey) values (?)";// 表的主键为自增
		} else {
			sql = "insert into patternkey values (seq_patternkey_id.nextval,?)";
		}
		List<String> lstpara = new ArrayList<String>();
		lstpara.add(keys);
		lstsql.add(sql);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 生成操作日志记录
		if(GetConfigValue.isOracle){
			lstsql.add("insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)");
		} else {
			lstsql.add("insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,now())");
		}
		lstpara = new ArrayList<String>();
		lstpara.add(user.getUserIP());
		lstpara.add(" ");
		lstpara.add(" ");
		lstpara.add("增加关键字");
		lstpara.add("上海");
		lstpara.add(user.getUserID());
		lstpara.add(user.getUserName());
		lstpara.add(keys);
		lstpara.add("PATTERNKEY");
		lstlstpara.add(lstpara);
		int i = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		
		//文件日志
		GlobalValue.myLog.info( lstsql + "#" + lstlstpara );
		
		return i;
	}
	
	public static Result DeleteSelect (String id){
		String keysql = "select patternkey from patternkey where keyid=" + id;
		Result rs = null;
		rs = Database.executeQuery(keysql);
		
		//文件日志
		GlobalValue.myLog.info( keysql );
		
		return rs;
	}
	
	public static int Delete(String id, User user, String patternkey){
		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String sql = "delete from patternkey where keyid=?";
		List<String> lstpara = new ArrayList<String>();
		lstpara.add(id);
		lstsql.add(sql);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 生成操作日志记录
		if(GetConfigValue.isOracle){
			lstsql.add("insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)");
		} else {
			lstsql.add("insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,now())");
		}
		
		lstpara = new ArrayList<String>();
		lstpara.add(user.getUserIP());
		lstpara.add(" ");
		lstpara.add(" ");
		lstpara.add("删除关键字");
		lstpara.add("上海");
		lstpara.add(user.getUserID());
		lstpara.add(user.getUserName());
		lstpara.add(patternkey);
		lstpara.add("PATTERNKEY");
		lstlstpara.add(lstpara);
		int i = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		//文件日志
		GlobalValue.myLog.info( lstsql + "#" + lstlstpara );
		
		return i;
	}
	
	public static Result UpdateSelect(String id, String newkeys, String oldkeys) {
		StringBuilder sb = new StringBuilder();
		List<String> lstpara = new ArrayList<String>();
		Result rs = null;
		sb.append("select * from patternkey where patternkey=?");
		lstpara.add(newkeys);
		rs = Database.executeQuery(sb.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sb + "#" + lstpara );
		
		return rs;
	}
	
	public static int Update(String id, String newkeys, String oldkeys, User user){
		List<String> lstpara = new ArrayList<String>();
		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String sql = "update patternkey set patternkey=? where keyid=?";
		lstpara.add(newkeys);
		lstpara.add(id);
		lstsql.add(sql);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 生成操作日志记录
		if(GetConfigValue.isOracle){
			lstsql.add("insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)");
		} else {
			lstsql.add("insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,now())");
		}
		lstpara = new ArrayList<String>();
		lstpara.add(user.getUserIP());
		lstpara.add(" ");
		lstpara.add(" ");
		lstpara.add("更新关键字");
		lstpara.add("上海");
		lstpara.add(user.getUserID());
		lstpara.add(user.getUserName());
		lstpara.add(oldkeys + "==>" + newkeys);
		lstpara.add("PATTERNKEY");
		lstlstpara.add(lstpara);
		int j = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		
		//文件日志
		GlobalValue.myLog.info( lstsql + "#" + lstlstpara );
		return j;
	}
	
}
