package com.itheima.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.constant.RedisConstant;
import com.itheima.dao.SetMealDao;
import com.itheima.entity.PageResult;
import com.itheima.entity.QueryPageBean;
import com.itheima.pojo.Setmeal;
import com.itheima.service.SetMealService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 体检套餐服务实现类
 */
@Service(interfaceClass = SetMealService.class)
@Transactional
public class SetmealServiceImpl implements SetMealService {

    @Autowired
    SetMealDao setMealDao;

    @Autowired
    JedisPool jedisPool;

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Value("${out_put_path}")//从属性文件读取输出目录的路径
    private String outputpath ;

    //新增套餐
    @Override
    public void add(Setmeal setmeal, Integer[] checkgroupIds) {

        setMealDao.add(setmeal);
        if(checkgroupIds != null && checkgroupIds.length > 0){
            //绑定套餐和检查组的多对多关系
            setSetMealAndCheckGroup(setmeal.getId(), checkgroupIds);
        }
        //将图片名称保存到Redis
        savePic2Redis(setmeal.getImg());

        // 当添加套餐后需要重新生成静态页面(套餐列表页面，套餐详情页面 )
        generateMobileStaticHtml();
    }

    @Override
    public PageResult pageQuery(QueryPageBean queryPageBean) {

        Integer currentPage = queryPageBean.getCurrentPage();
        Integer pageSize = queryPageBean.getPageSize();
        String queryString = queryPageBean.getQueryString();

        PageHelper.startPage(currentPage,pageSize);// 分页插件，会在执行sql之前将分页关键字追加到SQL后面
        Page<Setmeal> page = setMealDao.findByCondition(queryString);
        return new PageResult(page.getTotal(),page.getResult());

    }

    //查找所有套餐
    @Override
    public List<Setmeal> findAll() {
        return setMealDao.findAll();
    }


    // 根据套餐ID查询套餐详情（套餐基本信息、套餐对应的检查组信息、检查组对应的检查项信息)(多表连查)
    @Override
    public Setmeal findById(int id) {
        return setMealDao.findById(id);
    }


    // 查询套餐总数
    @Override
    public List<Map<String, Object>> findSetMealCount() {
        return setMealDao.findSetMealCount();
    }

    @Override
    public void deleteById(Integer id) {

        //删除关联关系
        setMealDao.deleteSetMealAndCheckGroup(id);

        //删除表
        setMealDao.deleteById(id);

    }

    //绑定套餐和检查组的多对多关系
    private void setSetMealAndCheckGroup(Integer id, Integer[] checkgroupIds) {
        for (Integer checkgroupId : checkgroupIds) {
            Map<String,Integer> map = new HashMap<>();
            map.put("setmeal_id",id);
            map.put("checkgroup_id",checkgroupId);
            setMealDao.setSetMealAndCheckGroup(map);
        }
    }

    //将图片名称保存到Redis
    private void savePic2Redis(String pic){
        jedisPool.getResource().sadd(RedisConstant.SETMEAL_PIC_DB_RESOURCES,pic);
    }

    //生成当前方法所需的静态页面
    public void generateMobileStaticHtml() {
        // 在生成静态页面之前需要查询数据
        List<Setmeal> list = setMealDao.findAll();

        // 需要生成套餐列表静态页面
        generateMobileSetMealListHtml(list);

        // 需要生成套餐详情静态页面
        generateMobileSetMealDetailHtml(list);

    }

    //生成套餐列表静态页面
    public void generateMobileSetMealListHtml(List<Setmeal> list) {
        Map map = new HashMap<>();
        // 为模板提供数据,用于生成静态页面
        map.put("setmealList", list);
        this.generateHtml("mobile_setmeal.ftl","m_setmeal.html",map);
    }

    //生成套餐详情静态页面（多个）
    public void generateMobileSetMealDetailHtml(List<Setmeal> list) {
        for (Setmeal setmeal : list) {
            Map map = new HashMap();
            map.put("setmeal", setMealDao.findById(setmeal.getId()));
            this.generateHtml("mobile_setmeal_detail.ftl",
                    "setmeal_detail_"+setmeal.getId()+".html",
                    map);
        }
    }

    // 用于生成静态页面
    public void generateHtml(String templateName, String htmlPageName, Map map){
        Configuration configuration = freeMarkerConfigurer.getConfiguration();// 获取配置对象
        Writer out = null;
        try {
            Template template = configuration.getTemplate(templateName);
            // 构造输出流
            out = new FileWriter(new File(outputpath + "/" + htmlPageName));
            // 输出文件
            template.process(map,out);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }



}
