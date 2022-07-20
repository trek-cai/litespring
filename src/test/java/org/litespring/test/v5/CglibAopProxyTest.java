package org.litespring.test.v5;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.litespring.aop.Pointcut;
import org.litespring.aop.aspectj.AspectJAfterReturningAdvice;
import org.litespring.aop.aspectj.AspectJAfterThrowingAdvice;
import org.litespring.aop.aspectj.AspectJBeforeAdvice;
import org.litespring.aop.aspectj.AspectJExpressionPointcut;
import org.litespring.aop.config.AspectInstanceFactory;
import org.litespring.aop.framework.AopConfig;
import org.litespring.aop.framework.AopConfigSupport;
import org.litespring.aop.framework.CglibProxyFactory;
import org.litespring.beans.factory.BeanFactory;
import org.litespring.service.v5.PetStoreService;
import org.litespring.tx.TransactionManager;
import org.litespring.util.MessageTracker;

import java.util.List;

public class CglibAopProxyTest extends AbstractV5Test {

    private AspectJBeforeAdvice beforeAdvice;
    private AspectJAfterReturningAdvice afterAdvice;
    private AspectJAfterThrowingAdvice afterThrowingAdvice;

    private PetStoreService petStore;
    private AspectJExpressionPointcut pc;

    @Before
    public void setUp() throws Exception {
        MessageTracker.clearMsgs();
        petStore = new PetStoreService();
        BeanFactory beanFactory = this.getBeanFactory("petstore-v5.xml");
        AspectInstanceFactory adviceObjectFactory = this.getAspectInstanceFactory("tx");
        adviceObjectFactory.setBeanFactory(beanFactory);

        pc = new AspectJExpressionPointcut();
        pc.setExpression("execution (* org.litespring.service.v5.*.placeOrder(..))");

        beforeAdvice = new AspectJBeforeAdvice(pc, TransactionManager.class.getMethod("start"), adviceObjectFactory);
        afterAdvice = new AspectJAfterReturningAdvice(pc, TransactionManager.class.getMethod("commit"), adviceObjectFactory);
        afterThrowingAdvice = new AspectJAfterThrowingAdvice(pc, TransactionManager.class.getMethod("rollback"), adviceObjectFactory);
    }

    @Test
    public void testGetProxy() {
        AopConfig config = new AopConfigSupport();
        config.addAdvice(beforeAdvice);
        config.addAdvice(afterAdvice);
        config.setTargetObject(petStore);

        CglibProxyFactory proxyFactory = new CglibProxyFactory(config);
        PetStoreService proxy = (PetStoreService) proxyFactory.getProxy();
        proxy.placeOrder();

        List<String> msgs = MessageTracker.getMsgs();
        Assert.assertEquals(3, msgs.size());
        Assert.assertEquals("start tx", msgs.get(0));
        Assert.assertEquals("place order", msgs.get(1));
        Assert.assertEquals("commit tx", msgs.get(2));
    }
}
