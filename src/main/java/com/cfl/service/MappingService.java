package com.cfl.service;

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

    public void createAuthorityUserMappting(String authorityId, User user) {
        mappingMapper.insertAuthorityUser(authorityId, user);
    }

    public boolean isExistAuthorityUserMapping(String authorityId, User user) {
        return mappingMapper.isExistAuthorityUserMapping(authorityId, user);
    }
}
