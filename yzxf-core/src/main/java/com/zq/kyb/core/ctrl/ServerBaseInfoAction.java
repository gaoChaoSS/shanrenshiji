package com.zq.kyb.core.ctrl;

import com.zq.kyb.core.conn.websocket.ServiceClient;
import com.zq.kyb.core.conn.websocket.adapter.ClientAdapter;
import com.zq.kyb.core.conn.websocket.adapter.ServiceAdapter;
import com.zq.kyb.core.dao.Dao;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.dao.redis.JedisUtil;
import com.zq.kyb.core.init.Constants;
import net.sf.json.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;

/**
 */
public class ServerBaseInfoAction extends BaseActionImpl {
    @GET
    @Path("/info")
    public void info() throws IOException {
        JSONObject re = new JSONObject();
        //基础信息
        re.put("system", System.getProperties());
        re.put("serverConf", Constants.confProerties);
        if ("service".equals(Constants.moduleType)) {
            JSONObject dbMap = new JSONObject();
            dbMap.put("jdbcUrl", MysqlDaoImpl.ds.getJdbcUrl());
            //dbMap.put("ConnectionCustomizerClassName", MysqlDaoImpl.ds.getConnectionCustomizerClassName());
            dbMap.put("driverClass", MysqlDaoImpl.ds.getDriverClass());
            re.put("db", dbMap);
        } else {
            //加载用户链接信息

        }
        re.put("entityTable", Dao.entityMap);
        re.put("entityTableFieldList", Dao.entityFieldsMap);
        //re.put("linkMe", ServiceAdapter.hostInfoMap);
        re.put("linkTo", ClientAdapter.linkToInfo);
        //re.put("ServiceClient", ServiceClient.hostMap);

        re.put("commonServer", Constants.adminHost + ":" + Constants.adminPort);
        re.put("redis", JedisUtil.redisHost + ":" + JedisUtil.redisPort);

        toResult(200, re);
    }

}
