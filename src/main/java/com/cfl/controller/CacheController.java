package com.cfl.controller;

import com.cfl.domain.ApiResponse;
import com.cfl.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CacheController {
    @Autowired
    private CacheService cacheService;

    @RequestMapping(value = "{serviceName}/{tenantId}/user")
    public ApiResponse clearTenantUserAuthorityCache(@PathVariable("serviceName") String serviceName,
                                                     @PathVariable("tenantId") String tenantId) {
        return cacheService.clearUserAuthorityTenantCache(serviceName, tenantId);
    }
    @RequestMapping(value = "{serviceName}/user")
    public ApiResponse clearServiceUserAuthorityCache(@PathVariable("serviceName") String serviceName) {
        return cacheService.clearUserAuthorityServiceCache(serviceName);
    }
    @RequestMapping(value = "/user")
    public ApiResponse clearUserAuthorityCache() {
        return cacheService.clearUserAuthorityCache();
    }

    @RequestMapping(value = "{serviceName}/{tenantId}/authority")
    public ApiResponse clearTenantAuthorityUserCache(@PathVariable("serviceName") String serviceName,
                                                     @PathVariable("tenantId") String tenantId) {
        return cacheService.clearUserAuthorityTenantCache(serviceName, tenantId);
    }
    @RequestMapping(value = "{serviceName}/authority")
    public ApiResponse clearServiceAuthorityUserCache(@PathVariable("serviceName") String serviceName) {
        return cacheService.clearUserAuthorityServiceCache(serviceName);
    }
    @RequestMapping(value = "/authority")
    public ApiResponse clearAuthorityUserCache() {
        return cacheService.clearUserAuthorityCache();
    }

    @RequestMapping(value = "{serviceName}/{tenantId}/object")
    public ApiResponse refreshTenantObjectCache(@PathVariable("serviceName") String serviceName,
                                                @PathVariable("tenantId") String tenantId) {
        return cacheService.refreshTenantObjectCache(serviceName, tenantId);
    }
    @RequestMapping(value = "{serviceName}/object")
    public ApiResponse refreshServiceObjectCache(@PathVariable("serviceName") String serviceName) {
        return cacheService.refreshServiceObjectCache(serviceName);
    }
    @RequestMapping(value = "/object")
    public ApiResponse createObjectCache(){
        return cacheService.createObjectCache();
    }

    @RequestMapping(value = "{serviceName}/{tenantId}/code")
    public ApiResponse refreshTenantCodeCache(@PathVariable("serviceName") String serviceName,
                                                @PathVariable("tenantId") String tenantId) {
        return cacheService.refreshTenantCodeCache(serviceName, tenantId);
    }
    @RequestMapping(value = "{serviceName}/code")
    public ApiResponse refreshServiceCodeCache(@PathVariable("serviceName") String serviceName) {
        return cacheService.refreshServiceCodeCache(serviceName);
    }
    @RequestMapping(value = "/code")
    public ApiResponse refreshCodeCache(){
        return cacheService.createCodeCache();
    }

    @RequestMapping(value = "/init")
    public ApiResponse cacheInit(@RequestBody String serverIp){
        return cacheService.allCacheInit(serverIp);
    }

}
