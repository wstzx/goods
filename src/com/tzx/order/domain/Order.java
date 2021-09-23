package com.tzx.order.domain;

import com.tzx.user.domain.User;

import java.util.List;

public class Order {

    private String oid;         //主键
    private String ordertime;   //下单时间
    private Double total;       //总计
    private Integer status;     //订单状态:1未付款;2.已付款未发货;3.已发货未确认收货;4.确认收货了;5.已取消(只有未付款才能取消)
    private String address;     //收货地址
    private User user;          //订单的所有者
    private List<OrderItem> orderItems;     //订单条目

    @Override
    public String toString() {
        return "Order{" +
                "oid='" + oid + '\'' +
                ", ordertime='" + ordertime + '\'' +
                ", total=" + total +
                ", status=" + status +
                ", address='" + address + '\'' +
                ", user=" + user +
                ", orderItems=" + orderItems +
                '}';
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(String ordertime) {
        this.ordertime = ordertime;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
