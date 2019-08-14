/**
 * 
 */
package com.knowology.UtilityOperate;

import java.net.URLEncoder;

/**
 *描述：加解密操作类
 * 
 * @author: qianlei
 *@date： 日期：2016-5-19 时间：下午02:35:59
 */
public class EncryptionOper {

	/**
	 * 
	 *描述：加密
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-5-19 时间：下午02:55:17
	 *@param str
	 *@return
	 *@throws Exception
	 *             String
	 */
	public static String addPassEncode(String str) throws Exception {
		if (str == null) {
			return "";
		}
		byte[] userIdByteArr = str.getBytes();
		byte[] encodeByteArr = Base64.encode(userIdByteArr);
		str = new String(encodeByteArr);
		try {
			str = URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * @description 字符串的解密 encode
	 * @return String
	 */
	public static String replacePassEncode(String str) throws Exception {
		if (str == null) {
			return "";
		}
		try {
			byte[] strByteArr = str.getBytes();
			byte[] decodeByteArr = Base64.decode(strByteArr);
			str = new String(decodeByteArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return str;
	}	
}
