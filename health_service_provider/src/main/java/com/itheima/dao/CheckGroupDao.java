package com.itheima.dao;


import com.github.pagehelper.Page;
import com.itheima.pojo.CheckGroup;

import java.util.List;
import java.util.Map;

public interface CheckGroupDao {

    // 插入检查组
    void add(CheckGroup checkGroup);

    // 插入检查组与检查项多对多关联
    void setCheckGroupAndCheckItem(Map<String, Integer> map);

    Page<CheckGroup> selectByCondition(String queryString);

    CheckGroup findById(Integer id);

    List<Integer> findCheckItemIdsByCheckGroupId(Integer id);

    //根据检查组id删除中间表数据（清理原有关联关系）
    void deleteAssociation(Integer id);

    //修改检查组
    void edit(CheckGroup checkGroup);

    //查询所有检查组
    List<CheckGroup> findAll();

    CheckGroup findCheckGroupById(Integer id);


}
