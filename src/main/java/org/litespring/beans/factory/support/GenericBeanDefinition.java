package org.litespring.beans.factory.support;

import org.litespring.beans.BeanDefinition;

public class GenericBeanDefinition implements BeanDefinition {

    private String beanID;
    private String beanClassName;
    private boolean singleton = true;
    private boolean prototype = false;
    private String score = SCORE_DEFAULT;

    public GenericBeanDefinition(String beanID, String beanClassName) {
        this.beanID = beanID;
        this.beanClassName = beanClassName;
    }

    public String getBeanClassName() {
        return this.beanClassName;
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
