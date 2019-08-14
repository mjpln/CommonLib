package com.knowology.bll;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.sql.Result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;


public class CommonLibMetafieldmappingDAO {
	/**
	 * 根据条件分页查询配置名
	 * 
	 * @param metafieldmapping参数配置名
	 * @param start参数起始条数
	 * @param limit参数每页条数
	 * @param show配置名是否展示标识
	 * @return json字符串
	 */
	public static Object select(String metafieldmapping, int start, int limit,String show) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义查询满足条件的总条数的SQL语句
		String sql = "select * from metafieldmapping where 1>0 ";
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断配置名条件是否为空，null
		if (!"".equals(metafieldmapping) && metafieldmapping != null
				&& metafieldmapping.length() > 0) {
			// 加上配置名条件
			paramSql.append(" and name like ? ");
			// 绑定配置名参数
			lstpara.add("%" + metafieldmapping + "%");
		}
		// 判断配置名是否展示
		if (!"".equals(show) && show != null) {
			// 加上配置名条件
			paramSql.append(" and isshow ='是' ");
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
				if (GetConfigValue.isOracle) {
					// 带分页的查询满足条件的SQL语句
					sql = "select t2.* from(select t1.*,rownum rn from (select * from metafieldmapping where 1>0 "
						+ paramSql
						+ " order by metafieldmappingid desc)t1)t2 where t2.rn>? and t2.rn<=?";
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
				} else if(GetConfigValue.isMySQL){
					sql = "select * from metafieldmapping where 1>0 " + paramSql + " order by metafieldmappingid desc limit ?,?";
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
						obj
								.put("metafieldmapping", rs.getRows()[i]
										.get("name"));
						// 生成metafieldmappingid对象
						obj.put("metafieldmappingid", rs.getRows()[i]
								.get("metafieldmappingid"));
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
				// 将空数组放入jsonObj的root对象中
				jsonObj.put("root", jsonArr);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 出现错误
			// 将0放入jsonObj的total对象中
			jsonObj.put("total", 0);
			// 清空jsonArr数组
			jsonArr.clear();
			// 将空数组放入jsonObj的root对象中
			jsonObj.put("root", jsonArr);
		}
		return jsonObj;
	}

	/**
	 * 
	 *描述：根据配置名返回配置信息
	 *@author: qianlei
	 *@date： 日期：2015-11-24 时间：下午04:39:05
	 *@param metafieldname
	 *@return Result
	 */
	public static Result select(String metafieldname)
	{
		try{
			String sql = "select mf1.name mkey,mf2.name mvalue from metafield mf1,metafield mf2 where mf1.metafieldid = mf2.stdmetafieldid and mf1.metafieldmappingid =(select metafieldmappingid from metafieldmapping where name='"
				+ metafieldname + "')";
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			return Database.executeQuery(sql);
		}catch(Exception e)
		{
			GlobalValue.myLog.error(e.toString());
			return null;
		}
	}
	
	/**
	 * 更新配置名的具体操作
	 * @param user 用户
	 * @param metafieldmappingid参数配置名id
	 * @param oldvalue参数旧的配置名
	 * @param newvalue参数新的配置名
	 * @return 更新返回值
	 */
	public static int update(User user,String metafieldmappingid, String oldvalue,
			String newvalue) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义更新配置名的SQL语句
		String sql = "update metafieldmapping t set t.name=? where t.metafieldmappingid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定新的配置名参数
		lstpara.add(newvalue);
		// 绑定配置名id参数
		lstpara.add(metafieldmappingid);
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
				.getUserID(), user.getUserName()," ", " ", "更新配置名",
				oldvalue+ "==>" + newvalue, "METAFIELDMAPPING"));
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}


	/**
	 * 更新配置名的具体操作
	 * @param metafieldmappingid参数配置名id
	 * @param newvalue参数新的配置名
	 * @return 更新返回值
	 */
	public static int update(String metafieldid, String newvalue) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义更新配置名的SQL语句
		String sql = "update metafield t set t.name=? where t.metafieldid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定新的配置名参数
		lstpara.add(newvalue);
		// 绑定配置名id参数
		lstpara.add(metafieldid);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}
	
	/**
	 * 判断配置名是否重复
	 * 
	 * @param metafieldmapping参数配置名
	 * @return 是否重复
	 */
	public static Boolean Exists(String metafieldmapping) {
		// 查询配置名的SQL语句
		String sql = "select * from metafieldmapping where name=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定配置名参数
		lstpara.add(metafieldmapping);
		try {
			// 执行SQL语句，返回数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 有重复配置名，返回true
				return true;
			} else {
				// 没有重复配置名，返回false
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 出现错误
			return false;
		}
	}

	/**
	 * 配置名添加具体方法
	 * @param user 当前用户
	 * @param lstName参数配置名集合
	 * @return 新增返回的结果
	 */
	public static int insert(User user,List<String> lstName) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		//获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());
		// 循环遍历配置名集合
		for (int i = 0; i < lstName.size(); i++) {
			// 插入配置名的SQL语句
			sql = "insert into metafieldmapping(metafieldmappingid,name) values(?,?) ";
			// 获取配置名表的序列值
			String id = "";
			if (GetConfigValue.isOracle) {
				id = ConstructSerialNum.GetOracleNextVal("metafieldmapping_id_seq")+"";
			} else if(GetConfigValue.isMySQL){
				id = ConstructSerialNum.getSerialID("metafieldmapping", "metafieldmappingid")+"";
			}
			
			//根据配置信息补充需插入主键ID
			if(!"".equals(bussinessFlag)){
				id = id+"."+bussinessFlag;	
			}
			// 绑定id参数
			lstpara.add(id);
			// 绑定配置名参数
			lstpara.add(lstName.get(i));
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
					.getUserID(), user.getUserName()," ", " ", "增加配置名",lstName.get(i), "METAFIELDMAPPING"));
		}
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	/**
	 * 删除配置名
	 * 
	 * @param metafieldmappingid参数配置名id
	 * @param metafieldmapping参数配置名
	 * @return 删除返回的json串
	 */
	public static int delete(User user,String metafieldmappingid,
			String metafieldmapping) {
		
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句集合对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 删除配置名的SQL语句
		String sql = "delete from metafieldmapping where metafieldmappingid = ?  ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定配置名id参数
		lstpara.add(metafieldmappingid);
		// 将SQL语句存入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合存入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		// 生成操作日志记录
		// 将SQL语句存入集合中
		lstsql.add(GetConfigValue.LogSql());
		// 将对应的绑定参数集合存入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName()," ", " ", "删除配置名",metafieldmapping, "METAFIELDMAPPING"));
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
	}

	   /**
	 *@description 获得参数配置表key对应value值
	 *@param name  配置参数名
	 *@param key   配置参数名对应key
	 *@return 
	 *@returnType Result 
	 */
	public static Result getConfigValue(String name ,String key){
	    List<Object> lstpara = new ArrayList<Object>();
		String sql ="select  s.metafieldid,s.name from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name =? and t.name =?  order by s.metafieldid ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		lstpara.add(name);
		lstpara.add(key);
		Result rs = null;
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	   
	}
	
	   /**
	 *@description 获得参数配置表value对应key值
	 *@param name  配置参数名
	 *@param key   配置参数名对应value
	 *@return 
	 *@returnType Result 
	 */
	public static Result getConfigKey(String name ,String value){
	    List<Object> lstpara = new ArrayList<Object>();
		String sql ="select t.name from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name =? and s.name  like ?  order by s.metafieldid ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		lstpara.add(name);
		lstpara.add("%"+value+"%");
		Result rs = null;
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	   /**
	 *@description 获得参数配置表对应key值
	 *@param name  配置参数名
	 *@return 
	 *@returnType Result 
	 */
	public static Result getConfigKey(String name ){
	    List<Object> lstpara = new ArrayList<Object>();
		String sql ="select s.name k , t.name name from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name =? and t.stdmetafieldid is null  order by s.metafieldid ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		lstpara.add(name);
		Result rs = null;
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		return rs;
	}
	
	/**
	 *@description 获得参数配置表对应key值
	 *@param name  配置参数名
	 *@return 
	 *@returnType Result 
	 */
	public static Result getConfigKey2(String name){
	    List<Object> lstpara = new ArrayList<Object>();
		String sql ="SELECT s.name FROM  metafield s,  metafieldmapping a WHERE s.metafieldmappingid=a.metafieldmappingid AND a.name =? and s.STDMETAFIELDID is null ORDER BY s.metafieldid ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		lstpara.add(name);
		Result rs = null;
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 *@description 获得参数配置表对应key值
	 *@param name  配置参数名
	 *@return 
	 *@returnType Result 
	 */
	public static Result getConfigKeyId(String name){
	    List<Object> lstpara = new ArrayList<Object>();
		String sql ="SELECT  s.metafieldid id,s.name name FROM  metafield s,  metafieldmapping a WHERE s.metafieldmappingid=a.metafieldmappingid AND a.name =? and s.STDMETAFIELDID is null ORDER BY s.metafieldid ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		lstpara.add(name);
		Result rs = null;
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	   /**
	 *@description 获得参数配置表对应max value
	 *@param name  配置参数名
	 *@return 
	 *@returnType Result 
	 */
	public static Result getConfigMaxValue(String name ){
	    List<Object> lstpara = new ArrayList<Object>();
		String sql ="select max(s.name) k , t.name name from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name =? and t.stdmetafieldid is null  group by t.name ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		lstpara.add(name);
		Result rs = null;
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	   /**
	 *@description 获得参数配置表对应min value
	 *@param name  配置参数名
	 *@return 
	 *@returnType Result 
	 */
	public static Result getConfigMinValue(String name ){
	    List<Object> lstpara = new ArrayList<Object>();
		String sql ="select min(s.name) k , t.name name from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name =? and t.stdmetafieldid is null  group by t.name ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		lstpara.add(name);
		Result rs = null;
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	  /**
	 *@description 获得参数配置表对应 key value
	 *@param name  配置参数名
	 *@return 
	 *@returnType Result 
	 */
	public static Result getConfigKeyValue(String name ){
	    List<Object> lstpara = new ArrayList<Object>();
		String sql ="select s.name k , t.name name from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name =? and t.stdmetafieldid is null ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		lstpara.add(name);
		Result rs = null;
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 *@description 获得商家标识符    内容如：默认值=1&&是否使用=是
	 *@param serviceType 服务
	 *@return  (businessflag ="" 不启用商家标识符) (businessflag !="" 启用商家标识符) = 商家标识符具体值
	 *@returnType int 
	 */
	public static String getBussinessFlag(String serviceType){
       String businessflag = "";
		Result flagrs = CommonLibMetafieldmappingDAO.getConfigValue("商家标识自定义配置",serviceType);
		if(flagrs!=null && flagrs.getRowCount()>0){
			String flagStr = flagrs.getRows()[0].get("name") != null ? flagrs.getRows()[0].get("name").toString(): "";
			if(!"".equals(flagStr)){
				String arry [] = flagStr.split("&&");
				String status =arry[1].split("=")[1];
				if("是".equals(status)){
					businessflag = arry[0].split("=")[1];
				}
			}
		}
		return businessflag;
	}
	
	
	/**
	 * 判断 token 是否过期
	 * 
	 * @param userid 用户ID
	 * @return 
	 */
	public static Boolean isTokenOver(String userid) {
		// 查询配置名的SQL语句
		String sql = "select * from singleloginatinterface  where uselesstime >sysdate and telephonenumber =? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定配置名参数
		lstpara.add(userid);
		try {
			// 执行SQL语句，返回数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 出现错误
			return false;
		}
	}
	
	/**
	 * 获取token
	 * 
	 * @param userid 用户ID
	 * @return 
	 */
	public static Result getToken(String userid) {
		// 查询配置名的SQL语句
		String sql = "select * from singleloginatinterface  where uselesstime >sysdate and telephonenumber =?";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定配置名参数
		lstpara.add(userid);
		try {
			// 执行SQL语句，返回数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			return rs;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * 插入token
	 * @param userid 前用户ID
	 * @param token 唯一标识
	 * @return 新增返回的结果
	 */
	public static int insertToken(String userid ,String token) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
			// 插入配置名的SQL语句
			sql = "insert into singleloginatinterface (telephonenumber,token,uselesstime) values (?,?,sysdate+10/24)";
			// 绑定id参数
			lstpara.add(userid);
			// 绑定配置名参数
			lstpara.add(token);
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	public static int InsertMetafield(User user, Map<String, String> metafield01Map,
			Map<String, String> metafield02Map, Map<String, String> metafield03Map,
			List<Object> siteIDList, List<Object> connectTypeList,
			List<Object> provinceList, List<Object> cityList,
			List<Object> skuList, List<Object> remarkList,
			Map<String, String> provinceInfoMap,
			Map<String, String> cityInfoMap, Map<String, String> skuInfoMap) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 四层结构
		String serviceType = user.getIndustryOrganizationApplication();
		//获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		int total = 0;
		
		for (int i = 0 ; i < siteIDList.size() ; i++){
			String siteID = siteIDList.get(i) == null ? "" : siteIDList.get(i).toString();
			String remark = remarkList.get(i) == null ? "" : remarkList.get(i).toString();
			String province = provinceList.get(i) == null ? "" : provinceList.get(i).toString();
			String city = cityList.get(i) == null ? "" : cityList.get(i).toString();
			String sku = skuList.get(i) == null ? "" : skuList.get(i).toString();
			String connectType = connectTypeList.get(i) == null ? "" : connectTypeList.get(i).toString();
			if (!"".equals(siteID)){
				total++;
				// 站点策略配置
				if(metafield01Map.containsKey(siteID)){// 该键值存在
					lstpara = new ArrayList<String>();
					sql = "delete from metafield where stdmetafieldid = ?";
					// 绑定stdmetafieldid参数
					lstpara.add(metafield01Map.get(siteID));
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					// 插入配置名的SQL语句
					sql = "insert into metafield (metafieldid,stdmetafieldid,name,type,time,metafieldmappingid,operationtype) values(?,?,?,?,sysdate,(select metafieldmappingid from metafieldmapping where name='站点策略配置'),'A') ";
					lstpara = new ArrayList<String>();
					// 获取配置名表的序列值
					String metafieldid = "";
					if (GetConfigValue.isOracle) {
						metafieldid = ConstructSerialNum.GetOracleNextVal("metafield_id_seq")+"";
					} else if(GetConfigValue.isMySQL){
						metafieldid = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
					}
					
					//根据配置信息补充需插入主键ID
					if(!"".equals(bussinessFlag)){
						metafieldid = metafieldid+"."+bussinessFlag;	
					}
					// 绑定id参数
					lstpara.add(metafieldid);
					// 绑定stdmetafieldid参数
					lstpara.add(metafield01Map.get(siteID));
					// 绑定配置名参数
					lstpara.add(remark);
					// 绑定type参数
					lstpara.add("标准值");
					
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
				} else {// 该键不存在
					// 插入配置名的SQL语句
					sql = "insert into metafield (metafieldid,name,type,time,metafieldmappingid,operationtype) values(?,?,?,sysdate,(select metafieldmappingid from metafieldmapping where name='站点策略配置'),'A') ";
					lstpara = new ArrayList<String>();
					// 获取配置名表的序列值
					String metafieldid = "";
					if (GetConfigValue.isOracle) {
						metafieldid = ConstructSerialNum.GetOracleNextVal("metafield_id_seq")+"";
					} else if(GetConfigValue.isMySQL){
						metafieldid = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
					}
					
					//根据配置信息补充需插入主键ID
					if(!"".equals(bussinessFlag)){
						metafieldid = metafieldid+"."+bussinessFlag;	
					}
					// 绑定id参数
					lstpara.add(metafieldid);
					// 绑定配置名参数
					lstpara.add(siteID);
					// 绑定type参数
					lstpara.add("标准键");
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					
					metafield01Map.put(siteID, metafieldid);
					
					// 插入配置名的SQL语句
					sql = "insert into metafield (metafieldid,stdmetafieldid,name,type,time,metafieldmappingid,operationtype) values(?,?,?,?,sysdate,(select metafieldmappingid from metafieldmapping where name='站点策略配置'),'A') ";
					lstpara = new ArrayList<String>();
					// 获取配置名表的序列值
					metafieldid = "";
					if (GetConfigValue.isOracle) {
						metafieldid = ConstructSerialNum.GetOracleNextVal("metafield_id_seq")+"";
					} else if(GetConfigValue.isMySQL){
						metafieldid = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
					}
					
					//根据配置信息补充需插入主键ID
					if(!"".equals(bussinessFlag)){
						metafieldid = metafieldid+"."+bussinessFlag;	
					}
					// 绑定id参数
					lstpara.add(metafieldid);
					// 绑定stdmetafieldid参数
					lstpara.add(metafield01Map.get(siteID));
					// 绑定配置名参数
					lstpara.add(remark);
					// 绑定type参数
					lstpara.add("标准值");
					
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
				}
			
				// 新天网接口参数配置 
				if(metafield02Map.containsKey(siteID)){// 该键值存在
					lstpara = new ArrayList<String>();
					sql = "delete from metafield where stdmetafieldid = ?";
					// 绑定stdmetafieldid参数
					lstpara.add(metafield02Map.get(siteID));
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					// 插入配置名的SQL语句
					sql = "insert into metafield (metafieldid,stdmetafieldid,name,type,time,metafieldmappingid,operationtype) values(?,?,?,?,sysdate,(select metafieldmappingid from metafieldmapping where name='新天网接口参数配置'),'A') ";
					lstpara = new ArrayList<String>();
					// 获取配置名表的序列值
					String metafieldid = "";
					if (GetConfigValue.isOracle) {
						metafieldid = ConstructSerialNum.GetOracleNextVal("metafield_id_seq")+"";
					} else if(GetConfigValue.isMySQL){
						metafieldid = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
					}
					
					//根据配置信息补充需插入主键ID
					if(!"".equals(bussinessFlag)){
						metafieldid = metafieldid+"."+bussinessFlag;	
					}
					// 绑定id参数
					lstpara.add(metafieldid);
					// 绑定stdmetafieldid参数
					lstpara.add(metafield02Map.get(siteID));
					// 绑定配置名参数
					lstpara.add(province + "#" + provinceInfoMap.get(province) 
							+ "#" + city + "#" + cityInfoMap.get(city)
							+ "#" + sku + "#" + skuInfoMap.get(sku)
							+ "#ONLINE_CS_ROBOT");
					// 绑定type参数
					lstpara.add("标准值");
					
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
				} else {// 该键不存在
					// 插入配置名的SQL语句
					sql = "insert into metafield (metafieldid,name,type,time,metafieldmappingid,operationtype) values(?,?,?,sysdate,(select metafieldmappingid from metafieldmapping where name='新天网接口参数配置'),'A') ";
					lstpara = new ArrayList<String>();
					// 获取配置名表的序列值
					String metafieldid = "";
					if (GetConfigValue.isOracle) {
						metafieldid = ConstructSerialNum.GetOracleNextVal("metafield_id_seq")+"";
					} else if(GetConfigValue.isMySQL){
						metafieldid = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
					}
					
					//根据配置信息补充需插入主键ID
					if(!"".equals(bussinessFlag)){
						metafieldid = metafieldid+"."+bussinessFlag;	
					}
					// 绑定id参数
					lstpara.add(metafieldid);
					// 绑定配置名参数
					lstpara.add(siteID);
					// 绑定type参数
					lstpara.add("标准键");
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					
					metafield02Map.put(siteID, metafieldid);
					
					// 插入配置名的SQL语句
					sql = "insert into metafield (metafieldid,stdmetafieldid,name,type,time,metafieldmappingid,operationtype) values(?,?,?,?,sysdate,(select metafieldmappingid from metafieldmapping where name='新天网接口参数配置'),'A') ";
					lstpara = new ArrayList<String>();
					// 获取配置名表的序列值
					metafieldid = "";
					if (GetConfigValue.isOracle) {
						metafieldid = ConstructSerialNum.GetOracleNextVal("metafield_id_seq")+"";
					} else if(GetConfigValue.isMySQL){
						metafieldid = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
					}
					
					//根据配置信息补充需插入主键ID
					if(!"".equals(bussinessFlag)){
						metafieldid = metafieldid+"."+bussinessFlag;	
					}
					// 绑定id参数
					lstpara.add(metafieldid);
					// 绑定stdmetafieldid参数
					lstpara.add(metafield02Map.get(siteID));
					// 绑定配置名参数
					lstpara.add(province + "#" + provinceInfoMap.get(province) 
							+ "#" + city + "#" + cityInfoMap.get(city)
							+ "#" + sku + "#" + skuInfoMap.get(sku)
							+ "#ONLINE_CS_ROBOT");
					// 绑定type参数
					lstpara.add("标准值");
					
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
				}
				// 站点维度配置
				JSONArray valueJsonArray = new JSONArray();
				JSONObject valueJsonObj = new JSONObject();
	
				valueJsonObj.put("name", "city");
				valueJsonObj.put("value", province);
				valueJsonArray.add(valueJsonObj);
				
				valueJsonObj = new JSONObject();
				valueJsonObj.put("name", "channel");
				valueJsonObj.put("value", "");
				valueJsonArray.add(valueJsonObj);
				
				valueJsonObj = new JSONObject();
				valueJsonObj.put("name", "模式11");
				valueJsonObj.put("value", connectType);
				valueJsonArray.add(valueJsonObj);
				
				valueJsonObj = new JSONObject();
				valueJsonObj.put("name", "SKU11");
				valueJsonObj.put("value", sku);
				valueJsonArray.add(valueJsonObj);
				
				valueJsonObj = new JSONObject();
				valueJsonObj.put("name", "技能组11");
				valueJsonObj.put("value", "武汉机器人测试一组");
				valueJsonArray.add(valueJsonObj);
				
				valueJsonObj = new JSONObject();
				valueJsonObj.put("name", "日期11");
				Date dNow = new Date( );
			    SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd");
				valueJsonObj.put("value", ft.format(dNow));
				valueJsonArray.add(valueJsonObj);
				
				if(metafield03Map.containsKey(siteID)){// 该键值存在
					lstpara = new ArrayList<String>();
					sql = "delete from metafield where stdmetafieldid = ?";
					// 绑定stdmetafieldid参数
					lstpara.add(metafield03Map.get(siteID));
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					// 插入配置名的SQL语句
					sql = "insert into metafield (metafieldid,stdmetafieldid,name,type,time,metafieldmappingid,operationtype) values(?,?,?,?,sysdate,(select metafieldmappingid from metafieldmapping where name='站点维度配置'),'A') ";
					lstpara = new ArrayList<String>();
					// 获取配置名表的序列值
					String metafieldid = "";
					if (GetConfigValue.isOracle) {
						metafieldid = ConstructSerialNum.GetOracleNextVal("metafield_id_seq")+"";
					} else if(GetConfigValue.isMySQL){
						metafieldid = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
					}
					
					//根据配置信息补充需插入主键ID
					if(!"".equals(bussinessFlag)){
						metafieldid = metafieldid+"."+bussinessFlag;	
					}
					// 绑定id参数
					lstpara.add(metafieldid);
					// 绑定stdmetafieldid参数
					lstpara.add(metafield03Map.get(siteID));
					// 绑定配置名参数
					
					
					
					lstpara.add(valueJsonArray.toString());
					// 绑定type参数
					lstpara.add("标准值");
					
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
				} else {// 该键不存在
					// 插入配置名的SQL语句
					sql = "insert into metafield (metafieldid,name,type,time,metafieldmappingid,operationtype) values(?,?,?,sysdate,(select metafieldmappingid from metafieldmapping where name='站点维度配置'),'A') ";
					lstpara = new ArrayList<String>();
					// 获取配置名表的序列值
					String metafieldid = "";
					if (GetConfigValue.isOracle) {
						metafieldid = ConstructSerialNum.GetOracleNextVal("metafield_id_seq")+"";
					} else if(GetConfigValue.isMySQL){
						metafieldid = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
					}
					
					//根据配置信息补充需插入主键ID
					if(!"".equals(bussinessFlag)){
						metafieldid = metafieldid+"."+bussinessFlag;	
					}
					// 绑定id参数
					lstpara.add(metafieldid);
					// 绑定配置名参数
					lstpara.add(siteID);
					// 绑定type参数
					lstpara.add("标准键");
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					
					metafield03Map.put(siteID, metafieldid);
					
					// 插入配置名的SQL语句
					sql = "insert into metafield (metafieldid,stdmetafieldid,name,type,time,metafieldmappingid,operationtype) values(?,?,?,?,sysdate,(select metafieldmappingid from metafieldmapping where name='站点维度配置'),'A') ";
					lstpara = new ArrayList<String>();
					// 获取配置名表的序列值
					metafieldid = "";
					if (GetConfigValue.isOracle) {
						metafieldid = ConstructSerialNum.GetOracleNextVal("metafield_id_seq")+"";
					} else if(GetConfigValue.isMySQL){
						metafieldid = ConstructSerialNum.getSerialID("metafield", "metafieldid")+"";
					}
					
					//根据配置信息补充需插入主键ID
					if(!"".equals(bussinessFlag)){
						metafieldid = metafieldid+"."+bussinessFlag;	
					}
					// 绑定id参数
					lstpara.add(metafieldid);
					// 绑定stdmetafieldid参数
					lstpara.add(metafield03Map.get(siteID));
					// 绑定配置名参数
					lstpara.add(valueJsonArray.toString());
					// 绑定type参数
					lstpara.add("标准值");
					
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
				}
			}
		}
		int count = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		System.out.println("count&&total="+count+"&&"+total);
		if (count > 0){
			return total;
		}
		return 0;
	}
}
