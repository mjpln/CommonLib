/**
 * 
 */
package com.knowology.UtilityOperate;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 *描述：
 *@author: qianlei
 *@date： 日期：2015-5-22 时间：上午09:42:32
 */
public class BeanUtils {
	/**
	 * java反射bean的get方法
	 * 
	 * @param objectClass
	 * @param fieldName
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings({ "unchecked" })
	private static Method getGetMethod(Class objectClass, String fieldName) throws NoSuchMethodException, SecurityException {
		StringBuffer sb = new StringBuffer();
		sb.append("get");
		sb.append(fieldName.substring(0, 1).toUpperCase());
		sb.append(fieldName.substring(1));
		return objectClass.getMethod(sb.toString());
	}

	/**
	 * java反射bean的set方法
	 * 
	 * @param objectClass
	 * @param fieldName
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NoSuchMethodException
	 */
	@SuppressWarnings({ "unchecked" })
	private static Method getSetMethod(Class objectClass, String fieldName) throws NoSuchFieldException, SecurityException, NoSuchMethodException {
		Method method = null;
		Class[] parameterTypes = new Class[1];
		Field field = objectClass.getDeclaredField(fieldName);
		parameterTypes[0] = field.getType();
		StringBuffer sb = new StringBuffer();
		sb.append("set");
		sb.append(fieldName.substring(0, 1).toUpperCase());
		sb.append(fieldName.substring(1));
		method = objectClass.getMethod(sb.toString(), parameterTypes);
		return method;
	}

	/**
	 * 执行set方法
	 * 
	 * @param o
	 *            执行对象
	 * @param fieldName
	 *            属性
	 * @param value
	 *            值
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 */
	private static void invokeSet(Object o, String fieldName, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchFieldException, SecurityException, NoSuchMethodException {
		Method method = getSetMethod(o.getClass(), fieldName);
		String objType = method.getParameterTypes()[0].getSimpleName();
		Object newValue = null;
		if (objType.toLowerCase().contains("int")) {
			newValue = Integer.valueOf(value.toString());
		} else if (objType.toLowerCase().contains("long")) {
			newValue = Long.valueOf(value.toString());
		} else if (objType.toLowerCase().contains("double")) {
			newValue = Double.valueOf(value.toString());
		} else if (objType.toLowerCase().contains("float")) {
			newValue = Float.valueOf(value.toString());
		} else {
			newValue = value;
		}
		method.invoke(o, new Object[] { newValue });
	}

	/**
	 * 执行get方法
	 * 
	 * @param o
	 *            执行对象
	 * @param fieldName
	 *            属性
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 */
	private static Object invokeGet(Object o, String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Method method = getGetMethod(o.getClass(), fieldName);
		return method.invoke(o, new Object[0]);
	}

	/**
	 * 获取对象的所有字段
	 * 
	 * @param o
	 * @return
	 */
	private static Field[] getFields(Object o) {
		Field[] fields = o.getClass().getDeclaredFields();
		return fields;
	}

	/**
	 * 是否有字段
	 * 
	 * @param o
	 * @param fieldName
	 * @return
	 */
	private static boolean isHasField(Object o, String fieldName) {
		Field[] fields = getFields(o);
		for (Field field : fields) {
			if (field.getName().equals(fieldName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 数组中是否包含字符串
	 * 
	 * @param ignoreProperties
	 * @param str
	 * @return
	 */
	private static boolean isHasStr(String[] ignoreProperties, String str) {
		for (String string : ignoreProperties) {
			if (string.equals(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * copy(class-->class)
	 * 
	 * @param source源
	 * @param target目标
	 * @param isCopyNull是否拷贝null
	 * @param ignoreProperties忽略的字段
	 * @return
	 */
	public static void copyProperties(Object source, Object target, boolean isCopyNull, String[] ignoreProperties) {
		Field[] fields = getFields(source);
		for (Field field : fields) {
			String name = field.getName();
			Object value = null;
			try {
				value = invokeGet(source, name);

				if (null != ignoreProperties && isHasStr(ignoreProperties, name)) {
					continue;
				}
				if (isHasField(target, name)) {
					if (null != value) {
						invokeSet(target, name, value);
					} else if (isCopyNull) {
						invokeSet(target, name, value);
					}
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * copy(class-->class)
	 * 
	 * @param source源
	 * @param target目标
	 * @param isCopyNull是否拷贝null
	 * @return
	 */
	public static void copyProperties(Object source, Object target, boolean isCopyNull) {
		copyProperties(source, target, isCopyNull, null);
	}

	/**
	 * copy(map-->class)
	 * 
	 * @param map源
	 * @param target目标
	 * @param isCopyNull是否拷贝null
	 * @param ignoreProperties忽略的字段
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void copyProperties(Map<String, Object> map, Object target, boolean isCopyNull, String[] ignoreProperties) {
		Set set = map.keySet();
		for (Object object : set) {
			try {
				String name = object.toString();
				Object value = map.get(name);
				if (null != ignoreProperties && isHasStr(ignoreProperties, name)) {
					continue;
				}
				if (isHasField(target, name)) {
					if (null != value) {
						invokeSet(target, name, value);
					} else if (isCopyNull) {
						invokeSet(target, name, value);
					}
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * copy(map-->class)
	 * 
	 * @param map源
	 * @param target目标
	 * @param isCopyNull是否拷贝null
	 * @param ignoreProperties忽略的字段
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void copyPropertiesMapArray(Map<String, String[]> map, Object target, boolean isCopyNull, String[] ignoreProperties) {
		Set set = map.keySet();
		for (Object object : set) {
			try {
				String name = object.toString();
				String value = map.get(name)[0];
				if (null != ignoreProperties && isHasStr(ignoreProperties, name)) {
					continue;
				}
				if (isHasField(target, name)) {
					if (null != value) {
						invokeSet(target, name, value);
					} else if (isCopyNull) {
						invokeSet(target, name, value);
					}
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				continue;
			}
		}
	}

	public static void copyPropertiesMapArray(Map<String, String[]> map, Object target, boolean isCopyNull) {
		copyPropertiesMapArray(map, target, isCopyNull, null);
	}

	/**
	 * copy(map-->class)
	 * 
	 * @param map源
	 * @param target目标
	 * @param isCopyNull是否拷贝null
	 * @return
	 */
	public static void copyProperties(Map<String, Object> map, Object target, boolean isCopyNull) {
		copyProperties(map, target, isCopyNull, null);
	}

	/**
	 * copy(class-->map)
	 * 
	 * @param source源
	 * @param map目标
	 * @param isCopyNull是否拷贝null
	 * @param ignoreProperties忽略的字段
	 * @return
	 */
	@SuppressWarnings({ "unchecked" })
	public static void copyProperties(Object source, Map map, boolean isCopyNull, String[] ignoreProperties) {
		Field[] fields = getFields(source);
		for (Field field : fields) {
			String name = field.getName();
			Object value = null;
			try {
				value = invokeGet(source, name);

				if (null != ignoreProperties && isHasStr(ignoreProperties, name)) {
					continue;
				}
				if (null != value) {
					map.put(name, value);
				} else if (isCopyNull) {
					map.put(name, value);
				}
			} catch (Exception e) {
				continue;
			}
		}
	}

	/**
	 * copy(class-->map)
	 * 
	 * @param source源
	 * @param map目标
	 * @param isCopyNull是否拷贝null
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static void copyProperties(Object source, Map map, boolean isCopyNull) {
		copyProperties(source, map, isCopyNull, null);
	}

}

