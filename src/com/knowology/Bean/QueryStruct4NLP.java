package com.knowology.Bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *内容摘要：咨询结构体类，主要用来存储咨询的结构化信息
 *
 *类修改者：
 *修改说明：
 *@ClassName：QueryStruct
 *@Company：knowology
 *@Author：zhanggang
 *@Date：2013-06-20 14：42
 *@Version: V1.0
 */
public class QueryStruct4NLP
{
    private String phone = "";//手机号
    private String query = "";//咨询问题
    private String channel = "";//服务
    private String callTime = "";//访问时间
    private Map<String, ArrayList<String>> serviceInfo = new HashMap<String, ArrayList<String>>();
    
    public QueryStruct4NLP()
    {
    	
    }
    public QueryStruct4NLP(QueryStruct qs)//构造函数
    {
        phone = qs.getUserID();
        query = qs.getQuery();
        channel = qs.getBusiness();
        callTime = qs.getCallTime();
    }
    
    public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
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
	public Map<String, ArrayList<String>> getServiceInfo() {
		return serviceInfo;
	}
	public void setServiceInfo(Map<String, ArrayList<String>> serviceInfo) {
		this.serviceInfo = serviceInfo;
	}
	/**
      *方法名称：GetValuesByKey
      *内容摘要：根据关键字，获取其对应的value值的集合
      *修改者：
      *修改说明：
      *@Author：zhanggang
      *@Param：key
      *@Return：ArrayList
      *@Throws：
      *
      */
    public String GetValueByKey(String key)
    {
        ArrayList<String> vs = GetValuesByKey(key);
        if (vs.size() > 0)
            return vs.get(0);
        return "";
    }
    /**
      *方法名称：GetValuesByKey
      *内容摘要：根据关键字，获取其对应的value值的集合
      *修改者：
      *修改说明：
      *@Author：zhanggang
      *@Param：key
      *@Return：ArrayList
      *@Throws：
      *
      */
    public ArrayList<String> GetValuesByKey(String key)
    {
        if (serviceInfo.containsKey(key))
            return serviceInfo.get(key);
        return new ArrayList<String>();
    }
}
