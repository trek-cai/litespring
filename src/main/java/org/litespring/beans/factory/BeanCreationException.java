package org.litespring.beans.factory;

import org.litespring.beans.BeanException;

public class BeanCreationException extends BeanException {

    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
