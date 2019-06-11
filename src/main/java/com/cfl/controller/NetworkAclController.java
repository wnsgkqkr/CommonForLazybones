package com.cfl.controller;

import com.cfl.domain.AllowedServer;
import com.cfl.domain.ApiRequest;
import com.cfl.service.CommonService;
import com.cfl.service.NetworkAclService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class NetworkAclController {
    @Autowired
    private NetworkAclService networkAclService;
    @Autowired
    private CommonService commonService;

    //network ACL create, modify, remove, get
    @PostMapping(value="/network-acl")
    public Map<String, Object> createNetworkAcl(@RequestBody ApiRequest requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.createData(requestObject);
            return commonService.successResult(commonService.toJson(allowedServer));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @PutMapping(value="/network-acl")
    public Map<String, Object> modifyNetworkAcl(@RequestBody ApiRequest requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(allowedServer));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/network-acl")
    public Map<String, Object> removeNetworkAcl(@RequestBody ApiRequest requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(allowedServer));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/network-acl")
    public Map<String, Object> getNetworkAcl(@RequestBody ApiRequest requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.getData(requestObject);
            return commonService.successResult(commonService.toJson(allowedServer));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
}
