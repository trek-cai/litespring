package org.litespring.beans.factory.config;

import org.litespring.beans.BeansException;

public interface InstantiationAwareBeanPostProcessor extends  BeanPostProcessor {

    Object beforeInstantiation(Class<?> beanClass, String beanName) throws BeansException;  // bean实例化之前的操作
    boolean afterInstantiation(Object bean, String beanName) throws BeansException;         // bean实例化之后的操作
    void postProcessPropertyValues(Object bean, String beanName) throws BeansException;
}
