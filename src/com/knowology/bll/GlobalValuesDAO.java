package com.knowology.bll;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.dal.Database;

public class GlobalValuesDAO {
	public static Result LoadEliminateSpecIdent(){
		Result rs = null;
		String sql = "select word from wordclass ,word where wordclass.wordclassid = word.wordclassid  and wordclass = '排除特殊符号父类'";
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static Result LoadChannel2ServicesMap(){
		Result rs = null;
		String sql = "select mf1.name mkey,mf1.type keytype,mf2.name mvalue,mf2.type valuetype from metafield mf1,metafield mf2 where mf1.metafieldid = mf2.stdmetafieldid and mf1.metafieldmappingid =(select metafieldmappingid from metafieldmapping where name='服务与业务范围对应关系配置')";
		
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static Result newLoadChannel2ServicesMap(){
		Result rs = null;
		String sql = "select * from m_industryapplication2services";
			
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static Result newLoadServicesMap(){
		Result rs = null;
		String sql = "select * from service";
			
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public static Result LoadOldBusi2NBusinessMap(){
		Result rs = null;
		String sql = "select mf1.name mkey,mf1.type keytype,mf2.name mvalue,mf2.type valuetype from metafield mf1,metafield mf2 where mf1.metafieldid = mf2.stdmetafieldid and mf1.metafieldmappingid =(select metafieldmappingid from metafieldmapping where name='原服务名称与新服务名称对应关系配置')";
			
		try{ 
			rs = Database.executeQuery(sql);
		} catch(Exception e) {
			e.printStackTrace();
		}
		return rs;
	}
}
