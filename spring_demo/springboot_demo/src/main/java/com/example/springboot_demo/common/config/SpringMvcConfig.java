package com.example.springboot_demo.common.config;

import com.example.springboot_demo.common.IpInterceptor;
import com.example.springboot_demo.common.LoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Override
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(getIpInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(getLoginInterceptor()).addPathPatterns("/**").
                excludePathPatterns("/login.html", "/Login", "/imgs/**", "/style/**", "/js/**",
                        "/Register", "/register.html");
    }

    @Bean
    public LoginInterceptor getLoginInterceptor() {
        return new LoginInterceptor();
    }

    @Bean
    public IpInterceptor getIpInterceptor(){
        return new IpInterceptor();
    }

}
