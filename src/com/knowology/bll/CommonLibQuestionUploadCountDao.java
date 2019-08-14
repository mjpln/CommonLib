package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.dal.Database;

public class CommonLibQuestionUploadCountDao {

	public static Result countbyProvince(String locString, String starttime, String endtime) {
		
		String sql = "";
		Result rs = null;
		sql = "select * from"
			+ "(select proname,username,peo,id from ("
			+ " select province,username,count(*) as peo from " 
			+ "(select * from hotquestion where province is not null ";
		if (starttime != null && !"".equals(starttime)){
			sql += "and uploadtime>= to_date('"
				+ starttime +" 00:00:00" + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (endtime != null && !"".equals(endtime)){
			sql += " and uploadtime<= to_date('"
				+ endtime + "23:59:59" + "','yyyy-mm-dd hh24:mi:ss')";
		}
		sql+= ")" 
			+ "where province in ("
			+ locString
			+ ")group by province,username),"
			+ "(select t.name as id,max(s.name) as proname from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' group by t.name order by id"
			+ ") where id = province)"
			+ "left join" 
			+ "(select province as pro,count(*) as total from hotquestion where province is not null group by province )"
			+ "	on pro=id";
		rs = Database.executeQuery(sql);
		return rs;
	}

	public static Result countbyGroup(String starttime, String endtime,
			String province) {
		String sql = "";
		Result rs = null;
		sql = "select proname,total from(select province,count(*) as total from " 
			+ "(select * from hotquestion where province is not null " ;
			if (starttime != null && !"".equals(starttime)){
				sql += "and uploadtime>= to_date('"
					+ starttime +" 00:00:00" + "','yyyy-mm-dd hh24:mi:ss')";
			}
			if (endtime != null && !"".equals(endtime)){
				sql += " and uploadtime<= to_date('"
					+ endtime + "23:59:59" + "','yyyy-mm-dd hh24:mi:ss')";
			}
			if (province != null && !"".equals(province)){
				sql += " and province ='"
					+ province + "'";
			}
			sql += ") group by province),(select t.name as id,max(s.name) as proname from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' group by t.name order by id) where id = province";
		rs = Database.executeQuery(sql);
		return rs;
	}

}
