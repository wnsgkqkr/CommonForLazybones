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
    private Map<String,Map<String,Map<String,User>>> userAuthorityCache = new HashMap<>();

    public JSONObject insertUserAuthority(JSONObject request){

    }
    public JSONObject updateUserAuthority(JSONObject request){

    }
    public JSONObject deleteUserAuthority(JSONObject request){

    }
    public JSONObject getUserAuthority(JSONObject request){

    }
}
