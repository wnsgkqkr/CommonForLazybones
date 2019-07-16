package com.cfl.mapper;

import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.PathVariable;

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
    List<Map<String, String>> selectObjectIdAndSubObjectIdMapList(String serviceName, String tenantId);
    List<Map<String, Object>> selectObjectIdAndAuthorityMapList(String serviceName, String tenantId);
    List<Map<String, Object>> selectAuthorityIdAndUserMapList(String serviceName, String tenantId);

    boolean isExistAuthorityUserMapping(@Param("authorityId")String authorityId, @Param("user")User user);
    boolean isExistObjectAuthorityMapping(@Param("objectId")String objectId, @Param("authority")Authority authority);
}
