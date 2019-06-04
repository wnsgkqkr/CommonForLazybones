package com.cfl.service;

import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.domain.User;
import com.cfl.mapper.AuthorityMapper;
import com.cfl.mapper.CflObjectMapper;
import com.cfl.mapper.UserMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthorityService {
    @Autowired
    private AuthorityMapper authorityMapper;
    //Authority - User Cache
    private Map<String,Map<String,Map<String,Authority>>> authorityUserCache = new HashMap<>();


    public JSONObject insertAuthorityUser(JSONObject request){
        return
    }
    public JSONObject updateAuthorityUser(JSONObject request){

    }
    public JSONObject deleteAuthorityUser(JSONObject request){

    }
    public JSONObject getAuthorityUser(JSONObject request){

    }

    public JSONObject insertAuthority(JSONObject request){

    }
    public JSONObject updateAuthority(JSONObject request){

    }
    public JSONObject deleteAuthority(JSONObject request){

    }
    public JSONObject getAuthority(JSONObject request){

    }
}
