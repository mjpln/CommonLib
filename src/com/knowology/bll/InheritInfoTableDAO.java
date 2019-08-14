/**
 * 
 */
package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.UtilityOperate.StringOper;
import com.knowology.dal.Database;

/**
 *描述：继承信息表操作类 继承信息表和产品业务信息表是同一张表，这里存放特有的操作方法函数
 * 
 * @author: qianlei
 *@date： 日期：2016-1-5 时间：下午04:13:31
 */
public class InheritInfoTableDAO {

	/**
	 * 
	 *描述：查询继承信息表中该业务下所有问题元素的信息（不分页）
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:29:45
	 *@param kbdataid
	 *            可以为空
	 *@param name
	 *            用于模糊查询
	 *@return Result (serviceattrname2colnum.*,wordclass.wordclass)
	 */
	public static Result selectInheritInfoAttrName(String kbdataid, String name) {
		try {
			String sql = "SELECT s.*,(SELECT wordclass FROM wordclass WHERE wordclassid=s.wordclassid) wordclass FROM serviceattrname2colnum s WHERE s.abstractid is not null";
			if (kbdataid != null && kbdataid.length() > 0) {
				sql += " and abstractid=" + kbdataid;
			}
			if (name != null && name.length() > 0) {
				sql += " AND s.`name` LIKE '%" + name + "%'";
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

	/**
	 * 
	 *描述：查询继承信息（不分页）
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-23 时间：下午01:59:39
	 *@param condition
	 *            查询参数
	 *@param selectStr
	 *            查询目标SQL
	 *@return Result (serviceorproductinfo.selectSTR)
	 */
	public static Result selectInheritInfo(Map<String, String> condition,
			String selectStr) {
		try {
			String sql = "select * from serviceorproductinfo ";
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 增加查询条件
			if (condition != null && condition.size() > 0) {
				sql += " where ";
				for (String key : condition.keySet()) {
					String value = condition.get(key);
					String sqlTemp = "";
					if (key.startsWith("attr")) {
						if (value.contains("<or>")) {
							ArrayList<String> values = StringOper.StringSplit(
									value, "<or>");
							for (String ss : values) {
								if (ss.contains("%")) {
									sqlTemp += key + " like ?  ";
								} else {
									sqlTemp += key + "=?  ";
								}
								sqlTemp += " or ";
								lstpara.add(ss);
							}
							sql += "(" + sqlTemp + key + " ='all') and ";
						}if(value.isEmpty()){//cc添加 用于处理值为空的情况
							sqlTemp=key + " is null";
							sql += "(" + sqlTemp + " or " + key
									+ " ='all') and ";
						}else {
							if (value.contains("%")) {
								sqlTemp = key + " like ?  ";
							} else {
								sqlTemp = key + "=?  ";
							}
							lstpara.add(value);
							sql += "(" + sqlTemp + " or " + key
									+ " ='all') and ";
						}
					} else {
						if (value.contains("%")) {
							sqlTemp = key + " like ?  ";
						} else {
							sqlTemp = key + "=?  ";
						}
						lstpara.add(value);
						sql += sqlTemp + " and ";
					}
				}
				if (sql.endsWith("and ")) {
					sql = sql.substring(0, sql.length() - 4);
				}
			}
			sql += " order by SERVICEORPRODUCTINFOID asc";
			// 修改查询目标
			if (selectStr != null && selectStr.length() > 0) {
				sql = sql.replace("*", selectStr);
			}
			// 执行
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());
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
