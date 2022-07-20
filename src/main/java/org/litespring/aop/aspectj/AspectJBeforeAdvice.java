package org.litespring.aop.aspectj;

import org.aopalliance.intercept.MethodInvocation;
import org.litespring.aop.Pointcut;
import org.litespring.aop.config.AspectInstanceFactory;

import java.lang.reflect.Method;

public class AspectJBeforeAdvice extends AbstractAspectJAdvice {

    public AspectJBeforeAdvice(AspectJExpressionPointcut pointcut, Method adviceMethod, AspectInstanceFactory adviceObjectFactory) {
        super(pointcut, adviceMethod, adviceObjectFactory);
    }

    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        this.invokeAdviceMethod();
        return mi.proceed();
    }
}
