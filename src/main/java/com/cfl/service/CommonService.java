package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.MappingMapper;
import com.cfl.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommonService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MappingMapper mappingMapper;
    @Autowired
    private HistoryService historyService;

    public static final String MESSAGE_SUCCESS = "SUCCESS";
    public static final String MESSAGE_FAILURE = "FAILURE";

    //set response API = isSuccess(Boolean), resultCode(int), resultMessage(String)
    public ApiResponse successResult(Object object, ApiRequest requestObject){
        ApiResponse apiResponse = new ApiResponse(true, HttpStatus.SC_OK, MESSAGE_SUCCESS, object);

        historyService.createHistory(requestObject, object.toString());
        return apiResponse;
    }
    public ApiResponse failResult(Object object){
        ApiResponse apiResponse = new ApiResponse(false, HttpStatus.SC_INTERNAL_SERVER_ERROR, MESSAGE_FAILURE, object);
        return apiResponse;
    }

    //user-authority insert / delete Database and clear cache
    public ApiRequest createUserAuthority(ApiRequest requestObject){
        User user = setUser(requestObject);
        userMapper.insertUser(user);
        Authority authority = setAuthority(requestObject);
        mappingMapper.insertUserAuthority(user, authority);
        clearUserAuthorityTenantCache(user.getServiceName(),user.getTenantId());
        return requestObject;
    }
    public ApiRequest removeUserAuthority(ApiRequest requestObject){
        User user = setUser(requestObject);
        Authority authority = setAuthority(requestObject);
        mappingMapper.deleteUserAuthority(user, authority);
        clearUserAuthorityTenantCache(user.getServiceName(),user.getTenantId());
        return requestObject;
    }

    //VO request to Authority Object
    public Authority setAuthority(ApiRequest requestObject){
        Authority authority = requestObject.getAuthority();
        authority.setServiceName(requestObject.getServiceName());
        authority.setTenantId(requestObject.getTenantId());

        return authority;
    }

    //VO request to User Object
    public User setUser(ApiRequest requestObject){
        User user = requestObject.getUser();
        user.setServiceName(requestObject.getServiceName());
        user.setTenantId(requestObject.getTenantId());

        return user;
    }

    public void clearUserAuthorityCache(){
        synchronized (Cache.authorityUserCache) {
            Cache.authorityUserCache.clear();
        }
        synchronized (Cache.userAuthorityCache){
            Cache.userAuthorityCache.clear();
        }
    }
    public void clearUserAuthorityServiceCache(String serviceName){
        synchronized (Cache.authorityUserCache) {
            if(Cache.authorityUserCache.get(serviceName)!=null) {
                Cache.authorityUserCache.get(serviceName).clear();
            }
        }
        synchronized (Cache.userAuthorityCache){
            if(Cache.userAuthorityCache.get(serviceName)!=null) {
                Cache.userAuthorityCache.get(serviceName).clear();
            }
        }
    }
    public void clearUserAuthorityTenantCache(String serviceName,String tenantId){
        synchronized (Cache.authorityUserCache) {
            if(Cache.authorityUserCache.get(serviceName)!=null){
                if(Cache.authorityUserCache.get(serviceName).get(tenantId)!=null){
                    Cache.authorityUserCache.get(serviceName).get(tenantId).clear();
                }
            }
        }
        synchronized (Cache.userAuthorityCache){
            if(Cache.userAuthorityCache.get(serviceName)!=null){
                if(Cache.userAuthorityCache.get(serviceName).get(tenantId)!=null){
                    Cache.userAuthorityCache.get(serviceName).get(tenantId).clear();
                }
            }
        }
    }
}
