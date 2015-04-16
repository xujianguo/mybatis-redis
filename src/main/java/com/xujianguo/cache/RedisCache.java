package com.xujianguo.cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.ibatis.cache.Cache;
import org.apache.log4j.Logger;

import com.xujianguo.cache.pool.JedisWrapper;
import com.xujianguo.cache.pool.RedisPool;

/**
 * Redis的缓存核心类
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月16日
 */
public class RedisCache implements Cache {
	private static Logger log = Logger.getLogger(RedisCache.class);
	
	//id属性只是一个名字标识，就像PerpetualCache里面的id被BaseExecutor设置为LocalCache
	private String id;
	//读写锁，这个必须提供，Executor调用Cache的时候就加锁
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	//对象池
	private RedisPool pool;
	
	public RedisCache(String id) {
		this.id = id;
		pool = RedisPool.getPool();
	}
	
	public String getId() {
		return id;
	}

	public int getSize() {
		JedisWrapper wrapper = pool.get();
		int result = (int) wrapper.getSize();
		pool.returnResource(wrapper);
		return result;
	}

	public void putObject(Object key, Object value) {
		JedisWrapper wrapper = pool.get();
		wrapper.putObject(key, value);
		pool.returnResource(wrapper);
	}

	public Object getObject(Object key) {
		JedisWrapper wrapper = pool.get();
		Object result = wrapper.getObject(key);
		pool.returnResource(wrapper);
		return result;
	}

	public Object removeObject(Object key) {
		JedisWrapper wrapper = pool.get();
		Object result = wrapper.removeObject(key);
		pool.returnResource(wrapper);
		return result;
	}

	public void clear() {
		JedisWrapper wrapper = pool.get();
		wrapper.clear();
		pool.returnResource(wrapper);
	}

	public ReadWriteLock getReadWriteLock() {
		return readWriteLock;
	}
}
