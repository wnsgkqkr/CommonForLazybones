package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.*;
import com.cfl.mapper.AuthorityMapper;
import com.cfl.mapper.MappingMapper;
import com.cfl.mapper.UserMapper;
import com.cfl.util.CommonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class AuthorityService{
    @Autowired
    private AuthorityMapper authorityMapper;
    @Autowired
    private MappingMapper mappingMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private HistoryService historyService;

    private static final String DEFAULT_TENANT_ID = "default";

    public ApiResponse createAuthority(String serviceName, String tenantId, String authorityId, Authority authority){
        try{
            authority = setAuthority(serviceName, tenantId, authorityId, authority);
            authorityMapper.insertAuthority(authority);
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = CommonUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e){
            return CommonUtil.getFailureApiResponse();
        }
    }
    public ApiResponse modifyAuthority(String serviceName, String tenantId, String authorityId, Authority authority){
        try {
            authority = setAuthority(serviceName, tenantId, authorityId, authority);
            authorityMapper.updateAuthority(authority);
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = CommonUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e){
            return CommonUtil.getFailureApiResponse();
        }
    }
    public ApiResponse removeAuthority(String serviceName, String tenantId, String authorityId){
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            authorityMapper.deleteAuthority(authority);
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = CommonUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e){
            return CommonUtil.getFailureApiResponse();
        }
    }

    //get Authority from cache or Database and put cache
    public ApiResponse getAuthority(String serviceName, String tenantId, String authorityId){
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            Map<String, Authority> authorityMap = getAuthorityMapFromCache(authority);
            Authority mapAuthority = authorityMap.get(authorityId);

            if(mapAuthority == null) {
                authority = authorityMapper.selectAuthority(authority);
                Cache.authorityUserCache.get(serviceName).get(authority.getTenantId()).put(authorityId, authority);
                return CommonUtil.getSuccessApiResponse(authority);
            } else {
                return CommonUtil.getSuccessApiResponse(mapAuthority);
            }
        } catch (Exception e){
            return CommonUtil.getFailureApiResponse();
        }
    }

    public ApiResponse createAuthorityUserMapping(String serviceName, String tenantId, String authorityId, List<User> requestUsers){
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            for (User requestUser : requestUsers) {
                requestUser.setServiceName(serviceName);
                requestUser.setTenantId(authority.getTenantId());
                userMapper.insertUser(requestUser);
                mappingMapper.insertAuthorityUser(requestUser, authorityId);
            }
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = CommonUtil.getSuccessApiResponse(requestUsers);
            historyService.createHistory(serviceName, authority.getTenantId(), requestUsers, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e){
            return CommonUtil.getFailureApiResponse();
        }
    }
    public ApiResponse removeAuthorityUserMapping(String serviceName, String tenantId, String authorityId, List<User> requestUsers){
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            for (User requestUser : requestUsers) {
                requestUser.setServiceName(serviceName);
                requestUser.setTenantId(authority.getTenantId());
                mappingMapper.deleteAuthorityUser(requestUser, authorityId);
            }
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = CommonUtil.getSuccessApiResponse(requestUsers);
            historyService.createHistory(serviceName, authority.getTenantId(), requestUsers, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e){
            return CommonUtil.getFailureApiResponse();
        }
    }

    //get UserList in Authority from cache, if it doesn't exist in Cache then get Database and put cache
    public ApiResponse getAuthorityUserMapping(String serviceName, String tenantId, String authorityId){
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            Map<String, Authority> authorityMap = getAuthorityMapFromCache(authority);
            Authority mapAuthority = authorityMap.get(authorityId);

            if (mapAuthority != null) {
                return CommonUtil.getSuccessApiResponse(mapAuthority.getAuthorityToUsers());
            } else {
                List<User> userList = mappingMapper.selectAuthorityUsers(authority);
                if (userList == null) {
                    userList = Collections.emptyList();
                }
                authority.setAuthorityToUsers(userList);
                Cache.authorityUserCache.get(authority.getServiceName()).get(authority.getTenantId()).put(authority.getAuthorityId(), authority);
                return CommonUtil.getSuccessApiResponse(userList);
            }
        } catch (Exception e){
            return CommonUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getTenantAuthorities(String serviceName, String tenantId){
        try {
            Authority authority = new Authority();
            authority.setServiceName(serviceName);
            if(tenantId != null){
                authority.setTenantId(tenantId);
            } else{
                authority.setTenantId(DEFAULT_TENANT_ID);
            }
            Map<String, Authority> authorityMap = getAuthorityMapFromCache(authority);
            if (authorityMap != null) {
                List<Authority> tenantAuthorities = new ArrayList<>(authorityMap.values());
                return CommonUtil.getSuccessApiResponse(tenantAuthorities);
            } else {
                List<Authority> tenantAuthorities = authorityMapper.selectTenantAuthorities(authority);
                for(Authority tenantAuthority : tenantAuthorities){
                    Cache.authorityUserCache.get(serviceName).get(tenantId).put(tenantAuthority.getAuthorityId(), tenantAuthority);
                }
                return CommonUtil.getSuccessApiResponse(tenantAuthorities);
            }
        } catch (Exception e){
            return CommonUtil.getFailureApiResponse();
        }
    }

    private Map<String, Authority> getAuthorityMapFromCache(Authority authority){
        String serviceName = authority.getServiceName();
        Map<String, Map<String, Authority>> serviceNameMap = Cache.authorityUserCache.get(serviceName);
        if(serviceNameMap == null){
            serviceNameMap = new HashMap<>();
            Cache.authorityUserCache.put(serviceName, serviceNameMap);
        }

        String tenantId = authority.getTenantId();
        Map<String, Authority> tenantIdMap = serviceNameMap.get(tenantId);
        if(tenantIdMap==null){
            tenantIdMap = new HashMap<>();
            Cache.authorityUserCache.get(serviceName).put(tenantId,tenantIdMap);
        }

        return tenantIdMap;
    }

    private Authority setAuthority(String serviceName, String tenantId, String authorityId, Authority authority){
        authority.setServiceName(serviceName);
        if(tenantId == null){
            authority.setTenantId(DEFAULT_TENANT_ID);
        } else {
            authority.setTenantId(tenantId);
        }
        authority.setAuthorityId(authorityId);
        return authority;
    }
}
