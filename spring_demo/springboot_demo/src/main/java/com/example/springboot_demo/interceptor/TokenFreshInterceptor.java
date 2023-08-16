package com.example.springboot_demo.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.example.springboot_demo.common.UserHolder;
import com.example.springboot_demo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

import static com.example.springboot_demo.constants.RedisConstants.LOGIN_USER_KEY;
import static com.example.springboot_demo.constants.RedisConstants.LOGIN_USER_TTL;

@Component
public class TokenFreshInterceptor implements HandlerInterceptor {

    public TokenFreshInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            return false;
        }
        String key = LOGIN_USER_KEY + token;
        User user = JSON.parseObject(stringRedisTemplate.opsForValue().get(key), User.class);
        if (user == null) {
            return false;
        }
        // 保存用户信息到 ThreadLocal
        UserHolder.saveUser(user);
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;

    }
}
