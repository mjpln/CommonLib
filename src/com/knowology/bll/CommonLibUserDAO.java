
package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.dal.Database;




public class CommonLibUserDAO {
	
	/**
	 *描述：@description  通过员工ID 查询员工基本信息
	 *参数：@param workerid
	 *参数：@return
	 *返回值类型：@returnType Result
	 *创建时间：@dateTime 2015-9-18下午12:19:19
	 *作者：@author wellhan
	 */
	public static Result getUserInfo(String workerid){
		String sql = "select * from worker w ,member m  where w.workerid = m.workerid and  w.workerid='"
			+ workerid + "'";
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		Result rs = Database.executeQuery(sql);
		return rs;
	} 
	


	/**
	 *描述：@description 通过员工ID 查询员工角色信息
	 *参数：@param workerid
	 *参数：@return
	 *返回值类型：@returnType Result
	 *创建时间：@dateTime 2015-9-18下午12:21:32
	 *作者：@author wellhan
	 */
	public static Result getUserRoleInfo(String workerid){
		String sql = "select r.rolename ,r.customer customer from role r,workerrolerel w where r.roleid=w.roleid and  w.workerid=?";
		//文件日志
		GlobalValue.myLog.info( sql );
		Result rs = Database.executeQuery(sql,workerid);
		return rs;
	} 
}
