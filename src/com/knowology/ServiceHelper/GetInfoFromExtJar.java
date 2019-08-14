/**
 * 
 */
package com.knowology.ServiceHelper;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import com.knowology.GlobalValue;

/**
 *描述：通过外部的jar包从json中解析出信息
 *@author: qianlei
 *@date： 日期：2015-5-24 时间：上午10:39:36
 */
public class GetInfoFromExtJar {
	
	/**
	 * 
	 *描述：初始化
	 *@author: qianlei
	 *@date： 日期：2015-5-24 时间：上午11:09:43
	 *@return void
	 */
	public static void Init()
	{
		String jarPath = GlobalValue.externalJarPath;
		loadJar(jarPath);
	}
	
	/**
	 * 
	 *描述：将外部的jar包 引入
	 *@author: qianlei
	 *@date： 日期：2015-5-24 时间：上午11:09:54
	 *@return void
	 */
	public static void loadJar(String rootPath)
	{
		
		File libPath  = new File(rootPath);
		File[] jarFiles = libPath.listFiles(new FilenameFilter() {  
		    public boolean accept(File dir, String name) {  
		        return name.endsWith(".jar") || name.endsWith(".zip");  
		    }  
		});
		
		if (jarFiles == null ) return;
		
		URLClassLoader urlLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        try {
            //改变方法的可见性（即通过反映访问本来不可以访问的方法）
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
            method.setAccessible(true);
            
            for (File file : jarFiles) {  
                URL url = file.toURI().toURL();  
                try {  
                    method.invoke(urlLoader, url); 
                    GlobalValue.myLog.info("读取jar文件[name={"+file.getName()+"}]");
                } catch (Exception e) {  
                	GlobalValue.myLog.error("读取jar文件[name={"+file.getName()+"}]失败");
                }  
            }  
        } catch (Exception e) {
            e.printStackTrace();
        } 
	}
	
	@SuppressWarnings("unchecked")
	public static Map <String,String> getResultFromExtJar(String classPath, String jsonStr)
	{
		try {
		URLClassLoader urlLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<?> objClass = urlLoader.loadClass(classPath); //注意，如果用forname,则最好定义接口类，例如：HelloIface impl2 = (HelloIface) Class.forName(classPath)
        Object instance = objClass.newInstance();
        Method method2 = objClass.getDeclaredMethod("jsonParer", new Class[]{ String.class});
        Map <String,String> map = (HashMap <String,String>) method2.invoke(instance, jsonStr);
        return map;
		} catch (Exception e) {
            e.printStackTrace();
            return null;
            
        } 
	}
}
