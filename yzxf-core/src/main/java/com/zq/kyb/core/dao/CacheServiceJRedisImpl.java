package com.zq.kyb.core.dao;

import com.zq.kyb.core.dao.redis.JedisUtil;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hujoey on 16/7/29.
 */
public class CacheServiceJRedisImpl implements CacheService {

    @Override
    public void putCache(String key, String value, Integer expre) {
        JedisUtil.setex(key, value, expre);
    }

    @Override
    public String getCache(String key) {
        return JedisUtil.get(key);
    }

    @Override
    public Set<String> getkeys(String pattern) {
        return JedisUtil.keys(pattern);
    }

    @Override
    public void putCache(byte[] key, byte[] value) {
        JedisUtil.set(key, value);
    }

    @Override
    public byte[] getCache(byte[] key) {
        return JedisUtil.get(key);
    }

    @Override
    public void removeCache(String key) {
        JedisUtil.del(key);
    }

    @Override
    public void removeCache(byte[] key) {
        JedisUtil.get(key);
    }
}
