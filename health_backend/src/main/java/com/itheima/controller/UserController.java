package com.itheima.controller;


import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.utils.SendMailUtil;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

/**
 * 用户操作
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    JedisPool jedisPool;

    //获取当前登录用户的用户名
    @RequestMapping("/getUsername")
    public Result getUsername()throws Exception{
        try{
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            return new Result(true, MessageConstant.GET_USERNAME_SUCCESS,user.getUsername());
        }catch (Exception e){
            return new Result(false, MessageConstant.GET_USERNAME_FAIL);
        }
    }

    //邮箱快速登录时发送手机验证码
    @RequestMapping("/sendEmailCodeLogin")
    public Result sendEmailCodeLogin(String email) {

        String codeInRedis =
                jedisPool.getResource().get(email + RedisMessageConstant.SENDTYPE_LOGIN);

        if (codeInRedis != null) {
            return new Result(true, MessageConstant.DONT_SED_REPEATEDLY);
        } else {

            Integer code = ValidateCodeUtils.generateValidateCode(6);//生成6位数字验证码

            try {
                //发送短信
                SendMailUtil.sendEmailCode(email, code);
            } catch (Exception e) {
                e.printStackTrace();
                //验证码发送失败
                return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
            }

            System.out.println("发送的邮箱验证码为：" + code);

            //将生成的验证码缓存到redis保存一分钟
            jedisPool.getResource().setex(email + RedisMessageConstant.SENDTYPE_LOGIN,
                    60,
                    code.toString());
            //验证码发送成功
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        }
    }


}
