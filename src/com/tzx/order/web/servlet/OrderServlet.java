package com.tzx.order.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import com.tzx.cart.domain.CartItem;
import com.tzx.cart.service.CartItemService;
import com.tzx.order.domain.Order;
import com.tzx.order.domain.OrderItem;
import com.tzx.order.service.OrderService;
import com.tzx.pager.PageBean;
import com.tzx.user.domain.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@WebServlet("/orderServlet")
public class OrderServlet extends BaseServlet {

    private OrderService service = new OrderService();
    private CartItemService cartItemService = new CartItemService();

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
     * 我的订单
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String myOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到pc:如果页面传,使用页面的;否则默认为1
        int pc = getPc(request);
        //2.得到url
        String url = getUrl(request);
        //3.获取查询条件
        User user = (User) request.getSession().getAttribute("sessionUser");
        if (user != null) {
            //4.使用pc和cid调用service方法
            PageBean<Order> pageBean = service.myOrders(user.getUid(), pc);
            //5.给pagebean设置url
            pageBean.setUrl(url);
            request.setAttribute("pb", pageBean);

            return "f:/jsps/order/list.jsp";
        }
        request.setAttribute("msg", "您还未登录,请登录后再试!");
        return "f:/jsps/user/login.jsp";
    }


    /**
     * 创建订单
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String addOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        //1.获取所有购物车条目的id
        String cartItemIds = request.getParameter("cartItemIds");
        List<CartItem> cartItems = cartItemService.loadCartItems(cartItemIds);
        //2.创建订单
        Order order = new Order();
        order.setOid(CommonUtils.uuid());
        order.setOrdertime(String.format("%tF %<tT", new Date()));    //获取下单时间
        order.setStatus(1); //设置1表示未付款
        order.setAddress(new String(request.getParameter("address").getBytes("ISO-8859-1"), "utf-8"));
        User user = (User) request.getSession().getAttribute("sessionUser");
        order.setUser(user);

        //计算总计
        BigDecimal total = new BigDecimal("0");
        for (CartItem cartItem : cartItems) {
            total = total.add(new BigDecimal(cartItem.getTotal() + ""));
        }
        order.setTotal(total.doubleValue());

        //创建orderItem列表
        List<OrderItem> orderItemList = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderItemId(CommonUtils.uuid());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getTotal());
            orderItem.setBook(cartItem.getBook());
            orderItem.setOrder(order);
            orderItemList.add(orderItem);
        }
        order.setOrderItems(orderItemList);
        service.addOrder(order);

        //删除购物车里的条目
        cartItemService.batchDelete(cartItemIds);
        request.setAttribute("order", order);
        return "f:/jsps/order/ordersucc.jsp";
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
        return "/jsps/order/desc.jsp";

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
            return "f:/jsps/msg.jsp";
        }
        service.updateStatus(oid, 5);
        request.setAttribute("code", "success");
        request.setAttribute("msg", "订单取消成功!");
        return "f:/jsps/msg.jsp";

    }


    /**
     * 确认收货
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String confirm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String oid = request.getParameter("oid");
        //校验订单状态
        int status = service.findStatus(oid);
        if (status != 3) {
            request.setAttribute("code", "error");
            request.setAttribute("msg", "状态异常,无法确认!");
            return "f:/jsps/msg.jsp";
        }
        service.updateStatus(oid, 4);
        request.setAttribute("code", "success");
        request.setAttribute("msg", "订单交易成功!");
        return "f:/jsps/msg.jsp";

    }


    /**
     * 支付前准备
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public String paymentPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setAttribute("order", service.load(request.getParameter("oid")));
        return "f:/jsps/order/pay.jsp";

    }


    /**
     * 易宝支付
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String payment(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
        //准备13个参数(大小写敏感)
        String p0_Cmd = "Buy";    //业务类型,固定值"Buy"
        String p1_MerId = properties.getProperty("p1_MerId");     //商号编码,在易宝的唯一标识
        String p2_Order = request.getParameter("oid");            //订单编号
        String p3_Amt = "0.01";   //支付金额
        String p4_Cur = "CNY";    //交易币种,固定值"CNY"
        String p5_Pid = "";       //商品名称
        String p6_Pcat = "";      //商品种类
        String p7_Pdesc = "";     //商品描述
        String p8_Url = properties.getProperty("p8_Url");     //支付成功后,易宝会访问这个地址
        String p9_SAF = "";       //送货地址
        String pa_MP = "";        //扩展信息
        String pd_FrpId = request.getParameter("yh");     //支付通道:银行
        String pr_NeedResponse = "1";     //应答机制,固定值"1"

        //计算hmac:需要13个参数,需要keyValue,需要加密算法
        String keyValue = properties.getProperty("keyValue");
        String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);
        //重定向到易宝的支付网关
        StringBuilder sb = new StringBuilder("https://www.yeepay.com/app-merchant-proxy/node");
        sb.append("?").append("p0_Cmd=").append(p0_Cmd);
        sb.append("&").append("p1_MerId=").append(p1_MerId);
        sb.append("&").append("p2_Order=").append(p2_Order);
        sb.append("&").append("p3_Amt=").append(p3_Amt);
        sb.append("&").append("p4_Cur=").append(p4_Cur);
        sb.append("&").append("p5_Pid=").append(p5_Pid);
        sb.append("&").append("p6_Pcat=").append(p6_Pcat);
        sb.append("&").append("p7_Pdesc=").append(p7_Pdesc);
        sb.append("&").append("p8_Url=").append(p8_Url);
        sb.append("&").append("p9_SAF=").append(p9_SAF);
        sb.append("&").append("pa_MP=").append(pa_MP);
        sb.append("&").append("pd_FrpId=").append(pd_FrpId);
        sb.append("&").append("pr_NeedResponse=").append(pr_NeedResponse);
        sb.append("&").append("hmac=").append(hmac);
        response.sendRedirect(sb.toString());
        return null;

    }

    /**
     * 易宝的回馈处理:当支付成功时,易宝会访问这里
     * 用两种方式访问:
     * 1.引导用户的浏览器重定向(如果用户关闭了浏览器,就不能访问这里了)
     * 2.易宝的服务器会使用点对点通讯的方式访问这个方法(必须回馈success,不然易宝服务器会一直调用这个方法)
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String back(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.获取12个参数
        String r0_Cmd = request.getParameter("p0_Cmd");
        String p1_MerId = request.getParameter("p1_MerId");
        String r1_Code = request.getParameter("r1_Code");
        String r2_TrxId = request.getParameter("r2_TrxId");
        String r3_Amt = request.getParameter("r3_Amt");
        String r4_Cur = request.getParameter("r4_Cur");
        String r5_Pid = request.getParameter("r5_Pid");
        String r6_Order = request.getParameter("r6_Order");
        String r7_Uid = request.getParameter("r7_Uid");
        String r8_MP = request.getParameter("r8_MP");
        String r9_BType = request.getParameter("r9_BType");
        String hmac = request.getParameter("hmac");

        //2.获取keyValue
        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
        String keyValue = properties.getProperty("keyValue");
        //3.调用PaymentUtil的校验方法校验调用者的身份
        //校验失败,保存错误信息
        //校验通过,判断访问的方式是重定向还是点对点
        //重定向:修改订单状态,保存成功信息
        //点对点:修改订单状态,返回success
        boolean b = PaymentUtil.verifyCallback(hmac, p1_MerId,
                r0_Cmd, r1_Code, r2_TrxId, r3_Amt,
                r4_Cur, r5_Pid, r6_Order, r7_Uid,
                r8_MP, r9_BType, keyValue);
        if (!b) {
            request.setAttribute("code", "error");
            request.setAttribute("msg", "无效的签名,支付失败!");
            return "f:/jsps/msg.jsp";
        }

        if (r1_Code.equals("1")) {
            service.updateStatus(r6_Order, 2);
            if (r9_BType.equals("1")) {       //重定向
                request.setAttribute("code", "success");
                request.setAttribute("msg", "支付成功!");
                return "f:/jsps/msg.jsp";
            } else if (r9_BType.equals("2")) {    //点对点
                response.getWriter().print("success");
            }
        }


        return null;
    }


    public String aa(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("what");
        return null;
    }

}
