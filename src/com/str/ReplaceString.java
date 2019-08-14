package com.str;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import oracle.net.aso.r;

import com.knowology.GlobalValue;
import com.treeSelect.Tree;

public class ReplaceString {

	/**
	 * mysql : DATE_FORMAT(NOW(),'%Y-%m-%d  %H:%i:%s')
		oracle:  TO_CHAR(SYSDATE,'YYYY-MM-DD hh24:mi:ss')
	 * @param sql
	 * @return
	 */
	public static String dateToChar(String sql){
		//"select channel,to_char(starttime,'yyyy-mm-dd') savetime,count(id) cn from queryhistorylog where starttime>=to_date(?,'yyyy-mm-dd hh24:mi:ss') and starttime<=to_date(?,'yyyy-mm-dd hh24:mi:ss') and channel in (?,?,?) group by channel,to_char(starttime,'yyyy-mm-dd') order by channel"
		String returnSql = sql;
		int index = sql.indexOf("yyyy");
		if(index != -1){
			String afteryyyy = sql.substring(index+4, index+5);
			String regex = "TO_CHAR\\s*\\(\\s*([^,]+)\\s*,\\s*'(YYYY-MM-DD\\s*hh24:mi:ss|YYYY-MM-DD)'\\s*\\)";
			regex = regex.replace("-", afteryyyy);
			System.out.println("charToDate zhengze:"+regex);
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
	        Matcher m = p.matcher(sql);  
	        while(m.find()){
	        	String getOracleSql = m.group();
				System.out.println("获取到的dataToChar："+getOracleSql);
				String date = m.group(1);
				date = date.toLowerCase();
//				if(date.indexOf("sysdate") != -1)////////////////////-----------------sysdate--->now()
//					date = date.replace("sysdate", "NOW()");
				System.out.println("date："+date);
				String ymd = m.group(2),ymd_rep = ",'%Y-%m-%d  %H:%i:%s')";
				if(ymd.indexOf("hh24") == -1)
					ymd_rep = ",'%Y-%m-%d')";
				returnSql = returnSql.replace(m.group(), "DATE_FORMAT("+date+ymd_rep);
	        }
		}
		else if(returnSql.matches("^(?i)(.*)TO_CHAR\\s*\\(\\s*([a-zA-Z0-9\\.]+)\\s*\\)(.*)$")){//转字符串0
			returnSql = returnSql.replaceAll("(?i)TO_CHAR\\s*\\(\\s*([a-zA-Z0-9\\.]+)\\s*\\)", "concat($1)");
		}
		else{//to_char(XX,'格式')数值型
			String regex = "TO_CHAR\\s*\\(\\s*([^,]+)\\s*,\\s*'(??????)'\\s*\\)";
			System.out.println("未开发");
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
	        Matcher m = p.matcher(sql);  
	        while(m.find()){
	        	String getOracleSql = m.group();
	        	System.out.println("不支持");
	        }
		}
		return returnSql;
		
	}
	
	/**
	 * TO_NUMBER('123') CAST("123" AS SIGNED INTEGER)
	 * 方法一：SELECT CAST('123' AS SIGNED); 方法二：SELECT CONVERT('123',SIGNED); 方法三：SELECT '123'+0;
	 * @param sql
	 * @return
	 */
	public static String StringToNumber(String sql) {
		String returnSql = sql;
		String regex = "TO_NUMBER\\s*\\(\\s*([^\\)]+)\\s*)";//
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
        Matcher m = p.matcher(sql);  
        while(m.find()){
        	String getOracleSql = m.group();
        	String key1 = m.group(1);//真实值 还是列名？
        	String key2 = m.group(2);//decimal还是int?
        	System.out.println("不支持 !!!!");
//        	if(key1.indexOf('.') != -1){
//        		returnSql = returnSql.replace(m.group(), "convert("+key1+","ymd_rep);
//        	}
        }
		return returnSql;
	}
	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public static String charToDate(String sql){
		String returnSql = sql;
		int index = sql.indexOf("yyyy");
		String afteryyyy = sql.substring(index+4, index+5);
		String regex = "TO_Date\\s*\\(\\s*([^,]+)\\s*,\\s*'YYYY-MM-DD hh24:mi:ss'\\s*\\)";
		regex = regex.replace("-", afteryyyy);
		System.out.println("charToDate zhengze:"+regex);
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
        Matcher m = p.matcher(sql);  
        while(m.find()){
        	String getOracleSql = m.group();
			System.out.println("获取到的CharTodate："+getOracleSql);
			String date = m.group(1);
//			date = date.toLowerCase();
			System.out.println("date："+date);
			returnSql = returnSql.replace(m.group(), "str_to_date("+date+",'%Y-%m-%d  %H:%i:%s')");
        }
		return returnSql;
		
	}
	/**
	 * oracle  'fds'||'hf'||'O'==>concat('fds''hf''O'),每个独立的部分：‘字符串’ ,?，函数,列名
	 * @param sql
	 * @return
	 */
	public static String ToConcat(String sql){
//		sql = "select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey like '%'||?||'%' )  where r >? and r <=?";
//		sql = "select count(*) from operationlog where 1>0  and time>=to_date(?||' 00:00:00','yyyy/mm/dd hh24:mi:ss')";
		sql = sql.replaceAll("(?i)like\\s+'%'\\s*\\|\\|\\s*\\?\\s*\\|\\|\\s*'%'\\s*", "regexp \\? ");
		System.out.println("like'%'||?||'%'过滤后sql="+sql);
		sql = sql.replaceAll("(?i)like\\s+'%+'\\s*", "is not null");
		System.out.println("like'%'过滤后sql="+sql);
		String returnSql = sql;
//		String regex = "TO_CHAR\\s*\\(\\s*([^,]+)\\s*,\\s*'YYYY-MM-DD hh24:mi:ss'\\s*\\)";
		String regex = "\\(?((\\s*'.*?'\\s*|\\s*\\?\\s*|\\s*[a-zA-Z0-9]+\\s*|\\s*[a-zA-Z]+\\(.*?\\)\\s*)\\|\\|(.*))";//文本的拼接
		//"\\(?\\s*((\\S+?)\\|\\|(\\S+))\\s+";
		//\(?(\s*'.*?'\s*|\s*\?\s*)\|\|(.*(?=,)),
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
        Matcher m = p.matcher(sql);  
        String getOracleSql ="";
        while(m.find()){
        	getOracleSql = m.group(1);
			System.out.println("获取到的拼接串："+getOracleSql);
			String str1 = m.group(2);
			StringBuffer str1bf = new StringBuffer(str1);
			String str2 = m.group(3);
//			str1 = str1.toLowerCase();
			System.out.println("||分割的第一个字符串str："+str1);
			System.out.println("||分割的之后的字符串str2 "+str2);
			
			str2 = "||"+str2;
			String rege = "\\|\\|(\\s*'[^']+?'\\s*|\\s*\\?\\s*|\\s*[a-zA-Z0-9]+\\s*)";//    \|\|(\s*'[^']+?'\s*|\s*\?\s*|\s*[a-zA-Z0-9]+\s*)
			Pattern p1 = Pattern.compile(rege, Pattern.CASE_INSENSITIVE);  
			 Matcher m1 = p1.matcher(str2);  
			 String generate = "";
			 while(m1.find()){
				 String get1String = m1.group();
				 //例如?||substr(abstract,instr(abstract,'>')+1)，截取到substr后就结束了，
				 	System.out.println(get1String);
				 	int index = sql.indexOf(get1String);//后面紧跟（需要特殊处理
				 	if(index>0 && '('==sql.charAt(index+get1String.length())){
				 		int canout = 0;
				 		int i ;
				 		for(i=index+get1String.length();i<sql.length();i++){
				 			if('(' == sql.charAt(i))
				 				canout ++;
				 			if(')' == sql.charAt(i)){
				 				canout --;
				 				if(canout == 0)
				 					break;
				 			}
				 		}
				 		System.out.println("!!"+sql.substring(index, i));
				 		get1String = sql.substring(index, i+1);
				 		
				 	}
				 generate = generate + get1String;
				 str1bf.append(",");
				 str1bf.append(get1String.substring(2));
			 }
			 String left = "";
			 left = str2.substring(generate.length());
			returnSql = returnSql.replace(getOracleSql, " concat_ws('',"+str1bf.toString()+") "+left);
        }
		return returnSql;
	}
	
	
	/**
	 * 树形查询
	 * @param sql
	 * @return
	 */
	public static String tree(String sql){
//		sql = "select *  from service where fdsaf start  with service in('电信垃圾问题库') where fdsaf  connect by nocycle prior serviceid = parentid where fdsa";
//		sql = "select * from (  select * from t_menuhx   start with menuid = 0 connect by prior menuid= parentid order by menuid ) a where a.menu in ('待办区','问题库管理','场景配置','报表统计','批量测试','交互规则','热点问法','批量训练','统计报表','操作日志','话务处理','单条训练','监控台','参数配置','词模返回值','相似问题训练','反馈意见统计','知识继承','子句词库','查询接口调用统计','关于','业务知识','热点多媒体配置','回归测试','基础词库','系统管理','词库知识','通用问法复用','问答训练','知识管理','帮助')";
		System.out.println("sql= "+sql);
//		sql = "获取到的dataToChar：select count(*) count from ( select * from ( select aa.serviceid , aa.service from service aa where  aa.serviceid in (select serviceid  from service start  with service in('个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库') connect by nocycle prior serviceid = parentid ";
//		sql = "select serviceid  from service start  with service in('个性化业务') connect by nocycle prior serviceid = parentid ";
//		sql = "  select distinct service from service start with serviceid=1826221 connect by nocycle prior parentid=serviceid";
//		sql = "select count(*) count from ( select * from ( select aa.serviceid , aa.service from service aa where  aa.serviceid in (select serviceid  from service start  with service in('个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库') connect by nocycle prior serviceid = parentid )    )ak ) a  inner join (select * from kbdata kk where kk.abstract  like '%全球股票的净值表现如何?%'  and kk.abstract not like '%(删除标识符近类)' ) b   on a.serviceid=b.serviceid  left join ( select c.kbansvaliddateid,c.kbdataid,g.answercontent from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g   where c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid  and e.kbansqryinsid=f.kbansqryinsid  and f.kbcontentid=g.kbcontentid  and f.servicetype='基金行业->华夏基金->多渠道应用'  ) al  on b.kbdataid = al.kbdataid   ";
		String returnSql = sql;
//String returnSql = sql="select * from (  select * from t_menuhx   start with menuid = 0 connect by nocyle prior menuid= parentid order by menuid ) a where a.menu in ('参数配置','词模返回值','相似问题训练','知识继承','批量测试','子句词库','其他','关于','批量训练','业务知识','操作日志','基础词库','监控台','单条训练','系统管理','词库知识','通用问法复用','问答训练','知识管理','帮助')";
//		String regex = "select[^\\(select\\)]+from\\s+(\\S+)\\s+start\\s+with\\s+([^=]+)\\s*=['\"\\s]*(\\S+)['\"\\s]*connect\\s+by\\s+prior\\s+(\\S+)\\s*=\\s*(\\S+)\\s";
		
		//由connect...YY = prior XX 改成connect ... prior XX = YY，，转换成这种格式
		String regexTH = "(connect .*?\\s*)(\\S+)\\s*=\\s*prior\\s*(\\S+)\\s*";
		Pattern pTH = Pattern.compile(regexTH, Pattern.CASE_INSENSITIVE);  
        Matcher mTH = pTH.matcher(sql); 
        if(mTH.find()){
        	String TH = mTH.group();
        	String s1 = mTH.group(1);
        	String s2 = mTH.group(2);
        	String s3 = mTH.group(3);
        	String s = s1+" prior "+s3+"="+s2;
        	returnSql = sql = sql.replace(TH, s);
        }
		
//		String regex = ".*(select(.*)from\\s+(.+)start\\s+with)";
//		(\\s+([^=]+)\\s*(=|in)\\s*(.*))(?:\\s*where.*|\\s*)connect.*prior\\s+(\\S+)\\s*=\\s*(\\w+)((?:\\s*where.*|\\s*))((?:\\s*order\\s+by\\s+\\S+\\s+(\\s*||asc||desc))))";
//		String regex = ".*(?<!in\\s{0,2}\\()\\s*(select(.*?)from\\s+(\\S+)\\s+.*(?:\\s*where.*|\\s*)start\\s+with(\\s+([^=]+)\\s*(=|in)\\s*(.*))(?:\\s*where.*|\\s*)connect.*prior\\s+(\\S+)\\s*=\\s*(\\w+)((?:\\s*where.*|\\s*))((?:\\s*order\\s+by\\s+\\S+\\s+(\\s*||asc||desc)|\\s*)))";
		String regex = ".*(?<!in\\s{0,2}\\()\\s*(select(.*?)from\\s+(\\S+\\s+\\S+||\\S+)\\s+(?:\\s*where.*|\\s*)start\\s+with(\\s+([^=]+)\\s*(=|in)\\s*(.*))(?:\\s*where.*|\\s*)connect.*prior\\s+(\\S+)\\s*=\\s*(\\w+)((?:\\s*where.*|\\s*))((?:\\s*order\\s+by\\s+\\S+\\s+(\\s*||asc||desc)|\\s*)))";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
        Matcher m = p.matcher(sql);  
//        System.out.println(sql);
        while(m.find()){
        	String getOracleSql = m.group();
//        	if(m.group(2).indexOf("select") != -1)
//        		continue;
        	String group1 = m.group(1);//以下都是对group1进行改造，group1就是完整的截取到的树形查询语句
        	
        	String whereAll = "";//对select中的where 部分抽取（start和connect之前和connect之后的）
        	String where0 = "";//对start和connect之前的where部分抽取
        	//从group1中  start之前  找到where从句
        	Pattern p1 = Pattern.compile("where(.*?)(?=start|connect)", Pattern.CASE_INSENSITIVE);  
            Matcher m1 = p1.matcher(group1);
            if(m1.find()){
            	where0 = m1.group(1);
            	if(!"".equals(where0.trim()))
            		whereAll = where0.trim();
            }
        	
			System.out.println("获取到的tree："+getOracleSql);
			String table = m.group(3);
			String key1 = m.group(5).trim();//start with 的条件字段
			String value1 = m.group(7).trim();//start with 的条件内容
			String key2 = m.group(8);//prior 当前的值 XX
			String parent1 = m.group(9);//YY
			
			String where = m.group(10);//对connect之后的where部分抽取
			if(!"".equals(where.trim()) && !"where".equals(where.trim()))
				whereAll = whereAll + where.trim().substring(5);
			if(!"".equals(whereAll.trim()))//where 合并
				whereAll = "and "+whereAll;
			
			String order  = m.group(11).toLowerCase();
			
			if(key1.equals(key2) && "=".equals(m.group(6))){//=prior和
				String list = Tree.getList(value1, key1, parent1, table);
				if(order.contains("order")){
					returnSql = returnSql.replace(m.group(1),"select * from "+table+" where find_in_set("+key1+",'"+list+"')>0 "+ whereAll+" "+order);
				}
				else{
					returnSql = returnSql.replace(m.group(1),"select * from "+table+" where find_in_set("+key1+",'"+list+"')>0 "+ whereAll + " order by "+m.group(4)+" desc ");
				}
			}else{
				String tempSql,addsql;
				if("".equals(m.group(4).trim())){//start with 的完整条件
					tempSql = "select group_concat("+key2+") from "+table;
					addsql = "";
				}
				else{
					System.out.println(m.group(4).trim()+";");
					tempSql = "select group_concat("+key2+") from "+table+" where "+m.group(4);
					addsql = " and "+m.group(4);
				}
				System.out.println(tempSql+";");
				String param = Tree.TokeepsameId(tempSql, key2, m.group(6));//XX的条件范围
				System.out.println(param);
				String list = Tree.getList(param, key2, parent1, table);
				if("$".equals(list.trim())){
					returnSql = returnSql.replace(m.group(1),"select "+m.group(2)+" from "+table+" where "+key2+" is null "+addsql + whereAll+" "+order);
				}
				else{
					if(order.contains("order")){
						returnSql = returnSql.replace(m.group(1),"select "+m.group(2)+" from "+table+" where find_in_set("+key2+",'"+list+"')>0 " + whereAll+" "+order);
					}
					else{
						returnSql = returnSql.replace(m.group(1),"select "+m.group(2)+" from "+table+" where find_in_set("+key2+",'"+list+"')>0 " + whereAll+ " order by "+m.group(4)+" desc ");
					}
				}
			}
			System.out.println(returnSql);
        }
		return returnSql;
	}
	/**
	 * rownum 同级的表 t.*,rownum rn ==》t
	 * @param sql
	 * @return
	 */
	public static String getActualTableFromRownum(String sql){
		//select.*select\s+(\S+)\.\*.*select
		//"：select * from (select t.*,rownum rn from (select c.*,(select wordclass from wordclass where wordclassid=c.wordclassid) wordclass from serviceattrname2colnum c where c.serviceid=?  order by c.columnnum asc) t) where rn>? and rn<=?";
		//select * from (select p.*,rownum r from patternkey p where 1=1 )  where r >? and r <=?
		//sql =" select t2.* from(select t1.*, rownum rn from (select a.wordclass,t.type,t.word,t.wordid,t.wordclassid from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=?  and a.wordclass=?  and (t.type=? or t.type=? or t.type is null )  order by t.wordid desc)t1)t2 where t2.rn>? and t2.rn<=?";
		String regex = "select.*?select.+?(\\w+)\\.\\*.*select";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
		Matcher m = p.matcher(sql);  
		String tab = "";
		if(m.find()){
			tab = m.group(1);
		}
		return tab;
	}
	/**
	 * 三层select包住
	 * @param sql
	 * @return
	 */
	public static String rownum(String sql, String rowtable){
//		sql = "select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey like '%'||?||'%' )  where r >? and r <=?";
//		sql = "select * from (select p.*,rownum r from patternkey p where 1=1 )  where r >? and r <=?";
//		sql = "select * from (select t.*,rownum rn from (select c.kbansvaliddateid,g.kbanswerid,g.kbcontentid,g.answercontent,f.channel,f.answercategory,f.customertype,to_char(c.begintime,'yyyy-MM-dd') begintime,to_char(c.endtime,'yyyy-MM-dd') endtime,f.servicetype,g.answer_clob from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g where b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid and b.kbdataid=? and f.servicetype=?  )t) where rn>? and rn<=? ";
//		sql = "select * from (select t.*,rownum rn from (select k.abstract,k.kbdataid,(select count(distinct w.wordpat) from wordpat w where w.kbdataid in(k.kbdataid)) wordpatcount,(select count(*) from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid  and f.servicetype=?  and b.kbdataid in(k.kbdataid)) answercount, (select count(*) from similarquestion where kbdataid in (k.kbdataid)) questioncount  from kbdata k,service s where k.serviceid=s.serviceid and s.serviceid = ?  and k.topic= ?   order by k.kbdataid desc )t  where rownum<= ? ) t1 where t1.rn>=? ";
		String returnSql = sql;
		
		//"：select * from (select t.*,rownum rn from (select c.*,(select wordclass from wordclass where wordclassid=c.wordclassid) wordclass from serviceattrname2colnum c where c.serviceid=?  order by c.columnnum asc) t) where rn>? and rn<=?";
		//select * from (select p.*,rownum r from patternkey p where 1=1 )  where r >? and r <=?
		//select wordid from word where rownum<2 and wordclassid=? and word=? and type=? 
//select t2.* from(select t1.*, rownum rn from (select a.wordclass,t.type,t.word,t.wordid,t.wordclassid from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=?  and a.wordclass=?  and (t.type=? or t.type=? or t.type is null )  order by t.wordid desc)t1)t2 where t2.rn>? and t2.rn<=?
//select * from  word t,wordclass a where t.wordclassid=a.wordclassid and a.container=?  and a.wordclass=?  and (t.type=? or t.type=? or t.type is null  limit ?,? 
		
//		String returnSql = sql="select t2.* from(select t1.*,rownum rn from (select * from metafieldmapping where 1>0  order by metafieldmappingid desc)t1)t2 where t2.rn>? and t2.rn<=?";
//		sql = "select t2.* from(select t1.*, rownum rn from (select a.wordclass,t.type,t.word,t.wordid,t.wordclassid from word t,wordclass a where t.wordclassid=a.wordclassid and a.container=?  and a.wordclass=?  and (t.type=? or t.type=? or t.type is null )  order by t.wordid desc)t1)t2 where t2.rn>? and t2.rn<=?";	
//		select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey like '%'||?||'%' )  where r >? and r <=?
		String actualTable = getActualTableFromRownum(sql);
//		String actualTable = "t1";
		System.out.println("rownum提取并列的表名："+actualTable);
		String regex ;
		if("".equals(actualTable)){//并列的表没有from
			regex = ".*rownum\\s+(\\S+)\\s+from\\s*(.*?)\\s*\\).*where.*>\\s*(\\S+).*<=?\\s*(\\S+)";//".*rownum\\s+(\\S+)\\s+from\\s*(.*?)\\s+where.*>\\s*(\\S+).*<=?\\s*(\\S+)";
//			select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey like '%'||?||'%' )  where r >? and r <=?
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
			Matcher m = p.matcher(sql);  
				while(m.find()){
		        	String getOracleSql = m.group();
					System.out.println("获取到的三层rownum："+getOracleSql);
					String str2 = m.group(2);
					String small = m.group(3);
					String large = m.group(4);
					
					returnSql = returnSql.replace(m.group(),"select * from "+str2+" limit "+small+","+large);
					System.out.println(returnSql);
					break;
		        }
				regex = ".*rownum\\s+(\\S+)\\s+from\\s*(.*?)\\s*\\).*where.*<=?\\s*(\\S+).*>\\s*(\\S+)";//".*rownum\\s+(\\S+)\\s+from\\s*(.*?)\\s+where.*>\\s*(\\S+).*<=?\\s*(\\S+)";
//				select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey like '%'||?||'%' )  where r >? and r <=?
				p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
				m = p.matcher(sql);  
					while(m.find()){
			        	String getOracleSql = m.group();
						System.out.println("获取到的三层rownum："+getOracleSql);
						String str2 = m.group(2);
						String small = m.group(4);
						String large = m.group(3);
						
						returnSql = returnSql.replace(m.group(),"select * from "+str2+" limit "+small+","+large);
						System.out.println(returnSql);
						break;
			        }
		}
		else{
//			regex = ".*rownum\\s+(\\S+)\\s+from\\s*\\((.*)(?=\\)\\s*"+actualTable+").*>\\s*(\\S+).*<=?\\s*(\\S+)";//.*from([^"+actualTable+")]+).*>\\s*(\\S+)\\s*.*<=\\s*(\\S+)\\*\\)//////.*>\\s*(\\S+).*<=?\\s*(\\S+)
////			System.out.println(regex);
//			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
//			Matcher m = p.matcher(sql);  
//				if(m.find()){
//			        	String getOracleSql = m.group();
//						System.out.println("获取到的三层rownum："+getOracleSql);
//						String str2 = m.group(2);
//						String small = m.group(3);
//						String large = m.group(4);
//						
//						returnSql = returnSql.replace(m.group(),str2+" limit "+small+","+large);
//						System.out.println(returnSql);
//			        }
			
			//寻找主要的sql部分
			if("".equals(rowtable.trim()))
				regex = ".*rownum\\s+(\\S+)\\s+from\\s*\\((.*)(?=\\)\\s*"+actualTable+")";
			else
				regex = ".*rownum\\s+(\\S+)\\s+from\\s*\\((.*)(?=\\)\\s*"+actualTable+")(?!\\)\\s*"+rowtable+")";//.*from([^"+actualTable+")]+).*>\\s*(\\S+)\\s*.*<=\\s*(\\S+)\\*\\)//////.*>\\s*(\\S+).*<=?\\s*(\\S+)
//			sql = "select * from (select t.*,rownum rn from (select k.abstract,k.kbdataid,(select count(distinct w.wordpat) from wordpat w where w.kbdataid in(k.kbdataid)) wordpatcount,(select count(*) from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid  and f.servicetype=?  and b.kbdataid in(k.kbdataid)) answercount, (select count(*) from similarquestion where kbdataid in (k.kbdataid)) questioncount  from kbdata k,service s where k.serviceid=s.serviceid and s.serviceid = ?  and k.topic= ?   order by k.kbdataid desc )t  where rownum<= ? ) t1 where t1.rn>=? ";
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
			Matcher m = p.matcher(sql);
			String sq1 = "";
			if(m.find()){
//			        String getOracleSql = m.group();
//					System.out.println("获取到的三层rownum："+getOracleSql);
					String str2 = m.group(2);
					sq1 = str2;
					System.out.println("rownum前一半的sql："+sq1);
			}
			
			//寻找较小值
			regex = ".*>=?\\s*([^\\)\\s]+)";//.*from([^"+actualTable+")]+).*>\\s*(\\S+)\\s*.*<=\\s*(\\S+)\\*\\)//////.*>\\s*(\\S+).*<=?\\s*(\\S+)
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
			m = p.matcher(sql);
			if(m.find()){
//			        String getOracleSql = m.group();
//					System.out.println("获取到的三层rownum："+getOracleSql);
					String str2 = m.group(1);
					sq1 += " limit "+ str2;
					System.out.println("rownum进一步的sql："+sq1);
			}
			
			//寻找较大值
			regex = ".*<=?\\s*([^\\)\\s]+)";//.*from([^"+actualTable+")]+).*>\\s*(\\S+)\\s*.*<=\\s*(\\S+)\\*\\)//////.*>\\s*(\\S+).*<=?\\s*(\\S+)
			p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
			m = p.matcher(sql);
			if(m.find()){
//			        String getOracleSql = m.group();
//					System.out.println("获取到的三层rownum："+getOracleSql);
					String str2 = m.group(1);
					sq1 += " , "+ str2+" ";
					System.out.println("rownum进一步的sql："+sq1);
			}
			
			returnSql = sq1;
			System.out.println("rownum最终 ："+returnSql);
		}
		
		return returnSql;
	}
	
	/**
	 * 只有rownum<X的情况
	 * @param sql
	 * @return
	 */
	public static String rownum1(String sql){
		String returnSql = sql;//="select wordid from word where rownum<2 and wordclassid=? and word=? and type=? ";
		//select wordid from word where rownum<2 and wordclassid=? and word=? and type=? 
		
		//select *  from (select * from A where rownum<3) 
		//此情况暂不考虑
		
		//以下情况是一层select查询：
		String regex1 = "rownum\\s*<=?\\s*(\\S+)";
		System.out.println(regex1);
		Pattern p = Pattern.compile(regex1, Pattern.CASE_INSENSITIVE);  
		Matcher m = p.matcher(sql);  
			if(m.find()){
		        	String getOracleSql = m.group();
					System.out.println("获取到的rownum："+getOracleSql);
					String small = m.group(1);
					String limitPart = " limit "+small+" ";
					returnSql = returnSql.replace(m.group(),limitPart);
					System.out.println(returnSql);
					
					//分三种情况：1.where limit 2 and 1<2;  2.where 2<3 and limit 2 and 2<5;   3.where w<4 and limit 2;
					//需要去掉and 并且调换位置
					int begin = returnSql.indexOf(limitPart);
					String beforePart = returnSql.substring(0, begin).trim();
					String afterPart = returnSql.substring(begin + limitPart.length()).trim();
					if("".equals(afterPart) && beforePart.matches("(?i).*and")){//第三种情况
						returnSql = beforePart.substring(0, beforePart.length()-3) + limitPart;
					}
					else if(!beforePart.matches("(?i).*and")){//第一种情况
						returnSql = beforePart + limitPart + afterPart.substring(3);
					}
					else{
						returnSql = beforePart + afterPart.substring(3) + limitPart;
					}
					System.out.println("and 符号处理后："+returnSql);
		        }
		return returnSql;
	}
	
	public static String maxMin_Count(String sql){
//		sql = "Select max(count(abstractid)) as maxnum From Serviceattrname2colnum Where Abstractid In (select abstractid from Serviceorproductinfo where attr7='电信行业->电信集团->4G业务客服应用' and abstractid is not null) group by abstractid";
		String returnSql = sql;
			//	="Select max(count(abstractid)) as maxnum ";
		//Select max(count(abstractid)) as maxnum 
		//select max(a.maxnum) as maxnum from (Select count(abstractid) as maxnum 
//		String regex = "TO_CHAR\\s*\\(\\s*([^,]+)\\s*,\\s*'YYYY-MM-DD hh24:mi:ss'\\s*\\)";//select\s+(max|min)\s*\(count\s*\((\S+)\s*\)\s*\)\s+as\s+(\S+)
		String regex = "select\\s+(max|min)\\s*\\(count\\s*\\((\\S+)\\s*\\)\\s*\\)\\s+as\\s+(\\S+)";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
        Matcher m = p.matcher(sql);  
        String getOracleSql ="";
        while(m.find()){
        	getOracleSql = m.group();
			System.out.println("获取到的拼接串："+getOracleSql);
			String str1 = m.group(1);
//			StringBuffer str1bf = new StringBuffer(str1);
			String str2 = m.group(2);
			String str3 = m.group(3);
//			str1 = str1.toLowerCase();
			String sj = "select max(gb.maxnum) as maxnum from (Select count(abstractid) as maxnum ";
			sj = sj.replace("max", str1).replace("maxnum",	str3).replace("abstractid", str2);
			returnSql = returnSql.replace(getOracleSql, sj);
			returnSql = returnSql+") gb ";
			System.out.println(returnSql);
        }
		return returnSql;
	}
	
	
	
	
	/**
	 * ceil,subStr
	 * @param sql
	 * @return
	 */
	public static String FuncNameReplace(String sql) {
		// TODO Auto-generated method stub
		String returnSql = sql;
		returnSql = returnSql.replaceAll("(?i)ceil(.*?)", "ceiling$1");
		returnSql = returnSql.replaceAll("(?i)subStr(.*?)", "substring$1");
		//oracle 支持substr(lalala,0,6),mysql 必须substring（lalala,1,6）
		if(returnSql.matches("(?i).*substring\\([^,]+,0,.*")){
			System.out.println("substring(*,0,*),mysql 必须substring（*,1,*）------");
			returnSql = returnSql.replaceAll("(?i)(substring\\([^,]+,)0,", "$1"+"1,");
		}
		returnSql = returnSql.replaceAll("(?i)nvl", "ifnull");
		returnSql = returnSql.replaceAll("(?i)length", "char_length");
		return returnSql;
	}

	public static String INSTR(String sql, int p) {
		
		// TODO Auto-generated method stub
//		sql = sql.replaceAll("(?<=\\(.*),(?=[^(]*\\))", "!@#");
		
//		sql = Regex.Replace(sql, @"(?<=\\(.*),(?=[^(]*\\))", "!@#");
		String returnSql = sql;
		String regex = "instr\\(((?:\\(.*?\\)|.)*?)\\)";
		
		Pattern pat = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
        Matcher m = pat.matcher(sql);  
        String getOracleSql ="";
        while(m.find()){
        	getOracleSql = m.group();
        	String param = m.group(1);
//        	param = param.replaceAll("(?<=\\(.*),(?=[^\\(]*\\))", "!@#");
//        	Pattern patttt = Pattern.compile("\\((.+?)\\)", Pattern.CASE_INSENSITIVE);
//        	Matcher mmmmm = patttt.matcher(param);
//        	while(mmmmm.find()){
//        		String sssss = mmmmm.group();
//        		String sssss1 = sssss.replace(",", "!@#");
//        		param = param.replace(sssss, sssss1);
//        	}
////        	String[] params = param.split("(?<!\\(.*\\)),");//(?=.*\\))
//        	String[] params = param.split(",",-1);
//        	for(int i = 0 ; i< params.length ; i++){
//        		params[i] = params[i].replace("!@#", ",");
//        	}
        	String[] params = splitByDouhao(param);
        	int len = params.length;
        	if(len == 2)
        		continue;
        	else if (len == 3){
        		returnSql = returnSql.replace(getOracleSql, "locate("+params[1]+","+params[0]+","+params[2]+")");
        	}
        	else if(len == 4){
        		if("1".equals(params[2].trim()))// LENGTH(SUBSTRING_INDEX('das/dsa/dsa/asds','/',2))+1
        			returnSql = returnSql.replace(getOracleSql, "LENGTH(SUBSTRING_INDEX("+params[0]+","+params[1]+","+params[3]+"))+1+"+p);
        		else{
        			int val = Integer.valueOf(params[2].trim());
        			params[0] = "subString("+params[0]+","+params[2]+")";
        			returnSql = returnSql.replace(getOracleSql, "instr("+params[0]+","+params[1]+",1,"+params[3]+")");
        			returnSql = INSTR(returnSql, val-1);
        		}
        	}
        }
        System.out.println(returnSql);
		return returnSql;
		
	}
	
	/**
	 * 替换逗号,逗号不能再括号内部
	 * @param param
	 * @return
	 */
	public static String[] splitByDouhao(String param){
    	ArrayList<Integer> l = new ArrayList<Integer>();
    	int foundCount = 0;
    	char []arrInput = param.toCharArray();
    	for(int i=0;i<arrInput.length;i++){
    		if(arrInput[i] == '('){
    			foundCount++;
    		}
    		if(arrInput[i]==',' && foundCount>0){
    			l.add(i);
    		}
    		if(arrInput[i]==')'){
    			foundCount--;
    		}
    	}
    	for(int index : l){
    		arrInput[index] = '#';
    	}
    	String res = new String(arrInput);
    	String[] params = res.split(",",-1);
    	
    	
    	int start = 0;
    	if(l.size() == 0)
    		return params;
    	else{
    		int index = 0;
    		for(int i = 0 ; i< params.length ; i++){
        		String ss = params[i];
        		if(ss.contains("#")){
        			char []arrOutput = ss.toCharArray();
            		for(int j = 0;j<ss.length() && index<l.size();j++,start ++){
            			if(start == l.get(index) ){
            				arrOutput[j]=',';
            				index ++;
            			}
            		}
            		params[i] = new String(arrOutput);
        		}
        		else{
        			start += ss.length(); 
        		}
        		
        		start ++;
        	}
        	return params;
    	}
    	
	}

////////////////bieming
	//select distinct attr2,service,attr6,attr5 from (select SI.attr2,S.service,SI.attr6,SI.attr5, ROWNUM RN  from SERVICEORPRODUCTINFO SI,service S where SI.serviceid='1831404.0' and SI.serviceid = S.serviceid)  H
	
	/**
	 * sys_connect_by_path,connect_by_root,connect_by_isleaf
	 * http://blog.csdn.net/yueliangdao0608/article/details/40787119
	 * @param sql
	 * @param p
	 * @return
	 */
	public static String connect_by_path(String sql) {
		String returnSql = sql;
		//String returnSql = sql="select * from (  select * from t_menuhx   start with menuid = 0 connect by nocyle prior menuid= parentid order by menuid ) a where a.menu in ('参数配置','词模返回值','相似问题训练','知识继承','批量测试','子句词库','其他','关于','批量训练','业务知识','操作日志','基础词库','监控台','单条训练','系统管理','词库知识','通用问法复用','问答训练','知识管理','帮助')";
				//connect ... prior XX = YY，，转换成这种格式
				String regexTH = "(connect .*?\\s*)(\\S+)\\s*=\\s*prior\\s*(\\S+)\\s*";
				Pattern pTH = Pattern.compile(regexTH, Pattern.CASE_INSENSITIVE);  
		        Matcher mTH = pTH.matcher(sql); 
		        if(mTH.find()){
		        	String TH = mTH.group();
		        	String s1 = mTH.group(1);
		        	String s2 = mTH.group(2);
		        	String s3 = mTH.group(3);
		        	String s = s1+" prior "+s3+"="+s2;
		        	returnSql = sql = sql.replace(TH, s);
		        }
				
				String regex = ".*(select(.*)from\\s+(\\S+)\\s+(?:\\s*where.*|\\s*)start\\s+with(\\s+([^=]+)\\s*(=|in)\\s*(.*))(?:\\s*where.*|\\s*)connect.*prior\\s+(\\S+)\\s*=\\s*(\\w+)((?:\\s*where.*|\\s*)))";
				Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
		        Matcher m = p.matcher(sql);  
//		        System.out.println(sql);
		        while(m.find()){
		        	String getOracleSql = m.group();
		        	if(m.group(2).indexOf("select") != -1)
		        		continue;
		        	String group1 = m.group(1);
		        	
		        	//zhao substring(SYS_CONNECT_BY_PATH(serviceid,'->'),3) SERVICEID_PATH  
		        	String for_select = m.group(2);
		        	String[] for_select_params = splitByDouhao(for_select);
		        	String new_for_select = "";
		        	for(String for1 : for_select_params){
		        		String for1_lower = for1.toLowerCase();
		        		if(for1_lower.contains("sys_connect_by_path")){
		        			String regex0 = "(?i)sys_connect_by_path\\s*\\(\\s*(\\S+)\\s*,\\s*'(.*?)'\\s*\\)";
		        			Pattern p0 = Pattern.compile(regex0, Pattern.CASE_INSENSITIVE);  
				            Matcher m0 = p0.matcher(for1);
				            while(m0.find()){
				            	String paramp = m0.group(1).toLowerCase();
				            	String fuhao = m0.group(2);
				            	if("service".equals(paramp) || "serviceid".equals(paramp)){
				            		for1 = for1.replaceFirst(regex0, "replace("+paramp+"_node_path,',','"+fuhao+"')");
				            	}
				            	else{
				            		GlobalValue.myLog.info("!!!!!!!sys_connect_by_path--"+paramp+"--没有设计"+returnSql);
				            		return returnSql;
				            	}
				            }
		        		}
		        		new_for_select += (","+for1);
		        	}
		        	new_for_select = new_for_select.substring(1);
		        	String new_sql = "select "+ new_for_select + " from tmp_country_list";
		        	
		        	String whereAll = "";//对select中的where 部分抽取（start和connect之前和connect之后的）
		        	String where0 = "";//对start和connect之前的where部分抽取
		        	//从group1中  start之前  找到where从句
		        	Pattern p1 = Pattern.compile("where(.*?)(?=start|connect)", Pattern.CASE_INSENSITIVE);  
		            Matcher m1 = p1.matcher(group1);
		            while(m1.find()){
		            	where0 = m1.group(1);
		            	if(!"".equals(where0.trim()))
		            		whereAll = where0.trim();
		            }
		        	
					System.out.println("获取到的tree："+getOracleSql);
					String table = m.group(3);
					String key1 = m.group(5).trim();
					String value1 = m.group(7).trim();
					String key2 = m.group(8);//prior 当前的值 
					String parent1 = m.group(9);
					
					String where = m.group(10);//对connect之后的where部分抽取
					if(!"".equals(where.trim()) && !"where".equals(where.trim()))
						whereAll = whereAll + where.trim().substring(5);
					if(!"".equals(whereAll.trim()))//where 合并
						whereAll = "and"+whereAll;
					
					
					new_sql = new_sql+whereAll;
					String param_for_prod = key1+" "+m.group(6)+" "+value1;
					Tree.get_connect_treeList(param_for_prod);
					
					
					returnSql = returnSql.replace(group1, new_sql);
					return returnSql;
		        }
		        return returnSql;
	}

	/**
	 * 左连接：即以左表为基准，到右表找匹配的数据，找不到匹配的用NULL补齐。emp.deptid = emp(+)  
 mysql没有这个(+)，所以都要改为left join
	 * @param mysqlSql
	 * @return
	 */
	public static String zuoLianJie(String sql) {
		// TODO Auto-generated method stub
		String returnSql = sql;
		String regexL = "(?i)(([\\w]+)\\.([\\w]+)\\s*=\\s*([\\w]+)\\.([\\w]+))\\s*\\(\\s*\\+\\s*\\)";//
		Pattern pL = Pattern.compile(regexL, Pattern.CASE_INSENSITIVE);  
        Matcher mL = pL.matcher(sql);  
        if(mL.find()){//左链接
        	String lianjieTiaojian = mL.group(1);
        	String table1 = mL.group(2);//真实值 还是列名？
        	String table2 = mL.group(4);//decimal还是int?
        	if(sql.matches("^(?i).*from.*"+table1+"\\s*,.*$")){
        		sql = sql.replaceAll("(?i)(from.*?)"+table1+"\\s*,", "$1"+table1+" left join ");
        		sql = sql.replaceAll(regexL+"\\s*and", "");
        		sql = sql.replaceAll("\\s*and"+regexL, "");
        		sql = sql.replaceAll("(?i)(from.*?)"+table2+"\\s*where", "$1"+table2+" ON "+lianjieTiaojian+" where ");
        	}
        	else if(sql.matches("^(?i).*from.*"+table2+"\\s*,.*$")){
        		sql = sql.replaceAll("(?i)(from.*?)"+table2+"\\s*,", "$1"+table2+" right join ");
        		sql = sql.replaceAll(regexL+"\\s*and", "");
        		sql = sql.replaceAll("\\s*and"+regexL, "");
        		sql = sql.replaceAll("(?i)(from.*?)"+table1+"\\s*where", "$1"+table1+" ON "+lianjieTiaojian+" where ");
        	}
        }
        
        String regexR = "(?i)(([\\w]+)\\.([\\w]+)\\s*\\(\\s*\\+\\s*\\)\\s*=\\s*([\\w]+)\\.([\\w]+))";//
		Pattern pR = Pattern.compile(regexR, Pattern.CASE_INSENSITIVE);  
        Matcher mR = pR.matcher(sql);  
        if(mR.find()){//右连接
        	String lianjieTiaojian = mR.group(1);
        	String table1 = mR.group(2);//真实值 还是列名？
        	String table2 = mR.group(4);//decimal还是int?
        	if(sql.matches("^(?i).*from.*"+table1+"\\s*,.*$")){
        		sql = sql.replaceAll("(?i)(from.*?)"+table1+"\\s*,", "$1"+table1+" right join ");
        		sql = sql.replaceAll(regexR+"\\s*and", "");
        		sql = sql.replaceAll("\\s*and"+regexR, "");
        		sql = sql.replaceAll("(?i)(from.*?)"+table2+"\\s*where", "$1"+table2+" ON "+lianjieTiaojian+" where ");
        	}
        	else if(sql.matches("^(?i).*from.*"+table2+"\\s*,.*$")){
        		sql = sql.replaceAll("(?i)(from.*?)"+table2+"\\s*,", "$1"+table2+" left join ");
        		sql = sql.replaceAll(regexR+"\\s*and", "");
        		sql = sql.replaceAll("\\s*and"+regexR, "");
        		sql = sql.replaceAll("(?i)(from.*?)"+table1+"\\s*where", "$1"+table1+" ON "+lianjieTiaojian+" where ");
        	}
        }
        returnSql = sql;
		return returnSql;
	}
	
	/**
	 * mysql 不支持在更新的时候查询同一张表,delete 的时候先不做？
	 * insert into service(serviceid,service,parentid,brand,city) values(1833534.1,'aa',1831780.0,'国信证券场景',(select city from service where serviceid=1831780.0))
	 * insert into service(serviceid,service,parentid,brand,city) values(1833534.1,'aa',1831780.0,'国信证券场景',(select A.city from service A where serviceid=1831780.0))
	 * Sql=update scenariosrules set excludedcity=? where relationserviceid=(select relationserviceid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)
	 * @param sql
	 * @return
	 */
	public static String AddBiemingForSameTable(String sql) {
//		sql = "update scenariosrules set excludedcity=? where relationserviceid=(select relationserviceid from scenariosrules where ruleid=?)and (city is null or city='全国') and ruletype=(select ruletype from scenariosrules where ruleid=?)";
//		sql = "insert into service(serviceid,service,parentid,brand,city) values(1833534.1,'aa',1831780.0,'国信证券场景',(select A.city from service  where serviceid=1831780.0))";
		// TODO Auto-generated method stub
		String returnSql = sql.trim();
		String regex = "";
		if(returnSql.toLowerCase().startsWith("insert")){
			regex = "insert\\s*into\\s*([^\\(]+).*values\\s*\\(";//
		}
		else if (returnSql.toLowerCase().startsWith("update")){
			regex = "update\\s*(\\S+)\\s*set\\s*";//
		}
		if("".equals(regex)){
			return returnSql;
		}
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
        Matcher m = p.matcher(returnSql);  
        if(m.find()){
        	String tables = m.group(1).toLowerCase();//表名集合
        	String[] tables1 = tables.split(",",-1);
        	for(String table : tables1){
        		String regexFrom = "\\(select(.*?)from(.*?)(where(.*?)|\\s*)\\)";//
    			Pattern p1 = Pattern.compile(regexFrom, Pattern.CASE_INSENSITIVE);  
    	        Matcher m1 = p1.matcher(returnSql);  
    	        int i= 0;
//    	        int lengthOfNewBieming = 0;
    	        while(m1.find()){
    	        	String tableParams = m1.group(2).toLowerCase();
    	        	String group = m1.group();
    	        	int start = tableParams.indexOf(table);
    	        	int len = table.length();
    	        	int end = start +len;
    	        	//-- update scenariosrules set excludedcity='140000,150000,000000' where relationserviceid=(select relationserviceid from (select * from scenariosrules bieming0 where bieming0.ruleid='974.1') j)
    	        	if(start>0 && tableParams.charAt(start-1)==' ' && tableParams.charAt(end)==' ')//表名确认
    	        	{	
    	        		String group1 = m1.group(1);
    	        		String group2 = m1.group(2);
    	        		String group3 = m1.group(3);
    	        		returnSql = returnSql.replace(group, "(select"+group1+"from(select * from "+group2+group3+") bieming"+i+++")");
//    	        		String before = tableParams.substring(0,end);//table1 ,table, table2的table1 ,table
//    	        		String after = m1.group().substring(end);//(select..., table2 where ) 的, table2 where )
//    	        		String beforeAll = m1.group().substring(0,m1.end(1)+4+end);
//    	        		String afterTrim = after.trim();
//    	        		if("".equals(afterTrim) || afterTrim.startsWith(",")){
//    	        			String bieming = " bieming"+i++;
////    	        			String newTableParams = before+bieming+after;
//    	        			
////    	        			returnSql = returnSql.replace(tableParams, newTableParams);
////    	        			returnSql = returnSql.substring(0,m1.start()+4+lengthOfNewBieming)+newTableParams+returnSql.substring(m1.end()-5+lengthOfNewBieming);
////							lengthOfNewBieming += bieming.length();
//    	        			String beforeFrom = returnSql.substring(0,m1.start()+4+lengthOfNewBieming);
//    	        			String newTableParams = " (select * from"+before+after+") " + bieming;
//    	        			
//							lengthOfNewBieming += bieming.length();
//    	        		}
    	        	}
    	        }
        	}
        	
        }
		return returnSql;
	}
	
	
	
	/**
	 * 
	 * @param sql增加别名操作
	 * @return
	 */
	public static String testBieMing(String sql) {
		//sql = "select * from table where a= b";
		//sql = "select * from (Select A from tableB)";
		//sql = "select * from (select A from tableB ) where s=1";
		//sql = "select * from (select A from tableB) left join (select B from tane) where ";
		//sql = "select * from worker w ,member m  where w.workerid = m.workerid and  w.workerid='179'";
		// TODO Auto-generated method stub
		String returnSql = sql;
		String regex = "\\sfrom";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
	    Matcher m = p.matcher(returnSql);  
	    while(m.find()){
	    	HashSet<Integer> afterBM = null ;
	    	int start = m.start();//后面紧跟（需要特殊处理 
	    	int end = m.end();
	    	int index = end;
	    	while(returnSql.charAt(index++)==' ');
	    	index --;
	    	String temp = returnSql.substring(index).toLowerCase();
	    	int  AddLength= 0 ;
	    	while(!temp.trim().equals("") && !temp.startsWith("where") && !temp.startsWith("start")){
//	    	while(returnSql.substring(--index).startsWith("where"))
		    	if(returnSql.charAt(index)=='('){
		    		int canout = 0;
		    		int j = index;
		    		int i;
		    		for(i=index;i<returnSql.length();i++){
	//	    			System.out.println(sql.charAt(i));
			 			if('(' == returnSql.charAt(i))
			 				canout ++;
			 			if(')' == returnSql.charAt(i)){
			 				canout --;
			 				if(canout == 0)
			 					break;
			 			}
			 		}
		    		i++;
		    		String kuohaonei = returnSql.substring(j, i);
		    		if(i == returnSql.length()){//右面有空格！？
		    			sql = sql+" Bieming"+CreateBieMingName(afterBM);
		    			break;
		    		}
		    		else{
		    			String leftSql = returnSql.substring(i);
		    			String leftRegex = "^\\s*(,|left\\s*join|right\\s*join|inner\\s*join|where)";
		    			Pattern leftp = Pattern.compile(leftRegex, Pattern.CASE_INSENSITIVE);  
		    		    Matcher leftm = leftp.matcher(leftSql); 
		    		    if(leftm.find()){//没有别名
		    		    	String jieci = leftm.group(1).trim();
		    		    	String biemingName = " Bieming"+CreateBieMingName(afterBM);
		    		    	returnSql = returnSql.substring(0,i)+biemingName+leftSql;//
//		    		    	sql = sql.substring(0,i)+biemingName+leftSql;//
		    		    	sql = sql.replace(kuohaonei, kuohaonei+biemingName);
		    		    	if("where".equalsIgnoreCase(jieci)){
		    		    		//彻底退出
		    		    		break;
		    		    		//continue;
		    		    	}
		    		    	else{//跳过介词继续
		    		    		temp = returnSql.substring(i+leftm.end()).toLowerCase();
		    		    		index = i+leftm.end();
		    		    	}
		    		    }
		    		    else{//有别名
	    		    		temp = returnSql.substring(i).toLowerCase();
	    		    		index = i;
		    		    }
		    		}
	//	    		System.out.println(sql.substring(i));
		    	}
		    	else{
	    			String leftRegex = "[a-zA-Z0-9_\\s,]";
	    			Pattern leftp = Pattern.compile(leftRegex, Pattern.CASE_INSENSITIVE);  
	    		    Matcher leftm = leftp.matcher(temp); 
	    		    if(leftm.find()){
	    		    	temp = temp.substring(leftm.end());
	    		    	index = index+leftm.end();
	    		    }
		    	}
//		    	temp =temp.trim();
		    	if(temp.startsWith(")"))
		    		break;
	    	}
	    }
	    System.out.println("sql加别名后:"+sql);
		return sql;
	}
	
	public static int CreateBieMingName(HashSet<Integer> already){
		if(already==null)
			already = new HashSet<Integer>();
		int random = (int) Math.floor(Math.random()*10000);
		while(already.contains(random)){
			random = (int) Math.floor(Math.random()*10000);
		}
		already.add(random);
		return random;
	}
//	/**
//	 * 
//	 * @param sql
//	 * @return
//	 */
//	public static String ExampleNewMethod(String sql) {
//		// TODO Auto-generated method stub
//		String returnSql = sql;
//		String regex = "";
//		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
//	    Matcher m = p.matcher(returnSql);  
//	    if(m.find()){
//		return returnSql;
//	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ReplaceString rs = new ReplaceString();
//		String s = "SELECT COUNT(*) count FROM   (SELECT b.serviceid,     b.service,     b.parentid,     a.abstract,     a.kbdataid,     q.query   FROM querymanage q,     kbdata a,     (select  *      from service where serviceid is null  and  service         IN ('电信垃圾问题库')                                  ) b   WHERE q.kbdataid = a.kbdataid   AND a.serviceid  = b.serviceid   AND q.query LIKE '%fds%'   ) queries,   (SELECT b.kbdataid,     c.kbansvaliddateid,     g.kbanswerid,     g.kbcontentid,     g.answercontent,     f.channel,     f.answercategory,     f.customertype,     DATE_FORMAT(c.begintime,'%Y-%m-%d') begintime,     DATE_FORMAT(c.endtime,'%Y-%m-%d') endtime,     f.servicetype,     g.answer_clob,     f.city,     f.excludedcity   FROM kbdata b,     kbansvaliddate c,     kbanspak d,     kbansqryins e,     kbcontent f,     kbanswer g   WHERE b.kbdataid      =c.kbdataid   AND c.kbansvaliddateid=d.kbansvaliddateid   AND d.kbanspakid      =e.kbanspakid   AND e.kbansqryinsid   =f.kbansqryinsid   AND f.kbcontentid     =g.kbcontentid   AND f.servicetype     = '证券行业->国信证券->多渠道应用'   ) answer WHERE queries.kbdataid = answer.kbdataid(+)  AND serviceid IN (1831779.0,1831782.0,1833525.1)  ";
		//String s = "insert into service(serviceid,service,parentid,brand,city) values(1833534.1,'aa',1831780.0,'国信证券场景',(select city from service where serviceid=1831780.0))";
		//String s1 = rs.AddBiemingForSameTable(s);
		
//		String s1 = " SELECT *  FROM    ( select  Service,      Serviceid,      city,      substring(replace(service_node_path,',','->'),3) Name_Path,    substring(replace(serviceid_node_path,',','->'),3) Serviceid_Path     from tmp_country_list)  WHERE Upper(Service) LIKE '%证券测试%'  AND Serviceid IN    	(SELECT resourceid AS serviceid    FROM role_resource    WHERE roleid IN      	(SELECT roleid FROM workerrolerel WHERE workerid= 179      )  	AND Resourcetype='querymanage'    AND Servicetype = '证券行业->国信证券->多渠道应用'    )";
//		System.out.println(testBieMing(s1));
		
		String sql = "SELECT SUBSTR(t.abstract,0,INSTR(t.abstract,'>')) FROM kbdata t WHERE t.kbdataid=11863517.0;";
		System.out.println(FuncNameReplace(sql));
		
		//System.out.println(AddBiemingForSameTable(""));
//		String s = "substring(SYS_CONNECT_BY_PATH(serviceid,'->'),3)";
//		String[] ss = splitByDouhao(s);
//		String sql = " SELECT q.query, a.abstract, b.* FROM kbdata a, querymanage q,   (SELECT service,     serviceid,     SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) NAME_PATH,     SUBSTR(SYS_CONNECT_BY_PATH(serviceid,'->'),3) SERVICEID_PATH   FROM service     START WITH serviceid         in(fd)     CONNECT BY prior serviceid = parentid   ) b WHERE a.serviceid  = b.serviceid AND b.serviceid IN (1831779.0,1831782.0) ANd a.kbdataid = q.kbdataid AND TRIM(q.query) = ?";
		//System.out.println(rs.connect_by_path(sql));
//		String testSql = "some thidng like 'fds'||'hf'||'O' sd";
//		String sql = "select * from t_menuhx   start with menuid = 0 connect by prior menuid= parentid order by menuid";
//		System.out.println(tree(sql));
		
//		String sql  = "select * from (select t.*,rownum rn from (  select * from (select s.service,s.brand,k.kbdataid,(select count(*) from wordpat where kbdataid=k.kbdataid and wordpattype!=5  and wordpat not like '%编者=\"问题库\"%' ) wordpatcount,(select count(*) from relatequery where kbdataid=k.kbdataid ) relatequerycount,(select count(*) from kbansvaliddate where kbdataid=k.kbdataid ) answercount ,(select count(*) from serviceorproductinfo where attr6 =to_char(k.kbdataid) and abstractid is not null ) extendcount , k.abstract,k.city abscity,k.responsetype,k.interacttype,k.topic  from (select * from service where serviceid=? ) s   inner join(select * from kbdata where abstract like ?  and (responsetype like '%%' or responsetype is null )  and (interactType like '%%' or interactType is null )   ) k on s.serviceid = k.serviceid ) aa order by aa.kbdataid desc   )t  where rownum<= ? ) t1 where t1.rn>?";
//		System.out.println(rs.charToDate(sql));
//		String sql = "select (industry || '->' || organization || '->' || application) as customer from M_INDUSTRYAPPLICATION2SERVICES";
//		String s = ToConcat(sql);
//		System.out.println(s);
		
//		String MysqlSql="select * from worker where customer like '%' order by workerid limit 15,20 and ";
//		String value = "10";
//		System.out.println(MysqlSql.matches("limit\\s+(\\S+)\\s*,.*"));
//		if(MysqlSql.matches("limit\\s+(\\S+)\\s*,.*"))
//			MysqlSql = MysqlSql.replaceAll("limit\\s+(\\S+)\\s*,\\s*\\S+\\s*", "limit $1 ,"+value+" ");
//		else
//			MysqlSql = MysqlSql.replaceAll("limit\\s+([\\d\\.]+)\\s*", "limit "+value+"  ");////错误！！！！！ limit ? //MysqlSql = MysqlSql.replaceAll("limit\\s+([\\d|\\.]+)(?!\\s*,)", "limit "+value+"  ");////错误
//		System.out.println("MysqlSql:2"+MysqlSql);
		
//		String testSql = "select TO_CHAR( '2018-01-02 23:43:22', 'YYYY-MM-DD hh24:mi:ss') from dual";
//		System.out.println(rs.dateToChar(testSql));
//		System.out.println(testSql);
//		System.out.println(rs.ToConcat(testSql));
//		rs.rownum1("select * from metafield t where rownum<2 and t.metafieldmappingid=? and t.name=? and t.stdmetafieldid is null");
//		rs.rownum("select * from (select p.*,rownum r from patternkey p where 1=1 )  where r >? and r <=?");
//		rs.maxMin_Count("");
//		System.out.println(rs.getActualTableFromRownum(""));
//		System.out.println(rs.ToConcat(""));
//		rs.maxMin_Count("");
//		rs.rownum("", "");
		
//		rs.tree("");
//		rs.INSTR("select instr('aaaaaaaaaaaa','a',5,2)",0);
//		String str="1#2#3";
//		String[] strs=str.split("#");
//		System.out.println(strs.length);
		
//		String filenameStr = "aa/haha.exe";
//        
//        //named group only support after Java 7
//        //here is my java version:
//        //Java: 1.7.0_09; Java HotSpot(TM) 64-Bit Server VM 23.5-b02
//        Pattern filenameP = Pattern.compile("^.+/(?<filenamePart>.+)$");
//        Matcher filenameMatcher = filenameP.matcher(filenameStr);
//        boolean foundFilename = filenameMatcher.matches();
//                 
//        System.out.println(filenameMatcher);
//        System.out.println(foundFilename);
//         
//        String onlyFilename = filenameMatcher.replaceFirst("${filenamePart}");
//        System.out.println(onlyFilename);
//		
	}

}
