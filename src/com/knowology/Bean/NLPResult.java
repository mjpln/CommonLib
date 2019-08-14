package com.knowology.Bean;

import java.util.ArrayList;

public class NLPResult {
	 public ArrayList<String> AllSegments = new ArrayList<String>();//所有的分词结果 一组分词用逗号隔开 不入库
     //public ArrayList<String> CandidateAbss = new ArrayList<String>();   //这一行新加的
     //public String CandidatePat = "";                           //这一行新加的
     public ArrayList<String> CandidateAbss = new ArrayList<String>();   //这一行新加的
     public String AutoLearnedPats = "";                       //这一行新加的
     public ArrayList<String> NegAbs = new ArrayList<String>();//所有的排除摘要  不入库
     public ArrayList<String> FeaAbs = new ArrayList<String>();
     public ArrayList<String> SeaAbs = new ArrayList<String>();

     /// <summary>
     /// 一个置信度下的多个摘要
     /// IDF:逆向文件频率 (inverse document frequency, IDF) 是一个词语普遍重要性的度量。
     /// 某一特定词语的IDF，可以由总文件数目除以包含该词语之文件的数目，再将得到的商取自然对数得到。
     /// TFIDF的主要思想是：如果某个词或短语在一篇文章中出现的频率TF高，并且在其他文章中很少出现，则认为此词或者短语具有很好的类别区分能力，适合用来分类。
     /// TFIDF实际上是：TF * IDF
     /// </summary>
     public class CreditResult
     {
         public double Credit = 0;						//置信度 = ( SUM(MatchedIdf) / (SUM(MissedIdf)+1) ) *  MatchedWordsCnt
         public double MatchedIdf = 0;					//分词中匹配词的idf值之和
         public double MissedIdf = 0;					//分词中未匹配词的idf值之和
         public int MatchedWordsCnt = 0;					//分词中匹配词的个数
         public double MatchRatio = 0;					//分词中匹配的词占所有词的比率
         public int MatchedCharsCnt = 0;
         public int MissedCharsCnt = 0;
         public ArrayList<Abstract> Abstracts = new ArrayList<Abstract>();					    //满足当前置信度的多个摘要

         /// <summary>
         /// 一个摘要结构
         /// </summary>
         public class Abstract
         {
             public String AbstractString = "";			                             //摘要字符串
             public int AbstractID = -1;			                                     //摘要ID
             public String Service = "";					                             //业务
             public String Topic = ""; 					                             //主题
             public Boolean IsAbsoluteCorrect = false;		                             //是否免检
             public int Type = -1;                                                    //0:咨询  1：排障  21:无需交互    22:需二次交互
             public String Brand = "";                                                //品牌
             public String Answer = "";                                               //答案字符串
             public ArrayList<WordPattern> WordPatterns = new ArrayList<WordPattern>();	     //满足当前摘要的多个词模

             /// <summary>
             /// 一个词模结构
             /// </summary>
             public class WordPattern
             {
                 public String PatternID = "";
                 public String PatternString = "";	    //模式字符串
                 public String OutputsString = "";	    //返回值字符串
                 public String PatternType = "";	        //词模类型，值域 = {普通词模，等于词模，统计免检词模，普通子句，等于子句}
                 public String SegmentString = "";       //匹配该模式的分词字符串		
                 public String Brand = "";				//该词模所属的品牌
                 public Boolean BrandChanged = false;	    //是否转了品牌
                 public Boolean WithContext = false;		//是否加了上下文信息后，进行理解
                 public Boolean NeedOrdered = false;        //匹配是否有序
                 public class Entity
                 {
                     public double Credit = 0;           //置信度
                     public String Value = "";           //词模中返回值部分的  值(value) eg.光速e9单机版199元套餐
                     public String Key = "";             //词模中返回值部分的  键(key)   eg.业务
                     public String Type = "";            //类型
                 }
                 public ArrayList<Entity> Entities = new ArrayList<Entity>();
             }
         }
     }

     public ArrayList<CreditResult> CreditResults = new ArrayList<CreditResult>();			//满足当前查询的多个置信度结果 不入库
     public String NLPStartTime;					                        //NLP开始处理时间 
     public String NLPEndTime;						                        //NLP处理完毕时间 
     public String NLPServerName = "";					                        //NLP服务器名（计算机名）
     //public String m_csrselpatternid = "";//csr选择句型编号 该值由话务员操作系统填写
     //public String m_csrselpatterntype = "";//csr选择句型的类型，值域 = {普通词模，等于词模，统计免检词模} 该值由话务员操作系统填写
     public String m_nlpmaxcredit_patternid;                                      //NLP分值最高句型编号
     public String m_nlpmaxcredit_patterntype = "";                               //NLP分值最高句型的类型，值域 = {普通词模，等于词模，统计免检词模}
     public String m_maxmatchedwords_patternid;                                   //NLP匹配词数最多的句型编号
     public String m_maxmatchedwords_patterntype = "";                            //NLP匹配词数最多的句型类型，值域 = {普通词模，等于词模，统计免检词模}
     public String m_nlpresultid;							                     //NLP结果ID（对应的数据库表中的sequence:nlpresult_seq）
}
