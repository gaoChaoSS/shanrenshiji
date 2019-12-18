package com.zq.kyb.payment.service.alipay.impl;

import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.utils.OrderInfoUtil2_0;

import java.util.HashMap;
import java.util.Map;

public class AlipayAppServiceImpl extends AlipayBaseServiceImpl {

    public static final String dtLong = "yyyyMMddHHmmss";

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
        String app_private_key = (String) PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY).get("privateKey");
        String app_id = (String) PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY).get("appId");

        Map<String, String> orderMap = OrderInfoUtil2_0.buildOrderParamMap(app_id, true, "付款-普惠生活", totalFee, orderNo);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(orderMap);
        String sign = OrderInfoUtil2_0.getSign(orderMap, app_private_key, true);

        Map<String, Object> map = new HashMap<>();
        String orderStr = orderParam + "&" + sign;
        map.put("orderStr", orderStr);
        return map;
    }


}
