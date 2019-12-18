package com.zq.kyb.core.conn.websocket.adapter;

import com.zq.kyb.core.conn.http.RpcFilter;
import com.zq.kyb.core.conn.websocket.ServiceClient;
import com.zq.kyb.core.dao.CheckMData;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.sql.SQLException;
import java.util.concurrent.*;

/**
 * 服务器使用websocket客户端的方式连接其他service服务器的逻辑处理
 * <p/>
 * 支持自动重连功能
 */
public class ClientAdapter extends WebSocketAdapter {
    private final WebSocketClient client;
    private final String moduleName;//目标连接的模块名
    private int tryCount = 0;
    private URI uri;
    //模块与session对应的map,TODO 目前只支持1个模块1个实例
    public static ConcurrentHashMap<String, Session> moduleSessionMap = new ConcurrentHashMap<>();

    //描述链接目标模块的链接信息
    public static ConcurrentHashMap<String, String> linkToInfo = new ConcurrentHashMap<>();

    //通知服务器对应的连接,只是portal会有
    //public static ConcurrentHashMap<String, Session> notificationSessionMap = new ConcurrentHashMap<>();

    //用于记录消息及对应的回调,通过Message的_id来作为key
    // public static ConcurrentHashMap<String, CompletableFuture<Message>> reqMsgMap = new ConcurrentHashMap<>();
    private Session session;


    private URI getUri() throws Exception {
        //尝试2次后切换下一个服务器
        if (tryCount++ % 2 == 0) {
            uri = ServiceClient.getUri(moduleName);
        }
        return uri;
    }


    public ClientAdapter(WebSocketClient client, String moduleName, URI uri) {
        this.client = client;
        this.moduleName = moduleName;
        this.uri = uri;
    }

    private Thread heardThread = null;

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        session.setIdleTimeout(4000);
        this.session = session;

        //1.心跳线程,与客户端进行心跳处理保持连接不被断开
        startHearThread();

        //2.将Session与模块映射, 用于访问对应模块服务
        moduleSessionMap.put(moduleName, session);
        linkToInfo.put(moduleName, uri.getHost() + ":" + uri.getPort());

        //3.如果是链接的common模块,则进行注册和获取元数据的动作
        if ("common".equals(moduleName)) {
            if ("service".equals(Constants.moduleType) && !"common".equals(Constants.moduleName)) {
                updateMData();
            }
            regServer();
        }
        Logger.getLogger(getClass()).info("Socket Connected: " + session);
    }

    private void startHearThread() {
        if (heardThread == null) {
            heardThread = new Thread() {
                @Override
                public void run() {
                    Logger.getLogger(getClass()).info("heart session:" + ClientAdapter.this.session);
                    while (true) {
                        if (ClientAdapter.this.session != null && ClientAdapter.this.session.isOpen()) {
                            try {
                                Future<Void> f = ClientAdapter.this.session.getRemote().sendStringByFuture("");
                                f.get(15, TimeUnit.SECONDS);
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            } catch (TimeoutException e) {
                                e.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            heardThread.start();
        }
    }

    private void updateMData() {
        // 请求更新元数据
        // 注意: 在Adapter的事件中发起新请求,需要新开线程
        try {
            Message msg = Message.newReqMessage("1:GET@/common/MData/getMDataByModule");
            msg.getContent().put("moduleName", Constants.moduleName);
            JSONObject con = ServiceAccess.callService(msg).getContent();
            //将返回结果加载到Dao内存,已供使用
            JSONArray items = con.getJSONArray("items");
            MysqlDaoImpl.getInstance();
            CheckMData.checkMysqlTableList(items);
            CheckMData.putMDataAllToCacheByList(items, true);
            MysqlDaoImpl.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try {
                MysqlDaoImpl.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        } finally {
            MysqlDaoImpl.clearContext();
        }
    }

    private void regServer() {
        Message msg;
        JSONObject con;//元数据处理成功,注册 自己到中央服务器
        msg = Message.newReqMessage("1:PUT@/common/ServerInfo/reg");
        con = new JSONObject();
        con.put("host", Constants.moduleHost);
        con.put("port", Constants.modulePort);
        con.put("name", Constants.moduleName);
        con.put("type", Constants.moduleType);
        msg.setContent(con);
        try {
            session.getRemote().sendString(msg.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);
        Logger.getLogger(getClass()).info("<--- resp msg: " + message);
        doReq(message);
    }

    private void doReq(String message) {
        if (!"".equals(message)) {
            Message m = Message.jsonTo(JSONObject.fromObject(message));
            //RPC的情况,即:收到一次服务请求的响应结果
            if (Message.MsgType.TYPE_REQ == Message.MsgType.valueOf(m.getType())) {
                if (("1:PUT@/common/ServerInfo/reg").equals(m.getActionPath())) {//连接中央服务器返回
                    if (m.getCode() != 200) {
                        Logger.getLogger(this.getClass()).info("注册到中央服务器失败: " + m.toString());
                    } else {
                        Logger.getLogger(this.getClass()).info("注册到中央服务器成功!");
                    }
                }
            }
            //接收到服务器通知消息
            else if (Message.MsgType.TYPE_NOTIFI == Message.MsgType.valueOf(m.getType())) {
                //作为客户端,接收到中央服务器的通知消息
                if ("1:PUT@/common/ServerInfo/serverList".equals(m.getActionPath())) {//客户端接收到中央服务器的广播消息
                    putServerList(m.getContent());
                } else if ("1:PUT@/common/MData/getMDataByModule".equals(m.getActionPath())) {//客户端接收到中央服务器发过来的元数据变化
                    //涉及到访问远程,需要另起一个线程
                    try {
                        JSONObject map = m.getContent();
                        JSONArray items = map.getJSONArray("items");
                        if (!StringUtils.mapValueIsEmpty(map, "storeId")) {
                            String storeId = (String) map.get("storeId");
                            CheckMData.checkMysqlTableListStore(items, storeId);
                        } else if (!StringUtils.mapValueIsEmpty(map, "sellerId")) {
                            String sellerId = (String) map.get("sellerId");
                            CheckMData.checkMysqlTableListSeller(items, sellerId);
                        } else {
                            CheckMData.checkMysqlTableList(items);
                        }

                        CheckMData.putMDataAllToCacheByList(items, false);

                        MysqlDaoImpl.commit();
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            MysqlDaoImpl.rollback();
                        } catch (SQLException e1) {
                            e1.printStackTrace();
                        }
                    } finally {
                        MysqlDaoImpl.clearContext();
                    }

                }
                //其他通知消息的情况
                else if (
                        "1:POST@/notification/ImMsg/sendMsgToGroup".equals(m.getActionPath())
                                || "1:POST@/notification/ImMsg/sendMsg".equals(m.getActionPath())
                        ) {//客户端接收到通知消息,转发给终端客户
                    JSONObject json = m.getContent();
                    String connId = json.getString("connId");
                    Session userSession = ConnectorAdapter.sessionGroup.get(connId);
                    if (userSession != null && userSession.isOpen()) {
                        try {
                            userSession.getRemote().sendStringByFuture(message).get(15, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                throw new RuntimeException("未知的消息类型:" + m.getType());
            }
        }
    }

    /**
     * 将其他服务器模块的连接信息同步到本地内存
     *
     * @param json
     */
    public static void putServerList(JSONObject json) {
        RpcFilter.hostInfoMap.clear();
        for (Object objKey : json.keySet()) {
            String module = (String) objKey;
            JSONObject items = json.getJSONObject(module);
            //就做到了hostMap的同步
            for (Object hostKey : items.keySet()) {
                JSONObject value = items.getJSONObject((String) hostKey);
                ConcurrentHashMap<String, Object> v = new ConcurrentHashMap<>();
                for (Object o : value.keySet()) {
                    v.put((String) o, value.get(o));
                }
                RpcFilter.addServerToCache(module, (String) hostKey, v);
            }


            //如果当前进程是portal模块,同时检查如果有notification,就建立连接
//            if (objKey.equals("notification") && Constants.moduleType.equals("portal")) {
//                //主动通过调用req建立连接并注册.
//                for (Object item : items) {
//                    JSONObject j = (JSONObject) item;
//                    String host = j.getString("host");
//                    String port = j.getString("port");
//                    String serverKey = host + ":" + port;
//                    Session session = notificationSessionMap.get(serverKey);
//                    if (session == null || !session.isOpen()) {
//                        try {
//                            Future<Session> f = ServiceClient.client("notification", host, port);
//                            notificationSessionMap.put(serverKey, f.get(15, TimeUnit.SECONDS));
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//            }
        }
        Logger.getLogger(ClientAdapter.class).info("---- 各模块服务器连接信息更新到最新, " + json);
    }


    boolean connecting = true;

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        Logger.getLogger(getClass()).info("Socket Closed: [" + statusCode + "] " + reason);
        moduleSessionMap.remove(moduleName);
        linkToInfo.remove(moduleName);
        this.session = null;
        try {
            //自动重连
            client.connect(this, getUri());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace();
        super.onWebSocketError(cause);
        cause.printStackTrace();

        //自动重连
        if (cause instanceof java.net.ConnectException) {
            connecting = false;
            this.session = null;
            Logger.getLogger(getClass()).info("网络异常,连接失败");
            try {
                Thread.sleep(3000);
                if (this.session == null || !this.session.isOpen()) {
                    client.connect(this, getUri());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
