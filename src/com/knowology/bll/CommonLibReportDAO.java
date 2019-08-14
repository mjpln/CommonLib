package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLibReportDAO {
	/**
	 * @description获取用户的信息
	 * @return
	 */
	public static Result getUserName(String workerid, String customer){
//		return Database.executeQuery("select distinct name from worker where (workerid ='"+workerid+"' or customer='"+customer+"') ");
		return Database.executeQuery("select distinct name from worker where (workerid ='"+workerid+"' or customer='全行业') ");
	}
	
	/**
	 * @description查询某段时间内员工工作量
	 * @param starttime
	 * @param endtime
	 * @param userArr
	 * @return
	 */
	public static Result queryAndexportWordload(String starttime, String endtime, Object[] userArr){
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义查询工作量的SQL语句
		if(GetConfigValue.isMySQL)
			sql.append("SELECT t.workerid,t.uptime,t.name from (select distinct object wordpat,workerid,DATE_FORMAT(TIME,'%Y-%m-%d') uptime,workername name from operationlog where tablename=? and operation in (?,?,?) and time>=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and time<=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and workername in (");
		else
			sql.append("select t.workerid,t.uptime,t.name from (select distinct object wordpat,workerid,to_char(time,'yyyy-mm-dd') uptime,workername name from operationlog where tablename=? and operation in (?,?,?) and time>=to_date(?,'yyyy-mm-dd Hh24:mi:ss') and time<=to_date(?,'yyyy-mm-dd Hh24:mi:ss') and workername in (");
		
		// 绑定tablename参数
		lstpara.add("WORDPAT");
		// 绑定操作参数
		lstpara.add("增加词模");
		// 绑定操作参数
		lstpara.add("增加简单词模");
		// 绑定操作参数
		lstpara.add("增加模板");
		// 绑定开始时间参数
		lstpara.add(starttime);
		// 绑定结束时间参数
		lstpara.add(endtime);
		// 循环遍历用户数组
		for (int i = 0; i < userArr.length; i++) {
			// 判断是否是最后一个
			if (i != userArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?))t");
			}
			// 绑定用户名称参数
			lstpara.add(userArr[i]);
		}
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}
	
	/**
	 * @description获取渠道
	 * @return
	 */
	public static Result getChannel(){
		// 查询渠道
		return Database.executeQuery("select distinct channel from t_channel order by channel");
	}
	
	/**
	 * @description查询某段时间内所有的PV量
	 * @param starttime
	 * @param endtime
	 * @param channelArr
	 * @return
	 */
	public static Result getAndExportPvAll(String starttime, String endtime, Object[] channelArr){
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义查询各渠道的数量的SQL语句
		if(GetConfigValue.isMySQL)
			sql.append("select channel,DATE_FORMAT(starttime,'%Y-%m-%d') savetime,count(id) cn from queryhistorylog where starttime>=str_to_date(?,'%Y-%m-%d %H:%i:%s') and starttime<=str_to_date(?,'%Y-%m-%d %H:%i:%s') and channel in (");
		else
			sql.append("select channel,to_char(starttime,'yyyy-mm-dd') savetime,count(id) cn from queryhistorylog where starttime>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and starttime<=to_date(?,'yyyy-mm-dd hh24:mi:ss') and channel in (");
		// 绑定开始时间参数
		lstpara.add(starttime);
		// 绑定结束时间参数
		lstpara.add(endtime);
		// 循环遍历渠道数组
		for (int i = 0; i < channelArr.length; i++) {
			// 判断是否是最后一个
			if (i != channelArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定渠道参数
			lstpara.add(channelArr[i]);
		}
		// 将SQL语句补充完整
		if(GetConfigValue.isMySQL)
			sql.append(" group by channel,DATE_FORMAT(starttime,'%Y-%m-%d') order by channel");
		else
			sql.append(" group by channel,to_char(starttime,'yyyy-mm-dd') order by channel");
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}
	
	/**
	 * @description查询某段时间内运行详情
	 * @param starttime
	 * @param endtime
	 * @param channelArr
	 * @param sqlno
	 * @return
	 */
	public static Result getRobotRate(String starttime, String endtime, Object[] channelArr, int sqlno){
		// 定义SQL语句
		StringBuilder sql =  new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		switch(sqlno){
		case 1:
			// 定义查询未识别的数量
			if(GetConfigValue.isMySQL)
				sql.append("select channel,DATE_FORMAT(starttime,'%Y-%m-%d') savetime,count(id) cn from queryhistorylog where (abstract like '%SYS默认回复%' or abstract='未匹配摘要') and starttime>=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and starttime<=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and channel in (");
			else 
				sql.append("select channel,to_char(starttime,'yyyy-mm-dd') savetime,count(id) cn from queryhistorylog where (abstract like '%SYS默认回复%' or abstract='未匹配摘要') and starttime>=to_date(?,'yyyy-mm-dd Hh24:mi:ss') and starttime<=to_date(?,'yyyy-mm-dd Hh24:mi:ss') and channel in (");
			break;
		case 2:
			if(GetConfigValue.isMySQL)
				sql.append("select channel,DATE_FORMAT(starttime,'%Y-%m-%d') savetime,count(id) cn from queryhistorylog where starttime>=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and starttime<=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and channel in (");
			else
				sql.append("select channel,to_char(starttime,'yyyy-mm-dd') savetime,count(id) cn from queryhistorylog where starttime>=to_date(?,'yyyy-mm-dd Hh24:mi:ss') and starttime<=to_date(?,'yyyy-mm-dd Hh24:mi:ss') and channel in (");
			break;
		default:
			break;
		}
		// 绑定开始时间参数
		lstpara.add(starttime);
		// 绑定结束时间参数
		lstpara.add(endtime);
		// 循环遍历渠道数组
		for (int i = 0; i < channelArr.length; i++) {
			// 判断是否是最后一个
			if (i != channelArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定渠道参数
			lstpara.add(channelArr[i]);
		}
		// 将SQL语句补充完整
		if(GetConfigValue.isMySQL)
			sql.append(" group by channel,DATE_FORMAT(starttime,'%Y-%m-%d')");
		else
			sql.append(" group by channel,to_char(starttime,'yyyy-mm-dd')");
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}
	
	/**
	 * @description获取未理解问题
	 * @param starttime
	 * @param endtime
	 * @param channelArr
	 * @return
	 */
	public static Result getRobotUnMatched(String starttime, String endtime, Object[] channelArr){
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义未识别的SQL语句
		if(GetConfigValue.isMySQL)
			sql.append("select * from (select query,channel,count(id) cn from queryhistorylog where (abstract like '%SYS默认回复%' or abstract='未匹配摘要') and starttime>=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and starttime<=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and channel in (");
		else
			sql.append("select * from (select query,channel,count(id) cn from queryhistorylog where (abstract like '%SYS默认回复%' or abstract='未匹配摘要') and starttime>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and starttime<=to_date(?,'yyyy-mm-dd hh24:mi:ss') and channel in (");
		// 绑定开始时间参数
		lstpara.add(starttime);
		// 绑定结束时间参数
		lstpara.add(endtime);
		// 循环遍历渠道数组
		for (int i = 0; i < channelArr.length; i++) {
			// 判断是否是最后一个
			if (i != channelArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整<右括号最好放在外面>
				sql.append("?)");
			}
			// 绑定渠道参数
			lstpara.add(channelArr[i]);
		}
		// 将SQL语句补充完整<词条语句放在外面防止渠道为空>
		sql.append(" group by query,channel) t order by t.cn desc");
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}
	
	/**
	 * @description读取数据库,获取未识别问题,生成Excel文件,返回文件的路径
	 * @param starttime
	 * @param endtime
	 * @param channelArr
	 * @return
	 */
	public static Result exportUnMatched(String starttime, String endtime, Object[] channelArr){
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义未识别的SQL语句
		if(GetConfigValue.isMySQL)
			sql.append("select query,channel,count(id) cn from queryhistorylog where (abstract like '%SYS默认回复%' or abstract='未匹配摘要') and starttime>=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and starttime<=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and channel in (");
		else
			sql.append("select query,channel,count(id) cn from queryhistorylog where (abstract like '%SYS默认回复%' or abstract='未匹配摘要') and starttime>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and starttime<=to_date(?,'yyyy-mm-dd hh24:mi:ss') and channel in (");
		// 绑定开始时间参数
		lstpara.add(starttime);
		// 绑定结束时间参数
		lstpara.add(endtime);
		// 循环遍历渠道数组
		for (int i = 0; i < channelArr.length; i++) {
			// 判断是否是最后一个
			if (i != channelArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定渠道参数
			lstpara.add(channelArr[i]);
		}
		// 将SQL语句补充完整
		sql.append(" group by query,channel order by query");
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}
	
	/**
	 * 读取数据库，获取全部咨询，生成Excel文件，返回文件的路径
	 * 
	 * @param params
	 * @return
	 */
	public static Result exportRobotDetailAll(String starttime, String endtime, Object[] channelArr){
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合 
		List<Object> lstpara = new ArrayList<Object>();
		// 定义总咨询量的SQL语句
		if(GetConfigValue.isMySQL)
			sql.append("select query,channel,abstract,answer,DATE_FORMAT(max(starttime),'%Y-%m-%d %H:%i:%s') as savetime,count(id) cn from queryhistorylog where starttime>=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and starttime<=STR_TO_DATE(?,'%Y-%m-%d %H:%i:%s') and channel in (");
		else
			sql.append("select query,channel,abstract,answer,to_char(max(starttime),'yyyy-mm-dd Hh24:mi:ss') as savetime,count(id) cn from queryhistorylog where starttime>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and starttime<=to_date(?,'yyyy-mm-dd hh24:mi:ss') and channel in (");
		// 绑定开始时间参数
		lstpara.add(starttime);
		// 绑定结束时间参数
		lstpara.add(endtime);
		// 循环遍历渠道数组
		for (int i = 0; i < channelArr.length; i++) {
			// 判断是否是最后一个
			if (i != channelArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定渠道参数
			lstpara.add(channelArr[i]);
		}
		// 将SQL语句补充完整
		sql.append(" group by query,channel,abstract,answer order by query");
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}

	public static Result getKnowledgeByStatus(String servicetype,
			String content, String serviceRoot, String sevicecontainer) {
		String sql = "";
		sql = "select service.serviceid,parentname,service.parentid,service.service from service,serviceorproductinfo where service.serviceid=serviceorproductinfo.serviceid and status=0 and parentname='模板业务' ";
		if (content != null && "".equals(content) ){
			sql += " and service like '%" + content + "%' ";
		}
		sql += " group by service.serviceid,parentname,service.parentid,service.service";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	public static Result getKnowledgeByStatus(String servicetype,
			String content, int start, int limit, String serviceRoot,
			String sevicecontainer) {
		String sql = "";
		sql = "select service.serviceid,parentname,service.parentid,service.service from service,serviceorproductinfo where service.serviceid=serviceorproductinfo.serviceid and status=0 and parentname='模板业务' ";
		if (content != null && "".equals(content) ){
			sql += " and service like '%" + content + "%' ";
		}
		sql += " group by service.serviceid,parentname,service.parentid,service.service";
		if (GetConfigValue.isOracle){
			sql = "select * from (select t.*, rownum  rn from (" + sql + ") t where rownum<=" + start + limit + " ) where rn>" + start;
		} else {
			int end = start + limit;
			sql = "select t.* from (" + sql + ") t limit " + start + "," + end;
		}
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
}
