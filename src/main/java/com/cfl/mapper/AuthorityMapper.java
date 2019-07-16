package com.cfl.mapper;

import com.cfl.domain.Authority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthorityMapper {
    List<Authority> selectAllAuthorities();
    List<Authority> selectServiceAuthorities(String serviceName);
    List<Authority> selectTenantAuthorities(String serviceName, String tenantId);
    Authority selectAuthority(Authority authority);

    void insertAuthority(Authority authority);
    void updateAuthority(String serviceName, String tenantId, String authorityId, @Param("authority") Authority authority);
    void deleteAuthority(Authority authority);
}
