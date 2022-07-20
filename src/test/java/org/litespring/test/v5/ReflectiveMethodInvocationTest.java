package org.litespring.test.v5;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.litespring.aop.aspectj.AspectJAfterReturningAdvice;
import org.litespring.aop.aspectj.AspectJAfterThrowingAdvice;
import org.litespring.aop.aspectj.AspectJBeforeAdvice;
import org.litespring.aop.config.AspectInstanceFactory;
import org.litespring.aop.framework.ReflectiveMethodInvocation;
import org.litespring.service.v5.PetStoreService;
import org.litespring.tx.TransactionManager;
import org.litespring.util.MessageTracker;

import java.util.ArrayList;
import java.util.List;

/**
 * ReflectiveMethodInvocation接收一个拦截器列表，对传入的业务方法在切面处进行拦截，比如AspectJBeforeAdvice为在业务方法前调用切面方法，
 * AspectJAfterReturningAdvice在业务方法返回后调用切面方法，AspectJAfterThrowingAdvice在业务方法抛出异常后调用切面方法。
 * 抽象出Advice用于表示切面或拦截器，继承于MethodInterceptor，AbstractAspectJAdvice封装了调用切面方法的公共部分，
 * 即接收切面对象，切面方法和切入点，使用切面对象调用切面方法。而AspectJxxxAdvice等具体实现就是切面方法的不同调用时机。
 *
 */
public class ReflectiveMethodInvocationTest extends AbstractV5Test {

    private AspectJBeforeAdvice beforeAdvice;
    private AspectJAfterReturningAdvice afterAdvice;
    private AspectJAfterThrowingAdvice afterThrowingAdvice;
    private PetStoreService petStore;

    @Before
    public void setUp() throws Exception {
        petStore = new PetStoreService();
        AspectInstanceFactory adviceObjectFactory = getAspectInstanceFactory("tx");
        adviceObjectFactory.setBeanFactory(this.getBeanFactory("petstore-v5.xml"));

        MessageTracker.clearMsgs();
        beforeAdvice = new AspectJBeforeAdvice(null, TransactionManager.class.getMethod("start"), adviceObjectFactory);
        afterAdvice = new AspectJAfterReturningAdvice(null, TransactionManager.class.getMethod("commit"), adviceObjectFactory);
        afterThrowingAdvice = new AspectJAfterThrowingAdvice(null, TransactionManager.class.getMethod("rollback"), adviceObjectFactory);
    }

    @Test
    public void testMethodInvocation() throws Throwable {
        List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
        interceptors.add(beforeAdvice);
        interceptors.add(afterAdvice);

        ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStore,
                PetStoreService.class.getMethod("placeOrder"), new Object[0], interceptors);

        mi.proceed();

        List<String> msgs = MessageTracker.getMsgs();
        Assert.assertEquals(3, msgs.size());
        Assert.assertEquals("start tx", msgs.get(0));
        Assert.assertEquals("place order", msgs.get(1));
        Assert.assertEquals("commit tx", msgs.get(2));
    }

    @Test
    public void testMethodInvocationSeq() throws Throwable {
        List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
        interceptors.add(afterAdvice);
        interceptors.add(beforeAdvice); // 拦截器的加入顺序不影响具体的调用顺序

        ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStore,
                PetStoreService.class.getMethod("placeOrder"), new Object[0], interceptors);

        mi.proceed();

        List<String> msgs = MessageTracker.getMsgs();
        Assert.assertEquals(3, msgs.size());
        Assert.assertEquals("start tx", msgs.get(0));
        Assert.assertEquals("place order", msgs.get(1));
        Assert.assertEquals("commit tx", msgs.get(2));
    }

    @Test
    public void testMethodInvocationThrowing() throws Throwable {
        List<MethodInterceptor> interceptors = new ArrayList<MethodInterceptor>();
        interceptors.add(beforeAdvice);
        interceptors.add(afterThrowingAdvice);

        ReflectiveMethodInvocation mi = new ReflectiveMethodInvocation(petStore,
                PetStoreService.class.getMethod("placeOrderWithException"), new Object[0], interceptors);

        try {
            mi.proceed();
        } catch (Throwable e) {
            List<String> msgs = MessageTracker.getMsgs();
            Assert.assertEquals(2, msgs.size());
            Assert.assertEquals("start tx", msgs.get(0));
            Assert.assertEquals("rollback tx", msgs.get(1));
            return;
        }

        Assert.fail("No Exception thrown");
    }
}
