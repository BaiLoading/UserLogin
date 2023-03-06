package com.example.springboot_demo.mapper;

import com.example.springboot_demo.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    @Insert("insert into tb_user (username, password) values (#{username}, #{password})")
    void add(User user);

    /**
     * 查询用户名是否已经在数据库中存在
     *
     * @param username 用户名
     * @return 返回数据库中相同用户名的个数，0表示不存在，1表示存在
     */
    @Select("select count(username) from tb_user where username = #{username}")
    int isDuplicated(String username);

    /**
     * 根据用户id查询用户名
     *
     * @param  username
     * @return 用户id
     */
    @Select("select id from tb_user where username = #{username}")
    Integer getUserID(String username);

    @Select("select password from tb_user where username = #{username}")
    String getPassWord(String username);

    @Select("select user_name from user where id = #{userId}")
    String getUserName(int userId);
}
