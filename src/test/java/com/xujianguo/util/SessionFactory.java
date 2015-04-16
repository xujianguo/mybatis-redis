package com.xujianguo.util;

import java.io.IOException;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

/**
 * SqlSessionFactory工厂
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年3月30日
 */
public class SessionFactory {
	private static SqlSessionFactory mainSessionFactory;
	private static SqlSessionFactory testSessionFactory;
	private final static String resource = "mybatis.xml";
	
	private static Logger logger = Logger.getLogger(SessionFactory.class);
	
	public static SqlSessionFactory getSessionFactory(String which) {
		switch(which) {
		case "main":
			return build(mainSessionFactory, which);
		case "test":
			return build(testSessionFactory, which);
		default:
			return null;
		}
	}
	
	public synchronized static SqlSessionFactory build(SqlSessionFactory sessionFactory, String which) {
		if(sessionFactory == null) {
			try {
				sessionFactory = new SqlSessionFactoryBuilder().build(Resources.getResourceAsReader(resource), which);
			} catch (IOException e) {
				logger.error("获取SqlSessionFactory失败", e);
				System.exit(-1);
			}
		}
		return sessionFactory;
	}
}
