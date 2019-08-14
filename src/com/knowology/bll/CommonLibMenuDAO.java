package com.knowology.bll;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.lang.StringUtils;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;




public class CommonLibMenuDAO {
	
	/**
	 *描述：@description  获取菜单结果集
	 *参数：@return
	 *返回值类型：@returnType Result
	 *创建时间：@dateTime 2015-9-18下午12:35:25
	 *作者：@author wellhan
	 */
	public static Result getMenuinfo(String containsMenuName){
		String sql ="";
		if (GetConfigValue.isMySQL) {
			sql = " select * from t_menuhx where parentid in(select MENUID from t_menuhx where PARENTID in(-1,0)) or parentid = -1 order by menuid  ";
		} else if(GetConfigValue.isOracle)  {
				sql = " select * from t_menuhx   start with menuid = 0 connect by prior menuid= parentid order by menuid";
		}
		if(!"".equals(containsMenuName)){
			sql = "select * from ( "+ sql +" ) a where a.menu in ("+containsMenuName+")";
		}
		Result res = Database.executeQuery(sql);
		//文件日志
		GlobalValue.myLog.info( sql );
		
		return res;
	}
	
	/**
	 * 根据父节点获取子节点菜单
	 * @return
	 */
	public static Result getMenuByPId(String pId){
		
		String sql ="select * from t_menuhx where parentid=?";
		Result res = Database.executeQuery(sql,pId);
		//文件日志
		GlobalValue.myLog.info( sql );
		return res;
	}
	
	/**
	 * 根据父节点获取子节点菜单，并且过滤名称
	 * @return
	 */
	public static Result getMenuByPIdAndName(String pId,String menu){
		
		String sql ="select * from t_menuhx where parentid=? and menu like ? ";
		Result res = Database.executeQuery(sql,pId,'%'+menu+'%');
		//文件日志
		GlobalValue.myLog.info( sql );
		return res;
	}
	
	/**
	 * 获取菜单名称
	 * @return
	 */
	public static Result getMenuName(String id){
		String sql ="select * from t_menuhx where menuid=?";
		Result res = Database.executeQuery(sql,id);
		//文件日志
		GlobalValue.myLog.info( sql );
		return res;
	}
	
	/**
	 * 删除菜单
	 * @return
	 */
	public static int deleteMenu(String id){
		StringBuilder sql = new StringBuilder();
		//sql语句
		sql.append("delete from t_menuhx where menuid = ?");
		List<String> listSqls = new ArrayList<String>();
		listSqls.add(sql.toString());
		
		//构建参数
		List<List<?>> listParams = new ArrayList<List<?>>();
		List<String> listParam = new ArrayList<String>();
		listParam.add(id);
		listParams.add(listParam);
		int i = Database.executeNonQueryTransaction(listSqls, listParams);
		//文件日志
		GlobalValue.myLog.info(sql.toString());
		return i;
	}
	
	/**
	 * 增加一个菜单
	 * @param menu
	 * @param parentId
	 * @param url
	 * @return
	 */
	public static int addMenu(String menu,String menuId,String parentId,String url){
		
		Result rs = Database.executeQuery("select * from t_menuhx where menu = ?", menu);
		//存在重复的菜单名称
		if(rs != null && rs.getRowCount() > 0){
			return 0;
		}
		rs = Database.executeQuery("select * from t_menuhx where menuId = ?", menuId);
		//存在重复的菜单id
		if(rs != null && rs.getRowCount() > 0){
			return -1;
		}
		
		
		//生成主键
//		String menuId = Database.executeQuery("select t_menuhx_bak_seq.nextval from dual").getRows()[0].get("nextval").toString();
		
		StringBuilder sql = new StringBuilder();
		sql.append("insert into t_menuhx (menuid,menu,url,parentid) ");
		sql.append("values (?,?,?,?)");
		
		if(StringUtils.isEmpty(url)){
			url = "";
		}
		
		List<String> listSqls = new ArrayList<String>();
		listSqls.add(sql.toString());
		
		//构建参数
		List<List<?>> listParams = new ArrayList<List<?>>();
		List<String> listParam = new ArrayList<String>();
		listParam.add(menuId);
		listParam.add(menu);
		listParam.add(url);
		listParam.add(parentId);
		listParams.add(listParam);
		int i = Database.executeNonQueryTransaction(listSqls, listParams);
		GlobalValue.myLog.info(sql.toString());
		return i;
	}
	

	/**
	 * 更新菜单
	 * @param menuId
	 * @param menu
	 * @param url
	 * @return
	 */
	public static int updateMenu(String menuId,String menu,String url){
		
		StringBuilder sql = new StringBuilder();
		//sql语句
		sql.append("update t_menuhx set menu=? , url=? where menuid = ? ");
		List<String> listSqls = new ArrayList<String>();
		listSqls.add(sql.toString());
		
		//构建参数
		List<List<?>> listParams = new ArrayList<List<?>>();
		List<String> listParam = new ArrayList<String>();
		listParam.add(menu);
		listParam.add(url);
		listParam.add(menuId);
		listParams.add(listParam);
		int i = Database.executeNonQueryTransaction(listSqls, listParams);
		GlobalValue.myLog.info(sql.toString());
		
		return i;
		
	}
	
	/**
	 * 更新菜单的父节点
	 * @param menu
	 * @param parentId
	 * @return
	 */
	public static int updateMenuParent(String menuId,String parentId){
		
		StringBuilder sql = new StringBuilder();
		//sql语句
		sql.append("update t_menuhx set parentid=? where menuid = ? ");
		List<String> listSqls = new ArrayList<String>();
		listSqls.add(sql.toString());
		
		List<List<?>> listParams = new ArrayList<List<?>>();
		List<String> listParam = new ArrayList<String>();
		listParam.add(parentId);
		listParam.add(menuId);
		listParams.add(listParam);
		int i = Database.executeNonQueryTransaction(listSqls, listParams);
		GlobalValue.myLog.info(sql.toString());
		
		return i;
	}
	
	/**
	 * 构建菜单树
	 * @param menu
	 * @param parentId
	 * @return
	 */
	public static Result buildMenuTree(String menuId){
		
		String sql= " select * from t_menuhx  start with menuid = ? connect by prior menuid= parentid order by menuid";
		
		Result res = Database.executeQuery(sql,menuId);
		GlobalValue.myLog.info(sql.toString());
		
		return res;
	}
	
	/**
	 * 获取所有菜单项，剔除根菜单
	 * @param menu
	 * @param parentId
	 * @return
	 */
	public static Result getAllMenu(){
		
		String sql= "select  a.menu MENU  from (select menu from t_menuhx  start with menuid = 0 connect  by  prior  menuid  = parentid order by menuid  ) a where a.menu not in('系统管理','词库知识','通用问法复用','问答训练','知识管理','帮助')";
		
		Result res = Database.executeQuery(sql);
		GlobalValue.myLog.info(sql.toString());
		
		return res;
	}

}
