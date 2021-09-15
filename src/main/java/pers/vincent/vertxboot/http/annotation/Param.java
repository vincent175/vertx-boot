package pers.vincent.vertxboot.http.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Vincent
 * @date: 2021/9/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Param {

    String name() default "";

    boolean required() default false;
}
