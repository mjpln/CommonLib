package com.knowology.DataLayer;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.log4j.Logger;

import com.knowology.dal.Database;

public class Gr {
	public Logger logger = Logger.getLogger("commonlib");
	// 数据库类型标识
	private boolean mySQL;
	// 表字段类型对应关系集合
	private Map<String, String> ctMap;
	
	/**
	 * @description
	 * @param classify
	 * @param className
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> find(String classify, 	
			  							  String className, 
			  							  List<Object> param
			  							  ) throws SQLException{
		// SQL语句.
		String sql = "";
		// 查询结果
		Result result = null;
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		// 处理分类
		if(null == classify){
			// CommonData类相关数据查询
			if(className.equals("CommonData")){
				// 数据库判断<MySQL/ORACLE>
				if (mySQL) {
					// MySQL转义符无须通过关键字ESCAPE指定
					sql = "SELECT TABLE_NAME NAME FROM INFORMATION_SCHEMA.TABLES " +
							"WHERE TABLE_NAME LIKE 'KM\\_%'";
				} else {
					sql = "SELECT TABLE_NAME NAME FROM USER_TABLES WHERE TABLE_NAME  " +
							"LIKE 'KM\\_%' ESCAPE '\\'";
				}
			}
			
			// DayUserNumDao类相关数据查询
			if(className.equals("DayUserNumDao")){
				if(mySQL){
					// 时间格式必须为'%Y-%m-%d %H:%i:%s'大小写也必须一致, 否则无法查出数据
					sql = "select DATE_FORMAT(starttime,'%Y-%m-%d') TIME, " +
						"count(distinct USERID) NUM from queryhistorylog " +
							"where starttime>=str_to_date(?,'%Y-%m-%d %H:%i:%s') " +
								"and starttime<=str_to_date(?,'%Y-%m-%d %H:%i:%s') " +
									"group by DATE_FORMAT(starttime,'%Y-%m-%d') " +
										"order by DATE_FORMAT(starttime,'%Y-%m-%d')";
				}else{
					sql = "select to_char(starttime,'yyyy-mm-dd') time, " +
						"count(distinct USERID) num from queryhistorylog " +
							"where starttime>=to_date(?,'yyyy-mm-dd hh24:mi:ss') " +
								"and starttime<=to_date(?,'yyyy-mm-dd hh24:mi:ss') " +
									"group by to_char(starttime,'yyyy-mm-dd') " +
										"order by to_char(starttime,'yyyy-mm-dd')";
				}
			}
			
			if(className.equals("TimDataDao")){
				// MySQL和ORACLE  SQL语句相同
				sql = "SELECT MAX(ID) FROM KM_TIMDATARES WHERE TIMDATAID=?";
				sql = "SELECT RESULT RES,DATETIME DT FROM KM_TIMDATARES WHERE ID="+"("+sql+")";
			}
			
			if(className.equals("QueryHistoryLogSplitTask")){
				// if(isMySQL) {} else {}
				sql = "SELECT MAX(LOGID) ID, COUNT(*) CN FROM QUERYHISTORYLOGSPLIT";
			}
			
			if(className.equals("")){
				if(mySQL){
					
				}else{
					
				}
			}
			
			if(className.equals("")){
				if(mySQL){
					
				}else{
					
				}
			}
		}else{
			if(className.equals("")){
				if(mySQL){
					
				}else{
					
				}
			}
			
			if(className.equals("")){
				if(mySQL){
					
				}else{
					
				}
			}
			
			if(className.equals("")){
				if(mySQL){
					
				}else{
					
				}
			}
			
			if(className.equals("")){
				if(mySQL){
					
				}else{
					
				}
			}
		}
		
		try {
			if (null == param) {
				result = Database.executeQueryReport(sql);
			} else {
				result = Database.executeQueryReport(sql, param.toArray());
			}
			
			if(result.getRows().length>0){
				
			}
			
		} finally {
			;
		}
		
		return list;
	}
	
	public int update(String classify, String className, List<Object> param) throws SQLException{
		String sql = "";
		int res = 0;
		if(className.equals("PlanTaskAction")){
			sql = "UPDATE KM_PLANTASK SET THREADID=?, EXCUTESTATE = '执行中' WHERE ID=?";
		}
		
		if(className.equals("SchduleJobAction")){
			sql = "UPDATE KM_SCHDULEJOB SET EXCUTESTATE ='执行中' WHERE ID=?";
		}
		
		if(className.equals("SchduleJobAction")){
			if(classify.equals("planTask")){
				sql = "UPDATE KM_PLANTASK SET EXCUTESTATE = '未执行' WHERE ID=?";
			}else if(classify.equals("schduleJob")){
				sql = "UPDATE KM_SCHDULEJOB SET EXCUTESTATE ='未执行' WHERE ID=?";
			}else if(classify.equals("timData")){
				sql = "UPDATE KM_TIMDATA SET EXCUTESTATE = '未执行' WHERE ID=?";
			}
		}
		
		if(className.equals("TimDataAction")){
			sql = "UPDATE KM_TIMDATA SET THREADID=?, EXCUTESTATE = '执行中' WHERE ID=?";
		}
		
		if(className.equals("TimDataDao")){
			sql = "INSERT INTO KM_TIMDATARES(ID, TIMDATAID, NAME, DATAITEM, RESULT, DATETIME) VALUES (?,?,?,?,?,?)"; 
		}
		
		// 数据库操作
		try {
			if(null == param){
				res = Database.executeNonQueryReport(sql);
			}else{
				res = Database.executeNonQueryReport(sql, param.toArray());
			}
		} finally {
			;
		}
		
		return res;
	}
	
	public List<Map<String, Object>> resultToList(Result result){
		String key = "";
		Object value = "";
		String type = "";
		
		// sql查询结果的一行记录
		SortedMap<String, Object> st = null;
		// 重置sql查询结果一行记录集合
		Map<String, Object> record = null;
		// sql查询结果重置后所有记录集合
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		try{
			for(int i=0; i<result.getRows().length; i++){
				record = new HashMap<String, Object>();
				st = result.getRows()[i];
				Iterator<Entry<String, Object>> itor = st.entrySet().iterator();
				while(itor.hasNext()){
					Entry<String, Object> entry = itor.next();
					key = entry.getKey();
					// value = entry.getValue();
					type = ctMap.get(key);
					if(null != key && key.equals("CLOB")){
						Clob clob = (Clob) entry.getValue();
						if(null != clob){
					    	String standInput = clob.getSubString((long)1,(int)clob.length());
					    }
					}else{
						value = entry.getValue();
					}
				}	
				list.add(record);
			}
		} catch(Exception e){
			logger.error("");
		}finally{
			key = null;
			value = null;
			st = null;
			record = null;
		}
		
		return list;
	}
	
	public boolean getMySQL() {
		return mySQL;
	}
	public void setMySQL() {
		String isNot = Database.getCommmonLibJDBCValues("connectFrom");
		if(null != isNot && isNot.equals("mysql")){
			this.mySQL = true;
		}else{
			this.mySQL = false;
		}
	}
	
	public Map<String, String> getCtMap() {
		return ctMap;
	}
	public void setCtMap(String tableName) {
		String key = "";
		String value = "";
		Object object = null;
		
		String sql = "";
		Result result = null;
		SortedMap<String, Object> st = null;
		if(mySQL){
			sql = "SELECT COLUMN_NAME C, DATA_TYPE T FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME=?";
		}else {
			// sql = "SELECT * FROM USER_TAB_COLUMNS WHERE TABLE_NAME = ?";
			sql = "SELECT COLUMN_NAME C, DATA_TYPE T, DATA_LENGTH FROM ALL_TAB_COLUMNS WHERE TABLE_NAME=UPPER(?)";
		}
		try{
			result = Database.executeQueryReport(sql, tableName);
			// result一定部位null, 当为null表名sql执行异常
			if(result.getRows().length > 0){
				this.ctMap = new HashMap<String, String>();
				for(int i=0; i<result.getRows().length; i++){
					st = result.getRows()[i];
					Iterator<Entry<String, Object>> itor = st.entrySet().iterator();
					while(itor.hasNext()){
						Entry<String, Object> entry = itor.next();
						key = entry.getKey();
						object = entry.getValue();
						if(null != object){
							value = object.toString();
							// 字段类型为NULL则无须记录
							this.ctMap.put(key, value);
						}
					}	
				}
			}
		}catch(Exception e){
			logger.error("SQL 错误 ==> \r\n Sql: "+sql, e);
		}finally{
			sql = null;
			result = null;
		}
	}
}
