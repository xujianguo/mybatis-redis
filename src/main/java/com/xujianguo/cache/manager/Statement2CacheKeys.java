package com.xujianguo.cache.manager;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.cache.CacheKey;

/**
 * 存储statement和CacheKey的对应关系
 * 
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月16日
 */
public class Statement2CacheKeys {
	private Map<String, Set<CacheKey>> statement2CacheKeys = new ConcurrentHashMap<String, Set<CacheKey>>();

	public Set<CacheKey> get(String key) {
		if (statement2CacheKeys.get(key) != null) {
			statement2CacheKeys.put(key, new HashSet<CacheKey>());
		}
		return statement2CacheKeys.get(key);
	}

	public Set<CacheKey> put(String key, Set<CacheKey> cacheKeys) {
		return statement2CacheKeys.put(key, cacheKeys);
	}

	public void putElement(String key, CacheKey cacheKey) {
		if (statement2CacheKeys.get(key) == null) {
			statement2CacheKeys.put(key, new HashSet<CacheKey>());
			System.out.println(statement2CacheKeys);
		}
		statement2CacheKeys.get(key).add(cacheKey);
	}

	public Set<CacheKey> remove(String key) {
		return statement2CacheKeys.remove(key);
	}

	public void clear() {
		statement2CacheKeys.clear();
	}

	public Set<String> keySet() {
		return statement2CacheKeys.keySet();
	}

	public Map<String, Set<CacheKey>> getEntity() {
		return statement2CacheKeys;
	}

	public void putAll(Statement2CacheKeys all) {
		for (Entry<String, Set<CacheKey>> entry : statement2CacheKeys.entrySet()) {
			for (CacheKey item : entry.getValue()) {
				putElement(entry.getKey(), item);
			}
		}
	}
}
