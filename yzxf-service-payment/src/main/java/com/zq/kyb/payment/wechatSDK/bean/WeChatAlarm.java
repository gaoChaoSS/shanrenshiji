package com.zq.kyb.payment.wechatSDK.bean;

/**
 * 微信告警 xml
 * 
 * @author L.cm email: 596392912@qq.com site: http://www.dreamlu.net
 * @date 2014-4-23 上午9:46:30
 */
public class WeChatAlarm {

    private String AppId; // appid
    private long TimeStamp; // 时间戳
    private String ErrorType; // 错误类型
    private String Description; // 描述
    private String AlarmContent; // 告警类型
    private String AppSignature; // 签名；字段来源：对前面的其他字段与 appKey按照字典序排序后，使用 SHA1 算法得到的结果。由商户生成后传入。
    private String SignMethod; // sha1

    public void setAlarmContent(String alarmContent) {
        AlarmContent = alarmContent;
    }

    public String getAlarmContent() {
        return AlarmContent;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getDescription() {
        return Description;
    }

    public void setErrorType(String errorType) {
        ErrorType = errorType;
    }

    public String getErrorType() {
        return ErrorType;
    }

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public long getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getAppSignature() {
        return AppSignature;
    }

    public void setAppSignature(String appSignature) {
        AppSignature = appSignature;
    }

    public String getSignMethod() {
        return SignMethod;
    }

    public void setSignMethod(String signMethod) {
        SignMethod = signMethod;
    }
}