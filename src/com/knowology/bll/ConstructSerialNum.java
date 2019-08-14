package com.knowology.bll;



import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.dal.Database;


/**
 * 操作的数据库表的主键ID
 * @author xsheng
 */
public class ConstructSerialNum {
	/**
	 * 根据表名获取主键ID
	 * @param tableName 表名
	 */
	public static int getSerialID(String tableName) {
		// 返回值
		Integer serialID = null;
		serialID = getSerialIDByTableName(tableName);
		return serialID;
	}
	
	/**
	 * 根据表名、主键名获得序列ID
	 * @param tableName 表名
	 * @param primaryKey 主键字段名
	 * @return
	 */
	public static int getSerialID(String tableName,String primaryKey) {
		// 全部转换为小写
		tableName = tableName.toLowerCase();
		primaryKey = primaryKey.toLowerCase();
		// 返回值
		Integer serialID = null;
		Result rs = null;
		String sql = "";
	
			// 查询该表的记录是否已经在序列表中
			sql = "select * from sequence where name='" + tableName + "'";
			rs = Database.executeQuery(sql);
			if (rs == null || rs.getRowCount()<1) {// 该表还未使用过序列
				serialID = createFirstSerialID(tableName,primaryKey);
			} else {
				serialID = getSerialIDByTableName(tableName);
			}
		
		return serialID;
	}
	
	/**
	 * 根据表名、主键名获得序列ID
	 * @param tableName 表名
	 * @param primaryKey 主键字段名
	 * @return
	 */
	public static String getSerialIDNew(String tableName,String primaryKey,String bussinessFlag) {
		// 全部转换为小写
		tableName = tableName.toLowerCase();
		primaryKey = primaryKey.toLowerCase();
		// 返回值
		String serialID = null;
		Result rs = null;
		String sql = "";
	
			// 查询该表的记录是否已经在序列表中
			sql = "select * from sequence where name='" + tableName + "'";
			rs = Database.executeQuery(sql);
			if (rs == null || rs.getRowCount()<1) {// 该表还未使用过序列
				System.out.println("rs == null");
				serialID = createFirstSerialID(tableName,primaryKey,bussinessFlag);
			} else {
				System.out.println("rs != null");
				serialID = getSerialIDByTableNameNew(tableName,bussinessFlag);
			}
		
		return serialID;
	}
	
	/**
	 * 表第一次产生序列值
	 * @param tableName 表名
	 * @param primaryKey 主键字段名
	 * @return
	 */
	private static int createFirstSerialID(String tableName, String primaryKey) {
		// 返回值
		Integer serialID = null;
		Result rs = null;
		String sql = "";
		try {
			// 查询当前表最大的主键id
			sql = "select max("+primaryKey+") as primaryKey from " + tableName;
			rs = Database.executeQuery(sql);
			if (rs == null || rs.getRowCount()<1) {
				serialID = 0;
			} else {
				serialID = Integer.parseInt(rs.getRows()[0].get("primaryKey")==null?"0":rs.getRows()[0].get("primaryKey").toString());
			}
			sql = "insert into sequence(name,current_value,increment) values (?,?,?)";
			// 默认递增为1
			int count = Database.executeNonQuery(sql,new Object[]{tableName,serialID,1});
			if (count > 0) {// 新增正确
				serialID = getSerialIDByTableName(tableName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serialID;
	}
	
	/**
	 * 表第一次产生序列值
	 * @param tableName 表名
	 * @param primaryKey 主键字段名
	 * @return
	 */
	private static String createFirstSerialID(String tableName, String primaryKey,String bussinessFlag) {
		// 返回值
		String serialID = null;
		Result rs = null;
		String sql = "";
		try {
			// 查询当前表最大的主键id
			sql = "select max("+primaryKey+") as primaryKey from " + tableName;
			rs = Database.executeQuery(sql);
			if (rs == null || rs.getRowCount()<1) {
				serialID = "0";
			} else {
				serialID = rs.getRows()[0].get("primaryKey")==null?"0":rs.getRows()[0].get("primaryKey").toString();
			}
			sql = "insert into sequence(name,current_value,increment) values (?,?,?)";
			if(!"".equals(bussinessFlag)){
				serialID = serialID.split("\\.")[0]+"."+ bussinessFlag;
			}else{
				serialID = serialID.split("\\.")[0];
			}
			// 默认递增为1
			int count = Database.executeNonQuery(sql,new Object[]{tableName,serialID,1});
			if (count > 0) {// 新增正确
				serialID = getSerialIDByTableNameNew(tableName,bussinessFlag);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serialID;
	}
	
	/**
	 * 根据序列表中已有的序列获取序列值
	 * @param tableName 表名
	 * @return
	 */
	private static String getSerialIDByTableNameNew(String tableName,String bussinessFlag) {
		String serialID = null;
		Result rs = null;
		String sql = "";
		try {
			sql = "select NEXTVAL('" + tableName + "') as serialID";
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount()>0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					 
					serialID = rs.getRows()[i].get("serialID")==null?"":rs.getRows()[i].get("serialID").toString();
				}
				if(!"".equals(bussinessFlag)){
					serialID = serialID.split("\\.")[0] +"."+ bussinessFlag;
				}else{
					serialID = serialID.split("\\.")[0];
				}
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("serialID="+serialID);
		return serialID;
	}
	
	/**
	 * 根据序列表中已有的序列获取序列值
	 * @param tableName 表名
	 * @return
	 */
	private static int getSerialIDByTableName(String tableName) {
		Integer serialID = null;
		Result rs = null;
		String sql = "";
		try {
			sql = "select NEXTVAL('" + tableName + "') as serialID";
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount()>0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					serialID = Integer.parseInt(rs.getRows()[i].get("serialID")==null?"":rs.getRows()[i].get("serialID").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serialID;
	}
	/**
	 * 获得当前表对应的最大序列值
	 * @param tableName 表名
	 * @return
	 */
	public static int getCurrentMaxSerialID(String tableName) {
		Integer serialID = null;
		Result rs = null;
		String sql = "";
		try {
			sql = "select currval ('" + tableName + "') as serialID from sequence where name='" + tableName + "'";
			rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount()>0) {
				for (int i = 0; i < rs.getRowCount(); i++) {
					serialID = Integer.parseInt(rs.getRows()[i].get("serialID")==null?"0":rs.getRows()[i].get("serialID").toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serialID;
	}
	
	/**
	 * 设置递增的
	 * @param tableName 表名
	 * @param value 递增数值
	 * @return
	 */
	public static int setIncrementValue(String tableName,int value) {
		int count = 0;
		String sql = "";
		try {
			sql = "update sequence set increment=? where name=?";
			count = Database.executeNonQuery(sql,new Object[]{value,tableName});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 *@description 查找  oracle 序列
	 *@param seqName
	 *@return 
	 *@returnType int 
	 */
	public static String GetOracleNextValNew(String seqName,String bussinessFlag) {
		String sql = null;
		sql = "select " + seqName + ".nextval  seq from dual";
		Result rs = null;
		rs = Database.executeQuery(sql);
		String id =rs.getRows()[0].get("seq").toString();
		id = id.split("\\.",-1)[0];
		if(!"".equals(bussinessFlag)){
			id = id+"."+ bussinessFlag;
		}
		return id;
	}
	
	/**
	 *@description 查找  oracle 序列
	 *@param seqName
	 *@return 
	 *@returnType int 
	 */
	public static int GetOracleNextVal(String seqName) {
		String sql = null;
		sql = "select " + seqName + ".nextval  seq from dual";
		Result rs = null;
		rs = Database.executeQuery(sql);
		if (rs == null || rs.getRows().length == 0) {
			return Integer.MAX_VALUE;
		}
		return (int)Math.round(Double.parseDouble(rs.getRows()[0].get("seq").toString()));
	}
}
