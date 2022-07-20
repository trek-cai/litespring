package org.litespring.test.v5;

import org.junit.Assert;
import org.junit.Test;
import org.litespring.beans.ConstructorArgument;
import org.litespring.aop.aspectj.AspectJBeforeAdvice;
import org.litespring.aop.aspectj.AspectJExpressionPointcut;
import org.litespring.aop.config.MethodLocatingFactory;
import org.litespring.beans.BeanDefinition;
import org.litespring.beans.PropertyValue;
import org.litespring.beans.factory.config.RuntimeBeanReference;
import org.litespring.beans.factory.support.DefaultBeanFactory;
import org.litespring.tx.TransactionManager;

import java.util.List;

public class BeanDefinitionTestV5 extends AbstractV5Test {

    @Test
    public void testAOPBean() {
        DefaultBeanFactory beanFactory = (DefaultBeanFactory) this.getBeanFactory("petstore-v5.xml");

        {
            BeanDefinition aspectBean = beanFactory.getBeanDefinition("tx");
            Assert.assertEquals(TransactionManager.class.getName(), aspectBean.getBeanClassName());
        }

        {
            BeanDefinition pcBean = beanFactory.getBeanDefinition("placeOrder");
            Assert.assertEquals(AspectJExpressionPointcut.class.getName(), pcBean.getBeanClassName());
            Assert.assertTrue(pcBean.isSynthetic());
            PropertyValue pv = pcBean.getPropertyValues().get(0);
            Assert.assertEquals("expression", pv.getName());
            Assert.assertEquals("execution(* org.litespring.service.v5.*.placeOrder(..))", pv.getValue());
        }

        //检查AspectJBeforeAdvice
        {
            String beforeAdviceBeanName = AspectJBeforeAdvice.class.getName() + "#0";
            BeanDefinition beforeAdviceBean = beanFactory.getBeanDefinition(beforeAdviceBeanName);
            Assert.assertTrue(beforeAdviceBean.isSynthetic());

            {
                Assert.assertEquals(AspectJBeforeAdvice.class.getName(), beforeAdviceBean.getBeanClassName());

                PropertyValue pv = beforeAdviceBean.getPropertyValues().get(0);
                Assert.assertEquals("aspectName", pv.getName());
                Assert.assertEquals("tx", pv.getValue());
            }

            {
                ConstructorArgument cArgs = beforeAdviceBean.getConstructorArgument();
                List<ConstructorArgument.ValueHolder> args = cArgs.getArgumentValues();
                Assert.assertEquals(3, args.size());
                RuntimeBeanReference pointcutRef = (RuntimeBeanReference) args.get(0).getValue();
                Assert.assertEquals("placeOrder", pointcutRef.getBeanName());

                BeanDefinition methodFactoryBean = (BeanDefinition) args.get(1).getValue();
                Assert.assertEquals(MethodLocatingFactory.class.getName(), methodFactoryBean.getBeanClassName());

                List<PropertyValue> pvs = methodFactoryBean.getPropertyValues();
                Assert.assertEquals("targetBeanName", pvs.get(0).getName());
                Assert.assertEquals("tx", pvs.get(0).getValue());
                Assert.assertEquals("methodName", pvs.get(1).getName());
                Assert.assertEquals("start", pvs.get(1).getValue());

                BeanDefinition aspInstanceFactoryBean = (BeanDefinition) args.get(2).getValue();
                PropertyValue pv = aspInstanceFactoryBean.getPropertyValues().get(0);
                Assert.assertEquals("aspectBeanName", pv.getName());
                Assert.assertEquals("tx", pv.getValue());
            }
        }

        // TODO 作业：检查另外两个Bean


    }
}
