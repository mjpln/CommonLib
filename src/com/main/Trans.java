package com.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sequence.ReplaceSequence;
import com.str.ReplaceString;
/**
 * http://localhost:8080/OracleToMysql/TransPort?wsdl
 * @author ghj
 *
 */
public class Trans {

	public static String transform(String oracleSql){
//		oracleSql = "insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,simplewordpat,city,workerid) values(?,?,?,?,?,?,sysdate,?,?,?)";
//		oracleSql = " SELECT * FROM service start WITH service='用户对订票' AND cityid IN (284) and brand in('个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库') and parentid =1810055.0 connect BY nocycle prior parentid=serviceid";
//		oracleSql = "select count(*) count from ( select * from ( select aa.serviceid , aa.service from service aa where  aa.serviceid in (select serviceid  from service start  with service in('个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库') connect by nocycle prior serviceid = parentid ";
//		oracleSql = "select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey regexp ? )  where r >? and r <=?";
//		oracleSql = "select count(*) count from ( select * from ( select aa.serviceid , aa.service from service aa where  aa.serviceid in (select serviceid  from service start  with service in('个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库') connect by nocycle prior serviceid = parentid )    )ak ) a  inner join (select * from kbdata kk where kk.abstract  like '%华夏%'  and kk.abstract not like '%(删除标识符近类)' ) b   on a.serviceid=b.serviceid  left join ( select c.kbansvaliddateid,c.kbdataid,g.answercontent from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g   where c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid  and e.kbansqryinsid=f.kbansqryinsid  and f.kbcontentid=g.kbcontentid  and f.servicetype='基金行业->华夏基金->多渠道应用'  ) al  on b.kbdataid = al.kbdataid";
//		oracleSql = "select * from (select t.*,rownum rn from (select k.abstract,k.kbdataid,(select count(distinct w.wordpat) from wordpat w where w.kbdataid in(k.kbdataid)) wordpatcount,(select count(*) from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid  and f.servicetype=?  and b.kbdataid in(k.kbdataid)) answercount, (select count(*) from similarquestion where kbdataid in (k.kbdataid)) questioncount  from kbdata k,service s where k.serviceid=s.serviceid and s.serviceid = ?  and k.topic= ?   order by k.kbdataid desc )t  where rownum<= ? ) t1 where t1.rn>=?";
//		oracleSql = "select * from (select t.*,rownum rn from (select k.abstract,k.kbdataid,(select count(distinct w.wordpat) from wordpat w where w.kbdataid in(k.kbdataid)) wordpatcount,(select count(*) from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid  and f.servicetype=?  and b.kbdataid in(k.kbdataid)) answercount, (select count(*) from similarquestion where kbdataid in (k.kbdataid)) questioncount  from kbdata k,service s where k.serviceid=s.serviceid and s.serviceid = ?  and k.topic= ?   order by k.kbdataid desc )t  where rownum<= ? ) t1 where t1.rn>=? ";
		String fs = oracleSql.toLowerCase();
		fs = fs.replaceAll("sysdate\\s*", "NOW() ");
		String fsnokong = fs.replace(" ", "");
		
		fs = fs.replace("systimestamp", "CURRENT_TIMESTAMP");
		
		if(fs.indexOf("||") != -1){
			fs = ReplaceString.ToConcat(fs);
			System.out.println("ReplaceString字符串类调用toConcat："+oracleSql+"==>"+fs);
		}
		if(fs.indexOf("nextval") != -1){
			fs = ReplaceSequence.mysqlStr(fs);
			System.out.println("ReplaceSequence序列类调用mysqlStr："+oracleSql+"==>"+fs);
		}
		if(fs.indexOf("to_date") != -1){
			fs = ReplaceString.charToDate(fs);
			System.out.println("ReplaceString字符串类调用charToDate："+oracleSql+"==>"+fs);
		}
		if(fs.indexOf("to_char") != -1){
			fs = ReplaceString.dateToChar(fs);
			System.out.println("ReplaceString字符串类调用dateToChar："+oracleSql+"==>"+fs);
		}
		if(fs.indexOf("connect") != -1){
			fs  = ReplaceString.tree(fs);
			System.out.println("ReplaceString字符串类调用Tree："+oracleSql+"==>"+fs);
		}
		if(fs.indexOf("rownum") != -1){
//			System.out.println("fsnokong:"+fsnokong);
//			if(fsnokong.indexOf("rownum>") != -1){//limit ?,? //rn就错了
//				fs  = ReplaceString.rownum(fs);
//				System.out.println("ReplaceString字符串类调用三层select的rownum："+oracleSql+"==>"+fs);
//			}
//			else{//limit ?
//				fs = ReplaceString.rownum1(fs);
//				System.out.println("ReplaceString字符串类调用一层select的rownum："+oracleSql+"==>"+fs);
//			}
			String regex = "rownum\\s+(?![><])(\\S+)\\s";
			Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
			Matcher m = p.matcher(fs);  
			String rownum = "rownum";//取出别名
			if(m.find()){
				rownum = m.group(1);
				System.out.println("rownum 别名为："+rownum);
			}
			
			String rowtable = "";
			if(!rownum.equals("rownum")){
				regex = ".*\\s+(\\S+)\\.rownum";
				regex = regex.replace("rownum", rownum);
				p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
				m = p.matcher(fs);  
				if(m.find()){
					rowtable = m.group(1);
					System.out.println("使用rownum 表名为："+rowtable);
				}
			}
			
			
			if(fsnokong.indexOf(rownum+">") != -1){//limit ?,? 
				fs  = ReplaceString.rownum(fs, rowtable);
				System.out.println("ReplaceString字符串类调用三层select的rownum："+oracleSql+"==>"+fs);
			}
			else{//limit ?
				fs = ReplaceString.rownum1(fs);
				System.out.println("ReplaceString字符串类调用一层select的rownum："+oracleSql+"==>"+fs);
			}
		}
		if(fsnokong.indexOf("max(count")!=-1 ||fsnokong.indexOf("min(count")!=-1){
			fs = ReplaceString.maxMin_Count(fs);
			System.out.println("ReplaceString字符串类调用maxcount"+oracleSql+"==>"+fs);
		}
		return fs;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String sql = transform("select name from metafield t where  t.name=? and t.stdmetafieldid=? and  rownum<2  ");
//		//String sql = transform("select *  from metafield t where rownum<2 and t.metafieldmappingid=? and t.name=? and t.stdmetafieldid is null ");
//		System.out.println(sql);
		String sql = "select name from metafield t where  t.name=? and t.stdmetafieldid=?andrownum<2  ";
		String S = "rownum<2";
		int pre = sql.indexOf(S);
		String s = sql.substring(0,pre);
		int after = sql.lastIndexOf(S);
		System.out.println(s.substring(0,s.length()-3)+"!");
		System.out.println(sql.substring(after)+"!");
	}

}
