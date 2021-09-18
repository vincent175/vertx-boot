package pers.vincent.vertxboot.http.annotation;

import pers.vincent.vertxboot.http.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author: Vincent
 * @date: 2021/9/15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE,ElementType.METHOD})
public @interface RequestMapping {

    String name() default "";

    String value() default "";

    String path() default "";

    String[] produces() default {"application/json"};

    String[] consumes() default {};

    HttpMethod[] method() default {HttpMethod.GET, HttpMethod.POST};

    boolean blocked() default true;
}
