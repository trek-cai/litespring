package org.litespring.aop.aspectj;

import org.litespring.aop.Advice;
import org.litespring.aop.Pointcut;
import org.litespring.aop.config.AspectInstanceFactory;

import java.lang.reflect.Method;

public abstract class AbstractAspectJAdvice implements Advice {
    private AspectJExpressionPointcut pointcut;
    private Method adviceMethod;
    private AspectInstanceFactory adviceObjectFactory;

    protected AbstractAspectJAdvice(AspectJExpressionPointcut pointcut, Method adviceMethod, AspectInstanceFactory adviceObjectFactory) {
        this.pointcut = pointcut;
        this.adviceMethod = adviceMethod;
        this.adviceObjectFactory = adviceObjectFactory;
    }

    public void invokeAdviceMethod() throws Throwable {
        adviceMethod.invoke(adviceObjectFactory.getAspectInstance());
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public Object getAdviceInstance() throws Exception {
        return this.adviceObjectFactory.getAspectInstance();
    }
}
