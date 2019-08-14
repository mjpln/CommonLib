/**
 * 
 */
package com.knowology.Junit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.knowology.bll.CommonLibKbDataDAO;

/**
 *描述：
 * 
 * @author: qianlei
 *@date： 日期：2016-1-7 时间：下午02:14:38
 */
public class CommonLibKbDataDAOTest {

	/**
	 *描述：
	 * 
	 * @author: qianlei
	 *@date： 日期：2016-1-7 时间：下午02:14:39
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.CommonLibKbDataDAO#getCity(java.lang.String)}.
	 */
	@Test
	public void testGetCity() {
		String city = CommonLibKbDataDAO.getCity("10592890");
		System.out.println(city);
	}

}
