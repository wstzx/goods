package com.tzx.admin.admin.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import com.tzx.admin.admin.domain.Admin;
import com.tzx.admin.admin.service.AdminService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/adminServlet")
public class AdminServlet extends BaseServlet {

    private AdminService service = new AdminService();


    /**
     * 登录模块
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map<String, String[]> map = request.getParameterMap();
        Admin admin = CommonUtils.toBean(map, Admin.class);
        Admin login = service.login(admin);
        if (login == null) {
            request.setAttribute("msg", "登录名或密码错误,请重试!");
            return "f:/adminjsps/login.jsp";
        }
        request.getSession().setAttribute("admin", login);
        return "r:/adminjsps/admin/index.jsp";

    }


    /**
     * 退出登录
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String quit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.getSession().invalidate();
        return "f:/adminjsps/   login.jsp";

    }
}
