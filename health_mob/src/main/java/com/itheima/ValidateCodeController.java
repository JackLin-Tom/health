package com.itheima;

import com.itheima.constant.MessageConstant;
import com.itheima.constant.RedisMessageConstant;
import com.itheima.entity.Result;
import com.itheima.utils.SendMailUtil;
import com.itheima.utils.ValidateCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.JedisPool;

@RestController
@RequestMapping("/validateCode")
public class ValidateCodeController {

    @Autowired
    JedisPool jedisPool;

//    //体检预约时发送手机验证码
//    @RequestMapping("/send4Order")
//    public Result send4Order(String telephone){
//        Integer code = ValidateCodeUtils.generateValidateCode(4);//生成4位数字验证码
//        try {
//            //发送短信
//            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code.toString());
//        } catch (ClientException e) {
//            e.printStackTrace();
//            //验证码发送失败
//            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
//        }
//        System.out.println("发送的手机验证码为：" + code);
//        //将生成的验证码缓存到redis，保存5分钟
//        jedisPool.getResource().setex(
//                telephone + RedisMessageConstant.SENDTYPE_ORDER,5 * 60,code.toString());
//        //验证码发送成功
//        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
//    }
//
//    //手机快速登录时发送手机验证码
//    @RequestMapping("/send4Login")
//    public Result send4Login(String telephone){
//        Integer code = ValidateCodeUtils.generateValidateCode(6);//生成6位数字验证码
//        try {
//            //发送短信
//            SMSUtils.sendShortMessage(SMSUtils.VALIDATE_CODE,telephone,code.toString());
//        } catch (ClientException e) {
//            e.printStackTrace();
//            //验证码发送失败
//            return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
//        }
//        System.out.println("发送的手机验证码为：" + code);
//        //将生成的验证码缓存到redis
//        jedisPool.getResource().setex(telephone+RedisMessageConstant.SENDTYPE_LOGIN,
//                5 * 60,
//                code.toString());
//        //验证码发送成功
//        return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
//    }


    //体检预约时发送邮箱验证码
    @RequestMapping("/sendEmailCodeOrder")
    public Result sendEmailCodeOrder(String email){

        String codeInRedis =
                jedisPool.getResource().get(email + RedisMessageConstant.SENDTYPE_ORDER);

        //如果缓存中已经存在验证码，不重新发送
        if(codeInRedis != null) {
            return new Result(true,MessageConstant.DONT_SED_REPEATEDLY);
        } else {
            Integer code = ValidateCodeUtils.generateValidateCode(4);//生成4位数字验证码
            try {
                //发送短信
                SendMailUtil.sendEmailCode(email,code);
            } catch (Exception e) {
                e.printStackTrace();
                //验证码发送失败
                return new Result(false, MessageConstant.SEND_VALIDATECODE_FAIL);
            }
            System.out.println("发送的邮箱验证码为：" + code);

            //将生成的验证码缓存到redis，保存1分钟
            jedisPool.getResource().setex(
                    email + RedisMessageConstant.SENDTYPE_ORDER, 60,code.toString());
            //验证码发送成功
            return new Result(true,MessageConstant.SEND_VALIDATECODE_SUCCESS);
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

            //将生成的验证码缓存到redis
            jedisPool.getResource().setex(email + RedisMessageConstant.SENDTYPE_LOGIN,
                    60,
                    code.toString());
            //验证码发送成功
            return new Result(true, MessageConstant.SEND_VALIDATECODE_SUCCESS);
        }
    }




}
