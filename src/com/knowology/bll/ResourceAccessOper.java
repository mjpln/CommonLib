package com.knowology.bll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class ResourceAccessOper {

	/**
	 * 根据资源id查找资源属性
	 * 
	 * @param resName
	 *            资源类型
	 * @param resSelfID
	 *            资源id
	 * @return
	 */
	public static Map<String, String> searchAttrsByResID(String resName,
			String resSelfID) {
		Result rs;
		// 存放返回值
		Map<String, String> result = new HashMap<String, String>();
		// 存放列名和列的序号
		Map<String, String> colNameMap = new HashMap<String, String>();
		// 查询列名和列值的sql
		String sql = "select * from ResourceAttrName2FieldColNum where resourceType like '"
				+ resName + "'";
		try {
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 属性类型
					String attrName = rs.getRows()[i].get("name") != null ? rs
							.getRows()[i].get("name").toString() : "";
					// 数据类型
					String dataType = rs.getRows()[i].get("dataType") != null ? rs
							.getRows()[i].get("dataType").toString()
							: "";
					// 对应的列值
					String columnNum = rs.getRows()[i].get("columnNum") != null ? rs
							.getRows()[i].get("columnNum").toString()
							: "";
					colNameMap.put(attrName, columnNum + "_" + dataType);
					result.put(attrName, "");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		sql = "select * from ResourceAcessManager where resourceID like '"
				+ resName + "_" + resSelfID;
		try {
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				// 遍历map，找到查询的列
				for (Entry<String, String> entry : result.entrySet()) {
					String value = rs.getRows()[0].get(colNameMap.get(entry
							.getKey())) != null ? rs.getRows()[0].get(
							colNameMap.get(entry.getKey())).toString() : "";
					// 设置对应的值
					entry.setValue(value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 通过属性条件查询资源id
	 * 
	 * @param map
	 *            属性条件<属性种类，条件表达式>
	 * @param resName
	 *            资源类型
	 * @return
	 */
	public static List<String> searchResIDByAttrs(Map<String, String> map,
			String resName) {
		// 判断包含操作的属性列和属性值
		Map<String, String> attrMap = new HashMap<String, String>();
		// 计算符
		String calStr = "><!=>=<=<>";
		// 存放资源id
		List<String> result = new ArrayList<String>();
		Result rs;
		// 存放列名和列的序号
		Map<String, String> colNameMap = new HashMap<String, String>();
		// 根据资源类型查找列值
		String sql = "select name,dataType,columnNum from ResourceAttrName2FieldColNum where resourceType like '%"
				+ resName + "%'";
		try {
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 属性类型
					String attrName = rs.getRows()[i].get("name") != null ? rs
							.getRows()[i].get("name").toString() : "";
					// 数据类型
					String dataType = rs.getRows()[i].get("dataType") != null ? rs
							.getRows()[i].get("dataType").toString()
							: "";
					// 对应的列值
					String columnNum = rs.getRows()[i].get("columnNum") != null ? rs
							.getRows()[i].get("columnNum").toString()
							: "";
					colNameMap.put(attrName, columnNum + "_" + dataType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String firstSql = "select resourceid,";
		sql = " from ResourceAcessManager where resourceID like '%" + resName
				+ "%' ";
		// 如果查询参数不为空
		if (map.isEmpty()) {
			sql += " and ";
		}
		// 遍历资源属性参数
		for (Entry<String, String> entry : map.entrySet()) {
			// 属性对应的列名
			String colName = colNameMap.get(entry.getKey());
			firstSql += " attr" + colName + ",";
			// 参数值
			String param = entry.getValue();
			// 获得字段类型
			String dataType = colName.split("_")[1];
			if (dataType.equals("number")) {// number 类型
				// 给sql添加where条件字段
				sql = sql + " and Attr" + colName;
				String firstChar = param.substring(0, 1);
				// 参数中有计算式
				if (calStr.contains(firstChar)) {
					sql = sql + param;
				} else {
					sql = sql + "=" + param;
				}
			} else if (dataType.equals("time")) {// 时间类型
				// 给sql添加where条件字段
				sql = sql + " and Attr" + colName;
				String firstChar = param.substring(0, 1);
				if (GetConfigValue.isMySQL) {
					// date_format(date,'%Y-%m-%d %H:%i:%s')
					// -------------->oracle中的to_char();
					// str_to_date(date,'%Y-%m-%d %H:%i:%s')
					// -------------->oracle中的to_date();
					if (calStr.contains(firstChar)) {// 参数中有逻辑表达式
						String secondChar = param.substring(1, 2);
						if (calStr.contains(secondChar)) {// 如果是类似<>,>=这种类型的运算符
							sql = sql + firstChar + secondChar
									+ "str_to_date('"
									+ param.substring(2, param.length())
									+ "','%Y-%m-%d %H:%i:%s')";
						} else {// +,-类似的操作符
							sql = sql + firstChar + "str_to_date('"
									+ param.substring(1, param.length())
									+ "','%Y-%m-%d %H:%i:%s')";
						}
					} else {
						sql = sql + "=str_to_date('%Y-%m-%d %H:%i:%s')";
					}
				} else if (GetConfigValue.isOracle) {
					if (calStr.contains(firstChar)) {// 参数中有逻辑表达式
						String secondChar = param.substring(1, 2);
						if (calStr.contains(secondChar)) {// 如果是类似<>,>=这种类型的运算符
							sql = sql + firstChar + secondChar + "to_date('"
									+ param.substring(2, param.length())
									+ "','YYYY-MM-dd HH24:mi:ss')";
						} else {// +,-类似的操作符
							sql = sql + firstChar + "to_date('"
									+ param.substring(1, param.length())
									+ "','YYYY-MM-dd HH24:mi:ss')";
						}
					} else {
						sql = sql + "=to_date('YYYY-MM-dd HH24:mi:ss')";
					}

				}

			} else if (dataType.equals("varchar")) {// 字符类型
				if (entry.getKey().equals("地市")) {// 属性集合，别的资源只要包含该属性值中的某一个就返回
					attrMap.put("Attr" + colName, param);
				} else {
					// 给sql添加where条件字段
					sql = sql + " and Attr" + colName;
					String[] array = param.split(",");
					if (array.length > 1) {// 如果属性值不止一个
						sql = sql + " in (";
						for (String str : array) {
							sql += "'" + str + "',";
						}
						sql = sql.substring(0, sql.lastIndexOf(",")) + ")";
					} else {
						sql = sql + "=" + param;
					}
				}
			}

			sql += " and ";
		}

		// 裁剪firstSql,sql
		firstSql = firstSql.substring(0, firstSql.lastIndexOf(","));
		sql = sql.substring(0, sql.lastIndexOf("and"));
		sql = firstSql + sql;
		try {
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				// 遍历map，找到查询的列
				for (int i = 0; i < rs.getRowCount(); i++) {
					String resourceID = rs.getRows()[i].get("resourceID") != null ? rs
							.getRows()[i].get("resourceID").toString()
							: "";
					// 判断属性是否包含
					if (!attrMap.isEmpty()) {
						for (Entry<String, String> entry : attrMap.entrySet()) {// 遍历属性
							String attrValue = rs.getRows()[i].get(entry
									.getKey()) != null ? rs.getRows()[i].get(
									entry.getKey()).toString() : "";
							if (isContain(attrValue, entry.getValue())) {
								result.add(resourceID.split("_")[1]);
							}
						}
					} else {
						result.add(resourceID.split("_")[1]);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 通过属性条件查询资源id(词类专用)
	 * 
	 * @param map
	 *            属性条件<属性种类，条件表达式>
	 * @param resName
	 *            资源类型
	 * @param customer
	 *            四层结构
	 * @param wordclasstype
	 *            行业归属
	 * @return
	 */
	public static Map<String, Map<String, String>> searchResIDByAttrs(
			Map<String, String> map, String resName, String customer,
			String wordclasstype) {
		// 返回值:<资源id，<属性名称，属性值>>
		Map<String, Map<String, String>> resultMap = new HashMap<String, Map<String, String>>();
		// 判断包含操作的属性列和属性值
		Map<String, String> attrMap = new HashMap<String, String>();
		// 计算符
		String calStr = "><!=>=<=<>";
		/*
		 * // 存放资源id List<String> result = new ArrayList<String>();
		 */
		Result rs = null;
		// 存放列名和列的序号，例如：<行业归属，2_varchar>
		Map<String, String> colNameMap = new HashMap<String, String>();
		// 存放列的序号和列名，例如：<attr2_varchar,行业归属>
		Map<String, String> colNameMap2 = new HashMap<String, String>();
		// 根据资源类型查找列值
		String sql = "select name,dataType,columnNum from ResourceAttrName2FieldColNum where resourceType like '%"
				+ resName + "%'";
		try {
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 属性类型
					String attrName = rs.getRows()[i].get("name") != null ? rs
							.getRows()[i].get("name").toString() : "";
					// 数据类型
					String dataType = rs.getRows()[i].get("dataType") != null ? rs
							.getRows()[i].get("dataType").toString()
							: "";
					// 对应的列值
					String columnNum = rs.getRows()[i].get("columnNum") != null ? rs
							.getRows()[i].get("columnNum").toString()
							: "";
					colNameMap.put(attrName, columnNum + "_" + dataType);
					colNameMap2.put("attr" + columnNum + "_" + dataType,
							attrName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String firstSql = "select resourceid,";
		sql = " from ResourceAcessManager where resourceID like '%" + resName
				+ "%' ";
		// 如果查询参数不为空
		if (map.isEmpty()) {
			sql += " and ";
		}
		// 遍历资源属性参数
		for (Entry<String, String> entry : map.entrySet()) {
			// 属性对应的列名
			String colName = colNameMap.get(entry.getKey());
			firstSql += " attr" + colName + ",";
			// 参数值
			String param = entry.getValue();
			// 获得字段类型
			String dataType = colName.split("_")[1];
			if (dataType.equals("number")) {// number 类型
				// 给sql添加where条件字段
				sql = sql + " and Attr" + colName;
				String firstChar = param.substring(0, 1);
				// 参数中有计算式
				if (calStr.contains(firstChar)) {
					sql = sql + param;
				} else {
					sql = sql + "=" + param;
				}
			} else if (dataType.equals("time")) {// 时间类型
				// 给sql添加where条件字段
				sql = sql + " and Attr" + colName;
				String firstChar = param.substring(0, 1);
				if (GetConfigValue.isMySQL) {
					// date_format(date,'%Y-%m-%d %H:%i:%s')
					// -------------->mysql中的to_char();
					// str_to_date(date,'%Y-%m-%d %H:%i:%s')
					// -------------->mysql中的to_date();
					if (calStr.contains(firstChar)) {// 参数中有逻辑表达式
						String secondChar = param.substring(1, 2);
						if (calStr.contains(secondChar)) {// 如果是类似<>,>=这种类型的运算符
							sql = sql + firstChar + secondChar
									+ "str_to_date('"
									+ param.substring(2, param.length())
									+ "','%Y-%m-%d %H:%i:%s')";
						} else {// +,-类似的操作符
							sql = sql + firstChar + "str_to_date('"
									+ param.substring(1, param.length())
									+ "','%Y-%m-%d %H:%i:%s')";
						}
					} else {
						sql = sql + "=str_to_date('%Y-%m-%d %H:%i:%s')";
					}
				} else if (GetConfigValue.isOracle) {
					if (calStr.contains(firstChar)) {// 参数中有逻辑表达式
						String secondChar = param.substring(1, 2);
						if (calStr.contains(secondChar)) {// 如果是类似<>,>=这种类型的运算符
							sql = sql + firstChar + secondChar + "to_date('"
									+ param.substring(2, param.length())
									+ "','YYYY-MM-dd HH24:mi:ss')";
						} else {// +,-类似的操作符
							sql = sql + firstChar + "to_date('"
									+ param.substring(1, param.length())
									+ "','YYYY-MM-dd HH24:mi:ss')";
						}
					} else {
						sql = sql + "=to_date('YYYY-MM-dd HH24:mi:ss')";
					}

				}

			} else if (dataType.equals("varchar")) {// 字符类型
				if (entry.getKey().equals("行业归属")) {
					if (!wordclasstype.equals("全部")) {
						param = wordclasstype;
					}
					String[] array = param.split(",");
					String str = "";
					for (int i = 0; i < array.length; i++) {
						if (array[i].equals("当前商家")) {
							array[i] = customer;
						} else if (array[i].equals("当前行业")) {
							array[i] = customer.split("->")[0] + "->通用商家->通用应用";
						} else if (array[i].equals("通用行业")) {
							array[i] = "通用行业->通用商家->通用应用";
						}
						str += array[i] + ",";
					}
					param = str.substring(0, str.lastIndexOf(","));
				}
				attrMap.put("Attr" + colName, param);
			}

			sql += " and ";
		}

		// 裁剪firstSql,sql
		firstSql = firstSql.substring(0, firstSql.lastIndexOf(","));
		sql = sql.substring(0, sql.lastIndexOf("and"));
		sql = firstSql + sql;
		try {
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				// 遍历map，找到查询的列
				for (int i = 0; i < rs.getRowCount(); i++) {
					String resourceID = rs.getRows()[i].get("resourceID") != null ? rs
							.getRows()[i].get("resourceID").toString()
							: "";
					// 判断属性是否包含
					if (!attrMap.isEmpty()) {
						for (Entry<String, String> entry : attrMap.entrySet()) {// 遍历属性
							// 属性值对应的列（例如attr1_varchar）
							String colName = entry.getKey();
							// 资源配置的属性值
							String attrValue = rs.getRows()[i].get(colName) != null ? rs
									.getRows()[i].get(colName).toString()
									: "";
							// 资源配置的属性值，角色配置的属性值
							if (isContain(attrValue, entry.getValue())) {
								// result.add(resourceID.split("_")[1]);
								if (resultMap
										.containsKey(resourceID.split("_")[1])) {// 如果资源id已经存在，则修改value值
									Map<String, String> attrInfoMap = resultMap
											.get(resourceID.split("_")[1]);
									attrInfoMap.put(colNameMap2.get(colName),
											attrValue);
								} else {// 第一次插入资源id对应的属性值
									Map<String, String> attrInfoMap = new HashMap<String, String>();
									attrInfoMap.put(colNameMap2.get(colName
											.toLowerCase()), attrValue);
									resultMap.put(resourceID.split("_")[1],
											attrInfoMap);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}

	/**
	 * 判断库保存的属性是否有值包含在参数传入的属性中
	 * 
	 * @param attrValueByCheck
	 *            资源配置的属性信息
	 * @param attrValueByParam
	 *            角色配置的属性信息
	 * @return
	 */
	public static boolean isContain(String attrValueByCheck,
			String attrValueByParam) {
		// 资源配置的属性信息
		List<String> checkList = new ArrayList<String>(Arrays
				.asList(attrValueByCheck.split(",")));
		// 角色配置的属性信息
		List<String> paramList = new ArrayList<String>(Arrays
				.asList(attrValueByParam.split(",")));
		if (checkList.removeAll(paramList)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断资源是否有对应属性条件的权限
	 * 
	 * @param resName
	 *            资源类型
	 * @param resSelfID
	 *            资源id
	 * @param userOwnAccessMap
	 *            属性条件集合
	 * @return
	 */
	public static boolean isAccess(String resName, String resSelfID,
			Map<String, String> userOwnAccessMap) {
		List<String> list = searchResIDByAttrs(userOwnAccessMap, resName);
		if (list.contains(resSelfID))
			return true;
		return false;
	}

	/**
	 * 根据父业务找到相关的子业务ID
	 * 
	 * @param array
	 */
	public static List<String> getChildService(Object[] array) {
		// 返回值
		List<String> list = new ArrayList<String>();
		if (array.length == 0) {// 当没有参数时
			return list;
		}
		if (GetConfigValue.isOracle) {
			String sql = "select serviceID from service start WITH serviceid in ("
					+ org.apache.commons.lang.StringUtils.join(array, ",")
					+ ")　connect BY nocycle prior serviceid = parentid";
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					String serviceid = rs.getRows()[i].get("serviceID") != null ? rs
							.getRows()[i].get("serviceID").toString()
							: "";
					list.add(serviceid);
				}
			}
		} else if (GetConfigValue.isMySQL) {
			// 通过自定义函数查询子业务ID
			String sql = "SELECT getServiceChildrenIdListByServiceid ("
					+ org.apache.commons.lang.StringUtils.join(array, ",")
					+ ") as serviceids";
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				String serviceid = rs.getRows()[0].get("serviceids").toString();
				String serviceidArray[] = serviceid.split(",");
				list = Arrays.asList(serviceidArray);
				Collections.sort(list);
			}
		}

		return list;
	}

	/**
	 * 根据资源名称，找到资源id
	 * 
	 * @param array
	 *            资源名数组
	 * @param resourceType
	 *            资源类型
	 * @return
	 */
	public static List<String> getResourceIDByName(Object[] array,
			String resourceType) {
		// 返回值
		List<String> list = new ArrayList<String>();
		if (array.length == 0) {// 如果参数为空
			return list;
		}
		String sql = "";
		if (resourceType.equals("service")) {// 业务
			sql = "select serviceID as resourceid from service where service in ("
					+ org.apache.commons.lang.StringUtils.join(array, ",")
					+ ")";
		} else if (resourceType.equals("kbdata")) {// 摘要
			sql = "select kbdataID as resourceid from kbdata where abstract in ("
					+ org.apache.commons.lang.StringUtils.join(array, ",")
					+ ")";
		} else if (resourceType.equals("answer")) {// 答案
			sql = "select kbanswerid as resourceid from kbanswer where answercontent in ("
					+ org.apache.commons.lang.StringUtils.join(array, ",")
					+ ")";
		} else if (resourceType.equals("baseWord")
				|| resourceType.equals("sentence")) {// 词类
			sql = "select wordclassid as resourceid from wordclass where wordclass in ("
					+ org.apache.commons.lang.StringUtils.join(array, ",")
					+ ")";
		}
		try {
			Result rs = Database.executeQuery(sql, array);
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					String resourceID = rs.getRows()[i].get("resourceid") != null ? rs
							.getRows()[i].get("resourceid").toString()
							: "";
					list.add(resourceID);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 新增资源属性
	 * 
	 * @param resName
	 *            资源类型
	 * @param resSelfID
	 *            资源id
	 * @param resMap
	 *            资源属性
	 * @return
	 */
	public static int addResource(String resName, String resSelfID,
			Map<String, String> resMap) {
		// 返回值
		int count = 0;
		// 获得插入的列名及类型
		String sql = "select * from ResourceAttrName2FieldColNum where resourceType like '"
				+ resName + "'";
		// 属性类型
		Object[] params = resMap.keySet().toArray();
		String attributes = "(";
		for (Object param : params) {
			attributes += param + ",";
		}
		attributes = attributes.substring(0, attributes.lastIndexOf(",")) + ")";
		sql += " and " + attributes;
		// 存放列名和列的序号
		Map<String, String> colNameMap = new HashMap<String, String>();
		Result rs;
		try {
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 属性类型
					String attrName = rs.getRows()[i].get("name") != null ? rs
							.getRows()[i].get("name").toString() : "";
					// 数据类型
					String dataType = rs.getRows()[i].get("dataType") != null ? rs
							.getRows()[i].get("dataType").toString()
							: "";
					// 对应的列值
					String columnNum = rs.getRows()[i].get("columnNum") != null ? rs
							.getRows()[i].get("columnNum").toString()
							: "";
					colNameMap.put(attrName, columnNum + "_" + dataType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// insert语句参数
		List<Object> insertParams = new ArrayList<Object>();
		// 拼接存值sql
		String firstInsert = "insert into ResourceAcessManager(id,resourceID";
		String lastInsert = " values (ResourceAcessManager.nextval,?";
		insertParams.add(resName + "_" + resSelfID);
		for (Entry<String, String> entry : colNameMap.entrySet()) {
			firstInsert += entry.getValue() + ",";
			// 构造出入的参数
			if (entry.getValue().split("_")[1].equals("time")) {// 时间类型
				insertParams.add("'" + resMap.get(entry.getKey())
						+ "','YYYY-MM-dd HH24:mi:ss'");
				lastInsert += "to_date(?),";
			} else {
				insertParams.add(resMap.get(entry.getKey()));
				lastInsert += "?,";
			}
		}
		firstInsert = firstInsert.substring(0, firstInsert.lastIndexOf(","))
				+ ")";
		lastInsert = lastInsert.substring(0, lastInsert.lastIndexOf(",")) + ")";
		sql = firstInsert + lastInsert;
		try {
			count = Database.executeNonQuery(sql, insertParams.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 修改资源属性
	 * 
	 * @param resName
	 *            资源类型
	 * @param resSelfID
	 *            资源id
	 * @param resMap
	 *            属性参数集合
	 * @return
	 */
	public static int updateResource(String resName, String resSelfID,
			Map<String, String> resMap) {
		// 返回值
		int count = 0;
		// 获得插入的列名及类型
		String sql = "select * from ResourceAttrName2FieldColNum where resourceType like '"
				+ resName + "'";
		// 属性类型
		Object[] params = resMap.keySet().toArray();
		String attributes = "(";
		for (Object param : params) {
			attributes += param + ",";
		}
		attributes = attributes.substring(0, attributes.lastIndexOf(",")) + ")";
		sql += " and " + attributes;
		// 存放列名和列的序号
		Map<String, String> colNameMap = new HashMap<String, String>();
		Result rs;
		try {
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 属性类型
					String attrName = rs.getRows()[i].get("name") != null ? rs
							.getRows()[i].get("name").toString() : "";
					// 数据类型
					String dataType = rs.getRows()[i].get("dataType") != null ? rs
							.getRows()[i].get("dataType").toString()
							: "";
					// 对应的列值
					String columnNum = rs.getRows()[i].get("columnNum") != null ? rs
							.getRows()[i].get("columnNum").toString()
							: "";
					colNameMap.put(attrName, columnNum + "_" + dataType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// insert语句参数
		List<Object> updateParams = new ArrayList<Object>();

		// 更新操作sql
		sql = "update ResourceAcessManager set ";

		// 组合sql
		for (Entry<String, String> entry : colNameMap.entrySet()) {
			// 构造出入的参数
			if (entry.getValue().split("_")[1].equals("time")) {// 时间类型
				updateParams.add("'" + resMap.get(entry.getKey())
						+ "','YYYY-MM-dd HH24:mi:ss'");
				sql += entry.getValue() + "=to_date(?),";
			} else {
				updateParams.add(resMap.get(entry.getKey()));
				sql += entry.getValue() + "=?,";
			}
		}

		sql = sql.substring(0, sql.lastIndexOf(","));
		sql = sql + " where resourceID=?";
		// 添加where参数
		updateParams.add(resName + "_" + resSelfID);
		try {
			count = Database.executeNonQuery(sql, updateParams.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 删除资源对应的属性
	 * 
	 * @param belongCom
	 *            所属机构
	 * @param resName
	 *            资源类型
	 * @param resSelfID
	 *            资源id
	 * @return
	 */
	public static int deleteResource(String resName, String resSelfID) {
		// 返回值
		int count = 0;
		// 操作sql语句
		String sql = "delete from ResourceAcessManager where resourceID=?";
		try {
			count = Database.executeNonQuery(sql, new Object[] { resName + "_"
					+ resSelfID });
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

}
