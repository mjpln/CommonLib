package com.knowology.bll;

import java.sql.SQLException;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.lang.StringUtils;

import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class NLPInterfaceDAO {
	
	/**
	 * @description 渠道->业务的对应，（参数配置表的访问）
	 * @param name
	 * @return
	 */
	public static Result configureTableAccess(String name){
		Result rs = null;
		String sql = "select mf1.name mkey,"
				+ "mf1.type keytype,"
				+ "mf2.name mvalue,"
				+ "mf2.type valuetype " 
				+ "from metafield mf1, metafield mf2 "
				+ "where mf1.metafieldid = mf2.stdmetafieldid and " 
				+ "mf1.metafieldmappingid = (select metafieldmappingid from metafieldmapping where name=?)";
		GlobalValue.myLog.info("sql语句 : " + sql.replace("?", "'" + name + "'"));
		try{ 
			rs = Database.executeQuery(sql, name);
		} catch(Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return rs;
	}
	
	/**
	 * @description 通过摘要ID或者摘要名获取kbdata表中的客户摘要ID
	 * @param abstractName
	 * @param abstractID
	 * @return
	 */
	public static Result getAnswerLink(String abstractName, String abstractID){
		Result rs = null;
		String sql = "";
		try{ 
			/** ******************************************  逻辑是否正确, 不正确自行修改  */
			if(null == abstractName || abstractName.trim().equals("")) {
//				sql = "SELECT customer_kbdataid FROM kbdata where kbdataid="+abstractID;
				sql = "SELECT customer_kbdataid FROM kbdata where kbdataid=?";
				GlobalValue.myLog.info("sql语句 : " + sql.replace("?", "'" + abstractID + "'"));
				rs = Database.executeQuery(sql, abstractID);
			} else {
//				sql = "SELECT customer_kbdataid FROM kbdata where abstract='" + abstractName + "'";
				sql = "SELECT customer_kbdataid FROM kbdata where abstract=?";
				GlobalValue.myLog.info("sql语句 : " + sql.replace("?", "'" + abstractName + "'"));
				rs = Database.executeQuery(sql, abstractName);
			/** ******************************************   */
			}
		} catch(Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return rs;
	}
	
	/**
	 * @description 
	 * @param userId 
	 * @param ip
	 * @return
	 */
	public static int InsertUSERID2IPTABLE(String userId, String ip){
		String sql = "";
		/** ***********************************************************主键是否为ID，不是的话自行修改 */
		if(GetConfigValue.isMySQL)
			sql = "INSERT INTO USERID2IPTABLE(ID, USERID, IP) VALUES("+ConstructSerialNum.getSerialID("USERID2IPTABLE", "id")+", '" + userId + "', '" + ip + "')";
		/** *********************************************************** */
		else 
			sql = "INSERT INTO USERID2IPTABLE(ID, USERID, IP) VALUES(USERID2IPTABLE_SEQ.NEXTVAL, '" + userId + "', '" + ip + "')";
		GlobalValue.myLog.info("sql语句 : " + sql);
		try {
			return Database.executeNonQuery(sql);
		} catch (SQLException e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * @description 接口里面有一张对用户IP进行记录的表，USERID2IPTABLE，涉及：ID、IP、userID三个字段
	 * @return
	 */
	public static int EmptyUserId2IpTable(){
		String sql = "DELETE FROM USERID2IPTABLE";
		GlobalValue.myLog.info("sql语句 : " + sql);
		try {
			return Database.executeNonQuery(sql);
		} catch (SQLException e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return 0;
	}
	
	/**
	 * @description 根据用户ID获取用户IP
	 * @param userID 
	 * @return
	 */
	public static Result getUserIP(String userID){
		Result rs = null;
		String sql = "SELECT * FROM USERID2IPTABLE WHERE USERID = '" + userID + "'";
		sql = "SELECT * FROM USERID2IPTABLE WHERE USERID = ?";
		GlobalValue.myLog.info("sql语句 : " + sql.replace("?", "'" + userID + "'"));
		try {
			rs = Database.executeQuery(sql, userID);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return rs;
	}
	
	/**
	 * description 获取热点问题
	 * @param st 开始时间
	 * @param et 结束时间
	 * @param business 行业
	 * @return
	 */
	public static Result getHotQuestion(String st, String et, String business){
		Result rs = null;
		String sql = "";
		if(GetConfigValue.isMySQL)
			sql = "select abstract, count(abstract) as total from queryhistorylog " +
					"where service not like '%用户%' and service not like '%粗话脏话%' " +
					"and service not like '%流程%' " +
					"and service not in ('未匹配业务', '低分模糊回复', '天弘基金信息表', '闲聊', '天气', '提醒') and ABSTRACT not like '%相似问题获取%' " +
					"and ABSTRACT not like '%SYS%' and ABSTRACT not like '%交互%' and " +
					"starttime between str_to_date(?, '%Y-%m-%d %H:%i:%s') and " +
					"str_to_date(?,'%Y-%m-%d %H:%i:%s') and standinput like ? " +
					"group by abstract order by total desc";
		else
			sql = "select abstract, count(abstract) as total from queryhistorylog " +
					"where service not like '%用户%' and service not like '%粗话脏话%' " +
					"and service not like '%流程%' " +
					"and service not in ('未匹配业务', '低分模糊回复', '天弘基金信息表', '闲聊', '天气', '提醒') and ABSTRACT not like '%相似问题获取%' " +
					"and ABSTRACT not like '%SYS%' and ABSTRACT not like '%交互%' and " +
					"starttime between to_date(?, 'yyyy-mm-dd hh24:mi:ss') and " +
					"to_date(?,'yyyy-mm-dd hh24:mi:ss') and standinput like ? " +
					"group by abstract order by total desc";
		
		try{
			if (null == business || "".equals(business)) {
				business = "";
			}
			rs = Database.executeQuery(sql, st, et, "%" + business + "%");
		} catch(Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return rs;
	}
	
	/**
	 * @description 预加载机IPTV敏感词字典，通过数据库表的方式进行加载
	 * @param String 表字段名
	 * @return
	 */
	public static Result LoadIPTVSenDicFromDB() {// IPTV禁词父类
		String sql = "select word from word where wordclassid =(select wordclassid from wordclass where wordclass='IPTV敏感词父类') and type='标准名称'";
		Result dt = null;
		GlobalValue.myLog.info("sql语句 : " + sql);
		try {
			dt = Database.executeQuery(sql);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return dt;
	}
	
	/**
	 * @description 预加载摘要答案字典
	 * @return
	 */
	public static Result LoadAbsBusiChan_Ans() {
		Result rs = null;
		String sql = "SELECT b.abstract,"
				+ "f.servicetype,"
				+ "f.channel,"
				+ "f.answercategory,"
				+ "g.answercontent,"
				+ "g.answer_clob,"
				+ "b.customer_kbdataid "
				+ "FROM KBData b,Kbansvaliddate c,Kbanspak d,Kbansqryins e,Kbcontent f,Kbanswer g "
				+ "WHERE b.kbdataid =c.kbdataid "
				+ "AND c.kbansvaliddateid=d.kbansvaliddateid "
				+ "AND d.kbanspakid =e.kbanspakid "
				+ "AND e.kbansqryinsid =f.kbansqryinsid "
				+ "AND f.kbcontentid =g.kbcontentid";
		GlobalValue.myLog.info("sql语句 : " + sql);
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return rs;
	}
	
	
	/**
	 * @description  加载系统扩展问题表信息
	 * 
	 */
	public static Result LoadExtendQueAbsMap() {
		Result rs = null;
		String sql = "select * from REGRESSQUERY";
		GlobalValue.myLog.info("sql语句 : " + sql);
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return rs;
	}
	
	/**
	 * 查询默认回复答案
	 * @param business
	 * @param channel
	 * @return
	 */
	public static Result findDefaultQueryAnswer(String business, String channel) {
		if (StringUtils.contains(business, "->") && StringUtils.split(business, "->").length > 1) {
			business = "<" + StringUtils.split(business, "->")[1] + ">" + "SYS默认回复";
		} else {
			return null;
		}
		Result rs = null;
		String sql = "select g.answercontent "
				+ "from service a, kbdata b, kbansvaliddate c, kbanspak d, kbansqryins e, kbcontent f, kbanswer g "
				+ "where a.serviceid = b.serviceid "
				+ "and b.kbdataid = c.kbdataid "
				+ "and c.kbansvaliddateid = d.kbansvaliddateid "
				+ "and d.kbanspakid = e.kbanspakid "
				+ "and e.kbansqryinsid = f.kbansqryinsid "
				+ "and f.kbcontentid = g.kbcontentid "
				+ "and b.abstract = ? "
				+ "and f.channel = ? ";
		GlobalValue.myLog.info("sql语句 : " + sql.replace("b.abstract = ?", "b.abstract = '" + business + "'")
				.replace("f.channel = ?", "f.channel = '" + channel + "'"));
		try{ 
			rs = Database.executeQuery(sql, business, channel);
		} catch(Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return rs;
	}
	
	public static Result findStationByUserId(String userId) {
		Result rs = null;
		String sql = "select isEnd from csruserrecord where userId = ?";
		try{ 
			rs = Database.executeQuery(sql, userId);
		} catch(Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return rs;
	}
	
	public static Result updateStationByUserId(String userId) {
		Result rs = null;
		String sql = "update csruserrecord set isend = 1 where userId = ?";
		try{ 
			rs = Database.executeQuery(sql, userId);
		} catch(Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		}
		return rs;
	}
	
}
