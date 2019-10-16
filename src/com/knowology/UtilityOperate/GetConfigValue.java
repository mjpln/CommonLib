package com.knowology.UtilityOperate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.bll.CommonLibMetafieldmappingDAO;

public class GetConfigValue {

	/** true:mysql连接 */
	public static boolean isMySQL = ("mysql"
			.equalsIgnoreCase(getDatabase("connectFrom")) ? true : false);
	/** true:oralce连接 */
	public static boolean isOracle = ("oracle"
			.equalsIgnoreCase(getDatabase("connectFrom")) ? true : false);

	public static boolean isToMysql = ("true"
			.equalsIgnoreCase(getDatabase("isToMysql")) ? true : false);

	public final static int NUMBER_OF_MAX_SCENE = 20;
	
	//是否启用存储过程
	public static boolean enableDBFun = false;
	
	static {
		init();
	}

	/**
	 * 读取全局配置文件信息
	 * 
	 * @param key
	 *            要读取的属性的名字
	 * @return 对应的value值
	 */
	public static String getDatabase(String key) {

		String result = "";

//		读取固定路径配置文件
//		 Properties prop = new Properties();
//		 try {
//		 prop.load(new
//		 FileInputStream("/cpic/cpicapp/conf/commonLibGlobal.properties"));
//		 result = prop.getProperty(key);
//		 } catch (FileNotFoundException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 } catch (IOException e) {
//		 // TODO Auto-generated catch block
//		 e.printStackTrace();
//		 }
//					
//		 return result;

		ResourceBundle resourcesTable = ResourceBundle
				.getBundle("commonLibGlobal");
		result = resourcesTable.getString(key);
		return result;
	}

	/**
	 * 日志信息的存储SQL
	 * 
	 * @return
	 */
	public static String LogSql() {
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "insert into operationlog(ip,workerid,workername,brand,service,operation,city,object,tablename,time) values(?,?,?,?,?,?,?,?,?,systimestamp)";
		} else if (GetConfigValue.isMySQL) {
			sql = "insert into operationlog(ip,workerid,workername,brand,service,operation,city,object,tablename,time) values(?,?,?,?,?,?,?,?,?,sysdate())";

		}
		return sql;
	}

	/**
	 * 日志信息的参数列表
	 * 
	 * @param brand品牌
	 * @param service业务
	 * @param operation数据操作类型
	 * @param city城市
	 * @param _object操作数据对象
	 * @param table对应操作表
	 * @return 数据参数集合
	 */
	public static List<String> LogParam(String userip, String userid,
			String username, String brand, String service, String operation,
			String _object, String table) {

		List<String> lstpara = new ArrayList<String>();
		lstpara.add(userip);
		lstpara.add(userid);
		lstpara.add(username);
		lstpara.add(brand);
		lstpara.add(service);
		lstpara.add(operation);
		lstpara.add(" ");

		lstpara.add(_object);
		lstpara.add(table);
		return lstpara;
	}

	public static void init(){
		Result result = CommonLibMetafieldmappingDAO.getConfigValue("语义存储过程启用配置", "默认");
		if(result != null && result.getRowCount() > 0){
			String s = Objects.toString(result.getRows()[0].get("name"),"关闭");
			if("开启".equals(s)){
				enableDBFun = true;
			}
		}
	}
}
