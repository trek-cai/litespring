package org.litespring.aop.framework;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

public class ReflectiveMethodInvocation implements MethodInvocation {

    protected final Method targetMethod;
    protected final Object targetObject;

    protected Object[] arguments;

    protected final List<MethodInterceptor> interceptors;

    private int currentInterceptorIndex = -1;

    public ReflectiveMethodInvocation(Object targetObject, Method targetMethod, Object[] arguments, List<MethodInterceptor> interceptors) {
        this.targetMethod = targetMethod;
        this.targetObject = targetObject;
        this.arguments = arguments;
        this.interceptors = interceptors;
    }

    public Object proceed() throws Throwable  {
        if(currentInterceptorIndex == interceptors.size() - 1) {
            return this.invokeJoinpoint();
        }
        this.currentInterceptorIndex++;
        MethodInterceptor interceptor = interceptors.get(currentInterceptorIndex);
        return interceptor.invoke(this);
    }

    public final Object getThis() {
        return this.targetObject;
    }


    /**
     * Return the method invoked on the proxied interface.
     * May or may not correspond with a method invoked on an underlying
     * implementation of that interface.
     */
    public final Method getMethod() {
        return this.targetMethod;
    }

    public final Object[] getArguments() {
        return (this.arguments != null ? this.arguments : new Object[0]);
    }

    protected Object invokeJoinpoint() throws Throwable {
        return this.targetMethod.invoke(this.targetObject, this.arguments);
    }

    public AccessibleObject getStaticPart() {
        return this.targetMethod;
    }
}
