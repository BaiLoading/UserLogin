package com.example.springboot_demo.service.serviceImpl;

import com.example.springboot_demo.domain.User;
import com.example.springboot_demo.mapper.UserMapper;
import com.example.springboot_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public void add(User user){
        userMapper.add(user);
    }

    @Override
    public Integer getUserID(String username) {
        return userMapper.getUserID(username);
    }

    @Override
    public boolean isDuplicated(String userName){
        if(userMapper.isDuplicated(userName)==0)
        {
            return false;
        }
        else {
            return true;
        }
    }
    @Override
    public String getPassWord(String username){
        return userMapper.getPassWord(username);
    }

    @Override
    public String getUserName(int userId){ return userMapper.getUserName(userId);};
}
