package com.itheima.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.constant.MessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/setmeal")
public class SetmealController {

    @Reference//(check = false)
    SetMealService setMealService;

    //获取所有套餐信息
    @RequestMapping("/getAllSetmeal")
    public Result getAllSetmeal(){
        try{
            List<Setmeal> list = setMealService.findAll();
            return new Result(true,MessageConstant.GET_SETMEAL_LIST_SUCCESS,list);
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false, MessageConstant.GET_SETMEAL_LIST_FAIL);
        }
    }

    // 根据套餐ID查询套餐详情（套餐基本信息、套餐对应的检查组信息、检查组对应的检查项信息)(多表连查)
    @RequestMapping("/")
    public Result findById(int id){
            try{
                Setmeal setmeal = setMealService.findById(id);
                return new Result(true,MessageConstant.QUERY_SETMEAL_SUCCESS,setmeal);
            }catch (Exception e){
                e.printStackTrace();
                return new Result(false,MessageConstant.QUERY_SETMEAL_FAIL);
            }
    }



}
