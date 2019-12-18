package com.zq.kyb.common.action;

import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceFactory;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仅用于后端管理访问
 */
public class SettingAction extends BaseActionImpl {


    public static String ALL_TYPE = "all";//全局设置类型

    public static boolean isDebug() throws Exception {
        return ("1".equals(SettingAction.getSettingMapByType(SettingAction.ALL_TYPE).get("isDebug")));
    }

    @GET
    @Path("/site")
    @Seller
    public void site() throws Exception {
        Map<String, Object> p = new HashMap<String, Object>();
        p.put("type", "site");
        List<Map<String, Object>> li = dao.findAll2Map("Setting", p, null, new String[]{"name", "title", "value"}, Dao.FieldStrategy.Include);
        JSONObject rObj = new JSONObject();
        rObj.put("items", li);
        toResult(200, rObj);
    }

    @GET
    @Path("/valueMapByType")
    public void valueMapByType() throws Exception {
        String type = ControllerContext.getPString("type");
        JSONObject rObj = getSettingMapByType(type);
        toResult(200, rObj);
    }

    public static JSONObject getSettingMapByType(String type) throws Exception {
        if (StringUtils.isEmpty(type)) {
            throw new UserOperateException(400, "type 为空!");
        }

        String key = CacheServiceFactory.cache_prefix_systemSetting + type;
        String str = CacheServiceFactory.getInc().getCache(key);
        JSONObject rObj;
        if (str == null) {
            List<Object> p = new ArrayList<>();
            p.add(type);
            List<String> re = new ArrayList<>();
            re.add("name");
            re.add("value");
            List<Map<String, Object>> li = MysqlDaoImpl.getInstance().queryBySql("select name,value from Setting where type=?", re, p);
            rObj = new JSONObject();
            for (Map<String, Object> item : li) {
                rObj.put(item.get("name"), item.get("value"));
            }
            CacheServiceFactory.getInc().putCache(key, rObj.toString(), 3600 * 24 * 365);//保存1年
        } else {
            rObj = JSONObject.fromObject(str);
        }
        rObj = rObj == null ? new JSONObject() : rObj;
        return rObj;
    }

    @Override
    public void save() throws Exception {
        //检查字段
        String type = ControllerContext.getPString("type");
        String name = ControllerContext.getPString("name");
        String value = ControllerContext.getPString("value");
        if (StringUtils.isEmpty(type)) {
            throw new UserOperateException(400, "type 为空!");
        }
        if (StringUtils.isEmpty(name)) {
            throw new UserOperateException(400, "name 为空!");
        }
        if (StringUtils.isEmpty(value)) {
            throw new UserOperateException(400, "value 为空!");
        }
        String key = CacheServiceFactory.cache_prefix_systemSetting + type;
        CacheServiceFactory.getInc().removeCache(key);//删除cache

        super.save();
    }

    @Override
    public void del() throws Exception {
        super.del();
        String key = CacheServiceFactory.cache_prefix_systemSetting + "*";
        CacheServiceFactory.getInc().removeCache(key);//删除cache
    }

    @Override
    public void deleteMore() throws Exception {
        super.deleteMore();
        String key = CacheServiceFactory.cache_prefix_systemSetting + "*";
        CacheServiceFactory.getInc().removeCache(key);//删除cache
    }

    @GET
    @Path("/getSystemTime")
    public void getSystemTime() throws Exception {
        JSONObject rObj = new JSONObject();
        rObj.put("time", System.currentTimeMillis());
        toResult(200, rObj);
    }
}
