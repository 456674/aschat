package com.asyou20.aschat.dao;

import com.asyou20.aschat.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Mapper
public interface UserDao {

    @Select("Select * from user")
    List<User> selectAll();

    @Select("select * from user where base64 = #{base64}")
    User getUserByBase64(String base64);
    @Select("select * from user where id = #{id}")
    User getUserById(int id);
    @Select("select * from user where username = #{username}")
    User getUserByUsername(String username);

    @Insert("insert into user (username, password, base64) " +
            "values (" +
            "#{user.username}," +
            "#{user.password}," +
            "#{user.base64})")
    @Options(useGeneratedKeys = true,keyProperty = "id",keyColumn = "id")//数据库事务要commit
    int insertUser(@Param("user") User user);




}
