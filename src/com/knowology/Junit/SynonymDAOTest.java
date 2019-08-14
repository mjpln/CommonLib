/**
 * 
 */
package com.knowology.Junit;

import static org.junit.Assert.*;

import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import org.junit.Before;
import org.junit.Test;

import com.knowology.bll.SynonymDAO;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-10-26 时间：下午03:51:43
 */
public class SynonymDAOTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2015-10-26 时间：下午03:51:43
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.bll.SynonymDAO#select4Paging(int, int, java.lang.String, java.lang.Boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSelect4Paging() {
		Result rs=SynonymDAO.select4Paging(1, 10, "明睿", true, null,"中欧基金名称父类","基础" , "5");
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.SynonymDAO#select(java.lang.String, java.lang.Boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSelect() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.SynonymDAO#selectCount(java.lang.String, java.lang.Boolean, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testSelectCount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.SynonymDAO#exists(java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public void testExists() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.SynonymDAO#update(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.SynonymDAO#insert(java.lang.Integer, java.util.List, java.lang.Integer, java.lang.String)}.
	 */
	@Test
	public void testInsert() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.SynonymDAO#delete(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

}
