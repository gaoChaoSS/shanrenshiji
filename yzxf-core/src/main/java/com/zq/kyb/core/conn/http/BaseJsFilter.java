package com.zq.kyb.core.conn.http;


import com.zq.kyb.util.FileExecuteUtils;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

public class BaseJsFilter implements Filter {

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }

    private static java.util.concurrent.ConcurrentHashMap<Long, String> fileMap = new java.util.concurrent.ConcurrentHashMap<Long, String>();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain f) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpRequest.setCharacterEncoding("utf-8");
        httpResponse.setCharacterEncoding("utf-8");

        String oldpath = httpRequest.getServletPath();
        Logger.getLogger(this.getClass()).info(oldpath);

        String qian = null;
        if (oldpath.startsWith("/front_view_js")) {
            qian = "/front_view_js";
        } else {
            qian = "/view_js";
        }

        String path = oldpath.substring(qian.length() + 1);
        String p = httpRequest.getServletContext().getRealPath(oldpath);
        String replaceAll = path.replaceAll("\\.js", "").replaceAll("/", "_");
        String[] split = replaceAll.split("_");
        httpRequest.setAttribute("modelName", split[0]);
        httpRequest.setAttribute("entityName", split[1]);

        if (p != null && new File(p).exists()) {
            f.doFilter(request, response);
        } else {
            String s = httpRequest.getServletContext().getRealPath(qian + "/i_ctrl.js");
            File basejsFile = new File(s);
            if (basejsFile.exists() && basejsFile.isFile()) {
                long lastModified = basejsFile.lastModified();
                String con = "";
                if (!fileMap.containsKey(lastModified)) {
                    con = FileExecuteUtils.getInstance().readFile(s).toString();
                    fileMap.put(lastModified, con);
                }
                con = fileMap.get(lastModified);
                httpResponse.getWriter().println(con);
                httpResponse.getWriter().flush();
            }
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

}
