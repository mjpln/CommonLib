package com.str;
/**
 * 
 */

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 方法名称：
 * 
 * @author weibin
 * @param 
 * @return 
 * @throws 
 */

public class TwoClassTrans {

	

	public static int subStrCount(String str,String subStr){  
        int count=0;  
        for(int i=0;i<str.length();i++){  
            for(int j=0;j<i;j++){  
                if(str.substring(j, i).equals(subStr)){  
                    count++;  
                }  
            }  
        }  
        return count;  
    }  
	
	public static String transRownum(String oSql){
        
	    
		
		
		
		oSql=oSql+" ";
		String mSql = "";
		
		int selectTimes = subStrCount(oSql,"select"); 
		
		System.out.println(selectTimes);
		if(selectTimes>=2){ //多层嵌套
		     
			int first = oSql.indexOf("(");
			int last=oSql.lastIndexOf(")");
			String innerSql = oSql.substring(first+1, last);
			//String lastSql = oSql.substring(last, oSql.length());//		
			int start = -1;
			int end = -1;			
			
			
			
			
			if(oSql.contains(">")){ //包含大于号,start				
				String rnStart = oSql.substring(oSql.indexOf("rn>"),oSql.indexOf(" ", oSql.indexOf("rn>")));
			    if(rnStart.contains("=")){
			    	start= Integer.valueOf(rnStart.substring(rnStart.indexOf(">=")+2));
			    }
			    else{
			    	start= Integer.valueOf(rnStart.substring(rnStart.indexOf(">")+1));
			    }
			}
			// 暂时只考虑 
			if(oSql.contains("<")){ //包含小于号,end
				String rnStart = oSql.substring(oSql.indexOf("rn<"),oSql.indexOf(" ", oSql.indexOf("rn<")));
			    if(rnStart.contains("=")){
			    	end= Integer.valueOf(rnStart.substring(rnStart.indexOf("<=")+2));
			    }
			    else{
			    	end = Integer.valueOf(rnStart.substring(rnStart.indexOf("<")+1));
			    }
			}			
		   
		   /* Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
	        Matcher m = p.matcher(input);  
	        String result = m.replaceAll(replacement);  
	        return result; */
			//Pattern p = Pattern.compile();// 正则表达式
			Matcher m = Pattern.compile(" *, *rownum *rn").matcher(innerSql);
			innerSql= m.replaceAll(""); 					
			System.out.println(innerSql);			
			mSql= "select t2.* from ( "+innerSql+" )t2 limit "+ start + " , "+ (end-start-1);		    
			System.out.println(mSql);
		}
			return mSql;
		
	}
	// 转换date格式
	/*public static String transDate(String oSql){
		
		String os = oSql;
		
		os = os.   ;
		
		
		
		
			
		return "  ";
	}*/
	// 转换字符串处理
	public static String transFunction(String  oSql){
		
		
		return "";
	}   

	
	public static void main(String[] args) {
		
		// 首先转换rownum，前提规范拼写(写一个sql 规范)-----写作规范化是个很大的问题，主要是字符串的提取替换等等
		
		// 规范1, rownum 必须是最外层，仅仅是用来分页的
		// 规范2，需要空格时，必须只有一个空格 （这个以后可以用函数来实现），运算符号不需要空格。
		
		// 规范3，分页只能写在最后，数字写在运算符后面，
		
		
		
		String oSql = "select * from (select t.*      ,     rownum rn from (sssss dfdfa fasfda)t) where rn >    9 and rn<10";
		
		String oSql1="select * from (select t.*,rownum rn from (select q.*,(select wordclass from wordclass where wordclassid=q.wordclassid) wordclass from scenarioselement q where q.relationserviceid=? order by q.weight asc ) t ) where rn>10 and rn<=15";
		System.out.println(transRownum(oSql1));		
		
		
		
		
		//找到内层sql
					
		// 再次转换时间的
		
		
		//最后转换特殊函数
		
		
	}
	
}

