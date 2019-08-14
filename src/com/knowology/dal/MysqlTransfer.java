package com.knowology.dal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.knowology.GlobalValue;
import com.sequence.ReplaceSequence;
import com.str.ReplaceString;

public class MysqlTransfer {
	
	public String OraSql;
	
	public String MysqlSql;
	
//	public List<?> Params;
	public Object[] Params;
	
	public int rownumSmal;
	public int rownumMax;
	public int rownumInterval;
	
	public int ParamLength;
	
	public int getParamLength() {
		return ParamLength;
	}

	public void setParamLength(int paramLength) {
		ParamLength = paramLength;
	}

	public Object[] getParams() {
		return Params;
	}

	public void setParams(Object[] params) {
		Params = params;
	}

	public int RownumAPos;
	
	
	public String getOraSql() {
		return OraSql;
	}

	public void setOraSql(String oraSql) {
		OraSql = oraSql;
	}

	public String getMysqlSql() {
		return MysqlSql;
	}

	public void setMysqlSql(String mysqlSql) {
		MysqlSql = mysqlSql;
	}


	public MysqlTransfer(String _sql, Object[] _params)
	{
		OraSql = _sql;
		Params = _params;
		rownumSmal = 0;
		rownumMax = 0;
		rownumInterval = 0;
		
		if(_params !=null && _params.length>0){
			ParamLength = Params.length;
		}
		
		
//		_params.
	}
	
	
	
	/**
	 * rownum 查询中，获取较小值的位置
	 * @return
	 */
	
	public  int getRownumSmall()//返回值得包含 是否有较小值      是否有问号
	{		
//		String result = "";//guize
		int result = 0;
		String rownumBieming = rownumBieMing(OraSql);
		String regex = "(rownum|rownumBieming)\\s*>\\s*(=?)\\s*(\\?|[\\d\\.]+)";//".*rownum\\s+(\\S+)\\s+from\\s*(.*?)\\s+where.*>\\s*(\\S+).*<=?\\s*(\\S+)";
		regex = regex.replaceAll("rownumBieming", rownumBieming);
//		select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey like '%'||?||'%' )  where r >? and r <=?
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
		Matcher m = p.matcher(OraSql);  
		int start = -1;
		String param = "",dengyu = "";
		if(m.find()){
			start = m.start();
			dengyu = m.group(2);
			param = m.group(3);
		}
		
		Boolean isExistEqual = ("".equals(dengyu))? false : true;
		GlobalValue.myLog.info("较小值是否存在‘=’"+isExistEqual);
		if(isExistEqual){
			//需要减一！！！！！！，默认是>Min的形式，转为limit Min ,***
		}
		if(start != -1){//
			if("?".equals(param)){
				String headA = OraSql.substring(0, start+1);
				String[] temp = headA.split("\\?",-1);
				int count = temp.length;
				result = count-1;
				Object o = Params[count-1];///可能会报错
				int value = Integer.parseInt(o.toString());
				GlobalValue.myLog.info("maxrownum>? 原始值是第"+count+"个，值为"+value);
				value = isExistEqual?value-1:value;
				if(value == -1){
					GlobalValue.myLog.info("smallrownum = -1====> smallrow = 0");
					value = 0;
				}
				rownumSmal = value;
				
				Params[count-1] = new Integer(value);
				GlobalValue.myLog.info("maxrownum>? 修改值为"+Params[count -1]);
				
			}
			else{
				int value = Integer.parseInt(param);
				value = isExistEqual?value-1:value;
				if(value == -1){
					GlobalValue.myLog.info("smallrownum = -1====> smallrow = 0");
					value = 0;
				}
				rownumSmal = value;
				GlobalValue.myLog.info("maxrownum>? 修改值为"+value);
//				param = String.valueOf(Integer.parseInt(param)-1);
//				param = String.valueOf(value);
				MysqlSql = MysqlSql.replaceAll("(?i)limit\\s+([\\d\\.]+)\\s*,", "limit "+value+" ,");
				GlobalValue.myLog.info("MysqlSql:较小值替换后"+MysqlSql);
			}
		}
		return result;
	}
	
	/**
	 * rownum 查询中，获取较大值的位置
	 * @return
	 */
	public  int getRownumLarge()
	{		
//		String result = "";//guize
		int result = 0;
		String rownumBieming = rownumBieMing(OraSql);
		String regex = "(rownum|rownumBieming)\\s*<\\s*(=?)\\s*(\\?|[\\d\\.]+)";//".*rownum\\s+(\\S+)\\s+from\\s*(.*?)\\s+where.*>\\s*(\\S+).*<=?\\s*(\\S+)";
		regex = regex.replaceAll("(?i)rownumBieming", rownumBieming);
//		select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey like '%'||?||'%' )  where r >? and r <=?
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
		Matcher m = p.matcher(OraSql);  
		int start = -1;
		String param = "",dengyu = "";
		if(m.find()){
			start = m.start();
			dengyu = m.group(2);
			param = m.group(3);
		}		
		Boolean isExistEqual = ("".equals(dengyu))? false : true;
		GlobalValue.myLog.info("较大值是否存在‘=’"+isExistEqual);
		if(!isExistEqual){//默认<=   没有=则需要减一操作
			//Params需要减一！！！！！！
		}
		
		if(start != -1){//
			if("?".equals(param)){
				String headA = OraSql.substring(0, start+1);
				String[] temp = headA.split("\\?",-1);
				int count = temp.length;
				result = count-1;
				Object o = Params[count-1];///可能会报错
				int value = Integer.parseInt(o.toString());
				GlobalValue.myLog.info("maxrownum<? 原始值是第"+count+"个，值为"+value);
				value = isExistEqual?value:value-1;
				rownumMax = value;
//				Params.set(count-1, (new Integer(value)));/////
				Params[count -1] = new Integer(value-rownumSmal);
				GlobalValue.myLog.info("maxrownum<? 修改值为"+Params[count -1]);
			}
			else{
//				System.out.println("111111111111111");
				int value;
				if(!isExistEqual){
					value = Integer.parseInt(param)-1;
				}
				else{
					value = Integer.parseInt(param);
				}
					value = value - rownumSmal;
					GlobalValue.myLog.info("maxrownum<? 修改值为"+value);
					GlobalValue.myLog.info("MysqlSql:较大值替换前"+MysqlSql);
					if(MysqlSql.matches(".*limit\\s+(\\S+)\\s*,.*"))//形如limit A,B 
						MysqlSql = MysqlSql.replaceFirst("limit\\s+(\\S+)\\s*,\\s*\\S+\\s*", "limit $1 ,"+value+" ");
					else//形如limit  A （=limit 0,A）
						MysqlSql = MysqlSql.replaceFirst("limit\\s+([\\d\\.]+)\\s*", "limit "+value+"  ");////错误！！！！！ limit ? //MysqlSql = MysqlSql.replaceAll("limit\\s+([\\d|\\.]+)(?!\\s*,)", "limit "+value+"  ");////错误
					GlobalValue.myLog.info("MysqlSql:较大值替换后"+MysqlSql);
					setMysqlSql(MysqlSql);
				
			}
		}
		return result;
	}
	
	
//	public  void processRowNum()
//	{
//		
//	}
	/**
	 * rownum< 和 rownum>     的位置正则判断不出来，有需要调换位置
	 * @param rownumBieming
	 * @param smallPos
	 * @param largePos
	 */
	public void exchangeRownum(String rownumBieming, int smallPos, int largePos){//Params
//		String regex = "(rownum|rownumBieming)\\s*>\\s*(=?)\\s*\\?.*(rownum|rownumBieming)\\s*<\\s*(=?)\\s*\\?";//".*rownum\\s+(\\S+)\\s+from\\s*(.*?)\\s+where.*>\\s*(\\S+).*<=?\\s*(\\S+)";
//		regex = regex.replaceAll("rownumBieming", rownumBieming);
////		select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey like '%'||?||'%' )  where r >? and r <=?
//		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
//		Matcher m = p.matcher(OraSql);  
//		if(!m.find()){
//			//交换大值与小值的问题
//			Object sm = Params[smallPos];
//			Params[smallPos] = Params[largePos];
//			Params[largePos] = sm;
//		}
		String regex = "(rownum|rownumBieming)\\s*<\\s*(=?)\\s*\\?.*(rownum|rownumBieming)\\s*>\\s*(=?)\\s*\\?";//".*rownum\\s+(\\S+)\\s+from\\s*(.*?)\\s+where.*>\\s*(\\S+).*<=?\\s*(\\S+)";
		regex = regex.replaceAll("rownumBieming", rownumBieming);
//		select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey like '%'||?||'%' )  where r >? and r <=?
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
		Matcher m = p.matcher(OraSql);  
		if(m.find()){
			//交换大值与小值的问题
			Object sm = Params[smallPos];
			Params[smallPos] = Params[largePos];
			Params[largePos] = sm;
		}
	}
	
	
	/**
	 * ceil 功能: 返回不小于 X 的最小整数
		ascii 功能:返回字符串 str 最左边的那个字符的 ASCII 码值。如果 str 是一个空字符串，那么返回值为 0。如果 str 是一个 NULL，返回值也是 NULL.
		floor功能: 返回不大于 X 的最大整数值
		CHAR() 以整数类型解释参数，返回这个整数所代表的 ASCII 码值给出的字符组成的字符串。NULL 值将被忽略.
		REPLACE(str,from_str,to_str) 在字符串 str 中所有出现的字符串 from_str 均被 to_str 替换，然后返回这个字符串.
		INSTR('sdsq','s',2)===>INSTR('sdsq','s')从默认的位置1开始
		instr(‘abcdefg’,’ab’)===>locate(‘ab’,’abcdefg’)
		SUBSTR('abcd',2,2)===>substring('abcd',2,2)
		length（str）===>char_length(str)
		nvl ==>ifnull?isnull?
		decode ==> if()
		trunc   => cast( ? as signed)
	 */
	public void transfer(){
		//the  same
			//method  round(),abs,floor,ascii, max, CHAR,REPLACE,lpad,upper,lower
		
			//parameter
		//not  same
			//method  nextval,to_date,to_char,connect,rownum,max(count),||, ceil,instr,substr,length,nvl,decode,trunc
			//parameter  sysdate,systimestamp
		
		//  substr instr
//		oracleSql = "insert into wordpat(wordpatid,wordpat,autosendswitch,wordpattype,kbdataid,brand,edittime,simplewordpat,city,workerid) values(?,?,?,?,?,?,sysdate,?,?,?)";
//		oracleSql = " SELECT * FROM service start WITH service='用户对订票' AND cityid IN (284) and brand in('个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库') and parentid =1810055.0 connect BY nocycle prior parentid=serviceid";
//		oracleSql = "select count(*) count from ( select * from ( select aa.serviceid , aa.service from service aa where  aa.serviceid in (select serviceid  from service start  with service in('个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库') connect by nocycle prior serviceid = parentid ";
//		oracleSql = "select * from (select p.*,rownum r from patternkey p where 1=1 and patternkey regexp ? )  where r >? and r <=?";
//		oracleSql = "select count(*) count from ( select * from ( select aa.serviceid , aa.service from service aa where  aa.serviceid in (select serviceid  from service start  with service in('个性化业务','产品','网上交易','理财课堂','服务','基金行业问题库') connect by nocycle prior serviceid = parentid )    )ak ) a  inner join (select * from kbdata kk where kk.abstract  like '%华夏%'  and kk.abstract not like '%(删除标识符近类)' ) b   on a.serviceid=b.serviceid  left join ( select c.kbansvaliddateid,c.kbdataid,g.answercontent from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g   where c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid  and e.kbansqryinsid=f.kbansqryinsid  and f.kbcontentid=g.kbcontentid  and f.servicetype='基金行业->华夏基金->多渠道应用'  ) al  on b.kbdataid = al.kbdataid";
//		oracleSql = "select * from (select t.*,rownum rn from (select k.abstract,k.kbdataid,(select count(distinct w.wordpat) from wordpat w where w.kbdataid in(k.kbdataid)) wordpatcount,(select count(*) from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid  and f.servicetype=?  and b.kbdataid in(k.kbdataid)) answercount, (select count(*) from similarquestion where kbdataid in (k.kbdataid)) questioncount  from kbdata k,service s where k.serviceid=s.serviceid and s.serviceid = ?  and k.topic= ?   order by k.kbdataid desc )t  where rownum<= ? ) t1 where t1.rn>=?";
//		oracleSql = "select * from (select t.*,rownum rn from (select k.abstract,k.kbdataid,(select count(distinct w.wordpat) from wordpat w where w.kbdataid in(k.kbdataid)) wordpatcount,(select count(*) from service a,kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  where a.serviceid=b.serviceid and b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid  and f.servicetype=?  and b.kbdataid in(k.kbdataid)) answercount, (select count(*) from similarquestion where kbdataid in (k.kbdataid)) questioncount  from kbdata k,service s where k.serviceid=s.serviceid and s.serviceid = ?  and k.topic= ?   order by k.kbdataid desc )t  where rownum<= ? ) t1 where t1.rn>=? ";
		OraSql = OraSql.replace("　", " ");
		MysqlSql = OraSql;
		MysqlSql = MysqlSql.replaceAll("(?i)sysdate\\s*", "now() ");
//		fs = fs.replaceAll("(?i)nvl", "ifnull");
		
		MysqlSql = ReplaceString.FuncNameReplace(MysqlSql);
		
		String fsnokong = MysqlSql.toLowerCase().replace(" ", "");
		
		MysqlSql = MysqlSql.replaceAll("(?i)systimestamp", "current_timestamp");
		
		if(fsnokong.indexOf("instr") != -1){
			MysqlSql = ReplaceString.INSTR(MysqlSql,0);
			GlobalValue.myLog.info("ReplaceString字符串类调用INSTR："+OraSql+"==>"+MysqlSql);
		}
		if(fsnokong.indexOf("||") != -1){
			MysqlSql = ReplaceString.ToConcat(MysqlSql);
			GlobalValue.myLog.info("ReplaceString字符串类调用toConcat："+OraSql+"==>"+MysqlSql);
		}
		if(fsnokong.indexOf("nextval") != -1){
			MysqlSql = ReplaceSequence.mysqlStr(MysqlSql);
			GlobalValue.myLog.info("ReplaceSequence序列类调用mysqlStr："+OraSql+"==>"+MysqlSql);
		}
		if(fsnokong.indexOf("to_date") != -1){
			MysqlSql = ReplaceString.charToDate(MysqlSql);
			GlobalValue.myLog.info("ReplaceString字符串类调用charToDate："+OraSql+"==>"+MysqlSql);
		}
		if(fsnokong.indexOf("to_char") != -1){
			MysqlSql = ReplaceString.dateToChar(MysqlSql);//暂时只做了部分时间的转换
			GlobalValue.myLog.info("ReplaceString字符串类调用dateToChar："+OraSql+"==>"+MysqlSql);
		}
		if(fsnokong.indexOf("to_number") != -1){
			MysqlSql = ReplaceString.StringToNumber(MysqlSql);
			GlobalValue.myLog.info("######未实现########ReplaceString字符串类调用StringToNumber："+OraSql+"==>"+MysqlSql);
		}
		if(fsnokong.indexOf("connect") != -1){
//			String connectRegex = "^.*start.*\\?(?:\\s*where.*|\\s*)connect.*prior.*$";
			String connectRegex = "^.*start.*\\?.*connect.*prior.*$";
			if(fsnokong.matches(connectRegex)){//prepared预处理，需要将sql替换，参数列表替换
				System.out.println("tree检测到？");
				processTree(MysqlSql, connectRegex);
			}
			if(fsnokong.contains("sys_connect_by_path")){
				MysqlSql = ReplaceString.connect_by_path(MysqlSql);
				GlobalValue.myLog.info("ReplaceString字符串类调用Connect_by_path："+OraSql+"==>"+MysqlSql);
			}
			else{
				MysqlSql  = ReplaceString.tree(MysqlSql);
				GlobalValue.myLog.info("ReplaceString字符串类调用Tree："+OraSql+"==>"+MysqlSql);
			}
		}
		if(fsnokong.indexOf("rownum") != -1){
			String rownum = rownumBieMing(MysqlSql);
			String rowtable = rownumUseTable(MysqlSql);
			
			
			if(fsnokong.indexOf(rownum+">") != -1){//limit ?,? 
				MysqlSql  = ReplaceString.rownum(MysqlSql, rowtable);
				GlobalValue.myLog.info("ReplaceString字符串类调用三层select的rownum："+OraSql+"==>"+MysqlSql);
				int smallPos = getRownumSmall();
				int largePos = getRownumLarge();
				exchangeRownum(rownum, smallPos, largePos);		
			}
			else if(fsnokong.indexOf(rownum+"<") != -1){//limit ?
				MysqlSql = ReplaceString.rownum1(MysqlSql);
				GlobalValue.myLog.info("ReplaceString字符串类调用一层select的rownum："+OraSql+"==>"+MysqlSql);
				int largePos = getRownumLarge();
			}
			else{
				MysqlSql = MysqlSql.replaceAll("(?i),\\s*rownum\\s*"+rownum, "");
				MysqlSql = MysqlSql.replaceAll("(?i),\\s*rownum\\s*", "");
				GlobalValue.myLog.info("rownum未使用，已被删除："+OraSql+"==>"+MysqlSql);
			}
		}
		if(fsnokong.indexOf("max(count")!=-1 ||fsnokong.indexOf("min(count")!=-1){
			MysqlSql = ReplaceString.maxMin_Count(MysqlSql);
			GlobalValue.myLog.info("ReplaceString字符串类调用maxcount"+OraSql+"==>"+MysqlSql);
		}
//		if(MysqlSql.matches("^(?i).*from\\s*\\(\\s*select.*?from[^\\(\\)]*(?=in\\s*\\([^\\)]*\\)[^\\(\\)]*\\)|[^\\(\\)]*\\))(?:where.*|\\s*)$")){///需要别名select distinct attr2,service,attr6,attr5 from (select SI.attr2,S.service,SI.attr6,SI.attr5, ROWNUM RN  from SERVICEORPRODUCTINFO SI,service S where SI.serviceid='1831404.0' and SI.serviceid = S.serviceid) 
//			MysqlSql = MysqlSql + " as MOKKKO";
//			GlobalValue.myLog.info("-----Every derived table must have its own alias---"+OraSql+"==>"+MysqlSql);
//		}
		
		if(fsnokong.indexOf("(+)")!=-1 ){
			MysqlSql = ReplaceString.zuoLianJie(MysqlSql);
			GlobalValue.myLog.info("ReplaceString字符串类调用zuoLianJie"+OraSql+"==>"+MysqlSql);
		}
		if(fsnokong.startsWith("insert") || fsnokong.startsWith("update") ){
			MysqlSql = ReplaceString.AddBiemingForSameTable(MysqlSql);
			GlobalValue.myLog.info("ReplaceString字符串类调用AddBieming"+OraSql+"==>"+MysqlSql);
		}
		if(fsnokong.matches("^.*from.*\\(.*\\).*where.*") ){
			MysqlSql = ReplaceString.testBieMing(MysqlSql);
			GlobalValue.myLog.info("ReplaceString字符串类调用AddBieming"+OraSql+"==>"+MysqlSql);
		}
		
//		return fs;
//		setMysqlSql(OraSql);
	}
	

/**
 * 树形查询替换prepared Statement参数  start with a=? ===>start with a=param[X]
 * @param fs
 * @param connectRegex
 * @return
 */
	private Object processTree(String fs, String connectRegex) {
		Object result = new Object();
//		connectRegex = "(.*start.*)\\?((?:\\s*where.*|\\s*)connect.*prior.*)";
//		String regex = "start.*\\?((?:\\s*where.*|\\s*)connect.*prior";////////////////////这种形式为什么报错？？？？？？、、、
		String regex = "(?i)(start.*)\\?(.*connect.*prior)";
		Matcher m = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(fs);
		try{
			if(m.find()){
				int end = m.end();
				String headA = OraSql.substring(end+1, OraSql.length());
				String[] temp = headA.split("\\?",-1);
				int count = temp.length-1;
				int ParamsLen = ParamLength;
				result = Params[ParamsLen-count-1];///可能会报错
				GlobalValue.myLog.info("maxrownum>? 原始值是   倒数    第"+count+"个，值为"+result);
				String S = "";
				if(Params != null  &&  ParamLength>0)
					for(Object i:Params){
						S += (i==null)?"":i.toString(); 
					}
				GlobalValue.myLog.info("-------初始数组为："+S);
				while(count > 0 ){
					Params[ParamsLen-count-1] = Params[ParamsLen-count];
					count --;
				}
				ParamLength = ParamLength - 1;/////////0  -->   -1
				S = "";
				Object[] a = {};
				if(Params != null  &&  ParamLength>0){
					a = new Object[ParamLength];
					for(int i=0;i<ParamLength;i++){
						S += Params[i].toString(); 
						a[i]  = Params[i];
					}
					Params = a ;
					GlobalValue.myLog.info("-------更改后的数组为："+S);
				}
				else{
					Params = null ;
					GlobalValue.myLog.info("-------更改后的数组为："+null);
				}
				
				MysqlSql = MysqlSql.replaceAll(regex, "$1"+result+"$2");
				GlobalValue.myLog.info("-------更改后的sql为："+MysqlSql);
			}
		}catch(Exception e){
			GlobalValue.myLog.error(e.getMessage(), e);
			return null;
		}
		return result;
			
	}

	/*
	 * <max,>min==>limit min,max              <max==> limit max 
	 * 
	 * 第一步将 < 找出，> 找出 后面跟的是?,替换参数位置
	 * 转mysql，，+等号处理
	 */
	/**
	 * 使用rownum的别名，rownum rn-->rn
	 * @param fs
	 * @return
	 */
	public String rownumBieMing(String fs){
		String regex = "rownum\\s+(?![><])(\\w+)";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
		Matcher m = p.matcher(fs);  
		String rownum = "rownum";//取出别名
		if(m.find()){
			rownum = m.group(1);
			GlobalValue.myLog.info("rownum 别名为："+rownum);
		}
		return rownum.toLowerCase();
	}
	
	/**
	 * 第一次使用rownum别名的表名  select (select t.*, rownum rn, from t) y where y.rn<? ==>y
	 * @param fs
	 * @return
	 */
	public String rownumUseTable(String fs){
		String rowtable = "";
		String regex = "";
		String rownum = rownumBieMing(fs);
		if(!rownum.equals("rownum")){
			regex = ".*\\s+(\\S+)\\.rownum";
			regex = regex.replaceAll("(?i)rownum", rownum);
			Pattern  p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
			Matcher m = p.matcher(fs);  
			if(m.find()){
				rowtable = m.group(1);
				GlobalValue.myLog.info("使用rownum 表名为："+rowtable);
			}
		}
		return rowtable.toLowerCase();
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
		String regex = "select.*?select\\s+(\\S+)\\.\\*.*select";
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);  
		Matcher m = p.matcher(sql);  
		String tab = "";
		if(m.find()){
			tab = m.group(1);
		}
		return tab;
	}
	
	public String paramToString(){
		String S = "";
		if(Params != null)
			for(Object i:Params){
				S += (i==null)?"":i.toString(); 
			}
		return S;
	}
	
	public String toString(){
		String S = "Oracle Sql="+OraSql+"=====>>>>>>Mysql  sql="+MysqlSql+"【】【】【】Params="+paramToString();
		return S;
	}
	
	public static void main(String[] args) {
//		new MysqlTransfer("select * from (select *,rownum rn  from table where rn >?)", null).getRownumSmall();
		
//		String MysqlSql = "select * from table limit  2 , and ,";
//		MysqlSql = MysqlSql.replaceAll("limit\\s+([\\d|\\.]+)(?!\\s*,)", "limit $1 +1 ");
//		GlobalValue.myLog.info(MysqlSql);
//		String s = "fdsj";
//		String[] ss = s.split("\\?");
//		GlobalValue.myLog.info(ss.length);
//		String[] s1 = "sf".split("f",-1);
//		System.out.println(s1.length);
//		
//		
//		String sql = "select relationserviceid scenariosid,abstractid,service from scenarios2kbdata where relationserviceid in (select serviceid  from service start  with service in ('联通集团场景','环保局场景','电信集团场景','电信集团指令场景','尚德机构场景')connect by nocycle prior serviceid = parentid) ";
//		Object[] param = null;
//		MysqlTransfer mt = new MysqlTransfer(sql,param);
//		mt.transfer();
//		sql = mt.getMysqlSql();
//		System.out.println(sql);
		
//		String MysqlSql = "select distinct attr2,service,attr6,attr5 from (select SI.attr2,S.service,SI.attr6,SI.attr5, ROWNUM RN  from SERVICEORPRODUCTINFO SI,service S where SI.serviceid='1831404.0' and SI.serviceid = S.serviceid)  ";
//		if(MysqlSql.matches("^(?i).*from\\s*\\(\\s*select.*?from.*?\\)\\s*$")){
//			
//			MysqlSql = MysqlSql + " MOKKKO";
//		}
//		System.out.println(MysqlSql);
		
		//String sql = " SELECT q.query, a.abstract, b.* FROM kbdata a, querymanage q,   (SELECT service,     serviceid,     SUBSTR(SYS_CONNECT_BY_PATH(service,'->'),3) NAME_PATH,     SUBSTR(SYS_CONNECT_BY_PATH(serviceid,'->'),3) SERVICEID_PATH   FROM service     START WITH serviceid         in(1831779.0)     CONNECT BY prior serviceid = parentid   ) b WHERE a.serviceid  = b.serviceid AND b.serviceid IN (1831779.0,1831782.0,1833525.1) ANd a.kbdataid = q.kbdataid AND TRIM(q.query) = 'fdsa'";
		//String sql = " SELECT COUNT(*) count FROM   (SELECT b.serviceid,     b.service,     b.parentid,     a.abstract,     a.kbdataid,     q.query   FROM querymanage q,     kbdata a,     (SELECT *     FROM service       START WITH service         IN ('电信垃圾问题库')       CONNECT BY prior serviceid = parentid     ) b   WHERE q.kbdataid = a.kbdataid   AND a.serviceid  = b.serviceid   AND q.query LIKE '%fds%'   ) queries,   (SELECT b.kbdataid,     c.kbansvaliddateid,     g.kbanswerid,     g.kbcontentid,     g.answercontent,     f.channel,     f.answercategory,     f.customertype,     TO_CHAR(c.begintime,'yyyy-MM-dd') begintime,     TO_CHAR(c.endtime,'yyyy-MM-dd') endtime,     f.servicetype,     g.answer_clob,     f.city,     f.excludedcity   FROM kbdata b,     kbansvaliddate c,     kbanspak d,     kbansqryins e,     kbcontent f,     kbanswer g   WHERE b.kbdataid      =c.kbdataid   AND c.kbansvaliddateid=d.kbansvaliddateid   AND d.kbanspakid      =e.kbanspakid   AND e.kbansqryinsid   =f.kbansqryinsid   AND f.kbcontentid     =g.kbcontentid   AND f.servicetype     = '证券行业->国信证券->多渠道应用'   ) answer WHERE queries.kbdataid = answer.kbdataid(+)  AND serviceid IN (1831779.0,1831782.0,1833525.1) ";
		//String sql = "select distinct attr2,service,attr6,attr5 from (select SI.attr2,S.service,SI.attr6,SI.attr5, ROWNUM RN  from SERVICEORPRODUCTINFO SI,service S where SI.serviceid='1831404.0' and SI.serviceid = S.serviceid) ";
		
//		String sql = "update kbdata set abstract=?||substr(abstract,instr(abstract,'>')+1) where serviceid=?";
//		String sql = "select distinct attr2,service,attr6,attr5 from (select SI.attr2,S.service,SI.attr6,SI.attr5, ROWNUM RN  from SERVICEORPRODUCTINFO SI,service S where SI.serviceid='1831404.0' and SI.serviceid = S.serviceid) BIEMING where rn>0 and rn<=10";
//		String sql ="select * from (select t.*,rownum rn from(select * from(select * from SCENARIOSRULES where relationserviceid=? )h where 1>0  and ruletype in(0,4) order by weight )t) where rn>? and rn <=?";
//		CommonLibInteractiveSceneDAO.java(3181) -异常sql==>select * from (select t.*,rownum rn from(select * from(select * from SCENARIOSRULES where relationserviceid=? )h where 1>0  and ruletype in(0,4) order by weight )t) where rn>? and rn <=?
//		String sql = "select * from (select rownum rn,t.* from (select w.wordid,w.stdwordid,w.word,w.type,w.city,w.cityname,c.wordclass,c.wordclassid from word w,wordclass c where w.wordclassid=c.wordclassid and c.wordclass='' and w.stdwordid is null order by w.wordid desc)t) where rn >0 and rn <= 10";
//		Object[] param = new Object[3];
//		param[0] = "1831783.0";
//		param[1] = 0;
//		param[2] = 10;
//		String sql = "select name from metafield t where  t.name=? and t.stdmetafieldid=? and rownum<2";
		String sql = "sql= SELECT * FROM service start WITH service='用户对机器人问好过程' AND cityid IN (284) and brand in('国信证券','个性化业务','证券行业问题库','国信证券问题库','场景','国信证券场景') and parentid =1804103.0 connect BY nocycle prior parentid=serviceid";
//		String sql = " select count(*) count from (SELECT *  FROM service Where  serviceid IN(select resourceid as serviceid from role_resource where roleid in(select roleid from  workerrolerel where workerid='all') and resourcetype='querymanage' and servicetype ='证券行业->国信证券->多渠道应用')  start  WITH service in('国信证券','个性化业务','证券行业问题库','国信证券问题库','场景','国信证券场景') connect BY nocycle prior serviceid = parentid ) a  inner join kbdata b   on a.serviceid=b.serviceid   and b.abstract not like '%(删除标识符近类)'  inner join ( select c.kbansvaliddateid,c.kbdataid,g.answercontent from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g   where c.kbansvaliddateid=d.kbansvaliddateid  and d.kbanspakid=e.kbanspakid  and e.kbansqryinsid=f.kbansqryinsid  and f.kbcontentid=g.kbcontentid  and f.servicetype='证券行业->国信证券->多渠道应用'  and g.answercontent like '%你好%' ) al  on b.kbdataid = al.kbdataid ";
		MysqlTransfer mt = new MysqlTransfer(sql,null);
		mt.transfer();
		sql = mt.getMysqlSql();
		System.out.println(sql);
		System.out.println(mt.paramToString());
	}
	
}


