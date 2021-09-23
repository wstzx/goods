package com.tzx.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import com.tzx.book.domain.Book;
import com.tzx.book.service.BookService;
import com.tzx.pager.PageBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/bookServlet")
public class BookServlet extends BaseServlet {

    private BookService service = new BookService();

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
     * 通过分类查询
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到pc:如果页面传,使用页面的;否则默认为1
        int pc = getPc(request);
        //2.得到url
        String url = getUrl(request);
        //3.获取查询条件
        String cid = request.getParameter("cid");
        //4.使用pc和cid调用service方法
        PageBean<Book> pageBean = service.findByCategory(cid, pc);
        //5.给pagebean设置url
        pageBean.setUrl(url);
        request.setAttribute("pb", pageBean);

        return "f:/jsps/book/list.jsp";
    }

    /**
     * 通过书名模糊查询
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByBname(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到pc:如果页面传,使用页面的;否则默认为1
        int pc = getPc(request);
        //2.得到url
        String url = getUrl(request);
        //3.获取查询条件
        String bname = request.getParameter("bname");
        //4.使用pc和cid调用service方法
        PageBean<Book> pageBean = service.findByBname(bname, pc);
        //5.给pagebean设置url
        pageBean.setUrl(url);
        request.setAttribute("pb", pageBean);

        return "f:/jsps/book/list.jsp";

    }

    /**
     * 通过作者模糊查询
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByAuthor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到pc:如果页面传,使用页面的;否则默认为1
        int pc = getPc(request);
        //2.得到url
        String url = getUrl(request);
        //3.获取查询条件
        String author = request.getParameter("author");
        //4.使用pc和cid调用service方法
        PageBean<Book> pageBean = service.findByAuthor(author, pc);
        //5.给pagebean设置url
        pageBean.setUrl(url);
        request.setAttribute("pb", pageBean);

        return "f:/jsps/book/list.jsp";

    }


    /**
     * 通过出版社模糊查询
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByPress(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到pc:如果页面传,使用页面的;否则默认为1
        int pc = getPc(request);
        //2.得到url
        String url = getUrl(request);
        //3.获取查询条件
        String press = request.getParameter("press");
        //4.使用pc和cid调用service方法
        PageBean<Book> pageBean = service.findByPress(press, pc);
        //5.给pagebean设置url
        pageBean.setUrl(url);
        request.setAttribute("pb", pageBean);

        return "f:/jsps/book/list.jsp";

    }


    /**
     * 高级模糊搜索
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByCombination(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //1.得到pc:如果页面传,使用页面的;否则默认为1
        int pc = getPc(request);
        //2.得到url
        String url = getUrl(request);
        //3.获取查询条件
        Map<String, String[]> parameterMap = request.getParameterMap();
        Book book = CommonUtils.toBean(parameterMap, Book.class);
        //4.使用pc和cid调用service方法
        PageBean<Book> pageBean = service.findByCombination(book, pc);
        //5.给pagebean设置url
        pageBean.setUrl(url);
        request.setAttribute("pb", pageBean);

        return "f:/jsps/book/list.jsp";

    }


    /**
     * 按图书的id查询整本书
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findByBid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String bid = request.getParameter("bid");
        Book book = service.findByBid(bid);
        request.setAttribute("book", book);

        return "f:/jsps/book/desc.jsp";

    }


}
