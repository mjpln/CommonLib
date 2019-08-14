package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.dal.Database;

public class Role_RuleDAO {
	
	public static Result getLogicByRoleID(int roleID,String RESOURCETYPE){
		Result rs = null;
		String sql = "SELECT logic FROM role_rule where ROLEID=" +roleID+" and RESOURCETYPE='"+RESOURCETYPE+"'";
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

}
