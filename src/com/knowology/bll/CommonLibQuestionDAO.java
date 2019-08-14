package com.knowology.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLibQuestionDAO {
	/**
	 *@description 查询满足条件的相似问题的数
	 *@param kbdataid
	 *            摘要ID
	 *@param question
	 *            模糊查询问题
	 *@param isbzquestion
	 *            标准问题标识
	 *@return
	 *@returnType int
	 */
	public static int getCount(String kbdataid, String question,
			Object isbzquestion) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 查询相似问题的SQL语句
		String sql = "select count(*) count from similarquestion where kbdataid =? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 判断问题参数是否为空且不为null
		if (question != null && !"".equals(question)) {
			// 添加对问题的like查询
			paramSql.append(" and question like ? ");
			// 绑定问题参数
			lstpara.add("%" + question.trim() + "%");
		}
		// 判断标准问题是否为空且不为null
		if (isbzquestion != null && !"".equals(isbzquestion)) {
			// 添加对问题类型的查询
			paramSql.append("and questiontype = ? ");
			// 绑定问题类型参数
			lstpara.add(isbzquestion);
		}
		// 执行SQL语句，返回执行结果
		Result rs = Database.executeQuery(sql + paramSql.toString(), lstpara
				.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + paramSql.toString() + "#" + lstpara );
		
		int count = 0;
		if (rs != null && rs.getRowCount() > 0) {
			count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		}
		return count;
	}

	/**
	 *@description 查询满足条件的相似问题数据
	 *@param kbdataid
	 *            摘要ID
	 *@param question
	 *            模糊查询问题
	 *@param isbzquestion
	 *            标准问题
	 *@param start
	 *            开始记录数据
	 *@param limit
	 *            间隔记录数
	 *@return
	 *@returnType Result
	 */
	public static Result select(String kbdataid, String question,
			Object isbzquestion, String start, String limit) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 判断问题参数是否为空且不为null
		if (question != null && !"".equals(question)) {
			// 添加对问题的like查询
			paramSql.append(" and question like ? ");
			// 绑定问题参数
			lstpara.add("%" + question.trim() + "%");
		}
		// 判断标准问题是否为空且不为null
		if (isbzquestion != null && !"".equals(isbzquestion)) {
			// 添加对问题类型的查询
			paramSql.append("and questiontype = ? ");
			// 绑定问题类型参数
			lstpara.add(isbzquestion);
		}
		// 查询满足条件的数据源
		String sqlQuery = "";
		if (GetConfigValue.isOracle) {
			sqlQuery = "select b.*,rownum rn from (select a.*, rownum rn from (select * from similarquestion where kbdataid=? "
					+ paramSql
					+ " order by weight )a)b where rn>=? and rn<=?";
		} else if (GetConfigValue.isMySQL) {
			sqlQuery = "select b.*from (select a.* from (select * from similarquestion where kbdataid=? "
					+ paramSql + " order by questionid desc)a)b limit ?,?";
		}
		// 绑定开始条数参数
		lstpara.add(Integer.valueOf(start));
		// 绑定截止条数参数
		lstpara.add(Integer.parseInt(start) + Integer.parseInt(limit));
		// 执行SQL语句，返回相应的数据源
		Result rs = Database.executeQuery(sqlQuery, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sqlQuery + "#" + lstpara );

		return rs;

	}

	/**
	 * 判断问题是否存在
	 * 
	 * @param question参数问题
	 * @param kbdataid参数摘要id
	 * @param questiontype参数问题类型
	 * @param serviceid参数业务id
	 * @return 是否存在
	 */
	public static int exist(String question, String kbdataid,
			String questiontype, String serviceid) {
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 查询当前摘要下问题是否存在的SQL语句
		String sql = "select * from similarquestion where question =? and kbdataid in(?) and questiontype=? ";
		// 绑定问题参数
		lstpara.add(question);
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定问题类型参数
		lstpara.add(questiontype);
		// 执行SQL语句，返回执行结果
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 定义当前摘要下是否存在系统问题的布尔值
		boolean b1 = rs != null && (rs.getRowCount() > 0);
		// 判断当前业务下的问题是否存在的SQL语句
		sql = "select * from similarquestion where question =? and kbdataid in (select kbdataid from kbdata where serviceid=? and kbdataid!=?) and questiontype=? ";
		lstpara = new ArrayList<String>();
		// 绑定问题参数
		lstpara.add(question);
		// 绑定业务id参数
		lstpara.add(serviceid);
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定问题类型参数
		lstpara.add(questiontype);
		// 执行SQL语句，返回执行结果
		Result rs1 = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 定义当前业务下是否存在系统问题的布尔值
		boolean b2 = rs1 != null && (rs1.getRowCount() > 0);
		// 判断是否满足当前摘要下问题不能重复和当前业务下问题不能重复
		if (b1) {
			// 返回1，代表当前摘要下的问题重复
			return 1;
		} else if (b2) {
			// 返回2，代表当前业务下的问题重复
			return 2;
		} else {
			// 两种条件下，都没有重复的问题
			return 0;
		}
	}

	/**
	 *@description 修改相似问题
	 *@param user
	 *            用户信息
	 *@param oldquestion
	 *            旧问题
	 *@param question
	 *            新问题
	 *@param questiontype
	 *            问题类型
	 *@param questionid
	 *            问题ID
	 *@return
	 *@returnType int
	 */
	public static int update(User user, String oldquestion, String question,
			String questiontype, String questionid) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义更新相似问题的SQL语句
		String sql = "update similarquestion set question = ? ,questiontype =? where questionid = ?";
		// 绑定问题参数
		lstpara.add(question);
		// 绑定问题类型参数
		lstpara.add(questiontype);
		// 绑定问题id参数
		lstpara.add(questionid);
		// 将更新SQL语句放入SQL语句集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", " ", "更新相似问题",
				oldquestion + "==>" + question, "SIMILARQUESTION"));
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);

	}

	/**
	 *@description 删除相似问题
	 *@param user
	 *            用户信息
	 *@param questionid
	 *            问题ID
	 *@param oldquestion
	 *            旧问题
	 *@param question
	 *            新问题
	 *@param service
	 *            业务名
	 *@param brand
	 *            品牌
	 *@param kbdataid
	 *            摘要ID
	 *@return
	 *@returnType int
	 */
	public static int delete(User user, String questionid, String question,
			String service, String brand, String kbdataid) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义删除相似问题的SQL语句
		String sql = "delete from similarquestion where questionid=?";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定问题id参数
		lstpara.add(questionid);
		// 将删除相似问题的SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", " ", "删除相似问题", question,
				"SIMILARQUESTION"));
        	// 删除原有的词模
     		sql = "delete from wordpat where wordpat like ?   and wordpattype=? and kbdataid=? and brand=?";
     		// 定义绑定参数集合
     		lstpara = new ArrayList<String>();
     		// 绑定词模like查询的参数
     		lstpara.add("%@2#编者=\"auto\"&来源=\"" + question.replace("&", "\\and")
     				+ "\"%");
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
     		
     		//文件日志
    		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
     		
     		// 执行多条SQL语句的绑定事务处理，并获取事务处理的结果 
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}

	/**
	 *@description 插入多条相似问题
	 *@param user
	 *            用户信息
	 *@param kbdataid
	 *            摘要ID
	 *@param abs
	 *            摘要名
	 *@param questionList
	 *            相似问题集合
	 *@param questiontype
	 *            相似问题类型
	 *@param brand
	 *            品牌
	 *@return
	 *@returnType int
	 */
	public static int insert(User user, String kbdataid, String abs,
			List<String> questionList, String questiontype, String brand) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		for (int i = 0; i < questionList.size(); i++) {
			String question = questionList.get(i);
			// 获取问题集合中的第几个问题
			question = questionList.get(i);
			// 判断问题是否为空
			if ("".equals(question)) {
				// 为空，循环下一个问题
				continue;
			}
			// 判断问题类型是否是标准问题
			if ("标准问题".equals(questiontype)) {
				// 取第一个问题
				question = questionList.get(0);
				// 将循环变量的值赋值为问题集合的大小
				i = questionList.size();
			}
			//获得商家标识符
			String serviceType = user.getIndustryOrganizationApplication();
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			// 获取插入相似问题的序列
			String similarquestionid = "";
			if (GetConfigValue.isOracle) {
				similarquestionid = ConstructSerialNum.GetOracleNextValNew("SIMILARQUESTION_SEQUENCE", bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				similarquestionid = ConstructSerialNum.getSerialIDNew("similarquestion", "questionid", bussinessFlag);
			}

			// 插入相似问题的SQL语句
			String sql = "insert into similarquestion(kbdataid,kbdata,questionid,question,remark,time,questiontype) values (?,?,?,?,'',sysdate,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定摘要参数
			lstpara.add(abs);
			// 绑定相似问题的id参数
			lstpara.add(similarquestionid);
			// 绑定问题参数
			lstpara.add(question);
			// 绑定问题类型参数
			lstpara.add(questiontype);
			// 将插入相似问题的SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

			// 新增功能，在保存问题时，调用接口生成一个词模，并存入词模表中
			// 删除原有的词模
			sql = "delete from wordpat where wordpat like ? and wordpattype=? and kbdataid=? and brand=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定词模like查询的参数
			lstpara.add("%@2#编者=\"auto\"&来源=\""
					+ question.replace("&", "\\and") + "\"%");
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
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			
			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstSql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), " ", " ", "增加相似问题",
					question, "SIMILARQUESTION"));
		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);

	}

	/**
	 *@description 通过问题类型摘要Id查询问题
	 *@param questiontype 问题类型
	 *@param kbdataid 摘要ID
	 *@return 
	 *@returnType Result 
	 */
	public static Result getQuestion(String questiontype, String kbdataid) {
		// 定义SQL语句
		String sql = "select * from similarquestion where questiontype=? and kbdataid=?";
		// 定义绑定参数
		List<String> lstpara = new ArrayList<String>();
		// 绑定问题类型参数
		lstpara.add(questiontype);
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 定义数据源
		// 执行SQL语句，返回相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		return rs;
	}

	/**
	 *@description 通过问题类型摘要Id查询问题优先级
	 *@param kbdataid 摘要ID
	 *@return 
	 *@returnType Result 
	 */
	public static Result getQuestionWeight( String kbdataid) {
		// 定义SQL语句
		String sql = "select * from similarquestion where  kbdataid=? and weight is not null";
		// 定义绑定参数
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 定义数据源
		// 执行SQL语句，返回相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		return rs;
	}
	

	/**
	 * 修改问题优先级
	 *@param questionid
	 *@param weight
	 *@param beforeQuestionid
	 *@param beforeWeight
	 *@return 
	 *@returnType int 
	 */
	public static int updateWeight( String questionid, String weight,
			String beforeQuestionid, String beforeWeight) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义更新相似问题的SQL语句
		String sql = "update similarquestion set weight = ?  where questionid = ?";
		lstpara.add(weight);
		lstpara.add(beforeQuestionid);
		// 将更新SQL语句放入SQL语句集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		lstpara = new ArrayList<Object>();
		
		lstpara.add(beforeWeight);
		lstpara.add(questionid);
		// 将更新SQL语句放入SQL语句集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);
	}
	
}
