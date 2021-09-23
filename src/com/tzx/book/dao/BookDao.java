package com.tzx.book.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import com.tzx.book.domain.Book;
import com.tzx.category.domain.Category;
import com.tzx.pager.Expression;
import com.tzx.pager.PageBean;
import com.tzx.pager.PageConstants;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 书模块持久层
 */
public class BookDao {

    private QueryRunner qr = new TxQueryRunner();

    /**
     * 按分类查询
     *
     * @param cid 所属分类的id
     * @param pc
     * @return
     */
    public PageBean<Book> findByCategory(String cid, int pc) throws SQLException {

        List<Expression> expressions = new ArrayList<>();
        expressions.add(new Expression("cid", "=", cid));
        return findByCriteria(expressions, pc);

    }

    /**
     * 按书名模糊查询
     *
     * @param bname
     * @param pc
     * @return
     * @throws SQLException
     */
    public PageBean<Book> findByBname(String bname, int pc) throws SQLException {

        List<Expression> expressions = new ArrayList<>();
        expressions.add(new Expression("bname", "like", "%" + bname + "%"));
        return findByCriteria(expressions, pc);

    }


    /**
     * 按作者模糊查询
     *
     * @param author
     * @param pc
     * @return
     * @throws SQLException
     */
    public PageBean<Book> findByAuthor(String author, int pc) throws SQLException {

        List<Expression> expressions = new ArrayList<>();
        expressions.add(new Expression("author", "like", "%" + author + "%"));
        return findByCriteria(expressions, pc);

    }


    /**
     * 按出版社模糊查询
     *
     * @param press
     * @param pc
     * @return
     * @throws SQLException
     */
    public PageBean<Book> findByPress(String press, int pc) throws SQLException {

        List<Expression> expressions = new ArrayList<>();
        expressions.add(new Expression("press", "like", "%" + press + "%"));
        return findByCriteria(expressions, pc);

    }


    /**
     * 多条件组合查询
     *
     * @param criteria
     * @param pc
     * @return
     */
    public PageBean<Book> findByCombination(Book criteria, int pc) throws SQLException {

        List<Expression> expressions = new ArrayList<>();
        expressions.add(new Expression("bname", "like", "%" + criteria.getBname() + "%"));
        expressions.add(new Expression("author", "like", "%" + criteria.getAuthor() + "%"));
        expressions.add(new Expression("press", "like", "%" + criteria.getPress() + "%"));
        return findByCriteria(expressions, pc);

    }


    /**
     * 通用的查询方法
     *
     * @param expressionList 查询条件封装的bean
     * @param pc             当前页数
     * @return
     */
    private PageBean<Book> findByCriteria(List<Expression> expressionList, int pc) throws SQLException {

        //1.得到ps:每页的记录数
        int ps = PageConstants.BOOK_PAGE_SIZE;  //每页的记录数
        //2.得到tr:总记录条数
        StringBuilder whereSql = new StringBuilder(" where 1=1 ");
        List<Object> params = new ArrayList<>();  //sql中对应?的值
        for (Expression expression : expressionList) {
            whereSql.append("and ").append(expression.getName() + " ").append(expression.getOperator() + " ");
            if (!expression.getOperator().equals("is null")) {
                whereSql.append("? ");
                params.add(expression.getValue());  //给参数数组中添加此时的参数,使用?作为占位符
            }
        }
        String sql = "select count(*) from t_book" + whereSql;
        Number num = (Number) qr.query(sql, new ScalarHandler(), params.toArray());
        int tr = num.intValue();      //得到了总记录数
        //3.得到beanList:当前页数据
        sql = "select * from t_book" + whereSql + " order by orderBy limit ?,?";
        params.add((pc - 1) * ps);  //当前页首行的记录下标
        params.add(ps);         //每页查询记录数
        List<Book> bookList = qr.query(sql, new BeanListHandler<Book>(Book.class), params.toArray());
        //4.创建PageBean,设置参数,返回
        PageBean<Book> pageBean = new PageBean<>();
        pageBean.setPs(ps);
        pageBean.setPc(pc);
        pageBean.setTr(tr);
        pageBean.setBeanList(bookList);
        //url由servlet完成赋值
        return pageBean;
    }


    /**
     * 通过书的id查询整本书的内容
     *
     * @param bid
     * @return
     */
    public Book findByBid(String bid) throws SQLException {

        String sql = "select * from t_book b,t_category c where b.cid=c.cid and b.bid=?";
        Map<String, Object> map = qr.query(sql, new MapHandler(), bid);
        //将查询的结果封装到book对象中
        Book book = CommonUtils.toBean(map, Book.class);
        //将book的cid谁能够封装到分类对象中
        Category category = CommonUtils.toBean(map, Category.class);
        book.setCategory(category);
        if (map.get("pid") != null) {
            Category parent = new Category();
            parent.setCid((String) map.get("pid"));
            category.setParent(parent);
        }
        return book;

    }


    /**
     * 通过分类查找图书是否存在
     *
     * @param cid
     * @return
     */
    public int findBookCountByCategory(String cid) throws SQLException {

        String sql = "select count(*) from t_book where cid=?";
        Number cnt = (Number) qr.query(sql, new ScalarHandler(), cid);
        return cnt == null ? 0 : cnt.intValue();


    }


    /**
     * 编辑图书
     *
     * @param book
     */
    public void edit(Book book) throws SQLException {

        String sql = "update t_book set bname=?,author=?,price=?,currPrice=?,discount=?,press=?,publishtime=?,edition=?,pageNum=?,wordNum=?,printtime=?,booksize=?,paper=?,cid=? where bid=? ";
        Object[] params = {book.getBname(), book.getAuthor(), book.getPrice(), book.getCurrPrice(), book.getDiscount(), book.getPress(), book.getPublishtime(), book.getEdition(), book.getPageNum(), book.getWordNum(), book.getPrinttime(), book.getBooksize(), book.getPaper(), book.getCategory().getCid(), book.getBid()};
        qr.update(sql, params);

    }


    /**
     * 添加图书
     *
     * @param book
     */
    public void add(Book book) throws SQLException {

        String sql = "insert into t_book(bid,bname,author,price,currPrice,discount,press,publishtime,edition,pageNum,wordNum,printtime,booksize,paper,cid,image_w,image_b) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

        Object[] params = {book.getBid(), book.getBname(), book.getAuthor(), book.getPrice(), book.getCurrPrice(), book.getDiscount(), book.getPress(), book.getPublishtime(), book.getEdition(), book.getPageNum(), book.getWordNum(), book.getPrinttime(), book.getBooksize(), book.getPaper(), book.getCategory().getCid(), book.getImage_w(), book.getImage_b()};

        qr.update(sql, params);

    }


    /**
     * 删除图书
     *
     * @param bid
     */
    public void delete(String bid) throws SQLException {

        String sql = "delete from t_book where bid=?";
        qr.update(sql, bid);

    }
}
