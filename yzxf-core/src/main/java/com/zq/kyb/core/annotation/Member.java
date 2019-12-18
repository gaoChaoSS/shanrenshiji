package com.zq.kyb.core.annotation;

import java.lang.annotation.ElementType;

/**
 * 标示该访问为会员访问
 */
@java.lang.annotation.Target(value = {ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Member {
}
