package com.zq.kyb.core.conn.http;


import com.zq.kyb.core.conn.websocket.servlet.PortalWebsocketServlet;
import com.zq.kyb.core.exception.UserOperateException;
import com.zq.kyb.core.init.Constants;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.util.Base64;
import com.zq.kyb.util.FileExecuteUtils;
import com.zq.kyb.util.StringUtils;
import com.zq.kyb.util.json.DateJsonProcessor;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;

public class BaseFilter implements Filter {
    private static JsonConfig config;

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        Logger.getLogger(getClass()).info("-init BaseFilter");
        //初始化当前模块的元数据结构
        config = new JsonConfig();
        DateJsonProcessor.setJsonConfig(config);
    }


    @Override
    public void destroy() {
        Logger.getLogger(this.getClass()).info("--stop baseFilter:");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain arg2) throws IOException, ServletException {

        Logger.getLogger(this.getClass()).info("----- Start Http Request");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (!httpRequest.getRequestURI().endsWith("/AlipayQrCode/payNotifi")) {// 阿里不能使用utf8
            httpRequest.setCharacterEncoding("utf-8");
            httpResponse.setCharacterEncoding("utf-8");
        }
        String vStr = httpRequest.getHeader("apiVersion");

        String servletPath = httpRequest.getServletPath();
        Logger.getLogger(this.getClass()).info("-Http servletPath:" + servletPath);

        String actionPath = "";
        String userType = "";
        if (servletPath.indexOf("/s_admin/api") == 0) {//超级管理
            actionPath = servletPath.replaceAll("/s_admin/api", "");
            userType = "admin";
        } else if (servletPath.indexOf("/s_user/api") == 0) {//商户访问
            actionPath = servletPath.replaceAll("/s_user/api", "");
            userType = "user";
        } else if (servletPath.indexOf("/s_agent/api") == 0) {//商户访问
            actionPath = servletPath.replaceAll("/s_agent/api", "");
            userType = "agent";
        } else if (servletPath.indexOf("/s_member/api") == 0) {//终端客户访问
            actionPath = servletPath.replaceAll("/s_member/api", "");
            userType = "member";
        }

        String token = null;
        //如果有cookie,用cookie的覆盖
        Cookie[] ccs = httpRequest.getCookies();
        String storeId = null, sellerId = null;
        if (ccs != null && ccs.length > 0) {
            for (Cookie cookie : ccs) {
                if ("user".equals(userType) && "___USER_TOKEN".equals(cookie.getName())) {//商户token
                    token = java.net.URLDecoder.decode(cookie.getValue(), "utf-8");
                }
                if ("agent".equals(userType) && "___AGENT_TOKEN".equals(cookie.getName())) {//商户token
                    token = java.net.URLDecoder.decode(cookie.getValue(), "utf-8");
                }
                if ("admin".equals(userType) && "___ADMIN_TOKEN".equals(cookie.getName())) {//商户token
                    token = java.net.URLDecoder.decode(cookie.getValue(), "utf-8");
                }
                if ("member".equals(userType) && "___MEMBER_TOKEN".equals(cookie.getName())) {//终端客户token
                    token = java.net.URLDecoder.decode(cookie.getValue(), "utf-8");
                }
                if ("apiVersion".equals(cookie.getName())) {
                    vStr = cookie.getValue();
                }
                if ("user".equals(userType) && "_seller_storeId".equals(cookie.getName())) {//商户访问时需要知道某个店
                    storeId = cookie.getValue();
                }

                if ("storeId".equals(cookie.getName())) {
                    {//商户访问时需要知道某个店
                        storeId = cookie.getValue();
                    }
                }

                if ("admin".equals(userType) && "sellerId".equals(cookie.getName())) {
                    {//商户访问时需要知道某个商户
                        sellerId = cookie.getValue();
                    }
                }
            }
        }
        // }
        //首先判断是否是管理员登录


        if (StringUtils.isEmpty(vStr)) {// for test
            vStr = request.getParameter("apiVersion");
        }
        if (StringUtils.isEmpty(token)) {// for test
            token = request.getParameter("__tokenUser");
        }
        if (StringUtils.isEmpty(token)) {// for test
            token = request.getParameter("__tokenMember");
        }

        if (StringUtils.isEmpty(vStr)) {
            vStr = "1";
        }
        actionPath = vStr + ":" + httpRequest.getMethod().toUpperCase() + "@" + actionPath;

        Message req = Message.newReqMessage(actionPath);

        JSONObject json = req.getContent();
        json = json == null ? new JSONObject() : json;

        Enumeration<String> parameterNames = httpRequest.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String key = parameterNames.nextElement();
            if (key.equals("_")) {
                continue;
            }
            String value = httpRequest.getParameter(key);
            json.put(key, value);
            //处理storeId
            if ("storeId".equals(key) || "_storeId".equals(key)) {
                storeId = value;
            }
            Logger.getLogger(BaseFilter.class).info("-HttpRequest Parameter:" + key + "=" + value);
        }


        //某些特殊情况下,不能用json来处理body
        if (actionPath.endsWith("InputText")) {
            StringBuffer sb = FileExecuteUtils.getInstance().readInputStream(request.getInputStream(), "utf-8");
            Logger.getLogger(BaseFilter.class).info("-HttpRequest body[___inText]:" + sb);
            if (sb != null && sb.length() > 0) {
                json.put("___inText", sb.toString());
            }
        } else if (actionPath.endsWith("/FileItem/upload")) {//二进制上次
            req.setContentByteArray(FileExecuteUtils.getInstance().readInputStreamByByte(request.getInputStream()));
        } else if (actionPath.endsWith("/FileItem/uploadBase64")) {//base64上传
            StringBuffer sb = FileExecuteUtils.getInstance().readInputStream(request.getInputStream(), "utf-8");
            Logger.getLogger(BaseFilter.class).info("-HttpRequest body[upload_base64]:" + sb);
            String requestBodyStr = sb.toString();
            if (requestBodyStr.indexOf(",") > 1) {
                String[] split = requestBodyStr.split(",");
                String idName = split[0];
                String[] names = idName.split("/");
                String fileId = names[0];
                String name = URLDecoder.decode(names[1], "utf-8");
                json.put("name", name);
                json.put("fileId", fileId);
                json.put("projectName", Constants.moduleName);
                json.put("entityName", names[2]);
                json.put("entityField", names[3]);
                json.put("entityId", names[4]);

                String body = split[2];
                req.setContentByteArray(Base64.decode(body));
            }
        } else if (actionPath.endsWith("/FileItem/uploadForm")) {//表单上传
            //TODO 表单上传
            throw new UserOperateException(400, "暂未实现");
        } else {
            StringBuffer sb = FileExecuteUtils.getInstance().readInputStream(request.getInputStream(), "utf-8");
            Logger.getLogger(BaseFilter.class).info("-HttpRequest body:" + sb);
            if (sb != null && sb.length() > 0) {
                JSONObject map = JSONObject.fromObject(sb.toString(), config);
                json.putAll(map);
            }
        }

        if (!StringUtils.isEmpty(storeId)) {
            json.put("storeId", storeId);
        }
        if (!StringUtils.isEmpty(sellerId) && StringUtils.mapValueIsEmpty(json, "sellerId")) {
            json.put("sellerId", sellerId);
        }

        req.setContent(json);

        if (token != null) {
            req.setTokenStr(token);
        }
        try {
            Message resp = PortalWebsocketServlet.connectAccessService(req);
            if (resp != null) {
                if (resp.getCode() > 0) {
                    httpResponse.setStatus(resp.getCode());
                }
                //对于一些特殊的API需要返回其他文本格式的
                httpResponse.setContentType("application/json,text/plain;charset=utf-8");
                if (!StringUtils.mapValueIsEmpty(resp.getContent(), "___outText")) {
                    httpResponse.getWriter().print(resp.getContent().get("___outText"));
                } else {
                    httpResponse.getWriter().print(resp.toString());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Message err = Message.copy(req);
            err.setCode(500);
            err.getContent().put("errMsg", e.getMessage());
            httpResponse.setStatus(500);
            httpResponse.getWriter().print(err);
        } finally {
            httpResponse.getWriter().flush();
        }
    }

    public static void main(String[] args) throws ServletException, UnsupportedEncodingException, InterruptedException {

    }
}
