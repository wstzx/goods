package com.tzx.order.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import com.tzx.book.domain.Book;
import com.tzx.order.domain.Order;
import com.tzx.order.domain.OrderItem;
import com.tzx.pager.Expression;
import com.tzx.pager.PageBean;
import com.tzx.pager.PageConstants;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrderDao {

    private QueryRunner qr = new TxQueryRunner();


    /**
     * 通用的查询方法
     *
     * @param expressionList
     * @param pc
     * @return
     */
    private PageBean<Order> findByCriteria(List<Expression> expressionList, int pc) throws SQLException {

        //1.得到ps,每页的记录数
        int ps = PageConstants.ORDER_PAGE_SIZE;
        //2.通过expression生成where子句
        StringBuilder whereSql = new StringBuilder(" where 1=1");
        List<Object> params = new ArrayList<>();  //对应问号的值
        for (Expression express : expressionList) {
            whereSql.append(" and ").append(express.getName() + " ").append(express.getOperator() + " ");
            if (!express.getOperator().equals("is null")) {
                whereSql.append("?");
                params.add(express.getValue());
            }
        }
        //3.总记录数
        String sql = "select count(*) from t_order" + whereSql;
        Number number = (Number) qr.query(sql, new ScalarHandler(), params.toArray());
        int tr = number.intValue();       //得到总记录数
        //4.得到beanList,即当前页记录
        sql = "select * from t_order" + whereSql + " order by ordertime desc limit ?,?";
        params.add((pc - 1) * ps);  //当前页首行记录的下标
        params.add(ps);         //一共查询几行,就是每页的记录数
        List<Order> beanList = qr.query(sql, new BeanListHandler<Order>(Order.class), params.toArray());
        //遍历每个订单,为其加载所有的订单条目
        for (Order order : beanList) {
            loadOrderItem(order);
        }


        //5.创建Pagebean设置参数
        PageBean<Order> pb = new PageBean<>();
        pb.setBeanList(beanList);
        pb.setPc(pc);
        pb.setPs(ps);
        pb.setTr(tr);
        return pb;

    }


    /**
     * 为指定的order加载它的所有条目
     *
     * @param order
     */
    private void loadOrderItem(Order order) throws SQLException {

        String sql = "select * from t_orderitem where oid=?";
        List<Map<String, Object>> maps = qr.query(sql, new MapListHandler(), order.getOid());
        List<OrderItem> orderItemList = toOrderItemList(maps);
        order.setOrderItems(orderItemList);

    }


    /**
     * 把多个map转换成多个Order对象
     *
     * @param maps
     * @return
     */
    private List<OrderItem> toOrderItemList(List<Map<String, Object>> maps) {

        List<OrderItem> orderItemList = new ArrayList<>();
        for (Map<String, Object> map : maps) {
            OrderItem orderItem = toOrderItem(map);
            orderItemList.add(orderItem);
        }
        return orderItemList;

    }

    /**
     * 把一个map转换成一个OrderItem
     *
     * @param map
     * @return
     */
    private OrderItem toOrderItem(Map<String, Object> map) {

        OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
        Book book = CommonUtils.toBean(map, Book.class);
        orderItem.setBook(book);
        return orderItem;

    }


    /**
     * 通过用户查询
     *
     * @param uid
     * @param pc
     * @return
     * @throws SQLException
     */
    public PageBean<Order> findByUser(String uid, int pc) throws SQLException {
        List<Expression> list = new ArrayList<>();
        list.add(new Expression("uid", "=", uid));
        return findByCriteria(list, pc);
    }


    /**
     * 添加订单
     *
     * @param order
     */
    public void add(Order order) throws SQLException {

        //1.插入订单
        String sql = "insert into t_order values(?,?,?,?,?,?)";
        //2.准备参数
        Object[] params = {order.getOid(), order.getOrdertime(), order.getTotal(), order.getStatus(), order.getAddress(), order.getUser().getUid()};
        qr.update(sql, params);
        //3.循环便利所有订单的条目,让每个条目生成一个Object[]数组,多个条目对应Object[][],执行批处理
        sql = "insert into t_orderitem values(?,?,?,?,?,?,?,?)";
        int size = order.getOrderItems().size();  //得到长度
        Object[][] objs = new Object[size][];
        for (int i = 0; i < size; i++) {
            OrderItem item = order.getOrderItems().get(i);
            objs[i] = new Object[]{item.getOrderItemId(), item.getQuantity(), item.getSubtotal(), item.getBook().getBid(), item.getBook().getBname(), item.getBook().getCurrPrice(), item.getBook().getImage_b(), order.getOid()};
        }
        qr.batch(sql, objs);


    }


    /**
     * 加载订单
     *
     * @param oid
     * @return
     * @throws SQLException
     */
    public Order load(String oid) throws SQLException {

        String sql = "select * from t_order where oid=?";
        Order order = qr.query(sql, new BeanHandler<Order>(Order.class), oid);
        loadOrderItem(order);       //为当前的订单加载所有的订单条目
        return order;


    }


    /**
     * 查询订单状态
     *
     * @param oid
     * @return
     */
    public int findStatus(String oid) throws SQLException {

        String sql = "select status from t_order where oid=?";
        Number res = (Number) qr.query(sql, new ScalarHandler(), oid);
        return res.intValue();

    }


    /**
     * 修改订单状态
     *
     * @param oid
     * @param status
     */
    public void updateStatus(String oid, int status) throws SQLException {

        String sql = "update t_order set status=? where oid=?";
        qr.update(sql, status, oid);

    }


    /**
     * 查询所有订单
     *
     * @param pc
     * @return
     * @throws SQLException
     */
    public PageBean<Order> findAll(int pc) throws SQLException {

        List<Expression> expressionList = new ArrayList<>();
        return findByCriteria(expressionList, pc);

    }


    /**
     * 按状态查询订单
     *
     * @param status
     * @param pc
     * @return
     */
    public PageBean<Order> findByStatus(int status, int pc) throws SQLException {

        List<Expression> expressions = new ArrayList<>();
        expressions.add(new Expression("status", "=", status + ""));
        return findByCriteria(expressions, pc);

    }

}
