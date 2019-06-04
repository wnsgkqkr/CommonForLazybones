package com.cfl.service;

import com.cfl.customexception.GetHttpStatusException;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.AuthorityMapper;
import com.cfl.mapper.MappingMapper;
import com.cfl.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
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

    //user-authority table insert, update, delete, get
    public void insertUserAuthority(JSONObject requestObject){
        User user = setUser(requestObject);
        Authority authority = setAuthority(requestObject);
        if(userMapper.getUser(user) == null){
            userMapper.insertUser(user);
            //initial User - Authority Cache
            Map<String, User> userMap = new HashMap<>();
            userMap.put(user.getUserId(), user);
            Map<String, Map<String, User>> tenantUserMap = new HashMap<>();
            tenantUserMap.put(user.getTenantId(), userMap);
            UserService.userAuthorityCache.put(user.getServiceName(),tenantUserMap);
            log.info(user.getUserId()+" Cache is created");
        }
        mappingMapper.insertUserAuthority(user, authority);

        UserService.userAuthorityCache.get(user.getServiceName()).get(user.getTenantId()).
                get(user.getUserId()).getUserToAuthorities().put(authority.getAuthorityId(), authority);
        AuthorityService.authorityUserCache.get(authority.getServiceName()).get(authority.getTenantId()).
                get(authority.getAuthorityId()).getAuthorityToUsers().put(user.getUserId(),user);
    }
    public void deleteUserAuthority(JSONObject requestObject){
        User user = setUser(requestObject);
        Authority authority = setAuthority(requestObject);
        mappingMapper.deleteUserAuthority(user, authority);

        UserService.userAuthorityCache.get(user.getServiceName()).get(user.getTenantId()).
                get(user.getUserId()).getUserToAuthorities().remove(authority.getAuthorityId());
        
    }

    public Authority setAuthority(JSONObject requestObject){
        //JSON request to Authority Object
        Authority authority = new Authority();
        authority.setAuthorityId((String)requestObject.getJSONObject("auth").get("authId"));
        authority.setAuthorityName((String)requestObject.getJSONObject("auth").get("authName"));
        authority.setAuthorityType((String)requestObject.getJSONObject("auth").get("authType"));
        authority.setAuthoritySequence((String)requestObject.getJSONObject("auth").get("authSeq"));
        authority.setServiceName((String)requestObject.get("serviceName"));
        authority.setTenantId((String)requestObject.get("tenantId"));

        return authority;
    }

    public User setUser(JSONObject requestObject){
        //JSON request to User Object
        User user = new User();
        user.setUserId((String)requestObject.getJSONObject("user").get("userId"));
        user.setUserType((String)requestObject.getJSONObject("user").get("userType"));
        user.setUserSequence((String)requestObject.getJSONObject("user").get("userSeq"));
        user.setServiceName((String)requestObject.get("serviceName"));
        user.setTenantId((String)requestObject.get("tenantId"));

        return user;
    }
}
