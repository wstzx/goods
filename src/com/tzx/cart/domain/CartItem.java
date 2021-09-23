package com.tzx.cart.domain;

import com.tzx.book.domain.Book;
import com.tzx.user.domain.User;

import java.math.BigDecimal;

public class CartItem {

    private String cartItemId;  //主键
    private Integer quantity;    //数量
    private Book book;          //条目所对应的图书
    private User user;          //所属用户

    //小计
    public double getTotal() {
        //这里使用*会有精度误差,使用 BigDecimal(String)解决这种误差.注意:必须要用string类型的构造器
        BigDecimal b1 = new BigDecimal(book.getCurrPrice() + "");
        BigDecimal b2 = new BigDecimal(quantity + "");
        BigDecimal b3 = b1.multiply(b2);

        return b3.doubleValue();    //最后将结果转化成double类型的
    }

    public String getCartItemId() {
        return cartItemId;
    }

    public void setCartItemId(String cartItemId) {
        this.cartItemId = cartItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemId='" + cartItemId + '\'' +
                ", quantity='" + quantity + '\'' +
                ", book=" + book +
                ", user=" + user +
                '}';
    }
}
