package com.sequence;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReplaceSequence {

	public static String mysqlStr(String OralceStr){
		String returnStr = OralceStr;//="insert into patternkey values (seq_patternkey_id.nextval,?)";
//		StringBuffer sb = new StringBuffer();
//		select AA.nextval from dual
		if(OralceStr.indexOf("dual") != -1){//有dual，就是一个单独的sql
			String regex = "(select[\\s]+([\\S]+).nextval).*?dual";
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(OralceStr);
			while(m.find()){
//				returnStr = returnStr.replace(m.group(2), "A");
				String getOracleSql = m.group();
				System.out.println(OralceStr+"获取到的oracleSql："+getOracleSql);
				String getSeqName = m.group(2);
				System.out.println(OralceStr+"序列名："+getSeqName);
				returnStr = returnStr.replace(m.group(1), "SELECT ghj_func_nextval('"+getSeqName+"') ");
			}
		}
		else{
			String regex = "\\(\\s*(([\\S]+).nextval\\s*)";
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(OralceStr);
			while(m.find()){
//				String getOracleSql = m.group(1);
//				System.out.println(OralceStr+"获取到的oracleSql："+getOracleSql);
				String getSeqName = m.group(2);
				System.out.println(OralceStr+"序列名："+getSeqName);
				returnStr = returnStr.replace(m.group(1), " ( SELECT ghj_func_nextval('"+getSeqName+"') from dual ) ");
			}
		}
		
		
		return returnStr;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String str = "Selectselect  bb.nextVal cc.nextVal from dual dual";
		String str1 = "insert into table(fds,fds,f) values(select k.nextval from dual,1,4)";
		String phase = mysqlStr(str1);
		System.out.println(phase);
	}

}
