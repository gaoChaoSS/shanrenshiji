package com.zq.kyb.payment;

import com.zq.kyb.core.conn.websocket.BaseWebsocketServer;
import com.zq.kyb.core.conn.websocket.servlet.ServiceWebsocketServlet;
import com.zq.kyb.core.init.Constants;

import java.io.IOException;

/**
 *
 */
public class PaymentServiceServer extends BaseWebsocketServer {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new RuntimeException("java 启动参数未设置!");
        }
        Constants.dbConfig = args[0];
        new PaymentServiceServer().bindBackend(new ServiceWebsocketServlet());
    }
}
