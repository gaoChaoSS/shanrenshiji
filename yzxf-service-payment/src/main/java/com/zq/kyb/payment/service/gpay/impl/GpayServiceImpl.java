package com.zq.kyb.payment.service.gpay.impl;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.payment.config.AlipayConfig;
import com.zq.kyb.payment.config.GpayConfig;
import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.service.gpay.util.AcpService;
import com.zq.kyb.payment.service.gpay.util.RequestParamMap;
import com.zq.kyb.payment.service.gpay.util.SDKUtil;
import com.zq.kyb.payment.service.gpay.util.Signature;
import com.zq.kyb.payment.utils.ArithUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zq2014 on 18/11/12.
 */
public class GpayServiceImpl extends GpayBaseServiceImpl {
    private static final Logger logger = Logger.getLogger(GpayServiceImpl.class);
    private static final String PRIVATE_KEY = GpayConfig.PRIVATE_KEY;
    private static final String BUSI_API_KEY = GpayConfig.BUSI_API_KEY;

    @Override
    public Map prepay(String payId,
                      String orderNo,
                      String subject,
                      String totalFee,
                      String body,
                      String memberNo,
                      String discountPrice,
                      String sellerId,
                      String storeId,
                      String returnUrl,
                      String authCode,
                      String openId,
                      String channelType) throws Exception {
        Map configMap = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_GPAY);

        JSONObject member = ServiceAccess.getRemoveEntity("crm","Member",memberNo);
        String sql = "SELECT subbusino FROM seller WHERE _id=?";
        List<String> returnField = new ArrayList<>();
        returnField.add("subbusino");
        List<Object> params = new ArrayList<>();
        params.add(sellerId);
        List<Map<String,Object>> seller = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
//        JSONObject orderInfo = ServiceAccess.getRemoveEntity("order","OrderInfo",orderNo);

        String total_fee = String.valueOf(Double.valueOf(ArithUtil.mul(Double.valueOf(totalFee), 100)).intValue());

        Map<String, String> data = RequestParamMap.initBaseMap();
        data.put("subcode",GpayConfig.SUBCODE_CONSUME);
        data.put("notify_url", AlipayConfig.notify_url);//异步调用地址
        data.put("return_url", returnUrl);//返回调用地址
        data.put("serialno", payId);

         /***报文体***/
         //以下为测试环境的商户，正式环境需改正
        data.put("subbusino", seller.get(0).get("subbusino").toString());//子商户号,测试时由贵商银行给账户B18112600001423
        data.put("currency",GpayConfig.CURRENCY);
        data.put("orderno", orderNo);//订单号
        data.put("amt_trans",total_fee);
        data.put("account", memberNo);//会员号
        data.put("regname", "");
        data.put("ordertitle", subject);
        data.put("orderdesc", subject);//订单描述
        data.put("channel_type", channelType);	// 渠道类型 07-PC 08-WAP
        data.put("confflag", "8");
        data.put("ordertype", "0");
        data.put("respcode","00000");
        data.put("respmsg","开始支付");

        // 优惠券和红包券
//        data.put("coupon", "");									// 优惠券
//        data.put("amt_coupon", "");							// 优惠金额
//        data.put("vircoin", "");								// 商户卡券代码
//        data.put("amt_vircoin", "");						// 商户卡券金额
//        data.put("redpack", "");								// 红包券代码
//        data.put("amt_redpack", "");						// 红包券金额
        data.put("amt_total", total_fee);							// 订单交易总金额

//        data.put("transno", transno);//IMP平台订单号，如果此值上送，则表示重新支付

        //微信公众号
        data.put("openid", openId);							// 微信公众平台openid
//        data.put("sub_appid", "");							// 微信公众平台sub_appid
//        data.put("sub_openid", "");

        data = SDKUtil.filterBlank(data);
        String content= Signature.getSignCheckContentV1(data);

        content = content + "&" + BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);
        data.put("sign_type", "RSA");
        String requestFrontUrl = GpayConfig.FRONT_TRANS_URL;
        String html = AcpService.createAutoFormHtml(requestFrontUrl, data, GpayConfig.ENCODE);   //生成自动跳转的Html表单
        data.put("requestHtml",html);
        return data;
    }
}
