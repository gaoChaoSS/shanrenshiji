/* 
 * jeasyPro
 * (c) 2012-2013 ____′↘ <wmails@126.cn>, MIT Licensed
 * http://www.jeasyuicn.com/
 * 2013-8-11 下午3:31:50
 */
package com.zq.kyb.payment.wechatSDK.bean;


import com.zq.kyb.payment.wechatSDK.inf.MsgTypes;

/**
 * 输出文字消息
 *
 * @author ____′↘
 */
public class TextOutMessage extends OutMessage {

    private String MsgType = MsgTypes.TEXT.getType();
    // 文本消息
    private String Content;

    public TextOutMessage() {
    }

    public TextOutMessage(String content) {
        Content = content;
    }

    public String getMsgType() {
        return MsgType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
