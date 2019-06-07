package com.cfl.controller;

import com.cfl.domain.Code;
import com.cfl.service.CodeService;
import com.cfl.service.CommonService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class CodeController {
    @Autowired
    private CodeService codeService;
    @Autowired
    private CommonService commonService;

    //Code create, modify, remove, get
    @RequestMapping(value = "/network-acl")
    public JSONObject createNetworkAcl(@RequestBody JSONObject requestObject) {
        try {
            Code code = codeService.createData(requestObject);
            return commonService.successResult(commonService.toJson(code));
        } catch (Exception e) {
            return commonService.failResult(commonService.toJson(e));
        }
    }

    @PutMapping(value = "/network-acl")
    public JSONObject modifyNetworkAcl(@RequestBody JSONObject requestObject) {
        try {
            Code code = codeService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(code));
        } catch (Exception e) {
            return commonService.failResult(commonService.toJson(e));
        }
    }

    @DeleteMapping(value = "/network-acl")
    public JSONObject removeNetworkAcl(@RequestBody JSONObject requestObject) {
        try {
            Code code = codeService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(code));
        } catch (Exception e) {
            return commonService.failResult(commonService.toJson(e));
        }
    }

    @GetMapping(value = "/network-acl")
    public JSONObject getNetworkAcl(@RequestBody JSONObject requestObject) {
        try {
            Code code = codeService.getData(requestObject);
            return commonService.successResult(commonService.toJson(code));
        } catch (Exception e) {
            return commonService.failResult(commonService.toJson(e));
        }
    }
}
