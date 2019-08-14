package com.sequence;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.knowology.dal.DESBase64;
import com.knowology.dal.Database;
import com.util.StringUtil;

/**
 * 首先，定义一张表sequence，每一行记录就可以作为一个序列，然后在字段上定义当前值、自增规则； 
 * 接着，定义一个next函数，用来获取下一个可用的自增主键
 * @author ghj
 *
 */
//private Logger log = Logger.getLogger(ActivityAction.class);

public class CreateSequenceFunc {
	
	// 秘钥
	public static final String TOKEN = "VFNSMzQ1Njc4OTAxMjM0NTY3ODkwVFNS";

//	public SequenceTableMaping stp = SequenceTableMaping.getInstance();
	/**
	 * 
	 */
	public void createSequenceTable(String table){
		String sql1 = "drop table if exists "+table;
		String sql2 = "create table "+table+
				"(name                 varchar(50) not null comment '序列的名字，唯一',"+
				"current_value        decimal(22,1) not null comment '当前的值',"+
				"increment_value      bigint(11) not null default 1 comment '步长，默认为1',"+
				"primary key (name))";
		Connection con = getCon();
		Statement st;
		try {
			st = con.createStatement();
			System.out.println(st.executeUpdate(sql1));
			System.out.println(st.executeUpdate(sql2));
			close(st);
			close(con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * @return currentVal
	 * @param seq_name序列名
	 */
	public void createFuncCurrentVal(){
		String func = "ghj_func_currval";
		String sql1 = "drop function if exists "+func;
//		String sql0 = "DELIMITER $";
//		String sql4 = "DELIMITER ;";
		String sql2 = "create function "+func+" ( seq_name varchar(50)) "+
				"RETURNS integer "+
				"begin " +
				"declare value integer; "+
				 "set value = 0; "+
				 "select current_value into value "+
				 "from ghj_sequence " +//ghj_sequence
				 "where name =  seq_name; "+
				" return value; " +
				"end ; ";
		System.out.println(sql2);

		Connection con = getCon();
		Statement st;
		try {
			st = con.createStatement();
			System.out.println(st.executeUpdate(sql1));
			close(st);
			CallableStatement cs = con.prepareCall(sql2);
			
			System.out.println(cs.execute());
			cs.close();
			close(con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param seq_name
	 * @param value
	 */
	public void createFuncSetVal(){
		String prod = "ghj_prod_setval";
		String sql1 = "drop PROCEDURE if exists "+prod;
		String sql2 = "create PROCEDURE "+prod+" (IN seq_name varchar(50) ,IN tab_name varchar(50),IN col_name varchar(50), IN step int, OUT now int) "+
				"begin  set now =0; " +
				"select max(col_name) into now from tab_name; "+
				 "if now then 		insert into ghj_sequence(name,current_value,increment_value) values(seq_name,now,1);"+
				 "else 		insert into ghj_sequence(name,current_value,increment_value) values(seq_name,0,1);  end if;" +
				"  select ghj_func_currval(seq_name) into now; " +
				"end ;";
		System.out.println(sql2);

		Connection con = getCon();
		Statement st;
		try {
			st = con.createStatement();
			System.out.println(st.executeUpdate(sql1));
			close(st);
			CallableStatement cs = con.prepareCall(sql2);
			
			System.out.println(cs.execute());
			cs.close();
			close(con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 *
	 * @param seq_name
	 * @return
	 */
	public void createFuncNextVal(){
		String func = "ghj_func_nextval";
		String sql1 = "drop function if exists "+func;
		String sql2 = " create function "+func+" ( seq_name varchar(50)) "+
				"RETURNS integer "+
				"contains sql "+
				"begin " +
				"update ghj_sequence "+
				 "set current_value = current_value + increment_value "+
				 "where name =  seq_name ; "+
				" return ghj_func_currval(seq_name ); " +
				"end  ;";
		System.out.println(sql2);
		Connection con = getCon();
		Statement st;
		try {
			st = con.createStatement();
			System.out.println(st.executeUpdate(sql1));
			close(st);
			CallableStatement cs = con.prepareCall(sql2);
			
			System.out.println(cs.execute());
			cs.close();
			close(con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * SELECT func_nextval('xxx');
	 *  String procedure = "{?=call test.get_next_value (?)}"; 
        
        CallableStatement cstmt = conn.prepareCall(procedure);
        cstmt.registerOutParameter(1, Types.INTEGER);
        cstmt.setString(2, "user_id");

        cstmt.execute();
        result = cstmt.getInt(1);

2.     String procedure = "select test.get_next_value (?)";         
        CallableStatement cstmt = conn.prepareCall(procedure);
        cstmt.setString(1, "user_id");
        cstmt.execute();
        ResultSet rs = cstmt.getResultSet();
        if (rs.next()) {
            result = rs.getInt(1);
        }
	 */
	public String getNextVal(String seq_name){
		
//		if(! stp.sequenceTableMap.keySet().contains(seq_name)){
//			System.out.println("seq_name序号未找到信息");
//			
//		}
//		else{
//			//zhixing
//		}
		
		String result = "";
		String procedure = "select ghj_func_nextval (?)"; 
		Connection conn = getCon();
        CallableStatement cstmt;
		try {
			cstmt = conn.prepareCall(procedure);
			cstmt.setString(1, seq_name);
	        cstmt.execute();
	        ResultSet rs = cstmt.getResultSet();
	        if (rs.next()) {
	            result = rs.getString(1);
	        }
	        rs.close();
	        cstmt.close();
	        conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return result;
	}
	
	
	public String setVal(String seq_name, String tab_name ,String col_name){
//		String result = "";
//		String procedure = "{call ghj_prod_setval (?,?,?,1,?)}";
//		Connection conn = getCon();
//        CallableStatement cstmt;
//		try {
//			cstmt = conn.prepareCall(procedure);
//	        cstmt.setString(1, seq_name);
//	        cstmt.setString(2, tab_name);
//	        cstmt.setString(3, col_name);
//	        cstmt.registerOutParameter(4, Types.INTEGER);
//	        cstmt.execute();
//	        result = String.valueOf(cstmt.getInt(4));
//	        cstmt.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		System.out.println("即将在序列表中新增信息seq_name:"+seq_name+"|tab_name"+tab_name+"|col_name"+col_name);
		String sql1 = "select max(col_name) r from tab_name ";
		sql1 = sql1.replace("col_name", col_name).replace("tab_name", tab_name);
		String sql2 = "insert into ghj_sequence(name,current_value,increment_value) values('seq_name',start,1)";
		sql2 = sql2.replace("seq_name", seq_name);
		Connection con = getCon();
		Statement st;
		String start = "";
		try {
			st = con.createStatement();
			ResultSet rs = st.executeQuery(sql1);
			if(rs.next()){
				 start = rs.getString(1);
				 sql2 = sql2.replace("start", start);
			}
			else{
				sql2 = sql2.replace("start", "0");
			}
			rs.close();
			System.out.println(sql2);
			System.out.println(st.executeUpdate(sql2));
			close(st);
			close(con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
		return start;
	}
	/*
	
	public void updateSequenceTableWithConfig(String seqTable){
		String sql = "select distinct name from seqTable";
		sql = sql.replace("seqTable", seqTable);
		HashSet<String> seqNameFromTab = new HashSet<String>();
		Connection con = getCon();
		Statement st;
		try {
			st = con.createStatement();
			ResultSet rs = st.executeQuery(sql);
			while(rs.next()){
				 seqNameFromTab.add(rs.getString(1));
			}
			rs.close();
			close(st);
			close(con);
			Set<String> ss = stp.sequenceTableMap.keySet();
			ss.removeAll(seqNameFromTab);
			for(String seq_name : ss){
				String tab_name = stp.sequenceTableMap.get(seq_name);
				String col_name = stp.tableSequenceColumnMap.get(tab_name).get(seq_name);
				setVal(seq_name, tab_name, col_name);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	*/
	
	public static Connection getCon(){
		Connection con = null;
		try {
//			driver = StringUtil.getConfigInfo("driverClassName");
//			url = StringUtil.getConfigInfo("url");		
//			String user = StringUtil.getConfigInfo("username");
//			String password = StringUtil.getConfigInfo("password");			
			String driver = Database.getCommmonLibJDBCValues("driverClassName"); // 数据库驱动
			String url = Database.getCommmonLibJDBCValues("url");// 连接地址
			String user = Database.getCommmonLibJDBCValues("username");// 用户名
			String password = Database.getCommmonLibJDBCValues("password");// 密码
			
			boolean isEncrypt = Database.getCommmonLibJDBCValues("isEncrypt") == null ? false
					: (Database.getCommmonLibJDBCValues("isEncrypt").equals("true") ? true : false);
			
			if (isEncrypt){
				url = DESBase64.decryptStringBase64(url, TOKEN);
				user = DESBase64.decryptStringBase64(user, TOKEN);
				password = DESBase64.decryptStringBase64(password, TOKEN);
			}
			
			Class.forName(driver); // 加载数据库驱动
			con = DriverManager.getConnection(url, user, password);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("连接失败");
//			logger.info("连接失败");
			con = null;
		}
		return con;
	}
	private static void close(ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception ex) {
			// 写异常日志
		}
	}

	private static void close(Statement stmt) {
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception ex) {
			// 写异常日志
		}
	}

	private static void close(Connection con) {
		try {
			if (con != null)
				con.close();
		} catch (Exception ex) {
			// 写异常日志
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String proFilePath = System.getProperty("user.dir") + "/"
//				+ "log4j.properties";
//		PropertyConfigurator.configure(proFilePath);
		
//		getCon();
//		new CreateSequenceFunc().createSequenceTable("ghj_sequence");
		CreateSequenceFunc cf = new CreateSequenceFunc();
//		cf.createFuncCurrentVal();
//		cf.createFuncNextVal();
//		cf.createFuncSetVal();
//		System.out.println(cf.setVal("test2_seq", "service", "serviceid"));
//		System.out.println(cf.getNextVal("test_seq"));
	}

}
