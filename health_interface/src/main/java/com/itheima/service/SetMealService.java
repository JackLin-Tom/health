package com.itheima.service;

import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;
import java.util.List;
import java.util.Map;


public interface SetMealService {

    void add(Setmeal setmeal, Integer[] checkgroupIds);

    //分页查询
    PageResult pageQuery(QueryPageBean queryPageBean);

    List<Setmeal> findAll();

    // 根据套餐ID查询套餐详情（套餐基本信息、套餐对应的检查组信息、检查组对应的检查项信息)(多表连查)
    Setmeal findById(int id);

    List<Map<String, Object>> findSetMealCount();


}
