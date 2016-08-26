package com.yy.ecache;

import com.ecache.proxy.CacheInterceptor;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;

/**
 * @author 谢俊权
 * @create 2016/8/8 16:34
 */
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
