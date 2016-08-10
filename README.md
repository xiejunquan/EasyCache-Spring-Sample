# EasyCacheForSpring
将EasyCache集成到Spring需要做哪些工作

实现bean工厂的接口

    public class SpringCacheBeanFactory implements BeanFactoryInterface{

        private DefaultListableBeanFactory beanFactory;

        public SpringCacheBeanFactory(ConfigurableWebApplicationContext applicationContext) {
            this.beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        }

        @Override
        public <T> void set(Class<?> clazz, T object) {
            String beanId = getBeanId(clazz);
            set(clazz, beanId, object);
        }

        @Override
        public <T> void set(Class<?> clazz, String id, T object) {
            String beanId = (id != null ) ? id : getBeanId(clazz);
            if(beanFactory.containsBean(beanId)){
                beanFactory.destroySingleton(beanId);
                beanFactory.registerSingleton(beanId, object);
            }else{
                BeanDefinition beanDefinition = new GenericBeanDefinition();
                beanDefinition.setBeanClassName(clazz.getName());
                beanDefinition.setScope("singleton");
                beanFactory.registerBeanDefinition(beanId, beanDefinition);
                beanFactory.registerSingleton(beanId, object);
            }
        }

        @Override
        public <T> T get(Class<?> clazz) {
            String beanId = getBeanId(clazz);
            return get(clazz, beanId);
        }

        @Override
        public <T> T get(Class<?> clazz, String id) {
            String beanId = (id != null ) ? id : getBeanId(clazz);
            return (T) beanFactory.getSingleton(beanId);
        }

        private String getBeanId(Class<?> clazz){
            Controller controller = clazz.getAnnotation(Controller.class);
            Service service = clazz.getAnnotation(Service.class);
            Repository repository = clazz.getAnnotation(Repository.class);
            Component component = clazz.getAnnotation(Component.class);
            String id = (controller != null && !"".equals(controller.value())) ? controller.value() : (
                        (service != null && !"".equals(service.value())) ? service.value() : (
                                (repository != null && !"".equals(repository.value())) ? repository.value() : (
                                        (component != null && !"".equals(component.value())) ? component.value() : null

                                )
                        )
            );
            String name = clazz.getSimpleName();
            String beanName = name.toLowerCase().substring(0, 1) + name.substring(1);
            return (id == null) ? beanName : id;
        }
    }


实现注入器的接口


    public class SpringCacheInjector implements InjectorInterface {

        private DefaultListableBeanFactory beanFactory;

        public SpringCacheInjector(ConfigurableWebApplicationContext applicationContext) {
            this.beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        }

        @Override
        public <T> void doInject(T bean) {
            beanFactory.autowireBean(bean);
        }
    }


实现缓存的接口


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

写CacheConfig的工厂方法，提供spring的xml配置中使用

    public class CacheConfigFactory {

        private int defaultExpiredSeconds = 60 * 60 * 24;

        private int schedulerCorePoolSize = 64;

        private int retryRegisterMSeconds = 1000 * 2;

        private int lockSegments = 32;

        private boolean lockIsFair = false;

        private boolean avoidServerOverload = false;


        public CacheConfig create(){
            CacheConfig config = new CacheConfig.Builder()
                    .defaultExpiredSeconds(defaultExpiredSeconds)
                    .schedulerCorePoolSize(schedulerCorePoolSize)
                    .retryRegisterMSeconds(retryRegisterMSeconds)
                    .lockSegments(lockSegments)
                    .lockIsFair(lockIsFair)
                    .avoidServerOverload(avoidServerOverload)
                    .build();
            return config;
        }

        public void setDefaultExpiredSeconds(int defaultExpiredSeconds) {
            this.defaultExpiredSeconds = defaultExpiredSeconds;
        }

        public void setSchedulerCorePoolSize(int schedulerCorePoolSize) {
            this.schedulerCorePoolSize = schedulerCorePoolSize;
        }

        public void setRetryRegisterMSeconds(int retryRegisterMSeconds) {
            this.retryRegisterMSeconds = retryRegisterMSeconds;
        }

        public void setLockSegments(int lockSegments) {
            this.lockSegments = lockSegments;
        }

        public void setLockIsFair(boolean lockIsFair) {
            this.lockIsFair = lockIsFair;
        }

        public void setAvoidServerOverload(boolean avoidServerOverload) {
            this.avoidServerOverload = avoidServerOverload;
        }
    }

继承DispatcherServlet，重写init方法

    public class DispatcherServletWithEasyCache extends DispatcherServlet {

        @Override
        public void init(ServletConfig servletConfig) throws ServletException {
            super.init(servletConfig);

            ConfigurableWebApplicationContext applicationContext = (ConfigurableWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(servletConfig.getServletContext());
            SpringCacheInjector cacheInjector = new SpringCacheInjector(applicationContext);
            SpringCacheBeanFactory cacheBeanFactory = new SpringCacheBeanFactory(applicationContext);
            CacheInterceptor cacheInterceptor = new CacheInterceptor(cacheBeanFactory, cacheInjector);
            cacheInterceptor.run("com.yy.ecache");
        }
    }

web.xml配置

    <servlet>
        <servlet-name>ecache</servlet-name>
        <servlet-class>com.yy.ecache.DispatcherServletWithEasyCache</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value></param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>ecache</servlet-name>
        <url-pattern>*.action</url-pattern>
    </servlet-mapping>

spring xml配置

    <!-- EasyCache缓存配置 -->
        <bean id="cacheConfigFactory" class="com.yy.ecache.CacheConfigFactory">
            <property name="defaultExpiredSeconds" value="300"></property>
            <property name="schedulerCorePoolSize" value="64"></property>
            <property name="retryRegisterMSeconds" value="1000"></property>
            <property name="avoidServerOverload" value="false"></property>
            <property name="lockIsFair" value="false"></property>
            <property name="lockSegments" value="128"></property>
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
        <bean id="redisCache" class="com.yy.ecache.RedisCache">
            <constructor-arg ref="redisConfig"></constructor-arg>
            <constructor-arg name="ip" value="127.0.0.1"></constructor-arg>
            <constructor-arg name="port" value="6380"></constructor-arg>
            <constructor-arg name="timeout" value="2000"></constructor-arg>
        </bean>
        <bean id="remoteCache" class="com.ecache.RemoteCache">
            <constructor-arg ref="cacheConfig"></constructor-arg>
            <constructor-arg ref="redisCache"></constructor-arg>
        </bean>
