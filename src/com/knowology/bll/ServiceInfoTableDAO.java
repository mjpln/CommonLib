/**
 * 
 */
package com.knowology.bll;

import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.DbDAO.DBValueOper;
import com.knowology.UtilityOperate.DateTimeOper;
import com.knowology.UtilityOperate.StringOper;
import com.knowology.dal.Database;

/**
 *描述：业务信息表操作类
 * 
 * @author: qianlei
 *@date： 日期：2015-9-6 时间：下午01:58:40
 */
public class ServiceInfoTableDAO {

	/**
	 *描述：分页查询该业务下的问题元素名称
	 * 
	 *@author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:25:22
	 *@param serviceid
	 *@param name
	 *            用于模糊查询
	 *@param page
	 *            第几分页
	 *@param rows
	 *            单页行数
	 *@return Result (serviceattrname2colnum.*,wordclass.wordclass)
	 */
	public static Result selectAttrName4Paging(String serviceid, String name,
			int page, int rows) {
		try {
			String sql = "SELECT s.*,(SELECT wordclass FROM wordclass WHERE wordclassid=s.wordclassid) wordclass FROM serviceattrname2colnum s WHERE s.`serviceid`="
					+ serviceid;

			if (name != null && name.length() > 0) {
				sql += " AND s.`name` LIKE '%" + name + "%'";
			}
			int start = (page - 1) * rows;
			sql += " ORDER BY columnnum ASC LIMIT " + start + "," + rows;
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
	 *描述：查询产品信息表中该业务下所有问题元素的信息（不分页）
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:29:45
	 *@param serviceid
	 *            可以为空
	 *@param name
	 *            用于模糊查询
	 *@return Result (serviceattrname2colnum.*,wordclass.wordclass)
	 */
	public static Result selectProductInfoAttrName(String serviceid, String name) {
		try {
			String sql = "SELECT s.*,(SELECT wordclass FROM wordclass WHERE wordclassid=s.wordclassid) wordclass FROM serviceattrname2colnum s WHERE s.serviceid is not null";
			if (serviceid != null && serviceid.length() > 0) {
				sql += " and serviceid=" + serviceid;
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
	 *描述：查询该业务下问题元素的总数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:30:28
	 *@param serviceid
	 *@param name
	 *            用于模糊查询
	 *@return Integer
	 */
	public static Integer selectAttrNameCount(String serviceid, String name) {
		try {
			String sql = "select count(*) c from serviceattrname2colnum s where serviceid="
					+ serviceid;
			if (name != null && name.length() > 0) {
				sql += " AND s.`name` LIKE '%" + name + "%'";
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
	 *描述：新增问题元素名
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:30:56
	 *@param serviceid
	 *@param service
	 *@param name
	 *@param column
	 *@param wordclass
	 *            可为空
	 *@return Integer
	 */
	public static Integer insertAttrName(String serviceid, String service,
			String name, String column, String wordclass) {
		try {
			if (name == null || name.length() == 0) {
				return 0;
			}
			// 判断问题元素名是否重复
			if (isContentAttrName(serviceid, name)) {
				return 0;
			}
			// 判断词类是否存在并获取词类ID
			String wordclassid = null;
			Result rs1 = WordclassDAO.select(wordclass, "基础");
			if (rs1 != null && rs1.getRowCount() > 0) {
				wordclassid = DBValueOper.GetValidateStringObj4Null(rs1
						.getRows()[0].get("wordclassid"));
			}
			String sql1 = "insert into serviceattrname2colnum (serviceattrname2colnumid,name,columnnum,wordclassid,serviceid,service) values (";
			int serviceattrname2colnumid = ConstructSerialNum.getSerialID(
					"serviceattrname2colnum", "serviceattrname2colnumid");
			sql1 += serviceattrname2colnumid + ",'" + name + "'," + column
					+ "," + wordclassid + "," + serviceid + ",'" + service
					+ "')";
			return Database.executeNonQuery(sql1);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：判断问题元素名是否存在
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:31:45
	 *@param serviceid
	 *@param name
	 *@return Boolean
	 */
	public static Boolean isContentAttrName(String serviceid, String name) {
		try {
			if (name == null || name.length() <= 0) {
				return false;
			}
			String sql = "select count(*) c from serviceattrname2colnum  where name='"
					+ name + "' and serviceid=" + serviceid;
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
	 *描述：查询问题元素名及其对应的值
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:32:09
	 *@param serviceid
	 *@return Result
	 *         Result(serviceattrname2colnum.name,serviceattrname2colnum.columnnum
	 *         ,word.wordclassid,word.word) word和wordclassid会出现null值
	 */
	public static Result selectAttrname2Values(String serviceid) {
		try {
			String sql = "SELECT n.name,n.columnnum,w.wordclassid,w.word FROM serviceattrname2colnum n LEFT JOIN word w ON w.wordclassid=n.wordclassid WHERE w.stdwordid IS NULL AND n.serviceid="
					+ serviceid + " ORDER BY n.columnnum ASC,w.wordid DESC;";
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
	 *描述：删除问题元素名及服务产品表中的对应列
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:35:16
	 *@param serviceid
	 *@param attrnameid
	 *@param column
	 *@return Boolean
	 */
	public static Boolean deleteAttrName(String serviceid, String attrnameid,
			String column) {
		try {
			// 删除问题元素名称
			String sql1 = "delete from serviceattrname2colnum where serviceattrname2colnumid="
					+ attrnameid;
			// 删除问题元素值（问题元素值表，暂时不用）
			// String sql2 =
			// "delete from serviceattrstdvalue where serviceattrname2colnumid="
			// + attrnameid;
			// 删除服务产品数据中的对应列
			String sql3 = "delete from serviceorproductinfo where serviceid="
					+ serviceid + " and attr" + column + " is not null";
			ArrayList<String> sqlList = new ArrayList<String>();
			sqlList.add(sql1);
			// sqlList.add(sql2);
			sqlList.add(sql3);
			return Database.ExecuteSQL(sqlList);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：修改问题元素名称
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:35:37
	 *@param serviceid
	 *@param attrnameid
	 *@param name
	 *@return Integer
	 */
	public static Integer updateAttrName(String serviceid, String attrnameid,
			String name) {
		try {
			if (!isContentAttrName(serviceid, name)) {
				String sql = "update serviceattrname2colnum set name='" + name
						+ "' where serviceattrname2colnumid=" + attrnameid
						+ " and serviceid=" + serviceid;
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
	 *描述：查询该问题元素下值的总数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:35:52
	 *@param wordclassName
	 *            词类名
	 *@param value
	 *            用于模糊查询
	 *@return Integer
	 */
	public static Integer selectAttrValueCount(String wordclassName,
			String value) {
		return CommonLibWordDAO.getWordCount(value, false, true, "1",
				wordclassName, "基础");

	}

	/**
	 * 
	 *描述：分页查询问题元素值
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:36:30
	 *@param wordclassName
	 *@param value
	 *            用于模糊查询
	 *@param start
	 *@param limit
	 *@return Result (word.*)
	 */
	public static Result selectAttrValue4Paging(String wordclassName,
			String value, int start, int limit) {
		return CommonLibWordDAO.select(start, limit, value, false, true, "1",
				wordclassName, "基础");
	}

	/**
	 * 
	 *描述：新增问题元素值
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-26 时间：下午05:22:34
	 *@param user
	 *@param worditemList
	 *            值List
	 *@param wordclassid
	 *            词类ID
	 *@param wordclassName
	 *            词类名称
	 *@return Integer
	 */
	public static Integer insertAttrValue(User user, List<String> worditemList,
			String wordclassid, String wordclassName) {
		return CommonLibWordDAO.insert(user, wordclassid, wordclassName, "",
				worditemList, "标准词", "基础");
	}

	/**
	 * 
	 *描述：修改问题元素值（词条），并更新相关数据
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-26 时间：下午05:27:42
	 *@param user
	 *@param wordid
	 *            词条ID
	 *@param name
	 *            词条
	 *@param wordclassid
	 *            词类ID
	 *@param oldname
	 *            旧词条值
	 *@param curwordclass
	 *            当前词类名
	 *@return Integer
	 */
	public static Integer updateAttrValue(User user, String wordid,
			String name, String wordclassid, String oldname, String curwordclass) {
		return CommonLibWordDAO.update(user, oldname, name, "标准词", "标准词",
				wordid, wordclassid, curwordclass, "", "基础");
	}

	/**
	 * 
	 *描述：删除问题元素的某个值，并删除相关数据
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-26 时间：下午05:29:43
	 *@param user
	 *@param wordid
	 *            词条ID
	 *@param curwordclass
	 *            当前词类名
	 *@param worditem
	 *            词条值
	 *@return int
	 */
	public static int deleteAttrValue(User user, String wordid,
			String curwordclass, String worditem) {
		return CommonLibWordDAO.delete(user, wordid, curwordclass, "",
				worditem, "基础");
	}

	/**
	 * 
	 *描述：获取业务下服务产品信息的总条数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:38:06
	 *@param serviceid
	 *@param selattr
	 *            用于模糊查询
	 *@return Integer
	 */
	public static Integer selectProductInfoCount(String serviceid,
			String[] selattr) {
		try {
			String sql = "select count(*) c from serviceorproductinfo where serviceid="
					+ serviceid;
			if (selattr != null) {
				for (int i = 0; i < selattr.length; i++) {
					String value = selattr[i];
					if (value != null && value.length() > 0) {
						sql += " and attr" + (i + 1) + " like '%" + value
								+ "%' ";
					}
				}
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
	 *描述：分页查询服务产品信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:38:30
	 *@param serviceid
	 *@param selattr
	 *            用户模糊查询
	 *@param page
	 *@param rows
	 *@return Result
	 */
	public static Result selectProductInfo4Paging(String serviceid,
			String[] selattr, int page, int rows) {
		try {
			String sql = "select * from serviceorproductinfo where serviceid="
					+ serviceid;
			// 增加查询条件
			if (selattr != null) {
				for (int i = 0; i < selattr.length; i++) {
					String value = selattr[i];
					if (value != null && value.length() > 0) {
						sql += " and attr" + (i + 1) + " like '%" + value
								+ "%' ";
					}
				}
			}
			// 分页
			int start = (page - 1) * rows;
			sql += " ORDER BY serviceorproductinfoid ASC LIMIT " + start + ","
					+ rows;
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
	 *描述：判断该业务下是否存在该服务产品信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:39:28
	 *@param serviceid
	 *@param attrArr
	 *@return Boolean
	 */
	public static Boolean isContenProductInfo(String serviceid, String[] attrArr) {
		try {
			String sql = "select count(*) c from serviceorproductinfo where serviceid="
					+ serviceid;
			if (attrArr != null) {
				if (attrArr.length != 0) {
					for (int i = 0; i < attrArr.length; i++) {
						String value = attrArr[i];
						if (value != null && value.length() > 0) {
							sql += " and attr" + (i + 1) + " ='" + value + "'";
						}
					}
					Result rs = Database.executeQuery(sql);
					if (rs != null && rs.getRowCount() > 0) {
						String count = rs.getRows()[0].get("c").toString();
						if (!count.equals("0")) {
							return true;
						}
					}
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
	 *描述：新增服务产品信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:39:39
	 *@param serviceid
	 *@param service
	 *@param attrArr
	 *@return Integer
	 */
	public static Integer insertProductInfo(String serviceid, String service,
			String[] attrArr) {
		try {
			if (attrArr == null) {
				return 0;
			}
			if (attrArr.length == 0) {
				return 0;
			}
			if (!isContenProductInfo(serviceid, attrArr)) {
				String sql = "insert into serviceorproductinfo (serviceorproductinfoid,serviceid,service,";
				// 数组个数决定插入列数
				for (int i = 0; i < attrArr.length; i++) {
					// 加上attr的条件
					sql += "attr" + (i + 1) + ",";
				}
				sql += "status) values (";
				Integer serviceorproductinfoid = ConstructSerialNum
						.getSerialID("serviceorproductinfo",
								"serviceorproductinfoid");
				sql += serviceorproductinfoid + "," + serviceid + ",'"
						+ service + "'";
				for (int i = 0; i < attrArr.length; i++) {
					String value = attrArr[i];
					if (value != null && value.length() > 0) {
						sql += " ,'" + value + "' ";
					}
				}
				sql += ",0)";
				Integer i = Database.executeNonQuery(sql);
				return i;
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：删除服务产品信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:40:01
	 *@param attrid
	 *@return Integer
	 */
	public static Integer deleteProductInfo(String[] attrid) {
		try {
			String sql = "delete from serviceorproductinfo where serviceorproductinfoid in (";
			if (attrid != null && attrid.length > 0) {
				for (String id : attrid) {
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
	 *描述：确认服务产品信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:40:12
	 *@param attrid
	 *@return Integer
	 */
	public static Integer confirmProductInfo(String[] attrid) {
		try {
			String sql = "update serviceorproductinfo set status=1 where serviceorproductinfoid in (";
			if (attrid != null && attrid.length > 0) {
				for (String id : attrid) {
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
	 *描述：更新服务产品信息,整条记录更新，列信息必须写全
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:40:28
	 *@param serviceid
	 *@param attrArr
	 *@param attrid
	 *@return Integer
	 */
	public static Integer updateProductInfo(String serviceid, String[] attrArr,
			String attrid) {
		try {
			if (attrArr == null) {
				return 0;
			}
			if (attrArr.length == 0) {
				return 0;
			}
			if (!isContenProductInfo(serviceid, attrArr)) {
				String sql = "update serviceorproductinfo set";
				for (int i = 0; i < attrArr.length; i++) {
					String value = attrArr[i];
					sql += " attr" + (i + 1) + "='" + value + "',";
				}
				sql += " status=0 where serviceorproductinfoid=" + attrid;
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
	 *描述：全量删除服务产品信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:40:38
	 *@param serviceid
	 *@return Integer
	 */
	public static Integer deleteAllProductInfo(String serviceid) {
		try {
			String sql = "delete from serviceorproductinfo where serviceid="
					+ serviceid;
			return Database.executeNonQuery(sql);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：全量确认服务产品信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-9 时间：下午04:40:48
	 *@param serviceid
	 *@return Integer
	 */
	public static Integer confirmAllProductInfo(String serviceid) {
		try {
			String sql = "update serviceorproductinfo set status=1 where serviceid="
					+ serviceid;
			return Database.executeNonQuery(sql);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：根据服务产品信息更新问题元素的值
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-26 时间：下午05:49:34
	 *@param user
	 *@param serviceid
	 *@return Boolean
	 */
	public static Boolean updateAttrValueByProductInfo(User user,
			String serviceid) {
		try {
			// 当前词类中的问题元素值（词条） {列号，{词条，占位}}
			Map<String, HashMap<String, Integer>> wordValueMap = new HashMap<String, HashMap<String, Integer>>();
			// 每列对应的词类ID
			Map<String, String> columnum2WordclassidDic = new HashMap<String, String>();
			// 读出每列对应的词条，为NULL的不取
			Result rs1 = selectAttrname2Values(serviceid);
			if (rs1 != null && rs1.getRowCount() > 0) {
				for (SortedMap<Object, Object> rows : rs1.getRows()) {
					String columnnum = DBValueOper
							.GetValidateStringObj4Null(rows.get("columnnum"));
					String word = DBValueOper.GetValidateStringObj4Null(rows
							.get("word"));
					String wordclassid = DBValueOper
							.GetValidateStringObj4Null(rows.get("wordclassid"));
					if (columnnum.length() > 0 && word.length() > 0
							&& wordclassid.length() > 0) {
						columnum2WordclassidDic.put(columnnum, wordclassid);
						if (wordValueMap.containsKey(columnnum)) {
							wordValueMap.get(columnnum).put(word, 0);
						} else {
							HashMap<String, Integer> map = new HashMap<String, Integer>();
							map.put(word, 0);
							wordValueMap.put(columnnum, map);
						}
					}
				}
			}
			// 当前服务产品信息每列 {列号，{值，占位}}
			Map<String, HashMap<String, Integer>> productInfo = new HashMap<String, HashMap<String, Integer>>();
			// 读出服务产品信息中列去重复后的值
			if (wordValueMap.size() > 0) {
				Map<String, String> mapTmp = new HashMap<String, String>();
				mapTmp.put("serviceid", serviceid);
				Result rs2 = selectProductInfo(mapTmp, "", "", false);// 将该业务下的所有服务产品信息查出
				if (rs2 != null && rs2.getRowCount() > 0) {
					for (SortedMap<Object, Object> rows : rs2.getRows()) {
						for (String columnnum : wordValueMap.keySet()) {
							String value = DBValueOper
									.GetValidateStringObj4Null(rows.get("attr"
											+ columnnum));
							if (value == "") {
								continue;
							}
							if (productInfo.containsKey(columnnum)) {
								if (!productInfo.get(columnnum).containsKey(
										value)) {
									productInfo.get(columnnum).put(value, 0);
								}
							} else {
								HashMap<String, Integer> tmpMap = new HashMap<String, Integer>();
								tmpMap.put(value, 0);
								productInfo.put(columnnum, tmpMap);
							}
						}
					}
				}
			}
			// 对比，将服务产品信息中新的值，即词条中没有的值存入updateWord，等待更新
			Map<String, ArrayList<String>> updateWord = new HashMap<String, ArrayList<String>>();// 待更新的问题元素值{wordclassid,{word}}
			if (wordValueMap.size() > 0 && productInfo.size() > 0) {
				for (String columnnum : wordValueMap.keySet()) {
					String wordclassid = "";
					if (columnum2WordclassidDic.containsKey(columnnum)) {
						wordclassid = columnum2WordclassidDic.get(columnnum);
					}
					for (String value : productInfo.get(columnnum).keySet()) {
						if (!wordValueMap.get(columnnum).containsKey(value)) {
							if (updateWord.containsKey(wordclassid)) {
								updateWord.get(wordclassid).add(value);
							} else {
								ArrayList<String> list = new ArrayList<String>();
								list.add(value);
								updateWord.put(wordclassid, list);
							}
						}
					}
				}
			}
			// 进行词条更新
			if (updateWord.size() > 0) {
				for (String wordClassid : updateWord.keySet()) {
					CommonLibWordDAO.insert(user, wordClassid, "", "",
							updateWord.get(wordClassid), "标准词", "基础");
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
	 *描述：根据条件查询产品业务信息表数据
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-3-7 时间：下午03:25:28
	 *@param _condition
	 *@param selectStr
	 *@param num
	 *            最大查找行数
	 *@param noNull
	 *            查询参数不允许为null
	 *@return Result
	 */
	public static Result selectProductInfo(Map<String, String> _condition,
			String selectStr, String num, Boolean noNull) {
		try {

			String sql = "select * from serviceorproductinfo ";
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			Map<String, String> condition = new HashMap<String, String>();
			condition.putAll(_condition);
			// 增加查询条件
			if (condition != null && condition.size() > 0) {
				sql += " where ";
				for (String key : condition.keySet()) {
					String sqlTemp = "";
					String value = condition.get(key);
					boolean canBeNull = true;
					if (value.startsWith("NotNull_")) {
						value = value.substring(8);
						canBeNull = false;
					}
					if (value.length() == 0) {
						continue;
					}
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
					if (key.startsWith("attr")) {
						if (canBeNull && !noNull) {
							// 问题元素列作为条件值，切没有说明noNull时，判段需要增加为空的情况
							sqlTemp = "(" + sqlTemp + " or " + key
									+ " is null)";
						}
					}
					sqlTemp = " (" + sqlTemp + ")";
					sql += sqlTemp + " and "; 
				}
				if (sql.endsWith("and ")) {
					sql = sql.substring(0, sql.length() - 4);
				}
			}
			// --
			if (num.length() > 0) {
				sql += " and rownum <= " + num
						+ "  order by SERVICEORPRODUCTINFOID asc";
			} else {
				sql += " order by SERVICEORPRODUCTINFOID asc";
			}
			// 修改查询目标
			if (selectStr != null && selectStr.length() > 0) {
				sql = sql.replace("*", selectStr);
			}
			// for debug
			System.out.println(sql);
			System.out.println(lstpara.toString());
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

	/**
	 * 
	 *描述：查询clob字段表对应的值
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-5-24 时间：上午09:51:13
	 *@param clobID
	 *@return String
	 */
	public static String selectClobString(String clobID) {
		try {
			String sql = "select * from  serviceorproductinfo_clob where id=?";
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			lstpara.add(clobID);
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());
			if (rs != null && rs.getRowCount() > 0) {
				return DBValueOper
						.clobToString((Clob) rs.getRowsByIndex()[0][1]);
			} else {
				return "";
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return "";
		}
	}

	public static void main(String[] args) {
		// Result rs=selectAttrName4Paging("1820996", "", "1", "30");
		// System.out.println(selectAttrNameCount("1820996", ""));
		// System.out.println(selectProductInfoCount("1820996", null));
		System.out
				.println(selectClobString("CLOB_4e948eb75f5648e49c18c5f3155a7ecd"));
		// Result rs = selectAttrName("1820996", "");
		// Result rs = selectAttrField("1820996");
		// if (rs != null) {
		// System.out.println(rs.getRowCount());
		// } else {
		// System.out.println("null");
		// }
		// System.out.println(isContentAttrName("1820996", "基金代码"));
		// System.out.println(insertAttrName("1820996","天弘基金信息表","测试列","100","天弘基金净值父类"));
	}
}
