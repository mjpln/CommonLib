/**
 * 
 */
package com.knowology.Junit;

import static org.junit.Assert.*;

import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.knowology.bll.WordPatDAO;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-9-25 时间：下午02:09:46
 */
public class WordPatDAOTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2015-9-25 时间：下午02:09:46
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.bll.WordPatDAO#select4Paging(java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.Integer, int, int)}.
	 */
	@Ignore
	public void testSelectWordPat4Paging() {
		String[] kbdataid=new String[]{"10519906"};
		Result rs=WordPatDAO.select4Paging("免费189邮箱", "电信指令业务", "办理", kbdataid, "免费近类", 0, 2, 2);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.WordPatDAO#selectCount(java.lang.String, java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.Integer)}.
	 */
	@Ignore
	public void testSelectWordPatCount() {
		String[] kbdataid=new String[]{"10519906"};
		int i=WordPatDAO.selectCount("免费189邮箱","电信指令业务", "办理", kbdataid, "", 0);
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.WordPatDAO#update(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.Integer, java.lang.String)}.
	 */
	@Ignore
	public void testUpdateWordPat() {
		int i=WordPatDAO.update("办理近类|开通近类#无序#编者=ql", "<!办理近类|!开通近类>#编者=\"ql\"", "上海", 0, 0, "词模brand", 10519906, 433902);
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.WordPatDAO#isContentWordPat(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.Integer)}.
	 */
	@Ignore
	public void testIsContentWordPat() {
		Boolean b=WordPatDAO.exists( "<!办理近类|!开通近类>#编者=\"l\"", "上海", 0, "词模brand", 10519906);
		System.out.println(b);
	}

	/**
	 * Test method for {@link com.knowology.bll.WordPatDAO#insert(java.lang.String, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.Integer)}.
	 */
	@Ignore
	public void testInsertWordPat() {
//		int i=WordPatDAO.insertWordPat("建立近类", "建立近类", "上海", 0, 0, "词模brand", 10519906);
		int i=WordPatDAO.insert("办理近类|开通近类|定制近类#无序#编者=hzy", "<!办理近类|!开通近类|!定制近类>#编者=hzy", "上海", 0, 0, "词模brand", 10519906);
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.WordPatDAO#delete(java.lang.Integer)}.
	 */
	@Ignore
	public void testDeleteWordPat() {
		System.out.println(WordPatDAO.delete(433902));
	}

}
