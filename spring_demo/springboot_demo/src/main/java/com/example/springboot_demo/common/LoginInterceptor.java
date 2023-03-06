package com.example.springboot_demo.common;

import com.example.springboot_demo.common.utils.MyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.concurrent.TimeUnit;

@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        String sessionAttribute = (String) session.getAttribute("LoginStatus");
        LocalDate date = LocalDate.now();
        String uv = date + ":uv";
        //判断用户是否是未登录但是有session的临时用户
        boolean isTmp = false;
        if (sessionAttribute != null) {
            String userId = redisTemplate.opsForValue().get(sessionAttribute);
            if (userId != null) {
                String onlineSession = redisTemplate.opsForValue().get("userId:" + userId);
                //当前用户不在线或在线的就是当前的session
                if (onlineSession == null || sessionAttribute.equals(onlineSession)) {
                    session.setMaxInactiveInterval(24 * 60 * 60);
                    //重置redis的过期时间
                    redisTemplate.expire(sessionAttribute, 1, TimeUnit.DAYS);
                    //将用户的登陆状态改为在线，1分钟后自动离线
                    redisTemplate.expire(userId, 1, TimeUnit.MINUTES);
                    String uri = request.getRequestURI();
                    if ("/FromZerotoExpert/index.html".equals(uri)) {
                        //PV+1
                        String pv = date + ":pv";
                        redisTemplate.opsForValue().increment(pv);
                        //维护当日登陆的用户set
                        redisTemplate.opsForHyperLogLog().add(uv, userId);
//                        redisTemplate.opsForSet().add(uv, userId);
                        //维护当日登陆的ip
                        String ip = date + ":ip";
                        String userIp = request.getRemoteAddr();
                        redisTemplate.opsForHyperLogLog().add(ip, userIp);
//                        redisTemplate.opsForSet().add(ip, userIp);
                    }
                    return true;
                }
            } else {
                //没有登陆但是并非第一次访问的用户
                isTmp = true;
            }
        }
        if (!isTmp) {
            //是没有登陆且第一次访问的临时用户，将生成的临时session进行存储
            String tmpSession = MyUtils.getGUID();
            session.setAttribute("LoginStatus", tmpSession);
            redisTemplate.opsForHyperLogLog().add(uv, tmpSession);
        }
        try {
            //TODO: 上线前更改重定向url
            response.sendRedirect("http://localhost:8080/FromZerotoExpert/login.html");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
