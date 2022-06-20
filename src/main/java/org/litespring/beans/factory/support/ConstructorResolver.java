package org.litespring.beans.factory.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.litespring.ConstructorArgument;
import org.litespring.beans.BeanDefinition;
import org.litespring.beans.SimpleTypeConverter;
import org.litespring.beans.factory.BeanCreationException;
import org.litespring.beans.factory.BeanFactory;
import org.litespring.beans.factory.config.ConfigurableBeanFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.List;

public class ConstructorResolver {

    protected final Log logger = LogFactory.getLog(getClass());

    private final ConfigurableBeanFactory factory;

    public ConstructorResolver(ConfigurableBeanFactory factory) {
        this.factory = factory;
    }

    public Object autowireConstructor(final BeanDefinition bd) {
        Constructor<?> constructorToUse = null;
        Object[] argsToUse = null;
        Class<?> beanClass = null;
        try {
            beanClass = this.factory.getBeanClassLoader().loadClass(bd.getBeanClassName());
        } catch (ClassNotFoundException e) {
            throw new BeanCreationException( bd.getID(), "Instantiation of bean failed, can't resolve class", e);
        }

        BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this.factory);
        SimpleTypeConverter typeConverter = new SimpleTypeConverter();

        Constructor<?>[] constructors = beanClass.getConstructors();
        ConstructorArgument costructArgs =  bd.getConstructorArgument();
        for (int i = 0; i<constructors.length; i++) {
            Class<?>[] parameterTypes = constructors[i].getParameterTypes();
            if(parameterTypes.length != costructArgs.getArgumentCount()) {
                continue;
            }
            argsToUse = new Object[parameterTypes.length];

            boolean result = this.valuesMatchTypes(parameterTypes, costructArgs.getArgumentValues(), valueResolver,
                    typeConverter, argsToUse);
            if(result) {
                constructorToUse = constructors[i];
                break;
            }
        }


        //找不到一个合适的构造函数
        if(constructorToUse == null){
            throw new BeanCreationException( bd.getID(), "can't find a apporiate constructor");
        }

        try {
            return constructorToUse.newInstance(argsToUse);
        } catch (Exception e) {
            throw new BeanCreationException( bd.getID(), "can't find a create instance using "+constructorToUse);
        }
    }

    private boolean valuesMatchTypes(Class<?>[] parameterTypes, List<ConstructorArgument.ValueHolder> argumentValues,
                                     BeanDefinitionValueResolver valueResolver, SimpleTypeConverter typeConverter,
                                     Object[] argsToUse) {

        for(int i=0; i<argumentValues.size(); i++) {
            ConstructorArgument.ValueHolder valueHolder = argumentValues.get(i);
            Object value = valueHolder.getValue();
            try {
                Object resolvedValue =  valueResolver.resolveValueIfNecessary(value);
                Object convertedValue = typeConverter.convertIfNecessary(resolvedValue, parameterTypes[i]);
                argsToUse[i] = convertedValue;
            } catch (Exception e) {
                logger.error(e);
                return false;
            }
        }
        return true;
    }


}
