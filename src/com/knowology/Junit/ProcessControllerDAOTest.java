/**
 * 
 */
package com.knowology.Junit;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.jsp.jstl.sql.Result;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.knowology.bll.ProcessControllerDAO;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-9-21 时间：上午11:16:11
 */
public class ProcessControllerDAOTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2015-9-21 时间：上午11:16:11
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectQueryElement4Paging(java.lang.String, java.lang.String, java.lang.String, int, int)}.
	 */
	@Ignore
	public void testSelectQueryElement4Paging() {
		Result rs = ProcessControllerDAO.selectQueryElement4Paging("10519885", "100", "", 1, 6);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectQueryElement(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectQueryElement() {
		Result rs=ProcessControllerDAO.selectQueryElement(null, null, null);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#isContentElementName(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testIsContentElementName() {
		System.out.println(ProcessControllerDAO.isContentElementName("基金", "10519885", "100"));
		System.out.println(ProcessControllerDAO.isContentElementName("基金名称", "10519885", "100"));
		System.out.println(ProcessControllerDAO.isContentElementName("基金", null, "100"));
		System.out.println(ProcessControllerDAO.isContentElementName(null, null, null));
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#insertQueryElementName(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testInsertQueryElementName() {
		int i= ProcessControllerDAO.insertQueryElementName("基金名称", "10519885", "100", "1", "基金名称维度父类");
		 i+= ProcessControllerDAO.insertQueryElementName("基金渠道", "10519885", "100", "2", "基金渠道维度父类");
		assertEquals(2, i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectQueryElementCount(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectQueryElementCount() {
		int i=ProcessControllerDAO.selectQueryElementCount("", "10519885", "100");
		System.out.println(i);
		assertEquals(2,i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#deleteQueryElement(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testDeleteQueryElement() {
		System.out.println(ProcessControllerDAO.deleteQueryElement("10519885", "100", "829", "1"));
		System.out.println(ProcessControllerDAO.deleteQueryElement("10519885", "100", "830", "1"));
		System.out.println(ProcessControllerDAO.deleteQueryElement("10519885", "100", "831", "1"));
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectQueryElementValues4Paging(java.lang.String, java.lang.String, int, int)}.
	 */
	@Ignore
	public void testSelectQueryElementValues4Paging() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectQueryElementValues(java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectQueryElementValues() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectQueryElementValuesCount(java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectQueryElementValuesCount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#insertQueryElementValue(java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testInsertQueryElementValue() {
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#deleteQueryElementValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testDeleteQueryElementValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectQueryElement2Values(java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectQueryElement2Values() {
		Result rs=ProcessControllerDAO.selectQueryElement2Values("10519885", null);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectConditioncomb4Paging(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, int, int)}.
	 */
	@Ignore
	public void testSelectConditioncomb4Paging() {
		String[] array=new String[]{"云端"};
		Result rs=ProcessControllerDAO.selectConditioncomb4Paging("10519885", "100", array, null, "1", 1, 5);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectConditioncomb(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectConditioncomb() {
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectConditioncombCount(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectConditioncombCount() {
		int i=ProcessControllerDAO.selectConditioncombCount("10519885", "100", null, null, "1");
		assertEquals(2, i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#isContentConditioncomb(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testIsContentConditioncomb() {
		String[] conditions=new String[]{"周期优选","淘宝"};
		Boolean b=ProcessControllerDAO.isContentConditioncomb("10519885", "100", conditions, "0", "测试");
		System.out.println(b);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#insertConditioncomb(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testInsertConditioncomb() {
		String[] conditions=new String[]{"云端生活","淘宝"};
		int i=ProcessControllerDAO.insertConditioncomb("10519885", "100", conditions, "0", "测试");
		assertEquals(1, i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#deleteConditioncomb(java.lang.String[])}.
	 */
	@Ignore
	public void testDeleteConditioncomb() {
		String[] arry=new String[]{"32530"};
		int i=ProcessControllerDAO.deleteConditioncomb(arry);
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#deleteAllConditioncomb(java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testDeleteAllConditioncomb() {
		int i=ProcessControllerDAO.deleteAllConditioncomb("10519885", "100");
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#confirmConditioncomb(java.lang.String[])}.
	 */
	@Ignore
	public void testConfirmConditioncomb() {
		String[] arry=new String[]{"32531"};
		int i=ProcessControllerDAO.confirmConditioncomb(arry);
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#confirmAllConditionComb(java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testConfirmAllConditionComb() {
		int i=ProcessControllerDAO.confirmAllConditionComb("10519885", "100");
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#updateConditionComb(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testUpdateConditionComb() {
		String[] conditions=new String[]{"云端生活","支付宝"};
		int i=ProcessControllerDAO.updateConditionComb("10519885", "100", conditions, "0", "update测试", "32532");
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#isContentSceneRules(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testIsContentSceneRules() {
		String[] arry=new String[]{"缺失","缺失"};
		Boolean b=ProcessControllerDAO.isContentSceneRules("10519885", "100", arry, "0", "1", "问题要素冲突规则");
		System.out.println(b);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#insertSceneRules(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testInsertSceneRules() {
		String[] arry=new String[]{"存在","缺失"};
		int i=ProcessControllerDAO.insertSceneRules("10519885", "100", arry, "0", "0", "缺失补全规则");
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectSceneRulesCount(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectSceneRulesCount() {
		String[] arry=new String[]{"缺失",""};
		int i=ProcessControllerDAO.selectSceneRulesCount("10519885", "100", arry, null, null);
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectSceneRules4Paging(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, int, int)}.
	 */
	@Ignore
	public void testSelectSceneRules4Paging() {
		Result rs=ProcessControllerDAO.selectSceneRules4Paging("10519885", "100", null, "1", null, 1, 5);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#selectSceneRules(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testSelectSceneRules() {
		Result rs=ProcessControllerDAO.selectSceneRules();
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#DeleteSceneRules(java.lang.String[])}.
	 */
	@Ignore
	public void testDeleteSceneRules() {
		String[] ruleid=new String[]{"966","967"};
		int i=ProcessControllerDAO.deleteSceneRules(ruleid);
		System.out.println(i);
	}

	/**
	 * Test method for {@link com.knowology.bll.ProcessControllerDAO#updateSceneRules(java.lang.String, java.lang.String, java.lang.String[], java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Ignore
	public void testUpdateSceneRules() {
		String[] arry=new String[]{"存在","缺失"};
		int i=ProcessControllerDAO.updateSceneRules("10519885", "100", arry, "0", "0", "缺失补全规则","967");
		System.out.println(i);
	}
	
	@Test
	public void testSelectDeficiencyRules() {
		Map<String,String> condition=new HashMap<String, String>();	
		Result rs=ProcessControllerDAO.selectDeficiencyRules("套餐信息表", "11276987", condition);
		if (rs != null && rs.getRowCount() > 0) {
			System.out.println(rs.getRowCount());
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}
	

}
