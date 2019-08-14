package com.knowology.Bean;

public class QueryHistory {

    //public String nlpresultid;              //winserviceAnswerSender不用
    //public String phone;                    //msisdn
    //public String port;                     //srctermid
    //public String answer;                   //对应结构体msgcontent 消息的内容
    //public String answerid;                 //不用,数据库序列answer_seq生成
    //public static String systemid = "";     //不入库
    //public static String smstype = "";      //不入库
    //public String channel;                  //sm表示短信渠道 只有sm才发送
    //public String service = "";             //业务
    //public String topic = "";               //主题
    //public String _abstract = "";           //摘要
    //public String patternid = "";           //csr选择句型编号 该值由话务员操作系统填写
    //public String patterntype = "";         //csr选择句型的类型，值域 = {普通词模，等于词模，统计免检词模} 该值由话务员操作系统填写
    //public String answertime;//回复时间
    //public String csrid;//回复人


    public String queryhistoryid;//咨询历史ID
    public String nlpresultid;//NLP结果ID
    public String brand;//答案品牌
    public String city;//答案地市
    public String channel;//   回复渠道
    public String phone;//手机号码
    public String port;//回复端口
    public String answercontent;//答案内容
    public String csraccepttime;//话务员接收短信时间
    public String csrreplytime;//话务员回复短信时间
    public String isresend;//补发标记
    public static String systemid;
    public static String smstype;
    public String service;
    public String topic;
    public String _abstract;
    public String patternid;
    public String patterntype;
    public String csrid;
    public String answertype;
}
