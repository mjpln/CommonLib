package com.knowology.DataLayer;

import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.DbDAO.DBValueOper;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.bll.ConstructSerialNum;
import com.knowology.dal.Database;

public class DbLogic {
	// 数据库类型标识
	// private boolean mySQL;
	// 表字段类型对应关系集合
	// private Map<String, String> ctMap;

	/**
	 * @description
	 * @param classify
	 *            SQL分支标识->String
	 * @param className
	 *            类名或方法名<类名_方法名_序列号> ->String
	 * @param param
	 *            参数集合->List<Object>
	 * @return
	 * @throws SQLException
	 */
	public int update(String classify, String className, List<Object> param)
			throws Exception {
		if (null == className) {
			throw new NullPointerException("The second param is null!!!");
		}

		// 更新结果行数
		int res = 0;
		// 设置连接数据库标识
		// setMySQL();
		// 操作SQL
		String sql = "";

		// SQL指定类型判断
		if (null == classify) {
			// 类或方法分类<放在数据库分类前因为有些sql数据库可以通用>
			if (className.equals("SaveQueryHistoryData")) {
				// 数据库分类
				if (GetConfigValue.isMySQL) {
					sql = "INSERT INTO QUERYHISTORYLOG(LOGSOURCE,ID,USERID,QUERY,CHANNEL,SERVICE,ABSTRACT,PARENTSERVICE,TYPE,ANSWER,STANDOUTPUT,STARTTIME,ENDTIME,ISENDSCENARIOS,NLPSERVER,ISMATCHED,STANDINPUT,STATISTICALINFORMATION) "
							+ "VALUES (?, "
							+ ConstructSerialNum.getSerialID("QUERYHISTORYLOG",
									"ID")
							+ ", ?, ?, ?, ?, ?, ?, ?, ?, ?, DATE_FORMAT(?,'%Y-%m-%d %H-%i-%s'), DATE_FORMAT(?,'%Y-%m-%d %H-%i-%s'), ?, ?, ?, ?, ?)";
				} else {
					sql = "INSERT INTO QUERYHISTORYLOG(LOGSOURCE,ID,USERID,QUERY,CHANNEL,SERVICE,ABSTRACT,PARENTSERVICE,TYPE,ANSWER,STANDOUTPUT,STARTTIME,ENDTIME,ISENDSCENARIOS,NLPSERVER,ISMATCHED,STANDINPUT,STATISTICALINFORMATION) "
							+ "VALUES (?, QUERYHISTORYLOG_ID_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_TIMESTAMP(?, 'yyyy-mm-dd hh24:mi:ss'), TO_TIMESTAMP(?, 'yyyy-mm-dd hh24:mi:ss'), ?, ?, ?, ?, ?)";
				}
			}

			if (className.equals("QuestionDAO.update")) {
				sql = "UPDATE SIMILARQUESTION SET QUESTION = ? ,QUESTIONTYPE =? WHERE QUESTIONID = ?";
			}

			// QuestionDao.java类中questionDelete()方法数据层相关操作
			if (className.equals("QuestionDao.questionDelete")) {
				// 删除SQL语句
				sql = "delete from similarquestioninc";
			}

		} else {
			;
		}

		// 数据库操作
		try {
			if (null == param || param.size() == 0) {
				res = Database.executeNonQueryReport(sql);
			} else {
				res = Database.executeNonQueryReport(sql, param.toArray());
			}
		} catch (Exception e) {
			GlobalValue.myLog.error("【咨询历史保存失败】" + e.toString());
			GlobalValue.myLog.error("【咨询历史保存失败】" + sql);
			String outParam="";
			for(Object p:param)
			{
				outParam+="$-$"+DBValueOper.GetValidateStringObj4Null(p);
			}
			GlobalValue.myLog.error("【咨询历史保存失败】" + outParam);
		} finally {
			sql = null;
		}
		return res;
	}

	public int updateTrans(List<String> lstSql, List<List<?>> paramList)
			throws Exception {
		if (null == paramList || null == lstSql) {
			throw new NullPointerException(
					"The first or second param is null!!!");
		}

		// 设置连接数据库标识
		// setMySQL();

		// 数据库操作, 执行多条SQL语句的绑定事务处理，并获取事务处理的结果
		return Database.executeNonQueryTransaction(lstSql, paramList);
	}

	public Result find(String className, Object condition, List<Object> param)
			throws Exception {
		if (null == className || null == param) {
			throw new NullPointerException(
					"The first or third param is null!!!");
		}

		// SQL语句.
		String sql = "";
		// 查询结果
		Result result = null;

		if (null == condition) {
			if (className.equals("QuestionDAO.findstandardquestion")) {
				sql = "select * from similarquestion where questiontype=? and kbdataid=?";
			}

			// QuestionDAO.java类中Exists()方法数据层相关操作
			if (className.equals("QuestionDAO.Exists.1")) {
				// 查询当前摘要下问题是否存在的SQL语句
				sql = "select * from similarquestion where question =? and kbdataid in(?) "
						+ "and questiontype=? ";
			}
			// QuestionDAO.java类中Exists()方法数据层相关操作
			if (className.equals("QuestionDAO.Exists.2")) {
				sql = "select * from similarquestion where question =? and kbdataid in "
						+ "(select kbdataid from kbdata where serviceid=? and kbdataid!=?) and "
						+ "questiontype=? ";
			}

			// QuestionDAO.java类中batchProductWordpat()方法数据层相关操作
			if (className.equals("QuestionDAO.batchProductWordpat")) {
				// 定义查询相似问题的SQL语句
				sql = "select * from similarquestion where kbdataid=?";
			}

			// QuestionDAO.java类中updateWordpat()方法数据层相关操作
			if (className.equals("QuestionDAO.updateWordpat.1")) {
				if (GetConfigValue.isMySQL) {
					sql = "call pro_settree('serviceid', 'service', 'parentid', ?, 'service')";
					Database.executeQueryReport(sql);
					sql = "SELECT id serviceid FROM mytree ORDER BY levv";
				} else {
					sql = "select serviceid from service start with serviceid in(?) "
							+ "connect by nocycle prior serviceid=parentid";
				}
			}
			if (className.equals("QuestionDAO.updateWordpat.2")) {
				// 查询当前业务id下的摘要id、相似问题的组合信息的SQL语句
				sql = "select k.kbdataid,s.question from similarquestion s,kbdata k where s.kbdataid=k.kbdataid and k.serviceid=? "
						+ " and s.question not in (select s.question from similarquestion s,kbdata k,wordpat w where s.kbdataid=k.kbdataid "
						+ " and k.kbdataid=w.kbdataid and k.serviceid=? and w.wordpat like ?)";
			}

		} else {
			if (className.equals("QuestionDAO.select.1")) {
				JSONObject json = (JSONObject) condition;
				// 查询相似问题的SQL语句
				sql = "select * from similarquestion where kbdataid =? ";
				// 判断问题参数是否为空且不为null
				if (json.get("question") != null
						&& !"".equals(json.get("question"))) {
					// 添加对问题的like查询
					sql += " and question like ? ";
					// 绑定问题参数
					param.add("%" + json.getString("question").trim() + "%");
				}

				// 判断标准问题是否为空且不为null
				if (json.get("isbzquestion") != null
						&& !"".equals(json.get("isbzquestion"))) {
					// 添加对问题类型的查询
					sql += "and questiontype = ? ";
					// 绑定问题类型参数
					param.add(json.getString("isbzquestion"));
				}
			}

			if (className.equals("QuestionDAO.select.2")) {
				JSONObject json = (JSONObject) condition;
				// 查询满足条件的数据源
				if (GetConfigValue.isMySQL) {
					sql = "(select b.* from (select a.* from "
							+ "(select * from similarquestion where kbdataid=? ";
				} else {
					sql = "(select b.*,rownum rn from (select a.*, rownum rn from "
							+ "(select * from similarquestion where kbdataid=? ";
				}

				if (json.get("question") != null
						&& !"".equals(json.get("question"))) {
					// 添加对问题的like查询
					sql += " and question like ? ";
				}

				// 判断标准问题是否为空且不为null
				if (json.get("isbzquestion") != null
						&& !"".equals(json.get("isbzquestion"))) {
					// 添加对问题类型的查询
					sql += "and questiontype = ? ";
				}

				if (GetConfigValue.isMySQL) {
					// limit a,b; 效果等价以(a, b]
					sql += " order by questionid desc) a) b) limit ?,?";
				} else {
					sql += " order by questionid desc)a)b where rn>=? and rn<=?)";
				}

			}
		}

		try {
			if (null == param) {
				result = Database.executeQueryReport(sql);
			} else {
				result = Database.executeQueryReport(sql, param.toArray());
			}

		} finally {
			;
		}

		return result;
	}

	/**
	 * @description
	 * @param classify
	 * @param className
	 * @param param
	 * @return
	 * @throws SQLException
	 */
	public String getSql(String classify, String className) {
		if (null == className) {
			throw new NullPointerException("The second param is null!!!");
		}

		// 设置连接数据库标识
		// setMySQL();
		// 操作SQL
		String sql = "";

		// SQL指定类型判断
		if (null == classify) {
			if (className.equals("MyUtil.LogSql")) {
				if (GetConfigValue.isMySQL) {
					sql = "insert into operationlog(ip,brand,service,operation,city,workerid,"
							+ "workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,current_timestamp)";
				} else {
					sql = "insert into operationlog(ip,brand,service,operation,city,workerid,"
							+ "workername,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)";
				}
			}

			// QuestionDAO.java类中update()方法数据层相关操作SQL
			if (className.equals("QuestionDAO.update")) {
				// 定义更新相似问题的SQL语句
				sql = "update similarquestion set question = ? ,questiontype =? where questionid = ?";
			}

			// QuestionDAO.java类中delete()方法数据层相关操作SQL
			if (className.equals("QuestionDAO.delete.1")) {
				// 定义删除相似问题的SQL语句
				sql = "delete from similarquestion where questionid=?";
			}
			if (className.equals("QuestionDAO.delete.2")) {
				// 删除原有的词模
				sql = "delete from wordpat where wordpat like ? and city=? and "
						+ "autosendswitch=? and wordpattype=? and kbdataid=? and brand=?";
			}

			// QuestionDao.java类中insert()方法数据层相关操作SQL
			if (className.equals("QuestionDao.insert.1")) {
				// 数据库分类
				if (GetConfigValue.isMySQL) {
					// 插入相似问题的SQL语句
					sql = "insert into similarquestion(kbdataid,kbdata,questionid,question,"
							+ "remark,time,questiontype) values (?,?,"
							+ ConstructSerialNum.getSerialID("similarquestion",
									"questionid") + ",?,null,now(),?)";
				} else {
					// 插入相似问题的SQL语句
					sql = "insert into similarquestion(kbdataid,kbdata,questionid,question,"
							+ "remark,time,questiontype) values (?,?,similarquestion_sequence.nextval,?,'',sysdate,?)";
				}
			}
			if (className.equals("QuestionDao.insert.2")) {
				// 新增功能，在保存问题时，调用接口生成一个词模，并存入词模表中
				// 删除原有的词模
				sql = "delete from wordpat where wordpat like ? and city=? and autosendswitch=? "
						+ "and wordpattype=? and kbdataid=? and brand=?";
			}
			if (className.equals("QuestionDao.insert.3")) {
				if (GetConfigValue.isMySQL) {
					// 定义插入词模的SQL语句
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,"
							+ "kbdataid,brand,edittime,simplewordpat) values(?,?,?,?,?,?,?,now(),?)";
				} else {
					// 定义插入词模的SQL语句
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,"
							+ "kbdataid,brand,edittime,simplewordpat) values(?,?,?,?,?,?,?,sysdate,?)";

				}
			}
			if (className.equals("QuestionDao.insert.4")) {
				// 插入wordpatprecision的SQL语句
				sql = "insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,"
						+ "wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0 )";
			}

			// QuestionDao.java类中batchProductWordpat()方法数据层相关操作SQL
			if (className.equals("QuestionDAO.batchProductWordpat.1")) {
				// 删除原有的词模的SQL语句
				sql = "delete from wordpat where wordpat like ? and city=? and autosendswitch=? "
						+ "and wordpattype=? and kbdataid=? and brand=?";

			}
			if (className.equals("QuestionDAO.batchProductWordpat.2")) {
				// 插入词模的SQL语句
				if (GetConfigValue.isMySQL) {
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,"
							+ "kbdataid,brand,edittime,simplewordpat) values(?,?,?,?,?,?,?,now(),?)";
				} else {
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,"
							+ "kbdataid,brand,edittime,simplewordpat) values(?,?,?,?,?,?,?,sysdate,?)";
				}
			}
			if (className.equals("QuestionDAO.batchProductWordpat.3")) {
				// 插入wordpatprecision的SQL语句
				sql = "insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,"
						+ "wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0 )";

			}

			// QuestionDao.java类中updateWordpatt()方法数据层相关操作SQL
			if (className.equals("QuestionDAO.updateWordpat.1")) {
				// 删除原有的词模的SQL语句
				sql = "delete from wordpat where wordpat like ? and city=? and autosendswitch=? "
						+ "and wordpattype=? and kbdataid=? and brand=?";
			}
			if (className.equals("QuestionDAO.updateWordpat.2")) {
				// 插入词模的SQL语句
				if (GetConfigValue.isMySQL) {
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,"
							+ "kbdataid,brand,edittime,simplewordpat) values(?,?,?,?,?,?,?,now(),?)";
				} else {
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,"
							+ "kbdataid,brand,edittime,simplewordpat) values(?,?,?,?,?,?,?,sysdate,?)";
				}
			}
			if (className.equals("QuestionDAO.updateWordpat.3")) {
				// 插入wordpatprecision的SQL语句
				sql = "insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,"
						+ "wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0 )";

			}
		}

		return sql;
	}

	// public boolean getMySQL() {
	// return mySQL;
	// }
	// public void setMySQL() {
	// String isNot = Database.getCommmonLibJDBCValues("connectFrom");
	// if(null != isNot && isNot.equals("mysql")){
	// this.mySQL = true;
	// }else{
	// this.mySQL = false;
	// }
	// }
}
