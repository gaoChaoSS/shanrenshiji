/* 
 * jeasyPro
 * (c) 2012-2013 ____′↘ <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/
 * 2013-8-11 下午3:31:50
 */
package com.zq.kyb.payment.wechatSDK.bean;


import com.zq.kyb.payment.wechatSDK.inf.MsgTypes;

/**
 * 把用户发来的消息转发给多客服处理
 */
public class TransferCustomerServiceMessage extends OutMessage {

    private String MsgType = MsgTypes.TRANSFER_CUSTOMER_SERVICE.getType();

    public TransferCustomerServiceMessage() {
    }

    public String getMsgType() {
        return MsgType;
    }
}
