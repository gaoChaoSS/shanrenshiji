package com.zq.kyb.order.task;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.order.action.OrderInfoAction;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by zq2014 on 17/8/14.
 * 检查订单状态为5的订单,7天之后自动变为状态100,并结算分润,养老金
 */
public class OnlineOrderEndTask implements Runnable {
    @Override
    public void run() {
        //等服务器加载完成
        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            //若是在线支付订单,则只需要查父表
            String sql = "select _id from OrderInfo" +
                    " where pid<>-1 and orderType=11 and orderStatus=5 and accountTime<=?" +
                    " order by accountTime desc";
            List<Object> params = new ArrayList<>();
            params.add(System.currentTimeMillis() - 1000 * 60 * 60*24*7);//查询7天之前的订单

            List<String> returnFiled = new ArrayList<>();
            returnFiled.add("_id");
            List<Map<String, Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql, returnFiled, params);
            if (re != null && re.size() > 0) {
                for (Map<String, Object> orderInfo : re) {
                    OrderInfoAction.endOnlineOrder((String) orderInfo.get("_id"));
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
