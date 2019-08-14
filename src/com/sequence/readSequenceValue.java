package com.sequence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import com.knowology.GlobalValue;

/**
 * 读取oracle库中的所有序号信息，供插入mysql数据库中
 * @author ghj
 *
 */
public class readSequenceValue {
	
	
	public static void read(){
		try {
			// GlobalValue.myLog.info("get connection by one!");
//			String driver = getCommmonLibJDBCValues("driverClassName"); // 数据库驱动
//			String url = getCommmonLibJDBCValues("url");// 连接地址
//			String user = getCommmonLibJDBCValues("username");// 用户名
//			String password = getCommmonLibJDBCValues("password");// 密码
			String driver = "oracle.jdbc.driver.OracleDriver";
			String url = "jdbc:oracle:thin:@222.186.101.210:1521:shhtele";
			String username = "kms";
			String password = "nkipwd";
			Connection con;
			Class.forName(driver); // 加载数据库驱动
			con = DriverManager.getConnection(url, username, password);
			Statement st = con.createStatement();
			String sql = "select SEQUENCE_NAME from dba_sequences where sequence_owner='KMS' ";
			ResultSet rs = st.executeQuery(sql);
			ArrayList<String> al = new ArrayList<String>();
			while(rs.next()){//取出所有的序列名
				String seq_name = rs.getString(1);
				al.add(seq_name);
			}
//			rs.close();st.close();
			System.out.println(al.toString());
			
			
			
			String sql2 = "select seq.nextVal from dual";
			for(String namel : al){
				String sql22 = sql2.replace("seq", namel);
				rs = st.executeQuery(sql22);
				rs.next();
				String sqlout = "insert into ghj_sequence(name,current_value,increment_value) values (\""+namel+"\","+rs.getString(1)+",1); ";
				System.out.println(sqlout);
				
			}
			rs.close();
			st.close();
			con.close();
			
			
		} catch (Exception e) {
			GlobalValue.myLog.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new readSequenceValue().read();
	}

}
