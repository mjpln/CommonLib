package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.dal.Database;

public class WordSegmentDAO {
	/**
	 * 
	 * @param sqlno sql编号
	 * @return
	 */
	public static Result isNecessary(int sqlno){
		Result rs = null;
		String sql = "";
		switch (sqlno){
		case 1:
			sql = "select count(*) from wordpat where wordpat like '%[<!家庭近类%' or wordpat like '%|!家庭近类>]%'";
			break;
		case 2:
			sql = "select count(*) from wordpat where wordpat like '%!家庭近类%'";
			break;
		}		
			
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
}
