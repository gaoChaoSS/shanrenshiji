package com.zq.kyb.payment.service.wechatpay.impl;

import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.config.WeChatConfig;
import com.zq.kyb.payment.utils.ArithUtil;
import com.zq.kyb.payment.wechatSDK.WxPay;
import com.zq.kyb.payment.wechatSDK.bean.WeChatBuyPost;
import com.zq.kyb.payment.wechatSDK.utils.CommonUtil;
import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiaoke on 2016/8/22.
 * 线下商家扫描线下用户微信钱包中的付款二维码 进行支付
 */
public class WechatMobileServiceImpl extends WechatBaseServiceImpl {
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
        Map configMap = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_WECHAT);


//        <body><![CDATA[H5支付测试]]></body>
//        <out_trade_no><![CDATA[100001_1433009089]]></out_trade_no>
//        <total_fee>1</total_fee>
//        //<notify_url><![CDATA[http://www.doucube.com/weixin/demo/notify_url.php]]></notify_url>
//        <trade_type><![CDATA[WAP]]></trade_type>
//        <device_info>100001</device_info>
//        <appid><![CDATA[wx1d065b0628e21103]]></appid>
//        <mch_id>1237905502</mch_id>
//        <spbill_create_ip><![CDATA[61.129.47.79]]></spbill_create_ip>
//        <nonce_str><![CDATA[gwpdlnn0zlfih21gipjj5z53i7vea8e8]]></nonce_str>
//        <sign><![CDATA[C5A1E210F9B4402D8254F731882F41AC]]></sign>


        //appid=wx2421b1c4370ec43b
        // noncestr=JsyPCgrgQ8f4Ynoa
        // package=WAP
        // prepayid=wx2016091711081486c87154fa0432014357
        // timestamp=1474081695
        // sign=8996F8C8BF7E4681442E6AE0941F1ABD


        Map<String, String> parameter = new HashMap<>();
        parameter.put("body", subject); // 商品描述。
        parameter.put("out_trade_no", orderNo); // 商户系统内部的订单号
        parameter.put("total_fee", total_fee); // 订单总金额
        parameter.put("trade_type", "WAP"); // 交易类型 JSPAI 支付
        parameter.put("spbill_create_ip", "127.0.0.1"); // ip
        parameter.put("notify_url", WeChatConfig.notifiUrl);
        WeChatBuyPost resp = WxPay.getPrepayInfo((String) configMap.get("appId"), (String) configMap.get("partnerId"), (String) configMap.get("partnerKey"), parameter);

        Map<String, Object> params = JSONObject.fromObject(resp);
        Map<String, String> re = new HashMap<>();
        if ("SUCCESS".equals(params.get("return_code"))
                && "SUCCESS".equals(params.get("result_code"))) {
            re.put("appid", (String) configMap.get("appId"));
            re.put("noncestr", CommonUtil.CreateNoncestr());
            re.put("package", "WAP");
            re.put("prepayid", (String) params.get("prepay_id"));
            re.put("timestamp", (System.currentTimeMillis() / 1000) + "");
            re.put("sign", WxPay.getBizSign((String) configMap.get("partnerKey"), re));
        }

        return re;
    }

    @Override
    public Map notifi(String sellerId, Map reqParams) throws Exception {
        return null;
    }

}
