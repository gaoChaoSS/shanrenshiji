package com.zq.kyb.filecloud.admin;

import com.zq.kyb.core.conn.websocket.BaseWebsocketServer;
import com.zq.kyb.core.conn.websocket.servlet.ServiceWebsocketServlet;

import java.io.IOException;

/**
 * 文件服务服务器(
 */
public class FileAdminServer extends BaseWebsocketServer {
    public static void main(String[] args) throws IOException {
        new FileAdminServer().bindBackend(new FileAdminWebsocketServlet());
    }
}
