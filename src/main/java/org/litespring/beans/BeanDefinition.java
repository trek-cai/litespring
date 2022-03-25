package org.litespring.beans;

public interface BeanDefinition {
    public static String SCORE_SINGLETON = "singleton";
    public static String SCORE_PROTOTYPE = "prototype";
    public static String SCORE_DEFAULT = "";

    boolean isSingleton();
    boolean isPrototype();
    String getScore();
    void setScore(String score);

    String getBeanClassName();
}
