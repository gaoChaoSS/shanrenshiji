package com.zq.kyb.core.dao;

/**
 * 用于通数据锁服务,保证写数据时避免脏数据
 */
public interface LockService {
    /**
     * 获取锁
     *
     * @param key
     * @return
     */
    boolean getLock(String key);

    /**
     * 阻塞当前线程的方式获取锁
     *
     * @param key
     * @param timeOutSeconds 超时时间
     */
    void whileGetLock(String key, int timeOutSeconds) throws InterruptedException, Exception;

    /**
     * 释放锁
     *
     * @param key
     */
    void unLock(String key);
}
