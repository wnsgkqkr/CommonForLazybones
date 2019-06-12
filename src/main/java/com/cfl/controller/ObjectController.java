package com.cfl.controller;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.CflObject;
import com.cfl.service.CommonService;
import com.cfl.service.ObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/object")
public class ObjectController {
    @Autowired
    private ObjectService objectService;
    @Autowired
    private CommonService commonService;

    //add, remove, get Authorities to Object
    @PostMapping(value="/authority")
    public ApiResponse createObjectAuthority(@RequestBody ApiRequest requestObject){
        try {
            objectService.createObjectAuthority(requestObject);
            return commonService.successResult(requestObject, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping(value="/authority")
    public ApiResponse removeObjectAuthority(@RequestBody ApiRequest requestObject){
        try {
            objectService.removeObjectAuthority(requestObject);
            return commonService.successResult(requestObject,requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping(value="/authority")
    public ApiResponse getObjectAuthorityIds(@RequestBody ApiRequest requestObject){
        try {
            List<String> authorityIdList = objectService.getObjectAuthorityIds(requestObject);
            return commonService.successResult(authorityIdList, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }

    //add, remove, get Object
    @PostMapping
    public ApiResponse createObject(@RequestBody ApiRequest requestObject){
        try {
            CflObject object = objectService.createData(requestObject);
            return commonService.successResult(object, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @PutMapping
    public ApiResponse modifyObject(@RequestBody ApiRequest requestObject){
        try {
            CflObject object = objectService.modifyData(requestObject);
            return commonService.successResult(object, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @DeleteMapping
    public ApiResponse removeObject(@RequestBody ApiRequest requestObject){
        try {
            CflObject object = objectService.removeData(requestObject);
            return commonService.successResult(object, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
    @GetMapping
    public ApiResponse getObject(@RequestBody ApiRequest requestObject){
        try {
            CflObject object = objectService.getData(requestObject);
            return commonService.successResult(object, requestObject);
        } catch (Exception e){
            return commonService.failResult(e);
        }
    }
}
