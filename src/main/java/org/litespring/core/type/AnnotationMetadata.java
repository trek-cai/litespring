package org.litespring.core.type;

import java.util.Set;

import org.litespring.core.annotation.AnnotationAttributes;

/**
 * AnnotationMetadata：操作类注解元数据的接口
 */
public interface AnnotationMetadata extends ClassMetadata{

    Set<String> getAnnotationTypes();


    boolean hasAnnotation(String annotationType);

    AnnotationAttributes getAnnotationAttributes(String annotationType);
}
