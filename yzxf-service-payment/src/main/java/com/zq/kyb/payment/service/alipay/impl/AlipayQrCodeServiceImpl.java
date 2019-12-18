package com.zq.kyb.payment.service.alipay.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.GoodsDetail;
import com.alipay.api.response.AlipayTradePrecreateResponse;
import com.alipay.demo.trade.model.ExtendParams;
import com.alipay.demo.trade.model.builder.AlipayTradePrecreateRequestBuilder;
import com.alipay.demo.trade.model.result.AlipayF2FPrecreateResult;
import com.alipay.demo.trade.service.AlipayTradeService;
import com.alipay.demo.trade.service.impl.AlipayTradeServiceImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.payment.config.AlipayConfig;
import com.zq.kyb.payment.config.PaymentConfig;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alipay.demo.trade.model.TradeStatus.SUCCESS;

/**
 * 线下买家通过使用支付宝扫描商家的二维码等方式完成支付
 */
public class AlipayQrCodeServiceImpl extends AlipayBaseServiceImpl {

    private static AlipayTradeService tradeService = null;

    @Override
    public Map prepay(String innerTxNo,
                      String orderId,
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

        Map<String, Object> config = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_ALIPAY);
        if (tradeService == null) {
            AlipayTradeServiceImpl.ClientBuilder clientBuilder = new AlipayTradeServiceImpl.ClientBuilder(
                    config.get("appId").toString()
                    , config.get("privateKey").toString()
                    , config.get("publicKey_alipay").toString());
            tradeService = new AlipayTradeServiceImpl(clientBuilder);
        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //String time_expire = sdf.format(System.currentTimeMillis() + 24 * 60 * 60 * 1000);
        //if (StringUtils.isNotBlank(discountPrice)) {
        //    params.put("discountable_amount", discountPrice);
        // } else {
        //     params.put("discountable_amount", "0");
        // }
        //params.put("body", body == null ? subject : body);
        //JSONArray goods_detail = new JSONArray();

//        if (orderId.startsWith(ZQUidUtils.B2C_ORDER)) {
//            for (OrderItem item : oi.getOrderItems()) {
//                JSONObject productInfo = JSONObject.fromObject(item.getProductJsonStr());
//                JSONObject goods = new JSONObject();
//                goods.put("goods_id", productInfo.getString("sn"));
//                goods.put("goods_name", productInfo.getString("name"));
//                if (productInfo.containsKey("type")) {
//                    goods.put("goods_category", productInfo.getString("type"));
//                } else {
//                    goods.put("goods_category", "未分类");
//                }
//                goods.put("price", item.getDiscountPrice().toString());
//                goods.put("quantity", item.getCount().intValue() + "");
//                goods_detail.add(goods);
//            }
//        } else {
//
//        }
//        JSONObject goods = new JSONObject();
//        goods.put("goods_id", orderId);
//        goods.put("goods_name", subject);
//        goods.put("goods_category", "order");
//        goods.put("price", totalFee);
//        goods.put("quantity", "1");
        //goods_detail.add(goods);
        //params.put("goods_detail", goods_detail);

        //params.put("operator_id", ControllerContext.getContext().getCurrentUserId());
        //params.put("store_id", storeId);
        //params.put("terminal_id", "");
        //params.put("time_expire", time_expire);


//        String notify_url = AlipayConfig.notifiUrl;
//        Logger.getLogger(this.getClass()).info("-alipay req sellerId:" + sellerId);
//        Logger.getLogger(this.getClass()).info("-alipay req notify_url:" + notify_url);
//        Logger.getLogger(this.getClass()).info("-alipay req params:" + params.toString());
//        AlipayTradePrecreateResponse res = ToAlipayQrTradePay.qrPay(sellerId, params, notify_url);
//        Logger.getLogger(this.getClass()).info("==ToAlipayQrTradePay.qrPay:" + res);
//
//        if (null != res && res.isSuccess()) {
//            Map result = new HashMap<>();
//            if ("10000".equals(res.getCode())) {
//                result.put("code_url", res.getQrCode());
//                result.put("payId", res.getOutTradeNo());
//                result.put("orderNo", orderNo);
//            }
//            result.put("body", res.getBody());
//            return result;
//        } else {
//            throw new UserOperateException(400, "支付调用失败!");
//        }
        // (必填) 商户网站订单系统中唯一订单号，64个字符以内，只能包含字母、数字、下划线，
        // 需保证商户系统端不能重复，建议通过数据库sequence生成，
//        String outTradeNo = "tradeprecreate" + System.currentTimeMillis()
//                + (long) (Math.random() * 10000000L);

        // (必填) 订单标题，粗略描述用户的支付目的。如“xxx品牌xxx门店当面付扫码消费”
//        String subject = "xxx品牌xxx门店当面付扫码消费";

        // (必填) 订单总金额，单位为元，不能超过1亿元
        // 如果同时传入了【打折金额】,【不可打折金额】,【订单总金额】三者,则必须满足如下条件:【订单总金额】=【打折金额】+【不可打折金额】
//        String totalAmount = "0.01";

        // (可选) 订单不可打折金额，可以配合商家平台配置折扣活动，如果酒水不参与打折，则将对应金额填写至此字段
        // 如果该值未传入,但传入了【订单总金额】,【打折金额】,则该值默认为【订单总金额】-【打折金额】
//        String undiscountableAmount = "0";

        // 卖家支付宝账号ID，用于支持一个签约账号下支持打款到不同的收款账号，(打款到sellerId对应的支付宝账号)
        // 如果该字段为空，则默认为与支付宝签约的商户的PID，也就是appid对应的PID
//        String sellerId = "";

        // 订单描述，可以对交易或商品进行一个详细地描述，比如填写"购买商品2件共15.00元"
//        String body = "购买商品3件共20.00元";

        // 商户操作员编号，添加此参数可以为商户操作员做销售统计
//        String operatorId = "test_operator_id";

        // (必填) 商户门店编号，通过门店号和商家后台可以配置精准到门店的折扣信息，详询支付宝技术支持
//        String storeId = "test_store_id";

        // 业务扩展参数，目前可添加由支付宝分配的系统商编号(通过setSysServiceProviderId方法)，详情请咨询支付宝技术支持
        ExtendParams extendParams = new ExtendParams();
        extendParams.setSysServiceProviderId("2088100200300400500");

        // 支付超时，定义为120分钟
        String timeoutExpress = "120m";

        // 商品明细列表，需填写购买商品详细信息，
//        List<GoodsDetail> goodsDetailList = new ArrayList<GoodsDetail>();
//        // 创建一个商品信息，参数含义分别为商品id（使用国标）、名称、单价（单位为分）、数量，如果需要添加商品类别，详见GoodsDetail
//        GoodsDetail goods1 = GoodsDetail.newInstance("goods_id001", "xxx小面包", 1000, 1);
//        // 创建好一个商品后添加至商品明细列表
//        goodsDetailList.add(goods1);
//
//        // 继续创建并添加第一条商品信息，用户购买的产品为“黑人牙刷”，单价为5.00元，购买了两件
//        GoodsDetail goods2 = GoodsDetail.newInstance("goods_id002", "xxx牙刷", 500, 2);
//        goodsDetailList.add(goods2);

        // 创建扫码支付请求builder，设置请求参数
        AlipayTradePrecreateRequestBuilder builder = new AlipayTradePrecreateRequestBuilder()
                .setSubject(subject).setTotalAmount(totalFee).setOutTradeNo(orderId)
                .setSellerId(config.get("partner").toString()).setBody(body)
                .setStoreId(storeId).setExtendParams(extendParams)
                .setTimeoutExpress(timeoutExpress);
        //                .setNotifyUrl("http://www.test-notify-url.com")//支付宝服务器主动通知商户服务器里指定的页面http路径,根据需要设置

        AlipayF2FPrecreateResult result = tradeService.tradePrecreate(builder);

        Map re = new HashMap<>();
        if (result.getTradeStatus() == SUCCESS) {
            Logger.getLogger(this.getClass()).info("支付宝预下单成功: )");

            AlipayTradePrecreateResponse res = result.getResponse();

            // 需要修改为运行机器上的路径
//                String filePath = String.format("/Users/sudo/Desktop/qr-%s.png",
//                        response.getOutTradeNo());
//                log.info("filePath:" + filePath);
            //                ZxingUtils.getQRCodeImge(response.getQrCode(), 256, filePath);

            if (null != res && res.isSuccess()) {
                if ("10000".equals(res.getCode())) {
                    re.put("code_url", res.getQrCode());
                    re.put("payId", res.getOutTradeNo());
                    re.put("orderId", orderId);
                }
                re.put("body", res.getBody());
            } else {
                throw new UserOperateException(400, "支付调用失败!");
            }
        }
        return re;
    }
}
