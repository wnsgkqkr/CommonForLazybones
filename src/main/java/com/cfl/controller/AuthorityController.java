package com.cfl.controller;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.service.AuthorityService;
import com.cfl.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/authority")
public class AuthorityController {
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private CommonService commonService;

    //add, remove, get users to authority
    @PostMapping(value="/user")
    public ApiResponse createAuthorityUser(@RequestBody ApiRequest requestObject){
        try{
            commonService.createUserAuthority(requestObject);
            return commonService.successResult(requestObject, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/user")
    public ApiResponse removeAuthorityUser(@RequestBody ApiRequest requestObject){
        try{
            commonService.removeUserAuthority(requestObject);
            return commonService.successResult(requestObject, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/user")
    public ApiResponse getAuthorityUsers(@RequestBody ApiRequest requestObject){
        try{
            List<User> userList = authorityService.getAuthorityUsers(requestObject);
            return commonService.successResult(userList, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }

    //add, update, remove, get authority
    @PostMapping
    public ApiResponse createAuthority(@RequestBody ApiRequest requestObject){
        try{
            Authority authority = authorityService.createData(requestObject);
            return commonService.successResult(authority, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @PutMapping
    public ApiResponse modifyAuthority(@RequestBody ApiRequest requestObject){
        try{
            Authority authority = authorityService.modifyData(requestObject);
            return commonService.successResult(authority, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping
    public ApiResponse removeAuthority(@RequestBody ApiRequest requestObject){
        try{
            Authority authority = authorityService.removeData(requestObject);
            return commonService.successResult(authority, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping
    public ApiResponse getAuthority(@RequestBody ApiRequest requestObject){
        try{
            Authority authority = authorityService.getData(requestObject);
            return commonService.successResult(authority, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/authorities")
    public ApiResponse getAuthorities(@RequestBody ApiRequest requestObject){
        try{
            List<Authority> authorityList = authorityService.getTenantAuthorities(requestObject);
            return commonService.successResult(authorityList, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
}
