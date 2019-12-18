package com.zq.kyb.common.action;


import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hujoey on 16/5/2.
 */
public class AreaAction extends BaseActionImpl {

    /**
     * 获取所有有商户的区域
     */
    @GET
    @Path("/getAllSellerArea")
    public void getAllSellerArea() throws Exception {

        String sql = "select " +
                "t1.name as area" +
                ",t1._id as areaId" +
                ",t1.pid as areaPid" +
                ",t3.name as sellerName" +
                ",t1._id as sellerId" +
                ",t1.pid as areaPid" +
                "from Area t1" +
                " inner join L_Seller_Area t2 on t2.AreaId=t1._id" +
                " inner join Seller t3 on t2.SellerId=t3._id" +
                " where t3.canUse is true";
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql(sql, null, null);
        Map<String, Object> allp = new HashMap<String, Object>();
        //查找所有父亲
        for (Map<String, Object> item : li) {
            String pid = (String) item.get("pid");
            while (pid != "-1") {
                if (allp.get(pid) != null) {
                    Map<String, Object> map = MysqlDaoImpl.getInstance().findById2Map("Area", pid, new String[]{"name", "pid", "level"}, Dao.FieldStrategy.Include);
                    allp.put(pid, map);
                }
            }
        }
        Map<String, Object> re = new HashMap<String, Object>();
        re.put("storeList", re);
        re.put("areaMap", allp);
        toResult(200, re);
    }
}


