package com.xujianguo.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 通过类和properties文件反射加载属性到类中
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月12日
 */
public class PropertyReflect {
	private static Logger log = Logger.getLogger(PropertyReflect.class);
	
	//支持的数组识别类型
	private static Class[] supportedArray = new Class[]{
			String[].class,
			Integer[].class,
			Boolean[].class
	}; 
	
	/**
	 * 设置clazz实例的属性值
	 * @param clazz
	 * @param prop
	 * @return
	 */
	public static <C> C reflect(Class<C> clazz, Properties prop) {
		Set<Object> keys = prop.keySet();
		try {
			//实例
			C target = (C) clazz.getDeclaredConstructors()[0].newInstance(null);
			//通过key找属性
			for(Object key : keys) {
				//用.分割properties里面的key，然后拿到最后一个作为属性名
				String[] keyStrings = ((String)key).split("\\.");
				String fieldName = keyStrings[keyStrings.length-1];
				try {
					//获取属性
					Field field = clazz.getDeclaredField(fieldName);
					if(field != null) {
						//获取属性类
						Class typeClass = field.getType();
						//查找一个属性类中以String为参数的构造方法
						Constructor constructor = typeClass.getConstructor(String.class);
						//构造出这样的一个值
						Object obj = constructor.newInstance(prop.get(key).toString());
						//强制访问属性
						field.setAccessible(true);
						//为属性设置值
						field.set(target, obj);
					}
				} catch(NoSuchFieldException e) {
					log.error("[ReflectProperty:没有properties文件中的字段]", e);
				} catch(SecurityException e) {
					log.error("[ReflectProperty:没有权限访问这个类]", e);
				}
			}
			//匹配数组属性
			Field[] fields = clazz.getDeclaredFields();
			for(Field field : fields) {
				// 判断是不是我们处理的数组类别
				if(index(field.getType()) != -1) {
					//存储遍历中找到的值
					List<Object> list = new ArrayList<Object>();
					for(Object key : prop.keySet()) {
						String[] keyStrings = ((String)key).split("\\.");
						String fieldName = keyStrings[keyStrings.length-1];
						if(field.getName().equals(fieldName)) {
							list.add(prop.get(key));
						}
					}
					//数组实例的创建
					Object array = Array.newInstance(field.getType().getComponentType(), list.size());
					for(int i = 0; i < list.size(); i++) {
						Array.set(array, i, list.get(i));
					}
					list.toArray();
					field.setAccessible(true);
					//为属性设置值
					field.set(target, array);
				}
			}
			return target;
		} catch(Exception e) {
			log.error("[ReflectProperty:构造实例失败]", e);
			return null;
		}
	}
	
	/**
	 * 判断传入的数组类别是否被该类支持
	 * @param clazz 传入的数组类别
	 * @return -1定位失败，其他都是可以被支持的
	 */
	public static int index(Class clazz) {
		for(int i = 0; i < supportedArray.length; i++) {
			if(supportedArray[i] == clazz)
				return i;
		}
		return -1;
	}
}
