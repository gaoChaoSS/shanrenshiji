package com.zq.kyb.core.ctrl;

import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.secu.SessionService;
import com.zq.kyb.core.secu.SessionServiceImpl;
import com.zq.kyb.util.StringUtils;
import com.zq.kyb.core.model.Message;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class ControllerContext implements java.io.Serializable {

    private static final long serialVersionUID = 1264206250944334587L;

    public static final ThreadLocal<ControllerContext> context = new ThreadLocal<ControllerContext>();
    //    private String currentRoles;
    private String currentSellerId;
    private String currentUserType;
    private Boolean isSellerAdmin = false;
    private String otherDataJson;

    public static ControllerContext getContext() {
        ControllerContext s = context.get();
        if (s == null) {
            s = new ControllerContext();
            context.set(s);
        }
        return s;
    }

    public static void setContext(ControllerContext s) {
        context.set(s);
    }

    public static void clearContext() {
        context.set(null);
        context.remove();
    }

    public static boolean isAdminUser() {
        String currentUserType = ControllerContext.getContext().getCurrentUserType();
        Logger.getLogger(ControllerContext.class).info("c:" + currentUserType);
        return "admin".equals(currentUserType);
    }

    public Message getReq() {
        if (req == null) {
            req = new Message();
        }
        return req;
    }

    public void setReq(Message req) {
        this.req = req;
    }

    public Message getResp() {
        if (req != null && resp == null) {
            resp = Message.copy(ControllerContext.getContext().getReq());
            resp.setCode(200);
            resp.setContent(null);
        }
        return resp;
    }

    public void setResp(Message resp) {
        this.resp = resp;
    }

    private Message req;// 请求消息

    private Message resp;// 响应消息

    private String token;// 令牌访问一个消息的唯一合法令牌
    private String sessionId;
    private String deviceId;
    private String currentUserId;
    private Long expireTime;
    private String remoteAddr;//IP地址


    // 对于需要传输2进制的情况下使用，默认不用
    private InputStream reqIn;
    private OutputStream respOut;

    public static void setResult(int i, JSONObject reContent) {
        Message re = Message.copy(ControllerContext.getContext().getReq());
        re.setCode(i);
        re.setContent(reContent);
        ControllerContext.getContext().setResp(re);
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public static String getPString(String key) {
        String str = null;
        JSONObject obj = ControllerContext.getContext().getReq().getContent();
        if (obj != null) {
            if (obj != null && obj.containsKey(key)) {
                str = obj.getString(key);
            }
        }
        return str;
    }

    public static Boolean getPBoolean(String key) {
        Boolean str = null;
        JSONObject obj = ControllerContext.getContext().getReq().getContent();
        if (obj != null) {
            if (obj != null && obj.containsKey(key)) {
                str = obj.getBoolean(key);
            }
        }
        return str;
    }

    public static int getPInteger(String key) {
        try {
            return Integer.valueOf(getPString(key));
        } catch (Exception e) {

        }
        return 0;
    }


    public static Long getPLong(String key) {
        try {
            return Long.valueOf(getPString(key));
        } catch (Exception e) {

        }
        return 0L;
    }

    public static Double getPDouble(String key) {
        try {
            return Double.valueOf(getPString(key));
        } catch (Exception e) {

        }
        return 0D;
    }

    /**
     * 设置客户或商户的登录token
     *
     * @param tokenStr
     */
    public void setToken(String tokenStr) throws Exception {
        if (StringUtils.isEmpty(tokenStr)) {
            Logger.getLogger(this.getClass()).info("token is null");
            return;
        }

        this.token = tokenStr;
        String str = this.token.split(SessionService.TOKEN_SIGN_REGEX)[0];
        String[] split = str.split(SessionService.TOKEN_REGEX);
        if (split.length < 4) {
            throw new UserOperateException(400, "token验证不通过!");
        }
        String userId = split[2];

        this.sessionId = split[0];
        this.deviceId = split[1];
        this.currentUserId = userId;
        this.expireTime = StringUtils.isEmpty(split[3]) ? 3600000 * 3 : Long.valueOf(split[3]);
        this.currentUserType = split[4];
        if (split.length > 5 && "user".equals(this.currentUserType)) {//如果是商户登录,则设置当前商户号
            this.currentSellerId = split[5];
        }
        if ("admin".equals(this.currentUserType)) {//如果是管理员登录,并且在请求参数中设置了sellerId,就放到上下文中
            this.currentSellerId = ControllerContext.getPString("sellerId");
        }

        if (split.length > 6) {
            this.isSellerAdmin = Boolean.valueOf(split[6]);
        }
        if (split.length > 7) {
            this.otherDataJson = split[7];
            if (StringUtils.isNotEmpty(this.otherDataJson)) {
                this.otherDataJson = URLDecoder.decode(this.otherDataJson, "utf-8");
            }
//            try {
//                this.currentSellerId = JSONObject.fromObject(this.otherDataJson).getString("sellerId");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }


    public String getSessionId() {
        return sessionId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Long getExpireTime() {
        return expireTime;
    }

    public String getToken() {
        return token;
    }

    public InputStream getReqIn() {
        return reqIn;
    }

    public void setReqIn(InputStream reqIn) {
        this.reqIn = reqIn;
    }

    public OutputStream getRespOut() {
        return respOut;
    }

    public void setRespOut(OutputStream respOut) {
        this.respOut = respOut;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }

//    public String getCurrentRoles() {
//        return currentRoles;
//    }
//
//    public void setCurrentRoles(String currentRoles) {
//        this.currentRoles = currentRoles;
//    }

    public String getCurrentSellerId() {
        return currentSellerId;
    }

    public void setCurrentSellerId(String currentSellerId) {
        this.currentSellerId = currentSellerId;
    }


    public String getCurrentUserType() {
        return currentUserType;
    }

    public void setCurrentUserType(String currentUserType) {
        this.currentUserType = currentUserType;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Boolean isSellerAdmin() {
        return isSellerAdmin;
    }

    public void setSellerAdmin(Boolean sellerAdmin) {
        isSellerAdmin = sellerAdmin;
    }

    public String getOtherDataJson() {
        return otherDataJson;
    }
}
