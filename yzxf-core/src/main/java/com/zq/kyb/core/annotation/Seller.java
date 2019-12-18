package com.zq.kyb.core.annotation;

import java.lang.annotation.ElementType;

/**
 * 标示该用户由商户用户访问
 */
@java.lang.annotation.Target(value = {ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Seller {
    boolean isAdmin() default false;
}
