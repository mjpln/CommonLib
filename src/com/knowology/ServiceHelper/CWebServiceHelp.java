/**
 * 
 */
package com.knowology.ServiceHelper;

import javax.xml.namespace.QName;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;

import com.knowology.Log;

/**
 *描述：调用 C++的服务接口
 * 
 * @author: qianlei
 *@date： 日期：2014-9-24 时间：下午03:37:06
 */
public class CWebServiceHelp {

	public static Log logger = Log.getLoger();
	
	public String CallWebService(String url, String[] arry) {

		String endpoint=url;
		String a = "";
		try {
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(endpoint);
			call.setOperationName(new QName("urn:isearch", "Search"));
			call.addParameter("q", org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);
			call.addParameter("s", org.apache.axis.encoding.XMLType.XSD_STRING,
					javax.xml.rpc.ParameterMode.IN);
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
			call.setReturnClass(String.class);
			a = (String) call.invoke(arry);
		} catch (Exception ex) {
			logger.error(ex.toString());
		}
		return a;
	}

}
