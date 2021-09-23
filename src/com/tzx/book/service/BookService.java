package com.tzx.book.service;

import com.tzx.book.dao.BookDao;
import com.tzx.book.domain.Book;
import com.tzx.pager.PageBean;

import java.sql.SQLException;

public class BookService {

    private BookDao dao = new BookDao();


    /**
     * 按分类查询
     *
     * @param cid
     * @param pc
     * @return
     */
    public PageBean<Book> findByCategory(String cid, int pc) {

        try {
            return dao.findByCategory(cid, pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 按书名模糊查询
     *
     * @param bname
     * @param pc
     * @return
     */
    public PageBean<Book> findByBname(String bname, int pc) {

        try {
            return dao.findByBname(bname, pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 按作者模糊查询
     *
     * @param author
     * @param pc
     * @return
     */
    public PageBean<Book> findByAuthor(String author, int pc) {

        try {
            return dao.findByAuthor(author, pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 按出版社模糊查询
     *
     * @param press
     * @param pc
     * @return
     */
    public PageBean<Book> findByPress(String press, int pc) {

        try {
            return dao.findByPress(press, pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 多条件组合查询
     *
     * @param criteria
     * @param pc
     * @return
     */
    public PageBean<Book> findByCombination(Book criteria, int pc) {

        try {
            return dao.findByCombination(criteria, pc);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 通过书的id查询整本书(加载图书)
     *
     * @param bid
     * @return
     */
    public Book findByBid(String bid) {

        try {
            return dao.findByBid(bid);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 查询指定分类下图书的数量
     *
     * @param cid
     * @return
     */
    public int findBookCountByCategory(String cid) {

        try {
            return dao.findBookCountByCategory(cid);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }


    /**
     * 编辑图书
     *
     * @param book
     */
    public void edit(Book book) {

        try {
            dao.edit(book);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }


    /**
     * 添加图书
     *
     * @param book
     */
    public void add(Book book) {

        try {
            dao.add(book);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    /**
     * 删除图书
     *
     * @param bid
     */
    public void delete(String bid) {

        try {
            dao.delete(bid);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }
}
