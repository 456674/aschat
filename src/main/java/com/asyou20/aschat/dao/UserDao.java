package com.asyou20.aschat.dao;

import com.asyou20.aschat.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
@Mapper
public interface UserDao {

    @Select("Select * from user")
    List<User> selectAll();

    @Select("select * from user where base64 = #{base64}")
    User getUserByBase64(String base64);

    void insertUser(User user);



}
