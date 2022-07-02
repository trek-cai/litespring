package org.litespring.beans.factory.annotation;

import org.litespring.beans.BeansException;
import org.litespring.beans.factory.BeanCreationException;
import org.litespring.beans.factory.config.AutowireCapableBeanFactory;
import org.litespring.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.litespring.core.annotation.AnnotationUtils;
import org.litespring.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

public class AutowiredAnnotationProcessor implements InstantiationAwareBeanPostProcessor {
    private AutowireCapableBeanFactory beanFactory;
    private String requiredParameterName = "required";
    private boolean requiredParameterValue = true;

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes =
            new LinkedHashSet<Class<? extends Annotation>>();

    public AutowiredAnnotationProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
    }

    public void setBeanFactory(AutowireCapableBeanFactory beanFactory){
        this.beanFactory = beanFactory;
    }

    public InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
        LinkedList<InjectionElement> elements = new LinkedList<InjectionElement>();
        Class<?> targetClass = clazz;

        do {
            LinkedList<InjectionElement> currElements = new LinkedList<InjectionElement>();
            for(Field field: clazz.getDeclaredFields()) {
                Annotation ann = findAutowiredAnnotation(field);    // 找到字段上的Autowired注解
                if (ann != null) {
                    if (Modifier.isStatic(field.getModifiers())) {
                        continue;
                    }
                    boolean required = determineRequiredStatus(ann);    // 判断该字段是否必需
                    currElements.add(new AutowiredFieldElement(field, required, beanFactory));
                }
            }
            for (Method method : targetClass.getDeclaredMethods()) {
                //TODO 处理方法注入
            }
            elements.addAll(0, currElements);
            targetClass = targetClass.getSuperclass();   // 获取父类需要被注入的成员变量
        } while(targetClass != null && targetClass != Object.class);
        return new InjectionMetadata(clazz, elements);
    }

    private Annotation findAutowiredAnnotation(AccessibleObject ao) {
        for(Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
            Annotation annotation = AnnotationUtils.getAnnotation(ao, type);
            if(annotation != null) {
                return annotation;
            }
        }
        return null;
    }

    protected boolean determineRequiredStatus(Annotation ann) {
        try {
            Method method = ReflectionUtils.findMethod(ann.annotationType(), this.requiredParameterName);
            if (method == null) {
                // Annotations like @Inject and @Value don't have a method (attribute) named "required"
                // -> default to required status
                return true;
            }
            return (this.requiredParameterValue == (Boolean) ReflectionUtils.invokeMethod(method, ann));
        }
        catch (Exception ex) {
            // An exception was thrown during reflective invocation of the required attribute
            // -> default to required status
            return true;
        }
    }

    public Object beforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object afterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object beforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    public boolean afterInstantiation(Object bean, String beanName) throws BeansException {
        return true;
    }

    public void postProcessPropertyValues(Object bean, String beanName) throws BeansException {
        InjectionMetadata injectionMetadata = this.buildAutowiringMetadata(bean.getClass());
        try {
            injectionMetadata.inject(bean);
        }
        catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
        }
    }
}
