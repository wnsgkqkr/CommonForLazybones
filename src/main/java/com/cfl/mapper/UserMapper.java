package com.cfl.mapper;

import com.cfl.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void insertUser(User user);
    User getUser(User user);
}
