package com.zq.kyb.core.conn.websocket.adapter;

import com.zq.kyb.core.conn.websocket.servlet.PortalWebsocketServlet;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.util.ByteUtil;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.io.IOException;
import java.net.CookieManager;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * 最终客户端Websocket连接到Portal服务器的逻辑处理
 */
public class ConnectorAdapter extends WebSocketAdapter {


    private Session session;
    private String token;
    private String userId;
    private String userType;
    private String connId;

    private static HashMap<String, String> userTypeMap = new HashMap<>();

    static {
        userTypeMap.put("1:PUT@/account/AdminUser/auth", "admin");
        userTypeMap.put("1:PUT@/account/User/auth", "user");
        userTypeMap.put("1:PUT@/crm/Member/auth", "member");
    }

    public static ConcurrentHashMap<String, Session> sessionGroup = new ConcurrentHashMap<>();


    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        sess.setIdleTimeout(20000);
        Logger.getLogger(getClass()).info("新用户连接:" + sess);
        this.session = sess;
        this.connId = UUID.randomUUID().toString();
        sessionGroup.put(this.connId, session);

        try {
            sess.getRemote().sendStringByFuture("ok").get(15, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
        //10秒没有授权就断开连接
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (token == null && session != null) {
                    session.close();
                }
            }
        }.start();

    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        Logger.getLogger(getClass()).info("连接关闭:" + reason);
        sessionGroup.remove(this.connId);
        userOffline();
        session.close();
        session = null;
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace();
        Logger.getLogger(getClass()).info("连接错误!");
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {//2进制传送
        Logger.getLogger(getClass()).info("recv byte[]: " + ByteUtil.bytesToHexString(payload));
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);

        if (message != null) {
            if (message.length() == 0) {
                getRemote().sendStringByFuture("");
                return;
            }
            Logger.getLogger(getClass()).debug("接收客户端请求:" + message);
            new Thread() {
                @Override
                public void run() {
                    Message req = Message.jsonTo(JSONObject.fromObject(message));
                    Message resp = PortalWebsocketServlet.connectAccessService(req);

                    if (getSession().isOpen() && resp != null) {
                        try {
                            if ((resp.getActionPath().equals("1:PUT@/account/AdminUser/auth")
                                    || resp.getActionPath().equals("1:PUT@/account/User/auth")
                                    || resp.getActionPath().equals("1:PUT@/crm/Member/auth")
                            ) && resp.getCode() == 200) {
                                ConnectorAdapter.this.userType = userTypeMap.get(resp.getActionPath());
                                ConnectorAdapter.this.token = req.getContent().getString("token");
                                //验证成功,注册用户服务器关联关系到notification
                                ControllerContext.getContext().setToken(token);
                                ConnectorAdapter.this.userId = ControllerContext.getContext().getCurrentUserId();
                                userOnline();
                            }
                            Future<Void> f = getRemote().sendStringByFuture(resp.toString());
                            f.get(15, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }.start();
        }
    }

    /**
     * 上线
     */
    private void userOnline() {
        if (StringUtils.isEmpty(this.token)) {
            return;
        }
        Message req = Message.newReqMessage("1:PUT@/notification/UserLinkServer/save");
        req.setTokenStr(this.token);
        JSONObject json = new JSONObject();
        json.put("port", Constants.modulePort);
        json.put("host", Constants.moduleHost);
        json.put("modelName", Constants.moduleName);
        json.put("modelType", Constants.moduleType);
        json.put("userId", userId);
        json.put("userType", userType);
        json.put("_id", connId);

        req.setContent(json);
        Message resp = PortalWebsocketServlet.connectAccessService(req);
        Logger.getLogger(getClass()).info("用户上线请求结果: " + resp);
    }

    /**
     * 下线
     */
    private void userOffline() {
        if (StringUtils.isEmpty(this.token)) {
            return;
        }
        Message req = Message.newReqMessage("1:POST@/notification/UserLinkServer/del");
        req.setTokenStr(this.token);
        JSONObject json = new JSONObject();
        json.put("_id", connId);
        req.setContent(json);
        Message resp = PortalWebsocketServlet.connectAccessService(req);
        Logger.getLogger(getClass()).info("用户下线请求结果: " + resp);
    }


}
