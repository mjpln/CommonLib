package com.knowology.API;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

import common.Logger;


public class HttpClient {
	
	private static final Logger logger = Logger.getLogger(HttpClient.class);
  
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
	public static Object sendPost(String url, String param) {
        JSONObject result = null;
        logger.info("发送HTTP POST请求,请求参数:param={}"+param+",url="+url);
        //创建httpClient对象
        CloseableHttpClient client = HttpClients.custom().build();
        //创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        /**
         * 设置请求超时时间
         * setConnectTimeout：设置连接超时时间，单位毫秒。
         * setConnectionRequestTimeout：设置从connect Manager(连接池)获取Connection 超时时间，单位毫秒。
         * 这个属性是新加的属性，因为目前版本是可以共享连接池的。
         * setSocketTimeout：请求获取数据的超时时间(即响应时间)，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
         */
//        RequestConfig requestConfig = RequestConfig.custom()
//                .setConnectTimeout(10000).setConnectionRequestTimeout(1000)
//                .setSocketTimeout(10000).build();
        RequestConfig requestConfig = RequestConfig.custom().build();

        httpPost.setConfig(requestConfig);

        //设置header信息
        //指定报文头【Content-type】
        httpPost.setHeader("Content-type", "application/json");
        HttpEntity entity = null;
        try {
            //设置参数到请求对象中
            httpPost.setEntity(new StringEntity(param, Consts.UTF_8));
            //执行请求操作，并拿到结果（同步阻塞）
            CloseableHttpResponse response = client.execute(httpPost);
            String content = "";
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //获取结果实体
                entity = response.getEntity();
                if (entity != null) {
                    //按指定编码转换结果实体为String类型
                    content = EntityUtils.toString(entity, "UTF-8");
                    result= JSONObject.parseObject(content);
                }

            }
            logger.info("发送HTTP POST请求,响应结果reponseStatus="+ response.getStatusLine().getStatusCode()+",httpResult="+ content);
        } catch (SocketTimeoutException s) {
            logger.error("接口请求超时 ,", s);
            result.put("code", false);
            result.put("msg", "调用接口超时");

        } catch (UnsupportedEncodingException e) {
            result.put("code", false);
            result.put("msg", "发送http请求异常");
            logger.error("发送http post请求异常", e);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            result.put("code", false);
            result.put("msg", "发送http请求异常");
            logger.error("发送http请求异常", e);
            e.printStackTrace();
        } catch (IOException e) {
            result.put("code", false);
            result.put("msg", "发送http请求异常");
            logger.error("发送http请求异常", e);
            e.printStackTrace();
        } finally {
            if (null != entity) {// 释放连接
                EntityUtils.consumeQuietly(entity);
            }
        }
        return result;
    }
    public static Map<String, String> getMap(String address)
    {
  
    	String json=HttpClient.sendGet("http://apis.map.qq.com/ws/geocoder/v1/", "address="+address+"&key=I4VBZ-7OEH2-DN2UC-CTHJA-3K2AZ-TFFY6");
    	Map<String, String> map = new HashMap<String, String>();
		JSONObject job = JSONObject.parseObject(json);
		if (job.containsKey("status")
				&& job.getString("status").equals("0")) {
			JSONObject result = JSONObject.parseObject((String)job.get("result"));
			JSONObject location = null;
			if(result.containsKey("location"))
			{
				location = JSONObject.parseObject((String)result
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



