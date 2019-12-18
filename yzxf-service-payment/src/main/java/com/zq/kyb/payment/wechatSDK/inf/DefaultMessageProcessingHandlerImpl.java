/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘ <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.zq.kyb.payment.wechatSDK.inf;

import com.zq.kyb.payment.wechatSDK.bean.InMessage;
import com.zq.kyb.payment.wechatSDK.bean.OutMessage;

public class DefaultMessageProcessingHandlerImpl implements MessageProcessingHandler {

    private OutMessage outMessage;

    @Override
    public void allType(InMessage msg) {

    }

    @Override
    public void textTypeMsg(InMessage msg) {
    }

    @Override
    public void locationTypeMsg(InMessage msg) {
    }

    @Override
    public void imageTypeMsg(InMessage msg) {
    }

    @Override
    public void videoTypeMsg(InMessage msg) {
    }

    @Override
    public void voiceTypeMsg(InMessage msg) {
    }

    @Override
    public void linkTypeMsg(InMessage msg) {
    }

    @Override
    public void verifyTypeMsg(InMessage msg) {
    }

    @Override
    public void eventTypeMsg(InMessage msg) throws Exception {
    }

    @Override
    public void setOutMessage(OutMessage outMessage) {
        this.outMessage = outMessage;
    }

    @Override
    public void afterProcess(InMessage inMessage, OutMessage outMessage) {
    }

    @Override
    public OutMessage getOutMessage() {
        return outMessage;
    }
}
