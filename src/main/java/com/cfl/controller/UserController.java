package com.cfl.controller;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.service.CommonService;
import com.cfl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CommonService commonService;

    //add, remove, get Authorities to User
    @PostMapping(value="/user/authority")
    public Map<String, Object> createUserAuthority(@RequestBody ApiRequest requestObject){
        try{
            commonService.createUserAuthority(requestObject);
            return commonService.successResult(commonService.toJson(requestObject));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/user/authority")
    public Map<String, Object> removeUserAuthority(@RequestBody ApiRequest requestObject){
        try{
            commonService.removeUserAuthority(requestObject);
            return commonService.successResult(commonService.toJson(requestObject));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/user/authority")
    public Map<String, Object> getUserAuthorities(@RequestBody ApiRequest requestObject){
        try{
            List<Authority> authorityList = userService.getUserAuthorities(requestObject);
            return commonService.successResult(commonService.toJson(authorityList));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }

    //add, update, remove, get user
    @PostMapping(value="/user")
    public Map<String, Object> createUser(@RequestBody ApiRequest requestObject){
        try{
            User user = userService.createData(requestObject);
            return commonService.successResult(commonService.toJson(user));
        } catch (Exception e){
            e.printStackTrace();
            return commonService.failResult(e);
        }
    }
    @PutMapping(value="/user")
    public Map<String, Object> modifyUser(@RequestBody ApiRequest requestObject){
        try{
            User user = userService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(user));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/user")
    public Map<String, Object> removeUser(@RequestBody ApiRequest requestObject){
        try{
            User user = userService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(user));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/user")
    public Map<String, Object> getUser(@RequestBody ApiRequest requestObject){
        try{
            User user = userService.getData(requestObject);
            return commonService.successResult(commonService.toJson(user));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
}
