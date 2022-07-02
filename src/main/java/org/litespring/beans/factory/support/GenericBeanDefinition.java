package org.litespring.beans.factory.support;

import org.litespring.ConstructorArgument;
import org.litespring.beans.BeanDefinition;
import org.litespring.beans.PropertyValue;

import java.util.ArrayList;
import java.util.List;

public class GenericBeanDefinition implements BeanDefinition {

    private String beanID;
    private String beanClassName;
    private Class<?> beanClass;
    private boolean singleton = true;
    private boolean prototype = false;
    private String score = SCORE_DEFAULT;
    private List<PropertyValue> propertyValues = new ArrayList<PropertyValue>();
    private ConstructorArgument constructorArgument = new ConstructorArgument();

    public GenericBeanDefinition(String beanID, String beanClassName) {
        this.beanID = beanID;
        this.beanClassName = beanClassName;
    }

    public GenericBeanDefinition() {

    }

    public String getBeanClassName() {
        return this.beanClassName;
    }

    public void setBeanClassName(String className){
        this.beanClassName = className;
    }

    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public ConstructorArgument getConstructorArgument() {
        return this.constructorArgument;
    }

    public String getID() {
        return this.beanID;
    }

    public void setID(String beanID) {
        this.beanID = beanID;
    }

    public boolean hasConstructorArgumentValues() {
        return !this.constructorArgument.isEmpty();
    }

    public Class<?> getBeanClass() throws IllegalStateException {
        if(this.beanClass == null){
            throw new IllegalStateException(
                    "Bean class name [" + this.getBeanClassName() + "] has not been resolved into an actual Class");
        }
        return this.beanClass;
    }

    public boolean hasBeanClass() {
        return this.beanClass != null;
    }

    public Class<?> resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
        String beanClassName = getBeanClassName();
        if(beanClassName == null) {
            return null;
        }
        Class<?> beanClass = classLoader.loadClass(beanClassName);
        this.beanClass = beanClass;
        return beanClass;
    }

    public boolean isSingleton() {
        return this.singleton;
    }

    public boolean isPrototype() {
        return this.prototype;
    }

    public String getScore() {
        return this.score;
    }

    public void setScore(String score) {
        this.score = score;
        this.singleton = SCORE_SINGLETON.equals(score) || SCORE_DEFAULT.equals(score);
        this.prototype = SCORE_PROTOTYPE.equals(score);
    }
}
