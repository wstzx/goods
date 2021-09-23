package com.tzx.order.service;

import cn.itcast.jdbc.JdbcUtils;
import com.tzx.order.dao.OrderDao;
import com.tzx.order.domain.Order;
import com.tzx.pager.PageBean;

import java.sql.SQLException;

public class OrderService {

    private OrderDao dao = new OrderDao();

    /**
     * 通过id查询用户的订单
     *
     * @param uid
     * @param pc
     * @return
     */
    public PageBean<Order> myOrders(String uid, int pc) {

        try {
            JdbcUtils.beginTransaction();
            PageBean<Order> orderPageBean = dao.findByUser(uid, pc);
            JdbcUtils.commitTransaction();
            return orderPageBean;
        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        }

    }


    /**
     * 创建订单
     *
     * @param order
     */
    public void addOrder(Order order) {

        try {
            JdbcUtils.beginTransaction();
            dao.add(order);
            JdbcUtils.commitTransaction();
        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException throwables) {
                throw new RuntimeException(throwables);
            }
            throw new RuntimeException(e);
        }

    }


    /**
     * 加载订单
     *
     * @param oid
     * @return
     */
    public Order load(String oid) {

        try {
            JdbcUtils.beginTransaction();
            Order order = dao.load(oid);
            JdbcUtils.commitTransaction();
            return order;
        } catch (Exception e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException throwables) {
                throw new RuntimeException(throwables);
            }
            throw new RuntimeException(e);
        }

    }


    /**
     * 查询订单状态
     *
     * @param oid
     * @return
     */
    public int findStatus(String oid) {

        try {
            return dao.findStatus(oid);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }


    /**
     * 修改订单状态
     *
     * @param oid
     * @param status
     */
    public void updateStatus(String oid, int status) {

        try {
            dao.updateStatus(oid, status);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }


    /**
     * 查询所有订单
     *
     * @param pc
     * @return
     */
    public PageBean<Order> findAll(int pc) {

        try {
            JdbcUtils.beginTransaction();
            PageBean<Order> orderPageBean = dao.findAll(pc);
            JdbcUtils.commitTransaction();
            return orderPageBean;
        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        }

    }


    /**
     * 按状态查询订单
     *
     * @param pc
     * @return
     */
    public PageBean<Order> findByStatus(int status, int pc) {

        try {
            JdbcUtils.beginTransaction();
            PageBean<Order> orderPageBean = dao.findByStatus(status, pc);
            JdbcUtils.commitTransaction();
            return orderPageBean;
        } catch (SQLException e) {
            try {
                JdbcUtils.rollbackTransaction();
            } catch (SQLException e1) {
                throw new RuntimeException(e1);
            }
            throw new RuntimeException(e);
        }

    }


}
