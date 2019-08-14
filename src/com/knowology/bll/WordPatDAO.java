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
 *描述：词模表操作类
 * 
 * @author: qianlei
 *@date： 日期：2015-9-23 时间：下午04:28:22
 */
public class WordPatDAO {

	/**
	 * 
	 *描述：词模查询
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-24 时间：上午11:05:42
	 *@param service
	 *            不为null
	 *@param brand
	 *            业务品牌（不为null）
	 *@param topic
	 *            不为null
	 *@param kbdataid
	 *            不为null
	 *@param wordpat
	 *            用于模糊查询
	 *@param autosendswitch
	 *            默认为0
	 *@return Result (wordpat.simplewordpat,wordpat.wordpat,wordpat.wordpatid
	 *         ,wordpat.city , c.wordpattype wordpattype ,c.autosendswitch)
	 */
	public static Result select4Paging(String service, String brand,
			String topic, String[] kbdataid, String wordpat,
			Integer autosendswitch, int page, int rows) {
		try {
			// 验证数据完整性
			if (service != null && brand != null && topic != null
					&& kbdataid != null) {
				String sql = "SELECT  c.simplewordpat,c.wordpat,c.wordpatid wordpatid,c.city , c.wordpattype  ,c.autosendswitch FROM service a,kbdata b,wordpat c WHERE a.serviceid=b.serviceid AND b.kbdataid=c.kbdataid";
				sql += " and a.brand='" + brand + "' and a.service='" + service
						+ "' and b.topic='" + topic + "'";
				if (kbdataid.length > 0) {
					sql += " and b.kbdataid in(";
					for (String id : kbdataid) {
						sql += id + ",";
					}
					if (sql.endsWith(",")) {
						sql = sql.substring(0, sql.length() - 1);
					}
					sql += ")";
				}
				// 判断词模是否为空，null
				if (wordpat != null && !"".equals(wordpat)
						&& wordpat.length() > 0) {
					// 加上词模的查询条件
					sql += " and c.wordpat like '%" + wordpat + "%' ";
				}
				// 判断autosendswitch是否为空，null
				if (autosendswitch != null) {
					// 加上autosendswitch条件
					sql += " and c.autosendswitch=" + autosendswitch;
				} else {
					// 默认为0
					sql += " and c.autosendswitch=0";
				}
				int start = (page - 1) * rows;
				sql += " ORDER BY c.wordpatid Desc LIMIT " + start + "," + rows;
				Result rs = Database.executeQuery(sql);
				if (rs != null && rs.getRowCount() > 0) {
					return rs;
				}
			}
			return null;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return null;
		}
	}

	/**
	 * 
	 *描述：查询词模（不分页）
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-25 时间：下午02:46:26
	 *@param service
	 *            不为null
	 *@param brand
	 *            业务品牌（不为null）
	 *@param topic
	 *            不为null
	 *@param kbdataid
	 *            不为null
	 *@param wordpat
	 *            用于模糊查询
	 *@param autosendswitch
	 *            默认为0
	 *@return Result (wordpat.simplewordpat,wordpat.wordpat,wordpat.wordpatid
	 *         ,wordpat.city , c.wordpattype wordpattype ,c.autosendswitch)
	 */
	public static Result select(String service, String brand, String topic,
			String[] kbdataid, String wordpat, Integer autosendswitch) {
		try {
			// 验证数据完整性
			if (service != null && brand != null && topic != null
					&& kbdataid != null) {
				String sql = "SELECT  c.simplewordpat,c.wordpat,c.wordpatid wordpatid,c.city , c.wordpattype  ,c.autosendswitch FROM service a,kbdata b,wordpat c WHERE a.serviceid=b.serviceid AND b.kbdataid=c.kbdataid";
				sql += " and a.brand='" + brand + "' and a.service='" + service
						+ "' and b.topic='" + topic + "'";
				if (kbdataid.length > 0) {
					sql += " and b.kbdataid in(";
					for (String id : kbdataid) {
						sql += id + ",";
					}
					if (sql.endsWith(",")) {
						sql = sql.substring(0, sql.length() - 1);
					}
					sql += ")";
				}
				// 判断词模是否为空，null
				if (wordpat != null && !"".equals(wordpat)
						&& wordpat.length() > 0) {
					// 加上词模的查询条件
					sql += " and c.wordpat like '%" + wordpat + "%' ";
				}
				// 判断autosendswitch是否为空，null
				if (autosendswitch != null) {
					// 加上autosendswitch条件
					sql += " and c.autosendswitch=" + autosendswitch;
				} else {
					// 默认为0
					sql += " and c.autosendswitch=0";
				}
				sql += " ORDER BY c.wordpatid Desc";
				Result rs = Database.executeQuery(sql);
				if (rs != null && rs.getRowCount() > 0) {
					return rs;
				}
			}
			return null;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return null;
		}
	}

	/**
	 * 
	 *描述：查询满足条件的词模条数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-24 时间：下午04:00:48
	 *@param service
	 *@param brand
	 *@param topic
	 *@param kbdataid
	 *@param wordpat
	 *@param autosendswitch
	 *@return Integer
	 */
	public static Integer selectCount(String service, String brand,
			String topic, String[] kbdataid, String wordpat,
			Integer autosendswitch) {
		try {
			// 验证数据完整性
			if (service != null && brand != null && topic != null
					&& kbdataid != null) {
				String sql = "SELECT count(*) c FROM service a,kbdata b,wordpat c WHERE a.serviceid=b.serviceid AND b.kbdataid=c.kbdataid";
				sql += " and a.brand='" + brand + "' and a.service='" + service
						+ "' and b.topic='" + topic + "'";
				if (kbdataid.length > 0) {
					sql += " and b.kbdataid in(";
					for (String id : kbdataid) {
						sql += id + ",";
					}
					if (sql.endsWith(",")) {
						sql = sql.substring(0, sql.length() - 1);
					}
					sql += ")";
				}
				// 判断词模是否为空，null
				if (wordpat != null && !"".equals(wordpat)
						&& wordpat.length() > 0) {
					// 加上词模的查询条件
					sql += " and c.wordpat like '%" + wordpat + "%' ";
				}
				// 判断autosendswitch是否为空，null
				if (autosendswitch != null) {
					// 加上autosendswitch条件
					sql += " and c.autosendswitch=" + autosendswitch;
				} else {
					// 默认为0
					sql += " and c.autosendswitch=0";
				}
				Result rs = Database.executeQuery(sql);
				if (rs != null && rs.getRowCount() > 0) {
					String count = rs.getRows()[0].get("c").toString();
					return Integer.valueOf(count);
				} else {
					return 0;
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
	 *描述：更新词模（所有参数均必填）
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-24 时间：下午02:11:28
	 *@param simplewordpat
	 *            简单词模
	 *@param wordpat
	 *            词模提
	 *@param city
	 *            城市
	 *@param autosendswitch
	 *@param wordpattype
	 *            词模类型
	 *@param brand
	 *            品牌
	 *@param kbdataid
	 *            摘要id
	 *@param wordpatid
	 *            词模id
	 *@return Integer
	 */
	public static Integer update(String simplewordpat, String wordpat,
			String city, Integer autosendswitch, Integer wordpattype,
			String brand, Integer kbdataid, Integer wordpatid) {
		try {
			if (exists(wordpat, city, wordpattype, brand, kbdataid)) {
				return 0;
			}
			if (wordpatid != null) {
				String sql = "update wordpat t set t.simplewordpat='"
						+ simplewordpat + "',t.wordpat='" + wordpat
						+ "',t.city='" + city + "',t.autosendswitch="
						+ autosendswitch + ",t.wordpattype=" + wordpattype
						+ ",t.brand='" + brand + "' where t.kbdataid="
						+ kbdataid + " and wordpatid=" + wordpatid;
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
	 *描述：查询词模是否已存在
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-28 时间：上午10:46:27
	 *@param wordpat
	 *@param city
	 *@param wordpattype
	 *@param brand
	 *@param kbdataid
	 *@return Boolean
	 */
	public static Boolean exists(String wordpat, String city,
			Integer wordpattype, String brand, Integer kbdataid) {
		try {
			// 获取模板,并赋值给变量
			String patternStr = wordpat;
			// 将模板按照#拆分
			String pattern[] = patternStr.split("#");
			if (pattern.length != 2) {
				return false;
			}
			// 获取词模体
			String patternbefore = pattern[0];
			// 将返回值按照&拆分，获取返回值数组
			String returnvalue[] = pattern[1].split("&");
			// 定义SQL语句集合
			StringBuilder sql = new StringBuilder();
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			// 判断词模是否含有~
			// 非词模不需要加摘要ID的判断，不同摘要可以有相同的非词模
			if (wordpattype == 2) {
				// 定义SQL语句集合
				sql = new StringBuilder();
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				sql
						.append("select wordpat from wordpat t where  t.brand=? and city =? ");
				// 绑定品牌参数
				lstpara.add(brand);
				// 绑定城市参数
				lstpara.add(city);
				// 加上摘要id条件
				sql.append(" and t.kbdataid=? ");
				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 加上词模查询条件
				sql.append(" and t.wordpat like ? ");
				// 绑定词模参数
				lstpara.add(patternbefore + "%");
			} else {
				// 定义SQL语句集合
				sql = new StringBuilder();
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				sql
						.append("select wordpat from wordpat t where  t.brand=? and city =? ");
				// 绑定品牌参数
				lstpara.add(brand);
				// 绑定城市参数
				lstpara.add(city);
				// 加上词模查询条件
				sql.append(" and t.wordpat like ? ");
				// 绑定词模参数
				lstpara.add(patternbefore + "%");
			}
			// 循环遍历返回值数组
			for (String s : returnvalue) {
				// 判断是否含有编者
				if (s.contains("编者")) {
					// 加上词模的条件
					sql.append(" and t.wordpat like ? ");
					// 加上编者参数
					lstpara.add("%编者%");
					continue;
					// 编者姓名不作为唯一性判断的条件，但是编者返回值必须有
				}
				// 加上词模的条件
				sql.append(" and t.wordpat like ? ");
				// 加上返回值参数
				lstpara.add("%" + s + "%");
			}
			// 执行SQL语句，获取相应的数据源
			Result rs = Database
					.executeQuery(sql.toString(), lstpara.toArray());
			// 判断数据源为null或者数据量为0
			if (rs == null || rs.getRowCount() == 0) {
				return false;
			} else {
				// 定义存放模板的集合
				List<String> ls = new ArrayList<String>();
				// 循环遍历数据源
				for (int i = 0; i < rs.getRowCount(); i++) {
					// 获取模板
					String wordpatstr = rs.getRows()[i].get("wordpat")
							.toString();
					// 将模板按照#拆分
					String patternarry[] = wordpatstr.split("#");
					// 获取返回值，并将返回值按照&拆分
					String returnvaluearry[] = patternarry[1].split("&");
					// 判断返回值的数组长度是否相等
					if (returnvalue.length == returnvaluearry.length) {
						// 将当前模板放入集合中
						ls.add(wordpatstr);
					}
				}
				// 判断集合的个数是否大于0
				if (ls.size() > 0) {
					return true;
				} else {
					return false;
				}
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：新增词模（所有参数均必填）
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-24 时间：下午02:29:36
	 *@param simplewordpat
	 *            简单词模
	 *@param wordpat
	 *            模板
	 *@param city
	 *            地市（默认：上海）
	 *@param autosendswitch
	 *            自动开关（默认：0）
	 *@param wordpattype
	 *            模板类型（0 普通词模；1等于词模；2排除词模；3选择词模；4特征词模；5自学习词模）
	 *@param brand
	 *            品牌
	 *@param kbdataid
	 *            摘要id
	 *@return Integer
	 */
	public static Integer insert(String simplewordpat, String wordpat,
			String city, Integer autosendswitch, Integer wordpattype,
			String brand, Integer kbdataid) {
		try {
			if (exists(wordpat, city, wordpattype, brand, kbdataid)) {
				return 0;
			}
			if (simplewordpat != null && wordpat != null && city != null
					&& autosendswitch != null && wordpattype != null
					&& brand != null && kbdataid != null) {
				// 定义绑定参数集合
				ArrayList<Object> lstpara = new ArrayList<Object>();
				String sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime,simplewordpat) values(?,?,?,?,?,?,?,sysdate(),?)";
				// 获取主键ID
				Integer wordpatid = ConstructSerialNum.getSerialID("wordpat",
						"wordpatid");
				// 绑定模板id参数
				lstpara.add(wordpatid);
				// 绑定模板参数
				lstpara.add(wordpat);
				// 绑定地市参数
				lstpara.add(city);
				// 绑定自动开关参数
				lstpara.add(autosendswitch);
				// 绑定模板类型参数
				lstpara.add(wordpattype);
				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定品牌参数
				lstpara.add(brand);
				// 绑定简单词模参数
				lstpara.add(simplewordpat);
				return Database.executeNonQuery(sql, lstpara.toArray());
			}
			return 0;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：删除词模
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-25 时间：上午10:35:19
	 *@param wordPatid
	 *@return Integer
	 */
	public static Integer delete(Integer wordPatid) {
		try {
			String sql = "delete from wordpat where wordPatid=" + wordPatid;
			return Database.executeNonQuery(sql);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}
}
