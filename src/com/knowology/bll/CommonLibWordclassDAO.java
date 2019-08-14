package com.knowology.bll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.sql.Result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.Bean.Role;
import com.knowology.Bean.RoleResourceAccessRule;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;


public class CommonLibWordclassDAO {
	/**
	 * 得到该用户针对operateType类型能操作的资源id集合
	 * 
	 * @param user
	 *            用户
	 * @param container
	 *            词库类型
	 * @param operateType
	 *            操作类型
	 * @param wordclasstype
	 *            行业归属
	 * @return
	 */
	public static Map<String, Map<String, String>> getResourceID(User user,
			String container, String operateType, String wordclasstype) {
		// 得到用户对应的角色信息
		List<Role> roleList = user.getRoleList();
		String resourceType = "";
		if ("基础".equals(container)) {// 基础词库
			resourceType = "baseWord";
		} else if ("子句".equals(container)) {// 子句词库
			resourceType = "sentence";
		}
		// 获得该操作的所有资源规则
		List<RoleResourceAccessRule> ruleList = RoleManager
				.getRolesRuleByOperate(roleList, resourceType, operateType);
		// 该操作类型用户能够操作的资源
		Map<String, Map<String, String>> resourceIDs = new HashMap<String, Map<String, String>>();
		if (!ruleList.isEmpty()) {// 该权限有对应的操作规则
			for (RoleResourceAccessRule rule : ruleList) {
				// 根据属性得到能够操作的所有资源id
				Map<String, Map<String, String>> serviceIDByAttr = ResourceAccessOper
						.searchResIDByAttrs(rule.getAccessResourceMap(),
								resourceType, user.getCustomer(), wordclasstype);
				if (!serviceIDByAttr.isEmpty()) {// 根据属性查询出相关资源
					resourceIDs.putAll(serviceIDByAttr);
				}
				// 压入用户指定的资源ID
				List<String> resourceNames = rule.getResourceNames();
				if (!resourceNames.isEmpty()) {// 用户指定资源名
					List<String> serviceIDByServiceName = ResourceAccessOper
							.getResourceIDByName(resourceNames.toArray(),
									resourceType);
					// 判断是否关联子业务
					if (rule.getIsRelateChild().equals("Y")
							&& resourceType.equals("service")) {// 关联子业务
						serviceIDByServiceName = ResourceAccessOper
								.getChildService(serviceIDByServiceName
										.toArray());
					}
					if (!serviceIDByServiceName.isEmpty()) {
						resourceIDs.putAll(serviceIDByAttr);
					}
				}
			}
		}
		return resourceIDs;
	}

	/**
	 * 根据条件分页查询词类
	 * 
	 * @param wordclass参数词类
	 * @param wordclassprecise
	 *            是否精确查找
	 * @param wordclasstype
	 *            行业归属
	 * @param container
	 *            词类所属
	 * @return int 数据量
	 */
	public static int getCountupdateWithResource(User user, String wordclass,
			Boolean wordclassprecise, String wordclasstype, String container) {
		// 返回值
		int count = 0;
		Map<String, Map<String, String>> resourceIDs = getResourceID(user,
				container, "S", wordclasstype);
		if (resourceIDs.isEmpty()) {
			return count;
		}
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义查询满足条件的总条数的SQL语句
		sql = "select wordclassid from wordclass where container=? ";
		// 绑定类型参数
		lstpara.add(container);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断词类条件是否为空，null
		if (!"".equals(wordclass) && wordclass != null
				&& wordclass.length() > 0) {
			// 加上词类条件
			paramSql.append(" and wordclass like ? ");
			// 绑定词类参数
			if (wordclassprecise) {
				lstpara.add(wordclass);
			} else {
				lstpara.add("%" + wordclass + "%");
			}

		}
		sql = sql + paramSql.toString();
		long s = System.currentTimeMillis();
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				String wordclassid = rs.getRows()[i].get("wordclassid")
						.toString();
				if (resourceIDs.containsKey(wordclassid)) {
					count++;
				}
			}
		}
		long d = System.currentTimeMillis();
		System.out.println("查找wordclasscount所需时间：" + (d - s) / 1000);
		return count;
	}
	
	/**
	 * 根据条件分页查询词类
	 * 
	 * @param wordclass参数词类
	 * @param wordclassprecise
	 *            是否精确查找
	 * @param wordclasstype
	 *            行业归属
	 * @param container
	 *            词类所属
	 * @return int 数据量
	 */
	public static int getCount(User user, String wordclass,
			Boolean wordclassprecise, String wordclasstype, String container) {
		// 返回值
		int count = 0;
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义查询满足条件的总条数的SQL语句
		sql = "select count(*) count from wordclass where container=? ";
		// 绑定类型参数
		lstpara.add(container);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断词类条件是否为空，null
		if (!"".equals(wordclass) && wordclass != null
				&& wordclass.length() > 0) {
			// 加上词类条件
			paramSql.append(" and wordclass like ? ");
			// 绑定词类参数
			if (wordclassprecise) {
				lstpara.add(wordclass);
			} else {
				lstpara.add("%" + wordclass + "%");
			}

		}
		sql = sql + paramSql.toString();
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			count = Integer.parseInt(rs.getRows()[0].get("count").toString());
		}
		return count; 
	}

	/**
	 * 根据条件分页查询词类
	 * 
	 * @param wordclass参数词类
	 * @param wordclassprecise
	 *            是否精确查找
	 * @param wordclasstype
	 *            行业归属
	 * @param container
	 *            词类所属
	 * @param start
	 *            开始的下表
	 * @param limit
	 *            参数每页条数
	 * @return Result 数据源
	 */
	public static JSONArray select(User user, String wordclass,
			Boolean wordclassprecise, String wordclasstype, String container,
			int start, int limit) {
		JSONArray jsonArr = new JSONArray();
		// 返回值
		Result rs = null;
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定类型参数
		lstpara.add(container);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断词类条件是否为空，null
		if (!"".equals(wordclass) && wordclass != null
				&& wordclass.length() > 0) {
			// 加上词类条件
			paramSql.append(" and wordclass like ? ");
			if(wordclassprecise){
				// 绑定词类参数
				lstpara.add( wordclass );
			}else{
				// 绑定词类参数
				lstpara.add("%" + wordclass + "%");
			}
			
		}
			// 判断数据源不为null且含有数据
				// 带分页的查询满足条件的SQL语句
		if(GetConfigValue.isOracle){
			sql = "select t2.* from(select t1.*,rownum rn from (select * from wordclass where container=? "
				+ paramSql
				+ " order by wordclassid desc)t1)t2 where t2.rn>? and t2.rn<=?";
//			if("true".equalsIgnoreCase(ResourceBundle
//					.getBundle("commonLibGlobal").getString("isToMysql")) ? true:false){
//				// 绑定开始条数参数
//				lstpara.add(start);
//				// 绑定截止条数参数
//				lstpara.add(limit);
//			}
//			else{
//				// 绑定开始条数参数
//				lstpara.add(start);
//				// 绑定截止条数参数
//				lstpara.add(start + limit);
//			}
		// 绑定开始条数参数
		lstpara.add(start);
		// 绑定截止条数参数
		lstpara.add(start + limit);
		GlobalValue.myLog.info("GHJ start="+start+"  and  start + limit="+start + limit);

		}else if(GetConfigValue.isMySQL){
			sql = "select t2.* from(select t1.* from (select * from wordclass where container=? "
				+ paramSql
				+ " order by wordclassid desc)t1)t2 limit ?,?";
		// 绑定开始条数参数
		lstpara.add(start);
		// 绑定截止条数参数
		lstpara.add(limit);
		// 执行SQL语句，获取相应的数据源
		}
		rs = Database.executeQuery(sql, lstpara.toArray());	
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				// 定义json对象
				JSONObject obj = new JSONObject();
				// 生成id对象
				obj.put("id", start + i + 1);
				// 生成wordclass对象
				obj.put("wordclass", rs.getRows()[i].get("wordclass"));
				// 生成wordclassid对象
				String wordcalssid = rs.getRows()[i].get("wordclassid")
						.toString();
				obj.put("wordclassid", wordcalssid);
				// 生成wordclasstype对象
				Map<String, String> map;
				obj.put("wordclasstype", "");
				// 将生成的对象放入jsonArr数组中
				jsonArr.add(obj);
			}
		}
		return jsonArr;

	}

	
	/**
	 * 根据条件分页查询词类
	 * 
	 * @param wordclass参数词类
	 * @param wordclassprecise
	 *            是否精确查找
	 * @param wordclasstype
	 *            行业归属
	 * @param container
	 *            词类所属
	 * @param start
	 *            开始的下表
	 * @param limit
	 *            参数每页条数
	 * @return Result 数据源
	 */
	public static JSONArray selectupdateWithResource(User user, String wordclass,
			Boolean wordclassprecise, String wordclasstype, String container,
			int start, int limit) {
		JSONArray jsonArr = new JSONArray();
		// 返回值
		Result rs = null;
		long s = System.currentTimeMillis();
		// 能够查看到的资源id
		Map<String, Map<String, String>> resourceIDs = getResourceID(user,
				container, "S", wordclasstype);
		long d = System.currentTimeMillis();
		System.out.println("查找资源ID所需时间：" + (d - s) / 1000);
		if (resourceIDs.isEmpty()) {
			return jsonArr;
		}
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		sql = "select * from wordclass where container=?";
		// 绑定类型参数
		lstpara.add(container);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 定义存放selectWordclassid 集合
		List<String> selectWordclassid = new ArrayList<String>();
		// 判断词类条件是否为空，null
		if (!"".equals(wordclass) && wordclass != null
				&& wordclass.length() > 0) {
			// 加上词类条件
			paramSql.append(" and wordclass like ? ");
			// 绑定词类参数
			if (wordclassprecise) {
				lstpara.add(wordclass);
			} else {
				lstpara.add("%" + wordclass + "%");
			}
			rs = Database.executeQuery(sql + paramSql.toString(), lstpara
					.toArray());
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + paramSql.toString() + "#" + lstpara );

			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 循环遍历数据源
				for (int i = 0; i < rs.getRowCount(); i++) {
					String wordcalssid = rs.getRows()[i].get("wordclassid")
							.toString();
					selectWordclassid.add(wordcalssid);
				}
			}
		}
		// 获得所有资源id的集合
		ArrayList<String> resourceIDList = new ArrayList<String>(resourceIDs
				.keySet());
		// 取资源id和selectWordclassid交集
		if (selectWordclassid.size() > 0) {
			resourceIDList.retainAll(selectWordclassid);
		}
		// 对resourceIDList中的内容进行从大到小排序
		List<String> sortedList = sortListDesc(resourceIDList);
		sql += paramSql.toString() + " and (";
		// 开始条数的下标
		int begin = start;
		// 结束条数的下标
		int last = (start + limit);
		String wordclsssid;
		for (int i = begin; i < last; i++) {
			if (i > (sortedList.size() - 1)) {
				break;
			} else {
				wordclsssid = sortedList.get(i);
			}
			sql += " wordclassid=" + wordclsssid + " or ";
		}
		sql = sql.substring(0, sql.lastIndexOf("or"))
				+ ") order by wordclassid desc";
		s = System.currentTimeMillis();
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				// 定义json对象
				JSONObject obj = new JSONObject();
				// 生成id对象
				obj.put("id", start + i + 1);
				// 生成wordclass对象
				obj.put("wordclass", rs.getRows()[i].get("wordclass"));
				// 生成wordclassid对象
				String wordcalssid = rs.getRows()[i].get("wordclassid")
						.toString();
				obj.put("wordclassid", wordcalssid);
				// 生成wordclasstype对象
				Map<String, String> map;
				String wtype;
				if ("全部".equals(wordclasstype)) {
					map = resourceIDs.get(wordcalssid);
					wtype = map.get("行业归属").toString();
					if (wtype.indexOf("通用行业") != -1) {
						wtype = "通用行业";
					} else if (wtype.indexOf("通用商家") != -1) {
						wtype = "当前行业";
					} else {
						wtype = "当前商家";
					}
				} else {
					wtype = wordclasstype;
				}
				obj.put("wordclasstype", wtype);
				// 将生成的对象放入jsonArr数组中
				jsonArr.add(obj);
			}
		}
		d = System.currentTimeMillis();
		System.out.println("查找wordclassInfo所需时间：" + (d - s) / 1000);
		return jsonArr;

	}
	
	/**
	 * 词类添加具体方法
	 * @param user 用户登录信息
	 * @param lstWordclass词类的集合
	 * @return 添加返回的结果
	 */
	public static int insert(User user, List<String> lstWordclass,
			String wordcalsstype, String container,String serviceType) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		String wordcalssid = "";
		String kbdataid = "";
		String service = null;
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		//获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		// 循环遍历词类集合
		for (int i = 0; i < lstWordclass.size(); i++) {
			if (GetConfigValue.isOracle) {
				wordcalssid = ConstructSerialNum
						.GetOracleNextValNew("seq_wordclass_id",bussinessFlag);
				kbdataid = ConstructSerialNum.GetOracleNextValNew("SEQ_KBDATA_ID",bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				wordcalssid = ConstructSerialNum.getSerialIDNew("wordclass",
						"wordclassid",bussinessFlag);
				kbdataid = ConstructSerialNum.getSerialIDNew("kbdata", "kbdataid",bussinessFlag)+"";
			}
			// 插入词类的SQL语句
			sql = "insert into wordclass(wordclassid,wordclass,container) values(?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定id参数
			lstpara.add(wordcalssid);
			// 绑定词类参数
			lstpara.add(lstWordclass.get(i));
			// 绑定类型参数
			lstpara.add(container);
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

			// 若为子句，同时同步行业主题树或通行业主题树
//			if ("子句".equals(container)) {
//				Result rs = CommonLibServiceDAO.getServiceIDByServiceAndBrand(
//						service, service);
//				String serviceid = rs.getRows()[0].get("serviceid").toString();
//				// 定义新增摘要的SQL语句
//				sql = "insert into kbdata(serviceid,kbdataid,topic,abstract) values (?,?,?,?)";
//				// 定义绑定参数集合ss
//				lstpara = new ArrayList<Object>();
//				// 绑定业务id参数
//				lstpara.add(serviceid);
//				// 获取摘要表的序列值，并绑定参数
//				lstpara.add(kbdataid);
//				// 绑定主题参数
//				lstpara.add("常见问题");
//				// 绑定摘要参数
//				lstpara.add("<" + service + ">"
//						+ lstWordclass.get(i).replace("子句", ""));
//				// 将SQL语句放入集合中
//				lstsql.add(sql.toString());
//				// 将定义的绑定参数集合放入集合中
//				lstlstpara.add(lstpara);
//			}

			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), " ", " ", "增加词类",
					lstWordclass.get(i), "WORDCLASS"));

		}
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	
	
	
	/**
	 * 词类添加具体方法
	 * @param user 用户登录信息
	 * @param lstWordclass词类的集合
	 * @return 添加返回的结果
	 */
	public static int insertWithSource(User user, List<String> lstWordclass,
			String wordcalsstype, String container) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		String wordcalssid = "";
		String kbdataid = "";
		String resourceId ="";
		String servicetype = null;
		String industry = null;
		String service = null;
		
		String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 循环遍历词类集合
		for (int i = 0; i < lstWordclass.size(); i++) {
			if (GetConfigValue.isOracle) {
				wordcalssid =  (ConstructSerialNum.GetOracleNextValNew("seq_wordclass_id", bussinessFlag));
				kbdataid =  (ConstructSerialNum.GetOracleNextValNew("SEQ_KBDATA_ID", bussinessFlag));
				resourceId =  (ConstructSerialNum.GetOracleNextValNew("RESOURCEACESSMANAGER_SEQUENCE", bussinessFlag));
			} else if (GetConfigValue.isMySQL) {
				wordcalssid = ConstructSerialNum.getSerialIDNew("wordclass", "wordclassid", bussinessFlag);
				kbdataid = ConstructSerialNum.getSerialIDNew("kbdata", "kbdataid", bussinessFlag);
				resourceId = ConstructSerialNum.getSerialIDNew("resourceAcessManager", "id", bussinessFlag);
			}
			// 插入词类的SQL语句
			sql = "insert into wordclass(wordclassid,wordclass,container) values(?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定id参数
			lstpara.add(wordcalssid);
			// 绑定词类参数
			lstpara.add(lstWordclass.get(i));
			// 绑定类型参数
			lstpara.add(container);
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);

			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			
			// 若为子句，同时同步行业主题树或通行业主题树
			 servicetype = user.getIndustryOrganizationApplication();
			if ("当前行业".equals(wordcalsstype)) {// 同步当前行业主题树 基金行业->通用商家->通用应用
				industry = servicetype.split("->")[0];
				servicetype = industry+ "->通用商家->通用应用";
				service = industry + "主题";
			} else if ("通用行业".equals(wordcalsstype)) {// 同步通用行业主题树
				servicetype ="通用行业->通用商家->通用应用";
				service = "通用行业主题";
			}
			if ("子句".equals(container)&&wordcalsstype.contains("行业")) {
				Result rs = CommonLibServiceDAO.getServiceIDByServiceAndBrand(
						service, service);
				String serviceid = rs.getRows()[0].get("serviceid").toString();
				// 定义新增摘要的SQL语句
				sql = "insert into kbdata(serviceid,kbdataid,topic,abstract) values (?,?,?,?)";
				// 定义绑定参数集合ss
				lstpara = new ArrayList<Object>();
				// 绑定业务id参数
				lstpara.add(serviceid);
				// 获取摘要表的序列值，并绑定参数
				lstpara.add(kbdataid);
				// 绑定主题参数
				lstpara.add("常见问题");
				// 绑定摘要参数
				lstpara.add("<" + service + ">"
						+ lstWordclass.get(i).replace("子句", ""));
				// 将SQL语句放入集合中
				lstsql.add(sql.toString());
				// 将定义的绑定参数集合放入集合中
				lstlstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			}

			// 资源类型
			String resourceType = "";
			if ("基础".equals(container)) {// 基础词库
				resourceType = "baseWord";
			} else if ("子句".equals(container)) {// 子句词库
				resourceType = "sentence";
			}
			// 查询属性对应的列
			sql = "select columnNum,dataType from resourceAttrName2FieldColNum where resourceType like '%"
					+ resourceType + "%'";
			// 对应的列名
			String colName = "";
			Result rs = Database.executeQuery(sql);
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql );
			if (rs != null && rs.getRowCount() > 0) {
				for (int j = 0; j < rs.getRowCount(); j++) {
					String columnNum = rs.getRows()[j].get("columnNum")
							.toString();
					String dataType = rs.getRows()[j].get("dataType")
							.toString();
					colName = "attr" + columnNum + "_" + dataType;
				}
			}
			if (!colName.equals("")) {// 属性对应的列不为空
				sql = "insert into resourceAcessManager (id,resourceid,"
						+ colName + ") values (?,?,?)";
				lstsql.add(sql);
				List<Object> listpara = new ArrayList<Object>();
				listpara.add(resourceId);
				listpara.add(resourceType + "_" + wordcalssid);
				listpara.add(servicetype);
				lstlstpara.add(listpara);
				
				//文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			}

			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), " ", " ", "增加词类",
					lstWordclass.get(i), "WORDCLASS"));

		}
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}
	
	
	
	/**
	 * 更新词类的具体操作
	 * 
	 * @param id参数id
	 * @param oldvalue参数原有词类
	 * @param newvalue参数新的词类
	 * @param oldwordclasstype
	 *            旧词类归属
	 * @param newwordclasstype
	 *            新词类归属
	 * @return 更新返回值
	 */
	public static int update(User user, String id, String oldvalue,
			String newvalue, String oldwordclasstype, String newwordclasstype,
			String container) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String sql = null;
		
		if (GetConfigValue.isOracle) {
			sql = "update wordclass set wordclass=?, time =sysdate where wordclassid=? ";
		} else if (GetConfigValue.isMySQL) {
			sql = "update wordclass set wordclass=?, time =sysdate() where wordclassid=? ";
		}
		// 定义更新词类的SQL语句

		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定词类参数
		lstpara.add(newvalue);
		// 绑定id参数
		lstpara.add(id);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		// 若为子句，同时同步行业主题树
		if ("子句".equals(container)) {
			
		}
		// 生成操作日志记录
			// 将SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), " ", " ", "更新词类",
					oldvalue +
					 "==>"
					 + newvalue, "WORDCLASS"));

		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}
	
	/**
	 * 更新词类的具体操作
	 * 
	 * @param id参数id
	 * @param oldvalue参数原有词类
	 * @param newvalue参数新的词类
	 * @param oldwordclasstype
	 *            旧词类归属
	 * @param newwordclasstype
	 *            新词类归属
	 * @return 更新返回值
	 */
	public static int updateWithResource(User user, String id, String oldvalue,
			String newvalue, String oldwordclasstype, String newwordclasstype,
			String container) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String sql = null;
		
		String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		
		// 更新资源表
		String industry = "";
		
		if("当前行业".equals(newwordclasstype)){
			industry = user.getIndustryOrganizationApplication().split("->")[0]+"->通用商家->通用应用";
		} else if("当前商家".equals(newwordclasstype)) {
			industry = user.getIndustryOrganizationApplication();
		} else if("通用行业".equals(newwordclasstype)) {
			industry = "通用行业->通用商家->通用应用";
		}
		// 资源类型
		String resourceType = "";
		if ("基础".equals(container)) {// 基础词库
			resourceType = "baseWord";
		} else if("子句".equals(container)) {// 子句词库
			resourceType = "sentence";
		}
		// 查询属性对应的列
		sql = "select columnNum,dataType from resourceAttrName2FieldColNum where resourceType like '%"+resourceType+"%'";
		// 对应的列名
		String colName = "";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql );
		
		if (rs != null && rs.getRowCount() > 0) {	
			for (int j = 0; j < rs.getRowCount(); j++) {
				String columnNum = rs.getRows()[j].get("columnNum").toString();
				String dataType = rs.getRows()[j].get("dataType").toString();
				colName = "attr" + columnNum + "_" + dataType;
			}
		}
		if(!colName.equals("")) {// 属性对应的列不为空
			sql = "update resourceAcessManager set " + colName + "=? where resourceid='" + resourceType + "_" + id+"'";
			List<Object> param = new ArrayList<Object>();
			param.add(industry);
			lstsql.add(sql);
			lstlstpara.add(param);
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + param );
		}
		
		
		if (GetConfigValue.isOracle) {
			sql = "update wordclass set wordclass=?, time =sysdate where wordclassid=? ";
		} else if (GetConfigValue.isMySQL) {
			sql = "update wordclass set wordclass=?, time =sysdate() where wordclassid=? ";
		}
		// 定义更新词类的SQL语句

		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定词类参数
		lstpara.add(newvalue);
		// 绑定id参数
		lstpara.add(id);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		industry = user.getIndustryOrganizationApplication().split("->")[0];
		// 若为子句，同时同步行业主题树或通行业主题树
		if ("子句".equals(container)) {
			// 词类相同，词类归属不同
			if (oldvalue.equals(newvalue)
					&& !oldwordclasstype.equals(newwordclasstype)) {
				// 当前行业 修改成 通用行业
				if ("当前行业".equals(oldwordclasstype)
						&& "通用行业".equals(newwordclasstype)) {// 当前行业 修改成通用行业
					// 删除关联当前行业主题树的子句摘要及词模，并将子句摘要及词模重新添加到通用行业主题树上
					String abs = "<" + industry + "主题>"
							+ newvalue.replace("子句", "");
					// 定义删除摘要的SQL语句
					sql = "delete  from kbdata where abstract=?";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定摘要参数
					lstpara.add(abs);
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
					// 查询子句及对应的词条 作为主题树摘要及词模并同步至通用主题业务下
					// 获得子句词条对应数据源
					rs = getWordByWordclass(newvalue, "子句");
					// 获得子句词条对应字典
					Map<String, List<String>> map = getWorclassWordDic(rs);
					// 获得通用主题树ID
					Result result = CommonLibServiceDAO
							.getServiceIDByServiceAndBrand("通用行业主题", "通用行业主题");
					String serviceid = result.getRows()[0].get("serviceid")
							.toString();
					// 遍历子句词条字典，并插入摘要 词条表中
					for (Map.Entry<String, List<String>> entry : map.entrySet()) {
						String key = entry.getKey();
						List<String> list = entry.getValue();
						String absStr = "<通用行业主题>" + key.replace("子句", "");
						// 定义新增摘要的SQL语句
						sql = "insert into kbdata(serviceid,kbdataid,topic,abstract) values (?,?,?,?)";
						// 定义绑定参数集合ss
						lstpara = new ArrayList<Object>();
						// 绑定业务id参数
						lstpara.add(serviceid);
						String kbdataid = "";
						if (GetConfigValue.isOracle) {
							kbdataid =  (ConstructSerialNum.GetOracleNextValNew("SEQ_KBDATA_ID",bussinessFlag));
						} else if (GetConfigValue.isMySQL) {
							kbdataid = ConstructSerialNum.getSerialIDNew("kbdata","kbdataid",bussinessFlag);
						}
						// 获取摘要表的序列值，并绑定参数
						lstpara.add(kbdataid);
						// 绑定主题参数
						lstpara.add("常见问题");
						// 绑定摘要参数
						lstpara.add(absStr);
						// 将SQL语句放入集合中
						lstsql.add(sql);
						// 将定义的绑定参数集合放入集合中
						lstlstpara.add(lstpara);
						
						//文件日志
						GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

						// 插入对应的词模
						for (int i = 0; i < list.size(); i++) {
							if("".equals(list.get(i))){
								continue;
							}
							String wordpat = list.get(i) + "@2#编者=\"来源子句库\"";
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							// 获取词模表的序列值
							String wordpatid = "";
							if (GetConfigValue.isOracle) {
								wordpatid =  (ConstructSerialNum.GetOracleNextValNew("SEQ_WORDPATTERN_ID", bussinessFlag));
								sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate)";
							} else if (GetConfigValue.isMySQL) {
								wordpatid = ConstructSerialNum.getSerialIDNew("wordpat", "wordpatid", bussinessFlag);
								sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate())";
							}

							// 绑定模板id参数
							lstpara.add(wordpatid);
							// 绑定模板参数
							lstpara.add(wordpat);
							// 绑定地市参数
							lstpara.add("上海");
							// 绑定自动开关参数
							lstpara.add(0);
							// 绑定模板类型参数
							lstpara.add(getWordpatType(wordpat));
							// 绑定摘要id参数
							lstpara.add(kbdataid);
							// 绑定品牌参数
							lstpara.add("通用行业主题");
							// 将SQL语句放入集合中
							lstsql.add(sql.toString());
							// 将对应的绑定参数集合放入集合中
							lstlstpara.add(lstpara);

							
							//文件日志
							GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
							
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							// 定义新增模板备份表
							sql = "insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0)";
							// 绑定模板id参数
							lstpara.add(wordpatid);
							// 绑定地市参数
							lstpara.add("上海");
							// 绑定品牌参数
							lstpara.add("通用行业主题");
							// 将SQL语句放入集合中
							lstsql.add(sql.toString());
							// 将对应的绑定参数集合放入集合中
							lstlstpara.add(lstpara);
						
							//文件日志
							GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
						
						}
					}
				} else if (("通用行业".equals(oldwordclasstype) && "当前行业"
						.equals(newwordclasstype))) { // 通用行业修改成当前行业

					// 删除关联通用行业主题树的子句摘要及词模，并将子句摘要及词模重新添加到当前行业主题树上
					String abs = "<通用行业主题>" + newvalue.replace("子句", "");
					// 定义删除摘要的SQL语句
					sql = "delete  from kbdata where abstract=?";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定摘要参数
					lstpara.add(abs);
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
					
					// 查询子句及对应的词条 作为主题树摘要及词模并同步至通用主题业务下
					// 获得子句词条对应数据源
					rs = getWordByWordclass(newvalue, "子句");
					// 获得子句词条对应字典
					Map<String, List<String>> map = getWorclassWordDic(rs);
					// 获得当前行业主题树ID
					String serviceName = industry + "主题";
					Result result = CommonLibServiceDAO
							.getServiceIDByServiceAndBrand(serviceName,
									serviceName);
					String serviceid = result.getRows()[0].get("serviceid")
							.toString();
					// 遍历子句词条字典，并插入摘要 词条表中
					for (Map.Entry<String, List<String>> entry : map.entrySet()) {
						String key = entry.getKey();
						List<String> list = entry.getValue();
						String absStr = "<" + serviceName + ">"
								+ key.replace("子句", "");
						// 定义新增摘要的SQL语句
						sql = "insert into kbdata(serviceid,kbdataid,topic,abstract) values (?,?,?,?)";
						// 定义绑定参数集合ss
						lstpara = new ArrayList<Object>();
						// 绑定业务id参数
						lstpara.add(serviceid);
						String kbdataid = "";
						if (GetConfigValue.isOracle) {
							kbdataid =  (ConstructSerialNum.GetOracleNextValNew("SEQ_KBDATA_ID", bussinessFlag));
						} else if (GetConfigValue.isMySQL) {
							kbdataid = ConstructSerialNum.getSerialIDNew("kbdata", "kbdataid", bussinessFlag);
						}
						// 获取摘要表的序列值，并绑定参数
						lstpara.add(kbdataid);
						// 绑定主题参数
						lstpara.add("常见问题");
						// 绑定摘要参数
						lstpara.add(absStr);
						// 将SQL语句放入集合中
						lstsql.add(sql);
						// 将定义的绑定参数集合放入集合中
						lstlstpara.add(lstpara);

						//文件日志
						GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
						
						// 插入对应的词模
						for (int i = 0; i < list.size(); i++) {
							if("".equals(list.get(i))){
								continue;
							}
							String wordpat = list.get(i) + "@2#编者=\"来源子句库\"";
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							// 获取词模表的序列值
							String wordpatid = "";
							if (GetConfigValue.isOracle) {
								wordpatid =  (ConstructSerialNum.GetOracleNextValNew("SEQ_WORDPATTERN_ID", bussinessFlag));
								sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate)";
							} else if (GetConfigValue.isMySQL) {
								wordpatid = ConstructSerialNum.getSerialIDNew("wordpat", "wordpatid", bussinessFlag);
								sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate())";
							}

							// 绑定模板id参数
							lstpara.add(wordpatid);
							// 绑定模板参数
							lstpara.add(wordpat);
							// 绑定地市参数
							lstpara.add("上海");
							// 绑定自动开关参数
							lstpara.add(0);
							// 绑定模板类型参数
							lstpara.add(getWordpatType(wordpat));
							// 绑定摘要id参数
							lstpara.add(kbdataid);
							// 绑定品牌参数
							lstpara.add(serviceName);
							// 将SQL语句放入集合中
							lstsql.add(sql.toString());
							// 将对应的绑定参数集合放入集合中
							lstlstpara.add(lstpara);

							//文件日志
							GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
							
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							// 定义新增模板备份表
							sql = "insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0)";
							// 绑定模板id参数
							lstpara.add(wordpatid);
							// 绑定地市参数
							lstpara.add("上海");
							// 绑定品牌参数
							lstpara.add(serviceName);
							// 将SQL语句放入集合中
							lstsql.add(sql.toString());
							// 将对应的绑定参数集合放入集合中
							lstlstpara.add(lstpara);
						
							//文件日志
							GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
						
						}
					}

				} else if (("通用行业".equals(oldwordclasstype) || "当前行业"
						.equals(oldwordclasstype))
						&& "当前商家".equals(newwordclasstype)) {// 通用行业或当前行业修改成当前商家
					// 删除通用行业或者当前行业主题树下的对应的摘要及词模
					// 获得当前行业主题树ID
					String serviceName = null;
					if ("通用行业".equals(oldwordclasstype)) {
						serviceName = "通用行业主题";
					} else if ("当前行业".equals(oldwordclasstype)) {
						serviceName = industry + "主题";
					}
					String abs = "<" + serviceName + ">"
							+ newvalue.replace("子句", "");
					// 定义删除摘要的SQL语句
					sql = "delete from kbdata where abstract=?";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定摘要参数
					lstpara.add(abs);
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
					
				} else if ("当前商家".equals(oldwordclasstype)
						&& ("通用行业".equals(newwordclasstype) || "当前行业"
								.equals(newwordclasstype))) {// 当前商家修改成通用行业或者当前行业
					// 查询子句及对应的词条 作为主题树摘要及词模并同步至主题业务下
					// 获得子句词条对应数据源
					rs = getWordByWordclass(newvalue, "子句");
					// 获得子句词条对应字典
					Map<String, List<String>> map = getWorclassWordDic(rs);
					// 获得当前行业主题树ID
					String serviceName = null;
					if ("通用行业".equals(newwordclasstype)) {
						serviceName = "通用行业主题";
					} else if ("当前行业".equals(newwordclasstype)) {
						serviceName = industry + "主题";
					}

					Result result = CommonLibServiceDAO
							.getServiceIDByServiceAndBrand(serviceName,
									serviceName);
					String serviceid = result.getRows()[0].get("serviceid")
							.toString();
					// 遍历子句词条字典，并插入摘要 词条表中
					for (Map.Entry<String, List<String>> entry : map.entrySet()) {
						String key = entry.getKey();
						List<String> list = entry.getValue();
						String absStr = "<" + serviceName + ">"
								+ key.replace("子句", "");
						// 定义新增摘要的SQL语句
						sql = "insert into kbdata(serviceid,kbdataid,topic,abstract) values (?,?,?,?)";
						// 定义绑定参数集合ss
						lstpara = new ArrayList<Object>();
						// 绑定业务id参数
						lstpara.add(serviceid);
						String kbdataid = "";
						if (GetConfigValue.isOracle) {
							kbdataid =  (ConstructSerialNum.GetOracleNextValNew("SEQ_KBDATA_ID", bussinessFlag));
						} else if (GetConfigValue.isMySQL) {
							kbdataid = ConstructSerialNum.getSerialIDNew("kbdata", "kbdataid" ,bussinessFlag);
						}
						// 获取摘要表的序列值，并绑定参数
						lstpara.add(kbdataid);
						// 绑定主题参数
						lstpara.add("常见问题");
						// 绑定摘要参数
						lstpara.add(absStr);
						// 将SQL语句放入集合中
						lstsql.add(sql);
						// 将定义的绑定参数集合放入集合中
						lstlstpara.add(lstpara);

						//文件日志
						GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
						
						// 插入对应的词模
						for (int i = 0; i < list.size(); i++) {
							if("".equals(list.get(i))){
								continue;
							}
							String wordpat = list.get(i) + "@2#编者=\"来源子句库\"";
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							// 获取词模表的序列值
							String wordpatid = "";
							if (GetConfigValue.isOracle) {
								wordpatid =  (ConstructSerialNum.GetOracleNextValNew("SEQ_WORDPATTERN_ID", bussinessFlag));
								sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate)";
							} else if (GetConfigValue.isMySQL) {
								wordpatid = ConstructSerialNum.getSerialIDNew("wordpat", "wordpatid", bussinessFlag);
								sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate())";
							}

							// 绑定模板id参数
							lstpara.add(wordpatid);
							// 绑定模板参数
							lstpara.add(wordpat);
							// 绑定地市参数
							lstpara.add("上海");
							// 绑定自动开关参数
							lstpara.add(0);
							// 绑定模板类型参数
							lstpara.add(getWordpatType(wordpat));
							// 绑定摘要id参数
							lstpara.add(kbdataid);
							// 绑定品牌参数
							lstpara.add(serviceName);
							// 将SQL语句放入集合中
							lstsql.add(sql.toString());
							// 将对应的绑定参数集合放入集合中
							lstlstpara.add(lstpara);

							//文件日志
							GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
							
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							// 定义新增模板备份表
							sql = "insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0)";
							// 绑定模板id参数
							lstpara.add(wordpatid);
							// 绑定地市参数
							lstpara.add("上海");
							// 绑定品牌参数
							lstpara.add(serviceName);
							// 将SQL语句放入集合中
							lstsql.add(sql.toString());
							// 将对应的绑定参数集合放入集合中
							lstlstpara.add(lstpara);
						
							//文件日志
							GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
							
						}
					}

				}

			} else if (!oldvalue.equals(newvalue)
					&& oldwordclasstype.equals(newwordclasstype)) {// 词类不同，词类归属相同
				if ("通用行业".equals(newwordclasstype)) {// 通用行业需修改通用行业主题树下对应的摘要
					String oldAbs = "<通用行业主题>" + oldvalue.replace("子句", "");
					String newAbs = "<通用行业主题>" + newvalue.replace("子句", "");
					sql = "update kbdata t set t.abstract=? where t.abstract =? ";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定新的摘要参数
					lstpara.add(newAbs);
					// 绑定旧的摘要参数
					lstpara.add(oldAbs);
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
					
				} else if ("当前行业".equals(newwordclasstype)) {// 当前行业需修改通用行业主题树下对应的摘要
					String oldAbs = "<" + industry + "主题>"
							+ oldvalue.replace("子句", "");
					String newAbs = "<" + industry + "主题>"
							+ newvalue.replace("子句", "");
					sql = "update kbdata t set t.abstract=? where t.abstract =? ";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定新的摘要参数
					lstpara.add(newAbs);
					// 绑定旧的摘要参数
					lstpara.add(oldAbs);
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
				
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
					
				}
			}

		}

		//	
		//
		// // 生成操作日志记录
		// // 将SQL语句放入集合中
		// lstsql.add(MyUtil.LogSql());
		// // 将对应的绑定参数集合放入集合中
		// lstlstpara.add(MyUtil.LogParam(" ", " ", "更新词类", "上海", oldvalue +
		// "==>"
		// + newvalue, "WORDCLASS"));

		/**
		 * 下列注释的操作替换成成了触发器 // 更新词类的SQL语句 sql =
		 * "update wordpat set wordpat=replace(wordpat,?,?) where wordpat like ?"
		 * ; // 定义绑定参数集合 lstpara = new ArrayList<String>(); // 绑定原有词类参数
		 * lstpara.add("!" + oldvalue + "|"); // 绑定新的词类参数 lstpara.add("!" +
		 * newvalue + "|"); // 绑定词类参数 lstpara.add("%!" + oldvalue + "|%"); //
		 * 将SQL语句放入集合中 lstsql.add(sql); // 将对应的绑定参数集合放入集合中
		 * lstlstpara.add(lstpara);
		 * 
		 * // 定义绑定参数集合 lstpara = new ArrayList<String>(); // 绑定原有词类参数
		 * lstpara.add("!" + oldvalue + ">"); // 绑定新的词类参数 lstpara.add("!" +
		 * newvalue + ">"); // 绑定词类参数 lstpara.add("%!" + oldvalue + ">%"); //
		 * 将SQL语句放入集合中 lstsql.add(sql); // 将对应的绑定参数集合放入集合中
		 * lstlstpara.add(lstpara);
		 */
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	/**
	 * 删除词类
	 * @param user 用户登录信息
	 * @param wordclassid参数词类id
	 * @param wordclass参数词类
	 * @return 删除返回的json串
	 */
	public static int delete(User user, String wordclassid, String wordclass,String wordclasstype,String container) {
		// 定义多条SQL语句集合 
		List<String> lstsql = new ArrayList<String>();
		// 对应多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 删除词类的SQL语句
		String sql = "delete from wordclass where wordclassid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定词类id参数
		lstpara.add(wordclassid);
		// 将删除词类sql存入集合中
		lstsql.add(sql);
		// 将删除词类参数集合存入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		//判断是否是子句，若是子句通过wordclasstype判断是否需要删除行业主题树对应摘要
		if("子句".equals(container)&&wordclasstype.contains("行业")){
			String service =null;
			if("通用行业".equals(wordclasstype)){
				service = "通用行业主题";
			}else if("当前行业".equals(wordclasstype)){
				String serviceType= user.getIndustryOrganizationApplication();
				service = serviceType.split("->")[0]+"主题";
			}
			String abs = "<"+service+">"+wordclass.replace("子句", "");
			// 删除词类的SQL语句
			 sql = "delete  from kbdata where abstract =? ";
			// 定义绑定参数集合
			 lstpara = new ArrayList<String>();
			// 绑定词类id参数
			lstpara.add(abs);
			// 将删除词类sql存入集合中
			lstsql.add(sql);
			// 将删除词类参数集合存入集合中
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			
		}
		
		// 删除资源表中的记录
		// 资源类型
		String resourceType = "";
		if ("基础".equals(container)) {// 基础词库
			resourceType = "baseWord";
		} else if("子句".equals(container)) {// 子句词库
			resourceType = "sentence";
		}
		sql = "delete  from resourceAcessManager where resourceid=?";
		lstsql.add(sql);
		List<Object> param = new ArrayList<Object>();
		param.add(resourceType+"_"+wordclassid);
		lstlstpara.add(param);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + param );
		
		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstsql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", " ", "删除词类",
				wordclass, "WORDCLASS"));
		
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
		
	}
	
	
	/**
	 * 删除词类
	 * @param user 用户登录信息
	 * @param wordclassid参数词类id
	 * @param wordclass参数词类
	 * @return 删除返回的json串
	 */
	public static int deleteWithResource(User user, String wordclassid, String wordclass,String wordclasstype,String container) {
		// 定义多条SQL语句集合 
		List<String> lstsql = new ArrayList<String>();
		// 对应多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 删除词类的SQL语句
		String sql = "delete from wordclass where wordclassid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定词类id参数
		lstpara.add(wordclassid);
		// 将删除词类sql存入集合中
		lstsql.add(sql);
		// 将删除词类参数集合存入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		//判断是否是子句，若是子句通过wordclasstype判断是否需要删除行业主题树对应摘要
		if("子句".equals(container)&&wordclasstype.contains("行业")){
		}
		
		// 删除资源表中的记录
		// 资源类型
		
		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstsql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", " ", "删除词类",
				wordclass, "WORDCLASS"));
		
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
		
	}
	
	
	
	/**
	 *@description 通过词类名和词类表中归属类型获得词条
	 *@param wordcalss
	 *            词类名
	 *@param container
	 *            归属
	 *@return
	 *@returnType Result
	 */
	public static Result getWordByWordclass(String wordcalss, String container) {
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 插入词类的SQL语句
		String sql = "select wc.wordclass,wr.word from (select * from wordclass where wordclass=? and container=? ) wc  left join (select * from word  where  stdwordid is null)  wr on  wc.wordclassid =wr.wordclassid ";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		lstpara.add(wordcalss);
		lstpara.add(container);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return Database.executeQuery(sql, lstpara.toArray());
	}

	/**
	 *@description 创建词类词条字典
	 *@param rs
	 *            词类词条数据源
	 *@return
	 *@returnType Map<String,List<String>>
	 */
	public static Map<String, List<String>> getWorclassWordDic(Result rs) {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < rs.getRowCount(); i++) {
			String wordclass = rs.getRows()[i].get("wordclass").toString();
			String word = rs.getRows()[i].get("word") == null ? "" : rs
					.getRows()[i].get("word").toString();
			String wordpat = word;
			if (map.containsKey(wordclass)) {
				list = map.get(wordclass);
				list.add(wordpat);
				map.put(wordclass, list);
			} else {
				list = new ArrayList<String>();
				list.add(wordpat);
				map.put(wordclass, list);
			}
		}
		return map;
	}

	/**
	 *@description 获得词模类型字段值
	 *@param wordpat
	 *            词模
	 *@return
	 *@returnType String
	 */
	public static String getWordpatType(String wordpat) {
		String wordpatType;
		if (wordpat.indexOf("-") != -1 && wordpat.indexOf("*") == -1) {// 等于词模
			wordpatType = "1";
		} else if (wordpat.startsWith("~")) {// 排除词模
			wordpatType = "2";
		} else if (wordpat.startsWith("++")) {// 选择词模
			wordpatType = "3";
		} else if (wordpat.startsWith("+") && !wordpat.startsWith("++")) {// 特征词模
			wordpatType = "4";
		} else {// 普通词模
			wordpatType = "0";
		}
		return wordpatType;

	}

	/**
	 * 查询该行业中是否有这个词
	 * 
	 * @param word
	 * @param business
	 * @return
	 */
	public static ArrayList<String> getWordClass(String word, String business) {
		ArrayList<String> wordclass = new ArrayList<String>();
		Result rs = null;
		// 根据词获得wordclass词类id
		String sql = "select wordclassid from word where word='" + word+ "'";
		List<String> wordclassids = new ArrayList<String>();
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				if (rs.getRows()[i].get("wordclassid") != null) {
					String wordclassid = rs.getRows()[i].get("wordclassid")
							.toString();
					wordclassids.add(wordclassid);
				}
			}
		} else {
			return wordclass;
		}

		// 根据属性名找到属性对应的列及列的值
		String colName = "";// 属性对应列的名称
		sql = "select columnNum,dataType from resourceAttrName2FieldColNum where name like '行业归属'";
		rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				String columnNum = rs.getRows()[i].get("columnNum").toString();
				String dataType = rs.getRows()[i].get("dataType").toString();
				colName = "attr" + columnNum + "_" + dataType;
			}
		} else {
			return wordclass;
		}

		// 词语对应的词类id
		List<String> cileiIDs = new ArrayList<String>();
		sql = "select resourceid," + colName + " from resourceAcessManager where (";
		for (String wordclassid : wordclassids) {
			sql += "resourceid like '%" + wordclassid + "' or ";
		}
		sql = sql.substring(0, sql.lastIndexOf("or")) + ")";
		rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		for (int i = 0; i < rs.getRowCount(); i++) {
			// 词类id
			String resourceid = rs.getRows()[i].get("resourceid").toString();
			// 词类对应的属性值
			String resource = rs.getRows()[i].get(colName)==null?"":rs.getRows()[i].get(colName).toString();
			if (business.equals(resource)) {// 如果词类对应的属性值等于传入参数的属性值
				cileiIDs.add(resourceid.substring(resourceid.indexOf("_")+1, resourceid.length()));
			}
		}

		if (cileiIDs.isEmpty()) {
			return wordclass;
		} else {// 查询对应的父类
			sql = "select wordclass from Wordclass where (";
			for (int i = 0; i < cileiIDs.size(); i++) {
				sql += "wordclassid=" + cileiIDs.get(i) + " or ";
			}
			sql = sql.substring(0, sql.lastIndexOf("or")) + ")";
			rs = Database.executeQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
			if (rs != null && rs.getRowCount() > 0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 词类
					String str = rs.getRows()[i].get("wordclass").toString();
					wordclass.add(str);
				}
			} else {
				return wordclass;
			}
		}
		return wordclass;
	}

	/**
	 *@description 集合降序排列
	 *@param list
	 *@return 
	 *@returnType List<String> 
	 */
	public static List<String> sortListDesc(List<String> list) {
		Collections.sort(list, new Comparator() {
			@Override
			public int compare(Object o1, Object o2) {
				double d = new Double((String) o1).compareTo(new Double(
						(String) o2));
				return d > 0 ? -1 : 1;
			}
		});
		return list;
	}

	
	/**
	 * 判断词类是否重复
	 * 
	 * @param oldwordclass参数旧词类
	 * @param newWordclass参数新词类
	 * @return 是否重复
	 */
	public static Boolean exist(String oldWordclass ,String newWordclass) {
		if(oldWordclass.equals(newWordclass)){
			return false; 
		}
		String sql = "select * from wordclass where wordclass=?  ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定词类参数
		lstpara.add(newWordclass);
			// 执行SQL语句，返回数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 有重复词类，返回true
				return true;
			} else {
				// 没有重复词类，返回false
				return false;
			}
		
	}
	
	
	/**
	 * 判断词类是否重复
	 * 
	 * @param newWordclass参数新词类
	 * @return 是否重复
	 */
	public static Boolean exist(String newWordclass) {
		String sql = "select * from wordclass where wordclass=?  ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定词类参数
		lstpara.add(newWordclass);
			// 执行SQL语句，返回数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 有重复词类，返回true
				return true;
			} else {
				// 没有重复词类，返回false
				return false;
			}
		
	}

	/**
	 * 查询父类父子句词类
	 * @return
	 */
	public static Result getFWordclass() {
		String sql = "select wordclass,wordclassid from wordclass where (wordclass like '%父类' or wordclass like'%父子句') ";
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}
	
	/**
	 *@description 通过词类获取词类ID 
	 *@return 
	 *@returnType Result 
	 */
	public static Result getWordclassID(String wordclass) {
		String sql = "select wordclassid from wordclass where wordclass ='"+wordclass+"'";
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}
	
}
