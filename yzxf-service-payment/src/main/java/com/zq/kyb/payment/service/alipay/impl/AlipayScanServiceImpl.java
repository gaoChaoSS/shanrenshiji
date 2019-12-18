//package com.zq.kyb.payment.service.alipay.impl;
//
//import com.alipay.api.response.AlipayTradePayResponse;
//import com.zq.kyb.core.ctrl.ControllerContext;
//import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
//import com.zq.kyb.payment.alipaySDK.ToAlipayBarTradePay;
//import com.zq.kyb.payment.config.AlipayConfig;
//import com.zq.kyb.util.BigDecimalUtil;
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//import org.apache.log4j.Logger;
//
//import java.text.SimpleDateFormat;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by xiaoke on 2016/8/19.
// * 商家通过扫描线下买家支付宝中的条码、二维码等方式将买家的交易资金直接打入卖家支付宝账户
// */
//public class AlipayScanServiceImpl extends AlipayBaseServiceImpl {
//    @Override
//    public Map prepay(
//            String innerTxNo,
//            String orderNo,
//            String subject,
//            String totalFee,
//            String body,
//            String memberNo,
//            String discountPrice,
//            String sellerId,
//            String storeId,
//            String showUrl,
//            String authCode,
//            String openId) throws Exception {
//
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String time_expire = sdf.format(System.currentTimeMillis() + 24 * 60
//                * 60 * 1000);
//        JSONObject params = new JSONObject();
//        params.put("out_trade_no", orderNo);
//        params.put("scene", "bar_code");
//        params.put("auth_code", authCode);
//        params.put("subject", subject);
////        params.put("store_id", storeId);
////        params.put("timeout_express", "3m");
//        params.put("total_amount", BigDecimalUtil.fixDoubleNum2(Double.valueOf(totalFee)));
//
//
//        AlipayTradePayResponse res = ToAlipayBarTradePay.barPay(sellerId, params, AlipayConfig.notifiUrl);
//        JSONObject re = JSONObject.fromObject(res);
//        Logger.getLogger(this.getClass()).info("response: " + re);
//        boolean isSuccess = false;
//        if (null != res && res.isSuccess()) {
//            if ("10000".equals(res.getCode())) {
//                isSuccess = true;
//            }
//        }
//        re.put("isSuccess", isSuccess);
//        return re;
//    }
//
//}
