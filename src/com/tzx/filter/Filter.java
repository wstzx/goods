package com.tzx.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "Filter", urlPatterns = {"/jsps/order/*", "/jsps/cart/*", "/com/tzx/cart/*", "/com/tzx/order/*"})
public class Filter implements javax.servlet.Filter {
    public void destroy() {

    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        //判断session中的user是否为null
        HttpServletRequest request = (HttpServletRequest) req;
        Object user = request.getSession().getAttribute("sessionUser");
        if (user == null) {
            req.setAttribute("code", "error");
            req.setAttribute("msg", "您还未登录，请先登录再试！");
            req.getRequestDispatcher("/jsps/msg.jsp").forward(req, resp);
        } else {
            chain.doFilter(req, resp);      //放行
        }

    }

    public void init(FilterConfig config) throws ServletException {

    }

}
