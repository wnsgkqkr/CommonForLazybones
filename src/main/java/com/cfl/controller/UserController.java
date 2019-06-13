package com.cfl.controller;

import com.cfl.domain.ApiResponse;
import com.cfl.domain.User;
import com.cfl.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    private static final String USER_URL_WITH_TENANT = "/{serviceName}/{tenantId}/user/{userId}";
    private static final String USER_URL_WITHOUT_TENANT = "/{serviceName}/user/{userId}";
    private static final String USER_MAPPING_AUTHORITIES_URL_WITH_TENANT = "/{serviceName}/{tenantId}/user/{userId}/authorities";
    private static final String USER_MAPPING_AUTHORITIES_URL_WITHOUT_TENANT = "/{serviceName}/user/{userId}/authorities";

    @GetMapping(value = {USER_MAPPING_AUTHORITIES_URL_WITH_TENANT, USER_MAPPING_AUTHORITIES_URL_WITHOUT_TENANT})
    public ApiResponse getUserAuthoritiesMapping(@PathVariable("serviceName") String serviceName,
                                                @PathVariable("tenantId") String tenantId,
                                                @PathVariable("userId") String userId) {
        return userService.getUserAuthoritiesMapping(serviceName, tenantId, userId);
    }

    @PostMapping(value = {USER_URL_WITH_TENANT, USER_URL_WITHOUT_TENANT})
    public ApiResponse createUser(@PathVariable("serviceName") String serviceName,
                                       @PathVariable("tenantId") String tenantId,
                                       @PathVariable("userId") String userId,
                                       @RequestBody User user) {
        return userService.createUser(serviceName, tenantId, userId, user);
    }

    @PutMapping(value = {USER_URL_WITH_TENANT, USER_URL_WITHOUT_TENANT})
    public ApiResponse modifyUser(@PathVariable("serviceName") String serviceName,
                                       @PathVariable("tenantId") String tenantId,
                                       @PathVariable("userId") String userId,
                                       @RequestBody User user) {
        return userService.modifyUser(serviceName, tenantId, userId, user);
    }

    @DeleteMapping(value = {USER_URL_WITH_TENANT, USER_URL_WITHOUT_TENANT})
    public ApiResponse removeUser(@PathVariable("serviceName") String serviceName,
                                       @PathVariable("tenantId") String tenantId,
                                       @PathVariable("userId") String userId) {
        return userService.removeUser(serviceName, tenantId, userId);
    }

    @GetMapping(value = {USER_URL_WITH_TENANT, USER_URL_WITHOUT_TENANT})
    public ApiResponse getUser(@PathVariable("serviceName") String serviceName,
                                  @PathVariable("tenantId") String tenantId,
                                  @PathVariable("userId") String userId) {
        return userService.getUser(serviceName, tenantId, userId);
    }
}
