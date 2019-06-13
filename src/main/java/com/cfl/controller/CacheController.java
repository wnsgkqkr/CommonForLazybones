package com.cfl.controller;

import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.service.CacheService;
import com.cfl.service.CommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/cache")
public class CacheController {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private CommonService commonService;

    @RequestMapping(value = "/user")
    public ApiResponse clearUserCache(ApiRequest request){
        try{
            cacheService.clearUserAuthorityCache();
            return commonService.successResult(null, null);
        } catch(Exception e){
            return commonService.failResult(e);
        }
    }
}
