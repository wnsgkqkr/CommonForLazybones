package com.cfl.controller;

import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.service.AuthorityService;
import com.cfl.service.CommonService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthorityController {
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private CommonService commonService;

    //add, remove, get users to authority
    @PostMapping(value="/authority/user")
    public JSONObject createAuthorityUser(@RequestBody JSONObject requestObject){
        try{
            return commonService.successResult(commonService.createUserAuthority(requestObject));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @DeleteMapping(value="/authority/user")
    public JSONObject removeAuthorityUser(@RequestBody JSONObject requestObject){
        try{
            return commonService.successResult(commonService.removeUserAuthority(requestObject));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @GetMapping(value="/authority/user")
    public JSONObject getAuthorityUsers(@RequestBody JSONObject requestObject){
        try{
            List<User> userList = authorityService.getAuthorityUsers(requestObject);
            return commonService.successResult(commonService.toJson(userList));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }

    //add, update, remove, get authority
    @PostMapping(value="/authority")
    public JSONObject createAuthority(@RequestBody JSONObject requestObject){
        try{
            Authority authority = authorityService.createData(requestObject);
            return commonService.successResult(commonService.toJson(authority));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @PutMapping(value="/authority")
    public JSONObject modifyAuthority(@RequestBody JSONObject requestObject){
        try{
            Authority authority = authorityService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(authority));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @DeleteMapping(value="/authority")
    public JSONObject removeAuthority(@RequestBody JSONObject requestObject){
        try{
            Authority authority = authorityService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(authority));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @GetMapping(value="/authority")
    public JSONObject getAuthority(@RequestBody JSONObject requestObject){
        try{
            Authority authority = authorityService.getData(requestObject);
            return commonService.successResult(commonService.toJson(authority));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @GetMapping(value="/authorities")
    public JSONObject getAuthorities(@RequestBody JSONObject requestObject){
        try{
            List<Authority> authorityList = authorityService.getTenantAuthorities(requestObject);
            return commonService.successResult(commonService.toJson(authorityList));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
}
