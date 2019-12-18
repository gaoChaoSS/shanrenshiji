package com.zq.kyb.order.service;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.util.BigDecimalUtil;

import java.util.*;

/**
 * Created by hujoey on 17/2/23.
 */
public class AgentMoneyAccountService {
    public void addMoneyToAgent(Double profitRatio, double orderCash, String orderId, String memberId, String agentId,int type) throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("_id", UUID.randomUUID().toString());
        map.put("agentId", agentId);
        map.put("orderCash", orderCash);
        map.put("createTime", System.currentTimeMillis());
        map.put("type", type);
        map.put("tradeId", memberId);
        map.put("cashProportion", profitRatio);
        map.put("orderId", orderId);
        MysqlDaoImpl.getInstance().saveOrUpdate("AgentMoneyLog", map);

        //更新账户总额


        String table = "AgentMoneyAccount";
        String lockKey = "Lock_" + table + "_" + agentId;

        try {
            JedisUtil.whileGetLock(lockKey, 25);
            List<Object> p = new ArrayList<>();
            p.add(agentId);
            List<String> r = new ArrayList<>();
            r.add("_id");
            r.add("cashCount");
            r.add("income");
            String sql = "select" +
                    " _id" +
                    ",cashCount" +
                    ",income" +
                    " from " + table +
                    " where agentId=?";

            List<Map<String, Object>> re4 = MysqlDaoImpl.getInstance().queryBySql(sql, r, p);

            if (re4 == null || re4.size() == 0) {
                map.clear();
                map.put("_id", UUID.randomUUID().toString());
                map.put("agentId", agentId);
                map.put("createTime", System.currentTimeMillis());
                map.put("cashCount", orderCash);
                map.put("cashCountUse", 0);
                map.put("income", orderCash);
                MysqlDaoImpl.getInstance().saveOrUpdate(table, map);
            } else {
                map.clear();
                map.put("_id", re4.get(0).get("_id"));
                map.put("updateTime", System.currentTimeMillis());
                map.put("cashCount", BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(new Double(re4.get(0).get("cashCount").toString()), orderCash)));
                map.put("income", BigDecimalUtil.fixDoubleNumProfit(BigDecimalUtil.add(new Double(re4.get(0).get("income").toString()), orderCash)));
                MysqlDaoImpl.getInstance().saveOrUpdate(table, map);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JedisUtil.del(lockKey);
        }
    }
}
