package com.zq.kyb.core.secu;

import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceFactory;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于终端用户连接时的API权限检查
 * <p/>
 * 主要有以下几种权限检查:
 * 1.user类型用户
 * 1)检查是否在对应的storeId下,及操作的数据是否为对应的storeId下
 * <p/>
 * 2.member类型用户
 * 1)只能操作自己的数据
 */
public class CheckServicePermission {
    /**
     * 检查执行该请求消息的权限
     *
     * @param message
     */
    public static void check(Message message) throws Exception {
        Map<String, Object> map = Message.actionPathToMap(message.getActionPath());
        int version = (int) map.get("version");
        String actionType = (String) map.get("actionType");
        String moduleName = (String) map.get("moduleName");
        String actionName = (String) map.get("actionName");
        String methodName = (String) map.get("methodName");

        if (version > 50) {
            // 如果版本大于50，可用判定是攻击
            throw new UserOperateException(400, "apiVersion[" + version + "] is error!");
        }


        //如果API不存在且是超级管理员访问就添加到数据库
        String api = getAPIStr(version, actionName, "/" + methodName, actionType);

        //apiMap.clear();
        Map<String, Object> apiDef = (Map<String, Object>) apiMap.get(api);
        if (apiDef == null) {
            //通过服务获取
            Message msg = Message.newReqMessage("1:GET@/common/Resource/show");
            msg.getContent().put("_id", api);
            msg.getContent().put("moduleName", moduleName);//主要用于告诉访问的是哪个模块下的API
            msg.getContent().put("actionName", actionName);//主要用于告诉访问的是哪个模块下的API
            msg.getContent().put("methodName", methodName);//主要用于告诉访问的是哪个模块下的API
            msg.getContent().put("actionType", actionType);//主要用于告诉访问的是哪个模块下的API

            msg.setTokenStr(message.getTokenStr());
            apiDef = ServiceAccess.callService(msg).getContent();
            apiMap.put(api, apiDef);

        }
        if (apiDef != null) {
            // 判定该API是否被禁用
            if (!"getMData".equals(methodName)) {
                if (StringUtils.mapValueIsEmpty(apiDef, "canUse") || (!(Boolean) apiDef.get("canUse"))) {
                    System.err.println("API info: " + JSONObject.fromObject(apiDef.toString()));
                    throw new UserOperateException(400, "API is disabled!");
                }
            }
            //部分API不需要登录和权限检查,比如:登录和注册等API
            if (!StringUtils.mapValueIsEmpty(apiDef, "notNeedLogin") && (Boolean) apiDef.get("notNeedLogin")) {// 无需登录几个访问
                return;
            }
        }

        String token = message.getTokenStr();
        if (StringUtils.isEmpty(token)) {
            throw new UserOperateException(401, "你没有登录，或登录已过期！");
        }
        if (CacheServiceFactory.getInc().getCache(token) == null) {
            SessionServiceImpl.checkToken(token);
            CacheServiceFactory.getInc().putCache(token, "1", 3600 * 3);//表示这个token在3个小时内是验证过的,不用重复验证
        }
        //将当前用户等各项信息加入到ControllerContext
        ControllerContext.getContext().setToken(token);


        //如果为超级管理员,不进行后面的权限校验
        if (ControllerContext.isAdminUser()) {
            return;
        }

        String currentUserId = ControllerContext.getContext().getCurrentUserId();
        if (StringUtils.isEmpty(currentUserId)) {// 未登录
            throw new UserOperateException(401, "你没有登录，或登录已过期！");
        }

        //非管理员, 且没有定义API
        if (apiDef == null) {
            throw new UserOperateException(400, "API is not def");
        }


        //如果该API需要对商户下的用户对店铺数据的校验, 用于禁止不拥商店的用户访问
//        if ("user".equals(ControllerContext.getContext().getCurrentUserType())) {

            //商户下的用户 从cache中拿到用户的权限资源,记得在修改权限时删除
//            String reListStr = CacheServiceFactory.getInc().getCache(CacheServiceFactory.cache_prefix_userResources + currentUserId);
//            JSONObject userResourceInfo;
//            if (reListStr == null) {
//                // 根据用户的角色通过服务获取权限
//                Message msg = Message.newReqMessage("1:GET@/account/User/getUserResources");
//                JSONObject con = new JSONObject();
//                con.put("userId", currentUserId);
//                msg.setContent(con);
//                userResourceInfo = ServiceAccess.callService(msg).getContent();
//                CacheServiceFactory.getInc().putCache(CacheServiceFactory.cache_prefix_userResources + currentUserId, userResourceInfo.toString(), 3600 * 3);//3小时
//            } else {
//                userResourceInfo = JSONObject.fromObject(reListStr);
//            }


//            Object checkStoreIdObj = apiDef.get("checkStoreId");
//            if (!StringUtils.mapValueIsEmpty(apiDef, "checkStoreId") && (Boolean) checkStoreIdObj) {
//                if (StringUtils.mapValueIsEmpty(message.getContent(), "storeId")) {
//                    throw new UserOperateException(400, "该API必须设定storeId参数!");
//                }
//                String storeId = message.getContent().getString("storeId");
//
//                JSONArray storeList = userResourceInfo.getJSONArray("storeList");
//                boolean storeIdOk = false;
//                for (Object o : storeList) {
//                    JSONObject jo = (JSONObject) o;
//                    if (jo.get("_id").equals(storeId)) {
//                        storeIdOk = true;
//                        break;
//                    }
//                }
//                if (!storeIdOk) {
//                    throw new UserOperateException(400, "不拥有访问或操作该店铺的权限!");
//                }
//            }
            //检查登录者是否拥有该API的权限
//            boolean checkOk = userResourceInfo.getJSONObject("resourceMap").containsKey(api);
//            if (!checkOk) {
//                throw new UserOperateException(403, "无执行该操作的权限，请联系管理员！path: " + api);
//            }
//        }

        //需要登录,但所有登陆者都能访问的权限,一般用于访问登陆者自己的操作,如修改自己的密码等
        if (!StringUtils.mapValueIsEmpty(apiDef, "loginAndNoCheck") && (Boolean) apiDef.get("loginAndNoCheck")) {
            return;
        }

    }

    /**
     * 用于判定api是否已经创建，如果没创建，就在数据库中创建
     */
    public static Map<String, Object> apiMap = new ConcurrentHashMap<>();

    public static void updateApiMap(String type, String key, Object value) {
        if ("save".equals(type)) {
            apiMap.put(key, value);
        } else if ("clear".equals(type)) {
            if (apiMap.containsKey(key)) {
                apiMap.remove(key);
            }
        }
    }

    public static String getAPIStr(int version, String actionName, String methodPath, String actionType) {
        return actionName + '+' + actionType + '+' + version + "+" + methodPath.replaceAll("\\/", "\\|").replaceAll("\\:", "\\$");
    }
}
