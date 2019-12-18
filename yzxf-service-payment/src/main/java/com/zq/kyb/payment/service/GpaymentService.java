package com.zq.kyb.payment.service;

import java.util.Map;

/**
 * @ClassName: GpaymentService.java
 * @Description: 贵商银行服务接口
 * @Author: Ali.Cao
 * @Date: 2018-12-25
 * @Version: 1.0
 **/
public interface GpaymentService {
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
               String openId,
               String channelType) throws Exception;

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
