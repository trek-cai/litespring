package org.litespring.test.v5;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PointcutTest.class,
        MethodLocatingFactoryTest.class,
        ReflectiveMethodInvocationTest.class,
        CGLibTest.class,
        CglibAopProxyTest.class,
        BeanDefinitionTestV5.class,
        BeanFactoryTestV5.class,
        ApplicationContextTest5.class
})
public class V5AllTests {
}
