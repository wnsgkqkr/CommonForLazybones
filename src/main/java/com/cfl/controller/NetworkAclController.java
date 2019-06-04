package com.cfl.controller;

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

    //network ACL insert, update, delete
    @RequestMapping(value="/network-acl")
    public JSONObject insertNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            networkAclService.insertNetworkAcl(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
    @PutMapping(value="/network-acl")
    public JSONObject updateNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            networkAclService.updateNetworkAcl(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
    @DeleteMapping(value="/network-acl")
    public JSONObject deleteNetworkAcl(@RequestBody JSONObject requestObject) {
        try{
            networkAclService.deleteNetworkAcl(requestObject);
            return commonService.successResult(new JSONObject());
        } catch (Exception e){
            return commonService.failResult(new JSONObject());
        }
    }
}
