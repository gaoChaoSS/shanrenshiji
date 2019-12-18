package com.zq.kyb.core.conn.websocket.servlet;

import com.zq.kyb.core.conn.websocket.ServiceClient;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.CacheServiceJRedisImpl;
import com.zq.kyb.core.dao.mysql.MysqlBaseDaoImpl;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.secu.CheckServicePermission;
import com.zq.kyb.core.conn.websocket.adapter.ConnectorAdapter;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.ServletException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 门户(admin,seller,member等)访问服务
 */
public class PortalWebsocketServlet extends WebSocketServlet {
    @Override
    public void init() throws ServletException {
        super.init();
        //启动与中心服务器的连接
        try {
            ServiceClient.client("common");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //中心服务器和服务节点,需要初始化数据库

    }

    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.register(ConnectorAdapter.class);
        webSocketServletFactory.getPolicy().setMaxBinaryMessageSize(1024 * 1024 * 10);//二进制消息最大10M
        webSocketServletFactory.getPolicy().setMaxTextMessageSize(1024 * 1024 * 6);//二进制消息最大6M
    }

    public static Message connectAccessService(Message req) {
        Message resp = null;
        try {
            //检查权限
            CheckServicePermission.check(req);
            if ("user".equals(ControllerContext.getContext().getCurrentUserType())) {
                String j = ControllerContext.getContext().getOtherDataJson();
                JSONObject obj = JSONObject.fromObject(j);
                String agentId = (String) obj.get("agentId");
                if (StringUtils.isNotEmpty(agentId)) {
                    CacheServiceJRedisImpl r = new CacheServiceJRedisImpl();
                    String l = r.getCache("agent_level_cache_" + agentId);
                    if (StringUtils.isEmpty(l)) {
                        Message msg = Message.newReqMessage("1:GET@/account/Agent/getAgentByCurrent");
                        JSONObject con = ServiceAccess.callService(msg).getContent();
                        r.putCache("agent_level_cache_" + agentId, con.get("level").toString(), 60 * 60 * 24 * 3);
                    }
                }
            }

            //ControllerContext.getContext().getReq().getContent().put("___isPortal", true);
            resp = ServiceAccess.callService(req);
            //resp = f.get(15, TimeUnit.SECONDS);//15秒后超时
        } catch (Exception e) {
            e.printStackTrace();
            if (req != null) {
                resp = Message.copy(req);
                JSONObject con = new JSONObject();
                if (e instanceof UserOperateException) {
                    resp.setCode(((UserOperateException) e).getErrCode());
                } else {
                    resp.setCode(500);
                }
                con.put("errMsg", e.getMessage());
                resp.setContent(con);
            }
        } finally {
            ControllerContext.clearContext();
            return resp;
        }
    }
}
