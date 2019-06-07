package com.cfl.controller;

import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.service.CommonService;
import com.cfl.service.ObjectService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ObjectController {
    @Autowired
    private ObjectService objectService;
    @Autowired
    private CommonService commonService;

    //add, remove, get Authorities to Object
    @PostMapping(value="/object/authority")
    public JSONObject insertObjectAuthority(@RequestBody JSONObject requestObject){
        try {
            return commonService.successResult(objectService.createObjectAuthority(requestObject));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @DeleteMapping(value="/object/authority")
    public JSONObject deleteObjectAuthority(@RequestBody JSONObject requestObject){
        try {
            return commonService.successResult(objectService.removeObjectAuthority(requestObject));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @GetMapping(value="/object/authority")
    public JSONObject getObjectAuthorityIds(@RequestBody JSONObject requestObject){
        try {
            List<String> authorityIdList = objectService.getObjectAuthorityIds(requestObject);
            return commonService.successResult(commonService.toJson(authorityIdList));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }

    //add, remove, get Object
    @PostMapping(value="/object")
    public JSONObject createObject(@RequestBody JSONObject requestObject){
        try {
            CflObject object = objectService.createData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @PutMapping(value="/object")
    public JSONObject modifyObject(@RequestBody JSONObject requestObject){
        try {
            CflObject object = objectService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @DeleteMapping(value="/object")
    public JSONObject removeObject(@RequestBody JSONObject requestObject){
        try {
            CflObject object = objectService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @GetMapping(value="/object")
    public JSONObject getObject(@RequestBody JSONObject requestObject){
        try {
            CflObject object = objectService.getData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
}
