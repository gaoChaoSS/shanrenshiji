package com.zq.kyb.account.action;

import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceFactory;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.secu.SessionServiceImpl;
import com.zq.kyb.util.MessageDigestUtils;
import net.sf.json.JSONObject;

import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class AdminUserAction extends BaseActionImpl {

    @PUT
    @Path("/logout")
    public void logout() throws Exception {
        String token = ControllerContext.getContext().getToken();
        if (token != null) {
            CacheServiceFactory.getInc().removeCache(token);
        }
    }

    @POST
    @Path("/login")
    public void login() throws Exception {
        JSONObject req = ControllerContext.getContext().getReq().getContent();
        if (!req.containsKey("loginName")) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "loginName_is_null");
        }
        if (!req.containsKey("password")) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "password_is_null");
        }

        String password = req.getString("password");
        // device
        String deviceId = req.getString("deviceId");


        String loginName = req.getString("loginName");
        JSONObject queryUserParams = new JSONObject();
        queryUserParams.put("loginName", loginName);
        queryUserParams.put("password", MessageDigestUtils.digest(password));

        Map<String, Object> user = MysqlDaoImpl.getInstance().findOne2Map(entityName, queryUserParams, null, null);
        if (user == null) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "用户名密码错误");
        } else if (user != null && user.get("canUse") != null && !(Boolean) user.get("canUse")) {
            throw new UserOperateException(Response.Status.BAD_REQUEST.getStatusCode(), "用户已经被禁用");
        }

        Map<String, Object> session = new HashMap<String, Object>();
        String userId = (String) user.get("_id");
        // session
        session.put("creator", userId);
        session.put("loginName", user.get("loginName"));
        session.put("type", "admin");
        session.put("deviceId", deviceId);
        //session.put("expireTime", 1800 * 1000);//
        session.put("sellerId", "admin");

        SessionServiceImpl ss = new SessionServiceImpl();
        session = ss.startSession(session);
        String sessionFullStr = (String) session.get("sessionFullStr");
        ControllerContext.getContext().setToken(sessionFullStr);

        //ControllerContext.getContext().setAdminInfo((String) session.get("_id"), deviceId, userId);

        JSONObject rObj = new JSONObject();
        rObj.putAll(user);
        rObj.remove("password");
        rObj.put("token", sessionFullStr);

        toResult(200, rObj);
    }

    @PUT
    @Path("/auth")
    public void auth() throws Exception {
        JSONObject req = ControllerContext.getContext().getReq().getContent();
        String token = req.getString("token");
        SessionServiceImpl.checkToken(token);
        ControllerContext.getContext().setToken(token);
        String currentUserId = ControllerContext.getContext().getCurrentUserId();
        Map<String, Object> user = MysqlDaoImpl.getInstance().findById2Map(entityName, currentUserId, null, null);
        if (user == null) {
            throw new UserOperateException(400, "验证失败");
        }
        JSONObject rObj = new JSONObject();
        rObj.put("name", user.get("name"));
        rObj.put("token", token);
        toResult(200, rObj);
    }
}
