package com.yy.ecache;

import com.ecache.bean.InjectorInterface;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * @author 谢俊权
 * @create 2016/8/1 17:15
 */
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
