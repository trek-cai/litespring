package org.litespring.beans.factory.annotation;

import org.litespring.beans.factory.BeanCreationException;
import org.litespring.beans.factory.config.AutowireCapableBeanFactory;
import org.litespring.beans.factory.config.DependencyDescriptor;
import org.litespring.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

public class AutowiredFieldElement extends InjectionElement {
    protected boolean required;

    public AutowiredFieldElement(Member member, boolean required, AutowireCapableBeanFactory factory) {
        super(member, factory);
        this.required = required;
    }

    public Field getField(){
        return (Field)this.member;
    }


    public void inject(Object target) {
        Field field = this.getField();
        DependencyDescriptor descriptor = new DependencyDescriptor(field, this.required);
        Object value = factory.resolveDependency(descriptor);
        if(value != null) {
            ReflectionUtils.makeAccessible(field);
            try {
                field.set(target, value);
            } catch (Throwable ex) {
                throw new BeanCreationException("Could not autowire field: " + field, ex);
            }
        }
    }
}
