package org.litespring.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationUtils {

    // 获取继承于Annotation的注解
    public static <T extends Annotation> T getAnnotation(AnnotatedElement ae, Class<T> annotationType) {
        T ann = ae.getAnnotation(annotationType);
        if(ann == null) {
            for(Annotation metaAnn: ae.getAnnotations()) {
                ann = metaAnn.annotationType().getAnnotation(annotationType);
                if(ann != null) {
                    break;
                }
            }
        }
        return ann;
    }
}
