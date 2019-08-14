package com.knowology.UtilityOperate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.knowology.GlobalValue;

public class DateTimeOper {

	/*
	 * 获取日期中的月份 dateStr 日期时间 2006-12-21 14:40:59 dateForm 日期格式 yyyy-MM-dd
	 * HH:mm:ss
	 */
	public static int GetMonth(String dateStr, String dateForm) {
		try {
			Date dt = null;
			DateFormat format = new SimpleDateFormat(dateForm);
			dt = format.parse(dateStr);
			if (dt == null)
				return 0;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			return calendar.get(Calendar.MONTH) + 1;
		} catch (Exception e) {
			return 0;
		}
	}

	/*
	 * 获取日期中的月份 dateStr 日期时间 2006-12-21 14:40:59
	 */
	public static int GetMonth(String dateStr) {
		String dateForm = "yyyy-MM";
		try {
			Date dt = null;
			DateFormat format = new SimpleDateFormat(dateForm);
			dt = format.parse(dateStr);
			if (dt == null)
				return 0;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			return calendar.get(Calendar.MONTH) + 1;
		} catch (Exception e) {
			return 0;
		}

	}

	/*
	 * 获取年份 dateStr 日期时间 2006-12-21 14:40:59 dateForm 日期格式 yyyy-MM-dd HH:mm:ss
	 */
	public static int GetYear(String dateStr, String dateForm) {
		try {
			Date dt = null;
			DateFormat format = new SimpleDateFormat(dateForm);
			dt = format.parse(dateStr);
			if (dt == null)
				return 0;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			return calendar.get(Calendar.YEAR);
		} catch (Exception e) {
			return 0;
		}
	}

	/*
	 * 获取年份 dateStr 日期时间 2006-12-21 14:40:59
	 */
	public static int GetYear(String dateStr) {
		String dateForm = "yyyy-MM";
		try {
			Date dt = null;
			DateFormat format = new SimpleDateFormat(dateForm);
			dt = format.parse(dateStr);
			if (dt == null)
				return 0;
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dt);
			return calendar.get(Calendar.YEAR);
		} catch (Exception e) {
			return 0;
		}
	}

	/**
	 * 
	 *描述：获取格式化的当前时间
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-17 时间：上午10:46:32
	 *@return String
	 */
	public static String getDateTimeByFormat() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String datetime = df.format(new Date()).toString();
		return datetime;
	}

	/**
	 * 
	 *描述：按照提供的时间格式返回当前时间
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-17 时间：上午10:46:52
	 *@return String
	 */
	public static String getDateTimeByFormat(String dateType) {
		SimpleDateFormat df = new SimpleDateFormat(dateType);// 设置日期格式
		String datetime = df.format(new Date()).toString();
		return datetime;
	}

	/**
	 * 
	 *描述：返回当前(时，天，分，年)的前一段时间
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-17 时间：上午10:47:16
	 *@return String
	 */
	public static String getNewDateAgoTime(String type, int timespin,
			String _format) {
		String newDateAgoTime = "";
		if (_format.length() == 0) {
			_format = "yyyyMMddHHmm";
		}
		Calendar cal = Calendar.getInstance();
		if (type.equals("H")) {
			cal.add(Calendar.HOUR, -timespin); // 把时间设置为当前时间-1小时，同理，也可以设置其他时间
			newDateAgoTime = new SimpleDateFormat(_format)
					.format(cal.getTime());// 获取到完整的时间
		} else if (type.equals("D")) {
			cal.add(Calendar.DATE, -timespin); // 当前天前几天
			newDateAgoTime = new SimpleDateFormat(_format)
					.format(cal.getTime());// 获取到完整的时间
		} else if (type.equals("M")) {
			cal.add(Calendar.MONTH, -timespin); // 当前月前几月
			newDateAgoTime = new SimpleDateFormat(_format)
					.format(cal.getTime());// 获取到完整的时间
		} else {
			cal.add(Calendar.YEAR, -timespin); // 当前年前几年
			newDateAgoTime = new SimpleDateFormat(_format)
					.format(cal.getTime());// 获取到完整的时间
		}
		return newDateAgoTime;
	}

	/**
	 * 
	 *描述：判断时间是否在返回内
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-3-17 时间：上午10:48:05
	 *@return boolean
	 */
	public static boolean IsBetween(String currentTime, String startTime,
			String endTime) {
		if (startTime.compareTo(endTime) > 0) {// 如果开始时间大于结束时间，结束时间+24
			endTime = String.format("%d%s", Integer.valueOf(endTime.substring(
					0, 2)) + 24, endTime.substring(2));
		}
		return (currentTime.compareTo(startTime) >= 0 && currentTime
				.compareTo(endTime) <= 0);
	}

	/**
	 * 
	 *描述：时间如果超过当前时间，将年份调整为去年
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-1-14 时间：下午05:10:06
	 *@param date
	 *            年-月-日
	 *@return String
	 */
	public static String getLastDate(String date) {
		String lastDate = date;
		try {
			String now = getDateTimeByFormat("yyyy-MM");
			if (date.contains("-")) {
				if (now.compareTo(date) < 0) {
					int b1 = date.indexOf('-');
					int year = Integer.valueOf(date.substring(0, b1));
					lastDate = year - 1 + date.substring(b1, date.length());
				}
			}
		} catch (Exception e) {
			GlobalValue.myLog.error("【年份纠正出错】" + date);
		}
		return lastDate;
	}

	/**
	 * 
	 *描述：封装程序内部DESC日志时间获取函数
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-5-18 时间：下午02:21:14
	 *@return String
	 */
	public static String getDescTime() {
		return getDateTimeByFormat("HH:mm:ss.SSS");
	}

	/**
	 * 格式化时间，加上今天或者昨天的标记
	 * 
	 * @param time
	 *            yyyy-MM-dd HH:mm:ss
	 * @return
	 */
	public static String formatDateTime(String time) {
		SimpleDateFormat format = new java.text.SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		if (time == null || "".equals(time)) {
			return "";
		}
		Date date = null;
		try {
			date = format.parse(time);
		} catch (Exception e) {
			GlobalValue.myLog.error("【时间格式错误】" + e.toString());
		}

		Calendar current = Calendar.getInstance();

		Calendar today = Calendar.getInstance(); // 今天

		today.set(Calendar.YEAR, current.get(Calendar.YEAR));
		today.set(Calendar.MONTH, current.get(Calendar.MONTH));
		today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
		today.set(Calendar.HOUR_OF_DAY, 0);// Calendar.HOUR――12小时制的小时数
		// Calendar.HOUR_OF_DAY――24小时制的小时数
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);

		Calendar tommory = Calendar.getInstance(); // 明天

		tommory.set(Calendar.YEAR, current.get(Calendar.YEAR));
		tommory.set(Calendar.MONTH, current.get(Calendar.MONTH));
		tommory.set(Calendar.DAY_OF_MONTH,
				current.get(Calendar.DAY_OF_MONTH) + 1);
		tommory.set(Calendar.HOUR_OF_DAY, 0);
		tommory.set(Calendar.MINUTE, 0);
		tommory.set(Calendar.SECOND, 0);
		tommory.set(Calendar.MILLISECOND, 0);// 字符串时间转化得到和赋值得到两种方式，在MILLISECOND的值上存在差异，此处做处理

		Calendar yesterday = Calendar.getInstance(); // 昨天

		yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
		yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
		yesterday.set(Calendar.DAY_OF_MONTH,
				current.get(Calendar.DAY_OF_MONTH) - 1);
		yesterday.set(Calendar.HOUR_OF_DAY, 0);
		yesterday.set(Calendar.MINUTE, 0);
		yesterday.set(Calendar.SECOND, 0);
		yesterday.set(Calendar.MILLISECOND, 0);// 字符串时间转化得到和赋值得到两种方式，在MILLISECOND的值上存在差异，此处做处理

		current.setTime(date);
//		System.out.println(current.getTime());
//		System.out.println(today.getTime());
//		System.out.println(yesterday.getTime());
//		System.out.println(tommory.getTime());
//		System.out.println(current.compareTo(today));
//		System.out.println(current.compareTo(yesterday));
		if ((current.after(today) && current.before(tommory))
				|| current.compareTo(today) == 0) {
			return "今天 " + time.split(" ")[1];
		} else if ((current.before(today) && current.after(yesterday))
				|| current.compareTo(yesterday) == 0) {
			return "昨天 " + time.split(" ")[1];
		} else {
			int index = time.indexOf("-") + 1;
			return time.substring(index, time.length());
		}
	}

	/**
	 * 
	 *描述：计算时间间隔
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-11-22 时间：下午03:18:04
	 *@param b
	 *@param e
	 *@return long
	 */
	public static long timeInterval(long b, long e) {
		long between = 0;
		try {
			Date begin = new Date(b + 3600 * 1000);
			Date end = new Date(e + 3600 * 1000);
			between = (end.getTime() - begin.getTime());// 得到两者的毫秒数
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return between;
	}

    /** 
     * 获取当月的 天数 
     * */  
    public static int getCurrentMonthDay() {  
          
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }  
  
    /** 
     * 根据年 月 获取对应的月份 天数 
     * */  
    public static int getDaysByYearMonth(int year, int month) {  
          
        Calendar a = Calendar.getInstance();  
        a.set(Calendar.YEAR, year);  
        a.set(Calendar.MONTH, month - 1);  
        a.set(Calendar.DATE, 1);  
        a.roll(Calendar.DATE, -1);  
        int maxDate = a.get(Calendar.DATE);  
        return maxDate;  
    }  
	
	public static void main(String[] args) {
		System.out.println(formatDateTime("2016-11-21 21:00:00"));
	}
}
