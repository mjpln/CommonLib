package com.knowology.bll;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.Role;
import com.knowology.Bean.RoleResourceAccessRule;
import com.knowology.Bean.User;
import com.knowology.DbDAO.DBValueOper;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.UtilityOperate.StringOper;
import com.knowology.dal.Database;

public class CommonLibServiceDAO {

	/**
	 * @description 加载根业务树所涉及的参数
	 */
	private static String serviceRoot = "";

	/**
	 * 描述：@description 获得最大的业务根节点信息 参数：@return 返回值类型：@returnType Result
	 */
	public static Result getRootBrand() {
		Result rs = null;
		String sql = "select * from service where serviceid=0";
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 *@description 通过父业务ID获得所有子业务 
	 *@param pid  父业务ID
 	 *@return 
	 *@returnType Result 
	 */
	public static Result getServiceInfoByPid(String pid) {
		Result rs = null;
		String sql = "SELECT *  FROM  service start  WITH serviceid='"+pid+"'　connect BY nocycle prior serviceid = parentid";
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}
	
	/**
	 *@description 通过父业务名获得所有子业务 
	 *@param pid  父业务名
 	 *@return 
	 *@returnType Result 
	 */
	public static Result getServiceInfoByPname(String pname) {
		Result rs = null;
		String sql = "SELECT *  FROM  service start  WITH service='"+pname+"'　connect BY nocycle prior serviceid = parentid";
		rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *@description 通过父业务名获得所有子业务 
	 *@param pname  父业务集合
 	 *@return 
	 *@returnType Result 
	 */
	public static Result getServiceInfoByPname(List<String> pname) {
		Result rs = null;
		String sql = "SELECT *  FROM  service start  WITH ";
		for(int i =0;i<pname.size();i++){
			if(i==0){
				sql = sql+" ( service='"+pname.get(i)+"' ";	
			}else{
				sql = sql+"   or service='"+pname.get(i)+"' ";	
			}
		}
		sql = sql+" ) ";	
		sql = sql+" connect BY nocycle prior serviceid = parentid";
		rs = Database.executeQuery(sql);
		GlobalValue.myLog.info("父业务名获得所有子业务sql->"+sql);
		return rs;
	}
	
	public static Result geServiceidByService() {
		Result rs = null;
		String sql = "select * from service where service='知识库'";
		rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	
	/**
	 *@description 通过业务ID获取业务名 
	 *@param serviceid 业务ID
	 *@return 
	 *@returnType String 
	 */
	public static String getNameByserviceid(String serviceid){
		Result rs = null;
		String service ="";
		String sql = "select service from service where serviceid="+serviceid;
		//文件日志
		GlobalValue.myLog.info( sql );
		rs = Database.executeQuery(sql);
		if(rs!=null&&rs.getRowCount()>0){
			 service = rs.getRows()[0].get("service").toString();
		}
		return service;
	}
	/**
	 *@description 通过业务ID获取业务信息 
	 *@param serviceid 业务ID
	 *@return 
	 *@returnType String 
	 */
	public static Result getServiceInfoByserviceid(String serviceid){
		Result rs = null;
		String service ="";
		String sql = "select * from service where serviceid="+serviceid;
		rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		if(rs!=null&&rs.getRowCount()>0){
			 service = rs.getRows()[0].get("service").toString();
		}
		return rs;
	}
	
	/**
	 * 根据业务ID获取业务名称
	 * @param serviceid
	 * @return
	 */
	public static String getServiceNameByID(String serviceid){
		String name = "";
		Result rs = getServiceInfoByserviceid(serviceid);
		if (rs != null && rs.getRowCount() > 0){
			name = (rs.getRows()[0].get("service") == null ? "" : rs.getRows()[0].get("service").toString());
		}
		return name;
	}
	
	/**
	 *@description 查询信息表业务名
	 *@param brand 品牌
	 *@return 
	 *@returnType Result 
	 */
	public static Result getServiceInfoName(String brand){
		Result rs = null;
		String service ="";
		String sql = "select service,serviceid from service where service like '%信息表' and brand ='"+brand+"'";
		//文件日志
		GlobalValue.myLog.info( sql );
		rs = Database.executeQuery(sql);
		return rs;
	}
	
	/**
	 *@description 通过行业商家组织获得业务根
	 *@return
	 *@returnType Result
	 */
	public static Result getServiceRoot(String industry, String organization,
			String application) {
		List<String> paras = new ArrayList<String>();
		Result rs = null;
		String sql = "select serviceroot  from  M_INDUSTRYAPPLICATION2SERVICES  where INDUSTRY=? and organization=? and APPLICATION=? ";
		paras.add(industry);
		paras.add(organization);
		paras.add(application);
		rs = Database.executeQuery(sql, paras.toArray());
		//文件日志
		GlobalValue.myLog.info( sql + "#" + paras );
		return rs;
	}

	/**
	 * 获得最大根业务对应的子业务
	 * 
	 * @param roleList
	 *            角色集合
	 * @param serviceString
	 *            业务根集合
	 * @return
	 */
	public static Result getChildByRoot(List<Role> roleList,
			String serviceString) {
		String sql = "";
		Result rs = null;
		// 解析参数
		List<String> params = new ArrayList<String>(Arrays.asList(serviceString
				.split(",")));
		for (Iterator<String> iter = params.iterator(); iter.hasNext();) {// '个性化业务'
			// 和
			// 'xxxx主题'
			// 不做处理
			String param = iter.next();
			if (param.equals("'个性化业务'") || param.contains("主题")) {
				serviceRoot += param + ",";
				iter.remove();
			}
		}

		if (params.size() == 0) {// 如何serviceString中只有 '个性化业务' 和 'xxxx主题'
			sql = "select distinct serviceid, ss.service,parentname, brand,cityid,container,mincredit,"
					+ "CASE WHEN (select count(*) from service  where parentid=ss.serviceid)>0 then 'false' ELSE 'true' end as leaf"
					+ " from service ss  "
					+ " where  "
					+ " ss.service in ("
					+ serviceString + ")" + " ORDER BY service";
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			return rs;
		}

		for (Role role : roleList) {// 判断是否为云平台角色
//			if (role.getBelongCom().equals("全行业")
//					|| role.getRoleName().endsWith("管理员")) {
				sql = "select distinct serviceid,ss.service,parentname, brand,cityid,container,mincredit,"
						+ "CASE WHEN (select count(*) from service  where parentid=ss.serviceid)>0 then 'false' ELSE 'true' end as leaf"
						+ " from service ss  "
						+ " where  "
						+ " ss.service in ("
						+ serviceString
						+ ")"
						+ " ORDER BY service";

				rs = Database.executeQuery(sql);
				//文件日志
				GlobalValue.myLog.info( sql );
				
				return rs;
//			}
		}

		// 获得该操作的所有资源规则
		List<RoleResourceAccessRule> ruleList = RoleManager
				.getRolesRuleByOperate(roleList, "service", "S");
		if (!ruleList.isEmpty()) {// 该权限有对应的操作规则

			// // 该操作类型用户能够操作的资源
			// List<String> resourceIDList = new ArrayList<String>();
			// for (RoleResourceAccessRule rule : ruleList) {
			// // 根据属性得到能够操作的所有资源id
			// List<String> serviceIDByAttr = ResourceAccessOper
			// .searchResIDByAttrs(rule.getAccessResourceMap(),
			// "service");
			// if (!serviceIDByAttr.isEmpty()) {// 根据属性查询出相关资源
			// resourceIDList.addAll(serviceIDByAttr);
			// }
			// // 压入用户指定的资源ID
			// List<String> resourceNames = rule.getResourceNames();
			// if (!resourceNames.isEmpty()) {// 用户指定资源名
			// List<String> serviceIDByServiceName = ResourceAccessOper
			// .getResourceIDByName(resourceNames.toArray(),
			// "service");
			// // 判断是否关联子业务
			// if (rule.getIsRelateChild().equals("Y")) {// 关联子业务
			// serviceIDByServiceName = ResourceAccessOper
			// .getChildService(serviceIDByServiceName
			// .toArray());
			// }
			// if (!serviceIDByServiceName.isEmpty()) {
			// resourceIDList.addAll(serviceIDByServiceName);
			// }
			// }
			// }
			// // 去重
			// resourceIDList = new ArrayList<String>(new HashSet<String>(
			// resourceIDList));

			HashMap<String, ArrayList<String>> resourseMap = CommonLibPermissionDAO
					.resourseAccess(roleList, "service", "S");
			// 该操作类型用户能够操作的资源
			List<String> resourceIDList = new ArrayList<String>();
			List<String> cityList = resourseMap.get("地市");
			if (cityList.size() == 0) {// 如果满足角色资源关联地市信息为空，则返回null
				return null;
			} else {// 获得地市信息关联资源ID
				resourceIDList = getServiceIDList(cityList, serviceString);
			}

			// 获得根业务信息
			Map<String, String> map = new HashMap<String, String>();
			for (String param : params) {
				sql = "select * from service where service=" + param;
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
				rs = Database.executeQuery(sql);
				if (rs != null && rs.getRowCount() > 0) {
					for (int i = 0; i < rs.getRowCount(); i++) {
						// 业务id
						String serviceID = rs.getRows()[i].get("serviceID") != null ? rs
								.getRows()[i].get("serviceID").toString()
								: "";
						// 业务名称
						String serviceName = rs.getRows()[i].get("service") != null ? rs
								.getRows()[i].get("service").toString()
								: "";
						map.put(serviceID, serviceName);
					}
				}

			}
			// 构造加载业务树
			CommonLibServiceDAO.constructRootTree(map, resourceIDList);
			String newserviceString = serviceRoot.substring(0, serviceRoot
					.lastIndexOf(","));
			sql = "select distinct serviceid,ss.service,parentname, brand,cityid,container,mincredit,"
					+ "CASE WHEN (select count(*) from service  where parentid=ss.serviceid)>0 then 'false' ELSE 'true' end as leaf"
					+ " from service ss  "
					+ " where ss.cityid  in (284) "
					+ "and ss.service in ("
					+ newserviceString
					+ ")"
					+ " and brand in ("
					+ serviceString
					+ ")"
					+ " ORDER BY service";
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			serviceRoot = "";
		}
		return rs;
	}

	/**
	 * 递归构造根业务
	 * 
	 * @param map
	 *            业务根
	 * @param list
	 *            属性范围中的资源id集合
	 */
	public static void constructRootTree(Map<String, String> map,
			List<String> list) {
		Map<String, String> newMap = new HashMap<String, String>();
		boolean flag = false;
		for (Entry<String, String> entry : map.entrySet()) {
			if (list.contains(entry.getKey())) {// 如果该值已存在list中
				serviceRoot += "'" + entry.getValue() + "',";
				flag = true;
			} else {
				String sql = "select * from service where parentID='"
						+ entry.getKey() + "'";
				Result rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
				if (rs != null && rs.getRowCount() > 0) {
					for (int i = 0; i < rs.getRowCount(); i++) {
						// 业务id
						String serviceID = rs.getRows()[i].get("serviceID") != null ? rs
								.getRows()[i].get("serviceID").toString()
								: "";
						// 业务名称
						String serviceName = rs.getRows()[i].get("service") != null ? rs
								.getRows()[i].get("service").toString()
								: "";
						newMap.put(serviceID, serviceName);
						constructRootTree(newMap, list);
					}
				}
			}
		}
		if (flag) {
			return;
		}
	}

	/**
	 * 根据父业务id获得子业务
	 * 
	 * @param user
	 *            当前登录用户
	 * @param serviceID
	 *            父业务id
	 * @param brand
	 *            根业务名
	 * @return
	 */
	public static Result getChildServiceByParentID(String serviceID,
			List<Role> roleList, String brand) {
		// 返回值
		Result rs = null;
		String sql = "";
		if ("个性化业务".equals(brand) || brand.endsWith("主题")) {// 当为个性化业务时，不做权限判断
			sql = "Select Serviceid,Service,Parentname,Brand,CityID,"
					+ "Container,Mincredit,Case When (Select Count(*) From Service Where Parentid=Ss.Serviceid)>0 Then 'false' Else 'true' End As Leaf "
					+ "from service ss where ss.parentid=" + serviceID
					+ " and cityID=284 and brand='" + brand + "'";
			try {
				rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return rs;
		}
		for (Role role : roleList) {// 判断是否为云平台角色
			if (role.getBelongCom().equals("全行业")
					|| role.getRoleName().endsWith("管理员")) {
				sql = "Select Serviceid,Service,Parentname,Brand,CityID,"
						+ "Container,Mincredit,Case When (Select Count(*) From Service Where Parentid=Ss.Serviceid)>0 Then 'false' Else 'true' End As Leaf "
						+ "from service ss where ss.parentid=" + serviceID
						+ " and cityID=284 and brand='" + brand + "'";
				try {
					rs = Database.executeQuery(sql);
					
					//文件日志
					GlobalValue.myLog.info( sql );
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				return rs;
			}
		}

		// 修改资源开始

		// 获得该操作的所有资源规则
		List<RoleResourceAccessRule> ruleList = RoleManager
				.getRolesRuleByOperate(roleList, "service", "S");
		if (!ruleList.isEmpty()) {// 该权限有对应的操作规则

			// // 该操作类型用户能够操作的资源
			// List<String> resourceIDList = new ArrayList<String>();
			// for (RoleResourceAccessRule rule : ruleList) {
			// // 根据属性得到能够操作的所有资源id
			// List<String> serviceIDByAttr = ResourceAccessOper
			// .searchResIDByAttrs(rule.getAccessResourceMap(),
			// "service");
			// if (!serviceIDByAttr.isEmpty()) {// 根据属性查询出相关资源
			// resourceIDList.addAll(serviceIDByAttr);
			// }
			// // 压入用户指定的资源ID
			// List<String> resourceNames = rule.getResourceNames();
			// if (!resourceNames.isEmpty()) {// 用户指定资源名
			// List<String> serviceIDByServiceName = ResourceAccessOper
			// .getResourceIDByName(resourceNames.toArray(),
			// "service");
			// // 判断是否关联子业务
			// if (rule.getIsRelateChild().equals("Y")) {// 关联子业务
			// serviceIDByServiceName = ResourceAccessOper
			// .getChildService(serviceIDByServiceName
			// .toArray());
			// }
			// if (!serviceIDByServiceName.isEmpty()) {
			// resourceIDList.addAll(serviceIDByServiceName);
			// }
			// }
			// }
			// // 去重
			// resourceIDList = new ArrayList<String>(new HashSet<String>(
			// resourceIDList));

			// 资源修改结束

			HashMap<String, ArrayList<String>> resourseMap = CommonLibPermissionDAO
					.resourseAccess(roleList, "service", "S");
			// 该操作类型用户能够操作的资源
			List<String> resourceIDList = new ArrayList<String>();
			List<String> cityList = resourseMap.get("地市");
			if (cityList.size() == 0) {// 如果满足角色资源关联地市信息为空，则返回null
				return null;
			} else {// 获得地市信息关联资源ID
				resourceIDList = getServiceIDList(cityList, "'" + brand + "'");
			}

			// 构造加载业务树
			Map<String, String> map = new HashMap<String, String>();
			sql = "select * from service where parentID='" + serviceID + "'";
			try {
				rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
				if (rs != null && rs.getRowCount() > 0) {
					for (int i = 0; i < rs.getRowCount(); i++) {
						// 业务id
						String serviceChildID = rs.getRows()[i]
								.get("serviceID") != null ? rs.getRows()[i]
								.get("serviceID").toString() : "";
						// 业务名称
						String serviceChildName = rs.getRows()[i]
								.get("service") != null ? rs.getRows()[i].get(
								"service").toString() : "";
						map.put(serviceChildID, serviceChildName);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			constructRootTree(map, resourceIDList);
			String newserviceString = serviceRoot.substring(0, serviceRoot
					.lastIndexOf(","));
			sql = "Select Serviceid,Service,Parentname,Brand,CityID,"
					+ "Container,Mincredit,Case When (Select Count(*) From Service Where Parentid=Ss.Serviceid)>0 Then 'false' Else 'true' End As Leaf "
					+ "from service ss where ss.service in ("
					+ newserviceString + ") and cityID=284 and brand='" + brand
					+ "' and ss.parentID='" + serviceID + "'";
			// and ss.serviceid
			// in("+org.apache.commons.lang.StringUtils.join(resourceIDList.toArray(),",")+")
			try {
				rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
				serviceRoot = "";
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return rs;
	}

	/**
	 * 根据父业务id获得子业务
	 * 
	 * @param user
	 *            当前登录用户
	 * @param serviceID
	 *            父业务id
	 * @param brand
	 *            根业务名
	 * @param childService
	 *            子业务名
	 * @return
	 */
	public static Result getChildServiceByParentID(String serviceID,
			List<Role> roleList, String brand, String childService) {
		// 返回值
		Result rs = null;
		String sql = "";

		if ("个性化业务".equals(brand) || brand.endsWith("主题")) {// 当为个性化业务时，不做权限判断
			sql = "Select Serviceid,Service,Parentname,Brand,CityID,"
					+ "Container,Mincredit,Case When (Select Count(*) From Service Where Parentid=Ss.Serviceid)>0 Then 'false' Else 'true' End As Leaf "
					+ "from service ss where ss.parentid=" + serviceID
					+ " and cityID=284 and brand='" + brand + "'";
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			return rs;
		}
		for (Role role : roleList) {// 判断是否为云平台角色
			if (role.getBelongCom().equals("全行业")
					|| role.getRoleName().endsWith("管理员")) {
				sql = "Select Serviceid,Service,Parentname,Brand,CityID,"
						+ "Container,Mincredit,Case When (Select Count(*) From Service Where Parentid=Ss.Serviceid)>0 Then 'false' Else 'true' End As Leaf "
						+ "from service ss where ss.parentid=" + serviceID
						+ " and cityID=284 and brand=" + brand
						+ " and ss.service not in(" + childService + ")";
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
					// 判断是否关联子业务
					if (rule.getIsRelateChild().equals("Y")) {// 关联子业务
						serviceIDByServiceName = ResourceAccessOper
								.getChildService(serviceIDByServiceName
										.toArray());
					}
					if (!serviceIDByServiceName.isEmpty()) {
						resourceIDList.addAll(serviceIDByServiceName);
					}
				}
			}
			// 去重
			resourceIDList = new ArrayList<String>(new HashSet<String>(
					resourceIDList));
			// 构造加载业务树
			Map<String, String> map = new HashMap<String, String>();
			sql = "select * from service where parentID='" + serviceID + "'";
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 业务id
					String serviceChildID = rs.getRows()[i].get("serviceID") != null ? rs
							.getRows()[i].get("serviceID").toString()
							: "";
					// 业务名称
					String serviceChildName = rs.getRows()[i].get("service") != null ? rs
							.getRows()[i].get("service").toString()
							: "";
					map.put(serviceChildID, serviceChildName);
				}
			}
			constructRootTree(map, resourceIDList);
			String newserviceString = serviceRoot.substring(0, serviceRoot
					.lastIndexOf(","));
			sql = "Select Serviceid,Service,Parentname,Brand,CityID,"
					+ "Container,Mincredit,Case When (Select Count(*) From Service Where Parentid=Ss.Serviceid)>0 Then 'false' Else 'true' End As Leaf "
					+ "from service ss where ss.service in ("
					+ newserviceString + ") and cityID=284 and brand=" + brand
					+ " and ss.service not in(" + childService + ")";
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			serviceRoot = "";
		}
		return rs;
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

				// // 获得用户能操作的资源ID-->resourceIDList
				// List<String> resourceIDList = new ArrayList<String>();
				// for (RoleResourceAccessRule rule : ruleList) {
				// // 根据属性得到能够操作的所有资源id
				// List<String> serviceIDByAttr = ResourceAccessOper
				// .searchResIDByAttrs(rule.getAccessResourceMap(),
				// "service");
				// if (!serviceIDByAttr.isEmpty()) {// 根据属性查询出相关资源
				// resourceIDList.addAll(serviceIDByAttr);
				// }
				// // 压入用户指定的资源ID
				// List<String> resourceNames = rule.getResourceNames();
				// if (!resourceNames.isEmpty()) {// 用户指定资源名
				// List<String> serviceIDByServiceName = ResourceAccessOper
				// .getResourceIDByName(resourceNames.toArray(),
				// "service");
				// if (!serviceIDByServiceName.isEmpty()) {
				// resourceIDList.addAll(serviceIDByServiceName);
				// }
				// }
				// // 判断是否关联子业务
				// if (rule.getIsRelateChild().equals("Y")) {// 关联子业务
				// resourceIDList = ResourceAccessOper
				// .getChildService(resourceIDList.toArray());
				// }
				// }
				// // 去重
				// resourceIDList = new ArrayList<String>(new HashSet<String>(
				// resourceIDList));

				HashMap<String, ArrayList<String>> resourseMap = CommonLibPermissionDAO
						.resourseAccess(roleList, "service", "S");
				// 该操作类型用户能够操作的资源
				List<String> resourceIDList = new ArrayList<String>();
				List<String> cityList = resourseMap.get("地市");
				if (cityList.size() == 0) {// 如果满足角色资源关联地市信息为空，则返回null
					return null;
				} else {// 获得地市信息关联资源ID
					resourceIDList = getServiceIDList(cityList,  brand
							);
				}

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
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
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
				//文件日志
				GlobalValue.myLog.info( sql );
				rs = Database.executeQuery(sql);
			}
		}
		return rs;
	}

	/**
	 * 根据业务名搜索业务
	 * 
	 * @param user
	 * @param serviceString
	 * @param service
	 * @return
	 */
	public static Result getServiceForDDL(List<Role> roleList,
			String serviceString, String service) {
		// 返回值
		Result rs = null;
		String sql = "";
		if (GetConfigValue.isOracle) {
			if (serviceString.endsWith("主题")) {// 当为个性化业务时，不做权限判断
				sql = "select distinct service from (select * from SERVICE START WITH service IN ("
						+ serviceString
						+ ")"
						+ " and cityID in (284) CONNECT BY prior serviceid = parentid) and upper(service) LIKE '%"
						+ service.toUpperCase() + "%')";
				rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
				return rs;
			}
			for (Role role : roleList) {// 判断是否为云平台角色
				if (role.getBelongCom().equals("全行业")) {
					sql = "select distinct service from (select * from SERVICE START WITH service IN ("
							+ serviceString
							+ ")"
							+ " CONNECT BY prior serviceid = parentid ) where upper(service) LIKE '%"
							+ service.toUpperCase() + "%'";
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

				// // 该操作类型用户能够操作的资源
				// List<String> resourceIDList = new ArrayList<String>();
				// for (RoleResourceAccessRule rule : ruleList) {
				// // 根据属性得到能够操作的所有资源id
				// List<String> serviceIDByAttr = ResourceAccessOper
				// .searchResIDByAttrs(rule.getAccessResourceMap(),
				// "service");
				// if (!serviceIDByAttr.isEmpty()) {// 根据属性查询出相关资源
				// resourceIDList.addAll(serviceIDByAttr);
				// }
				// // 压入用户指定的资源ID
				// List<String> resourceNames = rule.getResourceNames();
				// if (!resourceNames.isEmpty()) {// 用户指定资源名
				// List<String> serviceIDByServiceName = ResourceAccessOper
				// .getResourceIDByName(resourceNames.toArray(),
				// "service");
				// if (!serviceIDByServiceName.isEmpty()) {
				// resourceIDList.addAll(serviceIDByServiceName);
				// }
				// }
				// // 判断是否关联子业务
				// if (rule.getIsRelateChild().equals("Y")) {// 关联子业务
				// resourceIDList = ResourceAccessOper
				// .getChildService(resourceIDList.toArray());
				// }
				// }
				// // 去重
				// resourceIDList = new ArrayList<String>(new HashSet<String>(
				// resourceIDList));

				HashMap<String, ArrayList<String>> resourseMap = CommonLibPermissionDAO
						.resourseAccess(roleList, "service", "S");
				// 该操作类型用户能够操作的资源
				List<String> resourceIDList = new ArrayList<String>();
				List<String> cityList = resourseMap.get("地市");
				if (cityList.size() == 0) {// 如果满足角色资源关联地市信息为空，则返回null
					return null;
				} else {// 获得地市信息关联资源ID
					resourceIDList = getServiceIDList(cityList, serviceString);
				}

				/*
				 * String sql =
				 * "Select Serviceid As Id,Service As Text,Parentname,Brand,CityID,"
				 * +
				 * "Container,Mincredit,Case When (Select Count(*) From Service Where Parentid=Ss.Serviceid)>0 Then 'false' Else 'true' End As Leaf "
				 * +
				 * "from service ss where ss.serviceid in("+org.apache.commons.
				 * lang.StringUtils.join(resourceIDList.toArray(),",")+")";
				 */

				sql = "select distinct service from (select * from SERVICE START WITH service IN ("
						+ serviceString
						+ ")"
						+ "  CONNECT BY prior serviceid = parentid) where  upper(service) LIKE '%"
						+ service.toUpperCase() + "%'";
				if (!resourceIDList.isEmpty()) {
					sql += " and (";
					for (String resourceID : resourceIDList) {
						sql += "serviceID=" + resourceID + " or ";
					}
					sql = sql.substring(0, sql.lastIndexOf("or")) + ")";
				}

				if (serviceString.contains("'个性化业务'")) {
					sql = "select distinct service from service where upper(service) like '%"
							+ service.toUpperCase()
							+ "%' and brand='个性化业务'"
							+ " union  " + sql;
				}
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
				rs = Database.executeQuery(sql);

			}

		} else if (GetConfigValue.isMySQL) {
			if (serviceString.endsWith("主题")) {// 当为个性化业务时，不做权限判断
				sql = "select distinct service  from service where  brand in ("
						+ serviceString + ")" + " and upper(service) LIKE '%"
						+ service.toUpperCase() + "%'";
				rs = Database.executeQuery(sql);
				
				//文件日志
				GlobalValue.myLog.info( sql );
				
				return rs;
			}
			for (Role role : roleList) {// 判断是否为云平台角色
				if (role.getBelongCom().equals("全行业")) {
					sql = "select distinct service  from service where  brand in ("
							+ serviceString
							+ ")"
							+ " and upper(service) LIKE '%"
							+ service.toUpperCase() + "%'";
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
				/*
				 * String sql =
				 * "Select Serviceid As Id,Service As Text,Parentname,Brand,CityID,"
				 * +
				 * "Container,Mincredit,Case When (Select Count(*) From Service Where Parentid=Ss.Serviceid)>0 Then 'false' Else 'true' End As Leaf "
				 * +
				 * "from service ss where ss.serviceid in("+org.apache.commons.
				 * lang.StringUtils.join(resourceIDList.toArray(),",")+")";
				 */

				sql = "select distinct service from (select * from service where  brand in ("
						+ serviceString
						+ ")"
						+ " and upper(service) LIKE '%"
						+ service.toUpperCase() + "%')";

				if (!resourceIDList.isEmpty()) {
					sql += " and (";
					for (String resourceID : resourceIDList) {
						sql += "serviceID=" + resourceID + " or ";
					}
					sql = sql.substring(0, sql.lastIndexOf("or")) + ")";
				}

				//文件日志
				GlobalValue.myLog.info( sql );
				
				rs = Database.executeQuery(sql);
			}
		}
		return rs;
	}

	/**
	 * 通过根业务获得业务
	 * 
	 * @param service
	 * @return
	 */
	public static Result getServiceIDByBrand(String service, String brand) {
		Result rs = null;
		String sql = "SELECT DISTINCT ss.serviceid "
				+ "FROM service ss where ss.service in(" + brand + ")";
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 通过根业务,品牌获得业务ID
	 * 
	 * @param service
	 * @return
	 */
	public static Result getServiceIDByServiceAndBrand(String service,
			String brand) {
		Result rs = null;
		String sql = "SELECT DISTINCT ss.serviceid  FROM service ss where ss.service='"
				+ service + "' and ss.brand ='" + brand + "'";
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 通过业务名获得父业务
	 * 
	 * @param service
	 * @return
	 */
	public static Result getServiceIDByService(String service, String brand) {
		Result rs = null;
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "SELECT DISTINCT ss.serviceid FROM (SELECT * FROM service start WITH service = '"
					+ service
					+ "'  and  brand in ("
					+ brand
					+ ") AND cityid "
					+ "IN (284) 　connect BY nocycle prior parentid = serviceid) ss ";
			rs = Database.executeQuery(sql);
		} else if (GetConfigValue.isMySQL) {
			sql = "select getServiceParentIdList('" + service + "','"
					+ brand.replace("'", "") + "',NUll) as serviceids";
			rs = Database.executeQuery(sql);
			String serviceidArray[] = {};
			if (rs != null && rs.getRowCount() > 0) {
				String serviceids = rs.getRows()[0].get("serviceids")
						.toString();
				serviceidArray = serviceids.split(",");
			}
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
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			rs = Database.executeQuery(sql);
		}

		return rs;
	}

	/**
	 * 根据子业务id查找父业务名
	 * 
	 * @param serviceID
	 *            子业务id
	 * @return
	 */
	public static Result getParentNameByChildID(String serviceid) {
		Result rs = null;
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select distinct service from service start with serviceid="
					+ serviceid + "connect by nocycle prior parentid=serviceid";
			rs = Database.executeQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
		} else if (GetConfigValue.isMySQL) {

			sql = "select getServiceParentIdListByServiceid('" + serviceid
					+ "') as serviceids";
			rs = Database.executeQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
			String serviceidArray[] = {};
			if (rs != null && rs.getRowCount() > 0) {
				String serviceids = rs.getRows()[0].get("serviceids")
						.toString();
				serviceidArray = serviceids.split(",");
			}
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

			//文件日志
			GlobalValue.myLog.info( sql );
			rs = Database.executeQuery(sql);
		}

		return rs;
	}

	/**
	 *@description 根据业务ID获取子业务信息
	 *@param serviceid
	 *@returnType Result
	 */
	public static Result getChildServiceByParentID(String serviceid) {
		Result rs = null;
		String sql = "select * from service where parentid=" + serviceid;
		rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	/**
	 *@description 根据业务ID获取当前业务同级业务信息
	 *@param serviceid
	 *@return
	 *@returnType Result
	 */
	public static Result getSameCalssServiceByserviceID(String serviceid) {
		Result rs = null;
		String sql = "select distinct service from service where parentid in (select parentid from service where serviceid ="
				+ serviceid + ")";
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 *@description 在知识库下添加根业务
	 *@param params
	 *@param customer
	 *@returnType int
	 *@dateTime 2015-9-24上午09:53:46
	 *@author wellhan
	 */
	public static int insertRootService(
			List<List<Map<String, List<String>>>> params, String customer) {
		int count = 0;
		String sql = "";
		// 存放sql语句
		List<String> sqlsList = new ArrayList<String>();
		// 存放参数
		List<List<?>> paramsList = new ArrayList<List<?>>();
		// 四层机构
		String level = "|";

		// 新增根业务
		for (List<Map<String, List<String>>> param : params) {
			sql = "insert into service(";
			List<String> list = new ArrayList<String>();
			for (Entry<String, List<String>> entry : param.get(0).entrySet()) {// serviceid,service,parentname,brand,cityid,parentid,container,serviceid_brand
				sql += entry.getKey() + ",";
				list.add(entry.getValue().get(0));
			}
			sql = sql.substring(0, sql.lastIndexOf(","))
					+ ") values (?,?,?,?,?,?,?,?)";
			sqlsList.add(sql);
			paramsList.add(list);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + list );

			// 组合四层结构
			level += param.get(0).get("service").get(0) + "|";

			// 日志表添加一条记录
			sql = getInsertLogSql();

			List<String> list1 = new ArrayList<String>();
			list1.add(param.get(1).get("ip").get(0));
			list1.add(param.get(1).get("brand").get(0));
			list1.add(param.get(1).get("service").get(0));
			list1.add(param.get(1).get("operation").get(0));
			list1.add(param.get(1).get("city").get(0));
			list1.add(param.get(1).get("workerid").get(0));
			list1.add(param.get(1).get("workername").get(0));
			list1.add(param.get(1).get("object").get(0));
			list1.add(param.get(1).get("tablename").get(0));
			sqlsList.add(sql);
			paramsList.add(list1);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + list1 );
		}
		// 更新四层机构
		if (GetConfigValue.isOracle) {
			sql = "update m_industryapplication2services set serviceroot=serviceroot || ? where industry=? and organization=? and application=?";

		} else if (GetConfigValue.isMySQL) {
			sql = "update m_industryapplication2services set serviceroot=concat(serviceroot,'',?) where industry=? and organization=? and application=?";

		}
		sqlsList.add(sql);
		List<String> list2 = new ArrayList<String>();
		level = level.substring(0, level.lastIndexOf("|"));
		list2.add(level);
		String[] customers = customer.split("->");
		list2.add(customers[0]);
		list2.add(customers[1]);
		list2.add(customers[2]);
		paramsList.add(list2);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + list2 );

		count = Database.executeNonQueryTransaction(sqlsList, paramsList);

		return count;
	}

	/**
	 *@description 添加子业务
	 *@param params
	 *@param customer
	 *@returnType int
	 *@dateTime 2015-9-24上午09:53:46
	 *@author wellhan
	 */
	public static int insertChildService(
			List<List<Map<String, List<String>>>> params, User user) {
		int count = 0;
		String sql = "";
		// 存放sql语句
		List<String> sqlsList = new ArrayList<String>();
		// 存放参数
		List<List<?>> paramsList = new ArrayList<List<?>>();
		// 四层机构
		String level = "|";
		// 父业务id
		String parentID = "";
		// 当前业务id
		String serviceID = "";

		// 新增根业务
		for (List<Map<String, List<String>>> param : params) {
			sql = "insert into service(";
			List<String> list = new ArrayList<String>();
			for (Entry<String, List<String>> entry : param.get(0).entrySet()) {// serviceid,service,parentname,brand,cityid,parentid,container,serviceid_brand
				sql += entry.getKey() + ",";
				list.add(entry.getValue().get(0));
				if (entry.getKey().equalsIgnoreCase("parentid")) {
					parentID = entry.getValue().get(0);
				}
				if (entry.getKey().equalsIgnoreCase("serviceID")) {
					serviceID = entry.getValue().get(0);
				}
			}
			sql = sql.substring(0, sql.lastIndexOf(","))
					+ ") values (?,?,?,?,?,?,?,?)";
			sqlsList.add(sql);
			paramsList.add(list);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + list );

			// 根据父业务查询其配置的属性值
			Map<String, Object> attributeMap = new HashMap<String, Object>();
			sql = "select * from ResourceAcessManager where resourceid='service_"
					+ parentID + "'";
			Result rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 获得所有的列名
					String[] columns = rs.getColumnNames();
					for (int j = 0; j < columns.length; j++) {
						if (columns[j].equalsIgnoreCase("id")) {// ResourceAcessManager主键值不记录
							continue;
						}
						if (columns[j].equalsIgnoreCase("resourceid")) {// 替换ResourceAcessManager的serviceID值
							attributeMap
									.put(columns[j], "service_" + serviceID);
							continue;
						}
						Object value = rs.getRows()[i].get(columns[j]);
						attributeMap.put(columns[j], value);
					}
				}
			}
			if (attributeMap.size() > 0) {// 如果父业务已经有属性值，给子业务添加属性
				String serviceType = user.getIndustryOrganizationApplication();
				String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
				
				String sql_first = "insert into ResourceAcessManager(id,";
				String sql_last = " values (";
				if (GetConfigValue.isOracle) {
					sql_last += ConstructSerialNum.GetOracleNextValNew("Resourceacessmanager_sequence.nextval",bussinessFlag)
								+ ",";
				} else if (GetConfigValue.isMySQL) {
					sql_last += ConstructSerialNum.getSerialIDNew(
							"ResourceAcessManager", "id", bussinessFlag)
							+ ",";
				}
				List<Object> list2 = new ArrayList<Object>();
				for (Entry<String, Object> entry : attributeMap.entrySet()) {
					sql_first += entry.getKey() + ",";
					sql_last += "?,";
					list2.add(entry.getValue());
				}
				sql = sql_first.substring(0, sql_first.lastIndexOf(",")) + ")"
						+ sql_last.substring(0, sql_last.lastIndexOf(","))
						+ ")";
				sqlsList.add(sql);
				paramsList.add(list2);
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + list2 );
				
			}

			// 组合四层结构
			level += param.get(0).get("service").get(0) + "|";

			// 日志表添加一条记录
			sql = getInsertLogSql();

			List<String> list1 = new ArrayList<String>();
			list1.add(param.get(1).get("ip").get(0));
			list1.add(param.get(1).get("brand").get(0));
			list1.add(param.get(1).get("service").get(0));
			list1.add(param.get(1).get("operation").get(0));
			list1.add(param.get(1).get("city").get(0));
			list1.add(param.get(1).get("workerid").get(0));
			list1.add(param.get(1).get("workername").get(0));
			list1.add(param.get(1).get("object").get(0));
			list1.add(param.get(1).get("tablename").get(0));
			sqlsList.add(sql);
			paramsList.add(list1);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + list1 );
			
		}

		count = Database.executeNonQueryTransaction(sqlsList, paramsList);

		return count;
	}

	/**
	 * 修改根业务
	 * 
	 * @param params
	 * @return
	 */
	public static int updateService(List<Map<String, List<String>>> params,
			String customer) {
		int count = 0;
		String sql = "";
		// 存放sql语句
		List<String> sqlsList = new ArrayList<String>();
		// 存放参数
		List<List<?>> paramsList = new ArrayList<List<?>>();

		// 更新业务名
		sql = "update service set service=?, brand = ? where serviceid=?";
		List<String> list1 = new ArrayList<String>();
		list1.add(params.get(0).get("service").get(0));
		list1.add(params.get(0).get("brand").get(0));
		list1.add(params.get(0).get("serviceid").get(0));
		sqlsList.add(sql);
		paramsList.add(list1);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + list1 );

		// 更新父业务名
		sql = "update service set parentname=? , brand = ? where parentid=?";
		List<String> list2 = new ArrayList<String>();
		list2.add(params.get(1).get("parentname").get(0));
		list2.add(params.get(1).get("brand").get(0));
		list2.add(params.get(1).get("parentid").get(0));
		sqlsList.add(sql);
		paramsList.add(list2);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + list2 );

		// 更新kbdata的abstract
		if (GetConfigValue.isOracle) {
			sql = "update kbdata set abstract=?||substr(abstract,instr(abstract,'>')+1) where serviceid=?";
		} else if (GetConfigValue.isMySQL) {
			sql = "update kbdata set abstract=concat(?,'',substr(abstract,instr(abstract,'>')+1)) where serviceid=?";
		}

		List<String> list3 = new ArrayList<String>();
		list3.add(params.get(2).get("abstract").get(0));
		list3.add(params.get(2).get("serviceid").get(0));
		sqlsList.add(sql);
		paramsList.add(list3);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list3 );
		
		
		// 日志表添加一条记录
		sql = getInsertLogSql();
		List<String> list4 = new ArrayList<String>();
		list4.add(params.get(3).get("ip").get(0));
		list4.add(params.get(3).get("brand").get(0));
		list4.add(params.get(3).get("service").get(0));
		list4.add(params.get(3).get("operation").get(0));
		list4.add(params.get(3).get("city").get(0));
		list4.add(params.get(3).get("workerid").get(0));
		list4.add(params.get(3).get("workername").get(0));
		list4.add(params.get(3).get("object").get(0));
		list4.add(params.get(3).get("tablename").get(0));
		sqlsList.add(sql);
		paramsList.add(list4);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list4 );
		
		
		// 更新四层机构
		String[] customers = customer.split("->");
		try {
			// 查出旧的Serviceroot
			String serviceroot = "";
			sql = "select serviceroot from m_industryapplication2services where industry='"
					+ customers[0]
					+ "' and organization='"
					+ customers[1]
					+ "' and application='" + customers[2] + "'";
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				if (rs.getRows()[0].get("serviceroot") == null) {
					throw new Exception("修改四层结构出错");
				} else {
					serviceroot = rs.getRows()[0].get("serviceroot").toString();

				}
			}
			// 查询出旧的service
			String service = "";
			sql = "select service from service where serviceid="
					+ params.get(0).get("serviceid").get(0);
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			if (rs != null && rs.getRowCount() > 0) {
				if (rs.getRows()[0].get("service") == null) {
					throw new Exception("查询旧业务名出错");
				} else {
					service = rs.getRows()[0].get("service").toString();
				}
			}

			// 修改Serviceroot (update by hw)
			serviceroot = serviceroot.replace("|" + service, "|"
					+ params.get(0).get("service").get(0));

			// 更新四层机构
			sql = "update m_industryapplication2services set serviceroot=? where industry=? and organization=? and application=?";
			sqlsList.add(sql);
			List<String> list5 = new ArrayList<String>();
			list5.add(serviceroot);
			list5.add(customers[0]);
			list5.add(customers[1]);
			list5.add(customers[2]);
			paramsList.add(list5);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + list5 );
			
		} catch (Exception e) {
			e.printStackTrace();
			return count;
		}
		count = Database.executeNonQueryTransaction(sqlsList, paramsList);

		return count;
	}

	/**
	 * 修改子业务
	 * 
	 * @param params
	 * @return
	 */
	public static int updateService(List<Map<String, List<String>>> params) {
		int count = 0;
		String sql = "";
		// 存放sql语句
		List<String> sqlsList = new ArrayList<String>();
		// 存放参数
		List<List<?>> paramsList = new ArrayList<List<?>>();

		// 更新业务名
		sql = "update service set service=? where serviceid=?";
		List<String> list1 = new ArrayList<String>();
		list1.add(params.get(0).get("service").get(0));
		list1.add(params.get(0).get("serviceid").get(0));
		sqlsList.add(sql);
		paramsList.add(list1);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list1 );
		
		
		// 更新父业务名
		sql = "update service set parentname=? where parentid=?";
		List<String> list2 = new ArrayList<String>();
		list2.add(params.get(1).get("parentname").get(0));
		list2.add(params.get(1).get("parentid").get(0));
		sqlsList.add(sql);
		paramsList.add(list2);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list2 );
		
		// 更新kbdata的abstract
		if (GetConfigValue.isOracle) {
			sql = "update kbdata set abstract=?||substr(abstract,instr(abstract,'>')+1) where serviceid=?";
		} else if (GetConfigValue.isMySQL) {
			sql = "update kbdata set abstract=concat(?,'',substr(abstract,instr(abstract,'>')+1)) where serviceid=?";
		}
		List<String> list3 = new ArrayList<String>();
		list3.add(params.get(2).get("abstract").get(0));
		list3.add(params.get(2).get("serviceid").get(0));
		sqlsList.add(sql);
		paramsList.add(list3);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list3 );
		
		// 日志表添加一条记录
		sql = getInsertLogSql();
		List<String> list4 = new ArrayList<String>();
		list4.add(params.get(3).get("ip").get(0));
		list4.add(params.get(3).get("brand").get(0));
		list4.add(params.get(3).get("service").get(0));
		list4.add(params.get(3).get("operation").get(0));
		list4.add(params.get(3).get("city").get(0));
		list4.add(params.get(3).get("workerid").get(0));
		list4.add(params.get(3).get("workername").get(0));
		list4.add(params.get(3).get("object").get(0));
		list4.add(params.get(3).get("tablename").get(0));
		sqlsList.add(sql);
		paramsList.add(list4);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list4 );
		
		count = Database.executeNonQueryTransaction(sqlsList, paramsList);

		return count;
	}

	/**
	 * 根据业务id删除业务，以及业务对应的摘要，问题，词模。。。以及资源属性表(Resourceacessmanager)对应的数据
	 * 
	 * @param serviceID
	 * @return
	 */
	public static int deleteAllServiceByID(
			List<Map<String, List<String>>> params) {
		// 操作作用的行数
		int count = 0;
		String sql = "";
		Result rs = null;
		// 存放sql语句
		List<String> sqlsList = new ArrayList<String>();
		// 存放参数
		List<List<?>> paramsList = new ArrayList<List<?>>();

		// 删除业务
		sql = "delete from service where serviceID in (?)";
		List<String> serviceIDs = params.get(0).get("serviceid");
		String serviceID = "";
		if (serviceIDs.size() > 1) {
			for (String str : serviceIDs) {
				serviceID += str + "or";
			}
			serviceID = serviceID.substring(0, serviceID.lastIndexOf("or"));
		} else {
			serviceID = serviceIDs.get(0);
		}
		sqlsList.add(sql);
		List<String> list = new ArrayList<String>();
		list.add(serviceID);
		paramsList.add(list);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list );
		
		
		// 日志表添加一条记录

		// sql =
		// "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)";
		sql = getInsertLogSql();
		List<String> list1 = new ArrayList<String>();
		list1.add(params.get(1).get("ip").get(0));
		list1.add(params.get(1).get("brand").get(0));
		list1.add(params.get(1).get("service").get(0));
		list1.add(params.get(1).get("operation").get(0));
		list1.add(params.get(1).get("city").get(0));
		list1.add(params.get(1).get("workerid").get(0));
		list1.add(params.get(1).get("workername").get(0));
		list1.add(params.get(1).get("object").get(0));
		list1.add(params.get(1).get("tablename").get(0));
		sqlsList.add(sql);
		paramsList.add(list1);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list1 );
		
		// 要删除的资源id集合
		List<String> resourceIDList = new ArrayList<String>();
		try {
			// // 根据serviceID查询摘要
			// sql = "select * from kbdata where serviceID in (" + serviceID +
			// ")";
			// rs = Database.executeQuery(sql);
			// if (rs != null && rs.getRowCount() > 0) {
			// for (int i = 0; i < rs.getRowCount(); i++) {
			// String kbdataID = rs.getRows()[i].get("kbdataID") != null ? rs
			// .getRows()[i].get("kbdataID").toString()
			// : "";
			// resourceIDList.add("kbdata_" + kbdataID);
			// }
			// }
			//
			// // 查询serviceID下的词模
			// sql =
			// "Select * From Wordpat w where exists (select 1 from kbdata k where w.kbdataID=k.kbdataid and k.serviceID in ("
			// + serviceID + "))";
			// rs = Database.executeQuery(sql);
			// if (rs != null && rs.getRowCount() > 0) {
			// for (int i = 0; i < rs.getRowCount(); i++) {
			// String wordpatID = rs.getRows()[i].get("wordpatID") != null ? rs
			// .getRows()[i].get("wordpatID").toString()
			// : "";
			// resourceIDList.add("wordpat_" + wordpatID);
			// }
			// }
			//
			// // 查询serviceID下的答案
			// sql = "select * from ("
			// + "select g.kbanswerID as kbanswerID,b.kbdataID as kbdataID "
			// +
			// " from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g "
			// + " where a.serviceid=b.serviceid "
			// + " and B.Kbdataid=C.Kbdataid "
			// + " and c.kbansvaliddateid=d.kbansvaliddateid "
			// + " and d.kbanspakid=e.kbanspakid "
			// + " and E.Kbansqryinsid=F.Kbansqryinsid "
			// + " and F.Kbcontentid=G.Kbcontentid) a "
			// +
			// " where exists (select 1 from kbdata k where k.kbdataid=a.kbdataid and k.serviceID in ("
			// + serviceID + "))";
			// rs = Database.executeQuery(sql);
			// if (rs != null && rs.getRowCount() > 0) {
			// for (int i = 0; i < rs.getRowCount(); i++) {
			// String kbanswerID = rs.getRows()[i].get("kbanswerID") != null ?
			// rs
			// .getRows()[i].get("kbanswerID").toString()
			// : "";
			// resourceIDList.add("kbanswer_" + kbanswerID);
			// }
			// }
			//
			// // 查询serviceID下的相似问题
			// sql =
			// "Select s.kbdataid as kbdataID From Similarquestion s where exists"
			// +
			// " (select 1 from kbdata k where s.kbdataID=k.kbdataID and k.serviceid in ("
			// + serviceID + "))";
			// if (rs != null && rs.getRowCount() > 0) {
			// for (int i = 0; i < rs.getRowCount(); i++) {
			// String questionID = rs.getRows()[i].get("questionID") != null ?
			// rs
			// .getRows()[i].get("questionID").toString()
			// : "";
			// resourceIDList.add("similar" + questionID);
			// }
			// }
			//
			// // 删除资源权限表中的记录
			// for (String resourceID : resourceIDList) {
			// sql = "delete from ResourceAcessManager where resourceID=?";
			// sqlsList.add(sql);
			// List<String> param = new ArrayList<String>();
			// param.add(resourceID);
			// paramsList.add(param);
			// }

			count = Database.executeNonQueryTransaction(sqlsList, paramsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 根据业务id删除根业务，以及业务对应的摘要，问题，词模。。。以及资源属性表(Resourceacessmanager)对应的数据
	 * 
	 * @param serviceID
	 * @return
	 */
	public static int deleteAllServiceByID(
			List<Map<String, List<String>>> params, String customer) {
		// 操作作用的行数
		int count = 0;
		String sql = "";
		Result rs = null;
		// 存放sql语句
		List<String> sqlsList = new ArrayList<String>();
		// 存放参数
		List<List<?>> paramsList = new ArrayList<List<?>>();

		// 删除业务
		sql = "delete from service where serviceID in (?)";
		List<String> serviceIDs = params.get(0).get("serviceid");
		String serviceID = "";
		if (serviceIDs.size() > 1) {
			for (String str : serviceIDs) {
				serviceID += str + "or";
			}
			serviceID = serviceID.substring(0, serviceID.lastIndexOf("or"));
		} else {
			serviceID = serviceIDs.get(0);
		}
		sqlsList.add(sql);
		List<String> list = new ArrayList<String>();
		list.add(serviceID);
		paramsList.add(list);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list );
		
		
		// 日志表添加一条记录

		// sql =
		// "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)";
		sql = getInsertLogSql();
		List<String> list1 = new ArrayList<String>();
		list1.add(params.get(1).get("ip").get(0));
		list1.add(params.get(1).get("brand").get(0));
		list1.add(params.get(1).get("service").get(0));
		list1.add(params.get(1).get("operation").get(0));
		list1.add(params.get(1).get("city").get(0));
		list1.add(params.get(1).get("workerid").get(0));
		list1.add(params.get(1).get("workername").get(0));
		list1.add(params.get(1).get("object").get(0));
		list1.add(params.get(1).get("tablename").get(0));
		sqlsList.add(sql);
		paramsList.add(list1);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + list1 );
		
		// // 要删除的资源id集合
		List<String> resourceIDList = new ArrayList<String>();
		try {
			// // 根据serviceID查询摘要
			// sql = "select * from kbdata where serviceID in (" + serviceID +
			// ")";
			// rs = Database.executeQuery(sql);
			// if (rs != null && rs.getRowCount() > 0) {
			// for (int i = 0; i < rs.getRowCount(); i++) {
			// String kbdataID = rs.getRows()[i].get("kbdataID") != null ? rs
			// .getRows()[i].get("kbdataID").toString()
			// : "";
			// resourceIDList.add("kbdata_" + kbdataID);
			// }
			// }
			//
			// // 查询serviceID下的词模
			// sql =
			// "Select * From Wordpat w where exists (select 1 from kbdata k where w.kbdataID=k.kbdataid and k.serviceID in ("
			// + serviceID + "))";
			// rs = Database.executeQuery(sql);
			// if (rs != null && rs.getRowCount() > 0) {
			// for (int i = 0; i < rs.getRowCount(); i++) {
			// String wordpatID = rs.getRows()[i].get("wordpatID") != null ? rs
			// .getRows()[i].get("wordpatID").toString()
			// : "";
			// resourceIDList.add("wordpat_" + wordpatID);
			// }
			// }
			//
			// // 查询serviceID下的答案
			// sql = "select * from ("
			// + "select g.kbanswerID as kbanswerID,b.kbdataID as kbdataID "
			// +
			// " from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g "
			// + " where a.serviceid=b.serviceid "
			// + " and B.Kbdataid=C.Kbdataid "
			// + " and c.kbansvaliddateid=d.kbansvaliddateid "
			// + " and d.kbanspakid=e.kbanspakid "
			// + " and E.Kbansqryinsid=F.Kbansqryinsid "
			// + " and F.Kbcontentid=G.Kbcontentid) a "
			// +
			// " where exists (select 1 from kbdata k where k.kbdataid=a.kbdataid and k.serviceID in ("
			// + serviceID + "))";
			// rs = Database.executeQuery(sql);
			// if (rs != null && rs.getRowCount() > 0) {
			// for (int i = 0; i < rs.getRowCount(); i++) {
			// String kbanswerID = rs.getRows()[i].get("kbanswerID") != null ?
			// rs
			// .getRows()[i].get("kbanswerID").toString()
			// : "";
			// resourceIDList.add("kbanswer_" + kbanswerID);
			// }
			// }
			//
			// // 查询serviceID下的相似问题
			// sql =
			// "Select s.kbdataid as kbdataID From Similarquestion s where exists"
			// +
			// " (select 1 from kbdata k where s.kbdataID=k.kbdataID and k.serviceid in ("
			// + serviceID + "))";
			// if (rs != null && rs.getRowCount() > 0) {
			// for (int i = 0; i < rs.getRowCount(); i++) {
			// String questionID = rs.getRows()[i].get("questionID") != null ?
			// rs
			// .getRows()[i].get("questionID").toString()
			// : "";
			// resourceIDList.add("similar" + questionID);
			// }
			// }
			//
			// // 删除资源权限表中的记录
			// for (String resourceID : resourceIDList) {
			// sql = "delete from ResourceAcessManager where resourceID=?";
			// sqlsList.add(sql);
			// List<String> param = new ArrayList<String>();
			// param.add(resourceID);
			// paramsList.add(param);
			// }

			// 修改四层结构
			// 查出旧的Serviceroot
			String[] customers = customer.split("->");
			String serviceroot = "";
			sql = "select Serviceroot from M_Industryapplication2services where industry='"
					+ customers[0]
					+ "' and organization='"
					+ customers[1]
					+ "' and application='" + customers[2] + "'";
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			if (rs != null && rs.getRowCount() > 0) {
				if (rs.getRows()[0].get("Serviceroot") == null) {
					throw new Exception("修改四层结构出错");
				} else {
					serviceroot = rs.getRows()[0].get("Serviceroot").toString();

				}
			}
			// 查询出旧的service
			String service = "";
			sql = "select service from service where serviceid="
					+ params.get(0).get("serviceid").get(0);
			rs = Database.executeQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
			if (rs != null && rs.getRowCount() > 0) {
				if (rs.getRows()[0].get("service") == null) {
					throw new Exception("查询旧业务名出错");
				} else {
					service = rs.getRows()[0].get("service").toString();
				}
			}

			// 删除Serviceroot 中包含的 service （update by hw）
			serviceroot = serviceroot.replace("|" + service, "");

			// 更新四层机构
			sql = "update M_Industryapplication2services set Serviceroot=? where industry=? and organization=? and application=?";
			sqlsList.add(sql);
			List<String> list5 = new ArrayList<String>();
			list5.add(serviceroot);
			list5.add(customers[0]);
			list5.add(customers[1]);
			list5.add(customers[2]);
			paramsList.add(list5);

			//文件日志
			GlobalValue.myLog.info( sql + "#" + list5 );
			
			count = Database.executeNonQueryTransaction(sqlsList, paramsList);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	public static String getInsertLogSql() {
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)";
		} else if (GetConfigValue.isMySQL) {
			sql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,sysdate())";
		}
		return sql;
	}

	/**
	 * 
	 *描述：根据根业务获取子业务
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-27 时间：下午04:35:27
	 *@param ServiceRootIDe
	 *@return Result
	 */
	public static Result getServiceByServiceRoot(String ServiceRootIDe) {
		String sql = "SELECT * FROM service  WHERE parentid=" + ServiceRootIDe;
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		if (rs != null && rs.getRowCount() > 0) {
			return rs;
		} else {
			return null;
		}
	}

	/**
	 * 
	 *描述：根据根业务获取子业务
	 * 
	
	 *@param serviceid
	 *@return Result
	 */
	public static Result getFServiceByServiceid(String serviceid) {
		String sql = "select * from service where serviceid =(select parentid from service where serviceid=" + serviceid+")";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		if (rs != null && rs.getRowCount() > 0) {
			return rs;
		} else {
			return null;
		}
	}
	
	/**
	 * 根据业务名称集合获取对应的业务id集合
	 * 
	 * @param serviceList参数业务名称集合
	 * @return 业务id集合
	 */
	public static Result getServiceIDByServiceList(List<String> serviceList) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 查询service对应的serviceid的SQL语句
		sql
				.append("select serviceid from service where parentid=0 and service in (");
		// // 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 循环遍历业务名称集合
		for (int i = 0; i < serviceList.size(); i++) {
			if (i != serviceList.size() - 1) {
				// 除了集合的最后一个绑定参数不需要加上逗号，其他的都要加上
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定id参数
			lstpara.add(serviceList.get(i));
		}
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		return rs;
	}

	/**
	 * 
	 *描述：根据业务ID获取业务路径
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-3-3 时间：下午03:55:24
	 *@param serviceid
	 *@return ArrayList<String>
	 */
	public static ArrayList<String> getServicePath(String serviceid) {
		ArrayList<String> list = new ArrayList<String>();
		if (serviceid.length() != 0) {
			try {
				// 定义SQL语句
				StringBuilder sql = new StringBuilder();
				// 查询service对应的serviceid的SQL语句
				if (GetConfigValue.isOracle) {
					sql
							.append("SELECT service  FROM service start  WITH serviceid=?　connect BY nocycle prior  parentid=serviceid");
				} else {
					sql
							.append("SELECT getServiceParentIdListByServiceid(?) serviceid FROM DUAL");
				}
				// // 定义绑定参数集合
				List<String> lstpara = new ArrayList<String>();
				// 绑定id参数
				lstpara.add(serviceid);
				Result rs = Database.executeQuery(sql.toString(), lstpara
						.toArray());
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
				
				if (rs != null && rs.getRowCount() > 0) {
					if (GetConfigValue.isMySQL) {
						String serviceID = DBValueOper
						.GetValidateStringObj4Null(rs
								.getRowsByIndex()[0][0]);
						list.addAll(StringOper.StringSplit(serviceID, ","));
					} else {
						for (int r = rs.getRowCount() - 1; r >= 0; r--) {
							String service = DBValueOper
									.GetValidateStringObj4Null(rs
											.getRowsByIndex()[r][0]);
							if (service.length() > 0) {
								list.add(service);
							}
						}
					}
				}

			} catch (Exception e) {
				GlobalValue.myLog.error(e.toString());
			}
		}
		return list;
	}

	/**
	 *@description 通过业务ID查看是否存在子业务
	 *@param serviceid
	 *@return
	 *@returnType Boolean
	 */
	public static Boolean hasChildrenByServiceid(String serviceid) {
		String sql;
		if (GetConfigValue.isMySQL) {
			sql = "select serviceid from service where  parentid in ("
					+ serviceid + ") limit 1";
		} else {
			sql = "select serviceid from service where  parentid in ("
					+ serviceid + ") and rownum<2 ";
		}
		//文件日志
		GlobalValue.myLog.info( sql );
		Result res = Database.executeQuery(sql);
		if (res == null || res.getRowCount() == 0) {
			return true;
		}
		return false;
	}

	public static List<String> getServiceIDList(List<String> cityList,
			String serviceString) {

		List<String> list = new ArrayList<String>();
		
		String sql = "select serviceid from service where brand in("
			+ serviceString + ")";
//		String sql = "select serviceid from service where brand in("
//				+ serviceString + ") and (";
//		for (int i = 0; i < cityList.size(); i++) {
//			String city = cityList.get(i);
//			if (i < cityList.size() - 1) {
//				sql = sql + " city like'%" + city + "%' or ";
//			} else {
//				sql = sql + " city like'%" + city + "%' ) ";
//			}
//		}
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		for (int i = 0; i < rs.getRowCount(); i++) {
			String serviceid = rs.getRows()[i].get("serviceid").toString();
			list.add(serviceid);
		}

		return list;

	}

	/**
	 * 构造业务树
	 * @param serviceid 业务id
	 * @param brand 品牌
	 * @return
	 */
	public static Result createServiceTree(String serviceid,String brand) {
		String sql = "";
		Result rs = null;
		if ("".equals(serviceid) || serviceid == null) {// 加载根业务
			sql = "select serviceid,service from service where service in(?)";
			rs = Database.executeQuery(sql, brand);
		} else {// 根据父业务id，加载子业务id
			sql = "select serviceid,service from service where parentid=? and  brand in(?)" ;
			rs = Database.executeQuery(sql, serviceid, brand);
		}
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}
	
	/**
	 * 构造业务树
	 * @param serviceid 业务id
	 * @param brand 品牌
	 * @return
	 */
	public static Result createService(String serviceid,String brand) {
		String sql = "";
		Result rs = null;
		if ("".equals(serviceid) || serviceid == null) {// 加载根业务
			sql = "select serviceid,service from service where service in(?)";
			rs = Database.executeQuery(sql, brand);
		} else {// 根据父业务id，加载子业务id
			sql = "select serviceid,service from service where parentid=?";
			rs = Database.executeQuery(sql, serviceid);
		}
		//文件日志
		GlobalValue.myLog.info(sql);
		return rs;
	}
	
	/**
	 *@description  通过父ID获取儿子业务相关信息
	 *@param parentid
	 *@return 
	 *@returnType Result 
	 */
	public static Result getSonServiceInfo(String parentid){
		Result rs = null;
		String sql = " select * from (SELECT *  FROM service start WITH serviceid = ?　connect BY nocycle prior serviceid = parentid) where serviceid != ?";
		rs = Database.executeQuery(sql, parentid, parentid);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}
	
	/**
	 * 修改业务地市
	 * 
	 * @param sserviceidAndCity
	 * @return
	 */
	public static int updateServiceCity(Map<String,String> serviceidAndCity) {
		int count = 0;
		String sql = "";
		List<String> sqlsList = new ArrayList<String>();
		List<List<?>> paramsList = new ArrayList<List<?>>();
		List<String> list ;
		for (Map.Entry<String,String> entry : serviceidAndCity.entrySet()) {
			String serviceid = entry.getKey();
			String cityCode = entry.getValue();
			sql = "update service set city=? where serviceid=?";
			list = new ArrayList<String>();
			list.add(cityCode);
			list.add(serviceid);
			sqlsList.add(sql);
			paramsList.add(list);
			//文件日志
			GlobalValue.myLog.info( sql + "#" + list );
		}
		count = Database.executeNonQueryTransaction(sqlsList, paramsList);
		return count;
	}
	
	

	/**
	 *@description  修改业务路径
	 *@param pid
	 *@param pname
	 *@return 
	 *@returnType int 
	 */
	public static int updateServiceParentid(String pid,String pname,String serviceid) {
		int count = 0;
		String sql = "";
		List<String> sqlsList = new ArrayList<String>();
		List<List<?>> paramsList = new ArrayList<List<?>>();
		List<String> list ;
			sql = "update service set parentid =? ,parentname = ? where serviceid=?";
			list = new ArrayList<String>();
			list.add(pid);
			list.add(pname);
			list.add(serviceid);
			sqlsList.add(sql);
			paramsList.add(list);
			//文件日志
			GlobalValue.myLog.info( sql + "#" + list );
		count = Database.executeNonQueryTransaction(sqlsList, paramsList);
		return count;
	}
	
	/**
	 *@description  通过业务根、业务名称获得业务上层级相关数据
	 *@param serviceRoot
	 *@param name
	 *@return 
	 *@returnType Result 
	 */
	public static Result getUpHierarchyService(String serviceRoot, String name) {
		Result rs = null;
		String sql = "SELECT * FROM service start " + "WITH service=? and brand in(?) connect BY nocycle prior parentid=serviceid";
		rs = Database.executeQuery(sql, name, serviceRoot);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}
	

	/**
	 *@description  模糊产寻业务名
	 *@param rootService
	 *@param serviceStr
	 *@return 
	 *@returnType Result 
	 */
	public static Result getLikeService(String rootService, String serviceStr){
		Result rs = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT * ");
		sb.append(" FROM ");
		sb.append("   (SELECT service, ");
		sb.append("     serviceid, ");
		sb.append("     SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) NAME_PATH, ");
		sb.append("     SUBSTR(SYS_CONNECT_BY_PATH(serviceid,'->'),3) SERVICEID_PATH ");
		sb.append("   FROM service ");
		sb.append("     START WITH service        IN (");
		sb.append(rootService);
		sb.append(") ");
		sb.append("     CONNECT BY PRIOR serviceid = parentid ");
		sb.append("   ) GHJbieming ");
		sb.append(" WHERE UPPER(service) LIKE ?");
		rs = Database.executeQuery(sb.toString(), "%" + serviceStr.toUpperCase() +"%");
		
		//文件日志
		GlobalValue.myLog.info( sb + "#" + "%" + serviceStr.toUpperCase() +"%" );
		
		return rs;
	}

	/**
	 * 查询指定根目录下的业务
	 * @param rootService
	 * @return
	 */
	public static Result seletServiceByParentNameRootAndBrand(String rootService, String brand){
		Result rs = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT service, serviceid, parentid FROM service START WITH service IN(?)");
		sb.append(" AND brand in(?) CONNECT BY PRIOR serviceid = parentid ");
		rs = Database.executeQuery(sb.toString(), rootService, brand);
		
		//文件日志
		GlobalValue.myLog.info( sb );
		
		return rs;
	}
	
	/**
	 * 通过根业务,品牌获得业务ID
	 * 
	 * @param service
	 * @param brand
	 * @return
	 */
	public static Result getServiceID(String service, String brand) {
		Result rs = null;
		String sql = "SELECT DISTINCT ss.serviceid  FROM service ss where ss.service in(?) and ss.brand in(?)";
		rs = Database.executeQuery(sql, service, brand);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 通过ID删除业务
	 * 
	 * @param serviceIds 业务ID集合
	 */
	public static int deleteServiceByID(List<String> serviceIds) {
		String sql = "delete from service where serviceID in(?)";
		StringBuffer serviceIdsBuffer = new StringBuffer();
		for(int i = 0; i < serviceIds.size(); i++)  {
			serviceIdsBuffer.append(serviceIds.get(i));
			if(i < serviceIds.size() - 1) {
				serviceIdsBuffer.append(",");
			}
		}
		int count = Database.executeNonQuery(sql, serviceIdsBuffer.toString());
		//文件日志
		GlobalValue.myLog.info(sql);
		return count;
	}
}
