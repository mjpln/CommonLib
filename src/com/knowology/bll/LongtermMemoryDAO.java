package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.dal.Database;

public class LongtermMemoryDAO {

	public static Result selectLongtermMemory(String userID)
	{
		try {
			String sql = "select key,value from LONGTERMMEMORY where userid='"+userID+"'";
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				return rs;
			} else {
				return null;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return null;
		}
	}
}
