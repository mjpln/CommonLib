package com.knowology.bll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

/**
 * @author ll
 *
 */
public class CommonLibWordDAO {

	/**
	 * 词条添加操作
	 * 
	 * @param curwordclassid参数词类id
	 * @param curwordclass参数词类名称
	 * @param worditemList参数词条集合
	 * @param curwordclasstype
	 *            词类归属
	 * @param type参数词条类型
	 * @param container
	 *            词库类型
	 * @return 保存返回的结果
	 */
	public static int insert(User user, String curwordclassid,
			String curwordclass, String curwordclasstype,
			List<String> worditemList, String type, String container) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义保存词条的SQL语句
		String sql = "";
		// 定义绑定参数集合 
		List<Object> lstpara = new ArrayList<Object>();
		String service = "";
		String abs = "";
		String serviceType = "";
		String kbdataid = "";
		serviceType = user.getIndustryOrganizationApplication();
		if ("子句".equals(container) && user!=null) {// 如果添加的是子句条，分别根据四层结构分别处理业务 摘要 摘要ID
			
			if ("当前行业".equals(curwordclasstype)) {
				service = serviceType.split("->")[0] + "主题";
				abs = "<" + service + ">" + curwordclass.replace("子句", "");
				Result rs = CommonLibKbDataDAO.getKbdataInfoByAbstractName(abs);
				if (rs != null && rs.getRowCount() > 0) {
					kbdataid = rs.getRows()[0].get("kbdataid").toString();
				}
			}
			if ("通用行业".equals(curwordclasstype)) {
				service = curwordclasstype + "主题";
				abs = "<" + service + ">" + curwordclass.replace("子句", "");
				Result rs = CommonLibKbDataDAO.getKbdataInfoByAbstractName(abs);
				if (rs != null && rs.getRowCount() > 0) {
					kbdataid = rs.getRows()[0].get("kbdataid").toString();
				}
			}
		}

		// 定义词条遍历
		String worditem = "";
		//获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		// 循环遍历词条集合
		for (int i = 0; i < worditemList.size(); i++) {
			// 获取词条表的序列值
			String wordid = "";
			if (GetConfigValue.isOracle) {
				wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id",bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				wordid = ConstructSerialNum.getSerialIDNew("word", "wordid",bussinessFlag);
			}
		
			worditem = worditemList.get(i).split("@")[0];
			// 定义保存词条的SQL语句
			sql = "insert into word(wordid,wordclassid,word,type) values(?,?,?,?) ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定id参数
			lstpara.add(wordid);
			// 绑定词类id参数
			lstpara.add(curwordclassid);
			// 绑定词类名称
			lstpara.add(worditem);
			// 绑定类型参数
			lstpara.add(type);
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			
			if (!"".equals(service) && !"".equals(kbdataid)) {// 添加行业主题树摘要下词模
				String wordpat = worditem + "@2#编者=\"来源子句库\"";
				// 获取词模表的序列值
				int wordpatid = 0;
				if (GetConfigValue.isOracle) {
					wordpatid =  (ConstructSerialNum
							.GetOracleNextVal("SEQ_WORDPATTERN_ID"));
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate)";
				} else if (GetConfigValue.isMySQL) {
					wordpatid = ConstructSerialNum.getSerialID("wordpat",
							"wordpatid");
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate())";
				}
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定模板id参数
				lstpara.add(wordpatid);
				// 绑定模板参数
				lstpara.add(wordpat);
				// 绑定地市参数
				lstpara.add("上海");
				// 绑定自动开关参数
				lstpara.add(0);
				// 绑定模板类型参数
				lstpara.add(CommonLibWordclassDAO.getWordpatType(wordpat));
				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定品牌参数
				lstpara.add(service);
				// 将SQL语句放入集合中
				lstsql.add(sql.toString());
				// 将对应的绑定参数集合放入集合中
				lstlstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 定义新增模板备份表
				sql = "insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0)";
				// 绑定模板id参数
				lstpara.add(wordpatid);
				// 绑定地市参数
				lstpara.add("上海");
				// 绑定品牌参数
				lstpara.add(service);
				// 将SQL语句放入集合中
				lstsql.add(sql.toString());
				// 将对应的绑定参数集合放入集合中
				lstlstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
				
			}

			if (user != null) {
				// 生成操作日志记录
				// 将SQL语句放入集合中
				lstsql.add(GetConfigValue.LogSql());
				// 将定义的绑定参数集合放入集合中
				lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
						.getUserID(), user.getUserName(), " ", " ", "增加词条",
						worditem, "WORD"));
			}
		}

		// 更新当前词类编辑时间
		if (GetConfigValue.isOracle) {
			sql = "update wordclass set time =sysdate  where  wordclassid = ?";
		} else if (GetConfigValue.isMySQL) {
			sql = "update wordclass set time =sysdate()  where  wordclassid = ?";
		}
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定词条参数
		lstpara.add(curwordclassid);
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
	 * 词条添加操作
	 * 
	 * @param curwordclassid参数词类id
	 * @param curwordclass参数词类名称
	 * @param worditemList参数词条集合
	 * @param curwordclasstype
	 *            词类归属
	 * @param type参数词条类型
	 * @param container
	 *            词库类型
	 * @return 保存返回的结果
	 */
	public static int insertByLoginInfo(User user, String curwordclassid,
			String curwordclass, String curwordclasstype,
			List<String> worditemList, String type, String container) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义保存词条的SQL语句
		String sql = "";
		// 定义绑定参数集合 
		List<Object> lstpara = new ArrayList<Object>();
		String service = "";
		String abs = "";
		String serviceType = "";
		String kbdataid = "";
		serviceType = user.getCustomer();
		if ("子句".equals(container) && user!=null) {// 如果添加的是子句条，分别根据四层结构分别处理业务 摘要 摘要ID
			
			if ("当前行业".equals(curwordclasstype)) {
				service = serviceType.split("->")[0] + "主题";
				abs = "<" + service + ">" + curwordclass.replace("子句", "");
				Result rs = CommonLibKbDataDAO.getKbdataInfoByAbstractName(abs);
				if (rs != null && rs.getRowCount() > 0) {
					kbdataid = rs.getRows()[0].get("kbdataid").toString();
				}
			}
			if ("通用行业".equals(curwordclasstype)) {
				service = curwordclasstype + "主题";
				abs = "<" + service + ">" + curwordclass.replace("子句", "");
				Result rs = CommonLibKbDataDAO.getKbdataInfoByAbstractName(abs);
				if (rs != null && rs.getRowCount() > 0) {
					kbdataid = rs.getRows()[0].get("kbdataid").toString();
				}
			}
		}

		// 定义词条遍历
		String worditem = "";
		//获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		
		// 地市<编码,中文>
		Map<String, String> LocalMap = new HashMap<String, String>();
		Result LocalRs = CommonLibQuestionUploadDao.createLocal();
		
		if (LocalRs != null && LocalRs.getRowCount() > 0) {
			for (int i = 0; i < LocalRs.getRowCount(); i++) {
				if (LocalMap.containsKey(LocalRs.getRows()[i].get("id")
						.toString().replace(" ", ""))) {
					// 判断长度
					if (LocalRs.getRows()[i].get("province").toString()
							.replace(" ", "").length() < LocalMap.get(
							LocalRs.getRows()[i].get("id").toString().replace(
									" ", "")).toString().replace(" ", "")
							.length()) {
						LocalMap.put(LocalRs.getRows()[i].get("id").toString()
								.replace(" ", ""), LocalRs.getRows()[i].get(
								"province").toString().replace(" ", ""));
					}
				} else {
					LocalMap.put(LocalRs.getRows()[i].get("id").toString()
							.replace(" ", ""), LocalRs.getRows()[i].get(
							"province").toString().replace(" ", ""));
				}
			}
			// 特殊处理
			LocalMap.put("433100", "自治州");
		}
		// 循环遍历词条集合
		for (int i = 0; i < worditemList.size(); i++) {
			// 获取词条表的序列值
			String wordid = "";
			if (GetConfigValue.isOracle) {
				wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id",bussinessFlag);
			} else if (GetConfigValue.isMySQL) {
				wordid = ConstructSerialNum.getSerialIDNew("word", "wordid",bussinessFlag);
			}
		
			List<String> cityList = new ArrayList<String>();
			HashMap<String, ArrayList<String>> resourseMap = CommonLibPermissionDAO
			.resourseAccess(user.getUserID(), "querymanage", "S");
			// 该操作类型用户能够操作的资源
			cityList = resourseMap.get("地市");
			String citys = "";
			String cityNames = "";
			for (String city : cityList){
				citys = citys + city + ",";
				cityNames = cityNames + LocalMap.get(city) + ",";
			}
			if (citys.contains(",")){
				citys = citys.substring(0, citys.lastIndexOf(","));
				cityNames = cityNames.substring(0, cityNames.lastIndexOf(","));
			} else {
				citys = null;
				cityNames = null;
			}
			
			worditem = worditemList.get(i).split("@")[0];
			// 定义保存词条的SQL语句
			sql = "insert into word(wordid,wordclassid,word,type,city,cityname) values(?,?,?,?,?,?) ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定id参数
			lstpara.add(wordid);
			// 绑定词类id参数
			lstpara.add(curwordclassid);
			// 绑定词类名称
			lstpara.add(worditem);
			// 绑定类型参数
			lstpara.add(type);
			lstpara.add(citys);
			lstpara.add(cityNames);
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);

			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			
			if (!"".equals(service) && !"".equals(kbdataid)) {// 添加行业主题树摘要下词模
				String wordpat = worditem + "@2#编者=\"来源子句库\"";
				// 获取词模表的序列值
				int wordpatid = 0;
				if (GetConfigValue.isOracle) {
					wordpatid =  (ConstructSerialNum
							.GetOracleNextVal("SEQ_WORDPATTERN_ID"));
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate)";
				} else if (GetConfigValue.isMySQL) {
					wordpatid = ConstructSerialNum.getSerialID("wordpat",
							"wordpatid");
					sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate())";
				}
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定模板id参数
				lstpara.add(wordpatid);
				// 绑定模板参数
				lstpara.add(wordpat);
				// 绑定地市参数
				lstpara.add("上海");
				// 绑定自动开关参数
				lstpara.add(0);
				// 绑定模板类型参数
				lstpara.add(CommonLibWordclassDAO.getWordpatType(wordpat));
				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定品牌参数
				lstpara.add(service);
				// 将SQL语句放入集合中
				lstsql.add(sql.toString());
				// 将对应的绑定参数集合放入集合中
				lstlstpara.add(lstpara);

				//文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
				
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 定义新增模板备份表
				sql = "insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0)";
				// 绑定模板id参数
				lstpara.add(wordpatid);
				// 绑定地市参数
				lstpara.add("上海");
				// 绑定品牌参数
				lstpara.add(service);
				// 将SQL语句放入集合中
				lstsql.add(sql.toString());
				// 将对应的绑定参数集合放入集合中
				lstlstpara.add(lstpara);
				
				//文件日志
				GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
				
			}

			if (user != null) {
				// 生成操作日志记录
				// 将SQL语句放入集合中
				lstsql.add(GetConfigValue.LogSql());
				// 将定义的绑定参数集合放入集合中
				lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
						.getUserID(), user.getUserName(), " ", " ", "增加词条",
						worditem, "WORD"));
			}
		}

		// 更新当前词类编辑时间
		if (GetConfigValue.isOracle) {
			sql = "update wordclass set time =sysdate  where  wordclassid = ?";
		} else if (GetConfigValue.isMySQL) {
			sql = "update wordclass set time =sysdate()  where  wordclassid = ?";
		}
		// 定义绑定参数集合
		lstpara = new ArrayList<Object>();
		// 绑定词条参数
		lstpara.add(curwordclassid);
		// 将SQL语句放入集合中
		//lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		//lstlstpara.add(lstpara);

		//文件日志
		//GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	/**
	 * 删除词条
	 * 
	 * @param user
	 *            用户信息
	 * @param wordid参数词条id
	 * @param curwordclass参数词类名称
	 * @param curwordclasstype
	 *            词类归属
	 * @param worditem参数词条名称
	 * @param container
	 *            词库类型
	 * @return 删除返回的json串
	 */
	public static int delete(User user, String wordid, String curwordclass,
			String curwordclasstype, String worditem, String container) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		String service = "";
		String abs = "";
		String serviceType = "";
		String kbdataid = "";
		if ("子句".equals(container)) {// 如果删除的是子句条，分别根据四层结构分别处理业务 摘要 摘要ID
			serviceType = user.getCustomer();
			if ("当前行业".equals(curwordclasstype)) {
				service = serviceType.split("->")[0] + "主题";
				abs = "<" + service + ">" + curwordclass.replace("子句", "");
				Result rs = CommonLibKbDataDAO.getKbdataInfoByAbstractName(abs);
				if (rs != null && rs.getRowCount() > 0) {
					kbdataid = rs.getRows()[0].get("kbdataid").toString();
				}
			}

			if ("通用行业".equals(curwordclasstype)) {
				service = curwordclasstype + "主题";
				abs = "<" + service + ">" + curwordclass.replace("子句", "");
				Result rs = CommonLibKbDataDAO.getKbdataInfoByAbstractName(abs);
				if (rs != null && rs.getRowCount() > 0) {
					kbdataid = rs.getRows()[0].get("kbdataid").toString();
				}
			}
		}

		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 删除词条的SQL语句
		sql = "delete from word where wordid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定词条id参数
		lstpara.add(wordid);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);

		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 删除别名的SQL语句
		sql = "delete from word  where stdwordid=?";
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定词条id参数
		lstpara.add(wordid);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);

		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 更新当前词类编辑时间
		if (GetConfigValue.isOracle) {
			sql = "update wordclass set time =sysdate  where  wordclass = ?";
		} else if (GetConfigValue.isMySQL) {
			sql = "update wordclass set time =sysdate()  where  wordclass = ?";
		}
		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定词条参数
		lstpara.add(curwordclass);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);

		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 删除与子句词模有关的词模
		// sql =
		// "delete from wordpat t where t.wordpatid in(select w.wordpatid from service s,kbdata k,wordpat w where s.serviceid=k.serviceid and k.kbdataid=w.kbdataid and s.brand=? and w.wordpat like ''||?||'%')";
//		sql = "delete from  wordpat where kbdataid =? and wordpat like ? ";
//		// 定义绑定参数集合
//		lstpara = new ArrayList<String>();
//		// 绑定摘要ID参数
//		lstpara.add(kbdataid);
//		// 绑定子句词模参数
//		lstpara.add(worditem + "@%");
//		// 将SQL语句放入集合中
//		lstsql.add(sql);
//		// 将对应的绑定参数集合放入集合中
//		lstlstpara.add(lstpara);
//
//		// 生成操作日志记录
//		// 将SQL语句放入集合中
		lstsql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", " ", "删除词条", worditem,
				"WORD"));

		// 执行SQL语句，绑定事务处理，返回事务处理的结果
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
	}

	/**
	 * 修改词条操作
	 * 
	 * @param oldworditem参数原有词条
	 * @param newworditem参数新的词条
	 * @param oldtype参数原有类型
	 * @param newtype参数新的类型
	 * @param wordid参数词条id
	 * @param curwordclass参数词类名
	 * @param curwordclasstype
	 *            参数词类归属
	 * @param container
	 *            参数词库类型
	 * @return 修改返回的结果
	 */
	public static int update(User user, String oldworditem, String newworditem,
			String oldtype, String newtype, String wordid, String wordclassid,
			String curwordclass, String curwordclasstype, String container) {
		// 定义SQL语句
		String sql = "";
		String service = "";
		String abs = "";
		String serviceType = "";
		String kbdataid = "";
		if ("子句".equals(container)) {// 如果添加的是子句条，分别根据四层结构分别处理业务 摘要 摘要ID
			serviceType = user.getCustomer();
			if ("当前行业".equals(curwordclasstype)) {
				service = serviceType.split("->")[0] + "主题";
				abs = "<" + service + ">" + curwordclass.replace("子句", "");
				Result rs = CommonLibKbDataDAO.getKbdataInfoByAbstractName(abs);
				if (rs != null && rs.getRowCount() > 0) {
					kbdataid = rs.getRows()[0].get("kbdataid").toString();
				}
			}
			if ("通用行业".equals(curwordclasstype)) {
				service = curwordclasstype + "主题";
				abs = "<" + service + ">" + curwordclass.replace("子句", "");
				Result rs = CommonLibKbDataDAO.getKbdataInfoByAbstractName(abs);
				if (rs != null && rs.getRowCount() > 0) {
					kbdataid = rs.getRows()[0].get("kbdataid").toString();
				}
			}
		}
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 更新词条的SQL语句
		if (GetConfigValue.isOracle) {
			sql = "update word set word=?,type=? ,time = sysdate where wordid=? ";
		} else if (GetConfigValue.isMySQL) {
			sql = "update word set word=?,type=? ,time = sysdate() where wordid=? ";
		}
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定词条参数
		lstpara.add(newworditem);
		// 绑定类型参数
		lstpara.add(newtype);
		// 绑定词条id参数
		lstpara.add(wordid);
		// 将SQL语句放入集合中
		lstsql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
		
		// 更新当前词类编辑时间
		if (GetConfigValue.isOracle) {
			sql = "update wordclass set time =sysdate  where  wordclassid = ?";
		} else if (GetConfigValue.isMySQL) {
			sql = "update wordclass set time =sysdate()  where  wordclassid = ?";
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
		
		if (!"".equals(service) && !"".equals(kbdataid)) {
			// 删除与子句词模有关的词模
			sql = "delete from  wordpat where kbdataid =? and wordpat like ? ";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			// 绑定摘要ID参数
			lstpara.add(kbdataid);
			// 绑定子句词模参数
			lstpara.add(oldworditem + "@%");
			// 将SQL语句放入集合中
			lstsql.add(sql);
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );
			
			// 再将当前修改的子句条添入词模库中
			String wordpat = newworditem + "@2#编者=\"来源子句库\"";
			// 获取词模表的序列值
			int wordpatid = 0;
			if (GetConfigValue.isOracle) {
				wordpatid =  (ConstructSerialNum
						.GetOracleNextVal("SEQ_WORDPATTERN_ID"));
				sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate)";
			} else if (GetConfigValue.isMySQL) {
				wordpatid = ConstructSerialNum.getSerialID("wordpat",
						"wordpatid");
				sql = "insert into wordpat(wordpatid,wordpat,city,autosendswitch,wordpattype,kbdataid,brand,edittime) values(?,?,?,?,?,?,?,sysdate())";
			}
			lstpara = new ArrayList<Object>();
			// 绑定模板id参数
			lstpara.add(wordpatid);
			// 绑定模板参数
			lstpara.add(wordpat);
			// 绑定地市参数
			lstpara.add("全国");
			// 绑定自动开关参数
			lstpara.add(0);
			// 绑定模板类型参数
			lstpara.add(CommonLibWordclassDAO.getWordpatType(wordpat));
			// 绑定摘要id参数
			lstpara.add(kbdataid);
			// 绑定品牌参数
			lstpara.add(service);
			// 将SQL语句放入集合中
			lstsql.add(sql.toString());
			// 将对应的绑定参数集合放入集合中
			lstlstpara.add(lstpara);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql + "#" + lstpara );

//			// 定义绑定参数集合
//			lstpara = new ArrayList<Object>();
//			// 定义新增模板备份表
//			sql = "insert into wordpatprecision(wordpatid,city,brand,correctnum,callvolume,wpprecision,autoreplyflag,projectflag) values(?,?,?,0,0,0,0,0)";
//			// 绑定模板id参数
//			lstpara.add(wordpatid);
//			// 绑定地市参数
//			lstpara.add("上海");
//			// 绑定品牌参数
//			lstpara.add("通用行业主题");
//			// 将SQL语句放入集合中
//			lstsql.add(sql.toString());
//			// 将对应的绑定参数集合放入集合中
//			lstlstpara.add(lstpara);
		}
		// 生成操作日志记录
		// 将SQL语句放入集合中
		lstsql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		lstlstpara.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", " ", "更新词条", oldworditem
				+ "==>" + newworditem, "WORD"));
		// 执行SQL语句，绑定事务处理，返回事务处理结果
		return Database.executeNonQueryTransaction(lstsql, lstlstpara);
	}

	/**
	 * 判断词条是否重复
	 * 
	 * @param curwordclassid参数当前词类id
	 * @param worditem参数词条名称
	 * @param newtype参数词条类型
	 * @return 是否重复
	 */
	public static boolean exist(String curwordclassid, String worditem,
			String newtype) {
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 查询词条是否重复的SQL语句
		String sql = "select wordid from word where wordclassid=? and word=? and type=? ";
		// 绑定词类id参数
		lstpara.add(curwordclassid);
		// 绑定词条参数
		lstpara.add(worditem);
		// 绑定词条类型参数
		lstpara.add(newtype);
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		// 判断数据源标签null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 有重复词条，返回true
			return true;
		} else {
			// 没有重复词条，返回false
			return false;
		}

	}

	/**
	 * 判断词条是否重复
	 * 
	 * @param name
	 *            词条
	 * @param wordclassid参数词类名称id
	 * @return boolean
	 */
	public static boolean exist(String name, String wordclassid) {
		// 定义查询属性名称是否重复的SQL语句
		String sql = "select * from word where stdwordid is null and word=? and wordclassid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定属性名称id参数
		lstpara.add(wordclassid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return true;
		}
		return false;
	}

	/**
	 *@description 判断词条下是否存在别名
	 *@param wordid
	 *            词条ID
	 *@return
	 *@returnType Boolean
	 */
	public static Boolean isHaveOtherName(String wordid) {
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select wordid from word where stdwordid=? and rownum<2 ";
		} else if (GetConfigValue.isMySQL) {
			sql = "select wordid from word where  stdwordid=?  limit 0,2";
		}
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定别名id参数
		lstpara.add(wordid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		if (rs != null && rs.getRowCount() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取词条数量
	 * 
	 * @param worditem参数词条
	 * @param worditemprecise参数是否精确
	 * @param iscurrentwordclass参数是否当前词类
	 * @param worditemtype参数词条类型
	 * @param curwordclass参数当前词类名称
	 * @return Integer
	 */
	public static Integer getWordCount(String worditem,
			Boolean worditemprecise, Boolean iscurrentwordclass,
			String worditemtype, String curwordclass, String contatiner) {
		// 定义查询词条的SQL语句
		String sql = "select count(*) c from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=? ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定类型参数
		lstpara.add(contatiner);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前词类
		if (iscurrentwordclass) {
			// 加上词类条件
			paramSql.append(" and a.wordclass=? ");
			// 绑定词类参数
			lstpara.add(curwordclass);
		}
		// 判断词条是否为null，空
		if (worditem != null && !"".equals(worditem)) {
			// 判断是否精确查询词条
			if (worditemprecise) {
				// 加上精确查询词条条件
				paramSql.append(" and t.word =? ");
				// 绑定词条名称参数
				lstpara.add(worditem);
			} else {
				// 加上模糊查询词条条件
				paramSql.append(" and t.word like ? ");
				// 绑定词条名称参数
				lstpara.add("%" + worditem + "%");
			}
		}
		// 判断词条类型是否为null，空
		if (worditemtype != null && !"".equals(worditemtype)) {
			// 类型为全部时的条件SQL语句
			if (NewEquals.equals("0",worditemtype)) {
				// 加上条件SQL语句
				paramSql
						.append(" and (t.type=? or t.type=? or t.type is null ) ");
				// 绑定类型参数
				lstpara.add("标准名称");
				// 绑定类型参数
				lstpara.add("普通词");
			} else if (NewEquals.equals("1",worditemtype)) {// 类型是标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and t.type=? ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("2",worditemtype)) {// 类型是已录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid) ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("3",worditemtype)) {// 类型是未录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and not exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid)");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("4",worditemtype)) {// 类型是非标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and (t.type=? or t.type is null )");
				// 绑定类型参数
				lstpara.add("普通词");
			}
		} else {
			// 加上条件SQL语句
			paramSql.append(" and (t.type=? or t.type=? or t.type is null ) ");
			// 绑定类型参数
			lstpara.add("标准名称");
			// 绑定类型参数
			lstpara.add("普通词");
		}
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql + paramSql.toString(), lstpara
				.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + paramSql.toString() + "#" + lstpara );
		
		if (rs != null && rs.getRowCount() > 0) {
			String count = rs.getRows()[0].get("c").toString();
			return Integer.valueOf(count);
		} else {
			return 0;
		}
	}

	/**
	 * 带分页的查询满足条件的词条信息
	 * 
	 * @param start参数开始条数
	 * @param limit参数每页条数
	 * @param worditem参数词条
	 * @param worditemprecise参数是否精确
	 * @param iscurrentwordclass参数是否当前词类
	 * @param worditemtype参数词条类型
	 * @param curwordclass参数当前词类名称
	 * @return
	 */
	public static Result select(int start, int limit, String worditem,
			Boolean worditemprecise, Boolean iscurrentwordclass,
			String worditemtype, String curwordclass, String contatiner) {
		// 定义查询词条的SQL语句
		String sql = "select * from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定类型参数
		lstpara.add(contatiner);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前词类
		if (iscurrentwordclass) {
			// 加上词类条件
			paramSql.append(" and a.wordclass=? ");
			// 绑定词类参数
			lstpara.add(curwordclass);
		}
		// 判断词条是否为null，空
		if (worditem != null && !"".equals(worditem)) {
			// 判断是否精确查询词条
			if (worditemprecise) {
				// 加上精确查询词条条件
				paramSql.append(" and t.word =? ");
				// 绑定词条名称参数
				lstpara.add(worditem);
			} else {
				// 加上模糊查询词条条件
				paramSql.append(" and t.word like ? ");
				// 绑定词条名称参数
				lstpara.add("%" + worditem + "%");
			}
		}
		// 判断词条类型是否为null，空
		if (worditemtype != null && !"".equals(worditemtype)) {
			// 类型为全部时的条件SQL语句
			if (NewEquals.equals("0",worditemtype)) {
				// 加上条件SQL语句
				paramSql
						.append(" and (t.type=? or t.type=? or t.type is null ) ");
				// 绑定类型参数
				lstpara.add("标准名称");
				// 绑定类型参数
				lstpara.add("普通词");
			} else if (NewEquals.equals("1",worditemtype)) {// 类型是标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and t.type=? ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("2",worditemtype)) {// 类型是已录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid) ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("3",worditemtype)) {// 类型是未录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and not exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid)");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("4",worditemtype)) {// 类型是非标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and (t.type=? or t.type is null )");
				// 绑定类型参数
				lstpara.add("普通词");
			}
		} else {
			// 加上条件SQL语句
			paramSql.append(" and (t.type=? or t.type=? or t.type is null ) ");
			// 绑定类型参数
			lstpara.add("标准名称");
			// 绑定类型参数
			lstpara.add("普通词");
		}
		if (GetConfigValue.isOracle) {
			sql = "select t2.* from(select t1.*, rownum rn from (select a.wordclass,t.type,t.word,t.wordid,t.wordclassid from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=? "
					+ paramSql.toString()
					+ " order by t.wordid desc)t1)t2 where t2.rn>? and t2.rn<=? ";
			// 带分页的查询满足条件的SQL语句
			// 绑定开始条数参数
			lstpara.add(start);
			// 绑定截止条数参数
			lstpara.add(start + limit);
			// 执行SQL语句，获取相应的数据源
		} else if (GetConfigValue.isMySQL) {
			sql = "select t2.* from(select t1.*  from (select a.wordclass,t.type,t.word,t.wordid,t.wordclassid from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=? "
					+ paramSql.toString()
					+ " order by t.wordid desc)t1)t2 limit ?,? ";
			// 带分页的查询满足条件的SQL语句
			// 绑定开始条数参数
			lstpara.add(start);
			// 绑定截止条数参数
			lstpara.add(limit);
			// 执行SQL语句，获取相应的数据源
		}

		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}

	/**
	 * 
	 *描述：查询满足条件的词条信息(不分页)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-26 时间：下午05:00:43
	 * @param worditem参数词条
	 * @param worditemprecise参数是否精确
	 * @param iscurrentwordclass参数是否当前词类
	 * @param worditemtype参数词条类型
	 * @param curwordclass参数当前词类名称
	 */
	public static Result select(String worditem, Boolean worditemprecise,
			Boolean iscurrentwordclass, String worditemtype,
			String curwordclass, String contatiner) {
		// 定义查询词条的SQL语句
		String sql = "select * from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定类型参数
		lstpara.add(contatiner);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前词类
		if (iscurrentwordclass) { 
			// 加上词类条件
			paramSql.append(" and a.wordclass=? ");
			// 绑定词类参数
			lstpara.add(curwordclass);
		}
		// 判断词条是否为null，空
		if (worditem != null && !"".equals(worditem)) {
			// 判断是否精确查询词条
			if (worditemprecise) {
				// 加上精确查询词条条件
				paramSql.append(" and t.word =? ");
				// 绑定词条名称参数
				lstpara.add(worditem);
			} else {
				// 加上模糊查询词条条件
				paramSql.append(" and t.word like ? ");
				// 绑定词条名称参数
				lstpara.add("%" + worditem + "%");
			}
		}
		// 判断词条类型是否为null，空
		if (worditemtype != null && !"".equals(worditemtype)) {
			// 类型为全部时的条件SQL语句
			if (NewEquals.equals("0",worditemtype)) {
				// 加上条件SQL语句
				paramSql
						.append(" and (t.type=? or t.type=? or t.type is null ) ");
				// 绑定类型参数
				lstpara.add("标准名称");
				// 绑定类型参数
				lstpara.add("普通词");
			} else if (NewEquals.equals("1",worditemtype)) {// 类型是标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and t.type=? ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("2",worditemtype)) {// 类型是已录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid) ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("3",worditemtype)) {// 类型是未录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and not exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid)");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("4",worditemtype)) {// 类型是非标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and (t.type=? or t.type is null )");
				// 绑定类型参数
				lstpara.add("普通词");
			} else if (NewEquals.equals("5",worditemtype)) {// 类型为非错词 钱磊
				// 加上条件SQL语句
				paramSql.append(" and t.type!=? ");
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
		sql += paramSql.toString() + " order by t.wordid desc";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}

	/**
	 * 
	 *描述：查询满足条件的词条信息(不分页)
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-26 时间：下午05:00:43
	 * @param worditem参数词条
	 * @param worditemprecise参数是否精确
	 * @param iscurrentwordclass参数是否当前词类
	 * @param worditemtype参数词条类型
	 * @param curwordclass参数当前词类名称
	 */
	public static Result selectByWordClassID(String worditem, Boolean worditemprecise,
			Boolean iscurrentwordclass, String worditemtype,
			String curwordclassID) {
		// 定义查询词条的SQL语句
		String sql = "select * from word t,wordclass a where t.wordclassid=a.wordclassid ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();
		// 判断是否是当前词类
		if (iscurrentwordclass) { 
			// 加上词类条件
			paramSql.append(" and a.wordclassid=? ");
			// 绑定词类参数
			lstpara.add(curwordclassID);
		}
		// 判断词条是否为null，空
		if (worditem != null && !"".equals(worditem)) {
			// 判断是否精确查询词条
			if (worditemprecise) {
				// 加上精确查询词条条件
				paramSql.append(" and t.word =? ");
				// 绑定词条名称参数
				lstpara.add(worditem);
			} else {
				// 加上模糊查询词条条件
				paramSql.append(" and t.word like ? ");
				// 绑定词条名称参数
				lstpara.add("%" + worditem + "%");
			}
		}
		// 判断词条类型是否为null，空
		if (worditemtype != null && !"".equals(worditemtype)) {
			// 类型为全部时的条件SQL语句
			if (NewEquals.equals("0",worditemtype)) {
				// 加上条件SQL语句
				paramSql
						.append(" and (t.type=? or t.type=? or t.type is null ) ");
				// 绑定类型参数
				lstpara.add("标准名称");
				// 绑定类型参数
				lstpara.add("普通词");
			} else if (NewEquals.equals("1",worditemtype)) {// 类型是标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and t.type=? ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("2",worditemtype)) {// 类型是已录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid) ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("3",worditemtype)) {// 类型是未录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and not exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid)");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("4",worditemtype)) {// 类型是非标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and (t.type=? or t.type is null )");
				// 绑定类型参数
				lstpara.add("普通词");
			} else if (NewEquals.equals("5",worditemtype)) {// 类型为非错词 钱磊
				// 加上条件SQL语句
				paramSql.append(" and t.type!=? ");
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
		sql += paramSql.toString() + " order by t.wordid desc";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}
	
	/**
	 *@description  查询词条city
	 *@param wordclass 词类名称
	 *@param wordid 词条ID
	 *@return 
	 *@returnType Result 
	 */
	public static Result selectWordCity(String wordclass,String wordid) {
		// 定义查询词条的SQL语句
		String sql = "select * from word t,wordclass a where t.wordclassid=a.wordclassid and a.wordclass=? and t.wordid=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定词类参数
		lstpara.add(wordclass);
		// 绑定词条ID参数
		lstpara.add(wordid);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
	
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}
	
	/**
	 *@description  查询词条city
	 *@param wordid 词条ID
	 *@return 
	 *@returnType Result 
	 */
	public static Result selectWordCity(String wordid) {
		// 定义查询词条的SQL语句
		String sql = "select * from word t where t.wordid=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定词条ID参数
		lstpara.add(wordid);
	
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}
	
	/**
	 *@description  查询词条city
	 *@param wordid 词条ID
	 *@return 
	 *@returnType Result 
	 */
	public static Result selectStdWordCitybyWordid(String wordid) {
		// 定义查询词条的SQL语句
		String sql = "select * from word t where t.wordid=(select stdwordid from word where wordid=?)";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定词条ID参数
		lstpara.add(wordid);
	
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}
	
	/**
	 *@description  更新词条city
	 *@param wordclass 词类
	 *@param wordid 词条ID
	 *@param cityNme 城市名称
	 *@param cityCode 城市代码
	 *@return 
	 *@returnType int 
	 */
	public static int updateWordCity(String wordclass ,String wordid,String cityNme,String cityCode) {
		// 定义多条SQL语句集合
		List<String> lstsql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstlstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara;
		
		//修改词条地市信息
//		String sql = "update word w set w.cityname='"+cityNme+"', w.city ='"+cityCode+"' where  w.wordid="+wordid+" and w.wordclassid =(select wordclassid from wordclass where wordclass='"+wordclass+"' ) ";
		String sql = "update word w set w.cityname=?, w.city =? where  w.wordid=? and w.wordclassid =(select wordclassid from wordclass where wordclass= ? ) ";
		lstsql.add(sql);
		lstpara = new ArrayList<Object>();
		lstpara.add(cityNme);
		lstpara.add(cityCode);
		lstpara.add(wordid);
		lstpara.add(wordclass);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		//修改词条下别名地市信息
		lstpara = new ArrayList<Object>();
		sql = "update word w set w.cityname=?, w.city =? where  w.stdwordid =? ";
		lstsql.add(sql);
		lstpara.add(cityNme);
		lstpara.add(cityCode);
		lstpara.add(wordid);
		lstlstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		int c = Database.executeNonQueryTransaction(lstsql, lstlstpara);
		return c;
	}
	
	
	/**
	 * 
	 *描述：查询多个词类下的词条
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-11-27 时间：下午03:48:50
	 *@param curwordclass
	 *@param worditemtype
	 *@param contatiner
	 *@return Result
	 */
	public static Result selectBYwordCalssNameList(List<String> curwordclass,
			String worditemtype, String contatiner) {
		// 定义查询词条的SQL语句
		String sql = "select * from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定类型参数
		lstpara.add(contatiner);
		// 定义条件的SQL语句
		StringBuilder paramSql = new StringBuilder();

		if (curwordclass.size() <= 0) {
			return null;
		} else {
			paramSql.append(" and (");
			for (String wordClassName : curwordclass) {
				paramSql.append(" a.wordclass=? or ");
				lstpara.add(wordClassName);
			}
			paramSql.delete(paramSql.length() - 3, paramSql.length());
			paramSql.append(")");
		}

		// 判断词条类型是否为null，空
		if (worditemtype != null && !"".equals(worditemtype)) {
			// 类型为全部时的条件SQL语句
			if (NewEquals.equals("0",worditemtype)) {
				// 加上条件SQL语句
				paramSql
						.append(" and (t.type=? or t.type=? or t.type is null ) ");
				// 绑定类型参数
				lstpara.add("标准名称");
				// 绑定类型参数
				lstpara.add("普通词");
			} else if (NewEquals.equals("1",worditemtype)) {// 类型是标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and t.type=? ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("2",worditemtype)) {// 类型是已录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid) ");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("3",worditemtype)) {// 类型是未录入标准词时的SQL语句
				// 加上条件SQL语句
				paramSql
						.append(" and t.type=? and not exists (select t1.wordid from word t1 where  t1.stdwordid=t.wordid)");
				// 绑定类型参数
				lstpara.add("标准名称");
			} else if (NewEquals.equals("4",worditemtype)) {// 类型是非标准词时的SQL语句
				// 加上条件SQL语句
				paramSql.append(" and (t.type=? or t.type is null )");
				// 绑定类型参数
				lstpara.add("普通词");
			}
		} else {
			// 加上条件SQL语句
			paramSql.append(" and (t.type=? or t.type=? or t.type is null ) ");
			// 绑定类型参数
			lstpara.add("标准名称");
			// 绑定类型参数
			lstpara.add("普通词");
		}
		sql += paramSql.toString() + " order by t.wordid desc";
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}

	/**
	 * 分页查询满足条件的词条
	 * 
	 * @param wordclassid参数词类id
	 * @param name
	 *            词条名称
	 * @return int
	 */
	public static int getWordCount(String wordclassid, String name) {
		int count = -1;
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义SQL语句
		sql = new StringBuilder();
		// 定义查询属性值的总条数的SQL语句
		sql.append("select * from word where stdwordid is null and wordclassid=? ");
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称id参数
		lstpara.add(wordclassid);
		// 判断问题要素值(词条)名称是否为null，空
		if (name != null && !"".equals(name)) {
			// 加上问题要素值的like查询
			sql.append(" and word like ? ");
			// 绑定问题要素(词条)参数
			lstpara.add("%" + name + "%");
		}
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			// 将总条数放入jsonObj的total对象中
			count = rs.getRowCount();
		}
		return count;
	}

	/**
	 * 分页查询满足条件的词条
	 * 
	 * @param wordclassid参数词类id
	 * @param name参数问题要素值名称
	 * @param page参数页数
	 * @param rows参数每页条数
	 * @return Result
	 */
	public static Result select(String wordclassid, String name, int page,
			int rows) {
		// 定义返回的json串
		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 分页查询满足条件的属性值的SQL语句
		if (GetConfigValue.isOracle) {
			sql
					.append("select * from (select t.*,rownum rn from (select * from word where stdwordid is null and wordclassid=? ");
			// 绑定属性名称id参数
			lstpara.add(wordclassid);
			// 判断问题要素值(词条)名称是否为null，空
			if (name != null && !"".equals(name)) {
				// 加上问题要素值的like查询
				sql.append(" and word like ? ");
				// 绑定问题要素(词条)参数
				lstpara.add("%" + name + "%");
			}
			// 将SQL语句补充完整
			sql.append(" order by wordid desc)t) where rn>? and rn<=?");
		} else if (GetConfigValue.isMySQL) {
			sql
					.append("select * from (select t.* from (select * from word where stdwordid is null and wordclassid=? ");
			// 绑定属性名称id参数
			lstpara.add(wordclassid);
			// 判断问题要素值(词条)名称是否为null，空
			if (name != null && !"".equals(name)) {
				// 加上问题要素值的like查询
				sql.append(" and word like ? ");
				// 绑定问题要素(词条)参数
				lstpara.add("%" + name + "%");
			}
			// 将SQL语句补充完整
			sql.append(" order by wordid desc)t)w limit ?,?");
		}

		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(page * rows);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return rs;
	}

	/**
	 * 新增词条
	 * 
	 * @param name
	 *            词条名
	 * @param wordclassid词类名称id
	 * @param wordclass词类名称
	 * @return int
	 */
	public static int insert(String name, String wordclassid, User user) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义新增词条的SQL语句
		String sql = "insert into word (wordid,wordclassid,word,type) values(?,?,?,?)";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 获取词条表的序列值，并绑定参数
		String wordid = "";
		String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		if (GetConfigValue.isOracle) {
			wordid =  (ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag));
		} else if (GetConfigValue.isMySQL) {
			wordid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
		}
		lstpara.add(wordid);
		// 绑定词类id参数
		lstpara.add(wordclassid);
		// 绑定词条参数
		lstpara.add(name);
		// 绑定词条类型参数
		lstpara.add("标准名称");
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}
	
	/**
	 *通过city构造词类下标准词
	 * 
	 * @param wordclassid 词类ID
	 * @cityCode 地市编码          
	 * @return
	 */
	public static Result getWordByWordclassid(String wordclassid,String cityCode) {
		String sql = "select * from ( select word from word where  stdwordid is null and city like '%"+ cityCode +"%'  and wordclassid="+wordclassid  ;
		sql =sql +  " union select word from word where  stdwordid is null and (city is null or city like '%全国%')  and wordclassid="+wordclassid  ;
		sql =sql+") w order by  w.word desc";
		Result rs = Database.executeQuery(sql); 
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *通过city构造词类下标准词
	 * 
	 * @param wordclassid 词类ID
	 * @cityCode 地市编码          
	 * @return
	 */
	public static Result getWordByWordclassid2(String wordclassid,String cityCode) {
		String sql = "select * from ( select wordid,word from word where  stdwordid is null and city like '%"+ cityCode +"%'  and wordclassid="+wordclassid  ;
		sql =sql +  " union select wordid,word from word where  stdwordid is null and (city is null or city like '%全国%')  and wordclassid="+wordclassid  ;
		sql =sql+") w order by  w.wordid desc, w.word desc";
		Result rs = Database.executeQuery(sql); 
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	
	/**
	 *@description  通过词类查询对应词条及别名
	 *@param wordclass
	 *@return 
	 *@returnType Reault 
	 */
	public static Result getWordByWordclass(String wordclass){
		String sql = "select distinct wc.wordclassid , wd.word ,wd.wordid, wd.city, wd.cityname, wr.word as syn from (select * from wordclass where wordclass='"+wordclass+"')wc left join (select * from word where stdwordid is null ) wd  on  wc.wordclassid = wd.wordclassid  left join (select * from word where stdwordid is not null) wr on wd.wordid = wr.stdwordid"  ;
		Result rs = Database.executeQuery(sql); 
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	/**
	 *@description  通过词条名称及地市查询其别名
	 *@param knonameAndCity
	 *@return 
	 *@returnType Reault 
	 */
	public static Result getSynonymByWordAndCity(Map<String,List<String>> knonameAndCity){
		String knonameString="";
		int i=0;
		String sql = "select * from ( select distinct wc.wordclassid , wd.word , wd.wordid , wd.city, wd.cityname, wr.word as syn from (select * from wordclass where  wordclass like '%父子句%' and wordclass not like '%场景父子句' )wc left join (select * from word where stdwordid is null and ( "  ;
		for(Map.Entry<String,List<String>> entry:knonameAndCity.entrySet()){  
			i++;
			String knoname = entry.getKey();
		    String cityCode =  entry.getValue().get(0);
//		    if(i==knonameAndCity.size()){
//		    	sql = sql+" (word='"+knoname+"' and city like'%"+cityCode+"%')" ;	
//		    }else{
//		    	sql = sql+" (word='"+knoname+"' and city like'%"+cityCode+"%') or " ;	
//		    }
		    
		    if(i==knonameAndCity.size()){
		    	sql = sql+" (word='"+knoname+"')" ;	
		    }else{
		    	sql = sql+" (word='"+knoname+"') or " ;	
		    }
		} 
		sql=sql+" ))wd  on  wc.wordclassid = wd.wordclassid  left join (select * from word where stdwordid is not null) wr on wd.wordid = wr.stdwordid ) ww where ww.word is not null";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 *@description  通过词条名称及地市查询其别名
	 *@param knonameAndCity
	 *@return 
	 *@returnType Reault 
	 */
	public static int insert (Map<String,Map<String,List<String>>> kno_wordAndSynonym, String wordclass){
//		String knonameString="";
		int c=0;
//		String wordclassid ="";
//		Result rs = CommonLibWordclassDAO.getWordclassID(wordclass);
//		if (rs != null && rs.getRowCount() > 0) {
//			wordclassid = rs.getRows()[i].get("wordclassid").toString();
//			}
//		for(Map.Entry<String,Map<String,List<String>>> entry:kno_wordAndSynonym.entrySet()){ 
//			// 定义多条SQL语句集合
//			List<String> lstSql = new ArrayList<String>();
//			// 定义多条SQL语句对应的绑定参数集合
//			List<List<?>> lstLstpara = new ArrayList<List<?>>();
//			// 定义新增词条的SQL语句
//			String sql = "insert into word (wordid,wordclassid,word,type) values(?,?,?,?)";
//			// 定义绑定参数集合
//			List<Object> lstpara = new ArrayList<Object>();
//			// 获取词条表的序列值，并绑定参数
//			int wordid = 0;
//			if (GetConfigValue.isOracle) {
//				wordid = ConstructSerialNum.GetOracleNextVal("seq_word_id");
//			} else if (GetConfigValue.isMySQL) {
//				wordid = ConstructSerialNum.getSerialID("word", "wordid");
//			}
//			lstpara.add(wordid);
//			// 绑定词类id参数
//			lstpara.add(wordclassid);
//			// 绑定词条参数
//			lstpara.add(name);
//			// 绑定词条类型参数
//			lstpara.add("标准名称");
//			// 将SQL语句放入集合中
//			lstSql.add(sql);
//			// 将对应的绑定参数集合放入集合中
//			lstLstpara.add(lstpara);	
//
//		} 
//		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	
	}
	
	
	public static int insert(Map<String,Map<String,List<String>>>  wordAndSynonym,Map<String,List<String>> knonameAndCity,String serviceType,String wordclass){
		//获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
	    List<String> lstSql = new ArrayList<String>();
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		List<Object> lstpara = new ArrayList<Object>();
		String insert_sql = "insert into word(wordid,wordclassid,word,type,city,cityname) values(?,?,?,?,?,?)";
		String update_sql =" update word set city=?,cityname=? where wordid = ? and wordclassid =?";
		String updatesyn_sql =" update word set city=?,cityname=? where stdwordid = ? and wordclassid =?";
		String wordclassid ="";
		String word  ="";
		String wordid = "";
		Result rs = CommonLibWordclassDAO.getWordclassID(wordclass);
		if (rs != null && rs.getRowCount() > 0) {
			wordclassid = rs.getRows()[0].get("wordclassid").toString();
			}
		for(Map.Entry<String,List<String>> entry:knonameAndCity.entrySet()){ 
			String knoName = entry.getKey();
			List<String> cityAndCityName = entry.getValue();
			List<String> word_info = entry.getValue();
			if(!wordAndSynonym.containsKey(knoName)){//当前场景父子句下不包含knname 直接 insert
				// 获取词条表的序列值
				if (GetConfigValue.isOracle) {
					wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id",bussinessFlag);
				} else if (GetConfigValue.isMySQL) {
					wordid = ConstructSerialNum.getSerialIDNew("word", "wordid",bussinessFlag);
				}
				
				 lstpara = new ArrayList<Object>();
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词条参数
					lstpara.add(knoName);
					// 绑定词条类型参数
					lstpara.add("标准名称");
					//绑定地市编码类型参数
					lstpara.add(word_info.get(0).replace("|", ","));
					//绑定地市名称参数
					lstpara.add(word_info.get(1).replace("|", ","));
					// 将SQL语句放入集合中
					lstSql.add(insert_sql);
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( insert_sql + "#" + lstpara );
				
				
			}else{//比较词条地市 update
				
				Map<String,List<String>> tempMap = wordAndSynonym.get(knoName);
				List<String> tempList = tempMap.get("city");
				wordid = tempMap.get("wordid").get(0);
				String city = tempList.get(0);
				String cityName =  tempList.get(1);
				String knoCity = cityAndCityName.get(0);
				String knoCityArry[] = knoCity.split("\\|");
				String knoCityName = cityAndCityName.get(1);
				String knoCityNameArry[] = knoCityName.split("\\|");
				String newCity="";
				String tempnewCity="";
				String newCityName ="";
				String tempnewCityName="";
				for(int k=0;k<knoCityNameArry.length;k++){
					String tempCityName = knoCityNameArry[k];
					if("".equals(tempCityName)){
						continue;
					}
					if(!cityName.contains(tempCityName)){
						if("".equals(tempnewCityName)){
							tempnewCityName = tempCityName+"|";	
						}else{
							tempnewCityName = tempnewCityName+tempCityName+"|";	
						}
						 
					}
					
				}
				newCityName = tempnewCityName+cityName;
				
				for(int k=0;k<knoCityArry.length;k++){
					String tempCity = knoCityArry[k];
					if("".equals(tempCity)){
						continue;
					}
					if(!city.contains(tempCity)){
						if("".equals(tempnewCity)){
							tempnewCity = tempCity+"|";	
						}else{
							tempnewCity = tempnewCity+tempCity+"|";	
						}
					}
					
				}
				newCity = tempnewCity+city;
			
				
				if(!"".equals(newCity)&&!"".equals(newCityName)){
					newCity = newCity.replace("|", ",");
					newCityName = newCityName.replace("|", ",");
					//修改词条地市
					lstpara = new ArrayList<Object>();
		    	    //绑定地市编码类型参数
					lstpara.add(newCity);
					//绑定地市名称参数
					lstpara.add(newCityName);
					//绑定词条ID参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					lstSql.add(update_sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( update_sql + "#" + lstpara );
					
					//修改别名地市
					lstpara = new ArrayList<Object>();
		    	    //绑定地市编码类型参数
					lstpara.add(newCity);
					//绑定地市名称参数
					lstpara.add(newCityName);
					//绑定词条ID参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					lstSql.add(updatesyn_sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( updatesyn_sql + "#" + lstpara );
					
				}
			
				
				
			}
		}
		int result = Database.executeNonQueryTransaction(lstSql, lstLstpara); 
	    return result;
	}
	
	/**
	 *插入标准词
	 *@param wordclassid 词类ID
	 *@param list 标准词集合
	 *@param cityCode 地市编码
	 *@return 
	 *@returnType int 
	 */
	public static int insertWord(String wordclassid,List<String> list,String cityCode,String city_name,String serviceType){
		int result=-1;
		//查询当前词类下所有词条
		Result rs = getStandardWordByWordclassid(wordclassid);
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		List<String> wordInfo =null;
		String word  =null;
		String wordid = null;
		String city = null;
		String cityName = null;
		if (rs != null && rs.getRowCount() > 0) {
			// 遍历数据源，并封装成map
			for (int i = 0; i < rs.getRowCount(); i++) {
				wordInfo = new ArrayList<String>();
				word =  rs.getRows()[i].get("word")==null? "": rs.getRows()[i].get("word").toString();
				wordid = rs.getRows()[i].get("wordid").toString();
				city = rs.getRows()[i].get("city")==null? "全国": rs.getRows()[i].get("city").toString();
				cityName = rs.getRows()[i].get("cityname")==null? "全国": rs.getRows()[i].get("cityname").toString();
				wordInfo.add(wordid);
				wordInfo.add(city);
				wordInfo.add(cityName);
				map.put(word, wordInfo);
			}
		}
	  //循环比较，确定 insert or update
		List<Map<String, List<String>>>  operationList = new ArrayList<Map<String,List<String>>>();
		Map<String, List<String>> operationMap  =null;
		for(int k=0;k<list.size();k++){
			String wd = list.get(k);
			wordInfo = new ArrayList<String>();
			operationMap = new HashMap<String, List<String>>();
			// 该词条存在
			if(map.containsKey(wd)){
				List<String> wdlist  = map.get(wd);
				wordid = wdlist.get(0);
				city = wdlist.get(1);
				if(city.indexOf(cityCode)!=-1){
					continue;
				}
				city = city+","+cityCode;
				cityName = wdlist.get(2)+","+ city_name;
				
				if("全国".equals(cityCode)){
					city = "全国";
					cityName = "全国";
				}
				
				wordInfo.add(wordid);
				wordInfo.add(city);
				wordInfo.add(cityName);
				operationMap.put("update", wordInfo);
			}else{
				wordInfo.add(wd);
				wordInfo.add(cityCode);
				wordInfo.add(city_name);
				operationMap.put("insert", wordInfo);
			}
			operationList.add(operationMap);
			
		}
	 
	      //遍历 operationList，循环入库
		     List<String> lstSql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstLstpara = new ArrayList<List<?>>();
			// 定义新增词条的SQL语句
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			String insert_sql = "insert into word(wordid,wordclassid,word,type,city,cityname) values(?,?,?,?,?,?)";
			String update_sql =" update word set city=?,cityname=? where wordid = ? and wordclassid =?";
			String updatesyn_sql = "update word set city=?,cityname=? where stdwordid = ? and wordclassid =?";
			//获得商家标识符
			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			List<String> word_info =null;
		  for(int j =0;j<operationList.size();j++){
			  Map<String, List<String>> operation_map =  operationList.get(j);
			  if(operation_map.containsKey("insert")){
						// 获取词条表的序列值
						if (GetConfigValue.isOracle) {
							wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id",bussinessFlag);
						} else if (GetConfigValue.isMySQL) {
							wordid = ConstructSerialNum.getSerialIDNew("word", "wordid",bussinessFlag);
						}
					
				 word_info   = operation_map.get("insert");
				   lstpara = new ArrayList<Object>();
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词条参数
					lstpara.add(word_info.get(0));
					// 绑定词条类型参数
					lstpara.add("标准名称");
					//绑定地市编码类型参数
					lstpara.add(word_info.get(1));
					//绑定地市名称参数
					lstpara.add(word_info.get(2));
					// 将SQL语句放入集合中
					lstSql.add(insert_sql);
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( insert_sql + "#" + lstpara );
				  
		      }else if(operation_map.containsKey("update")){
		    	  word_info   = operation_map.get("update");
		    	  //修改词条地市
		    	  lstpara = new ArrayList<Object>();
		    	    //绑定地市编码类型参数
					lstpara.add(word_info.get(1));
					//绑定地市名称参数
					lstpara.add(word_info.get(2));
					//绑定词条ID参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					lstSql.add(update_sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					
					//文件日志
					GlobalValue.myLog.info( update_sql + "#" + lstpara );
					
					//修改别名地市
					 lstpara = new ArrayList<Object>();
			    	    //绑定地市编码类型参数
					 lstpara.add(word_info.get(1));
						//绑定地市名称参数
					 lstpara.add(word_info.get(2));
						//绑定词条ID参数
					 lstpara.add(wordid);
						// 绑定词类id参数
					 lstpara.add(wordclassid);
					 lstSql.add(updatesyn_sql);
						// 将对应的绑定参数集合放入集合中
					 lstLstpara.add(lstpara);
					 
					//文件日志
						GlobalValue.myLog.info( updatesyn_sql + "#" + lstpara );
					
					
		      }
			  
		  }
		  if(lstSql.size()<1){
			  result =1; 
		  }else{
			  result = Database.executeNonQueryTransaction(lstSql, lstLstpara); 
		  }
		
		
		return result;
	}
	
    /**
     *通过词类ID获取标准词
     *@param wordclassid
     *@return 
     *@returnType Result 
     */
    public static Result getStandardWordByWordclassid(String wordclassid){
    	String sql = "select * from word where wordclassid="+wordclassid;
    	Result rs = Database.executeQuery(sql); 
    	//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
    }
	
    /**
     * 获取词条信息
     * 
     * @param wordclassid
     * @param word
     * @return
     */
	public static Result getWordInfo(String wordclassid, String word) {
		String sql = "select * from word where wordclassid = ? and word = ?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		lstpara.add(wordclassid);
		lstpara.add(word);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		return rs;
	}
	
	/**
	 * 判断别名是否重复
	 * 
	 * @param name别名
	 * @param wordclassid参数词类名称id
	 * @return boolean
	 */
	public static boolean existOtherWord(String name, String wordid) {
		// 定义查询属性名称是否重复的SQL语句
		String sql = "select * from word where word=? and stdwordid=?";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定属性名称参数
		lstpara.add(name);
		// 绑定属性名称id参数
		lstpara.add(wordid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		// 判断数据源不为null且含有数据
		if (rs != null && rs.getRowCount() > 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * 新增别名
	 * 
	 * @param name别名
	 * @param wordclassid词类名称id
	 * @param wordclass词类名称
	 * @return int
	 */
	public static int insertOtherWord(String name, String wordid, String wordclassid, User user) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义新增词条的SQL语句
		String sql = "insert into word (wordid,wordclassid,word, type, stdwordid) values(?,?,?,?,?)";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 获取词条表的序列值，并绑定参数
		String word_sid = "";
		String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		if (GetConfigValue.isOracle) {
			word_sid =  (ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag));
		} else if (GetConfigValue.isMySQL) {
			word_sid = ConstructSerialNum.getSerialIDNew("word", "wordid", bussinessFlag);
		}
		lstpara.add(word_sid);
		// 绑定词类id参数
		lstpara.add(wordclassid);
		// 绑定词条参数
		lstpara.add(name);
		// 绑定词条类型参数
		lstpara.add("标准名称");
		// 绑定词条ID参数
		lstpara.add(wordid);
		// 将SQL语句放入集合中
		lstSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		lstLstpara.add(lstpara);
		
		//文件日志
		GlobalValue.myLog.info(sql + "#" + lstpara );
		
		int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return c;
	}
}