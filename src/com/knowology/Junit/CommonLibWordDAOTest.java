/**
 * 
 */
package com.knowology.Junit;


import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import org.junit.Before;
import org.junit.Test;

import com.knowology.bll.CommonLibWordDAO;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-11-27 时间：下午03:52:47
 */
public class CommonLibWordDAOTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2015-11-27 时间：下午03:52:47
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.bll.CommonLibWordDAO#selectBYwordCalssNameList(java.util.List, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSelectBYwordCalssNameList() {
		List<String> wordClassName=new ArrayList<String>();
		wordClassName.add("冗余词词类");
		wordClassName.add("选择交互冗余词类");
		Result rs = CommonLibWordDAO.selectBYwordCalssNameList(wordClassName, null, "基础");
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

}
