package com.tzx.admin.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tzx.book.domain.Book;
import com.tzx.book.service.BookService;
import com.tzx.category.domain.Category;
import com.tzx.category.service.CategoryService;
import com.tzx.pager.PageBean;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/adminBookServlet")
public class AdminBookServlet extends BaseServlet {

    private BookService service = new BookService();
    private CategoryService categoryService = new CategoryService();


    /**
     * 查找所有的分类
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public String findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //1.通过service得到所有的分类
        List<Category> all = categoryService.findAll();
        //2.保存到request中,转发到left.jsp
        req.setAttribute("parents", all);
        return "f:/adminjsps/admin/book/left.jsp";

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

        return "f:/adminjsps/admin/book/list.jsp";
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

        return "f:/adminjsps/admin/book/list.jsp";

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

        return "f:/adminjsps/admin/book/list.jsp";

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

        return "f:/adminjsps/admin/book/list.jsp";

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

        return "f:/adminjsps/admin/book/list.jsp";

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

        return "f:/adminjsps/admin/book/desc.jsp";

    }


    /**
     * 获取所有的一级分类
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findFirstCategoryAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        List<Category> parents = categoryService.findParents();
        request.setAttribute("parents", parents);
        return "f:/adminjsps/admin/book/add.jsp";

    }


    /**
     * 通过异步请求查找指定父分类下的子分类
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String ajaxFindSecodCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String pid = request.getParameter("pid");
        List<Category> children = categoryService.findByParent(pid);
        ObjectMapper mapper = new ObjectMapper();
        String s = mapper.writeValueAsString(children);
        response.setContentType("application/json;charset=utf-8");
        PrintWriter writer = response.getWriter();
        writer.write(s);
        writer.flush();
        writer.close();

        return null;

    }


    /**
     * 加载图书
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String bid = request.getParameter("bid");
        Book book = service.findByBid(bid);
        request.setAttribute("book", book);
        request.setAttribute("parents", categoryService.findParents());
        System.out.println(categoryService.findParents());
        String pid = book.getCategory().getParent().getCid();//获取当前图书所属分类下的所有二级分类
        request.setAttribute("children", categoryService.findByParent(pid));
        return "f:/adminjsps/admin/book/desc.jsp";

    }


    /**
     * 编辑图书
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Map map = request.getParameterMap();
        Book book = CommonUtils.toBean(map, Book.class);
        book.setBname(new String(book.getBname().getBytes("ISO-8859-1"), "UTF-8"));
        book.setPress(new String(book.getPress().getBytes("ISO-8859-1"), "UTF-8"));
        book.setPaper(new String(book.getPaper().getBytes("ISO-8859-1"), "UTF-8"));
        book.setAuthor(new String(book.getAuthor().getBytes("ISO-8859-1"), "UTF-8"));

        Category category = CommonUtils.toBean(map, Category.class);
        book.setCategory(category);
        service.edit(book);
        request.setAttribute("msg", "修改图书成功!");
        return "f:/adminjsps/msg.jsp";

    }


    /**
     * 删除图书
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String bid = request.getParameter("bid");
        Book book = service.findByBid(bid);
        String realPath = this.getServletContext().getRealPath("/");//获取真实路径

        new File(realPath, book.getImage_w()).delete();  //删除文件
        new File(realPath, book.getImage_b()).delete();

        service.delete(bid);

        request.setAttribute("msg", "删除成功!");
        return "f:/adminjsps/msg.jsp";

    }
}
