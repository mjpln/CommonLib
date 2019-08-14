/**
 * 
 */
package com.knowology.Bean;

import java.util.HashMap;
import java.util.Map;

/**
 *描述：经纬度范围类，用于存储一个经纬圆范围的信息
 *@author: qianlei
 *@date： 日期：2016-3-7 时间：上午10:19:24
 */
public class Coordinates {
	public Map<String,Double> local=new HashMap<String,Double>();//中心点经纬度
	public Map<String,Double> right=new HashMap<String,Double>();//东（右）边界经纬度
	public Map<String,Double> left=new HashMap<String,Double>();//西（左）边界经纬度
	public Map<String,Double> up=new HashMap<String,Double>();//北（上）边界经纬度
	public Map<String,Double> down=new HashMap<String,Double>();//南（下）边界经纬度
    //区域范围（公里）
    public Double range = 0.0;
    
    public Coordinates()
    {
    }

    /**
     * 
     *描述：构造函数
     *@author: qianlei
     *@date： 日期：2016-3-7 时间：上午11:31:08
     *@param lat  中心位置维度
     *@param lng  中心位置经度
     *@param range  区域范围(公里)
     */
    public Coordinates(Double lat, Double lng, Double range)
    {
    	save(lat, lng,0);
        this.range = range;
        setRound();
    }

    public Coordinates(Map<String, String> keyPait, Double range)
    {
        Double lat=Double.parseDouble(keyPait.get("纬度值"));
        Double lng=Double.parseDouble(keyPait.get("经度值"));
        save(lat,lng,0);
        this.range = range;
        setRound();
    }

    /**
     * 
     *描述：设置坐标
     *@author: qianlei
     *@date： 日期：2016-3-7 时间：上午10:32:52
     *@param lat 维度值
     *@param lng 经度值
     */
    private void save(Double lat, Double lng,int tag)
    {
    	Map<String,Double> map=new HashMap<String, Double>();
    	map.put("纬度值", lat);
    	map.put("经度值", lng);
    	switch(tag)
    	{
    		case 0:
    			local.putAll(map);
    			break;
    		case 1:
    			right.putAll(map);
    			break;
    		case 2:
    			left.putAll(map);
    			break;
    		case 3:
    			up.putAll(map);
    			break;
    		case 4:
    			down.putAll(map);
    	}
    }
    
    /**
     * 
     *描述：计算出方圆边界的坐标
     *@author: qianlei
     *@date： 日期：2016-3-7 时间：上午10:33:43 void
     */
    private void setRound()
    {
        Double latLocal =local.get("纬度值");
        Double lngLocal = local.get("经度值");
        Double lat=latLocal;
        Double lng=lngLocal;
        Double distance = 0.0;
        Double min = 0.0;

        //Right
        while (distance < range)
        {
            lng += 0.005;
            distance = getDistance(latLocal, lngLocal, lat, lng);
        }
        min = distance;
        save(lat, lng,1);

        //Left
        lat = latLocal;
        lng = lngLocal;
        distance = 0.0;
        while (distance < range)
        {
            lng -= 0.005;
            distance = getDistance(latLocal, lngLocal, lat, lng);
        }
        if (min > distance)
            min = distance;
        save(lat, lng,2);

        //Up
        lat = latLocal;
        lng = lngLocal;
        distance = 0.0;
        while (distance < range)
        {
            lat += 0.005;
            distance = getDistance(latLocal, lngLocal, lat, lng);
        }
        if (min > distance)
            min = distance;
        save(lat, lng,3);

        //Down
        lat = latLocal;
        lng = lngLocal;
        distance = 0.0;
        while (distance < range)
        {
            lat -= 0.005;
            distance = getDistance(latLocal, lngLocal, lat, lng);
        }
        if (min > distance)
            min = distance;
        save(lat, lng,4);

        range = min;//将实际范围值记录下来(由于计算精度不高，实际范围值应该略大于设定值)
    }
    
    private static Double EARTH_RADIUS = 6378.137;//地球半径 单位：千米
    private static double rad(double d)
    {
        return d * Math.PI / 180.0;
    }
    
    //计算两地的直线距离
    public  static Double getDistance(Double lat1, Double lng1, Double lat2, Double lng2)
    {
    	Double radLat1 = rad(lat1);
    	Double radLat2 = rad(lat2);
    	Double a = radLat1 - radLat2;
    	Double b = rad(lng1) - rad(lng2);

    	Double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
         Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = ((double)Math.round(s * 10000)) / 10000;
        return s;
    }
    
    /**
     * 
     *描述：判断该坐标是否在方圆范围内
     *@author: qianlei
     *@date： 日期：2016-3-7 时间：上午10:58:54
     *@param lat  纬度值
     *@param lng  经度值	
     *@return Boolean
     */
    public Boolean isInternally(Double lat, Double lng)
    {
        if (lng >= left.get("经度值") && lng <= right.get("经度值"))
        {
            if (lat >= down.get("纬度值") && lat <= up.get("纬度值"))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     *描述：以字符串形式返回坐标范围，主要用于作为SQL语句中的条件语句
     *@author: qianlei
     *@date： 日期：2016-3-7 时间：下午03:44:21
     *@param lat 纬度值对应列名
     *@param lng 经度值对应列名
     *@return String
     */
    public String getCoordinatesRange(String lat,String lng)
    {
        String str = "";
        str = lng+" >= " + left.get("经度值") + " and "+lng+" <= " + right.get("经度值") + " and " + lat+" >= " + down.get("纬度值") + " and "+lat+" <=" + up.get("纬度值");
        return str;
    }
    
    /**
     * 
     *描述：用于测试
     *@author: qianlei
     *@date： 日期：2016-3-7 时间：上午11:40:45 void
     */
    public void show()
    {
		System.out.println(local);
		System.out.println(right);
		System.out.println(left);
		System.out.println(up);
		System.out.println(down);
		System.out.println(range);
    }
    
    public static void main(String[] args)
    {
    	System.out.println(Coordinates.getDistance(31.234716,121.52157,31.230896,121.63848));
    }
}
