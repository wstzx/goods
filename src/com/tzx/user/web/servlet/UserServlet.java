package com.tzx.user.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import com.tzx.user.domain.User;
import com.tzx.user.service.UserService;
import com.tzx.user.service.exception.UserException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户模块控制层
 */
@WebServlet("/userServlet")
public class UserServlet extends BaseServlet {

    private UserService service = new UserService();

    /**
     * 注册功能
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.封装表单数据到User对象中
        User user = CommonUtils.toBean(req.getParameterMap(), User.class);
        //2.校验,如果失败,保存错误信息,回显数据
        Map<String, String> errors = registx(user, req.getSession());
        if (errors.size() > 0) {
            req.setAttribute("user", user);  //用于回显数据
            req.setAttribute("errors", errors);  //用于显示错误信息
            return "f:/jsps/user/regist.jsp";
        }
        //3.使用service完成业务注册
        service.regist(user);
        //4.保存成功信息,转发到msg.jsp显示
        req.setAttribute("code", "success");
        req.setAttribute("msg", "请马上到邮箱激活!");
        return "f:/jsps/msg.jsp";
    }

    /**
     * 后台注册校验(使用map保存错误信息)
     *
     * @param user
     * @return
     */
    private Map<String, String> registx(User user, HttpSession session) {
        Map<String, String> errors = new HashMap<>();  //保存错误信息
        //1.校验登录名
        String loginname = user.getLoginname();
        if (loginname == null || loginname.trim().isEmpty()) {
            errors.put("loginname", "用户名不能为空!");
        } else if (loginname.length() < 3 || loginname.length() > 10) {
            errors.put("loginname", "用户名长度在3-10位!");
        } else if (!service.ajaxLoginName(loginname)) {
            errors.put("loginname", "该用户名已被注册!");
        }

        //2.校验登录密码
        String loginpass = user.getLoginpass();
        if (loginpass == null || loginpass.trim().isEmpty()) {
            errors.put("loginpass", "密码不能为空!");
        } else if (loginname.length() < 3 || loginname.length() > 10) {
            errors.put("loginpass", "密码长度在3-10位!");
        }

        //3.确认密码校验
        String reloginpass = user.getReloginpass();
        if (reloginpass == null || reloginpass.trim().isEmpty()) {
            errors.put("reloginpass", "确认密码不能为空!");
        } else if (!loginpass.equals(reloginpass)) {
            errors.put("reloginpass", "两次密码不一致!");
        }

        //4.Email校验
        String email = user.getEmail();
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "邮箱不能为空!");
        } else if (!email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$")) {
            errors.put("email", "Email格式不正确!");
        } else if (!service.ajaxEmail(email)) {
            errors.put("email", "该邮箱已被注册!");
        }

        //5.验证码校验
        String verifyCode = user.getVerifyCode();
        String vcode = (String) session.getAttribute("vCode");
        if (verifyCode == null || verifyCode.trim().isEmpty()) {
            errors.put("verifyCode", "验证码不能为空!");
        } else if (!vcode.equalsIgnoreCase(verifyCode)) {
            errors.put("verifyCode", "验证码不正确!");
        }

        return errors;

    }

    /**
     * ajax用户名是否注册校验
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String ajaxLoginName(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取用户名
        String loginname = req.getParameter("loginname");
        //2.通过servce得到校验结果
        boolean b = service.ajaxLoginName(loginname);
        //3.发送给前端
        resp.getWriter().print(b);

        return null;
    }

    /**
     * ajax邮箱是否注册校验
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String ajaxEmail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取Email
        String email = req.getParameter("email");
        //2.通过servce得到校验结果
        boolean b = service.ajaxEmail(email);
        //3.发送给前端
        resp.getWriter().print(b);

        return null;
    }

    /**
     * ajax验证码是否正确校验
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String ajaxCode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获取验证码
        String verifyCode = req.getParameter("verifyCode");
        //2.获取图片上的真实验证码
        String vCode = (String) req.getSession().getAttribute("vCode");
        //3.忽略大小写比较
        boolean b = verifyCode.equalsIgnoreCase(vCode);
        //4.发送给前端
        resp.getWriter().print(b);
        return null;
    }

    /**
     * 激活功能
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String activation(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //1.获取参数激活码
        String code = req.getParameter("activationCode");
        //2.用激活码调用service方法完成激活,如果抛出异常,把异常信息拿出,保存到request中,转发到msg.jsp中
        try {
            service.activation(code);
            req.setAttribute("code", "success"); //通知msg.jsp显示对号
            req.setAttribute("msg", "恭喜您,已完成激活,注册成功!");
        } catch (UserException e) {
            //说明service抛出了异常
            req.setAttribute("msg", e.getMessage());
            req.setAttribute("code", "error");   //通知msg.jsp显示x
        }
        //3.保存成功信息到request中,转发到msg.jsp中

        return "f:/jsps/msg.jsp";
    }


    public String login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //1.封装表单数据到User中
        User user = CommonUtils.toBean(req.getParameterMap(), User.class);
        //2.校验表单数据
        Map<String, String> errors = loginx(user, req.getSession());
        if (errors.size() > 0) {
            req.setAttribute("user", user);  //用于回显数据
            req.setAttribute("errors", errors);  //用于显示错误信息
            return "f:/jsps/user/login.jsp";
        }
        //3.使用service查询得到User
        User login = service.login(user);
        //4.查看用户是否存在(保存错误信息,回显,转发到login.jsp)
        if (login == null) {
            req.setAttribute("msg", "用户名或者密码错误!");
            req.setAttribute("user", user);  //用于数据的回显
            return "f:/jsps/user/login.jsp";
        }
        //5.查看用户状态(保存错误信息,回显,转发login.jsp)
        else {
            if (!login.isStatus()) {
                req.setAttribute("msg", "您还未激活,请先激活后再登录!");
                req.setAttribute("user", user);
                return "f:/jsps/user/login.jsp";
            } else {
                //6.登录成功(保存用户到session和cookie,注意中文编码处理)
                //保存用户到session中
                req.getSession().setAttribute("sessionUser", login);//注意这个login使从数据库查询出的user
                //保存用户到cookie中
                String loginname = user.getLoginname();
                loginname = URLEncoder.encode(loginname, "utf-8");//给cookie设置编码
                Cookie cookie = new Cookie("loginname", loginname);
                cookie.setMaxAge(60 * 60 * 24 * 30); //设置cookie30天的寿命
                resp.addCookie(cookie);
                return "r:/index.jsp";//重定向到主页
            }
        }

    }

    /**
     * 后台登录校验(使用map保存错误信息)
     *
     * @param user
     * @return
     */
    private Map<String, String> loginx(User user, HttpSession session) {
        Map<String, String> errors = new HashMap<>();  //保存错误信息

        //1.校验登录名
        String loginname = user.getLoginname();
        if (loginname == null || loginname.trim().isEmpty()) {
            errors.put("loginname", "用户名不能为空!");
        } else if (loginname.length() < 3 || loginname.length() > 10) {
            errors.put("loginname", "用户名长度在3-10位!");
        }

        //2.校验登录密码
        String loginpass = user.getLoginpass();
        if (loginpass == null || loginpass.trim().isEmpty()) {
            errors.put("loginpass", "用户名不能为空!");
        } else if (loginname.length() < 3 || loginname.length() > 10) {
            errors.put("loginpass", "用户名长度在3-10位!");
        }

        return errors;

    }


    /**
     * 修改密码
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String updatePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.封装表单数据到user中
        User user = CommonUtils.toBean(req.getParameterMap(), User.class);
        //2.从session中获取uid
        User sessionUser = (User) req.getSession().getAttribute("sessionUser");
        if (user == null) {
            req.setAttribute("msg", "您还未登录!");
            return "f:/jsps/user/login.jsp";
        }
        try {
            //3.使用uid和表单中的新旧密码(有异常和无异常)
            service.updatepassword(sessionUser.getUid(), user.getLoginpass(), user.getNewloginpass());
            req.setAttribute("msg", "修改密码成功!");
            req.setAttribute("code", "success");
            return "f:/jsps/msg.jsp";
        } catch (UserException e) {
            req.setAttribute("msg", e.getMessage());//保存异常信息到request
            req.setAttribute("user", user);          //回显数据
            return "f:/jsps/user/pwd.jsp";
        }
    }


    /**
     * 退出功能
     *
     * @param req
     * @param resp
     * @return
     * @throws ServletException
     * @throws IOException
     */
    public String quit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getSession().invalidate();  //销毁session
        return "r:/jsps/user/login.jsp";
    }

}
