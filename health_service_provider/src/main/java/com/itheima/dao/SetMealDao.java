package com.itheima.dao;


import com.github.pagehelper.Page;
import com.itheima.pojo.Setmeal;

import java.util.List;
import java.util.Map;

public interface SetMealDao {

    void add(Setmeal setmeal);

    Page<Setmeal> findByCondition(String queryString);

    //套餐与检擦组关联关系
    void setSetMealAndCheckGroup(Map<String, Integer> map);

    List<Setmeal> findAll();

    Setmeal findById(int id);

    List<Map<String, Object>> findSetMealCount();


}
