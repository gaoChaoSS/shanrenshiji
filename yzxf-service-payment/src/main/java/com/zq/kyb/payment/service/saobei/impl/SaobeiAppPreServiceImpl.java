package com.zq.kyb.payment.service.saobei.impl;

import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.utils.MD5;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * 扫呗的APP支付统一下单
 */
public class SaobeiAppPreServiceImpl extends SaobeiBaseServiceImpl {

    public Map prepay(
            String innerTxNo,
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
            String openId

    ) throws Exception {

        String fee = String.valueOf(Double.parseDouble(totalFee) * 100).split("\\.")[0];

        Map payConfig = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_SAOBEI_WECHAT);

        String prePay_url = payConfig.get("baseUrl") + "/pay/100/apppay";

        JSONObject jsonParam = getParms(payConfig, payType + "");
        jsonParam.put("service_id", "013");//接口类型
        jsonParam.put("terminal_trace", orderNo);//终端流水号，填写商户系统的订单号
        jsonParam.put("total_fee", fee);//金额，单位分

        String param = "pay_ver=" + jsonParam.get("pay_ver")
                + "&pay_type=" + jsonParam.get("pay_type")
                + "&service_id=" + jsonParam.get("service_id")
                + "&merchant_no=" + jsonParam.get("merchant_no")
                + "&terminal_id=" + jsonParam.get("terminal_id")
                + "&terminal_trace=" + jsonParam.get("terminal_trace")
                + "&terminal_time=" + jsonParam.get("terminal_time")
                + "&total_fee=" + jsonParam.get("total_fee")
                + "&access_token=" + payConfig.get("access_token");

        String sign = MD5.sign(param, "utf-8");
        jsonParam.put("key_sign", sign);
        Logger.getLogger(this.getClass()).info(prePay_url + "");
        String xmlText = tojson(prePay_url, jsonParam.toString());
        JSONObject result = JSONObject.fromObject(xmlText);

        if (!StringUtils.mapValueIsEmpty(result, "return_code") && "01".equals(result.get("return_code").toString())) {
            if (!StringUtils.mapValueIsEmpty(result, "result_code") && "01".equals(result.get("result_code").toString())) {
                result.put("isSuccess", true);
            }
        }
        result = result == null ? new JSONObject() : result;
        return result;
    }
}
