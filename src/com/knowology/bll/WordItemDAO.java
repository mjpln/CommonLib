/**
 * 
 */
package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

/**
 *描述：
 * 
 * @author: qianlei
 *@date： 日期：2015-9-29 时间：下午02:34:41
 */
public class WordItemDAO {

	/**
	 * 
	 *描述：查询词条信息
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-30 时间：上午11:18:11
	 *@param wordItem
	 *            词条内容，用于模糊查询
	 *@param precise
	 *            是否精确查询
	 *@param wordItemType
	 *            词条类型（0：标准词、普通词、NULL 1:标准词 2:存在别名的标准词 3：不存在别名的标准词 4、普通词、NULL
	 *            5：除错词）
	 *@param curWordClass
	 *            当前词类，没有则为NULL
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@param page
	 *@param rows
	 *@return Result (word.*)
	 */
	public static Result select4Paging(String wordItem, Boolean precise,
			String wordItemType, String curWordClass, String container,
			int page, int rows) {
		try {
			// 定义查询词条的SQL语句
			String sql = "select * from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=? ";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 绑定类型参数
			lstpara.add(container);
			// 定义条件的SQL语句
			StringBuilder paramSql = additionSelectCondition(lstpara, wordItem,
					precise, wordItemType, curWordClass);
			int start = (page - 1) * rows;
			sql += paramSql.toString() + " ORDER BY wordid Desc LIMIT " + start
					+ "," + rows;
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
	 *描述：查询词条信息(不分页)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-12 时间：上午10:30:29
	 *@param wordItem
	 *            词条内容，用于模糊查询
	 *@param precise
	 *            是否精确查询
	 *@param wordItemType
	 *            词条类型（0：标准词、普通词、NULL 1:标准词 2:存在别名的标准词 3：不存在别名的标准词 4：普通词、NULL
	 *            5：除错词）
	 *@param curWordClass
	 *            当前词类，没有则为NULL
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@return Result (word.*)
	 */
	public static Result select(String wordItem, Boolean precise,
			String wordItemType, String curWordClass, String container) {
		try {
			// 定义查询词条的SQL语句
			String sql = "select * from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=? ";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 绑定类型参数
			lstpara.add(container);
			// 定义条件的SQL语句
			StringBuilder paramSql = additionSelectCondition(lstpara, wordItem,
					precise, wordItemType, curWordClass);
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
	 *描述：查询满足条件的数据条数
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-12 时间：上午11:44:40
	 *@param wordItem
	 *            词条内容，用于模糊查询
	 *@param precise
	 *            是否精确查询
	 *@param wordItemType
	 *            词条类型（0：标准词、普通词、NULL 1:标准词 2:存在别名的标准词 3：不存在别名的标准词 4、普通词、NULL
	 *            5：除错词）
	 *@param curWordClass
	 *            当前词类，没有则为NULL
	 *@param container
	 *            词库类型(企业、基础、子句)
	 *@return int
	 */
	public static int selectCount(String wordItem, Boolean precise,
			String wordItemType, String curWordClass, String container) {
		try {
			// 定义查询词条的SQL语句
			String sql = "select count(*) c from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=? ";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 绑定类型参数
			lstpara.add(container);
			// 定义条件的SQL语句
			StringBuilder paramSql = additionSelectCondition(lstpara, wordItem,
					precise, wordItemType, curWordClass);
			sql += paramSql.toString() + " ORDER BY wordid Desc ";
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
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
	 *@date： 日期：2015-10-12 时间：上午11:01:48
	 *@param lstpara
	 *            SQL参数LISR
	 *@param wordItem
	 *            词条内容，用于模糊查询
	 *@param precise
	 *            是否精确查询
	 *@param wordItemType
	 *            词条类型（0：标准词、普通词、NULL 1:标准词 2:存在别名的标准词 3：不存在别名的标准词 4、普通词、NULL
	 *            5：除错词）
	 *@param curWordClass
	 *            当前词类，没有则为NULL
	 *@return StringBuilder
	 */
	private static StringBuilder additionSelectCondition(List<String> lstpara,
			String wordItem, Boolean precise, String wordItemType,
			String curWordClass) {
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前词类
		if (curWordClass != null && curWordClass.length() > 0) {
			// 加上词类条件
			paramSql.append(" and a.wordclass=? ");
			// 绑定词类参数
			lstpara.add(curWordClass);
		}
		// 判断词条是否为null，空
		if (wordItem != null && !"".equals(wordItem)) {
			// 判断是否精确查询词条
			if (precise) {
				// 加上精确查询词条条件
				paramSql.append(" and t.word =? ");
				// 绑定词条名称参数
				lstpara.add(wordItem);
			} else {
				// 加上模糊查询词条条件
				paramSql.append(" and t.word like ? ");
				// 绑定词条名称参数
				lstpara.add("%" + wordItem + "%");
			}
		}
		// 判断词条类型是否为null，空
		if (wordItemType != null && !"".equals(wordItemType)) {
			// 类型为全部时的条件SQL语句
			if (NewEquals.equals("0",wordItemType)) {
				// 加上条件SQL语句
				paramSql
						.append(" and (t.type=? or t.type=? or t.type is null ) ");
				// 绑定类型参数
				lstpara.add("标准名称");
				// 绑定类型参数
				lstpara.add("普通词");
			} else if (NewEquals.equals("1",wordItemType)) {// 类型是标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and t.type=? ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("2",wordItemType)) {// 类型是已录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and exists (select t1.wordid from word t1 where   t1.stdwordid=t.wordid LIMIT 0,2) ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("3",wordItemType)) {// 类型是未录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and not exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid LIMIT 0,2)");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("4",wordItemType)) {// 类型是非标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and (t.type=? or t.type is null )");
				// 绑定类型参数
				lstpara.add("普通词");
			} else if (NewEquals.equals("5",wordItemType)) {// 不为错词
				// 加上条件SQL语句
				paramSql.append(" and (t.type!=?)");
				// 绑定类型参数
				lstpara.add("错词");
			}
		} else {
			// 加上条件SQL语句
			paramSql.append(" and (t.type=? or t.type=? or t.type is null ) ");
			// 绑定类型参数
			lstpara.add("标准名称");
			// 绑定类型参数
			lstpara.add("普通词");
		}
		return paramSql;
	}

	/**
	 * 
	 *描述：判断词条是否重复
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-12 时间：下午02:10:37
	 *@param curwordclassid
	 *            当前词类ID
	 *@param worditem
	 *            词条名称
	 *@param newtype
	 *            词条类型(标准词，普通词)
	 *@return Boolean
	 */
	public static Boolean exists(Integer curwordclassid, String worditem,
			String newtype) {
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 查询词条是否重复的SQL语句
		String sql = "select wordid from word where  wordclassid=? and word=? and type=? ";
		// 绑定词类id参数
		lstpara.add(curwordclassid + "");
		// 绑定词条参数
		lstpara.add(worditem);
		// 绑定词条类型参数
		lstpara.add(newtype);
		try {
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 有重复词条，返回true
				return true;
			} else {
				// 没有重复词条，返回false
				return false;
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.toString());
			return false;
		}
	}

	/**
	 * 
	 *描述：更新词条
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-12 时间：下午02:53:30
	 *@param oldworditem
	 *            旧词条
	 *@param newworditem
	 *            新词条
	 *@param oldtype
	 *            旧词条类型(标准词，普通词)
	 *@param newtype
	 *            新词条类型(标准词，普通词)
	 *@param wordid
	 *            词条ID
	 *@param wordclassid
	 *            词类ID
	 *@return Integer
	 */
	public static Integer update(String oldworditem, String newworditem,
			String oldtype, String newtype, Integer wordid, Integer wordclassid) {
		try {
			if (!exists(wordclassid, newworditem, newtype)) {
				// 旧词条类型输入错误时，不允许修改
				if (!exists(wordclassid, oldworditem, oldtype)) {
					return 0;
				}
				// --
				if ("标准名称".equals(oldtype)) {
					if (SynonymDAO.exists(wordclassid, null)) {
						// 存在别名的标准名，不允许修改
						return 0;
					}
				}
				// 定义多条SQL语句集合
				List<String> lstsql = new ArrayList<String>();
				// 定义多条SQL语句对应的绑定参数集合
				List<List<?>> lstlstpara = new ArrayList<List<?>>();
				// 更新词条的SQL语句
				String sql = "update word set word=?,type=? , time = sysdate()  where wordid=? ";
				// 定义绑定参数集合
				List<String> lstpara = new ArrayList<String>();
				// 绑定词条参数
				lstpara.add(newworditem);
				// 绑定类型参数
				lstpara.add(newtype);
				// 绑定词条id参数
				lstpara.add(wordid + "");
				// 将SQL语句放入集合中
				lstsql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstlstpara.add(lstpara);
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
	 *描述：新增词条
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-12 时间：下午03:29:04
	 *@param curwordclassid
	 *            词类ID
	 *@param lstWordItem
	 *            词条List
	 *@param type
	 *            词条类型(标准词，普通词)
	 *@return int
	 */
	public static int insert(Integer curwordclassid, List<String> lstWordItem,
			String type) {
		try {
			// 定义多条SQL语句集合
			List<String> lstsql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstlstpara = new ArrayList<List<?>>();
			// 定义保存词条的SQL语句
			String sql = "";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 循环遍历词条集合
			for (int i = 0; i < lstWordItem.size(); i++) {
				if (exists(curwordclassid, lstWordItem.get(i), type)) {
					// 词条已存在，不添加
					continue;
				}
				// 定义保存词条的SQL语句
				sql = "insert into word(wordid,wordclassid,word,type) values(?,?,?,?) ";
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				// 获取词条表的序列值
				int id = ConstructSerialNum.getSerialID("word", "wordid");
				// 绑定id参数
				lstpara.add(id + "");
				// 绑定词类id参数
				lstpara.add(curwordclassid + "");
				// 绑定词类名称参数
				lstpara.add(lstWordItem.get(i));
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
			lstpara.add(curwordclassid + "");
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
	 *描述：删除词条
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-12 时间：下午03:37:02
	 *@param wordid
	 *            词条ID
	 *@param curwordclass
	 *            词类名
	 *@return int
	 */
	public static Boolean delete(Integer wordid, String curwordclass) {
		try {
			// 定义多条SQL语句集合
			List<String> lstsql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstlstpara = new ArrayList<List<?>>();
			// 定义SQL语句
			String sql = "";
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 删除词条的SQL语句
			sql = "delete from word where wordid=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定词条id参数
			lstpara.add(wordid + "");
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);

			// 删除别名的SQL语句
			sql = "delete from word where stdwordid=?";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定词条id参数
			lstpara.add(wordid + "");
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);

			// 更新当前词类编辑时间
			sql = "update wordclass set time =sysdate()  where  wordclass = ? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定词条参数
			lstpara.add(curwordclass);
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);

			// 执行SQL语句，绑定事务处理，返回事务处理的结果
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
}
