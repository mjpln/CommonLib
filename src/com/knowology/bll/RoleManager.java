package com.knowology.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.Bean.Role;
import com.knowology.Bean.RoleResourceAccessRule;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;
/**
 * 对角色的DML操作
 * @author knowology
 *
 */

public class RoleManager {

	/**
	 * 查询角色
	 * @param roleName 角色名称
	 * @param customer 角色所属的机构
	 * @param limit 每页限制的条数
	 * @param start 开始的条数
	 * @return Map<String, Result>：<"data/count","Result">数据类型，数据result
	 */
	public static Map<String, Result> selectRole(String roleName, String customer, int limit, int start) {
		// 返回值
		Map<String, Result> resultMap = new HashMap<String, Result>();
		
		// 统计条数的sql
		String innerSql = "select * from role where ";
		//如果为空，查询所有行业
		if(customer == null){
			customer = "%";
		}
		String[] split = customer.split(",");
		innerSql += "( ";
		for(int i =0;i<split.length;i++){
			if(i == split.length -1){
				innerSql += " customer like '"+split[i]+"'";
			}else{
				innerSql += " customer like '"+split[i]+"' or";
			}
		}
		innerSql += ") ";
		
		// 添加查询条件
		if (!"".equals(roleName) && roleName != null) {
			innerSql += " and roleName like '%"+roleName+"%'";
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
				  " ("+innerSql+" order by roleid) t where rownum<"+(limit+start+1)+")"+
				  " where rn>"+start;
		} else if(GetConfigValue.isMySQL) {
			sql = innerSql + " order by roleid limit " + start + "," + limit;
		}
		Result rs = Database.executeQuery(sql);
		resultMap.put("data", rs);
		
		return resultMap;
	}
	
	/**
	 * 创建角色
	 * @param roleID
	 * @param roleName
	 * @param customer
	 * @return
	 */
	public static int addRole(String roleID, String roleName, String customer) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 返回值
		int count = 0;
		// 查询roleID是否已经被使用
		String sql = "select * from role where roleid='"+roleID+"'";
		try{
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount()>0) {
				count --;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if (count == -1) {
			return count;
		}
		// 对role表操作
		sql = "insert into role(roleid,roleName,customer) values (?,?,?)";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定id参数
		lstpara.add(roleID);
		// 绑定词类参数
		lstpara.add(roleName);
		// 绑定类型参数
		lstpara.add(customer);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		// 对role_menu表操作
		sql = "insert into role_menu(id,roleid) values (seq_rolemenu_id.nextval,?)";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定id参数
		lstpara.add(roleID);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		
		
		try {
			count = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return count;
	} 
	
	/**
	 * 更新角色
	 * @param oldRoleID 原角色id
	 * @param newRoleID 新角色id
	 * @param roleName 角色名称
	 * @param customer 所属机构
	 * @return
	 */
	public static int updateRole(String oldRoleID, String newRoleID, String roleName, String customer) {
		// 返回值
		int count = 0;
		// 存放sql的集合
		List<String> sqlList = new ArrayList<String>();
		// 存放参数的集合
		List<List<?>> paramsList = new ArrayList<List<?>>();
		
		// 判断角色id是否变化
		if (oldRoleID.equals(newRoleID)) {// 角色id未发生变化
			// 对role表操作
			String sql_worker = "update role set roleName=?,customer=? where roleid=?";
			// role参数
			List<Object> role_param = new ArrayList<Object>();
			role_param.add(roleName);
			role_param.add(customer);
			role_param.add(oldRoleID);
			
			sqlList.add(sql_worker);
			paramsList.add(role_param);
		} else {// 角色id发生变化
			// 查询roleID是否已经被使用
			String sql_role = "select * from role where roleid='"+newRoleID+"'";
			try{
				Result rs = Database.executeQuery(sql_role);
				if (rs != null && rs.getRowCount()>0) {
					count --;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if (count == -1) {
				return count;
			}
			
			// 对role表进行操作
			String sql_worker = "update role set roleid=? where roleid=?";
			// role参数
			List<Object> role_param = new ArrayList<Object>();
			role_param.add(newRoleID);
			role_param.add(oldRoleID);
			
			sqlList.add(sql_worker);
			paramsList.add(role_param);
			
			// 对role_rule表进行操作
			String sql_role_rule = "update role_rule set roleid=? where roleid=?";
			// role_rule参数
			List<Object> role_rule_param = new ArrayList<Object>();
			role_rule_param.add(newRoleID);
			role_rule_param.add(oldRoleID);
			
			sqlList.add(sql_role_rule);
			paramsList.add(role_rule_param);
			
			// 对workerrolerel表进行操作
			String sql_workerrolerel = "update workerrolerel set roleid=? where roleid=?";
			// workerrolerel参数
			List<Object> workerrolerel_param = new ArrayList<Object>();
			workerrolerel_param.add(newRoleID);
			workerrolerel_param.add(oldRoleID);
			
			sqlList.add(sql_workerrolerel);
			paramsList.add(workerrolerel_param);
		}
		count = Database.executeNonQueryTransaction(sqlList, paramsList);
		return count;
	}
	
	/**
	 * 删除角色
	 * @param roleID 角色id
	 * @return
	 */
	public static int deleteRole(String roleID) {
		// 返回值
		int count = 0;
		// 存放sql的集合
		List<String> sqlList = new ArrayList<String>();
		// 存放参数的集合
		List<List<?>> paramsList = new ArrayList<List<?>>();
		// 对role表操作
		String sql_role = "delete from role where roleid=?";
		// 对role_rule表操作
		String sql_role_rule = "delete from role_rule where roleid=?";
		// 对Workerrolerel表进行操作
		String sql_workerrolerel = "delete from workerrolerel where roleid=?";
		sqlList.add(sql_role);
		sqlList.add(sql_role_rule);
		sqlList.add(sql_workerrolerel);
		
		// worker参数
		List<Object> role_param = new ArrayList<Object>();
		role_param.add(roleID);
		// member参数
		List<Object> role_rule_param = new ArrayList<Object>();
		role_rule_param.add(roleID);
		// Workerrolerel参数
		List<Object> workerrolerel_param = new ArrayList<Object>();
		workerrolerel_param.add(roleID);
		
		
		paramsList.add(role_param);
		paramsList.add(role_rule_param);
		paramsList.add(workerrolerel_param);
		
		count = Database.executeNonQueryTransaction(sqlList, paramsList);
		
		return count;
	}
	
	/**
	 * 给角色设置逻辑表达式
	 * @param Map<String, String> 参数集合
	 * @return
	 */
	public static int setRuleToRole(List<Map<String, String>> params) {// 逻辑表达式格式：资源类型=xx&资源ID=xx,xx,xx...&属性类型=xx,xx,xx@属性类型=xx,xx&操作=xx,xx,xx&是否操作子业务=xxx; xx存放的都是编码
		// 返回值
		int count = 0;
		// 存放sql的集合
		List<String> sqlList = new ArrayList<String>();
		// 存放参数的集合
		List<List<?>> paramsList = new ArrayList<List<?>>();
		// 类型
		String resourceType = params.get(0).get("resourceType");
		// role_rule主键id
		String ID = params.get(1).get("ID");
		// 角色id
		String roleID = params.get(2).get("roleID");
		// 资源id
		String resourceID = params.get(3).get("资源名");
		
		// 构造逻辑表达式
		String logic = "";
		for (int i = 4; i < params.size()-2; i++) {
			Map<String,String> map = params.get(i);
			for(Entry<String,String> entry : map.entrySet()){
				logic += entry.getKey() + "=" + entry.getValue() + "@";
			}
		}
		if (!logic.equals("")) {
			logic = logic.substring(0, logic.lastIndexOf("@"));
		}
		// 操作权限
		String operateType = params.get(params.size()-2).get("操作权限");
		// 是否已关联子业务
		String isRelateChild = params.get(params.size()-1).get("是否关联子业务");
		logic = "资源类型="+resourceType+"&"+"资源名="+resourceID+"&"+logic+"&操作权限="+operateType+"&是否关联子业务="+isRelateChild;
		
		if ("".equals(ID)) {// 如果role_rule主键id为空
			// 为角色配置新的规则
			String insert_sql = "";
			if (GetConfigValue.isOracle) {
				insert_sql = "insert into role_rule (id,roleid,resourceType,logic) values (role_rule_sequence.nextval,?,?,?)";
			} else if(GetConfigValue.isMySQL) {
				insert_sql = "insert into role_rule (id,roleid,resourceType,logic) values (" + ConstructSerialNum.getSerialID("role_rule", "id") + ",?,?,?)";
			}
			List<Object> param_insert = new ArrayList<Object>();
			param_insert.add(roleID);
			param_insert.add(resourceType);
			param_insert.add(logic);
			sqlList.add(insert_sql);
			paramsList.add(param_insert);
		} else {
			// 为角色配置新的规则
			String insert_sql = "update role_rule set roleid=?,resourceType=?,logic=? where id=?";
			List<Object> param_update = new ArrayList<Object>();
			param_update.add(roleID);
			param_update.add(resourceType);
			param_update.add(logic);
			param_update.add(ID);
			sqlList.add(insert_sql);
			paramsList.add(param_update);
		}
		
		count = Database.executeNonQueryTransaction(sqlList, paramsList);
		
		return count;
	}
	
	/**
	 * 根据角色ID创建一个角色对象
	 * @param roleID
	 * @return
	 */
	public static Role constructRole(String roleID) {
		Result rs;
		// 返回值
		Role role = null;
		// 根据角色ID获得角色对象所需要的信息
		// 角色名称
		String roleName = "";
		// 角色所属的机构
		String belongCom = "";
		String sql = "select roleID,roleName,customer from role where roleID="+roleID;
		try{
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				// 角色名称
				roleName = rs.getRows()[0].get("roleName") != null ? rs.getRows()[0].get("roleName").toString() : "";
				// 角色所属的机构
				belongCom = rs.getRows()[0].get("customer") != null ? rs.getRows()[0].get("customer").toString() : "";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 查询逻辑表达式
		sql = "select logic from role_rule where roleID="+roleID;
		// 存储角色操作规则的集合
		List<RoleResourceAccessRule> ruleList = new ArrayList<RoleResourceAccessRule>();
		
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 逻辑表达式
					String logic = rs.getRows()[i].get("logic") != null ? rs.getRows()[i].get("logic").toString() : "";
					//根据逻辑表达式获得角色对资源的操作规则
					RoleResourceAccessRule ra = getRoleResourceByLogic(logic);
					ruleList.add(ra);
				}
			}
	
		//存储角色菜单加载项
		sql = "select menuname,loadmenuname from role_menu where roleID="+roleID;
		rs = Database.executeQuery(sql);
		String menuName ="";
		String loadMenuName  ="";
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				// 逻辑表达式
				 menuName = rs.getRows()[i].get("menuname") != null ? rs.getRows()[i].get("menuname").toString() : "";
				loadMenuName = rs.getRows()[i].get("loadmenuname") != null ? rs.getRows()[i].get("loadmenuname").toString() : "";
			}
		}
		role = new Role(roleID, roleName, ruleList, belongCom,menuName,loadMenuName);
		return role; 
	} 
	
	/**
	 * 查询包含指定资源操作权限的角色的规则
	 * @param roleList 角色集合
	 * @param resourceType 资源类型
	 * @param operateType 操作权限
	 * @return
	 */
	public static List<RoleResourceAccessRule> getRolesRuleByOperate(List<Role> roleList, String resourceType,String operateType) {
		// 返回的角色集合
		List<RoleResourceAccessRule> ruleList = new ArrayList<RoleResourceAccessRule>();
		for (Role role : roleList) {
			// 角色的操作规则
			List<RoleResourceAccessRule> rules = role.getRoleResourcePrivileges();
			for (RoleResourceAccessRule rule : rules) {
				if (NewEquals.equals(rule.getResourceType(),resourceType)) {// 是指定类型的规则
					if (rule.getOperateLimit().contains(operateType)) {// 如果角色拥有该操作权限
						ruleList.add(rule);
					}
				}
			}
		}
		return ruleList;
	}
	
	/**
	 * 构造角色树
	 * @param workerID 用户id
	 * @param customer 角色所属机构
	 * @return Map<String,Result>:<已有角色/所有角色,rs>
	 */
	public static Map<String,Result> constructRoleTree(String workerID, String customer) {
		// 返回值
		Map<String,Result> map = new HashMap<String, Result>();
		Result rs= null;
		String cs ="";
		// 查询该用户已有的角色
		String sql_hasRole = "select * from workerrolerel where workerID='"+workerID+"'";
		rs = Database.executeQuery(sql_hasRole);
		map.put("hasRole", rs);
		
		String sql_customer = "select customer from worker where workerID='"+workerID+"'";
			rs = Database.executeQuery(sql_customer);
			if(rs!=null && rs.getRowCount()>0){
				cs = rs.getRows()[0].get("customer") != null ? rs.getRows()[0].get("customer").toString() : "";
			}
		// 查询该组织机构对应的所有角色
		String sql_allRole = "select * from role where customer like '%"+cs+"%'";
		 rs = Database.executeQuery(sql_allRole);
			map.put("allRole", rs);
		return map;
	}
	
	/**
	 * 获得角色的规则信息
	 * @param roleID
	 * @param resourceType
	 * @param limit
	 * @param start
	 * @return
	 */
	public static Map<String,Result> selectResourceAttr(String roleID, String resourceType, int limit, int start) {
		// 返回值
		Map<String, Result> resultMap = new HashMap<String, Result>();
		// 统计条数的sql
		String innerSql = "select a.roleID as roleID,a.roleName as roleName,b.id as id,b.logic as logic from "+
						  "(select * from role where roleid like '"+roleID+"') a"+
						  " Left Join"+ 
						  " (Select * From Role_Rule Where Roleid Like '"+roleID+"' and resourceType='"+resourceType+"') B"+
						  " on a.RoleID=b.roleid";
		
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
				  " ("+innerSql+" order by roleID) t where rownum<"+(limit+start+1)+")"+
				  " where rn>"+start;
		} else if(GetConfigValue.isMySQL) {
			sql = innerSql + " order by roleID limit " + start + "," + limit;
		}
		
		Result rs = Database.executeQuery(sql);
		resultMap.put("data", rs);
		
		return resultMap;
	}
	
	/**
	 *  解析逻辑表达式获得可操作的角色资源权限
	 * @param logic 逻辑表达式
	 * @return
	 */
	public static RoleResourceAccessRule getRoleResourceByLogic(String logic) {// 逻辑表达式格式：资源类型=xx&资源名=xx,xx,xx...&属性类型=xx,xx,xx@属性类型=xx,xx&操作权限=xx,xx,xx&是否关联子业务=xxx; 
		// 返回值
		RoleResourceAccessRule ra = new RoleResourceAccessRule();
		// 解析逻辑表达式
		String[] logicArray = logic.split("&");
		// 资源类型
		String resourceType = logicArray[0].split("=")[1];
		// 资源名
		String resourceIDs = "";
		if (logicArray[1].split("=").length > 1) {// 资源id没有值，例如"资源名="，数组的length=1
			resourceIDs = logicArray[1].split("=")[1];
		}
		// 资源属性
		String[] attributes = logicArray[2].split("@");
		// 操作权限
		String operateType = "";
		if (logicArray[3].split("=").length > 1) {// 操作权限没有值，例如"操作权限="，数组的length=1
			operateType = logicArray[3].split("=")[1];
		}
		// 是否关联子业务
		String isRelateChild = "";
		if (logicArray[4].split("=").length > 1) {// 是否关联子业务没有值，例如"是否关联子业务="，数组length=1
			isRelateChild = logicArray[4].split("=")[1]; 
		}
		// 资源属性集合
		Map<String,String> map = new HashMap<String,String>();
		for (String attribute : attributes) {
			String[] array = attribute.split("=");
			String attributeType = array[0];
			String attributeValue = "";
			if (array.length > 1) {
				attributeValue = array[1];
			}
			map.put(attributeType, attributeValue);
		}
		
		// 设置对象属性
		ra.setResourceType(resourceType);
		ra.setOperateLimit(operateType);
		ra.setIsRelateChild(isRelateChild);
		if (resourceIDs.equals("")) {// 用户没有填写资源名
			ra.setResourceNames(new ArrayList<String>());
		} else {
			ra.setResourceNames(Arrays.asList(resourceIDs.split(",")));
		}
		ra.setAccessResourceMap(map);
		return ra;
	}
	
	
}
