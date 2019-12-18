/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘ <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.zq.kyb.account.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件
 *
 * @author L.cm email: 596392912@qq.com site: http://www.dreamlu.net
 *
 */
public class WeChatConfig {

    private static Properties props = new Properties();
    static {
        try {
            // play框架下要用这种方式加载
            // props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/wechat.properties"));
            props.load(WeChatConfig.class.getResourceAsStream("/wechat.properties"));
            messageProcessingHandlerImpl = props.getProperty("messageProcessingHandlerImpl");
            appId = props.getProperty("appId");
            appSecret = props.getProperty("appSecret");
            partnerId = props.getProperty("partnerId");
            partnerKey = props.getProperty("partnerKey");
            notifyUrl = props.getProperty("notifyUrl");
            redirectUri = props.getProperty("redirectUri");
            pluginDir = props.getProperty("pluginDir");
            openAppId = props.getProperty("openAppId");
            openAppSecret = props.getProperty("openAppSecret");
            jsapiUrl = props.getProperty("jsapiUrl");

            token = props.getProperty("token");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String messageProcessingHandlerImpl;
    public static String appId;
    public static String appSecret;
    public static String partnerId;
    public static String partnerKey;
    public static String notifyUrl;
    public static String redirectUri;
    public static String pluginDir;
    public static String openAppId;
    public static String openAppSecret;
    public static String jsapiUrl;

    public static String token;


}
