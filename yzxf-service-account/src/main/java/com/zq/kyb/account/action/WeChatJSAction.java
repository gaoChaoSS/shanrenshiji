package com.zq.kyb.account.action;

import com.zq.kyb.account.config.WeChatConfig;
import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceFactory;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.HttpClientUtils;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * Created by zq2014 on 17/8/28.
 */
public class WeChatJSAction extends BaseActionImpl {


    static final String API_TOKEN_GET = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";

    static final String API_JS_GETTICKET = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=%s&type=jsapi";

    static final String CACHE_KEY_TOKEN = CacheServiceFactory.cache_prefix_systemSetting + "token";
    static final String CACHE_KEY_TICKET_JSAPI = CacheServiceFactory.cache_prefix_systemSetting + "ticket_jsapi";

    @GET
    @Seller
    @Path("/getPicture")
    public void getPicture() throws Exception {
        String serverId = ControllerContext.getPString("serverId");
        String token = getToken();
        String url = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token=" + token + "&media_id=" + serverId;
        Message msg = Message.newReqMessage("1:POST@/file/FileItem/download");
        //msg.setContentByteArray(by);
        String fileId = ZQUidUtils.genUUID();
        msg.getContent().put("fileId", fileId);
        msg.getContent().put("name", "icon.jpg");
        msg.getContent().put("projectName", "weChat_sync");
        msg.getContent().put("entityName", ControllerContext.getPString("entityName"));
        msg.getContent().put("entityId", ControllerContext.getPString("_id"));
        msg.getContent().put("entityField", "icon");
        msg.getContent().put("downloadUrl", url);
//        msg.setConnectTimeout(5 * 60 * 1000);
//        msg.setSocketTimeout(5 * 60 * 1000);
        JSONObject reContent = ServiceAccess.callService(msg).getContent();

        Map<String, Object> re = new HashMap<>();
        re.put("fileId", reContent.get("_id"));

        toResult(200, re);
    }

    @GET
    @Path("/receiveMsg")
    public void receiveMsg() throws IOException {
        String signature = ControllerContext.getPString("signature");
        String timestamp = ControllerContext.getPString("timestamp");
        String nonce = ControllerContext.getPString("nonce");
        String echostr = ControllerContext.getPString("echostr");
        System.out.println("signature:" + signature);
        System.out.println("timestamp:" + timestamp);
        System.out.println("nonce:" + nonce);
        System.out.println("echostr:" + echostr);
        HashMap<String, Object> re = new HashMap<>();
        String str = "";

        List<String> params = new ArrayList<>();
        params.add(WeChatConfig.token);
        params.add(timestamp);
        params.add(nonce);
        Collections.sort(params, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        String temp = params.get(0) + params.get(1) + params.get(2);
        if (DigestUtils.shaHex(temp).equals(signature)) {
            str = echostr;
        }
        re.put("___outText", str);
        toResult(200, re);
    }


    @GET
    @Member
    @Path("/getConfig")
    public void getConfig() throws Exception {
        String url = ControllerContext.getPString("url");
        url = URLDecoder.decode(url, "utf-8");
        boolean debug = true;
        JSONObject config = getConfig(debug, url);
        System.out.println("=============" + config);
        toResult(200, config);
    }

    private String getToken() throws Exception {
        String token = CacheServiceFactory.getInc().getCache(CACHE_KEY_TOKEN);
        if (StringUtils.isEmpty(token)) {
            String url = String.format(API_TOKEN_GET, WeChatConfig.appId, WeChatConfig.appSecret);
            byte[] responseByte = HttpClientUtils.simpleGetInvokeByte(url);
            JSONObject responseData = JSONObject.fromObject(new String(responseByte));
            System.out.println(responseData.toString());

            if (StringUtils.mapValueIsEmpty(responseData, "access_token")) {
                throw new UserOperateException(400, responseData.getString("errmsg"));
            }

            token = responseData.getString("access_token");
            CacheServiceFactory.getInc().putCache(CACHE_KEY_TOKEN, token, responseData.getInt("expires_in"));
        }
        return token;
    }

    private String getTicket() throws Exception {
        String ticket = CacheServiceFactory.getInc().getCache(CACHE_KEY_TICKET_JSAPI);
        if (StringUtils.isEmpty(ticket)) {
            String url = String.format(API_JS_GETTICKET, getToken());
            byte[] responseByte = HttpClientUtils.simpleGetInvokeByte(url);
            JSONObject responseData = JSONObject.fromObject(new String(responseByte));
            System.out.println(responseData.toString());

            if (responseData.getInt("errcode") != 0) {
                throw new UserOperateException(400, responseData.getString("errmsg"));
            }

            ticket = responseData.getString("ticket");
            CacheServiceFactory.getInc().putCache(CACHE_KEY_TICKET_JSAPI, ticket, responseData.getInt("expires_in"));

        }
        return ticket;
    }

    public JSONObject getConfig(boolean debug, String url) {
        JSONObject config = new JSONObject();
        try {
            String timestamp = create_timestamp();
            String nonceStr = create_nonce_str();
            // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
            config.put("debug", debug);
            // 必填，公众号的唯一标识
            config.put("appId", WeChatConfig.appId);
            // 必填，生成签名的时间戳
            config.put("timestamp", Long.parseLong(timestamp));
            // 必填，生成签名的随机串
            config.put("nonceStr", nonceStr);
            // 必填，签名，见附录1
            config.putAll(sign(url, nonceStr, timestamp));
//            config.put("signature", );
            // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
            JSONArray jsApiList = new JSONArray();
            jsApiList.add("getLocation");
//            jsApiList.add("chooseImage");
//            jsApiList.add("previewImage");
//            jsApiList.add("uploadImage");
//            jsApiList.add("downloadImage");
//            jsApiList.add("chooseWXPay");
            jsApiList.add("scanQRCode");
            jsApiList.add("openLocation");
            config.put("jsApiList", jsApiList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return config;
    }

    public Map<String, String> sign(String url, String nonceStr, String timestamp) throws Exception {
        String jsapiTicket = getTicket();
        Map<String, String> ret = new HashMap<String, String>();
        String string1;
        String signature = "";

        //注意这里参数名必须全部小写，且必须有序
        string1 = "jsapi_ticket=" + jsapiTicket +
                "&noncestr=" + nonceStr +
                "&timestamp=" + timestamp +
                "&url=" + url;
        System.out.println(string1);

        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(string1.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ret.put("url", url);
        ret.put("jsapi_ticket", jsapiTicket);
        ret.put("nonceStr", nonceStr);
        ret.put("timestamp", timestamp);
        ret.put("signature", signature);

        return ret;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return ZQUidUtils.genUUID();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

}
