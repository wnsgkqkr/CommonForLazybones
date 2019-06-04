package com.cfl.controller;

import com.cfl.service.AuthorityService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class AuthorityController {
    @Autowired
    private AuthorityService authorityService;

    @PostMapping(value="/authority/user")
    public JSONObject insertAuthorityUser(@RequestBody JSONObject requestObject){
        return authorityService.insertAuthorityUser(requestObject);
    }
    @PutMapping(value="/authority/user")
    public JSONObject updateAuthorityUser(@RequestBody JSONObject requestObject){
        return authorityService.updateAuthorityUser(requestObject);
    }
    @DeleteMapping(value="/authority/user")
    public JSONObject deleteAuthorityUser(@RequestBody JSONObject requestObject){
        return authorityService.deleteAuthorityUser(requestObject);
    }
    @GetMapping(value="/authority/user")
    public JSONObject getAuthorityUser(@RequestBody JSONObject requestObject){
        return authorityService.getAuthorityUser(requestObject);
    }

    @PostMapping(value="/authority")
    public JSONObject insertAuthority(@RequestBody JSONObject requestObject){
        return authorityService.insertAuthority(requestObject);
    }
    @PutMapping(value="/authority")
    public JSONObject updateAuthority(@RequestBody JSONObject requestObject){
        return authorityService.updateAuthority(requestObject);
    }
    @DeleteMapping(value="/authority")
    public JSONObject deleteAuthority(@RequestBody JSONObject requestObject){
        return authorityService.deleteAuthority(requestObject);
    }
    @GetMapping(value="/authority")
    public JSONObject getAuthority(@RequestBody JSONObject requestObject){
        return authorityService.getAuthority(requestObject);
    }
}
