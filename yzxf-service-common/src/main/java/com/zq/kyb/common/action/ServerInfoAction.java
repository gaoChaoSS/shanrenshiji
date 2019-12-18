package com.zq.kyb.common.action;

import com.zq.kyb.core.annotation.Member;
import com.zq.kyb.core.annotation.Seller;
import com.zq.kyb.core.conn.websocket.adapter.ServiceAdapter;
import com.zq.kyb.core.ctrl.BaseActionImpl;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import net.sf.json.JSONObject;
import org.eclipse.jetty.websocket.api.Session;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 基础服务,必须所有用户都能访问,所以都要添加: @Seller和@Member
 */
public class ServerInfoAction extends BaseActionImpl {


    @GET
    @Seller
    @Member
    @Path("/reg")
    public void reg() throws Exception {
        //通过主机,端口查询,相同就修改,没有就添加
        JSONObject content = ControllerContext.getContext().getReq().getContent();
        Map<String, Object> p = new HashMap<>();
        p.put("port", ControllerContext.getPString("port"));
        p.put("host", ControllerContext.getPString("host"));
        Map<String, Object> s = MysqlDaoImpl.getInstance().findOne2Map("ServerInfo", p, null, null);
        if (s == null) {
            s = content;
        } else {
            if (content.containsKey("_id")) {
                content.remove("_id");
            }
            s.putAll(content);
        }
        if (!s.containsKey("_id")) {
            s.put("_id", UUID.randomUUID().toString());
            s.put("createTime", System.currentTimeMillis());
        }
        s.put("updateTime", System.currentTimeMillis());
        MysqlDaoImpl.getInstance().saveOrUpdate("ServerInfo", s);

        toResult(200, "{}");
    }

    @GET
    @Seller
    @Member
    @Path("/serverList")
    public void serverList() throws IOException {
      // toResult(200, ServiceAdapter.hostInfoMap);
    }

    @GET
    @Seller
    @Member
    @Path("/serverAllList")
    public void serverAllList() throws Exception {
        List<Map<String, Object>> li = MysqlDaoImpl.getInstance().findAll2Map("ServerInfo", null, null, null, null);
        for (Map<String, Object> map : li) {
            boolean online = false;
            String key = map.get("type") + ":" + map.get("name");
//            if (ServiceAdapter.hostInfoMap.containsKey(key)) {
//                List<Map<String, Object>> sli = ServiceAdapter.hostInfoMap.get(key);
//                if (sli != null && sli.size() > 0) {
//                    for (Map<String, Object> hostMap : sli) {
//                        if (map.get("host").equals(hostMap.get("host")) && map.get("port").equals("" + hostMap.get("port"))) {
//                            online = true;
//                            break;
//                        }
//                    }
//                }
//            }
            map.put("online", online);
        }
        toResult(200, li);
    }


    /**
     * 查询服务器信息
     *
     * @throws IOException
     */
    @GET
    @Path("/serverInfo")
    public void serverInfo() throws Exception {
        String _id = ControllerContext.getPString("_id");
        Map<String, Object> map = MysqlDaoImpl.getInstance().findById2Map("ServerInfo", _id, null, null);
        //String sessionKey = map.get("type") + ":" + map.get("name") + ":" + map.get("host") + ":" + map.get("port");
        String key = map.get("type") + ":" + map.get("name");
//        List<Map<String, Object>> sli = ServiceAdapter.hostInfoMap.get(key);
//        map.put("linkMe", sli);//
        toResult(200, map);
    }

}
