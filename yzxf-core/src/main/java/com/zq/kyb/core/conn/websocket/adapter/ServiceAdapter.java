package com.zq.kyb.core.conn.websocket.adapter;

import com.zq.kyb.core.conn.http.RpcFilter;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 各个service模块服务提供者提供服务的逻辑处理
 */
public class ServiceAdapter extends WebSocketAdapter {

    //建立sessionGroup,用于中央服务器(common)发送状态给各个服务器,包含了所有连接到中央服务器的客户端连接
    public static ConcurrentHashMap<String, Session> sessionGroup = new ConcurrentHashMap<>();

    Session session;

    private String sessionKey;
    private String hostInfoKey;
    private String moduleType;
    private String moduleName;

    /**
     * 与服务访问者(connector或service)建立长连接
     *
     * @param sess
     */
    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
        Logger.getLogger(getClass()).info("与新服务消费者(可能是Portal或Service服务器)连接成功:" + sess);
        this.session = sess;
        Logger.getLogger(this.getClass()).info("----:" + sess.getRemote().getInetSocketAddress());
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        super.onWebSocketError(cause);
        cause.printStackTrace();
        Logger.getLogger(getClass()).info("连接错误!");
    }


    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        try {
            doService(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    /**
     * 与服务访问者断开
     *
     * @param statusCode
     * @param reason
     */
    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);

        //删除在sessionGroup中的映射
        serviceClose(reason);
    }

    private void serviceClose(String reason) {
        if (sessionKey != null) {
            if (sessionGroup.containsKey(sessionKey)) {
                sessionGroup.remove(sessionKey, session);
            }
            //删除hostInfo的映射
            RpcFilter.delServerToCache(moduleName, hostInfoKey);
        }

        this.session.close();
        this.session = null;
        Logger.getLogger(getClass()).info("与客户端[" + hostInfoKey + "]连接关闭:" + reason);

        //通知其他服务器某个server下线了
        notifiServerList();
    }


    private void doService(String message) throws InterruptedException, ExecutionException, TimeoutException {
        if (!"".equals(message)) {
            Logger.getLogger(getClass()).info("接收请求:" + message);
            Message req = Message.jsonTo(JSONObject.fromObject(message));
            //Service服务器接受服务器的注册,包括中央服务器
            String regPath = "1:PUT@/common/ServerInfo/reg";
            if (regPath.equals(req.getActionPath())) {
                JSONObject con = req.getContent();
                String host = con.getString("host");
                int port = con.getInt("port");
                moduleType = con.getString("type");
                moduleName = con.getString("name");

                //添加session group
                sessionKey = moduleType + ":" + moduleName + ":" + host + ":" + port;
                hostInfoKey = host + ":" + port;
                //List<Session> li = sessionGroup.get(sessionKey);
                //li = li == null ? new ArrayList<>() : li;
                //li.add(session);
                sessionGroup.put(sessionKey, session);

                Logger.getLogger(getClass()).info("--模块:[" + Constants.moduleName + "]与客户端[" + sessionKey + "] 成功注册!");

                //添加主机信息
                ConcurrentHashMap<String, Object> hostInfo = new ConcurrentHashMap<>();
                hostInfo.put("host", host);
                hostInfo.put("port", port);
                hostInfo.put("type", moduleType);
                hostInfo.put("name", moduleName);
                RpcFilter.addServerToCache(moduleName, hostInfoKey, hostInfo);

                Message re = Message.copy(req);
                re.setCode(200);
                re.getContent().clear();
                session.getRemote().sendStringByFuture(re.toString()).get(15, TimeUnit.SECONDS);

                //中央服务器需要广播消息到所有连接的客户端
                if ("common".equals(Constants.moduleName)) {
                    //新开线程是为了不阻塞本线程的运行
                    notifiServerList();
                }
            }
        }
    }


    public static void notifiServerList() {
        //发送一个NOTIFI消息给中央服务器(common),用于通知所有服务器
        Message notifiHost = Message.newReqMessage("1:PUT@/common/ServerInfo/serverList");
        notifiHost.setContent(JSONObject.fromObject(RpcFilter.hostInfoMap));
        notifiHost.setType(Message.MsgType.TYPE_NOTIFI.toString());
        notifi(notifiHost.toString());

    }


    public static void sendMDataChanged(String moduleName, List tableDefList, String type, String ownerId) {
        Message m = Message.newReqMessage("1:PUT@/common/MData/getMDataByModule");
        if (type != null && ownerId != null) {
            m.getContent().put(type, ownerId);
        }
        m.getContent().put("items", tableDefList);
        m.setType(Message.MsgType.TYPE_NOTIFI.toString());
        boolean sendOk = false;
        for (String key : sessionGroup.keySet()) {
            if (!key.contains(":" + moduleName + ":")) {
                continue;
            }
            Session session = sessionGroup.get(key);
            if (session.isOpen()) {
                try {
                    session.getRemote().sendStringByFuture(m.toString()).get(15, TimeUnit.SECONDS);
                    sendOk = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!sendOk) {
            //   throw new UserOperateException(400, "无可用模块地址[" + moduleName + "]!");
        }
    }

    /**
     * 通知所有在线的客户端
     *
     * @param msg
     */
    private static void notifi(String msg) {
        for (String s : sessionGroup.keySet()) {
            Session session = sessionGroup.get(s);
            if (session.isOpen()) {
                try {
                    session.getRemote().sendStringByFuture(msg).get(15, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
