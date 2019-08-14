package com.knowology.Bean;

import java.util.Date;

public class QueryData {

    public String m_querydataid;		//咨询ID(对应数据库的querydata_seq)
    public String m_querycontent;		//咨询内容
    public String m_customertype;		//客户类型：集团、个人、家庭等 不填
    // public String m_phone;			    //手机号码
    public String m_srctermid;          //源终端ID（短信渠道：手机号码；IM：qq号码;机器人渠道:ip地址） 填写手机号码srctermid
    public String m_channel;			//渠道 sm
    public String m_city;				//地市  (PhoneheaderCity地市表 两个字段 phoneheader主键 city地市名称)  
    public String m_brand;				//品牌 不用
    //    public DateTime m_acceptsmtime;		//接收时间 
    public String m_accepttime;		//接收咨询时间（我们接收到的时间）
    public String m_calledtime;       //对方调用接口将咨询发送给我们的时间,由调用方填写（新增字段）
    public String m_port;		        //接收端口  msisdn ex:比如133888394发送给10000,133888394就是srctermid，10000就是msisdn
    //public String m_smservername;		//短信服务器名
    public String m_servername;		    //接受咨询服务器名
    public String m_packages;           //号码包
    public String m_systemid;			//发送消息的系统的ID（我们系统ID）
    public String m_smstype;			//短信的类型
    public Date m_savetime;         //保存到数据库的时间，有咨询和NLP结果保存模块填充值
}
