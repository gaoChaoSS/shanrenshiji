//package com.zq.kyb.payment.service.alipay.impl;
//
//import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
//import com.zq.kyb.payment.action.PayAction;
//import com.zq.kyb.payment.alipaySDK.AlipaySubmit;
//import com.zq.kyb.payment.config.AlipayConfig;
//import com.zq.kyb.payment.config.PaymentConfig;
//import net.sf.json.JSONObject;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by xiaoke on 2016/8/19.
// * 电脑网页版支付
// */
//public class AlipayPCServiceImpl extends AlipayBaseServiceImpl {
//
//    @Override
//    public Map prepay(String innerTxNo,
//                      String orderNo,
//                      String subject,
//                      String totalFee,
//                      String body,
//                      String memberNo,
//                      String discountPrice,
//                      String sellerId,
//                      String storeId,
//                      String showUrl,
//                      String authCode,
//                      String openId) throws Exception {
//
//        // 支付类型
//        String payment_type = "1";
//        // 必填，不能修改
//        // 卖家支付宝帐户
//        Map payConfig = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY);
//        String seller_email = (String) payConfig.get("seller_email");
//        String key = (String) payConfig.get("key");
//
//        String notify_url = AlipayConfig.notifiUrl;
//        //String return_url = AlipayConfig.returnUrl;
//
//        // 防钓鱼时间戳
//        String anti_phishing_key = "";
//        // 若要使用请调用类文件submit中的query_timestamp函数
//        // 客户端的IP地址
//        String exter_invoke_ip = "";
//        // 非局域网的外网IP地址，如：221.0.0.1
//
//        // 把请求参数打包成数组
//        Map<String, String> sParaTemp = new HashMap<String, String>();
//        sParaTemp.put("service", "create_direct_pay_by_user");
//        sParaTemp.put("partner", (String) payConfig.get("partner"));
//        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
//        sParaTemp.put("payment_type", payment_type);
//        sParaTemp.put("notify_url", notify_url);
//        sParaTemp.put("return_url", showUrl);
//        sParaTemp.put("seller_email", seller_email);
//        sParaTemp.put("out_trade_no", orderNo);
//        sParaTemp.put("subject", subject);
//        sParaTemp.put("total_fee", totalFee);
//        sParaTemp.put("body", body == null ? subject : body);
//        sParaTemp.put("show_url", showUrl);
//        sParaTemp.put("anti_phishing_key", anti_phishing_key);
//        sParaTemp.put("exter_invoke_ip", exter_invoke_ip);
//
//        String formStr = AlipaySubmit.buildRequest(key, sParaTemp);
//        JSONObject re = new JSONObject();
//        re.put("formStr", formStr);
//        return re;
//    }
//
//}
