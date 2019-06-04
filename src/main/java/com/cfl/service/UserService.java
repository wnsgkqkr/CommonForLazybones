package com.cfl.service;

import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.mapper.AuthorityMapper;
import com.cfl.mapper.UserMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    //User - Authority Cache
    public static Map<String,Map<String,Map<String,User>>> userAuthorityCache = new HashMap<>();

    public JSONObject getUserAuthority(JSONObject request){
        User user = userAuthorityCache.get().get().get();
        if(user != null){
            user.getUserToAuthorities()
        }else{
            userMapper.getUserAuthority();
        }
        json작업
        return ~;
    }
}
