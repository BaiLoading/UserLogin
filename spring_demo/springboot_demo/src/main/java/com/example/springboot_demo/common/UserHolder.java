package com.example.springboot_demo.common;

import com.example.springboot_demo.domain.User;

public class UserHolder {
    private static final ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public static void saveUser(User user){
        threadLocal.set(user);
    }

    public static User getUser(){
        return threadLocal.get();
    }

    public static void removeUser(){
        threadLocal.remove();
    }
}

