package com.zq.kyb.common.action;

import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.ctrl.ControllerProcess;
import com.zq.kyb.core.dao.CacheServiceFactory;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.secu.CheckServicePermission;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.*;

public class ResourceAction extends BaseActionImpl {
    @Override
    @Seller
    @Member
    public void show() throws Exception {
        String _id = ControllerContext.getPString("_id");


        Map<String, Object> m = dao.findById2Map(entityName, _id, null, null);

        //如果不存在,就添加
        if (SettingAction.isDebug()) {//开发模式,需要添加
            //用于测试那些API被访问到了
            if (m == null && _id.indexOf("+|") != -1) {
                //访问的是API,则自动添加
                String moduleName = ControllerContext.getPString("moduleName");
                String actionName = ControllerContext.getPString("actionName");
                String methodName = ControllerContext.getPString("methodName");
                String actionType = ControllerContext.getPString("actionType");


                if (StringUtils.isNotEmpty(moduleName)) {
                    Map<String, Object> modelObj = dao.findById2Map(entityName, moduleName, null, null);
                    if (modelObj == null) {
                        modelObj = new HashMap<>();
                        modelObj.put("_id", moduleName);
                        modelObj.put("name", moduleName);
                        modelObj.put("type", "model");
                        modelObj.put("pid", "-1");
                        modelObj.put("level", 0);
                        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, modelObj);
                    }
                }
                if (StringUtils.isNotEmpty(actionName)) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("_id", actionName);
                    p.put("pid", moduleName);
                    Map<String, Object> actionObj = dao.findOne2Map(entityName, p, null, null);
                    if (actionObj == null) {
                        actionObj = new HashMap<>();
                        actionObj.put("_id", actionName);
                        actionObj.put("name", actionName);
                        actionObj.put("type", "action");
                        actionObj.put("pid", moduleName);
                        actionObj.put("level", 1);
                        MysqlDaoImpl.getInstance().saveOrUpdate(entityName, actionObj);
                    }
                }

                if (StringUtils.isNotEmpty(actionType)) {
                    m = new HashMap<>();
                    m.put("pid", actionName);
                    m.put("_id", _id);
                    m.put("name", "/" + methodName);
                    m.put("type", "api");
                    m.put("apiType", actionType);
                    m.put("canUse", true);
                    m.put("level", 2);
                    MysqlDaoImpl.getInstance().saveOrUpdate(entityName, m);
                }

            } else {
                String type = (String) m.get("type");
                if ("api".equals(type)) {
                    //记录访问次数,TODO 记录到mongodb
                    // Integer accessTimes = (Integer) m.get("accessTimes");
                    // accessTimes = accessTimes == null ? 0 : accessTimes;
                    // accessTimes++;
                    // m.put("accessTimes", accessTimes);
                    // MysqlDaoImpl.getInstance().saveOrUpdate(entityName, m);
                }
            }

            toResult(Response.Status.OK.getStatusCode(), m);
        } else {
            super.show();
        }
    }

    @PUT
    @Path("/save")
    @Override
    public void save() throws Exception {
        JSONObject values = ControllerContext.getContext().getReq().getContent();
        String pid;
        if (values.containsKey("pid")) {
            // 父亲
            pid = values.getString("pid");
        } else {
            Map<String, Object> my = MysqlDaoImpl.getInstance().findById2Map("Resource", values.getString("_id"), null, null);
            pid = (String) my.get("pid");
        }

        Map<String, Object> fu = MysqlDaoImpl.getInstance().findById2Map("Resource", pid, null, null);
        if (fu != null) {
            Double plavel = Double.valueOf(fu.get("level").toString());
            plavel = plavel == null ? 0 : plavel;
            int level = plavel.intValue() + 1;
            values.put("level", level);// 层级
        } else {
            values.put("level", 0);
        }
        if (values.containsKey("type") && values.containsKey("name")) {
            String type = values.getString("type");
            String name = values.getString("name");
            if ("api".equals(type)) {
                values.put("_id", CheckServicePermission.getAPIStr(1, pid, name, values.getString("apiType")));
            } else if ("action".equals(type)) {
                Map<String, Object> p = new HashMap<>();
                p.put("_id", name);
                Map<String, Object> old = MysqlDaoImpl.getInstance().findOne2Map("Resource", p, null, null);
                if (old != null && !pid.equals(old.get("pid"))) {
                    throw new UserOperateException(400, "添加的数据已存在:/" + pid + "/" + name);
                }
                values.put("_id", name);
            } else if ("model".equals(type)) {
                values.put("_id", name);
            }
        }

        if (values.containsKey("checkStoreId")) {
            updateChild(values, "checkStoreId");
        }

        super.save();
        delUserResourceCache();

        String _id = ControllerContext.getPString("_id");
        CheckServicePermission.updateApiMap("clear", _id, null);
    }

    private void delUserResourceCache() throws Exception {
        Set<String> li = CacheServiceFactory.getInc().getkeys(CacheServiceFactory.cache_prefix_userResources + "*");
        for (String key : li) {
            CacheServiceFactory.getInc().removeCache(key);
        }
    }

    /**
     * 更新所有子孙的某个字段
     *
     * @param values
     * @param field
     * @throws Exception
     */
    private void updateChild(Map<String, Object> values, String field) throws Exception {
        Object value = values.get(field);
        String id = (String) values.get("_id");
        Map<String, Object> p = new HashMap<>();
        p.put("pid", id);

        List<Map<String, Object>> child = MysqlDaoImpl.getInstance().findAll2Map("Resource", p, null, null, null);
        if (child != null) {
            for (Map<String, Object> map : child) {
                map.put(field, value);
                MysqlDaoImpl.getInstance().saveOrUpdate("Resource", map);
                updateChild(map, field);
            }
        }
    }

    /**
     * 清空api的缓存
     *
     * @throws Exception
     */
    @GET
    @Path("/rePutApi")
    public void rePutApi() throws Exception {
        //ControllerProcess.apiMap.clear();
        String myId = ControllerContext.getContext().getCurrentUserId();
        if (StringUtils.isNotEmpty(myId)) {
            //   MemcachedService.getInstance().removeEntity("User_Resource", myId);
        }
    }

    @Override
    public void del() throws Exception {
        String _id = ControllerContext.getPString("_id");
        Map<String, Object> p = new HashMap<>();
        p.put("pid", _id);
        Map<String, Object> li = dao.findOne2Map(entityName, p, null, Dao.FieldStrategy.Include);
        if (li != null && li.size() > 0) {
            throw new UserOperateException(400, "请先删除该数据的子数据!");
        }
        super.del();
        delUserResourceCache();

        CheckServicePermission.updateApiMap("clear", _id, null);
    }

    @Override
    public void deleteMore() throws Exception {
        super.deleteMore();
        delUserResourceCache();

        JSONObject values = ControllerContext.getContext().getReq().getContent();
        JSONArray ids = values.getJSONArray("ids");
        for (Object id : ids) {
            CheckServicePermission.updateApiMap("clear", (String) id, null);
        }
    }

}
