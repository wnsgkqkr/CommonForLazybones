package com.cfl.controller;

import com.cfl.domain.AllowedServer;
import com.cfl.service.CommonService;
import com.cfl.service.NetworkAclService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class NetworkAclController {
    @Autowired
    private NetworkAclService networkAclService;
    @Autowired
    private CommonService commonService;

    //network ACL create, modify, remove, get
    @RequestMapping(value="/network-acl")
    public JSONObject createNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.createData(requestObject);
            return commonService.successResult(commonService.toJson(allowedServer));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @PutMapping(value="/network-acl")
    public JSONObject modifyNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(allowedServer));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @DeleteMapping(value="/network-acl")
    public JSONObject removeNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(allowedServer));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @GetMapping(value="/network-acl")
    public JSONObject getNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            AllowedServer allowedServer = networkAclService.getData(requestObject);
            return commonService.successResult(commonService.toJson(allowedServer));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
}
