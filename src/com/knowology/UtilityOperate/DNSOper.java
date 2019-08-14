/**
 * 
 */
package com.knowology.UtilityOperate;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *内容摘要：服务器DNS操作类
 *对应用程序所在服务器DNS信息进行获取
 *@ClassName：DBStringOper
 *@Company：knowology
 *@Author：zhanggang
 *@Date：2014-10-20 14：42
 *@Version: V1.0
 */
public class DNSOper {
	/** 
	 * 方法名称： GetSelfIP
	 * 内容摘要：获取服务器的IP地址
	 * 
	 * @author zhanggang
	 * @param 
	 * @return string
	 * @throws
	 */
public static String GetSelfIP() throws UnknownHostException {
	InetAddress address = InetAddress.getLocalHost();//获取主机网络配置信息
	return address.getHostAddress();
}
/**
 * @throws UnknownHostException  
 * 方法名称： GetSelfIP
 * 内容摘要：获取服务器的名称
 * 
 * @author zhanggang
 * @param 
 * @return string
 * @throws
 */
public static String GetSelfName() throws UnknownHostException{
	InetAddress address = InetAddress.getLocalHost();//获取主机网络配置信息
		return address.getHostName();
}
}

