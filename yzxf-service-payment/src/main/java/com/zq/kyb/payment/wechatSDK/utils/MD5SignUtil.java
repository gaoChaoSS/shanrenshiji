package com.zq.kyb.payment.wechatSDK.utils;

import com.zq.kyb.payment.wechatSDK.SDKRuntimeException;
import org.apache.commons.lang.StringUtils;

/**
 * 生成signValue
 *
 * @author zhangzhuo
 */
public class MD5SignUtil {

    /**
     * key=paternerKey 得到 stringSignTemp 字符串，幵对
     * stringSignTemp 迚行 md5 运算，再将得到的字符串所有字符转换为大写，得到 sign 值
     * 微信公众号支付接口文档 V2.5signValue。
     *
     * @param content
     * @param key
     * @return
     * @throws SDKRuntimeException
     */
    public static String Sign(String content, String key)
            throws SDKRuntimeException {
        if (StringUtils.isEmpty(key)) {
            throw new SDKRuntimeException("财付通签名key不能为空！");
        }
        if (StringUtils.isEmpty(content)) {
            throw new SDKRuntimeException("财付通签名内容不能为空");
        }
        String signStr = content + "&key=" + key;
//        System.out.println("签名之前=====    " +  signStr);
//        System.out.println("签名之后=====    " +  MD5Util.MD5(signStr).toUpperCase());
        return MD5Util.MD5(signStr).toUpperCase();
    }

    /**
     * sign校验
     *
     * @param content
     * @param sign
     * @param md5Key
     * @return
     */
    public static boolean VerifySignature(String content, String sign, String md5Key) {
        String signStr = content + "&key=" + md5Key;
        String calculateSign = MD5Util.MD5(signStr).toUpperCase();
        String tenpaySign = sign.toUpperCase();
        return (calculateSign == tenpaySign);
    }
}
