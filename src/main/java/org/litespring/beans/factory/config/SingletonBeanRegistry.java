package org.litespring.beans.factory.config;

public interface SingletonBeanRegistry {
    void registrySingleton(String beanID, Object singleton);
    Object getSingleton(String beanID);
}
