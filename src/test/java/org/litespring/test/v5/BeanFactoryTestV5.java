package org.litespring.test.v5;

import org.junit.Assert;
import org.junit.Test;
import org.litespring.aop.Advice;
import org.litespring.aop.aspectj.AspectJAfterReturningAdvice;
import org.litespring.aop.aspectj.AspectJAfterThrowingAdvice;
import org.litespring.aop.aspectj.AspectJBeforeAdvice;
import org.litespring.beans.factory.BeanFactory;
import org.litespring.tx.TransactionManager;

import java.util.List;

public class BeanFactoryTestV5 extends AbstractV5Test {

    static String expectedExpression = "execution(* org.litespring.service.v5.*.placeOrder(..))";

    @Test
    public void testGetBeanByType() throws Exception {
        BeanFactory factory = this.getBeanFactory("petstore-v5.xml");
        List<Object> advices = factory.getBeansByType(Advice.class);
        Assert.assertEquals(3, advices.size());
        {
            AspectJBeforeAdvice beforeAdvice = (AspectJBeforeAdvice) this.getAdvice(AspectJBeforeAdvice.class, advices);
            Assert.assertEquals(expectedExpression, beforeAdvice.getPointcut().getExpression());
            Assert.assertEquals(TransactionManager.class.getMethod("start"), beforeAdvice.getAdviceMethod());
            Assert.assertEquals(TransactionManager.class, beforeAdvice.getAdviceInstance().getClass());
        }

        {
            AspectJAfterReturningAdvice afterReturningAdvice = (AspectJAfterReturningAdvice) this.getAdvice(AspectJAfterReturningAdvice.class, advices);
            Assert.assertEquals(expectedExpression, afterReturningAdvice.getPointcut().getExpression());
            Assert.assertEquals(TransactionManager.class.getMethod("commit"), afterReturningAdvice.getAdviceMethod());
            Assert.assertEquals(TransactionManager.class, afterReturningAdvice.getAdviceInstance().getClass());
        }

        {
            AspectJAfterThrowingAdvice afterThrowingAdvice = (AspectJAfterThrowingAdvice) this.getAdvice(AspectJAfterThrowingAdvice.class, advices);
            Assert.assertEquals(expectedExpression, afterThrowingAdvice.getPointcut().getExpression());
            Assert.assertEquals(TransactionManager.class.getMethod("rollback"), afterThrowingAdvice.getAdviceMethod());
            Assert.assertEquals(TransactionManager.class, afterThrowingAdvice.getAdviceInstance().getClass());
        }
    }

    public Object getAdvice(Class<?> clazz, List<Object> advices) {
        for(Object obj : advices) {
            if(obj.getClass().equals(clazz)) {
                return obj;
            }
        }
        return null;
    }
}
