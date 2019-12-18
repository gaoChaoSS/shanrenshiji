package com.zq.kyb.payment.wechatSDK;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.payment.config.WeChatConfig;
import com.zq.kyb.payment.utils.HttpClientUtils;
import com.zq.kyb.payment.wechatSDK.bean.WeChatBuyPost;
import com.zq.kyb.payment.wechatSDK.utils.CommonUtil;
import com.zq.kyb.payment.wechatSDK.utils.MD5SignUtil;
import com.zq.kyb.payment.wechatSDK.utils.SHA1Util;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.*;

/**
 * 生成微信参数总入口
 *
 * @author zhangzhuo
 */
public class WxPay {
    // 发货通知接口
    public static final String DELIVERNOTIFY_URL = "https://api.weixin.qq.com/pay/delivernotify?access_token=";

    // 订单查询接口
    public static final String QUERY_ORDER_INFO_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

    // 通知支付接口 api
    public static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";


    /**
     * 查询订单
     *
     * @param out_trade_no return_code 和 result_code 都为 SUCCESS 是 返回状态码 trade_state SUCCESS—支付成功 REFUND—转入退款 NOTPAY—未支付 CLOSED—已关闭 REVOKED—已撤销 USERPAYING--用户支付中 NOPAY--未支付(输入密码或 确认支付超时) PAYERROR--支付失败(其他
     *                     原因,如银行返回失败)
     */
    public static WeChatBuyPost queryOrderInfo(String appId, String partnerId, String partnerKey, String out_trade_no) throws Exception {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("out_trade_no", out_trade_no);
        String xml = createXml(appId, partnerId, partnerKey, parameters);
        String responseXML = HttpClientUtils.simplePostInvoke(QUERY_ORDER_INFO_URL, xml);
        XStream xs = new XStream(new DomDriver());
        xs.alias("xml", WeChatBuyPost.class);
        WeChatBuyPost postData = (WeChatBuyPost) xs.fromXML(responseXML);
        return postData;
    }

    /**
     * 检查必填参数是否为空
     *
     * @return
     */
    public static void checkCftParameters(Map<String, String> parameters) {
        // 检测必填参数
        if (parameters.get("out_trade_no") == null) {
            throw new SDKRuntimeException("缺少统一支付接口必填参数out_trade_no！");
        } else if (parameters.get("body") == null) {
            throw new SDKRuntimeException("缺少统一支付接口必填参数body！");
        } else if (parameters.get("total_fee") == null) {
            throw new SDKRuntimeException("缺少统一支付接口必填参数total_fee！");
        } else if (parameters.get("notify_url") == null) {
            throw new SDKRuntimeException("缺少统一支付接口必填参数notify_url！");
        } else if (parameters.get("trade_type") == null) {
            throw new SDKRuntimeException("缺少统一支付接口必填参数trade_type！");
        } else if (parameters.get("trade_type").equals("JSAP") && parameters.get("openid") == null) {
            throw new SDKRuntimeException("统一支付接口中，缺少必填参数openid！trade_type为JSAPI时，openid为必填参数！");
        }
    }

    public static String getBizSign(String partnerKey, Map<String, String> bizObj) throws SDKRuntimeException {
        return getBizSign(partnerKey, bizObj, MD5SignUtil.class);
    }

    /**
     * 签名
     *
     * @param bizObj
     * @return
     * @throws SDKRuntimeException
     */
    public static String getBizSign(String partnerKey, Map<String, String> bizObj, Class signClass) throws SDKRuntimeException {
        HashMap<String, String> bizParameters = new HashMap<String, String>();

        List<Map.Entry<String, String>> infoIds = new ArrayList<Map.Entry<String, String>>(bizObj.entrySet());

        Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
            @Override
            public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                return (o1.getKey()).toString().compareTo(o2.getKey());
            }
        });

        for (int i = 0; i < infoIds.size(); i++) {
            Map.Entry<String, String> item = infoIds.get(i);
            if (StringUtils.isNotEmpty(item.getKey())) {
                bizParameters.put(item.getKey(), item.getValue());
            }
        }
        String bizString = CommonUtil.FormatBizQueryParaMap(bizParameters, false);
        if (signClass == MD5SignUtil.class) {
            return MD5SignUtil.Sign(bizString, partnerKey);
        } else if (signClass == SHA1Util.class) {
            return SHA1Util.Sha1(bizString);
        }
        return null;
    }

//    // 生成jsapi支付请求json
//    // 1 获取 openid
//    // 2 调用 统一订单接口 生成支付数据
//    public static JSONObject createBizPackage(String productid, String openId) throws Exception {
//
//        if (StringUtils.isEmpty(productid)) {
//            throw new SDKRuntimeException("orderNo is null");
//        }
//        String total_fee = null;
//        if (productid.startsWith("C")) {
//            OrderInfo orderInfo = (OrderInfo) ServiceUtil.getEntityForWhereStr(OrderInfo.class, "orderNo = ?", new Object[]{productid});
//            if (orderInfo == null) {
//                throw new UserOperateException(400, "orderNo ont found");
//            }
//            total_fee = String.valueOf((int) ((orderInfo.getTotalPrice() + orderInfo.getFreight() + orderInfo.getOtherPrice() - orderInfo.getPayMoney()) * 100));
//            // TODO test 微信的价格单位是 分 不能有小数点
//            total_fee = "1";
//        } else if (productid.startsWith("M")) {
//            MoneyOrder orderInfo = (MoneyOrder) ServiceUtil.getEntityForWhereStr(MoneyOrder.class, "orderNo = ?", new Object[]{productid});
//            if (orderInfo == null) {
//                throw new UserOperateException(400, "orderNo ont found");
//            }
//            total_fee = String.valueOf(Double.valueOf(ArithUtil.mul(orderInfo.getPayMoney(), 100)).intValue());
//        }
//
//        String orderNo = productid + "_" + RandomStringUtils.random(5, "1234567890");
//        String productName = "" + productid;
//
//        Map<String, String> parameter = new HashMap<String, String>();
//        parameter.put("body", productName); // 商品描述。
//        parameter.put("total_fee", total_fee); // 订单总金额
//        parameter.put("out_trade_no", orderNo); // 商户系统内部的订单号
//        parameter.put("trade_type", "JSAPI"); // 交易类型 JSPAI 支付
//        parameter.put("notify_url", Constants.BASE_URL + WeChatConfig.notifyUrl);
//        // trade_type 为 JSAPI 时 openid 是必填项
//        parameter.put("openid", openId);
//        WeChatBuyPost responseData = WxPay.getPrepayInfo(parameter);
//        String prepay_id = null;
//        if ("SUCCESS".equals(responseData.getReturn_code()) && "SUCCESS".equals(responseData.getResult_code())) {
//            prepay_id = responseData.getPrepay_id();
//        } else {
//            throw new UserOperateException(400, "获取微信支付数据出错");
//        }
//        JSONObject jsapiParam = new JSONObject();
//        jsapiParam.put("appId", WeChatConfig.appId);
//        jsapiParam.put("timeStamp", (System.currentTimeMillis() / 1000) + "");
//        jsapiParam.put("nonceStr", CommonUtil.CreateNoncestr());
//        jsapiParam.put("package", "prepay_id=" + prepay_id);
//        jsapiParam.put("signType", "MD5");
//        jsapiParam.put("paySign", getBizSign(jsapiParam));
//        jsapiParam.put("orderNo", orderNo);
//        return jsapiParam;
//    }
//
//    // 生成原生支付url 调用 统一订单接口 生成预支付 url
//    public Map<String, String> createNativeUrl(String productid) throws Exception {
//        if (StringUtils.isEmpty(productid)) {
//            throw new SDKRuntimeException("orderNo is null");
//        }
//        String total_fee = null;
//        if (productid.startsWith("C")) {
//            OrderInfo orderInfo = (OrderInfo) ServiceUtil.getEntityForWhereStr(OrderInfo.class, "orderNo = ?", new Object[]{productid});
//            if (orderInfo == null) {
//                throw new UserOperateException(400, "orderNo ont found");
//            }
//            total_fee = String.valueOf((int) ((orderInfo.getTotalPrice() + orderInfo.getFreight() + orderInfo.getOtherPrice() - orderInfo.getPayMoney()) * 100));
//            // TODO test 微信的价格单位是 分 不能有小数点
//            total_fee = "1";
//        } else if (productid.startsWith("M")) {
//            MoneyOrder orderInfo = (MoneyOrder) ServiceUtil.getEntityForWhereStr(MoneyOrder.class, "orderNo = ?", new Object[]{productid});
//            if (orderInfo == null) {
//                throw new UserOperateException(400, "orderNo ont found");
//            }
//            total_fee = String.valueOf(Double.valueOf(ArithUtil.mul(orderInfo.getPayMoney(), 100)).intValue());
//        }
//
//        String orderNo = productid + "_" + RandomStringUtils.random(5, "1234567890");
//        String productName = "" + productid;
//
//        // 对商品名截取, 去除空格
//        Map<String, String> parameter = new HashMap<String, String>();
//        parameter.put("body", productName); // 商品描述。
//        parameter.put("total_fee", total_fee); // 订单总金额
//        parameter.put("out_trade_no", orderNo); // 商户系统内部的订单号
//        parameter.put("trade_type", "NATIVE"); // 交易类型 本地支付
//        parameter.put("spbill_create_ip", ControllerContext.getIpAddr((HttpServletRequest) ControllerContext.getContext().getHttpRequest())); // ip
//        parameter.put("notify_url", Constants.BASE_URL + WeChatConfig.notifyUrl);
//        // trade_type 为 NATIVE 时 product_id 是必填项
//        parameter.put("product_id", orderNo);
//        WeChatBuyPost responseData = WxPay.getPrepayInfo(parameter);
//        String code_url = null;
//        if ("SUCCESS".equals(responseData.getReturn_code()) && "SUCCESS".equals(responseData.getResult_code())) {
//            code_url = responseData.getCode_url();
//        } else {
//            throw new UserOperateException(400, "获取微信支付二维码失败");
//        }
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("code_url", code_url);
//        params.put("orderNo", orderNo);
//        return params;
//    }

    static BeanInfo bi = null;

    static {
        try {
            bi = Introspector.getBeanInfo(WeChatBuyPost.class);
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, String> toMap(WeChatBuyPost postData) throws Exception {
        HashMap<String, String> nativeObj = new HashMap<String, String>();
        for (PropertyDescriptor pd : bi.getPropertyDescriptors()) {
            if (!"class".equals(pd.getName())) {
                String value = (String) pd.getReadMethod().invoke(postData, null);
                if (StringUtils.isNotEmpty(value)) {
                    nativeObj.put(pd.getName(), value);
                }
            }
        }
        return nativeObj;
    }


    public static String createXml(String appId, String partnerId, String partnerKey, Map<String, String> parameters) {
        parameters.put("appid", appId);// 公众账号ID
        parameters.put("mch_id", partnerId);// 商户号
        parameters.put("nonce_str", CommonUtil.CreateNoncestr());// 随机字符串
        parameters.put("sign", getBizSign(partnerKey, parameters));// 签名
        return CommonUtil.ArrayToXml(parameters);
    }

    public static WeChatBuyPost getPrepayInfo(String appId, String partnerId, String partnerKey, Map<String, String> parameters) throws Exception {
        checkCftParameters(parameters);
        String xml = createXml(appId, partnerId, partnerKey, parameters);
        Logger.getLogger(WxPay.class).info("postXML =============" + xml);
        String result = HttpClientUtils.simplePostInvoke(UNIFIED_ORDER_URL, xml);
        Logger.getLogger(WxPay.class).info("responseXML =============" + result);
        XStream xs = new XStream(new DomDriver());
        xs.alias("xml", WeChatBuyPost.class);
        WeChatBuyPost postData = (WeChatBuyPost) xs.fromXML(result);
        return postData;
    }

    /**
     * 构造 获取openid 的url
     *
     * @param productId
     * @return
     */
    public static String createOauthUrlForCode(String appId, String productId) {
        if (StringUtils.isEmpty(productId)) {
            throw new SDKRuntimeException("productId is null");
        }
        Map<String, String> urlObj = new HashMap<String, String>();
        urlObj.put("appid", appId);
//        urlObj.put("redirect_uri", WeChatConfig.jsapiUrl);
        urlObj.put("response_type", "code");
        urlObj.put("scope", "snsapi_base");
        urlObj.put("state", productId);
        String params = CommonUtil.FormatBizQueryParaMap(urlObj) + "#wechat_redirect";
        return "https://open.weixin.qq.com/connect/oauth2/authorize?" + params;
    }

    /**
     * 构造 获取 opendid 的url
     *
     * @return
     */
    private static String createOauthUrlForOpenid(String appId, String appSecret, String code) {
        Map<String, String> urlObj = new HashMap<String, String>();
        urlObj.put("appid", appId);
        urlObj.put("secret", appSecret);
        urlObj.put("code", code);
        urlObj.put("grant_type", "authorization_code");
        String bizString = CommonUtil.FormatBizQueryParaMap(urlObj);
        return "https://api.weixin.qq.com/sns/oauth2/access_token?" + bizString;
    }

    /**
     * 获取 openid
     *
     * @param code
     * @return
     * @throws Exception
     */
    public static String getOpenId(String appId, String appSecret, String code) throws Exception {
        String oauthUrl = createOauthUrlForOpenid(appId, appSecret, code);
        String resuflt = HttpClientUtils.simpleGetInvoke(oauthUrl);
        JSONObject obj = JSONObject.fromObject(resuflt);
        return obj.getString("openid");
    }

    public static boolean verifySign(String partnerKey, WeChatBuyPost postData) throws Exception {
        HashMap<String, String> nativeObj = toMap(postData);
        String sign = nativeObj.remove("sign");
        String paySign = getBizSign(partnerKey, nativeObj);
        return paySign.equalsIgnoreCase(sign);
    }

    public static boolean verifySign(String partnerKey, Map<String, String> nativeObj) throws Exception {
        String sign = nativeObj.remove("sign");
        String paySign = getBizSign(partnerKey, nativeObj);
        return paySign.equalsIgnoreCase(sign);
    }
}
