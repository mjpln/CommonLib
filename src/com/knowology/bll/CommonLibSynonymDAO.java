package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

public class CommonLibSynonymDAO {
	/**
	 * 查询别名记录数
	 * 
	 * @param synonym参数别名名称
	 * @param isprecise参数是否精确查询
	 * @param iscurrentworditem参数是否当前词条
	 * @param type参数别名类型
	 * @param curworditem参数当前词条名称
	 * @param curwordclass参数当前词类名称
	 * @return Result
	 */
	public static Result getSynonymCount(String synonym, Boolean isprecise,
			Boolean iscurrentworditem, String type, String curworditem,
			String curwordclass,String wordclassType) {
		// 查询满足条件的别名的SQL语句
		String sql = "select * from word t,word s,wordclass a where t.wordclassid=a.wordclassid and t.wordid=s.stdwordid and a.container=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定类型参数
		lstpara.add(wordclassType);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前词条且当前词条名称不为null，空
		if (iscurrentworditem && !"".equals(curworditem) && curworditem != null
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
			if (isprecise) {
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
		if (type != null && !"".equals(type)) {
			// 判断类型的不同，SQL语句也不同
			if (NewEquals.equals("0",type)) {
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
			} else if (NewEquals.equals("1",type)) {// 类型是全称时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("全称");
			} else if (NewEquals.equals("2",type)) {// 类型是简称时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("简称");
			} else if (NewEquals.equals("3",type)) {// 类型是代码时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("代码");
			} else if (NewEquals.equals("4",type)) {// 类型是错词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("错词");
			} else if (NewEquals.equals("5",type)) {// 类型是其他别名时的SQL语句
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
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql + paramSql.toString(), lstpara
				.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + paramSql.toString() + "#" + lstpara );
		
		return rs;
	}

	/**
	 * 带分页的查询满足条件的别名名称
	 * 
	 * @param start参数开始条数
	 * @param limit参数每页条数
	 * @param synonym参数别名名称
	 * @param isprecise参数是否精确查询
	 * @param iscurrentworditem参数是否当前词条
	 * @param type参数别名类型
	 * @param curworditem参数当前词条名称
	 * @param curwordclass参数当前词类名称
	 * @return
	 */
	public static Result select(int start, int limit, String synonym,
			Boolean isprecise, Boolean iscurrentworditem, String type,
			String curworditem, String curwordclass,String wordclassType) {
		// 查询满足条件的别名的SQL语句
		String sql = "select * from word t,word s,wordclass a where t.wordclassid=a.wordclassid and t.wordid=s.stdwordid and a.container=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定类型参数
		lstpara.add(wordclassType);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前词条且当前词条名称不为null，空
		if (iscurrentworditem && !"".equals(curworditem) && curworditem != null
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
			if (isprecise) {
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
		if (type != null && !"".equals(type)) {
			// 判断类型的不同，SQL语句也不同
			if (NewEquals.equals("0",type)) {
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
			} else if (NewEquals.equals("1",type)) {// 类型是全称时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("全称");
			} else if (NewEquals.equals("2",type)) {// 类型是简称时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("简称");
			} else if (NewEquals.equals("3",type)) {// 类型是代码时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("代码");
			} else if (NewEquals.equals("4",type)) {// 类型是错词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("错词");
			} else if (NewEquals.equals("5",type)) {// 类型是其他别名时的SQL语句
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
		// 执行带分页的查询满足条件的SQL语句
		
		if (GetConfigValue.isOracle) {
			sql = "select t2.* from(select t1.*, rownum rn from(select t.word worditem,s.type,s.word,a.wordclass,s.wordid,s.stdwordid from word t,word s,wordclass a where t.wordclassid=a.wordclassid and t.wordid=s.stdwordid and a.container=? "
				+ paramSql
				+ " order by s.wordid desc)t1)t2 where t2.rn>? and t2.rn<=? ";
		} else if (GetConfigValue.isMySQL) {
			sql = "select t2.* from(select t1.*  from(select t.word worditem,s.type,s.word,a.wordclass,s.wordid,s.stdwordid from word t,word s,wordclass a where t.wordclassid=a.wordclassid and t.wordid=s.stdwordid and a.container=? "
				+ paramSql
				+ " order by s.wordid desc)t1)t2 limit ?,? ";
		}
		// 绑定开始条数参数
		lstpara.add(start);
		// 绑定截止条数参数
		lstpara.add(start + limit);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		return rs;
	}
	

	/**
	 * 带分页的查询满足条件的别名名称(不分页)
	 * 
	 * @param synonym参数别名名称
	 * @param isprecise参数是否精确查询
	 * @param iscurrentworditem参数是否当前词条
	 * @param type参数别名类型
	 * @param curworditem参数当前词条名称
	 * @param curwordclass参数当前词类名称
	 * @return
	 */
	public static Result _select(String synonym,
			Boolean isprecise, Boolean iscurrentworditem, String type,
			String curworditem, String curwordclass) {
		// 查询满足条件的别名的SQL语句
		String sql = "select * from word t,word s,wordclass a where t.wordclassid=a.wordclassid and t.wordid=s.stdwordid and a.container=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定类型参数
		lstpara.add("基础");
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前词条且当前词条名称不为null，空
		if (iscurrentworditem && !"".equals(curworditem) && curworditem != null
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
			if (isprecise) {
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
		if (type != null && !"".equals(type)) {
			// 判断类型的不同，SQL语句也不同
			if (NewEquals.equals("0",type)) {
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
			} else if (NewEquals.equals("1",type)) {// 类型是全称时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("全称");
			} else if (NewEquals.equals("2",type)) {// 类型是简称时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("简称");
			} else if (NewEquals.equals("3",type)) {// 类型是代码时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("代码");
			} else if (NewEquals.equals("4",type)) {// 类型是错词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and s.type=? ");
				// 绑定类型参数
				lstpara.add("错词");
			} else if (NewEquals.equals("5",type)) {// 类型是其他别名时的SQL语句
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
		sql+=""+paramSql;
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 * 判断别名是否重复
	 * 
	 * @param stdwordid参数词条id
	 * @param synonym参数别名名称
	 * @return Boolean 是否重复
	 */
	public static Boolean exist(String stdwordid, String synonym) {
		// 对应绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 定义查询别名的SQL语句
		String sql ="";
		if (GetConfigValue.isOracle) {
			sql = "select word from word where  word=? and stdwordid=? and rownum<2 ";
		} else if (GetConfigValue.isMySQL) {
			sql = "select word from word where  word=? and stdwordid=? limit 0,2";
		}
		// 绑定别名名称参数
		lstpara.add(synonym);
		// 绑定词条id参数
		lstpara.add(stdwordid);
			// 执行SQL语句，获取相应的数据源
			Result rs = Database.executeQuery(sql, lstpara.toArray());
			
			//文件日志
			GlobalValue.myLog.info( sql + "#" + lstpara );
			
			// 判断数据源不为null且含有数据
			if (rs != null && rs.getRowCount() > 0) {
				// 有数据，表示重复
				return true;
			} else {
				// 没有数据，不是不重复
				return false;
			}
	}
	
	/**
	 * 更新别名
	 * 
	 * @param oldsynonym参数原有别名
	 * @param newsynonym参数新的别名
	 * @param oldtype参数原有类型
	 * @param newtype参数新的类型
	 * @param wordid参数别名id
	 * @param stdwordid参数词条id
	 * @return int 更新记录数
	 */
	public static int update(User user,String oldsynonym, String newsynonym,
			String oldtype, String newtype, String wordid, String stdwordid,String curwordclass) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 更新别名的SQL语句
		String sql ="";
		if (GetConfigValue.isOracle) {
			 sql = "update word set word=?,type=? ,time = sysdate where wordid=? ";
		} else if (GetConfigValue.isMySQL) {
			 sql = "update word set word=?,type=? ,time = sysdate() where wordid=? ";
		}
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
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 更新别名的SQL语句
		if (GetConfigValue.isOracle) {
			sql = "update wordclass set time =sysdate  where  wordclass =  ? "; 
		} else if (GetConfigValue.isMySQL) {
			sql = "update wordclass set time =sysdate()  where  wordclass =  ? "; 
		}
		// 定义绑定参数集合
		 lstpara = new ArrayList<String>();
		// 绑定别名名称参数
		lstpara.add(curwordclass);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstsql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", " ", "更新词条", oldsynonym
				+ "==>" + newsynonym, "WORD"));
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
	}
	
	/**
	 * 新增别名的操作
	 * @param User 用户信息(用于记录日志，可为null)
	 * @param wordclassid参数词类id
	 * @param lstSynonym参数别名集合
	 * @param stdwordid参数词条id
	 * @param type参数别名类型
	 * @param curworditem参数当前词条名称(用于记录日志，可为null)
	 * @param curwordclass参数当前词类名称(用于记录日志，可为null)
	 * @return int 新增返回的结果
	 */
     public static int insert(User user,String wordclassid, List<String> lstSynonym,
			String stdwordid, String type, String curworditem,
			String curwordclass) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义保存别名的SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara ;
		String city="";
		String cityName ="";
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(user.getIndustryOrganizationApplication());
		Result rs = CommonLibWordDAO.selectWordCity(curwordclass,stdwordid);
		cityName = rs.getRows()[0].get("cityname")==null ? "":rs.getRows()[0].get("cityname").toString();
		city = rs.getRows()[0].get("city")==null ? "":rs.getRows()[0].get("city").toString();
		
		// 循环遍历别名集合
		for (int i = 0; i < lstSynonym.size(); i++) {
			// 获取别名的序列值
			String id =null;
			if (GetConfigValue.isOracle) {
				id = ConstructSerialNum
						.GetOracleNextValNew("seq_word_id",bussinessFlag);
			} else if (GetConfigValue.isMySQL){ 
				id = ConstructSerialNum.getSerialIDNew("word", "wordid",bussinessFlag);
			}
			
			// 定义新增别名的SQL语句
			sql = "insert into word(wordid,wordclassid,word,stdwordid,type,city,cityname) values(?,?,?,?,?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定id参数
			lstpara.add(id);
			// 绑定词类id参数
			lstpara.add(wordclassid);
			// 绑定别名参数
			lstpara.add(lstSynonym.get(i));
			// 绑定词条id参数
			lstpara.add(stdwordid);
			// 绑定类型参数
			lstpara.add(type);
			
			lstpara.add(city);
			lstpara.add(cityName);
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			
			if(user!=null)
			{
			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), " ", " ", "增加别名", curwordclass
					+ "==>" + curworditem+ "==>" + lstSynonym.get(i), "WORD"));
			}
		}
		// 更新当前词类编辑时间
		if (GetConfigValue.isOracle) {
			sql = "update wordclass set time =sysdate  where  wordclassid = ? ";
		} else if (GetConfigValue.isMySQL) {
			sql = "update wordclass set time =sysdate()  where  wordclassid = ? ";
		}
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定词条参数
		lstpara.add(wordclassid);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}
     
 	/**
 	 * 删除别名
 	 * @param User 用户信息 
 	 * @param stdwordid参数别名id
 	 * @param synonym参数别名名称
 	 * @param curworditem参数词条名称
 	 * @param curwordclass参数词类名称
 	 * @return int 
 	 */
 	public static int delete(User user,String stdwordid, String synonym,
 			String curworditem, String curwordclass) {
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
 		// 将别名id按照逗号拆分
 		String[] ids = stdwordid.split(",");
 		// 循环遍历id数组
 		for (int i = 0; i < ids.length; i++) {
 			if (i != ids.length - 1) {
 				// 除了最后一个不加逗号，其他加上逗号
 				sql.append("?,");
 			} else {
 				// 最后一个加上右括号，将SQL语句补充完整
 				sql.append("?)");
 			}
 			// 绑定参数集合
 			lstpara.add(ids[i]);
 		}
 		// 将SQL语句放入集合中
 		lstsql.add(sql.toString());
 		// 将对应的绑定参数集合放入集合中
 		lstlstpara.add(lstpara);
 		
 		//文件日志
 		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
 		
 		// 更新当前词类编辑时间
 		String sql_update  ="";
 		if (GetConfigValue.isOracle) {
 		  sql_update = "update wordclass set time =sysdate  where  wordclass = ? ";
		} else if (GetConfigValue.isMySQL) {
		  sql_update = "update wordclass set time =sysdate()  where  wordclass = ? ";
		}
 		// 定义绑定参数集合
 		lstpara = new ArrayList<String>();
 		// 绑定词条参数
 		lstpara.add(curwordclass);
 		// 将SQL语句放入集合中
 		lstsql.add(sql_update);
 		// 将对应的绑定参数集合放入集合中
 		lstlstpara.add(lstpara);
 		
 		//文件日志
 		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
 		
 		// 生成操作日志记录
 		// 将别名按照逗号拆分
 		String[] synonyms = synonym.split(",");
 		// 循环遍历别名数组
 		for (int i = 0; i < synonyms.length; i++) {
 			// 生成操作日志记录
			// 将SQL语句放入集合中
			lstsql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), " ", " ", "删除别名", curwordclass
					+ "==>" + curworditem+ "==>" + synonyms[i], "WORD"));
 		}
 		// 执行SQL语句，绑定事务处理，返回事务处理的结果
 		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
 		// 判断事务处理结果
 		
 		return c;
 }
 	
 	
	
}
