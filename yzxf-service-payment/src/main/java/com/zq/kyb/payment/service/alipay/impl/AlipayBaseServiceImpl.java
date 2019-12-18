package com.zq.kyb.payment.service.alipay.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeQueryModel;
import com.alipay.api.domain.TradeFundBill;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradeFastpayRefundQueryRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeRefundResponse;
import com.alipay.demo.trade.model.TradeStatus;
import com.alipay.demo.trade.model.builder.AlipayTradeQueryRequestBuilder;
import com.alipay.demo.trade.model.builder.AlipayTradeRefundRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FQueryResult;
import com.alipay.demo.trade.model.result.AlipayF2FRefundResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.alipay.demo.trade.utils.Utils;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.payment.config.AlipayConfig;
import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.service.PaymentService;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.alipay.demo.trade.model.TradeStatus.SUCCESS;

/**
 */
public class AlipayBaseServiceImpl implements PaymentService {

    private static AlipayTradeService tradeService = null;

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

    ;

    /**
     * 用户主动查询支付结果
     *
     * @param payId
     * @return
     * @throws Exception
     */
    @Override
    public Map query(String sellerId, String payId) throws Exception {

//        AlipayClient alipayClient = AlipayAPIClientFactory.getAlipayClient(sellerId); //获得初始化的AlipayClient
//        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();//创建API对应的request类
//        JSONObject biz = new JSONObject();
//        biz.put("out_trade_no", payId);
//        request.setBizContent(biz.toString());//设置业务参数
//        AlipayTradeQueryResponse response = alipayClient.execute(request);
//        JSONObject rObj = JSONObject.fromObject(response);
//
//        rObj.put("isSuccess", isPaySuccess(rObj));
//        return rObj;
//        throw new UserOperateException(403, "未实现");


        Map<String, Object> config = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY);
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, config.get("appId").toString()
                , config.get("privateKey").toString(), AlipayConfig.FORMAT, AlipayConfig.CHARSET
                , config.get("publicKey_alipay").toString(), AlipayConfig.SIGNTYPE);
        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();

        AlipayTradeQueryModel model = new AlipayTradeQueryModel();
        model.setOutTradeNo(payId);
        alipay_request.setBizModel(model);

        AlipayTradeQueryResponse alipay_response = client.execute(alipay_request);

        JSONObject rObj = JSONObject.fromObject(alipay_response.getBody());
        if (StringUtils.mapValueIsEmpty(rObj, "alipay_trade_query_response")) {
            rObj.put("isSuccess", false);
        } else {
            rObj = JSONObject.fromObject(rObj.get("alipay_trade_query_response"));
            rObj.put("isSuccess", isPaySuccess(rObj));
            rObj.put("totalAmount", rObj.get("total_amount"));
        }
        return rObj;

//        String outTradeNo = "tradepay14817938139942440181";
//
//        // 创建查询请求builder，设置请求参数
//        AlipayTradeQueryRequestBuilder builder = new AlipayTradeQueryRequestBuilder()
//                .setOutTradeNo(outTradeNo);
//
//        JSONObject rObj = null;
//        AlipayF2FQueryResult result = tradeService.queryTradeResult(builder);
//        if(result.getTradeStatus() == TradeStatus.SUCCESS) {
//            AlipayTradeQueryResponse response = result.getResponse();
//            rObj = JSONObject.fromObject(alipay_response.getBody());
//        }
//        if(StringUtils.mapValueIsEmpty(rObj,"alipay_trade_query_response")){
//            rObj.put("isSuccess", false);
//        }else{
//            rObj = JSONObject.fromObject(rObj.get("alipay_trade_query_response"));
//            rObj.put("isSuccess", isPaySuccess(rObj));
//            rObj.put("totalAmount",rObj.get("total_amount"));
//        }
//        return rObj;
    }


    /**
     * @param sellerId
     * @param paramsMap
     * @return
     * @throws Exception
     */
    @Override
    public Map notifi(String sellerId, Map paramsMap) throws Exception {
        Map conf = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY);

        Logger.getLogger(this.getClass()).info("== alipay notifi params:\n " + paramsMap);

        Map<String, String> params = new HashMap<String, String>();
        for (Iterator iter = paramsMap.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            if (paramsMap.get(name) instanceof List) {
                JSONArray values = (JSONArray) paramsMap.get(name);
                String valueStr = values.toString();
//                for (int i = 0; i < values.size(); i++) {
//                    valueStr = (i == values.size() - 1) ? valueStr + values.getString(i)
//                            : valueStr + values.getString(i) + ",";
//                }
                params.put(name, valueStr);
            } else {
                String values = paramsMap.get(name).toString();
                params.put(name, values);
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
        }
        Logger.getLogger(this.getClass()).info("------------------params:" + JSONObject.fromObject(params));
        boolean signVerified;
        //旧版本的pc web的支付处理
//        if (!StringUtils.mapValueIsEmpty(paramsMap, "notify_id")) {
//            signVerified = AlipayCheck.verify((String) conf.get("partner"), (String) conf.get("key"), paramsMap);
        //参数转换
//            paramsMap.put("totalAmount", paramsMap.get("total_amount"));
//        }
        //新版本的签名校验
//        else {
//            signVerified = AlipaySignature.rsaCheckV1(params, (String) conf.get("publicKey_alipay"), AlipayConfig.SIGNTYPE);//调用SDK验证签名
        signVerified = AlipaySignature.rsaCheckV1(params, (String) conf.get("publicKey_alipay"), AlipayConfig.CHARSET, "RSA2");
//        }

        if (signVerified) {
            paramsMap.put("totalAmount", paramsMap.get("total_amount"));
            paramsMap.put("isSuccess", isPaySuccess(underline4camelMapKey(paramsMap)));
            return paramsMap;
        } else {
            throw new UserOperateException(403, "签名不通过,请检查");
        }
    }

    @Override
    public Map refund(String sellerId, Map reqParams) throws Exception {
//        AlipayClient alipayClient = AlipayAPIClientFactory.getAlipayClient(sellerId);
//        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
//        request.setBizContent(JSONObject.fromObject(reqParams).toString());
//        AlipayTradeRefundResponse response = alipayClient.execute(request);
//        JSONObject re = JSONObject.fromObject(response);
//        if (response.isSuccess()) {
//            Logger.getLogger(this.getClass()).info("退款调用成功");
//            re.put("isSuccess", true);
//        } else {
//            Logger.getLogger(this.getClass()).info("退款调用失败");
//            re.put("isSuccess", false);
//        }
//        return re;
//        throw new UserOperateException(403, "未实现");

        Map<String, Object> config = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY);
        if (tradeService == null) {
            AlipayTradeServiceImpl.ClientBuilder clientBuilder = new AlipayTradeServiceImpl.ClientBuilder(
                    config.get("appId").toString()
                    , config.get("privateKey").toString()
                    , config.get("publicKey_alipay").toString());
            tradeService = new AlipayTradeServiceImpl(clientBuilder);
        }

        AlipayTradeRefundRequestBuilder builder = new AlipayTradeRefundRequestBuilder()
                .setOutTradeNo(reqParams.get("out_trade_no").toString())
                .setRefundAmount(reqParams.get("refund_amount").toString())
                .setRefundReason("申请退款")
                .setOutRequestNo(reqParams.get("out_request_no").toString())
                .setStoreId(sellerId);

        AlipayF2FRefundResult result = tradeService.tradeRefund(builder);
        JSONObject re = JSONObject.fromObject(result);

        if (result.getTradeStatus() == SUCCESS) {
            Logger.getLogger(this.getClass()).info("退款调用成功");
            re.put("isSuccess", true);
        } else {
            Logger.getLogger(this.getClass()).info("退款调用失败");
            re.put("isSuccess", false);
        }
        return re;
    }

    @Override
    public Map refundQuery(String sellerId, String payId, String returnId) throws Exception {
        Map<String, Object> config = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY);
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, config.get("appId").toString()
                , config.get("privateKey").toString(), AlipayConfig.FORMAT, AlipayConfig.CHARSET
                , config.get("publicKey_alipay").toString(), AlipayConfig.SIGNTYPE);

        AlipayTradeQueryRequest alipay_request = new AlipayTradeQueryRequest();
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        JSONObject json = new JSONObject();
        json.put("out_trade_no", payId);
        json.put("out_request_no", returnId);
        request.setBizContent(json.toString());
        AlipayTradeFastpayRefundQueryResponse response = client.execute(request);
        JSONObject re = JSONObject.fromObject(response);
        if (response.isSuccess()) {
            Logger.getLogger(this.getClass()).info("调用成功");
        } else {
            Logger.getLogger(this.getClass()).info("退款调用失败");
            re.put("isSuccess", false);
        }
        return re;
    }

    /**
     * 判断支付是否成功
     *
     * @param rObj
     * @return
     */
    private boolean isPaySuccess(Map rObj) {
        boolean isSuccess = false;
//        if (!StringUtils.mapValueIsEmpty(rObj, "method") && "alipay.trade.wap.pay.return".equals(rObj.getString("method"))) {
//            isSuccess = true;
//        }
        //支付通知
        if (!StringUtils.mapValueIsEmpty(rObj, "trade_status")) {
            String r = (String) rObj.get("trade_status");
            if ("TRADE_SUCCESS".equals(r) || "TRADE_FINISHED".equals(r)) {//支付成功
                isSuccess = true;
            } else if ("TRADE_CLOSED".equals(r)) {//支付超时或全额退款
                isSuccess = false;
            }
        }
        return isSuccess;
    }

    /**
     * 将map的key由下划线转换为驼峰
     *
     * @param paramsMap
     * @return
     */
    private Map underline4camelMapKey(Map paramsMap) {
        Map<String, Object> obj = new HashMap<>();
        for (Object x : paramsMap.keySet()) {
            obj.put((String) x, paramsMap.get(x));
            String key = StringUtils.underline4camel((String) x);
            key = StringUtils.getEntityInstance(key);
            obj.put(key, paramsMap.get(x));
        }
        return obj;
    }

    public static void main(String[] args) throws Exception {
    }
}
