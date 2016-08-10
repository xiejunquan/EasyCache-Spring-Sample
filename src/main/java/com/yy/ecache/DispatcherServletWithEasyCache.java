package com.yy.ecache;

import com.ecache.proxy.CacheInterceptor;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

/**
 * @author 谢俊权
 * @create 2016/8/8 16:34
 */
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
