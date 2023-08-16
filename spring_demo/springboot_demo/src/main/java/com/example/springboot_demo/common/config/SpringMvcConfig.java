package com.example.springboot_demo.common.config;

import com.example.springboot_demo.interceptor.IpInterceptor;
import com.example.springboot_demo.interceptor.LoginInterceptor;
import com.example.springboot_demo.interceptor.TokenFreshInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截器
        registry.addInterceptor(getLoginInterceptor())
                .excludePathPatterns("/login.html", "/Login", "/imgs/**", "/style/**", "/js/**",
                        "/Register", "/register.html"
                );
        //token刷新拦截器
        registry.addInterceptor(getTokenFreshInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
    }


    @Bean
    public TokenFreshInterceptor getTokenFreshInterceptor(StringRedisTemplate stringRedisTemplate) {
        return new TokenFreshInterceptor(stringRedisTemplate);
    }

    @Bean
    public LoginInterceptor getLoginInterceptor(){return new LoginInterceptor();}

    @Bean
    public IpInterceptor getIpInterceptor(){
        return new IpInterceptor();
    }

}
