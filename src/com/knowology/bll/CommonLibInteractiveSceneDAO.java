package com.knowology.bll;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.axis2.databinding.types.soapencoding.Array;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.knowology.dal.MysqlTransfer;

/**
 * 场景操作类
 * 
 */
public class CommonLibInteractiveSceneDAO {
	public static Logger logger = Logger.getLogger("sqllog");

	/**
	 * 构造场景树
	 * 
	 * @param scenariosid
	 *            场景id
	 * @param serviceType
	 *            行业
	 * @return Result
	 */
	public static Result createInteractiveSceneTree(String scenariosid,
	String serviceType) {
	String sql = "";
	if ("".equals(scenariosid) || scenariosid == null) {
	sql = "select scenariosid, name from scenarios where parentid is null and servicetype ='"
	+ serviceType + "' order by scenariosid ";
	} else {
	sql = "select scenariosid , name  from scenarios where parentid="
	+ scenariosid + " order by scenariosid";
	}
	Result rs = null;
	rs = Database.executeQuery(sql);

	return rs;
	}
	
	
	/**
	 * 构造场景树
	 * 
	 * @param scenariosid
	 *            场景id
	 * @param serviceType
	 *            行业
	 * @return Result
	 */
	public static Result createInteractiveSceneTreeNew(String scenariosid,
			String serviceType, String citySelect, String userID) {
		String sql = "";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		if ("".equals(scenariosid) || scenariosid == null) {
			sql = "select serviceid as scenariosid, service as name from service where  parentid='0' and brand ='"
					+ brand + "' ";
			if (citySelect != null && !"全国".equals(citySelect)
					&& !"".equals(citySelect)) {
				sql = sql + " and (city like '%" + citySelect
						+ "%' or city is null or city = '全国')";
			}
			sql = sql + " order by serviceid";
		} else {
			sql = "select serviceid as scenariosid, service as name from service where serviceid in (select resourceid from  role_resource where resourcetype='scenariosrules' and servicetype='" 
					+ serviceType
					+ "' and operationtype like '%S%' and roleid=(select roleid from workerrolerel where workerid='"
					+ userID
					+"') ) and parentid="
					+ scenariosid + " ";
			if (citySelect != null && !"全国".equals(citySelect)
					&& !"".equals(citySelect)) {
				sql = sql + " and (city like '%" + citySelect
						+ "%' or city is null or city = '全国')";
			}
			sql = sql + " order by serviceid";
		}
		Result rs = null;
		rs = Database.executeQuery(sql);

		return rs;
	}

	/**
	 * 查询子场景记录数
	 * 
	 * @param scenariosid
	 *            场景id
	 * @return int
	 */
	public static int hasChild(String scenariosid) {
	int count = 0;
	String sql = "select count(*) as nums from scenarios where parentid =?";

	Result rs = Database.executeQuery(sql,scenariosid);
	if (rs != null) {
	count = Integer.parseInt(rs.getRows()[0].get("nums").toString());
	}
	return count;
	}
	
	/**
	 * 查询子场景记录数
	 * 
	 * @param scenariosid
	 *            场景id
	 * @return int
	 */
	public static int hasChildNew(String scenariosid) {
		int count = 0;
		String sql = "select count(*) as nums from service where parentid ="
				+ scenariosid;
		Result rs = Database.executeQuery(sql);
		if (rs != null) {
			count = Integer.parseInt(rs.getRows()[0].get("nums").toString());
		}
		return count;
	}

	/**
	 * 查看相同名场景名称
	 * 
	 * @param name
	 *            场景名称
	 * @param serviceType
	 *            四层结构串
	 * @return int
	 */
	public static int isExistSceneName(String name, String serviceType,String serviceid) {
		String tempSql = "select city from service where serviceid='" + serviceid + "'";
		Result tempRs = Database.executeQuery(tempSql);
		String city = "";
		if (tempRs != null && tempRs.getRowCount() > 0){
			city = tempRs.getRows()[0].get("city") == null ? "全国" :  tempRs.getRows()[0].get("city").toString();
		}
		city = city.substring(0, 2);
		int count = 0;
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		String sql = "";
		if ("".equals(city) || "全国".equals(city)){
			sql = "select count(*) as nums from service where service='"
				+ name + "' and brand='" + brand + "' and (city is null or city='全国')";
		}else {
			sql = "select count(*) as nums from service where service='"
				+ name + "' and brand='" + brand + "' and city like '%" + city + "0000%'";
		}

		Result rs = Database.executeQuery(sql);
		if (rs != null) {
			count = Integer.parseInt(rs.getRows()[0].get("nums").toString());
		}
		return count;
	}

	/**
	 *@description 判断场景元素值是否存在
	 *@param name
	 *            元素值
	 *@param scenariosid
	 *            场景ID
	 *@return
	 *@returnType boolean
	 */
	public static boolean isExistSceneElement(String name, String scenariosid) {
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String sql = "select * from scenarioselement where name=? and relationserviceid=? ";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定问题要素名称参数
		lstpara.add(name);
		// 绑定场景id参数
		lstpara.add(scenariosid);
		lsts.add(sql);
		lstlstpara.add(lstpara);
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		// 判断数据源不为空且含有数据
		if (rs != null && rs.getRowCount() > 0) {// 场景元素已存在
			return true;
		}
		return false;
	}

	/**
	 *查看相同场景场景业务摘要对应关系
	 * 
	 * @param service
	 *            业务名
	 *@param serviceid
	 *            业务ID
	 *@param abstractid
	 *            摘要ID
	 *@param abs
	 *            摘要
	 *@param userquery
	 *            用户问题
	 *@return
	 *@returnType int
	 */
	public static int isExistSceneRelation(String service, String serviceid,
			String abstractid, String abs, String userquery) {
		int count = 0;
		String sql = "";
		if ("".equals(abs) || abstractid == null) {
			sql = "select count(*) as nums from Scenarios2kbdata where service ='"
					+ service
					+ "' and serviceid = "
					+ serviceid
					+ " and abstract is null  and  abstractid is null ";
		} else {
			sql = "select count(*) as nums from Scenarios2kbdata where service ='"
					+ service
					+ "' and serviceid = "
					+ serviceid
					+ " and abstract ='"
					+ abs
					+ "' and abstractid = "
					+ abstractid + "";
		}
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		if (rs != null) {
			count = Integer.parseInt(rs.getRows()[0].get("nums").toString());
		}
		return count;
	}

	/**
	 *插入场景名称
	 * 
	 * @param scenariosid
	 *            场景主键ID
	 *@param scenarioselementid
	 *            场景主键元素ID
	 *@param parentid
	 *            父节点ID
	 *@param name
	 *            场景名称
	 *@param serviceType
	 *            四层结构串
	 *@param wordclass
	 *            词类名称
	 *@return
	 *@returnType int
	 */
	public static int insertSceneName(String scenariosid, String parentid,
			String name, String serviceType, String wordclass) {
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		String sql = "insert into service (serviceid,service,parentid,brand) values (?,?,?,?)";
		// 绑定参数
		lstpara.add(scenariosid);
		lstpara.add(name);
		lstpara.add(parentid);
		lstpara.add(brand);
		lsts.add(sql);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		

		// 同步默认添加场景知识名称默认元素
		String scenarioselementid = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		String elementName = "";
		if (name.endsWith("场景")) {
			elementName = name;
		} else {
			elementName = name + "场景";
		}
		// if(!isExistSceneElement(name,scenariosid)){
		sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,relationserviceid,WEIGHT,ITEMMODE,ISSHARE,city,cityname,CONTAINER,wordclassid) values (?,?,?,?,?,?,?,?,?,(select wordclassid from wordclass where wordclass=?))";
		// 获得主键ID
		if (GetConfigValue.isOracle) {
			scenarioselementid = ConstructSerialNum.GetOracleNextValNew(
					"seq_scenarioselement_id", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			scenarioselementid = ConstructSerialNum.getSerialIDNew(
					"scenarioselement", "scenarioselementid", bussinessFlag);
		}
		lstpara = new ArrayList<Object>();
		lstpara.add(scenarioselementid);
		lstpara.add(elementName + "_知识名称");
		lstpara.add(scenariosid);
		lstpara.add(1);
		lstpara.add("勾选+自定义");
		lstpara.add("是");
		lstpara.add("全国");
		lstpara.add("全国");
		lstpara.add("键值补全");
		lstpara.add(wordclass);
		lsts.add(sql);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		// }

		// if(!isExistSceneElement("常见问题",scenariosid)){
		// 同步默认添加场景常见问题默认元素
		sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,relationserviceid,WEIGHT,ITEMMODE,ISSHARE,city,cityname,CONTAINER) values (?,?,?,?,?,?,?,?,?)";
		// 获得主键ID
		if (GetConfigValue.isOracle) {
			scenarioselementid = ConstructSerialNum.GetOracleNextValNew(
					"seq_scenarioselement_id", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			scenarioselementid = ConstructSerialNum.getSerialIDNew(
					"scenarioselement", "scenarioselementid", bussinessFlag);
		}
		lstpara = new ArrayList<Object>();
		lstpara.add(scenarioselementid);
		lstpara.add("常见问题");
		lstpara.add(scenariosid);
		lstpara.add(2);
		lstpara.add("勾选+自定义");
		lstpara.add("是");
		lstpara.add("全国");
		lstpara.add("全国");
		lstpara.add("键值补全");
		lsts.add(sql);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		// }
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		return c;
	}

	/**
	 *插入场景父子句、场景名称及场景元素：知识名称
	 * 
	 * @param scenariosid
	 *            场景主键ID
	 *@param scenarioselementid
	 *            场景主键元素ID
	 *@param parentid
	 *            父节点ID
	 *@param name
	 *            场景名称
	 *@param serviceType
	 *            四层结构串
	 *@param wordclass
	 *            词类名称
	 *@return
	 *@returnType int
	 */
	public static int insertSceneInfo(User user, String scenariosid,
			String parentid, String name, String serviceType, String wordclass) {
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
//		String brand = serviceType.split("->")[1] + "场景";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}		
		String sql = "insert into service(serviceid,service,parentid,brand,city) values(?,?,?,?,(select city from service where serviceid=?))";
		// 绑定参数
		lstpara.add(scenariosid);
		lstpara.add(name);
		lstpara.add(parentid);
		lstpara.add(brand);
		lstpara.add(parentid);
		// lstpara.add(serviceType);
		lsts.add(sql);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ scenariosid + ","+ name + ","+ parentid + ","+ brand + ","+ parentid);
		
		String scenarioselementid = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		String wordcalssid = "";
		
		// 获取场景要素配置
		Result configRs = CommonLibMetafieldmappingDAO.getConfigValue("场景要素配置", serviceType);
		if (configRs != null && configRs.getRowCount() > 0){
			for (int i = 0 ; i < configRs.getRowCount() ; i++){
				// 最多十个配置
				if (i >= 20){
					break;
				}
				if (GetConfigValue.isOracle) {
					scenarioselementid = ConstructSerialNum.GetOracleNextValNew(
							"seq_scenarioselement_id", bussinessFlag);
				} else if (GetConfigValue.isMySQL) {
					scenarioselementid = ConstructSerialNum.getSerialIDNew(
							"scenarioselement", "scenarioselementid", bussinessFlag);
				}
				String element = configRs.getRows()[i].get("name").toString();
			
				// 需要添加关联词类的
				if (element.contains("::")){
					// 获得主键ID
					if (GetConfigValue.isOracle) {
						wordcalssid = ConstructSerialNum.GetOracleNextValNew(
								"seq_wordclass_id", bussinessFlag);
					} else if (GetConfigValue.isMySQL) {
						wordcalssid = ConstructSerialNum.getSerialIDNew("wordclass",
								"wordclassid", bussinessFlag);
					}
					String elementName = "";
					if (name.endsWith("场景")) {
						elementName = name;
					} else {
						elementName = name + "场景";
					}
					if (!CommonLibWordclassDAO.exist(elementName + element.split("::")[1].replace("场景", ""))) {
						// 插入父子句词类的SQL语句
						sql = "insert into wordclass(wordclassid,wordclass,container) values(?,?,?)";
						// 定义绑定参数集合
						lstpara = new ArrayList<Object>();
						// 绑定id参数
						lstpara.add(wordcalssid);
						// 绑定词类参数
						lstpara.add(elementName + element.split("::")[1].replace("场景", ""));
						// 绑定类型参数
						lstpara.add("子句");
						// 将SQL语句放入集合中
						lsts.add(sql);
						// 将对应的绑定参数集合放入集合中
						lstlstpara.add(lstpara);
						sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,relationserviceid,WEIGHT,ITEMMODE,ISSHARE,wordclassid,city,cityname,CONTAINER) values (?,?,?,?,?,?,?,?,?,?)";
//						sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,SCENARIOSID,WEIGHT,ITEMMODE,ISSHARE,wordclassid,city,cityname,CONTAINER) values (?,?,?,?,?,?,?,?,?,?)";
						lstpara = new ArrayList<Object>();
						lstpara.add(scenarioselementid);
						if (element.split("::")[0].contains("场景")){
							lstpara.add(elementName + "_" + element.split("::")[0].replace("场景", ""));
						}else{
							lstpara.add(element.split("::")[0]);
						}
						lstpara.add(scenariosid);
						lstpara.add(1 + i);
						lstpara.add("勾选+自定义");
						lstpara.add("是");
						lstpara.add(wordcalssid);
						lstpara.add("全国");
						lstpara.add("全国");
						lstpara.add("键值补全");
						lsts.add(sql);
						lstlstpara.add(lstpara);

					}else{
						//词类已存在，只需要添加关系
						sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,relationserviceid,WEIGHT,ITEMMODE,ISSHARE,city,cityname,CONTAINER,wordclassid) values (?,?,?,?,?,?,?,?,?,(select wordclassid from wordclass where wordclass=?))";
						lstpara = new ArrayList<Object>();
						lstpara.add(scenarioselementid);
						if (element.split("::")[0].contains("场景")){
							lstpara.add(elementName + "_" + element.split("::")[0].replace("场景", ""));
						}else{
							lstpara.add(element.split("::")[0]);
						}
						lstpara.add(scenariosid);
						lstpara.add(1 + i);
						lstpara.add("勾选+自定义");
						lstpara.add("是");
						lstpara.add("全国");
						lstpara.add("全国");
						lstpara.add("键值补全");
						lstpara.add(elementName + element.split("::")[1].replace("场景", ""));
						lsts.add(sql);
						lstlstpara.add(lstpara);
					}
				}else {
					// 根据配置添加场景要素
					sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,relationserviceid,WEIGHT,ITEMMODE,ISSHARE,city,cityname,CONTAINER,wordclassid) values (?,?,?,?,?,?,?,?,?,(select wordclassid from wordclass where wordclass=?))";
//				sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,SCENARIOSID,WEIGHT,ITEMMODE,ISSHARE,city,cityname,CONTAINER,wordclassid) values (?,?,?,?,?,?,?,?,?,(select wordclassid from wordclass where wordclass=?))";
					lstpara = new ArrayList<Object>();
					lstpara.add(scenarioselementid);
					lstpara.add(element);
					lstpara.add(scenariosid);
					lstpara.add(1 + i);
					lstpara.add("勾选+自定义");
					lstpara.add("是");
					lstpara.add("全国");
					lstpara.add("全国");
					lstpara.add("键值补全");
					lstpara.add(null);
					lsts.add(sql);
					lstlstpara.add(lstpara);
				}
				
			}
		}
		
		
		// 将操作日志SQL语句放入集合中
		lsts.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), brand, name, "增加交互场景", name,
				"SCENARIOS"));

		
		
		String logSqls = "";
		for (String logSql : lsts) {
			logSqls += logSql;
			logSqls += ";";
		}
		String logparas = "";
		for (List<?> loglstpara : lstlstpara) {
			for (Object logpara : loglstpara) {
				logparas += logpara == null ? "" : logpara.toString();
				logparas += ";";
			}
			logparas += "|";
		}
		logger.info("sql:" + logSqls + "---" + logparas);

		// 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		return c;
	}

	/**
	 *更新场景文档名称
	 * 
	 * @param scenariosid
	 *            场景主键ID
	 *@param name
	 *            文档名称
	 *@return
	 *@returnType int
	 */
	public static int updateDocname(String scenariosid, String name) {
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String sql = "update service set docname=? where serviceid=?";
		// String sql = "update scenarios set docname=? where scenariosid=? ";
		// 绑定参数
		lstpara.add(name);
		lstpara.add(scenariosid);
		lsts.add(sql);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		return c;
	}

	/**
	 *插入场景名称
	 * 
	 * @param scenariosid
	 *            场景主键ID
	 *@param name
	 *            文档名称
	 *@return
	 *@returnType String
	 */
	public static String getDocname(String scenariosid) {
		String sql = "select docname from service where serviceid ="
		// String sql = "select docname from  scenarios where scenariosid= "
				+ scenariosid;
		String docname = "";
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		if (rs != null) {
			docname = rs.getRows()[0].get("docname") == null ? "" : rs
					.getRows()[0].get("docname").toString();
		}
		return docname;
	}

	/**
	 *插入场景业务摘要关系
	 * 
	 * @param scenarios2kbdataid
	 *            场景关系表主键ID
	 *@param scenariosid
	 *            场景ID
	 *@param service
	 *            业务名
	 *@param serviceid
	 *            业务ID
	 *@param abstractid
	 *            摘要ID
	 *@param abs
	 *            摘要
	 *@param userquery
	 *            用户问题
	 *@return
	 *@returnType int
	 */
	public static int insertSceneRelation(User user, String serviceType,
			String scenarios2kbdataid, String scenariosid, String name,
			String service, String serviceid, String abstractid, String abs,
			String userquery) {
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String sql = " insert into Scenarios2kbdata(Scenarios2kbdataid,relationserviceid,abstractid,abstract,serviceid,service,userquery)  values(?,?,?,?,?,?,?)";
//		String sql = " insert into Scenarios2kbdata(Scenarios2kbdataid,Scenariosid,abstractid,abstract,serviceid,service,userquery)  values(?,?,?,?,?,?,?)";
		// 绑定参数
		lstpara.add(scenarios2kbdataid);
		lstpara.add(scenariosid);
		lstpara.add(abstractid);
		lstpara.add(abs);
		lstpara.add(serviceid);
		lstpara.add(service);
		lstpara.add(userquery);
		lsts.add(sql);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ scenarios2kbdataid + ","+ scenariosid + ","+ abstractid + ","
				+ abs + ","+ serviceid + service + ","+ userquery);

		// 将操作日志SQL语句放入集合中
		lsts.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
//		String brand = serviceType.split("->")[1] + "场景";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		String _object = "摘要：" + abs + "，业务：" + service;
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), brand, name, "增加场景业务摘要对应关系",
				_object, "SCENARIOS2KBDATA"));

		// 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		return c;
	}

	/**
	 *删除场景名称
	 * 
	 * @param scenariosid
	 *            主键ID
	 *@param name
	 *            场景名称
	 *@param serviceType
	 *            四层结构串
	 *@return
	 *@returnType int
	 */
	public static int deleteSceneName(User user, String scenariosid,
			String name, String serviceType) {
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
//		String brand = serviceType.split("->")[1] + "场景";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		String sql = " delete  from  service where serviceid =? and service =? and brand =?";
		// String sql =
		// " delete  from  scenarios where scenariosid =? and name =? and servicetype =? ";
		// 绑定参数
		lstpara.add(scenariosid);
		lstpara.add(name);
		lstpara.add(brand);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ scenariosid + ","+ name + ","+ brand );
		
		lsts.add(sql);
		lstlstpara.add(lstpara);

		// 将操作日志SQL语句放入集合中
		lsts.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), brand, name, "删除交互场景", name,
				"SCENARIOS"));

		// 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		return c;
	}

	/**
	 *删除场景业务摘要对应关系
	 * 
	 * @param scenerelationid
	 *            主键ID
	 *@return
	 *@returnType int
	 */
	public static int deleteSceneRelation(User user, String serviceType,
			String scenerelationid, String abs, String service, String name) {
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		String sql = " delete  from  Scenarios2kbdata where Scenarios2kbdataid =?  ";
		// 绑定参数
		lstpara.add(scenerelationid);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ scenerelationid );
		
		lsts.add(sql);
		lstlstpara.add(lstpara);

		// 将操作日志SQL语句放入集合中
		lsts.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
//		String brand = serviceType.split("->")[1] + "场景";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		String _object = "摘要：" + abs + "，业务：" + service;
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), brand, name, "删除场景业务摘要对应关系",
				_object, "SCENARIOS2KBDATA"));

		// 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		return c;
	}

	/**
	 *查询满足条件的场景关系信息记录数
	 * 
	 * @param scenariosid
	 *            场景ID
	 *@param serviceid
	 *            业务ID
	 *@param abstractid
	 *            摘要ID
	 *@param userquery
	 *            用户问题
	 *@return
	 *@returnType int
	 */
	public static int getSceneRelationCount(String scenariosid,
			String serviceid, String abstractid, String userquery, User user) {
		int count = 0;
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		List<Object> lstpara = new ArrayList<Object>();
		// 判断数据源不为空且含有数据
		sql = new StringBuilder();
		// 定义分页查询满足条件的问题要素名称SQL语句
		sql.append(" select count(*) nums from  service s,Scenarios2kbdata sk,kbdata k where  s.serviceid = sk.relationserviceid and k.kbdataid=sk.abstractid ");
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		sql.append(" and sk.relationserviceid = ? ");
		lstpara.add(scenariosid);
		if (!"".equals(serviceid) && serviceid != null) {
			sql.append(" and sk.serviceid = ? ");
			lstpara.add(serviceid);
		}
		if (!"".equals(abstractid) && abstractid != null) {
			sql.append(" and sk.abstractid = ? ");
			lstpara.add(abstractid);
		}
		if (!"".equals(userquery) && userquery != null) {
			sql.append(" and sk.userquery like ? ");
			lstpara.add("%" + userquery + "%");
		}
		
		String customer = user.getCustomer();
		if (!"全行业".equals(customer)){
			HashMap<String, ArrayList<String>> resourseMap = CommonLibPermissionDAO.resourseAccess(user.getUserID(), "scenariosrules", "S");
			List<String> cityList = new ArrayList<String>();
			String cityCode = "";
			// 该操作类型用户能够操作的资源
			cityList = resourseMap.get("地市");
			if (cityList != null) {
				cityCode = cityList.get(0);
				}
			sql.append(" and k.city like ? and k.city not like '%0000%0000%'");
			lstpara.add("%" + cityCode + "%");
		}
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		// 判断数据源不为空且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			count = Integer.parseInt(rs.getRows()[0].get("nums").toString());
		}
		return count;
	}

	/**
	 * 分页查询满足条件的问题要素信息
	 * 
	 *@param scenariosid
	 *            场景ID
	 *@param serviceid
	 *            业务ID
	 *@param abstractid
	 *            摘要ID
	 *@param userquery
	 *            用户问题
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return jResult
	 */
	public static Result getgetSceneRelation(String scenariosid,
			String serviceid, String abstractid, String userquery, int page,
			int rows, User user) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		List<Object> lstpara = new ArrayList<Object>();
		// 将SQL语句补充完整
		if (GetConfigValue.isOracle) {
			// 判断数据源不为空且含有数据
			sql = new StringBuilder();
			// 定义分页查询满足条件的问题要素名称SQL语句
			sql.append(" SELECT * FROM ( SELECT A.*, ROWNUM RN FROM ( select s.service as name,sk.*,k.abstract query,k.city from  service s,Scenarios2kbdata sk,kbdata k where  s.serviceid = sk.relationserviceid and k.kbdataid=sk.abstractid ");
//			sql.append(" SELECT * FROM ( SELECT A.*, ROWNUM RN FROM ( select s.name,sk.* from  Scenarios s,Scenarios2kbdata sk where  s.Scenariosid = sk.Scenariosid  ");
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			sql.append(" and sk.relationserviceid = ? ");
//			sql.append(" and sk.scenariosid = ? ");
			lstpara.add(scenariosid);
			if (!"".equals(serviceid) && serviceid != null) {
				sql.append(" and sk.serviceid = ? ");
				lstpara.add(serviceid);
			}
			if (!"".equals(abstractid) && abstractid != null) {
				sql.append(" and sk.abstractid = ? ");
				lstpara.add(abstractid);
			}
			if (!"".equals(userquery) && userquery != null) {
				sql.append(" and sk.userquery like ? ");
				lstpara.add("%" + userquery + "%");
			}
			
			String customer = user.getCustomer();
			if (!"全行业".equals(customer)){
				HashMap<String, ArrayList<String>> resourseMap = CommonLibPermissionDAO.resourseAccess(user.getUserID(), "scenariosrules", "S");
				List<String> cityList = new ArrayList<String>();
				String cityCode = "";
				// 该操作类型用户能够操作的资源
				cityList = resourseMap.get("地市");
				if (cityList != null) {
					cityCode = cityList.get(0);
					}
				sql.append(" and k.city like ? and k.city not like '%0000%0000%'");
				lstpara.add("%" + cityCode + "%");
			}
			
			sql.append("  order by sk.Scenarios2kbdataid desc ) A WHERE ROWNUM <= ? ) WHERE RN >= ?");
			// 绑定截止条数参数
			lstpara.add(page * rows);
			// 绑定开始条数参数
			lstpara.add((page - 1) * rows);

		} else if (GetConfigValue.isMySQL) {
			// 判断数据源不为空且含有数据
			sql = new StringBuilder();
			// 定义分页查询满足条件的问题要素名称SQL语句
			sql.append("select * from (select s.service as name,sk.* from  Service s,Scenarios2kbdata sk where  s.Serviceid = sk.relationserviceid  ");
//			sql.append("select * from (select s.name,sk.* from  Scenarios s,Scenarios2kbdata sk where  s.Scenariosid = sk.Scenariosid  ");
			lstpara = new ArrayList<Object>();
			sql.append(" and sk.relationserviceid = ? ");
//			sql.append(" and sk.scenariosid = ? ");
			lstpara.add(scenariosid);
			if (!"".equals(serviceid) && serviceid != null) {
				sql.append(" and sk.serviceid = ? ");
				lstpara.add(serviceid);
			}
			if (!"".equals(abstractid) && abstractid != null) {
				sql.append(" and sk.abstractid = ? ");
				lstpara.add(abstractid);
			}
			if (!"".equals(userquery) && userquery != null) {
				sql.append(" and sk.userquery like ? ");
				lstpara.add("%" + userquery + "%");
			}
			sql.append(" order by sk.Scenarios2kbdata desc )w limit ?,? ");
			// 绑定开始条数参数
			lstpara.add((page - 1) * rows);
			// 绑定截止条数参数
			lstpara.add(page * rows);
		}
		System.out.println( sql + "#" + lstpara );
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		return rs;
	}

	/**
	 * 查询满足条件的问题要素信息记录数
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param name参数问题要素名称
	 * @return int
	 */
	public static int getElementNameCount(String scenariosid, String name) {
		int count = 0;
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 查询问题元素的SQL语句
		sql.append("select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from scenarioselement q where q.relationserviceid=?  ");
//		sql.append("select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from scenarioselement q where q.scenariosid=?  ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		lstpara.add(scenariosid);
		// 判断问题要素名称是否为null，空
		if (name != null && !"".equals(name)) {
			// 加上问题要素名称查询条件
			sql.append(" and q.name like ? ");
			// 绑定问题要素名称参数
			lstpara.add("%" + name + "%");
		}
		// 加上优先级的升序排序条件
		sql.append(" order by q.weight asc");
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为空且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			count = rs.getRowCount();
		}
		return count;
	}

	/**
	 * 分页查询满足条件的问题要素信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param name参数问题要素名称
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return jResult
	 */
	public static Result getElementName(String scenariosid, String name,
			int page, int rows) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		List<Object> lstpara = new ArrayList<Object>();
		// 将SQL语句补充完整
		if (GetConfigValue.isOracle) {
			// 判断数据源不为空且含有数据
			sql = new StringBuilder();
			// 定义分页查询满足条件的问题要素名称SQL语句
			sql.append("select * from (select t.*,rownum rn from (select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from scenarioselement q where q.relationserviceid=?  ");
//			sql.append("select * from (select t.*,rownum rn from (select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from scenarioselement q where q.scenariosid=?  ");
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(scenariosid);
			// 判断问题要素名称是否为null，空
			if (name != null && !"".equals(name)) {
				// 加上问题要素名称查询条件
				sql.append(" and q.name like ? ");
				// 绑定问题要素名称参数
				lstpara.add("%" + name + "%");
			}
			sql.append(" order by q.weight asc)t) where rn>? and rn<=?");
		} else if (GetConfigValue.isMySQL) {
			// 判断数据源不为空且含有数据
			sql = new StringBuilder();
			// 定义分页查询满足条件的问题要素名称SQL语句
			sql.append("select * from (select t.* from (select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from queryelement q where q.relationserviceid=? ");
//			sql.append("select * from (select t.* from (select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from queryelement q where q.scenariosid=? ");
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(scenariosid);
			// 判断问题要素名称是否为null，空
			if (name != null && !"".equals(name)) {
				// 加上问题要素名称查询条件
				sql.append(" and q.name like ? ");
				// 绑定问题要素名称参数
				lstpara.add("%" + name + "%");
			}
			sql.append(" order by q.weight asc)t)w limit ?,? ");
		}
		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(page * rows);
		
		//文件日志
		GlobalValue.myLog.info( sql.toString() + "#" + lstpara );
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		return rs;
	}

	/**
	 * 查询当前问题元素下的优先级
	 * 
	 * @param scenariosid
	 *            场景ID
	 * @return Result
	 */
	public static Result getWeight(String scenariosid) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义当前的优先级集合
		List<String> weightNow = new ArrayList<String>();
		// 定义查询优先级的SQL语句
		String sql = "select weight from scenarioselement where  relationserviceid=? order by weight asc";
//		String sql = "select weight from scenarioselement where  scenariosid=? order by weight asc";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}

	/**
	 * 查询当前场景下的场景名称
	 * 
	 * @param scenariosid
	 *            场景ID
	 * @return Result
	 */
	public static Result getElementName(String scenariosid, String cityCode) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义当前的优先级集合
		List<String> weightNow = new ArrayList<String>();
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义查询优先级的SQL语句
		String sql = "";
		if ("".equals(cityCode) || cityCode == null) {
			sql = "select scenarioselementid,name from scenarioselement where  relationserviceid=?  ";
//			sql = "select scenarioselementid,name from scenarioselement where  scenariosid=?  ";
			// 绑定摘要id参数
			lstpara.add(scenariosid);
		} else {
			sql = "select scenarioselementid,name from scenarioselement where  relationserviceid=?  and (city like ?  or city like '%全国%' or city is null )";
//			 sql = "select scenarioselementid,name from scenarioselement where  scenariosid=?  and (city like ?  or city like '%全国%' or city is null )";
			// 绑定摘要id参数
			lstpara.add(scenariosid);
			lstpara.add("%" + cityCode + "%");

		}
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}

	/**
	 *@description 查询满足条件规则优先级
	 *@param responsetype
	 *            规则类型
	 *@param city
	 *            城市编码
	 *@param scenariosid
	 *            场景ID
	 *@return
	 *@returnType Result
	 */
	public static Result getRuleWeight(String responsetype, String city,
			String scenariosid) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义当前的优先级集合
		List<String> weightNow = new ArrayList<String>();
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义查询优先级的SQL语句
		String sql = "select weight, ruleid from scenariosrules where  relationserviceid=?  ";
//		String sql = "select weight, ruleid from scenariosrules where  relationserviceid=?  and ruletype=? ";
//		String sql = "select weight, ruleid from scenariosrules where  scenariosid=?  and ruletype=? ";
		// 绑定参数
		lstpara.add(scenariosid);
		if ("0".equals(responsetype) || "4".equals(responsetype)){
			sql += "and ruletype in (0,4)";
		}else {
			sql += "and ruletype=? ";
			lstpara.add(responsetype);
		}
		if (!"".equals(city) && city != null) {
			if (!"全国".equals(city)) {
				if (city.endsWith("0000")) {
					sql = sql + " and city like ?";
					lstpara.add("%" + city.substring(0, 2) + "%");
				} else {
					sql = sql + " and city =? ";
					lstpara.add(city);
				}

			}

		}
		sql = sql + "  order by weight ";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}

	/**
	 * 添加问题要素
	 * 
	 * @param name参数问题要素
	 * @param kbdataid参数摘要id
	 * @param weight参数优先级
	 * @param wordclass参数词类名称
	 * @param abs参数摘要名称
	 * @return 添加处理后的信息的json串
	 */
	public static int insertElementName(String name, String kbdataid,
			String kbcontentid, String weight, String wordclass, String abs,
			String serviceType) {
		int count = -1;
		// 1.先查询词类是否存在
		// 定义查询词类是否存在的SQL语句
		String sql = "select * from wordclass where wordclass=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定词类名称参数
		lstpara.add(wordclass);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 获取词类id值
			String wordclassid = rs.getRows()[0].get("wordclassid").toString();
			// 2.查询问题要素是否重复
			// 查询要添加的问题要素是否重复
			sql = "select * from queryelement where name=? and kbdataid=? and kbcontentid=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定问题要素名称参数
			lstpara.add(name);
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 执行SQL语句，获取相应的数据源
			rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为空且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				return count;
			} else {
				// 定义多条SQL语句集合
				List<String> lstSql = new ArrayList<String>();
				// 定义多条SQL语句对应的绑定参数集合
				List<List<?>> lstLstpara = new ArrayList<List<?>>();
				// 问题要素不存在
				// 插入问题要素的SQL语句
				sql = "insert into queryelement (queryelementid,name,kbdataid,kbcontentid,wordclassid,weight) values (?,?,?,?,?,?)";
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 获取插入问题要素表的序列,并绑定问题要素id参数
				String queryelementid = "";
				String bussinessFlag = CommonLibMetafieldmappingDAO
						.getBussinessFlag(serviceType);
				if (GetConfigValue.isOracle) {
					queryelementid = ConstructSerialNum.GetOracleNextValNew(
							"queryelement_seq", bussinessFlag);
				} else if (GetConfigValue.isMySQL) {
					queryelementid = ConstructSerialNum.getSerialIDNew(
							"queryelement", "queryelementid", bussinessFlag);
				}
				lstpara.add(queryelementid);
				// 绑定问题要素名称参数
				lstpara.add(name);
				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定kbcontentid参数
				lstpara.add(kbcontentid);
				// 绑定词类id参数
				lstpara.add(wordclassid);
				// 绑定优先级参数
				lstpara.add(weight);
				// 将SQL语句放入集合中
				lstSql.add(sql);
				// 将对应的绑定参数集合放入集合
				lstLstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
				
				// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
				count = Database.executeNonQueryTransaction(lstSql, lstLstpara);
			}
		}
		return count;
	}

	/**
	 * 删除场景要素，并返回相应的信息
	 * 
	 *@param scenarioselementid
	 *            场景元素ID
	 *@param weight
	 *            优先级
	 *@param scenariosid
	 *            场景ID
	 *@return
	 *@returnType int
	 */
	public static int deleteElementName(User user, String serviceType,
			String name, String scenarioselementid, String weight,
			String scenariosid, String scenariosName) {
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 删除场景要素的SQL语句
		String sql = "delete from scenarioselement where scenarioselementid=?";
		// 绑定场景要素id参数
		lstpara.add(scenarioselementid);
		// 将删除场景要素的SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入到集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ scenarioselementid );

		// 删除规则SQL语句
		// sql = "delete from scenariosrules where scenariosid=?  and condition"
		// + weight + " is not null";

		sql = "update scenariosrules set  condition" + weight
				+ " = null  where relationserviceid=? ";
//		 + " = null  where scenariosid=? ";
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		// 将删除问题要素组合的SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入到集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ scenariosid );
		// 删除无效的规则
		sql = "delete from scenariosrules where relationserviceid=? and ruletype=3";
		for(int i=1 ; i<21 ; i++){
			sql = sql + " and condition" + i + " is null";
		}
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		// 将删除问题要素组合的SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入到集合中
		lstLstpara.add(lstpara);
		
		// 将操作日志SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
//		String brand = serviceType.split("->")[1] + "场景";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), brand, scenariosName,
				"删除场景要素", name, "SCENARIOSELEMENT"));

		// 执行SQL语句集合，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}

	/**
	 * 删除问题要素值(词条),并更新对应的数据和规则
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param elementvalueid参数问题要素值id
	 * @param weight参数问题要素的优先级
	 * @param name参数问题要素值名称
	 * @param wordclass参数词类名称
	 * @return int
	 */
	public static int deleteElementValue(String kbdataid, String kbcontentid,
			String elementvalueid, String weight, String name, String wordclass) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义删除词条的SQL语句
		String sql = "delete from word where wordid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定问题要素值id参数
		lstpara.add(elementvalueid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 删除别名的SQL语句
		sql = "delete from word where stdwordid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定问题要素值id参数
		lstpara.add(elementvalueid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 定义更新数据的SQL语句
		sql = "update conditioncombtoreturntxt set condition" + weight
				+ " = null where condition" + weight
				+ "=? and kbdataid=? and kbcontentid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定属性值参数
		lstpara.add(name);
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 定义更新规则的SQL语句
		sql = "update scenerules set condition" + weight
				+ " = null where condition" + weight
				+ "=? and kbdataid=? and kbcontentid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定属性值参数
		lstpara.add(name);
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		// 判断事务处理结果
		return c;
	}

	/**
	 *查询当前场景下要素和关联词条组合信息
	 * 
	 * @param scenariosid
	 *@return
	 *@returnType Result
	 */
	public static Result queryElementAndWord(String scenariosid, String cityCode) {
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "";
		if ("".equals(cityCode) || "全国".equals(cityCode)) {
			sql = "select q.name,w.word from SCENARIOSELEMENT q left join word w on q.wordclassid=w.wordclassid where w.stdwordid is null and (w.city is null or w.city='全国') and q.relationserviceid=?  order by q.weight asc,w.wordid desc,w.word desc";
//			 sql = "select q.name,w.word from SCENARIOSELEMENT q left join word w on q.wordclassid=w.wordclassid where w.stdwordid is null and q.scenariosid=?  order by q.weight asc,w.wordid desc,w.word desc";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(scenariosid);
		} else {
			sql = "select q.name,w.word from SCENARIOSELEMENT q left join word w on q.wordclassid=w.wordclassid where w.stdwordid is null and (w.city like ? or w.city is null or w.city='全国') and q.relationserviceid=?  order by q.weight asc,w.wordid desc,w.word desc";
//			 sql = "select q.name,w.word from SCENARIOSELEMENT q left join word w on q.wordclassid=w.wordclassid where w.stdwordid is null and (w.city like ? or w.city is null or w.city='全国') and q.scenariosid=?  order by q.weight asc,w.wordid desc,w.word desc";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			lstpara.add("%" + cityCode + "%");
			// 绑定摘要id参数
			lstpara.add(scenariosid);
		}

		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}

	public static Result queryElementServiceAndAbstract(String scenariosid) {
		List<Object> lstpara = new ArrayList<Object>();
		// 获取问题要素组合的SQL语句
		String sql = " select *  from scenarios2kbdata where  relationserviceid =?";
//		 String sql = " select *  from scenarios2kbdata where  scenariosid =?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}

	/**
	 * 查询场景要素信息
	 * 
	 * @param scenariosid
	 *            场景ID
	 * @return 问题要素组合的json串
	 */
	public static Result queryElement(String scenariosid) {
		List<Object> lstpara = new ArrayList<Object>();
		// 定义查询问题要素名称
		String sql = "select * from SCENARIOSELEMENT where relationserviceid=?  order by weight asc";
//		 String sql = "select * from SCENARIOSELEMENT where scenariosid=?  order by weight asc";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		// 判断数据源不为null且含有数据
		return rs;

	}

	/**
	 * 查询满足条件的带分页的问题要素数据信息数据源
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合条件
	 * @param returntxttype参数答案类型
	 * @param status参数状态
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return Result
	 */
	public static Result getConditionCombToReturnTxt(String kbdataid,
			String kbcontentid, String conditions, String returntxttype,
			String status, int page, int rows) {
		// 定义问题要素数组，并设定长度为10
		String[] conditionArr = new String[20];
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将conditions按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		StringBuilder paramSql = new StringBuilder();

		lstpara.add(kbdataid);
		lstpara.add(kbcontentid);
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某一个问题要素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 在SQL语句中加上条件
					paramSql.append(" and condition" + (i + 1) + " = ? ");
					// 绑定对应的参数
					lstpara.add(conditionArr[i]);
				}
			}
		}
		// 判断答案类型不为null且不为空
		if (returntxttype != null && !"".equals(returntxttype)) {
			// 在SQL语句中加上条件
			paramSql.append(" and returntxttype = ? ");
			// 绑定答案类型参数
			lstpara.add(returntxttype);
		}
		// 判断状态不为null且不为空
		if (status != null && !"".equals(status)) {
			// 在SQL语句中加上条件
			paramSql.append(" and status = ? ");
			// 绑定状态参数
			lstpara.add(status);
		}
		String sql = "";
		if (GetConfigValue.isOracle) {
			// 查询满足条件的带分页的SQL语句
			sql = "select * from (select t.*,rownum rn from(select * from conditioncombtoreturntxt where kbdataid=? and kbcontentid=? "
					+ paramSql
					+ "order by status asc,combitionid desc)t) where rn>? and rn <=?";
		} else if (GetConfigValue.isMySQL) {
			// 查询满足条件的带分页的SQL语句
			sql = "select * from (select t.* from(select * from conditioncombtoreturntxt where kbdataid=? and kbcontentid=? "
					+ paramSql
					+ "order by status asc,combitionid desc)t)w limit ?,?";
		}

		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(page * rows);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}

	/**
	 * 查询问题要素数据信息记录数
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合条件
	 * @param returntxttype参数答案类型
	 * @param status参数状态
	 * @return int
	 */
	public static int getConditionCombToReturnTxtCount(String kbdataid,
			String kbcontentid, String conditions, String returntxttype,
			String status) {
		int count = 0;
		// 定义问题要素数组，并设定长度为10
		String[] conditionArr = new String[20];
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将conditions按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 查询满足条件的数量的SQL语句
		String sql = "select * from conditioncombtoreturntxt where kbdataid=? and kbcontentid=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某一个问题要素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 在SQL语句中加上条件
					paramSql.append(" and condition" + (i + 1) + " = ? ");
					// 绑定对应的参数
					lstpara.add(conditionArr[i]);
				}
			}
		}
		// 判断答案类型不为null且不为空
		if (returntxttype != null && !"".equals(returntxttype)) {
			// 在SQL语句中加上条件
			paramSql.append(" and returntxttype = ? ");
			// 绑定答案类型参数
			lstpara.add(returntxttype);
		}
		// 判断状态不为null且不为空
		if (status != null && !"".equals(status)) {
			// 在SQL语句中加上条件
			paramSql.append(" and status = ? ");
			// 绑定状态参数
			lstpara.add(status);
		}
		// 执行SQL语句，并返回相应的数据源
		Result rs = Database.executeQuery(sql + paramSql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + paramSql + "#" + lstpara );
		
		// 判断数据源不为null,且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			count = rs.getRowCount();
		}
		return count;
	}

	/**
	 * 确认问题元素值组合，将待审核变成已审核 ,返回确认条数
	 * 
	 * @param combitionid参数combitionid
	 * @return int
	 */
	public static int confirmConditionCombToReturnTxt(String combitionid) {
		// 定义更新状态的SQL语句
		StringBuilder sql = new StringBuilder();
		// 更新状态的SQL语句
		sql
				.append("update conditioncombtoreturntxt set status=? where combitionid in (");
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定状态参数
		lstpara.add("1");
		// 定义数据id数组
		String[] ids = combitionid.split(",");
		// 循环遍历数据id数组
		for (int i = 0; i < ids.length; i++) {
			if (i < ids.length - 1) {
				// 除了数组的最后一个绑定参数不需要加上逗号，其他的都要加上
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定id参数
			lstpara.add(ids[i]);
		}
		// 执行SQL语句，绑定事务处理，返回事务处理的结果
		int c = Database.executeNonQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断事务处理结果
		return c;
	}

	/**
	 * 确认问题元素值组合，将待审核变成已审核 ,返回确认条数
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @return int
	 */
	public static int confirmAllConditionCombToReturnTxt(String kbdataid,
			String kbcontentid) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		// 更新状态的SQL语句
		String sql = "update conditioncombtoreturntxt set status=? where kbdataid=? and kbcontentid=?";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定状态参数
		lstpara.add("1");
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
		// 执行SQL语句，绑定事务处理，返回事务处理的结果
		int c = Database.executeNonQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return c;
	}

	/**
	 * 根据数据id删除相应的数据，并返回执行条数
	 * 
	 * @param combitionid参数数据id
	 * @param abs参数摘要名称
	 * @return int
	 */
	public static int deleteConditionCombToReturnTxt(String combitionid,
			String abs) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 删除数据的SQL语句
		sql
				.append("delete from conditioncombtoreturntxt where combitionid in(");
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义数据id数组
		String[] ids = new String[] {};
		// 判断数据id不为null且不为空
		if (combitionid != null && !"".equals(combitionid)) {
			// 将数据id按照逗号拆分
			ids = combitionid.split(",");
		}
		// 循环遍历数据id数组
		for (int i = 0; i < ids.length; i++) {
			if (i < ids.length - 1) {
				// 除了数组的最后一个绑定参数不需要加上逗号，其他的都要加上
				sql.append("?,");
			} else {
				// 最后一个加右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定id参数
			lstpara.add(ids[i]);
		}
		// 将SQL语句放入集合中
		lstSql.add(sql.toString());
		// 将对应的绑定参数集合放入集合
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}

	/**
	 * 全量删除数据,并返回执行条数
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param abs参数摘要名称
	 * @return int
	 */
	public static int deleteAllConditionCombToReturnTxt(String kbdataid,
			String kbcontentid, String abs) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义删除数据的SQL语句
		String sql = "delete from conditioncombtoreturntxt where kbdataid=? and kbcontentid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 执行SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}

	/**
	 * 将回复模板保存到答案表中，并返回执行条数
	 * 
	 * @param answer参数回复模板
	 * @param kbanswerid参数kbanswerid
	 * @return int
	 */
	public static int saveModel(String answer, String kbanswerid) {
		// 更新回复模板的SQL语句
		String sql = "update kbanswer set answercontent=? where kbanswerid=?";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定答案参数
		lstpara.add(answer);
		// 绑定kbanswerid参数
		lstpara.add(kbanswerid);
		// 执行SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return c;
	}

	/**
	 * 判断问题要素信息数据库中是否存在
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合
	 * @param returntxttype参数答案类型
	 * @param returntxt参数答案内容
	 * @param abs参数摘要名称
	 * @return boolean
	 */
	public static boolean isExitConditionCombToReturnTxt(String kbdataid,
			String kbcontentid, String conditions, String returntxttype,
			String returntxt) {
		// 定义问题元素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将问题元素中按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 查询是否重复的SQL语句
		sql
				.append("select * from conditioncombtoreturntxt where kbdataid=? and kbcontentid=? ");
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题元素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某一个问题元素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 添加condition添加
					sql.append(" and condition" + (i + 1) + " = ? ");
					// 绑定相应的参数
					lstpara.add(conditionArr[i]);
				} else {
					// 其他条件为null
					sql.append(" and condition" + (i + 1) + " is null ");
				}
			}
		}
		// 加上答案类型和答案内容添加
		sql.append(" and returntxttype=? and returntxt=?");
		// 绑定答案类型参数
		lstpara.add(returntxttype);
		// 绑定答案内容参数
		lstpara.add(returntxt.trim());
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为空且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 将问题要素信息添加到数据库中，并返回执行条数
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合
	 * @param returntxttype参数答案类型
	 * @param returntxt参数答案内容
	 * @param abs参数摘要名称
	 * @return int
	 */
	public static int insertConditionCombToReturnTxt(String kbdataid,
			String kbcontentid, String conditions, String returntxttype,
			String returntxt, String abs, String serviceType) {
		// 定义问题元素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将问题元素中按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		sql = new StringBuilder();
		// 插入数据表的SQL语句
		sql
				.append("insert into conditioncombtoreturntxt (combitionid,kbdataid,kbcontentid,");
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 在SQL语句中将上condition1-10
				sql.append("condition" + (i + 1) + ",");
			}
		}
		// 将插入语句补充完整
		sql.append("status,returntxttype,returntxt) values (?,?,?,");
		String combitionid = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		if (GetConfigValue.isOracle) {
			combitionid = ConstructSerialNum.GetOracleNextValNew(
					"conditioncombtoreturntxt_seq", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			combitionid = ConstructSerialNum.getSerialIDNew(
					"conditioncombtoreturntxt", "combitionid", bussinessFlag);
		}
		// 获取插入语句的序列，绑定序列参数
		lstpara.add(combitionid);
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某一个问题要素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 添加某个问题要素不为空的条件
					sql.append("?,");
					// 绑定对应的参数
					lstpara.add(conditionArr[i]);
				} else {
					// 某个问题要素为null
					sql.append("null,");
				}
			}
		}
		// 补充插入语句
		sql.append("?,?,?)");
		// 绑定状态参数
		lstpara.add("0");
		// 绑定答案类型参数
		lstpara.add(returntxttype);
		// 绑定答案内容参数
		lstpara.add(returntxt.trim());
		// 将SQL语句放入集合中
		lstSql.add(sql.toString());
		// 将对应的绑定参数集合放入集合
		lstLstpara.add(lstpara);
		// 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return c;
	}

	/**
	 * 更新当前数据中需要修改的值，并将状态改为未审核，并返回执行条数
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合
	 * @param returntxttype参数答案类型
	 * @param returntxt参数答案内容
	 * @param combitionid参数数据id
	 * @return int
	 */
	public static int updateConditionCombToReturnTxt(String kbdataid,
			String kbcontentid, String conditions, String returntxttype,
			String returntxt, String combitionid) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		// 定义问题要素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null || !"".equals(conditions)) {
			// 将问题要素组合按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义SQL语句
		sql = new StringBuilder();
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 更新数据的SQL语句
		sql.append("update conditioncombtoreturntxt set ");
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某个问题要素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 在更新的SQL语句中添加condition的条件
					sql.append(" condition" + (i + 1) + "=?, ");
					// 绑定对应的参数
					lstpara.add(conditionArr[i]);
				} else {
					// 在更新的SQL语句中添加condition为null的条件
					sql.append(" condition" + (i + 1) + " = null, ");
				}
			}
		}
		// 补充完整更新的SQL语句
		sql.append("status=?,returntxttype=?,returntxt=? where combitionid=?");
		// 绑定状态参数
		lstpara.add("0");
		// 绑定答案类型参数
		lstpara.add(returntxttype);
		// 绑定答案内容参数
		lstpara.add(returntxt);
		// 绑定数据id参数
		lstpara.add(combitionid);
		// 执行更新SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return c;
	}

	/**
	 * 判断规则是否存在
	 * 
	 * @param scenariosid参数场景id
	 * @param conditions参数问题要素组合
	 * @param ruletype参数规则类型
	 * @param ruleresponse参数规则回复内容
	 * @return boolean
	 */
	public static boolean isExitSceneRules(String scenariosid,
			String conditions, String ruletype, String ruleresponse,
			String weight, String cityCode, String responsetype,
			String service, String abs) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		// 定义问题元素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将问题元素中按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 查询规则是否重复的SQL语句
		sql.append("select * from SCENARIOSRULES where relationserviceid=?  ");
//		 sql.append("select * from SCENARIOSRULES where scenariosid=?  ");
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题元素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某一个问题元素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 添加condition条件
					sql.append(" and condition" + (i + 1) + " = ? ");
					// 绑定相应的参数
					lstpara.add(conditionArr[i]);
				} else {
					// 其他条件为null
					sql.append(" and condition" + (i + 1) + " is null ");
				}
			}
		}
		// 将规则类型和规则回复内容补充到查询规则的SQL语句中
		// sql.append(" and ruletype=? and ruleresponse=? and weight=?  and city =?");
		sql.append(" and ruletype=?  and weight=?  and ruleresponse like ?");
		// 绑定规则类型参数
		lstpara.add(ruletype);
		// 绑定规则优先级
		lstpara.add(weight);
		// 绑定规则回复内容参数
		lstpara.add(ruleresponse.trim());
		// lstpara.add(responsetype);
		if ("".equals(cityCode) || cityCode == null) {
			sql.append("  and city  is null ");
		} else {
			sql.append("  and city = ? ");
			lstpara.add(cityCode);
		}

		if (!"".equals(service) && service != null) {
			if (!"".equals(abs) && abs != null) {
				sql
						.append(" and ABOVEQUESTIONOBJECT =? and ABOVESTANDARDQUESTION=? ");
				lstpara.add(service);
				lstpara.add(abs);
			} else {
				sql.append(" and ABOVEQUESTIONOBJECT =? ");
				lstpara.add(service);
			}

		}

		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为空且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return true;
		}
		return false;
	}

	// /**
	// * 将规则添加到规则表中，并返回相应的信息
	// *
	// * @param scenariosid参数场景id
	// * @param conditions参数问题要素组合
	// * @param weight参数规则优先级
	// * @param ruletype参数规则类型
	// * @param ruleresponse参数规则回复内容
	// * @param abs参数摘要名称
	// * @param userquestion
	// * 用户问题
	// * @return 添加后返回的json串
	// */
	// public static int insertSceneRules(String scenariosid, String conditions,
	// String weight, String ruletype, String ruleresponse,
	// String serviceType, String cityCode, String cityName,
	// String service, String abs, String ruleresponsetemplate,
	// String responsetype, String questionobject,
	// String standardquestion, String userquestion) {
	// // 定义问题元素数组
	// String[] conditionArr = new String[] {};
	// // 判断问题要素组合不为null且不为空
	// if (conditions != null && !"".equals(conditions)) {
	// // 将问题元素中按照@拆分
	// conditionArr = conditions.split("@", 10);
	// }
	// // 定义SQL语句
	// StringBuilder sql = new StringBuilder();
	// // 定义绑定参数集合
	// List<Object> lstpara = new ArrayList<Object>();
	// // 定义多条SQL语句集合
	// List<String> lsts = new ArrayList<String>();
	// // 定义多条SQL语句对应的绑定参数集合
	// List<List<?>> lstlstpara = new ArrayList<List<?>>();
	// // 插入规则表的SQL语句
	// sql.append("insert into SCENARIOSRULES (ruleid,scenariosid,");
	// // 判断问题要素组合不为null且不为空
	// if (conditions != null && !"".equals(conditions)) {
	// // 循环遍历问题要素数组
	// for (int i = 0; i < conditionArr.length; i++) {
	// // 在SQL语句中将上condition1-10
	// sql.append("condition" + (i + 1) + ",");
	// }
	// }
	// // 补充完整插入规则表的SQL语句
	// sql
	// .append("ruletype,ruleresponse,weight,city,cityname,ABOVEQUESTIONOBJECT,ABOVESTANDARDQUESTION,ruleresponsetemplate,responsetype,questionobject,standardquestion,userquestion) values (?,?,");
	// String ruleid = "";
	// String bussinessFlag = CommonLibMetafieldmappingDAO
	// .getBussinessFlag(serviceType);
	// if (GetConfigValue.isOracle) {
	// ruleid = ConstructSerialNum.GetOracleNextValNew(
	// "seq_scenariosrules_id", bussinessFlag);
	// } else if (GetConfigValue.isMySQL) {
	// ruleid = ConstructSerialNum.getSerialIDNew("scenariosrules",
	// "ruleid", bussinessFlag);
	// }
	// // 获取规则表的序列值,绑定序列值参数
	// lstpara.add(ruleid);
	// // 绑定摘要id参数
	// lstpara.add(scenariosid);
	// // 判断问题要素组合不为null且不为空
	// if (conditions != null && !"".equals(conditions)) {
	// // 循环遍历问题要素数组
	// for (int i = 0; i < conditionArr.length; i++) {
	// // 判断某一个问题要素不为null且不为空
	// if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
	// // 添加某个问题要素不为空的条件
	// sql.append("?,");
	// // 绑定对应的参数
	// lstpara.add(conditionArr[i]);
	// } else {
	// // 某个问题要素为null
	// sql.append("null,");
	// }
	// }
	// }
	// // 将插入规则表的SQL语句补充完整
	// sql.append("?,?,?,?,?,?,?,?,?,?,?,?)");
	// // 绑定规则类型参数
	// lstpara.add(ruletype);
	// // 绑定规则回复内容参数
	// lstpara.add(ruleresponse.trim());
	// // 绑定规则优先级参数
	// lstpara.add(weight);
	// // 绑定地市代码参数
	// lstpara.add(cityCode);
	// // 绑定地市名称参数
	// lstpara.add(cityName);
	// lstpara.add(service);
	// lstpara.add(abs);
	// // if("2".equals(responsetype)){
	// // lstpara.add(ruleresponsetemplate);
	// // lstpara.add(2);
	// // }else{
	// lstpara.add(ruleresponsetemplate);
	// lstpara.add(responsetype);
	// // }
	// lstpara.add(questionobject);
	// lstpara.add(standardquestion);
	// lstpara.add(userquestion);
	//
	// // 将SQL语句放入集合中
	// lsts.add(sql.toString());
	// // 将对应的绑定参数集合放入集合
	// lstlstpara.add(lstpara);
	//
	// //
	//
	//
	//
	// // 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
	// int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
	// // 判断事务处理结果
	// return c;
	// }

	/**
	 * 将规则添加到规则表中，并返回相应的信息
	 * 
	 * @param scenariosid参数场景id
	 * @param conditions参数问题要素组合
	 * @param weight参数规则优先级
	 * @param ruletype参数规则类型
	 * @param ruleresponse参数规则回复内容
	 * @param abs参数摘要名称
	 * @param userquestion
	 *            用户问题
	 * @return 添加后返回的json串
	 */
	public static int insertSceneRules(User user, String scenariosid,
			String scenariosName, String conditions, String weight,
			String ruletype, String ruleresponse, String serviceType,
			String cityCode, String cityName, String excludedcityCode,
			String service, String abs, String ruleresponsetemplate,
			String responsetype, String questionobject,
			String standardquestion, String userquestion, String currentnode) {
		// add by xzh
		String excludedcity = "";

		if ("".equals(cityCode) || "全国".equals(cityCode)) {// 获取全国规则的排除地市
			String tempSql = "";
			List<Object> templstpara = new ArrayList<Object>();
			// 定义绑定参数集合
			tempSql = "select city from scenariosrules where relationserviceid=? and ruletype=? and city like '%0000'";
//			 tempSql =  "select city from scenariosrules where scenariosid=? and ruletype=? and city like '%0000'";
			templstpara.add(scenariosid);
			templstpara.add(ruletype);
			Result tempRs = Database.executeQuery(tempSql, templstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( tempSql + "#" + templstpara );
			
			if (tempRs != null && tempRs.getRowCount() > 0) {
				for (int i = 0; i < tempRs.getRowCount(); i++) {
					String tempCity = tempRs.getRows()[i].get("city")
							.toString();
					if (!excludedcity.contains(tempCity)) {
						excludedcity = excludedcity + tempCity + ",";
					}
				}
				excludedcity = excludedcity.substring(0, excludedcity
						.lastIndexOf(","));
			}
			// } else if (cityCode.endsWith("0000")){// 省级规则
			// String tempSql ="";
			// List<Object> templstpara = new ArrayList<Object>();
			// tempSql =
			// "select city from scenariosrules where scenariosid=? and ruletype=? and city like ? and city not like ?";
			// templstpara.add(scenariosid);
			// templstpara.add(ruletype);
			// templstpara.add(cityCode.substring(0, 2) + "%");
			// templstpara.add("%0000");
			// Result tempRs =
			// Database.executeQuery(tempSql,templstpara.toArray());
			// if (tempRs != null && tempRs.getRowCount() > 0){
			// for (int i = 0;i < tempRs.getRowCount();i++){
			// String tempCity = tempRs.getRows()[i].get("city").toString();
			// if (!excludedcity.contains(tempCity)){
			// excludedcity = excludedcity + tempCity + ",";
			// }
			// }
			// excludedcity = excludedcity.substring(0,
			// excludedcity.lastIndexOf(","));
			// }
		}
		if (excludedcityCode != null && !"".equals(excludedcityCode)) {
			excludedcity = excludedcityCode;
		}
		// 定义问题元素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将问题元素中按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 插入规则表的SQL语句
		sql.append("insert into SCENARIOSRULES (ruleid,relationserviceid,");
//		 sql.append("insert into SCENARIOSRULES (ruleid,scenariosid,");
		if (excludedcity != null && !"".equals(excludedcity)) {
			sql.append("excludedcity,");
		}
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 在SQL语句中将上condition1-10
				sql.append("condition" + (i + 1) + ",");
			}
		}
		// 补充完整插入规则表的SQL语句
		sql
				.append("ruletype,ruleresponse,weight,city,cityname,ABOVEQUESTIONOBJECT,ABOVESTANDARDQUESTION,ruleresponsetemplate,responsetype,questionobject,standardquestion,userquestion,currentnode) values (?,?,");
		String ruleid = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		if (GetConfigValue.isOracle) {
			ruleid = ConstructSerialNum.GetOracleNextValNew(
					"seq_scenariosrules_id", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			ruleid = ConstructSerialNum.getSerialIDNew("scenariosrules",
					"ruleid", bussinessFlag);
		}
		// 获取规则表的序列值,绑定序列值参数
		lstpara.add(ruleid);
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		// 绑定排除city
		if (excludedcity != null && !"".equals(excludedcity)) {
			sql.append("?,");
			lstpara.add(excludedcity);
		}
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某一个问题要素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 添加某个问题要素不为空的条件
					sql.append("?,");
					// 绑定对应的参数
					lstpara.add(conditionArr[i]);
				} else {
					// 某个问题要素为null
					sql.append("null,");
				}
			}
		}
		// 将插入规则表的SQL语句补充完整
		sql.append("?,?,?,?,?,?,?,?,?,?,?,?,?)");
		// 绑定规则类型参数
		lstpara.add(ruletype);
		// 绑定规则回复内容参数
		lstpara.add("");
		// 绑定规则优先级参数
		lstpara.add(weight);
		// 绑定地市代码参数
		lstpara.add(cityCode);
		// 绑定地市名称参数
		lstpara.add(cityName);
		lstpara.add(service);
		lstpara.add(abs);
		// if("2".equals(responsetype)){
		// lstpara.add(ruleresponsetemplate);
		// lstpara.add(2);
		// }else{
		lstpara.add("");
		lstpara.add(responsetype);
		// }
		lstpara.add(questionobject);
		lstpara.add(standardquestion);
		lstpara.add(userquestion);
		lstpara.add(currentnode);

		// 将SQL语句放入集合中
		lsts.add(sql.toString());
		// 将对应的绑定参数集合放入集合
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" 
				+ lstpara );

		sql = new StringBuilder();
		sql.append("update SCENARIOSRULES set ruleresponse=? ,ruleresponsetemplate=? where ruleid=?");
		lstpara = new ArrayList<Object>();
		lstpara.add(ruleresponse);
		lstpara.add(ruleresponsetemplate);
		lstpara.add(ruleid);

		// 将SQL语句放入集合中
		lsts.add(sql.toString());
		// 将对应的绑定参数集合放入集合
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ lstpara );

		// add by xzh
		if (cityCode.endsWith("0000")) {// 省级
		// if (cityCode.endsWith("00")){// 非全国
			sql = new StringBuilder();
			lstpara = new ArrayList<Object>();
			sql.append("update SCENARIOSRULES set EXCLUDEDCITY=EXCLUDEDCITY||? where relationserviceid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY not like ?");
//			 sql.append("update SCENARIOSRULES set EXCLUDEDCITY=EXCLUDEDCITY||? where scenariosid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY not like ?");
			lstpara.add("," + cityCode);
			lstpara.add(scenariosid);
			lstpara.add(ruletype);
			lstpara.add("%" + cityCode + "%");
			// 将SQL语句放入集合中
			lsts.add(sql.toString());
			// 将对应的绑定参数集合放入集合
			lstlstpara.add(lstpara);

			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			
			sql = new StringBuilder();
			lstpara = new ArrayList<Object>();
			sql.append("update SCENARIOSRULES set EXCLUDEDCITY=? where relationserviceid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY is null");
//			 sql.append("update SCENARIOSRULES set EXCLUDEDCITY=? where scenariosid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY is null");
			lstpara.add(cityCode);
			lstpara.add(scenariosid);
			lstpara.add(ruletype);
			// 将SQL语句放入集合中
			lsts.add(sql.toString());
			// 将对应的绑定参数集合放入集合
			lstlstpara.add(lstpara);
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			
			// } else if (cityCode.endsWith("00") &&
			// !cityCode.endsWith("0000")){// 市级
			// sql = new StringBuilder();
			// lstpara = new ArrayList<Object>();
			// sql.append("update SCENARIOSRULES set EXCLUDEDCITY=EXCLUDEDCITY||? where scenariosid=? and ruletype=? and (city is null or city='全国' or city =?) and EXCLUDEDCITY is not like ?");
			// lstpara.add("," + cityCode);
			// lstpara.add(scenariosid);
			// lstpara.add(ruletype);
			// lstpara.add(cityCode.substring(0, 2)+"0000");
			// lstpara.add("%" + cityCode + "%");
			// // 将SQL语句放入集合中
			// lsts.add(sql.toString());
			// // 将对应的绑定参数集合放入集合
			// lstlstpara.add(lstpara);
			//
			// sql = new StringBuilder();
			// lstpara = new ArrayList<Object>();
			// sql.append("update SCENARIOSRULES set EXCLUDEDCITY=? where scenariosid=? and ruletype=? and (city is null or city='全国' or city =?) and EXCLUDEDCITY is null");
			// lstpara.add(cityCode);
			// lstpara.add(scenariosid);
			// lstpara.add(ruletype);
			// lstpara.add(cityCode.substring(0, 2)+"0000");
			// // 将SQL语句放入集合中
			// lsts.add(sql.toString());
			// // 将对应的绑定参数集合放入集合
			// lstlstpara.add(lstpara);
		}

		// 将操作日志SQL语句放入集合中
		lsts.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
//		String brand = serviceType.split("->")[1] + "场景";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		String operation = "";// 操作类型
		// if("0".equals(ruletype)||"4".equals(ruletype)){
		operation = "增加交互规则";
		// }else if("3".equals(ruletype)){
		// operation = "增加语意理解规则";
		// }else{
		// operation = "增加其他规则";
		// }
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), brand, scenariosName,
				operation, ruleresponse.substring(0,
						ruleresponse.length() > 1000 ? 1000 : ruleresponse
								.length()), "SCENARIOSRULES"));

		// 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		// 判断事务处理结果
		return c;
	}

	/**
	 * 复制规则，并返回相应的信息
	 * 
	 * @param scenariosid参数场景id
	 * @param conditions参数问题要素组合
	 * @param weight参数规则优先级
	 * @param ruletype参数规则类型
	 * @param ruleresponse参数规则回复内容
	 * @param abs参数摘要名称
	 * @return 添加后返回的json串
	 */
	public static int copyRules(String rid, String scenariosid,
			String conditions, String weight, String ruletype,
			String ruleresponse, String serviceType, String cityCode,
			String cityName, String excludedcity, String interpat,
			String responsetype, String userquestion, String currentnode) {
		// 定义问题元素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将问题元素中按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 插入规则表的SQL语句
		sql.append("insert into SCENARIOSRULES (ruleid,relationserviceid,");
//		 sql.append("insert into SCENARIOSRULES (ruleid,scenariosid,");
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 在SQL语句中将上condition1-10
				sql.append("condition" + (i + 1) + ",");
			}
		}
		// 补充完整插入规则表的SQL语句
		sql.append("ruletype,ruleresponse,weight,city,cityname,RULERESPONSETEMPLATE,responsetype,userquestion,currentnode) values (?,?,");
		String ruleid = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		if (GetConfigValue.isOracle) {
			ruleid = ConstructSerialNum.GetOracleNextValNew(
					"seq_scenariosrules_id", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			ruleid = ConstructSerialNum.getSerialIDNew("scenariosrules",
					"ruleid", bussinessFlag);
		}
		// 获取规则表的序列值,绑定序列值参数
		lstpara.add(ruleid);
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某一个问题要素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 添加某个问题要素不为空的条件
					sql.append("?,");
					// 绑定对应的参数
					lstpara.add(conditionArr[i]);
				} else {
					// 某个问题要素为null
					sql.append("null,");
				}
			}
		}
		// 将插入规则表的SQL语句补充完整
		sql.append("?,?,?,?,?,?,?,?,?)");
		// 绑定规则类型参数
		lstpara.add(ruletype);
		// 绑定规则回复内容参数
		lstpara.add(ruleresponse.trim());
		// 绑定规则优先级参数
		lstpara.add(weight);
		// 绑定地市代码参数
		lstpara.add(cityCode);
		// 绑定地市名称参数
		lstpara.add(cityName);
		if ("".equals(interpat) || interpat == null) {
			lstpara.add(null);
		} else {
			lstpara.add(interpat);
		}

		lstpara.add(responsetype);
		lstpara.add(userquestion);
		lstpara.add(currentnode);
		// 将SQL语句放入集合中
		lsts.add(sql.toString());
		// 将对应的绑定参数集合放入集合
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 修改当前被复制规则的排除地市
		// sql = new StringBuilder();
		// lstpara = new ArrayList<Object>();
		// sql.append("update SCENARIOSRULES set EXCLUDEDCITY =? where RULEID =?");
		// if (!"".equals(excludedcity) && excludedcity != null) {
		// if (!excludedcity.contains(cityCode)) {
		// cityCode = excludedcity + "," + cityCode;
		// } else {
		// cityCode = excludedcity;
		// }
		// }
		// // 绑定排除地市代码参数
		// lstpara.add(cityCode);
		// // 绑定规则ID参数
		// lstpara.add(rid);
		// // 将SQL语句放入集合中
		// lsts.add(sql.toString());
		// // 将对应的绑定参数集合放入集合
		// lstlstpara.add(lstpara);

		// add by xzh
		if (cityCode.endsWith("0000")) {
			// 排除全国规则的copy地市
			sql = new StringBuilder();
			lstpara = new ArrayList<Object>();
			sql.append("update SCENARIOSRULES set EXCLUDEDCITY=EXCLUDEDCITY||? where relationserviceid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY not like ?");
//			 sql.append("update SCENARIOSRULES set EXCLUDEDCITY=EXCLUDEDCITY||? where scenariosid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY not like ?");
			lstpara.add("," + cityCode);
			lstpara.add(scenariosid);
			lstpara.add(ruletype);
			lstpara.add("%" + cityCode + "%");
			// 将SQL语句放入集合中
			lsts.add(sql.toString());
			// 将对应的绑定参数集合放入集合
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );

			sql = new StringBuilder();
			lstpara = new ArrayList<Object>();
			sql.append("update SCENARIOSRULES set EXCLUDEDCITY=? where relationserviceid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY is null");
//			sql.append("update SCENARIOSRULES set EXCLUDEDCITY=? where scenariosid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY is null");
			lstpara.add(cityCode);
			lstpara.add(scenariosid);
			lstpara.add(ruletype);
			// 将SQL语句放入集合中
			lsts.add(sql.toString());
			// 将对应的绑定参数集合放入集合
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}

		// 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		// 判断事务处理结果
		return c;
	}

	/**
	 * 根据不同的条查询满足条件的规则信息记录数
	 * 
	 * @param scenariosid
	 *            参数场景id
	 * @param conditions参数问题要素组合
	 * @param ruletype参数规则类型
	 * @param weight参数规则优先级
	 * @param userCityCode
	 *            用户cityCode
	 * @return int
	 */
	public static int getSceneRulesCount(String scenariosid, String conditions,
			String ruletype, String weight, String cityCode, String cityName,
			String belong, String userCityCode, String ruleresponse, String issue,
			String strategy) {
		// 定义返回的count
		int count = 0;
		// 定义问题要素数组，并设定长度为10
		String[] conditionArr = new String[20];
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将conditions按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 查询规则的SQL语句
		String sql = "select count(*) count from SCENARIOSRULES where relationserviceid=?  ";
//		 String sql = "select count(*) count from SCENARIOSRULES where scenariosid=?  ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		if (ruletype != null && !"".equals(ruletype)) {
			if ("2,5".equals(ruletype)){
				sql += " and ruletype in(2,5) ";
			} else {
				sql += " and ruletype=? ";
				// 绑定规则类型参数
				lstpara.add(ruletype);
			}
		} else {
			sql += " and ruletype in(0,4) ";
		}

		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某一个问题要素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 在SQL语句中加上条件
					paramSql.append(" and condition" + (i + 1) + " like ? ");
					// 绑定对应的参数
					lstpara.add("%" + conditionArr[i] + "%");
				}
			}
		}
		// 判断规则优先级不为null且不为空
		if (weight != null && !"".equals(weight)) {
			// 在SQL语句中加上条件
			paramSql.append(" and weight = ? ");
			// 绑定答案类型参数
			lstpara.add(weight);
		}

		// 回复内容搜索
		if (ruleresponse != null && !"".equals(ruleresponse)) {
			// 在SQL语句中加上条件
			paramSql.append(" and ruleresponse like ? ");
			// 绑定答案类型参数
			lstpara.add("%" + ruleresponse + "%");
		}

//		if (belong != null && !"".equals(belong)) {
//			if (cityCode != null && !"".equals(cityCode) ) {
//				// 在SQL语句中加上条件
//				if (cityCode.endsWith("0000")) {
//					paramSql.append(" and city like ? ");
//					lstpara.add(cityCode.substring(0, 2) + "%");
//				} else {
//					paramSql.append(" and city = ? ");
//					lstpara.add(cityCode);
//				}
//			}
//		} else {
//			if (cityCode != null && !"".equals(cityCode)) {
//				if ("全国".equals(cityCode)){
//					if (userCityCode != null && !"".equals(userCityCode)){
//						paramSql.append("  and  (city is null or city ='全国') and (excludedcity is null or excludedcity not like ? )");
//						lstpara.add("%" + userCityCode + "%");
//					}else{
//						paramSql.append("  and  (city is null or city ='全国') ");
//					}
//				} else {
//					// 在SQL语句中加上条件
//					if (cityCode.endsWith("0000")) {
//						paramSql.append(" and city like ? ");
//						lstpara.add(cityCode.substring(0, 2) + "%");
//					} else {
//						paramSql.append(" and city = ? ");
//						lstpara.add(cityCode);
//					}
//				}
//				if ("全国".equals(cityCode) && userCityCode != null && !"".equals(userCityCode)) {
//						String cityStr = userCityCode.substring(0, 2);
//						// 在SQL语句中加上条件
//						if (userCityCode.endsWith("0000")) {
//							paramSql.append(" and (city like ? ");
//							lstpara.add(cityStr + "%");
//						} else {
//							paramSql.append(" and (city in (?,?) ");
//							lstpara.add(cityStr + "0000");// 取省级相关数据
//							lstpara.add(userCityCode);
//						}
//						paramSql.append("  or  (city is null or city ='全国')) and  (excludedcity is null or excludedcity not like ? ) ");
//						
//						lstpara.add("%" + userCityCode + "%");
//				}else if ("全国".equals(cityCode)){
//					paramSql.append("  and  (city is null or city ='全国') ");
//					
//				} else {
//					// 在SQL语句中加上条件
//					if (cityCode.endsWith("0000")) {
//						paramSql.append(" and city like ? ");
//						lstpara.add(cityCode.substring(0, 2) + "%");
//					} else {
//						paramSql.append(" and city = ? ");
//						lstpara.add(cityCode);
//					}
//				}
//			}
//		}
		
		if (cityCode != null && !"".equals(cityCode)) {
			if ("全国".equals(cityCode)){
				if (userCityCode != null && !"".equals(userCityCode)){
					paramSql.append("  and  (city is null or city ='全国') and (excludedcity is null or excludedcity not like ? )");
					lstpara.add("%" + userCityCode + "%");
				}else{
					paramSql.append("  and  (city is null or city ='全国') ");
				}
			} else {
				// 在SQL语句中加上条件
				if (cityCode.endsWith("0000")) {
					paramSql.append(" and city like ? ");
					lstpara.add(cityCode.substring(0, 2) + "%");
				} else {
					paramSql.append(" and city = ? ");
					lstpara.add(cityCode);
				}
			}
//			if ("全国".equals(cityCode) && userCityCode != null && !"".equals(userCityCode)) {
//					String cityStr = userCityCode.substring(0, 2);
//					// 在SQL语句中加上条件
//					if (userCityCode.endsWith("0000")) {
//						paramSql.append(" and (city like ? ");
//						lstpara.add(cityStr + "%");
//					} else {
//						paramSql.append(" and (city in (?,?) ");
//						lstpara.add(cityStr + "0000");// 取省级相关数据
//						lstpara.add(userCityCode);
//					}
//					paramSql.append("  or  (city is null or city ='全国')) and  (excludedcity is null or excludedcity not like ? ) ");
//					
//					lstpara.add("%" + userCityCode + "%");
//			}else if ("全国".equals(cityCode)){
//				paramSql.append("  and  (city is null or city ='全国') ");
//				
//			} else {
//				// 在SQL语句中加上条件
//				if (cityCode.endsWith("0000")) {
//					paramSql.append(" and city like ? ");
//					lstpara.add(cityCode.substring(0, 2) + "%");
//				} else {
//					paramSql.append(" and city = ? ");
//					lstpara.add(cityCode);
//				}
//			}
		}
		if (strategy != null && !"".equals(strategy) && !"undefined".equals(strategy)){
			if (ruletype.contains("2")){// 其他规则
				// 在SQL语句中加上条件
				paramSql.append(" and (ruleresponse not like '%KEY%' or ruleresponse like ?) ");
				// 绑定答案类型参数
				lstpara.add("%" + strategy + "%");
			}else{// 交互规则
				// 定位key列
				String strategySql = "select * from scenarioselement where relationserviceid=? and name='KEY'";
				Result strategyResult = Database.executeQuery(strategySql, scenariosid);
				String strategyWeight = "";
				if (strategyResult != null && strategyResult.getRowCount() > 0){
					strategyWeight = strategyResult.getRows()[0].get("weight").toString();
				}
				// 在SQL语句中加上条件
				paramSql.append(" and condition" + strategyWeight + "=? ");
				// 绑定答案类型参数
				lstpara.add(strategy);
			}
		}
		
		if (issue!=null && "scenariosrules_online".equals(issue)){
			sql = sql.replace("SCENARIOSRULES", issue);
		}
		//文件日志
		GlobalValue.myLog.info( sql + paramSql.toString() + "#" + lstpara );
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql + paramSql.toString(), lstpara
				.toArray());
		// 判断数据源不为null
		if (rs != null) {
			count = Integer.parseInt(rs.getRows()[0].get("count").toString());
		}

		return count;
	}

	/**
	 * 根据不同的条件分页查询满足条件的规则信息
	 * 
	 * @param scenariosid参数场景id
	 * @param conditions参数问题要素组合
	 * @param ruletype参数规则类型
	 * @param weight参数规则优先级
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @param userCityCode
	 *            用户cityCode
	 * @return Result
	 */
	public static JSONArray getSceneRules(String scenariosid,
			String conditions, String ruletype, String weight, int page,
			int rows, String cityCode, String cityName, String belong,
			String userCityCode, Map<String, String> cityCodeToCityName,
			String ruleresponse, String issue, String strategy) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义问题要素数组，并设定长度为20
		String[] conditionArr = new String[20];
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将conditions按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select * from (select t.*,rownum rn from(select * from(select * from SCENARIOSRULES where relationserviceid=? )h where 1>0 ";
		} else {
			sql = "select * from (select * from(select * from SCENARIOSRULES where relationserviceid=? )h where  1>0 ";
		}
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(scenariosid);
		if (ruletype != null && !"".equals(ruletype)) {
			if ("2,5".equals(ruletype)){
				sql += " and ruletype in(2,5) ";
			} else {
				sql += " and ruletype=? ";
				// 绑定规则类型参数
				lstpara.add(ruletype);
			}
		} else {
			sql += " and ruletype in(0,4) ";
		}

		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某一个问题要素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 在SQL语句中加上条件
					paramSql.append(" and condition" + (i + 1) + " like ? ");
					// 绑定对应的参数
					lstpara.add("%" + conditionArr[i] + "%");
				}
			}
		}
		// 判断规则优先级不为null且不为空
		if (weight != null && !"".equals(weight)) {
			// 在SQL语句中加上条件
			paramSql.append(" and weight = ? ");
			// 绑定答案类型参数
			lstpara.add(weight);
		}

		// 回复内容搜索
		if (ruleresponse != null && !"".equals(ruleresponse)) {
			// 在SQL语句中加上条件
			paramSql.append(" and ruleresponse like ? ");
			// 绑定答案类型参数
			lstpara.add("%" + ruleresponse + "%");
		}

//		if (belong != null && !"".equals(belong)) {
//			if (cityCode != null && !"".equals(cityCode) ) {
//				// 在SQL语句中加上条件
//				if (cityCode.endsWith("0000")) {
//					paramSql.append(" and city like ? ");
//					lstpara.add(cityCode.substring(0, 2) + "%");
//				} else {
//					paramSql.append(" and city = ? ");
//					lstpara.add(cityCode);
//				}
//			}
//		} else {
//			if (cityCode != null && !"".equals(cityCode) ) {
//				if ("全国".equals(cityCode) && userCityCode != null &&!"".equals(userCityCode)) {
//					String cityStr = userCityCode.substring(0, 2);
//					// 在SQL语句中加上条件
//					if (userCityCode.endsWith("0000")) {
//						paramSql.append(" and (city like ? ");
//						lstpara.add(cityStr + "%");
//					} else {
//						paramSql.append(" and (city in (?,?) ");
//						lstpara.add(cityStr + "0000");// 取省级相关数据
//						lstpara.add(userCityCode);
//					}
//					paramSql.append("  or  (city is null or city ='全国')) and  (excludedcity is null or excludedcity not like ? ) ");
//
//					lstpara.add("%" + userCityCode + "%");
//
//				}else if ("全国".equals(cityCode)){
//					paramSql.append("  and  (city is null or city ='全国') ");
//					
//				} else {
//					// 在SQL语句中加上条件
//					if (cityCode.endsWith("0000")) {
//						paramSql.append(" and city like ? ");
//						lstpara.add(cityCode.substring(0, 2) + "%");
//					} else {
//						paramSql.append(" and city = ? ");
//						lstpara.add(cityCode);
//					}
//				}
//			}
//		}

		if (cityCode != null && !"".equals(cityCode)) {
			if ("全国".equals(cityCode)){
				if (userCityCode != null && !"".equals(userCityCode)){
					paramSql.append("  and  (city is null or city ='全国') and (excludedcity is null or excludedcity not like ? )");
					lstpara.add("%" + userCityCode + "%");
				}else{
					paramSql.append("  and  (city is null or city ='全国') ");
				}
			} else {
				// 在SQL语句中加上条件
				if (cityCode.endsWith("0000")) {
					paramSql.append(" and city like ? ");
					lstpara.add(cityCode.substring(0, 2) + "%");
				} else {
					paramSql.append(" and city = ? ");
					lstpara.add(cityCode);
				}
			}
//			if ("全国".equals(cityCode) && userCityCode != null && !"".equals(userCityCode)) {
//					String cityStr = userCityCode.substring(0, 2);
//					// 在SQL语句中加上条件
//					if (userCityCode.endsWith("0000")) {
//						paramSql.append(" and (city like ? ");
//						lstpara.add(cityStr + "%");
//					} else {
//						paramSql.append(" and (city in (?,?) ");
//						lstpara.add(cityStr + "0000");// 取省级相关数据
//						lstpara.add(userCityCode);
//					}
//					paramSql.append("  or  (city is null or city ='全国')) and  (excludedcity is null or excludedcity not like ? ) ");
//					
//					lstpara.add("%" + userCityCode + "%");
//			}else if ("全国".equals(cityCode)){
//				paramSql.append("  and  (city is null or city ='全国') ");
//				
//			} else {
//				// 在SQL语句中加上条件
//				if (cityCode.endsWith("0000")) {
//					paramSql.append(" and city like ? ");
//					lstpara.add(cityCode.substring(0, 2) + "%");
//				} else {
//					paramSql.append(" and city = ? ");
//					lstpara.add(cityCode);
//				}
//			}
		}
		if (strategy != null && !"".equals(strategy) && !"undefined".equals(strategy)){
			if (ruletype.contains("2")){// 其他规则
				// 在SQL语句中加上条件
				paramSql.append(" and (ruleresponse not like '%KEY%' or ruleresponse like ?) ");
				// 绑定答案类型参数
				lstpara.add("%" + strategy + "%");
			}else{// 交互规则
				// 定位key列
				String strategySql = "select * from scenarioselement where relationserviceid=? and name='KEY'";
				Result strategyResult = Database.executeQuery(strategySql, scenariosid);
				String strategyWeight = "";
				if (strategyResult != null && strategyResult.getRowCount() > 0){
					strategyWeight = strategyResult.getRows()[0].get("weight").toString();
				}
				// 在SQL语句中加上条件
				paramSql.append(" and condition" + strategyWeight + "=? ");
				// 绑定答案类型参数
				lstpara.add(strategy);
			}
		}
		
		// 分页查询满足条件的规则的SQL语句
		sql = sql + paramSql;
		if (GetConfigValue.isOracle) {
			sql = sql + "order by weight )t) where rn>? and rn <=?";
		} else if (GetConfigValue.isMySQL) {
			sql = sql + "order by weight desc,ruleid desc )t limit ?,?";
		}
		
		if (issue!=null && "scenariosrules_online".equals(issue)){
			sql = sql.replace("SCENARIOSRULES", issue);
		}
		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(page * rows);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		
		//ghj update
		if(Database.isToMysql){
			Object[] objA = lstpara.toArray();
			System.out.print("--------------");
			for (int i = 0; i < objA.length; i++) {
				System.out.print(objA[i].toString()+"	");
			}
			System.out.println();
			MysqlTransfer mt = new MysqlTransfer(sql,objA);
			mt.transfer();
			sql = mt.getMysqlSql();
			objA = mt.getParams();
			GlobalValue.myLog.info(mt);
			lstpara = Arrays.asList(objA);
		}
		
		// 执行SQL语句，获取相应的数据源
		// 建立连接
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rsSet = null;
		try {
			con = Database.getCon();
			pstmt = con.prepareStatement(sql);
			for (int i = 0; i < lstpara.toArray().length; i++) {
				pstmt.setObject(i + 1, lstpara.toArray()[i]);
			}
			rsSet = pstmt.executeQuery();
			// 遍历resultSet结果
			while (rsSet.next()) {
				JSONObject obj = new JSONObject();
				// 生成id对象
				obj.put("id", rsSet.getObject("ruleid"));
				// 循环遍历condition1-10，并生成相应的对象
				for (int j = 1; j < 21; j++) {
					obj.put("condition" + j,
							rsSet.getObject("condition" + j) != null ? rsSet
									.getString("condition" + j) : "");
				}
				// 生成规则类型对象
				obj.put("type", rsSet.getObject("ruletype"));
				// 生成规则优先级对象
				obj.put("weight", rsSet.getObject("weight").toString());
				// 生成规则回复内容对象
				// obj.put("response", rs.getRows()[i].get("ruleresponse"));
				Object ruleresponse_clob = rsSet.getClob("ruleresponse");
				if (ruleresponse_clob != null && !"".equals(ruleresponse_clob)) {
					ruleresponse_clob = oracleClob2Str((Clob) ruleresponse_clob);
				}
				obj.put("response", ruleresponse_clob);

				obj.put("city", rsSet.getObject("city"));
				obj.put("cityname", rsSet.getObject("cityname"));
				obj.put("citycode", rsSet.getObject("city"));
				// 将排除地市编码转换为中文
				Object excludedcity = rsSet.getObject("excludedcity");
				String excludedcityName = "";
				if (excludedcity != null && !"".equals(excludedcity)) {
					String[] excludedcitys = excludedcity.toString().split(",");
					excludedcity = "";
					excludedcityName = "";
					for (int i = 0; i < excludedcitys.length; i++) {
						if("".equals(excludedcitys[i]))
							continue;
						excludedcity = excludedcity + excludedcitys[i] + ",";
						excludedcityName = excludedcityName
								+ cityCodeToCityName.get(excludedcitys[i])
								+ ",";
					}
					excludedcity = excludedcity.toString().substring(0,
							excludedcity.toString().lastIndexOf(","));
					excludedcityName = excludedcityName.substring(0,
							excludedcityName.lastIndexOf(","));
				}
				obj.put("excludedcity", excludedcity);
				obj.put("excludedcityName", excludedcityName);
				obj.put("abovequestionobject", rsSet
						.getObject("abovequestionobject"));
				obj.put("abovestandardquestion", rsSet
						.getObject("abovestandardquestion"));
				obj.put("questionobject", rsSet.getObject("questionobject"));
				obj
						.put("standardquestion", rsSet
								.getObject("standardquestion"));
				// obj.put("ruleresponsetemplate", rs.getRows()[i]
				// .get("ruleresponsetemplate"));
				Object ruleresponsetemplate_clob = rsSet
						.getClob("ruleresponsetemplate");
				if (ruleresponsetemplate_clob != null
						&& !"".equals(ruleresponsetemplate_clob)) {
					ruleresponsetemplate_clob = oracleClob2Str((Clob) ruleresponsetemplate_clob);
				}
				obj.put("response", ruleresponse_clob);
				obj.put("ruleresponsetemplate", ruleresponsetemplate_clob);
				obj.put("responsetype",
						rsSet.getObject("responsetype") == null ? "" : rsSet
								.getString("responsetype").toString());
				obj.put("userquestion",
						rsSet.getObject("userquestion") == null ? "" : rsSet
								.getString("userquestion").toString());
				obj.put("currentnode",
						rsSet.getObject("currentnode") == null ? "" : rsSet
								.getString("currentnode").toString());
				obj.put("isedit", rsSet.getObject("isedit"));

				// 将生成的对象放入jsonArr数组中
				jsonArr.add(obj);
			}
		} catch (SQLException e) {
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			try {
				if (rsSet != null) {
					rsSet.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (con != null) {
					con.close();
				}
			} catch (Exception ex) {
				GlobalValue.myLog.error("关闭连接异常信息==>" + ex);
			}
		}
		// 判断数据源不为null且含有数据
		return jsonArr;
	}

	/**
	 * 根据规则id删除规则信息，并返回执行记录数
	 * 
	 * @param ruleid参数规则id
	 * @return int
	 */
	public static int deleteSceneRules(User user, String serviceType,
			String ruleid, String scenariosName, String city,
			String currentcitycode, String excludedcity) {
		int c = 0;
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义数据id数组
		String[] ids = null;
		String[] cityCodes;
		String[] excludedcitys;
		// 定义数据id数组
		ids = new String[] {};
		// 判断数据id不为null且不为空
		if (ruleid != null && !"".equals(ruleid)) {
			// 将数据id按照逗号拆分
			ids = ruleid.split(",");
		}

		if (!"".equals(city) && !"全国".equals(city)) {// 判断当前用户非全国用户，删除时需做排除地市处理
			// 定义数据currentcitycode数组
			cityCodes = new String[] {};
			// 判断数据currentcitycode不为null且不为空
			if (currentcitycode != null && !"".equals(currentcitycode)) {
				// 将数据currentcitycode按照逗号拆分
				cityCodes = currentcitycode.split(",");
			}else {
				// 循环遍历数据id数组
				for (int i = 0; i < ids.length; i++) {
						sql = new StringBuilder();
						// 删除数据的SQL语句
						sql.append("delete from scenariosrules where  ruleid =? ");
						// 定义绑定参数集合
						lstpara = new ArrayList<String>();
						lstpara.add(ids[i]);
						// 将SQL语句放入集合中
						lsts.add(sql.toString());
						// 将对应的绑定参数集合放入集合
						lstlstpara.add(lstpara);
						
						//文件日志
						GlobalValue.myLog.info(user.getUserID() + "#" 
								+ sql + "#" 
								+ lstpara );

						// 将操作日志SQL语句放入集合中
						lsts.add(GetConfigValue.LogSql());
						// 将定义的绑定参数集合放入集合中
//						String brand = serviceType.split("->")[1] + "场景";
						String brand = "";
						Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
						if (rsConfig != null && rsConfig.getRowCount() > 0){
							brand = rsConfig.getRows()[0].get("name").toString();
						}
						// 定义绑定参数集合，查询要删除的规则
						lstpara = new ArrayList<String>();
						String qSql = "select ruleresponse from scenariosrules where ruleid=?";
						lstpara.add(ids[i]);
						
						//文件日志
						GlobalValue.myLog.info( qSql + "#" + lstpara );
						
						Result rs = Database.executeQuery(qSql, lstpara.toArray());
						String ruleresponse = "";
						if (rs != null && rs.getRowCount() > 0) {
							if(!Database.isToMysql)
								ruleresponse = oracleClob2Str((Clob) rs.getRows()[0]
										.get("ruleresponse"));
							else{
								Object ruleresponse_clob = rs.getRows()[0]
										.get("ruleresponse");
								if (ruleresponse_clob != null && !"".equals(ruleresponse_clob)) {
									ruleresponse = ruleresponse_clob.toString();
								}
							}
							
						}
						lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(),
								user.getUserID(), user.getUserName(), brand,
								scenariosName, "删除交互规则", (ruleresponse==null?"":(ruleresponse.substring(0,
										ruleresponse.length() > 1000 ? 1000 : ruleresponse.length())))+ruleid,
								"SCENARIOSRULES"));

						c = Database.executeNonQueryTransaction(lsts, lstlstpara);
				}
				return c;
			}
			// 定义数据excludedcitys数组
			excludedcitys = new String[] {};
			// 判断数据excludedcity不为null且不为空
			if (excludedcity != null && !"".equals(excludedcity)) {
				// 将数据excludedcity按照逗号拆分
				excludedcitys = excludedcity.split(",");
			}

			// 循环遍历数据id数组
			for (int i = 0; i < ids.length; i++) {
				// city
				String ccode = cityCodes[i];
				if ("@".equals(ccode)) {
					ccode = "";
				}
				if ("".equals(ccode) || "全国".equals(ccode) || ccode == null) {// 当前规则地市如果是全国的，添加排除地市
					continue;
				} else {// 当前地市不是全国，直接删掉
					String tempExcludedcity = "";
					sql = new StringBuilder();
					// 定义绑定参数集合
					lstpara = new ArrayList<String>();
					String tempSql = "select distinct city from scenariosrules where ruleid!=? and relationserviceid=(select relationserviceid from scenariosrules where ruleid=?) and city like '%0000' and ruletype=(select ruletype from scenariosrules where ruleid=?)";
//					String tempSql = "select distinct city from scenariosrules where ruleid!=? and scenariosid=(select scenariosid from scenariosrules where ruleid=?) and city like '%0000' and ruletype=(select ruletype from scenariosrules where ruleid=?)";
					lstpara.add(ids[i]);
					lstpara.add(ids[i]);
					lstpara.add(ids[i]);
					
					//文件日志
					GlobalValue.myLog.info( tempSql + "#" + lstpara );
					
					Result tempRs = Database.executeQuery(tempSql, lstpara.toArray());
					if (tempRs != null && tempRs.getRowCount() > 0) {
						for (int j = 0; j < tempRs.getRowCount(); j++) {
							tempExcludedcity = tempExcludedcity
									+ tempRs.getRows()[j].get("city").toString() + ",";
						}
						tempExcludedcity = tempExcludedcity.substring(0, tempExcludedcity.lastIndexOf(","));
					}
					sql = new StringBuilder();
					// 定义绑定参数集合
					lstpara = new ArrayList<String>();
					sql.append("update scenariosrules set excludedcity=? where relationserviceid=(select relationserviceid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)");
//					sql.append("update scenariosrules set excludedcity=? where scenariosid=(select scenariosid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)");
					if (tempExcludedcity != null
							&& !"".equals(tempExcludedcity)) {
						lstpara.add(tempExcludedcity);
					} else {
						lstpara.add(null);
					}
					lstpara.add(ids[i]);
					lstpara.add(ids[i]);
					// 将SQL语句放入集合中
					lsts.add(sql.toString());
					// 将对应的绑定参数集合放入集合
					lstlstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

					sql = new StringBuilder();
					// 删除数据的SQL语句
					sql.append("delete from scenariosrules where  ruleid =? ");
					// 定义绑定参数集合
					lstpara = new ArrayList<String>();
					lstpara.add(ids[i]);
					// 将SQL语句放入集合中
					lsts.add(sql.toString());
					// 将对应的绑定参数集合放入集合
					lstlstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" 
							+ sql + "#" 
							+ lstpara );

					// 将操作日志SQL语句放入集合中
					lsts.add(GetConfigValue.LogSql());
					// 将定义的绑定参数集合放入集合中
//					String brand = serviceType.split("->")[1] + "场景";
					String brand = "";
					Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
					if (rsConfig != null && rsConfig.getRowCount() > 0){
						brand = rsConfig.getRows()[0].get("name").toString();
					}
					// 定义绑定参数集合，查询要删除的规则
					lstpara = new ArrayList<String>();
					String qSql = "select ruleresponse from scenariosrules where ruleid=?";
					lstpara.add(ids[i]);
					
					//文件日志
					GlobalValue.myLog.info( qSql + "#" + lstpara );
					
					Result rs = Database.executeQuery(qSql, lstpara.toArray());
					String ruleresponse = "";
					if (rs != null && rs.getRowCount() > 0) {
						if(!Database.isToMysql)
							ruleresponse = oracleClob2Str((Clob) rs.getRows()[0]
									.get("ruleresponse"));
						else{
							Object ruleresponse_clob = rs.getRows()[0]
									.get("ruleresponse");
							if (ruleresponse_clob != null && !"".equals(ruleresponse_clob)) {
								ruleresponse = ruleresponse_clob.toString();
							}
						}
						
					}
					lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(),
							user.getUserID(), user.getUserName(), brand,
							scenariosName, "删除交互规则", (ruleresponse==null?"":(ruleresponse.substring(0,
									ruleresponse.length() > 1000 ? 1000
											: ruleresponse.length())))+ruleid,
							"SCENARIOSRULES"));

					c = Database.executeNonQueryTransaction(lsts, lstlstpara);
				}
				// }
			}

		} else {// 179
			for (int i = 0; i < ids.length; i++) {
				String tempExcludedcity = "";
				sql = new StringBuilder();
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				String tempSql = "select distinct city from scenariosrules where ruleid!=? and relationserviceid=(select relationserviceid from scenariosrules where ruleid=?) and city like '%0000' and ruletype=(select ruletype from scenariosrules where ruleid=?)";
//				 String tempSql = "select distinct city from scenariosrules where ruleid!=? and scenariosid=(select scenariosid from scenariosrules where ruleid=?) and city like '%0000' and ruletype=(select ruletype from scenariosrules where ruleid=?)";
				lstpara.add(ids[i]);
				lstpara.add(ids[i]);
				lstpara.add(ids[i]);
				
				//文件日志
				GlobalValue.myLog.info( tempSql + "#" + lstpara );
				
				Result tempRs = Database.executeQuery(tempSql, lstpara
						.toArray());
				if (tempRs != null && tempRs.getRowCount() > 0) {
					for (int j = 0; j < tempRs.getRowCount(); j++) {
						tempExcludedcity = tempExcludedcity
								+ tempRs.getRows()[j].get("city").toString()
								+ ",";
					}
					tempExcludedcity = tempExcludedcity.substring(0,
							tempExcludedcity.lastIndexOf(","));
				}
				sql = new StringBuilder();
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				sql.append("update scenariosrules set excludedcity=? where relationserviceid=(select relationserviceid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)");
//				 sql.append("update scenariosrules set excludedcity=? where scenariosid=(select scenariosid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)");
				if (tempExcludedcity != null && !"".equals(tempExcludedcity)) {
					lstpara.add(tempExcludedcity);
				} else {
					lstpara.add(null);
				}
				lstpara.add(ids[i]);
				lstpara.add(ids[i]);
				// 将SQL语句放入集合中
				lsts.add(sql.toString());
				// 将对应的绑定参数集合放入集合
				lstlstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" 
						+ sql + "#" 
						+ lstpara );

				sql = new StringBuilder();
				// 删除数据的SQL语句
				sql.append("delete from scenariosrules where  ruleid =? ");
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				lstpara.add(ids[i]);
				// 将SQL语句放入集合中
				lsts.add(sql.toString());
				// 将对应的绑定参数集合放入集合
				lstlstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" 
						+ sql + "#" 
						+ lstpara );

				// 将操作日志SQL语句放入集合中
				lsts.add(GetConfigValue.LogSql());
				// 将定义的绑定参数集合放入集合中
//				String brand = serviceType.split("->")[1] + "场景";
				String brand = "";
				Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
				if (rsConfig != null && rsConfig.getRowCount() > 0){
					brand = rsConfig.getRows()[0].get("name").toString();
				}
				// 定义绑定参数集合，查询要删除的规则
				lstpara = new ArrayList<String>();
				String qSql = "select ruleresponse from scenariosrules where ruleid=?";
				lstpara.add(ids[i]);
				
				//文件日志
				GlobalValue.myLog.info( qSql + "#" + lstpara );
				
				Result rs = Database.executeQuery(qSql, lstpara.toArray());
				String ruleresponse = "";
				if (rs != null && rs.getRowCount() > 0) {
//					ruleresponse = oracleClob2Str((Clob) rs.getRows()[0]
//							.get("ruleresponse"));
					if(!Database.isToMysql)
						ruleresponse = oracleClob2Str((Clob) rs.getRows()[0]
								.get("ruleresponse"));
					else{
						Object ruleresponse_clob = rs.getRows()[0]
								.get("ruleresponse");
						if (ruleresponse_clob != null && !"".equals(ruleresponse_clob)) {
							ruleresponse =  ruleresponse_clob.toString();
						}
					}
				}
				lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(),
						user.getUserID(), user.getUserName(), brand,
						scenariosName, "删除交互规则", (ruleresponse==null?"":(ruleresponse.substring(0,
								ruleresponse.length() > 1000 ? 1000
										: ruleresponse.length())))+ruleid,
						"SCENARIOSRULES"));

				c = Database.executeNonQueryTransaction(lsts, lstlstpara);
			}
		}
		// String tempSql =
		// "select distinct city from scenariosrules where city!='全国' and ruleid not in ("
		// + ruleid + ")";
		// Result tempRs = Database.executeQuery(tempSql);
		// String tempExcludedcity = "";
		// if (tempRs != null && tempRs.getRowCount() > 0){
		// for (int i = 0;i < tempRs.getRowCount();i++){
		// tempExcludedcity = tempExcludedcity +
		// tempRs.getRows()[i].get("city").toString() + ",";
		// }
		// tempExcludedcity = tempExcludedcity.substring(0,
		// tempExcludedcity.lastIndexOf(","));
		// }
		// sql = new StringBuilder();
		// // 定义绑定参数集合
		// lstpara = new ArrayList<String>();
		// sql.append("update scenariosrules set excludedcity=? where scenariosid=(select scenariosid from scenariosrules where ruleid="+ids[0]+")and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid="+ids[0]+")");
		// if (tempExcludedcity != null && !"".equals(tempExcludedcity)){
		// lstpara.add(tempExcludedcity);
		// } else {
		// lstpara.add(null);
		// }
		// // lstpara.add(ids[0]);
		// // lstpara.add(ids[0]);
		// // 将SQL语句放入集合中
		// lsts.add(sql.toString());
		// // 将对应的绑定参数集合放入集合
		// lstlstpara.add(lstpara);
		// 执行SQL语句，绑定事务处理，并返回事务处理的结果
		// int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		return c;
	}

	/**
	 * 更新规则中需要修改的值，并返回执行记录数
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合
	 * @param weight参数规则优先级
	 * @param ruletype参数规则类型
	 * @param ruleresponse参数规则回复内容
	 * @param ruleid参数规则id
	 * @param userquestion
	 *            用户问题
	 * @return int
	 */
	public static int updateSceneRules(User user, String serviceType,
			String scenariosid, String scenariosName, String conditions,
			String weight, String ruletype, String ruleresponse, String ruleid,
			String service, String abs, String responsetype,
			String ruleresponsetemplate, String cityCode, String cityName,
			String excludedcity, String questionobject,
			String standardquestion, String userquestion,String currentnode) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();

		// add by xzh

		String tempExcludedcity = "";
		sql = new StringBuilder();
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 查出除要修改的规则以外，存在哪些非全国地市
		String tempSql = "select distinct city from scenariosrules where ruleid!=? and relationserviceid=(select relationserviceid from scenariosrules where ruleid=?) and city like '%0000' and ruletype=(select ruletype from scenariosrules where ruleid=?)";
//		 String tempSql = "select distinct city from scenariosrules where ruleid!=? and scenariosid=(select scenariosid from scenariosrules where ruleid=?) and city like '%0000' and ruletype=(select ruletype from scenariosrules where ruleid=?)";
		lstpara.add(ruleid);
		lstpara.add(ruleid);
		lstpara.add(ruleid);
		
		//文件日志
		GlobalValue.myLog.info( tempSql + "#" + lstpara );
		
		Result tempRs = Database.executeQuery(tempSql, lstpara.toArray());
		if (tempRs != null && tempRs.getRowCount() > 0) {
			for (int j = 0; j < tempRs.getRowCount(); j++) {
				tempExcludedcity = tempExcludedcity
						+ tempRs.getRows()[j].get("city").toString() + ",";
			}
			tempExcludedcity = tempExcludedcity.substring(0, tempExcludedcity
					.lastIndexOf(","));
		}
		// if (excludedcity != null && !"".equals(excludedcity)){
		// tempExcludedcity = excludedcity;
		// }
		sql = new StringBuilder();
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		sql.append("update scenariosrules set excludedcity=? where relationserviceid=(select relationserviceid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)");
//		 sql.append("update scenariosrules set excludedcity=? where scenariosid=(select scenariosid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)");
		if (tempExcludedcity != null && !"".equals(tempExcludedcity)) {
			lstpara.add(tempExcludedcity);
		} else {
			lstpara.add(null);
		}
		lstpara.add(ruleid);
		lstpara.add(ruleid);
		// 将SQL语句放入集合中
		lsts.add(sql.toString());
		// 将对应的绑定参数集合放入集合
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ lstpara );

		// 判断是否要在排除地市中去除原有city
		// String tempSql ="";
		// List<Object> templstpara = new ArrayList<Object>();
		// tempSql =
		// "select count(*) as total from scenariosrules where scenariosid=? and ruletype=? and city=(select city from scenariosrules where ruleid=?)";
		// templstpara.add(scenariosid);
		// templstpara.add(ruletype);
		// templstpara.add(ruleid);
		// Result tempRs = Database.executeQuery(tempSql,templstpara.toArray());
		// if (tempRs != null && tempRs.getRowCount() > 0){
		// int total =
		// Integer.parseInt(tempRs.getRows()[0].get("total").toString());
		// if (total == 1){
		// templstpara = new ArrayList<Object>();
		// tempSql =
		// "select ruleid,EXCLUDEDCITY from scenariosrules where scenariosid=? and ruletype=? and (city is null or city='全国')";
		// templstpara.add(scenariosid);
		// templstpara.add(ruletype);
		// tempRs = null;
		// tempRs = Database.executeQuery(tempSql,templstpara.toArray());
		// if (tempRs != null && tempRs.getRowCount() > 0){
		// for (int i = 0;i < tempRs.getRowCount() ; i++){
		// String tempExcludedCity =
		// tempRs.getRows()[i].get("EXCLUDEDCITY").toString();
		// String tempRuleID = tempRs.getRows()[i].get("ruleid").toString();
		// tempExcludedCity = tempExcludedCity.replace(currentcitycode + ",",
		// "").replace("," + currentcitycode, "").replace(currentcitycode, "");
		// sql = new StringBuilder();
		// lstpara = new ArrayList<Object>();
		// sql.append("update scenariosrules set EXCLUDEDCITY=? where ruleid=?");
		// lstpara.add(tempExcludedCity);
		// lstpara.add(tempRuleID);
		// // 将SQL语句放入集合中
		// lsts.add(sql.toString());
		// // 将对应的绑定参数集合放入集合
		// lstlstpara.add(lstpara);
		// }
		// }
		// }
		// }
		sql = new StringBuilder();
		lstpara = new ArrayList<Object>();
		// 定义问题要素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null || !"".equals(conditions)) {
			// 将问题要素组合按照@拆分
			conditionArr = conditions.split("@", 20);
		}
		// 绑定规则类型参数
		lstpara.add(ruletype);
		// 绑定规则回复内容
		lstpara.add(ruleresponse.trim());
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 更新规则的SQL语句
		sql.append("update SCENARIOSRULES set ");
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 循环遍历问题要素数组
			for (int i = 0; i < conditionArr.length; i++) {
				// 判断某个问题要素不为null且不为空
				if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
					// 在更新的SQL语句中添加condition的条件
					sql.append(" condition" + (i + 1) + "=?, ");
					// 绑定对应的参数
					lstpara.add(conditionArr[i]);
				} else {
					// 在更新的SQL语句中添加condition为null的条件
					sql.append(" condition" + (i + 1) + " = null, ");
				}
			}
		}
		// 补充完整更新的SQL语句
		sql
				.append("ruleresponse=?,weight=? ,city=?,cityname=?,excludedcity=?,ruletype=?,isedit=1 ");
		// 绑定规则回复内容参数
		lstpara.add(ruleresponse.trim());
		// 绑定规则优先级参数
		lstpara.add(weight);
		lstpara.add(cityCode);
		lstpara.add(cityName);
		lstpara.add(excludedcity);
		lstpara.add(ruletype);

		// if("2".equals(responsetype)){
		// sql.append(" ,responsetype=2 ,ruleresponsetemplate=? ");
		// lstpara.add(ruleresponsetemplate);
		// }else{
		sql.append(" ,responsetype=? ,ruleresponsetemplate=? ,userquestion=? ,currentnode=?");
		lstpara.add(responsetype);
		lstpara.add(ruleresponsetemplate);
		lstpara.add(userquestion);
		lstpara.add(currentnode);
		// }

		if (!"".equals(service) && service != null) {
			if (!"".equals(abs) && abs != null) {
				sql
						.append("  ,ABOVEQUESTIONOBJECT =? , ABOVESTANDARDQUESTION=? ");
				lstpara.add(service);
				lstpara.add(abs);
			} else {
				sql
						.append(" ,ABOVEQUESTIONOBJECT =?, ABOVESTANDARDQUESTION=null ");
				lstpara.add(service);
			}

		}

		if (!"".equals(questionobject) && questionobject != null) {
			if (!"".equals(standardquestion) && standardquestion != null) {
				sql.append("  ,questionobject =? , standardquestion=? ");
				lstpara.add(questionobject);
				lstpara.add(standardquestion);
			} else {
				sql.append(" ,questionobject =?, standardquestion=null ");
				lstpara.add(questionobject);
			}

		}

		sql.append(" where ruleid=? ");
		// 绑定规则id参数
		lstpara.add(ruleid);
		// 将SQL语句放入集合中
		lsts.add(sql.toString());
		// 将对应的绑定参数集合放入集合
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ lstpara );

		/**
		 * if (cityCode.endsWith("00")){// 非全国 sql = new StringBuilder();
		 * lstpara = new ArrayList<Object>(); sql.append("update SCENARIOSRULES set EXCLUDEDCITY=EXCLUDEDCITY||? where scenariosid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY not like ?"
		 * ); lstpara.add("," + cityCode); lstpara.add(scenariosid);
		 * lstpara.add(ruletype); lstpara.add("%" + cityCode + "%"); //
		 * 将SQL语句放入集合中 lsts.add(sql.toString()); // 将对应的绑定参数集合放入集合
		 * lstlstpara.add(lstpara);
		 * 
		 * sql = new StringBuilder(); lstpara = new ArrayList<Object>();
		 * sql.append("update SCENARIOSRULES set EXCLUDEDCITY=? where scenariosid=? and ruletype=? and (city is null or city='全国') and EXCLUDEDCITY is null"
		 * ); lstpara.add(cityCode); lstpara.add(scenariosid);
		 * lstpara.add(ruletype); // 将SQL语句放入集合中 lsts.add(sql.toString()); //
		 * 将对应的绑定参数集合放入集合 lstlstpara.add(lstpara); // } else if
		 * (cityCode.endsWith("00") && !cityCode.endsWith("0000")){// 市级 // sql
		 * = new StringBuilder(); // lstpara = new ArrayList<Object>(); //
		 * sql.append("update SCENARIOSRULES set EXCLUDEDCITY=EXCLUDEDCITY||? where scenariosid=? and ruletype=? and (city is null or city='全国' or city =?) and EXCLUDEDCITY is not like ?"
		 * ); // lstpara.add("," + cityCode); // lstpara.add(scenariosid); //
		 * lstpara.add(ruletype); // lstpara.add(cityCode.substring(0,
		 * 2)+"0000"); // lstpara.add("%" + cityCode + "%"); // // 将SQL语句放入集合中
		 * // lsts.add(sql.toString()); // // 将对应的绑定参数集合放入集合 //
		 * lstlstpara.add(lstpara); // // sql = new StringBuilder(); // lstpara
		 * = new ArrayList<Object>(); // sql.append("update SCENARIOSRULES set EXCLUDEDCITY=? where scenariosid=? and ruletype=? and (city is null or city='全国' or city =?) and EXCLUDEDCITY is null"
		 * ); // lstpara.add(cityCode); // lstpara.add(scenariosid); //
		 * lstpara.add(ruletype); // lstpara.add(cityCode.substring(0,
		 * 2)+"0000"); // // 将SQL语句放入集合中 // lsts.add(sql.toString()); // //
		 * 将对应的绑定参数集合放入集合 // lstlstpara.add(lstpara);
		 * 
		 * // //将非全国的修改规则的排除地市置空 // sql = new StringBuilder(); // lstpara = new
		 * ArrayList<Object>(); // sql.append(
		 * "update SCENARIOSRULES set EXCLUDEDCITY=? where ruleid=?"); //
		 * lstpara.add(null); // lstpara.add(ruleid); // // 将SQL语句放入集合中 //
		 * lsts.add(sql.toString()); // // 将对应的绑定参数集合放入集合 //
		 * lstlstpara.add(lstpara); }
		 */

		// 将操作日志SQL语句放入集合中
		lsts.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
//		String brand = serviceType.split("->")[1] + "场景";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		String operation = "";// 操作类型
		// if("0".equals(ruletype)||"4".equals(ruletype)){
		operation = "更新交互规则";
		// }else if("3".equals(ruletype)){
		// operation = "更新语意理解规则";
		// }else{
		// operation = "更新其他规则";
		// }
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), brand, scenariosName,
				operation, ruleresponse.substring(0,
						ruleresponse.length() > 1000 ? 1000 : ruleresponse
								.length()), "SCENARIOSRULES"));

		// 执行更新SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		int d = updateExcludedcityBy000000(ruleid);
		return c + d;
	}

	public static int updateExcludedcityBy000000(String ruleid) {

		int rs = -1;
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();

		// add by xzh

		String tempExcludedcity = "";
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 查出除要修改的规则以外，存在哪些非全国地市
		String tempSql = "select distinct city from scenariosrules where relationserviceid=(select relationserviceid from scenariosrules where ruleid=?) and city like '%0000' and ruletype=(select ruletype from scenariosrules where ruleid=?)";
//		 String tempSql = "select distinct city from scenariosrules where scenariosid=(select scenariosid from scenariosrules where ruleid=?) and city like '%0000' and ruletype=(select ruletype from scenariosrules where ruleid=?)";
		lstpara.add(ruleid);
		lstpara.add(ruleid);
		
		//文件日志
		GlobalValue.myLog.info( tempSql + "#" + lstpara );
		
		Result tempRs = Database.executeQuery(tempSql, lstpara.toArray());
		if (tempRs != null && tempRs.getRowCount() > 0) {
			for (int j = 0; j < tempRs.getRowCount(); j++) {
				tempExcludedcity = tempExcludedcity
						+ tempRs.getRows()[j].get("city").toString() + ",";
			}
			tempExcludedcity = tempExcludedcity.substring(0, tempExcludedcity
					.lastIndexOf(","));
		}
		// if (excludedcity != null && !"".equals(excludedcity)){
		// tempExcludedcity = excludedcity;
		// }
		sql = new StringBuilder();
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		sql.append("update scenariosrules set excludedcity=? where relationserviceid=(select relationserviceid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)");
//		 sql.append("update scenariosrules set excludedcity=? where scenariosid=(select scenariosid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)");
		if (tempExcludedcity != null && !"".equals(tempExcludedcity)) {
			lstpara.add(tempExcludedcity);
		} else {
			lstpara.add(null);
		}
		lstpara.add(ruleid);
		lstpara.add(ruleid);
		// 将SQL语句放入集合中
		lsts.add(sql.toString());
		// 将对应的绑定参数集合放入集合
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行更新SQL语句，绑定事务处理，并返回事务处理的结果
		rs = Database.executeNonQueryTransaction(lsts, lstlstpara);
		return rs;
	}

	/**
	 *添加场景要素
	 * 
	 * @param scenariosid
	 *            场景ID
	 *@param infotalbepath
	 *            对应信息表
	 *@param city
	 *            地市编码
	 *@param cityname
	 *            地市名称
	 *@param itemmode
	 *            选项填写方式
	 *@param name
	 *            场景要素名称
	 *@param interpat
	 *            交互模板
	 *@param weight
	 *            优先级
	 *@param wordclass
	 *            词类名称
	 *@param serviceType
	 *            四层结构串
	 *@return
	 *@returnType int
	 */

	public static int insertElementName(User user, String scenariosid,
			String scenariosName, String infotalbepath, String city,
			String cityname, String itemmode, String name, String interpat,
			String weight, String wordclass, String serviceType,
			String container) {
		int count = -1;
		// 1.先查询词类是否存在
		// 定义查询词类是否存在的SQL语句
		String sql = null;
		List<Object> lstpara = null;
		Result rs = null;
		String wordclassid = null;
		if (!"".equals(wordclass) && wordclass != null) {// 词类不为空判断是否存在词库中
			sql = "select wordclassid from wordclass where wordclass=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词类名称参数
			lstpara.add(wordclass);
			// 执行SQL语句，获取相应的数据源
			rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 获取词类id值
				wordclassid = rs.getRows()[0].get("wordclassid").toString();
			} else {// 词类不存在
				return -2;
			}
		} else if (!"全行业".equals(user.getCustomer())){//非全行业用户
			sql = "select wordclassid from wordclass where wordclass=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词类名称参数
			lstpara.add(scenariosName + name + "父子句");
			// 执行SQL语句，获取相应的数据源
			rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 获取词类id值
				wordclassid = rs.getRows()[0].get("wordclassid").toString();
			} else {// 词类不存在
				sql  = "insert into wordclass (wordclassid,wordclass,time,container) values (seq_wordclass_id.nextval,'" + scenariosName + name + "父子句" + "',sysdate,'子句')";
				int tt = -1;
				try {
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" 
							+ sql );
					tt = Database.executeNonQuery(sql);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (tt > 0){
					sql = "select wordclassid from wordclass where wordclass=?";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定词类名称参数
					lstpara.add(scenariosName + name + "父子句");
					// 执行SQL语句，获取相应的数据源
					rs = Database.executeQuery(sql, lstpara.toArray());
					
					//文件日志
					GlobalValue.myLog.info( sql + "#" + lstpara );
					
					// 判断数据源不为null且含有数据
					if (rs != null && rs.getRowCount() > 0) {
						// 获取词类id值
						wordclassid = rs.getRows()[0].get("wordclassid").toString();
					}
				}
			}
		}

		// 2.查询问题要素是否重复
		// 查询要添加的场景要素是否重复
		sql = "select * from scenarioselement where name=? and relationserviceid=? ";
//		 sql = "select * from scenarioselement where name=? and scenariosid=? ";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定问题要素名称参数
		lstpara.add(name);
		// 绑定场景id参数
		lstpara.add(scenariosid);
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为空且含有数据
		if (rs != null && rs.getRowCount() > 0) {// 场景名称已存在
			return -3;
		} else {
			// 定义多条SQL语句集合
			List<String> lstSql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstLstpara = new ArrayList<List<?>>();
			// 问题要素不存在
			// 插入问题要素的SQL语句
			sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,relationserviceid,WEIGHT,WORDCLASSID ,INFOTALBEPATH,CITY,CITYNAME,INTERPAT,ITEMMODE,container) values (?,?,?,?,?,?,?,?,?,?,?)";
//			 sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,SCENARIOSID,WEIGHT,WORDCLASSID ,INFOTALBEPATH,CITY,CITYNAME,INTERPAT,ITEMMODE,container) values (?,?,?,?,?,?,?,?,?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 获取插入问题要素表的序列,并绑定问题要素id参数
			String scenarioselementid = "";
			String bussinessFlag = CommonLibMetafieldmappingDAO
					.getBussinessFlag(serviceType);
			if (GetConfigValue.isOracle) {
				scenarioselementid = ConstructSerialNum.GetOracleNextValNew(
						"seq_scenarioselement_id", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				scenarioselementid = ConstructSerialNum
						.getSerialIDNew("scenarioselement",
								"scenarioselementid", bussinessFlag);
			}
			// 绑定参数
			lstpara.add(scenarioselementid);
			lstpara.add(name);
			lstpara.add(scenariosid);
			lstpara.add(weight);
			lstpara.add(wordclassid);
			lstpara.add(infotalbepath);

			lstpara.add(city);
			lstpara.add(cityname);
			lstpara.add(interpat);
			lstpara.add(itemmode);
			lstpara.add(container);
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" 
					+ sql + "#" 
					+ lstpara );

			// 将操作日志SQL语句放入集合中
			lstSql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
//			String brand = serviceType.split("->")[1] + "场景";
			String brand = "";
			Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
			if (rsConfig != null && rsConfig.getRowCount() > 0){
				brand = rsConfig.getRows()[0].get("name").toString();
			}
			lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), brand, scenariosName,
					"增加场景要素", name, "SCENARIOSELEMENT"));

			// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
			count = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		}

		return count;
	}

	/**
	 *修改场景要素
	 * 
	 * @param scenariosid
	 *            场景ID
	 *@param scenarioselementid
	 *            场景元素ID
	 *@param infotalbepath
	 *            对应信息表
	 *@param city
	 *            地市编码
	 *@param cityname
	 *            地市名称
	 *@param itemmode
	 *            选项填写方式
	 *@param name
	 *            场景要素名称
	 *@param interpat
	 *            交互模板
	 *@param weight
	 *            优先级
	 *@param oldweight
	 *            旧优先级
	 *@param wordclass
	 *            词类名称
	 *@param serviceType
	 *            四层结构串
	 *@return
	 *@returnType int
	 */

	public static int updateElementName(User user, String scenariosid,
			String scenariosName, String scenarioselementid,
			String infotalbepath, String city, String cityname,
			String itemmode, String name, String interpat, String weight,
			String oldweight, String wordclass, String serviceType,
			String container) {
		int count = -1;
		// 1.先查询词类是否存在
		// 定义查询词类是否存在的SQL语句
		String sql = null;
		List<Object> lstpara = null;
		Result rs = null;
		String wordclassid = null;
		if (!"".equals(wordclass) && wordclass != null) {// 词类不为空判断是否存在词库中
			sql = "select wordclassid from wordclass where wordclass=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词类名称参数
			lstpara.add(wordclass);
			// 执行SQL语句，获取相应的数据源
			rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 获取词类id值
				wordclassid = rs.getRows()[0].get("wordclassid").toString();
			} else {// 词类不存在
				return -2;
			}
		}

		// 2.查询问题要素是否重复
		// 查询要添加的场景要素是否重复
		sql = "select * from scenarioselement where name=? and relationserviceid=?  and WEIGHT=?  and ITEMMODE=? ";
//		 sql = "select * from scenarioselement where name=? and scenariosid=?  and WEIGHT=?  and ITEMMODE=? ";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定问题要素名称参数
		lstpara.add(name);
		// 绑定场景id参数
		lstpara.add(scenariosid);
		lstpara.add(weight);
		lstpara.add(itemmode);
		if ("".equals(city) || city == null) {
			sql = sql + " and city is null ";
		} else {
			sql = sql + " and city=?";
			lstpara.add(city);
		}
		if ("".equals(wordclassid) || wordclassid == null) {
			sql = sql + " and wordclassid is null ";
		} else {
			sql = sql + " and wordclassid=?";
			lstpara.add(wordclassid);
		}
		if ("".equals(infotalbepath) || infotalbepath == null) {
			sql = sql + " and infotalbepath is null ";
		} else {
			sql = sql + " and infotalbepath=?";
			lstpara.add(infotalbepath);
		}

		if ("".equals(interpat) || interpat == null) {
			sql = sql + " and interpat is null ";
		} else {
			sql = sql + " and interpat=?";
			lstpara.add(interpat);
		}

		if ("".equals(container) || interpat == null) {
			sql = sql + " and container is null ";
		} else {
			sql = sql + " and container=?";
			lstpara.add(interpat);
		}

		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为空且含有数据
		if (rs != null && rs.getRowCount() > 0) {// 场景名称已存在
			return -3;
		} else {
			// 定义多条SQL语句集合
			List<String> lstSql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstLstpara = new ArrayList<List<?>>();
			// 问题要素不存在
			// 插入问题要素的SQL语句
			sql = " update scenarioselement set NAME=?,WEIGHT=?,WORDCLASSID=? ,INFOTALBEPATH=?,CITY=?,CITYNAME=?,INTERPAT=?,ITEMMODE=?,container=?  where SCENARIOSELEMENTID=? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			lstpara.add(name);
			lstpara.add(weight);
			lstpara.add(wordclassid);
			lstpara.add(infotalbepath);
			lstpara.add(city);
			lstpara.add(cityname);
			lstpara.add(interpat);
			lstpara.add(itemmode);
			lstpara.add(container);
			lstpara.add(scenarioselementid);
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合
			lstLstpara.add(lstpara);
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" 
					+ sql + "#" 
					+ lstpara );

			// 将操作日志SQL语句放入集合中
			lstSql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
//			String brand = serviceType.split("->")[1] + "场景";
			String brand = "";
			Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
			if (rsConfig != null && rsConfig.getRowCount() > 0){
				brand = rsConfig.getRows()[0].get("name").toString();
			}
			lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), brand, scenariosName,
					"更新场景要素", name, "SCENARIOSELEMENT"));

			// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
			count = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		}

		return count;
	}

	/**
	 * 通过场景ID产寻摘要
	 * 
	 * @param scenariosid
	 *            业务id
	 * @return
	 */
	public static Result getColumnByScenariosid(String scenariosid) {
		String sql = "select * from scenarioselement where relationserviceid="
//		 String sql = "select * from scenarioselement where scenariosid="
				+ scenariosid;
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 构造场景下拉
	 * 
	 * @param srviceType
	 * @return
	 */
	public static Result getScenariosByserviceType(String serviceType,
			String citySelect) {
//		String brand = serviceType.split("->")[1] + "场景";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		String sql = "select * from service where brand='"
		// String sql = "select * from scenarios where serviceType='"
				+ brand + "'  and service !='电信集团场景' ";
		// + serviceType + "'  and name !='场景名称' ";
		if (citySelect != null && !"".equals(citySelect)
				&& !"全国".equals(citySelect)) {
			sql += " and (city like '%" + citySelect + "%' or city is null or city='全国')";
		}
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 通过场景ID,优先级获得列信息
	 * 
	 * @param scenariosid
	 *            业务id
	 * @param weight
	 * @return
	 */
	public static Result getColumnByScenariosidAndWeight(String scenariosid,
			String weight) {
		String sql = "select * from scenarioselement where relationserviceid="
//		 String sql = "select * from scenarioselement where scenariosid="
				+ scenariosid + " and  WEIGHT=" + weight;
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 通过业务ID产寻摘要
	 * 
	 * @param scenariosid
	 *            业务id
	 * @return
	 */
	public static Result getColumnCityByScenariosidAndColumn(
			String scenariosid, String[] columnArr) {
		String sql = "select * from scenarioselement where relationserviceid="
//		 String sql = "select * from scenarioselement where scenariosid="
				+ scenariosid + "and ( name= ";
		for (int i = 0; i < columnArr.length; i++) {
			if (i == 0) {
				sql = sql + "'" + columnArr[i] + "' ";
			} else {
				sql = sql + " or name='" + columnArr[i] + "' ";
			}
		}
		sql = sql + ")";

		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	/**
	 *修改场景要素city
	 * 
	 * @param list
	 *            需修改数据集合
	 *@return
	 *@returnType int
	 */

	public static int updateElementCity(List<List<String>> list) {
		int count = -1;
		// 1.先查询词类是否存在
		// 定义查询词类是否存在的SQL语句
		String sql = null;
		List<Object> lstpara = null;
		Result rs = null;
		String wordclassid = null;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		for (int i = 0; i < list.size(); i++) {
			// 插入问题要素的SQL语句
			sql = " update scenarioselement set city=?,cityname=?  where SCENARIOSELEMENTID=? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			lstpara.add(list.get(i).get(1));
			lstpara.add(list.get(i).get(2));
			lstpara.add(list.get(i).get(0));
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合
			lstLstpara.add(lstpara);
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}

		// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
		count = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		return count;
	}

	/**
	 *修改场景要素city ,和规则
	 * 
	 * @param list
	 *            需修改数据集合
	 *@return
	 *@returnType int
	 */

	public static int updateElementCityAndRules(List<List<String>> list) {
		int count = -1;
		// 1.先查询词类是否存在
		// 定义查询词类是否存在的SQL语句
		String sql = null;
		List<Object> lstpara = null;
		Result rs = null;
		String wordclassid = null;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		for (int i = 0; i < list.size(); i++) {
			// 插入问题要素的SQL语句
			sql = " update scenarioselement set city=?,cityname=?  where SCENARIOSELEMENTID=? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			lstpara.add(list.get(i).get(1));
			lstpara.add(list.get(i).get(2));
			lstpara.add(list.get(i).get(0));
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			sql = "update  scenariosrules  set condition" + list.get(i).get(3)
					+ "= null where relationserviceid=?  and ruletype =? ";
//			 + "= null where scenariosid=?  and ruletype =? ";
			lstpara = new ArrayList<Object>();
			lstpara.add(list.get(i).get(5));
			lstpara.add(list.get(i).get(6));
			String city = list.get(i).get(4);
			if ("".equals(city)) {
				sql = sql + " and city is null ";
			} else {
				sql = sql + " and city=? ";
				lstpara.add(city);
			}
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合
			lstLstpara.add(lstpara);

			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}

		// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
		count = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		return count;
	}

	/**
	 *修改场景要素city ,和规则
	 * 
	 * @param list
	 *            需修改数据集合
	 *@return
	 *@returnType int
	 */

	public static int updateMenuitems(String scenariosid, String rulesid,
			String answer, String interpat) {
		int count = -1;
		String sql = null;
		List<Object> lstpara = null;
		Result rs = null;
		String wordclassid = null;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		sql = "update  scenariosrules  set RESPONSETYPE=2, RULERESPONSE =? ,RULERESPONSETEMPLATE=? where relationserviceid=? and ruleid=? ";
//		 sql = "update  scenariosrules  set RESPONSETYPE=2, RULERESPONSE =? ,RULERESPONSETEMPLATE=? where scenariosid=? and ruleid=? ";
		lstpara = new ArrayList<Object>();
		lstpara.add(answer);
		lstpara.add(interpat);
		lstpara.add(scenariosid);
		lstpara.add(rulesid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
		count = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		return count;
	}

	/**
	 *@description 获得当前场景下符合规则的规则最大优先级值
	 *@param scenariosid
	 *            场景ID
	 *@return
	 *@returnType int
	 */
	public static double getMaxWeight(String scenariosid) {
		double w = 0;

		String sql = " select max(weight) w from scenariosrules where relationserviceid ="
//		String sql = " select max(weight) w from scenariosrules where scenariosid ="
				+ scenariosid
				+ "and weight < 99999";

		//文件日志
		GlobalValue.myLog.info( sql );
		
		Result rs = Database.executeQuery(sql);
		if (rs != null) {
			w = Double.parseDouble(rs.getRows()[0].get("w") == null ? "0" : rs
					.getRows()[0].get("w").toString());

		}
		return w;
	}

	/**
	 *@description 修改规则优先级
	 *@param currentWeight
	 *            当前规则优先级
	 *@param currentRuleid
	 *            当前规则ID
	 *@param beforeWeight
	 *            当前规则前一级优先级
	 *@param beforeRuleid
	 *            当前规则前一级规则ID
	 *@return
	 *@returnType int
	 */
	public static int updateWeight(String currentWeight, String currentRuleid,
			String beforeWeight, String beforeRuleid) {
		int count = -1;
		String sql = null;
		List<Object> lstpara = null;
		Result rs = null;
		String wordclassid = null;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		sql = "update  scenariosrules  set Weight =? where  ruleid=? ";
		lstpara = new ArrayList<Object>();
		lstpara.add(beforeWeight);
		lstpara.add(currentRuleid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合
		lstLstpara.add(lstpara);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		sql = "update  scenariosrules  set Weight =? where  ruleid=? ";
		lstpara = new ArrayList<Object>();
		lstpara.add(currentWeight);
		lstpara.add(beforeRuleid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合
		lstLstpara.add(lstpara);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
		count = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		return count;
	}
	
	public static int setBottomRule( String currentRuleid) {
		int count = -1;
		String sql = null;
		List<Object> lstpara = null;
		Result rs = null;
		String wordclassid = null;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		sql = "update  scenariosrules  set Weight =? where  ruleid=? ";
		lstpara = new ArrayList<Object>();
		lstpara.add("99999");
		lstpara.add(currentRuleid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合
		lstLstpara.add(lstpara);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
		count = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		return count;
	}

	public static int setTopRule( String currentRuleid) {
		int count = -1;
		String sql = null;
		List<Object> lstpara = null;
		Result rs = null;
		String wordclassid = null;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		sql = "update  scenariosrules  set Weight =? where  ruleid=? ";
		lstpara = new ArrayList<Object>();
		lstpara.add("0");
		lstpara.add(currentRuleid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合
		lstLstpara.add(lstpara);

		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
		count = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		return count;
	}

	
	
	public static Result getScenariosTree(String serviceType, String name) {
//		String brand = serviceType.split("->")[1] + "场景";
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		Result rs = null;
		String sql = "";

		sql = "SELECT * FROM service start " + "WITH serviceid='" + name + "'"
				+ " and brand ='" + brand
				+ "' connect BY nocycle prior parentid=serviceid and serviceid!=0　";
		// sql = "SELECT * FROM scenarios start " + "WITH name='" + name + "'"
		// + " and servicetype ='" + serviceType
		// + "' connect BY nocycle prior parentid=scenariosid";
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	public static ArrayList<String> getScenariosIDByName(String name) {
		ArrayList<String> li = new ArrayList<String>();
		Result rs = null;
		String sql = "";
		sql = "SELECT DISTINCT ss.serviceid scenariosid FROM (SELECT * FROM service start WITH serviceid = '"
				+ name + "'　connect BY nocycle prior parentid = serviceid and serviceid!=0　) ss ";
		// sql =
		// "SELECT DISTINCT ss.scenariosid FROM (SELECT * FROM scenarios start WITH name = '"
		// + name
		// + "'　connect BY nocycle prior parentid = scenariosid) ss ";
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		for (int i = 0; i < rs.getRowCount(); i++) {
			li.add(rs.getRows()[i].get("scenariosid").toString());
		}
		return li;

	}

	public static Boolean hasChildrenByScenariosid(String scenariosid) {
		String sql;
		sql = "select serviceid as scenariosid from service where parentid in ("
				// sql =
				// "select scenariosid from scenarios where rownum<2 and parentid in ("
				+ scenariosid + ") and rownum<2 ";
		Result res = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		if (res == null || res.getRowCount() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * Description:将Clob对象转换为String对象,Blob处理方式与此相同
	 * 
	 * @param clob
	 */
	public static String oracleClob2Str(Clob clob) {
		try {
			return (clob != null ? clob.getSubString((long) 1, (int) clob
					.length()) : null);
		} catch (SQLException e) {
			return null;
		}
	}

	public static int editmenu(User user, String serviceType,
			String scenariosid, String name, String oldName) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();

		String sql = "";
		List<Object> lstpara = new ArrayList<Object>();
		sql = "update service set service=? where serviceid=?";
		// 添加名称要素
		lstpara.add(name);
		// 添加id参数
		lstpara.add(scenariosid);
		// 将sql放入集合
		lstSql.add(sql);
		// 将绑定参数放入集合
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ lstpara );

		sql = "update scenarioselement set name=? where relationserviceid=? and name like '%_知识名称%'";
		lstpara = new ArrayList<Object>();
		// 添加名称要素
		if (name.endsWith("场景")) {
			lstpara.add(name + "_知识名称");
		} else {
			lstpara.add(name + "场景_知识名称");
		}
		// 添加id参数
		lstpara.add(scenariosid);
		// 将sql放入集合
		lstSql.add(sql);
		// 将绑定参数放入集合
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" 
				+ sql + "#" 
				+ lstpara );

		// 将操作日志SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		String brand = "";
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			brand = rsConfig.getRows()[0].get("name").toString();
		}
		String _object = oldName + "==>" + name;
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), brand, name, "更新交互场景",
				_object, "SCENARIOS"));

		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}

	public static int copyOtherRules(String ruleid, String cityCode,
			String cityName, String scenariosid, String ruletype, String serviceType) {
		
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		
		
		String sql = "";
		int c = -1;
		sql = "insert into scenariosrules (ruleid,relationserviceid,"
//				 sql = "insert into scenariosrules (ruleid,scenariosid,"
				+ "condition1,condition2,condition3,condition4,condition5,condition6,condition7,condition8,condition9,condition10,condition11,condition12,condition13,condition14,condition15,condition16,condition17,condition18,condition19,condition20,"
				+ "ruletype,weight,city,cityname,abovequestionobject,abovestandardquestion,responsetype,"
				+ "questionobject,standardquestion,userquestion,currentnode,interactiveoptions,ruleresponse,ruleresponsetemplate"
				+ ") (select seq_scenariosrules_id.nextval";
		if (!"".equals(bussinessFlag)){
			sql = sql + "+'." + bussinessFlag +"'";
		}
		sql = sql
				+",relationserviceid,condition1,condition2,condition3,condition4,condition5,condition6,condition7,condition8,condition9,condition10,condition11,condition12,condition13,condition14,condition15,condition16,condition17,condition18,condition19,condition20,"
				+ "ruletype,weight,'"
				+ cityCode
				+ "','"
				+ cityName
				+ "',abovequestionobject,abovestandardquestion,responsetype,"
				+ "questionobject,standardquestion,userquestion,currentnode,interactiveoptions,ruleresponse,ruleresponsetemplate "
				+ "from scenariosrules where relationserviceid='"
//				 + "from scenariosrules where scenariosid='"
				+ scenariosid + "' and ruletype='" + ruletype
				+ "' and ruleid!='" + ruleid
				+ "' and (city is null or city = '全国')"
				+ " and (excludedcity not like '%" + cityCode
				+ "%' or excludedcity is null))";
		try {
			c = Database.executeNonQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 插入场景摘要关系
	 * 
	 * @param kbdataid
	 * @param scenoriosid
	 * @return
	 */
	public static int insertScenarios2kbdata(String kbdataid, String scenoriosid) {
		String sql = "select a.kbdataid,a.abstract,a.serviceid,b.service from kbdata a, service b where a.serviceid=b.serviceid and a.kbdataid=?";
		//文件日志
		GlobalValue.myLog.info( sql + "#" + kbdataid );
		Result rs = Database.executeQuery(sql, kbdataid);
		if (rs != null && rs.getRowCount() > 0) {
			String abs = rs.getRows()[0].get("abstract").toString();
			String serviceid = rs.getRows()[0].get("serviceid").toString();
			String service = rs.getRows()[0].get("service").toString();
			sql = "INSERT INTO scenarios2kbdata(scenarios2kbdataid, scenariosid,abstractid,abstract,serviceid,service,userquery) values(SEQ_SCENARIOS2KBDATA_ID.nextval,?,?,?,?,?,null)";

			int n = Database.executeNonQuery(sql, scenoriosid, kbdataid, abs,
					serviceid, service);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + scenoriosid + "," + kbdataid + "," + abs + "," + serviceid + "," + service );
			
			return n;
		} else {
			return -1;
		}
	}

	/**
	 * 插入场景摘要关系
	 * 
	 * @param kbdataid
	 * @param scenoriosid
	 * @return
	 */
	public static int insertScenarios2kbdataNew(String kbdataid,
			String scenoriosid, User user) {
		String serviceType = user.getIndustryOrganizationApplication();
		// 获取插入问题要素表的序列,并绑定问题要素id参数
		String scenarios2kbdataid = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		if (GetConfigValue.isOracle) {
			scenarios2kbdataid = ConstructSerialNum.GetOracleNextValNew(
					"SEQ_SCENARIOS2KBDATA_ID", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			scenarios2kbdataid = ConstructSerialNum
					.getSerialIDNew("scenarios2kbdata",
							"SEQ_SCENARIOS2KBDATA_ID", bussinessFlag);
		}
		String sql = "select a.kbdataid,a.abstract,a.serviceid,b.service from kbdata a, service b where a.serviceid=b.serviceid and a.kbdataid=?";
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + kbdataid );
		
		Result rs = Database.executeQuery(sql, kbdataid);
		if (rs != null && rs.getRowCount() > 0) {
			String abs = rs.getRows()[0].get("abstract").toString();
			String serviceid = rs.getRows()[0].get("serviceid").toString();
			String service = rs.getRows()[0].get("service").toString();
			sql = "INSERT INTO scenarios2kbdata(scenarios2kbdataid, relationserviceid,abstractid,abstract,serviceid,service,userquery) values("+scenarios2kbdataid+",?,?,?,?,?,null)";
//			sql = "INSERT INTO scenarios2kbdata(scenarios2kbdataid, scenariosid,abstractid,abstract,serviceid,service,userquery) values(SEQ_SCENARIOS2KBDATA_ID.nextval,?,?,?,?,?,null)";
			//文件日志
			GlobalValue.myLog.info( sql + "#" + scenoriosid + "," + kbdataid + "," + abs + "," + serviceid + "," + service );
			
			List<String> listSqls = new ArrayList<String>();
			List<List<?>> listParams = new ArrayList<List<?>>();
			listSqls.add(sql);
			listParams.add(Arrays.asList(scenoriosid, kbdataid, abs, serviceid, service));
			// 日志
			listSqls.add(GetConfigValue.LogSql());
			String scenoriosName = CommonLibServiceDAO.getNameByserviceid(scenoriosid);
			String _object = abs + " => " + scenoriosName;
			listParams.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), user.getBrand(), service, "绑定场景", _object, "SCENARIOS2KBDATA"));
					
			return Database.executeNonQueryTransaction(listSqls, listParams);
		} else {
			return -1;
		}
	}


	/**
	 * 省管理员发布
	 * @param city
	 * @return
	 */
	public static int issueOnProvince(String scenariosid, String service, String city, User user) {
		city = city.substring(0,2);
		int c = -1;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		String sql = "";
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 删除正式表的规则
//		sql = "delete from scenariosrules_online where relationserviceid=? and city like ? and ruletype in (0,4)";
		sql = "delete from scenariosrules_online where relationserviceid=? and city like ? ";
		// 添加场景id参数
		lstpara.add(scenariosid);
		// 添加城市id参数
		lstpara.add(city + "%");

		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 将线下表的规则放入正式表
		sql = "insert into scenariosrules_online(ruleid,relationserviceid,condition1,condition2,condition3,condition4,condition5,condition6,condition7,condition8,condition9,condition10,condition11,condition12,condition13,condition14,condition15,condition16,condition17,condition18,condition19,condition20,ruletype,weight,city,cityname,EXCLUDEDCITY,abovequestionobject,abovestandardquestion,responsetype,questionobject,standardquestion,userquestion,currentnode,interactiveoptions,ruleresponse,ruleresponsetemplate,isedit)  (select ruleid,relationserviceid,condition1,condition2,condition3,condition4,condition5,condition6,condition7,condition8,condition9,condition10,condition11,condition12,condition13,condition14,condition15,condition16,condition17,condition18,condition19,condition20,ruletype,weight,city,cityname,EXCLUDEDCITY,abovequestionobject,abovestandardquestion,responsetype,questionobject,standardquestion,userquestion,currentnode,interactiveoptions,ruleresponse,ruleresponsetemplate,'0' from scenariosrules where relationserviceid = ? and city like ? )";
		lstpara = new ArrayList<Object>();
		// 添加场景id参数
		lstpara.add(scenariosid);
		// 添加城市id参数
		lstpara.add(city + "%");

		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		sql = "update scenariosrules set isedit=0 where relationserviceid = ? and city like ? ";
//		sql = "update scenariosrules set isedit=0 where relationserviceid = ? and city like ? and ruletype in (0,4)";
		lstpara = new ArrayList<Object>();
		// 添加场景id参数
		lstpara.add(scenariosid);
		// 添加城市id参数
		lstpara.add(city + "%");

		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行sql集合
		c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		// 
		if( c > 0 ){
			String brand = "";
			String serviceType = user.getIndustryOrganizationApplication();
			Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
			
			if (rsConfig != null && rsConfig.getRowCount() > 0){
				brand = rsConfig.getRows()[0].get("name").toString();
			}
			
			String tempSql = "select * from scenariosrules_online where relationserviceid = ? and city like ? ";
			Result rs = Database.executeQuery(tempSql, scenariosid, city + "%");
			if (rs != null && rs.getRowCount() > 0){
				int count = rs.getRowCount();
				lstSql = new ArrayList<String>();
				lstLstpara = new ArrayList<List<?>>();
				// 将操作日志SQL语句放入集合中
				lstSql.add(GetConfigValue.LogSql());
				// 将定义的绑定参数集合放入集合中
				lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), brand, service,
						"增加线上交互规则", "发布<"+service+">"+count+"条规则", "SCENARIOSRULES_ONLINE"));
				// 执行sql集合
				int e = Database.executeNonQueryTransaction(lstSql, lstLstpara);
			} else {
				lstSql = new ArrayList<String>();
				lstLstpara = new ArrayList<List<?>>();
				// 将操作日志SQL语句放入集合中
				lstSql.add(GetConfigValue.LogSql());
				// 将定义的绑定参数集合放入集合中
				lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), brand, service,
						"删除线上交互规则", "下线<"+service+">"+c+"条规则！", "SCENARIOSRULES_ONLINE"));
				// 执行sql集合
				int e = Database.executeNonQueryTransaction(lstSql, lstLstpara);
			}
		}
		
		lstSql = new ArrayList<String>();
		lstLstpara = new ArrayList<List<?>>();
		
		// 处理线上全国规则的地市
		// ruletype = 0
		String excludedCityOnType0 = "";
		String tempSql = "select distinct city from scenariosrules_online where relationserviceid = '" + scenariosid + "' and city like '%0000' and ruletype = 0 ";
		
		//文件日志
		GlobalValue.myLog.info( tempSql );
		
		Result rs = Database.executeQuery(tempSql);
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0;i < rs.getRowCount();i++){
				excludedCityOnType0 = excludedCityOnType0 + rs.getRows()[i].get("city").toString() + ",";
			}
			excludedCityOnType0 = excludedCityOnType0.substring(0, excludedCityOnType0.lastIndexOf(","));
			sql = "update scenariosrules_online set excludedcity=? where relationserviceid=? and (city is null or city='全国') and ruletype=0";
			lstpara = new ArrayList<Object>();
			// 添加场景id参数
			lstpara.add(excludedCityOnType0);
			// 添加城市id参数
			lstpara.add(scenariosid);
			
			lstSql.add(sql);
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}
		
		// ruletype = 4
		String excludedCityOnType4 = "";
		tempSql = "select distinct city from scenariosrules_online where relationserviceid = '" + scenariosid + "' and city like '%0000' and ruletype = 4 ";
		rs = null;
		rs = Database.executeQuery(tempSql);
		
		//文件日志
		GlobalValue.myLog.info( tempSql  );
		
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0;i < rs.getRowCount();i++){
				excludedCityOnType4 = excludedCityOnType4 + rs.getRows()[i].get("city").toString() + ",";
			}
			excludedCityOnType4 = excludedCityOnType4.substring(0, excludedCityOnType4.lastIndexOf(","));
			sql = "update scenariosrules_online set excludedcity=? where relationserviceid=? and (city is null or city='全国') and ruletype=4";
			lstpara = new ArrayList<Object>();
			// 添加场景id参数
			lstpara.add(excludedCityOnType4);
			// 添加城市id参数
			lstpara.add(scenariosid);
			
			lstSql.add(sql);
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		
		// 执行sql集合
		int d = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		return c;
	}


	public static int issueOnAll(String scenariosid, String service, User user) {
		int c = -1;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		String sql = "";
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 删除正式表的规则
		sql = "delete from scenariosrules_online where relationserviceid=? and (city is null or city = '全国')";
		// 添加场景id参数
		lstpara.add(scenariosid);

		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 将线下表的规则放入正式表
		sql = "insert into scenariosrules_online(ruleid,relationserviceid,condition1,condition2,condition3,condition4,condition5,condition6,condition7,condition8,condition9,condition10,condition11,condition12,condition13,condition14,condition15,condition16,condition17,condition18,condition19,condition20,ruletype,weight,city,cityname,abovequestionobject,abovestandardquestion,responsetype,questionobject,standardquestion,userquestion,currentnode,interactiveoptions,ruleresponse,ruleresponsetemplate,isedit)  (select ruleid,relationserviceid,condition1,condition2,condition3,condition4,condition5,condition6,condition7,condition8,condition9,condition10,condition11,condition12,condition13,condition14,condition15,condition16,condition17,condition18,condition19,condition20,ruletype,weight,city,cityname,abovequestionobject,abovestandardquestion,responsetype,questionobject,standardquestion,userquestion,currentnode,interactiveoptions,ruleresponse,ruleresponsetemplate,'0' from scenariosrules where relationserviceid = ? and (city is null or city = '全国'))";
		lstpara = new ArrayList<Object>();
		// 添加场景id参数
		lstpara.add(scenariosid);

		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		sql = "update scenariosrules set isedit=0 where relationserviceid = ? and (city is null or city = '全国') ";
		lstpara = new ArrayList<Object>();
		// 添加场景id参数
		lstpara.add(scenariosid);

		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行sql集合
		c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		// 
		if( c > 0 ){
			String brand = "";
			String serviceType = user.getIndustryOrganizationApplication();
			Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
			
			if (rsConfig != null && rsConfig.getRowCount() > 0){
				brand = rsConfig.getRows()[0].get("name").toString();
			}
			
			String tempSql = "select * from scenariosrules_online where relationserviceid = ? and (city is null or city = '全国')";
			Result rs = Database.executeQuery(tempSql, scenariosid);
			if (rs != null && rs.getRowCount() > 0){
				int count = rs.getRowCount();
				lstSql = new ArrayList<String>();
				lstLstpara = new ArrayList<List<?>>();
				// 将操作日志SQL语句放入集合中
				lstSql.add(GetConfigValue.LogSql());
				// 将定义的绑定参数集合放入集合中
				lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), brand, service,
						"增加线上交互规则", "发布<"+service+">"+count+"条规则", "SCENARIOSRULES_ONLINE"));
				// 执行sql集合
				int e = Database.executeNonQueryTransaction(lstSql, lstLstpara);
			} else {
				lstSql = new ArrayList<String>();
				lstLstpara = new ArrayList<List<?>>();
				// 将操作日志SQL语句放入集合中
				lstSql.add(GetConfigValue.LogSql());
				// 将定义的绑定参数集合放入集合中
				lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), brand, service,
						"删除线上交互规则", "下线<"+service+">"+c+"条规则", "SCENARIOSRULES_ONLINE"));
				// 执行sql集合
				int e = Database.executeNonQueryTransaction(lstSql, lstLstpara);
			}
		}
		
		lstSql = new ArrayList<String>();
		lstLstpara = new ArrayList<List<?>>();
		
		// 处理线上全国规则的地市
		// ruletype = 0
		String excludedCityOnType0 = "";
		String tempSql = "select distinct city from scenariosrules_online where relationserviceid = '" + scenariosid + "' and city like '%0000' and ruletype = 0 ";
		Result rs = Database.executeQuery(tempSql);
		
		//文件日志
		GlobalValue.myLog.info( tempSql );
		
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0;i < rs.getRowCount();i++){
				excludedCityOnType0 = excludedCityOnType0 + rs.getRows()[i].get("city").toString() + ",";
			}
			excludedCityOnType0 = excludedCityOnType0.substring(0, excludedCityOnType0.lastIndexOf(","));
			sql = "update scenariosrules_online set excludedcity=? where relationserviceid=? and (city is null or city='全国') and ruletype=0";
			lstpara = new ArrayList<Object>();
			// 添加场景id参数
			lstpara.add(excludedCityOnType0);
			// 添加城市id参数
			lstpara.add(scenariosid);
			
			lstSql.add(sql);
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		
		// ruletype = 4
		String excludedCityOnType4 = "";
		tempSql = "select distinct city from scenariosrules_online where relationserviceid = '" + scenariosid + "' and city like '%0000' and ruletype = 4 ";
		rs = null;
		rs = Database.executeQuery(tempSql);
		
		//文件日志
		GlobalValue.myLog.info( tempSql );
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0;i < rs.getRowCount();i++){
				excludedCityOnType4 = excludedCityOnType4 + rs.getRows()[i].get("city").toString() + ",";
			}
			excludedCityOnType4 = excludedCityOnType4.substring(0, excludedCityOnType4.lastIndexOf(","));
			sql = "update scenariosrules_online set excludedcity=? where relationserviceid=? and (city is null or city='全国') and ruletype=4";
			lstpara = new ArrayList<Object>();
			// 添加场景id参数
			lstpara.add(excludedCityOnType4);
			// 添加城市id参数
			lstpara.add(scenariosid);
			
			lstSql.add(sql);
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}
		
		// 执行sql集合
		int d = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		return c;
	}

	// 获取场景表配置
	public static Result getIssueData() {
		String sql = " select s.name key1 ,t.name value1 from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name ='场景表配置' order by s.metafieldid";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	// 省里线上规则下线
	public static int deleteOnlineOnProvince(String scenariosid, String service, String city, User user) {
		city = city.substring(0,2);
		int c = -1;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		String sql = "";
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 删除正式表的规则
		sql = "delete from scenariosrules_online where relationserviceid=? and city like ? ";
//		sql = "delete from scenariosrules_online where relationserviceid=? and city like ? and ruletype in (0,4)";
		// 添加场景id参数
		lstpara.add(scenariosid);
		// 添加城市id参数
		lstpara.add(city + "%");

		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行sql集合
		c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		// 
		if( c > 0 ){
			String brand = "";
			String serviceType = user.getIndustryOrganizationApplication();
			Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
			
			if (rsConfig != null && rsConfig.getRowCount() > 0){
				brand = rsConfig.getRows()[0].get("name").toString();
			}
			
				lstSql = new ArrayList<String>();
				lstLstpara = new ArrayList<List<?>>();
				// 将操作日志SQL语句放入集合中
				lstSql.add(GetConfigValue.LogSql());
				// 将定义的绑定参数集合放入集合中
				lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), brand, service,
						"删除线上交互规则", "下线<"+service+">"+c+"条规则", "SCENARIOSRULES_ONLINE"));
				// 执行sql集合
				int e = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		}
		
		lstSql = new ArrayList<String>();
		lstLstpara = new ArrayList<List<?>>();
		
		// 处理线上全国规则的地市
		// ruletype = 0
		String excludedCityOnType0 = "";
		String tempSql = "select distinct city from scenariosrules_online where relationserviceid = '" + scenariosid + "' and city like '%0000' and ruletype = 0 ";
		
		//文件日志
		GlobalValue.myLog.info( tempSql );
		
		Result rs = Database.executeQuery(tempSql);
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0;i < rs.getRowCount();i++){
				excludedCityOnType0 = excludedCityOnType0 + rs.getRows()[i].get("city").toString() + ",";
			}
			excludedCityOnType0 = excludedCityOnType0.substring(0, excludedCityOnType0.lastIndexOf(","));
			sql = "update scenariosrules_online set excludedcity=? where relationserviceid=? and (city is null or city='全国') and ruletype=0";
			lstpara = new ArrayList<Object>();
			// 添加场景id参数
			lstpara.add(excludedCityOnType0);
			// 添加城市id参数
			lstpara.add(scenariosid);
			
			lstSql.add(sql);
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}
		
		// ruletype = 4
		String excludedCityOnType4 = "";
		tempSql = "select distinct city from scenariosrules_online where relationserviceid = '" + scenariosid + "' and city like '%0000' and ruletype = 4 ";
		rs = null;
		rs = Database.executeQuery(tempSql);
		
		//文件日志
		GlobalValue.myLog.info( tempSql  );
		
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0;i < rs.getRowCount();i++){
				excludedCityOnType4 = excludedCityOnType4 + rs.getRows()[i].get("city").toString() + ",";
			}
			excludedCityOnType4 = excludedCityOnType4.substring(0, excludedCityOnType4.lastIndexOf(","));
			sql = "update scenariosrules_online set excludedcity=? where relationserviceid=? and (city is null or city='全国') and ruletype=4";
			lstpara = new ArrayList<Object>();
			// 添加场景id参数
			lstpara.add(excludedCityOnType4);
			// 添加城市id参数
			lstpara.add(scenariosid);
			
			lstSql.add(sql);
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		
		// 执行sql集合
		int d = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		return c;
	}

	/**
	 * 全行业管理员规则下线
	 * @param scenariosid
	 * @return
	 */
	public static int deleteOnlineOnAll(String scenariosid, String service, User user) {
		int c = -1;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		String sql = "";
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 删除正式表的规则
		sql = "delete from scenariosrules_online where relationserviceid=? and (city is null or city = '全国') ";
//		sql = "delete from scenariosrules_online where relationserviceid=? and (city is null or city = '全国') and ruletype in (0,4)";
		// 添加场景id参数
		lstpara.add(scenariosid);

		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行sql集合
		c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		// 
		if( c > 0 ){
			String brand = "";
			String serviceType = user.getIndustryOrganizationApplication();
			Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("场景业务根对应关系配置",serviceType);
			
			if (rsConfig != null && rsConfig.getRowCount() > 0){
				brand = rsConfig.getRows()[0].get("name").toString();
			}
			
				lstSql = new ArrayList<String>();
				lstLstpara = new ArrayList<List<?>>();
				// 将操作日志SQL语句放入集合中
				lstSql.add(GetConfigValue.LogSql());
				// 将定义的绑定参数集合放入集合中
				lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user.getUserID(), user.getUserName(), brand, service,
						"删除线上交互规则", "下线<"+service+">"+c+"条规则", "SCENARIOSRULES_ONLINE"));
				// 执行sql集合
				int e = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		}
		
		lstSql = new ArrayList<String>();
		lstLstpara = new ArrayList<List<?>>();
		
		// 处理线上全国规则的地市
		// ruletype = 0
		String excludedCityOnType0 = "";
		String tempSql = "select distinct city from scenariosrules_online where relationserviceid = '" + scenariosid + "' and city like '%0000' and ruletype = 0 ";
		Result rs = Database.executeQuery(tempSql);
		
		//文件日志
		GlobalValue.myLog.info( tempSql );
		
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0;i < rs.getRowCount();i++){
				excludedCityOnType0 = excludedCityOnType0 + rs.getRows()[i].get("city").toString() + ",";
			}
			excludedCityOnType0 = excludedCityOnType0.substring(0, excludedCityOnType0.lastIndexOf(","));
			sql = "update scenariosrules_online set excludedcity=? where relationserviceid=? and (city is null or city='全国') and ruletype=0";
			lstpara = new ArrayList<Object>();
			// 添加场景id参数
			lstpara.add(excludedCityOnType0);
			// 添加城市id参数
			lstpara.add(scenariosid);
			
			lstSql.add(sql);
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		
		// ruletype = 4
		String excludedCityOnType4 = "";
		tempSql = "select distinct city from scenariosrules_online where relationserviceid = '" + scenariosid + "' and city like '%0000' and ruletype = 4 ";
		rs = null;
		rs = Database.executeQuery(tempSql);
		
		//文件日志
		GlobalValue.myLog.info( tempSql );
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0;i < rs.getRowCount();i++){
				excludedCityOnType4 = excludedCityOnType4 + rs.getRows()[i].get("city").toString() + ",";
			}
			excludedCityOnType4 = excludedCityOnType4.substring(0, excludedCityOnType4.lastIndexOf(","));
			sql = "update scenariosrules_online set excludedcity=? where relationserviceid=? and (city is null or city='全国') and ruletype=4";
			lstpara = new ArrayList<Object>();
			// 添加场景id参数
			lstpara.add(excludedCityOnType4);
			// 添加城市id参数
			lstpara.add(scenariosid);
			
			lstSql.add(sql);
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}
		
		// 执行sql集合
		int d = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		return c;
	}

	/**
	 * 获取交互要素下拉列表
	 * @return
	 */
	public static Result getInteractiveElement() {
		String sql = "select scenariosinteractiveelement.wordclassid,scenariosinteractiveelement.elementname,kbdata.abstract,kbdata.kbdataid from scenariosinteractiveelement,kbdata where scenariosinteractiveelement.abstractid=kbdata.kbdataid";
//		String sql = "select scenariosinteractiveelement.wordclassid,scenariosinteractiveelement.elementname,kbdata.abstract,kbdata.kbdataid from scenariosinteractiveelement,kbdata where scenariosinteractiveelement.abstractid=kbdata.kbdataid and scenariosinteractiveelement.wordclassid is not null";
		Result rs = Database.executeQuery(sql);
		return rs;
	}


	public static int saveinteractiveelement(User user, String scenariosid, String scenariosName,
			String wordclassid, String kbdataid, String elementName, String weight,
			String serviceType, String city, String cityname) {

		int count = -1;
		// 1.先查询词类是否存在
		// 定义查询词类是否存在的SQL语句
		String sql = null;
		List<Object> lstpara = null;
		Result rs = null;

		// 2.查询问题要素是否重复
		// 查询要添加的场景要素是否重复
		sql = "select * from scenarioselement where name=? and relationserviceid=? ";
//		 sql = "select * from scenarioselement where name=? and scenariosid=? ";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定问题要素名称参数
		lstpara.add(elementName);
		// 绑定场景id参数
		lstpara.add(scenariosid);
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为空且含有数据
		if (rs != null && rs.getRowCount() > 0) {// 场景名称已存在
			return -3;
		} else {
			// 定义多条SQL语句集合
			List<String> lstSql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstLstpara = new ArrayList<List<?>>();
			// 问题要素不存在
			// 插入问题要素的SQL语句
			sql = "insert into scenarioselement (SCENARIOSELEMENTID,NAME,relationserviceid,WEIGHT,WORDCLASSID ,CITY,CITYNAME,ITEMMODE,container) values (?,?,?,?,?,?,?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 获取插入问题要素表的序列,并绑定问题要素id参数
			String scenarioselementid = "";
			String bussinessFlag = CommonLibMetafieldmappingDAO
					.getBussinessFlag(serviceType);
			if (GetConfigValue.isOracle) {
				scenarioselementid = ConstructSerialNum.GetOracleNextValNew(
						"seq_scenarioselement_id", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				scenarioselementid = ConstructSerialNum
						.getSerialIDNew("scenarioselement",
								"scenarioselementid", bussinessFlag);
			}
			// 绑定参数
			lstpara.add(scenarioselementid);
			lstpara.add(elementName);
			lstpara.add(scenariosid);
			lstpara.add(weight);
			
			if ("".equals(wordclassid)){
				lstpara.add(null);
			}else{
				lstpara.add(wordclassid);
			}

			lstpara.add(city);
			lstpara.add(cityname);
			lstpara.add("勾选");
			lstpara.add("词模匹配");
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合
			lstLstpara.add(lstpara);
			
			//3.添加默认规则
			String conditions = "";
			for (int i = 1 ; i <= 20 ; i++){
				if(Integer.valueOf(weight)==i){
					conditions = conditions + "已选" + "@";
				}else {
					conditions += "@";
				}
			}
			conditions = conditions.substring(0, conditions.length()-1);
			// 获得当前场景下符合规则的规则最大优先级值
			double maxWeight = CommonLibInteractiveSceneDAO
					.getMaxWeight(scenariosid);
			weight = maxWeight + 1 + "";
			
			sql = "select service.serviceid,service.service,kbdata.kbdataid,kbdata.abstract from service,kbdata where service.serviceid=kbdata.serviceid and kbdata.kbdataid=?";
			rs = null;
			rs = Database.executeQuery(sql, kbdataid);
			String kbdata = "";
			String service = "";
			if (rs != null && rs.getRowCount() > 0){
				kbdata = rs.getRows()[0].get("abstract").toString();
				service = rs.getRows()[0].get("service").toString();
			}else {
				return -1;
			}
			
			
			int c = insertSceneRules(user,scenariosid,
					scenariosName,conditions, weight, "3", "信息补全(&quot;"+elementName+"&quot;,&quot;上文&quot;);", serviceType,
					city, cityname, "", "","", "信息补全(&quot;"+elementName+"&quot;,&quot;上文&quot;);",
					"0", service, kbdata,"","");
			
			// 执行SQL语句，绑定事务处理，并返回相应的事务处理结果
			count = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		}

		return count;
	}

	/**
	 * 从id获取规则
	 * @param string
	 * @return
	 */
	public static Result getrulebyID(String ruleid) {
		String sql = "select * from scenariosrules where ruleid=?";
		Result rs = Database.executeQuery(sql, ruleid);
		return rs;
	}

	/**
	 * 复制单条规则
	 * @param string
	 * @param weight
	 * @return
	 */
	public static int copyRule(String ruleid, String weight, String serviceType) {
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		int c = -1;
		sql = "insert into scenariosrules (ruleid,relationserviceid,"
				+ "condition1,condition2,condition3,condition4,condition5,condition6,condition7,condition8,condition9,condition10,condition11,condition12,condition13,condition14,condition15,condition16,condition17,condition18,condition19,condition20,"
				+ "ruletype,weight,city,cityname,abovequestionobject,abovestandardquestion,responsetype,"
				+ "questionobject,standardquestion,userquestion,currentnode,interactiveoptions,ruleresponse,ruleresponsetemplate,isedit"
				+ ") (select ?,relationserviceid,condition1,condition2,condition3,condition4,condition5,condition6,condition7,condition8,condition9,condition10,condition11,condition12,condition13,condition14,condition15,condition16,condition17,condition18,condition19,condition20,"
				+ "ruletype,?,city,cityname,abovequestionobject,abovestandardquestion,responsetype,"
				+ "questionobject,standardquestion,userquestion,currentnode,interactiveoptions,ruleresponse,ruleresponsetemplate,-1 "
				+ "from scenariosrules where ruleid=?)";
		
		// 获取插入问题要素表的序列,并绑定问题要素id参数
		String scenariosrulesid = "";
		if (GetConfigValue.isOracle) {
			scenariosrulesid = ConstructSerialNum.GetOracleNextValNew("seq_scenariosrules_id", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			scenariosrulesid = ConstructSerialNum.getSerialIDNew("scenariosrules","ruleid", bussinessFlag);
		}
		
		lstpara.add(scenariosrulesid);
		lstpara.add(weight);
		lstpara.add(ruleid);
		
		lsts.add(sql);
		lstlstpara.add(lstpara);
		
		c = Database.executeNonQueryTransaction(lsts, lstlstpara);
		
		return c;
	}

	
}
