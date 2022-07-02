package org.litespring.beans.factory.config;

import org.litespring.beans.BeansException;

public interface BeanPostProcessor {

    Object beforeInitialization(Object bean, String beanName) throws BeansException;    // bean初始化之前做的操作

    Object afterInitialization(Object bean, String beanName) throws BeansException;     // bean初始化之后做的操作
}
