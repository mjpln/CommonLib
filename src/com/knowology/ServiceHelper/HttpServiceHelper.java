/**
 * 
 */
package com.knowology.ServiceHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-5-22 时间：上午09:35:00
 */
public class HttpServiceHelper {

	/**
	 * get请求
	 * 
	 * @param url
	 *            请求的地址和参数
	 * @return url的结果
	 */
	public static String get(String url) {
		try {
			HttpGet request = new HttpGet(url);
			// 执行http get请求
			HttpResponse response = HttpClients.createDefault()
					.execute(request);

			// 根据返回码判断返回是否成功
			String result = "";
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
			}
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * POST请求
	 * 
	 * @param url
	 *            请求地址
	 * @param param
	 *            请求内容
	 * @return 接口返回的内容
	 */
	public static String post(String url, String param) {
		try {
			HttpPost request = new HttpPost(url);
			request.setEntity(new StringEntity(param, "UTF-8"));
			HttpResponse response = HttpClients.createDefault()
					.execute(request);

			// 根据返回码，判断请求是否成功
			if (200 == response.getStatusLine().getStatusCode()) {
				return EntityUtils.toString(response.getEntity());
			}
			return "";
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

}
