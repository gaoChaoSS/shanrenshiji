package com.zq.kyb.core.secu;

import java.util.List;
import java.util.Map;

public interface SessionService {
    public static final String TOKEN_REGEX = ",";// 用于分隔字段
    public static final String TOKEN_SIGN_REGEX = " "; // 用于分隔原文与签名

    // 保存用户登录<UserId，Cookies>
    // public static final java.util.concurrent.ConcurrentHashMap<String, String> userSessionMap = new java.util.concurrent.ConcurrentHashMap<String, String>();

    public Map<String, Object> startSession(Map<String, Object> session) throws Exception;

    public void endSession() throws Exception;

    public List<Map<String, Object>> getSesssionsByUserId(String userId) throws Exception;

    public Map<String, Object> getSesssionById(String sessionId) throws Exception;
}
