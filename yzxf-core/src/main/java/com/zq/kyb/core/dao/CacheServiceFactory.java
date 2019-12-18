package com.zq.kyb.core.dao;


import com.zq.kyb.core.init.Constants;

/**
 */
public class CacheServiceFactory {
    static CacheService inc;
    public static String cache_prefix_userResources = "User_Resource_";//用户权限的cache前缀
    public static String cache_prefix_systemSetting = "System_Setting_";//系统配置的cache前缀

    public static CacheService getInc() throws Exception {
        if (inc == null) {
            if (Constants.cacheImplClass != null) {
                inc = Constants.cacheImplClass.newInstance();
            } else {
//                inc = new CacheServiceHashMapImpl();
                inc = new CacheServiceJRedisImpl();
            }
        }
        return inc;
    }
}
