package com.cfl.mapper;

import com.cfl.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    void insertUser(@Param("user") User user);
    void updateUser(@Param("user") User user);
    void deleteUser(@Param("user") User user);
    User selectUser(@Param("user") User user);
}
