package com.zq.kyb.account.action;


import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import java.util.*;

public class StoreConfAction extends BaseActionImpl {

    /**
     * 访客访问店铺的基本信息,不登录可以访问
     *
     * @throws Exception
     */
    @GET
    @Seller
    @Member
    @Path("/getStoreConf")
    public void getStoreConf() throws Exception {
        String type = ControllerContext.getPString("type");
        String storeId = ControllerContext.getPString("storeId");
        JSONObject re = getStoreConf(storeId, type);
        toResult(200, re);
    }

    public static JSONObject getStoreConf(String storeId, String type) throws Exception {
        String whereStr = "";
        List<Object> p = new ArrayList<>();
        if (StringUtils.isNotEmpty(type)) {
            p.add(type);
            whereStr += " and type=?";
        }
        if(StringUtils.isNotEmpty(storeId)){
            p.add(storeId);
            whereStr += " and sellerId = ?";
        }
        String sql = "select `_id`,`key`,`value` from  StoreConf where 1=1" + whereStr;
        List<String> reField = new ArrayList<>();
        reField.add("_id");
        reField.add("key");
        reField.add("value");

        List<Map<String, Object>> s = MysqlDaoImpl.getInstance().queryBySql(sql, reField, p);
        JSONObject re = new JSONObject();
        for (Map<String, Object> o : s) {
            Object key = o.get("key");
            if (key != null) {
                re.put(key, o.get("value"));
            }
        }
        return re;
    }
}
