package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.MappingMapper;
import com.cfl.mapper.UserMapper;
import com.cfl.util.ApiResponseUtil;
import com.cfl.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MappingMapper mappingMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private HistoryService historyService;

    public ApiResponse createUser(String serviceName, String tenantId, String userId, User user) {
        try {
            user = setUser(serviceName, tenantId, userId, user);
            userMapper.insertUser(user);
            cacheService.clearUserTenantCache(serviceName, user.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(user);
            historyService.createHistory(serviceName, user.getTenantId(), user, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse modifyUser(String serviceName, String tenantId, String userId, User user) {
        try {
            user = setUser(serviceName, tenantId, userId, user);
            userMapper.updateUser(user);
            cacheService.clearUserTenantCache(serviceName, user.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(user);
            historyService.createHistory(serviceName, user.getTenantId(), user, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    public ApiResponse removeUser(String serviceName, String tenantId, String userId) {
        try {
            User user = setUser(serviceName, tenantId, userId, new User());
            userMapper.deleteUser(user);
            cacheService.clearUserTenantCache(serviceName, user.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(user);
            historyService.createHistory(serviceName, user.getTenantId(), user, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    //get User from cache, if it doesn't exist in Cache then get Database and put cache
    public ApiResponse getUser(String serviceName, String tenantId, String userId){
        try {
            User user = setUser(serviceName, tenantId, userId, new User());
            Map<String, User> userMap = getUserMapFromCache(user);
            User mapUser = userMap.get(userId);

            if(mapUser != null) {
                return ApiResponseUtil.getSuccessApiResponse(mapUser);
            } else {
                user = userMapper.selectUser(user);
                Cache.userAuthorityCache.get(serviceName).get(user.getTenantId()).put(userId, user);
                return ApiResponseUtil.getSuccessApiResponse(user);
            }
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    //get AuthorityList in User from cache, if it doesn't exist in Cache then get Database and put cache
    public ApiResponse getUserAuthoritiesMapping(String serviceName, String tenantId, String userId) {
        try {
            User user = setUser(serviceName, tenantId, userId, new User());
            Map<String, User> userMap = getUserMapFromCache(user);
            User mapUser = userMap.get(userId);

            if (mapUser != null) {
                return ApiResponseUtil.getSuccessApiResponse(mapUser.getUserToAuthorities());
            } else {
                List<Authority> authorityList = mappingMapper.selectUserAuthorities(user);
                if (authorityList == null) {
                    authorityList = Collections.emptyList();
                }
                user.setUserToAuthorities(authorityList);
                Cache.userAuthorityCache.get(serviceName).get(user.getTenantId()).put(user.getUserId(), user);
                return ApiResponseUtil.getSuccessApiResponse(authorityList);
            }
        } catch (Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }

    private Map<String, User> getUserMapFromCache(User user){
        String serviceName = user.getServiceName();
        Map<String, Map<String, User>> serviceNameMap = Cache.userAuthorityCache.get(serviceName);
        if(serviceNameMap == null){
            serviceNameMap = new HashMap<>();
            Cache.userAuthorityCache.put(serviceName, serviceNameMap);
        }

        String tenantId = user.getTenantId();
        Map<String, User> TenantIdMap = serviceNameMap.get(tenantId);
        if(TenantIdMap==null){
            TenantIdMap = new HashMap<>();
            Cache.userAuthorityCache.get(serviceName).put(tenantId,TenantIdMap);
        }

        return TenantIdMap;
    }

    private User setUser(String serviceName, String tenantId, String userId, User user) {
        user.setServiceName(serviceName);
        if(tenantId == null) {
            user.setTenantId(Constant.DEFAULT_TENANT_ID);
        } else {
            user.setTenantId(tenantId);
        }
        user.setUserId(userId);
        return user;
    }

    public User checkExistAndCreateUser(User needCheckUser) {
        User getUser = userMapper.selectUser(needCheckUser);
        if(getUser != null) {
            return getUser;
        } else {
            userMapper.insertUser(needCheckUser);
            return needCheckUser;
        }
    }
}
