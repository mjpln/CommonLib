/**
 * 
 */
package com.knowology.Bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.knowology.UtilityOperate.StringOper;

/**
 *描述：词模结构体，用来保存匹配上的词模体及相关信息
 * 
 * @author: qianlei
 *@date： 日期：2015-3-16 时间：下午02:06:58
 */
public class KNLPResult {
	private String abstractStr = "";// 摘要名
	private String abstractID = "0";// 摘要编号
	private String service = "";// 业务名
	private double credit = 0;// 分值
	private ArrayList<Map<String, ArrayList<String>>> paras = new ArrayList<Map<String, ArrayList<String>>>();// 返回值集合
	private String answer = "";// 答案

	// public int type =0;

	public KNLPResult() {

	}

	/**
	 * 构造函数，用于值克隆对象
	 */
	public KNLPResult(KNLPResult kn) {
		this.abstractID = kn.abstractID;
		this.abstractStr = kn.abstractStr;
		this.service = kn.service;
		this.credit = kn.credit;
		this.answer = kn.answer;
		// paras
		for (int i = 0; i < kn.paras.size(); i++) {
			Map<String, ArrayList<String>> tmpMap = new HashMap<String, ArrayList<String>>();
			for (String key : kn.paras.get(i).keySet()) {
				ArrayList<String> tmpList = new ArrayList<String>();
				tmpList.addAll(kn.paras.get(i).get(key));
				tmpMap.put(key, tmpList);
			}
			this.paras.clear();
			this.paras.add(tmpMap);
		}
		// --
	}

	/**
	 * 
	 *描述：获取知识点
	 * 
	 * @author: qianlei
	 *@date： 日期：2014-11-20 时间：下午07:50:01
	 *@return
	 */
	public String acquireKnowledge() {
		String abstractStr = acquireRealAbstractStr();
		if (abstractStr.contains("<") && abstractStr.contains(">")) {
			int p = abstractStr.indexOf(">");
			return abstractStr.substring(p + 1, abstractStr.length());
		} else {
			return "";
		}
	}

	/**
	 * 
	 *描述：获取摘要名，如果有子摘要，直接返回第一个子摘要的名称
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-12-9 时间：上午11:02:33
	 *@return String
	 */
	public String acquireRealAbstractStr() {
		if (isContainsKey("realAbstract")) {
			ArrayList<String> list = getValuesByKey("realAbstract");
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return abstractStr;
	}

	public void setAbstractStr(String abstractStr) {
		this.abstractStr = abstractStr;
	}

	/**
	 * 
	 *描述：获取业务名
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-2-15 时间：上午11:37:42
	 *@return String
	 */
	public String acquireRealService() {
		String abstractStr = acquireRealAbstractStr();
		if (abstractStr.contains("<") && abstractStr.contains(">")) {
			return StringOper.SubStrBetweenTag(abstractStr, "<", ">");
		} else {
			return "";
		}
	}

	/**
	 * 
	 *描述：获取摘要ID，如果有子摘要，直接返回第一个子摘要的ID
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-12-9 时间：上午11:40:02
	 *@return int
	 */
	public String acquireRealAbstractID() {
		if (isContainsKey("realAbstractID")) {
			ArrayList<String> list = getValuesByKey("realAbstractID");
			if (list.size() > 0) {
				return list.get(0);
			}
		}
		return abstractID;
	}

	public String getAbstractStr() {
		return abstractStr;
	}

	public String getAbstractID() {
		return abstractID;
	}

	public void setAbstractID(String abstractID) {
		this.abstractID = abstractID;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public double getCredit() {
		return credit;
	}

	public void setCredit(double credit) {
		this.credit = credit;
	}

	public ArrayList<Map<String, ArrayList<String>>> getParas() {
		return paras;
	}

	public void setParas(ArrayList<Map<String, ArrayList<String>>> paras) {
		this.paras = paras;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	/**
	 *方法名称：GetValueByKey 内容摘要：根据关键字，获取其对应的value值，也即所谓的键值对 修改者： 修改说明：
	 * 
	 * @Author：zhanggang
	 *@Param：key
	 *@Return：String
	 *@Throws：
	 * 
	 */
	public String getValueByKey(String key) {
		ArrayList<String> vs = getValuesByKey(key);
		if (vs.size() > 0)
			return vs.get(0);
		return "";
	}

	/**
	 * 
	 *描述：根据关键字获取值，如果list存在多个，用"|"连接
	 *@author: qianlei
	 *@date： 日期：2016-5-31 时间：上午10:39:49
	 *@param key
	 *@return String
	 */
	public String getListValueByKey(String key) {
		ArrayList<String> vs = getValuesByKey(key);
		if (vs.size() == 0) {
			return "";
		}
		if (vs.size() == 1) {
			return vs.get(0);
		} else {
			String re="";
			for(String v:vs)
			{
				re+=v+"|";
			}
			if(re.endsWith("|"))
			{
				re=re.substring(0, re.length()-1);
			}
			return re;
		}
	}

	/**
	 *方法名称：GetValuesByKey 内容摘要：根据关键字，获取其对应的value值的集合
	 * 
	 * @Author：zhanggang
	 *@Param：key
	 *@Return：ArrayList
	 *@Throws：
	 * 
	 */
	public ArrayList<String> getValuesByKey(String key) {
		for (Map<String, ArrayList<String>> para : paras) {
			if (para.containsKey(key)) {
				return para.get(key);
			}
		}
		return new ArrayList<String>();
	}

	/**
	 * 
	 *描述：根据键更新paras中的值。如果键已存在，先删除旧值后添加新值。如果键不存在，直接添加键值对。
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-1-14 时间：上午10:38:52
	 *@param key
	 *@param value
	 */
	public void updateValueByKey(String key, String value) {
		if (key.length() == 0)
			return;
		if (paras.size() == 0) {
			Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
			paras.add(map);
		}
		if (paras.get(0).containsKey(key)) {
			if (paras.get(0).get(key).size() == 0) {
				paras.get(0).get(key).add(value);
			} else {
				paras.get(0).get(key).clear();
				paras.get(0).get(key).add(value);
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(value);
			paras.get(0).put(key, list);
		}

	}

	/**
	 * 
	 *描述：根据KEY更新VALUE，如果KEY已经存在，删除原值将新值加入
	 * 
	 * @author: qianlei
	 *@date： 日期：2014-12-11 时间：下午08:48:09
	 *@param key
	 *@param valuesList
	 */
	public void updateValuesByKey(String key, ArrayList<String> valuesList) {
		if (paras.size() == 0) {
			Map<String, ArrayList<String>> tempMap = new HashMap<String, ArrayList<String>>();
			tempMap.put(key, valuesList);
			paras.add(tempMap);
		} else {
			paras.get(0).put(key, valuesList);
		}
	}

	/**
	 * 
	 *描述：根据键添加值到paras中,如果键值不存在就添加，如果键值存在就将值添加到List的末尾
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-1-14 时间：上午10:37:44
	 *@param key
	 *@param value
	 */
	public void addValueByKey(String key, String value) {
		if (key.length() == 0)
			return;
		if (paras.size() == 0) {
			Map<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
			paras.add(map);
		}
		if (paras.get(0).containsKey(key)) {
			if (!paras.get(0).containsKey(value)) {
				paras.get(0).get(key).add(value);
			}
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(value);
			paras.get(0).put(key, list);
		}

	}

	/**
	 * 
	 *描述：根据KEY增加VALUE，如果KEY已经存在，保留原值将新值加入
	 * 
	 * @author: qianlei
	 *@date： 日期：2014-12-11 时间：下午08:43:43
	 *@param key
	 *@param valuesList
	 */
	public void addValuesByKey(String key, ArrayList<String> valuesList) {
		if (paras.size() == 0) {
			Map<String, ArrayList<String>> tempMap = new HashMap<String, ArrayList<String>>();
			tempMap.put(key, valuesList);
			paras.add(tempMap);
		} else {
			ArrayList<String> tempList = new ArrayList<String>();
			if (paras.get(0).containsKey(key)) {
				tempList = paras.get(0).get(key);
				tempList.addAll(valuesList);
			} else {
				tempList = valuesList;
			}
			paras.get(0).put(key, tempList);
		}
	}

	public void deleteKey(String key)
	{
		if(paras== null || paras.size() == 0) return;
		if(paras.get(0).containsKey(key))
			paras.get(0).remove(key);
	}
	
	/**
	 * 
	 *描述：判断键值对是否存在
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-23 时间：下午01:34:45
	 *@return boolean
	 */
 	public boolean isContainsKey(String key) {
		if (paras.size() == 0)
			return false;
		else {
			if (paras.get(0).containsKey(key))
				return true;
			else
				return false;
		}
	}

	/**
	 * 
	 *描述：删除键值对
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-23 时间：下午01:35:14
	 *@return void
	 */
	public void removeValuesbyKey(String key) {
		if (paras.size() > 0) {
			if (paras.get(0).containsKey(key)) {
				paras.get(0).remove(key);
			}
		}
	}

	/**
	 * 
	 *描述：判断参数是不是为空
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-23 时间：下午02:29:10
	 *@return Boolean
	 */
	public Boolean parasIsEmptry() {
		if (this.paras.size() <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 *描述：判断一个KN是否是有效的KN 条件:摘要和业务全不为空
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-23 时间：下午03:19:58
	 *@return Boolean
	 */
	public Boolean isAvailable() {
		if (this.service.length() > 0 && this.abstractStr.length() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 
	 *描述：获取KN中Paras的键集合
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-3-31 时间：下午04:46:00
	 *@return ArrayList<String>
	 */
	public ArrayList<String> acquireParasKeySet() {
		ArrayList<String> list = new ArrayList<String>();
		if (!this.parasIsEmptry()) {
			list.addAll(this.paras.get(0).keySet());
		}
		return list;
	}

	/**
	 * 
	 *描述：获取键值对形式的Paras
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-3-31 时间：下午05:17:56
	 *@return Map<String,String>
	 */
	public Map<String, String> acquireParasKeyValue() {
		Map<String, String> map = new HashMap<String, String>();
		if (!this.parasIsEmptry()) {
			ArrayList<String> keySet = this.acquireParasKeySet();
			for (String key : keySet) {
				map.put(key, this.getValueByKey(key));
			}
		}
		return map;
	}

	/**
	 * 
	 *描述：复制
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-5-10 时间：下午04:46:24
	 *@param kn
	 *            void
	 */
	public void copy(KNLPResult kn) {
		this.abstractID = kn.abstractID;
		this.abstractStr = kn.abstractStr;
		this.service = kn.service;
		this.credit = kn.credit;
		this.answer = kn.answer;
		// paras
		for (int i = 0; i < kn.paras.size(); i++) {
			Map<String, ArrayList<String>> tmpMap = new HashMap<String, ArrayList<String>>();
			for (String key : kn.paras.get(i).keySet()) {
				ArrayList<String> tmpList = new ArrayList<String>();
				tmpList.addAll(kn.paras.get(i).get(key));
				tmpMap.put(key, tmpList);
			}
			this.paras.clear();
			this.paras.add(tmpMap);
		}
		// --
	}
}
