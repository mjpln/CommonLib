/**
 * 
 */
package com.knowology;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.bll.CommonLibMetafieldmappingDAO;

/**
 *描述：工具方法类，用于定义程序公用的方法。
 * 
 * @author: qianlei
 *@date： 日期：2014-6-18 时间：下午03:47:34
 */
public class ToolMethods {
	/*
	 * 读取配置文件参数
	 */
	public static String getConfigValues(String key) {
		//读取指定路径的配置文件
		// String result = "";
		// Properties prop = new Properties();
		// try {
		// prop.load(new
		// FileInputStream("/cpic/cpicapp/conf/CommonLibConfig.properties"));
		// result = prop.getProperty(key);
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// return result;

		ResourceBundle resourcesTable = ResourceBundle
				.getBundle("CommonLibConfig");
		return resourcesTable.getString(key);
	}

	/**
	 * 
	 *描述：读取文件
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-2-24 时间：上午11:39:50
	 *@param filePath
	 *@return ArrayList<String>
	 */
	public static ArrayList<String> readFile(String filePath) {
		ArrayList<String> result = new ArrayList<String>();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// 处理
					result.add(lineTxt);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 
	 *描述：写入文件
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-2-24 时间：上午11:40:02
	 *@param filePath
	 *@param list
	 *@param continuousWrite
	 *            void
	 */
	public static void writeLine(String filePath, ArrayList<String> list,
			Boolean continuousWrite) {
		try {
			File file = new File(filePath);
			FileWriter fw;
			if (continuousWrite) {
				if (!file.exists() || !file.isFile()) {
					file.createNewFile();
				}
				fw = new FileWriter(file.getAbsoluteFile(), true);// 连续写入，不覆盖
			} else {
				if (file.exists() && file.isFile()) {
					file.delete();
				}
				file.createNewFile();
				fw = new FileWriter(file.getAbsoluteFile());
			}
			BufferedWriter bw = new BufferedWriter(fw);
			for (String str : list) {
				bw.write(str + "\r\n");
			}
			bw.close();
		} catch (Exception e) {
			System.out.println("写入文件内容出错");
			e.printStackTrace();
		}
	}
}
