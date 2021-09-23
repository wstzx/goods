package com.tzx.admin.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(filterName = "AdminFilter", urlPatterns = {"/adminjsps/admin/*", "/admin/*"})
public class AdminFilter implements Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {

        HttpServletRequest request = (HttpServletRequest) req;
        Object admin = request.getSession().getAttribute("admin");
        if (admin == null) {
            request.setAttribute("msg", "您还未登录,请登录后尝试!");
            request.getRequestDispatcher("/adminjsps/login.jsp").forward(req, resp);
        } else {
            chain.doFilter(req, resp);
        }


    }

    public void init(FilterConfig config) throws ServletException {

    }

}
