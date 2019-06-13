package com.cfl.controller;

import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.User;
import com.cfl.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuthorityController {
    @Autowired
    private AuthorityService authorityService;

    private static final String AUTHORITY_URL_WITH_TENANT = "/{serviceName}/{tenantId}/authority/{authorityId}";
    private static final String AUTHORITY_URL_WITHOUT_TENANT = "/{serviceName}/authority/{authorityId}";
    private static final String AUTHORITY_MAPPING_USERS_URL_WITH_TENANT = "/{serviceName}/{tenantId}/authority/{authorityId}/users";
    private static final String AUTHORITY_MAPPING_USERS_URL_WITHOUT_TENANT = "/{serviceName}/authority/{authorityId}/users";

    @PostMapping(value = {AUTHORITY_MAPPING_USERS_URL_WITH_TENANT, AUTHORITY_MAPPING_USERS_URL_WITHOUT_TENANT})
    public ApiResponse createAuthorityUsersMapping(@PathVariable("serviceName") String serviceName,
                                           @PathVariable("tenantId") String tenantId,
                                           @PathVariable("authorityId") String authorityId,
                                           @RequestBody List<User> requestUsers) { //TODO: add requester in Http header
            return authorityService.createAuthorityUsersMapping(serviceName, tenantId, authorityId, requestUsers);
    }

    @DeleteMapping(value = {AUTHORITY_MAPPING_USERS_URL_WITH_TENANT, AUTHORITY_MAPPING_USERS_URL_WITHOUT_TENANT})
    public ApiResponse removeAuthorityUsersMapping(@PathVariable("serviceName") String serviceName,
                                           @PathVariable("tenantId") String tenantId,
                                           @PathVariable("authorityId") String authorityId,
                                           @RequestBody List<User> requestUsers) {
            return authorityService.removeAuthorityUsersMapping(serviceName, tenantId, authorityId, requestUsers);
    }
    @GetMapping(value = {AUTHORITY_MAPPING_USERS_URL_WITH_TENANT, AUTHORITY_MAPPING_USERS_URL_WITHOUT_TENANT})
    public ApiResponse getAuthorityUsersMapping(@PathVariable("serviceName") String serviceName,
                                         @PathVariable("tenantId") String tenantId,
                                         @PathVariable("authorityId") String authorityId) {
            return authorityService.getAuthorityUsersMapping(serviceName, tenantId, authorityId);
    }

    @PostMapping(value = {AUTHORITY_URL_WITH_TENANT, AUTHORITY_URL_WITHOUT_TENANT})
    public ApiResponse createAuthority(@PathVariable("serviceName") String serviceName,
                                       @PathVariable("tenantId") String tenantId,
                                       @PathVariable("authorityId") String authorityId,
                                       @RequestBody Authority authority) {
            return authorityService.createAuthority(serviceName, tenantId, authorityId, authority);
    }
    @PutMapping(value = {AUTHORITY_URL_WITH_TENANT, AUTHORITY_URL_WITHOUT_TENANT})
    public ApiResponse modifyAuthority(@PathVariable("serviceName") String serviceName,
                                       @PathVariable("tenantId") String tenantId,
                                       @PathVariable("authorityId") String authorityId,
                                       @RequestBody Authority authority) {
            return authorityService.modifyAuthority(serviceName, tenantId, authorityId, authority);
    }
    @DeleteMapping(value = {AUTHORITY_URL_WITH_TENANT, AUTHORITY_URL_WITHOUT_TENANT})
    public ApiResponse removeAuthority(@PathVariable("serviceName") String serviceName,
                                       @PathVariable("tenantId") String tenantId,
                                       @PathVariable("authorityId") String authorityId) {
            return authorityService.removeAuthority(serviceName, tenantId, authorityId);
    }
    @GetMapping(value = {AUTHORITY_URL_WITH_TENANT, AUTHORITY_URL_WITHOUT_TENANT})
    public ApiResponse getAuthority(@PathVariable("serviceName") String serviceName,
                                    @PathVariable("tenantId") String tenantId,
                                    @PathVariable("authorityId") String authorityId) {
            return authorityService.getAuthority(serviceName, tenantId, authorityId);
    }
    @GetMapping(value = {"/{serviceName}/{tenantId}/authority", "/{serviceName}/authority"})
    public ApiResponse getTenantAuthorities(@PathVariable("serviceName") String serviceName,
                                            @PathVariable("tenantId") String tenantId) {
            return authorityService.getTenantAuthorities(serviceName, tenantId);
    }
}