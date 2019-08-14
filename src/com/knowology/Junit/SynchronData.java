package com.knowology.Junit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.UtilityOperate.StringUtil;
import com.knowology.bll.CommonLibMetafieldmappingDAO;
import com.knowology.bll.ConstructSerialNum;
import com.knowology.bll.PKNextVal;
import com.knowology.dal.Database;


/**
 * @author outman
 *
 */
public class SynchronData {
	public static Logger logger = Logger.getLogger("sync");
	/**
	 *@description 
	 *@param args 
	 *@returnType void 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 
//		//插入词类
//		List<String> list = readWordClass();
//		int rs1  = insertWordclass(list);
//		if(rs1>0){
//			System.out.println("词类批量插入成功!");
//		}
		
//		//插入词条
//		List<Map<String, String>> wordList = readWord();
//		Map<String, Map<String, Map<String, Map<String, String>>>> map = getWordClassAndWordDic(wordList);
//		int rs2 = insertWord(map);
//		if(rs2>0){
//			System.out.println("词条批量插入成功!");
//		}
		
		//同步FAQ的词模
//		Map<String,List<String>> abstractAndWordpat = getAbstractAndWordpat();
//		Map<String,Map<String,String>>  abstractInfo = getAbstractInfo();
//		Map<String,List<String>> insertmap = getInsertMap(abstractAndWordpat,abstractInfo);
//		insertWordPat(insertmap);
		
//		readQuestion();
		 
		//同步摘要词模
		 Map<String,Map<String,List<String>>> map = getServiceAbsWordpatDic();
		  System.out.println(map.size());
		  String bussinessFlag ="8";//商家标识 如：基金行业->建信基金->多渠道应用 ：4 ，基金行业->东方证券->多渠道应用 ：3 ， 如没有特定要求  bussinessFlag 为空字符串
		  String serviceRoot="阳光保险个性化业务";//同步目标业务根
		  String cityCode ="全国";//地市编码  "全国" 除外，多地市编码以"," 分割，如：320000,420000
		  String operationID ="dc_man2";//操作标识，便于同步后验证数据
		 insertAbstractAndWordPat(map,bussinessFlag,serviceRoot,cityCode,operationID);
		 
		//同步答案
//		 String bussiness="保险行业->阳光保险->IVR机器人";
//		 syncAnswer(bussinessFlag,bussiness,serviceRoot,operationID);
		 
		 //更新用户密码
//		 updatepwd(getWordidandPwd());
		  
//		 String[] names = { "父亲业务", "父亲摘要ID", "父亲摘要", "儿子业务", "儿子摘要",
//					"儿子摘要ID", "Business","业务X","业务Y","业务Z","业务L","业务M","业务N","相关度","继承地市"};
		 
//		 String bussiness="基金行业->建信基金->多渠道应用";
//		 List<String> kbdataid  = getKbdataID("基金行业问题库");
//		 List<String> kbdataid  = new  ArrayList<String>();
//		 kbdataid.add("11864781.2");
//		 kbdataid.add("11864789.2");
//		 InsertAttrName(kbdataid,names,bussiness);
		 
//		 long begintime = System.currentTimeMillis();
		 
		 // 同步标准名
//		 Map<String, Map<String, String>> wordClassAndStandardWordDic= getWordClassAndStandardWordDicWithID("oldstdwordDF.xlsx");
//		 syncStandardWordWithIDToFile( wordClassAndStandardWordDic,wordClassAndStandardWordDicNew,"东方");

//		 long endtime=System.currentTimeMillis();
//		 long costTime = (endtime - begintime)/1000;
//		 logger.info("----------耗时" + costTime);
		 
//		 begintime = System.currentTimeMillis();
//		 // 同步别名
//		 Map<String, Map<String, String>> wordClassAndWordDic= getWordClassAndWordDicWithID("oldwordDF.xlsx");
//		 Map<String, Map<String, String>> wordClassAndWordDicNew= getWordClassAndWordDicWithID("newword.xlsx");
//		 syncWordWithIDToFile( wordClassAndWordDic,wordClassAndWordDicNew,"东方");
//		 
//		 endtime=System.currentTimeMillis();
//		 costTime = (endtime - begintime)/1000;
//		 logger.info("+++++++++耗时" + costTime);
//		 
//		 
//		 
//		
//		 
//		 begintime = System.currentTimeMillis();
//		 // 同步别名
//		 Map<String, Map<String, String>> wordClassAndWordZODic= getWordClassAndWordDicWithID("oldwordZO.xlsx");
//		 Map<String, Map<String, String>> wordClassAndWordDicNew= getWordClassAndWordDicWithID("newword.xlsx");		 
//		 syncWordWithIDToFile( wordClassAndWordZODic,wordClassAndWordDicNew,"中欧");
//		 
//		 endtime=System.currentTimeMillis();
//		 costTime = (endtime - begintime)/1000;
//		 logger.info("+++++++++耗时" + costTime);
//		 
//		 
//		 begintime = System.currentTimeMillis();
		 
//		 // 同步标准名
//		 Map<String, Map<String, String>> wordClassAndStandardWordZODic= getWordClassAndStandardWordDicWithID("oldstdwordZO.xlsx");
//		 Map<String, Map<String, String>> wordClassAndStandardWordDicNew= getWordClassAndStandardWordDicWithID("newstdword.xlsx");
//		 syncStandardWordWithIDToFile( wordClassAndStandardWordZODic,wordClassAndStandardWordDicNew,"中欧");
//
//		 endtime=System.currentTimeMillis();
//		 costTime = (endtime - begintime)/1000;
//		 logger.info("----------耗时" + costTime);
		 
		 
		 // 同步场景要素
		 /*
		 	select name_path 业务路径,s.condition1,s.condition2,s.condition3,s.condition4,s.condition5,s.condition6,s.condition7,s.condition8,s.condition9,s.condition10,
			s.condition11,s.condition12,s.condition13,s.condition14,s.condition15,s.condition16,s.condition17,s.condition18,s.condition19,s.condition20,
			s.ruletype,s.weight,s.city,s.cityname,s.excludedcity,s.abovequestionobject,s.abovestandardquestion,s.responsetype,s.standardquestion,
			s.userquestion,s.interactiveoptions,s.ruleresponse,s.ruleresponsetemplate,
			s.isedit,s.currentnode  from 
			(select serviceid,NAME_PATH from 
			(SELECT serviceid,SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) NAME_PATH,brand
			FROM service
 		START WITH service IN ('建信基金场景')
 		CONNECT BY PRIOR serviceid = parentid) where brand='建信基金场景')a,scenariosrules s  
 		where a.serviceid=s.relationserviceid
 		order by 业务路径;   
 		
 		
 		
 		
 		select name_path 业务路径,s.name,s.weight,s.wordclassid,s.infotalbepath,s.isshare,s.city,s.interpat,s.itemmode,s.cityname,s.container
		from 
		(select serviceid,NAME_PATH from 
		(SELECT serviceid,SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) NAME_PATH,brand
  		FROM service
    	START WITH service IN ('建信基金场景')
    	CONNECT BY PRIOR serviceid = parentid) where brand='建信基金场景')a,scenarioselement s  
    	where a.serviceid=s.relationserviceid
    	order by 业务路径; 
		 */
		 // 同步场景规则
		 String fileName = "";
		 String brand = "";
		 String tableName = "";
//		 Map<String, List<Object>> scenariosRulesMap = getScenariosRulesMap(fileName);
		 List<List<Object>> scenariosRulesList = getScenariosRulesList(fileName);
		 Map<String, String> scenariosMap = getScenariosMap(brand);
		 insertScenarios(scenariosRulesList, scenariosMap, tableName);
		 
	}
	
	private static void insertScenarios(
			List<List<Object>> scenariosRulesList,
			Map<String, String> scenariosMap, String tableName) {
		
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		
		String seq = "";
		if ("scenariosrules".equals(tableName)){
			tableName += " (ruleid";
			seq = "seq_scenariosrules_id.nextval";
		} else if ("scenarioselement".equals(tableName)){
			tableName += " (SCENARIOSELEMENTID";
			seq = "seq_scenarioselement_id.nextval";
		}
		
//		for(Map.Entry<String, List<Object>> entry : scenariosRulesMap.entrySet()){
		for (int k = 1; k < scenariosRulesList.size(); k++){
//			String namepath = entry.getKey();
			String namepath = scenariosRulesList.get(k).get(0).toString();
//			if (!"业务路径".equals(namepath)){
				List<Object> valueList = scenariosRulesList.get(k);
//				List<Object> valueList = entry.getValue();
				if (scenariosMap.containsKey(namepath)){
					String serviceid = scenariosMap.get(namepath);
					lstpara = new ArrayList<Object>();
					String sql = "insert into " + tableName + ",relationserviceid";
					String sqlend = ") values (" + seq + ",?";
					lstpara.add(serviceid);
					for (int i = 1; i < valueList.size(); i++){
						sql = sql + "," + scenariosRulesList.get(0).get(i).toString();
						sqlend = sqlend + ",?";
						lstpara.add(valueList.get(i));
					}
					sql = sql + sqlend + ")";
					lstSql.add(sql);
					lstLstpara.add(lstpara);
				} else {
					logger.warn(namepath + "未找到对应业务树");
				}
//			}
		}
		int count = Database.executeNonQueryTransaction(lstSql,lstLstpara );
		logger.info(count);
		System.out.println("最终结果" + count);
	}

	private static Map<String, String> getScenariosMap(String brand) {
		Map<String, String> scenariosMap = new HashMap<String, String>();
		String sql = "SELECT serviceid,SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) name_path,brand FROM service START WITH service=? and parentid=0 CONNECT BY PRIOR serviceid = parentid;";
		Result rs = Database.executeQuery(sql, brand);
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0;i < rs.getRowCount();i++){
				scenariosMap.put(rs.getRows()[i].get("name_path").toString(), rs.getRows()[i].get("serviceid").toString());
			}
		}
		return scenariosMap;
	}

	private static List<List<Object>> getScenariosRulesList(String fileName) {
		Map<String, List<Object>> scenariosRulesMap = new HashMap<String, List<Object>>();
		
		String path;
		if(System.getProperty("os.name").toLowerCase().startsWith("win")){
			path="E:/app/ICSR4TEST/syndata/" + fileName;
		}else{
			path="/app/ICSR4TEST/syndata/" + fileName;
		}
		logger.info("开始读取" + path);
		// 获取上传文件的file
		File file = new File(path);
		// 获取上传文件的类型
		String extension = path.lastIndexOf(".") == -1 ? "" : path.substring(path.lastIndexOf(".") + 1);
		// 定义存放读取Excel文件中的内容的集合
		List<List<Object>> comb = new ArrayList<List<Object>>();
		// 判断上传文件的类型来调用不同的读取Excel文件的方法
		if ("xls".equalsIgnoreCase(extension)) {
			// 读取2003的Excel方法
			comb = read2003Excel(file);
		} else if ("xlsx".equalsIgnoreCase(extension)) {
			// 读取2007的Excel方法
			comb = read2007Excel(file);
		}
//		for (int m = 0; m < comb.size(); m++) {
//			String namePath = comb.get(m).get(0).toString();
//			List<Object> valueList = new ArrayList<Object>();
//			for (int k = 1; k < comb.get(m).size(); k++){
//				Object value = comb.get(m).get(0);
//				valueList.add(value);
//			}
//			scenariosRulesMap.put(namePath, valueList);
//		}
		
		return comb;
	}

	public static  Map<String,String> absAndCity = new HashMap<String,String>();
	/**
	 *@description 获得词类文件数据
	 *@return 
	 *@returnType List<String> 
	 */
	public static List<String> readWordClass(){
		List<String> wordclassList = new ArrayList<String>();
		String path;
		if(System.getProperty("os.name").toLowerCase().startsWith("win")){
			path="E:/app/ICSR4TEST/syndata/wordclass.txt";
		}else{
			path="/app/ICSR4TEST/syndata/wordclass.txt";
		}
	    wordclassList = readTxt(path,"GBK");
	    return wordclassList;
	}
	
	
	/**
	 *@description 获得词条文件数据
	 *@return 
	 *@returnType List<String> 
	 */
	public static List<Map<String, String>> readWord(){
		List<Map<String, String>> returnList = new ArrayList<Map<String, String>>();
		List<String> wordList = new ArrayList< String>();
		String path;
		if(System.getProperty("os.name").toLowerCase().startsWith("win")){
			path="E:/app/ICSR4TEST/syndata/word.txt";
		}else{
			path="/app/ICSR4TEST/syndata/word.txt";
		}
	    wordList = readTxt(path,"GBK");
	    String key[]= {"WORDCLASS","WORDCLASSID","CONTAINER","WORD","WORDID","SYNONYMSTR","TYPE"};
	    for(int i=0;i<wordList.size();i++){
	    	Map<String,String> map = new HashMap<String, String>();
	    	String line = wordList.get(i);	
	    	String arry[] = line.split("\t");
	    	for(int j=0;j<arry.length;j++){
	    		map.put(key[j], arry[j].trim());
	    	}
	    	returnList.add(map);
	    }
	    return returnList;
	}
	
	
	
	/**
	 *@description 批量插入词条
	 *@param wordClassAndWordAndsynonyms
	 *@return 
	 *@returnType int 
	 */
	public static int insertWord(Map<String, Map<String, Map<String, Map<String, String>>>> wordClassAndWordAndsynonyms ){
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql ="insert into word(wordid,wordclassid,word,type,stdwordid) values(?,?,?,?,?)";
		Map<String,String> map = getWordclassDic();
		// 添加新的词类词条
		for (Map.Entry<String, Map<String, Map<String, Map<String, String>>>> entry : wordClassAndWordAndsynonyms
				.entrySet()) {
		
			String wordclass = entry.getKey();// 获取字典 中 词类名
			System.out.println("处理 <"+ wordclass +">下词条.....");
			String wordclassid = map.get(wordclass);
			Map<String, Map<String, Map<String, String>>> map_wordclass_value = entry
					.getValue();// 获取词类下词条及别名字典
			Map<String, Map<String, String>> container1 = map_wordclass_value
					.get("CONTAINER");
			Map<String, String> container2 = container1.get("CONTAINER1");
			String container = container2.get("CONTAINER2");
			// 插入词条
			Map<String, Map<String, String>> wordDic = map_wordclass_value
					.get("WORD");
			if (!wordDic.isEmpty()) {// 如果词条不为空，遍历去词条
				for (Map.Entry<String, Map<String, String>> wordEntry : wordDic
						.entrySet()) {
					String word = wordEntry.getKey();// 获取词条名称
					int wordid = PKNextVal.getNextVal("seq_word_id", "word");
					// 定义绑定参数集合
					lstpara = new ArrayList<Object>();
					// 绑定id参数
					lstpara.add(wordid);
					// 绑定词类id参数
					lstpara.add(wordclassid);
					// 绑定词类名称参数
					lstpara.add(word);
					// 绑定类型参数
					lstpara.add("标准名称");
					// 绑定类型参数
					lstpara.add(null);
					// 将SQL语句放入集合中
					lstSql.add(sql);
					// 将对应的绑定参数集合放入集合中
					lstLstpara.add(lstpara);
					System.out.println("处理 <"+ word +">词条.....");
					// 获取词条下别名字典
					Map<String, String> anotherNameDic = wordEntry
							.getValue();
					if (!anotherNameDic.isEmpty()) {// 如果词条别名不为空
						for (Map.Entry<String, String> anotherNameEntry : anotherNameDic
								.entrySet()) {
							String anotherName = anotherNameEntry.getKey();// 别名名称
							if("WORDID".equals(anotherName)){
								continue;
							}
							String anotherNameType = anotherNameEntry
									.getValue();// 别名类型
							// 插入别名
							int anotherNameid = PKNextVal.getNextVal("seq_word_id","word");
							// 定义绑定参数集合
							lstpara = new ArrayList<Object>();
							// 绑定id参数
							lstpara.add(anotherNameid);
							// 绑定词类id参数
							lstpara.add(wordclassid);
							// 绑定别名参数
							lstpara.add(anotherName);
							// 绑定类型参数
							lstpara.add(anotherNameType);
							// 绑定词条id参数
							lstpara.add(wordid);
							
							// 将SQL语句放入集合中
							lstSql.add(sql);
							// 将对应的绑定参数集合放入集合中
							lstLstpara.add(lstpara);

						}
					}

				}
			}
		}
		
		 return	Database.executeNonQueryBatchTransaction(sql, lstLstpara);
		
		
		
	}
	
	

	/**
	 *@description  批量插入词类
	 *@param list
	 *@return 
	 *@returnType int 
	 */
	public static int insertWordclass(List<String> list ){
			// 定义多条SQL语句集合
			List<String> lstsql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstlstpara = new ArrayList<List<?>>();
			// 定义SQL语句
			String sql = "";
			// 定义绑定参数集合
			List<Object> lstpara = new ArrayList<Object>();
			String container="";
			sql = "insert into wordclass(wordclassid,wordclass,container) values(seq_wordclass_id.nextval,?,?)";
			// 循环遍历词类集合
			for (int i = 0; i < list.size(); i++) {
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定词类参数
				lstpara.add(list.get(i));
				if(list.get(i).endsWith("子句")){
					container="子句";
				}else{
					container="基础";
				}
				// 绑定类型参数
				lstpara.add(container);
				// 将SQL语句放入集合中
				lstsql.add(sql);
				// 将对应的绑定参数集合放入集合中
				lstlstpara.add(lstpara);

			}
	
			 return	Database.executeNonQueryBatchTransaction(sql, lstlstpara);
			 
	}
	
	
	/**
	 * 
	 * 读出文件txt数据
	 * 
	 * @param name参数文件路径
	 * @return 
	 */
	private static List<String> readTxt(
			String fileNamePath,String encoding) {
		// 定义内容的集合
		List<String> list = new ArrayList<String>();
		Map<String, String> map;
		// 定义读文件流
		BufferedReader reader = null;
		// 定义文件每一行的内容变量
		String s = null;
		try {
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(fileNamePath)),encoding));
			int i = 0;
			// 循环遍历文件的每一行
			while ((s = reader.readLine()) != null) {
				try {
					// 判断每一行是否为空
					if (!"".equals(s.trim())) {
						if(s.startsWith("//")){
							continue;
						}else{
//							System.out.println(s);
							list.add(s);	
						}
						
					}
				} catch (Exception e) {
					continue;
				}
			}
			// 关闭文件流
			reader.close();
		} catch (Exception e) {
			System.out.println(e);
			return null;
		}
		return list;
	}
	
	/**
	 * 读取 office 2003 excel
	 * 
	 * @param file参数文件
	 * @return 读取Excel文件内容的集合
	 */
	private static List<List<Object>> read2003Excel(File file) {
		
		List<List<Object>> list = new LinkedList<List<Object>>();
		try {
			HSSFWorkbook hwb = new HSSFWorkbook(new FileInputStream(file));
			HSSFSheet sheet = hwb.getSheetAt(0);
			Object value = null;
			HSSFRow row = null;
			HSSFCell cell = null;

			// 读取第一行
			row = sheet.getRow(0);
			List<Object> linked = new LinkedList<Object>();
			if (row != null) {
				for (int j = 0; j <= row.getLastCellNum(); j++) {
					cell = row.getCell(j);
					if (cell == null) {
						continue;
					}
					value = cell.getStringCellValue().trim();
					linked.add(value);
				}
				list.add(linked);
			}
			int count = linked.size();
			// 读取第一行以下的部分
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				if (row == null) {
					continue;
				}
				linked = new LinkedList<Object>();
				for (int j = 0; j < count; j++) {
					cell = row.getCell(j);
					if (cell == null) {
						linked.add("");
					} else {
						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue().trim();
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							value = "";
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							cell.setCellType(XSSFCell.CELL_TYPE_STRING);
							value = cell.getStringCellValue().trim();
							break;
						default:
							value = cell.toString();
						}
						linked.add(value);
					}
				}
				list.add(linked);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * 读取Office 2007 excel
	 * 
	 * @param file参数文件
	 * @return 读取Excel文件内容的集合
	 */
	private static List<List<Object>> read2007Excel(File file) {
		List<List<Object>> list = new LinkedList<List<Object>>();
		try {
			// 构造 XSSFWorkbook 对象，strPath 传入文件路径
			XSSFWorkbook xwb = new XSSFWorkbook(new FileInputStream(file));
			XSSFSheet sheet = xwb.getSheetAt(0);
			Object value = null;
			XSSFRow row = null;
			XSSFCell cell = null;
			// 读取第一行
			row = sheet.getRow(0);
			List<Object> linked = new LinkedList<Object>();
			if (row != null) {
				for (int j = 0; j <= row.getLastCellNum(); j++) {
					cell = row.getCell(j);
					if (cell == null || "".equals(cell)) {
						continue;
					}
					linked.add(cell.getStringCellValue().trim());
				}
				list.add(linked);
			}
			int count = linked.size();
			// 读取第一行以下的部分
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				row = sheet.getRow(i);
				if (row == null) {
					continue;
				}
				linked = new LinkedList<Object>();
				for (int j = 0; j < count; j++) {
					cell = row.getCell(j);
					if (cell == null) {
						linked.add("");
					} else {
						switch (cell.getCellType()) {
						case XSSFCell.CELL_TYPE_STRING:
							value = cell.getStringCellValue().trim();
							break;
						case XSSFCell.CELL_TYPE_BLANK:
							value = "";
							break;
						case XSSFCell.CELL_TYPE_NUMERIC:
							cell.setCellType(XSSFCell.CELL_TYPE_STRING);
							value = cell.getStringCellValue().trim();
							break;
						default:
							value = cell.toString();
						}
						linked.add(value);
					}
				}
				list.add(linked);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	/**
	 * 获取Map<wordclass,wordclassid>
	 * @return
	 */
	public static Map<String, String> getWordclassidMap(){
		// 定义返回值
		Map<String, String> wordclassidMap= new HashMap<String, String>();
		String sql = "select wordclassid,wordclass from wordclass order by wordclassid desc";
		Result rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0){
			for (int i = 0 ; i < rs.getRowCount() ; i++){
				String wordclassid = rs.getRows()[i].get("wordclassid").toString();
				String wordclass = rs.getRows()[i].get("wordclass").toString();
				wordclassidMap.put(wordclass, wordclassid);
			}
		}
		return wordclassidMap;
	}
	
	/**
	 * 读取EXCEL文件，返回词类标准名MAP
	 * @param filename
	 * @return
	 */
	public static Map<String, Map<String, String>> getWordClassAndStandardWordDic(String filename) {
		// 定义返回值
		Map<String, Map<String, String>> wordClassAndStandardWordDic= new HashMap<String, Map<String, String>>();
		
		String path;
		if(System.getProperty("os.name").toLowerCase().startsWith("win")){
			path="E:/app/ICSR4TEST/syndata/" + filename;
		}else{
			path="/app/ICSR4TEST/syndata/" + filename;
		}
		logger.info("开始读取" + path);
		// 获取上传文件的file
		File file = new File(path);
		// 获取上传文件的类型
		String extension = path.lastIndexOf(".") == -1 ? "" : path.substring(path.lastIndexOf(".") + 1);
		// 定义存放读取Excel文件中的内容的集合
		List<List<Object>> comb = new ArrayList<List<Object>>();
		// 判断上传文件的类型来调用不同的读取Excel文件的方法
		if ("xls".equalsIgnoreCase(extension)) {
			// 读取2003的Excel方法
			comb = read2003Excel(file);
		} else if ("xlsx".equalsIgnoreCase(extension)) {
			// 读取2007的Excel方法
			comb = read2007Excel(file);
		}
		System.out.println("开始生成map...");
		for (int m = 1; m < comb.size(); m++) {
			if (m%1000==0){
				logger.info(m+"...");
			}
			String wordclassid = comb.get(m).get(0) == null ? "" : comb.get(m).get(0).toString();
			String wordclass = comb.get(m).get(1) == null ? "" : comb.get(m).get(1).toString();
			String standardWordid = comb.get(m).get(2) == null ? "" : comb.get(m).get(2).toString();
			String standardWord = comb.get(m).get(3) == null ? "" : comb.get(m).get(3).toString();
			if (wordClassAndStandardWordDic.containsKey(wordclass)){// 字典中存在改词类
				Map<String, String> standardWordMap = wordClassAndStandardWordDic.get(wordclass);
				standardWordMap.put(standardWord, "");
				wordClassAndStandardWordDic.put(wordclass, standardWordMap);
			}else{// 字典中不存在该词类
				Map<String, String> standardWordMap = new HashMap<String, String>();
				standardWordMap.put(standardWord, "");
				wordClassAndStandardWordDic.put(wordclass, standardWordMap);
			} 
		}
		
		return wordClassAndStandardWordDic;
	}
	
	/**
	 * 读取EXCEL文件，返回词类标准名MAP
	 * @param filename
	 * @return
	 * 
	 */
	public static Map<String, Map<String, String>> getWordClassAndStandardWordDicWithID(String filename) {
		// 定义返回值
		Map<String, Map<String, String>> wordClassAndStandardWordDic= new HashMap<String, Map<String, String>>();
		
		String path;
		if(System.getProperty("os.name").toLowerCase().startsWith("win")){
			path="E:/app/ICSR4TEST/syndata/" + filename;
		}else{
			path="/app/ICSR4TEST/syndata/" + filename;
		}
		logger.info("开始读取" + path);
		// 获取上传文件的file
		File file = new File(path);
		// 获取上传文件的类型
		String extension = path.lastIndexOf(".") == -1 ? "" : path.substring(path.lastIndexOf(".") + 1);
		// 定义存放读取Excel文件中的内容的集合
		List<List<Object>> comb = new ArrayList<List<Object>>();
		// 判断上传文件的类型来调用不同的读取Excel文件的方法
		if ("xls".equalsIgnoreCase(extension)) {
			// 读取2003的Excel方法
			comb = read2003Excel(file);
		} else if ("xlsx".equalsIgnoreCase(extension)) {
			// 读取2007的Excel方法
			comb = read2007Excel(file);
		}
		System.out.println("开始生成map...");
		for (int m = 1; m < comb.size(); m++) {
			if (m%1000==0){
				logger.info(m+"...");
			}
			String wordclassid = comb.get(m).get(0) == null ? "" : comb.get(m).get(0).toString();
			String wordclass = comb.get(m).get(1) == null ? "" : comb.get(m).get(1).toString();
			String standardWordid = comb.get(m).get(2) == null ? "" : comb.get(m).get(2).toString();
			String standardWord = comb.get(m).get(3) == null ? "" : comb.get(m).get(3).toString();
			if (wordClassAndStandardWordDic.containsKey(wordclassid)){// 字典中存在改词类
				Map<String, String> standardWordMap = wordClassAndStandardWordDic.get(wordclassid);
				standardWordMap.put(standardWordid, standardWord);
				wordClassAndStandardWordDic.put(wordclassid, standardWordMap);
			}else{// 字典中不存在该词类
				Map<String, String> standardWordMap = new HashMap<String, String>();
				standardWordMap.put(standardWordid ,  standardWord);
				wordClassAndStandardWordDic.put(wordclassid, standardWordMap);
			} 
		}
		
		return wordClassAndStandardWordDic;
	}
	
	/**
	 * 同步标准名
	 * @param wordClassAndStandardWordDic
	 * @param wordClassAndStandardWordDicNew
	 */
	public static void syncStandardWord (String bussinessFlag, Map<String, Map<String, String>> wordClassAndStandardWordDic, 
			Map<String, Map<String, String>> wordClassAndStandardWordDicNew, Map<String, String> wordclassidMap) {
		logger.info("开始同步。。。");
		for (Map.Entry<String, Map<String, String>> entry : wordClassAndStandardWordDicNew.entrySet()){
			// 定义SQL语句
			String sql ;
			// 定义绑定参数集合
			List<Object> lstpara ;
			// 定义多条SQL语句
			List<String> lstSql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstLstpara = new ArrayList<List<?>>();
			
			String wordclassNew = entry.getKey();
			Map<String, String> standardWordMapNew = entry.getValue();
			if (wordClassAndStandardWordDic.containsKey(wordclassNew)){// new词类存在于old词类中
				Map<String, String> standardWordMap = wordClassAndStandardWordDic.get(wordclassNew);
				for (String standWordNew : standardWordMapNew.keySet()){// 遍历循环
					if (!standardWordMap.containsKey(standWordNew)){// old词类中不含new词条
						String wordid = "";
						sql ="insert into word(wordid,wordclassid,word,type) values(?,(select min(wordclassid) from wordclass where wordclass=?),?,?)";
						lstpara = new ArrayList<Object>();
						// 获取词模表的序列值
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
						lstpara.add(wordid);
						lstpara.add(wordclassNew);
//						lstpara.add(wordclassidMap.get(wordclassNew));
						lstpara.add(standWordNew);
						lstpara.add("标准名称");
						
						lstSql.add(sql);
						lstLstpara.add(lstpara);
						
						logger.info(sql + "@@@" + lstpara);
						
						standardWordMap.put(standWordNew, "");
					}
				}
				int count =  Database.executeNonQueryTransaction(lstSql, lstLstpara);
				logger.info(wordclassNew+ "-已执行" + count);
			} else {
				logger.warn("需核实！词类不存在：" + wordclassNew);
			}
		}
	}
	
	/**
	 * 同步标准名
	 * @param wordClassAndStandardWordDic
	 * @param wordClassAndStandardWordDicNew
	 */
	public static void syncStandardWordToFile ( Map<String, Map<String, String>> wordClassAndStandardWordDic, 
			Map<String, Map<String, String>> wordClassAndStandardWordDicNew) {
		logger.info("开始同步。。。");
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, Map<String, String>> entry : wordClassAndStandardWordDicNew.entrySet()){
			
			// 定义SQL语句
			String sql ;
			
			String wordclassNew = entry.getKey();
			Map<String, String> standardWordMapNew = entry.getValue();
			if (wordClassAndStandardWordDic.containsKey(wordclassNew)){// new词类存在于old词类中
				Map<String, String> standardWordMap = wordClassAndStandardWordDic.get(wordclassNew);
				for (String standWordNew : standardWordMapNew.keySet()){// 遍历循环
					if (!standardWordMap.containsKey(standWordNew)){// old词类中不含new词条
						sql ="insert into word(wordid,wordclassid,word,type) values(seq_word_id.nextval,(select min(wordclassid) from wordclass where wordclass='"
							+ wordclassNew
							+ "'),'"
							+ standWordNew
							+"','标准名称')";
						
						list.add(sql);
						
						standardWordMap.put(standWordNew, "");
					}
				}
			} else {
				logger.warn("需核实！词类不存在：" + wordclassNew);
				for (String standWordNew : standardWordMapNew.keySet()){// 遍历循环
						sql ="insert into word(wordid,wordclassid,word,type) values(seq_word_id.nextval,(select min(wordclassid) from wordclass where wordclass='"
							+ wordclassNew
							+ "'),'"
							+ standWordNew
							+"','标准名称')";
						
						list.add(sql);
				}
			}
		}
		writeIntxt("/app/ICSR4TEST/syndata/stdword.txt", list, true, "UTF-8");
	}
	
	/**
	 * 同步标准名
	 * @param wordClassAndStandardWordDic
	 * @param wordClassAndStandardWordDicNew
	 */
	public static void syncStandardWordWithIDToFile ( Map<String, Map<String, String>> wordClassAndStandardWordDic, 
			Map<String, Map<String, String>> wordClassAndStandardWordDicNew, String flag) {
		logger.info("开始同步。。。");
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, Map<String, String>> entry : wordClassAndStandardWordDicNew.entrySet()){
			
			// 定义SQL语句
			String sql ;
			
			String wordclassIDNew = entry.getKey();
			Map<String, String> standardWordMapNew = entry.getValue();
			if (wordClassAndStandardWordDic.containsKey(wordclassIDNew)){// new词类存在于old词类中
				Map<String, String> standardWordMap = wordClassAndStandardWordDic.get(wordclassIDNew);
				for (Map.Entry<String, String> standWordMapNew : standardWordMapNew.entrySet()){// 遍历循环
					if (!standardWordMap.containsKey(standWordMapNew.getKey()) ){// old词类中不含new词条
						sql ="insert into word(wordid,wordclassid,word,type) values(" 
							+ standWordMapNew.getKey()
							+ ","
							+ wordclassIDNew
							+ ",'"
							+ standWordMapNew.getValue()
							+"','标准名称')";
						
						list.add(sql);
						
						standardWordMap.put(standWordMapNew.getKey(), standWordMapNew.getValue());
					}
				}
			} else {
				logger.warn("需核实！词类不存在：" + wordclassIDNew);
				for (Map.Entry<String, String> standWordMapNew : standardWordMapNew.entrySet()){// 遍历循环
						sql ="insert into word(wordid,wordclassid,word,type) values(" 
							+ standWordMapNew.getKey()
							+ ","
							+ wordclassIDNew
							+ ",'"
							+ standWordMapNew.getValue()
							+"','标准名称')";
						
						list.add(sql);
				}
			}
		}
		writeIntxt("/app/ICSR4TEST/syndata/stdword" + flag + ".txt", list, true, "UTF-8");
	}
	
	/**
	 * 读取EXCEL文件，返回词类词条别名MAP
	 * @param filename
	 * @return
	 */
	public static Map<String, Map<String, String >> getWordClassAndWordDic(String filename) {
		// 定义返回值
		Map<String, Map<String, String>> wordClassAndWordDic= new HashMap<String, Map<String, String>>();
		
		String path;
		if(System.getProperty("os.name").toLowerCase().startsWith("win")){
			path="E:/app/ICSR4TEST/syndata/" + filename;
		}else{
			path="/app/ICSR4TEST/syndata/" + filename;
		}
		logger.info("开始读取文件：" + path);
		// 获取上传文件的file
		File file = new File(path);
		// 获取上传文件的类型
		String extension = path.lastIndexOf(".") == -1 ? "" : path.substring(path.lastIndexOf(".") + 1);
		// 定义存放读取Excel文件中的内容的集合
		List<List<Object>> comb = new ArrayList<List<Object>>();
		// 判断上传文件的类型来调用不同的读取Excel文件的方法
		if ("xls".equalsIgnoreCase(extension)) {
			// 读取2003的Excel方法
			comb = read2003Excel(file);
		} else if ("xlsx".equalsIgnoreCase(extension)) {
			// 读取2007的Excel方法
			comb = read2007Excel(file);
		}
		logger.info("开始遍历。。。");
		for (int m = 0; m < comb.size(); m++) {
			if (m%1000==0){
				logger.info(m+"...");
			}
//			if (comb.get(m).get(5) == null || "".equals(comb.get(m).get(5).toString())){
//				continue;
//			}
			String wordclass = comb.get(m).get(0) == null ? "" : comb.get(m).get(0).toString();
			String standardWord = comb.get(m).get(1) == null ? "" : comb.get(m).get(1).toString();
			String word = comb.get(m).get(2) == null ? "" : comb.get(m).get(2).toString();
			String wordType = comb.get(m).get(3) == null ? "" : comb.get(m).get(3).toString();
			if (wordClassAndWordDic.containsKey(wordclass + "@@@" + standardWord)){// 字典中存在改词类
				Map<String, String> wordAndTypeMap = new HashMap<String, String>();
				wordAndTypeMap = wordClassAndWordDic.get(wordclass + "@@@" + standardWord);
				wordAndTypeMap.put(word + "@@@" + wordType, "");
				wordClassAndWordDic.put(wordclass + "@@@" + standardWord, wordAndTypeMap);
			}else{// 字典中不存在该词类
				Map<String, String> wordAndTypeMap = new HashMap<String, String>();
				wordAndTypeMap.put(word + "@@@" + wordType, "");
				wordClassAndWordDic.put(wordclass + "@@@" + standardWord, wordAndTypeMap);
			} 
		}
		
		return wordClassAndWordDic;
	}
	
	/**
	 * 读取EXCEL文件，返回词类词条别名MAP
	 * @param filename
	 * @return
	 */
	public static Map<String, Map<String, String >> getWordClassAndWordDicWithID(String filename) {
		// 定义返回值
		Map<String, Map<String, String>> wordClassAndWordDic= new HashMap<String, Map<String, String>>();
		
		String path;
		if(System.getProperty("os.name").toLowerCase().startsWith("win")){
			path="E:/app/ICSR4TEST/syndata/" + filename;
		}else{
			path="/app/ICSR4TEST/syndata/" + filename;
		}
		logger.info("开始读取文件：" + path);
		// 获取上传文件的file
		File file = new File(path);
		// 获取上传文件的类型
		String extension = path.lastIndexOf(".") == -1 ? "" : path.substring(path.lastIndexOf(".") + 1);
		// 定义存放读取Excel文件中的内容的集合
		List<List<Object>> comb = new ArrayList<List<Object>>();
		// 判断上传文件的类型来调用不同的读取Excel文件的方法
		if ("xls".equalsIgnoreCase(extension)) {
			// 读取2003的Excel方法
			comb = read2003Excel(file);
		} else if ("xlsx".equalsIgnoreCase(extension)) {
			// 读取2007的Excel方法
			comb = read2007Excel(file);
		}
		logger.info("开始遍历。。。");
		for (int m = 0; m < comb.size(); m++) {
			if (m%1000==0){
				logger.info(m+"...");
			}
			
			String wordclassid= comb.get(m).get(0) == null ? "" : comb.get(m).get(0).toString();
			String standardWordID= comb.get(m).get(2) == null ? "" : comb.get(m).get(2).toString();
			String wordID = comb.get(m).get(4) == null ? "" : comb.get(m).get(4).toString();
			String word = comb.get(m).get(5) == null ? "" : comb.get(m).get(5).toString();
			String wordType = comb.get(m).get(6) == null ? "" : comb.get(m).get(6).toString();
			if (wordClassAndWordDic.containsKey(wordclassid + "-" + standardWordID)){// 字典中存在改词类
				Map<String, String> wordAndTypeMap = new HashMap<String, String>();
				wordAndTypeMap = wordClassAndWordDic.get(wordclassid + "-" + standardWordID);
				wordAndTypeMap.put(wordID, word + "@@@" + wordType);
				wordClassAndWordDic.put(wordclassid + "-" + standardWordID, wordAndTypeMap);
			}else{// 字典中不存在该词类
				Map<String, String> wordAndTypeMap = new HashMap<String, String>();
				wordAndTypeMap.put(wordID, word + "@@@" + wordType);
				wordClassAndWordDic.put(wordclassid + "-" + standardWordID, wordAndTypeMap);
			} 
		}
		
		return wordClassAndWordDic;
	}
	
	/**
	 * 同步别名
	 * @param wordClassAndStandardWordDic
	 * @param wordClassAndStandardWordDicNew
	 */
	public static void syncWord (String bussinessFlag, Map<String, Map<String, String>> wordClassAndWordDic, 
			Map<String, Map<String, String>> wordClassAndWordDicNew) {
		for (Map.Entry<String, Map<String, String>> entry : wordClassAndWordDicNew.entrySet()){
			
			// 定义SQL语句
			String sql ;
			// 定义绑定参数集合
			List<Object> lstpara ;
			// 定义多条SQL语句
			List<String> lstSql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> lstLstpara = new ArrayList<List<?>>();
			
			String keyNew = entry.getKey();
			String wordclassNew = keyNew.split("@@@")[0];
			String standardWordNew = keyNew.split("@@@")[1];
			
			Map<String, String> wordAndTypeMapNew = new HashMap<String, String>();
			wordAndTypeMapNew = entry.getValue();
			if (wordClassAndWordDic.containsKey(keyNew)){// new词类+标准名存在于old词类+标准名中
				Map<String, String> wordAndTypeMap = wordClassAndWordDic.get(keyNew);
				for (String wordAndTypeNew : wordAndTypeMapNew.keySet()){// 遍历循环
					if (!wordAndTypeMap.containsKey(wordAndTypeNew)){// old词类中不含new词条+类型
						String wordid = "";
						sql ="insert into word(wordid,wordclassid,word,type,stdwordid) values(?,(select wordclass.wordclassid from word, wordclass where word.wordclassid=wordclass.wordclassid and word.stdwordid is null and wordclass.wordclass=? and word.word=?),?,?,(select wordid from word, wordclass where word.wordclassid=wordclass.wordclassid and word.stdwordid is null and wordclass.wordclass=? and word.word=?))";
						lstpara = new ArrayList<Object>();
//						// 获取词模表的序列值
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
						lstpara.add(wordid);
						lstpara.add(wordclassNew);
						lstpara.add(standardWordNew);
						lstpara.add(wordAndTypeNew.split("@@@")[0]);
						lstpara.add(wordAndTypeNew.split("@@@")[1]);
						lstpara.add(wordclassNew);
						lstpara.add(standardWordNew);
						
						lstSql.add(sql);
						lstLstpara.add(lstpara);
						
						logger.info(sql + "@@@" + lstpara);
						
						wordAndTypeMap.put(wordAndTypeNew, "");
					}
				}
				int count =  Database.executeNonQueryTransaction(lstSql, lstLstpara);
				logger.info(keyNew +" 执行" + count);
			} else {
				logger.info("需核实！词类+标准名不存在：" + keyNew);
				for (String wordAndTypeNew : wordAndTypeMapNew.keySet()){// 遍历循环
						String wordid = "";
						sql ="insert into word(wordid,wordclassid,word,type,stdwordid) values(?,(select wordclass.wordclassid from word, wordclass where word.wordclassid=wordclass.wordclassid and word.stdwordid is null and wordclass.wordclass=? and word.word=?),?,?,(select wordid from word, wordclass where word.wordclassid=wordclass.wordclassid and word.stdwordid is null and wordclass.wordclass=? and word.word=?))";
						lstpara = new ArrayList<Object>();
//						// 获取词模表的序列值
						wordid = ConstructSerialNum.GetOracleNextValNew("seq_word_id", bussinessFlag);
						lstpara.add(wordid);
						lstpara.add(wordclassNew);
						lstpara.add(standardWordNew);
						lstpara.add(wordAndTypeNew.split("@@@")[0]);
						lstpara.add(wordAndTypeNew.split("@@@")[1]);
						lstpara.add(wordclassNew);
						lstpara.add(standardWordNew);
						
						lstSql.add(sql);
						lstLstpara.add(lstpara);
						
						logger.info(sql + "@@@" + lstpara);
						
				}
				int count =  Database.executeNonQueryTransaction(lstSql, lstLstpara);
				logger.info(keyNew +" 执行" + count);
				
			}
		}
		
	}
	
	/**
	 * 同步别名
	 * @param wordClassAndStandardWordDic
	 * @param wordClassAndStandardWordDicNew
	 */
	public static void syncWordToFile ( Map<String, Map<String, String>> wordClassAndWordDic, 
			Map<String, Map<String, String>> wordClassAndWordDicNew) {
		List<String> list = new ArrayList<String>();
		for (Map.Entry<String, Map<String, String>> entry : wordClassAndWordDicNew.entrySet()){
			// 定义SQL语句
			String sql ;
			
			String keyNew = entry.getKey();
			String wordclassNew = keyNew.split("@@@")[0];
			String standardWordNew = keyNew.split("@@@")[1];
			
			Map<String, String> wordAndTypeMapNew = new HashMap<String, String>();
			wordAndTypeMapNew = entry.getValue();
			if (wordClassAndWordDic.containsKey(keyNew)){// new词类+标准名存在于old词类+标准名中
				Map<String, String> wordAndTypeMap = wordClassAndWordDic.get(keyNew);
				for (String wordAndTypeNew : wordAndTypeMapNew.keySet()){// 遍历循环
					if (!wordAndTypeMap.containsKey(wordAndTypeNew)){// old词类中不含new词条+类型
						String wordid = "";
						sql ="insert into word(wordid,wordclassid,word,type,stdwordid) values(seq_word_id.nextval,(select wordclass.wordclassid from word, wordclass where word.wordclassid=wordclass.wordclassid and word.stdwordid is null and wordclass.wordclass='"
							+ wordclassNew
							+ "' and word.word='" 
							+ standardWordNew
							+ "'),'" 
							+ wordAndTypeNew.split("@@@")[0]
							+ "','" +wordAndTypeNew.split("@@@")[1]
							+ "',(select wordid from word, wordclass where word.wordclassid=wordclass.wordclassid and word.stdwordid is null and wordclass.wordclass='" 
							+ wordclassNew
							+ "' and word.word='" 
							+ standardWordNew
							+ "'))";
						
						list.add(sql);
						
						wordAndTypeMap.put(wordAndTypeNew, "");
					}
				}
			} else {
				logger.info("需核实！词类+标准名不存在：" + keyNew);
				for (String wordAndTypeNew : wordAndTypeMapNew.keySet()){// 遍历循环
						String wordid = "";
						sql ="insert into word(wordid,wordclassid,word,type,stdwordid) values(seq_word_id.nextval,(select wordclass.wordclassid from word, wordclass where word.wordclassid=wordclass.wordclassid and word.stdwordid is null and wordclass.wordclass='" 
							+ wordclassNew
							+ "' and word.word='" 
							+ standardWordNew
							+ "'),'"
							+ wordAndTypeNew.split("@@@")[0]
							+ "','"
							+ wordAndTypeNew.split("@@@")[1]
							+ "',(select wordid from word, wordclass where word.wordclassid=wordclass.wordclassid and word.stdwordid is null and wordclass.wordclass='"
							+ wordclassNew
							+ "' and word.word='"
							+ standardWordNew
							+"'))";
						list.add(sql);
				}
				
			}
		}
		writeIntxt("/app/ICSR4TEST/syndata/word.txt", list, true, "UTF-8");
		
	}
	
	/**
	 * 同步别名
	 * @param wordClassAndStandardWordDic
	 * @param wordClassAndStandardWordDicNew
	 */
	public static void syncWordWithIDToFile ( Map<String, Map<String, String>> wordClassAndWordDic, 
			Map<String, Map<String, String>> wordClassAndWordDicNew, String flag) {
		List<String> list = new ArrayList<String>(); 
		for (Map.Entry<String, Map<String, String>> entry : wordClassAndWordDicNew.entrySet()){
			// 定义SQL语句
			String sql ;
			
			String wordclassidAndStdWordIDNew = entry.getKey();
			Map<String, String> wordMapNew = entry.getValue();
			
			if (wordClassAndWordDic.containsKey(wordclassidAndStdWordIDNew)){// new词类+标准名存在于old词类+标准名中
				Map<String, String> wordMap = wordClassAndWordDic.get(wordclassidAndStdWordIDNew);
				for (Map.Entry<String, String> wordAndTypeNew : wordMapNew.entrySet()){// 遍历循环
					if (!wordMap.containsKey(wordAndTypeNew.getKey())){// old词类中不含new词条+类型
						sql ="insert into word(wordid,wordclassid,word,type,stdwordid) values(" 
							+ wordAndTypeNew.getKey()
							+ ","
							+ wordclassidAndStdWordIDNew.split("-")[0]
							+ ",'" 
							+ wordAndTypeNew.getValue().split("@@@")[0]
							+ "','" 
							+ wordAndTypeNew.getValue().split("@@@")[1]
							+ "'," 
							+ wordclassidAndStdWordIDNew.split("-")[1]
							+ ")";
						
						list.add(sql);
						
						wordMap.put(wordAndTypeNew.getKey(), wordAndTypeNew.getValue());
					}
				}
			} else {
				logger.info("需核实！词类+标准名不存在：" + wordclassidAndStdWordIDNew);
				for (Map.Entry<String, String> wordAndTypeNew : wordMapNew.entrySet()){// 遍历循环
						sql ="insert into word(wordid,wordclassid,word,type,stdwordid) values(" 
							+ wordAndTypeNew.getKey()
							+ ","
							+ wordclassidAndStdWordIDNew.split("-")[0]
							+ ",'" 
							+ wordAndTypeNew.getValue().split("@@@")[0]
							+ "','" 
							+ wordAndTypeNew.getValue().split("@@@")[1]
							+ "'," 
							+ wordclassidAndStdWordIDNew.split("-")[1]
							+ ")";
						list.add(sql);
				}
				
			}
		}
		writeIntxt("/app/ICSR4TEST/syndata/word" + flag + ".txt", list, true, "UTF-8");
		
	}
	
	
	/**
	 * 分别查询需同步的词库 并将词类词条别名分装成结构体Map<String, Map<String, Map<String, Map<String,
	 * String>>>> ，如下：
	 * 
	 *'工资近类':{ 'word':{'~人工':{}, '工资':{ 'wordid':'977374', '人工':'其他别名',
	 * '公子':'其他别名', '公资':'其他别名', '奖金':'其他别名', '工钱':'其他别名', '待遇':'其他别名',
	 * '白干':'其他别名', '福利':'其他别名', '绩效工资':'其他别名', '薪':'其他别名', '薪水':'其他别名',
	 * '薪资':'其他别名' } }, 'wordclassid':{'wordclassid1':{'wordclassid2':'12413'}}
	 * }
	 * */
	public static Map<String, Map<String, Map<String, Map<String, String>>>> getWordClassAndWordDic(
			List<Map<String, String>> info) {
		Map<String, Map<String, Map<String, Map<String, String>>>> wordClassAndWordAndsynonyms = new HashMap<String, Map<String, Map<String, Map<String, String>>>>();

		for (int i = 0; i < info.size(); i++) {
			Map<String, Map<String, Map<String, String>>> wordAndsynonym = new HashMap<String, Map<String, Map<String, String>>>();
			Map<String, Map<String, String>> wordAndsynonymAndType = new HashMap<String, Map<String, String>>();
			Map<String, String> synonymAndType = new HashMap<String, String>();
			Map<String, String> map = info.get(i);
			String wordclass;
			String word;
			String synonym;
			String type = null;
			String wordclassid;
			String wordid;
			String container;
			wordclass = map.get("WORDCLASS").toString();
			wordclassid = map.get("WORDCLASSID").toString();
			container = map.get("CONTAINER") == null ? "" : map
					.get("CONTAINER").toString();
			if (wordClassAndWordAndsynonyms.containsKey(wordclass)) {
				word = map.get("WORD") == null ? "" : map.get("WORD")
						.toString();
				wordid = map.get("WORDID") == null ? "" : map.get("WORDID")
						.toString();
				synonym = map.get("SYNONYMSTR") == null ? "" : map.get(
						"SYNONYMSTR").toString();
				type = map.get("TYPE") == null ? "" : map.get("TYPE")
						.toString();
				wordAndsynonym = wordClassAndWordAndsynonyms.get(wordclass);
				if (wordAndsynonym.containsKey("WORD")) {
					wordAndsynonymAndType = wordAndsynonym.get("WORD");
					if (wordAndsynonymAndType.containsKey(word)) {
						if (!"".equals(synonym)) {
							wordAndsynonymAndType.get(word).put(synonym, type);
							wordAndsynonymAndType.get(word).put("WORDID",
									wordid);
							wordAndsynonym.put("WORD", wordAndsynonymAndType);
						}

					} else {
						if (!"".equals(synonym)) {
							synonymAndType = new HashMap<String, String>();
							synonymAndType.put(synonym, type);
							synonymAndType.put("WORDID", wordid);
							wordAndsynonymAndType.put(word, synonymAndType);
						} else {
							synonymAndType = new HashMap<String, String>();
							wordAndsynonymAndType.put(word, synonymAndType);
						}
						wordAndsynonym.put("WORD", wordAndsynonymAndType);
					}
				}

			} else {
				word = map.get("WORD") == null ? "" : map.get("WORD")
						.toString();
				wordid = map.get("WORDID") == null ? "" : map.get("WORDID")
						.toString();
				synonym = map.get("SYNONYMSTR") == null ? "" : map.get(
						"SYNONYMSTR").toString();
				if (!"".equals(word)) {
					if (!"".equals(synonym)) {

						type = map.get("TYPE") == null ? "" : map.get("TYPE")
								.toString();
						synonymAndType.put(synonym, type);
						synonymAndType.put("WORDID", wordid);
						wordAndsynonymAndType.put(word, synonymAndType);
					} else {
						wordAndsynonymAndType.put(word, synonymAndType);
					}
				}
				wordAndsynonym.put("WORD", wordAndsynonymAndType);
				// wordAndsynonymAndType = new HashMap<String, Map<String,
				// String>>();
				// synonymAndType = new HashMap<String, String>();
				// synonymAndType.put("WORDCLASSID2", wordclassid);
				// wordAndsynonymAndType.put("WORDCLASSID1", synonymAndType);
				// wordAndsynonym.put("WORDCLASSID", wordAndsynonymAndType);
				wordAndsynonymAndType = new HashMap<String, Map<String, String>>();
				synonymAndType = new HashMap<String, String>();
				synonymAndType.put("CONTAINER2", container);
				wordAndsynonymAndType.put("CONTAINER1", synonymAndType);
				wordAndsynonym.put("CONTAINER", wordAndsynonymAndType);

			}

			wordClassAndWordAndsynonyms.put(wordclass, wordAndsynonym);

		}

		return wordClassAndWordAndsynonyms;
	}

	
	
	
	/**
	 *@description 获得词类 词类ID字典
	 *@return 
	 *@returnType Map<String,String> 
	 */
	public static Map<String,String> getWordclassDic(){
		Map<String,String> map = new HashMap<String,String>();
		String sql ="select wordclassid,wordclass from wordclass";
		Result rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				map.put(rs.getRows()[i].get("wordclass").toString(),rs.getRows()[i].get("wordclassid").toString());
			}
		}
		return map;
		
	}
	
	public static Map<String,String> getServiceDic(String serviceRoot){
		Map<String,String> map = new HashMap<String,String>();
		String sql ="select  service,serviceid  FROM service start  with service = ?　connect by  nocycle prior serviceid = parentid ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定业务
		lstpara.add(serviceRoot);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		if (rs != null && rs.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				map.put(rs.getRows()[i].get("service").toString(),rs.getRows()[i].get("serviceid").toString());
			}
		}
		return map;
		
	}
	
	
	public static Map<String,String> getKbdataDic(String serviceRoot){
		Map<String,String> map = new HashMap<String,String>();
		String sql ="select k.topic,k.abstract,k.kbdataid from (select * FROM service start  with service = ?　connect by  nocycle prior serviceid = parentid) s,kbdata k where s.serviceid = k.serviceid ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要ID
		lstpara.add(serviceRoot);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		if (rs != null && rs.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				map.put(rs.getRows()[i].get("topic").toString()+"@@"+rs.getRows()[i].get("abstract").toString(),rs.getRows()[i].get("kbdataid").toString());
			}
		}
		return map;
		
	}
	
	/**
	 *@description 读文件
	 *@return 
	 *@returnType List<String> 
	 */
	public static List<String> readtxt(String txtName){
		List<String> list = new ArrayList<String>();
		String path;
		if(System.getProperty("os.name").toLowerCase().startsWith("win")){
			path="E:/app/ICSR4TEST/syndata/" +txtName;
		}else{
			path="/app/ICSR4TEST/syndata/"+txtName;
		}
	    list = readTxt(path,"GBK");
	    return list;
	}
	
	public static Map<String,List<String>> getAbstractAndWordpat(){
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		List<String> wordpatList = new ArrayList<String>();
		List<String> list = readtxt("cwordpat.txt");
		for(int i=0;i<list.size();i++){
			String str = list.get(i);
			String abs="";
			String tempabs = str.split("\t")[0];
			String realabs = "";
			if(tempabs.indexOf(">")!=-1){
			 realabs= tempabs.split(">")[1];
			 abs = replaceAllChar(realabs);
			 String wordpat = str.split("\t")[1];
			 if(map.containsKey(abs)){
				 List<String> tempList = map.get(abs);
				 tempList.add(wordpat);
				 map.put(abs, tempList);
			 }else{
				 wordpatList = new ArrayList<String>();
				 wordpatList.add(wordpat);
				 map.put(abs, wordpatList); 
			 }
		}
			
		}	
		logger.info("C#平台FAQ数量->"+ map.size());
		return map;
		
	}
	
	
	public static Map<String,Map<String,String>> getAbstractInfo(){
		Map<String,Map<String,String>> map = new HashMap<String,Map<String,String>>();
		Map<String,String> tempMap = new HashMap<String,String>();
		List<String> list = readtxt("javaabstract.txt");
		for(int i=0;i<list.size();i++){
			String str = list.get(i);
			String absid = str.split("\t")[1];
			String city = str.split("\t")[3];
			String abs="";
			String tempabs = str.split("\t")[2];
			if(tempabs.indexOf(">")!=-1){
			 String a= tempabs.split(">")[1];
			 abs = replaceAllChar(a);
			 tempMap = new HashMap<String,String>();
			 absAndCity.put(absid, city);
			 tempMap.put("abs", abs);
			 tempMap.put("city", city);
			 tempMap.put("realabs", a);
			 map.put(absid, tempMap);
		    }
		}
       logger.info("java平台FAQ数量->"+map.size());
		return map;
		
	}
	
	
	public static Map<String,List<String>> getInsertMap(Map<String,List<String>> abstractAndWordpat,Map<String,Map<String,String>>  abstractInfo){
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		List<String> notfindAbs = new ArrayList<String>();
		List<String> findAbs = new ArrayList<String>();
	    for (Map.Entry<String, Map<String, String>> entry : abstractInfo.entrySet()) {  
	      String absid = entry.getKey();
	      Map<String, String> tempmap = entry.getValue();
	      String abs = tempmap.get("abs");
	      String realabs = tempmap.get("realabs");
	      if(abstractAndWordpat.containsKey(abs)){
	    	  List<String> wordpatList = abstractAndWordpat.get(abs);
	    	  map.put(absid, wordpatList);
	    	  findAbs.add(realabs+"@@@@"+wordpatList);
	      }else{
	    	  notfindAbs.add(realabs);
	      }
	    }  
		writeIntxt("/app/ICSR4TEST/syndata/notfind.txt", notfindAbs, true, "UTF-8");
		writeIntxt("/app/ICSR4TEST/syndata/find.txt", findAbs, true, "UTF-8");
	    logger.info("java平台从C#平台找到FAQ数量->"+ map.size());
	    return map;
	}
	
	
	
	
	/**
	 *@description 新增词模
	 *@return 
	 *@returnType int 
	 */
	public static int insertWordPat(Map<String,List<String>> map) {

		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara ;
		// 定义多条SQL语句
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		sql = new StringBuilder();
		sql
		.append("insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,city,WORKERID) values(?,?,?,?,?,?,sysdate,?,?)");
		 String absid ="";
		 String city ="";
		for (Map.Entry<String,List<String>> entry : map.entrySet()) {
			absid = entry.getKey();
			city = absAndCity.get(absid).replace(",", "|");
			List<String> list = entry.getValue();
			Set set = new HashSet(list); 
			List<String> wordpatList = new ArrayList(set);
			for(int k =0;k<wordpatList.size();k++){
				String w = wordpatList.get(k);
				 // 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				String  wordpatid ="";
				if (GetConfigValue.isOracle) {
					// 获取词模表的序列值
					wordpatid = ConstructSerialNum
							.GetOracleNextValNew("SEQ_WORDPATTERN_ID","");
					// 定义新增模板的SQL语句
				} 
				// 绑定模板id参数
				lstpara.add(wordpatid);
				// 绑定模板参数
				lstpara.add(w);
				// 绑定自动开关参数
				lstpara.add(0);
				// 绑定模板类型参数
				lstpara.add(getWordpatType(w));
				// 绑定摘要id参数
				lstpara.add(absid);
				// 绑定品牌参数
				lstpara.add("安徽电信");
				if("".equals(city)){
					city ="全国";
				}
//				logger.info(city);
				lstpara.add(city);
				lstpara.add("179_test");
				// 将对应的绑定参数集合放入集合中
				lstLstpara.add(lstpara);
			}
		 }
		int c =-1;
		c= Database.executeNonQueryBatchTransaction(sql.toString(), lstLstpara);
		logger.info(c);
	    return c;
	
	}
	
	
	/**
	 *@description 新增词模
	 *@return 
	 *@returnType int 
	 */
	public static int insertAbstractAndWordPat( Map<String,Map<String,List<String>>> m,String bussinessFlag,String serviceRoot,String cityCode,String operationID) {
		// 定义绑定参数集合
		List<Object> lstpara ;
		// 定义多条SQL语句
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		String sql ="" ;
		String abs ="";
		String absid = "";
		 Map<String,String> servicetoserviceid = getServiceDic(serviceRoot);
		 Map<String,String> abstracttokbdataid = getKbdataDic(serviceRoot);
		 String service="";
		 Map<String,List<String>>  map =null;
		 for (Map.Entry<String,Map<String,List<String>>> e : m.entrySet()){
			 service = e.getKey();
			 String serviceid=null;
			 if(servicetoserviceid.containsKey(service)){
				 serviceid  = servicetoserviceid.get(service);
				  map =e.getValue(); 
			 }else{
				logger.info("未找到业务："+service);
				continue;
			 }
		 
		for (Map.Entry<String,List<String>> entry : map.entrySet()) {
			abs = entry.getKey();
			String topic = abs.split("@@")[0];
			String _abs = abs.split("@@")[1];
			if(abstracttokbdataid.containsKey(abs)){//如已存在摘要则取已有摘要ID,反之取序列，插入新摘要
				absid = abstracttokbdataid.get(abs);
				logger.info("已存在摘要："+abs);
				logger.info("获取摘要ID:"+absid );
			}else{
				if (GetConfigValue.isOracle) {
					// 获取词模表的序列值
					absid = ConstructSerialNum
							.GetOracleNextValNew("SEQ_KBDATA_ID",bussinessFlag);
					logger.info("===================================================");
					logger.info("不存在摘要:"+abs);
					logger.info("获取摘要序列ID:"+absid);
				} 
				
				// 定义新增模板的SQL语句
				sql = "insert into kbdata(serviceid,kbdataid,topic,abstract,CITY) values (?,?,?,?,?)";
				// 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				// 绑定业务id参数
				lstpara.add(serviceid);
				// 获取摘要表的序列值，并绑定参数
				lstpara.add(absid);
				// 绑定主题参数
				lstpara.add(topic);
				// 绑定摘要参数
				lstpara.add(_abs);
				// 绑定地地市编码参数
				lstpara.add(cityCode);
				// 将SQL语句放入集合中
				lstSql.add(sql.toString());
				// 将定义的绑定参数集合放入集合中
				lstLstpara.add(lstpara);
			}
		
			
		//插入词模
			List<String> list = entry.getValue();
			Set set = new HashSet(list); 
			List<String> wordpatList = new ArrayList(set);
			sql = "insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,city,WORKERID) values(?,?,?,?,?,?,sysdate,?,?)";
			for(int k =0;k<wordpatList.size();k++){
				String w = wordpatList.get(k);
				 // 定义绑定参数集合
				lstpara = new ArrayList<Object>();
				String  wordpatid ="";
				if (GetConfigValue.isOracle) {
					// 获取词模表的序列值
					wordpatid = ConstructSerialNum
							.GetOracleNextValNew("SEQ_WORDPATTERN_ID",bussinessFlag);
					logger.info("获取词模序列ID:"+wordpatid);
				} 
				// 绑定模板id参数
				lstpara.add(wordpatid);
				// 绑定模板参数
				lstpara.add(w);
				// 绑定自动开关参数
				lstpara.add(0);
				// 绑定模板类型参数
				lstpara.add(getWordpatType(w));
				// 绑定摘要id参数
				lstpara.add(absid);
				// 绑定品牌参数
				lstpara.add(serviceRoot);
				String wcity = cityCode.replace(",", "|");
				lstpara.add(wcity);
				lstpara.add(operationID);
				// 将对应的绑定参数集合放入集合中
				lstSql.add(sql.toString());
				lstLstpara.add(lstpara);
			}
		 }
		 }
		 logger.info("开始同步词模......");
		int c =-1;
		c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		logger.info("同步词模数："+c);
	    return c;
	
	}
	
	
	
	public  static String getWordpatType(String wordpat){
		String wordpatType="";
		String wordpatTypeName="";
		if (wordpat.indexOf("-") != -1 && wordpat.indexOf("*")==-1) {
			wordpatType="1";
			wordpatTypeName = "等于词模";
		} else if (wordpat.startsWith("~")) {
			wordpatType="2";
			wordpatTypeName = "排除词模";
		} else if (wordpat.startsWith("++")) {
			wordpatType="3";
			wordpatTypeName = "选择词模";
		} else if ((wordpat.startsWith("+") && !wordpat
				.startsWith("++"))
				) {
			wordpatType="4";
			wordpatTypeName = "特征词模";
		} else {
			wordpatType ="0";
		}
		return wordpatType;
	}
	
	
	
	
	public static boolean writeIntxt(String path, List<String> list,
			boolean append,String encoding) {
//		FileWriter fw = null;
//		try {
//			File myFilePath = new File(path);
//			if (myFilePath.exists()) {
//				myFilePath.delete();
//				myFilePath.createNewFile();
//			} else {
//				myFilePath.createNewFile();
//			}
//			// 第二个参数 append 说明文件是重新新建或可扩充
//			fw = new FileWriter(myFilePath, append);
//			for (int i = 0; i < list.size(); i++) {
//				String line = list.get(i);
//				fw.write(line + "\n");
//			}
//			fw.close();
//		} catch (Exception e) {
//			logger.error("写文件失败【"+path+"】",e);
//			return false;
//		}
		   try {   
				File myFilePath = new File(path);
				if (myFilePath.exists()) {
					myFilePath.delete();
					myFilePath.createNewFile();
				
				} else {
					myFilePath.createNewFile();
				}  
		        OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(myFilePath,append),encoding);
		       // OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(myFilePath,append),"UTF-8"); 
		        BufferedWriter writer=new BufferedWriter(write);   
		        for (int i = 0; i < list.size(); i++) {
					String line = list.get(i);
					 writer.write(line + "\n");  
				}
		        writer.close();   
		    } catch (Exception e) {  
		    	logger.info(e);
				return false;  
		    }  
		return true;
	}
	
	
	public static String replaceAllChar(String str){
		str = str.replaceAll("[\\[|\\]|!|<|+|,|，|？|?|'|“|”|【|】|、|-|。|.|（|）|(|)]{1}", "");
		str = str.replace("","");
		logger.info(str);
		return str;
	}
	
	
   public static String readQuestion(){
	   List<String> list = readtxt("question.txt");
	   List<String> newlist = new ArrayList<String>();
	   String head ="业务\t主题\t摘要\t相关问题\t对应业务\t对应主题\t对应摘要\t对应答案";
	   newlist.add(head);
	   String s="";
	   String t="";
	   String b="";
	   String question ="";
	   for(int i=0;i<list.size();i++){
		   String line ="";
		   s =  list.get(i).split("\t")[0];
		   t =  list.get(i).split("\t")[1];
		   b =  list.get(i).split("\t")[2];
		   question =  list.get(i).split("\t")[3];
		   String sql ="select distinct s.service,k.topic,k.abstract,t.answer from service s inner join (select * from kbdata where abstract like '%"+question+"') k on s.serviceid = k.serviceid  left join t_faq t on  k.kbdataid = t.kbdataid ";
//		   sql="select  s.service,s.brand topic,s.serviceid abstract ,s.brand answer from service s where s.brand='电信指令业务'";
		   Result rs = Database.executeQuery(sql);
			if (rs != null && rs.getRowCount() > 0) {
				// 获取回复模板数据
				String service = rs.getRows()[0].get("service") != null ? rs
						.getRows()[0].get("service").toString()
						: "空";
				String topic = rs.getRows()[0].get("topic") != null ? rs
								.getRows()[0].get("topic").toString()
								: "空";	
				String _abstract = rs.getRows()[0].get("abstract") != null ? rs
										.getRows()[0].get("abstract").toString()
										: "空";	
				String answer = rs.getRows()[0].get("answer") != null ? rs
										.getRows()[0].get("answer").toString()
										: "空";	
			line=s+"\t"+t+"\t"+b+"\t"+question+"\t"+service+"\t"+topic+"\t"+_abstract+"\t"+answer;	
			newlist.add(line);
			}else{
			line=s+"\t"+t+"\t"+b+"\t"+question+"\t空\t空\t空\t空";
			newlist.add(line);
			}
			logger.info(line);
	   }
	   writeIntxt("D:/app/ICSR4TEST/syndata/relatedquestion.txt", newlist, true, "UTF-8");
	   return "";
   }
   
   
   public static Map<String,Map<String,List<String>>> getServiceAbsWordpatDic(){
    Map<String,Map<String,List<String>>>  map =  new HashMap<String, Map<String,List<String>>>();
    Map<String,List<String>>  tempMap = new HashMap<String,List<String>>();
    List<String> list = readtxt("wordpat_new.txt");
    logger.info("读取文件行数："+list.size());
    List<String> tl;
    String line [];
    String service ="";
    String topic="";
    String abs ="";
    String wordpat="";
    for(int i=0;i<list.size();i++){
    	line = list.get(i).split("\t");
    	service=line[0];
    	topic = line[1];
    	abs = line[2];
    	String ta = topic+"@@"+abs;
    	wordpat = line[3];
    	if(map.containsKey(service)){
    		 Map<String,List<String>>  m  = map.get(service);
    		 if(m.containsKey(ta)){
    			List<String> l =  m.get(ta);
    			l.add(wordpat);
    			 m.put(ta, l);
    		 }else{
    			  tl =  new ArrayList<String>();
    			 tl.add(wordpat); 
    			 m.put(ta, tl); 
    		 }
    		 map.put(service, m);
    	}else{
    		tempMap = new HashMap<String,List<String>>();
    		tl =  new ArrayList<String>();
    		tl.add(wordpat);
    		tempMap.put(ta, tl);
    		map.put(service,tempMap);
    	}
    }
    
   //logger.info(map);
    return map;
    
   }
	
   
	 /**
	  * 同步答案
	  * */
	 public static void  syncAnswer(String bussinessFlag,String bussiness ,String serviceRoot,String operationID){
		     String configPath="E:/app/ICSR4TEST/syndata/ygan.txt";
//		     String configPath="/app/ICSR4TEST/syndata/answer_new.txt";
		     logger.info("读取文件路径："+configPath);
			 BufferedReader reader =null;
			 try {
//				reader = new BufferedReader(new FileReader(configPath));
				reader = new BufferedReader(new InputStreamReader(
						new FileInputStream(new File(configPath)),"GBK"));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			 String tempString = null;
			 List<Map<String, String>> list = new ArrayList<Map<String,
			 String>>();
			 Map<String, String> map ;
			 Map<String,String> absAndId = getAbstractAndIdDic(serviceRoot);
			 try {
				while ((tempString = reader.readLine()) != null) {
				logger.info("行数据："+tempString);
				 if("".equals(tempString)){
				 continue;
				 }
				 map = new HashMap<String,String>();
				 String array[] = tempString.split("\t");
				 String abs = array[0];
				 String absid = null ;
				 if(absAndId.containsKey(abs)){
					 absid =  absAndId.get(abs);
					 map.put("kbdataid",absid);
					 map.put("channel","IVR");
//					 map.put("channel",array[1]);
					 map.put("answer","SET(\"code\",\"802080\");SET(\"TTS\",\"" + array[1] + "\");SET(\"是否末梢编码\",\"否\");###;");
					 logger.info("生成插入元素："+map);
					 list.add(map);
				 }
				 }
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info(list.toString());
			logger.info("需同步的答案集合数："+list.size());
			 insert(list,bussinessFlag,bussiness,serviceRoot,operationID);
		 
		 
	 }
	 
		/**
		 *@description  插入答案
		 *@param list 
		 *@returnType void 
		 */
		private static void insert(List<Map<String, String>> list,String bussinessFlag,String bussiness ,String serviceRoot,String operationID) {
			// 定义多条SQL语句结果
			List<String> listSql = new ArrayList<String>();
			// 定义多条SQL语句对应的绑定参数集合
			List<List<?>> listParam = new ArrayList<List<?>>();
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			String sql;
			for (int i = 0; i < list.size(); i++) {

				Map<String, String> map = list.get(i);

//				String kbansvaliddateid = String.valueOf(SeqDAO
//						.GetNextVal("KBANSVALIDDATE_SEQ"));
				
				String kbansvaliddateid = ConstructSerialNum
				.GetOracleNextValNew("KBANSVALIDDATE_SEQ",bussinessFlag);
				// 判断开始时间、结束时间不为空、null

				// 插入kbansvaliddate的SQL语句
				sql = "insert into kbansvaliddate(KBANSVALIDDATEID,KBDATAID,BEGINTIME,ENDTIME ) values(?,?,null,null)";
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定kbansvaliddateid参数
				lstpara.add(kbansvaliddateid);
				// 绑定摘要id参数
				lstpara.add(map.get("kbdataid"));
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);

				// 插入kbanspak表
				// 获取kbanspak的序列
//				String kbanspakid = String.valueOf(SeqDAO
//						.GetNextVal("KBANSPAK_SEQ"));
				
				String kbanspakid = ConstructSerialNum
				.GetOracleNextValNew("KBANSPAK_SEQ",bussinessFlag);
				// 插入kbanspak的SQL语句
				sql = "insert into kbanspak(KBANSPAKID,KBANSVALIDDATEID,PACKAGE,PACKAGECODE,PAKTYPE) values(?,?,?,?,?)";
				// 对应绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定kbanspakid参数
				lstpara.add(kbanspakid);
				// 绑定kbansvaliddateid参数
				lstpara.add(kbansvaliddateid);
				// 绑定package参数
				lstpara.add("空号码包");
				// 绑定packagecode参数
				lstpara.add(null);
				// 绑定paktype参数
				lstpara.add("0");
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);

				// 插入kbansqryins表
				// 获取kbansqryins的序列
//				String kbansqryinsid = String.valueOf(SeqDAO
//						.GetNextVal("KBANSQRYINS_SEQ"));
				
				String kbansqryinsid = ConstructSerialNum
				.GetOracleNextValNew("KBANSQRYINS_SEQ",bussinessFlag);
				
				// 插入kbansqryins的SQL语句
				sql = "insert into kbansqryins(KBANSQRYINSID,KBANSPAKID,QRYINS) values(?,?,?)";
				// 对应绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定kbansqryinsid参数
				lstpara.add(kbansqryinsid);
				// 绑定kbanspakid参数
				lstpara.add(kbanspakid);
				// 绑定qryins参数
				lstpara.add("查询指令无关");
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);

				// 获取kbcontent的序列
//				String kbcontentid = String.valueOf(SeqDAO
//						.GetNextVal("SEQ_KBCONTENT_ID"));
				
				String kbcontentid = ConstructSerialNum
				.GetOracleNextValNew("SEQ_KBCONTENT_ID",bussinessFlag);
				// 插入kbcontent的SQL语句
				sql = "insert into kbcontent(KBCONTENTID,KBANSQRYINSID,CHANNEL,ANSWERCATEGORY,SERVICETYPE,CUSTOMERTYPE) values(?,?,?,0,?,?)";
				// 对应绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定kbcontentid参数
				lstpara.add(kbcontentid);
				// 绑定kbansqryinsid参数
				lstpara.add(kbansqryinsid);
				// 绑定渠道参数
				lstpara.add(map.get("channel"));
				// 绑定servicetype参数
				//lstpara.add("证券行业->国信证券->多渠道应用");
//				lstpara.add("政府行业->深圳国税->多渠道应用");
				//lstpara.add("卫生行业->所有组织->医疗应用");
				//lstpara.add("电信行业->上海电信->多渠道应用");
//				lstpara.add("基金行业->汇添富基金->多渠道应用");
//				lstpara.add("基金行业->南方基金->多渠道应用");
//				lstpara.add("教育行业->多渠道应用->多渠道应用");
				//lstpara.add("教育行业->韦博教育->多渠道应用");
//				lstpara.add("电信行业->安徽电信->多渠道应用");
				//lstpara.add("基金行业->华夏基金->多渠道应用");
				lstpara.add(bussiness);
				// 绑定customertype参数
				lstpara.add("普通客户");
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);

				// 插入kbanswer表
				// 过去kbanswer的序列
//				String kbanswerid = String.valueOf(SeqDAO
//						.GetNextVal("KBANSWER_SEQ"));
				
				String kbanswerid = ConstructSerialNum
				.GetOracleNextValNew("KBANSWER_SEQ",bussinessFlag);
				
				// 插入kbanswer的SQL语句
				sql = "insert into kbanswer(kbanswerid,kbcontentid,answercontent,servicehallstatus,city,customertype,brand) values(?,?,?,?,?,?,?)";
				// 对应绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定kbanswerid参数
				lstpara.add(kbanswerid);
				// 绑定kbcontentid参数
				lstpara.add(kbcontentid);
				// 绑定答案参数
				lstpara.add(map.get("answer"));
				// 绑定servicehallstatus参数
				lstpara.add("无关");
				// 绑定城市参数
				lstpara.add("上海");
				// 绑定customertype参数
				lstpara.add(operationID);
				// 绑定品牌参数
				lstpara.add(serviceRoot);
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);
			}
			// 执行SQL语句，绑定事务处理，并返回事务处理的结果
			int c =0;
			c = Database.executeNonQueryTransaction(listSql, listParam);
			System.out.print("同步答案数："+c);
		}
		
	public static   Map<String,String> getAbstractAndIdDic(String serviceRoot){
		Map<String, String> map = new  HashMap<String,String>();
		String sql = "select distinct k.abstract,k.kbdataid from service s ,kbdata k where s.serviceid=k.serviceid and s.serviceid in( SELECT serviceid  FROM service 　　start  WITH serviceid in (select serviceid from service where service='"+serviceRoot+"')　connect BY nocycle prior serviceid = parentid)";
		logger.info("getAbstractAndIdDic_sql:"+sql);
		Result rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				String _abstract = rs.getRows()[i].get("abstract").toString();
				String kbdataid = rs.getRows()[i].get("kbdataid").toString();
				map.put(_abstract, kbdataid);
			}
		}
		logger.info("getAbstractAndIdDic_map:"+map);
		return map;
		
	}
	
	public static   Map<String,String> getWordidandPwd(){
		Map<String, String> map = new  HashMap<String,String>();
		String sql = "select workerid,pwd from member ";
		Result rs = Database.executeQuery(sql);
		if (rs != null && rs.getRowCount() > 0) {
			for (int i = 0; i < rs.getRowCount(); i++) {
				String workerid = rs.getRows()[i].get("workerid").toString();
				String pwd = rs.getRows()[i].get("pwd").toString();
				String pwdn = StringUtil.EncryptMD5(pwd+workerid);
				map.put(workerid, pwdn);
			}
		}
		return map;
	}
	
	public static int updatepwd(Map<String,String> map ){
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		String sql ="update  member set pwdn=? where workerid =?";
        for (Map.Entry<String, String> m : map
							.entrySet()) {
        	          lstpara = new ArrayList<Object>();
						
						// 绑定词条id参数
						lstpara.add(m.getValue());
						lstpara.add(m.getKey());
						// 将SQL语句放入集合中
						lstSql.add(sql);
						// 将对应的绑定参数集合放入集合中
						lstLstpara.add(lstpara);
					}
        
        int c = Database.executeNonQueryTransaction(lstSql, lstLstpara);
    	logger.info("更新条数："+c);
        return c;	
    		
	   }
	
	
	
	public static List<String> getKbdataID(String service){
		Map<String,String> map = new HashMap<String,String>();
		String sql ="select k.topic,k.abstract,k.kbdataid from (select * FROM service start  with service = ?　connect by  nocycle prior serviceid = parentid) s,kbdata k where s.serviceid = k.serviceid ";
		List<String> kbdataids = new ArrayList<String>();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要ID
		lstpara.add(service);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		if (rs != null && rs.getRowCount() > 0) {
			// 循环遍历数据源
			for (int i = 0; i < rs.getRowCount(); i++) {
				kbdataids.add(rs.getRows()[i].get("kbdataid").toString());
			}
		}
		return kbdataids;
		
	}






 public static int InsertAttrName(List<String> kbdataid, String columnNames[],String serviceType) {
		// 定义多条SQL语句集合
		List<String> lstSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> lstLstpara = new ArrayList<List<?>>();
		// 定义SQL语句
		String sql = "";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		
		
		for(int l = 0; l < kbdataid.size(); l++){
		int column =0;
		for (int i = 0; i < columnNames.length; i++) {
			column++;
			lstpara = new ArrayList<Object>();
			// 定义新增属性名称的SQL语句
			sql = "insert into serviceattrname2colnum (serviceattrname2colnumid,name,columnnum,abstractid) values (?,?,?,?)";
			// 定义绑定参数集合
			lstpara = new ArrayList<Object>();
			String serviceattrname2colnumid = "";
//			String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
			String bussinessFlag = "4";
			if(GetConfigValue.isOracle){
				serviceattrname2colnumid = ConstructSerialNum.GetOracleNextValNew("serviceattrname2colnum_seq",bussinessFlag);	
			}else if(GetConfigValue.isMySQL){
				serviceattrname2colnumid = ConstructSerialNum.getSerialIDNew("serviceattrname2colnum","serviceattrname2colnumid",bussinessFlag);	
			}
			lstpara.add(serviceattrname2colnumid);
			// 绑定属性名称参数
			lstpara.add(columnNames[i]);
			// 绑定列值参数
			lstpara.add(column);
			// 绑定业务id参数
			lstpara.add(kbdataid.get(l));
			// 将SQL语句放入集合中
			lstSql.add(sql.toString());
			// 将定义的绑定参数集合放入集合中
			lstLstpara.add(lstpara);
			
		}
		}
		// 执行SQL语句，绑定事务，返回事务处理结果
		int result = Database.executeNonQueryTransaction(lstSql, lstLstpara);
		return result;
	}

	

	
	
}
