package com.zq.kyb.order.task;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiaoke on 2017/3/17.
 */
public class TaskExecutor {
    private static ScheduledExecutorService executorService = Executors.newScheduledThreadPool(5);
    public static void startTask() {
            executorService.scheduleWithFixedDelay(
                    new OfflineOrderCheckTask(),//查询第三方支付处理结果，并更新订单状态
                    15,
                    24,
                    TimeUnit.HOURS);
            executorService.scheduleWithFixedDelay(
                    new OnlineOrderCheckTask(),//线上订单15天之后更改为自动收货
                    0,
                    1,
                    TimeUnit.DAYS);
            executorService.scheduleWithFixedDelay(
                    new SettlementTask(),//服务站上月收入结算
                    0,
                    1,
                    TimeUnit.DAYS);
            executorService.scheduleWithFixedDelay(
                    new OnlineOrderEndTask(),//线上订单7天之后自动结算分润
                    0,
                    1,
                    TimeUnit.HOURS);
            executorService.scheduleWithFixedDelay(
                    new BusiStatusQueryTask(),//贵商开户审核状态查询
                    0,
                    1,
                    TimeUnit.DAYS);
//            executorService.scheduleWithFixedDelay(
//                    new PensionInsureTask(),
//                    0,
//                    1,
//                    TimeUnit.MINUTES);
        }
}
