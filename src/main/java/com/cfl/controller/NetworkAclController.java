package com.cfl.controller;

import com.cfl.domain.CacheUpdateRequest;
import com.cfl.domain.Server;
import com.cfl.domain.ApiResponse;
import com.cfl.service.NetworkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class NetworkAclController {
    @Autowired
    private NetworkService networkService;

    private static final String NETWORK_ACL_URL_WITH_TENANT = "/{serviceName}/{tenantId}/network-acl/{ip}";
    private static final String NETWORK_ACL_URL_WITHOUT_TENANT = "/{serviceName}/network-acl/{ip}";
    
    @PostMapping(value = {NETWORK_ACL_URL_WITH_TENANT, NETWORK_ACL_URL_WITHOUT_TENANT})
    public ApiResponse createNetworkAcl(@PathVariable("serviceName") String serviceName,
                                        @PathVariable(name = "tenantId", required = false) String tenantId,
                                        @PathVariable("ip") String serverIp,
                                        @RequestBody Server server) {
        return networkService.createNetworkAcl(serviceName, tenantId, serverIp, server);
    }

    @PostMapping("/{serviceName}/{tenantId}/network-acl")
    public ApiResponse createNetworkAcl(@PathVariable("serviceName") String serviceName,
                                        @PathVariable(name = "tenantId", required = false) String tenantId,
                                        @RequestBody Server server) {
        return networkService.createNetworkAcl(server.getServiceName(), tenantId, server.getServerIp(), server);
    }

    @PostMapping("/{serviceName}/{tenantId}/provide")
    public ApiResponse createProvideServer(@PathVariable("serviceName") String serviceName,
                                           @PathVariable(name = "tenantId", required = false) String tenantId,
                                           @RequestBody Server server) {
        return networkService.createProvideServer(server.getServiceName(), server.getServerName(), server.getServerIp(), server.getPortNumber());
    }

    @PutMapping(value = {NETWORK_ACL_URL_WITH_TENANT, NETWORK_ACL_URL_WITHOUT_TENANT})
    public ApiResponse modifyNetworkAcl(@PathVariable("serviceName") String serviceName,
                                        @PathVariable(name = "tenantId", required = false) String tenantId,
                                        @PathVariable("ip") String serverIp,
                                        @RequestBody Server server) {
        return networkService.modifyNetworkAcl(serviceName, tenantId, serverIp, server);
    }

    @DeleteMapping(value = {NETWORK_ACL_URL_WITH_TENANT, NETWORK_ACL_URL_WITHOUT_TENANT})
    public ApiResponse deleteNetworkAcl(@PathVariable("serviceName") String serviceName,
                                        @PathVariable(name = "tenantId", required = false) String tenantId,
                                        @PathVariable("ip") String serverIp) {
        return networkService.deleteNetworkAcl(serviceName, tenantId, serverIp);
    }

    @GetMapping(value = {NETWORK_ACL_URL_WITH_TENANT, NETWORK_ACL_URL_WITHOUT_TENANT})
    public ApiResponse getNetworkAcl(@PathVariable("serviceName") String serviceName,
                                        @PathVariable(name = "tenantId", required = false) String tenantId,
                                        @PathVariable("ip") String serverIp) {
        return networkService.getNetworkAcl(serviceName, tenantId, serverIp);
    }

    @RequestMapping(value = "/{serviceName}/server/cache/init")
    public ApiResponse sendCacheInit(@PathVariable("serviceName")String serviceName,
                              @RequestBody CacheUpdateRequest cacheUpdateRequest) {
        return networkService.sendProvideServersToInit(serviceName, cacheUpdateRequest);
    }
}