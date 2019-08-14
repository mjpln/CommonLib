package com.knowology.ServiceHelper;

import java.util.ArrayList;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.knowology.GlobalValue;

/**
 * 
 *描述：调用C#服务的方法类
 *@author: qianlei
 *@date： 日期：2015-3-17 时间：下午02:15:22
 */
public class WebServiceHelper {
	/*
	 * 根据参数调用WebService
	 * url 服务地址
	 * className 接口方法名
	 * nameSpace 服务命名空间
	 * paraMap 参数键值对（参数名，参数值）
	 */
	public  String InvokeWebService(String url, String className,
			String nameSpace, Map<String, String> paraMap) {
		try {
			String endpoint = url + "?WSDL";
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(new java.net.URL(endpoint));//http://222.186.101.212:91/NLPAppWebService.asmx?WSDL
			call.setOperationName(new QName(nameSpace, className)); // 那边的方法
																	// "http://tempuri.org/"
																	// 这个也要注意Namespace
																	// 的地址,不带也会报错
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);// 设置返回类型
			call.setUseSOAPAction(true);
			call.setSOAPActionURI(nameSpace + className);//http://tempuri.org/Analyze
			// 处理参数
			ArrayList<Object> objList = new ArrayList<Object>();
			for (String paraName : paraMap.keySet()) {
				objList.add(paraMap.get(paraName));//将参数值存入objList
				call.addParameter(new QName(nameSpace, paraName),
						org.apache.axis.encoding.XMLType.XSD_STRING,
						javax.xml.rpc.ParameterMode.IN); //设置参数名
			}
			Object[] paras = objList.toArray();
			//参数存储完毕，发送请求调用WS
			GlobalValue.myLog.info("【调用WEB服务】"+url);
			GlobalValue.myLog.info("【调用WEB服务入参】"+className+"||"+objList);
			String result = (String) call.invoke(paras);
			GlobalValue.myLog.info("【调用WEB服务出参】"+className+"||"+result);
			return result;
		} catch (Exception e) {
			GlobalValue.myLog.error("【调用服务失败】"+e.toString());
			return "";
		}
	}
	
//例子
//	String url="http://222.186.101.212:91/NLPAppWebService.asmx";
//	String className="Analyze";
//	String nameSpace="http://tempuri.org/";
//	String query="{\"userID\":\"1234\",\"query\":\"你好\",\"business\":\"sms\",\"channel\":\"\",\"callTime\":\"2014-06-03 15:07:28\",\"paras\":[]}";
//	String paraName="jsonQS";
//	Map<String,String> paraMap=new HashMap<String,String>();
//	paraMap.put(paraName, query);
//	String result=WebServiceHelper.InvokeWebService(url, className, nameSpace, paraMap);
}
