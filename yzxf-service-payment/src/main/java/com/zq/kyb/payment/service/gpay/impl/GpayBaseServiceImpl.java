package com.zq.kyb.payment.service.gpay.impl;

import com.zq.kyb.core.dao.ZQUidUtils;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.payment.config.GpayConfig;
import com.zq.kyb.payment.config.PaymentConfig;
import com.zq.kyb.payment.service.GpaymentService;
import com.zq.kyb.payment.service.gpay.util.AcpService;
import com.zq.kyb.payment.service.gpay.util.RequestParamMap;
import com.zq.kyb.payment.service.gpay.util.SDKUtil;
import com.zq.kyb.payment.service.gpay.util.Signature;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zq2014 on 18/11/12.
 */
public class GpayBaseServiceImpl implements GpaymentService {
    private static final Logger logger = Logger.getLogger(GpayBaseServiceImpl.class);
    protected int payType;
    private static final String PRIVATE_KEY = GpayConfig.PRIVATE_KEY;
    private static final String BUSI_API_KEY = GpayConfig.BUSI_API_KEY;
    private static final String PUBLIC_KEY = GpayConfig.PUBLIC_KEY;

    public void setPayType(int payType) {
        this.payType = payType;
    }

    /**
     * @param innerTxNo     内部交易号
     * @param orderNo       订单号
     * @param subject       主题
     * @param totalFee      金额
     * @param body          内容描述
     * @param memberNo      客户编号
     * @param discountPrice 折扣价格
     * @param storeId       店铺ID
     * @param showUrl       支付成功后跳转的url
     * @param authCode      支付宝或微信中的条码
     * @param openId        微信中 jsapi支付方式 openId 是必须的
     * @return
     * @throws Exception
     */
    @Override
    public Map prepay(String innerTxNo, String orderNo, String subject, String totalFee, String body, String memberNo, String discountPrice, String sellerId, String storeId, String showUrl, String authCode, String openId,String channelType) throws Exception {
        throw new RuntimeException("not impl");
    }

    /**
     * 查询支付是否成功
     *
     * @param payId
     * @return
     * @throws Exception
     */

    @Override
    public Map query(String sellerId, String payId) throws Exception {
        Map<String, String> data = RequestParamMap.initBaseMap();
        data.put("subcode", GpayConfig.SUBCODE_CONSUME_QUERY);
        /***，报文体***/
        //TODO  测试时需要补充
        Map<String, Object> pay = MysqlDaoImpl.getInstance().findById2Map("Pay", payId, null, null);
        data.put("transno",pay.get("trId").toString());
        String sql = "SELECT subbusino FROM seller WHERE _id=?";
        List<String> returnField = new ArrayList<>();
        returnField.add("subbusino");
        List<Object> params = new ArrayList<>();
        params.add(sellerId);
        List<Map<String,Object>> seller = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        //data.put("subbusino", "B18121200000346");//子商户号,测试时由贵商银行给账户B18112600001423
        data.put("subbusino", seller.get(0).get("subbusino").toString());//子商户号,测试时由贵商银行给账户B18112600001423
        data.put("orderno", pay.get("orderNo").toString());
        data.put("serialno", payId);


        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + BUSI_API_KEY;

        String sign = Signature.rsa256Sign(content, PRIVATE_KEY, "UTF-8");
        data.put("sign", sign);
        String url = GpayConfig.SINGLE_QUERY_URL;
        Map<String, String> rspData = AcpService.post(data, url, GpayConfig.ENCODE);
        if (!rspData.isEmpty()) {
            boolean flag = AcpService.validate256Sign(rspData, PUBLIC_KEY, BUSI_API_KEY, GpayConfig.ENCODE);
            if (flag) {
                rspData.put("verifyflag", "验证签名成功");
                if ("00000".equals(rspData.get("respcode"))
                        && "0".equals(rspData.get("status"))) {
                    rspData.put("isSuccess", "true");
                } else {
                    rspData.put("isSuccess", "false");
                }
            } else {
                rspData.put("verifyflag", "验证签名失败");
                rspData.put("isSuccess", "false");
            }
        } else {
            logger.error("未获取到返回报文或返回http状态码非200");
        }
        return rspData;
    }

    /**
     * 第三方支付服务器异步通知支付结果
     *
     * @param reqParams
     * @return 是否有效
     * @throws Exception
     */
    @Override
    public Map notifi(String sellerId, Map reqParams) throws Exception {
        Map configMap = PaymentConfig.getPayConfig(sellerId, PaymentConfig.PAY_TYPE_GPAY);
        if (!reqParams.isEmpty()) {
            Map<String, String> params = new HashMap<String, String>();
            for (Iterator iter = reqParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                if (reqParams.get(name) instanceof List) {
                    JSONArray values = (JSONArray) reqParams.get(name);
                    String valueStr = values.toString();
                    params.put(name, valueStr);
                } else {
                    String values = reqParams.get(name).toString();
                    params.put(name, values);
                }
            }

            if (AcpService.validate256Sign(params, PUBLIC_KEY, BUSI_API_KEY, GpayConfig.ENCODE)){
                logger.info("验证签名成功");
                reqParams.put("verifyflag", "验证签名成功");
                if ("00000".equals(reqParams.get("respcode"))) {//如果查询交易成功
                    //处理被查询交易的应答码逻辑
                    String status = (String) reqParams.get("status");
                    if ("0".equals(status)) {
                        //交易成功
                        reqParams.put("isSuccess", "true");
                        reqParams.put("respmsg", "交易成功");
                    } else if ("1".equals(status) ||
                            "9".equals(status) ||
                            "4".equals(status)) {
                        //需再次发起交易状态查询交易
                        //TODO
                        reqParams.put("isSuccess", "false");
                        reqParams.put("respmsg", "交易失败");
                    }
                }
            } else {
                reqParams.put("isSuccess", "false");
                reqParams.put("verifyflag", "验证签名失败");
                logger.error("验证签名失败");
                //TODO 检查验证签名失败的原因
            }
        } else {
            //未返回正确的http状态
            logger.error("未获取到返回报文或返回http状态码非200");
        }
        return reqParams;
    }

    /**
     * 支付退款
     *
     * @param reqParams
     * @return
     * @throws Exception
     */
    @Override
    public Map refund(String sellerId, Map reqParams) throws Exception {

        //TODO 以下两项测试时由贵商银行给定
        String sql = "SELECT subbusino FROM seller WHERE _id=?";
        List<String> returnField = new ArrayList<>();
        returnField.add("subbusino");
        List<Object> params = new ArrayList<>();
        params.add(sellerId);
        List<Map<String,Object>> seller = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        reqParams.put("subbusino", seller.get(0).get("subbusino").toString());//子商户号,测试时由贵商银行给账户B18112600001423
        //reqParams.put("subbusino", "B18121200000346");//子商户号,测试时由贵商银行给账户B18112600001423
        reqParams.put("currency", GpayConfig.CURRENCY);
        reqParams.remove("trtype");
        reqParams.put("trtype","GP00021");
        String content = Signature.getSignCheckContentV1(reqParams);
        content = content + "&" + BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, PRIVATE_KEY, GpayConfig.ENCODE);
        reqParams.put("sign", sign);
        reqParams.put("sign_type","RSA");
        String url = GpayConfig.BACK_TRANS_URL;
        Map<String, String> rspData = AcpService.post(reqParams, url, GpayConfig.ENCODE);
        if (!rspData.isEmpty()) {
            boolean flag = AcpService.validate256Sign(rspData, PUBLIC_KEY, BUSI_API_KEY, GpayConfig.ENCODE);
            if (flag) {
                rspData.put("verifyflag", "验证签名成功");
                if ("00000".equals(rspData.get("respcode"))) {
                    rspData.put("isSuccess", "true");
                }
            } else {
                rspData.put("verifyflag", "验证签名失败");
                rspData.put("isSuccess", "false");
                logger.error("验证签名失败");
            }
        } else {
            rspData.put("isSuccess", "false");
            logger.error("未获取到返回报文或返回http状态码非200");
        }
        return rspData;
    }

    /**
     * 退款查询
     *
     * @param sellerId
     * @param payId
     * @param returnId
     * @return
     * @throws Exception
     */
    @Override
    public Map refundQuery(String sellerId, String payId, String returnId) throws Exception {

        Map<String, String> data = RequestParamMap.initBaseMap();
        data.put("subcode", GpayConfig.SUBCODE_REFUND_QUERY);
        //TODO  此号为测试平台账号，正式环境需要更换成生产环境的账号
        String sql = "SELECT subbusino FROM seller WHERE _id=?";
        List<String> returnField = new ArrayList<>();
        returnField.add("subbusino");
        List<Object> params = new ArrayList<>();
        params.add(sellerId);
        List<Map<String,Object>> seller = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        data.put("subbusino", seller.get(0).get("subbusino").toString());//子商户号,测试时由贵商银行给账户B18112600001423
        //data.put("subbusino", "B18121200000346");//子商户号,测试时由贵商银行给账户B18112600001423
        data.put("refundno",returnId);
        data.put("orderno", payId);
        data.put("tradeno", "ORDE2018112700003229");
        Map<String,Object> pay = MysqlDaoImpl.getInstance().findById2Map("pay",payId,null,null);
        data.put("prv_transno", pay.get("trId").toString());
        data.put("respmsg", "测试");
        data.put("serialno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));
        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);
        String url = GpayConfig.REFUND_QUERY_URL;
        Map<String, String> rspData = AcpService.post(data, url, GpayConfig.ENCODE);
        if (!rspData.isEmpty()) {
            boolean flag = AcpService.validate256Sign(rspData, PUBLIC_KEY, PRIVATE_KEY, GpayConfig.ENCODE);
            if (flag) {
                rspData.put("verifyflag", "验证签名成功");
                if ("00000".equals(rspData.get("respcode"))
                        && "00000".equals("origRespcode")) {
                    rspData.put("isSuccess", "true");
                }
            } else {
                rspData.put("verifyflag", "验证签名失败");
                rspData.put("isSuccess", "false");
                logger.error("验证签名失败");
            }
        } else {
            logger.error("未获取到返回报文或返回http状态码非200");
        }
        return rspData;
    }

    /**
     * 平台合作商户绑定
     *
     * @param
     * @return
     */
    public Map bindECP(Map<String, Object> map) throws Exception {

        Map<String, String> data = RequestParamMap.initBaseMap();
        data.put("version", GpayConfig.VERSION);                //版本号
        data.put("encode", GpayConfig.ENCODE);              //字符集编码 可以使用UTF-8,GBK两种方式
        data.put("certid", GpayConfig.CERTID);             	//证书编号
        data.put("signMethod",GpayConfig.SIGN_METHOD);     //签名方法
        data.put("trtype", "GP00031");
        data.put("subcode",GpayConfig.SUBCODE_BINDECP);
        Date date = new Date();
        data.put("trdate", new SimpleDateFormat("yyyyMMdd").format(date));
        data.put("trtime", new SimpleDateFormat("hhmmss").format(date));
        /***商户接入参数***/
        // TODO  此号为测试平台账号，正式环境需要更换成生产环境的账号
        data.put("busino", "B18121200000278");//商户号,测试时由贵商银行给账户
        data.put("subbusino", map.get("subbusino").toString());//子商户号,测试时由贵商银行给账户
        data.put("assignno",map.get("assignno").toString());
        data.put("rate",map.get("rate").toString());
        data.put("qyfile",map.get("filename").toString());//暂时没有签约文件名，以贵商给的子商户行暂时代替
        data.put("serialno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));

//        String detail = detail(assignno,lstP,lstR);
//        data.put("detail",detail);

        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);
        data.put("sign_type","RSA");
        String url = GpayConfig.BIND_TRANS_URL;
        System.out.println("绑定=="+JSONObject.fromObject(data));
        Map<String, String> rspData = AcpService.post(data, url, GpayConfig.ENCODE);
        System.out.println("绑定返回=="+JSONObject.fromObject(rspData));
        List<String> returnOpearte = new ArrayList<>();
        returnOpearte.add("pendingId");
        returnOpearte.add("_id");
        List<Object> param = new ArrayList<>();
        param.add(map.get("subbusino").toString());
        String str="SELECT pendingId,_id FROM seller WHERE subbusino=?";
        List<Map<String, Object>> seller = MysqlDaoImpl.getInstance().queryBySql(str,returnOpearte,param);
        String subbusino = "\'" + map.get("subbusino").toString() + "\'";
        if (!rspData.isEmpty()) {
            boolean flag = AcpService.validate256Sign(rspData, PUBLIC_KEY, BUSI_API_KEY, GpayConfig.ENCODE);
            if (flag) {
                rspData.put("verifyflag", "验证签名成功");
                if ("00000".equals(rspData.get("respcode"))) {
                    String fiel = "UPDATE seller SET status= 7 WHERE subbusino=" + subbusino;
                    MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(fiel);
                    String pendingId = "\'" + seller.get(0).get("pendingId").toString() + "\'";
                    String explain = "\'" + rspData.get("respmsg").toString() + "\'";
                    String fsql = "UPDATE userpending SET status=1,`explain`=" + explain + " WHERE _id=" + pendingId;
                    MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(fsql);
                    MysqlDaoImpl.commit();
                    rspData.put("isSuccess", "true");
                }
            }
        } else {
            String fiel = "UPDATE seller SET status= 8 WHERE subbusino=" + subbusino;
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(fiel);
            String pendingId = "\'" + seller.get(0).get("pendingId").toString() + "\'";
            String explain = "\'" + rspData.get("respmsg").toString() + "\'";
            String fsql = "UPDATE userpending SET status=3,`explain`=" + explain + " WHERE _id=" + pendingId;
            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(fsql);
            MysqlDaoImpl.commit();
            rspData.put("isSuccess", "false");
            logger.error("未获取到返回报文或返回http状态码非200");
        }
        return rspData;
    }

    /**
     * 平台合作商户解绑
     *
     * @param sellerId
     * @return
     */
    public Map unbindECP(String sellerId) throws Exception {
        List<String> returnField = new ArrayList<>();
        returnField.add("subbusino");
        List<Object> params = new ArrayList<>();
        params.add(sellerId);
        String sql="SELECT subbusino FROM seller WHERE _id=?";
        List<Map<String, Object>> seller = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
        Map<String, String> data = RequestParamMap.initBaseMap();
        data.put("subcode", GpayConfig.SUBCODE_UNBINDECP);

        // TODO  此号为测试平台账号，正式环境需要更换成生产环境的账号
        data.put("subbusino", seller.get(0).get("subbusino").toString());//子商户号,测试时由贵商银行给账户B18112600001423
        data.put("serialno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));

        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);
        String url = GpayConfig.UNBIND_TRANS_URL;
        Map<String, String> rspData = AcpService.post(data, url, GpayConfig.ENCODE);
        System.out.println("解绑=="+ JSONObject.fromObject(rspData));
        if (!rspData.isEmpty()) {
            boolean flag = AcpService.validate256Sign(rspData, PUBLIC_KEY, BUSI_API_KEY, GpayConfig.ENCODE);
            if (flag) {
                rspData.put("verifyflag", "验证签名成功");
                if ("00000".equals(rspData.get("respcode"))) {
                    rspData.put("isSuccess", "true");
                }
            }
        } else {
            rspData.put("isSuccess", "false");
            logger.error("未获取到返回报文或返回http状态码非200");
        }
        return rspData;
    }

    /**
     * 平台对账方法,生成csv格式对账文件,需要登录贵商ftp服务器下载
     *
     * @param checkDate 对账日期
     * @return
     * @throws Exception
     */
    public Map downloadBill(String checkDate) throws Exception {

        Map<String, String> data = RequestParamMap.initBaseMap();
        data.put("subcode", GpayConfig.SUBCODE_BILL);
        /***报文体参数***/
        data.put("checkdate", checkDate);//yyyyMMdd格式
        data.put("serialno", ZQUidUtils.genUUID().replaceAll("-", "").replaceAll("_", ""));

        data = SDKUtil.filterBlank(data);
        String content = Signature.getSignCheckContentV1(data);
        content = content + "&" + BUSI_API_KEY;
        String sign = Signature.rsa256Sign(content, PRIVATE_KEY, GpayConfig.ENCODE);
        data.put("sign", sign);

        String url = GpayConfig.FILE_TRANS_URL;
        Map<String, String> rspData = AcpService.post(data, url, GpayConfig.ENCODE);
        if (!rspData.isEmpty()) {
            boolean flag = AcpService.validate256Sign(rspData, PUBLIC_KEY, BUSI_API_KEY, GpayConfig.ENCODE);
            if (flag) {
                rspData.put("verifyflag", "验证签名成功");
                if ("00000".equals(rspData.get("respcode"))) {
                    rspData.put("isSuccess", "true");
                }
            }
        } else {
            logger.error("未获取到返回报文或返回http状态码非200");
        }
        return rspData;
    }

    /**
     * 根据合作商集合对象和费率集合组装成jason数组字符串
     *
     * @param assignno
     * @param lstP
     * @param lstR
     * @return
     */
    public String detail(String assignno, List<String> lstP, List<String> lstR) {
        if (!lstP.isEmpty() && !lstR.isEmpty()) {
            String detail;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < lstP.size(); i++) {
                if (null != lstP.get(i) && !"".equals(lstP.get(i))
                        && null != lstR.get(i) && !"".equals(lstR.get(i))) {
                    sb.append("{\"assignno\":\"" + assignno + "\",\"partner\":\"" + lstP.get(i) + "\",\"rate\":\"" + lstR.get(i) + "\"}");
                    sb.append(",");
                }
            }
            detail = sb.toString();
            //如果发生拼接,则把最后一个逗号去掉
            detail = detail.length() > 0 ? detail.substring(0, detail.length() - 1) : detail;
            if (!detail.isEmpty()) {
                detail = "[" + detail + "]";
            }
            return detail;
        } else {
            return null;
        }
    }
}
