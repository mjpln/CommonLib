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

import com.knowology.bll.ServiceInfoTableDAO;

/**
 *描述：
 * 
 * @author: qianlei
 *@date： 日期：2015-9-10 时间：上午09:52:15
 */
public class ServiceInfoTableDAOTest {

	/**
	 *描述：
	 * 
	 * @author: qianlei
	 *@date： 日期：2015-9-10 时间：上午09:52:15
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#selectAttrName4Paging(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testSelectAttrName4Paging() {
		Result rs = ServiceInfoTableDAO.selectAttrName4Paging("1827330", null,
				1, 3);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#selectProductInfoAttrName(java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testSelectAttrName() {
		Result rs=ServiceInfoTableDAO.selectProductInfoAttrName("1827330", "基金经理");
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#selectAttrNameCount(java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testSelectAttrNameCount() {
		int i=ServiceInfoTableDAO.selectAttrNameCount("1827330", "");
		System.out.println(i);
		assertEquals(6,i);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#insertAttrName(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testInsertAttrName() {
		int i = 0;
		i = i
				+ ServiceInfoTableDAO.insertAttrName("1827330", "测试信息表",
						"基金代码", "1", "嘉实基金代码父类");
		i = i
				+ ServiceInfoTableDAO.insertAttrName("1827330", "测试信息表",
						"基金名称", "2", "嘉实基金名称父类");
		i = i
				+ ServiceInfoTableDAO.insertAttrName("1827330", "测试信息表",
						"基金经理", "3", "嘉实基金经理父类");
		i = i
				+ ServiceInfoTableDAO.insertAttrName("1827330", "测试信息表",
						"基金净值", "4", "嘉实基金净值父类");
		i = i
				+ ServiceInfoTableDAO.insertAttrName("1827330", "测试信息表",
						"基金类型", "5", "嘉实基金类型父类");
		i = i
		+ ServiceInfoTableDAO.insertAttrName("1827330", "测试信息表",
				"测试123", "6", "");
		assertEquals(6, i);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#isContentAttrName(java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testIsContentAttrName() {
		Boolean b1=ServiceInfoTableDAO.isContentAttrName("1827330", "基金类型");
		Boolean b2=ServiceInfoTableDAO.isContentAttrName("1827330", "");
		Boolean b3=ServiceInfoTableDAO.isContentAttrName("1827330", "基金");
		System.out.println(b1);
		System.out.println(b2);
		System.out.println(b3);
		assertEquals(true,b1);
		assertEquals(false,b2);
		assertEquals(false,b3);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#selectAttrname2Values(java.lang.String)}
	 * .
	 */
	@Ignore
	public void testSelectAttrname2Values() {
		Result rs=ServiceInfoTableDAO.selectAttrname2Values("1827330");
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#deleteAttrName(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testDeleteAttrName() {
		Boolean b= ServiceInfoTableDAO.deleteAttrName("1827330", "394", "6");
		System.out.println(b);
		assertEquals(true, b);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#updateAttrName(java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testModifyAttrName() {
		int i=ServiceInfoTableDAO.updateAttrName("1827330", "395", "是否支持申购");
		assertEquals(1,i);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#selectAttrValueCount(java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testSelectAttrValueCount() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#selectAttrValue4Paging(java.lang.String, java.lang.String, int, int)}
	 * .
	 */
	@Ignore
	public void testSelectAttrValue4Paging() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#insertAttrValue(java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testInsertAttrValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#updateAttrValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testModifyAttrValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#deleteAttrValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)}
	 * .
	 */
	@Ignore
	public void testDeleteAttrValue() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#selectProductInfoCount(java.lang.String, java.lang.String[])}
	 * .
	 */
	@Ignore
	public void testSelectProductInfoCount() {
		String[] stt=new String[60];
		stt[1]="稳固收益债券";
		stt[2]="曲扬";
		int i=ServiceInfoTableDAO.selectProductInfoCount("1827330", stt);
		System.out.println(i);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#selectProductInfo4Paging(java.lang.String, java.lang.String[], int, int)}
	 * .
	 */
	@Ignore
	public void testSelectProductInfo4Paging() {
		String[] stt=new String[60];
		stt[2]="曲扬";
		Result rs=ServiceInfoTableDAO.selectProductInfo4Paging("1827330", stt, 1, 10);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#selectProductInfo(java.util.Map)}
	 * .
	 */
	@Test
	public void testSelectProductInfo() {
		Map<String,String> selectCondition=new HashMap<String,String>();
		selectCondition.put("serviceid", "1820292");
//		Result rs=ServiceInfoTableDAO.selectProductInfo(selectCondition,"attr1,attr2,attr3,attr4");
		Result rs=ServiceInfoTableDAO.selectProductInfo(selectCondition,"","",false);
		if (rs != null && rs.getRowCount() > 0) {
			for (SortedMap<String, String> rows : rs.getRows()) {
				System.out.println(rows.toString());
			}
		}
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#isContenProductInfo(java.lang.String, java.lang.String[])}
	 * .
	 */
	@Ignore
	public void testIsContenProductInfo() {
		String[] attrArr=new String[5];
		attrArr[0]="070025";
		attrArr[1]="信用债券A";
		attrArr[2]="万晓西、胡永青";
		attrArr[3]="1.075元\n（2014-11-05）";
		attrArr[4]="债券型";
		Boolean b= ServiceInfoTableDAO.isContenProductInfo("1827330", attrArr);
		assertEquals(true, b);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#insertProductInfo(java.lang.String, java.lang.String, java.lang.String[])}
	 * .
	 */
	@Ignore
	public void testInsertProductInfo() {
		String[] attrArr=new String[5];
		attrArr[0]="070025";
		attrArr[1]="信用债券A";
		attrArr[2]="万晓西、胡永青";
		attrArr[3]="566";
		attrArr[4]="债券型";
		int result =ServiceInfoTableDAO.insertProductInfo("1827330", "测试信息表", attrArr);
		assertEquals(1, result);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#deleteProductInfo(java.lang.String[])}
	 * .
	 */
	@Ignore
	public void testDeleteProductInfo() {
		String[] id=new String[]{"5933"};
		int result=ServiceInfoTableDAO.deleteProductInfo(id);
		assertEquals(1,result);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#confirmProductInfo(java.lang.String[])}
	 * .
	 */
	@Ignore
	public void testConfirmProductInfo() {
		String[] id=new String[]{"5847"};
		int result=ServiceInfoTableDAO.confirmProductInfo(id);
		assertEquals(1,result);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#updateProductInfo(java.lang.String, java.lang.String[], java.lang.String)}
	 * .
	 */
	@Ignore
	public void testUpdateProductInfo() {
		String[] attrArr=new String[5];
//		attrArr[0]="070025";
//		attrArr[1]="信用债券A";
//		attrArr[2]="万晓西、胡永青";
//		attrArr[4]="债券型";
		attrArr[3]="566";
		int result=ServiceInfoTableDAO.updateProductInfo("1827330", attrArr, "5847");
		System.out.println(result);
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#deleteAllProductInfo(java.lang.String)}
	 * .
	 */
	@Ignore
	public void testDeleteAllProductInfo() {
		System.out.println(ServiceInfoTableDAO.deleteAllProductInfo("1827330"));
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#confirmAllProductInfo(java.lang.String)}
	 * .
	 */
	@Ignore
	public void testConfirmAllProductInfo() {
		System.out.println(ServiceInfoTableDAO.confirmAllProductInfo("1827330"));
	}

	/**
	 * Test method for
	 * {@link com.knowology.bll.ServiceInfoTableDAO#updateAttrValueByProductInfo(java.lang.String)}
	 * .
	 */
	@Test
	public void testUpdateAttrValueByProductInfo() {
		fail("Not yet implemented");
	}

}
