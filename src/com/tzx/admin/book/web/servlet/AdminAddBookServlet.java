package com.tzx.admin.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import com.tzx.book.domain.Book;
import com.tzx.book.service.BookService;
import com.tzx.category.domain.Category;
import com.tzx.category.service.CategoryService;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.Soundbank;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/adminAddBookServlet")
//这里涉及到文件的上传,不能使用request.getParameter(),必须使用post提交方式
public class AdminAddBookServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");

        //1.commons-fileupload的上传三部
        //创建工厂
        FileItemFactory factory = new DiskFileItemFactory();  //两个参数:(缓存大小:默认10kb,缓存目录)
        //创建解析器对象
        ServletFileUpload servletFileUpload = new ServletFileUpload(factory);
        servletFileUpload.setFileSizeMax(80 * 1024);//设置单个上传的文件上限为80kb
        //解析request,得到List<FileItem>
        List<FileItem> fileItemList = null;
        try {
            fileItemList = servletFileUpload.parseRequest(req);
        } catch (FileUploadException e) {
            //说明超出限制
            error("上传的文件超出上限,请重试!", req, resp);
            return;
        }
        //把List<FileItem>封装到Book对象中
        //普通表单字段
        Map<String, Object> map = new HashMap<>();
        for (FileItem fileItem : fileItemList) {
            if (fileItem.isFormField()) { //判断是否是普通表单字段
                map.put(fileItem.getFieldName(), fileItem.getString("utf-8"));//获取表单数据的name和value
            }
        }
        Book book = CommonUtils.toBean(map, Book.class);//封装表单字段
        Category category = CommonUtils.toBean(map, Category.class);
        book.setCategory(category);
        //开始把上传的图片保存起来
        //获取文件名并截取
        FileItem fileItem = fileItemList.get(1);    //获取大图
        String fieldName = fileItem.getName(); //获取文件名
        int index = fieldName.lastIndexOf("\\");//截取文件名:因为部分浏览器上传的是绝对路径
        if (index != -1) {
            fieldName = fieldName.substring(index + 1); //从index后面开始截取
        }
        //文件名添加前缀,避免重复
        fieldName = CommonUtils.uuid() + "_" + fieldName;
        //校验文件名的扩展名
        if (!fieldName.toLowerCase().endsWith(".jpg")) {
            error("上传的图片格式只能是jpg,请检查后再试!", req, resp);
            return;
        }
        //校验图片的尺寸
        //1.保存图片
        String realPath = this.getServletContext().getRealPath("/book_img");//获取真实路径
        File file = new File(realPath, fieldName);
        try {
            fileItem.write(file);   //它会把临时文件重定向到指定的,再删除临时文件
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        //2.校验尺寸
        ImageIcon imageIcon = new ImageIcon(file.getAbsolutePath());//使用文件路径创建ImageICon
        Image image = imageIcon.getImage(); //通过imageIcon得到Image对象
        if (image.getWidth(null) > 350 || image.getHeight(null) > 350) {
            error("您上传的图片尺寸太大,限制在350*350,请重试!", req, resp);
            file.delete();      //删除该图片
            return;
        }
        //指定图片的保存路径(需要获取真实路径),已保存

        //把图片的路径设置给Book对象
        book.setImage_w("book_img/" + fieldName);


        //开始把上传的图片保存起来
        //获取文件名并截取
        fileItem = fileItemList.get(2);    //获取小图
        fieldName = fileItem.getName(); //获取文件名
        index = fieldName.lastIndexOf("\\");//截取文件名:因为部分浏览器上传的是绝对路径
        if (index != -1) {
            fieldName = fieldName.substring(index + 1); //从index后面开始截取
        }
        //文件名添加前缀,避免重复
        fieldName = CommonUtils.uuid() + "_" + fieldName;
        //校验文件名的扩展名
        if (!fieldName.toLowerCase().endsWith(".jpg")) {
            error("上传的图片格式只能是jpg,请检查后再试!", req, resp);
            return;
        }
        //校验图片的尺寸
        //1.保存图片
        realPath = this.getServletContext().getRealPath("/book_img");//获取真实路径
        System.out.println(realPath);
        file = new File(realPath, fieldName);
        try {
            fileItem.write(file);   //它会把临时文件重定向到指定的,再删除临时文件
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        //2.校验尺寸
        imageIcon = new ImageIcon(file.getAbsolutePath());//使用文件路径创建ImageICon
        image = imageIcon.getImage(); //通过imageIcon得到Image对象
        if (image.getWidth(null) > 350 || image.getHeight(null) > 350) {
            error("您上传的图片尺寸太大,限制在350*350,请重试!", req, resp);
            file.delete();      //删除该图片
            return;
        }
        //指定图片的保存路径(需要获取真实路径),已保存

        //把图片的路径设置给Book对象
        book.setImage_b("book_img/" + fieldName);

        book.setBid(CommonUtils.uuid());

        //调用service完成保存
        BookService bookService = new BookService();
        bookService.add(book);

        //保存成功信息到masg.jsp
        req.setAttribute("msg", "图书添加成功!");
        req.getRequestDispatcher("/adminjsps/admin/msg.jsp").forward(req, resp);
    }

    /**
     * 保存错误信息,转发到错误页面
     *
     * @param msg
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    private void error(String msg, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setAttribute("msg", msg);
        request.setAttribute("psrents", new CategoryService().findParents());//回显数据
        request.getRequestDispatcher("/adminjsps/admin/book/add.jsp").forward(request, response);

    }
}
