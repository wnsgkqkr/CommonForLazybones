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
    List<Authority> insertObjectAuthority(CflObject object, Authority authority);
    List<Authority> deleteObjectAuthority(CflObject object, Authority authority);

    List<String> getObjectAuthorityIds(CflObject object);
    List<Authority> getUserAuthorities(User user);
    List<User> getAuthorityUsers(Authority authority);
    Map<String, List<String>> getObjectIdAuthorityIdListMap(String serviceName, String tenantId);
}
