package com.knowology.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.DbDAO.DBValueOper;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLib1Question3AnswersDao {
	
	
	/**
	 * hotquery 
	 */
	
	public static String getyaosutypebyname(String name)
	{
		String sql = "select yaosutype from yaosu where name =?";
		String re = Database.executeQueryAisa(sql, name);
		return re;
	}
	
	public static int updateHotquery(String queryid,int checked)
	{
		
		String sql = "";
		if(checked == 1)
		{
			sql="update hotquery set checked = 'y' where queryid In ("+queryid+")";
		}else{
			sql="update hotquery set checked = 'n' where queryid In ("+queryid+")";
		}
		
		System.out.println(sql);
		int c = 0;
		try {
			c = Database.executeNonQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	
	
	public static Result getHotqueryCount()
	{
		String sql = "";
		sql="select count(*) as total from hotquery where 1 = 1";

		Result rs = Database.executeQuery(sql);
		return rs;
	}
	public static Result getHotquery(int rows,int page) {
		String sql ="select * from(  select rownum rn,t.* from (select * from hotquery a left join (select kbdataid ,abstract from kbdata) b on  a.ABSTRACTID = b.kbdataid  order by a.checked desc , a.hot desc,a.freq desc ) t ) " +
				"where  rn<=" + page * rows +" and rn>"+ (page - 1) * rows;	
		
		
	System.out.println(sql);
	Result rs = Database.executeQuery(sql);
	return rs;
	}
	
	
	
	/**
	 * 查询摘要
	 */
	
	public static Result getKbdataCount(String abstractname) {
		String sql = "";
		sql="select count(*) as total from kbdata where 1 = 1";
		if(null!=abstractname && !abstractname.isEmpty())	
		{
			sql+=" and abstract like '%"+abstractname+"%' ";
		}
		Result rs = Database.executeQuery(sql);
		return rs;
	}
	public static Result getKbdatas(String abstractname,int rows,int page) {
		String sql = "";
		sql="select * from (select d.*,rownum rn from kbdata d where 1 = 1";
		if(null!=abstractname && !abstractname.isEmpty())	
		{
			sql+=" and abstract like '%"+abstractname+"%' ";
		}
		sql+=") t where t.rn<=" + page * rows +
				" and t.rn>"+ (page - 1) * rows;	
	System.out.println(sql);
	Result rs = Database.executeQuery(sql);
	return rs;
	}
	
	
	
	/**
	 * 以下为查询条件所使用的sql
	 */
	public static Result getRulescount(String ruletype,String weight,
			String title,String cityID,String begintime,String endtime,
			String applycode,String channel,String CONDITIONCHINESE,
			String excludedcity,String contentText) {
		String sql = "";
		sql="select count(*) as total from conditions1ton where 1 = 1";
		if(null!=ruletype && !ruletype.isEmpty())	
		{
			sql+=" and ruletype = "+ruletype;
		}
		if(null!=CONDITIONCHINESE && !CONDITIONCHINESE.isEmpty())	
		{
			sql+=" and CONDITIONCHINESE like '%"+CONDITIONCHINESE+"%' ";
		}
		if(null!=weight && !weight.isEmpty())	
		{
			sql+=" and weight =" +weight;
		}
		if(null!=title && !title.isEmpty())	
		{
			sql+=" and title like '%"+title+"%' ";
		}
		if(null!=cityID && !cityID.isEmpty())	
		{
			sql+=" and cityID like '%"+cityID+"%' ";
		}
		if(null!=begintime && !begintime.isEmpty())	
		{
			begintime=begintime+" 00:00:00";
			sql+=" and begintime >= to_date('"+begintime+"','yyyy-mm-dd hh24:mi:ss')";;
		}
		if(null!=endtime && !endtime.isEmpty())	
		{
			endtime=endtime+" 23:59:59";
			sql+=" and endtime <= to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss')";
		}
		if(null!=applycode && !applycode.isEmpty())	
		{
			sql+=" and applycode like '%"+applycode+"%' ";
		}
		if(null!=channel && !channel.isEmpty())	
		{
			sql+=" and channel like '%"+channel+"%' ";
		}
		if(null!=excludedcity && !excludedcity.isEmpty())	
		{
			sql+=" and excludedcity like '%"+excludedcity+"%' ";
		}
		if(null!=contentText && !contentText.isEmpty())	
		{
			sql+=" and contentText like '%"+contentText+"%' ";
		}
		Result rs = Database.executeQuery(sql);
		return rs;
	}
	
	
	public static Result getRules(int rows,int page,String ruletype,String weight,
			String title,String cityID,String begintime,String endtime,
			String applycode,String channel,String CONDITIONCHINESE,
			String excludedcity,String contentText) {
//		
//		ruletype
//		condition
//		weight
//		title
//		contentURL
//		contentID
//		cityID
//		cityname
//		excludedcity
//		begintime
//		endtime
//		applyname
//		applycode
//		channel
//		responsetype
//		ruleresponsetemplate
	
		String sql = "";
		sql="select * from (select d.*,rownum rn from conditions1ton d where 1 = 1";
		if(null!=ruletype && !ruletype.isEmpty())	
		{
			sql+=" and ruletype = "+ruletype;
		}
		if(null!=CONDITIONCHINESE && !CONDITIONCHINESE.isEmpty())	
		{
			sql+=" and CONDITIONCHINESE like '%"+CONDITIONCHINESE+"%' ";
		}
		if(null!=weight && !weight.isEmpty())	
		{
			sql+=" and weight =" +weight;
		}
		if(null!=title && !title.isEmpty())	
		{
			sql+=" and title like '%"+title+"%' ";
		}
		if(null!=cityID && !cityID.isEmpty())	
		{
			sql+=" and cityID like '%"+cityID+"%' ";
		}
		if(null!=begintime && !begintime.isEmpty())	
		{
			begintime=begintime+" 00:00:00";
			sql+=" and begintime >= to_date('"+begintime+"','yyyy-mm-dd hh24:mi:ss')";
		}
		if(null!=endtime && !endtime.isEmpty())	
		{
			endtime=endtime+" 23:59:59";
			sql+=" and endtime <= to_date('"+endtime+"','yyyy-mm-dd hh24:mi:ss')";
		}
		if(null!=applycode && !applycode.isEmpty())	
		{
			sql+=" and applycode like '%"+applycode+"%' ";
		}
		if(null!=channel && !channel.isEmpty())	
		{
			sql+=" and channel like '%"+channel+"%' ";
		}
		if(null!=excludedcity && !excludedcity.isEmpty())	
		{
			sql+=" and excludedcity like '%"+excludedcity+"%' ";
		}
		if(null!=contentText && !contentText.isEmpty())	
		{
			sql+=" and contentText like '%"+contentText+"%' ";
		}
		sql+=") t where t.rn<=" + page * rows +
					" and t.rn>"+ (page - 1) * rows;	
		System.out.println(sql);
		Result rs = Database.executeQuery(sql);
		return rs;
	}
	public static int delRules(String ids){
		String sql = "delete from conditions1ton where RULEID in (" + ids +")";
		int c = 0;
		try {
			c = Database.executeNonQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	
	public static int updateRule(String ruleid,String ruletype,String condition,
			String weight,String title,String contentURL,String contentID,
			String cityID,String cityname,String excludedcity,String begintime,
			String endtime,String applyname,String applycode,String channel,
			String CONDITIONCHINESE,String EXCLUDEDCITYNAME,String contentText) {
		
		begintime=begintime+" 00:00:00";
		if(!endtime.isEmpty() && endtime!=null)
		{
			endtime=endtime+" 23:59:59";
		}
		List<List<?>> listParams = new ArrayList<List<?>>();
		List<Object> param = new ArrayList<Object>();
		param.add(ruletype);param.add(condition);
		param.add(weight);	param.add(title);
		param.add(contentURL);	param.add(contentID);
		param.add(cityID);	param.add(cityname);
		param.add(excludedcity);	param.add(begintime);
		param.add(endtime);	param.add(applyname);		
		param.add(applycode);	param.add(channel);
		param.add(CONDITIONCHINESE);param.add(EXCLUDEDCITYNAME);	
		param.add(contentText);param.add(ruleid);
		listParams.add(param);
		
		String sql = "update conditions1ton set ruletype=? , condition=? ," +
				"weight=? , title=? ,contentURL=? , contentID=? ," +
				"cityID=? ,"+"cityname=? , excludedcity=? ,begintime=to_date(?,'yyyy-mm-dd hh24:mi:ss') , endtime=to_date(?,'yyyy-mm-dd hh24:mi:ss') ," +
				"applyname=? , applycode=? ,channel=? , CONDITIONCHINESE=? ," +
				"EXCLUDEDCITYNAME=? , contentText=? "
				+" where ruleid = ?";
		int r= 0;
		r = Database.executeNonQueryBatchTransaction(sql, listParams);
		return r;
	}
	
	
	
	public static int insertRule(String ruletype,String condition,
			String weight,String title,String contentURL,String contentID,
			String cityID,String cityname,String excludedcity,String begintime,
			String endtime,String applyname,String applycode,String channel,
			String CONDITIONCHINESE,String EXCLUDEDCITYNAME,String contentText){
		List<List<?>> listParams = new ArrayList<List<?>>();
		List<Object> param = new ArrayList<Object>();
		param.add(ruletype);param.add(condition);
		param.add(weight);param.add(title);
		param.add(contentURL);param.add(contentID);
		param.add(cityID);param.add(cityname);
		param.add(excludedcity);param.add(applyname);
		param.add(applycode);param.add(channel);
		param.add(CONDITIONCHINESE);param.add(EXCLUDEDCITYNAME);
		param.add(contentText);
		begintime=begintime+" 00:00:00";
		param.add(begintime);
		if(!endtime.isEmpty() && endtime!=null)
		{
			endtime=endtime+" 23:59:59";
			param.add(endtime);
		}
		listParams.add(param);
		
		String sql ="insert into conditions1ton " +
				" (RULEID,RULETYPE,CONDITION,WEIGHT,TITLE,CONTENTURL,CONTENTID,CITYID,CITYNAME," +
				"EXCLUDEDCITY,APPLYNAME,APPLYCODE,CHANNEL,CONDITIONCHINESE,EXCLUDEDCITYNAME,contentText,BEGINTIME,ENDTIME) " +
				"values (conditions1ton_sequence.nextval,?,?,?,?," +
				"?,?,?,?,?,?,?,?,?,?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss')";
		if(!endtime.isEmpty() && endtime!=null)
		{
			sql=sql+",to_date(?,'yyyy-mm-dd hh24:mi:ss'))";
		}else{
			sql=sql+","+null+")";
		}
		int result = Database.executeNonQueryBatchTransaction(sql, listParams);
		return result;
	}
	
	
	
	
	
	/**
	 * 以下为查询要素所使用的sql
	 */
	
	public static Result getYaosuCount(String showname ,String resultfw)
	{
		String sql = "";
		sql="select count(*) as total from  YAOSU where 1 = 1";
		if(null!=showname && !showname.isEmpty())	
		{
			sql+=" and showname like '%"+showname+"%' ";
		}
		if(null!=resultfw && resultfw.isEmpty())
		{
			sql+=" and resultfw like '%"+resultfw +"%' ";
		}		
		Result rs = Database.executeQuery(sql);
		return rs;
	}
	
	/**
	 * 
	 * @param rows
	 * @param page
	 * @param name
	 * @param resultfw
	 * @return
	 */
	
	public static Result getYaosu(int rows,int page,String showname,String resultfw) {
		String sql = "";
		sql="select * from (select d.*,rownum rn from YAOSU d where 1 = 1";
		if(null!=showname && !showname.isEmpty())	
		{
			sql+=" and showname like '%"+showname+"%' ";
		}
		if(null!=resultfw && resultfw.isEmpty())
		{
			sql+=" and resultfw like '%"+resultfw +"%' ";
		}		
		sql+=") t where t.rn<=" + page * rows +
					" and t.rn>"+ (page - 1) * rows;	
		Result rs = Database.executeQuery(sql);
		return rs;
	}
	public static Result getYaosuNames() {
		String sql = "select name,showname from YAOSU ";
		Result rs = Database.executeQuery(sql);
		return rs;
	}
	public static Result getYaosuresultbyName(String name) {
		String sql = "select resultfw from yaosu where NAME ='"+name+"'";
		Result rs = Database.executeQuery(sql);
		return rs;
	}
	
	
	
	
	
	
	public static int insertYaosu(String name ,String showname,String resultfw,String yaosutype ){
		List<List<?>> listParams = new ArrayList<List<?>>();
		List<Object> param = new ArrayList<Object>();
		param.add(name);
		param.add(resultfw);
		param.add(showname);
		param.add(yaosutype);
		listParams.add(param);
		String sql = "insert into YAOSU values " +
				"(yaosu_sequence.nextval,?,?,?,?)";
		int result = Database.executeNonQueryBatchTransaction(sql, listParams);
		return result;
	}

	
	
	public static int updateYaosu(String yaosuid,String name,String showname,String resultfw,String yaosutype) {
		List<List<?>> listParams = new ArrayList<List<?>>();
		List<Object> param = new ArrayList<Object>();
		param.add(name);
		param.add(showname);
		param.add(resultfw);
		param.add(yaosutype);
		param.add(yaosuid);
		listParams.add(param);
		String sql = "update YAOSU set name=? ,showname=? , resultfw=?,yaosutype =?where yaosuid = ?";
		int r= 0;
		r = Database.executeNonQueryBatchTransaction(sql, listParams);
		return r;
	}
	
	public static int delYaosu(String ids){
		String sql = "delete from yaosu where yaosuid in (" + ids +")";
		int c = 0;
		try {
			c = Database.executeNonQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

//查询应用渠道和技术渠道
	/**
	 * 
	 *描述：从数据库加载配置数据，一键对应多值
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-27 时间：下午05:13:27
	 *@param metafieldName
	 *@return Map<String,ArrayList<String>>
	 */
	public static Map<String, ArrayList<String>> readConfigFromDB1(
			String metafieldName) {
		Map<String, ArrayList<String>> dic = new HashMap<String, ArrayList<String>>();
		Result rst = CommonLibMetafieldmappingDAO.select(metafieldName);
		if (rst == null || rst.getRowCount() == 0)
			return new HashMap<String, ArrayList<String>>();
		for (SortedMap<Object, Object> row : rst.getRows()) {
			String key = DBValueOper.GetValidateStringObj4Null(row.get("mkey")
					.toString());
			String value = DBValueOper.GetValidateStringObj4Null(row.get(
					"mvalue").toString());
			if (!dic.containsKey(key)) {
				dic.put(key, new ArrayList<String>());
			}
			dic.get(key).add(value);
		}
		return dic;
	}

	/**
	 * 
	 *描述：从数据库加载配置数据，键值一一对应
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-27 时间：下午05:12:59
	 *@param metafieldName
	 *@return Map<String,String>
	 */
	public static HashMap<String, String> readConfigFromDB2(String metafieldName) {
		HashMap<String, String> dic = new HashMap<String, String>();
		try {
			Result rst = CommonLibMetafieldmappingDAO.select(metafieldName);
			if (rst == null) {
				return new HashMap<String, String>();
			}
			for (SortedMap<Object, Object> row : rst.getRows()) {
				String key = DBValueOper.GetValidateStringObj4Null(row.get(
						"mkey").toString());
				String value = DBValueOper.GetValidateStringObj4Null(row.get(
						"mvalue").toString());
				dic.put(key, value);
			}
		} catch (Exception e) {
			GlobalValue.myLog.error("【配置读取出错】" + metafieldName);
		}
		return dic;
	}

	
	public static void main(String[] args) {

		
		
//		Result rs=getKbdatas("机器人",10,1);
//		System.out.println(rs.getRows()[0].get("abstract"));
		
//		Result rs=getYaosuresultbyName("客户等级");
//		System.out.println(rs.getRows()[0].get("resultfw"));
//		
//		
//		Map<String, ArrayList<String>> dic = new HashMap<String, ArrayList<String>>();
//		dic=readConfigFromDB1("渠道参数配置");
//		HashMap<String, String> dic2 = new HashMap<String, String>();
//		dic2=readConfigFromDB2("applyCode业务渠道编码表配置");
//		System.out.println(dic.get("渠道"));
//		System.out.println(dic2);
	}
}
