package com.cfl.controller;

import com.cfl.domain.ApiResponse;
import com.cfl.domain.Authority;
import com.cfl.domain.CflObject;
import com.cfl.service.ObjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ObjectController {
    @Autowired
    private ObjectService objectService;
    
    private static final String OBJECT_URL_WITH_TENANT = "/{serviceName}/{tenantId}/object/{objectId}";
    private static final String OBJECT_URL_WITHOUT_TENANT = "/{serviceName}/object/{objectId}";
    private static final String OBJECT_MAPPING_AUTHORITIES_URL_WITH_TENANT = "/{serviceName}/{tenantId}/object/{objectId}/authorities";
    private static final String OBJECT_MAPPING_AUTHORITIES_URL_WITHOUT_TENANT = "/{serviceName}/object/{objectId}/authorities";
    private static final String OBJECT_MAPPING_SUB_OBJECTS_URL_WITH_TENANT = "/{serviceName}/{tenantId}/object/{objectId}/sub-objects";
    private static final String OBJECT_MAPPING_SUB_OBJECTS_URL_WITHOUT_TENANT = "/{serviceName}/object/{objectId}/sub-objects";

    @PostMapping(value = {OBJECT_MAPPING_AUTHORITIES_URL_WITH_TENANT, OBJECT_MAPPING_AUTHORITIES_URL_WITHOUT_TENANT})
    public ApiResponse createObjectAuthoritiesMapping(@PathVariable("serviceName") String serviceName,
                                             @PathVariable(name = "tenantId", required = false) String tenantId,
                                             @PathVariable("objectId") String objectId,
                                             @RequestBody List<Authority> requestAuthorities) {
            return objectService.createObjectAuthoritiesMapping(serviceName, tenantId, objectId, requestAuthorities);
    }

    @DeleteMapping(value = {OBJECT_MAPPING_AUTHORITIES_URL_WITH_TENANT, OBJECT_MAPPING_AUTHORITIES_URL_WITHOUT_TENANT})
    public ApiResponse deleteObjectAuthoritiesMapping(@PathVariable("serviceName") String serviceName,
                                                      @PathVariable(name = "tenantId", required = false) String tenantId,
                                                      @PathVariable("objectId") String objectId,
                                                      @RequestBody List<Authority> requestAuthorities) {
        return objectService.removeObjectAuthoritiesMapping(serviceName, tenantId, objectId, requestAuthorities);
    }

    @GetMapping(value = {OBJECT_MAPPING_AUTHORITIES_URL_WITH_TENANT, OBJECT_MAPPING_AUTHORITIES_URL_WITHOUT_TENANT})
    public ApiResponse getObjectAuthoritiesMapping(@PathVariable("serviceName") String serviceName,
                                                      @PathVariable(name = "tenantId", required = false) String tenantId,
                                                      @PathVariable("objectId") String objectId) {
        return objectService.getObjectAuthoritiesMapping(serviceName, tenantId, objectId);
    }

    @PostMapping(value = {OBJECT_URL_WITH_TENANT, OBJECT_URL_WITHOUT_TENANT})
    public ApiResponse createObject(@PathVariable("serviceName") String serviceName,
                                    @PathVariable(name = "tenantId", required = false) String tenantId,
                                    @PathVariable("objectId") String objectId,
                                    @RequestBody CflObject object) {
            return objectService.createObject(serviceName, tenantId, objectId, object);
    }

    @PutMapping(value = {OBJECT_URL_WITH_TENANT, OBJECT_URL_WITHOUT_TENANT})
    public ApiResponse modifyObject(@PathVariable("serviceName") String serviceName,
                                    @PathVariable(name = "tenantId", required = false) String tenantId,
                                    @PathVariable("objectId") String objectId,
                                    @RequestBody CflObject object) {
        return objectService.modifyObject(serviceName, tenantId, objectId, object);
    }

    @DeleteMapping(value = {OBJECT_URL_WITH_TENANT, OBJECT_URL_WITHOUT_TENANT})
    public ApiResponse removeObject(@PathVariable("serviceName") String serviceName,
                                    @PathVariable(name = "tenantId", required = false) String tenantId,
                                    @PathVariable("objectId") String objectId) {
        return objectService.removeObject(serviceName, tenantId, objectId);
    }

    @GetMapping(value = {OBJECT_URL_WITH_TENANT, OBJECT_URL_WITHOUT_TENANT})
    public ApiResponse getObject(@PathVariable("serviceName") String serviceName,
                                    @PathVariable(name = "tenantId", required = false) String tenantId,
                                    @PathVariable("objectId") String objectId) {
        return objectService.getObject(serviceName, tenantId, objectId);
    }

    @GetMapping(value = {"/{serviceName}/{tenantId}/object", "/{serviceName}/object"})
    public ApiResponse getTenantObjects(@PathVariable("serviceName") String serviceName,
                                        @PathVariable(name = "tenantId", required = false) String tenantId) {
        return objectService.getTenantObjectMap(serviceName, tenantId);
    }

    @PostMapping(value = {OBJECT_MAPPING_SUB_OBJECTS_URL_WITH_TENANT, OBJECT_MAPPING_SUB_OBJECTS_URL_WITHOUT_TENANT})
    public ApiResponse createObjectSubObjectMapping(@PathVariable("serviceName") String serviceName,
                                                    @PathVariable(name = "tenantId", required = false) String tenantId,
                                                    @PathVariable("objectId") String objectId,
                                                    @RequestBody List<CflObject> requestSubObjects) {
        return objectService.createObjectSubObjectsMapping(serviceName, tenantId, objectId, requestSubObjects);
    }

    @DeleteMapping(value = {OBJECT_MAPPING_SUB_OBJECTS_URL_WITH_TENANT, OBJECT_MAPPING_SUB_OBJECTS_URL_WITHOUT_TENANT})
    public ApiResponse deleteObjectSubObjectMapping(@PathVariable("serviceName") String serviceName,
                                                    @PathVariable(name = "tenantId", required = false) String tenantId,
                                                    @PathVariable("objectId") String objectId,
                                                    @RequestBody List<CflObject> requestSubObjects) {
        return objectService.removeObjectSubObjectsMapping(serviceName, tenantId, objectId, requestSubObjects);
    }
}
