###基于redis的mybatis二级缓存插件
***
####简介
使用redis作为mybatis的缓存介质，通过使用mybatis的拦截器，在SQL执行的过程中，检查各个Mapper的二级缓存中过期的部分，匹配成功后删除过期的缓存，保证缓存的实时正确性。

####使用

#####添加redis的配置文件
下面是一份properties文件，将它放置在src包下即可，我的FileUtile类就可以找到。这份是一个包含整个所可以配置的东西，根据自己项目的需求抽取对应的配置整理成properties文件即可。
```properties
#redis的基础配置
redis.base.ip=127.0.0.1
redis.base.port=6379
redis.base.auth=xujianguo
#redis的对象池配置
#可用连接实例的最大数目，默认值为8
redis.pool.maxActive=1024
#控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8
redis.pool.maxIdle=200
#等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException
redis.pool.maxWait=10000
#连接超时时间
redis.pool.timeout=10000
#是否开启shard分布式模式
redis.shard.enable=false
#主机one的配置
redis.shard.one.ip=127.0.0.1
redis.shard.one.port=6379
redis.shard.one.auth=xujianguo
#主机two的配置
redis.shard.two.ip=127.0.0.1
redis.shard.two.port=6378
redis.shard.two.auth=xujianguo
#线程数控制
redis.limiter.limit=100000
```

#####二级缓存的关联配置
解决Mapper之间二级缓存的过期文件，就是通过一份关联配置来实现的，observers配置的是一个观察者，观察着statement的变化，statement的缓存更新了，就通过观察者，观察者立刻更新缓存，从而保证获取到是最新的数据。
```xml
<?xml version="1.0" encoding="UTF-8"?>
<dependencies>
   <statements>
       <statement id="com.xujianguo.dao.PersonDao.findById">
          <observer id="com.xujianguo.dao.UserDao.findOne"/>
       </statement>
   </statements>
</dependencies>
```
这个是上面xml的dtd文件，大家可以根据这个进行配置xml元素
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!ELEMENT dependencies (statements?)>
<!ELEMENT statements (statement*)>
<!ELEMENT statement (observer+)>
<!ELEMENT statement EMPTY>
<!ATTLIST statement id CDATA #REQUIRED>
<!ELEMENT observer EMPTY>
<!ATTLIST observer id CDATA #REQUIRED>
```

#####使用指定二级缓存
首先在mybatis的核心xml中配置好下面的全局设置，目的是开启mybatis的二级缓存
```xml
	<!-- 指定Mybatis使用log4j -->
	<settings>
		<setting name="cacheEnabled" value="true"/>
	</settings>
```
在mapper中配置二级缓存，执行我们的RedisCache作为缓存的实现类
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//ibatis.apache.org//DTD Mapper 3.0//EN"
"http://ibatis.apache.org/dtd/ibatis-3-mapper.dtd">
<mapper namespace="com.xujianguo.dao.PersonDao">
	<cache eviction="LRU" type="com.xujianguo.cache.RedisCache"/>

	<select id="findById" parameterType="int" resultMap="PersonMap" useCache="true">
		select * from person where id=#{id}
	</select>
</mapper>
```
这样就可以实现我们的最终目的了。

####设计及实现

#####拦截器中实现缓存更新
我们在CacheHandlerIntercept这个核心拦截器中拦截了四类method，query/update/commit/rollback，分别对应查询/更新/提交/回滚四类操作，下面这幅图展示了四类操作是如何对缓存进行更新的。
![mybatis-redis-1](picture/mybatis-redis-1.png)

#####二级缓存实现类RedisCache














































