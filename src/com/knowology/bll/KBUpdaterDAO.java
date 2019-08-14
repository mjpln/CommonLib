package com.knowology.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.knowology.ConsoleLog;
import com.knowology.UtilityOperate.DateTimeOper;
import com.knowology.dal.Database;

public class KBUpdaterDAO {
	/**
	 * 方法名称： RebuildIncTables
	 * 内容摘要：在全量更新知识库之前对所有增量表进行重构
	 * @return
	 */
	public static Boolean RebuildIncTables(){
		List<String> sqls = new ArrayList<String>();
        sqls.add("alter table ServiceInc move");
		sqls.add("alter table KBDataInc move");
		sqls.add("alter table WordInc move");
		sqls.add("alter table wordpatinc move");
        return Database.ExecuteSQL(sqls);
	}
	
	/**
	 * 方法名称： CreateInitialKB
	 * 内容摘要：执行一次全量更新将数据库的知识全部加载到NLP内存知识库中
	 */
	public static void CreateInitialKB(){
		// 保持和源代码相同
		String[] sqls = new String[] { "delete from wordinc", 
                "delete from wordpatinc", 
                "delete from serviceinc", 
                "delete from kbdatainc"};
		for (int i = 0; i < sqls.length; ++i){
			ConsoleLog.ConsoleDebug(">>[%s] %s begin", DateTimeOper.getDateTimeByFormat(), sqls[i]);
			try {
				Database.executeNonQuery(sqls[i]);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		// 事务处理<建议采取>
		/*List<String> lstSQL = new ArrayList<String>();
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		lstSQL.add("delete from wordinc");
		lstSQL.add("delete from wordpatinc");
		lstSQL.add("delete from serviceinc");
		lstSQL.add("delete from kbdatainc");
		Database.executeNonQueryTransaction(lstSQL, lstLstpara);*/
	}
}
