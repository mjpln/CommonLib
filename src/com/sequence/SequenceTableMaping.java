package com.sequence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

//import javax.transaction.Synchronization;

import com.sun.org.apache.bcel.internal.generic.NEW;

//import org.apache.log4j.PropertyConfigurator;

/**
 * 序号关联的table和对应的列名columnName
 * @author ghj
 *
 */
public class SequenceTableMaping {
	
	public static HashMap<String, HashMap<String, String>> tableSequenceColumnMap = new HashMap<String, HashMap<String,String>>();
	public static HashMap<String, String> sequenceTableMap = new HashMap<String, String>();
	
	public static SequenceTableMaping seqTabMap;//单例对象

	
	private String pathName = System.getProperty("user.dir") + "/SequenceTableColumnMap.txt";
	
	private SequenceTableMaping(){
		 //防止通过 new SingletonTest()去实例化
		readfile(pathName);
	}
	
	public static SequenceTableMaping getInstance(){
		if(seqTabMap == null){
			synchronized (SequenceTableMaping.class){
				seqTabMap = new SequenceTableMaping();
			}
		}
		return seqTabMap;
	}
	/*
	 * txt中，序号，表名，列名
	 */
	private void readfile(String pathName){
		try{
			String encoding="UTF-8";//time.txt编码格式，个人写入
	        File file=new File(pathName);//跳板机
	        if(file.isFile() && file.exists()){ //判断文件是否存在
	           InputStreamReader read = new InputStreamReader(
	            new FileInputStream(file),encoding);//考虑到编码格式
	            BufferedReader bufferedReader = new BufferedReader(read);
	            String lineTxt = null;       
	            while((lineTxt = bufferedReader.readLine()) != null){
	            	String[] param = lineTxt.split("	");
	            	String seq_name = param[0]==null?"":param[0];
	            	String table_name = param[1]==null?"":param[1];
	            	String column_name = param[2]==null?"":param[2];
	            	putTabSeqColIntoMap(table_name, seq_name, column_name);
	            	putSeqTabIntoMap(seq_name, table_name);      	
	            }
	            bufferedReader.close();
	            read.close();
	        }
		}catch(Exception e){
//			log.error(e.getMessage(), e);
		}
		
	}
	
	public void putTabSeqColIntoMap(String table, String seq, String col){
		if(table == null || "".equals(table.trim())){
//			log.info("table == null|\"\"");
			return;
		}
		if(seq == null || "".equals(seq.trim())){
//			log.info("seq == null|\"\"");
			return;
		}
		if(col == null || "".equals(col.trim())){
//			log.info("col == null|\"\"");
			return;
		}
		
		if(tableSequenceColumnMap==null)
			tableSequenceColumnMap = new HashMap<String, HashMap<String,String>>();
		HashMap<String, String> seqColMap ;
		if(tableSequenceColumnMap.containsKey(table)){
			seqColMap = tableSequenceColumnMap.get(table);
			if(seqColMap.containsKey(seq)){
//				log.info("tableSequenceColumnMap初始化异常，同一张表下冲突的sequence："+seq+"，已经跳过");
			}
			else{
				seqColMap.put(seq, col);
			}
		}
		else{
			seqColMap = new HashMap<String, String>();
			seqColMap.put(seq, col);
			tableSequenceColumnMap.put(table, seqColMap);
		}
	}
	
	public void putSeqTabIntoMap(String seq, String table){
		if(table == null || "".equals(table.trim())){
//			log.info("table == null|\"\"");
			return;
		}
		if(seq == null || "".equals(seq.trim())){
//			log.info("seq == null|\"\"");
			return;
		}
		if(sequenceTableMap == null){
			sequenceTableMap = new HashMap<String, String>();
		}
		if(sequenceTableMap.containsKey(seq)){
//			log.info("sequenceTableMap出现重复的seq:"+seq+"，已跳过");
		}
		else{
			sequenceTableMap.put(seq, table);
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
		SequenceTableMaping r = new SequenceTableMaping();
		System.out.println(sequenceTableMap.get("test_seq"));
		System.out.println(tableSequenceColumnMap.get("test_table"));
	}

}
