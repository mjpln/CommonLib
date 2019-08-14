/**
 * 
 */
package com.knowology.Junit;


import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.knowology.bll.CommonLibServiceDAO;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2016-3-3 时间：下午03:47:39
 */
public class CommonLibServiceDAOTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2016-3-3 时间：下午03:47:39
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.bll.CommonLibServiceDAO#getServicePath(java.lang.String)}.
	 */
	@Test
	public void testGetServicePath() {
		ArrayList<String> out=CommonLibServiceDAO.getServicePath("1820074");
		System.out.println(out);
	}

}
