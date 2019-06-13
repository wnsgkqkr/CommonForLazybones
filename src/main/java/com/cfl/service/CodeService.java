package com.cfl.service;

import com.cfl.cache.Cache;
import com.cfl.domain.ApiRequest;
import com.cfl.domain.ApiResponse;
import com.cfl.domain.Code;
import com.cfl.domain.History;
import com.cfl.mapper.CodeMapper;
import com.cfl.util.ApiResponseUtil;
import com.cfl.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService{
    @Autowired
    private CodeMapper codeMapper;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private HistoryService historyService;

      public ApiResponse createCode(String serviceName, String tenantId, String codeId, Code code) {
        try {
            code = setCode(serviceName, tenantId, codeId, code);
            codeMapper.insertCode(code);
            cacheService.refreshTenantCodeCache(serviceName, code.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(code);
            historyService.createHistory(serviceName, code.getTenantId(), code, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch(Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse modifyCode(String serviceName, String tenantId, String codeId, Code code) {
        try {
            code = setCode(serviceName, tenantId, codeId, code);
            codeMapper.updateCode(serviceName, code.getTenantId(), codeId, code);
            cacheService.refreshTenantCodeCache(serviceName, code.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(code);
            historyService.createHistory(serviceName, code.getTenantId(), code, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch(Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse removeCode(String serviceName, String tenantId, String codeId) {
        try {
            Code code = setCode(serviceName, tenantId, codeId, new Code());
            codeMapper.deleteCode(code);
            cacheService.refreshTenantCodeCache(serviceName, code.getTenantId());
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(code);
            historyService.createHistory(serviceName, code.getTenantId(), code, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch(Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
    }
    public ApiResponse getCode(String serviceName, String tenantId, String codeId) {
        try {
            Code code = setCode(serviceName, tenantId, codeId, new Code());
            Code mapCode = Cache.codeCache.get(serviceName).get(code.getTenantId()).get(codeId);
            if(mapCode == null){
                code = codeMapper.selectCode(code);
                Cache.codeCache.get(serviceName).get(code.getTenantId()).put(codeId, code);
                mapCode = code;
            }
            ApiResponse successApiResponse = ApiResponseUtil.getSuccessApiResponse(mapCode);
            historyService.createHistory(serviceName, mapCode.getTenantId(), mapCode, successApiResponse.getHeader().getResultMessage());
            return successApiResponse;
        } catch(Exception e) {
            return ApiResponseUtil.getFailureApiResponse();
        }
      }

    private Code setCode(String serviceName, String tenantId, String codeId, Code code) {
        code.setServiceName(serviceName);
        if(tenantId == null) {
            code.setTenantId(Constant.DEFAULT_TENANT_ID);
        } else {
            code.setTenantId(tenantId);
        }
        code.setCodeId(codeId);
        return code;
    }
}
