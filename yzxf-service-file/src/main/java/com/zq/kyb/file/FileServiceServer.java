package com.zq.kyb.file;

import com.zq.kyb.core.conn.websocket.BaseWebsocketServer;
import com.zq.kyb.core.conn.websocket.servlet.ServiceWebsocketServlet;
import com.zq.kyb.core.init.Constants;

import java.io.IOException;

/**
 * 文件中央服务器,负责管理各个存储节点状态,及文件的元数据等
 */
public class FileServiceServer extends BaseWebsocketServer {
    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            throw new RuntimeException("java 启动参数未设置!");
        }
        Constants.dbConfig = args[0];
        new FileServiceServer().bindBackend(new ServiceWebsocketServlet());
    }
}
