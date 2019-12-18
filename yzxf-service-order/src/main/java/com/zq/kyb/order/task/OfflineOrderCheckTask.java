package com.zq.kyb.order.task;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.order.action.OrderInfoAction;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 主动去查询第三方支付处理结果,如查询到已支付,那么更改订单状态
 * Created by xiaoke on 2017/3/17.
 */
public class OfflineOrderCheckTask implements Runnable {
    @Override
    public void run() {
        //等服务器加载完成
        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            String sql = "select t1._id,t2._id as payId from" +
                    " OrderInfo t1" +
                    " left join Pay t2 on t2.orderId = t1._id" +
                    " where" +
                    " (t1.orderType<>11 or (t1.orderType=11 and t1.pid=-1))" +
                    " and t1.orderStatus=? and t1.createTime >= ? and t2._id is not null" +
                    " order by t1.createTime desc";
            List<Object> params = new ArrayList<>();
            params.add(OrderInfoAction.ORDER_TYPE_BOOKING);
            params.add(System.currentTimeMillis() - (1000 * 60 * 60 * 5));//查5小时之内的订单

            List<String> returnFiled = new ArrayList<>();
            returnFiled.add("_id");
            returnFiled.add("payId");
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFiled, params);
            if (null != re && re.size() > 0) {
                List<String> ids = new ArrayList<>();
                List<String> pay = new ArrayList<>();
                for (Map<String, Object> orderInfo : re) {
                    if (!StringUtils.mapValueIsEmpty(orderInfo, "payId")) {
                        ids.add((String) orderInfo.get("_id"));
                        pay.add((String) orderInfo.get("payId"));
                    }
                }
                try {
                    String idsStr = StringUtils.join(ids.toArray(), ",");
                    String payStr = StringUtils.join(pay.toArray(), ",");
                    Message msgFriend = Message.newReqMessage("1:GET@/payment/Pay/queryPayResultList");
                    msgFriend.getContent().put("orderIdList", idsStr);
                    msgFriend.getContent().put("payIdList", payStr);
                    JSONArray result = ServiceAccess.callService(msgFriend).getContent().getJSONArray("items");
                    for (Object o : result) {
                        JSONObject r = (JSONObject) o;
                        OrderInfoAction.checkOrderStatus(r);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            MysqlDaoImpl.commit();
        } catch (Exception e) {
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
