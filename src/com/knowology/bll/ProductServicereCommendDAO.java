/**
 * 
 */
package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.dal.Database;

/**
 *描述：产品服务推荐信息操作类
 * 
 * @author: qianlei
 *@date： 日期：2015-11-29 时间：上午10:46:44
 */
public class ProductServicereCommendDAO {

	/**
	 * 
	 *描述：查询出已审核的推荐信息
	 *@author: qianlei
	 *@date： 日期：2015-11-29 时间：上午10:50:13
	 *@return Result
	 */
	public static Result select() {
		try {
			String sql = "select id,type,context from productservicerecommend where isvalid=1 order by id";
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
