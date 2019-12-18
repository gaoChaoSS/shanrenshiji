package com.zq.kyb.payment.service.wechatpay.impl;

import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.service.PaymentService;
import com.zq.kyb.payment.utils.XmlUtils;
import com.zq.kyb.payment.wechatSDK.WxPay;
import com.zq.kyb.payment.wechatSDK.bean.WeChatBuyPost;
import com.zq.kyb.payment.wechatSDK.scanpay.protocol.refund_protocol.RefundReqData;
import com.zq.kyb.payment.wechatSDK.scanpay.protocol.refund_query_protocol.RefundQueryReqData;
import com.zq.kyb.payment.wechatSDK.scanpay.service.RefundQueryService;
import com.zq.kyb.payment.wechatSDK.scanpay.service.RefundService;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.Map;

/**
 * Created by xiaoke on 2016/8/23.
 */
public class WechatBaseServiceImpl implements PaymentService {
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
        throw new RuntimeException("not impl");
    }

    public Map query(String sellerId, String payId) throws Exception {
        return queryByType(sellerId,payId,PaymentConfig.PAY_TYPE_WECHAT);
    }

    public Map queryByType(String sellerId, String payId,int payType) throws Exception {
        Map configMap = PaymentConfig.getPayConfig(sellerId, payType);

        // 调用 微信 api 查询订单状态
        String appId = (String) configMap.get("appId");
        String partnerId = (String) configMap.get("partnerId");
        String partnerKey = (String) configMap.get("partnerKey");
        WeChatBuyPost postData = WxPay.queryOrderInfo(appId, partnerId, partnerKey, payId);
        boolean isSuccess = false;

        if ("SUCCESS".equals(postData.getReturn_code()) && "SUCCESS".equals(postData.getResult_code())) {
            if (postData.getTrade_state() != null) {
                // trade_state 为 SUCCESS 表示成功，其它为失败
                if ("SUCCESS".equals(postData.getTrade_state())) {
                    //return true;
                    // oi.setPayStatus(re);
                    // ServiceUtil.saveEntity(oi);
                    isSuccess = true;
                }
            }
        }
        JSONObject re = JSONObject.fromObject(postData);
        re.put("isSuccess", isSuccess);
        return re;
    }

    @Override
    public Map notifi(String sellerId,Map reqParams) throws Exception {
        Map conf = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_WECHAT);
        boolean temp = WxPay.verifySign((String) conf.get("partnerKey"), reqParams);

        if (!temp) {
            throw new RuntimeException("校验支付error！");
        }

        reqParams.put("isSuccess", "SUCCESS".equals(reqParams.get("return_code")) && "SUCCESS".equals(reqParams.get("result_code")));
        Logger.getLogger(this.getClass()).info("\n\n\n----------------------微信通知：reqParams："+JSONObject.fromObject(reqParams)+"\n\n\n");
        return reqParams;
    }

    /**
     * 微信退款
     *
     * @param sellerId
     * @param reqParams
     * @return
     * @throws Exception
     */
    @Override
    public Map refund(String sellerId, Map reqParams) throws Exception {
        Map configMap = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_WECHAT);
        String appId = (String) configMap.get("appId");
        String partnerId = (String) configMap.get("partnerId");
        String partnerKey = (String) configMap.get("partnerKey");
        String opUserId = (String) configMap.get("partnerId");

        String transactionId = null;
        String outTradeNo = (String) reqParams.get("outTradeNo");
        String deviceInfo = null;
        String outRefundNo = (String) reqParams.get("outRefundNo");
        int totalFee = (Integer) reqParams.get("totalFee");
        int refundFee = (Integer) reqParams.get("refundFee");
        String refundFeeType = null;
        RefundReqData req = new RefundReqData(appId, partnerId, partnerKey, transactionId, outTradeNo, deviceInfo, outRefundNo, totalFee, refundFee, opUserId, refundFeeType);
        RefundService r = new RefundService();
        String responseXml = r.request(sellerId, req);
        Map<String, Object> params = XmlUtils.xmlToMap(responseXml);
        Logger.getLogger(this.getClass()).info("response: " + params);

        boolean isSuccess = false;
        if ("SUCCESS".equals(params.get("return_code"))
                && "SUCCESS".equals(params.get("result_code"))) {
            isSuccess = true;
        }
        params.put("isSuccess", isSuccess);
        return params;
    }

    /**
     * 微信退款查询
     *
     * @param sellerId
     * @param payId
     * @param returnId
     * @return
     * @throws Exception
     */
    @Override
    public Map refundQuery(String sellerId, String payId, String returnId) throws Exception {
        Map configMap = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_WECHAT);
        String appId = (String) configMap.get("appId");
        String partnerId = (String) configMap.get("partnerId");
        String partnerKey = (String) configMap.get("partnerKey");
        String tId = null;
        String outTradeNo = payId;
        String deviceInfo = null;
        String outRefundNo = returnId;
        String refundId = null;
        RefundQueryReqData req = new RefundQueryReqData(appId, partnerId, partnerKey, tId, outTradeNo, deviceInfo, outRefundNo, refundId);
        RefundQueryService r = new RefundQueryService();
        String responseXml = r.request(sellerId, req);
        Map<String, Object> params = XmlUtils.xmlToMap(responseXml);
        Logger.getLogger(this.getClass()).info("response: " + params);

        boolean isSuccess = false;
        if ("SUCCESS".equals(params.get("return_code"))
                && "SUCCESS".equals(params.get("result_code"))) {
            isSuccess = true;
        }
        params.put("isSuccess", isSuccess);
        return params;
    }
}
