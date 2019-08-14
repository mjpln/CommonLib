package com.knowology.UtilityOperate;

import java.security.MessageDigest;

public class MD5 {

	/**
	 * 
	 *描述：使用MD5加密
	 *@author: qianlei
	 *@date： 日期：2017-2-23 时间：下午08:55:17
	 *@param ConvertString
	 *@return String
	 */
	public static String encryption(String ConvertString) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		String encodingStr = null;
		try {
			byte[] strTemp = ConvertString.getBytes();
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			encodingStr = new String(str);
		} catch (Exception e) {
		}
		return encodingStr;
	}
	public static void main(String[] args) {
		System.out.println(MD5.encryption("userid=17009901111&UIUDI=6501fa776e0e4ef69c8828033ec7597axjdx10000"));
	}
}
