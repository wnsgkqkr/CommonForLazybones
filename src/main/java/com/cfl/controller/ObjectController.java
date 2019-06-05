package com.cfl.controller;

import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.service.CommonService;
import com.cfl.service.ObjectService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class ObjectController {
    @Autowired
    private ObjectService objectService;
    @Autowired
    private CommonService commonService;

    @PostMapping(value="/object/authority")
    public JSONObject insertObjectAuthority(@RequestBody JSONObject requestObject){
        try {
            List<Authority> authorityList = objectService.createObjectAuthority(requestObject);
            return commonService.successResult(commonService.toJson(authorityList));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @DeleteMapping(value="/object/authority")
    public JSONObject deleteObjectAuthority(@RequestBody JSONObject requestObject){
        try {
            List<Authority> authorityList = objectService.removeObjectAuthority(requestObject);
            return commonService.successResult(commonService.toJson(authorityList));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @GetMapping(value="/object/authority")
    public JSONObject getObjectAuthority(@RequestBody JSONObject requestObject){
        try {
            List<String> authorityList = objectService.getObjectAuthorityIds(requestObject);
            return commonService.successResult(commonService.toJson(authorityList));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }

    @PostMapping(value="/object")
    public JSONObject insertObject(@RequestBody JSONObject requestObject){
        try {
            CflObject object = objectService.createData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @PutMapping(value="/object")
    public JSONObject updateObject(@RequestBody JSONObject requestObject){
        try {
            CflObject object = objectService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(commonService.toJson(e));
        }
    }
    @DeleteMapping(value="/object")
    public JSONObject deleteObject(@RequestBody JSONObject requestObject){
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
