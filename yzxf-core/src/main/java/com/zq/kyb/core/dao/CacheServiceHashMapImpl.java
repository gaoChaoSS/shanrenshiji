package com.zq.kyb.core.dao;

import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.exception.UserOperateException;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hujoey on 16/7/29.
 */
public class CacheServiceHashMapImpl implements CacheService {
    private ConcurrentHashMap m = new ConcurrentHashMap();

    @Override
    public void putCache(String key, String value, Integer expre) {
        m.put(key, value);
    }

    @Override
    public String getCache(String key) {
        return (String) m.get(key);
    }

    @Override
    public void putCache(byte[] key, byte[] value) {
        m.put(key, value);
    }

    @Override
    public byte[] getCache(byte[] key) {
        return (byte[]) m.get(key);
    }

    @Override
    public void removeCache(String key) {
        m.remove(key);
    }

    @Override
    public void removeCache(byte[] key) {
        m.remove(key);
    }

    @Override
    public Set<String> getkeys(String pattern) {
        throw new UserOperateException(400, "CacheServiceHashMapImpl not impl getkeys ");
    }
}
