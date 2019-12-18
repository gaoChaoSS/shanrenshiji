package com.zq.kyb.portal.admin;

import com.zq.kyb.core.conn.websocket.BaseWebsocketServer;
import com.zq.kyb.core.conn.websocket.servlet.PortalWebsocketServlet;


public class JettyServer_admin extends BaseWebsocketServer {

    public static void main(String[] args) throws Exception {
        new JettyServer_admin().bindFront(new PortalWebsocketServlet());
    }
}
