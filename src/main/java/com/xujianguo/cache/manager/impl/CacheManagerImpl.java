package com.xujianguo.cache.manager.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.log4j.Logger;

import com.xujianguo.cache.manager.CacheManager;
import com.xujianguo.cache.manager.Statement2CacheKeys;

public class CacheManagerImpl implements CacheManager {
	private static Logger log = Logger.getLogger(CacheManagerImpl.class);
	
	// 初始化状态
	private boolean init = false;
	// 开启缓存状态
	private boolean enabled = false;
	// 记录每一个statement对应的Cache
	private Map<String, Cache> statement2Cache = new ConcurrentHashMap<String, Cache>();
	// 单例模式
	private static CacheManagerImpl instance;
	//每一个statementId 更新依赖的statementId集合
	private Map<String, Set<String>> statement2Observers = new ConcurrentHashMap<String, Set<String>>();
	//记录statementId对应的CacheKey
	private Statement2CacheKeys statement2CacheKeys = new Statement2CacheKeys();
	
	private CacheManagerImpl() {}

	/**
	 * 懒汉式
	 * 
	 * @return
	 */
	public synchronized static CacheManager getInstance() {
		if (instance == null)
			instance = new CacheManagerImpl();
		return instance;
	}

	@Override
	public boolean isInit() {
		return init;
	}

	@Override
	public boolean isCacheEnabled() {
		return enabled;
	}

	@Override
	public void init(Properties properties) {
		String dependency = properties.getProperty("dependency");
		if (!("".equals(dependency) || dependency == null)) {
			InputStream inputStream;
			try {
				inputStream = Resources.getResourceAsStream(dependency);
				XPathParser parser = new XPathParser(inputStream);
				List<XNode> statements = parser
						.evalNodes("/dependencies/statements/statement");
				for (XNode node : statements) {
					Set<String> temp = new HashSet<String>();
					List<XNode> obs = node.evalNodes("observer");
					for (XNode observer : obs) {
						temp.add(observer.getStringAttribute("id"));
					}
					statement2Observers.put(node.getStringAttribute("id"), temp);
				}
				init = true;
			} catch (IOException e) {
				log.error("[CacheManagerImpl:加载xml失败]", e);
			}
		}
		String cacheEnabled = properties.getProperty("cacheEnabled", "true");
		if("true".equals(cacheEnabled)) {
			this.enabled = true;
		}
	}

	@Override
	public void addStatementCacheMap(String statement, Cache cache) {
		if(statement2Cache.containsKey(statement) && statement2Cache.get(statement) != null) {
			return;
		}
		statement2Cache.put(statement, cache);
	}

	@Override
	public void clearRelatedCaches(Set<String> statements) {
		for(String statement : statements) {
			//获取关联的带清理的statement
			Set<String> relatedStatements = statement2Observers.get(statement);
			for(String relatedStatement : relatedStatements) {
				//获取关联的带清理的statement的Cache
				Cache cache = statement2Cache.get(relatedStatement);
				//关联的带清理的statement
				Set<CacheKey> cacheKeys = statement2CacheKeys.get(relatedStatement);
				for(CacheKey cacheKey : cacheKeys) {
					cache.removeObject(cacheKey);
				}
			}
			//清理没有用的statement的CacheKeys
			statement2CacheKeys.remove(statement);
		}
	}

	@Override
	public void refreshCacheKey(Statement2CacheKeys all) {
		statement2CacheKeys.putAll(all);
	}
}
