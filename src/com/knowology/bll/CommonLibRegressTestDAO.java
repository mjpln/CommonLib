package com.knowology.bll;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLibRegressTestDAO {
	/**
	 * 分页查询满足条件的数据
	 * 
	 * @param question参数标准问题
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return json串
	 */
	public static Result selectQuery(String servicetype, String question, int page, int rows, int sqlno){
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		switch(sqlno){
		case 1:
			// 定义查询满足条件的数量的SQL语句
			sql.append("select count(*) count from regressquery where servicetype=? ");
			// 绑定servicetype参数
			lstpara.add(servicetype);
			// 判断标准问题是否为null，空
			if (question != null && !"".equals(question)) {
				// 加上标准问题的条件
				sql.append(" and question like ? ");
				// 绑定标准问题参数
				lstpara.add("%" + question + "%");
			}
			break;
		case 2:
			if(GetConfigValue.isMySQL){
				// 分页查询满足条件的SQL语句
				sql.append("SELECT * FROM (SELECT @rowno:=@rowno+1 AS rn, r.* FROM regressquery r,(SELECT @rowno:=0) t WHERE r.servicetype=? ");
				// 绑定servicetype参数
				lstpara.add(servicetype);
				// 判断标准问题是否为null，空
				if (question != null && !"".equals(question)) {
					// 加上标准问题的条件
					sql.append(" and r.question like ? ");
					// 绑定标准问题参数
					lstpara.add("%" + question + "%");
				}
				// 加上分页的条件
				sql.append(")t where t.rn>? and t.rn<=? ");
				// 绑定开始条数参数
				lstpara.add((page - 1) * rows);
				// 绑定截止条数参数
				lstpara.add(page * rows);
			}else{
				// 分页查询满足条件的SQL语句
				sql.append("select * from (select rownum rn,r.* from regressquery r where r.servicetype=? ");
				// 绑定servicetype参数
				lstpara.add(servicetype);
				// 判断标准问题是否为null，空
				if (question != null && !"".equals(question)) {
					// 加上标准问题的条件
					sql.append(" and r.question like ? ");
					// 绑定标准问题参数
					lstpara.add("%" + question + "%");
				}
				// 加上分页的条件
				sql.append(")t where t.rn>? and t.rn<=? ");
				// 绑定开始条数参数
				lstpara.add((page - 1) * rows);
				// 绑定截止条数参数
				lstpara.add(page * rows);
			}
			break;
		}
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}

	/**
	 * 删除回归问题
	 * 
	 * @param extendquestion参数扩展问题
	 * @param question参数标准问题
	 * @return 删除返回的json串
	 */
	public static int deleteRegress(String extendquestion, String question){
		// 删除回归问题的SQL语句
		return Database.executeNonQuery("delete from regressquery where question=? and extendquestion=?", question, extendquestion);
	}
	
	/**
	 * 分页查询满足条件的数据
	 * 
	 * @param starttime参数开始时间
	 * @param endtime参数结束时间
	 * @param question参数标准问题
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return json串
	 */
	public static Result selectQueryResult(String starttime, String endtime,
			String question, int page, int rows, int sqlno){
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		switch(sqlno){
		case 1:
			// 查询满足条件的数量
			sql.append("select count(*) count from regressqueryresult where 1=1 ");
			if(GetConfigValue.isMySQL){
				// 判断开始时间是否为null，空
				if (starttime != null && !"".equals(starttime)) {
					// 加上开始条件
					sql.append(" and time>=str_to_date(?,'%Y-%m-%d %H:%i:%s') ");
					// 绑定开始时间参数
					lstpara.add(starttime + " 00:00:00");
				}
				// 判断结束时间是否为null，空
				if (endtime != null && !"".equals(endtime)) {
					// 加上结束时间条件
					sql.append(" and time<=str_to_date(?,'%Y-%m-%d %H:%i:%s') ");
					// 加上结束时间参数
					lstpara.add(endtime + " 23:59:59");
				}
				// 判断标准问题是否为null，空
				if (question != null && !"".equals(question)) {
					// 加上标准问题条件
					sql.append(" and question like CONCAT('%',?,'%') ");
					// 绑定标准问题参数
					lstpara.add(question);
				}
			}else{
				// 判断开始时间是否为null，空
				if (starttime != null && !"".equals(starttime)) {
					// 加上开始条件
					sql.append(" and time>=to_date(?,'yyyy-MM-dd HH24:mi:ss') ");
					// 绑定开始时间参数
					lstpara.add(starttime + " 00:00:00");
				}
				// 判断结束时间是否为null，空
				if (endtime != null && !"".equals(endtime)) {
					// 加上结束时间条件
					sql.append(" and time<=to_date(?,'yyyy-MM-dd HH24:mi:ss') ");
					// 加上结束时间参数
					lstpara.add(endtime + " 23:59:59");
				}
				// 判断标准问题是否为null，空
				if (question != null && !"".equals(question)) {
					// 加上标准问题条件
					sql.append(" and question like '%'||?||'%' ");
					// 绑定标准问题参数
					lstpara.add(question);
				}
			}
			break;
		case 2:
			if(GetConfigValue.isMySQL){
				// 分页查询满足条件的数据
				// 定义SQL语句
				// 分页查询满足条件的SQL语句
				sql.append("select * from (SELECT @rowno:=@rowno+1 AS rn,t.* from (select r.question,r.extendquestion,r.systemresult,date_format(r.time,'%Y-%m-%d %H:%i:%s') time from regressqueryresult r where 1=1 ");
				// 判断开始时间是否为null，空
				if (starttime != null && !"".equals(starttime)) {
					// 加上开始条件
					sql.append(" and r.time>=str_to_date(?,'%Y-%m-%d %H:%i:%s') ");
					// 绑定开始时间参数
					lstpara.add(starttime + " 00:00:00");
				}
				// 判断结束时间是否为null，空
				if (endtime != null && !"".equals(endtime)) {
					// 加上结束时间条件
					sql.append(" and r.time<=str_to_date(?,'%Y-%m-%d %H:%i:%s') ");
					// 加上结束时间参数
					lstpara.add(endtime + " 23:59:59");
				}
				// 判断标准问题是否为null，空
				if (question != null && !"".equals(question)) {
					// 加上标准问题条件
					sql.append(" and r.question like concat('%',?,'%') ");
					// 绑定标准问题参数
					lstpara.add(question);
				}
				// 加上分页的条件
				sql.append(" order by r.time desc)t, (SELECT @rowno:=0) n)s where rn>? and rn<=? ");
				// 绑定开始条数参数
				lstpara.add((page - 1) * rows);
				// 绑定截止条数参数
				lstpara.add(page * rows);
			}else{
				// 分页查询满足条件的数据
				// 定义SQL语句
				// 分页查询满足条件的SQL语句
				sql.append("select * from (select rownum rn,t.* from (select r.question,r.extendquestion,r.systemresult,to_char(r.time,'yyyy-MM-dd HH24:mi:ss') time from regressqueryresult r where 1=1 ");
				// 判断开始时间是否为null，空
				if (starttime != null && !"".equals(starttime)) {
					// 加上开始条件
					sql.append(" and r.time>=to_date(?,'yyyy-MM-dd HH24:mi:ss') ");
					// 绑定开始时间参数
					lstpara.add(starttime + " 00:00:00");
				}
				// 判断结束时间是否为null，空
				if (endtime != null && !"".equals(endtime)) {
					// 加上结束时间条件
					sql.append(" and r.time<=to_date(?,'yyyy-MM-dd HH24:mi:ss') ");
					// 加上结束时间参数
					lstpara.add(endtime + " 23:59:59");
				}
				// 判断标准问题是否为null，空
				if (question != null && !"".equals(question)) {
					// 加上标准问题条件
					sql.append(" and r.question like '%'||?||'%' ");
					// 绑定标准问题参数
					lstpara.add(question);
				}
				// 加上分页的条件
				sql.append(" order by r.time desc)t)s where rn>? and rn<=? ");
				// 绑定开始条数参数
				lstpara.add((page - 1) * rows);
				// 绑定截止条数参数
				lstpara.add(page * rows);
			}
			break;
		}
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}
	
	/**
	 * 导入回归问题到数据库中
	 * 
	 * @param filename参数文件名称
	 * @return
	 */
	public static int importFile(List<List<String>> info, String servicetype){
		// 将数据新增到回归表中
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义删除当前四层结构的SQL语句
		String sql = "delete from regressquery where servicetype=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定servicetype参数
		lstpara.add(servicetype);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 对应新增回归测试的SQL语句
		sql = "insert into regressquery(question,extendquestion,servicetype) values (?,?,?)";
		
		// 循环遍历回归测试文件的数据
		for (int i = 0; i < info.size(); i++) {
			// 获取集合中第一个值
			String key = info.get(i).get(0);
			// 获取集合中第2个值
			String value = info.get(i).get(1);
			// 判断key是否为空
			if (!"".equals(key)) {
				// 对应绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定标准问题参数
				lstpara.add(key);
				// 绑定扩展问题参数
				lstpara.add(key);
				// 绑定servicetype参数
				lstpara.add(servicetype);
				// 将SQL语句放入集合中
				lstSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
			}
			// 判断value是否为空
			if (!"".equals(value)) {
				// 对应绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定标准问题参数
				lstpara.add(key);
				// 绑定扩展问题参数
				lstpara.add(value);
				// 绑定servicetype参数
				lstpara.add(servicetype);
				// 将SQL语句放入集合中
				lstSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
			}
			
		}
		// 执行SQL语句，绑定事务返回事务处理结果
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}
	
	/**
	 * 分析的错误结果记录到表regressqueryresult
	 * 
	 * @param lstLstRegress参数错误的回归问题集合
	 * @return 事务处理结果
	 */
	public static int insertRegressqueryresult(List<List<Object>> lstLstRegress) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 获取当前时间
		String time = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		// 定义删除回归测试的SQL语句
		String sql = "";
		if(GetConfigValue.isMySQL){
			sql = "delete from regressqueryresult where time>=str_to_date(?,'%Y-%m-%d %H:%i:%s') and time<=str_to_date(?,'%Y-%m-%d %H:%i:%s')";
		}else{
			sql = "delete from regressqueryresult where time>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and time<=to_date(?,'yyyy-mm-dd hh24:mi:ss')";
		}	
		// 绑定开始时间参数
		lstpara.add(time + " 00:00:00");
		// 绑定结束时间参数
		lstpara.add(time + " 23:59:59");
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 定义新增回归测试的SQL语句
		sql = "insert into regressqueryresult(question,extendquestion,systemresult,time) values(?,?,?,systimestamp)";
		// 循环遍历回归问题集合
		for (int i = 0; i < lstLstRegress.size(); i++) {
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定标准问题参数
			lstpara.add(lstLstRegress.get(i).get(0));
			// 绑定扩展问题参数
			lstpara.add(lstLstRegress.get(i).get(1));
			// 绑定测试后摘要的参数
			lstpara.add(lstLstRegress.get(i).get(2));
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}
	
	/**
	 * 查询所有回归问题
	 * 
	 * @param servicetype参数业务类型
	 * @return
	 */
	public static Result selectRegressquery(String servicetype) {
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery("select * from regressquery where servicetype=?", servicetype);
	}
	
	/**
	 * 删除当天的回归测试数据
	 */
	public static void DeleteRegressqueryresult(String nowTime){
		if(GetConfigValue.isMySQL)
			Database.executeNonQuery("delete from regressqueryresult where time>=str_to_date(?,'%Y-%m-%d %H:%i:%s') and time<=str_to_date(?,'%Y-%m-%d %H:%i:%s')", nowTime + " 00:00:00", nowTime + " 23:59:59");
		Database.executeNonQuery("delete from regressqueryresult where time>=to_date(?,'yyyy-MM-dd hh24:mi:ss') and time<=to_date(?,'yyyy-MM-dd hh24:mi:ss')", nowTime + " 00:00:00", nowTime + " 23:59:59");
	}
}
