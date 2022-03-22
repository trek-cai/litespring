package org.litespring.test.v1;
import org.junit.Assert;
import org.junit.Test;
import org.litespring.beans.BeanDefinition;
import org.litespring.beans.factory.BeanFactory;
import org.litespring.beans.support.DefaultBeanFactory;
import org.litespring.service.v1.PetStoreService;

public class BeanFactoryTest {

    @Test
    public void testGetBean() {
        BeanFactory beanFactory = new DefaultBeanFactory("petStore.xml");
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("petStore");
        Assert.assertEquals(beanDefinition.getBeanClassName(), "org.litespring.service.v1.PetStoreService");

        PetStoreService petStore = (PetStoreService) beanDefinition.getBean("petStore");
        Assert.assertNotNull(petStore);
    }
}
