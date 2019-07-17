package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.UserMapper;
import com.cfl.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MappingService mappingService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private HistoryService historyService;

    public ApiResponse createUser(String serviceName, String tenantId, String userId, User user) {
        try {
            user.setServiceName(serviceName);
            user.setTenantId(tenantId);
            user.setUserId(userId);

            userMapper.insertUser(user);
            cacheService.clearUserTenantCache(serviceName, user.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(user);
            historyService.createHistory(serviceName, user.getTenantId(), user, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            log.error("createUser fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse modifyUser(String serviceName, String tenantId, String userId, User user) {
        try {
            user.setServiceName(serviceName);
            user.setTenantId(tenantId);
            user.setUserId(userId);

            userMapper.updateUser(user);
            cacheService.clearUserTenantCache(serviceName, user.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(user);
            historyService.createHistory(serviceName, user.getTenantId(), user, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            log.error("modifyUser fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse removeUser(String serviceName, String tenantId, String userId) {
        try {
            User user = new User(serviceName, tenantId, userId);

            userMapper.deleteUser(user);
            cacheService.clearUserTenantCache(serviceName, user.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(user);
            historyService.createHistory(serviceName, user.getTenantId(), user, successApiResponse);
            return successApiResponse;
        } catch (Exception e) {
            log.error("removeUser fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getUser(String serviceName, String tenantId, String userId){
        try {
            User user = new User(serviceName, tenantId, userId);

            Map<String, User> userMapFromCache = getUserMapFromCache(user);
            User userFromCache = userMapFromCache.get(userId);

            if (userFromCache != null) {
                return ApiResponseUtil.getSuccessApiResponse(userFromCache);
            } else {
                user = userMapper.selectUser(user);
                List<Authority> authorityList = mappingService.getUserAuthorities(user);
                if (authorityList == null) {
                    authorityList = Collections.emptyList();
                }
                user.setUserToAuthorities(authorityList);

                synchronized (Cache.userAuthorityCache) {
                    Cache.userAuthorityCache.get(serviceName).get(user.getTenantId()).put(userId, user);
                }

                return ApiResponseUtil.getSuccessApiResponse(user);
            }
        } catch (Exception e) {
            log.error("getUser fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse getUserAuthoritiesMapping(String serviceName, String tenantId, String userId) {
        try {
            User user = new User(serviceName, tenantId, userId);
            Map<String, User> userMapFromCache = getUserMapFromCache(user);
            User userFromCache = userMapFromCache.get(userId);

            if (userFromCache != null) {
                return ApiResponseUtil.getSuccessApiResponse(userFromCache.getUserToAuthorities());
            } else {
                List<Authority> authorityList = mappingService.getUserAuthorities(user);
                if (authorityList == null) {
                    authorityList = Collections.emptyList();
                }
                user.setUserToAuthorities(authorityList);

                synchronized (Cache.userAuthorityCache) {
                    Cache.userAuthorityCache.get(serviceName).get(user.getTenantId()).put(user.getUserId(), user);
                }

                return ApiResponseUtil.getSuccessApiResponse(authorityList);
            }
        } catch (Exception e) {
            log.error("getUserAuthoritiesMapping fail", e);
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, User> getUserMapFromCache(User user){
        String serviceName = user.getServiceName();
        Map<String, Map<String, User>> serviceNameMap = Cache.userAuthorityCache.get(serviceName);
        if (serviceNameMap == null) {
            serviceNameMap = new HashMap<>();
            Cache.userAuthorityCache.put(serviceName, serviceNameMap);
        }

        String tenantId = user.getTenantId();
        Map<String, User> TenantIdMap = serviceNameMap.get(tenantId);
        if (TenantIdMap == null) {
            TenantIdMap = new HashMap<>();
            Cache.userAuthorityCache.get(serviceName).put(tenantId, TenantIdMap);
        }

        return TenantIdMap;
    }

    public User checkExistAndCreateUser(User needCheckUser) {
        User getUser = userMapper.selectUser(needCheckUser);
        if (getUser != null) {
            return getUser;
        } else {
            userMapper.insertUser(needCheckUser);
            return needCheckUser;
        }
    }
}
