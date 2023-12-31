package com.itheima;


import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.pojo.Member;
import com.itheima.service.MemberService;
import com.itheima.utils.SendMailUtil;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

/**
 * 会员登录
 */

@RestController
@RequestMapping("/member")
public class MemberController {

    @Reference
    private MemberService memberService;

    @Autowired
    private JedisPool jedisPool;

    //使用手机号和验证码登录
    @RequestMapping("/login")
    public Result login(HttpServletResponse response, @RequestBody Map map) {

        String telephone = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");
        //从Redis中获取缓存的验证码
        String codeInRedis =
                jedisPool.getResource().get(telephone + RedisMessageConstant.SENDTYPE_LOGIN);
        if (codeInRedis == null || !codeInRedis.equals(validateCode)) {
            //验证码输入错误
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        } else {
            //验证码输入正确
            //判断当前用户是否为会员
            Member member = memberService.findByTelephone(telephone);
            if (member == null) {
                //当前用户不是会员，自动完成注册
                member = new Member();
                member.setPhoneNumber(telephone);
                member.setRegTime(new Date());
                memberService.add(member);
            }

            //登录成功
            //写入Cookie，跟踪用户
            Cookie cookie = new Cookie("login_member_telephone",telephone);
            cookie.setPath("/");//路径
            cookie.setMaxAge(60*60*24*30);//有效期30天
            response.addCookie(cookie);
            //保存会员信息到Redis中
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(telephone,60*30,json);
            return new Result(true,MessageConstant.LOGIN_SUCCESS);
        }
    }


    @RequestMapping("/loginByEmail")
    public Result loginByEmail(HttpServletResponse response, @RequestBody Map map){

        String email = (String) map.get("telephone");
        String validateCode = (String) map.get("validateCode");

        String codeInRedis =
                jedisPool.getResource().get(email + RedisMessageConstant.SENDTYPE_LOGIN);

        if (codeInRedis == null || !codeInRedis.equals(validateCode)) {
            //验证码输入错误
            return new Result(false, MessageConstant.VALIDATECODE_ERROR);
        } else {
            //验证码输入正确
            //判断当前用户是否为会员
            Member member = memberService.findByTelephone(email);
            if (member == null) {
                //当前用户不是会员，自动完成注册
                member = new Member();
                member.setPhoneNumber(email);
                member.setRegTime(new Date());
                memberService.add(member);
            }

            //登录成功
            //写入Cookie，跟踪用户
            Cookie cookie = new Cookie("login_member_email", email);
            cookie.setPath("/");//路径
            cookie.setMaxAge(60 * 60 * 24 * 30);//有效期30天
            response.addCookie(cookie);

            //保存会员信息到Redis中
            String json = JSON.toJSON(member).toString();
            jedisPool.getResource().setex(email, 60 * 30, json);
            return new Result(true, MessageConstant.LOGIN_SUCCESS);
        }
    }

    //邮箱快速登录时发送手机验证码
    @RequestMapping("/sendEmailCodeLogin")
    public Result sendEmailCodeLogin(String email) {

        System.out.println(email);
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

            //将生成的验证码缓存到redis
            jedisPool.getResource().setex(email + RedisMessageConstant.SENDTYPE_LOGIN,
                    60,
                    code.toString());
            //验证码发送成功
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        }
    }


}
