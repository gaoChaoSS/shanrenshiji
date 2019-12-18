package com.zq.kyb.account.action;


import com.zq.kyb.account.config.WeChatConfig;
import com.zq.kyb.core.conn.websocket.BaseWebsocketServer;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceFactory;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.HttpClientUtils;
import com.zq.kyb.util.StringUtils;
import com.zq.kyb.util.UrlUtils;
import net.sf.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.*;

public class OauthAction extends BaseActionImpl {
    public static String callbackUrl = "http://s.yzxf8.cn/oauth_page/callback.jsp";//通过youlai01.com中转

    /**
     * 开始发起登录
     *
     * @throws Exception
     */
    @GET
    @Path("/start")
    public void start() throws Exception {
        String type = ControllerContext.getPString("type");
        String display = ControllerContext.getPString("display");
        String deviceId = ControllerContext.getPString("deviceId");
        String sellerId = ControllerContext.getPString("sellerId");
        String state = type + "_" + deviceId+"_login";
        if(StringUtils.isNotEmpty(sellerId)){
            state+="_"+sellerId;
        }
        String endUrl = null;
        String url = null;
        Map<String, String> params = null;

        if ("wechatPc".equals(type)) {
            url = "https://open.weixin.qq.com/connect/qrconnect";
            params = new LinkedHashMap<String, String>();
            params.put("appid", WeChatConfig.openAppId);
            params.put("scope", "snsapi_login"); // PC 上目前只支持 snsapi_login
            params.put("redirect_uri", callbackUrl);
            params.put("state", state);
        } else if ("wechatMobile".equals(type)) {
            url = "https://open.weixin.qq.com/connect/oauth2/authorize";
            params = new LinkedHashMap<String, String>();
            params.put("appid", WeChatConfig.appId);
            params.put("redirect_uri", callbackUrl);
            params.put("response_type", "code");
            params.put("scope", "snsapi_userinfo");
            params.put("state", state);
            params.put("connect_redirect", "1");
        } else {
            throw new UserOperateException(400, "请求参数错误！");
        }
        endUrl = url + "?" + UrlUtils.toQueryStr(params) + (type.startsWith("wechat") ? "#wechat_redirect" : "");
        Logger.getLogger(this.getClass()).info("-----start oatuh url:" + endUrl);

        // 缓存30分钟
        CacheServiceFactory.getInc().putCache(CacheServiceFactory.cache_prefix_systemSetting + "login_oauth_" + state, "start", 60 * 30);

        HashMap<String, Object> re = new HashMap<String, Object>();
        re.put("sendUrl", endUrl);
        toResult(200, re);
    }

    /**
     * 是由第3方访问
     *
     * @throws Exception
     */
    @GET
    @Path("/callback")
    public void callback() throws Exception {
        String code = ControllerContext.getPString("code");
        String v = ControllerContext.getPString("state");

        String state = java.net.URLDecoder.decode(v, "utf-8");

        String stateKey = CacheServiceFactory.cache_prefix_systemSetting + "login_oauth_" + state;

        // 缓存30分钟
        CacheServiceFactory.getInc().putCache(stateKey, "callback", 60 * 30);
        Map<String, Object> oldOauth = null;

        // 开始获取openId
        if (state.startsWith("wechat")) {
            oldOauth = wechatCallback(state, code);
            Logger.getLogger(this.getClass()).info("======================oldOauth:"+JSONObject.fromObject(oldOauth));
        } else {
            throw new RuntimeException("错误的state, state=" + state);
        }

        Map<String, Object> re = null;
        String memberId = (String) oldOauth.get("memberId");
        if (StringUtils.isNotEmpty(memberId) && !state.endsWith("_pay")) {// 直接去登录
            Map<String, Object> member = ServiceAccess.getRemoveEntity("crm", "Member", memberId);

            Logger.getLogger(this.getClass()).info("\n\n\n----------oldOauth:userInfo:"+oldOauth.get("userInfo"));
            if(oldOauth != null && !StringUtils.mapValueIsEmpty(oldOauth,"userInfo")){
                JSONObject userInfo = JSONObject.fromObject(oldOauth.get("userInfo"));
                member.put("openId",userInfo.get("openid"));
            }

            re = UserAction.loginAfter(-1, state.split("_")[1], member);
            CacheServiceFactory.getInc().removeCache(CacheServiceFactory.cache_prefix_systemSetting + "login_oauth_" + state);
        }
        if (re == null) {// 登录失败，或第一次登录未绑定
            re = new HashMap<>();
            re.put("bindId", oldOauth.get("_id"));
            re.put("state", state);
            re.put("name", oldOauth.get("nickName"));
            re.put("icon", oldOauth.get("logoUrl"));
            re.put("openId", oldOauth.get("openId"));
        }
        toResult(200, re);
    }

    private Map<String, Object> wechatCallback(String state, String code) throws Exception {
        Map<String, Object> oa = new JSONObject();
        if (state.startsWith("wechatMobile")) {
            oa = getWeChatUserInfoByMP(code);
        } else if (state.startsWith("wechatPc")) {
            oa = getWeChatUserInfoByOpen(code);
        }
        Map<String, Object> params = new HashMap<>();
        Object openId;
        if(StringUtils.mapValueIsEmpty(oa,"unionid")){
            openId = oa.get("openid");
        }else{
            openId = oa.get("unionid");
        }
        params.put("openId", openId);
        params.put("type", OauthType.WECHAT.toString());
        Map<String, Object> oldOauth = MysqlDaoImpl.getInstance().findOne2Map("Oauth", params, null, null);
        if (oldOauth == null) {
            oldOauth = new HashMap<>();
            oldOauth.put("_id", UUID.randomUUID().toString());
        }
        oldOauth.putAll(params);
        oldOauth.put("accessToken", oa.get("access_token"));
        oldOauth.put("nickName", oa.get("nickname"));
        oldOauth.put("logoUrl", oa.get("headimgurl"));
        if (oa.containsKey("openid")) {
            oldOauth.put("wechatOpenId", oa.get("openid"));// wechat 的openId
        }
        if (oa.containsKey("subscribe")) {
            oldOauth.put("wechatSubscribe", oa.get("subscribe"));// wechat 中，用户是否关注了公众号
        }
        oldOauth.put("userInfo", oa.toString());
        MysqlDaoImpl.getInstance().saveOrUpdate("Oauth", oldOauth);
        return oldOauth;
    }

    /**
     * 开放平台获取 用户信息，包含UnionID 不同的应用 有相同的 UnionID
     *
     * @param code
     * @return
     * @throws Exception
     */
    private Map<String, Object> getWeChatUserInfoByOpen(String code) throws Exception {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WeChatConfig.openAppId + "&secret=" + WeChatConfig.openAppSecret + "&code=" + code + "&grant_type=authorization_code";
        String str = file_get_contents(url);
        Map<String, Object> result = JSONObject.fromObject(str);
        url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + result.get("access_token") + "&openid=" + result.get("openid");
        str = file_get_contents(url);
        JSONObject userInfo = JSONObject.fromObject(str);
        userInfo.putAll(result);
        return userInfo;
    }

    /**
     * 公众平台获取 获取 用户信息，包含UnionID 不同的应用 有相同的 UnionID
     *
     * @param code
     * @return
     * @throws Exception
     */
    private Map<String, Object> getWeChatUserInfoByMP(String code) throws Exception {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WeChatConfig.appId + "&secret=" + WeChatConfig.appSecret + "&code=" + code + "&grant_type=authorization_code";
        String str = file_get_contents(url);
        Map<String, Object> result = JSONObject.fromObject(str);
        // url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=" + WeChat.getAccessToken() + "&openid=" + result.get("openid") + "&lang=zh_CN";
        url = "https://api.weixin.qq.com/sns/userinfo?access_token=" + result.get("access_token") + "&openid=" + result.get("openid") + "&lang=zh_CN";
        str = file_get_contents(url);
        JSONObject userInfo = JSONObject.fromObject(str);
        userInfo.putAll(result);
        // userInfo.put("access_token", WeChat.getAccessToken());
        return userInfo;
    }

    public static String file_get_contents(String link) throws Exception {
        return file_get_contents(link, null, null);
    }

    static int index = 0;

    private static String file_get_contents(String link, String authType, String access_token) throws Exception {
        getIndex();
        Logger.getLogger(OauthAction.class).info("==== start Request:[" + index + "]" + link);
        Logger.getLogger(OauthAction.class).info("  -- authType::[" + index + "]" + authType);
        Logger.getLogger(OauthAction.class).info("  -- access_token::[" + index + "]" + access_token);
        URL url = new URL(link);
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.setConnectTimeout(2000);
        if (authType != null && access_token != null) {
            conn.addRequestProperty("Authorization", authType + " " + access_token);
        }
        InputStream is = null;
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            try {
                is = conn.getInputStream();
                String re = IOUtils.toString(is);
                Logger.getLogger(OauthAction.class).info("  -- re:[" + index + "]" + re);
                return re;
            } finally {
                is.close();
            }
        }
        Logger.getLogger(OauthAction.class).info("  -- re err!:");
        return null;
    }

    private synchronized static int getIndex() {
        return ++index;
    }

}
