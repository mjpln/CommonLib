package com.knowology.UtilityOperate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.knowology.Log;
import com.knowology.Bean.INLPResult;
import com.knowology.Bean.QueryStruct;
import com.knowology.Bean.QueryStruct4NLP;
import com.knowology.Bean.KNLPResult;

//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class JSONOper {

	public static QueryStruct JSON2QueryStruct(String jsonStr) {
		JSONObject obj = JSON.parseObject(jsonStr);
		 Map<String, Class<QueryStruct>> classMap = new HashMap<String,
		 Class<QueryStruct>>();
		 classMap.put("QueryStruct", QueryStruct.class);
		QueryStruct qs = JSON.toJavaObject(obj,
				QueryStruct.class);
		//接口传JSON进来时，很可能将多个键值对放在了paras中的不同MAP中，此处一概处理成放在第一个MAP中
		 qs.setParas(new ArrayList<HashMap<String, ArrayList<String>>>());
		 JSONArray ja = obj.getJSONArray("paras");
		 if(ja.isEmpty())
		 return qs;
		 for (int i = 0; i < ja.toArray().length; i++) {
		 JSONObject jao = (JSONObject) ja.get(i);
		 for (String iter :jao.keySet()) {
		 String key = iter;
		 String valueStr = jao.get(key).toString().replaceAll("\"", "");
		 valueStr = valueStr.replace("[", "");
		 valueStr = valueStr.replace("]", "");
		 ArrayList<String> values = new ArrayList<String>();
		 if (valueStr.contains(",")) {
		 String[] tempStr = valueStr.split(",");
		 for (int j = 0; j < tempStr.length; j++) {
		 values.add(tempStr[j]);
		 }
		 } else {
		 if (!valueStr.equals(""))
		 values.add(valueStr);
		 }
		 qs.addValuesByKey(key, values);
		 }
		 }
		 //--
		return qs;
	}

	public static KNLPResult JSON2KNLPResult(String jsonStr) {
		try {
			JSONObject obj = JSON.parseObject(jsonStr);
			KNLPResult kn = JSON.toJavaObject(obj,
					KNLPResult.class);
			// kn.setParas(new ArrayList<Map<String, ArrayList<String>>>());
			// JSONObject obj = JSONObject.parseObject(jsonStr);
			// kn.setAbstractStr( obj.getString("abstractStr"));
			// kn.setAbstractID(obj.getString("abstractID"));
			// kn.setService(obj.getString("service"));
			// kn.setCredit(obj.getDouble("credit"));
			// kn.setAnswer(obj.getString("answer"));
			// JSONArray ja = obj.getJSONArray("paras");
			// if (ja.isEmpty())
			// return kn;
			// for (int i = 0; i < ja.toArray().length; i++) {
			// JSONObject jao = (JSONObject) ja.get(i);
			// for (String iter : jao.keySet()) {
			// String key = iter;
			// String valueStr = jao.get(key).toString().replaceAll("\"",
			// "");
			// valueStr = valueStr.replace("[", "");
			// valueStr = valueStr.replace("]", "");
			// ArrayList<String> values = new ArrayList<String>();
			// if (valueStr.contains(",")) {
			// String[] tempStr = valueStr.split(",");
			// for (int j = 0; j < tempStr.length; j++) {
			// values.add(tempStr[j]);
			// }
			// } else {
			// if (!valueStr.equals(""))
			// values.add(valueStr);
			// }
			// kn.addValuesByKey(key, values);
			// }
			// }
			return kn;
		} catch (Exception ex) {
			Log myLog = Log.getLoger();
			myLog.error(ex.toString());
			return null;
		}
	}

	public static INLPResult JSON2INLPResult(String jsonStr) {
		JSONObject obj = JSON.parseObject(jsonStr);
		// Map<String, Class<INLPResult>> classMap = new HashMap<String,
		// Class<INLPResult>>();
		// classMap.put("INLPResult", INLPResult.class);
		INLPResult ir = JSON.toJavaObject(obj,
				INLPResult.class);// 将json串反序列化成INLPResult.KNLPResult对象
		ir.getkNLPResults().clear();
		// 解析kNLPResults集合
		JSONArray ja = obj.getJSONArray("kNLPResults");
		if (ja.isEmpty())
			return ir;
		ArrayList<KNLPResult> kNLPResults = ir.getkNLPResults();
		for (int i = 0; i < ja.toArray().length; i++) {
			JSONObject jao = (JSONObject) ja.get(i);
			kNLPResults.add(JSON2KNLPResult(jao.toString()));
		}
		return ir;
	}

	public static String Object2JSONStr(QueryStruct qs) {
		return net.sf.json.JSONObject.fromObject(qs).toString();
	}

	public static String Object2JSONStr(KNLPResult kn) {
//		return net.sf.json.JSONObject.fromObject(kn).toString();
		return JSON.toJSONString(kn).toString();
	}

	public static String Object2JSONStr(INLPResult ir) {
		return net.sf.json.JSONObject.fromObject(ir).toString();
		// return JSONObject.toJSONString(ir).toString();
	}

	public static String Object2JSONStr(QueryStruct4NLP qs4Nlp) {
		String jsonStr = "{\"phone\":\"" + qs4Nlp.getPhone()
				+ "\",\"query\":\"" + qs4Nlp.getQuery() + "\",\"channel\":\""
				+ qs4Nlp.getChannel() + "\",\"callTime\":\""
				+ qs4Nlp.getCallTime() + "\",\"serviceInfo\":{";
		for (Entry<String, ArrayList<String>> entry : qs4Nlp.getServiceInfo()
				.entrySet()) {
			ArrayList<String> list = entry.getValue();
			if (list.size() == 0) {
				jsonStr += ",";
				continue;
			}
			jsonStr = jsonStr + "\"" + entry.getKey() + "\":[";
			for (String ss : list) {
				jsonStr = jsonStr + "\"" + ss + "\",";
			}
			jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
			jsonStr += "],";
		}
		jsonStr = jsonStr.substring(0, jsonStr.length() - 1);// 去除多余的逗号
		jsonStr = jsonStr + "}}";
		return jsonStr;
	}

	/**
	 * 方法名称： QAAnswer22JSONStr 内容摘要：将ArrayList<LinkedHashMap<String,
	 * String>>对象转化成标准的json格式
	 * 
	 * @author zhanggang
	 * @param relatedpoints
	 * @return String
	 * @throws
	 */
	public static String ArrayListMapValues2JSONStr(
			ArrayList<LinkedHashMap<String, String>> relatedpoints) {
		String result = "";
		result += "[";
		for (int i = 0; i < relatedpoints.size(); i++) {
			LinkedHashMap<String, String> valueMap = relatedpoints.get(i);
			result += LinkedHashMap2JSONStr(valueMap);
		}
		if (result.endsWith(","))
			result = result.substring(0, result.length() - 1);
		result += "],";
		return result;
	}

	/**
	 * 方法名称： Map2JSONStr 内容摘要：将LinkedHashMap<String, String>对象转化成标准的json格式
	 * 
	 * @author zhanggang
	 * @param map
	 * @return String
	 * @throws
	 */
	public static String LinkedHashMap2JSONStr(LinkedHashMap<String, String> map) {
		String result = "{";
		for (String key : map.keySet()) {
			String value = map.get(key);
			result += "\"" + key + "\":";
			if (key.equals("answer") || key.equals("menuItems"))
				result += value;
			else
				result += "\"" + value + "\"";
			result += ",";
		}
		if (result.endsWith(","))
			result = result.substring(0, result.length() - 1);
		result += "},";
		return result;
	}
}
