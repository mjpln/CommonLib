package com.knowology.permission;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.jstl.sql.Result;
import com.knowology.bll.CommonLibPermissionDAO;





public class MetafieldmappingManage {

	private static String []provinceArray={"北京","天津","重庆","上海","河北","山西","吉林","辽宁","黑龙江","陕西","甘肃","青海","山东","福建","浙江","河南","湖北","湖南","江西","江苏","安徽","广东","海南","四川","贵州","云南","内蒙古","新疆","宁夏","广西","西藏","台湾","香港","澳门"};
	private static HashSet<String> provinceSet=new HashSet<String>();
	
	static{
		for (int i = 0; i < provinceArray.length; i++) {
			provinceSet.add(provinceArray[i]);
		}
	}
	
	/**
	 * 获取某一种配置下的所有映射，例如“地市编码配置” 、“资源表名到呈现名称映射配置”
	 * @param name
	 * @return “地市编码配置” 第一个是编码第二个是汉字名称   “资源表名到呈现名称映射配置”第一个是汉字名称 第二个是对应数据库表名
	 */
	public static HashMap<String, String> getMapConfigValue(String name){
		HashMap<String, String> resultHashMap=new HashMap<String, String>();
//		Result rs = MetafieldmappingDAO.getConfigValue(name, "北京市");
		Result rs = MetafieldmappingDAO.getMapConfigValue(name);
		try {
			if (rs != null && rs.getRowCount() > 0) {
				Object resultArray[][]=rs.getRowsByIndex();
				for (int i = 0; i < resultArray.length; i++) {
					String key=(String)resultArray[i][0];
					String value=(String)resultArray[i][1];
					String valueExisting="";
					if (resultHashMap.containsKey(key)) {
						valueExisting=resultHashMap.get(key);
						if (provinceSet.contains(valueExisting)) {
							value=valueExisting;
						}
					}
					resultHashMap.put(key, value);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resultHashMap;
	}
	
	
	/**
	 * 获取用户能够操作的城市，并表示成树形结构，并用json的形式表示
	 * @param useId 用户的id
	 * @param isAllCity 是否返回所有城市，此标记为true就会返回全国所有城市
	 * @param resourceType 需要获取权限的资源类型
	 * @param level 树形结构的层次，1只返回所有的省，2返回到市
	 * @return
	 */
	public static String getCityTree(String useId, boolean isAllCity,String resourceType,int level){
		//获取省市编码到省市名称的映射
		HashMap<String, String> code2CityNameMap= getMapConfigValue("地市编码配置");
		HashSet<String> cityConstraintSet=new HashSet<String>();
		//获取角色能够操作的城市
		if (!isAllCity) {//如果需要根绝用户的角色限制来建立城市树结构，只获取用户能够操作的城市
			//A、D、U、S四种操作
			HashMap<String, ArrayList<String>> AMap=CommonLibPermissionDAO.resourseAccess(useId, resourceType, "A");//第二个参数是方芳的表格数据库名称，
			HashMap<String, ArrayList<String>> DMap=CommonLibPermissionDAO.resourseAccess(useId, resourceType, "D");//第二个参数是方芳的表格数据库名称，
			HashMap<String, ArrayList<String>> UMap=CommonLibPermissionDAO.resourseAccess(useId, resourceType, "U");//第二个参数是方芳的表格数据库名称，
			HashMap<String, ArrayList<String>> SMap=CommonLibPermissionDAO.resourseAccess(useId, resourceType, "S");//第二个参数是方芳的表格数据库名称，
			if (AMap.containsKey("地市")) {
				cityConstraintSet.addAll(AMap.get("地市"));
			}
			if (DMap.containsKey("地市")) {
				cityConstraintSet.addAll(DMap.get("地市"));
			}
			if (UMap.containsKey("地市")) {
				cityConstraintSet.addAll(UMap.get("地市"));
			}
			if (SMap.containsKey("地市")) {
				cityConstraintSet.addAll(SMap.get("地市"));
			}
		}
		if(cityConstraintSet.contains("全国")||isAllCity)	{//不需要用户限制则获取所有的城市编码
			Iterator iter = code2CityNameMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				String key = (String)entry.getKey();
				cityConstraintSet.add(key);
			}
		}
		//获取城市的tree树结构
		HashMap<String, Object> cityRootMap=getCityTreefromCitySet(cityConstraintSet);
		//遍历树结构，获取json串
		String jsonCity=CityTree2TreeJson(code2CityNameMap, cityRootMap, level, 1);
		if (jsonCity.endsWith(",")) {
			jsonCity=jsonCity.substring(0,jsonCity.length()-1);
		}
		if("report".equals(resourceType)){
			jsonCity="[{\"id\":\"全国\",\"text\":\"全国\",\"children\":"+"["+jsonCity+"]"+"}]";
		}else{
			jsonCity="["+jsonCity+"]";
		}
		return jsonCity;
	}
	
	/**
	 * 递归生成城市列表的树形结构
	 * @param code2CityNameMap
	 * @param cityRootMap
	 * @param level
	 * @param curLevel
	 * @return
	 */
	public static String CityTree2TreeJson(HashMap<String, String> code2CityNameMap,HashMap<String, Object> cityRootMap,int level,int curLevel){
		if (curLevel > level) {
			return "";
		}
		if (cityRootMap == null || cityRootMap.size() == 0)
			return "";
		String json = "";
		Object[] key = cityRootMap.keySet().toArray();
		Arrays.sort(key);
		for (int i = 0; i < key.length; i++) {
			 json =json+ "{"+
					"\"id\":\""+key[i]+"\","+
					"\"text\":\""+code2CityNameMap.get(key[i])+"\"";
			 HashMap<String, Object> childCityMap=(HashMap<String, Object>)cityRootMap.get(key[i]);
			 if (childCityMap.size()!=0 && curLevel<level) {
				 json =json+",\"state\":\"closed\""+",\"children\":[";
				 json=json+CityTree2TreeJson(code2CityNameMap, childCityMap, level, curLevel+1);
				 if (json.endsWith(",")) {
					json=json.substring(0, json.length()-1);
				}
				json =json+"]";
			}
			 json=json+"},";
		}
		return json;
	}
	
	/**
	 * 获取城市的trie树结构
	 * @param cityConstraintSet 城市编码集合
	 * @return
	 */
	public static HashMap<String, Object> getCityTreefromCitySet(HashSet<String> cityConstraintSet){
		HashMap<String, Object> rootMap=new HashMap<String, Object>();
		for (Iterator iterator = cityConstraintSet.iterator(); iterator.hasNext();) {
			String cityCode = (String) iterator.next();
			if (cityCode.equals("全国")) {//编码库里面“全国的编码”就是“全国”过滤掉
				continue;
			}
			if (cityCode.length()!=6) {
				System.out.println(cityCode+" 此地市编码有误！");
				return null;
			}
			String char12=cityCode.substring(0,2);
			String char34=cityCode.substring(2,4);
			String char56=cityCode.substring(4,6);
			HashMap<String, Object> curMap=rootMap;
			if (!curMap.containsKey(char12+"0000")) {
				curMap.put(char12+"0000", new HashMap<String, Object>());
			}
			curMap=(HashMap<String, Object>)curMap.get(char12+"0000");
			if (!"00".equals(char34)) {
				if (!curMap.containsKey(char12+char34+char56)) {
					curMap.put(char12+char34+char56, new HashMap<String, Object>());
				}
//				curMap=(HashMap<String, Object>)curMap.get(char12+char34+"00");
			}
//			if (!"00".equals(char56)) {
//				if (!curMap.containsKey(cityCode)) {
//					curMap.put(cityCode, new HashMap<String, Object>());
//				}
//			}
		}
		return rootMap;
	}
	
	/**
	 * 根据code构造地市树
	 * @param codeSet
	 * @return
	 */
	public static String getCityTreeByCode(HashSet<String> codeSet){
		//获取省市编码到省市名称的映射
		HashMap<String, String> code2CityNameMap= getMapConfigValue("地市编码配置");
		//获取城市的trie树结构
		HashMap<String, Object> cityRootMap=getCityTreefromCitySet(codeSet);
		//遍历树结构，获取json串
		String jsonCity=CityTree2TreeJson(code2CityNameMap, cityRootMap, 2, 1);
		if (jsonCity.endsWith(",")) {
			jsonCity=jsonCity.substring(0,jsonCity.length()-1);
		}
		jsonCity="["+jsonCity+"]";
		return jsonCity;
	}
	
	public static void main(String[] args) {
		String str = MetafieldmappingManage.getCityTree("23211", true, "service",2);;
		System.out.println(str);
	}
}


