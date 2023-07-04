package com.itheima.dao;

import com.itheima.pojo.Order;

import java.util.List;
import java.util.Map;

public interface OrderDao {


    void add(Order order);

    List<Order> findByCondition(Order order);


    Map findById4Detail(Integer id);


    List<Map> findHotSetmeal();


    Integer findOrderCountByDate(String today);


    Integer findOrderCountAfterDate(String thisWeekMonday);

    Integer findVisitsCountByDate(String today);

    Integer findVisitsCountAfterDate(String thisWeekMonday);


}
