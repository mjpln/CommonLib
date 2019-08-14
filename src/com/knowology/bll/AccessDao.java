package com.knowology.bll;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.io.FileUtils;

import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

/**
 * 
 * 权限操作的数据库处理类
 * 
 * @author knowology
 * 
 */
public class AccessDao {
	
	
//	static {
//		
//		Result r = CommonLibMetafieldmappingDAO.getConfigMinValue("地市编码配置");
//		if (r != null && r.getRowCount() > 0) {
//			// 循环遍历数据源
//			for (int i = 0; i < r.getRowCount(); i++) {
//				String key = r.getRows()[i].get("k") == null ? ""
//						: r.getRows()[i].get("k").toString();
//				String value = r.getRows()[i].get("name") == null ? "" : r
//						.getRows()[i].get("name").toString();
//				cityCodeToCityName.put(value, key);
//				cityNameToCityCode.put(key, value);
//			}
//		}
//	}
	
	/**
	 * 获取资源以及资源的属性信息
	 * @param resourceType 资源类型
	 * @param resourceID 资源ID
	 * @param customer 所属机构
	 * @param limit 每页显示的个数
	 * @param start 开始的条数
	 * @return
	 */
	public static Map<String,Result> selectResourceAttr(String resourceType, String resourceID, String customer, List<String> columnList, int limit, int start) {
		// 返回值
		Map<String, Result> resultMap = new HashMap<String, Result>();
		try{
			// 查询资源及属性的sql
			// 查询资源的sql
			String inner_resource = "";
			if ("service".equals(resourceType)) {// 业务 目前是写死的--CONCAT('service_',serviceID)
				if (GetConfigValue.isOracle) {
					inner_resource = "(select ('service_' || serviceID) as resourcesID,service as resourceName From Service where serviceID='"+resourceID+"'";
				} else if(GetConfigValue.isMySQL) {
					inner_resource = "(select CONCAT('service_',CAST(serviceID AS CHAR)) as resourcesID,service as resourceName From Service where serviceID='"+resourceID+"'";
				}
			} else if("kbdata".equals(resourceType)){// 摘要
				if (GetConfigValue.isOracle) {
					inner_resource = "(select ('kbdata_' || kbdataID) as resourcesID,abstract as resourceName from kbdata where kbdataID='"+resourceID+"'";
				} else if(GetConfigValue.isMySQL) {
					inner_resource = "(select CONCAT('kbdata_',cast(kbdataID as char)) as resourcesID,abstract as resourceName from kbdata where kbdataID='"+resourceID+"'";
				}
			} else {
				if (GetConfigValue.isOracle) {
					inner_resource = "(select ('answer_' || kbanswerid) as resourcesID,answercontent as resourceName from kbanswer where kbanswerid='"+resourceID+"'";
				} else if(GetConfigValue.isMySQL) {
					inner_resource = "(select CONCAT('answer_',cast(kbanswerid as char)) as resourcesID,answercontent as resourceName from kbanswer where kbanswerid='"+resourceID+"'";
				}
			}
			inner_resource += ") a";
			
			// 查询属性的sql
			String inner_attr = "(select ID,resourceID ";
			for (String column : columnList) {
				inner_attr += "," + column;
			}
			inner_attr += " from ResourceAcessManager where resourceID like '"+resourceType+"%') b";
			String inner_sql = "select * from "+inner_resource+" left join "+inner_attr+" on a.resourcesID=b.resourceID";
			Result rs = Database.executeQuery(inner_sql);
			resultMap.put("count", rs);
			
			// 分页的sql
			String sql = "";
			if (GetConfigValue.isOracle) {
				sql = "select * from (select t.*,rownum rn from"+
			 	  " ("+inner_sql+") t where rownum<"+(limit+start+1)+")"+
			 	  " where rn>"+start;
			} else if(GetConfigValue.isMySQL) {
				sql = inner_sql + " limit " + start + "," + limit;
			}
			
			rs = Database.executeQuery(sql);
			resultMap.put("data", rs);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 更新资源的属性值
	 * @param columnArray 要更新的列
	 * @param columnValueArray 列对应的值
	 * @param serviceIDs 业务id集合
	 * @param fatherID 根业务ID
	 * @return
	 */
	public static int updateResourceAttrInfo(String[] columnArray, String[] columnValueArray, List<String> serviceIDs, String fatherID) {
		// 返回值
		int count = 0;
		// 存放sql语句
		List<String> sqlsList = new ArrayList<String>();
		// 存放参数
		List<List<?>> paramsList = new ArrayList<List<?>>();
		// 所有业务对应的更新摘要和答案所涉及的数据集合
		List<Map<String, Object>> kbdataAndAnswerList = new ArrayList<Map<String, Object>>();
		for (String serviceID : serviceIDs) {
			// 每个业务对应的更新摘要和答案所涉及的数据集合
			Map<String, Object> map = new HashMap<String, Object>();
			// 修改serviceID的值
			columnValueArray[1] = "service_"+serviceID;
			// 根据列，查询该列是否已经有参数
			List<String> list = isContainValue(columnArray, serviceID);
			if (list.isEmpty() || list.get(0).equals("")) {
				String sql_first = "insert into ResourceAcessManager(ID,";
				String sql_last = "";
				if (GetConfigValue.isOracle) {
					sql_last = " values(Resourceacessmanager_sequence.nextval,";
				} else if(GetConfigValue.isMySQL) {
					sql_last = " values(" + ConstructSerialNum.getSerialID("ResourceAcessManager", "id") + ",";
				}
				
				List<String> paramList = new ArrayList<String>();
				for (int i=1 ; i<columnArray.length ; i++) {
					if (i>1) {
						if (!list.isEmpty() && !list.get(i).equals("")) {// 该列已有参数
							continue;
						}
					}
					sql_first += columnArray[i] + ",";
					sql_last += "?,";
					if (columnValueArray[i].equals("null")) {// 当属性值有为null，表示前台没有填写该值，则将该null转换为空，存入数据库
						columnValueArray[i] = "";
					}
					paramList.add(columnValueArray[i]);
					// 向更新摘要和答案的集合中
					map.put(columnArray[i],columnValueArray[i]);
				}
				kbdataAndAnswerList.add(map);
				sql_first = sql_first.substring(0, sql_first.lastIndexOf(",")) + ")";
				sql_last = sql_last.substring(0, sql_last.lastIndexOf(",")) + ")";
				String sql = sql_first + sql_last;
				
				sqlsList.add(sql);
				paramsList.add(paramList);
			} else {
				// 更新sql
				String sql = "update ResourceAcessManager set ";
				List<String> paramList = new ArrayList<String>();
				for (int i=1 ; i<columnArray.length ; i++) {
					String s = list.get(1).split("_")[1];
//					if (i>1 && !s.equals(fatherID)) { ghj update
					if (i>1 && ! NewEquals.equals(s,fatherID)) {

						if (!list.isEmpty() && !list.get(i).equals("")) {// 该列已有参数
							continue;
						}
					}
					sql += columnArray[i] + "=?,";
					if (columnValueArray[i].equals("null")) {// 当属性值有为null，表示前台没有填写该值，则将该null转换为空，存入数据库
						columnValueArray[i] = "";
					}
					paramList.add(columnValueArray[i]);
					// 向更新摘要和答案的集合中
					map.put(columnArray[i],columnValueArray[i]);
				}
				kbdataAndAnswerList.add(map);
				sql = sql.substring(0, sql.lastIndexOf(","));
				sql += " where ID=?";
				paramList.add(list.get(0));
				
				sqlsList.add(sql);
				paramsList.add(paramList);
			}
		}
		try {
			count = Database.executeNonQueryTransaction(sqlsList, paramsList);
			if (count < 1) {// 更新失败
				return 0;
			}
			// 更新业务对应的摘要和答案属性
			updateKbdataAndAnswer(kbdataAndAnswerList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 在更新业务的属性时，修改该业务以及其没有配置属性的子业务对应的摘要和答案
	 * @param list 每条业务
	 * @return
	 */
	public static int updateKbdataAndAnswer(List<Map<String, Object>> list) {
		// 返回值
		int count = 0;
		String sql = "";
		Result rs = null;
		
		// 查询数据库，获得业务，摘要，答案共有的属性
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("service,kbdata", "");// 业务和摘要都有的属性
		attrMap.put("service,answer", "");// 业务和答案都有的属性
		attrMap.put("service,kbdata,answer", "");// 业务、摘要、答案共有的属性
		sql = "SELECT resourceType,columnNum,dataType FROM Resourceattrname2fieldcolnum WHERE resourceType LIKE 'service,%' OR resourceType LIKE '%,service' OR resourceType LIKE '%,service,%'";
		rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				// 获得属性所适用的范围
				String resourceType = rs.getRows()[i].get("resourceType").toString();
				String columnNum = rs.getRows()[i].get("columnNum").toString();
				String dataType = rs.getRows()[i].get("dataType").toString();
				
				if(resourceType.split(",").length > 2) {// 业务、摘要、答案共有的属性
					String attr = attrMap.get("service,kbdata,answer").toString();
					attr += "attr" + columnNum + "_" + dataType + ",";
					attrMap.put("service,kbdata,answer", attr);
				} else if(resourceType.split(",").length == 2) {// 业务、摘要或者业务、答案共有的属性
					if(resourceType.contains("kbdata")) {// 业务、摘要
						String attr = attrMap.get("service,kbdata").toString();
						attr += "attr" + columnNum + "_" + dataType + ",";
						attrMap.put("service,kbdata", attr);
					} else if(resourceType.contains("answer")) {// 业务、答案
						String attr = attrMap.get("service,answer").toString();
						attr += "attr" + columnNum + "_" + dataType + ",";
						attrMap.put("service,answer", attr);
					}
				}
				
			}
		}
		
		// 根据业务id找出所有的摘要id
		for (Map<String, Object> map : list) {
			// 得到业务id
			String serviceID = map.get("resourceID").toString();
			serviceID = serviceID.substring(serviceID.indexOf("_") + 1, serviceID.length());
			// 查询该业务下是否有摘要
			sql = "select kbdataid from kbdata where serviceid=" + serviceID;
			rs = Database.executeQuery(sql);
			// 存放摘要的集合
			List<String> kbdataIDs = new ArrayList<String>();
			List<String> kbdataIDs_noSuffix = null;
			if (rs != null && rs.getRowCount() > 0) {// 有摘要
				kbdataIDs_noSuffix = new ArrayList<String>();
				for (int i = 0; i < rs.getRowCount(); i++) {
					Object obj = rs.getRows()[i].get("kbdataid");
					if (obj != null) {
						kbdataIDs.add("kbdata_" + obj.toString());
						kbdataIDs_noSuffix.add(obj.toString());
					}
				}
			}
			if (kbdataIDs.isEmpty()) {// 修改的业务没有对应的摘要
				continue;
			}
			// 存放答案的集合
			List<String> answerIDs = new ArrayList<String>();
			sql = "SELECT g.kbanswerid AS answerid FROM service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g" +
				  " WHERE a.serviceid=b.serviceid" +
				  " AND b.kbdataid=c.kbdataid" +
				  " AND c.kbansvaliddateid=d.kbansvaliddateid"+
				  " AND d.kbanspakid=e.kbanspakid" +
				  " AND E.Kbansqryinsid=F.Kbansqryinsid" +
				  " AND f.kbcontentid=g.kbcontentid AND b.kbdataid in (" + kbdataIDs_noSuffix.toString().substring(1, kbdataIDs_noSuffix.toString().length()-1) + ")";
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {// 有答案
				for (int i = 0; i < rs.getRowCount(); i++) {
					Object obj = rs.getRows()[i].get("answerid");
					if (obj != null) {
						answerIDs.add("answer_" + obj.toString());
					}
				}
			}
			//移除业务id
			map.remove("resourceID");
			
			
			
			// 业务、摘要、答案共有的属性
			Map<String, Object> allMap = new HashMap<String, Object>();
			// 业务、摘要共有的属性
			Map<String, Object> skMap = new HashMap<String, Object>();
			// 业务、答案共有的属性
			Map<String, Object> saMap = new HashMap<String, Object>();
			
			// 获得所有的属性、属性具体值，进行对属性的操作
			for (Entry<String, Object> entry : map.entrySet()) {
				// 属性名
				String attr = entry.getKey().toLowerCase();
				if(attrMap.get("service,kbdata,answer").toString().toLowerCase().contains(attr)) {// 业务、摘要、答案共有的属性
					allMap.put(entry.getKey(), entry.getValue());
				} else if(attrMap.get("service,kbdata").toString().toLowerCase().contains(attr)) {// 业务、摘要共有的属性
					skMap.put(entry.getKey(), entry.getValue());
				} else if(attrMap.get("service,answer").toString().toLowerCase().contains(attr)) {// 业务、答案共有的属性
					saMap.put(entry.getKey(), entry.getValue());
				}
			}
			count += updateKbdataAndAnswer(allMap, skMap, saMap, kbdataIDs, answerIDs);
		}
		return count;
	}
	
	/**
	 * 在更新业务的属性时，修改该业务以及其没有配置属性的子业务对应的摘要和答案
	 * @param allMap 所有资源都有的属性
	 * @param skMap 业务、摘要共有的属性
	 * @param saMap 业务、答案共有的属性
	 * @param kbdataIDs 摘要id集合
	 * @param answerIDs 答案id集合
	 * @return
	 */
	public static int updateKbdataAndAnswer(Map<String, Object> allMap, Map<String, Object> skMap, Map<String, Object> saMap, List<String> kbdataIDs, List<String> answerIDs) {
		int count = 0;
		// 存放sql语句
		List<String> sqlsList = new ArrayList<String>();
		// 存放参数
		List<List<?>> paramsList = new ArrayList<List<?>>();
		// 查询出所有ResourceAcessManager中摘要的id
		List<String> allKbdataID = new ArrayList<String>();
		// 查询出所有ResourceAcessManager中答案的id
		List<String> allAnswerID = new ArrayList<String>();
		String sql = "select resourceid from ResourceAcessManager where resourceid like 'kbdata_%'";
		Result rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				Object obj = rs.getRows()[i].get("resourceid");
				if (obj != null) {
					allKbdataID.add(obj.toString());
				}
			}
		}
		
		sql = "select resourceid from ResourceAcessManager where resourceid like 'answer_%'";
		rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				Object obj = rs.getRows()[i].get("resourceid");
				if (obj != null) {
					allKbdataID.add(obj.toString());
				}
			}
		}
		// 遍历传入的摘要id，如果在allKbdataID中没有就在ResourceAcessManager中新增	
		for (String kbdataID : kbdataIDs) {
			if(!allKbdataID.contains(kbdataID)) {
				if (GetConfigValue.isOracle) {
					sql = "insert into ResourceAcessManager (id,resourceid) values (RESOURCEACESSMANAGER_SEQUENCE.nextval,?)";
				} else if (GetConfigValue.isMySQL) {
					sql = "insert into ResourceAcessManager (id,resourceid) values (" + ConstructSerialNum.getSerialID("ResourceAcessManager", "id") + ",?)";
				}
				List<String> list = new ArrayList<String>();
				list.add(kbdataID);
				sqlsList.add(sql);
				paramsList.add(list);
			}
		}
		
		// 遍历传入的问题id，如果在allKbdataID中没有就在ResourceAcessManager中新增
		for (String answerID : answerIDs) {
			if (!allAnswerID.contains(answerID)) {
				if (GetConfigValue.isOracle) {
					sql = "insert into ResourceAcessManager (id,resourceid) values (RESOURCEACESSMANAGER_SEQUENCE.nextval,?)";
				} else if (GetConfigValue.isMySQL) {
					sql = "insert into ResourceAcessManager (id,resourceid) values (" + ConstructSerialNum.getSerialID("ResourceAcessManager", "id") + ",?)";
				}
				List<String> list = new ArrayList<String>();
				list.add(answerID);
				sqlsList.add(sql);
				paramsList.add(list);
			}
		}
		
		if (!allMap.isEmpty()) {// 所有资源都有的属性不为空
			String firstSql = "update ResourceAcessManager set ";
			List<Object> list = new ArrayList<Object>();
			for (Entry<String,Object> entry : allMap.entrySet()) {
				firstSql += entry.getKey() + "=?,";
				list.add(entry.getValue());
			}
			sql = firstSql.substring(0, firstSql.lastIndexOf(",")) + " where (";
			// 摘要
			for (String kbdataID : kbdataIDs) {
				sql += "resourceid='" + kbdataID + "' or ";
			}
			// 答案
			for(String answerID : answerIDs) {
				sql += "resourceid='" + answerID + "' or ";
			}
			sql = sql.substring(0, sql.lastIndexOf("or")) + ")";
			sqlsList.add(sql);
			paramsList.add(list);
		} 
		if(!skMap.isEmpty()) {// 业务、摘要共有的属性不为空
			String firstSql = "update ResourceAcessManager set ";
			List<Object> list = new ArrayList<Object>();
			for (Entry<String,Object> entry : skMap.entrySet()) {
				firstSql += entry.getKey() + "=?,";
				list.add(entry.getValue());
			}
			sql = firstSql.substring(0, firstSql.lastIndexOf(",")) + " where (";
			// 摘要
			for (String kbdataID : kbdataIDs) {
				sql += "resourceid='" + kbdataID + "' or ";
			}
			sqlsList.add(sql);
			paramsList.add(list);
		} 
		if(!saMap.isEmpty()) {// 业务、答案共有的属性不为空
			String firstSql = "update ResourceAcessManager set ";
			List<Object> list = new ArrayList<Object>();
			for (Entry<String,Object> entry : saMap.entrySet()) {
				firstSql += entry.getKey() + "=?,";
				list.add(entry.getValue());
			}
			sql = firstSql.substring(0, firstSql.lastIndexOf(",")) + " where (";
			// 答案
			for(String answerID : answerIDs) {
				sql += "resourceid='" + answerID + "' or ";
			}
			sqlsList.add(sql);
			paramsList.add(list);
		}
		count = Database.executeNonQueryTransaction(sqlsList, paramsList);
		return count;
	}
	
	/**
	 * 更新资源的属性值
	 * @param columns 要更新的列
	 * @param columnValue 列对应的值
	 * @param resourceType 资源类型
	 * @return
	 */
	public static int updateResourceAttrInfo(String columns, String columnValue, String resourceType) {
		// 返回值
		int count = 0;
		// 解析列
		String[] columnArray = columns.split(",");
		// 更新的sql语句
		String sql = "";
		// 参数
		List<String> params = new ArrayList<String>();
		// 解析列对应的值
		String[] columnValueArray = columnValue.split("@");
		
		// 如果资源类型为业务，则加载出该业务所有的子业务
		if ("service".equals(resourceType)) {
			List<String> childServiceIDs = ResourceAccessOper.getChildService(new String[]{columnValueArray[1].split("_")[1]});
			count = updateResourceAttrInfo(columnArray, columnValueArray, childServiceIDs, childServiceIDs.get(0));
			return count;
		}
		
		if (columnValueArray[0].equals("")) {
			String sql_first = "insert into ResourceAcessManager(";
			String sql_last = "";
			if (GetConfigValue.isOracle) {
				sql_last = " values(Resourceacessmanager_sequence.nextval,";
			} else if(GetConfigValue.isMySQL) {
				sql_last = " values(" + ConstructSerialNum.getSerialID("ResourceAcessManager", "id") + ",";
			}
			for (int i=0 ; i<columnArray.length ; i++) {
				sql_first += columnArray[i] + ",";
				sql_last += "?,";
				if (columnValueArray[i].equals("null")) {// 当属性值有为null，表示前台没有填写该值，则将该null转换为空，存入数据库
					columnValueArray[i] = "";
				}
			}
			sql_first = sql_first.substring(0, sql_first.lastIndexOf(",")) + ")";
			sql_last = sql_last.substring(0, sql_last.lastIndexOf("?")-1) + ")";
			sql = sql_first + sql_last;
			
			// 构造参数
			params.addAll(Arrays.asList(columnValueArray));
			params.remove(0);
		} else {
			// 更新sql
			sql = "update ResourceAcessManager set ";
			for (int i=1 ; i<columnArray.length ; i++) {
				sql += columnArray[i] + "=?,";
				if (columnValueArray[i].equals("null")) {// 当属性值有为null，表示前台没有填写该值，则将该null转换为空，存入数据库
					columnValueArray[i] = "";
				}
			}
			sql = sql.substring(0, sql.lastIndexOf(","));
			sql += " where ID=?";
			
			// 构造参数
			params.addAll(Arrays.asList(columnValueArray));
			params.add(params.get(0));
			params.remove(0);
		}
		
		try {
			count = Database.executeNonQuery(sql, params.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return count;
	}
	
	/**
	 * 根据列，查询该列是否已经有参数
	 * @param columnArray 列集合
	 * @param serviceID 业务id
	 * @return
	 */
	public static List<String> isContainValue(String[] columnArray, String serviceID) {
		// 返回值
		List<String> list = new ArrayList<String>();
		Result rs = null;
		String sql = "select * from ResourceAcessManager where resourceID like 'service_"+serviceID+"'";
		try{
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					for (String column : columnArray) {
						String attr = rs.getRows()[i].get(column) != null ? rs.getRows()[i].get(column).toString() : "";
						list.add(attr);
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 查询所有的属性
	 * @param attrName 属性名称
	 * @param customer 所属机构
	 * @param limit 每页显示的个数
	 * @param start 开始条目
	 * @return
	 */
	public static Map<String,Result> selectAttr(String attrName, String customer, int limit, int start) {
		// 返回值
		Map<String, Result> resultMap = new HashMap<String, Result>();
		// 统计条数的sql
		String innerSql = "select * from Resourceattrname2fieldcolnum where business like '"+customer+"'";
		// 添加查询条件
		if (!"".equals(attrName) && attrName != null) {
			innerSql += " and name like '%"+attrName+"%'";
		}
		
		try{
			Result rs = Database.executeQuery(innerSql);
			resultMap.put("count", rs);
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// 分页的sql
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select * from (select t.*,rownum rn from"+
			 	  " ("+innerSql+" order by columnnum ) t where rownum<"+(limit+start+1)+")"+
			 	  " where rn>"+start;
		} else if(GetConfigValue.isMySQL) {
			sql = innerSql + " order by id limit " + start + "," + limit;
		}
		
		try{
			Result rs = Database.executeQuery(sql);
			resultMap.put("data", rs);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
	/**
	 * 新增属性
	 * @param resourceType 资源类型
	 * @param attrName 属性名
	 * @param dataType 数据类型
	 * @param shape 展现形式
	 * @param customer 所属机构
	 * @param columnNum 对应列
	 * @return
	 */
	public static int addAttr(String resourceType, String attrName, String dataType, String shape, String customer, String columnNum) {
		// 返回值
		int count = 0;
		String insertSql = "";
		if (GetConfigValue.isOracle) {
			insertSql = "insert into Resourceattrname2fieldcolnum (ID,business,resourceType,columnNum,name,dataType,shape) values (attrcolumn_sequence.nextval,?,?,?,?,?,?)";
		} else if(GetConfigValue.isMySQL) {
			insertSql = "insert into Resourceattrname2fieldcolnum (ID,business,resourceType,columnNum,name,dataType,shape) values"+
						" (" + ConstructSerialNum.getSerialID("Resourceattrname2fieldcolnum", "id") + ",?,?,?,?,?,?)";
		}
		try {
			count = Database.executeNonQuery(insertSql,new Object[]{customer, resourceType, columnNum, attrName, dataType, shape});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 构造属性对应列下拉框
	 * @param customer
	 * @return
	 */
	public static Result constructAttrCombobox(String customer) {
		// 返回值
		Result rs = null;
		String sql = "select columnNum from Resourceattrname2fieldcolnum where business='"+customer+"'";
		try{
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 修改属性值
	 * @param attrID 属性id
	 * @param attrName 属性名
	 * @return
	 */
	public static int updateAttr(String attrID, String attrName) {
		// 返回值
		int count = 0;
		String sql = "update Resourceattrname2fieldcolnum set name=? where ID=?";
		try {
			count = Database.executeNonQuery(sql,new Object[]{attrName, attrID});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 删除属性
	 * @param attrID  属性ID
	 * @return
	 */
	public static int deleteAttr(String attrID) {
		// 返回值
		int count = 0;
		// 存放sql的集合
		List<String> sqlList = new ArrayList<String>();
		// 存放参数的集合
		List<List<?>> paramsList = new ArrayList<List<?>>();
		// 查询出该属性对应ResourceAcessManager表中的列名
		String columnName = "";// 要删除的列名
		String sql_selectColumn = "";
		if (GetConfigValue.isOracle) {
			sql_selectColumn = "select ('Attr' || ColumnNum || '_' || DataType) as columnName from ResourceAttrName2FieldColNum where ID="+attrID;
		} else if(GetConfigValue.isMySQL) {
			sql_selectColumn = "select GROUP_CONCAT('Attr',cast(ColumnNum as char),'_',cast(DataType as char))as columnName from ResourceAttrName2FieldColNum where ID="+attrID;
		}
		try{
			Result rs = Database.executeQuery(sql_selectColumn);
			if (rs != null && rs.getRowCount() > 0) {
				// 列名
				columnName = rs.getRows()[0].get("columnName") != null ? rs.getRows()[0].get("columnName").toString() : "";
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		// 删除Resourceattrname2fieldcolnum表数据
		String sql_rColumn = "delete from Resourceattrname2fieldcolnum where ID=?";
		// 设置ResourceAcessManager对应的列值为空
		String sql_rManager = "update ResourceAcessManager set "+columnName+"=?";
		// 删除attrvalueinfo对应的属性值
		String sql_rAttrInfo = "delete from attrvalueinfo where attrinfoid=?";
		sqlList.add(sql_rColumn);
		sqlList.add(sql_rManager);
		sqlList.add(sql_rAttrInfo);
		
		// Resourceattrname2fieldcolnum参数
		List<Object> rColumn_param = new ArrayList<Object>();
		rColumn_param.add(attrID);
		paramsList.add(rColumn_param);
		
		// ResourceAcessManager参数
		List<Object> rManager_param = new ArrayList<Object>();
		rManager_param.add(null);
		paramsList.add(rManager_param);
		
		// attrvalueinfo参数
		List<Object> rAttrInfo_param = new ArrayList<Object>();
		rAttrInfo_param.add(attrID);
		paramsList.add(rAttrInfo_param);
		
		count = Database.executeNonQueryTransaction(sqlList, paramsList);
		return count;
	}
	
	/**
	 * 构造属性值树 ztree 类型
	 * @param attrID 属性ID
	 * @param treeColumn 对应AttrValueInfo表的字段
	 * @param id 父节点id
	 * @param attrValue ResourceAcessManager的列对应的列值
	 * @return
	 */
	public static Result constructAttrTree(String attrID, String treeColumn, String id) {
		// 返回值
		Result rs = null;
		String sql_attr = "";
		if (id == null || "".equals(id)) {
			sql_attr = "Select a.Coding,a."+treeColumn+","+
					   "Case When (Select Count(*) From Attrvalueinfo Where Parentid=A.Coding)>0 Then 'false' else 'true' end as leaf"+ 
					   " from Attrvalueinfo a where length(a.Coding)=4 and attrInfoID='"+attrID+"'";
		} else {
			sql_attr = "Select a.Coding,a."+treeColumn+","+
			   		   "Case When (Select Count(*) From Attrvalueinfo Where Parentid=A.Coding)>0 Then 'false' else 'true' end as leaf"+ 
			   		   " from Attrvalueinfo a where a.Coding like '" + id + "%' and length(a.Coding)>4 and attrInfoID='"+attrID+"'";
		}
		try{
			rs = Database.executeQuery(sql_attr);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;	
	}
	
	/**
	 * 构造属性值树 ztree 类型
	 * @param attrID 属性ID
	 * @param treeColumn 对应AttrValueInfo表的字段
	 * @param id 父节点id
	 * @param attrValue ResourceAcessManager的列对应的列值
	 * @return
	 */
	public static Result constructAttrTree_new(String attrID, String treeColumn, String id) {
		// 返回值
		Result rs = null;
		String sql_attr = "";
		if (id == null || "".equals(id)) {
			sql_attr = "Select a.Coding,a."+treeColumn+","+
					   "Case When (Select Count(*) From Attrvalueinfo Where Parentid=A.Coding)>0 Then 'false' else 'true' end as leaf"+ 
					   " from Attrvalueinfo a where a.parentid is null and a.attrInfoID='"+attrID+"'";
		} else {
			sql_attr = "Select a.Coding,a."+treeColumn+","+
			   		   "Case When (Select Count(*) From Attrvalueinfo Where Parentid=A.Coding)>0 Then 'false' else 'true' end as leaf"+ 
			   		   " from Attrvalueinfo a where  a.parentid= '"+id+"' and attrInfoID='"+attrID+"'";
		}
		try{
			rs = Database.executeQuery(sql_attr);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;	
	}
	
	/**
	 * 给属性添加属性值
	 * @param attrID 属性ID
	 * @param fatherID 父id
	 * @param newAttrName 属性名 
	 * @param treeValue 对应AttrValueInfo表的字段
	 * @return
	 */
	public static int addAttrInfo(String attrID, String fatherID, String newAttrName, String treeValue) {
		// 返回值
		int count = 0;
		String sql_add = "";
		if (GetConfigValue.isOracle) {
			sql_add = "insert into AttrValueInfo(ID,AttrInfoID,parentID,coding,"+treeValue+") values (Attrvalueinfo_sequence.nextval,?,?,?,?)";
		} else if(GetConfigValue.isMySQL) {
			sql_add = "insert into AttrValueInfo(ID,AttrInfoID,parentID,coding,"+treeValue+") values (" + ConstructSerialNum.getSerialID("AttrValueInfo", "id") + ",?,?,?,?)";
		}
		
		// 属性编码
		Integer attrNum = null;
		try{
			// 查询属性名是否重名
			String sql_select = "select * from AttrValueInfo where " + treeValue + " like '" + newAttrName + "%' and AttrInfoID='" + attrID + "'";
			Result rs = Database.executeQuery(sql_select);
			if (rs != null && rs.getRowCount() > 0) {
				count--;
				return count;
			}
			if ("".equals(fatherID)) {// 如果父节点为空
				fatherID = null;// 如果父节点为空，则设置为null
				// 查询最大的父节点
				String sql_selFatherID = "select coding from AttrValueInfo where AttrInfoID='" + attrID + "' and length(coding)=4 order by coding desc";
				rs = Database.executeQuery(sql_selFatherID);
				if (rs != null && rs.getRowCount() > 0) {// 该属性值已存在数据
					String num = rs.getRows()[0].get("coding") != null ? rs.getRows()[0].get("coding").toString() : null;
					attrNum = Integer.parseInt(num) + 1;
				} else {// 首次为该属性添加属性值
					attrNum = 1000;
				}
			} else {// 在父节点下添加子节点
				// 查询最大的子节点
				String sql_selectChildID = "select coding from AttrValueInfo where AttrInfoID='" + attrID + "' and coding like '" + fatherID + "%' and length(coding)=8 order by coding desc";
				rs = Database.executeQuery(sql_selectChildID);
				if (rs != null && rs.getRowCount() > 0) {// 该属性值已存在数据
					String num = rs.getRows()[0].get("coding") != null ? rs.getRows()[0].get("coding").toString() : null;
					attrNum = Integer.parseInt(num) + 1;
				} else {// 首次为该属性添加子属性值
					attrNum = Integer.parseInt(fatherID + "0001");
				}
			}
			
			count = Database.executeNonQuery(sql_add,new Object[]{attrID, fatherID, attrNum, newAttrName});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	
	/**
	 * 给属性添加属性值
	 * @param attrID 属性ID
	 * @param fatherID 父id
	 * @param newAttrName 属性名 
	 * @param treeValue 对应AttrValueInfo表的字段
	 * @return
	 */
	public static int addAttrInfo_new(String attrID, String fatherID, String newAttrName, String treeValue) {
		// 返回值
		int count = 0;
		String sql_add = "";
		if (GetConfigValue.isOracle) {
			sql_add = "insert into AttrValueInfo(ID,AttrInfoID,parentID,coding,"+treeValue+") values (Attrvalueinfo_sequence.nextval,?,?,?,?)";
		} else if(GetConfigValue.isMySQL) {
			sql_add = "insert into AttrValueInfo(ID,AttrInfoID,parentID,coding,"+treeValue+") values (" + ConstructSerialNum.getSerialID("AttrValueInfo", "id") + ",?,?,?,?)";
		}
		//属性编码
		String attrNum =newAttrName;
		try{
			// 查询属性名是否重名
			String sql_select = "select * from AttrValueInfo where " + treeValue + " like '" + newAttrName + "%' and AttrInfoID='" + attrID + "'";
			Result rs = Database.executeQuery(sql_select);
			if (rs != null && rs.getRowCount() > 0) {
				count--;
				return count;
			}
			if ("".equals(fatherID)) {// 如果父节点为空
//				fatherID = null;// 如果父节点为空，则设置为null
//				// 查询最大的父节点
//				String sql_selFatherID = "select coding from AttrValueInfo where AttrInfoID='" + attrID + "' and length(coding)=4 order by coding desc";
//				rs = Database.executeQuery(sql_selFatherID);
//				if (rs != null && rs.getRowCount() > 0) {// 该属性值已存在数据
//					String num = rs.getRows()[0].get("coding") != null ? rs.getRows()[0].get("coding").toString() : null;
//					attrNum = Integer.parseInt(num) + 1;
//				} else {// 首次为该属性添加属性值
//					attrNum = 1000;
//				}
				rs = CommonLibMetafieldmappingDAO.getConfigKey("地市编码配置", newAttrName);
				// 属性编码
				if(rs!=null&&rs.getRowCount()>0){
					attrNum= rs.getRows()[0].get("name") != null ? rs.getRows()[0].get("name").toString() : null;
				}
				if(attrNum==null){//若参数配置表中不存在所添加地市信息，不做添加处理
					return count;
				}
				
				
			} else {// 在父节点下添加子节点
//				// 查询最大的子节点
//				String sql_selectChildID = "select coding from AttrValueInfo where AttrInfoID='" + attrID + "' and coding like '" + fatherID + "%' and length(coding)=8 order by coding desc";
//				rs = Database.executeQuery(sql_selectChildID);
//				if (rs != null && rs.getRowCount() > 0) {// 该属性值已存在数据
//					String num = rs.getRows()[0].get("coding") != null ? rs.getRows()[0].get("coding").toString() : null;
//					attrNum = Integer.parseInt(num) + 1;
//				} else {// 首次为该属性添加子属性值
//					attrNum = Integer.parseInt(fatherID + "0001");
//				}
				
				rs = CommonLibMetafieldmappingDAO.getConfigKey("地市编码配置", newAttrName);
				if(rs!=null&&rs.getRowCount()>0){
					attrNum= rs.getRows()[0].get("name") != null ? rs.getRows()[0].get("name").toString() : null;
				}
				if(attrNum==null){//若参数配置表中不存在所添加地市信息，不做添加处理
					return count;
				}
			}
			count = Database.executeNonQuery(sql_add,new Object[]{attrID, fatherID, attrNum, newAttrName});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 修改属性的属性值
	 * @param attrID 属性id
	 * @param coding 属性值编码
	 * @param newAttrName 新的属性名
	 * @param treeValue 更新的列名
	 * @return
	 */
	public static int updateAttrInfo(String attrID, String coding, String newAttrName, String treeValue) {
		// 返回值
		int count = 0;
		String sql_update = "update AttrValueInfo set " + treeValue + "=? where attrInfoID=? and coding=?";
		try{
			// 查询属性名是否重名
			String sql_select = "select * from AttrValueInfo where " + treeValue + " like '" + newAttrName + "%' and AttrInfoID='" + attrID + "'";
			Result rs = Database.executeQuery(sql_select);
			if (rs != null && rs.getRowCount() > 0) {
				count--;
				return count;
			}
			count = Database.executeNonQuery(sql_update,new Object[]{newAttrName, attrID, coding});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 删除属性值
	 * @param attrID 属性id
	 * @param coding 属性值编码
	 * @return
	 */
	public static int deleteAttrInfo(String attrID, String coding) {
		// 返回值
		int count = 0;
		String sql_delete = "delete from AttrValueInfo where AttrInfoID=? and Coding like ?";
		try {
			count = Database.executeNonQuery(sql_delete,new Object[]{attrID, coding+"%"});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 构造资源配置表的列
	 * @param resourceType 资源类型
	 * @param customer 所属机构
	 * @return
	 */
	public static Result constructTableColumn(String resourceType, String customer) {
		// 返回值
		Result rs = null;
		String sql = "select * from ResourceAttrName2FieldColNum where business='" + customer + "' and resourceType like '%" + resourceType + "%'";
		try{
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 构造属性值下拉框
	 * @param id 属性id
	 * @param columnName 对应的列名
	 * @return
	 */
	public static Result constructAttrInfoCombobox(String id) {
		Result rs = null;
		String sql = "select * from AttrValueInfo where attrInfoID='"+id+"'";
		try{
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 构造属性ID获得名
	 * @param id 属性id
	 * @return
	 */
	public static Result getResourceAttrname2FieldColnum(String id) {
		Result rs = null;
		String sql = "select * from resourceattrname2fieldcolnum where id="+id;
		try{
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 根据ResourceAcessManager属性对应的列名和属性的编码找到属性的名称
	 * @param column ResourceAcessManager属性对应的列名
	 * @param coding 属性的名称
	 * @return
	 */
	public static String getAttrNameForAttrToResource(String column, String coding) {
		// 返回的属性值串
		String attrName = "";
		Result rs;
		// 根据列名查询对应ResourceAttrName2FieldColNum的信息
		// 列号
		String columnNum = column.substring(4, 5);
		// 类型
		String dataType = column.substring(6, column.length());
		String sql = "select * from ResourceAttrName2FieldColNum where columnNum='"+columnNum+"' and dataType='"+dataType+"'";
		try {
			rs = Database.executeQuery(sql);
			// ResourceAttrName2FieldColNum主键
			String id = rs.getRows()[0].get("id") != null ? rs.getRows()[0].get("id").toString() : null;
			// 类型
			String shape = rs.getRows()[0].get("shape") != null ? rs.getRows()[0].get("shape").toString() : null;
			if (shape.equals("tree")) {
				sql = "select attrvalue_"+dataType+" from AttrValueInfo where attrInfoID='"+id+"' and coding in (";
				String[] array = coding.split(",");
				for(String str : array) {
					sql += "'" + str + "',";
				}
				sql = sql.substring(0, sql.lastIndexOf(",")) + ")";
				rs = Database.executeQuery(sql);
				if (rs != null && rs.getRowCount()>0) {
					for (int i = 0; i < rs.getRowCount(); i++) {
						String attrValue = rs.getRows()[i].get("attrvalue_"+dataType) != null ? rs.getRows()[i].get("attrvalue_"+dataType).toString() : null;
						attrName += attrValue+",";
					}
					attrName = attrName.substring(0, attrName.lastIndexOf(","));
				}
			} else {
				return coding;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrName;
	}
	
	public static String getAttrNameForAttrToResource(String name, String coding, String resourceType) {
		// 返回的属性值串
		String attrName = "";
		Result rs;
		String sql = "select * from ResourceAttrName2FieldColNum where name='"+name+"' and resourceType like '%"+resourceType+"%'";
		try {
			rs = Database.executeQuery(sql);
			// ResourceAttrName2FieldColNum主键
			if(rs != null && rs.getRowCount()> 0){
				String id = rs.getRows()[0].get("id") != null ? rs.getRows()[0].get("id").toString() : null;
				// 类型
				String dataType = rs.getRows()[0].get("dataType") != null ? rs.getRows()[0].get("dataType").toString() : null;
				sql = "select attrvalue_"+dataType+" from AttrValueInfo where attrInfoID='"+id+"' and coding in (";
				String[] array = coding.split(",");
				for(String str : array) {
					sql += "'" + str + "',";
				}
				sql = sql.substring(0, sql.lastIndexOf(",")) + ")";
				rs = Database.executeQuery(sql);
				if (rs != null && rs.getRowCount()>0) {
					for (int i = 0; i < rs.getRowCount(); i++) {
						String attrValue = rs.getRows()[i].get("attrvalue_"+dataType) != null ? rs.getRows()[i].get("attrvalue_"+dataType).toString() : null;
						attrName += attrValue+",";
					}
					attrName = attrName.substring(0, attrName.lastIndexOf(","));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attrName;
	}
	
	/**
	 * 构造四层结构下拉框
	 * @return
	 */
	public static Result constructCombobox() {
		Result rs = null;
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select (industry || '->' || organization || '->' || application) as customer from M_INDUSTRYAPPLICATION2SERVICES";
		} else if(GetConfigValue.isMySQL) {
			sql = "select CONCAT(cast(industry as char),'->',cast(organization as char),'->',cast(application as char)) as customer from M_INDUSTRYAPPLICATION2SERVICES";
		}
		rs = Database.executeQuery(sql);
		return rs;
	}
	
	/**
	 * 根据资源类型和资源id查询资源所对应的属性
	 * @param resourceType 资源类型
	 * @param resourceID 资源id
	 * @return
	 */
	public static Map<String,Object> getResourceAttrs(String resourceType,String resourceID) {
		// 返回值
		Map<String,Object> map = new HashMap<String,Object>(); 
		// 词类部分要进行转换
		if ("基础".equals(resourceType)) {// 基础词库
			resourceType = "baseWord";
		} else if("子句".equals(resourceType)) {// 子句词库
			resourceType = "sentence";
		}
		// 根据资源类型找到其属性所对应的列名
		Map<String,String> colNames = new HashMap<String,String>();
		String sql = "select columnNum,dataType,name from resourceAttrName2FieldColNum where resourceType like '%" + resourceType + "%'";
		Result rs = Database.executeQuery(sql);
		// 遍历属性列，获得属性值
		sql = "select ";
		if (rs != null && rs.getRowCount()>0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				String columnNum = rs.getRows()[i].get("columnNum").toString();
				String dataType = rs.getRows()[i].get("dataType").toString();
				String name = rs.getRows()[i].get("name").toString();
				sql += "attr" + columnNum + "_" + dataType + ",";
				colNames.put("attr" + columnNum + "_" + dataType,name);
			}
		} else {
			return map;
		}
		sql = sql.substring(0, sql.lastIndexOf(",")) + " from resourceAcessManager where resourceid like '" + resourceType + "_" + resourceID + "'";
		rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount()>0) {
			// 所有的列名
			String[] columns = rs.getColumnNames();
			for (String column : columns) {
				Object value = rs.getRows()[0].get(column);
				map.put(colNames.get(column.toLowerCase()), value);
			}
		}
		
		return map;
	}
}
