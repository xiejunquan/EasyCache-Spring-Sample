# EasyCache在Spring中的使用

## 导入EasyCache-Spring的maven依赖
EasyCache-Spring是EasyCache和Spring的适配包, 其中已经包含了EasyCache和Spring-Context的依赖包

    <dependency>
        <groupId>EasyCache-Spring</groupId>
        <artifactId>EasyCache-Spring</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>

## 实现缓存的接口
CacheInterface有get和set两个方法, 用于作为RemoteCache的调用接口, 你可以使用任意的存储实例来实现这两个方法(这里使用单实例的redis缓存来实现)

    public class RedisCache implements CacheInterface {
        private JedisPool jedisPool;

        public RedisCache(JedisPoolConfig config, String ip, int port, int timeout) {
            this.jedisPool = new JedisPool(config, ip, port, timeout);
        }

        @Override
        public void set(String key, String value, int expireSeconds) {
            Jedis jedis = null;
            try{
                jedis = jedisPool.getResource();
                jedis.setex(key, expireSeconds, value);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(jedis != null){
                    jedis.close();
                }
            }
        }

        @Override
        public String get(String key) {
            Jedis jedis = null;
            try{
                jedis = jedisPool.getResource();
                return jedis.get(key);
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(jedis != null){
                    jedis.close();
                }
            }
            return null;
        }
    }

## spring xml配置

    <!-- EasyCache缓存配置 -->
    <bean id="cacheConfigFactory" class="com.ecache.spring.ECacheConfigFactory">
        <property name="defaultExpiredSeconds" value="300"></property>
        <property name="schedulerCorePoolSize" value="64"></property>
        <property name="avoidServerOverload" value="false"></property>
        <property name="lockIsFair" value="false"></property>
        <property name="lockSegments" value="128"></property>
        <property name="clearSchedulerIntervalSeconds" value="3600"></property>
    </bean>
    <bean id="cacheConfig" class="com.ecache.CacheConfig" factory-bean="cacheConfigFactory" factory-method="create"></bean>

    <!-- EasyCache的本地缓存配置 -->
    <bean id="localCache" class="com.ecache.LocalCache">
        <constructor-arg ref="cacheConfig"></constructor-arg>
    </bean>

    <!-- EasyCache的远程缓存配置 -->
    <bean id="redisConfig" class="redis.clients.jedis.JedisPoolConfig">
        <property name="maxTotal" value="200"></property>
        <property name="maxIdle" value="20"></property>
        <property name="minIdle" value="20"></property>
        <property name="maxWaitMillis" value="5000"></property>
    </bean>
    <bean id="redisCache" class="com.ecache.test.RedisCache">
        <constructor-arg ref="redisConfig"></constructor-arg>
        <constructor-arg name="ip" value="127.0.0.1"></constructor-arg>
        <constructor-arg name="port" value="6380"></constructor-arg>
        <constructor-arg name="timeout" value="2000"></constructor-arg>
    </bean>
    <bean id="remoteCache" class="com.ecache.RemoteCache">
        <constructor-arg ref="cacheConfig"></constructor-arg>
        <constructor-arg ref="redisCache"></constructor-arg>
    </bean>

    <!-- 如果要使EasyCache生效, 必须要配置这个对象 -->
    <bean class="com.ecache.spring.ECacheBeanDefinitionRegistryPostProcessor">
        <constructor-arg value="com.ecache.test"></constructor-arg>
    </bean>

前面的配置都是为了初始化RemoteCache和LocalCache, 而最后一个配置是必须的, 它用于使@RemoteCache, @LocalCache和@Cache的注解生效

至此, EasyCache就可以在你的Spring项目中跑起来了


