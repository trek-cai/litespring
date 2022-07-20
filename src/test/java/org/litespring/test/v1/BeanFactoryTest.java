package org.litespring.test.v1;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.litespring.beans.BeanDefinition;
import org.litespring.beans.factory.BeanCreationException;
import org.litespring.beans.factory.BeanDefinitionStoreException;
import org.litespring.beans.factory.support.DefaultBeanFactory;
import org.litespring.beans.factory.xml.XmlBeanDefinitionReader;
import org.litespring.core.io.ClassPathResource;
import org.litespring.core.io.Resource;
import org.litespring.service.v1.PetStoreService;

public class BeanFactoryTest {

    private DefaultBeanFactory factory = null;
    private XmlBeanDefinitionReader reader = null;

    @Before
    public void setUp() {
        factory = new DefaultBeanFactory();
        reader = new XmlBeanDefinitionReader(factory);
    }

    @Test
    public void testGetBean() {
        Resource resource = new ClassPathResource("petstore-v1.xml");
        reader.loadBeanDefinitions(resource);
        BeanDefinition beanDefinition = factory.getBeanDefinition("petStore");

        Assert.assertTrue(beanDefinition.isSingleton());
        Assert.assertFalse(beanDefinition.isPrototype());
        Assert.assertEquals(BeanDefinition.SCOPE_DEFAULT, beanDefinition.getScope());

        Assert.assertEquals(beanDefinition.getBeanClassName(), "org.litespring.service.v1.PetStoreService");

        PetStoreService petStore = (PetStoreService) factory.getBean("petStore");
        Assert.assertNotNull(petStore);

        PetStoreService petStore1 = (PetStoreService) factory.getBean("petStore");
        Assert.assertTrue(petStore == petStore1);
    }

    @Test
    public void testGetBeanBySingleton() {
        Resource resource = new ClassPathResource("petstore-v1.xml");
        reader.loadBeanDefinitions(resource);
        BeanDefinition beanDefinition = factory.getBeanDefinition("singlePetStore");

        Assert.assertTrue(beanDefinition.isSingleton());
        Assert.assertFalse(beanDefinition.isPrototype());
        Assert.assertEquals(BeanDefinition.SCOPE_SINGLETON, beanDefinition.getScope());

        Assert.assertEquals(beanDefinition.getBeanClassName(), "org.litespring.service.v1.PetStoreService");

        PetStoreService petStore = (PetStoreService) factory.getBean("singlePetStore");
        Assert.assertNotNull(petStore);

        PetStoreService petStore1 = (PetStoreService) factory.getBean("singlePetStore");
        Assert.assertTrue(petStore == petStore1);
    }

    @Test
    public void testGetBeanByPrototype() {
        Resource resource = new ClassPathResource("petstore-v1.xml");
        reader.loadBeanDefinitions(resource);
        BeanDefinition beanDefinition = factory.getBeanDefinition("prototypePetStore");

        Assert.assertFalse(beanDefinition.isSingleton());
        Assert.assertTrue(beanDefinition.isPrototype());
        Assert.assertEquals(BeanDefinition.SCOPE_PROTOTYPE, beanDefinition.getScope());

        Assert.assertEquals(beanDefinition.getBeanClassName(), "org.litespring.service.v1.PetStoreService");

        PetStoreService petStore = (PetStoreService) factory.getBean("prototypePetStore");
        Assert.assertNotNull(petStore);

        PetStoreService petStore1 = (PetStoreService) factory.getBean("prototypePetStore");
        Assert.assertTrue(petStore != petStore1);
    }

    @Test
    public void testInvalidBean() {
        Resource resource = new ClassPathResource("petstore-v1.xml");
        reader.loadBeanDefinitions(resource);
        try {
            factory.getBean("invalidBean");
        } catch (BeanCreationException e) {
            return;
        }
        Assert.fail();
    }

    @Test
    public void testInvalidXml() {
        try {
            Resource resource = new ClassPathResource("petstore-v11.xml");
            reader.loadBeanDefinitions(resource);
        } catch (BeanDefinitionStoreException e) {
            return;
        }
        Assert.fail();
    }
}
