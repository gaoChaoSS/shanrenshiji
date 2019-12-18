package com.zq.kyb.core.dao;

import java.util.Set;

/**
 * 系统cache, 分为两种:
 * 1.系统cache,不会随着用户的登录或未登录而消失
 * 2.用户cache,如果用户已经退出,cache就消失,设置cache过期时间来解决这个问题.
 */
public interface CacheService {

    void putCache(String key, String value, Integer expre);

    String getCache(String key);

    Set<String> getkeys(String key);

    void putCache(byte[] key, byte[] value);

    byte[] getCache(byte[] key);

    void removeCache(String key);

    void removeCache(byte[] key);
}
