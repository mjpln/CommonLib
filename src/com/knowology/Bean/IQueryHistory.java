/**  
 * @Project: UIAOInterfaceWebService 
 * @Title: IQueryHistory.java
 * @Package com.knowology.UIAOInterfaceWebService.Entities
 * @author zhanggang
 * @date 2014-10-20 14：42
 * @Copyright: 2013 www.knowology.cn Inc. All rights reserved.
 * @version V1.0
 */
package com.knowology.Bean;

import com.knowology.Bean.INLPResult;
import com.knowology.Bean.KNLPResult;
import com.knowology.Bean.QueryStruct;
import com.knowology.UtilityOperate.DateTimeOper;
import com.knowology.UtilityOperate.JSONOper;

/**
 *内容摘要：NLP理解结果简单对象实体类 存储NLP理解结果转化后的咨询历史简单对象的相关信息
 * 
 * @ClassName：IQueryHistory
 *@Company：knowology
 *@Author：zhanggang
 *@Date：2014-10-20 14：42
 *@Version: V1.0
 */
public class IQueryHistory {
	private String logsource = "交互层";
	private String phone = "";// 用户手机号
	private String query = "";// 用户咨询
	private String channel = "";// 渠道
	private String service = "未匹配业务";// 咨询业务
	private String abstr = "未匹配摘要";// 咨询知识点
	private String parentservice = "";// 业务父类,此处仅限电信指令业务、电信咨询业务、个性化业务、号百业务、百科业务
	private String type = "";// 答案类型
	private String answer = "";// 答案内容
	private String outputstr = "";// 统一接口输出
	private String startTime = "";// 接口调用时间
	private String isEndScenarios = "";// 场景状态
	private String nlpserver = "";// 服务器信息

	private String ismatched = "true"; // 是否匹配
	private String queryObject = "";// 输入json
	private String statisticalInformation = "";// 统计信息

	public IQueryHistory(String logsource, INLPResult ir, QueryStruct qs,
			String qsJson) {
		setLogsource(logsource);
		setPhone(qs.getUserID());
		setQuery(qs.getQuery());
		setChannel(qs.getChannel());

		KNLPResult kn;
		if (!ir.knIsEmpty()) {
			kn = ir.getKNLPResultByCount(0);
		} else {
			kn = new KNLPResult();
		}
		setService(kn.getService());
		setAbstr(kn.getAbstractStr());
		setType("");
		setAnswer(kn.getAnswer());

		// 表中只保存分值最高的词模结果，其他词模匹配的KNLPResult删除
		INLPResult newIR = new INLPResult(ir);
		newIR.setkNLPResults(kn);
		String irJson = JSONOper.Object2JSONStr(newIR);
		setOutputstr(irJson);
		// --

		setStartTime(ir.getStartTime());
		setIsEndScenarios("");
		setNlpserver(ir.getNlpServer());
		setQueryObject(qsJson);
		setStatisticalInformation("");
		if (kn.getAbstractStr().contains("未匹配")
				|| kn.acquireRealAbstractStr().contains("默认回复")) {
			setIsmatched("false");
		}
	}

	public IQueryHistory(String _ls, String _ph, String _qu, String _ch,
			String _ser, String _abs, String _ty, String _ans, String _opstr,
			String _st, String _isend, String _nlps, String _qo, String _staInfs) {
		setLogsource(_ls);
		setPhone(_ph);
		setQuery(_qu);
		setChannel(_ch);
		setService(_ser);
		setAbstr(_abs);
		setType(_ty);
		setAnswer(_ans);
		setOutputstr(_opstr);
		setStartTime(_st);
		setIsEndScenarios(_isend);
		setNlpserver(_nlps);
		setQueryObject(_qo);
		setStatisticalInformation(_staInfs);
		if (_abs.contains("未匹配") || _abs.contains("默认回复")
				|| _abs.equals("百科接口"))
			setIsmatched("false");
	}

	public void setLogsource(String logsource) {
		this.logsource = logsource;
	}

	public String getLogsource() {
		return logsource;
	}

	public void setPhone(String phone) {
		if (phone.equals(""))
			this.phone = "NULL";
		else
			this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setQuery(String query) {
		if (query.equals(""))
			this.query = "NULL";
		else
			this.query = query;
	}

	public String getQuery() {
		return query;
	}

	public void setChannel(String channel) {
		if (channel.equals(""))
			this.channel = "NULL";
		else
			this.channel = channel;
	}

	public String getChannel() {
		return channel;
	}

	public void setService(String service) {
		if (service.equals(""))
			this.service = "未匹配业务";
		else
			this.service = service;
	}

	public String getService() {
		return service;
	}

	public void setAbstr(String abstr) {
		if (abstr.equals(""))
			this.abstr = "未匹配摘要";
		else
			this.abstr = abstr;
	}

	public String getAbstr() {
		return abstr;
	}

	public void setParentservice(String parentservice) {
		if (parentservice.equals(""))
			this.parentservice = "NULL";
		else
			this.parentservice = parentservice;
	}

	public String getParentservice() {
		return parentservice;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setAnswer(String answer) {
		if (answer.equals("")) {
			this.answer = "NULL";
		} else {
			this.answer = answer;
		}
	}

	public String getAnswer() {
		return answer;
	}

	public void setOutputstr(String outputstr) {
		if (outputstr.equals(""))
			this.outputstr = "NULL";
		else
			this.outputstr = outputstr;
	}

	public String getOutputstr() {
		return outputstr;
	}

	public void setStartTime(String startTime) {
		if (startTime.equals(""))
			this.startTime = DateTimeOper.getDateTimeByFormat();
		else
			this.startTime = startTime;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setIsEndScenarios(String isEndScenarios) {
		this.isEndScenarios = isEndScenarios;
	}

	public String getIsEndScenarios() {
		return isEndScenarios;
	}

	public void setNlpserver(String nlpserver) {
		if (nlpserver.equals(""))
			this.nlpserver = "未获取到主机IP";
		else
			this.nlpserver = nlpserver;
	}

	public String getNlpserver() {
		return nlpserver;
	}

	public void setIsmatched(String ismatched) {
		if (ismatched.equals(""))
			this.ismatched = "false";
		else
			this.ismatched = ismatched;
	}

	public String getIsmatched() {
		return ismatched;
	}

	public void setQueryObject(String queryObject) {
		this.queryObject = queryObject;
	}

	public String getQueryObject() {
		return queryObject;
	}

	public void setStatisticalInformation(String statisticalInformation) {
		this.statisticalInformation = statisticalInformation;
	}

	public String getStatisticalInformation() {
		return statisticalInformation;
	}

}
