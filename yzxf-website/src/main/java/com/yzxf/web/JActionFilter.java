package com.yzxf.web;

import com.zq.kyb.core.init.Constants;
import com.zq.kyb.util.EntityUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * Created by hujoey on 17/2/17.
 */
public class JActionFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        httpRequest.setCharacterEncoding("utf-8");
        httpResponse.setCharacterEncoding("utf-8");

        String oldpath = httpRequest.getServletPath();
        System.out.println(oldpath);
        String qian = "/action/";
        if (oldpath.startsWith(qian)) {

            String path = oldpath.substring(qian.length());
            String replaceAll = path.replaceAll("\\.html", "");
            String[] sp = replaceAll.split("/");
            String actionName = sp[0];
            String mStr = sp[1];
            try {
                String className = "com.yzxf.jaction." + EntityUtils.getEntityName(actionName) + "JAction";
                className = className.replaceAll("_", "");
                Class c = Class.forName(className);
                Object inc = c.newInstance();
                Method m = c.getMethod(mStr, HttpServletRequest.class, HttpServletResponse.class);
                m.invoke(inc, httpRequest, response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String pagePath = "/" + actionName.toLowerCase() + "/" + mStr + ".jsp";

            RequestDispatcher s = httpRequest.getRequestDispatcher(pagePath);
            s.forward(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
