package com.knowology.bll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.lang.StringUtils;

import com.knowology.GlobalValue;
import com.knowology.DbDAO.DBValueOper;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLibKbDataDAO {

	/**
	 * 政企业务下添加摘要
	 * 
	 * @param userip用户IP
	 * @param userid用户ID
	 * @param username用户名
	 * @param abs参数摘要集合
	 * @param serviceid业务id
	 * @param topic参数主题
	 * @param service参数业务名称
	 * @param brand参数品牌
	 * @return 新增是否成功
	 */
	public static boolean addAbstract(String userip, String userid,
			String username, List<String> abs, String serviceid, String topic,
			String service, String brand, String isInsertIntoKbdataid_brand,
			String serviceType) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		String kbdataid = "";
		String questionid = "";

		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 资源对应的属性值
		Map<String, String> attrMap = getAttrs();
		// 查询业务所对应的属性值
//		sql = "select ";
//		for (Entry<String, String> entry : attrMap.entrySet()) {
//			sql += entry.getValue();
//		}
//		sql = sql.substring(0, sql.lastIndexOf(","));
//		sql += " from ResourceAcessManager where resourceID like 'service_"
//				+ serviceid + "'";
//		Map<String, Object> attr_value = new HashMap<String, Object>();
//		Result rs = Database.executeQuery(sql);
//		if (rs != null && rs.getRowCount() > 0) {
//			String[] columns = rs.getColumnNames();
//			for (int i = 0; i < rs.getRowCount(); i++) {
//				Object value = rs.getRows()[i].get(columns[i]).toString();
//				attr_value.put(columns[i], value);
//			}
//		}

//		attrMap.remove("service,answer");// 移除业务和答案共有的属性
		// 循环遍历摘要集合
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		for (int i = 0; i < abs.size(); i++) {
			if (GetConfigValue.isOracle) {
				kbdataid = ConstructSerialNum.GetOracleNextValNew(
						"SEQ_KBDATA_ID", bussinessFlag);
				questionid = ConstructSerialNum.GetOracleNextValNew(
						"SIMILARQUESTION_SEQUENCE", bussinessFlag)
						+ "";
			} else if (GetConfigValue.isMySQL) {
				kbdataid = ConstructSerialNum.getSerialIDNew("kbdata",
						"kbdataid", bussinessFlag);
				questionid = ConstructSerialNum.getSerialIDNew(
						"similarquestion", "questionid", bussinessFlag);
			}

			String kbdataid_brand = null;
			if ("yes".equals(isInsertIntoKbdataid_brand)) {
				kbdataid_brand = kbdataid + "_" + brand;
			}
			// 定义新增摘要的SQL语句
			sql = "insert into kbdata(serviceid,kbdataid,topic,abstract,kbdataid_brand,city) values (?,?,?,?,?,(select city from service where serviceid=?) )";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定业务id参数
			lstpara.add(serviceid);
			// 获取摘要表的序列值，并绑定参数
			lstpara.add(kbdataid);
			// 绑定主题参数
			lstpara.add(topic);
			// 绑定摘要参数
			lstpara.add(abs.get(i));
			// 绑定地kbdataid_brand参数
			lstpara.add(kbdataid_brand);
			lstpara.add(serviceid);
			// 将SQL语句放入集合中
			lstSql.add(sql.toString());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			if (!"个性化业务".equals(brand) && topic.contains("常见问题")) {
				/** 添加标准问题 **/
				if (GetConfigValue.isOracle) {
					sql = "insert into similarquestion (kbdataid,kbdata,questionid,question,time,questiontype) values(?,?,?,?,sysdate,'标准问题')";
				} else if (GetConfigValue.isMySQL) {
					sql = "insert into similarquestion (kbdataid,kbdata,questionid,question,time,questiontype) values(?,?,?,?,sysdate(),'标准问题')";
				}

				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 获取摘要表的序列值，并绑定参数
				lstpara.add(kbdataid);
				// 绑定摘要参数
				lstpara.add(abs.get(i));
				// 绑定问题ID参数
				lstpara.add(questionid);
				// 绑定问题参数
				lstpara.add(abs.get(i).split(">")[1]);
				// 将SQL语句放入集合中
				lstSql.add(sql.toString());
				// 将定义的绑定参数集合放入集合中
				lstLstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
			}
			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstSql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(GetConfigValue.LogParam(userip, userid, username,
					brand, service, "增加摘要", abs.get(i) + "(主题:" + topic + ")",
					"KBDATA"));

			// 将业务的属性移植到摘要上
//			if (!attr_value.isEmpty()) {
//				List<Object> param = new ArrayList<Object>();
//				sql = "insert into resourceAcessManager(id,resourceid,";
//				String sql_last = "";
//				if (GetConfigValue.isOracle) {
//					sql_last = " values (resourceAcessManager_sequence.nextval,'kbdata_"
//							+ kbdataid + "',";
//				} else if (GetConfigValue.isMySQL) {
//					sql_last = " values ("
//							+ ConstructSerialNum.getSerialID(
//									"resourceAcessManager", "id") + ",'kbdata_"
//							+ kbdataid + "',";
//				}
//
//				for (Entry<String, Object> entry : attr_value.entrySet()) {
//					sql += entry.getKey() + ",";
//					sql_last += "?,";
//					param.add(entry.getValue());
//				}
//				sql = sql.substring(0, sql.lastIndexOf(",")) + ")";
//				sql_last = sql_last.substring(0, sql_last.lastIndexOf(","))
//						+ ")";
//				sql = sql + sql_last;
//				lstSql.add(sql);
//				lstLstpara.add(param);
//			}
			
		}

		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		// 判断事务处理结果
		if (c > 0) {
			// 事务处理成功,返回true，表示新增成功
			return true;
		} else {
			// 事务处理失败,返回false，表示新增失败
			return false;
		}

	}

	/**
	 * 通用主题下添加摘要
	 * 
	 * @param userip用户IP
	 * @param userid用户ID
	 * @param username用户名
	 * @param abs参数摘要集合
	 * @param serviceid业务id
	 * @param topic参数主题
	 * @param service参数业务名称
	 * @param brand参数品牌
	 * @return 新增是否成功
	 */
	public static boolean addPublicAbstract(String userip, String userid,
			String username, List<String> abs, String serviceid, String topic,
			String service, String brand, String serviceType) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		// 循环遍历摘要集合
		for (int i = 0; i < abs.size(); i++) {
			// 判断当前要录入的摘要词类表中是否存在对应的"摘要子句"
			String absWordclass = abs.get(i).split(">")[1] + "子句";
			String wordclassid = "";
			String kbdataid = "";
			if (GetConfigValue.isOracle) {
				kbdataid = ConstructSerialNum.GetOracleNextValNew(
						"SEQ_KBDATA_ID", bussinessFlag);
				wordclassid = ConstructSerialNum.GetOracleNextValNew(
						"SEQ_WORDCLASS_ID", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				kbdataid = ConstructSerialNum.getSerialIDNew("kbdata",
						"kbdataid", bussinessFlag)
						+ "";
				wordclassid = ConstructSerialNum.getSerialIDNew("wordclass",
						"wordclassid", bussinessFlag);
			}

			// 若不存在将"摘要子句"录入到词类表中
			// if (!ServiceController.Exists(absWordclass)) {
			// 定义新增词类的SQL语句
			sql = "insert into wordclass(wordclassid,wordclass,container) values(?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 获取词类表的序列值,并绑定词类id参数
			lstpara.add(wordclassid);
			// 绑定词类名称参数
			lstpara.add(absWordclass);
			// 绑定词类类型参数
			lstpara.add("子句");
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 加上对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );

			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstSql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(GetConfigValue.LogParam(userip, userid, username,
					" ", " ", "增加词类", absWordclass, "WORDCLASS"));
			// }

			// 定义新增摘要的SQL语句
			sql = "insert into kbdata(serviceid,kbdataid,topic,abstract) values (?,?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定业务id参数
			lstpara.add(serviceid);
			// 获取摘要表的序列值，并绑定参数
			lstpara.add(kbdataid);
			// 绑定主题参数
			lstpara.add(topic);
			// 绑定摘要参数
			lstpara.add(abs.get(i));
			// 将SQL语句放入集合中
			lstSql.add(sql.toString());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstSql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(GetConfigValue.LogParam(userip, userid, username,
					brand, service, "增加摘要", abs.get(i) + "(主题:" + topic + ")",
					"KBDATA"));

		}
		// 执行SQL语句，绑定事务，返回事务处理结果
		int rs = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		// 判断事务处理结果
		if (rs > 0) {
			// 事务处理成功,返回true，表示新增成功
			return true;
		} else {
			// 事务处理失败,返回false，表示新增失败
			return false;
		}
	}

	/**
	 * 得到资源对应的属性信息
	 * 
	 * @return
	 */
	public static Map<String, String> getAttrs() {
		String sql = "";
		Result rs = null;
		// 查询数据库，获得业务，摘要，答案共有的属性
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("service,kbdata", "");// 业务和摘要都有的属性
		attrMap.put("service,answer", "");// 业务和答案都有的属性
		attrMap.put("service,kbdata,answer", "");// 业务、摘要、答案共有的属性
		sql = "SELECT resourceType,columnNum,dataType FROM Resourceattrname2fieldcolnum WHERE resourceType LIKE 'service,%' OR resourceType LIKE '%,service' OR resourceType LIKE '%,service,%'";
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				// 获得属性所适用的范围
				String resourceType = rs.getRows()[i].get("resourceType")
						.toString();
				String columnNum = rs.getRows()[i].get("columnNum").toString();
				String dataType = rs.getRows()[i].get("dataType").toString();

				if (resourceType.split(",").length > 2) {// 业务、摘要、答案共有的属性
					String attr = attrMap.get("service,kbdata,answer")
							.toString();
					attr += "attr" + columnNum + "_" + dataType + ",";
					attrMap.put("service,kbdata,answer", attr);
				} else if (resourceType.split(",").length == 2) {// 业务、摘要或者业务、答案共有的属性
					if (resourceType.contains("kbdata")) {// 业务、摘要
						String attr = attrMap.get("service,kbdata").toString();
						attr += "attr" + columnNum + "_" + dataType + ",";
						attrMap.put("service,kbdata", attr);
					} else if (resourceType.contains("answer")) {// 业务、答案
						String attr = attrMap.get("service,answer").toString();
						attr += "attr" + columnNum + "_" + dataType + ",";
						attrMap.put("service,answer", attr);
					}
				}

			}
		}
		return attrMap;
	}

	/**
	 * 分页查询满足条件的摘要数据源
	 * 
	 * @param start参数开始条数
	 * @param limit参数每页条数
	 * @param key参数搜索摘要的关键字
	 * @param service参数业务名称
	 * @param brand参数品牌
	 * @param topic参数主题
	 * @param cityid参数id
	 * @param serviceid参数业务id
	 * @return 数据源
	 */
	public static Result getAbstracts(String start, String limit, String key,
			String service, String brand, String topic, String cityid,
			String serviceid, String industryOrganizationApplication) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义分页查询满足条件的SQL语句，其中根据摘要id查询词模数、答案数、问题数
		if (GetConfigValue.isOracle) {
			sql
					.append("select * from (select t.*,rownum rn from (select k.abstract,k.kbdataid,k.city,"
							// 下面的SQL语句是根据摘要id查询词模数
							+ "(select count(distinct w.wordpat) from wordpat w where w.kbdataid in(k.kbdataid) and wordpattype!=5  ) wordpatcount,"
							// 下面的SQL语句是根据摘要id查询答案数
							+ "(select count(*) from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g "
							+ " where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype like ?  and b.kbdataid in(k.kbdataid)) answercount,"
							// 下面的SQL语句是根据摘要id查询问题数
							+ " (select count(*) from similarquestion where kbdataid in (k.kbdataid)) questioncount "
							+ " from kbdata k,service s where k.serviceid=s.serviceid and s.serviceid = ? ");
			// 绑定登录时的商家组织应用
//			if("基金行业->华夏基金->对内应用".equals(industryOrganizationApplication)){//华夏特殊需求
//				lstpara.add("基金行业->华夏基金->%");
//			}else{
				lstpara.add(industryOrganizationApplication);	
//			}
			lstpara.add(serviceid);
			// 加上主题条件和地市ids条件
			sql.append(" and k.topic= ? ");
			// 绑定主题参数
			lstpara.add(topic);
			// 判断搜索关键字是否为空，null
			if (key != null && !"".equals(key) && key.length() > 0) {
				// 不为空或者null，加上like查询摘要
				sql.append(" and k.abstract like ? ");
				// 绑定参数变量
				lstpara.add("%" + key + "%");
			}
			// 加上分页的条件
			sql.append("  and k.abstract not like '%(删除标识符近类)'  order by k.kbdataid desc )t  where rownum<= ? ) t1 where t1.rn>=? ");
			// sql.append(")t) t1 where t1.rn>? and t1.rn<=?");
			// 绑定截止条数参数
			lstpara.add(Integer.parseInt(start) + Integer.parseInt(limit));
			// 绑定开始条数参数
			lstpara.add(start);
			GlobalValue.myLog.info("GHJ Integer.parseInt(start) + Integer.parseInt(limit)="+Integer.parseInt(start) + Integer.parseInt(limit)+"  and  start="+start);
//			
//			if("true".equalsIgnoreCase(ResourceBundle
//					.getBundle("commonLibGlobal").getString("isToMysql")) ? true:false){
//				// 绑定开始条数参数
//				lstpara.add(Integer.parseInt(start));
//				// 绑定截止条数参数
//				lstpara.add(Integer.parseInt(limit));		
//			}
//			else{
//				// 绑定截止条数参数
//				lstpara.add(Integer.parseInt(start) + Integer.parseInt(limit));
//				// 绑定开始条数参数
//				lstpara.add(start);
//			}

		} else if (GetConfigValue.isMySQL) {
			sql
					.append("select * from (select t.* from (select k.abstract,k.kbdataid,k.city,"
							// 下面的SQL语句是根据摘要id查询词模数
							+ "(select count(distinct w.wordpat) from wordpat w where w.kbdataid in(k.kbdataid)) wordpatcount,"
							// 下面的SQL语句是根据摘要id查询答案数
							+ "(select count(*) from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g "
							+ " where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype like ?  and b.kbdataid in(k.kbdataid)) answercount,"
							// 下面的SQL语句是根据摘要id查询问题数
							+ " (select count(*) from similarquestion where kbdataid in (k.kbdataid)) questioncount "
							+ " from kbdata k,service s where k.serviceid=s.serviceid and s.serviceid = ? ");
			// 绑定登录时的商家组织应用
			lstpara.add(industryOrganizationApplication);

//			if("基金行业->华夏基金->对内应用".equals(industryOrganizationApplication)){//华夏特殊需求
//				lstpara.add("基金行业->华夏基金->%");
//			}else{
				lstpara.add(industryOrganizationApplication);	
//			}
			lstpara.add(serviceid);
			// 加上主题条件和地市ids条件
			sql.append(" and k.topic= ? ");
			// 绑定主题参数
			lstpara.add(topic);
			// 判断搜索关键字是否为空，null
			if (key != null && !"".equals(key) && key.length() > 0) {
				// 不为空或者null，加上like查询摘要
				sql.append(" and k.abstract like ? ");
				// 绑定参数变量
				lstpara.add("%" + key + "%");
			}
			// 加上分页的条件

			int end = Integer.parseInt(limit);
			sql.append(" and k.abstract not like '%(删除标识符近类)' )t order by t.kbdataid desc ) t1  limit " + start
					+ ", " + end + " ");

		}
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		return Database.executeQuery(sql.toString(), lstpara.toArray());
	}

	/**
	 * 查询满足条件的摘要总数
	 * 
	 * @param key参数搜索摘要的关键字
	 * @param service参数业务名称
	 * @param brand参数品牌
	 * @param topic参数主题
	 * @param cityid参数地市id
	 * @param serviceid参数业务id
	 * @return 数量
	 */
	public static int getAbstractCount(String key, String service,
			String brand, String topic, String cityid, String serviceid) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义查询满足条件的摘要数量的SQL语句
		sql
				.append("select * from kbdata a,service b where a.serviceid=b.serviceid and a.abstract not like '%(删除标识符近类)' and b.serviceid  =? ");
		// 绑定业务id参数变量
		lstpara.add(serviceid);
		// 加上主题条件和地市ids条件
		sql.append(" and a.topic=?  ");
		// 绑定主题参数
		lstpara.add(topic);
		// 判断搜索关键字是否为空，null
		if (key != null && !"".equals(key) && key.length() > 0) {
			// 不为空或者null，加上like查询摘要
			sql.append(" and a.abstract like ? ");
			// 绑定参数变量
			lstpara.add("%" + key + "%");
		}

		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return rs.getRowCount();
		} else {
			return 0;
		}
	}

	/**
	 * 判断摘要是否存在
	 * 
	 * @param service参数业务名称
	 * @param brand参数品牌
	 * @param cityid参数地市id
	 * @param topic参数主题
	 * @param _abstract参数摘要
	 * @param t参数中间对象主要存放摘要集合
	 * @param serviceid参数业务id
	 * @return 是否存在
	 */
	public static List<String> isHave(String service, String brand,
			String topic, String _abstract, String serviceid) {
		// 获取摘要集合
		List<String> abs = new ArrayList<String>();
		// 将摘要按照回车符拆分
		String[] _abstracts = _abstract.split("\n");

		// 循环遍历摘要数组
		for (int i = 0; i < _abstracts.length; i++) {
			// 定义查询SQL语句
			StringBuilder sql = new StringBuilder();
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 定义查询摘要是否存在的SQL语句
			sql
					.append("select distinct k.abstract from service s,kbdata k where s.serviceid=k.serviceid and s.serviceid =?  and k.abstract=? ");
			// 绑定业务id参数
			lstpara.add(serviceid);
			// 绑定主题参数
			// lstpara.add(topic);
			// 绑定摘要参数
			lstpara.add(_abstracts[i]);

			// 执行SQL语句，获取相应的数据源
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null，且含有数据
			if (rs == null || rs.getRowCount() == 0) {
				// 这条摘要不存在，放入摘要集合中
				abs.add(_abstracts[i]);
			}
		}
		// 判断摘要集合中的个数是否大于0，大于0表示不存在，否则存在
		// return abs.size() > 0 ? false : true;
		return abs;
	}

	/**
	 * 更新摘要
	 * 
	 * @param userip用户IP
	 * @param userid用户ID
	 * @param username用户名
	 * @param newabstract参数新摘要
	 * @param oldabstract参数旧摘要
	 * @param serviceid业务id
	 * @param topic参数主题
	 * @param service参数业务名称
	 * @param brand参数品牌
	 * @param container参数类型
	 * @param kbdataid参数摘要id
	 * @return 更新是否成功
	 */
	public static boolean updateAbstract(String userip, String userid,
			String username, String newabstract, String oldabstract,
			String serviceid, String topic, String service, String brand,
			String container, String kbdataid) {
		// 获取新摘要<>后面的内容
		String newabspart = newabstract.substring(newabstract.indexOf(">") + 1);
		// 获取旧摘要<>后面的内容
		String oldabspart = oldabstract.substring(oldabstract.indexOf(">") + 1);
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();

		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 定义更新摘要的SQL语句
		if (GetConfigValue.isOracle) {
			// sql =
			// "update kbdata t set t.abstract=substr(t.abstract,0,instr(t.abstract,'>'))||? "
			// +
			// " where t.topic=? and t.abstract like '%'||? and (t.serviceid=? or t.serviceid in("
			// + " select k.serviceid from service k where k.parentid=?))  ";
			sql = "update kbdata t set t.abstract=substr(t.abstract,0,instr(t.abstract,'>'))||? "
					+ " where t.kbdataid =? ";
		} else if (GetConfigValue.isMySQL) {
			sql = "update kbdata t set t.abstract=CONCAT(substr(t.abstract,1,instr(t.abstract,'>')),?) "
					+ " where  t.kbdataid =? ";
		}
		// 绑定新摘要参数
		lstpara.add(newabspart);
		lstpara.add(kbdataid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstLstpara.add(GetConfigValue.LogParam(userip, userid, username, brand,
				service, "更新摘要", oldabstract + "==>" + newabstract, "KBDATA"));

		// 获取业务近类词类
		String servicejinlei = newabspart + "子句";
		// 获取旧业务近类词类
		String oldservicejinlei = oldabspart + "子句";
		// 判断类型是否等于通用
		if ("通用".equals(container)) {
			// 修改摘要子句及摘要子句词模
			// 更新词类表中对应子句
			sql = "update wordclass set wordclass=? where wordclass=? and container=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定新近类词类
			lstpara.add(servicejinlei);
			// 绑定旧近类词类
			lstpara.add(oldservicejinlei);
			// 绑定词库类型
			lstpara.add("子句");
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );

			// 生成操作日志记录

			// 将SQL语句放入集合中
			lstSql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(GetConfigValue.LogParam(userip, userid, username,
					" ", " ", "增加词类", oldservicejinlei + "==>" + servicejinlei,
					"WORDCLASS"));

			// 更新当前摘要子句词模
			sql = "update wordpat set wordpat=replace(wordpat,?,?),simplewordpat=replace(simplewordpat,?,?) where wordpat like ?";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定旧近类词类
			lstpara.add("!" + oldservicejinlei + "|");
			// 绑定新近类词类
			lstpara.add("!" + servicejinlei + "|");
			// 绑定旧近类词类
			lstpara.add(oldservicejinlei);
			// 绑定新近类词类
			lstpara.add(servicejinlei);
			// 绑定旧近类词类
			lstpara.add("%!" + oldservicejinlei + "|%");
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );

			sql = "update wordpat set wordpat=replace(wordpat,?,?) ,simplewordpat=replace(simplewordpat,?,?) where wordpat like ?";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定旧近类词类
			lstpara.add("!" + oldservicejinlei + ">");
			// 绑定新近类词类
			lstpara.add("!" + servicejinlei + ">");
			// 绑定旧近类词类
			lstpara.add(oldservicejinlei);
			// 绑定新近类词类
			lstpara.add(servicejinlei);
			// 绑定旧近类词类
			lstpara.add("%!" + oldservicejinlei + ">%");
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}
		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		// 判断事务处理结果
		if (c > 0) {
			// 事务处理成功,返回true，表示更新成功
			return true;
		} else {
			// 事务处理失败,返回false，表示更新失败
			return false;
		}
	}

	/**
	 * 删除摘要
	 * 
	 * @param userip用户IP
	 * @param userid用户ID
	 * @param username用户名
	 * @param abs参数摘要名称
	 * @param servceid
	 *            业务id
	 * @param topic参数主题
	 * @param service参数业务名称
	 * @param brand参数品牌
	 * @param kbdataids参数摘要ids
	 * @param container参数类型
	 * @return 删除是否成功
	 */
	public static boolean deleteAbstract(String userip, String userid,
			String username, String abs, String serviceid, String topic,
			String service, String brand, String kbdataid, String container) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();

		// 定义删除摘要的SQL语句
		sql = "delete from kbdata where  kbdataid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();

		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstLstpara.add(GetConfigValue.LogParam(userip, userid, username, brand,
				service, "删除摘要", abs + "(主题:" + topic + ")", "KBDATA"));

		// 判断类型是否等于通用
		if ("通用".equals(container)) {
			// 通用主题下删除摘要，级联删除子句词库中的对应摘要子句
			// 获取由摘要形成的子句词类
			String zijuString = abs.split(">")[1] + "子句";
			// 定义删除处理的SQL语句
			sql = "delete from wordclass where wordclass=? and container=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定词类名称参数
			lstpara.add(zijuString);
			// 绑定子句词库类型
			lstpara.add("子句");
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}

		// 删除摘要对应的属性
		sql = "delete from ResourceAcessManager where resourceid=?";
		// 定义绑定参数集合
		List<String> param = new ArrayList<String>();
		param.add("kbdata_" + kbdataid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(param);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + param );

		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		// 判断事务处理结果
		if (c > 0) {
			// 事务处理成功,返回true，表示删除成功
			return true;
		} else {
			// 事务处理失败,返回false，表示删除失败
			return false;
		}
	}

	/**
	 *@description 通过摘要名查询摘要信息
	 *@param abstractName摘要名
	 *@return
	 *@returnType Result
	 */
	public static Result getKbdataInfoByAbstractName(String abstractName) {
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义查询满足条件的总条数的SQL语句
		sql = "select * from kbdata where abstract=? ";
		// 绑定类型参数
		lstpara.add(abstractName);
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}

	/**
	 * 
	 *描述：通过摘要ID查询摘要信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-3-3 时间：下午04:05:16
	 *@param abstractID
	 *@return Result
	 */
	public static Result getServiceKbdataInfoByAbstractID(String abstractID) {
		try {
			// 定义SQL语句
			String sql = "";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 定义查询满足条件的总条数的SQL语句
			sql = "select kbdata.topic,kbdata.serviceid,service.brand,kbdata.city from kbdata,service where service.serviceid=kbdata.serviceid and kbdataid=? ";
			// 绑定类型参数
			lstpara.add(abstractID);

			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			return rs;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return null;
		}
	}
	
	/**
	 * 
	 *描述：通过摘要ID查询摘要信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-3-3 时间：下午04:05:16
	 *@param abstractID
	 *@return Result
	 */
	public static Result getKbdataInfoByAbstractID(String abstractID) {
		try {
			// 定义SQL语句
			String sql = "";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 定义查询满足条件的总条数的SQL语句
			sql = "select * from kbdata where kbdataid=? ";
			// 绑定类型参数
			lstpara.add(abstractID);

			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			return rs;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static String getKbdataByID(String abstractID)
	{
		Result rs= getKbdataInfoByAbstractID(abstractID);
		if(rs!=null && rs.getRowCount()>0)
		{
			if(rs.getRows()[0].containsKey("abstract"))
			{
			return DBValueOper.GetValidateStringObj4Null(rs.getRows()[0].get("abstract"));
			}
		}
		return "";
	}

	/**
	 * 
	 *描述：通过摘要ID或者摘要名获取kbdata表中的客户摘要ID
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-6-25 时间：下午07:13:58
	 *@return String
	 */
	public static String getCustomer_kbdataidFromDB(String kbdataID,
			String abstractStr) {
		String sql = "SELECT customer_kbdataid FROM kbdata ";
		String id = "";
		if (kbdataID != null && kbdataID.length() != 0) {
			sql += " where kbdataid=" + kbdataID;
		} else if (abstractStr != null && abstractStr.length() != 0) {
			sql += " where abstract='" + abstractStr + "'";
		} else {
			return id;
		}
		Result rs;
		try {
			rs = Database.executeQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
			
			if (rs != null && rs.getRowCount() > 0) {
				for (SortedMap<String, String> row : rs.getRows()) {
					id = DBValueOper.GetValidateStringObj4Null(row
							.get("customer_kbdataid"));
				}
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
		}
		return id;
	}

	/**
	 * 
	 *描述：根据客户ID获取摘要ID
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-6-28 时间：下午04:40:44
	 *@param customerID
	 *@return String  id#_#摘要
	 */
	public static String getKBDataByCustomerID(String customerID) {
		String sql = "SELECT kbdataid,abstract FROM kbdata ";
		String out = "";
		if (customerID != null && customerID.length() != 0) {
			sql += " where customer_kbdataid='" + customerID + "'";
		} else {
			return out;
		}
		Result rs;
		try {
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			if (rs != null && rs.getRowCount() > 0) {
				// 只取第一个
				String absid = DBValueOper.GetValidateStringObj4Null(rs
						.getRowsByIndex()[0][0]);
				String abs = DBValueOper.GetValidateStringObj4Null(rs
						.getRowsByIndex()[0][1]);
				if (absid.length() > 0 && abs.length() > 0) {
					out = absid + "#_#" + abs;
				}
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
		}
		return out;
	}

	/**
	 * 
	 *描述：获取摘要的城市信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-1-7 时间：下午02:14:10
	 *@param kbdataID
	 *@return String
	 */
	public static String getCity(String kbdataID) {
		String sql = "SELECT city FROM kbdata ";
		String city = "";
		if (kbdataID != null && kbdataID.length() != 0) {
			sql += " where kbdataid=" + kbdataID;
		} else {
			return city;
		}
		Result rs;
		try {
			//文件日志
			GlobalValue.myLog.info( sql );
			
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				for (SortedMap<String, String> row : rs.getRows()) {
					city = DBValueOper.GetValidateStringObj4Null(row
							.get("city"));
				}
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
		}
		return city;
	}

	/**
	 *@description 通过摘要ID获取摘要
	 *@param kbdataid
	 *@return
	 *@returnType String
	 */
	@SuppressWarnings("unchecked")
	public static String getCityByAbstractid(String kbdataid) {
		String city = "";
		String sql = "select city from kbdata where  kbdataid=?";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要ID
		lstpara.add(kbdataid);
		try {
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断简要不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				SortedMap[] map = rs.getRows();
				city = map[0].get("city") == null ? "" : map[0].get("city")
						.toString();
			}
			return city;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return city;
	}

	
	
	
	/**
	 * 通过业务ID产寻摘要
	 * @param serviceid 业务id
	 * @return
	 */
	public static Result getAbstractByServiceid(String serviceid) {
		String sql = "select kbdataid,abstract from kbdata where serviceid=" + serviceid ;
		//文件日志
		GlobalValue.myLog.info( sql );
		
		Result rs = Database.executeQuery(sql);
		return rs;
	}
	
	/**
	 *@description  查询摘要city
	 *@param kbdataids 词类名称
	 *@return 
	 *@returnType Result 
	 */
	public static Result selectKbdataCity(String kbdataids) {
		// 定义查询词条的SQL语句
		String sql = "select city from kbdata where kbdataid =? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要ID
		lstpara.add(kbdataids);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}

	
	/**
	 *@description  更新摘要city
	 *@param abstractid 摘要ID
	 *@param cityCode 城市代码
	 *@return 
	 *@returnType int 
	 */
	public static int updateKbdataCity(String abstractid ,String cityCode) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara;
		
		//修改摘要地市信息
		String sql = "update kbdata w set w.city=? where w.kbdataid =? ";
		lstsql.add(sql);
		lstpara = new ArrayList<Object>();
		lstpara.add(cityCode);
		lstpara.add(abstractid);
		lstlstpara.add(lstpara);
		
		//修改摘要下词模地市信息
		lstpara = new ArrayList<Object>();
		sql = "update wordpat w set w.city=?  where  w.kbdataid =? and wordpat not like '%&人工地市%'";
		lstsql.add(sql);
		if (cityCode == null || "".equals(cityCode)){
			cityCode = "全国";
		}
		if(!"".equals(cityCode)&&cityCode!=null){
			cityCode = cityCode.replace(",", "|");	
		}
		lstpara.add(cityCode);
		lstpara.add(abstractid);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		int c =-1;
		c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
	}
	
	/**
	 *@description 根据业务ID获取摘要信息
	 *@param serviceid 业务ID
	 *@return 
	 *@returnType Result 
	 */
	public static Result getAbstractInfoByServiceid(String serviceid) {
			// 定义SQL语句
			String sql = "";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 定义查询满足条件的总条数的SQL语句
			sql = "select * from kbdata where serviceid=? ";
			// 绑定类型参数
			lstpara.add(serviceid);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			return rs;
	}
	
	/**
	 *  通过业务ID名称获取摘要ID
	 *@param serviceid
	 *@return 
	 *@returnType Result 
	 */
	public static Result getKbdataidByServiceid(List<String> serviceid){
		String serviceids = StringUtils.join(serviceid.toArray(),",");
		// 定义绑定参数集合
		String sql ="select k.kbdataid from (SELECT *  FROM  service start  WITH serviceid in ("+serviceids+")　connect BY nocycle prior serviceid = parentid) s, kbdata k where s.serviceid = k.serviceid";
		// 绑定类型参数
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql);
		return rs;
	}
	
	/**
	 *  通过业务ID名称获取摘要ID
	 *@param serviceid
	 *@return 
	 *@returnType Result 
	 */
	public static Map<String,Result> getKbdataidByServiceidAndCity(List<String> serviceid,String city,int start,int limit){
		// 返回值
		Map<String, Result> resultMap = new HashMap<String, Result>();
				
		String serviceids = StringUtils.join(serviceid,",");
		// 定义绑定参数集合
		String innerSql ="from (SELECT *  FROM  service start  WITH serviceid in ("+serviceids+")　connect BY nocycle prior serviceid = parentid) s, kbdata k where s.serviceid = k.serviceid";
		if(city !=null && !"".equals(city)){
			innerSql += " and (k.city is null or k.city ='全国' or k.city like '%"+city+"')";
		}else{
			innerSql += " and (k.city is null or k.city ='全国')";
		}
		
		try {
			// 统计条数的sql
			String countSql = "select count(*) as count "+innerSql;
			Result rs = Database.executeQuery(countSql);
			resultMap.put("count", rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 分页的sql
		String sql = "";
		if(GetConfigValue.isOracle) {
			sql = "select * from (select a.*,rownum rn from" + " ( select k.kbdataid , k.abstract "
			  + innerSql + " order by k.kbdataid) a where rownum<"
			  + (limit + start + 1) + ")" + " where rn>" + start;
		} else if(GetConfigValue.isMySQL) {
			sql = innerSql + " order by k.kbdataid limit " + start + "," + limit;
		}
		
		try {
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql);
			resultMap.put("data", rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resultMap;
	}
	
}
