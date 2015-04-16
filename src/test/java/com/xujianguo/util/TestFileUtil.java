package com.xujianguo.util;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.cache.CacheKey;
import org.junit.Test;

import com.xujianguo.cache.RedisCache;
import com.xujianguo.cache.manager.Statement2CacheKeys;

public class TestFileUtil extends ReflectProperty<A> {
	public String id;
	public Integer age;
	
	@Test
	public void test1() {
		System.out.println("mybatis-redis.properties".matches(".*redis\\.properties"));
	}
	
	@Test
	public void test2() {
		System.out.println(TestFileUtil.class.getName());
	}
	
	@Test
	public void test3() {
		Properties properties = new Properties();
		properties.setProperty("id", "hello");
		properties.setProperty("age", "21");
		System.out.println(properties.keySet());
		A a = PropertyReflect.reflect(A.class, properties);
		System.out.println(a.id);
		System.out.println(a.age);
	}
	
	@Test
	public void test4() {
		Properties properties = new Properties();
		properties.setProperty("id", "hello");
		properties.setProperty("age", "21");
		Iterator<Object> it = properties.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			System.out.println(key);
			if(key.equals("id")) {
				properties.remove(key);
			}
		}
		System.out.println(properties.keySet());
	}
	
	@Test
	public void test5() {
		Class clazz = A.class;
		Field[] fields = clazz.getDeclaredFields();
		for(Field field : fields) {
			Class c = field.getType();
			System.out.println(c);
			System.out.println(c == String[].class);
		}
	}
	
	@Test
	public void test6() {
		Properties properties = new Properties();
		properties.setProperty("id", "hello");
		properties.setProperty("age", "21");
		properties.setProperty("redis.shard.two.ip", "a");
		properties.setProperty("redis.shard.two.port", "a");
		properties.setProperty("redis.shard.one.ip", "a");
		properties.setProperty("redis.shard.one.port", "a");
		Enumeration e = properties.propertyNames();
		while(e.hasMoreElements()) {
			System.out.println(e.nextElement());
		}
	}
	
	@Test
	public void test7() {
		List<String> list = new ArrayList<String>();
		list.add("adf");
		list.add("eqw");
		String[] strs;
		strs = list.toArray(new String[list.size()]);
		for(String str : strs) {
			System.out.println(str);
		}
		System.out.println();
	}
	
	@Test
	public void test8() {
		try {
			Constructor[] cs = String[].class.getDeclaredConstructors();
			System.out.println(cs.length);
			for(Constructor c : cs) {
				System.out.println(c.getName());
			}
//			Constructor c = String[].class.getConstructor(String[].class);
//			Object strs = c.newInstance(new String[]{"1", "2"});
//			System.out.println(strs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test9() {
		System.out.println("redis.based.ip".matches("^redis\\.base\\..*"));
	}
	
	@Test
	public void test10() {
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		map.put("name", new HashSet<String>());
		map.get("name").add("ok");
		System.out.println(map);
	}
	
	@Test
	public void test11() {
		Statement2CacheKeys sc = new Statement2CacheKeys();
		sc.putElement("1234", new CacheKey());
		System.out.println(sc);
	}
	
	public static void main(String[] args) {
		File file = FileUtil.find(new File(System.getProperty("user.dir")+"/src"), ".*redis\\.properties");
		System.out.println(file != null ? true : false);
	}
}
