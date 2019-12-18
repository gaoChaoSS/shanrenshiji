package com.zq.kyb.common.action;

import com.zq.kyb.common.sms.EmpUtils;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.util.Date;
import java.util.Map;

/**
 */
public class SmsAction extends BaseActionImpl {


    /**
     * 要严格注意防止攻击
     *
     * @throws Exception
     */
    @PUT
    @Path("/getCheckCode")
    public void getCheckCode() throws Exception {
        //用户手机
        String number = ControllerContext.getPString("loginName");
        //检查数据
        //客服端编码:时间戳%5=a,时间戳%7=b,时间戳%9=c,秒%5=d,秒%7=e,秒%9=f,传到服务器的变量check=a+b+c+时间戳+d+e+f
        String check = ControllerContext.getPString("c");

        if (check != null) {
            String a = check.substring(0, 1);
            String b = check.substring(1, 2);
            String c = check.substring(2, 3);
            String time = check.substring(3, check.length() - 3);
            String d = check.substring(check.length() - 3, check.length() - 2);
            String e = check.substring(check.length() - 2, check.length() - 1);
            String f = check.substring(check.length() - 1, check.length());

            long t = Long.parseLong(time);
            long miao = new Date(t).getSeconds();
            if (t % 5 != Long.parseLong(a)) {
                Logger.getLogger(this.getClass()).info("--- a的校验不通过");
                throw new UserOperateException(400, "无效请求!");
            }
            if (t % 7 != Long.parseLong(b)) {
                Logger.getLogger(this.getClass()).info("--- b的校验不通过");

                throw new UserOperateException(400, "无效请求!");
            }
            if (t % 9 != Long.parseLong(c)) {
                Logger.getLogger(this.getClass()).info("--- c的校验不通过");

                throw new UserOperateException(400, "无效请求!");
            }
            if (miao % 5 != Long.parseLong(d)) {
                Logger.getLogger(this.getClass()).info("--- d的校验不通过");

                throw new UserOperateException(400, "无效请求!");
            }
            if (miao % 7 != Long.parseLong(e)) {
                Logger.getLogger(this.getClass()).info("--- e的校验不通过");

                throw new UserOperateException(400, "无效请求!");
            }
            if (miao % 9 != Long.parseLong(f)) {
                Logger.getLogger(this.getClass()).info("--- f的校验不通过");

                throw new UserOperateException(400, "无效请求!");
            }
        }


        //if (ControllerContext.getContext().getSessionMap().get("smsCodeCheck") == null) {
        //     throw new UserOperateException(400, "无效请求!");
        // }

        int timeout = 60;

        String key = "sms_check_" + ControllerContext.getContext().getDeviceId();
        if (JedisUtil.get(key) == null) {

            String type = ControllerContext.getPString("type");
            Map<String, Object> result = EmpUtils.getCheckCode(type, number);

            toResult(200, result);
            JedisUtil.expire(key, timeout);
        } else {
            throw new UserOperateException(400, "请求间隔必须大于" + timeout + "秒!");
        }
    }

    /**审核通过以后发送短信
     *
     */
    @PUT
    @Path("/sendAuditIsOk")
    public void sendAuditIsOk() throws Exception {
        //用户手机
        String mobile = ControllerContext.getPString("mobile");
        //申请成功的类型
        String type = ControllerContext.getPString("type");
        //账号
        String loginName = ControllerContext.getPString("loginName");
        //短信内容
        String sendInfo = ControllerContext.getPString("sendInfo");
        EmpUtils.sendAuditResults(mobile,type,loginName,sendInfo);
    }

    /**
     *购买商品发送短信
     */
    @PUT
    @Path("/checkSendPay")
    public void checkSendPay() throws Exception {
        JSONObject json = ControllerContext.getContext().getReq().getContent();
        if(StringUtils.mapValueIsEmpty(json,"userPhone")){
            throw new UserOperateException(500,"发送短信失败:获取会员手机号码失败");
        }
        if(StringUtils.mapValueIsEmpty(json,"seller")){
            throw new UserOperateException(500,"发送短信失败:获取商家数据失败");
        }
        if(StringUtils.mapValueIsEmpty(json,"totalMoney") || StringUtils.mapValueIsEmpty(json,"product")
                || StringUtils.mapValueIsEmpty(json,"pensionMoney")){
            throw new UserOperateException(500,"发送短信失败:获取订单数据失败");
        }

        EmpUtils.sendPayInfo(json,"member");
    }
    /**
     *结算商品发送短信
     * {0}在您的{1}消费了{2}元，已到账。【普惠生活】
     */
    @PUT
    @Path("/checkSendPaySeller")
    public void checkSendPaySeller() throws Exception {
        JSONObject json = ControllerContext.getContext().getReq().getContent();
        if(StringUtils.mapValueIsEmpty(json,"userPhone")){
            throw new UserOperateException(500,"发送短信失败:获取接受短信的手机号码失败");
        }
        if(StringUtils.mapValueIsEmpty(json,"memberName")){//可以是会员名字或手机号码
            throw new UserOperateException(500,"发送短信失败:获取会员失败");
        }
        if(StringUtils.mapValueIsEmpty(json,"sellerName")){
            throw new UserOperateException(500,"发送短信失败:获取商家数据失败");
        }
        if(StringUtils.mapValueIsEmpty(json,"totalMoney")){
            throw new UserOperateException(500,"发送短信失败:获取订单数据失败");
        }

        EmpUtils.sendPayInfo(json,"seller");
    }

    /**
     *购买商品发送短信
     * 您的{1}收到新的订单请求，请及时发货。【普惠生活】
     */
    @PUT
    @Path("/checkSendPayBookingSeller")
    public void checkSendPayBookingSeller() throws Exception {
        JSONObject json = ControllerContext.getContext().getReq().getContent();
        if(StringUtils.mapValueIsEmpty(json,"userPhone")){
            throw new UserOperateException(500,"发送短信失败:获取接受短信的手机号码失败");
        }
        if(StringUtils.mapValueIsEmpty(json,"sellerName")){
            throw new UserOperateException(500,"发送短信失败:获取商家数据失败");
        }

        EmpUtils.sendPayInfo(json,"sellerBooking");
    }

    /**
     * 激活会员发送固定短信
     */
    @PUT
    @Path("/checkSendActivate")
    public void checkSendActivate() throws Exception {
        String mobile = ControllerContext.getPString("mobile");
        if(StringUtils.isEmpty(mobile)){
            throw new UserOperateException(500,"获取会员手机号码失败");
        }
        EmpUtils.sendActivate(mobile);
    }

    /**
     * 判断验证码是否正确
     *
     * @throws Exception
     */
    @PUT
    @Path("/checkSmsCode")
    public void checkSmsCode() throws Exception {
        String type = ControllerContext.getPString("type");
        String smsCode = ControllerContext.getPString("smsCode");
        String phone = ControllerContext.getPString("phone");
        EmpUtils.checkSmsCode(type, smsCode, phone);
    }

    /**
     * 无货退款,发送给会员
     * 您在{0}购买的{1}商品暂无库存，已将支付金额{2}退款到您账上。【普惠生活】
     */
    @PUT
    @Path("/sendNotStockMember")
    public void sendNotStockMember() throws Exception {
        JSONObject json = ControllerContext.getContext().getReq().getContent();
        if(StringUtils.mapValueIsEmpty(json,"userPhone")){
            throw new UserOperateException(500,"发送短信失败:获取接受短信的手机号码失败");
        }
        if(StringUtils.mapValueIsEmpty(json,"sellerName")){
            throw new UserOperateException(500,"发送短信失败:获取商家数据失败");
        }
        if(StringUtils.mapValueIsEmpty(json,"totalMoney")){
            throw new UserOperateException(500,"发送短信失败:获取订单数据失败");
        }

        EmpUtils.sendMsg(EmpUtils.PAY_NO_STOCK_RETURN,json.get("userPhone").toString(),
                new String[]{
                    json.get("sellerName").toString(),
                    json.get("product").toString(),
                    json.get("totalMoney").toString()
                });
    }

    @PUT
    @Path("/checkSendBind")
    public void checkSendBind() throws Exception{
        String phone = ControllerContext.getPString("phone");
        String isSuccess = ControllerContext.getPString("isSuccess");
        System.out.println("短信=="+JSONObject.fromObject(ControllerContext.getContext().getReq().getContent()));
        if(phone.isEmpty()){
            throw new UserOperateException(500,"发送短信失败:获取接受短信的手机号码失败");
        }
        if(isSuccess.isEmpty()){
            throw new UserOperateException(500,"发送短信失败:获取绑定状态失败");
        }
        if(!isSuccess.isEmpty()){
            EmpUtils.sendBind(phone, isSuccess);
        }
    }
}
