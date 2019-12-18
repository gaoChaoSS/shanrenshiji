package com.zq.kyb.order.task;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: BusiStatusQueryTask.java
 * @Description: 商户开户状态查询
 * @Author: TangHaiHong
 * @Date: 2019-03-11
 * @Version: 1.0
 **/
public class BusiStatusQueryTask implements Runnable {
    private static final String BUSI_SUCCESS = "0"; //贵商开户通过
    private static final String BUSI_FALSE ="4"; //贵商开户失败
    @Override
    public void run() {
        //等服务器加载完成
        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Map<String,String> data = new HashMap<>();
            data.put("version","1.0.0");                //版本号
            data.put("encode", "UTF-8");              //字符集编码 可以使用UTF-8,GBK两种方式
            data.put("certid","GLPH081202000001");             	//证书编号
            data.put("signMethod","01");     //签名方法
            data.put("trtype", "GP00062");
            data.put("subcode","guokai.gpay.querybusistatus");
            data.put("busino", "B18121200000278");//商户号,测试时由贵商银行给账户 B18102500001445
            Date date = new Date();
            data.put("trdate", new SimpleDateFormat("yyyyMMdd").format(date));
            data.put("trtime", new SimpleDateFormat("hhmmss").format(date));
            data.put("serialno", UUID.randomUUID().toString());
            List<String> returnField = new ArrayList<>();
            returnField.add("subbusino");
            returnField.add("_id");
            returnField.add("phone");
            returnField.add("contactPerson");
            returnField.add("integralRate");
            returnField.add("pendingId");
            List<Object> params = new ArrayList<>();
            params.add(4);
            String sql="SELECT subbusino,phone,contactPerson,integralRate,pendingId,_id FROM seller WHERE status=?";
            List<Map<String, Object>> seller = MysqlDaoImpl.getInstance().queryBySql(sql,returnField,params);
            for (Map<String,Object> map: seller) {
                Message msg = Message.newReqMessage("1:POST@/payment/Gpay/queryBusiStatus");
                msg.getContent().put("subbusino", map.get("subbusino").toString());
                JSONObject sell = ServiceAccess.callService(msg).getContent();
                if (!StringUtils.mapValueIsEmpty(sell,"isSuccess")){
                    String status = sell.getString("status");
                    Map<String, Object> sta = new HashMap<>();
                    sta.put("_id", map.get("_id"));
                    if (BUSI_SUCCESS.equals(status) || BUSI_FALSE.equals(status)) {
                        if (BUSI_SUCCESS.equals(status)) {
                            sta.put("canUse", true);
                            Message message = Message.newReqMessage("1:POST@/payment/Gpay/bindECP");
                            message.getContent().put("subbusino", map.get("subbusino"));
                            message.getContent().put("rate", Double.valueOf(map.get("integralRate").toString())/100);
                            message.getContent().put("filename", map.get("subbusino"));
                            message.getContent().put("assignno","0");
                            JSONObject bind = ServiceAccess.callService(message).getContent();
                            sta.put("isSuccess", bind.get("isSuccess"));
                        } else if (BUSI_FALSE.equals(status)) {
                            sta.put("isSuccess", "false");
                            String id = "\'" + sta.get("_id").toString() + "\'";
                            String fiel = "UPDATE seller SET status=6 WHERE _id=" + id;
                            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(fiel);
                            String pendingId = "\'" + map.get("pendingId").toString() + "\'";
                            String explain = "\'" + sell.get("respmsg").toString() + "\'";
                            String fsql = "UPDATE userpending SET status=3,`explain`=" + explain + " WHERE _id=" + pendingId;
                            MysqlDaoImpl.getInstance().getConn().createStatement().executeUpdate(fsql);
                            MysqlDaoImpl.commit();
                        }
                        //短信通知商户绑定结果
                        msg = Message.newReqMessage("1:PUT@/common/Sms/checkSendBind");
                        msg.getContent().put("phone", map.get("phone"));
                        msg.getContent().put("isSuccess",sta.get("isSuccess"));
                        ServiceAccess.callService(msg);
                    }
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
            try {
                MysqlDaoImpl.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            MysqlDaoImpl.clearContext();
        }
    }
}
