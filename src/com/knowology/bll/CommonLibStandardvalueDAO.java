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

public class CommonLibStandardvalueDAO {
	/**
	 * 带分页的查询满足条件的别名名称
	 * 
	 * @param start参数开始条数
	 * @param limit参数每页条数
	 * @param standardvalue参数配置值
	 * @param isprecise参数是否精确查询
	 * @param iscurrentstandardkey参数是否当前配置键
	 * @param curstandardkey参数配置键
	 * @param curmetafieldmapping参数配置名
	 * @return 返回json串
	 */
	public static Object select(int start, int limit, String standardvalue,
			Boolean isprecise, Boolean iscurrentstandardkey,
			String curstandardkey, String curmetafieldmapping) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		try {
			// 查询满足条件的配置值的SQL语句
			String sql = "select * from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid ";
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 定义条件的SQL语句
			StringBuilder paramSql = new StringBuilder();
			// 判断是否是当前配置键且当前配置键名称不为null，空
			if (iscurrentstandardkey && !"".equals(curstandardkey)
					&& curstandardkey != null && curstandardkey.length() > 0) {
				// 加上配置键条件
				paramSql.append(" and t.name=? and a.name=? ");
				// 绑定配置键参数
				lstpara.add(curstandardkey);
				// 绑定配置名参数
				lstpara.add(curmetafieldmapping);
			}
			// 判断配置值是否为空，null
			if (!"".equals(standardvalue) && standardvalue != null
					&& standardvalue.length() > 0) {
				// 判断是否精确查询
				if (isprecise) {
					// 精确查询配置值
					paramSql.append(" and s.name=? ");
					// 绑定配置值名称参数
					lstpara.add(standardvalue);
				} else {
					// 模糊查询配置值
					paramSql.append(" and s.name like ? ");
					// 绑定配置值名称参数
					lstpara.add("%" + standardvalue + "%");
				}
			}
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
					// 执行带分页的查询满足条件的SQL语句
					sql = "select t2.* from(select t1.*, rownum rn from(select  t.name standardkey,s.name standardvalue,a.name metafieldmapping,s.metafieldid,s.stdmetafieldid from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid "
							+ paramSql
							+ " order by s.metafieldid desc)t1)t2 where t2.rn>? and t2.rn<=? ";
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
					// 执行带分页的查询满足条件的SQL语句
					sql = "select t.name standardkey,s.name standardvalue,a.name metafieldmapping,s.metafieldid,s.stdmetafieldid from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid "
							+ paramSql
							+ " order by s.metafieldid desc limit ?,? ";
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
						// 生成standardkey对象
						obj.put("standardkey", rs.getRows()[i]
								.get("standardkey"));
						// 生成standardvalue对象
						obj.put("standardvalue", rs.getRows()[i]
								.get("standardvalue"));
						// 生成metafieldmapping对象
						obj.put("metafieldmapping", rs.getRows()[i]
								.get("metafieldmapping"));
						// 生成metafieldid对象
						obj.put("metafieldid", rs.getRows()[i]
								.get("metafieldid"));
						// 生成stdmetafieldid对象
						obj.put("stdmetafieldid", rs.getRows()[i]
								.get("stdmetafieldid"));
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
	 * 判断配置值是否重复
	 * 
	 * @param stdmetafieldid参数配置值id
	 * @param standardvalue参数配置值名称
	 * @return 是否重复
	 */
	public static Boolean Exists(String stdmetafieldid, String standardvalue) {
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义查询配置值的SQL语句
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select name from metafield t where  t.name=? and t.stdmetafieldid=? and rownum<2 ";
		} else if(GetConfigValue.isMySQL){
			sql = "select name from metafield t where t.name=? and t.stdmetafieldid=? limit 0,1";
		}
		// 绑定配置值名称参数
		lstpara.add(standardvalue);
		// 绑定配置值id参数
		lstpara.add(stdmetafieldid);
		try {
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 有数据，表示重复
				return true;
			} else {
				// 没有数据，不是不重复
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// 出现错误
			return false;
		}
	}

	/**
	 * 新增配置值的具体方法
	 * 
	 * @param curmetafieldmappingid参数配置名id
	 * @param lstStandardvalue参数配置值集合
	 * @param stdmetafieldid参数配置键id
	 * @param curstandardkey参数配置键名称
	 * @param curmetafieldmapping参数配置名名称
	 * @return 新增返回的结果
	 */
	public static int insert(User user,String curmetafieldmappingid,
			List<String> lstStandardvalue, String stdmetafieldid,
			String curstandardkey, String curmetafieldmapping) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义保存配置值的SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		//获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());
		// 循环遍历配置值集合
		for (int i = 0; i < lstStandardvalue.size(); i++) {
			// 定义新增配置值的SQL语句
			sql = "insert into metafield(metafieldid,metafieldmappingid,name,stdmetafieldid,type,operationtype) values(?,?,?,?,?,?) ";
			// 获取配置值的序列值
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
			lstpara = new ArrayList<String>();
			// 绑定id参数
			lstpara.add(id);
			// 绑定配置名id参数
			lstpara.add(curmetafieldmappingid);
			// 绑定配置值参数
			lstpara.add(lstStandardvalue.get(i));
			// 绑定配置键id参数
			lstpara.add(stdmetafieldid);
			// 绑定类型参数
			lstpara.add("标准值");
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
					.getUserID(), user.getUserName()," ", " ", "增加配置值",
					curmetafieldmapping + "==>" + curstandardkey + "==>"
					+ lstStandardvalue.get(i), "METAFIELD"));
		}
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	/**
	 * 删除配置值
	 * @param user 当前用户
	 * @param stdmetafieldid参数配置值id
	 * @param standardvalue参数配置值名称
	 * @param curstandardkey参数配置键名称
	 * @param curmetafieldmapping参数配置名名称
	 * @return 删除返回的json串
	 */
	public static int delete(User user,String stdmetafieldid, String standardvalue,
			String curstandardkey, String curmetafieldmapping) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义删除配置值的SQL语句
		sql.append("delete from metafield where metafieldid in (");
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 将别名id按照逗号拆分
		String[] ids = stdmetafieldid.split(",");
		// 循环遍历id数组
		for (int i = 0; i < ids.length; i++) {
			if (i != ids.length - 1) {
				// 除了最后一个不加逗号，其他加上逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定参数集合
			lstpara.add(ids[i]);
		}
		// 将SQL语句放入集合中
		lstsql.add(sql.toString());
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		// 生成操作日志记录
		// 将配置值按照逗号拆分
		String[] standardvalues = standardvalue.split(",");
		// 循环遍历配置值数组
		for (int i = 0; i < standardvalues.length; i++) {
			// 将SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName()," ", " ", "删除配置值",
					curmetafieldmapping + "==>" + curstandardkey + "==>"
					+ standardvalues[i], "METAFIELD"));
		}
		// 执行SQL语句，绑定事务处理，返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
	}

	/**
	 * 更新配置值
	 * @param user 当前用户
	 * @param oldstandardvalue参数旧的配置值
	 * @param newstandardvalue参数新的配置值
	 * @param metafieldid参数配置值id
	 * @param stdmetafieldid参数配置键id
	 * @return 更新返回的json串
	 */
	public static int update(User user,String oldstandardvalue,
			String newstandardvalue, String metafieldid, String stdmetafieldid) {
		
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 更新配置值的SQL语句
		String sql = "update metafield t set t.name=? where t.metafieldid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定配置值名称参数
		lstpara.add(newstandardvalue);
		// 绑定配置值id
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
				.getUserID(), user.getUserName()," ", " ", "更新配置值", oldstandardvalue + "==>" + newstandardvalue, "METAFIELD"));

		// 执行SQL语句，绑定事务处理，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
	}
}
