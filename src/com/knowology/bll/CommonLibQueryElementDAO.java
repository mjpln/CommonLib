package com.knowology.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

public class CommonLibQueryElementDAO {

	/**
	 * 查询满足条件的问题要素信息记录数
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param name参数问题要素名称
	 * @return int
	 */
	public static int getElementNameCount(String kbdataid, String kbcontentid,
			String name) {
		int count = 0;
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 查询问题元素的SQL语句
		sql
				.append("select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from queryelement q where q.kbdataid=? and q.kbcontentid=? ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
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
	public static Result getElementName(String kbdataid, String kbcontentid,
			String name, int page, int rows) {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		List<Object> lstpara = new ArrayList<Object>();
		// 将SQL语句补充完整
		if (GetConfigValue.isOracle) {
			// 判断数据源不为空且含有数据
			sql = new StringBuilder();
			// 定义分页查询满足条件的问题要素名称SQL语句
			sql.append("select * from (select t.*,rownum rn from (select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from queryelement q where q.kbdataid=? and q.kbcontentid=? ");
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
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
			sql.append("select * from (select t.* from (select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from queryelement q where q.kbdataid=? and q.kbcontentid=? ");
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
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
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}

	
	
	/**
	 * 查询当前问题元素下的优先级
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @return Result
	 */
	public static Result getWeight(String kbdataid, String kbcontentid) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义问题要素的所有的优先级的集合并赋值
		List<String> weightLst = new ArrayList<String>();
		// 循环遍历1-10
		for (int i = 0; i < 10; i++) {
			// 给问题要素的所有的优先级赋值
			weightLst.add(String.valueOf(i + 1));
		}
		// 定义当前的优先级集合
		List<String> weightNow = new ArrayList<String>();
		// 定义查询优先级的SQL语句
		String sql = "select weight from queryelement where kbdataid=? and kbcontentid=? order by weight asc";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
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
			String kbcontentid, String weight, String wordclass, String abs, String serviceType) {
	    int count =-1;
			// 1.先查询词类是否存在
			// 定义查询词类是否存在的SQL语句
			String sql = "select * from wordclass where wordclass=?";
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 绑定词类名称参数
			lstpara.add(wordclass);
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 获取词类id值
				String wordclassid = rs.getRows()[0].get("wordclassid")
						.toString();
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
					String queryelementid ="";
					String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
					if(GetConfigValue.isOracle){
						queryelementid = ConstructSerialNum.GetOracleNextValNew("queryelement_seq",bussinessFlag);	
					}else if(GetConfigValue.isMySQL){
						queryelementid = ConstructSerialNum.getSerialIDNew("queryelement","queryelementid",bussinessFlag);	
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
					count = Database.executeNonQueryTransaction(lstSql,
							lstLstpara);
				}
			} 
			return count;
	}
	
	
	/**
	 * 删除问题要素，并返回相应的信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param elementnameid参数问题要素id
	 * @param weight参数优先级
	 * @param abs参数摘要名称
	 * @return int
	 */
	public static int deleteElementName(String kbdataid, String kbcontentid,
			String elementnameid, String weight, String name, String abs) {
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 删除问题要素的SQL语句
		String sql = "delete from queryelement where queryelementid=?";
		// 绑定问题要素id参数
		lstpara.add(elementnameid);
		// 将删除问题要素的SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入到集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 删除问题要素组合的SQL语句
		sql = "delete from conditioncombtoreturntxt where kbdataid=? and kbcontentid=? and condition"
				+ weight + " is not null";
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
		// 将删除问题要素组合的SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入到集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );

		// 删除规则的SQL语句
		sql = "delete from scenerules where kbdataid=? and kbcontentid=? and condition"
				+ weight + " is not null";
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定kbcontentid参数
		lstpara.add(kbcontentid);
		// 将删除规则的SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入到集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
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
	public static int deleteElementValue(String kbdataid,
			String kbcontentid, String elementvalueid, String weight,
			String name, String wordclass) {
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
	 * 查询当前摘要下的问题要素和关联词条组合信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 */
	public static Result queryElementAndWord(String kbdataid, String kbcontentid) {
		    List<Object> lstpara = new ArrayList<Object>();
			// 获取问题要素组合的SQL语句
			String sql = "select q.name,w.word from queryelement q left join word w on q.wordclassid=w.wordclassid where w.stdwordid is null and q.kbdataid=? and q.kbcontentid=? order by q.weight asc,w.word desc";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
		    return rs;
	}
	
	/**
	 * 查询问题要素信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @return 问题要素组合的json串
	 */
	public static Result queryElement(String kbdataid, String kbcontentid) {
		List<Object> lstpara = new ArrayList<Object>();
			// 定义查询问题要素名称
			String sql = "select * from queryelement where kbdataid=? and kbcontentid=? order by weight asc";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
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
		String[] conditionArr = new String[10];
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将conditions按照@拆分
			conditionArr = conditions.split("@", 10);
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
			String sql ="";
			if(GetConfigValue.isOracle){
				// 查询满足条件的带分页的SQL语句
				sql = "select * from (select t.*,rownum rn from(select * from conditioncombtoreturntxt where kbdataid=? and kbcontentid=? "
						+ paramSql
						+ "order by status asc,combitionid desc)t) where rn>? and rn <=?";	
			}else if(GetConfigValue.isMySQL){
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
		String[] conditionArr = new String[10];
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将conditions按照@拆分
			conditionArr = conditions.split("@", 10);
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
			Result rs = Database
					.executeQuery(sql + paramSql, lstpara.toArray());
			
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
	public static boolean  isExitConditionCombToReturnTxt(String kbdataid,
			String kbcontentid, String conditions, String returntxttype,
			String returntxt) {
		// 定义问题元素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将问题元素中按照@拆分
			conditionArr = conditions.split("@", 10);
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
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());
			
			
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
	public static int  insertConditionCombToReturnTxt(String kbdataid,
			String kbcontentid, String conditions, String returntxttype,
			String returntxt, String abs, String serviceType) {
		// 定义问题元素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将问题元素中按照@拆分
			conditionArr = conditions.split("@", 10);
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
				sql.append("insert into conditioncombtoreturntxt (combitionid,kbdataid,kbcontentid,");
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
				String combitionid ="";
				String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
				if(GetConfigValue.isOracle){
					combitionid = ConstructSerialNum.GetOracleNextValNew("conditioncombtoreturntxt_seq",bussinessFlag);	
				}else if(GetConfigValue.isMySQL){
					combitionid = ConstructSerialNum.getSerialIDNew("conditioncombtoreturntxt","combitionid",bussinessFlag);	
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
						if (conditionArr[i] != null
								&& !"".equals(conditionArr[i])) {
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
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
				
				// 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
				int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
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
			conditionArr = conditions.split("@", 10);
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
						if (conditionArr[i] != null
								&& !"".equals(conditionArr[i])) {
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
						.append("status=?,returntxttype=?,returntxt=? where combitionid=?");
				// 绑定状态参数
				lstpara.add("0");
				// 绑定答案类型参数
				lstpara.add(returntxttype);
				// 绑定答案内容参数
				lstpara.add(returntxt);
				// 绑定数据id参数
				lstpara.add(combitionid);
				// 执行更新SQL语句，绑定事务处理，并返回事务处理的结果
				int c = Database.executeNonQuery(sql.toString(), lstpara
						.toArray());
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
		        return c;
	}
	
	/**
	 * 判断规则是否存在
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合
	 * @param ruletype参数规则类型
	 * @param ruleresponse参数规则回复内容
	 * @return boolean
	 */
	public static boolean isExitSceneRules(String kbdataid, String kbcontentid,
			String conditions,  String ruletype,
			String ruleresponse,String weight) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		// 定义问题元素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将问题元素中按照@拆分
			conditionArr = conditions.split("@", 10);
		}
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
			// 查询规则是否重复的SQL语句
			sql.append("select * from scenerules where kbdataid=? and kbcontentid=? ");
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
			sql.append(" and ruletype=? and ruleresponse=? and weight=? ");
			// 绑定规则类型参数
			lstpara.add(ruletype);
			// 绑定规则回复内容参数
			lstpara.add(ruleresponse.trim());
			// 绑定规则优先级
			lstpara.add(weight);
			// 执行SQL语句，获取相应的数据源
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为空且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				return true;
			} 
		     return false;
	}
	
	/**
	 * 将规则添加到规则表中，并返回相应的信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合
	 * @param weight参数规则优先级
	 * @param ruletype参数规则类型
	 * @param ruleresponse参数规则回复内容
	 * @param abs参数摘要名称
	 * @return 添加后返回的json串
	 */
	public static int insertSceneRules(String kbdataid, String kbcontentid,
			String conditions, String weight, String ruletype,
			String ruleresponse, String abs, String serviceType) {
		// 定义问题元素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将问题元素中按照@拆分
			conditionArr = conditions.split("@", 10);
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
						sql.append("insert into scenerules (ruleid,kbdataid,kbcontentid,");
				// 判断问题要素组合不为null且不为空
				if (conditions != null && !"".equals(conditions)) {
					// 循环遍历问题要素数组
					for (int i = 0; i < conditionArr.length; i++) {
						// 在SQL语句中将上condition1-10
						sql.append("condition" + (i + 1) + ",");
					}
				}
				// 补充完整插入规则表的SQL语句
				sql.append("ruletype,ruleresponse,weight) values (?,?,?,");
				String ruleid = ""; 
				String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
				if(GetConfigValue.isOracle){
					ruleid = ConstructSerialNum.GetOracleNextValNew("scenerules_seq",bussinessFlag);	
				}else if(GetConfigValue.isMySQL){
					ruleid = ConstructSerialNum.getSerialIDNew("scenerules","ruleid",bussinessFlag);	
				}
				// 获取规则表的序列值,绑定序列值参数
				lstpara.add(ruleid);
				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定kbcontentid参数
				lstpara.add(kbcontentid);
				// 判断问题要素组合不为null且不为空
				if (conditions != null && !"".equals(conditions)) {
					// 循环遍历问题要素数组
					for (int i = 0; i < conditionArr.length; i++) {
						// 判断某一个问题要素不为null且不为空
						if (conditionArr[i] != null
								&& !"".equals(conditionArr[i])) {
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
				sql.append("?,?,?)");
				// 绑定规则类型参数
				lstpara.add(ruletype);
				// 绑定规则回复内容参数
				lstpara.add(ruleresponse.trim());
				// 绑定规则优先级参数
				lstpara.add(weight);
				// 将SQL语句放入集合中
				lsts.add(sql.toString());
				// 将对应的绑定参数集合放入集合
				lstlstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
				
				// 执行插入SQL语句，绑定事务处理，并返回事务处理的结果
				int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
				// 判断事务处理结果
		        return c;
	}
	
	
	/**
	 * 根据不同的条查询满足条件的规则信息记录数
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合
	 * @param ruletype参数规则类型
	 * @param weight参数规则优先级
	 * @return int
	 */
	public static int getSceneRulesCount(String kbdataid, String kbcontentid,
			String conditions, String ruletype, String weight) {
		// 定义返回的count
		int count = 0;
		// 定义问题要素数组，并设定长度为10
		String[] conditionArr = new String[10];
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将conditions按照@拆分
			conditionArr = conditions.split("@", 10);
		}
			// 查询规则的SQL语句
			String sql = "select * from scenerules where kbdataid=? and kbcontentid=? and ruletype=? ";
			if (NewEquals.equals(ruletype,"2")){
				sql += "or ruletype=3 ";
			}
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 绑定规则类型参数
			lstpara.add(ruletype);
			// 定义条件的SQL语句
			StringBuilder paramSql = new StringBuilder();
			// 判断conditions不为null且不为空
			if (conditions != null && !"".equals(conditions)) {
				// 循环遍历问题要素数组
				for (int i = 0; i < conditionArr.length; i++) {
					// 判断某一个问题要素不为null且不为空
					if (conditionArr[i] != null && !"".equals(conditionArr[i])) {
						// 在SQL语句中加上条件
						paramSql
								.append(" and condition" + (i + 1) + " like ? ");
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
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql + paramSql.toString(),
					lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + paramSql.toString() + "#" + lstpara );
			// 判断数据源不为null
			if (rs != null) {
				count = rs.getRowCount();
			} 
		
		return count;
	}
	
	
	/**
	 * 根据不同的条件分页查询满足条件的规则信息
	 * 
	 * @param kbdataid参数摘要id
	 * @param kbcontentid参数kbcontentid
	 * @param conditions参数问题要素组合
	 * @param ruletype参数规则类型
	 * @param weight参数规则优先级
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return Result
	 */
	public static Result getSceneRules(String kbdataid, String kbcontentid,
			String conditions, String ruletype, String weight, int page,
			int rows) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义问题要素数组，并设定长度为10
		String[] conditionArr = new String[10];
		// 判断conditions不为null且不为空
		if (conditions != null && !"".equals(conditions)) {
			// 将conditions按照@拆分
			conditionArr = conditions.split("@", 10);
		}
		String sql = "";
		if(GetConfigValue.isOracle){
			sql = "select * from (select t.*,rownum rn from(select * from(select * from scenerules where kbdataid=? and kbcontentid=?)h where ruletype=? ";
		} else {
			sql = "select * from (select * from(select * from scenerules where kbdataid=? and kbcontentid=?)h where ruletype=? ";
		}
		if (NewEquals.equals(ruletype,"2")){
			sql += "or ruletype=3 ";
		}
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 绑定规则类型参数
			lstpara.add(ruletype);
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
				// 分页查询满足条件的规则的SQL语句
				 sql = sql + paramSql;
				 if(GetConfigValue.isOracle){
				  sql = sql + "order by weight desc,ruleid desc )t) where rn>? and rn <=?"; 
				 }else if(GetConfigValue.isMySQL){
				  sql = sql + "order by weight desc,ruleid desc )t limit ?,?";  
				 }
				// 绑定开始条数参数
				lstpara.add((page - 1) * rows);
				// 绑定截止条数参数
				lstpara.add(page * rows);
				// 执行SQL语句，获取相应的数据源
				Result rs = Database.executeQuery(sql, lstpara.toArray());
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
				
				// 判断数据源不为null且含有数据
		        return rs;
	}
	
	/**
	 * 根据规则id删除规则信息，并返回执行记录数
	 * 
	 * @param ruleid参数规则id
	 * @param abs参数摘要名称
	 * @return int
	 */
	public static int deleteSceneRules(String ruleid, String abs) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		// 定义多条SQL语句集合
		List<String> lsts = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 删除数据的SQL语句
		sql.append("delete from scenerules where ruleid in (");
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义数据id数组
		String[] ids = new String[] {};
		// 判断数据id不为null且不为空
		if (ruleid != null && !"".equals(ruleid)) {
			// 将数据id按照逗号拆分
			ids = ruleid.split(",");
		}
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
		// 将SQL语句放入集合中
		lsts.add(sql.toString());
		// 将对应的绑定参数集合放入集合
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
			// 执行SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lsts, lstlstpara);
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
	 * @return int
	 */
	public static int updateSceneRules(String kbdataid, String kbcontentid,
			String conditions, String weight, String ruletype,
			String ruleresponse, String ruleid) {
		// 定义问题要素数组
		String[] conditionArr = new String[] {};
		// 判断问题要素组合不为null且不为空
		if (conditions != null || !"".equals(conditions)) {
			// 将问题要素组合按照@拆分
			conditionArr = conditions.split("@", 10);
		}
			// 定义SQL语句
			StringBuilder sql = new StringBuilder();
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 绑定规则类型参数
			lstpara.add(ruletype);
			// 绑定规则回复内容
			lstpara.add(ruleresponse.trim());
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				// 更新规则的SQL语句
				sql.append("update scenerules set ");
				// 判断问题要素组合不为null且不为空
				if (conditions != null && !"".equals(conditions)) {
					// 循环遍历问题要素数组
					for (int i = 0; i < conditionArr.length; i++) {
						// 判断某个问题要素不为null且不为空
						if (conditionArr[i] != null
								&& !"".equals(conditionArr[i])) {
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
				sql.append("ruleresponse=?,weight=? where ruleid=?");
				// 绑定规则回复内容参数
				lstpara.add(ruleresponse.trim());
				// 绑定规则优先级参数
				lstpara.add(weight);
				// 绑定规则id参数
				lstpara.add(ruleid);
				// 执行更新SQL语句，绑定事务处理，并返回事务处理的结果
				int c = Database.executeNonQuery(sql.toString(), lstpara
						.toArray());
				
				//文件日志
				GlobalValue.myLog.info( sql + "#" + lstpara );
				
		       return c;
	}
	
}



