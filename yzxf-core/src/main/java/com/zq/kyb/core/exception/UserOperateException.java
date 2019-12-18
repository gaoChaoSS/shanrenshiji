package com.zq.kyb.core.exception;

/**
 * 作用: 用户操作异常
 * <p>
 * Timestamp: 2007-4-18 Time: 16:20:24
 */
public class UserOperateException extends RuntimeException {

    private static final long serialVersionUID = -5956024984071607997L;

    public int errCode;

    // public UserOperateException(ExceptionEnum errorEnum, Object... str) throws IOException {
    // super(PropertiesUtils.getFormatProValue(errorEnum.name(), str));
    // this.errCode = errorEnum.getCode();
    // }

    public UserOperateException(int errCode, String str) {
        super(str);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }

}
