package com.tzx.cart.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import com.tzx.book.domain.Book;
import com.tzx.cart.domain.CartItem;
import com.tzx.user.domain.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CartItemDao {

    private QueryRunner qr = new TxQueryRunner();

    /**
     * 通过用户的uid查询购物车的条目
     *
     * @param uid
     * @return
     */
    public List<CartItem> findByUser(String uid) throws SQLException {

        String sql = "select * from t_cartitem c,t_book b where c.bid=b.bid and uid=? order by c.orderBy";
        List<Map<String, Object>> maps = qr.query(sql, new MapListHandler(), uid);
        List<CartItem> cartItems = toCartItemList(maps);
        return cartItems;

    }


    /**
     * 将map映射成一个cartItemd对象
     *
     * @param map
     * @return
     */
    private CartItem toCartItem(Map<String, Object> map) {

        if (map == null || map.size() == 0) return null;
        CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
        Book book = CommonUtils.toBean(map, Book.class);
        User user = CommonUtils.toBean(map, User.class);
        cartItem.setBook(book);
        cartItem.setUser(user);
        return cartItem;

    }


    /**
     * 把多个Map的集合映射成多个CartItem的对象装在list的集合中
     *
     * @param mapList
     * @return
     */
    private List<CartItem> toCartItemList(List<Map<String, Object>> mapList) {

        List<CartItem> list = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            list.add(toCartItem(map));
        }
        return list;

    }


    /**
     * 查询某个用户的某本图书的购物车条目是否存在
     *
     * @param uid
     * @param bid
     * @return
     */
    public CartItem findByUidAndBid(String uid, String bid) throws SQLException {

        String sql = "select * from t_cartitem where uid=? and bid=?";
        Map<String, Object> map = qr.query(sql, new MapHandler(), uid, bid);
        CartItem cartItem = toCartItem(map);
        return cartItem;

    }


    /**
     * 通过购物车条目id修改图书的数量
     *
     * @param cartItemId
     * @param quantity
     */
    public void updateQuantity(String cartItemId, int quantity) throws SQLException {

        String sql = "update t_cartitem set quantity=? where cartItemId=?";
        qr.update(sql, quantity, cartItemId);

    }


    /**
     * 添加购物车条目
     *
     * @param cartItem
     */
    public void addCartItem(CartItem cartItem) throws SQLException {

        String sql = "insert into t_cartitem(cartItemId,quantity,bid,uid) values(?,?,?,?)";
        Object[] params = {cartItem.getCartItemId(), cartItem.getQuantity(), cartItem.getBook().getBid(), cartItem.getUser().getUid()};
        qr.update(sql, params);


    }


    /**
     * 批量删除条目
     *
     * @param cartItemIds
     */
    public void batchDelete(String cartItemIds) throws SQLException {

        Object[] split = cartItemIds.split(",");
        //把cartIemIds转换成where自子句
        String whereSql = toWhereSql(split.length);
        //与delete from连接在一起
        String sql = "delete from t_cartitem where " + whereSql;
        qr.update(sql, split);   //注意:这里的cartItemIds必须是Object类型的数组

    }

    /**
     * 用来生成where子句
     *
     * @param len
     * @return
     */
    private String toWhereSql(int len) {

        StringBuilder stringBuilder = new StringBuilder("cartItemId in (");
        for (int i = 0; i < len; i++) {
            stringBuilder.append("?");
            if (i < len - 1) {
                stringBuilder.append(",");
            }
        }
        stringBuilder.append(")");
        return stringBuilder.toString();

    }


    /**
     * 通过id查询
     *
     * @param cartItemId
     * @return
     */
    public CartItem findbyCartItemId(String cartItemId) throws SQLException {

        String sql = "select * from t_cartitem c,t_book b where c.bid=b.bid and c.cartItemId=?";
        Map<String, Object> map = qr.query(sql, new MapHandler(), cartItemId);
        return toCartItem(map);

    }


    /**
     * 加载多个cartItem
     *
     * @param cartItemIds
     * @return
     */
    public List<CartItem> loadCartItems(String cartItemIds) throws SQLException {

        //1.转换成数组
        String[] split = cartItemIds.split(",");
        //2.生成where子句
        String whereSql = toWhereSql(split.length);
        //3.生成sql子句
        String sql = "select * from t_cartitem c,t_book b where c.bid=b.bid and " + whereSql;
        List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(), split);
        List<CartItem> cartItems = toCartItemList(mapList);
        return cartItems;

    }


}
