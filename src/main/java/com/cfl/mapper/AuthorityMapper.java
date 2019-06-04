package com.cfl.mapper;

import com.cfl.domain.Authority;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AuthorityMapper {
    void insertAuthority(Authority authority);
    void updateAuthority(Authority authority);
    void deleteAuthority(Authority authority);
    Authority getAuthority(Authority authority);

    List<Authority> getAuthorities(Authority authority);

}
