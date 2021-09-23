package com.tzx.cart.service;

import cn.itcast.commons.CommonUtils;
import com.tzx.cart.dao.CartItemDao;
import com.tzx.cart.domain.CartItem;

import java.sql.SQLException;
import java.util.List;

public class CartItemService {

    private CartItemDao dao = new CartItemDao();

    /**
     * 我的购物车模块
     *
     * @param uid
     * @return
     */
    public List<CartItem> myCart(String uid) {
        try {
            return dao.findByUser(uid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 添加购物车条目
     *
     * @param cartItem
     */
    public void addItem(CartItem cartItem) {

        //查询该条目是否存在
        try {
            CartItem byUidAndBid = dao.findByUidAndBid(cartItem.getUser().getUid(), cartItem.getBook().getBid());
            if (byUidAndBid == null) { //没有就添加
                cartItem.setCartItemId(CommonUtils.uuid());
                dao.addCartItem(cartItem);
            } else {             //有就修改
                int quantity = cartItem.getQuantity() + byUidAndBid.getQuantity();
                dao.updateQuantity(byUidAndBid.getCartItemId(), quantity);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 批量删除
     *
     * @param cartItemIds
     */
    public void batchDelete(String cartItemIds) {

        try {
            dao.batchDelete(cartItemIds);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 修改数量并返回当前的数量
     *
     * @param cartItemId
     * @param quantity
     * @return
     */
    public CartItem updateQuantity(String cartItemId, int quantity) {

        try {
            dao.updateQuantity(cartItemId, quantity);
            return dao.findbyCartItemId(cartItemId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 加载多个cartItem
     *
     * @param cartItemIds
     * @return
     */
    public List<CartItem> loadCartItems(String cartItemIds) {

        try {
            return dao.loadCartItems(cartItemIds);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


}
