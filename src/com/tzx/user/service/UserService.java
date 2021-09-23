package com.tzx.user.service;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;
import com.tzx.user.dao.UserDao;
import com.tzx.user.domain.User;
import com.tzx.user.service.exception.UserException;

import javax.mail.MessagingException;
import javax.mail.Session;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * 用户模块业务层
 */
public class UserService {

    private UserDao dao = new UserDao();

    /**
     * 用户名注册校验
     *
     * @param loginName
     * @return
     */
    public boolean ajaxLoginName(String loginName) {
        try {
            return dao.ajaxLoginName(loginName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Email校验
     *
     * @param email
     * @return
     */
    public boolean ajaxEmail(String email) {
        try {
            return dao.ajaxEmail(email);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加注册信息,发送激活码
     *
     * @param user
     */
    public void regist(User user) {
        //数据补齐
        user.setUid(CommonUtils.uuid());
        user.setStatus(false);  //未激活
        user.setActivationCode(CommonUtils.uuid() + CommonUtils.uuid());
        //向数据库插入
        try {
            dao.add(user);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //发邮件
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("email_template.properties");
        Properties properties = new Properties();
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String host = properties.getProperty("host");
        String name = properties.getProperty("username");
        String password = properties.getProperty("password");
        Session session = MailUtils.createSession(host, name, password);

        String from = properties.getProperty("from");
        String to = properties.getProperty("from");
        String subject = properties.getProperty("subject");
        String c = properties.getProperty("content");
        //MessageFormat.format()会把第一个参数中的{0},使用第二个参数来替换
        String content = MessageFormat.format(c, user.getActivationCode());
        Mail mail = new Mail(from, to, subject, content);

        try {
            MailUtils.send(session, mail);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 激活功能
     *
     * @param code
     */
    public void activation(String code) throws UserException {

        try {
            //1.通过激活码查询用户
            User user = dao.findByCode(code);
            //2.User为null,说明激活码无效
            if (user == null) {
                throw new UserException("无效的激活码!");
            }
            //3.查看用户的激活状态(已激活)
            if (user.isStatus() == true) {
                throw new UserException("您已经激活过了,不要重复激活!");
            }
            //4.修改用户的激活状态
            dao.updateStatus(user.getUid(), true);  //修改状态
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 登录功能
     *
     * @param user
     * @return
     */
    public User login(User user) {
        try {
            return dao.findByLoginNameAndLoginPass(user.getLoginname(), user.getLoginpass());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 修改密码
     *
     * @param uid
     * @param oldPass
     * @param newPass
     */
    public void updatepassword(String uid, String oldPass, String newPass) throws UserException {
        //1.校验老密码
        try {
            boolean b = dao.findbyUidAndPassword(uid, oldPass);
            if (!b) {
                throw new UserException("原始密码错误!");
            }
            //2.修改密码
            dao.updatePassword(uid, newPass);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
