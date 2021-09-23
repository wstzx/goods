package com.tzx.admin.admin.service;

import com.tzx.admin.admin.dao.AdminDao;
import com.tzx.admin.admin.domain.Admin;

import java.sql.SQLException;

public class AdminService {

    private AdminDao dao = new AdminDao();

    /**
     * 登录功能
     *
     * @param admin
     * @return
     */
    public Admin login(Admin admin) {

        try {
            return dao.find(admin.getAdminname(), admin.getAdminpwd());
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }

}
