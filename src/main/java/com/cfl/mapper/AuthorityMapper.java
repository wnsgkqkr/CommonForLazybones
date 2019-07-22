package com.cfl.mapper;

import com.cfl.domain.Authority;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AuthorityMapper {
    List<Authority> selectAllAuthorities();
    List<Authority> selectServiceAuthorities(@Param("serviceName") String serviceName);
    List<Authority> selectTenantAuthorities(@Param("serviceName") String serviceName, @Param("tenantId") String tenantId);
    Authority selectAuthority(@Param("authority") Authority authority);

    void insertAuthority(@Param("authority") Authority authority);
    void updateAuthority(@Param("authority") Authority authority);
    void deleteAuthority(@Param("authority") Authority authority);
}
