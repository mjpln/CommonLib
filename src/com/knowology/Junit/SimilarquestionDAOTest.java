/**
 * 
 */
package com.knowology.Junit;


import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import org.junit.Before;
import org.junit.Test;

import com.knowology.bll.SimilarquestionDAO;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-11-24 时间：下午04:03:48
 */
public class SimilarquestionDAOTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2015-11-24 时间：下午04:03:48
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.bll.SimilarquestionDAO#select(java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public void testSelect() {
		Result rs=SimilarquestionDAO.select("10571637", "标准问题");
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

}
