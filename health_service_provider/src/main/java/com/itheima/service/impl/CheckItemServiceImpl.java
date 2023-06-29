package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.dao.CheckItemDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.CheckItem;
import com.itheima.service.CheckItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service(interfaceClass = CheckItemService.class)
@Transactional
public class CheckItemServiceImpl implements CheckItemService {

    @Autowired
    private CheckItemDao checkItemDao;

    @Override
    public void deleteById(Integer id) {
        //查询当前检查项目是否和检擦组有关联
        long count = checkItemDao.findCountByCheckItemId(id);
        if(count > 0){
            //当前检查项被引用，不能删除
            throw new RuntimeException("当前检查项被引用，不能删除");
        }
        checkItemDao.deleteById(id);
    }

    @Override
    public void add(CheckItem checkItem) {
        checkItemDao.add(checkItem);
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
        Page<CheckItem> page = checkItemDao.selectByCondition(queryString);
        // 获取总记录数
        long total = page.getTotal();
        // 当前页结果
        List<CheckItem> rows = page.getResult();
        
        return new PageResult(total,rows);
    }

    //修改检擦项
    @Override
    public void edit(CheckItem checkItem) {
        checkItemDao.edit(checkItem);
    }

    @Override
    public CheckItem findById(Integer id) {
        CheckItem byId = checkItemDao.findById(id);
        return byId;
    }

    @Override
    public List<CheckItem> findAll() {
        return checkItemDao.findAll();
    }


}
