package com.zq.kyb.order.service;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.util.BigDecimalUtil;

import java.util.*;

/**
 * 关于发卡点汇总表的写操作, 独立出来,为了处理了脏数据,及检查逻辑方便
 * Created by hujoey on 17/2/23.
 */
public class FactorMoneyAccountService {
    /**
     * 分配好的利润写入发卡点汇总表
     *
     * @param royaltyRate
     * @param orderCash
     * @param orderId
     * @param memberId
     * @param factorId
     */
    public void addMoneyToFactor(double royaltyRate, double orderCash, String orderId, String memberId, String factorId,int tradeType) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("_id", UUID.randomUUID().toString());
        map.put("factorId", factorId);
        map.put("orderCash", orderCash);
        map.put("createTime", System.currentTimeMillis());
        map.put("type", tradeType);
        map.put("tradeId", memberId);
        map.put("cashProportion", royaltyRate);
        map.put("orderId", orderId);
        MysqlDaoImpl.getInstance().saveOrUpdate("FactorMoneyLog", map);
        //更新发卡点账户总额


        String table = "FactorMoneyAccount";
        String lockKey = "Lock_" + table + "_" + factorId;
        JedisUtil.whileGetLock(lockKey, 25);

        try {
            List<Object> p = new ArrayList<>();
            p.add(factorId);
            List<String> r = new ArrayList<>();
            r.add("_id");
            r.add("cashCount");
            r.add("income");
            String sql = "select" +
                    " _id" +
                    ",cashCount" +
                    ",income" +
                    " from " + table +
                    " where factorId=?";

            List<Map<String, Object>> re2 = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

            if (re2 == null || re2.size() == 0) {
                map.clear();
                map.put("_id", UUID.randomUUID().toString());
                map.put("factorId", factorId);
                long time = System.currentTimeMillis();
                map.put("createTime", time);
                map.put("updateTime", time);
                map.put("cashCount", orderCash);
                map.put("cashCountUse", 0);
                map.put("income", orderCash);
                MysqlDaoImpl.getInstance().saveOrUpdate(table, map);
            } else {
                map.clear();
                map.put("_id", re2.get(0).get("_id"));
                map.put("updateTime", System.currentTimeMillis());
                map.put("cashCount", BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(new Double(re2.get(0).get("cashCount").toString()), orderCash)));
                map.put("income", BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(new Double(re2.get(0).get("income").toString()), orderCash)));
                MysqlDaoImpl.getInstance().saveOrUpdate(table, map);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisUtil.del(lockKey);
        }
    }
}
