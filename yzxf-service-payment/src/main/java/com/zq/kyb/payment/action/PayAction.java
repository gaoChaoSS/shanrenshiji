package com.zq.kyb.payment.action;

import com.zq.kyb.core.annotation.Lock;
import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.payment.config.GpayConfig;
import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.service.PayStatus;
import com.zq.kyb.payment.service.WechatOauthService;
import com.zq.kyb.payment.service.alipay.impl.AlipayAppServiceImpl;
import com.zq.kyb.payment.service.alipay.impl.AlipayBaseServiceImpl;
import com.zq.kyb.payment.service.alipay.impl.AlipayMobileWebServiceImpl;
import com.zq.kyb.payment.service.alipay.impl.AlipayQrCodeServiceImpl;
import com.zq.kyb.payment.service.gpay.impl.GpayBaseServiceImpl;
import com.zq.kyb.payment.service.gpay.impl.GpayServiceImpl;
import com.zq.kyb.payment.service.gpay.util.RequestParamMap;
import com.zq.kyb.payment.service.saobei.impl.*;
import com.zq.kyb.payment.service.wechatpay.impl.*;
import com.zq.kyb.payment.utils.ArithUtil;
import com.zq.kyb.payment.utils.XmlUtils;
import com.zq.kyb.payment.wechatSDK.utils.CommonUtil;
import com.zq.kyb.util.BigDecimalUtil;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 */
public class PayAction extends BaseActionImpl {
    private static final Logger logger = Logger.getLogger(PayAction.class);
    //店铺的支付配置信息,来源于店铺配置信息,注意缓存
    private static final Map<String, Map<String, Object>> payConf = new ConcurrentHashMap<>();


    private Map<String, Object> getPayConf(String storeId) throws Exception {
        if (StringUtils.mapValueIsEmpty(payConf, storeId)) {
            reLoadPayConf(storeId);
        }
        return payConf.get(storeId);
    }

    private static void reLoadPayConf(String storeId) throws Exception {
        Message msg = Message.newReqMessage("1:GET@/account/StoreConf/getStoreConf");
        msg.getContent().put("type", "payConf");
        msg.getContent().put("storeId", storeId);
        Message re = ServiceAccess.callService(msg);
        payConf.put(storeId, re.getContent());
    }

    @Override
    @GET
    @Seller
    @Member
    @Path("/show")
    public void show() throws Exception {
        super.show();
        JSONObject content = ControllerContext.getContext().getResp().getContent();
        String orderId = content.getString("orderId");
        String type = content.getString("type");
        if ("orderPay".equals(type)) {
            JSONObject orderInfo = ServiceAccess.getRemoveEntity("order", "OrderInfo", orderId);
            content.put("orderInfo", orderInfo);
        } else if ("pushCash".equals(type)) {
            Map<String, Object> pushCash = MysqlDaoImpl.getInstance().findById2Map("MemberCashLog", orderId, null, null);
            content.put("pushCash", pushCash);
        }
        content.put("payLogs", orderPayList(orderId));
    }

    /**
     * 微信JSAPI支付
     *
     * @throws Exception
     */
    @POST
    @Path("/wechatJsApiPay")
    public void wechatJsApiPay() throws Exception {
        String code = ControllerContext.getPString("code");
        String id = ControllerContext.getPString("id");
        Map<String, Object> payInfo =
                MysqlDaoImpl.getInstance().findById2Map("Pay", id, null, null);
        Map configMap = PaymentConfig.getPayConfig((String) payInfo.get("sellerId"), PaymentConfig.PAY_TYPE_WECHAT);
        Map<String, String> reObj =
                WechatOauthService.getAccessTokenAndOpenId((String) configMap.get("appId"), (String) configMap.get("appSecret"), code);
        Logger.getLogger(PayAction.class).info("getOpenId ===========  " + JSONObject.fromObject(reObj).toString());
        payInfo.put("openId", reObj.get("openId"));
        MysqlDaoImpl.getInstance().saveOrUpdate("Pay", payInfo);
        toResult(200, doStartPay(payInfo));
    }


    @POST
    @Path("/prepayGpay")
    public void prepayGpay() throws Exception {
            Map paramsMap = ControllerContext.getContext().getReq().getContent();
            String orderId = ControllerContext.getPString("orderId");
            String payForm = ControllerContext.getPString("payFrom");
            String openId = ControllerContext.getPString("openId");
            String channelType = ControllerContext.getPString("channelType");
            JSONObject orderInfo = ServiceAccess.getRemoveEntity("order", "OrderInfo", orderId);
            String type = "orderPay";//订单支付还是充值支付
            Double totalFee = NumberUtils.toDouble(orderInfo.get("totalPrice").toString());//本次支付金额
            String sellerId = orderInfo.get("sellerId").toString();//商户id

            int payType = 18;//支付类型
            String returnUrl;//订单id
            if ("member".equals(payForm)) {//https://m.phsh315.com
                returnUrl = "http://m.yzxf8.cn/yzxfMember/my/depositSuccess/orderNo/" + orderInfo.get("orderNo").toString();
            } else {//https://s.phsh315.com
                returnUrl = "http://s.yzxf8.cn/yzxfSeller/store/depositSuccess/orderNo/" + orderInfo.get("orderNo").toString();
            }
            String authCode = ControllerContext.getPString("authCode");

            ControllerContext.getContext().getReq().getContent().put("memberId", orderInfo.get("memberId"));
            Map con = prepay(type, orderId, totalFee, sellerId, null, payType, null, returnUrl, authCode, channelType);

            con.put("openId", openId);
            con = doStartPay(con);
            toResult(200, con);
    }

    /**
     * 预支付
     *
     * @throws Exception
     */
    @POST
    @Member
    @Path("/prepay")
    public void prepay() throws Exception {
        //检查必须字段
        String type = ControllerContext.getPString("type");//订单支付还是充值支付
        String orderId = ControllerContext.getPString("orderId");//订单id
        Double totalFee = ControllerContext.getPDouble("totalFee");//本次支付金额

        String sellerId = ControllerContext.getPString("sellerId");//商户id
        String storeId = ControllerContext.getPString("storeId");//店铺id
        String channelType = ControllerContext.getPString("channelType");
        if (sellerId == null) {
            if ("user".equals(ControllerContext.getContext().getCurrentUserType())) {
                sellerId = ControllerContext.getContext().getCurrentSellerId();
            } else if ("member".equals(ControllerContext.getContext().getCurrentUserType())) {
                sellerId = storeId.split("_")[0];
            }
        }
        int payType = ControllerContext.getPInteger("payType");//支付类型
        String clientType = ControllerContext.getPString("clientType");
        String returnUrl = ControllerContext.getPString("returnUrl");//订单id
        String authCode = ControllerContext.getPString("authCode");

        Map con = prepay(type, orderId, totalFee, sellerId, storeId, payType, clientType, returnUrl, authCode,channelType);

        //如果是刷卡支付,积分支付,储值卡,直接发起支付
        if (PaymentConfig.PAY_TYPE_CASH == payType//现金账户
                || PaymentConfig.PAY_TYPE_SCORE == payType//积分
                || PaymentConfig.PAY_TYPE_POS == payType//pos刷卡
                || PaymentConfig.PAY_TYPE_GET_CASH == payType//现金收款
                || PaymentConfig.PAY_TYPE_BANK == payType//银行转账
                || PaymentConfig.PAY_TYPE_OTHER == payType//其他
                || PaymentConfig.PAY_TYPE_PRODUCT_CARD == payType//产品卡
                || (null != clientType && clientType.equals("MobileApp"))
                || (null != clientType && clientType.equals("MobileSellerApp"))
                || (null != clientType && clientType.equals("ScanCode"))//在线支付的刷卡支付
                ) {
            con = doStartPay(con);
        }

        toResult(200, con);
    }

    public JSONObject prepay(String type, String orderId, Double totalFee, String sellerId, String storeId,
                             int payType, String clientType, String returnUrl, String authCode,String channelType) throws Exception {
        sellerId = PaymentConfig.PAY_ADMIN_ID;
        //用于切换支付方式
        payType = changePayType(sellerId, payType);


        if (StringUtils.isEmpty(orderId) && !"pushCash".equals(type)) {
            throw new UserOperateException(400, "订单编号不能为空!");
        }
        if (StringUtils.isEmpty(type)) {
            throw new UserOperateException(400, "支付类别不能为空!");
        }

        String memberId = ControllerContext.getPString("memberId");
        if ("member".equals(ControllerContext.getContext().getCurrentUserType())) {
            memberId = ControllerContext.getContext().getCurrentUserId();
        }
        if (memberId == null) {
            throw new UserOperateException(404, "客户编号不存在!");
        }

        String creator = ControllerContext.getContext().getCurrentUserId();
        String creatorType = ControllerContext.getContext().getCurrentUserType();
        String payStatus = PayStatus.START.toString();

        Double fei = 0.0, feiPercent = 0.0;
        Map<String, Object> conf = getPayConf(sellerId);
        if (PaymentConfig.PAY_TYPE_ALIPAY == payType) {
            //检查支付配置,是否已经开通
            if (StringUtils.mapValueIsEmpty(conf, "canAlipay") || !"true".equals(conf.get("canAlipay"))) {
                throw new UserOperateException(400, "该店铺 [" + sellerId + "] 未开通支付宝支付");
            }
            if (!StringUtils.mapValueIsEmpty(conf, "alipayFei")) {
                feiPercent = Double.valueOf(conf.get("alipayFei").toString());
                fei = BigDecimalUtil.fixDoubleNum(feiPercent / 100, 4);
            }
        } else if (PaymentConfig.PAY_TYPE_WECHAT == payType) {
            if (!StringUtils.mapValueIsEmpty(conf, "wechatFei")) {
                feiPercent = Double.valueOf(conf.get("wechatFei").toString());
                fei = BigDecimalUtil.fixDoubleNum(feiPercent / 100, 4);
            }
            //贵商银行支付
        } else if (PaymentConfig.PAY_TYPE_GPAY == payType) {
            if (StringUtils.mapValueIsEmpty(conf, "canGbankPay") || !"true".equals(conf.get("canGbankPay"))) {
                throw new UserOperateException(400, "该店铺 [" + sellerId + "] 未开通贵商银行支付");
            }
            if (!StringUtils.mapValueIsEmpty(conf, "gpayFei")) {
                feiPercent = Double.valueOf(conf.get("gpayFei").toString());
                fei = BigDecimalUtil.fixDoubleNum(feiPercent / 100, 4);
            }
        } else if (PaymentConfig.PAY_TYPE_SAOBEI_WECHAT == payType
                || PaymentConfig.PAY_TYPE_SAOBEI_ALIPAY == payType
                || PaymentConfig.PAY_TYPE_SAOBEI_QPAY == payType
                ) {
            if (!StringUtils.mapValueIsEmpty(conf, "saoBeiFei")) {
                feiPercent = Double.valueOf(conf.get("saoBeiFei").toString());
                fei = BigDecimalUtil.fixDoubleNum(feiPercent / 100, 4);
            }
        }

        if (null != clientType && clientType.equals("ScanCode") && StringUtils.isEmpty(authCode)) {
            throw new UserOperateException(400, "authCode is null");
        }

        String _id = ZQUidUtils.generatePayTno();//获取支付交易号
        JSONObject con = new JSONObject();
        con.put("creator", creator);
        con.put("type", type);
        con.put("orderId", orderId);
        con.put("orderNo", orderId);//可能为空
        con.put("creatorType", creatorType);
        con.put("payStatus", payStatus);
        con.put("totalFee", totalFee);
        con.put("storeId", storeId);
        con.put("sellerId", sellerId);
        con.put("memberId", memberId);
        con.put("payType", payType);
        con.put("clientType", clientType);
        con.put("authCode", authCode);
        con.put("showUrl", returnUrl);
        con.put("feiPercent", feiPercent);
        if (PaymentConfig.PAY_TYPE_SAOBEI_WECHAT == payType && clientType.equals("JsApi")) {//公众号内微信支付,需要将关注的AppId保存到支付记录
            con.put("otherData1", new SaobeiJsApiServiceImpl().getOpenIdUrl(sellerId, _id));
        }
        con.put("fei", BigDecimalUtil.fixDoubleNum2(totalFee * fei));

        con.put("__pos_sync", ControllerContext.getPString("__pos_sync"));
        String createTimeStr = ControllerContext.getPString("createTime");
        long createTime = StringUtils.isNotEmpty(createTimeStr) ? Long.parseLong(createTimeStr) : System.currentTimeMillis();
        con.put("createTime", createTime);
        con.put("updateTime", createTime);

        con.put("_id", _id);
        MysqlDaoImpl.getInstance().saveOrUpdate("Pay", con);
        con.put("payId", _id);
        con.put("channelType",channelType);
        return con;
    }

    private int changePayType(String storeId, int payType) throws Exception {
        String otherPayType = (String) getPayConf(storeId).get("other_payType");
        if ("sao_bei".equals(otherPayType)) {
            if (payType == PaymentConfig.PAY_TYPE_WECHAT) {
                payType = PaymentConfig.PAY_TYPE_SAOBEI_WECHAT;
            }
            if (payType == PaymentConfig.PAY_TYPE_ALIPAY) {
                payType = PaymentConfig.PAY_TYPE_SAOBEI_ALIPAY;
            }
        }
        return payType;
    }

    /**
     * 根据支付平台的支付记录进行具体的支付
     */
    @POST
    @Member
    @Path("/startPay")
    @Lock(key = "_id")
    public void startPay() throws Exception {
        String _id = ControllerContext.getPString("_id");
        String openId = ControllerContext.getPString("openId");
        String channelType = ControllerContext.getPString("channelType");
        Map<String, Object> pay = MysqlDaoImpl.getInstance().findById2Map("Pay", _id, null, null);

        if (pay != null && pay.size() != 0 && StringUtils.isNotEmpty(openId)) {
            pay.put("openId", openId);
        }
        logger.info("==startPay:openId=" + openId);
        pay.put("channelType",channelType);
        Map result = doStartPay(pay);
        if (!StringUtils.mapValueIsEmpty(pay, "orderId")) {
            Message msg = Message.newReqMessage("1:GET@/order/OrderInfo/show");
            msg.getContent().put("_id", pay.get("orderId"));
            JSONObject order = ServiceAccess.callService(msg).getContent();
            result.put("orderType", order.get("orderType"));
        }
        toResult(200, result);
    }

    public Map doStartPay(Map<String, Object> pay) throws Exception {
        String payId = (String) pay.get("_id");
        String orderId = (String) pay.get("orderId");
        System.out.println("orderId=="+orderId);
        String returnUrl = (String) pay.get("showUrl");
        String memberId = (String) pay.get("memberId");
        String sellerId = (String) pay.get("sellerId");
        String storeId = (String) pay.get("storeId");
        String type = (String) pay.get("type");
        String openId = (String) pay.get("openId");
        String channelType = (String)pay.get("channelType");
        Integer payType = Integer.valueOf(pay.get("payType").toString());
        Double totalFee = Double.valueOf(pay.get("totalFee").toString());
        String name = "普惠生活-在线支付!";
        //Long createTime = Long.valueOf(pay.get("createTime").toString());
        String clientType = (String) pay.get("clientType");

        if (PaymentConfig.PAY_TYPE_WECHAT == payType && "PcWeb".equals(clientType)) {
            clientType = "QrCode";
        }
        String desc = "";

        if (!pay.get("payStatus").equals(PayStatus.START.toString())) {
            throw new UserOperateException(400, "该支付记录已完成!");
        }

        //发起具体的预操作动作
        Map result = null;
        if (PaymentConfig.PAY_TYPE_ALIPAY == payType) {
            //支付宝的多种支付方式:
            if (clientType.equals("QrCode")) {//顾客扫描商家条码
                result = new AlipayQrCodeServiceImpl().prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, sellerId, null, null, null);
            } else if (clientType.equals("MobileWeb") || clientType.equals("PcWeb")) {//移动端网页支付
                result = new AlipayMobileWebServiceImpl().prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, returnUrl, null, null);
            } else if (clientType.equals("MobileApp")) {//商家扫顾客的手机条码
                result = new AlipayAppServiceImpl().prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, (String) pay.get("authCode"), null);
                //直接可以得到结果:
                result.put("isSuccess", true);
                return result;
            }
        } else if (PaymentConfig.PAY_TYPE_WECHAT == payType) {
            if (clientType.equals("PcWeb") || clientType.equals("QrCode")) {//微信的pc端只能使用扫码支付,即顾客扫描商家条码
                result = new WechatNativeServiceImpl().prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, null, null);
            } else if (clientType.equals("MobileWeb")) {//微信的手机浏览器支付
                result = new WechatMobileServiceImpl().prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, null, null);
            } else if (clientType.equals("JsApi")) {//公众号内支付
                result = new WechatJsApiServiceImpl().prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, null, openId);
            } else if (clientType.equals("ScanCode")) {//商家扫顾客的手机条码
                result = new WechatScanServiceImpl().prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, (String) pay.get("authCode"), null);
                //直接可以得到结果:
                if (!StringUtils.mapValueIsEmpty(result, "isSuccess") && (Boolean) result.get("isSuccess")) {
                    if (result.containsKey("total_fee")) {
                        Double tf = Double.valueOf(result.get("total_fee").toString());
                        result.put("totalAmount", BigDecimalUtil.fixDoubleNum2((tf / 100)).toString());
                        result.put("openId", result.get("openid"));

                    }
                    updateResult(payType, pay, result);
                }
            } else if (clientType.equals("MobileApp")) {//会员app支付
                result = new WechatAppServiceImpl().prepay(payId, "member", orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, null, (String) pay.get("openId"));
                return result;
            } else if (clientType.equals("MobileSellerApp")) {//商户app支付
                result = new WechatAppServiceImpl().prepay(payId, "seller", orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, null, (String) pay.get("openId"));
                return result;
            }
        } else if (PaymentConfig.PAY_TYPE_SAOBEI_WECHAT == payType
                || PaymentConfig.PAY_TYPE_SAOBEI_QPAY == payType
                || PaymentConfig.PAY_TYPE_SAOBEI_ALIPAY == payType) {

            if (clientType.equals("PcWeb") || clientType.equals("QrCode")) {//pc端使用扫码支付,即顾客扫描商家条码
                SaobeiQrCodeServiceImpl service = new SaobeiQrCodeServiceImpl();
                service.setPayType(payType);
                result = service.prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, null, null);
            } else if (clientType.equals("MobileWeb")) {//手机浏览器支付
                SaobeiMobileServiceImpl service = new SaobeiMobileServiceImpl();
                service.setPayType(payType);
                result = service.prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, null, null);
            } else if (clientType.equals("JsApi")) {//公众号内支付
                SaobeiJsApiServiceImpl service = new SaobeiJsApiServiceImpl();
                service.setPayType(payType);
                result = service.prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, null, openId);
            } else if (clientType.equals("ScanCode")) {//商家扫顾客的手机条码
                SaobeiCodeBarServiceImpl service = new SaobeiCodeBarServiceImpl();
                service.setPayType(payType);
                result = service.prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, null, (String) pay.get("authCode"), null);
            }

            boolean update = false;
            if (!StringUtils.mapValueIsEmpty(result, "out_trade_no")) {
                pay.put("trId", result.get("out_trade_no"));
                update = true;

            }
            if (!StringUtils.mapValueIsEmpty(result, "time_end")) {
                pay.put("endTime", new SimpleDateFormat("yyyyMMddHHmmss").parse(result.get("time_end").toString()).getTime());
                update = true;
            }
            if (update) {
                MysqlDaoImpl.getInstance().saveOrUpdate("Pay", pay);
            }
            //将pay的信息也返回给用户
            result.putAll(pay);
        } else if (PaymentConfig.PAY_TYPE_GPAY == payType) {//贵商银行支付
            GpayServiceImpl service = new GpayServiceImpl();
            service.setPayType(payType);
            result = service.prepay(payId, orderId, name, totalFee + "", desc, memberId, "", sellerId, storeId, returnUrl, null, openId,channelType);
        } else {
            throw new UserOperateException(400, "请使用order模块下面做支付");
        }

        result.put("payId", payId);
        return result;
    }

    /**
     * 由第3方服务器发送的异步通知
     */
    @POST
    @Path("/notifiResult")
    //@Lock(key = "transno    ")
    public void notifiResult() throws Exception {
        Map paramsMap = ControllerContext.getContext().getReq().getContent(); //将异步通知中收到的所有参数都存放到map中
        String orderId =ControllerContext.getPString("orderno");
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        String transno = "\'"+paramsMap.get("transno").toString()+"\'";
        Map<String, Object> pay = MysqlDaoImpl.getInstance().findOne2Map("Pay", params, null, null);
        String id="\'"+pay.get("_id").toString()+"\'";
        String field = "UPDATE pay SET trId ="+transno+" WHERE _id ="+id;
        MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(field);
        MysqlDaoImpl.commit();
        if (pay == null) {
            throw new UserOperateException(400, "支付记录不存在!");
        }
        Integer payType = Integer.valueOf(pay.get("payType").toString());
        if (!PayStatus.START.toString().equals(pay.get("payStatus"))) {//数据已经被处理过,重复通知
            returnNotifiSuccess(payType);
            return;
        }

        String sellerId = (String) pay.get("sellerId");

        if (PaymentConfig.PAY_TYPE_ALIPAY == payType) {
            paramsMap = new AlipayBaseServiceImpl().notifi(sellerId, paramsMap);
        } else if (PaymentConfig.PAY_TYPE_WECHAT == payType) {
            paramsMap = new WechatBaseServiceImpl().notifi(sellerId, paramsMap);

            if (!StringUtils.mapValueIsEmpty(paramsMap, "total_fee")) {
                Double tf = Double.valueOf(paramsMap.get("total_fee").toString());
                paramsMap.put("totalAmount", BigDecimalUtil.fixDoubleNum2((tf / 100)).toString());
            }
        } else if (PaymentConfig.PAY_TYPE_GPAY == payType) {
            Double tf = Double.valueOf(paramsMap.get("amt_trans").toString());
            paramsMap.put("totalAmount", BigDecimalUtil.fixDoubleNum2((tf / 100)).toString());
            paramsMap = new GpayBaseServiceImpl().notifi(sellerId, paramsMap);
        }

        //支付成功则更新支付表状态
        if ("true".equals(paramsMap.get("isSuccess"))) {
            updateResult(payType, pay, paramsMap);
            returnNotifiSuccess(payType);
            pay = MysqlDaoImpl.getInstance().findById2Map("Pay", pay.get("_id").toString(), null, null);
            Message msg = Message.newReqMessage("1:POST@/order/OrderInfo/checkOrderStatusByPay");
            msg.getContent().put("pay", pay);
            ServiceAccess.callService(msg).getContent();
        } else {
            throw new UserOperateException(403, "验证不成功");
        }
    }

    /**
     * 微信通知比较特殊,是通过一个post请求的body中的xml内容
     */

    @POST
    @Path("/notifiWechatInputText")
    public void notifiWechatInputText() throws Exception {
        String xmlMsg = ControllerContext.getPString("___inText");
        Logger.getLogger(this.getClass()).info("WechatNotifiXml=" + xmlMsg);

        Map<String, Object> map = XmlUtils.xmlToMap(xmlMsg);
        JSONObject con = ControllerContext.getContext().getReq().getContent();
        con.clear();
        con.putAll(map);

        notifiResult();
    }

    @POST
    @Seller
    @Member
    @Path("/queryPayResultList")
    public void queryPayResultList() throws Exception {
        String payIdList = ControllerContext.getPString("payIdList");
        List<Map<String, Object>> li = new ArrayList<>();
        if (StringUtils.isNotEmpty(payIdList)) {
            String[] payStr = payIdList.split(",");
            for (String payId : payStr) {
                JSONObject result = null;
                try {
                    result = queryPayResult(payId);
                    result.put("payId", payId);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (result == null) {
                        result = new JSONObject();
                        result.put("payId", payId);
                        result.put("payStatus", PayStatus.FAIL.toString());
                    }
                    Map<String, Object> pay = MysqlDaoImpl.getInstance().findById2Map("Pay", payId, null, null);
                    result.put("pay", pay);
                    result.put("transno",pay.get("trId"));
                    result.put("orderId", pay.get("orderId"));
                    li.add(result);
                }
            }
        }
        toResult(200, li);
    }


    /**
     * 主动查询某笔交易,首先查本地,如果为:start,就查第3方服务器
     */
    @GET
    @Seller
    @Member
    @Path("/queryPayResult")
    public void queryPayResult() throws Exception {
        String payId = ControllerContext.getPString("payId");
        toResult(200, queryPayResult(payId));
    }

    public JSONObject queryPayResult(String payId) throws Exception {
        Map<String, Object> pay = MysqlDaoImpl.getInstance().findById2Map("Pay", payId, null, null);

        pay = getPayResult(pay);
        String payStatus = (String) pay.get("payStatus");
        JSONObject re = new JSONObject();
        re.put("payStatus", payStatus);
        re.put("isSuccess", PayStatus.SUCCESS.toString().equals(payStatus));
        re.put("payId", payId);
        re.put("orderId", pay.get("orderId"));
        re.put("pay", pay);

        return re;
    }

    private Map<String, Object> getPayResult(Map<String, Object> pay) throws Exception { //584
        String payId = (String) pay.get("_id");
        Integer payType = Integer.valueOf(pay.get("payType").toString());
        String payStatus = (String) pay.get("payStatus");
        payStatus = "START";
        String sellerId = (String) pay.get("sellerId");

        if (!PayStatus.START.toString().equals(payStatus)) {//数据已经被处理过,重复通知

        } else {
            Map rObj = null;
            if (PaymentConfig.PAY_TYPE_ALIPAY == payType) {
                rObj = new AlipayBaseServiceImpl().query(sellerId, (String) pay.get("orderId"));
            } else if (PaymentConfig.PAY_TYPE_WECHAT == payType) {
                if ("MobileApp".equals(pay.get("clientType"))) {
                    payType = 16;
                } else if ("MobileSellerApp".equals(pay.get("clientType"))) {
                    payType = 17;
                }
                rObj = new WechatBaseServiceImpl().queryByType(sellerId, (String) pay.get("orderId"), payType);
                if (!StringUtils.mapValueIsEmpty(rObj, "total_fee")) {
                    Double tf = Double.valueOf(rObj.get("total_fee").toString());
                    rObj.put("totalAmount", BigDecimalUtil.fixDoubleNum2((tf / 100)).toString());
                }
            } else if (PaymentConfig.PAY_TYPE_GPAY == payType) {
                rObj = new GpayBaseServiceImpl().query(sellerId, (String) pay.get("_id"));
                if (!StringUtils.mapValueIsEmpty(rObj, "amt_trans")) {
                    Double tf = Double.valueOf(rObj.get("amt_trans").toString());
                    rObj.put("totalAmount", BigDecimalUtil.fixDoubleNum2((tf / 100)).toString());
                }

            } else if (PaymentConfig.PAY_TYPE_SAOBEI_WECHAT == payType
                    || PaymentConfig.PAY_TYPE_SAOBEI_QPAY == payType
                    || PaymentConfig.PAY_TYPE_SAOBEI_ALIPAY == payType) {
                rObj = new SaobeiBaseServiceImpl().query(sellerId, payId);
            }

            Logger.getLogger(this.getClass()).info("------- rObj:" + rObj.toString());
            if (!StringUtils.mapValueIsEmpty(rObj, "isSuccess") && Boolean.valueOf((String) rObj.get("isSuccess"))) {
                updateResult(payType, pay, rObj);
                pay = MysqlDaoImpl.getInstance().findById2Map("Pay", payId, null, null);
            }
        }
        return pay;
    }

    /**
     * 支付完成, 成功或失败!同时更新支付记录
     *
     * @param payType
     * @param pay
     * @param rObj
     * @throws Exception
     */
    private void updateResult(Integer payType, Map<String, Object> pay, Map rObj) throws Exception {
        //锁住记录
        String id = (String) pay.get("_id");
        String key = "pay_update_result_" + id;
        JedisUtil.whileGetLock(key, 25);
        try {
            //为了数据的准确性,在锁定的情况下,重新查询pay记录,检查是否已经更改支付状态了
            pay = MysqlDaoImpl.getInstance().findById2Map("Pay", id, null, null);
            if (!PayStatus.START.toString().equals(pay.get("payStatus"))) {//数据已经被处理过,重复通知

            } else {
                //检查金额与支付记录的金额是否对应
                Double reqNum = BigDecimalUtil.fixDoubleNum2(Double.valueOf(rObj.get("totalAmount").toString()));
                Double recordNum = BigDecimalUtil.fixDoubleNum2(Double.valueOf(pay.get("totalFee").toString()));
                if (reqNum <= 0) {
                    throw new UserOperateException(400, "支付记录金额不能小于或等于0!");
                }
                if (reqNum.doubleValue() != recordNum.doubleValue()) {
                    throw new UserOperateException(400, "与支付记录金额不一致!");
                }
                pay.put("payResultMap", JSONObject.fromObject(rObj));
                if (PaymentConfig.PAY_TYPE_ALIPAY == payType) {
                    pay.put("trId", rObj.get("trade_no"));
                    long endTime = System.currentTimeMillis();
                    if (!StringUtils.mapValueIsEmpty(rObj, "endTime")) {
                        endTime = Long.valueOf(rObj.get("endTime").toString());
                    }
                    pay.put("endTime", endTime);
                } else if (PaymentConfig.PAY_TYPE_WECHAT == payType) {
                    pay.put("trId", rObj.get("transaction_id"));
                    if (!StringUtils.mapValueIsEmpty(rObj, "time_end")) {
                        pay.put("endTime", new SimpleDateFormat("yyyyMMddHHmmss").parse(rObj.get("time_end").toString()).getTime());
                    }
                } else if (PaymentConfig.PAY_TYPE_GPAY == payType) {
                    pay.put("trId", rObj.get("transno"));
                    if (!StringUtils.mapValueIsEmpty(rObj, "trdate")
                             && !StringUtils.mapValueIsEmpty(rObj,"trtime")) {
                        String endTime =  rObj.get("trdate").toString() + rObj.get("trtime").toString();
                        pay.put("endTime", new SimpleDateFormat("yyyyMMddHHmmss").parse(endTime).getTime());
                    }

                }else if (PaymentConfig.PAY_TYPE_SAOBEI_WECHAT == payType
                        || PaymentConfig.PAY_TYPE_SAOBEI_ALIPAY == payType
                        || PaymentConfig.PAY_TYPE_SAOBEI_QPAY == payType
                        ) {
                    if (!StringUtils.mapValueIsEmpty(rObj, "out_trade_no")) {
                        pay.put("trId", rObj.get("out_trade_no"));
                    }
                    if (!StringUtils.mapValueIsEmpty(rObj, "pay_time")) {
                        pay.put("endTime", new SimpleDateFormat("yyyyMMddHHmmss").parse(rObj.get("pay_time").toString()).getTime());
                    }
                    if (!StringUtils.mapValueIsEmpty(rObj, "time_end")) {
                        pay.put("endTime", new SimpleDateFormat("yyyyMMddHHmmss").parse(rObj.get("time_end").toString()).getTime());
                    }
                }
                Boolean isSuccess = Boolean.valueOf(String.valueOf(rObj.get("isSuccess")));
                String status = isSuccess ? PayStatus.SUCCESS.toString() : PayStatus.FAIL.toString();
                pay.put("payStatus", status);
                long value = System.currentTimeMillis();
                pay.put("endTime", StringUtils.mapValueIsEmpty(pay, "endTime") ? value : pay.get("endTime"));
                pay.put("updateTime", value);
                MysqlDaoImpl.getInstance().saveOrUpdate("Pay", pay);
            }
        } finally {
            JedisUtil.del(key);
        }
    }

    /**
     * 退款完成,成功或失败
     *
     * @param returnRecord
     * @param r
     */

    private void updateReturnResult(Integer payType, Map returnRecord, Map r) throws Exception {
        returnRecord.put("returnStatus", (Boolean) r.get("isSuccess") ? PayStatus.SUCCESS.toString() : PayStatus.FAIL.toString());
        returnRecord.put("returnResultMap", r.toString());
        returnRecord.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("PayReturn", returnRecord);
    }

    /**
     * 通知第3方支付服务器一切ok
     */
    private void returnNotifiSuccess(Integer payType) throws IOException {
        JSONObject re = new JSONObject();
        if (PaymentConfig.PAY_TYPE_ALIPAY == payType) {
            re.put("___outText", "success");
        } else if (PaymentConfig.PAY_TYPE_WECHAT == payType) {
            Map<String, String> responseParam = new HashMap<>();
            responseParam.put("return_code", "SUCCESS");
            re.put("___outText", CommonUtil.ArrayToXml(responseParam));
        } else if(PaymentConfig.PAY_TYPE_GPAY == payType){
            re.put("___outText", "success");
        }
        toResult(200, re);
    }

    /**
     * 退款查询
     * 若查询到第三方已经退款，但本地未更新，则更新
     *
     * @throws Exception
     */
    @POST
    @Seller
    @Path("/queryRefund")
    public void queryRefund() throws Exception {
        String orderId = ControllerContext.getPString("orderId");

        if (StringUtils.isEmpty(orderId)) {
            throw new UserOperateException(500, "获取订单记录失败");
        }

        String sql = "select t1.orderNo,t2.payType,t3._id as payId,t3.sellerId,t4._id as returnId,t4.returnStatus" +
                " from OrderInfo t1" +
                " left join OrderInfo t2 on t1.pid=t2._id" +
                " left join Pay t3 on t2._id = t3.orderId" +
                " left join PayReturn t4 on t2._id=t4.orderId" +
                " where t1._id=?";
        List<Object> params = new ArrayList<>();
        params.add(orderId);
        List<String> returnFields = new ArrayList<>();
        returnFields.add("orderNo");
        returnFields.add("payType");
        returnFields.add("payId");
        returnFields.add("sellerId");
        returnFields.add("returnId");
        returnFields.add("returnStatus");
        List<Map<String, Object>> pay = MysqlDaoImpl.getInstance().queryBySql(sql, returnFields, params);

        if (pay != null && pay.size() != 0 && !StringUtils.mapValueIsEmpty(pay.get(0), "returnId")) {
            toResult(200, pay.get(0));
        } else {
            String payId = pay.get(0).get("payId").toString();
            String sellerId = pay.get(0).get("sellerId").toString();
            int payType = Integer.parseInt(pay.get(0).get("payType").toString());
            String returnId = payId + "_" + ZQUidUtils.generatePayReturnTno(ControllerContext.getContext().getCurrentSellerId());

            Map re;
            if (PaymentConfig.PAY_TYPE_ALIPAY == payType) {
                re = new AlipayBaseServiceImpl().refundQuery(sellerId, payId, returnId);
            } else if (PaymentConfig.PAY_TYPE_WECHAT == payType) {
                re = new WechatBaseServiceImpl().refundQuery(sellerId, payId, returnId);
            } else if (PaymentConfig.PAY_TYPE_GPAY == payType) {
                re = new GpayBaseServiceImpl().refundQuery(sellerId, payId, returnId);
            } else {
                throw new UserOperateException(500, "错误的支付类型");
            }
            String returnStatus = Boolean.valueOf((String) re.get("isSuccess")) ? PayStatus.SUCCESS.toString() : PayStatus.FAIL.toString();
            String returnResultMap = re.toString();
            if (Boolean.valueOf((String) re.get("isSuccess"))) {
                Map<String, Object> payReturn = new HashMap<>();
                payReturn.put("_id", returnId);
                payReturn.put("payId", payId);
                payReturn.put("orderId", orderId);
                payReturn.put("returnStatus", PayStatus.START.toString());
                payReturn.put("createTime", System.currentTimeMillis());
                payReturn.put("creator", ControllerContext.getContext().getCurrentUserId());

                payReturn.put("returnAmount", ((JSONObject) re.get("body")).get("returnFee"));
                payReturn.put("returnStatus", returnStatus);
                payReturn.put("returnResultMap", returnResultMap);
                payReturn.put("endTime", System.currentTimeMillis());
                MysqlDaoImpl.getInstance().saveOrUpdate("PayReturn", payReturn);
                toResult(200, payReturn);
            } else {
                toResult(200, re);
            }
        }
    }

    /**
     * 退款
     * returnAmount:退款金额
     */
    @POST
    @Seller
    @Path("/refund")
    public void refund() throws Exception {
        String payId = ControllerContext.getPString("payId");
        Double returnAmount = ControllerContext.getPDouble("returnAmount");//退款金额
        Map<String, Object> payReturn = refund(payId, returnAmount);
        toResult(200, payReturn);
    }

    private Map<String, Object> refund(String payId, double returnAmount) throws Exception {
        String returnId = payId + "_" + ZQUidUtils.generatePayReturnTno(ControllerContext.getContext().getCurrentSellerId());
        System.out.println("returnId=="+returnId);

        //检查对应的支付记录是否正确
        Map<String, Object> pay = MysqlDaoImpl.getInstance().findById2Map("Pay", payId, null, null);
        if (pay == null) {
            throw new UserOperateException(400, "支付记录不存在!");
        }
        if (!"SUCCESS".equals(pay.get("payStatus"))) {
            throw new UserOperateException(400, "该次支付未成功,无需退款!");
        }

        //检查是否超出支付记录的金额
        Double returnAmountAll = 0.0;
        String sql = "select sum(returnAmount) as returnAmountAll from " + Dao.getFullTableName("PayReturn") + " where payId=? and returnStatus='SUCCESS'";
        List<String> reField = new ArrayList<>();
        reField.add("returnAmountAll");
        List<Object> params = new ArrayList<>();
        params.add(payId);
        List<Map<String, Object>> s = MysqlDaoImpl.getInstance().queryBySql(sql, reField, params);
        if (s != null && s.size() > 0) {
            Map<String, Object> m = s.get(0);
            if (!StringUtils.mapValueIsEmpty(m, "returnAmountAll")) {
                returnAmountAll = (Double) m.get("returnAmountAll");
            }
        }
        if (BigDecimalUtil.add(returnAmount, returnAmountAll) > Double.valueOf(pay.get("totalFee").toString())) {
            throw new UserOperateException(400, "退款金额不能大于支付金额");
        }

        String sellerId = (String) pay.get("sellerId");
        Integer payType = Integer.valueOf(pay.get("payType").toString());//支付记录上的支付方式

        //检查退款记录是否存在,如果存在,检查对应的状态是否正确!
        Map<String, Object> payReturn = MysqlDaoImpl.getInstance().findById2Map("PayReturn", returnId, null, null);
        if (payReturn != null) {
            if (!payId.equals(payReturn.get("payId"))) {
                throw new UserOperateException(400, "退款与支付记录不一致,请检查!");
            }

        } else {
            payReturn = new HashMap<>();
            payReturn.put("_id", returnId);
            payReturn.put("payId", payId);
            payReturn.put("returnAmount", returnAmount);
            payReturn.put("orderId", pay.get("orderId"));
            payReturn.put("returnStatus", PayStatus.START.toString());
            payReturn.put("createTime", System.currentTimeMillis());
            payReturn.put("creator", ControllerContext.getContext().getCurrentUserId());
            MysqlDaoImpl.getInstance().saveOrUpdate("PayReturn", payReturn);
        }

        String returnStatus, returnResultMap = null;

        if (PaymentConfig.PAY_TYPE_ALIPAY == payType) {//支付宝退款
            Map<String, Object> p = new HashMap<>();
            p.put("out_trade_no", pay.get("orderId").toString());
            p.put("refund_amount", returnAmount);
            p.put("out_request_no", returnId);
            Map result = new AlipayBaseServiceImpl().refund(sellerId, p);
            returnStatus = (Boolean) result.get("isSuccess") ? PayStatus.SUCCESS.toString() : PayStatus.FAIL.toString();
            returnResultMap = result.toString();
        } else if (PaymentConfig.PAY_TYPE_WECHAT == payType) {//微信退款
            Integer totalFee = Double.valueOf(ArithUtil.mul(Double.valueOf(pay.get("totalFee").toString()), 100)).intValue();
            Integer returnAmountWechat = Double.valueOf(ArithUtil.mul(returnAmount, 100)).intValue();

            Map<String, Object> p = new HashMap<>();
            p.put("outTradeNo", pay.get("orderId").toString());
            p.put("totalFee", totalFee);
            p.put("refundFee", returnAmountWechat);
            p.put("outRefundNo", returnId);

            Map result = new WechatBaseServiceImpl().refund(sellerId, p);
            returnStatus = (Boolean) result.get("isSuccess") ? PayStatus.SUCCESS.toString() : PayStatus.FAIL.toString();
            returnResultMap = result.toString();
        } else if (PaymentConfig.PAY_TYPE_SAOBEI_WECHAT == payType
                || PaymentConfig.PAY_TYPE_SAOBEI_QPAY == payType
                || PaymentConfig.PAY_TYPE_SAOBEI_ALIPAY == payType) {//扫呗退款
            Integer totalFee = Double.valueOf(ArithUtil.mul(Double.valueOf(pay.get("totalFee").toString()), 100)).intValue();
            Integer refundFee = Double.valueOf(ArithUtil.mul(returnAmount, 100)).intValue();

            Map<String, Object> p = new HashMap<>();
            String trId = (String) pay.get("trId");
            p.put("outTradeNo", trId);
            p.put("totalFee", totalFee);
            p.put("refundFee", refundFee);
            p.put("outRefundNo", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));
            p.put("payType", payType);

            Map result = new SaobeiBaseServiceImpl().refund(sellerId, p);
            returnStatus = !StringUtils.mapValueIsEmpty(result, "isSuccess") && Boolean.valueOf(result.get("isSuccess").toString())
                    ? PayStatus.SUCCESS.toString() : PayStatus.FAIL.toString();
            returnResultMap = result.toString();
        } else if (PaymentConfig.PAY_TYPE_GPAY == payType) {//贵商银行退款
            Integer totalFee = Double.valueOf(ArithUtil.mul(Double.valueOf(pay.get("totalFee").toString()), 100)).intValue();
            Integer refundFee = Double.valueOf(ArithUtil.mul(returnAmount, 100)).intValue();
            Map<String, String> data = RequestParamMap.initBaseMap();
            String trId = (String) pay.get("trId");

            /***商户接入参数***/
            data.put("refundno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));
            data.put("refund_amt", String.valueOf(refundFee));
            data.put("refund_reason","客户退款");//订单主表中的字段-returnDesc
            data.put("orderno", (String) pay.get("orderId"));
            data.put("prv_tramt", String.valueOf(totalFee));
            data.put("prv_transno", trId);        //trId   原IMP平台订单号，对应消费类交易返回的transno
            Map<String,String> reslutMap = JSONObject.fromObject(pay.get("payResultMap"));
            data.put("prv_tradeno", reslutMap.get("imp_tradeno"));        //原IMP平台支付流水号，对应消费类交易返回的imp_tradeno
            data.put("prv_trdate", reslutMap.get("trdate"));//TODO 此处时间不太确定  测试的时候才能确定
            data.put("opertype", "2");//1- 消费撤销(限当天全额退款);2-退货(可分多笔退货,但目前平台不支持多笔退货)
            data.put("subcode", GpayConfig.SUBCODE_REFUND);
            data.put("serialno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));
            Map result = new GpayBaseServiceImpl().refund(sellerId, data);
            returnStatus = !StringUtils.mapValueIsEmpty(result, "isSuccess") && Boolean.valueOf(result.get("isSuccess").toString())
                    ? PayStatus.SUCCESS.toString() : PayStatus.FAIL.toString();
            returnResultMap = result.toString();

        } else {//其他非在线支付
            returnStatus = PayStatus.SUCCESS.toString();
        }

        //TODO 暂不支持多次退款
        if (PayStatus.SUCCESS.toString().equals(returnStatus)) {
            pay.put("refundAmountAll", returnAmount);
            double feiPercent = StringUtils.mapValueIsEmpty(pay, "feiPercent") ? 0.0 : Double.valueOf(pay.get("feiPercent").toString());
            double totalFee = StringUtils.mapValueIsEmpty(pay, "totalFee") ? 0.0 : Double.valueOf(pay.get("totalFee").toString());
            pay.put("fei", feiPercent / 100 * (totalFee - returnAmount));//重新计算费率
            pay.put("updateTime", System.currentTimeMillis());//重新计算费率
            MysqlDaoImpl.getInstance().saveOrUpdate("Pay", pay);
        } else {
            throw new UserOperateException(400, "退款失败!");
        }

        //将退款的结果记录下来
        // updateReturnResult(payType, payReturn, result);

        payReturn.put("returnStatus", returnStatus);
        payReturn.put("returnResultMap", returnResultMap);
        payReturn.put("endTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("PayReturn", payReturn);
        return payReturn;
    }


    @GET
    @Seller
    @Member
    @Path("/orderPayList")
    public void orderPayList() throws Exception {
        String orderId = ControllerContext.getPString("orderId");
        Iterable<Map<String, Object>> li = orderPayList(orderId);
        toResult(200, li);
    }

    private Double orderPayTotal(String orderId) throws Exception {
        Map<String, Object> orderBy = new HashMap<>();
        orderBy.put("createTime", 1);
        String[] fields = {"name", "totalFee", "memberId", "sellerId", "storeId", "createTime", "payType", "creator", "creatorType", "reviewer", "reviewDesc"};
        List<String> r = new ArrayList<>();
        r.add("totalFee");
        List<Object> p = new ArrayList<>();
        p.add(orderId);
        p.add(PayStatus.SUCCESS.toString());
        double total = 0.0;
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql("select sum(totalFee) as totalFee from " + Dao.getFullTableName("Pay") + " where orderId=? and payStatus=?", r, p);
        if (li != null && li.size() > 0) {
            Map<String, Object> m = li.get(0);
            if (StringUtils.mapValueIsEmpty(m, "totalFee")) {
                return 0.0;
            }
            total = (double) m.get("totalFee");
            total = BigDecimalUtil.fixDoubleNum2(total);
        }
        return total;
    }

    private Iterable<Map<String, Object>> orderPayList(String orderId) throws Exception {
        Map<String, Object> p = new HashMap<>();
        p.put("orderId", orderId);
        p.put("payStatus", PayStatus.SUCCESS.toString());
        Map<String, Object> orderBy = new HashMap<>();
        orderBy.put("createTime", 1);
        String[] fields = {"name", "totalFee", "memberId", "storeId", "createTime", "payType", "creator", "creatorType", "reviewer", "reviewDesc"};
        return MysqlDaoImpl.getInstance().findAll2Map("Pay", p, orderBy, fields, Dao.FieldStrategy.Include);
    }

    /**
     * 删除未支付的支付记录
     * 根据orderNo获取pay,并删除未支付的记录
     *
     * @throws Exception
     */
    @GET
    @Path("/delStartPayByOrderNo")
    public void delStartPayByOrderNo() throws Exception {
        String orderNo = ControllerContext.getPString("orderNo");
        Map<String, Object> param = new HashMap<>();
        param.put("orderNo", orderNo);
        Map<String, Object> pay = MysqlDaoImpl.getInstance().findOne2Map("Pay", param, null, null);
        if (pay == null || pay.size() == 0) {
            throw new UserOperateException(500, "获取订单支付记录失败");
        }
        if (!"START".equals(pay.get("payStatus"))) {
            throw new UserOperateException(500, "此订单不是未支付的订单");
        }
        List<Object> paramList = new ArrayList<>();
        paramList.add(pay.get("_id"));
        MysqlDaoImpl.getInstance().exeSql("delete from Pay where _id=? and payStatus='START'", paramList, "Pay", false);
    }

    /**
     * 根据ID获取pay
     *
     * @throws Exception
     */
    @GET
    @Path("/getPayById")
    public void getPayById() throws Exception {
        String payId = ControllerContext.getPString("payId");
        Map<String, Object> pay = MysqlDaoImpl.getInstance().findById2Map("Pay", payId, null, null);

        if (pay != null && pay.size() != 0 && !StringUtils.mapValueIsEmpty(pay, "orderId")) {
            Message msg = Message.newReqMessage("1:GET@/order/OrderInfo/show");
            msg.getContent().put("_id", pay.get("orderId"));
            JSONObject order = ServiceAccess.callService(msg).getContent();
            pay.put("orderType", order.get("orderType"));
        }

        toResult(200, pay);
    }

    /**
     * 根据order ID获取pay
     *
     * @throws Exception
     */
    @GET
    @Path("/getPayByOrderId")
    public void getPayByOrderId() throws Exception {
        String orderId = ControllerContext.getPString("orderId");
        Map<String, Object> params = new HashMap<>();
        params.put("orderId", orderId);
        Map<String, Object> pay = MysqlDaoImpl.getInstance().findOne2Map("Pay", params, null, null);
        toResult(200, pay);
    }

    /**
     * 检查扫呗支付结果
     * 根据扫呗订单号trId，终端流水号orderId查询
     *
     * @throws Exception
     */
    @GET
    @Path("/checkSaobeiPay")
    public void checkSaobeiPay() throws Exception {
        String trId = ControllerContext.getPString("trId");
        int payType = ControllerContext.getPInteger("payType");
        String orderId = ControllerContext.getPString("orderId");

        if (StringUtils.isEmpty(orderId)) {
            throw new UserOperateException(500, "未获取到支付订单");
        }

        if (payType == PaymentConfig.PAY_TYPE_WECHAT) {
            payType = PaymentConfig.PAY_TYPE_SAOBEI_WECHAT;
        }
        if (payType == PaymentConfig.PAY_TYPE_ALIPAY) {
            payType = PaymentConfig.PAY_TYPE_SAOBEI_ALIPAY;
        }

        Map re = new SaobeiBaseServiceImpl().queryByOrderId("001", trId, String.valueOf(payType));

        if (re == null || re.size() == 0 || StringUtils.mapValueIsEmpty(re, "result_code") || !"01".equals(re.get("result_code"))) {
            throw new UserOperateException(500, "第三方支付：未获取到支付记录");
        }
        if (!"SUCCESS".equals(re.get("trade_state"))) {
            throw new UserOperateException(500, "第三方支付：此订单未完成支付");
        }
        if (!orderId.equals(re.get("pay_trace"))) {
            throw new UserOperateException(500, "原支付记录与匹配到的支付记录不一致");
        }

        Map<String, Object> params = new HashMap<>();
        params.put("orderId", re.get("pay_trace").toString());
        Map<String, Object> pay = MysqlDaoImpl.getInstance().findOne2Map("Pay", params, null, null);

        if (pay == null || pay.size() == 0 || StringUtils.mapValueIsEmpty(pay, "payStatus")) {
            throw new UserOperateException(500, "未匹配到支付订单");
        }
        if (!"START".equals(pay.get("payStatus"))) {
            throw new UserOperateException(500, "匹配的订单不是未支付订单，请检查");
        }

        updateResult(payType, pay, re);
        MysqlDaoImpl.commit();

        toResult(200, queryPayResult(pay.get("_id").toString()));
    }


    /**
     * 获取当前登录的代理商
     *
     * @throws Exception
     */
    public Map<String, Object> checkAdmin() throws Exception {
        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
        JSONObject agentInfo = ServiceAccess.callService(msg).getContent();
        if (agentInfo == null || agentInfo.size() == 0) {
            throw new UserOperateException(400, "你无此操作权限");
        }
        return agentInfo;
    }

    /**
     * 平台管理:统计平台支付
     */
    @GET
    @Path("/countPay")
    public void countPay() throws Exception {
        Map<String, Object> agent = checkAdmin();

        long startTime = ControllerContext.getPLong("startTime");
        long endTime = ControllerContext.getPLong("endTime");

        List<Object> params = new ArrayList<>();
        List<String> returnField = new ArrayList<>();

        String where = " where 1=1 and t1.payStatus='SUCCESS'" +
                " and (t3.returnStatus not in ('SUCCESS','FAIL','START') or t3.returnStatus is null)";
        if (startTime != 0) {
            where += " and t1.createTime>=?";
            params.add(startTime);
        }
        if (endTime != 0) {
            where += " and t1.createTime<=?";
            params.add(endTime);
        }
        returnField.add("alipay");
        returnField.add("alipayCount");
        returnField.add("wechat");
        returnField.add("wechatCount");

        // 订单类型
        int[] typeList = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 11};
        StringBuffer filed = new StringBuffer();
        String where2 = " and (t3.returnStatus not in ('SUCCESS','FAIL','START') or t3.returnStatus is null)";

        for (int i = 0, len = typeList.length; i < len; i++) {
            filed.append(",sum(case when t1.payType=4 and t2.orderType=" + typeList[i] + " then t1.totalFee else 0 end) as alipay" + typeList[i] +
                    ",sum(case when t1.payType=4 and t2.orderType=" + typeList[i] + " then 1 else 0 end) as alipay" + typeList[i] + "Count" +
                    ",sum(case when t1.payType=10 and t2.orderType=" + typeList[i] + " then t1.totalFee else 0 end) as wechat" + typeList[i] +
                    ",sum(case when t1.payType=10 and t2.orderType=" + typeList[i] + " then 1 else 0 end) as wechat" + typeList[i] + "Count");

            returnField.add("alipay" + typeList[i]);
            returnField.add("alipay" + typeList[i] + "Count");
            returnField.add("wechat" + typeList[i]);
            returnField.add("wechat" + typeList[i] + "Count");
        }

        String sql = "select " +
                " sum(case when t1.payType=4 then t1.totalFee else 0 end) as alipay" +
                ",sum(case when t1.payType=4 then 1 else 0 end) as alipayCount" +
                ",sum(case when t1.payType=10 then t1.totalFee else 0 end) as wechat" +
                ",sum(case when t1.payType=10 then 1 else 0 end) as wechatCount" +
                filed +
                " from Pay t1" +
                " left join OrderInfo t2 on t1.orderId = t2._id" +
                " left join PayReturn t3 on t1.orderId = t3.orderId" +
                where;
        List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, params);

        if (re == null || re.size() == 0) {
            toResult(200, null);
        } else {
            toResult(200, re.get(0));
        }

    }

}
