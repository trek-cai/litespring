package org.litespring.beans.factory.support;

import org.litespring.beans.BeanDefinition;

public interface BeanDefinitionRegistry {

    void registerBeanDefinition(String beanID, BeanDefinition bd);
    BeanDefinition getBeanDefinition(String beanID);
}
