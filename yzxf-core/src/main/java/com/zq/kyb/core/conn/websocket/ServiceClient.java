package com.zq.kyb.core.conn.websocket;

import com.zq.kyb.core.conn.websocket.adapter.ClientAdapter;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import net.sf.json.JSONObject;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * 通过Websocket通过发送消息的方式来处理远程服务数据的调用
 * TODO 组要做到自动切换ip,将不可用的服务器剔除
 */
public class ServiceClient {

    public static void main(String[] args) {
        //        URI uri = URI.create("ws://localhost:8080/events/");
        //        client(uri);
    }

    public static Future<Session> reConnect(WebSocketClient client, ClientAdapter a, URI uri) {
        try {
            return client.connect(a, uri);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //暂停3秒重新连接,
                Thread.sleep(3000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            return reConnect(client, a, uri);
        }
    }

    public static Future<Session> client(String moduleName) throws Exception {
        //不能自己连接自己
        if (moduleName.equals(Constants.moduleName)) {
            throw new RuntimeException("不能自己连接自己!");
        }
        URI uri = getUri(moduleName);
        WebSocketClient client = new WebSocketClient();
        client.getPolicy().setMaxBinaryMessageSize(1024 * 1024 * 10);//最大10M
        client.getPolicy().setMaxTextMessageSize(124 * 1024 * 6);//最大6M
        client.start();
        ClientAdapter socket = new ClientAdapter(client, moduleName, uri);
        return reConnect(client, socket, uri);
    }

    public static Future<Session> client(String moduleName, String host, String port) throws Exception {
        //不能自己连接自己
        if (moduleName.equals(Constants.moduleName)) {
            throw new RuntimeException("不能自己连接自己!");
        }
        URI uri = URI.create("ws://" + host + ":" + port + "/events");
        WebSocketClient client = new WebSocketClient();

        client.getPolicy().setMaxBinaryMessageSize(1024 * 1024 * 10);//最大10M
        client.getPolicy().setMaxTextMessageSize(124 * 1024 * 6);//最大6M
        client.start();
        ClientAdapter socket = new ClientAdapter(client, moduleName, uri);
        return reConnect(client, socket, uri);
    }

    /**
     * 从中央服务器获取的数据
     */
    public static ConcurrentHashMap<String, List<Map<String, Object>>> hostMap = new ConcurrentHashMap<>();

    public static URI getUri(String moduleName) throws Exception {
        //if (hostMap.size() == 0) {
        if (moduleName.equals("common")) {
            //TODO 需要实现HA
            return URI.create("ws://" + Constants.adminHost + ":" + Constants.adminPort + "/events");
        }

        List<Map<String, Object>> hostStrList = hostMap.get("service:" + moduleName);
        if (hostStrList == null || hostStrList.size() == 0) {
            //重新从中央服务器获取一次serverList
            Message m = Message.newReqMessage("1:PUT@/common/ServerInfo/serverList");
            JSONObject re = ServiceAccess.callService(m).getContent();
            ClientAdapter.putServerList(re);
            hostStrList = hostMap.get("service:" + moduleName);
        }
        if (hostStrList != null && hostStrList.size() > 0) {
            Map<String, Object> map = hostStrList.get(0);
            String host = (String) map.get("host");
            String port = "" + map.get("port");
            //然后将第一个放到最后
            if (hostStrList.size() > 0) {
                hostStrList.add(map);
                hostStrList.remove(0);
            }
            return URI.create("ws://" + host + ":" + port + "/events");
        }
        throw new RuntimeException("模块[" + moduleName + "], 无可以连接的地址!");
    }
}
