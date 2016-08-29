package com.yy.ecache;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * @author 谢俊权
 * @create 2016/8/29 11:10
 */
public class EcacheAnnotationBeanNameGennerator extends AnnotationBeanNameGenerator{
    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        return definition.getBeanClassName();
    }
}
