package org.litespring.beans.factory;

import org.litespring.beans.BeanException;

public class BeanDefinitionStoreException extends BeanException {
    public BeanDefinitionStoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanDefinitionStoreException(String message) {
        super(message);
    }
}
