package com.knowology.bll;

//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.axis.utils.StringUtils;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLibServiceAttrDao {

	/**
	 * 定义全局 city字典
	 */
	public static Map<String, String> cityCodeToCityName = new HashMap<String, String>();

	/**
	 * 定义全局 cityNameToCityCode 字典
	 */
	public static Map<String, String> cityNameToCityCode = new HashMap<String, String>();

	/**
	 *创建字典
	 */

	static {
		Result r = CommonLibMetafieldmappingDAO.getConfigMinValue("地市编码配置");

		if (r != null && r.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < r.getRowCount(); i++) {
				String key = r.getRows()[i].get("k") == null ? ""
						: r.getRows()[i].get("k").toString();
				String value = r.getRows()[i].get("name") == null ? "" : r
						.getRows()[i].get("name").toString();
				cityCodeToCityName.put(value, key);
				cityNameToCityCode.put(key, value);
			}
		}

	}

	public static Result Select2column(String serviceid, String name,
			String sort, String order) {
		// 定义SQL语句
		String sql = "";
		if (null != name) {
			if (null != sort) {
				sql = "select serviceattrname2colnumid,name,columnnum from serviceattrname2colnum where serviceid='"
						+ serviceid
						+ "' and name like '%"
						+ name
						+ "%' order by " + sort + " " + order;
			} else {
				sql = "select serviceattrname2colnumid,name,columnnum from serviceattrname2colnum where serviceid='"
						+ serviceid
						+ "' and name like '%"
						+ name
						+ "%' order by columnnum ";
			}
		} else if (null != sort) {
			sql = "select serviceattrname2colnumid,name,columnnum from serviceattrname2colnum where serviceid='"
					+ serviceid + "' order by " + sort + " " + order;
		} else {
			sql = "select serviceattrname2colnumid,name,columnnum from serviceattrname2colnum where serviceid='"
					+ serviceid + "' order by columnnum ";
		}

		// 文件日志
		GlobalValue.myLog.info(sql);
		// 执行sql语句，获取相应的数据源
		Result rs = Database.executeQuery(sql);
		// 返回rs
		return rs;
	}

	/**
	 *@description 模糊查询信息表对应列
	 *@param content
	 *            查询内容
	 *@param serviceid
	 *            业务ID
	 *@return
	 *@returnType Result
	 */
	public static Result getLikeColumn(String content, String serviceid) {
		String sql = "select  distinct name  from serviceattrname2colnum where  upper(name) like '%"
				+ content
				+ "%'  and  serviceid = "
				+ serviceid
				+ " order by name";
		Result rs = Database.executeQuery(sql);

		// 文件日志
		GlobalValue.myLog.info(sql);

		return rs;
	}

	/**
	 * 查询属性名称信息
	 * 
	 * @param serviceid参数业务id
	 * @param name参数属性名称
	 * @return rs
	 */
	public static Result SelectAttrName(String serviceid, String name) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 查询满足条件的属性名称的总条数的SQL语句
		sql
				.append("select c.*,(select wordclass from wordclass where wordclassid=c.wordclassid) wordclass from serviceattrname2colnum c where c.serviceid=? ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 判断属性名称是否为null，空
		if (name != null && !"".equals(name)) {
			// 加上那么的like查询
			sql.append(" and c.name like ? ");
			// 绑定属性名称参数
			lstpara.add("%" + name + "%");
		}
		// 将SQL语句补充完整
		sql.append(" order by c.columnnum asc");
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 分页查询属性名称信息
	 * 
	 * @param serviceid参数业务id
	 * @param name参数属性名称
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return rs
	 */
	public static Result SelectAttrName(String serviceid, String name,
			int page, int rows) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义分页查询满足条件的属性名称SQL语句
		if (GetConfigValue.isOracle) {
			sql
					.append("select * from (select t.*,rownum rn from (select c.*,(select wordclass from wordclass where wordclassid=c.wordclassid) wordclass from serviceattrname2colnum c where c.serviceid=? ");
		} else {
			sql
					.append("select * from (select c.*,(select wordclass from wordclass where wordclassid=c.wordclassid) wordclass from serviceattrname2colnum c where c.serviceid=? ");
		}
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 判断属性名称是否为null，空
		if (name != null && !"".equals(name)) {
			// 加上那么的like查询
			sql.append(" and c.name like ? ");
			// 绑定属性名称参数
			lstpara.add("%" + name + "%");
		}
		// 将SQL语句补充完整
		if (GetConfigValue.isOracle) {
			sql.append(" order by c.columnnum asc)t) where rn>? and rn<=?");
		} else {
			sql.append(" order by c.columnnum asc) t limit ?,?");
		}
		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(page * rows);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 获取所有的列值
	 * 
	 * @param serviceid参数业务id
	 * @return rs
	 */
	public static Result GetColumn(String serviceid) {
		// 定义查询属性名称的列值的SQL语句
		String sql = "select columnnum from serviceattrname2colnum where serviceid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 先查询词类是否存在
	 * 
	 * @param wordclass参数词类名称
	 * @return rs
	 */
	public static Result InsertAttrName(String wordclass) {
		// 定义查询词类是否存在的SQL语句
		String sql = "select * from wordclass where wordclass=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定词类名称参数
		lstpara.add(wordclass);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 查询属性名称是否重复
	 * 
	 * @param serviceid参数业务id
	 * @param name参数属性名称
	 * @return rs
	 */
	public static Result InsertAttrName(String serviceid, String name) {
		// 定义查询属性名称是否重复的SQL语句
		String sql = "select * from serviceattrname2colnum where name=? and serviceid=?";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return rs;
	}

	/**
	 * 新增属性名称
	 * 
	 * @param serviceid参数业务id
	 * @param service参数业务
	 * @param name参数属性名称
	 * @param column参数列值
	 * @param wordclass参数词类名称
	 * @return 新增是否成功
	 */
	public static int InsertAttrName(User user, String serviceid, String service,
			String name, String semanticskeyword, String container,
			String column, String wordclass, String wordclassid,
			String serviceType) {
		 // 定义多条SQL语句集合
		 List<String> lstSql = new ArrayList<String>();
		 // 定义多条SQL语句对应的绑定参数集合
		 List<List<?>> lstLstpara = new ArrayList<List<?>>();

		// 定义新增属性名称的SQL语句
		String sql = "insert into serviceattrname2colnum (serviceattrname2colnumid,name,semanticskeyword,container,columnnum,wordclassid,serviceid,service) values (?,?,?,?,?,?,?,?)";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		String serviceattrname2colnumid = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		if (GetConfigValue.isOracle) {
			serviceattrname2colnumid = ConstructSerialNum.GetOracleNextValNew(
					"serviceattrname2colnum_seq", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			serviceattrname2colnumid = ConstructSerialNum.getSerialIDNew(
					"serviceattrname2colnum", "serviceattrname2colnumid",
					bussinessFlag);
		}
		// 获取属性名称表的序列值，并绑定参数
		lstpara.add(serviceattrname2colnumid);
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定语义关键词信息
		lstpara.add(semanticskeyword);
		// 绑定列归属参数
		lstpara.add(container);
		// 绑定列值参数
		lstpara.add(column);
		// 绑定词类id参数
		lstpara.add(wordclassid);
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 绑定业务参数
		lstpara.add(service);
		
		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		// 将操作日志SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", service,
				"增加信息列", name, "SERVICEATTRNAME2COLNUM"));

		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return c;
	}

	/**
	 * 查询属性名称组成field和name,以及对应的属性值
	 * 
	 * @param serviceid参数业务id
	 * @return rs
	 */
	public static Result SelectAttrField(String serviceid) {
		// 定义查询属性名称和属性值的SQL语句
		String sql = "select n.name,n.columnnum,w.word from serviceattrname2colnum n left join word w on w.wordclassid=n.wordclassid where w.stdwordid is null and n.serviceid=? order by n.columnnum asc,w.word asc";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return rs;
	}

	/**
	 * 查询属性名称组成field和name,以及对应的属性值
	 * 
	 * @param serviceid参数业务id
	 * @return rs
	 */
	public static Result SelectAttrField2(String serviceid,
			String serviceattrname2colnumids) {
		// 定义查询属性名称
		String sql = "select * from serviceattrname2colnum where serviceid=? order by columnnum asc";
		if (serviceattrname2colnumids != null
				&& !"".equals(serviceattrname2colnumids)) {
			sql = "select * from (" + sql
					+ ") se where serviceattrname2colnumid in ("
					+ serviceattrname2colnumids + ")";
		}
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return rs;
	}

	/**
	 * 删除属性名称，并删除相关的信息
	 * 
	 * @param serviceid参数业务id
	 * @param attrnameid参数属性名称id
	 * @param column参数对应列值
	 * @return rs
	 */
	public static int DeleteAttrName(User user, String serviceid, String attrnameid,
			String column) {
		String checkSql = "select * from serviceattrname2colnum where serviceattrname2colnumid=?";
		Result rs = Database.executeQuery(checkSql, attrnameid);
		String service = "";
		String name = "";
		if (rs != null && rs.getRowCount() > 0){
			service = rs.getRows()[0].get("service").toString();
			name = rs.getRows()[0].get("name").toString();
		}
		
		
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句定义的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义删除属性名称的SQL语句
		String sql = "delete from serviceattrname2colnum where serviceattrname2colnumid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称id参数
		lstpara.add(attrnameid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		// 定义删除属性值的SQL语句
		sql = "delete from serviceattrstdvalue where serviceattrname2colnumid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定属性名称id参数
		lstpara.add(attrnameid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		// 将操作日志SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", service,
				"删除信息列", name, "SERVICEATTRNAME2COLNUM"));

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		// 定义删除服务或产品的SQL语句
		sql = "update serviceorproductinfo set attr" + column
				+ "='' where serviceid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}

	/**
	 * 修改属性名称
	 * 
	 * @param serviceid参数业务id
	 * @param name参数属性名称
	 * @return 修改返回的json串
	 */
	public static Result ModifyAttrName(String serviceid, String name) {
		// 定义查询属性名称是否重复的SQL语句
		String sql = "select * from serviceattrname2colnum where name=? and serviceid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return rs;
	}

	/**
	 * 修改属性名称
	 * 
	 * @param serviceid参数业务id
	 * @param attrnameid参数属性名称id
	 * @param name参数属性名称
	 * @return 修改返回的json串
	 */
	public static int ModifyAttrName(User user, String serviceid, String attrnameid,
			String name, String semanticskeyword, String container,
			String checkway) {
		
		String checkSql = "select * from serviceattrname2colnum where serviceattrname2colnumid=?";
		Result rs = Database.executeQuery(checkSql, attrnameid);
		String service = "";
		String nameOld = "";
		String semanticskeywordOld = "";
		String containerOld = "";
		String checkwayOld = "";
		if (rs != null && rs.getRowCount() > 0){
			service = rs.getRows()[0].get("service").toString();
			nameOld = rs.getRows()[0].get("name").toString();
			// 语义关键词
			semanticskeywordOld = rs.getRows()[0].get("SEMANTICSKEYWORD").toString();
			// 列归属
			containerOld = rs.getRows()[0].get("CONTAINER").toString();
			// 数据约束
			checkwayOld = rs.getRows()[0].get("checkway").toString();
		}
		
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句定义的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		
		// 定义更新属性名称的SQL语句
		String sql = "update serviceattrname2colnum set name=?,semanticskeyword=?,container=?,checkway=? where serviceattrname2colnumid=? and serviceid=?";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定语义关键词参数
		lstpara.add(semanticskeyword);
		// 绑定列归属参数
		lstpara.add(container);
		// 绑定列数据约束参数
		lstpara.add(checkway);
		// 绑定属性名称id参数
		lstpara.add(attrnameid);
		// 绑定业务id参数
		lstpara.add(serviceid);
		
		lstSql.add(sql);
		lstLstpara.add(lstpara);
		
		// 将操作日志SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", service,
				"更新信息列", nameOld + "->" + name + "," 
				+ semanticskeywordOld + "->" + semanticskeyword + ","
				+ containerOld + "->" + container + ","
				+ checkwayOld + "->" + checkway , "SERVICEATTRNAME2COLNUM"));
		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return c;
	}

	/**
	 * 分页查询属性值
	 * 
	 * @param wordclassid参数词类id
	 * @param name参数词条名称
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return json串
	 */
	public static Result SelectAttrValue(String wordclassid, String name) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义查询属性值的总条数的SQL语句
		sql
				.append("select * from word where stdwordid is null and wordclassid=?");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称id参数
		lstpara.add(wordclassid);
		// 判断词条名称是否为null，空
		if (name != null && !"".equals(name)) {
			// 加上词条like查询
			sql.append(" and word like ? ");
			// 绑定词条名称参数
			lstpara.add("%" + name + "%");
		}
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return rs;
	}

	/**
	 * 分页查询属性值
	 * 
	 * @param wordclassid参数词类id
	 * @param name参数词条名称
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return json串
	 */
	public static Result SelectAttrValue(String wordclassid, String name,
			int page, int rows) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 分页查询满足条件的属性值的SQL语句
		if (GetConfigValue.isOracle) {
			sql
					.append("select * from (select t.*,rownum rn from (select * from word where stdwordid is null and wordclassid=? ");
		} else {
			sql
					.append("select * from (select * from word where stdwordid is null and wordclassid=? ");
		}
		// 绑定属性名称id参数
		lstpara.add(wordclassid);
		// 判断问题要素值(词条)名称是否为null，空
		if (name != null && !"".equals(name)) {
			// 加上问题要素值的like查询
			sql.append(" and word like ? ");
			// 绑定问题要素(词条)参数
			lstpara.add("%" + name + "%");
		}
		// 将SQL语句补充完整
		if (GetConfigValue.isOracle) {
			sql.append(" order by wordid desc)t) where rn>? and rn<=?");
		} else {
			sql.append(" order by wordid desc) t limit ?,?");
		}
		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(page * rows);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return rs;
	}

	/**
	 * 新增属性属性值
	 * 
	 * @param name参数属性值
	 * @param wordclassid参数词类名称id
	 * @return 新增返回的json串
	 */
	public static Result InsertAttrValue(String name, String wordclassid) {
		// 定义查询属性名称是否重复的SQL语句
		String sql = "select * from word where stdwordid is null and word=? and wordclassid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定属性名称id参数
		lstpara.add(wordclassid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 新增属性属性值
	 * 
	 * @param name参数属性值
	 * @param wordclassid参数词类名称id
	 * @param wordclass参数词类名称
	 * @return 新增返回的json串
	 */
	public static int InsertAttrValue(String name, String wordclassid,
			String wordclass, User user) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义新增词条的SQL语句
		String sql = "insert into word (wordid,wordclassid,word,type) values(?,?,?,?)";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		String wordid = "";
		String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		if (GetConfigValue.isOracle) {
			wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id",
					bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			wordid = ConstructSerialNum.getSerialIDNew("word", "wordid",
					bussinessFlag);
		}
		// 获取词条表的序列值，并绑定参数
		lstpara.add(wordid);
		// 绑定词类id参数
		lstpara.add(wordclassid);
		// 绑定词条参数
		lstpara.add(name);
		// 绑定词条类型参数
		lstpara.add("标准名称");
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		
		if (GetConfigValue.enableDBFun) {
			// 存储过程
			lstSql.add("call P_WORDADD(?)");
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			lstpara.add(wordid);
			lstLstpara.add(lstpara);
		}
		
		// 生成操作日志记录
		String logSql = "";
		if (GetConfigValue.isMySQL) {
			logSql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,sysdate())";
		} else if (GetConfigValue.isOracle) {
			logSql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)";

		}
		// 将SQL语句放入集合中
		lstSql.add(logSql);

		List<String> logLstpara = new ArrayList<String>();
		logLstpara.add(user.getUserIP());
		logLstpara.add(" ");
		logLstpara.add(" ");
		logLstpara.add("增加词条");
		logLstpara.add("上海");
		logLstpara.add(user.getUserID());
		logLstpara.add(user.getUserName());
		logLstpara.add(wordclass + "==>" + name);
		logLstpara.add("WORD");
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(logLstpara);
		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}

	/**
	 * 修改属性值(词条)
	 * 
	 * @param attrvalueid参数属性值id
	 * @return 修改返回的json串
	 */
	public static Result ModifyAttrValue(String attrvalueid) {
		// 定义查询别名的SQL语句
		String sql = "select wordid from word where stdwordid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性值(词条)id
		lstpara.add(attrvalueid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return rs;
	}

	/**
	 * 修改属性值(词条)
	 * 
	 * @param name参数属性值
	 * @param wordclassid参数词类id
	 * @return 修改返回的json串
	 */
	public static Result ModifyAttrValue(String name, String wordclassid) {
		// 定义查询属性名称是否重复的SQL语句
		String sql = "select * from word where stdwordid is null and word=? and wordclassid=?";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定属性名称id参数
		lstpara.add(wordclassid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return rs;
	}

	/**
	 * 修改属性值(词条)
	 * 
	 * @param attrvalueid参数属性值id
	 * @param name参数属性值
	 * @param column参数列值
	 * @param oldname参数原有的词条
	 * @param serviceid参数业务id
	 * @return 修改返回的json串
	 */
	public static int ModifyAttrValue(String attrvalueid, String name,
			String column, String oldname, String serviceid, User user) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义更新词条的SQL语句
		String sql = "update word set word=? where wordid=? ";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 绑定属性值(词条)参数
		lstpara.add(name);
		// 绑定属性值(词条)id参数
		lstpara.add(attrvalueid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		// 定义更新服务或产品的SQL语句
		sql = "update serviceorproductinfo set attr" + column + "=? where attr"
				+ column + "=? and serviceid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定属性值参数
		lstpara.add(name);
		// 绑定属性值参数
		lstpara.add(oldname);
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		// 生成操作日志记录
		String logSql = "";
		if (GetConfigValue.isMySQL) {
			logSql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,sysdate())";
		} else if (GetConfigValue.isOracle) {
			logSql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)";

		}
		// 将SQL语句放入集合中
		lstSql.add(logSql);

		List<String> logLstpara = new ArrayList<String>();
		logLstpara.add(user.getUserIP());
		logLstpara.add(" ");
		logLstpara.add(" ");
		logLstpara.add("更新词条");
		logLstpara.add("上海");
		logLstpara.add(user.getUserID());
		logLstpara.add(user.getUserName());
		logLstpara.add(oldname + "==>" + name);
		logLstpara.add("WORD");
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(logLstpara);

		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}

	/**
	 * 删除属性值，并删除相关的信息
	 * 
	 * @param serviceid参数业务id
	 * @param attrvalueid参数属性值id
	 * @param name参数词条
	 * @param column参数属性名称对应列值
	 * @param wordclass参数词类名称
	 * @return 删除返回的json串
	 */
	public static int DeleteAttrValue(String serviceid, String attrvalueid,
			String name, String column, String wordclass, User user) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		
		if (GetConfigValue.enableDBFun) {
			// 存储过程
			lstSql.add("call P_WORDDELETE(?)");
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			lstpara.add(attrvalueid);
			lstLstpara.add(lstpara);
		}
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		
		// 定义删除词条的SQL语句
		String sql = "delete from word where wordid=?";
		// 绑定属性值id参数
		lstpara.add(attrvalueid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 删除别名的SQL语句
		sql = "delete from word where stdwordid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定词条id参数
		lstpara.add(attrvalueid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		// 定义更新服务或产品的SQL语句
		sql = "update serviceorproductinfo set attr" + column
				+ " = null where attr" + column + "=? and serviceid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定属性值参数
		lstpara.add(name);
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		// 生成操作日志记录
		String logSql = "";
		if (GetConfigValue.isMySQL) {
			logSql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,sysdate())";
		} else if (GetConfigValue.isOracle) {
			logSql = "insert into operationlog(ip,brand,service,operation,city,workerid,workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)";

		}
		// 将SQL语句放入集合中
		lstSql.add(logSql);

		List<String> logLstpara = new ArrayList<String>();
		logLstpara.add(user.getUserIP());
		logLstpara.add(" ");
		logLstpara.add(" ");
		logLstpara.add("删除词条");
		logLstpara.add("上海");
		logLstpara.add(user.getUserID());
		logLstpara.add(user.getUserName());
		logLstpara.add(wordclass + "==>" + name);
		logLstpara.add("WORD");
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(logLstpara);

		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}

	/**
	 * 分页查询服务或产品信息
	 * 
	 * @param serviceid参数业务id
	 * @param selattr参数属性值数组
	 * @return json串
	 */
	public static Result SelectAttr(String serviceid, String selattr,
			String citys) {
		String[] attrArr = selattr.split("@");
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义查询服务或产品信息的总条数
		sql
				.append("select count(*) as total from serviceorproductinfo where serviceid=? ");
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 循环遍历属性值数组
		for (int i = 0; i < attrArr.length; i++) {
			// 判断attri是否等于0
			if (!"".equals(attrArr[i]) && attrArr[i] != null) {
				// 加上attri的条件
				sql.append(" and attr" + (i + 1) + " like ? ");
				// 绑定attri的参数
				lstpara.add("%" + attrArr[i] + "%");
			}
		}

		if (!"'全国'".equals(citys)) {
			String num = "";
			String tempSql = "select columnnum from serviceattrname2colnum where serviceid='"
					+ serviceid + "' and name='cityId'";
			Result tempResult = Database.executeQuery(tempSql);
			if (tempResult != null && tempResult.getRowCount() > 0) {
				num = tempResult.getRows()[0].get("columnnum").toString();
				sql.append(" and attr" + num + " in (" + citys + ")");
			}
		}
		// 将SQL语句补充完整
		sql.append(" order by serviceorproductinfoid desc");
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 分页查询服务或产品信息
	 * 
	 * @param serviceid参数业务id
	 * @param attrArr参数属性值数组
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return rs
	 */
	public static Result SelectAttr(String serviceid, String selattr,
			String citys, int page, int rows) {
		String[] attrArr = selattr.split("@");
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 分页查询满足条件的SQL语句
		if (GetConfigValue.isOracle) {
			sql
					.append("select * from (select t.*,rownum rn from (select * from serviceorproductinfo where serviceid=? ");
		} else {
			sql
					.append("select * from (select * from serviceorproductinfo where serviceid=? ");
		}
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 循环遍历属性值数组
		for (int i = 0; i < attrArr.length; i++) {
			// 判断attri是否等于0
			if (!"".equals(attrArr[i]) && attrArr[i] != null) {
				// 加上attri的条件
				sql.append(" and attr" + (i + 1) + " like ? ");
				// 绑定attri的参数
				lstpara.add("%" + attrArr[i] + "%");
			}
		}

		if (!"'全国'".equals(citys)) {
			String num = "";
			String tempSql = "select columnnum from serviceattrname2colnum where serviceid='"
					+ serviceid + "' and name='cityId'";
			Result tempResult = Database.executeQuery(tempSql);
			if (tempResult != null && tempResult.getRowCount() > 0) {
				num = tempResult.getRows()[0].get("columnnum").toString();
				sql.append(" and attr" + num + " in (" + citys + ")");
			}
		}

		// 将SQL语句补充完整
		if (GetConfigValue.isOracle) {
			sql
					.append(" order by status asc,serviceorproductinfoid desc)t) where rn>? and rn<=?");
		} else {
			sql
					.append(" order by status asc,serviceorproductinfoid desc) t limit ?,?");
		}
		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(page * rows);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 新增服务或产品信息
	 * 
	 * @param serviceid参数业务id
	 * @param attrArr参数属性值数组
	 * @return 新增返回的json串
	 */
	public static Result InsertAttr(String serviceid, String[] attrArr) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义查询服务或产品信息是否重复的SQL语句
		sql.append("select * from serviceorproductinfo where serviceid=? ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 循环遍历属性值数组
		for (int i = 0; i < attrArr.length; i++) {
			// 判断attri是否等于0
			if (!"".equals(attrArr[i])) {
				// 加上attri的条件
				sql.append(" and attr" + (i + 1) + "=? ");
				// 绑定attri的参数
				lstpara.add(attrArr[i]);
			} else {
				// 不等于0，加上attri is null 条件
				sql.append(" and attr" + (i + 1) + " is null ");
			}
		}
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 新增服务或产品信息
	 * 
	 * @param serviceid参数业务id
	 * @param service参数业务
	 * @param attrArr参数属性值数组
	 * @return 新增返回的json串
	 */
	public static int InsertAttr(User user, String serviceid, String service,
			String[] attrArr, String serviceType) {
		if ("电信行业->电信集团->指令系统应用".equals(serviceType)
				&& "短信指令信息表".equals(service)) {
			// 定义SQL语句
			StringBuilder sql = new StringBuilder();
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 定义获取属性名称对应的列值的SQL语句
			sql.append("select * from serviceattrname2colnum where serviceid=? ");
			// 绑定业务id参数
			lstpara.add(serviceid);
			// 执行SQL语句，获取相应的数据源
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());

			// 定义存放数据约束和对应列值的map集合
			Map<String, String> columncheckwayMap = new HashMap<String, String>();
			// 定义存放属性名称和对应列值的map集合
			Map<String, Integer> attrnameToColumnMap = new HashMap<String, Integer>();
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 循环遍历数据源
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 获取列值
					String column = rs.getRows()[i].get("columnnum").toString();
					String name = rs.getRows()[i].get("name").toString();
					// 获取数据约束
					String checkway = rs.getRows()[i].get("checkway") == null ? ""
							: rs.getRows()[i].get("checkway").toString();

					if (!"".equals(checkway)) {// 存在数据约束
						columncheckwayMap.put(column, checkway);
					}
					attrnameToColumnMap.put(name, Integer.valueOf(column));

				}
				// 进行数据重复验证
				if (columncheckwayMap.size() > 0) {
					String checkwaySql = "select * from serviceorproductinfo where serviceid=?";
					// 定义绑定参数集合
					List<Object> checkwaylstpara = new ArrayList<Object>();
					checkwaylstpara.add(serviceid);

					for (Map.Entry<String, String> entry : columncheckwayMap
							.entrySet()) {
						checkwaySql = checkwaySql + " and attr"
								+ entry.getKey() + "=?";
						String value = attrArr[Integer.parseInt(entry.getKey()) - 1] == null ? ""
								: attrArr[Integer.parseInt(entry.getKey()) - 1]
										.toString();
						checkwaylstpara.add(value);
					}
					Result checkwayRs = Database.executeQuery(checkwaySql,
							checkwaylstpara.toArray());
					// 存在重复值了
					if (checkwayRs != null && checkwayRs.getRowCount() > 0) {
						return -9;
					}
				}
			}
			// 电信集团短厅定制需求，数据处理
			if (attrnameToColumnMap.containsKey("业务名")
					&& attrnameToColumnMap.containsKey("操作类型")
					&& attrnameToColumnMap.containsKey("指令")
					&& attrnameToColumnMap.containsKey("cityName")
					&& attrnameToColumnMap.containsKey("cityId")
					&& attrnameToColumnMap.containsKey("isInstruction")) {
				Object value01 = attrArr[attrnameToColumnMap.get("业务名")-1];
				Object value02 = attrArr[attrnameToColumnMap.get("操作类型")-1];
				Object value03 = attrArr[attrnameToColumnMap.get("指令")-1];
				Object value04 = attrArr[attrnameToColumnMap.get("cityName")-1];
				Object value05 = attrArr[attrnameToColumnMap.get("cityId")-1];
				Object value06 = attrArr[attrnameToColumnMap.get("isInstruction")-1];
				if (null == value01 || null == value02 || null == value03
						|| null == value04 || null == value05
						|| null == value06 || "".equals(value01) || "".equals(value02) || "".equals(value03)
						|| "".equals(value04) || "".equals(value05)
						|| "".equals(value06)) {
					return -7;
				} else {
					Pattern pattern = Pattern.compile("\\d{6}");
					if (!pattern.matcher(value05.toString()).matches()){
						return -5;
					}else if (!"true"
							.equals(value06.toString().toLowerCase()) && !"false"
							.equals(value06.toString().toLowerCase())){
						return -6;
					}
				}
			} 
		}

		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		// 定义新增服务或产品信息的SQL语句
		sql
				.append("insert into serviceorproductinfo (serviceorproductinfoid,serviceid,service,");
		// 循环遍历属性值数组
		for (int i = 0; i < attrArr.length; i++) {
			// 加上attr的条件
			sql.append("attr" + (i + 1) + ",");
		}
		// 将SQL语句补充完整
		sql.append("status) values (?,?,?,");
		String serviceorproductinfoid = "";
		if (GetConfigValue.isOracle) {
			serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew(
					"serviceorproductinfo_seq", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			serviceorproductinfoid = ConstructSerialNum.getSerialIDNew(
					"serviceorproductinfo", "serviceorproductinfoid",
					bussinessFlag);
		}
		// 获取服务或产品信息表的序列值
		lstpara.add(serviceorproductinfoid);
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 绑定业务参数
		lstpara.add(service);
		// 循环遍历属性值数组
		for (int i = 0; i < attrArr.length; i++) {
			// 加上attr的绑定参数条件
			sql.append("?,");
			// 判断attri是否等于0
			if (!"".equals(attrArr[i])) {
				// 绑定attri的参数
				lstpara.add(attrArr[i]);
			} else {
				lstpara.add(null);
			}
		}
		// 加上状态的条数
		sql.append("?)");
		// 绑定状态参数
		lstpara.add("0");
		
		lstSql.add(sql.toString());
		lstLstpara.add(lstpara);
		
		// 将操作日志SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", service,
				"新增信息", Arrays.toString(attrArr).length() > 2000 ? Arrays.toString(attrArr).substring(0, 2000) : Arrays.toString(attrArr), "SERVICEORPRODUCTINFO"));
		
		// 执行SQL语句，绑定事务，返回事务处理
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return c;
	}

	/**
	 * 删除服务或产品信息
	 * 
	 * @param attrid参数服务或产品信息id
	 * @return 删除返回的json串
	 */
	public static int DeleteAttr(String attrid, User user) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		
		String selectSql = "select * from serviceorproductinfo where serviceorproductinfoid in (";
		// 将id按照逗号拆分
		String[] attridArr = attrid.split(",");
		// 循环遍历id数组
		for (int i = 0; i < attridArr.length; i++) {
			if (i != attridArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				selectSql += "?,";
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				selectSql += "?)";
			}
			// 绑定id参数
			lstpara.add(attridArr[i]);
		}
		Result selectRs = Database.executeQuery(selectSql, lstpara.toArray());
		String service = "";
		if (selectRs != null && selectRs.getRowCount() > 0){
			service = selectRs.getRows()[0].get("service").toString();
		}
		
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 定义删除服务或产品信息的SQL语句
		sql.append("delete from serviceorproductinfo where serviceorproductinfoid in (");
		// 将id按照逗号拆分
//		String[] attridArr = attrid.split(",");
		// 循环遍历id数组
		for (int i = 0; i < attridArr.length; i++) {
			if (i != attridArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定id参数
			lstpara.add(attridArr[i]);
		}
		
		lstSql.add(sql.toString());
		lstLstpara.add(lstpara);
		
		
		// 将操作日志SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", service,
				"删除信息", attrid, "SERVICEORPRODUCTINFO"));
		
		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return c;
	}

	/**
	 * 确认服务或产品信息
	 * 
	 * @param attrid参数服务或产品信息id
	 * @return 确认返回的json串
	 */
	public static int ConfirmAttr(String attrid) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义确认服务或产品信息的SQL语句
		sql
				.append("update serviceorproductinfo set status=1 where serviceorproductinfoid in (");
		// 将id按照逗号拆分
		String[] attridArr = attrid.split(",");
		// 循环遍历id数组
		for (int i = 0; i < attridArr.length; i++) {
			if (i != attridArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定id参数
			lstpara.add(attridArr[i]);
		}
		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQuery(sql.toString(), lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return c;
	}

	/**
	 * 更新服务或产品信息
	 * 
	 * @param serviceid参数业务id
	 * @param attrArr参数属性值数组
	 * @param attrid参数服务或产品信息id
	 * @return 更新返回的json串
	 */
	public static Result UpdateAttr(String serviceid, String[] attrArr) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义查询服务或产品信息是否重复的SQL语句
		sql.append("select * from serviceorproductinfo where serviceid=? ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 循环遍历属性值数组
		for (int i = 0; i < attrArr.length; i++) {
			// 判断attri是否等于0
			if (!"".equals(attrArr[i])) {
				// 加上attri的条件
				sql.append(" and attr" + (i + 1) + "=? ");
				// 绑定attri的参数
				lstpara.add(attrArr[i]);
			} else {
				// 不等于0，加上attri is null 条件
				sql.append(" and attr" + (i + 1) + " is null ");
			}
		}
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 更新服务或产品信息
	 * 
	 * @param serviceid参数业务id
	 * @param attrArr参数属性值数组
	 * @param attrid参数服务或产品信息id
	 * @return 更新返回的json串
	 */
	public static int UpdateAttr(String[] attrArr, String attrid,
			String serviceType, String serviceid, User user) {
		if ("电信行业->电信集团->指令系统应用".equals(serviceType)) {
			// 定义SQL语句
			StringBuilder sql = new StringBuilder();
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 定义获取属性名称对应的列值的SQL语句
			sql
					.append("select * from serviceattrname2colnum where serviceid=? ");
			// 绑定业务id参数
			lstpara.add(serviceid);
			// 执行SQL语句，获取相应的数据源
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());

			// 定义存放数据约束和对应列值的map集合
			Map<String, String> columncheckwayMap = new HashMap<String, String>();
			// 定义存放属性名称和对应列值的map集合
			Map<String, Integer> attrnameToColumnMap = new HashMap<String, Integer>();
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 循环遍历数据源
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 获取列值
					String column = rs.getRows()[i].get("columnnum").toString();
					String name = rs.getRows()[i].get("name").toString();
					// 获取数据约束
					String checkway = rs.getRows()[i].get("checkway") == null ? ""
							: rs.getRows()[i].get("checkway").toString();

					if (!"".equals(checkway)) {// 存在数据约束
						columncheckwayMap.put(column, checkway);
					}
					attrnameToColumnMap.put(name, Integer.valueOf(column));

				}
				// 进行数据重复验证
				if (columncheckwayMap.size() > 0) {
					String checkwaySql = "select * from serviceorproductinfo where serviceid=? and SERVICEORPRODUCTINFOID !=?";
					// 定义绑定参数集合
					List<Object> checkwaylstpara = new ArrayList<Object>();
					checkwaylstpara.add(serviceid);
					checkwaylstpara.add(attrid);

					for (Map.Entry<String, String> entry : columncheckwayMap
							.entrySet()) {
						checkwaySql = checkwaySql + " and attr"
								+ entry.getKey() + "=?";
						String value = attrArr[Integer.parseInt(entry.getKey()) - 1] == null ? ""
								: attrArr[Integer.parseInt(entry.getKey()) - 1]
										.toString();
						checkwaylstpara.add(value);
					}
					Result checkwayRs = Database.executeQuery(checkwaySql,
							checkwaylstpara.toArray());
					// 存在重复值了
					if (checkwayRs != null && checkwayRs.getRowCount() > 0) {
						return -9;
					}
				}
			}

			// 电信集团短厅定制需求，数据处理
			if (attrnameToColumnMap.containsKey("业务名")
					&& attrnameToColumnMap.containsKey("操作类型")
					&& attrnameToColumnMap.containsKey("指令")
					&& attrnameToColumnMap.containsKey("cityName")
					&& attrnameToColumnMap.containsKey("cityId")
					&& attrnameToColumnMap.containsKey("isInstruction")) {
				Object value01 = attrArr[attrnameToColumnMap.get("业务名")-1];
				Object value02 = attrArr[attrnameToColumnMap.get("操作类型")-1];
				Object value03 = attrArr[attrnameToColumnMap.get("指令")-1];
				Object value04 = attrArr[attrnameToColumnMap.get("cityName")-1];
				Object value05 = attrArr[attrnameToColumnMap.get("cityId")-1];
				Object value06 = attrArr[attrnameToColumnMap
						.get("isInstruction")-1];
				if (null == value01 || null == value02 || null == value03
						|| null == value04 || null == value05
						|| null == value06 || "".equals(value01) || "".equals(value02) || "".equals(value03)
						|| "".equals(value04) || "".equals(value05)
						|| "".equals(value06)) {
					return -7;
				} else {
					Pattern pattern = Pattern.compile("\\d{6}");
					if (!pattern.matcher(value05.toString()).matches()){
						return -5;
					}else if (!"true"
							.equals(value06.toString().toLowerCase()) && !"false"
							.equals(value06.toString().toLowerCase())){
						return -6;
					}
				}
			} 

		}
		String selectSql = "select * from serviceorproductinfo where serviceorproductinfoid in (";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 将id按照逗号拆分
		String[] attridArr = attrid.split(",");
		// 循环遍历id数组
		for (int i = 0; i < attridArr.length; i++) {
			if (i != attridArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				selectSql += "?,";
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				selectSql += "?)";
			}
			// 绑定id参数
			lstpara.add(attridArr[i]);
		}
		Result selectRs = Database.executeQuery(selectSql, lstpara.toArray());
		String service = "";
		if (selectRs != null && selectRs.getRowCount() > 0){
			service = selectRs.getRows()[0].get("service").toString();
		}
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
//		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 定义新增服务或产品信息的SQL语句
		sql.append("update serviceorproductinfo set ");
		// 循环遍历属性值数组
		for (int i = 0; i < attrArr.length; i++) {
			// 加上attr条件
			sql.append(" attr" + (i + 1) + "=?,");
			// 判断attri是否等于0
			if (!"".equals(attrArr[i])) {
				// 绑定attri的参数
				lstpara.add(attrArr[i]);
			} else {
				lstpara.add(null);
			}
		}
		// 将SQL语句补充完整
		sql.append("status=? where serviceorproductinfoid=?");
		// 绑定状态参数
		lstpara.add("0");
		// 绑定服务或产品信息id参数
		lstpara.add(attrid);
		
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		lstSql.add(sql.toString());
		lstLstpara.add(lstpara);
		
		
		// 将操作日志SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", service,
				"更新信息", org.apache.commons.lang.StringUtils.join(lstpara, ",").length()>2000?org.apache.commons.lang.StringUtils.join(lstpara, ",").substring(0, 2000):org.apache.commons.lang.StringUtils.join(lstpara, ","), "SERVICEORPRODUCTINFO"));
		
		// 执行SQL语句，绑定事务，返回事务处理
		int c =  Database.executeNonQueryTransaction(lstSql, lstLstpara);

		return c;
	}

	/**
	 * 全量删除服务或产品信息
	 * 
	 * @param serviceid参数业务id
	 * @return 全量删除返回的json串
	 */
	public static int DeleteAllAttr(String serviceid, String citys) {
		// 定义SQL语句
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义删除服务或产品信息的SQL语句
		String sql = "delete from serviceorproductinfo where serviceid=?";
		// 绑定业务id参数
		lstpara.add(serviceid);

		if (!"'全国'".equals(citys)) {
			String num = "";
			String tempSql = "select columnnum from serviceattrname2colnum where serviceid='"
					+ serviceid + "' and name='cityId'";
			Result tempResult = Database.executeQuery(tempSql);
			if (tempResult != null && tempResult.getRowCount() > 0) {
				num = tempResult.getRows()[0].get("columnnum").toString();
				sql += " and attr" + num + " in (" + citys + ")";
			}
		}

		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return c;
	}

	/**
	 * 全量确认服务或产品信息
	 * 
	 * @param serviceid参数业务id
	 * @return 全量确认返回的json串
	 */
	public static int ConfirmAllAttr(String serviceid, String citys) {
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义确认服务或产品信息的SQL语句
		String sql = "update serviceorproductinfo set status=1 where serviceid=?";
		// 绑定业务id参数
		lstpara.add(serviceid);

		if (!"'全国'".equals(citys)) {
			String num = "";
			String tempSql = "select columnnum from serviceattrname2colnum where serviceid='"
					+ serviceid + "' and name='cityId'";
			Result tempResult = Database.executeQuery(tempSql);
			if (tempResult != null && tempResult.getRowCount() > 0) {
				num = tempResult.getRows()[0].get("columnnum").toString();
				sql += " and attr" + num + " in (" + citys + ")";
			}
		}

		// 执行SQL语句，绑定事务，返回事务处理结果
		int c = Database.executeNonQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return c;
	}

	/**
	 * 新增服务或产品信息
	 * 
	 * @param attrinfo参数导入文件的内容
	 * @param serviceid参数业务id
	 * @param service参数业务
	 * @return 返回事务处理结果
	 */
	public static Result InsertServiceOrProductInfo(
			List<List<Object>> attrinfo, String serviceid) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义属性名称集合
		List<Object> attrnameLst = attrinfo.get(0);
		// 判断导入内容的集合的个数是否大于0
		if (attrinfo.size() > 0) {
			// 定义SQL语句
			sql = new StringBuilder();
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 定义获取属性名称对应的列值的SQL语句
			sql
					.append("select * from serviceattrname2colnum where serviceid=? and name in (");
			// 绑定业务id参数
			lstpara.add(serviceid);
			// 循环遍历属性名称集合
			for (int i = 0; i < attrnameLst.size(); i++) {
				if (i != attrnameLst.size() - 1) {
					// 除了最后一个不加逗号，其他都加逗号
					sql.append("?,");
				} else {
					// 最后一个加上右括号，将SQL语句补充完整
					sql.append("?)");
				}
				// 绑定属性名称参数
				lstpara.add(attrnameLst.get(i));
			}
			sql.append(" order by columnnum asc");
			// 执行SQL语句，获取相应的数据源
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());

			// 文件日志
			GlobalValue.myLog.info(sql + "#" + lstpara);

			return rs;
		}
		return null;
	}

	/**
	 * 新增服务或产品信息
	 * 
	 * @param attrinfo参数导入文件的内容
	 * @param serviceid参数业务id
	 * @param service参数业务
	 * @return 返回事务处理结果
	 */
	public static int InsertServiceOrProductInfo(List<List<Object>> attrinfo,
			String serviceid, String service, String serviceType, User user) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();

		// 定义属性名称集合
		List<Object> attrnameLst = attrinfo.get(0);

		// 添加词条标识
		boolean wordFlag = false;
		Map<String, String> nameToAttrMap = new HashMap<String, String>();
		List<String> wordList = new ArrayList<String>();
		String attrWordclassid = "";
		String attrColumn = "";
		// if ("电信行业->电信集团->指令系统应用".equals(serviceType)){
		// if (attrnameLst.contains("cityId") && attrnameLst.contains("业务名")){
		// wordFlag = true;
		// }
		// }
		int count = 0;
		// 判断导入内容的集合的个数是否大于0
		if (attrinfo.size() > 0) {
			// 定义SQL语句
			sql = new StringBuilder();
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 定义获取属性名称对应的列值的SQL语句
			sql
					.append("select * from serviceattrname2colnum where serviceid=? and name in (");
			// 绑定业务id参数
			lstpara.add(serviceid);
			// 循环遍历属性名称集合
			for (int i = 0; i < attrnameLst.size(); i++) {
				if (i != attrnameLst.size() - 1) {
					// 除了最后一个不加逗号，其他都加逗号
					sql.append("?,");
				} else {
					// 最后一个加上右括号，将SQL语句补充完整
					sql.append("?)");
				}
				// 绑定属性名称参数
				lstpara.add(attrnameLst.get(i));
			}
			sql.append(" order by columnnum asc");

			// 执行SQL语句，获取相应的数据源
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());

			// 文件日志
			GlobalValue.myLog.info(sql + "#" + lstpara);

			String[] columnArr = new String[attrnameLst.size()];
			// 定义存放属性名称的下标和对应列值的map集合
			Map<String, Integer> attrnamecolumnMap = new HashMap<String, Integer>();
			// 定义存放属性名称和对应列值的map集合
			Map<String, Integer> attrnameToColumnMap = new HashMap<String, Integer>();
			// 定义存放数据约束和对应列值的map集合
			Map<String, String> columncheckwayMap = new HashMap<String, String>();
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 循环遍历数据源
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 获取属性名称
					String attrname = rs.getRows()[i].get("name").toString();
					// 获取列值
					String column = rs.getRows()[i].get("columnnum").toString();
					// 获取词类id
					String wordclassid = rs.getRows()[i].get("WORDCLASSID") == null ? ""
							: rs.getRows()[i].get("WORDCLASSID").toString();
					// 获取数据约束
					String checkway = rs.getRows()[i].get("checkway") == null ? ""
							: rs.getRows()[i].get("checkway").toString();

					if (!"".equals(checkway)) {// 存在数据约束
						columncheckwayMap.put(column, checkway);
					}

					// 循环遍历属性名称数组
					for (int j = 0; j < attrnameLst.size(); j++) {
						// 判断属性名称与第j个集合的值是否相等
						if (attrname.equals(attrnameLst.get(j))) {
							// 将对应的下标和列值放入map集合中
							attrnamecolumnMap.put(column, j);
							// 将对应的name和列值放入map集合中
							attrnameToColumnMap.put(attrname, j);
							// 将列值数组中填充上列值
							columnArr[j] = column;

							// 需要做词条同步
							if (wordFlag) {
								// 保存列名信息
								if (attrname.equals("cityId")) {
									nameToAttrMap.put(attrname, column);
								}
								if (attrname.equals("业务名")) {
									nameToAttrMap.put(attrname, column);
									attrWordclassid = wordclassid;
									attrColumn = column;
									if (!"".equals(attrWordclassid)) {
										String tempsql = "select * from word where wordclassid=? and stdwordid is null";
										Result temprs = Database.executeQuery(
												tempsql, wordclassid);
										if (temprs != null
												&& temprs.getRowCount() > 0) {
											for (int k = 0; k < temprs
													.getRowCount(); k++) {
												wordList
														.add(temprs.getRows()[k]
																.get("word")
																.toString());
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				// 没有查询到对应的列值，就直接将列值从1开始计数
				for (int i = 0; i < columnArr.length; i++) {
					columnArr[i] = String.valueOf(i + 1);
				}
			}

			// 定义存放对比值的list
			List<Object> checkValueList = new ArrayList<Object>();

			// 循环遍历服务或产品信息的集合
			for (int m = 1; m < attrinfo.size(); m++) {

				// 进行数据重复验证
				if (columncheckwayMap.size() > 0) {
					String checkValue = "";
					String checkwaySql = "select * from serviceorproductinfo where serviceid=?";
					// 定义绑定参数集合
					List<Object> checkwaylstpara = new ArrayList<Object>();
					checkwaylstpara.add(serviceid);

					for (Map.Entry<String, String> entry : columncheckwayMap
							.entrySet()) {
						checkwaySql = checkwaySql + " and attr"
								+ entry.getKey() + "=?";
						String value = attrinfo.get(m).get(
								attrnamecolumnMap.get(entry.getKey())) == null ? ""
								: attrinfo.get(m).get(
										attrnamecolumnMap.get(entry.getKey()))
										.toString();
						checkwaylstpara.add(value);
						checkValue = checkValue + value + "@@";
					}
					Result checkwayRs = Database.executeQuery(checkwaySql,
							checkwaylstpara.toArray());
					// 存在重复值了
					if (checkwayRs != null && checkwayRs.getRowCount() > 0) {
						return -9;
					} else {
						if (checkValueList.contains(checkValue)) {
							return -9;
						} else {
							checkValueList.add(checkValue);
						}
					}
				}
				// 电信集团短厅定制需求，数据处理
				if ("电信行业->电信集团->指令系统应用".equals(serviceType)
						&& "短信指令信息表".equals(service)) {
					if (attrnameToColumnMap.containsKey("业务名")
							&& attrnameToColumnMap.containsKey("操作类型")
							&& attrnameToColumnMap.containsKey("指令")
							&& attrnameToColumnMap.containsKey("cityName")
							&& attrnameToColumnMap.containsKey("cityId")
							&& attrnameToColumnMap.containsKey("isInstruction")) {
						Object value01 = attrinfo.get(m).get(
								attrnameToColumnMap.get("业务名"));
						Object value02 = attrinfo.get(m).get(
								attrnameToColumnMap.get("操作类型"));
						Object value03 = attrinfo.get(m).get(
								attrnameToColumnMap.get("指令"));
						Object value04 = attrinfo.get(m).get(
								attrnameToColumnMap.get("cityName"));
						Object value05 = attrinfo.get(m).get(
								attrnameToColumnMap.get("cityId"));
						Object value06 = attrinfo.get(m).get(
								attrnameToColumnMap.get("isInstruction"));
						if (null == value01 || null == value02
								|| null == value03 || null == value04
								|| null == value05 || null == value06) {
							return -7;
						} else {
							Pattern pattern = Pattern.compile("\\d{6}");
							if (!pattern.matcher(value05.toString()).matches()
									|| (!"true".equals(value06.toString()
											.toLowerCase()) && !"false"
											.equals(value06.toString()
													.toLowerCase()))) {
								return -6;
							}
						}
					} else {
						return -8;
					}
				}

				// 定义SQL语句
				sql = new StringBuilder();
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 定义新增服务或产品信息的SQL语句
				sql
						.append("insert into serviceorproductinfo (serviceorproductinfoid,");
				// 循环遍历列值数组
				for (int i = 0; i < columnArr.length; i++) {
					// 判断第i个列值数组的值是否为null，空
					if (columnArr[i] != null && !"".equals(columnArr[i])) {
						// 加上attr的条件
						sql.append("attr" + columnArr[i] + ",");
					}
				}
				// 将新增的SQL语句补充完整
				sql.append("serviceid,service,status) values (");
				String bussinessFlag = CommonLibMetafieldmappingDAO
						.getBussinessFlag(serviceType);
				String serviceorproductinfoid = "";
				if (GetConfigValue.isOracle) {
					serviceorproductinfoid = ConstructSerialNum
							.GetOracleNextValNew("serviceorproductinfo_seq",
									bussinessFlag);
				} else if (GetConfigValue.isMySQL) {
					serviceorproductinfoid = ConstructSerialNum.getSerialIDNew(
							"serviceorproductinfo", "serviceorproductinfoid",
							bussinessFlag);
				}
				sql.append(serviceorproductinfoid + ",");
				// 循环遍历列值数组
				for (int i = 0; i < columnArr.length; i++) {
					// 判断第i个列值数组的值是否为null，空
					if (columnArr[i] != null && !"".equals(columnArr[i])) {

						// 加上attr的绑定变量
						sql.append("?,");
						Object value = attrinfo.get(m).get(
								attrnamecolumnMap.get(columnArr[i]));
						if (value != null) {
							if (value.toString().length() > 2000) {
								value = "too long to import";
							}
						}
						// 通过列值数组的第i个值为key，通过map集合获取对应的集合下标，通过下标得到新增时绑定attr的值
						lstpara.add(value);
					}
				}
				// 将新增的SQL语句补充完整
				sql.append("?,?,?)");
				// 绑定业务id参数
				lstpara.add(serviceid);
				// 绑定业务参数
				lstpara.add(service);
				// 绑定状态参数
				lstpara.add("0");
				// 将SQL语句放入集合中
				lstSql.add(sql.toString());
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);

				// 文件日志
				GlobalValue.myLog.info(sql + "#" + lstpara);

				if (!wordList.isEmpty()) {
					if (wordList.contains(attrinfo.get(m).get(
							attrnamecolumnMap.get(attrColumn)))) {
						// 定义SQL语句
						sql = new StringBuilder();
						// 定义绑定参数集合
						lstpara = new ArrayList<Object>();
						sql
								.append("update word set city=city||?,cityname=cityname||? where wordclassid=? and word=? and city not like ?");
						lstpara.add(","
								+ attrinfo.get(m).get(
										attrnamecolumnMap.get(nameToAttrMap
												.get("cityId"))));
						lstpara.add(","
								+ cityCodeToCityName.get(attrinfo.get(m).get(
										attrnamecolumnMap.get(nameToAttrMap
												.get("cityId")))));
						lstpara.add(attrWordclassid);
						lstpara.add(attrinfo.get(m).get(
								attrnamecolumnMap.get(attrColumn)));
						lstpara.add("%"
								+ attrinfo.get(m).get(
										attrnamecolumnMap.get(nameToAttrMap
												.get("cityId"))) + "%");

						lstSql.add(sql.toString());
						lstLstpara.add(lstpara);
					} else {
						// 新增
						int wordInsert = CommonLibWordDAO.insert(attrinfo
								.get(m).get(attrnamecolumnMap.get(attrColumn))
								.toString(), attrWordclassid, user);
						if (wordInsert > 0) {
							// 定义SQL语句
							sql = new StringBuilder();
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							sql
									.append("update word set city=?,cityname=? where wordclassid=? and word=?");
							lstpara.add(attrinfo.get(m).get(
									attrnamecolumnMap.get(nameToAttrMap
											.get("cityId"))));
							lstpara.add(cityCodeToCityName.get(attrinfo.get(m)
									.get(
											attrnamecolumnMap.get(nameToAttrMap
													.get("cityId")))));
							lstpara.add(attrWordclassid);
							lstpara.add(attrinfo.get(m).get(
									attrnamecolumnMap.get(attrColumn)));
							lstSql.add(sql.toString());
							lstLstpara.add(lstpara);

							wordList.add(attrinfo.get(m).get(
									attrnamecolumnMap.get(attrColumn))
									.toString());
						}
					}
				}
			}
			// 执行SQL语句，绑定事务返回事务处理结果
			count = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		}
		return count;
	}

	/**
	 * 读取数据库，生成Excel文件，返回文件的路径
	 * 
	 * @param kbdataid参数摘要id
	 * @return 生成文件的路径
	 */
	public static Result ExportExcel(String kbdataid) {
		// 定义查询属性名称的SQL语句
		String sql = "select * from serviceattrname2colnum where serviceid=? order by columnnum asc";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(kbdataid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);
		return rs;
	}

	/**
	 * 读取数据库，生成Excel文件，返回文件的路径
	 * 
	 * @param kbdataid参数摘要id
	 * @return 生成文件的路径
	 */
	public static Result ExportExcel2(String kbdataid) {
		// 查询服务或产品信息的SQL语句
		String sql = "select * from serviceorproductinfo where serviceid=? ";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(kbdataid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}

	/**
	 * 根据服务或产品信息表更新属性值
	 * 
	 * @param serviceid参数业务id
	 * @return 更新是否成功
	 */
	public static Result UpdateAttrValue(String serviceid) {
		// 查询属性名称跟词类表关联的SQL语句
		String sql = "select c.columnnum,c.wordclassid from serviceattrname2colnum c,wordclass w where c.wordclassid=w.wordclassid and c.serviceid=? order by c.columnnum asc";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 根据服务或产品信息表更新属性值
	 * 
	 * @param serviceid参数业务id
	 * @return 更新是否成功
	 */
	public static Result UpdateAttrValue2(String wordclassid) {
		// 先查询属性名称对应的所有属性值(词条)
		String sql = "select * from word where stdwordid is null and wordclassid=?";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 绑定词类名称id
		lstpara.add(wordclassid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 根据服务或产品信息表更新属性值
	 * 
	 * @param serviceid参数业务id
	 * @return 更新是否成功
	 */
	public static Result UpdateAttrValue(String serviceid, String column) {
		// 查询在服务或产品信息表中每一个attr对应的属性值的SQL语句
		String sql = "select distinct attr" + column
				+ " attr from serviceorproductinfo where serviceid=? and attr"
				+ column + " is not null";
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 根据服务或产品信息表更新属性值
	 * 
	 * @param serviceid参数业务id
	 * @return 更新是否成功
	 */
	public static List<String> UpdateAttrValue(List<String> lstSql) {
		// 新增属性值的SQL语句
		String sql = "insert into word(wordid,wordclassid,word,type) values(?,?,?,?)";
		lstSql.add(sql);
		return lstSql;
	}

	public static List<List<?>> UpdateAttrValueLstLstpara(
			List<List<?>> lstLstpara, String wordclassid, String value,
			String serviceType) {
		// 定义绑定参数集合
		ArrayList<Object> lstpara = new ArrayList<Object>();
		String wordid = "";
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(serviceType);
		if (GetConfigValue.isOracle) {
			wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id",
					bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			wordid = ConstructSerialNum.getSerialIDNew("word", "wordid",
					bussinessFlag);
		}
		// 绑定id参数
		lstpara.add(wordid);
		// 绑定词类id
		lstpara.add(wordclassid);
		// 绑定属性值名称
		lstpara.add(value);
		// 绑定类型参数
		lstpara.add("标准名称");
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		return lstLstpara;
	}

	/**
	 * 通过业务ID产寻摘要
	 * 
	 * @param scenariosid
	 *            场景ID
	 * @return
	 */
	public static Result getServiceInfoColumnClByScenariosid(String scenariosid) {
		String sql = "select sb.service,sc.name from serviceattrname2colnum sc ,(select sk.serviceid,sk.service from scenarios s,scenarios2kbdata sk where s.scenariosid = sk.scenariosid and s.scenariosid="
				+ scenariosid + ") sb where sc.serviceid = sb.serviceid";
		Result rs = Database.executeQuery(sql);
		// 文件日志
		GlobalValue.myLog.info(sql);

		return rs;
	}

	/**
	 * 通过业务ID产寻摘要
	 * 
	 * @param scenariosid
	 *            场景ID
	 * @return
	 */
	public static Result getTriggeractionNameByScenariosid(String scenariosid) {
		String sql = "select sb.service,sc.name from serviceattrname2colnum sc ,(select sk.serviceid,sk.service from scenarios s,scenarios2kbdata sk where s.scenariosid = sk.scenariosid and s.scenariosid="
				+ scenariosid + ") sb where sc.serviceid = sb.serviceid";
		Result rs = Database.executeQuery(sql);

		// 文件日志
		GlobalValue.myLog.info(sql);

		return rs;
	}

	/**
	 * 查找新表下docname cloum
	 * 
	 * @param serviceid参数业务id
	 * @return 更新是否成功
	 */
	public static String selectDocnameColumnnum(String serviceid,
			String attrname) {
		// 查询在服务或产品信息表中每一个attr对应的属性值的SQL语句
		String sql = "select columnnum from serviceattrname2colnum where serviceid =? and lower(name)  =? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 绑定属性名参数
		lstpara.add(attrname);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		String columnnum = "";
		if (rs != null && rs.getRowCount() > 0) {
			columnnum = rs.getRows()[0].get("columnnum").toString();
		}
		return columnnum;
	}

	/**
	 *@description 查询信息表下文档名称
	 *@param serviceid
	 *@param column
	 *@return
	 *@returnType Result
	 */
	public static Result selectColumnValue(String serviceid, String column) {
		// 查询在服务或产品信息表中每一个attr对应的属性值的SQL语句
		String sql = "select distinct attr" + column
				+ " attr from serviceorproductinfo where serviceid=? and attr"
				+ column + " is not null";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 *@description 查询信息表下文档名称
	 *@param serviceid
	 *@param column
	 *@return
	 *@returnType Result
	 */
	public static Result selectColumnValue(String serviceid, String column,
			String city) {
		String cityidColumn = selectDocnameColumnnum(serviceid, "cityid");
		String sql = "";
		if (StringUtils.isEmpty(cityidColumn)) {
			// 查询在服务或产品信息表中每一个attr对应的属性值的SQL语句
			sql = "select distinct attr"
					+ column
					+ " attr from serviceorproductinfo where serviceid=? and attr"
					+ column + " is not null";
		} else {
			// 查询在服务或产品信息表中每一个attr对应的属性值的SQL语句
			// sql = "select distinct attr"
			// + column
			// +
			// " attr from serviceorproductinfo where serviceid=? and attr"+cityidColumn+" like '"+city+"%' and attr"
			// + column + " is not null";

			sql = "select distinct attr"
					+ column
					+ " attr from serviceorproductinfo where serviceid=? and (attr"
					+ cityidColumn + " like '" + city + "%' or attr"
					+ cityidColumn + " like '%全国%') and attr" + column
					+ " is not null";
		}

		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 *@description 查询信息表下文档名称
	 *@param serviceid
	 *@param column
	 *@return
	 *@returnType Result
	 */
	public static Result selectDocname(String serviceid, String attrname) {
		String column = selectDocnameColumnnum(serviceid, attrname);
		// 查询在服务或产品信息表中每一个attr对应的属性值的SQL语句
		String sql = "";
		Result rs = null;
		if (!"".equals(column) && column != null) {
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 绑定业务id参数
			lstpara.add(serviceid);
			// 执行SQL语句，获取相应的数据源

			sql = "select distinct attr"
					+ column
					+ " attr from serviceorproductinfo where serviceid=? and attr"
					+ column + " is not null";
			rs = Database.executeQuery(sql, lstpara.toArray());

			// 文件日志
			GlobalValue.myLog.info(sql + "#" + lstpara);

		}

		return rs;
	}

	/**
	 * 获取所有的列值
	 * 
	 * @param serviceid参数业务id
	 * @return rs
	 */
	public static Result getColumnName(String serviceid) {
		// 定义查询属性名称的列值的SQL语句
		String sql = "select distinct name from serviceattrname2colnum where serviceid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 *@description 通过业务ID，列元素查询 列元素语义关键词
	 *@param serviceid
	 *@param name
	 *@return
	 *@returnType Result
	 */
	public static Result getSemanticsKeyWordName(String serviceid, String name) {
		// 定义查询属性名称的列值的SQL语句
		String sql = "select  semanticskeyword name from serviceattrname2colnum where serviceid=?  and name =? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		lstpara.add(name);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara);

		return rs;
	}

	/**
	 * 通过业务ID获取属性数据
	 * 
	 * @param serviceid
	 * @return
	 */
	public static Result getServiceAttributions(String serviceid) {
		String sql = "select name, columnnum, semanticskeyword from  serviceattrname2colnum where serviceid=?";

		// 文件日志
		GlobalValue.myLog.info(sql + "#" + serviceid);

		return Database.executeQuery(sql, serviceid);
	}

	/**
	 * 通过业务ID获取属性(docname)数据
	 * 
	 * @param serviceid
	 * @return
	 */
	public static Result getServiceAttributions2(String serviceid) {
		String sql = "select name, columnnum, semanticskeyword from  serviceattrname2colnum where serviceid=? and name='docName'";
		// 文件日志
		GlobalValue.myLog.info(sql + "#" + serviceid);
		return Database.executeQuery(sql, serviceid);
	}

	/**
	 * 通过业务ID批量获取属性(docname)数据
	 * 
	 * @param serviceid
	 * @return
	 */
	public static Result getServiceAttributions2(List<String> serviceids) {
		if (serviceids != null && serviceids.size() > 0) {
			String ids = "";
			for (int i = 0; i < serviceids.size(); i++) {
				if (i == serviceids.size() - 1) {
					ids += serviceids.get(i);
				} else {
					ids += serviceids.get(i) + ",";
				}
			}
			String sql = "select name, columnnum, semanticskeyword ,serviceid from  serviceattrname2colnum where serviceid in ("
					+ ids + ") and name='docName'";
			// 文件日志
			GlobalValue.myLog.info(sql);
			return Database.executeQuery(sql);
		} else {
			return null;
		}

	}

	/**
	 * 
	 * @param serviceid
	 * @param sign
	 * @param column
	 * @param statisticsCount
	 * @param statisticsObj
	 * @param statisticsObjValue
	 * @return
	 */
	public static int savestatisticinfo(String serviceid, String sign,
			String column, String statisticsCount, String statisticsObj,
			String statisticsObjValue, String minValue, String maxValue) {
		int i = 0;
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();

		sql = "delete from columnstatisticinfo where " + sign
				+ "=? and COLUMNNUM=?";
		lstpara = new ArrayList<Object>();
		lstpara.add(serviceid);
		lstpara.add(column);

		lstSql.add(sql);
		lstLstpara.add(lstpara);

		sql = "insert into columnstatisticinfo (id,"
				+ sign
				+ ",STATISTICSCOUNT,STATISTICSOBJ,STATISTICSOBJVALUE,COLUMNNUM,min,max) values (COLUMNSTATISTICINFOID_SEQ.nextval,?,?,?,?,?,?,?)";
		lstpara = new ArrayList<Object>();
		lstpara.add(serviceid);
		lstpara.add(statisticsCount);
		lstpara.add(statisticsObj);
		lstpara.add(statisticsObjValue);
		lstpara.add(column);
		lstpara.add(minValue);
		lstpara.add(maxValue);

		lstSql.add(sql);
		lstLstpara.add(lstpara);

		i = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		return i;
	}

	/**
	 * 
	 * @param serviceid
	 * @param sign
	 * @param column
	 * @return
	 */
	public static int deleteColumnStatisticInfo(String serviceid, String sign,
			String column) {
		int i = 0;
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();

		sql = "delete from columnstatisticinfo where " + sign
				+ "=? and COLUMNNUM=?";
		lstpara = new ArrayList<Object>();
		lstpara.add(serviceid);
		lstpara.add(column);

		lstSql.add(sql);
		lstLstpara.add(lstpara);

		i = Database.executeNonQueryTransaction(lstSql, lstLstpara);

		return i;
	}

	/**
	 * 
	 * @param serviceid
	 * @param sign
	 * @param column
	 * @return
	 */
	public static Result getColumnStatisticInfo(String serviceid, String sign,
			String column) {
		String sql = "select * from ColumnStatisticInfo where " + sign
				+ "=? and columnnum=?";
		Result result = Database.executeQuery(sql, serviceid, column);
		return result;
	}
}
