package com.zq.kyb.payment.action;

import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.payment.config.PaymentConfig;

/**
 * Created by hujoey on 16/9/12.
 */
public class PayConfigAction extends BaseActionImpl {
    @Override
    @Seller
    public void save() throws Exception {


        super.save();
        String sellerId = (String) ControllerContext.getContext().getResp().getContent().get("sellerId");
        String payType = (String) ControllerContext.getContext().getResp().getContent().get("payType");

        //数据发生变化, 删除支付缓存
//        AlipayAPIClientFactory.alipayClientMap.remove(sellerId);
        PaymentConfig.paymentConfigMap.remove(sellerId + "_" + payType);
    }
}
