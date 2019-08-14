/**
 * 
 */
package com.knowology.bll;

import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

/**
 *描述： 笑话数据查询
 * 
 * @author: qianlei
 *@date： 日期：2015-11-27 时间：上午10:04:40
 */
public class JokeDAO {
	/**
	 * 方法名称： GetJoke 内容摘要：进行数据库表查询，随机得到一条笑话数据返回
	 * 
	 * @author zhanggang
	 * @param
	 * @return
	 * @throws
	 */
	public static String GetJoke(){
		String joke = "";
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select * from(select * from bst_joke order by dbms_random.value) where rownum < 2";
		} else if (GetConfigValue.isMySQL) {
			sql = "SELECT * FROM bst_joke  ORDER BY RAND() LIMIT 1";
		}
		Result dr = Database.executeQuery(sql);
		try {
			for (SortedMap<String, String> sm : dr.getRows()) {
				joke = sm.get("content").toString();
			}
		} catch (Exception e) {
			GlobalValue.myLog.error("查询笑话信息错误:" + e.toString());
		}
		return joke;
	}
}
