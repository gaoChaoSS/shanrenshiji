package com.zq.kyb.payment.service.wechatpay.impl;

import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.config.WeChatConfig;
import com.zq.kyb.payment.utils.ArithUtil;
import com.zq.kyb.payment.wechatSDK.SDKRuntimeException;
import com.zq.kyb.payment.wechatSDK.WxPay;
import com.zq.kyb.payment.wechatSDK.bean.WeChatBuyPost;
import com.zq.kyb.payment.wechatSDK.utils.CommonUtil;
import com.zq.kyb.payment.wechatSDK.utils.MD5SignUtil;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Created by xiaoke on 2016/8/22.
 * 用户在微信公众号内 直接发起支付请求
 */
public class WechatJsApiServiceImpl extends WechatBaseServiceImpl {
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


        Map<String, String> parameter = new HashMap<String, String>();
        parameter.put("body", subject); // 商品描述。
        parameter.put("total_fee", total_fee); // 订单总金额
        parameter.put("out_trade_no", orderNo); // 商户系统内部的订单号
        parameter.put("trade_type", "JSAPI"); // 交易类型 JSPAI 支付
        parameter.put("notify_url", WeChatConfig.notifiUrl);
        // trade_type 为 JSAPI 时 openid 是必填项
        parameter.put("openid", openId);
        WeChatBuyPost responseData = WxPay.getPrepayInfo((String) configMap.get("appId"), (String) configMap.get("partnerId"), (String) configMap.get("partnerKey"), parameter);
        String prepay_id = null;
        if ("SUCCESS".equals(responseData.getReturn_code()) && "SUCCESS".equals(responseData.getResult_code())) {
            prepay_id = responseData.getPrepay_id();
        } else {
            throw new UserOperateException(400, "获取微信支付数据出错");
        }

        JSONObject jsapiParam = new JSONObject();
        jsapiParam.put("appId", configMap.get("appId"));
        jsapiParam.put("timeStamp", (System.currentTimeMillis() / 1000) + "");
        jsapiParam.put("nonceStr", CommonUtil.CreateNoncestr());
        jsapiParam.put("package", "prepay_id=" + prepay_id);
        jsapiParam.put("signType", "MD5");
        jsapiParam.put("paySign", getBizSign((String) configMap.get("partnerKey"), jsapiParam));
        jsapiParam.put("orderNo", innerTxNo);

//        Map payRecord = new HashMap<>();
//        payRecord.put("_id", UUID.randomUUID().toString());
//        payRecord.put("memberNo", memberNo);
//        payRecord.put("orderNo", orderNo);
//        payRecord.put("innerTxNo", innerTxNo);
//        payRecord.put("reqParams", parameter.toString());
//        payRecord.put("returnParams", jsapiParam.toString());
//        payRecord.put("price", totalFee);
//        payRecord.put("memberNo", memberNo);
//        MysqlDaoImpl.getInstance().saveOrUpdate("PayRecord", payRecord);

//        Logger.getLogger(this.getClass()).info(out_trade_no + "[JsApi]: memberNo=" + r.getMemberNo());
//        Logger.getLogger(this.getClass()).info(out_trade_no + "[JsApi]: reqParams=" + string);
//        Logger.getLogger(this.getClass()).info(out_trade_no + "[JsApi]: price=" + r.getPrice() + "\n\n");

        return jsapiParam;
    }

    @Override
    public Map notifi(String sellerId, Map reqParams) throws Exception {
        return null;
    }

    private static String getBizSign(String partnerKey, Map<String, String> bizObj) throws SDKRuntimeException {
        HashMap<String, String> bizParameters = new HashMap<String, String>();

        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(bizObj.entrySet());

        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        for (int i = 0; i < infoIds.size(); i++) {
            Map.Entry<String, String> item = infoIds.get(i);
            if (StringUtils.isNotEmpty(item.getKey())) {
                bizParameters.put(item.getKey(), item.getValue());
            }
        }
        String bizString = CommonUtil.FormatBizQueryParaMap(bizParameters, false);
        return MD5SignUtil.Sign(bizString, partnerKey);
    }

}
