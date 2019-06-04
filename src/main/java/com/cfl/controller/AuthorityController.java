package com.cfl.controller;

import com.cfl.domain.Authority;
import com.cfl.service.AuthorityService;
import com.cfl.service.CommonService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class AuthorityController {
    @Autowired
    private AuthorityService authorityService;
    @Autowired
    private CommonService commonService;

    //add, remove, get users to authority
    @PostMapping(value="/authority/user")
    public JSONObject insertAuthorityUser(@RequestBody JSONObject requestObject){
        try{
            commonService.insertUserAuthority(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
    @DeleteMapping(value="/authority/user")
    public JSONObject deleteAuthorityUser(@RequestBody JSONObject requestObject){
        try{
            commonService.deleteUserAuthority(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
    @GetMapping(value="/authority/user")
    public JSONObject getAuthorityUser(@RequestBody JSONObject requestObject){
        return authorityService.getAuthorityUser(requestObject);
    }

    //add, update, remove, get authority
    @PostMapping(value="/authority")
    public JSONObject insertAuthority(@RequestBody JSONObject requestObject){
        try{
            authorityService.insertAuthority(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
    @PutMapping(value="/authority")
    public JSONObject updateAuthority(@RequestBody JSONObject requestObject){
        try{
            authorityService.updateAuthority(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
    @DeleteMapping(value="/authority")
    public JSONObject deleteAuthority(@RequestBody JSONObject requestObject){
        try{
            authorityService.deleteAuthority(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
    @GetMapping(value="/authority")
    public JSONObject getAuthority(@RequestBody JSONObject requestObject){
        try{
            return commonService.successResult(authorityService.getAuthorities(requestObject));
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
}
