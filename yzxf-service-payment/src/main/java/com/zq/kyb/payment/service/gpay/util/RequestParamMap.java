package com.zq.kyb.payment.service.gpay.util;

import com.zq.kyb.payment.config.GpayConfig;
import org.apache.commons.collections.map.HashedMap;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 请求参数对象
 * Created by zq2014 on 18/11/21.
 */
public class RequestParamMap {

    /**
     * 初始化请求报文头参数对象
     * @return
     */
    public static Map<String,String> initBaseMap (){
        Map<String,String> data = new HashedMap();
        /***报文头，除了encoding自行选择外其他不需修改***/
        //TODO  此号为测试平台账号，正式环境需要更换成生产环境的账号
        data.put("busino", "B18121200000278");//商户号,测试时由贵商银行给账户 B18102500001445
        data.put("version", GpayConfig.VERSION);                //版本号
        data.put("encode", GpayConfig.ENCODE);              //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("certid", GpayConfig.CERTID);             	//证书编号
        data.put("signMethod", GpayConfig.SIGN_METHOD);     //签名方法
        data.put("trtype", GpayConfig.TRTYPE);
        Date date = new Date();
        data.put("trdate", new SimpleDateFormat("yyyyMMdd").format(date));
        data.put("trtime", new SimpleDateFormat("hhmmss").format(date));
        return data;
    }

    /**
     * 获取流水号字符串
     * @return
     */
    public static String getIdLongStr() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String datestr = sdf.format(new Date());
        String idstr = String.valueOf(getId());
        idstr = addonzero(idstr, 8);
        return datestr + String.valueOf(idstr).substring(0, 8);
    }

    /**
     * 获取当前日期的是hashcode和随机数相乘得到id
     * @return
     */
    public static long getId() {
        Date date = new Date();
        int a1 = date.hashCode();
        int a2 = (new Double((Math.random() * 100000))).intValue();
        int a3 = a1 * a2;
        if (a3 < 0) {
            a3 = (-1) * a3;
        }
        return a3;
    }

    /**
     * 根据已知字符串获取给定长度的字符串,长度不不够的在前面补0
     * @param str
     * @param len
     * @return
     */
    public static String addonzero(String str, int len) {
        int length = str.length();
        if (length < len)
            for (int i = 0; i < len - length; i++) {
                str = 0 + str;
            }
        return str;
    }
}
