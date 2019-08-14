/**
 * 
 */
package com.knowology.ServiceHelper;


import javax.xml.namespace.QName;


import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

import com.knowology.GlobalValue;

/**
 * 
 *描述：调用javaWebService的方法类
 * 
 * @author: qianlei
 *@date： 日期：2015-4-29 时间：上午10:25:05
 */
public class JavaWebServiceHelper {
	/**
	 * 
	 *描述：调用方法
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-4-29 时间：上午10:37:57
	 *@return String 参数 url 需调用的服务地址 methodName 需调用的服务方法名 nameSpace 服务的命名空间名
	 *         paras 参数数组
	 */
	public String InvokeWebService(String url, String methodName,
			String nameSpace, Object[] paras) {
		// 客户端控件
		RPCServiceClient serviceClient = null;
		try {
			// 服务器地址
			String serviceAddress = url;
			// 服务器对应的action，如果不知道的话随便写，系统运行时会提示你的
			String action = nameSpace;// "http://knowology.com";
			serviceClient = new RPCServiceClient();
			Options options = serviceClient.getOptions();
			options.setTimeOutInMilliSeconds(600000L);
			EndpointReference targetEPR = new EndpointReference(serviceAddress);
			options.setTo(targetEPR);
			// 命名空间
			QName qName = new QName(action, methodName);
			// 需要传递给服务器的值
			Object[] values = paras;
			// 返回值的类型，基本类型为
			Class<?>[] returnType = new Class[] { String.class };
			// 返回结果
			Object[] result = serviceClient.invokeBlocking(qName, values,
					returnType);
			if(result == null || result.length == 0 || result[0] == null)
			{
				GlobalValue.myLog.error("result is null ");
				return "";
			}
			return result[0].toString();
		} catch (Exception e) {
			GlobalValue.myLog.error(url+"调用异常：" +e.toString());
			return "";
		} finally {
			try {
				if (serviceClient != null) {
					serviceClient.cleanupTransport();
				}
			} catch (AxisFault e) {
				e.printStackTrace();
			}
		}
	}
	
}

