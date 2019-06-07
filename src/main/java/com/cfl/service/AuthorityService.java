package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.AuthorityMapper;
import com.cfl.mapper.MappingMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AuthorityService implements CflService<Authority>{
    @Autowired
    private AuthorityMapper authorityMapper;
    @Autowired
    private MappingMapper mappingMapper;
    @Autowired
    private CommonService commonService;

    //Authority insert / update / delete Database and refresh cache
    public Authority createData(JSONObject requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        authorityMapper.insertAuthority(authority);
        commonService.clearUserAuthorityTenantCache(authority.getServiceName(), authority.getTenantId());
        return authority;
    }
    public Authority modifyData(JSONObject requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        authorityMapper.updateAuthority(authority);
        commonService.clearUserAuthorityTenantCache(authority.getServiceName(), authority.getTenantId());
        return authority;
    }
    public Authority removeData(JSONObject requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        authorityMapper.deleteAuthority(authority);
        commonService.clearUserAuthorityTenantCache(authority.getServiceName(), authority.getTenantId());
        return authority;
    }
    //get Authority from cache or Database and put cache
    public Authority getData(JSONObject requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        Map<String, Authority> authorityMap = getAuthorityMap(authority);
        authority = authorityMap.get(authority.getAuthorityId());
        if(authority == null) {
            authority = authorityMapper.selectAuthority(authority);
            Cache.authorityUserCache.get(authority.getServiceName()).get(authority.getTenantId()).put(authority.getAuthorityId(), authority);
        }
        return authority;
    }
    //get UserList in Authority from cache or Database and put cache
    public List<User> getAuthorityUsers(JSONObject requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        Map<String, Authority> authorityMap = getAuthorityMap(authority);
        authority = authorityMap.get(authority.getAuthorityId());
        if(authority != null){
            return authority.getAuthorityToUsers();
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
    public List<Authority> getTenantAuthorities(JSONObject requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        List<Authority> authorityList = authorityMapper.selectTenantAuthorities(authority);
        return authorityList;
    }

    //get AuthorityMap in Cache(make cache)
    public Map<String, Authority> getAuthorityMap(Authority authority){
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
            serviceNameMap.put(tenantId,TenantIdMap);
        }

        return TenantIdMap;
    }
}
