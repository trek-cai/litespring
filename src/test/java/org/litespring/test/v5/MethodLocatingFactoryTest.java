package org.litespring.test.v5;

import org.junit.Assert;
import org.junit.Test;
import org.litespring.aop.config.MethodLocatingFactory;
import org.litespring.beans.factory.support.DefaultBeanFactory;
import org.litespring.beans.factory.xml.XmlBeanDefinitionReader;
import org.litespring.core.io.ClassPathResource;
import org.litespring.core.io.Resource;
import org.litespring.tx.TransactionManager;

import java.lang.reflect.Method;

/**
 * 抽象出MethodLocatingFactory用于根据Bean id和目标方法名定位到切面方法。具体过程为：根据bean id从beanFactory获取到相应的BeanDefinition，
 * 从BeanDefinition里获取相应Class，再获取Class的所有方法与目标方法进行匹配。
 */
public class MethodLocatingFactoryTest {

    @Test
    public void testgetMethod() throws Exception {
        DefaultBeanFactory factory = new DefaultBeanFactory();
        Resource resource = new ClassPathResource("petstore-v5.xml");
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
        reader.loadBeanDefinitions(resource);

        MethodLocatingFactory mlf = new MethodLocatingFactory();
        mlf.setTargetBeanName("tx");
        mlf.setMethodName("start");
        mlf.setBeanFactory(factory);

        Method method = mlf.getObject();

        Assert.assertEquals(TransactionManager.class, method.getDeclaringClass());
        Assert.assertEquals(TransactionManager.class.getMethod("start"), method);
    }
}
