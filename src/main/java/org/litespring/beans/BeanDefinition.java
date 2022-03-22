package org.litespring.beans;

public interface BeanDefinition {
    String getBeanClassName();

    Object getBean(String beanName);
}
