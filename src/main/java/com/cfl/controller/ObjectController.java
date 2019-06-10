package com.cfl.controller;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.CflObject;
import com.cfl.service.CommonService;
import com.cfl.service.ObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ObjectController {
    @Autowired
    private ObjectService objectService;
    @Autowired
    private CommonService commonService;

    //add, remove, get Authorities to Object
    @PostMapping(value="/object/authority")
    public Map<String, Object> insertObjectAuthority(@RequestBody ApiRequest requestObject){
        try {
            objectService.createObjectAuthority(requestObject);
            return commonService.successResult(commonService.toJson(requestObject));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/object/authority")
    public Map<String, Object> deleteObjectAuthority(@RequestBody ApiRequest requestObject){
        try {
            objectService.removeObjectAuthority(requestObject);
            return commonService.successResult(commonService.toJson(requestObject));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/object/authority")
    public Map<String, Object> getObjectAuthorityIds(@RequestBody ApiRequest requestObject){
        try {
            List<String> authorityIdList = objectService.getObjectAuthorityIds(requestObject);
            return commonService.successResult(commonService.toJson(authorityIdList));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }

    //add, remove, get Object
    @PostMapping(value="/object")
    public Map<String, Object> createObject(@RequestBody ApiRequest requestObject){
        try {
            CflObject object = objectService.createData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @PutMapping(value="/object")
    public Map<String, Object> modifyObject(@RequestBody ApiRequest requestObject){
        try {
            CflObject object = objectService.modifyData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/object")
    public Map<String, Object> removeObject(@RequestBody ApiRequest requestObject){
        try {
            CflObject object = objectService.removeData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/object")
    public Map<String, Object> getObject(@RequestBody ApiRequest requestObject){
        try {
            CflObject object = objectService.getData(requestObject);
            return commonService.successResult(commonService.toJson(object));
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
}
