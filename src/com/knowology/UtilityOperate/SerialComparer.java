package com.knowology.UtilityOperate;

import java.util.Comparator;
import java.util.Map.Entry;


/**
 * 
 *描述：排序比较器
 *@author: qianlei
 *@date： 日期：2015-3-17 时间：下午02:14:07
 */
public class SerialComparer {
    
	public  Comparator<String> ReverseStrCompare = new Comparator<String>() {
		public int compare(String x, String y) {
			return StringOper.ReverseStr(x).compareTo(StringOper.ReverseStr(y));
		}
	};
	
	public static Comparator<Entry<String, Double> > AccordValueCompare = new Comparator<Entry<String, Double> >() {
		public int compare(Entry<String, Double> x, Entry<String, Double> y) {
            if (x.getValue() > y.getValue()) return 1;
            else if (x.getValue() == y.getValue()) return 0;
            else return -1;
		}
	};
}
