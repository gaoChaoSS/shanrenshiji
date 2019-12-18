package com.zq.kyb.common.sms;


import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EmpUtils {
    public static final String FROM_MOBILE = "mobile";// 手机登录
    public static final String FORM_CHANGE_PASSWORD = "change_password";// 修改密码
    public static final String FORM_REG = "reg";// 注册的时候

    //private static final String LOGIN_MSG = "您的验证码是{0}，本验证码30分钟内有效，请注意保管。【宅家里】";
    private static final String YUNXIN_MSG = "尊敬的用户，您的验证码为{0}，本验证码30分钟内有效，感谢您的使用【普惠生活】";
    private static final String AUDITISOK_MSG = "恭喜您申请的{0}已通过审核,账号是{1}{2}。【普惠生活】";
    private static final String PAY_MSG = "您在{0}购买了{1}元的{2}{3}。【普惠生活】";
    private static final String PAY_SELLER_BOOKING_MSG = "您的店铺{0}收到新的订单请求，请及时发货。【普惠生活】";
    private static final String PAY_SELLER_MSG = "{0}在您的{1}消费了{2}元，已到账。【普惠生活】";
    public static final String PAY_NO_STOCK_RETURN = "您在{0}购买的{1}商品暂无库存，已将支付金额{2}退款到您账上。【普惠生活】";
    private static final String ACTIVATE_MSG = "尊敬的用户，欢迎您加入普惠生活服务平台！我们将竭诚为您提供优质服务。【普惠生活】";
    private static final String BINDECP_SUCCESS_MSG = "尊敬的用户，您在普惠生活服务平台提交的商户入驻申请已成功通过！我们将竭诚为您提供优质服务。【普惠生活】";
    private static final String BINDECP_DEFEATE_MSG = "尊敬的用户，对不起，您在普惠生活服务平台提交的商户入驻申请未能通过！请你检查提交信息是否正确，并从新提交申请。【普惠生活】";


    // 营销短信模板

    public static final Properties prop = new Properties();

    static {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("emp.properties");
            prop.load(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * 处理短信验证码
     *
     * @param mobile
     * @param type
     * @return
     */
    public static Map<String, Object> sendSmsCode(String mobile, String type) {
        Map<String, Object> re = new HashMap<String, Object>();
        re.put("idertifier", "");
        re.put("msg", "验证码短信发送失败！请重试！");

        try {
            if (Boolean.valueOf(prop.getProperty("isSend"))) {

                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                Long start = calendar.getTimeInMillis();
                calendar.add(Calendar.DATE, 1);
                Long end = calendar.getTimeInMillis();

                List<String> returnField = new ArrayList<String>();
                List<Object> paramValues = new ArrayList<Object>();
                paramValues.add(mobile);
                paramValues.add(type);
                paramValues.add(start);
                paramValues.add(end);
                String countStr = "select count(t1._id) as scount from " + Dao.getFullTableName("Sms") + " t1 where phone = ? and type = ? and sendTime>=? and sendTime<?";

                returnField.add("scount");
                Long count = (Long) MysqlDaoImpl.getInstance().queryBySql(countStr, returnField, paramValues).get(0).get("scount");
                //long count = MongoLogDaoImpl.getInstance().findCount(CollectionName.SmsRecord, params);

                if (count <= Long.valueOf(prop.getProperty("sendCount"))) {
                    Map<String, Object> result = sendRandCode_chufa(prop, mobile, type);
                    String res = result.get("idertifier").toString();
                    re.put("code", true);
                    re.put("msg", "发送成功");
                    re.put("idertifier", res);
                } else {
                    re.put("msg", "今日发送已达上限");
                    re.put("code", false);
                }
                return re;
            } else {
                re.put("code", false);
                return re;
            }
        } catch (Exception e) {
            e.printStackTrace();
            re.put("code", false);
            return re;
        }
    }

    public static void sendAuditResults(String userPhone, String type, String userName, String sendInfo) throws Exception {
        //0发送成功,1发送失败
        Map<String, Object> re = new HashMap<>();
        re.put("msg", "1");
        sendAuditIsOk_chufa(prop, userPhone, type, userName, sendInfo);
        if (Boolean.valueOf(prop.getProperty("isSend"))) {
            re.clear();
            re.put("msg", "0");
        }
        if ("1".equals(re.get("msg").toString())) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "短信发送失败!请重试!");
        }
    }

    private static void sendAuditIsOk_chufa(Properties prop, String userPhone, String type, String userName, String sendInfo) throws Exception {
        String msg = MessageFormat.format(AUDITISOK_MSG, type, userName, sendInfo);
        SendSMSCF.send(prop, userPhone, msg);
    }

    public static void sendMsg(String type ,String mobile,String[] arr) throws Exception {
        String msg = MessageFormat.format(type, arr);
        SendSMSCF.send(prop, mobile, msg);
    }

    /**
     * 购买商品,通知返回的养老金
     *
     * @throws Exception
     */
    public static void sendPayInfo(JSONObject json,String type) throws Exception {
        Map<String, Object> re = new HashMap<>();
        re.put("msg", "1");
        if(type.equals("member")){
            sendPayInfo_chufa(prop, json);
        }else if (type.equals("seller")){
            sendPaySeller_chufa(prop, json);
        }else if (type.equals("sellerBooking")){
            sendPaySellerBooking_chufa(prop, json);
        }
        if (Boolean.valueOf(prop.getProperty("isSend"))) {
            re.clear();
            re.put("msg", "0");
        }
        if ("1".equals(re.get("msg").toString())) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "短信发送失败!请重试!");
        }
    }

    private static void sendPayInfo_chufa(Properties prop, JSONObject json) throws Exception {
        String msg = MessageFormat.format(PAY_MSG, json.get("seller"), json.get("totalMoney"), json.get("product"), json.get("pensionMoney"));
        SendSMSCF.send(prop, json.get("userPhone").toString(), msg);
    }
    private static void sendPaySeller_chufa(Properties prop, JSONObject json) throws Exception {
        String msg = MessageFormat.format(PAY_SELLER_MSG, json.get("memberName"), json.get("sellerName"), json.get("totalMoney"));
        SendSMSCF.send(prop, json.get("userPhone").toString(), msg);
    }
    private static void sendPaySellerBooking_chufa(Properties prop, JSONObject json) throws Exception {
        String msg = MessageFormat.format(PAY_SELLER_BOOKING_MSG,json.get("sellerName"));
        SendSMSCF.send(prop, json.get("userPhone").toString(), msg);
    }

    /**
     * 激活会员通知短信
     *
     * @throws Exception
     */
    public static void sendActivate(String mobile) throws Exception {
        Map<String, Object> re = new HashMap<>();
        re.put("msg", "1");

        String msg = MessageFormat.format(ACTIVATE_MSG,new Object());
        SendSMSCF.send(prop, mobile, msg);

        if (Boolean.valueOf(prop.getProperty("isSend"))) {
            re.clear();
            re.put("msg", "0");
        }
        if ("1".equals(re.get("msg").toString())) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "短信发送失败!请重试!");
        }
    }

    /**
     * 商户绑定通知短信
     *
     * @throws Exception
     */
    public static void sendBind(String phone,String isSuccess) throws Exception{
        Map<String, Object> re = new HashMap<>();
        re.put("msg", "1");
        if("false".equals(isSuccess)){
            String msg = MessageFormat.format(BINDECP_DEFEATE_MSG,new Object());
            SendSMSCF.send(prop, phone, msg);
        }
        if (Boolean.valueOf(prop.getProperty("isSend"))) {
            re.clear();
            re.put("msg", "0");
        }
        if ("1".equals(re.get("msg").toString())) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "短信发送失败!请重试!");
        }
    }


    private static Map<String, Object> sendRandCode_chufa(Properties prop, String userPhone, String from) throws Exception {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String exp_time = prop.getProperty("exp_time");
        int random = (int) ((Math.random() * 9 + 1) * 100000);
        String msg = MessageFormat.format(YUNXIN_MSG, random + "");
        // String msg = YUNXIN_MSG.replace("{param1}", random+"");
        // 短信发送成功后，把验证码保存到数据库
        Map<String, Object> result = SendSMSCF.send(prop, userPhone, msg);
        if (result.get("code") != null && !(Boolean) result.get("code")) {
            String errmsg = null;
            if (result.containsKey("msg")) {
                errmsg = (String) result.get("msg");
            }
            errmsg = errmsg == null ? "发送不成功" : errmsg;
            throw new UserOperateException(400, errmsg);
        }
        result.put("idertifier", "");

        Map<String, Object> smsRecord = new HashMap<>();
        String id = UUID.randomUUID().toString();
        smsRecord.put("_id", id);
        smsRecord.put("type", from);
        smsRecord.put("randCode", random + "");
        String resMsg = "";
        String errorCode = (String) result.get("status");
        // 触发短信错误信息
        if (errorCode != null) {
            String res_msg = SendSMSCF.errorMsg.get(errorCode);
            if (res_msg != null) {
                resMsg = res_msg;
            }
        }
        smsRecord.put("resMessage", resMsg);// 错误信息
        smsRecord.put("errorCode", errorCode);// 错误码
        smsRecord.put("resCode", result.get("code"));// 是否已发送
        smsRecord.put("sendTime", System.currentTimeMillis());
        smsRecord.put("createTime", System.currentTimeMillis());
        smsRecord.put("ip", ControllerContext.getContext().getRemoteAddr());
        smsRecord.put("expTime", exp_time);
        smsRecord.put("phone", userPhone);
        smsRecord.put("isUse", false);
        smsRecord.put("sendStatus", 0);// 发送状态，0：已发送，1：发送成功,-1：发送失败
        Logger.getLogger(EmpUtils.class).info(smsRecord);
        MysqlDaoImpl.getInstance().saveOrUpdate("Sms", smsRecord);

        result.put("idertifier", id);
        return result;
    }

    private static String fillStringByArgs(String str, JSONObject json) throws Exception {
        Matcher m = Pattern.compile("\\{param(\\d)\\}").matcher(str);
        while (m.find()) {
            str = str.replace(m.group(), json.getString("param" + (Integer.parseInt(m.group(1)))));
        }
        return str;
    }

    /**
     * 根据当前手机号码,半个小时之内的所有验证码,能匹配其中一个则通过验证
     *
     * @param type     验证码类型
     * @param randCode 用户输入的验证码
     * @param phone    用户请求的手机号
     * @throws Exception
     */
    public static void checkSmsCode(String type, String randCode, String phone) throws Exception {

        // 30分钟内的验证码有效 当前时间 - 30分钟
        String exp_time = prop.getProperty("exp_time");
        long startTime = System.currentTimeMillis() - (60 * 1000 * Integer.valueOf(exp_time));

        // param.put("isUse", new BasicDBObject("$exists", false));
        String sql = "select t1._id from " + Dao.getFullTableName("Sms") + " t1 where isUse = ? and createTime >= ? and phone=? and randCode = ? and type = ?";
        List<String> returnField = new ArrayList<String>();
        List<Object> paramValues = new ArrayList<Object>();
        paramValues.add(false);
        paramValues.add(startTime);
        paramValues.add(phone);
        paramValues.add(randCode);
        paramValues.add(type);
        returnField.add("_id");
        List<Map<String, Object>> smsRecord = MysqlDaoImpl.getInstance().queryBySql(sql, returnField, paramValues);

        //List<Map<String, Object>> smsRecord = MongoLogDaoImpl.getInstance().findAll2Map(CollectionName.SmsRecord, param, null, null, null);
        if (smsRecord.size() > 0) {
            for (Map<String, Object> row : smsRecord) {
                row.put("isUse", true);
                row.put("useTime", System.currentTimeMillis());
                MysqlDaoImpl.getInstance().saveOrUpdate("Sms", row);

            }
        } else {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "错误的短信验证码");
        }
    }

    public static Map<String, Object> getCheckCode(String type, String number) throws Exception {
        //Long preSendTime = (Long) ControllerContext.getContext().getSessionMap().get(type);

//        if (preSendTime != null) {
//            long current = System.currentTimeMillis();
//            if (current <= preSendTime + 90 * 1000) {
//                throw new UserOperateException(400, "请勿在90秒内重复获取验证码");
//            }
//        }
        Map<String, Object> result = new HashMap<String, Object>();

        if (EmpUtils.FROM_MOBILE.equals(type) || EmpUtils.FORM_CHANGE_PASSWORD.equals(type) || EmpUtils.FORM_REG.equals(type)) {
            Map<String, Object> re = EmpUtils.sendSmsCode(number, type);
            result.putAll(re);
        } else {
            throw new UserOperateException(400, "错误的type " + type);
        }
        //ControllerContext.getContext().getSessionMap().put(type, System.currentTimeMillis());
        return result;
    }

    public static void main(String[] args) throws Exception {
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(1455530319585l);
//        System.out.println(calendar.getTime());
//        calendar.setTimeInMillis(1455528575739l);
//        System.out.println(calendar.getTime());
//        sendAuditIsOk_chufa(prop, "13540461624", "1", "a", "123");

//        JSONObject json = new JSONObject();
//        json.put("seller","商家A");
//        json.put("totalMoney","100");
//        json.put("product","口香糖");
//        json.put("pensionMoney","10");
//        json.put("userPhone","13540461624");
//        sendPayInfo(json);
//        sendActivate("13540461624");

//        JSONObject item = new JSONObject();
//        item.put("userPhone","18582553995");
//        item.put("sellerName","红旗连锁");
//        sendPayInfo(item,"sellerBooking");

        sendMsg(PAY_NO_STOCK_RETURN,"13540461624",
                new String[]{"红旗连锁","巧乐兹冰淇淋","5元"});
    }
}
