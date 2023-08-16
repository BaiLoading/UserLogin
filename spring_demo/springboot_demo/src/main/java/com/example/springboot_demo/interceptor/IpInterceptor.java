package com.example.springboot_demo.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

public class IpInterceptor  implements HandlerInterceptor {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        String ip = request.getRemoteAddr();
        //判断当前ip地址是否在白名单中
        Boolean ipWhiteList = redisTemplate.opsForSet().isMember("ipWhiteList", ip);
        if (ipWhiteList) {
            return true;
        }
        String keyName = "ip:" + ip;
        String times = redisTemplate.opsForValue().get(keyName);
        //判断当前ip地址在一分钟内的访问次数
        if (times == null) {
            //该ip地址一分钟内未访问过
            redisTemplate.opsForValue().increment(keyName);
            redisTemplate.expire(keyName,1, TimeUnit.MINUTES);
        } else if (Integer.parseInt(times) < 100) {
            //该ip地址一分钟内访问次数小于100次
            redisTemplate.opsForValue().increment(keyName);
        } else {
            //该ip地址一分钟内访问次数超过100次
            return false;
        }
        return true;
    }
}
