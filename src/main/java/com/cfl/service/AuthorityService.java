package com.cfl.service;

import com.cfl.domain.Authority;
import com.cfl.mapper.AuthorityMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AuthorityService {
    @Autowired
    private AuthorityMapper authorityMapper;
    @Autowired
    private CommonService commonService;
    //Authority - User Cache
    public static Map<String,Map<String,Map<String,Authority>>> authorityUserCache = new HashMap<>();


    public JSONObject getAuthorityUser(JSONObject requestObject){
        Authority authority = authorityUserCache.get().get().get();
        if(authority != null){
            authority.getAuthorityToUsers();
        }else{
            authorityMapper.getAuthorityUser();
        }
        json작업
        return ~;
    }

    public void insertAuthority(JSONObject requestObject){
        Authority authority = commonService.setAuthority(requestObject);
        if(authorityMapper.getAuthority(authority) != null){
            throw new DuplicateKeyException("Duplicate: same Id is already Exist");
        }
        authorityMapper.insertAuthority(authority);
        log.info(authority.getAuthorityName()+" is inserted");

        //initial authority - user cache
        Map<String, Authority> authorityMap = new HashMap<>();
        authorityMap.put(authority.getAuthorityId(), authority);
        Map<String, Map<String, Authority>> tenantAuthorityMap = new HashMap<>();
        tenantAuthorityMap.put(authority.getTenantId(), authorityMap);
        authorityUserCache.put(authority.getServiceName(),tenantAuthorityMap);
        log.info(authority.getAuthorityName()+" Cache is created");
    }
    public void updateAuthority(JSONObject requestObject){
        authorityMapper.updateAuthority(commonService.setAuthority(requestObject));
        log.info((String)requestObject.getJSONObject("auth").get("authName")+" is updated");
    }
    public void deleteAuthority(JSONObject requestObject){
        authorityMapper.deleteAuthority(commonService.setAuthority(requestObject));
        log.info((String)requestObject.getJSONObject("auth").get("authName")+" is deleted");
    }
    public JSONObject getAuthorities(JSONObject requestObject){
        List<Authority> authorityList = authorityMapper.getAuthorities(commonService.setAuthority(requestObject));
        JSONObject jsonObject = new JSONObject(new Gson().toJson(authorityList));
        return jsonObject;
    }
}
