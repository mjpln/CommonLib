package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.dal.Database;

public class CommonLibNlpAnalysisDAO {

	public static Result getiplist(String business) {
		String sql = "";
		Result rs = null;
		sql = "select s.name ip from metafield t,metafield s,metafieldmapping a where a.name='nlplog地址配置' and t.name='" + business + "' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid";
		rs = Database.executeQuery(sql);
		return rs;
	}
	
}
