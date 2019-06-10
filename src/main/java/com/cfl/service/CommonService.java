package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiRequest;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.MappingMapper;
import com.cfl.mapper.UserMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CommonService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MappingMapper mappingMapper;

    //set response API = isSuccess(Boolean), resultCode(int), resultMessage(String)
    public Map<String,Object> successResult(JSONObject jsonObject){
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("isSuccess", true);
        returnMap.put("resultCode", HttpStatus.SC_OK);
        returnMap.put("resultMessage", jsonObject.toString());

        return returnMap;
    }
    public Map<String,Object> failResult(Exception e){
        Map<String,Object> returnMap = new HashMap<>();
        returnMap.put("isSuccess", false);
        returnMap.put("resultCode", HttpStatus.SC_INTERNAL_SERVER_ERROR);
        returnMap.put("resultMessage", e.getMessage());

        return returnMap;
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

    //JSON request to Authority Object
    public Authority setAuthority(ApiRequest requestObject){
        Authority authority = requestObject.getAuthority();
        authority.setServiceName(requestObject.getServiceName());
        authority.setTenantId(requestObject.getTenantId());

        return authority;
    }

    //JSON request to User Object
    public User setUser(ApiRequest requestObject){
        User user = requestObject.getUser();
        user.setServiceName(requestObject.getServiceName());
        user.setTenantId(requestObject.getTenantId());

        return user;
    }

    //Object to JSON
    public JSONObject toJson(Object object){
        return new JSONObject(new Gson().toJson(object));
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
