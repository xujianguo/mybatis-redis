package com.xujianguo.cache.pool;

import org.apache.log4j.Logger;

import com.xujianguo.cache.concurrent.Limiter;

import redis.clients.jedis.ShardedJedisPool;
import redis.clients.util.Pool;

/**
 * 连接对象池
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月12日
 */
public class RedisPool {
	private static Logger log = Logger.getLogger(RedisPool.class);
	
	//单例模式
	private static RedisPool pool;
	//真实使用的池
	private Pool realPool;
	//线程控制器
	private Limiter limiter;
	//配置
	private Config config;
	
	private RedisPool() {
		//根据配置文件构造不同的
		this.config = new Config();
		if(config.isShardEnable()) {
			realPool = config.getShardedJedisPool(config.getConfig());
		} else {
			realPool = config.getRedisPool(config.getConfig());
		}
		limiter = config.getLimiter();
	}
	
	/**
	 * 懒汉式
	 * @return
	 */
	public synchronized static RedisPool getPool() {
		if(pool == null) {
			pool = new RedisPool();
		}
		return pool;
	}
	
	/**
	 * 获取客户端连接
	 * @return
	 */
	public synchronized JedisWrapper get() {
		try {
			limiter.limit();
		} catch (InterruptedException e) {
			log.error("[RedisPool:线程控制失败]", e);
		}
		return new JedisWrapper(realPool.getResource(), config.isShardEnable());
	}
	
	/**
	 * 将客户端连接返回连接池
	 * @param wrapper
	 */
	public void returnResource(JedisWrapper wrapper) {
		if(wrapper != null) {
			realPool.returnResource(wrapper.getTarget());
		}
		limiter.unlimit();
	}
}
