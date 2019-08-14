/**
 * 
 */
package com.knowology.Junit;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.knowology.Bean.Coordinates;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2016-3-7 时间：上午11:22:41
 */
public class CoordinatesTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2016-3-7 时间：上午11:22:41
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.Bean.Coordinates#Coordinates(java.lang.Double, java.lang.Double, java.lang.Double)}.
	 */
	@Test
	public void testCoordinatesCreate() {
		Coordinates cd=new Coordinates(34.42816,117.45076,5.0);
		cd.show();
	}

	/**
	 * Test method for {@link com.knowology.Bean.Coordinates#Coordinates(java.util.Map, java.lang.Double)}.
	 */
	@Test
	public void testCoordinatesMapOfStringStringDouble() {
		Map<String,String> keyValue=new HashMap<String,String>();
		keyValue.put("纬度值", "34.42816");
		keyValue.put("经度值", "117.45076");
		Coordinates cd =new Coordinates(keyValue,5.0);
		cd.show();
	}

	/**
	 * Test method for {@link com.knowology.Bean.Coordinates#getDistance(java.lang.Double, java.lang.Double, java.lang.Double, java.lang.Double)}.
	 */
	@Test
	public void testGetDistance() {
		Double lang=Coordinates.getDistance(31.899857, 121.17854, 34.4281, 117.45076);
		System.out.println(lang);
	}

	/**
	 * Test method for {@link com.knowology.Bean.Coordinates#isInternally(java.lang.Double, java.lang.Double)}.
	 */
	@Test
	public void testIsInternally() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.Bean.Coordinates#getCoordinatesRange()}.
	 */
	@Test
	public void testGetCoordinatesRange() {
		Coordinates cd=new Coordinates(34.42816,117.45076,5.0);
		System.out.println(cd.getCoordinatesRange("att1","att2"));
	}

}
