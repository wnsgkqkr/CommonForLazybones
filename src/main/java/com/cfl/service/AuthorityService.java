package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiRequest;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.AuthorityMapper;
import com.cfl.mapper.MappingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AuthorityService implements CflService<Authority>{
    @Autowired
    private AuthorityMapper authorityMapper;
    @Autowired
    private MappingMapper mappingMapper;
    @Autowired
    private CommonService commonService;
    @Autowired
    private HistoryService historyService;

    //Authority insert / update / delete Database and refresh cache
    public Authority createData(ApiRequest requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        authorityMapper.insertAuthority(authority);
        commonService.clearUserAuthorityTenantCache(authority.getServiceName(), authority.getTenantId());
        return authority;
    }
    public Authority modifyData(ApiRequest requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        authorityMapper.updateAuthority(authority);
        commonService.clearUserAuthorityTenantCache(authority.getServiceName(), authority.getTenantId());
        return authority;
    }
    public Authority removeData(ApiRequest requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        authorityMapper.deleteAuthority(authority);
        commonService.clearUserAuthorityTenantCache(authority.getServiceName(), authority.getTenantId());
        return authority;
    }
    //get Authority from cache or Database and put cache
    public Authority getData(ApiRequest requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        Map<String, Authority> authorityMap = getAuthorityMap(authority);
        Authority mapAuthority = authorityMap.get(authority.getAuthorityId());
        if(mapAuthority == null) {
            authority = authorityMapper.selectAuthority(authority);
            Cache.authorityUserCache.get(authority.getServiceName()).get(authority.getTenantId()).put(authority.getAuthorityId(), authority);
            return authority;
        }
        return mapAuthority;
    }
    //get UserList in Authority from cache or Database and put cache
    public List<User> getAuthorityUsers(ApiRequest requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        Map<String, Authority> authorityMap = getAuthorityMap(authority);
        Authority mapAuthority = authorityMap.get(authority.getAuthorityId());
        if(mapAuthority != null){
            return mapAuthority.getAuthorityToUsers();
        }else{
            List<User> userList = mappingMapper.selectAuthorityUsers(authority);
            if(userList == null){
                userList = Collections.emptyList();
            }
            authority.setAuthorityToUsers(userList);
            Cache.authorityUserCache.get(authority.getServiceName()).get(authority.getTenantId()).put(authority.getAuthorityId(), authority);
            return userList;
        }
    }

    //get All authorities in Tenant
    public List<Authority> getTenantAuthorities(ApiRequest requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        List<Authority> authorityList = authorityMapper.selectTenantAuthorities(authority);
        return authorityList;
    }

    //get AuthorityMap in Cache(make cache)
    private Map<String, Authority> getAuthorityMap(Authority authority){
        String serviceName = authority.getServiceName();
        Map<String, Map<String, Authority>> serviceNameMap = Cache.authorityUserCache.get(serviceName);
        if(serviceNameMap == null){
            serviceNameMap = new HashMap<>();
            Cache.authorityUserCache.put(serviceName, serviceNameMap);
        }

        String tenantId = authority.getTenantId();
        Map<String, Authority> TenantIdMap = serviceNameMap.get(tenantId);
        if(TenantIdMap==null){
            TenantIdMap = new HashMap<>();
            Cache.authorityUserCache.get(serviceName).put(tenantId,TenantIdMap);
        }

        return TenantIdMap;
    }
}
