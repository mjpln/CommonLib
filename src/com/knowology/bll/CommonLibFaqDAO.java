package com.knowology.bll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

import javax.servlet.jsp.jstl.sql.Result;

import org.apache.commons.lang.StringUtils;

import com.knowology.GlobalValue;
import com.knowology.Bean.User;
import com.knowology.UtilityOperate.DateTimeOper;
import com.knowology.UtilityOperate.GetConfigValue;
import com.knowology.dal.Database;
import com.str.NewEquals;

public class CommonLibFaqDAO {
	/**
	 * 查询答案记录数
	 * 
	 * @param User
	 *            用户信息
	 * @param kbdataid
	 *            摘要ID
	 * @return int
	 */
	public static int getAnswerCount(User user, String kbdataid) {
		String servicetype = user.getIndustryOrganizationApplication();
		// 定义查询答案的SQL语句
		String sql = "select count(*) count from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g where b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid and b.kbdataid=? and f.servicetype like ?  ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定商家组织应用
		// if("基金行业->华夏基金->对内应用".equals(servicetype)){// 华夏特殊需求：对内应用时可以展示 对内应用
		// 的答案及多渠道应用答案
		// // 绑定商家组织应用
		// lstpara.add("基金行业->华夏基金->%");
		// }else{
		lstpara.add(servicetype);
		// }
		// 执行SQL语句，返回相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		int count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		// 判断数据源不为null且含有数据
		return count;
	}

	/**
	 * 查询答案记录数
	 * 
	 * @param servicetype
	 *            行业组织应用
	 * @param kbdataid
	 *            摘要ID
	 * @return int
	 */
	public static int getAnswerCountNew(String servicetype, String kbdataid) {
		// 定义查询答案的SQL语句
		String sql = "select count(*) count from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g where b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid and b.kbdataid=? and f.servicetype=?  ";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定商家组织应用
		lstpara.add(servicetype);
		// 执行SQL语句，返回相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		int count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		// 判断数据源不为null且含有数据
		return count;
	}

	/**
	 * 查询答案信息
	 * 
	 * @param User
	 *            用户信息
	 * @param kbdataid
	 *            摘要ID
	 * @param start
	 *            起始条数
	 * @param limit
	 *            间隔条数
	 * @return Result
	 */
	public static Result select(User user, String kbdataid, String start,
			String limit) {
		String servicetype = user.getIndustryOrganizationApplication();
		// 定义查询答案的SQL语句
		String sql = "";

		// 执行SQL语句，返回相应的数据源
		if (GetConfigValue.isOracle) {
			sql = "select c.kbansvaliddateid,g.kbanswerid,g.kbcontentid,g.answercontent,f.channel,f.answercategory,f.customertype,to_char(c.begintime,'yyyy-MM-dd') begintime,to_char(c.endtime,'yyyy-MM-dd') endtime,f.servicetype,g.answer_clob from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g where b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid and b.kbdataid=? and f.servicetype like ?  ";
			sql = "select * from (select t.*,rownum rn from (" + sql
					+ ")t) where rn>? and rn<=?";
		} else if (GetConfigValue.isMySQL) {
			sql = "select c.kbansvaliddateid,g.kbanswerid,g.kbcontentid,g.answercontent,f.channel,f.answercategory,f.customertype,date_format(c.begintime,'%Y-%m-%d') begintime,date_format(c.endtime,'%Y-%m-%d') endtime,f.servicetype,g.answer_clob from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g where b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid and b.kbdataid=? and f.servicetype like ?  ";
			sql = "select t2.* from (select t.* from (" + sql
					+ " )t) t2  limit ?,? ";
		}
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// if("基金行业->华夏基金->对内应用".equals(servicetype)){// 华夏特殊需求：对内应用时可以展示 对内应用
		// 的答案及多渠道应用答案
		// // 绑定商家组织应用
		// lstpara.add("基金行业->华夏基金->%");
		// }else{
		lstpara.add(servicetype);
		// }
		// 绑定开始条数参数
		lstpara.add(Integer.parseInt(start));
		// 绑定截止条数参数
		int end = Integer.parseInt(start) + Integer.parseInt(limit);
		lstpara.add(end);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;

	}

	/**
	 *@description 查询答案信息
	 *@param servicetype
	 *@param kbdataid
	 *@param page
	 *@param rows
	 *@return
	 *@returnType Result
	 */
	public static Result selectNew(String servicetype, String kbdataid,
			int page, int rows) {
		// 定义查询答案的SQL语句
		String sql = "";

		// 执行SQL语句，返回相应的数据源
		if (GetConfigValue.isOracle) {
			sql = "select c.kbansvaliddateid,g.kbanswerid,g.kbcontentid,g.answercontent,f.channel,f.answercategory,f.customertype,to_char(c.begintime,'yyyy-MM-dd') begintime,to_char(c.endtime,'yyyy-MM-dd') endtime,f.servicetype,g.answer_clob,f.city,f.userid from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g where b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid and b.kbdataid=? and f.servicetype=?  ";
			sql = "select * from (select t.*,rownum rn from (" + sql
					+ ")t) where rn>? and rn<=?";
		} else if (GetConfigValue.isMySQL) {
			sql = "select c.kbansvaliddateid,g.kbanswerid,g.kbcontentid,g.answercontent,f.channel,f.answercategory,f.customertype,date_format(c.begintime,'%Y-%m-%d') begintime,date_format(c.endtime,'%Y-%m-%d') endtime,f.servicetype,g.answer_clob,f.city,f.userid from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g where b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid and b.kbdataid=? and f.servicetype=?  ";
			sql = "select t2.* from (select t.* from (" + sql
					+ " )t) t2  limit ?,? ";
		}
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定商家组织应用
		lstpara.add(servicetype);
		// 绑定开始条数参数
		lstpara.add((page - 1) * rows);
		// 绑定截止条数参数
		lstpara.add(page * rows);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;

	}

	/**
	 *@description 查询答案信息
	 *@param business
	 *            四层结构
	 *@param channel
	 *            渠道
	 *@param kbdataid
	 *            摘要ID
	 *@param cusType
	 *            客户类型
	 *@return
	 *@returnType Result
	 */
	public static Result GetDBAnswerWithConstraints(String business,
			String channel, String kbdataid, String cusType) {
		// 定义查询答案的SQL语句
		String sql = "";
		// 执行SQL语句，返回相应的数据源

		if (GetConfigValue.isOracle) {
			sql = "select b.customer_kbdataid,c.kbansvaliddateid,g.kbanswerid,g.kbcontentid,g.answercontent,f.channel,f.answercategory,f.customertype,to_char(c.begintime,'yyyy-MM-dd') begintime,to_char(c.endtime,'yyyy-MM-dd') endtime,f.servicetype,g.answer_clob from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g where b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid and b.kbdataid=? and f.servicetype=? and f.channel=? and f.customertype=? ";
		} else if (GetConfigValue.isMySQL) {
			sql = "select b.customer_kbdataid,c.kbansvaliddateid,g.kbanswerid,g.kbcontentid,g.answercontent,f.channel,f.answercategory,f.customertype,date_format(c.begintime,'%Y-%m-%d') begintime,date_format(c.endtime,'%Y-%m-%d') endtime,f.servicetype,g.answer_clob from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g where b.kbdataid=c.kbdataid and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid and f.kbcontentid=g.kbcontentid and b.kbdataid=? and f.servicetype=? and f.channel=? and f.customertype=? ";
		}
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 绑定商家组织应用
		lstpara.add(business);
		// 绑定渠道参数
		lstpara.add(channel);
		// 绑定截客户类型数参数
		lstpara.add(cusType);
		// 增加答案有效期限制
		String today = DateTimeOper.getDateTimeByFormat("yyyyMMdd");
		if (GetConfigValue.isOracle) {
			sql += " and ( c.begintime is null or c.endtime is null or (? between to_char(c.begintime,'yyyymmdd') and to_char(c.endtime,'yyyymmdd')))";
		} else if (GetConfigValue.isMySQL) {
			sql += " and ( c.begintime is null or c.endtime is null or (? between  date_format(c.begintime,'%Y-%m-%d') and date_format(c.endtime,'%Y-%m-%d')))";
		}
		lstpara.add(today);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}

	/**
	 * 判断相同条件下的答案是否存在（相同条件:商家+客户类型+渠道+USERID）
	 * 
	 * @param Operationtype
	 * @param kbdataid
	 * @param kbansvaliddateid
	 * @param channel
	 * @param servicetype
	 * @param userid
	 * @param customertype
	 * @param starttime
	 * @param endtime
	 * @return
	 */
	public static Result exist(String Operationtype, String kbdataid,
			String kbansvaliddateid, String channel, String servicetype,
			String userid, String customertype, String starttime, String endtime) {
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		String sql = "";

		String useridPhrase = (StringUtils.isBlank(userid) ? ""
				: " and f.userid=? ");

		// 定义查询是否重复的SQL语句
		if ("".equals(starttime) || "".equals(endtime) || starttime == null
				|| endtime == null) {
			if ("update".equals(Operationtype)) {
				sql = "select f.*, c.kbansvaliddateid from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f where b.kbdataid=c.kbdataid "
						+ "and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid "
						+ "and f.channel=? and f.customertype=? and f.servicetype=?"
						+ useridPhrase
						+ " and b.kbdataid=?  and c.kbansvaliddateid !="
						+ kbansvaliddateid + "";

			} else {
				sql = "select f.*, c.kbansvaliddateid from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f where b.kbdataid=c.kbdataid "
						+ "and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid "
						+ "and f.channel=? and f.customertype=? and f.servicetype=?"
						+ useridPhrase + " and b.kbdataid=? ";

			}
		} else {
			if ("update".equals(Operationtype)) {
				sql = "select f.*, c.kbansvaliddateid from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f where b.kbdataid=c.kbdataid "
						+ "and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid "
						+ "and f.channel=? and f.customertype=? and f.servicetype=?"
						+ useridPhrase
						+ " and b.kbdataid=?  and c.kbansvaliddateid !="
						+ kbansvaliddateid
						+ "   and  c.BEGINTIME is null and c.ENDTIME is null ";

			} else {
				sql = "select f.*, c.kbansvaliddateid from kbdata b,kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f where b.kbdataid=c.kbdataid "
						+ "and c.kbansvaliddateid=d.kbansvaliddateid and d.kbanspakid=e.kbanspakid and e.kbansqryinsid=f.kbansqryinsid "
						+ "and f.channel=? and f.customertype=? and f.servicetype=?"
						+ useridPhrase
						+ " and b.kbdataid=? and  c.BEGINTIME is null and c.ENDTIME is null";

			}
		}

		// 定义绑定参数集合
		lstpara = new ArrayList<String>();
		// 绑定渠道参数
		lstpara.add(channel);
		// // 绑定答案类型参数
		// lstpara.add(answerType);
		// 绑定客户类型参数
		lstpara.add(customertype);
		// 绑定业务类型参数
		lstpara.add(servicetype);
		if (StringUtils.isNotBlank(userid)) {
			// 绑定用户id(RobotID)类型参数
			lstpara.add(userid);
		}
		// 绑定摘要id参数
		lstpara.add(kbdataid);
		// 定义数据源
		Result rs = null;
		// 执行SQL语句获取相应的数据源
		rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}

	/**
	 *@description 答案新增修改操作
	 *@param user
	 *            用户信息
	 *@param Operationtype
	 *            操作类型
	 *@param kbdataid
	 *            摘要ID
	 *@param kbansvaliddateid
	 *            有效期ID
	 *@param channel
	 *            q渠道
	 *@param servicetype
	 *            四层结构
	 *@param customertype
	 *            客户类型
	 *@param starttime
	 *            开始时间
	 *@param endtime
	 *            结束时间
	 *@param answerType
	 *            答案类型
	 *@param brand
	 *            品牌
	 *@param service
	 *            业务
	 *@param city
	 *            地市
	 *@param channelPersonalityMap
	 *@return
	 *@returnType int
	 */
	@SuppressWarnings("unchecked")
	public static int insertOrUpdate(User user, String Operationtype,
			String kbdataid, String kbansvaliddateid, String[] channels,
			String servicetype, String customertype, String starttime,
			String endtime, String answer, String answerType, String brand,
			String service, String city, String userid,
			Map channelPersonalityMap) {
		// 定义多条SQL语句结果
		List<String> listSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> listParam = new ArrayList<List<?>>();
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		String sql = "";
		String kbvid = "";
		String kbanspakid = "";
		String kbansqryinsid = "";
		String kbcontentid = "";
		String kbanswerid = "";
		// 获得商家标识符
		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(user.getIndustryOrganizationApplication());
		// 如果是update操作先删除再插入
		if ("update".equals(Operationtype)) {
			// 删除答案知识的SQL语句
			sql = "delete from kbansvaliddate where kbansvaliddateid = ?";
			// 定义绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定kbansvaliddateid参数
			lstpara.add(kbansvaliddateid);
			// 将SQL语句放入SQL语句集合中
			listSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			listParam.add(lstpara);

			// 删除后更新
			if (channelPersonalityMap.size() > 0) {
				// 从缓存变量中删除该答案
				SortedMap deleteAnswer = removeOneAnswer(channelPersonalityMap,
						kbansvaliddateid);

				String delAnsChannel = (deleteAnswer.get("channel") == null ? ""
						: deleteAnswer.get("channel").toString());
				String delAnsCityCode = (deleteAnswer.get("city") == null ? ""
						: deleteAnswer.get("city").toString());
				List[] rtn = getKbcontUpdateSql2OnDelete(
						(Result) channelPersonalityMap.get(delAnsChannel),
						delAnsCityCode);

				listSql.addAll(rtn[0]);
				listParam.addAll(rtn[1]);
			}
		}
		String opanswer = "";
		for (int j = 0; j < channels.length; j++) {
			// 获取答案关联表主键ID序列
			if (GetConfigValue.isOracle) {
				kbvid = String.valueOf(ConstructSerialNum.GetOracleNextValNew(
						"KBANSVALIDDATE_SEQ", bussinessFlag));
				kbanspakid = String.valueOf(ConstructSerialNum
						.GetOracleNextValNew("KBANSPAK_SEQ", bussinessFlag));
				kbansqryinsid = String.valueOf(ConstructSerialNum
						.GetOracleNextValNew("KBANSQRYINS_SEQ", bussinessFlag));
				kbcontentid = String
						.valueOf(ConstructSerialNum.GetOracleNextValNew(
								"SEQ_KBCONTENT_ID", bussinessFlag));
				kbanswerid = String.valueOf(ConstructSerialNum
						.GetOracleNextValNew("KBANSWER_SEQ", bussinessFlag));
			} else if (GetConfigValue.isMySQL) {
				kbvid = String.valueOf(ConstructSerialNum.getSerialIDNew(
						"KBANSVALIDDATE", "KBANSVALIDDATEID", bussinessFlag));
				kbanspakid = String.valueOf(ConstructSerialNum.getSerialIDNew(
						"KBANSPAK", "KBANSPAKID", bussinessFlag));
				kbansqryinsid = String.valueOf(ConstructSerialNum
						.getSerialIDNew("KBANSQRYINS", "KBANSQRYINSID",
								bussinessFlag));
				kbcontentid = String.valueOf(ConstructSerialNum.getSerialIDNew(
						"KBCONTENT", "KBCONTENTID", bussinessFlag));
				kbanswerid = String.valueOf(ConstructSerialNum.getSerialIDNew(
						"KBANSWER", "KBANSWERID", bussinessFlag));
			}

			// 插入kbansvaliddate
			// 获取kbansvaliddate的序列
			// 判断开始时间、结束时间不为空、null
			if ("".equals(starttime) || "".equals(endtime) || starttime == null
					|| endtime == null) {
				// 插入kbansvaliddate的SQL语句
				sql = "insert into kbansvaliddate(KBANSVALIDDATEID,KBDATAID,BEGINTIME,ENDTIME) values(?,?,null,null)";
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定kbansvaliddateid参数
				lstpara.add(kbvid);
				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);
			} else {
				// 插入kbansvaliddate的SQL语句
				if (GetConfigValue.isOracle) {
					sql = "insert into kbansvaliddate(KBANSVALIDDATEID,KBDATAID,BEGINTIME,ENDTIME) values(?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),to_date(?,'yyyy-mm-dd hh24:mi:ss'))";
				} else if (GetConfigValue.isMySQL) {
					sql = "insert into kbansvaliddate(KBANSVALIDDATEID,KBDATAID,BEGINTIME,ENDTIME) values(?,?,str_to_date(?,'%Y-%m-%d %H:%i:%s')  ,str_to_date(?,'%Y-%m-%d %H:%i:%s')  )";
				}
				// 定义绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定kbansvaliddateid参数
				lstpara.add(kbvid);
				// 绑定摘要id参数
				lstpara.add(kbdataid);
				// 绑定开始时间参数
				lstpara.add(starttime);
				// 绑定结束时间参数
				lstpara.add(endtime);
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);
			}

			// 插入kbanspak表
			// 插入kbanspak的SQL语句
			sql = "insert into kbanspak(KBANSPAKID,KBANSVALIDDATEID,PACKAGE ,PACKAGECODE,PAKTYPE) values(?,?,?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定kbanspakid参数
			lstpara.add(kbanspakid);
			// 绑定kbansvaliddateid参数
			lstpara.add(kbvid);
			// 绑定package参数
			lstpara.add("空号码包");
			// 绑定packagecode参数
			lstpara.add(null);
			// 绑定paktype参数
			lstpara.add("0");
			// 将SQL语句放入SQL语句集合中
			listSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			listParam.add(lstpara);

			// 插入kbansqryins表
			// 插入kbansqryins的SQL语句
			sql = "insert into kbansqryins(KBANSQRYINSID,KBANSPAKID,QRYINS) values(?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定kbansqryinsid参数
			lstpara.add(kbansqryinsid);
			// 绑定kbanspakid参数
			lstpara.add(kbanspakid);
			// 绑定qryins参数
			lstpara.add("查询指令无关");
			// 将SQL语句放入SQL语句集合中
			listSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			listParam.add(lstpara);

			// 插入kbcontent表
			// 插入kbcontent的SQL语句
			sql = "insert into kbcontent(KBCONTENTID ,KBANSQRYINSID,CHANNEL,ANSWERCATEGORY,SERVICETYPE ,CUSTOMERTYPE, CITY, USERID) values(?,?,?,?,?,?,?,?)";
			// 对应绑定参数集合
			lstpara = new ArrayList<String>();
			// 绑定kbcontentid参数
			lstpara.add(kbcontentid);
			// 绑定kbansqryinsid参数
			lstpara.add(kbansqryinsid);
			// 绑定渠道参数
			lstpara.add(channels[j]);
			// 绑定answerType参数
			lstpara.add(answerType);
			// 绑定servicetype参数
			lstpara.add(servicetype);
			// 绑定customertype参数
			lstpara.add(customertype);
			// 绑定city参数
			if (city == null || "".equals(city)) {
				city = getKbdataCitiesStr(kbdataid);
			}
			lstpara.add(city);
			// 绑定userid参数
			lstpara.add(userid);
			// 将SQL语句放入SQL语句集合中
			listSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			listParam.add(lstpara);

			// 需要个性化地市的答案做特殊处理
			if (channelPersonalityMap.size() > 0
					&& channelPersonalityMap.containsKey(channels[j])) {
				Result answers = (Result) channelPersonalityMap
						.get(channels[j]);
				List[] rtn = getKbcontentUpdateSql(answers, city);
				listSql.addAll(rtn[0]);
				listParam.addAll(rtn[1]);
			}

			if (answer.length() < 2000) {// 答案内容小于2000 直接插入answercontent字段
				// 插入kbanswer表
				// 插入kbanswer的SQL语句
				sql = "insert into kbanswer(kbanswerid,kbcontentid,answercontent,servicehallstatus,city,customertype,brand) values(?,?,?,?,?,?,?)";
				// 对应绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定kbanswerid参数
				lstpara.add(kbanswerid);
				// 绑定kbcontentid参数
				lstpara.add(kbcontentid);
				// 绑定答案参数
				lstpara.add(answer);
				// 绑定servicehallstatus参数
				lstpara.add("无关");
				// 绑定城市参数
				lstpara.add("");
				// 绑定customertype参数
				lstpara.add("所有客户");
				// 绑定品牌参数
				lstpara.add(brand);
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);
			} else {// 答案内容大于2000 直接插入answer_clob字段
				// 插入kbanswer表
				// 插入kbanswer的SQL语句
				sql = "insert into kbanswer(kbanswerid,kbcontentid,answercontent,servicehallstatus,city,customertype,brand) values(?,?,?,?,?,?,?)";
				// 对应绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定kbanswerid参数
				lstpara.add(kbanswerid);
				// 绑定kbcontentid参数
				lstpara.add(kbcontentid);
				// 绑定答案参数
				lstpara.add("");
				// 绑定servicehallstatus参数
				lstpara.add("无关");
				// 绑定城市参数
				lstpara.add("");
				// 绑定customertype参数
				lstpara.add("所有客户");
				// 绑定品牌参数
				lstpara.add(brand);
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);

				// 更新answer_clob字段
				sql = "update kbanswer set answer_clob =? where kbanswerid =? and kbcontentid =?";
				// 对应绑定参数集合
				lstpara = new ArrayList<String>();
				// 绑定答案参数
				lstpara.add(answer);
				// 绑定kbanswerid参数
				lstpara.add(kbanswerid);
				// 绑定kbcontentid参数
				lstpara.add(kbcontentid);
				// 将SQL语句放入SQL语句集合中
				listSql.add(sql);
				// 将对应的绑定参数集合放入集合中
				listParam.add(lstpara);

				opanswer = answer.substring(0, 1000) + "....";
			}

			// 生成操作日志记录
			// 将SQL语句放入集合中
			listSql.add(GetConfigValue.LogSql());
			// 将定义的绑定参数集合放入集合中
			listParam.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), " ", " ", "update"
					.equals(Operationtype) ? "修改答案" : "增加答案", opanswer,
					"KBANSWER"));
		}

		// 文件日志
		GlobalValue.myLog.info(user.getUserID() + "#"
				+ StringUtils.join(listSql, ";\n") + "#"
				+ StringUtils.join(listParam, ";"));

		// 执行SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(listSql, listParam);

		return c;

	}

	/**
	 * 删除指定kbansvaliddateid的答案（设置删除标志位）
	 * 
	 * @param channelAnswersMap
	 * @param kbansvaliddateid
	 * @return 删除的答案
	 */
	@SuppressWarnings("unchecked")
	private static SortedMap removeOneAnswer(Map channelAnswersMap,
			String kbansvaliddateid) {
		Iterator it = channelAnswersMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Result answers = (Result) entry.getValue();
			for(SortedMap answer : answers.getRows()){
				String kbansvaliddateidTmp = (answer.get("kbansvaliddateid") == null ? "" : answer.get("kbansvaliddateid").toString());
				if(NewEquals.equals(kbansvaliddateidTmp,kbansvaliddateid)){
					answer.put("deleteflag", true);
					return answer;
				}
			}
		}
		return null;
	}

	/**
	 * 获取更新Kbconten的SQL
	 * 
	 * @param answers
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static List[] getKbcontentUpdateSql(Result answers,
			String addCityCode) {
		List[] rtn = new ArrayList<?>[2];
		List<String> sqls = new ArrayList<String>();
		List<List<?>> listParams = new ArrayList<List<?>>();
		rtn[0] = sqls;
		rtn[1] = listParams;

		String sql1 = "update kbcontent set excludedcity=? where kbcontentid=?";
		String sql2 = "update kbcontent set city=? where kbcontentid=?";

		// 全国及全省答案会做更新地市处理
		for (Map answer : answers.getRows()) {
			boolean deleted = (answer.get("deleteflag") == null ? false : true);
			// 该答案在前面操作中已经被删除，就不再使用了
			if (deleted) {
				continue;
			}

			String kbcontentid = (answer.get("kbcontentid") == null ? ""
					: answer.get("kbcontentid").toString());
			String city = (answer.get("city") == null ? "" : answer.get("city")
					.toString());

			if (StringUtils.isEmpty(city)) {
				continue;
			}

			if (city.contains("全国")) { // 全国答案
				String excludedCityCode = (answer.get("excludedcity") == null ? ""
						: answer.get("excludedcity").toString());
				if (StringUtils.isNotEmpty(excludedCityCode)) {
					// 获取并集地市
					Set unionCityCodeSet = getUnionCityCode(excludedCityCode,
							addCityCode);
					String newExcludedCityCode = StringUtils.join(
							unionCityCodeSet, ",");
					if (!NewEquals.equals(excludedCityCode,newExcludedCityCode)) {
						sqls.add(sql1);
						listParams.add(Arrays.asList(newExcludedCityCode,
								kbcontentid));
					}
				}
			} else if (isProvince(city)) { // 全省答案
				Set newCityCodeSet = removeCityCode(city, addCityCode);
				String newCityCode = StringUtils.join(newCityCodeSet, ",");
				if (!NewEquals.equals(city,newCityCode)){					sqls.add(sql2);
					listParams.add(Arrays.asList(newCityCode, kbcontentid));
				}
			}
		}

		return rtn;
	}

	private static boolean containsCityCode(String cityCode1, String cityCode2) {
		TreeSet<String> set1 = new TreeSet<String>(Arrays.asList(cityCode1
				.split(",")));
		TreeSet<String> set2 = new TreeSet<String>(Arrays.asList(cityCode2
				.split(",")));

		return set1.containsAll(set2);
	}

	@SuppressWarnings("unchecked")
	private static Set removeCityCode(String cityCode1, String cityCode2) {
		TreeSet<String> set1 = new TreeSet<String>(Arrays.asList(cityCode1
				.split(",")));
		TreeSet<String> set2 = new TreeSet<String>(Arrays.asList(cityCode2
				.split(",")));

		set1.removeAll(set2);
		return set1;
	}

	// @SuppressWarnings("unchecked")
	// private static Set getSameCityCode(String cityCode1, String cityCode2){
	// TreeSet<String> set1 = new
	// TreeSet<String>(Arrays.asList(cityCode1.split(",")));
	// TreeSet<String> set2 = new
	// TreeSet<String>(Arrays.asList(cityCode2.split(",")));
	//		
	// set1.retainAll(set2);
	// return set1;
	// }

	@SuppressWarnings("unchecked")
	private static Set getUnionCityCode(String cityCode1, String cityCode2) {
		TreeSet<String> set1 = new TreeSet<String>(Arrays.asList(cityCode1
				.split(",")));
		TreeSet<String> set2 = new TreeSet<String>(Arrays.asList(cityCode2
				.split(",")));

		set1.addAll(set2);
		return set1;
	}

	private static boolean isProvince(String city) {
		TreeSet<String> citySet = new TreeSet<String>(Arrays.asList(city
				.split(",")));
		for (String cityCode : citySet) {
			if (cityCode.endsWith("0000")) {
				return true;
			}
		}
		return false;
	}

	/**
	 *@description 删除答案
	 *@param user
	 *            用户信息
	 *@param kbansvaliddateid
	 *            有效期ID
	 *@param answer
	 *            答案
	 *@param service
	 *            业务
	 *@param brand
	 *            品牌
	 *@return
	 *@returnType int
	 */
	public static int delete(User user, String kbansvaliddateid, String answer,
			String service, String brand) {
		// 定义多条SQL语句结果
		List<String> listSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> listParam = new ArrayList<List<?>>();
		// 删除答案知识的SQL语句
		String sql = "delete from kbansvaliddate where kbansvaliddateid = ?";
		// 定义绑定参数集合
		List<String> lstpara = new ArrayList<String>();
		// 绑定kbansvaliddateid参数
		lstpara.add(kbansvaliddateid);
		// 将SQL语句放入SQL语句集合中
		listSql.add(sql);
		// 将对应的绑定参数集合放入集合中
		listParam.add(lstpara);
		
		if(answer.length()>2000){
			answer = answer.substring(0, 1000) + "....";
		}
		// 生成操作日志记录
		// 将SQL语句放入集合中
		listSql.add(GetConfigValue.LogSql());
		// 将定义的绑定参数集合放入集合中
		listParam.add(GetConfigValue.LogParam(user.getUserIP(), user
				.getUserID(), user.getUserName(), " ", " ", "删除答案", answer,
				"KBANSWER"));

		// 文件日志
		GlobalValue.myLog.info(user.getUserID() + "#"
				+ StringUtils.join(listSql, ";\n") + "#"
				+ StringUtils.join(listParam, ";"));
		// 执行SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(listSql, listParam);

		return c;

	}

	/**
	 *@description 删除答案
	 *@param user
	 *            用户信息
	 *@param kbansvaliddateid
	 *            有效期ID
	 *@return
	 *@returnType int
	 */
	@SuppressWarnings("unchecked")
	public static int bathDelete(String kbansvaliddateids, User user) {
		// 定义多条SQL语句结果
		List<String> listSql = new ArrayList<String>();
		// 定义多条SQL语句对应的绑定参数集合
		List<List<?>> listParam = new ArrayList<List<?>>();
		String temp[] = kbansvaliddateids.split("@@");
		// 删除答案知识的SQL语句
		String sql = "delete from kbansvaliddate where kbansvaliddateid = ?";

		for (int i = 0; i < temp.length; i++) {
			// 定义绑定参数集合
			List<String> lstpara = new ArrayList<String>();
			// 绑定kbansvaliddateid参数
			lstpara.add(temp[i]);
			// 将SQL语句放入SQL语句集合中
			listSql.add(sql);
			// 将对应的绑定参数集合放入集合中
			listParam.add(lstpara);

			// 待删除答案的kbansvaliddateid
			String kbansvaliddateid = temp[i];

			// /// 获取待删除答案详情 //////
			Result curAnswer = getAnswerByKbansvaliddateid(kbansvaliddateid);
			// 生成操作日志记录
			// 将SQL语句放入集合中
			listSql.add(GetConfigValue.LogSql());
			String answer = (curAnswer.getRows()[0].get("answercontent") == null) ? ""
					: curAnswer.getRows()[0].get("answercontent").toString();
			// 将定义的绑定参数集合放入集合中
			listParam.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), " ", " ", "删除答案", answer,
					"KBANSWER"));

			// 待删除的答案为空直接删除
			if (curAnswer == null) {
				continue;
			}
			String curAnsCity = (curAnswer.getRows()[0].get("city") == null ? ""
					: curAnswer.getRows()[0].get("city").toString());
			if (curAnsCity.contains("全国") || isProvince(curAnsCity)) {
				// 待删除的答案地市是全国或省级的时候，直接删除
				continue;
			}

			// /// 获取待删除答案的同渠道同地市同USERID答案集合 /////
			Result sameChannelAnswers = exist("insert", curAnswer.getRows()[0]
					.get("kbdataid").toString(), null, curAnswer.getRows()[0]
					.get("channel").toString(), curAnswer.getRows()[0].get(
					"servicetype").toString(), curAnswer.getRows()[0]
					.get("userid") == null ? "" : curAnswer.getRows()[0].get(
					"userid").toString(), curAnswer.getRows()[0].get(
					"customertype").toString(), null, null);

			List[] rtn = getKbcontUpdateSql2OnDelete(sameChannelAnswers,
					curAnsCity);
			listSql.addAll(rtn[0]);
			listParam.addAll(rtn[1]);

		}

		// 文件日志
		GlobalValue.myLog.info(user.getUserID() + "#"
				+ StringUtils.join(listSql, ";\n") + "#"
				+ StringUtils.join(listParam, ";"));
		// 执行SQL语句，绑定事务处理，并返回事务处理的结果
		int c = Database.executeNonQueryTransaction(listSql, listParam);

		return c;
	}

	/**
	 * 根据kbansvaliddateid获取与该答案相同
	 * 
	 * @param kbansvaliddateid
	 * @return
	 */
	public static Result getSameChannelAnswers(String kbansvaliddateid) {
		Result rs = null;
		Result curAnswer = getAnswerByKbansvaliddateid(kbansvaliddateid);
		if (curAnswer != null && curAnswer.getRowCount() > 0) {
			String kbdataid = (curAnswer.getRows()[0].get("kbdataid") == null ? ""
					: curAnswer.getRows()[0].get("kbdataid").toString());
			String channel = (curAnswer.getRows()[0].get("channel") == null ? ""
					: curAnswer.getRows()[0].get("channel").toString());
			String servicetype = (curAnswer.getRows()[0].get("servicetype") == null ? ""
					: curAnswer.getRows()[0].get("servicetype").toString());
			String customertype = (curAnswer.getRows()[0].get("customertype") == null ? ""
					: curAnswer.getRows()[0].get("customertype").toString());
			String userid = (curAnswer.getRows()[0].get("userid") == null ? ""
					: curAnswer.getRows()[0].get("userid").toString());

			rs = exist("insert", kbdataid, null, channel, servicetype, userid,
					customertype, null, null);
		}

		return rs;
	}

	@SuppressWarnings("unchecked")
	public static List[] getKbcontUpdateSql2OnDelete(Result answers,
			String cityCode) {
		List[] rtn = new ArrayList<?>[2];
		List<String> sqls = new ArrayList<String>();
		List<List<?>> listParams = new ArrayList<List<?>>();
		rtn[0] = sqls;
		rtn[1] = listParams;

		String sql1 = "update kbcontent set excludedcity=? where kbcontentid=?";
		String sql2 = "update kbcontent set city=? where kbcontentid=?";

		// 全获取国答案
		SortedMap answer = getAnswerByArea("全国", answers);
		if (answer != null) {
			String kbcontentid = (answer.get("kbcontentid") == null ? ""
					: answer.get("kbcontentid").toString());
			String excludedCityCode = (answer.get("excludedcity") == null ? ""
					: answer.get("excludedcity").toString());
			if (StringUtils.isNotEmpty(excludedCityCode)) {
				// 该全国答案可替代待删除答案
				if (containsCityCode(excludedCityCode, cityCode)) {
					String newExcludedCityCode = StringUtils.join(
							removeCityCode(excludedCityCode, cityCode), ",");
					sqls.add(sql1);
					listParams.add(Arrays.asList(newExcludedCityCode,
							kbcontentid));

					// 更新answers
					answer.put("excludedcity", newExcludedCityCode);
				}
			}
		} else {
			// 获取省答案
			SortedMap provinceAnswer = getAnswerByArea("省", answers);
			if (provinceAnswer != null) {
				String kbcontentid = (provinceAnswer.get("kbcontentid") == null ? ""
						: provinceAnswer.get("kbcontentid").toString());
				String city = (provinceAnswer.get("city") == null ? ""
						: provinceAnswer.get("city").toString());

				if (!containsCityCode(city, cityCode)) {
					String newCityCode = StringUtils.join(getUnionCityCode(
							city, cityCode), ",");
					sqls.add(sql2);
					listParams.add(Arrays.asList(newCityCode, kbcontentid));

					// 更新answers
					provinceAnswer.put("city", newCityCode);
				}
			}
		}

		return rtn;
	}

	/**
	 * 获取省/全国的答案
	 * 
	 * @param area
	 * @param answers
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static SortedMap getAnswerByArea(String area, Result answers) {
		for (SortedMap answer : answers.getRows()) {
			String city = (answer.get("city") == null ? "" : answer.get("city")
					.toString());
			if ("全国".equals(area)) {
				if (city.contains("全国")) {
					return answer;
				}
			} else if ("省".equals(area)) {
				if (isProvince(city)) {
					return answer;
				}
			}
		}

		return null;
	}

	/**
	 * 获取单条答案
	 * 
	 * @param kbansvaliddateid
	 * @return
	 */
	public static Result getAnswerByKbansvaliddateid(String kbansvaliddateid) {
		String sql = " SELECT f.*,b.kbdataid,g.answercontent"
				+ " FROM kbdata b," + "   kbansvaliddate c," + "   kbanspak d,"
				+ "   kbansqryins e," + "   kbcontent f," + "   kbanswer g"
				+ " WHERE b.kbdataid      =c.kbdataid"
				+ " AND c.kbansvaliddateid=d.kbansvaliddateid"
				+ " AND d.kbanspakid      =e.kbanspakid"
				+ " AND e.kbansqryinsid   =f.kbansqryinsid"
				+ " AND f.kbcontentid     =g.kbcontentid"
				+ " AND c.kbansvaliddateid=?";
		return Database.executeQuery(sql, kbansvaliddateid);
	}

	/**
	 *@description 通过 kbanswerid 查询答案内容
	 *@param kbanswerid
	 *@return
	 *@returnType Result
	 */
	public static Result select(String kbanswerid) {
		// 查询当前的回复模板的SQL语句
		String sql = "select answercontent from kbanswer where kbanswerid=? ";
		// 定义绑定参数集合
		List<Object> lstpara = new ArrayList<Object>();
		// 绑定答案id参数
		lstpara.add(kbanswerid);
		// 执行SQL语句，获取相应的数据源
		Result rs = Database.executeQuery(sql, lstpara.toArray());
		return rs;
	}

	/**
	 *@description 通过业务查询关联答案知识
	 *@param sevicetype
	 *            四层结构串
	 *@param content
	 *            查询内容
	 *@param serviceRoot
	 *            业务根
	 *@param sevicecontainer
	 *            业务归属标识
	 *@param subjectTreeName
	 *            主题树名称
	 *@param start
	 *            起始条数
	 *@param limit
	 *            间隔条数
	 *@return
	 *@returnType Result
	 */
	public static Result getKnowledgeByService(String sevicetype,
			String content, String serviceRoot, /* List<String> permitServices */
			String userId, int start, int limit) {
		Result rs = null;
		int end = start + limit;
		StringBuilder sqlResult = new StringBuilder();
		String sql = "";
		sqlResult
				.append("select al.kbansvaliddateid ,a.service,a.serviceid, a.parentname,a.parentid, b.topic,b.abstract,b.kbdataid, al.answercontent "
						+ "from ( select * from ( SELECT *  FROM service aa WHERE  aa.serviceid in  (SELECT serviceid  FROM service start  WITH service in("
						+ serviceRoot
						+ ") connect BY nocycle prior serviceid = parentid ) )ak  where ak.service like '"
						+ content
						+ "' and "
						+ getPermissionServiceSql(userId, sevicetype)
						+ ") a "
						+ " left join (SELECT * FROM kbdata kk WHERE  kk.abstract NOT LIKE '%(删除标识符近类)' ) b  "
						+ " on a.serviceid=b.serviceid   "
						+ " left join ("
						+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
						+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
						+ " where c.kbansvaliddateid=d.kbansvaliddateid "
						+ " and d.kbanspakid=e.kbanspakid "
						+ " and e.kbansqryinsid=f.kbansqryinsid "
						+ " and f.kbcontentid=g.kbcontentid "
						+ " and f.servicetype='" + sevicetype + "' ");

		sqlResult
				.append(" ) al  on b.kbdataid = al.kbdataid order by al.kbansvaliddateid ");

		if (GetConfigValue.isOracle) {
			sql = "select * from (select SS.*, rownum rn from (";
			sqlResult.append(" ) SS where rownum<=" + end + " ) where rn >="
					+ start + "");
		} else if (GetConfigValue.isMySQL) {
			sqlResult.append(" ) SS) CC limit " + start + "," + end);
			sql = "select * from (select SS.* from (";
		}

		GlobalValue.myLog.info("搜索-业务查询：" + sql + sqlResult.toString());
		rs = Database.executeQuery(sql + sqlResult.toString());
		return rs;
	}

	/**
	 *@description 通过业务查询关联答案知识记录数
	 *@param sevicetype
	 *            四层结构串
	 *@param content
	 *            模糊查询内容
	 *@param sevicecontainer
	 *            业务归属标识
	 *@param subjectTreeName
	 *            主题树名称
	 *@param serviceRoot
	 *            业务根串
	 *@return
	 *@returnType int
	 */
	public static int getKnowledgeByServiceCount(String sevicetype,
			String content, String serviceRoot, /* List<String> permitServices */
			String userId) {
		Result rs = null;
		int count = 0;
		StringBuilder sqlCount = new StringBuilder();
		sqlCount
				.append("select count(*) count  "
						+ "from ( select * from ( SELECT aa.serviceid , aa.service FROM service aa WHERE  aa.serviceid in(SELECT serviceid  FROM service start  WITH service in("
						+ serviceRoot
						+ ") connect BY nocycle prior serviceid = parentid ) )ak  where "
						+ getPermissionServiceSql(userId, sevicetype)
						+ " and ak.service like '"
						+ content
						+ "') a "
						+ " left join (SELECT * FROM kbdata kk WHERE  kk.abstract NOT LIKE '%(删除标识符近类)' ) b  "
						+ " on a.serviceid=b.serviceid   "
						+ " left join ("
						+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
						+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
						+ " where c.kbansvaliddateid=d.kbansvaliddateid "
						+ " and d.kbanspakid=e.kbanspakid "
						+ " and e.kbansqryinsid=f.kbansqryinsid "
						+ " and f.kbcontentid=g.kbcontentid "
						+ " and f.servicetype='" + sevicetype + "' ");
		sqlCount.append(" ) al  on b.kbdataid = al.kbdataid  ");

		GlobalValue.myLog.info("搜索-业务查询COUNT：" + sqlCount.toString());

		rs = Database.executeQuery(sqlCount.toString());
		if (rs != null) {
			count = Integer.valueOf(rs.getRows()[0].get("count").toString());
		}
		return count;
	}

	/**
	 *@description 通过摘要查询关联答案知识记录数
	 *@param sevicetype
	 *            四层机构串
	 *@param content
	 *            模糊查询内容
	 *@param brandStr
	 *            品牌组合串
	 *@param abs
	 *@return
	 *@returnType int
	 */
	public static int getKnowledgeByAbstractCount(String sevicetype,
			String content, String brandStr, String abs,/*
														 * List<String>
														 * permitServices
														 */String userId) {
		Result rsCount = null;
		int count = 0;
		StringBuilder sqlCount = new StringBuilder();
		if ("".equals(abs)) {
			sqlCount
					.append("select count(*) count "
							+ "from ( select * from ( SELECT aa.serviceid , aa.service FROM service aa WHERE  aa.serviceid in (SELECT serviceid  FROM service WHERE "
							+ getPermissionServiceSql(userId, sevicetype)
							+ " start  WITH service in("
							+ brandStr
							+ ") connect BY nocycle prior serviceid = parentid )    )ak ) a "
							+ " inner join (SELECT * FROM kbdata kk WHERE kk.abstract  like '"
							+ content
							+ "'  and kk.abstract not like '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		} else {
			sqlCount
					.append("select count(*) count "
							+ "from ( select * from ( SELECT aa.serviceid , aa.service FROM service aa WHERE  aa.serviceid in  (SELECT serviceid  FROM service WHERE "
							+ getPermissionServiceSql(userId, sevicetype)
							+ " start  WITH service in("
							+ brandStr
							+ ") connect BY nocycle prior serviceid = parentid )     )ak ) a "
							+ " inner join (SELECT * FROM kbdata kk WHERE kk.abstract  in ("
							+ abs
							+ ")  and kk.abstract not like '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		}
		sqlCount.append(" ) al  on b.kbdataid = al.kbdataid  ");
		rsCount = Database.executeQuery(sqlCount.toString());

		if (rsCount != null) {
			count = Integer.valueOf(rsCount.getRows()[0].get("count")
					.toString());
		}
		return count;
	}

	/**
	 *@description 通过摘要查询关联答案知识记录数
	 *@param sevicetype
	 *            四层机构串
	 *@param content
	 *            模糊查询内容
	 *@param brandStr
	 *            品牌组合串
	 *@param abs
	 *@return
	 *@returnType int
	 */
	public static int getKnowledgeByAbstractCount(String sevicetype,
			String content, String brandStr, String abs) {
		Result rsCount = null;
		int count = 0;
		StringBuilder sqlCount = new StringBuilder();
		if ("".equals(abs)) {
			sqlCount
					.append("select count(*) count "
							+ "from ( select * from ( SELECT aa.serviceid , aa.service FROM service aa WHERE  aa.brand in ("
							+ brandStr
							+ "))ak ) a "
							+ " inner join (SELECT * FROM kbdata kk WHERE kk.abstract  like '"
							+ content
							+ "'  and kk.abstract not like '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		} else {
			sqlCount
					.append("select count(*) count "
							+ "from ( select * from ( SELECT aa.serviceid , aa.service FROM service aa WHERE  aa.brand in ("
							+ brandStr
							+ "))ak ) a "
							+ " inner join (SELECT * FROM kbdata kk WHERE kk.abstract  in ("
							+ abs
							+ ")  and kk.abstract not like '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		}
		sqlCount.append(" ) al  on b.kbdataid = al.kbdataid  ");
		rsCount = Database.executeQuery(sqlCount.toString());
		if (rsCount != null) {
			count = Integer.valueOf(rsCount.getRows()[0].get("count")
					.toString());
		}
		return count;
	}

	/**
	 *@description 通过摘要查询关联答案知识
	 *@param sevicetype
	 *            四层机构串
	 *@param content
	 *            模糊查询内容
	 *@param brandStr
	 *            品牌组合串
	 *@param abs
	 *            摘要串
	 *@param start
	 *            起始记录数
	 *@param limit
	 *            间隔记录数
	 *@return
	 *@returnType Result
	 */
	public static Result getKnowledgeByAbstract(String sevicetype,
			String content, String brandStr, String abs,/*
														 * List<String>
														 * permitServices
														 */String userId,
			int start, int limit) {
		int end = start + limit;
		StringBuilder sqlResult = new StringBuilder();
		String sql = "";
		if ("".equals(abs)) {
			sqlResult
					.append("select al.kbansvaliddateid ,a.service,a.serviceid,a.parentname,a.parentid,b.topic,b.abstract,b.kbdataid, al.answercontent "
							+ "from ( select * from ( SELECT *  FROM service aa WHERE  aa.serviceid in (SELECT serviceid  FROM service WHERE "
							+ getPermissionServiceSql(userId, sevicetype)
							+ " start  WITH service in("
							+ brandStr
							+ ") connect BY nocycle prior serviceid = parentid ) )ak) a "
							+ " inner join ( SELECT * FROM kbdata kb WHERE  kb.abstract  like '"
							+ content
							+ "'   AND kb.abstract NOT LIKE '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		} else {
			sqlResult
					.append("select al.kbansvaliddateid ,a.service,a.serviceid,a.parentname,a.parentid,b.topic,b.abstract,b.kbdataid, al.answercontent "
							+ "from ( select * from ( SELECT *  FROM service aa WHERE  aa.serviceid in (SELECT serviceid  FROM service start WHERE "
							+ getPermissionServiceSql(userId, sevicetype)
							+ "  WITH service in("
							+ brandStr
							+ ") connect BY nocycle prior serviceid = parentid ) )ak) a "
							+ " inner join ( SELECT * FROM kbdata kb WHERE  kb.abstract  in ("
							+ abs
							+ ")   AND kb.abstract NOT LIKE '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		}

		sqlResult
				.append(" ) al  on b.kbdataid = al.kbdataid order by al.kbansvaliddateid ");

		if (GetConfigValue.isOracle) {
			sql = "select * from (select SS.*, rownum rn from (";
			sqlResult.append(" ) SS where rownum<=" + end + " ) where rn >="
					+ start + "");
		} else if (GetConfigValue.isMySQL) {
			sqlResult.append(" ) SS) CC limit " + start + "," + end);
			sql = "select * from (select SS.* from (";
		}
		Result rs = Database.executeQuery(sql + sqlResult.toString());
		return rs;
	}

	/**
	 *@description 通过摘要查询关联答案知识
	 *@param sevicetype
	 *            四层机构串
	 *@param content
	 *            模糊查询内容
	 *@param brandStr
	 *            品牌组合串
	 *@param abs
	 *            摘要串
	 *@param start
	 *            起始记录数
	 *@param limit
	 *            间隔记录数
	 *@return
	 *@returnType Result
	 */
	public static Result getKnowledgeByAbstract(String sevicetype,
			String content, String brandStr, String abs, int start, int limit) {
		int end = start + limit;
		StringBuilder sqlResult = new StringBuilder();
		String sql = "";
		if ("".equals(abs)) {
			sqlResult
					.append("select al.kbansvaliddateid ,a.service,a.serviceid,a.parentname,a.parentid,b.topic,b.abstract, al.answercontent "
							+ "from ( select * from ( SELECT *  FROM service aa WHERE  aa.brand in ("
							+ brandStr
							+ ") )ak) a "
							+ " inner join ( SELECT * FROM kbdata kb WHERE  kb.abstract  like '"
							+ content
							+ "'   AND kb.abstract NOT LIKE '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		} else {
			sqlResult
					.append("select al.kbansvaliddateid ,a.service,a.serviceid,a.parentname,a.parentid,b.topic,b.abstract, al.answercontent "
							+ "from ( select * from ( SELECT *  FROM service aa WHERE  aa.brand in ("
							+ brandStr
							+ ") )ak) a "
							+ " inner join ( SELECT * FROM kbdata kb WHERE  kb.abstract  in ("
							+ abs
							+ ")   AND kb.abstract NOT LIKE '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		}

		sqlResult
				.append(" ) al  on b.kbdataid = al.kbdataid order by al.kbansvaliddateid ");
		if (GetConfigValue.isOracle) {
			sql = "select * from (select SS.*, rownum rn from (";
			sqlResult.append(" ) SS where rownum<=" + end + " ) where rn >="
					+ start + "");
		} else if (GetConfigValue.isMySQL) {
			sqlResult.append(" ) SS) CC limit " + start + "," + end);
			sql = "select * from (select SS.* from (";
		}
		Result rs = Database.executeQuery(sql + sqlResult.toString());
		return rs;
	}

	/**
	 *@description 通过相似问题内容查询关联知识记录数
	 *@param sevicetype
	 *            四层结构串
	 *@param content
	 *            模糊查询内容
	 *@param serviceRoot
	 *            业务根串
	 *@param sevicecontainer
	 *            业务归属标识
	 *@param brandStr
	 *            品牌
	 *@return
	 *@returnType int
	 */
	public static int getKnowledgeBySimilarQuestion(String sevicetype,
			String content, String serviceRoot, String sevicecontainer,
			String brandStr) {
		StringBuilder sqlCount = new StringBuilder();
		sqlCount.append("select count(*) count " + "from service a "
				+ " inner join kbdata b  "
				+ " on a.serviceid=b.serviceid   and  a.brand in (" + brandStr
				+ ") and b.abstract not like '%(删除标识符近类)'  " + " inner join ("
				+ " select question,kbdataid " + " from  Similarquestion "
				+ " where question like '" + content + "'");
		sqlCount.append(" ) al  on b.kbdataid = al.kbdataid  ");

		Result rsCount = Database.executeQuery(sqlCount.toString());
		int count = 0;
		if (rsCount != null) {
			count = Integer.valueOf(rsCount.getRows()[0].get("count")
					.toString());
		}
		return count;
	}

	/**
	 *@description 通过答案内容查询关联知识记录数
	 *@param sevicetype
	 *            四层结构串
	 *@param content
	 *            模糊查询内容
	 *@param serviceRoot
	 *            业务根串
	 *@param sevicecontainer
	 *            业务归属标识
	 *@return
	 *@returnType int
	 */
	public static int getKnowledgeByAnswerCount(String sevicetype,
			String content, String serviceRoot, /* List<String> permitServices */
			String userId) {
		StringBuilder sqlCount = new StringBuilder();
		sqlCount
				.append("select count(*) count "
						+ "from (SELECT *  FROM service Where "
						+ getPermissionServiceSql(userId, sevicetype)
						+ " start  WITH service in("
						+ serviceRoot
						+ ") connect BY nocycle prior serviceid = parentid ) a "
						+ " inner join kbdata b  "
						+ " on a.serviceid=b.serviceid   and b.abstract not like '%(删除标识符近类)' "
						+ " inner join ("
						+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
						+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
						+ " where c.kbansvaliddateid=d.kbansvaliddateid "
						+ " and d.kbanspakid=e.kbanspakid "
						+ " and e.kbansqryinsid=f.kbansqryinsid "
						+ " and f.kbcontentid=g.kbcontentid "
						+ " and f.servicetype='" + sevicetype + "' "
						+ " and g.answercontent like '" + content + "'");
		sqlCount.append(" ) al  on b.kbdataid = al.kbdataid  ");

		Result rsCount = Database.executeQuery(sqlCount.toString());
		int count = 0;
		if (rsCount != null) {
			count = Integer.valueOf(rsCount.getRows()[0].get("count")
					.toString());
		}
		return count;
	}

	/**
	 *@description 通过答案内容查询关联知识
	 *@param sevicetype
	 *            四层结构串
	 *@param content
	 *            模糊产寻内容
	 *@param start
	 *            起始记录数
	 *@param limit
	 *            间隔记录数
	 *@param serviceRoot
	 *            业务根串
	 *@return
	 *@returnType Result
	 */
	public static Result getKnowledgeByAnswer(String sevicetype,
			String content, int start, int limit, String serviceRoot, /*
																	 * List<String
																	 * >
																	 * permitServices
																	 */
			String userId) {
		int end = start + limit;
		StringBuilder sqlResult = new StringBuilder();
		sqlResult
				.append("select al.kbansvaliddateid ,a.service,a.serviceid,a.parentname,a.parentid, b.topic,b.abstract,b.kbdataid, al.answercontent "
						+ "from (SELECT *  FROM service WHERE "
						+ getPermissionServiceSql(userId, sevicetype)
						+ " start  WITH service in("
						+ serviceRoot
						+ ") connect BY nocycle prior serviceid = parentid ) a "
						+ " inner join kbdata b  "
						+ " on a.serviceid=b.serviceid  and b.abstract not like '%(删除标识符近类)' "
						+ " inner join ("
						+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
						+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
						+ " where c.kbansvaliddateid=d.kbansvaliddateid "
						+ " and d.kbanspakid=e.kbanspakid "
						+ " and e.kbansqryinsid=f.kbansqryinsid "
						+ " and f.kbcontentid=g.kbcontentid "
						+ " and f.servicetype='"
						+ sevicetype
						+ "' "
						+ " and g.answercontent like '" + content + "'");
		sqlResult
				.append(" ) al  on b.kbdataid = al.kbdataid order by al.kbansvaliddateid ");

		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select * from (select SS.*, rownum rn from (";
			sqlResult.append(" ) SS where rownum<=" + end + " ) where rn >="
					+ start + "");
		} else if (GetConfigValue.isMySQL) {
			sql = "select * from (select SS.* from (";
			sqlResult.append(" ) SS ) CC  limit " + start + "," + end);
		}

		Result rs = Database.executeQuery(sql + sqlResult.toString());
		return rs;
	}

	/**
	 *@description 通过相似问题内容查询关联知识
	 *@param sevicetype
	 *            四层结构串
	 *@param content
	 *            模糊产寻内容
	 *@param start
	 *            起始记录数
	 *@param limit
	 *            间隔记录数
	 *@param serviceRoot
	 *            业务根串
	 *@param sevicecontainer
	 *            业务归属标识
	 *@param brandStr
	 *            品牌
	 *@return
	 *@returnType Result
	 */
	public static Result getKnowledgeBySimilarQuestion(String sevicetype,
			String content, int start, int limit, String serviceRoot,
			String sevicecontainer, String brandStr) {
		int end = start + limit;
		StringBuilder sqlResult = new StringBuilder();
		sqlResult
				.append("select a.service,a.serviceid,a.parentname,a.parentid, b.topic,b.abstract, al.question "
						+ "from service a "
						+ " inner join kbdata b  "
						+ " on a.serviceid=b.serviceid  and  a.brand in ("
						+ brandStr
						+ ") and b.abstract not like '%(删除标识符近类)' "
						+ " inner join ("
						+ " select question ,kbdataid "
						+ " from   Similarquestion "
						+ " where question like '"
						+ content + "'");
		sqlResult.append(" ) al  on b.kbdataid = al.kbdataid  ");
		String sql = "";
		if (GetConfigValue.isOracle) {
			sql = "select * from (select SS.*, rownum rn from (";
			sqlResult.append(" ) SS where rownum<=" + end + " ) where rn >="
					+ start + "");
		} else if (GetConfigValue.isMySQL) {
			sql = "select * from (select SS.* from (";
			sqlResult.append(" ) SS ) CC  limit " + start + "," + end);
		}
		Result rs = Database.executeQuery(sql + sqlResult.toString());
		return rs;
	}

	/**
	 * 通过扩展问查询关联知识
	 * 
	 * @param content
	 * @param sevicetype
	 * @param rootServices
	 * @param start
	 * @param limit
	 * @return
	 */
	public static int getKnowledgeByExtendQueryCount(String content,
			String sevicetype, String rootServices, String userId) {
		String sql = "";
		sql = sql + " SELECT COUNT(*) count";
		sql = sql + " FROM";
		sql = sql + "   (SELECT b.serviceid,";
		sql = sql + "     b.service,";
		sql = sql + "     b.parentid,";
		sql = sql + "     a.abstract,";
		sql = sql + "     a.kbdataid,";
		sql = sql + "     q.query";
		sql = sql + "   FROM querymanage q,";
		sql = sql + "     kbdata a,";
		sql = sql + "     (SELECT *";
		sql = sql + "     FROM service";
		sql = sql + "       START WITH service         IN (" + rootServices
				+ ")";
		sql = sql + "       CONNECT BY prior serviceid = parentid";
		sql = sql + "     ) b";
		sql = sql + "   WHERE q.kbdataid = a.kbdataid";
		sql = sql + "   AND a.serviceid  = b.serviceid";
		sql = sql + "   AND q.query LIKE '" + content + "'";
		sql = sql + "   ) queries,";
		sql = sql + "   (SELECT b.kbdataid,";
		sql = sql + "     c.kbansvaliddateid,";
		sql = sql + "     g.kbanswerid,";
		sql = sql + "     g.kbcontentid,";
		sql = sql + "     g.answercontent,";
		sql = sql + "     f.channel,";
		sql = sql + "     f.answercategory,";
		sql = sql + "     f.customertype,";
		sql = sql + "     TO_CHAR(c.begintime,'yyyy-MM-dd') begintime,";
		sql = sql + "     TO_CHAR(c.endtime,'yyyy-MM-dd') endtime,";
		sql = sql + "     f.servicetype,";
		sql = sql + "     g.answer_clob,";
		sql = sql + "     f.city,";
		sql = sql + "     f.excludedcity";
		sql = sql + "   FROM kbdata b,";
		sql = sql + "     kbansvaliddate c,";
		sql = sql + "     kbanspak d,";
		sql = sql + "     kbansqryins e,";
		sql = sql + "     kbcontent f,";
		sql = sql + "     kbanswer g";
		sql = sql + "   WHERE b.kbdataid      =c.kbdataid";
		sql = sql + "   AND c.kbansvaliddateid=d.kbansvaliddateid";
		sql = sql + "   AND d.kbanspakid      =e.kbanspakid";
		sql = sql + "   AND e.kbansqryinsid   =f.kbansqryinsid";
		sql = sql + "   AND f.kbcontentid     =g.kbcontentid";
		sql = sql + "   AND f.servicetype     = '" + sevicetype + "'";
		sql = sql + "   ) answer";
		sql = sql + " WHERE queries.kbdataid = answer.kbdataid(+) ";

		// 允许的SERVICE
		sql = sql + " AND" + getPermissionServiceSql(userId, sevicetype);

		Result rsCount = Database.executeQuery(sql);
		int count = 0;
		if (rsCount != null) {
			count = Integer.valueOf(rsCount.getRows()[0].get("count")
					.toString());
		}
		return count;
	}

	/**
	 * 通过扩展问查询关联知识
	 * 
	 * @param content
	 * @param sevicetype
	 * @param rootServices
	 * @param start
	 * @param limit
	 * @return
	 */
	public static Result getKnowledgeByExtendQuery(String content,
			String sevicetype, String rootServices, String userId, int start,
			int limit) {
		int end = start + limit;
		String sql = "";
		sql = sql
				+ " SELECT queries.serviceid,queries.service,queries.parentid,queries.abstract,queries.kbdataid,queries.topic,queries.query,answer.answercontent,answer.channel";
		sql = sql + " FROM";
		sql = sql + "   (SELECT b.serviceid,";
		sql = sql + "     b.service,";
		sql = sql + "     b.parentid,";
		sql = sql + "     a.abstract,";
		sql = sql + "     a.kbdataid,";
		sql = sql + "     a.topic,";
		sql = sql + "     q.query";
		sql = sql + "   FROM querymanage q,";
		sql = sql + "     kbdata a,";
		sql = sql + "     (SELECT *";
		sql = sql + "     FROM service";
		sql = sql + "       START WITH service         IN (" + rootServices
				+ ")";
		sql = sql + "       CONNECT BY prior serviceid = parentid";
		sql = sql + "     ) b";
		sql = sql + "   WHERE q.kbdataid = a.kbdataid";
		sql = sql + "   AND a.serviceid  = b.serviceid";
		sql = sql + "   AND q.query LIKE '" + content + "'";
		sql = sql + "   ) queries,";
		sql = sql + "   (SELECT b.kbdataid,";
		sql = sql + "     c.kbansvaliddateid,";
		sql = sql + "     g.kbanswerid,";
		sql = sql + "     g.kbcontentid,";
		sql = sql + "     g.answercontent,";
		sql = sql + "     f.channel,";
		sql = sql + "     f.answercategory,";
		sql = sql + "     f.customertype,";
		sql = sql + "     TO_CHAR(c.begintime,'yyyy-MM-dd') begintime,";
		sql = sql + "     TO_CHAR(c.endtime,'yyyy-MM-dd') endtime,";
		sql = sql + "     f.servicetype,";
		sql = sql + "     g.answer_clob,";
		sql = sql + "     f.city,";
		sql = sql + "     f.excludedcity";
		sql = sql + "   FROM kbdata b,";
		sql = sql + "     kbansvaliddate c,";
		sql = sql + "     kbanspak d,";
		sql = sql + "     kbansqryins e,";
		sql = sql + "     kbcontent f,";
		sql = sql + "     kbanswer g";
		sql = sql + "   WHERE b.kbdataid      =c.kbdataid";
		sql = sql + "   AND c.kbansvaliddateid=d.kbansvaliddateid";
		sql = sql + "   AND d.kbanspakid      =e.kbanspakid";
		sql = sql + "   AND e.kbansqryinsid   =f.kbansqryinsid";
		sql = sql + "   AND f.kbcontentid     =g.kbcontentid";
		sql = sql + "   AND f.servicetype     = '" + sevicetype + "'";
		sql = sql + "   ) answer";
		sql = sql + " WHERE queries.kbdataid = answer.kbdataid(+) ";

		// 允许的SERVICE
		sql = sql + " AND" + getPermissionServiceSql(userId, sevicetype);

		if (GetConfigValue.isOracle) {
			sql = "select * from (select SS.*, rownum rn from (" + sql
					+ " ) SS where rownum<=" + end + " ) where rn >=" + start;
		} else if (GetConfigValue.isMySQL) {
			sql = "select * from (select SS.* from (" + sql
					+ " ) SS ) CC  limit " + start + "," + end;
		}
		Result rs = Database.executeQuery(sql);
		return rs;
	}

	/**
	 * 获取标准问地市
	 * 
	 * @param kbdataid
	 * @return
	 */
	public static Result getKbdataCities(String kbdataid) {
		return Database.executeQuery(
				"select city from kbdata where kbdataid=?", kbdataid);
	}

	/**
	 * 获取标准问地市
	 * 
	 * @param kbdataid
	 * @return
	 */
	public static String getKbdataCitiesStr(String kbdataid) {
		Result rs = getKbdataCities(kbdataid);
		if (rs != null && rs.getRowCount() > 0) {
			return (rs.getRows()[0] == null ? "" : rs.getRows()[0].get("city")
					.toString());
		}
		return "";
	}

	/**
	 * 
	 *@param sevicetype
	 *@param content
	 *@param sevicecontainer
	 *@param subjectTreeName
	 *@param serviceRoot
	 *@return
	 *@returnType int
	 *@dateTime 2017-7-10上午10:51:00
	 */
	public static int getKnowledgeByServiceCount(String sevicetype,
			String content, String sevicecontainer, String subjectTreeName,
			String serviceRoot) {
		Result rs = null;
		int count = 0;
		StringBuilder sqlCount = new StringBuilder();
		if (!"all".equals(sevicecontainer)) {
			sqlCount
					.append("select count(*) count from ( select * from ( SELECT aa.serviceid , aa.service FROM service aa WHERE aa.brand='"
							+ subjectTreeName
							+ "')ak  where ak.service like '"
							+ content
							+ "') a "
							+ " left join (SELECT * FROM kbdata kk WHERE  kk.abstract NOT LIKE '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid  "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		} else {
			sqlCount
					.append("select count(*) count  from ( select * from ( SELECT aa.serviceid , aa.service FROM service aa WHERE  aa.serviceid in(SELECT serviceid  FROM service start  WITH service in("
							+ serviceRoot
							+ ") connect BY nocycle prior serviceid = parentid ) )ak  where ak.service like '"
							+ content
							+ "') a "
							+ " left join (SELECT * FROM kbdata kk WHERE  kk.abstract NOT LIKE '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid   "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		}
		sqlCount.append(" ) al  on b.kbdataid = al.kbdataid  ");
		rs = Database.executeQuery(sqlCount.toString());
		if (rs != null) {
			count = Integer.valueOf(rs.getRows()[0].get("count").toString())
					.intValue();
		}
		return count;
	}

	// public static int getKnowledgeByAnswerCount(String sevicetype, String
	// content, String serviceRoot, String sevicecontainer)
	// {
	// StringBuilder sqlCount = new StringBuilder();
	// sqlCount
	// .append("select count(*) count from (SELECT *  FROM service start  WITH service in("
	// +
	// serviceRoot + ") connect BY nocycle prior serviceid = parentid ) a " +
	// " inner join kbdata b  " +
	// " on a.serviceid=b.serviceid   and b.abstract not like '%(删除标识符近类)' " +
	// " inner join (" +
	// " select c.kbansvaliddateid,c.kbdataid,g.answercontent " +
	// "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
	// +
	// " where c.kbansvaliddateid=d.kbansvaliddateid " +
	// " and d.kbanspakid=e.kbanspakid " +
	// " and e.kbansqryinsid=f.kbansqryinsid " +
	// " and f.kbcontentid=g.kbcontentid " +
	// " and f.servicetype='" + sevicetype + "' " +
	// " and g.answercontent like '" + content + "'");
	// sqlCount
	// .append(" ) al  on b.kbdataid = al.kbdataid  ");
	//
	// Result rsCount = Database.executeQuery(sqlCount.toString());
	// int count = 0;
	// if (rsCount != null) {
	// count =
	// Integer.valueOf(rsCount.getRows()[0].get("count").toString()).intValue();
	// }
	// return count;
	// }

	// public static Result getKnowledgeByAnswer(String sevicetype, String
	// content, int start, int limit, String serviceRoot, String
	// sevicecontainer)
	// {
	// int end = start + limit;
	// StringBuilder sqlResult = new StringBuilder();
	// sqlResult
	// .append("select al.kbansvaliddateid ,a.service,a.serviceid,a.parentname,a.parentid, b.topic,b.abstract, al.answercontent from (SELECT *  FROM service start  WITH service in("
	// +
	// serviceRoot + ") connect BY nocycle prior serviceid = parentid ) a " +
	// " inner join kbdata b  " +
	// " on a.serviceid=b.serviceid  and b.abstract not like '%(删除标识符近类)' " +
	// " inner join (" +
	// " select c.kbansvaliddateid,c.kbdataid,g.answercontent " +
	// "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
	// +
	// " where c.kbansvaliddateid=d.kbansvaliddateid " +
	// " and d.kbanspakid=e.kbanspakid " +
	// " and e.kbansqryinsid=f.kbansqryinsid " +
	// " and f.kbcontentid=g.kbcontentid " +
	// " and f.servicetype='" + sevicetype + "' " +
	// " and g.answercontent like '" + content + "'");
	// sqlResult
	// .append(" ) al  on b.kbdataid = al.kbdataid order by al.kbansvaliddateid ");
	// String sql = "";
	// if (GetConfigValue.isOracle) {
	// sql = "select * from (select SS.*, rownum rn from (";
	// sqlResult.append(" ) SS where rownum<=" + end + " ) where rn >=" +
	// start);
	// } else if (GetConfigValue.isMySQL) {
	// sql = "select * from (select SS.* from (";
	// sqlResult.append(" ) SS ) CC  limit " + start + "," + end);
	// }
	// Result rs = Database.executeQuery(sql + sqlResult.toString());
	// return rs;
	// }
	public static Result getKnowledgeByService(String sevicetype,
			String content, String serviceRoot, String sevicecontainer,
			String subjectTreeName, int start, int limit) {
		Result rs = null;
		int end = start + limit;
		StringBuilder sqlResult = new StringBuilder();
		String sql = "";
		if (!"all".equals(sevicecontainer)) {
			sqlResult
					.append("select al.kbansvaliddateid ,a.service,a.serviceid,a.parentname,a.parentid, b.topic,b.abstract, al.answercontent from ( select * from ( SELECT * FROM service aa WHERE aa.brand ='"
							+ subjectTreeName
							+ "' )ak  where ak.service like '"
							+ content
							+ "') a "
							+ " left join (SELECT * FROM kbdata kk WHERE  kk.abstract NOT LIKE '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid  "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		} else {
			sqlResult
					.append("select al.kbansvaliddateid ,a.service,a.serviceid, a.parentname,a.parentid, b.topic,b.abstract, al.answercontent from ( select * from ( SELECT *  FROM service aa WHERE  aa.serviceid in  (SELECT serviceid  FROM service start  WITH service in("
							+ serviceRoot
							+ ") connect BY nocycle prior serviceid = parentid ) )ak  where ak.service like '"
							+ content
							+ "') a "
							+ " left join (SELECT * FROM kbdata kk WHERE  kk.abstract NOT LIKE '%(删除标识符近类)' ) b  "
							+ " on a.serviceid=b.serviceid   "
							+ " left join ("
							+ " select c.kbansvaliddateid,c.kbdataid,g.answercontent "
							+ "from  kbansvaliddate c,kbanspak d,kbansqryins e,kbcontent f,kbanswer g  "
							+ " where c.kbansvaliddateid=d.kbansvaliddateid "
							+ " and d.kbanspakid=e.kbanspakid "
							+ " and e.kbansqryinsid=f.kbansqryinsid "
							+ " and f.kbcontentid=g.kbcontentid "
							+ " and f.servicetype='" + sevicetype + "' ");
		}

		sqlResult
				.append(" ) al  on b.kbdataid = al.kbdataid order by al.kbansvaliddateid ");
		if (GetConfigValue.isOracle) {
			sql = "select * from (select SS.*, rownum rn from (";
			sqlResult.append(" ) SS where rownum<=" + end + " ) where rn >="
					+ start);
		} else if (GetConfigValue.isMySQL) {
			sqlResult.append(" ) SS) CC limit " + start + "," + end);
			sql = "select * from (select SS.* from (";
		}
		rs = Database.executeQuery(sql + sqlResult.toString());
		return rs;
	}

	public static int insertOrUpdate(User user, String Operationtype,
			String kbdataid, String kbansvaliddateid, String[] channels,
			String servicetype, String customertype, String starttime,
			String endtime, String answer, String answerType, String brand,
			String service) {
		List listSql = new ArrayList();

		List listParam = new ArrayList();

		List lstpara = new ArrayList();
		String sql = "";
		String kbvid = "";
		String kbanspakid = "";
		String kbansqryinsid = "";
		String kbcontentid = "";
		String kbanswerid = "";

		String bussinessFlag = CommonLibMetafieldmappingDAO
				.getBussinessFlag(user.getIndustryOrganizationApplication());

		if ("update".equals(Operationtype)) {
			sql = "delete from kbansvaliddate where kbansvaliddateid = ?";

			lstpara = new ArrayList();

			lstpara.add(kbansvaliddateid);

			listSql.add(sql);

			listParam.add(lstpara);
		}
		String opanswer = "";
		for (int j = 0; j < channels.length; j++) {
			if (GetConfigValue.isOracle) {
				kbvid = String.valueOf(ConstructSerialNum.GetOracleNextValNew(
						"KBANSVALIDDATE_SEQ", bussinessFlag));
				kbanspakid = String.valueOf(ConstructSerialNum
						.GetOracleNextValNew("KBANSPAK_SEQ", bussinessFlag));
				kbansqryinsid = String.valueOf(ConstructSerialNum
						.GetOracleNextValNew("KBANSQRYINS_SEQ", bussinessFlag));
				kbcontentid = String
						.valueOf(ConstructSerialNum.GetOracleNextValNew(
								"SEQ_KBCONTENT_ID", bussinessFlag));
				kbanswerid = String.valueOf(ConstructSerialNum
						.GetOracleNextValNew("KBANSWER_SEQ", bussinessFlag));
			} else if (GetConfigValue.isMySQL) {
				kbvid = String.valueOf(ConstructSerialNum.getSerialIDNew(
						"KBANSVALIDDATE", "KBANSVALIDDATEID", bussinessFlag));
				kbanspakid = String.valueOf(ConstructSerialNum.getSerialIDNew(
						"KBANSPAK", "KBANSPAKID", bussinessFlag));
				kbansqryinsid = String.valueOf(ConstructSerialNum
						.getSerialIDNew("KBANSQRYINS", "KBANSQRYINSID",
								bussinessFlag));
				kbcontentid = String.valueOf(ConstructSerialNum.getSerialIDNew(
						"KBCONTENT", "KBCONTENTID", bussinessFlag));
				kbanswerid = String.valueOf(ConstructSerialNum.getSerialIDNew(
						"KBANSWER", "KBANSWERID", bussinessFlag));
			}

			if (("".equals(starttime)) || ("".equals(endtime))
					|| (starttime == null) || (endtime == null)) {
				sql = "insert into kbansvaliddate(KBANSVALIDDATEID,KBDATAID,BEGINTIME,ENDTIME) values(?,?,null,null)";

				lstpara = new ArrayList();

				lstpara.add(kbvid);

				lstpara.add(kbdataid);

				listSql.add(sql);

				listParam.add(lstpara);
			} else {
				if (GetConfigValue.isOracle)
					sql = "insert into kbansvaliddate(KBANSVALIDDATEID,KBDATAID,BEGINTIME,ENDTIME) values(?,?,to_date(?,'yyyy-mm-dd hh24:mi:ss'),to_date(?,'yyyy-mm-dd hh24:mi:ss'))";
				else if (GetConfigValue.isMySQL) {
					sql = "insert into kbansvaliddate(KBANSVALIDDATEID,KBDATAID,BEGINTIME,ENDTIME) values(?,?,str_to_date(?,'%Y-%m-%d %H:%i:%s')  ,str_to_date(?,'%Y-%m-%d %H:%i:%s')  )";
				}

				lstpara = new ArrayList();

				lstpara.add(kbvid);

				lstpara.add(kbdataid);

				lstpara.add(starttime);

				lstpara.add(endtime);

				listSql.add(sql);

				listParam.add(lstpara);
			}

			sql = "insert into kbanspak(KBANSPAKID,KBANSVALIDDATEID,PACKAGE ,PACKAGECODE,PAKTYPE) values(?,?,?,?,?)";

			lstpara = new ArrayList();

			lstpara.add(kbanspakid);

			lstpara.add(kbvid);

			lstpara.add("空号码包");

			lstpara.add(null);

			lstpara.add("0");

			listSql.add(sql);

			listParam.add(lstpara);

			sql = "insert into kbansqryins(KBANSQRYINSID,KBANSPAKID,QRYINS) values(?,?,?)";

			lstpara = new ArrayList();

			lstpara.add(kbansqryinsid);

			lstpara.add(kbanspakid);

			lstpara.add("查询指令无关");

			listSql.add(sql);

			listParam.add(lstpara);

			sql = "insert into kbcontent(KBCONTENTID ,KBANSQRYINSID,CHANNEL,ANSWERCATEGORY,SERVICETYPE ,CUSTOMERTYPE) values(?,?,?,?,?,?)";

			lstpara = new ArrayList();

			lstpara.add(kbcontentid);

			lstpara.add(kbansqryinsid);

			lstpara.add(channels[j]);

			lstpara.add(answerType);

			lstpara.add(servicetype);

			lstpara.add(customertype);

			listSql.add(sql);

			listParam.add(lstpara);

			if (answer.length() < 2000) {
				sql = "insert into kbanswer(kbanswerid,kbcontentid,answercontent,servicehallstatus,city,customertype,brand) values(?,?,?,?,?,?,?)";

				lstpara = new ArrayList();

				lstpara.add(kbanswerid);

				lstpara.add(kbcontentid);

				lstpara.add(answer);

				lstpara.add("无关");

				lstpara.add("");

				lstpara.add("所有客户");

				lstpara.add(brand);

				listSql.add(sql);

				listParam.add(lstpara);

			} else {

				sql = "insert into kbanswer(kbanswerid,kbcontentid,answercontent,servicehallstatus,city,customertype,brand) values(?,?,?,?,?,?,?)";

				lstpara = new ArrayList();

				lstpara.add(kbanswerid);

				lstpara.add(kbcontentid);

				lstpara.add("");

				lstpara.add("无关");

				lstpara.add("");

				lstpara.add("所有客户");

				lstpara.add(brand);

				listSql.add(sql);

				listParam.add(lstpara);

				sql = "update kbanswer set answer_clob =? where kbanswerid =? and kbcontentid =?";
				lstpara = new ArrayList<String>();
				lstpara.add(answer);
				lstpara.add(kbanswerid);
				lstpara.add(kbcontentid);
				listSql.add(sql);
				listParam.add(lstpara);

				opanswer = answer.substring(0, 1000) + "....";

			}
			listSql.add(GetConfigValue.LogSql());
			listParam.add(GetConfigValue.LogParam(user.getUserIP(), user
					.getUserID(), user.getUserName(), " ", " ", "增加答案",
					opanswer, "KBANSWER"));
		}

		int c = Database.executeNonQueryTransaction(listSql, listParam);

		return c;
	}

	public static Result getRobotID() {
		String sql = "";
		sql += " SELECT s.name k ,";
		sql += "   t.name name";
		sql += " FROM metafield t,";
		sql += "   metafield s,";
		sql += "   metafieldmapping a";
		sql += " WHERE t.metafieldmappingid=a.metafieldmappingid";
		sql += " AND t.metafieldid         =s.stdmetafieldid";
		sql += " AND a.name                ='实体机器人ID配置'";
		sql += " AND t.stdmetafieldid     IS NULL";

		return Database.executeQuery(sql);
	}

	/**
	 * 获取允许Service的SQL
	 * 
	 * @param services
	 * @return
	 */
	private static String getPermissionServiceSql(List<String> services) {
		StringBuilder sb = new StringBuilder();
		if (services != null && services.size() > 0) {
			sb.append(" serviceid IN (");
			for (String serviceid : services) {
				sb.append("" + serviceid + ",");
			}
			if (services.size() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			sb.append(") ");
		}
		return sb.toString();
	}

	/**
	 * 获取允许Service的SQL
	 * 
	 * @param services
	 * @return
	 */
	private static String getPermissionServiceSql(String userId,
			String serviceType) {
		return " serviceid IN(select resourceid as serviceid from role_resource where roleid in(select roleid from  workerrolerel where workerid='"
				+ userId
				+ "') and resourcetype='querymanage' and servicetype ='"
				+ serviceType + "') ";
	}
}
