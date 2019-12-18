package com.zq.kyb.payment.config;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hujoey on 16/9/12.
 */
public class PaymentConfig {
    public static java.util.concurrent.ConcurrentMap<String, Map> paymentConfigMap = new ConcurrentHashMap<>();
//    public static String baseUrl = "http://s.phsh315.com/";
    public static String baseUrl = "http://s.yzxf8.cn/";

    //public static String PAY_WECHAT_JSAPI_RETURN_URL = "http://www.youlai01.com/jsapi_pay/wechatJsApiReturnCode.jsp";//微信JSAPI发起请求和return的页面
    public static String PAY_WECHAT_NOTIFI_URL = "http://s.phsh315.com/s_user/api/payment/Pay/notifiWechatInputText";//在线支付的第3方支付服务器异步通知页面


    /**
     * 注意与定单模块同步
     */
    // 1：积分，2：优惠券，3：现金账户，4：支付宝，5：pos刷卡, 6：现金收款，7：银行转账，8：其他, 9:产品卡, 10:微信, 11:活动折扣 12:美团在线
    public static int PAY_TYPE_SCORE = 1;//积分
    public static int PAY_TYPE_COUPON = 2;//优惠券
    public static int PAY_TYPE_CASH = 3;//现金账户
    public static int PAY_TYPE_ALIPAY = 4;//支付宝
    public static final int PAY_TYPE_ALIPAY_MOBILE = 4000;//仅用于支付回调区别为mobile
    public static int PAY_TYPE_POS = 5;//pos刷卡
    public static int PAY_TYPE_GET_CASH = 6;//现金收款
    public static int PAY_TYPE_BANK = 7;//银行转账
    public static int PAY_TYPE_OTHER = 8;//其他
    public static int PAY_TYPE_PRODUCT_CARD = 9;//产品卡
    public static int PAY_TYPE_WECHAT = 10;//微信会员版
    public static int PAY_TYPE_PARTY_DISCOUNT = 11;//优惠折扣
    public static int PAY_TYPE_MEITUAN_ONLINE = 12;//美团在线
    public static int PAY_TYPE_SAOBEI_WECHAT = 13;//扫呗支付（微信）
    public static int PAY_TYPE_SAOBEI_ALIPAY = 14;//扫呗支付（支付宝）
    public static int PAY_TYPE_SAOBEI_QPAY = 15;//扫呗支付（QQ钱包）
    public static int PAY_TYPE_WECHAT_MEMBER_APP = 16;// 微信会员APP支付
    public static int PAY_TYPE_WECHAT_SELLER_APP = 17;// 微信商家APP支付
    public static int PAY_TYPE_GPAY = 18;// 贵商银行支付
    public static Map<Integer, String> payTypeMap = new java.util.concurrent.ConcurrentHashMap<>();

    public static String PAY_ADMIN_ID = "001";//平台账户ID

    static {
        payTypeMap.put(PAY_TYPE_SCORE, "积分");
        payTypeMap.put(PAY_TYPE_COUPON, "优惠券");
        payTypeMap.put(PAY_TYPE_CASH, "现金账户");
        payTypeMap.put(PAY_TYPE_ALIPAY, "支付宝");
        payTypeMap.put(PAY_TYPE_POS, "pos刷卡");
        payTypeMap.put(PAY_TYPE_GET_CASH, "现金收款");
        payTypeMap.put(PAY_TYPE_BANK, "银行转账");
        payTypeMap.put(PAY_TYPE_OTHER, "其他");
        payTypeMap.put(PAY_TYPE_PRODUCT_CARD, "产品卡");
        payTypeMap.put(PAY_TYPE_WECHAT, "微信");
        payTypeMap.put(PAY_TYPE_PARTY_DISCOUNT, "活动折扣");
        payTypeMap.put(PAY_TYPE_MEITUAN_ONLINE, "美团在线");
        payTypeMap.put(PAY_TYPE_SAOBEI_WECHAT, "扫呗支付（微信）");
        payTypeMap.put(PAY_TYPE_SAOBEI_ALIPAY, "扫呗支付（支付宝）");
        payTypeMap.put(PAY_TYPE_SAOBEI_QPAY, "扫呗支付（QQ钱包）");
        payTypeMap.put(PAY_TYPE_WECHAT_MEMBER_APP, "微信会员APP支付");
        payTypeMap.put(PAY_TYPE_WECHAT_SELLER_APP, "微信商家APP支付");
    }


    public static Map getPayConfig(String sellerId, Integer payType) throws Exception {
        String key = payType + "";
        if (StringUtils.isNotEmpty(sellerId)) {
            key += sellerId;
        }
        if (!paymentConfigMap.containsKey(key)) {
            Map<String, Object> p = new HashMap<>();
            p.put("sellerId", sellerId);
            p.put("payType", payType);
            Logger.getLogger(PaymentConfig.class).info("Search Payconfig Params: " + p.toString());
            Map<String, Object> r = MysqlDaoImpl.getInstance().findOne2Map("PayConfig", p, null, null);
            Logger.getLogger(PaymentConfig.class).info("Search Payconfig Result: " + p.toString());
            if (r == null) {
                throw new UserOperateException(500, "未设置支付配置!");
            }
            JSONObject con = JSONObject.fromObject(r.get("content"));
            paymentConfigMap.put(key, con);
        }
        Logger.getLogger(PaymentConfig.class).info("========="+paymentConfigMap.get(key));

        return paymentConfigMap.get(key);
    }

}
