package com.tzx.category.dao;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import com.tzx.category.domain.Category;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.*;
import org.junit.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 分类持久层
 */
public class CategoryDao {
    private QueryRunner qr = new TxQueryRunner();

    /**
     * 返回所有分类
     *
     * @return
     */
    public List<Category> findAll() throws SQLException {
        //1.查询出所有的一级分类
        String sql = "select * from t_category where pid is null order by orderBy";
        List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler());
        List<Category> parents = toCategoryList(mapList);//获取到了所有的一级分类
        //2.循环遍历所有的一级分类,为每个一级分类加载二级分类
        for (Category parent : parents) {
            List<Category> children = findByParent(parent.getCid());
            parent.setChildren(children);//为一级分类添加二级分类
        }
        return parents;
    }


    /**
     * 通过父分类查询子分类
     *
     * @param pid
     * @return
     */
    public List<Category> findByParent(String pid) throws SQLException {
        String sql = "select * from t_category where pid=? order by orderBy";
        List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(), pid);
        List<Category> children = toCategoryList(mapList);
        return children;
    }


    /**
     * 把一个Map中的数据映射到Category中
     *
     * @param map
     * @return
     */
    private Category toCategory(Map<String, Object> map) {
        Category category = CommonUtils.toBean(map, Category.class);
        String pid = (String) map.get("pid");
        if (pid != null) {
            Category parent = new Category();
            parent.setCid(pid);
            category.setParent(parent);
        }
        return category;
    }


    /**
     * 可以把多个Map映射成多个Category
     *
     * @param mapList
     * @return
     */
    private List<Category> toCategoryList(List<Map<String, Object>> mapList) {
        List<Category> categoryList = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            Category category = toCategory(map);
            categoryList.add(category);
        }
        return categoryList;
    }



    /*管理员功能模块*/

    /**
     * 添加一级分类和二级分类
     *
     * @param category
     */
    public void add(Category category) throws SQLException {

        String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)    ";

        //一级分类没有parent,二级分类有
        String pid = null;//一级分类
        if (category.getParent() != null) {     //二级分类
            pid = category.getParent().getCid();
        }
        Object[] params = {category.getCid(), category.getCname(), pid, category.getDesc()};
        qr.update(sql, params);

    }


    /**
     * 查询所有的父分类
     *
     * @return
     */
    public List<Category> findParents() throws SQLException {

        String sql = "select * from t_category where pid is null order by orderBy";
        List<Map<String, Object>> maps = qr.query(sql, new MapListHandler());
        return toCategoryList(maps);

    }


    /**
     * 查询分类名是否存在
     *
     * @param cname
     * @return
     */
    public Category findByCname(String cname) throws SQLException {

        String sql = "select * from t_category where cname=?";
        Category query = qr.query(sql, new BeanHandler<Category>(Category.class), cname);
        return query;

    }


    /**
     * 查询分类名是否存在且不包含本身
     *
     * @param cname
     * @return
     * @throws SQLException
     */
    public Category findByCnameButCid(String cname, String cid) throws SQLException {

        String sql = "select * from t_category where cname=? and cid!=?";
        Category query = qr.query(sql, new BeanHandler<Category>(Category.class), cname, cid);
        return query;

    }


    /**
     * 即可加载一级分类,也可加载二级分类
     *
     * @param cid
     * @return
     */
    public Category load(String cid) throws SQLException {

        String sql = "select * from t_category where cid=?";
        return toCategory(qr.query(sql, new MapHandler(), cid));

    }


    /**
     * 修改分类
     *
     * @param category
     */
    public void edit(Category category) throws SQLException {

        String sql = "update t_category set cname=?,pid=?,`desc`=? where cid=?";
        String pid = null;
        if (category.getParent() != null) {
            pid = category.getParent().getCid();
        }
        Object[] params = {category.getCname(), pid, category.getDesc(), category.getCid()};
        qr.update(sql, params);

    }


    /**
     * 查询指定父分类下的子分类的个数
     *
     * @param pid
     * @return
     */
    public int findCountByParent(String pid) throws SQLException {

        String sql = "select count(*) from t_category where pid=?";
        Number num = (Number) qr.query(sql, new ScalarHandler(), pid);
        return num == null ? 0 : num.intValue();

    }


    /**
     * 删除指定分类
     *
     * @param cid
     */
    public void delete(String cid) throws SQLException {

        String sql = "delete from t_category where cid=?";
        qr.update(sql, cid);

    }
}