package com.knowology.bll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.Role;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.UtilityOperate.StringUtil;
import com.knowology.dal.Database;
import com.str.NewEquals;

/**
 * 用户管理数据库操作
 * @author knowology
 *
 */
public class UserManagerDAO {
	/**
	 * 查询用户信息
	 * @param workerName 页面查询项：用户名称
	 * @param department 页面查询项： 部门
	 * @param num 工号
	 * @param customer 用户的所属机构
	 * @param limit 每页限制的条数
	 * @param start 开始条数
	 * @return Map<String, Result>：<"data/count","Result">数据类型，数据result
	 */
	public static Map<String, Result> selectUser(String workerName, String department, String num, String customer,int limit,int start) {
		// 返回值
		Map<String, Result> resultMap = new HashMap<String, Result>();
		// 统计条数的sql
		String innerSql = "select * from Worker where ";
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
		if(!"".equals(num) && num != null) {
			innerSql += " and workerID like '%" + num + "%'";
		}
		if (!"".equals(workerName) && workerName != null) {
			innerSql += " and name like '%" + workerName + "%'";
		}
		if (!"".equals(department) && department != null) {
			innerSql += " and department like '%" + department + "%'";
		}
		try {
			GlobalValue.myLog.info(innerSql);
			Result rs = Database.executeQuery(innerSql);
			resultMap.put("count", rs);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 分页的sql
		String sql = "";
		if(GetConfigValue.isOracle) {
			sql = "select * from (select t.*,rownum rn from" + " ("
			  + innerSql + " order by workerid) t where rownum<"
			  + (limit + start + 1) + ")" + " where rn>" + start;
		} else if(GetConfigValue.isMySQL) {
			sql = innerSql + " order by workerid limit " + start + "," + limit;
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
	 *  创建用户
	 * @param m_request
	 * @return
	 */
	public static int addUser(Map<String,String> params) {
		// 返回值
		int count = 0;
		// 存放sql的集合
		List<String> sqlList = new ArrayList<String>();
		// 存放参数的集合
		List<List<?>> paramsList = new ArrayList<List<?>>();
		
		// 查询wokerid是否已经存在
		String sql_checkID = "select * from worker where workerid='"+params.get("workerID")+"'";
		
		Result rs = Database.executeQuery(sql_checkID);
		if (rs.getRowCount() > 0) {
			count++;
		}
		
		if (count > 0) {// 该workerID已经被使用
			return count;
		}
		
		// 对worker表操作
		String sql_worker = "insert into worker(workerid,name,gender,phone,customer,department)"+
							" values (?,?,?,?,?,?)";
		// 对member表操作
		String sql_member = "";
		if(GetConfigValue.isOracle) {
			sql_member = "insert into member(workerid,pwdn,pwd,regdate) values (?,?,?,sysdate)";
		} else if(GetConfigValue.isMySQL) {
			sql_member = "insert into member(workerid,pwdn,pwd,regdate) values (?,?,?,sysdate())";
		}
		sqlList.add(sql_worker);
		sqlList.add(sql_member);
		
		// worker参数
		List<Object> worker_param = new ArrayList<Object>();
		worker_param.add(params.get("workerID"));
		worker_param.add(params.get("name"));
		worker_param.add(params.get("gender"));
		worker_param.add(params.get("phone"));
		worker_param.add(params.get("customer"));
		worker_param.add(params.get("department"));
		
		paramsList.add(worker_param);
		
		// member参数
		List<Object> member_param = new ArrayList<Object>();
		member_param.add(params.get("workerID"));
		//新密码
		member_param.add(StringUtil.EncryptMD5(StringUtil.EncryptMD5(params.get("pwd"))+params.get("workerID")));
		//向下兼容
		member_param.add(StringUtil.EncryptMD5(params.get("pwd")));
		paramsList.add(member_param);
		count = Database.executeNonQueryTransaction(sqlList, paramsList);
		
		return count;
	}
	
	/**
	 * 根据用户id删除用户
	 * @param workerID 用户id
	 * @return
	 */
	public static int deleteUser(String workerID) {
		// 返回值
		int count = 0;
		// 存放sql的集合
		List<String> sqlList = new ArrayList<String>();
		// 存放参数的集合
		List<List<?>> paramsList = new ArrayList<List<?>>();
		// 对worker表操作
		String sql_worker = "delete from worker where workerid=?";
		// 对member表操作
		String sql_member = "delete from member where workerid=?";
		// 对workerrolerel表操作
		String sql_workrole = "delete from workerrolerel where workerid=?";
		sqlList.add(sql_worker);
		sqlList.add(sql_member);
		sqlList.add(sql_workrole);
		
		// worker参数
		List<Object> worker_param = new ArrayList<Object>();
		worker_param.add(workerID);
		// member参数
		List<Object> member_param = new ArrayList<Object>();
		member_param.add(workerID);
		// workerrolerel参数
		List<Object> workerrole_param = new ArrayList<Object>();
		workerrole_param.add(workerID);
		
		paramsList.add(worker_param);
		paramsList.add(member_param);
		paramsList.add(workerrole_param);
		
		count = Database.executeNonQueryTransaction(sqlList, paramsList);
		
		return count;
	}
	
	/**
	 * 根据id查询对应的用户
	 * @param userID
	 * @return
	 */
	public static Result selectUserByID(String userID) {
		Result rs = null;
		// 查询wokerid是否已经存在
		String sql = "select w.workerid,w.name,w.gender,w.phone,w.customer,w.department,m.pwd from worker w,member m where w.workerid='"+userID+"' and w.workerid=m.workerid";
		rs = Database.executeQuery(sql);
		return rs;
	}
	
	/**
	 * 修改用户密码
	 * @param workerID 用户id
	 * @param pwd 密码
	 * @param newPwd 新密码
	 * @return
	 */
	public static int updateUserPwd(String workerID, String oldPwd, String newPwd) {
		// 返回值
		int count = 0;
		// 查询原密码
		String originalPwd = "";
		String sql_member = "select pwdn from member where workerid='"+workerID+"'";
		try{
			Result rs = Database.executeQuery(sql_member);
			if (rs != null && rs.getRowCount() > 0) {
				// 密码
				originalPwd = rs.getRows()[0].get("pwdn") != null ? rs.getRows()[0].get("pwdn").toString() : "";
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		// 比较用户录入的原密码是否正确
		if (originalPwd.equals(StringUtil.EncryptMD5(StringUtil.EncryptMD5(oldPwd)+workerID))) {
			// 对member表操作
			sql_member = "";
			if (GetConfigValue.isOracle) {
				sql_member = "update member set pwdn=?,pwd=?,regdate=sysdate where workerid=?";
			} else if(GetConfigValue.isMySQL) {
				sql_member = "update member set pwdn=?,pwd=?,regdate=sysdate() where workerid=?";
			}
			try {
				String pwd = StringUtil.EncryptMD5(newPwd);
				count = Database.executeNonQuery(sql_member, new Object[]{StringUtil.EncryptMD5(pwd+workerID),pwd,workerID});
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			return -1;
		}
		return count;
	}
	
	/**
	 * 重置用户密码
	 * @param workerID 用户id
	 * @param pwd 密码
	 * @param newPwd 新密码
	 * @return
	 */
	public static int resetUserPwd(String workerID, String newPwd) {
		// 返回值
		int count = 0;
		// 对member表操作
		String sql_member = "";
		if (GetConfigValue.isOracle) {
			sql_member = "update member set pwdn=?,pwd=?,regdate=sysdate where workerid=?";
		} else if(GetConfigValue.isMySQL) {
			sql_member = "update member set pwdn=?,pwd=?,regdate=sysdate() where workerid=?";
		}
		try {
			String oldPwd = StringUtil.EncryptMD5(newPwd);
			count = Database.executeNonQuery(sql_member, new Object[]{StringUtil.EncryptMD5(oldPwd+workerID),oldPwd,workerID});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 更新用户
	 * @param params
	 * @return
	 */
	public static int updateUser(Map<String,String> params) {
		// 返回值
		int count = 0;
		// 存放sql的集合
		List<String> sqlList = new ArrayList<String>();
		// 存放参数的集合
		List<List<?>> paramsList = new ArrayList<List<?>>();
		
		// 判断用户id是否变化
		if (NewEquals.equals(params.get("oldWorkerID").toString(),params.get("workerID").toString())) {// workerid没有变化，对worker，member操作
			// 对worker表操作
			String sql_worker = "update worker set name=?,gender=?,phone=?,customer=?,department=? where workerid=?";
			// worker参数
			List<Object> worker_param = new ArrayList<Object>();
			worker_param.add(params.get("name"));
			worker_param.add(params.get("gender"));
			worker_param.add(params.get("phone"));
			worker_param.add(params.get("customer"));
			worker_param.add(params.get("department"));
			worker_param.add(params.get("workerID"));
			
			sqlList.add(sql_worker);
			paramsList.add(worker_param);
			
		} else {// workerid被修改
			// 查询wokerid是否已经存在
			String sql_checkID = "select * from worker where workerid='"+params.get("workerID")+"'";
			try{
				Result rs = Database.executeQuery(sql_checkID);
				if (rs.getRowCount() > 0) {
					count--;
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			if (count == -1) {// 该workerID已经被使用
				return count;
			}
			
			// 对worker表操作
			String sql_worker = "update worker set workerid=?,name=?,gender=?,phone=?,customer=?,department=? where workerid=?";
			// worker参数
			List<Object> worker_param = new ArrayList<Object>();
			worker_param.add(params.get("workerID"));
			worker_param.add(params.get("name"));
			worker_param.add(params.get("gender"));
			worker_param.add(params.get("phone"));
			worker_param.add(params.get("customer"));
			worker_param.add(params.get("department"));
			worker_param.add(params.get("oldWorkerID"));

			sqlList.add(sql_worker);
			paramsList.add(worker_param);
			
			// 对member表操作
			String sql_member = "update member set workerID=? where workerid=?";
			// member参数
			List<Object> member_param = new ArrayList<Object>();
			member_param.add(params.get("workerID"));
			member_param.add(params.get("oldWorkerID"));
			
			sqlList.add(sql_member);
			paramsList.add(member_param);
						
			// 对workerrolerel操作
			String sql_workerrol = "update workerrolerel set workerid=? where workerid=?";
			// workerrolerel参数
			List<Object> workerrol_param = new ArrayList<Object>();
			workerrol_param.add(params.get("workerID"));
			workerrol_param.add(params.get("oldWorkerID"));
			
			sqlList.add(sql_workerrol);
			paramsList.add(workerrol_param);
		}
		count = Database.executeNonQueryTransaction(sqlList, paramsList);
		return count;
	}
	
	/**
	 * 给用户配置角色
	 * @param m_request
	 * @return
	 */
	public static int setRoleToWorker(String workerID, String roleIDs) {
		// 返回值
		int count = 0;
		// 存放sql的集合
		List<String> sqlList = new ArrayList<String>();
		// 存放参数的集合
		List<List<?>> paramsList = new ArrayList<List<?>>();
		// 角色集合
		String[] roleIDArray = roleIDs.split(",");
		
		String del_sql = "delete from workerrolerel where workerid=?";
		List<Object> param_del = new ArrayList<Object>();
		param_del.add(workerID);
		sqlList.add(del_sql);
		paramsList.add(param_del);
		
		// 为用户配置新的角色
		String insert_sql = "insert into workerrolerel (workerid,roleid) values (?,?)";
		for (String roleID : roleIDArray) {
			if(roleID == null || "".equals(roleID)){
				continue;
			}
			List<Object> param_insert = new ArrayList<Object>();
			param_insert.add(workerID);
			param_insert.add(roleID);
			sqlList.add(insert_sql);
			paramsList.add(param_insert);
		}
		
		count = Database.executeNonQueryTransaction(sqlList, paramsList);
		
		return count;
	}
	
	/**
	 * 构造登录用户对象
	 * @param sql 需要执行的sql
	 * @return
	 */
	public static Result constructLoginUser(String sql) {
		Result rs;
		rs = Database.executeQuery(sql);
		return rs;
	}
	
	public static List<Role> getRoleListByUserId(String userID){
		List<Role> roleList=new ArrayList<Role>();
		String sql = "select roleID from Workerrolerel where workerID='"+userID+"'";
		Result rs = constructLoginUser(sql);
		try {
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					String roleID = rs.getRows()[i].get("roleID") != null ? rs.getRows()[i].get("roleID").toString() : "";
					Role role = RoleManager.constructRole(roleID);
					roleList.add(role);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roleList;
	}
	
	
	/**
	 *获得用户关联角色ID
	 *@param userID
	 *@return 
	 *@returnType List<String> 
	 */
	public static List<String> getRoleIDListByUserId(String userID){
		List<String> roleList=new ArrayList<String>();
		String sql = "select roleID from Workerrolerel where workerID='"+userID+"'";
		Result rs = constructLoginUser(sql);
		try {
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					String roleID = rs.getRows()[i].get("roleID") != null ? rs.getRows()[i].get("roleID").toString() : "";
					roleList.add(roleID);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return roleList;
	}
}
