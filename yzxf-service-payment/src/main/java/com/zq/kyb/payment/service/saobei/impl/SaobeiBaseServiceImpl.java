package com.zq.kyb.payment.service.saobei.impl;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.service.PaymentService;
import com.zq.kyb.payment.utils.MD5;
import com.zq.kyb.util.BigDecimalUtil;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 *
 *
 */
public class SaobeiBaseServiceImpl implements PaymentService {
    protected int payType;

    public void setPayType(int payType) {
        this.payType = payType;
    }

    @Override
    public Map prepay(String innerTxNo, String orderNo, String subject, String totalFee, String body, String memberNo, String discountPrice, String sellerId, String storeId, String showUrl, String authCode, String openId) throws Exception {
        throw new RuntimeException("not impl");
    }

    public Map query(String sellerId, String payId) throws Exception {
        Map payConfig = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_SAOBEI_WECHAT);

        Map<String, Object> payLog = MysqlDaoImpl.getInstance().findById2Map("Pay", payId, null, null);
        JSONObject re = new JSONObject();
        if (payLog == null) {
            return re;
        }

        String Query_url = payConfig.get("baseUrl") + "/pay/100/query";

        JSONObject jsonParam = getParms(payConfig, payLog.get("payType").toString());
        jsonParam.put("service_id", "020");//接口类型
        jsonParam.put("terminal_trace", new java.util.Random().nextInt(100000) + "");
        jsonParam.put("out_trade_no", payLog.get("trId"));

        String param = "pay_ver=" + jsonParam.get("pay_ver")
                + "&pay_type=" + jsonParam.get("pay_type")
                + "&service_id=" + jsonParam.get("service_id")
                + "&merchant_no=" + jsonParam.get("merchant_no")
                + "&terminal_id=" + jsonParam.get("terminal_id")
                + "&terminal_trace=" + jsonParam.get("terminal_trace")
                + "&terminal_time=" + jsonParam.get("terminal_time")
                + "&out_trade_no=" + jsonParam.get("out_trade_no")
                + "&access_token=" + payConfig.get("access_token");
//                + "&total_fee=" + jsonParam.get("total_fee")
//                + "&open_id=" + payConfig.get("open_id");

        String sign = MD5.sign(param, "utf-8");
        jsonParam.put("key_sign", sign);
        String xmlText = tojson(Query_url, jsonParam.toString());

        JSONObject result = JSONObject.fromObject(xmlText);
        if (!StringUtils.mapValueIsEmpty(result, "return_code") && "01".equals(result.get("return_code").toString())) {
            if (!StringUtils.mapValueIsEmpty(result, "result_code") && "01".equals(result.get("result_code").toString())) {
                Double total_fee = BigDecimalUtil.divide(Double.valueOf(result.get("total_fee").toString()), 100.0);
                result.put("totalAmount", BigDecimalUtil.fixDoubleNum2(total_fee));
                result.put("isSuccess", true);
            }
        }
        return result;
    }

    public Map queryByOrderId(String sellerId,String trId,String payType) throws Exception {
        Map payConfig = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_SAOBEI_WECHAT);
        String Query_url = payConfig.get("baseUrl") + "/pay/100/query";

        JSONObject jsonParam = getParms(payConfig, payType);
        jsonParam.put("service_id", "020");//接口类型
        jsonParam.put("terminal_trace", new java.util.Random().nextInt(100000) + "");
        jsonParam.put("out_trade_no", trId);

        String param = "pay_ver=" + jsonParam.get("pay_ver")
                + "&pay_type=" + jsonParam.get("pay_type")
                + "&service_id=" + jsonParam.get("service_id")
                + "&merchant_no=" + jsonParam.get("merchant_no")
                + "&terminal_id=" + jsonParam.get("terminal_id")
                + "&terminal_trace=" + jsonParam.get("terminal_trace")
                + "&terminal_time=" + jsonParam.get("terminal_time")
                + "&out_trade_no=" + jsonParam.get("out_trade_no")
                + "&access_token=" + payConfig.get("access_token");
//                + "&total_fee=" + jsonParam.get("total_fee")
//                + "&open_id=" + payConfig.get("open_id");

        String sign = MD5.sign(param, "utf-8");
        jsonParam.put("key_sign", sign);
        String xmlText = tojson(Query_url, jsonParam.toString());

        JSONObject result = JSONObject.fromObject(xmlText);
        if (!StringUtils.mapValueIsEmpty(result, "return_code") && "01".equals(result.get("return_code").toString())) {
            if (!StringUtils.mapValueIsEmpty(result, "result_code") && "01".equals(result.get("result_code").toString())) {
                Double total_fee = BigDecimalUtil.divide(Double.valueOf(result.get("total_fee").toString()), 100.0);
                result.put("totalAmount", BigDecimalUtil.fixDoubleNum2(total_fee));
                result.put("isSuccess", true);
            }
        }
        return result;
    }

    protected static JSONObject getParms(Map payConfig, String payType) {
        String pay_type = null;
        if ("13".equals(payType)) {
            pay_type = "010";
        }
        if ("14".equals(payType)) {
            pay_type = "020";
        }
        if ("15".equals(payType)) {
            pay_type = "060";
        }

        JSONObject jsonParam = new JSONObject();
        jsonParam.put("pay_ver", payConfig.get("payVer"));//版本号，当前版本100
        jsonParam.put("pay_type", pay_type);//请求类型，010微信，020 支付宝，060qq钱包，080京东钱包
        jsonParam.put("merchant_no", payConfig.get("merchantNo"));//商户号
        jsonParam.put("terminal_id", payConfig.get("terminalId"));//终端号,TODO 终端号需要单独管理
        jsonParam.put("terminal_time", new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));//终端交易时间，yyyyMMddHHmmss，全局统一时间格式
        return jsonParam;
    }

    @Override
    public Map notifi(String sellerId, Map reqParams) throws Exception {
        return null;
    }

    @Override
    public Map refund(String sellerId, Map reqParams) throws Exception {
//        p.put("outTradeNo", pay.get("trId"));
//        p.put("totalFee", totalFee);
//        p.put("refundFee", refundFee);
//        p.put("outRefundNo", returnId);
//        p.put("payType", payType);
        Map payConfig = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_SAOBEI_WECHAT);

        JSONObject jsonParam = getParms(payConfig, reqParams.get("payType").toString());
        jsonParam.put("service_id", "030");//接口类型
        jsonParam.put("terminal_trace", reqParams.get("outRefundNo"));

        jsonParam.put("refund_fee", reqParams.get("refundFee"));
        jsonParam.put("out_trade_no", reqParams.get("outTradeNo"));

        String param = "pay_ver=" + jsonParam.get("pay_ver")
                + "&pay_type=" + jsonParam.get("pay_type")
                + "&service_id=" + jsonParam.get("service_id")
                + "&merchant_no=" + jsonParam.get("merchant_no")
                + "&terminal_id=" + jsonParam.get("terminal_id")
                + "&terminal_trace=" + jsonParam.get("terminal_trace")
                + "&terminal_time=" + jsonParam.get("terminal_time")
                + "&refund_fee=" + jsonParam.get("refund_fee")
                + "&out_trade_no=" + jsonParam.get("out_trade_no")
                + "&access_token=" + payConfig.get("access_token");
        String sign = MD5.sign(param, "utf-8");
        jsonParam.put("key_sign", sign);
        String xmlText = tojson(payConfig.get("baseUrl") + "/pay/100/refund", jsonParam.toString());
        Logger.getLogger(this.getClass()).info("---- sao bei refund xmlText: " + xmlText);

        JSONObject result = JSONObject.fromObject(xmlText);
        if (!StringUtils.mapValueIsEmpty(result, "return_code") && "01".equals(result.get("return_code").toString())) {
            //if (!StringUtils.mapValueIsEmpty(result, "result_code") && "01".equals(result.get("result_code").toString())) {
            result.put("isSuccess", true);
            //}
        }
        return result;
    }

    @Override
    public Map refundQuery(String sellerId, String payId, String returnId) throws Exception {
        return query(sellerId, payId);
    }


    public String tojson(String gateway, String jsonParam) throws Exception {
        Logger.getLogger(this.getClass()).info("--url: " + gateway);
        String xmlText = "";

        CloseableHttpClient httpclient = HttpClients.custom().build();
        try {

            HttpPost httpPost = new HttpPost(gateway);
            httpPost.addHeader("charset", "UTF-8");
            Logger.getLogger(this.getClass()).info(jsonParam.toString());
            StringEntity stentity = new StringEntity(jsonParam.toString(), "utf-8");//解决中文乱码问题
            stentity.setContentEncoding("UTF-8");
            stentity.setContentType("application/json");
            httpPost.setEntity(stentity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {

                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                    String text;
                    while ((text = bufferedReader.readLine()) != null) {
                        xmlText = xmlText + text;
                    }
                }

                EntityUtils.consume(entity);
                Logger.getLogger(this.getClass()).info(xmlText);
            } finally {

                response.close();
            }
        } finally {

            httpclient.close();
        }

        return xmlText;
    }
}
