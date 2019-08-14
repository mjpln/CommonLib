package com.knowology.bll;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

public class CommonLibOperationlogDAO {
	/**
	 * 获取操作对象
	 * 
	 * @return
	 */
	public static Result table(String sql) {

		Result rs = null;
		rs = Database.executeQuery(sql);

		// 文件日志
		GlobalValue.myLog.info(sql);

		return rs;
	}

	/**
	 * 分页查询满足条件的日志信息
	 * 
	 * @param start参数开始条数
	 * @param limit参数每页条数
	 * @param starttime参数开始时间
	 * @param endtime参数截止时间
	 * @param workername参数操作人
	 * @param queryname参数是否精确查询操作人
	 * @param operation参数操作类型
	 * @param table参数操作对象
	 * @param object参数操作内容
	 * @param queryobject参数是否精确查询操作内容
	 * @return json串
	 */
	public static Object select(int start, int limit, String starttime,
			String endtime, String workername, String queryname,
			String operation, String table, String object, String queryobject) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义条件的SQL语句
		StringBuilder sbpart = new StringBuilder();
		//特殊处理排除非集团行业用户操作记录
		sbpart.append(" and workerid not in (select s.name workerid from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name ='用户操作日志配置' and t.name ='workerid') ");
		// 判断开始时间是否为null，空
		if (starttime != "" && starttime != null && starttime.length() > 0) {
			// 加上开始时间条件
			if (GetConfigValue.isOracle) {
				sbpart
						.append(" and time>=to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
			} else if (GetConfigValue.isMySQL) {
//				starttime += " 00:00:00";
				sbpart.append(" and time>=DATE_FORMAT(?,'%Y-%m-%d %H:%i:%s') ");
			}

			// 绑定开始时间参数
			lstpara.add(starttime.replace("/", "-") + " 00:00:00");
		}
		// 判断截止时间是否为null，空
		if (endtime != "" && endtime != null && endtime.length() > 0) {
			// 加上截止时间条件
			if (GetConfigValue.isOracle) {
				sbpart.append(" and time<=to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
			} else if (GetConfigValue.isMySQL) {
//				endtime += " 23:59:59";
				sbpart.append(" and time<=DATE_FORMAT(?,'%Y-%m-%d %H:%i:%s') ");
			}

			// 绑定截止时间参数
			lstpara.add(endtime.replace("/", "-") + " 23:59:59");
		}
		// 判断操作人是否为null，空
		if (workername != "" && workername != null && workername.length() > 0) {
			// 判断是否模糊查询操作人
			if ("模糊".equals(queryname)) {
				// 模糊查询操作人的SQL语句
				sbpart.append(" and workerid like '%"
						+ workername.replaceAll(" ", "") + "%' ");
			} else {
				// 精确查询操作人的SQL语句
				sbpart.append(" and workerid='"
						+ workername.replaceAll(" ", "") + "' ");
			}
		}
		// 判断操作内容是否为null，空
		if (object != "" && object != null && object.length() > 0) {
			// 判断是否模糊查询操作内容
			if ("模糊".equals(queryobject)) {
				// 模糊查询操作内容的SQL语句
				sbpart.append(" and object like '%" + object.trim() + "%' ");
			} else {
				// 精确查询操作内容的SQL语句
				sbpart.append(" and object='" + object.trim() + "' ");
			}
		}
		// 判断操作类型是否为null，空
		if (operation != "" && operation != null && operation.length() > 0) {
			// 加上操作类型条件
			sbpart.append(" and operation like '%" + operation + "%' ");
		}
		// 判断操作对象是否为null，空
		if (table != "" && table != null && table.length() > 0) {
			// 获取数据表名称的SQL语句
			String tablesql = "select tname from tablemenu where cname=?";
			// 定义绑定参数集合
			List<String> lst = new ArrayList<String>();
			// 绑定操作对象参数
			lst.add(table);
			try {
				// 执行SQL语句，获取相应的数据源
				Result rs = Database.executeQuery(tablesql, lst.toArray());

				// 文件日志
				GlobalValue.myLog.info(tablesql + "#" + lst);

				// 判断数据源不为null且含有数据
				if (rs != null && rs.getRowCount() > 0) {
					// 加上操作对象条件
					sbpart.append(" and tablename=? ");
					// 绑定操作对象参数
					lstpara.add(rs.getRowsByIndex()[0][0].toString());
					// 判断操作对象是否为业务树
					// if (!"业务树".equals(table)) {
					// // 不是业务树，需要加上对操作类型的判断条件
					// sbpart.append(" and operation like '__%" + table
					// + "%' ");
					// }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			// 获取总记录数的SQL语句
			String sqlCount = "select count(*) from operationlog where 1>0 "
					+ sbpart.toString();
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sqlCount, lstpara.toArray());

			// 文件日志
			GlobalValue.myLog.info(sqlCount + "#" + lstpara+"=>"+rs);

			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0
					&& !NewEquals.equals("0",rs.getRowsByIndex()[0][0].toString())) {
				// 将条数放入jsonObj的total对象中
				jsonObj.put("total", rs.getRowsByIndex()[0][0].toString());
				// 分页查询满足条件的SQL语句
				String sqlQuery = "";
				if (GetConfigValue.isOracle) {
					sqlQuery = "select t2.* from(select t1.*,rownum rn from  (select * from operationlog where 1>0 "
							+ sbpart.toString()
							+ ") t1)t2 where t2.rn>? and t2.rn<=? order by time desc";
					// 绑定开始条数参数
					lstpara.add(start);
					// 绑定截止条数参数
					lstpara.add(start + limit);
				} else if (GetConfigValue.isMySQL) {
					sqlQuery = "select * from operationlog where 1>0 "
							+ sbpart.toString() + " order by time desc"
							+ " limit ?,?";
					// 绑定开始条数参数
					lstpara.add(start);
					// 绑定截止条数参数
					lstpara.add(limit);
				}

				// 执行SQL语句，获取相应的数据源
				rs = Database.executeQuery(sqlQuery, lstpara.toArray());

				// 文件日志
				GlobalValue.myLog.info(sqlQuery + "#" + lstpara+"=>"+rs);

				// 判断数据源不为null，且含有数据
				if (rs != null && rs.getRowCount() > 0) {
					// 循环遍历数据源
					for (int i = 0; i < rs.getRowCount(); i++) {
						// 定义json对象
						JSONObject obj = new JSONObject();
						// 生成userid对象
						obj.put("userid", rs.getRows()[i].get("workerid"));
						// 生成workername对象
						obj
								.put("workername", rs.getRows()[i]
										.get("workername"));
						// 生成time对象
						obj.put("time", rs.getRows()[i].get("time").toString());
						// 获取操作类型
						String opertion = rs.getRows()[i].get("operation") != null ? rs
								.getRows()[i].get("operation").toString()
								: "";
						// 生成opertion对象
						obj.put("opertion", opertion);
						// 生成object对象
						obj.put("object", rs.getRows()[i].get("object"));
						obj.put("service", rs.getRows()[i].get("service"));
						// 生成tablename对象
						String tablename1 = rs.getRows()[i].get("tablename") != null ? rs
								.getRows()[i].get("tablename").toString()
								: "";
						String tablename = " ";
						if ("SERVICE".equals(tablename1)) {
							tablename = "业务树";
						} else {
							tablename = opertion.replace("增加", "").replace(
									"更新", "").replace("删除", "").replace("(批量)",
									"").replace("子句", "");
						}
						// 生成操作对象对象
						obj.put("tablename", tablename);
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
		// 文件日志
		//GlobalValue.myLog.info(jsonObj);
		return jsonObj;
	}

	/**
	 * 下载满足条件的日志信息
	 * 
	 * @param start参数开始条数
	 * @param limit参数每页条数
	 * @param starttime参数开始时间
	 * @param endtime参数截止时间
	 * @param workername参数操作人
	 * @param queryname参数是否精确查询操作人
	 * @param operation参数操作类型
	 * @param table参数操作对象
	 * @param object参数操作内容
	 * @param queryobject参数是否精确查询操作内容
	 * @return json串
	 */
	public static List<List<String>> download(String starttime, String endtime,
			String workername, String queryname, String operation,
			String table, String object, String queryobject) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义存放生成Excel文件的所有内容的集合
		List<List<String>> attrinfoList = new ArrayList<List<String>>();
		// 定义存放生成Excel文件的每一行内容的集合
		List<String> rowList = new ArrayList<String>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义条件的SQL语句
		StringBuilder sbpart = new StringBuilder();
		// 判断开始时间是否为null，空
		if (starttime != "" && starttime != null && starttime.length() > 0) {
			// 加上开始时间条件
			if (GetConfigValue.isOracle) {
				sbpart.append(" and time>=to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
			} else if (GetConfigValue.isMySQL) {
//				starttime += " 00:00:00";
				sbpart.append(" and time>=DATE_FORMAT(?,'%Y-%m-%d %H:%i:%s') ");
			}

			// 绑定开始时间参数
			lstpara.add(starttime.replace("/", "-") + " 00:00:00");
		}
		// 判断截止时间是否为null，空
		if (endtime != "" && endtime != null && endtime.length() > 0) {
			// 加上截止时间条件
			if (GetConfigValue.isOracle) {
				sbpart.append(" and time<=to_date(?,'yyyy-mm-dd hh24:mi:ss') ");
			} else if (GetConfigValue.isMySQL) {
//				endtime += " 23:59:59";
				sbpart.append(" and time<=DATE_FORMAT(?,'%Y-%m-%d %H:%i:%s') ");
			}
			// 绑定截止时间参数
			lstpara.add(endtime.replace("/", "-") + " 23:59:59");
		}
		// 判断操作人是否为null，空
		if (workername != "" && workername != null && workername.length() > 0) {
			// 判断是否模糊查询操作人
			if ("模糊".equals(queryname)) {
				// 模糊查询操作人的SQL语句
				sbpart.append(" and workerid like '%"
						+ workername.replaceAll(" ", "") + "%' ");
			} else {
				// 精确查询操作人的SQL语句
				sbpart.append(" and workerid='"
						+ workername.replaceAll(" ", "") + "' ");
			}
		}
		// 判断操作内容是否为null，空
		if (object != "" && object != null && object.length() > 0) {
			// 判断是否模糊查询操作内容
			if ("模糊".equals(queryobject)) {
				// 模糊查询操作内容的SQL语句
				sbpart.append(" and object like '%" + object.trim() + "%' ");
			} else {
				// 精确查询操作内容的SQL语句
				sbpart.append(" and object='" + object.trim() + "' ");
			}
		}
		// 判断操作类型是否为null，空
		if (operation != "" && operation != null && operation.length() > 0) {
			// 加上操作类型条件
			sbpart.append(" and operation like '%" + operation + "%' ");
		}
		// 判断操作对象是否为null，空
		if (table != "" && table != null && table.length() > 0) {
			// 获取数据表名称的SQL语句
			String tablesql = "select tname from tablemenu where cname=?";
			// 定义绑定参数集合
			List<String> lst = new ArrayList<String>();
			// 绑定操作对象参数
			lst.add(table);
			try {
				// 执行SQL语句，获取相应的数据源
				Result rs = Database.executeQuery(tablesql, lst.toArray());
				// 文件日志
				GlobalValue.myLog.info(tablesql + "#" + lst);
				// 判断数据源不为null且含有数据
				if (rs != null && rs.getRowCount() > 0) {
					// 加上操作对象条件
					sbpart.append(" and tablename=? ");
					// 绑定操作对象参数
					lstpara.add(rs.getRowsByIndex()[0][0].toString());
					// 判断操作对象是否为业务树
					// if (!"业务树".equals(table)) {
					// // 不是业务树，需要加上对操作类型的判断条件
					// sbpart.append(" and operation like '__%" + table
					// + "%' ");
					// }
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			// 获取总记录数的SQL语句
			String sqlCount = "select * from operationlog where 1>0 "
					+ sbpart.toString();
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sqlCount, lstpara.toArray());

			// 判断数据源不为null，且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 定义存放生成Excel文件的每一行内容的集合
				rowList = new ArrayList<String>();
				// 定义存放属性名称对应列值的数组
				String[] columnArr = { "操作工号", "操作人", "操作时间", "操作类型", "操作内容", "操作业务",
						"操作对象" };
				for (int i = 0; i < columnArr.length; i++) {
					// 将属性名称放入Excel文件的第一行内容的集合中
					rowList.add(columnArr[i]);
				}
				attrinfoList.add(rowList);
				// 循环遍历数据源
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 定义存放生成Excel文件的每一行内容的集合
					rowList = new ArrayList<String>();
					// 生成userid对象
					rowList.add(rs.getRows()[i].get("workerid").toString());
					// 生成workername对象
					rowList.add(rs.getRows()[i].get("workername").toString());
					// 生成time对象
					rowList.add(rs.getRows()[i].get("time").toString());
					// 获取操作类型
					String opertion = rs.getRows()[i].get("operation") != null ? rs
							.getRows()[i].get("operation").toString()
							: "";
					// 生成opertion对象
					rowList.add(opertion);
					// 生成object对象
					rowList.add(rs.getRows()[i].get("object")==null?"":rs.getRows()[i].get("object").toString());
					rowList.add(rs.getRows()[i].get("service")==null?"":rs.getRows()[i].get("service").toString());
					// 生成tablename对象
					String tablename1 = rs.getRows()[i].get("tablename") != null ? rs
							.getRows()[i].get("tablename").toString()
							: "";
					String tablename = " ";
					if ("SERVICE".equals(tablename1)) {
						tablename = "业务树";
					} else {
						tablename = opertion.replace("增加", "")
								.replace("更新", "").replace("删除", "").replace(
										"(批量)", "").replace("子句", "");
					}
					// 生成操作对象对象
					rowList.add(tablename);
					// 将行内容的集合放入全内容集合中
					attrinfoList.add(rowList);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrinfoList;
		
		
	}

	
}
