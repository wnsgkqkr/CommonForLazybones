package com.cfl.service;

import com.cfl.domain.CflObject;
import com.cfl.mapper.CflObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ObjectService {
    @Autowired
    private CflObjectMapper cflObjectMapper;
    //Object - Authority cache
    private Map<String, Map<String, Map<String, CflObject>>> objectAuthorityCache = new HashMap<>();


    public JSONObject insertObject(JSONObject request){

    }
    public JSONObject updateObject(JSONObject request){

    }
    public JSONObject deleteObject(JSONObject request){

    }
    public JSONObject getObject(JSONObject request){

    }

    public JSONObject insertObjectAuthority(JSONObject request){

    }
    public JSONObject updateObjectAuthority(JSONObject request){

    }
    public JSONObject deleteObjectAuthority(JSONObject request){

    }
    public JSONObject getObjectAuthority(JSONObject request){

    }
}
