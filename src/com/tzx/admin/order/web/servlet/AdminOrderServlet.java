package com.tzx.admin.order.web.servlet;

import cn.itcast.servlet.BaseServlet;
import com.tzx.order.domain.Order;
import com.tzx.order.service.OrderService;
import com.tzx.pager.PageBean;
import com.tzx.user.domain.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/admin/adminOrderServlet")
public class AdminOrderServlet extends BaseServlet {

    private OrderService service = new OrderService();


    /**
     * 截取url,页面中的分页导航需要使用他们作为超链接的目标
     *
     * @param req
     * @return
     */
    private String getUrl(HttpServletRequest req) {
        //          /goods/bookServlet        ?后面的参数
        String url = req.getRequestURI() + "?" + req.getQueryString();
        //如果url中存在pc参数,截取掉
        int index = url.lastIndexOf("&pc=");
        if (index != -1) {
            url = url.substring(0, index);
        }
        return url;

    }


    /**
     * 获取当前页的页码
     *
     * @param req
     * @return
     */
    private int getPc(HttpServletRequest req) {
        int pc = 1;
        String param = req.getParameter("pc");
        if (param != null && !param.trim().isEmpty()) {
            try {
                pc = Integer.parseInt(param);
            } catch (RuntimeException e) {
            }
        }
        return pc;
    }


    /**
     * 查询所有订单
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到pc:如果页面传,使用页面的;否则默认为1
        int pc = getPc(request);
        //2.得到url
        String url = getUrl(request);
        //3.获取查询条件
        //4.使用pc和cid调用service方法
        PageBean<Order> pageBean = service.findAll(pc);
        //5.给pagebean设置url
        pageBean.setUrl(url);
        request.setAttribute("pb", pageBean);

        return "f:/adminjsps/admin/order/list.jsp";
    }


    /**
     * 按状态查询订单
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到pc:如果页面传,使用页面的;否则默认为1
        int pc = getPc(request);
        //2.得到url
        String url = getUrl(request);
        //3.获取查询条件
        int status = Integer.parseInt(request.getParameter("status"));
        //4.使用pc和cid调用service方法
        PageBean<Order> pageBean = service.findByStatus(status, pc);
        //5.给pagebean设置url
        pageBean.setUrl(url);
        request.setAttribute("pb", pageBean);

        return "f:/adminjsps/admin/order/list.jsp";
    }


    /**
     * 加载订单
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String oid = request.getParameter("oid");
        Order order = service.load(oid);
        request.setAttribute("order", order);
        String btn = request.getParameter("btn");   //btn说明了用户点击那个超链接来访问订单详情
        request.setAttribute("btn", btn);
        return "/adminjsps/admin/order/desc.jsp";

    }


    /**
     * 取消订单
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String cancle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String oid = request.getParameter("oid");
        //校验订单状态
        int status = service.findStatus(oid);
        if (status != 1) {
            request.setAttribute("code", "error");
            request.setAttribute("msg", "状态异常,无法取消!");
            return "f:/adminjsps/msg.jsp";
        }
        service.updateStatus(oid, 5);
        request.setAttribute("code", "success");
        request.setAttribute("msg", "订单取消成功!");
        return "f:/adminjsps/msg.jsp";

    }


    /**
     * 发货
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String deliver(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String oid = request.getParameter("oid");
        //校验订单状态
        int status = service.findStatus(oid);
        if (status != 2) {
            request.setAttribute("code", "error");
            request.setAttribute("msg", "状态异常,无法发货!");
            return "f:/adminjsps/msg.jsp";
        }
        service.updateStatus(oid, 3);
        request.setAttribute("code", "success");
        request.setAttribute("msg", "订单发货成功!");
        return "f:/adminjsps/msg.jsp";

    }

}
