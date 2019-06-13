package com.cfl.controller;

import com.cfl.domain.ApiResponse;
import com.cfl.domain.Code;
import com.cfl.service.CodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class CodeController {
    @Autowired
    private CodeService codeService;

    private static final String CODE_URL_WITH_TENANT = "/{serviceName}/{tenantId}/code/{codeId}";
    private static final String CODE_URL_WITHOUT_TENANT = "/{serviceName}/code/{codeId}";

    @PostMapping(value = {CODE_URL_WITH_TENANT, CODE_URL_WITHOUT_TENANT})
    public ApiResponse createCode(@PathVariable("serviceName") String serviceName,
                                  @PathVariable("tenantId") String tenantId,
                                  @PathVariable("codeId") String codeId,
                                  @RequestBody Code code) {
        return codeService.createCode(serviceName, tenantId, codeId, code);
    }

    @PutMapping(value = {CODE_URL_WITH_TENANT, CODE_URL_WITHOUT_TENANT})
    public ApiResponse modifyCode(@PathVariable("serviceName") String serviceName,
                                  @PathVariable("tenantId") String tenantId,
                                  @PathVariable("codeId") String codeId,
                                  @RequestBody Code code) {
        return codeService.modifyCode(serviceName, tenantId, codeId, code);
    }

    @DeleteMapping(value = {CODE_URL_WITH_TENANT, CODE_URL_WITHOUT_TENANT})
    public ApiResponse removeCode(@PathVariable("serviceName") String serviceName,
                                  @PathVariable("tenantId") String tenantId,
                                  @PathVariable("codeId") String codeId) {
        return codeService.removeCode(serviceName, tenantId, codeId);
    }

    @GetMapping(value = {CODE_URL_WITH_TENANT, CODE_URL_WITHOUT_TENANT})
    public ApiResponse getCode(@PathVariable("serviceName") String serviceName,
                               @PathVariable("tenantId") String tenantId,
                               @PathVariable("codeId") String codeId) {
        return codeService.getCode(serviceName, tenantId, codeId);
    }
}
