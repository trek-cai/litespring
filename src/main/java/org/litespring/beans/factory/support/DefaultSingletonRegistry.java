package org.litespring.beans.factory.support;

import org.litespring.beans.factory.config.SingletonBeanRegistry;
import org.litespring.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultSingletonRegistry implements SingletonBeanRegistry {

    private Map<String, Object> singletonMap = new ConcurrentHashMap<String, Object>(64);

    public void registrySingleton(String beanID, Object singleton) {
        Assert.notNull(beanID, "'beanID' must not be null");
        Object oldObject = this.singletonMap.get(beanID);
        if (oldObject != null) {
            throw new IllegalStateException("Could not register object [" + singleton +
                    "] under bean name '" + beanID + "': there is already object [" + oldObject + "] bound");
        }
        singletonMap.put(beanID, singleton);
    }

    public Object getSingleton(String beanID) {
        return singletonMap.get(beanID);
    }
}
