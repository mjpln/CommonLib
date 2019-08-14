/**
 * 
 */
package com.knowology.Junit;

import javax.servlet.jsp.jstl.sql.Result;

import org.junit.Before;
import org.junit.Test;

import com.knowology.bll.CommonLibSynonymDAO;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2016-5-3 时间：下午05:14:23
 */
public class CommonLibSynonymDAOTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2016-5-3 时间：下午05:14:23
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.bll.CommonLibSynonymDAO#_select(java.lang.String, java.lang.Boolean, java.lang.Boolean, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void test_select() {
		Result rs=CommonLibSynonymDAO._select("", false, true, "0", "三星－F539", "手机型号档次父类");
		System.out.println(rs.getRowCount());
	}

}
