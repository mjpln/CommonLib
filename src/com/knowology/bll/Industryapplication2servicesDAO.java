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
 *描述：行业应用信息配置表操作类
 * 
 * @author: qianlei
 *@date： 日期：2015-10-21 时间：下午03:50:51
 */
public class Industryapplication2servicesDAO {
	@Deprecated
	/**
	 * 
	 *描述：分页查询行业应用配置信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-21 时间：下午04:12:29
	 *@param page
	 *            页数
	 *@param rows
	 *            单页行数
	 *@param industry
	 *            行业，用于模糊查询，可为null
	 *@param organization
	 *            组织，用于模糊查询，可为null
	 *@param application
	 *            应用，用于模糊查询，可为null
	 *@return Result (m_industryapplication2services.*)
	 */
	public static Result select4Paging(int page, int rows, String industry,
			String organization, String application) {
		try {
			String sql = "select * from m_industryapplication2services";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			StringBuilder paramSql = additionSelectCondition(lstpara, industry,
					organization, application);
			int start = (page - 1) * rows;
			sql += paramSql.toString() + "  LIMIT " + start + "," + rows;
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
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
	 *描述：查询行业应用配置信息(不分页)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-21 时间：下午04:15:09
	 *@param industry
	 *            行业，用于模糊查询，可为null
	 *@param organization
	 *            组织，用于模糊查询，可为null
	 *@param application
	 *            应用，用于模糊查询，可为null
	 *@return Result (m_industryapplication2services.*)
	 */
	public static Result select(String industry, String organization,
			String application) {
		try {
			String sql = "select * from m_industryapplication2services";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			StringBuilder paramSql = additionSelectCondition(lstpara, industry,
					organization, application);
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql + paramSql.toString(),
					lstpara.toArray());
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

	@Deprecated
	/**
	 * 
	 *描述：查询满足条件的配置信息条数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-21 时间：下午04:17:31
	 *@param industry
	 *            行业，用于模糊查询，可为null
	 *@param organization
	 *            组织，用于模糊查询，可为null
	 *@param application
	 *            应用，用于模糊查询，可为null
	 *@return Integer
	 */
	public static Integer selectCount(String industry, String organization,
			String application) {
		try {
			String sql = "select count(*) c from m_industryapplication2services";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			StringBuilder paramSql = additionSelectCondition(lstpara, industry,
					organization, application);
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql + paramSql.toString(),
					lstpara.toArray());
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
	 *描述：词条查询条件SQL生成方法
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-21 时间：下午04:20:20
	 *@param lstpara
	 *            SQL参数LISR
	 *@param industry
	 *            行业，用于模糊查询，可为null
	 *@param organization
	 *            组织，用于模糊查询，可为null
	 *@param application
	 *            应用，用于模糊查询，可为null
	 *@return StringBuilder
	 */
	private static StringBuilder additionSelectCondition(List<String> lstpara,
			String industry, String organization, String application) {
		StringBuilder paramSql = new StringBuilder();
		if (industry != null || organization != null || application != null) {
			paramSql.append(" where ");
			if (industry != null) {
				paramSql.append(" industry like ?  ");
				lstpara.add("%" + industry + "%");
			}
			if (organization != null) {
				paramSql.append(" organization like ?  ");
				lstpara.add("%" + organization + "%");
			}
			if (application != null) {
				paramSql.append(" application like ?  ");
				lstpara.add("%" + application + "%");
			}
		}
		return paramSql;
	}

	@Deprecated
	/**
	 * 
	 *描述：判断配置是否存在
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-21 时间：下午04:30:09
	 *@param industry
	 *            行业，不为null
	 *@param organization
	 *            组织，不为null
	 *@param application
	 *            应用，不为null
	 *@return Boolean
	 */
	public static Boolean exists(String industry, String organization,
			String application) {
		try {
			if (industry != null && organization != null && application != null) {
				String sql = "SELECT * FROM  m_industryapplication2services where industry=? and organization=? and application=?";
				// 定义绑定参数集合
				List<String> lstpara = new ArrayList<String>();
				lstpara.add(industry);
				lstpara.add(organization);
				lstpara.add(application);
				Result rs = Database.executeQuery(sql, lstpara.toArray());
				// 判断数据源不为null且含有数据
				if (rs != null && rs.getRowCount() > 0) {
					// 有数据，表示重复
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	@Deprecated
	/**
	 * 
	 *描述：新增一条配置
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-21 时间：下午04:38:08
	 *@param industry
	 *            行业，不为null
	 *@param organization
	 *            组织，不为null
	 *@param application
	 *            应用，不为null
	 *@param initkbConfig
	 *            NLP初始化配置，可为null
	 *@param analyzeConfig
	 *            NLP分析配置，可为null
	 *@return Integer
	 */
	public static Integer insert(String industry, String organization,
			String application, String initkbConfig, String analyzeConfig) {
		try {
			if (industry != null && organization != null && application != null) {
				if (exists(industry, organization, application)) {
					return 0;
				}
				String sql = "insert into m_industryapplication2services(industry,organization,application,initkbConfig,analyzeConfig) values(?,?,?,?,?)";
				// 定义绑定参数集合
				List<String> lstpara = new ArrayList<String>();
				lstpara.add(industry);
				lstpara.add(organization);
				lstpara.add(application);
				if (initkbConfig != null) {
					lstpara.add(initkbConfig);
				} else {
					lstpara.add("");
				}
				if (analyzeConfig != null) {
					lstpara.add(analyzeConfig);
				} else {
					lstpara.add("");
				}
				return Database.executeNonQuery(sql, lstpara.toArray());
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}
}
