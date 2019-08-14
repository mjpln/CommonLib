package com.util;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import com.knowology.dal.Database;

public class XMLResourceBundleControl extends ResourceBundle.Control {
	//项目是否使用xml格式配置文件
	public static boolean isXML = false;
	private static String XML = "xml";
	public static ResourceBundle.Control INSTANCE = new XMLResourceBundleControl();
	
	static{
		if(!isXML){
			INSTANCE = ResourceBundle.Control.getControl(FORMAT_DEFAULT);
		}
	}
	public List<String> getFormats(String baseName) {
		return Collections.singletonList(XML);
	}

	public ResourceBundle newBundle(String baseName, Locale locale,
			String format, ClassLoader loader, boolean reload)
			throws IllegalAccessException, InstantiationException, IOException {

		if ((baseName == null) || (locale == null) || (format == null)
				|| (loader == null)) {
			throw new NullPointerException();
		}
		ResourceBundle bundle = null;
		if (!format.equals(XML)) {
			return null;
		}

		String bundleName = toBundleName(baseName, locale);
		String resourceName = toResourceName(bundleName, format);
		URL url = loader.getResource(resourceName);
		if (url == null) {
			return null;
		}
		URLConnection connection = url.openConnection();
		if (connection == null) {
			return null;
		}
		if (reload) {
			connection.setUseCaches(false);
		}
		InputStream stream = connection.getInputStream();
		if (stream == null) {
			return null;
		}
		BufferedInputStream bis = new BufferedInputStream(stream);
		bundle = new XMLResourceBundle(bis);
		bis.close();

		return bundle;
	}

	public static Properties readProp(String baseName) throws Exception{
		Properties props = new Properties();
		InputStream in = null;
		if(XMLResourceBundleControl.isXML){
			String jdbcProPath = baseName + ".xml";
			in = Database.class.getClassLoader()
					.getResourceAsStream(jdbcProPath);
			props.loadFromXML(in);
		}else{
			String jdbcProPath = baseName + ".properties";
			in = Database.class.getClassLoader()
					.getResourceAsStream(jdbcProPath);
			props.load(in);
		}
		return props;
		
	}
	public static class XMLResourceBundle extends ResourceBundle {
		private Properties props;

		XMLResourceBundle(InputStream stream) throws IOException {
			props = new Properties();
			props.loadFromXML(stream);
		}

		protected Object handleGetObject(String key) {
			return props.getProperty(key);
		}

		public Enumeration<String> getKeys() {
			Set<String> handleKeys = props.stringPropertyNames();
			return Collections.enumeration(handleKeys);
		}
	}

	/**
	 * 将properties配置转换成xml
	 * @param baseName
	 * @throws Exception
	 */
	public static void convert2XML(String baseName) throws Exception{
		Properties props = new Properties();
		URL resource = XMLResourceBundleControl.class.getClassLoader().getResource(baseName+".properties");
		if(resource != null){
			props.load(resource.openStream());
			
			//保存到xml文件中
	        OutputStream os = new FileOutputStream("src/"+baseName+".xml");
	        props.storeToXML(os, null);
		}
		
	}
	public static void main(String args[]) {
		try {
			convert2XML("jdbc_oracle");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
