package org.litespring.beans;

import java.util.List;

public interface BeanDefinition {
    String SCORE_SINGLETON = "singleton";
    String SCORE_PROTOTYPE = "prototype";
    String SCORE_DEFAULT = "";

    boolean isSingleton();
    boolean isPrototype();
    String getScore();
    void setScore(String score);

    String getBeanClassName();
    List<PropertyValue> getPropertyValues();
}
