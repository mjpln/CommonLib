package com.knowology.bll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;


public class CommonLibWordpatDAO {

	
	
	/**
	 *@description 查询模板记录数 
	 *@param brand 品牌
	 *@param service 业务
	 *@param kbdataid 摘要ID
	 *@param selectWordpat 查询词模内容
	 *@return 
	 *@returnType Result 
	 */
	public static Result getWordpatCount(String brand,String service,String kbdataid,String selectWordpat){
		// 定义返回的json串

		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义查询的条件的SQL语句
		paramSql
				.append(" from service a,kbdata b,wordpat c where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid  ");

		paramSql
				.append(" and a.brand=? and a.service=? and b.kbdataid =?");
		// 绑定品牌参数
		lstpara.add(brand);
		// 绑定业务名称参数
		lstpara.add(service);
	    // 绑定摘要id参数
		lstpara.add(kbdataid);
		// 判断词模是否为空，null
		if (selectWordpat != null && !"".equals(selectWordpat)
				&&selectWordpat.length() > 0) {
//			if("auto".equalsIgnoreCase(selectWordpat)){//判断询auto词模
			if(selectWordpat.contains("auto")){//判断询auto词模
				// 加上词模的查询条件
//				paramSql.append(" and c.wordpat like ? and (c.wordpattype =5 or  c.wordpat  like '%编者=\"问题库\"%') ");
				paramSql.append(" and c.wordpat like ? and (c.wordpattype =5 ) ");
				selectWordpat = selectWordpat.replace("auto", "");
				lstpara.add("%"+selectWordpat+"%");	
			}else{
				// 加上词模的查询条件
//				paramSql.append(" and c.wordpat like ? and c.wordpattype!=5 and c.wordpat not like '%编者=\"问题库\"%'");
				paramSql.append(" and c.wordpat like ? and c.wordpattype!=5 ");
				// 绑定词模参数
				lstpara.add("%"+selectWordpat+"%");	
			}
		}else{
//			paramSql.append(" and c.wordpattype !=5 and c.wordpat not like '%编者=\"问题库\"%' ");
			paramSql.append(" and c.wordpattype !=5 ");
		}
			// 获取查询满足条件的数量的SQL语句
			String countSql = "select count(*) count from (select distinct c.wordpat,c.autosendswitch autosendswitch "
					+ paramSql.toString() + ") t2";
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(countSql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( countSql + "#" + lstpara );
			return rs;
		
	}
	
	/**
	 *@description 查询模板记录数 
	 *@param brand 品牌
	 *@param service 业务
	 *@param kbdataid 摘要ID
	 *@param selectWordpat 查询词模内容
	 *@return 
	 *@returnType Result 
	 */
	public static Result getWordpatCount_old(String brand,String service,String kbdataid,String selectWordpat){
		// 定义返回的json串

		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义查询的条件的SQL语句
		paramSql
				.append(" from service a,kbdata b,wordpat c where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid  ");

		paramSql
				.append(" and a.brand=? and a.service=? and b.kbdataid =?");
		// 绑定品牌参数
		lstpara.add(brand);
		// 绑定业务名称参数
		lstpara.add(service);
	    // 绑定摘要id参数
		lstpara.add(kbdataid);
		// 判断词模是否为空，null
		if (selectWordpat != null && !"".equals(selectWordpat)
				&&selectWordpat.length() > 0) {
				// 加上词模的查询条件
				paramSql.append(" and c.wordpat like ? ");
				// 绑定词模参数
				lstpara.add("%"+selectWordpat+"%");	
		}
			// 获取查询满足条件的数量的SQL语句
			String countSql = "select count(*) count from (select distinct c.wordpat,c.autosendswitch autosendswitch "
					+ paramSql.toString() + ") t2";
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(countSql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( countSql + "#" + lstpara );
			return rs;
		
	}

	/**
	 *@description 查询模板数据
	 *@param brand  品牌
	 *@param service 业务
	 *@param kbdataid 摘要ID
	 *@param selectWordpat 查询词模内容
	 *@param start 开始记录数
	 *@param limit 间隔记录数
	 *@return 
	 *@returnType Result 
	 */
	public static Result select(String brand,String service,String kbdataid,String selectWordpat,String start,String limit) {
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		if(GetConfigValue.isOracle){
//			paramSql.append("select t2.* from(select t1.* ,rownum rn from (select  c.simplewordpat,c.wordpat,c.wordpatid wordpatid,c.city city,d.wpprecision correctratio,d.callvolume callnumber,c.wordpattype wordpattype ,c.autosendswitch autosendswitch ");
			paramSql.append("select t2.* from(select t1.* ,rownum rn from (select  c.simplewordpat,c.wordpat,c.wordpatid wordpatid,c.city city,c.wordpattype wordpattype ,c.autosendswitch autosendswitch ");
		}else if(GetConfigValue.isMySQL){
			paramSql.append("select t2.* from(select t1.* from (select  c.simplewordpat,c.wordpat,c.wordpatid wordpatid,c.city city,c.wordpattype wordpattype ,c.autosendswitch autosendswitch ");
		}
		// 定义查询的条件的SQL语句
		paramSql
				.append(" from service a,kbdata b,wordpat c where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid  and a.brand=? and a.service=? and b.kbdataid =? ");
		// 绑定品牌参数
		lstpara.add(brand);
		// 绑定业务名称参数
		lstpara.add(service);
	    // 绑定摘要id参数
		lstpara.add(kbdataid);
		// 判断词模是否为空，null
		if (selectWordpat != null && !"".equals(selectWordpat)
				&&selectWordpat.length() > 0) {
		if(selectWordpat.contains("auto")){//判断询auto词模
					// 加上词模的查询条件
//					paramSql.append(" and c.wordpat like ? and ( c.wordpattype =5 or  c.wordpat like '%编者=\"问题库\"%' )  ");
					paramSql.append(" and c.wordpat like ? and ( c.wordpattype =5 )  ");
					selectWordpat = selectWordpat.replace("auto", "");
					lstpara.add("%"+selectWordpat+"%");	
		}else
			{
				// 加上词模的查询条件
//				paramSql.append(" and c.wordpat like ? and c.wordpattype!=5 and c.wordpat not like '%编者=\"问题库\"%' ");
				paramSql.append(" and c.wordpat like ? and c.wordpattype!=5  ");
				// 绑定词模参数
				lstpara.add("%"+selectWordpat+"%");	
			}
		}else{
//			paramSql.append(" and c.wordpattype !=5 and c.wordpat not like '%编者=\"问题库\"%'  ");
			paramSql.append(" and c.wordpattype !=5  ");
		}
		
		if(GetConfigValue.isOracle){
			 paramSql.append(" )t1)t2 where t2.rn>? and t2.rn<=? ");
			// 绑定开始条数参数
				lstpara.add(Integer.parseInt(start));
				// 绑定截止条数参数
				lstpara.add(Integer.parseInt(start)+Integer.parseInt(limit));
		}else if(GetConfigValue.isMySQL){
			 paramSql.append(" )t1)t2  limit ?,?");
			// 绑定开始条数参数
				lstpara.add(Integer.parseInt(start));
				// 绑定截止条数参数
				lstpara.add(Integer.parseInt(limit));
		}
				// 执行SQL语句，获取相应的数据源
				Result rs = Database.executeQuery(paramSql.toString(), lstpara.toArray());
				
				//文件日志
				GlobalValue.myLog.info( paramSql + "#" + lstpara );
				
		        return rs;
	}
    
	/**
	 *@description 查询模板数据
	 *@param brand  品牌
	 *@param service 业务
	 *@param kbdataid 摘要ID
	 *@param selectWordpat 查询词模内容
	 *@param start 开始记录数
	 *@param limit 间隔记录数
	 *@return 
	 *@returnType Result 
	 */
	public static Result select_old(String brand,String service,String kbdataid,String selectWordpat,String start,String limit) {
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		if(GetConfigValue.isOracle){
//			paramSql.append("select t2.* from(select t1.* ,rownum rn from (select  c.simplewordpat,c.wordpat,c.wordpatid wordpatid,c.city city,d.wpprecision correctratio,d.callvolume callnumber,c.wordpattype wordpattype ,c.autosendswitch autosendswitch ");
			paramSql.append("select t2.* from(select t1.* ,rownum rn from (select  c.simplewordpat,c.wordpat,c.wordpatid wordpatid,c.city city,c.wordpattype wordpattype ,c.autosendswitch autosendswitch ");
		}else if(GetConfigValue.isMySQL){
			paramSql.append("select t2.* from(select t1.* from (select  c.simplewordpat,c.wordpat,c.wordpatid wordpatid,c.city city,c.wordpattype wordpattype ,c.autosendswitch autosendswitch ");
		}
		// 定义查询的条件的SQL语句
		paramSql
				.append(" from service a,kbdata b,wordpat c where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid  and a.brand=? and a.service=? and b.kbdataid =? ");
		// 绑定品牌参数
		lstpara.add(brand);
		// 绑定业务名称参数
		lstpara.add(service);
	    // 绑定摘要id参数
		lstpara.add(kbdataid);
		// 判断词模是否为空，null
		if (selectWordpat != null && !"".equals(selectWordpat)
				&&selectWordpat.length() > 0) {
					// 加上词模的查询条件
					paramSql.append(" and c.wordpat like ? ");
					lstpara.add("%"+selectWordpat+"%");	
		}
		
		if(GetConfigValue.isOracle){
			 paramSql.append(" )t1)t2 where t2.rn>? and t2.rn<=? ");
			// 绑定开始条数参数
				lstpara.add(Integer.parseInt(start));
				// 绑定截止条数参数
				lstpara.add(Integer.parseInt(start)+Integer.parseInt(limit));
		}else if(GetConfigValue.isMySQL){
			 paramSql.append(" )t1)t2  limit ?,?");
			// 绑定开始条数参数
				lstpara.add(Integer.parseInt(start));
				// 绑定截止条数参数
				lstpara.add(Integer.parseInt(limit));
		}
				// 执行SQL语句，获取相应的数据源
				Result rs = Database.executeQuery(paramSql.toString(), lstpara.toArray());
				
				//文件日志
				GlobalValue.myLog.info( paramSql + "#" + lstpara );
				
		        return rs;
	}
	
	
	/**
	 *通过ID获取词模
	 *@param wordpatid
	 *@return 
	 *@returnType Result 
	 */
	public static Result getWordpatById(String wordpatid){
		String sql = "select * from wordpat where wordpatid ="+wordpatid;
		//文件日志
		GlobalValue.myLog.info( sql );
		return Database.executeQuery(sql);
	}

	/**
	 *@description  更新词模
	 *@param user   用户信息
	 *@param service 业务
	 *@param brand 品牌
	 *@param kbdataid 摘要ID
	 *@param oldWordpatid 旧词模ID
	 *@param newWordpat 新词模
	 *@param autosendswitch 
	 *@param wordpattype 词模类型
	 *@param oldsimplewordpat 旧简单词模
	 *@param simplewordpat 简单词模
	 *@return 
	 *@returnType int 
	 */
	public static int updateByinsertAndelete(User user,String service,String brand,String kbdataid,String oldWordpatid,String newWordpat,String autosendswitch,String wordpattype,String oldsimplewordpat,String simplewordpat) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		
		String tempSql = "select * from wordpat where wordpatid=" + oldWordpatid;
		Result tempRs = Database.executeQuery(tempSql);
		//文件日志
		GlobalValue.myLog.info( tempSql );
		String oldcity = "全国";
		if (tempRs != null && tempRs.getRowCount() > 0){
			oldcity = tempRs.getRows()[0].get("city")==null ? "全国" : tempRs.getRows()[0].get("city").toString();
		}else{
			return -1;
		}
		
		//文件日志
		GlobalValue.myLog.info( user.getUserID() + "#获取词模地市修改词模地市为："+oldcity );
		
		Result rs = null;
	         //删除后添加
			// 定义SQL语句
			sql = new StringBuilder();
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 对应删除模板的SQL语句
			sql
					.append("delete from wordpat  where  wordpatid= ?");
			// 绑定旧的模板参数
			lstpara.add(oldWordpatid);
			// 将SQL语句放入集合中
			lstSql.add(sql.toString());
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

			// 定义SQL语句
			sql = new StringBuilder();
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			
			String serviceType = user.getIndustryOrganizationApplication();
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			// 获取模板的序列值
			String wordpatid ="";
			if (GetConfigValue.isOracle) {
				wordpatid =   ConstructSerialNum.GetOracleNextValNew("SEQ_WORDPATTERN_ID", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				wordpatid = ConstructSerialNum.getSerialIDNew("wordpat", "wordpatid", bussinessFlag);
			}
			// 定义新增模板的SQL语句
			sql.append("insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,city) values(?,?,?,?,?,?,?) ");
			// 绑定模板id参数
			lstpara.add(wordpatid);
			// 绑定模板参数
			lstpara.add(newWordpat);
			// 绑定自动开关参数
			lstpara.add(0);
			// 绑定模板类型参数
			lstpara.add(wordpattype);
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			lstpara.add(brand);
			lstpara.add(oldcity);
			// 将SQL语句放入集合中
			lstSql.add(sql.toString());
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

//			// 定义SQL语句
//			sql = new StringBuilder();
//			// 定义绑定参数集合
//			lstpara = new ArrayList<Object>();
//			// 定义新增模板备份表
//			sql
//					.append("insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0)");
//			// 绑定模板id参数
//			lstpara.add(wordpatid);
//			// 绑定地市参数
//			lstpara.add("上海");
//			// 绑定品牌参数
//			lstpara.add(brand);
//			// 将SQL语句放入集合中
//			lstSql.add(sql.toString());
//			// 将对应的绑定参数集合放入集合中
//			lstLstpara.add(lstpara);
		// 生成操作日志记录
		String _object  = oldsimplewordpat + "==>" + simplewordpat +";地市："+oldcity;
		// 将SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
			lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(),brand,service, "更新模板",
				_object,  "WORDPAT"));
			
			
		// 执行SQL语句，绑定事务，返回事务处理结果
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}
	
	/**
	 *@description 新增词模
	 *@param user 用户信息
	 *@param service 业务
	 *@param brand 品牌
	 *@param kbdataid 摘要ID
	 *@param wordpat  词模
	 *@param simplewordpat 简单词模
	 *@param wordpattype 词模类型
	 *@return 
	 *@returnType int 
	 */
	public static int insert(User user, String service, String brand,
			String kbdataid, String wordpat, String simplewordpat,
			String wordpattype) {

		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara;
		// 定义多条SQL语句
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		sql = new StringBuilder();
		// 定义绑定参数集合
		// 定义SQL语句
		sql = new StringBuilder();
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(user.getIndustryOrganizationApplication());
		String wordpatid = "";
		if (GetConfigValue.isOracle) {
			// 获取词模表的序列值
			wordpatid = ConstructSerialNum.GetOracleNextValNew(
					"SEQ_WORDPATTERN_ID", bussinessFlag);
			// 定义新增模板的SQL语句
			sql
					.append("insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,simplewordpat,city,workerid) values(?,?,?,?,?,?,sysdate,?,?,?)");
		} else if (GetConfigValue.isMySQL) {
			// 获取词模表的序列值
			wordpatid = ConstructSerialNum.getSerialIDNew("wordpat",
					"wordpatid", bussinessFlag);
			// 定义新增模板的SQL语句
			sql
					.append("insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,simplewordpat,city,workerid) values(?,?,?,?,?,?,sysdate(),?,?,?)");
		}

		// 绑定模板id参数
		lstpara.add(wordpatid);
		// 绑定模板参数
		lstpara.add(wordpat);
		// 绑定自动开关参数
		lstpara.add(0);
		// 绑定模板类型参数
		lstpara.add(wordpattype);
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定品牌参数
		lstpara.add(brand);
		// 绑定简单词模参数
		lstpara.add(simplewordpat);
		String city = CommonLibKbDataDAO.getCityByAbstractid(kbdataid).replace(
				",", "|");
		if ("".equals(city)) {
			city = "全国";
		}
		lstpara.add(city);
		lstpara.add(user.getUserID());
		// 将SQL语句放入集合中
		lstSql.add(sql.toString());
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

		// // 定义SQL语句
		// sql = new StringBuilder();
		// // 定义绑定参数集合
		// lstpara = new ArrayList<Object>();
		// // 定义新增模板备份表
		// sql
		// .append("insert into wordpatprecision(wordpatid,brand,correctnum,callvolume,wpprecision,autoreplyflag,projectflag) values(?,?,0,0,0,0,0)");
		// // 绑定模板id参数
		// lstpara.add(wordpatid);
		// // 绑定品牌参数
		// lstpara.add(brand);
		// // 将SQL语句放入集合中
		// lstSql.add(sql.toString());
		// // 将对应的绑定参数集合放入集合中
		// lstLstpara.add(lstpara);

		// 添加简单词模操作日志记录
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), brand, service, "增加模板",
				simplewordpat, "WORDPAT"));

		// 判断是否需要记录词模返回值
		Map<String, String> map = getKbdataid();
		if (map.containsKey(kbdataid)) {
			if (!getRetunValueLog(kbdataid)) {// 判断是否已经记录返回值，如未记录继续记录
				Map<String, String> keyValue = getReturnKeyValue(wordpat);
				for (Map.Entry<String, String> entry : keyValue.entrySet()) {
					String key = entry.getKey();
					String value = entry.getValue();
					if (value.contains("父类>") || value.contains("父子句>")) {
						String wordclass = value.split("<!")[1].split(">")[0];
						Result rs = CommonLibWordclassDAO
								.getWordclassID(wordclass);
						String wordclassid = "";
//						if(!getRetunValueLogByElementname(key)){//判断返回值key是否已经记录，如未记录继续记录
						if (rs != null && rs.getRowCount() > 0) {
							wordclassid = rs.getRows()[0].get("wordclassid")
									.toString();
							sql = new StringBuilder();
							sql.append("insert into scenariosinteractiveelement(ELEMENTNAME,WORDCLASSID,RETURNVALUE,ABSTRACTID,WORDPATID,value) values (?,?,?,?,?,?)");
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							
							lstpara.add(key);
							lstpara.add(wordclassid);
							lstpara.add(key + "=" + value);
							lstpara.add(kbdataid);
							lstpara.add(wordpatid);
							lstpara.add(value);
							// 将SQL语句放入集合中
							lstSql.add(sql.toString());
							// 将对应的绑定参数集合放入集合中
							lstLstpara.add(lstpara);
						}
//						}
					}else{
						sql = new StringBuilder();
						sql
								.append("insert into scenariosinteractiveelement(ELEMENTNAME,RETURNVALUE,ABSTRACTID,WORDPATID,value) values (?,?,?,?,?)");
						// 定义绑定参数集合
						lstpara = new ArrayList<Object>();
						lstpara.add(key);
						lstpara.add(key + "=" + value);
						lstpara.add(kbdataid);
						lstpara.add(wordpatid);
						lstpara.add(value);
						// 将SQL语句放入集合中
						lstSql.add(sql.toString());
						// 将对应的绑定参数集合放入集合中
						lstLstpara.add(lstpara);
					}
					
					
				}

			}
		}

		int r = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return r;
	}
	/**
	 * 获取需记录词模返回值对应摘要
	 *@return 
	 *@returnType Map<String,String> 
	 */
	public static Map<String,String> getKbdataid(){
		Map<String,String> map = new HashMap<String,String>();
		Result rs = CommonLibMetafieldmappingDAO.getConfigValue("系统补充词模返回值配置", "指定业务范围");
	    List<String> list = new ArrayList<String>();
		for (int i = 0; i < rs.getRowCount(); i++) {
			list.add(rs.getRows()[i].get("name").toString().split("#@#")[1]);
		}
		rs = CommonLibKbDataDAO.getKbdataidByServiceid(list);
		for (int i = 0; i < rs.getRowCount(); i++) {
			map.put(rs.getRows()[i].get("kbdataid").toString(), "");
		}
		return map;
	}
	/**
	 * 获取需过滤返回值KEY
	 *@return 
	 *@returnType Map<String,String> 
	 */
	public static Map<String,String> getFilterkey(){
		Map<String,String> map = new HashMap<String,String>();
		Result rs = CommonLibMetafieldmappingDAO.getConfigValue("系统补充词模返回值配置", "过滤返回值KEY");
	    List<String> list = new ArrayList<String>();
		for (int i = 0; i < rs.getRowCount(); i++) {
			map.put(rs.getRows()[i].get("name").toString(), "");
		}
		return map;
	}
	
	  /**
	 * 判断当前摘要下词模返回值表中是否已有记录
	 *@param kbdataid
	 *@return 
	 *@returnType Boolean 
	 */
	public static Boolean getRetunValueLog(String kbdataid){
		  String sql = "select * from  scenariosinteractiveelement where abstractid = "+kbdataid;
		   if(Database.executeQuery(sql).getRowCount()>0){
			   return true;
		   }
		   else{
			   return  false;
		   }
	}
	
	  /**
	 * 获取当前摘要下词模返回值表中是否已有记录
	 *@param kbdataid
	 *@return 
	 *@returnType Boolean 
	 */
	public static Result getRetunValue(String kbdataid){
		  String sql = "select * from  scenariosinteractiveelement where abstractid = "+kbdataid;
		   return Database.executeQuery(sql);
	}
	
	/**
	 * 获取当前摘要下关联场景交互要素
	 *@param kbdataid
	 *@return 
	 *@returnType Result 
	 */
	public static Result getInteractiveElements(String kbdataid){
		 String  sql = "select se.name, (select wordclass from wordclass where wordclassid =(se.wordclassid)) wordclass from scenarios2kbdata sk,scenarioselement se where sk.relationserviceid = se.relationserviceid and se.wordclassid is not null and se.container='词模匹配' and sk.abstractid ="+kbdataid;
	     return Database.executeQuery(sql);
	}
	
	/**
	 * 获取当前摘要场景关系
	 *@param kbdataid
	 *@return 
	 *@returnType Result 
	 */
	public static Result getScenarios2kbdata(String kbdataid){
		 String  sql = "select * from scenarios2kbdata where abstractid = "+kbdataid;
	     return Database.executeQuery(sql);
	}

	  /**
	 * 判断当前摘要下词模返回值key表中是否已有记录
	 *@param kbdataid
	 *@return 
	 *@returnType Boolean 
	 */
	public static Boolean getRetunValueLogByElementname(String elementname){
		  String sql = "select * from  scenariosinteractiveelement where elementname ='"+elementname+"'";
		   if(Database.executeQuery(sql).getRowCount()>0){
			   return true;
		   }
		   else{
			   return  false;
		   }
	}
	

	/**
	 *  获得词模返回值key value 字典
	 *@param wordpat
	 *@return 
	 *@returnType Map<String,String> 
	 */
	public static Map<String,String> getReturnKeyValue(String wordpat){
		Map<String,String> map = new HashMap<String, String>();
		String returnKeyValue[] = wordpat.split("#")[1].split("&");
		Map<String,String> filterkey = getFilterkey();
		for(int i=0 ;i<returnKeyValue.length;i++){
			String keyVaue[] =returnKeyValue[i].split("=");
			String key =keyVaue[0];
//			String value =keyVaue[1];
//			if (value.contains("父类>") || value.contains("父子句>")){
//			map.put(keyVaue[0], keyVaue[1]);	
//			}
           if(!filterkey.containsKey(key)){
        	 map.put(keyVaue[0], keyVaue[1]);
         }
			
		}
		return map;
	}
	/**
	 *@description 新增词模
	 *@param user 用户信息
	 *@param service 业务
	 *@param brand 品牌
	 *@param kbdataid 摘要ID
	 *@param wordpat  词模
	 *@param simplewordpat 简单词模
	 *@param wordpattype 词模类型
	 *@return 
	 *@returnType int 
	 */
	public static int insert(User user,String service,String brand,String kbdataid,String wordpat,String simplewordpat,String wordpattype,String city) {

		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara ;
		// 定义多条SQL语句
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		sql = new StringBuilder();
		// 定义绑定参数集合
					// 定义SQL语句
					sql = new StringBuilder();
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					//获得商家标识符
					String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());
					String  wordpatid ="";
					if (GetConfigValue.isOracle) {
						// 获取词模表的序列值
						wordpatid = ConstructSerialNum
								.GetOracleNextValNew("SEQ_WORDPATTERN_ID",bussinessFlag);
						// 定义新增模板的SQL语句
						sql
						.append("insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,simplewordpat,city,workerid) values(?,?,?,?,?,?,sysdate,?,?,?)");
					} else if (GetConfigValue.isMySQL) {
						// 获取词模表的序列值
						wordpatid = ConstructSerialNum.getSerialIDNew("wordpat",
								"wordpatid",bussinessFlag);
						// 定义新增模板的SQL语句
						sql
						.append("insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,simplewordpat,city,workerid) values(?,?,?,?,?,?,sysdate(),?,?,?)");
					}
				
					// 绑定模板id参数
					lstpara.add(wordpatid);
					// 绑定模板参数
					lstpara.add(wordpat);
					// 绑定自动开关参数
					lstpara.add(0);
					// 绑定模板类型参数
					lstpara.add(wordpattype);
					// 绑定摘要id参数
					lstpara.add(kbdataid);
					// 绑定品牌参数
					lstpara.add(brand);
					// 绑定简单词模参数
					lstpara.add(simplewordpat);
					lstpara.add(city);
					lstpara.add(user.getUserID());
					// 将SQL语句放入集合中
					lstSql.add(sql.toString());
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

//					// 定义SQL语句
//					sql = new StringBuilder();
//					// 定义绑定参数集合
//					lstpara = new ArrayList<Object>();
//					// 定义新增模板备份表
//					sql
//							.append("insert into wordpatprecision(wordpatid,brand,correctnum,callvolume,wpprecision,autoreplyflag,projectflag) values(?,?,0,0,0,0,0)");
//					// 绑定模板id参数
//					lstpara.add(wordpatid);
//					// 绑定品牌参数
//					lstpara.add(brand);
//					// 将SQL语句放入集合中
//					lstSql.add(sql.toString());
//					// 将对应的绑定参数集合放入集合中
//					lstLstpara.add(lstpara);
				
		// 添加简单词模操作日志记录
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
			lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(),brand,service, "增加模板",
				simplewordpat+";地市："+city,  "WORDPAT"));
			
			// 判断是否需要记录词模返回值
			Map<String, String> map = getKbdataid();
			if (map.containsKey(kbdataid)) {
				if (!getRetunValueLog(kbdataid)) {// 判断是否已经记录返回值，如未记录继续记录
					Map<String, String> keyValue = getReturnKeyValue(wordpat);
					for (Map.Entry<String, String> entry : keyValue.entrySet()) {
						String key = entry.getKey();
						String value = entry.getValue();
						if (value.contains("父类>") || value.contains("父子句>")) {
							String wordclass = value.split("<!")[1].split(">")[0];
							Result rs = CommonLibWordclassDAO
									.getWordclassID(wordclass);
							String wordclassid = "";
//							if(!getRetunValueLogByElementname(key)){//判断返回值key是否已经记录，如未记录继续记录
							if (rs != null && rs.getRowCount() > 0) {
								wordclassid = rs.getRows()[0].get("wordclassid").toString();
								sql = new StringBuilder();
								sql.append("insert into scenariosinteractiveelement(ELEMENTNAME,WORDCLASSID,RETURNVALUE,ABSTRACTID,WORDPATID,value) values (?,?,?,?,?,?)");
								// 定义绑定参数集合
								lstpara = new ArrayList<Object>();
								
								lstpara.add(key);
								lstpara.add(wordclassid);
								lstpara.add(key + "=" + value);
								lstpara.add(kbdataid);
								lstpara.add(wordpatid);
								lstpara.add(value);
								// 将SQL语句放入集合中
								lstSql.add(sql.toString());
								// 将对应的绑定参数集合放入集合中
								lstLstpara.add(lstpara);
							}
//							}
						}else{
							sql = new StringBuilder();
							sql
									.append("insert into scenariosinteractiveelement(ELEMENTNAME,RETURNVALUE,ABSTRACTID,WORDPATID,value) values (?,?,?,?,?)");
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							
							lstpara.add(key);
							lstpara.add(key + "=" + value);
							lstpara.add(kbdataid);
							lstpara.add(wordpatid);
							lstpara.add(value);
							// 将SQL语句放入集合中
							lstSql.add(sql.toString());
							// 将对应的绑定参数集合放入集合中
							lstLstpara.add(lstpara);
							
						}
					}

				}
			}
			
			
		
		int r = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return r;
	
		
	}

	
	
	/**
	 *@description 查询模板是否存在
	 *@param brand 品牌
	 *@param service 业务
	 *@param kbdataid 摘要ID
	 *@param wordpat 复杂词模
	 *@param serviceRoot 业务根
	 *@return 
	 *@returnType Result 
	 */
	public static Result exist(String brand,String service,String kbdataid,String wordpat,String serviceRoot) {
		// 将模板按照#拆分
		String pattern[] = wordpat.split("#");
		// 获取词模体
		String patternbefore = pattern[0];
		// 将返回值按照&拆分，获取返回值数组
		String returnvalue[] = pattern[1].split("&");
		// 定义SQL语句集合
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 判断词模是否含有~
		if (wordpat.contains("~")) {//如果是排除词模，当前摘要下相同名的词模只能存在一条
			// 定义SQL语句集合
			sql = new StringBuilder();
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			sql
					.append("select s.service,s.brand,k.topic,k.abstract,t.wordpat from service s,kbdata k,wordpat t where  s.serviceid=k.serviceid and k.kbdataid=t.kbdataid  and s.brand= ?  ");
			// 绑定品牌参数
			lstpara.add(brand);
			// 加上摘要id条件
			sql.append(" and  t.kbdataid = ?");
				// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 加上词模查询条件
			sql.append(" and t.wordpat like ? ");
			// 绑定词模参数
			lstpara.add(patternbefore  + "%");

		} else {//非排除词模当前四层结构下只能存在一条
			// 定义SQL语句集合
			sql = new StringBuilder();
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 定义查询词模的SQL语句
			sql
					.append("select s.service,s.brand,k.topic,k.abstract,t.wordpat from service s,kbdata k,wordpat t where  s.serviceid=k.serviceid and k.kbdataid=t.kbdataid   and s.brand in("+serviceRoot+") ");
			// 加上词模查询条件
			sql.append(" and t.wordpat like ? ");
			// 绑定词模参数
			lstpara.add(patternbefore+ "%");
		}
		// 循环遍历返回值数组
		for (String s : returnvalue) {
			// 判断是否含有编者
			if (s.contains("编者")) {
				// 加上词模的条件
				sql.append(" and t.wordpat like ? ");
				// 加上编者参数
				lstpara.add("%编者%");
				continue;
			}
			// 加上词模的条件
			sql.append(" and t.wordpat like ? ");
			// 加上返回值参数
			lstpara.add("%" + s + "%");
		}
			// 执行SQL语句，获取相应的数据源
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			return rs;
	}
	
	/**
	 *@description 判断当前子句下词模是否存在
	 *@param wordpat 词模
	 *@param abs 摘要
	 *@return 
	 *@returnType Boolean 
	 */
	public static Boolean isExistZijuWordpat(String wordpat,String abs) {
		String patternStr = wordpat.split("@")[0];
		String ziju =abs.split(">")[1] + "子句";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 查询词条是否重复的SQL语句
		String sql = "select wordid from word where  wordclassid=(select wordclassid from wordclass where wordclass=? ) and word=?  and stdwordid is null and rownum<2 ";
		// 绑定词类id参数
		lstpara.add(ziju);
		// 绑定词条参数
		lstpara.add(patternStr);
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		if (rs != null && rs.getRowCount() > 0) {
			// 有重复词条，返回true
			return true;
		} else {
			// 没有重复词条，返回false
			return false;
		}
	}

	/**
	 *@description  删除词模
	 *@param user   用户信息
	 *@param brand  品牌
	 *@param service 业务
	 *@param wordpatid 词模ID
	 *@param wordpat 词模
	 *@param simpleWordpat 简单词模
	 *@return 
	 *@returnType int 
	 */
	public static int delete(User user,String brand,String service,String wordpatid,String wordpat,String simpleWordpat) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 将地市id按照逗号拆分
		
		// 定义删除模板的SQL语句
		sql.append("delete from wordpat  where wordpatid=? ");
		// 绑定旧的模板参数
		lstpara.add(wordpatid);
		lstSql.add(sql.toString());
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 添加简单词模操作日志记录
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
			lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(),brand,service, "删除模板",
				simpleWordpat,  "WORDPAT"));
		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
		
	}
	
}
