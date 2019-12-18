package com.zq.kyb.payment.service.alipay.impl;


import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.zq.kyb.payment.config.AlipayConfig;
import com.zq.kyb.payment.config.PaymentConfig;
import net.sf.json.JSONObject;

import java.util.Map;

/**
 * Created by xiaoke on 2016/8/19.
 * 手机版网页版支付
 */
public class AlipayMobileWebServiceImpl extends AlipayBaseServiceImpl {

    public static final String dtLong = "yyyyMMddHHmmss";

    @Override
    public Map prepay(String innerTxNo,
                      String orderId,
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

        Map<String, Object> config = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY);
        JSONObject con = new JSONObject();
        con.put("seller_id", PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY).get("partner"));

        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL,
                config.get("appId").toString(), config.get("privateKey").toString(),
                AlipayConfig.FORMAT, AlipayConfig.CHARSET,
                config.get("publicKey_alipay").toString(), AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();

        // 封装请求支付信息
        AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
        model.setOutTradeNo(orderId);
        model.setSubject(subject);
        model.setTotalAmount(totalFee);
        model.setBody(body);
        model.setProductCode("QUICK_WAP_PAY");
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(AlipayConfig.notify_url);
        // 设置同步地址
        alipay_request.setReturnUrl(showUrl);
        // form表单生产
        String form = "";
        try {
            // 调用SDK生成表单
            form = client.pageExecute(alipay_request).getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        JSONObject re = new JSONObject();
        re.put("formStr", form);
        return re;
    }
}
