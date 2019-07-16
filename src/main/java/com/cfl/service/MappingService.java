package com.cfl.service;

import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.MappingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MappingService {

    @Autowired
    private MappingMapper mappingMapper;

    public boolean isExistObjectAuthorityMapping(String objectId, Authority authority) {
        return mappingMapper.isExistObjectAuthorityMapping(objectId, authority);
    }

    public void createObjectAuthorityMappting(String objectId, Authority authority) {
        mappingMapper.insertObjectAuthority(objectId, authority);
    }

    public boolean isExistAuthorityUserMapping(String authorityId, User user) {
        return mappingMapper.isExistAuthorityUserMapping(authorityId, user);
    }

    public void createAuthorityUserMappting(String authorityId, User user) {
        mappingMapper.insertAuthorityUser(authorityId, user);
    }
}
