package com.yy.ecache;

import com.ecache.bean.BeanFactoryInterface;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * @author 谢俊权
 * @create 2016/8/1 17:13
 */
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
        T bean = (T) beanFactory.getSingleton(beanId);
        return (bean == null) ? (T)beanFactory.getBean(clazz) : bean;
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
