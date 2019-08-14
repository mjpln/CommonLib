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

import com.knowology.bll.WordItemDAO;

/**
 *描述：
 * 
 * @author: qianlei
 *@date： 日期：2015-10-23 时间：下午04:16:03
 */
public class WordItemDAOTest {

	/**
	 *描述：
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-10-23 时间：下午04:16:03
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.WordItemDAO#selectPaging(java.lang.String, java.lang.Boolean, java.lang.String, java.lang.String, java.lang.String, int, int)}
	 * .
	 */
	@Ignore
	public void testSelectPaging() {
		Result rs = WordItemDAO.select4Paging(null, false, "", "中欧基金名称父类",
				"基础", 1, 5);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.WordItemDAO#select(java.lang.String, java.lang.Boolean, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testSelect() {
		Result rs = WordItemDAO.select("", true, "5", "中欧基金名称父类", "基础");
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.WordItemDAO#selectCount(java.lang.String, java.lang.Boolean, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testSelectCount() {
		int i = WordItemDAO.selectCount("", false, "1", "中欧基金名称父类", "基础");
		System.out.println(i);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.WordItemDAO#exists(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testExists() {
		Boolean b = WordItemDAO.exists(40575, "中欧", "标准名称");
		System.out.println(b);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.WordItemDAO#update(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testUpdate() {
		int i = WordItemDAO.update("中欧天启","中欧天启", "标准词", "普通词", 10949821,
				40575);
		System.out.println(i);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.WordItemDAO#insert(java.lang.String, java.util.List, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testInsert() {
		ArrayList<String> lstWorditem = new ArrayList<String>();
		lstWorditem.add("中欧天平");
		lstWorditem.add("中欧摩羯");
		int i = WordItemDAO.insert(40575, lstWorditem, "标准词");
		System.out.println(i);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.WordItemDAO#delete(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testDelete() {
	}

}
