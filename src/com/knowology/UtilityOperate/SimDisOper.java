package com.knowology.UtilityOperate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.servlet.jsp.jstl.sql.Result;

import com.knowology.bll.CommonLibMetafieldmappingDAO;

public class SimDisOper {

	// / <summary>
	// / 获取编辑跟离,该编辑距离是带有交换的
	// / </summary>
	// / <param name="s1"></param>
	// / <param name="s2"></param>
	// / <returns></returns>
	public static Integer GetEditDis(String source, String target) {
		if (source.isEmpty() || source == null) {
			if (target.isEmpty() || target == null) {
				return 0;
			} else {
				return target.length();
			}
		} else if (target.isEmpty() || target == null) {
			return source.length();
		}

		int m = source.length();
		int n = target.length();
		int H[][] = new int[m + 2][n + 2];
		int INF = m + n;
		H[0][0] = INF;
		for (int i = 0; i <= m; i++) {
			H[i + 1][1] = i;
			H[i + 1][0] = INF;
		}
		for (int j = 0; j <= n; j++) {
			H[1][j + 1] = j;
			H[0][j + 1] = INF;
		}

		Map<Character, Integer> sd = new HashMap<Character, Integer>();
		for (Character Letter : (source + target).toCharArray()) {
			if (!sd.containsKey(Letter))
				sd.put(Letter, 0);
		}

		for (Integer i = 1; i <= m; i++) {
			Integer DB = 0;
			for (Integer j = 1; j <= n; j++) {
				Integer i1 = sd.get(target.charAt(j - 1));
				Integer j1 = DB;

				if (source.charAt(i - 1) == target.charAt(j - 1)) {
					H[i + 1][j + 1] = H[i][j];
					DB = j;
				} else {
					H[i + 1][j + 1] = Math.min(H[i][j], Math.min(H[i + 1][j],
							H[i][j + 1])) + 1;
				}

				H[i + 1][j + 1] = Math.min(H[i + 1][j + 1], H[i1][j1]
						+ (i - i1 - 1) + 1 + (j - j1 - 1));
			}
			sd.put(source.charAt(i - 1), i);
		}

		return H[m + 1][n + 1];
	}

	public static class StoreTop10Value {
		public Integer count = 0;

		public Comparator<Double> MyComparer = new Comparator<Double>() {
			public int compare(Double d1, Double d2) {
				return d2.compareTo(d1);
			}
		};

		TreeMap<Double, String> top10lst = new TreeMap<Double, String>(
				MyComparer);

		public void Add(double value, String s) {
			count++;
			if (top10lst.containsKey(value)) {
				String stmp = top10lst.get(value) + ";" + s;
				top10lst.put(value, stmp);
			} else {
				top10lst.put(value, s);
			}
		}

		public ArrayList<String> GetTop10Values() {
			ArrayList<String> result = new ArrayList<String>();
			Integer count = 0;
			for (Entry<Double, String> iter : top10lst.entrySet()) {
				String val = iter.getValue();
				if (val.contains(";")) {
					if (val.charAt(0) == ';') {
						// 防止第一个字符为“；”导致截出的数组第一个为空
						val.substring(1, val.length() - 1);
					}
					String[] arry = val.split(";");
					List<String> item = java.util.Arrays.asList(arry);
					count += item.size();
					result.addAll(item);
				} else {
					count++;
					result.add(val);
				}
				if (count >= 10)
					break;
			}
			return result;
		}

		public ArrayList<Entry<Double, String>> GetTop10KeyValuePairs() {
			ArrayList<Entry<Double, String>> result = new ArrayList<Entry<Double, String>>();
			Integer count = 0;
			for (Entry<Double, String> iter : top10lst.entrySet()) {
				String val = iter.getValue();
				if (val.contains(";")) {
					// List<String> item = new List<String>(val.Split(new
					// String[] { ";" },
					// StringSplitOptions.RemoveEmptyEntries));
					if (val.charAt(0) == ';') {
						// 防止第一个字符为“；”导致截出的数组第一个为空
						val.substring(1, val.length() - 1);
					}
					String[] arry = val.split(";");
					List<String> item = java.util.Arrays.asList(arry);
					count += item.size();
					for (String str : item) {
						Map<Double, String> map = new HashMap<Double, String>();
						map.put(iter.getKey(), str);
						result.addAll(map.entrySet());
					}
				} else {
					count++;
					Map<Double, String> map = new HashMap<Double, String>();
					map.put(iter.getKey(), iter.getValue());
					result.addAll(map.entrySet());
				}
				if (count >= 10)
					break;
			}
			return result;
		}
	}

	// / <summary>
	// / 从simRange中获取s1的相似串，最多获取个数不超过10个。
	// / </summary>
	// / <param name="s1"></param>
	// / <param name="simRange"></param>
	// / <returns></returns>
	public static ArrayList<String> GetSimStr(String s1,
			ArrayList<String> simRange, String type) {
		ArrayList<Entry<Double, String>> res1 = GetSimStrWithVal(s1, simRange,
				type);
		ArrayList<String> res2 = new ArrayList<String>();
		if (res1.size() == 0)
			return res2;
		for (Entry<Double, String> iter : res1) {
			res2.add(iter.getValue());
		}
		return res2;

	}

	// / <summary>
	// / 从simRange中获取s1\s2的相似串，最多获取个数不超过10个。
	// / 【目前实现策略】
	// / 同时包括则认为相似，长度更短则相似度更高
	// / </summary>
	// / <param name="s1"></param>
	// / <param name="simRange"></param>
	// / <returns></returns>
	public static ArrayList<String> GetSimStr(String s1, String s2,
			ArrayList<String> simRange, String type) {

		ArrayList<String> result = new ArrayList<String>();
		if (simRange == null || simRange.size() == 0)
			return result;
		TreeMap<Integer, ArrayList<String>> sortedDic = new TreeMap<Integer, ArrayList<String>>();
		for (String str : simRange) {
			if (str.contains(s1) && str.contains(s2)) {
				if (sortedDic.containsKey(str.length())) {
					sortedDic.get(str.length()).add(str);
				} else {
					ArrayList<String> tmpList = new ArrayList<String>();
					tmpList.add(str);
					sortedDic.put(str.length(), tmpList);
				}
			}
		}
		if (sortedDic.size() == 0)
			return result;
		for (Entry<Integer, ArrayList<String>> iter : sortedDic.entrySet()) {
			if (result.size() > 10)
				break;
			result.addAll(iter.getValue());
		}
		return result;
	}

	/*
	 * 读取配置文件
	 */
	public static String getConfigValues(String key) {
		
//			读取指定路径配置文件
//			String result = "";
//		
//			Properties prop = new Properties();  
//			try {
//				prop.load(new FileInputStream("/cpic/cpicapp/conf/CommonLibConfig.properties"));
//				result =  prop.getProperty(key);
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return result;
		
			ResourceBundle resourcesTable = ResourceBundle.getBundle("CommonLibConfig");
			return resourcesTable.getString(key);
	}

	public static ArrayList<Entry<Double, String>> GetSimStrWithVal(String s1,
			ArrayList<String> simRange, String type) {

		double thred = 0;
		if (type == "0")
			thred = Double.parseDouble(getConfigValues("ABCNumberSimThred"));
		else
			thred = Double.parseDouble(getConfigValues("ServiceSimThred"));
		StoreTop10Value stv = new StoreTop10Value();
		// Integer count = 0;
		for (String s : simRange) {
			Double sim = 0.0;
			if (type != "0") {
				sim = GetSimDis(s1, s);
			} else
				sim = GetLetterNumberSimDis(s1.toUpperCase(), s);
			if (sim > thred) {
				stv.count++;
				stv.Add(sim, s);
			}

		}
		return stv.GetTop10KeyValuePairs();

	}

	// / <summary>
	// / 获取两个字符串的相似度
	// / </summary>
	// / <param name="s1"></param>
	// / <param name="s2"></param>
	// / <returns></returns>
	public static Double GetSimDis(String s1, String s2) {
		double a = Double.parseDouble(getConfigValues("PrexSimModulus"));
		double b = Double.parseDouble(getConfigValues("SufSimModulus"));
		double c = Double.parseDouble(getConfigValues("EditSimModulus"));
		double d = Double.parseDouble(getConfigValues("ContainSimModulus"));
		return a * GetPrexSim(s1, s2) + b * GetSufSim(s1, s2) + c
				* GetEditSim(s1, s2) + d * GetContainSim(s1, s2);
		// double sim = 0;
		// if (a != 0)
		// { }
	}

	public static Double GetABCSimDis(String s1, String s2) {
		double a = Double.parseDouble(getConfigValues("ABC_PrexSimModulus"));
		double b = Double.parseDouble(getConfigValues("ABC_SufSimModulus"));
		double c = Double.parseDouble(getConfigValues("ABC_EditSimModulus"));
		double d = Double.parseDouble(getConfigValues("ABC_ContainSimModulus"));
		return a * GetPrexSim(s1, s2) + b * GetSufSim(s1, s2) + c
				* GetEditSim(s1, s2) + d * GetContainSim(s1, s2);

	}

	// / <summary>
	// / 获取英文数字串的相似度
	// / </summary>
	// / <param name="s1"></param>
	// / <param name="s2"></param>
	// / <returns></returns>
	public static Double GetLetterNumberSimDis(String s1, String s2) {
		Integer len1 = s1.length();
		Integer len2 = s2.length();
		Integer dif = Math.abs(len1 - len2);
		if ((s1.length() < 3 || s2.length() < 3) && dif >= 2)
			return 0.0;
		else {
			return GetABCSimDis(s1, s2);
		}
	}

	// / <summary>
	// / 获取前缀相似度
	// / </summary>
	// / <returns></returns>
	public static Double GetPrexSim(String s1, String s2) {
		Integer len = Math.min(s1.length(), s2.length());
		Integer i = 0;
		for (i = 0; i < s1.length(); i++) {
			if (s2.length() > i && s1.charAt(i) == s2.charAt(i))
				continue;
			else
				break;
		}
		return (double) i / (double) len;

	}

	public static Double GetSufSim(String s1, String s2) {
		Integer len = Math.min(s1.length(), s2.length());
		Integer i = s1.length();
		Integer j = s2.length();
		Integer simLen = 0;
		while (i >= 0 && j >= 0) {
			i--;
			j--;
			if (i >= 0 && j >= 0 && s2.charAt(j) == s1.charAt(i))
				simLen++;
			else
				break;
		}
		return (double) simLen / (double) len;

	}

	public static Double GetEditSim(String s1, String s2) {
		Integer editDis = 0;
		editDis = GetEditDis(s1, s2);
		double sim = 1 - (double) editDis
				/ (double) Math.max(s1.length(), s2.length());
		return sim;
	}

	public static Double GetContainSim(String s1, String s2) {
		Map<Character, Double> dic = new HashMap<Character, Double>();
		for (Character c : dic.keySet()) {
			if (dic.containsKey(c))
				continue;
			dic.put(c, 0.0);
		}
		double count = 0;
		char[] cc = s2.toCharArray();
		for (Character c : cc) {
			if (dic.containsKey(c)) {
				// count++;
				dic.put(c, dic.get(c) + 1);
			} else
				count++;
		}

		for (Entry<Character, Double> iter : dic.entrySet()) {
			if (iter.getValue() == 0)
				count++;
		}

		return 1 - count / (double) Math.min(s1.length(), s2.length());

	}

	public static void Main_(String[] args) {
		ArrayList<String> range = new ArrayList<String>();
		String filePath = "D:\\CODE\\FindAnswerWS - Copy\\FindAnswer\\bin\\x64\\Debug\\App_Data\\ServiceTragData.txt";
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					if (lineTxt.charAt(0) == '	') {
						// 防止第一个字符为“；”导致截出的数组第一个为空
						lineTxt.substring(1, lineTxt.length() - 1);
					}
					String[] arry = lineTxt.split("	");
					List<String> list = java.util.Arrays.asList(arry);
					if (list.size() < 2)
						continue;
					range.addAll(list);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		while (true) {
			System.out.print("Please enter:");
			Scanner in = new Scanner(System.in);
			String line = in.next();
			ArrayList<Entry<Double, String>> lst = GetSimStrWithVal(line,
					range, "0");
			for (Entry<Double, String> iter : lst) {
				System.out.println(iter.getKey() + "\t" + iter.getValue());
			}
		}
	}
	public static ArrayList<String> GetSimStr(String abcNumbers,
			Map<String, String> insAbstractMap, String type) {
		// TODO Auto-generated method stub
		if(insAbstractMap==null||insAbstractMap.size()==0)return new ArrayList<String>();
		ArrayList<String> insAL=new ArrayList<String>();		
		for(Entry<String,String> entry :insAbstractMap.entrySet())
		{
			insAL.add(entry.getKey());
		}	
		return GetSimStr(abcNumbers,insAL,type);
	}
}
