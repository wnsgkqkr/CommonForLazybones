package com.cfl.controller;

import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.domain.User;
import com.cfl.service.CommonService;
import com.cfl.service.UserService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CommonService commonService;

    //add, remove, get Authorities to User
    @PostMapping(value="/user/authority")
    public JSONObject createUserAuthority(@RequestBody JSONObject requestObject){
        try{
            return commonService.successResult(commonService.createUserAuthority(requestObject));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @DeleteMapping(value="/user/authority")
    public JSONObject removeUserAuthority(@RequestBody JSONObject requestObject){
        try{
            return commonService.successResult(commonService.removeUserAuthority(requestObject));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @GetMapping(value="/user/authority")
    public JSONObject getUserAuthorities(@RequestBody JSONObject requestObject){
        try{
            List<Authority> authorityList = userService.getUserAuthorities(requestObject);
            return commonService.successResult(commonService.toJson(authorityList));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }

    //add, update, remove, get user
    @PostMapping(value="/user")
    public JSONObject createUser(@RequestBody JSONObject requestObject){
        try{
            User user = userService.createData(requestObject);
            return commonService.successResult(commonService.toJson(user));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @PutMapping(value="/user")
    public JSONObject modifyUser(@RequestBody JSONObject requestObject){
        try{
            User user = userService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(user));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @DeleteMapping(value="/user")
    public JSONObject removeUser(@RequestBody JSONObject requestObject){
        try{
            User user = userService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(user));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @GetMapping(value="/user")
    public JSONObject getUser(@RequestBody JSONObject requestObject){
        try{
            User user = userService.getData(requestObject);
            return commonService.successResult(commonService.toJson(user));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
}
