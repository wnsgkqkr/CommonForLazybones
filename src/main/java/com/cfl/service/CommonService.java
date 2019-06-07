package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.customexception.GetHttpStatusException;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.MappingMapper;
import com.cfl.mapper.UserMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class CommonService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MappingMapper mappingMapper;

    //set response API = isSuccess(Boolean), resultCode(int), resultMessage(String)
    public JSONObject successResult(JSONObject jsonObject){
        jsonObject = getHttpResponseProperty(jsonObject);
        jsonObject.put("isSuccess", true);

        log.info("result = "+jsonObject.toString());
        return jsonObject;
    }
    public JSONObject failResult(JSONObject jsonObject){
        jsonObject = getHttpResponseProperty(jsonObject);
        jsonObject.put("isSuccess", false);

        log.info("result = "+jsonObject.toString());
        return jsonObject;
    }

    private JSONObject getHttpResponseProperty(JSONObject jsonObject) {
        HttpClient client = HttpClientBuilder.create().build();
        try{
            HttpResponse httpResponse = client.execute(new HttpGet());

            jsonObject.put("resultCode", httpResponse.getStatusLine().getStatusCode());
            jsonObject.put("resultMessage", httpResponse.getStatusLine().getReasonPhrase());

            return jsonObject;
        } catch (IOException e){
            e.printStackTrace();
            throw new GetHttpStatusException(e);
        }
    }
    //user-authority insert / delete Database and clear cache
    public JSONObject createUserAuthority(JSONObject requestObject){
        User user = setUser(requestObject);
        userMapper.insertUser(user);
        Authority authority = setAuthority(requestObject);
        mappingMapper.insertUserAuthority(user, authority);
        clearUserAuthorityTenantCache(user.getServiceName(),user.getTenantId());
        return requestObject;
    }
    public JSONObject removeUserAuthority(JSONObject requestObject){
        User user = setUser(requestObject);
        Authority authority = setAuthority(requestObject);
        mappingMapper.deleteUserAuthority(user, authority);
        clearUserAuthorityTenantCache(user.getServiceName(),user.getTenantId());
        return requestObject;
    }

    //JSON request to Authority Object
    public Authority setAuthority(JSONObject requestObject){
        Authority authority = new Authority();
        authority.setAuthorityId((String)requestObject.getJSONObject("auth").get("authId"));
        authority.setAuthorityName((String)requestObject.getJSONObject("auth").get("authName"));
        authority.setAuthorityType((String)requestObject.getJSONObject("auth").get("authType"));
        authority.setAuthoritySequence((String)requestObject.getJSONObject("auth").get("authSeq"));
        authority.setServiceName((String)requestObject.get("serviceName"));
        authority.setTenantId((String)requestObject.get("tenantId"));

        return authority;
    }

    //JSON request to User Object
    public User setUser(JSONObject requestObject){
        User user = new User();
        user.setUserId((String)requestObject.getJSONObject("user").get("userId"));
        user.setUserType((String)requestObject.getJSONObject("user").get("userType"));
        user.setUserSequence((String)requestObject.getJSONObject("user").get("userSeq"));
        user.setServiceName((String)requestObject.get("serviceName"));
        user.setTenantId((String)requestObject.get("tenantId"));

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
            Cache.authorityUserCache.get(serviceName).clear();
        }
        synchronized (Cache.userAuthorityCache){
            Cache.userAuthorityCache.get(serviceName).clear();
        }
    }
    public void clearUserAuthorityTenantCache(String serviceName,String tenantId){
        synchronized (Cache.authorityUserCache) {
            Cache.authorityUserCache.get(serviceName).get(tenantId).clear();
        }
        synchronized (Cache.userAuthorityCache){
            Cache.userAuthorityCache.get(serviceName).get(tenantId).clear();
        }
    }
}
