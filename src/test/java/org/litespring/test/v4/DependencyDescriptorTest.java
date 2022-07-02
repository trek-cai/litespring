package org.litespring.test.v4;

import org.junit.Assert;
import org.junit.Test;
import org.litespring.beans.factory.support.DefaultBeanFactory;
import org.litespring.beans.factory.xml.XmlBeanDefinitionReader;
import org.litespring.core.io.ClassPathResource;
import org.litespring.core.io.Resource;
import org.litespring.beans.factory.config.DependencyDescriptor;

import org.litespring.dao.v4.AccountDao;
import org.litespring.service.v4.PetStoreService;

import java.lang.reflect.Field;

/**
 * 抽象出DependencyDescriptor用于描述注入对象的依赖项（Field、Method、required属性）
 * 新增AutowireCapableBeanFactory接口，resolveDependency方法使beanFactory具有解析DependencyDescriptor的能力
 * AutowireCapableBeanFactory继承于BeanFactory，ConfigurableBeanFactory继承于AutowireCapableBeanFactory
 * 实现方式为：在解析类元数据时将其依赖封装成DependencyDescriptor，然后使用resolveDependency解析成具体的bean实例。
 */
public class DependencyDescriptorTest {

    @Test
    public void testResolveDependency() throws Exception{
        DefaultBeanFactory factory = new DefaultBeanFactory();
        Resource resource = new ClassPathResource("petstore-v4.xml");
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(factory);
        xmlReader.loadBeanDefinitions(resource);

        Field field = PetStoreService.class.getDeclaredField("accountDao");
        DependencyDescriptor descriptor = new DependencyDescriptor(field, true);
        Object o = factory.resolveDependency(descriptor);
        Assert.assertTrue(o instanceof AccountDao);

    }
}
