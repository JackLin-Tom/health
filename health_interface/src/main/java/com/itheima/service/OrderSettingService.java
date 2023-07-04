package com.itheima.service;


import com.itheima.pojo.OrderSetting;

import java.util.List;
import java.util.Map;

public interface OrderSettingService {

    //批量导入数据
    void add(List<OrderSetting> data);

    //根据日期修改人数
    void editNumberByDate(OrderSetting orderSetting);

    // 根据日期获取数据
    List<Map> getOrderSettingByMonth(String date);


}
