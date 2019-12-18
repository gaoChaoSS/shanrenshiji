package com.zq.kyb.order;

import com.zq.kyb.core.conn.websocket.BaseWebsocketServer;
import com.zq.kyb.core.conn.websocket.servlet.ServiceWebsocketServlet;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.order.task.TaskExecutor;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class YzxfOrderServiceServer extends BaseWebsocketServer {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            throw new RuntimeException("java 启动参数未设置!");
        }
        Constants.dbConfig = args[0];
        TaskExecutor.startTask();
        new YzxfOrderServiceServer().bindBackend(new ServiceWebsocketServlet());
    }
}
