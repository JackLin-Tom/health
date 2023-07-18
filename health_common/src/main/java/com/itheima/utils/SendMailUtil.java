package com.itheima.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * 发送QQ邮箱工具类
 */
public class SendMailUtil {

    /**
     * 发送邮件代码
     *
     * @param targetEmail 目标用户邮箱
     * @param authCode    发送的验证码
     */
    public static void sendEmailCode(String targetEmail, Integer authCode) {
        try {
            // 创建邮箱对象
            SimpleEmail mail = new SimpleEmail();
            // 设置发送邮件的服务器
            mail.setHostName("smtp.qq.com");
            // "你的邮箱号"+ "上文开启SMTP获得的授权码"
            mail.setAuthentication("2253805775@qq.com", "yiilhmimcikrebbe");
            // 发送邮件 "你的邮箱号"+"发送时用的昵称"
            mail.setFrom("2253805775@qq.com", "test01");

//提示端口问题时配置
//            // 发送服务端口
//            mail.setSslSmtpPort(String.valueOf(465));
//            // 使用安全链接
//            mail.setSSLOnConnect(true);
//            System.setProperty("mail.smtp.ssl.enable", "true");
//            System.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");

            // 使用安全链接
            mail.setSSLOnConnect(true);
            // 接收用户的邮箱
            mail.addTo(targetEmail);
            // 邮件的主题(标题)
            mail.setSubject("注册验证码");
            // 邮件的内容
            mail.setMsg("您的验证码为:" + authCode+"一分钟内有效");
            // 发送
            mail.send();
        } catch (EmailException e) {
            e.printStackTrace();
        }
    }

}
