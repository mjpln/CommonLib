package com.knowology.bll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

public class CommonLibClassifyqueryDAO {

	/**
	 * 分类问题查询
	 *  
	 * @param query
	 * @param city
	 * @param serviceid
	 * @param classified
	 * @param checked
	 * @param checktimeStart
	 * @param checktimeEnd
	 * @param inserttimeStart
	 * @param inserttimeEnd
	 * @return
	 */
	public static Result selectClassifyquery(String query, String city, String serviceid, String normalquery, String classified, String checked, String checktimeStart, String checktimeEnd, String inserttimeStart, String inserttimeEnd, boolean isPage, int page, int rows){
		String sql = "select id, query, applycode, applyname, channel, city, serviceid, service, kbdataid, abstract, classified, checked, to_char(checktime, 'YYYY-MM-DD HH24:MI:SS') checktime, to_char(inserttime, 'YYYY-MM-dd HH24:MI:SS') inserttime from classifyquery where 1=1";
		List<Object> params = new ArrayList<Object>();
		if(StringUtils.isNotBlank(query)){
			sql += " and query like ?";
			params.add("%" + query + "%");
		}
		if(StringUtils.isNotBlank(city)){
			if(!city.contains(",") && StringUtils.substring(city, -4).equals("0000")){
				city = city.substring(0,city.length() - 4);
			}
			sql += " and city like ?";
			params.add("%" + city + "%");
		}
		if(StringUtils.isNotBlank(normalquery)){
			sql += " and abstract like ?";
			params.add("%" + normalquery + "%");
		}
		if(StringUtils.isNotBlank(serviceid)){
			sql += " and serviceid = ?";
			params.add(serviceid);
		}
		if(StringUtils.isNotBlank(classified)){
			sql += " and classified = ?";
			params.add(classified);
		}
		if(StringUtils.isNotBlank(checked)){
			sql += " and checked = ?";
			params.add(checked);
		}
		if(StringUtils.isNotBlank(checktimeStart)){
			sql += " and checktime >= to_date(?,'YYYY-MM-DD HH24:MI:SS')";
			params.add(checktimeStart);
		}
		if(StringUtils.isNotBlank(checktimeEnd)){
			sql += " and checktime <= to_date(?,'YYYY-MM-DD HH24:MI:SS')";
			params.add(checktimeEnd);
		}
		if(StringUtils.isNotBlank(inserttimeStart)){
			sql += " and inserttime >= to_date(?,'YYYY-MM-DD HH24:MI:SS')";
			params.add(inserttimeStart);
		}
		if(StringUtils.isNotBlank(inserttimeEnd)){
			sql += " and inserttime <= to_date(?,'YYYY-MM-DD HH24:MI:SS')";
			params.add(inserttimeEnd);
		}
		sql += " order by checked, classified, query, applycode, applyname, channel, city";
		Result rs = null;
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + params );
		
		if(isPage){
			rs = queryByPage(sql, page, rows, params.toArray());
		}else{
			rs = Database.executeQuery(sql, params.toArray());
		}
		return rs;
	}
	
	public static int countClassifyquery(String query, String city, String serviceid, String normalquery, String classified, String checked, String checktimeStart, String checktimeEnd, String inserttimeStart, String inserttimeEnd){
		String sql = "select count(*) count from classifyquery where 1=1";
		List<Object> params = new ArrayList<Object>();
		if(StringUtils.isNotBlank(query)){
			sql += " and query like ?";
			params.add("%" + query + "%");
		}
		if(StringUtils.isNotBlank(city)){
			if(!city.contains(",") && StringUtils.substring(city, -4).equals("0000")){
				city = city.substring(0,city.length() - 4);
			}
			sql += " and city like ?";
			params.add("%" + city + "%");
		}
		if(StringUtils.isNotBlank(serviceid)){
			sql += " and serviceid = ?";
			params.add(serviceid);
		}
		if(StringUtils.isNotBlank(normalquery)){
			sql += " and abstract like ?";
			params.add("%" + normalquery + "%");
		}
		if(StringUtils.isNotBlank(classified)){
			sql += " and classified = ?";
			params.add(classified);
		}
		if(StringUtils.isNotBlank(checked)){
			sql += " and checked = ?";
			params.add(checked);
		}
		if(StringUtils.isNotBlank(checktimeStart)){
			sql += " and checktime >= to_date(?,'YYYY-MM-DD HH24:MI:SS')";
			params.add(checktimeStart);
		}
		if(StringUtils.isNotBlank(checktimeEnd)){
			sql += " and checktime <= to_date(?,'YYYY-MM-DD HH24:MI:SS')";
			params.add(checktimeEnd);
		}
		if(StringUtils.isNotBlank(inserttimeStart)){
			sql += " and inserttime >= to_date(?,'YYYY-MM-DD HH24:MI:SS')";
			params.add(inserttimeStart);
		}
		if(StringUtils.isNotBlank(inserttimeEnd)){
			sql += " and inserttime <= to_date(?,'YYYY-MM-DD HH24:MI:SS')";
			params.add(inserttimeEnd);
		}
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + params );
		Result rs = Database.executeQuery(sql, params.toArray());
		if(rs != null && rs.getRowCount() > 0){
			return Integer.parseInt(rs.getRows()[0].get("count").toString());
		}
		return 0;
	}
	/**
	 * 根据客户问题查询
	 * 
	 * @param query 客户问题
	 * @param classified 分类状态。false:未分类，true:分配，null:忽略该字段
	 * @param checked 审核状态。false:未审核， true:审核，null:忽略该字段
	 * @return
	 */
	public static Result selectClassifyqueryByQuery(String serviceid, String query, Boolean classified, Boolean checked){
		// 定义SQL语句
		String sql = "select c.* from service s, kbdata k, classifyquery c where s.serviceid = ? and s.serviceid = k.serviceid and k.kbdataid = c.kbdataid";
		// 定义绑定参数集合
		List<String> params = new ArrayList<String>();
		
		// 动态绑定参数
		params.add(serviceid);
		if (StringUtils.isNotBlank(query)){
			sql += " and query=?";
			params.add(query);
		}
		if (classified != null){
			sql += " and classified=?";
			params.add(classified ? "1" : "0");
		}
		if (checked != null){
			sql += " and checked=?";
			params.add(checked ? "1" : "0");
		}
		//文件日志
		GlobalValue.myLog.info( sql + "#" + params );
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, params.toArray());
		return rs;
	}
	
	/**
	 * 根据客户问题查询
	 * 
	 * @param query 客户问题
	 * @param classified 分类状态。false:未分类，true:分配，null:忽略该字段
	 * @param checked 审核状态。false:未审核， true:审核，null:忽略该字段
	 * @return
	 */
	public static Result selectClassifyqueryById(String id){
		// 定义SQL语句
		String sql = "select * from classifyquery where id=?";
		//文件日志
		GlobalValue.myLog.info( sql + "#" + id );
		Result rs = Database.executeQuery(sql, id);
		return rs;
	}
	
	/**
	 * 插入分类问题
	 * @param query
	 * @param applycode
	 * @param applyname
	 * @param channel
	 * @param city
	 * @param workerid
	 * @return
	 */
	public static int insertClassifyquery(String query, String applycode, String applyname, String channel, String city, String workerid){
		String sql = "insert into classifyquery(id, query, applycode, applyname, channel, city, inserttime, workerid) values(seq_classifyquery_id.nextval,?,?,?,?,?,sysdate,?)";
		List<String> params = Arrays.asList(query, applycode, applyname, channel, city, workerid);
		//文件日志
		GlobalValue.myLog.info( sql + "#" + params );
		int n = Database.executeNonQuery(sql, params.toArray());
		
		return n;
	}
	
	/**
	 * 更新分类问题
	 * @param id
	 * @param city
	 * @param workerid
	 * @return
	 */
	public static int updateClassifyquery(String id, String city, String workerid){
		String sql = "update classifyquery set city=?, workerid=? where id=?";
		List<String> params = Arrays.asList(city, workerid, id);
		//文件日志
		GlobalValue.myLog.info( sql + "#" + params );
		int n = Database.executeNonQuery(sql, params.toArray());
		
		return n;
	}
	
	/**
	 * 更新分类问题的业务模型
	 * @param id
	 * @param serviceid
	 * @param service
	 * @param kbdataid
	 * @param abs
	 * @return
	 */
	public static int updateClassfyquery(List<List<?>> listParams){
		String sql = "update classifyquery set serviceid=?, service=?, kbdataid=?, abstract=?, classified=1, workerid=? where id=?";
		List<String> sqls = new ArrayList<String>();
		for(int i = 0; i < listParams.size(); i++){
			sqls.add(sql);
		}
		//文件日志
		GlobalValue.myLog.info( sqls + "#" + listParams );
		int n = Database.executeNonQueryTransaction(sqls, listParams);
		return n;
	}
	
	/**
	 * 批量删除分类问题
	 * 只能删除未审核的问题分类
	 * @param ids
	 * @return
	 */
	public static int deleteClassifyquerys(String[] ids){
		String sql = "delete from classifyquery where id=? and checked=0";
		List<String> sqls = new ArrayList<String>();
		List<List<?>> listparams = new ArrayList<List<?>>();
		for(String id : ids){
			//文件日志
			sqls.add(sql);
			List<String> params = new ArrayList<String>();
			params.add(id);
			listparams.add(params);
			GlobalValue.myLog.info( sql + "#" + params );
		}
		
		int n = Database.executeNonQueryTransaction(sqls, listparams);
		
		return n;
	}
	
	/**
	 * 取得业务路径
	 * @param serviceid
	 * @return
	 */
	/*
	@SuppressWarnings("unchecked")
	public static String selectServicePath(String serviceid){
		StringBuilder sql = new StringBuilder();
		sql.append("WITH t (serviceid, parentid, service) AS");
		sql.append("  ( SELECT serviceid, parentid, service FROM service WHERE serviceid=?");
		sql.append("  UNION ALL");
		sql.append("  SELECT s.serviceid,");
		sql.append("    s.parentid,");
		sql.append("    s.service");
		sql.append("  FROM t,");
		sql.append("    service s");
		sql.append("  WHERE t.parentid=s.serviceid");
		sql.append("  )");
		sql.append("SELECT service, rownum r FROM t ORDER BY r DESC");
		
		String servicePath = "";
		Result rs = Database.executeQuery(sql.toString(), serviceid);
		if(rs != null && rs.getRowCount() > 0){
			for(Map row : rs.getRows()){
				servicePath = servicePath + row.get("service").toString() + "->";
			}
			if(servicePath.endsWith("->")){
				servicePath = StringUtils.substringBeforeLast(servicePath, "->");
			}
		}
		
		return servicePath;
	}
	*/
	
	/**
	 * 取得所有业务路径（根：安徽电信问题库、电信垃圾问题库）
	 * @return
	 */
	public static Result selectAllServicePath(){
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT service,");
		sql.append("  SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) NAME_PATH");
		sql.append("  FROM service");
		sql.append("  START WITH service  in ( '安徽电信问题库', '电信垃圾问题库')");
		sql.append("  CONNECT BY PRIOR serviceid  = parentid");
		//文件日志
		GlobalValue.myLog.info( sql );
		Result rs = Database.executeQuery(sql.toString());
		return rs;
	}
	
	
	/**
	 * 根据service名取得serviceid
	 * @param service
	 * @return
	 */
	public static String selectServiceidByService(String service){
		String sql = "select serviceid from service where service=?";
		//文件日志
		GlobalValue.myLog.info( sql + "#" + service );
		Result rs = Database.executeQuery(sql, service);
		if(rs != null && rs.getRowCount() > 0){
			return rs.getRows()[0].get("serviceid").toString();
		}
		return null;
	}
	
	
	/**
	 * 对单一问题进行分配处理
	 * @param id
	 * @param NPLAnswer
	 * @return
	 */
	public static boolean classify(String id, JSONObject nplAnswer, String userid){
		String success = nplAnswer.get("success").toString();
		// 判断NLP应答是否成功
		if (!"true".equals(success)) {
			return false;
		}
		
//		// 判断该分类问题id在数据库中是否存在
//		Result rs = CommonLibClassifyqueryDAO.selectClassifyqueryById(id);
//		if (rs == null || rs.getRowCount() <= 0) {
//			return false;
//		}
		
		JSONArray results = (JSONArray) nplAnswer.get("result");
		if(results != null){
			String updateSql = "update classifyquery set serviceid=?, service=?, kbdataid=?, abstract=?, classified=1, workerid=? where id=?";
			String insertSql = "insert into classifyquery(id, query, applycode, applyname, channel, province, city, serviceid, service, kbdataid, abstract, classified, inserttime, workerid)" +
					" select seq_classifyquery_id.nextval, query, applycode, applyname, channel, province, city, ?, ?, ?, ?, 1, systimestamp, ? from classifyquery where id = ? ";
			List<String> sqls = new ArrayList<String>();
			List<List<?>> listparams = new ArrayList<List<?>>();
			for(int i = 0; i < results.size(); i++){
				String service = ((JSONObject) results.get(i)).getString("service");
				String kbdataid = ((JSONObject) results.get(i)).getString("absid");
				String abs = ((JSONObject) results.get(i)).getString("abstract");
				String serviceid = CommonLibClassifyqueryDAO.selectServiceidByService(service);
				if(i == 0){
					sqls.add(updateSql);
				}else{
					sqls.add(insertSql);
				}
				List<String> params = new ArrayList<String>();
				params.addAll(Arrays.asList(serviceid, service, kbdataid, abs, userid, id));
				listparams.add(params);
			}

			//文件日志
			GlobalValue.myLog.info( sqls + "#" + listparams );
			
			// 执行sql
			int n = Database.executeNonQueryTransaction(sqls, listparams);
			if (n > 0) {
				return true;
			}else{
				return false;
			}
		}else{
			return false;
		}
	}
	
	/**
	 * 问题分类审核
	 * @param ids 问题分类id
	 * @return
	 */
	/**
	 * @param ids
	 * @param userid
	 * @return
	 */
	public static Object check(String[] ids, String userid){
		JSONObject jsonObj = new JSONObject();
		
		String insertSql = "insert into querymanage values(seq_querymanage_id.nextval,?,?,?,?,?,systimestamp, null)";
		String updateSql = "update querymanage set kbdataid=?, query=?, province=?, city=?, workerid=?, edittime=systimestamp where id=?";
		String checkedSql = "update classifyquery set checked=1, checktime=systimestamp where id=?";
		List<String> sqls = new ArrayList<String>();
		List<List<?>> listparams = new ArrayList<List<?>>();
		
		/*
		 1.该问题分类在classifyquery中是否存在并且为未审核
		 1.1 1为真，判断对querymanage中该数据是否存在； 存在时对city做并集操作；不存在时对querymanage做插入处理。
		 1.2 更新classifyquery审核状态、审核时间
		 */
		for(String id : ids){
			Result rs = selectClassifyqueryById(id);
			// 判断分类在classifyquery中是否存在并且已分配且未审核
			if ( rs != null && rs.getRowCount() > 0 && NewEquals.equals("0",rs.getRows()[0].get("checked").toString()) ){
//				if ( rs != null && rs.getRowCount() > 0 && "0".equals(rs.getRows()[0].get("checked").toString()) ){
//				if(!"1".equals(rs.getRows()[0].get("classified").toString())){

				if(!NewEquals.equals("1",rs.getRows()[0].get("classified").toString())){
					jsonObj.put("success", false);
					jsonObj.put("msg", "该客户问题未分配【" + rs.getRows()[0].get("query").toString() + "】");
					return jsonObj;
				}
				String kbdataid = rs.getRows()[0].get("kbdataid").toString();
				String query = rs.getRows()[0].get("query").toString();
				String province = rs.getRows()[0].get("province") == null ? "" : rs.getRows()[0].get("province").toString();
				String city = rs.getRows()[0].get("city").toString();
				rs = selectQuerymanageByKbdataidAndQuery(kbdataid, query);
				// 判断querymanage中该数据是否存在
				if ( rs != null && rs.getRowCount() > 0){
					String querymanageId = rs.getRows()[0].get("id").toString();
					String querymangeCity = rs.getRows()[0].get("city").toString();
					sqls.add(updateSql);
					// 对city做并集操作
//					if(!querymangeCity.equals(city)){
					if(!NewEquals.equals(querymangeCity,city)){
						city = unionCityCodes(querymangeCity, city);
						// 更新querymanage
						listparams.add(Arrays.asList(kbdataid, query, province, city, userid, querymanageId));
					}
				} else{
					// 对querymanage做插入处理
					sqls.add(insertSql);
					listparams.add(Arrays.asList(kbdataid, query, province, city, userid));
				}
				
				// 更新classifyquery审核状态
				sqls.add(checkedSql);
				listparams.add(Arrays.asList(id));
			}
		}
		// 没有可审核的分类问题
		if(sqls.size() <= 0){
			jsonObj.put("success", false);
			jsonObj.put("msg", "没有可审核的分类问题");
			return jsonObj;
		}

		System.out.println("审核sql:\n" + StringUtils.join(sqls.toArray(new String[sqls.size()]), "\n"));

		//文件日志
				GlobalValue.myLog.info( sqls + "#" + listparams );
		int n = Database.executeNonQueryTransaction(sqls, listparams);
		if(n > 0){
			jsonObj.put("success", true);
			jsonObj.put("msg", "审核成功");
		}else{
			jsonObj.put("success", false);
			jsonObj.put("msg", "审核失败");
		}
		
		return jsonObj;
	}
	
	
	/**
	 * 根据摘要id和客户问题查询问题库
	 * @param kbdataid
	 * @param query
	 * @return
	 */
	public static Result selectQuerymanageByKbdataidAndQuery(String kbdataid, String query){
		String sql = "select * from querymanage where kbdataid=? and query=?";
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + kbdataid + "," + query );
		Result rs = Database.executeQuery(sql, kbdataid, query);
		
		return rs;
	}
	
	/**
	 * 分页查询
	 * @param sql
	 * @param page
	 * @param rows
	 * @param obj
	 * @return
	 */
	private static Result queryByPage(String sql, int page, int rows, Object... obj) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * FROM ");
		sb.append(" (SELECT A.*, ROWNUM RN ");
		sb.append(" FROM (" + sql + ") A ");
		sb.append(" WHERE ROWNUM <=" + (rows * page) + ") ");
		sb.append(" WHERE RN >=" + (rows * (page - 1) + 1));
		//文件日志
		GlobalValue.myLog.info( sb.toString() + "#" + obj );
		return Database.executeQuery(sb.toString(), obj);
	}
	
	/**
	 * 取两个地市代码字符串的并集
	 * 
	 * @param codes1
	 * @param codes2
	 * @return
	 */
	private static String unionCityCodes(String codes1, String codes2){
		Set<String> set = new TreeSet<String>();
		String[] tmp1 = codes1.split(",");
		String[] tmp2 = codes2.split(",");
		for(String s : tmp1) {
			if(StringUtils.isNotBlank(s))
				set.add(s);
		}
		for(String s : tmp2) {
			if(StringUtils.isNotBlank(s))
				set.add(s);
		}
		return StringUtils.join(set.toArray(new String[set.size()]), ",");
	}
}
