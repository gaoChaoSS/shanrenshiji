package com.zq.kyb.payment.service;

import com.zq.kyb.payment.utils.HttpClientUtils;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by luoyunze on 17/3/7.
 */
public class WechatOauthService {

    public static Map<String, String> getAccessTokenAndOpenId(String appid, String secret, String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
                "appid=" + appid + "&secret=" + secret + "&code=" + code + "&grant_type=authorization_code";
        Map<String, String> re = new HashMap<>();
        try {
            String result = HttpClientUtils.simpleGetInvoke(url);
            JSONObject obj = JSONObject.fromObject(result);
            if (obj != null && obj.containsKey("openid")) {
                re.put("openId", obj.getString("openid"));
            }
            if (obj != null && obj.containsKey("access_token")) {
                re.put("accessToken", obj.getString("access_token"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return re;
    }
}
