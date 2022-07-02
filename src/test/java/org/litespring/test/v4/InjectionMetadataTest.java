package org.litespring.test.v4;

import org.junit.Assert;
import org.junit.Test;
import org.litespring.beans.factory.annotation.AutowiredFieldElement;
import org.litespring.beans.factory.support.DefaultBeanFactory;
import org.litespring.beans.factory.xml.XmlBeanDefinitionReader;
import org.litespring.beans.factory.annotation.InjectionElement;
import org.litespring.beans.factory.annotation.InjectionMetadata;
import org.litespring.core.io.ClassPathResource;
import org.litespring.core.io.Resource;
import org.litespring.dao.v4.AccountDao;
import org.litespring.dao.v4.ItemDao;
import org.litespring.service.v4.PetStoreService;

import java.lang.reflect.Field;
import java.util.LinkedList;

/**
 * 抽象出抽象类InjectionElement用于表示需要注入的依赖项，持有Member、AutowireCapableBeanFactory实例，具有抽象方法inject
 * AutowiredFieldElement为InjectionElement的实现。
 * 抽象出InjectionMetadata用于表示类元数据的注入过程，持有注入的目标类targetClass，以及需要注入的依赖项列表injectionElements。
 */
public class InjectionMetadataTest {

    @Test
    public void testInjection() throws Exception{
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        Resource resource = new ClassPathResource("petstore-v4.xml");
        XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(beanFactory);
        reader.loadBeanDefinitions(resource);

        Class<?> targetClass = PetStoreService.class;
        LinkedList<InjectionElement> injectionElements = new LinkedList<InjectionElement>();
        InjectionMetadata injectionMetadata = null;

        {
            Field field = PetStoreService.class.getDeclaredField("accountDao");
            InjectionElement injectionElement = new AutowiredFieldElement(field, true, beanFactory);
            injectionElements.add(injectionElement);
        }

        {
            Field field = PetStoreService.class.getDeclaredField("itemDao");
            InjectionElement injectionElement = new AutowiredFieldElement(field, true, beanFactory);
            injectionElements.add(injectionElement);
        }
        injectionMetadata = new InjectionMetadata(targetClass, injectionElements);

        PetStoreService petStoreService = new PetStoreService();
        injectionMetadata.inject(petStoreService);

        Assert.assertTrue(petStoreService.getAccountDao() instanceof AccountDao);
        Assert.assertTrue(petStoreService.getItemDao() instanceof ItemDao);
    }
}
