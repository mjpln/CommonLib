package com.knowology.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

public class CommonLibKbdataAttrDAO {
	public static String[] names = { "父亲业务", "父亲摘要ID", "父亲摘要", "儿子业务", "儿子摘要",
		"儿子摘要ID", "Business","业务X","业务Y","业务Z","业务L","业务M","业务N","相关度","继承地市"};

	public static Result constructCitys() {
		String sql = "Select a.name as code,b.name as city From "+
					 "(Select Metafield.Metafieldid,Metafield.Name From Metafield,Metafieldmapping Where Metafieldmapping.Metafieldmappingid=Metafield.Metafieldmappingid and Metafieldmapping.name='地市编码配置' And Stdmetafieldid Is Null) A,"+
					 "(Select Metafield.Metafieldid,Metafield.Stdmetafieldid,Metafield.Name From Metafield,Metafieldmapping Where Metafieldmapping.Metafieldmappingid=Metafield.Metafieldmappingid and Metafieldmapping.name='地市编码配置' And Stdmetafieldid Is Not Null) B"+
					 " where a.metafieldid=b.stdmetafieldid order by b.metafieldid";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 构造业务树
	 * @param serviceid 业务id
	 * @param industry 行业
	 * @return
	 */
	public static Result createServiceTree(String serviceid,String industry) {
		String sql = "";
		if ("".equals(serviceid) || serviceid == null) {// 加载根业务
			sql = "select serviceid,service from service where service='"+industry+"'";
		} else {// 根据父业务id，加载子业务id
			sql = "select serviceid,service from service where parentid="+serviceid;
		}
	
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
			
		return rs;
	}
	
	/**
	 * 是否有子业务
	 * @param serviceid 业务id
	 * @return
	 */
	public static int hasChild(String serviceid) {
		int count = 0;
		String sql = "select count(*) as nums from service where parentid="+serviceid;
	
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		if (rs != null) {
			count = Integer.parseInt(rs.getRows()[0].get("nums").toString());
		}
		return count;
	}
	
	/**
	 * 构造摘要下拉框
	 * @param serviceid 业务id
	 * @return
	 */
	public static Result createCombobox(String serviceid) {
		String sql = "select kbdataid,abstract from kbdata where serviceid=" + serviceid 
//		+ " and topic like '复用-%'"
		;
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 根据子摘要id构造父摘要下拉框
	 * @param kbdataid 子摘要id
	 * @return
	 */
	public static Result createFatherCombobox(String kbdataid) {
		// sql语句
		String sql = "";
		sql = "select kbdataid,abstract from kbdata where kbdataid in (" +
			  "select abstractid from ServiceOrProductInfo where attr6='"+kbdataid+"')";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 根据摘要id新增列以及列对应的数据
	 * @param fkbdataid 父摘要id
	 * @return
	 */
	public static int insertColumn(String fkbdataid,String serviceType) {
		Result rs = null;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句定义的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		String[] names = {"父亲业务","父亲摘要ID","父亲摘要","儿子业务","儿子摘要","儿子摘要ID","Business"};
		String sql = "";
		// 根据kbdataid查询是否已经插入过
		sql = "select count(*) as nums from ServiceAttrName2ColNum where abstractid='"+fkbdataid+"'";
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("商家与行业问题库四层结构映射关系配置",serviceType);
		if (rsConfig != null && rsConfig.getRowCount() > 0){
			serviceType = rsConfig.getRows()[0].get("name").toString();
		}
		
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		if (rs != null && rs.getRowCount()>0) {
			int nums = Integer.parseInt(rs.getRows()[0].get("nums").toString());
			if (nums > 0) {
//				jsonObj.put("result", "该kbdataid已插入列");
				return -1;
			} else {
				sql = "insert into ServiceAttrName2ColNum(SERVICEATTRNAME2COLNUMID,NAME,ABSTRACTID,COLUMNNUM) values (?,?,?,?)";
				for (int i=0 ; i < names.length ; i++) {
					// 定义绑定参数集合
					List<Object> lstpara = new ArrayList<Object>();
					// 获取属性名称表的序列值，并绑定参数
					String serviceattrname2colnum = "";
					if(GetConfigValue.isOracle){
						serviceattrname2colnum = ConstructSerialNum.GetOracleNextValNew("serviceattrname2colnum_seq",bussinessFlag);	
						}else if(GetConfigValue.isMySQL){
						serviceattrname2colnum = ConstructSerialNum.getSerialIDNew("ServiceAttrName2ColNum","SERVICEATTRNAME2COLNUMID",bussinessFlag);	
						}
					lstpara.add(serviceattrname2colnum);
					lstpara.add(names[i]);
					lstpara.add(fkbdataid);
					lstpara.add(i+1);
					lstSql.add(sql);
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( sql + "#" + lstpara );
					
				}
			}
		} else {
			return -2;
		}
		int result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return result;
	}
	
	/**
	 * 非问题库摘要是否已经继承
	 * @param kbdataid 儿子摘要id
	 * @return
	 */
	public static Result isInherited(String cKbdataid) {
		String sql = "select abstractid from ServiceOrProductInfo where attr6='" + cKbdataid + "'";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 根据继承关系向ServiceOrProductInfo中插入值
	 * @param fkbdataid 父kbdataid
	 * @param ckbdataid 子kbdataid
	 * @return
	 */
	public static int insertColumnValue(String fkbdataid,String ckbdataid,String industry,String serviceType) {
		int result = 0;
		String sql = "insert into ServiceOrProductInfo(ServiceOrProductInfoid,abstractid,attr4,attr5,attr6,attr7,status) values (?,?,?,?,?,?,?)";
		List<Object> lstpara = new ArrayList<Object>();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		String serviceOrProductInfoid = "";
		if(GetConfigValue.isOracle){
			serviceOrProductInfoid = ConstructSerialNum.GetOracleNextValNew("ServiceOrProductInfo_seq",bussinessFlag);	
		}else if(GetConfigValue.isMySQL){
			serviceOrProductInfoid = ConstructSerialNum.getSerialIDNew("ServiceOrProductInfo","ServiceOrProductInfoid",bussinessFlag);	
		}
		lstpara.add(serviceOrProductInfoid);
		lstpara.add(fkbdataid);
		lstpara.add(getServiceOrAbstract(ckbdataid,"service"));
		lstpara.add(getServiceOrAbstract(ckbdataid,"kbdata"));
		lstpara.add(ckbdataid);
		lstpara.add(industry);
		lstpara.add(1);
		try {
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );

			result = Database.executeNonQuery(sql, lstpara.toArray());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 根据id获得业务或者摘要名
	 * @param id 主键id
	 * @param type 资源类型
	 * @return
	 */
	private static String getServiceOrAbstract(String id,String type) {
		String sql = "";
		Result rs = null;
		String result = "";
		if ("kbdata".equals(type)) {// 摘要
			sql = "select abstract as resources from kbdata where kbdataid="+id;
		} else {// 业务
			sql = "select service as resources from service where serviceid in (select serviceid from kbdata where kbdataid="+id+")";
		}
		
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		if (rs != null && rs.getRowCount()>0) {
			result = rs.getRows()[0].get("resources").toString();
		}
		
		return result;
	} 
	
	/**
	 * 分页查询属性名称信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param name参数属性名称
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return json串
	 */
	public static Result SelectAttrName(String kbdataid, String name,
			int page, int rows) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 查询满足条件的属性名称的总条数的SQL语句
		sql.append("select c.*,(select wordclass from wordclass where wordclassid=c.wordclassid) wordclass from serviceattrname2colnum c where c.abstractid=? ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(kbdataid);
		// 判断属性名称是否为null，空
		if (name != null && !"".equals(name)) {
			// 加上那么的like查询
			sql.append(" and c.name like ? ");
			// 绑定属性名称参数
			lstpara.add("%" + name + "%");
		}
		// 将SQL语句补充完整
		sql.append(" order by c.columnnum asc");
		// 分页查询
		sql.append(" limit " + (page-1)*rows + "," + rows);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 * 分页查询属性名称信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param name参数属性名称
	 * @return json串
	 */
	public static int SelectAttrName(String kbdataid, String name) {
		int result = 0;
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 查询满足条件的属性名称的总条数的SQL语句
		sql.append("select c.*,(select wordclass from wordclass where wordclassid=c.wordclassid) wordclass from serviceattrname2colnum c where c.abstractid=? ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(kbdataid);
		// 判断属性名称是否为null，空
		if (name != null && !"".equals(name)) {
			// 加上那么的like查询
			sql.append(" and c.name like ? ");
			// 绑定属性名称参数
			lstpara.add("%" + name + "%");
		}
		// 将SQL语句补充完整
		sql.append(" order by c.columnnum asc");
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		if (rs != null) {
			result = rs.getRowCount();
		}
		return result;
	}
	
	/**
	 * 获取所有的列值
	 * 
	 * @param kbdataid参数摘要id
	 * @return 列值的json串
	 */
	public static Result GetColumn(String kbdataid) {
		// 定义查询属性名称的列值的SQL语句
		String sql = "select columnnum from serviceattrname2colnum where abstractid=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 * 新增属性名称
	 * 
	 * @param kbdataid参数摘要id
	 * @param name参数属性名称
	 * @param column参数列值
	 * @param wordclass参数词类名称
	 * @return 新增是否成功
	 * @throws SQLException 
	 */
	public static int InsertAttrName(String kbdataid, String name, String column, String wordclass, String serviceType) throws SQLException {
		int result = 0;
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
		
		// 定义词类id变量
		String wordclassid = "";
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 获取词类id值
			wordclassid = rs.getRows()[0].get("wordclassid").toString();
		}
		// 2.查询属性名称是否重复
		// 定义查询属性名称是否重复的SQL语句
		sql = "select * from serviceattrname2colnum where name=? and abstractid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定业务id参数
		lstpara.add(kbdataid);
		// 执行SQL语句，获取相应的数据源
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return -1;
		} else {
			Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("商家与行业问题库四层结构映射关系配置",serviceType);
			if (rsConfig != null && rsConfig.getRowCount() > 0){
				serviceType = rsConfig.getRows()[0].get("name").toString();
			}
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			// 3.新增属性名称
			// 定义新增属性名称的SQL语句
			sql = "insert into serviceattrname2colnum (serviceattrname2colnumid,name,columnnum,wordclassid,abstractid) values (?,?,?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			String serviceattrname2colnumid = "";
			if(GetConfigValue.isOracle){
				serviceattrname2colnumid = ConstructSerialNum.GetOracleNextValNew("serviceattrname2colnum_seq",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
				serviceattrname2colnumid = ConstructSerialNum.getSerialIDNew("serviceattrname2colnum","serviceattrname2colnumid",bussinessFlag);	
			}
			lstpara.add(serviceattrname2colnumid);
			// 绑定属性名称参数
			lstpara.add(name);
			// 绑定列值参数
			lstpara.add(column);
			// 绑定词类id参数
			lstpara.add(wordclassid);
			// 绑定业务id参数
			lstpara.add(kbdataid);
			// 执行SQL语句，绑定事务，返回事务处理结果
			result = Database.executeNonQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		return result;
	}
	
	/**
	 * 查询属性名称组成field和name,以及对应的属性值
	 * 
	 * @param kbdataid参数 父亲摘要id
	 * @return json串
	 */
	public static Result SelectAttrField(String kbdataid) {
		// 定义属性名称和属性值的map集合
		Map<String, List<String>> attrnamevalueMap = new HashMap<String, List<String>>();

		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义查询属性名称和属性值的SQL语句
		String sql = "select n.name,n.columnnum,w.word from serviceattrname2colnum n left join word w on w.wordclassid=n.wordclassid where w.stdwordid is null and n.abstractid=?";
		
		// 绑定业务id参数
		lstpara.add(kbdataid);
		
		sql += " order by n.columnnum asc,w.wordid desc";
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				// 获取属性名称
				String name = rs.getRows()[i].get("name").toString();
				// 获取属性值(词条)
				Object word = rs.getRows()[i].get("word");
				// 判断属性名称和属性值的map集合是否含有属性名称
				if (attrnamevalueMap.containsKey(name)) {
					// 判断属性值(词条)是否为null
					if (word != null) {
						// 将key为当前属性名称的对应的value的集合中添加属性值(词条)
						attrnamevalueMap.get(name).add(word.toString());
					}
				} else {
					// 定义属性值(词条)集合
					List<String> valueLst = new ArrayList<String>();
					// 判断属性值(词条)是否为null
					if (word != null) {
						// 将属性值(词条)放入属性值(词条)集合中
						valueLst.add(word.toString());
					}
					// 将key和value放入map集合中
					attrnamevalueMap.put(name, valueLst);
				}
			}
		}
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 定义查询属性名称
		sql = "select * from serviceattrname2colnum where abstractid=?";
		
		// 绑定业务id参数
		lstpara.add(kbdataid);
	
		sql += " order by columnnum asc";
		
		
		// 执行SQL语句，获取相应的数据源
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 * 删除属性名称，并删除相关的信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param attrnameid参数属性名称id
	 * @param column参数对应列值
	 * @return 删除返回的json串
	 */
	public static int DeleteAttrName(String kbdataid, String attrnameid,String column) {
		int result = 0;
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
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

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
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 定义删除服务或产品的SQL语句
		sql = "delete from serviceorproductinfo where abstractid=? and attr"
				+ column + " is not null";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(kbdataid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，绑定事务，返回事务处理结果
		result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return result;
	}
	
	/**
	 *@description 判断属性名称对应列值是否存在
	 *@param kbdataid 参数摘要id
	 *@param column 对应列索引
	 *@return 
	 *@returnType boolean 
	 */
	public static Result getAttrNameValue(String kbdataid,String column){
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句定义的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义删除服务或产品的SQL语句
		String sql = "select attr"+column+"  as attrvalue from serviceorproductinfo where abstractid=? and attr"
				+ column + " is not null";
		// 绑定业务id参数
		lstpara.add(kbdataid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，绑定事务，返回事务处理结果
		Result rs  = Database.executeQueryTransaction(lstSql, lstLstpara);
		return rs;
	}
	
	/**
	 * 修改属性名称
	 * 
	 * @param kbdataid参数摘要id
	 * @param attrnameid参数属性名称id
	 * @param name参数属性名称
	 * @return 修改返回的json串
	 * @throws SQLException 
	 */
	public static int ModifyAttrName(String kbdataid, String attrnameid,
			String name) throws SQLException {
		int result = 0;
		// 1.查询属性名称是否重复
		// 定义查询属性名称是否重复的SQL语句
		String sql = "select * from serviceattrname2colnum where name=? and abstractid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定业务id参数
		lstpara.add(kbdataid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return -1;
		} else {
			// 3.更新属性名称
			// 定义更新属性名称的SQL语句
			sql = "update serviceattrname2colnum set name=? where serviceattrname2colnumid=? and abstractid=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定属性名称参数
			lstpara.add(name);
			// 绑定属性名称id参数
			lstpara.add(attrnameid);
			// 绑定业务id参数
			lstpara.add(kbdataid);
			// 执行SQL语句，绑定事务，返回事务处理结果
			result = Database.executeNonQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		return result;
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
	public static int SelectAttrValue(String wordclassid, String name) {
		int result = 0;
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义查询属性值的总条数的SQL语句
		sql.append("select * from word where stdwordid is null and wordclassid=?");
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
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		if (rs != null) {
			result = rs.getRowCount();
		}
		return result;
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
	public static Result SelectAttrValue(String wordclassid, String name, int page, int rows) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义查询属性值的总条数的SQL语句
		sql.append("select * from word where stdwordid is null and wordclassid=?");
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
		sql.append(" order by wordid desc limit ?,?");
		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(rows);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
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
			String wordclass,String serviceType) {
		int result = 0;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		
		// 1.查询属性值是否重复
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
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return -1;
		} else {
			// 2.新增词条
			// 定义新增词条的SQL语句
			sql = "insert into word (wordid,wordclassid,word,type) values(?,?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			String wordid = "";
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			if(GetConfigValue.isOracle){
				wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
				wordid = ConstructSerialNum.getSerialIDNew("word","wordid",bussinessFlag);	
			}
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
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );

			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstSql.add(GetConfigValue.LogSql());
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(GetConfigValue.LogParam(" ", " "," "," ", "增加词条", "上海",wordclass + "==>" + name, "WORD"));
			// 执行SQL语句，绑定事务，返回事务处理结果
			result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		}
		return result;
	}
	
	/**
	 * 修改属性值(词条)
	 * 
	 * @param attrvalueid参数属性值id
	 * @param name参数属性值
	 * @param wordclassid参数词类id
	 * @param column参数列值
	 * @param oldname参数原有的词条
	 * @param kbdataid参数摘要id
	 * @return 修改返回的json串
	 */
	public static int ModifyAttrValue(String attrvalueid, String name,
			String wordclassid, String column, String oldname, String kbdataid) {
		int result = 0;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();

		// 1.查询词条是否含有别名
		// 定义查询别名的SQL语句
		String sql = "select wordid from word where stdwordid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性值(词条)id
		lstpara.add(attrvalueid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return -1;
		} else {
			// 2.查询属性值是否重复
			// 定义查询属性名称是否重复的SQL语句
			sql = "select * from word where stdwordid is null and word=? and wordclassid=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定属性名称参数
			lstpara.add(name);
			// 绑定属性名称id参数
			lstpara.add(wordclassid);
			// 执行SQL语句，获取相应的数据源
			rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				return -2;
			} else {
				// 3.更新词条
				// 定义更新词条的SQL语句
				sql = "update word set word=? where wordid=? ";
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定属性值(词条)参数
				lstpara.add(name);
				// 绑定属性值(词条)id参数
				lstpara.add(attrvalueid);
				// 将SQL语句放入集合中
				lstSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );

				// 定义更新服务或产品的SQL语句
				sql = "update serviceorproductinfo set attr" + column
						+ "=? where attr" + column + "=? and abstractid=?";
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定属性值参数
				lstpara.add(name);
				// 绑定属性值参数
				lstpara.add(oldname);
				// 绑定业务id参数
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
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(GetConfigValue.LogParam(" ", " ", " ", " ", "更新词条", "上海",
						oldname + "==>" + name, "WORD"));
				result = Database.executeNonQueryTransaction(lstSql,lstLstpara);
			}
		}
			return result;
	}
	
	/**
	 * 删除属性值，并删除相关的信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param attrvalueid参数属性值id
	 * @param name参数词条
	 * @param column参数属性名称对应列值
	 * @param wordclass参数词类名称
	 * @return 删除返回的json串
	 */
	public static int DeleteAttrValue(String kbdataid, String attrvalueid,
			String name, String column, String wordclass) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义删除词条的SQL语句
		String sql = "delete from word where wordid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性值id参数
		lstpara.add(attrvalueid);
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
		// 绑定词条id参数
		lstpara.add(attrvalueid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 定义更新服务或产品的SQL语句
		sql = "update serviceorproductinfo set attr" + column
				+ " = null where attr" + column + "=? and abstractid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定属性值参数
		lstpara.add(name);
		// 绑定业务id参数
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
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(GetConfigValue.LogParam(" ", " ", " ", " ", "删除词条", "上海", wordclass
				+ "==>" + name, "WORD"));

		// 执行SQL语句，绑定事务，返回事务处理结果
		int result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return result;
	}
	
	/**
	 * 分页查询服务或产品信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param topic类型
	 * @param selattr列名
	 * @param selattrValue列名及对应的值
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return json串
	 */
	public static int SelectAttr(String fKbdataid, String cKbdataid, String topic,String selattr, String selattrValue) {
		int result = 0;
		String[] attrArr = selattr.split("@");
		String[] attrValueAttr = selattrValue.split("@");
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		if (topic.startsWith("复用-")) {
			// 定义查询服务或产品信息的总条数
			sql.append("select * from serviceorproductinfo where abstractid=? ");
			// 绑定业务id参数
			lstpara.add(fKbdataid);
		} else {
			String beginSql = "select serviceorproductinfoid, ";
			// 循环遍历属性值数组
			for (int i = 0; i < attrArr.length; i++) {
				// 判断attri是否等于0
				if (!"".equals(attrArr[i]) && attrArr[i] != null) {
					if (i<3) {
						if (!topic.startsWith("复用-")) {
							if (i==0) {
								beginSql += "s.service as " + attrArr[i] + ",";
							} else if(i==1){
								beginSql += "k.kbdataid as " + attrArr[i] + ",";
							} else {
								beginSql += "k.abstract as " + attrArr[i] + ",";
							}
						} else {
							beginSql += attrArr[i]+",";
						}
					} else {
						// 加上attri的条件
						beginSql += attrArr[i]+",";
					}
				}
			}
			
			
			beginSql = beginSql.substring(0, beginSql.lastIndexOf(","));
			
			// 定义查询服务或产品信息的总条数
			sql.append("select * from ("+beginSql+" from serviceorproductinfo ser,kbdata k,service s where Ser.Abstractid=? And Ser.Attr6=?"+
						" and k.kbdataid=ser.abstractid and s.serviceid=k.serviceid order by serviceorproductinfoid desc) where 1=1");
			// 绑定业务id参数
			lstpara.add(fKbdataid);
			lstpara.add(cKbdataid);
			
		}
		// 循环遍历属性值数组
		for (int i = 0; i < attrValueAttr.length; i++) {
			// 判断attri是否等于0
			if (!"".equals(attrValueAttr[i]) && attrValueAttr[i] != null) {
				// 加上attri的条件
				sql.append(" and " + attrValueAttr[i].substring(0, attrValueAttr[i].indexOf("~")) + " like ? ");
				// 绑定attri的参数
				lstpara.add("%" + attrValueAttr[i].substring(attrValueAttr[i].indexOf("~")+1,attrValueAttr[i].length()) + "%");
			}
		}
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		if (rs != null) {
			result = rs.getRowCount();
		}	
		return result;
	}
	
	/**
	 * 分页查询服务或产品信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param topic类型
	 * @param selattr列名
	 * @param selattrValue列名及对应的值
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return json串
	 */
	public static Result SelectAttr(String fKbdataid, String cKbdataid, String topic,String selattr, String selattrValue, int page,
			int rows) {
		String[] attrArr = selattr.split("@");
		String[] attrValueAttr = selattrValue.split("@");
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义SQL语句
		sql = new StringBuilder();
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		String beginSql = "select serviceorproductinfoid,status, ";
		// 循环遍历属性值数组
		for (int i = 0; i < attrArr.length; i++) {
			// 判断attri是否等于0
			if (!"".equals(attrArr[i]) && attrArr[i] != null) {
				if (i<3) {
					if (!topic.startsWith("复用-")) {
						if (i==0) {
							beginSql += "s.service as " + attrArr[i] + ",";
						} else if(i==1){
							beginSql += "k.kbdataid as " + attrArr[i] + ",";
						} else {
							beginSql += "k.abstract as " + attrArr[i] + ",";
						}
					} else {
						beginSql += attrArr[i]+",";
					}
				} else {
					// 加上attri的条件
					beginSql += attrArr[i]+",";
				}
			}
		}
		
		
		beginSql = beginSql.substring(0, beginSql.lastIndexOf(","));
		
		if (topic.startsWith("复用-")) {
			// 分页查询满足条件的SQL语句
			sql.append(beginSql+" from serviceorproductinfo where abstractid=? ");
			// 绑定业务id参数
			lstpara.add(fKbdataid);
		} else {
			// 分页查询满足条件的SQL语句
			sql.append(beginSql+" from serviceorproductinfo ser,kbdata k,service s"
					+" Where Ser.Abstractid=? And Ser.Attr6=?"+
					" and k.kbdataid=ser.abstractid and s.serviceid=k.serviceid");
			// 绑定业务id参数
			lstpara.add(fKbdataid);
			lstpara.add(cKbdataid);
		}
		
		// 将SQL语句补充完整
		sql.append(" order by status asc,serviceorproductinfoid desc");
		// 循环遍历属性值数组
		for (int i = 0; i < attrValueAttr.length; i++) {
			// 判断attri是否等于0
			if (!"".equals(attrValueAttr[i]) && attrValueAttr[i] != null) {
				// 加上attri的条件
				sql.append(" and " + attrValueAttr[i].substring(0, attrValueAttr[i].indexOf("~")) + " like ? ");
				// 绑定attri的参数
				lstpara.add("%" + attrValueAttr[i].substring(attrValueAttr[i].indexOf("~")+1,attrValueAttr[i].length()) + "%");
			}
		}
		sql.append(" limit ?,?");
		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(rows);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 * 新增服务或产品信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param attrArr参数属性值数组
	 * @return 新增返回的json串
	 * @throws SQLException 
	 */
	public static int InsertAttr(String kbdataid,String[] attrArr,String serviceType) throws SQLException {
		int result = 0;	
		// 1.查询服务或产品信息是否重复
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义查询服务或产品信息是否重复的SQL语句
		sql.append("select * from serviceorproductinfo where abstractid=? ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(kbdataid);
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
		Result rs = Database
				.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return -1;
		} else {
			// 2.新增服务或产品信息
			// 定义SQL语句
			sql = new StringBuilder();
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 定义新增服务或产品信息的SQL语句
			sql.append("insert into serviceorproductinfo (serviceorproductinfoid,abstractid,");
			// 循环遍历属性值数组
			for (int i = 0; i < attrArr.length; i++) {
				// 加上attr的条件
				sql.append("attr" + (i + 1) + ",");
			}
			// 将SQL语句补充完整
			sql.append("status) values (?,?,?,");
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			String serviceorproductinfoid = "";
			if(GetConfigValue.isOracle){
				serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
				serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
			}
			lstpara.add(serviceorproductinfoid);
			// 绑定业务id参数
			lstpara.add(kbdataid);
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
			lstpara.add("1");
			// 执行SQL语句，绑定事务，返回事务处理
			result = Database.executeNonQuery(sql.toString(), lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		return result;
	}
	
	/**
	 * 删除服务或产品信息
	 * 
	 * @param attrid参数服务或产品信息id
	 * @return 删除返回的json串
	 * @throws SQLException 
	 */
	public static int DeleteAttr(String attrid) throws SQLException {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义删除服务或产品信息的SQL语句
		sql.append("delete from serviceorproductinfo where abstractid is not null and serviceorproductinfoid in (");
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
		int result = Database.executeNonQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return result;
	}
	
	/**
	 * 确认服务或产品信息
	 * 
	 * @param attrid参数服务或产品信息id
	 * @return 确认返回的json串
	 * @throws SQLException 
	 */
	public static int ConfirmAttr(String attrid) throws SQLException {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义确认服务或产品信息的SQL语句
		sql.append("update serviceorproductinfo set status=1 where serviceorproductinfoid in (");
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
		int result = Database.executeNonQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return result;
	}
	
	/**
	 * 更新服务或产品信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param attrArr参数属性值数组
	 * @param attrid参数服务或产品信息id
	 * @return 更新返回的json串
	 * @throws SQLException 
	 */
	public static int UpdateAttr(String kbdataid, String[] attrArr,
			String attrid) throws SQLException {
		int result = 0;
		// 1.查询服务或产品信息是否重复
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义查询服务或产品信息是否重复的SQL语句
		sql.append("select * from serviceorproductinfo where abstractid=? ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(kbdataid);
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
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return -1;
		}else if(attrArr.length<8){
			return -2;
		} else {
			// 2.新增服务或产品信息
			// 定义SQL语句
			sql = new StringBuilder();
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 定义新增服务或产品信息的SQL语句
			sql.append("update serviceorproductinfo set ");
			// 循环遍历属性值数组
			for (int i = 7; i < attrArr.length; i++) {
				String[] attr = attrArr[i].split("~");
				// 加上attr条件
				sql.append(attr[0]+"=?,");
				// 判断attri是否等于0
				if (!attr[1].equals("null")) {
					// 绑定attri的参数
					lstpara.add(attr[1]);
				} else {
					lstpara.add(null);
				}
			}
			// 将SQL语句补充完整
			sql.append("status=? where serviceorproductinfoid=?");
			// 绑定状态参数
			lstpara.add("1");
			// 绑定服务或产品信息id参数
			lstpara.add(attrid);
			// 执行SQL语句，绑定事务，返回事务处理
			result = Database.executeNonQuery(sql.toString(), lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
		}
		return result;
	}
	
	/**
	 * 全量删除服务或产品信息
	 * 
	 * @param kbdataid参数 儿子摘要id+父亲摘要id
	 * @param topic 类型
	 * @return 全量删除返回的json串
	 * @throws SQLException 
	 */
	public static int DeleteAllAttr(String kbdataid, String topic) throws SQLException {
		// 儿子摘要id
		String cKbdataid = kbdataid.split("@")[0];
		// 父亲摘要id
		String fKbdataid = kbdataid.split("@")[1];

		// 定义SQL语句
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义删除服务或产品信息的SQL语句
		String sql = "";
		if (topic.startsWith("复用-")) {
			sql = "delete from serviceorproductinfo where abstractid=?";
			// 绑定业务id参数
			lstpara.add(fKbdataid);
		} else {
			sql = "delete from serviceorproductinfo where abstractid=? and attr6=?";
			// 绑定业务id参数
			lstpara.add(fKbdataid);
			lstpara.add(cKbdataid);
		}
		int result = Database.executeNonQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return result;
	}
	
	/**
	 * 全量确认服务或产品信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param 资源类型
	 * @return 全量确认返回的json串
	 * @throws SQLException 
	 */
	public static int ConfirmAllAttr(String kbdataid,String type,String industry) throws SQLException {
		int result = 0;
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义确认服务或产品信息的SQL语句
		String sql = "";
		if (type.equals("kbdata")) {
			sql = "update serviceorproductinfo set status=1 where abstractid=?";
			// 绑定业务id参数
			lstpara.add(kbdataid);
		} else if(type.equals("service")) {
			sql = "update serviceorproductinfo set status=1 where attr7=? and abstractid is not null";
			lstpara.add(industry);
		}
		result = Database.executeNonQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return result;
	}
	
	/**
	 * 新增服务或产品信息
	 * 
	 * @param attrinfo参数导入文件的内容
	 * @param kbdataid参数债哟id
	 * @return 返回事务处理结果
	 */
	public static int InsertServiceOrProductInfo(List<List<Object>> attrinfo, String kbdataid, String serviceType) {
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
		int count = 0;
		// 判断导入内容的集合的个数是否大于0
		if (attrinfo.size() > 0) {
			// 定义SQL语句
			sql = new StringBuilder();
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 定义获取属性名称对应的列值的SQL语句
			sql.append("select * from serviceattrname2colnum where abstractid=? and name in (");
			// 绑定业务id参数
			lstpara.add(kbdataid);
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
			Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );

			String[] columnArr = new String[attrnameLst.size()];
			// 定义存放属性名称的下标和对应列值的map集合
			Map<String, Integer> attrnamecolumnMap = new HashMap<String, Integer>();
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 循环遍历数据源
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 获取属性名称
					String attrname = rs.getRows()[i].get("name")
							.toString();
					// 获取列值
					String column = rs.getRows()[i].get("columnnum")
							.toString();
					// 循环遍历属性名称数组
					for (int j = 0; j < attrnameLst.size(); j++) {
						// 判断属性名称与第j个集合的值是否相等
						if (attrname.equals(attrnameLst.get(j))) {
							// 将对应的下标和列值放入map集合中
							attrnamecolumnMap.put(column, j);
							// 将列值数组中填充上列值
							columnArr[j] = column;
						}
					}
				}
			} else {
				// 没有查询到对应的列值，就直接将列值从1开始计数
				for (int i = 0; i < columnArr.length; i++) {
					columnArr[i] = String.valueOf(i + 1);
				}
			}

			// 循环遍历服务或产品信息的集合
			for (int m = 1; m < attrinfo.size(); m++) {
				// 定义SQL语句
				sql = new StringBuilder();
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 定义新增服务或产品信息的SQL语句
				sql.append("insert into serviceorproductinfo (serviceorproductinfoid,");
				// 循环遍历列值数组
				for (int i = 0; i < columnArr.length; i++) {
					// 判断第i个列值数组的值是否为null，空
					if (columnArr[i] != null && !"".equals(columnArr[i])) {
						// 加上attr的条件
						sql.append("attr" + columnArr[i] + ",");
					}
				}
				// 将新增的SQL语句补充完整
				sql.append("abstractid,status) values (");
				String serviceorproductinfoid = "";
				String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
				if(GetConfigValue.isOracle){
					serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
				}else if(GetConfigValue.isMySQL){
					serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
				}
				sql.append(serviceorproductinfoid);
				sql.append(",");
				// 循环遍历列值数组
				for (int i = 0; i < columnArr.length; i++) {
					// 判断第i个列值数组的值是否为null，空
					if (columnArr[i] != null && !"".equals(columnArr[i])) {
						// 加上attr的绑定变量
						sql.append("?,");
						// 通过列值数组的第i个值为key，通过map集合获取对应的集合下标，通过下标得到新增时绑定attr的值
						lstpara.add(attrinfo.get(m).get(attrnamecolumnMap.get(columnArr[i])));
					}
				}
				// 将新增的SQL语句补充完整
				sql.append("?,?,?)");
				// 绑定业务id参数
				lstpara.add(kbdataid);
				// 绑定状态参数
				lstpara.add("1");
				// 将SQL语句放入集合中
				lstSql.add(sql.toString());
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
			}
			count = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		}
		return count;
	}
	
	/**
	 * 读取数据库，生成Excel文件，返回文件的路径
	 * 
	 * @param serviceid参数业务id
	 * @return 生成文件的路径
	 */
	public static List<List<String>> ExportExcel(String serviceid) {
		// 定义存放生成Excel文件的每一行内容的集合
		List<String> rowList = new ArrayList<String>();
		// 定义存放生成Excel文件的所有内容的集合
		List<List<String>> attrinfoList = new ArrayList<List<String>>();
		
		// 定义存放属性名称对应列值的数组
		String[] columnArr = null;
		// 定义查询属性名称的SQL语句
		String sql = "select * from serviceattrname2colnum where serviceid=? order by columnnum asc";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null，且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 设置列值数组的长度与查询出来属性名称对应列值的数量一致
			columnArr = new String[rs.getRowCount()];
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				// 将属性名称放入Excel文件的第一行内容的集合中
				rowList.add(rs.getRows()[i].get("name").toString());
				// 给列值数组赋值
				columnArr[i] = rs.getRows()[i].get("columnnum").toString();
			}
		}
		// 将每一行的内容就会放入所有内容的集合中
		attrinfoList.add(rowList);

		// 查询服务或产品信息的SQL语句
		sql = "select * from serviceorproductinfo where serviceid=? ";
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 执行SQL语句，获取相应的数据源
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				// 定义存放生成Excel文件的每一行内容的集合
				rowList = new ArrayList<String>();
				// 循环遍历列值数组
				for (int j = 0; j < columnArr.length; j++) {
					// 获取attr的值
					String attr = rs.getRows()[i]
							.get("attr" + columnArr[j]) != null ? rs
							.getRows()[i].get("attr" + columnArr[j])
							.toString() : "";
					// 将attr的值放入行内容的集合中
					rowList.add(attr);
				}
				// 将行内容的集合放入全内容集合中
				attrinfoList.add(rowList);
			}
		}
	
		return attrinfoList;
	}
	
	/**
	 * 根据服务或产品信息表更新属性值
	 * 
	 * @param kbdataid参数摘要id
	 * @return 更新是否成功
	 */
	public static int UpdateAttrValue(String kbdataid,String serviceType) {
		int result = 0;
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义列数和属性名称id的集合
		List<String> lstColWordclassid = new ArrayList<String>();
		// 定义存放业务下的所有属性名称的列值和属性名称id组合的集合
		List<List<String>> lstLstColWordclassid = new ArrayList<List<String>>();
		// 定义存放属性值表中的属性值的set集合
		Set<String> oldAttrValue = new HashSet<String>();
		// 定义存放服务或产品信息表中attr中的对应的属性值的set集合
		Set<String> newAttrValue = new HashSet<String>();
		// 查询属性名称跟词类表关联的SQL语句
		String sql = "select c.columnnum,c.wordclassid from serviceattrname2colnum c,wordclass w where c.wordclassid=w.wordclassid and c.abstractid=? order by c.columnnum asc";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务id参数
		lstpara.add(kbdataid);
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				// 定义列数和属性名称id的集合
				lstColWordclassid = new ArrayList<String>();
				// 将列值放入集合中
				lstColWordclassid.add(rs.getRows()[i].get("columnnum")
						.toString());
				// 将词类id放入集合中
				lstColWordclassid
						.add(rs.getRows()[i].get("wordclassid") != null ? rs
								.getRows()[i].get("wordclassid").toString()
								: "");
				lstLstColWordclassid.add(lstColWordclassid);
			}
		}
		// 循环遍历每一列的值
		for (int i = 0; i < lstLstColWordclassid.size(); i++) {
			// 定义存放属性值表中的属性值的set集合
			oldAttrValue = new HashSet<String>();
			// 定义存放服务或产品信息表中attr中的对应的属性值的set集合
			newAttrValue = new HashSet<String>();
			// 获取词类名称id
			String wordclassid = lstLstColWordclassid.get(i).get(1);
			// 获取属性名称对应的列值
			String column = lstLstColWordclassid.get(i).get(0);
			// 判断词类id是否为null，空
			if (wordclassid != null && !"".equals(wordclassid)) {
				// 先查询属性名称对应的所有属性值(词条)
				sql = "select * from word where stdwordid is null and wordclassid=?";
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定词类名称id
				lstpara.add(wordclassid);
				// 执行SQL语句，获取相应的数据源
				rs = Database.executeQuery(sql, lstpara.toArray());
				// 判断数据源被null且含有数据
				if (rs != null && rs.getRowCount() > 0) {
					// 循环遍历数据源
					for (int m = 0; m < rs.getRowCount(); m++) {
						// 将属性值放入旧的set集合中
						oldAttrValue.add(rs.getRows()[m].get("word")
								.toString());
					}
				}

				// 查询在服务或产品信息表中每一个attr对应的属性值的SQL语句
				sql = "select distinct attr"
						+ column
						+ " attr from serviceorproductinfo where abstractid=? and attr"
						+ column + " is not null";
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定业务id参数
				lstpara.add(kbdataid);
				// 执行SQL语句，获取相应的数据源
				rs = Database.executeQuery(sql, lstpara.toArray());
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
				
				// 判断数据源不为null且含有数据
				if (rs != null && rs.getRowCount() > 0) {
					// 循环遍历数据源
					for (int n = 0; n < rs.getRowCount(); n++) {
						// 获取属性值
						Object attr = rs.getRows()[n].get("attr");
						// 判断属性值是否为null
						if (attr != null) {
							// 将属性值放入新的set集合中
							newAttrValue.add(attr.toString());
						}
					}
				}
				// 判断服务或产品信息表中attr中的对应的属性值的set集合的公司是否大于0
				if (newAttrValue.size() > 0) {
					// 将服务或产品信息表中attr中的对应的属性值的set集合中去除掉属性值表中的属性值的set集合中的值
					// 就是去掉已存在数据库中的属性值
					newAttrValue.removeAll(oldAttrValue);
				}
				// 循环遍历去掉已存在的set集合
				for (String value : newAttrValue) {
					// 新增属性值的SQL语句
					sql = "insert into word(wordid,wordclassid,word,type) values(?,?,?,?)";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					String wordid = "";
					String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
					// 绑定id参数
					if(GetConfigValue.isOracle){
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id",bussinessFlag);	
					}else if(GetConfigValue.isMySQL){
						wordid = ConstructSerialNum.getSerialIDNew("word","wordid",bussinessFlag);	
					}
					lstpara.add(wordid);
					// 绑定词类id
					lstpara.add(wordclassid);
					// 绑定属性值名称
					lstpara.add(value);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( sql + "#" + lstpara );
					
				}
			}	
		}
		if (lstSql.size() > 0) {
			result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		} else {
			return -1;
		}
		return result;
	}
	
	/**
	 * 构造全表查询所涉及的列
	 * @param serviceid 业务id
	 * @return
	 */
	public static Result constructColumn(String industry) { 
		// 根据业务id获得所有子业务id以及摘要
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "Select max(count(abstractid)) as maxnum From Serviceattrname2colnum Where Abstractid In ("+
			  	  "select abstractid from Serviceorproductinfo where attr7='" + industry + "'" +
			      " and abstractid is not null) group by abstractid";
		} else {
			sql = "select max(a.maxnum) as maxnum from ("+
				  "Select count(abstractid) as maxnum From Serviceattrname2colnum Where Abstractid In ("+
		  	      "select abstractid from Serviceorproductinfo where attr7='" + industry + "'" +
		          " and abstractid is not null) group by abstractid) a";
		}
		
		Result rs = Database.executeQuery(sql);
		
		
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}
	
	
	public static Map<Object,Result> constructAllKbdataAttr2(String kbdataid){
		Map<Object,Result> map = new HashMap<Object,Result>();
		String sql = "";
		sql = "Select S.Serviceid,S.Service,K.Kbdataid,K.Abstract From Service S,Kbdata K Where S.Serviceid=K.Serviceid and K.Kbdataid=" + kbdataid;
		//文件日志
		GlobalValue.myLog.info( sql );
		Result rs = Database.executeQuery(sql);
		for (int i = 0; i < rs.getRowCount();i++){
			map.put(rs.getRows()[i].get("kbdataid"),rs);
		}
		return map;
	}
	
	/**
	 * 构造查询所有摘要的配置所涉及的数据
	 * @param column 要查询的列数
	 * @param page 页码
	 * @param rows 每页多少条数据
	 * @param service 业务
	 * @param sabstract 问题库摘要
	 * @param cabstract 儿子摘要
	 * @param time 插入时间
	 * @return
	 * @throws SQLException 
	 */
	public static Map<Integer,Result> constructAllKbdataAttr(String column,int page,int rows,
			String service,String sabstract,String cabstract,List<String> city,String industry,String serviceX,String serviceRoot,List<String> extendCity) throws SQLException {
		Map<Integer,Result> map = new HashMap<Integer, Result>();
		List<String> serviceRoots = new ArrayList<String>(Arrays.asList(serviceRoot.split(",")));
		// 去除个性化业务
		serviceRoots.remove("'个性化业务'");
		// 去除行业问题看
		serviceRoots.remove("'"+industry.split("->")[0]+"问题库'");
		String sql = "";
		// 遍历column得到要查询的列
		String str = "";
		int columnNum = Integer.parseInt(column);
		for (int i = 1; i <= 20; i++) {
			if (i == 5){
				str += "abstract attr" + i + ",";
			} else {
				str += "attr" + i + ",";
			}
		}
		str = str.substring(0, str.lastIndexOf(","));
		// 电信行业问题库
		String innerSql = "";
		if(GetConfigValue.isOracle){
			innerSql ="select serviceorproductinfoid,abstractid,status,To_char(inserttime,'yyyy-mm-dd') as inserttime,inserttime as inserttime2,getCity(attr6) as city,"+ str +" From Serviceorproductinfo ser,kbdata k"+
			  	" Where ser.attr6=k.kbdataid and attr7='"+industry+"' and ser.abstractid is not null";
		}else{
			innerSql = "select serviceorproductinfoid,abstractid,status,DATE_FORMAT(inserttime,'%Y-%m-%d') as inserttime,inserttime as inserttime2,getCity(attr6) as city,"+ str +" From Serviceorproductinfo ser,kbdata k"+
				" Where ser.attr6=k.kbdataid and attr7='"+industry+"' and ser.abstractid is not null";
		}
		// 除电信行业问题库下的摘要--商家摘要
		if(cabstract != null && !cabstract.equals("")) {
			sql = constructChildAbstract(serviceRoots,cabstract,industry,columnNum);
			innerSql = "select * from (select * from (" 
				+ innerSql 
				+ " and attr5 like '%" 
				+ cabstract 
				+ "%' order by inserttime2 desc,serviceorproductinfoid ) aaa union all " 
				+ sql
				+") cc where 1=1 ";
		} 
		// 问题库业务
		if(service != null && !service.equals("")) {
			if(GetConfigValue.isOracle){
				innerSql += " and abstractid in (select kbdataid || '' from kbdata where serviceid in (select serviceid from service where service like '%"+service+"%' and brand like '"+industry.split("->")[0]+"问题库'))";
			}else{
				String tempSql = "";
				Result tempRs = null;
				List<String> abstractids = new ArrayList<String>();
				tempSql = "SELECT CAST(kbdataid AS VARCHAR(40)) AS abstractid FROM kbdata,service WHERE service.serviceid=kbdata.serviceid and service LIKE '%" 
					+ service 
					+ "%' AND brand='" 
					+industry.split("->")[0]
					+ "问题库'";
				tempRs = Database.executeQuery(tempSql);
				if (tempRs != null){
					for (int i = 0; i < tempRs.getRowCount(); i++) {
						Object obj = tempRs.getRows()[i].get("abstractid");
						if (obj != null) {
							abstractids.add(obj.toString());
						}
					}
					innerSql = "select * from (" + innerSql + ") ee where abstractid in (";
					for (String abstractid : abstractids){
						innerSql += "'" + abstractid + "',";
					}
					innerSql = innerSql.substring(0, innerSql.lastIndexOf(",")) + ")";
				}
			}
		} 
		// 问题库摘要
		if (sabstract != null && !sabstract.equals("")) {
			if(GetConfigValue.isOracle){
				innerSql += " and abstractid in (select kbdataid || '' from kbdata where serviceid in (select serviceid from service where service like '%"+service+"%' and brand like '"+industry.split("->")[0]+"问题库')"
//				+ " and topic like '复用-%'"
				+ " and abstract like '%>%"+sabstract+"%')";
			}else{
				String tempSql = "";
				Result tempRs = null;
				List<String> abstractids = new ArrayList<String>();
				tempSql = "SELECT CAST(kbdataid AS VARCHAR(40)) AS abstractid FROM kbdata,service WHERE service.serviceid=kbdata.serviceid "
//					+ "and topic LIKE '复用-%' "
					+ "AND brand='" 
					+industry.split("->")[0]
					+ "问题库'"
					+ "and abstract like '%>%"
					+ sabstract
					+ "%'";
				tempRs = Database.executeQuery(tempSql);
				if (tempRs != null){
					for (int i = 0; i < tempRs.getRowCount(); i++) {
						Object obj = tempRs.getRows()[i].get("abstractid");
						if (obj != null) {
							abstractids.add(obj.toString());
						}
					}
					innerSql = "select * from (" + innerSql + ") dd where abstractid in (";
					for (String abstractid : abstractids){
						innerSql += "'" + abstractid + "',";
					}
					innerSql = innerSql.substring(0, innerSql.lastIndexOf(",")) + ")";
				}
			}
		} 
		// 商家摘要地市
		if(city != null && city.size()>0) {
			innerSql = "select * from (" + innerSql + ") a where city like '%" + city.get(0) + "%'";
			for(int i=1;i<city.size();i++){
				innerSql+=" or city like '%"+city.get(i)+"%'";
			}
		}
		//继承地市
		if(extendCity != null && extendCity.size()>0) {
			innerSql = "select * from (" + innerSql + ") a where attr15 like '%" + extendCity.get(0) + "%'";
			for(int i=1;i<extendCity.size();i++){
				innerSql+=" or attr15 like '%"+extendCity.get(i)+"%'";
			}
		}
		// 业务X
		if (serviceX != null && !serviceX.equals("")){
			innerSql = "select * from (" + innerSql + ") b where attr8 like '%" + serviceX + "%'";
		}
		sql = innerSql;
		
		if(cabstract == null || cabstract.equals("")) {
			sql += " order by inserttime2 desc,serviceorproductinfoid ";
		}
		System.out.println("search sql------------> :"+sql);
		Result rs = Database.executeQuery(sql);
		int result = 0;
		if (rs == null || rs.getRowCount() == 0) {
			map.put(0, null);
		} else {
			result = rs.getRowCount();
			if (GetConfigValue.isOracle) {
				sql = "select * from (select t.*, rownum  rn from ("+ sql +") t where rownum<=? ) where rn>? ";
				List<Object> lstpara = new ArrayList<Object>();
				lstpara.add(page * rows);
				lstpara.add((page - 1) * rows);
				System.out.println("========="+sql);
				rs = Database.executeQuery(sql, lstpara.toArray());
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
			} else if (GetConfigValue.isMySQL) {
				sql += " limit ?,?";
				List<Integer> lstpara = new ArrayList<Integer>();
				lstpara.add((page-1)*rows);
				lstpara.add(rows);
				rs = Database.executeQuery(sql, lstpara.toArray());
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
			}
			map.put(result, rs);
		}
		return map;
	}
	
	/**
	 * 根据地市构造问题库和非问题库下的数据
	 * @param serviceRoots
	 * @param city
	 * @param industry
	 * @param columnNum
	 * @return
	 */
	private static String constructCityAbstract(List<String> serviceRoots,String city,String industry,int columnNum) {
		// 查询根业务下的所有摘要
		String sql = "select kbdataid from kbdata where serviceid in (select serviceid from service where brand in (";
		for (String serviceRoot : serviceRoots) {
			sql += serviceRoot + ",";
		}
		sql = sql.substring(0, sql.lastIndexOf(","))+")) and city like '%"+city+"%'";
		sql += " and kbdataid not in (select kbdataid from kbdata where kbdataid in (select * from (select attr6 from Serviceorproductinfo where attr7='"+industry+"' and abstractid is not null)as att))";
		String s = "";
		for (int i = 1; i <= 20; i++) {	
			if(i==4) {
				s += "s.service as attr" + i + ",";
			} else if(i==5) {
				s += "k.abstract as attr" + i + ",";
			} else if(i==6) {
				if(GetConfigValue.isOracle){
					s += "k.kbdataid || '' as attr" + i + ",";
				}else{
					s += "cast(k.kbdataid as varchar(40)) as attr" + i + ",";
				}
			} else if(i==7) {
				s += "'"+industry+"' as attr" + i + ",";
			} else {
				s += "'' as attr" + i + ",";
			}
		}
		s = s.substring(0, s.lastIndexOf(","));
		sql = "select null as serviceorproductinfoid,'' as abstractid,null as status,'' as inserttime,null as inserttime2,null as city," + s + " from service s,kbdata k where s.serviceid=k.serviceid and "+
			  "kbdataid in ("+sql+")";
		return sql;
			
	}
	
	private static String constructFatherAbstract(String sabstract,String industry,int columnNum) {
		String sql = "";
		Result rs = null;
		// 根据父亲摘要名查询所有的摘要
		List<String> kbdataids = new ArrayList<String>();
		sql = "select kbdataid from kbdata where serviceid in (select serviceid from service where brand like '"+industry.split("->")[0]+"问题库') " 
//		+ "and topic like '复用-%'" 
		+ " and abstract like '%"+sabstract+"%'";
		try {
			rs = Database.executeQuery(sql);
			if (rs != null) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					Object obj = rs.getRows()[i].get("kbdataid");
					if (obj != null) {
						kbdataids.add(obj.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 查询已继承的摘要
		sql = "select abstractid from Serviceorproductinfo where abstractid in(";
		for (String kbdataid : kbdataids) {
			sql += "'"+kbdataid+"',";
		}
		sql = sql.substring(0, sql.lastIndexOf(",")) + ") and attr7 like '"+industry+"'";
		List<String> readyKbdataids = new ArrayList<String>();
		try {
			rs = Database.executeQuery(sql);
			if (rs != null) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					Object obj = rs.getRows()[i].get("abstractid");
					if (obj != null) {
						readyKbdataids.add(obj.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 将已继承的数据移除
		kbdataids.removeAll(readyKbdataids);
		
		if (!kbdataids.isEmpty()) {// 如果摘要id不为空
			String s = "";
			for (int i = 1; i <= 20; i++) {	
				if(i==1) {
					s += "s.service as attr" + i + ",";
				} else if(i==2) {
					if (GetConfigValue.isOracle) {
						s += "k.kbdataid || '' as attr" + i + ",";
					} else {
						s += "Cast(k.kbdataid as varchar(40)) as attr" + i + ",";
					}
				} else if(i==3) {
					s += "k.abstract as attr" + i + ",";
				} else if(i==7) {
					s += "'"+industry+"' as attr" + i + ",";
				} else {
					s += "'' as attr" + i + ",";
				}
			}
			s = s.substring(0, s.lastIndexOf(","));
			if (GetConfigValue.isOracle){
				sql = "select null as serviceorproductinfoid,k.kbdataid || '' as abstractid,null as status,'' as inserttime,null as inserttime2,null as city," + s + " from service s,kbdata k where s.serviceid=k.serviceid and (k.kbdataid in (";
			}else{
				sql = "select null as serviceorproductinfoid,cast(k.kbdataid as varchar(40)) as abstractid,null as status,'' as inserttime,null as inserttime2,null as city," + s + " from service s,kbdata k where s.serviceid=k.serviceid and (k.kbdataid in (";
			}
			int len = kbdataids.size();
			for (int i=0; i<len; i++) {
				if ((i+1)%1000==0) {// 满足一千个
					sql = sql.substring(0, sql.lastIndexOf(","));
					sql += ") or k.kbdataid in (";
				} else {
					sql += kbdataids.get(i) + ",";
				}
			}
			if (sql.endsWith(",")) {
				sql = sql.substring(0, sql.lastIndexOf(",")) + "))";
			} else {
				sql = sql.substring(0, sql.lastIndexOf("or")) + "))";	
			}
		} else {
			sql = "";
		}
		return sql;
	}
	
	/**
	 * 根据业务名构造问题库下的数据
	 * @param service
	 * @param industry
	 * @param columnNum
	 * @return
	 */
	private static String constructFatherService(String service,String industry,int columnNum) {
		String sql = "";
		Result rs = null;
		// 根据业务名查询所有的摘要id
		List<String> kbdataids = new ArrayList<String>();
		sql = "select kbdataid from kbdata where serviceid in (select serviceid from service where service like '%"+service+"%' and brand like '"+industry.split("->")[0]+"问题库')";
		try {
			rs = Database.executeQuery(sql);
			if (rs != null) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					Object obj = rs.getRows()[i].get("kbdataid");
					if (obj != null) {
						kbdataids.add(obj.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 查询已继承的摘要
		sql = "select abstractid from Serviceorproductinfo where abstractid in(";
		for (String kbdataid : kbdataids) {
			sql += "'"+kbdataid+"',";
		}
		sql = sql.substring(0, sql.lastIndexOf(",")) + ") and attr7 like '"+industry+"'";
		List<String> readyKbdataids = new ArrayList<String>();
		try {
			rs = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
			if (rs != null) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					Object obj = rs.getRows()[i].get("abstractid");
					if (obj != null) {
						readyKbdataids.add(obj.toString());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// 将已继承的数据移除
		kbdataids.removeAll(readyKbdataids);
		if (!kbdataids.isEmpty()) {// 如果摘要id不为空
			String s = "";
			for (int i = 1; i <= 20; i++) {	
				if(i==1) {
					s += "s.service as attr" + i + ",";
				} else if(i==2) {
					if (GetConfigValue.isOracle) {
						s += "k.kbdataid || '' as attr" + i + ",";
					} else {
						s += "Cast(k.kbdataid as varchar(40)) as attr" + i + ",";
					}
				} else if(i==3) {
					s += "k.abstract as attr" + i + ",";
				} else if(i==7) {
					s += "'"+industry+"' as attr" + i + ",";
				} else {
					s += "'' as attr" + i + ",";
				}
			}
			s = s.substring(0, s.lastIndexOf(","));
			if(GetConfigValue.isOracle){
				sql = "select null as serviceorproductinfoid,k.kbdataid || '' as abstractid,null as status,'' as inserttime,null as inserttime2,null as city," + s + " from service s,kbdata k where s.serviceid=k.serviceid and (k.kbdataid in(";
			}else{
				sql = "select null as serviceorproductinfoid,cast(k.kbdataid as varchar(40)) as abstractid,null as status,'' as inserttime,null as inserttime2,null as city," + s + " from service s,kbdata k where s.serviceid=k.serviceid and (k.kbdataid in(";
			}
			int len = kbdataids.size();
			for (int i = 0; i < len; i++){
				if ((i+1)%1000==0) {// 满足一千个
					sql = sql.substring(0, sql.lastIndexOf(","));
					sql += ") or k.kbdataid in (";
				} else {
					sql += kbdataids.get(i) + ",";
				}
			}
			if (sql.endsWith(",")) {
				sql = sql.substring(0, sql.lastIndexOf(",")) + "))";
			} else {
				sql = sql.substring(0, sql.lastIndexOf("or")) + "))";	
			}
		} else {
			sql = "";
		}
		return sql;
	}
	
	/**
	 * 根据儿子摘要构造问题库和非问题库下的数据
	 * @param serviceRoots
	 * @param _abstract
	 * @param industry
	 * @param columnNum
	 * @return
	 */
	private static String constructChildAbstract(List<String> serviceRoots,String _abstract,String industry,int columnNum) {
		String sql = "SELECT  NULL AS serviceorproductinfoid,'' AS abstractid,NULL AS STATUS,'' AS inserttime,null as inserttime2,k.city AS city,'' AS attr1,'' AS attr2,'' AS attr3,s.service AS attr4,k.abstract AS attr5,cast(k.kbdataid as varchar(50)) AS attr6,'" 
			+ industry 
			+ "' AS attr7, ";
//			+ "' AS attr7,'' AS attr8,'' AS attr9,'' AS attr10,'' AS attr11,'' AS attr12,'' AS attr13,'' AS attr14,'' AS attr15,'' AS attr16,'' As attr17, '' As attr18  FROM  service s , kbdata k WHERE s.serviceid=k.serviceid AND s.brand IN(";
		for (int i=8 ; i<=20 ; i++){
			sql = sql +  " '' AS attr" + i +",";
		}
		sql = sql.substring(0, sql.lastIndexOf(","));
		sql += " FROM  service s , kbdata k WHERE s.serviceid=k.serviceid AND s.brand IN('";
		
		for (String serviceRoot : serviceRoots) {
			sql += serviceRoot.replace("\'", "") + "','";
		}
		sql = sql.substring(0, sql.lastIndexOf(","))+") AND k.abstract LIKE '%" 
			+ _abstract 
			+ "%' AND  k.flag =0 AND k.abstract not like '%(删除标识符近类)'";
		System.out.println("------sql:" + sql);
		return sql;
	}
	
	/**
	 *@description 通过业务ID查找，
	 *@param serviceid
	 *            业务ID
	 *@param topic  知识类别         
	 *@return
	 *@returnType Result
	 */
	public static Result getAbstractByServiceidAndTopic(String serviceid,String topic) {
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "select * from  kbdata where abstract is not null and serviceid=? and topic =? ";
		lstpara = new ArrayList<Object>();
		lstpara.add(serviceid);
		lstpara.add(topic);
		Result rs = null;
		
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 * 获取父亲摘要id
	 * 
	 * @param kbdataid
	 *            传入的摘要id
	 * @param topic
	 *            类型
	 * @return
	 */
	public static Result getFatherKbdataid(String kbdataid) {
		String sql = "select abstractid from ServiceOrProductInfo where attr6='"
			+ kbdataid + "'";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *@description 通过列名及列值获得列与元素对应数据源
	 *@param id
	 *@param colum
	 *@return
	 *@returnType Result
	 */
	public static Result getServiceattrname2colnum(String colum,
			String columValue) {
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "select * from  Serviceattrname2colnum  where " + colum
				+ " =?";
		lstpara = new ArrayList<Object>();
		lstpara.add(columValue);
		Result rs = null;
		
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 *@description 获得继承摘要数据源
	 *@param id
	 *@return 
	 *@returnType Result 
	 */
	public static Result getServiceOrproductInfo(String id) {
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "select * from  SERVICEORPRODUCTINFO  where  attr6 =? ";
		lstpara = new ArrayList<Object>();
		lstpara.add(id);
		Result rs = null;
		
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 *@description 插入继承摘要相关信息
	 *@param abstractId
	 *            当前继承摘要ID
	 *@param fatherAbstractID
	 *            被继承摘要ID
	 *@param sonService
	 *            儿子业务
	 *@param sonAbstract
	 *            儿子摘要
	 *@return
	 *@returnType boolean
	 */
	public static int insertAttr(String currAbstractId,
			String fatherAbstractID, String sonService, String sonAbstract,String serviceType) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义SQL语句
		String sql = "";
		Result rs = getServiceOrproductInfo(currAbstractId);
		if (rs != null && rs.getRowCount() > 0) {// 继承过
			// 循环遍历数据源
			for (int n = 0; n < rs.getRowCount(); n++) {
				String abstractid = rs.getRows()[n].get("abstractid")
						.toString();
				Object status = rs.getRows()[n].get("status");
				if(status == null){
				   status = "1";
				}else{
					status = status.toString();
				}
				if ("0".equals(status)) {// 继承过，但未审核，直接删除，重新继承
					// 删除
					sql = " delete from SERVICEORPRODUCTINFO  where attr6 =? ";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定z摘要id参数
					lstpara.add(currAbstractId);
					// 将SQL语句放入集合中
					lstSql.add(sql.toString());
					// 将定义的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( sql + "#" + lstpara );

					// 新增
					sql = " insert into serviceorproductinfo (serviceorproductinfoid,abstractid,attr4,attr5,attr6,attr7,status) values(?,?,?,?,?,?,?) ";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					String serviceorproductinfoid = "";
					String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
					if(GetConfigValue.isOracle){
						serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
					}else if(GetConfigValue.isMySQL){
						serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
					}
					lstpara.add(serviceorproductinfoid);
					// 绑定业务id参数
					lstpara.add(fatherAbstractID);
					// 绑定儿子业务参数
					lstpara.add(sonService);
					// 绑定儿子摘要参数
					lstpara.add(sonAbstract);
					// 绑定摘要id参数
					lstpara.add(currAbstractId);
					// 绑定四层参数
					lstpara.add(serviceType);
					// 绑定处理状态参数
					lstpara.add(1);

					// 将SQL语句放入集合中
					lstSql.add(sql.toString());
					// 将定义的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( sql + "#" + lstpara );

				} else {
					if (!NewEquals.equals(abstractid,fatherAbstractID)) {// 继承且审核过，但摘要ID不相等，说明存在多继承，直接插入继承信息
						// 新增
						sql = " insert into serviceorproductinfo (serviceorproductinfoid,abstractid,attr4,attr5,attr6,attr7,status) values(?,?,?,?,?,?,?) ";
						// 定义绑定参数集合
						lstpara = new ArrayList<Object>();
						String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
						String serviceorproductinfoid = "";
						if(GetConfigValue.isOracle){
							serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
						}else if(GetConfigValue.isMySQL){
							serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
						}
						lstpara.add(serviceorproductinfoid);
						// 绑定业务id参数
						lstpara.add(fatherAbstractID);
						// 绑定儿子业务参数
						lstpara.add(sonService);
						// 绑定儿子摘要参数
						lstpara.add(sonAbstract);
						// 绑定摘要id参数
						lstpara.add(currAbstractId);
						// 绑定四层参数
						lstpara.add(serviceType);
						// 绑定处理状态参数
						lstpara.add(1);
						// 将SQL语句放入集合中
						lstSql.add(sql.toString());
						// 将定义的绑定参数集合放入集合中
						lstLstpara.add(lstpara);
						
						//文件日志
						GlobalValue.myLog.info( sql + "#" + lstpara );
					}
				}
			}

		} else {// 未继承，直接插入继承信息
			// 新增
			sql = " insert into serviceorproductinfo (serviceorproductinfoid,abstractid,attr4,attr5,attr6,attr7,status) values(?,?,?,?,?,?,?) ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			String serviceorproductinfoid = "";
			if(GetConfigValue.isOracle){
				serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
				serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
			}
			lstpara.add(serviceorproductinfoid);
			// 绑定业务id参数
			lstpara.add(fatherAbstractID);
			// 绑定儿子业务参数
			lstpara.add(sonService);
			// 绑定儿子摘要参数
			lstpara.add(sonAbstract);
			// 绑定摘要id参数
			lstpara.add(currAbstractId);
			// 绑定四层参数
			lstpara.add(serviceType);
			// 绑定处理状态参数
			lstpara.add(1);
			// 将SQL语句放入集合中
			lstSql.add(sql.toString());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}
		// 执行SQL语句，绑定事务，返回事务处理结果
		int result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return result;
	}
	
//	public static int updateKbdataFlag(String kbdataid){
//		int count = 0;
//		String sql ="";
//		sql = "update kbdata set flag=1 where kbdataid=" + kbdataid;
//		try {
//			count = Database.executeNonQuery(sql);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return count;
//	}
	
	public static int insertAttr(String currabstractId,String fatherAbstractID, Map<String,String> indexAndvalue,String serviceType) {

		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		//存放sql
		StringBuilder sqlbf = new StringBuilder();
		StringBuilder sqlaf = new StringBuilder();
		// 定义SQL语句
		String sql = "";
		Result rs = getServiceOrproductInfo(currabstractId);
		if (rs != null && rs.getRowCount() > 0) {// 继承过
			// 循环遍历数据源
			for (int n = 0; n < rs.getRowCount(); n++) {
				String abstractid = rs.getRows()[n].get("abstractid").toString();
				Object status_ = rs.getRows()[n].get("status");
				String status = "";
				if(status == null){
				   status = "0";
				}else{
					status = status.toString();
				}
				if (NewEquals.equals("0",status)) {// 继承过，但未审核，直接删除，重新继承
					// 删除
					sql = " delete from SERVICEORPRODUCTINFO  where attr6 =? ";
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定z摘要id参数
					lstpara.add(currabstractId);
					// 将SQL语句放入集合中
					lstSql.add(sql.toString());
					// 将定义的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( sql + "#" + lstpara );
					
					//插入
					lstpara = new ArrayList<Object>();
					sqlbf = new StringBuilder();
					sqlbf.append("insert into serviceorproductinfo (serviceorproductinfoid,abstractid,status");
					sqlaf = new StringBuilder();
					sqlaf.append(" values(?,?,?");
					String serviceorproductinfoid = "";
					String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
					if(GetConfigValue.isOracle){
						serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
					}else if(GetConfigValue.isMySQL){
						serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
					}
					lstpara.add(serviceorproductinfoid);
					// 绑定业务id参数
					lstpara.add(fatherAbstractID);
					// 绑定处理状态参数
					lstpara.add(1);
					for (Map.Entry<String, String> entry : indexAndvalue.entrySet()) {  
						String index = entry.getKey();  
						String value = entry.getValue();
						sqlbf.append(",attr"+index); 
						sqlaf.append(",?");
						lstpara.add(value);
					} 
					sqlbf.append(")");
					sqlaf.append(")");
					sql = sqlbf.toString()+sqlaf.toString();
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将定义的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( sql + "#" + lstpara );

				} else {
					if (!NewEquals.equals(abstractid,fatherAbstractID)) {// 继承且审核过，但摘要ID不相等，说明存在多继承，直接插入继承信息
						//插入
						lstpara = new ArrayList<Object>();
						sqlbf = new StringBuilder();
						sqlbf.append("insert into serviceorproductinfo (serviceorproductinfoid,abstractid,status");
						sqlaf = new StringBuilder();
						sqlaf.append(" values(?,?,?");
						String serviceorproductinfoid = "";
						String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
						if(GetConfigValue.isOracle){
							serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
						}else if(GetConfigValue.isMySQL){
							serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
						}
						lstpara.add(serviceorproductinfoid);
						// 绑定业务id参数
						lstpara.add(fatherAbstractID);
						// 绑定处理状态参数
						lstpara.add(1);
						for (Map.Entry<String, String> entry : indexAndvalue.entrySet()) {  
							String index = entry.getKey();  
							String value = entry.getValue();
							sqlbf.append(",attr"+index); 
							sqlaf.append(",?");
							lstpara.add(value);
						} 
						sqlbf.append(")");
						sqlaf.append(")");
						sql = sqlbf.toString()+sqlaf.toString();
						
						// 将SQL语句放入集合中
						lstSql.add(sql.toString());
						// 将定义的绑定参数集合放入集合中
						lstLstpara.add(lstpara);
						
						//文件日志
						GlobalValue.myLog.info( sql + "#" + lstpara );
					}
				}
			}

		} else {// 未继承，直接插入继承信息
			//插入
			lstpara = new ArrayList<Object>();
			sqlbf = new StringBuilder();
			sqlbf.append("insert into serviceorproductinfo (serviceorproductinfoid,abstractid,status");
			sqlaf = new StringBuilder();
			sqlaf.append(" values(?,?,?");
			String  serviceorproductinfoid = "";
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			if(GetConfigValue.isOracle){
				serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
				serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
			}
			lstpara.add(serviceorproductinfoid);
			// 绑定业务id参数
			lstpara.add(fatherAbstractID);
			// 绑定处理状态参数
			lstpara.add(1);
			for (Map.Entry<String, String> entry : indexAndvalue.entrySet()) {  
				String index = entry.getKey();  
				String value = entry.getValue();
				sqlbf.append(",attr"+index); 
				sqlaf.append(",?");
				lstpara.add(value);
			} 
			sqlbf.append(")");
			sqlaf.append(")");
			sql = sqlbf.toString()+sqlaf.toString();
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}
		lstpara = new ArrayList<Object>();
		String tempSql = "update kbdata set flag=1 where kbdataid=?";
		lstpara.add(currabstractId);
		// 将SQL语句放入集合中
		lstSql.add(tempSql);
		// 将定义的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		// 执行SQL语句，绑定事务，返回事务处理结果
		int result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return result;
	}
	
	/**
	 *@description 根据四层结构串，知识类别获得行业问题库下摘要词模数据源
	 *@param serviceType 四层结构串
	 *@param topic 知识类别
	 *@param serviceid 业务ID
	 *@return 
	 *@returnType Result 
	 */
	public static Result getAbstractAndWordpat(String serviceType,String serviceid,String topic){
		List<Object> lstpara = new ArrayList<Object>();
		String sql ="select s.serviceid,s.service,k.kbdataid,k.abstract,k.topic,w.wordpatid,w.wordpat from  service s ,kbdata k,wordpat w where s.serviceid =k.serviceid  and k.kbdataid  = w.kbdataid and s.brand =? and s.serviceid = ? and k.topic like ? and (w.wordpat like '%父类%' or w.wordpat like '%业务子句%' or w.wordpat like '%父子句%') and w.wordpat not like '~%' and  w.wordpat not like '+%'  ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		String brand =serviceType.split("->")[0]+"问题库";
		lstpara.add(brand);
		lstpara.add(serviceid);
		lstpara.add(topic+"%");
		Result rs = null;
		
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
	
		return rs;
		
	}
	
	/**
	 *@description 批量更新词模
	 *@param map 词模字典  map<id,wordpat>
	 *@param userid 用户ID
	 *@return 
	 *@returnType int 
	 */
	public static int updateWordpat(Map<String, String> map,String workerid){
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		List<Object> lstpara = new ArrayList<Object>();
		String sql = "update wordpat set wordpat =? ,workerid ='test', edittime = sysdate where wordpatid =? ";
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String wordpatid = entry.getKey();
			String wordpat = entry.getValue();
			lstpara = new ArrayList<Object>();
			lstpara.add(wordpat);
//			lstpara.add(workerid);
			lstpara.add(wordpatid);
			lstLstpara.add(lstpara);
		}
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstLstpara );
		int result  = Database.executeNonQueryBatchTransaction(sql, lstLstpara);
		return  result;
	}
	
	/**
	 *@description 获得参数配置表具体值数据源
	 *@param name  配置参数名
	 *@param key   配置参数名对应key
	 *@return 
	 *@returnType Result 
	 */
	public static Result getConfigValue(String name ,String key){
	    List<Object> lstpara = new ArrayList<Object>();
		String sql ="select s.name from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and a.name =? and t.name =?  order by s.metafieldid ";
		lstpara = new ArrayList<Object>();
		//根据四层结构串获得brand
		lstpara.add(name);
		lstpara.add(key);
		Result rs = null;
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		rs = Database.executeQuery(sql, lstpara.toArray());
		
		return rs;
	   
	}
	
	public static int InsertAttrName(String kbdataid, String columnNames[],
			int column,String serviceType) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		
		for (int i = 0; i < columnNames.length; i++) {
			column++;
			lstpara = new ArrayList<Object>();
			// 定义新增属性名称的SQL语句
			sql = "insert into serviceattrname2colnum (serviceattrname2colnumid,name,columnnum,abstractid) values (?,?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			
			Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("商家与行业问题库四层结构映射关系配置",serviceType);
			if (rsConfig != null && rsConfig.getRowCount() > 0){
				serviceType = rsConfig.getRows()[0].get("name").toString();
			}
			
			String serviceattrname2colnumid = "";
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			if(GetConfigValue.isOracle){
				serviceattrname2colnumid = ConstructSerialNum.GetOracleNextValNew("serviceattrname2colnum_seq",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
				serviceattrname2colnumid = ConstructSerialNum.getSerialIDNew("serviceattrname2colnum","serviceattrname2colnumid",bussinessFlag);	
			}
			lstpara.add(serviceattrname2colnumid);
			// 绑定属性名称参数
			lstpara.add(columnNames[i]);
			// 绑定列值参数
			lstpara.add(column);
			// 绑定业务id参数
			lstpara.add(kbdataid);
			// 将SQL语句放入集合中
			lstSql.add(sql.toString());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
		}
		// 执行SQL语句，绑定事务，返回事务处理结果
		int result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return result;
	}
	
	/**
	 * 批量删除操作
	 * @return
	 * @throws SQLException 
	 */
	public static int doDeleteInfo(String ids) throws SQLException {
		int result = 0;
		String[] id = ids.split(",");
		int len = id.length;
		for (int i=0;i<len;i++){
			String tempSql = "select * from serviceorproductinfo where attr6 in (select attr6 from serviceorproductinfo where serviceorproductinfoid in (" + id[i] + "))";
			
			//文件日志
			GlobalValue.myLog.info( tempSql );
			Result rs = Database.executeQuery(tempSql);
			if (rs != null && rs.getRowCount() == 1) {
				String sql2 = "update kbdata set flag=0 where kbdataid ='" + rs.getRows()[0].get("attr6") + "'";
				int result2 = Database.executeNonQuery(sql2);
			}
			String sql = "delete from Serviceorproductinfo where Serviceorproductinfoid='" + id[i] + "'";
//			String sql = "delete from Serviceorproductinfo where Serviceorproductinfoid in (" + ids + ")";
			result += Database.executeNonQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
		}
		return result;
	}
	
	/**
	 * 复制操作
	 * @param serviceorproductinfoid 表serviceorproductinfo主键
	 * @return
	 */
	public static int copyInfo(String serviceorproductinfoid,String serviceType) {
		int result = 0;
		// 插入数据的参数集合
		List<Object> param = new ArrayList<Object>();
		// 查询出原数据attr1~attr7的数据
		String sql = "select attr1,attr2,attr3,attr4,attr5,attr6,attr7,attr8,attr9,attr10,attr11,attr12,attr13,attr14,attr15,abstractid,inserttime from serviceorproductinfo where serviceorproductinfoid="+serviceorproductinfoid;
		
		Result rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			String[] columns = rs.getColumnNames();
			for (int i = 0; i < rs.getRowCount(); i++) {
				for (int j = 0; j < columns.length; j++) {
					Object obj = rs.getRows()[i].get(columns[j]);
//					if (obj == null) {
						param.add(obj);
//					} else {
//						param.add(obj.toString());
//					}
				}
			}
		} else {
			return result;
		}
		// 插入数据
		sql = "insert into serviceorproductinfo(serviceorproductinfoid,attr1,attr2,attr3,attr4,attr5,attr6,attr7,attr8,attr9,attr10,attr11,attr12,attr13,attr14,attr15,abstractid,inserttime,status)"+
		      " values ('";
		String newserviceorproductinfoid="";
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		if(GetConfigValue.isOracle){
			newserviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
			newserviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
			}
		sql+=newserviceorproductinfoid;
		sql+="',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		// 状态为0，未审批
		param.add(0);
		result = Database.executeNonQuery(sql, param.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + param );
		return result;
	}
	
	/**
	 * 大表更新数据操作
	 * @param array
	 * @return
	 */
	public static int doSave(Map<String, Object> map,String industry,String serviceType) {
		int result = 0;
		// sql参数集合
		List<Object> param = new ArrayList<Object>();
		// 查询时否有相同的数据
		String sql = "select * from Serviceorproductinfo where ";
		for (Entry<String, Object> entry : map.entrySet()) {
			if (entry.getValue() == null) {
				sql += entry.getKey() + " is null and ";
			} else {
				sql += entry.getKey() + "=? and ";
				param.add(entry.getValue());
			}
		}
		sql = sql.substring(0, sql.lastIndexOf("and"));
		//文件日志
		GlobalValue.myLog.info( sql + "#" + param );
		Result rs = Database.executeQuery(sql, param.toArray());
		if (rs != null && rs.getRowCount() > 0) {// 已存在该数据
			result = -1;
			return result;
		} else {
			param.clear();
		}
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		if (map.get("serviceorproductinfoid")==null) {// 进行新增操作
			map.remove("serviceorproductinfoid");// 将逐渐id值移除
			String firstSql = "insert into serviceorproductinfo(serviceorproductinfoid,attr7,";
			String lastSql = "";
			String serviceorproductinfoid = "";
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			if(GetConfigValue.isOracle){
				serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
				serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
			}
			lastSql = " values (?,?,";
			param.add(serviceorproductinfoid);
			param.add(industry);
			for (Entry<String, Object> entry : map.entrySet()) {
				firstSql += entry.getKey() + ",";
				lastSql += "?,";
				param.add(entry.getValue());
			}
			sql = firstSql.substring(0, firstSql.lastIndexOf(",")) + ")" + lastSql.substring(0, lastSql.lastIndexOf(",")) + ")";
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(param);
			param = new ArrayList<Object>();
			String tempSql = "update kbdata set flag=1 where kbdataid=?";
			param.add(map.get("attr6"));
			// 将SQL语句放入集合中
			lstSql.add(tempSql);
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(param);
			
			//文件日志
			GlobalValue.myLog.info( tempSql + "#" + param );
		} else {// 进行更新操作
			sql = "update serviceorproductinfo set status=?,";
			param.add(1);
			String str = "attr8,attr9,attr10,attr11,attr12,attr13,attr14,attr15";
			for (Entry<String, Object> entry : map.entrySet()) {
				if(str.contains(entry.getKey())) {
					sql += entry.getKey() + "=?,";
					param.add(entry.getValue());
				} else if (entry.getValue() != null && !entry.getKey().equals("serviceorproductinfoid")) {
					sql += entry.getKey() + "=?,";
					param.add(entry.getValue());
				}
			}
			sql = sql.substring(0, sql.lastIndexOf(",")) + " where serviceorproductinfoid=?";
			param.add(map.get("serviceorproductinfoid"));
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(param);
			//文件日志
			GlobalValue.myLog.info( sql + "#" + param );
		}
		result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return result;
	}
	
	/**
	 * 手动批量继承
	 * @param fckbdataids 父亲-儿子摘要id组合
	 * @param fkbdataid 要继承的父亲摘要id
	 * @param serviceArray 手动录入的业务X~M
	 * @return
	 */
	public static int doManualInherit(String[] fckbdataids, String fkbdataid, String[] serviceArray, String industry,String serviceType,User user, String oldinfo) {
		String fkbdata = fkbdataid.split("-")[1];
		fkbdataid = fkbdataid.split("-")[0];
		
		int result = 0;
		Result rs = null;
		// sql语句
		String sql = "";
		List<String> lstsql = new ArrayList<String>();
		List<List<?>> lstparam = new ArrayList<List<?>>();
		// 查询serviceattrname2colnum中是否存在数据
		sql = "select abstractid from serviceattrname2colnum where abstractid='"+fkbdataid+"'";
		//文件日志
		GlobalValue.myLog.info( sql );
		
		rs = Database.executeQuery(sql);
		if (rs == null || rs.getRowCount() <= 0) {// 如果没有数据
			for (int i=0; i<names.length; i++) {
				// 定义新增属性名称的SQL语句
				sql = "insert into serviceattrname2colnum (serviceattrname2colnumid,name,columnnum,abstractid) values (?,?,?,?)";
				lstsql.add(sql);
				// 定义绑定参数集合
				List<Object> lstpara = new ArrayList<Object>();
				
				Result rsConfig = CommonLibMetafieldmappingDAO.getConfigValue("商家与行业问题库四层结构映射关系配置",serviceType);
				if (rsConfig != null && rsConfig.getRowCount() > 0){
					serviceType = rsConfig.getRows()[0].get("name").toString();
				}
				
				String serviceattrname2colnumid = "";
				String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
				// 获取属性名称表的序列值，并绑定参数
				if(GetConfigValue.isOracle){
					serviceattrname2colnumid = ConstructSerialNum.GetOracleNextValNew("serviceattrname2colnum_seq",bussinessFlag);	
				}else if(GetConfigValue.isMySQL){
					serviceattrname2colnumid = ConstructSerialNum.getSerialIDNew("serviceattrname2colnum","serviceattrname2colnumid",bussinessFlag);	
				}
				lstpara.add(serviceattrname2colnumid);
				// 绑定属性名称参数
				lstpara.add(names[i]);
				// 绑定列值参数
				lstpara.add(i+1);
				// 绑定业务id参数
				lstpara.add(fkbdataid);
				lstparam.add(lstpara);
			}
			
			
		}
		for (String str : fckbdataids) {
//			String fkbdatainfo = "";
			String[] array = str.split("-");
			// 儿子摘要id
			String cKbdataid = "";
			cKbdataid = array[1].split(":")[1];
			
			// 修改摘要状态
			sql = "update kbdata set flag=1 where kbdataid =?";
			lstsql.add(sql);
			List<Object> lstpara = new ArrayList<Object>();
			// 绑定业务id参数
			lstpara.add(cKbdataid);
			lstparam.add(lstpara);
			
			
			
			if (array[0].split(":").length > 1) {// 已经有继承的数据
				// 根据儿子和父亲摘要查找出是否有业务返回值未填写的数据
				sql = "select * from Serviceorproductinfo where abstractid=? and attr6=? and ";
				List<Object> param = new ArrayList<Object>();
				param.add(fkbdataid);
				param.add(cKbdataid);
				for(int i=0; i<serviceArray.length; i++) {
					String[] s = serviceArray[i].split(":");
					if (s.length > 1) {
						sql += s[0] + "=? and ";
						param.add(s[1]);
//						fkbdatainfo = fkbdatainfo + s[1] + ",";
					}
				}
//				if (fkbdatainfo.contains(",")){
//					fkbdatainfo = fkbdatainfo.substring(0, fkbdatainfo.lastIndexOf(","));
//				}
				sql = sql.substring(0, sql.lastIndexOf("and"));
				rs = Database.executeQuery(sql, param.toArray());
				//文件日志
				GlobalValue.myLog.info( sql + "#" + param );
				if (rs != null && rs.getRowCount() > 0) {// 存在该数据
					continue;
				}
			}
			// 执行插入操作
			// 根据儿子摘要id，查询摘要对应的业务和摘要名
			sql = "select s.service,k.abstract from service s,kbdata k where s.serviceid=k.serviceid and k.kbdataid="+cKbdataid;
			rs = Database.executeQuery(sql);
			// 儿子摘要业务
			String cService = "";
			// 儿子摘要名
			String cAbstract = "";
			if (rs != null && rs.getRowCount() > 0) {
				cService = rs.getRows()[0].get("service").toString();
				cAbstract = rs.getRows()[0].get("abstract").toString();
			}
			String serviceorproductinfoid = "";
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			String sql1 = "insert into Serviceorproductinfo(serviceOrProductInfoid,attr4,attr5,attr6,attr7,abstractid,status,";
			if(GetConfigValue.isOracle){
				serviceorproductinfoid = ConstructSerialNum.GetOracleNextValNew("serviceorproductinfo_seq",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
				serviceorproductinfoid = ConstructSerialNum.getSerialIDNew("serviceorproductinfo","serviceorproductinfoid",bussinessFlag);	
			}
			String sql2 = "";
			sql2 = " values(?,?,?,?,?,?,?,";
			List<Object> param = new ArrayList<Object>();
			param.add(serviceorproductinfoid);
			param.add(cService);
			param.add(cAbstract);
			param.add(cKbdataid);
			param.add(industry);
			param.add(fkbdataid);
			param.add(1);
			
			for(int i=0; i<serviceArray.length; i++) {
				String[] s = serviceArray[i].split(":");
				if (s.length > 1) {
					sql1 += s[0]+",";
					sql2 += "?,";
					param.add(s[1]);
				}
			}
			sql = sql1.substring(0, sql1.lastIndexOf(",")) + ")" + sql2.substring(0, sql2.lastIndexOf(",")) + ")";
			lstsql.add(sql);
			lstparam.add(param);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + param );
			
			// 将操作日志SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			String brand = serviceType.split("->")[1];
			String _object = cAbstract + "继承" + fkbdata + ",INFO:" + oldinfo;
			lstparam.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), brand, " ", "增加继承",
					_object, "INHERIT"));
		}
		if (lstsql.size() == 0) {
			result = -1;
			return result;
		}
		result = Database.executeNonQueryTransaction(lstsql, lstparam);
		return result;
	}
}
