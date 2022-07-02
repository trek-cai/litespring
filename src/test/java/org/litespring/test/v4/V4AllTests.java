package org.litespring.test.v4;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.litespring.test.v3.ApplicationContextTestV3;
import org.litespring.test.v3.BeanDefinitionTestV3;
import org.litespring.test.v3.ConstructorResolverTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        PackageResourceLoaderTest.class,
        ClassReaderTest.class,
        MetadataReaderTest.class,
        ClassPathBeanDefinitionScannerTest.class,
        XmlBeanDefinitionReaderTest.class,
        DependencyDescriptorTest.class,
        InjectionMetadataTest.class,
        AutowiredAnnotationProcessorTest.class,
        ApplicationContextTest4.class
})
public class V4AllTests {
}
