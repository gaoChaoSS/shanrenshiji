package com.zq.kyb.order.task;

import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.order.action.OrderInfoAction;
import com.zq.kyb.order.action.PensionAction;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PensionInsureTask implements Runnable {
    @Override
    public void run() {
        //等服务器加载完成
        try {
            Thread.sleep(5 * 60 * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            List<Object> params = new ArrayList<>();
            List<String> returnFields = new ArrayList<>();
            returnFields.add("memberId");
            returnFields.add("_id");
            returnFields.add("pensionCount");
            returnFields.add("insureCountUse");
            returnFields.add("insureCount");

            String sql = "select" +
                    " t2.memberId" +
                    ",t2._id" +
                    ",t2.pensionCount" +    //养老金总金额
                    ",t2.insureCountUse" +  //已投保金额
                    ",t2.insureCount" +     //未投保金额
                    " from Member t1" +
                    " inner join MemberPensionAccount t2 on t1._id = t2.memberId" +
                    " where t1.canUse = true and t1.isActive = true";
            List<Map<String,Object>> re = MysqlDaoImpl.getInstance().queryBySql(sql,returnFields,params);

            if (re != null && re.size() > 0) {
                for (Map<String, Object> item : re) {
                    try {
                        PensionAction.checkPension(item);
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
