package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.dal.Database;



public class CommonLibNewCheckGrammerDAO {
	public static Result getReturnValues() {
		Result rs = null;
	
			rs = Database.executeQuery(" select PATTERNKEY  from PatternKey ");
	
		return rs;
	}

	public static boolean IsWordClassExist(String wordclass) {
		String sql = "select * from wordclass where wordclass='" + wordclass
				+ "'";
		Result rs = null;
	
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
	
		if (rs == null || rs.getRows().length == 0)
			return false;
		return true;
	}
}
