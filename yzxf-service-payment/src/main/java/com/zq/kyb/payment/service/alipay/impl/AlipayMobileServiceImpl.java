//package com.zq.kyb.payment.service.alipay.impl;
//
//import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
//import com.zq.kyb.payment.action.PayAction;
//import com.zq.kyb.payment.alipaySDK.AlipayCore;
//import com.zq.kyb.payment.alipaySDK.sign.MD5;
//import com.zq.kyb.payment.alipaySDK.sign.RSA;
//import com.zq.kyb.payment.config.AlipayConfig;
//import com.zq.kyb.payment.config.PaymentConfig;
//import com.zq.kyb.payment.utils.HttpProtocolHandler;
//import com.zq.kyb.payment.utils.HttpRequest;
//import com.zq.kyb.payment.utils.HttpResponse;
//import com.zq.kyb.payment.utils.HttpResultType;
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.dom4j.Document;
//import org.dom4j.DocumentHelper;
//
//import java.net.URLDecoder;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * Created by xiaoke on 2016/8/19.
// * 手机版网页版支付
// */
//public class AlipayMobileServiceImpl extends AlipayBaseServiceImpl {
//
//    public static final String dtLong = "yyyyMMddHHmmss";
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
//        // 支付宝网关地址
//        String ALIPAY_GATEWAY_NEW = "http://wappaygw.alipay.com/service/rest.htm?";
//        // //////////////////////////////////调用授权接口alipay.wap.trade.create.direct获取授权码token//////////////////////////////////////
//        // 返回格式
//        String format = "xml";
//        // 必填，不需要修改
//        // 返回格式
//        String v = "2.0";
//        // 服务器异步通知页面路径
//        String notify_url = AlipayConfig.notifiUrl;
//        // 需http://格式的完整路径，不能加?id=123这类自定义参数
//        // 页面跳转同步通知页面路径
//
//        // 需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
//        // 操作中断返回地址
//        // String merchant_url = Constants.BASE_URL + "/m/pages/buy/orderView.jsp?orderNo=" + orderNo;
//        // 用户付款中途退出返回商户的地址。需http://格式的完整路径，不允许加?id=123这类自定义参数
//
//        Map payConfig = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY);
//        // 卖家支付宝帐户
//        String seller_email = (String) payConfig.get("seller_email");
//        String key = (String) payConfig.get("key");
//
//        // 必填
//        // 请求业务参数详细
//        String req_dataToken = "<direct_trade_create_req><notify_url>" + notify_url + "</notify_url><call_back_url>" + showUrl + "</call_back_url><seller_account_name>" + seller_email
//                + "</seller_account_name><out_trade_no>" + innerTxNo + "</out_trade_no><subject>" + subject + "</subject><total_fee>" + totalFee + "</total_fee><merchant_url>" + showUrl
//                + "</merchant_url></direct_trade_create_req>";
//        // 必填
//
//        // ////////////////////////////////////////////////////////////////////////////////
//
//        // 把请求参数打包成数组
//        Map<String, String> sParaTempToken = new HashMap<String, String>();
//        sParaTempToken.put("service", "alipay.wap.trade.create.direct");
//        sParaTempToken.put("partner", (String) payConfig.get("partner"));
//        sParaTempToken.put("_input_charset", AlipayConfig.input_charset);
//        sParaTempToken.put("sec_id", AlipayConfig.sign_type);
//        sParaTempToken.put("format", format);
//        sParaTempToken.put("v", v);
//        sParaTempToken.put("req_id", orderNo);
//        sParaTempToken.put("req_data", req_dataToken);
//
//        // 建立请求
//        // String sHtmlTextToken = AlipayWapSubmit.buildRequest(ALIPAY_GATEWAY_NEW, "", "", sParaTempToken);
//        // 待请求参数数组
//        Map<String, String> sPara = buildRequestPara(key, sParaTempToken);
//        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
//        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
//        // 设置编码集
//        request.setCharset(AlipayConfig.input_charset);
//
//        request.setParameters(generatNameValuePair(sPara));
//        request.setUrl(ALIPAY_GATEWAY_NEW + "_input_charset=" + AlipayConfig.input_charset);
//
//        HttpResponse response = httpProtocolHandler.execute(request, "", "");
//        if (response == null) {
//            // return null;
//        }
//        String sHtmlTextToken = response.getStringResult();
//
//        // URLDECODE返回的信息
//        sHtmlTextToken = URLDecoder.decode(sHtmlTextToken, AlipayConfig.input_charset);
//        // 获取token
//        String request_token = getRequestToken(key, sHtmlTextToken);
//        // out.println(request_token);
//
//        // //////////////////////////////////根据授权码token调用交易接口alipay.wap.auth.authAndExecute//////////////////////////////////////
//
//        // 业务详细
//        String req_data = "<auth_and_execute_req><request_token>" + request_token + "</request_token></auth_and_execute_req>";
//        // 必填
//
//        // 把请求参数打包成数组
//        Map<String, String> sParaTemp = new HashMap<String, String>();
//        sParaTemp.put("service", "alipay.wap.auth.authAndExecute");
//        sParaTemp.put("partner", (String) payConfig.get("partner"));
//        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
//        sParaTemp.put("sec_id", AlipayConfig.sign_type);
//        sParaTemp.put("format", format);
//        sParaTemp.put("v", v);
//        sParaTemp.put("req_data", req_data);
//
//        // 签名
//        Map<String, String> payReqParams = buildRequestPara(key, sParaTemp);
//        Map payRecord = new HashMap<>();
//        payRecord.put("memberNo", memberNo);
//        payRecord.put("orderNo", orderNo);
//        payRecord.put("innerTxNo", innerTxNo);
//        payRecord.put("reqParams", sParaTemp.toString());
//        payRecord.put("returnParams", payReqParams);
//        payRecord.put("price", totalFee);
//        //payRecord.put("payType", PayType.ALIPAY_MOBILE.toString());
//        payRecord.put("memberNo", memberNo);
//        MysqlDaoImpl.getInstance().saveOrUpdate("PayRecord", payRecord);
//        return payReqParams;
//    }
//
//    /**
//     * MAP类型数组转换成NameValuePair类型
//     *
//     * @param properties MAP类型数组
//     * @return NameValuePair类型数组
//     */
//    private static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
//        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
//        int i = 0;
//        for (Map.Entry<String, String> entry : properties.entrySet()) {
//            nameValuePair[i++] = new BasicNameValuePair(entry.getKey(), entry.getValue());
//        }
//
//        return nameValuePair;
//    }
//
//    /**
//     * 解析远程模拟提交后返回的信息，获得token
//     *
//     * @param key
//     * @param text 要解析的字符串
//     * @return 解析结果
//     * @throws Exception
//     */
//    public static String getRequestToken(String key, String text) throws Exception {
//        String request_token = "";
//        // 以“&”字符切割字符串
//        String[] strSplitText = text.split("&");
//        // 把切割后的字符串数组变成变量与数值组合的字典数组
//        Map<String, String> paraText = new HashMap<String, String>();
//        for (int i = 0; i < strSplitText.length; i++) {
//
//            // 获得第一个=字符的位置
//            int nPos = strSplitText[i].indexOf("=");
//            // 获得字符串长度
//            int nLen = strSplitText[i].length();
//            // 获得变量名
//            String strKey = strSplitText[i].substring(0, nPos);
//            // 获得数值
//            String strValue = strSplitText[i].substring(nPos + 1, nLen);
//            // 放入MAP类中
//            paraText.put(strKey, strValue);
//        }
//
//        if (paraText.get("res_data") != null) {
//            String res_data = paraText.get("res_data");
//            // 解析加密部分字符串（RSA与MD5区别仅此一句）
//            if (AlipayConfig.sign_type.equals("0001")) {
//                res_data = RSA.decrypt(res_data, key, AlipayConfig.input_charset);
//            }
//
//            // token从res_data中解析出来（也就是说res_data中已经包含token的内容）
//            Document document = DocumentHelper.parseText(res_data);
//            request_token = document.selectSingleNode("//direct_trade_create_res/request_token").getText();
//        }
//        return request_token;
//    }
//
//    /**
//     * 生成要请求给支付宝的参数数组
//     *
//     * @param sParaTemp 请求前的参数数组
//     * @return 要请求的参数数组
//     */
//    private static Map<String, String> buildRequestPara(String key, Map<String, String> sParaTemp) {
//        // 除去数组中的空值和签名参数
//        Map<String, String> sPara = AlipayCore.paraFilter(sParaTemp);
//        // 生成签名结果
//        String mysign = buildRequestMysign(key, sPara);
//
//        // 签名结果与签名方式加入请求提交参数组中
//        sPara.put("sign", mysign);
//        if (!sPara.get("service").equals("alipay.wap.trade.create.direct") && !sPara.get("service").equals("alipay.wap.auth.authAndExecute")) {
//            sPara.put("sign_type", AlipayConfig.sign_type);
//        }
//
//        return sPara;
//    }
//
//    /**
//     * 生成签名结果
//     *
//     * @param sPara 要签名的数组
//     * @return 签名结果字符串
//     */
//    private static String buildRequestMysign(String key, Map<String, String> sPara) {
//        String prestr = AlipayCore.createLinkString(sPara); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
//        String mysign = "";
//        if (AlipayConfig.sign_type.equals("MD5")) {
//            mysign = MD5.sign(prestr, key, AlipayConfig.input_charset);
//        }
//        if (AlipayConfig.sign_type.equals("0001")) {
//            mysign = RSA.sign(prestr, key, AlipayConfig.input_charset);
//        }
//        return mysign;
//    }
//
//
//}
