package com.cfl.mapper;

import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface MappingMapper {
    void insertUserAuthority(User user, Authority authority);
    void deleteUserAuthority(User user, Authority authority);
    void insertObjectAuthority(CflObject object, Authority authority);
    void deleteObjectAuthority(CflObject object, Authority authority);

    List<String> selectObjectAuthorityIds(CflObject object);
    List<Authority> selectUserAuthorities(User user);
    List<User> selectAuthorityUsers(Authority authority);
    Map<String, List<String>> selectObjectIdAuthorityIdListMap(String serviceName, String tenantId);
    Map<String, List<String>> selectObjectIdSubObjectIdListMap(String serviceName, String tenantId);
}
