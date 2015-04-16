package com.xujianguo.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * 自动加载Property属性到到反射的类中
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月10日
 */
public abstract class ReflectProperty<T> {
	private static Logger log = Logger.getLogger(ReflectProperty.class);
	//实例Class
	private Class<T> entity;
	
	/**
	 * 在构造方法中获取实体类型
	 */
	public ReflectProperty() {
		//获取直接超类的Type，也就是这个类必须被继承才能获取类型
		Type genericType = getClass().getGenericSuperclass();
		//获取实际类型参数的 Type 对象的数组
		Type[] params = ((ParameterizedType)genericType).getActualTypeArguments();
		//获取第一个参数
		entity = (Class)params[0];
	}
	
	/**
	 * 通过Properties参数反射加载类属性的值
	 * @param prop
	 * @return
	 */
	public T get(Properties prop) {
		Set<Object> keys = prop.keySet();
		try {
			//实例
			T target = entity.newInstance();
			for(Object key : keys) {
				//用.分割properties里面的key，然后拿到最后一个作为属性名
				String[] keyStrings = ((String)key).split("\\.");
				String fieldName = keyStrings[keyStrings.length-1];
				try {
					//获取属性
					Field field = entity.getDeclaredField(fieldName);
					if(field != null) {
						//获取属性类
						Class typeClass = field.getType();
						//查找一个属性类中以String为参数的构造方法
						Constructor constructor = typeClass.getConstructor(String.class);
						//构造出这样的一个值
						Object obj = constructor.newInstance(prop.get(key));
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
			return target;
		} catch(Exception e) {
			log.error("[ReflectProperty:构造实例失败]", e);
			return null;
		}
	}
}
