package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLibTopicDAO {
	/**
	 * 查询当前业务下的所有的主题
	 * 
	 * @param lstserviceid参数业务id集合
	 * @return 数据源
	 */
	public static  Result  GetTopicByServiceid(String serviceid){
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义查询主题的SQL语句
		sql.append("select distinct topic  from kbdata  where abstract not like '%(删除标识符近类)' and  serviceid = ?");
	    // 绑定参数变量
		lstpara.add(serviceid);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}
	
	/**
	 *@description  查询配置表当前行业下的所有的主题
	 *@param servicetype 行业标识
	 *@return 
	 *@returnType Result 
	 */
	public static Result GetTopicConfig(String servicetype){
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 查询所有主题SQL语句
		String sql = "select distinct s.name from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name=? and t.name in (?,?)";
		// 绑定配置主题的配置名参数
		lstpara.add("行业商家知识类别名称配置");
		// 绑定登录时的商家组织应用配置键参数
		lstpara.add(servicetype);
		// 绑定基础配置键参数
		lstpara.add("基础");
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}
	
	/**
	 * 判断主题是否存在
	 * 
	 * @param topic参数主题
	 * @return 是否存在
	 */
	public static boolean IsTopicHere(String topic) {
		// 定义查询主题的SQL语句
		String sql = "select * from topic where topic=?";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定主题参数
		lstpara.add(topic);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 该主题已存在
			return true;
		} else {
			// 该主题不存在
			return false;
		}
	}

	/**
	 * 添加知识点
	 * 
	 * @param topic参数主题
	 * @param service参数业务名称
	 * @param brand参数品牌
	 * @param citys参数地市名称
	 * @return 是否选择成功
	 */
	public static boolean AddTopic(String userip, String userid,
			String username, String topic, String service, String brand) {
		String id = null;
		if (GetConfigValue.isMySQL) {
			id = ConstructSerialNum.GetOracleNextVal("SEQ_TOPICID") + "";
		} else if (GetConfigValue.isOracle) {
			id = ConstructSerialNum.getSerialID("topic", "topicid") + "";
		}
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义新增知识点的SQL语句
		String sql = "insert into topic(topicid,topic) values (?,?)";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 获取主题的表的序列值，并绑定参数
		lstpara.add(id);
		// 绑定知识点参数
		lstpara.add(topic);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 加上对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		// 加上对应的绑定参数集合放入集合中
		lstLstpara.add(GetConfigValue.LogParam(userip, userid, username, brand,
				service, "增加主题", topic, "TOPIC"));
		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		// 判断事务处理结果
		if (c > 0) {
			// 事务处理成功，返回true，表示新增成功
			return true;
		} else {
			// 事务处理失败，返回false，表示新增失败
			return false;
		}
	}
	
	/**
	 * 判断该知识点是否正在使用
	 * 
	 * @param topic
	 * @return
	 */
	public static boolean IsTopicUsed(String topic) {
		// 查询知识点语句
		String sql = "select * from kbdata where topic=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定主题参数
		lstpara.add(topic);
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 正在使用
				return true;
			} else {
				// 不使用
				return false;
			}
		
	}
	
	/**
	 * 删除主题
	 * 
	 * @param topic参数主题
	 * @param service参数业务名称
	 * @param brand参数品牌
	 * @param citys参数地市名称
	 * @return 是否成功
	 */
	public static boolean DeleteTopic(String userip, String userid,
			String username,String topic, String service,
			String brand, String citys) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义删除知识点的SQL语句
		String sql = "delete from topic where topic=?";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定主题参数
		lstpara.add(topic);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 加上对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		// 加上对应的绑定参数集合放入集合中
		lstLstpara.add(GetConfigValue.LogParam(userip,userid,username,brand, service, "删除主题", topic,
				"TOPIC"));
		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		// 判断事务处理结果
		if (c > 0) {
			// 事务处理成功，返回true，表示删除成功
			return true;
		} else {
			// 事务处理失败，返回false，表示删除失败
			return false;
		}
	}
	


}
