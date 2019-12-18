package com.zq.kyb.payment.service.saobei.impl;

import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.utils.MD5;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 扫呗的JSAPI网页支付
 */
public class SaobeiJsApiServiceImpl extends SaobeiBaseServiceImpl {

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

        String prePay_url = payConfig.get("baseUrl") + "/pay/100/jspay";

        JSONObject jsonParam = getParms(payConfig, payType + "");
        jsonParam.put("service_id", "012");//接口类型
        jsonParam.put("terminal_trace", orderNo);//终端流水号，填写商户系统的订单号
        jsonParam.put("total_fee", fee);//金额，单位分
        jsonParam.put("open_id", openId);

        List<String> list = new ArrayList<>();
        list.add("pay_ver");
        list.add("pay_type");
        list.add("service_id");
        list.add("merchant_no");
        list.add("terminal_id");
        list.add("terminal_trace");
        list.add("terminal_time");
        list.add("total_fee");
        //list.add("open_id");
        //Collections.sort(list);

        String param = "";
        for (String s : list) {
            param += (param.length() == 0 ? "" : "&") + s + "=" + jsonParam.get(s).toString();
        }

        param += "&access_token=" + payConfig.get("access_token");

        Logger.getLogger(this.getClass()).info("-req Url: " + prePay_url);
        Logger.getLogger(this.getClass()).info("-param: " + param);
        String sign = MD5.sign(param, "utf-8");
        Logger.getLogger(this.getClass()).info("-key_sign: " + sign);
        jsonParam.put("key_sign", sign);
        String xmlText = tojson(prePay_url, jsonParam.toString());
        JSONObject result = JSONObject.fromObject(xmlText);

        JSONObject jsapiParam = new JSONObject();
        jsapiParam.put("appId", result.get("appId"));
        jsapiParam.put("timeStamp", result.get("timeStamp"));
        jsapiParam.put("nonceStr", result.get("nonceStr"));
        jsapiParam.put("package", result.get("package_str"));
        jsapiParam.put("signType", result.get("signType"));
        jsapiParam.put("paySign", result.get("paySign"));
        jsapiParam.put("orderNo", innerTxNo);
        result.put("jsApiMap", jsapiParam);

        if (!StringUtils.mapValueIsEmpty(result, "return_code") && "01".equals(result.get("return_code").toString())) {
            if (!StringUtils.mapValueIsEmpty(result, "result_code") && "01".equals(result.get("result_code").toString())) {
                result.put("isSuccess", true);
            }
        }
        result = result == null ? new JSONObject() : result;
        return result;
    }

    public String getOpenIdUrl(String sellerId, String id) throws Exception {
        Map payConfig = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_SAOBEI_WECHAT);
        String access_token = (String) payConfig.get("access_token");
        String url = payConfig.get("baseUrl") + "/wx/jsapi/authopenid";
        String merchant_no = (String) payConfig.get("merchantNo");
        String terminal_no = (String) payConfig.get("terminalId");
        String redirect_uri = (String) payConfig.get("get_openId_uri") + "?_id=" + id;


        String uri = "merchant_no=" + merchant_no
                + "&redirect_uri=" + redirect_uri
                + "&terminal_no=" + terminal_no;
        String param = uri + "&access_token=" + access_token;
        Logger.getLogger(this.getClass()).info(param);

        String sign = MD5.sign(param, "utf-8");
        uri = "merchant_no=" + merchant_no
                + "&redirect_uri=" + URLEncoder.encode(redirect_uri, "utf-8")
                + "&terminal_no=" + terminal_no;
        url += "?" + uri + "&key_sign=" + sign;
        return url;
    }

}
