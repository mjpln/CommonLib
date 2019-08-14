package com.knowology.DbDAO;

import java.io.Reader;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.SortedMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;
import javax.sql.DataSource;

import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import com.knowology.GlobalValue;
import com.knowology.ToolMethods;
import com.knowology.UtilityOperate.StringUtil;

/*************************
 * @function 数据库操作通用类
 * @version v1.0
 * @see 按jdbc标准，ResultSet, Statement, Connection都要close()，否则会出现资源泄漏的情况××××
 * @source from internet
 * @updater wwm
 * @date 2013-1-14
 */

// 这里我们建立访问数据库的通用类Database
@Deprecated
public class Database4NLPapp {
	/*
	 * 方法测试
	 */
	public static Logger logger = Logger.getLogger(Database4NLPapp.class);

	static Locale zhLoc=new Locale("zh","CN");
	public static boolean IsMySql=Boolean.parseBoolean(ToolMethods.getConfigValues("IsMysql"));

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws SQLException {
		String str = "select * from t_user";
		Result res = executeQuery(str);
		String strr = JSONObject.fromObject(res).toString();
		System.out.print(strr);
		SortedMap[] st = res.getRows();
		for (int i = 0; i < st.length; i++) {
			SortedMap stt = st[i];
			System.out.print(stt.get("USERNAME"));
		}
		System.out.print(res.getRowCount());
	}

	/*
	 * 读取配置文件参数
	 */
	public static String getJDBCValues(String key) {
		ResourceBundle resourcesTable = ResourceBundle.getBundle("jdbc");
		return resourcesTable.getString(key);
	}
	

	
	/**
	 * 
	 *描述：通用数据库连接开启入口
	 *调试模式：用普通连接方式
	 *发布模式:用连接池
	 *@author: qianlei
	 *@date： 日期：2015-2-11 时间：上午11:37:54
	 *@return
	 */
	public static Connection getConnectCommon()
	{
		if(GlobalValue.IsDebug)
		{
			return getCon();
		}
		else
		{
			return getConNew();
		}
	}

	// 此方法为获取数据库连接，此处以及后续文章中使用的都是MS SQL2005
	public static Connection getCon() {
		Connection con = null;
		try {
				if(IsMySql){
				// MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
				// 避免中文乱码要指定useUnicode和characterEncoding
				// 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
				// 下面语句之前就要先创建javademo数据库
				String url=getJDBCValues("jdbc.url");
				String driver = getJDBCValues("jdbc.driverClassName");
				Class.forName(driver); 
				//System.out.println("成功加载MySQL驱动程序");
	            // 一个Connection代表一个数据库连接
	            con = DriverManager.getConnection(url);
			  }
			  else{
			String driver = getJDBCValues("jdbc.driverClassName");// "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			// 数据库驱动
			String url = getJDBCValues("jdbc.url");// "jdbc:sqlserver://localhost:1433;DatabaseName=FileManager";//
			String user = getJDBCValues("jdbc.username");// "admin"; // 用户名
			String password = getJDBCValues("jdbc.password");// "123456";// 密码
			Class.forName(driver); // 加载数据库驱动
			con = DriverManager.getConnection(url, user, password);
		}
		} catch (Exception e) {
			e.printStackTrace();
			con = null;
		}
		return con;
	}

	/**
	 * 
	 *描述：用数据库连接池连接数据库
	 *@author: qianlei
	 *@date： 日期：2015-2-11 时间：上午11:46:00
	 *@return
	 */
	public static Connection getConNew() {
		DataSource ds = null;
		Connection conn = null;

		try {
			Context initCtx = new InitialContext();

			Context envCtx = (Context) initCtx.lookup("java:comp/env");

			if (IsMySql) {
				ds = (DataSource) envCtx.lookup("jdbc/mysql");
			} else {
				ds = (DataSource) envCtx.lookup("jdbc/oracle");
			}

			conn = ds.getConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception ex) {
			// 写异常日志
		}
	}

	static void close(Statement stmt) {
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception ex) {
			// 写异常日志
		}
	}

	static void close(Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (Exception ex) {
			// 写异常日志
		}
	}

	// 查询语句
	public static Result executeQuery(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		Result result = null;
		try {
			con = getConnectCommon();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs != null) {
				result = ResultSupport.toResult(rs);
			}
		} catch (SQLException e) {
			logger.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(rs);
			close(stmt);
			close(con);
		}
		return result;
	}

	public static Result executeQuery(String sql, Object... obj)
			throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Result result = null;
		try {
			con = getConnectCommon();
			pstmt = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				pstmt.setObject(i + 1, obj[i]);
			}
			rs = pstmt.executeQuery();
			if (rs != null) {
				result = ResultSupport.toResult(rs);
			}
		} catch (SQLException e) {
			logger.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(rs);
			close(pstmt);
			close(con);
		}
		return result;
	}

	/**
	 * 执行非查询增删改
	 */
	public static int executeNonQuery(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		int result = 0;
		try {
			con = getConnectCommon();
			stmt = con.createStatement();
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			logger.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(stmt);
			close(con);
		}
		return result;
	}

	/**
	 * 执行非查询增删改
	 */
	public static int executeNonQuery(String sql, Object... obj)
			throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;
		try {
			con = getConnectCommon();
			pstmt = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				pstmt.setObject(i + 1, obj[i]);
			}
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			logger.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(pstmt);
			close(con);
		}
		return result;
	}

	public static int executeNonQueryTransaction(List<String> listSqls,
			List<List<?>> listParams) {
		int ret = 0;// 返回执行成功影响的条数
		Connection con = null;// 获取数据库连接
		PreparedStatement pstm = null;
		String sql = "";
		try {
			con = getConnectCommon();// 获取数据库连接
			con.setAutoCommit(false);// 设置自动提交为false
			for (int i = 0; i < listSqls.size(); i++) {
				sql = listSqls.get(i);
				pstm = con.prepareStatement(sql);// 创建PreparedStatement
				Object[] objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据
				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstm.setObject(j + 1, objs[j]);
				}
				ret += pstm.executeUpdate();// 执行sql语句
				pstm.close();
			}
			con.commit();// 执行完成后，进行事务提交
			con.close();
		} catch (SQLException e) {
			ret = 0;
			try {
				con.rollback();// 出现异常后，事务回滚
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
			}
			logger.error("异常sql==>" + sql + " 异常信息==>" + e);
			e.printStackTrace();
		} finally {
			close(pstm);
			close(con);
		}
		return ret;
	}

	public static int executeNonQueryBatchTransaction(String sql,
			List<List<?>> listParams) {
		int ret = 0;// 返回执行成功影响的条数
		Connection con = null;// 获取数据库连接
		PreparedStatement pstm = null;
		try {
			con = getConnectCommon();// 获取数据库连接
			con.setAutoCommit(false);// 设置自动提交为false
			pstm = con.prepareStatement(sql);
			for (int i = 0; i < listParams.size(); i++) {
				Object[] objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据
				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstm.setObject(j + 1, objs[j]);
				}
				pstm.addBatch();
				// 判断凑够20个，发送执行
				if (i % 20 == 0) {
					pstm.executeBatch();
				}
			}
			int[] c = pstm.executeBatch();// 执行sql语句
			ret = c.length;
			pstm.close();
			con.commit();// 执行完成后，进行事务提交
			con.close();
		} catch (SQLException e) {
			ret = 0;
			try {
				con.rollback();// 出现异常后，事务回滚
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
			}
			logger.error("异常sql==>" + sql + " 异常信息==>" + e);
			e.printStackTrace();
		} finally {
			close(pstm);
			close(con);
		}
		return ret;
	}

	public static Result executeQueryTransaction(List<String> listSql,
			List<List<?>> listListparam) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Result result = null;
		String sql = "";
		try {
			con = getConnectCommon();
			// 加入事务处理
			con.setAutoCommit(false);// 设置不能默认提交
			for (int i = 0; i < listSql.size(); i++) {
				sql = listSql.get(i);
				pstmt = con.prepareStatement(sql);// 创建PreparedStatement
				Object[] objs = listListparam.get(i).toArray();// 获取第i个sql语句对应的参数数据
				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstmt.setObject(j + 1, objs[j]);
				}
				rs = pstmt.executeQuery();
			}
			if (rs != null) {
				result = ResultSupport.toResult(rs);
			}
			con.commit();
		} catch (Exception e) {
			// 如果发生异常，就回滚
			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
			}
			logger.error("异常sql==>" + sql + " 异常信息==>" + e);
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
			close(con);
		}
		return result;
	}

	public static Result executeQueryTransaction(List<String> listSql,
			List<List<?>> listListparam, int index) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Result result = null;
		String sql = "";
		try {
			con = getConnectCommon();
			// 加入事务处理
			con.setAutoCommit(false);// 设置不能默认提交
			for (int i = 0; i < listSql.size(); i++) {
				sql = listSql.get(i);
				pstmt = con.prepareStatement(sql);// 创建PreparedStatement
				Object[] objs = listListparam.get(i).toArray();// 获取第i个sql语句对应的参数数据
				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstmt.setObject(j + 1, objs[j]);
				}
				if (index == i) {
					rs = pstmt.executeQuery();
					if (rs != null) {
						result = ResultSupport.toResult(rs);
					}
				} else {
					pstmt.execute();
				}
			}
			con.commit();
		} catch (Exception e) {
			// 如果发生异常，就回滚
			try {
				con.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
				e1.printStackTrace();
			}
			logger.error("异常sql==>" + sql + " 异常信息==>" + e);
			e.printStackTrace();
		} finally {
			close(rs);
			close(pstmt);
			close(con);
		}
		return result;
	}

	/*
	 * sql例子：{ call HYQ.TESTA(?,?) }
	 */
	public static void executeProcWithoutreturn(String sql, List<?> lstPara) {
		Connection conn = null;
		CallableStatement proc = null;
		try {
			conn = getConnectCommon();
			proc = conn.prepareCall(sql);
			for (int i = 0; i < lstPara.size(); i++) {
				proc.setObject(i + 1, lstPara.get(i));
			}
			proc.execute();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		} finally {
			close(conn);
		}
	}

	public static JSONObject executeProcWithreturn(String sql, List<?> lstPara) {
		Connection con = null;
		CallableStatement proc = null;
		int len = lstPara.size();
		try {
			con = getConnectCommon();
			proc = con.prepareCall(sql);
			for (int i = 0; i < len; i++) {
				proc.setObject(i + 1, lstPara.get(i));
			}
			proc.registerOutParameter(len + 1, java.sql.Types.JAVA_OBJECT);
			proc.execute();
			JSONObject ret = (JSONObject) proc.getObject(len + 1);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			close(con);
		}
	}

	public static JSONObject executeFun(String sql, List<?> lstPara) {
		Connection con = null;
		CallableStatement proc = null;
		int len = lstPara.size();
		try {
			con = getConnectCommon();
			proc = con.prepareCall(sql);
			int sqlType;
			sqlType = oracle.jdbc.OracleTypes.JAVA_STRUCT;
			String typeName;
			typeName = "JSON";
			proc.registerOutParameter(1, sqlType, typeName);
			for (int i = 0; i < len; i++) {
				proc.setObject(i + 2, lstPara.get(i));
			}
			proc.execute();
			java.sql.Struct jdbcStruct = (java.sql.Struct) proc.getObject(1);
			Object[] attrs = jdbcStruct.getAttributes();
			// 获取第一个属性json_data，其oracle类型是json_value_array
			java.sql.Array jdbcArray = (java.sql.Array) attrs[0];
			Object obj = jdbcArray.getArray();// 通过getArray方法，会返回Object对象
			Object[] javaArray = (Object[]) obj;// 将obj强转为最终类型的数组

			for (int i = 0; i < javaArray.length; i++) {
				System.out.println(javaArray[i].toString());
			}
			JSONObject ret = JSONObject.fromObject(obj);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			close(con);
		}
	}

	public static String executeFunClob(String sql, List<?> lstPara) {
		Connection con = null;
		CallableStatement proc = null;
		int len = lstPara.size();
		try {
			con = getConnectCommon();
			proc = con.prepareCall(sql);
			int sqlType;
			sqlType = oracle.jdbc.OracleTypes.CLOB;
			proc.registerOutParameter(1, sqlType);
			for (int i = 0; i < len; i++) {
				proc.setObject(i + 2, lstPara.get(i));
			}
			proc.execute();
			Clob ret = proc.getClob(1);
			return StringUtil.ClobToString(ret);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			close(con);
		}
	}

	/**
	 * @function 批量处理多条SQL语句
	 * @param sqls
	 *            需要处理的sql语句数组
	 * @param objs
	 *            对应的绑定变量参数数组
	 * @return
	 */
	public static int executeSql(List<String> sqls, List<List<Object[]>> objs)
			throws SQLException {
		Connection con = null;
		PreparedStatement[] pstmts = new PreparedStatement[sqls.size()];
		try {
			con = getConnectCommon();
			con.setAutoCommit(false);
			for (int i = 0; i < sqls.size(); i++) {
				pstmts[i] = con.prepareStatement(sqls.get(i));
				for (int j = 0; j < objs.get(0).get(i).length; j++) {
					pstmts[i].setObject(j + 1, objs.get(0).get(i)[j]);
				}
				pstmts[i].executeUpdate();
			}
			con.commit();
			con.setAutoCommit(true);
			return 1;
		} catch (SQLException e) {
			logger.error("execute执行事务处理时发生错误：" + e.getMessage());
			con.rollback();
			return -1;
		} finally {
			for (int i = 0; i < sqls.size(); i++) {
				close(pstmts[i]);
			}
			close(con);
		}
	}

	// <summary>
	// 从一个DataRow中，安全得到列colname中的值：值为字符串类型
	// </summary>
	// <param name="row">数据行对象</param>
	// <param name="colname">列名</param>
	// <returns>如果值存在，返回；否则，返回System.String.Empty</returns>
	@SuppressWarnings("unchecked")
	public static String ValidateDataRow_S(SortedMap row, String colname) {
		try {
			if (row != null) {
				if (row.get("colname") != null)
					return row.get("colname").toString();
				else
					return "";
			} else
				return "";
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// <summary>
	// 从一个DataRow中，安全得到列colname中的值：值为整数类型
	// </summary>
	// <param name="row">数据行对象</param>
	// <param name="colname">列名</param>
	// <returns>如果值存在，返回；否则，返回System.Int32.MinValue</returns>
	@SuppressWarnings("unchecked")
	public static int ValidateDataRow_N(SortedMap row, String colname) {
		try {
			if (row != null) {
				if (row.get(colname) != null)
					return Integer.parseInt((String) row.get(colname));
				else
					return Integer.MIN_VALUE;
			} else
				return Integer.MIN_VALUE;
		} catch (Exception e) {
			return -1;
		}
	}
	
	/*
	 * 执行一组SQL，返回执行是否成功
	 */
    public static Boolean ExecuteSQL (List<String> SqlStrings)
    {
        //执行结果
    	Boolean success = true;
		Connection con = null;
		Statement stmt = null;
        try
        {
			con = getCon();
			stmt = con.createStatement();
            //执行每条sql
            for (String str :SqlStrings)
            {
    			stmt.executeUpdate(str);
            }
        }
        //某条语句出错，则回滚事务
        catch (Exception ex)
        {
        	logger.error("====>>Error："+ ex.toString());
            success = false;
        }
        finally 
 		{
			close(stmt);
			close(con);
		}
        return success;
    }
    	
	public static void Dispose(Connection newcon, Statement newstmt,ResultSet newrs){	
		//Lock ll = new ReentrantLock();
		//ll.lock();
		try {
			if(newrs!=null)newrs.close();
			if(newstmt!=null)newstmt.close();
			if(newcon!=null)newcon.close();
		} catch (Exception e) {
			logger.error("数据库连接关闭时异常，"+e.toString());		
		}
		//finally{
			//ll.unlock();
		//}
		
	}
	public static int executeNonQueryTransaction_clob(String sql,List<?> Params) {
		  int ret = 0;// 返回执行成功影响的条
		  Connection con = null;// 获取数据库连
		  PreparedStatement pstm = null;
		  try {
		   con = getConnectCommon();//getCon();// 获取数据库
		   con.setAutoCommit(false);// 设置自动提交为false
		    pstm = con.prepareStatement(sql);// 创建
		    Object[] objs = Params.toArray();// 获取第i个sql语句对应的参数数
		    for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
		     Object temp = objs[j];
		     if(Text_Length(temp.toString())>=4000){
		      Reader clobReader = new StringReader(temp.toString()); // 将 text转成流形式
		      pstm.setCharacterStream(j+1, clobReader, temp.toString().length());// 替换sql语句中的？ 
		     }
		     else{
		      pstm.setObject(j + 1, temp); 
		     }
		    }
		    ret += pstm.executeUpdate();// 执行sql语句
		    pstm.close();

		   con.commit();//进行事务提交
		   con.close();
		  } catch (SQLException e) {
		   ret = 0;
		   try {
		    pstm.close();
		    con.rollback();// 出现异常后，事务回滚
		    con.setAutoCommit(true);
		    e.printStackTrace();
		    logger.error("executeNonQueryTransaction_clob时异常，sql:"+sql+e.toString());
	
		   } catch (SQLException e1) {
			    logger.error("executeNonQueryTransaction_clob时异常，sql:"+sql+e.toString());
		    e1.printStackTrace();
		   }
		    logger.error("executeNonQueryTransaction_clob时异常，sql:"+sql+e.toString());
		   e.printStackTrace();
		  } finally {
		   close(pstm);
		   close(con);
		  }
		  return ret;
		 }
	
	public static int executeNonQueryTransaction_clob(List<String> listSqls,List<List<?>> listParams) {
		  int ret = 0;// 返回执行成功影响的条
		  Connection con = null;// 获取数据库连
		  PreparedStatement pstm = null;
		  try {
		   con = getConnectCommon();//getCon();// 获取数据库
		   con.setAutoCommit(false);// 设置自动提交为false
		   for (int i = 0; i < listSqls.size(); i++) {
		    pstm = con.prepareStatement(listSqls.get(i));// 创建
		    Object[] objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数
		    for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
		     Object temp = objs[j];
		     if(Text_Length(temp.toString())>=4000){
		      Reader clobReader = new StringReader(temp.toString()); // 将 text转成流形式
		      pstm.setCharacterStream(j+1, clobReader, temp.toString().length());// 替换sql语句中的？ 
		     }
		     else{
		      pstm.setObject(j + 1, temp); 
		     }
		    }
		    ret += pstm.executeUpdate();// 执行sql语句
		    pstm.close();
		   }
		   con.commit();//进行事务提交
		   con.close();
		  } catch (SQLException e) {
		   ret = 0;
		   try {
		    pstm.close();
		    con.rollback();// 出现异常后，事务回滚
		    con.setAutoCommit(true);
		    e.printStackTrace();
		   } catch (SQLException e1) {

		    e1.printStackTrace();
		   }
		   logger.info(e);
		   e.printStackTrace();
		  } finally {
		   close(pstm);
		   close(con);
		  }
		  return ret;
		 }
		 
	static int Text_Length(String Text) {
		  int len = 0;
		  for (int i = 0; i < Text.length(); i++) {
		   byte[] byte_len = Text.substring(i, i + 1).getBytes();
		   if (byte_len.length > 1)
		    len += 2; // 如果长度大于1，是中文，占两个字节，+2
		   else
		    len += 1; // 如果长度等于1，是英文，占一个字节，+1
		  }
		  return len;
		 }
}