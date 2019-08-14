package com.knowology.UtilityOperate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.knowology.GlobalValue;

public class GetProvinceCityFromNum {

	public static Map<String, String> num2Provice = new ConcurrentHashMap<String, String>();
	public static Map<String, String> num2Ctiy = new ConcurrentHashMap<String, String>();

	public GetProvinceCityFromNum() {
		ArrayList<String> numprovincecity = new ArrayList<String>();
		if (num2Provice.size() == 0 || num2Ctiy.size() == 0) {
			CommonReadAndWrite.read(getFilePath() + "phoneNum2ProvinceCity",
					numprovincecity);
		}
		for (Iterator iterator = numprovincecity.iterator(); iterator.hasNext();) {
			String string = (String) iterator.next();
			String temp[] = string.split("	");
			if (temp.length == 4) {
				num2Provice.put(temp[0], temp[2]);
				num2Ctiy.put(temp[0], temp[3]);
			}
		}
	}

	public String getFilePath() {
		String path = this.getClass().getClassLoader().getResource("")
				.getPath();
		try {
			path = URLDecoder.decode(path, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return path;
	}

	public String getCity(String phoneNum) {
		if (phoneNum.length() != 11)// 其它合法性检查这里就不再检查 后续补充
			return "";
		String topSeven = phoneNum.substring(0, 7);
		return num2Ctiy.get(topSeven);
	}

	public String getProvince(String phoneNum) {
		if (phoneNum.length() != 11)// 其它合法性检查这里就不再检查 后续补充
			return "";
		String topSeven = phoneNum.substring(0, 7);
		return num2Provice.get(topSeven);
	}
}
