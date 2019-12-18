package com.zq.kyb.payment.service.wechatpay.impl;

import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.utils.ArithUtil;
import com.zq.kyb.payment.utils.XmlUtils;
import com.zq.kyb.payment.wechatSDK.scanpay.protocol.pay_protocol.ScanPayReqData;
import com.zq.kyb.payment.wechatSDK.scanpay.service.ScanPayService;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by xiaoke on 2016/8/22.
 * 线下商家扫描线下用户微信钱包中的付款二维码 进行支付
 */
public class WechatScanServiceImpl extends WechatBaseServiceImpl {
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
        int total_fee = Double.valueOf(ArithUtil.mul(Double.valueOf(totalFee), 100)).intValue();

        Map configMap = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_WECHAT);

        String ip = "127.0.0.1";
        Date start = new Date();
        start.setTime(System.currentTimeMillis() - 5 * 60000);
        Date end = new Date();
        end.setTime(System.currentTimeMillis() + 3600000);

        SimpleDateFormat yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
        String startTime = yyyyMMddHHmmss.format(start);
        String endTime = yyyyMMddHHmmss.format(end);
        String deviceInfo = ControllerContext.getContext().getDeviceId();
        ScanPayReqData scanPayReqData = new ScanPayReqData(
                (String) configMap.get("appId"), (String) configMap.get("partnerId"),
                (String) configMap.get("partnerKey"),
                authCode,
                subject, "", orderNo, total_fee, deviceInfo, ip, startTime, endTime, ""
        );
        String responseXml = new ScanPayService().request(sellerId, scanPayReqData);
        Logger.getLogger(this.getClass()).info("response Xml: " + responseXml);
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

    @Override
    public Map notifi(String sellerId, Map reqParams) throws Exception {
        return null;
    }

}
