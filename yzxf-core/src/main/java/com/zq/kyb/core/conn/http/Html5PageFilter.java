package com.zq.kyb.core.conn.http;

import org.apache.log4j.Logger;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Html5PageFilter implements Filter {

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

        String[] v = oldpath.split("/");
        httpRequest.setAttribute("serverFront", v[1]);

        if (v.length > 3) {
            httpRequest.setAttribute("modelName", v[2]);
            httpRequest.setAttribute("entityName", v[3]);
//                SellerResourceAction action = new SellerResourceAction();
//                action.checkSellerResource("-1", v[2], -1);
//                action.checkSellerResource("-1:" + v[2], v[3], 0);
        }
        RequestDispatcher s = httpRequest.getRequestDispatcher("/" + v[1] + "_page/" + v[1] + ".jsp");
        s.forward(request, response);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // TODO Auto-generated method stub

    }

}
