package com.cfl.controller;

import com.cfl.domain.AllowedServer;
import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.service.CommonService;
import com.cfl.service.NetworkAclService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value="/network-acl")
public class NetworkAclController {
    @Autowired
    private NetworkAclService networkAclService;
    @Autowired
    private CommonService commonService;

    //network ACL create, modify, remove, get
    @PostMapping
    public ApiResponse createNetworkAcl(@RequestBody ApiRequest requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.createData(requestObject);
            return commonService.successResult(allowedServer, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @PutMapping
    public ApiResponse modifyNetworkAcl(@RequestBody ApiRequest requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.modifyData(requestObject);
            return commonService.successResult(allowedServer, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping
    public ApiResponse removeNetworkAcl(@RequestBody ApiRequest requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.removeData(requestObject);
            return commonService.successResult(allowedServer, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping
    public ApiResponse getNetworkAcl(@RequestBody ApiRequest requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.getData(requestObject);
            return commonService.successResult(allowedServer, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
}
