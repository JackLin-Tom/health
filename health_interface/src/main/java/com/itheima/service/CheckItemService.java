package com.itheima.service;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;

import java.util.List;

/**
 * 检查项服务接口
 */
public interface CheckItemService {

    //删除项目
    void deleteById(Integer id);

    //新增检查项目
    public void add(CheckItem checkItem);

    //查询检查项目
    PageResult pageQuery(QueryPageBean queryPageBean);

    //修改检项目
    void edit(CheckItem checkItem);

    //查找点击修改时的项目信息
    CheckItem findById(Integer id);

    //查询所有的检项
    List<CheckItem> findAll();


    
}
