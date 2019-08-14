package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class PatternUpdaterDAO {
	/**
	 * 方法名称： Update
	 * 内容摘要：词模更新的入口函数
	 * 针对是否全量更新或增量更新，将词模表或词模增量表的数据实时更新到NLP内存知识库中
	 * @param flage
	 * @return
	 */
	public static Result update(boolean flage){
		Result rs = null;
		String sql = "";
		if (!flage) {
			sql = "select distinct A.*, A.EditTime t, C.BRAND as brand"
					+ " from WordPatInc A, KBData B, Service C"
					+ " where A.KbDataId = B.KbDataId and B.Serviceid = C.Serviceid"
					+ " and (EditFlag = 'A' or EditFlag = 'U')"
					+ " union"
					+ " select distinct A.*, A.EditTime t,  'whatever' as BRAND"
					+ " from WordPatInc A" 
					+ " where EditFlag = 'D'"
					+ " order by t";
		} else {
			sql = "select distinct A.*, C.BRAND"
					+ " from WordPat A, KBData B, Service C"
					+ " where A.KbDataId = B.KbDataId and B.Serviceid = C.Serviceid";
		}
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 方法名称： MarkProcessedDataInDB
	 * 内容摘要：在数据库中，标记已经加载到内存知识库中的数据。方法是在“IncStatus”列中，加上本机的IP
	 * @param a
	 * @param SelfIP
	 * @return
	 */
	public static int MarkProcessedDataInDB(String[] a, String SelfIP){
		String sql = "";
		if(GetConfigValue.isMySQL){
			if(!a[0].equals("")){
				/* a[0]的数值是IncStatus从数据库查回来的值,mysql中concat不能处理null的拼接,
				 * 故而这里需要单独处理,oracle则直接用||就可以完成 */
				sql="update WordPatInc set IncStatus = concat(IncStatus, '#"
					+ SelfIP + "#)' where ";
				sql += "WordPatID = " + a[1];
				sql += " and DATE_FORMAT(EditTime,'%Y-%m-%d %H:%i:%s') = '" + a[2]+"'";
			}
			else{
				sql="update WordPatInc set IncStatus = '#"
					+ SelfIP + "#' where ";
				sql += "WordPatID = " + a[1];
				sql += " and EditTime = '" + a[2]+"'";
			}
		}
		else{
			sql = "update WordPatInc set IncStatus = IncStatus || '#"
				+ SelfIP + "#' where ";
			sql += "WordPatID = " + a[1];
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
	 * 方法名称： DeleteProcessedDataInDB
	 * 内容摘要：删除增量表中，已经更新到内存中、并且保存了的数据
	 * @description selectDeleteProcessedDataInDB和DeleteProcessedDataInDB在原理类中是同一方法
	 * @return
	 */
	public static Result selectDeleteProcessedDataInDB(){
		Result rs = null;
		String sql = "select * from WordpatInc";
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	/**
	 * 方法名称： DeleteProcessedDataInDB
	 * 内容摘要：删除增量表中，已经更新到内存中、并且保存了的数据
	 * @param a 
	 * @return  >0 执行成功
				<0 没有执行相关sql或执行失败
	 * @author bibin
	 */
	public static int DeleteProcessedDataInDB(String[] a){
		String sql = "delete WordPatInc where  WordPatID = " + a[0];

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
}
