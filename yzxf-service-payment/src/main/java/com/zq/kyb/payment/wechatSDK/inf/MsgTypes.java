/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘ <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.zq.kyb.payment.wechatSDK.inf;

/**
 * 消息类型
 * 
 * @author L.cm email: 596392912@qq.com site: http://www.dreamlu.net
 * 
 */
public enum MsgTypes {
    TEXT("text"), LOCATION("location"), IMAGE("image"), LINK("link"), VOICE("voice"), EVENT("event"), VIDEO("video"), NEWS("news"), MUSIC("music"), VERIFY("verify"), TRANSFER_CUSTOMER_SERVICE(
            "transfer_customer_service"), ;
    private String type;

    MsgTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
