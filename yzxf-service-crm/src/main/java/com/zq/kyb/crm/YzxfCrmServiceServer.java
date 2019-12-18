package com.zq.kyb.crm;

import com.zq.kyb.core.conn.websocket.BaseWebsocketServer;
import com.zq.kyb.core.conn.websocket.servlet.ServiceWebsocketServlet;
import com.zq.kyb.core.init.Constants;

import java.io.IOException;

/**
 *
 */
public class YzxfCrmServiceServer extends BaseWebsocketServer {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new RuntimeException("java 启动参数未设置!");
        }
        Constants.dbConfig = args[0];
        new YzxfCrmServiceServer().bindBackend(new ServiceWebsocketServlet());
    }
}
