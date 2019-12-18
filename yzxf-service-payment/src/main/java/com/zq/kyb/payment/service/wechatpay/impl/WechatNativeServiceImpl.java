package com.zq.kyb.payment.service.wechatpay.impl;

import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.config.WeChatConfig;
import com.zq.kyb.payment.utils.ArithUtil;
import com.zq.kyb.payment.wechatSDK.WxPay;
import com.zq.kyb.payment.wechatSDK.bean.WeChatBuyPost;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaoke on 2016/8/22.
 * 用户使用手机微信 扫描其它系统中微信产生的二维码 完成支付
 */
public class WechatNativeServiceImpl extends WechatBaseServiceImpl {
    @Override
    public Map prepay(String innerTxNo,
                      String orderNo,
                      String subject,
                      String totalFee,
                      String body,
                      String memberNo,
                      String discountPrice,
                      String sellerId,
                      String storeId,
                      String showUrl,
                      String authCode,
                      String openId) throws Exception {
        String total_fee = String.valueOf(Double.valueOf(ArithUtil.mul(Double.valueOf(totalFee), 100)).intValue());
        // 对商品名截取, 去除空格
        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put("body", subject); // 商品描述。
        parameter.put("total_fee", total_fee); // 订单总金额,注意：单位为分
        parameter.put("out_trade_no", orderNo); // 商户系统内部的订单号
        parameter.put("trade_type", "NATIVE"); // 交易类型 本地支付
        parameter.put("spbill_create_ip", "127.0.0.1"); // ip
        parameter.put("notify_url", WeChatConfig.notifiUrl);
        // trade_type 为 NATIVE 时 product_id 是必填项
        parameter.put("product_id", orderNo);

        //公共参数
        Map configMap = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_WECHAT);

        if (configMap == null || configMap.size() == 0) {
            throw new UserOperateException(400, "支付参数未配置, 请联系管理员!");
        }

        //<xml>
        // <sign><![CDATA[298FA4BD9DD2DFCFAFDC3C0635D9A6B3]]></sign>
        // <mch_id><![CDATA[10029249]]></mch_id>
        // <body><![CDATA[31609170000090]]></body>
        // <product_id><![CDATA[31609170000090]]></product_id>
        // <spbill_create_ip><![CDATA[222.212.202.30]]></spbill_create_ip>
        // <total_fee>1</total_fee>
        // <notify_url><![CDATA[http://www.youlai01.com/front/api/web/WechatNative/payNotifi]]></notify_url>
        // <appid><![CDATA[wx2da9a67909bc7185]]></appid>
        // <nonce_str><![CDATA[Xol0OYpjhCgNkmLu]]></nonce_str>
        // <out_trade_no><![CDATA[31609170000090_13083]]></out_trade_no>
        // <trade_type><![CDATA[NATIVE]]></trade_type>
        // </xml>

        WeChatBuyPost resp = WxPay.getPrepayInfo((String) configMap.get("appId"), (String) configMap.get("partnerId"), (String) configMap.get("partnerKey"), parameter);

        Map<String, Object> returnParams = JSONObject.fromObject(resp);

        if ("SUCCESS".equals(returnParams.get("return_code"))
                && "SUCCESS".equals(returnParams.get("result_code"))) {
            return returnParams;
        } else {
            throw new UserOperateException(400, "获取微信支付二维码失败");
        }
    }

    @Override
    public Map notifi(String sellerId, Map reqParams) throws Exception {
        return null;
    }

}
