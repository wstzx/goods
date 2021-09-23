package com.tzx.user.dao;

import cn.itcast.jdbc.TxQueryRunner;
import com.tzx.user.domain.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

/**
 * 用户模块持久层
 */
public class UserDao {

    //对数据库操作的对象
    private QueryRunner qr = new TxQueryRunner();

    /**
     * 校验用户名是否注册
     *
     * @param loginName
     * @return
     */
    public boolean ajaxLoginName(String loginName) throws SQLException {
        String sql = "select count(*) from t_user where loginname=?";
        Number num = (Number) qr.query(sql, new ScalarHandler(), loginName);
        System.out.println(num);
        return num.intValue() == 0;

    }

    /**
     * 校验Email是否已注册
     *
     * @param email
     * @return
     * @throws SQLException
     */
    public boolean ajaxEmail(String email) throws SQLException {
        String sql = "select count(*) from t_user where email=?";
        Number num = (Number) qr.query(sql, new ScalarHandler(), email);
        return num.intValue() == 0;
    }

    /**
     * 添加用户
     *
     * @param user
     */
    public void add(User user) throws SQLException {

        String sql = "insert into t_user values(?,?,?,?,?,?)";
        Object[] params = {user.getUid(), user.getLoginname(), user.getLoginpass(), user.getEmail(), user.isStatus(), user.getActivationCode()};
        qr.update(sql, params);

    }

    /**
     * 通过激活码查询用户
     *
     * @param code
     * @return
     */
    public User findByCode(String code) throws SQLException {
        String sql = "select * from t_user where activationCode=?";
        return qr.query(sql, new BeanHandler<User>(User.class), code);
    }


    /**
     * 通过id修改用户的激活状态
     *
     * @param uid
     */
    public void updateStatus(String uid, boolean status) throws SQLException {
        String sql = "update t_user set status=? where uid=?";
        qr.update(sql, status, uid);
    }


    /**
     * 按用户名和密码查询用户
     *
     * @param loginname
     * @param loginpass
     * @return
     */
    public User findByLoginNameAndLoginPass(String loginname, String loginpass) throws SQLException {
        String sql = "select * from t_user where loginname=? and loginpass=?";
        User query = qr.query(sql, new BeanHandler<User>(User.class), loginname, loginpass);
        return query;
    }


    /**
     * 通过uid和密码查询用户
     *
     * @param uid
     * @param password
     * @return
     * @throws SQLException
     */
    public boolean findbyUidAndPassword(String uid, String password) throws SQLException {
        String sql = "select count(*) from t_user where uid=? and loginpass=?";
        Number num = (Number) qr.query(sql, new ScalarHandler(), uid, password);
        return num.intValue() > 0;
    }


    /**
     * 修改密码
     *
     * @param uid
     * @param password
     */
    public void updatePassword(String uid, String password) throws SQLException {
        String sql = "update t_user set loginpass=? where uid=?";
        qr.update(sql, password, uid);
    }

}
