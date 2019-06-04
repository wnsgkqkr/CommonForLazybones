package com.cfl.mapper;

import com.cfl.domain.Authority;
import com.cfl.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MappingMapper {
    void insertUserAuthority(User user, Authority authority);
    void deleteUserAuthority(User user, Authority authority);
}
