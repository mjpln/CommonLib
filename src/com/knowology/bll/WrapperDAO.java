package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.dal.Database;

public class WrapperDAO {
	public static Result GetMinCreditByAbsID(int abstractID){
		Result rs = null;
		String sql ="select s1.mincredit smincredit,s2.mincredit pmincredit from service s1,service s2 where s1.parentid=s2.serviceid and s1.serviceid =(select se.serviceid from kbdata kd,service se where kd.serviceid=se.serviceid and kd.kbdataid="+abstractID+")";
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	} 
}
