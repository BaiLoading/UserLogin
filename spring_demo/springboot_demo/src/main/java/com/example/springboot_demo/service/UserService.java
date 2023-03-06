package com.example.springboot_demo.service;

import com.example.springboot_demo.domain.User;

public interface UserService {

    void add(User user);

    boolean isDuplicated(String userName);

    Integer getUserID(String username);

    String getPassWord(String username);

    String getUserName(int userId);
}
