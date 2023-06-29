package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckGroupDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckGroup;
import com.itheima.service.CheckGroupService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(interfaceClass = CheckGroupService.class)
@Transactional
public class CheckGroupServiceImpl implements CheckGroupService {

    @Autowired
    CheckGroupDao checkGroupDao;

    @Override
    public void add(CheckGroup checkGroup, Integer[] checkItemIds) {
        // 新增检查组，操作t_checkgroup表
        checkGroupDao.add(checkGroup);
        // 设置检查组和检查项的多对多的关联关系，操作t_checkgroup_checkitem表
        setCheckGroupAndCheckItem(checkGroup.getId(),checkItemIds);
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {
        //页码
        Integer currentPage = queryPageBean.getCurrentPage();
        //每页记录数
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        // 完成分页查询,基于mybatis框架提供的分页助手插件完成,限制每页10条
        // select * from t_checkitem limit 0,10
        PageHelper.startPage(currentPage,pageSize);
        //查询
        Page<CheckGroup> page = checkGroupDao.selectByCondition(queryString);
        // 获取总记录数
        long total = page.getTotal();
        // 当前页结果
        List<CheckGroup> rows = page.getResult();

        return new PageResult(total,rows);

    }

    @Override
    public CheckGroup findById(Integer id) {
        return checkGroupDao.findById(id);
    }

    @Override
    public List<Integer> findCheckItemIdsByCheckGroupId(Integer id) {
        return checkGroupDao.findCheckItemIdsByCheckGroupId(id);
    }

    // 编辑检查组，同时需要更新和检查项的关联关系
    @Override
    public void edit(CheckGroup checkGroup, Integer[] checkItemIds) {
        //根据检查组id删除中间表数据（清理原有关联关系）
        checkGroupDao.deleteAssociation(checkGroup.getId());
        //向中间表插入数据（建立检查组和检查项关联关系）
        setCheckGroupAndCheckItem(checkGroup.getId(),checkItemIds);
        //更新检查组基本信息
        checkGroupDao.edit(checkGroup);
    }

    @Override
    public List<CheckGroup> findAll() {
        return checkGroupDao.findAll();
    }


    public void setCheckGroupAndCheckItem(Integer checkGroupId, Integer[] checkItemIds){
        if(checkGroupId != null && checkItemIds.length > 0){
            for(Integer checkItemId : checkItemIds){
                Map<String,Integer> map = new HashMap<>();
                map.put("checkgroup_id",checkGroupId);
                map.put("checkitem_id",checkItemId);
                checkGroupDao.setCheckGroupAndCheckItem(map);
            }
        }
    }


}
