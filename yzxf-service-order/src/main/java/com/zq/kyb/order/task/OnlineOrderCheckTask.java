package com.zq.kyb.order.task;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.order.action.OrderInfoAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by luoyunze on 2017/5/31.
 * 检查 是否已经收货,15天未收货自动收货
 */
public class OnlineOrderCheckTask implements Runnable {
    @Override
    public void run() {
        //等服务器加载完成
        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            //若是在线支付订单,则只需要查父表?
            String sql = "select _id from" +
                    " OrderInfo where orderType=11 and orderStatus = ? and createTime <= ?" +
                    " order by createTime desc";
            List<Object> params = new ArrayList<>();
            params.add(OrderInfoAction.ORDER_TYPE_SENT);
            params.add(System.currentTimeMillis() - (1000 * 60 * 60*24*15));//查询15天之前的订单

            List<String> returnFiled = new ArrayList<>();
            returnFiled.add("_id");
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFiled, params);
            if (re != null && re.size() > 0) {
                for (Map<String, Object> orderInfo : re) {
                    try {
                        OrderInfoAction.updateOnlineOrderByMember((String) orderInfo.get("_id"));
                        MysqlDaoImpl.commit();
                    } catch (Exception e) {
                        MysqlDaoImpl.rollback();
                    }finally {
                        MysqlDaoImpl.clearContext();
                        Thread.sleep(1000);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
