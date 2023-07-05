package com.itheima.service;


import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;

import java.util.List;

public interface CheckGroupService {

    //新增检查组
    void add(CheckGroup checkGroup, Integer[] checkitemIds);

    //分页
    PageResult pageQuery(QueryPageBean queryPageBean);

    //id查询
    CheckGroup findById(Integer id);

    //查询检擦
    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    //编辑检查组
    void edit(CheckGroup checkGroup, Integer[] checkItemIds);

    //查询所有的检查组
    List<CheckGroup> findAll();


}
