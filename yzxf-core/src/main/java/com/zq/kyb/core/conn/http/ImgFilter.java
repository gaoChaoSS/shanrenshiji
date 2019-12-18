package com.zq.kyb.core.conn.http;


import com.zq.kyb.core.conn.websocket.ServiceClient;
import com.zq.kyb.core.conn.websocket.servlet.PortalWebsocketServlet;
import com.zq.kyb.core.ctrl.ControllerContext;
import com.zq.kyb.core.dao.mysql.MysqlDaoImpl;
import com.zq.kyb.core.model.Message;
import com.zq.kyb.core.service.ServiceAccess;
import com.zq.kyb.util.StringUtils;
import net.sf.json.JSONObject;
import org.eclipse.jetty.websocket.api.Session;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.Future;

public class ImgFilter extends FileFilter {

    private static final String ACTION_GET = "get";
    private static final String ACTION_PUT = "put";

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain f) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpRequest.setCharacterEncoding("utf-8");
        httpResponse.setCharacterEncoding("utf-8");

        JSONObject content = new JSONObject();
        String fileId = httpRequest.getParameter("_id");
        String wh = httpRequest.getParameter("wh");
        String type = httpRequest.getParameter("type");
        String action = httpRequest.getParameter("action");
        if (StringUtils.isEmpty(type)) {
            type = "img";
        }
        if (StringUtils.isEmpty(action)) {
            action = ACTION_GET;
        }
        content.put("_id", fileId);
        content.put("wh", wh);
        String actionPath = null;
        if (ACTION_GET.equals(action)) {
            actionPath = "/file/FileItem/showImg";
        } else if (ACTION_PUT.equals(action)) {
            actionPath = "/file/FileItem/showImg";
        }
        Message req = Message.newReqMessage(actionPath);
        req.set_id(UUID.randomUUID().toString());
        req.setContent(content);
        ControllerContext.getContext().setReq(req);
        ControllerContext.getContext().setRespOut(httpResponse.getOutputStream());


        try {
            Calendar cd = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
            cd.setTimeInMillis(System.currentTimeMillis());
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            //客户端是否有缓存:
            String ifLast = httpRequest.getHeader("If-None-Match");
            boolean useCache = false;
            if (StringUtils.isNotEmpty(ifLast)) {
                useCache = true;
            }

            //设置缓存2个月
            String timeStr = sdf.format(cd.getTime());
            cd.add(Calendar.MONTH, 2);
            String expires = sdf.format(cd.getTime());
            httpResponse.setHeader("Date", timeStr);
            httpResponse.setHeader("Expires", expires);
            httpResponse.setHeader("ETag", fileId + "_" + wh);

            if (useCache) {
                httpResponse.setHeader("If-None-Match", fileId);
                httpResponse.setStatus(304);
            } else {
                //可以考虑缓存

                Message msg = Message.newReqMessage("1:GET@/file/FileItem/showImg");
                msg.getContent().put("_id", fileId);
                msg.getContent().put("wh", wh);
                Message en = ServiceAccess.callService(msg);
                httpResponse.getOutputStream().write(en.getContentByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
            //TODO 出错后使用一个默认图片:
        } finally {
            httpResponse.getOutputStream().close();
            MysqlDaoImpl.clearContext();
        }
        httpResponse.setContentType("image/*");
        //httpResponse.getWriter().flush();
        ControllerContext.clearContext();
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }

}
