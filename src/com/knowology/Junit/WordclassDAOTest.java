/**
 * 
 */
package com.knowology.Junit;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.knowology.bll.WordclassDAO;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-10-22 时间：下午02:48:14
 */
public class WordclassDAOTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2015-10-22 时间：下午02:48:14
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.bll.WordclassDAO#select4Paging(java.lang.String, java.lang.String, int, int)}.
	 */
	@Ignore
	public void testSelectWordClass4Paging() {
		Result rs =WordclassDAO.select4Paging("", "子句", 2, 3);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.WordclassDAO#select(java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectWordClass() {
		Result rs =WordclassDAO.select("中欧基金", "基础");
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.WordclassDAO#selectCount(java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectWordClassCount() {
		int c=WordclassDAO.selectCount("中欧基金", "基础");
		System.out.println(c);
	}

	/**
	 * Test method for {@link com.knowology.bll.WordclassDAO#update(java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testUpdate() {
		int c=WordclassDAO.update(40666, "例子维度父类");
		System.out.println(c);
	}

	/**
	 * Test method for {@link com.knowology.bll.WordclassDAO#exists(java.lang.String)}.
	 */
	@Test
	public void testIsContentWordClass() {
		System.out.println(WordclassDAO.exists("一般维度近类"));
	}

	/**
	 * Test method for {@link com.knowology.bll.WordclassDAO#insert(java.util.List, java.lang.String)}.
	 */
	@Test
	public void testInsertWordClass() {
		ArrayList<String> lstWordclass=new ArrayList<String>();
		lstWordclass.add("里程维度近类");
		lstWordclass.add("里程维度父类");
		int c=WordclassDAO.insert(lstWordclass, "基础");
		System.out.println(c);
	}

	/**
	 * Test method for {@link com.knowology.bll.WordclassDAO#delete(java.lang.String)}.
	 */
	@Ignore
	public void testDeleteWordClass() {
		int c =WordclassDAO.delete("40665");
		System.out.println(c);
	}

}
