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

    @RequestMapping(value = "{serviceName}/{tenantId}/user/cache")
    public ApiResponse clearTenantUserAuthorityCache(@PathVariable("serviceName") String serviceName,
                                                     @PathVariable(name = "tenantId", required = false) String tenantId) {
        return cacheService.clearUserAuthorityTenantCache(serviceName, tenantId);
    }
    @RequestMapping(value = "{serviceName}/user/cache")
    public ApiResponse clearServiceUserAuthorityCache(@PathVariable("serviceName") String serviceName) {
        return cacheService.clearUserAuthorityServiceCache(serviceName);
    }
    @RequestMapping(value = "/user/cache")
    public ApiResponse clearUserAuthorityCache() {
        return cacheService.clearUserAuthorityCache();
    }

    @RequestMapping(value = "{serviceName}/{tenantId}/authority/cache")
    public ApiResponse clearTenantAuthorityUserCache(@PathVariable("serviceName") String serviceName,
                                                     @PathVariable(name = "tenantId", required = false) String tenantId) {
        return cacheService.clearUserAuthorityTenantCache(serviceName, tenantId);
    }
    @RequestMapping(value = "{serviceName}/authority/cache")
    public ApiResponse clearServiceAuthorityUserCache(@PathVariable("serviceName") String serviceName) {
        return cacheService.clearUserAuthorityServiceCache(serviceName);
    }
    @RequestMapping(value = "/authority/cache")
    public ApiResponse clearAuthorityUserCache() {
        return cacheService.clearUserAuthorityCache();
    }

    @RequestMapping(value = "{serviceName}/{tenantId}/object/cache")
    public ApiResponse refreshTenantObjectCache(@PathVariable("serviceName") String serviceName,
                                                @PathVariable(name = "tenantId", required = false) String tenantId) {
        return cacheService.refreshTenantObjectCache(serviceName, tenantId);
    }
    @RequestMapping(value = "{serviceName}/object/cache")
    public ApiResponse refreshServiceObjectCache(@PathVariable("serviceName") String serviceName) {
        return cacheService.refreshServiceObjectCache(serviceName);
    }
    @RequestMapping(value = "/object/cache")
    public ApiResponse createObjectCache(){
        return cacheService.createObjectCache();
    }

    @RequestMapping(value = "{serviceName}/{tenantId}/code/cache")
    public ApiResponse refreshTenantCodeCache(@PathVariable("serviceName") String serviceName,
                                                @PathVariable(name = "tenantId", required = false) String tenantId) {
        return cacheService.refreshTenantCodeCache(serviceName, tenantId);
    }
    @RequestMapping(value = "{serviceName}/code/cache")
    public ApiResponse refreshServiceCodeCache(@PathVariable("serviceName") String serviceName) {
        return cacheService.refreshServiceCodeCache(serviceName);
    }
    @RequestMapping(value = "/code/cache")
    public ApiResponse refreshCodeCache(){
        return cacheService.createCodeCache();
    }

    @RequestMapping(value = "/init")
    public ApiResponse cacheInit(@RequestBody String serverIp){
        return cacheService.allCacheInit(serverIp);
    }

}
