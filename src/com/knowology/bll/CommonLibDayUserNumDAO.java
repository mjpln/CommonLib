package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLibDayUserNumDAO {
	/**
	 * @description获取每日用户量数据
	 * @param hotType
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static Result getData(String startTime, String endTime,String channel) {
		// 定义SQL语句
		String sql = "";
		try{
			if(null == channel || "".equals(channel)||"全部".equals(channel)){
				if(GetConfigValue.isMySQL)
					sql = "select date_format(starttime,'%Y-%m-%d') time,count(distinct USERID) num from queryhistorylog where starttime>=str_to_date(?,'%Y-%m-%d %H:%i:%s') and starttime<=str_to_date(?,'%Y-%m-%d %H:%i:%s') group by date_format(starttime,'%Y-%m-%d') order by date_format(starttime,'%Y-%m-%d')";
				else 
					sql = "select to_char(starttime,'yyyy-mm-dd') time,count(distinct USERID) num from queryhistorylog where starttime>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and starttime<=to_date(?,'yyyy-mm-dd hh24:mi:ss') group by to_char(starttime,'yyyy-mm-dd') order by to_char(starttime,'yyyy-mm-dd')";
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + startTime + "," + endTime );
				
				return Database.executeQuery(sql, startTime, endTime);
			}else{
				if(GetConfigValue.isMySQL)
					sql = "select date_format(starttime,'%Y-%m-%d') time,count(distinct USERID) num from queryhistorylog where starttime>=str_to_date(?,'%Y-%m-%d %H:%i:%s') and starttime<=str_to_date(?,'%Y-%m-%d %H:%i:%s') and channel=? group by date_format(starttime,'%Y-%m-%d') order by date_format(starttime,'%Y-%m-%d')";
				else
					sql = "select to_char(starttime,'yyyy-mm-dd') time,count(distinct USERID) num from queryhistorylog where starttime>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and starttime<=to_date(?,'yyyy-mm-dd hh24:mi:ss') and channel=? group by to_char(starttime,'yyyy-mm-dd') order by to_char(starttime,'yyyy-mm-dd')";
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + startTime + "," + endTime + "," + channel );
				
				return Database.executeQuery(sql, startTime, endTime, channel);
			}
		}finally{
			sql = null;
		}
		
	}
}
