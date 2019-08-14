package com.knowology.Bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

import com.knowology.UtilityOperate.DateTimeOper;

/**
 *内容摘要：NLP理解结果信息类，主要用来存储NLP分析后的各项结果字段信息
 * 
 *类修改者： 修改说明：
 * 
 * @ClassName：INLPResult
 *@Company：knowology
 *@Author：zhanggang
 *@Date：2013-06-20 14：42
 *@Version: V1.0
 */
public class INLPResult {
	private String query = "";// 用户咨询
	private String queryType = "";// 咨询类别
	private String autoLearnedPat = "";// 自学习词模
	private ArrayList<KNLPResult> kNLPResults = new ArrayList<KNLPResult>();// 理解结果集合
	private String startTime = DateTimeOper.getDateTimeByFormat();// 分析开始时间
	private String endTime = DateTimeOper.getDateTimeByFormat();// 分析结束时间
	private String nlpServer = "";// 分析服务器

	public INLPResult() {

	}

	/**
	 * 
	 *描述：构造函数，用于克隆对象
	 * 
	 * @author: qianlei
	 *@date： 日期：2014-10-20 时间：下午11:24:08
	 *@param r
	 */
	public INLPResult(INLPResult r) {
		this.query = r.query;
		this.queryType = r.queryType;
		this.autoLearnedPat = r.autoLearnedPat;
		this.endTime = r.endTime;
		this.startTime = r.startTime;
		this.nlpServer = r.nlpServer;
		for (KNLPResult kn : r.kNLPResults) {
			KNLPResult newKn = new KNLPResult(kn);
			this.kNLPResults.add(newKn);
		}
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}

	public String getAutoLearnedPat() {
		return autoLearnedPat;
	}

	public ArrayList<KNLPResult> getkNLPResults() {
		return kNLPResults;
	}

	public void setAutoLearnedPat(String autoLearnedPat) {
		this.autoLearnedPat = autoLearnedPat;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public void setNlpServer(String nlpServer) {
		this.nlpServer = nlpServer;
	}

	/**
	 * 
	 *描述：根据编号返回KN，不存在返回 new KNLPResult()
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-23 时间：上午09:25:24
	 *@return KNLPResult
	 */
	public KNLPResult getKNLPResultByCount(Integer i) {
		if (i > this.kNLPResults.size() - 1 || i < 0) {
			return new KNLPResult();
		} else {
			return this.kNLPResults.get(i);
		}
	}

	public void setkNLPResults(ArrayList<KNLPResult> kNLPResults) {
		this.kNLPResults = kNLPResults;
	}

	/**
	 * 
	 *描述：清空之后添加
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-23 时间：上午10:15:58
	 *@return void
	 */
	public void setkNLPResults(KNLPResult kn) {
		this.kNLPResults.clear();
		this.kNLPResults.add(kn);
	}

	public String getStartTime() {
		return startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public String getNlpServer() {
		return nlpServer;
	}

	/**
	 * 
	 *描述：将KN加入链表的指定位置,原KN删除 注意：编号从0开始
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-23 时间：下午03:26:42
	 *@return void
	 */
	public void setKNLPResultByCount(Integer i, KNLPResult kn) {
		if (kn == null || !kn.isAvailable()) {
			return;
		}

		if (i > this.kNLPResults.size() - 1) {
			this.kNLPResults.add(kn);
		} else {
			this.kNLPResults.set(i, kn);
		}
	}

	/**
	 * 
	 *描述：将KN加入到指定位置，原KN后移
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-4-23 时间：下午07:24:09
	 *@return void
	 */
	public void addKNLPResultByCount(Integer i, KNLPResult kn) {
		if (kn == null || !kn.isAvailable()) {
			return;
		}

		if (i > this.kNLPResults.size() - 1) {
			this.kNLPResults.add(kn);
		} else {
			this.kNLPResults.add(i, kn);
		}
	}

	/**
	 * 
	 *描述：将多个kn添加到链表的末尾
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-4-28 时间：下午02:17:27
	 *@return void
	 */
	public void addKNLPResult(ArrayList<KNLPResult> knList) {
		this.kNLPResults.addAll(knList);
	}

	/**
	 * 
	 *描述：判断kn是不是为空 。 如果链表中只有1个KN，且业务和摘要为空的话，清空链表返回true
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-16 时间：下午02:28:04
	 *@return Boolean
	 */
	public Boolean knIsEmpty() {
		if (this.kNLPResults.isEmpty()) {
			return true;
		} else {
			if (this.kNLPResults.size() == 1
					&& this.kNLPResults.get(0).getService().length() == 0
					&& this.kNLPResults.get(0).acquireRealAbstractStr()
							.length() == 0) {
				this.kNLPResults.clear();
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 
	 *描述：KN按照分值降序排列
	 * 
	 * @author: qianlei
	 *@date： 日期：2017-3-13 时间：下午04:44:19 void
	 */
	public void sortKN() {
		if (kNLPResults.size() > 0) {
			TreeMap<Double, ArrayList<KNLPResult>> sortMap = new TreeMap<Double, ArrayList<KNLPResult>>(
					Collections.reverseOrder());
			ArrayList<KNLPResult> list = new ArrayList<KNLPResult>();
			for (KNLPResult kn : kNLPResults) {
				Double credit = kn.getCredit();
				if (sortMap.containsKey(credit)) {
					sortMap.get(credit).add(kn);
				} else {
					ArrayList<KNLPResult> tmp = new ArrayList<KNLPResult>();
					tmp.add(kn);
					sortMap.put(credit, tmp);
				}
			}
			for (Iterator<Double> it = sortMap.keySet().iterator(); it
					.hasNext();) {
				list.addAll(sortMap.get(it.next()));
			}
			this.setkNLPResults(list);
		}
	}

	/**
	 *方法名称：GetClearResultString 内容摘要：根据NLP的理解结果，从中获取出摘要和答案的信息串，存入咨询历史 修改者：
	 * 修改说明：
	 * 
	 * @Author：zhanggang
	 *@Param：
	 *@Return：String
	 *@Throws：
	 * 
	 */
	public String GetClearResultString() {
		String r = "";
		r += "结果：\r\n";
		int i = 1;
		String answer = "";
		// 遍历每一个KNLPResult集合，取出其对应的摘要和答案信息
		for (KNLPResult kn : kNLPResults) {
			answer = kn.getAnswer();
			if (answer.length() > 50)
				answer = answer.substring(0, 29);
			if (kn.acquireRealAbstractStr() == "") {
				r += "\t" + i + ". 无摘要（" + answer + "）\r\n";
			} else {
				r += "\t" + i + ". " + kn.acquireRealAbstractStr() + "（"
						+ answer + "）\r\n";
			}
			i++;
		}
		return r;
	}
}
