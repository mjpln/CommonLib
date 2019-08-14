/**
 * 
 */
package com.knowology.Junit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.knowology.UtilityOperate.EncryptionOper;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2016-5-19 时间：下午02:57:40
 */
public class EncryptionOperTest {

	/**
	 *描述：
	 *@author: qianlei
	 *@date： 日期：2016-5-19 时间：下午02:57:40
	 *@throws java.lang.Exception void
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for {@link com.knowology.UtilityOperate.EncryptionOper#addPassEncode(java.lang.String)}.
	 */
	@Test
	public void testAddPassEncode() {
		String plaintext="oHQuEjtumQBCxf43STYMUsk4Dnfg";
//		String plaintext="oHQuEjiKUmaxmynaav4b_13RYV5A";
		try {
			String ciphertext=EncryptionOper.addPassEncode(plaintext);
			System.out.println(ciphertext);
			assertEquals("b0hRdUVqdHVtUUJDeGY0M1NUWU1Vc2s0RG5mZw%3D%3D",ciphertext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test method for {@link com.knowology.UtilityOperate.EncryptionOper#replacePassEncode(java.lang.String)}.
	 */
	@Test
	public void testReplacePassEncode() {
		String ciphertex="b0hRdUVqdHVtUUJDeGY0M1NUWU1Vc2s0RG5mZw==";
		try {
			String plaintext=EncryptionOper.replacePassEncode(ciphertex);
			System.out.println(plaintext);
			assertEquals("oHQuEjtumQBCxf43STYMUsk4Dnfg",plaintext);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
