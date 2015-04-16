package com.xujianguo.cache.manager;

import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.cache.Cache;

/**
 * 缓存管理器
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月16日
 */
public interface CacheManager {
	/**
	 * 是否初始化
	 * @return
	 */
	public boolean isInit();
	
	/**
	 * 是否启动了二级缓存
	 * @return
	 */
	public boolean isCacheEnabled();
	
	/**
	 * 初始化
	 * @param properties
	 */
	public void init(Properties properties);
	
	/**
	 * 添加statement和cache的对应关系
	 * @param statement
	 * @param cache
	 */
	public void addStatementCacheMap(String statement, Cache cache);
	
	/**
	 * 清理关联的Cache
	 * @param relatedStatements
	 */
	public void clearRelatedCaches(Set<String> statements);
	
	/**
	 * 通过CacheKey数据
	 * @param statement2CacheKeys
	 */
	public void refreshCacheKey(Statement2CacheKeys statement2CacheKeys);
}
