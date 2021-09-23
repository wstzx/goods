package com.tzx.category.service;

import com.tzx.category.dao.CategoryDao;
import com.tzx.category.domain.Category;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * 分类模块业务层
 */
public class CategoryService {
    private CategoryDao dao = new CategoryDao();


    /**
     * 查找所有的分类
     *
     * @return
     */
    public List<Category> findAll() {

        try {
            return dao.findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }



    /*管理员模块*/

    /**
     * 添加一级分类和二级分类
     *
     * @param category
     */
    public void add(Category category) {

        try {
            dao.add(category);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }


    /**
     * 查询所有的父分类
     *
     * @return
     */
    public List<Category> findParents() {

        List<Category> parents = null;
        try {
            parents = dao.findParents();
            return parents;
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }

    /**
     * 通过分类名查找是否存在
     *
     * @param cname
     * @return
     */
    public Category findByCname(String cname) {

        try {
            return dao.findByCname(cname);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }

    /**
     * 查询分类名是否存在且不包含本身
     *
     * @param cname
     * @return
     * @throws SQLException
     */
    public Category findByCnameButCid(String cname, String cid) {

        try {
            return dao.findByCnameButCid(cname, cid);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }


    /**
     * 即可加载一级分类,也可加载二级分类
     *
     * @param cid
     * @return
     */
    public Category load(String cid) {

        try {
            return dao.load(cid);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }


    /**
     * 修改分类
     *
     * @param category
     */
    public void edit(Category category) {

        try {
            dao.edit(category);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }


    /**
     * 查找指定父分类下的子分类个数
     *
     * @param pid
     * @return
     */
    public int findCountByParent(String pid) {

        try {
            return dao.findCountByParent(pid);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }


    /**
     * 删除指定分类
     *
     * @param cid
     */
    public void delete(String cid) {
        try {
            dao.delete(cid);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }
    }


    /**
     * 查询指定父分类下的二级分类
     *
     * @param pid
     * @return
     */
    public List<Category> findByParent(String pid) {

        try {
            return dao.findByParent(pid);
        } catch (SQLException throwables) {
            throw new RuntimeException(throwables);
        }

    }
}
