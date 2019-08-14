/**
 * 
 */
package com.knowology;

import org.apache.log4j.Logger;

import com.knowology.ToolMethods;


/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-2-11 时间：上午11:39:35
 */
public class GlobalValue {

	/**
	 * 是否开启调试模式
	 */
	public static boolean IsDebug = Boolean.parseBoolean(ToolMethods.getConfigValues("IsDebug")); 
	/**
	 * 系统日志输出对象
	 */
	//public static Log myLog=Log.getLoger();
	public static Logger myLog = Logger.getLogger(Object.class);
	
	/**
	 * 服务器操作系统类型
	 */
	public static String platform=System.getProperty("os.name");
	/**
	 * 外部jar包路径
	 */
	public static String externalJarPath="";
	
	/**
	 * 
	 *描述：初始化
	 *@author: qianlei
	 *@date： 日期：2015-5-24 时间：上午11:19:34
	 *@return void
	 */
	public static void init()
	{
		myLog.info("CommLib开始初始化");
		//获取外部jar包的路径
		if(platform.contains("Windows"))
		{
			externalJarPath=ToolMethods.getConfigValues("ExternalJarPath_Windows");
		}
		if(platform.contains("Linux"))
		{
			externalJarPath=ToolMethods.getConfigValues("ExternalJarPath_Linux");
		}
		//--
		//外部jar包载入
//		GetInfoFromExtJar.Init();
		myLog.info("外部jar包载入――――完成");
		myLog.info("CommLib开始初始化完成");
	}
}
