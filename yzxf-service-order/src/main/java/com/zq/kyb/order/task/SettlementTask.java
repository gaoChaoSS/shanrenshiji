package com.zq.kyb.order.task;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.order.action.OrderInfoAction;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by haozigg on 17/4/6.
 */
public class SettlementTask implements Runnable {
    @Override
    public void run() {
        //等服务器加载完成
        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Calendar startCal = new GregorianCalendar();
        Calendar nowCal = new GregorianCalendar();
        startCal.set(startCal.get(Calendar.YEAR), startCal.get(Calendar.MONDAY), startCal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        startCal.set(Calendar.DAY_OF_MONTH, startCal.getActualMinimum(Calendar.DAY_OF_MONTH));
        startCal.set(Calendar.MILLISECOND, 0);
        Long startTime = startCal.getTimeInMillis();
        nowCal.set(Calendar.HOUR_OF_DAY, 0);
        nowCal.set(Calendar.MINUTE, 0);
        nowCal.set(Calendar.SECOND, 0);
        nowCal.set(Calendar.MILLISECOND, 0);
        long nowTime = nowCal.getTimeInMillis();
        if (startTime == nowTime) {
            try {
                new OrderInfoAction().createAgentAccountMonth();
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
}
