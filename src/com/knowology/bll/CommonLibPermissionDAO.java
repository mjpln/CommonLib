package com.knowology.bll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.Bean.Role;
import com.knowology.Bean.RoleResourceAccessRule;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

public class CommonLibPermissionDAO {
	
	public static Logger logger = Logger.getLogger("sqllog");


	public static HashMap<String, ArrayList<String>> resourseAccess(
			String userid, String resourceTableName, String operationPermissions) {
		HashMap<String, ArrayList<String>> resultMap = new HashMap<String, ArrayList<String>>();
		// 获取角色列表
		List<Role> roleList = UserManagerDAO.getRoleListByUserId(userid);
		// 获得该操作的所有资源规则
		List<RoleResourceAccessRule> ruleList = RoleManager
				.getRolesRuleByOperate(roleList, resourceTableName,
						operationPermissions);
		// List<RoleResourceAccessRule> ruleList =
		// RoleManager.getRolesRuleByOperate(roleList, "service", "S");
		if (!ruleList.isEmpty()) {// 该权限有对应的操作规则
			// 获得用户能操作的资源ID-->resourceIDList sx
			List<String> resourceIDList = new ArrayList<String>();
			for (RoleResourceAccessRule rule : ruleList) {
				// 根据属性得到能够操作的所有资源id
				HashMap<String, String> accessResourceMap = (HashMap<String, String>) rule
						.getAccessResourceMap();
				Iterator iter = accessResourceMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					String val = (String) entry.getValue();
					ArrayList<String> cityList;
					if (resultMap.containsKey(key)) {
						cityList = resultMap.get(key);
					} else {
						cityList = new ArrayList<String>();
					}
					String[] cityArray = val.split(",");
					for (int i = 0; i < cityArray.length; i++) {
						// if(!cityList.contains(cityArray[i]))
						cityList.add(cityArray[i]);
					}
					cityList = new ArrayList<String>(new HashSet<String>(
							cityList));
					Collections.sort(cityList);
					resultMap.put(key, cityList);
				}
			}
		}
		return resultMap;
	}

	public static HashMap<String, ArrayList<String>> resourseAccess(
			List<Role> roleList, String resourceTableName,
			String operationPermissions) {
		HashMap<String, ArrayList<String>> resultMap = new HashMap<String, ArrayList<String>>();
		// 获得该操作的所有资源规则
		List<RoleResourceAccessRule> ruleList = RoleManager
				.getRolesRuleByOperate(roleList, resourceTableName,
						operationPermissions);
		// List<RoleResourceAccessRule> ruleList =
		// RoleManager.getRolesRuleByOperate(roleList, "service", "S");
		if (!ruleList.isEmpty()) {// 该权限有对应的操作规则
			// 获得用户能操作的资源ID-->resourceIDList sx
			List<String> resourceIDList = new ArrayList<String>();
			for (RoleResourceAccessRule rule : ruleList) {
				// 根据属性得到能够操作的所有资源id
				HashMap<String, String> accessResourceMap = (HashMap<String, String>) rule
						.getAccessResourceMap();
				Iterator iter = accessResourceMap.entrySet().iterator();
				while (iter.hasNext()) {
					Map.Entry entry = (Map.Entry) iter.next();
					String key = (String) entry.getKey();
					String val = (String) entry.getValue();
					ArrayList<String> cityList;
					if (resultMap.containsKey(key)) {
						cityList = resultMap.get(key);
					} else {
						cityList = new ArrayList<String>();
					}
					String[] cityArray = val.split(",");
					for (int i = 0; i < cityArray.length; i++) {
						// if(!cityList.contains(cityArray[i]))
						cityList.add(cityArray[i]);
					}
					cityList = new ArrayList<String>(new HashSet<String>(
							cityList));
					resultMap.put(key, cityList);
				}
			}
		}
		return resultMap;
	}

	/**
	 * 根据业务名查询业务树
	 * 
	 * @param roleList
	 *            角色集合
	 * @param parentID
	 *            父业务id
	 * @param brand
	 *            业务根
	 * @param service
	 *            业务名
	 * @return
	 */
	public static Result getServiceForTree(List<Role> roleList,
			String parentID, String brand, String service) {
		Result rs = null;
		String sql = "";

		if (GetConfigValue.isOracle) {
			if (brand.endsWith("主题")) {// 当为个性化业务时，不做权限判断
				if (parentID != null && !"".equals(parentID)) {
					sql = "SELECT * FROM service start " + "WITH service='"
							+ service + "'"
							+ " AND cityid IN (284) and brand in(" + brand
							+ ") and parentid =" + parentID
							+ " connect BY nocycle prior parentid=serviceid";
				} else {
					sql = "SELECT * FROM service start " + "WITH service='"
							+ service + "'"
							+ " AND cityid IN (284) and brand in(" + brand
							+ ") connect BY nocycle prior parentid = serviceid";
				}
				rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
				return rs;
			}
			for (Role role : roleList) {// 判断是否为云平台角色
				if (role.getBelongCom().equals("全行业")) {
					if (parentID != null && !"".equals(parentID)) {
						sql = "SELECT * FROM service start "
								+ "WITH service='"
								+ service
								+ "'"
								+ " AND cityid IN (284) and brand in("
								+ brand
								+ ") and parentid ="
								+ parentID
								+ " connect BY nocycle prior parentid=serviceid";
					} else {
						sql = "SELECT * FROM service start "
								+ "WITH service='"
								+ service
								+ "'"
								+ " AND cityid IN (284) and brand in("
								+ brand
								+ ") connect BY nocycle prior parentid = serviceid";
					}

					rs = Database.executeQuery(sql);
					
					//文件日志
					GlobalValue.myLog.info( sql );
					
					return rs;
				}
			}
			// 获得该操作的所有资源规则
			List<RoleResourceAccessRule> ruleList = RoleManager
					.getRolesRuleByOperate(roleList, "service", "S");

			if (!ruleList.isEmpty()) {// 该权限有对应的操作规则
				// 获得用户能操作的资源ID-->resourceIDList
				List<String> resourceIDList = new ArrayList<String>();
				for (RoleResourceAccessRule rule : ruleList) {
					// 根据属性得到能够操作的所有资源id
					List<String> serviceIDByAttr = ResourceAccessOper
							.searchResIDByAttrs(rule.getAccessResourceMap(),
									"service");
					if (!serviceIDByAttr.isEmpty()) {// 根据属性查询出相关资源
						resourceIDList.addAll(serviceIDByAttr);
					}
					// 压入用户指定的资源ID
					List<String> resourceNames = rule.getResourceNames();
					if (!resourceNames.isEmpty()) {// 用户指定资源名
						List<String> serviceIDByServiceName = ResourceAccessOper
								.getResourceIDByName(resourceNames.toArray(),
										"service");
						if (!serviceIDByServiceName.isEmpty()) {
							resourceIDList.addAll(serviceIDByServiceName);
						}
					}
					// 判断是否关联子业务
					if (rule.getIsRelateChild().equals("Y")) {// 关联子业务
						resourceIDList = ResourceAccessOper
								.getChildService(resourceIDList.toArray());
					}
				}
				// 去重
				resourceIDList = new ArrayList<String>(new HashSet<String>(
						resourceIDList));

				// 以下属于KM本身逻辑，只是把上面查到的ID作为where条件，具体为代码【I】
				if (parentID != null && !"".equals(parentID)) {
					sql = "select * from (SELECT * FROM service start "
							+ "WITH service='" + service + "'"
							+ " AND cityid IN (284) and brand in(" + brand
							+ ") and parentid =" + parentID
							+ " connect BY nocycle prior parentid=serviceid)";
				} else {
					sql = "select * from (SELECT * FROM service start "
							+ "WITH service='"
							+ service
							+ "'"
							+ " AND cityid IN (284) and brand in("
							+ brand
							+ ") connect BY nocycle prior parentid = serviceid)";
				}
				if (!resourceIDList.isEmpty()) { // 【I】
					sql += " where (";
					for (String resourceID : resourceIDList) {
						sql += "serviceID=" + resourceID + " or ";
					}
					sql += "serviceID=0)";
				}

				if (brand.contains("'个性化业务'")) {
					if (parentID != null && !"".equals(parentID)) {
						sql = "select * from ("
								+ "SELECT * FROM service start "
								+ "WITH service='"
								+ service
								+ "'"
								+ " AND cityid IN (284) and brand='个性化业务' and parentid ="
								+ parentID
								+ " connect BY nocycle prior parentid=serviceid"
								+ " union " + sql
								+ ") rr order by rr.serviceID desc";
					} else {
						sql = "select * from ("
								+ "SELECT * FROM service start "
								+ "WITH service='"
								+ service
								+ "'"
								+ " AND cityid IN (284) and brand='个性化业务' connect BY nocycle prior parentid = serviceid"
								+ " union " + sql
								+ ") rr order by rr.serviceID desc";
					}
				}

				rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
			}
		} else if (GetConfigValue.isMySQL) {//
			String serviceids = null;
			String serviceidArray[] = {};
			if (brand.endsWith("主题")) {// 当为个性化业务时，不做权限判断
				if (parentID != null && !"".equals(parentID)) {
					sql = "select getServiceparentIdList('" + service + "','"
							+ brand.replace("'", "") + "','" + parentID
							+ "') as serviceids";
				} else {
					sql = "select getServiceparentIdList('" + service + "','"
							+ brand.replace("'", "") + "',null) as serviceids";
				}
				rs = Database.executeQuery(sql);
				if (rs != null && rs.getRowCount() > 0) {
					serviceids = rs.getRows()[0].get("serviceids").toString();
					serviceidArray = serviceids.split(",");
					for (int i = 0; i < serviceidArray.length; i++) {
						if (i == 0) {
							sql += "select * from service where serviceid="
									+ serviceidArray[i] + "";
						} else {
							sql += " UNION ALL SELECT * FROM service WHERE serviceid="
									+ serviceidArray[i] + "";
						}
					}
					rs = Database.executeQuery(sql);
					
					//文件日志
					GlobalValue.myLog.info( sql );
				}
				return rs;
			}
			for (Role role : roleList) {// 判断是否为云平台角色
				if (role.getBelongCom().equals("全行业")) {
					// 当为个性化业务时，不做权限判断
					if (parentID != null && !"".equals(parentID)) {
						sql = "select getServiceparentIdList('" + service
								+ "','" + brand.replace("'", "") + "','"
								+ parentID + "') as serviceids";
					} else {
						sql = "select getServiceparentIdList('" + service
								+ "','" + brand.replace("'", "")
								+ "',null) as serviceids";
					}
					rs = Database.executeQuery(sql);
					
					//文件日志
					GlobalValue.myLog.info( sql );
					
					if (rs != null && rs.getRowCount() > 0) {
						serviceids = rs.getRows()[0].get("serviceids")
								.toString();
						serviceidArray = serviceids.split(",");
						sql = "";
						for (int i = serviceidArray.length - 1; i >= 0; i--) {
							if (i == serviceidArray.length - 1) {
								sql += "select * from service where serviceid="
										+ serviceidArray[i] + "";
							} else {
								sql += " UNION ALL SELECT * FROM service WHERE serviceid="
										+ serviceidArray[i] + "";
							}
						}
						rs = Database.executeQuery(sql);
						
						//文件日志
						GlobalValue.myLog.info( sql );
						
					}
					return rs;

				}
			}
			// 获得该操作的所有资源规则
			List<RoleResourceAccessRule> ruleList = RoleManager
					.getRolesRuleByOperate(roleList, "service", "S");
			if (!ruleList.isEmpty()) {// 该权限有对应的操作规则
				// 该操作类型用户能够操作的资源
				List<String> resourceIDList = new ArrayList<String>();
				for (RoleResourceAccessRule rule : ruleList) {
					// 根据属性得到能够操作的所有资源id
					List<String> serviceIDByAttr = ResourceAccessOper
							.searchResIDByAttrs(rule.getAccessResourceMap(),
									"service");
					if (!serviceIDByAttr.isEmpty()) {// 根据属性查询出相关资源
						resourceIDList.addAll(serviceIDByAttr);
					}
					// 压入用户指定的资源ID
					List<String> resourceNames = rule.getResourceNames();
					if (!resourceNames.isEmpty()) {// 用户指定资源名
						List<String> serviceIDByServiceName = ResourceAccessOper
								.getResourceIDByName(resourceNames.toArray(),
										"service");
						if (!serviceIDByServiceName.isEmpty()) {
							resourceIDList.addAll(serviceIDByServiceName);
						}
					}
					// 判断是否关联子业务
					if (rule.getIsRelateChild().equals("Y")) {// 关联子业务
						resourceIDList = ResourceAccessOper
								.getChildService(resourceIDList.toArray());
					}
				}
				// 去重
				resourceIDList = new ArrayList<String>(new HashSet<String>(
						resourceIDList));

				if (parentID != null && !"".equals(parentID)) {
					sql = "select getServiceparentIdList('" + service + "','"
							+ brand.replace("'", "") + "','" + parentID
							+ "') as serviceids";
				} else {
					sql = "select getServiceparentIdList('" + service + "','"
							+ brand.replace("'", "") + "',null) as serviceids";
				}
				rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
				if (rs != null && rs.getRowCount() > 0) {
					serviceids = rs.getRows()[0].get("serviceids").toString();
					serviceidArray = serviceids.split(",");

				}
				List serviceidList = null;
				if (!resourceIDList.isEmpty()) {
					serviceidList = Arrays.asList(serviceidArray);
					serviceidList.retainAll(resourceIDList);// 取serviceid 交集
				}

				if (brand.contains("'个性化业务'")) {
					if (parentID != null && !"".equals(parentID)) {
						sql = "select getServiceparentIdList('" + service
								+ "','个性化业务','" + parentID + "') as serviceids";
					} else {
						sql = "select getServiceparentIdList('" + service
								+ "','个性化业务',null) as serviceids";
					}
					rs = Database.executeQuery(sql);
					
					//文件日志
					GlobalValue.myLog.info( sql );
					
					if (rs != null && rs.getRowCount() > 0) {
						serviceids = rs.getRows()[0].get("serviceids")
								.toString();
						serviceidArray = serviceids.split(",");
						serviceidList.addAll(Arrays.asList(serviceidArray));
					}
				}
				for (int i = 0; i < serviceidList.size(); i++) {
					if (i == 0) {
						sql += "select * from service where serviceid="
								+ serviceidList.get(i) + "";
					} else {
						sql += " UNION ALL SELECT * FROM service WHERE serviceid="
								+ serviceidList.get(i) + "";
					}
				}

				rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
			}
		}
		return rs;
	}

	/**
	 * 获得业务数据源
	 * 
	 * @param userid
	 *            用户ID
	 *@param resourceType
	 *            资源类型
	 *@return
	 *@returnType List<Map<String,String>>
	 */
	public static List<Map<String, String>> getServiceResource(String userid,
			String resourceType) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		Result rs = CommonLibServiceDAO.getServiceInfoByPid("1832282");
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				map = new HashMap<String, String>();
				map.put("id", rs.getRows()[i].get("serviceid") == null ? ""
						: rs.getRows()[i].get("serviceid").toString());
				map.put("pid", rs.getRows()[i].get("parentid") == null ? ""
						: rs.getRows()[i].get("parentid").toString());
				map.put("name", rs.getRows()[i].get("service") == null ? ""
						: rs.getRows()[i].get("service").toString());
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * 保存资源
	 * 
	 * @param roleid
	 *@param resourceid
	 *@param resourceType
	 *@param serviceType
	 *@return
	 *@returnType boolean
	 */
	public static boolean saveResource(String roleid, String resourceid[],
			String resourceType, String serviceType) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara;
		String id = null;
		// 清空角色对应资源
		String sql = "delete from ROLE_RESOURCE where  ROLEID =? and RESOURCETYPE=? and SERVICETYPE =?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定参数
		lstpara.add(roleid);
		lstpara.add(resourceType);
		lstpara.add(serviceType);
		// 将SQL语句放入集合中
		lstsql.add(sql.toString());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 新建角色对应资源
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		sql = "insert into ROLE_RESOURCE(ID,ROLEID,RESOURCEID,RESOURCETYPE,SERVICETYPE,OPERATIONTYPE) values(?,?,?,?,?,?)";
		for (int i = 0; i < resourceid.length; i++) {
			if (GetConfigValue.isOracle) {
				System.out.println("$$$$$$$$$$$$$$$oracle");
				id = ConstructSerialNum.GetOracleNextValNew(
						"SEQ_ROLERESOURCE_ID", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				System.out.println("$$$$$$$$$$$$$$$mysql");
				id = ConstructSerialNum.getSerialIDNew("role_resourceid", "id",
						bussinessFlag);
			}
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定参数
			lstpara.add(id);
			lstpara.add(roleid);
			lstpara.add(resourceid[i]);
			lstpara.add(resourceType);
			lstpara.add(serviceType);
			lstpara.add("S");
			// 将SQL语句放入集合中
			lstsql.add(sql.toString());
			// 将定义的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		// 判断事务处理结果
		if (c > 0) {
			// 事务处理成功,返回true，表示新增成功
			return true;
		} else {
			// 事务处理失败,返回false，表示新增失败
			return false;
		}
	}
	
	/**
	 * 保存资源和操作类型
	 * 
	 * @param roleid
	 *@param resourceid
	 *@param resourceType
	 *@param serviceType
	 *@return
	 *@returnType boolean
	 */
	public static boolean saveResourceWithOperType(String roleid, String resourceid[],
			String resourceType, String serviceType,String operationType) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara;
		String id = null;
		// 清空角色对应资源
		String sql = "delete from ROLE_RESOURCE where  ROLEID =? and RESOURCETYPE=? and SERVICETYPE =? and resourceid in ("
						+StringUtils.join(resourceid, ",")+")";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定参数
		lstpara.add(roleid);
		lstpara.add(resourceType);
		lstpara.add(serviceType);
		// 将SQL语句放入集合中
		lstsql.add(sql.toString());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 新建角色对应资源
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		sql = "insert into ROLE_RESOURCE(ID,ROLEID,RESOURCEID,RESOURCETYPE,SERVICETYPE,OPERATIONTYPE) values(?,?,?,?,?,?)";
		for (int i = 0; i < resourceid.length; i++) {
			if (GetConfigValue.isOracle) {
				System.out.println("$$$$$$$$$$$$$$$oracle");
				id = ConstructSerialNum.GetOracleNextValNew(
						"SEQ_ROLERESOURCE_ID", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				System.out.println("$$$$$$$$$$$$$$$mysql");
				id = ConstructSerialNum.getSerialIDNew("role_resourceid", "id",
						bussinessFlag);
			}
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定参数
			lstpara.add(id);
			lstpara.add(roleid);
			//过滤空资源id;
			if(StringUtils.isEmpty(resourceid[i]))
				continue;
			lstpara.add(resourceid[i]);
			lstpara.add(resourceType);
			lstpara.add(serviceType);
			lstpara.add(operationType);
			// 将SQL语句放入集合中
			lstsql.add(sql.toString());
			// 将定义的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		// 判断事务处理结果
		if (c > 0) {
			// 事务处理成功,返回true，表示新增成功
			return true;
		} else {
			// 事务处理失败,返回false，表示新增失败
			return false;
		}
	}
	
	/**
	 * 更新资源操作类型
	 *@param roleid
	 *@param resourceid
	 *@param resourceType
	 *@param serviceType
	 *@param operationType
	 *@return 
	 *@returnType boolean 
	 */
	public static boolean updateResourceOperationTypeCascade(String roleid, String resourceid,
			String resourceType, String serviceType,String operationType) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara;
		String id = null;
		// 清空角色对应资源
		String sql = "update  ROLE_RESOURCE  set operationType =? where  ROLEID =? and resourceid in(SELECT serviceid FROM (select * from service start WITH serviceid in(?) connect BY nocycle prior serviceid=parentid) aaa ) and RESOURCETYPE=? and SERVICETYPE =? ";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定参数
		lstpara.add(operationType);
		lstpara.add(roleid);
		lstpara.add(resourceid);
		lstpara.add(resourceType);
		lstpara.add(serviceType);
		// 将SQL语句放入集合中
		lstsql.add(sql.toString());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		// 判断事务处理结果
		if (c > 0) {
			// 返回true，修改成功
			return true;
		} else {
			// 返回false，修改失败
			return false;
		}
	}
	
	
	/**
	 * 更新资源操作类型
	 *@param roleid
	 *@param resourceid
	 *@param resourceType
	 *@param serviceType
	 *@param operationType
	 *@return 
	 *@returnType boolean 
	 */
	public static boolean updateResourceOperationType(String roleid, String resourceid,
			String resourceType, String serviceType,String operationType) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara;
		String id = null;
		// 清空角色对应资源
		String sql = "update  ROLE_RESOURCE  set operationType =? where  ROLEID =? and resourceid =? and RESOURCETYPE=? and SERVICETYPE =?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定参数
		lstpara.add(operationType);
		lstpara.add(roleid);
		lstpara.add(resourceid);
		lstpara.add(resourceType);
		lstpara.add(serviceType);
		// 将SQL语句放入集合中
		lstsql.add(sql.toString());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		// 判断事务处理结果
		if (c > 0) {
			// 返回true，修改成功
			return true;
		} else {
			// 返回false，修改失败
			return false;
		}
	}
	
	/**
	 * 获得业务数据源
	 * 
	 *@param userid
	 *            用户ID
	 *@param resourceType
	 *            资源类型
	 *@param serviceType
	 *            商家标识
	 *@param cityCode 
	 *           城市编码集合
	 *@return
	 *@returnType List<Map<String,String>>
	 */
	public static List<Map<String, String>> getServiceResource(String userid,
			String resourceType, String serviceType, List<String> cityCode) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> map;
		Result rs = null;
		String ioa[] = serviceType.split("->");
		List<String> roleidList = UserManagerDAO.getRoleIDListByUserId(userid);
		List<String> cityList = new ArrayList<String>();
		List<String> parentNameList = new ArrayList<String>();
		// cityCode 为空后者包含全国，不做city过滤，加载所有角色关联资源
		if (cityCode != null) {
			if (cityCode.size() != 0) {
				if (!cityCode.contains("全国")) {
					cityCode.add("全国");
					cityList.addAll(cityCode);
				}
			}
		}
		String parentName = "";
		if ("structuredknowledge".equals(resourceType)) {
			parentNameList.add(ioa[1] + "结构化");
		} else if ("querymanage".equals(resourceType)) {
			parentNameList.add(ioa[1] + "问题库");
		} else if ("scenariosrules".equals(resourceType)) {
			parentNameList.add(ioa[1] + "场景");
		}
		Map<String, String> serviceDic = getServiceDic(parentNameList);
		GlobalValue.myLog.info(parentNameList.toString()+"父业务字典->"+serviceDic);
		Map<String, Map<String, String>> resourceDic = getResourceDic(
				roleidList, resourceType, serviceType, cityList);
		GlobalValue.myLog.info("查询资源表数据->"+resourceDic);
		return getResourceFromDic(serviceDic, resourceDic);
	}

	/**
	 *比较资源数据和业务数据
	 * 
	 * @param serviceDic
	 *@param resourceDic
	 *@return
	 *@returnType List<Map<String,String>>
	 */
	private static List<Map<String, String>> getResourceFromDic(
			Map<String, String> serviceDic,
			Map<String, Map<String, String>> resourceDic) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> tempMap = new HashMap<String, String>();
		for (Map.Entry<String, Map<String, String>> entry : resourceDic
				.entrySet()) {
			String serviceid = entry.getKey();
			Map<String, String> map = entry.getValue();
			String parentid = map.get("parentid");
			String service = map.get("service");
			tempMap = new HashMap<String, String>();
			String newPid = getParentid(parentid, serviceDic, resourceDic);
			tempMap.put("id", serviceid);
			tempMap.put("pid", newPid);
			tempMap.put("name", service);
			list.add(tempMap);
		}
		sort("ASC", list, "id");
		GlobalValue.myLog.info(list.toString());
		return list;
	}

	/**
	 * List<Map<String, String>> 排序
	 * @param sortFlag
	 *            (ASC:升序；DESC:降序)
	 * @param doubleList
	 * 
	 * @param sortField
	 *            根据那个字段排序
	 */
	private static void sort(String sortFlag,
			List<Map<String, String>> doubleList, String sortField) {
		int len2 = doubleList.size();
		for (int i = 0; i < len2; i++) {
			for (int j = 0; j < i; j++) {
				String one = doubleList.get(j).get(sortField);
				String two = doubleList.get(i).get(sortField);
				double oneL = str2double(one);
				double twoL = str2double(two);

				if ("DESC".equalsIgnoreCase(sortFlag)) {
					if (oneL < twoL) {
						swap(doubleList, i, j);
					}
				} else if ("ASC".equalsIgnoreCase(sortFlag)) {
					if (oneL > twoL) {
						swap(doubleList, i, j);
					}
				}
			}
		}
	}

	/**
	 * @param one
	 * @return
	 */
	private static double str2double(String str) {
		// TODO Auto-generated method stub
		str = str.replaceAll("%", "");
		Double d = Double.parseDouble(str);
		return d;
	}

	// 交换位置
	private static void swap(List<Map<String, String>> list, int j, int i) {
		// TODO Auto-generated method stub
		Map<String, String> emp;
		emp = list.get(j);
		list.set(j, list.get(i));
		list.set(i, emp);
	}

	/**
	 *比较资源数据和业务数据
	 * 
	 * @param serviceDic
	 *@param resourceDic
	 *@return
	 *@returnType Object
	 */
	private static Object getResourceInfo(Map<String, String> serviceDic,
			Map<String, Map<String, String>> resourceDic) {
		JSONObject jsonObj = new JSONObject();
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String, String> tempMap = null;
		for (Map.Entry<String, Map<String, String>> entry : resourceDic
				.entrySet()) {
			String serviceid = entry.getKey();
			Map<String, String> map = entry.getValue();
			String parentid = map.get("parentid");
			String service = map.get("service");
			String city = map.get("city");
			String operationtype = map.get("operationtype");
			tempMap = new HashMap<String, String>();
			String newPid = getParentid(parentid, serviceDic, resourceDic);
			tempMap.put("id", serviceid);
			tempMap.put("name", service);
			tempMap.put("operationtype", operationtype);
			if (!"0".equals(newPid) && !"0.000".equals(newPid)) {
				tempMap.put("_parentId", newPid);
			}
			
			list.add(tempMap);
		}
		sort("ASC", list, "id");
		JSONArray jsonArr = (JSONArray) JSONObject.toJSON(list);
		jsonObj.put("total", resourceDic.size());
		jsonObj.put("rows", jsonArr);
		return jsonObj;
		
	}

	/**
	 *@description 递归获取父ID
	 *@param parentid
	 *@param serviceDic
	 *@param resourceDic
	 *@return
	 *@returnType String
	 */
	private static String getParentid(String parentid,
			Map<String, String> serviceDic,
			Map<String, Map<String, String>> resourceDic) {
		String newPid = "";
		if ("0".equals(parentid)) {
			return "0";
		}
		else if ("0.000".equals(parentid)) {
			return "0.000";
		}		
		if (resourceDic.containsKey(parentid)) {
			newPid = parentid;
		} else {
			String pid = serviceDic.get(parentid);
			System.out.println("!@#$parentid="+parentid+";pid="+pid);//GHJ
			return getParentid(pid, serviceDic, resourceDic);
		}
		return newPid;
	}

	/**
	 *获得资源操作类型
	 * 
	 * @param roleid
	 *@param resourceType
	 *@param serviceType
	 *@return
	 *@returnType Object
	 */
	public static Object getResourceOperation(String roleid,
			String resourceType, String serviceType) {
		List<String> roleidList = new ArrayList<String>();
		List<String> cityList = new ArrayList<String>();
		List<String> parentNameList = new ArrayList<String>();
		String ioa[] = serviceType.split("->");
		String parentName = "";
		if ("structuredknowledge".equals(resourceType)) {
			parentNameList.add(ioa[1] + "结构化");
		} else if ("querymanage".equals(resourceType)) {
			parentNameList.add(ioa[1] + "问题库");
		} else if ("scenariosrules".equals(resourceType)) {
			parentNameList.add(ioa[1] + "场景");
		}else if("service".equals(resourceType)){
			Result rs = CommonLibIndustryApplicationToServicesDAO
			.getIndustryapplicationToServicesInfo(ioa[0],ioa[1],ioa[2],"是");
			if (rs != null && rs.getRowCount() > 0) {
				String serviceRoot = rs.getRows()[0].get("serviceroot").toString();
				parentNameList.addAll(Arrays.asList(serviceRoot.split("\\|")));
				parentNameList.add(ioa[1] + "问题库");
				parentNameList.add(ioa[1] + "场景");
				parentNameList.add(ioa[1] + "结构化");
			} 
		}
		roleidList.add(roleid);
		Map<String, String> serviceDic = getServiceDic(parentNameList);
		Map<String, Map<String, String>> resourceDic = getResourceDic(
				roleidList, resourceType, serviceType, cityList);
		return getResourceInfo(serviceDic, resourceDic);

	}

	/**
	 *查询资源源数据
	 * 
	 * @param roleidList
	 *@param resourceType
	 *@param serviceType
	 *@param cityCode
	 *@return
	 *@returnType Result
	 */
	private static Result getResourceFromDB2(List<String> roleidList,
			String resourceType, String serviceType, List<String> cityCode) {
		Result rs = null;
		String sql = "select * from service where serviceid in( select resourceid from ROLE_RESOURCE where resourcetype =lower('"
				+ resourceType + "') and servicetype = '" + serviceType + "' ";
		for (int i = 0; i < roleidList.size(); i++) {
			if (i == 0) {
				sql = sql + "and ( roleid =" + roleidList.get(i);
			} else {
				sql = sql + " or roleid =" + roleidList.get(i);
			}
		}
		sql = sql + " ))";
		if (cityCode.size() > 0) {
			for (int j = 0; j < cityCode.size(); j++) {
				if (j == 0) {
					sql = sql + " and(  city  like '%" + cityCode.get(j) + "%'";
				} else {
					sql = sql + " or city  like '%" + cityCode.get(j) + "%'";
				}
			}
			sql = sql + " )";
		}

		rs = Database.executeQuery(sql);
		return rs;
	}

	/**
	 *查询资源源数据
	 * 
	 * @param roleidList
	 *@param resourceType
	 *@param serviceType
	 *@param cityCode
	 *@return
	 *@returnType Result
	 */
	 public static Result getResourceFromDB(List<String> roleidList,
			String resourceType, String serviceType, List<String> cityCode) {
		Result rs = null;
		String sql = "select * from service s,( select * from ROLE_RESOURCE where resourcetype =lower('"
				+ resourceType + "') and servicetype = '" + serviceType + "' ";
		for (int i = 0; i < roleidList.size(); i++) {
			if (i == 0) {
				sql = sql + "and ( roleid =" + roleidList.get(i);
			} else {
				sql = sql + " or roleid =" + roleidList.get(i);
			}
		}
		sql = sql + " )) r where s.serviceid = r.resourceid ";
		if(cityCode!=null){
			if (cityCode.size() > 0) {
				for (int j = 0; j < cityCode.size(); j++) {
					if (j == 0) {
						sql = sql + " and(  city  like '%" + cityCode.get(j) + "%'";
					} else {
						sql = sql + " or city  like '%" + cityCode.get(j) + "%'";
					}
				}
				sql = sql + " ) ";
			}	
		}
	
		sql = sql + " order by s.serviceid ";
		rs = Database.executeQuery(sql);
		GlobalValue.myLog.info("查询资源表数据sql->"+sql);
		return rs;
	}

	/**
	 *查询资源源数据
	 * 
	 * @param roleidList
	 *@param resourceType
	 *@param resourceid
	 *@return
	 *@returnType Result
	 */
	private static Result getResourceFromDB(List<String> roleidList,
			String resourceType, String resourceid) {
		Result rs = null;
		String sql = "select resourceid, operationtype from ROLE_RESOURCE where resourcetype ='"
				+ resourceType + "' and resourceid = '" + resourceid + "' ";
		for (int i = 0; i < roleidList.size(); i++) {
			if (i == 0) {
				sql = sql + "and ( roleid =" + roleidList.get(i);
			} else {
				sql = sql + " or roleid =" + roleidList.get(i);
			}
		}
		sql = sql + " )";
		rs = Database.executeQuery(sql);
		return rs;
	}

	/**
	 *获得业务ID&父业务ID字典
	 * 
	 * @param pname
	 *@return
	 *@returnType Map<String,String>
	 */
	private static Map<String, String> getServiceDic(List<String> pname) {
		Map<String, String> map = new HashMap<String, String>();
		Result rs = CommonLibServiceDAO.getServiceInfoByPname(pname);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				map.put(rs.getRows()[i].get("serviceid") == null ? "" : rs
						.getRows()[i].get("serviceid").toString(),
						rs.getRows()[i].get("parentid") == null ? "" : rs
								.getRows()[i].get("parentid").toString());
			}
		}
		return map;
	}

	/**
	 *获得资源字典
	 * 
	 * @param roleidList
	 *@param resourceType
	 *@param serviceType
	 *@param cityList
	 *@return
	 *@returnType Map<String,Map<String,String>>
	 */
	private static Map<String, Map<String, String>> getResourceDic(
			List<String> roleidList, String resourceType, String serviceType,
			List<String> cityList) {
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		Map<String, String> tempmap = new HashMap<String, String>();
		Result rs = getResourceFromDB(roleidList, resourceType, serviceType,
				cityList);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				tempmap = new HashMap<String, String>();
				tempmap.put("parentid",
						rs.getRows()[i].get("parentid") == null ? "" : rs
								.getRows()[i].get("parentid").toString());
				tempmap.put("service",
						rs.getRows()[i].get("service") == null ? "" : rs
								.getRows()[i].get("service").toString());
				tempmap.put("operationtype", rs.getRows()[i]
						.get("operationtype") == null ? "" : rs.getRows()[i]
						.get("operationtype").toString());
				tempmap.put("city", rs.getRows()[i].get("city") == null ? "全国"
						: rs.getRows()[i].get("city").toString());
				map.put(rs.getRows()[i].get("serviceid") == null ? "" : rs
						.getRows()[i].get("serviceid").toString(), tempmap);
			}
		}
		return map;
	}

	/**
	 *判断是否具备资源操作权限
	 *@param userid 用户ID
	 *@param resourceType 资源类型
	 *@param resourceid 资源ID
	 *@param operationtype 操作类型 
	 *@return 
	 */
	public static boolean isHaveOperationPermission(String userid,String resourceType,String resourceid,String operationType,String wordpatid){
		List<String> roleidList = UserManagerDAO.getRoleIDListByUserId(userid);
		if("wordpat".equalsIgnoreCase(resourceType)){
			return _isHaveOperationPermission(userid,resourceType,resourceid,operationType,wordpatid);
		}else{
			return  _isHaveOperationPermission(roleidList,resourceType,resourceid,operationType);
		}
		
	}
	
	/**
	 *判断是否具备资源操作权限
	 *@param userid 用户ID
	 *@param resourceType 资源类型
	 *@param resourceid 资源ID
	 *@param operationtype 操作类型 
	 *@param robotid 实体机器人ID
	 *@return 
	 */
	public static boolean isHaveOperationPermissionByRobotid(String userid,String resourceType,String resourceid,String operationType,String robotid){
		List<String> robotidList = new ArrayList<String>();
		List<String> roleidList = UserManagerDAO.getRoleIDListByUserId(userid);
		HashMap<String, ArrayList<String>> resourseMap = CommonLibPermissionDAO.resourseAccess(userid, resourceType, "S");
		if(resourseMap.containsKey("实体机器人ID")){//角色已配置实体机器人ID属性,则做robotid过滤判断
			robotidList= resourseMap.get("实体机器人ID");
			if(robotidList.contains(robotid)){//如包含robotid,做具体资源权限判断
			return  _isHaveOperationPermission(roleidList,resourceType,resourceid,operationType);
			}
		}else{//反之，不做robotid过滤判断，仅限资源操作权限判断
			return _isHaveOperationPermission(roleidList,resourceType,resourceid,operationType);
		}
	   return false;
	}
	
	/**
	 *判断是否具备资源操作权限
	 *@param roleidList
	 *@param resourceType
	 *@param resourceid
	 *@param operationType
	 *@return 
	 */
	private static Boolean _isHaveOperationPermission(List<String> roleidList,String resourceType,String resourceid,String operationType){
		//如包含robotid,做具体资源权限判断
		Map<String,String> map = getResourceOperationtype(roleidList,resourceType,resourceid);
		operationType = operationType.toUpperCase();
		if(map.containsKey(resourceid)){
			String _operationType = map.get(resourceid);
			if(_operationType.contains(operationType)){
				return true;
			}else{
				return false;
			}
		}
	   return false;
	}
	
	public static boolean _isHaveOperationPermission(String userid,String resourceType,String resourceid,String operationType,String wordpatid){
		Map<String, ArrayList<String>> map = CommonLibPermissionDAO.resourseAccess(userid, resourceType, "S");
		List<String> roleidList = UserManagerDAO.getRoleIDListByUserId(userid);
		Map<String,String> serivceDic = new HashMap<String,String>();
		if(map.containsKey("受限资源")){
			List<String> list = map.get("受限资源");
			if(list.size()>0){
				Result rs = CommonLibServiceDAO.getServiceInfoByPname(list);
				if(rs!=null&&rs.getRowCount()>0){
					for (int i = 0; i < rs.getRowCount(); i++) {
						serivceDic.put(
								rs.getRows()[i].get("serviceid") == null ? "" : rs
										.getRows()[i].get("serviceid").toString(),"");
					}
					if(serivceDic.containsKey(resourceid)){
						if(getOperateLimit(userid,resourceType, operationType)){
							if("A".equals(operationType)){
								return true;
							}
							String city = getWordpatCity(wordpatid);
							List<String> cityList = map.get("地市");
							boolean b = false;
							for(int k=0;k<cityList.size();k++){
								if(city.contains(cityList.get(k))){
									b = true;
								}
							}
							return b;
						}
					}else{
						return _isHaveOperationPermission(roleidList,"querymanage", resourceid, operationType);
					}
					
				}else{
					return _isHaveOperationPermission(roleidList,"querymanage", resourceid, operationType);
				}
				
			}else{
				return _isHaveOperationPermission(roleidList,"querymanage", resourceid, operationType);
			}
				
		}else{
			return _isHaveOperationPermission(roleidList,"querymanage", resourceid, operationType);
		}
		
		return false;
		
	}
	
	
	/**
	 *判断是否存在对应操作
	 *@param userid
	 *@param resourceType
	 *@param operationType
	 *@return 
	 *@returnType boolean 
	 */
	public static boolean getOperateLimit(String userid,String resourceType,String operationType){
		List<Role> roleList = UserManagerDAO.getRoleListByUserId(userid);
		for (Role role : roleList) {
			// 角色的操作规则
			List<RoleResourceAccessRule> rules = role.getRoleResourcePrivileges();
			for (RoleResourceAccessRule rule : rules) {
//				if (rule.getResourceType().equals(resourceType)) {// 是指定类型的规则
				if (NewEquals.equals(rule.getResourceType(),resourceType)) {// 是指定类型的规则
					if (rule.getOperateLimit().contains(operationType)) {// 如果角色拥有该操作权限
						return true;
					}else{
						return false;
					}
				}
			}
		}
		return false;
	
	}
	
	/**
	 *通过ID获得词模地市
	 *@param wordpatid
	 *@return 
	 *@returnType String 
	 */
	private static String getWordpatCity(String wordpatid){
		String city ="";
		Result rs = CommonLibWordpatDAO.getWordpatById(wordpatid);
		if(rs!=null&&rs.getRowCount()>0){
			for (int i = 0; i < rs.getRowCount(); i++) {
				city = 	rs.getRows()[i].get("city") == null ? "" : rs.getRows()[i].get("city").toString();
			}
		}
		return city;
	}
	
	
	/**
	 *获得资源id和操作类型字典
	 *@param roleidList
	 *@param resourceType
	 *@param resourceid
	 *@return 
	 *@returnType Map<String,String> 
	 */
	private static Map<String,String> getResourceOperationtype(List<String> roleidList, String resourceType,String resourceid){
		Map<String,String> map = new HashMap<String,String>();
		Result rs = getResourceFromDB(roleidList, resourceType, resourceid);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				String _resourceid = rs.getRows()[i].get("resourceid") == null ? "": rs.getRows()[i].get("resourceid").toString();
				String operationType =  rs.getRows()[i].get("operationtype") == null ? "": rs.getRows()[i].get("operationtype").toString();
				if(map.containsKey(_resourceid)){
					String _operationType  = map.get(_resourceid);
					_operationType = _operationType+","+operationType;
					map.put(_resourceid, _operationType);
				}else{
					map.put(_resourceid, operationType);
				}
			}
		}
		return map;
	}
	
	/**
	 * 根据角色获取词类的资源，并附带权限信息
	 * @param wordclass 查询信息
	 * @param wordclassprecise 是否精确查找
	 * @param wordclassType 词类分类  <父子句，近类，父类，其他>
	 * @param roleid 角色编号
	 * @param servicetype 
	 * @param limit
	 * @param start
	 * @return
	 */
	public static Map<String, Result> getWordClassWithResource(String wordclass ,Boolean wordclassprecise,String wordclassType,String roleid,String servicetype,int limit,int start){
		
		// 返回值
		Map<String, Result> resultMap = new HashMap<String, Result>();
		
		// 统计条数的sql
		String innerSql = " from wordclass w left join role_resource r "
				+ "on w.wordclassid=r.resourceid and r.resourcetype='wordclass' and r.roleid='"
				+roleid+"' and  r.servicetype='"+servicetype+"' ";
		String whereSql = "";
		// 添加查询条件
		if(!"".equals(wordclassType) && wordclassType != null) {
			if("其他".endsWith(wordclassType)){
				whereSql += " and w.wordclass not like '%近类' and w.wordclass not like '%父类' and w.wordclass not like '%子句'" ;
			}else{
				whereSql += " and w.wordclass like '%" + wordclassType+"'" ;
			}
			
		}
		if (!wordclassprecise) {
			if(!"".equals(wordclass) && wordclass != null){
				whereSql += " and w.wordclass like '%" + wordclass+"%'" ;
			}
			
		}else{
			if(!"".equals(wordclass) && wordclass != null){
				whereSql += " and w.wordclass like '" + wordclass+"'" ;
			}
		}
		if(!"".equals(whereSql)){
			whereSql =" where " + whereSql.substring(4);
		}
		try {
			String countSql = "select count(*) as count "+innerSql+whereSql;
			Result rs = Database.executeQuery(countSql);
			resultMap.put("count", rs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 分页的sql
		String sql = "";
		if(GetConfigValue.isOracle) {
			sql = "select * from (select a.*,rownum rn from" + " ( select w.wordclassid ,w.wordclass , r.operationtype "
			  + innerSql+whereSql + " order by w.wordclassid) a where rownum<"
			  + (limit + start + 1) + ")" + " where rn>" + start;
		} else if(GetConfigValue.isMySQL) {
			sql = innerSql + " order by w.wordclassid limit " + start + "," + limit;
		}
		
		try {
			Result rs = Database.executeQuery(sql);
			resultMap.put("data", rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	/**
	 * 全量保存词类资源
	 * 
	 * @param roleid
	 *@param resourceid
	 *@param resourceType
	 *@param serviceType
	 *@return
	 *@returnType boolean
	 */
	public static boolean saveAllWordClassRes(String wordclass ,Boolean wordclassprecise,
			String wordclassType,String roleid,String servicetype,String operationType) {
		
		//查询词类的sql
		String wcSql = "select wordclassid from wordclass where ";
		String whereSql = "";
		String resourcetype = "wordclass";
		Result rs = null;
		// 添加查询条件
		if(!"".equals(wordclassType) && wordclassType != null) {
			if("其他".endsWith(wordclassType)){
				whereSql += " and wordclass not like '%近类' and wordclass not like '%父类' and wordclass not like '%子句'" ;
			}else{
				whereSql += " and wordclass like '%" + wordclassType+"'" ;
			}
		}
		if (!wordclassprecise) {
			if(!"".equals(wordclass) && wordclass != null){
				whereSql += " and wordclass like '%" + wordclass+"%'" ;
			}
		}else{
			if(!"".equals(wordclass) && wordclass != null){
				whereSql += " and wordclass like '" + wordclass+"'" ;
			}
		}
		try{
			if(!"".equals(whereSql)){
				whereSql = whereSql.substring(4);
			}
			rs = Database.executeQuery(wcSql+whereSql);
		}catch(Exception e){
			e.printStackTrace();
		}
		String[] resourceid = new String[]{};
		if(rs != null && rs.getRowCount() > 0){
			resourceid = new String[rs.getRowCount()];
			for(int i =0 ,l = rs.getRowCount();i<l;i++){
				resourceid[i] = rs.getRows()[i].get("wordclassid") != null ?rs.getRows()[i].get("wordclassid").toString():"" ;
			}
		}
		return saveResourceWithOperType(roleid, resourceid, resourcetype, servicetype, operationType);
	}
	/**
	 * 获取角色配置的菜单
	 * @param roleid
	 * @param limit
	 * @param start
	 * @return
	 */
	public static Map<String,Result> getRoleMenu(String roleid,int limit, int start){
		
		//内部sql
		String innerSql = "select role_menu.* ,role.rolename from role_menu left join role on role_menu.roleid=role.roleid where role_menu.roleid='"+roleid+"'";
		//返回结果
		Map<String, Result> map = new HashMap<String,Result>();
		//统计个数
		String countSql = "select count(*) as count from role_menu where roleid='"+roleid+"'";
		try {
			Result rs = Database.executeQuery(countSql);
			map.put("count", rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		//分页查询数据
		String sql = "select * from  ( select a.*, rownum rn  from ("
						+innerSql
						+") a where rownum <"+(limit + start + 1)+") where rn > "+start;
		try {
			Result rs = Database.executeQuery(sql);
			map.put("data", rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
		
	}
	/**
	 * 保存角色配置的菜单
	 * @param roleId
	 * @param menuname
	 * @param loadmenuname
	 * @return
	 */
	public static boolean saveRoleMenu(String roleId,String menuname,String loadmenuname){
		
		List<String> listSqls = new ArrayList<String>();
		List<List<?>> listParams = new ArrayList<List<?>>();
		//清空角色对应的菜单
		String delSql = "delete from role_menu where roleid = ?";
		listSqls.add(delSql);
		//定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		//绑定参数
		lstpara.add(roleId);
		listParams.add(lstpara);
		//文件日志
		GlobalValue.myLog.info( delSql + "#" + lstpara );
		
		// 新建角色对应菜单
		String id = null;
		if (GetConfigValue.isOracle) {
			id = ConstructSerialNum.GetOracleNextValNew(
					"SEQ_ROLEMENU_ID","");
		} else if (GetConfigValue.isMySQL) {
			id = ConstructSerialNum.getSerialIDNew("role_menuid", "id",
					"");
		}
		
		String insertSql = "insert into role_menu(id,roleid,menuname,loadmenuname) values (?,?,?,?)";
		listSqls.add(insertSql);
		
		//定义绑定参数集合
		lstpara = new ArrayList<Object>();
		//绑定参数
		lstpara.add(id);
		lstpara.add(roleId);
		lstpara.add(menuname);
		lstpara.add(loadmenuname);
		//添加参数到参数集合
		listParams.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( insertSql + "#" + lstpara );
		int c = Database.executeNonQueryTransaction(listSqls, listParams);
		if(c > 0){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 是否具备页面操作权限
	 * @param userid
	 * @param resourcePage
	 * @return
	 */
	public static boolean isHaveOperationPermissionByPage(String userid,
			String resourcePage) {
		List<String> roleidList = UserManagerDAO.getRoleIDListByUserId(userid);
		Result rs = null;
		String sql = "select * from role_menu where menuname like '%"
			+ resourcePage
			+ "%' ";
		for (int i = 0; i < roleidList.size(); i++) {
			if (i == 0) {
				sql = sql + "and ( roleid =" + roleidList.get(i);
			} else {
				sql = sql + " or roleid =" + roleidList.get(i);
			}
		}
		sql = sql + " )";
		rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0){
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		// Map<String, ArrayList<String>> map =
		// resourseAccess("10001","hotquestion","S");
		// Map<String, ArrayList<String>> map =
		// resourseAccess("12333","report","S");
		// List<String> list = map.get("地市");
		// System.out.println(list);
		List<String> cityList = new ArrayList<String>();
		
		// System.out.println(CommonLibPermissionDAO.getServiceResource("179","structuredknowledge").size());
//		System.out.println(CommonLibPermissionDAO.getServiceResource("179",
//				"scenariosrules", "电信行业->电信集团->4G业务客服应用", cityList));
	
		System.out.println(CommonLibPermissionDAO.isHaveOperationPermission("179", "scenariosrules", "1831782", "S","B8-88-E3-E9-6A-C0"));
//		System.out.println(CommonLibPermissionDAO.resourseAccess("179", "scenariosrules", "S"));
//		System.out.println(CommonLibPermissionDAO.isHaveOperationPermission("179", "querymanage", "1827238", "S"));
//		cityList.add("320000");
//		System.out.println(CommonLibPermissionDAO.getServiceResource("186",
//				"scenariosrules", "电信行业->电信集团->4G业务客服应用", cityList));
		

	}


}
