package com.xujianguo.cache.pool;

import org.apache.log4j.Logger;

import com.xujianguo.util.SerializeUtil;

import redis.clients.jedis.BinaryJedisCommands;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;

public class JedisWrapper {
	private static Logger log = Logger.getLogger(JedisWrapper.class);
	
	private boolean shardedEnable;
	private BinaryJedisCommands target;
	
	public JedisWrapper(Object target, boolean shardedEnable) {
		this.shardedEnable = shardedEnable;
		try {
			if(shardedEnable) {
				this.target = (ShardedJedis)target;
			} else {
				this.target = (Jedis)target;
			}
		} catch(Exception e) {
			log.error("[JedisWrapper:加载Class文件出错]", e);
		}
		
	}
	
	public void putObject(Object key, Object value) {
		target.set(SerializeUtil.serialize(key), SerializeUtil.serialize(value));
	}
	
	public Object getObject(Object key) {
		byte[] data = target.get(SerializeUtil.serialize(key));
		if(data != null && data.length > 0) {
			return SerializeUtil.deserialize(data);
		} else {
			return null;
		}
	}
	
	public Object removeObject(Object key) {
		return target.expire(SerializeUtil.serialize(key), 0);
	}
	
	public void clear() {
		if(!shardedEnable) {
			((Jedis)target).flushDB();
		}
	}
	
	public long getSize() {
		if(!shardedEnable) {
			return ((Jedis)target).dbSize();
		}
		return 0;
	}
	
	public Object getTarget() {
		return target;
	}
	
	public boolean isShardedEnable() {
		return shardedEnable;
	}
}
