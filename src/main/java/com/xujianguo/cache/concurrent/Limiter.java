package com.xujianguo.cache.concurrent;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

import org.apache.log4j.Logger;

/**
 * 处理高并发下，达到最大值时，产生阻塞
 * @author xujianguo
 * @email ray_xujianguo@yeah.net
 * @time 2015年4月12日
 */
public class Limiter {
	//日志记录
	private static final Logger log = Logger.getLogger(Limiter.class);
	
	//同步器
	private final Sync sync;
	//内部计数器
	private final AtomicLong count;
	//限制值
	private volatile long limit;
	
	//初始化
	public Limiter(long limit) {
		this.limit = limit;
		count = new AtomicLong(0);
		sync = new Sync();
	}
	
	/**
	 * 使用AQS框架的共享模式进行同步
	 * @author xujianguo
	 * @email ray_xujianguo@yeah.net
	 * @time 2015年3月31日
	 */
	@SuppressWarnings("serial")
	private class Sync extends AbstractQueuedSynchronizer {
		public Sync() {}
		
		/**
		 * 超过limit值进行阻塞
		 */
		@Override
		protected int tryAcquireShared(int arg) {
			long newValue = count.incrementAndGet();
			if(newValue > limit) {
				count.decrementAndGet();
				return -1;
			}
			return 1;
		}
		
		/**
		 * 减少limit
		 */
		@Override
		protected boolean tryReleaseShared(int arg) {
			count.decrementAndGet();
			return true;
		}
	}
	
	/**
	 * 限制
	 * @throws InterruptedException
	 */
	public void limit() throws InterruptedException {
		sync.acquireSharedInterruptibly(0);
	}
	
	/**
	 * 解除限制
	 * @return
	 */
	public boolean unlimit() {
		return sync.releaseShared(0);
	}
	
	/**
	 * 重置计数器
	 */
	public void reset() {
		count.set(0);
	}
}
