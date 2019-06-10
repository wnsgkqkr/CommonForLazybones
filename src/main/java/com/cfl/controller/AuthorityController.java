package com.cfl.controller;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.service.AuthorityService;
import com.cfl.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class AuthorityController {
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private CommonService commonService;

    //add, remove, get users to authority
    @PostMapping(value="/authority/user")
    public Map<String, Object> createAuthorityUser(@RequestBody ApiRequest requestObject){
        try{
            commonService.createUserAuthority(requestObject);
            return commonService.successResult(commonService.toJson(requestObject));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/authority/user")
    public Map<String, Object> removeAuthorityUser(@RequestBody ApiRequest requestObject){
        try{
            commonService.removeUserAuthority(requestObject);
            return commonService.successResult(commonService.toJson(requestObject));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/authority/user")
    public Map<String, Object> getAuthorityUsers(@RequestBody ApiRequest requestObject){
        try{
            List<User> userList = authorityService.getAuthorityUsers(requestObject);
            return commonService.successResult(commonService.toJson(userList));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }

    //add, update, remove, get authority
    @PostMapping(value="/authority")
    public Map<String, Object> createAuthority(@RequestBody ApiRequest requestObject){
        try{
            Authority authority = authorityService.createData(requestObject);
            return commonService.successResult(commonService.toJson(authority));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @PutMapping(value="/authority")
    public Map<String, Object> modifyAuthority(@RequestBody ApiRequest requestObject){
        try{
            Authority authority = authorityService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(authority));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/authority")
    public Map<String, Object> removeAuthority(@RequestBody ApiRequest requestObject){
        try{
            Authority authority = authorityService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(authority));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/authority")
    public Map<String, Object> getAuthority(@RequestBody ApiRequest requestObject){
        try{
            Authority authority = authorityService.getData(requestObject);
            return commonService.successResult(commonService.toJson(authority));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/authorities")
    public Map<String, Object> getAuthorities(@RequestBody ApiRequest requestObject){
        try{
            List<Authority> authorityList = authorityService.getTenantAuthorities(requestObject);
            return commonService.successResult(commonService.toJson(authorityList));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
}
