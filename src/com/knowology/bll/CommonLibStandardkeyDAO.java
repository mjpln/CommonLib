package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.sql.Result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;


public class CommonLibStandardkeyDAO {
	/**
	 * 查询配置键
	 * 
	 * @param start参数开始条数
	 * @param limit参数每页条数
	 * @param standardkey参数配置键
	 * @param standardkeyprecise参数是否精确查询
	 * @param iscurrentmetafieldmapping参数是否当前配置名
	 * @param curmetafieldmapping参数配置名
	 * @return 返回json串
	 */
	public static Object select(int start, int limit, String standardkey,
			Boolean standardkeyprecise, Boolean iscurrentmetafieldmapping,
			String curmetafieldmapping) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义查询配置键的SQL语句
		String sql = "select * from metafield t,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.stdmetafieldid is null  ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前配置名
		if (iscurrentmetafieldmapping) {
			// 加上配置名条件
			paramSql.append(" and a.name=? ");
			// 绑定配置名参数
			lstpara.add(curmetafieldmapping);
		}
		// 判断配置键是否为null，空
		if (standardkey != null && !"".equals(standardkey)) {
			// 判断是否精确查询配置键
			if (standardkeyprecise) {
				// 加上精确查询配置键条件
				paramSql.append(" and t.name =? ");
				// 绑定配置键名称参数
				lstpara.add(standardkey);
			} else {
				// 加上模糊查询配置键条件
				paramSql.append(" and t.name like ? ");
				// 绑定配置键名称参数
				lstpara.add("%" + standardkey + "%");
			}
		}
		try {
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql + paramSql.toString(),
					lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + paramSql.toString() + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 将条数放入jsonObj的total对象中
				jsonObj.put("total", rs.getRowCount());
				// 带分页的查询满足条件的SQL语句
				if (GetConfigValue.isOracle) {
					sql = "select t2.* from(select t1.*, rownum rn from (select a.name,t.name metafield,t.metafieldid,t.metafieldmappingid from metafield t,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.stdmetafieldid is null "
						+ paramSql.toString()
						+ " order by t.metafieldid desc)t1)t2 where t2.rn>? and t2.rn<=? ";
					// 绑定开始条数参数
					lstpara.add(String.valueOf(start));
					// 绑定截止条数参数
					lstpara.add(String.valueOf(start + limit));
					GlobalValue.myLog.info("GHJ start="+start+"  and  start + limit="+start + limit);

//					if("true".equalsIgnoreCase(ResourceBundle
//							.getBundle("commonLibGlobal").getString("isToMysql")) ? true:false){
//						// 绑定开始条数参数
//						lstpara.add(start);
//						// 绑定截止条数参数
//						lstpara.add(limit);
//					}
//					else{
//						// 绑定开始条数参数
//						lstpara.add(String.valueOf(start));
//						// 绑定截止条数参数
//						lstpara.add(String.valueOf(start + limit));
//					}
				} else if(GetConfigValue.isMySQL) {
					sql = "select a.name as name,t.name as metafield,t.metafieldid,t.metafieldmappingid from metafield t,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.stdmetafieldid is null "
						  + paramSql.toString()
						  + " order by t.metafieldid desc limit ?,?";
					// 绑定开始条数参数
					lstpara.add(start);
					// 绑定截止条数参数
					lstpara.add(limit);
				}
				
				// 执行SQL语句，获取相应的数据源
				rs = Database.executeQuery(sql, lstpara.toArray());
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
				
				// 判断数据源不为null且含有数据
				if (rs != null && rs.getRowCount() > 0) {
					// 循环遍历数据源
					for (int i = 0; i < rs.getRowCount(); i++) {
						// 定义json对象
						JSONObject obj = new JSONObject();
						// 生成id对象
						obj.put("id", start + i + 1);
						// 生成metafieldmapping对象
						obj.put("metafieldmapping", rs.getRows()[i].get("name"));
						
						// 生成standardkey对象
						obj.put("standardkey", rs.getRows()[i].get("metafield"));
						
						// 生成metafieldid对象
						obj.put("metafieldid", rs.getRows()[i].get("metafieldid"));
						
						// 生成metafieldmappingid对象
						obj.put("metafieldmappingid", rs.getRows()[i].get("metafieldmappingid"));
						
						// 将生成的对象放入jsonArr数组中
						jsonArr.add(obj);
					}
				}
				// 将jsonArr数组放入jsonObj的root对象中
				jsonObj.put("root", jsonArr);
			} else {
				// 将0放入jsonObj的total对象中
				jsonObj.put("total", 0);
				// 清空jsonArr数组
				jsonArr.clear();
				// 将空的jsonArr数组放入jsonObj的root对象中
				jsonObj.put("root", jsonArr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 出现错误
			// 将0放入jsonObj的total对象中
			jsonObj.put("total", 0);
			// 清空jsonArr数组
			jsonArr.clear();
			// 将空的jsonArr数组放入jsonObj的root对象中
			jsonObj.put("root", jsonArr);
		}
		return jsonObj;
	}

	/**
	 * 判断配置键是否重复
	 * 
	 * @param metafieldmappingid参数配置名id
	 * @param newstandardkey参数配置键
	 * @return 是否重复
	 */
	public static Boolean ExistsKey(String metafieldmappingid,
			String newstandardkey) {
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 查询配置键是否重复的SQL语句
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select * from metafield t where  t.metafieldmappingid=? and t.name=? and t.stdmetafieldid is null and rownum<2  ";
		} else {
			sql = "select * from metafield t where t.metafieldmappingid=? and t.name=? and t.stdmetafieldid is null limit 0,1";
		}
		// 绑定配置键id参数
		lstpara.add(metafieldmappingid);
		// 绑定配置键参数
		lstpara.add(newstandardkey);
		try {
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 有重复配置键，返回true
				return true;
			} else {
				// 没有重复配置键，返回false
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 出现错误
			return false;
		}
	}

	/**
	 * 更新配置键的具体操作
	 * 
	 * @param oldstandardkey参数旧的配置键
	 * @param newstandardkey参数新的配置键
	 * @param metafieldid参数配置键id
	 * @return 更新返回的结果
	 */
	public static int update(User user,String oldstandardkey, String newstandardkey,
			String metafieldid) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 更新配置键的SQL语句
		String sql = "update metafield t set t.name=? where t.metafieldid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定配置键参数
		lstpara.add(newstandardkey);
		// 绑定配置键id参数
		lstpara.add(metafieldid);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstsql.add(GetConfigValue.LogSql());
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName()," ", " ", "更新配置键", oldstandardkey + "==>" + newstandardkey, "METAFIELD"));
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	/**
	 * 新增配置键的具体方法
	 * 
	 * @param curmetafieldmappingid参数配置名id
	 * @param curmetafieldmapping参数配置名
	 * @param lstStandardkey参数配置键集合
	 * @param logSql 日志sql
	 * @param lstlstparas 日志sql参数
	 * @return 新增返回的结果
	 */
	public static int insert(User user,String curmetafieldmappingid,
			String curmetafieldmapping, List<String> lstStandardkey) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义新增配置键的SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		//获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());
		// 循环遍历配置键集合
		for (int i = 0; i < lstStandardkey.size(); i++) {
			// 定义保存配置键的SQL语句
			sql = "insert into metafield(metafieldid,metafieldmappingid,name,type,operationtype) values(?,?,?,?,?) ";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 获取配置键表的序列值
			String id = "";
			if (GetConfigValue.isOracle) {
				id = (ConstructSerialNum.GetOracleNextVal("metafield_id_seq"))+"";
			} else {
				id = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
			}
			//根据配置信息补充需插入主键ID
			if(!"".equals(bussinessFlag)){
				id = id+"."+bussinessFlag;	
			}

			// 绑定id参数
			lstpara.add(id);
			// 绑定配置名id参数
			lstpara.add(curmetafieldmappingid);
			// 绑定配置键名称参数
			lstpara.add(lstStandardkey.get(i));
			// 绑定类型参数
			lstpara.add("标准键");
			// 绑定操作类型参数
			lstpara.add("A");
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName()," ", " ", "增加配置键",
						curmetafieldmapping + "==>" + lstStandardkey.get(i),
				"METAFIELD"));
		}
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	/**
	 * 删除配置键
	 * @param user 当前用户
	 * @param metafieldid参数配置键id
	 * @param curmetafieldmapping参数配置名
	 * @param standardkey参数配置键
	 * @return 删除返回的json串
	 */
	public static int delete(User user,String metafieldid, String curmetafieldmapping,
			String standardkey) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 删除配置键的SQL语句
		sql = "delete from metafield where metafieldid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定配置键id参数
		lstpara.add(metafieldid);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		// 删除配置值的SQL语句
		sql = "delete from metafield where stdmetafieldid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定配置键id参数
		lstpara.add(metafieldid);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstsql.add(GetConfigValue.LogSql());
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName()," ", " ", "删除配置键", curmetafieldmapping + "==>" + standardkey, "METAFIELD"));
		// 执行SQL语句，绑定事务处理，返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
	}
}
