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
                return (id == null) ? clazz.getName() : id;
            }
    }

默认地, 在spring中beanName都是以简单类名命名的, 例如com.yy.ecache.PageService在spring的beanFactory中, 是以pageService来命名的. 所以, 如果在不同的包下有相同名称的类, 也是会报错的. 为了解决这个问题, 我们可以重写spring的命名定义类AnnotationBeanNameGenerator. 然后在扫描类配置中, 添加类命名的类. 具体看配置文件. 因为重写AnnotationBeanNameGenerator类时我使用了类全名作为类的命名, 所以当自定义id为null的时候, 直接返回clazz.getName();


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
继承ContextLoaderListener，重写contextInitialized方法

    public class ContextLoaderListenerWithEasyCache extends ContextLoaderListener {

        @Override
        public void contextInitialized(ServletContextEvent event) {
            super.contextInitialized(event);

            ConfigurableWebApplicationContext applicationContext = (ConfigurableWebApplicationContext) WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
            SpringCacheInjector cacheInjector = new SpringCacheInjector(applicationContext);
            SpringCacheBeanFactory cacheBeanFactory = new SpringCacheBeanFactory(applicationContext);
            CacheInterceptor cacheInterceptor = new CacheInterceptor(cacheBeanFactory, cacheInjector);
            cacheInterceptor.run("com.yy.ecache");
        }
    }

web.xml配置

    <context-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>WEB-INF/conf/applicationContext.xml</param-value>
        </context-param>

        <listener>
            <listener-class>com.yy.ecache.ContextLoaderListenerWithEasyCache</listener-class>
        </listener>

        <servlet>
            <servlet-name>ecache</servlet-name>
            <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
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

    <mvc:annotation-driven/>
    <context:component-scan name-generator="com.yy.ecache.EcacheAnnotationBeanNameGennerator"  base-package="com.yy.ecache.controller"/>
    <context:component-scan name-generator="com.yy.ecache.EcacheAnnotationBeanNameGennerator" base-package="com.yy.ecache.service"/>
    <context:component-scan name-generator="com.yy.ecache.EcacheAnnotationBeanNameGennerator" base-package="com.yy.ecache.dao"/>

    <bean class="org.springframework.web.servlet.view.ContentNegotiatingViewResolver">
        <property name="viewResolvers">
            <list>
                <bean class="org.springframework.web.servlet.view.BeanNameViewResolver"/>
                <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
                    <property name="prefix" value="/WEB-INF/jsp/"/>
                    <property name="suffix" value=".jsp"/>
                </bean>
            </list>
        </property>
        <property name="defaultViews">
            <list>
                <bean class="org.springframework.web.servlet.view.json.MappingJackson2JsonView"/>
            </list>
        </property>
    </bean>

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
