package com.cfl.controller;

import com.cfl.service.CommonService;
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
    @Autowired
    private CommonService commonService;

    @PostMapping(value="/user/authority")
    public JSONObject insertUserAuthority(@RequestBody JSONObject requestObject){
        try{
            commonService.insertUserAuthority(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
    @DeleteMapping(value="/user/authority")
    public JSONObject deleteUserAuthority(@RequestBody JSONObject requestObject){
        try{
            commonService.deleteUserAuthority(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
    @GetMapping(value="/user/authority")
    public JSONObject getUserAuthority(@RequestBody JSONObject requestObject){
        return userService.getUserAuthority(requestObject);
    }
}
