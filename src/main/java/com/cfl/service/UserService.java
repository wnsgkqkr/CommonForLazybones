package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.MappingMapper;
import com.cfl.mapper.UserMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService implements CflService<User> {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommonService commonService;
    @Autowired
    private MappingMapper mappingMapper;

    //User insert / update / delete Database and refresh cache
    public User createData(JSONObject requestObject){
        User user = commonService.setUser(requestObject);
        userMapper.insertUser(user);
        commonService.clearUserAuthorityTenantCache(user.getServiceName(), user.getTenantId());
        return user;
    }
    public User modifyData(JSONObject requestObject){
        User user = commonService.setUser(requestObject);
        userMapper.updateUser(user);
        commonService.clearUserAuthorityTenantCache(user.getServiceName(), user.getTenantId());
        return user;
    }
    public User removeData(JSONObject requestObject){
        User user = commonService.setUser(requestObject);
        userMapper.deleteUser(user);
        commonService.clearUserAuthorityTenantCache(user.getServiceName(), user.getTenantId());
        return user;
    }
    //get User from cache or Database and put cache
    public User getData(JSONObject requestObject){
        User user = commonService.setUser(requestObject);
        Map<String, User> userMap = getUserMap(user);
        user = userMap.get(user.getUserId());
        if(user == null) {
            user = userMapper.selectUser(user);
            Cache.userAuthorityCache.get(user.getServiceName()).get(user.getTenantId()).put(user.getUserId(), user);
        }
        return user;
    }
    //get AuthorityList in User from cache or Database and put cache
    public List<Authority> getUserAuthorities(JSONObject requestObject){
        User user = commonService.setUser(requestObject);
        Map<String, User> userMap = getUserMap(user);
        user = userMap.get(user.getUserId());
        if(user != null){
            return user.getUserToAuthorities();
        }else{
            List<Authority> authorityList = mappingMapper.selectUserAuthorities(user);
            if(authorityList == null){
                authorityList = Collections.emptyList();
            }
            user.setUserToAuthorities(authorityList);
            Cache.userAuthorityCache.get(user.getServiceName()).get(user.getTenantId()).put(user.getUserId(), user);
            return authorityList;
        }
    }

    //get UserMap in Cache(make cache)
    public Map<String, User> getUserMap(User user){
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
            serviceNameMap.put(tenantId,TenantIdMap);
        }

        return TenantIdMap;
    }
}
