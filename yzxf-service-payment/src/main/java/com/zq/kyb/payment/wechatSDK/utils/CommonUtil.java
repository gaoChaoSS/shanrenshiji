package com.zq.kyb.payment.wechatSDK.utils;

import com.zq.kyb.payment.wechatSDK.SDKRuntimeException;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * 支付公共工具
 *
 * @author zhangzhuo
 */
public class CommonUtil {
    /**
     * 商户生成的随机字符串
     * 字符串类型，32个字节以下
     *
     * @param length
     * @return
     */
    public static String CreateNoncestr(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < length; i++) {
            Random rd = new Random();
            res += chars.indexOf(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    /**
     * 商户生成的随机字符串
     * 字符串类型，32个字节以下
     *
     * @return
     */
    public static String CreateNoncestr() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String res = "";
        for (int i = 0; i < 16; i++) {
            Random rd = new Random();
            res += chars.charAt(rd.nextInt(chars.length() - 1));
        }
        return res;
    }

    /**
     * 格式化参数
     *
     * @param parameters
     * @return
     * @throws SDKRuntimeException
     */
    public static String FormatBizQueryParaMap(Map<String, String> parameters)
            throws SDKRuntimeException {
        return FormatBizQueryParaMap(parameters, false);
    }

    /**
     * 与上述方法相同，根据不同的编码方式编码
     *
     * @param parameters
     * @param urlencode
     * @return
     * @throws SDKRuntimeException
     */
    public static String FormatBizQueryParaMap(Map<String, String> parameters,
                                               boolean urlencode) throws SDKRuntimeException {

        String buff = "";
        try {
            List<Entry<String, String>> infoIds = new ArrayList<Entry<String, String>>(
                    parameters.entrySet());

            Collections.sort(infoIds,
                    new Comparator<Entry<String, String>>() {
                        @Override
                        public int compare(Map.Entry<String, String> o1,
                                           Map.Entry<String, String> o2) {
                            return (o1.getKey()).toString().compareTo(
                                    o2.getKey());
                        }
                    });

            for (int i = 0; i < infoIds.size(); i++) {
                Map.Entry<String, String> item = infoIds.get(i);
                //System.out.println(item.getKey());
                if (item.getKey() != "") {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlencode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    buff += key + "=" + val + "&";
                }
            }
            if (buff.isEmpty() == false) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            throw new SDKRuntimeException(e.getMessage());
        }
        return buff;
    }

    /**
     * 判断是否是数字
     *
     * @param str
     * @return
     */
    public static boolean IsNumeric(String str) {
        if (StringUtils.isEmpty(str)) return false;
        if (str.matches("\\d *")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * map转成xml
     *
     * @param arr
     * @return
     */
    public static String ArrayToXml(Map<String, String> arr) {
        String xml = "<xml>";

        Iterator<Entry<String, String>> iter = arr.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            String key = entry.getKey();
            String val = entry.getValue();
            if (IsNumeric(val)) {
                xml += "<" + key + ">" + val + "</" + key + ">";
            } else
                xml += "<" + key + "><![CDATA[" + val + "]]></" + key + ">";
        }

        xml += "</xml>";
        return xml;
    }


}
