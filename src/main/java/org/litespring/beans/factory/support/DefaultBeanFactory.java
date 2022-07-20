package org.litespring.beans.factory.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.litespring.beans.*;
import org.litespring.beans.factory.BeanCreationException;
import org.litespring.beans.factory.BeanFactoryAware;
import org.litespring.beans.factory.NoSuchBeanDefinitionException;
import org.litespring.beans.factory.config.BeanPostProcessor;
import org.litespring.beans.factory.config.ConfigurableBeanFactory;
import org.litespring.beans.factory.config.DependencyDescriptor;
import org.litespring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.litespring.util.ClassUtils;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanFactory extends AbstractBeanFactory
        implements BeanDefinitionRegistry {
    private static final Log logger = LogFactory.getLog(DefaultBeanFactory.class);

    private List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();
    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();
    private ClassLoader beanClassLoader;

    public DefaultBeanFactory() {
    }

    public void registerBeanDefinition(String beanID, BeanDefinition bd) {
        beanDefinitionMap.put(beanID, bd);
    }

    public BeanDefinition getBeanDefinition(String beanID) {
        return beanDefinitionMap.get(beanID);
    }

    public Object getBean(String beanID) {
        BeanDefinition bd = this.getBeanDefinition(beanID);
        if(bd == null) {
            throw new BeanCreationException("Bean definition does not exists.");
        }
        if(bd.isSingleton()) {
            Object bean = this.getSingleton(beanID);
            if(bean == null) {
                bean = createBean(bd);
                this.registrySingleton(beanID, bean);
            }
            return bean;
        }
        return createBean(bd);
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        BeanDefinition bd = this.getBeanDefinition(name);
        if(bd == null){
            throw new NoSuchBeanDefinitionException(name);
        }
        resolveBeanClass(bd);
        return bd.getBeanClass();
    }

    @Override
    public List<Object> getBeansByType(Class<?> type) {
        List<Object> result = new ArrayList<Object>();
        List<String> beanIds = getBeanIDsByType(type);
        for(String id : beanIds) {
            result.add(this.getBean(id));
        }
        return result;
    }

    private List<String> getBeanIDsByType(Class<?> type) {
        List<String> result = new ArrayList<String>();
        for(String beanName : this.beanDefinitionMap.keySet()) {
            Class<?> beanClass = null;
            try{
                beanClass = this.getType(beanName);
            }catch(Exception e){
                logger.warn("can't load class for bean :"+beanName+", skip it.");
                continue;
            }

            if((beanClass != null) && type.isAssignableFrom(beanClass)){
                result.add(beanName);
            }
        }
        return result;
    }


    protected Object createBean(BeanDefinition bd) {
        //创建实例
        Object bean = instantiateBean(bd);
        //设置属性
        populateBean(bd, bean);
        // 创建代理对象
        bean = initializeBean(bean, bd);
        return bean;
    }

    private Object instantiateBean(BeanDefinition bd) {
        if(bd.hasConstructorArgumentValues()) {
            ConstructorResolver constructorResolver = new ConstructorResolver(this);
            return constructorResolver.autowireConstructor(bd);
        } else {
            ClassLoader cl = this.getBeanClassLoader();
            String beanClassName = bd.getBeanClassName();
            Class<?> clz = null;
            try {
                clz = cl.loadClass(beanClassName);
                return clz.newInstance();
            } catch (Exception e) {
                throw new BeanCreationException("create bean for "+ beanClassName +" failed",e);
            }
        }
    }

    private void populateBean(BeanDefinition bd, Object bean) {
        for(BeanPostProcessor processor: this.beanPostProcessors) {
            if(processor instanceof InstantiationAwareBeanPostProcessor){
                ((InstantiationAwareBeanPostProcessor) processor).postProcessPropertyValues(bean, bd.getID());
            }
        }

        List<PropertyValue> pvs = bd.getPropertyValues();
        if (pvs == null || pvs.isEmpty()) {
            return;
        }
        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this);
        TypeConverter converter = new SimpleTypeConverter();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

            for(PropertyValue pv: pvs) {
                String propertyName = pv.getName();
                Object originalValue = pv.getValue();
                for(PropertyDescriptor pd:pds) {
                    if(pd.getName().equals(propertyName)) {
                        Object resolvedValue = valueResolver.resolveValueIfNecessary(originalValue);
                        Object convertedValue = converter.convertIfNecessary(resolvedValue, pd.getPropertyType());
                        pd.getWriteMethod().invoke(bean, convertedValue);
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Object initializeBean(Object bean, BeanDefinition bd) {
        invokeAwareMethods(bean);
        //Todo，调用Bean的init方法，暂不实现
        // 创建动态代理对象
        if(!bd.isSynthetic()) {
            return applyBeanPostProcessorsAfterInitialization(bean ,bd.getID());
        }
        return bean;
    }

    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException {

        Object result = existingBean;
        for (BeanPostProcessor beanProcessor : getBeanPostProcessors()) {
            result = beanProcessor.afterInitialization(result, beanName);
            if (result == null) {
                return result;
            }
        }
        return result;
    }

    private void invokeAwareMethods(final Object bean) {
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
    }

    public void setBeanClassLoader(ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader;
    }

    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader != null ? this.beanClassLoader : ClassUtils.getDefaultClassLoader();
    }

    public void addBeanPostProcessor(BeanPostProcessor postProcessor) {
        this.beanPostProcessors.add(postProcessor);
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    public Object resolveDependency(DependencyDescriptor descriptor) {
        Class<?> typeToMatch = descriptor.getDependencyType();
        for(BeanDefinition bd:this.beanDefinitionMap.values()) {
            resolveBeanClass(bd);
            if(typeToMatch.isAssignableFrom(bd.getBeanClass())) {
                return this.getBean(bd.getID());
            }
        }
        return null;
    }

    private void resolveBeanClass(BeanDefinition bd) {
        if(!bd.hasBeanClass()) {
            try {
                bd.resolveBeanClass(this.getBeanClassLoader());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("can't load class:" + bd.getBeanClassName());
            }
        }
    }
}
