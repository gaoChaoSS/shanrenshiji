package com.zq.kyb.core.annotation;

/**
 * API版本控制
 */
@java.lang.annotation.Target(value = {java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface DVersion {

    int version() default 1;// 版本，必须指定
}
