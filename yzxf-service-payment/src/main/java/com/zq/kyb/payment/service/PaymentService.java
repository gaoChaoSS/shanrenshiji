package com.zq.kyb.payment.service;

import java.util.Map;

/**
 * Created by xiaoke on 2016/8/19.
 * 支付的几种状态
 */

public interface PaymentService {

    /**
     * @param innerTxNo     内部交易号
     * @param orderNo       订单号
     * @param subject       主题
     * @param totalFee      金额
     * @param body          内容描述
     * @param memberNo      客户编号
     * @param discountPrice 折扣价格
     * @param storeId       店铺ID
     * @param showUrl       支付成功后跳转的url
     * @param authCode      支付宝或微信中的条码
     * @param openId        微信中 jsapi支付方式 openId 是必须的
     * @return
     * @throws Exception
     */
    Map prepay(String innerTxNo,
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
               String openId) throws Exception;

    /**
     * 查询支付是否成功
     *
     * @param payId
     * @return
     * @throws Exception
     */
    Map query(String sellerId, String payId) throws Exception;

    /**
     * 第3方支付服务器异步通知支付结果
     *
     * @param reqParams
     * @return 是否有效
     * @throws Exception
     */
    Map notifi(String sellerId, Map reqParams) throws Exception;

    /**
     * 支付退款
     *
     * @param reqParams
     * @return
     * @throws Exception
     */
    Map refund(String sellerId, Map reqParams) throws Exception;

    /**
     * 退款查询
     *
     * @param sellerId
     * @param payId
     * @param returnId
     * @return
     * @throws Exception
     */
    Map refundQuery(String sellerId, String payId, String returnId) throws Exception;
}
