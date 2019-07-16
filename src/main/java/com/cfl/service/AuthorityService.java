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
    @Autowired
    private MappingService mappingService;

    public ApiResponse createAuthority(String serviceName, String tenantId, String authorityId, Authority authority) {
        try {
            authority.setServiceName(serviceName);
            authority.setTenantId(tenantId);
            authority.setAuthorityId(authorityId);

            authorityMapper.insertAuthority(authority);
            cacheService.refreshTenantAuthorityCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) { // TODO Exception 정해서 코드 정하고 넘겨주기
            log.error(e.getMessage());
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse modifyAuthority(String serviceName, String tenantId, String authorityId, Authority authority) {
        try {
            authority.setServiceName(serviceName);
            authority.setTenantId(tenantId);
            authority.setAuthorityId(authorityId);

            authorityMapper.updateAuthority(serviceName, authority.getTenantId(), authorityId, authority);
            cacheService.refreshTenantAuthorityCache(serviceName, authority.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(authority);
            historyService.createHistory(serviceName, authority.getTenantId(), authority, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) { // TODO 실패도 히스토리추가(오류이후)
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse removeAuthority(String serviceName, String tenantId, String authorityId) {
        try { // TODO 삭제처리 할 경우 매핑도 삭제하게 처리?
            Authority authority = new Authority(serviceName, tenantId, authorityId);
            authorityMapper.deleteAuthority(authority);
            cacheService.refreshTenantAuthorityCache(serviceName, authority.getTenantId());
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
            Authority authority = new Authority(serviceName, tenantId, authorityId);
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
            Authority authority = new Authority(serviceName, tenantId, authorityId);
            List<User> duplicatedUserList = new ArrayList<>();

            for (User requestUser : requestUsers) {
                requestUser.setServiceName(serviceName);
                requestUser.setTenantId(authority.getTenantId());
                requestUser = userService.checkExistAndCreateUser(requestUser);

                if (mappingService.isExistAuthorityUserMapping(authorityId, requestUser)) {
                    duplicatedUserList.add(requestUser);
                } else {
                    mappingService.createAuthorityUserMappting(authorityId, requestUser);
                }
            }

            cacheService.refreshTenantAuthorityCache(serviceName, authority.getTenantId());

            ApiResponse apiResponse;

            if (duplicatedUserList.size() == 0) {
                apiResponse = ApiResponseUtil.getSuccessApiResponse(requestUsers);
            } else {
                apiResponse =  ApiResponseUtil.getDuplicateApiResponse(duplicatedUserList);
            }

            historyService.createHistory(serviceName, authority.getTenantId(), requestUsers, apiResponse.getHeader().getResultMessage());
            return apiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse removeAuthorityUsersMapping(String serviceName, String tenantId, String authorityId, List<User> requestUsers) {
        try {
            Authority authority = new Authority(serviceName, tenantId, authorityId);
            for (User requestUser : requestUsers) {
                requestUser.setServiceName(serviceName);
                requestUser.setTenantId(authority.getTenantId());
                mappingMapper.deleteAuthorityUser(authorityId, requestUser);
            }
            cacheService.refreshTenantAuthorityCache(serviceName, authority.getTenantId());
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
            Authority authority = new Authority(serviceName, tenantId, authorityId);
            Map<String, Authority> authorityMap = getAuthorityMapFromCache(authority);
            Authority mapAuthority = authorityMap.get(authorityId);

            if (mapAuthority != null) {
                return ApiResponseUtil.getSuccessApiResponse(mapAuthority.getAuthorityToUsers());
            } else {
                authority = authorityMapper.selectAuthority(authority);
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
            Authority authority = new Authority(serviceName, tenantId);

            Map<String, Authority> authorityMap = getAuthorityMapFromCache(authority);

            if (authorityMap != null) {
                List<Authority> tenantAuthorities = new ArrayList<>(authorityMap.values());
                return ApiResponseUtil.getSuccessApiResponse(tenantAuthorities);
            } else {
                List<Authority> tenantAuthorities = authorityMapper.selectTenantAuthorities(serviceName, tenantId);
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
