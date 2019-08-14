package com.treeSelect;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONObject;
import com.sequence.CreateSequenceFunc;

public class Tree {
	
	
//create function getChildList (rootId VARCHAR(50),id varchar(50),pid VARCHAR(50),tb VARCHAR(50)) RETURNS VARCHAR(3000) BEGIN DECLARE stemp VARCHAR(3000);DECLARE stempChd VARCHAR(1000);set stemp = '$';set stempChd = rootId;WHILE stempChd is not null DO set stemp = concat(stemp, ',',stempChd);SELECT GROUP_CONCAT(id) into stempChd from tb where FIND_IN_SET(pid,stempChd)>0;END WHILE;RETURN stemp;END;
	public void createFuncGetChildList(){
//		String func = "ghj_func_currval";
//		String sql1 = "drop function if exists "+func;
		String sql2 = "create Procedure getChildList "+
				"	( IN rootId VARCHAR(500),IN id varchar(50),IN pid VARCHAR(50),IN tb VARCHAR(50),OUT outCome VARCHAR(3000))"+
			" BEGIN"+
			" DECLARE stemp VARCHAR(3000);DECLARE stempChd VARCHAR(1000);"+
			"set stemp = '$';set stempChd = rootId;set @v_stempChd = stempChd;SET session group_concat_max_len =4294967295;"+
			"WHILE stempChd is not null DO set stemp = concat(stemp, ',',stempChd);"+
			"SET @sql1=CONCAT('SELECT GROUP_CONCAT(',id,') into @v_stempChd from ',tb, ' where  FIND_IN_SET(',pid,',@v_stempChd)>0');"+
			"PREPARE  stmt FROM @sql1;EXECUTE stmt;DEALLOCATE PREPARE stmt;"+
			"set stempChd = @v_stempChd;"+
			" END WHILE;"+
			" set outCome = stemp;"+
			"END;";
		System.out.println(sql2);

		Connection con = CreateSequenceFunc.getCon();
		Statement st;
		try {
			st = con.createStatement();
//			System.out.println(st.executeUpdate(sql1));
//			close(st);
			CallableStatement cs = con.prepareCall(sql2);
			
			System.out.println(cs.execute());
			cs.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 创建一个function getChildLst, 得到一个由所有子节点号组成的字符串.
	 * @param rootid
	 * @param idName
	 * @param parentidName
	 * @param table
	 * @return
	 */
	public static String getList(String rootid,String idName,String parentidName,String table){
		String result = "";
		try {
			Connection connection = CreateSequenceFunc.getCon();
			String sql = "{CALL getChildList(?,?,?,?,?)}"; //调用存储过程 
			CallableStatement cstm = connection.prepareCall(sql); //实例化对象cstm 
			cstm.setString(1, rootid); //存储过程输入参数 
			cstm.setString(2, idName); //存储过程输入参数 
			cstm.setString(3, parentidName); //存储过程输入参数 
			cstm.setString(4, table); //存储过程输入参数 
			//cstm.setInt(2, 2); // 存储过程输入参数 
			cstm.registerOutParameter(5, Types.VARCHAR); // 设置返回值类型 即返回值 
			cstm.execute(); // 执行存储过程 
			System.out.println(cstm.getString(5)); 
			result = cstm.getString(5);
			cstm.close(); 
			connection.close(); 
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	
	public static String TokeepsameId(String sql, String keyid, String way){
		String result = "";
		try {
			Connection connection = CreateSequenceFunc.getCon();
			Statement st = connection.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if(rs.next()){
//				 Object o = rs.getObject(1);
//				 result = o.toString();
				 result = rs.getString(1);
			}
			rs.close();
			st.close();
			connection.close(); 
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		if(!"".equals(result)){
//			result = keyid +" "+way+" '"+result+"' ";
//		}
		return result;
	}
	
	/**
	 * 创建一个function tree_sp_list, 得到一个由所有子节点号组成的字符串.
	 * @param rootid
	 * @param idName
	 * @param parentidName
	 * @param table
	 * @return
	 */
	public static void get_connect_treeList(String rootid){
		try {
			Connection connection = CreateSequenceFunc.getCon();
			String sql = "{CALL tree_connect(?)}"; //调用存储过程 
			CallableStatement cstm = connection.prepareCall(sql); //实例化对象cstm 
			cstm.setString(1, rootid); //存储过程输入参数 
			//cstm.setInt(2, 2); // 存储过程输入参数 
//			cstm.registerOutParameter(5, Types.VARCHAR); // 设置返回值类型 即返回值 
			cstm.execute(); // 执行存储过程 
			cstm.close(); 
			connection.close(); 
		}catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		new Tree().getList("0","menuid","parentid","t_menuhx");
//		$,0,15,1,7,9,3,4,5,6,8,2,11,64,53,54,69,67,90,50,51,40,65,63,41,60,61,62,102,101,68,110,112,56,59
//		$,0,15,1,7,9,3,4,5,6,8,2,11,64,53,54,69,67,90,50,51,40,65,63,41,60,61,62,102,101,68,110,112,56,59
//		new Tree().getList("'个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库'",  "serviceid","parentid", "service");
//		new Tree().getList("'个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库'",  "parentid","serviceid", "service");
		
		while(true){
		int randomIntegrationNumber = 0;

//		String s = "{\"50\":\"0.01\",\"30\":\"0.04\",\"20\":\"0.1\",\"10\":\"0.2\",\"5\":\"1\"}";
		String mwSettings = "{\"0.01\":\"50\",\"0.04\":\"30\",\"0.1\":\"20\",\"0.6\":\"10\",\"1\":\"5\"}";
		System.out.println(mwSettings);
//		String s1 = "{"0.01":"50","0.04":"30","0.1":"20","0.2":"10","5":"1"}";

		JSONObject hsSettingsObject = JSONObject.parseObject(mwSettings);
//		System.out.println(hsSettingsObject.getString("5"));
		Double prob = Math.random();//随机概率
		
		String regex = "\"([\\d\\.]+)\":";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(mwSettings);
		while(m.find()){
			String JiangpingGailv = m.group(1);
			System.out.println(JiangpingGailv);
			Double max = Double.parseDouble(JiangpingGailv);
			if(prob <= max){
				randomIntegrationNumber = Integer.parseInt(hsSettingsObject.getString(JiangpingGailv));
				System.out.println("用户概率是"+prob+"分值概率是"+JiangpingGailv+"中奖金额"+randomIntegrationNumber);
				break;
			}
		}
		}
		
	}

}
