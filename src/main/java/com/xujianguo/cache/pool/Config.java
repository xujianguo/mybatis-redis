package com.xujianguo.cache.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.ShardedJedisPool;

import com.xujianguo.cache.concurrent.Limiter;
import com.xujianguo.util.PropertyParser;
import com.xujianguo.util.PropertyReflect;
import com.xujianguo.util.RegexFilter;

/**
 * 加载配置文件的类，维护了properties文件对应的属性
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月12日
 */
public class Config {
	//基本配置
	private RedisBase base;
	//对象池的配置
	private RedisPool pool;
	//分片的配置
	private RedisShard shard;
	//线程控制参数
	private LimiterParam limiterParam;
	
	/**
	 * 加载对应的配置到对应的类中
	 */
	public Config() {
		PropertyParser parser = PropertyParser.getInstance();
		Properties pBase = parser.doFilter(new RegexFilter("^redis\\.base\\..*"));
		Properties pPool = parser.doFilter(new RegexFilter("^redis\\.pool\\..*"));
		Properties pShard = parser.doFilter(new RegexFilter("^redis\\.shard\\..*"));
		Properties pLimiter = parser.doFilter(new RegexFilter("^redis\\.limiter\\..*"));
		base = PropertyReflect.reflect(RedisBase.class, pBase);
		pool = PropertyReflect.reflect(RedisPool.class, pPool);
		shard = PropertyReflect.reflect(RedisShard.class, pShard);
		limiterParam = PropertyReflect.reflect(LimiterParam.class, pLimiter);
	}
	
	/**
	 * 分片模式是否有效
	 * @return
	 */
	public boolean isShardEnable() {
		return shard.getEnable();
	}
	
	/**
	 * 获取对象池的配置
	 * @return
	 */
	public JedisPoolConfig getConfig() {
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxIdle(pool.getMaxIdle());
		config.setMaxTotal(pool.getMaxActive());
		config.setMaxWaitMillis(pool.maxWait);
		return config;
	}
	
	/**
	 * 获取RedisPool
	 * @param config
	 * @return
	 */
	public JedisPool getRedisPool(JedisPoolConfig config) {
		JedisPool basePool = new JedisPool(config, base.getIp(), base.getPort(), pool.getTimeout(), base.getAuth());
		return basePool;
	}
	
	/**
	 * 获取ShardedRedisPool
	 * @param config
	 * @return
	 */
	public ShardedJedisPool getShardedJedisPool(JedisPoolConfig config) {
		ShardedJedisPool shardPool = new ShardedJedisPool(config, getShardList());
		return shardPool;
	}
	
	/**
	 * 获取分片主机列表
	 * @return
	 */
	public List<JedisShardInfo> getShardList() {
		List<JedisShardInfo> list = new ArrayList<JedisShardInfo>();
		String[] ip = shard.getIp();
		Integer[] port = shard.getPort();
		String[] auth = shard.getAuth();
		for(int i = 0; i < ip.length; i++) {
			JedisShardInfo jsi = new JedisShardInfo(ip[i], port[i], auth[i]);
			list.add(jsi);
		}
		return list;
	}
	
	/**
	 * 获取线程控制器
	 * @return
	 */
	public Limiter getLimiter() {
		return new Limiter(limiterParam.getLimit());
	}
	
	/**
	 * RedisBase基本配置
	 * @author xujianguo
	 * @email ray_xujianguo@yeah.net
	 * @time 2015年4月12日
	 */
	public static class RedisBase {
		private String ip = "127.0.0.1";
		private Integer port = 6379;
		private String auth;
		
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
		public String getAuth() {
			return auth;
		}
		public void setAuth(String auth) {
			this.auth = auth;
		}
		public Integer getPort() {
			return port;
		}
		public void setPort(Integer port) {
			this.port = port;
		}
	}
	
	/**
	 * RedisPool对象池的配置
	 * @author xujianguo
	 * @email ray_xujianguo@yeah.net
	 * @time 2015年4月12日
	 */
	public static class RedisPool {
		private Integer maxActive = 1024;
		private Integer maxIdle = 200;
		private Integer maxWait = 10000;
		private Integer timeout = 10000;
		
		public Integer getMaxActive() {
			return maxActive;
		}
		public void setMaxActive(Integer maxActive) {
			this.maxActive = maxActive;
		}
		public Integer getMaxIdle() {
			return maxIdle;
		}
		public void setMaxIdle(Integer maxIdle) {
			this.maxIdle = maxIdle;
		}
		public Integer getMaxWait() {
			return maxWait;
		}
		public void setMaxWait(Integer maxWait) {
			this.maxWait = maxWait;
		}
		public Integer getTimeout() {
			return timeout;
		}
		public void setTimeout(Integer timeout) {
			this.timeout = timeout;
		}
	}
	
	/**
	 * RedisShard分片的配置
	 * @author xujianguo
	 * @email ray_xujianguo@yeah.net
	 * @time 2015年4月12日
	 */
	public static class RedisShard {
		private Boolean enable = false;
		private String[] ip;
		private Integer[] port;
		private String[] auth;
		public Boolean getEnable() {
			return enable;
		}
		public void setEnable(Boolean enable) {
			this.enable = enable;
		}
		public String[] getIp() {
			return ip;
		}
		public void setIp(String[] ip) {
			this.ip = ip;
		}
		public Integer[] getPort() {
			return port;
		}
		public void setPort(Integer[] port) {
			this.port = port;
		}
		public String[] getAuth() {
			return auth;
		}
		public void setAuth(String[] auth) {
			this.auth = auth;
		}
	}
	
	/**
	 * 线程控制器的参数类
	 * @author xujianguo
	 * @email ray_xujianguo@yeah.net
	 * @time 2015年4月12日
	 */
	private class LimiterParam {
		private Long limit;

		public Long getLimit() {
			return limit;
		}

		public void setLimit(Long limit) {
			this.limit = limit;
		}
	}
}
