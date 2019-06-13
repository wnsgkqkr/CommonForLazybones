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
    void insertAuthorityUser(@Param("authorityId")String authorityId, @Param("user")User user);
    void deleteAuthorityUser(@Param("authorityId")String authorityId, @Param("user")User user);
    void insertObjectAuthority(@Param("objectId")String objectId, @Param("authority")Authority authority);
    void deleteObjectAuthority(@Param("objectId")String objectId, @Param("authority")Authority authority);

    List<Authority> selectObjectAuthorities(CflObject object);
    List<Authority> selectUserAuthorities(User user);
    List<User> selectAuthorityUsers(Authority authority);
    Map<String, List<Authority>> selectObjectIdAuthoritiesMap(String serviceName, String tenantId);
    Map<String, List<String>> selectObjectIdSubObjectIdListMap(String serviceName, String tenantId);
}
