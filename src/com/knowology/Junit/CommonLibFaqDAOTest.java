/**
 * 
 */
package com.knowology.Junit;

import static org.junit.Assert.*;

import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import org.junit.Before;
import org.junit.Test;

import com.knowology.bll.CommonLibFaqDAO;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-11-24 时间：下午03:07:37
 */
public class CommonLibFaqDAOTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2015-11-24 时间：下午03:07:37
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.bll.CommonLibFaqDAO#getAnswerCount(com.knowology.Bean.User, java.lang.String)}.
	 */
	@Test
	public void testGetAnswerCount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.CommonLibFaqDAO#select(com.knowology.Bean.User, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSelect() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.CommonLibFaqDAO#GetDBAnswerWithConstraints(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testGetDBAnswerWithConstraints() {
		Result rs= CommonLibFaqDAO.GetDBAnswerWithConstraints("基金行业->嘉实基金->多渠道应用", "Web", "10592890", "普通客户");
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.CommonLibFaqDAO#exist(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testExist() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.CommonLibFaqDAO#insertOrUpdate(com.knowology.Bean.User, java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testInsertOrUpdate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.CommonLibFaqDAO#delete(com.knowology.Bean.User, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

}
