package com.zq.kyb.core.conn.websocket.servlet;

import com.zq.kyb.core.conn.websocket.ServiceClient;
import com.zq.kyb.core.conn.websocket.adapter.ServiceAdapter;
import com.zq.kyb.core.dao.CheckMData;
import com.zq.kyb.core.init.Constants;
import org.apache.log4j.Logger;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.ServletException;

/**
 * service服务器启动实现的逻辑
 */
public class ServiceWebsocketServlet extends WebSocketServlet {

    @Override
    public void init() throws ServletException {
        super.init();
        //启动与中心服务器的连接
        if (!"common".equals(Constants.moduleName)) {
            //启动与中心服务器的连接
            try {
                ServiceClient.client("common");//链接中央服务器
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //如果是中央服务器本身,则加载元数据
            try {
                CheckMData.putMDataAllToCacheBySql();
                CheckMData.checkMysqlTableList(CheckMData.getMDataByModule(Constants.moduleName));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void configure(WebSocketServletFactory webSocketServletFactory) {
        webSocketServletFactory.register(ServiceAdapter.class);
        webSocketServletFactory.getPolicy().setMaxBinaryMessageSize(1024 * 1024 * 10);//二进制消息最大10M
        webSocketServletFactory.getPolicy().setMaxTextMessageSize(1024 * 1024 * 6);//二进制消息最大6M
    }

    @Override
    public void destroy() {
        super.destroy();
        Logger.getLogger(this.getClass()).info("--stop Webscoket ");
    }
}
