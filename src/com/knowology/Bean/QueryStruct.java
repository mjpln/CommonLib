package com.knowology.Bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *内容摘要：咨询结构体类，主要用来存储咨询的结构化信息
 * 
 *类修改者： 修改说明：
 * 
 * @ClassName：QueryStruct
 *@Company：knowology
 *@Author：zhanggang
 *@Date：2013-06-20 14：42
 *@Version: V1.0
 */
public class QueryStruct {
	private String userID = "";// 用户ID
	private String query = "";// 咨询问题
	private String business = "sys";// 服务 ： {}
	private String channel = "";// 渠道 ： {}
	private String callTime = "";// 访问时间
	private ArrayList<HashMap<String, ArrayList<String>>> paras = new ArrayList<HashMap<String, ArrayList<String>>>();// 控制值集合

	public QueryStruct() {
	}

	public QueryStruct(QueryStruct qs) {
		userID = qs.userID;
		query = qs.query;
		channel = qs.channel;
		callTime = qs.callTime;
		business = qs.business;
		//paras
		for(int i=0;i<qs.paras.size();i++)
		{
			HashMap<String,ArrayList<String>> tmpMap=new HashMap<String,ArrayList<String>>();
			for(String key : qs.paras.get(i).keySet())
			{
				ArrayList<String> tmpList=new ArrayList<String>();
				tmpList.addAll(qs.paras.get(i).get(key));
				tmpMap.put(key, tmpList);
			}
			this.paras.add(tmpMap);
		}
		//--
	}
	
	/**
	 * 
	 *描述：通过QS赋值
	 *@author: qianlei
	 *@date： 日期：2015-4-22 时间：下午09:01:51
	 *@return void
	 */
	public void newQueryStruct(QueryStruct qs)
	{
		this.clear();
		this.userID = qs.userID;
		this.query = qs.query;
		this.channel = qs.channel;
		this.callTime = qs.callTime;
		this.business = qs.business;
		//paras
		for(int i=0;i<qs.paras.size();i++)
		{
			HashMap<String,ArrayList<String>> tmpMap=new HashMap<String,ArrayList<String>>();
			for(String key : qs.paras.get(i).keySet())
			{
				ArrayList<String> tmpList=new ArrayList<String>();
				tmpList.addAll(qs.paras.get(i).get(key));
				tmpMap.put(key, tmpList);
			}
			this.paras.add(tmpMap);
		}
		//--
	}

	public String getQS2Str() {
		return query + "==" + business + "==" + userID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getBusiness() {
		return business;
	}

	public void setBusiness(String business) {
		this.business = business;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getCallTime() {
		return callTime;
	}

	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}

	public ArrayList<HashMap<String, ArrayList<String>>> getParas() {
		return paras;
	}

	public void setParas(ArrayList<HashMap<String, ArrayList<String>>> paras) {
		this.paras = paras;
	}
	
	/**
	 *方法名称：GetValueByKey 内容摘要：根据关键字，获取其对应的value值，也即所谓的键值对 修改者： 修改说明：
	 * 
	 * @Author：zhanggang
	 *@Param：key
	 *@Return：String
	 *@Throws：
	 * 
	 */
	public String getValueByKey(String key) {
		ArrayList<String> vs = getValuesByKey(key);
		if (vs.size() > 0)
			return vs.get(0);
		return "";
	}
	
	/**
	 *方法名称：GetValuesByKey 内容摘要：根据关键字，获取其对应的value值的集合 
	 * 
	 * @Author：zhanggang
	 *@Param：key
	 *@Return：ArrayList
	 *@Throws：
	 * 
	 */
	public ArrayList<String> getValuesByKey(String key) {
		for (Map<String, ArrayList<String>> para : paras) {
			if (para.containsKey(key)) {
				return para.get(key);
			}
		}
		return new ArrayList<String>();
	}

	/**
	 * 
	 *描述：根据键更新paras中的值。如果键已存在，先删除旧值后添加新值。如果键不存在，直接添加键值对。
	 *@author: qianlei
	 *@date： 日期：2015-1-14 时间：上午10:38:52
	 *@param key
	 *@param value
	 */
	public void updateValueByKey(String key, String value) {
		if (key.length() == 0)
			return;
		if (paras.size() == 0) {
			HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
			paras.add(map);
		}
		if (paras.get(0).containsKey(key)) {
			if (paras.get(0).get(key).size() == 0) {
				paras.get(0).get(key).add(value);
			} else {
				paras.get(0).get(key).clear();
				paras.get(0).get(key).add(value);
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(value);
			paras.get(0).put(key, list);
		}

	}
	
	/**
	 * 
	 *描述：根据KEY更新VALUE，如果KEY已经存在，删除原值将新值加入
	 *@author: qianlei
	 *@date： 日期：2014-12-11 时间：下午08:48:09
	 *@param key
	 *@param valuesList
	 */
	public void updateValuesByKey(String key , ArrayList<String> valuesList)
	{
		if(paras.size()==0)
		{
			HashMap<String, ArrayList<String>> tempMap = new HashMap<String, ArrayList<String>>();
			tempMap.put(key, valuesList);
			paras.add(tempMap);
		}
		else
		{
			paras.get(0).put(key, valuesList);
		}
	}


	/**
	 * 
	 *描述：根据键添加值到paras中,如果键值不存在就添加，如果键值存在就将值添加到List的末尾
	 *@author: qianlei
	 *@date： 日期：2015-1-14 时间：上午10:37:44
	 *@param key
	 *@param value
	 */
	public void addValueByKey(String key, String value) {
		if (key.length() == 0)
			return;
		if (paras.size() == 0) {
			HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
			paras.add(map);
		}
		if (paras.get(0).containsKey(key)) {
			if(!paras.get(0).containsKey(value))
			{
				paras.get(0).get(key).add(value);
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(value);
			paras.get(0).put(key, list);
		}

	}
	
	/**
	 * 
	 *描述：根据KEY增加VALUE，如果KEY已经存在，保留原值将新值加入
	 *@author: qianlei
	 *@date： 日期：2014-12-11 时间：下午08:43:43
	 *@param key
	 *@param valuesList
	 */
	public void addValuesByKey(String key, ArrayList<String> valuesList) {
		if (paras.size() == 0) {
			HashMap<String, ArrayList<String>> tempMap = new HashMap<String, ArrayList<String>>();
			tempMap.put(key, valuesList);
			paras.add(tempMap);
		} else {
			ArrayList<String> tempList = new ArrayList<String>();
			if (paras.get(0).containsKey(key)) {
				tempList = paras.get(0).get(key);
				tempList.addAll(valuesList);
			} else {
				tempList = valuesList;
			}
			paras.get(0).put(key, tempList);
		}
	}
	
	/**
	 * 
	 *描述：将参数加入结构体
	 *@author: qianlei
	 *@date： 日期：2015-4-10 时间：下午02:00:08
	 *@return void
	 */
	public void addValuesByDic(HashMap<String, ArrayList<String>> map)
	{
		if(paras.size()==0)
		{
			paras.add(map);
		}
		else
		{
			paras.get(0).putAll(map);
		}
	}
	
	/**
	 * 
	 *描述：判断键值对是否存在
	 *@author: qianlei
	 *@date： 日期：2015-3-23 时间：下午01:34:45
	 *@return boolean
	 */
	public boolean IsContainsKey(String key) {
		if (paras.size() == 0)
			return false;
		else {
			if (paras.get(0).containsKey(key))
				return true;
			else
				return false;
		}
	}
	
	/**
	 * 
	 *描述：删除键值对
	 *@author: qianlei
	 *@date： 日期：2015-3-23 时间：下午01:35:14
	 *@return void
	 */
	public void removeValuesbyKey(String key)
	{
		if(paras.size()>0)
		{
			if(paras.get(0).containsKey(key))
			{
				paras.get(0).remove(key);
			}
		}
	}
	
	/**
	 * 
	 *描述：判断参数是不是为空
	 *@author: qianlei
	 *@date： 日期：2015-3-23 时间：下午02:29:10
	 *@return Boolean
	 */
	public Boolean parasIsEmptry()
	{
		if(this.paras.size()<=0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * 
	 *描述：对象清空
	 *@author: qianlei
	 *@date： 日期：2015-4-23 时间：上午08:17:37
	 *@return void
	 */
	public void clear()
	{
		this.userID = "";
		this.query ="";
		this.channel = "sys";
		this.callTime = "";
		this.business = "";
		this.paras.clear();
	}
}
