package com.zq.kyb.core.conn.http;

import com.zq.kyb.core.init.Constants;
import com.zq.kyb.util.EntityUtils;
import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class BaseJspFilter implements Filter {

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

        String oldpath = httpRequest.getServletPath();
        Logger.getLogger(this.getClass()).info(oldpath);

        String qian;
        //通过El表达式来展示数据
        if (oldpath.startsWith("/front_view")) {
            qian = "/front_view";
            String path = oldpath.substring(qian.length() + 1);
            String replaceAll = path.replaceAll("\\.jsp", "").replaceAll("/", "");
            try {
                String className = Constants.basePackage + "." + Constants.moduleName + ".jaction." + EntityUtils.getEntityName(replaceAll) + "JAction";
                className = className.replaceAll("_", "");
                Class c = Class.forName(className);
                Object inc = c.newInstance();
                Method m = c.getMethod("page", HttpServletRequest.class, HttpServletResponse.class);
                m.invoke(inc, httpRequest, response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            RequestDispatcher s = httpRequest.getRequestDispatcher(oldpath);
            s.forward(request, response);

        } else {
            qian = "/view";

            String path = oldpath.substring(qian.length() + 1);
            String p = httpRequest.getServletContext().getRealPath(oldpath);
            String replaceAll = path.replaceAll("\\.jsp", "").replaceAll("/", "_");
            String[] split = replaceAll.split("_");
            httpRequest.setAttribute("model", split[0]);
            httpRequest.setAttribute("entity", split[1]);

            if (p != null && new File(p).exists()) {
                RequestDispatcher s = httpRequest.getRequestDispatcher(oldpath);
                s.forward(request, response);
            } else {
                RequestDispatcher s = httpRequest.getRequestDispatcher(qian + "/common.jsp");
                s.forward(request, response);
            }
        }
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

}
