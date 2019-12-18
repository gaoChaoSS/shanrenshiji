/**
 * 微信公众平台开发模式(JAVA) SDK
 * (c) 2012-2013 ____′↘ <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/wechat
 */
package com.zq.kyb.payment.wechatSDK.inf;

/**
 * 事件类型
 */
public enum EventTypes {
    SUBSCRIBE("subscribe"), SCAN("SCAN"), UNSUBSCRIBE("unsubscribe");
    private String type;

    EventTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
