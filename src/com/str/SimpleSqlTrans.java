package com.str;
/**
 * 
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class SimpleSqlTrans {
	
	/**
	 * 方法名称：字符串替换，不考虑大小写
	
    */
	 public static String repNoFormat(String input, String regex, String replacement) {  
	        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
	        Matcher m = p.matcher(input);  
	        String result = m.replaceAll(replacement);  
	        return result;  
	    }  
	/**
		 * 方法名称：简单sql的转换
		 */
    public static String transformSql(String oracleSql){
    	
    	//首先转化Date类
    	
    	 String oSql = oracleSql;    	
    	 oSql=repNoFormat(oSql, "sysdate", "sysdate()");
    	  	
    	 return "  ";
    }
	
	
	public static void main(String[] args) {
		  String input = "I like Java,jAva is Very easy and jaVa jAvA is so popular.";  
	        String regex = "java";  
	        String replacement = "cccc";  
	  
	        input = repNoFormat(input, regex, replacement);  
	        System.out.println(input);  
	        System.out.println(input);  
	}
}


