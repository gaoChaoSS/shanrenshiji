package com.zq.kyb.payment.service.gpay.util;

import java.util.HashMap;
import java.util.Properties;

/**
 * Created by zq2014 on 18/11/19.
 */
public class Constants {
    public static final Properties PS = new Properties();

    public static HashMap Seqs = new HashMap();

    public static final String SIGN_TYPE_RSA                  = "RSA";

    /**
     * sha256WithRsa 算法请求类型
     */
    public static final String SIGN_TYPE_RSA2                 = "RSA2";

    public static final String SIGN_SHA256RSA_ALGORITHMS      = "SHA256WithRSA";

    public static final String SIGN_ALGORITHMS                = "SHA1WithRSA";

    /** GBK字符集 **/
    public static final String CHARSET_GBK                    = "GBK";
}
