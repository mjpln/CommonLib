package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.dal.Database;

public class CommonLibDoSqlDao {

	public static Result doSelect(String sql) {
		Result rs = Database.executeQuery(sql);
		return rs;
	}
}
