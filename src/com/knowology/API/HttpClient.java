package com.knowology.API;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;



public class HttpClient {
  
	public static String sendGet(String url, String param) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
  
    public static Map<String, String> getMap(String address)
    {
  
    	String json=HttpClient.sendGet("http://apis.map.qq.com/ws/geocoder/v1/", "address="+address+"&key=I4VBZ-7OEH2-DN2UC-CTHJA-3K2AZ-TFFY6");
    	Map<String, String> map = new HashMap<String, String>();
		JSONObject job = JSONObject.fromObject(json);
		if (job.containsKey("status")
				&& job.getString("status").equals("0")) {
			JSONObject result = JSONObject.fromObject(job.get("result"));
			JSONObject location = null;
			if(result.containsKey("location"))
			{
				location = JSONObject.fromObject(result
						.get("location"));
			}
			if (location != null && location.containsKey("lng") && location.containsKey("lat")) {
				map.put("经度值", location.getString("lng"));
				map.put("纬度值", location.getString("lat"));
			}
		}
    	return map;
    }
    
    
    public static void main(String[] args) throws UnsupportedEncodingException {
        // GET 
       Map<String, String> map=getMap("上海市济宁路355号(江浦路口)");
        System.out.println(map);
      //  {经度值=121.53245, 纬度值=31.268002} 百度
      //  {经度值=121.52593, 纬度值=31.26208}  腾讯
        
        
        //POST 
        //String sr=HttpClient.sendPost("http://localhost:6144/Home/RequestPostString", "key=123&v=456");
        //System.out.println(sr);
    }
    
}



