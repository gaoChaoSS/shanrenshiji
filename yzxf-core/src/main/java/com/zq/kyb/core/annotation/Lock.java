package com.zq.kyb.core.annotation;

import java.lang.annotation.ElementType;

@java.lang.annotation.Target(value = {ElementType.METHOD})
@java.lang.annotation.Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Lock {

    String key();//根据请求参数的那个字段来设置锁
}
