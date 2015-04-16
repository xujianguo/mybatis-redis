package com.xujianguo.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 加载redis的配置文件
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月10日
 */
public class PropertyParser {
	private static Logger log = Logger.getLogger(PropertyParser.class); 
	
	//文件名的正则表达式
	private final static String SUFFIX = ".*redis\\.properties";
	//使用单例模式
	private static PropertyParser instance;
	//Properties文件实例
	private static Properties properties;
	
	//设置私有构造方法不被外界调用
	private PropertyParser() {}
	
	/**
	 * 使用懒汉式，存在线程安全问题，用synchronized避免
	 * @return 对应实例
	 */
	public synchronized static PropertyParser getInstance() {
		if(instance == null) {
			File file = FileUtil.find(new File(System.getProperty("user.dir") + "/src"), SUFFIX);
			if(file != null) {
				instance = new PropertyParser();
				properties = new Properties();
				try {
					properties.load(new FileInputStream(file));
				} catch (Exception e) {
					log.error("[PropertyParser:加载properties文件失败]", e);
				}
			} else {
				log.error("[PropertyParser:找不到对应的properties]");
				System.exit(0);
			}
		} 
		return instance;
	}
	
	public Properties getProperties() {
		return properties;
	}
	
	/**
	 * 通过过滤器过滤掉不想看到的属性
	 * @param filter 过滤器
	 * @return
	 */
	public Properties doFilter(PropertyFilter filter) {
		Properties prop = (Properties) properties.clone();
		Iterator<Object> it = prop.keySet().iterator();
		while(it.hasNext()) {
			String key = (String) it.next();
			if(!filter.accept(key))
				it.remove();
		}
		return prop;
	}
	
	/**
	 * 属性过滤器
	 * @author xujianguo
	 * @email ray_xujianguo@yeah.net
	 * @time 2015年4月11日
	 */
	public interface PropertyFilter {
		/**
		 * 核心过滤方法
		 * @param key
		 * @return
		 */
		public boolean accept(String key);
	}
}
