package com.zq.kyb.payment.service.wechatpay.impl;

/**
 * Created by liaohuilin on 17/4/7.
 */

import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.config.WeChatConfig;
import com.zq.kyb.payment.utils.ArithUtil;
import com.zq.kyb.payment.wechatSDK.WxPay;
import com.zq.kyb.payment.wechatSDK.bean.WeChatBuyPost;
import com.zq.kyb.payment.wechatSDK.utils.CommonUtil;
import net.sf.json.JSONObject;

import java.util.*;

public class WechatAppServiceImpl extends WechatBaseServiceImpl {

    public Map prepay(String innerTxNo,
                      String payFrom,
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
        Map configMap = PaymentConfig.getPayConfig(sellerId,
                "member".equals(payFrom)?PaymentConfig.PAY_TYPE_WECHAT_MEMBER_APP:PaymentConfig.PAY_TYPE_WECHAT_SELLER_APP);

        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put("body", subject); // 商品描述。
        parameter.put("total_fee", total_fee); // 订单总金额
        parameter.put("out_trade_no", orderNo); // 商户系统内部的订单号
        parameter.put("trade_type", "APP"); // 交易类型 JSPAI 支付
        parameter.put("notify_url", WeChatConfig.notifiUrl);
        parameter.put("spbill_create_ip", "127.0.0.1");//ip
        WeChatBuyPost responseData = WxPay.getPrepayInfo((String) configMap.get("appId"), (String) configMap.get("partnerId"), (String) configMap.get("partnerKey"), parameter);
        String prepay_id = null;
        if ("SUCCESS".equals(responseData.getReturn_code()) && "SUCCESS".equals(responseData.getResult_code())) {
            prepay_id = responseData.getPrepay_id();
        } else {
            throw new UserOperateException(400, "获取微信支付数据出错");
        }

        JSONObject jsapiParam = new JSONObject();
        jsapiParam.put("appid", configMap.get("appId"));
        jsapiParam.put("partnerid", (String) configMap.get("partnerId"));
        jsapiParam.put("prepayid",prepay_id);
        jsapiParam.put("timestamp", (System.currentTimeMillis() / 1000) + "");
        jsapiParam.put("noncestr", CommonUtil.CreateNoncestr());
        jsapiParam.put("package", "Sign=WXPay");
        jsapiParam.put("sign", WxPay.getBizSign((String) configMap.get("partnerKey"), jsapiParam));
        jsapiParam.put("orderNo", innerTxNo);

        return jsapiParam;
    }




}