/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘ <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.zq.kyb.payment.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * 配置文件
 *
 * @author L.cm email: 596392912@qq.com site: http://www.dreamlu.net
 */
public class WeChatConfig {

    //以下是几个API的路径：
    //1）被扫支付API
    public static String PAY_API = "https://api.mch.weixin.qq.com/pay/micropay";

    //2）被扫支付查询API
    public static String PAY_QUERY_API = "https://api.mch.weixin.qq.com/pay/orderquery";

    //3）退款API
    public static String REFUND_API = "https://api.mch.weixin.qq.com/secapi/pay/refund";

    //4）退款查询API
    public static String REFUND_QUERY_API = "https://api.mch.weixin.qq.com/pay/refundquery";

    //5）撤销API
    public static String REVERSE_API = "https://api.mch.weixin.qq.com/secapi/pay/reverse";

    //6）下载对账单API
    public static String DOWNLOAD_BILL_API = "https://api.mch.weixin.qq.com/pay/downloadbill";

    //7) 统计上报API
    public static String REPORT_API = "https://api.mch.weixin.qq.com/payitil/report";

    //被扫支付的请求类
    public static String HttpsRequestClassName = "com.zq.kyb.payment.wechatSDK.scanpay.common.HttpsRequest";

    public static String notifiUrl = PaymentConfig.baseUrl + "s_user/api/payment/Pay/notifiWechatInputText";//支付宝的异步通知服务;

    private static Properties props = new Properties();

    static {
        try {
            // play框架下要用这种方式加载
            // props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("/wechat.properties"));
            props.load(WeChatConfig.class.getResourceAsStream("/wechat.properties"));
            messageProcessingHandlerImpl = props.getProperty("messageProcessingHandlerImpl");

            pluginDir = props.getProperty("pluginDir");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String messageProcessingHandlerImpl;
    public static String pluginDir;

}
