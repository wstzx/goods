package com.tzx.cart.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tzx.book.domain.Book;
import com.tzx.cart.domain.CartItem;
import com.tzx.cart.service.CartItemService;
import com.tzx.user.domain.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/cartServlet")
public class CartItemServlet extends BaseServlet {

    private CartItemService service = new CartItemService();


    /**
     * 我的购物车模块
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String myCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到uid
        User user = (User) request.getSession().getAttribute("sessionUser");
        if (user != null) {
            String uid = user.getUid();
            //2.通过service得到该用户的多有购物车条目
            List<CartItem> cartItems = service.myCart(uid);

            //3.保存到request
            request.setAttribute("cartItem", cartItems);
            return "f:/jsps/cart/list.jsp";
        }
        request.setAttribute("msg", "您还未登录,请登录后再试!");
        return "f:/jsps/user/login.jsp";
    }


    /**
     * 添加购物车条目
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String addItem(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        ///封装表单数据
        Map<String, String[]> parameterMap = request.getParameterMap();
        CartItem cartItem = CommonUtils.toBean(parameterMap, CartItem.class);
        Book book = CommonUtils.toBean(parameterMap, Book.class);
        User user = (User) request.getSession().getAttribute("sessionUser");
        if (user != null) {
            cartItem.setBook(book);
            cartItem.setUser(user);
            //调用service完成添加
            service.addItem(cartItem);
            //查询当前用户的所有条目,转发到list.jsp显示
            return myCart(request, response);
        }
        request.setAttribute("msg", "您还未登录,请登录后再试!");
        return "f:/jsps/user/login.jsp";
    }


    /**
     * 批量删除
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String batchDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String cartItemIds = request.getParameter("cartItemIds");
        service.batchDelete(cartItemIds);
        return myCart(request, response);

    }


    /**
     * 修改购物车条目的数量并返回当前的数量
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String updateQuantity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String cartItemIds = request.getParameter("cartItemIds");
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        System.out.println("你好");
        CartItem cartItem = service.updateQuantity(cartItemIds, quantity);
        System.out.println(cartItem);
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(cartItem);
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(s);
        writer.flush();
        writer.close();
        return null;
    }


    /**
     * 加载多个CartItem
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String loadCartItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String cartItemIds = request.getParameter("cartItemIds");
        List<CartItem> cartItems = service.loadCartItems(cartItemIds);
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartItemIds", cartItemIds);
        return "f:/jsps/cart/showitem.jsp";

    }


}
