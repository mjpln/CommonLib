/**
 * 
 */
package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.dal.Database;

/**
 *描述：相似问题表操作类
 * 
 * @author: qianlei
 *@date： 日期：2015-11-24 时间：下午03:54:30
 */
public class SimilarquestionDAO {

	/**
	 * 
	 *描述：根据摘要、问题类型查询相似问题
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-24 时间：下午04:00:46
	 *@param kbdataid
	 *            摘要ID，可为空
	 *@param questiontype
	 *            问题类型（标准问题，普通问题）为空时不做查询限制，可为空
	 *@return Result
	 */
	public static Result select(String kbdataid, String questionType) {
		try {
			String sql = "select * from similarquestion where kbdata is not null";
			if(kbdataid!=null)
			{
				sql+=" and kbdataid="+kbdataid;
			}
			if (questionType != null && questionType.length()>0) {
				sql += " and questiontype='" + questionType + "'";
			}
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				return rs;
			} else {
				return null;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return null;
		}
	}
}
