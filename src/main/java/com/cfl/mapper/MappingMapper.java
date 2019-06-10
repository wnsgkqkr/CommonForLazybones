package com.cfl.mapper;

import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface MappingMapper {
    void insertUserAuthority(@Param("user")User user, @Param("authority")Authority authority);
    void deleteUserAuthority(@Param("user")User user, @Param("authority")Authority authority);
    void insertObjectAuthority(@Param("object")CflObject object, @Param("authority")Authority authority);
    void deleteObjectAuthority(@Param("object")CflObject object, @Param("authority")Authority authority);

    List<String> selectObjectAuthorityIds(CflObject object);
    List<Authority> selectUserAuthorities(User user);
    List<User> selectAuthorityUsers(Authority authority);
    Map<String, List<String>> selectObjectIdAuthorityIdListMap(String serviceName, String tenantId);
    Map<String, List<String>> selectObjectIdSubObjectIdListMap(String serviceName, String tenantId);
}
