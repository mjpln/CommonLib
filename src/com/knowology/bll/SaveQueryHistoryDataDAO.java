/**
 * 
 */
package com.knowology.bll;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.IQueryHistory;
import com.knowology.DataLayer.DbLogic;
import com.knowology.DbDAO.DBValueOper;
import com.knowology.UtilityOperate.DateTimeOper;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-11-29 时间：下午02:50:27
 */
public class SaveQueryHistoryDataDAO {

	/**
	 *方法名称：CreateHistoryTable 内容摘要：检查数据库中是否存在NLP咨询历史表，该表是按月份进行分类的
	 * 
	 *@Author：zhanggang
	 *@Param：
	 *@Return：
	 *@Throws：
	 */
	@Deprecated
	public static void CreateHistoryTable() {
		String sql = "";
		try {
			if (GetConfigValue.isMySQL) {
				// 检查当前表是否存在，如不存在则创建
				sql = "SELECT count(*) FROM information_schema.TABLES WHERE table_name='QUERYHISTORYLOG';";
			} else if(GetConfigValue.isOracle) {
				sql = "select count(*) cnt from user_tables where table_name = 'QUERYHISTORYLOG'";
			}

			Result rs = Database.executeQuery(sql);
			if (rs==null || rs.getRowCount()==0) {
				GlobalValue.myLog.error("====>> CreateHistoryTable中执行：" + sql + "失败！");
				return;
			}
			String c=DBValueOper.GetValidateStringObj4Null(rs.getRowsByIndex()[0][0]);
//			if(!c.equals(0))
			if(!NewEquals.equals(c,"0"))
			{
				return;
			}
			if (GetConfigValue.isMySQL) {
				sql = "  CREATE TABLE QUERYHISTORYLOG ";
				sql += "(LOGSOURCE VARCHAR(500) NOT NULL,";
				sql += "ID int AUTO_INCREMENT,";
				sql += "USERID VARCHAR(500) NOT NULL,";
				sql += "QUERY VARCHAR(2000) NOT NULL,";
				sql += "CHANNEL VARCHAR(200) NOT NULL,";
				sql += "SERVICE VARCHAR(500),";
				sql += "ABSTRACT VARCHAR(500),";
				sql += "PARENTSERVICE VARCHAR(200),";
				sql += "TYPE VARCHAR(200),";
				sql += "ANSWER VARCHAR(2000),";
				sql += "STANDOUTPUT VARCHAR(4000),";
				sql += "STARTTIME DATETIME,";
				sql += "ENDTIME DATETIME,";
				sql += "ISENDSCENARIOS VARCHAR(20),";
				sql += "NLPSERVER VARCHAR(50),";
				sql += "ismatched VARCHAR(50),";
				sql += "STANDINPUT VARCHAR(2000),";
				sql += "STATISTICALINFORMATION VARCHAR(2000),";
				sql += " PRIMARY KEY(ID)";
				sql += ")";
			} else if(GetConfigValue.isOracle) {
				sql = "  CREATE TABLE QUERYHISTORYLOG ";
				sql += "(\"LOGSOURCE\" VARCHAR2(500 CHAR) NOT NULL ENABLE, ";
				sql += "\"ID\" NUMBER NOT NULL ENABLE, ";
				sql += "\"USERID\" VARCHAR2(500 CHAR) NOT NULL ENABLE, ";
				sql += "\"QUERY\" VARCHAR2(2000 CHAR) NOT NULL ENABLE, ";
				sql += "\"CHANNEL\" VARCHAR2(200 CHAR) NOT NULL ENABLE, ";
				sql += "\"SERVICE\" VARCHAR2(500 CHAR), ";
				sql += "\"ABSTRACT\" VARCHAR2(500 CHAR), ";
				sql += "\"PARENTSERVICE\" VARCHAR2(200 CHAR), ";
				sql += "\"TYPE\" VARCHAR2(200 CHAR), ";
				sql += "\"ANSWER\" VARCHAR2(2000 CHAR), ";
				sql += "\"STANDOUTPUT\" CLOB, ";
				sql += "\"STARTTIME\" TIMESTAMP (6), ";
				sql += "\"ENDTIME\" TIMESTAMP (6), ";
				sql += "\"ISENDSCENARIOS\" VARCHAR2(20 CHAR), ";
				sql += "\"NLPSERVER\" VARCHAR2(50 CHAR),";
				sql += "\"ismatched\" VARCHAR2(50 CHAR),";
				sql += "\"STANDINPUT\" VARCHAR2(2000 CHAR),";
				sql += "\"STATISTICALINFORMATION\" VARCHAR2(2000 CHAR)";
				sql += ")";
			}
			Database.executeNonQuery(sql);
		} catch (Exception ex) {
			GlobalValue.myLog.error("====>> Error：咨询历史中执行：" + sql + "出错 ->" + ex);
			return;
		}
	}
	
	/**
	 *方法名称：SaveHistory 内容摘要：将接口测试的咨询历史信息存入数据库
	 * 
	 *@Author：zhanggang
	 *@Param：hr
	 *@Return：
	 *@Throws：SQLException
	 * 
	 */
	public static void SaveOneReulst(IQueryHistory hr) throws SQLException {
		if (hr == null)
			return;

		// 修正咨询和返回结果中的单引号问题，防止引起sql插入异常
		String logsource = hr.getLogsource();
		String phone = hr.getPhone();
		String query = hr.getQuery();
		String channel = hr.getChannel();
		String serv = hr.getService();
		String abstr = hr.getAbstr();
		String parSer = hr.getParentservice();
		String type = hr.getType();
		String answer = hr.getAnswer();
		String standoutput = hr.getOutputstr();
		String startTime = hr.getStartTime();
		if (startTime.endsWith(".000"))
			startTime = startTime.substring(0, startTime.length() - 4);
		String isEndScenarios = hr.getIsEndScenarios();
		String nlpserver = hr.getNlpserver();

		String ismatched = hr.getIsmatched();
		String standinput = hr.getQueryObject();
		String staInfs = hr.getStatisticalInformation();

		if (query.contains("'"))
			query = query.replace("'", "''");
		if (answer.contains("'"))
			answer = answer.replace("'", "''");
		if (answer.length() > 1000)
			answer = answer.substring(0, 200);
		if (standoutput.contains("'"))
			standoutput = standoutput.replace("'", "''");

		List<Object> listParams=new ArrayList<Object>();
		listParams.add(logsource);
		listParams.add(phone);
		listParams.add(query);
		listParams.add(channel);
		listParams.add(serv);
		listParams.add(abstr);
		listParams.add(parSer);
		listParams.add(type);
		listParams.add(answer);
		listParams.add(standoutput);
		listParams.add(startTime.replace("/", "-"));
		listParams.add(DateTimeOper.getDateTimeByFormat());
		listParams.add(isEndScenarios);
		listParams.add(nlpserver);
		listParams.add(ismatched);
		listParams.add(standinput);
		listParams.add(staInfs);
		DbLogic dbLogic =new DbLogic();
		try {
			dbLogic.update(null, "SaveQueryHistoryData", listParams);
		} catch (Exception e) {
			GlobalValue.myLog.error("【咨询历史保存失败】"+e.toString());
		}	
	}
}
