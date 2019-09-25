package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.lang.StringUtils;

import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

/**
 * 新词信息表
 * 
 * @author sundj
 *
 */
public class CommonLibNewWordInfoDAO {

	/**
	 * 增加新词
	 * 
	 * @param list
	 * @return
	 */
	public static int insertNewWordInfo(List<List<String>> list, String userid) {
		String sql = "";
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 遍历所有分词
		for (int i = 0; i < list.size(); i++) {
			List<String> tempList = list.get(i);
			String businessid = tempList.get(0);
			String newWord = tempList.get(1);
			String wordclassid = tempList.get(2);
			String wordpatId = tempList.get(3);
			String isserviceword = tempList.get(4);
			sql = "delete from newwordinfo where newword = ? and businessid = ?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			lstpara.add(newWord);
			lstpara.add(businessid);
			// 将删除新词的SQL放入sql集合中
			lstSql.add(sql);
			// 将对应的参数集合放入集合中
			lstLstpara.add(lstpara);
			// 文件日志
			GlobalValue.myLog.info(userid + "#" + sql + "#" + lstpara);
			// 获取插入词模的序列
			if (GetConfigValue.isOracle) {
				// 定义新增模板的SQL语句
				sql = "insert into newwordinfo(businessid,newword,wordclassid,wordpatid,isserviceword,time) values(?,?,?,?,?,sysdate)";
			} else if (GetConfigValue.isMySQL) {
				// 定义新增模板的SQL语句
				sql = "insert into newwordinfo(businessid,newword,wordclassid,wordpatid,isserviceword,time) values(?,?,?,?,?,sysdate())";
			}
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			lstpara.add(businessid);
			lstpara.add(newWord);
			lstpara.add(wordclassid);
			lstpara.add(wordpatId);
			lstpara.add(isserviceword);
			// 将删除新词的SQL放入sql集合中
			lstSql.add(sql);
			// 将对应的参数集合放入集合中
			lstLstpara.add(lstpara);
			// 文件日志
			GlobalValue.myLog.info(userid + "#" + sql + "#" + lstpara);

		}
		return Database.executeNonQueryTransaction(lstSql, lstLstpara);

	}

	/**
	 * 查询新词
	 * 
	 * @param serviceType
	 * @param isserviceword
	 * @return
	 */
	public static Result selectNewWordInfo(String serviceType, String isserviceword) {
		String sql = "select n.businessid,n.newword,n.wordclassid,n.wordpatid,n.isserviceword from newwordinfo n where n.businessid = '"+serviceType+"'";
		if(StringUtils.isNotBlank(isserviceword)){
			sql += " and n.isserviceword="+isserviceword;
		}
		sql += "and  exists(select w.wordclassid from wordclass w where w.wordclassid =n.wordclassid)";
		sql += " order by n.time desc";

		return Database.executeQuery(sql);

	}

}
