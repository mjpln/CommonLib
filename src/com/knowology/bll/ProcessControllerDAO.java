/**
 * 
 */
package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.DbDAO.DBValueOper;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.UtilityOperate.StringOper;
import com.knowology.dal.Database;

/**
 *描述：规则流程表操作类
 * 
 * @author: qianlei
 *@date： 日期：2015-9-17 时间：下午03:07:29
 */
public class ProcessControllerDAO {

	/**
	 * 
	 *描述：分页查询问题要素信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午03:19:02
	 *@param kbdataid
	 *@param kbcontentid
	 *@param name
	 *            用于模糊查询
	 *@param start
	 *            起始条数，从0开始
	 *@param rows
	 *            每页行数
	 *@return Result (queryelement.*,wordclass.wordclass)
	 */
	public static Result selectQueryElement4Paging(String kbdataid,
			String kbcontentid, String name, int start, int rows) {
		try {
			String sql = "select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from queryelement q where q.kbdataid="
					+ kbdataid + " and q.kbcontentid= " + kbcontentid;
			// 定义条件的SQL语句
			StringBuilder paramSql = new StringBuilder();
			if (name != null && name.length() > 0) {
				paramSql.append(" AND name LIKE '%" + name + "%'");
			}
			if (GetConfigValue.isOracle) {
				sql = "select * from (select t.*,rownum rn from (select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from queryelement q where q.kbdataid="
						+ kbdataid + " and q.kbcontentid=" + kbcontentid;
				sql += paramSql + " order by q.weight asc)t) where rn>" + start
						+ " and rn<=" + rows;
			} else {
				sql += paramSql + " order by q.weight asc LIMIT " + start + ","
						+ rows;
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
	 *描述：查询问题要素信息（不分页）
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午03:22:49
	 *@param kbdataid
	 *            可以为null
	 *@param kbcontentid
	 *            可以为null
	 *@param name
	 *            用于模糊查询,可以为null
	 *@return Result (queryelement.*,wordclass.wordclass)
	 */
	public static Result selectQueryElement(String kbdataid,
			String kbcontentid, String name) {
		try {
			String sql = "select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from queryelement q ";
			// 添加查询条件
			ArrayList<String> conditions = new ArrayList<String>();
			if (kbdataid != null && kbdataid.length() > 0) {
				conditions.add(" q.kbdataid=" + kbdataid);
			}
			if (kbcontentid != null && kbcontentid.length() > 0) {
				conditions.add("  q.kbcontentid= " + kbcontentid);
			}

			if (name != null && name.length() > 0) {
				conditions.add("  name LIKE '%" + name + "%'");
			}
			if (conditions.size() > 0) {
				sql += " where ";
				for (String ss : conditions) {
					sql += ss + " and";
				}
				if (sql.endsWith("and")) {
					sql = sql.substring(0, sql.length() - 3);
				}
			}
			// --
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
	 *描述：判断问题要素是否存在
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午03:31:47
	 *@param name
	 *@param kbdataid
	 *@param kbcontentid
	 *@return Boolean
	 */
	public static Boolean isContentElementName(String name, String kbdataid,
			String kbcontentid) {
		try {
			if (name == null || name.length() <= 0) {
				return false;
			}
			String sql = "select count(*) c from queryelement where name='"
					+ name + "' and kbdataid=" + kbdataid + " and kbcontentid="
					+ kbcontentid;
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				String count = rs.getRows()[0].get("c").toString();
				if (!count.equals("0")) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：插入问题要数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午03:52:20
	 *@param name
	 *@param kbdataid
	 *@param kbcontentid
	 *@param weight
	 *@param wordclass
	 *@param abs
	 *@return Integer
	 */
	public static Integer insertQueryElementName(String name, String kbdataid,
			String kbcontentid, String weight, String wordclass) {
		try {
			if (name == null || name.length() == 0) {
				return 0;
			}
			// 判断问题要素名是否重复
			if (isContentElementName(name, kbdataid, kbcontentid)) {
				return 0;
			}
			// 判断词类是否存在并获取词类ID
			String wordclassid = null;
			Result rs1 = WordclassDAO.select(wordclass, "操作");
			if (rs1 != null && rs1.getRowCount() > 0) {
				wordclassid = DBValueOper.GetValidateStringObj4Null(rs1
						.getRows()[0].get("wordclassid"));
			} else {
				return 0;
			}
			String sql1 = "insert into queryelement (queryelementid,name,kbdataid,kbcontentid,wordclassid,weight) values (";
			int queryelementid = ConstructSerialNum.getSerialID("queryelement",
					"queryelementid");
			sql1 += queryelementid + ",'" + name + "'," + kbdataid + ","
					+ kbcontentid + "," + wordclassid + ",'" + weight + "')";
			return Database.executeNonQuery(sql1);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：查询问题要素的总数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午03:58:11
	 *@param name
	 *            用于模糊查询
	 *@param kbdataid
	 *@param kbcontentid
	 *@return Integer
	 */
	public static Integer selectQueryElementCount(String name, String kbdataid,
			String kbcontentid) {
		try {
			String sql = "select count(*) c from queryelement q where q.kbdataid="
					+ kbdataid + " and q.kbcontentid= " + kbcontentid;
			if (name != null && name.length() > 0) {
				sql += " AND name LIKE '%" + name + "%'";
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
	 *描述：删除问题要素，并删除数据和规则中使用到这个问题要素的记录
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午05:13:10
	 *@param kbdataid
	 *@param kbcontentid
	 *@param elementnameid
	 *@param weight
	 *@return Boolean
	 */
	public static Boolean deleteQueryElement(String kbdataid,
			String kbcontentid, String elementnameid, String weight) {
		try {
			String sql1 = "delete from queryelement where queryelementid="
					+ elementnameid;
			String sql2 = "delete from conditioncombtoreturntxt where kbdataid="
					+ kbdataid
					+ " and kbcontentid="
					+ kbcontentid
					+ " and condition" + weight + " is not null";
			String sql3 = "delete from scenerules where kbdataid=" + kbdataid
					+ " and kbcontentid=" + kbcontentid + " and condition"
					+ weight + " is not null";
			ArrayList<String> sqlList = new ArrayList<String>();
			sqlList.add(sql1);
			sqlList.add(sql2);
			sqlList.add(sql3);
			return Database.ExecuteSQL(sqlList);

		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：分页查询满足条件的问题要素值(词条)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午05:17:06
	 *@param curwordclass
	 *            词类名
	 *@param name
	 *            用于模糊查询
	 *@param start
	 *@param rows
	 *@return Result
	 */
	public static Result selectQueryElementValues4Paging(String curwordclass,
			String name, int start, int rows) {
		return CommonLibWordDAO.select(start, rows, name, false, true, null,
				curwordclass, "基础");
	}

	/**
	 * 
	 *描述：查询满足条件的问题要素值(词条,不分页)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午05:20:30
	 *@param curwordclass
	 *            词类名
	 *@return Result
	 */
	public static Result selectQueryElementValues(String curwordclass) {
		return CommonLibWordDAO.select(null, false, true, null, curwordclass,
				"基础");
	}

	/**
	 * 
	 *描述：查询问题要素值的个数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午05:28:29
	 *@param curwordclass
	 *            词类名
	 *@param name
	 *            词条信息，用于模糊查询
	 *@return Integer
	 */
	public static Integer selectQueryElementValuesCount(String curwordclass,
			String name) {
		return CommonLibWordDAO.getWordCount(name, false, true, null,
				curwordclass, "基础");
	}

	/**
	 * 
	 *描述：新增问题要素值(词条)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午05:23:32
	 *@param name
	 *@param wordclassid
	 *@return Integer
	 */
	public static Integer insertQueryElementValue(User user, String name,
			String curwordclass, String curwordclassid) {
		// 新增标准词
		List<String> lstWordItem = new ArrayList<String>();
		lstWordItem.add(name);
		return CommonLibWordDAO.insert(user, curwordclassid, curwordclass, "",
				lstWordItem, "标准词", "基础");
	}

	/**
	 * 
	 *描述：删除问题要素值(词条)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-17 时间：下午05:25:34
	 *@param kbdataid
	 *@param kbcontentid
	 *@param wordItemid
	 *@param weight
	 *@param name
	 *@param wordclass
	 *@return Boolean
	 */
	public static Boolean deleteQueryElementValue(User user, String kbdataid,
			String kbcontentid, String wordItemid, String weight, String name,
			String wordclass) {
		// 删除词条
		CommonLibWordDAO.delete(user, wordItemid, wordclass, "", name, "基础");
		try {
			// 定义多条SQL语句集合
			List<String> lstSql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstLstpara = new ArrayList<List<?>>();

			// 定义更新数据的SQL语句
			String sql = "update conditioncombtoreturntxt set condition"
					+ weight + " = null where condition" + weight
					+ "=? and kbdataid=? and kbcontentid=?";
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 绑定属性值参数
			lstpara.add(name);
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);

			// 定义更新规则的SQL语句
			sql = "update scenerules set condition" + weight
					+ " = null where condition" + weight
					+ "=? and kbdataid=? and kbcontentid=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定属性值参数
			lstpara.add(name);
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 将SQL语句放入集合中
			lstSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstLstpara.add(lstpara);

			// 执行SQL语句，绑定事务，返回事务处理结果
			int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
			if (c > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：查询问题要素和问题要素值的对应数据
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-18 时间：上午11:40:45
	 *@param kbdataid
	 *@param kbcontentid
	 *            可以为null
	 *@return Result (queryelement.name.kbdataid,queryelement.name.kbcontentid
	 *         ,queryelement.name,word.word)
	 */
	public static Result selectQueryElement2Values(String kbdataid,
			String kbcontentid) {
		try {
			// for debug
			String sql = "SELECT queryelement.kbdataid,abstract,name,queryelement.weight,  'xixi' valuename  FROM queryelement,kbdata WHERE  kbdata.kbdataid=queryelement.kbdataid  order by weight asc";
			/*
			 * String sql =
			 * "SELECT queryelement.kbdataid,abstract,name,word valuename,queryelement.weight FROM queryelement,word,kbdata WHERE queryelement.wordclassid=word.wordclassid AND kbdata.kbdataid=queryelement.kbdataid AND (TYPE='标准名称' OR TYPE='普通词')"
			 * ; if (kbdataid != null) { sql += " and q.kbdataid=" + kbdataid; }
			 * if (kbcontentid != null) { sql += " and q.kbcontentid=" +
			 * kbcontentid; } sql += " order by weight asc,word desc";
			 */
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
	 *描述：分页查询问题要素组合数据及答案
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-18 时间：下午04:04:18
	 *@param kbdataid
	 *@param kbcontentid
	 *@param conditions
	 *            用于模糊查询
	 *@param returntxtType
	 *            回复类型
	 *@param status
	 *            是否已审核
	 *@param page
	 *@param rows
	 *@return Result (conditioncombtoreturntxt.*)
	 */
	public static Result selectConditioncomb4Paging(String kbdataid,
			String kbcontentid, String[] conditions, String returntxtType,
			String status, int page, int rows) {
		try {
			String sql = "select * from conditioncombtoreturntxt where kbdataid="
					+ kbdataid + " and kbcontentid=" + kbcontentid;
			if (conditions != null && conditions.length > 0) {
				for (int i = 0; i < conditions.length; i++) {
					String ss = conditions[i];
					if (ss != null && ss.length() > 0) {
						sql += " and condition" + (i + 1) + " like '%"
								+ conditions[i] + "%'";// 拼接查询条件
					}
				}
			}
			if (returntxtType != null && returntxtType.length() > 0) {
				sql += " and returntxttype ='" + returntxtType + "'";
			}
			if (status != null && status.length() > 0) {
				sql += " and status ='" + status + "'";
			}
			int start = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
			sql += " order by status asc LIMIT " + start + "," + rows;
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
	 *描述：查询问题要素组合数据及答案（不分页）
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-26 时间：下午03:05:36
	 *@param condition
	 *            查询条件Mao
	 *@param selectStr
	 *            查询目标串
	 *@return Result
	 */
	public static Result selectConditioncomb(Map<String, String> condition,
			String selectStr) {
		try {
			String sql = "select * from conditioncombtoreturntxt";
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 增加查询条件
			if (condition != null && condition.size() > 0) {
				sql += " where ";
				for (String key : condition.keySet()) {
					String sqlTemp = "";
					String value = condition.get(key);
					sqlTemp = key + "=? ";
					if (key.startsWith("condition")) {
						if (value.length() > 0) {
							// 问题元素列作为条件值，判段需要增加为空的情况
							sqlTemp = "(" + sqlTemp + " or " + key
									+ " is null)";
						} else {
							continue;
						}
					}
					lstpara.add(value);
					sql += sqlTemp + " and ";
				}
				if (sql.endsWith("and ")) {
					sql = sql.substring(0, sql.length() - 4);
				}
			}
			// --
			sql += " order by COMBITIONID asc";
			// 修改查询目标
			if (selectStr != null && selectStr.length() > 0) {
				sql = sql.replace("*", selectStr);
			}
			// 执行
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
	 *描述：查询符合条件的问题要素组合数据总数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-18 时间：下午05:17:40
	 *@param kbdataid
	 *@param kbcontentid
	 *@param conditions
	 *@param returntxtType
	 *@param status
	 *@return Integer
	 */
	public static Integer selectConditioncombCount(String kbdataid,
			String kbcontentid, String[] conditions, String returntxtType,
			String status) {
		try {
			String sql = "select count(*) c from conditioncombtoreturntxt where kbdataid="
					+ kbdataid + " and kbcontentid=" + kbcontentid;
			if (conditions != null && conditions.length > 0) {
				for (int i = 0; i < conditions.length; i++) {
					String ss = conditions[i];
					if (ss != null && ss.length() > 0) {
						sql += " and condition" + (i + 1) + " = '" + ss + "'";
					}
				}
			}
			if (returntxtType != null && returntxtType.length() > 0) {
				sql += " and returntxttype ='" + returntxtType + "'";
			}
			if (status != null && status.length() > 0) {
				sql += " and status ='" + status + "'";
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
	 *描述：问题要素组合是否已存在
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-20 时间：下午03:21:10
	 *@param kbdataid
	 *@param kbcontentid
	 *@param conditions
	 *@param returntxtType
	 *@param returntxt
	 *@param abs
	 *@return Boolean
	 */
	public static Boolean isContentConditioncomb(String kbdataid,
			String kbcontentid, String[] conditions, String returntxtType,
			String returntxt) {
		try {
			String sql = "select * from conditioncombtoreturntxt where kbdataid="
					+ kbdataid + " and kbcontentid=" + kbcontentid;
			if (conditions != null && conditions.length > 0) {
				for (int i = 0; i < conditions.length; i++) {
					String ss = conditions[i];
					if (ss != null) {
						sql += " and condition" + (i + 1) + "='" + ss + "' ";
					} else {
						sql += " and condition" + (i + 1) + " is null";
					}
				}
			}
			sql += " and returntxttype=" + returntxtType + " and returntxt='"
					+ returntxt + "'";
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：新增问题要数组合
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-20 时间：下午03:45:04
	 *@param kbdataid
	 *            不为null
	 *@param kbcontentid
	 *            不为null
	 *@param conditions
	 *@param returntxtType
	 *            不为null
	 *@param returntxt
	 *@return Integer
	 */
	public static Integer insertConditioncomb(String kbdataid,
			String kbcontentid, String[] conditions, String returntxtType,
			String returntxt) {
		try {
			// 数据有效性验证
			if (kbdataid != null && returntxtType != null
					&& kbcontentid != null) {
				if (!isContentConditioncomb(kbdataid, kbcontentid, conditions,
						returntxtType, returntxt)) {
					String sql = "insert into conditioncombtoreturntxt (combitionid,kbdataid,kbcontentid,";
					// 定义绑定参数集合
					List<Object> lstpara = new ArrayList<Object>();
					// 获取主键
					Integer combitionid = ConstructSerialNum.getSerialID(
							"conditioncombtoreturntxt", "combitionid");
					// 填入参数
					lstpara.add(combitionid);
					lstpara.add(kbdataid);
					lstpara.add(kbcontentid);
					if (conditions != null && conditions.length > 0) {
						for (int i = 0; i < conditions.length; i++) {
							sql += "condition" + (i + 1) + ",";// 拼接SQL
							lstpara.add(conditions[i]);// 填入参数
						}
					}
					// 根据参数个数拼接SQL中的"?"
					sql += "status,returntxttype,returntxt) values (?,?,?,";
					for (int i = 3; i < lstpara.size(); i++) {
						sql += "?,";
					}
					lstpara.add(0);
					lstpara.add(returntxtType);
					lstpara.add(returntxt);
					sql += "?,?,?)";
					// --
					return Database.executeNonQuery(sql.toString(), lstpara
							.toArray());
				}
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：删除单条或多条问题要数组合
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-20 时间：下午03:56:08
	 *@param combitionid
	 *@return Integer
	 */
	public static Integer deleteConditioncomb(String[] combitionid) {
		try {
			String sql = "delete from conditioncombtoreturntxt where combitionid in(";
			if (combitionid != null && combitionid.length > 0) {
				for (String id : combitionid) {
					if (id.length() > 0) {
						sql += id + ",";
					}
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1) + ")";
				} else {
					// 参数无效，终止
					return 0;
				}
				return Database.executeNonQuery(sql);
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：全量删除问题要素组合数据
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-20 时间：下午04:00:32
	 *@param kbdataid
	 *@param kbcontentid
	 *@return Integer
	 */
	public static Integer deleteAllConditioncomb(String kbdataid,
			String kbcontentid) {
		try {
			String sql = "delete from conditioncombtoreturntxt where kbdataid="
					+ kbdataid + " and kbcontentid=" + kbcontentid;
			return Database.executeNonQuery(sql);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：单条或多条问题要素组合审核
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-20 时间：下午04:01:31
	 *@param attrid
	 *@return Integer
	 */
	public static Integer confirmConditioncomb(String[] combitionid) {
		try {
			String sql = "update conditioncombtoreturntxt set status=1 where combitionid in (";
			if (combitionid != null && combitionid.length > 0) {
				for (String id : combitionid) {
					if (id.length() > 0) {
						sql += id + ",";
					}
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1) + ")";
				} else {
					// 参数无效，终止
					return 0;
				}
				return Database.executeNonQuery(sql);
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：全量确认所有问题要素组合
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-20 时间：下午04:05:17
	 *@param kbdataid
	 *@param kbcontentid
	 *@return Integer
	 */
	public static Integer confirmAllConditionComb(String kbdataid,
			String kbcontentid) {
		try {
			String sql = "update conditioncombtoreturntxt set status=1 where kbdataid="
					+ kbdataid + " and kbcontentid=" + kbcontentid;
			return Database.executeNonQuery(sql);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：更新问题要素组合数据
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-20 时间：下午04:15:08
	 *@param kbdataid
	 *            不为空
	 *@param kbcontentid
	 *            不为空
	 *@param conditions
	 *@param returntxttype
	 *            不为空
	 *@param returntxt
	 *@param combitionid
	 *@return Integer
	 */
	public static Integer updateConditionComb(String kbdataid,
			String kbcontentid, String[] conditions, String returntxttype,
			String returntxt, String combitionid) {
		try {
			// 数据有效性验证
			if (kbdataid.length() > 0 && returntxttype.length() > 0
					&& kbcontentid.length() > 0) {
				if (!isContentConditioncomb(kbdataid, kbcontentid, conditions,
						returntxttype, returntxt)) {
					String sql = "update conditioncombtoreturntxt set";
					for (int i = 0; i < conditions.length; i++) {
						String value = conditions[i];
						sql += " condition" + (i + 1) + "='" + value + "',";
					}
					sql += " status=0,returntxttype=" + returntxttype
							+ ",returntxt='" + returntxt
							+ "' where combitionid=" + combitionid;
					return Database.executeNonQuery(sql);
				}
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：判断规则是否存在
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-20 时间：下午04:44:24
	 *@param kbdataid
	 *@param kbcontentid
	 *@param conditions
	 *@param weight
	 *@param ruletype
	 *@param ruleresponse
	 *@return Boolean
	 */
	public static Boolean isContentSceneRules(String kbdataid,
			String kbcontentid, String[] conditions, String weight,
			String ruletype, String ruleresponse) {
		try {
			String sql = "select * from scenerules where kbdataid=" + kbdataid
					+ " and kbcontentid=" + kbcontentid;
			if (conditions != null && conditions.length > 0) {
				for (int i = 0; i < conditions.length; i++) {
					String ss = conditions[i];
					if (ss != null) {
						sql += " and condition" + (i + 1) + "='" + ss + "' ";
					} else {
						sql += " and condition" + (i + 1) + " is null";
					}
				}
			}
			sql += " and ruletype=" + ruletype + " and weight=" + weight;
			Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-20 时间：下午04:31:42
	 *@param kbdataid
	 *            不为null
	 *@param kbcontentid
	 *            不为null
	 *@param conditions
	 *            总数10
	 *@param weight
	 *            不为null
	 *@param ruletype
	 *            不为null
	 *@param ruleresponse
	 *@return Integer
	 */
	public static Integer insertSceneRules(String kbdataid, String kbcontentid,
			String[] conditions, String weight, String ruletype,
			String ruleresponse) {
		try {
			// 验证数据完整性
			if (kbdataid != null && kbcontentid != null && weight != null
					&& ruletype != null) {
				if (!isContentSceneRules(kbdataid, kbcontentid, conditions,
						weight, ruletype, ruleresponse)) {
					String sql = "insert into scenerules (ruleid,kbdataid,kbcontentid,";
					// 定义绑定参数集合
					List<Object> lstpara = new ArrayList<Object>();
					// 获取主键
					Integer combitionid = ConstructSerialNum.getSerialID(
							"scenerules", "ruleid");
					// 填入参数
					lstpara.add(combitionid);
					lstpara.add(kbdataid);
					lstpara.add(kbcontentid);
					if (conditions != null && conditions.length > 0) {
						for (int i = 0; i < conditions.length; i++) {
							sql += "condition" + (i + 1) + ",";// 拼接SQL
							lstpara.add(conditions[i]);// 填入参数
						}
					}
					// 根据参数个数拼接SQL中的"?"
					sql += "ruletype,ruleresponse,weight)values(?,?,?,";
					for (int i = 3; i < lstpara.size(); i++) {
						sql += "?,";
					}
					lstpara.add(ruletype);
					lstpara.add(ruleresponse);
					lstpara.add(weight);
					sql += "?,?,?)";
					// --
					return Database.executeNonQuery(sql.toString(), lstpara
							.toArray());
				}
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：查询规则总数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-21 时间：上午10:31:24
	 *@param kbdataid
	 *@param kbcontentid
	 *@param conditions
	 *            用于模糊查询
	 *@param ruletype
	 *@param weight
	 *@return Integer
	 */
	public static Integer selectSceneRulesCount(String kbdataid,
			String kbcontentid, String[] conditions, String ruletype,
			String weight) {
		try {
			String sql = "select count(*) c from scenerules where kbdataid="
					+ kbdataid + " and kbcontentid=" + kbcontentid;
			if (conditions != null && conditions.length > 0) {
				for (int i = 0; i < conditions.length; i++) {
					sql += " and condition" + (i + 1) + " like '%"
							+ conditions[i] + "%'";// 拼接查询条件
				}
			}
			if (ruletype != null && ruletype.length() > 0) {
				sql += " and ruletype =" + ruletype;
			}
			if (weight != null && weight.length() > 0) {
				sql += " and weight =" + weight;
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
	 *描述：分页查询场景规则
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-21 时间：上午10:44:07
	 *@param kbdataid
	 *@param kbcontentid
	 *@param conditions
	 *            用于模糊查询(10个)
	 *@param ruletype
	 *@param weight
	 *@param page
	 *@param rows
	 *@return Result
	 */
	public static Result selectSceneRules4Paging(String kbdataid,
			String kbcontentid, String[] conditions, String ruletype,
			String weight, int page, int rows) {
		try {
			String sql = "select * from scenerules where kbdataid=" + kbdataid
					+ " and kbcontentid=" + kbcontentid + " and ruletype="
					+ ruletype;
			if (conditions != null && conditions.length > 0) {
				for (int i = 0; i < conditions.length; i++) {
					String ss = conditions[i];
					if (ss != null) {
						sql += "and condition" + (i + 1) + " like '%"
								+ conditions[i] + "%'";// 拼接查询条件
					} else {
						sql += "and condition" + (i + 1) + " is null";
					}
				}
			}
			if (weight != null && weight.length() > 0) {
				sql += "and weight =" + weight;
			}
			// 分页
			int start = (Integer.valueOf(page) - 1) * Integer.valueOf(rows);
			sql += " order by weight asc LIMIT " + start + "," + rows;
			// 执行
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
	 *描述：查询场景规则（不分页）
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-29 时间：下午02:22:51
	 *@return Result
	 */
	public static Result selectSceneRules() {
		try {
			String sql = "select s.service,kd.kbdataid,kd.abstract,kc.channel,kc.servicetype,kc.customertype,sr.* from scenerules sr,service s,kbdata kd,kbcontent kc"
					+ " where sr.kbcontentid = kc.kbcontentid and sr.kbdataid = kd.kbdataid and s.serviceid = kd.serviceid  and sr.ruleresponse is not null";
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
	 *描述：查询场景规则
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-7-28 时间：下午05:00:30
	 *@return Result
	 */
	public static Result selectScenarioRules() {
		try {
			// 不加载缺失补全规则
			String sql = "select scenarios.scenariosid,scenarios.servicetype,Scenarios2kbdata.service,scenarios2kbdata.abstractid kbdataid,scenariosrules.* from scenarios,scenariosrules,Scenarios2kbdata where scenariosrules.scenariosid = scenarios.scenariosid and scenarios.scenariosid=Scenarios2kbdata.scenariosid and ruletype!=0 and ruletype!=4";
			// String sql =
			// "select scenarios.scenariosid,scenarios.servicetype,Scenarios2kbdata.service,scenarios2kbdata.abstractid kbdataid,scenariosrules.* from scenarios,scenariosrules,Scenarios2kbdata where scenariosrules.scenariosid = scenarios.scenariosid and scenarios.scenariosid=Scenarios2kbdata.scenariosid";
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
	 *描述：删除一条或多条场景规则
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-21 时间：上午10:47:04
	 *@param ruleid
	 *@return Integer
	 */
	public static Integer deleteSceneRules(String[] ruleid) {
		try {
			String sql = "delete from scenerules where ruleid  in(";
			if (ruleid != null && ruleid.length > 0) {
				for (String id : ruleid) {
					if (id.length() > 0) {
						sql += id + ",";
					}
				}
				if (sql.endsWith(",")) {
					sql = sql.substring(0, sql.length() - 1) + ")";
				} else {
					// 参数无效，终止
					return 0;
				}
				return Database.executeNonQuery(sql);
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：更新一条规则
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-21 时间：上午10:56:25
	 *@param kbdataid
	 *            不为null
	 *@param kbcontentid
	 *            不为null
	 *@param conditions
	 *@param weight
	 *            不为null
	 *@param ruletype
	 *            不为null
	 *@param ruleresponse
	 *@param ruleid
	 *@return Integer
	 */
	public static Integer updateSceneRules(String kbdataid, String kbcontentid,
			String[] conditions, String weight, String ruletype,
			String ruleresponse, String ruleid) {
		try {
			// 数据有效性验证
			if (kbdataid != null && weight != null && kbcontentid != null
					&& ruletype != null) {
				if (!isContentSceneRules(kbdataid, kbcontentid, conditions,
						weight, ruletype, ruleresponse)) {
					String sql = "update scenerules set";
					for (int i = 0; i < conditions.length; i++) {
						String value = conditions[i];
						sql += " condition" + (i + 1) + "='" + value + "',";
					}
					sql += "ruleresponse='" + ruleresponse + "',weight="
							+ weight + " where ruleid=" + ruleid;
					return Database.executeNonQuery(sql);
				}
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：查询场景元素
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-7-27 时间：下午03:25:54
	 *@return Result
	 */
	public static Result selectScenarioEnlements() {
		try {
			String sql = "select scenarios.scenariosid sname,scenarioselement.name ename,scenarioselement.weight,scenarioselement.wordclassid,scenarioselement.city,scenarioselement.itemmode,scenarioselement.container  from scenarios,scenarioselement where scenarioselement.scenariosid = scenarios.scenariosid";
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
	 *描述：查询场景
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-7-28 时间：下午04:39:52
	 *@return Result
	 */
	public static Result selectScenario() {
		try {
			String sql = "select scenarios.scenariosid,scenarios2kbdata.abstractid,scenarios2kbdata.service from scenarios,Scenarios2kbdata where scenarios2kbdata.scenariosid = scenarios.scenariosid";
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
	 *描述：加载新交互表，缺失补全规则
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-9-21 时间：上午10:05:51
	 *@param service
	 *@param absID
	 *@param _condition
	 *            加载条件组合
	 *@return Result
	 */
	public static Result selectDeficiencyRules(String service, String absID,
			Map<String, String> _condition) {
		try {
			String sql = "select scenarios.scenariosid,scenarios.servicetype,Scenarios2kbdata.service,scenarios2kbdata.abstractid kbdataid,scenariosrules.* "
					+ "from scenarios,scenariosrules,Scenarios2kbdata "
					+ "where scenariosrules.scenariosid = scenarios.scenariosid and scenarios.scenariosid=Scenarios2kbdata.scenariosid and (ruletype=0 or ruletype=4) ";
			List<Object> lstpara = new ArrayList<Object>();
			// 摘要ID等于空，业务不为空时，规则为业务级规则。其他为摘要级规则。
			if (service.length() > 0) {
				sql += " and Scenarios2kbdata.service=? ";
				lstpara.add(service);
			}
			if (absID.length() > 0) {
				sql += " and Scenarios2kbdata.abstractid=? ";
				lstpara.add(absID);
			} else {
				sql += " and Scenarios2kbdata.abstractid is null ";
			}
			// --
			// 增加查询条件
			sql += " and ";
			if (_condition != null && _condition.size() > 0) {
				for (String key : _condition.keySet()) {
					String sqlTemp = "";
					String value = _condition.get(key);
					boolean canBeNull = true;
					if (value.startsWith("NotNull_")) {
						value = value.substring(8);
						canBeNull = false;
					}
					if (value.length() == 0) {
						continue;
					}
//					if (key.equals("city") && value.equals("全国")) {
//						sqlTemp += "(" + key + " like ? or " + key
//								+ " is null) ";
//						sql += sqlTemp + " and ";
//						lstpara.add(value);
//						continue;
//					}
					ArrayList<String> valueList = new ArrayList<String>();
					if (value.contains("#_#")) {
						valueList = StringOper.StringSplit(value, "#_#");
					} else {
						valueList.add(value);
					}
					for (String v : valueList) {
						if (v.length() > 0) {
							if (v.contains("%")) {
								sqlTemp += key + " like ?  or ";
							} else {
								sqlTemp += key + "=?  or ";
							}
							lstpara.add(v);
						}
					}
					sqlTemp = sqlTemp.substring(0, sqlTemp.length() - 3);
					if (key.startsWith("condition") || key.equals("city")) {
						if (canBeNull) {
							// 问题元素列作为条件值，切没有说明noNull时，判段需要增加为空的情况
							sqlTemp = sqlTemp + " or " + key + " is null ";
						}
					}
					sqlTemp = "(" + sqlTemp + " )";
					sql += sqlTemp + " and ";
				}
			}
			if (sql.endsWith("and ")) {
				sql = sql.substring(0, sql.length() - 4);
			}
			GlobalValue.myLog.info("【缺失规则查询】" + sql);
			GlobalValue.myLog.info("【缺失规则查询】" + lstpara.toString());
			// 执行
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());
			if (rs != null && rs.getRowCount() > 0) {
				return rs;
			} else {
				return null;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error("【查询缺失补全规则出错】" + e.toString());
			return null;
		}
	}
}
