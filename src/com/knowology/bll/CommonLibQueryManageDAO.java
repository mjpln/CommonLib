package com.knowology.bll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.Bean.ImportNormalqueryBean;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.UtilityOperate.StringUtil;
import com.knowology.dal.Database;
import com.str.NewEquals;

public class CommonLibQueryManageDAO {

	/**
	 * @description 查询问题模型业务树
	 * @param serviceid
	 *            业务ID
	 * @param brand
	 *            品牌
	 * @return
	 * @returnType Result
	 */
	public static Result createServiceTree(String serviceid, String brand, String citySelect) {
		String sql = "";
		if ("".equals(serviceid) || serviceid == null) {
			sql = "select serviceid, service from service where parentid =0  and service='" + brand + "' ";
			if (citySelect != null && !"全国".equals(citySelect) && !"".equals(citySelect)) {
				sql = sql + " and (city like '%" + citySelect + "%' or city is null)";
			}
		} else {
			String service = CommonLibServiceDAO.getNameByserviceid(serviceid);
			if (NewEquals.equals(service, brand)) {
				// MOD Zhao Lipeng 2017-03-21 START 解决业务树加载不出来部分业务问题
				// sql = "select serviceid ,service from service where brand =
				// '"
				// + brand + "' and service like '%模型' and parentid="
				// + serviceid;
				sql = "select serviceid ,service  from service where brand = '" + brand + "' and parentid=" + serviceid;
				if (citySelect != null && !"全国".equals(citySelect) && !"".equals(citySelect)) {
					sql = sql + " and (city like '%" + citySelect + "%' or city is null)";
				}
				// MOD Zhao Lipeng 2017-03-21 END
			} else {
				sql = "select serviceid ,service  from service where brand = '" + brand + "' and parentid=" + serviceid;
				if (citySelect != null && !"全国".equals(citySelect) && !"".equals(citySelect)) {
					sql = sql + " and (city like '%" + citySelect + "%' or city is null)";
				}
			}
		}
		Result rs = null;
		rs = Database.executeQuery(sql);

		// 文件日志
		GlobalValue.myLog.info(sql);

		return rs;
	}

	/**
	 * @description 查询问题模型业务树 ，延时加载
	 * @param serviceid
	 *            业务ID
	 * @param brand
	 *            品牌
	 * @return
	 * @returnType Result
	 */
	public static Result createServiceTreeNew(String userId, String serviceType, String resourcetype, String citySelect,
			String scenariosid) {
		String sql = null;

		if (scenariosid == null || "".equals(scenariosid.trim())) {

			sql = "select serviceid, service  from service where  parentid='0' and brand in (";
			Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("问题库业务根对应关系配置", serviceType);

			if (rsConfig != null && rsConfig.getRowCount() > 0) {
				for (int i = 0; i < rsConfig.getRowCount(); i++) {
					String brand = rsConfig.getRows()[i].get("name").toString();
					if (i == rsConfig.getRowCount() - 1) {
						sql += "'" + brand + "')";
					} else {
						sql += "'" + brand + "',";
					}
				}
			} else {// 未配置问题库业务根对应关系
				return null;
			}

			// sql = "select serviceid, service from service where brand ='"
			// + brand + "' ";
			if (citySelect != null && !"全国".equals(citySelect) && !"".equals(citySelect)) {
				String[] cityList = citySelect.split(",");
				sql = sql + " and (city = '全国' or  city is null ";
				for (int i = 0; i < cityList.length; i++) {
					sql = sql + " or city like '%" + cityList[i] + "%'";
				}
				sql = sql + " )";
			}
			sql = sql + " order by serviceid";
			// sql =
			// "select scenariosid, name from scenarios where parentid is null
			// and servicetype ='"
			// + serviceType + "' order by scenariosid ";
		} else {
			sql = "select serviceid, service  from service where serviceid in (select resourceid from  role_resource where resourcetype='"
					+ resourcetype + "' and servicetype='" + serviceType
					+ "' and operationtype like '%S%' and roleid=(select roleid from workerrolerel where workerid='"
					+ userId + "') ) and parentid='" + scenariosid + "' ";
			// if (citySelect != null && !"全国".equals(citySelect)
			// && !"".equals(citySelect)) {
			// sql = sql + " and (city like '%" + citySelect
			// + "%' or city is null or city = '全国')";
			// }

			if (citySelect != null && !"全国".equals(citySelect) && !"".equals(citySelect)) {
				String[] cityList = citySelect.split(",");
				sql = sql + " and (city = '全国' or  city is null ";
				for (int i = 0; i < cityList.length; i++) {
					sql = sql + " or city like '%" + cityList[i] + "%'";
				}
				sql = sql + " )";
			}
			sql = sql + " order by serviceid";
			// sql = "select scenariosid , name from scenarios where parentid="
			// + scenariosid + " order by scenariosid";
		}
		Result rs = null;
		rs = Database.executeQuery(sql);

		// 文件日志
		GlobalValue.myLog.info(sql);

		return rs;
	}

	/**
	 * 递归查询子节点，层级为3
	 * 
	 * @param parentId
	 * @param brand
	 * @return
	 */
	public static Result getServiceByPId(String parentId, String brand) {
		Result rs = null;
		String sql = "select serviceid ,parentid  from  service start  with  ( parentid='";
		if (StringUtils.isEmpty(parentId)) {
			parentId = "0";
		}
		if (GetConfigValue.isToMysql) {
			if (StringUtils.isEmpty(brand))
				sql = sql + parentId + "') connect by nocycle prior serviceid = parentid ";
			else {
				sql = sql + parentId + "' and brand='" + brand + "') connect by nocycle prior serviceid = parentid";
			}
		} else if (StringUtils.isEmpty(brand)) {
			sql += parentId + "') connect by nocycle prior serviceid = parentid and level <= 3";
		} else {
			sql += parentId + "' and brand='" + brand
					+ "') connect by nocycle prior serviceid = parentid and level <= 3";
		}
		rs = Database.executeQuery(sql);

		// 文件日志
		GlobalValue.myLog.info(sql);
		return rs;
	}

	/**
	 * 根据子节点递归查询父节点
	 * 
	 * @param parentId
	 * @param brand
	 * @return
	 */
	public static Result getServicePIdById(String serviceId) {
		Result rs = null;
		String sql = "select serviceid ,parentid  from  service start  with  ( serviceid='" + serviceId
				+ "' )  connect by nocycle serviceid = prior parentid ";

		rs = Database.executeQuery(sql);

		// 文件日志
		GlobalValue.myLog.info(sql);
		return rs;
	}

	/**
	 * 查询指定根目录下的业务
	 * 
	 * @param rootService
	 * @return
	 */
	public static Result createServiceTree(String[] rootService, String brand, String citySelect) {
		Result rs = null;
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT service, serviceid, parentid FROM service START WITH service IN (");
		for (String service : rootService) {
			sb.append("'" + service + "',");
		}
		if (rootService.length > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(") ");
		sb.append(" AND brand='" + brand + "'");

		if (citySelect != null && !"全国".equals(citySelect) && !"".equals(citySelect)) {
			sb.append(" and (city like '%" + citySelect + "%' or city is null)");
		}
		sb.append(" CONNECT BY PRIOR serviceid = parentid ");

		rs = Database.executeQuery(sb.toString());

		// 文件日志
		GlobalValue.myLog.info(sb);

		return rs;
	}

	/**
	 * 问题库业务查询
	 * 
	 * @param rootService
	 *            业务根节点。这里就是“安徽电信问题库”
	 * @param serviceStr
	 *            查询的业务名
	 * @param serviceidList
	 *            当前用户业务id集合
	 * @return
	 */
	public static Result searchService(List<String> rootServiceList, String serviceStr, String cityCode, User user) {
		Result rs = null;
		StringBuilder sb = new StringBuilder();
		List<String> params = new ArrayList<String>();

		sb.append(" SELECT * ");
		sb.append(" FROM ");
		sb.append("   (SELECT Service, ");
		sb.append("     Serviceid, ");
		sb.append("     city, ");
		sb.append("     SUBSTR(Sys_Connect_By_Path(Service,'->'),3) Name_Path, ");
		sb.append("     SUBSTR(Sys_Connect_By_Path(Serviceid,'->'),3) Serviceid_Path ");
		sb.append("   FROM Service ");
		sb.append("     START WITH service        IN (");
		for (String service : rootServiceList) {
			sb.append("'" + service + "',");
		}
		if (rootServiceList.size() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		sb.append(") ");
		sb.append("     CONNECT BY Prior Serviceid = Parentid ");
		sb.append(") GHJbieming ");
		sb.append(" WHERE Upper(Service) LIKE ? ");
		params.add("%" + serviceStr.toUpperCase() + "%");
		if (!"全国".equals(cityCode)) {
			sb.append(" AND( city LIKE ? ");
			sb.append(" OR city LIKE '全国') ");
			params.add("%" + cityCode + "%");
		}
		sb.append(" AND Serviceid IN ");
		sb.append("   (SELECT resourceid AS serviceid ");
		sb.append("   FROM role_resource ");
		sb.append("   WHERE roleid IN ");
		sb.append("     (SELECT roleid FROM workerrolerel WHERE workerid= ? ");
		sb.append("     ) ");
		sb.append("   AND Resourcetype='querymanage' ");
		sb.append("   AND Servicetype = ? ");
		sb.append("   )");

		params.add(user.getUserID());
		params.add(user.getIndustryOrganizationApplication());

		rs = Database.executeQuery(sb.toString(), params.toArray());

		// 文件日志
		GlobalValue.myLog.info(sb + "#" + StringUtils.join(params, "#"));

		return rs;
	}

	/**
	 * 问题库业务查询（停止使用）
	 * 
	 * @param rootService
	 *            业务根节点。这里就是“安徽电信问题库”
	 * @param serviceStr
	 *            查询的业务名
	 * @param serviceidList
	 *            当前用户业务id集合
	 * @return
	 */
	@Deprecated
	public static Result searchService(List<String> rootServiceList, String serviceStr, List<String> serviceidList) {
		Result rs = null;
		StringBuilder sb = new StringBuilder();
		// List<String> params = new ArrayList<String>();

		sb.append(" SELECT * ");
		sb.append(" FROM ");
		sb.append("   (SELECT service, ");
		sb.append("     serviceid, ");
		sb.append("     SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) NAME_PATH, ");
		sb.append("     SUBSTR(SYS_CONNECT_BY_PATH(serviceid,'->'),3) SERVICEID_PATH ");
		sb.append("   FROM service ");
		sb.append("     START WITH service        IN (");
		for (String service : rootServiceList) {
			sb.append("'" + service + "',");
		}
		if (rootServiceList.size() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		// sb.append(" START WITH service IN ('安徽电信问题库', '电信垃圾问题库') ");
		sb.append(") ");
		sb.append("     CONNECT BY PRIOR serviceid = parentid ");
		sb.append("   ) GHJbieming ");
		sb.append(" WHERE UPPER(service) LIKE ?");

		if (serviceidList != null) {
			sb.append(" and serviceid IN (");
			for (String serviceid : serviceidList) {
				sb.append("" + serviceid + ",");
			}
			if (serviceidList.size() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append(") ");
		}

		// params.add("%" + serviceStr.toUpperCase() +"%");

		rs = Database.executeQuery(sb.toString(), "%" + serviceStr.toUpperCase() + "%");

		// 文件日志
		GlobalValue.myLog.info(sb + "#" + "%" + serviceStr.toUpperCase() + "%");

		return rs;
	}

	/**
	 * 查询子业务记录数
	 * 
	 * @param serviceid
	 *            业务id
	 * @return int
	 */
	public static int hasChild(String serviceid) {
		int count = 0;
		String sql = "select count(*) as nums from service where parentid =" + serviceid;

		Result rs = Database.executeQuery(sql);

		// 文件日志
		GlobalValue.myLog.info(sql);

		if (rs != null) {
			count = Integer.parseInt(rs.getRows()[0].get("nums").toString());
		}
		return count;
	}

	/**
	 * @description 新增表标准问题及客户问题
	 * @param serviceid
	 * @param normalQuery
	 * @param customerQuery
	 * @param cityCode
	 * @param serviceType
	 * @param workerid
	 * @param userCityCode
	 * @param serviceCityCode
	 *            业务地市
	 * @return
	 * @returnType int
	 */
	public static int addNormalQueryAndCustomerQuery(String serviceid, String normalQuery, String customerQuery,
			String cityCode, User user, String userCityCode, String serviceCityCode) {
		String updateSql = "update kbdata set city =? where kbdataid =? ";
		String updateWordpatSql = " update wordpat set city =? where kbdataid =? and city =? and wordpat not like '%来源=%'";
		String insertSql = "insert into querymanage(ID,KBDATAID,QUERY,CITY,WORKERID) values(?,?,?,?,?)";
		String insertKbdataSql = "insert into kbdata(serviceid,kbdataid,topic,abstract,city) values (?,?,?,?,?)";

		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		List<Object> lstpara = new ArrayList<Object>();
		int rs = -1;
		String kbdataid = "";
		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());

		cityCode = new String(serviceCityCode); // 用户地市改成业务地市 add by zhao
												// lipeng.

		Map<String, Map<String, String>> map = getNormalQueryDic(serviceid);
		if (map.containsKey(normalQuery)) {// 标准问题已存在, update标准问city
			Map<String, String> tempMap = map.get(normalQuery);
			String city = tempMap.get("city"); // 业务对应地市
			kbdataid = tempMap.get("kbdataid");
			if (!"".equals(city)) {
				List<String> cityCodeList = new ArrayList<String>(Arrays.asList(city.split(",")));
				List<String> tempCityCodeList = new ArrayList<String>(Arrays.asList(serviceCityCode.split(",")));
				tempCityCodeList.addAll(cityCodeList);
				List<String> addCityCodeList = new ArrayList<String>(Arrays.asList(cityCode.split(",")));
				tempCityCodeList.addAll(addCityCodeList);
				Set set = new HashSet(tempCityCodeList);
				List<String> tempList = new ArrayList(set);
				String cityCodeAll = StringUtils.join(tempList.toArray(), ",");
				// 修改摘要地市
				lstpara = new ArrayList<Object>();
				lstpara.add(cityCodeAll);
				lstpara.add(kbdataid);
				lstsql.add(updateSql);
				lstlstpara.add(lstpara);

				// 文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + updateSql + "#" + lstpara);

				// 日志 insert into
				// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
				lstsql.add(getInsertLogSql());
				String service = CommonLibServiceDAO.getNameByserviceid(serviceid);
				lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), service, "修改标准问题", " ", user.getUserID(),
						user.getUserName(), normalQuery, "KBDATA"));

				// 修改摘要关联词模地市
				lstpara = new ArrayList<Object>();
				lstpara.add(cityCodeAll.replace(",", "|"));
				lstpara.add(kbdataid);
				lstpara.add(city.replace(",", "|"));
				lstsql.add(updateWordpatSql);
				lstlstpara.add(lstpara);

				// 文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + updateWordpatSql + "#" + lstpara);

				Database.executeNonQueryTransaction(lstsql, lstlstpara);
				return addCustomerQuery(kbdataid, customerQuery, cityCode, user);

			} else {// 为空默认为全国，视为已存在，直接插入客户问题
				return addCustomerQuery(kbdataid, customerQuery, cityCode, user);

			}

		} else {
			if (GetConfigValue.isOracle) {
				kbdataid = ConstructSerialNum.GetOracleNextValNew("SEQ_KBDATA_ID", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				kbdataid = ConstructSerialNum.getSerialIDNew("kbdata", "kbdataid", bussinessFlag);
			}
			String service = CommonLibServiceDAO.getNameByserviceid(serviceid);
			String abs = "<" + service + ">" + normalQuery;
			// 新增摘要
			lstpara = new ArrayList<Object>();
			lstpara.add(serviceid);
			lstpara.add(kbdataid);
			lstpara.add("常见问题");
			lstpara.add(abs);
			// 标准问默认取业务地市
			lstpara.add(serviceCityCode);
			lstsql.add(insertKbdataSql);
			lstlstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + insertKbdataSql + "#" + lstpara);

			// 日志 insert into
			// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
			lstsql.add(getInsertLogSql());
			lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), service, "新增标准问题", " ", user.getUserID(),
					user.getUserName(), abs, "KBDATA"));

			String querymanageId = "";

			// 新增和摘要相同的客户问题（避免重复添加标准问扩展问）
			boolean isInsert = true;
			if (customerQuery != null && !"".equals(customerQuery)) {// 客户问不为空时判断是否包含和标准问相同的客户问
				String[] customerQueryArray = customerQuery.split("\n");
				for (int i = 0; i < customerQueryArray.length; i++) {
					String q = customerQueryArray[i];
					if (normalQuery.equals(q)) {// 包含同名客户问则不插入
						isInsert = false;
					}
				}
			}
			if (isInsert) {
				if (GetConfigValue.isOracle) {
					querymanageId = ConstructSerialNum.GetOracleNextValNew("seq_querymanage_id", bussinessFlag);
				} else if (GetConfigValue.isMySQL) {
					querymanageId = ConstructSerialNum.getSerialIDNew("querymanage", "id", bussinessFlag);
				}
				lstpara = new ArrayList<Object>();
				lstpara.add(querymanageId);
				lstpara.add(kbdataid);
				lstpara.add(normalQuery);
				// lstpara.add(userCityCode);
				// 未选择地市时取业务地市
				if (cityCode == null || "".equals(cityCode)) {
					cityCode = serviceCityCode;
				}
				lstpara.add(cityCode);
				lstpara.add(user.getUserID());
				lstsql.add(insertSql);
				lstlstpara.add(lstpara);
				// 文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + insertSql + "#" + lstpara);

				// 日志 insert into
				// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
				lstsql.add(getInsertLogSql());
				lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), service, "增加客户问题", " ", user.getUserID(),
						user.getUserName(), normalQuery, "QUERYMANAGE"));
			}

			// 新增客户问题
			if (!"".equals(customerQuery) && customerQuery != null) {
				String customerQueryArray[] = customerQuery.split("\n");
				for (int i = 0; i < customerQueryArray.length; i++) {
					String temp = customerQueryArray[i];
					if ("".equals(temp)) {
						continue;
					}
					if (GetConfigValue.isOracle) {
						querymanageId = ConstructSerialNum.GetOracleNextValNew("seq_querymanage_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						querymanageId = ConstructSerialNum.getSerialIDNew("querymanage", "id", bussinessFlag);
					}
					lstpara = new ArrayList<Object>();
					lstpara.add(querymanageId);
					lstpara.add(kbdataid);
					lstpara.add(temp);
					lstpara.add(cityCode);
					lstpara.add(user.getUserID());
					lstsql.add(insertSql);
					lstlstpara.add(lstpara);

					// 文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" + insertSql + "#" + lstpara);

					// 日志 insert into
					// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
					lstsql.add(getInsertLogSql());
					lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), service, "增加客户问题", " ",
							user.getUserID(), user.getUserName(), temp, "QUERYMANAGE"));
				}
			}

		}

		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	/**
	 * @description 新增客户问题
	 * @param normalQuery
	 * @param customerQuery
	 * @param cityCode
	 * @param serviceType
	 * @param workerid
	 * @param
	 * @return
	 * @returnType int
	 */
	public static int addCustomerQuery(String normalQuery, String customerQuery, String cityCode, User user) {
		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		List<Object> lstpara = new ArrayList<Object>();
		int rs = -1;
		String querymanageId = "";
		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());
		String insertSql = "insert into querymanage(ID,KBDATAID,QUERY,CITY,WORKERID,QUERYTYPE) values(?,?,?,?,?,0)";
		String updateSql = " update querymanage set CITY=? , EDITTIME=sysdate where QUERY=? and KBDATAID=? and QUERYTYPE = 0";
		Map<String, String> map = getCustomerQueryDic(normalQuery, 0);
		Map<String, Map<String, String>> insertOrUpdateDic = getCustomerQueryInsertOrUpdateDic(map, customerQuery,
				cityCode);
		if (insertOrUpdateDic.size() > 0) {
			for (Map.Entry<String, Map<String, String>> entry : insertOrUpdateDic.entrySet()) {
				String type = entry.getKey();
				if ("insert".equals(type)) {// insert
					for (Map.Entry<String, String> insertDic : entry.getValue().entrySet()) {
						String query = insertDic.getKey();
						String city = insertDic.getValue();
						if (GetConfigValue.isOracle) {
							querymanageId = ConstructSerialNum.GetOracleNextValNew("seq_querymanage_id", bussinessFlag);
						} else if (GetConfigValue.isMySQL) {
							querymanageId = ConstructSerialNum.getSerialIDNew("querymanage", "id", bussinessFlag);
						}
						lstpara = new ArrayList<Object>();
						lstpara.add(querymanageId);
						lstpara.add(normalQuery);
						lstpara.add(query);
						lstpara.add(city);
						lstpara.add(user.getUserID());
						lstsql.add(insertSql);
						lstlstpara.add(lstpara);
						// 日志 insert into
						// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
						String[] serviceArr = getServiceByKbdataid(normalQuery);
						lstsql.add(getInsertLogSql());
						lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), serviceArr[0], "增加客户问题", " ",
								user.getUserID(), user.getUserName(), query, "QUERYMANAGE"));

					}
				} else {// update
					for (Map.Entry<String, String> insertDic : entry.getValue().entrySet()) {
						String query = insertDic.getKey();
						String city = insertDic.getValue();
						lstpara = new ArrayList<Object>();
						lstpara.add(city);
						lstpara.add(query);
						lstpara.add(normalQuery);
						lstsql.add(updateSql);
						lstlstpara.add(lstpara);
						// 日志 insert into
						// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
						String[] serviceArr = getServiceByKbdataid(normalQuery);
						lstsql.add(getInsertLogSql());
						lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), serviceArr[0], "更新客户问题", " ",
								user.getUserID(), user.getUserName(), query, "QUERYMANAGE"));
					}
				}
			}
			return Database.executeNonQueryTransaction(lstsql, lstlstpara);
		} else {
			rs = 1;// 新增客户问题已存在默认插入成功，后续优化给出提示 TODO
		}

		return rs;
	}

	/**
	 * @description 获取客户问题插入或则修改字典
	 * @param map
	 * @param customerQuery
	 * @param cityCode
	 * @return
	 * @returnType Map<String,Map<String,String>>
	 */
	public static Map<String, Map<String, String>> getCustomerQueryInsertOrUpdateDic(Map<String, String> map,
			String customerQuery, String cityCode) {
		Map<String, Map<String, String>> insertOrUpdateDic = new HashMap<String, Map<String, String>>();
		List<String> cityCodeList = new ArrayList<String>(Arrays.asList(cityCode.split(",")));
		if (!"".equals(customerQuery) && customerQuery != null) {
			String customerQueryArray[] = customerQuery.split("\n");
			for (int i = 0; i < customerQueryArray.length; i++) {
				String temp = customerQueryArray[i].trim();
				if ("".equals(temp)) {
					continue;
				}
				if (map.containsKey(temp)) {// 客户问题已存在,直接update地市
					String tempCityCode = map.get(temp);
					List<String> tempCityCodeList = new ArrayList<String>(Arrays.asList(tempCityCode.split(",")));
					tempCityCodeList.addAll(cityCodeList);
					Set set = new HashSet(tempCityCodeList);
					List<String> tempList = new ArrayList(set);
					tempCityCode = StringUtils.join(tempList.toArray(), ",");
					if (insertOrUpdateDic.containsKey("update")) {
						Map<String, String> updateDic = insertOrUpdateDic.get("update");
						updateDic.put(temp, tempCityCode);
						insertOrUpdateDic.put("update", updateDic);
					} else {
						Map<String, String> tempUpdateDic = new HashMap<String, String>();
						tempUpdateDic.put(temp, tempCityCode);
						insertOrUpdateDic.put("update", tempUpdateDic);
					}
				} else {// 客户问题不存在，直接insert
					if (insertOrUpdateDic.containsKey("insert")) {
						Map<String, String> insertDic = insertOrUpdateDic.get("insert");
						insertDic.put(temp, cityCode);
						insertOrUpdateDic.put("insert", insertDic);
					} else {
						Map<String, String> tempInsertDic = new HashMap<String, String>();
						tempInsertDic.put(temp, cityCode);
						insertOrUpdateDic.put("insert", tempInsertDic);
					}
				}
			}
		}

		return insertOrUpdateDic;

	}

	/**
	 * @description 获取客户问题字典
	 * @param normalQueryId
	 * @return
	 * @returnType Map<String,String>
	 */
	public static Map<String, String> getCustomerQueryDic(String normalQueryId, int querytype) {
		Map<String, String> map = new HashMap<String, String>();
		String sql = "select * from querymanage where kbdataid = " + normalQueryId;
		sql += " and querytype=" + querytype;
		Result rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			if (rs != null && rs.getRowCount() > 0) {
				// 循环遍历数据源
				for (int i = 0; i < rs.getRowCount(); i++) {
					String query = rs.getRows()[i].get("query") == null ? "" : rs.getRows()[i].get("query").toString();
					String city = rs.getRows()[i].get("city") == null ? "" : rs.getRows()[i].get("city").toString();
					map.put(query, city);
				}
			}
		}
		return map;
	}

	/**
	 * @description 获取标准问题字典
	 * @param serviceid
	 * @return
	 * @returnType Map<String,Map<String,String>>
	 */
	public static Map<String, Map<String, String>> getNormalQueryDic(String serviceid) {
		Map<String, Map<String, String>> map = new HashMap<String, Map<String, String>>();
		Map<String, String> tempMap = new HashMap<String, String>();
		String sql = "select * from kbdata where serviceid = " + serviceid;
		Result rs = Database.executeQuery(sql);

		// 文件日志
		GlobalValue.myLog.info(sql);

		if (rs != null && rs.getRowCount() > 0) {
			if (rs != null && rs.getRowCount() > 0) {
				// 循环遍历数据源
				for (int i = 0; i < rs.getRowCount(); i++) {
					String abs = rs.getRows()[i].get("abstract").toString();
					String kbdataid = rs.getRows()[i].get("kbdataid").toString();
					String city = rs.getRows()[i].get("city") != null ? rs.getRows()[i].get("city").toString() : "";
					tempMap = new HashMap<String, String>();
					tempMap.put("abstract", abs);
					tempMap.put("kbdataid", kbdataid);
					tempMap.put("city", city);
					map.put(abs.split(">")[1], tempMap);
				}
			}
		}
		return map;
	}

	/**
	 * @description 获取问题记录数
	 * @param serviceid
	 * @param normalQuery
	 * @param customerQuery
	 * @param cityCode
	 * @param responseType
	 * @param interactType
	 * @return
	 * @returnType int
	 */
	public static int getQueryCount(String serviceid, String normalQuery, String customerQuery, String cityCode,
			String responseType, String interactType) {
		int count = -1;
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "select count(*) count  from (select * from service where serviceid=? ) s  "
				+ " inner join (select * from kbdata where abstract like ? ";
		lstpara.add(serviceid);
		lstpara.add("%" + normalQuery + "%");
		if ("".equals(responseType) || responseType == null) {
			sql = sql + " and (responsetype like '%%' or responsetype is null ) ";
		} else {
			sql = sql + " and (responsetype like ? ) ";
			lstpara.add("%" + responseType + "%");
		}

		if ("".equals(interactType) || interactType == null) {
			sql = sql + " and (interactType like '%%' or interactType is null ) ";
		} else {
			sql = sql + " and (interactType like ? ) ";
			lstpara.add("%" + interactType + "%");
		}
		if (!"".equals(cityCode) && cityCode != null) {
			if (cityCode.endsWith("0000")) {// 地市为省级加载省级下面的所有内容
				cityCode = cityCode.replace("0000", "");
			}
			lstpara.add("%" + cityCode + "%");
		} else {
			lstpara.add("%%");
		}
		lstpara.add("%" + customerQuery + "%");
		sql = sql
				+ "  ) k on s.serviceid = k.serviceid  left join (select * from querymanage where city like ? and query like ? ) q  on k.kbdataid = q.kbdataid";
		rs = Database.executeQuery(sql, lstpara.toArray());
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		if (rs != null && rs.getRowCount() > 0) {
			count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		}
		return count;
	}

	/**
	 * @description 获取标准问题下客户问题记录数
	 * @param serviceid
	 * @param kbdataid
	 * @param customerQuery
	 * @param cityCode
	 * @return
	 * @returnType int
	 */
	public static int getCustomerQueryCount(String serviceid, String kbdataid, String customerQuery, String cityCode,
			String isTrain, String understandStatus) {
		int count = -1;
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "select count(*) count  from (select * from service where serviceid=? ) s  "
				+ " inner join (select * from kbdata where 1>0  ";
		lstpara.add(serviceid);
		if (!"".equals(kbdataid) && kbdataid != null) {
			sql = sql + "  and ( kbdataid = ? ) ";
			lstpara.add(kbdataid);
		}

		if (!"".equals(cityCode) && cityCode != null) {
			if ("全国".equals(cityCode)) {
				cityCode = "";
			} else if (cityCode.endsWith("0000")) {// 地市为省级加载省级下面的所有内容
				cityCode = cityCode.replace("0000", "");
			}
			lstpara.add("%" + cityCode + "%");
		} else {
			lstpara.add("%%");
		}
		lstpara.add("%" + customerQuery + "%");

		String statusSql, istrainSql;

		if (StringUtils.isBlank(understandStatus)) { // 全部
			statusSql = "";
		} else if (understandStatus.equals("none")) { // 未理解
			statusSql = " and status is null";
		} else {
			lstpara.add(understandStatus);
			statusSql = " and status = ?";
		}
		if (StringUtils.isBlank(isTrain)) { // 全部
			istrainSql = "";
		} else {
			lstpara.add(StringUtils.trim(isTrain));
			istrainSql = " and istrain = ?";
		}

		sql = sql
				+ "  ) k on s.serviceid = k.serviceid  inner join (select * from querymanage where querytype=0 and city like ? and query like ? "
				+ statusSql + istrainSql + ") q  on k.kbdataid = q.kbdataid";
		rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		if (rs != null && rs.getRowCount() > 0) {
			count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		}
		return count;
	}

	/**
	 * @description 获取标准问题记录数
	 * @param serviceid
	 * @param normalQuery
	 * @param responseType
	 * @param interactType
	 * @return
	 * @returnType int
	 */
	public static int getNormalQueryCount(String serviceid, String normalQuery, String responseType,
			String interactType) {
		int count = -1;
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "select count(*) count  from (select * from service where serviceid=? ) s  "
				+ " inner join (select * from kbdata where abstract like ? ";
		lstpara.add(serviceid);
		lstpara.add("%" + normalQuery + "%");
		if ("".equals(responseType) || responseType == null) {
			sql = sql + " and (responsetype like '%%' or responsetype is null ) ";
		} else {
			if ("未知".equals(responseType)) {
				sql = sql + " and (responsetype like ?   or responsetype is null ) ";
				lstpara.add("%" + responseType + "%");
			} else {
				sql = sql + " and (responsetype like ?  ) ";
				lstpara.add("%" + responseType + "%");
			}
		}

		if ("".equals(interactType) || interactType == null) {
			sql = sql + " and (interactType like '%%' or interactType is null ) ";
		} else {
			// 0和未交互是默认值
			if ("0".equals(interactType) || "未交互".equals(interactType)) {
				sql = sql + " and (interactType like ?  or interactType is null  ) ";
				lstpara.add("%" + interactType + "%");
			} else {
				sql = sql + " and (interactType like ?  ) ";
				lstpara.add("%" + interactType + "%");
			}

		}
		sql = sql + "  ) k on s.serviceid = k.serviceid ";
		rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		if (rs != null && rs.getRowCount() > 0) {
			count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		}
		return count;
	}

	/**
	 * @description 获取标准问题记录数
	 * @param serviceid
	 * @param normalQuery
	 * @param responseType
	 * @param interactType
	 * @return
	 * @returnType int
	 */
	public static int getNormalQueryCount_New(String serviceid, String normalQuery, String responseType,
			String interactType) {
		int count = -1;
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "select count(*) as count  from kbdata " + " where serviceid = ? and abstract like ? ";
		lstpara.add(serviceid);
		lstpara.add("%" + normalQuery + "%");
		if ("".equals(responseType) || responseType == null) {
			sql = sql + " and (responsetype like '%%' or responsetype is null ) ";
		} else {
			if ("未知".equals(responseType)) {
				sql = sql + " and (responsetype like ?   or responsetype is null ) ";
				lstpara.add("%" + responseType + "%");
			} else {
				sql = sql + " and (responsetype like ?  ) ";
				lstpara.add("%" + responseType + "%");
			}
		}

		if ("".equals(interactType) || interactType == null) {
			sql = sql + " and (interactType like '%%' or interactType is null ) ";
		} else {
			// 0和未交互是默认值
			if ("0".equals(interactType) || "未交互".equals(interactType)) {
				sql = sql + " and (interactType like ?  or interactType is null  ) ";
				lstpara.add("%" + interactType + "%");
			} else {
				sql = sql + " and (interactType like ?  ) ";
				lstpara.add("%" + interactType + "%");
			}

		}

		rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		if (rs != null && rs.getRowCount() > 0) {
			count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		}
		return count;
	}

	/**
	 * @description 获取问题详情
	 * @param serviceid
	 * @param normalQuery
	 * @param responseType
	 * @param interactType
	 * @param page
	 * @param rows
	 * @return
	 * @returnType Result
	 */
	public static Result selectNormalQuery(String serviceid, String normalQuery, String responseType,
			String interactType, int page, int rows) {
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = " select * from (select t.*,rownum rn from ( "
				// + " select * from (select
				// s.service,s.brand,k.kbdataid,(select count(*) from wordpat
				// where kbdataid=k.kbdataid and wordpattype!=5 and wordpat not
				// like '%编者=\"问题库\"%' ) wordpatcount,"
				+ " select * from (select s.service,s.brand,k.kbdataid,(select count(*) from wordpat where kbdataid=k.kbdataid and wordpattype!=5 ) wordpatcount,"
				+ "(select count(*) from relatequery where kbdataid=k.kbdataid ) relatequerycount,"
				+ "(select count(*) from kbansvaliddate where kbdataid=k.kbdataid ) answercount ,"
				+ "(select count(*) from serviceorproductinfo where attr6 =to_char(k.kbdataid) and abstractid is not null ) extendcount ,"
				+ " k.abstract,k.city abscity,k.responsetype,k.interacttype,k.topic  from (select * from service where serviceid=? ) s  "
				+ " inner join(select * from kbdata where abstract like ? ";

		lstpara.add(serviceid);
		lstpara.add("%" + normalQuery + "%");
		if ("".equals(responseType) || responseType == null) {
			sql = sql + " and (responsetype like '%%' or responsetype is null ) ";
		} else {
			if ("未知".equals(responseType)) {
				sql = sql + " and (responsetype like ?   or responsetype is null ) ";
				lstpara.add("%" + responseType + "%");
			} else {
				sql = sql + " and (responsetype like ?  ) ";
				lstpara.add("%" + responseType + "%");
			}

		}

		if ("".equals(interactType) || interactType == null) {
			sql = sql + " and (interactType like '%%' or interactType is null ) ";
		} else {
			// 0和未交互是默认值
			if ("0".equals(interactType) || "未交互".equals(interactType)) {
				sql = sql + " and (interactType like ?  or interactType is null  ) ";
				lstpara.add("%" + interactType + "%");
			} else {
				sql = sql + " and (interactType like ?  ) ";
				lstpara.add("%" + interactType + "%");
			}
		}

		lstpara.add(page * rows);
		lstpara.add((page - 1) * rows);

		sql = sql
				+ "  ) k on s.serviceid = k.serviceid ) aa order by aa.kbdataid desc   )t  where rownum<= ? ) t1 where t1.rn>?";

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return Database.executeQuery(sql, lstpara.toArray());
	}

	/**
	 * @description 获取问题详情
	 * @param serviceid
	 * @param normalQuery
	 * @param responseType
	 * @param interactType
	 * @param page
	 * @param rows
	 * @return
	 * @returnType Result
	 */
	public static Result selectNormalQuery_New(String serviceid, String normalQuery, String responseType,
			String interactType, int page, int rows) {
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = " select * from (select t.*,rownum rn from ( "
				// + " select * from (select
				// s.service,s.brand,k.kbdataid,(select count(*) from wordpat
				// where kbdataid=k.kbdataid and wordpattype!=5 and wordpat not
				// like '%编者=\"问题库\"%' ) wordpatcount,"
				+ "select  kbdataid ,abstract,city abscity,responsetype,interacttype,topic  "
				+ " from kbdata where serviceid = ? and abstract like ? ";

		lstpara.add(serviceid);
		lstpara.add("%" + normalQuery + "%");
		if ("".equals(responseType) || responseType == null) {
			sql = sql + " and (responsetype like '%%' or responsetype is null ) ";
		} else {
			if ("未知".equals(responseType)) {
				sql = sql + " and (responsetype like ?   or responsetype is null ) ";
				lstpara.add("%" + responseType + "%");
			} else {
				sql = sql + " and (responsetype like ?  ) ";
				lstpara.add("%" + responseType + "%");
			}

		}

		if ("".equals(interactType) || interactType == null) {
			sql = sql + " and (interactType like '%%' or interactType is null ) ";
		} else {
			// 0和未交互是默认值
			if ("0".equals(interactType) || "未交互".equals(interactType)) {
				sql = sql + " and (interactType like ?  or interactType is null  ) ";
				lstpara.add("%" + interactType + "%");
			} else {
				sql = sql + " and (interactType like ?  ) ";
				lstpara.add("%" + interactType + "%");
			}
		}

		lstpara.add(page * rows);
		lstpara.add((page - 1) * rows);

		sql = sql + "  order by kbdataid desc   )t  where rownum<= ? ) t1 where t1.rn>?";

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return Database.executeQuery(sql, lstpara.toArray());
	}

	/**
	 * @description 获取个性化业务问题详情 答案数根据商家进行区分
	 * @param serviceid
	 * @param normalQuery
	 * @param responseType
	 * @param interactType
	 * @param page
	 * @param rows
	 * @return
	 * @returnType Result
	 */
	public static Result selectNormalQueryByIoa(String serviceid, String ioa, String normalQuery, String responseType,
			String interactType, int page, int rows) {
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = " select * from (select t.*,rownum rn from ( "
				// + " select * from (select
				// s.service,s.brand,k.kbdataid,(select count(*) from wordpat
				// where kbdataid=k.kbdataid and wordpattype!=5 and wordpat not
				// like '%编者=\"问题库\"%' ) wordpatcount,"
				+ " select * from (select s.service,s.brand,k.kbdataid,(select count(*) from wordpat where kbdataid=k.kbdataid and wordpattype!=5 ) wordpatcount,"
				+ "(select count(*) from relatequery where kbdataid=k.kbdataid ) relatequerycount,"
				// 下面的SQL语句是根据摘要id查询答案数
				+ "(select count(*) from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g "
				+ " where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid "
				+ " and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid "
				+ " and f.servicetype like ?  and b.kbdataid in(k.kbdataid)) answercount,"

		// + "(select count(*) from kbansvaliddate where kbdataid=k.kbdataid )
		// answercount ,"
				+ "(select count(*) from serviceorproductinfo where attr6 =to_char(k.kbdataid) and abstractid is not null ) extendcount ,"
				+ " k.abstract,k.city abscity,k.responsetype,k.interacttype,k.topic  from (select * from service where serviceid=? ) s  "
				+ " inner join(select * from kbdata where abstract like ? ";

		lstpara.add(ioa);
		lstpara.add(serviceid);
		lstpara.add("%" + normalQuery + "%");
		if ("".equals(responseType) || responseType == null) {
			sql = sql + " and (responsetype like '%%' or responsetype is null ) ";
		} else {
			if ("未知".equals(responseType)) {
				sql = sql + " and (responsetype like ?   or responsetype is null ) ";
				lstpara.add("%" + responseType + "%");
			} else {
				sql = sql + " and (responsetype like ?  ) ";
				lstpara.add("%" + responseType + "%");
			}

		}

		if ("".equals(interactType) || interactType == null) {
			sql = sql + " and (interactType like '%%' or interactType is null ) ";
		} else {
			// 0和未交互是默认值
			if ("0".equals(interactType) || "未交互".equals(interactType)) {
				sql = sql + " and (interactType like ?  or interactType is null  ) ";
				lstpara.add("%" + interactType + "%");
			} else {
				sql = sql + " and (interactType like ?  ) ";
				lstpara.add("%" + interactType + "%");
			}
		}

		lstpara.add(page * rows);
		lstpara.add((page - 1) * rows);

		sql = sql
				+ "  ) k on s.serviceid = k.serviceid ) aa order by aa.kbdataid desc   )t  where rownum<= ? ) t1 where t1.rn>?";

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return Database.executeQuery(sql, lstpara.toArray());
	}

	/**
	 * @description 获取标准问题下客户问题详情
	 * @param serviceid
	 * @param kbdataid
	 * @param customerQuery
	 * @param cityCode
	 * @param page
	 * @param rows
	 * @return
	 * @returnType Result
	 */
	public static Result selectCustomerQuery(String serviceid, String kbdataid, String customerQuery, String cityCode,
			String isTrain, String understandStatus, int page, int rows) {
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		// String sql = " select * from (select t.*,rownum rn from ( "
		// + " select * from (select s.service,s.brand,k.kbdataid,(select
		// count(*) from wordpat where kbdataid=k.kbdataid )
		// wordpatcount,(select count(*) from relatequery where
		// kbdataid=k.kbdataid ) relatequerycount,(select count(*) from
		// kbansvaliddate where kbdataid=k.kbdataid ) answercount
		// ,k.abstract,k.city abscity,k.responsetype,k.interacttype,k.topic,
		// q.query,q.city ,q.id ,q.status ,q.result, q.istrain from (select *
		// from service where serviceid=? ) s "
		// + " inner join(select * from kbdata where 1>0 ";

		String sql = " select * from (select t.*,rownum rn from ( "
				+ " select * from (select s.service,s.brand,k.kbdataid,k.abstract,k.city abscity,k.responsetype,k.interacttype,k.topic, q.query,q.city ,q.id ,q.status ,q.result, q.istrain from (select * from service where serviceid=? ) s  "
				+ " inner join(select * from kbdata where  1>0  ";

		lstpara.add(serviceid);
		if (!"".equals(kbdataid) && kbdataid != null) {
			sql = sql + "  and ( kbdataid = ? ) ";
			lstpara.add(kbdataid);
		}

		if (!"".equals(cityCode) && cityCode != null) {
			if ("全国".equals(cityCode)) {
				cityCode = "";
			} else if (cityCode.endsWith("0000")) {// 地市为省级加载省级下面的所有内容
				cityCode = cityCode.replace("0000", "");
			}
			lstpara.add("%" + cityCode + "%");
		} else {
			lstpara.add("%%");
		}
		lstpara.add("%" + customerQuery + "%");

		String statusSql, istrainSql;
		if (StringUtils.isBlank(understandStatus)) { // 全部
			statusSql = "";
		} else if (understandStatus.equals("none")) { // 未理解
			statusSql = " and status is null";
		} else {
			lstpara.add(understandStatus);
			statusSql = " and status = ?";
		}
		if (StringUtils.isBlank(isTrain)) {
			istrainSql = "";
		} else {
			lstpara.add(StringUtils.trim(isTrain));
			istrainSql = " and istrain = ?";
		}

		lstpara.add(page * rows);
		lstpara.add((page - 1) * rows);

		sql = sql
				+ "  ) k on s.serviceid = k.serviceid  inner join (select * from querymanage where (querytype = 0 or querytype is null) and city like ? and query like ? "
				+ statusSql + istrainSql
				+ ") q  on k.kbdataid = q.kbdataid) aa order by aa.id desc   )t  where rownum<= ? ) t1 where t1.rn>?";

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return Database.executeQuery(sql, lstpara.toArray());
	}

	/**
	 * @description 获取问题详情
	 * @param serviceid
	 * @param normalQuery
	 * @param customerQuery
	 * @param cityCode
	 * @param responseType
	 * @param interactType
	 * @param page
	 * @param rows
	 * @return
	 * @returnType Result
	 */
	public static Result selectQuery(String serviceid, String normalQuery, String customerQuery, String cityCode,
			String responseType, String interactType, int page, int rows) {
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = " select * from (select t.*,rownum rn from ( "
				+ " select * from (select s.service,s.brand,k.kbdataid,(select count(*) from wordpat where kbdataid=k.kbdataid and wordpattype !=5 ) wordpatcount,(select count(*) from relatequery where kbdataid=k.kbdataid ) relatequerycount,(select count(*) from kbansvaliddate where kbdataid=k.kbdataid ) answercount ,k.abstract,k.city abscity,k.responsetype,k.interacttype,k.topic, q.query,q.city ,q.id from (select * from service where serviceid=? ) s  "
				+ " inner join(select * from kbdata where abstract like ? ";

		lstpara.add(serviceid);
		lstpara.add("%" + normalQuery + "%");
		if ("".equals(responseType) || responseType == null) {
			sql = sql + " and (responsetype like '%%' or responsetype is null ) ";
		} else {
			sql = sql + " and (responsetype like ? ) ";
			lstpara.add("%" + responseType + "%");
		}

		if ("".equals(interactType) || interactType == null) {
			sql = sql + " and (interactType like '%%' or interactType is null ) ";
		} else {
			sql = sql + " and (interactType like ? ) ";
			lstpara.add("%" + interactType + "%");
		}

		if (!"".equals(cityCode) && cityCode != null) {
			if (cityCode.endsWith("0000")) {// 地市为省级加载省级下面的所有内容
				cityCode = cityCode.replace("0000", "");
			}
			lstpara.add("%" + cityCode + "%");
		} else {
			lstpara.add("%%");
		}
		lstpara.add("%" + customerQuery + "%");
		lstpara.add(page * rows);
		lstpara.add((page - 1) * rows);

		sql = sql
				+ "  ) k on s.serviceid = k.serviceid  left join (select * from querymanage where city like ? and query like ? ) q  on k.kbdataid = q.kbdataid) aa order by aa.id desc   )t  where rownum<= ? ) t1 where t1.rn>?";

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return Database.executeQuery(sql, lstpara.toArray());
	}

	/**
	 * @description 获得相关问记录数
	 * @param kbdataid
	 * @param relatequery
	 * @return
	 * @returnType int
	 */
	public static int getRelateQueryCount(String kbdataid, String relatequery) {
		int count = -1;
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "select count(*) count  from  relatequery where kbdataid=? and relatequery like ?  ";
		lstpara.add(kbdataid);
		lstpara.add("%" + relatequery + "%");
		rs = Database.executeQuery(sql, lstpara.toArray());
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		if (rs != null && rs.getRowCount() > 0) {
			count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		}
		return count;
	}

	/**
	 * @description 获取问题详情
	 * @param serviceid
	 * @param normalQuery
	 * @param customerQuery
	 * @param cityCode
	 * @param responseType
	 * @param interactType
	 * @param page
	 * @param rows
	 * @return
	 * @returnType Result
	 */
	public static Result selectRelateQuery(String kbdataid, String relatequery, int page, int rows) {
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = " select * from (select t.*,rownum rn from ( select id,kbdataid,relatequerytokbdataid,relatequery,workerid,edittime,remark, (select s.service from service s ,kbdata k where s.serviceid= k.serviceid and  k.kbdataid = aa.relatequerytokbdataid ) service,(select abstract from kbdata where kbdataid = aa.relatequerytokbdataid ) abs from relatequery  aa where kbdataid =?  and relatequery like ? order by id desc  )t  where rownum<= ? ) t1 where t1.rn>=?";
		lstpara.add(kbdataid);
		lstpara.add("%" + relatequery + "%");
		lstpara.add(page * rows);
		lstpara.add((page - 1) * rows);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return Database.executeQuery(sql, lstpara.toArray());
	}

	/**
	 * @description 插入问题库自学习词模
	 * @param list
	 * @param serviceType
	 * @param userid
	 * @return
	 * @returnType int
	 */
	public static int insertWordpat(List<List<String>> list, String serviceType, String userid, String wordpattype) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		String brand = serviceType.split("->")[1];
		for (int i = 0; i < list.size(); i++) {
			List<String> tempList = list.get(i);
			String wordpat = tempList.get(0);
			String cityCode = tempList.get(1);
			String query = tempList.get(2);
			String kbdataid = tempList.get(3);
			String queryid = tempList.get(4);
			String wordpatid = "";
			sql = "delete from wordpat where wordpat like ?  and wordpattype=? and kbdataid=? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词模like查询的参数，为了增加精准词模,改为动态获取词模后的字符串
			String split = "@2#";
			if (wordpat.contains("@1#")) {
				split = "@1#";
			}
			String wordpatcon = wordpat.split(split)[1];
			lstpara.add("%" + split + wordpatcon + "%");
			// // 绑定问题类型参数,0代表普通词模
			// lstpara.add("0");
			// 绑定问题类型参数,5代表自学习词模

			if (wordpattype == null || "".equals(wordpattype)) {
				lstpara.add("5");
			} else {
				lstpara.add(wordpattype);
			}

			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// // 绑定品牌城市
			// lstpara.add(brand);
			// 将删除词模的SQL语句放入SQL语句集合中
			lstSql.add(sql);
			// 将对应的参数集合放入集合中
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(userid + "#" + sql + "#" + lstpara);

			// 获取插入词模的序列
			if (GetConfigValue.isOracle) {
				// 获取词模表的序列值
				wordpatid = ConstructSerialNum.GetOracleNextVal("SEQ_WORDPATTERN_ID") + "";
				// 定义新增模板的SQL语句
				sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime,workerid) values(?,?,?,?,?,?,?,sysdate,?)";
			} else if (GetConfigValue.isMySQL) {
				// 获取词模表的序列值
				wordpatid = ConstructSerialNum.getSerialID("wordpat", "wordpatid") + "";
				// 定义新增模板的SQL语句
				sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime,workerid) values(?,?,?,?,?,?,?,sysdate(),?)";
			}
			// 根据配置信息补充需插入主键ID
			if (!"".equals(bussinessFlag)) {
				wordpatid = wordpatid + "." + bussinessFlag;
			}
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词模id参数
			lstpara.add(wordpatid);
			// 绑定词模参数
			lstpara.add(wordpat);
			// 绑定城市名称参数
			lstpara.add(cityCode);
			// 绑定自动开关参数
			lstpara.add("0");

			// // 绑定词模类型参数,0代表普通词模
			// lstpara.add("0");

			// 绑定问题类型参数,5代表自学习词模
			if (wordpattype == null || "".equals(wordpattype)) {
				lstpara.add("5");
			} else {
				lstpara.add(wordpattype);
			}

			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定品牌参数
			lstpara.add(brand);
			lstpara.add(userid);
			// 将插入词模的SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);

			// 更新扩展问训练状态为'是'
			lstSql.add("update querymanage set istrain='是' where id=? and istrain <> '是'");
			lstLstpara.add(Arrays.asList(queryid));

			// 文件日志
			GlobalValue.myLog.info(userid + "#" + sql + "#" + lstpara);
		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}

	/**
	 * @description 插入普通学习词模
	 * @param list
	 * @param serviceType
	 * @param userid
	 * @return
	 * @returnType int
	 */
	public static int insertOrdinaryWordpat(List<List<String>> list, String serviceType, String userid) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		String brand = serviceType.split("->")[1];
		for (int i = 0; i < list.size(); i++) {
			List<String> tempList = list.get(i);
			String wordpat = tempList.get(0);
			String cityCode = tempList.get(1);
			String query = tempList.get(2);
			String kbdataid = tempList.get(3);
			String queryid = tempList.get(4);
			String wordpatid = "";

			// 获取插入词模的序列
			if (GetConfigValue.isOracle) {
				// 获取词模表的序列值
				wordpatid = ConstructSerialNum.GetOracleNextVal("SEQ_WORDPATTERN_ID") + "";
				// 定义新增模板的SQL语句
				sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime,workerid) values(?,?,?,?,?,?,?,sysdate,?)";
			} else if (GetConfigValue.isMySQL) {
				// 获取词模表的序列值
				wordpatid = ConstructSerialNum.getSerialID("wordpat", "wordpatid") + "";
				// 定义新增模板的SQL语句
				sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime,workerid) values(?,?,?,?,?,?,?,sysdate(),?)";
			}
			// 根据配置信息补充需插入主键ID
			if (!"".equals(bussinessFlag)) {
				wordpatid = wordpatid + "." + bussinessFlag;
			}
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词模id参数
			lstpara.add(wordpatid);
			// 绑定词模参数
			lstpara.add(wordpat);
			// 绑定城市名称参数
			lstpara.add(cityCode);
			// 绑定自动开关参数
			lstpara.add("0");

			// // 绑定词模类型参数,0代表普通词模
			lstpara.add("0");

			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定品牌参数
			lstpara.add(brand);
			lstpara.add(userid);
			// 将插入词模的SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(userid + "#" + sql + "#" + lstpara);
		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}

	/**
	 * @description 插入问题库自学习词模（单条）
	 * @param list
	 * @param serviceType
	 * @param userid
	 * @return
	 * @returnType int
	 */
	public static int insertWordpat2(List<String> list, String serviceType, String userid) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		String brand = serviceType.split("->")[1];

		String wordpat = list.get(0);
		String cityCode = list.get(1);
		String query = list.get(2);
		String kbdataid = list.get(3);
		String wordpatid = "";
		sql = "delete from wordpat where wordpat like ?  and wordpattype=? and kbdataid=? ";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定词模like查询的参数
		lstpara.add("%@2#编者=\"问题库\"&来源=\"" + query.replace("&", "\\and") + "\"%");
		// // 绑定问题类型参数,0代表普通词模
		// lstpara.add("0");

		// 绑定问题类型参数,5代表自学习词模
		lstpara.add("5");

		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// // 绑定品牌城市
		// lstpara.add(brand);
		// 将删除词模的SQL语句放入SQL语句集合中
		lstSql.add(sql);
		// 将对应的参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(userid + "#" + sql + "#" + lstpara);

		// 获取插入词模的序列
		if (GetConfigValue.isOracle) {
			// 获取词模表的序列值
			wordpatid = ConstructSerialNum.GetOracleNextVal("SEQ_WORDPATTERN_ID") + "";
			// 定义新增模板的SQL语句
			sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime,workerid) values(?,?,?,?,?,?,?,sysdate,?)";
		} else if (GetConfigValue.isMySQL) {
			// 获取词模表的序列值
			wordpatid = ConstructSerialNum.getSerialID("wordpat", "wordpatid") + "";
			// 定义新增模板的SQL语句
			sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime,workerid) values(?,?,?,?,?,?,?,sysdate(),?)";
		}
		// 根据配置信息补充需插入主键ID
		if (!"".equals(bussinessFlag)) {
			wordpatid = wordpatid + "." + bussinessFlag;
		}
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定词模id参数
		lstpara.add(wordpatid);
		// 绑定词模参数
		lstpara.add(wordpat);
		// 绑定城市名称参数
		lstpara.add(cityCode);
		// 绑定自动开关参数
		lstpara.add("0");

		// // 绑定词模类型参数,0代表普通词模
		// lstpara.add("0");

		// 绑定问题类型参数,5代表自学习词模
		lstpara.add("5");

		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定品牌参数
		lstpara.add(brand);
		lstpara.add(userid);
		// 将插入词模的SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(userid + "#" + sql + "#" + lstpara);

		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}

	/**
	 * @description 更新问题
	 * @param service
	 * @param normalquery
	 * @param oldnormalquery
	 * @param responsetype
	 * @param interacttype
	 * @param kbdataid
	 * @param queryid
	 * @param oldCustomerQuery
	 * @param customerQuery
	 * @param wordpat
	 * @param cityCode
	 * @param serviceType
	 * @return
	 */
	public static int _updateQuery(String userCityCode, String service, String normalquery, String normalqueryWordpat,
			String oldnormalquery, String responsetype, String interacttype, String kbdataid, String queryid,
			String oldCustomerQuery, String customerQuery, String customerQueryWordpat, String cityCode,
			String serviceType, User user) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		String wordpatid = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		String brand = serviceType.split("->")[1];

		if (!"".equals(queryid) && queryid != null) {// 修改客户问题
			if (!"".equals(customerQueryWordpat) && customerQueryWordpat != null) {

				// 修改客户问题
				sql = "update querymanage set query=?, city=?  where id =? ";
				lstpara = new ArrayList<Object>();
				lstpara.add(customerQuery);
				lstpara.add(cityCode);
				lstpara.add(queryid);
				lstSql.add(sql);
				lstLstpara.add(lstpara);

				// 文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

				// 日志 insert into
				// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
				lstSql.add(getInsertLogSql());
				lstLstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), service, "修改客户问题", " ", user.getUserID(),
						user.getUserName(), customerQuery, "QUERYMANAGE"));

				sql = "delete from wordpat where wordpat like ?  and wordpattype=? and kbdataid=? and brand=?";
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定词模like查询的参数
				lstpara.add("%@2#编者=\"问题库\"&来源=\"" + oldCustomerQuery.replace("&", "\\and") + "\"%");
				// // 绑定问题类型参数,0代表普通词模
				// lstpara.add("0");

				// 绑定问题类型参数,5代表自学习词模
				lstpara.add("5");

				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定品牌城市
				lstpara.add(brand);
				// 将删除词模的SQL语句放入SQL语句集合中
				lstSql.add(sql);
				// 将对应的参数集合放入集合中
				lstLstpara.add(lstpara);

				// 文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

				// 获取插入词模的序列
				if (GetConfigValue.isOracle) {
					// 获取词模表的序列值
					wordpatid = (ConstructSerialNum.GetOracleNextVal("SEQ_WORDPATTERN_ID")) + "";
					// 定义新增模板的SQL语句
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate)";
				} else if (GetConfigValue.isMySQL) {
					// 获取词模表的序列值
					wordpatid = ConstructSerialNum.getSerialID("wordpat", "wordpatid") + "";
					// 定义新增模板的SQL语句
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate())";
				}
				// 根据配置信息补充需插入主键ID
				if (!"".equals(bussinessFlag)) {
					wordpatid = wordpatid + "." + bussinessFlag;
				}
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定词模id参数
				lstpara.add(wordpatid);
				// 绑定词模参数
				lstpara.add(customerQueryWordpat);
				// 绑定城市名称参数
				lstpara.add(cityCode.replace(",", "|"));
				// 绑定自动开关参数
				lstpara.add("0");
				// // 绑定词模类型参数,0代表普通词模
				// lstpara.add("0");

				// 绑定问题类型参数,5代表自学习词模
				lstpara.add("5");

				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定品牌参数
				lstpara.add(brand);
				// 将插入词模的SQL语句放入集合中
				lstSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);

				// 更新扩展问训练状态为'是'
				lstSql.add("update querymanage set istrain='是' where id=? and istrain <> '是'");
				lstLstpara.add(Arrays.asList(queryid));

				// 文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

			}

		} else {

			String abs = "<" + service + ">" + normalquery;
			// 修改摘要
			sql = "update kbdata set abstract=?,responsetype=?, interacttype=? where kbdataid =? ";
			lstpara = new ArrayList<Object>();
			lstpara.add(abs);
			lstpara.add(responsetype);
			lstpara.add(interacttype);
			lstpara.add(kbdataid);
			lstSql.add(sql);
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

			// 日志 insert into
			// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
			lstSql.add(getInsertLogSql());
			lstLstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), service, "修改标准问题", " ", user.getUserID(),
					user.getUserName(), normalquery, "KBDATA"));

			if (!"".equals(normalqueryWordpat) && normalqueryWordpat != null) {
				// 删除标准问对应词模

				sql = "delete from wordpat where wordpat like ?  and wordpattype=? and kbdataid=? and brand=?";
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定词模like查询的参数
				lstpara.add("%@2#编者=\"问题库\"&来源=\"" + oldnormalquery.replace("&", "\\and") + "\"%");
				// // 绑定问题类型参数,0代表普通词模
				// lstpara.add("0");

				// 绑定问题类型参数,5代表自学习词模
				lstpara.add("5");

				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定品牌城市
				lstpara.add(brand);
				// 将删除词模的SQL语句放入SQL语句集合中
				lstSql.add(sql);
				// 将对应的参数集合放入集合中
				lstLstpara.add(lstpara);

				// 文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

				// 获取插入词模的序列
				if (GetConfigValue.isOracle) {
					// 获取词模表的序列值
					wordpatid = (ConstructSerialNum.GetOracleNextVal("SEQ_WORDPATTERN_ID")) + "";
					// 定义新增模板的SQL语句
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate)";
				} else if (GetConfigValue.isMySQL) {
					// 获取词模表的序列值
					wordpatid = ConstructSerialNum.getSerialID("wordpat", "wordpatid") + "";
					// 定义新增模板的SQL语句
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate())";
				}
				// 根据配置信息补充需插入主键ID
				if (!"".equals(bussinessFlag)) {
					wordpatid = wordpatid + "." + bussinessFlag;
				}
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定词模id参数
				lstpara.add(wordpatid);
				// 绑定词模参数
				lstpara.add(normalqueryWordpat);
				// 绑定城市名称参数
				lstpara.add(cityCode.replace(",", "|"));
				// 绑定自动开关参数
				lstpara.add("0");
				// // 绑定词模类型参数,0代表普通词模
				// lstpara.add("0");

				// 绑定问题类型参数,5代表自学习词模
				lstpara.add("5");

				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定品牌参数
				lstpara.add(brand);
				// 将插入词模的SQL语句放入集合中
				lstSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);

				// 更新扩展问训练状态为'是'
				lstSql.add("update querymanage set istrain='是' where kbdataid=? and query=? and istrain<>'是'");
				lstLstpara.add(Arrays.asList(kbdataid, normalquery));

				// 文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

			}
		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);

	}

	/**
	 * @description 修改问题库对应词模地市
	 * @param kbdataid
	 * @param citycode
	 * @param query
	 * @return
	 * @returnType int
	 */
	public static int updateWordpatCity(String kbdataid, String citycode, String query) {

		if (citycode == null || "".equals(citycode)) {
			citycode = "全国";
		} else {
			citycode.replace(",", "|");
		}
		String sql = "";
		sql = "update wordpat set city= ? where kbdataid=? and  wordpat like ?";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定city参数
		lstpara.add(citycode);
		lstpara.add(kbdataid);
		lstpara.add("%@2#编者=\"问题库\"&来源=\"" + query.replace("&", "\\and") + "\"%");

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return Database.executeNonQuery(sql, lstpara.toArray());
	}

	/**
	 * @description 删除客户问题
	 * @param list
	 * @return
	 * @returnType int
	 */
	public static int _deleteCustomerQuery(List<List<String>> list, User user) {
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		for (int i = 0; i < list.size(); i++) {
			List<String> tempList = list.get(i);
			String queryid = tempList.get(0);
			String query = tempList.get(1);
			String kbdataid = tempList.get(2);

			// 删除客户问题
			sql = "delete  from querymanage where id=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			lstpara.add(queryid);
			// 将删除词模的SQL语句放入SQL语句集合中
			lstSql.add(sql);
			// 将对应的参数集合放入集合中
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

			// 删除客户问题关联词模
			sql = "delete from wordpat where wordpat like ?  and wordpattype=? and kbdataid=? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词模like查询的参数
			lstpara.add("%@2#编者=\"问题库\"&来源=\"" + query.replace("&", "\\and") + "\"%");
			// // 绑定问题类型参数,0代表普通词模
			// lstpara.add("0");

			// 绑定问题类型参数,5代表自学习词模
			lstpara.add("5");

			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 将删除词模的SQL语句放入SQL语句集合中
			lstSql.add(sql);
			// 将对应的参数集合放入集合中
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

			// 日志 insert into
			// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
			lstSql.add(getInsertLogSql());
			lstLstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), getServiceByKbdataid(kbdataid)[0], "删除客户问题",
					" ", user.getUserID(), user.getUserName(), getCustomerqueryByID(queryid), "QUERYMANAGE"));

		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);

	}

	/**
	 * @description 删除标准问题
	 * @param list
	 * @return
	 * @returnType int
	 */
	public static int _deleteNormalQuery(List<String> list, User user) {
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		for (int i = 0; i < list.size(); i++) {
			String kbdataid = list.get(i);

			// 删除摘要
			sql = "delete from kbdata where kbdataid=?";
			lstpara = new ArrayList<Object>();
			lstpara.add(kbdataid);
			lstSql.add(sql);
			lstLstpara.add(lstpara);
			// 日志 insert into
			// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
			lstSql.add(getInsertLogSql());
			lstLstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), getServiceByKbdataid(kbdataid)[0], "删除标准问题",
					" ", user.getUserID(), user.getUserName(), getNormalqueryByKbdataid(kbdataid), "KBDATA"));

			// 删除客户问题
			sql = "delete from querymanage where kbdataid=?";
			lstpara = new ArrayList<Object>();
			lstpara.add(kbdataid);
			lstSql.add(sql);
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

			// 删除相关问题
			sql = "delete from relatequery where kbdataid=?";
			lstpara = new ArrayList<Object>();
			lstpara.add(kbdataid);
			lstSql.add(sql);
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara);

		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}

	/**
	 * @description 通过摘要ID 获取场景信息
	 * @param kbdataid
	 * @return
	 * @returnType Result
	 */
	public static Result _findScenarios(String kbdataid) {
		// String sql = "select s.scenariosid,s.name from scenarios
		// s,scenarios2kbdata sk where s.scenariosid = sk.scenariosid and
		// sk.abstractid="+kbdataid;
		String sql = "select sk.relationserviceid scenariosid from scenarios2kbdata sk, service s where sk.relationserviceid= s.serviceid and sk.abstractid="
				+ kbdataid;

		// 文件日志
		GlobalValue.myLog.info(sql);

		return Database.executeQuery(sql);
	}

	/**
	 * @description 通过业务ID获取客户问题
	 * @param serviceid
	 * @return
	 * @returnType Result
	 */
	public static Result getQuery(String serviceid, int querytype) {
		String sql = "select k.kbdataid, k.abstract,k.city abscity,q.id,q.query,q.city,q.isstrictexclusion  from (select * from service where serviceid ="
				+ serviceid
				+ " ) s inner join  kbdata k on s.serviceid = k.serviceid left join querymanage q  on k.kbdataid=q.kbdataid and q.querytype="
				+ querytype;

		// 文件日志
		GlobalValue.myLog.info(sql);

		return Database.executeQuery(sql);
	}

	/**
	 * @description 通过业务名称递归（如果存在子业务）获取客户问题
	 * @param service
	 * @return
	 * @returnType Result
	 */
	public static Result getQuery2(String service) {
		// String sql = "select k.kbdataid, k.abstract,k.city
		// abscity,q.query,q.city from (select * from service where serviceid
		// ="+serviceid+" ) s inner join kbdata k on s.serviceid = k.serviceid
		// left join querymanage q on k.kbdataid=q.kbdataid";
		String sql = "select b.service, b.namepath, a.kbdataid, a.abstract, a.city abscity, q.query, q.city from querymanage q, kbdata a, (select service, serviceid, SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) namepath from service start with service = '"
				+ service
				+ "' connect by prior serviceid = parentid) b where q.kbdataid = a.kbdataid and a.serviceid = b.serviceid";
		return Database.executeQuery(sql);
	}

	/**
	 * @description 通过业务ID递归（如果存在子业务）获取客户问题
	 * @param serviceid
	 * @return
	 * @returnType Result
	 */
	public static Result getQuery3(String service) {
		// String sql = "select k.kbdataid, k.abstract,k.city
		// abscity,q.query,q.city from (select * from service where serviceid
		// ="+serviceid+" ) s inner join kbdata k on s.serviceid = k.serviceid
		// left join querymanage q on k.kbdataid=q.kbdataid";
		String sql = "select a.kbdataid, a.abstract, a.city abscity, q.query, q.city, q.id querymanageid from querymanage q, kbdata a, (select * from service start with service = '"
				+ service
				+ "' connect by prior serviceid = parentid) b where q.kbdataid = a.kbdataid and a.serviceid = b.serviceid";

		// 文件日志
		GlobalValue.myLog.info(sql);

		return Database.executeQuery(sql);
	}

	/**
	 * @description 通过业务ID递归（如果存在子业务）获取理解不一致/理解无答案客户问题
	 * @param serviceid
	 * @return
	 * @returnType Result
	 */
	public static Result getQuery4(String service) {
		// String sql = "select k.kbdataid, k.abstract,k.city
		// abscity,q.query,q.city from (select * from service where serviceid
		// ="+serviceid+" ) s inner join kbdata k on s.serviceid = k.serviceid
		// left join querymanage q on k.kbdataid=q.kbdataid";
		String sql = "select a.kbdataid, a.abstract, a.city abscity, q.query, q.city, q.id querymanageid from querymanage q, kbdata a, (select * from service start with service = '"
				+ service
				+ "' connect by prior serviceid = parentid) b where q.kbdataid = a.kbdataid and a.serviceid = b.serviceid and (q.status is null or q.status != 0)";

		// 文件日志
		GlobalValue.myLog.info(sql);

		return Database.executeQuery(sql);
	}

	/**
	 * @description 新增相关问题
	 * @param relatequerytokbdataid
	 * @param relatequery
	 * @param kbdataid
	 * @param workerid
	 * @return
	 * @returnType int
	 */
	public static int _insertRelatequery(String relatequerytokbdataid, String relatequery, String kbdataid,
			String workerid) {
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 删除摘要
		String sql = "insert into relatequery(id,kbdataid,relatequerytokbdataid,relatequery,workerid) values (seq_relatequery_id.nextval,?,?,?,?)";
		lstpara = new ArrayList<Object>();
		lstpara.add(kbdataid);
		lstpara.add(relatequerytokbdataid);
		lstpara.add(relatequery);
		lstpara.add(workerid);
		lstSql.add(sql);
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(workerid + "#" + sql + "#" + lstpara);

		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}

	/**
	 * @description 删除相关问题
	 * @param list
	 * @return
	 * @returnType int
	 */
	public static int _deleteRelateQuery(List<String> list) {
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		for (int i = 0; i < list.size(); i++) {
			String id = list.get(i);
			// 删除相关问题
			sql = "delete from relatequery where id=?";
			lstpara = new ArrayList<Object>();
			lstpara.add(id);
			lstSql.add(sql);
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(sql + "#" + lstpara);

		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);

	}

	/**
	 * @description 迁移相关问
	 * @param serviceid
	 * @param kbdataid
	 * @param abs
	 * @return
	 * @returnType int
	 */
	public static int updateNormalQueryPath(String serviceid, String[] kbdataids, String[] abses) {
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "update kbdata set serviceid =? ,abstract = ? where kbdataid=?";

		if (kbdataids.length != abses.length) {
			return -1;
		}
		for (int i = 0; i < kbdataids.length; i++) {
			lstpara = new ArrayList<Object>();
			lstpara.add(serviceid);
			lstpara.add(abses[i]);
			lstpara.add(kbdataids[i]);
			lstSql.add(sql);
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(sql + "#" + lstpara);
		}

		return Database.executeNonQueryTransaction(lstSql, lstLstpara);

	}

	/**
	 * @description 通过业务ID获取业务地市
	 * @param serviceid
	 * @return
	 * @returnType Result
	 */
	public static Result getServiceCitys(String serviceid, String brand) {
		String sql = " select city from service where serviceid=" + serviceid + " and brand='" + brand + "'";

		// 文件日志
		GlobalValue.myLog.info(sql);

		return Database.executeQuery(sql);
	}

	/**
	 * @description 通过业务ID获取业务地市
	 * @param serviceid
	 * @return
	 * @returnType Result
	 */
	public static Result getServiceCitys(String serviceid) {
		String sql = " select city from service where serviceid=" + serviceid;

		// 文件日志
		GlobalValue.myLog.info(sql);

		return Database.executeQuery(sql);
	}

	/**
	 * @description 导入客户问题
	 * @param info
	 * @param map
	 * @param serviceCityList
	 * @param serviceid
	 * @param bussinessFlag
	 * @param workerid
	 * @return
	 * @returnType int
	 */
	public static int importQuery(Map<ImportNormalqueryBean, Map<String, List<String>>> info,
			Map<String, Map<String, String>> map, List<String> serviceCityList, String serviceid, String bussinessFlag,
			String workerid, int querytype) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		String querymanageId = "";
		// 是否严格排除状态
		String removeQueryStatus = "";
		if (1 == querytype) {// 排除问题，默认严格排除状态为否
			removeQueryStatus = "否";
		}
		// 翻转info
		List<ImportNormalqueryBean> infoKeys = new ArrayList<ImportNormalqueryBean>(info.keySet());
		Collections.reverse(infoKeys);
		// for (Entry<ImportNormalqueryBean, Map<String, List<String>>> entry :
		// info.entrySet()) {
		for (ImportNormalqueryBean normalqueryBean : infoKeys) {
			List<String> cityList = new ArrayList<String>();
			// ImportNormalqueryBean normalqueryBean = entry.getKey();
			String normalquery = normalqueryBean.getNormalquery();
			String responsetype = normalqueryBean.getResponsetype();
			String interacttype = normalqueryBean.getInteracttype();
			Map<String, List<String>> queryAndCity = info.get(normalqueryBean);
			String customerquery = "";
			if (map.containsKey(normalquery)) {// 现有业务下已存在该标准问，标准问不做导入操作,此处不做标准问题city比较，后补充
												// TODO
				Map<String, String> tempMap = map.get(normalquery);
				String kbdataid = tempMap.get("kbdataid");
				String abscity = tempMap.get("abscity");
				// 扩展问地市集合
				String cityListString = "";
				for (Map.Entry<String, List<String>> e : queryAndCity.entrySet()) {
					customerquery = e.getKey();
					cityList = e.getValue();
					String oldcity = tempMap.get(customerquery);
					String oldcityArray[] = {};
					if (!"".equals(oldcity) && oldcity != null) {
						oldcityArray = oldcity.split(",");
					}
					List<String> oldCitylist = new ArrayList<String>(Arrays.asList(oldcityArray));
					if (cityList.size() > 0) {
						cityList.addAll(oldCitylist);
					}

					Set set = new HashSet(cityList);
					List<String> newCityCodelist = new ArrayList<String>(set);
					Collections.sort(newCityCodelist);
					String newCityCode = StringUtils.join(newCityCodelist.toArray(), ",");

					// 要导入的客户问地市集合
					List<String> cityList2 = e.getValue();
					String cityListStr = StringUtils.join(cityList2.toArray(), ",");
					cityListString = unionCityCodes(cityListString, cityListStr);

					if (serviceCityList.size() == 1 && !serviceCityList.get(0).endsWith("0000")
							&& !serviceCityList.contains("全国")) {// 省级以下用户 TODO

					} else {// 省级用户
						if (tempMap.containsKey(customerquery)) {// 标准问下存在客户问，补充客户问地市并做修改
							// 修改客户问题cityid
							sql = "update querymanage set  city=?  where query =? and kbdataid =? and querytype=? ";
							lstpara = new ArrayList<Object>();
							lstpara.add(newCityCode);
							lstpara.add(customerquery);
							lstpara.add(kbdataid);
							lstpara.add(querytype);
							lstSql.add(sql);
							lstLstpara.add(lstpara);

							// 文件日志
							GlobalValue.myLog.info(workerid + "#" + sql + "#" + lstpara);

							// 修改客户问题对应词模
							sql = "update  wordpat set city =? where wordpat like ?  and wordpattype=? and kbdataid=?";
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							lstpara.add(newCityCode.replace(",", "|"));
							// 绑定词模like查询的参数
							lstpara.add("%@2#编者=\"问题库\"&来源=\"" + customerquery.replace("&", "\\and") + "\"%");
							// // 绑定问题类型参数,0代表普通词模
							// lstpara.add("0");

							// 绑定问题类型参数,5代表自学习词模
							lstpara.add("5");

							// 绑定摘要id参数
							lstpara.add(kbdataid);
							// 将删除词模的SQL语句放入SQL语句集合中
							lstSql.add(sql);
							// 将对应的参数集合放入集合中
							lstLstpara.add(lstpara);

							// 文件日志
							GlobalValue.myLog.info(workerid + "#" + sql + "#" + lstpara);

						} else {// 标准问下不存在客户问题，直接insert
							sql = "insert into querymanage(ID,KBDATAID,QUERY,CITY,WORKERID,QUERYTYPE,ISSTRICTEXCLUSION) values(?,?,?,?,?,?,?)";

							if (GetConfigValue.isOracle) {
								querymanageId = ConstructSerialNum.GetOracleNextValNew("seq_querymanage_id",
										bussinessFlag);
							} else if (GetConfigValue.isMySQL) {
								querymanageId = ConstructSerialNum.getSerialIDNew("querymanage", "id", bussinessFlag);
							}
							lstpara = new ArrayList<Object>();
							lstpara.add(querymanageId);
							lstpara.add(kbdataid);
							lstpara.add(customerquery);
							lstpara.add(StringUtils.join(cityList.toArray(), ","));
							lstpara.add(workerid);
							lstpara.add(querytype);
							lstpara.add(removeQueryStatus);
							lstSql.add(sql);
							lstLstpara.add(lstpara);

							// 文件日志
							GlobalValue.myLog.info(workerid + "#" + sql + "#" + lstpara);

						}

					}

				}

				// 合并修改标准问扩展问地市
				String oldabscity = tempMap.get(normalquery);
				String oldabscityArray[] = {};
				List<String> oldabscityList = new ArrayList<String>();
				if (!"".equals(oldabscity) && oldabscity != null) {
					oldabscityArray = oldabscity.split(",");
					oldabscityList = new ArrayList<String>(Arrays.asList(oldabscityArray));
				}
				String cityListStringArray[] = {};
				if (!"".equals(cityListString) && cityListString != null) {
					cityListStringArray = cityListString.split(",");
				}
				List<String> cityListStringList = new ArrayList<String>(Arrays.asList(cityListStringArray));
				if (oldabscityList.size() > 0) {
					oldabscityList.addAll(cityListStringList);
				}

				Set absset = new HashSet(oldabscityList);
				List<String> newabsCityCodelist = new ArrayList<String>(absset);
				Collections.sort(newabsCityCodelist);
				String newabsCityCode = StringUtils.join(newabsCityCodelist.toArray(), ",");

				// 修改标准问扩展问地市
				sql = "update querymanage set  city=?  where query =? and kbdataid =? and querytype=? ";
				lstpara = new ArrayList<Object>();
				lstpara.add(newabsCityCode);
				lstpara.add(normalquery);
				lstpara.add(kbdataid);
				lstpara.add(querytype);
				lstSql.add(sql);
				lstLstpara.add(lstpara);

				// 文件日志
				GlobalValue.myLog.info(workerid + "#" + sql + "#" + lstpara);

			} else {// 现有业务下不存在该标准问直接insert

				String insertKbdataSql = "insert into kbdata(serviceid,kbdataid,topic,abstract,city,responsetype,interacttype) values (?,?,?,?,?,?,?)";
				String kbdataid = "";
				// 新增摘要
				if (GetConfigValue.isOracle) {
					kbdataid = ConstructSerialNum.GetOracleNextValNew("SEQ_KBDATA_ID", bussinessFlag);
				} else if (GetConfigValue.isMySQL) {
					kbdataid = ConstructSerialNum.getSerialIDNew("kbdata", "kbdataid", bussinessFlag);
				}
				String service = CommonLibServiceDAO.getNameByserviceid(serviceid);
				String abs = "<" + service + ">" + normalquery;
				// 修改 START 标准问地市取业务地市 by lixu
				String serviceCityListString = StringUtils.join(serviceCityList.toArray(), ",");
				String userCityListString = "";
				// 修改 END 20170310 by zhaolipeng

				// 插入摘要
				lstpara = new ArrayList<Object>();
				lstpara.add(serviceid);
				lstpara.add(kbdataid);
				lstpara.add("常见问题");
				lstpara.add(abs);
				lstpara.add(serviceCityListString);
				lstpara.add(responsetype);
				lstpara.add(interacttype);
				lstSql.add(insertKbdataSql);
				lstLstpara.add(lstpara);

				// 文件日志
				GlobalValue.myLog.info(workerid + "#" + insertKbdataSql + "#" + lstpara);

				/*
				 * 修改 START 标准问地市取客户问地市并集 20170310 by zhaolipeng
				 * 内容：标准问地市变为根据其下客户问地市的并集 修改下面代码顺序: 摘要插入->标准问作为客户问插入->客户问插入，改为
				 * 客户问插入（获得并集后的地市）->摘要插入->标准问作为客户问插入
				 *
				 * 修改 END 20170310 by zhaolipeng
				 */

				// 存在客户问时插入
				String insertSql = "insert into querymanage(ID,KBDATAID,QUERY,CITY,WORKERID,QUERYTYPE,ISSTRICTEXCLUSION) values(?,?,?,?,?,?,?)";
				for (Map.Entry<String, List<String>> e : queryAndCity.entrySet()) {
					customerquery = e.getKey();

					// 修改 START 防止重复导入标准问 20170310 by zhaolipeng
					// 客户问与其标准一样的话，不做导入（防止重复导入）
					if (customerquery.equals(normalquery)) {
						continue;
					}
					// 修改 END 20170310 by zhaolipeng

					if (!"".equals(customerquery)) {
						cityList = e.getValue();
						String cityListStr = StringUtils.join(cityList.toArray(), ",");
						// 修改 START 标准问地市取客户问地市并集 20170310 by zhaolipeng
						userCityListString = unionCityCodes(userCityListString, cityListStr);
						// 修改 END 20170310 by zhaolipeng
						if (GetConfigValue.isOracle) {
							querymanageId = ConstructSerialNum.GetOracleNextValNew("seq_querymanage_id", bussinessFlag);
						} else if (GetConfigValue.isMySQL) {
							querymanageId = ConstructSerialNum.getSerialIDNew("querymanage", "id", bussinessFlag);
						}

						lstpara = new ArrayList<Object>();
						lstpara.add(querymanageId);
						lstpara.add(kbdataid);
						lstpara.add(customerquery);
						lstpara.add(cityListStr);
						lstpara.add(workerid);
						lstpara.add(querytype);
						lstpara.add(removeQueryStatus);
						lstSql.add(insertSql);
						lstLstpara.add(lstpara);

						// 文件日志
						GlobalValue.myLog.info(workerid + "#" + insertSql + "#" + lstpara);
					}
				}

				// 标准问作为扩展问插入
				if (GetConfigValue.isOracle) {
					querymanageId = ConstructSerialNum.GetOracleNextValNew("seq_querymanage_id", bussinessFlag);
				} else if (GetConfigValue.isMySQL) {
					querymanageId = ConstructSerialNum.getSerialIDNew("querymanage", "id", bussinessFlag);
				}

				lstpara = new ArrayList<Object>();
				lstpara.add(querymanageId);
				lstpara.add(kbdataid);
				lstpara.add(normalquery);
				// 修改 START 客户问地市如果没有的话
				lstpara.add(StringUtils.isEmpty(userCityListString) ? serviceCityListString : userCityListString);
				lstpara.add(workerid);
				lstpara.add(querytype);
				lstpara.add(removeQueryStatus);
				lstSql.add(insertSql);
				lstLstpara.add(lstpara);

				// 文件日志
				GlobalValue.myLog.info(workerid + "#" + insertSql + "#" + lstpara);

			}
		}

		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}

	/**
	 * 导出客户问题|排除问题
	 * 
	 * @param serviceid
	 * @param normalQuery
	 * @param responseType
	 * @param interactType
	 * @return
	 */
	public static Result exportQuery(String serviceid, String normalQuery, String responseType, String interactType,
			int queryType) {
		String sql = "select a.abstract, b.query, a.responsetype, a.interacttype, b.city from kbdata a, querymanage b, service s";
		sql += " where s.serviceid = ?";
		sql += " and a.serviceid = s.serviceid";
		sql += " and a.kbdataid = b.kbdataid(+)";
		sql += " and a.abstract like ?";
		List<String> lstpara = new ArrayList<String>();

		lstpara.add(serviceid);
		lstpara.add("%" + normalQuery + "%");
		if ("".equals(responseType) || responseType == null) {
			sql += " and (a.responsetype like '%%' or a.responsetype is null ) ";
		} else {
			if ("未知".equals(responseType)) {
				sql = sql + " and (a.responsetype like ?   or a.responsetype is null ) ";
				lstpara.add("%" + responseType + "%");
			} else {
				sql = sql + " and (a.responsetype like ?  ) ";
				lstpara.add("%" + responseType + "%");
			}

		}

		if ("".equals(interactType) || interactType == null) {
			sql += " and (a.interactType like '%%' or a.interactType is null ) ";
		} else {
			if ("未交互".equals(interactType)) {
				sql += " and (a.interactType like ?  or a.interactType is null  ) ";
				lstpara.add("%" + interactType + "%");
			} else {
				sql += " and (a.interactType like ?  ) ";
				lstpara.add("%" + interactType + "%");
			}
		}
		sql += " and b.querytype = " + queryType;
		sql += " order by a.kbdataid desc";
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}

	/**
	 * 根据标准问查找
	 * 
	 * @param normalquery
	 *            标准问
	 * @return
	 */
	public static Result findNormalquery(String normalquery, List<Map<String, String>> accessServices) {
		List<String> rootServiceIds = new ArrayList<String>();
		List<String> permisssionServiceIds = new ArrayList<String>();
		for (Map<String, String> resource : accessServices) {
			permisssionServiceIds.add(resource.get("id"));
			if ("0".equals(resource.get("pid"))) {
				rootServiceIds.add(resource.get("id"));
			}
			if ("0.000".equals(resource.get("pid"))) {
				rootServiceIds.add(resource.get("id"));
			}
		}
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT a.abstract, b.*");
		sql.append(" FROM kbdata a,");
		sql.append("   (SELECT service,");
		sql.append("     serviceid,");
		sql.append("     SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) NAME_PATH,");
		sql.append("     SUBSTR(SYS_CONNECT_BY_PATH(serviceid,'->'),3) SERVICEID_PATH");
		sql.append("   FROM service");
		sql.append("     START WITH serviceid         in(" + StringUtils.join(rootServiceIds, ",") + ")");
		sql.append("     CONNECT BY prior serviceid = parentid");
		sql.append("   ) b");
		sql.append(" WHERE a.serviceid  = b.serviceid");
		sql.append(" AND b.serviceid IN (" + StringUtils.join(permisssionServiceIds, ",") + ")");
		sql.append(" AND TRIM(a.abstract) LIKE ?");
		// 文件日志
		GlobalValue.myLog.info(sql.toString() + "#" + "%>" + normalquery);
		return Database.executeQuery(sql.toString(), "%>" + normalquery);
	}

	/**
	 * 根据标准问查找
	 * 
	 * @param normalquery
	 *            标准问
	 * @return
	 */
	public static Result findNormalquery(List<String> normalquery, String serviceType, List<String> roleId,
			List<String> cityList) {
		StringBuilder sql = new StringBuilder();
		sql.append(" select * from kbdata where serviceid in  ");
		sql.append(
				" ( select resourceid from ROLE_RESOURCE where resourcetype =lower('querymanage') and servicetype = ?  ");
		sql.append(" and roleid in ( " + StringUtils.join(roleId.toArray(), ",") + ") )");

		if (normalquery != null && normalquery.size() > 0) {
			for (int j = 0; j < normalquery.size(); j++) {
				if (j == 0) {
					sql.append(" and (  abstract  like '%>" + normalquery.get(j) + "'");
				} else {
					sql.append(" or abstract  like '%>" + normalquery.get(j) + "'");
				}
			}
			sql.append(" ) ");
		}
		if (cityList != null && cityList.size() > 0) {
			for (int j = 0; j < cityList.size(); j++) {
				if (j == 0) {
					sql.append(" and (  city  like '%" + cityList.get(j) + "%'");
				} else {
					sql.append(" or city  like '%" + cityList.get(j) + "%'");
				}
			}
			sql.append(" ) ");
		}

		// 文件日志
		GlobalValue.myLog.info(sql.toString() + "#" + serviceType);
		return Database.executeQuery(sql.toString(), serviceType);
	}

	/**
	 * 根据客户问查找
	 * 
	 * @param customerquery
	 *            客户问
	 * @return
	 */
	public static Result findCustomerquery(String customerquery, List<Map<String, String>> accessServices) {
		List<String> rootServiceIds = new ArrayList<String>();
		List<String> permisssionServiceIds = new ArrayList<String>();
		for (Map<String, String> resource : accessServices) {
			permisssionServiceIds.add(resource.get("id"));
			if ("0".equals(resource.get("pid"))) {
				rootServiceIds.add(resource.get("id"));
			}
			if ("0.000".equals(resource.get("pid"))) {
				rootServiceIds.add(resource.get("id"));
			}
		}
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT q.query, a.abstract, b.*");
		sql.append(" FROM kbdata a,");
		sql.append(" querymanage q,");
		sql.append("   (SELECT service,");
		sql.append("     serviceid,");
		sql.append("     SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) NAME_PATH,");
		sql.append("     SUBSTR(SYS_CONNECT_BY_PATH(serviceid,'->'),3) SERVICEID_PATH");
		sql.append("   FROM service");
		sql.append("     START WITH serviceid         in(" + StringUtils.join(rootServiceIds, ",") + ")");
		sql.append("     CONNECT BY prior serviceid = parentid");
		sql.append("   ) b");
		sql.append(" WHERE a.serviceid  = b.serviceid");
		sql.append(" AND b.serviceid IN (" + StringUtils.join(permisssionServiceIds, ",") + ")");
		sql.append(" ANd a.kbdataid = q.kbdataid");
		sql.append(" AND TRIM(q.query) = ?");

		// 文件日志
		GlobalValue.myLog.info(sql.toString() + "#" + customerquery);

		return Database.executeQuery(sql.toString(), customerquery);
	}

	/**
	 * 根据客户问查找
	 * 
	 * @param customerquery
	 *            客户问
	 * @return
	 */
	public static Result findCustomerquery(List<String> customerquery, String serviceType, List<String> roleId,
			List<String> cityList, int querytype) {
		StringBuilder sql = new StringBuilder();
		sql.append(
				" select k.serviceid,k.abstract ,q.query,q.id queryid,k.kbdataid from kbdata k,querymanage q where k.serviceid in  ");
		sql.append(
				" ( select resourceid from ROLE_RESOURCE where resourcetype =lower('querymanage') and servicetype = ? ");
		sql.append(" and roleid in(" + StringUtils.join(roleId.toArray(), ",") + ") )");
		sql.append("and k.kbdataid = q.kbdataid ");

		if (customerquery != null && customerquery.size() > 0) {
			for (int j = 0; j < customerquery.size(); j++) {
				if (j == 0) {
					sql.append(" and (  q.query  = '" + customerquery.get(j) + "'");
				} else {
					sql.append(" or q.query  = '" + customerquery.get(j) + "'");
				}
			}
			sql.append(" ) ");
		}
		sql.append(" and  q.querytype  = " + querytype + " ");
		if (cityList != null && cityList.size() > 0) {
			for (int j = 0; j < cityList.size(); j++) {
				if (j == 0) {
					sql.append(" and (  k.city  like '%" + cityList.get(j) + "%'");
				} else {
					sql.append(" or k.city  like '%" + cityList.get(j) + "%'");
				}
			}
			sql.append(" ) ");
		}

		// 文件日志
		GlobalValue.myLog.info(sql.toString() + "#" + serviceType);
		return Database.executeQuery(sql.toString(), serviceType);
	}

	/**
	 * 添加业务
	 * 
	 * @param parentId
	 * @param serviceName
	 * @param brand
	 * @return
	 */
	public static String insertService(String parentId, String serviceName, String brand, String bussinessFlag,
			User user) {
		String sql = "insert into service(serviceid, service, parentid, parentname, brand, cityid,city) values(?,?,?,?,?,?,?)";
		List<String> sqls = new ArrayList<String>();
		List<List<?>> lstParam = new ArrayList<List<?>>();

		// 文件日志
		GlobalValue.myLog.info(user.getUserID() + "#"
				+ "select serviceid, service, cityid,city from service where serviceid=?" + "#" + parentId);

		Result rs = Database.executeQuery("select serviceid, service, cityid,city from service where serviceid=?",
				parentId);
		if (rs != null && rs.getRowCount() > 0) {
			Object parentName = rs.getRows()[0].get("service");
			Object cityId = rs.getRows()[0].get("cityid");
			Object city = rs.getRows()[0].get("city");
			if (parentName != null) {
				String servicdId = ConstructSerialNum.GetOracleNextValNew("SEQ_SERVICE_ID", bussinessFlag);

				sqls.add(sql);
				lstParam.add(Arrays.asList(servicdId, serviceName, parentId, parentName.toString(), brand,
						String.valueOf(cityId), String.valueOf(city)));

				// 文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + Arrays.asList(servicdId, serviceName,
						parentId, parentName.toString(), brand, String.valueOf(cityId), String.valueOf(city)));

				// 日志
				sqls.add(GetConfigValue.LogSql());
				lstParam.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), brand,
						parentName.toString(), "增加业务", serviceName, "SERVICE"));

				int n = Database.executeNonQueryTransaction(sqls, lstParam);
				if (n > 0) {
					return servicdId;
				}
			}
		}
		return null;
	}

	/**
	 * 添加业务不记录操作日志
	 * 
	 * @param parentId
	 * @param serviceName
	 * @param brand
	 * @return
	 */
	public static String insertServiceNotLog(String parentId, String serviceName, String brand, String bussinessFlag) {
		String sql = "insert into service(serviceid, service, parentid, parentname, brand, cityid,city) values(?,?,?,?,?,?,?)";
		List<String> sqls = new ArrayList<String>();
		List<List<?>> lstParam = new ArrayList<List<?>>();

		// 文件日志
		GlobalValue.myLog
				.info("select serviceid, service, cityid,city from service where serviceid=?" + "#" + parentId);

		Result rs = Database.executeQuery("select serviceid, service, cityid,city from service where serviceid=?",
				parentId);
		if (rs != null && rs.getRowCount() > 0) {
			Object parentName = rs.getRows()[0].get("service");
			Object cityId = rs.getRows()[0].get("cityid");
			Object city = rs.getRows()[0].get("city");
			if (parentName != null) {
				String servicdId = ConstructSerialNum.GetOracleNextValNew("SEQ_SERVICE_ID", bussinessFlag);

				sqls.add(sql);
				lstParam.add(Arrays.asList(servicdId, serviceName, parentId, parentName.toString(), brand,
						String.valueOf(cityId), String.valueOf(city)));

				// 文件日志
				GlobalValue.myLog.info(sql + "#" + Arrays.asList(servicdId, serviceName, parentId,
						parentName.toString(), brand, String.valueOf(cityId), String.valueOf(city)));

				int n = Database.executeNonQueryTransaction(sqls, lstParam);
				if (n > 0) {
					return servicdId;
				}
			}
		}
		return null;
	}

	/**
	 * 添加业务不记录操作日志
	 * 
	 * @param parentId
	 * @param parentName
	 * @param serviceName
	 * @param brand
	 * @param bussinessFlag
	 * @param cityId
	 * @param city
	 * @return
	 */
	public static String insertServiceNotLog(String parentId, String parentName, String serviceName, String brand,
			String bussinessFlag, String cityId, String city) {
		String sql = "insert into service(serviceid, service, parentid, parentname, brand, cityid,city) values(?,?,?,?,?,?,?)";
		List<String> sqls = new ArrayList<String>();
		List<List<?>> lstParam = new ArrayList<List<?>>();

		// 文件日志
		String servicdId = ConstructSerialNum.GetOracleNextValNew("SEQ_SERVICE_ID", bussinessFlag);

		sqls.add(sql);
		lstParam.add(Arrays.asList(servicdId, serviceName, parentId, parentName, brand, cityId, city));

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + Arrays.asList(servicdId, serviceName, parentId, parentName.toString(), brand,
				String.valueOf(cityId), String.valueOf(city)));

		int n = Database.executeNonQueryTransaction(sqls, lstParam);
		if (n > 0) {
			return servicdId;
		}
		return null;
	}

	/**
	 * 查看同名业务名称
	 * 
	 * @param name
	 *            业务
	 * @param serviceType
	 *            四层结构串
	 * @return int
	 */
	public static boolean isExistServiceName(String serviceid, String name, String serviceType) {
		String brand = serviceType.split("->")[1] + "问题库";
		return isExistServiceNameNew(serviceid, name, brand);
	}

	/**
	 * 查看同名业务名称
	 * 
	 * @param name
	 *            业务
	 * @param brand
	 * @return int
	 */
	public static boolean isExistServiceNameNew(String serviceid, String name, String brand) {
		int count = 0;
		// 查询同级目录是否有重名
		String sql1 = "select count(*) as nums from service where parentid=" + serviceid + " and service='" + name
				+ "' and brand='" + brand + "'";
		Result rs1 = Database.executeQuery(sql1);

		// 文件日志
		GlobalValue.myLog.info(sql1);

		if (rs1 != null) {
			count = Integer.parseInt(rs1.getRows()[0].get("nums").toString());
			if (count > 0) {
				return true;
			}
		}
		// 查询祖先目录是否有重名
		String sql2 = "SELECT service  FROM service start  WITH serviceid=" + serviceid + " and brand='" + brand + "'"
				+ " connect BY nocycle prior  parentid=serviceid";
		Result rs2 = Database.executeQuery(sql2);

		// 文件日志
		GlobalValue.myLog.info(sql2);

		if (rs2 != null) {
			for (int i = 0; i < rs2.getRowCount(); i++) {
				String service = rs2.getRows()[i].get("service").toString();
				if (name.equals(service)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 修改业务名称
	 * 
	 * @param serviceId
	 * @param newServiceName
	 * @return
	 */
	public static int renameService(String serviceId, String newServiceName, User user) {
		List<String> sqls = new ArrayList<String>();
		List<List<?>> lstParam = new ArrayList<List<?>>();

		String sql = "update service set service=? where serviceid=?";
		sqls.add(sql);
		lstParam.add(Arrays.asList(newServiceName, serviceId));
		// 更新父业务名
		sql = "update service set parentname=? where parentid=?";
		sqls.add(sql);
		lstParam.add(Arrays.asList(newServiceName, serviceId));

		// 更新kbdata的abstract
		sql = "update kbdata set abstract=?||substr(abstract,instr(abstract,'>')+1) where serviceid=?";
		sqls.add(sql);
		lstParam.add(Arrays.asList("<" + newServiceName + ">", serviceId));

		// 文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + Arrays.asList(newServiceName, serviceId));

		// 日志
		sqls.add(GetConfigValue.LogSql());
		String serviceName = CommonLibServiceDAO.getServiceNameByID(serviceId);
		lstParam.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), user.getBrand(),
				serviceName, "修改业务", newServiceName, "SERVICE"));

		return Database.executeNonQueryTransaction(sqls, lstParam);
	}

	/**
	 * 删除业务（数据库实现级联删除）
	 * 
	 * @param serviceId
	 * @return
	 */
	public static int deleteService(String serviceId, User user) {
		List<String> sqls = new ArrayList<String>();
		List<List<?>> lstParam = new ArrayList<List<?>>();

		String sql = "delete from service where serviceid=?";
		sqls.add(sql);
		lstParam.add(Arrays.asList(serviceId));

		// 文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + Arrays.asList(serviceId));

		// 日志
		sqls.add(GetConfigValue.LogSql());
		String serviceName = CommonLibServiceDAO.getServiceNameByID(serviceId);
		lstParam.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), user.getBrand(),
				serviceName, "删除业务", serviceName, "SERVICE"));

		return Database.executeNonQueryTransaction(sqls, lstParam);
	}

	/**
	 * 取两个地市代码字符串的并集
	 * 
	 * @param codes1
	 * @param codes2
	 * @return
	 */
	private static String unionCityCodes(String codes1, String codes2) {
		Set<String> set = new TreeSet<String>();
		String[] tmp1 = codes1.split(",");
		String[] tmp2 = codes2.split(",");
		for (String s : tmp1) {
			if (StringUtils.isNotBlank(s))
				set.add(s);
		}
		for (String s : tmp2) {
			if (StringUtils.isNotBlank(s))
				set.add(s);
		}
		return StringUtils.join(set.iterator(), ",");
	}

	/**
	 * 问题库理解状态更新
	 * 
	 * @param result
	 * @param string
	 * @param flag
	 * @return
	 */
	public static int understand(String result, String id, String flag) {
		String sql = "update querymanage set result=?,status=? where id =?";
		int c = 0;
		// 文件日志
		GlobalValue.myLog.info(sql);

		c = Database.executeNonQuery(sql, result, flag, id);
		return c;
	}

	/**
	 * 获取操作日志SQL
	 */
	private static String getInsertLogSql() {
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)";
		} else if (GetConfigValue.isMySQL) {
			sql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,sysdate())";
		}
		return sql;
	}

	/**
	 * 根据摘要ID获取业务名
	 * 
	 * @param kbdataid
	 * @return
	 */
	private static String[] getServiceByKbdataid(String kbdataid) {
		String[] strArr = new String[2];
		Result rs = Database.executeQuery(
				"select service,serviceid from service where serviceid=(select serviceid from kbdata where kbdataid=?)",
				kbdataid);
		if (rs != null && rs.getRowCount() > 0) {
			Object tmp;
			if ((tmp = rs.getRows()[0].get("service")) != null) {
				strArr[0] = tmp.toString();
			}
			if ((tmp = rs.getRows()[0].get("serviceid")) != null) {
				strArr[1] = tmp.toString();
			}
		}
		return strArr;
	}

	/**
	 * 根据摘要ID获取标准问
	 * 
	 * @param kbdataid
	 * @return
	 */
	private static String getNormalqueryByKbdataid(String kbdataid) {
		Result rs = Database.executeQuery("select abstract from kbdata where kbdataid=?", kbdataid);

		// 文件日志
		GlobalValue.myLog.info("select abstract from kbdata where kbdataid=?" + "#" + kbdataid);

		if (rs != null && rs.getRowCount() == 1) {
			String abs = rs.getRows()[0].get("abstract").toString();
			return abs.split(">")[1];
		}
		return "";
	}

	/**
	 * 根据ID查询客户问题
	 * 
	 * @param id
	 * @return
	 */
	private static String getCustomerqueryByID(String id) {
		Result rs = Database.executeQuery("select query from querymanage where id=?", id);

		// 文件日志
		GlobalValue.myLog.info("select query from querymanage where id=?" + "#" + id);

		if (rs != null && rs.getRowCount() == 1) {
			String query = rs.getRows()[0].get("query").toString();
			return query;
		}
		return "";
	}

	/**
	 * 转换SQL参数列表为List
	 * 
	 * @param params
	 * @return
	 */
	private static List<Object> getSQLParams(Object... params) {
		List<Object> list = new ArrayList<Object>();
		for (Object param : params) {
			list.add(param);
		}
		return list;
	}

	/**
	 * 标准问发现新词插入词类词条
	 * 
	 * @param info
	 * @return
	 */
	public static int insertWordClassAndItem(User user, List<List<Object>> info) {

		String returnMsg = "新增成功！";
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义sql
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();

		// Map<wordclassid,词条>
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		// Map<wordclass, wordclassid>
		Map<String, String> nameToIdMap = new LinkedHashMap<String, String>();

		// 获得商家标识符
		String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);

		int index = 0;
		// 遍历每一行
		for (List<Object> line : info) {
			index++;
			// 词类，如果包含英文统一转化为大写
			String wordclass = line.get(0) == null ? "" : line.get(0).toString().replace("近类", "").trim().toUpperCase();
			// 词条，如果包含英文统一转化为大写
			String word = line.get(1) == null ? "" : line.get(1).toString().trim().toUpperCase();
			// 如果词类/词条为空，则略过这一行,词条长度大于4也略过这一行
			if ("".equals(wordclass)) {
				returnMsg = returnMsg + "<br/>第" + index + "条数据同义词为空！";
				continue;
			}
			// 词类未收集
			if (!nameToIdMap.containsKey(wordclass + "近类")) {
				String checkWordclassSql = "select wordclassid from wordclass where container='基础' and wordclass = '"
						+ wordclass + "近类" + "'";
				Result checkWordclassResult = Database.executeQuery(checkWordclassSql);
				// 库中存在该词类
				if (checkWordclassResult != null && checkWordclassResult.getRowCount() > 0) {
					String wordclassid = checkWordclassResult.getRows()[0].get("wordclassid").toString();
					String checkWordSql = "select * from word where wordclassid='" + wordclassid + "'";
					Result checkWordResult = Database.executeQuery(checkWordSql);
					// 词类存在，则把所有库中词条放入map中
					if (checkWordResult != null && checkWordResult.getRowCount() > 0) {
						List<String> wordList = new ArrayList<String>();
						for (int i = 0; i < checkWordResult.getRowCount(); i++) {
							wordList.add(checkWordResult.getRows()[i].get("word").toString());
						}
						// 同义词不在库中
						if (!wordList.contains(wordclass)) {
							sql = "";
							lstpara = new ArrayList<Object>();

							// 获取词条表的序列值
							String wordid = "";
							if (GetConfigValue.isOracle) {
								wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
							} else if (GetConfigValue.isMySQL) {
								wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
							}
							sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
							// 绑定id参数
							lstpara.add(wordid);
							// 绑定词类id参数
							lstpara.add(wordclassid);
							// 绑定词类名称
							lstpara.add(wordclass);
							// 绑定类型参数
							lstpara.add("标准名称");
							// 将SQL语句放入集合中
							lstSql.add(sql);
							// 将对应的绑定参数集合放入集合中
							lstLstpara.add(lstpara);

							wordList.add(wordclass);
						}
						// 词条不在库中
						if (!"".equals(word) && !wordList.contains(word)) {
							sql = "";
							lstpara = new ArrayList<Object>();

							// 获取词条表的序列值
							String wordid = "";
							if (GetConfigValue.isOracle) {
								wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
							} else if (GetConfigValue.isMySQL) {
								wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
							}
							sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
							// 绑定id参数
							lstpara.add(wordid);
							// 绑定词类id参数
							lstpara.add(wordclassid);
							// 绑定词类名称
							lstpara.add(word);
							// 绑定类型参数
							lstpara.add("标准名称");
							// 将SQL语句放入集合中
							lstSql.add(sql);
							// 将对应的绑定参数集合放入集合中
							lstLstpara.add(lstpara);

							wordList.add(word);
						}
						nameToIdMap.put(wordclass + "近类", wordclassid);
						map.put(wordclassid, wordList);
					}
				} else {// 库中不存在该词类，则新建词类

					String wordclassid = "";
					if (GetConfigValue.isOracle) {
						wordclassid = ConstructSerialNum.GetOracleNextValNew("seq_wordclass_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordclassid = ConstructSerialNum.getSerialIDNew("wordclass", "wordclassid", bussinessFlag);
					}
					// 插入词类的SQL语句
					sql = "insert into wordclass(wordclassid,wordclass,container) values(?,?,?)";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定id参数
					lstpara.add(wordclassid);
					// 绑定词类参数
					lstpara.add(wordclass + "近类");
					// 绑定类型参数
					lstpara.add("基础");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					// 新建词类完成后添加对应的词条
					// 获取词条表的序列值
					String wordid = "";

					if (GetConfigValue.isOracle) {
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
					}
					sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称
					lstpara.add(wordclass);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					List<String> wordList = new ArrayList<String>();
					wordList.add(wordclass);

					if (!"".equals(word) && !wordclass.equals(word)) {
						if (GetConfigValue.isOracle) {
							wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
						} else if (GetConfigValue.isMySQL) {
							wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
						}
						sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
						// 定义绑定参数集合
						lstpara = new ArrayList<Object>();
						// 绑定id参数
						lstpara.add(wordid);
						// 绑定词类id参数
						lstpara.add(wordclassid);
						// 绑定词类名称
						lstpara.add(word);
						// 绑定类型参数
						lstpara.add("标准名称");
						// 将SQL语句放入集合中
						lstSql.add(sql);
						// 将对应的绑定参数集合放入集合中
						lstLstpara.add(lstpara);

						wordList.add(word);
					}

					nameToIdMap.put(wordclass + "近类", wordclassid);
					map.put(wordclassid, wordList);
				}
			} else {// 该词类已收集
				String wordclassid = nameToIdMap.get(wordclass + "近类");
				List<String> wordList = new ArrayList<String>();
				wordList = map.get(wordclassid);

				// 同义词不在库中
				if (!wordList.contains(wordclass)) {
					sql = "";
					lstpara = new ArrayList<Object>();

					// 获取词条表的序列值
					String wordid = "";
					if (GetConfigValue.isOracle) {
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
					}
					sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称
					lstpara.add(wordclass);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					wordList.add(wordclass);
				}

				// 词条未收集
				if (!wordList.contains(word)) {
					sql = "";
					lstpara = new ArrayList<Object>();

					// 获取词条表的序列值
					String wordid = "";
					if (GetConfigValue.isOracle) {
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
					}
					sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称
					lstpara.add(word);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					wordList.add(word);
					map.put(wordclassid, wordList);
				}
			}
		}

		int count = -1;
		count = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		if (count == 0) {
			returnMsg = returnMsg + "！";
		}
		System.out.println(returnMsg);

		// if (count == 0){
		// return returnMsg + "！";
		// }else if (count == -1){
		// return "导入失败";
		// }
		return count;
	}

	/**
	 * 仅更新答案
	 * 
	 * @param user
	 * @param serviceid
	 * @param service
	 * @param kbdata
	 * @param answer
	 * @param city
	 * @param configValueList
	 * @param serviceType
	 * @return
	 */
	public static int updateKbdataAnswer(User user, String serviceid, String service, String kbdata, String answer,
			String city, List<String> configValueList, String serviceType, String kbdataid) {
		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstparam = new ArrayList<List<?>>();

		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());

		// 定义sql
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();

		sql = "select kbansvaliddate.kbansvaliddateid from kbanswer,kbansvaliddate,kbanspak,kbansqryins,kbcontent "
				+ "where kbansvaliddate.kbansvaliddateid=kbanspak.kbansvaliddateid "
				+ "and kbanspak.kbanspakid=kbansqryins.kbanspakid "
				+ "and kbansqryins.kbansqryinsid=kbcontent.kbansqryinsid "
				+ "and kbcontent.kbcontentid=kbanswer.kbcontentid " + "and kbansvaliddate.kbdataid=? " + "and (";
		lstpara.add(kbdataid);

		for (String channel : configValueList) {
			sql += "kbcontent.channel=? or ";
			lstpara.add(channel);
		}
		sql = sql.substring(0, sql.lastIndexOf("or"));
		sql += ")";

		// 删除已存在的答案
		sql = "delete from kbansvaliddate where kbansvaliddateid in (" + sql + ")";

		// 将SQL语句放入SQL语句集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstparam.add(lstpara);

		String kbvid = "";
		String kbanspakid = "";
		String kbansqryinsid = "";
		String kbcontentid = "";
		String kbanswerid = "";

		// 插入答案相关连数据
		for (String channel : configValueList) {
			// 获取答案关联表主键ID序列
			if (GetConfigValue.isOracle) {
				kbvid = String.valueOf(ConstructSerialNum.GetOracleNextValNew("KBANSVALIDDATE_SEQ", bussinessFlag));
				kbanspakid = String.valueOf(ConstructSerialNum.GetOracleNextValNew("KBANSPAK_SEQ", bussinessFlag));
				kbansqryinsid = String
						.valueOf(ConstructSerialNum.GetOracleNextValNew("KBANSQRYINS_SEQ", bussinessFlag));
				kbcontentid = String.valueOf(ConstructSerialNum.GetOracleNextValNew("SEQ_KBCONTENT_ID", bussinessFlag));
				kbanswerid = String.valueOf(ConstructSerialNum.GetOracleNextValNew("KBANSWER_SEQ", bussinessFlag));
			} else if (GetConfigValue.isMySQL) {
				kbvid = String.valueOf(
						ConstructSerialNum.getSerialIDNew("KBANSVALIDDATE", "KBANSVALIDDATEID", bussinessFlag));
				kbanspakid = String.valueOf(ConstructSerialNum.getSerialIDNew("KBANSPAK", "KBANSPAKID", bussinessFlag));
				kbansqryinsid = String
						.valueOf(ConstructSerialNum.getSerialIDNew("KBANSQRYINS", "KBANSQRYINSID", bussinessFlag));
				kbcontentid = String
						.valueOf(ConstructSerialNum.getSerialIDNew("KBCONTENT", "KBCONTENTID", bussinessFlag));
				kbanswerid = String.valueOf(ConstructSerialNum.getSerialIDNew("KBANSWER", "KBANSWERID", bussinessFlag));
			}

			// 插入kbansvaliddate表
			// 插入kbansvaliddate的SQL语句
			sql = "insert into kbansvaliddate(KBANSVALIDDATEID,KBDATAID,BEGINTIME,ENDTIME) values(?,?,null,null)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbansvaliddateid参数
			lstpara.add(kbvid);
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);

			// 插入kbanspak表
			// 插入kbanspak的SQL语句
			sql = "insert into kbanspak(KBANSPAKID,KBANSVALIDDATEID,PACKAGE ,PACKAGECODE,PAKTYPE) values(?,?,?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbanspakid参数
			lstpara.add(kbanspakid);
			// 绑定kbansvaliddateid参数
			lstpara.add(kbvid);
			// 绑定package参数
			lstpara.add("空号码包");
			// 绑定packagecode参数
			lstpara.add(null);
			// 绑定paktype参数
			lstpara.add("0");
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);

			// 插入kbansqryins表
			// 插入kbansqryins的SQL语句
			sql = "insert into kbansqryins(KBANSQRYINSID,KBANSPAKID,QRYINS) values(?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbansqryinsid参数
			lstpara.add(kbansqryinsid);
			// 绑定kbanspakid参数
			lstpara.add(kbanspakid);
			// 绑定qryins参数
			lstpara.add("查询指令无关");
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);

			// 插入kbcontent表
			// 插入kbcontent的SQL语句
			sql = "insert into kbcontent(KBCONTENTID ,KBANSQRYINSID,CHANNEL,ANSWERCATEGORY,SERVICETYPE ,CUSTOMERTYPE, CITY) values(?,?,?,?,?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 绑定kbansqryinsid参数
			lstpara.add(kbansqryinsid);
			// 绑定渠道参数
			lstpara.add(channel);
			// 绑定answerType参数
			lstpara.add("0");
			// 绑定servicetype参数
			lstpara.add(serviceType);
			// 绑定customertype参数
			lstpara.add("普通客户");
			// 绑定city参数
			lstpara.add(city);
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);

			// 插入kbanswer表
			// 插入kbanswer的SQL语句
			sql = "insert into kbanswer(kbanswerid,kbcontentid,answercontent,servicehallstatus,city,customertype,brand) values(?,?,?,?,?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbanswerid参数
			lstpara.add(kbanswerid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 绑定答案参数
			lstpara.add(answer);
			// 绑定servicehallstatus参数
			lstpara.add("无关");
			// 绑定城市参数
			lstpara.add("上海");
			// 绑定customertype参数
			lstpara.add("所有客户");
			// 绑定品牌参数
			lstpara.add(serviceType.split("->")[1] + "问题库");
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);
			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstparam.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), " ", " ",
					"增加答案", answer, "KBANSWER"));
		}
		int result = Database.executeNonQueryTransaction(lstsql, lstparam);
		return result;
	}

	/**
	 * 插入摘要答案
	 * 
	 * @param user
	 * @param serviceid
	 * @param service
	 * @param kbdata
	 * @param answer
	 * @param city
	 * @param configValueList
	 * @param serviceType
	 * @return
	 */
	public static int insertKbdata(User user, String serviceid, String service, String kbdataid, String kbdata,
			String answer, String city, List<String> configValueList, String serviceType, String querymanageId) {

		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstparam = new ArrayList<List<?>>();

		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());

		String sql = "";

		String kbvid = "";
		String kbanspakid = "";
		String kbansqryinsid = "";
		String kbcontentid = "";
		String kbanswerid = "";

		// 新增摘要
		sql = "insert into kbdata(serviceid,kbdataid,topic,abstract,city,RESPONSETYPE,INTERACTTYPE) values (?,?,?,?,?,?,?)";

		List<Object> lstpara = new ArrayList<Object>();
		// 新增摘要
		lstpara = new ArrayList<Object>();
		lstpara.add(serviceid);
		lstpara.add(kbdataid);
		lstpara.add("常见问题");
		lstpara.add("<" + service + ">" + kbdata);
		lstpara.add(city);
		lstpara.add("未知");
		lstpara.add("未交互");

		lstsql.add(sql);
		lstparam.add(lstpara);

		sql = "insert into querymanage(ID,KBDATAID,QUERY,CITY,WORKERID) values(?,?,?,?,?)";
		// String querymanageId = "";
		// if (GetConfigValue.isOracle) {
		// querymanageId =
		// ConstructSerialNum.GetOracleNextValNew("seq_querymanage_id",bussinessFlag);
		// } else if (GetConfigValue.isMySQL) {
		// querymanageId = ConstructSerialNum.getSerialIDNew("querymanage",
		// "id", bussinessFlag);
		// }
		lstpara = new ArrayList<Object>();
		lstpara.add(querymanageId);
		lstpara.add(kbdataid);
		lstpara.add(kbdata);
		lstpara.add(city);
		lstpara.add(user.getUserID());
		lstsql.add(sql);
		lstparam.add(lstpara);

		// 插入答案相关连数据
		for (String channel : configValueList) {
			// 获取答案关联表主键ID序列
			if (GetConfigValue.isOracle) {
				kbvid = String.valueOf(ConstructSerialNum.GetOracleNextValNew("KBANSVALIDDATE_SEQ", bussinessFlag));
				kbanspakid = String.valueOf(ConstructSerialNum.GetOracleNextValNew("KBANSPAK_SEQ", bussinessFlag));
				kbansqryinsid = String
						.valueOf(ConstructSerialNum.GetOracleNextValNew("KBANSQRYINS_SEQ", bussinessFlag));
				kbcontentid = String.valueOf(ConstructSerialNum.GetOracleNextValNew("SEQ_KBCONTENT_ID", bussinessFlag));
				kbanswerid = String.valueOf(ConstructSerialNum.GetOracleNextValNew("KBANSWER_SEQ", bussinessFlag));
			} else if (GetConfigValue.isMySQL) {
				kbvid = String.valueOf(
						ConstructSerialNum.getSerialIDNew("KBANSVALIDDATE", "KBANSVALIDDATEID", bussinessFlag));
				kbanspakid = String.valueOf(ConstructSerialNum.getSerialIDNew("KBANSPAK", "KBANSPAKID", bussinessFlag));
				kbansqryinsid = String
						.valueOf(ConstructSerialNum.getSerialIDNew("KBANSQRYINS", "KBANSQRYINSID", bussinessFlag));
				kbcontentid = String
						.valueOf(ConstructSerialNum.getSerialIDNew("KBCONTENT", "KBCONTENTID", bussinessFlag));
				kbanswerid = String.valueOf(ConstructSerialNum.getSerialIDNew("KBANSWER", "KBANSWERID", bussinessFlag));
			}

			// 插入kbansvaliddate表
			// 插入kbansvaliddate的SQL语句
			sql = "insert into kbansvaliddate(KBANSVALIDDATEID,KBDATAID,BEGINTIME,ENDTIME) values(?,?,null,null)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbansvaliddateid参数
			lstpara.add(kbvid);
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);

			// 插入kbanspak表
			// 插入kbanspak的SQL语句
			sql = "insert into kbanspak(KBANSPAKID,KBANSVALIDDATEID,PACKAGE ,PACKAGECODE,PAKTYPE) values(?,?,?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbanspakid参数
			lstpara.add(kbanspakid);
			// 绑定kbansvaliddateid参数
			lstpara.add(kbvid);
			// 绑定package参数
			lstpara.add("空号码包");
			// 绑定packagecode参数
			lstpara.add(null);
			// 绑定paktype参数
			lstpara.add("0");
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);

			// 插入kbansqryins表
			// 插入kbansqryins的SQL语句
			sql = "insert into kbansqryins(KBANSQRYINSID,KBANSPAKID,QRYINS) values(?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbansqryinsid参数
			lstpara.add(kbansqryinsid);
			// 绑定kbanspakid参数
			lstpara.add(kbanspakid);
			// 绑定qryins参数
			lstpara.add("查询指令无关");
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);

			// 插入kbcontent表
			// 插入kbcontent的SQL语句
			sql = "insert into kbcontent(KBCONTENTID ,KBANSQRYINSID,CHANNEL,ANSWERCATEGORY,SERVICETYPE ,CUSTOMERTYPE, CITY) values(?,?,?,?,?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 绑定kbansqryinsid参数
			lstpara.add(kbansqryinsid);
			// 绑定渠道参数
			lstpara.add(channel);
			// 绑定answerType参数
			lstpara.add("0");
			// 绑定servicetype参数
			lstpara.add(serviceType);
			// 绑定customertype参数
			lstpara.add("普通客户");
			// 绑定city参数
			lstpara.add(city);
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);

			// 插入kbanswer表
			// 插入kbanswer的SQL语句
			sql = "insert into kbanswer(kbanswerid,kbcontentid,answercontent,servicehallstatus,city,customertype,brand) values(?,?,?,?,?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定kbanswerid参数
			lstpara.add(kbanswerid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 绑定答案参数
			lstpara.add(answer);
			// 绑定servicehallstatus参数
			lstpara.add("无关");
			// 绑定城市参数
			lstpara.add("上海");
			// 绑定customertype参数
			lstpara.add("所有客户");
			// 绑定品牌参数
			lstpara.add(serviceType.split("->")[1] + "问题库");
			// 将SQL语句放入SQL语句集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstparam.add(lstpara);
			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstparam.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), " ", " ",
					"增加答案", answer, "KBANSWER"));
		}
		int result = Database.executeNonQueryTransaction(lstsql, lstparam);
		return result;
	}

	public static Result getCityFromService(String serviceid) {
		String sql = "select city from service where serviceid=" + serviceid;
		Result rs = Database.executeQuery(sql);
		return rs;
	}

	public static Result getInfoFromKbdataByServiceid(String serviceid) {
		String sql = "select kbdataid,abstract,city from kbdata where serviceid =" + serviceid;
		Result rs = Database.executeQuery(sql);
		return rs;
	}

	/**
	 * 方法名称： deleteSpecialCharacter 内容摘要： 删除空格、回车、换行符、制表符特殊字符
	 * 
	 * @author lcen 2014-8-21
	 * @param str
	 *            要处理的字符串
	 * @return String
	 */
	private static String deleteSpecialCharacter(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	public static int insertbyExcel(String sWordpat, String city, String kbdataid, User user) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara;
		// 定义多条SQL语句
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		sql = new StringBuilder();
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());
		String wordpatid = "";
		if (GetConfigValue.isOracle) {
			// 获取词模表的序列值
			wordpatid = ConstructSerialNum.GetOracleNextValNew("SEQ_WORDPATTERN_ID", bussinessFlag);
			// 定义新增模板的SQL语句
			sql.append(
					"insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,simplewordpat,city,workerid) values(?,?,?,?,?,(select brand from service where serviceid=(select serviceid from kbdata where kbdataid=?)),sysdate,?,?,?)");
		} else if (GetConfigValue.isMySQL) {
			// 获取词模表的序列值
			wordpatid = ConstructSerialNum.getSerialIDNew("wordpat", "wordpatid", bussinessFlag);
			// 定义新增模板的SQL语句
			sql.append(
					"insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,simplewordpat,city,workerid) values(?,?,?,?,?,(select brand from service where serviceid=(select serviceid from kbdata where kbdataid=?)),sysdate(),?,?,?)");
		}

		// 绑定模板id参数
		lstpara.add(wordpatid);
		// 绑定模板参数
		lstpara.add(sWordpat);
		// 绑定自动开关参数
		lstpara.add(0);
		// 绑定模板类型参数
		lstpara.add(0);
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定品牌参数
		lstpara.add(kbdataid);
		// 绑定简单词模参数
		lstpara.add(null);
		lstpara.add(city);
		lstpara.add(user.getUserID());
		// 将SQL语句放入集合中
		lstSql.add(sql.toString());
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		int r = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return r;
	}

	/**
	 * 搜索自学习词模
	 * 
	 * @param kbdataid
	 * @param wordpat
	 * @param queryid
	 * @return
	 */
	public static Result selectWordpatByKbdataid(List<String> kbdataids, String wordpattype) {

		// 定义查询sql语句
		String sql = "select * from wordpat where wordpattype = ? and ( ";

		// 如果参数为空返回空结果集
		if (kbdataids.isEmpty())
			return null;

		// 绑定参数
		for (int i = 0; i < kbdataids.size(); i++) {
			if (i == 0) {
				sql += "kbdataid = " + kbdataids.get(i);
			} else {
				sql += " or kbdataid = " + kbdataids.get(i);
			}
		}
		sql += " )";
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + wordpattype);

		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(sql, wordpattype);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;
	}

	/**
	 * 查找标准问下的扩展问
	 * 
	 * @param normalquery
	 * @param brands
	 * @return
	 */
	public static Result findCustomerqueryByNormalquery(String normalquery, List<String> brands) {

		// 定义查询sql语句
		String sql = "select q.* from kbdata k,querymanage q where "
				+ "k.serviceid in (select serviceid from service start with service in ('"
				+ StringUtils.join(brands, "','")
				+ "') connect by nocycle prior serviceid = parentid ) and k.abstract like ? and k.kbdataid = q.kbdataid order by q.id";

		// 如果参数为空返回空结果集
		if (brands.isEmpty())
			return null;

		// 文件日志
		GlobalValue.myLog.info(sql + "#[" + normalquery + brands + "]");

		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(sql, "%" + normalquery + "%");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;
	}

	/**
	 * 导出词模
	 * 
	 * @param serviceid
	 * @param flag
	 *            0：级联子节点业务导出，1：仅此业务导出
	 * @return
	 */
	public static Result exportWordpat(String serviceid, String flag) {

		StringBuilder builder = new StringBuilder();
		builder.append("select s1.*,sr2.ruleresponse as ruleresponse from ( ");
		builder.append(
				"select s.serviceid,k.kbdataid,k.abstract as abstract,k.responsetype,k.interacttype,w.wordpat ,w.wordpattype,w.city,to_char(w.edittime,'yyyy/MM/dd HH24:mi') as edittime,sk.relationserviceid from service s ");
		builder.append("join kbdata k ");
		if (flag != null && flag.equals("0")) {
			builder.append(
					"	on s.serviceid = k.serviceid and s.serviceid in (SELECT serviceid  FROM  service start  WITH serviceid=? connect BY nocycle prior serviceid = parentid) ");
		} else {
			builder.append("	on s.serviceid = k.serviceid and s.serviceid = ? ");
		}
		builder.append("left join wordpat w");
		builder.append(
				"	on k.kbdataid = w.kbdataid and (w.wordpattype = 0 or w.wordpattype = 1 or w.wordpattype = 2 or w.wordpattype = 3 or w.wordpattype = 4 or w.wordpattype is null)");
		builder.append("left join scenarios2kbdata sk ");
		builder.append("	on k.kbdataid = sk.abstractid ");
		builder.append(") s1 ");
		builder.append("left join scenariosrules sr2 ");
		builder.append("	on s1.relationserviceid = sr2.relationserviceid and sr2.weight = 1 ");
		builder.append("order by s1.kbdataid");
		// 文件日志
		GlobalValue.myLog.info(builder.toString() + "#" + serviceid);
		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(builder.toString(), serviceid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 导出场景-标准问-客户问
	 * 
	 * @param serviceid
	 * @param flag
	 *            0：级联子节点业务导出，1：仅此业务导出
	 * @return
	 */
	public static Result exportCustomerQuery(String serviceid, String flag) {

		StringBuilder builder = new StringBuilder();
		builder.append("select s1.*,sr2.ruleresponse as  ruleresponse from ( ");
		builder.append(
				"select s.serviceid,k.kbdataid,k.abstract ,q.query, k.responsetype,k.interacttype,q.city,to_char(q.edittime,'yyyy/MM/dd HH24:mi') as edittime,sk.relationserviceid ");
		builder.append("from service s ");
		builder.append("join kbdata k ");
		if (flag != null && flag.equals("0")) {
			builder.append(
					"	on s.serviceid = k.serviceid and s.serviceid in (SELECT serviceid  FROM  service start  WITH serviceid=? connect BY nocycle prior serviceid = parentid) ");
		} else {
			builder.append("	on s.serviceid = k.serviceid and s.serviceid = ? ");
		}
		builder.append("left join querymanage q ");
		builder.append("  on k.kbdataid = q.kbdataid ");
		builder.append("left join scenarios2kbdata sk ");
		builder.append("  on k.kbdataid = sk.abstractid ");
		builder.append(") s1 ");
		builder.append("left join scenariosrules sr2 ");
		builder.append("  on s1.relationserviceid = sr2.relationserviceid and sr2.weight = 1 ");
		builder.append("order by s1.kbdataid");
		// 文件日志
		GlobalValue.myLog.info(builder.toString() + "#" + serviceid);
		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(builder.toString(), serviceid);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static Result getWordpatCountByKbdataId(List<String> kbdataId) {
		if (kbdataId == null || kbdataId.size() < 1)
			return null;

		StringBuilder builder = new StringBuilder();
		builder.append("SELECT kbdataid, COUNT(*) as count FROM wordpat WHERE kbdataid in("
				+ StringUtils.join(kbdataId, ",") + ") AND wordpattype!=5 group by kbdataid");

		// 文件日志
		GlobalValue.myLog.info(builder.toString());
		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static Result getWordpatCountByKbdataId(List<String> kbdataId, boolean showAutoWordpat) {
		if (kbdataId == null || kbdataId.size() < 1)
			return null;

		StringBuilder builder = new StringBuilder();
		builder.append("SELECT kbdataid, COUNT(*) as count FROM wordpat WHERE kbdataid in("
				+ StringUtils.join(kbdataId, ",") + ")  ");
		if (!showAutoWordpat) {
			builder.append(" AND wordpattype!=5 ");
		}
		builder.append(" group by kbdataid");
		// 文件日志
		GlobalValue.myLog.info(builder.toString());
		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static Result getRelatequeryCountByKbdataId(List<String> kbdataId) {
		if (kbdataId == null || kbdataId.size() < 1)
			return null;

		StringBuilder builder = new StringBuilder();
		builder.append("SELECT kbdataid , COUNT(*) as count FROM relatequery WHERE kbdataid in ("
				+ StringUtils.join(kbdataId, ",") + ") group by kbdataid");

		// 文件日志
		GlobalValue.myLog.info(builder.toString());
		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	public static Result getExtendCountByKbdataId(List<String> kbdataId) {
		if (kbdataId == null || kbdataId.size() < 1)
			return null;

		StringBuilder builder = new StringBuilder();
		builder.append("SELECT attr6 as kbdataid,COUNT(*) as count FROM serviceorproductinfo WHERE attr6 in ("
				+ StringUtils.join(kbdataId, ",") + ") AND abstractid IS NOT NULL group by attr6");

		// 文件日志
		GlobalValue.myLog.info(builder.toString());
		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(builder.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * 查询问题ID
	 * 
	 * @param query
	 * @param kbdateId
	 * @return
	 */
	public static Result getQueryIdByQuery(String query, String kbdateId) {
		// 定义查询sql语句
		String sql = "select q.* from querymanage q where q.kbdataid = ? and q.query = ?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		lstpara.add(kbdateId);
		lstpara.add(query);
		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(sql, lstpara.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rs;
	}

	/**
	 * 新增排除问题
	 * 
	 * @param normalQuery
	 *            标准问
	 * @param customerQuery
	 *            排除问
	 * @param cityCode
	 *            地市编码
	 * @param user
	 *            用户信息
	 * @param removequerystatus
	 *            是否严格排除
	 * @return
	 */
	public static int addRemoveQuery(String normalQuery, String customerQuery, String cityCode, User user,
			String removequerystatus) {
		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		List<Object> lstpara = new ArrayList<Object>();
		int rs = -1;
		String querymanageId = "";
		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());
		String insertSql = "insert into querymanage(ID,KBDATAID,QUERY,CITY,WORKERID, QUERYTYPE, ISSTRICTEXCLUSION) values(?,?,?,?,?,1,?)";
		String updateSql = " update querymanage set CITY=? , EDITTIME=sysdate where QUERY=? and KBDATAID=? and querytype=1";
		Map<String, String> map = getCustomerQueryDic(normalQuery, 1);
		Map<String, Map<String, String>> insertOrUpdateDic = getCustomerQueryInsertOrUpdateDic(map, customerQuery,
				cityCode);
		if (insertOrUpdateDic.size() > 0) {
			for (Map.Entry<String, Map<String, String>> entry : insertOrUpdateDic.entrySet()) {
				String type = entry.getKey();
				if ("insert".equals(type)) {// insert
					for (Map.Entry<String, String> insertDic : entry.getValue().entrySet()) {
						String query = insertDic.getKey();
						String city = insertDic.getValue();
						if (GetConfigValue.isOracle) {
							querymanageId = ConstructSerialNum.GetOracleNextValNew("seq_querymanage_id", bussinessFlag);
						} else if (GetConfigValue.isMySQL) {
							querymanageId = ConstructSerialNum.getSerialIDNew("querymanage", "id", bussinessFlag);
						}
						lstpara = new ArrayList<Object>();
						lstpara.add(querymanageId);
						lstpara.add(normalQuery);
						lstpara.add(query);
						lstpara.add(city);
						lstpara.add(user.getUserID());
						lstpara.add(StringUtil.isEmpty(removequerystatus) ? "否" : removequerystatus);
						lstsql.add(insertSql);
						lstlstpara.add(lstpara);
						// 日志 insert into
						// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
						String[] serviceArr = getServiceByKbdataid(normalQuery);
						lstsql.add(getInsertLogSql());
						lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), serviceArr[0], "增加排除问题", " ",
								user.getUserID(), user.getUserName(), query, "QUERYMANAGE"));

					}
				} else {// update
					for (Map.Entry<String, String> insertDic : entry.getValue().entrySet()) {
						String query = insertDic.getKey();
						String city = insertDic.getValue();
						lstpara = new ArrayList<Object>();
						lstpara.add(city);
						lstpara.add(query);
						lstpara.add(normalQuery);
						lstsql.add(updateSql);
						lstlstpara.add(lstpara);
						// 日志 insert into
						// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
						String[] serviceArr = getServiceByKbdataid(normalQuery);
						lstsql.add(getInsertLogSql());
						lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), serviceArr[0], "更新排除问题", " ",
								user.getUserID(), user.getUserName(), query, "QUERYMANAGE"));
					}
				}
			}
			return Database.executeNonQueryTransaction(lstsql, lstlstpara);
		} else {
			rs = 1;// 新增排除问题已存在默认插入成功，后续优化给出提示 TODO
		}

		return rs;
	}

	/**
	 * @description 获取排除问题下客户问题详情
	 * @param serviceid
	 * @param kbdataid
	 * @param customerQuery
	 * @param cityCode
	 * @param page
	 * @param rows
	 * @return
	 * @returnType Result
	 */
	public static Result selectRemoveQuery(String serviceid, String kbdataid, String customerQuery, String cityCode,
			String isTrain, String removequerystatus, int page, int rows) {
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();

		String sql = " select * from (select t.*,rownum rn from ( "
				+ " select * from (select s.service,s.brand,k.kbdataid,k.abstract,k.city abscity,k.responsetype,k.interacttype,k.topic, q.query,q.city ,q.id ,q.status ,q.result, q.istrain,q.isstrictexclusion from (select * from service where serviceid=? ) s  "
				+ " inner join(select * from kbdata where  1>0  ";

		lstpara.add(serviceid);
		if (!"".equals(kbdataid) && kbdataid != null) {
			sql = sql + "  and ( kbdataid = ? ) ";
			lstpara.add(kbdataid);
		}

		if (!"".equals(cityCode) && cityCode != null) {
			if ("全国".equals(cityCode)) {
				cityCode = "";
			} else if (cityCode.endsWith("0000")) {// 地市为省级加载省级下面的所有内容
				cityCode = cityCode.replace("0000", "");
			}
			lstpara.add("%" + cityCode + "%");
		} else {
			lstpara.add("%%");
		}
		lstpara.add("%" + customerQuery + "%");

		String statusSql, istrainSql;
		if (StringUtils.isBlank(removequerystatus)) { // 全部
			statusSql = "";
		} else if (removequerystatus.equals("none")) { // 未理解
			statusSql = " and status is null";
		} else {
			lstpara.add(removequerystatus);
			statusSql = " and isstrictexclusion = ?";
		}
		if (StringUtils.isBlank(isTrain)) {
			istrainSql = "";
		} else {
			lstpara.add(StringUtils.trim(isTrain));
			istrainSql = " and istrain = ?";
		}

		lstpara.add(page * rows);
		lstpara.add((page - 1) * rows);

		sql = sql
				+ "  ) k on s.serviceid = k.serviceid  inner join (select * from querymanage where querytype = 1 and city like ? and query like ? "
				+ statusSql + istrainSql
				+ ") q  on k.kbdataid = q.kbdataid) aa order by aa.id desc   )t  where rownum<= ? ) t1 where t1.rn>?";

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return Database.executeQuery(sql, lstpara.toArray());
	}

	/**
	 * @description 获取标准问题下排除问题记录数
	 * @param serviceid
	 * @param kbdataid
	 * @param customerQuery
	 * @param cityCode
	 * @return
	 * @returnType int
	 */
	public static int getRemoveQueryCount(String serviceid, String kbdataid, String customerQuery, String cityCode,
			String isTrain, String removequerystatus) {
		int count = -1;
		Result rs = null;
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "select count(*) count  from (select * from service where serviceid=? ) s  "
				+ " inner join (select * from kbdata where 1>0  ";
		lstpara.add(serviceid);
		if (!"".equals(kbdataid) && kbdataid != null) {
			sql = sql + "  and ( kbdataid = ? ) ";
			lstpara.add(kbdataid);
		}

		if (!"".equals(cityCode) && cityCode != null) {
			if ("全国".equals(cityCode)) {
				cityCode = "";
			} else if (cityCode.endsWith("0000")) {// 地市为省级加载省级下面的所有内容
				cityCode = cityCode.replace("0000", "");
			}
			lstpara.add("%" + cityCode + "%");
		} else {
			lstpara.add("%%");
		}
		lstpara.add("%" + customerQuery + "%");

		String statusSql, istrainSql;

		if (StringUtils.isBlank(removequerystatus)) { // 全部
			statusSql = "";
		} else if (removequerystatus.equals("none")) { // 未理解
			statusSql = " and status is null";
		} else {
			lstpara.add(removequerystatus);
			statusSql = " and isstrictexclusion = ?";
		}
		if (StringUtils.isBlank(isTrain)) { // 全部
			istrainSql = "";
		} else {
			lstpara.add(StringUtils.trim(isTrain));
			istrainSql = " and istrain = ?";
		}

		sql = sql
				+ "  ) k on s.serviceid = k.serviceid  inner join (select * from querymanage where querytype = 1 and city like ? and query like ? "
				+ statusSql + istrainSql + ") q  on k.kbdataid = q.kbdataid";
		rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		if (rs != null && rs.getRowCount() > 0) {
			count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		}
		return count;
	}

	/**
	 * @description 新增业务词词模
	 * @param list
	 * @param serviceType
	 * @param userid
	 * @return
	 * @returnType int
	 */
	public static int insertWordpatByBusiness(List<List<String>> list, String serviceType, String userid,
			String wordpattype) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		String brand = serviceType.split("->")[1];
		for (int i = 0; i < list.size(); i++) {
			List<String> tempList = list.get(i);
			String wordpat = tempList.get(0);
			String cityCode = tempList.get(1);
			String kbdataid = tempList.get(2);
			String wordpatid = "";
			sql = "delete from wordpat where wordpat like ?  and wordpattype=? and kbdataid=? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词模like查询的参数
			lstpara.add("%" + wordpat + "%");
			// 绑定问题类型参数,0代表普通词模,5代表自学习词模
			if (wordpattype == null || "".equals(wordpattype)) {
				lstpara.add("5");
			} else {
				lstpara.add(wordpattype);
			}
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 将删除词模的SQL语句放入SQL语句集合中
			lstSql.add(sql);
			// 将对应的参数集合放入集合中
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(userid + "#" + sql + "#" + lstpara);

			// 获取插入词模的序列
			if (GetConfigValue.isOracle) {
				// 获取词模表的序列值
				wordpatid = ConstructSerialNum.GetOracleNextVal("SEQ_WORDPATTERN_ID") + "";
				// 定义新增模板的SQL语句
				sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime,workerid) values(?,?,?,?,?,?,?,sysdate,?)";
			} else if (GetConfigValue.isMySQL) {
				// 获取词模表的序列值
				wordpatid = ConstructSerialNum.getSerialID("wordpat", "wordpatid") + "";
				// 定义新增模板的SQL语句
				sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime,workerid) values(?,?,?,?,?,?,?,sysdate(),?)";
			}
			// 根据配置信息补充需插入主键ID
			if (!"".equals(bussinessFlag)) {
				wordpatid = wordpatid + "." + bussinessFlag;
			}
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词模id参数
			lstpara.add(wordpatid);
			// 绑定词模参数
			lstpara.add(wordpat);
			// 绑定城市名称参数
			lstpara.add(cityCode);
			// 绑定自动开关参数
			lstpara.add("0");

			// // 绑定词模类型参数,0代表普通词模
			// lstpara.add("0");

			// 绑定问题类型参数,5代表自学习词模
			if (wordpattype == null || "".equals(wordpattype)) {
				lstpara.add("5");
			} else {
				lstpara.add(wordpattype);
			}

			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定品牌参数
			lstpara.add(brand);
			lstpara.add(userid);
			// 将插入词模的SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(userid + "#" + sql + "#" + lstpara);
		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}

	/**
	 * 插入词类词条
	 * 
	 * @param info
	 * @return
	 */
	public static String getWordInsert(User user, List<List<Object>> info) {

		String returnMsg = "导入成功！";
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义sql
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();

		// Map<wordclassid,词条>
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		// Map<wordclass, wordclassid>
		Map<String, String> nameToIdMap = new LinkedHashMap<String, String>();
		// wordpat词条
		List<String> wordclassList = new ArrayList<String>();

		// 获得商家标识符
		String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);

		// 获取配置表中的channel
		Result rs = CommonLibMetafieldmappingDAO.getConfigValue("问题库系统默认摘要配置", serviceType);
		List<String> configValueList = new ArrayList<String>();
		if (rs != null && rs.getRowCount() > 0) {
			for (int n = 0; n < rs.getRowCount(); n++) {
				String value = rs.getRows()[n].get("name").toString();
				configValueList.add(value);
			}
		} else {
			returnMsg = "缺少默认摘要，请联系管理员！";
			return returnMsg;
		}

		int index = 0;
		// 遍历每一行
		for (List<Object> line : info) {
			index++;
			// 词类
			String wordclass = line.get(0) == null ? "" : line.get(0).toString().replace("近类", "").trim();
			// 词条
			String word = line.get(1) == null ? "" : line.get(1).toString().trim();
			// 如果词类/词条为空，则略过这一行,词条长度大于4也略过这一行
			if ("".equals(wordclass)) {
				returnMsg = returnMsg + "<br/>第" + index + "条数据同义词为空！";
				continue;
			}
			// if (word.length() > 4){
			// returnMsg = returnMsg + "<br/>第" + index + "条同义问字数过长";
			// continue;
			// }
			// 词类未收集
			if (!nameToIdMap.containsKey(wordclass + "近类")) {
				String checkWordclassSql = "select wordclassid from wordclass where container='基础' and wordclass = '"
						+ wordclass + "近类" + "'";
				Result checkWordclassResult = Database.executeQuery(checkWordclassSql);
				// 库中存在该词类
				if (checkWordclassResult != null && checkWordclassResult.getRowCount() > 0) {
					String wordclassid = checkWordclassResult.getRows()[0].get("wordclassid").toString();
					String checkWordSql = "select * from word where wordclassid='" + wordclassid + "'";
					Result checkWordResult = Database.executeQuery(checkWordSql);
					// 词类存在，则把所有库中词条放入map中
					if (checkWordResult != null && checkWordResult.getRowCount() > 0) {
						List<String> wordList = new ArrayList<String>();
						for (int i = 0; i < checkWordResult.getRowCount(); i++) {
							wordList.add(checkWordResult.getRows()[i].get("word").toString());
						}
						// 同义词不在库中
						if (!wordList.contains(wordclass)) {
							sql = "";
							lstpara = new ArrayList<Object>();

							// 获取词条表的序列值
							String wordid = "";
							if (GetConfigValue.isOracle) {
								wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
							} else if (GetConfigValue.isMySQL) {
								wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
							}
							sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
							// 绑定id参数
							lstpara.add(wordid);
							// 绑定词类id参数
							lstpara.add(wordclassid);
							// 绑定词类名称
							lstpara.add(wordclass);
							// 绑定类型参数
							lstpara.add("标准名称");
							// 将SQL语句放入集合中
							lstSql.add(sql);
							// 将对应的绑定参数集合放入集合中
							lstLstpara.add(lstpara);

							wordList.add(wordclass);
						}
						// 词条不在库中
						if (!"".equals(word) && !wordList.contains(word)) {
							sql = "";
							lstpara = new ArrayList<Object>();

							// 获取词条表的序列值
							String wordid = "";
							if (GetConfigValue.isOracle) {
								wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
							} else if (GetConfigValue.isMySQL) {
								wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
							}
							sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
							// 绑定id参数
							lstpara.add(wordid);
							// 绑定词类id参数
							lstpara.add(wordclassid);
							// 绑定词类名称
							lstpara.add(word);
							// 绑定类型参数
							lstpara.add("标准名称");
							// 将SQL语句放入集合中
							lstSql.add(sql);
							// 将对应的绑定参数集合放入集合中
							lstLstpara.add(lstpara);

							wordList.add(word);
						}
						nameToIdMap.put(wordclass + "近类", wordclassid);
						map.put(wordclassid, wordList);
					}
				} else {// 库中不存在该词类，则新建词类

					String wordclassid = "";
					if (GetConfigValue.isOracle) {
						wordclassid = ConstructSerialNum.GetOracleNextValNew("seq_wordclass_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordclassid = ConstructSerialNum.getSerialIDNew("wordclass", "wordclassid", bussinessFlag);
					}
					// 插入词类的SQL语句
					sql = "insert into wordclass(wordclassid,wordclass,container) values(?,?,?)";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定id参数
					lstpara.add(wordclassid);
					// 绑定词类参数
					lstpara.add(wordclass + "近类");
					// 绑定类型参数
					lstpara.add("基础");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					wordclassList.add(wordclass + "近类");

					// 新建词类完成后添加对应的词条
					// 获取词条表的序列值
					String wordid = "";

					if (GetConfigValue.isOracle) {
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
					}
					sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称
					lstpara.add(wordclass);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					List<String> wordList = new ArrayList<String>();
					wordList.add(wordclass);

					if (!"".equals(word)) {
						if (GetConfigValue.isOracle) {
							wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
						} else if (GetConfigValue.isMySQL) {
							wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
						}
						sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
						// 定义绑定参数集合
						lstpara = new ArrayList<Object>();
						// 绑定id参数
						lstpara.add(wordid);
						// 绑定词类id参数
						lstpara.add(wordclassid);
						// 绑定词类名称
						lstpara.add(word);
						// 绑定类型参数
						lstpara.add("标准名称");
						// 将SQL语句放入集合中
						lstSql.add(sql);
						// 将对应的绑定参数集合放入集合中
						lstLstpara.add(lstpara);

						wordList.add(word);
					}

					nameToIdMap.put(wordclass + "近类", wordclassid);
					map.put(wordclassid, wordList);
				}
			} else {// 该词类已收集
				String wordclassid = nameToIdMap.get(wordclass + "近类");
				List<String> wordList = new ArrayList<String>();
				wordList = map.get(wordclassid);

				// 同义词不在库中
				if (!wordList.contains(wordclass)) {
					sql = "";
					lstpara = new ArrayList<Object>();

					// 获取词条表的序列值
					String wordid = "";
					if (GetConfigValue.isOracle) {
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
					}
					sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称
					lstpara.add(wordclass);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					wordList.add(wordclass);
				}

				// 词条未收集
				if (!wordList.contains(word)) {
					sql = "";
					lstpara = new ArrayList<Object>();

					// 获取词条表的序列值
					String wordid = "";
					if (GetConfigValue.isOracle) {
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
					}
					sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称
					lstpara.add(word);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					wordList.add(word);
					map.put(wordclassid, wordList);
				}
			}
		}

		// 插入词模
		if (wordclassList.size() > 0) {
			// 获取kbdataid
			String kbdataid = "";
			String kbdata = configValueList.get(0);
			sql = "select kbdataid from kbdata where abstract=?";
			Result kbdataRs = Database.executeQuery(sql, kbdata);
			if (kbdataRs != null && kbdataRs.getRowCount() > 0) {
				kbdataid = kbdataRs.getRows()[0].get("kbdataid").toString();
			} else {
				returnMsg = "缺少默认摘要，请联系管理员！";
				return returnMsg;
			}

			int num = wordclassList.size() / 5;
			String wordpat = "";
			for (int i = 0; i < (num - 1); i++) {
				wordpat = "";
				wordpat = "<!" + wordclassList.get(i * 5) + ">*" + "<!" + wordclassList.get(i * 5 + 1) + ">*" + "<!"
						+ wordclassList.get(i * 5 + 2) + ">*" + "<!" + wordclassList.get(i * 5 + 3) + ">*" + "<!"
						+ wordclassList.get(i * 5 + 4) + ">" + "@1#编者=\"系统默认\"";
				// 获取词模表的序列值
				String wordpatid = "";
				if (GetConfigValue.isOracle) {
					wordpatid = ConstructSerialNum.GetOracleNextValNew("SEQ_WORDPATTERN_ID", bussinessFlag);
				} else if (GetConfigValue.isMySQL) {
					wordpatid = ConstructSerialNum.getSerialIDNew("wordpat", "wordpatid", bussinessFlag);
				}
				sql = "insert into wordpat(wordpatid,kbdataid,wordpat,city,wordpattype,autosendswitch,brand,edittime) values (?,?,?,?,?,?,?,sysdate)";
				lstpara = new ArrayList<Object>();
				lstpara.add(wordpatid);
				lstpara.add(kbdataid);
				lstpara.add(wordpat);
				lstpara.add("全国");
				lstpara.add(4);
				lstpara.add(0);
				lstpara.add(serviceType.split("->")[1] + "问题库");
				// 将SQL语句放入集合中
				lstSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);
			}
			wordpat = "";
			// 添加剩下的词类
			for (int i = (num - 1) * 5 < 0 ? 0 : (num - 1) * 5; i < wordclassList.size(); i++) {
				wordpat = wordpat + "<!" + wordclassList.get(i) + ">*";
			}
			if (wordpat.contains("*")) {
				wordpat = wordpat.substring(0, wordpat.lastIndexOf("*")) + "@1#编者=\"系统默认\"";
				// 获取词模表的序列值
				String wordpatid = "";
				if (GetConfigValue.isOracle) {
					wordpatid = ConstructSerialNum.GetOracleNextValNew("SEQ_WORDPATTERN_ID", bussinessFlag);
				} else if (GetConfigValue.isMySQL) {
					wordpatid = ConstructSerialNum.getSerialIDNew("wordpat", "wordpatid", bussinessFlag);
				}
				sql = "insert into wordpat(wordpatid,kbdataid,wordpat,city,wordpattype,autosendswitch,brand,edittime) values (?,?,?,?,?,?,?,sysdate)";
				lstpara = new ArrayList<Object>();
				lstpara.add(wordpatid);
				lstpara.add(kbdataid);
				lstpara.add(wordpat);
				lstpara.add("全国");
				lstpara.add(4);
				lstpara.add(0);
				lstpara.add(serviceType.split("->")[1] + "问题库");
				// 将SQL语句放入集合中
				lstSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);
			}
		}
		int count = -1;
		count = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		System.out.println(count);
		if (count == 0) {
			return returnMsg + "！";
		} else if (count == -1) {
			return "导入失败";
		}
		return returnMsg;
	}

	/**
	 * 根据标准问摘要,问题库业务根获取标准问ID
	 * 
	 * @param normalAbstract
	 * @param brand
	 * @return
	 */
	public static String getKbDataIdByNormalQuery(String normalAbstract, String brand) {
		String sql = "select kbdataid,abstract from kbdata where serviceid in (select serviceid from service where brand='"
				+ brand + "') and abstract='" + normalAbstract + "'";
		// 文件日志
		GlobalValue.myLog.info(sql);
		String kbdataid = null;
		Result rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				kbdataid = rs.getRows()[i].get("kbdataid") == null ? ""
						: String.valueOf(rs.getRows()[i].get("kbdataid"));
				break;

			}
		}
		return kbdataid;
	}

	/**
	 * 标准问发现新词插入词类词条
	 * 
	 * @param info
	 * @return
	 */
	public static int insertWordClassAndItem2(String serviceType, List<List<Object>> info) {

		String returnMsg = "新增成功！";
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义sql
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();

		// Map<wordclassid,词条>
		Map<String, List<String>> map = new LinkedHashMap<String, List<String>>();
		// Map<wordclass, wordclassid>
		Map<String, String> nameToIdMap = new LinkedHashMap<String, String>();

		// 获得商家标识符
		// String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);

		int index = 0;
		// 遍历每一行
		for (List<Object> line : info) {
			index++;
			// 词类，如果包含英文统一转化为大写
			String wordclass = line.get(0) == null ? "" : line.get(0).toString().replace("近类", "").trim().toUpperCase();
			// 词条，如果包含英文统一转化为大写
			String word = line.get(1) == null ? "" : line.get(1).toString().trim().toUpperCase();
			// 如果词类/词条为空，则略过这一行,词条长度大于4也略过这一行
			if ("".equals(wordclass)) {
				returnMsg = returnMsg + "<br/>第" + index + "条数据同义词为空！";
				continue;
			}
			// 词类未收集
			if (!nameToIdMap.containsKey(wordclass + "近类")) {
				String checkWordclassSql = "select wordclassid from wordclass where container='基础' and wordclass = '"
						+ wordclass + "近类" + "'";
				Result checkWordclassResult = Database.executeQuery(checkWordclassSql);
				// 库中存在该词类
				if (checkWordclassResult != null && checkWordclassResult.getRowCount() > 0) {
					String wordclassid = checkWordclassResult.getRows()[0].get("wordclassid").toString();
					String checkWordSql = "select * from word where wordclassid='" + wordclassid + "'";
					Result checkWordResult = Database.executeQuery(checkWordSql);
					// 词类存在，则把所有库中词条放入map中
					if (checkWordResult != null && checkWordResult.getRowCount() > 0) {
						List<String> wordList = new ArrayList<String>();
						for (int i = 0; i < checkWordResult.getRowCount(); i++) {
							wordList.add(checkWordResult.getRows()[i].get("word").toString());
						}
						// 同义词不在库中
						if (!wordList.contains(wordclass)) {
							sql = "";
							lstpara = new ArrayList<Object>();

							// 获取词条表的序列值
							String wordid = "";
							if (GetConfigValue.isOracle) {
								wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
							} else if (GetConfigValue.isMySQL) {
								wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
							}
							sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
							// 绑定id参数
							lstpara.add(wordid);
							// 绑定词类id参数
							lstpara.add(wordclassid);
							// 绑定词类名称
							lstpara.add(wordclass);
							// 绑定类型参数
							lstpara.add("标准名称");
							// 将SQL语句放入集合中
							lstSql.add(sql);
							// 将对应的绑定参数集合放入集合中
							lstLstpara.add(lstpara);

							wordList.add(wordclass);
						}
						// 词条不在库中
						if (!"".equals(word) && !wordList.contains(word)) {
							sql = "";
							lstpara = new ArrayList<Object>();

							// 获取词条表的序列值
							String wordid = "";
							if (GetConfigValue.isOracle) {
								wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
							} else if (GetConfigValue.isMySQL) {
								wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
							}
							sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
							// 绑定id参数
							lstpara.add(wordid);
							// 绑定词类id参数
							lstpara.add(wordclassid);
							// 绑定词类名称
							lstpara.add(word);
							// 绑定类型参数
							lstpara.add("标准名称");
							// 将SQL语句放入集合中
							lstSql.add(sql);
							// 将对应的绑定参数集合放入集合中
							lstLstpara.add(lstpara);

							wordList.add(word);
						}
						nameToIdMap.put(wordclass + "近类", wordclassid);
						map.put(wordclassid, wordList);
					}
				} else {// 库中不存在该词类，则新建词类

					String wordclassid = "";
					if (GetConfigValue.isOracle) {
						wordclassid = ConstructSerialNum.GetOracleNextValNew("seq_wordclass_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordclassid = ConstructSerialNum.getSerialIDNew("wordclass", "wordclassid", bussinessFlag);
					}
					// 插入词类的SQL语句
					sql = "insert into wordclass(wordclassid,wordclass,container) values(?,?,?)";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定id参数
					lstpara.add(wordclassid);
					// 绑定词类参数
					lstpara.add(wordclass + "近类");
					// 绑定类型参数
					lstpara.add("基础");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					// 新建词类完成后添加对应的词条
					// 获取词条表的序列值
					String wordid = "";

					if (GetConfigValue.isOracle) {
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
					}
					sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称
					lstpara.add(wordclass);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					List<String> wordList = new ArrayList<String>();
					wordList.add(wordclass);

					if (!"".equals(word) && !wordclass.equals(word)) {
						if (GetConfigValue.isOracle) {
							wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
						} else if (GetConfigValue.isMySQL) {
							wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
						}
						sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
						// 定义绑定参数集合
						lstpara = new ArrayList<Object>();
						// 绑定id参数
						lstpara.add(wordid);
						// 绑定词类id参数
						lstpara.add(wordclassid);
						// 绑定词类名称
						lstpara.add(word);
						// 绑定类型参数
						lstpara.add("标准名称");
						// 将SQL语句放入集合中
						lstSql.add(sql);
						// 将对应的绑定参数集合放入集合中
						lstLstpara.add(lstpara);

						wordList.add(word);
					}

					nameToIdMap.put(wordclass + "近类", wordclassid);
					map.put(wordclassid, wordList);
				}
			} else {// 该词类已收集
				String wordclassid = nameToIdMap.get(wordclass + "近类");
				List<String> wordList = new ArrayList<String>();
				wordList = map.get(wordclassid);

				// 同义词不在库中
				if (!wordList.contains(wordclass)) {
					sql = "";
					lstpara = new ArrayList<Object>();

					// 获取词条表的序列值
					String wordid = "";
					if (GetConfigValue.isOracle) {
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
					}
					sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称
					lstpara.add(wordclass);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					wordList.add(wordclass);
				}

				// 词条未收集
				if (!wordList.contains(word)) {
					sql = "";
					lstpara = new ArrayList<Object>();

					// 获取词条表的序列值
					String wordid = "";
					if (GetConfigValue.isOracle) {
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
					}
					sql = "insert into word (wordid,wordclassid,word,type) values (?,?,?,?)";
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称
					lstpara.add(word);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);

					wordList.add(word);
					map.put(wordclassid, wordList);
				}
			}
		}

		int count = -1;
		count = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		if (count == 0) {
			returnMsg = returnMsg + "！";
		}
		System.out.println(returnMsg);

		// if (count == 0){
		// return returnMsg + "！";
		// }else if (count == -1){
		// return "导入失败";
		// }
		return count;
	}

	/**
	 * @description 新增标准问题及客户问题,针对场景
	 * @param serviceid
	 * @param normalQuery
	 * @param customerQuery
	 * @param cityCode
	 * @param serviceType
	 * @param workerid
	 * @param userCityCode
	 * @param serviceCityCode
	 *            业务地市
	 * @param returnValue
	 *            词模返回值
	 * @return
	 * @returnType int
	 */
	public static int addNormalQueryAndCustomerQueryByScene(String serviceid, String normalQuery, String cityCode,
			User user, String userCityCode, String serviceCityCode, String returnValue) {
		String updateSql = "update kbdata set city =? where kbdataid =? ";
		String updateWordpatSql = " update wordpat set city =? where kbdataid =? and city =? and wordpat not like '%来源=%'";
		String insertSql = "insert into querymanage(ID,KBDATAID,QUERY,CITY,WORKERID) values(?,?,?,?,?)";
		String insertKbdataSql = "insert into kbdata(serviceid,kbdataid,topic,abstract,city,returnvalue) values (?,?,?,?,?,?)";

		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		List<Object> lstpara = new ArrayList<Object>();
		int rs = -1;
		String kbdataid = "";
		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());

		cityCode = new String(serviceCityCode); // 用户地市改成业务地市 add by zhao
												// lipeng.

		Map<String, Map<String, String>> map = getNormalQueryDic(serviceid);
		if (!map.containsKey(normalQuery)) {// 标准问题已存在, update标准问city
			if (GetConfigValue.isOracle) {
				kbdataid = ConstructSerialNum.GetOracleNextValNew("SEQ_KBDATA_ID", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				kbdataid = ConstructSerialNum.getSerialIDNew("kbdata", "kbdataid", bussinessFlag);
			}
			String service = CommonLibServiceDAO.getNameByserviceid(serviceid);
			String abs = "<" + service + ">" + normalQuery;
			// 新增摘要
			lstpara = new ArrayList<Object>();
			lstpara.add(serviceid);
			lstpara.add(kbdataid);
			lstpara.add("常见问题");
			lstpara.add(abs);
			// 标准问默认取业务地市
			lstpara.add(serviceCityCode);
			// 新增返回值 --update by sundj 20191106
			lstpara.add(returnValue);

			lstsql.add(insertKbdataSql);
			lstlstpara.add(lstpara);

			// 文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + insertKbdataSql + "#" + lstpara);

			// 日志 insert into
			// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
			lstsql.add(getInsertLogSql());
			lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), service, "新增标准问题", " ", user.getUserID(),
					user.getUserName(), abs, "KBDATA"));

			String querymanageId = "";

			// 新增和摘要相同的客户问题（避免重复添加标准问扩展问）
			if (GetConfigValue.isOracle) {
				querymanageId = ConstructSerialNum.GetOracleNextValNew("seq_querymanage_id", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				querymanageId = ConstructSerialNum.getSerialIDNew("querymanage", "id", bussinessFlag);
			}
			lstpara = new ArrayList<Object>();
			lstpara.add(querymanageId);
			lstpara.add(kbdataid);
			lstpara.add(normalQuery);
			// lstpara.add(userCityCode);
			// 未选择地市时取业务地市
			if (cityCode == null || "".equals(cityCode)) {
				cityCode = serviceCityCode;
			}
			lstpara.add(cityCode);
			lstpara.add(user.getUserID());
			lstsql.add(insertSql);
			lstlstpara.add(lstpara);
			// 文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + insertSql + "#" + lstpara);

			// 日志 insert into
			// operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename
			lstsql.add(getInsertLogSql());
			lstlstpara.add(getSQLParams(user.getUserIP(), user.getBrand(), service, "增加客户问题", " ", user.getUserID(),
					user.getUserName(), normalQuery, "QUERYMANAGE"));

			return Database.executeNonQueryTransaction(lstsql, lstlstpara);
		} else {
			return -2;
		}
	}
	
	/**
	 * 得到标准问需要额外增加的返回值
	 * @param kbdataIdList
	 */
	public static Result getReturnValueByKbdataId(List<String> kbdataIds){
		String sql = "select kbdataid,returnvalue from kbdata where  (";
		if(kbdataIds.isEmpty()){
			return null;
		}
		// 绑定参数
		for (int i = 0; i < kbdataIds.size(); i++) {
			if (i == 0) {
				sql += "kbdataid = " + kbdataIds.get(i);
			} else {
				sql += " or kbdataid = " + kbdataIds.get(i);
			}
		}
		sql += " )";
		// 文件日志
		GlobalValue.myLog.info(sql );
		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	/**
	 * 得到标准问需要额外增加的返回值
	 * @param kbdataIdList
	 */
	public static Result getNormalQuery(String serviceid,String normalquery){
		String sql = "select * from kbdata where serviceid= "+serviceid;
		
		sql += " and abstract like '%>"+normalquery+"'";
		// 文件日志
		GlobalValue.myLog.info(sql );
		Result rs = null;
		try {
			// 执行sql语句
			rs = Database.executeQuery(sql);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rs;
	}

}
