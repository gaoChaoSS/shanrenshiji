package com.zq.kyb.core.secu;


import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.StringUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 */
public class SessionServiceImpl implements SessionService {

    public SessionServiceImpl() {

    }

    @Override
    public Map<String, Object> startSession(Map<String, Object> session) throws Exception {
        session.put("_id", UUID.randomUUID().toString());
        session.put("createTime", System.currentTimeMillis());
        // session.put("clientIp", ControllerContext.getIpAddr());
        String genStr = genSessionStr(session);
        String signStr = SessionSign.getInstant().sign(genStr);
        String session_full_str = genStr + TOKEN_SIGN_REGEX + signStr;
        session.put("sessionFullStr", session_full_str);
        // userSessionMap.put((String) session.get("_id"), session_full_str);
        MysqlDaoImpl.getInstance().saveOrUpdate("Session", session);
        return session;
    }


    /**
     * 通过签名检查token是否有效
     *
     * @param token
     * @throws Exception
     */
    public static void checkToken(String token) throws Exception {
        boolean check = false;

        // Logger.getLogger(SessionServiceImpl.class).info("---check session:");
        // Logger.getLogger(SessionServiceImpl.class).info("---token:" + token);
        int indexOf = token.lastIndexOf(" ");
        String f = token.substring(0, indexOf);
        String e = token.substring(indexOf + 1, token.length());
        // Logger.getLogger(SessionServiceImpl.class).info("---first:" + f);
        // Logger.getLogger(SessionServiceImpl.class).info("---end:" + e);
        check = SessionSign.getInstant().check(f, e);
        //debug
        if (!check) {// 验证未通过
            Logger.getLogger(SessionServiceImpl.class).info("--token:" + token);
            Logger.getLogger(SessionServiceImpl.class).info("--data:" + f);
            Logger.getLogger(SessionServiceImpl.class).info("--sign:" + e);
            throw new UserOperateException(401, "登录已失效");
        }
    }

    @Override
    public Map<String, Object> getSesssionById(String sessionId) throws Exception {
        return MysqlDaoImpl.getInstance().findById2Map("Session", sessionId, null, null);
    }

    @Override
    public List<Map<String, Object>> getSesssionsByUserId(String userId) throws Exception {

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("creator", userId);
        return MysqlDaoImpl.getInstance().findAll2Map("Session", params, null, null, null);
    }

    public static String genSessionStr(Map<String, Object> session) throws UnsupportedEncodingException {
        Object expireTime = session.get("expireTime");
        Object creator = session.get("creator");
        Object deviceId = session.get("deviceId");
        Object type = session.get("type");
        Object sellerId = session.get("sellerId");
        Object isSellerAdmin = session.get("isSellerAdmin");
        Object otherDataJson = session.get("otherDataJson");
        expireTime = expireTime == null ? "" : expireTime;
        creator = creator == null ? "" : creator;
        deviceId = deviceId == null ? "" : deviceId;
        type = type == null ? "" : type;
        sellerId = sellerId == null ? "-1" : sellerId;
        otherDataJson = otherDataJson == null ? "" : otherDataJson;
        Logger.getLogger(SessionServiceImpl.class).info("==session:deviceId=" + deviceId +
                ",creator=" + creator +
                ",expireTime=" + expireTime +
                ",sellerId=" + session.get("sellerId") +
                ",isSellerAdmin=" + isSellerAdmin +
                ",otherDataJson=" + otherDataJson
        );


        Object[] objects = { //
                session.get("_id").toString(), //sessionId,
                deviceId.toString(), //设备id
                creator.toString(), //用户id
                expireTime.toString(), //过期时间
                type.toString(),//用户类型
                sellerId,//商户id,如果不是商户就为-1
                isSellerAdmin,
                URLEncoder.encode(otherDataJson.toString(), "utf-8")
        };
        return StringUtils.join(objects, SessionService.TOKEN_REGEX);
    }

    @Override
    public void endSession() throws Exception {
        String sessionId = ControllerContext.getContext().getSessionId();
        endSession(sessionId);
    }

    private void endSession(String sessionId) throws Exception {
        if (org.apache.commons.lang.StringUtils.isNotEmpty(sessionId)) {
            MysqlDaoImpl.getInstance().remove("Session", sessionId);
        }
    }


}