import com.zq.kyb.core.conn.websocket.BaseWebsocketServer;
import com.zq.kyb.core.conn.websocket.servlet.PortalWebsocketServlet;

public class YzxfJettyServer_seller extends BaseWebsocketServer {
    public static void main(String[] args) throws Exception {
        new YzxfJettyServer_seller().bindFront(new PortalWebsocketServlet());
    }
}
