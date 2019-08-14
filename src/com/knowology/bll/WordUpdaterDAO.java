package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class WordUpdaterDAO {
	public static Result LoadWordClasses(int classno){
		Result rs = null;
		String sql = "";
		switch(classno){
		case 1:
			sql = "select wordclassid,wordclass from wordclass where wordclass not like 'SYS%'";
			break;
		case 2:
			sql = "select wordclassid,wordclass from wordclass";
			break;
		}
			
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static Result InitCheckedWords(){
		Result rs = null;
		String sql = "select stdwords.wordid stdwordid, stdwords.word stdword,words.wordid, words.word, words.type, words.wordclassid from word stdwords, word words where stdwords.wordid = words.stdwordid"
			+" union all"
			+" select stdwords.wordid stdwordid, stdwords.word stdword,words.wordid, words.word, words.type, words.wordclassid from word stdwords, word words where words.stdwordid is null and stdwords.wordid = words.wordid";
			
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 
	 * @param sqlno sql编号
	 * @param wordclassID 词类ID
	 * @return
	 */
	public static Result IsContainWordClassID(int sqlno, String wordclassID){
		Result rs = null;
		String sql = "";
		switch (sqlno){
		case 1:
			sql = "select * from wordclass where wordclassid = " + wordclassID + " and wordclass not like 'SYS%'";
			break;
		case 2:
			sql = "select * from wordclassinc where wordclassid = " + wordclassID + " and wordclass not like 'SYS%'";
			break;
		}		
			
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 
	 * @param sqlno sql编号
	 * @param wordclassID 词类ID
	 * @return
	 */
	public static Result GetWordClassByID(int sqlno, String wordclassID){
		Result rs = null;
		String sql = "";
		switch (sqlno){
		case 1:
			sql = "select * from wordclass where wordclassid = " + wordclassID;
			break;
		case 2:
			sql = "select * from wordclassinc where wordclassid = " + wordclassID;
			break;
		}		
			
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static Result Update(boolean flage, int sqlno, String WordClass){
		Result rs = null;
		String sql = "";
		switch(sqlno){
		case 1:
			if (flage) 
				sql = "SELECT StdWords.WordID StdWordID, StdWords.WORD stdword,Words.WordID, Words.word, Words.type, Words.WordClassID FROM Word StdWords, Word Words WHERE StdWords.wordid = Words.stdwordid"
	                +" union"
	                +" SELECT StdWords.WordID StdWordID, StdWords.WORD stdword,Words.WordID, Words.word, Words.type, Words.WordClassID FROM Word StdWords, Word Words WHERE Words.StdWordID IS NULL AND StdWords.WordId = Words.WordId";
			else 
				sql = "select distinct * from "
						+ "(SELECT "
						+ " StdWords.WordID StdWordID, StdWords.WORD stdword,"
						+ " Words.WordID, Words.word, Words.type, Words.WordClassID,"
						+ " Words.incstatus, Words.EditFlag, WORDS.EDITTIME"
						+ " FROM WordInc StdWords, WordInc Words"
						+ " WHERE StdWords.wordid = Words.stdwordid OR (Words.StdWordID IS NULL AND StdWords.WordId = Words.WordId)"
						+ " union"
						+ " SELECT"
						+ " StdWords.WordID StdWordID, StdWords.WORD stdword, "
						+ " Words.WordID, Words.word, Words.type, Words.WordClassID, "
						+ " Words.incstatus, Words.EditFlag, WORDS.EDITTIME "
						+ " FROM Word StdWords, WordInc Words "
						+ " WHERE StdWords.wordid = Words.stdwordid OR (Words.StdWordID IS NULL AND StdWords.WordId = Words.WordId) "
						+ ") winc order by edittime";
			break;
		case 2:
			sql = "select * from wordpat where wordpat like '%"+ WordClass + ">%'";
			break;
		}
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static int UpdateTrans(int wpid){
		// 保存事务处理的SQL和SQL对应的参数
		List<String> lstSQL = new ArrayList<String>();
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		String sql = "update wordpat set wordpat = replace(wordpat,'@','＠') where wordpatid="+ wpid;;
		lstSQL.add(sql);
		sql = "update wordpat set wordpat = replace(wordpat,'＠','@') where wordpatid="+ wpid;
		lstSQL.add(sql);
		sql = "delete from wordpatinc where wordpat like '%＠%' and wordpatid="+ wpid;
		lstSQL.add(sql);
		return Database.executeNonQueryTransaction(lstSQL, lstLstpara);
	}
	
	public static Result GetWordsByClass(int classno, Object wordClass, boolean flage) {
		Result rs = null;
		String sql = "";
		switch(classno){
		case 1:
			sql = "select word, wordid, stdwordid from word a where ";
			if (!flage)
				sql+="(a.wordid = a.stdwordid or a.stdwordid is null) and a.wordclassid="+wordClass;
			else
				sql += " a.wordclassid ="+wordClass;
			break;
		case 2:
			sql = "select word, wordid, stdwordid from word a, wordclass b where ";
			if (!flage)
				sql += " (a.wordid = a.stdwordid or a.stdwordid is null or a.type = '标准名称') and a.wordclassid = b.wordclassid and b.wordclass = '"
						+ wordClass.toString().replace("<", "").replace("!", "")
								.replace(">", "") + "'";
			else
				sql += " a.wordclassid = b.wordclassid and b.wordclass = '"
						+ wordClass.toString().replace("<", "").replace("!", "")
								.replace(">", "") + "'";
			break;
		}
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
//	public static int MarkProcessedDataInDB(Map<String, String> ProcessedData, String SelfIP){
//		// 保存事务处理的SQL和SQL对应的参数
//		List<String> lstSQL = new ArrayList<String>();
//		List<List<?>> lstLstpara = new ArrayList<List<?>>();
//		
//		for(String id : ProcessedData.keySet()){
//			String sql = "";
//			String[] a = id.split("#_#");
//			if(GetConfigValue.isMySQL){
//				if(!a[0].equals("")){
//					sql="update WordInc set IncStatus = concat(IncStatus, '#"
//						+ SelfIP + "#)' where ";
//					sql += "WordID = " + a[1];
//					sql += " and date_formate(EditTime,'%Y-%m-%d %H:%i:%s') = '" + a[2]+"'";
//				}
//				else{
//					sql="update WordInc set IncStatus = '#"
//						+ SelfIP + "#' where ";
//					sql += "WordID = " + a[1];
//					sql += " and EditTime = '" + a[2]+"'";
//				}
//			}
//			else{
//				sql = "update WordInc set IncStatus = IncStatus || '#"
//					+ SelfIP + "#' where ";
//				sql += "WordID = " + a[1];
//				sql += " and to_char(EditTime, 'yyyy-MM-dd hh24:mi:ss') = '" + a[2]+"'";
//			}
//		}
//		return Database.executeNonQueryTransaction(lstSQL, lstLstpara);
//	}
	
	public static int MarkProcessedDataInDB(String[] a, String SelfIP){
		String sql = "";
		
		if(GetConfigValue.isMySQL){
			if(!a[0].equals("")){
				sql="update WordInc set IncStatus = concat(IncStatus, '#"
					+ SelfIP + "#)' where ";
				sql += "WordID = " + a[1];
				sql += " and DATE_FORMAT(EditTime,'%Y-%m-%d %H:%i:%s') = '" + a[2]+"'";
			}
			else{
				sql="update WordInc set IncStatus = '#"
					+ SelfIP + "#' where ";
				sql += "WordID = " + a[1];
				sql += " and EditTime = '" + a[2]+"'";
			}
		}
		else{
			sql = "update WordInc set IncStatus = IncStatus || '#"
				+ SelfIP + "#' where ";
			sql += "WordID = " + a[1];
			sql += " and to_char(EditTime, 'yyyy-MM-dd hh24:mi:ss') = '" + a[2]+"'";
		}
		
		try{ 
			return Database.executeNonQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * @description selectDeleteProcessedDataInDB和DeleteProcessedDataInDB在原理类中是同一方法
	 * @return
	 */
	public static Result selectDeleteProcessedDataInDB(){
		Result rs = null;
		String sql = "select * from wordinc";
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 
	 * @param a 
	 * @return  >0 执行成功
				<0 没有执行相关sql或执行失败
	 */
	public static int DeleteProcessedDataInDB(String[] a){
		String sql = "delete wordinc where  WordID = " + a[0];
		if(GetConfigValue.isMySQL)
			sql += " and DATE_FORMAT(EditTime, '%Y-%m-%d %H:%i:%s')= '"+a[1]+"'";					
		else
			sql += " and to_char(EditTime, 'yyyy-MM-dd hh24:mi:ss') = '"+a[1]+"'";
		
		try{ 
			return Database.executeNonQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public static Result InitCheckedOrgs(){
		Result rs = null;
		String sql = "select org_id, org_name, org_short_name, org_alias from DYL_J_OUT_ORGAN_20120217";
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static Result UpdateOrgnizations(){
		Result rs = null;
		String sql = "select org_id, org_name, org_short_name, org_alias from DYL_J_OUT_ORGAN_20120217";
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
}
