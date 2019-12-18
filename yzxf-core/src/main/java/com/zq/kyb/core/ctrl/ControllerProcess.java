package com.zq.kyb.core.ctrl;

import com.zq.kyb.core.annotation.Lock;
import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.util.StringUtils;
import com.zq.kyb.core.model.Message;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ControllerProcess {


    public static Map<String, Class<?>> actionMap = new ConcurrentHashMap<>();
    public static boolean isNeedMysql = "service".equals(Constants.moduleType);


    public void exeAction() {

        try {
            //storeId = (String) req.getContent().get("storeId");
            doAction();
            if (isNeedMysql) {
                MysqlDaoImpl.commit();
            }
        } catch (UserOperateException e) {
            e.printStackTrace();
            JSONObject re = new JSONObject();
            re.put("errMsg", e.getMessage());
            ControllerContext.setResult(e.errCode, re);
        } catch (Exception e) {
            try {
                if (isNeedMysql) {
                    MysqlDaoImpl.rollback();
                }
            } catch (SQLException e1) {
                e1.printStackTrace();
                errorToLog4j(e1, ControllerContext.getContext().getReq());
            }
            e.printStackTrace();
            errorToLog4j(e, ControllerContext.getContext().getReq());

            JSONObject re = new JSONObject();
            re.put("errMsg", "服务器忙");
            int code = 500;
            if (e instanceof InvocationTargetException) {
                InvocationTargetException ie = (InvocationTargetException) e;
                if (ie.getTargetException() != null && ie.getTargetException() instanceof UserOperateException) {
                    UserOperateException ue = (UserOperateException) ie.getTargetException();
                    re.put("errMsg", ue.getMessage());
                    code = ue.getErrCode();
                }
            }

            ControllerContext.setResult(code, re);
        } finally {
            if (isNeedMysql) {
                MysqlDaoImpl.clearContext();
            }
        }
    }

    public static void errorToLog4j(Exception e, Message req) {
        String fullStackTrace = org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace(e);
        Logger.getLogger(ERROR_LOG_TAG).info("-error:");
        Logger.getLogger(ERROR_LOG_TAG).info("req: " + JSONObject.fromObject(req));
        Logger.getLogger(ERROR_LOG_TAG).info("fullStackTrace: \n" + fullStackTrace);
        Logger.getLogger(ERROR_LOG_TAG).info("\n\n");
    }

    public static String ERROR_LOG_TAG = "error_file";
    public static String API_LOG_TAG = "access_api";

    private void doAction() throws Exception {
        Message req = ControllerContext.getContext().getReq();
        String pathStr = req.getActionPath();
        Logger.getLogger(ControllerProcess.class).info("\n");
        Logger.getLogger(ControllerProcess.class).info("-Req path:" + pathStr);
        Logger.getLogger(ControllerProcess.class).info("-Req body:" + JSONObject.fromObject(req).toString());
        JSONObject reqLog = JSONObject.fromObject(req);
        String tokenStr = reqLog.getString("tokenStr");
        if (StringUtils.isNotEmpty(tokenStr) && tokenStr.indexOf(" ") > -1) {
            tokenStr = tokenStr.split(" ")[0];
            reqLog.put("token", tokenStr);
        }
        reqLog.remove("tokenStr");
        Logger.getLogger(API_LOG_TAG).info("-req:" + reqLog.toString());

        int version;
        String modelName = null, actionName = null, methodName = null;
        List<String> pathArray = new ArrayList<>();

        String[] splits = pathStr.split(":");
        version = Integer.valueOf(splits[0]);
        String[] split = splits[1].split("@");
        String actionType = split[0].toUpperCase();
        String actionPath = split[1];
        String[] actionsStr = actionPath.split("/");
        int i = 0;
        for (String string : actionsStr) {
            if (StringUtils.isNotEmpty(string)) {
                i++;
                if (i == 1) {
                    modelName = string;
                } else if (i == 2) {
                    actionName = string;
                } else if (i == 3) {
                    methodName = string;
                } else if (i > 3) {
                    pathArray.add(string);
                }
            }
        }


        // 根据Path，actionName，commonName，actionType,apiVersion来判定api是否在定义范围内，是否禁用，是否有权限等

        //checkResourceService.checkAPI(modelName, actionName, methodPath, actionType, version, req.getTokenStr());

        //if ("baseSql".equals(apiDef.get("type"))) {
        //基于简单sql的API
        //}

        //如果不是当前模块,就不能提供服务
        if (!modelName.equals(Constants.moduleName)) {
            throw new UserOperateException(400, "模块不存在:" + modelName);
        }

        String baseActionKey = modelName + "/" + actionName;
        String actionKey = baseActionKey + ":" + version;
        // System.out.println("exe:" + actionKey);
        Class<?> actionClass;
        if (!actionMap.containsKey(actionKey)) {//如果该类没有加入,就添加到actionMap
            addActionClassToMap(modelName, actionName, baseActionKey);
        }
        actionClass = actionMap.get(actionKey);
        if (actionClass == null) {
            throw new RuntimeException("class not found, pls check! " + actionKey);
        }
        Method m = actionClass.getMethod(methodName);
        Logger.getLogger(API_LOG_TAG).info(" Exe Class:" + actionClass.getSimpleName() + "[" + actionType + "@" + m.getName() + "]");

        BaseAction newInstance = (BaseAction) actionClass.newInstance();
        newInstance.setModelName(modelName);
        newInstance.setEntityName(actionName);

        // 必须注入dao，只能使用注入的dao
        if (isNeedMysql) {
            Dao dao = MysqlDaoImpl.getInstance();
            Map entityMap = (Map) Dao.entityMap.get(actionName);
            if (entityMap != null) {
                dao = MysqlDaoImpl.getInstance();
            }
            newInstance.setDao(dao);
        }
        //检查用户权限
        String userType = ControllerContext.getContext().getCurrentUserType();
        Boolean ___isPortal = ControllerContext.getPBoolean("___isPortal");
        if (___isPortal != null && ___isPortal) {
            ControllerContext.getContext().getReq().getContent().remove("___isPortal");
//            if ("user".equals(userType)) {
//                Seller annotation = m.getAnnotation(Seller.class);
//                if (annotation == null) {
//                    throw new UserOperateException(400, "未设定访问权限!");
//                }
//                //仅仅Seller的超级管理员能访问
//                if (annotation.isAdmin() && !ControllerContext.getContext().isSellerAdmin()) {
//                    throw new UserOperateException(403, "仅商户管理员能访问!");
//                }
//
//            } else if ("member".equals(userType)) {
//                Member annotation = m.getAnnotation(Member.class);
//                if (annotation == null) {
//                    throw new UserOperateException(400, "未设定访问权限!");
//                }
//            }
        }

        Annotation annotation = m.getAnnotation(Lock.class);
        String keyValue = null;
        if (annotation != null) {
            Lock l = (Lock) annotation;
            keyValue = actionName;//实体名
            String keyStr = l.key();
            if (StringUtils.isNotEmpty(keyStr)) {
                JSONObject r = ControllerContext.getContext().getReq().getContent();
                String[] keys;
                if (keyStr.contains(",")) {
                    keys = keyStr.split(",");
                } else {
                    keys = new String[]{keyStr};
                }
                for (String key : keys) {
                    String value = (String) r.get(key);
                    if (StringUtils.isEmpty(value)) {
                        throw new RuntimeException("锁的关键字段:" + key + ", 在参数中不存在!");//一般为主键,表示锁一条数据
                    }
                    keyValue += "_" + value;
                }
            } else {
                throw new RuntimeException("必须设置Lock元数据的key属性,如:@Lock(keyValue = \"_id\")");
            }
        }

        //实体名+主键, 即表名+主键或其他字段的锁方式
        if (StringUtils.isNotEmpty(keyValue)) {//需要进行方法+实体的锁操作
            JedisUtil.whileGetLock(keyValue, 25);
        }
        try {
            m.invoke(newInstance);
        } finally {
            if (StringUtils.isNotEmpty(keyValue)) {
                JedisUtil.del(keyValue);//释放锁
            }
            //要修复对应的API Path,因为有可能Action中调用Action的情况
            Message r = ControllerContext.getContext().getResp();
            if (r == null) {
                r = Message.copy(req);
                r.getContent().clear();
                ControllerContext.getContext().setResp(r);
            }
            if (!req.get_id().equals(r.get_id())) {
                r.set_id(req.get_id());
                r.setActionPath(req.getActionPath());
            }
        }
    }

    private synchronized void addActionClassToMap(String modelName, String actionName, String baseActionKey) throws Exception {
        // TODO Auto-generated method stub

        String pkg = Constants.basePackage + "." + modelName + ".action";
        String aPath = pkg + "." + actionName + "Action";
        Class<?> baseActionClass;
        try {
//            if ("MData".equals(actionName)) {//元数据的Action特殊匹配
//                baseActionClass = MDataAction.class;
//            } else
//            if ("DataVersion".equals(actionName)) {//版本控制的表
//                baseActionClass = DataVersionAction.class;
//            } else if ("Setting".equals(actionName)) {//系统设置的表
//                baseActionClass = SettingAction.class;
            if ("ServerBaseInfo".equals(actionName)) {//元数据的Action特殊匹配
                baseActionClass = ServerBaseInfoAction.class;
            } else {
                baseActionClass = Class.forName(aPath);
            }
        } catch (ClassNotFoundException e) {
            //e.printStackTrace();
            Logger.getLogger(ControllerProcess.class).info("not found action class:" + e.getMessage());
            baseActionClass = BaseActionImpl.class;
        }
        actionMap.put(baseActionKey + ":1", baseActionClass);// 版本1
        //putActionMethods(baseActionClass);
    }
}
