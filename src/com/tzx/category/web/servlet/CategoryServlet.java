package com.tzx.category.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import com.tzx.category.domain.Category;
import com.tzx.category.service.CategoryService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 分类模块控制层
 */
@WebServlet("/categoryServlet")
public class CategoryServlet extends BaseServlet {
    private CategoryService service = new CategoryService();

    /**
     * 查找所有的分类
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    public String findCategoryAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //1.通过service得到所有的分类
        List<Category> all = service.findAll();
        //2.保存到request中,转发到left.jsp
        req.setAttribute("parents", all);
        return "f:/jsps/left.jsp";

    }


    /**
     * 查询所有分类
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String findAll(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setAttribute("parents", service.findAll());
        return "f:/jsps/left.jsp";

    }


    /**
     * 添加一级分类
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String addFirst(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String cname = new String(request.getParameter("cname").getBytes("ISO-8859-1"), "UTF-8");
        Category byCname = service.findByCname(cname);
        if (byCname != null) {
            request.setAttribute("msg", "该分类名已存在请重试!");
            return "f:/jsps/category/add.jsp";
        }

        Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
        category.setCname(new String(category.getCname().getBytes("ISO-8859-1"), "UTF-8"));
        category.setDesc(new String(category.getDesc().getBytes("ISO-8859-1"), "UTF-8"));
        category.setCid(CommonUtils.uuid());
        service.add(category);
        return findAll(request, response);

    }


    /**
     * 添加二级分类准备
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String addSecondPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String pid = request.getParameter("pid");//获取当前点击的父分类id
        List<Category> parents = service.findParents(); //获取所有的父分类
        request.setAttribute("pid", pid);
        request.setAttribute("parents", parents);
        return "f:/jsps/category/add2.jsp";

    }


    /**
     * 添加二级分类
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String addSecond(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        String cname = new String(request.getParameter("cname").getBytes("ISO-8859-1"), "UTF-8");
        Category byCname = service.findByCname(cname);
        if (byCname != null) {
            request.setAttribute("msg", "该分类名已存在请重试!");
            return "f:/jsps/category/add2.jsp";
        }
        Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
        category.setCname(new String(category.getCname().getBytes("ISO-8859-1"), "UTF-8"));
        category.setDesc(new String(category.getDesc().getBytes("ISO-8859-1"), "UTF-8"));
        category.setCid(CommonUtils.uuid());//设置cid
        String pid = request.getParameter("pid");
        Category parengt = new Category();
        parengt.setCid(pid);
        category.setParent(parengt);
        service.add(category);
        return findAll(request, response);
    }


    /**
     * 修改一级分类:第一步
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String editFirstPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String cid = request.getParameter("cid");
        Category parent = service.load(cid);
        request.setAttribute("parent", parent);
        return "f:/jsps/category/edit.jsp";

    }


    /**
     * 修改一级分类:第二步
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String editFirst(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
        category.setCname(new String(category.getCname().getBytes("ISO-8859-1"), "UTF-8"));
        category.setDesc(new String(category.getDesc().getBytes("ISO-8859-1"), "UTF-8"));
        if (service.findByCnameButCid(category.getCname(), category.getCid()) != null) {
            request.setAttribute("msg", "该分类名已存在请重试!");
            return "f:/jsps/category/edit.jsp";
        }
        category.setDesc(new String(category.getDesc().getBytes("ISO-8859-1"), "UTF-8"));
        Category byCname = service.findByCname(category.getCname());
        service.edit(category);
        return findAll(request, response);

    }


    /**
     * 修改二级分类：第一步
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String editSecondPre(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String cid = request.getParameter("cid");
        Category load = service.load(cid);
        request.setAttribute("child", load);
        request.setAttribute("parents", service.findParents());
        return "f:/jsps/category/edit2.jsp";

    }


    /**
     * 修改二级分类：第二部
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String editSecond(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Category category = CommonUtils.toBean(request.getParameterMap(), Category.class);
        category.setCname(new String(category.getCname().getBytes("ISO-8859-1"), "UTF-8"));
        category.setDesc(new String(category.getDesc().getBytes("ISO-8859-1"), "UTF-8"));
        if (service.findByCnameButCid(category.getCname(), category.getCid()) != null) {
            request.setAttribute("msg", "该分类名已存在请重试!");
            return "f:/jsps/category/edit2.jsp";
        }
        String pid = request.getParameter("pid");
        Category parent = new Category();
        parent.setCid(pid);
        category.setParent(parent);
        service.edit(category);
        return findAll(request, response);

    }


    /**
     * 删除一级分类
     *
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String deleteFirst(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //查看该分类下是否有子分类
        String cid = request.getParameter("cid");
        int countByParent = service.findCountByParent(cid);
        if (countByParent > 0) {
            request.setAttribute("code", "error");
            request.setAttribute("msg", "该分类下还有子分类,无法删除!");
            return "f:/jsps/msg.jsp";
        }
        service.delete(cid);
        return findAll(request, response);
    }


}
