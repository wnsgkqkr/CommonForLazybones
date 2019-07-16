package com.cfl.mapper;

import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MappingMapper {
    List<Map<String, String>> selectObjectIdAndSubObjectIdMapList(String serviceName, String tenantId);
    List<Map<String, Object>> selectObjectIdAndAuthorityMapList(String serviceName, String tenantId);
    List<Map<String, Object>> selectAuthorityIdAndUserMapList(String serviceName, String tenantId);

    boolean isExistObjectAuthorityMapping(String objectId, Authority authority);
    void insertObjectAuthority(String objectId, Authority authority);
    void deleteObjectAuthority(String objectId, Authority authority);
    void deleteObjectMapping(CflObject object);

    boolean isExistAuthorityUserMapping(String authorityId, User user);
    void insertAuthorityUser(String authorityId, User user);
    void deleteAuthorityUser(String authorityId, User user);
    void deleteAuthorityMapping(Authority authority);

    List<Authority> selectObjectAuthorities(CflObject object);
    List<User> selectAuthorityUsers(Authority authority);
    List<Authority> selectUserAuthorities(User user);
}
