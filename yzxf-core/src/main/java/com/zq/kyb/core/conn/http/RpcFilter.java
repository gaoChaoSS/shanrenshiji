package com.zq.kyb.core.conn.http;

import com.zq.kyb.core.conn.websocket.ServiceClient;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.model.ProtoMessage;
import com.zq.kyb.core.service.ServiceAccess;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by hujoey on 17/2/6.
 */
public class RpcFilter implements Filter {
    public static ConcurrentHashMap<String, ConcurrentHashMap<String, ConcurrentHashMap<String, Object>>> hostInfoMap = new ConcurrentHashMap<>();

    static {
        ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> m = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Object> v = new ConcurrentHashMap<>();
        v.put("type", "admin");
        v.put("name", "common");
        if ("common".equals(Constants.moduleName)) {
            v.put("host", Constants.moduleHost);
            v.put("port", Constants.modulePort);
        } else {
            v.put("host", Constants.adminHost);
            v.put("port", Constants.adminPort);
        }
        m.put(v.get("host") + ":" + v.get("port"), v);
        hostInfoMap.put("common", m);
    }

    public static void addServerToCache(String moduleName, String hostName, ConcurrentHashMap<String, Object> value) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> serverList = hostInfoMap.get(moduleName);
        if (serverList == null) {
            serverList = new ConcurrentHashMap<>();
        }
        serverList.put(hostName, value);
        hostInfoMap.put(moduleName, serverList);
    }

    public static void delServerToCache(String moduleName, String hostInfoKey) {
        ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> serverList = hostInfoMap.get(moduleName);
        if (serverList == null) {
            return;
        }
        serverList.remove(hostInfoKey);
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        if (!"common".equals(Constants.moduleName)) {//非中央服务器的服务模块,主动通过websocket连接中央服务器
            try {
                ServiceClient.client("common");//链接中央服务器
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpRequest.setCharacterEncoding("utf-8");
        httpResponse.setCharacterEncoding("utf-8");
        Message msg = null, re = null;
        try {
            //服务器的注册和状态查询走额外的逻辑:
            ProtoMessage.message req = ProtoMessage.message.parseDelimitedFrom(httpRequest.getInputStream());
            msg = Message.newReqMessage(req);
            re = ServiceAccess.callService(msg);
        } catch (Exception e) {
            e.printStackTrace();
            int code = 500;
            if (e instanceof UserOperateException) {
                code = ((UserOperateException) e).getErrCode();
            }
            if (re == null && msg != null) {
                re = Message.copy(msg);
            }
            re.setCode(code);
            re.getContent().put("errMsg", e.getMessage());
        } finally {
            if (re != null) {
                ProtoMessage.message resp = Message.genProtoMessage(re);
                //System.out.println("-----re:" + Base64.encode(resp.toByteArray()));
                resp.writeDelimitedTo(httpResponse.getOutputStream());
            }
            httpRequest.getInputStream().close();
            httpResponse.getOutputStream().flush();
            httpResponse.getOutputStream().close();
        }
    }

    @Override
    public void destroy() {

    }


}
