package com.tzx.admin.admin.dao;

import cn.itcast.jdbc.TxQueryRunner;
import com.tzx.admin.admin.domain.Admin;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

public class AdminDao {

    private QueryRunner qr = new TxQueryRunner();


    /**
     * 查询管理员
     *
     * @param adminname
     * @param adminpwd
     * @return
     */
    public Admin find(String adminname, String adminpwd) throws SQLException {

        String sql = "select * from t_admin where adminname=? and adminpwd=?";
        Admin admin = qr.query(sql, new BeanHandler<Admin>(Admin.class), adminname, adminpwd);
        return admin;
    }

}
