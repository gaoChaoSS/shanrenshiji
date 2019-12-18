package com.zq.kyb.payment.wechatSDK;


import com.zq.kyb.core.exception.UserOperateException;

public class SDKRuntimeException extends UserOperateException {

    private static final long serialVersionUID = 1L;

    public SDKRuntimeException(String str) {
        super(400, str);
    }
}
