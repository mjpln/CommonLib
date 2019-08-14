/**
 * 
 */
package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.DbDAO.DBValueOper;
import com.knowology.dal.Database;
import com.str.NewEquals;

/**
 *描述：词条别名数据操作类
 * 
 * @author: qianlei
 *@date： 日期：2015-10-12 时间：下午02:29:26
 */
public class SynonymDAO {

	/**
	 * 
	 *描述：分页查询别名
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-19 时间：上午10:37:16
	 *@param page
	 *@param rows
	 *@param synonym
	 *            别名
	 *@param precise
	 *            是否精确查询
	 *@param curworditemid
	 *            当前词条
	 *@param curwordclass
	 *            当前词类，没有则为NULL
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@param wordItemType
	 *            词条类型（全称 1，简称 2，代码 3，错词 4，其他别名（其他） 5）
	 *@return Result (word.*,wordclass.*)
	 */
	public static Result select4Paging(int page, int rows, String synonym,
			Boolean precise, String curworditem, String curwordclass,
			String container, String wordItemType) {
		try {
			// 定义查询词条的SQL语句
			String sql = "select * from word t,word s,wordclass a where t.wordclassid=a.wordclassid and t.wordid=s.stdwordid and a.container=? ";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 绑定类型参数
			lstpara.add(container);
			// 定义条件的SQL语句
			StringBuilder paramSql = additionSelectCondition(lstpara, synonym,
					precise, curworditem, curwordclass, wordItemType);
			int start = (page - 1) * rows;
			sql += paramSql.toString() + " ORDER BY t.wordid Desc LIMIT "
					+ start + "," + rows;
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
	 *描述：查询别名(不分页)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-19 时间：上午10:57:36
	 *@param synonym
	 *            别名
	 *@param precise
	 *            是否精确查询
	 *@param curworditem
	 *            当前词条
	 *@param curwordclass
	 *            当前词类，没有则为NULL
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@param wordItemType
	 *            词条类型（全程 1，简称 2，代码 3，错词 4，其他别名（其他） 5）
	 *@return Result (word.*,wordclass.*)
	 */
	public static Result select(String synonym, Boolean precise,
			String curworditem, String curwordclass, String container,
			String wordItemType) {
		try {
			// 定义查询词条的SQL语句
			String sql = "select * from word t,word s,wordclass a where t.wordclassid=a.wordclassid and t.wordid=s.stdwordid and a.container=? ";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 绑定类型参数
			lstpara.add(container);
			// 定义条件的SQL语句
			StringBuilder paramSql = additionSelectCondition(lstpara, synonym,
					precise, curworditem, curwordclass, wordItemType);
			sql += paramSql.toString() + " ORDER BY wordid Desc ";
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
	 *描述：查询符合条件的别名数据条数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-19 时间：上午11:01:16
	 *@param synonym
	 *            别名
	 *@param precise
	 *            是否精确查询
	 *@param curworditem
	 *            当前词条
	 *@param curwordclass
	 *            当前词类，没有则为NULL
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@param wordItemType
	 *            词条类型（全程 1，简称 2，代码 3，错词 4，其他别名（其他） 5）
	 *@return Integer
	 */
	public static Integer selectCount(String synonym, Boolean precise,
			String curworditem, String curwordclass, String container,
			String wordItemType) {
		try {
			// 定义查询词条的SQL语句
			String sql = "select count(*) c from word t,word s,wordclass a where t.wordclassid=a.wordclassid and t.wordid=s.stdwordid and a.container=? ";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 绑定类型参数
			lstpara.add(container);
			// 定义条件的SQL语句
			StringBuilder paramSql = additionSelectCondition(lstpara, synonym,
					precise, curworditem, curwordclass, wordItemType);
			sql += paramSql.toString() + " ORDER BY wordid Desc ";
			// 执行SQL语句，获取相应的数据源
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
	 *描述：词条查询条件SQL生成方法
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-19 时间：上午10:42:57
	 *@param lstpara
	 *            SQL参数LIST
	 *@param synonym
	 *            别名
	 *@param precise
	 *            是否精确
	 *@param curworditem
	 *            当前词条
	 *@param curwordclass
	 *            当前词类
	 *@param wordItemType
	 *            词条类型（全程 1，简称 2，代码 3，错词 4，其他别名（其他） 5）
	 *@return StringBuilder
	 */
	private static StringBuilder additionSelectCondition(List<String> lstpara,
			String synonym, Boolean precise, String curworditem,
			String curwordclass, String wordItemType) {
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前词条且当前词条名称不为null，空
		if (!"".equals(curwordclass) && curworditem != null
				&& curworditem.length() > 0) {
			// 加上词条条件
			paramSql.append(" and t.word=? and a.wordclass=? ");
			// 绑定词条参数
			lstpara.add(curworditem);
			// 绑定词类参数
			lstpara.add(curwordclass);
		}
		// 判断别名是否为空，null
		if (!"".equals(synonym) && synonym != null && synonym.length() > 0) {
			// 判断是否精确查询
			if (precise) {
				// 精确查询别名
				paramSql.append(" and s.word=? ");
				// 绑定别名名称参数
				lstpara.add(synonym);
			} else {
				// 模糊查询别名
				paramSql.append(" and s.word like ? ");
				// 绑定别名名称参数
				lstpara.add("%" + synonym + "%");
			}
		}
		// 判断类型是否为空，null
		if (wordItemType != null && !"".equals(wordItemType)) {
			// 判断类型的不同，SQL语句也不同

			if (NewEquals.equals("1",wordItemType)) {// 类型是全称时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("全称");
			} else if (NewEquals.equals("2",wordItemType)) {// 类型是简称时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("简称");
			} else if (NewEquals.equals("3",wordItemType)) {// 类型是代码时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("代码");
			} else if (NewEquals.equals("4",wordItemType)) {// 类型是错词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("错词");
			} else if (NewEquals.equals("5",wordItemType)) {// 类型是其他别名时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and (s.type=? or s.type=?) ");
				// 绑定类型参数
				lstpara.add("其他");
				// 绑定类型参数
				lstpara.add("其他别名");
			}
		} else {
			// 加上条件SQL语句
			paramSql
					.append(" and (s.type=? or s.type=? or s.type =? or s.type=? or s.type=? or s.type=? ) ");
			// 绑定类型参数
			lstpara.add("全称");
			// 绑定类型参数
			lstpara.add("简称");
			// 绑定类型参数
			lstpara.add("错词");
			// 绑定类型参数
			lstpara.add("代码");
			// 绑定类型参数
			lstpara.add("其他");
			// 绑定类型参数
			lstpara.add("其他别名");
		}
		return paramSql;
	}

	/**
	 * 
	 *描述： 判断别名是否重复
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-12 时间：下午02:52:04
	 *@param stdwordid
	 *            标准名ID
	 *@param synonym
	 *            别名（可为null）
	 *@return Boolean
	 */
	public static Boolean exists(Integer stdwordid, String synonym) {
		// 对应绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义查询别名的SQL语句
		String sql = "select word from word where stdwordid=?";
		// 绑定词条id参数
		lstpara.add(stdwordid + "");
		if (synonym != null) {
			sql += " and word=? ";
			// 绑定别名名称参数
			lstpara.add(synonym);
		}
		try {
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 有数据，表示重复
				return true;
			} else {
				// 没有数据，不是不重复
				return false;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：更新别名
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-20 时间：上午10:18:51
	 *@param oldsynonym
	 *            待修改词条
	 *@param newsynonym
	 *            新词条
	 *@param newtype
	 *            词条类型（全称，简称，代码，错词 ，其他别名，其他）
	 *@param wordid
	 *            词条ID
	 *@param stdwordid
	 *            标准词ID
	 *@param curwordclass
	 *            当前词类名
	 *@return Integer
	 */
	public static Boolean update(String oldsynonym, String newsynonym,
			String newtype, String wordid, Integer stdwordid,
			String curwordclass) {
		try {
			// 判断是否更新的是别名名称
			if (!newsynonym.equals(oldsynonym)) {
				// 判断是否已存在相同词类
				if (exists(stdwordid, newsynonym)) {
					return false;
				}
			}
			// 定义多条SQL语句集合
			List<String> lstsql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstlstpara = new ArrayList<List<?>>();
			// 更新别名的SQL语句
			String sql = "update word set word=?,type=? ,time = sysdate() where wordid=? ";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 绑定别名名称参数
			lstpara.add(newsynonym);
			// 绑定流程参数
			lstpara.add(newtype);
			// 绑定别名id
			lstpara.add(wordid);
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);

			// 更新别名的SQL语句
			sql = "update wordclass set time =sysdate()  where  wordclass =  ? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定别名名称参数
			lstpara.add(curwordclass);
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
			// 执行SQL语句，绑定事务处理，返回事务处理结果
			int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
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
	 *描述：新增别名
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-20 时间：上午10:30:07
	 *@param wordclassid
	 *            词类ID
	 *@param lstSynonym
	 *            新增别名列表
	 *@param stdwordid
	 *            标准名ID
	 *@param type
	 *            词条类型（全称，简称，代码，错词 ，其他别名，其他）
	 *@return int
	 */
	public static int insert(Integer wordclassid, List<String> lstSynonym,
			Integer stdwordid, String type) {
		try {

			// 定义多条SQL语句集合
			List<String> lstsql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstlstpara = new ArrayList<List<?>>();
			// 定义保存别名的SQL语句
			String sql = "";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 循环遍历别名集合
			for (int i = 0; i < lstSynonym.size(); i++) {
				if (exists(stdwordid, lstSynonym.get(i))) {
					// 别名已存在，排除
					continue;
				}
				// 定义新增别名的SQL语句
				sql = "insert into word(wordid,wordclassid,word,stdwordid,type) values(?,?,?,?,?)";
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				// 获取别名的序列值
				int id = ConstructSerialNum.getSerialID("word", "wordid");
				// 绑定id参数
				lstpara.add(id + "");
				// 绑定词类id参数
				lstpara.add(wordclassid + "");
				// 绑定别名参数
				lstpara.add(lstSynonym.get(i));
				// 绑定词条id参数
				lstpara.add(stdwordid + "");
				// 绑定类型参数
				lstpara.add(type);
				// 将SQL语句放入集合中
				lstsql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstlstpara.add(lstpara);
			}

			// 更新当前词类编辑时间
			sql = "update wordclass set time =sysdate()  where  wordclassid = ? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定词条参数
			lstpara.add(wordclassid + "");
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);

			// 执行SQL语句，绑定事务处理，返回事务处理结果
			return Database.executeNonQueryTransaction(lstsql, lstlstpara);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return 0;
		}
	}

	/**
	 * 
	 *描述：删除别名
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-20 时间：上午10:36:12
	 *@param synonymID
	 *            别名ID
	 *@param curWordClass
	 *            词类名
	 *@return Boolean
	 */
	public static Boolean delete(List<String> synonymID, String curWordClass) {
		try {
			if (synonymID != null && synonymID.size() > 0) {
				// 定义多条SQL语句集合
				List<String> lstsql = new ArrayList<String>();
				// 定义多条SQL语句对应的绑定参数集合
				List<List<?>> lstlstpara = new ArrayList<List<?>>();
				// 定义SQL语句
				StringBuilder sql = new StringBuilder();
				// 定义删除别名的SQL语句
				sql.append("delete from word where wordid in (");
				// 定义绑定参数集合
				List<String> lstpara = new ArrayList<String>();
				// 循环遍历id数组
				for (int i = 0; i < synonymID.size(); i++) {
					if (i != synonymID.size() - 1) {
						// 除了最后一个不加逗号，其他加上逗号
						sql.append("?,");
					} else {
						// 最后一个加上右括号，将SQL语句补充完整
						sql.append("?)");
					}
					// 绑定参数集合
					lstpara.add(synonymID.get(i));
				}
				// 将SQL语句放入集合中
				lstsql.add(sql.toString());
				// 将对应的绑定参数集合放入集合中
				lstlstpara.add(lstpara);

				// 更新当前词类编辑时间
				String sql_update = "update wordclass set time =sysdate()  where  wordclass = ? ";
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定词条参数
				lstpara.add(curWordClass);
				// 将SQL语句放入集合中
				lstsql.add(sql_update);
				// 将对应的绑定参数集合放入集合中
				lstlstpara.add(lstpara);

				// 执行SQL语句，绑定事务处理，返回事务处理的结果
				int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
				// 判断事务处理结果
				if (c > 0) {
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
	 *描述：根据标准词ID删除别名
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-5 时间：下午02:16:05
	 *@param stdWordID
	 *            标准词ID
	 *@param curWordClass
	 *            当前词类名
	 *@return Boolean
	 */
	public static Boolean deleteByStdWordID(Integer stdWordID,
			String curWordClass) {
		try {
			String sql = "select wordid from word where stdwordid=" + stdWordID;
			Result rs = Database.executeQuery(sql);
			ArrayList<String> synonymID = new ArrayList<String>();
			if (rs != null && rs.getRowCount() > 0) {
				for (SortedMap<Object, Object> rows : rs.getRows()) {
					synonymID.add(DBValueOper.GetValidateStringObj4Null(rows
							.get("wordid")));
				}
				return delete(synonymID, curWordClass);
			}
			return false;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}
}
