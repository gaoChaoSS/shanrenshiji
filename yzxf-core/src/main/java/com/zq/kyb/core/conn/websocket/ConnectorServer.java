package com.zq.kyb.core.conn.websocket;

import com.zq.kyb.core.conn.websocket.servlet.PortalWebsocketServlet;

import java.io.IOException;

/**
 * 客户端websocket连接服务器, 暂时未用到.
 */
public class ConnectorServer extends BaseWebsocketServer {
    public static void main(String[] args) throws IOException {
        //new ConnectorServer().bindFront(new PortalWebsocketServlet());
    }
}
