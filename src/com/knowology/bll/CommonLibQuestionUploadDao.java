package com.knowology.bll;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;

public class CommonLibQuestionUploadDao {
	
	/**
	 * 获取下拉框中的省份
	 * @param rolename
	 * @param bsname
	 * @return
	 */
	public static Result selProvince(String province,String customer) {
		String sql = "";
		sql = "select t.name as id,min(s.name) as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' group by t.name order by id";
//		sql = "select * from (select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' order by s.metafieldid) a where province like '%市' or province like '%区' or province like '%省'";
//		// 非集团管理员，非云平台组长
		if (!"全行业".equals(customer)){
			sql = "select * from (" + sql + ") qx where id like '%" + province + "%'";
		}
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 获取所有省编码名称
	 * @return
	 */
	public static Result selProvince() {
		String sql = "";
		sql = "select t.name as id,min(s.name) as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' group by t.name order by id";
//		sql = "select * from (select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' order by s.metafieldid) a where province like '%市' or province like '%区' or province like '%省'";
		Result rs = null;
		rs = Database.executeQuery(sql); 
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}


	public static Result selProvinceByServiceType(String serviceType) {
		String name = "地市编码配置@"+serviceType;
		String sql = "";
		sql = "select t.name as id,min(s.name) as province from metafield t,metafield s,metafieldmapping a where a.name='"+name+"' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' group by t.name order by id";
		Result rs = null;
		rs = Database.executeQuery(sql); 
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	

	/**
	 * 获取所有省+"全国"编码名称
	 * @return
	 */

	public static Result selAllProvince() {
		String sql = "";
		sql = "select t.name as id,min(s.name) as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  (t.name like '%0000' or t.name='全国') group by t.name order by id";
//		sql = "select * from (select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' order by s.metafieldid) a where province like '%市' or province like '%区' or province like '%省'";
		Result rs = null;
		rs = Database.executeQuery(sql); 
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 获取某省的编码
	 * @param city
	 * @return
	 */
	public static Result selProvince(String city) {
		String sql = "";
		sql = "select t.name as id,min(s.name) as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '"+city+"%' group by t.name order by id";
//		sql = "select * from (select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' order by s.metafieldid) a where province like '%市' or province like '%区' or province like '%省'";
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	

	public static Result selProvinceBycityCodes(String city) {
		String sql = "";
		sql = "select t.name as id,min(s.name) as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name in ("+city+") group by t.name order by id";
//		sql = "select * from (select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' order by s.metafieldid) a where province like '%市' or province like '%区' or province like '%省'";
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	

	/**
	 * 获取某省+“全国”编码名称
	 * @param city
	 * @return
	 */

	public static Result selAllProvince(String city) {
		String sql = "";
		sql = "select t.name as id,min(s.name) as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  (t.name like '"+city+"%' or t.name='全国') group by t.name order by id";
//		sql = "select * from (select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' order by s.metafieldid) a where province like '%市' or province like '%区' or province like '%省'";
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 获取城市名称下拉框
	 * @param id
	 * @return
	 */
	public static Result getCity(String id) {
		String innerid = id.substring(0, 2);
		String sql = "";
		sql = "select t.name as id,min(s.name) as city from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '"
				+ innerid + "%00' and t.name not like '%0000' group by t.name order by t.name";
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 获取某一个城市的编码名称
	 * @param city
	 * @return
	 */
	public static Result getOneCity(String city) {
		String sql = "select t.name as id,min(s.name) as city from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name='"+city+"'  group by t.name order by t.name";
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 *  获取某省下所有城市的city编码名称
	 * @param id
	 * @return
	 */
	public static Result getCityByTree(String id) {
		String innerid = id.substring(0, 2);
		String sql = "";
		sql = "select t.name as id,min(s.name) as city from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '"
				+ innerid + "%' and t.name not like '%0000' group by t.name order by t.name";
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 获取城市编码名称
	 * @param id
	 * @return
	 */
	public static Result getCityByProvince(String id) {
		String innerid ="";
		if(id.endsWith("0000")){
		 innerid = id.substring(0, 2);	
		}else{
		 innerid=id;	
		}
		String sql = "";
		sql = "select t.name as id,min(s.name) as city from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '"
				+ innerid + "%' and t.name not like '%0000' group by t.name order by t.name";
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	

	/**
	 * 查询问法
	 * @param question
	 * @param other
	 * @param starttime
	 * @param endtime
	 * @param username
	 * @param status
	 * @param selProvince
	 * @param selCity
	 * @param hot
	 * @param hot2
	 * @param pid
	 * @param user
	 * @param ids
	 * @param locString
	 * @return
	 */
	public static Result gethotquestion(String question, String other, String starttime,
			String endtime, String username, String status, String selProvince, String selCity,
			String hot,String hot2,Integer pid,User user, String ids, String locString) {
		String sql = "";
		sql = "SELECT count(*) as total FROM hotquestion s, hotquestion p WHERE s.parentid=p.hotquestionid and s.servicetype='"+user.getIndustryOrganizationApplication()+"' ";
//			sql = "SELECT p.hotquestionid as pid, s.hotquestionid as sid,p.question as question,s.question as other,s.uploadtime,s.username,s.province,s.city,s.result,s.status,p.hot,s.hot as hot2,s.reason,s.solution FROM hotquestion s, hotquestion p WHERE s.parentid=p.hotquestionid and s.servicetype='"+user.getCustomer()+"' ";
		if (null != question && !question.equals("")) {
			sql += " and p.question like'%" + question +"%'"; 
		}
		if (null != other && !other.equals("")) {
			sql += " and s.question like'%" + other +"%'"; 
		}
		if (null != starttime && !starttime.equals("")) {
			sql += " and  s.uploadtime>= to_date('"
			+ starttime +" 00:00:00" + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (null != endtime && !endtime.equals("")) {
			sql += " and s.uploadtime<= to_date('"
					+ endtime + "23:59:59" + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (null != username && !username.equals("")) {
			sql += " and s.username like '%"
					+ username + "%'";
		}
//		String rolename = user.getRoleList().get(0).getRoleName().toString().replace("管理员", "");
//		String bsname = user.getCustomer().split("->")[1];
//		if("云平台组长".equals(rolename)||bsname.equals(rolename)){
//			if (null != selProvince && !selProvince.equals("")) {
//				sql = "select * from (" + sql + ") sel where province = '" + selProvince
//				+ "'";
//			}
//		} else {
//			sql = "select * from (" + sql + ") sel where province = '" + rolename
//			+ "'";
//		}
		if (null != selProvince && !selProvince.equals("")) {
			sql += "and s.province = '" + selProvince
			+ "'";
		}
		if (null != selCity && !selCity.equals("")) {
			sql += " and s.city = '" + selCity
			+ "'";
		}
		if(locString.length() > 0){
			if (!locString.contains("全国")){
				sql += " and s.province in (" + locString + ")"; 
			}
		}else {
			sql += " and s.province in ('')";
		}
		if (null != hot && !hot.equals("")) {
			sql += " and p.hot = '" + hot + "'";
		}
		if (null != hot2 && !hot2.equals("")) {
			sql += " and s.hot = '" + hot2 + "'";
		}
		if (null != status && !status.equals("")) {
			sql += " and s.status = '" + status
					+ "'";
		}
		if(null != pid && !pid.equals("")){
			sql += " and p.hotquestionid = " + pid;
		}
		
		if(null != ids && !ids.equals("")){
			sql += " and s.hotquestionid in (" + ids + ")";
		}
//		if (GetConfigValue.isOracle) {
//			sql = "SELECT pid,sid,question,other,to_char(uploadtime,'yyyy-mm-dd') as uploadtime,username,province,city,result,status,hot,hot2,reason,solution FROM (" + sql + ") al where 1=1";
//			
//		} else {
//			sql = "SELECT pid,sid,question,other,DATE_FORMAT(uploadtime,'%Y-%m-%d') as uploadtime,username,province,city,result,status,hot,hot2,reason,solution FROM (" + sql + ") al where 1=1";
//		}
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 根据id查询问法用于下载
	 * @param ids
	 * @return
	 */
	public static Result gethotquestiondown( String ids) {
		String sql = "SELECT p.hotquestionid as pid, s.hotquestionid as sid,p.question as question,s.question as other,s.uploadtime,s.username,s.province,s.city,s.result,s.status,p.hot,s.hot as hot2,s.reason,s.solution,s.flag FROM hotquestion s, hotquestion p WHERE s.parentid=p.hotquestionid and s.hotquestionid in(" + ids + ")";
		if (GetConfigValue.isOracle) {
			sql = "SELECT pid,sid,question,other,to_char(uploadtime,'yyyy-mm-dd') as uploadtime,username,province,city,result,status,hot,hot2,reason,solution,flag FROM (" + sql + ") al where 1=1";
			
		} else {
			sql = "SELECT pid,sid,question,other,DATE_FORMAT(uploadtime,'%Y-%m-%d') as uploadtime,username,province,city,result,status,hot,hot2,reason,solution,flag FROM (" + sql + ") al where 1=1";
		}
		
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 分页查询问法
	 * @param page
	 * @param rows
	 * @param question
	 * @param other
	 * @param starttime
	 * @param endtime
	 * @param username
	 * @param status
	 * @param selProvince
	 * @param selCity
	 * @param hot
	 * @param hot2
	 * @param pid
	 * @param user
	 * @param locString
	 * @return
	 */
	public static Result gethotquestion(int page, int rows, String question,String other,
			String starttime, String endtime, String username, String status,
			String selProvince, String selCity, String hot,String hot2,Integer pid,User user,String locString) {
		String sql = "";
		if(GetConfigValue.isMySQL){
			sql = "SELECT p.hotquestionid as pid, s.hotquestionid as sid,p.question as question,s.question as other,s.uploadtime,s.username,s.province,s.city,s.result,s.status,p.hot,s.hot as hot2,s.reason,s.solution,s.flag FROM hotquestion s, hotquestion p WHERE s.parentid=p.hotquestionid and s.servicetype='"+user.getIndustryOrganizationApplication()+"'";
		} else {
			sql = "SELECT p.hotquestionid as pid, s.hotquestionid as sid,p.question as question,s.question as other,s.uploadtime,s.username,s.province,s.city,s.result,s.status,p.hot,s.hot as hot2,s.reason,s.solution,s.flag FROM hotquestion s, hotquestion p WHERE s.parentid=p.hotquestionid and s.servicetype='"+user.getIndustryOrganizationApplication()+"'";
		}
		if (null != question && !question.equals("")) {
			sql = "select * from (" + sql + ") q where question like '%"
					+ question + "%'";
		}
		if (null != other && !other.equals("")) {
			sql = "select * from (" + sql + ") ot where other like '%"
					+ other + "%'";
		}
		if (null != starttime && !starttime.equals("")) {
			sql = "select * from (" + sql + ") st where uploadtime>= to_date('"
			+ starttime +" 00:00:00" + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (null != endtime && !endtime.equals("")) {
			sql = "select * from (" + sql + ") et where uploadtime<= to_date('"
					+ endtime + "23:59:59" + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (null != username && !username.equals("")) {
			sql = "select * from (" + sql + ") us where username like '%"
					+ username + "%'";
		}
		String rolename = user.getRoleList().get(0).getRoleName().toString().replace("管理员", "");
		String bsname = user.getIndustryOrganizationApplication().split("->")[1];
//		if("云平台组长".equals(rolename)||bsname.equals(rolename)){
			if (null != selProvince && !selProvince.equals("")) {
				sql = "select * from (" + sql + ") sel where province = '" + selProvince
				+ "'";
			}
//		} else {
//			sql = "select * from (" + sql + ") sel where province = '" + rolename
//			+ "'";
//		}
		if (null != selCity && !selCity.equals("")) {
			sql = "select * from (" + sql + ") sel where city = '" + selCity
			+ "'";
		}
		if(locString.length() > 0){
			if (!locString.contains("全国")){
				sql = "select * from (" + sql + ") ls where province in (" + locString + ")"; 
			}
		}else {
			sql ="select * from (" + sql + ") ls where province in ('')";
		}
		if (null != hot && !hot.equals("")) {
			sql = "select * from (" + sql + ") ho where hot = '" + hot + "'";
		}
		if (null != hot2 && !hot2.equals("")) {
			sql = "select * from (" + sql + ") ho2 where hot2 = '" + hot2 + "'";
		}
		if (null != status && !status.equals("")) {
			sql = "select * from (" + sql + ") sta where status = '" + status
					+ "'";
		}
		if(null != pid && !pid.equals("")){
			sql = "select * from (" + sql + ") pi where pid = " + pid;
		}
		if (GetConfigValue.isOracle) {
			sql = "SELECT pid,sid,question,other,to_char(uploadtime,'yyyy-mm-dd') as uploadtime,username,province,city,result,status,hot,hot2,reason,solution,flag FROM (" + sql + ") al where 1=1";
			
		} else {
			sql = "SELECT pid,sid,question,other,DATE_FORMAT(uploadtime,'%Y-%m-%d') as uploadtime,username,province,city,result,status,hot,hot2,reason,solution,flag FROM (" + sql + ") al where 1=1";
		}
		
		
		
		if (GetConfigValue.isOracle) {
			sql = "select * from (select t.*, rownum  rn from (" + sql
					+ " order by sid desc) t where rownum<=" + page * rows + " ) where rn>"
					+ (page - 1) * rows;
		} else {
			sql = sql + "order by sid desc limit " + (page - 1) * rows + "," + page * rows;
		}
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	public static int setAttr(String attrid) throws SQLException {
		// 定义SQL语句
		StringBuilder sql = new StringBuilder();
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 定义确认服务或产品信息的SQL语句
		sql.append("update hotquestion set hot='yes' where hotquestionid in (");
		// 将id按照逗号拆分
		String[] attridArr = attrid.split(",");
		// 循环遍历id数组
		for (int i = 0; i < attridArr.length; i++) {
			if (i != attridArr.length - 1) {
				// 除了最后一个不加逗号，其他都加逗号
				sql.append("?,");
			} else {
				// 最后一个加上右括号，将SQL语句补充完整
				sql.append("?)");
			}
			// 绑定id参数
			lstpara.add(attridArr[i]);
		}
		// 执行SQL语句，绑定事务，返回事务处理结果
		int result = Database.executeNonQuery(sql.toString(), lstpara.toArray());
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + lstpara );
		
		return result;
	}

	/**
	 * 保存提交
	 * @param ids
	 * @param reason
	 * @param solution
	 * @return
	 */
	public static int doSaveReport(String  ids, String reason, String solution) {
		String sql = "";
		if (null != reason && !reason.equals("")){
			sql = "update hotquestion set reason='" + reason + "',solution='',status='-1' where hotquestionid in (" + ids + ")";
		}
		if (null != solution && !solution.equals("")){
			sql = "update hotquestion set solution='"+ solution + "',status='1' where hotquestionid in (" + ids + ")";
		}
		int c = 0;
		try {
			c = Database.executeNonQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 上传问法
	 * @param attrinfo
	 * @param user
	 * @param ProvinceLocalMap
	 * @param CityLocalMap
	 * @return
	 */
	public static int[] InsertHotQuestion(List<List<Object>> attrinfo,User user,Map<String,String> ProvinceLocalMap,Map<String,String> CityLocalMap)
			{
		
		int r = -1;
		
		int count = 0;
		
		int repeat = 0;
		int pnull = 0;
		int snull = 0;
		int pronull = 0;
		int cityfail = 0;
		
		// 循环遍历
		for (int m = 1; m < attrinfo.size(); m++) {
			// 判定该行数据是否为空
			if (null==attrinfo.get(m).get(0) || "".equals(attrinfo.get(m).get(0).toString().replace(" ", ""))){
				if (null==attrinfo.get(m).get(1) || "".equals(attrinfo.get(m).get(1).toString().replace(" ", ""))){
					continue;
				} else {// 标准问题为空
					pnull ++;
					r = 0;
					continue;
				}
			}
			
			// 去重
			String sql ="";
			sql = "select * from hotquestion s, hotquestion p where p.hotquestionid = s.parentid and p.question = '" 
				+ attrinfo.get(m).get(0).toString().replace(" ", "")
				+ "' and s.question ='"
				+ attrinfo.get(m).get(1).toString().replace(" ", "")
				+ "' and s.province='"
				+ ProvinceLocalMap.get(attrinfo.get(m).get(2))
				+ "' and s.city='"
				+ CityLocalMap.get(attrinfo.get(m).get(3))
				+ "'";
			Result result = Database.executeQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql );
			
			if (result != null && result.getRowCount() > 0){// 重复
				repeat ++;
				r = 0;
				continue;
			}
			
			String tempSql = "select hotquestionid from hotquestion where parentid is null and question ='"
					+ attrinfo.get(m).get(0).toString().replace(" ", "") 
					+ "'";
			Result rs = Database.executeQuery(tempSql);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + tempSql );
			
			//父亲问法存在
			if (rs != null && rs.getRowCount() > 0) {
				if (null==attrinfo.get(m).get(1) || "".equals(attrinfo.get(m).get(1).toString().replace(" ", ""))){
					// 同义问法不存在
					snull ++;
					r = 0;
					continue;
				}
				// 省份不存在
				if (null==attrinfo.get(m).get(2) || "".equals(attrinfo.get(m).get(2).toString().replace(" ", ""))){
					pronull ++;
					r = 0;
					continue;
				}
				// 城市存在 判断城市的合法性
				if (null!=attrinfo.get(m).get(3) && !"".equals(attrinfo.get(m).get(3).toString().replace(" ", ""))){
					String citycode = "";
					citycode = CityLocalMap.get(attrinfo.get(m).get(3).toString());
					if (citycode==null || citycode.equals("")){
						cityfail ++;
						r = 0;
						continue;
					} else {
						if (!citycode.startsWith(ProvinceLocalMap.get(attrinfo.get(m).get(2).toString()).substring(0,2)) || citycode.endsWith("0000")){
							cityfail ++;
							r = 0;
							continue;
						}
					}
				}
				//获得商家标识符
				String serviceType = user.getIndustryOrganizationApplication();
				String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
				String insql ="";
				if(GetConfigValue.isMySQL){
					insql = "insert into hotquestion (hotquestionid,question,parentid,province,city,uploadtime,username,servicetype) values ("
						+ ConstructSerialNum.getSerialIDNew("hotquestion","hotquestionid",bussinessFlag)
						+ ""
						+ ",'"
						+ attrinfo.get(m).get(1).toString().replace(" ", "")
						+ "',"
						+ rs.getRows()[0].get("hotquestionid")
						+ ",'"
						+ ProvinceLocalMap.get(attrinfo.get(m).get(2))
						+ "','"
						+ CityLocalMap.get(attrinfo.get(m).get(3))
						+ "',"
						+ "now(),'"
						+user.getUserID()
						+"','"
						+user.getIndustryOrganizationApplication()
						+"'"
						+")";
				} else{
					insql = "insert into hotquestion (hotquestionid,question,parentid,province,city,uploadtime,username,servicetype) values ("
						+ ConstructSerialNum.GetOracleNextValNew("hotquestionid_seq",bussinessFlag)
						+ ""
						+ ",'"
						+ attrinfo.get(m).get(1).toString().replace(" ", "")
						+ "',"
						+ rs.getRows()[0].get("hotquestionid")
						+ ",'"
						+ ProvinceLocalMap.get(attrinfo.get(m).get(2))
						+ "','"
						+ CityLocalMap.get(attrinfo.get(m).get(3))
						+ "',"
						+ "sysdate,'"
						+ user.getUserID()
						+ "','"
						+ user.getIndustryOrganizationApplication()
						+ "'"
						+")";
				}
				try {
					r = Database.executeNonQuery(insql);
					
					//文件日志
					GlobalValue.myLog.info(user.getUserID() + "#" + insql );
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				count += r;
			} 
			// 父亲问法不存在
				else {
					// 同义问法不存在
					if (null==attrinfo.get(m).get(1) || "".equals(attrinfo.get(m).get(1).toString().replace(" ", ""))){
						snull ++;
						r = 0;
						continue;
					}
					// 城市存在 判断城市的合法性
					if (null!=attrinfo.get(m).get(3) && !"".equals(attrinfo.get(m).get(3).toString().replace(" ", ""))){
						String citycode = "";
						citycode = CityLocalMap.get(attrinfo.get(m).get(3).toString());
						if (citycode==null || citycode.equals("")){
							cityfail ++;
							r = 0;
							continue;
						} else {
							if (!citycode.startsWith(ProvinceLocalMap.get(attrinfo.get(m).get(2).toString()).substring(0,2)) || citycode.endsWith("0000")){
								cityfail ++;
								r = 0;
								continue;
							}
						}
					}
					//获得商家标识符
					String serviceType = user.getIndustryOrganizationApplication();
					String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
					
					String seq = "";
					if(GetConfigValue.isMySQL){
						seq = ConstructSerialNum.getSerialIDNew("hotquestion","hotquestionid",bussinessFlag);
					} else{
						seq =  ConstructSerialNum.GetOracleNextValNew("hotquestionid_seq",bussinessFlag);
					}
					String insql = "";
					insql = "insert into hotquestion (hotquestionid,question,username,servicetype) values ("
							+ seq + "" 
							+ ",'" 
							+ attrinfo.get(m).get(0).toString().replace(" ", "") 
							+ "','"
							+ user.getUserID()
							+ "','"
							+ user.getIndustryOrganizationApplication()
							+ "'"
							+")";
					int innerr = 0;
					try {
						innerr = Database.executeNonQuery(insql);
						
						//文件日志
						GlobalValue.myLog.info(user.getUserID() + "#" + insql );
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (innerr > 0){
						if (null==attrinfo.get(m).get(2) || "".equals(attrinfo.get(m).get(2).toString().replace(" ", ""))){// 省份不存在
							pronull ++;
							r = 0;
							continue;
						}
						if(GetConfigValue.isMySQL){
							insql = "insert into hotquestion (hotquestionid,question,parentid,province,city,uploadtime,username,servicetype) values ("
								+ ConstructSerialNum.getSerialIDNew("hotquestion","hotquestionid",bussinessFlag)
								+ ""
								+ ",'"
								+ attrinfo.get(m).get(1).toString().replace(" ", "") 
								+ "'," 
								+ seq 
								+ ",'"
								+ ProvinceLocalMap.get(attrinfo.get(m).get(2))
								+ "','"
								+ CityLocalMap.get(attrinfo.get(m).get(3))
								+ "',"
								+ "now(),'"
								+ user.getUserID()
								+ "','"
								+ user.getIndustryOrganizationApplication()
								+ "'"
								+")";
						} else {
							insql = "insert into hotquestion (hotquestionid,question,parentid,province,city,uploadtime,username,servicetype) values ("
								+ ConstructSerialNum.GetOracleNextValNew("hotquestionid_seq",bussinessFlag)
								+ ",'"
								+ attrinfo.get(m).get(1).toString().replace(" ", "") 
								+ "'," 
								+ seq 
								+ ",'"
								+ ProvinceLocalMap.get(attrinfo.get(m).get(2))
								+ "','"
								+ CityLocalMap.get(attrinfo.get(m).get(3))
								+ "',"
								+ "sysdate,'"
								+ user.getUserID()
								+ "','"
								+ user.getIndustryOrganizationApplication()
								+ "'"
								+ ")";
						}
						try {
							r = Database.executeNonQuery(insql);
							
							//文件日志
							GlobalValue.myLog.info(user.getUserID() + "#" + insql );
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						count += r;
					}
				}
		}
		int[] rs ={r,count,repeat,pnull,snull,pronull,cityfail}; 
		return rs;
	}

	/**
	 * 查询问法
	 * @param question
	 * @param starttime
	 * @param endtime
	 * @param status
	 * @param selProvince
	 * @param selCity
	 * @param hot
	 * @param hot2
	 * @param pid
	 * @param user
	 * @param locString
	 * @return
	 */
	public static Result gethotquestion2(String question, String starttime,
			String endtime, String status, String selProvince, String selCity,
			String hot, String hot2, Integer pid,User user,String locString) {
		String sql = "select aa.hotquestionid as pid  ,aa.fquestion as question ,count(aa.cquestion) as status from (select s.question fquestion ,p.question  cquestion ,s.hotquestionid  from (select * from hotquestion where parentid is null and hot='yes' and servicetype='"+user.getIndustryOrganizationApplication()+"') s left join (select * from hotquestion where parentid is not null and servicetype='"+user.getIndustryOrganizationApplication()+"' ";
		
//		String rolename = user.getRoleList().get(0).getRoleName().toString().replace("管理员", "");
//		String bsname = user.getCustomer().split("->")[1];
		
//		if(!"云平台组长".equals(rolename)&&!bsname.equals(rolename)){
//			sql+= " and province='" + rolename + "'"; 
//		}
		
		if(locString.length() > 0){
			if (!locString.contains("全国")){
				sql += "and province in (" + locString + ")"; 
			}
		}else {
			sql += "and province in ('')";
		}
		
		sql += ") p on s.hotquestionid=p.parentid and p.STATUS=-1)aa  group by aa.fquestion, aa.hotquestionid ";
		if (null != question && !"".equals(question)){
			sql = "select * from (" + sql +") qu where question like '%" + question +"%'";
		}
		
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql );
		
		return rs;
	}

	/**
	 * 分页查询问法
	 * @param page
	 * @param rows
	 * @param question
	 * @param starttime
	 * @param endtime
	 * @param status
	 * @param selProvince
	 * @param selCity
	 * @param hot
	 * @param hot2
	 * @param pid
	 * @param user
	 * @param locString
	 * @return
	 */
	public static Result gethotquestion2(int page, int rows, String question,
			String starttime, String endtime, String status,
			String selProvince, String selCity, String hot, String hot2,
			Integer pid,User user, String locString) {
		String sql = "select aa.hotquestionid as pid  ,aa.fquestion as question ,count(aa.cquestion) as status from (select s.question fquestion ,p.question  cquestion ,s.hotquestionid  from (select * from hotquestion where parentid is null and hot='yes' and servicetype='"+user.getIndustryOrganizationApplication()+"' order by uploadtime desc) s left join (select * from hotquestion where parentid is not null and servicetype='"+user.getIndustryOrganizationApplication()+"' ";
		String rolename = user.getRoleList().get(0).getRoleName().toString().replace("管理员", "");
		String bsname = user.getIndustryOrganizationApplication().split("->")[1];
		
//		if(!"云平台组长".equals(rolename)&&!bsname.equals(rolename)){
//			sql+= " and province='" + rolename + "'"; 
//		}
		
		if(locString.length() > 0){
			if (!locString.contains("全国")){
				sql += "and province in (" + locString + ")"; 
			}
		}else {
			sql += "and province in ('')";
		}
		
		sql += ") p on s.hotquestionid=p.parentid and p.STATUS=-1)aa  group by aa.fquestion, aa.hotquestionid ";
		
		if (null != question && !"".equals(question)){
			sql = "select * from (" + sql +") qu where question like '%" + question +"%'";
		}
		
		if (GetConfigValue.isOracle) {
			sql = "select * from (select t.*, rownum  rn from (" + sql
					+ ") t where rownum<=" + page * rows + " ) where rn>"
					+ (page - 1) * rows;
		} else {
			sql = sql + " limit " + (page - 1) * rows + "," + page * rows;
		}
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql );
		
		return rs;
	}

	/**
	 * 查询同义问法
	 * @param question
	 * @param pid
	 * @param status
	 * @param user
	 * @param locString
	 * @return
	 */
	public static Result getsonquestion(String question,String pid,String status,User user,String locString) {
		String sql = "";
		
		sql = "select count(*) as total from hotquestion where parentid=" + pid+" and servicetype='"+user.getIndustryOrganizationApplication()+"'";
//		String rolename = user.getRoleList().get(0).getRoleName().toString().replace("管理员", "");
//		String bsname = user.getCustomer().split("->")[1];
//		if(!"云平台组长".equals(rolename)&&!bsname.equals(rolename)){
//			sql+= " and province='" + rolename + "'"; 
//		}
		if(locString.length() > 0){
			if (!locString.contains("全国")){
				sql += " and province in (" + locString + ")"; 
			}
		} else {
			sql += " and province in ('')";
		}
		if (null != question && !"".equals(question)){
			sql += " and question like'%" + question + "%'";
		}
		if (null != status && !"".equals(status)){
			sql += " and status ='" + status + "'";
		}
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql );
		
		return rs;
	}

	/**
	 * 分页查询同义问法
	 * @param page
	 * @param rows
	 * @param question
	 * @param pid
	 * @param status
	 * @param user
	 * @param locString
	 * @return
	 */
	public static Result getsonquestion(int page, int rows,String question, String pid,String status,User user,String locString) {
		String sql = "";
		sql = "select * from hotquestion where parentid=" + pid + " and servicetype='"+user.getIndustryOrganizationApplication()+"'";
		String rolename = user.getRoleList().get(0).getRoleName().toString().replace("管理员", "");
		String bsname = user.getIndustryOrganizationApplication().split("->")[1];
//		if(!"云平台组长".equals(rolename)&&!bsname.equals(rolename)){
//			sql+= " and province='" + rolename + "'"; 
//		}
		if(locString.length() > 0){
			if (!locString.contains("全国")){
				sql += "and province in (" + locString + ")"; 
			}
		}else {
			sql += "and province in ('')";
		}
		if (null != question && !"".equals(question)){
			sql = "select * from (" + sql + ") where question like'%" + question + "%'";
		}
		if (null != status && !"".equals(status)){
			sql = "select * from (" + sql + ") where status ='" + status + "'";
		}
		sql += " order by uploadtime desc";
		if (GetConfigValue.isOracle) {
			sql = "select * from (select t.*, rownum  rn from (" + sql
					+ ") t where rownum<=" + page * rows + " ) where rn>"
					+ (page - 1) * rows;
		} else {
			sql = sql + " limit " + (page - 1) * rows + "," + page * rows;
		}
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info(user.getUserID() + "#" + sql );
		
		return rs;
	}

	/**
	 * 新增同义问法
	 * @param question
	 * @param pid
	 * @param user
	 * @param provinceid
	 * @param city
	 * @return
	 */
	public static int insertother(String question,Integer pid,User user,String provinceid,String city) {
		String innerSql = "";
		innerSql = "select * from (select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' order by s.metafieldid) a where province like '%市' or province like '%区' or province like '%省'";
		innerSql = "select * from (" + innerSql + ") iid where id='" + provinceid +"'";
		Result innerRs = Database.executeQuery(innerSql);
//		String province = innerRs.getRows()[0].get("province").toString();
		String province = provinceid;
		//获得商家标识符
		String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		String sql = "";
		if(GetConfigValue.isMySQL){
			sql = "insert into hotquestion (hotquestionid,question,parentid,uploadtime,username,servicetype,province,city) values ("
				+ ConstructSerialNum.getSerialIDNew("hotquestion","hotquestionid",bussinessFlag)
				+ ""
				+ ",'"
				+ question 
				+ "'," 
				+ pid 
				+ ",now(),'"
				+ user.getUserID()
				+ "','"
				+ user.getIndustryOrganizationApplication()
				+ "','"
				+ province
				+ "','"
				+ city
				+ "'"
				+")";
		} else {
			sql = "insert into hotquestion (hotquestionid,question,parentid,uploadtime,username,servicetype,province,city) values ("
				+ ConstructSerialNum.GetOracleNextValNew("hotquestionid_seq",bussinessFlag)
				+ ""
				+ ",'"
				+ question + "'," 
				+ pid 
				+ ",sysdate,'"
				+ user.getUserID()
				+ "','"
				+ user.getIndustryOrganizationApplication()
				+ "','"
				+ province
				+ "','"
				+ city
				+ "'"
				+")";
		}
		int c = 0;
		try {
			c = Database.executeNonQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info(user.getUserID() + "#" + sql );
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 批量理解
	 * @param result
	 * @param hotquestionid
	 * @param flag
	 * @return
	 */
	public static int understand(String result, String hotquestionid, String flag)  {
		String sql ="update hotquestion set result='" + result + "',flag='" + flag + "' where hotquestionid ='" + hotquestionid + "'";
		int c = 0;
		try {
			c = Database.executeNonQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 删除问法
	 * @param sid
	 * @return
	 */
	public static int delOther(int sid){
		String sql = "delete from hotquestion where hotquestionid = " + sid;
		int c = 0;
		try {
			c = Database.executeNonQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 修改问法
	 * @param sid
	 * @param other
	 * @return
	 */
	public static int updateQueName(int sid,String other) {
		String sql = "";
		int c = 0;
		sql = "update hotquestion set question='" + other + "' where hotquestionid=" + sid;
		try {
			c = Database.executeNonQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 批量删除问法
	 * @param ids
	 * @return
	 */
	public static int deleteOther(String ids) {
		String sql = "delete from hotquestion where hotquestionid in (" + ids +")";
		int c = 0;
		try {
			c = Database.executeNonQuery(sql);
			
			//文件日志
			GlobalValue.myLog.info( sql );
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return c;
	}

	/**
	 * 查询地市编码配置
	 * @return
	 */
	public static Result createLocal() {
		String sql = "select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid order by id";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 查询31省
	 * @return
	 */
	public static Result createLocalProvince() {
		String sql = "select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '%0000' order by id";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 查询市
	 * @return
	 */
	public static Result createLocalCity() {
		String sql = "select t.name as id,s.name as province from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name not like '%0000' order by id";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}
	
	/**
	 * 统计pv量
	 * @param user
	 * @param ProvinceLocalMap
	 * @return
	 */
	public static String pvCount(User user,Map<String,String> ProvinceLocalMap){
		
		String userId=user.getUserID(); // 前台传到后台的UserID（登陆账号）
		String customer = user.getCustomer();
		String province = ""; 
		if ("全行业".equals(customer)){
			province = "北京";
		} else {
			HashMap<String, ArrayList<String>> resourseMap = CommonLibPermissionDAO
			.resourseAccess(user.getUserID(), "scenariosrules", "S");
			List<String> cityList = new ArrayList<String>();
			cityList = resourseMap.get("地市");
			province = cityList.get(0);
		}
//		String province=user.getRoleList().get(0).getRoleName().toString().replace("管理员", ""); // 登陆账号的省份
//		if ("电信集团".equals(province)||"云平台组长".equals(province)){
//			province = "北京";
//		}
		String city="";     // 登陆账号所在省份的省会城市
		
		String nProvince = province.replace("0000", "0100");
		Result rs = CommonLibQuestionUploadDao.getCapital(nProvince);
		if (rs != null && rs.getRowCount() > 0)	{
			for (int j = 0;j<rs.getRowCount();j++){
				if(city.length()<rs.getRows()[j].get("city").toString().length()){
					city = rs.getRows()[j].get("city").toString();
				}
			}
		}
		if (province.equals("北京")){
			city = "北京";
		} else if(province.equals("上海")){
			city = "上海";
		} else if(province.equals("天津")){
			city = "天津";
		} else if(province.equals("重庆")){
			city = "重庆";
		} else if(province.equals("电渠")){
			city = "电渠";
		} else if(province.equals("集团")){
			city = "集团";
		} 
		
		city = city.replace("市", "");
		List<List<?>> listParams = new ArrayList<List<?>>();
		List<Object> param = new ArrayList<Object>();
		//获得商家标识符
		String serviceType = user.getIndustryOrganizationApplication();
		String bussinessFlag = CommonLibMetafieldmappingDAO.getBussinessFlag(serviceType);
		// 获取插入相似问题的序列
		String id = "";
		if (GetConfigValue.isOracle) {
			id = ConstructSerialNum.GetOracleNextValNew("TelePV", bussinessFlag);
		} else if (GetConfigValue.isMySQL) {
			id = ConstructSerialNum.getSerialIDNew("TELECOM_PV", "id", bussinessFlag);
		}
		param.add(id);
		java.util.Date dt = new  java.util.Date();
		java.sql.Timestamp date = new java.sql.Timestamp(dt.getTime());
		param.add(date);
		param.add("web");
		param.add("问法上传");
		param.add(userId);
		param.add(province);
		param.add(city);
		param.add("");
		param.add("View");
		listParams.add(param);
		String sql = "insert into TELECOM_PV(ID,PVDATE,CHANNEL,APPLYNAME,USERID,PROVINCE,CITY,LINKCONTEXT,CATEGORY)"+
		" values (?,?,?,?,?,?,?,?,?)";
		
		//文件日志
		GlobalValue.myLog.info( sql + "#" + param );
		
		int result = Database.executeNonQueryBatchTransaction(sql, listParams);
		return "success";
	}

	/**
	 * 查询省
	 * @param nProvince
	 * @return
	 */
	public static Result getCapital(String nProvince) {
		String sql = "select t.name as id,s.name as city from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name ='" + nProvince + "' order by id";
		Result rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 查询问法
	 * @param question
	 * @param other
	 * @param starttime
	 * @param endtime
	 * @param username
	 * @param status
	 * @param selProvince
	 * @param selCity
	 * @param user
	 * @param locString
	 * @return
	 */
	public static Result gethotquestionnondown(String question, String other, String starttime, String endtime, String username, String status, String selProvince,String selCity, User user, String locString) {
		String sql = "";
		if(GetConfigValue.isMySQL){
			sql = "SELECT p.hotquestionid as pid, s.hotquestionid as sid,p.question as question,s.question as other,s.uploadtime,s.username,s.province,s.city,s.result,s.status,p.hot,s.hot as hot2,s.reason,s.solution,s.flag FROM hotquestion s, hotquestion p WHERE s.parentid=p.hotquestionid and s.servicetype='"+user.getIndustryOrganizationApplication()+"'";
		} else {
			sql = "SELECT p.hotquestionid as pid, s.hotquestionid as sid,p.question as question,s.question as other,s.uploadtime,s.username,s.province,s.city,s.result,s.status,p.hot,s.hot as hot2,s.reason,s.solution,s.flag FROM hotquestion s, hotquestion p WHERE s.parentid=p.hotquestionid and s.servicetype='"+user.getIndustryOrganizationApplication()+"'";
		}
		if (null != question && !question.equals("")) {
			sql = "select * from (" + sql + ") q where question like '%"
					+ question + "%'";
		}
		if (null != other && !other.equals("")) {
			sql = "select * from (" + sql + ") ot where other like '%"
					+ other + "%'";
		}
		if (null != starttime && !starttime.equals("")) {
			sql = "select * from (" + sql + ") st where uploadtime>= to_date('"
			+ starttime +" 00:00:00" + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (null != endtime && !endtime.equals("")) {
			sql = "select * from (" + sql + ") et where uploadtime<= to_date('"
					+ endtime + "23:59:59" + "','yyyy-mm-dd hh24:mi:ss')";
		}
		if (null != username && !username.equals("")) {
			sql = "select * from (" + sql + ") us where username like '%"
					+ username + "%'";
		}
		String rolename = user.getRoleList().get(0).getRoleName().toString().replace("管理员", "");
		String bsname = user.getIndustryOrganizationApplication().split("->")[1];
//		if("云平台组长".equals(rolename)||bsname.equals(rolename)){
			if (null != selProvince && !selProvince.equals("")) {
				sql = "select * from (" + sql + ") sel where province = '" + selProvince
				+ "'";
			}
//		} else {
//			sql = "select * from (" + sql + ") sel where province = '" + rolename
//			+ "'";
//		}
		if (null != selCity && !selCity.equals("")) {
			sql = "select * from (" + sql + ") sel where city = '" + selCity
			+ "'";
		}
		if(locString.length() > 0){
			if (!locString.contains("全国")){
				sql = "select * from (" + sql + ") ls where province in (" + locString + ")"; 
			}
		}else {
			sql ="select * from (" + sql + ") ls where province in ('')";
		}
		if (null != status && !status.equals("")) {
			sql = "select * from (" + sql + ") sta where status = '" + status
					+ "'";
		}
		if (GetConfigValue.isOracle) {
			sql = "SELECT pid,sid,question,other,to_char(uploadtime,'yyyy-mm-dd') as uploadtime,username,province,city,result,status,hot,hot2,reason,solution,flag FROM (" + sql + ") al where 1=1";
			
		} else {
			sql = "SELECT pid,sid,question,other,DATE_FORMAT(uploadtime,'%Y-%m-%d') as uploadtime,username,province,city,result,status,hot,hot2,reason,solution,flag FROM (" + sql + ") al where 1=1";
		}
		
		Result rs = null;
		rs = Database.executeQuery(sql);
		
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return rs;
	}

	/**
	 * 自动审核
	 * @param rolename
	 * @param sid
	 * @return
	 */
	public static int autosol(String rolename, String sid) {
		String sql = "update hotquestion set solution='标准问法与同义问法已一致',status='1' where hotquestionid=" + sid + "and status='-1'";
		int r= 0;
		try {
			r = Database.executeNonQuery(sql);
			//文件日志
			GlobalValue.myLog.info( sql );
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return r;
	}

	/**
	 * 获取城市
	 * @param cityId
	 * @return
	 */
	public static Result getScity(String cityId) {
		String sql = "";
		cityId = cityId.substring(0, 4);
		sql = "select t.name as id,min(s.name) as city from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '"
				+ cityId + "%' and t.name fnot like '%00' and t.name not like '%01' group by t.name order by t.name";
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

	/**
	 * 获取区
	 * @param cityId
	 * @return
	 */
	public static Result getzCity(String cityId) {
		String sql = "";
		cityId = cityId.substring(0, 2);
		sql = "select t.name as id,min(s.name) as city from metafield t,metafield s,metafieldmapping a where a.name='地市编码配置' and t.metafieldmappingid=a.metafieldmappingid and t.metafieldid=s.stdmetafieldid and  t.name like '"
				+ cityId + "____' and t.name not like '%0_00' group by t.name order by t.name";
		Result rs = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		return rs;
	}

}
