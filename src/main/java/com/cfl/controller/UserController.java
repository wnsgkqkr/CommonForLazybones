package com.cfl.controller;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.service.CommonService;
import com.cfl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CommonService commonService;

    //add, remove, get Authorities to User
    @PostMapping(value="/authority")
    public ApiResponse createUserAuthority(@RequestBody ApiRequest requestObject){
        try{
            commonService.createUserAuthority(requestObject);
            return commonService.successResult(requestObject, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/authority")
    public ApiResponse removeUserAuthority(@RequestBody ApiRequest requestObject){
        try{
            commonService.removeUserAuthority(requestObject);
            return commonService.successResult(requestObject, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/authority")
    public ApiResponse getUserAuthorities(@RequestBody ApiRequest requestObject){
        try{
            List<Authority> authorityList = userService.getUserAuthorities(requestObject);
            return commonService.successResult(authorityList, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }

    //add, update, remove, get user
    @PostMapping
    public ApiResponse createUser(@RequestBody ApiRequest requestObject){
        try{
            User user = userService.createData(requestObject);
            return commonService.successResult(user, requestObject);
        } catch (Exception e){
            e.printStackTrace();
            return commonService.failResult(e);
        }
    }
    @PutMapping
    public ApiResponse modifyUser(@RequestBody ApiRequest requestObject){
        try{
            User user = userService.modifyData(requestObject);
            return commonService.successResult(user, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping
    public ApiResponse removeUser(@RequestBody ApiRequest requestObject){
        try{
            User user = userService.removeData(requestObject);
            return commonService.successResult(user, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping
    public ApiResponse getUser(@RequestBody ApiRequest requestObject){
        try{
            User user = userService.getData(requestObject);
            return commonService.successResult(user, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
}
