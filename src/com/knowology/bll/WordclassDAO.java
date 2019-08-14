/**
 * 
 */
package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.dal.Database;

/**
 *描述：
 * 
 * @author: qianlei
 *@date： 日期：2015-9-17 时间：下午03:38:23
 */
public class WordclassDAO {

	/**
	 * 
	 *描述：分页查询词类信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-29 时间：上午10:24:46
	 *@param wordclass
	 *            词类名，用于模糊查询，可以为null
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@param page
	 *            页数
	 *@param rows
	 *            每页行数
	 *@return Result (wordclass.*)
	 */
	public static Result select4Paging(String wordclass, String container,
			int page, int rows) {
		try {
			String sql = "select * from wordclass where container='"
					+ container + "'";
			// 判断词类条件是否为空，null
			if (!"".equals(wordclass) && wordclass != null
					&& wordclass.length() > 0) {
				// 加上词类条件
				sql += " and wordclass like '%" + wordclass + "%'";
			}
			int start = (page - 1) * rows;
			sql += " ORDER BY wordclassid Desc LIMIT " + start + "," + rows;
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
	 *描述：根据词类名查询词类信息(不分页)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-29 时间：上午10:57:51
	 *@param wordClassName
	 *            词类名，用于模糊查询，可以为null
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@return Result (wordclass.*)
	 */
	public static Result select(String wordClassName, String container) {
		try {
			String sql = "select * from wordclass where " + " container='"
					+ container + "'";
			// 判断词类条件是否为空，null
			if (!"".equals(wordClassName) && wordClassName != null
					&& wordClassName.length() > 0) {
				// 加上词类条件
				sql += " and wordclass like '%" + wordClassName + "%'";
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
	 *描述：查询满足条件的词类个数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-29 时间：上午11:00:59
	 *@param wordclass
	 *            词类名，用于模糊查询，可以为null
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@return Integer
	 */
	public static Integer selectCount(String wordclass, String container) {
		try {
			String sql = "select count(*) c from wordclass where container='"
					+ container + "'";
			// 判断词类条件是否为空，null
			if (!"".equals(wordclass) && wordclass != null
					&& wordclass.length() > 0) {
				// 加上词类条件
				sql += " and wordclass like '%" + wordclass + "%'";
			}
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				String count = rs.getRows()[0].get("c").toString();
				return Integer.valueOf(count);
			} else {
				return 0;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：更新词类名
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-29 时间：上午10:44:11
	 *@param wordClassid
	 *@param newvalue
	 *@return Integer
	 */
	public static Integer update(int wordClassid, String newvalue) {
		try {
			if (!exists(newvalue)) {
				String sql = "update wordclass set wordclass='" + newvalue
						+ "', time = sysdate() where wordclassid="
						+ wordClassid;
				return Database.executeNonQuery(sql);
			} else {
				return 0;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：判断词类是否存在
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-29 时间：上午11:01:15
	 *@param wordClass
	 *@return Boolean
	 */
	public static Boolean exists(String wordClass) {
		// 查询词类的SQL语句
		String sql = "select * from wordclass where wordclass= ? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定词类参数
		lstpara.add(wordClass);
		try {
			// 执行SQL语句，返回数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 有重复词类，返回true
				return true;
			} else {
				// 没有重复词类，返回false
				return false;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：多条或单条插入词类
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-29 时间：上午11:16:10
	 *@param lstWordclass
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@return Integer
	 */
	public static Integer insert(List<String> lstWordclass, String container) {
		try {
			if (lstWordclass != null && lstWordclass.size() > 0
					&& container != null) {
				// 定义多条SQL语句集合
				List<String> lstsql = new ArrayList<String>();
				// 定义多条SQL语句对应的绑定参数集合
				List<List<?>> lstlstpara = new ArrayList<List<?>>();
				// 定义SQL语句
				String sql = "";
				// 定义绑定参数集合
				List<String> lstpara = new ArrayList<String>();
				// 循环遍历词类集合
				for (int i = 0; i < lstWordclass.size(); i++) {
					if (exists(lstWordclass.get(i))) {
						// 已存在的词类不添加
						continue;
					}
					// 插入词类的SQL语句
					sql = "insert into wordclass(wordclassid,wordclass,container) values(?,?,?)";
					// 定义绑定参数集合
					lstpara = new ArrayList<String>();
					// 获取词类表的序列值
					int id = ConstructSerialNum.getSerialID("wordclass",
							"wordclassid");
					// 绑定id参数
					lstpara.add(id + "");
					// 绑定词类参数
					lstpara.add(lstWordclass.get(i));
					// 绑定类型参数
					lstpara.add(container);
					// 将SQL语句放入集合中
					lstsql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstlstpara.add(lstpara);
				}
				return Database.executeNonQueryTransaction(lstsql, lstlstpara);
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：删除词类
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-29 时间：上午11:18:36
	 *@param wordClassid
	 *@return Integer
	 */
	public static Integer delete(String wordClassid) {
		try {
			String sql = "delete from wordclass where wordclassid="
					+ wordClassid;
			return Database.executeNonQuery(sql);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：根据词类ID查询词类信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-5 时间：上午11:06:53
	 *@param wordClassid
	 *            词类ID
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@return Result （wordclass.*）
	 */
	public static Result selectByID(String wordclassid, String container) {
		try {
			String sql = "select * from wordclass where " + " container='"
					+ container + "' and wordclassid=" + wordclassid;
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
