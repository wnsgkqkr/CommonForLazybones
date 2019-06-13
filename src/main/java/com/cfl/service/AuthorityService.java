package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.*;
import com.cfl.mapper.AuthorityMapper;
import com.cfl.mapper.MappingMapper;
import com.cfl.util.ApiResponseUtil;
import com.cfl.util.Constant;
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
    private UserService userService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private HistoryService historyService;

    public ApiResponse createAuthority(String serviceName, String tenantId, String authorityId, Authority authority) {
        try {
            authority = setAuthority(serviceName, tenantId, authorityId, authority);
            authorityMapper.insertAuthority(authority);
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) { // TODO Exception 정해서 코드 정하고 넘겨주기
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse modifyAuthority(String serviceName, String tenantId, String authorityId, Authority authority) {
        try {
            authority = setAuthority(serviceName, tenantId, authorityId, authority);
            authorityMapper.updateAuthority(serviceName, authority.getTenantId(), authorityId, authority);
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) { // TODO 실패도 히스토리추가(오류이후)
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse removeAuthority(String serviceName, String tenantId, String authorityId) {
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            authorityMapper.deleteAuthority(authority);
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    //get Authority from cache, if it doesn't exist in Cache then get Database and put cache
    public ApiResponse getAuthority(String serviceName, String tenantId, String authorityId){
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            Map<String, Authority> authorityMap = getAuthorityMapFromCache(authority);
            Authority mapAuthority = authorityMap.get(authorityId);

            if(mapAuthority != null) {
                return ApiResponseUtil.getSuccessApiResponse(mapAuthority);
            } else {
                authority = authorityMapper.selectAuthority(authority);
                Cache.authorityUserCache.get(serviceName).get(authority.getTenantId()).put(authorityId, authority);
                return ApiResponseUtil.getSuccessApiResponse(authority);
            }
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse createAuthorityUsersMapping(String serviceName, String tenantId, String authorityId, List<User> requestUsers) {
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            for (User requestUser : requestUsers) {
                requestUser.setServiceName(serviceName);
                requestUser.setTenantId(authority.getTenantId());
                requestUser = userService.checkExistAndCreateUser(requestUser);
                mappingMapper.insertAuthorityUser(authorityId, requestUser);
            }
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(requestUsers);
            historyService.createHistory(serviceName, authority.getTenantId(), requestUsers, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse removeAuthorityUsersMapping(String serviceName, String tenantId, String authorityId, List<User> requestUsers) {
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            for (User requestUser : requestUsers) {
                requestUser.setServiceName(serviceName);
                requestUser.setTenantId(authority.getTenantId());
                mappingMapper.deleteAuthorityUser(authorityId, requestUser);
            }
            cacheService.clearUserAuthorityTenantCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(requestUsers);
            historyService.createHistory(serviceName, authority.getTenantId(), requestUsers, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    //get UserList in Authority from cache, if it doesn't exist in Cache then get Database and put cache
    public ApiResponse getAuthorityUsersMapping(String serviceName, String tenantId, String authorityId) {
        try {
            Authority authority = setAuthority(serviceName, tenantId, authorityId, new Authority());
            Map<String, Authority> authorityMap = getAuthorityMapFromCache(authority);
            Authority mapAuthority = authorityMap.get(authorityId);

            if (mapAuthority != null) {
                return ApiResponseUtil.getSuccessApiResponse(mapAuthority.getAuthorityToUsers());
            } else {
                List<User> userList = mappingMapper.selectAuthorityUsers(authority);
                if (userList == null) {
                    userList = Collections.emptyList();
                }
                authority.setAuthorityToUsers(userList);
                Cache.authorityUserCache.get(serviceName).get(authority.getTenantId()).put(authority.getAuthorityId(), authority);
                return ApiResponseUtil.getSuccessApiResponse(userList);
            }
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getTenantAuthorities(String serviceName, String tenantId) {
        try {
            Authority authority = new Authority();
            authority.setServiceName(serviceName);
            if(tenantId != null) {
                authority.setTenantId(tenantId);
            } else {
                authority.setTenantId(Constant.DEFAULT_TENANT_ID);
            }
            Map<String, Authority> authorityMap = getAuthorityMapFromCache(authority);
            if (authorityMap != null) {
                List<Authority> tenantAuthorities = new ArrayList<>(authorityMap.values());
                return ApiResponseUtil.getSuccessApiResponse(tenantAuthorities);
            } else {
                List<Authority> tenantAuthorities = authorityMapper.selectTenantAuthorities(authority);
                for(Authority tenantAuthority : tenantAuthorities){
                    Cache.authorityUserCache.get(serviceName).get(tenantId).put(tenantAuthority.getAuthorityId(), tenantAuthority);
                }
                return ApiResponseUtil.getSuccessApiResponse(tenantAuthorities);
            }
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, Authority> getAuthorityMapFromCache(Authority authority) {
        String serviceName = authority.getServiceName();
        Map<String, Map<String, Authority>> serviceNameMap = Cache.authorityUserCache.get(serviceName);
        if(serviceNameMap == null) {
            serviceNameMap = new HashMap<>();
            Cache.authorityUserCache.put(serviceName, serviceNameMap);
        }

        String tenantId = authority.getTenantId();
        Map<String, Authority> tenantIdMap = serviceNameMap.get(tenantId);
        if(tenantIdMap==null) {
            tenantIdMap = new HashMap<>();
            Cache.authorityUserCache.get(serviceName).put(tenantId,tenantIdMap);
        }

        return tenantIdMap;
    }

    private Authority setAuthority(String serviceName, String tenantId, String authorityId, Authority authority) {
        authority.setServiceName(serviceName);
        if(tenantId == null) {
            authority.setTenantId(Constant.DEFAULT_TENANT_ID);
        } else {
            authority.setTenantId(tenantId);
        }
        authority.setAuthorityId(authorityId);
        return authority;
    }

    public Authority checkExistAndCreateAuthority(Authority needCheckAuthority){
        Authority getAuthority = authorityMapper.selectAuthority(needCheckAuthority);
        if(getAuthority != null) {
            return getAuthority;
        } else {
            authorityMapper.insertAuthority(needCheckAuthority);
            return needCheckAuthority;
        }
    }
}
