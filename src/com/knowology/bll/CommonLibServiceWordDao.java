package com.knowology.bll;

import java.sql.SQLException;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.dal.Database;

public class CommonLibServiceWordDao {

	public static Result getStandardWord(String band, String word, int rows, int page) {
		// 电信业务父类
		String sql = "";
		sql = "select w.wordid,w.stdwordid,w.word,w.type,w.city,w.cityname,c.wordclass,c.wordclassid from word w,wordclass c where w.wordclassid=c.wordclassid and c.wordclass='" + band + "' and w.stdwordid is null";
		if (word != null && !"".equals(word)){
			sql += " and word like '%" + word + "%'";
		}
		sql += " order by w.wordid desc";
		sql = "select * from (select rownum rn,t.* from (" + sql + ")t) where rn >" 
			+ (rows * (page-1)) 
			+ " and rn <= "
			+ (rows * page);
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	public static Result getStandardWordCount(String band, String word) {
		// 电信业务父类
		String sql = "";
		sql = "select count(*) total from word where wordclassid=(select wordclassid from wordclass where wordclass='" + band + "') and stdwordid is null";
		if (word != null && !"".equals(word)){
			sql += " and word like '%" + word + "%'";
		}
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}
	
	public static Result getOtherWordCount(String band, String word) {
		// 电信业务父类
		String sql = "";
		sql = "select count(*) total from word where wordclassid=(select wordclassid from wordclass where wordclass='" + band + "') and stdwordid is not null";
		if (word != null && !"".equals(word)){
			sql += " and word like '%" + word + "%'";
		}
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	public static int delStandardWord(String wordid) {
		int rs = -1;
		String sql = "delete from word where wordid =" + wordid;
		try {
			rs = Database.executeNonQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rs;
	}

	public static Result getOtherWordCount(String wordid) {
		String sql = "";
		sql = "select count(*) total from word where stdwordid=" + wordid;
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	public static Result getOtherWord(String wordid, int rows, int page) {
		// 电信业务父类
		String sql = "";
		sql = "select w.wordid,w.stdwordid,w.word,w.type,w.city,w.cityname,c.wordclass,c.wordclassid from word w,wordclass c where w.wordclassid=c.wordclassid and w.stdwordid=" + wordid + " order by w.wordid desc";
		sql = "select * from (select rownum rn,t.* from (" + sql + ")t) where rn >" 
			+ (rows * (page-1)) 
			+ " and rn <= "
			+ (rows * page);
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	public static Result getotherWord(String band, String word, int rows,
			int page) {
		// 电信业务父类
		String sql = "";
		sql = "select w.wordid,w.stdwordid,w.word,w.type,w.city,w.cityname,c.wordclass,c.wordclassid from word w,wordclass c where w.wordclassid=c.wordclassid and c.wordclass='" + band + "' and w.stdwordid is not null";
		if (word != null && !"".equals(word)){
			sql += " and word like '%" + word + "%'";
		}
		sql += " order by w.wordid desc";
		sql = "select * from (select rownum rn,t.* from (" + sql + ")t) where rn >" 
			+ (rows * (page-1)) 
			+ " and rn <= "
			+ (rows * page);
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	public static Result getStandardWordByOtherWordCount(String band,
			String word) {
		// 电信业务父类
		String sql = "";
		sql = "select count(*) total from word where wordid in (select stdwordid from word where wordclassid=(select wordclassid from wordclass where wordclass='" + band + "') and stdwordid is not null and word like '%" + word + "%')";
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	public static Result getStandardWordByOtherWord(String band, String word,
			int rows, int page) {
		// 电信业务父类
		String sql = "";
		sql = "select w.wordid,w.stdwordid,w.word,w.type,w.city,w.cityname,c.wordclass,c.wordclassid from word w,wordclass c where w.wordclassid=c.wordclassid and c.wordclass='" + band + "' and w.stdwordid is null";
		sql += " and wordid in (select stdwordid from word where stdwordid is not null and word like '%" + word + "%')";
		sql += " order by w.wordid desc";
		sql = "select * from (select rownum rn,t.* from (" + sql + ")t) where rn >" 
			+ (rows * (page-1)) 
			+ " and rn <= "
			+ (rows * page);
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

}
