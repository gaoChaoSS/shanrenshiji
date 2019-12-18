package com.zq.kyb.payment.service.gpay.util;

import com.sun.org.apache.xml.internal.security.utils.Base64;
import com.zq.kyb.payment.config.GpayConfig;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author: Ali.Cao
 * Created by zq2014 on 18/11/19.
 */
public class Signature {
    public static String getSignCheckContentV1(Map<String, String> params) {
        if (params == null) {
            return null;
        }
        params.remove("sign");
        params.remove("sign_type");
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
            if (StringUtils.areNotEmpty(key, value)) {
                content.append((i == 0 ? "" : "&") + key + "=" + value);
            }
        }
        return content.toString();
    }

    /**
     * sha256WithRsa 加签
     *
     * @param content
     * @param privateKey
     * @param charset
     * @return
     * @throws RuntimeException
     */
    public static String rsa256Sign(String content, String privateKey,
                                    String charset) throws RuntimeException {

        try {
            PrivateKey priKey = getPrivateKeyFromPKCS8(Constants.SIGN_TYPE_RSA,
                    new ByteArrayInputStream(privateKey.getBytes()));
            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_SHA256RSA_ALGORITHMS);
            signature.initSign(priKey);
            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }
            byte[] signed = signature.sign();
            return new String(org.apache.commons.codec.binary.Base64.encodeBase64(signed), charset);
        } catch (Exception e) {
            throw new RuntimeException("RSAcontent = " + content + "; charset = " + charset, e);
        }
    }

    public static PrivateKey getPrivateKeyFromPKCS8(String algorithm,InputStream ins) throws Exception {
        if (ins == null || StringUtils.isEmpty(algorithm)) {
            return null;
        }

        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        byte[] encodedKey = StreamUtil.readText(ins).getBytes();
        encodedKey = Base64.decode(encodedKey);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encodedKey));
    }

    public static boolean rsa256CheckContent(String content, String sign, String publicKey,
                                             String charset) throws RuntimeException {
        try {
            PublicKey pubKey = getPublicKeyFromX509(GpayConfig.SIGN_TYPE,
                    new ByteArrayInputStream(publicKey.getBytes()));
            com.sun.org.apache.xml.internal.security.Init.init();
            java.security.Signature signature = java.security.Signature
                    .getInstance(Constants.SIGN_SHA256RSA_ALGORITHMS);
            signature.initVerify(pubKey);
            if (StringUtils.isEmpty(charset)) {
                signature.update(content.getBytes());
            } else {
                signature.update(content.getBytes(charset));
            }
            boolean b = signature.verify(Base64.decode(sign.getBytes()));
            return b;
        } catch (Exception e) {
            throw new RuntimeException(
                    "RSAcontent = " + content + ",sign=" + sign + ",charset = " + charset, e);
        }
    }


    public static PublicKey getPublicKeyFromX509(String algorithm,InputStream ins) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        StringWriter writer = new StringWriter();
        StreamUtil.io(new InputStreamReader(ins), writer);
        byte[] encodedKey = writer.toString().getBytes();
        encodedKey = Base64.decode(encodedKey);
        return keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
    }
}
