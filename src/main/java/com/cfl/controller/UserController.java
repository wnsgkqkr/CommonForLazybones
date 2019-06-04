package com.cfl.controller;

import com.cfl.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping(value="/user/authority")
    public JSONObject insertUserAuthority(@RequestBody JSONObject requestObject){
        return userService.insertUserAuthority(requestObject);
    }
    @PutMapping(value="/user/authority")
    public JSONObject updateUserAuthority(@RequestBody JSONObject requestObject){
        return userService.updateUserAuthority(requestObject);
    }
    @DeleteMapping(value="/user/authority")
    public JSONObject deleteUserAuthority(@RequestBody JSONObject requestObject){
        return userService.deleteUserAuthority(requestObject);
    }
    @GetMapping(value="/user/authority")
    public JSONObject getUserAuthority(@RequestBody JSONObject requestObject){
        return userService.getUserAuthority(requestObject);
    }
}
