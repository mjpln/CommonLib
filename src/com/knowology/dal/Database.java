package com.knowology.dal;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.BatchUpdateException;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;
import javax.servlet.jsp.jstl.sql.ResultSupport;
import javax.sql.DataSource;

import net.sf.json.JSONObject;

import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.knowology.GlobalValue;
import com.knowology.DbDAO.DBValueOper;
import com.knowology.UtilityOperate.StringUtil;

/*************************
 * @function 数据库操作通用类
 * @version v1.0
 * @see 按jdbc标准，ResultSet, Statement, Connection都要close()，否则会出现资源泄漏的情况××××
 */

/**
 * @author wellhan
 * 
 */
public class Database {

	/**
	 * 定义全局 dataSource
	 */
	private static DataSource dataSource;

	/** true:mysql连接 */
	public static boolean isMySQL;
	/** true:oralce连接 */
	public static boolean isOracle;
	/** true:toMysql转换 */
	public static boolean isToMysql;
	// 秘钥
	public static final String TOKEN = "VFNSMzQ1Njc4OTAxMjM0NTY3ODkwVFNS";
	/**
	 *描述：@description 阿里巴巴数据库连接池应用
	 */
	static {
		isToMysql = ("true"
				.equalsIgnoreCase(getCommmonLibGlobalValues("isToMysql")) ? true
				: false);
		isMySQL = ("mysql"
				.equalsIgnoreCase(getCommmonLibGlobalValues("connectFrom")) ? true
				: false);
		isOracle = ("oracle"
				.equalsIgnoreCase(getCommmonLibGlobalValues("connectFrom")) ? true
				: false);

		String isUsedataPool = getCommmonLibGlobalValues("isUsedataPool");
		if ("yes".equalsIgnoreCase(isUsedataPool)) {
			// 使用数据库连接池
			String conObject = getCommmonLibGlobalValues("connectFrom");
			conObject = conObject.replace(" ", "");
			if (isToMysql)
				conObject = "mysql";
			try {
				Properties props = new Properties();

				String jdbcProPath = "jdbc_" + conObject + ".properties";
				InputStream in = Database.class.getClassLoader()
						.getResourceAsStream(jdbcProPath);
				props.load(in);

				// String jdbcProPath = "/cpic/cpicapp/conf/jdbc_" + conObject +
				// ".properties";
				// props.load(new FileInputStream(jdbcProPath));

				boolean isEncrypt = props.getProperty("isEncrypt") == null ? false
						: (props.getProperty("isEncrypt").equals("true") ? true
								: false);

				if (isEncrypt) {
					String url = props.getProperty("url");
					String username = props.getProperty("username");
					String password = props.getProperty("password");
					props.setProperty("url",
							DESBase64.decryptStringBase64(url, TOKEN));
					props.setProperty("username",
							DESBase64.decryptStringBase64(username, TOKEN));
					props.setProperty("password",
							DESBase64.decryptStringBase64(password, TOKEN));
				}

				dataSource = DruidDataSourceFactory.createDataSource(props);

			} catch (Exception e) {
				GlobalValue.myLog.error(e.getMessage(), e);
			}
		}
	}

	/**
	 *描述：@description 读取全局配置文件参数 参数：@param key 参数：@return value
	 * 返回值类型：@returnType String 创建时间：@dateTime 2015-9-21下午01:45:09 作者：@author
	 * wellhan
	 */
	public static String getCommmonLibGlobalValues(String key) {
		// String result = "";
		//
		// Properties prop = new Properties();
		// try {
		// prop
		// .load(new FileInputStream(
		// "/cpic/cpicapp/conf/commonLibGlobal.properties"));
		// result = prop.getProperty(key);
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// return result;

		ResourceBundle resourcesTable = ResourceBundle
				.getBundle("commonLibGlobal");
		return resourcesTable.getString(key);
	}

	/**
	 *描述：@description 读取jdbc参数 参数：@param key 参数：@return value 返回值类型：@returnType
	 * String 创建时间：@dateTime 2015-9-21下午01:44:52 作者：@author wellhan
	 */
	public static String getCommmonLibJDBCValues(String key) {
		String conObject = getCommmonLibGlobalValues("connectFrom");
		if ("".equals(conObject) || conObject == null) {
			return null;
		}
		conObject = conObject.replace(" ", "");
		if (isToMysql)
			conObject = "mysql";
		String jdbcProPath = "jdbc_" + conObject;
		try {
			// String result = "";
			//
			// Properties prop = new Properties();
			// try {
			// prop.load(new FileInputStream("/cpic/cpicapp/conf/" + jdbcProPath
			// + ".properties"));
			// result = prop.getProperty(key);
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// return result;
			ResourceBundle resourcesTable = ResourceBundle
					.getBundle(jdbcProPath);
			return resourcesTable.getString(key);

		} catch (Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
			return "";
		}
	}

	/**
	 *描述：@description 读取jdbc参数 参数：@param key 参数：@return value 返回值类型：@returnType
	 * String 创建时间：@dateTime 2015-9-21下午01:44:52 作者：@author wellhan
	 */
	public static String getCommmonLibJDBCDecryptValues(String key) {
		String conObject = getCommmonLibGlobalValues("connectFrom");
		if ("".equals(conObject) || conObject == null) {
			return null;
		}
		conObject = conObject.replace(" ", "");
		if (isToMysql)
			conObject = "mysql";
		String jdbcProPath = "jdbc_" + conObject;
		try {
			// String result = "";
			//
			// Properties prop = new Properties();
			// try {
			// prop.load(new FileInputStream("/cpic/cpicapp/conf/" + jdbcProPath
			// + ".properties"));
			// result = prop.getProperty(key);
			// } catch (FileNotFoundException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			//
			// return result;
			ResourceBundle resourcesTable = ResourceBundle
					.getBundle(jdbcProPath);
			String value = resourcesTable.getString(key);
			value = DESBase64.decryptStringBase64(value, TOKEN);
			return value;

		} catch (Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
			return "";
		}
	}

	/**
	 *描述：@description 获取数据库连接 参数：@return 返回值类型：@returnType Connection
	 * 创建时间：@dateTime 2015-9-21下午01:27:52 作者：@author wellhan
	 */
	public static Connection getCon() {
		Connection con = null;
		String isUsedataPool = getCommmonLibGlobalValues("isUsedataPool");
		if ("".equals(isUsedataPool) || isUsedataPool == null) {
			return null;
		} else {
			if ("yes".equalsIgnoreCase(isUsedataPool)) {// 使用数据库连接池
				try {
					con = dataSource.getConnection();
					con = getValidConnection(con);
				} catch (SQLException e) {
					GlobalValue.myLog.error(e.getMessage(), e);
					con = null;
				}
			} else {// 使用数据库普通连接
				try {
					String driver = getCommmonLibJDBCValues("driverClassName"); // 数据库驱动
					
					boolean isEncrypt = getCommmonLibJDBCValues("isEncrypt") == null ? false
							: (getCommmonLibJDBCValues("isEncrypt").equals(
									"true") ? true : false);
					if (isEncrypt) {
						String url = getCommmonLibJDBCDecryptValues("url");// 连接地址
						String user = getCommmonLibJDBCDecryptValues("username");// 用户名
						String password = getCommmonLibJDBCDecryptValues("password");// 密码
						Class.forName(driver); // 加载数据库驱动
						con = DriverManager.getConnection(url, user, password);
					} else {
						String url = getCommmonLibJDBCValues("url");// 连接地址
						String user = getCommmonLibJDBCValues("username");// 用户名
						String password = getCommmonLibJDBCValues("password");// 密码
						Class.forName(driver); // 加载数据库驱动
						con = DriverManager.getConnection(url, user, password);
					}
				} catch (Exception e) {
					GlobalValue.myLog.error(e.getMessage(), e);
					con = null;
				}
			}
		}
		return con;
	}

	/**
	 *@description 获得验证后连接
	 *@param oldConn
	 *@return
	 *@throws SQLException
	 *@returnType Connection
	 */
	private static Connection getValidConnection(Connection oldConn)
			throws SQLException {
		Connection conn = oldConn;
		int commonTimeout = 5;
		while (null == conn || conn.isClosed() || !conn.isValid(commonTimeout)) {
			if (null != conn && !conn.isClosed()) {
				GlobalValue.myLog
						.info("get connection by dataSource but connection is invalid , invalid connection: "
								+ conn);
				conn.close();
			}
			return getValidConnection(dataSource.getConnection());

		}
		return conn;
	}

	/**
	 *描述：@description 阿里巴巴数据库连接池应用 参数：@return 返回值类型：@returnType Connection
	 * 创建时间：@dateTime 2015-9-21下午01:27:59 作者：@author wellhan
	 */
	private static Connection getDruidCon() {// isUsedataPool
		DataSource ds = null;
		String conObject = getCommmonLibGlobalValues("connectFrom");
		if ("".equals(conObject) || conObject == null) {
			return null;
		}
		conObject = conObject.replace(" ", "");
		String jdbcProPath = "jdbc_" + conObject + ".properties";
		InputStream in = Database.class.getClassLoader().getResourceAsStream(
				jdbcProPath);
		Properties props = new Properties();
		try {
			props.load(in);
			ds = DruidDataSourceFactory.createDataSource(props);
		} catch (Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
			return null;
		}
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			GlobalValue.myLog.error(e.getMessage(), e);
			return null;
		}
	}

	protected static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception ex) {
			GlobalValue.myLog.error("关闭连接异常信息==>" + ex);
		}
	}

	/**
	 *@description 关闭 Statement
	 *@param stmt
	 *@returnType void
	 */
	protected static void close(Statement stmt) {
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception ex) {
			// 写异常日志
			GlobalValue.myLog.error(ex.getMessage(), ex);
		}
	}

	/**
	 *@description 关闭 Connection
	 *@param con
	 *@returnType void
	 */
	protected static void close(Connection con) {
		try {
			if (con != null && !con.isClosed())
				con.close();

		} catch (Exception ex) {
			GlobalValue.myLog.error("关闭连接异常信息==>" + ex);
		}
	}

	/**
	 *@description 返回可执行sql result
	 *@param sql
	 *@return
	 *@returnType Result
	 */
	public static Result executeQueryReport(String sql) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		Result result = null;

		if (isToMysql) {
			System.out.println("-----------------");
			// sql = Trans.transform(sql);////ghj
			// GlobalValue.myLog.info(sql);
			MysqlTransfer mt = new MysqlTransfer(sql, null);
			mt.transfer();
			sql = mt.getMysqlSql();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs != null) {
				result = ResultSupport.toResult(rs);
			}
		} catch (Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
		} finally {
			close(rs);
			close(stmt);
			close(con);
		}
		return result;
	}

	/**
	 *@description 返回可执行sql result
	 *@param sql
	 *@return
	 *@returnType Result
	 */
	public static Result executeQuery(String sql) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		Result result = null;

		if (isToMysql) {
			System.out.println("-----------------");
			MysqlTransfer mt = new MysqlTransfer(sql, null);
			mt.transfer();
			sql = mt.getMysqlSql();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs != null) {
				result = ResultSupport.toResult(rs);
			}
		} catch (SQLException e) {
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(rs);
			close(stmt);
			close(con);
		}
		return result;
	}

	/**
	 * @author wyz
	 * @param sql
	 * @return String 用于mysql中自定义的函数
	 * @throws SQLException
	 * 
	 */
	public static String executeQueryAisa(String sql) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String result = null;

		if (isToMysql) {
			System.out.println("-----------------");
			MysqlTransfer mt = new MysqlTransfer(sql, null);
			mt.transfer();
			sql = mt.getMysqlSql();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			if (rs != null) {
				rs.next();
				result = rs.getString(1);
			}
		} catch (SQLException e) {
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(rs);
			close(stmt);
			close(con);
		}
		return result;
	}

	/**
	 * @author wyz
	 * @param sql
	 * @param obj
	 * @return String 用于mysql中自定义的函数
	 */
	public static String executeQueryAisa(String sql, Object... obj) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String result = null;

		if (isToMysql) {
			System.out.print("--------------");
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null)

					System.out.print(obj[i].toString() + "	");
			}
			System.out.println();
			MysqlTransfer mt = new MysqlTransfer(sql, obj);
			mt.transfer();
			sql = mt.getMysqlSql();
			obj = mt.getParams();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			pstmt = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				pstmt.setObject(i + 1, obj[i]);

			}
			rs = pstmt.executeQuery();
			if (rs != null) {
				rs.next();
				result = rs.getString(1);
			}
		} catch (SQLException e) {
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(rs);
			close(pstmt);
			close(con);
		}
		return result;
	}

	/**
	 *@description 返回带参数 sql Result
	 *@param sql
	 *@param obj
	 *@return
	 *@throws SQLException
	 *@returnType Result
	 */
	public static Result executeQueryReport(String sql, Object... obj)
			throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Result result = null;

		if (isToMysql) {
			System.out.print("--------------");
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null)

					System.out.print(obj[i].toString() + "	");
			}
			System.out.println();
			MysqlTransfer mt = new MysqlTransfer(sql, obj);
			mt.transfer();
			sql = mt.getMysqlSql();
			obj = mt.getParams();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			pstmt = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				pstmt.setObject(i + 1, obj[i]);
			}
			rs = pstmt.executeQuery();
			if (rs != null) {
				result = ResultSupport.toResult(rs);
			}
		} finally {
			close(rs);
			close(pstmt);
			close(con);
		}
		return result;
	}

	/**
	 *@description 返回带参数 sql Result
	 *@param sql
	 *@param obj
	 *@return
	 *@returnType Result
	 */
	public static Result executeQuery(String sql, Object... obj) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Result result = null;

		if (isToMysql) {
			System.out.print("--------------");
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null)

					System.out.print(obj[i].toString() + "	");
			}
			System.out.println();
			MysqlTransfer mt = new MysqlTransfer(sql, obj);
			mt.transfer();
			sql = mt.getMysqlSql();
			obj = mt.getParams();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			pstmt = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				pstmt.setObject(i + 1, obj[i]);
			}
			rs = pstmt.executeQuery();
			if (rs != null) {
				result = ResultSupport.toResult(rs);
			}
		} catch (SQLException e) {
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(rs);
			close(pstmt);
			close(con);
		}
		return result;
	}

	public static List executeQueryClob(String sql) {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;

		List<Map<Object, Object>> list = new ArrayList<Map<Object, Object>>();
		try {
			con = getCon();
			stmt = con.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData md = rs.getMetaData();
			int columncount = md.getColumnCount();
			list = extractData(rs);// 将查询的结果
			System.out.println(list);
			if (null == rs) {
				System.out.println("rs is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(rs);
			close(stmt);
			close(con);
		}

		return list;
	}

	private static List<Map<Object, Object>> extractData(ResultSet rs)
			throws SQLException {
		List<Map<Object, Object>> listOfRows = new ArrayList();
		ResultSetMetaData md = rs.getMetaData();

		int num = md.getColumnCount();
		while (rs.next()) {
			Map<Object, Object> mapOfColValues = new HashMap<Object, Object>();
			for (int i = 1; i <= num; i++) {
				String columnName = md.getColumnName(i);
				mapOfColValues.put(columnName, rs.getObject(i));
			}
			listOfRows.add(mapOfColValues);
		}
		return listOfRows;
	}

	/**
	 *@description 返回可执行sql 执行记录数
	 *@param sql
	 *@return
	 *@throws SQLException
	 *@returnType int
	 */
	public static int executeNonQueryReport(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		int result = 0;

		if (isToMysql) {
			System.out.println("--------------");
			MysqlTransfer mt = new MysqlTransfer(sql, null);
			mt.transfer();
			sql = mt.getMysqlSql();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			stmt = con.createStatement();
			result = stmt.executeUpdate(sql);
		} finally {
			close(stmt);
			close(con);
		}
		return result;
	}

	/**
	 *@description 返回带参数sql 执行记录数
	 *@param sql
	 *@return
	 *@throws SQLException
	 *@returnType int
	 */
	public static int executeNonQueryReport(String sql, Object... obj)
			throws SQLException {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;

		if (isToMysql) {
			System.out.print("--------------");
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null)

					System.out.print(obj[i].toString() + "	");
			}
			System.out.println();
			MysqlTransfer mt = new MysqlTransfer(sql, obj);
			mt.transfer();
			sql = mt.getMysqlSql();
			obj = mt.getParams();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			pstmt = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				pstmt.setObject(i + 1, obj[i]);
			}
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
			GlobalValue.myLog.error("【SQL异常信息】" + e.toString());
			GlobalValue.myLog.error("【异常SQL】" + sql);
			String outParam = "";
			for (Object p : obj) {
				outParam += "$-$" + DBValueOper.GetValidateStringObj4Null(p);
			}
			GlobalValue.myLog.error("【异常SQL参数】" + outParam);
		} finally {
			close(pstmt);
			close(con);
		}
		return result;
	}

	/**
	 *@description 返回可执行sql 执行记录数
	 *@param sql
	 *@return
	 *@throws SQLException
	 *@returnType int
	 */
	public static int executeNonQuery(String sql) throws SQLException {
		Connection con = null;
		Statement stmt = null;
		int result = 0;

		if (isToMysql) {
			System.out.println("--------------");
			MysqlTransfer mt = new MysqlTransfer(sql, null);
			mt.transfer();
			sql = mt.getMysqlSql();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			stmt = con.createStatement();
			result = stmt.executeUpdate(sql);
		} catch (SQLException e) {
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(stmt);
			close(con);
		}
		return result;
	}

	/**
	 *@description 返回带参数sql 执行记录数
	 *@param sql
	 *@return
	 *@throws SQLException
	 *@returnType int
	 */
	public static int executeNonQuery(String sql, Object... obj) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;

		if (isToMysql) {
			System.out.print("--------------");
			for (int i = 0; i < obj.length; i++) {
				if (obj[i] != null)
					System.out.print(obj[i].toString() + "	");
			}
			System.out.println();
			MysqlTransfer mt = new MysqlTransfer(sql, obj);
			mt.transfer();
			sql = mt.getMysqlSql();
			obj = mt.getParams();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();
			pstmt = con.prepareStatement(sql);
			for (int i = 0; i < obj.length; i++) {
				pstmt.setObject(i + 1, obj[i]);
			}
			result = pstmt.executeUpdate();
		} catch (SQLException e) {
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(pstmt);
			close(con);
		}
		return result;
	}

	/**
	 *@description 返回事务处理sql 执行记录数
	 *@param listSqls
	 *@param listParams
	 *@return
	 *@returnType int
	 */
	public static int executeNonQueryTransaction(List<String> listSqls,
			List<List<?>> listParams) {
		int ret = 0;// 返回执行成功影响的条数
		Connection con = null;// 获取数据库连接
		PreparedStatement pstm = null;
		String sql = "";
		Object[] objs = null;
		try {
			con = getCon();// 获取数据库连接
			con.setAutoCommit(false);// 设置自动提交为false
			for (int i = 0; i < listSqls.size(); i++) {
				sql = listSqls.get(i);

				objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据

				if (isToMysql) {
					System.out.print("--------------");
					for (int ii = 0; ii < objs.length; ii++) {
						if (objs[ii] != null)
							System.out.print(objs[ii].toString() + "	");
					}
					System.out.println();
					MysqlTransfer mt = new MysqlTransfer(sql, objs);
					mt.transfer();
					sql = mt.getMysqlSql();
					objs = mt.getParams();
					GlobalValue.myLog.info(mt);
				}

				pstm = con.prepareStatement(sql);// 创建PreparedStatement

				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstm.setObject(j + 1, objs[j]);
				}
				ret += pstm.executeUpdate();// 执行sql语句
				pstm.close();
				// System.out.println(ret);
			}
			con.commit();// 执行完成后，进行事务提交
			con.close();
		} catch (SQLException e) {
			ret = 0;
			try {
				con.rollback();// 出现异常后，事务回滚
			} catch (SQLException e1) {
				GlobalValue.myLog.error(e1.toString());
				e1.printStackTrace();
			}
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(pstm);
			close(con);
		}
		return ret;
	}

	/**
	 *@description 返回事务处理sql 执行记录数
	 *@param listSqls
	 *@param listParams
	 *@return
	 *@returnType int
	 */
	public static int executeNonQueryTransactionReport(List<String> listSqls,
			List<List<?>> listParams) {
		int ret = 0;// 返回执行成功影响的条数
		Connection con = null;// 获取数据库连接
		PreparedStatement pstm = null;
		String sql = "";
		int i = 0;
		try {
			con = getCon();// 获取数据库连接
			con.setAutoCommit(false);// 设置自动提交为false
			for (; i < listSqls.size(); i++) {
				sql = listSqls.get(i);
				Object[] objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据
				objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据

				if (isToMysql) {
					System.out.print("--------------");
					for (int ii = 0; ii < objs.length; ii++) {
						if (objs[ii] != null)
							System.out.print(objs[ii].toString() + "	");
					}
					System.out.println();
					MysqlTransfer mt = new MysqlTransfer(sql, objs);
					mt.transfer();
					sql = mt.getMysqlSql();
					objs = mt.getParams();
					GlobalValue.myLog.info(mt);
				}
				pstm = con.prepareStatement(sql);// 创建PreparedStatement

				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstm.setObject(j + 1, objs[j]);
				}
				ret += pstm.executeUpdate(); // 执行sql语句
				pstm.close();
			}
			con.commit();// 执行完成后，进行事务提交
			con.close();
		} catch (SQLException e) {
			ret = 0;
			try {
				con.rollback();// 出现异常后，事务回滚
			} catch (SQLException e1) {
				GlobalValue.myLog.error(e1.toString());
				e1.printStackTrace();
			}
			GlobalValue.myLog.error("异常sql==>" + sql + "\r\n异常参数集合"
					+ listParams.get(i) + "\r\n异常信息==>" + e);
		} finally {
			close(pstm);
			close(con);
		}
		return ret;
	}

	/**
	 *@description 返回批量处理sql执行记录数
	 *@param listSqls
	 *@param listParams
	 *@return
	 *@returnType int
	 */
	public static int executeNonQueryTransactionBatch(List<String> listSqls,
			List<List<?>> listParams) {
		int ret = 0;// 返回执行成功影响的条数
		Connection con = null;// 获取数据库连接
		PreparedStatement pstm = null;
		String sql = "";
		try {
			con = getCon();// 获取数据库连接
			con.setAutoCommit(false);// 设置自动提交为false
			for (int i = 0; i < listSqls.size(); i++) {
				sql = listSqls.get(i);

				Object[] objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据
				objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据

				if (isToMysql) {
					System.out.print("--------------");
					for (int ii = 0; ii < objs.length; ii++) {
						if (objs[ii] != null)
							System.out.print(objs[ii].toString() + "	");
					}
					System.out.println();
					MysqlTransfer mt = new MysqlTransfer(sql, objs);
					mt.transfer();
					sql = mt.getMysqlSql();
					objs = mt.getParams();
					GlobalValue.myLog.info(mt);
				}

				pstm = con.prepareStatement(sql);// 创建PreparedStatement
				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstm.setObject(j + 1, objs[j]);
				}

				pstm.addBatch();
				// 判断凑够20个，发送执行
				if (i % 2000 == 0) {
					ret += pstm.executeBatch().length;
					// pstm.executeBatch();// 执行sql语句
					// pstm.clearBatch();
					// System.out.println("批量处理数量---> "+i);

				}

			}
			ret += pstm.executeBatch().length;
			// pstm.executeBatch();// 执行剩余的sql语句
			pstm.close();
			con.commit();// 执行完成后，进行事务提交
			con.close();
			ret = 1;
		} catch (SQLException e) {
			ret = -1;
			try {
				con.rollback();// 出现异常后，事务回滚
			} catch (SQLException e1) {
				GlobalValue.myLog.error(e1.getMessage(), e1);
				e1.printStackTrace();
			}
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(pstm);
			close(con);
		}
		return ret;
	}

	public static int executeNonQueryTransactionBatch(List<String> listSqls,
			List<List<?>> listParams, String sql1, String sql2) {
		int ret = -1;// 返回执行成功影响的条数
		Connection con = null;// 获取数据库连接
		PreparedStatement pstm = null;
		// String sql = "";
		try {
			con = getCon();// 获取数据库连接
			con.setAutoCommit(false);// 设置自动提交为false
			// if (!"".equals(sql1)) {
			// pstm = con.prepareStatement(sql1);
			// pstm.addBatch();
			// pstm.executeBatch();// 执行删除语句
			// pstm.clearBatch();
			// }
			// pstm = con.prepareStatement(sql2);
			for (int i = 0; i < listSqls.size(); i++) {
				// sql = listSqls.get(i);
				// pstm = con.prepareStatement(sql);// 创建PreparedStatement
				Object[] objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据
				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstm.setObject(j + 1, objs[j]);
				}
				pstm.addBatch();// /////////////////////////////////////////////////////////////////////////////////////////////
				if (i % 30 == 0) {
					pstm.executeBatch();// 执行sql语句
					pstm.clearBatch();
					// System.out.println("批量产生中---> "+i);
				}
			}

			pstm.executeBatch();// 执行剩余的sql语句
			pstm.close();
			con.commit();// 执行完成后，进行事务提交
			con.close();
			ret = 1;
		} catch (SQLException e) {
			ret = -1;
			try {
				con.rollback();// 出现异常后，事务回滚
			} catch (SQLException e1) {
				GlobalValue.myLog.error(e1.getMessage(), e1);
			}
			GlobalValue.myLog.error("异常sql==>" + sql2 + " 异常信息==>" + e);
		} finally {
			close(pstm);
			close(con);
		}
		return ret;
	}

	public static int executeNonQueryTransactionBatch(List<String> listSqls,
			List<List<?>> listParams, String sql1, String sql2, String sql3,
			String sql4, String sql5) {
		int ret = -1;// 返回执行成功影响的条数
		Connection con = null;// 获取数据库连接
		PreparedStatement pstm = null;
		// String sql = "";
		try {
			con = getCon();// 获取数据库连接
			con.setAutoCommit(false);// 设置自动提交为false
			if (!"".equals(sql1)) {
				pstm = con.prepareStatement(sql1);
				pstm.addBatch();
				pstm.executeBatch();// 执行删除语句
				pstm.clearBatch();
			}
			pstm = con.prepareStatement(sql2);
			for (int i = 0; i < listSqls.size(); i++) {
				// sql = listSqls.get(i);
				// pstm = con.prepareStatement(sql);// 创建PreparedStatement
				Object[] objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据
				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstm.setObject(j + 1, objs[j]);
				}
				pstm.addBatch();
				if (i % 30 == 0) {
					pstm.executeBatch();// 执行sql语句/////////////////////////////////////////////////////////////////////
					pstm.clearBatch();
					// System.out.println("批量产生中---> "+i);
				}
			}
			pstm.executeBatch();// 执行剩余的sql语句
			if (!"".equals(sql3)) {
				pstm = con.prepareStatement(sql3);
				pstm.addBatch();
				pstm.executeBatch();// 执行单条可执行语句
				pstm.clearBatch();

			}
			if (!"".equals(sql4)) {
				pstm = con.prepareStatement(sql4);
				pstm.addBatch();
				pstm.executeBatch();// 执行单条可以执行语句
				pstm.clearBatch();
			}
			if (!"".equals(sql5)) {
				pstm = con.prepareStatement(sql5);
				pstm.addBatch();
				pstm.executeBatch();// 执行单条可以执行语句
				pstm.clearBatch();
			}

			pstm.close();
			con.commit();// 执行完成后，进行事务提交
			con.close();
			ret = 1;
		} catch (SQLException e) {
			ret = -1;
			try {
				con.rollback();// 出现异常后，事务回滚
			} catch (SQLException e1) {
				GlobalValue.myLog.error(e1.getMessage(), e1);
			}
			GlobalValue.myLog.error("异常sql==>" + sql2 + " 异常信息==>" + e);
		} finally {
			close(pstm);
			close(con);
		}
		return ret;
	}

	/**
	 *@description 返回批量处理sql执行记录数
	 *@param listSqls
	 *@param listParams
	 *@return
	 *@returnType int
	 */
	public static int executeNonQueryBatchTransaction(String sql,
			List<List<?>> listParams) {
		int ret = 0;// 返回执行成功影响的条数
		Connection con = null;// 获取数据库连接
		PreparedStatement pstm = null;

		if (isToMysql) {
			System.out
					.println("-----executeNonQueryBatchTransaction--(String sql,List<List<?>> listParams)-------");

			MysqlTransfer mt = new MysqlTransfer(sql, null);
			mt.transfer();
			sql = mt.getMysqlSql();
			GlobalValue.myLog.info(mt);
		}

		try {
			con = getCon();// 获取数据库连接
			con.setAutoCommit(false);// 设置自动提交为false
			pstm = con.prepareStatement(sql);
			for (int i = 0; i < listParams.size(); i++) {
				Object[] objs = listParams.get(i).toArray();// 获取第i个sql语句对应的参数数据
				for (int j = 0; j < objs.length; j++) {// 将参数存入对应的pstm
					pstm.setObject(j + 1, objs[j]);
				}
				pstm.addBatch();
				// 判断凑够20个，发送执行
				if (i % 10000 == 0) {
					System.out.println("批量处理数量---> " + i);
					ret += pstm.executeBatch().length;
				}
			}
			ret += pstm.executeBatch().length;// 执行sql语句
			pstm.close();
			con.commit();// 执行完成后，进行事务提交
			con.close();
		} catch (SQLException e) {
			ret = 0;
			try {
				con.rollback();// 出现异常后，事务回滚
			} catch (SQLException e1) {
				GlobalValue.myLog.error(e1.getMessage(), e1);
			}
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
		} finally {
			close(pstm);
			close(con);
		}
		return ret;
	}

	/**
	 *@description 返回事务处理sql执行记录数
	 *@param listSqls
	 *@param listParams
	 *@return
	 *@returnType int
	 */
	public static Result executeQueryTransaction(List<String> listSql,
			List<List<?>> listListparam) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Result result = null;
		String sql = "";
		try {
			con = getCon();
			// 加入事务处理
			con.setAutoCommit(false);// 设置不能默认提交
			for (int i = 0; i < listSql.size(); i++) {
				sql = listSql.get(i);
				Object[] objs = listListparam.get(i).toArray();// 获取第i个sql语句对应的参数数据

				if (isToMysql) {
					System.out.print("--------------");
					for (int ii = 0; ii < objs.length; ii++) {
						if (objs[ii] != null)
							System.out.print(objs[ii].toString() + "	");
					}
					System.out.println();
					MysqlTransfer mt = new MysqlTransfer(sql, objs);
					mt.transfer();
					sql = mt.getMysqlSql();
					objs = mt.getParams();
					GlobalValue.myLog.info(mt);
				}

				pstmt = con.prepareStatement(sql);// 创建PreparedStatement
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
				GlobalValue.myLog.error(e1.getMessage(), e1);
			}
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
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
			con = getCon();
			// 加入事务处理
			con.setAutoCommit(false);// 设置不能默认提交
			for (int i = 0; i < listSql.size(); i++) {
				sql = listSql.get(i);

				Object[] objs = listListparam.get(i).toArray();// 获取第i个sql语句对应的参数数据

				if (isToMysql) {
					System.out.print("--------------");
					for (int ii = 0; ii < objs.length; ii++) {
						if (objs[ii] != null)
							System.out.print(objs[ii].toString() + "	");
					}
					System.out.println();
					MysqlTransfer mt = new MysqlTransfer(sql, objs);
					mt.transfer();
					sql = mt.getMysqlSql();
					objs = mt.getParams();
					GlobalValue.myLog.info(mt);
				}

				pstmt = con.prepareStatement(sql);// 创建PreparedStatement
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
				GlobalValue.myLog.error(e1.getMessage(), e1);
			}
			GlobalValue.myLog.error("异常sql==>" + sql + " 异常信息==>" + e);
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

		if (isToMysql) {
			System.out.println("--------proc---!!!!!!!!------");
			// sql = Trans.transform(sql);////ghj
			GlobalValue.myLog.info("--------proc---!!!!!!!!------" + sql);
		}

		try {
			conn = getCon();
			proc = conn.prepareCall(sql);
			for (int i = 0; i < lstPara.size(); i++) {
				proc.setObject(i + 1, lstPara.get(i));
			}
			proc.execute();
		} catch (Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
			return;
		} finally {
			close(conn);
		}
	}

	public static JSONObject executeProcWithreturn(String sql, List<?> lstPara) {
		Connection con = null;
		CallableStatement proc = null;
		int len = lstPara.size();

		if (isToMysql) {
			System.out.println("--------proc---!!!!!!!!------");
			// sql = Trans.transform(sql);////ghj
			GlobalValue.myLog.info("--------proc---!!!!!!!!------" + sql);
		}

		try {
			con = getCon();
			proc = con.prepareCall(sql);
			for (int i = 0; i < len; i++) {
				proc.setObject(i + 1, lstPara.get(i));
			}
			proc.registerOutParameter(len + 1, java.sql.Types.JAVA_OBJECT);
			proc.execute();
			JSONObject ret = (JSONObject) proc.getObject(len + 1);
			return ret;
		} catch (Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
			return null;
		} finally {
			close(con);
		}
	}

	public static JSONObject executeFun(String sql, List<?> lstPara) {
		Connection con = null;
		CallableStatement proc = null;
		int len = lstPara.size();

		if (isToMysql) {
			System.out.println("--------fun c---!!!!!!!!------");
			// sql = Trans.transform(sql);////ghj
			GlobalValue.myLog.info("-------func---!!!!!!!!------" + sql);
		}

		try {
			con = getCon();
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
			GlobalValue.myLog.error(e.getMessage(), e);
			return null;
		} finally {
			close(con);
		}
	}

	public static String executeFunClob(String sql, List<?> lstPara) {
		Connection con = null;
		CallableStatement proc = null;
		int len = lstPara.size();

		if (isToMysql) {
			System.out.println("--------func---!!!!!!!!------");
			// sql = Trans.transform(sql);////ghj
			GlobalValue.myLog.info("--------func---!!!!!!!!------" + sql);
		}

		try {
			con = getCon();
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
			GlobalValue.myLog.error(e.getMessage(), e);
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
			con = getCon();
			con.setAutoCommit(false);
			for (int i = 0; i < sqls.size(); i++) {
				String sql = sqls.get(i);

				Object[] objss = objs.get(i).toArray();// 获取第i个sql语句对应的参数数据

				if (isToMysql) {
					System.out.print("--------List<List<Object[]>> objs------");
					// for (int ii = 0; ii < objss.length; ii++) {
					// System.out.print(objss[ii].toString()+"	");
					// }
					// System.out.println();
					MysqlTransfer mt = new MysqlTransfer(sql, null);
					mt.transfer();
					sql = mt.getMysqlSql();
					objss = mt.getParams();
					GlobalValue.myLog.info(mt);
				}

				pstmts[i] = con.prepareStatement(sql);
				for (int j = 0; j < objs.get(0).get(i).length; j++) {
					pstmts[i].setObject(j + 1, objs.get(0).get(i)[j]);
				}
				pstmts[i].executeUpdate();
			}
			con.commit();
			con.setAutoCommit(true);
			return 1;
		} catch (SQLException e) {
			GlobalValue.myLog.error("execute执行事务处理时发生错误：" + e.getMessage());
			con.rollback();
			return -1;
		} finally {
			for (int i = 0; i < sqls.size(); i++) {
				close(pstmts[i]);
			}
			close(con);
		}
	}

	/*
	 * 执行一组SQL，返回执行是否成功
	 */
	public static Boolean ExecuteSQL(List<String> SqlStrings) {
		// 执行结果
		Boolean success = true;
		Connection con = null;
		Statement stmt = null;
		try {
			con = getCon();
			stmt = con.createStatement();
			// 执行每条sql
			for (String sql : SqlStrings) {

				if (isToMysql) {
					System.out.println("-----------------");
					MysqlTransfer mt = new MysqlTransfer(sql, null);
					mt.transfer();
					sql = mt.getMysqlSql();
					GlobalValue.myLog.info(mt);
				}

				stmt.executeUpdate(sql);
			}
		}
		// 某条语句出错，则回滚事务
		catch (Exception ex) {
			GlobalValue.myLog.error("====>>Error：" + ex.toString());
			success = false;
		} finally {
			close(stmt);
			close(con);
		}
		return success;
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
			GlobalValue.myLog.error(e.getMessage(), e);
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

	/**
	 *@description clob字段toString
	 *@param clob
	 *@return
	 *@returnType String
	 */
	public static String oracleClob2Str(Clob clob) {
		try {
			return (clob != null ? clob.getSubString((long) 1, (int) clob
					.length()) : null);
		} catch (SQLException e) {
			GlobalValue.myLog.error(e.getMessage(), e);
			return null;
		}
	}

	public static int executeNonQueryTransactionBatch(List<String> listSqls) {
		int ret = 0;// 返回执行成功影响的条数
		Connection con = null;// 获取数据库连接
		Statement stm = null;
		String sql = "";
		int hCount = 0;
		try {
			con = getCon();// 获取数据库连接
			con.setAutoCommit(false);// 设置自动提交为false
			stm = con.createStatement();
			for (int i = 0; i < listSqls.size(); i++) {
				sql = listSqls.get(i);
				if (com.knowology.dal.Database.isToMysql) {
					System.out.print("--------------");
					Object[] objs = {};
					for (int ii = 0; ii < objs.length; ii++) {
						if (objs[ii] != null)
							System.out.print(objs[ii].toString() + "	");
					}
					System.out.println();
					MysqlTransfer mt = new MysqlTransfer(sql, objs);
					mt.transfer();
					sql = mt.getMysqlSql();
					GlobalValue.myLog.info(mt);
				}
				stm.addBatch(sql);
				// GlobalValue.myLog.info("添加sql==>(" + (i + 1) + "): " + sql);
				if ((i + 1) % 100 == 0) {
					// GlobalValue.myLog.info("第" + (hCount + 1) + "次批量执行sql");
					ret += stm.executeBatch().length;
					hCount++;
					// GlobalValue.myLog.info("已执行成功" + ret + "条");
				}
			}
			// 执行剩余的sql语句
			ret += stm.executeBatch().length;
			stm.clearBatch();
			con.commit();// 执行完成后，进行事务提交
			con.setAutoCommit(true);// 在把自动提交打开
		} catch (SQLException e) {
			if (e instanceof BatchUpdateException) {
				BatchUpdateException bException = (BatchUpdateException) e;
				int[] s = bException.getUpdateCounts();
				GlobalValue.myLog.error("异常信息==>语句: "
						+ listSqls.get(hCount * 100 + s.length) + " 执行失败" + e);
			}
			e.printStackTrace();

			try {
				con.rollback();// 出现异常后，事务回滚
			} catch (SQLException e1) {
				GlobalValue.myLog.error(e1);
				e1.printStackTrace();
			}
		} finally {
			close(stm);
			close(con);
		}
		return ret;
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws SQLException {
		String str = "select name AS NAME from worker";
		// str="SELECT * FROM(SELECT GROUP_CONCAT('service_','', CAST(serviceID AS CHAR)) AS resourcesID,service AS resourceName FROM Service  WHERE serviceID = '1827280') a LEFT JOIN (SELECT ID,resourceID,Attr1_varchar FROM ResourceAcessManager WHERE resourceID LIKE 'service%') b ON a.resourcesID = b.resourceID LIMIT 0, 10 ";
		// // str
		// ="SELECT * FROM(SELECT GROUP_CONCAT('service_','', service) AS resourcesID,service AS resourceName FROM Service  WHERE serviceID = '1827280') a LEFT JOIN (SELECT ID,resourceID,Attr1_varchar FROM ResourceAcessManager WHERE resourceID LIKE 'service%') b ON a.resourcesID = b.resourceID LIMIT 0, 10 ";

		// String str ="CALL getServiceParent('华夏基金')";
		// str
		// ="SELECT getServiceChildrenIdList('粗话脏话','华夏基金,个性化业务',null) as Name";

		str = "select group_concat(parentid) from service where  serviceid=1827590.0";
		Result res = executeQuery(str);

		String strr = JSONObject.fromObject(res).toString();
		System.out.println(strr);

		SortedMap[] st = res.getRows();
		for (int i = 0; i < st.length; i++) {
			SortedMap stt = st[i];
			Object obj = stt.get("resourcesID");

			System.out.println(obj);

		}
		System.out.print(res.getRowCount());
	}

}