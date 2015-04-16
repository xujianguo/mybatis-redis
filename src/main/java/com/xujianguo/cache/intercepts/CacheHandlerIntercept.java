package com.xujianguo.cache.intercepts;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.log4j.Logger;

import com.xujianguo.cache.manager.CacheManager;
import com.xujianguo.cache.manager.Statement2CacheKeys;
import com.xujianguo.cache.manager.impl.CacheManagerImpl;

@Intercepts(value = {
		@Signature(args = { MappedStatement.class, Object.class,
				RowBounds.class, ResultHandler.class }, method = "query", type = Executor.class),
		@Signature(args = { MappedStatement.class, Object.class }, method = "update", type = Executor.class),
		@Signature(args = { boolean.class }, method = "commit", type = Executor.class),
		@Signature(args = { boolean.class }, method = "rollback", type = Executor.class),
		@Signature(args = { boolean.class }, method = "close", type = Executor.class) })
public class CacheHandlerIntercept implements Interceptor {
	private static Logger log = Logger.getLogger(CacheHandlerIntercept.class);
	// 缓存管理器
	private CacheManager manager = CacheManagerImpl.getInstance();
	private Statement2CacheKeys queryCacheOnCommit = new Statement2CacheKeys();
	private Set<String> updateStatementOnCommit = new HashSet<String>();

	/**
	 * 核心拦截方法
	 */
	public Object intercept(Invocation invocation) throws Throwable {
		String name = invocation.getMethod().getName();
		Object result = null;
		if ("query".equals(name)) {
			result = this.processQuery(invocation);
		} else if ("update".equals(name)) {
			result = this.processUpdate(invocation);
		} else if ("commit".equals(name)) {
			result = this.processCommit(invocation);
		} else if ("rollback".equals(name)) {
			result = this.processRollback(invocation);
		} else if ("close".equals(name)) {
			result = this.processClose(invocation);
		}
		return result;
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	/**
	 * 执行查询时候的操作
	 * 
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	public Object processQuery(Invocation invocation) throws Throwable {
		Object result = invocation.proceed();
		if (manager.isCacheEnabled()) {
			Object[] args = invocation.getArgs();
			MappedStatement mappedStatement = (MappedStatement) args[0];
			// 清除缓存
			if (mappedStatement.isFlushCacheRequired()) {
				queryCacheOnCommit.clear();
			}
			// 如果缓存可用就mark下
			if (mappedStatement.isUseCache()
					&& mappedStatement.getCache() != null) {
				manager.addStatementCacheMap(mappedStatement.getId(),
						mappedStatement.getCache());
			}
			Object parameter = args[1];
			RowBounds rowBounds = (RowBounds) args[2];
			Executor executor = (Executor) invocation.getTarget();
			BoundSql boundSql = mappedStatement.getBoundSql(parameter);
			// 记录本次查询所产生的CacheKey
			CacheKey cacheKey = executor.createCacheKey(mappedStatement,
					parameter, rowBounds, boundSql);
			queryCacheOnCommit.putElement(mappedStatement.getId(), cacheKey);
		}
		return result;
	}

	/**
	 * 执行update时候的操作
	 * 
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	public Object processUpdate(Invocation invocation) throws Throwable {
		Object result = invocation.proceed();
		MappedStatement mappedStatement = (MappedStatement) invocation
				.getArgs()[0];
		// 添加需要更新的statement
		updateStatementOnCommit.add(mappedStatement.getId());
		return result;
	}

	/**
	 * 执行commit时候的操作
	 * 
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	public Object processCommit(Invocation invocation) throws Throwable {
		Object result = invocation.proceed();
		// 刷新缓存
		refreshCache();
		return result;
	}

	/**
	 * 执行rollback时候的操作
	 * 
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	public Object processRollback(Invocation invocation) throws Throwable {
		Object result = invocation.proceed();
		// 清除此次会话的数据
		clearSessionData();
		return result;
	}

	/**
	 * 执行close方法的操作
	 * 
	 * @param invocation
	 * @return
	 * @throws Throwable
	 */
	public Object processClose(Invocation invocation) throws Throwable {
		Object result = invocation.proceed();
		boolean forceRollback = (Boolean) invocation.getArgs()[0];
		if (forceRollback) {
			clearSessionData();
		} else {
			refreshCache();
		}
		return result;
	}

	/**
	 * 清除会话产生的数据
	 */
	private synchronized void clearSessionData() {
		queryCacheOnCommit.clear();
		updateStatementOnCommit.clear();
	}

	/**
	 * 刷新缓存
	 */
	private synchronized void refreshCache() {
		manager.refreshCacheKey(queryCacheOnCommit);
		manager.clearRelatedCaches(updateStatementOnCommit);
		clearSessionData();
	}

	/**
	 * 通过Properties文件初始化manager
	 */
	public void setProperties(Properties properties) {
		if (!manager.isInit()) {
			manager.init(properties);
		}
	}
}
